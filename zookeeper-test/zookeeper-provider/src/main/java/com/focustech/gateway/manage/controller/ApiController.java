package com.focustech.gateway.manage.controller;

import com.alibaba.fastjson.JSON;
import com.focustech.gateway.manage.dao.ApiDao;
import com.focustech.gateway.manage.entity.ApiEntity;
import com.focustech.gateway.manage.request.ApiPostRequest;
import com.focustech.gateway.manage.service.ApiService;
import com.focustech.gateway.manage.service.CuratorApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ApiController {
    @Autowired
    private ApiService apiService;
    @Autowired
    private ApiDao apiDao;
    @Autowired
    private CuratorApiService curatorApiService;

    @GetMapping("/api/{id}")
    public ResponseEntity<String> addApi(@PathVariable("id") int id) {
        log.info("getApi method,id = {}", id);
        ApiEntity api = apiDao.findById(id);
        return ResponseEntity.ok("success");
    }

    @PostMapping("/api")
    public ResponseEntity<String> addApi(@RequestBody ApiPostRequest request) {
        log.info("addApi method,request = {}", JSON.toJSONString(request));
        apiService.addApi(request);
        return ResponseEntity.ok("success");
    }

    @PostMapping("/api2")
    public ResponseEntity<String> addApi2(@RequestBody ApiPostRequest request) throws Exception {
        log.info("addApi method,request = {}", JSON.toJSONString(request));
        curatorApiService.addApi(request);
        return ResponseEntity.ok("success");
    }
}
