package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.GenerateBarcodeService;

@Controller	
public class GenerateBarcodeController {
	@Autowired  GenerateBarcodeService generateBarcodeService;
	
	@RequestMapping("/scm/reserve/getBarFormStore.action")  
	@ResponseBody 
	public Map<String, Object> getBarFormStore(String caller,String condition){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (condition != null && !"".equals(condition)) {
			String data = generateBarcodeService.getBarFormStore(caller, condition);
			if (data != null) {
				modelMap.put("data", data);
			}
		}		
		return modelMap;
	}

	/**
	 * 批量生成条形码，批量生成条形码，可以一次生成一条记录，也可以一次生成多条记录，可以多次生成
	 * @param caller
	 * @param formStore
	 * @return
	 */
	
	@RequestMapping("scm/reserve/batchGenBarcode.action")  
	@ResponseBody 
	public Map<String, Object> batchGenBarcode(String caller,int id,String data){
		Map<String, Object> modelMap = new HashMap<String, Object>();
        generateBarcodeService.batchGenBarcode(caller, id,data);
	    modelMap.put("success", true);	
		return modelMap;
	}
	
	@RequestMapping("scm/reserve/saveBarcode.action")  
	@ResponseBody
	public  Map<String, Object> saveBarcode(String caller,String gridStore,String pd_inqty){
		Map<String, Object> modelMap = new HashMap<String, Object>();
        generateBarcodeService.saveBarcode(caller, gridStore);
	    modelMap.put("success", true);	
		return modelMap;
	}
	
	/**
	 * 删除条码维护中产生的所有条码明细
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/scm/reserve/deleteAllBarDetails.action")
	@ResponseBody
	public Map<String, Object> deleteAllBarDetails(String caller,Integer pi_id,String biids) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		generateBarcodeService.deleteAllBarDetails(caller, pi_id,biids);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/reserve/getFields.action")
	@ResponseBody
	public Map<String, Object> getDatasFields(String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("datas", generateBarcodeService.getDatasFields(condition));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 批量生成条码和箱号
	 * @param caller
	 * @param data
	 * @return
	 */
	@RequestMapping("/scm/reserve/batchGenBarOBcode.action")
	@ResponseBody
	public Map<String, Object> batchGenBarOBcode(String caller,String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		generateBarcodeService.batchGenBarOBcode(caller,formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量冻结条码
	 */
	@RequestMapping("/scm/reserve/freezeBarcode.action")
	@ResponseBody
	public Map<String, Object> freezeBarcode(String caller, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		generateBarcodeService.freezeBarcode(caller, condition);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 批量释放条码
	 */
	@RequestMapping("/scm/reserve/releaseBarcode.action")
	@ResponseBody
	public Map<String, Object> releaseBarcode(String caller, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		generateBarcodeService.releaseBarcode(caller, condition);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 拆分条码
	 */
	@RequestMapping("/scm/reserve/breakingBatch.action")
	@ResponseBody
	public Map<String, Object> breakingBatch(String or_barcode, Double or_remain, Double bar_remain) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", generateBarcodeService.breakingBatch(or_barcode, or_remain,bar_remain));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 拆分合并条码
	 */
	@RequestMapping("/scm/reserve/combiningAndBreaking.action")
	@ResponseBody
	public Map<String, Object> combiningAndBreaking(String ids, Double total_remain,Double zxbzs,String every_remain) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", generateBarcodeService.combiningAndBreaking(ids, total_remain,zxbzs,every_remain));
		modelMap.put("success", true);
		return modelMap;
	}
	
}
