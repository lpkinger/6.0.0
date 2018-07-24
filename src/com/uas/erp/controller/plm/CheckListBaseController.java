package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.model.Employee;
import com.uas.erp.service.plm.CheckListBaseService;

@Controller
public class CheckListBaseController extends BaseController {
	@Autowired
	private CheckListBaseService checkListBaseService;

	@RequestMapping("/plm/check/saveCheckListBase.action")
	@ResponseBody
	public Map<String, Object> saveCheckListBase(HttpSession session, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkListBaseService.saveCheckListBase(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/check/deleteCheckListBase.action")
	@ResponseBody
	public Map<String, Object> deleteCheckListBase(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkListBaseService.deleteCheckListBase(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/check/deleteAllDetails.action")
	@ResponseBody
	public Map<String, Object> deleteAllDetails(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkListBaseService.deleteAllDetails(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/check/updateCheckListBase.action")
	@ResponseBody
	public Map<String, Object> updateCheckListBase(HttpSession session, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkListBaseService.updateCheckListBase(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/check/submitCheckListBase.action")
	@ResponseBody
	public Map<String, Object> submitCheckListBase(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkListBaseService.submitCheckListBase(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/plm/check/resSubmitCheckListBase.action")
	@ResponseBody
	public Map<String, Object> resSubmitCheckListBase(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkListBaseService.reSubmitCheckListBase(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/plm/check/auditCheckListBase.action")
	@ResponseBody
	public Map<String, Object> auditCheckListBase(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkListBaseService.auditCheckListBase(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/plm/check/resAuditCheckListBase.action")
	@ResponseBody
	public Map<String, Object> resAuditCheckListBase(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkListBaseService.resAuditCheckListBase(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 载入
	 */
	@RequestMapping("/plm/test/LoadTestItem.action")
	@ResponseBody
	public Map<String, Object> LoadTestItem(HttpSession session, int id, String kinds, String producttype) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkListBaseService.loadTestItem(id, kinds, producttype);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 设置测试项结果
	 * */
	@RequestMapping("/plm/test/setItemResult.action")
	@ResponseBody
	public Map<String, Object> setItemResult(HttpSession session, String result, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkListBaseService.setItemResult(result, data);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/test/EndProject.action")
	@ResponseBody
	public Map<String, Object> EndProject(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkListBaseService.EndProject(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/test/resEndProject.action")
	@ResponseBody
	public Map<String, Object> resEndProject(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkListBaseService.resEndProject(id);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/plm/test/updateResult.action")  
	@ResponseBody 
	public Map<String, Object> updateResult(HttpSession session, String caller,String data,String field,
			String keyValue) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkListBaseService.updateResult(data,field,keyValue);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 更新结果按钮
	 */
	@RequestMapping("/plm/test/updateResultCheckListBase.action")
	@ResponseBody
	public Map<String, Object> updateResultCheckListBase(String data,int id) {//maz 修改为获取gridstore和id，为了可以循环遍历勾选了的从表ID进行循环插入数据到测试历史中
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkListBaseService.updateResultCheckListBase(data, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/plm/test/getChecklistGridData.action")
	@ResponseBody
	public Map<String, Object> getData(String id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String s = id.replaceAll("\'", "");
		int ids = Integer.parseInt(s);
		List<Map<Object, Object>> data = checkListBaseService.getCheckListGridData(ids);
		modelMap.put("data", data);
		modelMap.put("success", true);
		return modelMap;
	}

}
