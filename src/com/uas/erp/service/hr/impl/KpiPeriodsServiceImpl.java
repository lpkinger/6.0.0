package com.uas.erp.service.hr.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.KpiPeriodsService;

@Service
public class KpiPeriodsServiceImpl implements KpiPeriodsService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	@Transactional
	public void saveKpiPeriods(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid=new ArrayList<Map<Object,Object>>();
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] {store, grid});
		if("年".equals(store.get("pe_type")+"")){
			Calendar start=Calendar.getInstance();
			start.setTime(DateUtil.parseStringToDate(store.get("pe_fromdate")+"", null));
			Calendar end=Calendar.getInstance();
			end.setTime(DateUtil.parseStringToDate(store.get("pe_todate")+"", null));
			int times=end.get(Calendar.YEAR)-start.get(Calendar.YEAR);
			for(int i=0;i<times;i++){
				Map<Object, Object> map=new HashMap<Object, Object>();
				map.put("pd_id", baseDao.getSeqId("PeriodsDetail_seq"));
				map.put("pd_pdno", i+1);
				map.put("pd_name", "Y"+start.get(Calendar.YEAR));
				map.put("pd_year", start.get(Calendar.YEAR));
				map.put("pd_startdate", start.get(Calendar.YEAR)+"-01-01");
				map.put("pd_enddate", start.get(Calendar.YEAR)+"-12-31");
				map.put("pd_peid", store.get("pe_id"));
				grid.add(map);
				start.add(Calendar.YEAR, 1);
			}
		}else if("季度".equals(store.get("pe_type")+"")){
			Calendar start=Calendar.getInstance();
			start.setTime(DateUtil.parseStringToDate(store.get("pe_fromdate")+"", null));
			Calendar end=Calendar.getInstance();
			end.setTime(DateUtil.parseStringToDate(store.get("pe_todate")+"", null));
			start.add(Calendar.MONTH, -(start.get(Calendar.MONTH)%3));//默认为正常季度的开始月份
			start.add(Calendar.DATE, 1-start.get(Calendar.DAY_OF_MONTH));
			int month=0;
			if(end.get(Calendar.MONTH)%3!=0){
				month=3-end.get(Calendar.MONTH)%3;
			}
			end.add(Calendar.MONTH, month);//默认为正常季度的结束月份
			int times=(end.get(Calendar.YEAR)-start.get(Calendar.YEAR))*4+(end.get(Calendar.MONTH)-start.get(Calendar.MONTH))/3;
			for(int i=0;i<times;i++){
				Map<Object, Object> map=new HashMap<Object, Object>();
				map.put("pd_id", baseDao.getSeqId("PeriodsDetail_seq"));
				map.put("pd_pdno", i+1);
				map.put("pd_name", start.get(Calendar.YEAR)+"Q0"+(start.get(Calendar.MONTH)/3+1));//月份是从零开始的，所以要加1
				map.put("pd_year", start.get(Calendar.YEAR));
				map.put("pd_startdate", DateUtil.parseDateToString(start.getTime(), null));
				start.add(Calendar.MONTH, 3);
				start.add(Calendar.DATE,-1);
				map.put("pd_enddate", DateUtil.parseDateToString(start.getTime(), null));
				start.add(Calendar.DATE,1);
				map.put("pd_peid", store.get("pe_id"));
				grid.add(map);
			}
		}else if("月".equals(store.get("pe_type")+"")){
			Calendar start=Calendar.getInstance();
			start.setTime(DateUtil.parseStringToDate(store.get("pe_fromdate")+"", null));
			Calendar end=Calendar.getInstance();
			end.setTime(DateUtil.parseStringToDate(store.get("pe_todate")+"", null));
			int times=(end.get(Calendar.YEAR)-start.get(Calendar.YEAR))*12+end.get(Calendar.MONTH)-start.get(Calendar.MONTH);
			start.add(Calendar.DATE, 1-start.get(Calendar.DAY_OF_MONTH));//日期设为月份的第一天
			for(int i=0;i<times;i++){
				Map<Object, Object> map=new HashMap<Object, Object>();
				map.put("pd_id", baseDao.getSeqId("PeriodsDetail_seq"));
				map.put("pd_pdno", i+1);
				String month="";
				if(start.get(Calendar.MONTH)+1<10){
					month="0"+(start.get(Calendar.MONTH)+1);
				}else{
					month=""+(start.get(Calendar.MONTH)+1);
				}
				map.put("pd_name", start.get(Calendar.YEAR)+"M"+month);
				map.put("pd_year", start.get(Calendar.YEAR));
				map.put("pd_startdate", DateUtil.parseDateToString(start.getTime(), null));
				start.add(Calendar.MONTH, 1);
				start.add(Calendar.DATE,-1);
				map.put("pd_enddate", DateUtil.parseDateToString(start.getTime(), null));
				start.add(Calendar.DATE,1);
				map.put("pd_peid", store.get("pe_id"));
				grid.add(map);
			}
		}else if("周".equals(store.get("pe_type")+"")){
			Calendar start=Calendar.getInstance();
			start.setTime(DateUtil.parseStringToDate(store.get("pe_fromdate")+"", null));
			Calendar end=Calendar.getInstance();
			end.setTime(DateUtil.parseStringToDate(store.get("pe_todate")+"", null));
			int day=2-start.get(Calendar.DAY_OF_WEEK);
			if(day==1){
				day=-6;
			}
			start.add(Calendar.DATE, day);//默认星期一为一周的开始
			if(end.get(Calendar.DAY_OF_WEEK)==1){
				day=0;
			}else{
				day=7-end.get(Calendar.DAY_OF_WEEK);
			}
			end.add(Calendar.DATE, day);
			long times=((end.getTimeInMillis()-start.getTimeInMillis())/(1000*3600*24)+1)/7;
			for(int i=0;i<times;i++){
				Map<Object, Object> map=new HashMap<Object, Object>();
				String week=""+start.get(Calendar.WEEK_OF_YEAR);
				if(start.get(Calendar.WEEK_OF_YEAR)<10){
					week="0"+week;
				}
				map.put("pd_pdno", i+1);
				map.put("pd_id", baseDao.getSeqId("PeriodsDetail_seq"));
				map.put("pd_name", start.get(Calendar.YEAR)+"W"+week);
				map.put("pd_year", start.get(Calendar.YEAR));
				map.put("pd_startdate", DateUtil.parseDateToString(start.getTime(), null));
				start.add(Calendar.DATE, 6);
				map.put("pd_enddate", DateUtil.parseDateToString(start.getTime(), null));
				start.add(Calendar.DATE,1);
				map.put("pd_peid", store.get("pe_id"));
				grid.add(map);
				
			}
		}
		 List<String> sqls=SqlUtil.getInsertSqlbyGridStore(grid, "PeriodsDetail");
		 String sql=SqlUtil.getInsertSqlByFormStore(store,
					"Periods", new String[] {}, new Object[] {});
		 baseDao.execute(sql);
		 baseDao.execute(sqls);
		try {
			// 记录操作
			baseDao.logger.save(caller, "pe_id", store.get("ke_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store, grid });
	}

}
