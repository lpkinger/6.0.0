package com.uas.erp.service.oa.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.oa.SchedulerResourceService;
@Service
public class SchedulerResourceServiceImpl implements SchedulerResourceService {
	@Autowired
	private BaseDao baseDao;
	@Override
	public Map<String,List<Map<String,Object>>> getSchedulerResourceData(String caller) {
		Map<String,List<Map<String,Object>>> map=new HashMap<String,List<Map<String,Object>>>();
		List<Map<String,Object>> lists=new ArrayList<Map<String,Object>>();
		Map<String,String> sqlmap=getSql(caller);
		SqlRowList schsl=baseDao.queryForRowSet(sqlmap.get("schsql"));
		while(schsl.next()){
			lists.add(schsl.getCurrentMap());
		}
		map.put("schdata", lists);
		lists=new ArrayList<Map<String,Object>>();
		SqlRowList datasl=baseDao.queryForRowSet(sqlmap.get("resourcesql"));
		while(datasl.next()){
			lists.add(datasl.getCurrentMap());
		}
		map.put("resourcedata",lists);
		return map;
	}
	private Map<String,String> getSql(String caller){
		Map<String,String> map=new HashMap<String,String>();
		if("Meeting".equals(caller)){
			map.put("schsql", "select ma_code ,ma_type,to_char(ma_starttime,'yyyy-mm-dd HH24:mi:ss') startdate,to_char(ma_endtime,'yyyy-mm-dd HH24:mi:ss') enddate,ma_remark,ma_mrcode ResourceId from Meetingroomapply where ma_statuscode in ('COMMITED','AUDITED')");
			map.put("resourcesql","select mr_name,mr_code Id,mr_site from meetingroom where mr_statuscode='AUDITED' order by mr_name asc");
		}else if("VehicleapplyToVehiclereturn!Deal".equals(caller)){
			map.put("schsql", "select vr_code ,to_char(vr_starttime,'yyyy-mm-dd HH24:mi:ss') startdate,to_char(vr_endtime,'yyyy-mm-dd HH24:mi:ss') enddate,vr_remark,vr_vecard ResourceId from Vehiclereturn where vr_statuscode in ('COMMITED','AUDITED')");
			map.put("resourcesql","select va_card,va_card Id,va_driver from Vehiclearchives order by va_card asc");
		}
		return map;
	}


}
