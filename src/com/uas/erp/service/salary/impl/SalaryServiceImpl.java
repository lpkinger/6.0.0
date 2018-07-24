package com.uas.erp.service.salary.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.exception.SystemException;
import com.uas.erp.core.web.DocumentConfig;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.InitDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.InitData;
import com.uas.erp.model.InitDetail;
import com.uas.erp.model.Salary;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.InitService;
import com.uas.erp.service.common.impl.SalaryInit;
import com.uas.erp.service.salary.SalaryService;


@Service("salaryService")
public class SalaryServiceImpl implements SalaryService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private InitDao initDao;
	@Autowired
	private InitService initService;
	
	private final String DATA = "SL_NUMBER1,SL_NUMBER2,SL_NUMBER3,SL_NUMBER4,SL_NUMBER5,SL_BASICSAL,SL_SKILLSAL,SL_PERFORMANCESAL,SL_PERCENTAGESAL,SL_BONUS,SL_TRAFFICSUB,"
			+ "SL_PHONESUB,SL_OVERTIMESUB,SL_FOODSUB,SL_HOUSESUB,SL_HOTSUB,SL_OTHERSUB,SL_SHOULDPAY,SL_UNJOBINSURANCE,SL_OLDINSURANCE,SL_MEDICALINSURANCE,SL_HURTINSURANCE,SL_BIRTHINSURANCE,SL_HOUSINGFUND,SL_TAX,SL_WATERFEE,SL_ELECTRICFEE,SL_BOARDFEE,SL_REALPAY";
	
	
	@Override
	public void sendMsg(String ilid,String text,String date,int sign) {
		List<String> sqls=new LinkedList<String>();
		if(text!=null&&!"".equals(text)){
			text=text.replace("'", "''");
			sqls.add("update salary  set sl_text='"+text+"' where sl_ilid='"+ilid+"'");
		}
		String msg="你的"+date+"工资条信息已经更新,请及时确认!";
		List<Object> emps = baseDao.getFieldDatasByCondition("salary", "sl_emcode", "sl_ilid='"+ilid+"'");
		sqls.add("update salary  set sl_signature='"+sign+"' where sl_ilid='"+ilid+"'");
		for(Object emp:emps){
			if(emp!=null){
				Employee e = employeeService.getEmployeeByEmcode(emp.toString());
				if(e!=null) {
					sqls.addAll(sendMessage(e,msg,ilid,null));
				}
			}
		}
		baseDao.execute(sqls);
	}

	@Override
	public void saveDate(String ilid, String date, String text,
			Integer signature) {
		List<String> sqls=new LinkedList<String>();
		if(text!=null&&!"".equals(text)){
			text=text.replace("'", "''");
			sqls.add("update salary  set sl_text='"+text+"' where sl_ilid='"+ilid+"'");
		}
		sqls.add("update salary  set sl_signature='"+signature+"' , sl_informdate=to_date('"+date+"','yyyy-mm-dd hh24:mi:ss') where sl_ilid='"+ilid+"'");
		baseDao.execute(sqls);
	}
	
	private List<String> sendMessage(Employee emp,String msg,String ilid,String id){
		List<String> sqls=new LinkedList<String>();
		int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
		int ih_id = baseDao.getSeqId("ICQHISTORY_SEQ");
		
		sqls.add("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,PR_FROM,pr_title,pr_caller)"
				+ " select distinct '"+pr_id+"','系统管理员',sysdate,0,'"+msg+"','system',null,'Salary'  from dual");	
		sqls.add("insert into pagingreleasedetail(prd_id,prd_prid,prd_recipientid,prd_recipient)"
				+ "select pagingreleasedetail_seq.nextval,"+pr_id+",'"+emp.getEm_id()+"','"+emp.getEm_name()+"'  from dual");	
		sqls.add("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
				+ "select "+ih_id+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id "
				+ "from PAGINGRELEASE where pr_id="+pr_id);		
		sqls.add("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
				+ "select ICQHISTORYdetail_seq.nextval,"+ih_id+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail "
				+ "where prd_prid="+pr_id+" and ("+ih_id+",prd_recipient,prd_recipientid) "
				+ "not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");
		if(id==null){
			sqls.add("update salary set sl_status='已发送' where sl_ilid='"+ilid+"' and sl_emcode='"+emp.getEm_code()+"'");
		}else{
			sqls.add("update salary set sl_status='已发送',sl_recorddate=sysdate where sl_id='"+id+"'");
		}
		return sqls;
	}

	@Override
	public Map<String,Object> getHistory(String date, String condition, int start, int end) {
		Map<String,Object> map=new HashMap<String,Object>();
		if (!StringUtil.hasText(condition)) {
			condition = "1=1";
		}
		int count = baseDao.getCount("select count(1) from salary where to_char(sl_date,'yyyymm')='"+date+"' and " + condition );
		try {
			List<Salary> sns = baseDao.getJdbcTemplate()
					.query("select * from (select tt.*,rownum rn from (select * from salary where to_char(sl_date,'yyyymm')=?  and " + condition
							+ "order by sl_detno,SL_RECORDDATE desc,sl_id desc) tt where rownum<="+end+") where rn>"+start,//"select * from salary where to_char(sl_date,'yyyymm')=? order by sl_detno,SL_RECORDDATE desc",
							new BeanPropertyRowMapper<Salary>(Salary.class), date);
			for (Salary sal : sns) {
				//解密处理
				deal(sal);
			}
			map.put("data", sns);
		} catch (EmptyResultDataAccessException exception) {
			map.put("data", null);
		}
		map.put("num",count);
		return map;
	}
	
	private void deal(Salary sal){
		String ilid = sal.getSl_ilid() ==null ? "" : sal.getSl_ilid().toString() ;
		sal.setSl_basicsal(deCode(sal.getSl_basicsal(), ilid));
		sal.setSl_birthinsurance(deCode(sal.getSl_birthinsurance(), ilid));
		sal.setSl_boardfee(deCode(sal.getSl_boardfee(), ilid));
		sal.setSl_bonus(deCode(sal.getSl_bonus(), ilid));
		sal.setSl_electricfee(deCode(sal.getSl_electricfee(), ilid));
		sal.setSl_foodsub(deCode(sal.getSl_foodsub(),ilid));
		sal.setSl_hotsub(deCode(sal.getSl_hotsub(), ilid));
		sal.setSl_housesub(deCode(sal.getSl_housesub(), ilid));
		sal.setSl_housingfund(deCode(sal.getSl_housingfund(), ilid));
		sal.setSl_hurtinsurance(deCode(sal.getSl_hurtinsurance(), ilid));
		sal.setSl_medicalinsurance(deCode(sal.getSl_medicalinsurance(), ilid));
		sal.setSl_number1(deCode(sal.getSl_number1(), ilid));
		sal.setSl_number2(deCode(sal.getSl_number2(), ilid));
		sal.setSl_number3(deCode(sal.getSl_number3(), ilid));
		sal.setSl_number4(deCode(sal.getSl_number4(), ilid));
		sal.setSl_number5(deCode(sal.getSl_number5(), ilid));
		sal.setSl_oldinsurance(deCode(sal.getSl_oldinsurance(), ilid));
		sal.setSl_othersub(deCode(sal.getSl_othersub(), ilid));
		sal.setSl_overtimesub(deCode(sal.getSl_overtimesub(), ilid));
		sal.setSl_percentagesal(deCode(sal.getSl_percentagesal(), ilid));
		sal.setSl_performancesal(deCode(sal.getSl_performancesal(), ilid));
		sal.setSl_phonesub(deCode(sal.getSl_phonesub(), ilid));
		sal.setSl_realpay(deCode(sal.getSl_realpay(), ilid));
		sal.setSl_shouldpay(deCode(sal.getSl_shouldpay(), ilid));
		sal.setSl_skillsal(deCode(sal.getSl_skillsal(), ilid));
		sal.setSl_tax(deCode(sal.getSl_tax(), ilid));
		sal.setSl_trafficsub(deCode(sal.getSl_trafficsub(), ilid));
		sal.setSl_unjobinsurance(deCode(sal.getSl_unjobinsurance(), ilid));
		sal.setSl_waterfee(deCode(sal.getSl_waterfee(), ilid));
	}
	
	private  String  deCode(Object code,String key){
		Double rev = 0.0;
		if(code!=null && !"0".equals(code.toString())){
			BigDecimal a1 = new BigDecimal(code.toString());
			BigDecimal a2 = new BigDecimal(key);
			BigDecimal a3 = new BigDecimal(key.length());
			a1 = a1.multiply(new BigDecimal("100"));
			a1 = a1.divide(a3);
			a1 = a1.subtract(a2);
			rev = a1.doubleValue();
		}
		return rev.toString();
	}
	
	@Override
	public void toFormalData(Employee employee,String date, Integer id, Integer start,
			Integer end,String type) {
		Object obj = baseDao.getFieldDataByCondition("initlog", "il_caller", "il_id=" + id + " AND il_toformal = 0");
		if (obj != null) {
			Object tableName = baseDao.getFieldDataByCondition("InitDetail", "id_table", "id_caller='" + obj + "'");
			if (tableName == null) {
				BaseUtil.showError("未找到配置!");
			} else {
					try {
						List<InitData> datas = initDao.getInitDatas("id_ilid=" + id + " AND id_detno between " + start + " AND " + end);
							SalaryInit init=new SalaryInit(datas,employee,id,date,type);
							init.toFormal();						
					} catch (Exception e) {
						e.printStackTrace();
						throw new SystemException(e.getMessage());
					}
			}
		} else {
			throw new SystemException("数据已删除或已转正式,无法执行转正式数据操作!");
		}
	}

	@Override
	public void reSend(String grid) {
		if(grid!=null&&!"".equals(grid)){
			List<Map<Object,Object>> maps = BaseUtil.parseGridStoreToMaps(grid);
			List<String>sqls=new LinkedList<String>();
			Object id=null;
			Object date=null;
			Object emcode=null;
			String text=null;
			for(Map<Object,Object> map:maps){
				id=map.get("id");
				date=map.get("date");
				emcode=map.get("emcode");
				text="你的"+date+"工资条信息已经更新,请及时确认!";
				if(emcode!=null){
					Employee e = employeeService.getEmployeeByEmcode(emcode.toString());
					if(e!=null&&id!=null) {
						sqls.addAll(sendMessage(e,text,null,id.toString()));
					}
				}
			}
			baseDao.execute(sqls);
		}
	}
	
	@Override
	public void deleteData(String ids) {
		// TODO Auto-generated method stub
		if(ids!=null&&!"".equals(ids)){
			baseDao.execute("delete from salary where sl_id in("+ids+")");
		}
	}

	@Override
	public Map<String, Object> exportAllHis(String date) {
		Map<String,Object>sal=new HashMap<String,Object>();
		List<InitDetail> details = initService.getInitDetails("Salary");
		List<InitDetail> newDetailes = null;
		if (baseDao.isDBSetting("SalaryRequest","notAddFields")) {
			newDetailes = details;
		}else {
			newDetailes=addDetails(details);
		}
		DocumentConfig config = new DocumentConfig();
		List<Map<String, Object>> datas = baseDao.queryForList("select * from salary where to_char(sl_date,'yyyymm')=?", date);
		List<Map<String, Object>> fdatas = format(datas);
		for (InitDetail d : newDetailes) {
			if (d.getId_visible() == 1) {
				if (d.getId_need() ==1 && baseDao.isDBSetting("SalaryRequest", "showNecessary")) {
					config.getHeaders().put(d.getId_field(), d.getId_caption()+"(必填)");
				}				
				else {
					config.getHeaders().put(d.getId_field(), d.getId_caption());		
				} 	
				config.getWidths().put(d.getId_field(), d.getId_width());
				config.getComments().put(d.getId_field(), d.getId_rule());
				if (d.getId_type().startsWith("number")) {
					config.getTypes().put(d.getId_field(), "0.000000");
				} else {
					config.getTypes().put(d.getId_field(), "");
				}	
			}
		}
		//处理与前台显示的不同情况
		for(Map<String, Object> data : fdatas) {
			Object obj = data.get("sl_result");
			if (obj == null || "null".equals(obj) || "".equals(obj)) {
				data.put("sl_result", "未确认");
			}else if ("1".equals(obj.toString())) {
				data.put("sl_result", "已确认");
			}else {
				data.put("sl_result", "报错");
			}
		}
		sal.put("config", config);
		sal.put("datas",fdatas);
		return sal;
	}
	
	//添加列  sl_result
	private List<InitDetail> addDetails(List<InitDetail> details){
		List<InitDetail> newDetails=new LinkedList<InitDetail>();
		for (InitDetail detail : details) {
			newDetails.add(detail);
			if ("sl_phone".equals(detail.getId_field())) {
				InitDetail result = new InitDetail();
				result.setId_caption("确认结果");
				result.setId_field("sl_result");
				result.setId_width(100);
				result.setId_visible(1);
				result.setId_type("varchar2");
				newDetails.add(result);
			}
		}	
		return newDetails;
	}
	
	private List<Map<String,Object>> format(List<Map<String,Object>> maps){
		List<Map<String,Object>>newMaps=new LinkedList<Map<String,Object>>();
		Map<String,Object>m=null;
		for(Map<String,Object>map:maps){
			m=new HashMap<String,Object>();
			Object ilid = map.get("SL_ILID");
			Set<String> keys = map.keySet();
			for(String key:keys){
				if("SL_RECORDDATE".equals(key)||"SL_DATE".equals(key)){
					if(map.get(key)!=null){
						String date=map.get(key).toString().substring(0, 11);
						m.put(key.toLowerCase(), date);
					}
				}else if (DATA.contains(key) && !"SL_PHONE".equals(key)) {
					ilid = ilid == null ? "0" : ilid.toString();
					m.put(key.toLowerCase(), deCode(map.get(key), ilid.toString()));
				}else {
					m.put(key.toLowerCase(), map.get(key));
				}
			}
			newMaps.add(m);
		}
		return newMaps;
	}
	
	@Override
	public Map<String,Object> verify(String phone, String type) {
		Map<String,Object>map=new HashMap<String,Object>();
		if(phone!=null&&!"".equals(phone)){
			String templateId="login".equals(type)?"9f1aa32c-7eaa-4335-9c13-9038d3786416":"557f8d23-6a90-4ca6-ac2c-bdb375929fcc";
			Map<String,Object> params=new HashMap<String,Object>();
			Integer vcode=0;
			while (vcode.toString().length()!=6){
				vcode=(int)(Math.random()*1000000);
			}	
			params.put("templateId",templateId);
			params.put("receiver", phone);
			params.put("params", new String[]{vcode.toString()});
		
			try{
				URL url=new URL("http://message.ubtob.com/sms/send");
			    HttpURLConnection connection = (HttpURLConnection) url.openConnection(); 
	            connection.setDoOutput(true);  
	            connection.setDoInput(true);  
	            connection.setUseCaches(false);  
	            connection.setInstanceFollowRedirects(true);  
	            connection.setRequestMethod("POST"); // 设置请求方式  
	            connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式  
	            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8"); // 设置发送数据的格式  
	            connection.connect();  
	            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8"); // utf-8编码  
	            out.append(BaseUtil.parseMap2Str(params));  
	            out.flush();  
	            out.close();  
	  
	            int code = connection.getResponseCode();  
	            InputStream is = null;  
	            if (code == 200) {  
	                is = connection.getInputStream();  
	            } else {  
	                is = connection.getErrorStream();  
	            }  
	  
	            // 读取响应  
	            int length = (int) connection.getContentLength();// 获取长度  
	            if (length != -1) {  
	                byte[] data = new byte[length];  
	                byte[] temp = new byte[512];  
	                int readLen = 0;  
	                int destPos = 0;  
	                while ((readLen = is.read(temp)) > 0) {  
	                    System.arraycopy(temp, 0, data, destPos, readLen);  
	                    destPos += readLen;  
	                }  
	                String result = new String(data, "UTF-8"); // utf-8编码  
	                Map<Object, Object> res = BaseUtil.parseFormStoreToMap(result);
	                if("1".equals(res.get("success")!=null?res.get("success").toString():"")){
		                map.put("success", true);
	                	map.put("vecode",vcode.toString());
	                }
	            }  		
        } catch (IOException e) {  
        	map.put("success", false);
        }  		
	}
		return map;
	}

	@Override
	public Map<String,Object> login(String emcode, String password,String value,Object ve_code) {
		Map<String,Object> map=new HashMap<String,Object>();
		boolean flag=false;
		if(ve_code!=null){
			if(value.equals(ve_code)){
				password=password.replace("'", "''");
				boolean f = baseDao.checkIf("salarypassword", "sp_emcode='"+emcode+"' and sp_password='"+password+"' and sp_type='admin' and sp_statuscode='AUDITED' ");
				if(f){
					flag=true;
				}else{
					map.put("reason", "密码不正确或者不存在可用账号!");
				}		
			}else{
				map.put("reason", "验证码错误!");
			}
		}
		map.put("success", flag);
		return map;
	}

	@Override
	public Map<String, Object> modifyPwd(String emcode, String password,
			String value, Object ve_code) {
		Map<String,Object> map=new HashMap<String,Object>();
		boolean flag=false;
		if(ve_code!=null){
			if(value.equals(ve_code)){
				password=password.replace("'", "''");
				flag=true;
				baseDao.execute("update salarypassword set sp_password='"+password+"' where sp_emcode='"+emcode+"'");	
			}else{
				map.put("reason", "验证码错误!");
			}
		}
		map.put("success", flag);
		return map;
	}

	@Override
	public Map<String,Object> getMessgeLog(int page, int start, int limit) {
		int num=0;
		List<Map<String, Object>> maps=new LinkedList<Map<String,Object>>();
		Map<String,Object> res=new HashMap<String,Object>();
		num=baseDao.getCount("select count(1) from salarynotelog");
		StringBuffer sql=new StringBuffer("select * from (select tt.*,rownum rn from (select * from salarynotelog ORDER BY SL_DATE DESC) tt where rownum<="+(page*limit)+") where rn>"+start+"");
		 maps= baseDao.queryForList(sql.toString());
		 res.put("logs", maps);		 		
		 res.put("num",num);
	  	return res;	
	}

}
