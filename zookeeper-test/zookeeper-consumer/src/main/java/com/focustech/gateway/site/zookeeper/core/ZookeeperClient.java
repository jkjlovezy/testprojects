package com.focustech.gateway.site.zookeeper.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Slf4j
public class ZookeeperClient {
    private CuratorFramework curator;

    private String zkHostPort;

    public ZookeeperClient(String zkHostPort) {
        this.zkHostPort = zkHostPort;
        init();
    }

    private void init() {
        try {
            RetryPolicy policy = new ExponentialBackoffRetry(1000, 10);
            curator = CuratorFrameworkFactory.builder().connectString(zkHostPort).sessionTimeoutMs(15000)
                    .retryPolicy(policy).build();
            curator.start();

        } catch (Exception e) {
            log.error("init zkClient error: ", e);
        }
    }

    public void registerListener(String rootPath, TreeCacheListener treeCacheListener) throws Exception {
        TreeCache treeCache = new TreeCache(curator, rootPath);
        treeCache.start();
        treeCache.getListenable().addListener(treeCacheListener);
    }

    public static interface CheckAddedConsumer<Processor> {
        public void check(Processor processor, String path, byte[] data, int version);
    }

    public static interface OnDeletedConsumer<Processor> {
        public void onDelete(Processor processor, String deletedNodePath);
    }

    public <PROCESSOR> void synchronizeNodeInfo(String apiRootPath, Set<String> apiPaths, PROCESSOR processor, CheckAddedConsumer<PROCESSOR> checkAddedConsumer, OnDeletedConsumer<PROCESSOR> onDeletedConsumer) throws Exception {
        List<String> fetchedNodePaths = new LinkedList<>();
        //递归查询zookeeper的节点信息，若节点上有数据，与内存apiMap比较。若内存apiMap中不存在，则进行路由新增操作； 若存在但数据版本号小于zk的版本号时，进行路由更新操作。
        checkForAddedRecursive(apiRootPath, fetchedNodePaths, processor, checkAddedConsumer);
        //内存apiMap中存在，但zookeeper上已不存在的API信息，进行路由删除操作。
        checkForRemoved(apiRootPath, apiPaths, fetchedNodePaths, processor, onDeletedConsumer);
    }

    private <PROCESSOR> void checkForRemoved(String apiRootPath, Set<String> apiPaths, List<String> nodePaths, PROCESSOR processor, OnDeletedConsumer<PROCESSOR> onDeletedConsumer) {
        if (apiPaths == null || apiPaths.isEmpty()) {
            return;
        }
        for (String apiPath : apiPaths) {
            if (!nodePaths.contains(apiRootPath + apiPath)) {
                onDeletedConsumer.onDelete(processor, apiPath);
            }
        }
    }

    private <PROCESSOR> void checkForAddedRecursive(String path, List<String> fetchedNodePaths, PROCESSOR processor, CheckAddedConsumer<PROCESSOR> checkAddedConsumer) throws Exception {
        List<String> children = curator.getChildren().forPath(path);
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        Stat stat;
        for (String childName : children) {
            String childPath = path + "/" + childName;
            fetchedNodePaths.add(childPath);
            byte[] bytes = curator.getData().storingStatIn(stat = new Stat()).forPath(childPath);
            //节点上有数据：维护API配置信息。
            if (stat.getDataLength() != 0) {
                checkAddedConsumer.check(processor, childPath, bytes, stat.getVersion());
            }
            //节点已是叶子节点，continue操作
            if (stat.getNumChildren() == 0) {
                continue;
            }
            checkForAddedRecursive(childPath, fetchedNodePaths, processor, checkAddedConsumer);
        }

    }


}
