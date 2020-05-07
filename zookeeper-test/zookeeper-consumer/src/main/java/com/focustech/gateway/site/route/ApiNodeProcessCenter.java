package com.focustech.gateway.site.route;

import com.alibaba.fastjson.JSON;
import com.focustech.gateway.site.route.data.ApiNodeData;
import com.focustech.gateway.site.route.data.ApiNodeInfo;
import com.focustech.gateway.site.zookeeper.core.NodeEvent;
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

@Slf4j
@Component
public class ApiNodeProcessCenter {
//    private ConcurrentHashMap<String, NodeInfo<ApiNodeData>> apiNodes = new ConcurrentHashMap<String, NodeInfo<ApiNodeData>>();
    @Value("${zookeeper.api.path:/gateway/api}")
    private String apiRootPath;
    @Autowired
    ApiNodeChangeHandler apiNodeHandler;
    @Autowired
    ZookeeperClient zookeeperClient;
    @Autowired
    ApiHolder apiHolder;
    @Autowired
    @Qualifier("apiNodeListener")
    TreeCacheListener apiNodeListener;

    @PostConstruct
    public void registerListener() {
        try {
            zookeeperClient.registerListener(apiRootPath, apiNodeListener);
        } catch (Exception e) {
            log.error("register the apiNodeListener error: ", e);
        }
    }

    public void synchronizeNodeInfo() throws Exception {
        zookeeperClient.synchronizeNodeInfo(apiRootPath, apiHolder.getApi().keySet(), this, ApiNodeProcessCenter::checkForAdded, ApiNodeProcessCenter::onDeleted);

    }

    public void checkForAdded(String nodePath, byte[] data, int dataVersion) {
        try {
            ApiNodeInfo<ApiNodeData> currentNode = new ApiNodeInfo<>(dataVersion, JSON.parseObject(new String(data, Charset.forName("UTF-8")), ApiNodeData.class));
            ApiNodeInfo<ApiNodeData> oldNode = apiHolder.getApi().putIfAbsent(nodePath.substring(apiRootPath.length()), currentNode);
            if (oldNode != null) {
                if (oldNode.getDataVersion() < currentNode.getDataVersion()) {
                    if (log.isDebugEnabled())
                        log.debug("【checkForAdded】api node updated,nodePath={},nodeInfo={}", nodePath, currentNode);
                    NodeEvent<ApiNodeData> updateEvent = new NodeEvent(NodeOperationType.UPDATED, nodePath.substring(apiRootPath.length()), dataVersion, JSON.parseObject(new String(data, Charset.forName("UTF-8")), ApiNodeData.class));
                    apiNodeHandler.handle(updateEvent);
                }
            } else {
                if (log.isDebugEnabled())
                    log.debug("【checkForAdded】api node added,nodePath={},nodeInfo={}", nodePath, currentNode);
                NodeEvent<ApiNodeData> addEvent = new NodeEvent(NodeOperationType.ADDED, nodePath.substring(apiRootPath.length()), dataVersion, JSON.parseObject(new String(data, Charset.forName("UTF-8")), ApiNodeData.class));
                apiNodeHandler.handle(addEvent);

            }
        } catch (Exception e) {
            log.error("【checkForAdded】api node updated,nodePath={},data={}", nodePath.substring(apiRootPath.length()), new String(data, Charset.forName("UTF-8")));
        }
    }

    public void onDeleted(String deletedNodePath) {
        if (log.isDebugEnabled())
            log.debug("【onDeleted】api node deleted,path={}", deletedNodePath);
        NodeEvent<ApiNodeData> deleteEvent = new NodeEvent<>(NodeOperationType.DELETED, deletedNodePath.substring(apiRootPath.length()), 0, null);
        apiNodeHandler.handle(deleteEvent);

    }
}
