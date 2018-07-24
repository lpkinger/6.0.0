package com.uas.erp.service.as.impl;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.as.StandbyApplicationService;

@Service
public class StandbyApplicationImpl implements StandbyApplicationService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;	

	public void saveStandbyApplication(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		//保存detail
		for (Map<Object, Object> s : grid) {
			s.put("sad_id", baseDao.getSeqId("As_StandbyDetail_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"As_StandbyDetail");
		baseDao.execute(gridSql);
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "As_StandbyApplication",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "sa_id", store.get("sa_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });

	}
	
	public void deleteStandbyApplication(int ct_id, String caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("As_StandbyApplication",
				"sa_statuscode", "sa_id=" + ct_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { ct_id });
		// 删除CustTurn
		baseDao.deleteById("As_StandbyApplication", "sa_id", ct_id);
		// 删除detail
		baseDao.deleteById("As_Standbydetail", "sad_said", ct_id);
		// 记录操作
		baseDao.logger.delete(caller, "sa_id", ct_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ct_id);
	}
	
	public void updateStandbyApplication(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("As_StandbyApplication",
				"sa_statuscode", "sa_id=" + store.get("sa_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.update_onlyEntering"));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		//update detail
		List<String> gridSql = SqlUtil.getInsertOrUpdateSql(gstore, "As_Standbydetail", "sad_id");
		baseDao.execute(gridSql);
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "As_StandbyApplication",
				"sa_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "sa_id", store.get("sa_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });

	}
	
	public void submitStandbyApplication(int ct_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("As_StandbyApplication",
				"sa_statuscode", "sa_id=" + ct_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.submit_onlyEntering"));
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ct_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"As_StandbyApplication",
				"sa_statuscode='COMMITED',sa_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'",
				"sa_id=" + ct_id);
		// 记录操作
		baseDao.logger.submit(caller, "sa_id", ct_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ct_id);

	}
	
	public void resSubmitStandbyApplication(int ct_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("As_StandbyApplication",
				"sa_statuscode", "sa_id=" + ct_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.resSubmit_onlyCommited"));
		}
		handlerService.beforeResSubmit(caller, ct_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"As_StandbyApplication",
				"sa_statuscode='ENTERING',sa_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'",
				"sa_id=" + ct_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "sa_id", ct_id);
		// 执行提交后的其它逻辑
		handlerService.afterResSubmit(caller, ct_id);

	}
	
	public void auditStandbyApplication(int ct_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("As_StandbyApplication",
				"sa_statuscode", "sa_id=" + ct_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ct_id);
		// 执行审核操作
		Employee employee = SystemSession.getUser();
		baseDao.updateByCondition(
				"As_StandbyApplication",
				"sa_statuscode='AUDITED',sa_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',SA_AUDITEMAN='"+employee.getEm_name()+"',SA_AUDITEDATE=sysdate", "sa_id=" + ct_id);
		// 记录操作
		baseDao.logger.audit(caller, "sa_id", ct_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ct_id);

	}
	
	public void resAuditStandbyApplication(int ct_id, String caller) {
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, ct_id);
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("As_StandbyApplication",
				"sa_statuscode", "sa_id=" + ct_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.resAudit_onlyAudit"));
		}
		Object sa_code = baseDao.getFieldDataByCondition("As_StandbyApplication",
				"sa_code", "sa_id=" + ct_id);
		if(baseDao.checkIf("As_StandbyOutdetail", "SOD_CODE='"+sa_code+"'")){
			Object sod_soid = baseDao.getFieldDataByCondition("As_StandbyOutdetail",
					"max(sod_soid)", "SOD_CODE='"+sa_code+"'");
			Object so_code = baseDao.getFieldDataByCondition("As_StandbyOut",
					"so_code", "so_id="+sod_soid);
			BaseUtil.showError("此单据有关联的《备用机出库单》，单号为:"
					+ "<a href=\"javascript:openUrl('jsps/as/port/StandbyOut.jsp?formCondition=so_idIS" + sod_soid
					+ "&gridCondition=sod_soidIS" + sod_soid + "')\">" + so_code + "</a>&nbsp;");
		}
		// 执行反审核操作
		baseDao.updateByCondition(
				"As_StandbyApplication",
				"sa_statuscode='ENTERING',sa_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',SA_APPLICATIONMAN='',SA_APPLICATIONDATE=null", "sa_id=" + ct_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "sa_id", ct_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, ct_id);

	}

}
