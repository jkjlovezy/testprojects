package com.focustech.gateway.manage.zookeeper;

import com.focustech.gateway.manage.entity.ApiEntity;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;

@Slf4j
@Component
public class ZookeeperClient implements Watcher {
    @Value("${zookeeper.url}")
    private String zkHostPort;
    @Value("${zookeeper.rootpath:/gateway}")
    private String zkRootPath;

    ZkClient zkClient;

    @PostConstruct
    public void initZk() {
        try {
            zkClient = new ZkClient(zkHostPort, 15000, 10000, zkSerializer);
        } catch (Exception e) {
            log.error("init zkClient error:", e);
        }
    }

    static ZkSerializer zkSerializer = new ZkSerializer() {
        public Object deserialize(byte[] bytes) throws ZkMarshallingError {
            return new String(bytes, Charset.forName("UTF-8"));
        }

        public byte[] serialize(Object obj) throws ZkMarshallingError {
            return String.valueOf(obj).getBytes(Charset.forName("UTF-8"));
        }
    };


    public void addNode(String nodePath, String data, AsyncCallback.StringCallback createApiCallback) throws Exception {
        if (!zkClient.exists(zkRootPath))
            zkClient.createPersistent(zkRootPath, true);
        zkClient.createPersistent(zkRootPath + "/" + nodePath, data, ZooDefs.Ids.OPEN_ACL_UNSAFE);
    }


    public void deleteApi(ApiEntity apiEntity) {

    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println(event);
        switch (event.getType()) {
            case None:
                System.out.println("event type is None");
                break;
            case NodeCreated:
                System.out.println("event type is NodeCreated");
                break;
            case NodeDeleted:
                System.out.println("event type is NodeDeleted");
                break;
            case NodeDataChanged:
                System.out.println("event type is NodeDataChanged");
                break;
            case NodeChildrenChanged:
                System.out.println("event type is NodeChildrenChanged");
                break;
        }
    }
}
