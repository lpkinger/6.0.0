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
import com.uas.erp.service.oa.MeetingroomapplyService;

@Service
public class MeetingroomapplyServiceImpl implements MeetingroomapplyService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	@Transactional
	public void saveMeetingroomapply(String formStore, String gridStore,String caller) throws ParseException {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		int count=baseDao.getCount("select count(1) from Meetingroomapply where ma_code='"+store.get("ma_code")+"'");		
		if(count!=0){
			BaseUtil.showError("此申请单号已存在！");
		}
		checkIsUsed1(store.get("ma_starttime"), store.get("ma_endtime"), store.get("ma_mrname"));
		checkGrid(grid);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller,new Object[] { store,grid});
		// 会议参与人
		for (Map<Object, Object> s : grid) {
			s.put("md_id", baseDao.getSeqId("MeetingDetail_SEQ"));
		}
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"Meetingroomapply", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"MeetingDetail");
		baseDao.execute(gridSql);
		//如果明细表会议参与人员为空，则将主记录选的参会人员自动插入到明细参会人员
		if(store.get("ma_groupid")!=null
				&&!"".equals(store.get("ma_groupid").toString())){
			insertAllEmps(store.get("ma_groupid"), store.get("ma_id"));
		}
		// 记录操作
		baseDao.logger.save(caller, "ma_id", store.get("ma_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller,new Object[] { store,grid});
		//return checkIsUsed(store.get("ma_starttime"), store.get("ma_endtime"), store.get("ma_mrname"));
	}

	@Override
	public void deleteMeetingroomapply(int ma_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[] { ma_id});
		// 删除purchase
		baseDao.deleteById("Meetingroomapply", "ma_id", ma_id);
		baseDao.deleteById("MeetingDetail", "md_maid", ma_id);
		// 记录操作
		baseDao.logger.delete(caller, "ma_id", ma_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[] { ma_id});
	}

	@Override
	@Transactional
	public void updateMeetingroomapply(String formStore, String gridStore,String  caller) throws ParseException {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int count=baseDao.getCount("select count(1) from Meetingroomapply where ma_id<> "+store.get("ma_id")+" and ma_code='"+store.get("ma_code")+"'");
		if(count!=0){
			BaseUtil.showError("此申请单号已存在！");
		}
		checkIsUsed1(store.get("ma_starttime"), store.get("ma_endtime"), store.get("ma_mrname"));
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		//检查明细行更新人员是否与数据库以保存数据重复
		for(int i=0;i<grid.size();i++){
			SqlRowList rs=baseDao.queryForRowSet("select md_detno from MeetingDetail where md_maid="+store.get("ma_id")+" and md_detno<>'"+grid.get(i).get("md_detno")+"' and md_emcode='"+grid.get(i).get("md_emcode")+"'");
			if(rs.next()){
				BaseUtil.showError("明细行员工编号重复，行号："+rs.getString("md_detno")+"&nbsp&nbsp"+grid.get(i).get("md_detno").toString());
			}
		}
		checkGrid(grid);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store,grid});
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"MeetingDetail", "md_id");
		// 处理gird
		for (Map<Object, Object> s : grid) {
			if (s.get("md_id") == null || s.get("md_id").equals("")
					|| s.get("md_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("MeetingDetail_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "MeetingDetail",
						new String[] { "md_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}		
		store.put("ma_updateman", SystemSession.getUser().getEm_name());//更新人
		store.put("ma_updatedate", DateUtil.currentDateString(null));//更新时间
		// 修改Meetingroomapply
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"Meetingroomapply", "ma_id");
		baseDao.execute(formSql);
		baseDao.execute(gridSql);
		
		//如果明细表会议参与人员为空，则将主记录选的参会人员自动插入到明细参会人员
		baseDao.deleteByCondition("MeetingDetail","md_maid="+store.get("ma_id"));
		if(store.get("ma_groupid")!=null&&!"".equals(store.get("ma_groupid").toString())){
		insertAllEmps(store.get("ma_groupid"), store.get("ma_id"));
		}
		
		// 记录操作
		baseDao.logger.update(caller, "ma_id", store.get("ma_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store,grid});
		//return checkIsUsed(store.get("ma_starttime"), store.get("ma_endtime"), store.get("ma_mrname"));
	}

	@Override
	public void submitMeetingroomapply(int ma_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Meetingroomapply",
				"ma_statuscode", "ma_id=" + ma_id);
		StateAssert.submitOnlyEntering(status);
		Object[]data=baseDao.getFieldsDataByCondition("Meetingroomapply", new String[]{"ma_starttime","ma_endtime","ma_mrname"}, "ma_id=" + ma_id);
		checkIsUsed1(data[0], data[1], data[2]);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,new Object[] { ma_id});
		// 执行提交操作
		baseDao.submit("Meetingroomapply", "ma_id=" + ma_id, "ma_status", "ma_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "ma_id", ma_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller,new Object[] { ma_id});
	}

	@Override
	public void resSubmitMeetingroomapply(int ma_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Meetingroomapply",
				"ma_statuscode", "ma_id=" + ma_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller,new Object[] { ma_id});
		// 执行反提交操作
		baseDao.resOperate("Meetingroomapply", "ma_id=" + ma_id, "ma_status", "ma_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ma_id", ma_id);
		handlerService.afterResSubmit(caller,new Object[] { ma_id});
	}

	@Override
	@Transactional
	public void auditMeetingroomapply(int ma_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Meetingroomapply",
				"ma_statuscode", "ma_id=" + ma_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { ma_id});
		// 执行审核操作
		baseDao.audit("Meetingroomapply", "ma_id=" + ma_id, "ma_status", "ma_statuscode","ma_auditdate", "ma_auditer");
		String code = baseDao.getFieldValue("Meetingroomapply", "ma_code", "ma_id=" + ma_id, String.class);
		//通知与会人员
		notifyEmp(ma_id,"",caller,code,ma_id);
		// 记录操作
		baseDao.logger.audit(caller, "ma_id", ma_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { ma_id});
	}

	@Override
	public void resAuditMeetingroomapply(int ma_id, String caller) {
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, new Object[] { ma_id});
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Meetingroomapply",
				"ma_statuscode", "ma_id=" + ma_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("Meetingroomapply", "ma_id=" + ma_id, "ma_status", "ma_statuscode","ma_auditdate", "ma_auditer");
		// 记录操作
		baseDao.logger.resAudit(caller, "ma_id", ma_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, new Object[] { ma_id});
	}

	@Override
	public void confirmMan(String gridStore, String  caller) {//[{"md_id":3720,"md_meid":3165}]
		List<Map<Object, Object>> gStore=BaseUtil.parseGridStoreToMaps(gridStore);
		baseDao.execute("update MeetingDetail set md_attend='否' where md_meid="+gStore.get(0).get("md_meid"));//所有成员都默认为未出席
		List<String> sqls=new ArrayList<String>();
		for(Map<Object, Object> map:gStore){
			String sql="update MeetingDetail set md_attend='是' where md_id="+map.get("md_id");
			sqls.add(sql);
		}
		baseDao.execute(sqls);
	}

	@Override
	@Transactional
	public String turnDoc(int ma_id, String  caller) {
		String code=baseDao.sGetMaxNumber("MeetingDoc", 2);
		int id=baseDao.getSeqId("MeetingDoc_seq");	
		String sql="insert into MeetingDoc (md_id,md_fileno,md_meetingcode,md_starttime,md_endtime,md_group," +
				"md_groupid,md_recorder,md_recorderdate,md_status,md_statuscode,MD_MRCODE,MD_MRNAME,md_title,md_meetingname,md_meetingplace,md_meetingconvener,md_meetingparticipants) select  " +
				id+",'"+code+"',ma_code,ma_starttime,ma_endtime,ma_group,ma_groupid,'"+
				SystemSession.getUser().getEm_name()+"',sysdate,'在录入','ENTERING',ma_mrcode,ma_mrname,ma_theme,ma_theme,ma_mrname,ma_recorder,ma_group from Meetingroomapply where ma_id="+ma_id;
		baseDao.execute(sql);
		//会议申请单据修改状态
		baseDao.updateByCondition("Meetingroomapply", "ma_isturndoc='是'", "ma_id="+ma_id);
		baseDao.logger.others("转会议纪要", "转入成功", "Meetingroomapply", "ma_id", ma_id);
		return "转入成功,会议纪要单号:"
				+ "<a href=\"javascript:openUrl('jsps/oa/meeting/meetingDoc.jsp?formCondition=md_idIS"
				+ id + "')\">" + code
				+ "</a>&nbsp;";
	}
	/**
	 * 检查此会议室在该时间段是否有被预订
	 * 提示单号
	 */
	private void checkIsUsed1(Object starttime,Object endtime,Object roomname){
		String orderDay=starttime.toString().substring(0, 10);
		SqlRowList rs  = baseDao.queryForRowSet("select ma_id,ma_code from Meetingroomapply where "
				+ "(( to_date('"+endtime+"','yyyy-mm-dd hh24:mi:ss')>=ma_starttime and to_date('"+endtime+"','yyyy-mm-dd hh24:mi:ss')<=ma_endtime ) "
				+ "or (ma_endtime>=to_date('"+starttime+"','yyyy-mm-dd hh24:mi:ss') and ma_endtime <=to_date('"+endtime+"','yyyy-mm-dd hh24:mi:ss'))) "
				+ "and (ma_statuscode='COMMITED' OR ma_statuscode='AUDITED') and ma_mrname='"+roomname+"'"+" and to_date('"+orderDay+"','yyyy-mm-dd')=to_date(to_char(ma_starttime,'yyyy-mm-dd'),'yyyy-mm-dd')");
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
	 * 检查明细行人员编号是否重复
	 * 检查通知时间（通知时间不能小于当前日期）
	 * @throws ParseException 
	 */
	 private void checkGrid(List<Map<Object, Object>> grid) throws ParseException{
		//检查明细行人员编号是否存在重复
			for(int i=0;i<grid.size();i++){
				for(int j=i+1;j<grid.size();j++){
				if(grid.get(i).get("md_emcode").toString().equals(grid.get(j).get("md_emcode").toString())){
					BaseUtil.showError("明细行员工编号重复，行号："+grid.get(i).get("md_detno").toString()+"&nbsp&nbsp"+grid.get(j).get("md_detno").toString());
				}
				}
			}
			//通知时间不能小于当前日期
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
			String today=sdf.format(new Date());
			StringBuffer sb=new StringBuffer();
			sb.append("通知日期不能小于当前日期，行号：");
			for(int i=0;i<grid.size();i++){ 
				if(grid.get(i).get("md_noticetime")!=""&&grid.get(i).get("md_noticetime")!=null){
					if(sdf.parse(grid.get(i).get("md_noticetime").toString()).before(sdf.parse(today))){
						sb.append(grid.get(i).get("md_detno").toString()+"&nbsp&nbsp");
					}
				}
			}
			if(sb.length()>16){
				BaseUtil.showError(sb.toString());
			}
	 }
	/**
	 * 把虚拟组中的包含的员工都插入到明细中
	 */
	private void insertAllEmps(Object groupString,Object maid){
		//group:job#8;employee#106;employee#107;org#5
		Set<String> jobs = new HashSet<String>();// 岗位
		Set<String> emps = new HashSet<String>();// 人员
		Set<String> orgs = new HashSet<String>();// 组织
		for (String group : groupString.toString().split(";")) {
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
				List<Object> empids = baseDao
						.getFieldDatasByCondition(
								"HRORGEMPLOYEES left join employee on OM_EMID=em_id", "em_id",
								"em_class<>'离职' and OM_ORID=" + org);
				for (Object empid : empids) {
						emps.add(empid.toString());
				}
			}
		}
		if (jobs.size() > 0) {
			for (String job : jobs) {
				List<Object> empids = baseDao
						.getFieldDatasByCondition(
								"employee", "EM_id",
								"em_class<>'离职' and EM_DEFAULTHSID=" + job);
				for (Object empid : empids) {
					emps.add(empid.toString());
				}
			}
		}
		List<String> sqls = new ArrayList<String>();
		int detno = 1;
		for (String empid : emps) {
			String sqldetail = "insert into MeetingDetail(md_detno,md_maid,md_participantsid,md_participants,md_emcode,md_isnoticed,md_id,md_trueparter,MD_CONFIRMTIME) select "
					+ detno++
					+ ","
					+ maid
					+ ",em_id,em_name,em_code,-1,MeetingDetail_seq.nextval,em_name,null from employee where em_code not in (select md_emcode from MeetingDetail where md_maid="+maid+") and em_id="
					+ empid;
			sqls.add(sqldetail);
		}
		baseDao.execute(sqls);
	}
	/**
	 * 审核时发寻呼给与会成员
	 */
	private void notifyEmp(Object id,String type,String caller,String code,Integer keyvalue){
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
			if("CANCELED".equals(type))
				sb.append(SystemSession.getUser().getEm_name()+"取消了<a href=\"javascript:openUrl(''"
						+ url
						+ "?formCondition="
						+ formCondition
						+ "&gridCondition="
						+ gridCondition
						+"&_noc=1"
						+ "'')\" style=\"font-size:14px; color:blue;\">"+meetings[1]+"</a>会议</br>");				
			else sb.append(meetings[0]+"发起了<a href=\"javascript:openUrl(''"
					+ url
					+ "?formCondition="
					+ formCondition
					+ "&gridCondition="
					+ gridCondition
					+"&_noc=1"
					+ "'')\" style=\"font-size:14px; color:blue;\">"+meetings[1]+"</a>会议</br>");
			sqls.add("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,pr_istop,pr_from,pr_caller,pr_keyvalue,pr_codevalue)values('"
					+ pr_id
					+ "','"
					+  SystemSession.getUser().getEm_name()
					+ "',"
					+ DateUtil.parseDateToOracleString(Constant.YMD_HMS,
							new Date())
					+ ",'"
					+  SystemSession.getUser().getEm_id()
					+ "','" + sb.toString() + "',1,'meeting','"+caller+"','"+keyvalue+"','"+code+"')");
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
				//保存到历史消息表
				int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
				sqls.add("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
						+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
						+ " where pr_id="+pr_id);
				sqls.add("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
						+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+"and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");
				baseDao.execute(sqls);
		}
	}

	@Override
	@Transactional
	public void reLoad(int ma_id, String caller) {
		//删除原来的人员
		baseDao.deleteByCondition("MeetingDetail", "md_maid="+ma_id);
		//重新根据虚拟组插入与会人员
		insertAllEmps(baseDao.getFieldDataByCondition("Meetingroomapply", "ma_groupid", "ma_id="+ma_id), ma_id);
	}

	@Override
	@Transactional
	public void cancel(int ma_id, String caller) {
		boolean bool=baseDao.checkIf("Meetingroomapply", "ma_isturndoc='是' and ma_id="+ma_id);
		if (bool)BaseUtil.showError("已转会议纪要,请先删除对应会议纪要再取消该会议");
		Object status=baseDao.getFieldDataByCondition("Meetingroomapply", "ma_statuscode", "ma_id="+ma_id);
		baseDao.updateByCondition("Meetingroomapply", "ma_statuscode='CANCELED',ma_status='已取消'", "ma_id="+ma_id);
		String code = baseDao.getFieldValue("Meetingroomapply", "ma_code", "ma_id=" + ma_id, String.class);
		//如果单据之前的状态时已审核，则通知与会人员，会议已经取消
		if("AUDITED".equals(status)){
			notifyEmp(ma_id, "CANCELED","Meetingroomapply",code,ma_id);
		}
		baseDao.logger.others("取消会议", "取消成功", "Meetingroomapply", "ma_id", ma_id);
	}
}
