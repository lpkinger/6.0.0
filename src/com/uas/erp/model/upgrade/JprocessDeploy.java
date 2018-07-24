package com.uas.erp.model.upgrade;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的jprocessdeploy
 * 
 * @author yingp
 * 
 */
public class JprocessDeploy implements Saveable {

	private Integer jd_selfid; // 自身Id
	private Integer jd_parentid; // 类 Id
	private String jd_classifiedname; // 分类名称 ;
	private Short jd_isleaf; // 是否叶子节点,
	private String jd_caller;
	private String jd_processdefinitionid;
	private String jd_formurl;
	private String jd_processdefinitionname;
	private String jd_processdescription;
	private String jd_xmlstring;
	private String jd_enabled;
	private String jd_type;
	private Short jd_detno;
	private String jd_updater;
	private Date jd_updatetime;
	private Date jd_recorddate;

	private String plan_id;

	public String getJd_caller() {
		return jd_caller;
	}

	public Integer getJd_selfid() {
		return jd_selfid;
	}

	public void setJd_selfid(Integer jd_selfid) {
		this.jd_selfid = jd_selfid;
	}

	public Integer getJd_parentid() {
		return jd_parentid;
	}

	public void setJd_parentid(Integer jd_parentid) {
		this.jd_parentid = jd_parentid;
	}

	public String getJd_classifiedname() {
		return jd_classifiedname;
	}

	public void setJd_classifiedname(String jd_classifiedname) {
		this.jd_classifiedname = jd_classifiedname;
	}

	public Short getJd_isleaf() {
		return jd_isleaf;
	}

	public void setJd_isleaf(Short jd_isleaf) {
		this.jd_isleaf = jd_isleaf;
	}

	public String getJd_processdefinitionid() {
		return jd_processdefinitionid;
	}

	public void setJd_processdefinitionid(String jd_processdefinitionid) {
		this.jd_processdefinitionid = jd_processdefinitionid;
	}

	public String getJd_formurl() {
		return jd_formurl;
	}

	public void setJd_formurl(String jd_formurl) {
		this.jd_formurl = jd_formurl;
	}

	public String getJd_processdefinitionname() {
		return jd_processdefinitionname;
	}

	public void setJd_processdefinitionname(String jd_processdefinitionname) {
		this.jd_processdefinitionname = jd_processdefinitionname;
	}

	public String getJd_processdescription() {
		return jd_processdescription;
	}

	public void setJd_processdescription(String jd_processdescription) {
		this.jd_processdescription = jd_processdescription;
	}

	public String getJd_xmlstring() {
		return jd_xmlstring;
	}

	public void setJd_xmlstring(String jd_xmlstring) {
		this.jd_xmlstring = jd_xmlstring;
	}

	public void setJd_caller(String jd_caller) {
		this.jd_caller = jd_caller;
	}

	public String getJd_enabled() {
		return jd_enabled;
	}

	public void setJd_enabled(String jd_enabled) {
		this.jd_enabled = jd_enabled;
	}

	public String getJd_type() {
		return jd_type;
	}

	public void setJd_type(String jd_type) {
		this.jd_type = jd_type;
	}

	public Short getJd_detno() {
		return jd_detno;
	}

	public void setJd_detno(Short jd_detno) {
		this.jd_detno = jd_detno;
	}

	public String getJd_updater() {
		return jd_updater;
	}

	public void setJd_updater(String jd_updater) {
		this.jd_updater = jd_updater;
	}

	public Date getJd_updatetime() {
		return jd_updatetime;
	}

	public void setJd_updatetime(Date jd_updatetime) {
		this.jd_updatetime = jd_updatetime;
	}

	public Date getJd_recorddate() {
		return jd_recorddate;
	}

	public void setJd_recorddate(Date jd_recorddate) {
		this.jd_recorddate = jd_recorddate;
	}

	@JsonIgnore
	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}

	@Override
	public String table() {
		return "upgrade$jprocessdeploy";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
