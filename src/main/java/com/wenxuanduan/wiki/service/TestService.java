package com.wenxuanduan.wiki.service;

import com.wenxuanduan.wiki.domain.Test;
import com.wenxuanduan.wiki.mapper.TestMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestService {

    @Resource
    private TestMapper testMapper;

    public List<Test> list() {
        return testMapper.list();
    }
}
