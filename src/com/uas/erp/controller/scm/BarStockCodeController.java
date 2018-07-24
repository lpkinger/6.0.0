package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.BarStockCodeService;

@Controller
public class BarStockCodeController {
   @Autowired 
   private BarStockCodeService barStockCodeService;
	
	/**
	 * 批量生成条形码,既可以一次生成一条记录，也可以一次生成多条记录
	 * @param caller
	 * @param formStore
	 * @return
	 */	
	@RequestMapping("/scm/reserve/barStock/batchGenBarcode.action")  
	@ResponseBody 
	public Map<String, Object> batchGenBarcode(String caller,int id,String data){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barStockCodeService.batchGenBarcode(caller, id,data);
	    modelMap.put("success", true);	
		return modelMap;
	}
	
	@RequestMapping("/scm/reserve/barStock/saveBarcode.action")  
	@ResponseBody
	public  Map<String, Object> saveBarcode(String caller,String gridStore,String pd_inqty){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barStockCodeService.saveBarcode(caller, gridStore);
	    modelMap.put("success", true);	
		return modelMap;
	}
	
	/**
	 * 删除条码维护中产生的所有条码明细，包含删除关联的package，箱号明细packagedetail
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/scm/reserve/barStock/deleteAllBarDetails.action")
	@ResponseBody
	public Map<String, Object> deleteAllBarDetails(String caller,String  id ,String bddids) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barStockCodeService.deleteAllBarDetails(caller, id,bddids);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 批量生成条码和箱号,同批量生成条码一样，增加了生成箱号
	 * @param caller
	 * @param formStore
	 * @return
	 */
	@RequestMapping("/scm/reserve/barStock/BatchGenBO.action")  
	@ResponseBody 
	public Map<String, Object> batchGenBO(String caller,String formStore){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barStockCodeService.batchGenBO(caller, formStore);
	    modelMap.put("success", true);	
		return modelMap;
	}
}
