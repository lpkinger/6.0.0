package com.uas.erp.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.SqlMap;

public class ExecModel {
	private String ExecStatement;
    private boolean isSELECT=false;
	private boolean isDELETE=false;
	private boolean isUPDATE=false;
	private boolean isINSERT=false;
	private boolean isDLL=false;
	private boolean isBLOCK=false;
    private int MAXSIZE=5000;//不考虑分页只能查询前5000条结果集

	public String getExecStatement() {
		return ExecStatement;
	}


	public void setExecStatement(String execStatement) {
		ExecStatement = execStatement;
	}


	public boolean isDELETE() {
		return isDELETE;
	}


	public void setDELETE(boolean isDELETE) {
		this.isDELETE = isDELETE;
	}


	public boolean isUPDATE() {
		return isUPDATE;
	}


	public void setUPDATE(boolean isUPDATE) {
		this.isUPDATE = isUPDATE;
	}


	public boolean isINSERT() {
		return isINSERT;
	}


	public void setINSERT(boolean isINSERT) {
		this.isINSERT = isINSERT;
	}


	public boolean isDLL() {
		return isDLL;
	}


	public void setDLL(boolean isDLL) {
		this.isDLL = isDLL;
	}


	public boolean isBLOCK() {
		return isBLOCK;
	}


	public void setBLOCK(boolean isBLOCK) {
		this.isBLOCK = isBLOCK;
	}
	
    public boolean isSELECT() {
		return isSELECT;
	}


	public void setSELECT(boolean isSELECT) {
		this.isSELECT = isSELECT;
	}


	public ExecModel(){
    	
    }
    
    public ExecModel(String ExecStatement){
    	this.ExecStatement=ExecStatement;
    }
    
	public void parse(){
		/**
		 * 暂不考虑语句块执行，只支持创建简单视图和查询
		 * */
		String upperStatement=this.ExecStatement=this.ExecStatement.toUpperCase().replaceAll("(\n|\t|;)", " ");
		isBLOCK =upperStatement.matches("(BENGIN|EXECUTE).+(END);*");
		isINSERT =upperStatement.matches(".*(INSERT)\\s+(INTO).+");
		isUPDATE =upperStatement.matches(".*(UPDATE)\\s+.+\\s+(SET).+");
		isDELETE =upperStatement.matches(".*(DELETE)\\s+.*");
		isSELECT= upperStatement.matches("(SELECT)\\s+.*\\s*(FROM).+");
		isDLL    =upperStatement.matches(".*(CREATE|ALTER|DROP|GRANT)\\s+.*");	

	}
	public String  getCountSql(String statement){
	   return " SELECT  COUNT(1) FROM  ("+statement+")";
	}
	public String getLimitSql(){
	  return "SELECT *  FROM ("+this.getExecStatement()+") WHERE ROWNUM<="+MAXSIZE;
	}
	
	public boolean checkValid(){
	   if(this.isBLOCK || isINSERT || isUPDATE || isDELETE)	 return false;
	   if(this.isDLL){
		   /**
		    * 目前仅允许创建视图
		    * */
		  return this.ExecStatement.matches(".*(CREATE)\\s+(OR\\s+REPLACE)?\\s+(FORCE|NOFORCE){0,1}+\\s+(VIEW).+");
	   }
	   return true;
	}
	public SqlMap getLogSql(String man,String status){
		SqlMap map=new SqlMap("DEVLOG");
		map.set("SQL_TEXT",this.getExecStatement());
		map.set("LOG_MAN",man);
		map.set("LOG_DATE",new Date());
		map.set("STATUS_", status);
	    return map;
	}
}
