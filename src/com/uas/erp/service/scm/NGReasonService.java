package com.uas.erp.service.scm;

public interface NGReasonService {
	void saveNGReason(String formStore, String gridStore);
	void updateNGReasonById(String formStore, String gridStore);
	void deleteNGReason(int nr_id);
	void printNGReason(int nr_id);
}
