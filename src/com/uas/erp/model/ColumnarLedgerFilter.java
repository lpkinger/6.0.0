package com.uas.erp.model;

import org.drools.util.StringUtils;

public class ColumnarLedgerFilter {

	/**
	 * 方案名称
	 */
	private String mas_name;
	/**
	 * 方案ID
	 */
	private Integer mas_id;
	/**
	 * 是否辅助核算方案
	 */
	private Integer mas_assistant;

	/**
	 * 选择的期间
	 */
	private YearmonthArea sl_yearmonth;

	/**
	 * 币别
	 */
	private String sl_currency;

	/**
	 * 是否包含未记账
	 */
	private Boolean chkhaveun;

	/**
	 * 业务记录是否分行显示
	 */
	private Boolean businessbranch;

	/**
	 * 无发生额不显示
	 */
	private Boolean chkno;

	/**
	 * 是否显示期末余额
	 */
	private Boolean monthend;

	/**
	 * 是否显示禁用科目
	 */
	private Boolean chkdis;

	/**
	 * 核算类型
	 */
	private String ak_name;

	/**
	 * 选择的核算号
	 */
	private AssArea ass_code;

	/**
	 * 是否显示辅助核算
	 */
	private Boolean chkall;

	/**
	 * 辅助核算是否分页显示
	 */
	private Boolean assbranch;

	/**
	 * 排序方式
	 */
	private String operator;

	/**
	 * 上一条prev、直接筛选current、下一条next
	 */
	private String querytype;

	public String getMas_name() {
		return mas_name;
	}

	public void setMas_name(String mas_name) {
		this.mas_name = mas_name;
	}

	public Integer getMas_id() {
		return mas_id;
	}

	public void setMas_id(Integer mas_id) {
		this.mas_id = mas_id;
	}

	public Integer getMas_assistant() {
		return mas_assistant;
	}

	public void setMas_assistant(Integer mas_assistant) {
		this.mas_assistant = mas_assistant;
	}

	public YearmonthArea getSl_yearmonth() {
		return sl_yearmonth;
	}

	public void setSl_yearmonth(YearmonthArea sl_yearmonth) {
		this.sl_yearmonth = sl_yearmonth;
	}

	public String getSl_currency() {
		return sl_currency;
	}

	public void setSl_currency(String sl_currency) {
		this.sl_currency = sl_currency;
	}

	public Boolean getChkhaveun() {
		return chkhaveun;
	}

	public void setChkhaveun(Boolean chkhaveun) {
		this.chkhaveun = chkhaveun;
	}

	public Boolean getBusinessbranch() {
		return businessbranch;
	}

	public void setBusinessbranch(Boolean businessbranch) {
		this.businessbranch = businessbranch;
	}

	public Boolean getChkno() {
		return chkno;
	}

	public void setChkno(Boolean chkno) {
		this.chkno = chkno;
	}

	public Boolean getMonthend() {
		return monthend;
	}

	public void setMonthend(Boolean monthend) {
		this.monthend = monthend;
	}

	public Boolean getChkdis() {
		return chkdis;
	}

	public void setChkdis(Boolean chkdis) {
		this.chkdis = chkdis;
	}

	public String getAk_name() {
		return ak_name;
	}

	public void setAk_name(String ak_name) {
		this.ak_name = ak_name;
	}

	public AssArea getAss_code() {
		return ass_code;
	}

	public void setAss_code(AssArea ass_code) {
		this.ass_code = ass_code;
	}

	public Boolean getChkall() {
		return chkall;
	}

	public void setChkall(Boolean chkall) {
		this.chkall = chkall;
	}

	public Boolean getAssbranch() {
		return assbranch;
	}

	public void setAssbranch(Boolean assbranch) {
		this.assbranch = assbranch;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getQuerytype() {
		return querytype;
	}

	public boolean isQueryFirst() {
		return "first".equals(this.querytype);
	}

	public boolean isQueryNext() {
		return "next".equals(this.querytype);
	}

	public boolean isQueryPrev() {
		return "prev".equals(this.querytype);
	}

	public boolean isQueryEnd() {
		return "end".equals(this.querytype);
	}

	public void setQuerytype(String querytype) {
		this.querytype = querytype;
	}

	/**
	 * 是否辅助核算方案
	 * 
	 * @return
	 */
	public boolean isAssistant() {
		return 0 != this.mas_assistant;
	}

	public static class YearmonthArea {
		private Integer begin;
		private Integer end;

		public Integer getBegin() {
			return begin;
		}

		public void setBegin(Integer begin) {
			this.begin = begin;
		}

		public Integer getEnd() {
			return end;
		}

		public void setEnd(Integer end) {
			this.end = end;
		}
	}

	public static class AssArea {
		private String begin;
		private String beginName;
		private String end;
		private String endName;
		private String current;
		private String currentName;

		public String getCurrent() {
			return current;
		}

		public void setCurrent(String current) {
			this.current = current;
		}

		public void setDefaultCurrent() {
			if (StringUtils.isEmpty(this.current)) {
				this.current = (StringUtils.isEmpty(begin) ? end : begin);
				this.currentName = (StringUtils.isEmpty(beginName) ? endName : beginName);
			}
		}

		public String getBegin() {
			return begin;
		}

		public void setBegin(String begin) {
			this.begin = begin;
		}

		public String getEnd() {
			return end;
		}

		public void setEnd(String end) {
			this.end = end;
		}

		public String getBeginName() {
			return beginName;
		}

		public void setBeginName(String beginName) {
			this.beginName = beginName;
		}

		public String getEndName() {
			return endName;
		}

		public void setEndName(String endName) {
			this.endName = endName;
		}

		public String getCurrentName() {
			return currentName;
		}

		public void setCurrentName(String currentName) {
			this.currentName = currentName;
		}
	}

}
