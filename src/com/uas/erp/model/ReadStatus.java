package com.uas.erp.model;

import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.Saveable;

public class ReadStatus implements Saveable{
	private Integer status; //-1 已读,0 未读
	private Integer man;//操作人id
	private Integer mainid;//关联主键id
	private String sourcekind;//来源类型	
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getMan() {
		return man;
	}
	public void setMan(Integer man) {
		this.man = man;
	}
	public Integer getMainid() {
		return mainid;
	}
	public void setMainid(Integer mainid) {
		this.mainid = mainid;
	}
	public String getSourcekind() {
		return sourcekind;
	}
	public void setSourcekind(String sourcekind) {
		this.sourcekind = sourcekind;
	}
	public ReadStatus(){
		
	}
	public ReadStatus(String sourcekind,Integer mainid){
		this.status=-1;
		this.man=SystemSession.getUser().getEm_id();
		this.mainid=mainid;
		this.sourcekind=sourcekind;
	}
	public ReadStatus(Integer status,Integer man,Integer mainid,String sourcekind){
		this.status=status;
		this.man=man;
		this.mainid=mainid;
		this.sourcekind=sourcekind;
	}
	@Override
	public String table() {
		// TODO Auto-generated method stub
		return this.getClass().getSimpleName();
	}
	@Override
	public String[] keyColumns() {
		// TODO Auto-generated method stub
		return null;
	}

}
