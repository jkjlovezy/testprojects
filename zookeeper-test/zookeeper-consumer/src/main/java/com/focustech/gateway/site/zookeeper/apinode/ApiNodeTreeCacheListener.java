package com.focustech.gateway.site.zookeeper.apinode;

import com.focustech.gateway.site.zookeeper.core.BaseTreeCacheListener;

public class ApiNodeTreeCacheListener extends BaseTreeCacheListener<ApiNodeHandler, ApiNodeData> {
    public ApiNodeTreeCacheListener(ApiNodeHandler nodeHandler) {
        super(nodeHandler);
    }

}
