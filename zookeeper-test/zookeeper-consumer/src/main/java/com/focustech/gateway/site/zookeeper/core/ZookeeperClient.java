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

    public interface CheckAddedConsumer<Processor> {
        public void check(Processor processor, String path, byte[] data, int version);
    }

    public interface OnDeletedConsumer<Processor> {
        public void onDelete(Processor processor, String deletedNodePath);
    }

    public <PROCESSOR> void synchronizeNodeInfo(String path, Set<String> memoryNodes, PROCESSOR processor, CheckAddedConsumer<PROCESSOR> checkAddedConsumer, OnDeletedConsumer<PROCESSOR> onDeletedConsumer) throws Exception {
        List<String> zkNodes = new LinkedList<>();
        checkForAddedRecursive(path, zkNodes, processor, checkAddedConsumer);
        checkForRemoved(memoryNodes, zkNodes, processor, onDeletedConsumer);
    }

    private <PROCESSOR> void checkForRemoved(Set<String> memoryNodes, List<String> zkNodes, PROCESSOR processor, OnDeletedConsumer<PROCESSOR> onDeletedConsumer) {
        if (memoryNodes == null || memoryNodes.isEmpty()) {
            return;
        }
        for (String nodePath : memoryNodes) {
            if (!zkNodes.contains(nodePath)) {
                onDeletedConsumer.onDelete(processor, nodePath);
            }
        }
    }

    private <PROCESSOR> void checkForAddedRecursive(String path, List<String> allNodePathList, PROCESSOR processor, CheckAddedConsumer<PROCESSOR> checkAddedConsumer) throws Exception {
        List<String> children = curator.getChildren().forPath(path);
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        Stat stat;
        for (String childName : children) {
            String childPath = path + "/" + childName;
            allNodePathList.add(childPath);
            byte[] bytes = curator.getData().storingStatIn(stat = new Stat()).forPath(childPath);
            //节点上有数据：维护API配置信息。
            if (stat.getDataLength() != 0) {
                checkAddedConsumer.check(processor, childPath, bytes, stat.getVersion());
            }
            //节点已是叶子节点，continue操作
            if (stat.getNumChildren() == 0) {
                continue;
            }
            checkForAddedRecursive(childPath, allNodePathList, processor, checkAddedConsumer);
        }

    }


}
