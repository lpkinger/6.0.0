package com.uas.erp.service.pm;

import java.util.Map;


public interface StepioService {
	void saveStepio(String formStore, String  caller,String param);
	void updateStepioById(String formStore, String  caller ,String param, String param2);
	void deleteStepio(int bo_id, String  caller);
	void submitStepio(int bo_id, String  caller);
	void resSubmitStepio(int bo_id, String  caller);
	Map<String, Object> getClashInfo(String caller,String con);
	void postStepIO(int id, String caller);
	void resPostStepIO(int id, String caller);
	void setclash(int id, String  caller);
	void saveclash(String caller,String data, int id,int clashqty) ;
	void batchSumbitStepio(String datas, String caller);
	void batchPostStepio(String datas, String caller);
}
