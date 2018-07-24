package com.uas.erp.service.plm.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DataListComboDao;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.dao.common.FormDao;
import com.uas.erp.model.DataListCombo;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Form;
import com.uas.erp.model.FormDetail;
import com.uas.erp.model.FormItems;
import com.uas.erp.model.FormPanel;
import com.uas.erp.service.plm.CheckService;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class CheckServiceImpl implements CheckService {
	@Autowired
	private FormDao formDao;
	@Autowired
	private DataListComboDao dataListComboDao;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private EmployeeDao employeeDao;

	@Override
	public FormPanel getFormItemsByCaller(String caller, String condition) {
		FormPanel formPanel = new FormPanel();
		Form form = formDao.getForm(caller, SpObserver.getSp());
		String data = baseDao.getDataStringByForm(form, condition.replace("cld_id", "ch_cldid"));
		// 取责任人
		if (data == null) {
			SqlRowSet rs = baseDao.getJdbcTemplate("CheckListDetail").queryForRowSet("SELECT * from CheckListDetail where " + condition);
			StringBuffer sb = new StringBuffer("{");
			if (rs.next()) {
				sb.append("ch_cldid:");
				sb.append(rs.getInt("cld_id"));
				sb.append(",ch_clid:");
				sb.append(rs.getInt("cld_clid"));
				sb.append(",ch_itemcode:");
				sb.append("\"" + rs.getString("cld_itemcode") + "\"");
				sb.append(",ch_itemname:");
				sb.append("\"" + rs.getString("cld_itemname") + "\"");
				sb.append(",ch_taskname:");
				sb.append("\"" + rs.getString("cld_taskname") + "\"");
				sb.append(",ch_testman:");
				sb.append("\"" + rs.getString("cld_testman") + "\"");
				sb.append(",ch_testdate:");
				sb.append("\"" + rs.getString("cld_testdate") + "\"");
			}
			// 查询任务责任人
			SqlRowSet rsman = baseDao.getJdbcTemplate("ResourceAssignment").queryForRowSet(
					"SELECT ra_emid,ra_resourcename from ResourceAssignment where ra_type='0' AND ra_taskid=" + rs.getObject("cld_taskid"));
			int count = 1;
			String str = "(";
			while (rsman.next()) {
				sb.append(",ch_man" + count + ":");
				sb.append("\"" + rsman.getString("ra_resourcename") + "\"");
				str += rsman.getInt("ra_emid") + ",";
			}
			sb.append(",ch_manemid:");
			sb.append("\"" + str.substring(0, str.lastIndexOf(",")) + ")\"");
			form.setDataString(sb.substring(0, sb.length()) + "}");
		} else
			form.setDataString(data);
		List<DataListCombo> combos = dataListComboDao.getComboxsByCaller(caller, SpObserver.getSp());
		List<FormDetail> formDetails = form.getFormDetails();
		List<FormItems> items = new ArrayList<FormItems>();
		Map<String, List<FormDetail>> map = new HashMap<String, List<FormDetail>>();// form分组
		for (FormDetail formDetail : formDetails) {
			if (!map.containsKey(formDetail.getFd_group())) {
				List<FormDetail> list = new ArrayList<FormDetail>();
				list.add(formDetail);
				map.put(formDetail.getFd_group(), list);
			} else {
				List<FormDetail> list = map.get(formDetail.getFd_group());
				list.add(formDetail);
				map.put(formDetail.getFd_group(), list);
			}
		}
		Iterator<String> iterator = map.keySet().iterator();
		int count = 1;
		while (iterator.hasNext()) {
			String group = iterator.next();
			List<FormDetail> list = map.get(group);
			items.add(new FormItems(count, group));
			for (FormDetail formDetail : list) {
				items.add(new FormItems(count, group, formDetail, combos));
			}
			count++;
		}
		formPanel.setData(form.getDataString());
		formPanel.setItems(items);
		formPanel.setButtons(form.getFo_button4add());
		return formPanel;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveCheck(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<String> sqls = new ArrayList<String>();
		handlerService.handler("Check", "save", "before", new Object[] { store });
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "CheckListDetail", new String[] {}, new Object[] {});
		// 同时更新checklistDetail
		String updateSql = "update CheckListDetail set cld_result='" + store.get("ch_result") + "', cld_description='"
				+ store.get("ch_remark") + "',cld_status='" + store.get("ch_dostatus") + "' where cld_id=" + store.get("ch_cldid");
		sqls.add(formSql);
		sqls.add(updateSql);
		baseDao.execute(sqls);
		baseDao.logger.update("Check", "cld_id", store.get("cld_id"));
		handlerService.handler("Check", "save", "after", new Object[] { store });
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateCheck(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<String> sqls = new ArrayList<String>();
		String language = SystemSession.getLang();
		handlerService.handler("Check", "save", "before", new Object[] { store });
		store.remove("cld_status");
		store.remove("cld_statuscode");
		store.remove("cl_prjplanname");
		store.remove("cl_prjplanid");
		// 插入语句
		int ch_id = baseDao.getSeqId("CHECKTABLE_SEQ");
		String description = null;
		String type = null;
		String statuscode = null;
		String status = null;
		int confirmed = -1;
		StringBuffer sb = new StringBuffer();
		Employee employee = SystemSession.getUser();
		if (Integer.parseInt(store.get("cld_newhandmanid").toString()) == employee.getEm_id()) {
			type = "Handle";
			description = "cld_handdescription";
			if("HUNG".equals(store.get("cld_handresult"))){
				type = "Pause";
				statuscode = "HANDUP";
				status = "已挂起";
			}else{
				statuscode = "TESTING";
				status = BaseUtil.getLocalMessage(statuscode);
				confirmed = 0;
				baseDao.updateByCondition("CheckListBaseDetail", "cbd_status='"+BaseUtil.getLocalMessage("TESTING", language)+"',cbd_statuscode='TESTING'", "cbd_name='"+store.get("cld_name")+"' and cbd_id="+store.get("cld_cbdid")+"");
				baseDao.updateByCondition("CHECKHISTORY", "ch_cbdstatus='"+BaseUtil.getLocalMessage("TESTING", language)+"'", "ch_cbdcode='"+store.get("cld_name")+"' and ch_cldid="+store.get("cld_id")+"");
				// 给测试人员发寻呼
				int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
				int prd_id = baseDao.getSeqId("PAGINGRELEASEDETAIL_SEQ");
				sb.setLength(0);
				/*sb.append("任务提醒["
						+ DateUtil.parseDateToString(DateUtil.parseStringToDate(null, Constant.YMD_HMS), "MM-dd HH:mm") + "]</br>");
				sb.append("<a href=\"javascript:openGridUrl(" + store.get("cld_id")
				+ ",''cld_id'',''ch_cldid'',''jsps/plm/test/check.jsp'',''Check单''" + ")\">" + store.get("cld_name") + "</a></br>");*/
				sb.append("("+employee.getEm_name()+")回复了你提出的BUG！");
				System.out.println(sb);
				sqls.add("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,pr_from)values('" + pr_id + "','"
						+ employee.getEm_name() + "'," + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",'"
						+ employee.getEm_id() + "','" + sb.toString() + "','task')");
				sqls.add("insert into pagingreleasedetail(prd_id,prd_prid,PRD_RECIPIENTID,PRD_RECIPIENT) values('" + prd_id + "','" + pr_id
						+ "','" + store.get("cld_newtestmanid") + "','" + store.get("cld_newtestman") + "')");
				//保存到历史消息表
				int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
				sqls.add("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
						+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
						+ " where pr_id="+pr_id);
				sqls.add("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
						+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+"and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");
			}
		} else {
			type = "Test";
			description = "cld_testdescription";
			if (store.get("cld_testresult").equals("-1")) {
				statuscode = "HANDED";
				store.put("cld_realenddate", DateUtil.parseDateToString(new Date(), Constant.YMD_HMS));
				baseDao.updateByCondition("CheckListBaseDetail", "cbd_status='"+BaseUtil.getLocalMessage("HANDED", language)+"',cbd_statuscode='HANDED',cbd_result='"+store.get("cld_testdescription")+"'", "cbd_name='"+store.get("cld_name")+"' and cbd_id="+store.get("cld_cbdid")+"");
				baseDao.updateByCondition("CHECKHISTORY", "ch_cbdstatus='"+BaseUtil.getLocalMessage("HANDED", language)+"'", "ch_cbdcode='"+store.get("cld_name")+"' and ch_cldid="+store.get("cld_id")+"");
				int fbid = Integer.parseInt(store.get("cld_id").toString());
				updateFeedback(fbid);
				
				sb.append("("+employee.getEm_name()+")已回复了你处理的BUG！");
				int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
				int prd_id = baseDao.getSeqId("PAGINGRELEASEDETAIL_SEQ");
				sqls.add("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,pr_from)values('" + pr_id + "','"
						+ employee.getEm_name() + "'," + DateUtil.parseDateToOracleString(Constant.YMD, new Date()) + ",'"
						+ employee.getEm_id() + "','" + sb.toString() + "','task')");
				sqls.add("insert into pagingreleasedetail(prd_id,prd_prid,PRD_RECIPIENTID,PRD_RECIPIENT) values('" + prd_id + "','" + pr_id
						+ "','" + store.get("cld_newhandmanid") + "','" + store.get("cld_newhandman") + "')");
				//保存到历史消息表
				int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
				sqls.add("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
						+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
						+ " where pr_id="+pr_id);
				sqls.add("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
						+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+"and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");
				
			} else if (store.get("cld_testresult").equals("1")) {
				// 不做处理的BUG
				statuscode = "NOTDEAL";
				store.put("cld_realenddate", DateUtil.parseDateToString(new Date(), Constant.YMD_HMS));
				// sqls.add("update CheckListBaseDetail set cbd_status='"+BaseUtil.getLocalMessage("NOTDEAL",
				// language)+"',cbd_statuscode='NOTDEAL' where cbd_id="+store.get("cld_cbdid"));
			} else {
				statuscode = "PENDING";
				// 插入
				int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
				int prd_id = baseDao.getSeqId("PAGINGRELEASEDETAIL_SEQ");
				/*sb.append("任务提醒["
						+ DateUtil.parseDateToString(DateUtil.parseStringToDate(null, Constant.YMD_HMS), "MM-dd HH:mm") + "]");
				sb.append("<a href=\"javascript:openGridUrl(" + store.get("cld_id")
						+ ",''cld_id'',''ch_cldid'',''jsps/plm/test/check.jsp'',''Check单''" + ")\">" + store.get("cld_name") + "</a>");*/
				//sb.append("你有处理的BUG测试未通过!");
				sb.append("("+employee.getEm_name()+")已回复了你处理的BUG!");
				System.out.println(sb);
				sqls.add("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,pr_from)values('" + pr_id + "','"
						+ employee.getEm_name() + "'," + DateUtil.parseDateToOracleString(Constant.YMD, new Date()) + ",'"
						+ employee.getEm_id() + "','" + sb.toString() + "','task')");
				sqls.add("insert into pagingreleasedetail(prd_id,prd_prid,PRD_RECIPIENTID,PRD_RECIPIENT) values('" + prd_id + "','" + pr_id
						+ "','" + store.get("cld_newhandmanid") + "','" + store.get("cld_newhandman") + "')");
				//保存到历史消息表
				int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
				sqls.add("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
						+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
						+ " where pr_id="+pr_id);
				sqls.add("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
						+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+"and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");
			}
			status = BaseUtil.getLocalMessage(statuscode);
		}
		store.put("cld_isconfirmed", confirmed);
		store.put("cld_status", status);
		store.put("cld_statuscode", statuscode);
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "CheckListDetail", "cld_id");
		String insertSql = "insert into checktable(ch_id,ch_cldid,ch_recorder,ch_recorddate,ch_description,ch_type,ch_detno) values('"
				+ ch_id + "','" + store.get("cld_id") + "','" + employee.getEm_name() + "',"
				+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",'" + store.get(description) + "','" + type + "','"
				+ ch_id + "')";
		sqls.add(insertSql);
		sqls.add(formSql);
		baseDao.execute(sqls);
		// 查询确认时间是否为空 则更新相应的确认时间
		baseDao.updateByCondition("CheckListDetail", "cld_confirmdate=" + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()),
				"cld_id=" + store.get("cld_id") + " and  cld_confirmdate is null");
		baseDao.logger.update("Check", "cld_id", store.get("cld_id"));
		handlerService.handler("Check", "save", "after", new Object[] { store });
	}

	@Override
	public void submitCheck(int id) {
		handlerService.beforeSubmit("Check", id);
		// 执行提交操作
		baseDao.submit("CheckTable", "ch_id=" + id, "ch_status", "ch_statuscode");
		// 两种类型 测试记录 提交记录
		SqlRowList sl = baseDao.queryForRowSet("select *  from CheckListDetail  where cld_id=" + id);
		if (sl.next()) {
			int handmanid = sl.getInt("cld_newhandmanid");
			if (handmanid == SystemSession.getUser().getEm_id()) {
				// 说明是提交操作
				int checkid = baseDao.getSeqId("CHECKTABLE_SEQ");
				// String
				// insertsql="insert into CheckTable (ct_id,ct_type,ct_description,ct_cldid,ct_sumbmiter) values('"+checkid+"','TEST','"++"','";
			}
		}
		baseDao.logger.submit("Check", "cld_id", id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit("Check", id);
	}

	@Override
	public void resubmitCheck(int id) {
		handlerService.beforeResSubmit("Check", id);
		// 执行提交操作
		baseDao.resOperate("CheckTable", "ch_id=" + id, "ch_status", "ch_statuscode");
		// 执行提交后的其它逻辑
		baseDao.logger.resSubmit("Check", "cld_id", id);
		handlerService.afterResSubmit("Check", id);
	}

	@Override
	public void auditCheck(int id) {
		handlerService.beforeAudit("Check", id);
		// 执行提交操作
		baseDao.audit("CheckTable", "ch_id=" + id, "ch_status", "ch_statuscode");
		baseDao.logger.audit("Check", "cld_id", id);
		// 执行提交后的其它逻辑
		handlerService.afterAudit("Check", id);
	}

	@Override
	public void reauditCheck(int id) {
		// 执行提交操作
		baseDao.resOperate("CheckTable", "ch_id=" + id, "ch_status", "ch_statuscode");
		// 执行提交后的其它逻辑
		baseDao.logger.resAudit("Check", "cld_id", id);
	}

	@Override
	public void changeBugStatus(int id) {
		baseDao.updateByCondition("CheckListDetail", "cld_isconfirmed=0", "cld_id=" + id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void changeHandler(int id, int oldemid, int newemid, String description) {
		Employee employee = SystemSession.getUser();
		Employee newem = employeeDao.getEmployeeByEmId(newemid);
		Employee oldem = employeeDao.getEmployeeByEmId(oldemid);
		baseDao.updateByCondition("CheckListDetail", "cld_newhandmanid='" + newemid + "',cld_newhandman='" + newem.getEm_name()
				+ "',cld_isconfirmed=0,cld_confirmdate=null", "cld_id='" + id + "'");
		StringBuffer sb = new StringBuffer();
		List<String> insertSqls = new ArrayList<String>();
		sb.append("任务提醒&nbsp;&nbsp;&nbsp;&nbsp;["
				+ DateUtil.parseDateToString(DateUtil.parseStringToDate(null, Constant.YMD_HMS), "MM-dd HH:mm") + "]</br>");
		sb.append("你有新 [" + oldem.getEm_name() + "]转过来的BUG待处理快去看看吧!</br></br>");
		int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
		int prd_id = baseDao.getSeqId("PAGINGRELEASEDETAIL_SEQ");
		insertSqls.add("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,pr_from)values('" + pr_id + "','"
				+ employee.getEm_name() + "'," + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",'"
				+ employee.getEm_id() + "','" + sb.toString() + "','task')");
		insertSqls.add("insert into pagingreleasedetail(prd_id,prd_prid,PRD_RECIPIENTID,PRD_RECIPIENT) values('" + prd_id + "','" + pr_id
				+ "','" + newemid + "','" + newem.getEm_name() + "')");
		//保存到历史消息表
		int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
		insertSqls.add("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
				+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
				+ " where pr_id="+pr_id);
		insertSqls.add("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
				+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+"and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");

		// 插入变更记录
		insertSqls.add("insert into checkchange (cc_id,cc_date,cc_handler,cc_turn,cc_description,cc_cldid)values('"
				+ baseDao.getSeqId("CHECKCHANGE_SEQ") + "'," + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",'"
				+ employee.getEm_name() + "','" + oldem.getEm_name() + ";" + newem.getEm_name() + "','" + description + "','" + id + "')");
		baseDao.execute(insertSqls);
	}

	/* 更新系统问题反馈 */
	@Override
	public void updateFeedback(int clid) {
		int fbid = baseDao.getCount("select cld_sourceid from checklistdetail where cld_id=" + clid);
		if (fbid != 0) {
			String sql = "update feedback set fb_uasstatus='待发布',fb_sendstatus='待上传' where fb_id=" + fbid;
			baseDao.execute(sql);
		}
	}

	@Override
	public void deleteCheck(int id) {
		handlerService.beforeDel("Check", id);
		baseDao.deleteById("CheckListDetail", "cld_id", id);
		// 记录操作
		baseDao.logger.delete("Check", "cld_id", id);
		// 执行删除后的其它逻辑
		handlerService.afterDel("Check", id);
	}

	@Override
	public void confirm(int id) {
		// 更新状态
		baseDao.updateByCondition("checklistdetail",
				"cld_isconfirmed='-1',cld_confirmdate=" + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()), "cld_id=" + id);
		int chid = baseDao.getSeqId("CHECKTABLE_SEQ");
		String insertSql = "insert into checktable(ch_id,ch_cldid,ch_recorder,ch_recorddate,ch_description,ch_type,ch_detno) values("
				+ chid + "," + id + ",'" + SystemSession.getUser().getEm_name() + "',"
				+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",'确认BUG','CONFIRM','" + chid + "')";
		baseDao.execute(insertSql);
	}
}
