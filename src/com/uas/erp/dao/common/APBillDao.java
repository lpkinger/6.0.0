package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.Employee;

public interface APBillDao {
	Object[] turnBillOutAP(String language, String vcode, String curr, Employee employee, Object bidate, Object refno);

	void turnBillOutAPDetail(String no, int abdid, int abid, int detno, Double qty, Double price);

	/**
	 * 判断thisqty ≤ qty - yqty
	 */
	void checkyqty(List<Map<Object, Object>> datas);

	Object[] turnAPCheck(String vcode, String curr, Object soureid, Object sourcetype);

	void turnAPCheckDetail(String no, Object sourcedetailid, int id, int detno, Double qty, Double price, Object sourcetype);

	void checkqty(List<Map<Object, Object>> datas);

	void apbill_return_deletedetail(int abdid, int sourcedetailid, Object sourcekind, int adid);

	/**
	 * 修改出入库单开票状态
	 */
	void updateProdIOBillStatus(Long pi_id);

	/**
	 * 修改暂估单开票状态
	 */
	void updateEstimateBillStatus(Integer es_id);

	void updateSourceYqty(int abdid, int sourcedetailid, Object sourcekind);
}
