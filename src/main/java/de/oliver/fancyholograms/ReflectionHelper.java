package de.oliver.fancyholograms;

import java.lang.reflect.Field;

public class ReflectionHelper {

    public static Object getStaticValue(Class clazz, String fieldName){
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object val = field.get(null);
            field.setAccessible(false);
            return val;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Object getValue(Object obj, String fieldName){
        Field field = null;
        try {
            field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object val = field.get(obj);
            field.setAccessible(false);
            return val;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

}
