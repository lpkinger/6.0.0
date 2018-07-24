package com.uas.erp.controller.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.common.DocumentSetService;
import com.uas.erp.service.oa.DocumentListService;

@Controller
public class DocumentSetController {

	@Autowired
	private DocumentListService documentListService;
	@Autowired
	private DocumentSetService documentSetService;
	

	/**
	 * 根据父节点加载子节点
	 */
	@RequestMapping("/common/DocSetting/getDocTree.action")
	@ResponseBody
	public Map<String, Object> getTreeByParentId(String caller, int parentid, String condition) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 此处Employee在el表达式用到，不能去掉
		modelMap.put("tree", documentListService.loadDir(parentid, condition, caller));
		return modelMap;
	}
	
	/**
	 * 保存文档归档规则
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/common/DocSetting/saveDocSetting.action")
	@ResponseBody
	public Map<String, Object> saveDocSetting(String caller, String formStore, String param){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		documentSetService.saveDocSetting(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除文档归档规则
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/common/DocSetting/deleteDocSetting.action")
	@ResponseBody
	public Map<String, Object> deleteDocSetting(String caller, int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		documentSetService.deleteDocSetting(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改文档归档规则
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/common/DocSetting/updateDocSetting.action")
	@ResponseBody
	public Map<String, Object> updateDocSetting(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		documentSetService.updateDocSettingById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交文档归档规则
	 */
	@RequestMapping("/common/DocSetting/submitDocSetting.action")
	@ResponseBody
	public Map<String, Object> submitDocSetting(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		documentSetService.submitDocSetting(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反提交文档归档规则
	 */
	@RequestMapping("/common/DocSetting/resSubmitDocSetting.action")
	@ResponseBody
	public Map<String, Object> resSubmitDocSetting(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		documentSetService.resSubmitDocSetting(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 审核文档归档规则
	 */
	@RequestMapping("/common/DocSetting/auditDocSetting.action")
	@ResponseBody
	public Map<String, Object> auditDocSetting(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		documentSetService.auditDocSetting(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反审核文档归档规则
	 */
	@RequestMapping("/common/DocSetting/resAuditDocSetting.action")
	@ResponseBody
	public Map<String, Object> resAuditDocSetting(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		documentSetService.resAuditDocSetting(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
