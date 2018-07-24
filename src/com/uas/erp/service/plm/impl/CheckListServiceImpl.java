package com.uas.erp.service.plm.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
import com.uas.erp.model.Employee;
import com.uas.erp.model.Notification;
import com.uas.erp.service.plm.CheckListService;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class CheckListServiceImpl implements CheckListService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCheckList(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(gridStore);
		Map<Object, Object> rowmap = new HashMap<Object, Object>();
		List<String> gridSqls = new ArrayList<String>();
		Employee employee = SystemSession.getUser();
		// 执行保存前的其它逻辑
		handlerService.beforeSave("CheckList", new Object[] { store, rowmap });
		// 保存CheckList
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "CheckList", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存CheckListDetail
		for (int i = 0; i < maps.size(); i++) {
			rowmap = maps.get(i);
			rowmap.remove("cld_status");
			rowmap.remove("cld_id");
			rowmap.put("cld_id", baseDao.getSeqId("CHECKLISTDETAIL_SEQ"));
			if (rowmap.get("cld_newtestman") == null || rowmap.get("cld_newtestman").equals("")) {
				rowmap.remove("cld_newtestman");
				rowmap.remove("cld_newtestmanid");
				rowmap.put("cld_newtestman", employee.getEm_name());
				rowmap.put("cld_newtestmanid", employee.getEm_id());
			}
			rowmap.put("cld_exhibitor", employee.getEm_name());
			rowmap.put("cld_exhibitorid", employee.getEm_id());
			rowmap.put("cld_isconfirm", "-1");
			rowmap.put("cld_exhibitdate", DateUtil.parseDateToString(new Date(), Constant.YMD_HMS));
			gridSqls.add(SqlUtil.getInsertSqlByMap(rowmap, "CheckListDetail"));
		}
		baseDao.execute(gridSqls);
		baseDao.logger.save("CheckList", "cl_id", store.get("cl_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave("CheckList", new Object[] { store, rowmap });
	}

	@Override
	public void deleteCheckList(int id) {
		Object status = baseDao.getFieldDataByCondition("CheckList", "cl_statuscode", "cl_id=" + id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel("CheckList", id);
		// 删除CheckList
		baseDao.deleteById("CheckList", "cl_id", id);
		// 删除CheckListDetail
		baseDao.deleteById("CheckListdetail", "cld_clid", id);
		// 记录操作
		baseDao.logger.delete("CheckList", "cl_id", id);
		// 执行删除后的其它逻辑
		handlerService.afterDel("CheckList", id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateCheckList(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gridmaps = BaseUtil.parseGridStoreToMaps(gridStore);
		Map<Object, Object> map = null;
		Object status = baseDao.getFieldDataByCondition("CheckList", "cl_statuscode", "cl_id=" + store.get("cl_id"));
		StateAssert.updateOnlyEntering(status);
		Employee employee = SystemSession.getUser();
		handlerService.beforeSave("CheckList", new Object[] { store, gridmaps });
		// 修改CheckList
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "CheckList", "cl_id");
		baseDao.execute(formSql);
		// 修改CheckListDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "CheckListDetail", "cld_id");
		for (int i = 0; i < gridmaps.size(); i++) {
			map = gridmaps.get(i);
			if (map.get("cld_id") == null || map.get("cld_id").equals("") || map.get("cld_id").equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("CHECKLISTDETAIL_SEQ");
				map.remove("cld_status");
				if (map.get("cld_newtestman") == null || map.get("cld_newtestman").equals("")) {
					map.remove("cld_newtestman");
					map.remove("cld_newtestmanid");
					map.put("cld_newtestman", employee.getEm_name());
					map.put("cld_newtestmanid", employee.getEm_id());
				}
				map.put("cld_exhibitor", employee.getEm_name());
				map.put("cld_exhibitorid", employee.getEm_id());
				map.put("cld_exhibitdate", DateUtil.parseDateToString(new Date(), Constant.YMD_HMS));
				String sql = SqlUtil.getInsertSqlByMap(map, "CheckListDetail", new String[] { "cld_id" },
						new Object[] { id });
				gridSql.add(sql);
			} else {
				gridSql.add(SqlUtil.getUpdateSqlByFormStore(map, "CheckListDetail", "cld_id"));
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update("CheckList", "cl_id", store.get("cl_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave("CheckList", new Object[] { store, gridmaps });
	}

	@Override
	public void auditCheckList(int id) {
		Object status = baseDao.getFieldDataByCondition("CheckList", "cl_statuscode", "cl_id=" + id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit("CheckList", id);
		// 发送寻呼
		sendMsg(id);
		// 执行审核操作
		baseDao.audit("CheckList", "cl_id=" + id, "cl_status", "cl_statuscode");
		// 记录操作
		baseDao.logger.audit("CheckList", "cl_id", id);
		handlerService.afterAudit("CheckList", id);
	}
	
	/**
	 * 发送寻呼
	 */
	private void sendMsg(int clid) {
		baseDao.execute("update checklistdetail set cld_newhandmanid=(select max(em_id) from employee where em_name=cld_newhandman) where cld_clid=? and nvl(cld_newhandmanid,0)=0", clid);
		SqlRowList sl = baseDao.queryForRowSet("select * from checkListdetail where cld_clid=" + clid);
		List<String> insertSqls = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		Employee employee = SystemSession.getUser();
		while (sl.next()) {
			int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
			int prd_id = baseDao.getSeqId("PAGINGRELEASEDETAIL_SEQ");
			int handmanid = sl.getInt("cld_newhandmanid");
			if (handmanid == 0 || handmanid == -1) {
				BaseUtil.showError("第" + sl.getInt("cld_detno") + "行   处理人未选择 审核不能通过,请重新设定!");
				return;
			}
			String cld_name = sl.getString("cld_name").toString();
			if(cld_name.contains("'")){
				cld_name = cld_name.replace("'","''");
			}
			sb.setLength(0);
			sb.append("任务提醒&nbsp;&nbsp;&nbsp;&nbsp;["
					+ DateUtil.parseDateToString(DateUtil.parseStringToDate(null, "yyyy-MM-dd HH:mm:ss"), "MM-dd HH:mm")
					+ "]</br>");
			sb.append("<a href=\"javascript:openGridUrl(" + sl.getInt("cld_id")
					+ ",''cld_id'',''ch_cldid'',''jsps/plm/test/check.jsp'',''Check单''" + ")\">"
					+ cld_name + "</a></br>");
			sb.append("<button onclick=''onUrlClick(\"plm/check/changeBugStatus.action?id=" + sl.getInt("cld_id")
					+ "\");''>确认</button></br>");
			sb.append("你有新的待处理BUG快去看看吧!</br></br>");
			insertSqls.add("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,pr_from)values('"
					+ pr_id + "','" + employee.getEm_name() + "',"
					+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",'" + employee.getEm_id()
					+ "','" + sb.toString() + "','task')");
			insertSqls.add("insert into pagingreleasedetail(prd_id,prd_prid,PRD_RECIPIENTID,PRD_RECIPIENT) values('"
					+ prd_id + "','" + pr_id + "','" + handmanid + "','" + sl.getString("cld_newhandman") + "')");		
			//保存到历史消息表
			int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
			insertSqls.add("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
					+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
					+ " where pr_id="+pr_id);
			insertSqls.add("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
					+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+"and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");	
		}
		baseDao.execute(insertSqls);
	}
	
	/**
	 * 通过UU消息接口发送消息
	 */
	private void sendUUMsg(int fId) {
		Notification nf = new Notification();
		baseDao.save(nf);
	}
	
	@Override
	public void submitCheckList(int id) {
		Object status = baseDao.getFieldDataByCondition("CheckList", "cl_statuscode", "cl_id=" + id);
		StateAssert.submitOnlyEntering(status);
		handlerService.beforeSubmit("CheckList", id);
		// 执行操作
		baseDao.submit("CheckList", "cl_id=" + id, "cl_status", "cl_statuscode");
		// 记录操作
		baseDao.logger.submit("CheckList", "cl_id", id);
		handlerService.afterSubmit("CheckList", id);
	}

	@Override
	public void reSubmitCheckList(int cl_id) {
		Object status = baseDao.getFieldDataByCondition("CheckList", "cl_statuscode", "cl_id=" + cl_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit("CheckList", cl_id);
		// 执行反提交操作
		baseDao.resOperate("CheckList", "cl_id=" + cl_id, "cl_status", "cl_statuscode");
		// 记录操作
		baseDao.logger.resSubmit("CheckList", "cl_id", cl_id);
		handlerService.afterResSubmit("CheckList", cl_id);
	}

	@Override
	public void resAuditCheckList(int cl_id) {
		Object status = baseDao.getFieldDataByCondition("CheckList", "cl_statuscode", "cl_id=" + cl_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("CheckList", "cl_id=" + cl_id, "cl_status", "cl_statuscode");
		// 记录操作
		baseDao.logger.resAudit("CheckList", "cl_id", cl_id);
	}
}
