package com.uas.erp.service.fs.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fs.FsResultsReportService;

@Service("fsResultsReportService")
public class FsResultsReportServiceImpl implements FsResultsReportService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void updateFsResultsReportById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("FS_REPORT", "re_statuscode", "re_id=" + store.get("re_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, store, gstore);
		// 修改FsResultsReportDetail
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "FS_RESULTSREPORT", "rr_id"));
		// 记录操作
		baseDao.logger.update(caller, "re_id", store.get("re_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, store, gstore);
	}

	@Override
	public void auditFsResultsReport(int re_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("FS_REPORT", "re_statuscode", "re_id=" + re_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { re_id });
		// 执行审核操作
		baseDao.audit("FS_REPORT", "re_id=" + re_id, "re_status", "re_statuscode");
		// 记录操作
		baseDao.logger.audit(caller, "re_id", re_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { re_id });
	}

	@Override
	public void resAuditFsResultsReport(int re_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("FS_REPORT", new String[] { "re_statuscode", "re_cqid" }, "re_id=" + re_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(aa_code) from AccountApply where aa_cacode=(select cq_code from customerquota where cq_id=?) and aa_statuscode = 'AUDITED'",
						String.class, status[1]);
		if (dets != null) {
			BaseUtil.showError("已存在已审核的出账申请[" + dets + "]，不允许反审核！");
		}
		// 执行反审核操作
		baseDao.resOperate("FS_REPORT", "re_id=" + re_id, "re_status", "re_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "re_id", re_id);
	}

	@Override
	public void submitFsResultsReport(int re_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("FS_REPORT", "re_statuscode", "re_id=" + re_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { re_id });
		// 执行提交操作
		baseDao.submit("FS_REPORT", "re_id=" + re_id, "re_status", "re_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "re_id", re_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { re_id });
	}

	@Override
	public void resSubmitFsResultsReport(int re_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("FS_REPORT", "re_statuscode", "re_id=" + re_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { re_id });
		// 执行反提交操作
		baseDao.resOperate("FS_REPORT", "re_id=" + re_id, "re_status", "re_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "re_id", re_id);
		handlerService.afterResSubmit(caller, new Object[] { re_id });
	}

}
