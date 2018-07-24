package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fa.AmortProgramService;

@Service("amortProgramService")
public class AmortProgramServiceImpl implements AmortProgramService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void deleteAmortProgram(int ap_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("AmortProgram",
				"ap_statuscode", "ap_id=" + ap_id);
		StateAssert.delOnlyEntering(status);
		// 是否已产生业务数据
		baseDao.delCheck("AmortProgram", ap_id);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, ap_id);
		// 删除AmortProgram
		baseDao.deleteById("AmortProgram", "ap_id", ap_id);
		// 删除AmortProgramDetail
		baseDao.deleteById("AmortProgramdetail", "ad_apid", ap_id);
		// 记录操作
		baseDao.logger.delete(caller, "ap_id", ap_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ap_id);
	}

	@Override
	public void updateAmortProgramById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("AmortProgram",
				"ap_statuscode", "ap_id=" + store.get("ap_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改AmortProgram
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "AmortProgram",
				"ap_id");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore,
				"AmortProgramDetail", "ad_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("ad_id") == null || s.get("ad_id").equals("")
					|| s.get("ad_id").equals("0")
					|| Integer.parseInt(s.get("ad_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("AmortProgramDETAIL_SEQ");
				s.put("ad_code", store.get("ap_code"));
				String sql = SqlUtil.getInsertSqlByMap(s, "AmortProgramDetail",
						new String[] { "ad_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "ap_id", store.get("ap_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });

	}

	@Override
	public void auditAmortProgram(int ap_id, String caller) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(ap_code) from AmortProgram where ap_id=? and ap_enddate-ap_startdate<12*nvl(ap_amortmonth,1) and ap_enddate is not null and ap_startdate is not null",
						String.class, ap_id);
		if (dets != null) {
			BaseUtil.showError("摊销开始时间至摊销完成时间中间的时间段小于摊销月份，不允许进行当前操作！");
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ap_id);
		// 执行审核操作
		baseDao.audit("AmortProgram", "ap_id=" + ap_id, "ap_status", "ap_statuscode", "ap_auditdate", "ap_auditer");
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ap_id);
	}

	@Override
	public void resAuditAmortProgram(int ap_id, String caller) {
		// 只能反审核已审核的资料!
		Object status = baseDao.getFieldDataByCondition("AmortProgram",
				"ap_statuscode", "ap_id=" + ap_id);
		StateAssert.resAuditOnlyAudit(status);
		// 是否已产生业务数据
		baseDao.resAuditCheck("AmortProgram", ap_id);
		handlerService.beforeResAudit(caller, ap_id);
		baseDao.resAudit("AmortProgram", "ap_id=" + ap_id, "ap_status", "ap_statuscode", "ap_auditdate", "ap_auditer");
		handlerService.afterResAudit(caller, ap_id);
	}

	@Override
	public void saveAmortProgram(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("AmortProgram", "ap_code='"
				+ store.get("ap_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存AmortProgram
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "AmortProgram",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存AmortProgramDetail
		for (Map<Object, Object> m : grid) {
			m.put("ad_id", baseDao.getSeqId("PrePaidDETAIL_SEQ"));
			m.put("ad_code", store.get("ap_code"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"AmortProgramDetail");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "ap_id", store.get("ap_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}
}
