package com.uas.erp.core.support;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 有唯一主键的实体
 * 
 * @author yingp
 *
 */
public abstract class KeyEntity {

	/**
	 * 主键值
	 * 
	 * @return
	 */
	@JsonIgnore
	public abstract Object getKey();

}
