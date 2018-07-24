package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.GridPanel;
import com.uas.erp.model.ProdChargeDetail;
import com.uas.erp.service.common.SingleGridPanelService;
import com.uas.erp.service.ma.ConfigService;
import com.uas.erp.service.scm.ProdInOutService;

@Controller
public class ProdInOutControlller extends BaseController {
	@Autowired
	private ProdInOutService prodInOutService;
	@Autowired
	private SingleGridPanelService singleGridPanelService;
	@Autowired
	private ConfigService configService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/reserve/saveProdInOut.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.saveProdInOut(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 注意:prodInOut与其他单据不同，需要将caller也传回后台
	 */
	@RequestMapping("/scm/reserve/deleteProdInOut.action")
	@ResponseBody
	public Map<String, Object> deleteProdIo(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.deleteProdInOut(caller, id);
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
	@RequestMapping("/scm/reserve/updateProdInOut.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.updateProdInOutById(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/scm/reserve/printProdInOut.action")
	@ResponseBody
	public Map<String, Object> printProdInOut(int id, String caller, String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = prodInOutService.printProdInOut(caller, id, reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/reserve/submitProdInOut.action")
	@ResponseBody
	public Map<String, Object> submitProdInOut(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.submitProdInOut(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/reserve/resSubmitProdInOut.action")
	@ResponseBody
	public Map<String, Object> resSubmitProdInOut(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.resSubmitProdInOut(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/reserve/auditProdInOut.action")
	@ResponseBody
	public Map<String, Object> auditProdInOut(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.auditProdInOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/reserve/resAuditProdInOut.action")
	@ResponseBody
	public Map<String, Object> resAuditProdInOut(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.resAuditProdInOut(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 过账
	 */
	@RequestMapping("/scm/reserve/postProdInOut.action")
	@ResponseBody
	public Map<String, Object> postProdInOut(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.postProdInOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反过账
	 */
	@RequestMapping("/scm/reserve/resPostProdInOut.action")
	@ResponseBody
	public Map<String, Object> resPostProdInOut(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.resPostProdInOut(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 换货入库单转换货出库单
	 */
	@RequestMapping("/scm/reserve/turnExOut.action")
	@ResponseBody
	public Map<String, Object> turnExOut(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", prodInOutService.turnExOut(caller, id));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 重置按钮
	 */
	@RequestMapping("/scm/reserve/checkresetBatchcode.action")
	@ResponseBody
	public Map<String, Object> checkresetBatchCode(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("bool", prodInOutService.checkresetBatchCode(caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批号重置
	 */
	@RequestMapping("/scm/reserve/resetBatchcode.action")
	@ResponseBody
	public Map<String, Object> resetBatchCode(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.resetBatchCode(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 其它出库转入库
	 */
	@RequestMapping("/scm/reserve/turnProdinoutIn.action")
	@ResponseBody
	public Map<String, Object> turnProdinoutIn(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String logString = prodInOutService.turnProdinoutIn(id, caller);
		modelMap.put("success", true);
		modelMap.put("log", logString);
		return modelMap;
	}

	/**
	 * 采购验收单更新成本单价
	 */
	@RequestMapping("/scm/reserve/updatepdPrice.action")
	@ResponseBody
	public Map<String, Object> updatepdPrice(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.updatepdPrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * yaozx@13-12-24 发货单比例维护 修改明细行数据
	 */
	@RequestMapping("/scm/sale/updatepdscaleremark.action")
	@ResponseBody
	public Map<String, Object> updatepdscaleremark(int id, String field, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.updatepdscaleremark(id, field, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 其它入库单 已提交状态下允许更新备注
	 */
	@RequestMapping("/scm/sale/updateProdInOutOtherInRemark.action")
	@ResponseBody
	public Map<String, Object> updateProdInOutOtherInRemark(int id, String remark, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.updateProdInOutOtherInRemark(id, remark, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * yaozx@13-12-25 出货单转销售退货
	 * 
	 */
	@RequestMapping("/scm/sale/turnTurnProdinoutReturn.action")
	@ResponseBody
	public Map<String, Object> turnTurnProdinoutReturn(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = prodInOutService.turnTurnProdinoutReturn(caller, id);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * madan@13-12-30 出入库单拆分
	 * 
	 */
	@RequestMapping("/scm/reserve/split.action")
	@ResponseBody
	public Map<String, Object> split(int id, String caller, String cls) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = prodInOutService.split(caller, id, cls);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * shenj@2014-11-21 用品借用单转用品归回单
	 * 
	 */
	@RequestMapping("/scm/reserve/turnYPOutReturnnew.action")
	@ResponseBody
	public Map<String, Object> turnYPOutReturnnew(String data, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = prodInOutService.turnYPOutREturnnew(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * tanyh@2014-08-06 出货单转借销售退货单
	 * 
	 */
	@RequestMapping("/scm/reserve/turnTurnProdinoutReturnnew.action")
	@ResponseBody
	public Map<String, Object> turnTurnProdinoutReturnnew(String data, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = prodInOutService.turnTurnProdinoutReturnnew(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 分装单据
	 */
	@RequestMapping("/scm/reserve/Subpackage.action")
	@ResponseBody
	public Map<String, Object> Subpackage(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String logString = prodInOutService.Subpackage(id);
		modelMap.put("success", true);
		modelMap.put("log", logString);
		return modelMap;
	}

	/**
	 * 清除分装明细
	 */
	@RequestMapping("/scm/reserve/ClearSubpackage.action")
	@ResponseBody
	public Map<String, Object> ClearSubpackage(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String logString = prodInOutService.ClearSubpackage(id);
		modelMap.put("success", true);
		modelMap.put("log", logString);
		return modelMap;
	}

	/**
	 * 条码打印（整单）
	 */
	@RequestMapping("/scm/reserve/printBar.action")
	@ResponseBody
	public Map<String, Object> printBar(int id, String reportName, String condition, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = prodInOutService.printBar(id, reportName, condition, caller);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 分装单据(明细)
	 */
	@RequestMapping("/scm/reserve/SubpackageDetail.action")
	@ResponseBody
	public Map<String, Object> SubpackageDetail(int id, double tqty) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String logString = prodInOutService.SubpackageDetail(id, tqty);
		modelMap.put("success", true);
		modelMap.put("log", logString);
		return modelMap;
	}

	/**
	 * 清除分装明细(明细)
	 */
	@RequestMapping("/scm/reserve/ClearSubpackageDetail.action")
	@ResponseBody
	public Map<String, Object> ClearSubpackageDetail(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String logString = prodInOutService.ClearSubpackageDetail(id);
		modelMap.put("success", true);
		modelMap.put("log", logString);
		return modelMap;
	}

	/**
	 * 条码打印(明细)
	 */
	@RequestMapping("/scm/reserve/PrintBarDetail.action")
	@ResponseBody
	public Map<String, Object> PrintBarDetail(int id, String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = prodInOutService.PrintBarDetail(id, reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 抓取批号
	 */
	@RequestMapping("/scm/reserve/catchBatch.action")
	@ResponseBody
	public Map<String, Object> catchBatch(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.catchBatch(caller, id);
		modelMap.put("success", true);
		modelMap.put("log", "");
		return modelMap;
	}

	/**
	 * 计算可用量
	 */
	@RequestMapping("/scm/reserve/loadOnHandQty.action")
	@ResponseBody
	public Map<String, Object> loadOnHandQty(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.loadOnHandQty(id);
		GridPanel gridPanel = singleGridPanelService.getGridPanelByCaller(caller, "pd_piid=" + id, null, null, 1, false, "");
		if (gridPanel != null) {
			modelMap.put("data", gridPanel.getDataString());
		}
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 确认实际可发数量
	 */
	@RequestMapping("/scm/reserve/SetMMQTY.action")
	@ResponseBody
	public Map<String, Object> SetMMQTY(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.SetMMQTY(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 报关型号修改
	 * */
	@RequestMapping("scm/reserve/updatebgxh.action")
	@ResponseBody
	public Map<String, Object> updatebgxh(String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.updatebgxh(data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 关联单号修改
	 * */
	@RequestMapping("scm/reserve/updateOrderCode.action")
	@ResponseBody
	public Map<String, Object> updateOrderCode(String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.updateOrderCode(data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批号修改
	 * */
	@RequestMapping("scm/reserve/updateBatchCode.action")
	@ResponseBody
	public Map<String, Object> updateBatchCode(String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.updateBatchCode(data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 不良品入库单转MRB
	 */
	@RequestMapping("/scm/reserve/turnMRB.action")
	@ResponseBody
	public Map<String, Object> turnDefectOut(String data, String caller, String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = prodInOutService.turnMRB(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改借货类型
	 * 
	 * @param type
	 *            借货类型
	 * @param remark
	 *            备注
	 */
	@RequestMapping("/scm/reserve/updateBorrowCargoType.action")
	@ResponseBody
	public Map<String, Object> updateUU(String caller, Integer id, String type, String remark) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.updateBorrowCargoType(id, type, remark, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 拆件入库单批量入库
	 */
	@RequestMapping(value = "/pm/make/vastTurnProdIn.action")
	@ResponseBody
	public Map<String, Object> vastTurnIN(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = prodInOutService.vastTurnIn(data, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新明细仓库信息
	 * */
	@RequestMapping(value = "/scm/reserve/updateWhCodeInfo.action")
	@ResponseBody
	public Map<String, Object> updateWhCodeInfo(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = prodInOutService.updateWhCodeInfo(data, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 抓取价格
	 */
	@RequestMapping("/scm/reserve/getPrice.action")
	@ResponseBody
	public Map<String, Object> getPrice(int pdid, int piid, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.getPrice(pdid, piid, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 生成条码
	 */
	@RequestMapping("/scm/reserve/GenerateBarcodeByZxbzs.action")
	@ResponseBody
	public Map<String, Object> generateBarcodeByZxbzs(int pi_id, String pi_class, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.generateBarcodeByZxbzs(pi_id, pi_class, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 产生形式发票
	 */
	@RequestMapping("/scm/reserve/createBill.action")
	@ResponseBody
	public Map<String, Object> createBill(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.createBill(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 产生其它应收、其它应付单
	 */
	@RequestMapping("/scm/reserve/createOtherBill.action")
	@ResponseBody
	public Map<String, Object> createOtherBill(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.createOtherBill(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 按订单抓取批号
	 * */
	@RequestMapping("scm/reserve/catchBatchByOrder.action")
	@ResponseBody
	public Map<String, Object> catchBatchByOrder(Long pd_piid, Long pd_id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.catchBatchByOrder(pd_piid, pd_id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 按入仓单抓取批号
	 * */
	@RequestMapping("scm/reserve/catchBatchByIncode.action")
	@ResponseBody
	public Map<String, Object> catchBatchByIncode(Long pi_id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.catchBatchByIncode(pi_id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 按委托方抓取批号
	 * */
	@RequestMapping("scm/reserve/catchBatchByClient.action")
	@ResponseBody
	public Map<String, Object> catchBatchByClient(String type, Long pi_id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.catchBatchByClient(type, pi_id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 按业务员抓取批号
	 * */
	@RequestMapping("scm/reserve/catchBatchBySeller.action")
	@ResponseBody
	public Map<String, Object> catchBatchBySeller(Long pi_id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.catchBatchBySeller(pi_id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 明细订单
	 * */
	@RequestMapping("scm/reserve/splitProdIODetail.action")
	@ResponseBody
	public Map<String, Object> splitProdIODetail(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.splitProdIODetail(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 生成发票箱单
	 */
	@RequestMapping("/scm/reserve/turnPackInvo.action")
	@ResponseBody
	public Map<String, Object> turnPaIn(Long id, String catecode, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", prodInOutService.turnPaIn(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/scm/reserve/getProdCharge.action")
	@ResponseBody
	public Map<String, Object> getProdCharge(HttpServletRequest req, String caller, String condition, Integer start, Integer end,
			String master, Integer _m, String _config, String piclass, int piid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (master != null && !master.equals(""))
			SpObserver.putSp(master);
		boolean isCloud = Constant.UAS_CLOUD.equals(req.getParameter("_config"));
		GridPanel gridPanel = singleGridPanelService.getGridPanelByCaller(caller, condition, start, end, _m, isCloud, "");
		modelMap.put("fields", gridPanel.getGridFields());
		modelMap.put("columns", gridPanel.getGridColumns());
		modelMap.put("dbfinds", gridPanel.getDbfinds());
		modelMap.put("limits", gridPanel.getLimits());
		if (StringUtils.isEmpty(gridPanel.getDataString()) || "[]".equals(gridPanel.getDataString())) {
			// 默认数据
			List<ProdChargeDetail> kinds = prodInOutService.createProdChargeByKinds(piclass, piid);
			modelMap.put("data", FlexJsonUtil.toJsonArray(kinds));
		} else {
			modelMap.put("data", gridPanel.getDataString());
		}
		// 必填项label特殊颜色
		JSONObject config = configService.getConfigByCallerAndCode("sys", "necessaryFieldColor");
		if (config != null && config.get("data") != null)
			modelMap.put("necessaryFieldColor", config.get("data"));
		return modelMap;
	}

	/**
	 * 费用明细保存
	 */
	@RequestMapping("/scm/reserve/saveProdCharge.action")
	@ResponseBody
	public Map<String, Object> saveProdCharge(String caller, String gridStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.saveProdCharge(gridStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 采购验收单、出货单 费用明细保存
	 */
	@RequestMapping("/scm/reserve/checkStatus.action")
	@ResponseBody
	public Map<String, Object> checkStatus(int pi_id, String pi_inoutno, String pi_class, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.checkStatus(pi_id, pi_inoutno, pi_class, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新明细仓库
	 * 
	 * @author wsy
	 */
	@RequestMapping(value = "/scm/reserve/updateDetailWH.action")
	@ResponseBody
	public Map<String, Object> updateDetailWH(String pi_id, String codevalue, String value, String pd_inwhcode, String pd_inwhname,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.updateDetailWH(pi_id, codevalue, value, pd_inwhcode, pd_inwhname, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 发送EDI，执行存储过程，生成json数据
	 */
	@RequestMapping(value = "/scm/reserve/sendEdi.action")
	@ResponseBody
	public Map<String, Object> sendEdi(String id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.sendEdi(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 退单
	 */
	@RequestMapping(value = "/scm/reserve/cancelEdi.action")
	@ResponseBody
	public Map<String, Object> cancelEdi(String id, String caller,String remark) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.cancelEdi(id, caller,remark);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 发送id，执行存储过程，确认入库
	 */
	@RequestMapping(value = "/scm/reserve/confirmIn.action")
	@ResponseBody
	public Map<String, Object> confirmIn(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.confirmIn(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 费用分摊
	 */
	@RequestMapping("/scm/reserve/feeShare.action")
	@ResponseBody
	public Map<String, Object> feeShare(Long id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutService.feeShare(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 检查是否有物料号+批号+仓库出现在已过账的未制作凭证的成本调整单中
	 */
	@RequestMapping("/scm/reserve/resPostCheck.action")
	@ResponseBody
	public Map<String, Object> resPostCheck(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String detnos=prodInOutService.resPostCheck(caller, id);
		modelMap.put("log", detnos);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 西博泰科 采购验收转销售订单
	 */
	@RequestMapping("/scm/reserve/turnSale.action")
	@ResponseBody
	public Map<String, Object> turnSale(int id,String caller){
		Map<String, Object>modelMap = new HashMap<String, Object>();
		int sa_id = prodInOutService.turnSale(id,caller);
		modelMap.put("id", sa_id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 怡海能达 edi数据转入库单
	 */
	@RequestMapping("/scm/reserve/turnEdiToProdIn.action")
	@ResponseBody
	public Map<String, Object> turnEdiToProdin(String caller,String ids){
		Map<String, Object>modelMap = new HashMap<String, Object>();
		modelMap.put("msg", prodInOutService.turnEdiToProdin(ids));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 怡海能达:edi数据标记为已处理
	 */
	@RequestMapping("/scm/reserve/markEdiAsDone.action")
	@ResponseBody
	public Map<String, Object> markEdiAsDone(String caller,String ids){
		Map<String, Object>modelMap = new HashMap<String, Object>();
		prodInOutService.markEdiAsDone(ids,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/reserve/getCancelConfig.action")
	@ResponseBody
	public Map<String, Object> getCancelConfig(String caller){
		Map<String, Object>modelMap = new HashMap<String, Object>();
		String data = prodInOutService.getCancelConfig(caller);
		modelMap.put("data", data);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/reserve/updateCancelReason.action")
	@ResponseBody
	public Map<String, Object> updateCancelReason(int id,String value,String caller){
		Map<String, Object>modelMap = new HashMap<String, Object>();
		prodInOutService.updateCancelReason(id,value,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/reserve/getFittingData.action")
	@ResponseBody
	public Map<String, Object> getFittingData(String pr_code, String pi_id, String qty, String detno, String caller){
		Map<String, Object>modelMap = new HashMap<String, Object>();
		prodInOutService.getFittingData(pr_code, pi_id, qty, detno, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
