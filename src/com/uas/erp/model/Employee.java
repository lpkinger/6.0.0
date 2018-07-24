package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 员工表
 * 
 * @author yingp
 * @date 2012-07-13 17:00:00
 */
public class Employee implements Saveable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9154546832037377352L;
	private Integer em_id;// Id
	private String em_code;// 员工编号
	private Long em_uu;// 员工uu
	private Integer em_defaultorid = 0;// 默认的组织ID(HrOrg)
	private Integer em_defaultjbid = 0;// 默认的职位ID
	private Integer em_defaulthsid = 0;// 默认的岗位ID(JOB)
	private String em_name;// 姓名
	private String em_sex;// 性别
	private String em_iccode;// 身份证号
	private Date em_birthday;// 出入年月
	private String em_nation;// 民族
	private String em_marry;// 婚否
	private String em_polity;// 政党
	private String em_tel;// 联系电话
	private String em_mobile;// 移动电话
	private String em_email;// 邮箱
	private String em_mailpassword;// 邮箱密码
	private String em_native;// 籍贯
	private String em_address;// 地址
	private String em_finishschool;// 毕业学校
	private String em_speciality;// 专业
	private String em_culture;// 文化程度
	private Float em_worktime;// 工作年限
	private String em_remark;// 备注
	private String em_status;// 状态
	private String em_statuscode;
	private String em_bank;// 开户行
	private String em_accounts;// 帐号
	private String em_recorder;// 录入
	private Date em_indate;// 入职日期
	private Integer em_imageid;// 照片ID
	private String em_contact;// 联系人
	private String em_ctel;// 联系人电话
	private Integer em_maid = 0;// 帐套ID
	private String em_master;// 当前帐套名称
	private String em_masters;// 可登录的帐套
	private Integer em_remind = 1;// 是否刷新寻呼,1-是,0-否
	private Master currentMaster;// 当前登录帐套
	private List<EmpsJobs> empsJobs;// 员工岗位关系
	private Date em_mologintime;// 移动客户端最新登录时间
	private Integer em_pdamobilelogin;// PDA移动端是否登录
	private String em_cardcode;
	private String em_photourl;// 人员照片在服务器端的存储路径
	private Integer virtual_enuu;// 虚拟账号对应客户方UU
	private Integer joborgnorelation;// 岗位不关联组织
	private Integer em_onlyinner;
	private String em_defaulthscode;//岗位编号
	private String em_defaulthsname;//岗位名称
	private Date em_pwdupdatedate;//密码修改时间
	private String em_saledepart;//所属销售部
	
	private String em_cop;//所属公司
	private String em_factory ;//所属工厂
	public Date getEm_pwdupdatedate() {
		return em_pwdupdatedate;
	}

	public void setEm_pwdupdatedate(Date em_pwdupdatedate) {
		this.em_pwdupdatedate = em_pwdupdatedate;
	}

	private Integer em_dtremind=0;//是否桌面提醒（20170309 by zyc）
	
	
	public String getEm_defaulthscode() {
		return em_defaulthscode;
	}

	public void setEm_defaulthscode(String em_defaulthscode) {
		this.em_defaulthscode = em_defaulthscode;
	}

	public String getEm_defaulthsname() {
		return em_defaulthsname;
	}

	public void setEm_defaulthsname(String em_defaulthsname) {
		this.em_defaulthsname = em_defaulthsname;
	}

	public Integer getJoborgnorelation() {
		return joborgnorelation;
	}

	public void setJoborgnorelation(Integer joborgnorelation) {
		this.joborgnorelation = joborgnorelation;
	}

	public String getEm_cardcode() {
		return em_cardcode;
	}

	public void setEm_cardcode(String em_cardcode) {
		this.em_cardcode = em_cardcode;
	}

	public Date getEm_mologintime() {
		return em_mologintime;
	}

	public void setEm_mologintime(Date em_mologintime) {
		this.em_mologintime = em_mologintime;
	}

	private String model_code;
	private String model_name;

	public String getModel_code() {
		return model_code;
	}

	public void setModel_code(String model_code) {
		this.model_code = model_code;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public Master getCurrentMaster() {
		return currentMaster;
	}

	public void setCurrentMaster(Master currentMaster) {
		this.currentMaster = currentMaster;
	}

	public String getEm_master() {
		return em_master;
	}

	public void setEm_master(String em_master) {
		this.em_master = em_master;
	}

	private String em_type = "normal";// 员工账号类型,超级账号、普通账号
	private String em_password;// 登录密码
	private Integer em_enid;// 员工所在企业id
	private String em_position; // 岗位
	private String em_depart; // 部门
	private String em_departmentcode;// 部门编号
	private String em_professname;// 职称
	private String em_class;// 员工类型 (试用，正式，离职)
	private String em_endinsu;
	private String em_medinsu;
	private String em_unempinsu;
	private String em_eminjuinsu;
	private String em_mateinsu;
	private String em_socailcard;
	private String em_accumucard;
	private String em_accimount;
	private String em_secuclass;
	private String em_height;
	private String em_weight;
	private String em_blood;
	private String em_heathlevel;
	private String em_defaultorname;
	private String em_lastip;
	private Date em_leavedate;// 离职时间
	private String em_leavetype;// 离职类型
	private Integer em_imid;// IM的ID

	public Integer getEm_id() {
		return em_id;
	}

	public void setEm_id(Integer em_id) {
		this.em_id = em_id;
	}

	public String getEm_code() {
		return em_code;
	}

	public void setEm_code(String em_code) {
		this.em_code = em_code;
	}

	public Long getEm_uu() {
		return em_uu;
	}

	public void setEm_uu(Long em_uu) {
		this.em_uu = em_uu;
	}

	public Integer getEm_defaultorid() {
		return em_defaultorid == null ? 0 : em_defaultorid;
	}

	public void setEm_defaultorid(Integer em_defaultorid) {
		this.em_defaultorid = em_defaultorid;
	}

	public Integer getEm_defaultjbid() {
		return em_defaultjbid;
	}

	public void setEm_defaultjbid(Integer em_defaultjbid) {
		this.em_defaultjbid = em_defaultjbid;
	}

	public Integer getEm_defaulthsid() {
		return em_defaulthsid;
	}

	public void setEm_defaulthsid(Integer em_defaulthsid) {
		this.em_defaulthsid = em_defaulthsid;
	}

	public String getEm_name() {
		return em_name;
	}

	public void setEm_name(String em_name) {
		this.em_name = em_name;
	}

	public String getEm_sex() {
		return em_sex;
	}

	public void setEm_sex(String em_sex) {
		this.em_sex = em_sex;
	}

	public String getEm_iccode() {
		return em_iccode;
	}

	public void setEm_iccode(String em_iccode) {
		this.em_iccode = em_iccode;
	}

	public Date getEm_birthday() {
		return em_birthday;
	}

	public void setEm_birthday(Date em_birthday) {
		this.em_birthday = em_birthday;
	}

	public String getEm_nation() {
		return em_nation;
	}

	public void setEm_nation(String em_nation) {
		this.em_nation = em_nation;
	}

	public String getEm_marry() {
		return em_marry;
	}

	public void setEm_marry(String em_marry) {
		this.em_marry = em_marry;
	}

	public String getEm_polity() {
		return em_polity;
	}

	public void setEm_polity(String em_polity) {
		this.em_polity = em_polity;
	}

	public String getEm_tel() {
		return em_tel;
	}

	public void setEm_tel(String em_tel) {
		this.em_tel = em_tel;
	}

	public String getEm_mobile() {
		return em_mobile;
	}

	public void setEm_mobile(String em_mobile) {
		this.em_mobile = em_mobile;
	}

	public String getEm_email() {
		return em_email;
	}

	public void setEm_email(String em_email) {
		this.em_email = em_email;
	}

	public String getEm_mailpassword() {
		return em_mailpassword;
	}

	public void setEm_mailpassword(String em_mailpassword) {
		this.em_mailpassword = em_mailpassword;
	}

	public String getEm_native() {
		return em_native;
	}

	public void setEm_native(String em_native) {
		this.em_native = em_native;
	}

	public String getEm_address() {
		return em_address;
	}

	public void setEm_address(String em_address) {
		this.em_address = em_address;
	}

	public String getEm_finishschool() {
		return em_finishschool;
	}

	public void setEm_finishschool(String em_finishschool) {
		this.em_finishschool = em_finishschool;
	}

	public String getEm_speciality() {
		return em_speciality;
	}

	public void setEm_speciality(String em_speciality) {
		this.em_speciality = em_speciality;
	}

	public String getEm_culture() {
		return em_culture;
	}

	public void setEm_culture(String em_culture) {
		this.em_culture = em_culture;
	}

	public Float getEm_worktime() {
		return em_worktime;
	}

	public void setEm_worktime(Float em_worktime) {
		this.em_worktime = em_worktime;
	}

	public String getEm_remark() {
		return em_remark;
	}

	public void setEm_remark(String em_remark) {
		this.em_remark = em_remark;
	}

	public String getEm_status() {
		return em_status;
	}

	public void setEm_status(String em_status) {
		this.em_status = em_status;
	}

	public String getEm_bank() {
		return em_bank;
	}

	public void setEm_bank(String em_bank) {
		this.em_bank = em_bank;
	}

	public String getEm_accounts() {
		return em_accounts;
	}

	public void setEm_accounts(String em_accounts) {
		this.em_accounts = em_accounts;
	}

	public String getEm_recorder() {
		return em_recorder;
	}

	public void setEm_recorder(String em_recorder) {
		this.em_recorder = em_recorder;
	}

	public Date getEm_indate() {
		return em_indate;
	}

	public void setEm_indate(Date em_indate) {
		this.em_indate = em_indate;
	}

	public Integer getEm_imageid() {
		return em_imageid;
	}

	public void setEm_imageid(Integer em_imageid) {
		this.em_imageid = em_imageid;
	}

	public String getEm_contact() {
		return em_contact;
	}

	public void setEm_contact(String em_contact) {
		this.em_contact = em_contact;
	}

	public String getEm_ctel() {
		return em_ctel;
	}

	public void setEm_ctel(String em_ctel) {
		this.em_ctel = em_ctel;
	}

	public Integer getEm_maid() {
		return em_maid;
	}

	public void setEm_maid(Integer em_maid) {
		this.em_maid = em_maid;
	}

	public String getEm_type() {
		return em_type;
	}

	public void setEm_type(String em_type) {
		this.em_type = em_type;
	}

	public String getEm_password() {
		return em_password;
	}

	public void setEm_password(String em_password) {
		this.em_password = em_password;
	}

	public Integer getEm_enid() {
		return em_enid;
	}

	public void setEm_enid(Integer em_enid) {
		this.em_enid = em_enid;
	}

	public String getEm_position() {
		return em_position;
	}

	public void setEm_position(String em_position) {
		this.em_position = em_position;
	}

	public String getEm_depart() {
		return em_depart;
	}

	public void setEm_depart(String em_depart) {
		this.em_depart = em_depart;
	}

	public String getEm_departmentcode() {
		return em_departmentcode;
	}

	public void setEm_departmentcode(String em_departmentcode) {
		this.em_departmentcode = em_departmentcode;
	}

	public String getEm_professname() {
		return em_professname;
	}

	public void setEm_professname(String em_professname) {
		this.em_professname = em_professname;
	}

	public String getEm_class() {
		return em_class;
	}

	public void setEm_class(String em_class) {
		this.em_class = em_class;
	}

	public String getEm_endinsu() {
		return em_endinsu;
	}

	public void setEm_endinsu(String em_endinsu) {
		this.em_endinsu = em_endinsu;
	}

	public String getEm_medinsu() {
		return em_medinsu;
	}

	public void setEm_medinsu(String em_medinsu) {
		this.em_medinsu = em_medinsu;
	}

	public String getEm_unempinsu() {
		return em_unempinsu;
	}

	public void setEm_unempinsu(String em_unempinsu) {
		this.em_unempinsu = em_unempinsu;
	}

	public String getEm_eminjuinsu() {
		return em_eminjuinsu;
	}

	public void setEm_eminjuinsu(String em_eminjuinsu) {
		this.em_eminjuinsu = em_eminjuinsu;
	}

	public String getEm_mateinsu() {
		return em_mateinsu;
	}

	public void setEm_mateinsu(String em_mateinsu) {
		this.em_mateinsu = em_mateinsu;
	}

	public String getEm_socailcard() {
		return em_socailcard;
	}

	public void setEm_socailcard(String em_socailcard) {
		this.em_socailcard = em_socailcard;
	}

	public String getEm_accumucard() {
		return em_accumucard;
	}

	public void setEm_accumucard(String em_accumucard) {
		this.em_accumucard = em_accumucard;
	}

	public String getEm_accimount() {
		return em_accimount;
	}

	public void setEm_accimount(String em_accimount) {
		this.em_accimount = em_accimount;
	}

	public String getEm_secuclass() {
		return em_secuclass;
	}

	public void setEm_secuclass(String em_secuclass) {
		this.em_secuclass = em_secuclass;
	}

	public Integer getEm_remind() {
		return em_remind;
	}

	public void setEm_remind(Integer em_remind) {
		em_remind = em_remind == null ? 1 : em_remind;
		this.em_remind = em_remind;
	}

	public String getEm_lastip() {
		return em_lastip;
	}

	public void setEm_lastip(String em_lastip) {
		this.em_lastip = em_lastip;
	}

	@Override
	public String table() {
		return "Employee";
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "em_id" };
	}

	public String getEm_height() {
		return em_height;
	}

	public void setEm_height(String em_height) {
		this.em_height = em_height;
	}

	public String getEm_weight() {
		return em_weight;
	}

	public void setEm_weight(String em_weight) {
		this.em_weight = em_weight;
	}

	public String getEm_masters() {
		return em_masters;
	}

	public void setEm_masters(String em_masters) {
		this.em_masters = em_masters;
	}

	public String getEm_blood() {
		return em_blood;
	}

	public void setEm_blood(String em_blood) {
		this.em_blood = em_blood;
	}

	public String getEm_heathlevel() {
		return em_heathlevel;
	}

	public void setEm_heathlevel(String em_heathlevel) {
		this.em_heathlevel = em_heathlevel;
	}

	public String getEm_defaultorname() {
		return em_defaultorname;
	}

	public void setEm_defaultorname(String em_defaultorname) {
		this.em_defaultorname = em_defaultorname;
	}

	public Date getEm_leavedate() {
		return em_leavedate;
	}

	public void setEm_leavedate(Date em_leavedate) {
		this.em_leavedate = em_leavedate;
	}

	public String getEm_leavetype() {
		return em_leavetype;
	}

	public void setEm_leavetype(String em_leavetype) {
		this.em_leavetype = em_leavetype;
	}

	public List<EmpsJobs> getEmpsJobs() {
		return empsJobs;
	}

	public void setEmpsJobs(List<EmpsJobs> empsJobs) {
		this.empsJobs = empsJobs;
	}

	public Integer getEm_pdamobilelogin() {
		return em_pdamobilelogin;
	}

	public void setEm_pdamobilelogin(Integer em_pdamobilelogin) {
		this.em_pdamobilelogin = em_pdamobilelogin;
	}

	public String getEm_statuscode() {
		return em_statuscode;
	}

	public void setEm_statuscode(String em_statuscode) {
		this.em_statuscode = em_statuscode;
	}

	public String getEm_photourl() {
		return em_photourl;
	}

	public void setEm_photourl(String em_photourl) {
		this.em_photourl = em_photourl;
	}

	public Integer getVirtual_enuu() {
		return virtual_enuu;
	}

	public void setVirtual_enuu(Integer virtual_enuu) {
		this.virtual_enuu = virtual_enuu;
	}

	/**
	 * 超级账户
	 * 
	 * @return
	 */
	@JsonIgnore
	public boolean isAdmin() {
		return "admin".equals(this.em_type);
	}

	/**
	 * 是否虚拟超级账户
	 * 
	 * @return
	 */
	@JsonIgnore
	public boolean isAdminVirtual() {
		return "admin".equals(this.em_type) && "admin_virtual".equals(this.em_class);
	}

	/**
	 * 是否虚拟客户账户
	 * */
	@JsonIgnore
	public boolean isCustomerVirtual() {
		return //"admin".equals(this.em_type) &&
				"customer_virtual".equals(this.em_class);
	}

	public Integer getEm_imid() {
		return em_imid;
	}

	public void setEm_imid(Integer em_imid) {
		this.em_imid = em_imid;
	}

	public Integer getEm_onlyinner() {
		return em_onlyinner;
	}

	public void setEm_onlyinner(Integer em_onlyinner) {
		this.em_onlyinner = em_onlyinner;
	}

	@Override
	public String toString() {
		return this.em_code + "(" + this.em_name + ")";
	}

	public Integer getEm_dtremind() {
		return em_dtremind;
	}

	public void setEm_dtremind(Integer em_dtremind) {
		this.em_dtremind = em_dtremind;
	}

	public String getEm_saledepart() {
		return em_saledepart;
	}

	public void setEm_saledepart(String em_saledepart) {
		this.em_saledepart = em_saledepart;
	}

	public String getEm_cop() {
		return em_cop;
	}

	public void setEm_cop(String em_cop) {
		this.em_cop = em_cop;
	}

	/**
	 * @return the em_factory
	 */
	public String getEm_factory() {
		return em_factory;
	}

	/**
	 * @param em_factory the em_factory to set
	 */
	public void setEm_factory(String em_factory) {
		this.em_factory = em_factory;
	}
}
