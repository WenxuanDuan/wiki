package com.wenxuanduan.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wenxuanduan.wiki.domain.Content;
import com.wenxuanduan.wiki.domain.Doc;
import com.wenxuanduan.wiki.domain.DocExample;
import com.wenxuanduan.wiki.exception.BusinessException;
import com.wenxuanduan.wiki.exception.BusinessExceptionCode;
import com.wenxuanduan.wiki.mapper.ContentMapper;
import com.wenxuanduan.wiki.mapper.DocMapper;
import com.wenxuanduan.wiki.mapper.DocMapperCust;
import com.wenxuanduan.wiki.req.DocQueryReq;
import com.wenxuanduan.wiki.req.DocSaveReq;
import com.wenxuanduan.wiki.resp.DocQueryResp;
import com.wenxuanduan.wiki.resp.PageResp;
import com.wenxuanduan.wiki.util.CopyUtil;
import com.wenxuanduan.wiki.util.RedisUtil;
import com.wenxuanduan.wiki.util.RequestContext;
import com.wenxuanduan.wiki.util.SnowFlake;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class DocService {
    
    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);
    @Resource
    private DocMapper docMapper;

    @Resource
    private DocMapperCust docMapperCust;

    @Resource
    private ContentMapper contentMapper;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    private RedisUtil redisUtil;

    public List<DocQueryResp> all(Long ebookId) {
        DocExample docExample = new DocExample();
        docExample.createCriteria().andEbookIdEqualTo(ebookId);
        docExample.setOrderByClause("sort asc");
        List<Doc> docList = docMapper.selectByExample(docExample);

        // copy list
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        return list;
    }

    /**
     * query
     */
    public PageResp<DocQueryResp> list(DocQueryReq req) {
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        DocExample.Criteria criteria = docExample.createCriteria();
        if(!ObjectUtils.isEmpty(req.getName())){
            criteria.andNameLike("%" + req.getName() + "%");
        }
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Doc> docList = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        LOG.info("Total columns: {}", pageInfo.getTotal());
        LOG.info("Total pages: {}", pageInfo.getPages());

        // copy list
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        PageResp<DocQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    /**
     * Save
     */
    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        Content content = CopyUtil.copy(req, Content.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            // add a new book
            doc.setId(snowFlake.nextId());
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);

            // add content
            content.setId(doc.getId());
            contentMapper.insert(content);
        }
        else {
            // update previous book
            docMapper.updateByPrimaryKey(doc);

            // update content
            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);
            if (count == 0) {
                contentMapper.insert(content);
            }
        }
    }


    /**
     * Delete
     * @param id
     */
    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }

    public void delete(List<String> ids) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }

    public String findContent(Long id) {
        Content content = contentMapper.selectByPrimaryKey(id);

        // doc view count + 1
        docMapperCust.increaseViewCount(id);
        if (ObjectUtils.isEmpty(content)) {
            return "";
        }
        else {
            return content.getContent();
        }
    }

    /**
     * Vote
     */
    public void vote(Long id) {
        // use remote IP + doc.id as key，could not repeat within 24 hours
        String ip = RequestContext.getRemoteAddr();
        if (redisUtil.validateRepeat("DOC_VOTE_" + id + "_" + ip, 3600 * 24)) {
            docMapperCust.increaseVoteCount(id);
        } else {
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }
    }
}
