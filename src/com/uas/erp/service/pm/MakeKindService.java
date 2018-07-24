package com.uas.erp.service.pm;


public interface MakeKindService {
	void saveMakeKind(String formStore, String caller);
	void updateMakeKindById(String formStore, String caller);
	void deleteMakeKind(int mk_id, String caller);
}
