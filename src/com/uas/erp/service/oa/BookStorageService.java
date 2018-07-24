package com.uas.erp.service.oa;

import java.text.ParseException;


public interface BookStorageService {
	void saveBookStorage(String formStore, String param, String  caller)throws ParseException;
	void updateBookStorageById(String formStore, String param, String  caller)throws ParseException;
	void deleteBookStorage(int bs_id, String  caller);
	void submitBookStorage(int bs_id, String  caller);
	void resSubmitBookStorage(int bs_id, String  caller);
	void auditBookStorage(int bs_id, String  caller);
	void resAuditBookStorage(int bs_id, String  caller);

}
