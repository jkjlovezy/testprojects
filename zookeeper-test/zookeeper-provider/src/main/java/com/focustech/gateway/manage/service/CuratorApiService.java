package com.focustech.gateway.manage.service;

import com.alibaba.fastjson.JSON;
import com.focustech.gateway.manage.dao.ApiDao;
import com.focustech.gateway.manage.dao.ServiceDao;
import com.focustech.gateway.manage.dto.ApiZkNodeData;
import com.focustech.gateway.manage.entity.ApiEntity;
import com.focustech.gateway.manage.entity.ServiceEntity;
import com.focustech.gateway.manage.request.ApiPostRequest;
import com.focustech.gateway.manage.zookeeper.CuratorClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class CuratorApiService {
    @Autowired
    private ApiDao apiDao;
    @Autowired
    private ServiceDao serviceDao;
    @Autowired
    private CuratorClient curatorClient;

    @Transactional
    public void addApi(ApiPostRequest request) throws Exception {
        ApiEntity apiEntity = request.convertToEntity();
        ServiceEntity serviceEntity = serviceDao.findById(request.getServiceId().intValue());
        if (serviceEntity == null) {
            throw new RuntimeException("params is not valid");
        }
        apiEntity.setService(serviceEntity);
        apiDao.save(apiEntity);

        ApiZkNodeData apiZkNodeData = buildApiZkNodeData(apiEntity);
        log.info("build apiZkNodeData :{}", JSON.toJSONString(apiZkNodeData));

        curatorClient.addNode(apiEntity.getApiPath(), JSON.toJSONString(apiZkNodeData));

    }

    private ApiZkNodeData buildApiZkNodeData(ApiEntity api) {
        return ApiZkNodeData.builder()
                .serviceDomain("http://goods.crov.com")
                .servicePath(api.getService().getServicePath())
                .authentStrategy(api.getAuthentStrategy())
                .requestTimeout(api.getRequestTimeout())
                .flowlimitEnable(api.getFlowlimitEnable())
                .ruleGroupId(api.getRuleGroupId())
                .build();
    }

}
