package com.uas.erp.service.plm.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import com.uas.erp.service.plm.ProjectReviewService;

@Service
public class ProjectReviewServiceImpl implements ProjectReviewService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void loadKeyDevice(String producttype, int prid) {
		// 载入关键元器件载入前将删除原来的
		baseDao.execute("delete  from projectkeyDevice where pkd_prid=" + prid);
		SqlRowList sl = baseDao.queryForRowSet("select kd_name from KeyDevice where kd_productkind='" + producttype + "'");
		List<String> sqls = new ArrayList<String>();
		int detno = 1;
		while (sl.next()) {
			sqls.add("insert into projectkeyDevice(pkd_id,pkd_name,pkd_detno,pkd_prid) values('" + baseDao.getSeqId("PROJECTKEYDEVICE_SEQ")
					+ "','" + sl.getObject(1) + "','" + detno + "','" + prid + "')");
			detno++;
		}
		baseDao.execute(sqls);
	}

	@SuppressWarnings("rawtypes")
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateProjectReview(String formStore, String param1, String param2) {
		List<String> sqls = new ArrayList<String>();
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Iterator it = store.keySet().iterator();
		Map<Object, Object> laststore = store;
		while (it.hasNext()) {
			Object entry = it.next();
			String str = entry.toString();
			if (str.contains("rating_")) {
				it.remove();
			}
		}
		sqls.add(SqlUtil.getUpdateSqlByFormStore(laststore, "ProjectReview", "pr_id"));
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(param1);
		if(maps.size() > 0){
			Map<Object, Object> map = new HashMap<Object, Object>();
			Object keyValue = null;
			for (int i = 0; i < maps.size(); i++) {
				map = maps.get(i);
				keyValue = map.get("pp_id");
				if (keyValue != null && !keyValue.equals("0") && !keyValue.equals("")) {
					sqls.add(SqlUtil.getUpdateSqlByFormStore(map, "ProjectPhase", "pp_id"));
				} else {
					sqls.add(SqlUtil.getInsertSqlByMap(map, "ProjectPhase", new String[] { "pp_id" },
							new Object[] { baseDao.getSeqId("PROJECTPHASE_SEQ") }));
				}
			}
		}
		List<Map<Object, Object>> maps2 = BaseUtil.parseGridStoreToMaps(param2);
		if(maps2.size() > 0){
			Map<Object, Object> map2 = new HashMap<Object, Object>();
			Object keyValue2 = null;
			for (int i = 0; i < maps2.size(); i++) {
				map2 = maps2.get(i);
				keyValue2 = map2.get("pcb_id");
				if (keyValue2 != null && !keyValue2.equals("0") && !keyValue2.equals("")) {
					sqls.add(SqlUtil.getUpdateSqlByFormStore(map2, "ProjectCostBudget", "pcb_id"));
				} else {
					sqls.add(SqlUtil.getInsertSqlByMap(map2, "ProjectCostBudget", new String[] { "pcb_id" },
							new Object[] { baseDao.getSeqId("PROJECTCOSTBUDGET_SEQ") }));
				}
			}
		}
		baseDao.execute(sqls);
		baseDao.logger.update("ProjectReview", "pr_id", store.get("pr_id"));
	}

	public void submitProjectReview(int id) {
		Object status = baseDao.getFieldDataByCondition("ProjectReview", "pr_statuscode", "pr_id=" + id);
		StateAssert.submitOnlyEntering(status);
		handlerService.beforeSubmit("ProjectReview", id);
		baseDao.submit("ProjectReview", "pr_id=" + id, "pr_status", "pr_statuscode");
		Object prj_id=baseDao.getFieldDataByCondition("PROJECTREVIEW left join project on pr_prjcode=prj_code", "prj_id","pr_id=" + id);
		baseDao.execute("update ProjectPhase set pp_prjid ="+prj_id+" where pp_prid ="+id);
		// 记录操作
		baseDao.logger.submit("ProjectReview", "pr_id", id);
		handlerService.afterSubmit("ProjectReview", id);
	}

	public void resSubmitProjectReview(int id) {
		Object status = baseDao.getFieldDataByCondition("ProjectReview", "pr_statuscode", "pr_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit("ProjectReview", id);
		// 执行反操作
		baseDao.resOperate("ProjectReview", "pr_id=" + id, "pr_status", "pr_statuscode");
		// 记录操作
		baseDao.logger.resSubmit("ProjectReview", "pr_id", id);
		handlerService.afterResSubmit("ProjectReview", id);
	}

	public void resAuditProjectReview(int id) {
		Object status = baseDao.getFieldDataByCondition("ProjectReview", "pr_statuscode", "pr_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("ProjectReview", "pr_id=" + id, "pr_status", "pr_statuscode");
		// 记录操作
		baseDao.logger.resAudit("ProjectReview", "pr_id", id);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void auditProjectReview(int id) {
		Object status = baseDao.getFieldDataByCondition("ProjectReview", "pr_statuscode", "pr_id=" + id);
		StateAssert.auditOnlyCommited(status);
		handlerService.beforeAudit("ProjectReview", id);
		// 审核操作
		baseDao.audit("ProjectReview", "pr_id=" + id, "pr_status", "pr_statuscode");
		Object[] data = baseDao.getFieldsDataByCondition("ProjectReview", "pr_prjid,pr_chargeperson,pr_chargedepart", "pr_id=" + id);
		// 更新项目状态变成已启动 //更新项目的责任人
		baseDao.updateByCondition("Project", "prj_statuscode='DOING',prj_status='" + BaseUtil.getLocalMessage("DOING")
				+ "',prj_assignto='" + data[1] + "',prj_dept='" + data[2] + "'", "prj_id=" + data[0]);
		// 更新里程碑实际开始时间，里程碑状态为进行中,立项中的当前状态为当前的里程碑
		SqlRowList rs = baseDao.queryForRowSet(
						"select pp_detno,pp_phase from (select pp_detno,pp_phase from ProjectPhase where pp_prid=? order by pp_detno) where rownum=1",
						id);
		if (rs.next()) {
			baseDao.updateByCondition("ProjectPhase", "pp_status='进行中',pp_realstartdate=sysdate", "pp_detno="+rs.getObject("pp_detno")+" and pp_prid="+id);
			baseDao.updateByCondition("Project", "prj_phase='" + rs.getObject("pp_phase") + "'", "prj_id="+data[0]);
		}
		// 记录操作
		baseDao.logger.audit("ProjectReview", "pr_id", id);
		handlerService.afterAudit("ProjectReview", id);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void planMainTask(int id) {
		Object[] data = baseDao.getFieldsDataByCondition("ProjectReview",new String[]{"pr_statuscode","pr_prjid"}, "pr_id=" + id);
		if (data!=null && !data[0].equals("AUDITED")) {
			BaseUtil.showError("状态非已审核");
		}
		// 转研发任务书操作
		// 允许转多个研发任务书
		/*
		 * baseDao.updateByCondition("ProjectReview",
		 * "pr_statuscode='TURNMT',pr_status='" +
		 * BaseUtil.getLocalMessage("TURNMT") + "'", "pr_id=" + id);
		 */
		// 转入操作
		// 查看当前项目评审是否存在相应的 录入状态的研发任务书
		Object[] info = baseDao.getFieldsDataByCondition("ProjectMainTask", new String[]{"pt_code","pt_id"}, "pt_prjid='" + data[1] + "' and pt_statuscode='ENTERING'");
		if (info != null ) {
			BaseUtil.showError("该评审已存在相应在录入的任务书编号:"
					+ "<a href=\"javascript:openUrl('jsps/plm/task/projectmaintask.jsp?formCondition=pt_idIS" + info[1]
					+ "&gridCondition=ptidIS" + info[1] + "')\">" + info[0] + "</a>&nbsp;");
		}
		SqlRowList sl = baseDao.queryForRowSet("select * from ProjectReview left join Project on pr_prjid=prj_id  where pr_id=?", id);
		String insertSql = "";
		StringBuffer sb = new StringBuffer();
		int ptid= baseDao.getSeqId("PROJECTMAINTASK_SEQ");
		String code =  baseDao.sGetMaxNumber("PROJECTMAINTASK", 2);
		if (sl.next()) {
			Map<String, Object> map = sl.getCurrentMap();
			
			
			sb.append("insert into projectmaintask (pt_id,pt_code,pt_prjid,pt_prjcode,pt_prjname,pt_orger,pt_orgerdept,pt_prcode,pt_statuscode,pt_status,pt_recorder,pt_recorddate,pt_startdate,pt_enddate,pt_producttype) values(");
			sb.append("'" + ptid+ "','" + code + "','" + map.get("pr_prjid") + "','"
					+ map.get("pr_prjcode") + "','" + map.get("pr_prjname") + "','" + map.get("prj_assignto") + "',");
			sb.append("'" + map.get("prj_organigerdep") + "','" + map.get("pr_code") + "','ENTERING','"
					+ BaseUtil.getLocalMessage("ENTERING") + "','" + SystemSession.getUser().getEm_name() + "',"
					+ DateUtil.parseDateToOracleString(Constant.YMD, new Date()) + ",");
			sb.append("to_date('" + map.get("prj_start").toString().substring(0, 10) + "','yyyy-MM-dd'),to_date('"
					+ map.get("prj_end").toString().substring(0, 10) + "','yyyy-MM-dd'),'" + map.get("pr_producttype") + "')");
		}
		insertSql = sb.toString();
		baseDao.execute(insertSql);
		BaseUtil.appendError("生成成功,任务单号:" + "<a href=\"javascript:openUrl('jsps/plm/task/projectmaintask.jsp?formCondition=pt_idIS"+ptid+"&gridCondition=ptidIS"+ptid+"')\">" + code + "</a>&nbsp;");
	}

	@Override
	public void reviewupdate(String reviewitem, String reviewresult, int id) {
		baseDao.updateByCondition("ProjectReview", "pr_reviewitem='" + reviewitem + "',pr_reviewresult='" + reviewresult + "'", "pr_id="
				+ id);
	}

	@Override
	public void deleteProjectReview(int id) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("ProjectReview", "pr_statuscode", "pr_id=" + id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler("ProjectReview", "delete", "before", new Object[] { id });
		// 还原原单据的状态
		Object prjcode = baseDao.getFieldDataByCondition("ProjectReview", "pr_prjcode", "pr_id=" + id);
		if (prjcode != null) {
			baseDao.audit("Project", "prj_code='" + prjcode + "'", "prj_status", "prj_statuscode");
		}
		// 删除AssistRequire
		baseDao.deleteById("ProjectReview", "pr_id", id);
		// 删除Contact
		baseDao.deleteById("ProjectPhase", "pp_prid", id);
		// 记录操作
		baseDao.logger.delete("ProjectReview", "pr_id", id);
		// 执行删除后的其它逻辑
		handlerService.handler("ProjectReview", "delete", "after", new Object[] { id });
	}
}
