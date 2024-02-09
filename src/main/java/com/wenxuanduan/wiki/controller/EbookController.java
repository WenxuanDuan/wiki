package com.wenxuanduan.wiki.controller;

import com.wenxuanduan.wiki.req.EbookReq;
import com.wenxuanduan.wiki.resp.CommonResp;
import com.wenxuanduan.wiki.resp.EbookResp;
import com.wenxuanduan.wiki.resp.PageResp;
import com.wenxuanduan.wiki.service.EbookService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Resource
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp list(EbookReq req) {
        CommonResp<PageResp<EbookResp>> resp = new CommonResp<>();
        PageResp<EbookResp> list = ebookService.list(req);
        resp.setContent(list);
        return resp;
    }
}
