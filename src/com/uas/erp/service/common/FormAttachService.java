package com.uas.erp.service.common;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

import com.uas.erp.model.Employee;
import com.uas.erp.model.FormAttach;

public interface FormAttachService {
	List<FormAttach> getFormAttachs(String caller, int keyValue);

	JSONArray getFiles(String id);

	int beforeExport(String caller, String type, String condition, Employee employee,Boolean self,boolean jobemployee,HttpServletRequest req);
	
	/**
	 * 抽象出来的上传实现了AttachAble接口的对象附带的附件的方法
	 * @param attachAbles 实现接口的列表
	 * @param uploadPath 上传到的路径
	 * @param refrenceKey 附件相关联的对应字段
	 */
	public void uploadAttachs(List<? extends AttachUploadedAble> attachAbles, String uploadPath, String refrenceKey, boolean sign, String signKey);
}
