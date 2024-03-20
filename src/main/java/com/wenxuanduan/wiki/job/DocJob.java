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
        LOG.info("Update docs under ebooks STARTED");
        long start = System.currentTimeMillis();
        docService.updateEbookInfo();
        LOG.info("Update docs under ebooks FINISHED. Time consumption: {}ms", System.currentTimeMillis() - start);
    }

}
