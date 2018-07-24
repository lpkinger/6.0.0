package com.uas.erp.service.plm.impl;

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
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.service.plm.ProjectConfirmService;

@Service
public class ProjectConfirmServiceImpl implements ProjectConfirmService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private TransferRepository transferRepository;

	@Override
	public void saveProjectConfirm(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store });
		// 执行保存操作
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "ProjectConfirm"));
		baseDao.execute("update ProjectConfirm set pc_amount=round(nvl(pc_incomeamount,0)*(1+nvl(pc_rate,0)/100),2) where pc_id="
				+ store.get("pc_id") + " and nvl(pc_amount,0)=0");
		// 记录操作
		baseDao.logger.save(caller, "pc_id", store.get("pc_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void updateProjectConfirm(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store });
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "ProjectConfirm", "pc_id"));
		baseDao.execute("update ProjectConfirm set pc_amount=round(nvl(pc_incomeamount,0)*(1+nvl(pc_rate,0)/100),2) where pc_id="
				+ store.get("pc_id") + " and nvl(pc_amount,0)=0");
		// 记录操作
		baseDao.logger.update(caller, "pc_id", store.get("pc_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void deleteProjectConfirm(int id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { id });
		// 删除
		baseDao.deleteById("ProjectConfirm", "pc_id", id);
		// 记录操作
		baseDao.logger.delete(caller, "pc_id", id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { id });
	}

	@Override
	public void auditProjectConfirm(int id, String caller) {
		SqlRowList rs = baseDao.queryForRowSet("SELECT * from ProjectConfirm WHERE pc_id=?", id);
		if (rs.next()) {
			StateAssert.auditOnlyCommited(rs.getObject("pc_statuscode"));
			// 执行审核前的其它逻辑
			handlerService.handler(caller, "audit", "before", new Object[] { id });
			// 生成一张其它应收单
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(ab_code) from arbill where ab_class='其它应收单' and ab_sourceid=" + id + " and ab_sourcetype='项目收入确认'",
					String.class);
			if (dets != null) {
				BaseUtil.showError("当前项目收入确认已产生其它应收单[" + dets + "]！");
			}
			String res = baseDao.callProcedure("SP_PROJECTCONFIRMARBILL", new Object[] { id, SystemSession.getUser().getEm_id() });
			if (res.equals("OK")) {

			} else {
				BaseUtil.showError(res);
			}
			// 执行审核操作
			baseDao.audit("ProjectConfirm", "pc_id=" + id, "pc_status", "pc_statuscode", "pc_auditdate", "pc_auditman");
			// 记录操作
			baseDao.logger.audit(caller, "pc_id", id);
			// 执行审核后的其它逻辑
			handlerService.handler(caller, "audit", "after", new Object[] { id });
		}
	}

	@Override
	public void resAuditProjectConfirm(int id, String caller) {
		SqlRowList rs = baseDao.queryForRowSet("SELECT * from ProjectConfirm WHERE pc_id=?", id);
		if (rs.next()) {
			StateAssert.resAuditOnlyAudit(rs.getObject("pc_statuscode"));
			String prjcode = rs.getString("pc_prjcode");
			double inamount = rs.getGeneralDouble("PC_INCOMEAMOUNT"); // 收入金额
			double amount = rs.getGeneralDouble("PC_AMOUNT"); // 开票金额
			List<String> sqls = new ArrayList<String>();
			SqlRowList rs1 = baseDao
					.queryForRowSet(
							"SELECT ab_id,ab_code,ab_vouchercode,ab_payamount,to_char(ab_date,'yyyymm') ab_yearmonth,ab_statuscode from arbill where ab_class='其它应收单' and ab_sourceid=? and ab_sourcetype='项目收入确认'",
							id);
			if (rs1.next()) {
				int ab_id = rs1.getGeneralInt("ab_id");
				String abcode = rs1.getString("ab_code");
				if (StringUtil.hasText(rs1.getObject("ab_vouchercode"))) {
					BaseUtil.showError("产生的其它应收单[" + abcode + "]已生成凭证[" + rs1.getObject("ab_vouchercode") + "]，不允许反审核！");
				}
				if (Math.abs(rs.getGeneralDouble("ab_payamount")) > 0) {
					BaseUtil.showError("产生的其它应收单[" + abcode + "]已收款[" + rs.getGeneralDouble("ab_payamount") + "]，不允许反审核！");
				}
				boolean bool = baseDao.checkIf("PeriodsDetail",
						"pd_code='MONTH-C' and pd_status=99 and pd_detno=" + rs.getGeneralInt("ab_yearmonth"));
				if (bool) {
					BaseUtil.showError("产生的其它应收单[" + abcode + "]单据日期所属期间已结账，不允许反审核！");
				}
				if ("POSTED".equals(rs.getGeneralString("ab_statuscode"))) {
					// 存储过程
					String res = baseDao.callProcedure("Sp_UnCommiteARBill", new Object[] { abcode, 1 });
					if (res != null && !res.trim().equals("")) {
						BaseUtil.showError(res);
					}
					baseDao.updateByCondition("ARBill",
							"ab_auditstatuscode='ENTERING',ab_statuscode='UNPOST',ab_auditstatus='" + BaseUtil.getLocalMessage("ENTERING")
									+ "',ab_status='" + BaseUtil.getLocalMessage("UNPOST") + "'", "ab_id=" + ab_id);
					baseDao.updateByCondition("ARBillDetail", "abd_status=0,abd_statuscode='ENTERING'", "abd_abid=" + ab_id);
				}
				sqls.add("delete from ARBillDetailASS where DASS_CONDID in (select abd_id from ARBillDetail where abd_abid=" + ab_id + ")");
				sqls.add("delete from ARBillDetail where abd_abid=" + ab_id);
				sqls.add("delete from ARBill where ab_id=" + ab_id);
			}
			// 1.更新当前项目编号立项申请里的【已确认收入】字段=数据库值-当前确认收入界面【收入金额】数据
			sqls.add("update project set PRJ_CONFIRMAMOUNT=nvl(PRJ_CONFIRMAMOUNT,0)-" + inamount + " where PRJ_CODE='" + prjcode + "'");
			// 2.更新当前项目编号立项申请里的【已开票金额】字段=数据库值-当前确认收入界面【开票金额】数据
			sqls.add("update project set PRJ_YAMOUNT=nvl(PRJ_YAMOUNT,0)-" + amount + " where PRJ_CODE='" + prjcode + "'");
			// 3.计算当前项目编号项目成本表里的【本月收入金额】=数据库值-当前确认收入界面【收入金额】数据
			sqls.add("update projectcost set PC_TURNCONFIRMAMOUNT=nvl(PC_TURNCONFIRMAMOUNT,0)-" + inamount + " where PC_PRJCODE='"
					+ prjcode + "' AND PC_YEARMONTH=" + rs.getGeneralInt("PC_YEARMONTH"));
			baseDao.execute(sqls);
			// 执行反审核操作
			baseDao.resAudit("ProjectConfirm", "pc_id=" + id, "pc_status", "pc_statuscode", "pc_auditdate", "pc_auditman");
			// 记录操作
			baseDao.logger.resAudit(caller, "pc_id", id);
		}
	}

	@Override
	public void submitProjectConfirm(int id, String caller) {
		baseDao.execute("update ProjectConfirm set pc_amount=round(nvl(pc_incomeamount,0)*(1+nvl(pc_rate,0)/100),2) where pc_id=" + id
				+ " and nvl(pc_amount,0)=0");
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { id });
		// 执行提交操作
		baseDao.submit("ProjectConfirm", "pc_id=" + id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pc_id", id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { id });
	}

	@Override
	public void resSubmitProjectConfirm(int id, String caller) {
		// 执行反提交操作
		baseDao.resOperate("ProjectConfirm", "pc_id=" + id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pc_id", id);
	}
}
