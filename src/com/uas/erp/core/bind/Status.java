package com.uas.erp.core.bind;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;

/**
 * 系统业务单据状态
 * 
 * @author yingp
 * 
 */
public enum Status {

	/**
	 * 在录入
	 */
	ENTERING,
	/**
	 * 已提交
	 */
	COMMITED,
	/**
	 * 已审核
	 */
	AUDITED,
	/**
	 * 已冻结
	 */
	FREEZE,
	/**
	 * 已结案
	 */
	FINISH,
	/**
	 * 已作废
	 */
	NULLIFIED,
	/**
	 * 已过账
	 */
	POSTED,
	/**
	 * 未记账
	 */
	UNPOST,
	/**
	 * 已打印
	 */
	PRINTED,
	/**
	 * 未打印
	 */
	UNPRINT,
	/**
	 * 已回复
	 */
	REPLYED,
	/**
	 * 未回复
	 */
	UNREPLY,
	/**
	 * 未完工
	 */
	UNCOMPLET,
	/**
	 * 挂起
	 */
	HUNG,
	/**
	 * 已禁用
	 */
	DISABLE,
	/**
	 * 已批准
	 */
	APPROVE,
	/**
	 * 未批准
	 */
	UNAPPROVED,
	/**
	 * 已入库
	 */
	TURNIN,
	/**
	 * 已出库
	 */
	TURNOUT,
	/**
	 * 已转采购
	 */
	TURNPURC,
	/**
	 * 已转通知单
	 */
	TURNSN,
	/**
	 * 待确认
	 */
	TO_CONFIRM,
	/**
	 * 已确认
	 */
	CONFIRMED;

	private final String code;
	private final String display;

	private Status() {
		this.code = this.toString();
		this.display = this.code;
	}

	private Status(String code) {
		this.code = code;
		this.display = code;
	}

	private Status(String code, String display) {
		this.code = code;
		this.display = display;
	}

	/**
	 * @return 状态的编码
	 */
	public String code() {
		return this.code;
	}

	/**
	 * @param language
	 *            语言
	 * @return 状态的描述
	 */
	public String display(String language) {
		return BaseUtil.getLocalMessage(this.display, language);
	}

	/**
	 * @return 状态的描述
	 */
	public String display() {
		return display(SystemSession.getLang());
	}

	public boolean equals(Status status) {
		return this.code.equals(status.code());
	}

	public boolean equals(String code) {
		return this.code.equals(code);
	}

}
