package com.uas.erp.service.scm;

public interface ProductBaseService {
	void saveProductBase(String formStore, String caller);

	void updateProductBaseById(String formStore, String caller);

	void deleteProductBase(int pr_id, String caller);

	void auditProductBase(int pr_id, String caller);

	void resAuditProductBase(int pr_id, String caller);

	void submitProductBase(int pr_id, String caller);

	void resSubmitProductBase(int pr_id, String caller);

	/**
	 * 禁用
	 * 
	 * @param pr_id
	 * @param language
	 * @param employee
	 */
	void bannedProduct(int pr_id, String caller);

	/**
	 * 反禁用
	 * 
	 * @param pr_id
	 * @param language
	 * @param employee
	 */
	void resBannedProduct(int pr_id, String caller);

	/**
	 * 物料复制
	 */
	int copyProduct(int pr_id, String caller, String newcode, String newname, String newspec );

	void SubmitStandard(int id, String caller);

	void resSubmitNoStandard(int id, String caller);
	void updateStandard(int id ,String caller);
	/**
	 * 客户物料维护
	 * 
	 * @param param
	 * @param language
	 * @param employee
	 */
	void saveCustprod(String param, String caller);
}
