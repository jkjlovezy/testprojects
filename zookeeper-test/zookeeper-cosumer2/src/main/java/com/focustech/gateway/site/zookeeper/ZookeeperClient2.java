package com.focustech.gateway.site.zookeeper;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

//@Component
@Slf4j
public class ZookeeperClient2 {
    @Value("${zookeeper.url}")
    private String zkHostPort;
    @Value("${zookeeper.rootpath:/gateway}")
    private String zkRootPath;

    ZkClient zkClient;

    static Map<String, String> apiInfos = new ConcurrentHashMap<String, String>();
    static Set<String> apiIds = new HashSet<>();

    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("127.0.0.1:2181", 30000, 30000, zkSerializer);
        Optional.ofNullable(zkClient.subscribeChildChanges("/gateway", (parentPath, currentChilds) -> {
            log.info("handleChildChange, parentPath={},currentChilds={}", parentPath, JSON.toJSONString(currentChilds));
        })).ifPresent(s -> s.forEach(System.out::println));
    }

    @PostConstruct
    public void initZk() {
        try {
//            zkClient = new ZkClient(zkHostPort, 30000, 30000, new SerializableSerializer());
            zkClient = new ZkClient(zkHostPort, 30000, 30000, zkSerializer);
            if (!zkClient.exists(zkRootPath)) {
                zkClient.createPersistent(zkRootPath, true);
            }
            recursiveWatch(zkRootPath);
        } catch (Exception e) {
            e.printStackTrace();
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

    private void recursiveWatch(String nodePath) {
        Optional.ofNullable(startWatch(nodePath)).ifPresent(list -> list.forEach(s -> recursiveWatch(nodePath+"/"+s)));
    }

    private List<String> startWatch(String nodePath) {
        log.info("start watch nodePath: {}", nodePath);
        zkClient.subscribeDataChanges(nodePath, dataListener);
        return zkClient.subscribeChildChanges(nodePath, childListener);
    }

    IZkChildListener childListener = new IZkChildListener() {
        @Override
        public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
            try {
                if (currentChilds == null) {
                    return;
                }
                log.info("handleChildChange, parentPath={},currentChilds={}", parentPath, JSON.toJSONString(currentChilds));
                for (String currentChild : currentChilds) {
                    if (apiInfos.get(currentChild) == null) {
                        String nodePath = parentPath + "/" + currentChild;
                        String nodeData = getNodeData(nodePath);
                        log.info(" child node path = {}, data={}", nodePath, nodeData);
                        startWatch(nodePath);
                    }
                }
            } catch (Exception e) {
                log.info("handleChildChange exception:", e);
            }
        }
    };

    IZkDataListener dataListener = new IZkDataListener() {
        @Override
        public void handleDataChange(String dataPath, Object data) throws Exception {
//                String commandNodeDataJsonString = new String((byte[]) data);
            log.info("--节点内容变更,path={},data={}", dataPath, data);
        }

        @Override
        public void handleDataDeleted(String dataPath) throws Exception {
            log.info("--节点已被删除,path={}", dataPath);
        }
    };


    private String getNodeData(String nodePath) {
        String data = zkClient.readData(nodePath, true);
        return data == null ? "" : data;
    }


}
