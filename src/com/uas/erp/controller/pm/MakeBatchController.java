package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.model.Employee;
import com.uas.erp.service.pm.MakeBatchService;

@Controller
public class MakeBatchController extends BaseController {
	@Autowired
	private MakeBatchService MakeBatchService;

	/**
	 * 清除明细
	 */
	@RequestMapping("/pm/make/cleanMakeBatch.action")
	@ResponseBody
	public Map<String, Object> cleanMakeBatch(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeBatchService.cleanMakeBatch(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 清除失败的数据
	 */
	@RequestMapping("/pm/make/cleanFailed.action")
	@ResponseBody
	public Map<String,Object> cleanFailed(String caller,int id) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		MakeBatchService.cleanFailed(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量生成制造单 
	 */
	@RequestMapping("/pm/make/batchToMake.action")
	@ResponseBody
	public Map<String, Object> batchToMake(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeBatchService.batchToMake(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/pm/make/updateMakeBatch.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeBatchService.updateMakeBatchById(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取编号
	 */
	@RequestMapping("/pm/make/getcode.action")
	@ResponseBody
	public Map<String,Object> getcode(String caller,String table,int type,String conKind){
		Map<String, Object> modelMap = new HashMap<String,Object>();
		modelMap.put("code", MakeBatchService.getcode(caller,table,type,conKind));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 更新工单
	 */
	@RequestMapping("/pm/make/makeupdate.action")
	@ResponseBody
	public Map<String, Object> makeupdate(HttpSession session,String caller, String data) {
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeBatchService.makeupdateDatalist(employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}
	
}
