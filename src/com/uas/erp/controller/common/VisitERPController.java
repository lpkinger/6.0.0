package com.uas.erp.controller.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Enterprise;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.common.VisitERPService;

@Controller
public class VisitERPController {

	@Autowired
	private VisitERPService visitERPService;
	@Autowired
	private EnterpriseService enterpriseService;
	
	@RequestMapping("/common/visitERP/getNameAndPwd.action")
	@ResponseBody
	public Map<String, Object> getNameAndPwd(String vendorId){
		Map<String, Object> map = visitERPService.getNameAndPwd(vendorId);
		return map;
	}
	
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/common/VisitERP/updateVAM.action")  
	@ResponseBody 
	public Map<String, Object> updateVAM(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		visitERPService.updateVAM(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/common/VisitERP/updateCAM.action")  
	@ResponseBody 
	public Map<String, Object> updateCAM(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		visitERPService.updateCAM(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/common/VisitERP/updateAM.action")  
	@ResponseBody 
	public Map<String, Object> updateAM(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		visitERPService.updateAM(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 跳转到登陆界面
	 * @param request
	 * @param master
	 * @return
	 */
	@RequestMapping("/common/VisitERP/login.action")  
	public ModelAndView login(HttpServletRequest request, String master) {
		ModelAndView model = new ModelAndView();
		Map<String, Object> map = visitERPService.getMasterAndEntpName(master);
		model.addObject("data", map);
		model.setViewName("/common/VisitERP/Login");
		return model;
	}
	
	@RequestMapping("/common/VisitERP/getActorNavigation.action")
	@ResponseBody
	public Map<String,Object> getActorNavigation(String type){
		Map<String,Object> modelMap=new HashMap<String, Object>();
		modelMap.put("data", visitERPService.getActorNavigation(type));
		modelMap.put("success", true);
		return modelMap;	
	}
	
	@RequestMapping("/common/VisitERP/BomSync.action")
	@ResponseBody
	public Map<String,Object> BomSync(String pr_code,String bomid,String data,String type) throws InterruptedException{
		Map<String,Object> modelMap=new HashMap<String, Object>();
		modelMap.put("data", visitERPService.BomSync(pr_code,bomid,data,type));
		modelMap.put("success", true);
		return modelMap;	
	}
	
	/**
	 * 快速登陆
	 * @param session
	 * @param request
	 * @return
	 */
	@RequestMapping("/common/VisitERP/customer.action")
	@ResponseBody
	public ModelAndView CustomerService(HttpServletRequest request, String username, String password,String cu_uu, 
			String accesskey, String master, String success) {
		Map<String,Object> params=new HashMap<String,Object>();
		Enterprise enterprise = enterpriseService.getEnterprise();
		HttpSession session = request.getSession(true);
		params.put("master", master);
		params.put("accesskey", accesskey);
		if("true".equals(success)){
			params = visitERPService.validCustomer(username, password, cu_uu, accesskey);
			if("true".equals(params.get("valid"))){
				session.setAttribute("role", params.get("role"));
				session.setAttribute("cu_uu", params.get("vcUU"));
				session.setAttribute("cu_name",enterprise.getEn_Name());
				Employee employee = new Employee();
				employee.setEm_code(username);
				employee.setEm_name("虚拟账号");
				employee.setEm_class("admin_virtual");
				employee.setEm_type("admin");
				employee.setEm_id(-99998);
				employee.setEm_master(master);
				employee.setCurrentMaster(getMaster(master));
				logSession(session, enterprise, employee);
				return new ModelAndView("opensys/customer/default", params);
			}else{
				request.setAttribute("masterName", enterpriseService.getMasterByName(master).getMa_function());
				request.setAttribute("enterpriseName", enterprise.getEn_Name());
				return new ModelAndView("/common/VisitERP/Login", params);
			}
		}else{
			request.setAttribute("masterName", enterpriseService.getMasterByName(master).getMa_function());
			request.setAttribute("enterpriseName", enterprise.getEn_Name());
			if(!"specail".equals(success)){
				params.put("error", "账号密码不正确!");
			}
			return new ModelAndView("/common/VisitERP/Login", params);
		}
	}
	/**
	 * 信息写到session
	 * 
	 * @param session
	 * @param enterprise
	 * @param employee
	 */
	private void logSession(HttpSession session, Enterprise enterprise, Employee employee) {
		session.setAttribute("employee", employee);
		session.setAttribute("en_uu", enterprise.getEn_uu());
		session.setAttribute("en_name", enterprise.getEn_Name());
		session.setAttribute("en_uu", enterprise.getEn_uu());
		session.setAttribute("en_name", enterprise.getEn_Name());
		session.setAttribute("em_uu", employee.getEm_uu());
		session.setAttribute("em_id", employee.getEm_id());
		session.setAttribute("em_name", employee.getEm_name());
		session.setAttribute("em_code", employee.getEm_code());
		session.setAttribute("em_position", employee.getEm_position());
		session.setAttribute("em_defaulthsid", employee.getEm_defaulthsid());
		session.setAttribute("em_defaultorid", employee.getEm_defaultorid());
		session.setAttribute("em_defaultorname", employee.getEm_defaultorname());
		session.setAttribute("em_depart", employee.getEm_depart());
		session.setAttribute("em_departmentcode", employee.getEm_departmentcode());
		session.setAttribute("em_type", employee.getEm_type());
		session.setAttribute("username", employee.getEm_code());
		session.setAttribute("enUU", employee.getVirtual_enuu());
		session.setAttribute("language", "zh_CN");
		SystemSession.setUser(employee);
		//UserOnlineListener.addUser(employee, session.getId());
	}
	
	@RequestMapping("/common/VisitERP/getGridStore.action")
	@ResponseBody
	public List<Map<String, Object>> getGridStore(String caller, String bomid, String cu_uu){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list.addAll(visitERPService.getGridStore(caller, bomid, cu_uu));
		return list;
	}
	
	@RequestMapping("/common/VisitERP/saveGridStore.action")
	@ResponseBody
	public Map<String, Object> saveGridStore(String data){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		try {
			visitERPService.SaveGridStore(data);
			modelMap.put("success", true);
		} catch (Exception e) {
			modelMap.put("success", false);
		}
		return modelMap;
	}
	
	@RequestMapping("/common/VisitERP/TurnTemplate.action")
	@ResponseBody
	public boolean TurnTemplate(HttpServletRequest request, String key, String data, String cu_uu, String type, String master){
		return visitERPService.specileTruenTemplate(request, key, data, cu_uu, type, master);
	}
	
	@RequestMapping("/common/VisitERP/validConvertTurn.action")
	@ResponseBody
	public Map<String, Object> validConvertTurn(int bomid, int cu_uu){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		boolean flag = visitERPService.validConvertTurn(bomid, cu_uu);
		modelMap.put("success", flag);
		return modelMap;
	}
	
	@RequestMapping("/common/VisitERP/TrunFormal.action")
	@ResponseBody
	public Map<String, Object> TrunFormal(int bomId, String formStore, String gridStore){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = visitERPService.TrunFormal(bomId, formStore, gridStore);
		return modelMap;
	}
	@RequestMapping("common/VisitERP/getCNTree.action")
	@ResponseBody
	public Map<String, Object> getCNTree(int parentId, String condition){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<JSONTree> tree = visitERPService.getCNTree(parentId, condition);
		modelMap.put("tree", tree);
		return modelMap;
	}
	
	@RequestMapping("common/VisitERP/saveCurnavigation.action")
	@ResponseBody
	public Map<String, Object> saveCurnavigation(String cn_title, String cn_url,String type){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		visitERPService.saveCurnavigation(cn_title, cn_url,type);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("common/VisitERP/updateCurnavigation.action")
	@ResponseBody
	public Map<String, Object> updateCurnavigation(String cn_id,String cn_title, String cn_url,String type){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		visitERPService.updateCurnavigation(cn_id,cn_title, cn_url,type);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("common/VisitERP/deleteCurnavigation.action")
	@ResponseBody
	public Map<String, Object> deleteCurnavigation(String cn_id,String type){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		visitERPService.deleteCurnavigation(cn_id,type);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/common/VisitERP/BomEnable.action")
	@ResponseBody
	public boolean BomEnable(String bo_mothercode,String ve_uu,String key,String master){
		return visitERPService.BomEnable(bo_mothercode,ve_uu,key,master);	
	}
	
	/**
	 * 取当前账套信息
	 * 
	 * @param masters
	 * @param name
	 * @return
	 */
	public Master getMaster(String name) {
		List<Master> masters = enterpriseService.getMasters();
		if (masters != null && name != null) {
			for (Master m : masters) {
				if (name.equals(m.getMa_name())) {
					return m;
				}
			}
		}
		return null;
	}

	@RequestMapping("/common/VisitERP/orderProcess.action")
	@ResponseBody
	public List<Map<String, Object>> BomEnable(String purchaseCode){
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		list = visitERPService.getOrderProcess(purchaseCode);
		return list;
	}
	
	@RequestMapping("/common/VisitERP/trunPreproduct.action")
	@ResponseBody
	public Map<String, Object> trunPreproduct(String data){
		Map<String, Object> map = new HashMap<String, Object>();
		map = visitERPService.turnPreproduct(data);
		map.put("success", true);
		return map;
	}
	
}

