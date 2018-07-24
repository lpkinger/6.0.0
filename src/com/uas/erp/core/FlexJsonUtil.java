package com.uas.erp.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * <h1>flexjson</h1><br>
 * 1.序列化json字符串时较快，优于jackson<br>
 * 2.但是数据量较大时，序列化有问题<br>
 * 3.反序列化较慢
 * 
 * @author yingp
 * 
 */
public class FlexJsonUtil {

	public static <T> T fromJson(String json, Class<?> cls) {
		return new JSONDeserializer<T>().use(null, cls).deserialize(json);
	}

	public static <K, V> Map<K, V> fromJson(String json) {
		if (json != null) {
			Map<K, V> map = new HashMap<K, V>();
			return new JSONDeserializer<Map<K, V>>().use(null, map.getClass()).deserialize(json);
		}
		return null;
	}

	public String toJson() {
		return new JSONSerializer().exclude("*.class").serialize(this);
	}

	public static String toJson(Object obj) {
		if (obj == null)
			return null;
		return new JSONSerializer().exclude("*.class").serialize(obj);
	}

	public static String toJsonDeep(Object obj) {
		if (obj == null)
			return null;
		return new JSONSerializer().exclude("*.class").deepSerialize(obj);
	}

	public static <T> String toJsonArray(Collection<?> collection) {
		return new JSONSerializer().exclude("*.class").serialize(collection);
	}

	public static <T> String toJsonArrayDeep(Collection<?> collection) {
		return new JSONSerializer().exclude("*.class").deepSerialize(collection);
	}

	public static <T> List<T> fromJsonArray(String json, Class<?> cls) {
		return new JSONDeserializer<List<T>>().use(null, ArrayList.class).use("values", cls).deserialize(json);
	}
}
