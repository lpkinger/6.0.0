package com.uas.erp.controller.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.common.UploadDocumentService;

@Controller
public class UploadDocumentController {

	
	@Autowired
	private UploadDocumentService uploadDocumentService;
	
	/**
	 * 保存form和grid数据
	 * @param caller
	 * @param formStore
	 * 				form数据
	 * @param param
	 * 				grid数据
	 * @return
	 */
	@RequestMapping("/common/uploadDocument/saveUploadDocument.action")
	@ResponseBody
	public Map<String, Object> saveUploadDocument(String caller, String formStore, String param){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		uploadDocumentService.saveUploadDocument(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/common/uploadDocument/deleteUploadDocument.action")
	@ResponseBody
	public Map<String, Object> deleteUploadDocument(String caller, int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		uploadDocumentService.deleteUploadDocument(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改form和grid数据
	 * @param caller
	 * @param formStore
	 * 				form数据
	 * @param param
	 * 				grid数据
	 * @return
	 */
	@RequestMapping("/common/uploadDocument/updateUploadDocument.action")
	@ResponseBody
	public Map<String, Object> updateUploadDocument(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		uploadDocumentService.updateUploadDocument(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/common/uploadDocument/submitUploadDocument.action")
	@ResponseBody
	public Map<String, Object> submitUploadDocument(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		uploadDocumentService.submitUploadDocumentById(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反提交
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/common/uploadDocument/resSubmitUploadDocument.action")
	@ResponseBody
	public Map<String, Object> resSubmitUploadDocument(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		uploadDocumentService.resSubmitUploadDocument(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 审核
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/common/uploadDocument/auditUploadDocument.action")
	@ResponseBody
	public Map<String, Object> auditUploadDocument(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		uploadDocumentService.auditUploadDocument(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反审核
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/common/uploadDocument/resAuditUploadDocument.action")
	@ResponseBody
	public Map<String, Object> resAuditUploadDocument(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		uploadDocumentService.resAuditUploadDocument(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
}
