package com.test.constants;

public enum UserStatusEnum {
    DISABLE(1, "未启用"), ENABLE(3, "已启用");
    private int code;
    private String desc;

    private UserStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String toString(){
        return this.code+"";
    }


}
