package com.uas.erp.service.oa;

import java.text.ParseException;
import java.util.List;

import com.uas.erp.model.JSONTree;

public interface BorrowAppService {
	void saveBorrowList(String formStore, String param, String caller) throws ParseException;

	void deleteBorrowList(int bl_id, String caller);

	void updateBorrowListById(String formStore, String param, String caller) throws ParseException;

	void submitBorrowList(int bl_id, String caller);

	void resSubmitBorrowList(int bl_id, String caller);

	void auditBorrowList(int bl_id, String caller);

	void resAuditBorrowList(int bl_id, String caller);

	String vastReturn(String caller, String data);

	void vastRenew(String caller, int[] id);

	List<JSONTree> getJSONModule(String caller);
	
	String OverDue(String caller, String data);
}
