package com.test.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericSuperclassTest<Boolean> {

    void test(){
        Type t = this.getClass().getGenericSuperclass();
        ParameterizedType p = (ParameterizedType) t ;
        Class<String> c  = (Class<String>)p.getActualTypeArguments()[0];
        System.out.println(c );    //java.lang.String
        System.out.println(t);
    }

    public static void main(String[] args) {
        GenericSuperclassTest instance = new GenericSuperclassTest();
        instance.test();
    }
}
