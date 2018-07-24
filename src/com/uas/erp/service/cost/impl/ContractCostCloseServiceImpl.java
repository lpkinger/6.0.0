package com.uas.erp.service.cost.impl;

import java.util.ArrayList;
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
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.cost.ContractCostCloseService;

@Service
public class ContractCostCloseServiceImpl implements ContractCostCloseService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private VoucherDao voucherDao;

	@Override
	public void saveContractCostClose(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ProjectCostClose", "pcc_code='" + store.get("pcc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存ContractCostClose
		baseDao.execute(SqlUtil.getInsertSqlByFormStore(store, "ProjectCostClose", new String[] {}, new Object[] {}));
		// 保存ContractCostCloseDetail
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid, "ProjectCostCloseDetail", "pcd_id"));
		// 记录操作
		baseDao.logger.save(caller, "pcc_id", store.get("pcc_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void updateContractCostClose(String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("ProjectCostClose", "pcc_statuscode", "pcc_id=" + store.get("pcc_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 修改ContractCostClose
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "ProjectCostClose", "pcc_id"));
		// 修改ContractCostCloseDetail
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "ProjectCostCloseDetail", "pcd_id"));
		// 记录操作
		baseDao.logger.update(caller, "pcc_id", store.get("pcc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteContractCostClose(int id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { id });
		// 删除
		baseDao.deleteById("ProjectCostClose", "pcc_id", id);
		baseDao.deleteById("ProjectCostCloseDetail", "pcd_pccid", id);
		// 记录操作
		baseDao.logger.delete(caller, "pcc_id", id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { id });
	}

	@Override
	public void auditContractCostClose(int id, String caller) {
		Object[] status = baseDao.getFieldsDataByCondition("ProjectCostClose", new String[] { "pcc_statuscode", "pcc_yearmonth" },
				"pcc_id=" + id);
		StateAssert.auditOnlyCommited(status[0]);
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(pcc_code) from ProjectCostClose where pcc_id<>? and pcc_yearmonth=?", String.class, id, status[1]);
		if (dets != null) {
			BaseUtil.showError(status[1] + "月已存在[结转主营业务成本-合同编号]的凭证，不允许进行当前操作！凭证号：" + dets);
		}
		dets = baseDao.getJdbcTemplate()
				.queryForObject("select WM_CONCAT(PCD_DETNO) from ProjectCostCloseDetail where PCD_PCCID=? and nvl(PCD_CATECODE,' ')=' '",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("主营业务成本科目未填写！行号：" + dets);
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, id);
		// 执行审核操作
		baseDao.audit("ProjectCostClose", "pcc_id=" + id, "pcc_status", "pcc_statuscode", "pcc_auditdate", "pcc_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "pcc_id", id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, id);
	}

	@Override
	public void resAuditContractCostClose(int id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("ProjectCostClose", new String[] { "pcc_statuscode", "pcc_yearmonth" },
				"pcc_id=" + id);
		StateAssert.resAuditOnlyAudit(status[0]);
		Object vo = baseDao.getFieldDataByCondition("Voucher", "vo_code", "vo_yearmonth=" + status[1] + " and vo_source='结转主营业务成本-合同编号'");
		if (vo != null) {
			BaseUtil.showError("已制作凭证[" + vo + "]，不允许反审核！");
		}
		// 执行反审核操作
		baseDao.resOperate("ProjectCostClose", "pcc_id=" + id, "pcc_status", "pcc_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "pcc_id", id);
	}

	@Override
	public void submitContractCostClose(int id, String caller) {
		Object[] status = baseDao.getFieldsDataByCondition("ProjectCostClose", new String[] { "pcc_statuscode", "pcc_yearmonth" },
				"pcc_id=" + id);
		StateAssert.submitOnlyEntering(status[0]);
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(pcc_code) from ProjectCostClose where pcc_id<>? and pcc_yearmonth=?", String.class, id, status[1]);
		if (dets != null) {
			BaseUtil.showError(status[1] + "月已存在[结转主营业务成本-合同编号]的凭证，不允许进行当前操作！凭证号：" + dets);
		}
		dets = baseDao.getJdbcTemplate()
				.queryForObject("select WM_CONCAT(PCD_DETNO) from ProjectCostCloseDetail where PCD_PCCID=? and nvl(PCD_CATECODE,' ')=' '",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("主营业务成本科目未填写！行号：" + dets);
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, id);
		// 执行提交操作
		baseDao.submit("ProjectCostClose", "pcc_id=" + id, "pcc_status", "pcc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pcc_id", id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, id);
	}

	@Override
	public void resSubmitContractCostClose(int id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ProjectCostClose", "pcc_statuscode", "pcc_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.resOperate("ProjectCostClose", "pcc_id=" + id, "pcc_status", "pcc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pcc_id", id);
	}

	@Override
	public int createCostVoucher(int pcc_id, String caller) {
		Object yearmonth = baseDao.getFieldDataByCondition("ProjectCostClose", "pcc_yearmonth", "pcc_id=" + pcc_id);
		if (yearmonth != null && !"".equals(yearmonth) && !"0".equals(yearmonth)) {
			Object vo = baseDao.getFieldDataByCondition("Voucher", "vo_code", "vo_yearmonth=" + yearmonth
					+ " and vo_explanation like '%结转主营业务成本-合同编号'");
			if (vo != null) {
				BaseUtil.showError("当月已存在[结转主营业务成本-合同编号]的凭证，凭证编号[" + vo + "]");
			}
		} else {
			BaseUtil.showError("请先选择期间！");
		}
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(PCD_DETNO) from ProjectCostCloseDetail where PCD_PCCID=? and nvl(PCD_CATECODE,' ')=' '", String.class,
				pcc_id);
		if (dets != null) {
			BaseUtil.showError("主营业务成本科目未填写！行号：" + dets);
		}
		int vo_id = baseDao.getSeqId("VOUCHER_SEQ");
		Employee employee = SystemSession.getUser();
		String code = baseDao.sGetMaxNumber("Voucher", 2);
		String vonumber = voucherDao.getVoucherNumber(String.valueOf(yearmonth), null, null);
		String res = baseDao.callProcedure("SP_CREATECONTRACTCOSTVOUCHER",
				new Object[] { vo_id, code, vonumber, pcc_id, employee.getEm_id() });
		if (res.equals("OK")) {
			voucherDao.validVoucher(vo_id);
			String error = baseDao.getJdbcTemplate().queryForObject("select vo_errstring from Voucher Where vo_id=?", String.class, vo_id);
			String codeStr = "<a href=\"javascript:openUrl('jsps/fa/ars/voucher.jsp?formCondition=vo_idIS" + vo_id
					+ "&gridCondition=vd_voidIS" + vo_id + "')\">" + code + "</a><br>";
			if ((error != null && error.trim().length() > 0)) {
				BaseUtil.showError("产生的凭证有问题，请打开凭证查看!<br>" + codeStr);
			} else {
				baseDao.updateByCondition("Voucher", "vo_statuscode='COMMITED',vo_status='" + BaseUtil.getLocalMessage("COMMITED") + "'",
						"vo_id=" + vo_id);
				return vo_id;
			}

		} else {
			BaseUtil.showError(res);
		}
		return 0;
	}

	@Override
	public void cancelCostVoucher(int pcc_id, String caller) {
		Object yearmonth = baseDao.getFieldDataByCondition("ProjectCostClose", "pcc_yearmonth", "pcc_id=" + pcc_id);
		if (yearmonth != null) {
			boolean bool = baseDao.checkIf("PeriodsDetail", "pd_code='MONTH-A' and pd_status=99 and pd_detno=" + yearmonth);
			if (bool) {
				BaseUtil.showError("当月总账期间已结账，不能取消凭证！");
			}
			Object[] vo = baseDao.getFieldsDataByCondition("Voucher", new String[] { "vo_code", "vo_id", "vo_statuscode" }, "vo_yearmonth="
					+ yearmonth + " and vo_source='结转主营业务成本-合同编号'");
			if (vo != null) {
				if ("ACCOUNT".equals(vo[2])) {
					BaseUtil.showError("当月[结转主营业务成本-合同编号]的凭证已记账，不能取消凭证！凭证编号[" + vo[0] + "]");
				} else {
					List<String> sqls = new ArrayList<String>();
					sqls.add("delete from VOUCHERDETAILASS where vds_vdid in (select vd_id from voucherdetail where vd_void=" + vo[1] + ")");
					sqls.add("delete from voucherdetail where vd_void=" + vo[1]);
					sqls.add("delete from voucher where vo_id=" + vo[1]);
					sqls.add("delete from voucherbill where vb_void=" + vo[1]);
					sqls.add("update ProjectCostClose set pcc_vouchercode=null where pcc_id=" + pcc_id);
					baseDao.execute(sqls);
				}
			} else {
				baseDao.execute("update ProjectCostClose set pcc_vouchercode=null where pcc_id=" + pcc_id);
			}
		}
	}

	@Override
	public void catchProjectCost(String caller, String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int pcc_id = Integer.parseInt(store.get("pcc_id").toString());
		Object status = baseDao.getFieldDataByCondition("ProjectCostClose", "pcc_statuscode", "pcc_id=" + pcc_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		String res = baseDao.callProcedure("SP_CATCHCONTRACT", new Object[] { pcc_id });
		if (res.trim().equals("ok")) {
			baseDao.logger.others("抓取合同明细", "抓取成功", caller, "pcc_id", pcc_id);
		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void cleanProjectCost(String caller, String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		int pcc_id = Integer.parseInt(store.get("pcc_id").toString());
		Object status = baseDao.getFieldDataByCondition("ProjectCostClose", "pcc_statuscode", "pcc_id=" + pcc_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		baseDao.deleteByCondition("ProjectCostCloseDetail", "pcd_pccid=" + pcc_id);
		baseDao.logger.others("清除合同明细", "清除成功", caller, "pcc_id", pcc_id);
	}
}
