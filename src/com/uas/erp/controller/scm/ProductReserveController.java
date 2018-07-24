package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.ProductReserveService;

@Controller
public class ProductReserveController {
	@Autowired
	private ProductReserveService productReserveService;
	/**
	 * 保存product!reserve
	 * @param formStore form数据
	 * @param param 其它数据
	 */
	@RequestMapping("/scm/product/saveProductReserve.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productReserveService.saveProductReserve(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/product/updateProductReserve.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productReserveService.updateProductReserveById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * RefreshProdMonthNew
	 * */
	@RequestMapping("/scm/product/RefreshProdMonthNew.action")
	@ResponseBody
	public Map<String, Object> RefreshProdMonthNew(String caller, String currentMonth) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productReserveService.RefreshProdMonthNew(currentMonth, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 生成期末调整单
	 **/
	@RequestMapping("/scm/product/turnProductWHMonthAdjust.action")
	@ResponseBody
	public Map<String, Object> turnProductWHMonthAdjust(String caller, String currentMonth) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", productReserveService.turnProductWHMonthAdjust(currentMonth, caller));
		modelMap.put("success", true);
		return modelMap;
	}
}
