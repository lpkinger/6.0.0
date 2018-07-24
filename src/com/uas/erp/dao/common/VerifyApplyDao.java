package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public interface VerifyApplyDao {
	int turnStorage(int id);

	JSONObject detailTurnStorage(String caller, List<Map<Object, Object>> maps);

	void deleteVerifyApply(int id);

	void restorePurc(int id);

	void restorePurcWithQty(int id, double uqty,double yqty);
	
	void restorePurcYqty(Object vadid, double uqty, String vad_pucode, Integer vad_pudetno);

	/**
	 * 批量转检验单
	 * 
	 * @param ids
	 * @param employee
	 * @param language
	 * @param qcClass
	 * @param qcType
	 * @param statusCode
	 * @return
	 */
	Map<Integer, String> turnQC(String ids, String qcClass, String qcType, String statusCode);

	/**
	 * 批量转检验单(免检)
	 * 
	 * @param ids
	 * @param employee
	 * @param language
	 * @param qcClass
	 * @param qcType
	 * @param statusCode
	 * @return
	 */
	Map<Integer, String> turnFreeQC(String ids, String qcClass, String qcType, String statusCode);

	void updatesourceqty(int vaid);

	void resauditsourceqty(int vaid);
	
	/**
	 * 生成条码号公用方法
	 * @param pr_code
	 * @param ve_id
	 * @return
	 */
	String barcodeMethod (String pr_code,String ve_id,int num);

	/**
	 * 生成包装箱号方法
	 * @param string
	 * @return
	 */
	String outboxMethod(String pr_id,String tracekind);
	
	void updateAccStatus(int vadid);
	
	void updatePurcStatus2(String pucode);
}
