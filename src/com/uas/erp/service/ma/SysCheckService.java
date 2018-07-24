package com.uas.erp.service.ma;

import java.util.List;

import com.uas.erp.model.JSONTree;

public interface SysCheckService {
	void saveSysCheckFormula(String formStore);

	void deleteSysCheckFormula(int id);

	void updateSysCheckFormula(String formStore);

	List<JSONTree> getAllHrTree();

	String getDataByOrg(int orgid, String type);

	void TurnReandpunish(String data);

	void RunCheck();

	String getTreeData(String condition);

	void vastUpdateSysCheckFormula(String data);
}
