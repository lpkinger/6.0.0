package com.uas.erp.controller.pm;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.service.pm.PackageCollectionService;

@Controller
public class PackageCollectionController {
      @Autowired 
      private PackageCollectionService packageCollectionService;
    
      /**
       * 筛选已采集的序列号
       * @param caller
       * @param condition
       * @return
       */
  	@RequestMapping("/pm/mes/loadQueryGridStore.action")
  	@ResponseBody
  	public Map<String, Object> loadQueryGridStore(String caller, String condition) {
  		Map<String, Object> modelMap = new HashMap<String, Object>();
  		modelMap.put("data",packageCollectionService.loadQueryGridStore(caller, condition));
  		modelMap.put("success", true);
  		return modelMap;
  	}
  	
  	/**
  	 * 生成包装箱号
  	 * @param pa_totalqty
  	 * @param pa_prodcode
  	 * @param pr_id
  	 * @param pa_makecode
  	 * @return
  	 */
  	@RequestMapping("/pm/mes/generatePackage.action")
  	@ResponseBody
  	public Map<String, Object> generatePackage(double pa_totalqty, String pa_prodcode,String pr_id,String pa_makecode,String pa_outboxcode) {
  		Map<String, Object> modelMap = new HashMap<String, Object>();
  		modelMap.put("data",packageCollectionService.generatePackage(pa_totalqty,pa_prodcode,pr_id, pa_makecode,pa_outboxcode));
  		modelMap.put("success", true);
  		return modelMap;
  	}
  	 	
  	/**
  	 * 采集序列号入箱
  	 * @param condition
  	 * @return
  	 */
  	@RequestMapping("/pm/mes/getPackageDetail.action")
  	@ResponseBody
  	public Map<String, Object> getPackageDetail(String condition) {
  		Map<String, Object> modelMap = new HashMap<String, Object>();
  		modelMap.put("data",packageCollectionService.getPackageDetail(condition));
  		modelMap.put("success", true);
  		return modelMap;
  	}
  	
  	/**
  	 * 取消采集的序列号
  	 * @param condition
  	 * @return
  	 */
  	@RequestMapping("/pm/mes/clearPackageDetail.action")
  	@ResponseBody
  	public Map<String, Object> clearPackageDetail(String caller,String outbox,String sncode) {
  		Map<String, Object> modelMap = new HashMap<String, Object>();
  		packageCollectionService.clearPackageDetail(caller,outbox,sncode);
  		modelMap.put("success", true);
  		return modelMap;
  	}
  		
  	/**
  	 * 更新箱内容量
  	 * @param caller
  	 * @param data
  	 * @return
  	 */
  	@RequestMapping("/pm/mes/updatePackageQty.action")
  	@ResponseBody
  	public Map<String, Object> updatePackageQty(String caller,String pa_outboxcode,long pa_totalqty) {
  		Map<String, Object> modelMap = new HashMap<String, Object>();
  		packageCollectionService.updatePackageQty(caller,pa_outboxcode,pa_totalqty);
  		modelMap.put("success", true);
  		return modelMap;
  	}
  	
  	/**
  	 * 打印箱号以及箱内序列号
  	 * @param caller
  	 * @param data
  	 * @return
  	 * @throws IOException 
  	 */
  	@RequestMapping("/pm/mes/printPackageSN.action")
  	public void printPackageSN(HttpServletResponse response, HttpServletRequest request,String caller,String pa_outboxcode,long lps_id) throws IOException {		
		packageCollectionService.printPackageSN(caller,pa_outboxcode,lps_id);
		String size = "0";
		String text = "  ";
		InputStream in = new ByteArrayInputStream(text.getBytes());    
		size = String.valueOf(text.length());
		OutputStream os = response.getOutputStream();
		response.addHeader("Content-Disposition", "attachment;filename="
				+ new String("printBartender.txt".getBytes("utf-8"), "iso-8859-1"));
		response.addHeader("Content-Length",size);
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/octec-stream");
		int data = 0;
		while ((data = in.read()) != -1) {
			os.write(data);
		}
		in.close();
		os.close();
  	}
  
  	
  	@RequestMapping("/pm/mes/getTemplates.action")
  	@ResponseBody
  	public Map<String, Object> getPrintTemplates(String caller,String condition) {
  		Map<String, Object> modelMap = new HashMap<String, Object>();
  		modelMap.put("datas",packageCollectionService.getPrintTemplates(caller,condition));
  		modelMap.put("success", true);
  		return modelMap;
  	}
}
