package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.service.hr.EmployeeManagerService;

@Controller
public class EmployeeManagerController {

	@Autowired
	private EmployeeManagerService employeeManagerService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/hr/emplmana/saveEmployee.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param, String param2,HttpSession session) {
		boolean JobOrgNoRelation=(Boolean) session.getAttribute("joborgnorelation");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		employeeManagerService.saveEmployee(formStore, param, caller,JobOrgNoRelation);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/emplmana/updateEmployee.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param, String param2,HttpSession session) {
		boolean JobOrgNoRelation= (Boolean) session.getAttribute("joborgnorelation");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		employeeManagerService.updateEmployeeById(formStore, param, param2,
				caller,JobOrgNoRelation);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/emplmana/deleteEmployee.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		employeeManagerService.deleteEmployee(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除额外岗位配置
	 */
	@RequestMapping("/hr/emplmana/deleteExtraJob.action")
	@ResponseBody
	public Map<String, Object> deleteExtraJob(String caller, int empId,
			int jobId) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		employeeManagerService.deleteExtraJob(empId, jobId, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量离职
	 * 
	 * @param session
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/hr/emplmana/vastTurnover.action")
	@ResponseBody
	public Map<String, Object> vastTurnOver(String caller, Integer[] id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		employeeManagerService.vastTurnOver(caller, id);
		modelMap.put("success", true);
		return modelMap;

	}

	/**
	 * 批量转正
	 * 
	 * @param session
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/hr/emplmana/vastTurnfullmemb.action")
	@ResponseBody
	public Map<String, Object> vastTurnfullmemb(String caller,
			Integer[] id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		employeeManagerService.vastTurnfullmemb(caller, id);
		modelMap.put("success", true);
		return modelMap;

	}

	/**
	 * 生成转正申请
	 */
	@RequestMapping("/hr/emplmana/turnfullmemb.action")
	@ResponseBody
	public Map<String, Object> Turnfullmemb(String caller, 
			int[] id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		employeeManagerService.turnFullmemb(caller, id);
		modelMap.put("success", true);
		return modelMap;

	}

	/**
	 * 更新人员资料
	 */
	@RequestMapping("/hr/emplmana/updatePosition.action")
	@ResponseBody
	public Map<String, Object> updatePosition(String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		employeeManagerService.updatePosition(param, caller);
		modelMap.put("success", true);
		return modelMap;

	}
	
	/**
	 * 离职转录用申请（列表界面）
	 */
	@RequestMapping("/hr/emplmana/turnCaree.action")
	@ResponseBody
	public Map<String, Object> turnCaree(String caller, int[] id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		employeeManagerService.turnCaree(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 离职转正式（批量处理界面）
	 */
	@RequestMapping("/hr/vastLZTurnZS.action")
	@ResponseBody
	public Map<String, Object> vastLZTurnZS(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		employeeManagerService.vastLZTurnZS(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/hr/emplmana/printUnpackApply.action")
	@ResponseBody
	public Map<String, Object> printUnpackApply(String caller, int id,
			String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = employeeManagerService.printUnpackApply(id, caller,
				reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	@RequestMapping("/hr/emplmana/vastOver.action")
	@ResponseBody
	public Map<String, Object> vastOver(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(data);
		Integer[] id = new Integer[gstore.size()];
		int index = 0;
		for (Map<Object, Object> store : gstore) {
			id[index] = Integer.parseInt(store.get("em_id") + "");
			index++;
		}
		employeeManagerService.vastTurnOver(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/hr/emplmana/search.action")
	@ResponseBody
	public List<String> searchEmployees(String caller, String keyword) {
		return employeeManagerService.searchEmployeesByKey(keyword);
	}
	
	/**
	 * 试用到期人员转人员转正申请单（批量处理界面）
	 */
	@RequestMapping(value = "/hr/vastTurnfullmemb.action")
	@ResponseBody
	public Map<String, Object> vastTurnfullmemb(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", employeeManagerService.vastTurnfullmemb(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 人员合同批签作业（批量处理界面）
	 */
	@RequestMapping(value = "/hr/vastTurnContract.action")
	@ResponseBody
	public Map<String, Object> vastTurnContract(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", employeeManagerService.vastTurnContract(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 人员资料同步时只同步界面配置字段
	 * */
	@RequestMapping("/hr/emplmana/vastPost.action")
	@ResponseBody
	public  Map<String,Object> vastPost(HttpSession session, String datas,String to) {
		employeeManagerService.postEmployee(datas,to);
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("success", true);
		return map;
	}

	/**
	 * @author wsy
	 * */
	@RequestMapping("/hr/emplmana/checkEmcode.action")
	@ResponseBody
	public  Map<String,Object> checkEmcode(HttpSession session, String emcode,String emname) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("log", employeeManagerService.checkEmcode(emcode,emname));
		map.put("success", true);
		return map;
	}
}
