package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.Employee;

public interface ARBillDao {
	Object[] turnBillOut(String language, String vcode, String curr, Employee employee, Object bidate, String abid, Object sendtype);

	void turnBillOutDetail(String no, int abdid, int abid, int detno, Double qty, Double price);

	/**
	 * 判断thisqty ≤ qty - yqty
	 */
	void checkyqty(List<Map<Object, Object>> datas);

	Object[] turnARCheck(String vcode, String curr, Object soureid, Object sourcetype);

	void turnARCheckDetail(String no, Object sourcedetailid, int id, int detno, Double qty, Double price, Object sourcetype);

	void checkqty(List<Map<Object, Object>> datas);

	void updateSourceYqty(int abdid, int sourcedetailid, Object sourcekind);

	/**
	 * 修改出入库单开票状态
	 */
	void updateProdIOBillStatus(Long pi_id);

	/**
	 * 修改发出商品开票状态
	 */
	void updateGoodsSendStatus(Integer gs_id);
}
