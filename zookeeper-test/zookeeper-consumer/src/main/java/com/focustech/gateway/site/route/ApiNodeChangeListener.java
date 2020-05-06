package com.focustech.gateway.site.route;

import com.focustech.gateway.site.route.data.ApiNodeData;
import com.focustech.gateway.site.zookeeper.core.NodeChangeListener;

public class ApiNodeChangeListener extends NodeChangeListener<ApiNodeChangeHandler, ApiNodeData> {
    public ApiNodeChangeListener(ApiNodeChangeHandler nodeHandler, String rootPath) {
        super(nodeHandler, rootPath);
    }

}
