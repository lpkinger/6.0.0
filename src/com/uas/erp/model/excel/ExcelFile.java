package com.uas.erp.model.excel;

import java.io.Serializable;
import java.util.*;

import com.uas.erp.dao.Saveable;

public class ExcelFile implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 396019713269329894L;
	
	private Integer fileid;
	
	private String filename;
	
	private String filedesc;
	
	private String filecolor;
	
	private Date filecreatetime;
	
	private Date fileupdatetime;
	
	private String fileman;
	
	private String filestatus;
	
	private String filestatuscode;
	
	private String fileversion;
	
	private Boolean fileuse;
	
	private Integer filetplsource;
	
	private Integer fileversionsource;
	
	public ExcelFile() {
	
	}

	public Integer getFileid() {
		return fileid;
	}

	public void setFileid(Integer fileid) {
		this.fileid = fileid;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFiledesc() {
		return filedesc;
	}

	public void setFiledesc(String filedesc) {
		this.filedesc = filedesc;
	}

	public String getFilecolor() {
		return filecolor;
	}

	public void setFilecolor(String filecolor) {
		this.filecolor = filecolor;
	}

	public Date getFilecreatetime() {
		return filecreatetime;
	}

	public void setFilecreatetime(Date filecreatetime) {
		this.filecreatetime = filecreatetime;
	}

	public Date getFileupdatetime() {
		return fileupdatetime;
	}

	public void setFileupdatetime(Date fileupdatetime) {
		this.fileupdatetime = fileupdatetime;
	}

	public String getFileman() {
		return fileman;
	}

	public void setFileman(String fileman) {
		this.fileman = fileman;
	}

	public String getFilestatus() {
		return filestatus;
	}

	public void setFilestatus(String filestatus) {
		this.filestatus = filestatus;
	}

	public String getFilestatuscode() {
		return filestatuscode;
	}

	public void setFilestatuscode(String filestatuscode) {
		this.filestatuscode = filestatuscode;
	}

	public String getFileversion() {
		return fileversion;
	}

	public void setFileversion(String fileversion) {
		this.fileversion = fileversion;
	}

	public Boolean getFileuse() {
		return fileuse;
	}

	public void setFileuse(Boolean fileuse) {
		this.fileuse = fileuse;
	}

	public Integer getFiletplsource() {
		return filetplsource;
	}

	public void setFiletplsource(Integer filetplsource) {
		this.filetplsource = filetplsource;
	}

	public Integer getFileversionsource() {
		return fileversionsource;
	}

	public void setFileversionsource(Integer fileversionsource) {
		this.fileversionsource = fileversionsource;
	}

	@Override
	public String toString() {
		return "ExcelFile [fileid=" + fileid + ", filename=" + filename + ", filedesc=" + filedesc + ", filecolor="
				+ filecolor + ", filecreatetime=" + filecreatetime + ", fileupdatetime=" + fileupdatetime + ", fileman="
				+ fileman + ", filestatus=" + filestatus + ", filestatuscode=" + filestatuscode + ", fileversion="
				+ fileversion + ", fileuse=" + fileuse + ", filetplsource=" + filetplsource + ", fileversionsource="
				+ fileversionsource + "]";
	}


}
