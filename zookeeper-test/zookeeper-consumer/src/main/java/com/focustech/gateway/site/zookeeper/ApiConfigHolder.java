package com.focustech.gateway.site.zookeeper;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.focustech.gateway.site.zookeeper.ZookeeperClient.Operation.ADDED;
import static com.focustech.gateway.site.zookeeper.ZookeeperClient.Operation.REMOVED;
import static com.focustech.gateway.site.zookeeper.ZookeeperClient.Operation.UPDATED;

@Slf4j
@Component
public class ApiConfigHolder {
    private ConcurrentHashMap<String, ZkNode<ZkApiNodeData>> apiNodes = new ConcurrentHashMap<String, ZkNode<ZkApiNodeData>>();

    @Autowired
    private ApiNodeHandler apiNodeHandler;

    public void checkApiNodeData(String nodePath, byte[] data, int dataVersion) {
        ZkNode<ZkApiNodeData> apiNode = new ZkNode<>(dataVersion, JSON.parseObject(new String(data, Charset.forName("UTF-8")), ZkApiNodeData.class));
        ZkNode<ZkApiNodeData> currentNode = apiNodes.putIfAbsent(nodePath, apiNode);
        if (currentNode != null) {
            if (currentNode.getVersion() < apiNode.getVersion()) {
                log.info("【ApiConfigHolder.checkApiNodeData】api node updated,path={},data={}", nodePath, apiNode.getData());
                apiNodeHandler.handle(new ApiConfigEvent(UPDATED, nodePath, apiNode.getData()));
                apiNodes.put(nodePath, apiNode);
            }
        } else {
            log.info("【ApiConfigHolder.checkApiNodeData】api node added,path={},data={}", nodePath, apiNode.getData());
            apiNodeHandler.handle(new ApiConfigEvent(ADDED, nodePath, apiNode.getData()));
        }
    }

    public void compareAndRemoveApi(List<String> allNodePathList) {
        apiNodes.entrySet().removeIf(entry -> {
            if (!allNodePathList.contains(entry.getKey())) {
                apiNodeHandler.handle(new ApiConfigEvent(REMOVED, entry.getKey(), null));
                return true;
            }
            return false;
        });
    }
}
