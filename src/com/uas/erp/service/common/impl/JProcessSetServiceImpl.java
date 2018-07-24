package com.uas.erp.service.common.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.bouncycastle.jce.provider.JDKKeyFactory.RSA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.JProcessDao;
import com.uas.erp.model.DataList;
import com.uas.erp.model.DataListDetail;
import com.uas.erp.model.Employee;
import com.uas.erp.model.JProcessSet;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.common.JProcessSetService;
import com.uas.erp.service.common.ProcessService;
@Service
public class JProcessSetServiceImpl implements JProcessSetService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private JProcessDao jProcessDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private ProcessService processService;
	@Override
	public Map<String, Object> getFormDataByformCondition(String formCondition) {
		//List<Map<String,Object>> data = baseDao.queryForList('select * from JProcessSet')		
		Employee employee = SystemSession.getUser();
		String name = employee.getEm_name();		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Object[] data = baseDao.getFieldsDataByCondition("JProcessSet",
				new String[] { "js_id", "js_caller", "js_formKeyName", "js_formStatusName",
						"js_table", "js_formDetailKey", "js_decisionCondition", "js_decisionVariables",
						"js_bean", "js_formurl", "js_serviceclass",
						"js_auditmethod", "js_notefields", "js_codefield",
						"js_groupby"}, formCondition );			
		modelMap.put("js_id", data[0]);
		modelMap.put("js_caller", data[1]);
		modelMap.put("js_formKeyName", data[2]);
		modelMap.put("js_formStatusName", data[3]);
		modelMap.put("js_table", data[4]);
		modelMap.put("js_formDetailKey", data[5]);
		modelMap.put("js_decisionCondition", data[6]);
		modelMap.put("js_decisionVariables", data[7]);
		modelMap.put("js_bean", data[8]);
		modelMap.put("js_formurl", data[9]);
		modelMap.put("js_serviceclass", data[10]);
		modelMap.put("js_auditmethod", data[11]);
		modelMap.put("js_notefields",data[12]);
		modelMap.put("js_codefield",data[13]);
		modelMap.put("js_groupby",data[14]);
		return modelMap;

	}
	@Override
	public void saveJProcessSet(String caller, String formStore, String param,String language, Employee employee) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("JProcessSet", "js_caller='" + store.get("js_caller") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist", language));
		}
		//保存前先校验该流程设置是否启用，启用了则和form配置比较，校验基本属性字段
		Object[] objs=baseDao.getFieldsDataByCondition("form", "fo_keyfield,fo_codefield,fo_statusfield,fo_detailmainkeyfield", "fo_isautoflow=-1 and fo_flowcaller='"+store.get("js_caller")+"'");
		if(objs != null){
			String err="";
			if(objs[0]!=null && store.get("js_formKeyName")!=null && !objs[0].toString().equalsIgnoreCase(store.get("js_formKeyName").toString())) err+="主表关键字;";
			if(objs[1]!=null && store.get("js_codefield")!=null && !store.get("js_codefield").equals("") && !objs[1].toString().equalsIgnoreCase(store.get("js_codefield").toString()))err+="编号字段;";
			if(objs[2]!=null && store.get("js_formStatusName")!=null && !objs[2].toString().equalsIgnoreCase(store.get("js_formStatusName").toString()))err+="状态字段;";
			if(objs[3]!=null && store.get("js_formDetailKey")!=null && !store.get("js_formDetailKey").equals("") && !objs[3].toString().equalsIgnoreCase(store.get("js_formDetailKey").toString()))err+="关联主表字段;";	
			if(err!="")BaseUtil.showError("请检查："+err);
		}
		//执行保存前的其它逻辑
		/*	handlerService.handler("JProcessSet", "save", "before", new Object[]{formStore, language});*/
		//保存Employee
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "JProcessSet", new String[]{},new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.save", language), 
					BaseUtil.getLocalMessage("msg.saveSuccess", language), "JProcessSet|js_id=" + store.get("js_id")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteJProcessSet(String caller, int id, String language,Employee employee) {
		baseDao.deleteById("JProcessSet", "js_id", id);
		//删除AccountRegisterDetail

		//记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.delete", language), 
				BaseUtil.getLocalMessage("msg.deleteSuccess", language), "JProcessSet|js_id=" + id));

	}

	@Override
	public void updateJProcessSetById(String caller, String formStore,String param, String language, Employee employee) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//保存前先校验该流程设置是否启用，启用了则和form配置比较，校验基本属性字段
				Object[] objs=baseDao.getFieldsDataByCondition("form", "fo_keyfield,fo_codefield,fo_statusfield,fo_detailmainkeyfield", "fo_isautoflow=-1 and fo_flowcaller='"+store.get("js_caller")+"'");
				if(objs != null){
					String err="";
					if(objs[0]!=null && store.get("js_formKeyName")!=null && !objs[0].toString().equalsIgnoreCase(store.get("js_formKeyName").toString())) err+="主表关键字;";
					if(objs[1]!=null && store.get("js_codefield")!=null && !store.get("js_codefield").equals("") && !objs[1].toString().equalsIgnoreCase(store.get("js_codefield").toString()))err+="编号字段;";
					if(objs[2]!=null && store.get("js_formStatusName")!=null && !objs[2].toString().equalsIgnoreCase(store.get("js_formStatusName").toString()))err+="状态字段;";
					if(objs[3]!=null && store.get("js_formDetailKey")!=null && !store.get("js_formDetailKey").equals("") && !objs[3].toString().equalsIgnoreCase(store.get("js_formDetailKey").toString()))err+="关联主表字段;";	
					if(err!="")BaseUtil.showError("请检查："+err);
				}
		
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "JProcessSet", "js_id");
		baseDao.execute(formSql);
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.update", language), 
				BaseUtil.getLocalMessage("msg.updateSuccess", language), "JProcessSet|js_id=" + store.get("js_id")));

	}

	@Override
	public void saveJprocessTemplate(String formStore,String clobtext,String language,Employee employee) {
		// TODO Auto-generated method stub
		jProcessDao.saveJprocessTemplate(formStore,clobtext,language,employee);
	}

	@Override
	public void updateJprocessTemplate(String formStore, String clobtext,String language, Employee employee) {
		// TODO Auto-generated method stub
		jProcessDao.updateJprocessTemplate(formStore,clobtext,language,employee);

	}

	@Override
	public void deleteJprocessTemplate(int id, String language,
			Employee employee) {
		// TODO Auto-generated method stub
		baseDao.deleteById("JprocessTemplate", "pt_id", id);
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.delete", language), 
				BaseUtil.getLocalMessage("msg.deleteSuccess", language), "JprocessTemplate|pt_id=" + id));
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void auditJprocessTemplate(int id, String language, Employee employee) {
		// TODO Auto-generated method stub
		Object status = baseDao.getFieldDataByCondition("JprocessTemplate", "pt_statuscode", "pt_id=" + id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError("只能审核已提交的单据!");
		}
		handlerService.handler("JprocessTemplate", "audit", "before", new Object[]{id, language,employee});
		//执行提交操作
		baseDao.updateByCondition("JprocessTemplate", "pt_statuscode='AUDITED',pt_status='" + 
				BaseUtil.getLocalMessage("AUDITED", language) + "'", "pt_id=" + id);
		/**
		 * 流程模板审核之后  流程设置建立
		 * */
		Object[] data=baseDao.getFieldsDataByCondition("JprocessTemplate", "pt_caller,pt_type,pt_prenum,pt_createobjectsid", "pt_id="+id);
		boolean  bool=baseDao.checkByCondition("jprocessset", "js_caller='"+data[0]+"'");
		if(bool){
			Map<String,Object> map=new HashMap<String, Object>();
			map.put("js_id", baseDao.getSeqId("JPROCESSSET_SEQ"));
			map.put("js_caller", data[0]);
			map.put("js_formurl", "jsps/ma/jprocess/AutoJprocess.jsp?type=1");
			map.put("js_formkeyname", "ap_id");
			map.put("js_formstatusname", "ap_status");
			map.put("js_table", "AUTOPROCESS LEFT JOIN EMPLOYEE ON AP_MANID=EM_ID LEFT JOIN HRORG ON EM_DEFAULTORID=OR_ID");
			map.put("js_decisionCondition","or_level");
			map.put("js_decisionVariables","or_level");
			map.put("js_type", data[1]);
			map.put("js_codefield", "ap_code");
			baseDao.execute(SqlUtil.getInsertSqlByMap(map, "jprocessset"));
		}
		int maxid=baseDao.getSeqId("MAXNUMBERS_SEQ");
		if(data[2]!=null && !" ".equals(data[2])){
			baseDao.execute("insert into maxnumbers(mn_id,mn_maxreturn,mn_tablename,mn_type,mn_leadcode) values ("+maxid+",20,'"+data[0]+"',2,'"+data[2]+"')");
		}
		//生成创建权限
		if(data[3]!=null && !" ".equals(data[3])){
			setProcessPower(data[3],id,"add");
		}
		//记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.audit", language), 
				BaseUtil.getLocalMessage("msg.auditSuccess", language), "JprocessTemplate|pt_id=" + id));
		//执行提交后的其它逻辑
		handlerService.handler("JprocessTemplate", "audit", "after", new Object[]{id, language,employee});
		//
	}

	@Override
	public void resAuditJprocessTemplate(int id, String language,Employee employee) {
		// TODO Auto-generated method stub
		handlerService.handler("JprocessTemplate", "resAudit", "before", new Object[]{id, language,employee});
	    baseDao.deleteByCondition("JprocessSet", "js_caller =(select pt_caller from JprocessTemplate where pt_id="+id+")");
		deleteProcessPower(id);
		//执行提交操作
		baseDao.updateByCondition("JprocessTemplate", "pt_statuscode='ENTERING',pt_status='" + 
				BaseUtil.getLocalMessage("ENTERING", language) + "'", "pt_id=" + id);
		//记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.resAudit", language), 
				BaseUtil.getLocalMessage("msg.resAuditSuccess", language), "JprocessTemplate|pt_id=" + id));
		//执行提交后的其它逻辑
		handlerService.handler("JprocessTemplate", "resAudit", "after", new Object[]{id, language,employee});
	}
	@Override
	public void submitJprocessTemplate(int id, String language,
			Employee employee) {
		// TODO Auto-generated method stub
		Object status = baseDao.getFieldDataByCondition("JprocessTemplate", "pt_statuscode", "pt_id=" + id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError("只能提交在录入的单据!");
		}
		handlerService.handler("JprocessTemplate", "commit", "before", new Object[]{id, language,employee});
		//执行提交操作
		baseDao.updateByCondition("JprocessTemplate", "pt_statuscode='COMMITED',pt_status='" + 
				BaseUtil.getLocalMessage("COMMITED", language) + "'", "pt_id=" + id);
		//记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.submit", language), 
				BaseUtil.getLocalMessage("msg.submitSuccess", language), "JprocessTemplate|pt_id=" + id));
		//执行提交后的其它逻辑
		handlerService.handler("JprocessTemplate", "commit", "after", new Object[]{id, language,employee});
	}

	@Override
	public void resSubmitJprocessTemplate(int id, String language,
			Employee employee) {
		// TODO Auto-generated method stub
		handlerService.handler("JprocessTemplate", "resCommit", "before", new Object[]{id, language,employee});
		//执行提交操作
		baseDao.updateByCondition("JprocessTemplate", "pt_statuscode='ENTERING',pt_status='" + 
				BaseUtil.getLocalMessage("ENTERING", language) + "'", "pt_id=" + id);
		//记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.resSubmit", language), 
				BaseUtil.getLocalMessage("msg.resSubmitSuccess", language), "JprocessTemplate|pt_id=" + id));
		//执行提交后的其它逻辑
		handlerService.handler("JprocessTemplate", "resCommit", "after", new Object[]{id, language,employee});
	}

	@Override
	public void saveAutoJprocess(String formStore, String clobtext,String language, Employee employee) {	
		jProcessDao.saveAutoJprocess(formStore,clobtext,language,employee);
	}

	@Override
	public void updateAutoJprocess(String formStore, String clobtext,String language, Employee employee) {
		// TODO Auto-generated method stub
		jProcessDao.updateAutoJprocess(formStore,clobtext,language,employee);
	}

	@Override
	public void deleteAutoJprocess(int id, String language, Employee employee) {
		// TODO Auto-generated method stub
		Object status = baseDao.getFieldDataByCondition("AUTOPROCESS", "ap_statuscode", "ap_id=" + id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError("只能删除状态为在录入状态的单据!");
		}
		baseDao.deleteById("AUTOPROCESS", "AP_ID", id);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void auditAutoJprocess(int id, String caller,String language, Employee employee) {
		// TODO Auto-generated method stub
		handlerService.handler(caller, "audit", "before", new Object[]{id, language,employee});
		//执行审核操作
		Object status = baseDao.getFieldDataByCondition("AUTOPROCESS", "ap_statuscode", "ap_id=" + id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError("只能审核已提交的单据!");
		}
		baseDao.updateByCondition("AUTOPROCESS", "ap_statuscode='AUDITED',ap_status='" + 
				BaseUtil.getLocalMessage("AUDITED", language) + "'", "ap_id=" + id);
		Object  data=baseDao.getFieldDataByCondition("AUTOPROCESS", "ap_readobjectsid", "ap_id="+id);
		setProcessPower(data, id, "read");
		//记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.audit", language), 
				BaseUtil.getLocalMessage("msg.auditSuccess", language), caller+"|ap_id=" + id));		
		handlerService.handler(caller, "audit", "after", new Object[]{id, language,employee});
	}

	@Override
	public void resAuditAutoJprocess(int id,String caller, String language, Employee employee) {
		// TODO Auto-generated method stub
		Object status = baseDao.getFieldDataByCondition("AUTOPROCESS", "ap_statuscode", "ap_id=" + id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError("只能反审核已审核的单据!");
		}
		handlerService.handler(caller, "resAudit", "before", new Object[]{id, language,employee});
		deleteProcessPower(id);
		//执行提交操作
		baseDao.updateByCondition("AUTOPROCESS", "ap_statuscode='ENTERING',ap_status='" + 
				BaseUtil.getLocalMessage("ENTERING", language) + "'", "ap_id=" + id);
		//记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.resAudit", language), 
				BaseUtil.getLocalMessage("msg.auditSuccess", language), caller+"|ap_id=" + id));
		//执行提交后的其它逻辑
		handlerService.handler(caller, "resAudit", "after", new Object[]{id, language,employee});
	}

	@Override
	public void submitAutoJprocess(int id,String caller, String language, Employee employee) {
		// TODO Auto-generated method stub
		handlerService.handler(caller, "commit", "before", new Object[]{id, language,employee});
		Object[]data=baseDao.getFieldsDataByCondition("AUTOPROCESS", "ap_caller,ap_type,ap_nodeman", "ap_id="+id);
		if(data!=null){
			if("commonuse".equals(data[1])){
				saveJnodePerson(data,id);
			}
		}
		Object status = baseDao.getFieldDataByCondition("AutoProcess", "ap_statuscode", "ap_id=" + id);
		//执行提交操作
		Object name=baseDao.getFieldDataByCondition("AutoProcess", "ap_name", "ap_id="+id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("只能对在录入的单据进行提交", language));
		}
		baseDao.updateByCondition("AUTOPROCESS", "ap_statuscode='COMMITED',ap_status='" + 
				BaseUtil.getLocalMessage("COMMITED", language) + "'", "ap_id=" + id);
		//记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.submit", language), 
				BaseUtil.getLocalMessage("msg.submitSuccess", language), caller+"|ap_id=" + id));
		//执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[]{id, language,employee});
		handlerService.launchProcess(caller, id, name.toString());
	}
	@Override
	public void resSubmitAutoJprocess(int id,String caller, String language, Employee employee) {
		// TODO Auto-generated method stub
		handlerService.handler(caller, "resCommit", "before", new Object[]{id, language,employee});
		baseDao.updateByCondition("AUTOPROCESS", "ap_statuscode='ENTERING',ap_status='" + 
				BaseUtil.getLocalMessage("ENTERING", language) + "'", "ap_id=" + id);
		//记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.resSubmit", language), 
				BaseUtil.getLocalMessage("msg.resSubmitSuccess", language), caller+"|ap_id=" + id));
		//执行提交后的其它逻辑
		handlerService.handler(caller, "resCommit", "after", new Object[]{id, language,employee});
		baseDao.deleteByCondition("jnodeperson", "jp_caller='"+caller+"' and jp_keyvalue="+id);
		try {
			// 删除该单据已实例化的流程
			processService.deletePInstance(id, caller, "resCommit");
		}catch (Exception e) {
		}
	}

	private void saveJnodePerson(Object[] data,int keyvalue) {
		// TODO Auto-generated method stub
		Object defId=baseDao.getFieldDataByCondition("JprocessDeploy", "jd_processdefinitionid", "jd_caller='"+data[0]+"'");
		List<String> Sqls=new ArrayList<String>();
		Object nodeman=data[2];
		if(nodeman!=null && nodeman!=""){
			String []arr=nodeman.toString().split(";");
			for(int i=0;i<arr.length;i++){
				String[] man=String.valueOf(arr[i]).split("#");
				Sqls.add("insert into jnodeperson (jp_id,jp_caller,jp_keyvalue,jp_nodename,jp_processdefid,jp_newnodedealman,jp_newnodedealmanname)values(JNODEPERSON_SEQ.NEXTVAL,'"+data[0]+"',"+keyvalue+",'节点"+(i+1)+"','"+defId+"','"+man[0]+"','"+man[1]+"')");
			}
			baseDao.execute(Sqls);
		}else BaseUtil.showError("请指定流程节点处理人!");

	}
	private void setProcessPower(Object data,int relativeid,String type){
		String []arr=String.valueOf(data).split(";"),smallstr;
		List<String>Sql=new ArrayList<String>();
		for(String s:arr){
			smallstr=s.split("#");
			if("org".equals(smallstr[0])){
				Sql.add("insert into oaprocesshrorgpower (oop_relativeid,oop_orid,oop_powertype) values ("+relativeid+","+smallstr[1]+",'"+type+"')");
			}else if("job".equals(smallstr[0])){
				Sql.add("insert into oaprocessjobpower (ojp_relativeid,ojp_jobid,ojp_powertype) values ("+relativeid+","+smallstr[1]+",'"+type+"')");
			}else {
				Sql.add("insert into oaprocesspersonpower (opp_relativeid,opp_emid,opp_powertype) values ("+relativeid+","+smallstr[1]+",'"+type+"')");		 
			}
		}
		baseDao.execute(Sql);
	}
    private void deleteProcessPower(int relativeid){
    	baseDao.deleteById("oaprocesshrorgpower", "oop_relativeid", relativeid);
    	baseDao.deleteById("oaprocessjobpower", "ojp_relativeid", relativeid);
    	baseDao.deleteById("oaprocesspersonpower", "opp_relativeid", relativeid);
    }

	@Override
	public List<JSONObject> ProcessQueryAgentPersons(String likestring) {
		 List<JSONObject> js=new ArrayList<JSONObject>();
		 Employee employee = SystemSession.getUser();
		 Object agentname = baseDao.getFieldDataByCondition("hrorg", "agentname", "or_id="+employee.getEm_defaultorid());
		 SqlRowList orsl=baseDao.queryForRowSet("select  or_name,or_id from Hrorg where nvl(or_statuscode,' ')<>'DISABLE' and or_name like '%"+likestring+"%' and agentname='"+agentname+"'");
	    while(orsl.next()){
	    	JSONObject  ob=new JSONObject();
	    	ob.put("text", "<font color=\"#D52B2B\">[组织]</font>"+orsl.getString(1));
	    	ob.put("value", "org#"+orsl.getInt(2));
	    	js.add(ob);
	    }
	SqlRowList jobsql = baseDao.queryForRowSet("select  jo_name,jo_id from job where nvl(jo_statuscode,' ')<>'DISABLE' and jo_name like '%"+likestring+"%' and nvl(ISAGENT,0)=-1 and jo_orgid in (select or_id from hrorg where nvl(or_statuscode,' ')<>'DISABLE' and agentname='"+agentname+"')");
	  while(jobsql.next()){
	    	 JSONObject  ob=new JSONObject();
	    	ob.put("text", "<font color=\"#4DB34D\">[岗位]</font>"+jobsql.getString(1));
	    	ob.put("value", "job#"+jobsql.getInt(2));
	    	js.add(ob);
	    }
   	  SqlRowList employeesql=baseDao.queryForRowSet("select  em_name,em_id from employee where nvl(em_class,' ')<>'离职' and em_name like '%"+likestring+"%' and em_defaulthsid in(select jo_id from job where nvl(ISAGENT,0)=-1 and jo_orgid in (select or_id from hrorg where nvl(or_statuscode,' ')<>'DISABLE' and agentname='"+agentname+"'))");
   	  while(employeesql.next()){
	 	    	 JSONObject  ob=new JSONObject();
	 	    	ob.put("text", /*"<font color=\"#4DB34D\">[个人]</font>"+*/employeesql.getString(1));
	 	    	ob.put("value","employee#"+employeesql.getInt(2));
	 	    	js.add(ob);
	 	    }
   	  return js;
	 
	}
    
	@Override
	public List<JSONObject> ProcessQueryPersons(String likestring) {
		// TODO Auto-generated method stub
		List<JSONObject> js=new ArrayList<JSONObject>();
	    SqlRowList orsl=baseDao.queryForRowSet("select  or_name,or_id from Hrorg where nvl(or_statuscode,' ')<>'DISABLE' and or_name like '%"+likestring+"%' ");
	    while(orsl.next()){
	    	JSONObject  ob=new JSONObject();
	    	ob.put("text", "<font color=\"#D52B2B\">[组织]</font>"+orsl.getString(1));
	    	ob.put("value", "org#"+orsl.getInt(2));
	    	js.add(ob);
	    }
	    SqlRowList jobsl=baseDao.queryForRowSet("select  jo_name,jo_id from job where nvl(jo_statuscode,' ')<>'DISABLE' and jo_name like '%"+likestring+"%' ");
	    while(jobsl.next()){
	    	JSONObject  ob=new JSONObject();
	    	ob.put("text", "<font color=\"#C4C43C\">[岗位]</font>"+jobsl.getString(1));
	    	ob.put("value", "job#"+jobsl.getInt(2));
	    	js.add(ob);
	    }
	    SqlRowList employeesl=baseDao.queryForRowSet("select  em_name,em_id from employee where nvl(em_class,' ')<>'离职' and em_name like '%"+likestring+"%' ");
	     while(employeesl.next()){
	    	 JSONObject  ob=new JSONObject();
	    	ob.put("text", /*"<font color=\"#4DB34D\">[个人]</font>"+*/employeesl.getString(1));
	    	ob.put("value", "employee#"+employeesl.getInt(2));
	    	js.add(ob);
	    }
	    return js;
	}

	@Override
	public JProcessSet getJprocessSet(String caller) {
		// TODO Auto-generated method stub
		try{
			JProcessSet jprocessset = baseDao.getJdbcTemplate().queryForObject("select *  from jprocessset where js_caller=?", 
					new BeanPropertyRowMapper<JProcessSet>(JProcessSet.class),caller);					
			return jprocessset;
		} catch (EmptyResultDataAccessException e){
			e.printStackTrace();
			return null;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

}
