package com.uas.b2b.model;

import java.util.Date;

import com.uas.erp.core.support.KeyEntity;

/**
 * 条码产生规则表模型
 * @author aof
 * @date 2015年9月11日
 */
public class BarcodeSet extends KeyEntity {
	
	private Long bs_id;
	private String bs_type;
	private Long bs_lenprid;
	private Long bs_lenveid;
	private String bs_datestr;
	private Long bs_lennum;
	private String bs_split;
	private String bs_combine;
	private String bs_code;
	private String bs_name;
	private String bs_statuscode;
	private String bs_status;
	private String bs_jitype;
	private String bs_ifcommon;
	private Long bs_totallen;
	private Date bs_date;
	private String bs_recorder;
	private String bs_prtypepreix ;
	private String bs_fromerp ;
	private String bs_fromplat;
	private String bs_sendstatus;
	
	public Long getBs_id() {
		return bs_id;
	}

	public void setBs_id(Long bs_id) {
		this.bs_id = bs_id;
	}

	public String getBs_type() {
		return bs_type;
	}

	public void setBs_type(String bs_type) {
		this.bs_type = bs_type;
	}

	public Long getBs_lenprid() {
		return bs_lenprid;
	}

	public void setBs_lenprid(Long bs_lenprid) {
		this.bs_lenprid = bs_lenprid;
	}

	public Long getBs_lenveid() {
		return bs_lenveid;
	}

	public void setBs_lenveid(Long bs_lenveid) {
		this.bs_lenveid = bs_lenveid;
	}

	public String getBs_datestr() {
		return bs_datestr;
	}

	public void setBs_datestr(String bs_datestr) {
		this.bs_datestr = bs_datestr;
	}

	public Long getBs_lennum() {
		return bs_lennum;
	}

	public void setBs_lennum(Long bs_lennum) {
		this.bs_lennum = bs_lennum;
	}

	public String getBs_split() {
		return bs_split;
	}

	public void setBs_split(String bs_split) {
		this.bs_split = bs_split;
	}

	public String getBs_combine() {
		return bs_combine;
	}

	public void setBs_combine(String bs_combine) {
		this.bs_combine = bs_combine;
	}

	public String getBs_code() {
		return bs_code;
	}

	public void setBs_code(String bs_code) {
		this.bs_code = bs_code;
	}

	public String getBs_name() {
		return bs_name;
	}

	public void setBs_name(String bs_name) {
		this.bs_name = bs_name;
	}

	public String getBs_statuscode() {
		return bs_statuscode;
	}

	public void setBs_statuscode(String bs_statuscode) {
		this.bs_statuscode = bs_statuscode;
	}

	public String getBs_status() {
		return bs_status;
	}

	public void setBs_status(String bs_status) {
		this.bs_status = bs_status;
	}

	public String getBs_jitype() {
		return bs_jitype;
	}

	public void setBs_jitype(String bs_jitype) {
		this.bs_jitype = bs_jitype;
	}

	public String getbs_ifcommon() {
		return bs_ifcommon;
	}

	public void setbs_ifcommon(String bs_ifcommon) {
		this.bs_ifcommon = bs_ifcommon;
	}

	public Long getBs_totallen() {
		return bs_totallen;
	}

	public void setBs_totallen(Long bs_totallen) {
		this.bs_totallen = bs_totallen;
	}

	public Date getBs_date() {
		return bs_date;
	}

	public void setBs_date(Date bs_date) {
		this.bs_date = bs_date;
	}

	public String getBs_recorder() {
		return bs_recorder;
	}

	public void setBs_recorder(String bs_recorder) {
		this.bs_recorder = bs_recorder;
	}

	public String getBs_prtypepreix() {
		return bs_prtypepreix;
	}

	public void setBs_prtypepreix(String bs_prtypepreix) {
		this.bs_prtypepreix = bs_prtypepreix;
	}

	public String getBs_fromerp() {
		return bs_fromerp;
	}

	public void setBs_fromerp(String bs_fromerp) {
		this.bs_fromerp = bs_fromerp;
	}

	public String getBs_fromplat() {
		return bs_fromplat;
	}

	public void setBs_fromplat(String bs_fromplat) {
		this.bs_fromplat = bs_fromplat;
	}

	public String getBs_sendstatus() {
		return bs_sendstatus;
	}

	public void setBs_sendstatus(String bs_sendstatus) {
		this.bs_sendstatus = bs_sendstatus;
	}

	@Override
	public Object getKey() {
		return this.bs_id;
	}
	
}
