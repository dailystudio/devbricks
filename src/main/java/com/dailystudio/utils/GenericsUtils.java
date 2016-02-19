package com.dailystudio.utils;

import java.lang.reflect.ParameterizedType;  
import java.lang.reflect.Type;  
  
public class GenericsUtils {  
 
	public static Class<?> getSuperClassGenricType(Class<?> klass) {  
        return getSuperClassGenricType(klass, 0);  
    }  
  
	public static Class<?> getSuperClassGenricType(Class<?> klass, int index) throws IndexOutOfBoundsException {
        Type genType = klass.getGenericSuperclass(); 

        if (!(genType instanceof ParameterizedType)) {  
            return Object.class;  
        }  
  
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();  
        
        if (index >= params.length || index < 0) {  
            return Object.class;  
        }  
        
        if (!(params[index] instanceof Class<?>)) {  
            return Object.class;  
        }
        
        return (Class<?>) params[index];  
    }  
	
}  
