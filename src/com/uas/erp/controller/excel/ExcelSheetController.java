package com.uas.erp.controller.excel;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.excel.ExcelFileService;
import com.uas.erp.service.excel.ExcelSheetService;
@Controller
public class ExcelSheetController extends BaseController{

	@Autowired
	private ExcelFileService excelFileService;
	@Autowired
	private ExcelSheetService excelSheetService;
	
	
	//sheet相关 
		@RequestMapping("/Excel/sheet/renameSheet.action")
		@ResponseBody
		public Map<String, Object> renameSheet(@RequestParam(required = true) String sheetId,
				@RequestParam(required = true) String name,
				HttpServletRequest request, HttpServletResponse response) {
			Map<String, Object> modelMap = new HashMap<String, Object>();
			excelSheetService.renameSheet(sheetId,name);
			modelMap.put("success", true);
			modelMap.put("info", "Changes Saved");
			return modelMap;
		}
		
		//需要
		@RequestMapping("/Excel/sheet/createSheet.action")
		@ResponseBody
		public Map<String, Object> createSheet(@RequestParam(required = true) String fileId,
				@RequestParam(required = true) Integer position,
				@RequestParam(required = true) String name,
				@RequestParam(required = false) String color
				) {
			Map<String, Object> modelMap = new HashMap<String, Object>();
			int id = 0;
			id=excelSheetService.createSheet(fileId,position,name,color);
			modelMap.put("id", id);
			modelMap.put("success", true);
			modelMap.put("info", "Changes Saved");
			return modelMap;
		}
		
		//删除
		@RequestMapping("/Excel/sheet/deleteSheet.action")
		@ResponseBody
		public Map<String, Object> deleteSheet(
				@RequestParam(required = true) String sheetId,
				HttpServletRequest request, HttpServletResponse response) {
			Map<String, Object> modelMap = new HashMap<String, Object>();
			excelSheetService.deleteSheet(sheetId);
			modelMap.put("success", true);
			modelMap.put("info", "Changes Saved");
			return modelMap;
		}
	
		//改变顺序
		@RequestMapping("/Excel/sheet/changeSheetOrder.action")
		@ResponseBody
		public Map<String, Object> changeSheetOrder(@RequestParam(required = true) String sheetId,
				@RequestParam(required = true) Integer prePos,
				@RequestParam(required = true) Integer curPos,
				HttpServletRequest request, HttpServletResponse response) {
			Map<String, Object> modelMap = new HashMap<String, Object>();
			excelSheetService.changeSheetOrder(sheetId,prePos,curPos);
			modelMap.put("success", true);
			modelMap.put("info", "Changes Saved");
			return modelMap;
		}
		
		//删除
		@RequestMapping("/Excel/sheet/copySheet.action")
		@ResponseBody
		public Map<String, Object> copySheet(@RequestParam(required = true) String oldSheetId,
				@RequestParam(required = true) String newSheetName,
				@RequestParam(required = true) Integer pos,
				HttpServletRequest request, HttpServletResponse response) {
			return excelSheetService.copySheet(oldSheetId,newSheetName,pos);
		}
	
}
