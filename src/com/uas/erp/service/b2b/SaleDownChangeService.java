package com.uas.erp.service.b2b;

public interface SaleDownChangeService {

	void submitSaleDownChange(int id, String caller);

	void resSubmitSaleDownChange(int id, String caller);

	void auditSaleDownChange(int id, String caller);

	/**
	 * 从平台回复的客户变更单，回复信息传到卖家ERP后的处理
	 */
	void onChangeAgreed(String sc_code);

	String confirmSaleDownChange(int id, int agreed, String remark);
	
	void updateSaleDownChange(String caller,String formStore,String gridStore);

}
