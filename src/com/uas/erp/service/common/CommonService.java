package com.uas.erp.service.common;

import java.util.Map;

public interface CommonService {
	int saveCommon(String caller, String formStore, String gridStore);

	void updateCommonById(String caller, String formStore, String gridStore);

	void deleteCommon(String caller, int id);

	void printCommon(String caller, int id);

	void auditCommon(int id,String caller);

	void resAuditCommon(String caller, int id);

	void submitCommon(String caller, int id);

	void resSubmitCommon(String caller, int id);

	void bannedCommon(String caller, int id);

	void resBannedCommon(String caller, int id);

	void endCommon(String caller, int id);

	void resEndCommon(String caller, int id);

	int getId(String caller);

	int getSequenceid(String seqname);

	void postCommon(String caller, int id);

	void confirmCommon(String caller, int id);

	int getCountByTable(String condition, String tablename);

	String[] printCommon(int id, String caller, String reportName, String condition);
	
	void modify(String caller, String formStore);

	void modifyDetail(String caller,String param,String log);

	void openSysConfirmCommon(String caller,int id,String confirmres,String confirmdesc);
	
	Map<String, Object> getPrintSet();
	
	Map<String, Object> getButtonconfigs(String caller);

	String turnAllCommon(String caller, int id, String name);

	Object turnCommon(String caller, String data);
	
	Map<String,Object> getqueryConfigs(String caller,String xtype);

	Map<String, Object> getBankName(String condition, Integer start, Integer end);

	void abate(Integer id, String caller, String remark);

	void resAbate(Integer id, String caller, String remark);
}
