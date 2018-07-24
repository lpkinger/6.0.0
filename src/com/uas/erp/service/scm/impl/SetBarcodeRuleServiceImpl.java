package com.uas.erp.service.scm.impl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.SetBarcodeRuleService;

@Service("setBarcodeRuleService")
public class SetBarcodeRuleServiceImpl implements SetBarcodeRuleService{
	@Autowired  
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void saveRule(String formStore, String param, String caller) {
		// TODO Auto-generated method stub
		Map<Object, Object> store = FlexJsonUtil.fromJson(formStore);
		handlerService.handler(caller, "save", "before", new Object[] {store});
		int totallen = Integer.parseInt(store.get("bs_lenprid").toString())+
				Integer.parseInt(store.get("bs_lennum").toString());
		if("BarCodeSet!BATCH".equals(caller)){
			totallen += Integer.parseInt(store.get("bs_lenveid").toString());
			totallen += store.get("bs_datestr").toString().length();
		}
		store.put("bs_totallen", totallen);
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "barcodeset", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.logger.save(caller, "bs_id", store.get("bs_id"));
		handlerService.handler(caller, "save", "after", new Object[] {store});
	}

	@Override
	public Map<String,Object> getData(String condition, String caller) {
		// TODO Auto-generated method stub
		Map<String,Object> map = new HashMap<String, Object>();
		SqlRowList rs = baseDao.queryForRowSet("select * from barcodeset where "+condition);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(rs.next()){
			if("BarCodeSet!BATCH".equals(caller)){
				map.put("bs_lenveid", rs.getObject("bs_lenveid"));
				map.put("bs_datestr", rs.getObject("bs_datestr")==null?"无":rs.getObject("bs_datestr"));
				map.put("bs_maxdate", rs.getObject("bs_maxdate"));
			}
			map.put("bs_maxnum", lpad(rs.getInt("bs_lennum"),rs.getString("bs_maxnum")));
			map.put("bs_id", rs.getObject("bs_id"));
			map.put("bs_lenprid", rs.getObject("bs_lenprid"));
			map.put("bs_lennum", rs.getObject("bs_lennum"));
			map.put("bs_totallen", rs.getObject("bs_totallen"));
			map.put("bs_date", sdf.format(rs.getDate("bs_date")));
			map.put("bs_recorder", rs.getObject("bs_recorder"));
			map.put("bs_type", rs.getObject("bs_type"));
		}else{
			return null;
		}
		return map;
	}

	@Override
	public void updateRule(String formStore, String param, String caller) {
		// TODO Auto-generated method stub
		Map<Object, Object> store = FlexJsonUtil.fromJson(formStore);
		handlerService.beforeUpdate(caller, new Object[]{store});
		int totallen = Integer.parseInt(store.get("bs_lenprid").toString())+
				Integer.parseInt(store.get("bs_lennum").toString());
		if("BarCodeSet!BATCH".equals(caller)){
			totallen += Integer.parseInt(store.get("bs_lenveid").toString());
			totallen += store.get("bs_datestr").toString().length();
		}
		store.put("bs_totallen", totallen);
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "barcodeset","bs_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "bs_id", store.get("bs_id"));
		//执行保存后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
	}
	
	private String lpad(int length, String number){
		if(number.length() > length){
			return number;
		}
		while (number.length() < length) {
			number = "0" + number;
		}
		number = number.substring(number.length() - length, number.length());
		return number;
	}
	
}
