package com.test.reflect;

import com.alibaba.fastjson.JSON;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class ClassDeclarationSpy {
    /**
     * args:
     * java ClassDeclarationSpy java.util.concurrent.ConcurrentNavigableMap
     * java ClassDeclarationSpy "[Ljava.lang.String;"
     * java ClassDeclarationSpy java.io.InterruptedIOException
     * java ClassDeclarationSpy java.security.Identity
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            Class<?> c = Class.forName(args[0]);
            System.out.format("Class:%s%n", c.getCanonicalName());
            System.out.format("Modifiers:%s%n", Modifier.toString(c.getModifiers()));

            System.out.format("Type Parameters:%n");
            TypeVariable[] tv = c.getTypeParameters();
            if (tv.length != 0) {
                for (TypeVariable t : tv)
                    out.format("%s %s%n", t.getName(), JSON.toJSONString(t));
                out.format("%n");
            } else {
                out.format(" -- No Type Parameters -- %n%n");
            }

            System.out.format("Implemented Interfaces:%n");
            Type[] intfs = c.getGenericInterfaces();
            if (intfs.length != 0) {
                for (Type t : intfs)
                    out.format("%s%n", t.toString());
                out.format("%n");
            } else {
                out.format("-- No Implemented interfaces -- %n%n");
            }

            out.format("Inheritance Path:%n");
            List<Class> l = new ArrayList<Class>();
            printAncestor(c, l);
            if (l.size() != 0) {
                for (Class<?> cl : l)
                    out.format("%s%n", cl.getCanonicalName());
                out.format("%n");
            } else {
                out.format("-- No Super Classes -- %n%n");
            }

            out.format("Annotations:%n");
            Annotation[] ann = c.getAnnotations();
            if (ann.length != 0) {
                for (Annotation a : ann)
                    out.format("%s%n", a.toString());
                out.format("%n");
            } else {
                out.format("-- No Annotations --%n%n");
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void printAncestor(Class c, List<Class> l) {
        Class ancestor = c.getSuperclass();
        if (ancestor != null) {
            l.add(ancestor);
            printAncestor(ancestor, l);
        }
    }
}
