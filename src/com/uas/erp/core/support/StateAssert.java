package com.uas.erp.core.support;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.bind.Status;

/**
 * 对状态进行合法性检查<br>
 * 如果不符合要求，将通过抛出异常的方式中断
 * 
 * @author yingp
 * 
 */
public class StateAssert {

	/**
	 * 修改操作，判断状态是否为在录入
	 */
	public static void updateOnlyEntering(Object actual) {
		Assert.isEquals("common.update_onlyEntering", Status.ENTERING.code(), actual);
	}

	/**
	 * 提交操作，判断状态是否为在录入
	 */
	public static void submitOnlyEntering(Object actual) {
		Assert.isEquals("common.submit_onlyEntering", Status.ENTERING.code(), actual);
	}

	/**
	 * 反提交操作，判断状态是否为已提交
	 */
	public static void resSubmitOnlyCommited(Object actual) {
		Assert.isEquals("common.resSubmit_onlyCommited", Status.COMMITED.code(), actual);
	}

	/**
	 * 审核操作，判断状态是否为已提交
	 */
	public static void auditOnlyCommited(Object actual) {
		Assert.isEquals("common.audit_onlyCommited", Status.COMMITED.code(), actual);
	}

	/**
	 * 反审核操作，判断状态是否为已审核
	 */
	public static void resAuditOnlyAudit(Object actual) {
		Assert.isEquals("common.resAudit_onlyAudit", Status.AUDITED.code(), actual);
	}

	/**
	 * 删除操作，判断状态是否为在录入
	 */
	public static void delOnlyEntering(Object actual) {
		Assert.isEquals("common.delete_onlyEntering", Status.ENTERING.code(), actual);
	}

	/**
	 * 反过账操作，判断状态是否为已过账
	 */
	public static void resPostOnlyPosted(Object actual) {
		Assert.isEquals("common.resPost_onlyPost", Status.POSTED.code(), actual);
	}

	/**
	 * 打印操作，判断状态是否为已审核
	 */
	public static void printOnlyAudited(Object actual) {
		Assert.isEquals("common.print_onlyAudit", Status.AUDITED.code(), actual);
	}

	/**
	 * 确认操作，判断状态是否为已审核
	 */
	public static void confirmOnlyAudited(Object actual) {
		Assert.isEquals("common.confirm_onlyAudit", Status.AUDITED.code(), actual);
	}

	/**
	 * 结案操作，判断状态是否为已审核
	 */
	public static void end_onlyAudited(Object actual) {
		Assert.isEquals("common.end_onlyAudited", Status.AUDITED.code(), actual);
	}

	/**
	 * 反结案操作，判断状态是否为已结案
	 */
	public static void resEnd_onlyAudited(Object actual) {
		Assert.isEquals("common.resEnd_onlyEnd", Status.FINISH.code(), actual);
	}

	/**
	 * 判断上传平台状态是否为已上传
	 * 
	 * @param actual
	 */
	public static void onSendedLimit(Object actual) {
		if ("已上传".equals(actual))
			BaseUtil.showError(BaseUtil.getLocalMessage("common.sended_limit"));
	}

	/**
	 * 判断上传平台状态是否为上传中
	 * 
	 * @param actual
	 */
	public static void onSendingLimit(Object actual) {
		if ("上传中".equals(actual))
			BaseUtil.showError(BaseUtil.getLocalMessage("common.onsend_limit"));
	}
}
