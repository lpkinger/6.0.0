package com.uas.mobile.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.common.EmployeeService;
import com.uas.mobile.service.MobileSalaryService;

@Service("mobileSalaryService")
public class MobileSalaryServiceImpl implements MobileSalaryService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private EmployeeService employeeService;
	
	private final String DATA = "SL_NUMBER1,SL_NUMBER2,SL_NUMBER3,SL_NUMBER4,SL_NUMBER5,SL_BASICSAL,SL_SKILLSAL,SL_PERFORMANCESAL,SL_PERCENTAGESAL,SL_BONUS,SL_TRAFFICSUB,"
			+ "SL_PHONESUB,SL_OVERTIMESUB,SL_FOODSUB,SL_HOUSESUB,SL_HOTSUB,SL_OTHERSUB,SL_SHOULDPAY,SL_UNJOBINSURANCE,SL_OLDINSURANCE,SL_MEDICALINSURANCE,SL_HURTINSURANCE,SL_BIRTHINSURANCE,SL_HOUSINGFUND,SL_TAX,SL_WATERFEE,SL_ELECTRICFEE,SL_BOARDFEE,SL_REALPAY";
	
	@Override
	public Map<String, Object> getEmSalary(String emcode, String date,String phone) {
		Map<String,Object> model=new HashMap<String,Object>();
		Map<String,Object> cf=null;
		Map<String,Object> data=null;
		List<Map<String,Object>>cfs=new LinkedList<Map<String,Object>>();
		StringBuffer sql=new StringBuffer("select ");
		List<Object[]> configs = baseDao.getFieldsDatasByCondition("initdetail",new String[]{"id_field","id_caption","id_type"}, "id_caller='Salary' and id_visible=1 order by id_detno");
		if(configs.size()>0){
			for(Object[] config:configs){
				cf=new HashMap<String,Object>();
				cf.put("Field", config[0]);
				sql.append(config[0]+",");
				cf.put("Caption", config[1]);
				cf.put("Type", config[2]);
				cfs.add(cf);
			}
			sql.append("sl_id,sl_text,sl_result,sl_signature,SL_REMARK,sl_ilid  from salary where sl_emcode='"+emcode+"' and to_char(sl_date,'yyyy-mm')='"+date+"' and sl_status='已发送' order by SL_RECORDDATE desc");
			List<Map<String,Object>> list = baseDao.queryForList(sql.toString());
			ListIterator<Map<String, Object>> iter = list.listIterator();
			data=new HashMap<String,Object>();
			if(iter.hasNext()){
				Map<String,Object> map=iter.next();
				Set<String> keys = map.keySet();
				Object ilid = map.get("SL_ILID");
				for(String key:keys){
					if("SL_RESULT".equals(key)){
						Object value=map.get(key);
						data.put("sl_result",(value==null)?0:value);
					}else{
						Object value = map.get(key);
						if (DATA.contains(key) && !"SL_PHONE".equals(key)) {
							value = deCode(value, ilid == null ? "0" : ilid.toString());
						}
						data.put(key.toLowerCase(), value);
					}
				}
			}
		}
		model.put("configs", cfs);
		model.put("data", data);
		return model;
	}
	
	private static Double  deCode(Object code,String key){
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
		return rev;
	}

	@Override
	public void updateSalary(boolean result, String sl_id, int fp_id,String msg,Employee e) {
		baseDao.updateByCondition("salary", "sl_fpid='"+fp_id+"' ,sl_result="+(result?"1":"-1")+",SL_REMARK="+(msg==null?"SL_REMARK":"'"+msg+"'"), 
				"sl_id="+sl_id);
		baseDao.execute("insert into salarynotelog select salarynotelog_seq.nextval,'"+e.getEm_name()+"','"+(result?"已认领":"报错")+"',+sysdate,"+sl_id+","+(msg==null?"null":"'"+msg+"'")+",sl_date,sl_type  from salary where sl_id="+sl_id);
		if(!result){
			Object[] sal = baseDao.getFieldsDataByCondition("salary", "sl_name,sl_recordercode", "sl_id='"+sl_id+"'");
			if(sal[1]!=null){
				Employee emp = employeeService.getEmployeeByEmcode(sal[1].toString());
				if(emp!=null)
					sendMsg(emp,sal[0]+"认为这次工资内容有误,请及时核对!",sl_id);
			}
		}
	}
	
	private void sendMsg(Employee emp,String msg,String sl_id){
		List<String> sqls=new LinkedList<String>();
		int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
		int ih_id = baseDao.getSeqId("ICQHISTORY_SEQ");
		
		sqls.add("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,PR_FROM,pr_title,pr_caller,pr_keyvalue)"
				+ " select distinct '"+pr_id+"','系统管理员',sysdate,0,'"+msg+"','system',null,'Salary',sl_id from salary where sl_id= "+sl_id);
		sqls.add("insert into pagingreleasedetail(prd_id,prd_prid,prd_recipientid,prd_recipient)"
				+ "select pagingreleasedetail_seq.nextval,"+pr_id+","+emp.getEm_id()+",'"+emp.getEm_name()+"'  from dual");
		sqls.add("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
				+ "select "+ih_id+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id "
				+ "from PAGINGRELEASE where pr_id="+pr_id);
		sqls.add("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
				+ "select ICQHISTORYdetail_seq.nextval,"+ih_id+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail "
				+ "where prd_prid="+pr_id+" and ("+ih_id+",prd_recipient,prd_recipientid) "
				+ "not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");
		baseDao.execute(sqls);
	}

	@Override
	public boolean changePassword(String em_uu, String em_code, String phone,String pwd) {
		boolean flag=false;
		if(pwd!=null&&!"".equals(pwd)){
			StringBuffer sql=new StringBuffer("begin ");
			pwd=pwd.replace("'","''");
			sql.append(" insert into salarypassword select salarypassword_seq.nextval,'"+em_code+"' , '"+pwd+"','"+phone+"','"+em_uu+"','query','AUDITED','已审核',null,null,sysdate  from dual"
					+ " where not exists (select 1 from salarypassword where sp_emcode='"+em_code+"' and sp_phone='"+phone+"');");
			sql.append(" update salarypassword set sp_password='"+pwd+"' where sp_emcode='"+em_code+"' and sp_phone='"+phone+"' ;");
			sql.append(" end;");
			baseDao.execute(sql.toString());
			flag=true;
		}
		return flag;
	}
}
