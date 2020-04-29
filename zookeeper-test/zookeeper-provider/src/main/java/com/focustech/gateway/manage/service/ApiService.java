package com.focustech.gateway.manage.service;

import com.alibaba.fastjson.JSON;
import com.focustech.gateway.manage.entity.ApiEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import com.focustech.gateway.manage.dao.ApiDao;
import com.focustech.gateway.manage.request.ApiPostRequest;
import com.focustech.gateway.manage.zookeeper.ZookeeperClient;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ApiService {
    @Autowired
    private ApiDao apiDao;
    @Autowired
    private ZookeeperClient zookeeperClient;

    public void addApi(ApiPostRequest request) {
        ApiEntity apiEntity = apiDao.save(request.convertToEntity());
        try {
            zookeeperClient.addNode(apiEntity.getId().toString(), JSON.toJSONString(apiEntity), createApiCallback);
        } catch (Exception e) {
            log.error("ApiService.addApi exception:", e);
            rollbackAddedApi(apiEntity);
        }
    }

    private void rollbackAddedApi(ApiEntity apiEntity) {
        apiDao.delete(apiEntity.getId());
    }

    StringCallback createApiCallback = new StringCallback() {
        public void processResult(int rc, String path, Object ctx, String name) {
            ApiEntity apiEntity = (ApiEntity) ctx;
            System.out.println("ctx=" + apiEntity);
            switch (Code.get(rc)) {
                case OK:
                    log.info("create node [{}] success.", path);
                    break;
                default:
                    log.info("create node [{}] fail, reason: {}", KeeperException.create(Code.get(rc), path));
                    rollbackAddedApi(apiEntity);
            }
        }
    };
}
