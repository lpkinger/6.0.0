package com.uas.erp.service.scm;

public interface SampleService {
	void saveSample(String formStore, String gridStore, String caller);
	void updateSampleById(String formStore, String gridStore, String caller);
	void deleteSample(int sa_id, String caller);
	void printSample(int sa_id, String caller);
	int turnSendSample(String caller, int id);
}
