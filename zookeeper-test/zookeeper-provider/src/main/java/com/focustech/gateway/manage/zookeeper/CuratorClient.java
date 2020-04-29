package com.focustech.gateway.manage.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;

@Component
@Slf4j
public class CuratorClient {
    @Value("${zookeeper.url}")
    private String zkHostPort;
    @Value("${zookeeper.rootpath:/gateway}")
    private String zkRootPath;
    CuratorFramework curator;

    @PostConstruct
    public void initZk() {
        try {
            RetryPolicy policy = new ExponentialBackoffRetry(1000, 10);
            curator = CuratorFrameworkFactory.builder().connectString(zkHostPort).sessionTimeoutMs(15000)
                    .retryPolicy(policy).build();
            curator.start();
            TreeCache treeCache = new TreeCache(curator, zkRootPath);
            treeCache.start();

        } catch (Exception e) {
            log.error("init zkClient error: ", e);
        }
    }

    public void addNode(String nodePath, String data) throws Exception {
        try {
            String s = curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(zkRootPath + nodePath, data.getBytes(Charset.forName("UTF-8")));
            log.info("【CuratorClient.addNode】 path=:{}", s);
        } catch (KeeperException.NodeExistsException e) {
            log.warn("【CuratorClient.addNode】the node has been existed, path={}:", e.getPath());
            log.warn("【CuratorClient.addNode】before replace data:{}", new String(curator.getData().forPath(zkRootPath + nodePath), "UTF-8"));
            curator.setData().forPath(zkRootPath + nodePath, data.getBytes(Charset.forName("UTF-8")));
            log.warn("【CuratorClient.addNode】after replace data:{}", new String(curator.getData().forPath(zkRootPath + nodePath), "UTF-8"));
        }
    }


}
