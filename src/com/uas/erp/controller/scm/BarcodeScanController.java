package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.BarcodeScanService;

@Controller
public class BarcodeScanController extends BaseController {
	@Autowired
	private BarcodeScanService barcodeScanService;

	@RequestMapping("/scm/sale/getProdioBarcode.action")
	@ResponseBody
	public Map<String, Object> getProdioBarcode(int piid, boolean iswcj) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", barcodeScanService.getProdioBarcode(piid, iswcj));
		return modelMap;
	}

	@RequestMapping("/scm/sale/insertProdioBarcode.action")
	@ResponseBody
	public Map<String, Object> insertProdioBarcode(int piid, String inoutno, String lotNo,String DateCode,String remark, String prcode, int qty) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barcodeScanService.insertProdioBarcode(piid, inoutno, lotNo, DateCode,remark,prcode, qty);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/scm/sale/deleteProdioBarcode.action")
	@ResponseBody
	public Map<String, Object> deleteProdioBarcode(int piid, String inoutno, String lotNo, String prcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barcodeScanService.deleteProdioBarcode(piid, inoutno, lotNo, prcode);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/scm/sale/clearProdioBarcode.action")
	@ResponseBody
	public Map<String, Object> clearProdioBarcode(int piid, String prcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barcodeScanService.clearProdioBarcode(piid, prcode);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/sale/printBarcode.action")
	@ResponseBody
	public Map<String, Object> printBarcode(int id, String reportName,HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("info", barcodeScanService.printBarcode(id,reportName,request));
		map.put("success", true);
		return map;
	}
}
