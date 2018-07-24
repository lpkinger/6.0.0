package com.uas.erp.service.fa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.AutoDepreciationService;
import com.uas.erp.service.fa.CategoryBaseService;

@Service("categoryBaseService")
public class CategoryBaseServiceImpl implements CategoryBaseService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private VoucherDao voucherDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private AutoDepreciationService autoDepreciationService;

	@Override
	public void saveCategoryBase(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Category", "ca_code='" + store.get("ca_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 科目描述
		Object pcode = store.get("ca_pcode");
		if (pcode != null && pcode.toString().trim().length() > 0) {
			Object pDesc = baseDao.getFieldDataByCondition("Category", "nvl(ca_description, ca_name)", "ca_code='" + pcode + "'");
			if (pDesc != null) {
				store.put("ca_description", pDesc + ":" + store.get("ca_name"));
			} else {
				store.put("ca_description", store.get("ca_name"));
			}
		} else {
			store.put("ca_description", store.get("ca_name"));
		}
		checkVoucher(store.get("ca_pcode"));
		// 保存
		String formSql = SqlUtil.getInsertSqlByMap(store, "Category");
		baseDao.execute(formSql);
		baseDao.updateByCondition("Category A", "ca_subof=nvl((select ca_id from category B where B.ca_code=A.ca_pcode),0)", "ca_id="
				+ store.get("ca_id"));
		baseDao.updateByCondition("Category", "ca_isleaf=0", "ca_id=" + store.get("ca_id")
				+ " and ca_id in (select nvl(ca_subof,0) from category)");
		baseDao.updateByCondition("Category", "ca_isleaf=1", "ca_id=" + store.get("ca_id")
				+ " and ca_id not in (select nvl(ca_subof,0) from category)");
		baseDao.updateByCondition("Category A", "ca_level=nvl((select nvl(ca_level,1)+1 from category B where B.ca_code=A.ca_pcode),1)",
				"ca_id=" + store.get("ca_id"));
		baseDao.execute("update category set ca_typename=case when ca_type=0 then '借' when ca_type=1 then '贷' when ca_type=2 then '借或贷' end where ca_id="
				+ store.get("ca_id"));
		baseDao.logger.save(caller, "ca_id", store.get("ca_id"));
		checkAccount(store.get("ca_id"), store.get("ca_bankaccount"), caller);
		checkBank(store.get("ca_id"), store.get("ca_bank"), caller);
		checkpcode(store.get("ca_code"), store.get("ca_pcode"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	private void checkAccount(Object id, Object bankaccount, String caller) {
		// 更新辅助核算编号
		baseDao.execute("declare v_ass str_table_type; v_ca_asstype category.ca_asstype%type; v_code varchar2(30); v_name varchar2(30); begin for rs in (select parsestring(ca_assname, '#') strs,ca_id from category where instr(ca_assname,'#')>1 and ca_id="
				+ id
				+ ") loop v_ass := rs.strs; v_ca_asstype := null; for i in v_ass.first()..v_ass.last() loop v_name := v_ass(i); begin select ak_code into v_code from asskind where ak_name=v_name; exception when others then v_code := null; end; if nvl(v_code,' ')<>' ' then if v_ca_asstype is null then v_ca_asstype := v_code; else v_ca_asstype := v_ca_asstype || '#' || v_code; end if; end if; end loop; if v_ca_asstype is not null then update category set ca_asstype=v_ca_asstype where ca_id=rs.ca_id; end if; end loop; end;");
		Object code = baseDao
				.getFieldDataByCondition("Category", "ca_code", "ca_id <> " + id + " AND ca_bankaccount='" + bankaccount + "'");
		if (code != null) {
			BaseUtil.appendError("银行帐号在科目 " + code + " 中已存在！");
		}
	}

	private void checkpcode(Object cacode, Object pcode) {
		if (pcode != null && cacode != null) {
			if (cacode.equals(pcode)) {
				BaseUtil.appendError("父级科目有误，不能与当前科目编号一致");
			}
		}
	}

	private void checkBank(Object id, Object bank, String caller) {
		Object code = baseDao.getFieldDataByCondition("Category", "ca_code", "ca_id <> " + id + " AND ca_bank='" + bank + "'");
		if (code != null) {
			BaseUtil.appendError("银行全称在科目 " + code + " 已存在，科目编号！");
		}
	}

	private void checkVoucher(Object ca_pcode) {
		int count = 0;
		if (StringUtil.hasText(ca_pcode)) {
			count = baseDao.getCount("select count(*) from category where ca_code='" + ca_pcode + "' and abs(nvl(ca_isleaf,0))=1");
			if (count > 0) {
				count = baseDao.getCount("select count(*) from voucher left join voucherdetail on vo_id=vd_void where vd_catecode='"
						+ ca_pcode + "'");
				if (count > 0) {
					BaseUtil.showError("做过凭证的科目[" + ca_pcode + "]不允许被作为父级科目！");
				}
				Object yearmonth = baseDao.getFieldDataByCondition("PeriodsDetail", "min(PD_DETNO)", "pd_code='MONTH-A' and pd_status=0");
				count = baseDao.getCount("select count(*) from CATEMONTH where cm_catecode='" + ca_pcode + "' and cm_yearmonth="
						+ yearmonth + " and nvl(CM_ENDDEBIT,0)+nvl(CM_ENDCREDIT,0)<>0");
				if (count > 0) {
					BaseUtil.showError("有余额的科目[" + ca_pcode + "]不允许被作为父级科目！");
				}
			}
		}
	}

	@Override
	public void deleteCategoryBase(int ca_id, String caller) {
		// 已做凭证的科目无法删除
		boolean bool = baseDao.checkByCondition("VoucherDetail", "vd_catecode = (select ca_code from category " + "where ca_id=" + ca_id
				+ ")");
		if (!bool) {
			BaseUtil.showError("该科目已使用，无法删除!");
		}
		int count = baseDao.getCount("select count(*) from Category where ca_subof=" + ca_id);
		if (count > 0) {
			BaseUtil.showError("该科目已有下级科目，不允许删除！");
		}
		// 是否已产生业务数据
		baseDao.delCheck("category", ca_id);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, ca_id);
		// 删除
		baseDao.deleteById("Category", "ca_id", ca_id);
		// 记录操作
		baseDao.logger.delete(caller, "ca_id", ca_id);
		isLeaf();
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ca_id);
	}

	@Override
	public void updateCategoryBaseById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		/*
		 * checkCurrency(store.get("ca_currency"),store.get("ca_currencytype"));
		 * checkCash(store.get("ca_iscashbank"), store.get("ca_cashflow"));
		 */
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 科目描述
		Object pcode = store.get("ca_pcode");
		if (pcode != null && pcode.toString().trim().length() > 0) {
			Object pDesc = baseDao.getFieldDataByCondition("Category", "nvl(ca_description, ca_name)", "ca_code='" + pcode + "'");
			if (pDesc != null) {
				store.put("ca_description", pDesc + ":" + store.get("ca_name"));
			} else {
				store.put("ca_description", store.get("ca_name"));
			}
		} else {
			store.put("ca_description", store.get("ca_name"));
		}
		Object id = store.get("ca_id");
		checkAccount(id, store.get("ca_bankaccount"), caller);
		checkBank(id, store.get("ca_bank"), caller);
		checkpcode(store.get("ca_code"), store.get("ca_pcode"));
		// 修改科目的编号、核算类型时，关联校验
		SqlRowList rs = baseDao.queryForRowSet("select ca_code,abs(ca_currencytype),ca_asstype from category where ca_id=?", id);
		if (rs.next()) {
			String oldCode = rs.getString(1);
			// 已制作凭证
			boolean hasVoucher = baseDao.checkIf("voucherdetail", "vd_catecode='" + oldCode + "'");
			// 总账初始化，科目有余额
			boolean hasInit = baseDao.checkIf("CateMonth", "cm_catecode='" + oldCode
					+ "' and (nvl(cm_enddebit,0)<>0 or nvl(cm_endcredit,0)<>0)");
			if (!oldCode.equals(store.get("ca_code"))) {
				if (hasVoucher) {
					BaseUtil.showError("科目 " + oldCode + " 已经制作凭证，不允许修改科目编号");
				} else if (hasInit) {
					BaseUtil.showError("科目 " + oldCode + " 还有余额，不允许修改科目编号");
				}
			}
			if (store.get("ca_currencytype") != null
					&& rs.getGeneralInt(2) != Math.abs(Integer.parseInt(store.get("ca_currencytype").toString()))) {
				if (hasVoucher) {
					BaseUtil.showError("科目 " + oldCode + " 已经制作凭证，不允许修改科目外币核算");
				} else if (hasInit) {
					BaseUtil.showError("科目 " + oldCode + " 还有余额，不允许修改科目外币核算");
				}
			}
			if (!StringUtil.nvl(store.get("ca_asstype"), "").equals(rs.getGeneralString(3))) {
				if (hasVoucher) {
					BaseUtil.showError("科目 " + oldCode + " 已经制作凭证，不允许修改科目核算类型");
				} else if (hasInit) {
					BaseUtil.showError("科目 " + oldCode + " 还有余额，不允许修改科目核算类型");
				}
			}
		}
		checkVoucher(store.get("ca_pcode"));
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Category", "ca_id");
		baseDao.execute(formSql);
		baseDao.updateByCondition("Category A", "ca_subof=nvl((select ca_id from category B where B.ca_code=A.ca_pcode),0)", "ca_id="
				+ store.get("ca_id"));
		baseDao.updateByCondition("Category", "ca_isleaf=0", "ca_id=" + store.get("ca_id")
				+ " and ca_id in (select nvl(ca_subof,0) from category)");
		baseDao.updateByCondition("Category", "ca_isleaf=1", "ca_id=" + store.get("ca_id")
				+ " and ca_id not in (select nvl(ca_subof,0) from category)");
		baseDao.updateByCondition("Category A", "ca_level=nvl((select nvl(ca_level,1)+1 from category B where B.ca_code=A.ca_pcode),1)",
				"ca_id=" + store.get("ca_id"));
		baseDao.execute("update category set ca_typename=case when ca_type=0 then '借' when ca_type=1 then '贷' when ca_type=2 then '借或贷' end where ca_id="
				+ store.get("ca_id"));
		// 记录操作
		baseDao.logger.update(caller, "ca_id", store.get("ca_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void auditCategory(int ca_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("Category", new String[] { "ca_statuscode", "ca_bankaccount", "ca_bank",
				"nvl(ca_subof,0)", "ca_code", "ca_pcode", "nvl(ca_cashflow,0)" }, "ca_id=" + ca_id);
		StateAssert.auditOnlyCommited(status[0]);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ca_id);
		checkAccount(ca_id, status[1], caller);
		checkBank(ca_id, status[2], caller);
		checkpcode(status[4], status[5]);
		checkVoucher(status[5]);
		// 执行提交操作
		baseDao.updateByCondition("Category", "ca_statuscode='AUDITED',ca_status='" + BaseUtil.getLocalMessage("AUDITED")
				+ "',ca_auditer='" + SystemSession.getUser().getEm_name() + "',ca_auditdate=sysdate", "ca_id=" + ca_id);
		if (!"0".equals(status[6])) {
			int yearmonth = autoDepreciationService.getCurrentYearmonthGL();
			baseDao.execute("update voucher set vo_iscashflow=1 where nvl(vo_iscashflow,0)=0 and to_char(vo_date,'yyyymm')>=" + yearmonth
					+ " and vo_id in (select vd_void from voucherdetail where vd_catecode='" + status[4] + "')");
		}
		// 记录操作
		baseDao.logger.audit(caller, "ca_id", ca_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ca_id);
	}

	@Override
	public void resAuditCategory(int ca_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Category", "ca_statuscode", "ca_id=" + ca_id);
		StateAssert.resAuditOnlyAudit(status);
		isLeaf();
		// 是否已产生业务数据
		baseDao.resAuditCheck("category", ca_id);
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, ca_id);
		// 执行反审核操作
		baseDao.updateByCondition("Category", "ca_statuscode='ENTERING',ca_status='" + BaseUtil.getLocalMessage("ENTERING")
				+ "',ca_auditer='',ca_auditdate=null", "ca_id=" + ca_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ca_id", ca_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, ca_id);
	}

	@Override
	public void submitCategory(int ca_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("Category", new String[] { "ca_statuscode", "ca_bankaccount", "ca_bank",
				"nvl(ca_subof,0)", "ca_code", "ca_pcode" }, "ca_id=" + ca_id);
		StateAssert.submitOnlyEntering(status[0]);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ca_id);
		checkAccount(ca_id, status[1], caller);
		checkBank(ca_id, status[2], caller);
		checkpcode(status[4], status[5]);
		checkVoucher(status[5]);
		// 执行提交操作
		baseDao.updateByCondition("Category", "ca_statuscode='COMMITED',ca_status='" + BaseUtil.getLocalMessage("COMMITED") + "'", "ca_id="
				+ ca_id);
		// 记录操作
		baseDao.logger.submit(caller, "ca_id", ca_id);
		isLeaf();
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ca_id);
	}

	@Override
	public void resSubmitCategory(int ca_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Category", "ca_statuscode", "ca_id=" + ca_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, ca_id);
		// 执行反提交操作
		baseDao.updateByCondition("Category", "ca_statuscode='ENTERING',ca_status='" + BaseUtil.getLocalMessage("ENTERING") + "'", "ca_id="
				+ ca_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ca_id", ca_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, ca_id);
	}

	@Override
	public void bannedCategory(int ca_id, String caller) {
		Map<String, Object> periods = voucherDao.getJustPeriods("MONTH-A");
		int ym = Integer.parseInt(periods.get("PD_DETNO").toString());
		// 科目有余额时，不允许禁用
		boolean hasBalance = baseDao.checkIf("CateMonth", "cm_catecode=(select ca_code from category where ca_id=" + ca_id
				+ ") and (nvl(cm_enddebit,0)<>0 or nvl(cm_endcredit,0)<>0) and cm_yearmonth=" + ym);
		if (hasBalance)
			BaseUtil.showError("该科目还挂有余额，不允许禁用！");
		// 执行禁用前的其它逻辑
		handlerService.handler("Category!Base", "banned", "before", new Object[] { ca_id });
		// 执行禁用操作
		baseDao.updateByCondition("Category", "ca_statuscode='DISABLE',ca_status='" + BaseUtil.getLocalMessage("DISABLE") + "'", "ca_id="
				+ ca_id);
		// 记录操作
		try {
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.banned"), BaseUtil
					.getLocalMessage("msg.bannedSuccess"), "Category!Base|ca_id=" + ca_id));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行禁用后的其它逻辑
		handlerService.handler("Category!Base", "banned", "after", new Object[] { ca_id });
	}

	@Override
	public void resBannedCategory(int ca_id, String caller) {
		// 只能对状态为[已禁用]的单据进行反禁用操作!
		Object status = baseDao.getFieldDataByCondition("Category", "ca_statuscode", "ca_id=" + ca_id);
		if (!"DISABLE".equals(status)) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resBanned_onlyBanned"));
		}
		// 执行反禁用前的其它逻辑
		handlerService.handler("Category!Base", "banned", "before", new Object[] { ca_id });
		// 执行反禁用操作
		baseDao.updateByCondition("Category", "ca_statuscode='AUDITED',ca_status='" + BaseUtil.getLocalMessage("AUDITED") + "'", "ca_id="
				+ ca_id);
		// 记录操作
		try {
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.resBanned"), BaseUtil
					.getLocalMessage("msg.resBannedSuccess"), "Category!Base|ca_id=" + ca_id));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行反禁用后的其它逻辑
		handlerService.handler("Category!Base", "banned", "after", new Object[] { ca_id });
	}

	private void isLeaf() {
		baseDao.updateByCondition("Category", "ca_isleaf=0", "ca_id in(select ca_subof from category)");
		baseDao.updateByCondition("Category", "ca_isleaf=1", "ca_id not in(select ca_subof from category)");
	}

	@Override
	public String getDefaultCurrency() {
		String defaultCurrency = baseDao.getDBSetting("defaultCurrency");
		return defaultCurrency;
	}

}
