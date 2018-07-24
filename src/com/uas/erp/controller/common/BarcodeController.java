package com.uas.erp.controller.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.common.BarcodeService;

@Controller
public class BarcodeController {
	
	
	@Autowired
	private BarcodeService barcodeService;
	
	@RequestMapping("common/barcode/Print.action")
	@ResponseBody
	public Map<String, Object> print(String caller,String lps_caller,String gridStore,String printForm){		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", barcodeService.barcodePrint(caller,lps_caller,gridStore,printForm));
		modelMap.put("success", true);
		return modelMap;			
	}
	
	@RequestMapping("common/barcode/PrintAll.action")
	@ResponseBody
	public Map<String, Object> printAll(String caller,String lps_caller,String printStore,String printForm){		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", barcodeService.barcodePrintAll(caller,lps_caller,printStore,printForm));
		modelMap.put("success", true);
		return modelMap;				
	}
	
	@RequestMapping("common/barcode/updatePrintStatus.action")
	@ResponseBody
	public Map<String, Object> updatePrintStatus(String caller,String ids){		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barcodeService.updatePrintStatus(caller,ids);
		modelMap.put("success", true);
		return modelMap;				
	}
	
	/**
	 * 打印条码维护中产生的所有条码明细
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/common/purchase/printPurBarcode.action")
	@ResponseBody
	public Map<String, Object> printPurBarcode(String caller,String gridStore,String printForm ) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", barcodeService.printPurBarcode(caller,gridStore,printForm));
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("common/purchase/printAllPurBarcode.action")
	@ResponseBody
	public Map<String, Object> printAllPurBarcode(String caller,String printStore,String printForm){		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", barcodeService.printAllPurBarcode(caller,printStore,printForm));
		modelMap.put("success", true);
		return modelMap;				
	}
	
	@RequestMapping("common/purchase/updatePurPrintStatus.action")
	@ResponseBody
	public Map<String, Object> updatePurPrintStatus(String caller,String ids){		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barcodeService.updatePurPrintStatus(caller,ids);
		modelMap.put("success", true);
		return modelMap;				
	}
	
}
