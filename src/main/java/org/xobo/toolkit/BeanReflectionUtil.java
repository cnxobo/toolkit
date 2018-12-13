package org.xobo.toolkit;

import javassist.util.proxy.ProxyObject;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.aop.SpringProxy;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;

public class BeanReflectionUtil {

  /**
   * 加载Clazz及其父类的Fields
   *
   * @param clazz
   * @return
   */
  public static List<Field> loadClassFields(Class<?> clazz) {
    List<Field> fields = new ArrayList<Field>();
    Class<?> currentClazz = clazz;
    while (!currentClazz.equals(Object.class)) {
      fields.addAll(Arrays.asList(currentClazz.getDeclaredFields()));
      currentClazz = currentClazz.getSuperclass();
    }
    return fields;
  }

  public static Class<?> getClass(Object instance) {
    Class<?> clazz = instance.getClass();
    if (instance instanceof ProxyObject) {
      clazz = clazz.getSuperclass();
    } else if (Proxy.isProxyClass(clazz)) {
      return clazz.getSuperclass();
    } else if (hasClass("org.springframework.aop.SpringProxy") && instance instanceof SpringProxy) {
      return clazz.getSuperclass();
    } else if (isCglibProxyClass(clazz)) {
      return clazz.getSuperclass();
    }
    return clazz;
  }

  public static boolean hasClass(String className) {
    try {
      Class.forName(className);
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static Map<String, Object> entityToMap(Object value) {
    Map<String, Object> entity = new LinkedHashMap<String, Object>();
    if (value instanceof Map) {
      entity.putAll((Map<? extends String, ? extends Object>) value);
    } else if (value instanceof Collection) {
      Integer i = 0;
      for (Object obj : (Collection<?>) value) {
        entity.put(i.toString(), entityToMap(obj));
        i++;
      }
    } else if (value != null) {
      Map beanmap = new BeanMap(value);
      entity.putAll(beanmap);
    }
    return entity;
  }

  public static void mergeObjectWithAppointedProperties(Object target, Object source,
    String[] properties) {
    for (String property : properties) {
      try {
        Object targetValue = PropertyUtils.getProperty(target, property);
        Object sourceValue = PropertyUtils.getProperty(source, property);
        if (targetValue == null && sourceValue != null) {
          PropertyUtils.setProperty(target, property, sourceValue);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }


  public static boolean isCglibProxyClass(Class<?> clazz) {
    return (clazz != null && isCglibProxyClassName(clazz.getName()));
  }

  public static boolean isCglibProxyClassName(String className) {
    return (className != null && className.contains(CGLIB_CLASS_SEPARATOR));
  }

  /**
   * The CGLIB class separator character "$$"
   */
  public static final String CGLIB_CLASS_SEPARATOR = "$$";
}
