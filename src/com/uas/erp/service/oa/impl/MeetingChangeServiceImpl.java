package com.uas.erp.service.oa.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.sql.MckoiCaseFragment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.oa.MeetingChangeService;

@Service
public class MeetingChangeServiceImpl implements MeetingChangeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveMeetingChange(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int count=baseDao.getCount("select count(1) from MeetingChange where mc_code='"+store.get("mc_code")+"'");		
		if(count!=0){
			BaseUtil.showError("此变更单号已存在！");
		}
		checkIsUsed1(store.get("mc_newstarttime"),store.get("mc_newendtime"), store.get("mc_newmrname"),store.get("mc_macode"));
		handlerService.beforeSave(caller, new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"MeetingChange", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "mc_id", store.get("mc_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteMeetingChange(int mc_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { mc_id });
		// 删除purchase
		baseDao.deleteById("MeetingChange", "mc_id", mc_id);
		// 记录操作
		baseDao.logger.delete(caller, "mc_id", mc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { mc_id });
	}

	@Override
	public void updateMeetingChange(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int count=baseDao.getCount("select count(1) from MeetingChange where mc_id<> "+store.get("mc_id")+" and mc_code='"+store.get("mc_code")+"'");
		if(count!=0){
			BaseUtil.showError("此变更单号已存在！");
		}
		//检查变更时间会议室是否被占用
		checkIsUsed1(store.get("mc_newstarttime"),store.get("mc_newendtime"), store.get("mc_newmrname"),store.get("mc_macode"));
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		store.put("mc_updateman", SystemSession.getUser().getEm_name());//更新人
		store.put("mc_updatedate", DateUtil.currentDateString(null));//更新时间
		// 修改Meetingroomapply
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"MeetingChange", "mc_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "mc_id", store.get("mc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void submitMeetingChange(int mc_id, String caller) {
		SqlRowList rs = baseDao.queryForRowSet("select mc_id,mc_code,mc_macode from MeetingChange where mc_macode=(select mc_macode from MeetingChange where mc_id='"+mc_id+"') and mc_statuscode ='COMMITED'");
		while (rs.next()) {
			BaseUtil.showError("会议申请单:"+rs.getString("mc_macode")+"存在另一张已提交状态的会议申请变更单:<a href=\"javascript:openUrl('jsps/oa/meeting/meetingChange.jsp?"
					+ "formCondition=mc_idIS"+rs.getString("mc_id")+"&gridCondition=md_maidIS"+rs.getString("mc_id")+"')\">" + rs.getString("mc_code") + "</a>&nbsp;");
		}
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MeetingChange",
				"mc_statuscode", "mc_id=" + mc_id);
		StateAssert.submitOnlyEntering(status);
		Object[] data = baseDao.getFieldsDataByCondition("MeetingChange",
				new String[] { "mc_newstarttime", "mc_newendtime", "mc_newmrname","mc_macode" },
				"mc_id=" + mc_id);
		checkIsUsed1(data[0], data[1], data[2],data[3]);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { mc_id });
		// 执行提交操作
		baseDao.submit("MeetingChange", "mc_id=" + mc_id, "mc_status",
				"mc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "mc_id", mc_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { mc_id });
	}

	@Override
	public void resSubmitMeetingChange(int mc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MeetingChange",
				"mc_statuscode", "mc_id=" + mc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { mc_id });
		// 执行反提交操作
		baseDao.resOperate("MeetingChange", "mc_id=" + mc_id, "mc_status",
				"mc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "mc_id", mc_id);
		handlerService.afterResSubmit(caller, new Object[] { mc_id });

	}

	@Override
	@Transactional
	public void auditMeetingChange(int mc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("MeetingChange",
				"mc_statuscode", "mc_id=" + mc_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { mc_id });
		// 执行审核操作
		baseDao.audit("MeetingChange", "mc_id=" + mc_id, "mc_status","mc_statuscode", "mc_auditdate", "mc_auditer");
		Object[] data=baseDao.getFieldsDataByCondition("MeetingChange left join Meetingroomapply on mc_macode=ma_code",
				new String[]{"mc_newstarttime","mc_newendtime","mc_newmrcode","mc_newmrname","ma_id","ma_code","ma_statuscode"}, "mc_id="+mc_id);
		checkIsUsed1(data[0], data[1], data[3], data[5]);
		//变更的内容反应到Meetingroomapply表上
		baseDao.updateByCondition("Meetingroomapply",
				"ma_starttime=to_date('"+data[0]+"','yyyy-mm-dd hh24:mi:ss'),ma_endtime=to_date('"+data[1]+"','yyyy-mm-dd hh24:mi:ss'),"
				+"ma_mrcode='"+data[2]+"',ma_mrname='"+data[3]+"'",
				"ma_id="+data[4]);
		if("AUDITED".equals(data[6])){
			notifyEmp(data[4], "CHANGE");
		}
		// 记录操作
		baseDao.logger.audit(caller, "mc_id", mc_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { mc_id });

	}

	@Override
	public void resAuditMeetingChange(int mc_id, String caller) {
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, new Object[] { mc_id });
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("MeetingChange",
				"mc_statuscode", "mc_id=" + mc_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("MeetingChange", "mc_id=" + mc_id, "mc_status","mc_statuscode", "mc_auditdate", "mc_auditer");
		// 记录操作
		baseDao.logger.resAudit(caller, "mc_id", mc_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, new Object[] { mc_id });

	}

	/**
	 * 检查此会议室在该时间段是否有被预订
	 */
	private boolean checkIsUsed(Object starttime,Object endtime,Object roomname,Object code){
		String orderDay=starttime.toString().substring(0, 10);
		String countSql="select count(1) from Meetingroomapply where " +
				"(( to_date('"+endtime+"','yyyy-mm-dd hh24:mi:ss')>=ma_starttime and to_date('"+endtime+"','yyyy-mm-dd hh24:mi:ss')<=ma_endtime ) or " +
						"(ma_endtime>=to_date('"+starttime+"','yyyy-mm-dd hh24:mi:ss') and ma_endtime <=to_date('"+endtime+"','yyyy-mm-dd hh24:mi:ss')))"+
						" and (ma_statuscode='COMMITED' OR ma_statuscode='AUDITED') and ma_code<>'"+code+"' and ma_mrname='"+roomname+"'"+" and to_date('"+orderDay+"','yyyy-mm-dd')=to_date(to_char(ma_starttime,'yyyy-mm-dd'),'yyyy-mm-dd') ";
		if(baseDao.getCount(countSql)>0){
			return true;
		}
		return false;
	}
	/**
	 * 检查此会议室在该时间段是否有被预订
	 * 提示单号
	 */
	private void checkIsUsed1(Object starttime,Object endtime,Object roomname,Object code){
		String orderDay=starttime.toString().substring(0, 10);
		SqlRowList rs  = baseDao.queryForRowSet("select ma_id,ma_code from Meetingroomapply where "
				+ "(( to_date('"+endtime+"','yyyy-mm-dd hh24:mi:ss')>=ma_starttime and to_date('"+endtime+"','yyyy-mm-dd hh24:mi:ss')<=ma_endtime ) "
				+ "or (ma_endtime>=to_date('"+starttime+"','yyyy-mm-dd hh24:mi:ss') and ma_endtime <=to_date('"+endtime+"','yyyy-mm-dd hh24:mi:ss'))) "
				+ "and (ma_statuscode='COMMITED' OR ma_statuscode='AUDITED') and ma_code<>'"+code+"' and ma_mrname='"+roomname+"'"+" and to_date('"+orderDay+"','yyyy-mm-dd')=to_date(to_char(ma_starttime,'yyyy-mm-dd'),'yyyy-mm-dd')");
		StringBuffer re=new StringBuffer();
		re.append("此会议室在该时段已经被占用,申请单号:");
		while (rs.next()) {
			re.append("<a href=\"javascript:openUrl('jsps/oa/meeting/meetingroomapply.jsp?formCondition=ma_idIS"+rs.getString("ma_id")+"&gridCondition=md_maidIS"+rs.getString("ma_id")
					+"')\">" + rs.getString("ma_code") + "</a>&nbsp;");
		}
		if(re.length()>19){
			BaseUtil.showError(re.toString());
		}
	}
	/**
	 * 审核时发寻呼给与会成员
	 */
	private void notifyEmp(Object id, String type) {
		List<Object[]> gridDate = baseDao.getFieldsDatasByCondition(
				"MeetingDetail", new String[] { "md_participantsid",
						"md_participants", "md_isnoticed" }, "md_maid=" + id);
		// 发布寻呼信息
		List<String> sqls = new ArrayList<String>();
		StringBuffer sb = null;
		Object [] meetings=baseDao.getFieldsDataByCondition("Meetingroomapply",new String[]{"ma_recorder","ma_theme"},"ma_id="+id);
		String url = "jsps/oa/meeting/meetingroomapply.jsp";
		String formCondition = "ma_id=" + id;
		String gridCondition = "md_maid=" + id;
		if(meetings!=null && meetings[0]!=null && meetings[1]!=null){
			sb = new StringBuffer();			
			int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
			sb.append(SystemSession.getUser().getEm_name()+"变更了<a href=\"javascript:openUrl(''"
					+ url
					+ "?formCondition="
					+ formCondition
					+ "&gridCondition="
					+ gridCondition
					+ "'')\" style=\"font-size:14px; color:blue;\">"+meetings[1]+"</a>会议</br>");
			sqls.add("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,pr_istop,pr_from)values('"
					+ pr_id
					+ "','"
					+  SystemSession.getUser().getEm_name()
					+ "',"
					+ DateUtil.parseDateToOracleString(Constant.YMD_HMS,
							new Date())
					+ ",'"
					+  SystemSession.getUser().getEm_id()
					+ "','" + sb.toString() + "',1,'meeting')");
		for (Object[] s : gridDate) {
			if ("-1".equals(s[2] + "")) {// 需要发通知才发送
				sqls.add("insert into pagingreleasedetail(prd_id,prd_prid,PRD_RECIPIENTID,PRD_RECIPIENT) values(PAGINGRELEASEDETAIL_SEQ.NEXTVAL,'"
						+ pr_id
						+ "','"
						+ s[0]
						+ "','"
						+ s[1]
						+ "')");
			}
		}
				baseDao.execute(sqls);
				//保存到历史消息表
				int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
				sqls.add("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
						+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
						+ " where pr_id="+pr_id);
				sqls.add("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
						+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+"and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");
		}
	}

}
