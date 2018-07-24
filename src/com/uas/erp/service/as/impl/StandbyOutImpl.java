package com.uas.erp.service.as.impl;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sun.tools.internal.ws.api.TJavaGeneratorExtension;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.service.as.StandbyOutService;

@Service
public class StandbyOutImpl implements StandbyOutService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;	

	public void saveStandbyOut(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		//保存detail
		for (Map<Object, Object> s : grid) {
			s.put("sod_id", baseDao.getSeqId("As_StandbyOutDetail_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"As_StandbyOutDetail");
		baseDao.execute(gridSql);
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "As_StandbyOut",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.execute("update AS_STANDBYOUTDETAIL set sod_out=sod_chuqty,sod_socode='"+store.get("so_code")+"' where sod_soid="+store.get("so_id")+"");
		// 记录操作
		baseDao.logger.save(caller, "sa_id", store.get("sa_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });

	}
	
	public void deleteStandbyOut(int ct_id, String caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("As_StandbyOut",
				"so_statuscode", "so_id=" + ct_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.delete_onlyEntering"));
		}
		String sql="select sod_id,sod_sourceid,sod_code,sod_out from as_standbyoutdetail where sod_soid="+ct_id;
		SqlRowList rs1 = baseDao.queryForRowSet(sql);
		while(rs1.next()){
			Object sa_id = baseDao.getFieldDataByCondition("As_StandbyApplication",
					"sa_id", "sa_code='" + rs1.getString("sod_code")+"'");
			baseDao.execute("update as_standbydetail set sad_out=nvl(sad_out,0)-"+rs1.getInt("sod_out")+" where sad_said="+sa_id+" and sad_id="+rs1.getInt("sod_sourceid"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { ct_id });
		// 删除CustTurn
		baseDao.deleteById("As_StandbyOut", "so_id", ct_id);
		// 删除detail
		baseDao.deleteById("As_StandbyOutdetail", "sod_soid", ct_id);
		// 记录操作
		baseDao.logger.delete(caller, "so_id", ct_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ct_id);
	}
	
	public void updateStandbyOut(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		/*Object status = baseDao.getFieldDataByCondition("As_StandbyOut",
				"so_statuscode", "so_id=" + store.get("so_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.update_onlyEntering"));
		}*/
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		List<String> gridSql = SqlUtil.getInsertOrUpdateSql(gstore, "As_StandbyOutdetail", "sod_id");
		baseDao.execute(gridSql);
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "As_StandbyOut",
				"so_id");
		baseDao.execute(formSql);
		baseDao.execute("update AS_STANDBYOUTDETAIL set sod_out=sod_chuqty where sod_soid="+store.get("so_id")+"");
		baseDao.updateByCondition("As_StandbyOutDetail", "sod_socode='"+store.get("so_code")+"'", "sod_soid="+store.get("so_id"));
		// 记录操作
		baseDao.logger.update(caller, "so_id", store.get("so_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}
	
	public void submitStandbyOut(int ct_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("As_StandbyOut",
				"so_statuscode", "so_id=" + ct_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.submit_onlyEntering"));
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ct_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"As_StandbyOut",
				"so_statuscode='COMMITED',so_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'",
				"so_id=" + ct_id);
		// 记录操作
		baseDao.logger.submit(caller, "so_id", ct_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ct_id);

	}
	
	public void resSubmitStandbyOut(int ct_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("As_StandbyOut",
				"so_statuscode", "so_id=" + ct_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.resSubmit_onlyCommited"));
		}
		handlerService.beforeResSubmit(caller, ct_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"As_StandbyOut",
				"so_statuscode='ENTERING',so_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'",
				"so_id=" + ct_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "so_id", ct_id);
		// 执行提交后的其它逻辑
		handlerService.afterResSubmit(caller, ct_id);

	}
	
	public void auditStandbyOut(int ct_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("As_StandbyOut",
				"so_statuscode", "so_id=" + ct_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ct_id);
		String sql1="select SOD_ID from As_StandbyOutDetail where sod_soid="+ct_id;
		SqlRowList rs1 = baseDao.queryForRowSet(sql1);
		while(rs1.next()){
			baseDao.execute("update As_StandbyOutDetail set SOD_UNBACK=nvl(SOD_OUT,0)-nvl(SOD_BACK,0) where sod_id="+rs1.getInt("SOD_ID"));
		}
		String sql="select SOD_CODE,SOD_OUT,SOD_SOURCEID from As_StandbyOutDetail where sod_soid="+ct_id;
		SqlRowList rs = baseDao.queryForRowSet(sql);
		while(rs.next()){
			Object sa_id = baseDao.getFieldDataByCondition("AS_STANDBYAPPLICATION",
					"sa_id", "sa_code='" + rs.getString("SOD_CODE")+"'");
			baseDao.execute("update AS_STANDBYDETAIL set SAD_DELIVERY=nvl(SAD_DELIVERY,0)+"+rs.getInt("SOD_OUT")+" where SAD_SAID="+sa_id+" and SAD_ID="+rs.getInt("SOD_SOURCEID"));
		}
		// 执行审核操作
		Employee employee = SystemSession.getUser();
		baseDao.updateByCondition(
				"As_StandbyOut",
				"so_statuscode='AUDITED',so_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',SO_AUDITEMAN='"+employee.getEm_name()+"',SO_AUDITEDATE=sysdate", "so_id=" + ct_id);
		// 记录操作
		baseDao.logger.audit(caller, "so_id", ct_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ct_id);

	}
	
	public void resAuditStandbyOut(int ct_id, String caller) {
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, ct_id);
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("As_StandbyOut",
				"so_statuscode", "so_id=" + ct_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.resAudit_onlyAudit"));
		}
		Object so_code = baseDao.getFieldDataByCondition("As_StandbyOut",
				"so_code", "so_id=" + ct_id);
		if(baseDao.checkIf("As_StandbyBackdetail", "SBD_CODE='"+so_code+"'")){
			Object sod_soid = baseDao.getFieldDataByCondition("As_StandbyBackdetail",
					"max(sbd_sbid)", "SBD_CODE='"+so_code+"'");
			Object sb_code = baseDao.getFieldDataByCondition("As_StandbyBack",
					"sb_code", "sb_id="+sod_soid);
			BaseUtil.showError("此单据有关联的《备用机归还单》，单号为:"
					+ "<a href=\"javascript:openUrl('jsps/as/port/StandbyBack.jsp?formCondition=sb_idIS" + sod_soid
					+ "&gridCondition=sbd_sbidIS" + sod_soid + "')\">" + sb_code + "</a>&nbsp;");
		}
		String sql="select SOD_CODE,SOD_OUT,SOD_SOURCEID from As_StandbyOutDetail where sod_soid="+ct_id;
		SqlRowList rs = baseDao.queryForRowSet(sql);
		while(rs.next()){
			Object sa_id = baseDao.getFieldDataByCondition("AS_STANDBYAPPLICATION",
					"sa_id", "sa_code='" + rs.getString("SOD_CODE")+"'");
			baseDao.execute("update AS_STANDBYDETAIL set SAD_DELIVERY=nvl(SAD_DELIVERY,0)-"+rs.getInt("SOD_OUT")+" where SAD_SAID="+sa_id+" and SAD_ID="+rs.getInt("SOD_SOURCEID"));
		}
		
		// 执行反审核操作
		baseDao.updateByCondition(
				"As_StandbyOut",
				"so_statuscode='ENTERING',so_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',So_APPLICATIONMAN=''", "so_id=" + ct_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "so_id", ct_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, ct_id);

	}

}
