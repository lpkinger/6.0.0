package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.fa.RemainQueryService;

@Service
public class RemainQueryServiceImpl implements RemainQueryService{
	
	@Autowired
	private BaseDao baseDao;
	
	static final String CMC = "SELECT * FROM CateMonthCurrency left join Category on cmc_catecode=ca_code WHERE cmc_yearmonth=? order by cmc_catecode";
	
	static final String CMC_CATE = "SELECT * FROM CateMonthCurrency left join Category on cmc_catecode=ca_code WHERE cmc_yearmonth=? and cmc_catecode like '@CATE%' order by cmc_catecode";
	
	@Override
	public List<Map<String, Object>> RemainQuery(String condition) {
		List<Map<String, Object>> store = new ArrayList<Map<String,Object>>();
		try {
			JSONObject d = JSONObject.fromObject(condition);
			Object chkhaveun = d.get("chkhaveun");//显示未记账凭证
			Object cate = d.get("cmc_catecode");
			SqlRowList rs = null;
			if(cate != null && cate.toString().length() > 0) {
				rs = baseDao.queryForRowSet(CMC_CATE.replace("@CATE", cate.toString()), d.get("cmc_yearmonth"));
			} else {
				rs = baseDao.queryForRowSet(CMC, d.get("cmc_yearmonth"));
			}
			while(rs.next()){
				if(chkhaveun != null && !"0".equals(chkhaveun.toString())) {
					store.add(getRemain(rs));
				} else {
					store.add(getData(rs));
				}
			}
		} catch(RuntimeException e){
			e.printStackTrace();
			BaseUtil.showError(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return store;
	}


	/**
	 * @param chkhaveun {boolean} 包含未记账
	 */
	private Map<String, Object> getRemain(SqlRowList rs) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("cmc_yearmonth", rs.getGeneralInt("cmc_yearmonth"));
		item.put("cmc_catecode", rs.getString("cmc_catecode"));
		item.put("ca_description", rs.getString("ca_description"));
		item.put("cmc_currency", rs.getString("cmc_currency"));
		item.put("cmc_doublebegindebit", rs.getGeneralDouble("cmc_doublebegindebit"));
		item.put("cmc_begindebit", rs.getGeneralDouble("cmc_begindebit"));
		item.put("cmc_doublebegincredit", rs.getGeneralDouble("cmc_doublebegincredit"));
		item.put("cmc_begincredit", rs.getGeneralDouble("cmc_begincredit"));
		//本期借方（原币）	120	-1	0	0	cmc_doublenowdebit
		//本期借方（本币）	120	-1	0	0	cmc_nowdebit
		//本期贷方（原币）	120	-1	0	0	cmc_doublenowcredit
		//本期贷方（本币）	120	-1	0	0	cmc_nowcredit
		//期末借方（原币）	120	-1	0	0	cmc_doubleenddebit
		//期末借方（本币）	120	-1	0	0	cmc_enddebit
		//期末贷方（原币）	120	-1	0	0	cmc_doubleendcredit
		//期末贷方（本币）	120	-1	0	0	cmc_endcredit
		item.put("cmc_nowdebit", rs.getGeneralDouble("cmc_umnowdebit"));
		item.put("cmc_nowcredit", rs.getGeneralDouble("cmc_umnowcredit"));
		item.put("cmc_enddebit", rs.getGeneralDouble("cmc_umenddebit"));
		item.put("cmc_endcredit", rs.getGeneralDouble("cmc_umendcredit"));
		item.put("cmc_doublenowdebit", rs.getGeneralDouble("cmc_umdoublenowdebit"));
		item.put("cmc_doublenowcredit", rs.getGeneralDouble("cmc_umdoublenowcredit"));
		item.put("cmc_doubleenddebit", rs.getGeneralDouble("cmc_umdoubleenddebit"));
		item.put("cmc_doubleendcredit", rs.getGeneralDouble("cmc_umdoubleendcredit"));
		return item;
	}
	
	private Map<String, Object> getData(SqlRowList rs) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("cmc_yearmonth", rs.getGeneralInt("cmc_yearmonth"));
		item.put("cmc_catecode", rs.getString("cmc_catecode"));
		item.put("ca_description", rs.getString("ca_description"));
		item.put("cmc_currency", rs.getString("cmc_currency"));
		item.put("cmc_doublebegindebit", rs.getGeneralDouble("cmc_doublebegindebit"));
		item.put("cmc_begindebit", rs.getGeneralDouble("cmc_begindebit"));
		item.put("cmc_doublebegincredit", rs.getGeneralDouble("cmc_doublebegincredit"));
		item.put("cmc_begincredit", rs.getGeneralDouble("cmc_begincredit"));
		//本期借方（原币）	120	-1	0	0	cmc_doublenowdebit
		//本期借方（本币）	120	-1	0	0	cmc_nowdebit
		//本期贷方（原币）	120	-1	0	0	cmc_doublenowcredit
		//本期贷方（本币）	120	-1	0	0	cmc_nowcredit
		//期末借方（原币）	120	-1	0	0	cmc_doubleenddebit
		//期末借方（本币）	120	-1	0	0	cmc_enddebit
		//期末贷方（原币）	120	-1	0	0	cmc_doubleendcredit
		//期末贷方（本币）	120	-1	0	0	cmc_endcredit
		item.put("cmc_nowdebit", rs.getGeneralDouble("cmc_nowdebit"));
		item.put("cmc_nowcredit", rs.getGeneralDouble("cmc_nowcredit"));
		item.put("cmc_enddebit", rs.getGeneralDouble("cmc_enddebit"));
		item.put("cmc_endcredit", rs.getGeneralDouble("cmc_endcredit"));
		item.put("cmc_doublenowdebit", rs.getGeneralDouble("cmc_doublenowdebit"));
		item.put("cmc_doublenowcredit", rs.getGeneralDouble("cmc_doublenowcredit"));
		item.put("cmc_doubleenddebit", rs.getGeneralDouble("cmc_doubleenddebit"));
		item.put("cmc_doubleendcredit", rs.getGeneralDouble("cmc_doubleendcredit"));
		return item;
	}
}