package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.GridPanel;
import com.uas.erp.service.common.SingleGridPanelService;
import com.uas.erp.service.oa.MeetingRoomService;

@Controller
public class MeetingRoomController {
	@Autowired
	private MeetingRoomService meetingRoomService;
	@Autowired
	private SingleGridPanelService singleGridPanelService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/oa/meeting/saveMeetingRoom.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingRoomService.saveMeetingRoom(formStore,param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/oa/meeting/updateMeetingRoom.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String param, String formStore) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingRoomService.updateMeetingRoom(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/meeting/deleteMeetingRoom.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingRoomService.deleteMeetingRoom(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核MeetingRoom
	 */
	@RequestMapping("/oa/meeting/auditMeetingRoom.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingRoomService.auditMeetingRoom(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/meeting/resAuditMeetingRoom.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingRoomService.resAuditMeetingRoom(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/meeting/submitMeetingRoom.action")
	@ResponseBody
	public Map<String, Object> submit(String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingRoomService.submitMeetingRoom(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/meeting/resSubmitMeetingRoom.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingRoomService.resSubmitMeetingRoom(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/oa/meeting/singleGridPanel.action")
	@ResponseBody
	public Map<String, Object> getGridFields(String caller, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String conditionDate = null;
		if (condition.contains("ma_date")) {
			String[] contion = condition.split("AND");
			conditionDate = condition.split("AND")[0];
			if (contion.length > 1) {
				condition = contion[1];
			} else {
				condition = null;
			}
		}
		if (condition == null || condition == "") {
			condition = "mr_status='已审核'";
		} else {
			condition += " AND mr_status='已审核'";
		}
		GridPanel gridPanel = singleGridPanelService.getGridPanelByCaller(
				caller, condition, null, null, 1,false,"");
		modelMap.put("fields", gridPanel.getGridFields());
		// 这里的columns里面添加了属性dbfind，方便进行dbfind操作。详见com.uas.erp.model.GridColumns的构造函数
		modelMap.put("columns", gridPanel.getGridColumns());
		// B2B里面每次dbfind都要重新查找dbfindsetgrid配置，实在麻烦，
		// 所以在grid加载时，直接将dbfindsetgrid配置得到并传到前台
		modelMap.put("dbfinds", gridPanel.getDbfinds());
		modelMap.put("limits", gridPanel.getLimits());
		// modelMap.put("date",chanceService.getDateRange(condition));
		modelMap.put("data", gridPanel.getDataString());
		modelMap.put("data2", meetingRoomService.showapply(
				gridPanel.getDataString(), conditionDate));
		return modelMap;
	}
}
