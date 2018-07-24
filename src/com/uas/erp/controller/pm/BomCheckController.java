package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.BomCheckService;

@Controller
public class BomCheckController {

	@Autowired
	private BomCheckService bomCheckService;

	/************** BOM有效性检测 **************/

	/**
	 * 获得BOM 有效性验证的检测明细
	 * @return
	 */
	@RequestMapping("pm/bomCheck/getBomCheckItems.action")
	@ResponseBody
	public Map<String,Object> getBomCheckItems(String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", bomCheckService.getItems(caller));
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	/**
	 * Bom检测
	 * @param session
	 * @return
	 */
	@RequestMapping("pm/bomCheck/checkBom.action")
	@ResponseBody
	public Map<String, Object> bomCheck(String bomId,String bomMotherCode,String gridStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", bomCheckService.bomCheck(bomId,bomMotherCode,gridStore));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取BOMMessage，检测结果对应的
	 * @param session
	 * @return
	 */
	@RequestMapping("pm/bomCheck/getBomMessage.action")
	@ResponseBody
	public Map<String, Object> getBomMessage(String bomId,String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", bomCheckService.getBomMessage(bomId,type));
		modelMap.put("success", true);
		return modelMap;
	}

		
}
