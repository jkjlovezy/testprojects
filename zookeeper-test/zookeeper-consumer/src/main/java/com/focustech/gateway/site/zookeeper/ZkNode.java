package com.focustech.gateway.site.zookeeper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZkNode<T> {
    private int version;
    private T data;

}
