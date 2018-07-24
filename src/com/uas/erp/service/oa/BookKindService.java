package com.uas.erp.service.oa;


public interface BookKindService {
	void saveBookKind(String formStore, String  caller);
	void deleteBookKind(int bk_id, String  caller);
	void updateBookKindById(String formStore, String  caller);
}
