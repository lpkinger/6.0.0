package com.uas.erp.service.pm;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.GridPanel;

public interface MPSMainService {
	void saveMPS(String formStore, String caller);

	void updateMPSById(String formStore, String param, String caller);

	void deleteMPS(int bo_id, String caller);

	void auditMPS(int id, String caller);

	void resAuditMPS(int bo_id, String caller);

	void submitMPS(int bo_id, String caller);

	void resSubmitMPS(int bo_id, String caller);

	void LoadData(int keyValue, String mainCode, String caller, String detailcaller, String Store, String gridStore, String kind);

	void deleteAllDetails(int id, String caller);

	int getCountByCaller(String caller, String condition);

	GridPanel getDataListGridByCaller(String caller, String condition, Boolean _self);

	int getMPSPRonorder(String caller, String condition);

	GridPanel getMPSPRonorder(String caller, String condition, Boolean _self, int page, int pageSize);

	String turnReplaceProd(String data, String apdata, String purchasecode, String caller);

	void loadSaleDetailDet(int keyValue, String type, String caller, String data, String condition);

	String RunMrp(String code, String caller);

	Map<String, Object> NeedThrow(String caller, String mainCode, String gridStore, String toWhere, String toCode, String condition,String maKind,String purcaseCop,String apKind);

	String mpsdesk_turnmake(String code, String caller);

	String mpsdesk_turnpurchase(String code, String caller);

	String mpsdesk_turnpurchaseforecast(String code, String caller);

	String getSum(String fields, String caller, String condition);

	void autoLoadData(int id, String caller);

	String turnSupplyToNeed(String caller, String gridstore, String maincode);

	String getMaxMcode(String caller, String code);

	public List<Map<String, Object>> getGridData(String caller, String condition);

	String TurnGoodsUp(String caller, String mainCode, String gridStore, String toWhere);

	/**
	 * 库存运算及上架
	 * 
	 * @param code
	 * @param caller
	 * @return
	 */
	String RunMrpAndGoods(String code, String caller);

	void updatePoLockqty(String caller, String data);

	String TurnDeviceInApply(String caller, String gridStore);

	List<Map<String, Object>> getSeriousWarn(String caller, String code);

	String throwCancle(String gridStore);

	void autoThrow() throws Exception;
}
