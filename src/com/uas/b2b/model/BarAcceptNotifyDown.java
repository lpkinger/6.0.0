package com.uas.b2b.model;


/**
 * 供应商方，ERP系统的条码发货通知
 * @author aof
 * @date 2015年10月30日
 */
public class BarAcceptNotifyDown {
	private Long bad_id;
	private String bad_barcode;
	private Long bad_ssid;
	private String bad_sscode;
	private Long bad_siid;
	private Short bad_sinumber;
	private String bad_prodcode;
	private Long bad_prodid;
	private Double bad_qty;
	private Long bad_vendcode;
	private String bad_outboxcode;
	private Long bad_outboxid;
	private String bad_status;
	
	public Long getBad_id() {
		return bad_id;
	}
	public void setBad_id(Long bad_id) {
		this.bad_id = bad_id;
	}
	public String getBad_barcode() {
		return bad_barcode;
	}
	public void setBad_barcode(String bad_barcode) {
		this.bad_barcode = bad_barcode;
	}
	public Long getBad_ssid() {
		return bad_ssid;
	}
	public void setBad_ssid(Long bad_ssid) {
		this.bad_ssid = bad_ssid;
	}
	public String getBad_sscode() {
		return bad_sscode;
	}
	public void setBad_sscode(String bad_sscode) {
		this.bad_sscode = bad_sscode;
	}
	public Long getBad_siid() {
		return bad_siid;
	}
	public void setBad_siid(Long bad_siid) {
		this.bad_siid = bad_siid;
	}
	public Short getBad_sinumber() {
		return bad_sinumber;
	}
	public void setBad_sinumber(Short bad_sinumber) {
		this.bad_sinumber = bad_sinumber;
	}
	public String getBad_prodcode() {
		return bad_prodcode;
	}
	public void setBad_prodcode(String bad_prodcode) {
		this.bad_prodcode = bad_prodcode;
	}
	public Long getBad_prodid() {
		return bad_prodid;
	}
	public void setBad_prodid(Long bad_prodid) {
		this.bad_prodid = bad_prodid;
	}
	public Double getBad_qty() {
		return bad_qty;
	}
	public void setBad_qty(Double bad_qty) {
		this.bad_qty = bad_qty;
	}
	public Long getBad_vendcode() {
		return bad_vendcode;
	}
	public void setBad_vendcode(Long bad_vendcode) {
		this.bad_vendcode = bad_vendcode;
	}
	public String getBad_outboxcode() {
		return bad_outboxcode;
	}
	public void setBad_outboxcode(String bad_outboxcode) {
		this.bad_outboxcode = bad_outboxcode;
	}
	public Long getBad_outboxid() {
		return bad_outboxid;
	}
	public void setBad_outboxid(Long bad_outboxid) {
		this.bad_outboxid = bad_outboxid;
	}
	public String getBad_status() {
		return bad_status;
	}
	public void setBad_status(String bad_status) {
		this.bad_status = bad_status;
	}
	/**
	 * 数据转化 
	 */
	public BarAcceptNotify conver(){
		BarAcceptNotify barAcceptNotify = new BarAcceptNotify();
		barAcceptNotify.setBcn_barcode(this.bad_barcode);
		barAcceptNotify.setBcn_prodcode(this.bad_prodcode);
		barAcceptNotify.setBcn_prodid(this.bad_prodid);
		barAcceptNotify.setBcn_qty(this.bad_qty);
		barAcceptNotify.setBcn_vendcode(this.bad_vendcode);
		barAcceptNotify.setBcn_outboxcode(this.bad_outboxcode);
		barAcceptNotify.setBcn_outboxid(this.bad_outboxid);
		barAcceptNotify.setBcn_status(this.bad_status);
		return barAcceptNotify;
	}
	
}
