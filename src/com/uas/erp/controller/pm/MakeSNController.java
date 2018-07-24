package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MakeSNService;

@Controller
public class MakeSNController extends BaseController {
	@Autowired
	private MakeSNService makeSNService;


	/**
	 * 删除明细
	 */
	@RequestMapping("/pm/mes/deleteMakeSN.action")
	@ResponseBody
	public Map<String, Object> deleteMakeSerial(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeSNService.deleteMakeSN(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/pm/mes/makeSN/occurCode.action")
	@ResponseBody
	public Map<String, Object> occurCode(int id, String prefixcode, String suffixcode, String startno, int number){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeSNService.occurCode(id, prefixcode, suffixcode, startno, number);
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
	@RequestMapping("/pm/mes/makeSN/checkOrNewSerialCode.action")
	@ResponseBody
	public Map<String, Object> checkOrNewSerialCode(boolean newSerial,String serialCode,int ma_id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("message",makeSNService.checkOrNewBarcode(newSerial, serialCode, ma_id));
		modelMap.put("success", true);
		return modelMap;
	}
}
