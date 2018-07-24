package com.uas.erp.service.sys.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sun.org.apache.xerces.internal.impl.dv.xs.BaseSchemaDVFactory;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.bind.Status;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.FeedbackDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.FeedBackFlow;
import com.uas.erp.model.FeedbackModule;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.MessageLog;
import com.uas.erp.model.excel.ExcelFileTemplate;
import com.uas.erp.service.sys.FeedBackService;

@Service("feedbackService")
public class FeedBackServiceImpl implements FeedBackService {
	@Autowired
	private FeedbackDao feedbackDao;
	@Autowired
	private BaseDao baseDao;	
	@Autowired
	private HandlerService handlerService;	
	private static String logsql="insert into feedbacklog(fl_id,fl_fbid,fl_remark,fl_man,fl_kind,fl_position )values(?,?,?,?,?,?)";
	@Override
	public void saveFeedback(String formStore, String param, String language,
			Employee employee) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);		
		formStore = BaseUtil.parseMap2Str(store);
		Object enuu=baseDao.getFieldDataByCondition("ENTERPRISE", "en_id", " 1=1");		
		String enname=baseDao.getFieldDataByCondition("ENTERPRISE", "en_shortname", " 1=1").toString();		
		handlerService.handler("Feedback", "save", "before", new Object[]{formStore, language});				
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Feedback", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		if(employee.getVirtual_enuu()!=null){
			baseDao.updateByCondition("FeedBack","fb_enid="+employee.getVirtual_enuu(),"fb_id="+store.get("fb_id"));
		}else {
			String sql="update Feedback set FB_ENID="+enuu+",fb_enname='"+enname+"' where fb_id="+store.get("fb_id");
			baseDao.execute(sql);
		}		
	}

	@Override
	public void deleteFeedback(int id, String language, Employee employee) {

	}

	@Override
	public void updateFeedback(String formStore, String param, String language,
			Employee employee) {

	}

	@Override
	public void reply(int id, String comment, String language, Employee employee) {
		String sendname = employee.getEm_name();
		feedbackDao.replyCommetnt(id, comment, sendname,employee);
	}

	@Override
	public int feedbackTurnBug(String language, Employee employee,
			int id) {
		int buid = 0;
		//判断该问题反馈是否已转过BUG
		Object code = baseDao.getFieldDataByCondition("checklistdetail", "cld_id", "cld_sourceid=" + id);
		if(code != null && !code.equals("")){
			Object clid = baseDao.getFieldDataByCondition("checklistdetail", "cld_clid", "cld_sourceid=" + id);
			BaseUtil.showError(BaseUtil.getLocalMessage("sys.feedback.haveturnBug", language) + 
					"<a href=\"javascript:openUrl('jsps/plm/test/newchecklist.jsp?formCondition=cl_idIS" + clid + "&gridCondition=cld_clidIS" + clid + "')\">" + code + "</a>&nbsp;");
		} else {
			//转BUG
			buid = feedbackDao.turnBuglist(id, language, employee);
			//修改问题反馈状态
			baseDao.updateByCondition("Feedback", "fb_uasstatus='处理中',fb_sendstatus='待上传'", "fb_id=" + id);
			//记录操作
			baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.turnBuglist", language), 
					BaseUtil.getLocalMessage("msg.turnSuccess", language), "Feedback|fb_id=" + id));
		}
		return buid;
	}

	@Override
	public void resAudit(int id, String language, Employee employee) {
		Object code = baseDao.getFieldDataByCondition("feedback", "fb_uasstatus", "fb_id=" + id);
		Object status = baseDao.getFieldDataByCondition("feedback", "fb_statuscode","fb_id=" + id);
		if(!status.equals("AUDITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit", language));
		}
		if(code != null && !code.equals("")){
			BaseUtil.showError(BaseUtil.getLocalMessage("", language));
		}else{
			baseDao.updateByCondition("Feedback", "fb_statuscode='ENTERING',fb_status='"+BaseUtil.getLocalMessage("ENTERING", language)+"'", "fb_id=" + id);
			//记录操作
			baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.resAuditSuccess", language), 
					BaseUtil.getLocalMessage("msg.resAuditSuccess", language), "Feedback|fb_id=" + id));
		}
	}

	@Override
	public void submit(int id, String language, Employee employee) {
		Object status = baseDao.getFieldDataByCondition("feedback", "fb_statuscode", "fb_id=" + id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.submit_onlyEntering", language));
		}
		// 执行提交前的其它逻辑
		handlerService.handler("Feedback", "commit", "before", new Object[] { id, language, employee });
		// 执行提交操作
		baseDao.updateByCondition("feedback",
				"fb_statuscode='COMMITED',fb_status='" + BaseUtil.getLocalMessage("COMMITED", language) + "'", "fb_id="
						+ id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.submit", language),
				BaseUtil.getLocalMessage("msg.submitSuccess", language), "Feedback|id=" + id));
		// 执行提交后的其它逻辑
		handlerService.handler("Feedback", "commit", "after", new Object[] { id, language, employee });
		//提交时插入datacenter
		/*String isGroup = BaseUtil.getXmlSetting("group");
		if("true".equals(isGroup)){
			Master master = employee.getCurrentMaster(); 
			String masoncode = master.getMa_name();
			if (master != null && master.getMa_type() == 3 &&masoncode!= null) {
				String sql = "insert into datacenter.feedback(FB_ID,FB_CODE,FB_DATE,FB_ENNAME,FB_ENID,FB_EMNAME,FB_EMID,FB_TEL," +
						"FB_EMAIL,FB_STATUS,FB_STATUSCODE,FB_URGENT,FB_RATE,FB_MODULE,FB_ATTCH,FB_UASSTATUS,FB_UASMAN,FB_YWCODE," +
						"FB_SENDSTATUS,FB_OTHERENID,FB_OTHERENNAME,FB_DETAIL,FB_COMPLAINTS,FB_UASDETAIL,fb_master) select datacenter.feedback_SEQ.nextval,FB_CODE,FB_DATE,FB_ENNAME,FB_ENID,FB_EMNAME,FB_EMID,FB_TEL,FB_EMAIL,FB_STATUS,FB_STATUSCODE,FB_URGENT,FB_RATE,FB_MODULE,FB_ATTCH,FB_UASSTATUS,FB_UASMAN,FB_YWCODE,FB_SENDSTATUS,FB_OTHERENID,FB_OTHERENNAME,FB_DETAIL,FB_COMPLAINTS,FB_UASDETAIL,'"+masoncode+"' from "+masoncode+".feedback where fb_id="+id;
				baseDao.execute(sql);
			}			
		}*/
	}

	@Override
	public void reSubmit(int id, String language, Employee employee) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("feedback", "fb_statuscode", "fb_id=" + id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resSubmit_onlyCommited", language));
		}
		handlerService.handler("Feedback", "resCommit", "before", new Object[] { id, language, employee });
		// 执行反提交操作
		baseDao.updateByCondition("feedback",
				"fb_statuscode='ENTERING',fb_status='" + BaseUtil.getLocalMessage("ENTERING", language) + "'", "fb_id="
						+ id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.resSubmit", language),
				BaseUtil.getLocalMessage("msg.resSubmitSuccess", language), "Feedback|id=" + id));
		handlerService.handler("Feedback", "resCommit", "after", new Object[] { id, language, employee });
	}

	@Override
	public void audit(int id) {
		String language=SystemSession.getLang();
		Employee employee=SystemSession.getUser();
		Object[] data = baseDao.getFieldsDataByCondition("feedback", new String[]{"fb_statuscode","fb_kind"}, "fb_id=" + id);
		/*if (!data[0].equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.audit_onlyCommited", language));
		}*/
		// 执行审核前的其它逻辑
		handlerService.handler("Feedback", "audit", "before", new Object[] { id, language });
		// 执行审核操作
		baseDao.updateByCondition("Feedback","fb_statuscode='AUDITED',fb_status='" + BaseUtil.getLocalMessage("AUDITED", language) + "',fb_auditdate=sysdate", "fb_id="+ id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.audit", language),
				BaseUtil.getLocalMessage("msg.auditSuccess", language), "Feedback|id=" + id));
		// 执行审核后的其它逻辑
		handlerService.handler("Feedback", "audit", "after", new Object[] { id, language,employee });
		Object[] objects = baseDao.getFieldsDataByCondition("Feedback", new String[]{"fb_code","fb_master"}, "fb_id="+id);
		if(objects[1]!=null){
			String sql = "update "+objects[1]+".Feedback set fb_status='已审核',fb_statuscode='AUDITED' where fb_code='"+objects[0]+"'";
			baseDao.execute(sql);
		}
		/**
		 * 确定问题流向
		 * */
		String position=baseDao.getFieldValue("Feedbackflow", "ff_step", "ff_code='"+data[1]+"' and ff_detno=1", String.class);
		baseDao.updateByCondition("FeedBack", "fb_position='"+position+"'","fb_id="+id);
		/**
		 *分析节点处理人 
		 * */
		setNodeDealMan(id,String.valueOf(data[1]),position);
	}
	/**
	 *客户方审核 
	 * */
	@Override
	public void CustomerAudit(int id) {
		String language=SystemSession.getLang();
		Employee employee=SystemSession.getUser();
		handlerService.handler("Feedback", "audit", "before", new Object[] { id, language });
		// 执行审核操作
		baseDao.updateByCondition("Feedback",
				"fb_statuscode='AUDITED',fb_status='" + BaseUtil.getLocalMessage("AUDITED", language) + "',fb_position='End'", "fb_id="
						+ id);		
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.audit", language),
				BaseUtil.getLocalMessage("msg.auditSuccess", language), "Feedback|id=" + id));
		// 执行审核后的其它逻辑
		handlerService.handler("Feedback", "audit", "after", new Object[] { id, language,employee });
	}
	@Override
	public void changestatus(String language, Employee employee, int id) {
		//处理人
		Object resourcename = baseDao.getFieldDataByCondition("feedback left join PROJECTTASK on fb_code=sourcecode", "resourcename", "fb_id=" + id);
		if(resourcename!=null){
			baseDao.updateByCondition("Feedback", "fb_uasstatus='处理中',fb_sendstatus='待上传',fb_email='"+resourcename+"'", "fb_id=" + id);
		}else{
			BaseUtil.showError("请先建立任务,选择处理人,再更改该问题的处理状态！");
		}
	}

	@Override
	public void canceltask(String language, Employee employee, int id) {
		baseDao.updateByCondition("Feedback", "fb_uasstatus='不处理',fb_sendstatus='待上传'", "fb_id=" + id);
	}

	@Override
	public void endFeedback(String language, Employee employee, int id) {
		Object resourcename = baseDao.getFieldDataByCondition("feedback left join PROJECTTASK on fb_code=sourcecode", "resourcename", "fb_id=" + id);
		baseDao.updateByCondition("Feedback", "fb_uasstatus='已处理',fb_sendstatus='待上传',fb_email='"+resourcename+"'", "fb_id=" + id);
	}
	private String getCurrentNode(int id){
		return baseDao.getFieldValue("FeedBack", "fb_position","fb_id="+id,String.class);
	}
	private void Review(int id){
		Object[] data=baseDao.getFieldsDataByCondition("FeedBack", new String[]{"fb_position","fb_kind"}, "fb_id="+id);
		if(data!=null){
			List<Object[]>infos=baseDao.getFieldsDatasByCondition("FeedBack", new String[]{"ff_step","ff_autoend"}, "ff_code='"+data[1]+"'");
			for(Object []info:infos){

			}
		} 

	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void backPlan(String data) {
		// TODO Auto-generated method stub
		Map<Object,Object> map=BaseUtil.parseFormStoreToMap(data);
		Object position=map.get("fb_position"),remark=null;
		FeedBackFlow current=getCurrentNode(String.valueOf(map.get("fb_kind")), String.valueOf(position));
		if(position!=null){
			remark=map.get(current.getFf_remarkfield());
		}
		remark=remark!=null?"回复预计完成时间:"+map.get(current.getFf_plandatefield())+" 处理描述:"+remark:"回复预计完成时间:"+map.get(current.getFf_plandatefield());
		log("PLAN", remark,map.get("fb_position"),map.get("fb_id"));
		String updateSql=SqlUtil.getUpdateSqlByFormStore(map, "FeedBack", "fb_id");
		baseDao.execute(updateSql);

	}
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void confirm(String data,Integer _customer,Integer _process) {
		// TODO Auto-generated method stub
		Map<Object,Object> map=BaseUtil.parseFormStoreToMap(data);
		Employee employee=SystemSession.getUser();
		String language=SystemSession.getLang();
		Object resultkey="",result=0,baseposition=map.get("fb_position"),remark=null;
		String nextposition="";

		if(_customer!=null){
			/**
			 * 客户方确认问题
			 * */
			result=map.get("fb_backresult")!=null?map.get("fb_backresult"):0;
			map.put("fb_backdate",DateUtil.parseDateToString(null,Constant.YMD_HMS));
			map.put("fb_backman", employee.getEm_name());
			baseposition="客户确认";
			if(Integer.parseInt(result.toString())==-1){
				Status status = Status.FINISH;
				map.put("fb_statuscode", status.code());
				map.put("fb_status", status.display());
				nextposition="Backed";
				result="已处理";			
			}else{
				nextposition=baseDao.getFieldValue("feedbackflow", "ff_step", "Ff_Code='FeedBack' and ff_detno=1", String.class);  
				result="未处理";
				handlerService.handler("Feedback", "resCommit", "after", new Object[] { map.get("fb_id") });
				baseDao.updateByCondition("feedback",
						"fb_statuscode='COMMITED',fb_status='" + BaseUtil.getLocalMessage("COMMITED", language) + "'", "fb_id="
								+map.get("fb_id"));
				handlerService.handler("Feedback", "commit", "after", new Object[] { Integer.parseInt(String.valueOf(map.get("fb_id")))});
			} 
			remark=StringUtil.hasText(map.get("fb_backdescription"))?map.get("fb_backdescription"):"无";

		}else {
			FeedBackFlow current=getCurrentNode(String.valueOf(map.get("fb_kind")), String.valueOf(map.get("fb_position")));
			if("End".equals(map.get("fb_position"))){
				result=map.get("fb_backresult");
				if("未处理".equals(result)){
					nextposition="R&D";
				}else {
					baseDao.updateByCondition("FeedBack", "fb_status='已回复',fb_statuscode='REPLYED',fb_position='Backed',fb_backman='"+employee.getEm_name()+"',fb_backmanid="+employee.getEm_id(),"fb_id="+map.get("fb_id"));
					nextposition="Backed";
				}

			}else{
				if(current!=null) resultkey=current.getFf_resultfield();	
				result=map.get(resultkey);
				if(StringUtil.hasText(resultkey) && result==null){
					BaseUtil.showError("确认需填写相应的处理结果!");
				}
				if("-1".equals(result) || !StringUtil.hasText(resultkey)){
					//同意或未设置结果字段 自动执行
					nextposition=baseDao.getFieldValue("feedbackflow", "ff_step", "Ff_Code='"+map.get("fb_kind")+"' and ff_detno=(select Ff_Detno+1 from feedbackflow where ff_code='"+map.get("fb_kind")+"' and ff_step='"+map.get("fb_position")+"')", String.class);                 
					result="确认处理";
				}else if(result.equals("0")){
					//判断节点是否能强制结束
					if(current.getFf_autoend()==0){
						//不能强制结束,返回上一层
						nextposition=baseDao.getFieldValue("feedbackflow", "ff_step", "Ff_Code='"+map.get("fb_kind")+"' and ff_detno=(select Ff_Detno-1 from feedbackflow where ff_code='"+map.get("fb_kind")+"' and ff_step='"+map.get("fb_position")+"')", String.class);     
						result="处理失败";        		
					}else {
						nextposition="End";
						result="不作处理";
					}
				}

			}
			remark=map.get(current.getFf_remarkfield());
		}
		if (nextposition==null) {
			baseDao.execute("UPDATE FeedBack SET FB_STATUS='在录入',FB_STATUSCODE='ENTERING',FB_POSITION='UNAUDITED' WHERE fb_id="+map.get("fb_id"));
			remark=remark!=null?"处理结果:"+result+" 处理描述:"+remark:"处理结果:"+result;
			log("REVIEW", remark,baseposition,map.get("fb_id"));		
		}else {
			map.put("fb_position", nextposition);
			String updateSql=SqlUtil.getUpdateSqlByFormStore(map, "FeedBack", "fb_id");
			baseDao.execute(updateSql);
			if(!"Backed".equals(nextposition) && _process==null) setNodeDealMan(map.get("fb_id"), String.valueOf(map.get("fb_kind")), nextposition);
			remark=remark!=null?"处理结果:"+result+" 处理描述:"+remark:"处理结果:"+result;
			log("REVIEW", remark,baseposition,map.get("fb_id"));			
		}
	}
	@Override
	public void processConfirm(String data,String step) {
		// TODO Auto-generated method stub
		Map<Object,Object> map=BaseUtil.parseFormStoreToMap(data);
		String updateSql=SqlUtil.getUpdateSqlByFormStore(map, "FeedBack", "fb_id");
		/** 审批流程方式走问题反馈*/
		String remark="";
		FeedBackFlow current=getCurrentNode("FeedBack",step);
		if(current!=null) remark=current.getRemark(map);
		else remark="处理结果:处理成功!";
		baseDao.execute(updateSql);		
		log("REVIEW", remark,step,map.get("fb_id"));
	}
	@Override
	public void changeHandler(String data) {
		// TODO Auto-generated method stub
		Map<Object,Object> map=BaseUtil.parseFormStoreToMap(data);
		Employee  employee=SystemSession.getUser();
		FeedBackFlow current=getCurrentNode(String.valueOf(map.get("fb_kind")), String.valueOf(map.get("fb_position")));
		Object handman="",remark=null;	
		if(current.getFf_manfield()!=null){
			handman=map.get(current.getFf_manfield());
		}		
		remark=map.get(current.getFf_remarkfield());
		remark=remark!=null?"变更处理人:"+employee.getEm_name()+"->"+handman+" 描述:"+remark:"变更处理人:"+employee.getEm_name()+"->"+handman;
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(map, "FeedBack", "fb_id"));
		log("CHANGEHANDLER", remark,map.get("fb_position"),map.get("fb_id"));

	}
	private void log(String kind,Object object,Object position,Object id){
		Employee employee=SystemSession.getUser();
		baseDao.execute(logsql, new Object[]{
				baseDao.getSeqId("feedbacklog_seq"),
				id,object,employee.getEm_name(),kind,position
		});
	}
	@Override
	public FeedBackFlow getCurrentNode(String kind, String position) {
		// TODO Auto-generated method stub			
		try{
			return baseDao.getJdbcTemplate().queryForObject("select *  from FeedBackFlow where ff_code=? and ff_step=?",new BeanPropertyRowMapper<FeedBackFlow>(FeedBackFlow.class),kind,position);	
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}		
	}
	/**
	 * 分析下个step处理人，并赋值
	 * */
	private void setNodeDealMan(Object id,String kind,String position){
		FeedBackFlow flow=getCurrentNode(kind, position);
		String querySql=flow.getManSql(id);
		if(querySql!=null){
			SqlRowList sl=baseDao.queryForRowSet(flow.getManSql(id));
			if(sl.next() && flow.getFf_manfield()!=null && flow.getFf_manidfield()!=null){
				baseDao.updateByCondition("feedback",flow.getFf_manidfield()+"="+sl.getObject(1)+","+flow.getFf_manfield()+"='"+sl.getObject(2)+"'", "fb_id="+id);
			}
		}		
	}
	String Day_Count="select * from (with t1 as (select sysdate-level+1 field from dual connect by  level<=365 order by field) select t1.field,count(*) value from  t1 left join feedback on fb_date<t1.field where ? group by t1.field ) order by field asc";
	String Week_Count="select * from (with t1 as (select sysdate-7*level field from dual connect by  level<=52 order by field) select t1.field,count(*) value from  t1 left join feedback on fb_date<t1.field where ? group by t1.field ) order by field asc";
	String Month_Count="select * from (with t1 as (select sysdate-30*level field from dual connect by  level<=12 order by field) select t1.field,count(*) value from  t1 left join feedback on fb_date<t1.field where ? group by t1.field ) order by field asc";
	@Override
	public List<Map<String, Object>> getDay_count(String condition) {
		// TODO Auto-generated method stub
		condition=condition==null?"1=1":condition;
		Day_Count=Day_Count.replaceAll("\\?", condition);
		return baseDao.getJdbcTemplate().queryForList(Day_Count);
	}

	@Override
	public List<Map<String, Object>> getWeek_count(String condition) {
		// TODO Auto-generated method stub
		condition=condition==null?"1=1":condition;
		Week_Count=Week_Count.replaceAll("\\?", condition);
		return baseDao.getJdbcTemplate().queryForList(Week_Count);
	}

	@Override
	public List<Map<String, Object>> getMonth_count(String condition) {
		// TODO Auto-generated method stub
		condition=condition==null?"1=1":condition;
		Month_Count=Month_Count.replaceAll("\\?", condition);
		return baseDao.getJdbcTemplate().queryForList(Month_Count);
	}
	@Override
	public List<Map<String, Object>>  getFeedback(String condition) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		String sql="select fb_id ,fb_code,fb_enid,fb_kind,fb_theme,fb_detail,fb_position,fb_date  from feedback ";
		if(condition!=null){
			sql=sql+" where "+condition;
		}
		SqlRowList rs=baseDao.queryForRowSet(sql);
		String sql1="select dlc_value,dlc_display from datalistcombo where dlc_caller='Feedback!Customer' and dlc_fieldname='fb_position'";
		SqlRowList rs1=baseDao.queryForRowSet(sql1);
		Map<String, Object> combo=new HashMap<String, Object>();
		while(rs1.next()){
			combo.put(rs1.getString("dlc_display"),rs1.getString("dlc_value"));
		}
		while(rs.next()){
			Map<String, Object> item=new HashMap<String, Object>();
			item.put("FB_ID", rs.getInt("fb_id"));
			item.put("FB_CODE", rs.getString("fb_code"));
			item.put("FB_ENID", rs.getInt("fb_enid"));
			item.put("FB_KIND", rs.getString("fb_kind"));
			item.put("FB_THEME", rs.getString("fb_theme"));
			item.put("FB_DETAIL", rs.getString("fb_detail"));
			item.put("FB_POSITION", rs.getString("fb_position"));
			if(combo.get(rs.getString("fb_position"))!=null){
				item.put("FB_POSITION", combo.get(rs.getString("fb_position")));
			}
			item.put("FB_DATE", rs.getDate("fb_date"));		
			store.add(item);
		}
		return store;
	}

	@Override
	public List<JSONTree> getJSONTreeByParentId(int parentId,String kind,String condition, Integer _noc) {
		String sql = "select * from feedbackmodule where FM_SUBOF="+parentId+" and fm_kind='"+kind+"' order by fm_detno";
		List<FeedbackModule> modules = baseDao.query(sql, FeedbackModule.class);
		List<JSONTree> tree = new ArrayList<JSONTree>();
		for (FeedbackModule module : modules) {
			tree.add(new JSONTree(module));
		}
		return tree;
	}
}
