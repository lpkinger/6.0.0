package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.SendOfficialDocument;
import com.uas.erp.service.common.FilePathService;
import com.uas.erp.service.oa.SendODMService;

@Controller
public class SendOfficialDocumentController {
	@Autowired
	private SendODMService sendODMService;
	@Autowired
	private FilePathService filePathService;

	@RequestMapping(value = "/oa/officialDocument/sendODManagement/saveDraft.action")
	@ResponseBody
	public Map<String, Object> saveSOD(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sendODMService.saveSOD(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/officialDocument/sendODManagement/deleteDraft.action")
	@ResponseBody
	public Map<String, Object> deleteSOD(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sendODMService.deleteSOD(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/oa/officialDocument/sendODManagement/updateDraft.action")
	@ResponseBody
	public Map<String, Object> updateSOD(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sendODMService.updateSODById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/officialDocument/sendODManagement/submitDraft.action")
	@ResponseBody
	public Map<String, Object> submitDraft(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sendODMService.submitDraft(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/officialDocument/sendODManagement/resSubmitDraft.action")
	@ResponseBody
	public Map<String, Object> resSubmitDraft(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sendODMService.resSubmitDraft(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/officialDocument/sendODManagement/auditDraft.action")
	@ResponseBody
	public Map<String, Object> auditDraft(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sendODMService.auditDraft(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/officialDocument/sendODManagement/resAuditDraft.action")
	@ResponseBody
	public Map<String, Object> resAuditDraft(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sendODMService.resAuditDraft(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/oa/officialDocument/sendODManagement/save.action")
	@ResponseBody
	public Map<String, Object> save(String caller, int rid, int sid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sendODMService.save(rid, sid, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/officialDocument/getSODDetail.action")
	@ResponseBody
	public Map<String, Object> getSODDetail(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SendOfficialDocument sod = sendODMService.getSODById(id, caller);
		modelMap.put("sod", sod);
		String attach = "";
		if (sod.getSod_attach() != null && sod.getSod_attach() != "") {
			String[] fpid = sod.getSod_attach()
					.substring(0, sod.getSod_attach().lastIndexOf(";"))
					.split(";");
			for (String st : fpid) {
				attach += filePathService.getFilepath(Integer.parseInt(st))
						+ ";";
			}
			attach = attach.substring(0, attach.lastIndexOf(";"));
		}
		modelMap.put("sod_attach", attach);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/officialDocument/getSODDetail2.action")
	@ResponseBody
	public Map<String, Object> getSODDetail2(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SendOfficialDocument sod = sendODMService.getSODById(id, caller);
		modelMap.put("sod", sod);
		modelMap.put("success", true);
		return modelMap;
	}

	/*
	 * ==========================================================================
	 * ==================
	 */
	@RequestMapping("/oa/officialDocument/sendODM/deleteSOD.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, String ids) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] aid = ids.split(",");
		for (String id : aid) {
			sendODMService.deleteById(Integer.parseInt(id));
		}
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/officialDocument/sendODM/getSOD.action")
	@ResponseBody
	public Map<String, Object> get(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SendOfficialDocument sod = sendODMService.getSODById(id, caller);
		modelMap.put("success", true);
		modelMap.put("sod", sod);
		return modelMap;
	}

	@RequestMapping("/oa/officialDocument/sendODM/getSODList.action")
	@ResponseBody
	public Map<String, Object> getAll(String caller, int page, int pageSize) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<SendOfficialDocument> list = sendODMService
				.getList(page, pageSize);
		modelMap.put("success", list);
		modelMap.put("count", sendODMService.getListCount());
		return modelMap;
	}

	@RequestMapping("/oa/officialDocument/sendODM/search.action")
	@ResponseBody
	public Map<String, Object> search(String caller, int page, int pageSize,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<SendOfficialDocument> list = sendODMService.getByCondition(
				condition, page, pageSize);
		modelMap.put("success", list);
		modelMap.put("count", sendODMService.getSearchCount(condition));
		return modelMap;
	}

	@RequestMapping("/oa/officialDocument/sendODM/getAttach.action")
	@ResponseBody
	public Map<String, Object> getAttach(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("path", filePathService.getFilepath(id));
		return modelMap;
	}

}
