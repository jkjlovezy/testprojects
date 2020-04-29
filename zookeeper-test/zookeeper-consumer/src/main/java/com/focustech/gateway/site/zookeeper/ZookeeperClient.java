package com.focustech.gateway.site.zookeeper;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.mysql.jdbc.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ZookeeperClient {
    private CuratorFramework curator;

    @Value("${zookeeper.url}")
    private String zkHostPort;
    @Value("${zookeeper.rootpath:/gateway}")
    private String zkRootPath;

    @Autowired
    private ApiConfigInfoHandler apiConfigInfoHandler;

    @PostConstruct
    public void initZk() {
        try {
            RetryPolicy policy = new ExponentialBackoffRetry(1000, 10);
            curator = CuratorFrameworkFactory.builder().connectString(zkHostPort).sessionTimeoutMs(15000)
                    .retryPolicy(policy).build();
            curator.start();
            TreeCache treeCache = new TreeCache(curator, zkRootPath);
            treeCache.start();
            treeCache.getListenable().addListener(treeCacheListener);
        } catch (Exception e) {
            log.error("init zkClient error: ", e);
        }
    }

    public Map<String, String> getAllNodeData() throws Exception {
        Map<String, String> dataMap = new HashMap<String, String>();
        obtainNodeDataRecursive(zkRootPath, dataMap);
        return dataMap;
    }

    private void obtainNodeDataRecursive(String path, Map<String, String> dataMap) throws Exception {
        List<String> children = curator.getChildren().forPath(path);
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        for (String childName : children) {
            String childPath = path + "/" + childName;
            byte[] bytes = curator.getData().forPath(childPath);
            if (bytes != null) {
                String data = new String(bytes, Charset.forName("UTF-8"));
                if (!StringUtils.isNullOrEmpty(data))
                    dataMap.put(childPath.substring(zkRootPath.length()), data);
            }
            obtainNodeDataRecursive(childPath, dataMap);
        }

    }

    TreeCacheListener treeCacheListener = ((curatorFramework, treeCacheEvent) -> {
        ChildData childData = treeCacheEvent.getData();
        if (childData == null) {
            log.info("treeCacheEvent.getData is null, treeCacheEvent = {}", treeCacheEvent);
            return;
        }
        String data = childData.getData() == null ? "" : new String(childData.getData());
        switch (treeCacheEvent.getType()) {
            case NODE_ADDED:
                log.info("add node, path={},data={}", childData.getPath(), data);
                if (!Strings.isNullOrEmpty(data)) {
                    processApiConfigEvent(new ApiConfigEvent(ApiConfigEvent.Type.ADDED, childData.getPath(), JSON.parseObject(data, ApiZkNodeData.class)));
                }
                break;
            case NODE_UPDATED:
                log.info("update node, path={},data={}", childData.getPath(), data);
                if (!Strings.isNullOrEmpty(data)) {
                    processApiConfigEvent(new ApiConfigEvent(ApiConfigEvent.Type.UPDATED, childData.getPath(), JSON.parseObject(data, ApiZkNodeData.class)));
                }
                break;
            case NODE_REMOVED:
                log.info("remove node, path={},data={}", childData.getPath(), data);
                processApiConfigEvent(new ApiConfigEvent(ApiConfigEvent.Type.REMOVED, childData.getPath(), null));
                break;
            default:
                log.info("other event, type={}", treeCacheEvent.getType());
                break;
        }
    });

    public void processApiConfigEvent(ApiConfigEvent event) {
        apiConfigInfoHandler.handle(event);
    }
}
