package com.uas.erp.service.oa.impl;

import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.DateUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.service.oa.checkResultService;

@Service
public class CheckResultServiceImpl implements checkResultService {
	
	static final String selectEM = "select em_code from employee order by em_code";
	static final String getCheckType = "slect * from Checktype where ct_code=?";
	static final String getCheck = "select em_checktype from employee where em_code=?";
	static final String getallType = "select * from checktype";
	static final String insertSql = "insert into AttendData(ad_id,ad_emcode,to_date(ad_indate,'yyyy-MM-dd'),ad_kind,ad_ondutyone,ad_offdutyone," +
			"ad_ondutytwo,ad_offdutytwo,ad_ondutythree,ad_offdutythree,ad_overtimemin,ad_latemin,ad_leaveearlymin," +
				"ad_gradetype,ab_allworktime)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	@Autowired
	private BaseDao baseDao;
	
	@Override
	public void getcheckresult(String language, Employee employee,String firstDate,String secondDate) {
		
		SqlRowList sqlData;
		String emCode = null;
		String emCheckType = null;
		String getCountByDate = "select trunc(to_date('" +firstDate + "','yyyy-MM-dd')-to_date('" +
				secondDate + "','yyyy-MM-dd')) from dual";
		//要计算考勤的天数
		int days = baseDao.getCount(getCountByDate);
		JSONArray allArray = getAllEmCheckType(getallType);
		JSONArray array = new JSONArray();
		String getData;
		String adKind;
		String lastDate;
		int j,k = 0;
		long leaEarMin ,lateAllmin,attendanceTime ;
		//标准的上下班时间，包括加班时间
		String firstInTime,firstOffTime,secondInTime,secondOffTime,thirdInTime,thirdOffTime;
		String firstInTime2,firstOffTime2,secondInTime2,secondOffTime2,thirdInTime2,thirdOffTime2;
		//要插入数据库的上下班时间
		String[] attendTime = new String[5];
		SqlRowList rs = baseDao.queryForRowSet(selectEM);
		while(rs.next()){
			emCode = rs.getString("em_code");
			emCheckType = getEmCheckType(emCode);
			array = getTypeDetail(allArray, emCheckType);
			leaEarMin = 0;
			lateAllmin = 0;
			for(int i=0;i<days;i++){
				getData = "select * from cardrecords where cr_emcode=" + emCode +" and cr_date=to_date('"+ 
						firstDate +"','yyyy-MM-dd')+"+i+" order by cr_id asc";
				sqlData = baseDao.queryForRowSet(getData);
				lastDate = sqlData.getString("cr_date");
				j = sqlData.getKeys().size();
				String[] attendData = new String[j];
				k = 0;
				while(sqlData.next()){
					attendData[k] = sqlData.getString(k);
					k++;
				}
				firstInTime = getCompareTime(array, "ct_intimefirst", 0, 0);
				firstInTime2 = getCompareTime(array, "ct_intimefirst", 0, 1);
				attendTime[0] = getInData(firstInTime, firstInTime2, attendData);
				firstOffTime = getCompareTime(array, "ct_offtimefirst", 1, 2);
				firstOffTime2 = getCompareTime(array, "ct_offtimefirst", 1,3);
				attendTime[1] = getInData(firstOffTime, firstOffTime2, attendData);
				if(lateMin(attendTime[0], firstInTime2)>0){
					lateAllmin = lateMin(attendTime[0], firstInTime2)+lateAllmin;
				}
				if(leaveEarlyTime(attendTime[1], firstOffTime)<0){
					leaEarMin = leaveEarlyTime(attendTime[1], firstOffTime)+leaEarMin;
				}
				secondInTime = getCompareTime(array, "ct_intimesecond", 2, 0);
				secondInTime2 = getCompareTime(array, "ct_intimesecond", 2, 1);
				attendTime[2] = getInData(secondInTime, secondInTime2, attendData);
				
				secondOffTime = getCompareTime(array, "ct_intimesecond", 3, 2);
				secondOffTime2 = getCompareTime(array, "ct_intimesecond", 3, 3);
				attendTime[3] = getInData(secondOffTime, secondOffTime2, attendData);
				if(lateMin(attendTime[2],secondInTime)>0){
					lateAllmin = lateMin(attendTime[0], firstInTime2)+lateAllmin;
				}
				if(leaveEarlyTime(attendTime[3], secondOffTime)<0){
					leaEarMin = leaveEarlyTime(attendTime[3], secondOffTime)+leaEarMin;
				}
				
				thirdInTime = getCompareTime(array, "ct_intimethird", 4, 0);
				thirdInTime2 = getCompareTime(array, "ct_intimethird", 4, 1);
				attendTime[4] = getInData(thirdInTime, thirdInTime2, attendData);
				
				thirdOffTime = getCompareTime(array, "ct_offtimethird", 5, 2);
				thirdOffTime2 = getCompareTime(array, "ct_offtimethird", 5, 3);
				attendTime[5] = getInData(thirdOffTime, thirdOffTime2, attendData);
				
				attendanceTime = attendtime(attendTime);
				if(attendanceTime ==0){
					adKind = "异常";
				} else {
					adKind = "正常";
				}
				baseDao.execute(insertSql, new Object[]{baseDao.getSeqId("AttendData_SEQ"),emCode,lastDate,adKind,
					attendTime[0],attendTime[1],attendTime[2],attendTime[3],attendTime[4],attendTime[5],'0',
						lateAllmin,leaEarMin,emCheckType,attendanceTime});
			}
		}
	}

	public String getEmCheckType(String code){
		
		SqlRowList sqlRowList = baseDao.queryForRowSet(getCheckType,code);
		String emCheckType = null;
		while(sqlRowList.next()){
			 emCheckType = sqlRowList.getString("em_checktype");
		}
		return emCheckType;
	}

	/**
	 * jsonarray 存储所有的班次类型，后面作比较需要
	 * @param sql
	 * @return
	 */
	public JSONArray getAllEmCheckType(String sql){
		SqlRowList sqlRowList = baseDao.queryForRowSet(sql);
		JSONArray jsonArray = new JSONArray();
		JSONArray allJsonArray = new JSONArray();
		JSONObject allJson = new JSONObject();
		JSONObject jsonObject = new JSONObject();
		while(sqlRowList.next()){
			jsonObject.put("ct_intimefirst", sqlRowList.getString("ct_intimefirst"));
			jsonArray.add(jsonObject);
			jsonObject.put("ct_offtimefirst", sqlRowList.getString("ct_offtimefirst"));
			jsonArray.add(jsonObject);
			jsonObject.put("ct_intimesecond", sqlRowList.getString("ct_intimesecond"));
			jsonArray.add(jsonObject);
			jsonObject.put("ct_offtimesecond", sqlRowList.getString("ct_offtimesecond"));
			jsonArray.add(jsonObject);
			jsonObject.put("ct_intimethird", sqlRowList.getString("ct_intimethird"));
			jsonArray.add(jsonObject);
			jsonObject.put("ct_offtimethird", sqlRowList.getString("ct_offtimethird"));
			jsonArray.add(jsonObject);
			jsonObject.put("ct_allowlate", sqlRowList.getString("ct_allowlate"));
			jsonArray.add(jsonObject);
			jsonObject.put("ct_inworkbeaf ",sqlRowList.getString("ct_inworkbeaf"));
			jsonArray.add(jsonObject);
			allJson.put(sqlRowList.getString("ct_code"),jsonArray);
			allJsonArray.add(allJson);
		}
		return allJsonArray;
	}
	
	public JSONArray getTypeDetail(JSONArray allArray,String emCheckType){
		
		JSONArray jsonArray = new JSONArray();
		for(int j=0;j<allArray.size();j++){
			JSONObject jsonObject = allArray.getJSONObject(j);
			jsonArray = jsonObject.getJSONArray(emCheckType);
			if(jsonArray.size()!= 0) {
				break;
			}
		}
		return jsonArray;
	}
	
	/**
	 * 得到要记录的上班下班时间
	 * startDate 最早开始打卡时间
	 * endDate 最迟打卡时间
	 * attendData 一天的打卡记录
	 */
	public String getInData(String startDate,String endDate,String[] attendData){
		
		int i,j = attendData.length;
		long m,n,k;
		String dataDetail = null;
		for (i=0;i<j;i++) {
			dataDetail = attendData[i];
			m = DateUtil.parseStringToDate(startDate, "HH:mm:ss").getTime()-
					DateUtil.parseStringToDate(dataDetail, "HH:mm:ss").getTime();
			n = DateUtil.parseStringToDate(endDate, "HH:mm:ss").getTime()-
					DateUtil.parseStringToDate(dataDetail, "HH:mm:ss").getTime();
			if (m<0&&n>0) {
				break;
			}
		}
		if(dataDetail.equals(null)){
			for (i=0;i<j;i++) {
				k = DateUtil.parseStringToDate(attendData[i], "HH:mm:ss").getTime()-
						DateUtil.parseStringToDate(endDate, "HH:mm:ss").getTime();
				if (k<60*60*1000 &&k>0) {
					dataDetail = attendData[i];
					break;
				}
			}
		}
		return dataDetail;
	}
	
	/**
	 * 得到比较时间
	 * @param array
	 * @param code
	 * @param i ID
	 * @param status 标示起始比较时间和结束比较时间
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String getCompareTime(JSONArray array,String code,int i,int status){
		
		String lastTime;
		lastTime = array.getJSONObject(i).getString(code);
		Date getTime = new Date();
		int newfirst;
		int allowlateMin = array.getJSONObject(6).getInt("ct_allowlate");
		int inworkBeaf = array.getJSONObject(7).getInt("inworkBeaf");
		getTime = DateUtil.parseStringToDate(lastTime, "HH:mm:ss");
		if (status == 0) {
			newfirst = getTime.getMinutes()-inworkBeaf;
			if (newfirst>0) {
				getTime.setMinutes(newfirst);
			} else {
				newfirst = getTime.getMinutes()+60-inworkBeaf;
				getTime.setHours(getTime.getHours()-1);
				getTime.setMinutes(newfirst);
			}
		} else if (status == 1) {
			newfirst = getTime.getMinutes()+allowlateMin;
			getTime.setMinutes(newfirst);
		} else if(status == 2){
			newfirst = getTime.getMinutes()-allowlateMin;
			if (newfirst>0) {
				getTime.setMinutes(newfirst);
			} else {
				newfirst = getTime.getMinutes()+60-allowlateMin;
				getTime.setHours(getTime.getHours()-1);
				getTime.setMinutes(newfirst);
			}
		} else {
			newfirst = getTime.getMinutes()+inworkBeaf;
			if (newfirst>60) {
				newfirst = newfirst -60;
				getTime.setHours(getTime.getHours()+1);
				getTime.setMinutes(newfirst);
			} else {
				getTime.setMinutes(newfirst);
			}
		}
		lastTime = DateUtil.parseDateToString(getTime, "HH:mm:ss");
		return lastTime;
	}
	
	/**
	 * 迟到时间
	 * @param lastTime
	 * @param standardTime
	 * @return
	 */
	public long lateMin(String lastTime,String standardTime){
		long t =0;
		if(!lastTime.equals(null)){
			t = (DateUtil.parseStringToDate(lastTime, "HH:mm:ss").getTime()-
					DateUtil.parseStringToDate(standardTime, "HH:mm:ss").getTime())/60000;
		}
		
		return t;
	}
	
	/**
	 * 早退时间
	 * @param lastTime
	 * @param standardTime
	 * @return
	 */
	public long leaveEarlyTime(String lastTime,String standardTime){
		long t =0;
		if(!lastTime.equals(null)){
			t = (DateUtil.parseStringToDate(standardTime, "HH:mm:ss").getTime()-
					DateUtil.parseStringToDate(lastTime, "HH:mm:ss").getTime())/60000;
		}
		
		return t;
	}
	/**
	 * 出勤时间
	 * @param attendData
	 * @return
	 */
	public long attendtime(String[] attendTime){
		long time = 0;
        if(!attendTime[0].equals(null) &&!attendTime[1].equals(null)
        		&&attendTime[2].equals(null)&&attendTime[3].equals(null)){
        	//假如有加班情况的话
		        	if(!attendTime[4].equals(null) &&!attendTime[5].equals(null)){
		        		time = getAttendanceTime(attendTime[5], attendTime[4]);
		        	}
        	time = getAttendanceTime(attendTime[1], attendTime[0]) +
        			getAttendanceTime(attendTime[3], attendTime[2]) + time;
        }
    	return time;
	}
	
	public long getAttendanceTime(String endTime,String startTime){
		
		long time =0;
		time = (DateUtil.parseStringToDate(endTime, "HH:mm:ss").getTime() -
				DateUtil.parseStringToDate(startTime, "HH:mm:ss").getTime())/60000*60;
		return time;
	}
}

