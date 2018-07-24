package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;

public class DocumentList implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int dl_id;
	private String dl_code;
	private String dl_name;
	private String dl_version;// 版本
	private float dl_size;// 文件大小
	private String dl_creator;// 创建者
	private String dl_depart;// 所属部门
	private Date dl_updatetime;// 最近更新时间
	private String dl_createtime;// 创建时间
	private int dl_hits;// 点击次数
	private int dl_comments;// 评论次数
	private int dl_downloads;// 下载次数
	private String dl_keywords;// 关键字
	private String dl_style;// 文件类型 pdf xls...
	private String dl_filepath;// 文件路径
	private String dl_capturepath;// 截图路径
	private String dl_abstract;// 摘要
	private String dl_ownerid;// 所有者ID
	private String dl_owner;// 所有者
	private int dl_kind;// -1--文件夹 0--文件
	private int dl_locked;// -1 --锁定 0--未锁定
	private String dl_relate;// 关联文档ID
	private int dl_validate;// 有效期
	private String dl_remark;// 备注
	private int dl_sourcekind;// 是否是映射文件 -1/0
	private int dl_sourceid;// 映射文件ID
	private int dl_parentid;// 上级ID
	private String dl_status;// 状态
	private String dl_statuscode;// 状态码
	private int dl_detno;// 序号
	private int dl_needflowchildren;// 下级目录是否需要审批
	private String dl_virtualpath;// 虚拟路径
	private String dl_fpid;
	private String dl_prefixcode;//前缀码
	private String dl_labelinfo;//文档标签
	private String dl_source;	//文档来源
	private String dl_displayname;// 单据名称
	private String dl_linked;	//单据链接

	public String getDl_displayname() {
		return dl_displayname;
	}

	public void setDl_displayname(String dl_displayname) {
		this.dl_displayname = dl_displayname;
	}

	public String getDl_linked() {
		return dl_linked;
	}

	public void setDl_linked(String dl_linked) {
		this.dl_linked = dl_linked;
	}

	public String getDl_source() {
		return dl_source;
	}

	public void setDl_source(String dl_source) {
		this.dl_source = dl_source;
	}

	public String getDl_labelinfo() {
		return dl_labelinfo;
	}

	public void setDl_labelinfo(String dl_labelinfo) {
		this.dl_labelinfo = dl_labelinfo;
	}

	public String getDl_prefixcode() {
		return dl_prefixcode;
	}

	public void setDl_prefixcode(String dl_prefixcode) {
		this.dl_prefixcode = dl_prefixcode;
	}

	public String getDl_abstract() {
		return dl_abstract;
	}

	public void setDl_abstract(String dl_abstract) {
		this.dl_abstract = dl_abstract;
	}

	public int getDl_id() {
		return dl_id;
	}

	public void setDl_id(int dl_id) {
		this.dl_id = dl_id;
	}

	public String getDl_code() {
		return dl_code;
	}

	public void setDl_code(String dl_code) {
		this.dl_code = dl_code;
	}

	public String getDl_name() {
		return dl_name;
	}

	public void setDl_name(String dl_name) {
		this.dl_name = dl_name;
	}

	public String getDl_version() {
		return dl_version;
	}

	public void setDl_version(String dl_version) {
		this.dl_version = dl_version;
	}

	public float getDl_size() {
		return dl_size;
	}

	public void setDl_size(float dl_size) {
		this.dl_size = dl_size;
	}

	public String getDl_creator() {
		return dl_creator;
	}

	public void setDl_creator(String dl_creator) {
		this.dl_creator = dl_creator;
	}

	public String getDl_depart() {
		return dl_depart;
	}

	public void setDl_depart(String dl_depart) {
		this.dl_depart = dl_depart;
	}

	public Date getDl_updatetime() {
		return dl_updatetime;
	}

	public void setDl_updatetime(Date dl_updatetime) {
		this.dl_updatetime = dl_updatetime;
	}

	public String getDl_createtime() {
		return dl_createtime;
	}

	public void setDl_createtime(String dl_createtime) {
		this.dl_createtime = dl_createtime;
	}

	public int getDl_hits() {
		return dl_hits;
	}

	public void setDl_hits(int dl_hits) {
		this.dl_hits = dl_hits;
	}

	public int getDl_comments() {
		return dl_comments;
	}

	public void setDl_comments(int dl_comments) {
		this.dl_comments = dl_comments;
	}

	public int getDl_downloads() {
		return dl_downloads;
	}

	public void setDl_downloads(int dl_downloads) {
		this.dl_downloads = dl_downloads;
	}

	public String getDl_keywords() {
		return dl_keywords;
	}

	public void setDl_keywords(String dl_keywords) {
		this.dl_keywords = dl_keywords;
	}

	public String getDl_style() {
		return dl_style;
	}

	public void setDl_style(String dl_style) {
		this.dl_style = dl_style;
	}

	public String getDl_filepath() {
		return dl_filepath;
	}

	public void setDl_filepath(String dl_filepath) {
		this.dl_filepath = dl_filepath;
	}

	public String getDl_capturepath() {
		return dl_capturepath;
	}

	public void setDl_capturepath(String dl_capturepath) {
		this.dl_capturepath = dl_capturepath;
	}

	public String getDl_ownerid() {
		return dl_ownerid;
	}

	public void setDl_ownerid(String dl_ownerid) {
		this.dl_ownerid = dl_ownerid;
	}

	public String getDl_owner() {
		return dl_owner;
	}

	public void setDl_owner(String dl_owner) {
		this.dl_owner = dl_owner;
	}

	public int getDl_kind() {
		return dl_kind;
	}

	public void setDl_kind(int dl_kind) {
		this.dl_kind = dl_kind;
	}

	public int getDl_locked() {
		return dl_locked;
	}

	public void setDl_locked(int dl_locked) {
		this.dl_locked = dl_locked;
	}

	public String getDl_relate() {
		return dl_relate;
	}

	public void setDl_relate(String dl_relate) {
		this.dl_relate = dl_relate;
	}

	public int getDl_validate() {
		return dl_validate;
	}

	public void setDl_validate(int dl_validate) {
		this.dl_validate = dl_validate;
	}

	public String getDl_remark() {
		return dl_remark;
	}

	public void setDl_remark(String dl_remark) {
		this.dl_remark = dl_remark;
	}

	public int getDl_sourcekind() {
		return dl_sourcekind;
	}

	public void setDl_sourcekind(int dl_sourcekind) {
		this.dl_sourcekind = dl_sourcekind;
	}

	public int getDl_sourceid() {
		return dl_sourceid;
	}

	public void setDl_sourceid(int dl_sourceid) {
		this.dl_sourceid = dl_sourceid;
	}

	public int getDl_parentid() {
		return dl_parentid;
	}

	public void setDl_parentid(int dl_parentid) {
		this.dl_parentid = dl_parentid;
	}

	public String getDl_status() {
		return dl_status;
	}

	public void setDl_status(String dl_status) {
		this.dl_status = dl_status;
	}

	public String getDl_statuscode() {
		return dl_statuscode;
	}

	public void setDl_statuscode(String dl_statuscode) {
		this.dl_statuscode = dl_statuscode;
	}

	public int getDl_detno() {
		return dl_detno;
	}

	public void setDl_detno(int dl_detno) {
		this.dl_detno = dl_detno;
	}

	public int getDl_needflowchildren() {
		return dl_needflowchildren;
	}

	public void setDl_needflowchildren(int dl_needflowchildren) {
		this.dl_needflowchildren = dl_needflowchildren;
	}

	public String getDl_virtualpath() {
		return dl_virtualpath;
	}

	public void setDl_virtualpath(String dl_virtualpath) {
		this.dl_virtualpath = dl_virtualpath;
	}

	public String getDl_fpid() {
		return dl_fpid;
	}

	public void setDl_fpid(String dl_fpid) {
		this.dl_fpid = dl_fpid;
	}

}
