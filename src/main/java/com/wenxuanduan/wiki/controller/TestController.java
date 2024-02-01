package com.wenxuanduan.wiki.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 @RestController: return a string
 @Controller: return a page
 @RestController = @Controller + Responsebody

 HTTP Request: Get-query, post-add, put-update, delete-delete
 1. @GetMapping("/hello") == @RequestMapping(value="/hello", method=RequestMethod.GET)
 2. @PostMapping
 3. @PutMapping
 4. @DeleteMapping
 **/
@RestController
public class TestController {
    @Value("${test.hello:Test}")
    private String testHello;

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!" + testHello;
    }

    @PostMapping("/hello/post")
    public String helloPost(String name) {
        return "Hello World! Post, " + name;
    }
}
