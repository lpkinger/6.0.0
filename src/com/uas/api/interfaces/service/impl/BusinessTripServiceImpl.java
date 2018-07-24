package com.uas.api.interfaces.service.impl;


import java.io.IOException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.api.interfaces.service.BusinessTripService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.SingleFormItemsService;

import net.sf.json.JSONObject;
@Service
public class BusinessTripServiceImpl implements BusinessTripService{
    
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private SingleFormItemsService singleFormItemsService;


	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Map<String,Object> UpdateOrInsertBussinessTrip(String emcode,String jsonStr,String master,int isLead,String otherContent) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		JSONObject json = JSONObject.fromObject(jsonStr);
		String startTime = "";
		String endTime = "";
		String remark = "";
		if(jsonStr.indexOf("fpd_start_time")>0) {
			startTime = json.getString("fpd_start_time");
		}
		if(jsonStr.indexOf("fpd_end_time")>0) {
			endTime = json.getString("fpd_end_time");
		}
		if(jsonStr.indexOf("fpd_remark")>0) {
			remark = json.getString("fpd_remark");
		}
		String fpdid = json.getString("fpd_id");
		json.remove("fpd_start_time");
		json.remove("fpd_end_time");
		json.remove("fpd_remark");
		boolean bool = baseDao.checkIf("employee", "em_code='"+emcode+"' and nvl(em_class,' ') <>'离职'");
		if(bool) {
			if(isLead==1) {
				JSONObject otherJson = JSONObject.fromObject(otherContent);
				String fpid = "";
				if(otherContent!=null&&!"".equals(otherContent)) {
					fpid = otherJson.getString("fpid");
				}
				if(fpid!=null && !"".equals(fpid)) {
						int feepleaseDetailId = baseDao.getSeqId("feepleaseDetail_seq");
						json.put("fpd_id", feepleaseDetailId);
						json.put("fpd_fpid",fpid);
						jsonStr = json.toString();
						List<String> gridSq = SqlUtil.getInsertSqlbyGridStore(jsonStr, "feepleasedetail", new String (), new Object[] {});
						baseDao.execute(gridSq);
						if(!"".equals(startTime)) {
							baseDao.execute("update feepleasedetail set fpd_start_time=to_date('1970-01-01 08:00:00','yyyy-mm-dd hh24:mi:ss')+"+startTime+"/1000/24/60/60"
									+ " where fpd_id='"+feepleaseDetailId+"'");
						}
						if(!"".equals(endTime)) {
							baseDao.execute("update feepleasedetail set fpd_end_time=to_date('1970-01-01 08:00:00','yyyy-mm-dd hh24:mi:ss')+"+endTime+"/1000/24/60/60"
									+ " where fpd_id='"+feepleaseDetailId+"'");
						}
						if(!"".equals(remark)) {
							baseDao.execute("update feepleasedetail set fpd_remark=trim(fpd_remark)||'  "+remark+"' where fpd_id='"+feepleaseDetailId+"'");
						}
						modelMap.put("outOrderno", feepleaseDetailId);
					
				}else {
					StringBuffer feepleased = new StringBuffer();
					int feepleaseId = baseDao.getSeqId("FeePlease_seq");
					String feepleaseCode = singleFormItemsService.getCodeString("FeePlease!CCSQ!new", "FeePlease", 2);
					Employee employee = employeeService.getEmployeeByEmcode(emcode);
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
					int periodTime = 0;
					if(("".equals(startTime)||startTime==null)&&("".equals(endTime)||endTime==null)) {//当开始时间和结束时间为空，设置默认值
						startTime = format.format(new Date());
						endTime = format.format(getNextDay(new Date()));
						periodTime = 1;
					}else {	
						if(Long.valueOf(endTime)>=Long.valueOf(startTime)){//当开始时间小于结束时间，结束时间设置成开始时间后的一天
							Date start = new Date();
							start.setTime(Long.valueOf(startTime));
							startTime =format.format(start);
							endTime = format.format(getNextDay(start));
							periodTime = 1;
						}else {
							Date start = new Date();
							Date end = new Date();
							start.setTime(Long.valueOf(startTime));
							startTime =format.format(start);
							end.setTime(Long.valueOf(endTime));
							endTime = format.format(getNextDay(end));
							periodTime = getPeriodTime(start,end);
						}
					}
					
					//自动生成出差单主表
					feepleased.append("insert into FeePlease(fp_id,fp_code,FP_PEOPLE2,fp_recordman,fp_recorddate,fp_status,FP_V12,fp_department," + 
							"FP_V2,fp_kind,fp_v3,fp_prestartdate,fp_preenddate,FP_N6,fp_auditman,fp_auditdate,fp_statuscode,fp_people3) values("
							+ feepleaseId+",'"+feepleaseCode+"','"+employee.getEm_code()+"','"+employee.getEm_name()+"',sysdate,'已审核','"+employee.getEm_defaultorname()
							+"','"+employee.getEm_depart()+"','"+employee.getEm_position()+"','出差申请单','客户往来',to_date('"+startTime+"','yyyy-mm-dd hh24:mi:ss'),to_date('"+endTime+"','yyyy-mm-dd hh24:mi:ss'),"
							+ periodTime+",'"+employee.getEm_name()+"',sysdate,'AUDITED','"+employee.getEm_code()+"')");
					baseDao.execute(feepleased.toString());
					
					//将新的明细表数据加进来
					json.put("fpd_fpid", feepleaseId);
					int feepleaseDetailId = baseDao.getSeqId("feepleaseDetail_seq");
					json.put("fpd_id", feepleaseDetailId);
					jsonStr = json.toString();
					Map<Object, Object> store = BaseUtil.parseFormStoreToMap(jsonStr);
					String sql = SqlUtil.getInsertSqlByMap(store, "FEEPLEASEDETAIL");
					baseDao.execute(sql);
					if(!"".equals(startTime)) {
						baseDao.execute("update feepleasedetail set fpd_start_time=to_date('"+startTime+"','yyyy-mm-dd hh24:mi:ss')"
								+ " where fpd_id='"+feepleaseDetailId+"'");
					}
					if(!"".equals(endTime)) {
						baseDao.execute("update feepleasedetail set fpd_end_time=to_date('"+endTime+"','yyyy-mm-dd hh24:mi:ss')"
								+ " where fpd_id='"+feepleaseDetailId+"'");
					}
					if(!"".equals(remark)) {
						baseDao.execute("update feepleasedetail set fpd_remark=trim(fpd_remark)||'  "+remark+"' where fpd_id='"+feepleaseDetailId+"'");
					}
					modelMap.put("outOrderno", feepleaseDetailId);
				}
				modelMap.put("success", true);
				return modelMap;
			
			}else{
				jsonStr = json.toString();
				List<String> gridSq = SqlUtil.getUpdateSqlbyGridStore(jsonStr, "feepleasedetail", "fpd_id");
				baseDao.execute(gridSq);
				if(!"".equals(startTime)) {
					baseDao.execute("update feepleasedetail set fpd_start_time=to_date('1970-01-01 08:00:00','yyyy-mm-dd hh24:mi:ss')+"+startTime+"/1000/24/60/60"
							+ " where fpd_id='"+fpdid+"'");
				}
				if(!"".equals(endTime)) {
					baseDao.execute("update feepleasedetail set fpd_end_time=to_date('1970-01-01 08:00:00','yyyy-mm-dd hh24:mi:ss')+"+endTime+"/1000/24/60/60"
							+ " where fpd_id='"+fpdid+"'");
				}
				if(!"".equals(remark)) {
					baseDao.execute("update feepleasedetail set fpd_remark=trim(fpd_remark)||'  "+remark+"' where fpd_id='"+fpdid+"'");
				}
				modelMap.put("outOrderno", fpdid);
				modelMap.put("success", true);
				return modelMap;
			}
		}else {
			modelMap.put("error", "请确定人员资料是否存在或者人员资料是否离职");
			return modelMap;
		}
		
	}
	//获取下一天
	private  Date getNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, +1);//+1今天的时间加一天
        date = calendar.getTime();
        return date;
    }
	//取两个时间的天数差
	private  int getPeriodTime(Date date1,Date date2)
    {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
       int day1= cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);
        
        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if(year1 != year2)   //同一年
        {
            int timeDistance = 0 ;
            for(int i = year1 ; i < year2 ; i ++)
            {
                if(i%4==0 && i%100!=0 || i%400==0)    //闰年            
                {
                    timeDistance += 366;
                }
                else    //不是闰年
                {
                    timeDistance += 365;
                }
            }
            
            return timeDistance + (day2-day1) ;
        }
        else    //不同年
        {
            System.out.println("判断day2 - day1 : " + (day2-day1));
            return day2-day1;
        }
    }
	@Override
	public List<Map<String,Object>>  getEmployee(String emcode,String master) {
		boolean bool = baseDao.checkIf("employee", "em_code='"+emcode+"' and nvl(em_class,' ')<>'离职'");
		if(bool) {
			List<Map<String,Object>> employee = baseDao.queryForList("SELECT em_id,em_code,em_name,em_cttpId,em_sex," + 
					" em_mobile,em_type,em_enid,em_cop,em_position,em_depart,em_birthday,em_email,em_iccode,em_class from employee "+
					" where em_code='"+emcode+"' and nvl(em_class,' ')<>'离职'"); 
			return employee;
		}
		return null;
	}

	@Override
	public boolean updateEmployee(String master,String params) {
		List<Map<Object, Object>> em = BaseUtil.parseGridStoreToMaps(params);
		for (Map<Object, Object> map : em) {
			Object emid = map.get("em_id");
			Object emcttpid = map.get("em_cttpId");
			try {
				baseDao.execute("update employee set em_cttpId="+emcttpid +" where em_id=(select em_id from employee where  nvl(em_class,' ')<>'离职' and em_id="+emid+")");
			}catch(Exception e) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Map<String,Object> getDepartment(String dept) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		try {
			Object[] url = baseDao.getFieldsDataByCondition("tp_url", new String[] {"tu_url","TU_PARAMS1","TU_PARAMS2"}, "tu_name='deptquery' and tu_valid=1");
			Object deartid = baseDao.getFieldDataByCondition("tp_auvgo_employee", "TA_DEPTID", "TA_DEPTID='"+dept+"'");

			String data = createDept(String.valueOf(deartid));
			String sign = createSign(data,String.valueOf(url[2]));
			String param  = "appkey="+url[1]+"&sign="+sign+"&data="+URLEncoder.encode(data, "UTF-8");
			Map<String,String> map = new HashMap<String, String>();
			Response response = HttpUtil.sendPostRequest(String.valueOf(url[0])+"?"+param,map);
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String text = response.getResponseText();
				if (StringUtil.hasText(text)) {
					if(text.indexOf("200")>0) {
						modelMap.put("success", true);
						modelMap.put("data", text);
					}else {
						modelMap.put("success", false);
						modelMap.put("data", text);
					}
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
			modelMap.put("error",e.getMessage());
			return modelMap;
		}
		return modelMap;
	}
	
	private static String createDept(String dept) {
		return "{\"bianhao\":\""+dept+"\"}";
	}
	private static String createNewDept(String dept,String deptname) {
		return "{\"deptlist\":[{\"name\":\""+deptname+"\",\"bianhao\":\""+dept+"\",\"pid\":\"\"}]";
	}
	private static String createNewEmp(String dept,String code,String name) {
		return "{\"name\":\""+name+"\",\"deptid\":\""+dept+"\",\"accno\":\""+code+"\"}";
	}
	public static String createSign(String dataJson, String appsecret) throws SQLException,IOException{
		String key = MD5Encode(appsecret).toUpperCase();
		String sign = MD5Encode(key + dataJson);
		return sign;
	}
	private static String MD5Encode(String origin) {
		String resultString = null;
		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToHexString(md.digest(resultString.getBytes("utf-8")));
		} catch (Exception exception) {
		}
		return resultString;
	}
	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n += 256;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}
	private static String byteArrayToHexString(byte b[]) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++)
			resultSb.append(byteToHexString(b[i]));

		return resultSb.toString();
	}
	private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	@Override
	public boolean newDepartment(String dept,String deptname) {
		try {
			Object[] url = baseDao.getFieldsDataByCondition("tp_url", new String[] {"tu_url","TU_PARAMS1","TU_PARAMS2"}, "tu_name='deptop' and tu_valid=1");
			
			String data = createNewDept(dept,deptname);
			String sign = createSign(data,String.valueOf(url[2]));
			String param  = "appkey="+url[1]+"&sign="+sign+"&data="+URLEncoder.encode(data, "UTF-8");
			Map<String,String> map = new HashMap<String, String>();
			Response response = HttpUtil.sendPostRequest(String.valueOf(url[0])+"?"+param,map);
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String text = response.getResponseText();
				if (StringUtil.hasText(text)) {
					System.out.println(text);
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public Map<String,Object> getEmp(String dept, String code) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		try {
			Object[] url = baseDao.getFieldsDataByCondition("tp_url", new String[] {"tu_url","TU_PARAMS1","TU_PARAMS2"}, "tu_name='getemp' and tu_valid=1");
			String name = String.valueOf(baseDao.getFieldDataByCondition("employee", "em_name", "em_code='"+code+"'"));
			String data = createNewEmp(dept,"","");
			String sign = createSign(data,String.valueOf(url[2]));
			String param  = "appkey="+url[1]+"&sign="+sign+"&data="+URLEncoder.encode(data, "UTF-8");
			Map<String,String> map = new HashMap<String, String>();
			Response response = HttpUtil.sendPostRequest(String.valueOf(url[0])+"?"+param,map);
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 下载成功
				String text = response.getResponseText();
				if (StringUtil.hasText(text)) {
					if(text.indexOf("200")>0) {
						modelMap.put("success", true);
						modelMap.put("data", text);
					}else {
						modelMap.put("success", false);
						modelMap.put("data", text);
					}
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
			modelMap.put("error",e.getMessage());
			return modelMap;
		}
		return modelMap;
	}

	 
}
