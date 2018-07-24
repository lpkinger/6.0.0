package com.uas.erp.controller.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.CheckBoxTree;
import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.common.SysnavigationService;

/**
 * TreeAction
 * 
 * @author yingp
 * @date 2012-07-16 08:35:00
 */
@Controller
public class TreeController {

	@Autowired
	private SysnavigationService sysnavigationService;

	/**
	 * 拿到树所有节点信息
	 */
	@RequestMapping(value = "/common/tree.action", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getTree(HttpServletResponse resp) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<JSONTree> tree = sysnavigationService.getJSONTree();
		modelMap.put("tree", tree);
		return modelMap;
	}

	/**
	 * 根据父节点加载子节点
	 */
	@RequestMapping(value = "/common/lazyTree.action")
	@ResponseBody
	public Map<String, Object> getTreeByParentId(HttpSession session, int parentId, String condition, Integer _noc) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		condition = condition == null || "".equals(condition) ? "1=1" : condition;
		List<JSONTree> tree = sysnavigationService.getJSONTreeByParentId(parentId, condition, _noc);
		modelMap.put("tree", tree);
		return modelMap;
	}

	/**
	 * 按搜索条件加载树
	 */
	@RequestMapping(value = "/common/searchTree.action")
	@ResponseBody
	public Map<String, Object> getTreeBySearch(HttpSession session, String search, Boolean isPower) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		isPower = isPower == null ? false : isPower;
		List<JSONTree> tree = sysnavigationService.getJSONTreeBySearch(search, employee, isPower);
		modelMap.put("tree", tree);
		return modelMap;
	}

	/**
	 * 企业通讯录
	 */
	@RequestMapping(value = "/common/addrbook.action")
	@ResponseBody
	public Map<String, Object> getAddrBook(HttpSession session) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();

		return modelMap;
	}
	
	/**
	 * 全功能导航
	 */
	@RequestMapping(value = "/common/getAllNavigation.action")
	@ResponseBody
	public Map<String, Object> getAllNavigation(HttpSession session, int parentId, String condition) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		condition = condition == null || "".equals(condition) ? "1=1" : condition;
		List<JSONTree> tree = sysnavigationService.getAllNavigation(parentId, condition);
		modelMap.put("tree", tree);
		return modelMap;
	}
	/**
	 * 全功能导航数据更新
	 */
	@RequestMapping(value = "/common/initAllNavigation.action")
	@ResponseBody
	public Map<String, Object> initAllNavigation(){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sysnavigationService.initAllNavigation();
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 导航信息
	 */
	@RequestMapping(value = "/common/getNavigationDetails.action")
	@ResponseBody
	public Map<String, Object> getNavigationDetails(int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", sysnavigationService.getNavigationDetails(id));
		return modelMap;
	}
	
	/**
	 * 按搜索条件加载全功能导航树
	 */
	@RequestMapping(value = "/common/searchNavigation.action")
	@ResponseBody
	public Map<String, Object> searchNavigation(HttpSession session, String search) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<JSONTree> tree = sysnavigationService.getJSONNavigationTreeBySearch(search);
		modelMap.put("tree", tree);
		return modelMap;
	}
	/**
	 * 全功能导航
	 * 默认路径获取
	 */
	@RequestMapping(value = "/common/getUpdatePath.action")
	@ResponseBody
	public Map<String, Object> getUpdatePath(int id,String num){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap= sysnavigationService.getUpdatePath(id,num);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 全功能导航
	 * 升级说明及默认路径获取
	 */
	@RequestMapping(value = "/common/getUpdateInfo.action")
	@ResponseBody
	public Map<String, Object> getUpdateInfo(String num){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap= sysnavigationService.getUpdateInfo(num);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 全功能导航
	 * 数据更新
	 */
	@RequestMapping(value = "/common/updateNavigation.action")
	@ResponseBody
	public Map<String, Object> updateNavigation(int id,int addToId){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sysnavigationService.updateNavigation(id,addToId);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 全功能导航
	 * 检查是否有更新
	 */
	@RequestMapping(value = "/common/checkUpgrade.action")
	@ResponseBody
	public Map<String, Object> checkUpgrade(boolean upgrade){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("hasUpgrade", sysnavigationService.checkUpgrade());
		if(upgrade){
			sysnavigationService.refreshSysnavigation();
		}
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 拿到树所有节点信息
	 */
	@RequestMapping(value = "/common/getAllCheckTree.action")
	@ResponseBody
	public Map<String, Object> getAllCheckTree() throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<CheckBoxTree> tree = sysnavigationService.getAllCheckTree();
		modelMap.put("tree", tree);
		return modelMap;
	}
	
	/**
	 * 全功能导航
	 * 一键升级
	 */
	@RequestMapping(value = "/common/updateAllNavigation.action")
	@ResponseBody
	public Map<String, Object> updateAllNavigation(){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap=sysnavigationService.updateAllNavigation();
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 快速制单
	 */
	@RequestMapping(value = "/common/getAddBtn.action")
	@ResponseBody
	public Map<String, Object> getAddBtn(){
		Map<String, Object> modelMap = new HashMap<String, Object>();		
		modelMap.put("data", sysnavigationService.getAddBtn());
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获得自定义常用功能项
	 */
	@RequestMapping(value = "/common/getCommonUseTree.action")
	@ResponseBody
	public Map<String, Object> getCommonUseTree(HttpSession session) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		List<JSONTree> tree = sysnavigationService.getCommonUseTree(employee);
		modelMap.put("tree", tree);
		return modelMap;
	}
	
	/**
	 * 搜索自定义常用功能项
	 */
	@RequestMapping(value = "/common/searchCommonUseTree.action")
	@ResponseBody
	public Map<String, Object> searchCommonUseTree(HttpSession session, String value) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		List<JSONTree> tree = sysnavigationService.searchCommonUseTree(employee, value);
		modelMap.put("tree", tree);
		return modelMap;
	}
}
