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
public class BaseTreeCacheListener<Handler extends NodeHandler, Data extends NodeData> implements TreeCacheListener {
    protected NodeHandler nodeHandler;

    public BaseTreeCacheListener(Handler nodeHandler) {
        this.nodeHandler = nodeHandler;
    }

    private Class getEntityClass() {
        Type t = this.getClass().getGenericSuperclass();
        return (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
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
            log.debug("receive node event: type={},path={},dataVersion={},data={}", treeCacheEvent.getType(), path, dataVersion, new String(data, Charset.forName("UTF-8")));
            switch (treeCacheEvent.getType()) {
                case NODE_ADDED:
                    if (data != null && data.length > 0) {
                        NodeEvent<Data> nodeEvent = (NodeEvent<Data>) NodeEvent.class.getConstructor(NodeOperationType.class, String.class, int.class, NodeData.class).newInstance(NodeOperationType.ADDED, path, dataVersion, JSON.parseObject(new String(data, Charset.forName("UTF-8")), getEntityClass()));
                        addNode(nodeEvent);
                    }
                    break;
                case NODE_UPDATED:
                    if (data != null && data.length > 0) {
                        NodeEvent<Data> nodeEvent = (NodeEvent<Data>) NodeEvent.class.getConstructor(NodeOperationType.class, String.class, int.class, NodeData.class).newInstance(NodeOperationType.UPDATED, path, dataVersion, JSON.parseObject(new String(data, Charset.forName("UTF-8")), getEntityClass()));
                        updateNode(nodeEvent);
                    }
                    break;
                case NODE_REMOVED:
                    NodeEvent<Data> nodeEvent = (NodeEvent<Data>) NodeEvent.class.getConstructor(NodeOperationType.class, String.class, int.class, NodeData.class).newInstance(NodeOperationType.REMOVED, path, dataVersion, null);
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
