package com.uas.erp.service.scm;

public interface SendSampleService {

	String turnProductApproval(int id, String caller);

	void saveSendSample(String formStore, String param, String caller);

	int sendToProdInout(String formStore, String param, String caller);

	int sendToPurInout(String formStore, String param, String caller);

}
