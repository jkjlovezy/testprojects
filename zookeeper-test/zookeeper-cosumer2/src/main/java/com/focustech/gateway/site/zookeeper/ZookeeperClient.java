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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

//@Component
@Slf4j
public class ZookeeperClient {
    @Value("${zookeeper.url}")
    private String zkHostPort;
    @Value("${zookeeper.rootpath:/gateway}")
    private String zkRootPath;

    ZkClient zkClient;

    static Map<String, String> apiInfos = new ConcurrentHashMap<String, String>();
    static Set<String> apiIds = new HashSet<>();

    @PostConstruct
    public void initZk() {
        try {
//            zkClient = new ZkClient(zkHostPort, 30000, 30000, new SerializableSerializer());
            zkClient = new ZkClient(zkHostPort, 30000, 30000, new ZkSerializer() {
                public Object deserialize(byte[] bytes) throws ZkMarshallingError {
                    return new String(bytes, Charset.forName("UTF-8"));
                }

                public byte[] serialize(Object obj) throws ZkMarshallingError {
                    return String.valueOf(obj).getBytes(Charset.forName("UTF-8"));
                }
            });

            if (!zkClient.exists(zkRootPath)) {
                zkClient.createPersistent(zkRootPath, true);
            }
            startWatch(zkRootPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startWatch(final String path) throws Exception {
        log.info("start watch path: {}", path);
        zkClient.subscribeChildChanges(path, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                try {
                    log.info("handleChildChange, parentPath={},currentChilds={}", parentPath, JSON.toJSONString(currentChilds));
                    if (currentChilds == null) {
                        apiInfos.clear();
                        apiIds.clear();
                        return;
                    }
                    List<String> needAddCurrentChilds = new ArrayList<>(currentChilds);
                    //内存{1、2、3} ； ZK： {2、3、4}；
                    needAddCurrentChilds.removeAll(apiIds);
                    //内存{1、2、3} ； ZK： {4}；
                    log.info("handleChildChange, need add: {}", JSON.toJSONString(needAddCurrentChilds));
                    for (String currentChild : needAddCurrentChilds) {
                        if (apiInfos.get(currentChild) == null) {
                            String nodePath = parentPath + "/" + currentChild;
                            apiIds.add(currentChild);
                            apiInfos.put(currentChild, getApiData(nodePath));
                            watchNode(nodePath);
                        }
                    }

                    List<String> needDeleteApiIds = new ArrayList<>(apiIds);
                    needDeleteApiIds.removeAll(currentChilds);
                    log.info("handleChildChange, need delete: {}", JSON.toJSONString(needDeleteApiIds));
                    for (String needDeleteApiId : needDeleteApiIds) {
                        apiInfos.remove(needDeleteApiId);
                        apiIds.remove(needDeleteApiId);
                    }

                    log.info("mem apiInfos = {}", apiInfos);
                    log.info("mem apiIds = {}", JSON.toJSONString(apiIds));
                } catch (Exception e) {
                    log.info("handleChildChange exception:", e);
                    log.info("mem apiInfos = {}", apiInfos);
                    log.info("mem apiIds = {}", JSON.toJSONString(apiIds));
                }
            }


        });
        watchNode(path);
    }

    IZkDataListener dataListener = new IZkDataListener() {
        @Override
        public void handleDataChange(String dataPath, Object data) throws Exception {
//                String commandNodeDataJsonString = new String((byte[]) data);
            log.info("handleDataChange,path={},data={}", dataPath, data);
        }

        @Override
        public void handleDataDeleted(String dataPath) throws Exception {
            log.info("handleDataDeleted,path={}", dataPath);
        }
    };

    private void watchNode(String nodePath) {
        zkClient.subscribeDataChanges(nodePath, dataListener);
    }

    private String getApiData(String nodePath) {
        String data = zkClient.readData(nodePath, true);
        log.info("getApiData: {}", data);
        return data == null ? "" : data;
    }


}
