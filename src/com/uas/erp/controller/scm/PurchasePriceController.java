package com.uas.erp.controller.scm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.common.FilePathService;
import com.uas.erp.service.scm.PurchasePriceService;

@Controller
public class PurchasePriceController extends BaseController {
	@Autowired
	private PurchasePriceService purchasePriceService;
	@Autowired
	private FilePathService filePathService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/savePurchasePrice.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchasePriceService.savePurchasePrice(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/purchase/deletePurchasePrice.action")  
	@ResponseBody 
	public Map<String, Object> deletePurchasePrice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchasePriceService.deletePurchasePrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/updatePurchasePrice.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchasePriceService.updatePurchasePriceById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/purchase/printPurchasePrice.action")  
	@ResponseBody 
	public Map<String, Object> printPurchasePrice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchasePriceService.printPurchasePrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/purchase/submitPurchasePrice.action")  
	@ResponseBody 
	public Map<String, Object> submitPurchasePrice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchasePriceService.submitPurchasePrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitPurchasePrice.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitPurchasePrice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchasePriceService.resSubmitPurchasePrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/purchase/auditPurchasePrice.action")  
	@ResponseBody 
	public Map<String, Object> auditPurchasePrice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchasePriceService.auditPurchasePrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/purchase/resAuditPurchasePrice.action")  
	@ResponseBody 
	public Map<String, Object> resAuditPurchasePrice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchasePriceService.resAuditPurchasePrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 禁用PurchasePrice
	 */
	@RequestMapping("/scm/purchase/bannedPurchasePrice.action")  
	@ResponseBody 
	public Map<String, Object> banned(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchasePriceService.bannedPurchasePrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反禁用PurchasePrice
	 */
	@RequestMapping("/scm/purchase/resBannedPurchasePrice.action")  
	@ResponseBody 
	public Map<String, Object> resBanned(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchasePriceService.resBannedPurchasePrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("scm/purchaseprice/ImportExcel.action")
	@ResponseBody
	 public String ImportExcel(String caller, int id,int fileId) {
		InputStream is = null;
		String filePath=filePathService.getFilepath(fileId);	
		Workbook wbs=null;
		try{		
			String ft = filePath.substring(filePath.lastIndexOf(".") + 1);
			is = new FileInputStream(new File(filePath));
			if(ft.equals("xls")) {
				wbs = new HSSFWorkbook(is);
			}else if(ft.equals("xlsx")){
				wbs = new XSSFWorkbook(is);

			}else {
				return new String("{error: 'excel文件的格式不太规范,导入失败<hr>可以尝试将文件另存为,然后导入'}".getBytes("utf-8"), "iso8859-1");
			}
			 boolean bool=purchasePriceService.ImportExcel(id,wbs,filePath.substring(filePath.lastIndexOf("/")+1), caller);
			if(bool){  
				//Excel 解析成功之后要删除
				File file = new File(filePath);   
				// 路径为文件且不为空则进行删除   
				if (file.isFile() && file.exists()) {
					file.delete();
				}   
			}
			String r = "{success: true}";
			return r;
		} catch (Exception e){
			e.printStackTrace();
			try {
				return new String("{error: 'excel文件的格式不太规范,导入失败<hr>可以尝试将文件另存为,然后导入'}".getBytes("utf-8"), "iso8859-1");
			} catch (UnsupportedEncodingException e1) {
				return "{success: false}";
			}
		} finally {
			try {
				is.close();
			} catch (IOException e) {

			}
		}
	}
	/**
	 * 明细价格失效
	 */
	@RequestMapping("/scm/purchase/abatepurchaseprice.action")  
	@ResponseBody 
	public Map<String, Object> abatepurchaseprice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchasePriceService.abatepurchasepricestatus(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/purchase/resabatepurchaseprice.action")  
	@ResponseBody 
	public Map<String, Object> resabatepurchaseprice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchasePriceService.resabatepurchasepricestatus(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 *  明细价格转未认定
	 */
	@RequestMapping("/scm/purchase/resAppstatuspurchaseprice.action")  
	@ResponseBody 
	public Map<String, Object> resAppstatuspurchaseprice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchasePriceService.resappstatuspurchaseprice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 *明细价格转合格
	 */
	@RequestMapping("/scm/purchase/appstatuspurchaseprice.action")  
	@ResponseBody 
	public Map<String, Object> appstatuspurchaseprice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchasePriceService.appstatuspurchaseprice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 
	 */
	
	
	/**
	 * 复制核价单
	 * 
	 * @param session
	 * @param id
	 * @param caller
	 * @return
	 */
	@RequestMapping("/scm/purchase/copyPurchasePrice.action")
	@ResponseBody
	public Map<String, Object> copyPurchase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", purchasePriceService.copyPurchasePrice(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}
}
