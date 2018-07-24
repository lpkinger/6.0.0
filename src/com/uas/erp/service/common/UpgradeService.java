package com.uas.erp.service.common;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.SysUpdates;

public interface UpgradeService {

	/**
	 * 从服务接口查找更新程序信息
	 */
	Map<String, Object> getUpgradePlans(Integer page, Integer limit, String filter);

	/**
	 * 更新程序
	 * 
	 * @param planId
	 *            方案ID
	 * @param type
	 *            升级类型
	 * @param version
	 *            版本
	 */
	boolean upgrade(String planId, String type, int version);

	/**
	 * 程序更新日志
	 * 
	 * @param planIds
	 * @return
	 */
	List<SysUpdates> getUpgradeLog(String[] planIds);

}
