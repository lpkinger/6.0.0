package com.uas.erp.service.fs.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.fs.AssessSchemeService;

@Service
public class AssessSchemeServiceImpl implements AssessSchemeService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	@Transactional
	public void saveAssessScheme(String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 执行保存操作
		String code = baseDao.sGetMaxNumber("ASSESSSCHEME", 2);
		store.put("as_code", code);
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ASSESSSCHEME", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		for (Map<Object, Object> map : grid) {
			map.put("asd_id", baseDao.getSeqId("ASSESSSCHEMEDETAIL_SEQ"));
			map.put("asd_code", code);
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "ASSESSSCHEMEDETAIL");
		baseDao.execute(gridSql);
		// 合计
		countStandard(store.get("as_id"));
		// 记录操作
		baseDao.logger.save(caller, "as_id", store.get("as_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });

	}

	@Override
	@Transactional
	public void updateAssessScheme(String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ASSESSSCHEME", "as_id");
		baseDao.execute(formSql);
		for (Map<Object, Object> map : gstore) {
			if (map.get("asd_code") == null || "".equals(map.get("asd_code"))) {
				map.put("asd_code", store.get("as_code"));
			}
		}
		List<String> gridSql = SqlUtil.getInsertOrUpdateSqlbyGridStore(gstore, "ASSESSSCHEMEDETAIL", "asd_id");
		baseDao.execute(gridSql);
		// 合计
		countStandard(store.get("as_id"));
		// 记录操作
		baseDao.logger.update(caller, "as_id", store.get("as_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void deleteAssessScheme(int as_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { as_id });

		// 删除ASSESSSCHEME
		baseDao.deleteById("ASSESSSCHEME", "as_id", as_id);
		// 删除ASSESSSCHEMEDETAIL
		baseDao.deleteById("ASSESSSCHEMEDETAIL", "asd_asid", as_id);
		// 记录操作
		baseDao.logger.delete(caller, "as_id", as_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { as_id });
	}

	@Override
	public void submitAssessScheme(int as_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		SqlRowList rs = baseDao.queryForRowSet("select as_statuscode,as_standard,as_finance,as_nofinance from ASSESSSCHEME where as_id=?",
				as_id);
		if (rs.next()) {
			StateAssert.submitOnlyEntering(rs.getObject("as_statuscode"));
			double fastandard = baseDao.getSummaryByField("ASSESSSCHEMEDETAIL left join CreditTargets on asd_ctid=ct_id", "asd_standard",
					"nvl(ct_isleaf,0)<>0 and asd_type='FINANCE' and asd_asid=" + as_id);
			double nofastandard = baseDao.getSummaryByField("ASSESSSCHEMEDETAIL left join CreditTargets on asd_ctid=ct_id", "asd_standard",
					"nvl(ct_isleaf,0)<>0 and asd_type='NOFINANCE' and asd_asid=" + as_id);
			if (rs.getGeneralDouble("as_finance") != 0) {
				fastandard = fastandard * rs.getGeneralDouble("as_finance") / 100;
			}
			if (rs.getGeneralDouble("as_nofinance") != 0) {
				nofastandard = nofastandard * rs.getGeneralDouble("as_nofinance") / 100;
			}
			if (fastandard + nofastandard != rs.getGeneralDouble("as_standard")) {
				BaseUtil.showError("明细财务总标准分[" + fastandard + "]+非财务总标准分[" + nofastandard + "]不等于" + rs.getGeneralDouble("as_standard")
						+ "，不能进行提交操作！");
			}
			// 执行提交前的其它逻辑
			handlerService.beforeSubmit(caller, new Object[] { as_id });
			// 执行提交操作
			baseDao.submit("ASSESSSCHEME", "as_id=" + as_id, "as_status", "as_statuscode");
			// 记录操作
			baseDao.logger.submit(caller, "as_id", as_id);
			// 执行提交后的其它逻辑
			handlerService.afterSubmit(caller, new Object[] { as_id });
		}
	}

	@Override
	public void resSubmitAssessScheme(int as_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ASSESSSCHEME", "as_statuscode", "as_id=" + as_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, as_id);
		// 执行反提交操作
		baseDao.resOperate("ASSESSSCHEME", "as_id=" + as_id, "as_status", "as_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "as_id", as_id);
		// 执行提交后的其它逻辑
		handlerService.afterResSubmit(caller, as_id);
	}

	@Override
	@Transactional
	public void auditAssessScheme(int as_id, String caller) {
		SqlRowList rs = baseDao.queryForRowSet("select as_statuscode,as_standard,as_finance,as_nofinance from ASSESSSCHEME where as_id=?",
				as_id);
		if (rs.next()) {
			StateAssert.auditOnlyCommited(rs.getObject("as_statuscode"));
			double fastandard = baseDao.getSummaryByField("ASSESSSCHEMEDETAIL left join CreditTargets on asd_ctid=ct_id", "asd_standard",
					"nvl(ct_isleaf,0)<>0 and asd_type='FINANCE' and asd_asid=" + as_id);
			double nofastandard = baseDao.getSummaryByField("ASSESSSCHEMEDETAIL left join CreditTargets on asd_ctid=ct_id", "asd_standard",
					"nvl(ct_isleaf,0)<>0 and asd_type='NOFINANCE' and asd_asid=" + as_id);
			if (rs.getGeneralDouble("as_finance") != 0) {
				fastandard = fastandard * rs.getGeneralDouble("as_finance") / 100;
			}
			if (rs.getGeneralDouble("as_nofinance") != 0) {
				nofastandard = nofastandard * rs.getGeneralDouble("as_nofinance") / 100;
			}
			if (fastandard + nofastandard != rs.getGeneralDouble("as_standard")) {
				BaseUtil.showError("明细财务总标准分[" + fastandard + "]+非财务总标准分[" + nofastandard + "]不等于" + rs.getGeneralDouble("as_standard")
						+ "，不能进行审核操作！");
			}
			// 执行审核前的其它逻辑
			handlerService.beforeAudit(caller, new Object[] { as_id });
			// 执行审核操作
			baseDao.audit("ASSESSSCHEME", "as_id=" + as_id, "as_status", "as_statuscode", "as_auditdate", "as_auditman");
			// 记录操作
			baseDao.logger.audit(caller, "as_id", as_id);
			// 执行审核后的其它逻辑
			handlerService.afterAudit(caller, new Object[] { as_id });
		}
	}

	@Override
	public void resAuditAssessScheme(int as_id, String caller) {
		// 只能对状态为[已审核]的采购单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ASSESSSCHEME", "as_statuscode", "as_id=" + as_id);
		StateAssert.resAuditOnlyAudit(status);

		baseDao.resAuditCheck("ASSESSSCHEME", as_id);
		handlerService.beforeResAudit(caller, new Object[] { as_id });

		// 执行反审核操作
		baseDao.resAudit("ASSESSSCHEME", "as_id=" + as_id, "as_status", "as_statuscode", "as_auditman", "as_auditdate");

		// 记录操作
		baseDao.logger.resAudit(caller, "as_id", as_id);
		handlerService.afterResAudit(caller, new Object[] { as_id });
	}

	// 子项标准分合计为父项标准分
	private void countStandard(Object as_id) {
		try {
			// 合计子节点
			SqlRowList rs = baseDao.queryForRowSet("select wmsys.wm_concat(asd_ctid) children,ct_subof from ASSESSSCHEMEDETAIL left join "
					+ "CreditTargets on asd_ctid =ct_id where asd_asid =? group by ct_subof order by ct_subof desc", as_id);
			while (rs.next()) {
				// 合计父节点标准分
				baseDao.execute(
						"update ASSESSSCHEMEDETAIL set asd_standard = (select sum(asd_standard) from ASSESSSCHEMEDETAIL where asd_asid = ? "
								+ "and asd_ctid in (" + rs.getString("children") + ")) where asd_asid = ? and asd_ctid = ?", as_id, as_id,
						rs.getInt("ct_subof"));
			}
		} catch (Exception e) {
			BaseUtil.showError("合计标准分错误：" + e.getMessage());
		}
	}

}
