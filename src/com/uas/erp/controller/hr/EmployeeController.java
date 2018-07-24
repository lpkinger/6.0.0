package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.hr.HrOrgService;

@Controller
public class EmployeeController extends BaseController {
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private HrOrgService hrOrgService;
	@Autowired
	private BaseDao baseDao;
	/**
	 * condition='em_id=3023/em_code=A024'
	 */
	@RequestMapping("/hr/employee/getEmployee.action")
	@ResponseBody
	public Map<String, Object> get(String caller, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		employeeService.getByCondition(condition,caller);
		modelMap.put("success", true);
		modelMap.put("employee", employeeService.getByCondition(condition,caller));
		return modelMap;
	}

	/**
	 * 修改为手动修改密码才调用的接口
	 * 需要传原密码em_oldpassword
	 */
	@RequestMapping("/hr/employee/updatePwd.action")
	@ResponseBody
	public Map<String, Object> updatePwd(String caller, String em_oldpassword,
			String em_newpassword,HttpSession session,String synchronize) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		session.setAttribute("hascheckInitpwd",true);
		String result = employeeService.updatePwd(caller, em_oldpassword,em_newpassword,SystemSession.getUser().getEm_id().toString(),
				SystemSession.getUser().getEm_code().toString(),SystemSession.getUser().getEm_password(),synchronize);	
		if (result == null||result.length()==0) {
			modelMap.put("success", true);
			employeeService.updateChangeStatues(SystemSession.getUser().getEm_id());
			SystemSession.getUser().setEm_password(em_newpassword);
		} else {
			modelMap.put("result", result);
		}
		return modelMap;
	}
	
	/**
	 * 重置密码或者3个月没有修改密码调用的接口
	 * 不需要传原密码，原密码通过session获取
	 */
	@RequestMapping("/hr/employee/updateChPwd.action")
	@ResponseBody
	public Map<String, Object> updateChPwd(String caller, String em_oldpassword,
			String em_newpassword,HttpSession session,String synchronize) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		session.setAttribute("changepsw",false);
		session.setAttribute("hascheckInitpwd",true);
		String result = employeeService.updatePwd(caller, SystemSession.getUser().getEm_password(),em_newpassword,SystemSession.getUser().getEm_id().toString(),
				SystemSession.getUser().getEm_code().toString(),SystemSession.getUser().getEm_password(),synchronize);	
		if (result == null||result.length()==0) {
			modelMap.put("success", true);
			employeeService.updateChangeStatues(SystemSession.getUser().getEm_id());
			SystemSession.getUser().setEm_password(em_newpassword);
		} else {
			modelMap.put("result", result);
		}
		return modelMap;
	}
	
	@RequestMapping("hr/employee/updateStatus.action")
	@ResponseBody
	public void updateStatus(HttpSession session, ModelMap modelMap) {
		session.setAttribute("hascheckInitpwd",true);
	}
	
	@RequestMapping("hr/employee/updateChangeStatus.action")
	@ResponseBody
	public void updateChangeStatus(HttpSession session, ModelMap modelMap) {
		employeeService.updateChangeStatues(SystemSession.getUser().getEm_id());
		session.setAttribute("changepsw",false);
		session.setAttribute("hascheckInitpwd",true);
	}
	/**
	 * 员工资料 TreeGrid查找
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping("/hr/employee/tree.action")
	@ResponseBody
	public Map<String, Object> empTree(String caller,HttpSession session) {
		boolean JobOrgNoRelation=(Boolean) session.getAttribute("joborgnorelation");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", hrOrgService.getOrgTrees(caller, null,JobOrgNoRelation));
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/hr/employee/updateEmployeeList.action")
	@ResponseBody
	public Map<String, Object> updateEmployeeList(String caller,
			String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		employeeService.updateEmployeeList(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/hr/employee/getEmployees.action")
	@ResponseBody
	public Map<String, Object> getEmployees(String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("employees", employeeService.getEmployeesByCondition(condition));
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/hr/employee/saveEmployees.action")
	@ResponseBody
	public Map<String,Object> saveEmployees(String jsonData){
		Map<String,Object> modelMap=new HashMap<String,Object>();
		modelMap.put("success",true);
		employeeService.saveEmployees(jsonData);
		return modelMap;
	}
	@RequestMapping("/hr/employee/updateEmployees.action")
	@ResponseBody
	public Map<String,Object>updateEmployees(String jsonData){
		Map<String,Object> modelMap=new HashMap<String,Object>();
		modelMap.put("success", true);
		employeeService.updateEmployees(jsonData);
		return  modelMap;
	}
	@RequestMapping("/hr/employee/deleteEmployees.action")
	@ResponseBody
	public Map<String,Object>deleteEmployees(String jsonData){
		Map<String,Object> modelMap=new HashMap<String,Object>();
		modelMap.put("success", true);
		employeeService.deleteEmployees(jsonData);
		return  modelMap;
	}
	
	/**
	 * lidy  通过条件获取判断密码是否是默认密码111111 是则返回true
	 * @param caller
	 * @param condition
	 * @return
	 */
	@RequestMapping("hr/employee/checkInitpwd.action")
	@ResponseBody
	public Map<String,Object> getPassword(String caller, String condition){
		Map<String,Object> modelMap=new HashMap<String,Object>();
		modelMap.put("data", employeeService.checkInitpwd(condition));
		modelMap.put("success", true);
		return  modelMap;
	}
}
