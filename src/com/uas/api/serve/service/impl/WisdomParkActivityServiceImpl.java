package com.uas.api.serve.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.api.serve.service.WisdomParkActivityService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;

@Service
public class WisdomParkActivityServiceImpl extends ServeCommon implements WisdomParkActivityService{
	
	@Autowired 
	BaseDao baseDao;
	
	@Override
	public List<Map<String, Object>> getActivityType(String basePath) {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		SqlRowList rs = baseDao.queryForRowSet("select at_id,at_name,at_desc,at_count,at_icon from ActivityType");
		while (rs.next()) {
			Map<String, Object> activity = new HashMap<String, Object>();
			activity.put("at_id", rs.getGeneralInt("at_id"));
			activity.put("at_name", rs.getGeneralString("at_name"));
			activity.put("at_desc", rs.getGeneralString("at_desc"));
			activity.put("at_count", rs.getGeneralLong("at_count"));
			
			list.add(activity);
		}
		return list;
	}


	@Override
	public List<Map<String, Object>> getActivitylist(String basePath, String type, Integer limit, Integer page) {

		String sql = "select * from (select T.*,rownum rn from (select ac_id,ac_title,ac_desc,ac_author,ac_type,ac_icon,ac_enternum,ac_startdate,ac_status from ActivityCenter where nvl(ac_status,'草稿箱') in ('进行中','已结束')" + (StringUtil.hasText(type)?" and ac_type = '"+type+"'":"")+" order by ac_publishdate desc) T) where rn <=? and rn >?";
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		int start = limit*(page-1),end = limit*page;
		SqlRowList rs = baseDao.queryForRowSet(sql, end, start);
		while (rs.next()) {
			Map<String, Object> activity = new HashMap<String, Object>();
			activity.put("ac_id", rs.getGeneralInt("ac_id"));
			activity.put("ac_title", rs.getGeneralString("ac_title"));
			activity.put("ac_desc", rs.getGeneralString("ac_desc"));
			activity.put("ac_author", rs.getGeneralString("ac_author"));
			activity.put("ac_type", rs.getGeneralString("ac_type"));
			activity.put("ac_icon", getLogoUrl(basePath, rs.getGeneralString("ac_icon")));
			activity.put("ac_enternum", rs.getGeneralInt("ac_enternum"));
			activity.put("ac_addr", rs.getGeneralString("ac_addr"));
			//activity.put("ac_publishdate", rs.getDate("ac_publishdate"));
			activity.put("ac_startdate", rs.getDate("ac_startdate"));
			activity.put("ac_status", rs.getGeneralString("ac_status"));
			list.add(activity);
		}
		return list;
	}


	@Override
	public Map<String, Object> getActivityContent(String basePath, Integer id, Long uu) {
		Map<String, Object> activity = new HashMap<String, Object>();
		SqlRowList rs = baseDao.queryForRowSet("select ac_id,ac_title,ac_desc,ac_image,ac_type,ac_startdate,ac_enddate,ac_addr,ac_maxenternum,ac_enternum,replace(ac_content,'\n','</br>') ac_content,replace(ac_notice,'\n','</br>') ac_notice,replace(ac_costinfo,'\n','</br>') ac_costinfo,ac_author,ac_status from ActivityCenter where ac_id = ?", id);
		if (rs.next()) {
			activity.put("ac_id", rs.getGeneralInt("ac_id"));
			activity.put("ac_title", rs.getGeneralString("ac_title"));
			activity.put("ac_desc", rs.getGeneralString("ac_desc"));
			activity.put("ac_image", getLogoUrl(basePath, rs.getGeneralString("ac_image")));
			activity.put("ac_type", rs.getGeneralString("ac_type"));
			activity.put("ac_startdate", rs.getDate("ac_startdate"));
			activity.put("ac_enddate", rs.getDate("ac_enddate"));
			activity.put("ac_addr", rs.getGeneralString("ac_addr"));
			activity.put("ac_maxenternum", rs.getObject("ac_maxenternum"));
			activity.put("ac_enternum", rs.getGeneralInt("ac_enternum"));
			activity.put("ac_content", rs.getGeneralString("ac_content"));
			activity.put("ac_notice", rs.getGeneralString("ac_notice"));
			activity.put("ac_costinfo", rs.getGeneralString("ac_costinfo"));
			activity.put("ac_author", rs.getGeneralString("ac_author"));
			activity.put("ac_status", rs.getGeneralString("ac_status"));
		}
		if (uu!=null) {
			activity.put("hasRegistrat", baseDao.checkIf("ActivityRegistration", "ag_acid = " + id +" and ag_uu = " + uu));
		}else{
			activity.put("hasRegistrat",false);
		}
		return activity;
	}


	@Override
	public Integer getActivityTotal(String type) {
		return baseDao.getCountByCondition("ActivityCenter", "nvl(ac_status,'草稿箱') in ('进行中','已结束')" + (StringUtil.hasText(type)?" and ac_type = '"+type+"'":""));
	}


	@Override
	public String ActivityRegistration(Integer id, Long uu, String name) {
		String msg = "报名成功！";
		boolean bool = baseDao.checkIf("ActivityRegistration", "ag_acid = " + id +" and ag_uu = " + uu);
		if (bool) {
			return "您已经报名，请不要重复报名!";
		}
		bool = baseDao.checkIf("ActivityCenter", "ac_id = " + id +" and nvl(ac_enternum,0)+1 > ac_maxenternum and nvl(ac_maxenternum,0)>0");
		if (bool) {
			return "对不起，该活动报名人数已经满了!";
		}
		bool = baseDao.checkIf("ActivityCenter", "ac_id = " + id +" and ac_startdate < sysdate");
		if (bool) {
			return "对不起，该活动已经结束!";
		}
		try {
			int keyValue = baseDao.getSeqId("ACTIVITYREGISTRATION_SEQ");
			
			baseDao.execute("insert into ActivityRegistration(ag_id,ag_acid,ag_uu,ag_name) values(" + keyValue + "," + id + "," + uu + ",'" + name + "')");
			
			baseDao.updateByCondition("ActivityCenter", "ac_enternum = nvl(ac_enternum,0)+1", "ac_id = "+id);
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError(e.getMessage());
		}
		
		return msg;
	}

}
