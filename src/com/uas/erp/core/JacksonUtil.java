package com.uas.erp.core;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * <h1>jackson</h1><br>
 * 建议反序列化采用和序列化同一util，不要jackson和flexjson混着用，否则消耗会加大很多
 * 
 * @author yingp
 * 
 */
public class JacksonUtil {
	@SuppressWarnings("unchecked")
	public static <T> List<T> fromJsonArray(String json) {
		ObjectMapper mapper = getObjectMapper();
		try {
			return mapper.readValue(json, List.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 字段必须是有引号的，形如{"a":"1","b":2}<br>
	 * 如果格式是{a:"1",b:2}是无法解析的
	 * 
	 * @param json
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> HashMap<K, V> fromJson(String json) {
		ObjectMapper mapper = getObjectMapper();
		try {
			return mapper.readValue(json, HashMap.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String toJson(Map<?, ?> map) {
		ObjectMapper mapper = getObjectMapper();
		try {
			return mapper.writeValueAsString(map);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String toJsonArray(Collection<?> collection) {
		ObjectMapper mapper = getObjectMapper();
		try {
			return mapper.writeValueAsString(collection);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static ObjectMapper getObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		return mapper;
	}

}
