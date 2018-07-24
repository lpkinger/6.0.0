package com.uas.erp.service.hr.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.DataListDao;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.model.DataList;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.hr.AccountemService;

@Service
public class AccountemServiceImpl implements AccountemService {
	
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private DataListDao  dataListDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private EmployeeDao employeeDao;

	@Override
	@CacheEvict(value={"employees", "employee","empsrelativesettings"}, allEntries=true)
	public void updateAccountById(String formStore,String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//使用employeeDao层获取数据，密码会进行解密
		Object[] ob=employeeDao.getFieldsEmployeeByCondition(new String[]{"nvl(em_password,' ')  AS em_password","em_code"}, "em_id="+store.get("em_id"));
		List<Map<Object,Object>> maps=BaseUtil.parseGridStoreToMaps(gridStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store,maps});
		//密码更新时同步密码(密码字段不为空，且密码不等于原来的密码时)
		if(store.get("em_password").toString()!=null&&!store.get("em_password").toString().equals("")){
			if(!String.valueOf(ob[0]).equals(store.get("em_password").toString())){
				String result = employeeService.updatePwd(caller, ob[0].toString(), store.get("em_password").toString(),store.get("em_id").toString(),ob[1].toString(),ob[0].toString(),store.get("synchronize").toString());
				if(result!=null){
					BaseUtil.showError(result);
				}
			}
		}
		//去除是否同步密码到优软云的字段,和密码字段
		store.remove("synchronize");
		store.remove("em_password");
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Employee", "em_id");
		String conditionStr="";
		List<String>sqls=new ArrayList<String>();
		if(gridStore!=null) {
			baseDao.getSeqId("empsrelativesettings_SEQ");
			for(Map<Object,Object> map:maps){
				Object isuse=map.get("es_isuse");
				Object connector=map.get("es_connector");
				Object values=map.get("es_values");
				Object field=map.get("es_field");
				conditionStr=getConditionStr(field, connector, values);
				map.put("es_conditionstr", conditionStr);
				map.put("es_isuse","true".equals(isuse.toString()) ? 0 : 1);
				sqls.add(SqlUtil.getUpdateSqlByFormStore(map, "empsrelativesettings", "es_id"));
				if("1".equals(isuse) && !checkSql(caller, conditionStr)){
				 BaseUtil.showError("单据"+map.get("es_pagetitle")+"|字段"+field+"设置条件有误!");
				}
				if(map.get("es_id")==null || map.get("es_id")=="" || Integer.parseInt(map.get("es_id").toString())==0)
					sqls.add(SqlUtil.getInsertSql(map, "empsrelativesettings", "es_id"));
			}
			
		}
		baseDao.execute(formSql);
		baseDao.execute(sqls);
		//记录操作
		baseDao.logger.update(caller, "em_id", store.get("em_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store,maps});
	}
	private boolean checkSql(Object caller,String condition){
		  DataList datalist=dataListDao.getDataList(String.valueOf(caller),SpObserver.getSp());
		  return baseDao.checkSQL("select  count(1) from "+datalist.getDl_tablename()+" where "+condition);		
	}
	private String getConditionStr(Object field,Object connector,Object values){
	   if("in".equals(connector) || "not in".equals(connector)){
		   return " "+connector+" ('"+values.toString().replaceAll(",","','")+"')";
	   }else if("like".equals(connector)){
		   return " "+connector+" '%"+values.toString()+"%'";
	   }else return " " +connector+" '"+values+"'";
	}
	@Override
	@CacheEvict(value={"employees", "employee","empsrelativesettings"}, allEntries=true)
	public void copyRelativeSettings(String toobjects, int fromemid,
			String caller) {
		// TODO Auto-generated method stub
		String []arr=toobjects.split(";"),smallstr;
		String orgids="",jobids="",emids="";
	    String querySql="",employeecondition="";
		for(String s:arr){
			smallstr=s.split("#");
			if("org".equals(smallstr[0])){
				orgids+=smallstr[1]+",";
			}else if("job".equals(smallstr[0])){
				jobids+=smallstr[1]+",";
			}else {
				emids+=smallstr[1]+",";		 
			}
		}
		if(orgids.length()>0) {
			orgids=orgids.substring(0, orgids.lastIndexOf(","));
			querySql="select om_emid em_id from hrorgemployees where om_orid in ("+orgids+")";
		}
		if(jobids.length()>0) {
			jobids=jobids.substring(0, jobids.lastIndexOf(","));
			employeecondition+=" NVL(em_defaulthsid,0) in ("+jobids+") ";
		}
		if(emids.length()>0) {
			emids=emids.substring(0, emids.lastIndexOf(","));
			employeecondition+=employeecondition!=""?" or em_id in ("+emids+")":"em_id in ("+emids+")";
		}
		if(querySql!="" && employeecondition!=""){
			querySql+=" UNION select  distinct em_id from employee where "+employeecondition;
		}else if(employeecondition!=""){
			querySql+=" select  distinct em_id from employee where "+employeecondition;
		}
        baseDao.execute("delete empsrelativesettings where es_emid in ("+querySql+") and es_emid <>"+fromemid);   
        baseDao.execute("insert into empsrelativesettings select empsrelativesettings_SEQ.nextval," +
        		"em_id,es_pagecaller,es_connector,es_values,es_kind,es_isuse,es_field,es_pagetitle,es_fieldcaption,es_conditionstr from empsrelativesettings,employee where es_emid="+fromemid +" and em_id in ("+querySql+") and em_id<> "+fromemid);
	}

}
