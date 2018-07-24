package com.uas.erp.service.pm;

public interface BatchDealService {
	String turnMake(String data, String caller);

	String OSturnMake(String data, String caller);

	String turnProdIn(String data, boolean wh, String type, String caller, boolean outtoint);

	/**
	 * 成套转报废单
	 * 
	 * @param data
	 * @param type
	 * @param employee
	 * @param language
	 * @return
	 */
	String turnProdScrap(String data, String type, String caller);

	String turnProdOut(String data, boolean wh, String whman, String type, String caller);

	String turnProdAdd(String data, boolean wh, String type, String caller);

	String turnMade(String data, String caller);// 制造单转完工入库单

	String turnMadebyflow(String data, String caller);// 制造单转完工入库单

	String turnFinishIn(String data, String caller);// 生产检验单转完工入库单

	String OSturnFinishIn(String data, String caller);// 锤子科技FQC转委外验收

	String turnQuaCheck(String data, String caller);// 制造单转生产检验单

	void vastSaveProductMrpSet(String data, String caller);

	void EndSaleForeCast(String data, String caller);

	void ResEndSaleForeCast(String data, String caller);

	String vastTurnAccept(String data, String caller);

	String turnlssend(String data, boolean bywhcode, String wipwhcode, String maid, String departmentcode, String emcode, String cgycode,
			String caller);// 拉式发料生成调拨单和领料单

	String multiturnlssend(String data, boolean bywhcode, String wipwhcode, String maid, String departmentcode, String emcode,
			String cgycode, String caller);// 多工单拉式发料生成调拨单和领料单

	String turnProdIOBC(String data, String inwhcode, String whmancode, String caller);

	String turnPurMould(String data, String caller);

	String vastToMouleFee(String data, String caller);

	String confirmThrowQty(String data, String caller);

	void updatecust(String data);

	void updatevend(String data);

	String vastTurnMakeCraft(String caller, String data);// 制造单批量下达作业

	String turnProdIOMakeOS(String data, String caller);// 委外单转委外验收单

	void refreshFeatureView(String ftcode, String caller);

	void refreshFeatureViewProd(String ftcode, String caller);

	String batchGoodsOff(String data, String caller);

	void vastMakeClose(String data, String caller);

	void vastMakeOpen(String data, String caller);

	void updateMakeSubMaterial(String data, String caller);

	void vastSetMain(String data, String caller);

	String vastTurnProcessIn(String data, String caller);

	String vastMakeCraftTurnAccept(String data, String caller);

	String turnProdIOAdd(String data, String caller);

	String turnProdIOReturn(String data, String caller);

	String turnProdIOGet(String data, String caller);

	String turnStockScrap(String data, String caller);
	
	void batchEndMakeCraft(String caller ,String data);

	void changeWhcode(String isrep, String whcode, String mmid,String mpdetno);

	void batchMakeECNCancelPerform(String caller, String data);

	void batchMakeECNTurnPerform(String caller, String data);
}
