package com.uas.erp.service.scm;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.ProdChargeDetail;

public interface ProdInOutService {
	void saveProdInOut(String caller, String formStore, String gridStore);

	void deleteProdInOut(String caller, int pi_id);

	void updateProdInOutById(String caller, String formStore, String gridStore);

	void auditProdInOut(int pi_id, String caller);

	String turnProdinoutIn(int pi_id, String caller);

	void updatepdPrice(int pi_id, String caller);

	void resAuditProdInOut(String caller, int pi_id);

	void submitProdInOut(String caller, int pi_id);

	void resSubmitProdInOut(String caller, int pi_id);

	void postProdInOut(int pi_id, String caller);

	void resPostProdInOut(String caller, int pi_id);

	void confirmIn(String caller, int pi_id);

	void resetBatchCode(String caller, int pi_id);

	void updatepdscaleremark(int pi_id, String field, String data);

	void updateProdInOutOtherInRemark(int pi_id, String remark, String caller);

	/**
	 * 换货入库单转换货出库单
	 * 
	 * @param pi_id
	 * @param language
	 * @param employee
	 */
	String turnExOut(String caller, int pi_id);

	String turnTurnProdinoutReturn(String caller, int pi_id);

	String turnTurnProdinoutReturnnew(String caller, String data);

	String split(String caller, int pi_id, String piclass);

	boolean checkresetBatchCode(String caller);

	String[] printProdInOut(String caller, int pi_id, String reportName, String condition);

	String Subpackage(int pi_id);

	String ClearSubpackage(int pi_id);

	String SubpackageDetail(int pd_id, double tqty);

	String ClearSubpackageDetail(int pd_id);

	String[] PrintBarDetail(int pd_id, String reportName, String condition);

	String[] printBar(int pi_id, String reportName, String condition, String caller);

	void catchBatch(String caller, int id);

	void loadOnHandQty(int id);

	void SetMMQTY(int pi_id, String caller);

	void updatebgxh(String data);

	void updateOrderCode(String data);

	void updateBatchCode(String data);

	String turnMRB(String caller, String data);

	String turnYPOutREturnnew(String caller, String data);

	void updateBorrowCargoType(int pi_id, String type, String remark, String caller);

	String vastTurnIn(String data, String caller);

	String updateWhCodeInfo(String data, String caller);

	void getPrice(int pdid, int piid, String caller);

	void generateBarcodeByZxbzs(int pi_id, String pi_class, String caller);

	void createBill(String caller, int id);

	void createOtherBill(String caller, int id);

	void catchBatchByOrder(Long pd_piid, Long pd_id, String caller);

	void catchBatchByIncode(Long pi_id, String caller);

	void catchBatchByClient(String type, Long pi_id, String caller);
	
	void catchBatchBySeller(Long pi_id, String caller);

	void splitProdIODetail(String formStore, String param, String caller);

	String turnPaIn(Long pi_id, String caller);

	void printCheck(int pi_id, String caller);

	/**
	 * 费用类型
	 * 
	 * @param piclass
	 *            出入库单据类型
	 * @return
	 */
	List<ProdChargeDetail> createProdChargeByKinds(String piclass, int piid);

	void saveProdCharge(String data, String caller);

	/**
	 * @author wsy 更新明细仓库
	 */
	void updateDetailWH(String pi_id, String codevalue, String value, String pd_inwhcode, String pd_inwhname, String caller);

	void checkStatus(int pi_id, String pi_inoutno, String pi_class, String caller);

	void sendEdi(String id, String caller);
	
	void cancelEdi(String id, String caller,String remark);

	void feeShare(Long id, String caller);

	String resPostCheck(String caller, int pi_id);
	
	int turnSale(int id,String caller);
	
	List<Map<Object,Object>> turnEdiToProdin(String ids);
	
	void markEdiAsDone(String ids, String caller);
	
	String getCancelConfig(String caller);
	
	void updateCancelReason(int id ,String value,String caller);

	void getFittingData(String pr_code, String pi_id, String qty, String detno,
			String caller);
}
