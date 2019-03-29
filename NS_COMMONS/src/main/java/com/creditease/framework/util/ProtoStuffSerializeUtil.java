package com.creditease.framework.util;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;


public class ProtoStuffSerializeUtil {
	@Deprecated
	public static <T> byte[] serialize(T t,Class<T> cl)
	{
		Schema<T> schema = RuntimeSchema.getSchema(cl);
		
		LinkedBuffer linkedBuffer = LinkedBuffer.allocate(1024);
		
		return	ProtostuffIOUtil.toByteArray(t, schema, linkedBuffer);
		
	}
	@Deprecated
	public static  <T> byte[]  serialize(List<T> lst,Class<T> cl) throws IOException
	{
		if (lst == null) 
		{
			return new byte[0];
		}
		
		
		Schema<T> schema = RuntimeSchema.getSchema(cl);
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		
		LinkedBuffer linkedBuffer = LinkedBuffer.allocate(1024);
		
		ProtostuffIOUtil.writeListTo(arrayOutputStream, lst, schema, linkedBuffer);
		
		byte[] bs = arrayOutputStream.toByteArray();
		arrayOutputStream.close();
		return bs;
	}
	
	/**
	 * 解析所有的类 不区分集合和普通对象
	 * 由于protostuff对一个普通pojo支持的最好，所以这里采用一个wrapper把对象包裹起来使用
	 * @param o
	 * @return
	 * @throws IOException
	 */
	
	public static   byte[]  serializeForCommon(Object o) throws IOException
	{
		
		ObjectWrapper objectWrapper = new ObjectWrapper();
		objectWrapper.setWrappedStuff(o);
		
		Schema<ObjectWrapper> schema = RuntimeSchema.getSchema(ObjectWrapper.class);
		LinkedBuffer linkedBuffer = LinkedBuffer.allocate(1024);
		return ProtostuffIOUtil.toByteArray(objectWrapper, schema, linkedBuffer);
	}
	
	@Deprecated
	public static <T>  List<T> unSerializeForList(byte[] bs,Class<T> cl) throws IOException
	{
		ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bs);
		Schema<T> schema = RuntimeSchema.getSchema(cl);
		List<T> lst	 = ProtostuffIOUtil.parseListFrom(arrayInputStream,schema);
		arrayInputStream.close();
		return lst;
	}
	
	@Deprecated
	public static <T>  T unSerialize(byte[] bs,Class<T> cl) throws InstantiationException, IllegalAccessException
	{
		Constructor<T>[] constructors = (Constructor<T>[])cl.getConstructors();
		
		T	t = cl.newInstance();
		return unSerialize(bs, cl,t);
	}

	@Deprecated
	public static <T>  T unSerialize(byte[] bs,Class<T> cl,T t) throws InstantiationException, IllegalAccessException
	{
		Schema<T> schema = RuntimeSchema.getSchema(cl);
		ProtostuffIOUtil.mergeFrom(bs, t, schema);
		return t;
	}
	
	/**
	 * 这里只解析使用serrializeforcommon方法所序列化的对象，不支持这个工具类中的其他方法序列化的字节流
	 * @param bs
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static Object unSerializeForCommon(byte[] bs) throws InstantiationException, IllegalAccessException
	{
		Schema<ObjectWrapper>  schema = RuntimeSchema.getSchema(ObjectWrapper.class);
		ObjectWrapper objectWrapper = new ObjectWrapper();
		ProtostuffIOUtil.mergeFrom(bs, objectWrapper, schema);
		return objectWrapper.getWrappedStuff();
	}
	
	static class ObjectWrapper
	{
		private Object wrappedStuff;

		public Object getWrappedStuff() {
			return wrappedStuff;
		}

		public void setWrappedStuff(Object wrappedStuff) {
			this.wrappedStuff = wrappedStuff;
		}
		
		
	}
}
