package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.util.StringUtils;

import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.Saveable;

/**
 * 帐套
 * 
 * @author yingp
 * @date 2012-7-17 15:24:33
 */
public class Master implements Saveable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7479469727810677867L;
	private int ma_id;
	private String ma_user;// 数据库用户名
	private String ms_pwd;// 数据库密码
	private String ma_name;// 帐套名，与bean名一致
	private String ma_man;
	private Date ma_time;
	private String ma_language;
	private String ma_function;
	private Integer ma_type = 3;// 类型,0--集团中心,1--资料中心,2--子集团,3--营运中心
	private Integer ma_pid = 0;// 上级集团ID
	private Integer ma_kind = 0;//帐套种类
	private String ma_soncode;
	private Long ma_uu;// 帐套绑定UU号
	private Integer ma_b2benable;// 是否启用B2B,1启用,0未启用
	private String ma_b2bwebsite;// B2B平台站点
	private String ma_accesssecret;// 密钥
	private String ma_domain;// 域名
	private List<Master> children;
	private long ma_selectTime;// 查询待办事宜的比较时间
	private String ma_driver;
	private String ma_url;
	private String ma_installtype;// 初始化类型:make/trade
	private Short ma_init;// 是否初始化完成
	private Short ma_enable;// 是否可使用
	private Long ma_manageid; // 对应后台管理的企业ID
	private String ma_env;// 对应商城环境
	private String ma_fssecret;//保理密钥
	private String ma_finwebsite;//金融服务站点
	private String ma_ccwebsite;//产城服务站点

	public String getMa_env() {
		return ma_env;
	}

	public void setMa_env(String ma_env) {
		this.ma_env = ma_env;
	}

	public long getMa_selectTime() {
		return ma_selectTime;
	}

	public void setMa_selectTime(long ma_selectTime) {
		this.ma_selectTime = ma_selectTime;
	}

	public int getMa_id() {
		return ma_id;
	}

	public void setMa_id(int ma_id) {
		this.ma_id = ma_id;
	}

	public String getMa_user() {
		return ma_user;
	}

	public void setMa_user(String ma_user) {
		this.ma_user = ma_user;
	}

	public String getMs_pwd() {
		return ms_pwd;
	}

	public void setMs_pwd(String ms_pwd) {
		this.ms_pwd = ms_pwd;
	}

	public String getMa_name() {
		return ma_name;
	}

	public void setMa_name(String ma_name) {
		this.ma_name = ma_name;
	}

	public String getMa_language() {
		return ma_language;
	}

	public void setMa_language(String ma_language) {
		this.ma_language = ma_language;
	}

	public String getMa_man() {
		return ma_man;
	}

	public void setMa_man(String ma_man) {
		this.ma_man = ma_man;
	}

	public Date getMa_time() {
		return ma_time;
	}

	public void setMa_time(Date ma_time) {
		this.ma_time = ma_time;
	}

	public String getMa_function() {
		return ma_function;
	}

	public void setMa_function(String ma_function) {
		this.ma_function = ma_function;
	}

	public Integer getMa_type() {
		return ma_type;
	}

	public void setMa_type(Integer ma_type) {
		this.ma_type = ma_type;
	}

	public Integer getMa_kind() {
		return ma_kind;
	}

	public void setMa_kind(Integer ma_kind) {
		this.ma_kind = ma_kind;
	}

	public Integer getMa_pid() {
		return ma_pid;
	}

	public void setMa_pid(Integer ma_pid) {
		this.ma_pid = ma_pid;
	}

	public String getMa_soncode() {
		return ma_soncode;
	}

	public void setMa_soncode(String ma_soncode) {
		this.ma_soncode = ma_soncode;
	}

	@Override
	public String table() {
		return "Master";
	}

	public Long getMa_uu() {
		return ma_uu;
	}

	public void setMa_uu(Long ma_uu) {
		this.ma_uu = ma_uu;
	}

	@Override
	public String[] keyColumns() {
		return new String[] { "ma_id" };
	}

	public List<Master> getChildren() {
		return children;
	}

	public void setChildren(List<Master> children) {
		this.children = children;
	}

	public Integer getMa_b2benable() {
		return ma_b2benable;
	}

	public void setMa_b2benable(Integer ma_b2benable) {
		this.ma_b2benable = ma_b2benable;
	}

	public String getMa_b2bwebsite() {
		 return this.ma_b2bwebsite;
	}

	public void setMa_b2bwebsite(String ma_b2bwebsite) {
		this.ma_b2bwebsite = ma_b2bwebsite;
	}

	public String getMa_driver() {
		return ma_driver;
	}

	public void setMa_driver(String ma_driver) {
		this.ma_driver = ma_driver;
	}

	public String getMa_url() {
		return ma_url;
	}

	public void setMa_url(String ma_url) {
		this.ma_url = ma_url;
	}

	public String getMa_domain() {
		return ma_domain;
	}

	public void setMa_domain(String ma_domain) {
		this.ma_domain = ma_domain;
	}

	public String getMa_installtype() {
		return ma_installtype;
	}

	public void setMa_installtype(String ma_installtype) {
		this.ma_installtype = ma_installtype;
	}

	public Short getMa_init() {
		return ma_init;
	}

	public void setMa_init(Short ma_init) {
		this.ma_init = ma_init;
	}

	public Short getMa_enable() {
		return ma_enable;
	}

	public void setMa_enable(Short ma_enable) {
		this.ma_enable = ma_enable;
	}

	public String getMa_ccwebsite() {
		return ma_ccwebsite;
	}

	public void setMa_ccwebsite(String ma_ccwebsite) {
		this.ma_ccwebsite = ma_ccwebsite;
	}

	/**
	 * 是否启用B2B数据传输功能
	 * 
	 * @return
	 */
	public boolean b2bEnable() {
		return ma_uu != null && getMa_b2benable() != null && Constant.YES == getMa_b2benable() && !StringUtils.isEmpty(getMa_b2bwebsite());
	}

	/**
	 * 是否初始化完成
	 * 
	 * @return
	 */
	public boolean isInit() {
		return this.ma_init != null && Constant.YES == this.ma_init;
	}

	/**
	 * 是否有效
	 * 
	 * @return
	 */
	public boolean isEnable() {
		return this.ma_enable != null && Constant.YES == this.ma_enable;
	}

	/**
	 * 是否演示
	 * 
	 * @return
	 */
	public boolean isGuest() {
		return "guest".equals(this.ma_installtype);
	}

	public Long getMa_manageid() {
		return ma_manageid;
	}

	public void setMa_manageid(Long ma_manageid) {
		this.ma_manageid = ma_manageid;
	}

	public String getMa_accesssecret() {
		return ma_accesssecret;
	}

	public void setMa_accesssecret(String ma_accesssecret) {
		this.ma_accesssecret = ma_accesssecret;
	}

	public String getEnv() {
		if (StringUtils.isEmpty(this.getMa_env())) {
			return Env.test.name();
		}
		return this.getMa_env();
	}

	enum Env {
		test, prod
	}

	public String getMa_fssecret() {
		return ma_fssecret;
	}

	public void setMa_fssecret(String ma_fssecret) {
		this.ma_fssecret = ma_fssecret;
	}

	
	public String getMa_finwebsite() {
		return ma_finwebsite;
	}

	public void setMa_finwebsite(String ma_finwebsite) {
		this.ma_finwebsite = ma_finwebsite;
	}

	/**
	 * 获取b2c商城的链接地址
	 * 
	 * @return
	 */
	public String getB2CUrl() {
		String url = Constant.b2cTestHost();
		if (this.getEnv().equals("prod")) {
			url = Constant.b2cHost();
		}
		return url;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ma_id;
		result = prime * result + ((ma_user == null) ? 0 : ma_user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Master other = (Master) obj;
		if (ma_id != other.ma_id)
			return false;
		if (ma_user == null) {
			if (other.ma_user != null)
				return false;
		} else if (!ma_user.equals(other.ma_user))
			return false;
		return true;
	}

}
