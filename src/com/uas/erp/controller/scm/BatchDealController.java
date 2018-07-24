package com.uas.erp.controller.scm;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.BatchDealService;
import com.uas.erp.service.scm.VerifyApplyService;

/**
 * SCM模块 批量处理
 */
@Controller("ScmBatchDealController")
public class BatchDealController {
	@Autowired
	private BatchDealService batchDealService;
	@Autowired
	private VerifyApplyService verifyApplyService;

	/**
	 * 请购单批量转采购单
	 */
	@RequestMapping(value = "/scm/vastTurnPurc.action")
	@ResponseBody
	public Map<String, Object> vastTurnPurc(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.vastTurnPurc(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量转收料单
	 */
	@RequestMapping(value = "/scm/vastTurnAccept.action")
	@ResponseBody
	public Map<String, Object> vastTurnAccept(String caller, String data,String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.vastTurnAccept(caller, data,formStore);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量转分配 请购单批量转分配
	 */
	@RequestMapping(value = "/common/vastTurnDistribute.action")
	@ResponseBody
	public Map<String, Object> vastTurnDistribute(String caller, int[] id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastTurnDistribute(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 销售单转发货通知单(销售订单界面)
	 */
	@RequestMapping(value = "/scm/sale/turnNotify.action")
	@ResponseBody
	public Map<String, Object> turnNotify(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.turnSendNotify(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * yaozx 13-11-07 添加收货地址ID 销售单批量转发货通知单(转单界面)
	 */
	@RequestMapping(value = "/scm/vastTurnNotify.action")
	@ResponseBody
	public Map<String, Object> vastTurnNotify(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.vastTurnSendNotify(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 报价单批量转销售订单
	 */
	@RequestMapping(value = "/common/vastTurnSale.action")
	@ResponseBody
	public Map<String, Object> vastTurnSale(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.vastTurnSale(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批抛转
	 */
	@RequestMapping(value = "/scm/vastPost.action")
	@ResponseBody
	public Map<String, Object> vastPost(String caller, int[] id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastPost(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批冻结
	 */
	@RequestMapping(value = "/scm/vastFreeze.action")
	@ResponseBody
	public Map<String, Object> vastFreeze(String caller, int[] id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastFreeze(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批发出
	 */
	@RequestMapping(value = "/scm/vastSend.action")
	@ResponseBody
	public Map<String, Object> vastSend(String caller, int[] id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastSend(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批删除
	 */
	@RequestMapping(value = "/scm/vastDelete.action")
	@ResponseBody
	public Map<String, Object> vastDelete(String caller, int[] id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastDelete(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 整批作废
	 */
	@RequestMapping(value = "/scm/vastCancel.action")
	@ResponseBody
	public Map<String, Object> vastCancel(String caller, int[] id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastCancel(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 出货单整批反过账
	 */
	@RequestMapping(value = "/scm/vastResPost.action")
	@ResponseBody
	public Map<String, Object> vastResPost(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastResPost(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 销售退货单整批反过账
	 */
	@RequestMapping(value = "/scm/vastResPost2.action")
	@ResponseBody
	public Map<String, Object> vastResPost2(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastResPost(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 出货通知单整批转出货单
	 */
	@RequestMapping(value = "/scm/vastTurnProdIN.action")
	@ResponseBody
	public Map<String, Object> vastTurnProdIN(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.vastTurnProdIN(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 出货通知单转出货单(通知单界面)
	 * 
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/scm/sale/turnProdOut.action")
	@ResponseBody
	public Map<String, Object> turnProdOut(String caller, String data, String type) throws UnsupportedEncodingException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.turnProdOut(caller, data, type);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 销售单整批转出货单
	 */
	@RequestMapping(value = "/scm/vastTurnProdIN2.action")
	@ResponseBody
	public Map<String, Object> vastTurnProdIN2(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.vastTurnProdIN2(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 销售单整批转客户验货
	 */
	@RequestMapping(value = "/scm/vastTurnProdINCustomer.action")
	@ResponseBody
	public Map<String, Object> vastTurnProdINCustomer(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.vastTurnProdINCustomer(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 通知单单整批转客户验货
	 */
	@RequestMapping(value = "/scm/notifyTurnCustomerCheck.action")
	@ResponseBody
	public Map<String, Object> notifyTurnCustomerCheck(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.notifyTurnCustomerCheck(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 收料单批量入库作业
	 */
	@RequestMapping(value = "/scm/vastTurnStorage.action")
	@ResponseBody
	public Map<String, Object> vastTurnStorage(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.detailTurnStorage(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 销售订单排定交期更改
	 */
	@RequestMapping(value = "/scm/vastSaveSale.action")
	@ResponseBody
	public Map<String, Object> vastSaveSale(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastSaveSale(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 出货单客户签收
	 */
	@RequestMapping(value = "/scm/vastSignin.action")
	@ResponseBody
	public Map<String, Object> vastSignin(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastSignin(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 出货单排货模拟
	 */
	@RequestMapping(value = "/scm/vastSimulate.action")
	@ResponseBody
	public Map<String, Object> vastSimulate(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastSignin(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 采购检验单批量入库作业
	 */
	@RequestMapping(value = "/scm/vastTurnPurcStorage.action")
	@ResponseBody
	public Map<String, Object> vastTurnPurcStorage(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.detailTurnIn(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 采购单批量入库作业
	 */
	@RequestMapping(value = "/scm/vastTurnCheckAccept.action")
	@ResponseBody
	public Map<String, Object> vastTurnPurcProdIO(String caller, String data,String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.detailTurnPurcProdIO(caller, data,formStore);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 采购单批量入库作业
	 */
	@RequestMapping(value = "/scm/VastTurnJobDuty.action")
	@ResponseBody
	public Map<String, Object> VastTurnJobDuty(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.VastTurnJobDuty(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 订单单批量转制造单
	 */
	@RequestMapping(value = "/scm/vastTurnMake.action")
	@ResponseBody
	public Map<String, Object> vastTurnMake(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.turnMake(data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 采购收料单批量转采购检验单
	 */
	@RequestMapping(value = "/scm/vastTurnIQC.action")
	@ResponseBody
	public Map<String, Object> vastTurnIQC(String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", verifyApplyService.detailTurnIQC(data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 委外收料单批量转委外检验单
	 */
	@RequestMapping(value = "/scm/vastTurnFQC.action")
	@ResponseBody
	public Map<String, Object> vastTurnFQC(String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", verifyApplyService.detailTurnFQC(data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 收料通知单批量转收料单
	 */
	@RequestMapping(value = "/scm/purchase/notifyToVerify.action")
	@ResponseBody
	public Map<String, Object> vastNotifyToVerify(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.vastTurnVerifyApply(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 入库单转出库单(批量页面)
	 */
	@RequestMapping(value = "/scm/reserve/vastProdIOin2out.action")
	@ResponseBody
	public Map<String, Object> vastProdIOin2out(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.vastProdIOin2out(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 入库单转出库单(入库单页面)
	 */
	@RequestMapping("/scm/reserve/turnDefectOut.action")
	@ResponseBody
	public Map<String, Object> turnDefectOut(String data, String caller, String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.turnDefectOut(caller, data, type);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 出库单转入库单(出库单页面)
	 */
	@RequestMapping("/scm/reserve/turnDefectIn.action")
	@ResponseBody
	public Map<String, Object> turnDefectIn(String data, String caller, String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.turnDefectIn(caller, data, type);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 特采的操作
	 */
	@RequestMapping("/scm/reserve/erptecai.action")
	@ResponseBody
	public Map<String, Object> turnDefectOutandInCheck(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.erpteCai(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量转分配 请购单批量转分配
	 */
	@RequestMapping(value = "/common/vastAPBillPost.action")
	@ResponseBody
	public Map<String, Object> vastAPBillPost(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.vastAPBillPost(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量转分配 请购单批量转分配
	 */
	@RequestMapping(value = "/oa/vastTurnOaappcetion.action")
	@ResponseBody
	public Map<String, Object> vastTurnOaapplicate(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.vastTurnOaapplicate(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量取消 用品请购单批量取消已批准数
	 */
	@RequestMapping(value = "/oa/cancelApproveNum.action")
	@ResponseBody
	public Map<String, Object> cancelApproveNum(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.cancelApproveNum(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获取分仓库存,
	 * @param useFactory 是否根据登录人员所属工厂获取对应工厂仓库库存数据，默认为false
	 */
	@RequestMapping(value = "/scm/product/getProductwh.action")
	@ResponseBody
	public Map<String, Object> getProductWh(String codes,boolean useFactory,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", batchDealService.getProductWh(codes,useFactory,caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量转新物料申请
	 */
	@RequestMapping(value = "/scm/vastTurnPreProduct.action")
	@ResponseBody
	public Map<String, Object> vastPreProduct(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.vastTurnPreProduct(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 合并生成发票箱单
	 */
	@RequestMapping(value = "/scm/reserve/turnPaIn.action")
	@ResponseBody
	public Map<String, Object> turnPaIn(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.turnPaIn(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 合并生成发票箱单（信扬）
	 */
	@RequestMapping(value = "/scm/reserve/turnPaInXY.action")
	@ResponseBody
	public Map<String, Object> turnPaInXY(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.turnPaInXY(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 销售预测单批量转销售单
	 */
	@RequestMapping(value = "/common/sfvastTurnSale.action")
	@ResponseBody
	public Map<String, Object> turnSale(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.turnSale(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 取消供应商送货通知 data!=null则取消打钩选择的未完成的提醒 condition=筛选条件语句 按条件取消未完成的提醒
	 */
	@RequestMapping("scm/cancelPurchaseNotify.action")
	@ResponseBody
	public Map<String, Object> cancelPurchaseNotify(String data, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.cancelPurchaseNotify(data, null);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 取消未完成的提醒
	 */
	@RequestMapping("scm/cancelALLPurcNotify.action")
	@ResponseBody
	public Map<String, Object> cancelALLPurchaseNotify(String data, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.cancelPurchaseNotify(null, "ALL");
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 取消当前筛选条件的送货提醒
	 */
	@RequestMapping("scm/cancelCondPurcNotify.action")
	@ResponseBody
	public Map<String, Object> cancelSelectPurcNotify(String data, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.cancelPurchaseNotify(null, condition);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 手工选择采购单投放送货通知
	 */
	@RequestMapping(value = "scm/newPurchaseNotify.action")
	@ResponseBody
	public Map<String, Object> newPurchaseNotify(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.newPurchaseNotify(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 设置供应商比例分配
	 */
	@RequestMapping(value = "scm/setVendorRate.action")
	@ResponseBody
	public Map<String, Object> setVendorRate(String Mode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.setVendorRate(Mode));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 检验单批量审核作业
	 */
	@RequestMapping(value = "/scm/vastAuditQua.action")
	@ResponseBody
	public Map<String, Object> vastAuditQua(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastAuditQua(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 业务员预算批量处理
	 */
	@RequestMapping(value = "/scm/vastTurnPreSaleFTSaleF.action")
	@ResponseBody
	public Map<String, Object> vastPreSaleFTSaleF(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.vastPreSaleFTSaleF(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * wusy
	 */
	/**
	 * 转业务员预测调整单
	 */
	@RequestMapping(value = "/scm/vastTurnForecastAdjust.action")
	@ResponseBody
	public Map<String, Object> vastTurnForecastAdjust(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.vastTurnForecastAdjust(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * @author wsy
	 * 问题反馈编号：2017020746
	 * 未认定物料转认定单
	 */
	@RequestMapping("scm/turnIdentify.action")  
	@ResponseBody 
	public Map<String, Object> turnIdentify(String data){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.turnIdentify(data));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * @author wsy
	 * 问题反馈编号：2017030165
	 * 出货单批量更新
	 */
	@RequestMapping("scm/commonBatchUpdate.action")  
	@ResponseBody 
	public Map<String, Object> commonBatchUpdate(String data,String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.commonBatchUpdate(data,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * @author wsy
	 * 
	 * 索菱：备用机申请单转备用机出库单
	 */
	@RequestMapping("scm/ApplyToOut.action")  
	@ResponseBody 
	public Map<String, Object> ApplyToOut(String data,String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.ApplyToOut(data,caller));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * @author wsy
	 * 
	 * 索菱：备用机出库单转备用机归还单
	 */
	@RequestMapping("scm/OutToReturn.action")  
	@ResponseBody 
	public Map<String, Object> OutToReturn(String data,String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.OutToReturn(data,caller));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * @author wsy
	 * 
	 * 怡海能达：手动加锁
	 */
	@RequestMapping("scm/handLocked.action")
	@ResponseBody
	public Map<String,Object> handLocked(String caller,String data,String formStore){
		Map<String,Object> modelMap = new HashMap<String,Object>();
		modelMap.put("log", batchDealService.handLocked(caller,data,formStore));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * @author wsy
	 * 
	 * 信扬国际：采购单转应付账款
	 */
	@RequestMapping("scm/purchaseToPrePay.action")  
	@ResponseBody 
	public Map<String, Object> purchaseToPrePay(String data,String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.purchaseToPrePay(data,caller));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 批次信息转库存检验单
	 */
	@RequestMapping(value = "/scm/vastTurnQUABatch.action")
	@ResponseBody
	public Map<String, Object> vastTurnQUABatch(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.vastTurnQUABatch(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 库存检验单不合格品调拨
	 */
	@RequestMapping(value = "/scm/reserve/turnBoChu.action")
	@ResponseBody
	public Map<String, Object> turnBoChu(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.turnBoChu(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * mrb单批量入库作业
	 */
	@RequestMapping(value = "/scm/vastTurnMRBStorage.action")
	@ResponseBody
	public Map<String, Object> vastTurnMRBStorage(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.vastTurnMRBStorage(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 产线不良退料转验退
	 */
	@RequestMapping(value = "/scm/reserve/batchToCheckOut.action")
	@ResponseBody
	public Map<String, Object> vastToCheckOut(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.batchToCheckOut(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 委外补料单转应付发票
	 */
	@RequestMapping(value = "/scm/reserve/batchToAPBill.action")
	@ResponseBody
	public Map<String, Object> vastTurnAPBill(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.vastTurnAPBill(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量生成条码盘盈
	 * 
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/scm/vastTurnBarStockProfit.action")
	@ResponseBody
	public Map<String, Object> vastTurnBarStockProfit(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.vastTurnBarStockProfit(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 供应商送货通知更新交货日期 data!=null则取消打钩选择的未完成的提醒 condition=筛选条件语句 按条件取消未完成的提醒
	 */
	@RequestMapping("scm/changePurchaseNotifyDelivery.action")
	@ResponseBody
	public Map<String, Object> changePurchaseNotifyDelivery(String data, String condition, String condParams) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.changePurchaseNotifyDelivery(data, condition, condParams);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量更新送货提醒数量
	 * 
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/scm/changePurcNotifyQty.action")
	@ResponseBody
	public Map<String, Object> changePurcNotifyQty(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.changePurcNotifyQty(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量更新出入库单单据日期
	 * 
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/scm/vastUpdateProdinoutDate.action")
	@ResponseBody
	public Map<String, Object> vastUpdateProdinoutDate(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastUpdateProdinoutDate(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量更新报废单单据日期
	 * 
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/scm/vastUpdateMakeScrapDate.action")
	@ResponseBody
	public Map<String, Object> vastUpdateMakeScrapDate(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastUpdateMakeScrapDate(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 人员开通手机客户端（批量处理界面）
	 */
	@RequestMapping(value = "/scm/vastOpenMobile.action")
	@ResponseBody
	public Map<String, Object> vastOpenMobile(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.vastPostToAccountCenter(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 离职人员取消手机客户端
	 * @throws Exception 
	 */
	@RequestMapping(value="/scm/vastCloseMobile.action")
	@ResponseBody
	public Map<String,Object> vastCloseMobile(String caller,String data) throws Exception {
		Map<String,Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.vastCloseToAccountCenter(caller,data));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 批量获取供应商UU号（批量处理界面）
	 */
	@RequestMapping(value = "/scm/vastOpenVendorUU.action")
	@ResponseBody
	public Map<String, Object> vastOpenVendorUU(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.vastOpenVendorUU(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量获取客户UU号（批量处理界面）
	 */
	@RequestMapping(value = "/scm/vastOpenCustUU.action")
	@ResponseBody
	public Map<String, Object> vastOpenCustUU(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.vastOpenCustUU(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批次信息转库存检验单
	 */
	@RequestMapping(value = "/scm/purchase/vastToQuotation.action")
	@ResponseBody
	public Map<String, Object> vastToQuotation(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.vastToQuotation(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 库存批号批量锁定
	 */
	@RequestMapping(value = "/scm/vastLockBatch.action")
	@ResponseBody
	public Map<String, Object> vastLockBatch(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastLockBatch(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 库存批号批量解锁
	 */
	@RequestMapping(value = "/scm/vastUnLockBatch.action")
	@ResponseBody
	public Map<String, Object> vastUnLockBatch(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastUnLockBatch(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 出入库单MRP状态批量关闭
	 */
	@RequestMapping(value = "/scm/vastCloseMRPProdio.action")
	@ResponseBody
	public Map<String, Object> vastCloseMRPProdio(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastCloseMRPProdio(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 出入库单MRP状态批量打开
	 */
	@RequestMapping(value = "/scm/vastOpenMRPProdio.action")
	@ResponseBody
	public Map<String, Object> vastOpenMRPProdio(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastOpenMRPProdio(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 借货出货单转续借申请单
	 */
	@RequestMapping("/scm/reserve/turnRenewApply.action")
	@ResponseBody
	public Map<String, Object> turnRenewApply(String data, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.turnRenewApply(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 销售价格批量失效
	 */
	@RequestMapping("/scm/vastAbateSalePrice.action")
	@ResponseBody
	public Map<String, Object> vastAbateSalePrice(String data, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastAbateSalePrice(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 销售价格批量转有效
	 */
	@RequestMapping("/scm/vastResabateSalePrice.action")
	@ResponseBody
	public Map<String, Object> vastResabateSalePrice(String data, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastResabateSalePrice(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 费用比例批量失效
	 */
	@RequestMapping("/scm/vastAbateProductRate.action")
	@ResponseBody
	public Map<String, Object> vastAbateProductRate(String data, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastAbateProductRate(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 费用比例批量转有效
	 */
	@RequestMapping("/scm/vastResabateProductRate.action")
	@ResponseBody
	public Map<String, Object> vastResabateProductRate(String data, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastResabateProductRate(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 出入库单批量过账作业
	 */
	@RequestMapping(value = "/scm/prodInOutPost.action")
	@ResponseBody
	public Map<String, Object> prodInOutPost(String caller, String from, String to, String pclass) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.prodInOutPost(caller, from, to, pclass);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 *  批量回复BUG
	 */
	@RequestMapping(value = "/scm/batchReplyBug.action")
	@ResponseBody
	public Map<String, Object> batchReplyBug(String caller,String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.batchReplyBug(caller,data);
		modelMap.put("success", true);
		modelMap.put("log", log);
		return modelMap;
	}
	/**
	 *  批量发送BUG邮件
	 */
	@RequestMapping(value = "/scm/vastSendBugMeg.action")
	@ResponseBody
	public Map<String, Object> vastSendBugMeg(String caller,String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.vastSendBugMeg(caller,data);
		modelMap.put("success", true);
		modelMap.put("log", log);
		return modelMap;
	}
	/**
	 *  批量确认BUG
	 */
	@RequestMapping(value = "/scm/confirmBug.action")
	@ResponseBody
	public Map<String, Object> confirmBug(String caller,String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.confirmBug(caller,data);
		modelMap.put("success", true);
		modelMap.put("log", log);
		return modelMap;
	}
	/**
	 *  索菱：物料申请单批量转物料出库单
	 */
	@RequestMapping(value = "/scm/applyVastTurnOut.action")
	@ResponseBody
	public Map<String, Object> applyVastTurnOut(String caller,String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.applyVastTurnOut(caller,data);
		modelMap.put("success", true);
		modelMap.put("log", log);
		return modelMap;
	}
	/**
	 *  索菱：物料出库单批量转物料归还单
	 */
	@RequestMapping(value = "/scm/outVastTurnReturn.action")
	@ResponseBody
	public Map<String, Object> outVastTurnReturn(String caller,String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.outVastTurnReturn(caller,data);
		modelMap.put("success", true);
		modelMap.put("log", log);
		return modelMap;
	}
	/**
	 * 怡海能达在途在库解锁
	 */
	@RequestMapping(value ="/scm/Deblock.action")
	@ResponseBody
	public Map<String, Object> Deblock(String caller,String data){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.Deblock(caller,data);
		modelMap.put("success", true);
		modelMap.put("log", log);
		return modelMap;
	}
	/**
	 * 怡海能达在途在库拆分
	 */
	@RequestMapping("scm/splitDeblock.action")
	@ResponseBody
	public Map<String, Object> splitDeblock(String formdata, String data,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.splitDeblock(formdata, data, caller);
		modelMap.put("success", true);
		modelMap.put("log", log);
		return modelMap;
	}
	/**
	 * 怡海能达在途在库借调
	 */
	@RequestMapping("scm/LendTry.action")
	@ResponseBody
	public Map<String, Object> LendTry(String formdata, String data,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.LendTry(formdata, data, caller);
		modelMap.put("success", true);
		modelMap.put("log", log);
		return modelMap;
	}
	
	/**
	 * 一种出入库单转为另一种出入库单
	 */
	@RequestMapping("/scm/reserve/turnOtherProdIO.action")
	@ResponseBody
	public Map<String, Object> turnOtherProdIO(String caller, String pi_inoutno, String pi_class, int pi_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.turnOtherProdIO(caller, pi_inoutno, pi_class, pi_id);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * maz 借货出货单批量转借货归还单 2017070873
	 */
	@RequestMapping("/scm/reserve/batchTurnReturn.action")
	@ResponseBody
	public Map<String, Object> batchTurnReturn(String data, String caller, String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.batchTurnReturn(caller, data, type);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * maz 批量操作核价单
	 */
	@RequestMapping("/scm/reserve/batchPLXG.action")
	@ResponseBody
	public Map<String, Object> batchPLXG(String data, String caller, String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.batchPLXG(data, caller, type);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 批量挂起客户
	 */
	@RequestMapping(value = "/scm/sale/batchHungCustomer.action")
	@ResponseBody
	public Map<String, Object> batchHungCustomer(HttpSession session, String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.batchHungCustomer(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 报价单批量转销售订单
	 */
	@RequestMapping(value = "/scm/sale/vastTurnSale.action")
	@ResponseBody
	public Map<String, Object> batchTurnSale(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.batchTurnSale(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 销售出货单批量转销售退货单
	 */
	@RequestMapping("/scm/reserve/vastTurnSaleReturn.action")
	@ResponseBody
	public Map<String, Object> vastTurnSaleReturn(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.vastTurnSaleReturn(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 采购验收批量转采购验退
	 */
	@RequestMapping("/scm/reserve/vastTurnPurcCheckout.action")
	@ResponseBody
	public Map<String, Object> vastTurnPurcCheckout(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.vastTurnPurcCheckout(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * maz 批量变更申请
	 */
	@RequestMapping("scm/reserve/batchReplaceRateChange.action")
	@ResponseBody
	public Map<String, Object> batchReplaceRateChange(String data, String caller, String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.batchReplaceRateChange(data, caller, type);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 请购单批量转采购单
	 */
	@RequestMapping(value = "/scm/purchase/turnMakeExp.action")
	@ResponseBody
	public Map<String, Object> turnMakeExp(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.turnMakeExp(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * wuyx 物料批量授权按钮
	 */
	@RequestMapping(value = "/scm/vastEmpowerProdSaler.action")
	@ResponseBody
	public Map<String, Object> vastEmpowerProdSaler(String caller, String data,String ps_emcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.vastEmpowerProdSaler(caller, data,ps_emcode);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * wuyx 物料批量取消授权按钮
	 */
	@RequestMapping(value = "/scm/vastUnPowerProdSaler.action")
	@ResponseBody
	public Map<String, Object> vastUnPowerProdSaler(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.vastUnPowerProdSaler(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * maz 2018030011 锤子数码 料号供应商批量操作
	 */ 
	@RequestMapping(value = "/scm/ProdJoinPLZC.action")
	@ResponseBody
	public Map<String, Object> ProdJoinPLZC(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.ProdJoinPLZC(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * wuyx 20180402 供应商批量快速邀请
	 */ 
	@RequestMapping(value = "/scm/inviteVendors.action")
	@ResponseBody
	public Map<String, Object> inviteVendors(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.inviteVendors(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
}
