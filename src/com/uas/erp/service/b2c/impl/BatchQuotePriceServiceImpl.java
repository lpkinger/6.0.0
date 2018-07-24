package com.uas.erp.service.b2c.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.uas.api.b2c_erp.seller.model.GoodsPriceUas;
import com.uas.b2c.service.seller.B2CGoodsPriceService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.b2c.BatchQuotePriceService;

@Service
public class BatchQuotePriceServiceImpl implements BatchQuotePriceService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	@Autowired
	private B2CGoodsPriceService b2CGoodsPriceService;

	@Override
	public Map<String, Object> getCurrencyAndTaxrate(String caller, String code) {
		String currency = baseDao.getDBSetting("sys", "defaultCurrency");
		double taxrate = 0;
		Object cu_rate = baseDao.getFieldDataByCondition("Currencys", "cr_taxrate", "cr_name='"+currency+"'");
		double rate = cu_rate == null ? 17 : Double.parseDouble(cu_rate.toString());
		if("RMB".equals(currency)){
			taxrate = rate;
		}
		String defaultcurrency = baseDao.getDBSetting("B2CSetting", "B2CDefaultCurrency");
		Object monthrate = "";
		int nowdate = Integer.parseInt(DateUtil.getCurrentDate().toString().substring(8,10));
		int month = Integer.parseInt(DateUtil.getCurrentDate().toString().substring(0,8).replace("-", ""));
		if(nowdate<15){
			monthrate = baseDao.getFieldDataByCondition("CURRENCYSMONTH", "(case when cm_crrate is null then cm_endrate else cm_crrate  end)cm_crrate", "CM_CRNAME='"+defaultcurrency+"' and  CM_YEARMONTH='"+month+"'");
		}else{
			monthrate = baseDao.getFieldDataByCondition("CURRENCYSMONTH", "(case when cm_endrate is null then cm_crrate else cm_endrate  end)cm_endrate", "CM_CRNAME='"+defaultcurrency+"' and  CM_YEARMONTH='"+month+"'");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("currency", currency);
		map.put("taxrate", taxrate);
		map.put("monthrate", monthrate);
		return map;
	}

	@Override
	public Map<String, Object> quotePrice(String caller, String parameters) {
		Employee employee = SystemSession.getUser();
		String emName = employee.getEm_name();

		Map<String, Object> map = new HashMap<String, Object>();
		Map<Object, Object> recvMap = FlexJsonUtil.fromJson(parameters);
		List<Map<Object, Object>> datas = BaseUtil.parseGridStoreToMaps(recvMap
				.get("gridStore").toString());

		String currency = recvMap.get("currency").toString();
		String taxrate = recvMap.get("taxrate").toString();

		List<String> updateSql = new ArrayList<String>();
		List<String> updateGoodsSql = new ArrayList<String>();
		List<String> quotationLog = new ArrayList<String>();
		List<GoodsPriceUas> goodsPriceUas = new ArrayList<GoodsPriceUas>();

		String goidCondition = "";

		if (datas != null) {
			for (Map<Object, Object> data : datas) {

				//记录goid
				if(data.get("go_id")!=null){
					if(Integer.parseInt(data.get("go_id").toString())!=0){
						goidCondition += "," + data.get("go_id");
					}
				}
				
				
				
				// 更新B2C$goodsonhand.go_saleprice报价
				if (data.get("go_id") != null&Integer.parseInt(data.get("go_id").toString())!=0) {
					String defaultcurrency = baseDao.getDBSetting("B2CSetting", "B2CDefaultCurrency");
					if("USD".equals(defaultcurrency)){
						String sql = "update b2c$goodsonhand set go_usdsaleprice="
								+ data.get("newprice") + ",go_saleprice=0 where go_id="
								+ data.get("go_id");
						updateSql.add(sql);
					}else{
						String sql = "update b2c$goodsonhand set go_saleprice="
								+ data.get("newprice") + ",go_usdsaleprice=0 where go_id="
								+ data.get("go_id");
						updateSql.add(sql);
					}
				}
				Object pr_avpurcprice = baseDao.getFieldDataByCondition("Product", "pr_avpurcprice", "pr_code='"+data.get("pr_code")+"'");
				// 记录日志到B2C$Quotationlog表
				String insertSql = "insert into b2c$quotationlog(ql_id,ql_date,ql_man,ql_uuid,ql_prodcode,ql_costprice,ql_oldprice,ql_newprice,ql_currency,ql_erpunit,ql_taxrate,ql_remark,ql_action) select b2c$quotationlog_seq.nextval,sysdate,'"
						+ emName
						+ "',"
						+ data.get("pr_uuid")
						+ ",'"
						+ data.get("pr_code")
						+ "',"
						+ pr_avpurcprice
						+ ","
						+ data.get("go_saleprice")
						+ ","
						+ data.get("newprice")
						+ ",'"
						+ currency
						+ "','"
						+ data.get("go_erpunit")
						+ "','"
						+ taxrate
						+ "',null,'一键报价' from dual";
				quotationLog.add(insertSql);
			}
			baseDao.execute(updateSql);
			baseDao.execute(quotationLog);

			// 调用平台接口更新价格，并更新上架单goodsdetail的价格
			if(!"".equals(goidCondition)){
				goidCondition = goidCondition.substring(1);
				
				SqlRowSet rs = baseDao.getJdbcTemplate().queryForRowSet("select  go_saleprice,go_uuid from B2c$goodsonhand left join (select go_prodcode prodcode,sum(go_onhand) onhand from goodspwonhand where go_onhand>0 group by go_prodcode)A on A.prodcode=go_prodcode where go_id in ("+goidCondition+") and onhand>0");
				
				while(rs.next()){
					GoodsPriceUas goodUas = new GoodsPriceUas();
					goodUas.setCurrencyName(currency);
					goodUas.setOriginal(1311);
					goodUas.setPrice(rs.getDouble("go_saleprice"));
					goodUas.setTax(Short.parseShort(taxrate));
					goodUas.setUuid(rs.getString("go_uuid"));
					goodsPriceUas.add(goodUas);
					
					String goodsSql = "update goodsdetail set gd_price="+rs.getDouble("go_saleprice")+" where gd_uuid='"+rs.getString("go_uuid")+"'";
					updateGoodsSql.add(goodsSql);
				}
				
				baseDao.execute(updateGoodsSql);
				
				try{
					b2CGoodsPriceService.maintain(goodsPriceUas, null, null);
				}catch(Exception e){
					BaseUtil.showError(e.getMessage());
				}
			}
		}
		return map;
	}
}
