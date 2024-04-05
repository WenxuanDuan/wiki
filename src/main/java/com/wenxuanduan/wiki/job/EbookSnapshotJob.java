package com.wenxuanduan.wiki.job;

import com.wenxuanduan.wiki.service.EbookSnapshotService;
import com.wenxuanduan.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class EbookSnapshotJob {

    private static final Logger LOG = LoggerFactory.getLogger(EbookSnapshotJob.class);

    @Resource
    private EbookSnapshotService ebookSnapshotService;

    @Resource
    private SnowFlake snowFlake;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void doSnapshot() {
        // add log id
        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));
        LOG.info("Start to generate today's ebook snapshot");
        Long start = System.currentTimeMillis();
        ebookSnapshotService.genSnapshot();
        LOG.info("Finished today's ebook snapshot generation. Time consumed：{}ms", System.currentTimeMillis() - start);
    }

}
