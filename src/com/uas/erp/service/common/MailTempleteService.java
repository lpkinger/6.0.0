package com.uas.erp.service.common;

public interface MailTempleteService {

	/**
	 * 保存
	 * @param formStore
	 * @param caller
	 */
	public abstract void saveMailTemplete(String formStore, String caller);

	/**
	 * 删除
	 * @param mtId
	 * @param caller
	 */
	public abstract void deleteMailTempleteById(int mtId, String caller);

	/**
	 * 修改
	 * @param formStore
	 * @param caller
	 */
	public abstract void updateMailTemplete(String formStore, String caller);

	/**
	 * 提交
	 * @param mtId
	 * @param caller
	 */
	public abstract void submitMailTemplete(int mtId, String caller);

	/**
	 * 反提交
	 * @param mtId
	 * @param caller
	 */
	public abstract void resSubmitMailTemplete(int mtId, String caller);

	/**
	 * 审核
	 * @param mtId
	 * @param caller
	 */
	public abstract void auditMailTemplete(int mtId, String caller);

	/**
	 * 反审核
	 * @param mtId
	 * @param caller
	 */
	public abstract void resAuditMailTemplete(int mtId, String caller);

}