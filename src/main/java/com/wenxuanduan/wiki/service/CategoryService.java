package com.wenxuanduan.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wenxuanduan.wiki.domain.Category;
import com.wenxuanduan.wiki.domain.CategoryExample;
import com.wenxuanduan.wiki.mapper.CategoryMapper;
import com.wenxuanduan.wiki.req.CategoryQueryReq;
import com.wenxuanduan.wiki.req.CategorySaveReq;
import com.wenxuanduan.wiki.resp.CategoryQueryResp;
import com.wenxuanduan.wiki.resp.PageResp;
import com.wenxuanduan.wiki.util.CopyUtil;
import com.wenxuanduan.wiki.util.SnowFlake;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class CategoryService {
    
    private static final Logger LOG = LoggerFactory.getLogger(CategoryService.class);
    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<CategoryQueryResp> list(CategoryQueryReq req) {
        CategoryExample categoryExample = new CategoryExample();
        CategoryExample.Criteria criteria = categoryExample.createCriteria();
        if(!ObjectUtils.isEmpty(req.getName())){
            criteria.andNameLike("%" + req.getName() + "%");
        }
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);

        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);
        LOG.info("Total columns: {}", pageInfo.getTotal());
        LOG.info("Total pages: {}", pageInfo.getPages());

        // copy list
        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);

        PageResp<CategoryQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    /**
     * Save
     */
    public void save(CategorySaveReq req) {
        Category category = CopyUtil.copy(req, Category.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            // add a new book
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);
        }
        else {
            // update previous book
            categoryMapper.updateByPrimaryKey(category);
        }
    }


    /**
     * Delete
     * @param id
     */
    public void delete(Long id) {
        categoryMapper.deleteByPrimaryKey(id);
    }
}
