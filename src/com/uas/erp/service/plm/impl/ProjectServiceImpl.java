package com.uas.erp.service.plm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ProjectDao;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.plm.ProjectService;

@Service("projectService")
public class ProjectServiceImpl implements ProjectService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private ProjectDao projectDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProject(String formStore) {
		String formSql = "";
		Map<Object, Object> mapstore = BaseUtil.parseFormStoreToMap(formStore);
		formSql = SqlUtil.getInsertSqlByFormStore(mapstore, "Project", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.logger.save("Project", "prj_id", mapstore.get("prj_id"));
	}

	@Override
	public void deleteProject(int prj_id) {
		// 来源为项目评估（ProjectEvaluation）更新项目评估已转立项(pe_turn)为否(0)
		baseDao.updateByCondition("projectEvaluation", " pe_turn=0",
				"pe_code =(select nvl(prj_source,0) from project where prj_sourcetype='ProjectEvaluation' and prj_id=" + prj_id + ")");
		baseDao.deleteById("Project", "prj_id", prj_id);
		baseDao.logger.delete("Project", "prj_id", prj_id);
	}

	@Override
	public void updateProject(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Project", "prj_id");
		baseDao.execute(formSql);
		baseDao.logger.update("Project", "prj_id", store.get("prj_id"));
	}

	@Override
	public void submitProject(int prj_id) {
		handlerService.handler("Project", "commit", "before", new Object[] { prj_id });
		// 执行提交操作
		baseDao.submit("Project", "prj_id=" + prj_id, "prj_status", "prj_statuscode");
		// 记录操作
		baseDao.logger.submit("Project", "prj_id", prj_id);
		// 执行提交后的其它逻辑
		handlerService.handler("Project", "commit", "after", new Object[] { prj_id });

	}

	@Override
	@Transactional
	public void auditProject(int prj_id,String  caller) {
		handlerService.handler("Project", "audit", "before", new Object[] { prj_id });
		baseDao.audit("Project", "prj_id=" + prj_id, "prj_status", "prj_statuscode");
		if (baseDao.isDBSetting("Project", "assProject")) {
			SqlRowList prj = baseDao.queryForRowSet("select * from Project where prj_id=?", prj_id);
			if (prj.next()) {
				SqlRowList rs = baseDao.queryForRowSet("select * from asskind where ak_code='Otp'");
				if (rs.next()) {
					int akdid = baseDao.getSeqId("ASSKINDDETAIL_SEQ");
					int akid = rs.getGeneralInt("ak_id");
					int count = baseDao.getCount("select count(1) from asskindDetail where AKD_ASSCODE='" + prj.getObject("prj_code")
							+ "' and AKD_ASSNAME='" + prj.getObject("prj_name") + "' and AKD_AKID=" + akid);
					if (count == 0) {
						baseDao.execute(
								"insert into asskinddetail(akd_id,AKD_AKID,AKD_DETNO,AKD_ASSNAME,AKD_ASSCODE,AKD_STATUS) values(?,?,?,?,?,'CANUSE')",
								new Object[] { akdid, akid,
										baseDao.getFieldDataByCondition("asskinddetail", "max(nvl(akd_detno,0))+1", "akd_akid=" + akid),
										prj.getObject("prj_name"), prj.getObject("prj_code") });
					}
				} else {
					int akid = baseDao.getSeqId("ASSKIND_SEQ");
					baseDao.execute(
							"insert into asskind(AK_ID,AK_CODE,AK_NAME,AK_TABLE,AK_DBFIND,AK_ASSCODE,AK_ASSNAME,AK_ADDKIND,AK_STATUS,AK_RECORDDATE,AK_RECORDMAN,AK_EMID) values (?,'Otp','项目','AssKindDetail','AssKindDetail','akd_asscode','akd_assname','项目','VALID',sysdate,?,?)",
							new Object[] { akid, SystemSession.getUser().getEm_name(), SystemSession.getUser().getEm_id() });
					int akdid = baseDao.getSeqId("ASSKINDDETAIL_SEQ");
					baseDao.execute(
							"insert into asskinddetail(akd_id,AKD_AKID,AKD_DETNO,AKD_ASSNAME,AKD_ASSCODE,AKD_STATUS) values(?,?,1,?,?,'CANUSE')",
							new Object[] { akdid, akid, prj.getObject("prj_name"), prj.getObject("prj_code") });
				}
			}
		}
		// 记录操作
		baseDao.logger.audit("Project", "prj_id", prj_id);
		handlerService.handler("Project", "audit", "after", new Object[] { prj_id });
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public String TurnProjectreview(int id) {
		// 转评审
		String result = projectDao.TurnProjectReview(id);
		baseDao.updateByCondition("Project", "prj_statuscode='TURNRE',prj_status='" + BaseUtil.getLocalMessage("TURNRE") + "'", "prj_id="
				+ id);
		// 记录操作
		baseDao.logger.turn("转项目评审", "Project", "prj_id", id);
		return result;
	}

	@Override
	public void resSubmitProject(int id) {
		handlerService.handler("Project", "resCommit", "before", new Object[] { id });
		// 执行反提交操作
		baseDao.resOperate("Project", "prj_id=" + id, "prj_status", "prj_statuscode");
		// 记录操作
		baseDao.logger.resSubmit("Project", "prj_id", id);
		// 执行反提交后的其它逻辑
		handlerService.handler("Project", "resCommit", "after", new Object[] { id });
	}

	@Override
	public void resAuditProject(int id) {
		handlerService.handler("Project", "resAudit", "before", new Object[] { id });
		if (baseDao.isDBSetting("Project", "assProject")) {
			Object prjcode = baseDao.getFieldDataByCondition("Project", "prj_code", "prj_id="+id);
			Object akid = baseDao.getFieldDataByCondition("asskind", "ak_id", "ak_code='Otp'");
			if (prjcode!=null&&akid!=null) {
				baseDao.deleteByCondition("asskinddetail", "akd_akid=? and akd_asscode=?",akid,prjcode);
			}
		}
		// 执行反提交操作
		baseDao.resOperate("Project", "prj_id=" + id, "prj_status", "prj_statuscode");
		// 记录操作
		baseDao.logger.resAudit("Project", "prj_id", id);
		// 执行反提交后的其它逻辑
		handlerService.handler("Project", "resAudit", "after", new Object[] { id });
	}

	@Override
	public void updateProjectjzxh(int prj_id, String prj_sptext70, String caller) {
		baseDao.updateByCondition("Project", "prj_sptext70='" + prj_sptext70 + "'", "prj_id=" + prj_id);
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "更新型号", "更新成功", caller + "|prj_id=" + prj_id));
	}
	@Override
	public Map<Object, List<JSONObject>> getPhases(String condition) {
		// TODO Auto-generated method stub
		/**
		 * condition 为筛选项目condition
		 * */
		Map<Object, List<JSONObject>> phases = new HashMap<Object, List<JSONObject>>();
		SqlRowList sl = baseDao
				.queryForRowSet("SELECT pp_status,pp_phase,pp_prjid,to_char(pp_startdate,'yyyy-mm-dd') pp_startdate,to_char(pp_enddate,'yyyy-mm-dd') pp_enddate,pp_chargeperson,to_char(pp_realenddate,'yyyy-mm-dd') pp_realenddate,to_char(pp_realstartdate,'yyyy-mm-dd') pp_realstartdate from  ProjectPhase pp where Exists (select 1 from project left join Projectphase on prj_id=pp_prjid  and prj_phase=pp_phase where "
						+ condition + " and prj_id=pp.pp_prjid) order by pp_prjid,pp_detno");
		List<JSONObject> lists = new ArrayList<JSONObject>();
		int prjid = 0;
		JSONObject o = null;
		while (sl.next()) {
			o = new JSONObject();
			o.putAll(sl.getCurrentMap());
			if (prjid != sl.getInt("pp_prjid")) {
				lists = new ArrayList<JSONObject>();
				prjid = sl.getInt("pp_prjid");
				phases.put(prjid, lists);
			}
			lists.add(o);
		}
		return phases;
	}
}
