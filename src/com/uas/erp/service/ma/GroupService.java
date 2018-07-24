package com.uas.erp.service.ma;

public interface GroupService {
	/**
	 * 更新帐套设置
	 * 
	 * @param data
	 */
	void updateBaseDataSet(String data);

	/**
	 * 更新同步设置
	 * @param data
	 */
	void updatePostStyleSet(String data);
}
