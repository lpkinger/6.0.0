package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.MeetingDocTempService;

@Controller
public class MeetingDocTempController {
	@Autowired
	private MeetingDocTempService meetingDocTempService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/oa/meeting/saveMeetingDocTemp.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingDocTempService.saveMeetingDocTemp(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除ECN数据 包括ECN明细
	 */
	@RequestMapping("/oa/meeting/deleteMeetingDocTemp.action")
	@ResponseBody
	public Map<String, Object> deleteMeetingroomapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingDocTempService.deleteMeetingDocTemp(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/oa/meeting/updateMeetingDocTemp.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingDocTempService.updateMeetingDocTemp(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
