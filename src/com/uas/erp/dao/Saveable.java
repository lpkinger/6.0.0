package com.uas.erp.dao;

/**
 * could be inserted into database
 * @author yingp
 * @date  2012-7-23 22:55:34
 */
public interface Saveable {

	public String table();
	//key field
	public String[] keyColumns();
}
