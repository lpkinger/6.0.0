package com.uas.erp.model;

public class LedgerFilter {

	/**
	 * 选择的期间
	 */
	private YearmonthArea sl_yearmonth;

	/**
	 * 选择的日期
	 */
	private DateArea sl_date;

	/**
	 * 选择的科目级别
	 */
	private LevelArea ca_level;

	/**
	 * 币别
	 */
	private String sl_currency;

	/**
	 * 是否包含未记账
	 */
	private Boolean chkhaveun;

	/**
	 * 无发生额不显示
	 */
	private Boolean chkno;

	/**
	 * 是否显示禁用科目
	 */
	private Boolean chkdis;

	/**
	 * 辅助核算
	 */
	private AssArea vds_asscode;

	/**
	 * 选择的科目
	 */
	private CateArea ca_code;

	/**
	 * 是否显示辅助核算
	 */
	private Boolean chkall;

	/**
	 * 是否只显示末级科目
	 */
	private Boolean chkDispLeaf;

	/**
	 * 按明细科目列表显示
	 */
	private Boolean chkcatelist;

	/**
	 * 按对方科目多条显示
	 */
	private Boolean chkothasslist;

	/**
	 * 带核算项目的末级科目不单独显示
	 */
	private Boolean singleshow;

	/**
	 * 余额为零且无发生额不显示
	 */
	private Boolean chkzeroandno;

	/**
	 * 强制显示对方科目
	 */
	private Boolean chkoth;

	/**
	 * 排序方式
	 */
	private String operator;

	/**
	 * 上一条prev、直接筛选current、下一条next
	 */
	private String querytype;

	private String queryId;

	/**
	 * 辅助核算组合
	 */
	private AssMulti assMulti;

	/**
	 * 科目第一次显示
	 */
	private Integer iscafirst;

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

	public Boolean getChkno() {
		return chkno;
	}

	public void setChkno(Boolean chkno) {
		this.chkno = chkno;
	}

	public Boolean getChkdis() {
		return chkdis;
	}

	public void setChkdis(Boolean chkdis) {
		this.chkdis = chkdis;
	}

	public String getQueryId() {
		return queryId;
	}

	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}

	public Boolean getChkall() {
		return chkall;
	}

	public void setChkall(Boolean chkall) {
		this.chkall = chkall;
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

	public void setQuerytype(String querytype) {
		this.querytype = querytype;
	}

	public DateArea getSl_date() {
		return sl_date;
	}

	public void setSl_date(DateArea sl_date) {
		this.sl_date = sl_date;
	}

	public CateArea getCa_code() {
		return ca_code;
	}

	public void setCa_code(CateArea ca_code) {
		this.ca_code = ca_code;
	}

	public Boolean getChkDispLeaf() {
		return chkDispLeaf;
	}

	public void setChkDispLeaf(Boolean chkDispLeaf) {
		this.chkDispLeaf = chkDispLeaf;
	}

	public AssArea getVds_asscode() {
		return vds_asscode;
	}

	public void setVds_asscode(AssArea vds_asscode) {
		this.vds_asscode = vds_asscode;
	}

	public Boolean getChkcatelist() {
		return chkcatelist;
	}

	public void setChkcatelist(Boolean chkcatelist) {
		this.chkcatelist = chkcatelist;
	}

	public Boolean getChkothasslist() {
		return chkothasslist;
	}

	public void setChkothasslist(Boolean chkothasslist) {
		this.chkothasslist = chkothasslist;
	}

	public Boolean getSingleshow() {
		return singleshow;
	}

	public void setSingleshow(Boolean singleshow) {
		this.singleshow = singleshow;
	}

	public Boolean getChkzeroandno() {
		return chkzeroandno;
	}

	public void setChkzeroandno(Boolean chkzeroandno) {
		this.chkzeroandno = chkzeroandno;
	}

	public Boolean getChkoth() {
		return chkoth;
	}

	public void setChkoth(Boolean chkoth) {
		this.chkoth = chkoth;
	}

	public LevelArea getCa_level() {
		return ca_level;
	}

	public void setCa_level(LevelArea ca_level) {
		this.ca_level = ca_level;
	}

	public AssMulti getAssMulti() {
		return assMulti;
	}

	public void setAssMulti(AssMulti assMulti) {
		this.assMulti = assMulti;
	}

	public Integer getIscafirst() {
		return iscafirst;
	}

	public void setIscafirst(Integer iscafirst) {
		this.iscafirst = iscafirst;
	}

	public static class YearmonthArea {
		private Integer begin;
		private Integer end;

		public YearmonthArea() {
			super();
		}

		public YearmonthArea(Integer begin, Integer end) {
			super();
			this.begin = begin;
			this.end = end;
		}

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

	public static class DateArea {
		private String begin;
		private String end;

		public DateArea() {
			super();
		}

		public DateArea(String begin, String end) {
			super();
			this.begin = begin;
			this.end = end;
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
	}

	public static class LevelArea {
		private Integer begin;
		private Integer end;

		public LevelArea() {
			super();
		}

		public LevelArea(Integer begin, Integer end) {
			super();
			this.begin = begin;
			this.end = end;
		}

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
		private String asl_asstype;
		private String asl_asscode;
		private String lastType;
		private String lastCode;
		private String lastName;

		public AssArea() {
			super();
		}

		public AssArea(String lastType, String lastCode, String lastName) {
			super();
			this.lastType = lastType;
			this.lastCode = lastCode;
			this.lastName = lastName;
		}

		public String getLastType() {
			return lastType;
		}

		public void setLastType(String lastType) {
			this.lastType = lastType;
		}

		public String getLastCode() {
			return lastCode;
		}

		public void setLastCode(String lastCode) {
			this.lastCode = lastCode;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public String getAsl_asstype() {
			return asl_asstype;
		}

		public void setAsl_asstype(String asl_asstype) {
			this.asl_asstype = asl_asstype;
		}

		public String getAsl_asscode() {
			return asl_asscode;
		}

		public void setAsl_asscode(String asl_asscode) {
			this.asl_asscode = asl_asscode;
		}

	}

	public static class CateArea {
		private String begin;
		private String end;
		private String last;
		private String lastName;
		private Boolean continuous;

		public CateArea() {
			super();
		}

		public CateArea(String begin, String end, String last, String lastName) {
			super();
			this.begin = begin;
			this.end = end;
			this.last = last;
			this.lastName = lastName;
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

		public String getLast() {
			return last;
		}

		public void setLast(String last) {
			this.last = last;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public Boolean getContinuous() {
			return continuous;
		}

		public void setContinuous(Boolean continuous) {
			this.continuous = continuous;
		}
	}

	public static class AssMulti {
		private String amm_acid;
		private String amm_assmulti;
		private String lastAcid;
		private String lastAssMulti;

		public AssMulti() {
			super();
		}

		public AssMulti(String lastAcid, String lastAssMulti) {
			super();
			this.setLastAcid(lastAcid);
			this.setLastAssMulti(lastAssMulti);
		}

		public String getAmm_acid() {
			return amm_acid;
		}

		public void setAmm_acid(String amm_acid) {
			this.amm_acid = amm_acid;
		}

		public String getAmm_assmulti() {
			return amm_assmulti;
		}

		public void setAmm_assmulti(String amm_assmulti) {
			this.amm_assmulti = amm_assmulti;
		}

		public String getLastAssMulti() {
			return lastAssMulti;
		}

		public void setLastAssMulti(String lastAssMulti) {
			this.lastAssMulti = lastAssMulti;
		}

		public String getLastAcid() {
			return lastAcid;
		}

		public void setLastAcid(String lastAcid) {
			this.lastAcid = lastAcid;
		}
	}

}
