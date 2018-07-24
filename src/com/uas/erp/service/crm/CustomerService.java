package com.uas.erp.service.crm;



public interface CustomerService {
	void saveCustomer(String formStore, String param,String caller);
	void deleteCustomer(int cu_id,String caller);
	void updateCustomerById(String formStore,String param,String caller);
	void submitCustomer(int cu_id,String caller);
	void resSubmitCustomer(int cu_id,String caller);
	void auditCustomer(int cu_id,String caller);
	void resAuditCustomer(int cu_id,String caller);
	/**
	 * 根据客户资料的UU号获取业务员编号
	 * @param customerUU
	 * @return
	 */
	String getSallerCodeByCustomerUU(Long customerUU);
	/**
	 * 根据客户资料的UU号获取客户名称
	 * @param customerUU
	 * @return
	 */
	String getNameByCustomerUU(Long customerUU);
	
	void checkCustomerUU(String data);
}
