package com.uas.erp.dao.common;

import java.io.InputStream;
import java.util.List;

import com.uas.erp.model.InitData;
import com.uas.erp.model.InitDetail;
import com.uas.erp.model.InitLog;
import com.uas.erp.model.InitNodes;
import com.uas.erp.model.InitToFormal;
import com.uas.erp.model.Initialize;

public interface InitDao {
	List<Initialize> getInitializes(int pid);

	List<InitDetail> getInitDetails(String caller);

	List<InitDetail> getInitDetailsByCondition(String caller, String condition);

	List<InitLog> getInitHistory(String caller);

	List<InitData> getInitDatas(String condition);

	void save(List<InitData> datas);

	void saveErrorMsg(final int id, final String errors);

	void logNodes(List<InitNodes> nodes);

	/**
	 * 获取校验结果<br>
	 * 由于结果较长，用字符串String恐出现异常，直接以流形式传回到前台
	 * 
	 * @param il_id
	 * @return
	 */
	InputStream getResult(int il_id);

	String toFormalData(String keyField, String tableName, List<InitToFormal> datas);
}
