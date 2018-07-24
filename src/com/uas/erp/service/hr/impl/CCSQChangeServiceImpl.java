package com.uas.erp.service.hr.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.CCSQChangeService;

@Service
public class CCSQChangeServiceImpl implements CCSQChangeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCCSQChange(String formStore, String gridStore,
			String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller,  new Object[] {store,grid });
		// 保存CCSQChange
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "CCSQChange",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存Contact
		for (Map<Object, Object> s : grid) {
			s.put("cd_id", baseDao.getSeqId("CCSQChangedet_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"CCSQChangedet");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "cc_id", store.get("cc_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller,  new Object[] {store,grid });
	}

	@Override
	public void deleteCCSQChange(int cc_id, String  caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("CCSQChange",
				"cc_statuscode", "cc_id=" + cc_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] {cc_id});
		// 删除CCSQChange
		baseDao.deleteById("CCSQChange", "cc_id", cc_id);
		// 删除Contact
		baseDao.deleteById("CCSQChangedet", "cd_ccid", cc_id);
		// 记录操作
		baseDao.logger.delete(caller, "cc_id", cc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] {cc_id});
	}

	@Override
	public void updateCCSQChangeById(String formStore, String gridStore,
			String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("CCSQChange",
				"cc_statuscode", "cc_id=" + store.get("cc_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] {store,gstore });
		// 修改CCSQChange
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "CCSQChange",
				"cc_id");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"CCSQChangedet", "cd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("cd_id") == null || s.get("cd_id").equals("")
					|| s.get("cd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("CCSQChangedet_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "CCSQChangedet",
						new String[] { "cd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.delete(caller, "cc_id", store.get("cc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] {store,gstore });
	}

	@Override
	public void submitCCSQChange(int cc_id, String  caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("CCSQChange",
				"cc_statuscode", "cc_id=" + cc_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] {cc_id});
		// 执行提交操作
		baseDao.submit("CCSQChange", "cc_id=" + cc_id, "cc_status", "cc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "cc_id", cc_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] {cc_id});
	}

	@Override
	public void resSubmitCCSQChange(int cc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("CCSQChange",
				"cc_statuscode", "cc_id=" + cc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller,new Object[] { cc_id});
		// 执行反提交操作
		baseDao.resOperate("CCSQChange", "cc_id=" + cc_id, "cc_status", "cc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "cc_id", cc_id);
		handlerService.afterResSubmit(caller,new Object[] { cc_id});
	}

	@Transactional
	@Override
	public void auditCCSQChange(int cc_id, String  caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("CCSQChange",
				"cc_statuscode", "cc_id=" + cc_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] {cc_id });
		// 反应到原单据中
		Object[] data = baseDao.getFieldsDataByCondition("CCSQChange",
				new String[] { "cc_cqid", "cc_newplace", "cc_newline",
						"cc_newremark" }, "cc_id=" + cc_id);
		baseDao.execute("update FeePlease set fp_v1='" + data[1] + "',fp_v3='"
				+ data[2] + "',fp_v14='" + data[3] + "' where fp_id=" + data[0]);
		List<Object[]> gridData = baseDao.getFieldsDatasByCondition(
				"CCSQChangedet",
				new String[] { "cd_newstartdate", "cd_newenddate", "cd_newn5",
						"cd_newn6", "cd_newvehicle", "cd_newplace", "cd_newd2",
						"cd_cqdid" }, "cd_ccid=" + cc_id);
		List<String> sqls = new ArrayList<String>();
		for (Object[] d : gridData) {
			String sql = "update FeePleaseDetail set fpd_date1="
					+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, d[0] + "")
					+ ",fpd_date2="
					+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, d[1] + "")
					+ ",fpd_n5=" + d[2] + ",fpd_n6=" + d[3] + ",fpd_d3='" + d[4]
					+ "',fpd_d4='" + d[5] + "',fpd_d2='" + d[6]
					+ "' where fpd_id=" + d[7];
			sqls.add(sql);
		}
		baseDao.execute(sqls);
		// 执行审核操作
		baseDao.audit("CCSQChange", "cc_id=" + cc_id, "cc_status", "cc_statuscode", "cc_auditdate", "cc_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "cc_id", cc_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] {cc_id });
	}

	@Transactional
	@Override
	public void resAuditCCSQChange(int cc_id, String  caller) {
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller,new Object[] {cc_id});
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("CCSQChange",
				"cc_statuscode", "cc_id=" + cc_id);
		StateAssert.resAuditOnlyAudit(status);
		// 反应到原单据中
		Object[] data = baseDao.getFieldsDataByCondition("CCSQChange",
				new String[] { "cc_cqid", "cc_oldplace", "cc_oldline",
						"cc_oldremark" }, "cc_id=" + cc_id);
		baseDao.execute("update FeePlease set fp_v1='" + data[1] + "',fp_v3='"
				+ data[2] + "',fp_v14='" + data[3] + "' where fp_id=" + data[0]);
		List<Object[]> gridData = baseDao.getFieldsDatasByCondition(
				"CCSQChangedet",
				new String[] { "cd_oldstartdate", "cd_oldenddate", "cd_oldn5",
						"cd_oldn6", "cd_oldvehicle", "cd_oldplace", "cd_oldd2",
						"cd_cqdid" }, "cd_ccid=" + cc_id);
		List<String> sqls = new ArrayList<String>();
		for (Object[] d : gridData) {
			String sql = "update FeePleaseDetail set fpd_date1="
					+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, d[0] + "")
					+ ",fpd_date2="
					+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, d[1] + "")
					+ ",fpd_n5=" + d[2] + ",fpd_n6=" + d[3] + ",fpd_d3='" + d[4]
					+ "',fpd_d4='" + d[5] + "',fpd_d2='" + d[6]
					+ "' where fpd_id=" + d[7];
			sqls.add(sql);
		}
		baseDao.execute(sqls);
		// 执行反审核操作
		baseDao.resOperate("CCSQChange", "cc_id=" + cc_id, "cc_status", "cc_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "cc_id", cc_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller,new Object[] {cc_id});
	}

}
