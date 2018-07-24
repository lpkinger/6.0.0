package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.dao.Saveable;

/**
 * 个人权限表
 * 
 * @author yingp
 * @date 2013-5-24 8:47:06
 */
public class PersonalPower implements Saveable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String SEE = "pp_see";
	public static final String SEE_OTHER = "pp_alllist";
	public static final String SELF_LIST = "pp_selflist";
	public static final String ALL_LIST = "pp_alllist";
	public static final String ADD = "pp_add";
	public static final String SAVE = "pp_save";
	public static final String SAVE_OTHER = "pp_saveoth";
	public static final String DELETE = "pp_delete";
	public static final String DELETE_OTHER = "pp_saveoth";
	public static final String SUBMIT = "pp_commit";
	public static final String SUBMIT_OTHER = "pp_saveoth";
	public static final String RESSUBMIT = "pp_uncommit";
	public static final String RESSUBMIT_OTHER = "pp_saveoth";
	public static final String AUDIT = "pp_audit";
	public static final String AUDIT_OTHER = "pp_saveoth";
	public static final String RESAUDIT = "pp_unaudit";
	public static final String RESAUDIT_OTHER = "pp_saveoth";
	public static final String BANNED = "pp_disable";
	public static final String BANNED_OTHER = "pp_saveoth";
	public static final String RESBANNED = "pp_undisable";
	public static final String RESBANNED_OTHER = "pp_saveoth";
	public static final String END = "pp_closed";
	public static final String END_OTHER = "pp_saveoth";
	public static final String RESEND = "pp_unclosed";
	public static final String RESEND_OTHER = "pp_saveoth";
	public static final String POST = "pp_posting";
	public static final String POST_OTHER = "pp_saveoth";
	public static final String RESPOST = "pp_unposting";
	public static final String RESPOST_OTHER = "pp_saveoth";
	public static final String PRINT = "pp_print";
	public static final String PRINT_OTHER = "pp_printoth";
	public static final String JOBEMPLOYEE_LIST = "pp_jobemployee";
	public static final String PRINT_REPEAT = "pp_printrepeat";
	
	private int pp_id;
	private int pp_emid;// Employee.em_id
	private String pp_caller;// Caller
	/* 权限 */
	private Integer pp_add;// 新增
	private Integer pp_save;// 修改
	private Integer pp_saveoth;// 修改、(反)提交...他人
	private Integer pp_see;// 浏览
	private Integer pp_seeoth;// 浏览他人
	private Integer pp_selflist;// 列表--浏览自己
	private Integer pp_alllist;// 列表--浏览所有
	private Integer pp_delete;// 删除
	private Integer pp_commit;// 提交
	private Integer pp_uncommit;// 反..
	private Integer pp_audit;// 审核
	private Integer pp_unaudit;// 反..
	private Integer pp_print;// 打印
	private Integer pp_printoth;// 打印他人
	private Integer pp_printrepeat;// 多次打印
	private Integer pp_disable;// 禁用
	private Integer pp_undisable;// 反..
	private Integer pp_closed;// 操作
	private Integer pp_unclosed;// 反..
	private Integer pp_posting;// 过账
	private Integer pp_unposting;// 反..
	private Integer pp_jobemployee;// 列表--浏览岗位下属
	
	
	public int getPp_id() {
		return pp_id;
	}

	public void setPp_id(int pp_id) {
		this.pp_id = pp_id;
	}

	public int getPp_emid() {
		return pp_emid;
	}

	public void setPp_emid(int pp_emid) {
		this.pp_emid = pp_emid;
	}

	public String getPp_caller() {
		return pp_caller;
	}

	public void setPp_caller(String pp_caller) {
		this.pp_caller = pp_caller;
	}

	public Integer getPp_add() {
		return pp_add;
	}

	public void setPp_add(Integer pp_add) {
		this.pp_add = pp_add;
	}

	public Integer getPp_save() {
		return pp_save;
	}

	public void setPp_save(Integer pp_save) {
		this.pp_save = pp_save;
	}

	public Integer getPp_saveoth() {
		return pp_saveoth;
	}

	public void setPp_saveoth(Integer pp_saveoth) {
		this.pp_saveoth = pp_saveoth;
	}

	public Integer getPp_see() {
		return pp_see;
	}

	public void setPp_see(Integer pp_see) {
		this.pp_see = pp_see;
	}

	public Integer getPp_seeoth() {
		return pp_seeoth;
	}

	public void setPp_seeoth(Integer pp_seeoth) {
		this.pp_seeoth = pp_seeoth;
	}

	public Integer getPp_selflist() {
		return pp_selflist;
	}

	public void setPp_selflist(Integer pp_selflist) {
		this.pp_selflist = pp_selflist;
	}

	public Integer getPp_alllist() {
		return pp_alllist;
	}

	public void setPp_alllist(Integer pp_alllist) {
		this.pp_alllist = pp_alllist;
	}

	public Integer getPp_delete() {
		return pp_delete;
	}

	public void setPp_delete(Integer pp_delete) {
		this.pp_delete = pp_delete;
	}

	public Integer getPp_commit() {
		return pp_commit;
	}

	public void setPp_commit(Integer pp_commit) {
		this.pp_commit = pp_commit;
	}

	public Integer getPp_uncommit() {
		return pp_uncommit;
	}

	public void setPp_uncommit(Integer pp_uncommit) {
		this.pp_uncommit = pp_uncommit;
	}

	public Integer getPp_audit() {
		return pp_audit;
	}

	public void setPp_audit(Integer pp_audit) {
		this.pp_audit = pp_audit;
	}

	public Integer getPp_unaudit() {
		return pp_unaudit;
	}

	public void setPp_unaudit(Integer pp_unaudit) {
		this.pp_unaudit = pp_unaudit;
	}

	public Integer getPp_print() {
		return pp_print;
	}

	public void setPp_print(Integer pp_print) {
		this.pp_print = pp_print;
	}

	public Integer getPp_printoth() {
		return pp_printoth;
	}

	public void setPp_printoth(Integer pp_printoth) {
		this.pp_printoth = pp_printoth;
	}

	public Integer getPp_printrepeat() {
		return pp_printrepeat;
	}

	public void setPp_printrepeat(Integer pp_printrepeat) {
		this.pp_printrepeat = pp_printrepeat;
	}

	public Integer getPp_disable() {
		return pp_disable;
	}

	public void setPp_disable(Integer pp_disable) {
		this.pp_disable = pp_disable;
	}

	public Integer getPp_undisable() {
		return pp_undisable;
	}

	public void setPp_undisable(Integer pp_undisable) {
		this.pp_undisable = pp_undisable;
	}

	public Integer getPp_closed() {
		return pp_closed;
	}

	public void setPp_closed(Integer pp_closed) {
		this.pp_closed = pp_closed;
	}

	public Integer getPp_unclosed() {
		return pp_unclosed;
	}

	public void setPp_unclosed(Integer pp_unclosed) {
		this.pp_unclosed = pp_unclosed;
	}

	public Integer getPp_posting() {
		return pp_posting;
	}

	public void setPp_posting(Integer pp_posting) {
		this.pp_posting = pp_posting;
	}

	public Integer getPp_unposting() {
		return pp_unposting;
	}

	public void setPp_unposting(Integer pp_unposting) {
		this.pp_unposting = pp_unposting;
	}

	public Integer getPp_jobemployee() {
		return pp_jobemployee;
	}

	public void setPp_jobemployee(Integer pp_jobemployee) {
		this.pp_jobemployee = pp_jobemployee;
	}

	@Override
	public String table() {
		return "PositionPower";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "pp_id" };
	}
}
