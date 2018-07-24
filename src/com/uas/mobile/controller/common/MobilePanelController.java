package com.uas.mobile.controller.common;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.Employee;
import com.uas.mobile.model.Panel;
import com.uas.mobile.service.PanelService;


@Controller
public class MobilePanelController {
	
	@Autowired
	private PanelService panelService;
	
	
	@RequestMapping(value = "/mobile/common/getPanel.action")
	@ResponseBody
	public Map<String,Object> getPanel(HttpServletRequest req, String caller,String formCondition,String gridCondition) {
		Map<String,Object> map=new HashMap<String, Object>(); 
		Employee employee=(Employee)req.getSession().getAttribute("employee");
		if(employee==null) BaseUtil.showError("会话已断开!");
		Panel panel=panelService.getPanelByCaller(caller, formCondition, gridCondition,employee.getEm_code());
        map.put("panelItems",panel.getPanelItems());
        map.put("panelData",panel.getFormdata());
        map.put("detailColumns", panel.getColumns());
        map.put("detailDatas", panel.getListdata());
        map.put("sessionId", req.getSession().getId());
		return map;
	}
	@RequestMapping(value="/mobile/common/getProductDetail.action")
	@ResponseBody
	public Map<String,Object> getProductDetail(HttpServletRequest req,String code){
		Map<String,Object> map=new HashMap<String, Object>(); 
		map.put("data", panelService.getProductDetail(code));
		return map;
	}
	
	//移动端获取formdetail和detailgrid数据
	@RequestMapping(value="/mobile/common/getformandgriddetail.action")
	@ResponseBody
	public Map<String,Object> getFormAndGridDetail(HttpServletRequest req,String caller,String condition,String isprocess,String config){
		Map<String,Object> map=new HashMap<String, Object>(); 
		map.put("data", panelService.getFormAndGridDetail(caller,condition,isprocess,config,req.getSession()));
		/** OA表单获取配置接口修改,返回form配置的主键、状态码等信息 */
		map.put("config", panelService.getFormConfig(caller));
		map.put("sessionId", req.getSession().getId());
		map.put("success", true);
		return map;
	}
	
	//移动端更新formdetail和detailgrid的数据
	@RequestMapping(value="/mobile/common/updateformandgriddetail.action")
	@ResponseBody
	public Map<String,Object> updateFormAndGridDetail(HttpServletRequest req,String formStore,String gridStore){
		Map<String,Object> map=new HashMap<String, Object>(); 
		panelService.updateDetailData(formStore,gridStore);
		map.put("sessionId", req.getSession().getId());
		map.put("success", true);
		return map;
	}
	
	//移动端更新mobileformdetail和mobiledetailgrid的数据
	@RequestMapping(value="/mobile/common/updatemobiledefault.action")
	@ResponseBody
	public Map<String,Object> updateMobileDefault(HttpServletRequest req,String caller,String formStore){
		Map<String,Object> map=new HashMap<String, Object>(); 
		panelService.updateMobileDefault(caller,formStore);
		map.put("sessionId", req.getSession().getId());
		map.put("success", true);
		return map;
	}
	
	//移动端更新formdetail和detailgrid的mobileused
	@RequestMapping(value="/mobile/common/updatemobileused.action")
	@ResponseBody
	public Map<String,Object> updateMobileused(HttpServletRequest req,String caller,String formStore){
		Map<String,Object> map=new HashMap<String, Object>(); 
		panelService.updateMobileused(caller,formStore);
		map.put("sessionId", req.getSession().getId());
		map.put("success", true);
		return map;
	}
	
	//mobileformdetail和mobileformdetail删除方法
	@RequestMapping(value="/mobile/common/deletemobilefields.action")
	@ResponseBody
	public Map<String,Object> deleteMobileFields(HttpServletRequest req,String caller,String fields){
		Map<String,Object> map=new HashMap<String, Object>(); 
		panelService.deleteMobileFields(caller,fields);
		map.put("sessionId", req.getSession().getId());
		map.put("success", true);
		return map;
	}
	
		//移动端获取formdetail的数据
		@RequestMapping(value="/mobile/common/getFormPanel.action")
		@ResponseBody
		public Map<String,Object> getFormPanel(HttpServletRequest req,String caller){
			Map<String,Object> map=new HashMap<String, Object>(); 
			map.put("data", panelService.getFormPanel(req.getSession(),caller));
			map.put("success", true);
			return map;
		}
		
		//移动端获取detailgrid数据,根据条件获取明细表数据
		@RequestMapping(value="/mobile/common/getGridPanel.action")
		@ResponseBody
		public Map<String,Object> getGridPanel(String caller,String condition){
			Map<String,Object> map=new HashMap<String, Object>(); 
			map = panelService.getGridPanel(caller, condition);
			return map;
		}
		
		//反提交后获取配置和数据
		@RequestMapping(value="/mobile/common/getFormPanelAndData.action")
		@ResponseBody
		public Map<String,Object> getFormPanelAndData(HttpServletRequest req,String caller,Integer id,String condition){
			Map<String,Object> map=new HashMap<String, Object>(); 
			map.put("data", panelService.getFormPanelAndData(req.getSession(),caller,id,condition));
			map.put("success", true);
			return map;
		}
		/*
		 * 移动端获取明细数据添加分页操作
		 * 
		 */
		@RequestMapping(value="/mobile/common/getGridPanelandDataPage.action")
		@ResponseBody
		public Map<String,Object> getGridPanelandDataPage(String caller,String condition,int page,int pageSize){
			Map<String,Object> map=new HashMap<String, Object>(); 
			map = panelService.getGridPanelandDataPage(caller, condition,page,pageSize);
			return map;
		}
		
}
