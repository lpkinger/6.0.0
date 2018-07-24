package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

/**
 * @author yingp
 * 
 */
public interface SendNotifyDao {
	int turnProdIN(int id);

	String newProdInOut();

	String getPICodeBySourceCode(int id);

	void toAppointedProdInOut(int pi_id, String pi_code, int snd_id, double qty, int detno, String whcode, String whName, String type);

	JSONObject newProdInOutWithCustomer(int custid, String custcode, String custname, String currency, Double rate, int cusaddressid);

	void getCustomer(int[] id);

	void checkSNDQty(int sndid, Object snid);

	void deleteSendNotify(int id);

	void restoreSaleWithQty(int sndid, Double f, Object ordercode, Object orderdetno);

	/**
	 * 修改来源送货提醒单的已转数
	 * 
	 * @param sndId
	 * @param thisQty
	 */
	void restoreNoticeWithQty(int sndId, Double thisQty, Object ordercode, Object orderdetno);

	void restoreSaleYqty(double snd_outqty, String snd_ordercode, Integer detno);

	void restoreSale(int sndid);
	
	/**
	 * 出货通知单删除时，修改送货提醒已转数
	 * 
	 * @param sndId
	 */
	void restoreNotice(int sndId);

	/**
	 * 重新计算通知单的已转数量、以及状态
	 * 
	 * @param sn_id
	 */
	void calYqty(int sn_id);

	JSONObject newProdInOutBySendNotify(int sn_id, String whcode, String whName, String pi_class);

	/**
	 * 出货通知单转入出货单之前，判断thisqty ≤ qty - yqty
	 */
	void checkAdYqty(List<Map<Object, Object>> datas, String piclass);

	String turnProdInOutCustomer(String caller, List<Map<Object, Object>> maps);
}
