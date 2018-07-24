package com.uas.erp.model.excel;

import java.io.Serializable;
import java.util.*;

import com.uas.erp.dao.Saveable;

public class ExcelFileTemplate implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7836305138496295385L;
	
	private Integer fileid_tpl;
	
	private String filename_tpl;
	
	private String filedesc_tpl;
	
	private String filecolor_tpl;
	
	private Integer filesubof_tpl;
	
	private Date filecreatetime_tpl;
	
	private Date fileupdatetime_tpl;
	
	private String fileman_tpl;
	
	private Boolean filecategory_tpl;
	
	private String filevirtualpath_tpl;
	
	private String filecaller_tpl;
	
	
	public ExcelFileTemplate() {
	
	}

	public Integer getFileid_tpl() {
		return fileid_tpl;
	}

	public void setFileid_tpl(Integer fileid_tpl) {
		this.fileid_tpl = fileid_tpl;
	}

	public String getFilename_tpl() {
		return filename_tpl;
	}

	public void setFilename_tpl(String filename_tpl) {
		this.filename_tpl = filename_tpl;
	}

	public String getFiledesc_tpl() {
		return filedesc_tpl;
	}

	public void setFiledesc_tpl(String filedesc_tpl) {
		this.filedesc_tpl = filedesc_tpl;
	}

	public String getFilecolor_tpl() {
		return filecolor_tpl;
	}

	public void setFilecolor_tpl(String filecolor_tpl) {
		this.filecolor_tpl = filecolor_tpl;
	}

	public Integer getFilesubof_tpl() {
		return filesubof_tpl;
	}

	public void setFilesubof_tpl(Integer filesubof_tpl) {
		this.filesubof_tpl = filesubof_tpl;
	}

	public Date getFilecreatetime_tpl() {
		return filecreatetime_tpl;
	}

	public void setFilecreatetime_tpl(Date filecreatetime_tpl) {
		this.filecreatetime_tpl = filecreatetime_tpl;
	}

	public Date getFileupdatetime_tpl() {
		return fileupdatetime_tpl;
	}

	public void setFileupdatetime_tpl(Date fileupdatetime_tpl) {
		this.fileupdatetime_tpl = fileupdatetime_tpl;
	}

	public String getFileman_tpl() {
		return fileman_tpl;
	}

	public void setFileman_tpl(String fileman_tpl) {
		this.fileman_tpl = fileman_tpl;
	}

	public Boolean getFilecategory_tpl() {
		return filecategory_tpl;
	}

	public void setFilecategory_tpl(Boolean filecategory_tpl) {
		this.filecategory_tpl = filecategory_tpl;
	}

	public String getFilevirtualpath_tpl() {
		return filevirtualpath_tpl;
	}

	public void setFilevirtualpath_tpl(String filevirtualpath_tpl) {
		this.filevirtualpath_tpl = filevirtualpath_tpl;
	}

	public String getFilecaller_tpl() {
		return filecaller_tpl;
	}

	public void setFilecaller_tpl(String filecaller_tpl) {
		this.filecaller_tpl = filecaller_tpl;
	}

	@Override
	public String toString() {
		return "ExcelFileTemplate [fileid_tpl=" + fileid_tpl + ", filename_tpl=" + filename_tpl + ", filedesc_tpl="
				+ filedesc_tpl + ", filecolor_tpl=" + filecolor_tpl + ", filesubof_tpl=" + filesubof_tpl
				+ ", filecreatetime_tpl=" + filecreatetime_tpl + ", fileupdatetime_tpl=" + fileupdatetime_tpl
				+ ", fileman_tpl=" + fileman_tpl + ", filecategory_tpl=" + filecategory_tpl + ", filevirtualpath_tpl="
				+ filevirtualpath_tpl + "]";
	}
}
