package com.uas.erp.service.ma;

import java.util.List;

import com.uas.erp.model.DataListCombo;
import com.uas.erp.model.Initialize;

public interface SysInitService {
	void saveInitSet(String update,String argType);
	List<DataListCombo> getComboDataByField(String caller, String field);
	List<Initialize> getImportDataItem();
	void InitDataFromStandard(String table);
	void InitHrDataFromStandard();
	void finishInit();
	void InitProcessDataFromStandard();
	void beforeDelete(String status,int keyValue,String table,String statuscode,String keyField);
	void saveBefore(String caller, int keyValue);
	void saveAfter(String caller, int keyValue);
	void finishUcloud();
	/**
	 * wusy
	 * @return
	 */
	void insertReadStatus(String status, int man,String sourcekind);
	boolean getStatus(int man,String em_code);
	List<?> getFieldsDatas(String tablename, String fields,String relfields ,String condition);
}
