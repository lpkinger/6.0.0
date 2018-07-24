package com.uas.erp.controller.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.common.DocDistributionService;

@Controller
public class DocDistributionController {

	@Autowired
	private DocDistributionService docDistributionService;
	
	
	/**
	 * 获取文件树
	 * @param condition
	 * @param id
	 * @param checked
	 * @return
	 */
	@RequestMapping("/common/sendEmail/getMenuTree.action")
	@ResponseBody
	public Map<String, Object> getProjectFileTree(String condition, int id, String checked) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> tree = docDistributionService.getProjectFileTree(condition,id, checked);
		modelMap.put("tree", tree);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 通过id获取文件信息
	 * @param ids
	 * @return
	 */
	@RequestMapping("/common/sendEmail/getFileInfo.action")
	@ResponseBody
	public Map<String, Object> getFileInfo(int[] ids) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> fileList = docDistributionService.getFileInfo(ids);
		modelMap.put("fileList", fileList);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/common/sendEmail/saveSendEmail.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		docDistributionService.saveDocDistribution(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/common/sendEmail/deleteSendEmail.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		docDistributionService.deleteDocDistribution(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 更新
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/common/sendEmail/updateSendEmail.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		docDistributionService.updateDocDistribution(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/common/sendEmail/submitSendEmail.action")
	@ResponseBody
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		docDistributionService.submitDocDistribution(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反提交
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/common/sendEmail/resSubmitSendEmail.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		docDistributionService.resSubmitDocDistribution(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 审核
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/common/sendEmail/auditSendEmail.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		docDistributionService.auditDocDistribution(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反审核
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/common/sendEmail/resAuditSendEmail.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		docDistributionService.resAuditDocDistribution(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	
}
