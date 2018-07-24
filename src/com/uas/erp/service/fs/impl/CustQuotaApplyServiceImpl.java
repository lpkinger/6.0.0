package com.uas.erp.service.fs.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.common.SingleFormItemsService;
import com.uas.erp.service.fs.CustQuotaApplyService;

@Service
public class CustQuotaApplyServiceImpl implements CustQuotaApplyService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Autowired
	private SingleFormItemsService singleFormItemsService;

	@Override
	public void saveCustQuotaApply(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[] { store });
		Object value = null;
		List<String> clobFields = new ArrayList<String>();
		List<String> clobStrs = new ArrayList<String>();
		for (Object field : store.keySet()) {
			value = store.get(field);
			if (value != null) {
				String val = value.toString();
				if (val.length() > 2000) {
					clobFields.add(field.toString());
					clobStrs.add(val);
				}
			}
		}
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "CustomerQuotaApply"));
		baseDao.saveClob("CustomerQuotaApply", clobFields, clobStrs, "ca_id=" + store.get("ca_id"));
		baseDao.logger.save(caller, "ca_id", store.get("ca_id"));
		store.put("ca_isvalid", "否");
		Object cucode = store.get("ca_custcode");
		baseDao.execute("insert into hxfinancingsituation (fis_id,fis_caid,fis_detno,fis_condition,FIS_LASTCREDIT,FIS_MAXCREDIT,fis_creditperiod) values (HXFINANCINGSITUATION_SEQ.NEXTVAL,"
				+ store.get("ca_id")
				+ ", 1, '在我司的授信',nvl((select ca_factorquota FROM CustomerQuotaApply WHERE ca_isvalid='是' and ca_custcode='"
				+ cucode
				+ "'),0), nvl((select max(ca_factorquota) FROM CustomerQuotaApply WHERE ca_statuscode='AUDITED' and ca_custcode='"
				+ cucode
				+ "'),0), nvl((select ca_effectdays FROM CustomerQuotaApply WHERE ca_isvalid='是' and ca_custcode='" + cucode + "'),0))");
		baseDao.execute("insert into hxfinancingsituation (fis_id,fis_caid,fis_detno,fis_condition)"
				+ " values (HXFINANCINGSITUATION_SEQ.NEXTVAL," + store.get("ca_id") + ", 2, '在其他机构授信')");
		baseDao.execute("insert into hxfinancingsituation (fis_id,fis_caid,fis_detno,fis_condition)"
				+ " values (HXFINANCINGSITUATION_SEQ.NEXTVAL," + store.get("ca_id") + ", 3, '关联企业在我司的授信')");
		baseDao.execute("insert into HXSURVEYBASE (sb_cuid,sb_caid) values ((select cu_id from CustomerInfor where cu_code='" + cucode
				+ "'), " + store.get("ca_id") + ")");
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
		baseDao.procedure("FS_FSFINANCEITEMS", new Object[] { store.get("ca_id"), "核心企业额度申请" });
	}

	@Override
	public void updateCustQuotaApply(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object value = null;
		List<String> clobFields = new ArrayList<String>();
		List<String> clobStrs = new ArrayList<String>();
		for (Object field : store.keySet()) {
			value = store.get(field);
			if (value != null) {
				String val = value.toString();
				if (val.length() > 2000) {
					clobFields.add(field.toString());
					clobStrs.add(val);
				}
			}
		}
		handlerService.handler(caller, "save", "before", new Object[] { store });
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "CustomerQuotaApply", "ca_id"));
		baseDao.saveClob("CustomerQuotaApply", clobFields, clobStrs, "ca_id=" + store.get("ca_id"));
		// 记录操作
		baseDao.logger.update(caller, "ca_id", store.get("ca_id"));
		baseDao.procedure("FS_FSFINANCEITEMS", new Object[] { store.get("ca_id"), "核心企业额度申请" });
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void deleteCustQuotaApply(int ca_id, String caller) {

		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { ca_id });
		// 删除主表内容
		baseDao.deleteById("CustomerQuotaApply", "ca_id", ca_id);
		baseDao.deleteById("HXSURVEYBASE", "sb_caid", ca_id);
		baseDao.deleteById("hxfinancingsituation", "fis_caid", ca_id);
		baseDao.deleteById("HXBUSINESSCONDITION", "bc_caid", ca_id);
		baseDao.deleteById("HXFINANCCONDITION", "fc_caid", ca_id);
		baseDao.logger.delete(caller, "ca_id", ca_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { ca_id });
	}

	@Override
	public void submitCustQuotaApply(int ca_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		SqlRowList rs = baseDao.queryForRowSet("SELECT ca_statuscode,ca_custname,ca_type,ca_pcucode from CustomerQuotaApply where ca_id=?",
				ca_id);
		if (rs.next()) {
			StateAssert.submitOnlyEntering(rs.getObject("ca_statuscode"));
			// 执行提交前的其它逻辑
			handlerService.handler(caller, "commit", "before", new Object[] { ca_id });
			int count = baseDao.getCount("select count(1) from CustCreditRatingApply where cra_cuvename='" + rs.getObject("ca_custname")
					+ "' and cra_statuscode='AUDITED' and cra_valid = 'VALID'");
			if (count == 0) {
				BaseUtil.showError("该客户没有有效的信用评级报告，请先进行信用评级！");
			} else {
				baseDao.execute("update CustomerQuotaApply set (ca_creditlevel,ca_creditscore)=(select cra_creditrating,cra_score from CUSTOMERINFOR_VIEW where nvl(cra_creditrating,' ')<>' ' and ca_custcode=cu_code) where ca_id="
						+ ca_id);
			}
			if ("二级额度".equals(rs.getGeneralString("ca_type"))) {
				Object pcustcode = rs.getObject("ca_pcucode");
				if (!StringUtil.hasText(pcustcode)) {
					BaseUtil.showError("二级额度客户必须填写父级客户！");
				}
				double amount = baseDao.getSummaryByField("CustomerQuotaApply", "ca_factorquota", "ca_pcucode='" + pcustcode
						+ "' and ca_type='二级额度'");
				double pamount = baseDao.getFieldValue("CustomerQuotaApply", "nvl(ca_factorquota,0)", "ca_custcode='" + pcustcode + "'",
						double.class);
				if (amount > pamount) {
					BaseUtil.showError("一级额度客户[" + pcustcode + "]下所有二级额度客户的合计[" + amount + "]大于一级额度客户[" + pamount + "]");
				}
			}
			// 执行提交操作
			baseDao.submit("CustomerQuotaApply", "ca_id=" + ca_id, "ca_status", "ca_statuscode");
			// 记录操作
			baseDao.logger.submit(caller, "ca_id", ca_id);
			// 执行提交后的其它逻辑
			handlerService.handler(caller, "commit", "after", new Object[] { ca_id });
		}
	}

	@Override
	public void resSubmitCustQuotaApply(int ca_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("CustomerQuotaApply", "ca_statuscode", "ca_id=" + ca_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { ca_id });

		// 执行反提交操作
		baseDao.resOperate("CustomerQuotaApply", "ca_id=" + ca_id, "ca_status", "ca_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ca_id", ca_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { ca_id });
	}

	@Override
	public void auditCustQuotaApply(int ca_id, String caller) {
		// 只能对已提交进行审核操作
		Object[] status = baseDao.getFieldsDataByCondition("CustomerQuotaApply", new String[] { "ca_statuscode", "ca_custcode" }, "ca_id="
				+ ca_id);
		StateAssert.auditOnlyCommited(status[0]);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { ca_id });
		Object[] oth = baseDao.getFieldsDataByCondition("CUSTOMERQUOTAAPPLY", new String[] { "ca_id", "ca_code" },
				"ca_isvalid='是' and ca_id<>" + ca_id + " and ca_custcode='" + status[1] + "'");
		if (oth != null) {
			baseDao.execute("update CUSTOMERQUOTAAPPLY set ca_isvalid='否' where ca_id=" + oth[0]);
			baseDao.execute("update CUSTOMERQUOTAAPPLY set CA_CREDITCOND='续作',CA_OLDCODE='" + oth[1] + "' where ca_id=" + ca_id);
		} else {
			baseDao.execute("update CUSTOMERQUOTAAPPLY set CA_CREDITCOND='新增' where ca_id=" + ca_id);
		}
		baseDao.audit("CustomerQuotaApply", "ca_id=" + ca_id, "ca_status", "ca_statuscode", "ca_auditdate", "ca_auditman");
		baseDao.execute("update CUSTOMERQUOTAAPPLY set ca_isvalid='是' where ca_id=" + ca_id);
		// 记录操作
		baseDao.logger.audit(caller, "ca_id", ca_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { ca_id });
	}

	@Override
	public void resAuditCustQuotaApply(int ca_id, String caller) {
		// 只能对状态为[已审核]的采购单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("CustomerQuotaApply", "ca_statuscode", "ca_id=" + ca_id);
		StateAssert.resAuditOnlyAudit(status);
		baseDao.resAuditCheck("CustomerQuotaApply", ca_id);
		handlerService.beforeResAudit(caller, new Object[] { ca_id });

		// 执行反审核操作
		baseDao.resAudit("CustomerQuotaApply", "ca_id=" + ca_id, "ca_status", "ca_statuscode", "ca_auditman", "ca_auditdate");
		// 记录操作
		baseDao.logger.resAudit(caller, "ca_id", ca_id);
		handlerService.afterResAudit(caller, new Object[] { ca_id });
	}

	@Override
	public void saveHXSurveyBase(String caller, String formStore, String param1, String param2, String param3) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param1);
		List<Map<Object, Object>> grid2 = BaseUtil.parseGridStoreToMaps(param2);
		List<Map<Object, Object>> grid3 = BaseUtil.parseGridStoreToMaps(param3);
		Object value = null;
		Object id = store.get("sb_caid");
		List<String> clobFields = new ArrayList<String>();
		List<String> clobStrs = new ArrayList<String>();
		for (Object field : store.keySet()) {
			value = store.get(field);
			if (value != null) {
				String val = value.toString();
				if (val.length() > 2000) {
					clobFields.add(field.toString());
					clobStrs.add(val);
				}
			}
		}
		List<String> sqls = new ArrayList<String>();
		boolean bool = baseDao.checkByCondition("HXSURVEYBASE", "sb_caid = " + id);
		if (bool) {
			sqls.add(SqlUtil.getInsertSqlByMap(store, "HXSURVEYBASE"));
		} else {
			sqls.add(SqlUtil.getUpdateSqlByFormStore(store, "HXSURVEYBASE", "sb_caid"));
		}
		baseDao.execute(sqls);
		if (clobFields.size() > 0) {
			baseDao.saveClob("HXSURVEYBASE", clobFields, clobStrs, "sb_caid=" + id);
		}
		List<String> gridSql = new ArrayList<String>();
		// 授信详情
		if (param1 != null && !"".equals(param1)) {
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid, "HXFINANCINGSITUATION", "fis_id"));
		}
		// 准入公司详情
		if (param2 != null && !"".equals(param2)) {
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid2, "HXACCESSCOMPANY", "ac_id"));
		}
		// 纳税详情
		if (param3 != null && !"".equals(param3)) {
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid3, "CUSTOMERPAYTAXES", "ct_id"));
		}
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "bs_caid", store.get("bs_caid"));
		baseDao.logger.others("更新基本情况", "更新成功", "Cust!QuotaApply", "ca_id", store.get("bs_caid"));
	}

	@Override
	public void saveHXBusinessCondition(String caller, String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object value = null;
		List<String> clobFields = new ArrayList<String>();
		List<String> clobStrs = new ArrayList<String>();
		for (Object field : store.keySet()) {
			value = store.get(field);
			if (value != null) {
				String val = value.toString();
				if (val.length() > 2000) {
					clobFields.add(field.toString());
					clobStrs.add(val);
				}
			}
		}
		String sql = null;
		boolean bool = baseDao.checkByCondition("HXBUSINESSCONDITION", "bc_caid = " + store.get("bc_caid"));
		if (bool) {
			sql = SqlUtil.getInsertSqlByMap(store, "HXBUSINESSCONDITION");
		} else {
			sql = SqlUtil.getUpdateSqlByFormStore(store, "HXBUSINESSCONDITION", "bc_caid");
		}
		baseDao.execute(sql);
		baseDao.saveClob("HXBUSINESSCONDITION", clobFields, clobStrs, "bc_caid=" + store.get("bc_caid"));
		baseDao.logger.save(caller, "bc_caid", store.get("bc_caid"));
		baseDao.logger.others("更新经营情况", "更新成功", "Cust!QuotaApply", "ca_id", store.get("bs_caid"));
	}

	@Override
	public void saveHXFinancCondition(String caller, String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object value = null;
		List<String> clobFields = new ArrayList<String>();
		List<String> clobStrs = new ArrayList<String>();
		for (Object field : store.keySet()) {
			value = store.get(field);
			if (value != null) {
				String val = value.toString();
				if (val.length() > 2000) {
					clobFields.add(field.toString());
					clobStrs.add(val);
				}
			}
		}
		String sql = null;
		boolean bool = baseDao.checkByCondition("HXFINANCCONDITION", "fc_caid = " + store.get("fc_caid"));
		if (bool) {
			sql = SqlUtil.getInsertSqlByMap(store, "HXFINANCCONDITION");
		} else {
			sql = SqlUtil.getUpdateSqlByFormStore(store, "HXFINANCCONDITION", "fc_caid");
		}
		baseDao.execute(sql);
		baseDao.saveClob("HXFINANCCONDITION", clobFields, clobStrs, "fc_caid=" + store.get("fc_caid"));
		baseDao.logger.save(caller, "fc_caid", store.get("fc_caid"));
		baseDao.logger.others("更新财务情况", "更新成功", "Cust!QuotaApply", "ca_id", store.get("fc_caid"));
	}

}
