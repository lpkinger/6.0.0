package com.uas.erp.core.web;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.CellStyle;

/**
 * 文档属性
 * 
 * @author yingp
 * 
 */
public class DocumentConfig {

	// 大标题
	private String title;
	private Set<String> fields;
	private Map<String, String> headers;
	private Map<String, Integer> widths;
	private Map<String, String> types;
	private Map<String, String> comments;
	private Map<String, Boolean> locks;
	private Map<String, Boolean> summary;
	private Map<MixedKey, Object> combos;
	private Map<String, CellStyle> styles;
	//是否为必填字段
	private Map<String,Boolean> necessary;  

	public DocumentConfig() {
		headers = new LinkedHashMap<String, String>();
		widths = new HashMap<String, Integer>();
		types = new HashMap<String, String>();
		comments = new HashMap<String, String>();
		locks = new HashMap<String, Boolean>();
		summary = new HashMap<String, Boolean>();
		combos = new HashMap<MixedKey, Object>();
		styles = new HashMap<String, CellStyle>();
		necessary = new HashMap<String,Boolean>();
	}

	public String getTitle() {
		return title;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public Map<String, Integer> getWidths() {
		return widths;
	}

	public Map<String, String> getTypes() {
		return types;
	}

	public Map<String, String> getComments() {
		return comments;
	}
	
	public Map<String, Boolean> getLocks() {
		return locks;
	}

	public Map<String, Boolean> getSummary() {
		return summary;
	}

	public Map<MixedKey, Object> getCombos() {
		return combos;
	}

	public Set<String> getFields() {
		if (fields == null)
			fields = getHeaders().keySet();
		return fields;
	}

	public Map<String, CellStyle> getStyles() {
		return styles;
	}
	
	public Map<String, Boolean> getNecessary() {
		return necessary;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setFields(Set<String> fields) {
		this.fields = fields;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public void setWidths(Map<String, Integer> widths) {
		this.widths = widths;
	}

	public void setTypes(Map<String, String> types) {
		this.types = types;
	}

	public void setComments(Map<String, String> comments) {
		this.comments = comments;
	}
	
	public void setLocks(Map<String, Boolean> locks) {
		this.locks = locks;
	}

	public void setSummary(Map<String, Boolean> summary) {
		this.summary = summary;
	}

	public void setCombos(Map<MixedKey, Object> combos) {
		this.combos = combos;
	}

	public void setStyles(Map<String, CellStyle> styles) {
		this.styles = styles;
	}
	
	public void setNecessary(Map<String, Boolean> necessary) {
		this.necessary = necessary;
	}

	/**
	 * 复合key
	 * 
	 * @author yingp
	 * 
	 */
	public static class MixedKey {

		private Object[] keys;

		public MixedKey(Object... keys) {
			this.keys = keys;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(keys);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MixedKey other = (MixedKey) obj;
			if (!Arrays.equals(keys, other.keys))
				return false;
			return true;
		}

	}

}
