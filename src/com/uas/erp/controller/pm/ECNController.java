package com.uas.erp.controller.pm;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.uas.erp.core.BaseController;
import com.uas.erp.core.JSONUtil;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.FileUpload;
import com.uas.erp.model.GridPanel;
import com.uas.erp.service.pm.ECNService;

import net.sf.json.JSONObject;

@Controller
public class ECNController extends BaseController {
	@Autowired
	private ECNService ECNService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveECN.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECNService.saveECN(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除BOM数据 包括BOM明细
	 */
	@RequestMapping("/pm/bom/deleteECN.action")
	@ResponseBody
	public Map<String, Object> deleteECN(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECNService.deleteECN(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/updateECN.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECNService.updateECNById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交BOM
	 */
	@RequestMapping("/pm/bom/submitECN.action")
	@ResponseBody
	public Map<String, Object> submitECN(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECNService.submitECN(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交BOM
	 */
	@RequestMapping("/pm/bom/resSubmitECN.action")
	@ResponseBody
	public Map<String, Object> resSubmitECN(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECNService.resSubmitECN(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核BOM
	 */
	@RequestMapping("/pm/bom/auditECN.action")
	@ResponseBody
	public Map<String, Object> auditECN(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECNService.auditECN(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核BOM
	 */
	@RequestMapping("/pm/bom/resAuditECN.action")
	@ResponseBody
	public Map<String, Object> resAuditECN(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECNService.resAuditECN(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印ECN变更通知
	 */
	@RequestMapping("/pm/bom/printECN.action")
	@ResponseBody
	public Map<String, Object> printPurchase(String caller, int id, String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = ECNService.printECN(id, caller, reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 关闭ECN明细行
	 */
	@RequestMapping("/pm/bom/closeECNDetail.action")
	@ResponseBody
	public Map<String, Object> closeECNDetail(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECNService.closeECNDetail(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打开ECN明细行
	 */
	@RequestMapping("/pm/bom/openECNDetail.action")
	@ResponseBody
	public Map<String, Object> openECNDetail(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECNService.openECNDetail(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 执行自然切换ECN
	 */
	@RequestMapping("/pm/bom/executeAutoECN.action")
	@ResponseBody
	public Map<String, Object> executeAutoECN() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECNService.executeAutoECN();
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 关闭ECN所有明细行
	 */
	@RequestMapping("/pm/bom/closeECNAllDetail.action")
	@ResponseBody
	public Map<String, Object> closeECNAllDetail(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECNService.closeECNAllDetail(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打开ECN所有明细行
	 */
	@RequestMapping("/pm/bom/openECNAllDetail.action")
	@ResponseBody
	public Map<String, Object> openECNAllDetail(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECNService.openECNAllDetail(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 已审核打开状态的自然变更ECN转立即变更
	 * 
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/pm/bom/turnAutoECN.action")
	@ResponseBody
	public Map<String, Object> turnAutoECN(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECNService.turnAutoECN(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 针对ECN中不存在的子件编号用户点击自动生成，则将自动插入数据到物料资料表中
	 * 
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("pm/bom/autoNewProdECN.action")
	@ResponseBody
	public Map<String, Object> autoNewProdECN(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECNService.autoNewProdECN(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 导入ECN
	 * @throws Exception 
	 */
	@RequestMapping("pm/bom/importECN.action")
	@ResponseBody
	public String importECN(String caller, FileUpload uploadItem){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String string = ECNService.importECN(caller,uploadItem);
		return string;
	}
	
	/**
	 * 欧盛ECN审核后允许转请购单
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("pm/bom/turnApplication.action")
	@ResponseBody
	public Map<String, Object> turnApplication(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",ECNService.turnApplication(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}
	
}
