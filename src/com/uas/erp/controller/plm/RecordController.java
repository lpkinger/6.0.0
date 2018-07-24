package com.uas.erp.controller.plm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.Employee;
import com.uas.erp.model.FormItems;
import com.uas.erp.model.FormPanel;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.plm.RecordService;
import com.uas.erp.service.plm.impl.TaskUtilService;

@Controller
public class RecordController {
	@Autowired
	private RecordService recordService;
	@Autowired
	private TaskUtilService taskUtilService;

	@RequestMapping(value = "/plm/RecordFormItemsAndData.action")
	@ResponseBody
	public Map<String, Object> getFormItems(HttpSession session, String caller, String condition) {
		String language = (String) session.getAttribute("language");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		FormPanel panel = recordService.getFormItemsByCaller(caller, condition, language);
		for (FormItems item : panel.getItems()) {
			Object value = decodeDefaultValue(session, item.getValue());
			item.setValue(value);
		}
		modelMap.put("data", panel.getData());
		modelMap.put("items", panel.getItems());
		modelMap.put("buttons", panel.getButtons());
		modelMap.put("keyField", panel.getFo_keyField());
		modelMap.put("codeField", panel.getCodeField());
		modelMap.put("tablename", panel.getTablename());
		modelMap.put("statusField", panel.getStatusField());
		modelMap.put("statuscodeField", panel.getStatuscodeField());
		modelMap.put("fo_id", panel.getFo_id());
		modelMap.put("fo_keyField", panel.getFo_keyField());
		modelMap.put("fo_detailMainKeyField", panel.getFo_detailMainKeyField());
		modelMap.put("attach", recordService.getFormAttachs(condition));
		return modelMap;
	}

	public Object decodeDefaultValue(HttpSession session, Object value) {
		if (value.toString().contains("getCurrentDate()")) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			return format.format(new Date());
		} else if (value.toString().contains("getCurrentTime()")) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return format.format(new Date());
		} else if (value.toString().contains("session:")) {
			Object obj = session.getAttribute(value.toString().trim().split(":")[1]);
			return (obj == null) ? "" : obj;
		}
		return (value == null || value.equals("null") ? "" : value);
	}

	@RequestMapping(value = "/plm/record/saveWorkRecord.action")
	@ResponseBody
	public Map<String, Object> submitWork(HttpSession session, String formStore) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recordService.saveWorkRecord(formStore, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/plm/record/updateWorkRecord.action")
	@ResponseBody
	public Map<String, Object> updateWorkRecord(HttpSession session, String formStore) {
		String language = (String) session.getAttribute("language");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recordService.updateWorkRecord(formStore, language);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/plm/record/updateBillRecord.action")
	@ResponseBody
	public Map<String, Object> updateBillRecord(HttpSession session, Integer wr_raid, String wr_redcord) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recordService.updateBillRecord(wr_raid, wr_redcord, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 结束任务
	 * 
	 * @param session
	 * @param ra_id
	 * @return
	 */
	@RequestMapping(value = "/plm/record/endBillTask.action")
	@ResponseBody
	public Map<String, Object> endBillTask(HttpSession session, Integer ra_id, Integer id,String record) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recordService.endBillTask(ra_id, id,record, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 获取任务沟通消息
	 * 
	 * @param session
	 * @param ra_id
	 * @return
	 */
	@RequestMapping(value = "/plm/record/msgTask.action")
	@ResponseBody
	public Map<String, Object> msgTask(HttpSession session, Integer ra_id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		if(employee==null) BaseUtil.showError("会话已断开!");
		List<Map<String, Object>> msg = null;	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		msg=recordService.getMsg(ra_id);
		modelMap.put("success", true);
		modelMap.put("taskmsg", msg);
		return modelMap;
	}
	/**
	 * 变更任务
	 * 
	 * @param session
	 * @param ra_id
	 * @return
	 */
	@RequestMapping(value = "/plm/record/changeBillTask.action")
	@ResponseBody
	public Map<String, Object> changeBillTask(HttpSession session, Integer ra_id, Integer em_id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recordService.changeBillTask(ra_id, em_id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
   /**
    * 确认任务
    * */
	@RequestMapping(value = "/plm/record/confirmBillTask.action")
	@ResponseBody
	public Map<String, Object> confirmBillTask(HttpSession session, Integer ra_id, Integer id,String record) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recordService.confirmBillTask(ra_id, id,record, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 驳回任务
	 * */
	@RequestMapping(value = "/plm/record/noConfirmBillTask.action")
	@ResponseBody
	public Map<String, Object> noConfirmBillTask(HttpSession session, Integer ra_id, Integer id,String record) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recordService.noConfirmBillTask(ra_id, id,record, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping(value = "plm/record/submitWorkRecord.action")
	@ResponseBody
	public Map<String, Object> submitWorkRecord(HttpSession session, int id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recordService.submitWorkRecord(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "plm/record/resSubmitWorkRecord.action")
	@ResponseBody
	public Map<String, Object> resSubmitWorkRecord(HttpSession session, int id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recordService.resSubmitWorkRecord(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "plm/record/GetResource.action")
	@ResponseBody
	public Map<String, Object> getTreeByParentId(HttpSession session, int id) {
		String language = (String) session.getAttribute("language");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<JSONTree> tree = recordService.getJSONResource(id, language);
		modelMap.put("tree", tree);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "plm/record/GetRecordTree.action")
	@ResponseBody
	public Map<String, Object> getTreeByParentId(HttpSession session, String condition) {
		String language = (String) session.getAttribute("language");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<JSONTree> tree = recordService.getJSONRecord(condition, language);
		modelMap.put("tree", tree);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "plm/record/start.action")
	@ResponseBody
	public Map<String, Object> startTask(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskUtilService.startTask(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "plm/record/stop.action")
	@ResponseBody
	public Map<String, Object> stopTask(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskUtilService.stopTask(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "plm/record/getRecordData.action")
	@ResponseBody
	public Map<String, Object> getRecordData(HttpSession session, int id) {
		String language = (String) session.getAttribute("language");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("data", recordService.getRecordData(id, employee, language));
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "plm/record/resSubmitRecord.action")
	@ResponseBody
	public Map<String, Object> resSubmitRecord(HttpSession session, int id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recordService.resSubmitWorkRecord(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "plm/record/SubmitRecord.action")
	@ResponseBody
	public Map<String, Object> submitRecord(HttpSession session, int id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recordService.submitWorkRecord(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/*
	 * 获取任务所需的文件
	 */
	@RequestMapping(value = "plm/record/getTaskFiles.action")
	@ResponseBody
	public Map<String, Object> getTaskFiles(HttpSession session, Integer id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = recordService.getTaskFiles(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	/*
	 * 提交workrecord,只触发流程
	 */
	@RequestMapping(value = "plm/record/SubmitWorkRecordFlow.action")
	@ResponseBody
	public Map<String, Object> submitWorkRecordFlow(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recordService.submitWorkRecordFlow(id);
		modelMap.put("success", true);
		return modelMap;
	}
		
	/*
	 * 任务移交
	 */
	@RequestMapping(value = "plm/record/taskTransfer.action")
	@ResponseBody
	public Map<String, Object> taskTransfer(HttpSession session, String ids,String from,String to) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recordService.taskTransfer(ids,from,to);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/*
	 * 载入关联业务数据
	 */
	@RequestMapping(value = "plm/record/loadRelationData.action")
	@ResponseBody
	public Map<String, Object> loadRelationData(HttpSession session, String id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", recordService.loadRelationData(id));
		modelMap.put("success", true);
		return modelMap;
	}
}
