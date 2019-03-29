package com.creditease.framework.util;

import java.lang.reflect.Type;

import com.creditease.framework.pojo.ServiceMessage;
import com.creditease.framework.scope.ExchangeKey;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;



public class JsonUtil {
	
	public static final ObjectMapper mapper = new ObjectMapper();
	static{
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		mapper.setVisibility(PropertyAccessor.GETTER, Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.IS_GETTER, Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.SETTER, Visibility.NONE);
		
		mapper.setVisibility(PropertyAccessor.CREATOR, Visibility.PUBLIC_ONLY);
		mapper.setVisibility(PropertyAccessor.FIELD,Visibility.ANY);
	}

	public static String jsonFromObject(Object object) throws Exception{
		return mapper.writeValueAsString(object);
	}
	
	
	public static String jsonFromObject(Object object,Type genericType) throws Exception{
		return mapper.writer().withType(mapper.constructType(genericType)).writeValueAsString(object);
	}
	
	/**
	 * 适用于没有泛型的情况
	 * @param <T>
	 * @param json
	 * @param klass
	 * @return
	 * @throws Exception
	 */
	public static <T> T objectFromJson(String json, Class<T> klass) throws Exception{
		T object;
		object = mapper.readValue(json, klass);
		return object;
	}
	
	/**
	 * 适用于已知的泛型，比如HashMap<String,Long>
	 * 具体用法：
	 *    HashMap<String,Long> map = (HashMap<String,Long>)objectFromJson(jsonStr,new TypeReference<HashMap<String,Long>>(){});
	 * @param <T>
	 * @param json
	 * @param klass
	 * @return
	 * @throws Exception
	 */
	public static <T> T objectFromJson(String json, TypeReference<T> klass) throws Exception{
		T object;
		object = mapper.readValue(json, klass);
		return object;
	}
	
	/**
	 * 适用于未知的泛型，比如反射某个属性，无法知道确切的泛型类型
	 * 具体用法：
	 * 		Field f = ....;
	 * 		Object o = objectFromJson(jsonStr,f.getGenericType());
	 * 		
	 * @param <T>
	 * @param json
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static <T> T objectFromJson(String json, Type type) throws Exception{
		T object;
		object = mapper.readValue(json, mapper.constructType(type));
		return object;
	}
	
	public static Object convertToSpecialedType(Object o, Class t) throws Exception
	{
		 return objectFromJson( jsonFromObject(o),t);
	}
	
	public static <T> T convertToTypeReferenceType(Object o, TypeReference<T> t) throws Exception
	{
		 return  objectFromJson(  jsonFromObject(o),t);
	}
	
	public static <T> T convertToTypeReferenceType(ServiceMessage serviceMessage, ExchangeKey key, TypeReference<T> t) throws Exception
	{
		Object o = serviceMessage.getExchangeByType(key, Object.class);
		return convertToTypeReferenceType(o, t);
	}
	
	
	public static <T> T convertToTypeReferenceType(Object o,Class<T> clazz) throws Exception
	{
		 return  objectFromJson( jsonFromObject(o),new TypeReference<T>() {
		});
	}
	
}
