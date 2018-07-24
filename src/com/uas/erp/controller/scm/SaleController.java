package com.uas.erp.controller.scm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.SaleService;

@Controller
public class SaleController extends BaseController {
	@Autowired
	private SaleService saleService;
	@Autowired
	private BaseDao baseDao;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/sale/saveSale.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleService.saveSale(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存从账户中心获取的数据
	 * 
	 * @param formStore
	 * 
	 *
	 */
	@RequestMapping("/scm/sale/saveCustomerSimple.action")
	@ResponseBody
	public Map<String, Object> save(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int cu_id = saleService.saveCustomerSimple(formStore);
		modelMap.put("cu_id", cu_id);
		return modelMap;
	}

	/**
	 */
	@RequestMapping("/scm/sale/deleteSale.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleService.deleteSale(id, caller);
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
	 * @throws IOException
	 */
	@RequestMapping("/scm/sale/updateSale.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param, String caller) throws IOException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String error = saleService.updateSale(formStore, param, caller);
		if (error == null)
			modelMap.put("success", true);
		else
			BaseUtil.showErrorOnSuccess(error);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitSale.action")
	@ResponseBody
	public Map<String, Object> submitPurchase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleService.submitSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交销售单
	 */
	@RequestMapping("/scm/sale/resSubmitSale.action")
	@ResponseBody
	public Map<String, Object> resSubmit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleService.resSubmitSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核销售单
	 */
	@RequestMapping("/scm/sale/auditSale.action")
	@ResponseBody
	public Map<String, Object> audit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleService.auditSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核销售单
	 */
	@RequestMapping("/scm/sale/resAuditSale.action")
	@ResponseBody
	public Map<String, Object> resAudit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleService.resAuditSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印销售单
	 */
	@RequestMapping("/scm/sale/printSale.action")
	@ResponseBody
	public Map<String, Object> print(int id, String reportName, String condition, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = saleService.printSale(id, reportName, condition, caller);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 转发货通知单
	 */
	@RequestMapping("/scm/sale/saleturnNotify.action")
	@ResponseBody
	public Map<String, Object> turnSendNotify(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int snid = saleService.turnSendNotify(id, caller);
		modelMap.put("id", snid);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交 转正常
	 */
	@RequestMapping("/scm/sale/submitTurnSale.action")
	@ResponseBody
	public Map<String, Object> submitTurnSale(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleService.submitTurnSale(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交 转正常
	 */
	@RequestMapping("/scm/sale/resSubmitTurnSale.action")
	@ResponseBody
	public Map<String, Object> resSubmitTurnSale(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleService.resSubmitTurnSale(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转 正式订单
	 */
	@RequestMapping("/scm/sale/TurnNormalSale.action")
	@ResponseBody
	public Map<String, Object> TurnNormalSale(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleService.turnNormalSale(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 分拆订单
	 */
	@RequestMapping("scm/sale/splitSale.action")
	@ResponseBody
	public Map<String, Object> splitSale(String formdata, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleService.splitSale(formdata, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 订单更新PMC交期
	 */
	@RequestMapping("scm/sale/updatepmc.action")
	@ResponseBody
	public Map<String, Object> updatepmc(String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleService.updatePMC(data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 复制订单
	 * 
	 * @param session
	 * @param id
	 * @param caller
	 * @return
	 */
	@RequestMapping("/scm/sale/copySale.action")
	@ResponseBody
	public Map<String, Object> copySale(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", saleService.copySale(caller, id));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改比例
	 * 
	 * @param session
	 * @param id
	 * @param caller
	 * @return
	 */
	@RequestMapping("/scm/sale/updateDiscount.action")
	@ResponseBody
	public Map<String, Object> updateDiscount(int id, String caller, String data, Boolean oth) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleService.updateDiscount(caller, id, data, oth);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 拿到编号
	 */
	@RequestMapping("/scm/sale/getCodeString.action")
	@ResponseBody
	public Map<String, Object> getCode(String caller, String table, int type, String conKind) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("code", saleService.getCodeString(caller, table, type, conKind));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改SalePayment,帕诺迪专用
	 */
	@RequestMapping("/scm/sale/updateSalePayment.action")
	@ResponseBody
	public Map<String, Object> updateSalePayment(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleService.updateSalePayment(formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打开Mrp
	 */
	@RequestMapping("/scm/sale/saleMrpOpen.action")
	@ResponseBody
	public Map<String, Object> saleMrpOpen(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleService.saleMrpOpen(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 关闭Mrp
	 */
	@RequestMapping("/scm/sale/saleMrpClose.action")
	@ResponseBody
	public Map<String, Object> saleMrpClose(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleService.saleMrpClose(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 载入配件
	 */
	@RequestMapping("/scm/sale/getFittingData.action")
	@ResponseBody
	public Map<String, Object> getFittingData(String caller, String pr_code, String qty, String sa_id, String detno) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleService.getFittingData(caller, pr_code, qty, sa_id, detno);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 销售订单结案 id 销售订单id结案中没有使用此参数，用于权限管理
	 */
	@RequestMapping("/scm/sale/endSale.action")
	@ResponseBody
	public Map<String, Object> endSale(String caller, String data, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleService.endSale(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * BOM成本计算
	 */
	@RequestMapping("/scm/sale/salebomcost.action")
	@ResponseBody
	public Map<String, Object> bomCost(int sa_id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleService.calBOMCost(sa_id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 平台获取的销售订单确认接收
	 */
	@RequestMapping("/scm/sale/confirmAgree.action")
	@ResponseBody
	public Map<String, Object> confirmAgree(int sa_id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleService.confirmAgree(sa_id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转出货单
	 */
	@RequestMapping("/scm/sale/turnB2CSaleOut.action")
	@ResponseBody
	public Map<String, Object> turnB2CSaleOut(int sa_id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", saleService.turnB2CSaleOut(sa_id, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转出货单
	 */
	@RequestMapping("/scm/sale/updateld.action")
	@ResponseBody
	public Map<String, Object> UpdateLD(int sd_id, String LDCode, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleService.UpdateLD(sd_id, LDCode, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 转采购单
	 */
	@RequestMapping("/scm/sale/saleturnPurc.action")
	@ResponseBody
	public Map<String, Object> saleturnPurc(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", saleService.saleturnPurc(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 信扬国际
	 * 计算资金费用
	 */
	@RequestMapping("/scm/sale/chargerCalc.action")
	@ResponseBody
	public Map<String, Object> chargerCalc( String data,String pickdate ,int sa_deposit,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap=saleService.chargerCalc(data, pickdate,sa_deposit);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 驳回订单
	 */
	@RequestMapping("/scm/sale/updateSaleStatus.action")  
	@ResponseBody 
	public Map<String, Object> updateSaleStatus(String caller, String value,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleService.updateSaleStatus(caller, value, id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 信扬
	 * 复审功能
	 */
	@RequestMapping("/scm/sale/recheck.action")  
	@ResponseBody 
	public Map<String, Object> recheck(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleService.recheck(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 信扬
	 * 反复审功能
	 */
	@RequestMapping("/scm/sale/resRecheck.action")  
	@ResponseBody 
	public Map<String, Object> resRecheck(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleService.resRecheck(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 转单 
	 */
	@RequestMapping("/scm/sale/turnPage.action")
	@ResponseBody
	public Map<String, Object> turnPage(int id,String caller,String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleService.turnPage(id,caller,data);
		modelMap.put("success", true);
		return modelMap;
	}
}
