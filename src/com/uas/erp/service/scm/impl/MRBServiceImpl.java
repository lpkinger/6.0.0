package com.uas.erp.service.scm.impl;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.QUAMRBDao;
import com.uas.erp.service.common.ProcessService;
import com.uas.erp.service.scm.MRBService;

@Service("mRBService")
public class MRBServiceImpl implements MRBService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Autowired
	private QUAMRBDao quaMRBDao;
	@Autowired
	private ProcessService processService;

	@Override
	public void saveMRB(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("QUA_MRB", "mr_code='" + store.get("mr_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存QUA_MRB
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "QUA_MRB", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存QUA_MRBDetail
		for (Map<Object, Object> m : grid) {
			m.put("mrd_id", baseDao.getSeqId("QUA_MRBDETAIL_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "QUA_MRBDetail");
		baseDao.execute(gridSql);
		baseDao.execute("update QUA_MRBDet set md_checkqty=nvl(md_okqty,0) + nvl(md_ngqty,0) where md_mrid=" + store.get("mr_id"));
		baseDao.execute(
				"update QUA_MRB set (mr_okqty,mr_ngqty)=(select sum(nvl(md_okqty,0)), sum(nvl(md_ngqty,0)) from QUA_MRBDet where mr_id=md_mrid) where mr_id=?",
				store.get("mr_id"));
		baseDao.execute("update QUA_MRB set mr_checkqty=nvl(mr_ngqty,0) + nvl(mr_okqty,0) where mr_id=?", store.get("mr_id"));
		// 记录操作
		baseDao.logger.save(caller, "mr_id", store.get("mr_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void updateMRBById(String formStore, String gridStore1, String gridStore2, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore1 = BaseUtil.parseGridStoreToMaps(gridStore1);
		List<Map<Object, Object>> gstore2 = BaseUtil.parseGridStoreToMaps(gridStore2);
		baseDao.execute("update QUA_MRB set mr_statuscode='ENTERING',mr_status='" + BaseUtil.getLocalMessage("ENTERING")
				+ "' WHERE mr_statuscode='UNAUDIT' AND mr_id=" + store.get("mr_id"));
		// 只能修改[未审核]的单据资料!
		Object[] status = baseDao.getFieldsDataByCondition("QUA_MRB", new String[] { "mr_statuscode", "mr_id", "mr_testman" }, "mr_id="
				+ store.get("mr_id"));
		StateAssert.updateOnlyEntering(status[0]);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore1 });
		// 修改QUA_MRB
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "QUA_MRB", "mr_id");
		baseDao.execute(formSql);
		// 修改QUA_MRBDet
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore1, "QUA_MRBDet", "md_id");
		for (Map<Object, Object> s : gstore1) {
			if (s.get("md_id") == null || s.get("md_id").equals("") || s.get("md_id").equals("0")
					|| Integer.parseInt(s.get("md_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("QUAMRBDET_SEQ");
				s.put("md_statuscode", "UNAUDIT");
				s.put("md_status", BaseUtil.getLocalMessage("UNAUDIT"));
				s.put("md_code", store.get("mr_code"));
				s.put("md_date", new Date(new java.util.Date().getTime()));
				String sql = SqlUtil.getInsertSqlByMap(s, "QUA_MRBDet", new String[] { "md_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		Object testman = store.get("mr_testman");
		if (!"".equals(testman) && testman != null) {
			baseDao.updateByCondition("QUA_MRBDet", "md_testman='" + testman + "'", "md_mrid=" + store.get("mr_id")
					+ " and nvl(md_testman,' ')=' '");
		} else {
			baseDao.updateByCondition("QUA_MRBDet", "md_testman='" + SystemSession.getUser().getEm_name() + "'",
					"md_mrid=" + store.get("mr_id") + " and nvl(md_testman,' ')=' '");
		}
		// 修改QUA_MRBDetail
		gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore2, "QUA_MRBDetail", "mrd_id");
		for (Map<Object, Object> s : gstore2) {
			if (s.get("mrd_id") == null || s.get("mrd_id").equals("") || s.get("mrd_id").equals("0")
					|| Integer.parseInt(s.get("mrd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("QUAMRBDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "QUA_MRBDetail", new String[] { "mrd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute("update QUA_MRBDet set md_checkqty=nvl(md_okqty,0) + nvl(md_ngqty,0) where md_mrid=" + store.get("mr_id"));
		baseDao.execute(
				"update QUA_MRB set (mr_okqty,mr_ngqty)=(select sum(nvl(md_okqty,0)), sum(nvl(md_ngqty,0)) from QUA_MRBDet where mr_id=md_mrid) where mr_id=?",
				store.get("mr_id"));
		baseDao.execute("update QUA_MRB set mr_checkqty=nvl(mr_ngqty,0) + nvl(mr_okqty,0) where mr_id=?", store.get("mr_id"));
		// 记录操作
		baseDao.logger.update(caller, "mr_id", store.get("mr_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore1 });
	}

	@Override
	public void deleteMRB(int mr_id, String caller) {
		// 只能删除未审核的单据!
		Object status = baseDao.getFieldDataByCondition("QUA_MRB", "mr_statuscode", "mr_id=" + mr_id);
		if (!status.equals("UNAUDIT") && !status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.qua_verifyapplydetail.delete_onlyEntering"));
		}
		SqlRowList rs = baseDao.queryForRowSet("select md_statuscode,md_isok,md_isng from QUA_MRBDet where md_mrid=?", mr_id);
		while (rs.next()) {
			if (("TURNIN").equals(rs.getString("md_statuscode")) || rs.getInt("md_isok") == 1 || rs.getInt("md_isng") == 1) {
				BaseUtil.showError("明细行已入库，不允许删除!");
			}
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, mr_id);
		// 删除QUA_MRBDet
		baseDao.deleteById("QUA_MRBDet", "md_mrid", mr_id);
		// 删除QUA_MRBDetail
		baseDao.deleteById("QUA_MRBDetail", "mrd_mrid", mr_id);
		// 删除QUA_MRB
		quaMRBDao.deleteMRB(mr_id);
		// 记录操作
		baseDao.logger.delete(caller, "mr_id", mr_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, mr_id);
	}

	@Override
	public void printMRB(int mr_id, String caller) {
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, mr_id);
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "mr_id", mr_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, mr_id);
	}

	@Override
	public void auditMRB(int mr_id, String caller) {
		baseDao.execute("update QUA_MRB set mr_statuscode='ENTERING',mr_status='" + BaseUtil.getLocalMessage("ENTERING")
				+ "' WHERE mr_statuscode='UNAUDIT' AND mr_id=" + mr_id);
		// 只能对状态为[未审核]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("QUA_MRB", new String[] { "mr_statuscode", "mr_code", "mr_testman",
				"mr_checkstatuscode" }, "mr_id=" + mr_id);
		StateAssert.auditOnlyCommited(status[0]);
		/*
		 * String dets = baseDao.getJdbcTemplate().queryForObject(
		 * "select wmsys.wm_concat(pd_inoutno) from prodiodetail left join QUA_MRBDet on pd_mrid=md_id where md_mrid="
		 * + mr_id + " and pd_status<>'99' AND PD_PICLASS='不良品入库单'",
		 * String.class); if (dets != null) {
		 * BaseUtil.showError("存在未过账的不良品入库单，不允许进行当前操作!不良品入库单号：<br>"+ dets); }
		 */
		Object checkqty = baseDao.getFieldDataByCondition("QUA_MRBDet left join QUA_MRB on md_mrid=mr_id","nvl(sum(nvl(md_okqty,0)+nvl(md_ngqty,0)),0)-nvl(max(nvl(mr_inqty,0)),0)", "md_mrid="+mr_id);
		 if(Double.parseDouble(checkqty.toString())!=0){  //2018060403  maz  MRB单审核时限制改为必须数量相等  18-06-19
			 BaseUtil.showError("合格数量+不合格数量的和需等于来料数量");
		 }
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, mr_id);
		int count = baseDao.getCountByCondition("QUA_MRBDET", "md_mrid=" + mr_id);
		if (count == 1) {
			//MRB单审核时明细只有一行按照明细更新主表，如果在审批流中修改了主表，可以勾选processupdate的逻辑配置，按主表信息更新明细
			baseDao.execute("update QUA_MRBDET set md_checkqty=nvl(md_ngqty,0) + nvl(md_okqty,0) where md_mrid=?", mr_id);
			baseDao.execute("update QUA_MRB set (mr_checkqty,mr_okqty,mr_ngqty)=(select md_checkqty,md_okqty,md_ngqty from QUA_MRBDET where md_mrid=mr_id) where mr_id=?", mr_id);
		} else if (count == 0) {
			baseDao.execute("update QUA_MRB set mr_checkqty=nvl(mr_ngqty,0) + nvl(mr_okqty,0) where mr_id=?", mr_id);
			baseDao.execute("insert into QUA_MRBDET(md_id, md_mrid, md_detno, md_okqty, md_ngqty, md_checkqty, MD_DATE, MD_CHECKDATE, MD_STATUS, MD_STATUSCODE, MD_CODE) select QUAMRBDET_SEQ.nextval, mr_id, 1, mr_okqty,mr_ngqty,mr_checkqty, sysdate, mr_date, '已审核', 'AUDITED', mr_code from QUA_MRB where mr_id="
					+ mr_id);
		}
		// 执行审核操作
		baseDao.audit("QUA_MRB", "mr_id=" + mr_id, "mr_status", "mr_statuscode", "mr_auditdate", "mr_auditman");
		baseDao.audit("QUA_MRBDet", "md_mrid=" + mr_id + " AND nvl(md_statuscode,' ')<>'TURNIN'", "md_status", "md_statuscode");
		if (!"".equals(status[2]) && status[2] != null) {
			baseDao.updateByCondition("QUA_MRBDet", "md_testman='" + status[2] + "'", "md_mrid=" + mr_id + " and nvl(md_testman,' ')=' '");
		} else {
			baseDao.updateByCondition("QUA_MRBDet", "md_testman='" + SystemSession.getUser().getEm_name() + "'", "md_mrid=" + mr_id
					+ " and nvl(md_testman,' ')=' '");
		}
		Object[] qty = baseDao.getFieldsDataByCondition("QUA_MRBDet", new String[] { "sum(md_checkqty)", "sum(md_okqty)", "sum(md_ngqty)",
				"sum(md_samplingqty)", "sum(md_samplingokqty)", "sum(md_samplingngqty)" }, "md_mrid=" + mr_id);
		baseDao.updateByCondition("QUA_MRB", "MR_CHECKQTY=" + qty[0] + ",MR_NGQTY=" + qty[2] + ",mr_okqty=" + qty[1], "mr_id=" + mr_id);
		// 记录操作
		baseDao.logger.audit(caller, "mr_id", mr_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, mr_id);
	}

	@Override
	public void resAuditMRB(int mr_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		SqlRowList rs = baseDao.queryForRowSet("select md_statuscode,md_isok,md_isng from QUA_MRBDet where md_mrid=?", mr_id);
		while (rs.next()) {
			if (("TURNIN").equals(rs.getString("md_statuscode")) || rs.getInt("md_isok") == 1 || rs.getInt("md_isng") == 1) {
				BaseUtil.showError("明细行已入库，不允许反审核！");
			}
		}
		Object status = baseDao.getFieldDataByCondition("QUA_MRB", "mr_statuscode", "mr_id=" + mr_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("QUA_MRB", "mr_id=" + mr_id, "mr_status", "mr_statuscode", "mr_auditman", "mr_auditdate");
		baseDao.updateByCondition("QUA_MRBDet", "md_statuscode='UNAUDIT', md_status='" + BaseUtil.getLocalMessage("UNAUDIT") + "'",
				"md_mrid=" + mr_id + " and nvl(md_statuscode,' ')<>'TURNIN'");
		// 记录操作
		baseDao.logger.resAudit(caller, "mr_id", mr_id);
	}

	@Override
	public void approveMRB(int mr_id, String caller) {
		// 只能对状态为[已提交]的单进行批准操作!
		Object status = baseDao.getFieldDataByCondition("QUA_MRB", "mr_checkstatuscode", "mr_id=" + mr_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.qua_mrb.approve_onlycommited"));
		}
		// 执行批准前的其它逻辑
		handlerService.handler(caller, "approve", "before", new Object[] { mr_id });
		// 执行批准操作
		baseDao.updateByCondition("QUA_MRB", "mr_checkstatuscode='APPROVE',mr_checkstatus='" + BaseUtil.getLocalMessage("APPROVE")
				+ "',mr_checkdate=sysdate, mr_checkman='" + SystemSession.getUser().getEm_name() + "'", "mr_id=" + mr_id);
		// 记录操作
		baseDao.logger.approve(caller, "mr_id", mr_id);
		// 清除 批准流程
		String flowcaller = processService.getFlowCaller(caller);
		if (flowcaller != null) {
			processService.deletePInstance(mr_id, caller, "approve");
		}
		// 执行批准后的其它逻辑
		handlerService.handler(caller, "approve", "after", new Object[] { mr_id });
	}

	@Override
	public void resApproveMRB(int mr_id, String caller) {
		// 只能对状态为[已批准]的单进行反批准操作!
		Object status = baseDao.getFieldDataByCondition("QUA_MRB", "mr_checkstatuscode", "mr_id=" + mr_id);
		if (!status.equals("APPROVE")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.statuswrong"));
		}
		// 执行反批准操作
		baseDao.updateByCondition("QUA_MRB", "mr_checkstatuscode='UNAPPROVED',mr_checkstatus='" + BaseUtil.getLocalMessage("UNAPPROVED")
				+ "'", "mr_id=" + mr_id);
		// 记录操作
		baseDao.logger.resApprove(caller, "mr_id", mr_id);
		handlerService.handler(caller, "resApprove", "after", new Object[] { mr_id });
	}

	@Override
	public void submitMRB(int mr_id, String caller) {
		// 只能对在录入的单提交
		Object status = baseDao.getFieldDataByCondition("QUA_MRB", "mr_statuscode", "mr_id=" + mr_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, mr_id);
		// 执行提交操作
		baseDao.submit("QUA_MRB", "mr_id=" + mr_id, "mr_status", "mr_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "mr_id", mr_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, mr_id);
	}

	@Override
	public void resSubmitMRB(int mr_id, String caller) {
		// 只能对状态为[已提交]的单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("QUA_MRB", "mr_statuscode", "mr_id=" + mr_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, mr_id);
		// 执行反提交操作
		baseDao.resOperate("QUA_MRB", "mr_id=" + mr_id, "mr_status", "mr_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "mr_id", mr_id);
		handlerService.afterResSubmit(caller, mr_id);
	}
}
