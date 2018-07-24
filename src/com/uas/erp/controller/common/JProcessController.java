package com.uas.erp.controller.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Employee;
import com.uas.erp.model.JNode;
import com.uas.erp.model.JProcess;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.JProcessService;
import com.uas.erp.service.common.ProcessService;

@Controller
public class JProcessController {
	@Autowired
	private JProcessService jprocessService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired ProcessService processService;
	
	/**
	 * 获取所有流程
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value="/oa/myprocess/getJProcessList.action")
	@ResponseBody
	public Map<String,Object> getJProcessList(int page, int pageSize){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jprocesslist", jprocessService.getJProcessList(page ,pageSize));
		map.put("count", jprocessService.getJProcessCount());
		return map;
	}
	@RequestMapping(value="/oa/myprocess/getJNodeEfficiencyFromReviewedJProcessList.action")
	@ResponseBody
	public Map<String,Object> getReviewedJProcessList(int page, int pageSize){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jprocesslist", jprocessService.getJNodeEfficiencysList(page ,pageSize));
		map.put("count", jprocessService.getJProcessCount());
		return map;
	}
	@RequestMapping(value="/oa/myprocess/getTimeoutNodeList.action")
	@ResponseBody
	public Map<String,Object> getTimeoutNodeList(int page, int pageSize){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jprocesslist", jprocessService.getTimeoutNodeList(page ,pageSize));
		map.put("count", jprocessService.getJProcessCount());
		return map;
	}
	@RequestMapping(value="/oa/myprocess/searchTimeoutJNode.action")
	@ResponseBody
	public Map<String,Object> searchTimeoutJNode(HttpSession session, String condition, int page, int pageSize){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jprocesslist", jprocessService.searchTimeoutJNode(condition, page ,pageSize));
		map.put("count", jprocessService.searchCount(condition));
		return map;
	}
	/**
	 * 根据所选id删除流程
	 * @param ids
	 * @return
	 */
	@RequestMapping(value="/oa/myprocess/delete.action")
	@ResponseBody
	public Map<String,Object> delete(String ids){
		Map<String, Object> map = new HashMap<String, Object>();
		jprocessService.delete(ids);
		map.put("success", true);
		return map;
	}
	/**
	 * 筛选流程(从所有流程中筛选)
	 * @param session
	 * @param condition
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value="/oa/myprocess/search.action")
	@ResponseBody
	public Map<String,Object> search(HttpSession session, String condition, int page, int pageSize){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jprocesslist", jprocessService.search(condition, page ,pageSize));
		map.put("count", jprocessService.searchCount(condition));
		return map;
	}
	
	@RequestMapping(value="/oa/myprocess/searchJNodeEfficiency.action")
	@ResponseBody
	public Map<String,Object> searchJNodeEfficiency(HttpSession session, String condition, int page, int pageSize){
		//Employee employee  = (Employee) session.getAttribute("employee");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("jprocesslist", jprocessService.searchJNodeEfficiency(condition, page ,pageSize));
		map.put("count", jprocessService.searchCount(condition));
		return map;
	}
	
	/**
	 * 单据如果有流程处理，就显示当前流程处理情况
	 * @param caller 单据caller，在配置表FORM中字段fo_flowcaller为对应的流程caller
	 * @param keyValue 单据主键值，具有唯一性
	 * @return 当前流程处理情况
	 */
	@RequestMapping(value="/common/getJProcessByForm.action")
	@ResponseBody
	public Map<String,Object> getJProcessByForm(HttpSession session, String caller, int keyValue){
		Map<String, Object> map = new HashMap<String, Object>();
		map=jprocessService.getJprocessNode(caller, keyValue,"current");
		return map;
	}
	/**
	 *获得当前分支的所有节点 
	 * */
	@RequestMapping(value="/common/getCurrentJnodes.action")
	@ResponseBody 
	public Map<String,Object> getCurrentJnodes(HttpSession session,String caller,int keyValue){
		Map<String,Object> map=new HashMap<String,Object>();
		List<Map<String,Object>> lists=jprocessService.SetCurrentJnodes(caller, keyValue);
		Map<String,Object>  currentmap=jprocessService.getJprocessNode(caller, keyValue,"current");
		List<JProcess> processs=processService.getJProcesssByInstanceId(String.valueOf(currentmap.get("instanceId")));
	    //TODO 更新待办可选流程
		List<String>jprocands=processService.getJProCandByByInstanceId(String.valueOf(currentmap.get("instanceId")));
		List <JNode> nodes=processService.getAllHistoryNode(String.valueOf(currentmap.get("instanceId")),null);
		map.put("data", lists);
		map.put("nodes", nodes);
		map.put("jprocands", jprocands);
		map.put("currentnode", currentmap);
		map.put("processs",processs);
		map.put("success", true);
		return  map;
	}
	/**
	 * 设置流程处理人
	 * */
	@RequestMapping(value="/common/SetJProcessNodeDealMan.action")
	@ResponseBody
	public Map<String,Object> SetJProcessNodeDealMan(HttpSession session, String caller, int keyValue){
		Map<String, Object> map = new HashMap<String, Object>();
		//map=jprocessService.SetJProcessNodeDealMan(caller, keyValue);
		return map;
	}
	/**
	 * 更新流程设置
	 * */
	@RequestMapping(value="/common/updateJnodePerson.action")
	@ResponseBody
	public Map<String,Object> updateJnodePerson(HttpSession session,String param,String caller,Integer keyValue){
		Map<String, Object> map = new HashMap<String, Object>();
		Employee employee=(Employee)session.getAttribute("employee");
		jprocessService.updateJnodePerson(param,caller,keyValue,employee);
		map.put("success",true);
		return map;
	}
	
	/**
	 * 筛选流程(从我的一级下属发起的流程中筛选)
	 * @param session
	 * @param condition
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value="/oa/myprocess/search2.action")
	@ResponseBody
	public Map<String,Object> search2(HttpSession session, String condition, int page, int pageSize){
		Employee employee  = (Employee) session.getAttribute("employee");
		Map<String, Object> map = new HashMap<String, Object>();
		List<Employee> employeeList = employeeService.getEmployeesByOrId(employee.getEm_defaultorid());
		condition += " AND JP_LAUNCHERID IN(";
		for (Employee em : employeeList) {
			condition += "'" +em.getEm_code() + "',";
		}
		condition = condition.substring(0, condition.lastIndexOf(",")) + ")";
		map.put("jprocesslist", jprocessService.search(condition, page ,pageSize));
		map.put("count", jprocessService.searchCount(condition));
		return map;
	}
	/**
	 * 获取我的一级下属发起的流程
	 * @param session
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value="/oa/myprocess/getMyList.action")
	@ResponseBody
	public Map<String,Object> getMyList(HttpSession session, int page, int pageSize){
		Employee employee  = (Employee) session.getAttribute("employee");
		Map<String, Object> map = new HashMap<String, Object>();
		Integer or = employee.getEm_defaultorid();
		if (or == null) {
			map.put("error", "没有下属！");
		} else {
			List<Employee> employeeList = employeeService.getEmployeesByOrId(or);
			if (employeeList.size() == 0) {
				map.put("error", "没有下属");
			} else {
				String condition = "JP_LAUNCHERID IN(";
				for (Employee em : employeeList) {
					condition += "'" +em.getEm_code() + "',";
				}
				condition = condition.substring(0, condition.lastIndexOf(",")) + ")";
				map.put("jprocesslist", jprocessService.search(condition, page ,pageSize));
				map.put("count", jprocessService.searchCount(condition));							
			}
		}
		return map;
	}
	
}
