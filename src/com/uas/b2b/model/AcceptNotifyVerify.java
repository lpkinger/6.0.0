package com.uas.b2b.model;

import java.util.Date;

/**
 * 已检验的收料单
 * @author suntg
 *
 */
public class AcceptNotifyVerify {

	private Long vad_id;
	private Long b2b_ss_id;
	private short and_detno;
	private Double vad_jyqty;
	private String ve_code;
	private Double ve_okqty;
	private Double ve_notokqty;
	private String ve_testman;
	private Date ve_date;
	
	public Long getB2b_ss_id() {
		return b2b_ss_id;
	}
	public void setB2b_ss_id(Long b2b_ss_id) {
		this.b2b_ss_id = b2b_ss_id;
	}
	public short getAnd_detno() {
		return and_detno;
	}
	public void setAnd_detno(short and_detno) {
		this.and_detno = and_detno;
	}
	public Long getVad_id() {
		return vad_id;
	}
	public void setVad_id(Long vad_id) {
		this.vad_id = vad_id;
	}
	public Double getVad_jyqty() {
		return vad_jyqty;
	}
	public void setVad_jyqty(Double vad_jyqty) {
		this.vad_jyqty = vad_jyqty;
	}
	public String getVe_code() {
		return ve_code;
	}
	public void setVe_code(String ve_code) {
		this.ve_code = ve_code;
	}
	public Double getVe_okqty() {
		return ve_okqty;
	}
	public void setVe_okqty(Double ve_okqty) {
		this.ve_okqty = ve_okqty;
	}
	public Double getVe_notokqty() {
		return ve_notokqty;
	}
	public void setVe_notokqty(Double ve_notokqty) {
		this.ve_notokqty = ve_notokqty;
	}
	public String getVe_testman() {
		return ve_testman;
	}
	public void setVe_testman(String ve_testman) {
		this.ve_testman = ve_testman;
	}
	public Date getVe_date() {
		return ve_date;
	}
	public void setVe_date(Date ve_date) {
		this.ve_date = ve_date;
	}

	

}
