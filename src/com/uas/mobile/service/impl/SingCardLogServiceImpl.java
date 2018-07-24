package com.uas.mobile.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date; 
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.model.Employee;
import com.uas.mobile.service.SignCardLogService;

@Service("SignCardLogService")
public class SingCardLogServiceImpl implements SignCardLogService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private EmployeeDao employeeDao;

	/*
	 * 获取员工上下班时间(ver1.1)
	 */
	@Override
	public Map<String, Object> getDutyTime(String emcode, String date) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> mapClassOne = new HashMap<String, Object>();
		Map<String, Object> mapClassTwo = new HashMap<String, Object>();
		Map<String, Object> mapClassThree = new HashMap<String, Object>();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		int time=Integer.parseInt(date);
		Date now = new Date();
		int systime = Integer.parseInt(sdf.format(now));
		Object[] objs = baseDao.getFieldsDataByCondition(
				"(empworkdate left join workdate on ew_wdcode=wd_code)",
				new String[] { "wd_ondutyone","wd_degree", "wd_offdutyone",
						"wd_ondutytwo", "wd_offdutytwo", "wd_ondutythree",
						"wd_offdutythree", "wd_onbeg1", "wd_offend1",
						"wd_onbeg2", "wd_offend2", "wd_onbeg3", "wd_offend3","wd_pcount","wd_day","wd_earlytime","wd_id","wd_name","wd_code","wd_autooffdutyone","wd_autoondutytwo"},
				"ew_emcode='" + emcode + "' and ew_date=to_date('" + date
						+ "','yyyymmdd')");
		Object[] comAddress = baseDao.getFieldsDataByCondition(
				"comaddressset left join employee on em_cscode=cs_code",
				"cs_longitude,cs_latitude,cs_validrange,cs_innerdistance,cs_count", "em_code='" + emcode
						+ "'");
		List<Map<String, Object>> comAddresses =getcomaddressset("1=1");
		boolean ifNeedSignCard=ifNeedSignCard(emcode);
		map.put("ifNeedSignCard", ifNeedSignCard);
		// 取员工默认班次
		Object[] defaultClass = baseDao.getFieldsDataByCondition(
				"(workdate left join employee on em_wdcode=wd_code)",
				new String[] { "wd_ondutyone","wd_degree",  "wd_offdutyone",
						"wd_ondutytwo", "wd_offdutytwo", "wd_ondutythree",
						"wd_offdutythree", "wd_onbeg1", "wd_offend1",
						"wd_onbeg2", "wd_offend2", "wd_onbeg3", "wd_offend3" ,"wd_pcount","wd_day","wd_earlytime","wd_id","wd_name","wd_code","wd_autooffdutyone","wd_autoondutytwo" },
				"em_code='" + emcode + "'");
	Object[] workdate_emp=baseDao.getFieldsDataByCondition("(workdate_emp  left join workdate on we_wdcode=wd_code)", 
				new String[] { "wd_ondutyone","wd_degree", "wd_offdutyone",
					"wd_ondutytwo", "wd_offdutytwo", "wd_ondutythree",
					"wd_offdutythree", "wd_onbeg1", "wd_offend1",
					"wd_onbeg2", "wd_offend2", "wd_onbeg3", "wd_offend3","wd_pcount","wd_day","wd_earlytime","wd_id","wd_name","wd_code","wd_autooffdutyone","wd_autoondutytwo" }, 
						"we_days=(select (case when to_char(to_date('"+date+"','yyyymmdd'),'D')=1 then 7 else to_char(to_date('"+date+"','yyyymmdd'),'D')-1 end) from dual) and we_emcode='"+emcode+"'");
	Object[] workdate_hrorg=baseDao.getFieldsDataByCondition("(workdate_hrorg  left join workdate on wh_wdcode=wd_code)", 
			new String[] { "wd_ondutyone","wd_degree", "wd_offdutyone",
				"wd_ondutytwo", "wd_offdutytwo", "wd_ondutythree",
				"wd_offdutythree", "wd_onbeg1", "wd_offend1",
				"wd_onbeg2", "wd_offend2", "wd_onbeg3", "wd_offend3","wd_pcount","wd_day","wd_earlytime","wd_id","wd_name","wd_code" ,"wd_autooffdutyone","wd_autoondutytwo"}, 
					"wh_defaultorname=(select or_name from hrorg left join employee on or_code=em_defaultorcode where em_code='"+emcode+"') and  wh_days=(select (case when to_char(to_date('"+date+"','yyyymmdd'),'D')=1 then 7 else to_char(to_date('"+date+"','yyyymmdd'),'D')-1 end) from dual)");
		if (comAddress != null) {
			map.put("comaddressset", true);
			map.put("longitude", comAddress[0]);
			map.put("latitude", comAddress[1]);
			map.put("distance", comAddress[2]);
			map.put("innerdistance", comAddress[3]);
			map.put("count", comAddress[4]);
		} else {
			map.put("comaddressset", false);
			map.put("longitude", null);
			map.put("latitude", null);
			map.put("distance", null);
			map.put("innerdistance", null);
			map.put("count", null);
		}
		if(time<systime){
			if (objs != null) {
				if (objs[0] != null) {
					boolean gapNotSignCard = false;
					if(objs[19]!=null){
						if("1".equals(objs[19].toString())){
							gapNotSignCard = true;
						}
					}
					if(objs[20]!=null){
						if("1".equals(objs[20].toString())){
							gapNotSignCard = true;
						}
					}
					
					mapClassOne.put("wd_onduty", objs[0]);
					mapClassOne.put("wd_offduty", objs[2]);
					mapClassOne.put("wd_onbeg", objs[7]);
					mapClassOne.put("wd_offend", objs[8]);

					mapClassTwo.put("wd_onduty", objs[3]);
					mapClassTwo.put("wd_offduty", objs[4]);
					mapClassTwo.put("wd_onbeg", objs[9]);
					mapClassTwo.put("wd_offend", objs[10]);
					
					mapClassThree.put("wd_onduty", objs[5]);
					mapClassThree.put("wd_offduty", objs[6]);
					mapClassThree.put("wd_onbeg", objs[11]);
					mapClassThree.put("wd_offend", objs[12]);

					if(gapNotSignCard){
						if(objs[4]!=null){
							mapClassOne.put("wd_offduty", objs[4]);
							mapClassTwo.put("wd_onduty", null);
							mapClassTwo.put("wd_offduty", null);
							mapClassTwo.put("wd_onbeg", null);
							mapClassTwo.put("wd_offend",null);
						}
						if(objs[10]!=null){
							mapClassOne.put("wd_offend", objs[10]);
						}
					}
					
					map.put("success", true);
					map.put("wd_degree", objs[1]);
					map.put("Class1", mapClassOne);
					map.put("Class2", mapClassTwo);
					map.put("Class3", mapClassThree);
					map.put("wd_pcount", objs[13]);
					map.put("wd_day", objs[14]);
					map.put("wd_earlytime", objs[15]);
					map.put("wd_id", objs[16]);
					map.put("wd_name", objs[17]);
					map.put("wd_code", objs[18]);
					map.put("ifDefaultClass", false);
				} else {
					map.put("success", false);
				}
			}else if (defaultClass != null) {
				boolean gapNotSignCard = false;
				if(defaultClass[19]!=null){
					if("1".equals(defaultClass[19].toString())){
						gapNotSignCard = true;
					}
				}
				if(defaultClass[20]!=null){
					if("1".equals(defaultClass[20].toString())){
						gapNotSignCard = true;
					}
				}
				
				mapClassOne.put("wd_onduty", defaultClass[0]);
				mapClassOne.put("wd_offduty", defaultClass[2]);
				mapClassOne.put("wd_onbeg", defaultClass[7]);
				mapClassOne.put("wd_offend", defaultClass[8]);

				mapClassTwo.put("wd_onduty", defaultClass[3]);
				mapClassTwo.put("wd_offduty", defaultClass[4]);
				mapClassTwo.put("wd_onbeg", defaultClass[9]);
				mapClassTwo.put("wd_offend", defaultClass[10]);

				mapClassThree.put("wd_onduty", defaultClass[5]);
				mapClassThree.put("wd_offduty", defaultClass[6]);
				mapClassThree.put("wd_onbeg", defaultClass[11]);
				mapClassThree.put("wd_offend", defaultClass[12]);
				
				if(gapNotSignCard){ //如何勾选自动打上下班卡，则只取前两个或前一个的最早和早晚时间
					if(defaultClass[4]!=null){
						mapClassOne.put("wd_offduty", defaultClass[4]);
						mapClassTwo.put("wd_onduty", null);
						mapClassTwo.put("wd_offduty", null);
						mapClassTwo.put("wd_onbeg", null);
						mapClassTwo.put("wd_offend",null);
					}
					if(defaultClass[10]!=null){
						mapClassOne.put("wd_offend", defaultClass[10]);
					}
				}
				map.put("success", true);
				map.put("wd_degree", defaultClass[1]);
				map.put("Class1", mapClassOne);
				map.put("Class2", mapClassTwo);
				map.put("Class3", mapClassThree);
				map.put("wd_pcount", defaultClass[13]);
				map.put("wd_day", defaultClass[14]);
				map.put("wd_earlytime", defaultClass[15]);
				map.put("wd_id", defaultClass[16]);
				map.put("wd_name", defaultClass[17]);
				map.put("wd_code", defaultClass[18]);
				map.put("ifDefaultClass", true);
			} else // 取系统考勤时间
			{
				Object[] objsys = baseDao.getFieldsDataByCondition("AttendSystem",
						new String[] { "AS_AMSTARTTIME", "AS_PMENDTIME",
								"AS_PMSTARTTIME", "AS_AMENDTIME" }, "1=1");
				if (objsys != null) {
					map.put("success", true);
					map.put("as_amstarttime", objsys[0]);
					map.put("as_pmendtime", objsys[1]);
					map.put("as_pmstarttime", objsys[2]);
					map.put("as_amendtime", objsys[3]);
				} else {
					map.put("success", false);
				}
			}
		}else{
			if (objs != null) {
				if (objs[0] != null) {
					boolean gapNotSignCard = false;
					if(objs[19]!=null){
						if("1".equals(objs[19].toString())){
							gapNotSignCard = true;
						}
					}
					if(objs[20]!=null){
						if("1".equals(objs[20].toString())){
							gapNotSignCard = true;
						}
					}
					
					mapClassOne.put("wd_onduty", objs[0]);
					mapClassOne.put("wd_offduty", objs[2]);
					mapClassOne.put("wd_onbeg", objs[7]);
					mapClassOne.put("wd_offend", objs[8]);

					mapClassTwo.put("wd_onduty", objs[3]);
					mapClassTwo.put("wd_offduty", objs[4]);
					mapClassTwo.put("wd_onbeg", objs[9]);
					mapClassTwo.put("wd_offend", objs[10]);
					
					mapClassThree.put("wd_onduty", objs[5]);
					mapClassThree.put("wd_offduty", objs[6]);
					mapClassThree.put("wd_onbeg", objs[11]);
					mapClassThree.put("wd_offend", objs[12]);

					if(gapNotSignCard){
						if(objs[4]!=null){
							mapClassOne.put("wd_offduty", objs[4]);
							mapClassTwo.put("wd_onduty", null);
							mapClassTwo.put("wd_offduty", null);
							mapClassTwo.put("wd_onbeg", null);
							mapClassTwo.put("wd_offend",null);
						}
						if(objs[10]!=null){
							mapClassOne.put("wd_offend", objs[10]);
						}
					}
					
					map.put("success", true);
					map.put("wd_degree", objs[1]);
					map.put("Class1", mapClassOne);
					map.put("Class2", mapClassTwo);
					map.put("Class3", mapClassThree);
					map.put("wd_pcount", objs[13]);
					map.put("wd_day", objs[14]);
					map.put("wd_earlytime", objs[15]);
					map.put("wd_id", objs[16]);
					map.put("wd_name", objs[17]);
					map.put("wd_code", objs[18]);
					map.put("ifDefaultClass", false);
				} else {
					map.put("success", false);
				}
			} else if(workdate_emp!=null){
				if (workdate_emp[0] != null) {
					boolean gapNotSignCard = false;
					if(workdate_emp[19]!=null){
						if("1".equals(workdate_emp[19].toString())){
							gapNotSignCard = true;
						}
					}
					if(workdate_emp[20]!=null){
						if("1".equals(workdate_emp[20].toString())){
							gapNotSignCard = true;
						}
					}
					
					mapClassOne.put("wd_onduty", workdate_emp[0]);
					mapClassOne.put("wd_offduty", workdate_emp[2]);
					mapClassOne.put("wd_onbeg", workdate_emp[7]);
					mapClassOne.put("wd_offend", workdate_emp[8]);

					mapClassTwo.put("wd_onduty", workdate_emp[3]);
					mapClassTwo.put("wd_offduty", workdate_emp[4]);
					mapClassTwo.put("wd_onbeg", workdate_emp[9]);
					mapClassTwo.put("wd_offend", workdate_emp[10]);
					
					mapClassThree.put("wd_onduty", workdate_emp[5]);
					mapClassThree.put("wd_offduty", workdate_emp[6]);
					mapClassThree.put("wd_onbeg", workdate_emp[11]);
					mapClassThree.put("wd_offend", workdate_emp[12]);

					if(gapNotSignCard){ //如何勾选自动打上下班卡，则只取前两个或前一个的最早和早晚时间
						if(workdate_emp[4]!=null){
							mapClassOne.put("wd_offduty", workdate_emp[4]);
							mapClassTwo.put("wd_onduty", null);
							mapClassTwo.put("wd_offduty", null);
							mapClassTwo.put("wd_onbeg", null);
							mapClassTwo.put("wd_offend",null);
						}
						if(workdate_emp[10]!=null){
							mapClassOne.put("wd_offend", workdate_emp[10]);
						}
					}
					
					map.put("workdate_emp_success", true);
					map.put("wd_degree", workdate_emp[1]);
					map.put("Class1", mapClassOne);
					map.put("Class2", mapClassTwo);
					map.put("Class3", mapClassThree);
					map.put("wd_pcount", workdate_emp[13]);
					map.put("wd_day", workdate_emp[14]);
					map.put("wd_earlytime", workdate_emp[15]);
					map.put("wd_id", workdate_emp[16]);
					map.put("wd_name", workdate_emp[17]);
					map.put("wd_code", workdate_emp[18]);
					map.put("ifDefaultClass", false);
				} else {
					map.put("workdate_emp_success", false);
				}
			}else if(workdate_hrorg!=null){
				if (workdate_hrorg[0] != null) {
					boolean gapNotSignCard = false;
					if(workdate_hrorg[19]!=null){
						if("1".equals(workdate_hrorg[19].toString())){
							gapNotSignCard = true;
						}
					}
					if(workdate_hrorg[20]!=null){
						if("1".equals(workdate_hrorg[20].toString())){
							gapNotSignCard = true;
						}
					}
					
					mapClassOne.put("wd_onduty", workdate_hrorg[0]);
					mapClassOne.put("wd_offduty", workdate_hrorg[2]);
					mapClassOne.put("wd_onbeg", workdate_hrorg[7]);
					mapClassOne.put("wd_offend", workdate_hrorg[8]);

					mapClassTwo.put("wd_onduty", workdate_hrorg[3]);
					mapClassTwo.put("wd_offduty", workdate_hrorg[4]);
					mapClassTwo.put("wd_onbeg", workdate_hrorg[9]);
					mapClassTwo.put("wd_offend", workdate_hrorg[10]);
					
					mapClassThree.put("wd_onduty", workdate_hrorg[5]);
					mapClassThree.put("wd_offduty", workdate_hrorg[6]);
					mapClassThree.put("wd_onbeg", workdate_hrorg[11]);
					mapClassThree.put("wd_offend", workdate_hrorg[12]);
					
					if(gapNotSignCard){ //如何勾选自动打上下班卡，则只取前两个或前一个的最早和早晚时间
						if(workdate_hrorg[4]!=null){
							mapClassOne.put("wd_offduty", workdate_hrorg[4]);
							mapClassTwo.put("wd_onduty", null);
							mapClassTwo.put("wd_offduty", null);
							mapClassTwo.put("wd_onbeg", null);
							mapClassTwo.put("wd_offend",null);
						}
						if(workdate_hrorg[10]!=null){
							mapClassOne.put("wd_offend", workdate_hrorg[10]);
						}
					}
					
					map.put("workdate_hrorg_success", true);
					map.put("wd_degree", workdate_hrorg[1]);
					map.put("Class1", mapClassOne);
					map.put("Class2", mapClassTwo);
					map.put("Class3", mapClassThree);
					map.put("wd_pcount", workdate_hrorg[13]);
					map.put("wd_day", workdate_hrorg[14]);
					map.put("wd_earlytime", workdate_hrorg[15]);
					map.put("wd_id", workdate_hrorg[16]);
					map.put("wd_name", workdate_hrorg[17]);
					map.put("wd_code", workdate_hrorg[18]);
					map.put("ifDefaultClass", false);
				} else {
					map.put("workdate_hrorg_success", false);
				}		
			}else if (defaultClass != null) {
				boolean gapNotSignCard = false;
				if(defaultClass[19]!=null){
					if("1".equals(defaultClass[19].toString())){
						gapNotSignCard = true;
					}
				}
				if(defaultClass[20]!=null){
					if("1".equals(defaultClass[20].toString())){
						gapNotSignCard = true;
					}
				}
				
				mapClassOne.put("wd_onduty", defaultClass[0]);
				mapClassOne.put("wd_offduty", defaultClass[2]);
				mapClassOne.put("wd_onbeg", defaultClass[7]);
				mapClassOne.put("wd_offend", defaultClass[8]);

				mapClassTwo.put("wd_onduty", defaultClass[3]);
				mapClassTwo.put("wd_offduty", defaultClass[4]);
				mapClassTwo.put("wd_onbeg", defaultClass[9]);
				mapClassTwo.put("wd_offend", defaultClass[10]);

				mapClassThree.put("wd_onduty", defaultClass[5]);
				mapClassThree.put("wd_offduty", defaultClass[6]);
				mapClassThree.put("wd_onbeg", defaultClass[11]);
				mapClassThree.put("wd_offend", defaultClass[12]);
				
				if(gapNotSignCard){ //如何勾选自动打上下班卡，则只取前两个或前一个的最早和早晚时间
					if(defaultClass[4]!=null){
						mapClassOne.put("wd_offduty", defaultClass[4]);
						mapClassTwo.put("wd_onduty", null);
						mapClassTwo.put("wd_offduty", null);
						mapClassTwo.put("wd_onbeg", null);
						mapClassTwo.put("wd_offend",null);
					}
					if(defaultClass[10]!=null){
						mapClassOne.put("wd_offend", defaultClass[10]);
					}
				}
				map.put("success", true);
				map.put("wd_degree", defaultClass[1]);
				map.put("Class1", mapClassOne);
				map.put("Class2", mapClassTwo);
				map.put("Class3", mapClassThree);
				map.put("wd_pcount", defaultClass[13]);
				map.put("wd_day", defaultClass[14]);
				map.put("wd_earlytime", defaultClass[15]);
				map.put("wd_id", defaultClass[16]);
				map.put("wd_name", defaultClass[17]);
				map.put("wd_code", defaultClass[18]);
				map.put("ifDefaultClass", true);
			} else // 取系统考勤时间
			{
				Object[] objsys = baseDao.getFieldsDataByCondition("AttendSystem",
						new String[] { "AS_AMSTARTTIME", "AS_PMENDTIME",
								"AS_PMSTARTTIME", "AS_AMENDTIME" }, "1=1");
				if (objsys != null) {
					map.put("success", true);
					map.put("as_amstarttime", objsys[0]);
					map.put("as_pmendtime", objsys[1]);
					map.put("as_pmstarttime", objsys[2]);
					map.put("as_amendtime", objsys[3]);
				} else {
					map.put("success", false);
				}
			}				
		}
		map.put("comAddressdata", comAddresses);
		return map;
	}
	@Override
	public List<Map<String, Object>> getAllWorkDate(String emcode) {
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();		
		Map<String, Object> map = null;
		Map<String, Object> map1 = null;
		//Map<String, Object> map2 = null;
		List<Object[]> objs = baseDao.getFieldsDatasByCondition(
				"workdate",
				new String[] { "wd_degree", "wd_ondutyone", "wd_offdutyone",
						"wd_ondutytwo", "wd_offdutytwo", "wd_ondutythree",
						"wd_offdutythree", "wd_onbeg1", "wd_offend1",
						"wd_onbeg2", "wd_offend2", "wd_onbeg3", "wd_offend3","wd_pcount","wd_day","wd_earlytime","wd_id","wd_name","wd_code"},
				"wd_recorddate>=trunc(SYSDATE, 'MM') and to_char(wd_recorddate,'dd')<=to_char(last_day(SYSDATE),'dd') and wd_name <> '默认班次' order by wd_recorddate asc");	
		// 取员工默认班次
				Object[] defaultClass = baseDao.getFieldsDataByCondition(
						"(workdate left join employee on em_wdcode=wd_code)",
						new String[] { "wd_ondutyone","wd_degree",  "wd_offdutyone",
								"wd_ondutytwo", "wd_offdutytwo", "wd_ondutythree",
								"wd_offdutythree", "wd_onbeg1", "wd_offend1",
								"wd_onbeg2", "wd_offend2", "wd_onbeg3", "wd_offend3" ,"wd_pcount","wd_day","wd_earlytime","wd_id","wd_name","wd_code" },
						"em_code='" + emcode + "'");
				if (defaultClass != null) {
					List<Object> defaultmans =baseDao.getFieldDatasByCondition("employee", "em_code", "em_code not in (select ew_wdcode from empworkdate where ew_date=to_date(to_char(sysdate,'yyyymmdd'),'yyyymmdd'))");
					List<Object> defaultmansname =baseDao.getFieldDatasByCondition("employee", "em_name", "em_code not in (select ew_wdcode from empworkdate where ew_date=to_date(to_char(sysdate,'yyyymmdd'),'yyyymmdd'))");
					//List<Object> hrorgcodes =baseDao.getFieldDatasByCondition("hrorg left join employee on or_code=EM_DEFAULTORCODE", "or_code", "em_code in (select em_code from employee where em_code not in (select ew_wdcode from empworkdate where ew_date=to_date(to_char(sysdate,'yyyymmdd'),'yyyymmdd') ))GROUP BY or_code having count(*)>=1 order by or_code");
					//List<Object> hrorgnames =baseDao.getFieldDatasByCondition("hrorg left join employee on or_code=EM_DEFAULTORCODE", "or_name", "em_code in (select em_code from employee where em_code not in (select ew_wdcode from empworkdate where ew_date=to_date(to_char(sysdate,'yyyymmdd'),'yyyymmdd') ))GROUP BY or_name having count(*)>=1 order by or_name");
					String defaultmancode="";
					String defaultmanname="";
					/*String hrorgcode="";
					String hrorgname="";*/
					defaultmans.removeAll(Collections.singleton(null));
					defaultmansname.removeAll(Collections.singleton(null));
					if(!(defaultmans.isEmpty())){
						for(int i=0;i<defaultmans.size();i++){							
							 defaultmancode+=defaultmans.get(i)+",";							
						}
					 defaultmancode=defaultmancode.substring(0,defaultmancode.length()-1);
					}
					if(!(defaultmansname.isEmpty())){
						for(int j=0;j<defaultmansname.size();j++){
							 defaultmanname+=defaultmansname.get(j)+",";
						}	
						defaultmanname=defaultmanname.substring(0,defaultmanname.length()-1);
					}
					/*if(!(hrorgcodes.isEmpty())){
						for(int k=0;k<hrorgcodes.size()-1;k++){
							hrorgcode+=hrorgcodes.get(k)+",";
						}
						hrorgcode=hrorgcode.substring(0,hrorgcode.length()-1);
					}
					if(!(hrorgnames.isEmpty())){
						for(int l=0;l<hrorgnames.size()-1;l++){
							hrorgname+=hrorgnames.get(l)+",";
						}
						hrorgname=hrorgname.substring(0,hrorgname.length()-1);
					}*/
					map1=new HashMap<String, Object>();
					map1.put("success", true);
					/*map1.put("hrorgcode", hrorgcode);
					map1.put("hrorgname", hrorgname);*/
					map1.put("emdefaultorcodes",null);
					map1.put("emdefaultors",null);
					map1.put("emnames", defaultmanname);
					map1.put("emcodes",defaultmancode);
					map1.put("wd_degree", defaultClass[1]);
					map1.put("wd_ondutyone", defaultClass[0]);
					map1.put("wd_offdutyone", defaultClass[2]);
					//map1.put("wd_onbeg1", defaultClass[7]);
					//map1.put("wd_offend1", defaultClass[8]);
					map1.put("wd_ondutytwo", defaultClass[3]);
					map1.put("wd_offdutytwo", defaultClass[4]);
					//map1.put("wd_onbeg2", defaultClass[9]);
					//map1.put("wd_offend2", defaultClass[10]);
					map1.put("wd_ondutythree", defaultClass[5]);
					map1.put("wd_offdutythree", defaultClass[6]);
					//map1.put("wd_onbeg3", defaultClass[11]);
					//map1.put("wd_offend3", defaultClass[12]);
					map1.put("wd_pcount", defaultClass[13]);
					map1.put("wd_day", defaultClass[14]);
					map1.put("wd_earlytime", defaultClass[15]);
					map1.put("wd_id", defaultClass[16]);
					map1.put("wd_name", defaultClass[17]);
					map1.put("wd_code", defaultClass[18]);
					map1.put("ifDefaultClass", true);
					lists.add(map1);
				}				
		for (Object[] obj : objs) {
			map = new HashMap<String, Object>();
			if (objs != null) {
				List<Object> em_name=baseDao.getFieldDatasByCondition("workdate_emp left join empworkdate on we_wdcode=ew_wdcode", "we_emname", "we_wdcode='"+obj[18]+"' GROUP BY we_emname having count(*)>=1 ");
				List<Object> em_code=baseDao.getFieldDatasByCondition("workdate_emp left join empworkdate on we_wdcode=ew_wdcode", "we_emcode", "we_wdcode='"+obj[18]+"' GROUP BY we_emcode having count(*)>=1 ");
				List<Object> em_defaultor=baseDao.getFieldDatasByCondition("workdate_hrorg left join empworkdate on wh_wdcode=ew_wdcode", "WH_DEFAULTORNAME", "wh_wdcode='"+obj[18]+"' GROUP BY WH_DEFAULTORNAME having count(*)>=1 ");
				List<Object> em_defaultorcode=baseDao.getFieldDatasByCondition("workdate_hrorg left join empworkdate on wh_wdcode=ew_wdcode", "WH_DEFAULTCODE", "wh_wdcode='"+obj[18]+"' GROUP BY WH_DEFAULTCODE having count(*)>=1");
				String emnames="";
				String emcodes="";
				String emdefaultors="";
				String emdefaultorcodes="";
				em_name.removeAll(Collections.singleton(null));
				em_code.removeAll(Collections.singleton(null));
				em_defaultor.removeAll(Collections.singleton(null));
				em_defaultorcode.removeAll(Collections.singleton(null));
				if(!(em_name.isEmpty())){
					for(int i=0;i<em_name.size();i++){							
						emnames+=em_name.get(i)+",";							
					}
					emnames=emnames.substring(0,emnames.length()-1);
				}
				if(!(em_code.isEmpty())){
					for(int i=0;i<em_code.size();i++){							
						emcodes+=em_code.get(i)+",";							
					}
					emcodes=emcodes.substring(0,emcodes.length()-1);
				}
				if(!(em_defaultor.isEmpty())){
					for(int i=0;i<em_defaultor.size();i++){							
						emdefaultors+=em_defaultor.get(i)+",";							
					}
					emdefaultors=emdefaultors.substring(0,emdefaultors.length()-1);
				}
				if(!(em_defaultorcode.isEmpty())){
					for(int i=0;i<em_defaultorcode.size();i++){							
						emdefaultorcodes+=em_defaultorcode.get(i)+",";							
					}
					emdefaultorcodes=emdefaultorcodes.substring(0,emdefaultorcodes.length()-1);
				}
				//Object[] a=baseDao.getFieldsDataByCondition("workdate_emp left join empworkdate on we_wdcode=ew_wdcode","we_emcode,we_emname", "we_wdcode='"+obj[18]+"'");
				//Object[] b=baseDao.getFieldsDataByCondition("workdate_hrorg left join empworkdate on wh_wdcode=ew_wdcode","WH_DEFAULTCODE,WH_DEFAULTORNAME", "wh_wdcode='"+obj[18]+"'");
				
				map.put("emcodes",emcodes);
				map.put("emnames", emnames);								
				map.put("emdefaultorcodes",emdefaultorcodes);
				map.put("emdefaultors",emdefaultors);				
				map.put("success", true);
				map.put("id", obj[16]);
				map.put("wd_degree", obj[0]);
				map.put("wd_pcount", obj[13]);
				map.put("wd_day", obj[14]);
				map.put("wd_earlytime", obj[15]);			
				map.put("wd_name", obj[17]);
				map.put("wd_code", obj[18]);
				map.put("wd_ondutyone", obj[1]);
				map.put("wd_offdutyone", obj[2]);
				map.put("wd_ondutytwo", obj[3]);
				map.put("wd_offdutytwo", obj[4]);
				map.put("wd_ondutythree", obj[5]);
				map.put("wd_offdutythree", obj[6]);	
			}else {
				map.put("success", false);
			}
			lists.add(map);
		}
		return lists;
	}
	/**
	 * 保存班次
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Map<String, Object> saveWorkDateTime(String caller, String formStore) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<String> sqls = new ArrayList<String>();
		String code = baseDao.sGetMaxNumber("WORKDATE", 2);
		if(store.get("wd_code")==null||"".equals(store.get("wd_code").toString())){
			store.put("wd_code", code);
		}
		if(store.get("wd_defaultorcode")!=null){
			if(store.get("wd_defaultorcode").toString().length()>0){
		 		if(!("".equals(store.get("wd_defaultorcode").toString()))){
					String[] days = ((String) store.get("wd_day")).split(",");
					String[] defaultors=((String) store.get("wd_defaultor")).replace("'","").split(",");
					String[] defaultorscode=((String) store.get("wd_defaultorcode")).replace("'","").split(",");
					if(defaultors.length!=0){
						for(String day:days){
						for(int i=0;i<Math.min(defaultors.length, defaultorscode.length);i++){
							sqls.add("delete from workdate_hrorg where WH_DEFAULTCODE='"+defaultorscode[i]+"' and wh_days='"+day+"'");
						sqls.add("insert into workdate_hrorg(WH_ID,WH_DAYS,WH_WDCODE,WH_DEFAULTORNAME,WH_DEFAULTCODE) values(workdate_hrorg_SEQ.nextval,'"+day+"','"+code+"','"+defaultors[i]+"','"+defaultorscode[i]+"')");										
						}
					}
						/*String insertsql="insert into workdate_hrorg values(workdate_hrorg_SEQ.nextval,"+store.get("wd_defaultorcode").toString().replace("','",",")+",'"+store.get("wd_day")+"',"+store.get("wd_defaultor").toString().replace("','",",")+",'"+code+"')";
						baseDao.execute(insertsql);*/
				/*	sqls.add("delete from empworkdate where ew_emcode in (select em_code from employee where em_defaultorcode in("
							+store.get("wd_defaultorcode")
							+ ")) and ew_date>=to_date(TO_CHAR(SYSDATE, 'YYYYmmdd'),'yyyymmdd')");*/
							String wd_code=(String) store.get("wd_code");
							String[] wddays=((String)store.get("wd_day")).split(",");
							for(String wdday:wddays){						
								int day=Integer.parseInt(wdday);
								/*int count=baseDao.getCountByCondition("workdate_hrorg", "wh_days='"+day+"' and WH_DEFAULTORNAME in("+store.get("wd_defaultor")+")");
								if(count>0){
									BaseUtil.showError("今天部门已有排班，请重新修改日期或更新原来班次");
								}*/
								if(day==7){
									day=0;
								}
						String datesql = "SELECT * FROM (SELECT TRUNC(SYSDATE, 'mm') + ROWNUM - 1 DAYS FROM (SELECT LEVEL FROM DUAL CONNECT BY LEVEL <= TRUNC(LAST_DAY(SYSDATE)) - TRUNC(SYSDATE, 'mm') + 1)) WHERE TO_CHAR(DAYS, 'd') = '"
								+ (day + 1) + "' and days>=to_date(sysdate) ";
								SqlRowSet rs=baseDao.getJdbcTemplate().queryForRowSet(datesql);										
								while(rs.next()){
									String a=rs.getString("DAYS").substring(rs.getString("DAYS").indexOf("2"),rs.getString("DAYS").indexOf(" "));									
									sqls.add("delete from empworkdate where ew_emcode in (select em_code from employee where em_defaultorcode in("
											+store.get("wd_defaultorcode")
											+ ")) and ew_date=to_date('"+a+"','yyyy-mm-dd')");
									sqls.add("insert into empworkdate(ew_id,ew_date,ew_wdcode,ew_emid,ew_emcode) select EMPWORKDATE_SEQ.nextval,to_date('"
									+ a
									+ "','yyyy-mm-dd'),'"
									+ wd_code
									+ "',em_id,em_code from employee where em_code in(select em_code from employee where em_defaultorcode in("
									+store.get("wd_defaultorcode")+"))");
									
																			
								}
							}	
					}else{map.put("defaultorcodes",null);}
				}
		 	}
		}
		 	if(store.get("wd_emcode")!=null){
		 		if(store.get("wd_emcode").toString().length()>0){
			 		if(!("".equals(store.get("wd_emcode").toString()))){
						String[] mans = ((String) store.get("wd_man")).replace("'","").split(",");
						String[] days = ((String) store.get("wd_day")).split(",");
						String[] emcode = ((String) store.get("wd_emcode")).replace("'","").split(",");
						if(mans!=null){
							for(String day:days){
								for(int i=0;i<Math.min(mans.length, emcode.length);i++){
									sqls.add("delete from workdate_emp where We_emname='"+mans[i]+"' and We_DAYS='"+day+"'");
									sqls.add("insert into workdate_emp(We_ID,We_DAYS,We_WDCODE,We_emname,We_emCODE) values(workdate_emp_SEQ.nextval,'"+day+"','"+code+"','"+mans[i]+"','"+emcode[i]+"')");										
									}
								}
							
							/*String insertsql="insert into workdate_emp values(workdate_emp_SEQ.nextval,"+store.get("wd_emcode").toString().replace("','",",")+",'"+store.get("wd_day")+"',"+store.get("wd_man").toString().replace("','",",")+",'"+code+"')";
							baseDao.execute(insertsql);*/
					/*		sqls.add("delete from empworkdate where ew_emcode in ("
								+store.get("wd_emcode")
								+ ")and ew_date>=to_date(TO_CHAR(SYSDATE, 'YYYYmmdd'),'yyyymmdd')");*/
							String wd_code=(String) store.get("wd_code");
							String[] wddays=((String)store.get("wd_day")).split(",");
							for(String wdday:wddays){						
								int day=Integer.parseInt(wdday);
								/*int count=baseDao.getCountByCondition("workdate_emp", "we_days='"+day+"' and We_emNAME in("+store.get("wd_man")+")");
								if(count>0){
									BaseUtil.showError("今天该人员已有排班，请重新修改日期或更新原来班次");
								}*/
								if(day==7){
									day=0;
								}
							String datesql = "SELECT * FROM (SELECT TRUNC(SYSDATE, 'mm') + ROWNUM - 1 DAYS FROM (SELECT LEVEL FROM DUAL CONNECT BY LEVEL <= TRUNC(LAST_DAY(SYSDATE)) - TRUNC(SYSDATE, 'mm') + 1)) WHERE TO_CHAR(DAYS, 'd') = '"
									+ (day + 1) + "' and days>=to_date(sysdate) ";
								SqlRowSet rs=baseDao.getJdbcTemplate().queryForRowSet(datesql);										
								while(rs.next()){
									String a=rs.getString("DAYS").substring(rs.getString("DAYS").indexOf("2"),rs.getString("DAYS").indexOf(" "));
									sqls.add("delete from empworkdate where ew_emcode in ("
											+store.get("wd_emcode")
											+ ")and ew_date=to_date('"+a+"','yyyy-mm-dd')");
									sqls.add("insert into empworkdate(ew_id,ew_date,ew_wdcode,ew_emid,ew_emcode) select EMPWORKDATE_SEQ.nextval,to_date('"
										+ a
										+ "','yyyy-mm-dd'),'"
										+ wd_code
										+ "',em_id,em_code from employee where em_code in("
										+store.get("wd_emcode")+ ")");
								
																			
								}				
							}
						}else{map.put("wd_emcode", null);}
					}
			 	}
		 	}		 
		 baseDao.execute(sqls);
		//handlerService.beforeSave(caller, new Object[] { store });
		int id = baseDao.getSeqId("WORKDATE_SEQ");
		store.remove("wd_emcode");
		store.remove("wd_man");
		store.remove("wd_defaultorcode");
		store.remove("wd_defaultor");
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "WORKDATE",
				new String[] {"wd_id"}, new Object[] {id});
		baseDao.execute(formSql);		
		baseDao.logger.save(caller, "wd_id", store.get("wd_id"));
		handlerService.afterSave(caller, new Object[] { store });
		map.put("id",id);
		return map;
	}
	/**
	 * 更新班次
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Map<String, Object> updateWorkDateTime(String caller, String formStore) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<String> sqls = new ArrayList<String>();
		if(store.get("wd_defaultorcode")!=null){
			if(store.get("wd_defaultorcode").toString().length()>0){
				if(!("".equals(store.get("wd_defaultorcode").toString()))){
					sqls.add("delete from workdate_hrorg where wh_wdcode='"+store.get("wd_code")+"'");
					sqls.add("delete from empworkdate where ew_wdcode='"+store.get("wd_code")+"'");
					String[] days = ((String) store.get("wd_day")).split(",");
					String[] defaultors=((String) store.get("wd_defaultor")).replace("'","").split(",");
					String[] defaultorscode=((String) store.get("wd_defaultorcode")).replace("'","").split(",");
					if(defaultors!=null){
						for(String day:days){
							for(int i=0;i<Math.min(defaultors.length, defaultorscode.length);i++){
							sqls.add("delete from workdate_hrorg where WH_DEFAULTCODE='"+defaultorscode[i]+"' and wh_days='"+day+"'");
							sqls.add("insert into workdate_hrorg(WH_ID,WH_DAYS,WH_WDCODE,WH_DEFAULTORNAME,WH_DEFAULTCODE) values(workdate_hrorg_SEQ.nextval,'"+day+"','"+store.get("wd_code")+"','"+defaultors[i]+"','"+defaultorscode[i]+"')");										
							}
						}
						
						/*sqls.add("delete from empworkdate where ew_emcode in (select em_code from employee where em_defaultorcode in("
								+store.get("wd_defaultorcode")
								+ ")) and ew_date>=to_date(TO_CHAR(SYSDATE, 'YYYYmmdd'),'yyyymmdd')");*/
						String wd_code=(String) store.get("wd_code");
						String[] wddays=((String)store.get("wd_day")).split(",");
						for(String wdday:wddays){						
							int day=Integer.parseInt(wdday);
							/*int count=baseDao.getCountByCondition("workdate_hrorg", "wh_days='"+day+"' and WH_DEFAULTORNAME in("+store.get("wd_defaultor")+")");
							if(count>0){
								BaseUtil.showError("今天部门已有排班，请重新修改日期或更新原来班次");
							}*/
							if(day==7){
								day=0;
							}
							String datesql="SELECT * FROM (SELECT TRUNC(SYSDATE, 'mm') + ROWNUM - 1 DAYS FROM (SELECT LEVEL FROM DUAL CONNECT BY LEVEL <= TRUNC(LAST_DAY(SYSDATE)) - TRUNC(SYSDATE, 'mm') + 1)) WHERE TO_CHAR(DAYS, 'd') = '"+(day+1)+"' and days>=to_date(sysdate) ";
							SqlRowSet rs=baseDao.getJdbcTemplate().queryForRowSet(datesql);										
							while(rs.next()){
								String a=rs.getString("DAYS").substring(rs.getString("DAYS").indexOf("2"),rs.getString("DAYS").indexOf(" "));																	
								sqls.add("delete from empworkdate where ew_emcode in (select em_code from employee where em_defaultorcode in("
										+store.get("wd_defaultorcode")
										+ ")) and ew_date=to_date('"+a+"','yyyy-mm-dd')");
								sqls.add("insert into empworkdate(ew_id,ew_date,ew_wdcode,ew_emid,ew_emcode) select EMPWORKDATE_SEQ.nextval,to_date('"
										+ a
										+ "','yyyy-mm-dd'),'"
										+ wd_code
										+ "',em_id,em_code from employee where em_code in(select em_code from employee where em_defaultorcode in("
										+ store.get("wd_defaultorcode") + "))");
																		
							}				
						}	
					}else{map.put("defaultorcodes",null);}
				}else{
					sqls.add("delete from workdate_hrorg where wh_wdcode='"+store.get("wd_code")+"'");
				}
			}else{
				sqls.add("delete from workdate_hrorg where wh_wdcode='"+store.get("wd_code")+"'");
			}
		}else{
			sqls.add("delete from workdate_hrorg where wh_wdcode='"+store.get("wd_code")+"'");
		}
		if(store.get("wd_emcode")!=null){
			if(store.get("wd_emcode").toString().length()>0){
				if(!("".equals(store.get("wd_emcode").toString()))){
					sqls.add("delete from workdate_emp where we_wdcode='"+store.get("wd_code")+"'");
					sqls.add("delete from  empworkdate where ew_wdcode='"+store.get("wd_code")+"'");
					String[] mans = ((String) store.get("wd_man")).replace("'","").split(",");
					String[] days = ((String) store.get("wd_day")).split(",");
					String wd_code=(String) store.get("wd_code");
					String[] emcode = ((String) store.get("wd_emcode")).replace("'","").split(",");
					if(mans!=null){
						for(String day:days){
							for(int i=0;i<Math.min(mans.length, emcode.length);i++){
								sqls.add("delete from workdate_emp where We_emname='"+mans[i]+"' and We_DAYS='"+day+"'");
								sqls.add("insert into workdate_emp(We_ID,We_DAYS,We_WDCODE,We_emname,We_emCODE) values(workdate_emp_SEQ.nextval,'"+day+"','"+wd_code+"','"+mans[i]+"','"+emcode[i]+"')");										
								}
							}
						
						/*sqls.add("delete from empworkdate where ew_emcode in ("
								+store.get("wd_emcode")
								+ ")and ew_date>=to_date(TO_CHAR(SYSDATE, 'YYYYmmdd'),'yyyymmdd')");*/
						
						String[] wddays=((String)store.get("wd_day")).split(",");
						for(String wdday:wddays){						
							int day=Integer.parseInt(wdday);
							/*int count=baseDao.getCountByCondition("workdate_emp", "we_days='"+day+"' and We_emNAME in("+store.get("wd_man")+")");
							if(count>0){
								BaseUtil.showError("今天该人员已有排班，请重新修改日期或更新原来班次");
							}*/
							if(day==7){
								day=0;
							}
							String datesql = "SELECT * FROM (SELECT TRUNC(SYSDATE, 'mm') + ROWNUM - 1 DAYS FROM (SELECT LEVEL FROM DUAL CONNECT BY LEVEL <= TRUNC(LAST_DAY(SYSDATE)) - TRUNC(SYSDATE, 'mm') + 1)) WHERE TO_CHAR(DAYS, 'd') = '"
									+ (day + 1) + "' and days>=to_date(sysdate) ";
							SqlRowSet rs=baseDao.getJdbcTemplate().queryForRowSet(datesql);										
							while(rs.next()){
								String a=rs.getString("DAYS").substring(rs.getString("DAYS").indexOf("2"),rs.getString("DAYS").indexOf(" "));
								sqls.add("delete from empworkdate where ew_emcode in ("
										+store.get("wd_emcode")
										+ ")and ew_date=to_date('"+a+"','yyyy-mm-dd')");
								sqls.add("insert into empworkdate(ew_id,ew_date,ew_wdcode,ew_emid,ew_emcode) select EMPWORKDATE_SEQ.nextval,to_date('"
										+ a
										+ "','yyyy-mm-dd'),'"
										+ wd_code
										+ "',em_id,em_code from employee where em_code in("
										+store.get("wd_emcode")+ ")");																	
							}									
					}	
					}else{map.put("wd_emcode", null);}
				}else{
					sqls.add("delete from workdate_emp where we_wdcode='"+store.get("wd_code")+"'");
				}	
			}else{
				sqls.add("delete from workdate_emp where we_wdcode='"+store.get("wd_code")+"'");
			}
		}else{
			sqls.add("delete from workdate_emp where we_wdcode='"+store.get("wd_code")+"'");
		}
		if(store.get("wd_emcode")==null&&store.get("wd_defaultorcode")==null){
			if(store.get("wd_emcode").toString().length()==0&&store.get("wd_defaultorcode").toString().length()==0){
				sqls.add("delete from empworkdate where ew_date>=to_date(to_char(sysdate,'yyyymmdd'),'yyyymmdd') and ew_wdcode='"
						+ store.get("wd_code") + "'");
				sqls.add("delete from workdate_emp where we_wdcode='"+store.get("wd_code")+"'");
				sqls.add("delete from workdate_hrorg where wh_wdcode='"+store.get("wd_code")+"'");
			}
		}
		
		//handlerService.beforeSave(caller, new Object[] { store });
		//int id = baseDao.getSeqId("WORKDATE_SEQ");
		baseDao.execute(sqls);
		store.remove("wd_emcode");
		store.remove("wd_man");
		store.remove("wd_defaultorcode");
		store.remove("wd_defaultor");
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "WORKDATE",
				"wd_id");
		baseDao.execute(formSql);		
		baseDao.logger.save(caller, "wd_id", store.get("wd_id"));
		//handlerService.afterSave(caller, new Object[] { store });
		map.put("id",store.get("wd_id"));
		return map;
	}
	/**
	 * 更新排班记录
	 */
	@Override
	public Map<String,Object> updateEmpWorkDate(String deptcodes,
			String emcodes, String date, String workcode ){
		Map<String,Object> modelMap=new HashMap<String,Object>();
		if(deptcodes!=null&&deptcodes!=""){
			if(deptcodes.length()!=0){
				String deptcode=deptcodes.replace(",","','");
				try {
					baseDao.execute("update empworkdate set ew_date=to_date('"
							+ date
							+ "','yyyyMMdd') where ew_wdcode='"
							+ workcode
							+ "' and ew_emcode in (select em_code from employee where em_defaultorcode in ('"
							+ deptcode + "'))");
					baseDao.execute("delete from workdate_hrorg where wh_wdcode='"+workcode+"' and wh_id not in(select min(wh_id) from workdate_hrorg group by wh_defaultorname,wh_defaultcode,wh_wdcode)");
					baseDao.execute("update workdate_hrorg set wh_days=(select  case d when 0 then 7 else d end day from (select to_char(to_date('"+date+"','yyyymmdd'),'d')-1 d from dual) ) where  wh_wdcode='"+workcode+"'");
					baseDao.execute("update workdate set wd_day=(select case when d=0 then 7 else d end day from (select to_char(to_date('"+date+"','yyyymmdd'),'d')-1 d from dual)) where wd_code='"+workcode+"'");
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}			
		}
		if(emcodes!=null&&emcodes!=""){
			if(emcodes.length()!=0){
				String emcode=emcodes.replace(",","','");
				 try {
					 baseDao.execute("update empworkdate set ew_date=to_date('"
								+ date + "','yyyyMMdd') where ew_wdcode='" + workcode
								+ "' and ew_emcode in('" + emcode + "')");
					 baseDao.execute("delete from workdate_emp where we_wdcode='"+workcode+"' and we_id not in(select min(we_id) from workdate_emp group by we_emname,we_emcode,we_wdcode)");
					 baseDao.execute("update workdate_emp set we_days=(select  case d when 0 then 7 else d end day from (select to_char(to_date('"+date+"','yyyymmdd'),'d')-1 d from dual) ) where  we_wdcode='"+workcode+"'");
					 baseDao.execute("update workdate set wd_day=(select case when d=0 then 7 else d end day from (select to_char(to_date('"+date+"','yyyymmdd'),'d')-1 d from dual)) where wd_code='"+workcode+"'");
				 } catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		return modelMap;
		
	}
 /**
  * 删除排班记录
  */
	@Override
	public Map<String,Object> deleteEmpworkDate(String deptcodes,
			String emcodes, String date, String workcode, String flag){
		Map<String, Object> modelMap=new HashMap<String,Object>();
		if("rest".equals(flag)){
			if(deptcodes!=null&&deptcodes!=""){
				if(deptcodes.length()!=0){
					String deptcode=deptcodes.replace(",","','");
					baseDao.execute("delete from empworkdate where ew_date=to_date('"
							+ date
							+ "','yyyyMMdd') and ew_wdcode='"
							+ workcode
							+ "' and ew_emcode in (select em_code from employee where em_defaultorcode in ('"
							+ deptcode + "'))");
				}			
			}
			if(emcodes!=null&&emcodes!=""){
				if(emcodes.length()!=0){
					String emcode=emcodes.replace(",","','");
					baseDao.execute("delete from empworkdate where ew_date=to_date('"
							+ date + "','yyyyMMdd') and ew_wdcode='" + workcode
							+ "' and ew_emcode in('" + emcode + "')");
				}
			}
		}
		
		return modelMap;
	}
	/**
	 * 获取冲突班次和冲突部门
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Map<String, Object>> getManAndDefaultor(String formStore) {
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		Map<String,Object> list1map=new HashMap<String,Object>();
		Map<String,Object> list2map=new HashMap<String,Object>();
		List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		Map<String,Object> modlemap=null;		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		if(store.get("wd_emcode")!=null){
			if(!("".equals(store.get("wd_emcode").toString()))){
				String[] mans = ((String) store.get("wd_emcode")).split(",");
				if(mans!=null){
					for (String man : mans) {
						map = new HashMap<String, Object>();
						Object[] workdates=baseDao.getFieldsDataByCondition("empworkdate left join workdate on ew_wdcode=wd_code", "wd_code,wd_name", "ew_emcode='"+man+"' and wd_name<>'默认班次' group by wd_code,wd_name having count(*)>=1");
						Object[] data1 = baseDao.getFieldsDataByCondition("empworkdate left join employee on ew_emcode=em_code","em_name,ew_emcode,em_id,em_position,em_defaulthscode,em_defaultorname,em_depart,em_defaulthsname,em_cop","ew_wdcode is not null and ew_emcode='"+man+"' and ew_date=to_date(to_char(sysdate,'yyyyMMdd'),'yyyymmdd')");
						if(workdates!=null){
							map.put("workdatecode",workdates[0]);
							map.put("workdatename",workdates[1]);
						}else{
							map.put("workdatecode",null);
							map.put("workdatename",null);
							
						}
						if (data1 != null) {
							map.put("ew_emcode", data1[1]);
							map.put("em_name", data1[0]);
							map.put("em_id", data1[2]);
							map.put("em_position", data1[3]);
							map.put("em_defaulthscode", data1[4]);
							map.put("em_defaultorname", data1[5]);
							map.put("em_depart", data1[6]);
							map.put("em_defaulthsname", data1[7]);
							map.put("em_cop", data1[8]);
							//baseDao.updateByCondition("empworkdate", "ew_wdcode='"+store.get("wd_code")+"'", "ew_emcode='"+man+"'");
						}else{
							map.put("ew_emcode", null);
							map.put("em_name", null);
							map.put("em_id", null);
							map.put("em_position", null);
							map.put("em_defaulthscode", null);
							map.put("em_defaultorname", null);
							map.put("em_depart", null);
							map.put("em_defaulthsname", null);
							map.put("em_cop", null);
						}
						list1.add(map);
						
					}
				}
				
			}
		}

		if(store.get("wd_defaultorcode")!=null){
			if(!("".equals(store.get("wd_defaultorcode").toString()))){			
				String[] defaultorcode = ((String) store.get("wd_defaultorcode")).split(",");
				if(defaultorcode!=null){
					for (String defaultor : defaultorcode) {
						modlemap=new HashMap<String,Object>();
						 //list2map=new HashMap<String,Object>();
						//list2 = new ArrayList<Map<String, Object>>();
						Object[] workdates=baseDao.getFieldsDataByCondition("employee left join hrorg on employee.EM_DEFAULTORCODE=or_code left join empworkdate on em_code=ew_emcode left join workdate on ew_wdcode=wd_code", "wd_code,wd_name", "ew_emcode in (select em_code from employee where em_defaultorcode='"+defaultor+"') and wd_name<>'默认班次'and ew_date=to_date(to_char(sysdate,'yyyyMMdd'),'yyyymmdd')");
						Object[] data2 = baseDao.getFieldsDataByCondition("employee left join empworkdate on em_code=ew_emcode","em_defaultorcode,em_defaultorname","em_defaultorcode='"+defaultor+"' and ew_wdcode is not null and ew_date=to_date(to_char(sysdate,'yyyyMMdd'),'yyyymmdd')");
						int count =baseDao.getCount("select count(1) from employee where em_defaultorcode='"+defaultor+"'");
						modlemap.put("defaultormancount", count);
						if(workdates!=null){
							modlemap.put("workdatescode", workdates[0]);
							modlemap.put("workdatesname", workdates[1]);					
						}else{
							modlemap.put("workdatescode", null);
							modlemap.put("workdatesname", null);	
							}
						if (data2 != null) {
							modlemap.put("conflictem_defaultorcode", data2[0]);
							modlemap.put("conflictem_defaultorname", data2[1]);
						}else{
							Object[] data1=baseDao.getFieldsDataByCondition("hrorg_mobile", "or_code,or_name","or_code='"+defaultor+"'");
							modlemap.put("conflictem_defaultorcode", data1[0]);
							modlemap.put("conflictem_defaultorname", data1[1]);
						}
						list2.add(modlemap);
						
					}
				}
		
			}
		}
	
		list1map.put("man", list1);
		list2map.put("defaultor", list2);
		lists.add(list1map);
		lists.add(list2map);
		return lists;
	}
	/*
	 * 保存考勤记录
	 */
	@Override
	public Map<String, Object> saveCardLog(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Map<String,Object> modelMap=new HashMap<String,Object>();
		handlerService.beforeSave(caller, new Object[] { store });
		Object faceCard=store.get("facecard");
		Object iffaceCard=baseDao.getFieldDataByCondition("mobile_attendsystem", "MA_NEEDVALIDATEFACE", "1=1");		
		if(faceCard!=null){
			if(faceCard.toString().equals("0")&&iffaceCard.toString().equals("1")){
					BaseUtil.showError("请升级APP再打卡");
			}
		} 
		store.remove("facecard");
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "CardLog",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.logger.save(caller, "cl_code", store.get("cl_code"));
		handlerService.afterSave(caller, new Object[] { store });
		modelMap.put("success", "true");
		return modelMap;
	}

	/*
	 * 保存外勤计划
	 */
	@Override
	public Map<String, Object> saveOutPlan(String formStore, String caller,
			String gridStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		int id = baseDao.getSeqId("MOBILE_OUTPLAN_SEQ");
		if (store.get("mp_id") == null) {
			store.put("mp_id", id);
		} else if ("".equals("mp_id")) {
			store.remove("mp_id");
			store.put("mp_id", id);
		}
		modelMap.put("mp_id", id);
		//handlerService.beforeSave(caller, new Object[] { id });
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"mobile_outplan", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		int mpd_id=0;
		for(int i=0;i<grid.size();i++){
			mpd_id=baseDao.getSeqId("MOBILE_OUTPLANDETAIL_SEQ");
			grid.get(i).put("mpd_id", mpd_id);
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"mobile_outplandetail");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "mp_id", store.get("mp_id"));
		modelMap.put("mpd_id", mpd_id);
		//handlerService.afterSave(caller, new Object[] { store, grid });
		return modelMap;
	}

	/*
	 * 保存外勤目的地
	 */
	@Override
	public Map<String, Object> saveOutAddress(String formStore, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当保存的公司是有历史拜访记录的,更新拜访次数，及上次拜访时间
		int count = baseDao.getCount("select count(1) from mobile_outaddress where md_company='"+ store.get("Md_company") + "'");
		if (count > 0) {
			String sql = "update mobile_outaddress set md_visitcount=md_visitcount+1,md_visittime=sysdate,md_latitude="+store.get("Md_latitude") +",md_longitude="+store.get("Md_longitude") +" where md_company='"
					+ store.get("Md_company") + "'";
			baseDao.execute(sql);
		} else {
			int id = baseDao.getSeqId("MOBILE_OUTADDRESS_SEQ");
			if (store.get("md_id") == null) {
				store.put("md_id", id);
			} else if ("".equals("md_id")) {
				store.remove("md_id");
				store.put("md_id", id);
			}
			modelMap.put("md_id", id);
			//handlerService.beforeSave(caller, new Object[] { id });
			String formSql = SqlUtil.getInsertSqlByFormStore(store,
					"mobile_outaddress", new String[] {}, new Object[] {});
			baseDao.execute(formSql);

			baseDao.logger.save(caller, "md_id", store.get("md_id"));
		}
		return modelMap;
	}

	/*
	 * 获取外勤目的地
	 */
	@Override
	public List<Map<String, Object>> getOutAddressDate(String condition,
			int pageIndex, int pageSize) {
		if ("".equals(condition)) {
			condition = "1=1";
		}
		int start = ((pageIndex - 1) * pageSize + 1);
		int end = pageIndex * pageSize;
		String sql = "select *　from (select a.*,rownum rn from (select * from mobile_outaddress where  md_company is not null and "
				+ condition
				+ " and rownum<="
				+ end
				+ " order by  md_visittime desc)a) where rn >="
				+ start
				+ " order by  md_visittime desc";

		return baseDao.getJdbcTemplate().queryForList(sql);
	}

	/*
	 * 保存更新外勤设置
	 */
	@Override
	public Map<String, Object> updateOutSet(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int id = baseDao.getSeqId("MOBILE_OUTSET_SEQ");
		if (store.get("mo_id") == null) {
			store.put("mo_id", id);
		} else if ("".equals("mo_id")) {
			store.remove("mo_id");
			store.put("mo_id", id);
		}
		modelMap.put("mo_id", id);
		handlerService.beforeUpdate(caller, new Object[] { id });
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"mobile_outset", "mo_id");
		baseDao.execute(formSql);
		baseDao.logger.update(caller, "mo_id", store.get("mo_id"));
		handlerService.afterUpdate(caller, new Object[] { store });
		return modelMap;
	}

	/*
	 * 获取外勤设置
	 */
	@Override
	public Map<String, Object> getOutSet(String condition) {
		// List<Map<String,Object>> data =
		// baseDao.queryForList("select * from JProcessSet") ;
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Object[] data = baseDao.getFieldsDataByCondition("mobile_outset",
				new String[] { "mo_id", "mo_distance", "mo_time" }, "1=1");
		modelMap.put("mo_id", data[0]);
		modelMap.put("mo_distance", data[1]);
		modelMap.put("mo_time", data[2]);
		return modelMap;
	}

	@Override
	public Map<String, Object> ifAdmin(String emcode) {
		Map<String, Object> map = new HashMap<String, Object>();
		boolean ifAdmin = baseDao.checkIf("employee", "em_code='" + emcode
				+ "' and em_type='admin'");
		map.put("isAdmin", ifAdmin ? "1" : "0");
		return map;
	}

	@Override
	public Map<String, Object> saveConfigs(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);		
		int count =baseDao.getCountByCondition("MOBILE_ATTENDSYSTEM", "1=1");
		if(count>0){
		Object id =baseDao.getFieldDataByCondition("MOBILE_ATTENDSYSTEM", "ma_id", "1=1");
		store.put("ma_id", id);
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"MOBILE_ATTENDSYSTEM","ma_id");
		//String sql = "UPDATE MOBILE_ATTENDSYSTEM SET MA_SERIOUSLATETIME='"+store.get("MA_SERIOUSLATETIME")+"',MA_LATETIME='"+store.get("MA_LATETIME")+"',MA_EARLYTIME='"+store.get("MA_EARLYTIME")+"',MA_ID='"+id+"',MA_ABSENTTIME='"+store.get("MA_ABSENTTIME")+"',AUTOCARDLOG='"+store.get("AUTOCARDLOG")+"' where ma_id='"+id+"'";
		baseDao.execute(formSql);
		//baseDao.logger.update(caller, "ma_id", store.get("ma_id"));
		//handlerService.afterUpdate(caller, new Object[] { store });
		}else{
			int ids = baseDao.getSeqId("MOBILE_ATTENDSYSTEM_SEQ");
			if (store.get("ma_id") == null) {
				store.put("ma_id", ids);
			} else if ("".equals("ma_id")) {
				store.remove("ma_id");
				store.put("ma_id", ids);
			}
			modelMap.put("ma_id", ids);
			handlerService.beforeSave(caller, new Object[] { ids });
			String formSql = SqlUtil.getInsertSql(store,
					"mobile_attendsystem", "ma_id");
			baseDao.execute(formSql);
			baseDao.logger.save(caller, "ma_id", store.get("ma_id"));
			//handlerService.afterSave(caller, new Object[] { store });
		}		
		return modelMap;
	}

	@Override
	public Map<String, Object> savecomaddressset(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String code = baseDao.sGetMaxNumber("COMADDRESSSET", 2);
		if(store.get("cs_code")==null){
			store.put("cs_code", code);
		}
		int id = baseDao.getSeqId("COMADDRESSSET_SEQ");
		if (store.get("cs_id") == null) {
			store.put("cs_id", id);
		} else if ("".equals("cs_id")) {
			store.remove("cs_id");
			store.put("cs_id", id);
		}
		modelMap.put("cs_id", id);
		store.put("CS_RECORDER", store.get("CS_RECORDER"));
		String sql="select count(*) from COMADDRESSSET where CS_WORKADDR='"+store.get("CS_WORKADDR")+"'";
		int count=baseDao.getCount(sql);
		if(count<=0){
			handlerService.beforeSave(caller, new Object[] {store });
			String formSql = SqlUtil.getInsertSqlByFormStore(store, "COMADDRESSSET",
					new String[] {}, new Object[] {});
			baseDao.execute(formSql);
			baseDao.logger.save(caller, "cs_id", store.get("cs_id"));
			handlerService.afterSave(caller, new Object[] { store });
		}		
		return modelMap;
	}

	@Override
	public Map<String, Object> updatecomaddressset( String caller,String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//Object id = store.get("cs_id");
		/*if (store.get("cs_id") == null) {
			store.put("cs_id", id);
		} else if ("".equals("cs_id")) {
			store.remove("cs_id");
			store.put("cs_id", id);
		}*/
		//modelMap.put("cs_id", id);
		/*String sql="select count(*) from COMADDRESSSET where CS_WORKADDR='"+store.get("CS_WORKADDR")+"'";
		int count=baseDao.getCount(sql);
		if(count>0){
			BaseUtil.showError("已经有相同的考勤地址设置，不要重复设置");
		}*/
		//handlerService.beforeUpdate(caller, new Object[] { id });
		String formSql ="update comaddressset set CS_VALIDRANGE='"+store.get("CS_VALIDRANGE")+"',CS_INNERDISTANCE='"+store.get("CS_INNERDISTANCE")+"'";
		baseDao.execute(formSql);
		//baseDao.logger.update(caller, "cs_id", store.get("cs_id"));
		//handlerService.afterUpdate(caller, new Object[] { store });
		return modelMap;
	}
	@Override
	public Map<String, Object> deletecomaddressset( String caller,String id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("cs_id", id);
		String sql=SqlUtil.getDeleteSql("comaddressset", "cs_id='"+id+"'");
		baseDao.execute(sql);
		return modelMap;
	}
	
	@Override
	public Map<String, Object> deleteWorkDate(String caller,String wdcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<String> sqls=new ArrayList<String>();
		modelMap.put("wd_code", wdcode);
		String empworkdatesql =SqlUtil.getDeleteSql("empworkdate", "ew_wdcode='"+wdcode+"'");
		sqls.add(empworkdatesql);
		String sql=SqlUtil.getDeleteSql("workdate", "wd_code='"+wdcode+"'");
		sqls.add(sql);
		String sqlwe=SqlUtil.getDeleteSql("workdate_emp", "we_wdcode='"+wdcode+"'");
		sqls.add(sqlwe);
		String sqlwh=SqlUtil.getDeleteSql("workdate_hrorg", "wh_wdcode='"+wdcode+"'");
		sqls.add(sqlwh);
		baseDao.execute(sqls);
		return modelMap;
	}
	@Override
	public List<Map<String, Object>> myComPlan(String emcode){
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		Map<String, Object> modelMap = null;
		//Object emname=baseDao.getFieldDataByCondition("employee", "em_name", "em_code='"+emcode+"'");
		List<Object[]> datas=baseDao.getFieldsDatasByCondition("workdate", new String[]{"wd_name","wd_ondutyone","wd_offdutyone","wd_ondutytwo","wd_offdutytwo","wd_ondutythree","wd_offdutythree","wd_hours","wd_degree","wd_recorddate","wd_day"}," wd_recorddate>=trunc(SYSDATE, 'MM') and wd_recorddate<=last_day(SYSDATE) and wd_emcode like '%"+emcode+"%'");
		for (Object[] data : datas) {
			modelMap = new HashMap<String, Object>();
			if(datas!=null){
				modelMap.put("success", true);
				modelMap.put("name",data[0]);
				modelMap.put("starttime", data[1]);
				if("1".equals(data[8].toString())){
					modelMap.put("endtime", data[2]);
				}else if("2".equals(data[8].toString())){
					modelMap.put("endtime", data[4]);
				}else if("3".equals(data[8].toString())){
					modelMap.put("endtime", data[6]);
				}
				modelMap.put("totaltime", data[7]);
				modelMap.put("wd_recorddate", data[9]);
				modelMap.put("wd_day", data[10]);
			}else {
				modelMap.put("success", false);
			}
			lists.add(modelMap);
		}		
		return lists;
		
	}
	/*
	 * 获取考勤设置
	 */
	@Override
	public List<Map<String, Object>> getcomaddressset(String condition) {
		// List<Map<String,Object>> data =
		// baseDao.queryForList("select * from JProcessSet") ;
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		if ("".equals(condition)) {
			condition = "1=1";
		}
		//Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Object[]> datas = baseDao.getFieldsDatasByCondition("comaddressset",
				new String[] { "CS_CODE", "CS_WORKADDR", "CS_LONGITUDE" ,"CS_LATITUDE","CS_VALIDRANGE","CS_INNERDISTANCE","CS_SHORTNAME","CS_ID"}, "1=1");		
		for (Object[] data : datas) {
			map = new HashMap<String, Object>();
			if (datas != null) {				
				map.put("success", true);
				map.put("CS_CODE", data[0]);
				map.put("CS_WORKADDR", data[1]);
				map.put("CS_LONGITUDE", data[2]);
				map.put("CS_LATITUDE", data[3]);
				map.put("CS_VALIDRANGE", data[4]);
				map.put("CS_INNERDISTANCE", data[5]);
				map.put("CS_SHORTNAME", data[6]);
				map.put("CS_ID", data[7]);
			}else {
				map.put("success", false);
			}
			lists.add(map);
		}
		 return lists;
	}
	@Override
	public Map<String, Object> getConfigs(String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if ("".equals(condition)) {
			condition = "1=1";
		}
		/*for (String set : settings) {
			Object data = baseDao.getFieldDataByCondition("CONFIGS", "data",
					"code='" + set + "' and rownum=1");
			if (data != null) {
				map.put(set, data.toString());
			}else{
				map.put(set, data);
			}
		}*/
		Object[] data = baseDao.getFieldsDataByCondition("mobile_attendsystem", new String[] {"AUTOCARDLOG",
				"MA_ABSENTTIME","MA_EARLYTIME","MA_ID","MA_LATETIME","MA_SERIOUSLATETIME","MA_NEEDVALIDATEFACE"},"1=1");
		if(data!=null){
			modelMap.put("autosign", data[0]!=null?data[0]:1);
			modelMap.put("nonclass", data[1]);
			modelMap.put("earlyoff", data[2]);
			modelMap.put("id", data[3]);
			modelMap.put("latetime", data[4]);
			modelMap.put("overlatetime", data[5]);		
			modelMap.put("needValidateFace", String.valueOf(data[6]).equals("0")||data[6]==null?false:true);
		}else {
			modelMap.put("autosign", 1);
			modelMap.put("nonclass", null);
			modelMap.put("earlyoff", null);
			modelMap.put("id", null);
			modelMap.put("latetime", null);
			modelMap.put("overlatetime", null);
			modelMap.put("needValidateFace", false);
		}
		return modelMap;
	}

	@Override
	public void updateConfigs(String caller, String formStore) {
		Map<Object, Object> datas = BaseUtil.parseFormStoreToMap(formStore);
		Set<Object> sets = datas.keySet();
		Iterator i = sets.iterator();
		while (i.hasNext()) {
			Object key = i.next();
			baseDao.execute("update configs set data='" + datas.get(key)
					+ "' where code='" + key + "'");
		}
	}
	public Map<String, Object> autoCardLog(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int id = baseDao.getSeqId("MOBILE_ATTENDSYSTEM_SEQ");
		if (store.get("ma_id") == null) {
			store.put("ma_id", id);
		} else if ("".equals("ma_id")) {
			store.remove("ma_id");
			store.put("ma_id", id);
		}
		modelMap.put("ma_id", id);
		handlerService.beforeUpdate(caller, new Object[] { id });
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"MOBILE_ATTENDSYSTEM", "ma_id");
		baseDao.execute(formSql);
		baseDao.logger.update(caller, "ma_id", store.get("ma_id"));
		//handlerService.afterUpdate(caller, new Object[] { store });
		return modelMap;	
	}

	@Override
	public boolean ifNeedSignCard(String emcode){
		Object isNeedSignCard = baseDao.getFieldDataByCondition("employee", "case when em_workattendance is null then 0 else em_workattendance end", "em_code='"+emcode+"'");
		if(isNeedSignCard!=null||!"null".equals(isNeedSignCard)){
			if("1".equals(isNeedSignCard.toString())){
				return true;
			}else if("0".equals(isNeedSignCard.toString())){
				return false;
			}else{
				BaseUtil.showError("未知标识!");
			}
		}else{
			BaseUtil.showError("未设置是否考勤!");
		}
		return false;
	}
	
	@Override
	public boolean ifInCompany(String emcode) {		
		boolean workClass = baseDao
				.checkIf(
						"(empworkdate left join workdate on ew_wdcode=wd_code)",
						"ew_emcode='"
								+ emcode
								+ "' and ew_date=to_date(to_char(sysdate,'yyyymmdd'),'yyyymmdd')");
		if (workClass) {
			boolean inWorkClass = baseDao
					.checkIf(
							"(empworkdate left join workdate on ew_wdcode=wd_code)",
							"(to_char(sysdate,'HH24:mi')>wd_ondutyone and to_char(sysdate,'HH24:mi')<wd_offdutyone) or (to_char(sysdate,'HH24:mi')>wd_ondutytwo and to_char(sysdate,'HH24:mi')<wd_offdutytwo) or (to_char(sysdate,'HH24:mi')>wd_ondutythree and to_char(sysdate,'HH24:mi')<wd_offdutythree)");
			if (inWorkClass) {
				return notice(emcode);
			} else {
				return false;
			}
		} else {
			// 当天没有班次，取默认班次
			boolean defaultClass = baseDao.checkIf(
					"(workdate left join employee on em_wdcode=wd_code)",
					"em_code='" + emcode + "'");
			if (defaultClass) {
				boolean inWorkClass = baseDao
						.checkIf(
								"(workdate left join employee on em_wdcode=wd_code)",
								"(to_char(sysdate,'HH24:mi')>wd_ondutyone and to_char(sysdate,'HH24:mi')<wd_offdutyone) or (to_char(sysdate,'HH24:mi')>wd_ondutytwo and to_char(sysdate,'HH24:mi')<wd_offdutytwo) or (to_char(sysdate,'HH24:mi')>wd_ondutythree and to_char(sysdate,'HH24:mi')<wd_offdutythree)");
				if (inWorkClass) {
					return notice(emcode);
				} else {
					return false; // 不在考勤时间内
				}
			} else {
				// 没有默认班次,取系统设置
				boolean objsys = baseDao.checkIf("AttendSystem", "1=1");
				if (objsys) {
					boolean inWorkClass = baseDao
							.checkIf(
									"AttendSystem",
									"rownum=1 and (to_char(sysdate,'HH24:mi')>as_amstarttime and to_char(sysdate,'HH24:mi')<as_amendtime) or (to_char(sysdate,'HH24:mi')>as_pmstarttime and to_char(sysdate,'HH24:mi')<as_pmendtime)");
					if (inWorkClass) {
						return notice(emcode);
					} else {
						return false;
					}
				} else {
					return false;
				}

			}
		}
	}

	private boolean notice(String emcode) {
		Employee employee = employeeDao.getEmployeeByEmcode(emcode);
		// 判断是否有请假
		boolean haveVacation = baseDao.checkIf("vacation",
				"va_startime<sysdate and va_endtime>sysdate and va_emcode='"
						+ emcode + "'");
		if (haveVacation) {
			return true;
		} else {
			// 是否有外勤计划
			boolean haveOutSignPlan = baseDao
					.checkIf(
							"(mobile_outplandetail left join mobile_outplan on mpd_mpid=mp_id)",
							"mp_recordercode='" + emcode
									+ "' and mpd_status is null");
			if (haveOutSignPlan) {
				return true;
			} else {
				//判断是否有出差申请
				boolean haveFeePlease = baseDao.checkIf("feeplease", "fp_recordman='"+employee.getEm_name()+"' and fp_prestartdate<sysdate and fp_preenddate>sysdate");
				if(haveFeePlease){
					return true;
				}else{
					return false;
				}
			}
		}
	}
	
	/*
	 * 个人考勤统计接口
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Map<String, Object>> getPersonAttend(String emcode,String yearmonth) {
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		Object[] data = baseDao.getFieldsDataByCondition("AttendData",
				new String[] {"ad_intimeday","ad_vatimes","ad_onovertime"}, "AD_EMCODE='"+emcode+"'");		
		Integer latecount=baseDao.getCountByCondition("AttendData", " ad_latemin is not null and ad_latemin<>0 and to_char(AD_INDATE,'yyyymm')='"+yearmonth+"' and AD_EMCODE='"+emcode+"'");
		Integer earlycount=baseDao.getCountByCondition("AttendData", " ad_leaveearlymin is not null and ad_leaveearlymin<>0 and to_char(AD_INDATE,'yyyymm')='"+yearmonth+"' and AD_EMCODE='"+emcode+"'");
		//出差天数
		Object outdays=baseDao.getFieldDataByCondition("AttendData", "sum(ad_vatimes)", "nvl(ad_vacationtype,' ')='出差申请单' and ad_emcode='"+emcode+"' and to_char(ad_indate,'yyyymm')='"+yearmonth+"'");		
		String condition = "ad_emcode='" + emcode + "' and to_char(ad_indate,'yyyymm')='"+yearmonth+"'";	
		//请假天数
		Object out=baseDao.getFieldDataByCondition("AttendData", "sum(ad_vatimes)", "nvl(ad_vacationtype,' ')<>'出差申请单' and " + condition);
		//实际出勤
		Object intime=baseDao.getFieldDataByCondition("AttendData", "sum(ad_intimeday)", condition);
		//加班时数
		Object overtime=baseDao.getFieldDataByCondition("AttendData", "sum(ad_onovertime)", condition);
		//应出勤
		Object attend = baseDao.getFieldDataByCondition("AttendData left join employee on ad_emcode=em_code", "round((sum(nvl(ad_absentmin,0))+sum(nvl(ad_intimeday,0))),1) ad_needday", "to_char(ad_indate,'yyyymm')='"+yearmonth+"' and em_code='"+emcode+"'");
		//实际工时
		Object intimemin = baseDao.getFieldDataByCondition("AttendData left join employee on ad_emcode=em_code","sum(nvl(ad_intimemin,0))", "to_char(ad_indate,'yyyymm')='"+yearmonth+"' and em_code='"+emcode+"'");
		//正常打卡
		Object nday=baseDao.getFieldDataByCondition("AttendData", " count(1)", "nvl(ad_desc,' ')<>'pbnodk' and nvl(ad_latemin,0)=0 and nvl(ad_leaveearlymin,0)=0 and "+condition+"");
		//旷工
		Object count=baseDao.getFieldDataByCondition("AttendData left join employee on ad_emcode=em_code", "sum(case when ad_kind='异常' and ad_intimemin=0 then 1 else 0 end) ad_kind", condition+" ");
		//补卡
		Object signcard=baseDao.getFieldDataByCondition("MOBILE_SIGNCARD","count(1)","ms_emcode='"+emcode+"' and to_char(ms_signtime,'yyyymm')='"+yearmonth+"'");
		//外勤次数
		Object outcount=baseDao.getFieldDataByCondition("mobile_outsign","count(1)","mo_mancode='"+emcode+"' and to_char(mo_signtime,'yyyymm')='"+yearmonth+"'");
		//if (data != null) {				
			map.put("ychuqin", attend==null?"0":attend.toString());
			map.put("achuqin", intime==null?"0":intime.toString());
			map.put("nday", nday==null?"0":nday.toString());
			map.put("atime", intimemin==null?"0":intimemin.toString());
			map.put("latecount", latecount.toString());
			map.put("earlycount", earlycount.toString());
			map.put("noncount", count==null?"0":count.toString());
			map.put("qjdaty", out==null?"0":out.toString());
			map.put("overtime", overtime==null?"0":overtime.toString());
			map.put("outdays", outdays==null?"0":outdays.toString());
			map.put("outcount", outcount==null?"0":outcount.toString());
			map.put("signcard", signcard==null?"0":signcard.toString());
		//}
		lists.add(map);
	
		return lists;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public Map<String, Object> getTeamAttend(String emcode,String yearmonth) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		List<Map<String,Object>> othersLists = new ArrayList<Map<String, Object>>();
		
		String condition = "";
		condition += " em_code in (";
		SqlRowList srl = baseDao.queryForRowSet("select * from employee where em_defaultorid in (select or_id from hrorg connect by prior or_id=or_subof start with or_id=(select em_defaultorid from employee where em_code='"+emcode+"') and nvl(or_statuscode,' ') <> 'DISABLE')  and NVL(em_class,' ')<>'离职'");
		if(srl.hasNext()){
			while(srl.next()){
				condition += "'" + srl.getString("em_code") + "',";				
			}
			condition = condition.substring(0, condition.lastIndexOf(",")) + ")";
		}else{
			BaseUtil.showError("员工所属组织人员数为0!");
			condition += "'')";
		}
		List<Object[]> latecounts=baseDao.getFieldsDatasByCondition("(employee b left join (select count(1) cu,ad_emcode from attenddata where  to_char(ad_indate,'yyyymm')='"+yearmonth+"' and nvl(ad_latemin,0)<>0 and "+condition.replace("em_code", "ad_emcode")+" group by ad_emcode)a on a.ad_emcode=b.em_code)", new String[]{"nvl(cu,0)","em_code","em_name"}, condition + " order by em_code");
		List<Object[]> earlycounts=baseDao.getFieldsDatasByCondition("(employee b left join (select count(1) cu,ad_emcode from attenddata where  to_char(ad_indate,'yyyymm')='"+yearmonth+"' and nvl(ad_leaveearlymin,0)<>0 and "+condition.replace("em_code", "ad_emcode")+" group by ad_emcode)a on a.ad_emcode=b.em_code)",new String[]{"nvl(cu,0)","em_code","em_name"}, condition+" order by em_code");
		//请假天数
		List<Object> out = baseDao.getFieldDatasByCondition("(employee b left join (select sum(ad_vatimes) cu,ad_emcode from attenddata where  to_char(ad_indate,'yyyymm')='"+yearmonth+"' and nvl(ad_vacationtype,' ')<>'出差申请单' and "+condition.replace("em_code", "ad_emcode")+" group by ad_emcode)a on a.ad_emcode=b.em_code)", "nvl(cu,0)", condition+" order by em_code");
		//出差天数
		List<Object> away = baseDao.getFieldDatasByCondition("(employee b left join (select sum(ad_vatimes) cu,ad_emcode from attenddata where  to_char(ad_indate,'yyyymm')='"+yearmonth+"' and nvl(ad_vacationtype,' ')='出差申请单' and "+condition.replace("em_code", "ad_emcode")+" group by ad_emcode)a on a.ad_emcode=b.em_code)", "nvl(cu,0)", condition+" order by em_code");
		//实际出勤
		List<Object> intime = baseDao.getFieldDatasByCondition("(employee b left join (select sum(ad_intimeday) cu,ad_emcode from attenddata where  to_char(ad_indate,'yyyymm')='"+yearmonth+"' and "+condition.replace("em_code", "ad_emcode")+" group by ad_emcode)a on a.ad_emcode=b.em_code)", "nvl(cu,0)", condition+" order by em_code");
		//加班时数
		List<Object> overtime = baseDao.getFieldDatasByCondition("(employee b left join (select sum(ad_onovertime) cu,ad_emcode from attenddata where  to_char(ad_indate,'yyyymm')='"+yearmonth+"' and "+condition.replace("em_code", "ad_emcode")+" group by ad_emcode)a on a.ad_emcode=b.em_code)", "nvl(cu,0)", condition+" order by em_code");
		//应出勤
		List<Object> attends = baseDao.getFieldDatasByCondition("(employee b left join (select round((sum(nvl(ad_absentmin,0))+sum(nvl(ad_intimeday,0))),1) cu,ad_emcode from AttendData left join employee on ad_emcode=em_code where to_char(ad_indate,'yyyymm')='"+yearmonth+"' and "+condition.replace("em_code", "ad_emcode")+" group by ad_emcode)a on a.ad_emcode=b.em_code)", "nvl(cu,0)", condition+" order by em_code");
		//实际工时
		List<Object> intimemins = baseDao.getFieldDatasByCondition("(employee b left join (select sum(nvl(ad_intimemin,0)) cu,ad_emcode from AttendData left join employee on ad_emcode=em_code where to_char(ad_indate,'yyyymm')='"+yearmonth+"' and "+condition.replace("em_code", "ad_emcode")+" group by ad_emcode)a on a.ad_emcode=b.em_code)","nvl(cu,0)", condition+" order by em_code");
		//正常打卡
		List<Object> ndays=baseDao.getFieldDatasByCondition("(employee b left join (select count(1) cu,ad_emcode,ad_desc,ad_latemin,ad_leaveearlymin from AttendData left join employee on ad_emcode=em_code where to_char(ad_indate,'yyyymm')='"+yearmonth+"' and "+condition.replace("em_code", "ad_emcode")+" and (nvl(ad_desc,' ')<>'pbnodk' and nvl(ad_latemin,0)=0 and nvl(ad_leaveearlymin,0)=0) group by ad_emcode,ad_desc,ad_latemin,ad_leaveearlymin)a on a.ad_emcode=b.em_code)", "nvl(cu,0)", "(nvl(ad_desc,' ')<>'pbnodk' and nvl(ad_latemin,0)=0 and nvl(ad_leaveearlymin,0)=0) and "+condition+" order by em_code");
		//补卡
		List<Object> signcards=baseDao.getFieldDatasByCondition("(employee b left join (select count(1) cu,ms_emcode from MOBILE_SIGNCARD left join employee on ms_emcode=em_code where to_char(ms_signtime,'yyyymm')='"+yearmonth+"' and "+condition.replace("em_code", "ms_emcode")+" group by ms_emcode)a on a.ms_emcode=b.em_code)","nvl(cu,0)",condition+" order by em_code");
		//旷工
		List<Object> counts=baseDao.getFieldDatasByCondition("(employee b left join (select count(1) cu,sum(case when ad_kind='异常' and ad_intimemin=0 then 1 else 0 end) ad_kind,ad_emcode,ad_kg from AttendData left join employee on ad_emcode=em_code where to_char(ad_indate,'yyyymm')='"+yearmonth+"' and "+condition.replace("em_code", "ad_emcode")+" and ad_kg is not null group by ad_emcode,ad_desc,ad_latemin,ad_leaveearlymin,ad_kg)a on a.ad_emcode=b.em_code)", " nvl(cu,0)", condition+"  order by em_code");
		//外勤次数
		List<Object> outcounts=baseDao.getFieldDatasByCondition("(employee b left join (select count(1) cu,mo_mancode from mobile_outsign left join employee on mo_mancode=em_code where to_char(mo_signtime,'yyyymm')='"+yearmonth+"' and "+condition.replace("em_code", "mo_mancode")+" group by mo_mancode)a on a.mo_mancode=b.em_code)","nvl(cu,0)",condition+" order by em_code");										
		Map<String, Object> map;
		for(int i=0;i<latecounts.size();i++){
			Object[] latecount = latecounts.get(i);
			Object[] earlycount = earlycounts.get(i);
			Object outdays = out.get(i);
			Object awaydays = away.get(i);
			Object intimework = intime.get(i);
			Object otime = overtime.get(i);
			Object attend = attends.get(i);
			Object intimemin = intimemins.get(i);
			Object nday = ndays.get(i);
			Object signcard=signcards.get(i);
			Object count=counts.get(i);
			Object outcount=outcounts.get(i);
			map = new HashMap<String, Object>();
			
			map.put("emcode",latecount[1]);
			map.put("emname",latecount[2]);
			
			map.put("ychuqin", attend!=null?attend.toString():"0"); //应该出勤
			map.put("achuqin", intimework!=null?intimework.toString():"0");  //实际出勤
			map.put("nday", nday!=null?nday.toString():"0"); //正常打卡天数
			map.put("atime", intimemin!=null?intimemin.toString():"0"); //实际工时
			map.put("latecount",latecount[0]!=null?latecount[0].toString():"0"); //迟到次数
			map.put("earlycount",earlycount[0]!=null?earlycount[0].toString():"0"); //早退次数
			map.put("noncount", count!=null?count.toString():"0"); //旷工次数
			map.put("qjdaty", outdays!=null?outdays.toString():"0"); //请假天数
			map.put("overtime",otime!=null?otime.toString():"0"); //加班时数
			map.put("outdays", awaydays!=null?awaydays.toString():"0"); //出差天数
			map.put("outcount", outcount!=null?outcount.toString():"0");	//外勤次数
			map.put("intimemin", intimemin!=null?intimemin.toString():"0");
			map.put("signcard", signcard.toString());
			othersLists.add(map);

		}
		modelMap.put("datas", othersLists);

		return modelMap;
	}
	
	/**
	 * 查询某月有效打卡记录
	 * @param em_code	员工Code
	 * @param date		日期：2018-05
	 * @return
	 */
	public List<Map<String, Object>> effectiveWorkdata(String em_code, String date) {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT TO_CHAR(AD_INDATE,'YYYY-MM-DD') DATES, WD_ONDUTYONE,WD_OFFDUTYONE,AD_ONDUTYONE,AD_OFFDUTYONE,"); 
		sql.append(" WD_ONDUTYTWO,WD_OFFDUTYTWO,AD_ONDUTYTWO,AD_OFFDUTYTWO,WD_ONDUTYTHREE,WD_OFFDUTYTHREE,AD_ONDUTYTHREE,AD_OFFDUTYTHREE");
		sql.append(" FROM ATTENDDATA LEFT JOIN WORKDATE ON AD_BC=WD_CODE ");
		sql.append(" WHERE AD_EMCODE='"+em_code+"' AND TO_CHAR(AD_INDATE,'YYYY-MM') = '"+date+"' ");
		String month = DateUtil.format(new Date(), "yyyy-MM");
		String today = DateUtil.format(new Date(), "yyyy-MM-dd");
		if(!month.equals(date)){
			sql.append(" ORDER BY AD_INDATE");
			list = baseDao.queryForList(sql.toString());
		}else{		//如果查询的是本月的打卡记录
			sql.append(" AND TRUNC(AD_INDATE) < TRUNC(SYSDATE) ORDER BY AD_INDATE");
			list = baseDao.queryForList(sql.toString());
			//给list增加当日打卡记录
			String todaySql = "select WD_ONDUTYONE,WD_OFFDUTYONE,(case when (minCL_TIME <  wd_offdutyone) then minCL_TIME else '' end ) AD_ONDUTYONE,"
							+ "'' AD_OFFDUTYONE,wd_ondutytwo,wd_offdutytwo,(case when (maxCL_TIME >  wd_ondutytwo) then maxCL_TIME else '' end ) ad_ondutytwo,'' ad_offdutytwo"
							+ " from ("
							+ " select max(wd_ondutyone) wd_ondutyone,max(wd_offdutyone) wd_offdutyone,max(wd_ondutytwo) wd_ondutytwo,max(wd_offdutytwo) wd_offdutytwo,"
							+ " max(wd_ondutythree) wd_ondutythree,max(wd_offdutythree) wd_offdutythree,min(to_char(CL_TIME,'hh24:mi')) minCL_TIME ,max(to_char(CL_TIME,'hh24:mi') ) maxCL_TIME"
							+ " from empworkdate left join workdate on wd_code = ew_wdcode "
							+ " left join cardlog on cl_emcode = ew_emcode and to_char(EW_DATE,'yyyy-mm-dd') = to_char(CL_TIME,'yyyy-mm-dd')"
							+ " where nvl(CL_ADDRESS,' ')<>' ' and nvl(cl_emcode,' ') = '"+em_code+"' and to_char(EW_DATE,'yyyy-mm-dd') = '"+today+"' order by CL_TIME)";
			List<Map<String, Object>> todayList = baseDao.queryForList(todaySql);
			if(todayList != null && todayList.size() > 0){
				todayList.get(0).put("DATES", today);
			}
			list.addAll(todayList);
		}
		Object[] earlyAndlate = baseDao.getFieldsDataByCondition("mobile_attendsystem", new String[] {
				"MA_EARLYTIME","MA_LATETIME"},"1=1");
		//构造JSON数据
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		Map<String, Object> resultMap = null;
		Map<String, Object> tmpMap = null;
		for(Map<String, Object> map : list){
			resultMap = new HashMap<String, Object>();
			resultMap.put("isWorkDate", true);
			resultMap.put("date", map.get("DATES"));
			resultMap.put("early", earlyAndlate==null ? 0 : earlyAndlate[0]);
			resultMap.put("late", earlyAndlate==null ? 0 : earlyAndlate[1]);
			tmpMap = new HashMap<String, Object>();
			//班次1
			tmpMap.put("wd_onduty", map.get("WD_ONDUTYONE"));
			tmpMap.put("wd_offduty", map.get("WD_OFFDUTYONE"));
			tmpMap.put("wd_onduty_sign", map.get("AD_ONDUTYONE"));
			tmpMap.put("wd_offduty_sign", map.get("AD_OFFDUTYONE"));
			if(map.get("AD_ONDUTYONE") == null || "".equals(map.get("AD_ONDUTYONE"))){			//正班1上班是否有补卡记录
				String signsql = "select count(*) from mobile_signcard WHERE MS_EMCODE ='"+em_code+"' AND MS_STATUS<>'已审核' "
						+ "AND TO_CHAR(MS_SIGNTIME,'YYYY-MM-DD') = '"+map.get("DATES")+"' and TO_CHAR(MS_SIGNTIME,'HH24:MI')='"+map.get("WD_ONDUTYONE")+"'";
				int count = baseDao.getCount(signsql);
				tmpMap.put("onduty_apprecord", count > 0 ? true : false);
				signsql = "select count(*) from mobile_signcard WHERE MS_EMCODE ='"+em_code+"' AND MS_STATUS='已审核' "
						+ "AND TO_CHAR(MS_SIGNTIME,'YYYY-MM-DD') = '"+map.get("DATES")+"' and TO_CHAR(MS_SIGNTIME,'HH24:MI')='"+map.get("WD_ONDUTYONE")+"'";
				count = baseDao.getCount(signsql);
				if(count > 0){
					tmpMap.put("wd_onduty_sign", map.get("WD_ONDUTYONE"));
				}
			}else{
				tmpMap.put("onduty_apprecord", false);
			}
			
			if(map.get("AD_OFFDUTYONE") == null || "".equals(map.get("AD_OFFDUTYONE"))){			//正班1下班是否有补卡记录
				String signsql = "select count(*) from mobile_signcard WHERE MS_EMCODE ='"+em_code+"' AND MS_STATUS<>'已审核' "
						+ "AND TO_CHAR(MS_SIGNTIME,'YYYY-MM-DD') = '"+map.get("DATES")+"' and TO_CHAR(MS_SIGNTIME,'HH24:MI')='"+map.get("WD_OFFDUTYONE")+"'";
				int count = baseDao.getCount(signsql);
				tmpMap.put("offduty_apprecord", count > 0 ? true : false);
				signsql = "select count(*) from mobile_signcard WHERE MS_EMCODE ='"+em_code+"' AND MS_STATUS='已审核' "
						+ "AND TO_CHAR(MS_SIGNTIME,'YYYY-MM-DD') = '"+map.get("DATES")+"' and TO_CHAR(MS_SIGNTIME,'HH24:MI')='"+map.get("WD_OFFDUTYONE")+"'";
				count = baseDao.getCount(signsql);
				if(count > 0){
					tmpMap.put("wd_offduty_sign", map.get("WD_OFFDUTYONE"));
				}
			}else{
				tmpMap.put("offduty_apprecord", false);
			}
			resultMap.put("class1", tmpMap);
			
			//班次2
			tmpMap = new HashMap<String, Object>();
			tmpMap.put("wd_onduty", map.get("WD_ONDUTYTWO"));
			tmpMap.put("wd_offduty", map.get("WD_OFFDUTYTWO"));
			tmpMap.put("wd_onduty_sign", map.get("AD_ONDUTYTWO"));
			tmpMap.put("wd_offduty_sign", map.get("AD_OFFDUTYTWO"));
			if(map.get("AD_ONDUTYTWO") == null || "".equals(map.get("AD_ONDUTYTWO"))){			//正班2上班是否有补卡记录
				String signsql = "select count(*) from mobile_signcard WHERE MS_EMCODE ='"+em_code+"' AND MS_STATUS<>'已审核' "
						+ "AND TO_CHAR(MS_SIGNTIME,'YYYY-MM-DD') = '"+map.get("DATES")+"' and TO_CHAR(MS_SIGNTIME,'HH24:MI')='"+map.get("WD_ONDUTYTWO")+"'";
				int count = baseDao.getCount(signsql);
				tmpMap.put("onduty_apprecord", count > 0 ? true : false);
				signsql = "select count(*) from mobile_signcard WHERE MS_EMCODE ='"+em_code+"' AND MS_STATUS='已审核' "
						+ "AND TO_CHAR(MS_SIGNTIME,'YYYY-MM-DD') = '"+map.get("DATES")+"' and TO_CHAR(MS_SIGNTIME,'HH24:MI')='"+map.get("WD_ONDUTYTWO")+"'";
				count = baseDao.getCount(signsql);
				if(count > 0){
					tmpMap.put("wd_onduty_sign", map.get("WD_ONDUTYTWO"));
				}
			}else{
				tmpMap.put("onduty_apprecord", false);
			}
			if(map.get("AD_OFFDUTYTWO") == null || "".equals(map.get("AD_OFFDUTYTWO"))){			//正班2下班是否有补卡记录
				String signsql = "select count(*) from mobile_signcard WHERE MS_EMCODE ='"+em_code+"' AND MS_STATUS<>'已审核' "
						+ "AND TO_CHAR(MS_SIGNTIME,'YYYY-MM-DD') = '"+map.get("DATES")+"' and TO_CHAR(MS_SIGNTIME,'HH24:MI')='"+map.get("WD_OFFDUTYTWO")+"'";
				int count = baseDao.getCount(signsql);
				tmpMap.put("offduty_apprecord", count > 0 ? true : false);
				signsql = "select count(*) from mobile_signcard WHERE MS_EMCODE ='"+em_code+"' AND MS_STATUS='已审核' "
						+ "AND TO_CHAR(MS_SIGNTIME,'YYYY-MM-DD') = '"+map.get("DATES")+"' and TO_CHAR(MS_SIGNTIME,'HH24:MI')='"+map.get("WD_OFFDUTYTWO")+"'";
				count = baseDao.getCount(signsql);
				if(count > 0){
					tmpMap.put("wd_offduty_sign", map.get("WD_OFFDUTYTWO"));
				}
			}else{
				tmpMap.put("offduty_apprecord", false);
			}
			resultMap.put("class2", tmpMap);
			
			//班次3
			tmpMap = new HashMap<String, Object>();
			tmpMap.put("wd_onduty", map.get("WD_ONDUTYTHREE"));
			tmpMap.put("wd_offduty", map.get("WD_OFFDUTYTHREE"));
			tmpMap.put("wd_onduty_sign", map.get("AD_ONDUTYTHREE"));
			tmpMap.put("wd_offduty_sign", map.get("AD_OFFDUTYTHREE"));
			if(map.get("AD_ONDUTYTHREE") == null || "".equals(map.get("AD_ONDUTYTHREE"))){			//正班3上班是否有补卡记录
				String signsql = "select count(*) from mobile_signcard WHERE MS_EMCODE ='"+em_code+"' AND MS_STATUS<>'已审核' "
						+ "AND TO_CHAR(MS_SIGNTIME,'YYYY-MM-DD') = '"+map.get("DATES")+"' and TO_CHAR(MS_SIGNTIME,'HH24:MI')='"+map.get("WD_ONDUTYTHREE")+"'";
				int count = baseDao.getCount(signsql);
				tmpMap.put("onduty_apprecord", count > 0 ? true : false);
				signsql = "select count(*) from mobile_signcard WHERE MS_EMCODE ='"+em_code+"' AND MS_STATUS='已审核' "
						+ "AND TO_CHAR(MS_SIGNTIME,'YYYY-MM-DD') = '"+map.get("DATES")+"' and TO_CHAR(MS_SIGNTIME,'HH24:MI')='"+map.get("WD_ONDUTYTHREE")+"'";
				count = baseDao.getCount(signsql);
				if(count > 0){
					tmpMap.put("wd_onduty_sign", map.get("WD_ONDUTYTHREE"));
				}
			}else{
				tmpMap.put("onduty_apprecord", false);
			}
			if(map.get("AD_OFFDUTYTHREE") == null || "".equals(map.get("AD_OFFDUTYTHREE"))){			//正班3下班是否有补卡记录
				String signsql = "select count(*) from mobile_signcard WHERE MS_EMCODE ='"+em_code+"' AND MS_STATUS<>'已审核' "
						+ "AND TO_CHAR(MS_SIGNTIME,'YYYY-MM-DD') = '"+map.get("DATES")+"' and TO_CHAR(MS_SIGNTIME,'HH24:MI')='"+map.get("WD_OFFDUTYTHREE")+"'";
				int count = baseDao.getCount(signsql);
				tmpMap.put("offduty_apprecord", count > 0 ? true : false);
				signsql = "select count(*) from mobile_signcard WHERE MS_EMCODE ='"+em_code+"' AND MS_STATUS='已审核' "
						+ "AND TO_CHAR(MS_SIGNTIME,'YYYY-MM-DD') = '"+map.get("DATES")+"' and TO_CHAR(MS_SIGNTIME,'HH24:MI')='"+map.get("WD_OFFDUTYTHREE")+"'";
				count = baseDao.getCount(signsql);
				if(count > 0){
					tmpMap.put("wd_offduty_sign", map.get("WD_OFFDUTYTHREE"));
				}
			}else{
				tmpMap.put("offduty_apprecord", false);
			}
			resultMap.put("class3", tmpMap);
			
			result.add(resultMap);
		}
		//增加周六周日打卡记录
		String extraSql = "SELECT TO_CHAR(MIN(CL_TIME),'YYYY-MM-DD') DATES, TO_CHAR(MIN(CL_TIME),'HH24:MI') FIRST,TO_CHAR(MAX(CL_TIME),'HH24:MI') END "
				+ " FROM CARDLOG LEFT JOIN EMPLOYEE ON CL_EMNAME=EM_NAME WHERE EM_CODE='"+em_code+"' "
				+ " AND CL_TIME BETWEEN ADD_MONTHS(LAST_DAY(TO_DATE('"+date+"','YYYY-MM')),-1)+1 AND last_day(to_date('"+date+"','YYYY-MM')) "
				+ " AND (TO_CHAR(CL_TIME,'DAY')='星期六' or TO_CHAR(CL_TIME,'DAY')='星期日') GROUP BY TO_CHAR(CL_TIME,'YYYY-MM-DD')";
		List<Map<String, Object>> extraResult = baseDao.queryForList(extraSql);
		boolean repeat = false;
		for(Map<String, Object> extraMap : extraResult){
			for(Map<String, Object> tmpResultMap : result){
				if(tmpResultMap.get("date").equals(extraMap.get("DATES"))){
					repeat = true;
				}
			}
			if(!repeat){
				resultMap = new HashMap<String, Object>();
				resultMap.put("isWorkDate", false);
				resultMap.put("date", extraMap.get("DATES"));
				tmpMap = new HashMap<String, Object>();
				tmpMap.put("wd_onduty_sign", extraMap.get("FIRST"));
				if(extraMap.get("FIRST").equals(extraMap.get("END"))){
					tmpMap.put("wd_offduty_sign", null);
				}else{
					tmpMap.put("wd_offduty_sign", extraMap.get("END"));
				}
				resultMap.put("class1", tmpMap);
				result.add(resultMap);
			}
			repeat = false;
		}
		//增加申诉中状态
		
		return result;
	}
	
}
