package com.uas.erp.service.oa.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.PathUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.oa.MeetingDocService;
import com.uas.erp.service.oa.PagingReleaseService;

@Service
public class MeetingDocServiceImpl implements MeetingDocService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private PagingReleaseService pagingReleaseService;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveMeetingDoc(String formStore, String caller) throws ParseException {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//防止文号重复
		int count=baseDao.getCount("select count(1) from MeetingDoc where md_fileno='"+store.get("md_fileno")+"'");		
		if(count!=0){
			BaseUtil.showError("此文号已存在！");
		}
		//限制时间
		String start=store.get("md_starttime").toString();
		String end=store.get("md_endtime").toString();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		if(sdf.parse(start).getTime()-sdf.parse(end).getTime()>=0){
			BaseUtil.showError("会议时间输入有误，截止时间不能早于开始时间！");
		}
		/*SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd"); 
		String today=sdf1.format(new Date());
		if(sdf1.parse(start).before(sdf1.parse(today))){
			BaseUtil.showError("会议时间输入有误，开始时间不能早于当天！");
		}*/
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// md_contents是clob字段，特殊处理
		String html = store.get("md_contents").toString();
		store.remove("md_contents");
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MeetingDoc",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.saveClob("MeetingDoc", "md_contents", html,
				"md_id=" + store.get("md_id"));
		// 记录操作
		baseDao.logger.save(caller, "md_id", store.get("md_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });

	}

	@Override
	public void updateMeetingDoc(String formStore, String caller) throws ParseException {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int count=baseDao.getCount("select count(1) from MeetingDoc where md_id<> "+store.get("md_id")+" and md_fileno='"+store.get("md_fileno")+"'");
		if(count!=0){
			BaseUtil.showError("此文号已存在！");
		}
		String start=store.get("md_starttime").toString();
		String end=store.get("md_endtime").toString();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		if(sdf.parse(start).getTime()-sdf.parse(end).getTime()>=0){
			BaseUtil.showError("会议时间输入有误，截止时间不能早于开始时间！");
		}
		/*SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd"); 
		String today=sdf1.format(new Date());
		if(sdf1.parse(start).before(sdf1.parse(today))){
			BaseUtil.showError("会议时间输入有误，开始时间不能早于当天！");
		}*/
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// md_contents是clob字段，特殊处理
		String html = store.get("md_contents").toString();
		store.remove("md_contents");
		store.put("md_updateman", SystemSession.getUser().getEm_name());//更新人
		store.put("md_updatedate", DateUtil.currentDateString(null));//更新时间
		// 修改MeetingDoc
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MeetingDoc",
				"md_id");
		baseDao.execute(formSql);
		baseDao.saveClob("MeetingDoc", "md_contents", html,
				"md_id=" + store.get("md_id"));
		// 记录操作
		baseDao.logger.update(caller, "md_id", store.get("md_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });

	}

	@Override
	public void deleteMeetingDoc(int md_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { md_id });
		// 还原会议申请的状态Meetingroomapply
		baseDao.execute("update Meetingroomapply set ma_isturndoc='否' where ma_code=(select md_meetingcode from meetingdoc where md_id="
				+ md_id + ")");
		// 删除purchase
		baseDao.deleteById("MeetingDoc", "md_id", md_id);
		// 记录操作
		baseDao.logger.delete(caller, "md_id", md_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { md_id });

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void auditMeetingDoc(int md_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("MeetingDoc",
				"md_statuscode", "md_id=" + md_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { md_id });
		Object []datas=baseDao.getFieldsDataByCondition("MeetingDoc", new String[]{"md_groupid","md_title"}, "md_id="+md_id);
		if(datas[0]!=null){
			pagingReleaseService.paging(String.valueOf(datas[0]), "会议纪要提醒",getPagingContext(md_id,String.valueOf(datas[1])),"meeting");
		}
		// 执行审核操作
		baseDao.audit("MeetingDoc", "md_id=" + md_id, "md_status","md_statuscode","md_auditdate", "md_auditman");
		//wusy
		Object md_meetingcode = baseDao.getFieldDataByCondition("MeetingDoc", "md_meetingcode", "md_id="+md_id);
		baseDao.updateByCondition("MEETINGROOMAPPLY", " ma_type='已填写' ", "ma_code='"+md_meetingcode+"'");
		
		//更新会议申请与会人员和明细表				
		SqlRowList rs = baseDao.queryForRowSet("select * from meetingroomapply left join meetingdoc on md_meetingcode=ma_code where ma_code='"+md_meetingcode+"'");
		if(rs.next()){
			if(rs.getString("md_groupid")!=null&&!"".equals(rs.getString("md_groupid"))){
				String[] applyGroups = rs.getString("ma_groupid").split(";");
				String[] docGroups = rs.getString("md_groupid").split(";");
				String[] docGroupsNames = rs.getString("md_group").split(";");
				
				for(int i=docGroups.length-1;i>=0;i--){
					for(int j=applyGroups.length-1;j>=0;j--){
						if(docGroups[i].equals(applyGroups[j])){
							docGroups[i] = "";
							docGroupsNames[i] = "";
							break;
						}
					}
				}
				
				//增加明细
				StringBuffer sb = new StringBuffer();
				StringBuffer namesb = new StringBuffer();
				for(int i=0;i<docGroups.length;i++){
					if(!"".equals(docGroups[i])){
						sb.append(";" + docGroups[i]);
						namesb.append(";" + docGroupsNames[i]);						
					}
				}
				if(sb.length()>0){
					baseDao.execute("update meetingroomapply set ma_group=ma_group||'"+namesb+"',ma_groupid=ma_groupid||'"+sb+"' where ma_id=" + rs.getString("ma_id"));
					operateAllEmps(sb.substring(1),rs.getString("ma_id"),"add");				
				}				
			}
		}
		
		// 记录操作
		baseDao.logger.audit(caller, "md_id", md_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { md_id });

	}

	/**
	 * 把虚拟组中的包含的员工都插入到明细中
	 */
	private void operateAllEmps(String groupString,String maid,String operateType){
		//group:job#8;employee#106;employee#107;org#5
		Set<String> jobs = new HashSet<String>();// 岗位
		Set<String> emps = new HashSet<String>();// 人员
		Set<String> orgs = new HashSet<String>();// 组织
		for (String group : groupString.split(";")) {
			String[] g = group.split("#");
			if ("job".equals(g[0])) {
				jobs.add(g[1]);
			}
			if ("employee".equals(g[0])) {
				emps.add(g[1]);
			}
			if ("org".equals(g[0])) {
				orgs.add(g[1]);
			}
		}
		if (orgs.size() > 0) {
			for (String org : orgs) {
				List<Object> empids = baseDao.getFieldDatasByCondition("HRORGEMPLOYEES", "OM_EMID","OM_ORID=" + org);
				for (Object empid : empids) {
					emps.add(empid.toString());
				}
			}
		}
		if (jobs.size() > 0) {
			for (String job : jobs) {
				List<Object> empids = baseDao.getFieldDatasByCondition("employee", "EM_id","EM_DEFAULTHSID=" + job);
				for (Object empid : empids) {
					emps.add(empid.toString());
				}
			}
		}
		List<String> sqls = new ArrayList<String>();
		int detno = 1;
		Object detnoobj = baseDao.getFieldDataByCondition("meetingdetail", "max(md_detno)", "md_maid=" + maid);
		if(detnoobj!=null&&!"".equals(detnoobj)){
			detno = Integer.parseInt(detnoobj.toString()) + 1;
		}
		
		for (String empid : emps) {
			if(operateType.equals("add")){
				String sqldetail = "insert into MeetingDetail(md_detno,md_maid,md_participantsid,md_participants,"
								+ "md_emcode,md_isnoticed,md_id,md_trueparter,MD_CONFIRMTIME) select "
								+ (detno++) + ","+ maid+ ",em_id,em_name,em_code,-1,MeetingDetail_seq.nextval,"
								+ "em_name,null from employee where em_code not in "
								+ "(select md_emcode from MeetingDetail where md_maid="+maid+") and em_id="+ empid;
				sqls.add(sqldetail);
				
			}else if("delete".equals(operateType)){
				String sqlDel = "delete from meetingdetail where md_participantsid='"+empid+"' and md_maid=" + maid;
				sqls.add(sqlDel);
			}
		}
		baseDao.execute(sqls);
	}
	
	@Override
	public void resAuditMeetingDoc(int md_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("MeetingDoc",
				"md_statuscode", "md_id=" + md_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.handler(caller, "resAudit", "before", new Object[]{md_id});
		// 执行反审核操作
		baseDao.resAudit("MeetingDoc", "md_id=" + md_id, "md_status","md_statuscode", "md_auditdate", "md_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "md_id", md_id);
		handlerService.handler(caller, "resAudit", "after", new Object[]{md_id});

	}

	@Override
	public void submitMeetingDoc(int md_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MeetingDoc",
				"md_statuscode", "md_id=" + md_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { md_id });
		// 执行提交操作
		baseDao.submit("MeetingDoc", "md_id=" + md_id, "md_status",
				"md_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "md_id", md_id);
		;
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { md_id });

	}

	@Override
	public void resSubmitMeetingDoc(int md_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MeetingDoc",
				"md_statuscode", "md_id=" + md_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[]{md_id});
		// 执行反提交操作
		baseDao.resOperate("MeetingDoc", "md_id=" + md_id, "md_status",
				"md_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "md_id", md_id);
		handlerService.handler(caller, "resCommit", "after", new Object[]{md_id});

	}

	@Override
	public void meetingSign(String data, String caller) {
		// data:[{"md_id":3990,"md_trueparter":"\u5e94\u9e4f"},{"md_id":3991,"md_trueparter":"\u5468\u8881"}]
		List<Map<Object, Object>> list = BaseUtil.parseGridStoreToMaps(data);
		baseDao.updateByCondition("MeetingDetail", "md_isconfirmed=0",
				"md_maid=" + list.get(0).get("md_maid"));
		List<String> sqls = new ArrayList<String>();
		for (Map<Object, Object> store : list) {
			String sql = "update MeetingDetail set md_isconfirmed=-1,md_trueparter='"
					+ store.get("md_trueparter")
					+ "',md_confirmtime=sysdate where md_id="
					+ store.get("md_id");
			sqls.add(sql);
		}
		baseDao.execute(sqls);
	}
    private String getPagingContext(int id,String title){
    	StringBuffer sb=new StringBuffer();    	
    	sb.append("<a style=\"padding-left:10px;\" href=\"javascript:parent.openUrl('jsps/oa/meeting/meetingDoc.jsp?formCondition=md_idIS"+id+"')\">"+title+"</a>");
    	sb.append("会议有新的会议纪要，请注意查看!");
    	return sb.toString();
    }
}
