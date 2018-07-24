package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.fa.MQueryService;

@Service("mQueryService")
public class MQueryServiceImpl implements MQueryService {

	@Autowired
	private BaseDao baseDao;
	
	static final String GL = "SELECT * FROM AssetsMonthStatement WHERE ms_detno BETWEEN ? AND ? ";
	static final String AR = "SELECT * FROM AccountRegister WHERE to_char(ar_date,'yyyymmdd') >= ? and to_char(ar_date,'yyyymmdd') <= ? ";
	
	@Override
	public List<Map<String, Object>> getMQuery(String condition) {
		try {
			JSONObject d = JSONObject.fromObject(condition);
			JSONObject ymd = JSONObject.fromObject(d.get("cm_yearmonth").toString());//期间
			Integer bym = Integer.parseInt(ymd.get("begin").toString());//期间始
			Integer eym = Integer.parseInt(ymd.get("end").toString());//期间始
			SqlRowList rs = baseDao.queryForRowSet(GL, bym, eym);
			return rs.getResultList();
		} catch(RuntimeException e){
			BaseUtil.showError(e.getMessage());
		} catch (Exception e) {
			
		}
		return null;
	}
	@Override
	public List<Map<String, Object>> getARDateQuery(String condition) {
		try {
			JSONObject d = JSONObject.fromObject(condition);
			JSONObject ymd = JSONObject.fromObject(d.get("ar_date").toString());//期间
			String bym = ymd.get("begin").toString();//期间始
			String eym = ymd.get("end").toString();//期间始
			SqlRowList rs = baseDao.queryForRowSet(AR, bym, eym);
			return rs.getResultList();
		} catch(RuntimeException e){
			BaseUtil.showError(e.getMessage());
		} catch (Exception e) {
			
		}
		return null;
	}
}