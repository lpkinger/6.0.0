
package com.uas.erp.service.scm;

import java.util.List;
import java.util.Map;

public interface BatchDealService {
	String vastTurnPurc(String caller, String data);

	void vastTurnDistribute(String caller, int[] id);

	String vastTurnSale(String caller, String data);

	String vastTurnAccept(String caller, String data, String formParam);

	void vastFreeze(String caller, int[] id);

	void vastSaveSale(String caller, String data);

	void vastPost(String caller, int[] id);

	void vastResPost(String caller, String data);

	void vastCancel(String caller, int[] id);

	void vastDelete(String caller, int[] id);

	void vastSend(String caller, int[] id);

	String vastTurnProdIN(String caller, String data);

	String turnProdOut(String caller, String data, String type);

	void vastEndApplication(String caller, int[] id);

	String vastTurnProdIN2(String caller, String data);

	String vastTurnProdINCustomer(String caller, String data);// 销售单整批转客户验货

	String notifyTurnCustomerCheck(String caller, String data);// 出货通知单整批转客户验货

	String turnSendNotify(String caller, String data);

	String vastTurnSendNotify(String caller, String data);

	void vastSignin(String caller, String data);

	String detailTurnStorage(String caller, String data);

	String detailTurnIn(String caller, String data);// 检验单批量转验收单

	String detailTurnPurcProdIO(String caller, String data, String formStore);// 采购单批量转验收单

	String turnMake(String data);// 订单转制造单

	String vastTurnVerifyApply(String caller, String data);// 收料通知单批量转收料单

	String vastProdIOin2out(String caller, String data);// 入库单批量出库单

	String turnDefectOut(String caller, String data, String type);

	String turnDefectIn(String caller, String data, String type);

	String erpteCai(String caller, String data);

	String vastTurnOaapplicate(String caller, String data);

	void cancelApproveNum(String caller, String data);

	String vastAPBillPost(String caller, String data);

	String vastTurnPreProduct(String caller, String data);

	String turnPaIn(String caller, String data);

	List<Map<String, Object>> getProductWh(String codes,boolean useFactory,String caller);

	String turnSale(String caller, String data);

	String cancelPurchaseNotify(String data, String condition);

	void changePurchaseNotifyDelivery(String data, String condition, String condParams);

	String newPurchaseNotify(String caller, String data);

	String setVendorRate(String Mode);

	void vastAuditQua(String caller, String data);

	String vastTurnQUABatch(String caller, String data);

	String turnBoChu(String caller, String data);

	String batchToCheckOut(String caller, String data);

	String vastPreSaleFTSaleF(String caller, String data);

	String vastTurnMRBStorage(String caller, String data);// mrb单批量转验收单

	String vastTurnAPBill(String caller, String data); // 委外补料单转应付发票

	String vastTurnBarStockProfit(String caller, String data);

	void changePurcNotifyQty(String caller, String data);

	void vastUpdateProdinoutDate(String caller, String data);

	void vastUpdateMakeScrapDate(String caller, String data);

	String vastPostToAccountCenter(String caller, String data);

	String vastToQuotation(String caller, String data);

	String vastOpenVendorUU(String caller, String data);

	String vastOpenCustUU(String caller, String data);

	void vastLockBatch(String caller, String data);

	void vastUnLockBatch(String caller, String data);

	void vastCloseMRPProdio(String caller, String data);

	void vastOpenMRPProdio(String caller, String data);

	String turnRenewApply(String caller, String data);

	String VastTurnJobDuty(String caller, String data);

	void vastAbateSalePrice(String caller, String data);

	void vastResabateSalePrice(String caller, String data);

	void vastAbateProductRate(String caller, String data);

	void vastResabateProductRate(String caller, String data);

	void prodInOutPost(String caller, String from, String to, String pclass);

	String vastTurnForecastAdjust(String caller, String data);
	
	String batchReplyBug(String caller,String data);

	String vastSendBugMeg(String caller,String data);
	
	String confirmBug(String caller,String data);
	
	String applyVastTurnOut(String caller,String data);

	String outVastTurnReturn(String caller,String data);
	
	String turnIdentify(String data);

	void commonBatchUpdate(String data,String caller);

	String ApplyToOut(String data, String caller);

	String OutToReturn(String data, String caller);
	
	String Deblock(String caller,String data);
	
	String LendTry(String formdata, String data, String caller);
	
	String splitDeblock(String formdata, String data, String caller);

	String handLocked(String caller, String data, String formStore);

	String purchaseToPrePay(String data, String caller);
	
	String turnOtherProdIO(String caller, String pi_inoutno, String pi_class, int pi_id);

	String vastCloseToAccountCenter(String caller, String data) throws Exception;

	String batchTurnReturn(String data, String caller, String type);
	
	String batchPLXG(String data, String caller, String type);
	
	void batchHungCustomer(String caller, String data);
	
	String batchTurnSale(String caller, String data);
	
	String vastTurnSaleReturn(String caller, String data);
	
	String vastTurnPurcCheckout(String caller, String data);
	
	String batchReplaceRateChange(String data, String caller, String type);

	String turnMakeExp(String caller, String data);

	String turnPaInXY(String caller, String data);
	
	String vastEmpowerProdSaler(String caller,String data,String ps_emcode);
	
	String vastUnPowerProdSaler(String caller,String data);
	
	String ProdJoinPLZC(String caller,String data);

	String inviteVendors(String caller, String data);
}
