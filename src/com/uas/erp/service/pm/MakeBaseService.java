package com.uas.erp.service.pm;

import java.util.List;
import java.util.Map;

public interface MakeBaseService {
	Map<String, String> statistics(String param);

	void saveMakeBase(String formStore, String gridStore, String caller);

	void updateMakeBase(String formStore, String gridStore, String caller);

	void deleteMakeBase(int ma_id, String caller);

	void auditMakeBase(int ma_id, String caller);

	void resAuditMakeBase(int ma_id, String caller);

	void approveMakeBase(int ma_id, String caller);

	void resApproveMakeBase(int ma_id, String caller);

	void submitMakeBase(int ma_id, String caller);

	void resSubmitMakeBase(int ma_id, String caller);

	void endMakeBase(int ma_id, String caller);

	void resEndMakeBase(int ma_id, String caller);

	void setMakeMaterial(String code, String caller);

	void makeMaterialCheck(String code, String caller);

	void saveMakeSubMaterial(String formStore, String caller);

	void deleteMakeSubMaterial(String formStore, String caller);

	Object saveModifyMaterial(String formStore, String caller);

	void deleteModifyMaterial(int id, String caller);

	String[] printMakeBase(int sa_id, String caller, String reportName, String condition);

	void calThisQty(String ids, String caller);

	void calOnlineQty(String ids, String caller,String data);

	void enforceEndMake(int ma_id, String caller, String remark);

	/**
	 * 计算本次可补料数
	 * 
	 * @param ids
	 *            ma_id
	 */
	void calAddQty(String ids, String caller);

	void vastCloseMake(String data, String caller);

	void vastEnforceEndMake(String data, String caller);
	
	void vastApproveMake(String data, String caller);

	void vastResStart(String data, String caller);

	void vastFinishResStart(String data, String caller);

	void setLSThisqty(String caller, String maid, Integer setqty, String wipwhcode);

	void refreshqty(Integer maid, String caller, Integer mmid);

	List<Object> checkmfcode(String mf_code, String caller);

	void splitMake(String formdata, String data, String caller);

	void updateOSVendor(Integer id, String vendcode, String currency, String taxrate, String price, String paymc, String paym,
			String ma_servicer, String remark, String apvendcode, String caller);

	void vastupdatemakecoststatus(String data, String caller);

	void vastcostrestartMake(String data, String caller);

	void updateRemark(int id, String remark, String caller);

	void updateTeamcode(int id, String value, String caller);

	void updateMaterialWH(int id, String whcode, String caller);

	void turnOSMake(String caller, Integer maid, String kind);

	void turnOSToMake(String caller, Integer maid, String kind);

	String getCodeString(String caller, String table, int type, String conKind);

	void setMain(Integer mmid, Integer detno, String caller);

	void updateCraftById(String formStore, String caller);

	void updateMaStyle(int id, String value, String caller);

	void updateShiPAddress(Integer id, String address, String caller);

	void closeMrp(int id, String caller);

	void openMrp(int id, String caller);

	List<Map<String,Object>> getPastBom(Long ma_id, String caller);

	void disableBomPast(Long mm_id, String caller);
	
	void vastFreeze(String data, String caller);
	
	String vastWriteoff(String data,String caller);
	
	String createReturnMake(String data, String caller);

//	void calBackQty(String data, String caller);
}
