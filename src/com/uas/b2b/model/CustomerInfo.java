package com.uas.b2b.model;

import java.util.Date;
import java.util.List;

/**
 * created by shicr on 2017/12/26
 **/
public class CustomerInfo {

	/**
	 * id
	 */
	private Long id;

	/**
	 * 主键id
	 */
	private Long erpId;

	/**
	 * 企业uu
	 * 
	 * @return
	 */
	private Long enuu;

	/**
	 * 公司中文名
	 */
	private String cu_name;

	/**
	 * 公司英文名
	 */
	private String cu_engname;

	/**
	 * 公司类型
	 */
	private String cu_enterptype;

	/**
	 * 成立时间
	 */
	private Date cu_licensedate;

	/**
	 * 证件类型
	 */
	private String cu_paperstype;

	/**
	 * 证件类型
	 */
	private String cu_institutype;

	/**
	 * 证件号码
	 */
	private String cu_paperscode;

	/**
	 * 证件到期日
	 */
	private Date cu_ctfduedate;

	/**
	 * 营业执照号
	 */
	private String cu_businesscode;

	/**
	 * 国标行业分类
	 */
	private String cu_nastdinducls;

	/**
	 * 注册资本币种
	 */
	private String cu_capcurrency;

	/**
	 * 注册资本
	 */
	private Double cu_regcapital;

	/**
	 * 实缴资本
	 */
	private Double cu_paidincapital;
	/**
	 * 公司注册地
	 */
	private String cu_regadd;
	/**
	 * 办公地址
	 */
	private String cu_officeadd;

	/**
	 * 经营范围
	 */
	private String cu_businsscope;

	/**
	 * 其他情况说明
	 */
	private String cu_others;

	/**
	 * 最高权力机构
	 * 
	 * @return
	 */
	private String cu_highestauthority;

	/**
	 * 员工人数
	 * 
	 * @return
	 */
	private Long cu_employeesnum;

	/**
	 * 部门设置概况
	 * 
	 * @return
	 */
	private String cu_departsituation;

	/**
	 * 管理层信息
	 */
	private List<CustomerExcutiveInfo> customerExcutives;

	/**
	 * 股东信息表
	 */
	private List<ShareholdersInfo> shareholders;

	/**
	 * 关联企业
	 */
	private List<AssociateCompanyInfo> associateCompanies;

	/**
	 * 变更内容
	 */
	private List<ChangesInstructionInfo> changesInstructions;

	/**
	 * 情况描述
	 */
	// private BusinessConditionInfo businessCondition;

	/**
	 * 财务情况说明
	 */
	// private FinanceConditionInfo financeConditions;

	/**
	 * 买方客户资料
	 */
	// private List<PurcCustInfo> mfCustInfos;

	/**
	 * 经营情况表
	 */
	// private List<ProductMixInfo> productMixes;

	/**
	 * 财务数据表
	 */
	// private List<AccountInfo> accountInfoList;

	/**
	 * 供应商客户
	 */
	// private List<UpdowncastInfo> updowncasts;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getErpId() {
		return erpId;
	}

	public void setErpId(Long erpId) {
		this.erpId = erpId;
	}

	public Long getEnuu() {
		return enuu;
	}

	public void setEnuu(Long enuu) {
		this.enuu = enuu;
	}

	public String getCu_name() {
		return cu_name;
	}

	public void setCu_name(String cu_name) {
		this.cu_name = cu_name;
	}

	public String getCu_engname() {
		return cu_engname;
	}

	public void setCu_engname(String cu_engname) {
		this.cu_engname = cu_engname;
	}

	public String getCu_enterptype() {
		return cu_enterptype;
	}

	public void setCu_enterptype(String cu_enterptype) {
		this.cu_enterptype = cu_enterptype;
	}

	public Date getCu_licensedate() {
		return cu_licensedate;
	}

	public void setCu_licensedate(Date cu_licensedate) {
		this.cu_licensedate = cu_licensedate;
	}

	public String getCu_paperstype() {
		return cu_paperstype;
	}

	public void setCu_paperstype(String cu_paperstype) {
		this.cu_paperstype = cu_paperstype;
	}

	public String getCu_institutype() {
		return cu_institutype;
	}

	public void setCu_institutype(String cu_institutype) {
		this.cu_institutype = cu_institutype;
	}

	public String getCu_paperscode() {
		return cu_paperscode;
	}

	public void setCu_paperscode(String cu_paperscode) {
		this.cu_paperscode = cu_paperscode;
	}

	public Date getCu_ctfduedate() {
		return cu_ctfduedate;
	}

	public void setCu_ctfduedate(Date cu_ctfduedate) {
		this.cu_ctfduedate = cu_ctfduedate;
	}

	public String getCu_businesscode() {
		return cu_businesscode;
	}

	public void setCu_businesscode(String cu_businesscode) {
		this.cu_businesscode = cu_businesscode;
	}

	public String getCu_nastdinducls() {
		return cu_nastdinducls;
	}

	public void setCu_nastdinducls(String cu_nastdinducls) {
		this.cu_nastdinducls = cu_nastdinducls;
	}

	public String getCu_capcurrency() {
		return cu_capcurrency;
	}

	public void setCu_capcurrency(String cu_capcurrency) {
		this.cu_capcurrency = cu_capcurrency;
	}

	public Double getCu_regcapital() {
		return cu_regcapital;
	}

	public void setCu_regcapital(Double cu_regcapital) {
		this.cu_regcapital = cu_regcapital;
	}

	public Double getCu_paidincapital() {
		return cu_paidincapital;
	}

	public void setCu_paidincapital(Double cu_paidincapital) {
		this.cu_paidincapital = cu_paidincapital;
	}

	public String getCu_regadd() {
		return cu_regadd;
	}

	public void setCu_regadd(String cu_regadd) {
		this.cu_regadd = cu_regadd;
	}

	public String getCu_officeadd() {
		return cu_officeadd;
	}

	public void setCu_officeadd(String cu_officeadd) {
		this.cu_officeadd = cu_officeadd;
	}

	public String getCu_businsscope() {
		return cu_businsscope;
	}

	public void setCu_businsscope(String cu_businsscope) {
		this.cu_businsscope = cu_businsscope;
	}

	public String getCu_others() {
		return cu_others;
	}

	public void setCu_others(String cu_others) {
		this.cu_others = cu_others;
	}

	public String getCu_highestauthority() {
		return cu_highestauthority;
	}

	public void setCu_highestauthority(String cu_highestauthority) {
		this.cu_highestauthority = cu_highestauthority;
	}

	public Long getCu_employeesnum() {
		return cu_employeesnum;
	}

	public void setCu_employeesnum(Long cu_employeesnum) {
		this.cu_employeesnum = cu_employeesnum;
	}

	public String getCu_departsituation() {
		return cu_departsituation;
	}

	public void setCu_departsituation(String cu_departsituation) {
		this.cu_departsituation = cu_departsituation;
	}

	public List<CustomerExcutiveInfo> getCustomerExcutives() {
		return customerExcutives;
	}

	public void setCustomerExcutives(List<CustomerExcutiveInfo> customerExcutives) {
		this.customerExcutives = customerExcutives;
	}

	public List<ShareholdersInfo> getShareholders() {
		return shareholders;
	}

	public void setShareholders(List<ShareholdersInfo> shareholders) {
		this.shareholders = shareholders;
	}

	public List<AssociateCompanyInfo> getAssociateCompanies() {
		return associateCompanies;
	}

	public void setAssociateCompanies(List<AssociateCompanyInfo> associateCompanies) {
		this.associateCompanies = associateCompanies;
	}

	public List<ChangesInstructionInfo> getChangesInstructions() {
		return changesInstructions;
	}

	public void setChangesInstructions(List<ChangesInstructionInfo> changesInstructions) {
		this.changesInstructions = changesInstructions;
	}

	/**
	 * created by shicr on 2017/12/20 管理层信息
	 **/
	public static class CustomerExcutiveInfo {

		/**
		 * ID
		 */
		private Long id;

		/**
		 * 关联ID
		 */
		private Long cusId;

		/**
		 * 主键id
		 */
		private Long erpId;

		/**
		 * 高管姓名
		 */
		private String ce_name;

		/**
		 * 性别
		 */
		private String ce_sex;

		/**
		 * 证件类型
		 */
		private String ce_paperstype;

		/**
		 * 证件号码
		 */
		private String ce_paperscode;

		/**
		 * 学历
		 */
		private String ce_education;

		/**
		 * 职务
		 */
		private String ce_position;

		/**
		 * 担任该职务时间
		 */
		private Date ce_stwkfpo;

		/**
		 * 从事本行业时间
		 */
		private Integer ce_workeyears;

		/**
		 * 住所
		 */
		private String ce_nowaddress;

		/**
		 * 工作简介
		 */
		private String ce_woekexper;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getCusId() {
			return cusId;
		}

		public void setCusId(Long cusId) {
			this.cusId = cusId;
		}

		public Long getErpId() {
			return erpId;
		}

		public void setErpId(Long erpId) {
			this.erpId = erpId;
		}

		public String getCe_name() {
			return ce_name;
		}

		public void setCe_name(String ce_name) {
			this.ce_name = ce_name;
		}

		public String getCe_sex() {
			return ce_sex;
		}

		public void setCe_sex(String ce_sex) {
			this.ce_sex = ce_sex;
		}

		public String getCe_paperstype() {
			return ce_paperstype;
		}

		public void setCe_paperstype(String ce_paperstype) {
			this.ce_paperstype = ce_paperstype;
		}

		public String getCe_paperscode() {
			return ce_paperscode;
		}

		public void setCe_paperscode(String ce_paperscode) {
			this.ce_paperscode = ce_paperscode;
		}

		public String getCe_education() {
			return ce_education;
		}

		public void setCe_education(String ce_education) {
			this.ce_education = ce_education;
		}

		public String getCe_position() {
			return ce_position;
		}

		public void setCe_position(String ce_position) {
			this.ce_position = ce_position;
		}

		public Date getCe_stwkfpo() {
			return ce_stwkfpo;
		}

		public void setCe_stwkfpo(Date ce_stwkfpo) {
			this.ce_stwkfpo = ce_stwkfpo;
		}

		public Integer getCe_workeyears() {
			return ce_workeyears;
		}

		public void setCe_workeyears(Integer ce_workeyears) {
			this.ce_workeyears = ce_workeyears;
		}

		public String getCe_nowaddress() {
			return ce_nowaddress;
		}

		public void setCe_nowaddress(String ce_nowaddress) {
			this.ce_nowaddress = ce_nowaddress;
		}

		public String getCe_woekexper() {
			return ce_woekexper;
		}

		public void setCe_woekexper(String ce_woekexper) {
			this.ce_woekexper = ce_woekexper;
		}

	}

	/**
	 * 股东信息表 created by shicr on 2017/12/18
	 **/
	public static class ShareholdersInfo {

		/**
		 * ID
		 */
		private Long id;

		/**
		 * 关联ID
		 */
		private Long cusId;

		/**
		 * 主键id
		 */
		private Long erpId;

		/**
		 * 股东名称
		 */
		private String cs_name;

		/**
		 * 证件类型
		 */
		private String cs_paperstype;

		/**
		 * 证件号码
		 */
		private String cs_paperscode;

		/**
		 * 出资比例（%）
		 */
		private Double cs_investratio;

		/**
		 * 实际投资金额
		 */
		private Double cs_investamount;

		public String getCs_name() {
			return cs_name;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getCusId() {
			return cusId;
		}

		public void setCusId(Long cusId) {
			this.cusId = cusId;
		}

		public Long getErpId() {
			return erpId;
		}

		public void setErpId(Long erpId) {
			this.erpId = erpId;
		}

		public void setCs_name(String cs_name) {
			this.cs_name = cs_name;
		}

		public String getCs_paperstype() {
			return cs_paperstype;
		}

		public void setCs_paperstype(String cs_paperstype) {
			this.cs_paperstype = cs_paperstype;
		}

		public String getCs_paperscode() {
			return cs_paperscode;
		}

		public void setCs_paperscode(String cs_paperscode) {
			this.cs_paperscode = cs_paperscode;
		}

		public Double getCs_investratio() {
			return cs_investratio;
		}

		public void setCs_investratio(Double cs_investratio) {
			this.cs_investratio = cs_investratio;
		}

		public Double getCs_investamount() {
			return cs_investamount;
		}

		public void setCs_investamount(Double cs_investamount) {
			this.cs_investamount = cs_investamount;
		}

	}

	/**
	 * 关联企业 created by shicr on 2017/12/18
	 **/
	public static class AssociateCompanyInfo {

		/**
		 * ID
		 */
		private Long id;

		/**
		 * 关联ID
		 */
		private Long cusId;

		/**
		 * 主键id
		 */
		private Long erpId;

		/**
		 * 关联方企业名称
		 */
		private String cud_name;

		/**
		 * 关联关系
		 */
		private String cud_association;

		/**
		 * 法人代表
		 */
		private String cud_legalperson;

		/**
		 * 法人代表股权(占比%)
		 */
		private Double cud_ratio;

		/**
		 * 主营业务
		 */
		private String cud_product;

		/**
		 * 注册资本(万元)
		 */
		private Double cud_amount;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getErpId() {
			return erpId;
		}

		public void setErpId(Long erpId) {
			this.erpId = erpId;
		}

		public Long getCusId() {
			return cusId;
		}

		public void setCusId(Long cusId) {
			this.cusId = cusId;
		}

		public String getCud_name() {
			return cud_name;
		}

		public void setCud_name(String cud_name) {
			this.cud_name = cud_name;
		}

		public String getCud_association() {
			return cud_association;
		}

		public void setCud_association(String cud_association) {
			this.cud_association = cud_association;
		}

		public String getCud_legalperson() {
			return cud_legalperson;
		}

		public void setCud_legalperson(String cud_legalperson) {
			this.cud_legalperson = cud_legalperson;
		}

		public Double getCud_ratio() {
			return cud_ratio;
		}

		public void setCud_ratio(Double cud_ratio) {
			this.cud_ratio = cud_ratio;
		}

		public String getCud_product() {
			return cud_product;
		}

		public void setCud_product(String cud_product) {
			this.cud_product = cud_product;
		}

		public Double getCud_amount() {
			return cud_amount;
		}

		public void setCud_amount(Double cud_amount) {
			this.cud_amount = cud_amount;
		}
	}

	/**
	 * 变更内容
	 */
	public static class ChangesInstructionInfo {

		/**
		 * ID
		 */
		private Long id;

		/**
		 * 主键id
		 */
		private Long erpId;

		/**
		 * 关联ID
		 */
		private Long cusId;

		/**
		 * 变更科目
		 */
		private String cd_type;

		/**
		 * 变更时间
		 */
		private Date cs_date;

		/**
		 * 变更前内容
		 */
		private String cs_before;

		/**
		 * 变更后内容
		 */
		private String cs_after;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getErpId() {
			return erpId;
		}

		public void setErpId(Long erpId) {
			this.erpId = erpId;
		}

		public Long getCusId() {
			return cusId;
		}

		public void setCusId(Long cusId) {
			this.cusId = cusId;
		}

		public String getCd_type() {
			return cd_type;
		}

		public void setCd_type(String cd_type) {
			this.cd_type = cd_type;
		}

		public Date getCs_date() {
			return cs_date;
		}

		public void setCs_date(Date cs_date) {
			this.cs_date = cs_date;
		}

		public String getCs_before() {
			return cs_before;
		}

		public void setCs_before(String cs_before) {
			this.cs_before = cs_before;
		}

		public String getCs_after() {
			return cs_after;
		}

		public void setCs_after(String cs_after) {
			this.cs_after = cs_after;
		}
	}

	/**
	 * 买方客户资料 created by shicr on 2017/12/20
	 **/
	public static class PurcCustInfo {

		/**
		 * ID
		 */
		private Long id;

		/**
		 * 主键id
		 */
		private Long erpId;

		/**
		 * 关联ID
		 */
		private Long cusId;

		/**
		 * 买方名称
		 */
		private String mf_custname;

		/**
		 * 额度申请
		 */
		private Double mf_credit;

		/**
		 * 法人代表
		 */
		private String mf_legrep;

		/**
		 * 注册资本(万元)
		 */
		private Double mf_regcapital;

		/**
		 * 经营地址
		 */
		private String mf_addr;

		/**
		 * 成立时间
		 */
		private Date mf_estabtime;

		/**
		 * 联系人
		 */
		private String mf_contact;

		/**
		 * 联系电话
		 */
		private String mf_contactnum;

		/**
		 * 传真
		 */
		private String mf_fax;

		/**
		 * 电子邮箱
		 */
		private String mf_email;

		/**
		 * 结算开户行
		 */
		private String mf_balancebank;

		/**
		 * 银行账户
		 */
		private String mf_bankaccount;

		/**
		 * 交易产品
		 */
		private String mf_tradprod;

		/**
		 * 付款条件
		 */
		private String mf_payterm;

		/**
		 * 股东占比(%)
		 */
		private Double mf_shareholder;

		/**
		 * 经营范围
		 */
		private String mf_businsscope;

		/**
		 * 是否与其他保理商合作 -1:true 0:false
		 */
		private Short mf_coopothers;

		/**
		 * 保险公司取得保险额度 -1:true 0:false
		 */
		private Short mf_hasinslimit;

		// private Long mf_id;

		/**
		 * 买方客户信息详情
		 */
		// private List<MFCustInfoDetail> mfCustInfoDetailList;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getErpId() {
			return erpId;
		}

		public void setErpId(Long erpId) {
			this.erpId = erpId;
		}

		public Long getCusId() {
			return cusId;
		}

		public void setCusId(Long cusId) {
			this.cusId = cusId;
		}

		public String getMf_custname() {
			return mf_custname;
		}

		public void setMf_custname(String mf_custname) {
			this.mf_custname = mf_custname;
		}

		public Double getMf_credit() {
			return mf_credit;
		}

		public void setMf_credit(Double mf_credit) {
			this.mf_credit = mf_credit;
		}

		public String getMf_legrep() {
			return mf_legrep;
		}

		public void setMf_legrep(String mf_legrep) {
			this.mf_legrep = mf_legrep;
		}

		public Double getMf_regcapital() {
			return mf_regcapital;
		}

		public void setMf_regcapital(Double mf_regcapital) {
			this.mf_regcapital = mf_regcapital;
		}

		public String getMf_addr() {
			return mf_addr;
		}

		public void setMf_addr(String mf_addr) {
			this.mf_addr = mf_addr;
		}

		public Date getMf_estabtime() {
			return mf_estabtime;
		}

		public void setMf_estabtime(Date mf_estabtime) {
			this.mf_estabtime = mf_estabtime;
		}

		public String getMf_contact() {
			return mf_contact;
		}

		public void setMf_contact(String mf_contact) {
			this.mf_contact = mf_contact;
		}

		public String getMf_contactnum() {
			return mf_contactnum;
		}

		public void setMf_contactnum(String mf_contactnum) {
			this.mf_contactnum = mf_contactnum;
		}

		public String getMf_fax() {
			return mf_fax;
		}

		public void setMf_fax(String mf_fax) {
			this.mf_fax = mf_fax;
		}

		public String getMf_email() {
			return mf_email;
		}

		public void setMf_email(String mf_email) {
			this.mf_email = mf_email;
		}

		public String getMf_balancebank() {
			return mf_balancebank;
		}

		public void setMf_balancebank(String mf_balancebank) {
			this.mf_balancebank = mf_balancebank;
		}

		public String getMf_bankaccount() {
			return mf_bankaccount;
		}

		public void setMf_bankaccount(String mf_bankaccount) {
			this.mf_bankaccount = mf_bankaccount;
		}

		public String getMf_tradprod() {
			return mf_tradprod;
		}

		public void setMf_tradprod(String mf_tradprod) {
			this.mf_tradprod = mf_tradprod;
		}

		public String getMf_payterm() {
			return mf_payterm;
		}

		public void setMf_payterm(String mf_payterm) {
			this.mf_payterm = mf_payterm;
		}

		public Double getMf_shareholder() {
			return mf_shareholder;
		}

		public void setMf_shareholder(Double mf_shareholder) {
			this.mf_shareholder = mf_shareholder;
		}

		public String getMf_businsscope() {
			return mf_businsscope;
		}

		public void setMf_businsscope(String mf_businsscope) {
			this.mf_businsscope = mf_businsscope;
		}

		public Short getMf_coopothers() {
			return mf_coopothers;
		}

		public void setMf_coopothers(Short mf_coopothers) {
			this.mf_coopothers = mf_coopothers;
		}

		public Short getMf_hasinslimit() {
			return mf_hasinslimit;
		}

		public void setMf_hasinslimit(Short mf_hasinslimit) {
			this.mf_hasinslimit = mf_hasinslimit;
		}

		/*
		 * public Long getMf_id() { return mf_id; }
		 * 
		 * public void setMf_id(Long mf_id) { this.mf_id = mf_id; }
		 */

		/*
		 * public List<MFCustInfoDetail> getMfCustInfoDetailList() { return
		 * mfCustInfoDetailList; }
		 * 
		 * public void setMfCustInfoDetailList(List<MFCustInfoDetail>
		 * mfCustInfoDetailList) { this.mfCustInfoDetailList =
		 * mfCustInfoDetailList; }
		 */

		/**
		 * 买方客户明细 created by shicr on 2017/12/29
		 **/
		public static class MFCustInfoDetail {

			/**
			 * ID
			 */
			private Long id;

			/**
			 * 主键id
			 */
			private Long erpId;

			/**
			 * 年度
			 */
			private Integer mfd_year;

			/**
			 * 购买总额
			 */
			private Double mfd_amount;

			/**
			 * 赊账总额
			 */
			private Double mfd_chargeamount;

			/**
			 * 折扣金额
			 */
			private Double mfd_discountamount;

			/**
			 * 有无逾期
			 */
			private String mfd_overdue;

			/**
			 * 买方信息id
			 */
			private Long mfId;

			public Long getId() {
				return id;
			}

			public void setId(Long id) {
				this.id = id;
			}

			public Long getErpId() {
				return erpId;
			}

			public void setErpId(Long erpId) {
				this.erpId = erpId;
			}

			public Integer getMfd_year() {
				return mfd_year;
			}

			public void setMfd_year(Integer mfd_year) {
				this.mfd_year = mfd_year;
			}

			public Double getMfd_amount() {
				return mfd_amount;
			}

			public void setMfd_amount(Double mfd_amount) {
				this.mfd_amount = mfd_amount;
			}

			public Double getMfd_chargeamount() {
				return mfd_chargeamount;
			}

			public void setMfd_chargeamount(Double mfd_chargeamount) {
				this.mfd_chargeamount = mfd_chargeamount;
			}

			public Double getMfd_discountamount() {
				return mfd_discountamount;
			}

			public void setMfd_discountamount(Double mfd_discountamount) {
				this.mfd_discountamount = mfd_discountamount;
			}

			public String getMfd_overdue() {
				return mfd_overdue;
			}

			public void setMfd_overdue(String mfd_overdue) {
				this.mfd_overdue = mfd_overdue;
			}

			public Long getMfId() {
				return mfId;
			}

			public void setMfId(Long mfId) {
				this.mfId = mfId;
			}

		}
	}

	/**
	 * created by shicr on 2017/12/20 情况描述
	 **/
	public static class BusinessConditionInfo {

		/**
		 * ID
		 */
		private Long id;

		/**
		 * 主键id
		 */
		private Long erpId;

		/**
		 * 关联ID
		 */
		private Long cusId;

		/**
		 * 经营情况简述
		 */
		private String bc_bcremark;

		/**
		 * 贸易流程简述
		 * 
		 * @return
		 */
		private String bc_sweaterprocess;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getErpId() {
			return erpId;
		}

		public void setErpId(Long erpId) {
			this.erpId = erpId;
		}

		public Long getCusId() {
			return cusId;
		}

		public void setCusId(Long cusId) {
			this.cusId = cusId;
		}

		public String getBc_bcremark() {
			return bc_bcremark;
		}

		public void setBc_bcremark(String bc_bcremark) {
			this.bc_bcremark = bc_bcremark;
		}

		public String getBc_sweaterprocess() {
			return bc_sweaterprocess;
		}

		public void setBc_sweaterprocess(String bc_sweaterprocess) {
			this.bc_sweaterprocess = bc_sweaterprocess;
		}
	}

	/**
	 * created by shicr on 2017/12/20 经营情况表
	 **/
	public static class ProductMixInfo {

		/**
		 * ID
		 */
		private Long id;

		/**
		 * 主键id
		 */
		private Long erpId;

		/**
		 * 关联ID
		 */
		private Long cusId;

		/**
		 * 产品/服务种类
		 */
		private String pm_kind;

		/**
		 * 今年营业收入
		 */
		private Double pm_tannualrevenue;

		/**
		 * 占今年总收入比例
		 */
		private Double pm_tratio;

		/**
		 * 上年营业收入
		 */
		private Double pm_annualrevenue;

		/**
		 * 占总收入比例
		 */
		private Double pm_ratio;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getErpId() {
			return erpId;
		}

		public void setErpId(Long erpId) {
			this.erpId = erpId;
		}

		public Long getCusId() {
			return cusId;
		}

		public void setCusId(Long cusId) {
			this.cusId = cusId;
		}

		public String getPm_kind() {
			return pm_kind;
		}

		public void setPm_kind(String pm_kind) {
			this.pm_kind = pm_kind;
		}

		public Double getPm_tannualrevenue() {
			return pm_tannualrevenue;
		}

		public void setPm_tannualrevenue(Double pm_tannualrevenue) {
			this.pm_tannualrevenue = pm_tannualrevenue;
		}

		public Double getPm_tratio() {
			return pm_tratio;
		}

		public void setPm_tratio(Double pm_tratio) {
			this.pm_tratio = pm_tratio;
		}

		public Double getPm_annualrevenue() {
			return pm_annualrevenue;
		}

		public void setPm_annualrevenue(Double pm_annualrevenue) {
			this.pm_annualrevenue = pm_annualrevenue;
		}

		public Double getPm_ratio() {
			return pm_ratio;
		}

		public void setPm_ratio(Double pm_ratio) {
			this.pm_ratio = pm_ratio;
		}
	}

	/**
	 * 供应商客户 created by shicr on 2017/12/20
	 **/
	public static class UpdowncastInfo {

		/**
		 * ID
		 */
		private Long id;

		/**
		 * 主键id
		 */
		private Long erpId;

		/**
		 * 关联ID
		 */
		private Long cusId;

		/**
		 * 名称
		 */
		private String udc_name;

		/**
		 * 产品
		 */
		private String udc_product;

		/**
		 * 上年交易额
		 */
		private Double udc_lastyear;

		/**
		 * 今年交易额
		 */
		private Double udc_thisyear;

		/**
		 * 结账方式及日期
		 */
		private String udc_payment;

		/**
		 * 合作年限
		 */
		private Short udc_cooperationyears;

		/**
		 * 上/下游客户
		 */
		private String udc_kind;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getErpId() {
			return erpId;
		}

		public void setErpId(Long erpId) {
			this.erpId = erpId;
		}

		public Long getCusId() {
			return cusId;
		}

		public void setCusId(Long cusId) {
			this.cusId = cusId;
		}

		public String getUdc_name() {
			return udc_name;
		}

		public void setUdc_name(String udc_name) {
			this.udc_name = udc_name;
		}

		public String getUdc_product() {
			return udc_product;
		}

		public void setUdc_product(String udc_product) {
			this.udc_product = udc_product;
		}

		public Double getUdc_lastyear() {
			return udc_lastyear;
		}

		public void setUdc_lastyear(Double udc_lastyear) {
			this.udc_lastyear = udc_lastyear;
		}

		public Double getUdc_thisyear() {
			return udc_thisyear;
		}

		public void setUdc_thisyear(Double udc_thisyear) {
			this.udc_thisyear = udc_thisyear;
		}

		public String getUdc_payment() {
			return udc_payment;
		}

		public void setUdc_payment(String udc_payment) {
			this.udc_payment = udc_payment;
		}

		public Short getUdc_cooperationyears() {
			return udc_cooperationyears;
		}

		public void setUdc_cooperationyears(Short udc_cooperationyears) {
			this.udc_cooperationyears = udc_cooperationyears;
		}

		public String getUdc_kind() {
			return udc_kind;
		}

		public void setUdc_kind(String udc_kind) {
			this.udc_kind = udc_kind;
		}
	}

	/**
	 * created by shicr on 2017/12/20 财务情况说明
	 **/
	public static class FinanceConditionInfo {

		/**
		 * ID
		 */
		private Long id;

		/**
		 * 主键id
		 */
		private Long erpId;

		/**
		 * 关联ID
		 */
		private Long cusId;

		/**
		 * 账龄<=3个月
		 */
		private Double fc_agingamount1;

		/**
		 * 3个月<帐龄≤6个月
		 */
		private Double fc_agingamount2;

		/**
		 * 6个月<帐龄<=1年
		 */
		private Double fc_agingamount4;

		/**
		 * 帐龄>1年
		 */
		private Double fc_agingamount3;

		/**
		 * 其他应收账款说明
		 */
		private String fc_otharremark;

		/**
		 * 截止时间，年月的拼接,字段名按UAS表来写，不是手误
		 */
		private Integer fc_yeatmonth;

		/**
		 * 截止至XX年
		 */
		private Integer year;

		/**
		 * 截止至XX月
		 */
		private Integer month;

		/**
		 * 余额
		 */
		private Long fc_arbalance;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getErpId() {
			return erpId;
		}

		public void setErpId(Long erpId) {
			this.erpId = erpId;
		}

		public Long getCusId() {
			return cusId;
		}

		public void setCusId(Long cusId) {
			this.cusId = cusId;
		}

		public Double getFc_agingamount1() {
			return fc_agingamount1;
		}

		public void setFc_agingamount1(Double fc_agingamount1) {
			this.fc_agingamount1 = fc_agingamount1;
		}

		public Double getFc_agingamount2() {
			return fc_agingamount2;
		}

		public void setFc_agingamount2(Double fc_agingamount2) {
			this.fc_agingamount2 = fc_agingamount2;
		}

		public Double getFc_agingamount4() {
			return fc_agingamount4;
		}

		public void setFc_agingamount4(Double fc_agingamount4) {
			this.fc_agingamount4 = fc_agingamount4;
		}

		public Double getFc_agingamount3() {
			return fc_agingamount3;
		}

		public void setFc_agingamount3(Double fc_agingamount3) {
			this.fc_agingamount3 = fc_agingamount3;
		}

		public String getFc_otharremark() {
			return fc_otharremark;
		}

		public void setFc_otharremark(String fc_otharremark) {
			this.fc_otharremark = fc_otharremark;
		}

		public Integer getFc_yeatmonth() {
			return fc_yeatmonth;
		}

		public void setFc_yeatmonth(Integer fc_yeatmonth) {
			this.fc_yeatmonth = fc_yeatmonth;
		}

		public Integer getYear() {
			return year;
		}

		public void setYear(Integer year) {
			this.year = year;
		}

		public Integer getMonth() {
			return month;
		}

		public void setMonth(Integer month) {
			this.month = month;
		}

		public Long getFc_arbalance() {
			return fc_arbalance;
		}

		public void setFc_arbalance(Long fc_arbalance) {
			this.fc_arbalance = fc_arbalance;
		}
	}

	/**
	 * created by shicr on 2017/12/20 财务数据表
	 **/
	public static class AccountInfo {

		/**
		 * ID
		 */
		private Long id;

		/**
		 * 主键id
		 */
		private Long erpId;

		/**
		 * 关联ID
		 */
		private Long cusId;

		/**
		 * 财务类型情况
		 */
		private String ai_kind;

		/**
		 * 欠款客户全称
		 */
		private String ai_cuname;

		/**
		 * 项目名称
		 */
		private String ai_caname;

		/**
		 * 金额
		 */
		private Double ai_amount;

		/**
		 * 占比
		 */
		private Double ai_rate;

		/**
		 * 账龄
		 */
		private String ai_payment;

		/**
		 * 机构名称
		 */
		private String ai_bank;

		/**
		 * 应收账款余额
		 */
		private Double ai_leftamount;

		/**
		 * 产生原因
		 */
		private String ai_remark;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getErpId() {
			return erpId;
		}

		public void setErpId(Long erpId) {
			this.erpId = erpId;
		}

		public Long getCusId() {
			return cusId;
		}

		public void setCusId(Long cusId) {
			this.cusId = cusId;
		}

		public String getAi_kind() {
			return ai_kind;
		}

		public void setAi_kind(String ai_kind) {
			this.ai_kind = ai_kind;
		}

		public String getAi_cuname() {
			return ai_cuname;
		}

		public void setAi_cuname(String ai_cuname) {
			this.ai_cuname = ai_cuname;
		}

		public Double getAi_amount() {
			return ai_amount;
		}

		public void setAi_amount(Double ai_amount) {
			this.ai_amount = ai_amount;
		}

		public Double getAi_rate() {
			return ai_rate;
		}

		public void setAi_rate(Double ai_rate) {
			this.ai_rate = ai_rate;
		}

		public String getAi_payment() {
			return ai_payment;
		}

		public void setAi_payment(String ai_payment) {
			this.ai_payment = ai_payment;
		}

		public Double getAi_leftamount() {
			return ai_leftamount;
		}

		public void setAi_leftamount(Double ai_leftamount) {
			this.ai_leftamount = ai_leftamount;
		}

		public String getAi_remark() {
			return ai_remark;
		}

		public void setAi_remark(String ai_remark) {
			this.ai_remark = ai_remark;
		}

		public String getAi_caname() {
			return ai_caname;
		}

		public void setAi_caname(String ai_caname) {
			this.ai_caname = ai_caname;
		}

		public String getAi_bank() {
			return ai_bank;
		}

		public void setAi_bank(String ai_bank) {
			this.ai_bank = ai_bank;
		}
	}
}