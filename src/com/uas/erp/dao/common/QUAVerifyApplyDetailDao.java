package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

/**
 * @author yingp
 * 
 */
public interface QUAVerifyApplyDetailDao {
	JSONObject newProdIO(String caller, String whcode, String piclass);

	JSONObject newProdIO2(String caller, String piclass);

	void turnMadeWh(String no, int veid, double qty);

	List<JSONObject> detailTurnStorage(String caller, String piclass, List<Map<Object, Object>> maps, boolean isok);

	List<JSONObject> detailTurnStorageByVacode(String caller, String piclass, List<Map<Object, Object>> maps, boolean isok);

	void updateverifyqty(int veid);

	void deleteQC(int id,String caller);

	String checkqtyCheck(int id);

	List<JSONObject> turnFinish(String caller, String piclass, List<Map<Object, Object>> maps, boolean isok, JSONObject j);

	List<JSONObject> detailTurnStorageOs(String caller, String piclass, List<Map<Object, Object>> maps, boolean isok);
	
	List<JSONObject> OSdetailTurnStorageOs(String caller, String piclass, List<Map<Object, Object>> maps, boolean isok);

	List<JSONObject> detailTurnStorageOsByVacode(String caller, String piclass, List<Map<Object, Object>> maps, boolean isok);

	/**
	 * 检验单批量入库之前，判断检验单明细状态
	 */
	void checkstatus(List<Map<Object, Object>> datas);

	void updatesourceqty(int veid);

	void resauditsourceqty(int veid);

	int turnProdAbnormal(int id);

	int turnT8DReport(int id);
}
