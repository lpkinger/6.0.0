package com.uas.erp.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.uas.erp.core.StringUtil;

import net.sf.json.JSONObject;

public class ProjectTask implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int prjplanid;
	private String recorder;
	private Date recorddate;
	private String baselineenddate;
	private int baselinepercentdone;
	private String baselinestartdate;
	private String calendarid;
	private String duration;
	private String durationunit;
	private String EndDate;
	private String name;
	private String resourcename;
	private int percentdone;
	private String StartDate;
	private String tasktype;
	private String taskcolor;
	private int Id;
	private boolean leaf;
	private int parentid;
	private String description;
	private String prjname;
	private String taskcode;
	private String isfirst;
	private String islast;
	private String solution;
	private String delayreason;
	private String handstatus;
	private String ManuallyScheduled;
	private String phasename;
	private String phaseid;
	private String realstartdate;
	private String realenddate;
	private String prjdocname;
	private String prjdocid;
	private String prjdocstatus;
	private String status;
	private String preconditioncode;
	private String preconditionname;
	private String backconditioncode;
	private String backconditionname;
	
	

	public String getBackconditioncode() {
		return backconditioncode;
	}
	public void setBackconditioncode(String backconditioncode) {
		this.backconditioncode = backconditioncode;
	}
	public String getBackconditionname() {
		return backconditionname;
	}
	public void setBackconditionname(String backconditionname) {
		this.backconditionname = backconditionname;
	}
	public String getPreconditioncode() {
		return preconditioncode;
	}
	public void setPreconditioncode(String preconditioncode) {
		this.preconditioncode = preconditioncode;
	}
	public String getPreconditionname() {
		return preconditionname;
	}
	public void setPreconditionname(String preconditionname) {
		this.preconditionname = preconditionname;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	private List<JSONObject> children;
	public int getPrjplanid() {
		return prjplanid;
	}
	public void setPrjplanid(int prjplanid) {
		this.prjplanid = prjplanid;
	}
	public String getRecorder() {
		return recorder;
	}
	public void setRecorder(String recorder) {
		this.recorder = recorder;
	}
	public Date getRecorddate() {
		return recorddate;
	}
	public void setRecorddate(Date recorddate) {
		this.recorddate = recorddate;
	}
	public int getBaselinepercentdone() {
		return baselinepercentdone;
	}
	public void setBaselinepercentdone(int baselinepercentdone) {
		this.baselinepercentdone = baselinepercentdone;
	}	
	public String getStartDate() {
		return StartDate;
	}
	public String getBaselineenddate() {
		return baselineenddate;
	}
	public void setBaselineenddate(String baselineenddate) {
		this.baselineenddate = baselineenddate;
	}
	public String getBaselinestartdate() {
		return baselinestartdate;
	}
	public void setBaselinestartdate(String baselinestartdate) {
		this.baselinestartdate = baselinestartdate;
	}
	public String getCalendarid() {
		return calendarid;
	}
	public void setCalendarid(String calendarid) {
		this.calendarid = calendarid;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getDurationunit() {
		return durationunit;
	}
	public void setDurationunit(String durationunit) {
		this.durationunit = durationunit;
	}
	public int getPercentdone() {
		return percentdone;
	}
	public void setPercentdone(int percentdone) {
		this.percentdone = percentdone;
	}

	public String getTasktype() {
		return tasktype;
	}
	public void setTasktype(String tasktype) {
		this.tasktype = tasktype;
	}
	public String getTaskcolor() {
		return taskcolor;
	}
	public void setTaskcolor(String taskcolor) {
		this.taskcolor = taskcolor;
	}
	public String getEndDate() {
		return EndDate;
	}
	public void setEndDate(String endDate) {
		this.EndDate = endDate;
	}
	public void setStartDate(String startDate) {
		this.StartDate = startDate;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public boolean isLeaf() {
		return leaf;
	}
	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}
	public int getParentid() {
		return parentid;
	}
	public void setParentid(int parentid) {
		this.parentid = parentid;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPrjname() {
		return prjname;
	}
	public void setPrjname(String prjname) {
		this.prjname = prjname;
	}
	public String getTaskcode() {
		return taskcode;
	}
	public void setTaskcode(String taskcode) {
		this.taskcode = taskcode;
	}
	public String getIsfirst() {
		return isfirst;
	}
	public void setIsfirst(String isfirst) {
		this.isfirst = isfirst;
	}
	public String getIslast() {
		return islast;
	}
	public void setIslast(String islast) {
		this.islast = islast;
	}
	public String getSolution() {
		return solution;
	}
	public void setSolution(String solution) {
		this.solution = solution;
	}
	public String getDelayreason() {
		return delayreason;
	}
	public void setDelayreason(String delayreason) {
		this.delayreason = delayreason;
	}	
	public String getResourcename() {
		return resourcename;
	}
	public void setResourcename(String resourcename) {
		this.resourcename = resourcename;
	}
	
	public List<JSONObject> getChildren() {
		return children;
	}
	public void setChildren(List<JSONObject> children) {
		this.children = children;
	}
	
	public String getHandstatus() {
		return handstatus;
	}
	public void setHandstatus(String handstatus) {
		this.handstatus = handstatus;
	}
	
	public String getPhasename() {
		return phasename;
	}
	
	public String getPhaseid() {
		return phaseid;
	}
	
	public void setPhaseid(String phaseid) {
		this.phaseid = phaseid;
	}
	public void setPhasename(String phasename) {
		this.phasename = phasename;
	}
	public String getRealstartdate() {
		return realstartdate;
	}
	public void setRealstartdate(String realstartdate) {
		this.realstartdate = realstartdate;
	}
	public String getRealenddate() {
		return realenddate;
	}
	public void setRealenddate(String realenddate) {
		this.realenddate = realenddate;
	}
	
	public String getPrjdocname() {
		return prjdocname;
	}
	public void setPrjdocname(String prjdocname) {
		this.prjdocname = prjdocname;
	}
	public String getPrjdocid() {
		return prjdocid;
	}
	public void setPrjdocid(String prjdocid) {
		this.prjdocid = prjdocid;
	}
	
	public String getPrjdocstatus() {
		return prjdocstatus;
	}
	public void setPrjdocstatus(String prjdocstatus) {
		this.prjdocstatus = prjdocstatus;
	}
	public JSONObject getGantData(String live){
		JSONObject o=new JSONObject();
		/**gant框架 约定的字段name*/
		o.put("Name",this.name);
		o.put("Id", this.Id);
		o.put("EndDate",this.EndDate);
		o.put("StartDate",this.StartDate);
		if (StringUtil.hasText(live) && "true".equals(live)) {
			 o.put("BaselineStartDate", this.StartDate);
			 o.put("BaselineEndDate", this.EndDate);   
		} else {
			  o.put("BaselineStartDate", this.baselinestartdate);
			  o.put("BaselineEndDate", this.baselineenddate);   
		}
	     o.put("BaselinePercentdone", this.baselinepercentdone);
	    o.put("children",this.children);
		o.put("Duration",this.duration);
		o.put("PercentDone", this.percentdone);
		o.put("leaf", this.leaf);
		o.put("expanded", !this.leaf);
		o.put("phasename",this.phasename);
		o.put("phaseid",this.phaseid);
		o.put("handstatus", this.handstatus);
		o.put("ManuallyScheduled","true".equals(this.getManuallyScheduled()));
		o.put("realstartdate",this.realstartdate);
		o.put("realenddate", this.realenddate);
		o.put("prjdocid", this.prjdocid);
		o.put("prjdocname",this.prjdocname);
		o.put("prjdocstatus",this.prjdocstatus);
		o.put("tasktype", this.tasktype);
		o.put("preconditioncode", this.preconditioncode);
		o.put("preconditionname",this.preconditionname);
		o.put("backconditioncode", this.backconditioncode);
		o.put("backconditionname",this.backconditionname);
		return o;
	}
	
	public String getManuallyScheduled() {
		return ManuallyScheduled;
	}
	public void setManuallyScheduled(String manuallyScheduled) {
		ManuallyScheduled = manuallyScheduled;
	}

}
