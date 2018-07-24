package com.uas.erp.service.drp.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.drp.PartCheckService;

@Service
public class PartCheckServiceImpl implements PartCheckService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void savePartCheck(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存PartCheck
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PartCheck",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存Contact
		for (Map<Object, Object> s : grid) {
			s.put("pcd_id", baseDao.getSeqId("PartCheckdet_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"PartCheckdet");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "pc_id", store.get("pc_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void deletePartCheck(int pc_id, String caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("PartCheck",
				"pc_statuscode", "pc_id=" + pc_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, pc_id);
		// 删除PartCheck
		baseDao.deleteById("PartCheck", "pc_id", pc_id);
		// 删除Contact
		baseDao.deleteById("PartCheckdet", "pcd_pcid", pc_id);
		// 记录操作
		baseDao.logger.delete(caller, "pc_id", pc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, pc_id);
	}

	@Override
	public void updatePartCheckById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("PartCheck",
				"pc_statuscode", "pc_id=" + store.get("pc_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改PartCheck
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PartCheck",
				"pc_id");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"PartCheckdet", "pcd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("pcd_id") == null || s.get("pcd_id").equals("")
					|| s.get("pcd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("PartCheckdet_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "PartCheckdet",
						new String[] { "pcd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "pc_id", store.get("pc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void submitPartCheck(int pc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("PartCheck",
				"pc_statuscode", "pc_id=" + pc_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pc_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"PartCheck",
				"pc_statuscode='COMMITED',pc_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "pc_id="
						+ pc_id);
		baseDao.updateByCondition("PartCheckDet", "pcd_isturn='未转其它单据'",
				"pcd_pcid=" + pc_id);
		// 记录操作
		baseDao.logger.submit(caller, "pc_id", pc_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pc_id);
	}

	@Override
	public void resSubmitPartCheck(int pc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("PartCheck",
				"pc_statuscode", "pc_id=" + pc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, pc_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"PartCheck",
				"pc_statuscode='ENTERING',pc_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "pc_id="
						+ pc_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "pc_id", pc_id);
		handlerService.afterResSubmit(caller, pc_id);
	}

	@Override
	public void auditPartCheck(int pc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("PartCheck",
				"pc_statuscode", "pc_id=" + pc_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, pc_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"PartCheck",
				"pc_statuscode='AUDITED',pc_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',pc_auditdate=sysdate,pc_auditer='"
						+ SystemSession.getUser().getEm_name() + "'", "pc_id="
						+ pc_id);
		// 记录操作
		baseDao.logger.audit(caller, "pc_id", pc_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, pc_id);
	}

	@Override
	public void resAuditPartCheck(int pc_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("PartCheck",
				"pc_statuscode", "pc_id=" + pc_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, pc_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"PartCheck",
				"pc_statuscode='ENTERING',pc_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',pc_auditer='',pc_auditdate=null", "pc_id=" + pc_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "pc_id", pc_id);
		handlerService.afterResAudit(caller, pc_id);
	}

	@Override
	@Transactional
	public String batchTurnOtherIn(String data, String caller) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Employee employee = SystemSession.getUser();
		int id = baseDao.getSeqId("ProdInOut_SEQ");
		String code = baseDao.sGetMaxNumber("ProdInOut!SHIn", 2);
		int detno = 1;
		String insertSql = "insert into ProdInOut(pi_inoutno,pi_class,pi_date,pi_status,pi_recorddate,"
				+ "pi_recordman,pi_invostatus,pi_updateman,pi_updatedate,pi_id,pi_invostatuscode,pi_statuscode) values (?,'售后入库单',sysdate,?,sysdate,?,?,?,sysdate,?,?,?)";
		String insertDetSql = "insert into ProdIODetail(pd_pdno,pd_prodcode,pd_inqty,pd_status,pd_piid,pd_id,pd_inoutno,pd_piclass,pd_auditstatus,pd_accountstatuscode,pd_accountstatus,pd_batchcode)"
				+ " values (?,?,1,0,?,PRODIODETAIL_SEQ.nextval,?,?,?,?,?,?)";
		for (Map<Object, Object> map : maps) {
			baseDao.updateByCondition("PartCheckDet", "pcd_isturn='已转售后入库单'",
					"pcd_id=" + map.get("pcd_id"));
			baseDao.execute(
					insertDetSql,
					new Object[] { detno++, map.get("pcd_prodcode"), id, code,
							"售后入库单", "ENTERING", "UNACCOUNT",
							BaseUtil.getLocalMessage("UNACCOUNT"),
							baseDao.getBatchcode("ProdInOut!SHIn") });
		}
		baseDao.execute(
				insertSql,
				new Object[] { code, BaseUtil.getLocalMessage("UNPOST"),
						employee.getEm_name(),
						BaseUtil.getLocalMessage("ENTERING"),
						employee.getEm_name(), id, "ENTERING", "UNPOST" });
		return "转入成功,售后入库单号:<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?whoami=ProdInOut!SHIn&formCondition=pi_idIS"
				+ id
				+ "&gridCondition=pd_piidIS"
				+ id
				+ "')\">"
				+ code
				+ "</a>&nbsp;";
	}

	@Override
	@Transactional
	public String bathcTurnSaleReturn(String data, String caller) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Employee employee = SystemSession.getUser();
		int id = baseDao.getSeqId("ProdInOut_SEQ");
		String code = baseDao.sGetMaxNumber("ProdInOut!SaleReturn", 2);
		int detno = 1;
		String insertSql = "insert into ProdInOut(pi_inoutno,pi_class,pi_date,pi_status,pi_recorddate,"
				+ "pi_recordman,pi_invostatus,pi_updateman,pi_updatedate,pi_id,pi_invostatuscode,pi_statuscode) values (?,'销售退货单',sysdate,?,sysdate,?,?,?,sysdate,?,?,?)";
		String insertDetSql = "insert into ProdIODetail(pd_pdno,pd_prodcode,pd_inqty,pd_status,pd_piid,pd_id,pd_inoutno,pd_piclass,pd_auditstatus,pd_accountstatuscode,pd_accountstatus)"
				+ " values (?,?,1,0,?,PRODIODETAIL_SEQ.nextval,?,?,?,?,?)";
		for (Map<Object, Object> map : maps) {
			baseDao.updateByCondition("PartCheckDet", "pcd_isturn='已转销售退货单'",
					"pcd_id=" + map.get("pcd_id"));
			baseDao.execute(
					insertDetSql,
					new Object[] { detno++, map.get("pcd_prodcode"), id, code,
							"销售退货单", "ENTERING", "UNACCOUNT",
							BaseUtil.getLocalMessage("UNACCOUNT") });
		}
		baseDao.execute(
				insertSql,
				new Object[] { code, BaseUtil.getLocalMessage("UNPOST"),
						employee.getEm_name(),
						BaseUtil.getLocalMessage("ENTERING"),
						employee.getEm_name(), id, "ENTERING", "UNPOST" });
		return "转入成功,退货单号:<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?whoami=ProdInOut!SaleReturn&formCondition=pi_idIS"
				+ id
				+ "&gridCondition=pd_piidIS"
				+ id
				+ "')\">"
				+ code
				+ "</a>&nbsp;";
	}

	@Override
	public void confirmPartCheck(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("PartCheck",
				"pc_statuscode", "pc_id=" + id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.confirm_onlyAudit"));
		}
		Employee employee = SystemSession.getUser();
		// 执行反审核操作
		baseDao.updateByCondition(
				"PartCheck",
				"pc_confirmtime=sysdate,pc_confirmman='"
						+ employee.getEm_name() + "',pc_confirmstatus='已处理'",
				"pc_id=" + id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
				.getLocalMessage("msg.confirm"), BaseUtil
				.getLocalMessage("msg.confirmSuccess"), "PartCheck|pc_id=" + id));
	}

}
