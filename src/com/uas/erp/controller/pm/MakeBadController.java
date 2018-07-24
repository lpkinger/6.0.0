package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.MakeBadService;

@Controller
public class MakeBadController {

	@Autowired
	private MakeBadService makeBadService;
	
	/**序列号回车
	 * 判断序列号是否存在，        
	 * @param ms_sncode
	 * @param st_code
	 * @return
	 */
	@RequestMapping("/pm/mes/checkSNcode.action")
	@ResponseBody
	public Map<String, Object> checkSNcode(String ms_sncode, String st_code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", makeBadService.checkSNcode(ms_sncode, st_code));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除当前makebad不良记录
	 * @param mb_id
	 * @return
	 */
	@RequestMapping("/pm/mes/deleteMakeBad.action")
	@ResponseBody
	public Map<String, Object> deleteMakeBad(int mb_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBadService.deleteMakeBad(mb_id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 *保存不良记录：ID不存在则新增
	 * @param mb_id
	 * @return
	 */
	@RequestMapping("/pm/mes/addOrUpdateMakeBad.action")
	@ResponseBody
	public Map<String, Object> addOrUpdateMakeBad(String  data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
	    modelMap.put("data", makeBadService.addOrUpdateMakeBad(data));
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	/**
	 * 完成维修
	 * @param mb_id
	 * @return
	 */
	@RequestMapping("/pm/mes/finishFix.action")
	@ResponseBody
	public Map<String, Object> finishFix(String  data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
	    makeBadService.finishFix(data);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 报废
	 * @param data
	 * @return
	 */
	@RequestMapping("/pm/mes/makeBadScrap.action")
	@ResponseBody
	public Map<String, Object> makeBadScrap(String  data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
	    makeBadService.makeBadScrap(data);
		modelMap.put("success", true);
		return modelMap;
	}
	
}
