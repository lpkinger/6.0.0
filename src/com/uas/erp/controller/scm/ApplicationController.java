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
import com.uas.erp.service.scm.ApplicationService;

@Controller
public class ApplicationController extends BaseController {
	@Autowired
	private ApplicationService applicationService;
	@Autowired
	private FilePathService filePathService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/saveApplication.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		applicationService.saveApplication(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/purchase/deleteApplication.action")  
	@ResponseBody 
	public Map<String, Object> deleteApplication(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		applicationService.deleteApplication(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/updateApplication.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		applicationService.updateApplicationById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/purchase/printApplication.action")  
	@ResponseBody 
	public Map<String, Object> printApplication(int id,String reportName,String condition, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys=applicationService.printApplication(id, caller ,reportName,condition);
		modelMap.put("success", true);
		modelMap.put("keyData",keys);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/purchase/submitApplication.action")  
	@ResponseBody 
	public Map<String, Object> submitApplication(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		applicationService.submitApplication(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitApplication.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitApplication(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		applicationService.resSubmitApplication(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/purchase/auditApplication.action")  
	@ResponseBody 
	public Map<String, Object> auditApplication(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		applicationService.auditApplication(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/purchase/resAuditApplication.action")  
	@ResponseBody 
	public Map<String, Object> resAuditApplication(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		applicationService.resAuditApplication(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转采购
	 */
	@RequestMapping("/scm/purchase/turnPurchase.action")  
	@ResponseBody 
	public Map<String, Object> turnPurchase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int puid = applicationService.turnPurchase(id, caller);
		modelMap.put("id", puid);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 到PurchasePrice取供应商
	 */
	@RequestMapping("/scm/purchase/getVendor.action")  
	@ResponseBody 
	public Map<String, Object> getVendor(int[] id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		applicationService.getVendor(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 到PurchasePrice取模材供应商
	 */
	@RequestMapping("/scm/purchase/getMCVendor.action")  
	@ResponseBody 
	public Map<String, Object> getMCVendor(int[] id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		applicationService.getMCVendor(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 到PurchasePrice取委外供应商
	 */
	@RequestMapping("/scm/purchase/getVendorByCaller.action")  
	@ResponseBody 
	public Map<String, Object> getOSMVendor(int[] id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		applicationService.getVendorByCaller(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 请购单批量抛转
	 * @param id 请购单ID
	 * @param ma_id 帐套ID
	 */
	@RequestMapping(value="/scm/purchase/postAppliaction.action")
	@ResponseBody
	public Map<String, Object> vastPost(int[] id, int ma_id, String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", applicationService.postApplication(id, ma_id));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 导入Excel
	 * */
	@RequestMapping(value="scm/application/ImportExcel.action")
	@ResponseBody
    public String ImportExcel(int id,int fileId, String caller) {
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
			 boolean bool=applicationService.ImportExcel(id,wbs,filePath.substring(filePath.lastIndexOf("/")+1));
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
	 * 提交
	 */
	@RequestMapping("/scm/purchase/applicationdataupdate.action")  
	@ResponseBody 
	public Map<String, Object> applicationdataupdate(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 更新明细数量
	 */
	@RequestMapping("/scm/purchase/updateQty.action")  
	@ResponseBody 
	public Map<String, Object> updateQty(String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		applicationService.updateQty(data);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 请购单批量转工单
	 */
	@RequestMapping(value = "/scm/vastApplicationTurnMake.action")
	@ResponseBody
	public Map<String, Object> vastTurnMake(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", applicationService.ApplicationTurnMake(caller,data));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 英唐集团  请购单抛转  maz 18-05-25
	 */
	@RequestMapping("/scm/purchase/postApplication.action")  
	@ResponseBody 
	public Map<String, Object> postApplication(String caller,String data,String to) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log",applicationService.postApplication(caller,data,to));
		modelMap.put("success", true);
		return modelMap;
	}
}
