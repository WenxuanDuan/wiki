package com.wenxuanduan.wiki.controller;

import com.wenxuanduan.wiki.req.EbookQueryReq;
import com.wenxuanduan.wiki.req.EbookSaveReq;
import com.wenxuanduan.wiki.resp.CommonResp;
import com.wenxuanduan.wiki.resp.EbookQueryResp;
import com.wenxuanduan.wiki.resp.PageResp;
import com.wenxuanduan.wiki.service.EbookService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/ebook")
public class EbookController {

    private static final Logger LOG = LoggerFactory.getLogger(EbookController.class);

    @Resource
    private EbookService ebookService;

    @GetMapping("/list")
    public CommonResp list(@Valid EbookQueryReq req) {
        CommonResp<PageResp<EbookQueryResp>> resp = new CommonResp<>();
        PageResp<EbookQueryResp> list = ebookService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody EbookSaveReq req) {
        CommonResp resp = new CommonResp<>();
        ebookService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        ebookService.delete(id);
        return resp;
    }

    @RequestMapping("/upload/avatar")
    public CommonResp upload(@RequestParam MultipartFile avatar) throws IOException {
        LOG.info("Start to upload files：{}", avatar);
        LOG.info("File Name：{}", avatar.getOriginalFilename());
        LOG.info("File Size：{}", avatar.getSize());

        // save files to local
        String fileName = avatar.getOriginalFilename();
        String fullPath = "D:/file/wiki/" + fileName;
        File dest = new File(fullPath);
        avatar.transferTo(dest);
        LOG.info(dest.getAbsolutePath());

        return new CommonResp();
    }
}
