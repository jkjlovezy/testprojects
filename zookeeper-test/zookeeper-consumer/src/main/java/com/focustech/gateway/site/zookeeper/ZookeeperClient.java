package com.focustech.gateway.site.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

@Component
@Slf4j
public class ZookeeperClient {
    private CuratorFramework curator;

    @Value("${zookeeper.url}")
    private String zkHostPort;
    @Value("${zookeeper.rootpath:/gateway}")
    private String zkRootPath;

    @Autowired
    private ApiConfigHolder aiConfigHolder;


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

    public List<String> getAllNode() throws Exception {
        List<String> allNodePathList = new LinkedList<>();
        obtainNodeDataRecursive(zkRootPath, allNodePathList);
        return allNodePathList;
    }

    public static void main(String[] args) throws Exception {
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 10);
        CuratorFramework curator = CuratorFrameworkFactory.builder().connectString("127.0.0.1:2181").sessionTimeoutMs(15000)
                .retryPolicy(policy).build();
        curator.start();
        Stat stat = new Stat();

        List<String> children = curator.getChildren().storingStatIn(stat).forPath("/gateway/api");
        System.out.println("----------------" + stat);
        System.out.println(System.currentTimeMillis());
    }

    private void obtainNodeDataRecursive(String path, List<String> allNodePathList) throws Exception {
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
                aiConfigHolder.checkApiNodeData(childPath, stat.getVersion(), bytes);
            }
            //节点已是叶子节点，continue操作
            if (stat.getNumChildren() == 0) {
                continue;
            }
            obtainNodeDataRecursive(childPath, allNodePathList);
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
                if (childData.getStat().getDataLength() != 0) {
                    aiConfigHolder.checkApiNodeData(childData.getPath(), childData.getStat().getVersion(), childData.getData());
                }
                break;
            case NODE_UPDATED:
                log.info("update node, path={},data={}", childData.getPath(), data);
                if (childData.getStat().getDataLength() != 0) {
                    aiConfigHolder.checkApiNodeData(childData.getPath(), childData.getStat().getVersion(), childData.getData());
                }
                break;
            case NODE_REMOVED:
                log.info("remove node, path={},data={}", childData.getPath(), data);
                aiConfigHolder.removeApiNode(childData.getPath());
                break;
            default:
                log.info("other event, type={}", treeCacheEvent.getType());
                break;
        }
    });

}
