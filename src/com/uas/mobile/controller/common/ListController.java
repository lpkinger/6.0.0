package com.uas.mobile.controller.common;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.interceptor.InterceptorUtil;
import com.uas.erp.core.support.MobileSessionContext;
import com.uas.erp.model.Employee;
import com.uas.erp.model.GridPanel;
import com.uas.erp.service.common.DbfindService;
import com.uas.mobile.model.ListView;
import com.uas.mobile.service.ListService;
/**
 *mobile-list
 * */
@Controller
public class ListController {
	@Autowired
	private ListService listService;
	@Autowired
	private DbfindService dbfindService;
	
	@RequestMapping(value = "/mobile/common/list.action")
	@ResponseBody
	public Map<String,Object> getDataListGrid(HttpServletRequest req, HttpSession session,String currentMaster,String caller,
			String condition, int page, int pageSize, Integer _f, String orderby,String sessionId) {
		Map<String,Object> map=new HashMap<String, Object>(); 
		Boolean _self = (Boolean) req.getAttribute("_self");	
		Employee employee=session.getAttribute("employee")!=null? (Employee)session.getAttribute("employee"):(Employee)MobileSessionContext.getInstance().getSessionById(sessionId).getAttribute("employee");
		ListView  view=listService.getListGridByCaller(caller, condition, page, pageSize, orderby, _self, _f,employee,currentMaster);
        map.put("listdata", view.getListdata());
        map.put("columns", view.getColumns());
        map.put("keyField", view.getKeyField());
        map.put("pfField", view.getPfField());
        map.put("sessionId", req.getSession().getId());
		return map;
	}
	@RequestMapping(value = "/mobile/common/listconditions.action")
	@ResponseBody
    public Map<String,Object> getAllQueryConditions(String caller){
		Map<String,Object> map=new HashMap<String, Object>(); 
		map.put("conditions", listService.getAllConditionsByCaller(caller));
    	return map;
    	
    }
	@RequestMapping(value ="/mobile/common/getMobileQuerys.action")
	@ResponseBody
	public Map<String,Object> getMobileQuerys(HttpServletRequest request,HttpSession  session,String sessionId){
		Map<String,Object> map=new HashMap<String, Object>(); 
		Employee employee=session.getAttribute("employee")!=null? (Employee)session.getAttribute("employee"):(Employee)MobileSessionContext.getInstance().getSessionById(sessionId).getAttribute("employee");
		map.put("querys", listService.getMobileQuerys(employee));
		map.put("sessionId", request.getSession().getId());
    	return map;
    	
    }
	
	//手机端取combos
	@RequestMapping(value ="/mobile/common/getCombo.action")
	@ResponseBody
	public Map<String,Object> getCombo(HttpServletRequest request,HttpSession session, String caller, String field,String sessionId){
		Map<String,Object> map=new HashMap<String, Object>(); 
		Employee employee=session.getAttribute("employee")!=null? (Employee)session.getAttribute("employee"):(Employee)MobileSessionContext.getInstance().getSessionById(sessionId).getAttribute("employee");
		map.put("combdatas", listService.getCombByCaller(caller,field,employee));
		map.put("sessionId", request.getSession().getId());
		return map;
    	
    }
	
	//手机端取combos,返回显示值和实际值
	@RequestMapping(value ="/mobile/common/getComboValue.action")
	@ResponseBody
	public Map<String,Object> getComboValue(HttpServletRequest request,HttpSession session, String caller, String field,String sessionId){
		Map<String,Object> map=new HashMap<String, Object>(); 
		map.put("combdatas", listService.getCombValueByCaller(caller,field));
		map.put("sessionId", request.getSession().getId());
		return map;
    	
    }
	
	//手机端取关联明细表名称个数
    @RequestMapping(value ="/mobile/common/getAuditDetail.action")
	@ResponseBody
	public Map<String,Object> getAuditDetail(HttpSession session, String caller){
		Map<String,Object> map=new HashMap<String, Object>(); 
		//Employee employee=session.getAttribute("employee")!=null? (Employee)session.getAttribute("employee"):(Employee)MobileSessionContext.getInstance().getSessionById(sessionId).getAttribute("employee");
		map.put("detail", listService.getAuditDetail(caller));
	    return map;	    	
	  }
    
	//手机端取dbfind
	@RequestMapping(value ="/mobile/common/getDbfind.action")
	@ResponseBody
	public Map<String,Object> getDbfind(HttpSession session, String which, String caller, String field, String condition,Integer page, Integer pageSize,String sessionId){
		Map<String,Object> map=new HashMap<String, Object>(); 
		if (page==null || page == 0)
			page = 1;
		if(pageSize==null || pageSize==0)
			pageSize=10;
		if(condition==null)condition="1=1";
		if (which.equals("grid")) {
			//map.put("datas", listService.getDbfindGridByCaller(caller, condition,page, pageSize));
		} else {
			map.put("datas", listService.getDbfindGridByField(caller, field, condition, page, pageSize));
		}		
    	return map;
    	
    }
	
	//手机端根据mobileformdetail和mobiledetailgrid取数据
	@RequestMapping(value ="/mobile/common/getformandgriddata.action")
	@ResponseBody
	public Map<String,Object> getFormAndGridData(HttpServletRequest request, String caller, String id,String isprocess,String config){
		Map<String,Object> map=new HashMap<String, Object>(); 
		map.put("datas", listService.getFormAndGridData(caller, id,isprocess,config,request.getSession()));
		map.put("success", true);
		map.put("sessionId", request.getSession().getId());
    	return map;
    	
    }
	
	/**
	 * 手机端grid的dbfind
	 */
	@RequestMapping(value = "/mobile/common/dbfind.action")
	@ResponseBody
	public Map<String, Object> getGrid(HttpServletRequest req,HttpSession session, String which, String caller, String field, String condition, String ob,
			int page, int pageSize,String gridCaller,String gridField) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		GridPanel gridPanel = new GridPanel();
		String language = (String) session.getAttribute("language");
		boolean isCloud=Constant.UAS_CLOUD.equals(req.getParameter("_config"));
		if (which.equals("grid")) {
			gridPanel = dbfindService.getDbfindGridByCaller(caller, condition, ob, page, pageSize, language,isCloud);
			modelMap.put("gridDbfinds",listService.getGridDbfinds(gridCaller,gridField));
		} else {
			gridPanel = dbfindService.getDbfindGridByField(caller, field, condition, page, pageSize,isCloud);
		}
		modelMap.put("dbfinds", gridPanel.getDbfinds());
		modelMap.put("fields", gridPanel.getGridFields());
		modelMap.put("columns", gridPanel.getGridColumns());
		modelMap.put("data", gridPanel.getDataString());
		modelMap.put("reset", gridPanel.isAllowreset());
		modelMap.put("autoHeight", gridPanel.isAutoHeight());
		return modelMap;
	}
	
	/**
	 * 获取所有服务信息
	 */
	@RequestMapping("/mobile/common/getServices.action")  
	@ResponseBody 
	public Map<String, Object> getServices(HttpServletRequest request, HttpSession session, String kind, String type, String sessionId) {
		String basePath = BaseUtil.getBasePath(request);
		Employee employee=session.getAttribute("employee")!=null? (Employee)session.getAttribute("employee"):(Employee)MobileSessionContext.getInstance().getSessionById(sessionId).getAttribute("employee");
		boolean noControl = false;
		if (employee!=null) {
			noControl = InterceptorUtil.noControl(request, employee);
		}
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("configs", listService.getServices(basePath, employee, kind, type, noControl));
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}
	
}
