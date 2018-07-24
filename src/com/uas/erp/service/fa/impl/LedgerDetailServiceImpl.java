package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.LedgerDetailDao;
import com.uas.erp.model.LedgerFilter;
import com.uas.erp.service.fa.LedgerDetailService;

@Service
public class LedgerDetailServiceImpl implements LedgerDetailService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private LedgerDetailDao ledgerDetailDao;

	@Override
	public List<Map<String, Object>> getGLDetail(LedgerFilter filter) {
		int count = baseDao.getCount("select count(1) from subledger_temp where query_id='" + filter.getQueryId() + "'");
		if (count > 5000) {
			BaseUtil.showError("数据量太大，请直接打印！");
		}
		SqlRowList rs = baseDao.queryForRowSet("select * from subledger_temp where query_id=? ORDER BY query_detno", filter.getQueryId());
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		Map<String, Object> item = null;
		while (rs.next()) {
			item = new HashMap<String, Object>();
			item.put("sl_voucherid", rs.getGeneralInt("sl_voucherid"));
			item.put("sl_date", DateUtil.parseDateToString(rs.getDate("sl_date"), Constant.YMD));
			item.put("sl_vocode", rs.getGeneralString("sl_vocode"));
			item.put("sl_vonumber", rs.getGeneralString("sl_leadnum"));
			item.put("sl_explanation", rs.getGeneralString("sl_explanation"));
			item.put("asl_asstype", rs.getGeneralString("sl_asstype"));
			item.put("asl_asscode", rs.getGeneralString("sl_asscode"));
			item.put("asl_assname", rs.getGeneralString("sl_assname"));
			item.put("sl_othercate", rs.getGeneralString("sl_othercate"));
			item.put("sl_debit", rs.getGeneralDouble("sl_debit"));
			item.put("sl_credit", rs.getGeneralDouble("sl_credit"));
			item.put("sl_debitorcredit", rs.getGeneralString("sl_debitorcredit"));
			item.put("sl_balance", rs.getGeneralDouble("sl_balance"));
			item.put("sl_currency", rs.getGeneralString("sl_currency"));
			item.put("sl_rate", rs.getGeneralDouble("sl_rate"));
			item.put("sl_doubledebit", rs.getGeneralDouble("sl_doubledebit"));
			item.put("sl_doublecredit", rs.getGeneralDouble("sl_doublecredit"));
			item.put("sl_doublebalance", rs.getGeneralDouble("sl_dbbalance"));
			item.put("ca_code", rs.getGeneralString("sl_catecode"));
			item.put("ca_name", rs.getGeneralString("sl_catename"));
			item.put("isCount", rs.getGeneralString("query_row_type"));
			item.put("sl_detno", rs.getGeneralDouble("query_detno"));
			item.put("sl_acid", rs.getGeneralString("sl_acid"));
			item.put("sl_assmulti", rs.getGeneralString("sl_assmulti"));
			store.add(item);
		}
		return store;
	}
}