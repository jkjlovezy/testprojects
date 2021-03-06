package com.focustech.gateway.site.zookeeper.servicenode;

import com.focustech.gateway.site.route.data.ApiNodeData;
import com.focustech.gateway.site.zookeeper.core.NodeEvent;
import com.focustech.gateway.site.zookeeper.core.ZookeeperClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ServiceNodeProcessCenter {
    private ConcurrentHashMap<String, NodeEvent<ApiNodeData>> apiNodes = new ConcurrentHashMap<String, NodeEvent<ApiNodeData>>();
    @Value("${zookeeper.service.path:/gateway/service}")
    private String monitorPath;
    @Autowired
    ZookeeperClient zookeeperClient;
    @Autowired
    @Qualifier("serviceNodeListener")
    TreeCacheListener serviceNodeListener;


    @PostConstruct
    public void registerListener() {
        try {
            zookeeperClient.registerListener(monitorPath, serviceNodeListener);
        } catch (Exception e) {
            log.error("register the apiNodeListener error: ", e);
        }
    }

//    public void checkApiNodeData(String nodePath, byte[] data, int dataVersion) {
//        NodeEvent<ApiNodeData> apiNode = new NodeEvent<>(dataVersion, JSON.parseObject(new String(data, Charset.forName("UTF-8")), ApiNodeData.class));
//        NodeEvent<ApiNodeData> currentNode = apiNodes.putIfAbsent(nodePath, apiNode);
//        if (currentNode != null) {
//            if (currentNode.getDataVersion() < apiNode.getDataVersion()) {
//                log.info("【ApiConfigHolder.checkApiNodeData】api node updated,path={},data={}", nodePath, apiNode.getData());
//                apiNodeHandler.handle(new ApiConfigEvent(UPDATED, nodePath, apiNode.getData()));
//                apiNodes.put(nodePath, apiNode);
//            }
//        } else {
//            log.info("【ApiConfigHolder.checkApiNodeData】api node added,path={},data={}", nodePath, apiNode.getData());
//            apiNodeHandler.handle(new ApiConfigEvent(ADDED, nodePath, apiNode.getData()));
//        }
//    }
//
//    public void compareAndRemoveApi(List<String> allNodePathList) {
//        apiNodes.entrySet().removeIf(entry -> {
//            if (!allNodePathList.contains(entry.getKey())) {
//                apiNodeHandler.handle(new ApiConfigEvent(REMOVED, entry.getKey(), null));
//                return true;
//            }
//            return false;
//        });
//    }
}
