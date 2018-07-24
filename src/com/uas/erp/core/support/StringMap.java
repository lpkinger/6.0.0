package com.uas.erp.core.support;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 针对VALUE为String类型，简化Map集合操作
 * 
 * @author yingp
 *
 */
public class StringMap extends LinkedHashMap<String, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StringMap() {
	}

	public StringMap(String attributeName, String attributeValue) {
		addAttribute(attributeName, attributeValue);
	}

	public StringMap addAttribute(String attributeName, String attributeValue) {
		org.springframework.util.Assert.notNull(attributeName, "Model attribute name must not be null");
		put(attributeName, attributeValue);
		return this;
	}

	public StringMap addAllAttributes(Map<String, String> attributes) {
		if (attributes != null) {
			putAll(attributes);
		}
		return this;
	}

	public StringMap mergeAttributes(Map<String, String> attributes) {
		if (attributes != null) {
			for (String key : attributes.keySet()) {
				if (!containsKey(key)) {
					put(key, attributes.get(key));
				}
			}
		}
		return this;
	}

	public boolean containsAttribute(String attributeName) {
		return containsKey(attributeName);
	}

}
