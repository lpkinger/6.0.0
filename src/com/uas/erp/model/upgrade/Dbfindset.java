package com.uas.erp.model.upgrade;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.uas.erp.dao.Saveable;

/**
 * 更新升级涉及的dbfindset
 * 
 * @author yingp
 *
 */
public class Dbfindset implements Saveable{

	private String ds_id;
	private String ds_caller;
	private String ds_tablename;
	private String ds_searchcaller;
	private String ds_caption;
	private String ds_groupby;
	private String ds_orderby;
	private String ds_fixedcondition;
	private Short ds_allownew;
	private Short ds_allowedit;
	private Short ds_allowfilt;
	private Short ds_findall;
	private Short ds_gstcodefield;
	private Short ds_gstnextcodefield;
	private Short ds_gstdetno;
	private Short ds_outcondition;
	private Short ds_distinct;
	private Short ds_multichoose;
	private Integer ds_pagesize;
	private String ds_popedommodule;
	private String ds_recordfield;
	private String ds_copfield;
	private Short ds_copdatabase;
	private String ds_caption_en;
	private String ds_caption_fan;
	private String ds_enid;
	private String ds_error;
	private Short ds_allowreset;
	private Short ds_autoheight;

	private List<DbfindsetDetail> dbFindSetDetails;

	private String plan_id;

	@JsonIgnore
	public String getDs_id() {
		return ds_id;
	}

	public void setDs_id(String ds_id) {
		this.ds_id = ds_id;
	}

	public String getDs_caller() {
		return ds_caller;
	}

	public void setDs_caller(String ds_caller) {
		this.ds_caller = ds_caller;
	}

	public String getDs_tablename() {
		return ds_tablename;
	}

	public void setDs_tablename(String ds_tablename) {
		this.ds_tablename = ds_tablename;
	}

	public String getDs_searchcaller() {
		return ds_searchcaller;
	}

	public void setDs_searchcaller(String ds_searchcaller) {
		this.ds_searchcaller = ds_searchcaller;
	}

	public String getDs_caption() {
		return ds_caption;
	}

	public void setDs_caption(String ds_caption) {
		this.ds_caption = ds_caption;
	}

	public String getDs_groupby() {
		return ds_groupby;
	}

	public void setDs_groupby(String ds_groupby) {
		this.ds_groupby = ds_groupby;
	}

	public String getDs_orderby() {
		return ds_orderby;
	}

	public void setDs_orderby(String ds_orderby) {
		this.ds_orderby = ds_orderby;
	}

	public String getDs_fixedcondition() {
		return ds_fixedcondition;
	}

	public void setDs_fixedcondition(String ds_fixedcondition) {
		this.ds_fixedcondition = ds_fixedcondition;
	}

	public Short getDs_allownew() {
		return ds_allownew;
	}

	public void setDs_allownew(Short ds_allownew) {
		this.ds_allownew = ds_allownew;
	}

	public Short getDs_allowedit() {
		return ds_allowedit;
	}

	public void setDs_allowedit(Short ds_allowedit) {
		this.ds_allowedit = ds_allowedit;
	}

	public Short getDs_allowfilt() {
		return ds_allowfilt;
	}

	public void setDs_allowfilt(Short ds_allowfilt) {
		this.ds_allowfilt = ds_allowfilt;
	}

	public Short getDs_findall() {
		return ds_findall;
	}

	public void setDs_findall(Short ds_findall) {
		this.ds_findall = ds_findall;
	}

	public Short getDs_gstcodefield() {
		return ds_gstcodefield;
	}

	public void setDs_gstcodefield(Short ds_gstcodefield) {
		this.ds_gstcodefield = ds_gstcodefield;
	}

	public Short getDs_gstnextcodefield() {
		return ds_gstnextcodefield;
	}

	public void setDs_gstnextcodefield(Short ds_gstnextcodefield) {
		this.ds_gstnextcodefield = ds_gstnextcodefield;
	}

	public Short getDs_gstdetno() {
		return ds_gstdetno;
	}

	public void setDs_gstdetno(Short ds_gstdetno) {
		this.ds_gstdetno = ds_gstdetno;
	}

	public Short getDs_outcondition() {
		return ds_outcondition;
	}

	public void setDs_outcondition(Short ds_outcondition) {
		this.ds_outcondition = ds_outcondition;
	}

	public Short getDs_distinct() {
		return ds_distinct;
	}

	public void setDs_distinct(Short ds_distinct) {
		this.ds_distinct = ds_distinct;
	}

	public Short getDs_multichoose() {
		return ds_multichoose;
	}

	public void setDs_multichoose(Short ds_multichoose) {
		this.ds_multichoose = ds_multichoose;
	}

	public Integer getDs_pagesize() {
		return ds_pagesize;
	}

	public void setDs_pagesize(Integer ds_pagesize) {
		this.ds_pagesize = ds_pagesize;
	}

	public String getDs_popedommodule() {
		return ds_popedommodule;
	}

	public void setDs_popedommodule(String ds_popedommodule) {
		this.ds_popedommodule = ds_popedommodule;
	}

	public String getDs_recordfield() {
		return ds_recordfield;
	}

	public void setDs_recordfield(String ds_recordfield) {
		this.ds_recordfield = ds_recordfield;
	}

	public String getDs_copfield() {
		return ds_copfield;
	}

	public void setDs_copfield(String ds_copfield) {
		this.ds_copfield = ds_copfield;
	}

	public Short getDs_copdatabase() {
		return ds_copdatabase;
	}

	public void setDs_copdatabase(Short ds_copdatabase) {
		this.ds_copdatabase = ds_copdatabase;
	}

	public String getDs_caption_en() {
		return ds_caption_en;
	}

	public void setDs_caption_en(String ds_caption_en) {
		this.ds_caption_en = ds_caption_en;
	}

	public String getDs_caption_fan() {
		return ds_caption_fan;
	}

	public void setDs_caption_fan(String ds_caption_fan) {
		this.ds_caption_fan = ds_caption_fan;
	}

	public String getDs_enid() {
		return ds_enid;
	}

	public void setDs_enid(String ds_enid) {
		this.ds_enid = ds_enid;
	}

	public String getDs_error() {
		return ds_error;
	}

	public void setDs_error(String ds_error) {
		this.ds_error = ds_error;
	}

	public Short getDs_allowreset() {
		return ds_allowreset;
	}

	public void setDs_allowreset(Short ds_allowreset) {
		this.ds_allowreset = ds_allowreset;
	}

	public Short getDs_autoheight() {
		return ds_autoheight;
	}

	public void setDs_autoheight(Short ds_autoheight) {
		this.ds_autoheight = ds_autoheight;
	}

	public List<DbfindsetDetail> getDbFindSetDetails() {
		return dbFindSetDetails;
	}

	public void setDbFindSetDetails(List<DbfindsetDetail> dbFindSetDetails) {
		this.dbFindSetDetails = dbFindSetDetails;
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
		return "upgrade$dbfindset";
	}

	@Override
	public String[] keyColumns() {
		return null;
	}

}
