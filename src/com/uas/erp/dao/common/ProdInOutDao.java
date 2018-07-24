package com.uas.erp.dao.common;

import java.util.Map;

import net.sf.json.JSONObject;

public interface ProdInOutDao {
	String checkOutqty(int pi_id);

	int turnARBill(int id);

	String newARBill(String type);

	String getARCodeBySourceCode(int id);

	void toAppointedARBill(String ab_code, int pd_id, double qty, Double price, String sourcekind, String detailid, Object ifbypi);

	void toAppointedAPBill(String ab_code, int pd_id, double qty, Double price, String sourcekind, String detailid, Object date);

	String newARBillWithCustomer(Object date, String sendtype, Object tradecode, Object tradename, Object refno,
			Map<String, Object> config, Object ifbypi, Object picode, Object differ, Object taxamount);

	String newAPBillWithVendor(int veid, String vecode, String vename, String currency, Double rate, Object date, Object refno,
			Object remark);

	String newGoodsSendByPiid(String piid);

	void turnGoodsSendDetail(String gsid, String pdid, String piid);

	void account(String piclass, String pricekind, Object startdate, Object enddate, int flowid);

	String getPrice(String piclass, String pricekind, String prodcode, Object startdate, Object enddate, String ordercode,
			Object orderdetno, Double pirate, String whcode, int type, int pdid, int flowid);

	String turnProdIO(int id);

	void resPostProdIn(int id);

	/**
	 * 销售拨出单反过账前，反过账销售拨入单
	 * 
	 * @param id
	 * @return
	 */
	boolean resPostSaleProdIn(int id);

	void getCustomer(int[] id);

	void checkPDQty(int pdid);

	void checkPDQtyAP(int pdid);

	void checkGSDQtyAR(String gsdid);

	void checkESDQtyAR(String esdid);

	String checkProduct(int id);

	String checkPurcDetail(int id);

	String checkSaleDetail(int id);

	String checkexpbackqty(int id);

	String checkaddqty(int id);

	String checkgetqty(int id);

	String checkkits(int id);

	String delletednotAllowPrint(int id);

	String expcurrencyCheck(int id);

	String pucurrencyCheck(int id);

	String sacurrencyCheck(int id);

	String makestatusCheck(int id);

	String orderinfoCheck(int id);

	String whcostCheck(int id);

	String pwonhandCheck(int id);

	String qtyonhandCheck(int id);

	String lineCheck(int id);

	String plantCheck(int id);

	String departmentvalidCheck(int id);

	String descriptionvalidCheck(int id);

	String pitypeCheck(int id);

	String prstatusCheck(int id);

	String pdqtyCheck(int id);

	String piclassCheck(int id);

	String kpstatusCheck(int id);

	void outqtyCheck(Object id, Object ordercode, Object orderdetno, Double uqty, Object piclass);

	void checkProductcode(Object purCode, Object purdetno, Object proCode);

	String getBatchCode(String caller, String field);

	boolean getCostPrice(String caller);

	void getexpPrice(int id);

	void getpuPrice(int id);

	void getsdTaxrate(int id);

	void getqtyfromorder(int id);

	void departmentUpdate(int id);

	void descriptionUpdate(int id);

	void getcmrate(int id);

	void addMaterial(int id, String caller);

	JSONObject newProdDefectOut(int id, String piclass, String type);

	void toAppointedProdDefectOut(int pi_id, int pd_id, double qty, int detno);

	void toAppointedProdOtherOut(int pi_id, int pd_id, double qty, int detno);

	void toAppointedProdSaleOut(int pi_id, int pd_id, double qty, int detno);

	void toAppointedProdSaleReturnOut(int pi_id, int pd_id, double qty, int detno);

	void toAppointedAppropriationOut(int pi_id, int pd_id, double qty, int detno);

	void toAppointedProdDefectIn(int pi_id, int pd_id, double qty, int detno);

	void toAppointedProdOutReturn(int pi_id, int pd_id, double qty, int detno);

	void toAppointedProdSaleReturn(int pi_id, int pd_id, double qty, int detno);

	JSONObject newMRB(int pd_id, double qty);

	/**
	 * 是否入库类型
	 * 
	 * @param caller
	 * @return
	 */
	boolean isIn(String caller);

	/**
	 * 是否出库类型
	 * 
	 * @param caller
	 * @return
	 */
	boolean isOut(String caller);

	void restoreSNYqty(double uqty, Object sndid);

	void restoreSaleYqty(double uqty, String sdcode, Integer sddetno);

	void restoreSNWithQty(int pdid, Double uqty, Object sndid, Object sdcode);

	void restoreSaleWithQty(int pdid, Double uqty, Object sdcode, Object sddetno);

	void restorePurcYqty(double uqty, String pdcode, Integer pddetno);
}
