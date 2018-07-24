package com.uas.vendbarcode.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseUtil;
import com.uas.vendbarcode.service.VendAcceptNotifyService;

@Controller
public class VendAcceptNotifyController {
	@Autowired
	private VendAcceptNotifyService  vendAcceptNotifyService;
	
	//获取请购单列表
		@RequestMapping("vendbarcode/datalist/getAcceptNotifyList.action")
		@ResponseBody
		public Map<String, Object> getPurchaseList(Integer page,String condition,Integer start,Integer limit,HttpSession session) {	
			Map<String, Object> modelMap = new HashMap<String, Object >();
			modelMap=vendAcceptNotifyService.getAcceptNotifyList(page,condition,start,limit,session.getAttribute("ve_code").toString());
			modelMap.put("success", true);
			return modelMap;
		}
		
		//采购单批量转送货通知单
		@RequestMapping("vendbarcode/batch/getPurchaseData.action")
		@ResponseBody
		public Map<String, Object> getPurchaseData(HttpSession session,String caller, String condition) {	
			Map<String, Object> modelMap = new HashMap<String, Object >();
			String vendcode = session.getAttribute("ve_code").toString();
			modelMap.put("data",vendAcceptNotifyService.getPurchaseData(caller,condition,vendcode));
			modelMap.put("success", true);
			return modelMap;
		}
		
		//获取请购单form的数据
		@RequestMapping("/vendbarcode/datalist/getAcceptNotifyForm.action")
		@ResponseBody
		public Map<String, Object> getPurchaseForm(String caller,Integer id) {	
			Map<String, Object> modelMap = new HashMap<String, Object >();
			modelMap.put("data", vendAcceptNotifyService.getAcceptNotifyForm(caller,id));
			modelMap.put("success", true);
			return modelMap;
		}
		//获取请购单grid的数据
		@RequestMapping("/vendbarcode/datalist/getAcceptNotifyGrid.action")
		@ResponseBody
		public Map<String, Object> getPurchaseGrid(String caller,Integer id) {	
			Map<String, Object> modelMap = new HashMap<String, Object >();
			modelMap.put("data", vendAcceptNotifyService.getAcceptNotifyGrid(caller,id));
			modelMap.put("success", true);
			return modelMap;
		}
		//更新按钮
		@RequestMapping("/vendbarcode/acceptNotify/update.action")
		@ResponseBody
		public Map<String, Object> update(String caller, String formStore, String param) {	
			Map<String, Object> modelMap = new HashMap<String, Object >();
			vendAcceptNotifyService.update(caller, formStore, param);
			modelMap.put("success", true);
			return modelMap;
		}
		//提交按钮
		@RequestMapping("/vendbarcode/acceptNotify/submit.action")
		@ResponseBody
		public Map<String, Object> submit(String caller, int id) {	
			Map<String, Object> modelMap = new HashMap<String, Object >();
			vendAcceptNotifyService.submit(caller, id);
			modelMap.put("success", true);
			return modelMap;
		}
		//反提交按钮
		@RequestMapping("/vendbarcode/acceptNotify/resSubmit.action")
		@ResponseBody
		public Map<String, Object> resSubmit(String caller, int id) {	
			Map<String, Object> modelMap = new HashMap<String, Object >();
			vendAcceptNotifyService.resSubmit(caller,id);
			modelMap.put("success", true);
			return modelMap;
		}
		//删除按钮
		@RequestMapping("/vendbarcode/acceptNotify/delete.action")
		@ResponseBody
		public Map<String, Object> delete(String caller, int id) {	
			Map<String, Object> modelMap = new HashMap<String, Object >();
			vendAcceptNotifyService.delete(caller,id);
			modelMap.put("success", true);
			return modelMap;
		}
		//确认送货按钮
		@RequestMapping("/vendbarcode/acceptNotify/confirmDelivery.action")
		@ResponseBody
		public Map<String, Object> confirmDelivery(String caller, int id) {	
			Map<String, Object> modelMap = new HashMap<String, Object >();
			vendAcceptNotifyService.confirmDelivery(caller,id);
			modelMap.put("success", true);
			return modelMap;
		}
		
		//删除按钮
		@RequestMapping("/vendbarcode/acceptNotify/cancelDelivery.action")
		@ResponseBody
		public Map<String, Object> cancelDelivery(String caller, int id) {	
			Map<String, Object> modelMap = new HashMap<String, Object >();
			vendAcceptNotifyService.cancelDelivery(caller,id);
			modelMap.put("success", true);
			return modelMap;
		}
		//生成条码按钮
		@RequestMapping("/vendbarcode/acceptNotify/batchGenBarcode.action")  
		@ResponseBody 
		public Map<String, Object> batchGenBarcode(String caller,int id,String data,HttpSession session){
			Map<String, Object> modelMap = new HashMap<String, Object>();
			vendAcceptNotifyService.batchGenBarcode(caller, id,data,session);
		    modelMap.put("success", true);	
			return modelMap;
		}
		//清除明细按钮
		@RequestMapping("/vendbarcode/acceptNotify/deleteAllBarDetails.action")
		@ResponseBody
		public Map<String, Object> deleteAllBarDetails(String caller,Integer an_id,String biids) {
			Map<String, Object> modelMap = new HashMap<String, Object>();
			vendAcceptNotifyService.deleteAllBarDetails(caller, an_id,biids);
			modelMap.put("success", true);
			return modelMap;
		}
		
		//清除明细按钮
		@RequestMapping("/vendbarcode/acceptNotify/vastTurnAccptNotify.action")
		@ResponseBody
		public Map<String, Object> vastTurnAccptNotify(HttpSession session,String caller, String data) {
			Map<String, Object> modelMap = new HashMap<String, Object>();
			modelMap.put("log", vendAcceptNotifyService.vastTurnAccptNotify(caller, data,session));
			modelMap.put("success", true);
			return modelMap;
		}
		//获取请购单关联列表
		@RequestMapping("vendbarcode/datalist/getAcceptNotifyListDetail.action")
		@ResponseBody
		public Map<String, Object> getPurchaseListDetail(Integer page,String condition,Integer start,Integer limit,HttpSession session) {	
			Map<String, Object> modelMap = new HashMap<String, Object >();
			modelMap=vendAcceptNotifyService.getAcceptNotifyListDetail(page,condition,start,limit,session.getAttribute("ve_code").toString());
			modelMap.put("success", true);
			return modelMap;
		}
		//获取当前登陆的账号
		@RequestMapping("vendbarcode/vendbarcode/getUser.action")
		@ResponseBody
		public Map<String, Object> getUser(HttpSession session,String caller) {	
			if(("Delivery!Deal").equals(caller)){	
				Map<String, Object> modelMap = new HashMap<String, Object >();
				if(session.getAttribute("ve_code") != null && !("").equals(session.getAttribute("ve_code"))){
					modelMap.put("data", session.getAttribute("ve_code").toString());
					modelMap.put("success", true);
					return modelMap;
				}else{
					BaseUtil.showError("您已丢失连接,请重新登录");
				}
			}
			return null;
		}
}
