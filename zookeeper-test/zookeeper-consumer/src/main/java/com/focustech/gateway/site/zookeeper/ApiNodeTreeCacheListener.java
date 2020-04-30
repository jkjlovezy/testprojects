package com.focustech.gateway.site.zookeeper;

import com.alibaba.fastjson.JSON;
import com.focustech.gateway.site.zookeeper.ZookeeperClient.AbstractTreeCacheListener;
import com.focustech.gateway.site.zookeeper.ZookeeperClient.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.Charset;

import static com.focustech.gateway.site.zookeeper.ZookeeperClient.Operation.ADDED;
import static com.focustech.gateway.site.zookeeper.ZookeeperClient.Operation.REMOVED;
import static com.focustech.gateway.site.zookeeper.ZookeeperClient.Operation.UPDATED;

@Slf4j
public class ApiNodeTreeCacheListener extends AbstractTreeCacheListener {
    @Autowired
    ApiNodeHandler apiNodeHandler;

    @Override
    void handleEvent(String path, byte[] data, int dataVersion, Operation operation) {
        log.info("remove node, path={},data={},dataVersion={},operation={}", path, data, dataVersion, operation);
        switch (operation) {
            case ADDED:
                if (data != null && data.length > 0)
                    apiNodeHandler.handle(new ApiConfigEvent(ADDED, path, JSON.parseObject(new String(data, Charset.forName("UTF-8")), ZkApiNodeData.class)));
                break;
            case UPDATED:
                if (data != null && data.length > 0)
                    apiNodeHandler.handle(new ApiConfigEvent(UPDATED, path, JSON.parseObject(new String(data, Charset.forName("UTF-8")), ZkApiNodeData.class)));
                break;
            case REMOVED:
                apiNodeHandler.handle(new ApiConfigEvent(REMOVED, path, null));
                break;
            default:

        }

    }
}
