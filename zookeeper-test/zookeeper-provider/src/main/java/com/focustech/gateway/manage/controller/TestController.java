package com.focustech.gateway.manage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @PostMapping("/test/testRateLimit")
    public ResponseEntity<String> testRateLimit(){
        return ResponseEntity.ok("this is a post request");
    }

    @GetMapping("/test/testRateLimit")
    public ResponseEntity<String> testRateLimit2(){
        return ResponseEntity.ok("this is a get request");
    }
}
