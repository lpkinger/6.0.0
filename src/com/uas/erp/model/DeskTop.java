package com.uas.erp.model;

import java.io.Serializable;

import com.uas.erp.dao.Saveable;

public class DeskTop  implements Serializable,Saveable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String  xtype_;
	private Integer detno_;
	private Integer count_;
	private Integer emid_;
	public String getXtype_() {
		return xtype_;
	}
	public void setXtype_(String xtype_) {
		this.xtype_ = xtype_;
	}
	public Integer getDetno_() {
		return detno_;
	}
	public void setDetno_(Integer detno_) {
		this.detno_ = detno_;
	}
	public Integer getCount_() {
		return count_;
	}
	public void setCount_(Integer count_) {
		this.count_ = count_;
	}
	public Integer getEmid_() {
		return emid_;
	}
	public void setEmid_(Integer emid_) {
		this.emid_ = emid_;
	}
	public DeskTop(){

	}
	public DeskTop(String xtype_,Integer detno_,Integer count_,Integer emid_){
		this.xtype_=xtype_;
		this.detno_=detno_;
		this.count_=count_;
		this.emid_=emid_;
	}
	@Override
	public String table() {
		// TODO Auto-generated method stub
		return "DeskTop";
	}
	@Override
	public String[] keyColumns() {
		// TODO Auto-generated method stub
		return new String[]{};
	}

}
