package com.uas.erp.core.bind;

import com.uas.erp.core.BaseUtil;

/**
 * 系统常用操作
 * 
 * @author yingp
 * 
 */
public enum Operation {

	/**
	 * 新增
	 */
	SAVE("msg.save", "msg.saveSuccess", Status.ENTERING),
	/**
	 * 修改
	 */
	UPDATE("msg.update", "msg.updateSuccess", Status.ENTERING),
	/**
	 * 提交
	 */
	COMMIT("msg.commit", "msg.commitSuccess", Status.COMMITED),
	/**
	 * 反提交
	 */
	RESCOMMIT("msg.resCommit", "msg.resCommitSuccess", Status.ENTERING),
	/**
	 * 审核
	 */
	AUDIT("msg.audit", "msg.auditSuccess", Status.AUDITED),
	/**
	 * 反审核
	 */
	RESAUDIT("msg.resAudit", "msg.resAuditSuccess", Status.ENTERING),
	/**
	 * 记账
	 */
	POST("msg.post", "msg.postSuccess", Status.POSTED),
	/**
	 * 反记账
	 */
	RESPOST("msg.resPost", "msg.resPostSuccess", Status.UNPOST),
	/**
	 * 打印
	 */
	PRINT("msg.print", "msg.printSuccess", Status.PRINTED),
	/**
	 * 冻结
	 */
	FREEZE("msg.freeze", "msg.freezeSuccess", Status.FREEZE),
	/**
	 * 取消冻结
	 */
	RESFREEZE("msg.resFreeze", "msg.resFreezeSuccess", Status.ENTERING),
	/**
	 * 重启
	 */
	RESSTART("msg.resStart", "msg.resStartSuccess", Status.AUDITED),
	/**
	 * 结案
	 */
	FINISH("msg.close", "msg.closeSuccess", Status.FINISH),
	/**
	 * 取消结案=>在录入
	 */
	RESFINISH("msg.resClose", "msg.resCloseSuccess", Status.ENTERING),
	/**
	 * 作废
	 */
	NULLIFY("msg.nullify", "msg.nullifySuccess", Status.NULLIFIED),
	/**
	 * 删除
	 */
	DELETE("msg.delete", "msg.deleteSuccess"),
	/**
	 * 结案操作
	 */
	CLOSE("msg.close", "msg.closeSuccess", Status.FINISH),
	/**
	 * 反结案=>已审核
	 */
	RESCLOSE("msg.resClose", "msg.resCloseSuccess", Status.AUDITED),
	/**
	 * 禁用操作
	 */
	BANNED("msg.banned", "msg.bannedSuccess", Status.DISABLE),
	/**
	 * 反禁用
	 */
	RESBANNED("msg.resBanned", "msg.resBannedSuccess", Status.ENTERING),
	/**
	 * 批准操作
	 */
	APPROVE("msg.approve", "msg.approveSuccess", Status.APPROVE),
	/**
	 * 反批准
	 */
	RESAPPROVE("msg.resApprove", "msg.resApproveSuccess", Status.UNAPPROVED);

	private final String title;
	private final String result;
	private final Status resultStatus;

	private Operation(String title, String result) {
		this.title = title;
		this.result = result;
		this.resultStatus = null;
	}

	private Operation(String title, String result, Status resultStatus) {
		this.title = title;
		this.result = result;
		this.resultStatus = resultStatus;
	}
	
	/**
	 * 操作描述
	 * 
	 * @param language
	 * @return
	 */
	public String getTitle() {
		return getTitle(null);
	}

	/**
	 * 操作描述
	 * 
	 * @param language
	 * @return
	 */
	public String getTitle(String language) {
		return BaseUtil.getLocalMessage(this.title, language);
	}
	
	/**
	 * 操作结果
	 * 
	 * @param language
	 * @return
	 */
	public String getResult() {
		return getResult(null);
	}

	/**
	 * 操作结果
	 * 
	 * @param language
	 * @return
	 */
	public String getResult(String language) {
		return BaseUtil.getLocalMessage(this.result, language);
	}

	/**
	 * 结果状态
	 * 
	 * @param language
	 * @return
	 */
	public Status getResultStatus() {
		return this.resultStatus;
	}
}
