package com.uas.erp.service.oa;

import java.util.List;

import com.uas.erp.model.SendOfficialDocument;

public interface SendODMService {
	void saveSOD(String formStore, String  caller);

	void deleteSOD(int id, String  caller);

	void updateSODById(String formStore, String  caller);
	
	void submitDraft(int id, String  caller);
	
	void resSubmitDraft(int id, String  caller);
	
	void auditDraft(int id, String  caller);
	
	void resAuditDraft(int id, String  caller);
	
	void save(int rid, int sid, String  caller);

	SendOfficialDocument getSODById(int id, String  caller);
	
    void deleteById(int sod_id);
	
	List<SendOfficialDocument> getList(int page, int pageSize);
	
	int getListCount();
	
	List<SendOfficialDocument> getByCondition(String condition, int page, int pageSize);
	
	int getSearchCount(String condition);

}
