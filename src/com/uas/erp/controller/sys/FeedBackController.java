package com.uas.erp.controller.sys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.sys.FeedBackService;

@Controller
public class FeedBackController extends BaseController {
	@Autowired
	private FeedBackService feedbackService;
	
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/sys/feedback/saveFeedback.action")  
	@ResponseBody 
	public Map<String, Object> save(HttpSession session, String formStore, String param) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feedbackService.saveFeedback(formStore, param, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/sys/feedback/deleteFeedback.action")  
	@ResponseBody 
	public Map<String, Object> deleteAcceptNotify(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feedbackService.deleteFeedback(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 * 包括明细
	 */
	@RequestMapping("/sys/feedback/submitFeedback.action")  
	@ResponseBody 
	public Map<String, Object> submitFeedback(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feedbackService.submit(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核问题
	 * 包括明细
	 */
	@RequestMapping("/sys/feedback/auditFeedback.action")  
	@ResponseBody 
	public Map<String, Object> resubmitFeedback(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feedbackService.audit(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/sys/feedback/resAudit.action")  
	@ResponseBody 
	public Map<String, Object> resAudit(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feedbackService.resAudit(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/sys/feedback/updateFeedback.action")  
	@ResponseBody 
	public Map<String, Object> update(HttpSession session, String formStore, String param) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feedbackService.updateFeedback(formStore, param, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/sys/feedback/reply.action")  
	@ResponseBody 
	public Map<String, Object> reply(HttpSession session, int id,String comment) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feedbackService.reply(id,comment, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 系统问题反馈转buglist
	 */
	@RequestMapping("/sys/feedback/turnBuglist.action")
	@ResponseBody
	public Map<String, Object> turnBuglist(HttpSession session, int id){
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int buid = feedbackService.feedbackTurnBug(language, employee,id);
		modelMap.put("id", buid);
		modelMap.put("success", true);
		return modelMap;
	}
	

	/**
	 * 系统问题反馈转buglist
	 */
	@RequestMapping("/sys/feedback/changestatus.action")
	@ResponseBody
	public Map<String, Object> changestatus(HttpSession session, int id){
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feedbackService.changestatus(language, employee,id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 系统问题反馈转buglist
	 */
	@RequestMapping("/sys/feedback/canceltask.action")
	@ResponseBody
	public Map<String, Object> canceltask(HttpSession session, int id){
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feedbackService.canceltask(language, employee,id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/sys/feedback/Endfeedback.action")
	@ResponseBody
	public Map<String, Object> Endfeedback(HttpSession session, int id){
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feedbackService.endFeedback(language, employee,id);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/sys/feedback/backPlan.action")
	@ResponseBody
	public Map<String, Object> backPlan(HttpSession session,String data){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feedbackService.backPlan(data);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/sys/feedback/confirm.action")
	@ResponseBody
	public Map<String, Object> confirm(HttpSession session,String data,Integer _customer,Integer _process){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feedbackService.confirm(data,_customer,_process);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/sys/feedback/processConfirm.action")
	@ResponseBody
	public Map<String,Object> processConfirm(HttpSession session,String data,String step){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feedbackService.processConfirm(data,step);
		modelMap.put("success", true);
		return modelMap;
	} 
	
	@RequestMapping("/sys/feedback/changeHandler.action")
	@ResponseBody
	public Map<String, Object> changeHandler(HttpSession session,String data){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feedbackService.changeHandler(data);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/sys/feedback/getCurrentNode.action")
	@ResponseBody
	public Map<String,Object> getCurrentNode(HttpSession session,String kind,String position){
		Map<String,Object> modelMap=new HashMap<String,Object>();
		modelMap.put("data",feedbackService.getCurrentNode(kind,position));
		return modelMap;
	}	
	/**
	 * 按天统计 
	 * */
	@RequestMapping(value="/sys/feedback/day_count.action",method=RequestMethod.GET)
	@ResponseBody
	public List<Map<String,Object>> getDay_count(HttpSession session,String condition){
		return feedbackService.getDay_count(condition);
	}
	/**
	 * 按周统计 
	 * */
	@RequestMapping(value="/sys/feedback/week_count.action",method=RequestMethod.GET)
	@ResponseBody
	public List<Map<String,Object>> getWeek_count(HttpSession session,String condition){
		return feedbackService.getWeek_count(condition);
	}
	/**
	 * 按月统计 
	 * */
	@RequestMapping(value="/sys/feedback/month_count.action",method=RequestMethod.GET)
	@ResponseBody
	public List<Map<String,Object>> month_count(HttpSession session,String condition){
		return feedbackService.getMonth_count(condition);
	}
   /**
    * 查询
    * */
	@RequestMapping(value="sys/feedback/getFeedback.action",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getFeedback(String condition){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",feedbackService.getFeedback(condition));
		modelMap.put("success",true);
	    return modelMap;
	}
	
	/**
	 * 根据父节点加载子节点
	 */
	@RequestMapping(value = "/sys/feedback/getModules.action")
	@ResponseBody
	public Map<String, Object> getTreeByParentId(HttpSession session, int parentId, String kind,String condition, Integer _noc) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		condition = condition == null || "".equals(condition) ? "1=1" : condition;
		List<JSONTree> tree = feedbackService.getJSONTreeByParentId(parentId,kind,condition, _noc);
		modelMap.put("tree", tree);
		return modelMap;
	}
	
	
}
