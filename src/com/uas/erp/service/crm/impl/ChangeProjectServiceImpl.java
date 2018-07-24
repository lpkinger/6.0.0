package com.uas.erp.service.crm.impl;

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

import com.uas.erp.service.crm.ChangeProjectService;

@Service
public class ChangeProjectServiceImpl implements ChangeProjectService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveChangeProject(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"ChangeProject", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "cp_id", store.get("cp_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteChangeProject(int cp_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, cp_id);
		// 删除purchase
		baseDao.deleteById("ChangeProject", "cp_id", cp_id);
		// 记录操作
		baseDao.logger.delete(caller, "cp_id", cp_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, cp_id);
	}

	@Override
	public void updateChangeProject(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改ChangeProject
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"ChangeProject", "cp_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "cp_id", store.get("cp_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void auditChangeProject(int cp_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ChangeProject",
				"cp_statuscode", "cp_id=" + cp_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, cp_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"ChangeProject",
				"cp_statuscode='AUDITED',cp_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',cp_auditer='"
						+ SystemSession.getUser().getEm_name()
						+ "',cp_auditdate=sysdate", "cp_id=" + cp_id);
		change(cp_id);

		// 记录操作
		baseDao.logger.audit(caller, "cp_id", cp_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, cp_id);
	}

	@Override
	public void resAuditChangeProject(int cp_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("ChangeProject",
				"cp_statuscode", "cp_id=" + cp_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, cp_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"ChangeProject",
				"cp_statuscode='ENTERING',cp_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',cp_auditer='',cp_auditdate=null", "cp_id=" + cp_id);
		resChange(cp_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "cp_id", cp_id);
		handlerService.afterResAudit(caller, cp_id);
	}

	@Override
	public void submitChangeProject(int cp_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ChangeProject",
				"cp_statuscode", "cp_id=" + cp_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeResSubmit(caller, cp_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"ChangeProject",
				"cp_statuscode='COMMITED',cp_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "cp_id="
						+ cp_id);
		// 记录操作
		baseDao.logger.submit(caller, "cp_id", cp_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, cp_id);
	}

	@Override
	public void resSubmitChangeProject(int cp_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ChangeProject",
				"cp_statuscode", "cp_id=" + cp_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, cp_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"ChangeProject",
				"cp_statuscode='ENTERING',cp_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "cp_id="
						+ cp_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "cp_id", cp_id);
		handlerService.afterResSubmit(caller, cp_id);
	}

	public void change(int cp_id) {
		String querysql = "SELECT cp_newppdate,cp_newmpdate,cp_newworksampledate,cp_newremark,cp_newmoldcondition,"
				+ "cp_newproducttype,cp_newmprequires,cp_newpprequires,cp_newworksamplerequires,cp_newspecialrequires,"
				+ "cp_newmainfunction,cp_newdisplaysize,cp_newbusinessmodel,cp_newservicetype,cp_newresolutionradio,cp_prjcode"
				+ " FROM ChangeProject WHERE cp_id=?";
		String updatesql = "UPDATE PROJECT SET prj_ppdate = ? ,prj_mpdate = ? ,prj_worksampledate = ? ,prj_remark = ? ,prj_moldcondition = ? "
				+ ",prj_producttype = ? ,prj_mprequires = ? ,prj_pprequires = ? ,prj_worksamplerequires = ? ,prj_specialrequires = ? "
				+ ",prj_mainfunction = ? ,prj_displaysize = ? ,prj_businessmodel = ? ,prj_servicetype = ? ,prj_resolutionradio = ? WHERE "
				+ "prj_code=?";
		SqlRowList rs = baseDao
				.queryForRowSet(querysql, new Object[] { cp_id });
		while (rs.next()) {
			baseDao.execute(
					updatesql,
					new Object[] { rs.getObject(1), rs.getObject(2),
							rs.getObject(3), rs.getObject(4), rs.getObject(5),
							rs.getObject(6), rs.getObject(7), rs.getObject(8),
							rs.getObject(9), rs.getObject(10),
							rs.getObject(11), rs.getObject(12),
							rs.getObject(13), rs.getObject(14),
							rs.getObject(15), rs.getObject(16) });
		}
	}

	public void resChange(int cp_id) {
		String querysql = "SELECT cp_ppdate,cp_mpdate,cp_worksampledate,cp_remark,cp_moldcondition,"
				+ "cp_producttype,cp_mprequires,cp_pprequires,cp_worksamplerequires,cp_specialrequires,"
				+ "cp_mainfunction,cp_displaysize,cp_businessmodel,cp_servicetype,cp_resolutionradio,cp_prjcode"
				+ " FROM ChangeProject WHERE cp_id=?";
		String updatesql = "UPDATE PROJECT SET prj_ppdate = ? ,prj_mpdate = ? ,prj_worksampledate = ? ,prj_remark = ? ,prj_moldcondition = ? "
				+ ",prj_producttype = ? ,prj_mprequires = ? ,prj_pprequires = ? ,prj_worksamplerequires = ? ,prj_specialrequires = ? "
				+ ",prj_mainfunction = ? ,prj_displaysize = ? ,prj_businessmodel = ? ,prj_servicetype = ? ,prj_resolutionradio = ? WHERE "
				+ "prj_code=?";
		SqlRowList rs = baseDao
				.queryForRowSet(querysql, new Object[] { cp_id });
		while (rs.next()) {
			baseDao.execute(
					updatesql,
					new Object[] { rs.getObject(1), rs.getObject(2),
							rs.getObject(3), rs.getObject(4), rs.getObject(5),
							rs.getObject(6), rs.getObject(7), rs.getObject(8),
							rs.getObject(9), rs.getObject(10),
							rs.getObject(11), rs.getObject(12),
							rs.getObject(13), rs.getObject(14),
							rs.getObject(15), rs.getObject(16) });
		}
	}

}
