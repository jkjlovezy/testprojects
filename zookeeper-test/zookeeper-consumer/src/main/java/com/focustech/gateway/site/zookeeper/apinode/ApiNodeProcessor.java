package com.focustech.gateway.site.zookeeper.apinode;

import com.alibaba.fastjson.JSON;
import com.focustech.gateway.site.zookeeper.core.NodeEvent;
import com.focustech.gateway.site.zookeeper.core.NodeInfo;
import com.focustech.gateway.site.zookeeper.core.NodeOperationType;
import com.focustech.gateway.site.zookeeper.core.ZookeeperClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ApiNodeProcessor {
    private ConcurrentHashMap<String, NodeInfo<ApiNodeData>> apiNodes = new ConcurrentHashMap<String, NodeInfo<ApiNodeData>>();
    @Value("${zookeeper.api.path:/gateway/api}")
    private String monitorPath;
    @Autowired
    ApiNodeHandler apiNodeHandler;
    @Autowired
    ZookeeperClient zookeeperClient;
    @Autowired
    @Qualifier("apiNodeListener")
    TreeCacheListener apiNodeListener;

    @PostConstruct
    public void registerListener() {
        try {
            zookeeperClient.registerListener(monitorPath, apiNodeListener);
        } catch (Exception e) {
            log.error("register the apiNodeListener error: ", e);
        }
    }

    public void synchronizeNodeInfo() throws Exception {
        zookeeperClient.synchronizeNodeInfo(monitorPath, apiNodes.keySet(), this, ApiNodeProcessor::checkForAdded, ApiNodeProcessor::onDeleted);

    }

    public void checkForAdded(String nodePath, byte[] data, int dataVersion) {
        try {
            NodeInfo<ApiNodeData> currentNode = new NodeInfo<>(dataVersion, JSON.parseObject(new String(data, Charset.forName("UTF-8")), ApiNodeData.class));
            NodeInfo<ApiNodeData> oldNode = apiNodes.putIfAbsent(nodePath, currentNode);
            if (oldNode != null) {
                if (oldNode.getDataVersion() < currentNode.getDataVersion()) {
                    if (log.isDebugEnabled())
                        log.debug("【checkForAdded】api node updated,path={},nodeInfo={}", nodePath, currentNode);
                    apiNodes.put(nodePath, currentNode);
                    NodeEvent<ApiNodeData> updateEvent = new NodeEvent<>(NodeOperationType.UPDATED, nodePath, dataVersion, JSON.parseObject(new String(data, Charset.forName("UTF-8")), ApiNodeData.class));
                    apiNodeHandler.handle(updateEvent);
                }
            } else {
                if (log.isDebugEnabled())
                    log.debug("【checkForAdded】api node added,path={},nodeInfo={}", nodePath, currentNode);
                NodeEvent<ApiNodeData> addEvent = new NodeEvent<>(NodeOperationType.ADDED, nodePath, dataVersion, JSON.parseObject(new String(data, Charset.forName("UTF-8")), ApiNodeData.class));
                apiNodeHandler.handle(addEvent);

            }
        } catch (Exception e) {
            log.error("【checkForAdded】api node updated,path={},data={}", nodePath, new String(data, Charset.forName("UTF-8")));
        }
    }

    public void onDeleted(String deletedNodePath) {
        if (log.isDebugEnabled())
            log.debug("【checkForAdded】api node added,path={}", deletedNodePath);
        NodeEvent<ApiNodeData> deleteEvent = new NodeEvent<>(NodeOperationType.DELETED, deletedNodePath, 0, null);
        apiNodeHandler.handle(deleteEvent);
        apiNodes.remove(deletedNodePath);
    }
}
