package com.uas.erp.service.pm;


public interface MakeFlowService {
	void saveMakeFlow(String formStore, String caller);
	void deleteMakeFlow(int ma_id, String caller);
	String CheckdeleteMakeFlow(String mf_code, String caller);
	void makeMakeFlows(int id,int number,int mfqty, String date,String caller);
	String[] printMakeFlow(int sa_id, String caller,String reportName,String condition);
}
