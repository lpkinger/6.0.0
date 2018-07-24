package com.uas.erp.service.plm;

public interface BatchDealService {
	
	String makeDeal(String data);// 试产制造单批量结案
	void salevastClose(String data);
	String batchTestBug(String caller,String data);//批量测试BUG

}
