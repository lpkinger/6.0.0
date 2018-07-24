package com.uas.erp.service.scm;

import com.uas.erp.model.Employee;

public interface ProductService {
	void saveProduct(String formStore, String caller);

	void updateProductById(String formStore, String caller);
	
	void updateProductStatus(int id , String value, String crman, String remark, String date, String caller,String mfile);
	
	void updateProductLevel(int id , String value,String remark, String caller);

	void deleteProduct(int pr_id, String caller);

	void auditProduct(int pr_id, String caller);

	void resAuditProduct(int pr_id, String caller);

	void submitProduct(int pr_id, String caller);

	void resSubmitProduct(int pr_id, String caller);

	/**
	 * 禁用
	 * @param pr_id
	 * @param language
	 * @param employee
	 */
	void bannedProduct(int pr_id, String remark,  String caller);

	/**
	 * 反禁用
	 * @param pr_id
	 * @param language
	 * @param employee
	 */
	void resBannedProduct(int pr_id, String caller);

	String[] postProduct(int[] id, int ma_id_f, int ma_id_t);

	int prodturnsample(int id, String caller);
	void changeStandardPrice(Employee employee, String caller, String data);
	
	String turnTender(Integer id, String caller, String title, String qty);
	
	String getCodePostfix(String caller);
}
