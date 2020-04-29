package com.focustech.gateway.manage.controller;

import com.alibaba.fastjson.JSON;
import com.focustech.gateway.manage.request.ApiPostRequest;
import com.focustech.gateway.manage.service.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ApiController {
    @Autowired
    private ApiService apiService;

    @PostMapping("/api")
    public ResponseEntity<String> addApi(@RequestBody ApiPostRequest request) {
        log.info("addApi method,request = {}", JSON.toJSONString(request));
        apiService.addApi(request);
        return ResponseEntity.ok("success");
    }
}
