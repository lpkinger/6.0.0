package com.uas.erp.dao.common.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.CalendarDao;
import com.uas.erp.model.Agenda;

@Repository("calendarDao")
public class CalendarDaoImpl extends BaseDao implements CalendarDao  {
  private  final String insertSql="insert into Calendar(ca_EventId,ca_CalendarId,ca_Title,ca_StartDate,ca_EndDate,ca_IsAllDay,ca_IsNew,ca_WeekEnds,ca_IsContainWeekends,ca_weekIndex,ca_renderAsAllDay)" +
   		"values(?,?,?,?,?,?,?,?,?,?.?)";
   private final String UpdateSql="update Calendar set ca_CalendarId=?,ca_Title=?,ca_StartDate=?,ca_EndDate=?,ca_IsAllDay=?,ca_IsNew?,ca_WeekEnds?,ca_IsContainWeekends=?,ca_weekIndex=?,ca_renderAsAllDay=?  where ca_eventid=?";
	@Override
	/*@Transactional(propagation=Propagation.REQUIRED)*/
	public void save(String addData,String keyField) {
		List<Map<Object,Object>> maps=BaseUtil.parseGridStoreToMaps(addData);    
		int EventId=0;
		keyField="";
		for(Map<Object,Object> map:maps ){
			if(checkTimeInterval(map.get("StartDate"),map.get("EndDate")))
				BaseUtil.showError("该设置时间区间包含与其他设置有冲突不能保存!");
			EventId=getSeqId("CALENDAR_SEQ");
			keyField+=EventId+",";
			String Sql=" insert into Calendar(ca_EventId,ca_CalendarId,ca_Title,ca_StartDate,ca_EndDate,ca_IsAllDay,ca_IsNew,ca_WeekEnds,ca_IsContainWeekends,ca_weekIndex,ca_renderAsAllDay,ca_shift) values('"+EventId+"','"+map.get("CalendarId")+"','"
		    +map.get("Title")+"','"+String.valueOf(map.get("StartDate")).replaceAll("T"," ")+"','"+String.valueOf(map.get("EndDate")).replaceAll("T"," ")+"','"+map.get("IsAllDay")+"','"+ map.get("IsNew")+"','"+map.get("WeekEnds")+"','"+map.get("IsContainWeekends")+"','"+map.get("_weekIndex")+"','"+map.get("_renderAsAllDay")+"','"+map.get("Shift")+"')";
		   execute(Sql);
		}
		if(keyField.length()>0){
			keyField=keyField.substring(0,keyField.lastIndexOf(","));
			String res=callProcedure("SP_UPDATECALENDARDET", new Object[]{keyField});
			if(res!=null) BaseUtil.showError(res);
		}
	}
	@Override
	public void update(String updateData, String keyField) {
		List<Map<Object,Object>> maps=BaseUtil.parseGridStoreToMaps(updateData);    
		keyField="";
		for(Map<Object,Object> map:maps ){
			if(checkTimeInterval(map.get("StartDate"),map.get("EndDate")))
				BaseUtil.showError("该设置时间区间包含与其他设置有冲突不能保存!");
			keyField+=map.get("EventId")+",";
			String Sql=" update Calendar set ca_CalendarId='"+map.get("CalendarId")+"',ca_Title='"+map.get("Title")+"',ca_StartDate='"+String.valueOf(map.get("StartDate")).replaceAll("T"," ")+"',ca_EndDate='"+
			String.valueOf(map.get("EndDate")).replaceAll("T"," ")+"',ca_IsAllDay='"+map.get("IsAllDay")+"',ca_IsNew='"+map.get("IsNew")+"',ca_WeekEnds='"+map.get("WeekEnds")+"',ca_IsContainWeekends='"+map.get("IsContainWeekends")+"',ca_weekIndex='"+map.get("_weekIndex")+"',ca_renderAsAllDay='"+map.get("_renderAsAllDay")+"',ca_shift='"+map.get("Shift")+"'  where ca_eventid='"+map.get("EventId")+"'";
			execute(Sql);
		}    
		if(keyField.length()>0){
			keyField=keyField.substring(0,keyField.lastIndexOf(","));
			String res=callProcedure("SP_UPDATECALENDARDET", new Object[]{keyField});
			if(res!=null) BaseUtil.showError(res);
		}

	}
	@Override
	public void delete(String deleteData, String keyField) {
		List<Map<Object,Object>> maps=BaseUtil.parseGridStoreToMaps(deleteData);
		keyField="";
		for(Map<Object,Object> map:maps ){
			keyField+=map.get("EventId")+",";
		}
		if(keyField.length()>0){
			keyField=keyField.substring(0,keyField.lastIndexOf(","));
			execute("delete calendar where ca_eventid in ("+keyField+")");		  
			execute("delete calendardet where cad_eventid in ("+keyField+")");	
		}
	}
	@Override
	public String getMyData(String emcode,String condition) {
		String sql="select AG_ID,AG_TITLE,AG_START,AG_END from AGENDA where AG_EXECUTOR_ID like '"+emcode+"#%' or AG_EXECUTOR_ID like '%#"+emcode+"%' or AG_EXECUTOR_ID like '%#"+emcode+"#%' or AG_EXECUTOR_ID ='"+emcode+"'";
		if(condition!=null) sql+=" and "+condition; 
		SqlRowList rs =queryForRowSet(sql);
		JSONArray arr=new JSONArray();		
		while(rs.next()){
			JSONObject jo=	new JSONObject();
			jo.put("ca_eventid", rs.getInt("AG_ID"));
			jo.put("ca_calendarid",2);
			jo.put("ca_title", rs.getString("AG_TITLE"));
			jo.put("ca_startdate", rs.getString("AG_START"));
			jo.put("ca_enddate", rs.getString("AG_END"));
			jo.put("ca_isallday", "true");
			jo.put("ca_iscontainweekends", "false");
			arr.add(jo);
		}
		String sql1="Select id,name,description,type,startdate,enddate FROM projecttask left join Resourceassignment on id=ra_taskid WHERE RA_EMID='"+emcode+"' and class in ('projecttask','agendatask','researchtask')";
		if(condition!=null) sql1+=" and "+condition; 
		SqlRowList rs1 =queryForRowSet(sql1);	
		while(rs1.next()){
			JSONObject jo=	new JSONObject();
			jo.put("ca_eventid", rs1.getInt("id"));
			jo.put("ca_calendarid",2);
			jo.put("ca_title", rs1.getString("name"));
			jo.put("ca_startdate", rs1.getString("startdate"));
			jo.put("ca_enddate", rs1.getString("enddate"));
			jo.put("ca_isallday", "true");
			jo.put("ca_iscontainweekends", "false");
			arr.add(jo);
		}
		return arr.toString();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public String getMyAgenda(String emid) {
		List<Agenda>  list = getJdbcTemplate("Agenda").
				query("select * from agenda where ag_arrange_id = ?", 
						new BeanPropertyRowMapper(Agenda.class),emid);
		JSONArray arr=new JSONArray();
		if(list != null){
			for (Agenda ag : list) {
				JSONObject jo=	new JSONObject();
				jo.put("ca_eventid", ag.getAg_id());
				jo.put("ca_calendarid",1);
				jo.put("ca_title", ag.getAg_title());
				jo.put("ca_startdate",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format( ag.getAg_start()).replace(" ", "T"));
				jo.put("ca_enddate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format( ag.getAg_end()).replace(" ", "T"));
				jo.put("ca_isallday", "false");
				jo.put("ca_iscontainweekends", "true");
				arr.add(jo);			
			}
		}
		return arr.toString();
	}
	//判断时间区间是否已经设置
	private  boolean checkTimeInterval(Object startdate,Object enddate){
	    startdate=startdate.toString().substring(0, 10);
	    enddate=enddate.toString().substring(0, 10);
		return  checkIf("calendar", "('"+startdate +"'<= substr(ca_startdate,0,10) and '"+enddate +"'>=substr(ca_startdate,0,10)) or ('"
				+startdate +"'>= substr(ca_startdate,0,10) and '"+startdate +"'<=substr(ca_enddate,0,10)) or ('"+
				startdate +"'>= substr(ca_startdate,0,10) and '"+enddate +"'<=substr(ca_enddate,0,10)) or ('"+
				startdate +"'<= substr(ca_startdate,0,10) and '"+enddate +"'>=substr(ca_enddate,0,10))");
	}
	
	
}
