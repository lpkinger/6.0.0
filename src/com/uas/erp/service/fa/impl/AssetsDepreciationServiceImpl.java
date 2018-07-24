package com.uas.erp.service.fa.impl;

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
import com.uas.erp.service.fa.AssetsDepreciationService;

@Service("assetsDepreciationService")
public class AssetsDepreciationServiceImpl implements AssetsDepreciationService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveAssetsDepreciation(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		String code = store.get("de_code").toString();
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("AssetsDepreciation", "de_code='" + code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		checkCloseMonth(caller, store.get("de_date"), store.get("de_id"));
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存AssetsDepreciation
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "AssetsDepreciation", new String[] {}, new Object[] {});
		baseDao.execute(formSql);

		for (Map<Object, Object> m : grid) {
			m.put("dd_id", baseDao.getSeqId("ASSETSDEPRECIATIONDETAIL_SEQ"));
			m.put("dd_status", 0);
			m.put("dd_code", code);
			m.put("dd_class", store.get("de_class").toString());
		}
		// 保存AssetsDepreciationDetail
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "AssetsDepreciationDetail");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save(caller, "de_id", store.get("de_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	void checkVoucher(Object id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(de_vouchercode) from AssetsDepreciation where de_id=? and nvl(de_vouchercode,' ') <>' ' and de_vouchercode<>'UNNEED'",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("已有凭证，不允许进行当前操作!凭证编号：" + dets);
		}
	}

	void checkCloseMonth(String caller, Object dedate, Object de_id) {
		baseDao.checkCloseMonth("MONTH-F", dedate);
		if ("AssetsDepreciation".equals(caller)) {
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(de_code) from AssetsDepreciation where de_class='折旧单' and de_id<>" + de_id
							+ " and to_char(de_date,'yyyymm')=to_char(to_date('" + dedate + "','yyyy-mm-dd hh24:mi:ss'), 'yyyymm')",
					String.class);
			if (dets != null) {
				BaseUtil.showError("当前月份已存在折旧单，不能进行当前操作!折旧单号：" + dets);
			}
		}
		if ("AssetsDepreciation!Add".equals(caller) || "AssetsDepreciation!Reduce".equals(caller)) {
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(de_code) from AssetsDepreciation where de_class='折旧单' and de_statuscode='POSTED' and to_char(de_date,'yyyymm')=to_char(to_date('"
									+ dedate + "','yyyy-mm-dd hh24:mi:ss'), 'yyyymm')", String.class);
			if (dets == null) {
				BaseUtil.showError("当前月份不存在已过账的折旧单，不能进行当前操作！");
			}
		}
	}

	@Override
	public void updateAssetsDepreciationById(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("AssetsDepreciation", "de_statuscode", "de_id=" + store.get("de_id"));
		if (!status.equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("fa.fix.AssetsDepreciation.update_onlyPost"));
		}
		checkVoucher(store.get("de_id"));
		checkCloseMonth(caller, store.get("de_date"), store.get("de_id"));
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改AssetsDepreciation
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "AssetsDepreciation", "de_id");
		baseDao.execute(formSql);
		// 修改AssetsDepreciationDetail
		for (Map<Object, Object> m : gstore) {
			m.put("dd_status", 0);
			m.put("dd_code", store.get("de_code").toString());
			m.put("dd_class", store.get("de_class").toString());
		}
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "AssetsDepreciationDetail", "dd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("dd_id") == null || s.get("dd_id").equals("") || s.get("dd_id").equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("AssetsDepreciationDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "AssetsDepreciationDetail", new String[] { "dd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "de_id", store.get("de_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteAssetsDepreciation(String caller, int de_id) {
		// 只能删除在录入的采购单!
		Object[] status = baseDao.getFieldsDataByCondition("AssetsDepreciation", new String[] { "de_statuscode", "de_date" }, "de_id="
				+ de_id);
		if (!status[0].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("fa.fix.AssetsDepreciation.delete_onlyUnpost"));
		}
		baseDao.checkCloseMonth("MONTH-F", status[1]);
		checkVoucher(de_id);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, de_id);
		// 删除AssetsDepreciation
		baseDao.deleteById("AssetsDepreciation", "de_id", de_id);
		// 删除AssetsDepreciationDetail
		baseDao.deleteById("AssetsDepreciationdetail", "dd_deid", de_id);
		// 记录操作
		baseDao.logger.delete(caller, "de_id", de_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, de_id);
	}

	@Override
	public void auditAssetsDepreciation(String caller, int de_id) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("AssetsDepreciation", new String[] { "de_statuscode", "de_date" }, "de_id="
				+ de_id);
		StateAssert.auditOnlyCommited(status[0]);
		checkCloseMonth(caller, status[1], de_id);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(dd_detno) from AssetsDepreciationDetail left join AssetsDepreciation on dd_deid=de_id left join AssetsCard on dd_accode=ac_code where to_char(ac_date, 'yyyymm') > to_char(de_date, 'yyyymm') and dd_deid=? and de_class in ('折旧单')",
						String.class, de_id);
		if (dets != null) {
			BaseUtil.showError("折旧单明细行卡片日期所在期间大于当前单据日期所在期间，不允许进行当前操作!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(dd_detno) from AssetsDepreciationDetail left join AssetsDepreciation on dd_deid=de_id left join AssetsCard on dd_accode=ac_code where to_char(ac_date, 'yyyymm') > to_char(de_date, 'yyyymm') and dd_deid=? and de_class in ('资产增加单','资产减少单')",
						String.class, de_id);
		if (dets != null) {
			BaseUtil.showError("明细行卡片日期所在期间大于当前单据日期所在期间，不允许进行当前操作!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(de_code) from AssetsDepreciation where de_class='折旧单' and de_statuscode<>'POSTED' AND to_char(de_date,'yyyymm')=(select to_char(de_date,'yyyymm') from AssetsDepreciation where de_class in ('资产增加单','资产减少单') and de_id=?)",
						String.class, de_id);
		if (dets != null) {
			BaseUtil.showError("当前单据日期所在期间未计提折旧或者折旧单未过账，不允许进行当前操作!行号：" + dets);
		}
		if ("AssetsDepreciation!Add".equals(caller) || "AssetsDepreciation!Reduce".equals(caller)) {
			baseDao.execute("update AssetsDepreciationDetail set (dd_oldvalue,dd_totaldepreciation)=(select nvl(ac_oldvalue,0),nvl(ac_totaldepreciation,0) from AssetsCard where dd_accode=ac_code) where nvl(dd_accode,' ')<>' ' and dd_deid="
					+ de_id);
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, de_id);
		// 执行审核操作
		baseDao.updateByCondition("AssetsDepreciation", "de_statuscode='AUDITED',de_status='" + BaseUtil.getLocalMessage("AUDITED")
				+ "',de_auditman='" + SystemSession.getUser().getEm_name() + "',de_auditdate=sysdate", "de_id=" + de_id);
		// 记录操作
		baseDao.logger.audit(caller, "de_id", de_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, de_id);
	}

	@Override
	public void resAuditAssetsDepreciation(String caller, int de_id) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("AssetsDepreciation", new String[] { "de_statuscode", "de_date" }, "de_id="
				+ de_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		checkCloseMonth(caller, status[1], de_id);
		handlerService.beforeResAudit(caller, de_id);
		// 执行反审核操作
		baseDao.updateByCondition(caller, "de_statuscode='ENTERING',de_status='" + BaseUtil.getLocalMessage("ENTERING")
				+ "',de_auditman='',de_auditdate=null", "de_id=" + de_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "de_id", de_id);
		handlerService.afterResAudit(caller, de_id);
	}

	@Override
	public void submitAssetsDepreciation(String caller, int de_id) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("AssetsDepreciation", new String[] { "de_statuscode", "de_date" }, "de_id="
				+ de_id);
		if (!status[0].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("fa.fix.AssetsDepreciation.submit_onlyUnpost"));
		}
		checkCloseMonth(caller, status[1], de_id);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(dd_detno) from AssetsDepreciationDetail left join AssetsDepreciation on dd_deid=de_id left join AssetsCard on dd_accode=ac_code where to_char(ac_date, 'yyyymm') > to_char(de_date, 'yyyymm') and dd_deid=? and de_class in ('折旧单')",
						String.class, de_id);
		if (dets != null) {
			BaseUtil.showError("折旧单明细行卡片日期所在期间大于当前单据日期所在期间，不允许进行当前操作!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(dd_detno) from AssetsDepreciationDetail left join AssetsDepreciation on dd_deid=de_id left join AssetsCard on dd_accode=ac_code where to_char(ac_date, 'yyyymm') > to_char(de_date, 'yyyymm') and dd_deid=? and de_class in ('资产增加单','资产减少单')",
						String.class, de_id);
		if (dets != null) {
			BaseUtil.showError("明细行卡片日期所在期间大于当前单据日期所在期间，不允许进行当前操作!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(de_code) from AssetsDepreciation where de_class='折旧单' and de_statuscode<>'POSTED' AND to_char(de_date,'yyyymm')=(select to_char(de_date,'yyyymm') from AssetsDepreciation where de_class in ('资产增加单','资产减少单') and de_id=?)",
						String.class, de_id);
		if (dets != null) {
			BaseUtil.showError("当前单据日期所在期间未计提折旧或者折旧单未过账，不允许进行当前操作!行号：" + dets);
		}
		if ("AssetsDepreciation!Add".equals(caller) || "AssetsDepreciation!Reduce".equals(caller)) {
			baseDao.execute("update AssetsDepreciationDetail set (dd_oldvalue,dd_totaldepreciation)=(select nvl(ac_oldvalue,0),nvl(ac_totaldepreciation,0) from AssetsCard where dd_accode=ac_code) where nvl(dd_accode,' ')<>' ' and dd_deid="
					+ de_id);
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, de_id);
		// 执行提交操作
		baseDao.updateByCondition("AssetsDepreciation",
				"de_statuscode='COMMITED',de_status='" + BaseUtil.getLocalMessage("COMMITED") + "'", "de_id=" + de_id);
		// 记录操作
		baseDao.logger.submit(caller, "de_id", de_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, de_id);
	}

	@Override
	public void resSubmitAssetsDepreciation(String caller, int de_id) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("AssetsDepreciation", new String[] { "de_statuscode", "de_date" }, "de_id="
				+ de_id);
		StateAssert.resSubmitOnlyCommited(status[0]);
		checkCloseMonth(caller, status[1], de_id);
		handlerService.beforeResSubmit(caller, de_id);
		// 执行反提交操作
		baseDao.updateByCondition("AssetsDepreciation", "de_statuscode='UNPOST',de_status='" + BaseUtil.getLocalMessage("UNPOST")
				+ "',DE_POSTDATE=null,DE_POSTMAN=null", "de_id=" + de_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "de_id", de_id);
		handlerService.afterResSubmit(caller, de_id);
	}

	@Override
	public void postAssetsDepreciation(String caller, int de_id) {
		// 只能对状态为[已提交]的单据进行过账操作!
		Object[] status = baseDao.getFieldsDataByCondition("AssetsDepreciation", new String[] { "de_statuscode", "de_date" }, "de_id="
				+ de_id);
		if (!status[0].equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("fa.fix.AssetsDepreciation.post_onlyCommited"));
		}
		checkCloseMonth(caller, status[1], de_id);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(dd_detno) from AssetsDepreciationDetail left join AssetsDepreciation on dd_deid=de_id left join AssetsCard on dd_accode=ac_code where to_char(ac_date, 'yyyymm') > to_char(de_date, 'yyyymm') and dd_deid=?",
						String.class, de_id);
		if (dets != null) {
			BaseUtil.showError("明细行卡片日期所在期间大于当前单据日期所在期间，不允许进行当前操作!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(de_code) from AssetsDepreciation where de_class='折旧单' and de_statuscode<>'POSTED' AND to_char(de_date,'yyyymm')=(select to_char(de_date,'yyyymm') from AssetsDepreciation where de_class in ('资产增加单','资产减少单') and de_id=?)",
						String.class, de_id);
		if (dets != null) {
			BaseUtil.showError("当前单据日期所在期间未计提折旧或者折旧单未过账，不允许进行当前操作!行号：" + dets);
		}
		// 过账前的其它逻辑
		handlerService.beforePost(caller, de_id);
		// 存储过程
		String res = baseDao.callProcedure("Sp_CommitAssetsInOut",
				new Object[] { de_id, String.valueOf(SystemSession.getUser().getEm_id()) });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
		baseDao.updateByCondition("AssetsDepreciation", "de_statuscode='POSTED',de_status='" + BaseUtil.getLocalMessage("POSTED")
				+ "',DE_POSTDATE=sysdate,DE_POSTMAN='" + SystemSession.getUser().getEm_name() + "'", "de_id=" + de_id);
		// 记录操作
		baseDao.logger.post(caller, "de_id", de_id);
		// 执行过账后的其它逻辑
		handlerService.afterPost(caller, de_id);
	}

	@Override
	public void resPostAssetsDepreciation(String caller, int de_id) {
		// 只能对状态为[已过账]的单据进行反过账操作!
		Object[] status = baseDao.getFieldsDataByCondition("AssetsDepreciation", new String[] { "de_statuscode", "de_date", "de_class" },
				"de_id=" + de_id);
		StateAssert.resPostOnlyPosted(status[0]);
		checkVoucher(de_id);
		checkCloseMonth(caller, status[1], de_id);
		if ("资产增加单".equals(status[2]) || "资产减少单".equals(status[2])) {
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(a.de_code) from assetsdepreciation a,assetsdepreciation b where a.de_class='折旧单' and b.de_class in ('资产增加单','资产减少单') and b.de_id=? and to_char(a.de_date,'yyyymm')>to_char(b.de_date,'yyyymm')",
							String.class, de_id);
			if (dets != null) {
				BaseUtil.showError(status[2] + "之后月份存在折旧单，不允许反过账！");
			}
		}
		// 过账前的其它逻辑
		handlerService.beforeResPost(caller, de_id);
		// 存储过程
		String res = baseDao.callProcedure("Sp_UnCommitAssetsInOut",
				new Object[] { de_id, String.valueOf(SystemSession.getUser().getEm_id()) });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
		baseDao.updateByCondition("AssetsDepreciation", "de_statuscode='UNPOST',de_status='" + BaseUtil.getLocalMessage("UNPOST") + "'",
				"de_id=" + de_id);
		// 记录操作
		baseDao.logger.resPost(caller, "de_id", de_id);
		// 执行过账后的其它逻辑
		handlerService.afterResPost(caller, de_id);
	}

	@Override
	public String[] printAssetsDepreciation(String caller, int de_id, String reportName, String condition) {
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, de_id);
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 记录操作
		baseDao.logger.print(caller, "de_id", de_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, de_id);
		return keys;
	}
}
