package com.onedream.wedoxdb.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jdallen
 * @since 2020/9/1
 */
public class DBUtils {
    private final static String[] typeOfVarchar = {"String"};
    private final static String[] typeOfInteger = {"Integer", "int"};
    private final static String[] typeOfFloat = {"Float", "float"};
    private final static String[] typeOfLong = {"Long", "long"};

    //判断某个变量的注解类型是不是传入的注解类型
    public static boolean isAnnotationPresentsInField(Field field, String annotation) {
        Annotation[] ants = field.getAnnotations();
        for (Annotation a : ants) {
            if (annotation.equals(a.annotationType().getSimpleName())) {
                return true;
            }
        }
        return false;
    }

    //将基本数据类型转换为与其对应的数据库的数据类型
    public static String getTypeName(String className) {
        if (classNameIsIn(className, typeOfVarchar)) {
            return "varchar";
        }
        if (classNameIsIn(className, typeOfInteger)) {
            return "integer default 0";
        }
        if (classNameIsIn(className, typeOfFloat)) {
            return "float";
        }
        if (classNameIsIn(className, typeOfLong)) {
            return "long ";
        }
        return null;
    }

    private static boolean classNameIsIn(String className, String[] type) {
        for (String s : type) {
            if (s.equalsIgnoreCase(className)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是不是基本类型
     *
     * @param typeClass
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static boolean isBasicType(Class typeClass) {
        if (typeClass.equals(Integer.class) || typeClass.equals(Long.class)
                || typeClass.equals(Float.class)
                || typeClass.equals(Double.class)
                || typeClass.equals(Boolean.class)
                || typeClass.equals(Byte.class)
                || typeClass.equals(Short.class)
                || typeClass.equals(String.class)) {

            return true;

        } else {
            return false;
        }
    }

    /**
     * 获得包装类
     *
     * @param typeClass
     * @return
     */
    @SuppressWarnings("all")
    public static Class<? extends Object> getBasicClass(Class typeClass) {
        Class _class = basicMap.get(typeClass);
        if (_class == null)
            _class = typeClass;
        return _class;
    }

    @SuppressWarnings("rawtypes")
    private static Map<Class, Class> basicMap = new HashMap<Class, Class>();

    static {
        basicMap.put(int.class, Integer.class);
        basicMap.put(long.class, Long.class);
        basicMap.put(float.class, Float.class);
        basicMap.put(double.class, Double.class);
        basicMap.put(boolean.class, Boolean.class);
        basicMap.put(byte.class, Byte.class);
        basicMap.put(short.class, Short.class);
    }
}
