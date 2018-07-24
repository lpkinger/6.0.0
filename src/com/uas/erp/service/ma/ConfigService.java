package com.uas.erp.service.ma;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import com.uas.erp.model.Configs;

public interface ConfigService {

	/**
	 * 配置参数
	 * 
	 * @param caller
	 * @return
	 */
	List<Configs> getConfigsByCaller(String caller, HttpSession session);

	/**
	 * 修改配置参数
	 * 
	 * @param updated
	 */
	void saveConfigs(List<Map<Object, Object>> updated);

	/**
	 * 取配置参数的值
	 * 
	 * @param caller
	 * @param code
	 *            编号
	 * @return
	 */
	JSONObject getConfigByCallerAndCode(String caller, String code);

	Configs getConfig(String caller, String code);

	List<Configs> getConfigsByCondition(String condition);

	void beforeSave(String updated);

	void afterSave(String updated);
}
