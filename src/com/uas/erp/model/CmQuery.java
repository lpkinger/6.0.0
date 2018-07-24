package com.uas.erp.model;

import java.io.Serializable;

public class CmQuery implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int cm_id;
	int cm_yearmonth;
	String cm_custcode;
	String cu_name;
	String cm_currency;
	float cm_beginamount;
	float cm_nowamount;
	float cm_payamount;
	float cm_endamount;
	float cm_prepayamount;
	float cm_over;
	float cm_justnow;
	float cm_next;
	float cm_beginlast;

	public int getCm_id() {
		return cm_id;
	}

	public void setCm_id(int cm_id) {
		this.cm_id = cm_id;
	}

	public int getCm_yearmonth() {
		return cm_yearmonth;
	}

	public void setCm_yearmonth(int cm_yearmonth) {
		this.cm_yearmonth = cm_yearmonth;
	}

	public String getCm_custcode() {
		return cm_custcode;
	}

	public void setCm_custcode(String cm_custcode) {
		this.cm_custcode = cm_custcode;
	}

	public String getCu_name() {
		return cu_name;
	}

	public void setCu_name(String cu_name) {
		this.cu_name = cu_name;
	}

	public String getCm_currency() {
		return cm_currency;
	}

	public void setCm_currency(String cm_currency) {
		this.cm_currency = cm_currency;
	}

	public float getCm_beginamount() {
		return cm_beginamount;
	}

	public void setCm_beginamount(float cm_beginamount) {
		this.cm_beginamount = cm_beginamount;
	}

	public float getCm_nowamount() {
		return cm_nowamount;
	}

	public void setCm_nowamount(float cm_nowamount) {
		this.cm_nowamount = cm_nowamount;
	}

	public float getCm_payamount() {
		return cm_payamount;
	}

	public void setCm_payamount(float cm_payamount) {
		this.cm_payamount = cm_payamount;
	}

	public float getCm_endamount() {
		return cm_endamount;
	}

	public void setCm_endamount(float cm_endamount) {
		this.cm_endamount = cm_endamount;
	}

	public float getCm_prepayamount() {
		return cm_prepayamount;
	}

	public void setCm_prepayamount(float cm_prepayamount) {
		this.cm_prepayamount = cm_prepayamount;
	}

	public float getCm_over() {
		return cm_over;
	}

	public void setCm_over(float cm_over) {
		this.cm_over = cm_over;
	}

	public float getCm_justnow() {
		return cm_justnow;
	}

	public void setCm_justnow(float cm_justnow) {
		this.cm_justnow = cm_justnow;
	}

	public float getCm_next() {
		return cm_next;
	}

	public void setCm_next(float cm_next) {
		this.cm_next = cm_next;
	}

	public float getCm_beginlast() {
		return cm_beginlast;
	}

	public void setCm_beginlast(float cm_beginlast) {
		this.cm_beginlast = cm_beginlast;
	}

}
