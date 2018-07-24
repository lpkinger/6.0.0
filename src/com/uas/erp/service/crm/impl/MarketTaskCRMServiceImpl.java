package com.uas.erp.service.crm.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.crm.MarketTaskCRMService;
@Service
public class MarketTaskCRMServiceImpl implements MarketTaskCRMService{
	@Autowired
	private BaseDao baseDao;
	@Override
	public void toMarketTask(String gridStore, 
			String caller) {
		String date=DateUtil.parseDateToOracleString(null, new Date());
		String name=SystemSession.getUser().getEm_name();
		StringBuffer sb=null;
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(gridStore);
		for(Map<Object, Object> map:maps){
			String code=baseDao.sGetMaxNumber("MarketTaskCRM", 2);
			sb=new StringBuffer("INSERT INTO MarketTaskCRM (mt_code,mt_begintime,mt_endtime,mt_projectcode,mt_object,mt_reportcode,mt_needqty,mt_tasker,mt_date,mt_recorder,mt_purpose)");
			sb.append(" VALUES (");
			sb.append("'"+code+"',"+DateUtil.parseDateToOracleString(null, map.get("pp_begintime")+"")+",");
			sb.append(DateUtil.parseDateToOracleString(null, map.get("pp_endtime")+"")+",'"+map.get("pp_code")+"','"+map.get("rp_object")+"',");
			sb.append("'"+map.get("mt_reportcode")+"',"+Integer.parseInt(map.get("mt_needqty").toString())+",");
			sb.append("'"+map.get("mt_tasker")+"',"+date+",'"+name+"','"+map.get("rp_purpose")+"')");
			baseDao.execute(sb.toString());
		}
	}

}
