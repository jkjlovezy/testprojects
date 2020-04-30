package com.focustech.gateway.site.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class ZookeeperClient {
    private CuratorFramework curator;

    @Value("${zookeeper.url}")
    private String zkHostPort;
    @Value("${zookeeper.rootpath:/gateway}")
    private String zkRootPath;

    @PostConstruct
    public void initZk() {
        try {
            RetryPolicy policy = new ExponentialBackoffRetry(1000, 10);
            curator = CuratorFrameworkFactory.builder().connectString(zkHostPort).sessionTimeoutMs(15000)
                    .retryPolicy(policy).build();
            curator.start();

        } catch (Exception e) {
            log.error("init zkClient error: ", e);
        }
    }

    public void register(String rootPath, TreeCacheListener treeCacheListener) throws Exception {
        TreeCache treeCache = new TreeCache(curator, rootPath);
        treeCache.start();
        treeCache.getListenable().addListener(treeCacheListener);
    }

//    public List<String> getAllNode() throws Exception {
//        List<String> allNodePathList = new LinkedList<>();
//        obtainNodeDataRecursive(zkRootPath, allNodePathList);
//        return allNodePathList;
//    }

//    private void obtainNodeDataRecursive(String path, List<String> allNodePathList) throws Exception {
//        List<String> children = curator.getChildren().forPath(path);
//        if (CollectionUtils.isEmpty(children)) {
//            return;
//        }
//        Stat stat;
//        for (String childName : children) {
//            String childPath = path + "/" + childName;
//            allNodePathList.add(childPath);
//            byte[] bytes = curator.getData().storingStatIn(stat = new Stat()).forPath(childPath);
//            //节点上有数据：维护API配置信息。
//            if (stat.getDataLength() != 0) {
//                aiConfigHolder.checkApiNodeData(childPath, bytes, stat.getVersion());
//            }
//            //节点已是叶子节点，continue操作
//            if (stat.getNumChildren() == 0) {
//                continue;
//            }
//            obtainNodeDataRecursive(childPath, allNodePathList);
//        }
//
//    }

    public static abstract class AbstractTreeCacheListener implements TreeCacheListener {

        @Override
        public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent){
            ChildData childData = treeCacheEvent.getData();
            if (childData == null) {
                log.info("treeCacheEvent.getData is null, treeCacheEvent = {}", treeCacheEvent);
                return;
            }

            switch (treeCacheEvent.getType()) {
                case NODE_ADDED:
                    handleEvent(childData.getPath(), childData.getData(), childData.getStat().getVersion(), Operation.ADDED);
                    break;
                case NODE_UPDATED:
                    handleEvent(childData.getPath(), childData.getData(), childData.getStat().getVersion(), Operation.UPDATED);
                    break;
                case NODE_REMOVED:
                    handleEvent(childData.getPath(), childData.getData(), childData.getStat().getVersion(), Operation.REMOVED);
                    break;
                default:
                    log.info("other event, type={}", treeCacheEvent.getType());
                    break;
            }
        }

        abstract void handleEvent(String path, byte[] data, int dataVersion, Operation operation);
    }



    public enum Operation {ADDED, UPDATED, REMOVED}
}
