package com.wenxuanduan.wiki.job;

import com.wenxuanduan.wiki.service.DocService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DocJob {

    private static final Logger LOG = LoggerFactory.getLogger(DocJob.class);

    @Resource
    private DocService docService;

    /**
     * update ebook doc information every 30 seconds
     */
    @Scheduled(cron = "5/30 * * * * ?")
    public void cron() {
        docService.updateEbookInfo();
    }

}
