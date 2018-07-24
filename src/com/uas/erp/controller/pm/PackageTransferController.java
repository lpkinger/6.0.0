package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.PackageTransferService;

@Controller
public class PackageTransferController {
	@Autowired
	private PackageTransferService packageTransferService;
	
	/**
     * 根据原有的箱号生成新的箱号
     * @param caller
     * @param condition
     * @return
     */
	@RequestMapping("/pm/mes/generateNewPackage.action")
	@ResponseBody
	public Map<String, Object> generateNewPackage( double pa_totalqtynew,String pa_oldcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",packageTransferService.generateNewPackage(pa_totalqtynew,pa_oldcode));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 包装转移采集序列号
	 * @param condition
	 * @return
	 */
	@RequestMapping("/pm/mes/getPackageDetailSerial.action")
	@ResponseBody
	public Map<String, Object> getPackageDetailSerial(String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		packageTransferService.getPackageDetailSerial(condition);
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	/**
	 * 获取，判断原箱号是否存在，判断目标箱号是否存在,并且是同一工单号
	 * @param condition
	 * @return
	 */
	@RequestMapping("/pm/mes/getFormTStore.action")
	@ResponseBody
	public Map<String, Object> getFormTStore(String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",packageTransferService.getFormTStore(condition));
		modelMap.put("success", true);
		return modelMap;
	}
	
}
