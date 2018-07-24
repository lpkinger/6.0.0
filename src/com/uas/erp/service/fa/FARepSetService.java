package com.uas.erp.service.fa;

import net.sf.json.JSONObject;

import com.uas.erp.model.FARepSet;

public interface FARepSetService {
	void saveFARepSet(String formStore, String gridStore, String caller);

	void updateFARepSetById(String formStore, String gridStore, String caller);

	void deleteFARepSet(int fs_id, String caller);

	void auditFARepSet(int fs_id, String caller);

	void resAuditFARepSet(int fs_id, String caller);

	void submitFARepSet(int fs_id, String caller);

	void resSubmitFARepSet(int fs_id, String caller);

	/**
	 * 查找
	 * 
	 * @param fs_id
	 * @return
	 */
	FARepSet getFARepSet(int fs_id);

	/**
	 * 保存
	 * 
	 * @param repSet
	 */
	void saveFARepSet(FARepSet repSet);

	JSONObject copyFARepSet(int id, String caller);

}
