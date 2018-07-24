package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MakeSerialService;

@Controller
public class MakeSerialController extends BaseController {
	@Autowired
	private MakeSerialService makeSerialService;


	/**
	 * 删除明细
	 */
	@RequestMapping("/pm/mes/deleteMakeSerial.action")
	@ResponseBody
	public Map<String, Object> deleteMakeSerial(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeSerialService.deleteMakeSerial(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mes/updateMakeSerial.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeSerialService.updateMakeSerialById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mes/makeSerial/occurCode.action")
	@ResponseBody
	public Map<String, Object> occurCode(int id, String prefixcode, String suffixcode, String startno, int number,int combineqty){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeSerialService.occurCode(id, prefixcode, suffixcode, startno, number,combineqty);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 补打条码判断新生成还是原有条码
	 * @param newSerial
	 * @param serialCode
	 * @param mc_id
	 * @return
	 */
	@RequestMapping("/pm/mes/makeSerial/checkOrNewSerialCode.action")
	@ResponseBody
	public Map<String, Object> checkOrNewSerialCode(boolean newSerial,String serialCode,int mc_id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("message",makeSerialService.checkOrNewBarcode(newSerial, serialCode, mc_id));
		modelMap.put("success", true);
		return modelMap;
	}
}
