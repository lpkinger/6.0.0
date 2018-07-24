package com.uas.erp.service.common;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.uas.erp.model.Employee;
import com.uas.erp.model.InitData;
import com.uas.erp.model.InitDetail;
import com.uas.erp.model.InitLog;
import com.uas.erp.model.Initialize;

public interface InitService {
	List<Initialize> getInitTree(int pid);

	List<InitDetail> getInitDetails(String caller);

	List<InitDetail> getInitDetails(String caller, String condition);

	List<InitLog> getInitHistory(String caller);

	List<InitData> getInitDatas(String condition);

	List<InitData> getErrDatas(int id, String condition);

	/**
	 * 清除之前导入的，且未校验通过的数据
	 * 
	 * @param caller
	 */
	void clearBefore(String caller);

	int saveInitData(String caller, List<String> data, Integer ilid);

	void updateInitData(String data);

	void deleteInitData(int id);

	void clearInitData();

	List<Map<String, Object>> getErrInitData(int id);

	List<Map<String, Object>> getInitData(String condition);
	
	List<Map<String, Object>> getDemoData(String caller);

	void deleteErrInitData(int id);

	void beforeCheckLog(int id);

	void afterCheckLog(int id);

	void check(int id);

	InputStream getResult(int id);

	void toFormalData(Employee employee, int id, int start, int end);

	void beforeToFormal(int id);

	void afterToFormal(int id);

	void saveInitDetail(Employee employee, String store);

	/**
	 * 导入配置
	 * 
	 * @param store
	 */
	void importInitDetail(List<InitDetail> details);

	List<Map<String, Object>> sysInitNavigation();

	List<String> getAdminInfo();

	boolean checkData(String table, String value);

	void toDemo(int id,String caller);

	void matchingCode(int id,String caller);
}
