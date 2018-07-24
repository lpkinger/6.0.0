package com.uas.erp.dao.common;

import java.util.Map;

public interface SettingDao {

	/**
	 * 出入库单类型IN、OUT
	 * 
	 * @param caller
	 * @return
	 */
	Map<String, String> getInOutTypes();

}
