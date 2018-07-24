package com.uas.erp.controller.hr;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.CheckBoxTree;
import com.uas.erp.model.CheckTree;
import com.uas.erp.model.HROrg;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.hr.HrOrgService;

@Controller
public class HrOrgController {
	@Autowired
	private HrOrgService hrOrgService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/hr/employee/saveHrOrg.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrOrgService.saveHrOrg(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/employee/updateHrOrg.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,HttpSession session) {
		boolean JobOrgNoRelation=(Boolean) session.getAttribute("joborgnorelation");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrOrgService.updateHrOrgById(formStore, caller,JobOrgNoRelation);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * Wsy 更新
	 */
	@RequestMapping("/hr/employee/updateHrOrgById.action")
	@ResponseBody
	public Map<String, Object> updateHRorg(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrOrgService.updateHrOrg(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/hr/employee/deleteHrOrg.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id,HttpSession session) {
		boolean JobOrgNoRelation=(Boolean) session.getAttribute("joborgnorelation");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrOrgService.deleteHrOrg(id, caller,JobOrgNoRelation);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 
	 * Wsy 删除
	 */
	@RequestMapping("/hr/employee/deleteHrOrgById.action")
	@ResponseBody
	public Map<String, Object> deleteHrOrg(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrOrgService.deleteHrOrgById(id);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/hr/employee/getHrOrgStrTree.action")
	@ResponseBody
	public Map<String, Object> getHrOrgStrTree(String caller, int parentid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<CheckBoxTree> tree = hrOrgService.getHrOrgStrTree(parentid,
				caller);
		modelMap.put("tree", tree);
		return modelMap;
	}
	/**
	 *组织架构树 
	 */
	@RequestMapping("/hr/employee/getAllHrOrgsTree.action")
	@ResponseBody
	public Map<String, Object> getAllHrOrgsTree(String caller,int parentId,HttpSession session) {
		boolean JobOrgNoRelation=(Boolean) session.getAttribute("joborgnorelation");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<CheckBoxTree> tree = hrOrgService.getAllHrOrgsTree(caller,parentId,JobOrgNoRelation);
		modelMap.put("tree", tree);
		return modelMap;
	}

	@RequestMapping("/hr/employee/getHrOrgTree.action")
	@ResponseBody
	public Map<String, Object> getHrOrgTree(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<CheckTree> tree = hrOrgService.getHrOrgTree(caller);
		modelMap.put("tree", tree);
		return modelMap;
	}

	@RequestMapping("/hr/employee/getHrOrgMap.action")
	@ResponseBody
	public Map<String,Object> getHrOrgMap(String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<CheckTree> tree = hrOrgService.getHrOrgMap(caller);
		modelMap.put("tree", tree);
		return modelMap;
	}
	
	@RequestMapping("/hr/employee/getHrOrg.action")
	@ResponseBody
	public Map<String, Object> getHrOrg(String caller, int em_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		HROrg hrOrg = hrOrgService.getHROrgByEmId(em_id);
		modelMap.put("hrOrg", hrOrg);
		return modelMap;
	}

	/*
	 * 从组织中删除员工
	 */
	@RequestMapping("/hr/HrOrgStrTree/deleteEmployee.action")
	@ResponseBody
	public Map<String, Object> deleteEmployee(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrOrgService.deleteEmployee(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/*
	 * 从更新员工的组织
	 */
	@RequestMapping("/hr/HrOrgStrTree/updateEmployee.action")
	@ResponseBody
	public Map<String, Object> updateEmployee(String caller, int em_id,
			int hrOrgid, String hrOrgName) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrOrgService.updateEmployee(em_id, hrOrgid, hrOrgName);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * @author zhouy 组织架构
	 * */
	@RequestMapping("/hr/employee/getHrOrgsTreeAndEmployees.action")
	@ResponseBody
	public Map<String, Object> getHrOrgsTreeAndEmployees(String caller,Integer parentId,HttpSession session) {
		boolean JobOrgNoRelation=(Boolean) session.getAttribute("joborgnorelation");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (parentId==null)parentId=0;
			List<JSONTree> tree = hrOrgService.getHrOrgsTreeAndEmployees(caller,parentId,JobOrgNoRelation);
			modelMap.put("tree", tree);
		return modelMap;
	}
	
	 
	  // @author chenp 代理商组织架构
	  
	@RequestMapping("/hr/employee/getAgentHrOrgsTreeAndEmployees.action")
	@ResponseBody
	public Map<String, Object> getAgentHrOrgsTreeAndEmployees(String caller,Integer parentId,HttpSession session) {
		boolean JobOrgNoRelation=true;
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (parentId==null)parentId=0;
			List<JSONTree> tree = hrOrgService.getAgentHrOrgsTreeAndEmployees(caller,parentId,JobOrgNoRelation);
			modelMap.put("tree", tree);
		return modelMap;
	}
	/**
	 * @author zhouy 刷新组织层级
	 * */
	@RequestMapping("/hr/refreshOrgLevel.action")
	@ResponseBody
	public Map<String, Object> refreshOrgLevel(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("result", hrOrgService.refreshOrgLevel());
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 刷新岗位层级
	 * @param caller
	 * @return
	 */
	@RequestMapping("/hr/refreshJobLevel.action")
	@ResponseBody
	public Map<String, Object> refreshPositionLevel(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("result", hrOrgService.refreshPositionLevel());
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * @author zhouy 刷新组织人员关系对照
	 * */
	@RequestMapping("/hr/refreshOrgEmployees.action")
	@ResponseBody
	public Map<String, Object> refreshOrgEmployees(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("result", hrOrgService.refreshOrgEmployees());
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * @author wsy 刷新下属岗位人员对照关系
	 * */
	@RequestMapping("/hr/refreshJobEmployees.action")
	@ResponseBody
	public Map<String, Object> refreshJobEmployees(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("result", hrOrgService.refreshJobEmployees());
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * @author zhouy 刷新组织架构树
	 * */
	@RequestMapping("/hr/refreshOrgJobEmployeeTree.action")
	@ResponseBody
	public Map<String, Object> refreshOrgJobEmployeeTree(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("result", hrOrgService.refreshOrgJobEmployeeTree(caller));
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/hr/getTreeNode.action")
	@ResponseBody
	public Map<String,Object> getTreeNode(int  parentId,String condition){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("result", hrOrgService.getTreeNode(parentId,condition));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * @author wusy 组织架构树
	 * */
	@RequestMapping("/hr/getChildTreeNode.action")
	@ResponseBody
	public Map<String,Object> getChildTreeNode(String condition){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("result", hrOrgService.getChildTreeNode(condition));
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/hr/addOrgByParent.action")
	@ResponseBody
	public Map<String,Object> addOrgByParent(int parentId){
		Map<String,Object>modelMap=new HashMap<String,Object>();
		modelMap.put("org", hrOrgService.addOrgByParent(parentId));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * @author wsy 添加组织   客户服务
	 */
	@RequestMapping("/hr/addOrg.action")
	@ResponseBody
	public Map<String,Object> addOrg(int parentId){
		Map<String,Object>modelMap=new HashMap<String,Object>();
		modelMap.put("org", hrOrgService.addOrg(parentId));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/hr/employee/auditHrOrg.action")
	@ResponseBody
	public Map<String, Object> auditHrOrg( int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrOrgService.auditHrOrg(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/hr/employee/resAuditHrOrg.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrOrgService.resAuditHrOrg(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获得组织下所有人员
	 * */
	@RequestMapping("/hr/hrorg/getEmployees.action")
	@ResponseBody
	public Map<String, Object> getEmployees(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<String, String> emp = hrOrgService.getEmployees(id);
		modelMap.put("emp", emp);
		return modelMap;
	}
	/**
	 *禁用
	 */
	@RequestMapping("hr/employee/bannedHrOrg.action")
	@ResponseBody 
	public Map<String, Object> bannedHrOrg(String caller,int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrOrgService.bannedHrOrg(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 *反禁用
	 */
	@RequestMapping("hr/employee/resBannedHrOrg.action")
	@ResponseBody 
	public Map<String, Object> resBannedHrOrg(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrOrgService.resBannedHrOrg(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获得组织下所有人员
	 * */
	@RequestMapping("/hr/hrorg/getHrogrs.action")
	@ResponseBody
	public Map<String, Object> getHrOrg() {
		return hrOrgService.getHrOrg();
	}
	
	/**
	 * 快速开账 保存组织
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping("/hr/employee/saveHrOrgAndEmp.action")
	@ResponseBody
	public Map<String, Object> saveHrOrgAndEmp(String param,String type) throws UnsupportedEncodingException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",hrOrgService.saveHrOrgAndEmp(param,type));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 保存HrOrg  SAAS  保存组织并添加作为部门
	 */
	@RequestMapping("/hr/employee/saveHrOrgSaas.action")
	@ResponseBody
	public Map<String, Object> saveHrOrgSaas(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrOrgService.saveHrOrgSaas(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 *  新增：删除HrOrg SAAS 删除组织并且删除对应部门
	 */
	@RequestMapping("/hr/employee/deleteHrOrgSaas.action")
	@ResponseBody
	public Map<String, Object> deleteHrOrgSaas(int id, String  caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrOrgService.deleteHrOrgSaas(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/employee/updateHrOrgSaas.action")
	@ResponseBody
	public Map<String, Object> updateHrOrgSaas(String formStore,HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrOrgService.updateHrOrgByIdSaas(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	/*
	 * saas快速开账，组织树
	 */
	@RequestMapping("/hr/employee/getHrOrgTreeSaas.action")
	@ResponseBody
	public Map<String, Object> getHrOrgTreeSaas() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<JSONTree> tree = hrOrgService.getHrOrgTreeSaas();
		modelMap.put("tree", tree);
		return modelMap;

	}
/*	*//**
	 * @author wsy
	 *//*
	@RequestMapping("/hr/hrorg/getField.action")
	@ResponseBody
	public Map<String,Object> getField(String caller,String em_code){
		Map<String,Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", hrOrgService.getHROrgByCode(em_code));
		modelMap.put("success",true);
		return modelMap;
	}
	*//**
	 * wusy
	 *//*
	@RequestMapping("/hr/hrorg/insertReadStatus.action")
	@ResponseBody
	public Map<String,Object> insertReadStatus(int status,int man,String sourcekind){
		Map<String,Object> modelMap = new HashMap<String,Object>();
		hrOrgService.insertReadStatus(status,man,sourcekind);
		modelMap.put("success", true);
		return modelMap;
	}
	*//**
	 * wusy
	 *//*
	@RequestMapping("/hr/hrorg/getStatus.action")
	@ResponseBody
	public Map<String,Object> getStatus(int man){
		Map<String,Object> modelMap = new HashMap<String,Object>();
		int sts = hrOrgService.getStatus(man);
		modelMap.put("success", true);
		modelMap.put("status", sts);
		return modelMap;
	} */
	
	/**
	 * 获取组织架构图横向显示level
	 * @return
	 */
	@RequestMapping("/hr/employee/getHrOrgMapLevel.action")
	@ResponseBody
	public Map<String, Object> getHrOrgMapLevel() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int level = hrOrgService.getHrOrgMapLevel();
		modelMap.put("level", level);
		return modelMap;

	}
	
	/**
	 * 更新组织架构图横向显示level
	 * @param level
	 * @return
	 */
	@RequestMapping("/hr/employee/updateHrOrgMapLevel.action")
	@ResponseBody
	public Map<String, Object> updateHrOrgMapLevel(int level) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrOrgService.updateHrOrgMapLevel(level);
		modelMap.put("success", true);
		return modelMap;
	}
	
}
