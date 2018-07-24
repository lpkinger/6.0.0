package com.uas.erp.controller.pm;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.core.StringUtil;
import com.uas.erp.service.oa.DocumentListService;
import com.uas.erp.service.pm.BOMService;

@Controller
public class BOMController extends BaseController {
	@Autowired
	private BOMService bomService;
	@Autowired
	private DocumentListService documentListService;
	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveBOM.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomService.saveBOM(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除BOM数据 包括BOM明细
	 */
	@RequestMapping("/pm/bom/deleteBOM.action")
	@ResponseBody
	public Map<String, Object> deleteBOM(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomService.deleteBOM(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除明细行某一条数据
	 */
	@RequestMapping("/pm/bom/deleteDetail.action")
	@ResponseBody
	public Map<String, Object> deleteDetail(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomService.deleteDetail(id, caller);
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
	@RequestMapping("/pm/bom/updateBOM.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomService.updateBOMById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交BOM
	 */
	@RequestMapping("/pm/bom/submitBOM.action")
	@ResponseBody
	public Map<String, Object> submitBOM(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomService.submitBOM(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交BOM
	 */
	@RequestMapping("/pm/bom/resSubmitBOM.action")
	@ResponseBody
	public Map<String, Object> resSubmitBOM(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomService.resSubmitBOM(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核BOM
	 */
	@RequestMapping("/pm/bom/auditBOM.action")
	@ResponseBody
	public Map<String, Object> auditBOM(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomService.auditBOM(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核BOM
	 */
	@RequestMapping("/pm/bom/resAuditBOM.action")
	@ResponseBody
	public Map<String, Object> resAuditBOM(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomService.resAuditBOM(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 成本计算
	 */
	@RequestMapping("/pm/bom/cost.action")
	@ResponseBody
	public Map<String, Object> bomCost(String caller, int bo_id, String pr_code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		JSONObject obj = bomService.calBOMCost(bo_id, pr_code,caller);
		modelMap.put("success", true);
		modelMap.put("data", obj);
		return modelMap;
	}
	
	/**
	 * 成本计算   财务使用
	 */
	@RequestMapping("/pm/bom/costFi.action")
	@ResponseBody
	public Map<String, Object> bomCostFi(String caller, int bo_id, String pr_code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		JSONObject obj = bomService.calBOMCost(bo_id, pr_code,caller);
		modelMap.put("success", true);
		modelMap.put("data", obj);
		return modelMap;
	}

	
	/**
	 * 成本计算
	 */
	@RequestMapping("/pm/bom/periodCost.action")
	@ResponseBody
	public Map<String, Object> bomPeriodCost(String caller, int bo_id,String bv_bomversionid , String fromdate , String todate) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		JSONObject obj = bomService.calBOMPeriodCost(bo_id,bv_bomversionid,  fromdate ,todate);
		modelMap.put("success", true);
		modelMap.put("data", obj);
		return modelMap;
	}
	
	/**
	 * 成本计算(自定义) 鼎智
	 */
	@RequestMapping("/pm/bom/bomCostCustom.action")
	@ResponseBody
	public Map<String, Object> bomCostCustom(String caller, int bo_id,String bv_bomversionid , String fromdate , String todate,String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		JSONObject obj = bomService.bomCostCustom(bo_id,bv_bomversionid,  fromdate ,todate,data);
		modelMap.put("success", true);
		modelMap.put("data", obj);
		return modelMap;
	}
	
	/**
	 * 帐龄计算
	 */
	@RequestMapping("/pm/bom/Zhangling.action")
	@ResponseBody
	public Map<String, Object> Zhangling(String caller, String todate) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomService.zhangling(todate,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * BOM成本计算汇总
	 */
	@RequestMapping("/pm/bom/printBOM.action")
	@ResponseBody
	public Map<String, Object> printbomCost(String caller, int id,
			String reportName, String condition, String prodcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = bomService.printBomCost(id,caller,
				reportName, condition, prodcode);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 打印BOM
	 */
	@RequestMapping("/pm/bom/printsingleBOM.action")
	@ResponseBody
	public Map<String, Object> printsingleBom(String caller, int id,
			String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = bomService.printsingleBom(id,caller,
				reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	@RequestMapping("/pm/bom/bomcopy.action")
	@ResponseBody
	public Map<String, Object> bomcopy(String caller, String formStore,
			String param, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomService.bomcopy(id, formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/pm/bom/turnBOM.action")
	@ResponseBody
	public Map<String, Object> turnBOM(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomService.turnBOM(data,caller);
		modelMap.put("success", true);
		return modelMap;
	} 
	/**
	 * 禁用BOM
	 */
	@RequestMapping("/pm/bom/bannedBOM.action")  
	@ResponseBody 
	public Map<String, Object> bannedBOM(String caller, String data) {		
		Map<String, Object> map = new HashMap<String, Object>();
		bomService.bannedBOM(data,caller);
		map.put("success", true);
		return map;
	}
	/**
	 * 反禁用BOM
	 */
	@RequestMapping("/pm/bom/resBannedBOM.action")  
	@ResponseBody 
	public Map<String, Object> resBannedBOM(String caller, String data) {		
		Map<String, Object> map = new HashMap<String, Object>();
		bomService.resBannedBOM(data,caller);
		map.put("success", true);
		return map;
	}

	@RequestMapping("pm/bom/updateBOMPast.action")
	@ResponseBody
	public Map<String, Object> updateBOMPast(String caller, String value,
			int bo_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomService.updateBomPast(bo_id, value,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 制造ECN确认
	 */
	@RequestMapping("/pm/bom/confirmECN.action")
	@ResponseBody
	public Map<String, Object> confirmECN(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = bomService.confirmECN(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 制造ECN取消
	 */
	@RequestMapping("/pm/bom/cancelECN.action")
	@ResponseBody
	public Map<String, Object> cancelECN(String data,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = bomService.cancelECN(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 载入替代关系
	 */
	@RequestMapping("/pm/bom/loadRelation.action")
	@ResponseBody
	public Map<String, Object> loadRelation(String data,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = bomService.loadRelation(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获取物料的 MRP库存、不良品库存、采购待检数、 PO在途数、请购在途数、工单未发数、工单未完工数
	 */
	@RequestMapping(value = "/pm/product/getProductCount.action")
	@ResponseBody
	public Map<String, Object> getProductCount(String caller, String codes) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", bomService.getProductCount(codes,caller));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取物料的 仓库编号、仓库名称、数量、账套名.
	 */
	@RequestMapping(value = "/pm/product/getProductwhMaster.action")
	@ResponseBody
	public Map<String, Object> getProductMaster(String caller, String master , String codes) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", bomService.getProductMaster(codes,master));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 对所有bo_versionID跟bs_node不一致，或者无展开结果的BOM进行多级展开
	 */
	@RequestMapping(value = "/pm/bom/BOMStructPrintAll.action")
	@ResponseBody
	public Map<String, Object> BOMStructPrintAll() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomService.BOMStructPrintAll();
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * BOM多级展开批量下载物料资料附件
	 * @author lidy  反馈编号：2017110174
	 */
	@RequestMapping(value = "/pm/bom/BOMAttachDownload.action")
	@ResponseBody
	public void BOMAttachDownload(HttpServletResponse response, HttpServletRequest request,String caller,String bo_mothercode)  throws IOException, KeyManagementException, NoSuchAlgorithmException {
		String ids = request.getParameter("ids");
		if(StringUtil.hasText(ids)){
			if("BOMStruct!Struct!Query".equals(caller)){				
				documentListService.downloadbyIds(response,ids,"【"+bo_mothercode+"】物料附件");
			}else{
				documentListService.downloadbyIds(response,ids,"物料附件");
			}
		}
    }
}
