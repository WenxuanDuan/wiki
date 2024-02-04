package com.wenxuanduan.wiki.service;

import com.wenxuanduan.wiki.domain.Demo;
import com.wenxuanduan.wiki.mapper.DemoMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemoService {

    @Resource
    private DemoMapper demoMapper;

    public List<Demo> list() {
        return demoMapper.selectByExample(null);
    }
}
