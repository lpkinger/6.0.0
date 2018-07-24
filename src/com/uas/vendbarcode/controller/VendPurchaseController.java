package com.uas.vendbarcode.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.vendbarcode.service.VendPurchaseService;

@Controller
public class VendPurchaseController {
	@Autowired
	private VendPurchaseService vendPurchaseService;
	
	//获取请购单列表
	@RequestMapping("/vendbarcode/datalist/getPurchaseList.action")
	@ResponseBody
	public Map<String, Object> getPurchaseList(String caller,String condition,Integer page,Integer start,Integer limit,HttpSession session) {	
		Object vendcode = session.getAttribute("ve_code");
		Map<String, Object> modelMap = new HashMap<String, Object >();
		modelMap=vendPurchaseService.getPurchaseList(caller,condition,page,start,limit,vendcode);
		modelMap.put("success", true);
		return modelMap;
	}
	//获取请购单form的数据
	@RequestMapping("/vendbarcode/datalist/getPurchaseForm.action")
	@ResponseBody
	public Map<String, Object> getPurchaseForm(String caller,Integer id) {	
		Map<String, Object> modelMap = new HashMap<String, Object >();
		modelMap.put("data", vendPurchaseService.getPurchaseForm(caller,id));
		modelMap.put("success", true);
		return modelMap;
	}
	//获取请购单grid的数据
		@RequestMapping("/vendbarcode/datalist/getPurchaseGrid.action")
		@ResponseBody
		public Map<String, Object> getPurchaseGrid(String caller,Integer id) {	
			Map<String, Object> modelMap = new HashMap<String, Object >();
			modelMap.put("data", vendPurchaseService.getPurchaseGrid(caller,id));
			modelMap.put("success", true);
			return modelMap;
		}
	
}
