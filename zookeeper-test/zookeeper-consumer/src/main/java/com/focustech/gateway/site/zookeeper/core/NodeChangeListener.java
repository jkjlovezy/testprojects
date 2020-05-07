package com.focustech.gateway.site.zookeeper.core;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;


@Slf4j
public class NodeChangeListener<Handler extends NodeHandler, Data extends NodeData> implements TreeCacheListener {
    private NodeHandler nodeHandler;
    private String rootPath;

    public NodeChangeListener(Handler nodeHandler, String rootPath) {
        this.nodeHandler = nodeHandler;
        this.rootPath = rootPath;
    }

    protected Class getNodeDataClass() {
        Type t = this.getClass().getGenericSuperclass();
        if (ParameterizedType.class.isAssignableFrom(t.getClass())) {
            return (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        } else {
            throw new RuntimeException("the treeCacheListener lose the NodeData generic,please specify the NodeData Generic or override the method of getNodeDataClass");
        }
    }

    @Override
    public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) {
        try {
            ChildData childData = treeCacheEvent.getData();
            if (childData == null)
                return;
            byte[] data = childData.getData();
            String path = childData.getPath();
            int dataVersion = childData.getStat().getVersion();
            if (data == null) {
                log.debug("treeCacheListener receive node event: type={},path={},dataVersion={},data={}", treeCacheEvent.getType(), path, dataVersion, null);
                return;
            } else {
                log.debug("treeCacheListener receive node event: type={},path={},dataVersion={},data={}", treeCacheEvent.getType(), path, dataVersion, new String(data, Charset.forName("UTF-8")));
            }
            switch (treeCacheEvent.getType()) {
                case NODE_ADDED:
                    if (data != null && data.length > 0) {
                        NodeEvent<Data> nodeEvent = (NodeEvent<Data>) NodeEvent.class.getConstructor(NodeOperationType.class, String.class, int.class, NodeData.class).newInstance(NodeOperationType.ADDED, path, dataVersion, JSON.parseObject(new String(data, Charset.forName("UTF-8")), getNodeDataClass()));
                        addNode(nodeEvent);
                    }
                    break;
                case NODE_UPDATED:
                    if (data != null && data.length > 0) {
                        NodeEvent<Data> nodeEvent = (NodeEvent<Data>) NodeEvent.class.getConstructor(NodeOperationType.class, String.class, int.class, NodeData.class).newInstance(NodeOperationType.UPDATED, path, dataVersion, JSON.parseObject(new String(data, Charset.forName("UTF-8")), getNodeDataClass()));
                        updateNode(nodeEvent);
                    }
                    break;
                case NODE_REMOVED:
                    NodeEvent<Data> nodeEvent = (NodeEvent<Data>) NodeEvent.class.getConstructor(NodeOperationType.class, String.class, int.class, NodeData.class).newInstance(NodeOperationType.DELETED, path, dataVersion, JSON.parseObject(new String(data, Charset.forName("UTF-8")), getNodeDataClass()));
                    removeNode(nodeEvent);
                    break;
                default:
                    log.warn("receive node event but not expected type={},don't process!", treeCacheEvent.getType());
                    break;
            }
        } catch (Exception e) {
            log.error("receive node event but process error: ", e);
        }
    }

    protected void addNode(NodeEvent<Data> nodeEvent) {
        nodeHandler.handle(nodeEvent);
    }

    protected void updateNode(NodeEvent<Data> nodeEvent) {
        nodeHandler.handle(nodeEvent);
    }

    protected void removeNode(NodeEvent<Data> nodeEvent) {
        nodeHandler.handle(nodeEvent);
    }

}
