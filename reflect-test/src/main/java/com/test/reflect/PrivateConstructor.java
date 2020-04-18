package com.test.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class PrivateConstructor {
    public static void main(String[] args) {
        try {
            Class<?> c = Class.forName("com.test.reflect.Cls");
            try {
                Constructor constructor = c.getDeclaredConstructor();
                constructor.setAccessible(true);
                constructor.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
class Cls{
    private Cls(){}
}
