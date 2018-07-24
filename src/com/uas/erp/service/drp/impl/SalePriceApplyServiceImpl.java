package com.uas.erp.service.drp.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.model.MessageLog;
import com.uas.erp.service.drp.SalePriceApplyService;

@Service("salePriceApplyService")
public class SalePriceApplyServiceImpl implements SalePriceApplyService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveSalePriceApply(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("SalePriceApply", "sp_code='"
				+ store.get("sp_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存SalePriceApply
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"SalePriceApply", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存SalePriceApplyDetail
		for (int i = 0; i < grid.size(); i++) {
			Map<Object, Object> map = grid.get(i);
			map.put("spd_id", baseDao.getSeqId("SALEPRICEAPPLYDETAIL_SEQ"));
			map.put("spd_status", BaseUtil.getLocalMessage("UNVALID"));
			map.put("spd_statuscode", "UNVALID");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"SalePriceApplyDetail");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "sp_id", store.get("sp_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void deleteSalePriceApply(int sp_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("SalePriceApply",
				"sp_statuscode", "sp_id=" + sp_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, sp_id);
		// 删除SalePriceApply
		baseDao.deleteById("SalePriceApply", "sp_id", sp_id);
		// 删除SalePriceApplyDetail
		baseDao.deleteById("SalePriceApplydetail", "apd_spid", sp_id);
		// 记录操作
		baseDao.logger.delete(caller, "sp_id", sp_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, sp_id);
	}

	@Override
	public void updateSalePriceApplyById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("SalePriceApply",
				"sp_statuscode", "sp_id=" + store.get("sp_id"));
		StateAssert.updateOnlyEntering(status);

		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改SalePriceApply
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"SalePriceApply", "sp_id");
		baseDao.execute(formSql);
		// 修改SalePriceApplyDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"SalePriceApplyDetail", "spd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("spd_id") == null || s.get("spd_id").equals("")
					|| s.get("spd_id").equals("0")
					|| Integer.parseInt(s.get("spd_id").toString()) == 0) {// 新添加的数据，id不存在
				s.put("spd_status", BaseUtil.getLocalMessage("UNVALID"));
				s.put("spd_statuscode", "UNVALID");
				int id = baseDao.getSeqId("SalePriceApplyDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s,
						"SalePriceApplyDetail", new String[] { "spd_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "sp_id", store.get("sp_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void printSalePriceApply(int sp_id, String caller) {
		// 只能打印审核后的单据!
		Object status = baseDao.getFieldDataByCondition("SalePriceApply",
				"sp_statuscode", "sp_id=" + sp_id);
		if (!status.equals("AUDITED") && !status.equals("PARTRECEIVED")
				&& !status.equals("RECEIVED") && !status.equals("NULLIFIED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.print_onlyAudit"));
		}
		// 执行打印前的其它逻辑
		handlerService.handler("SalePriceApply", "print", "before",
				new Object[] { sp_id });
		// 执行打印操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),
				BaseUtil.getLocalMessage("msg.print"), BaseUtil
						.getLocalMessage("msg.printSuccess"),
				"SalePriceApply|sp_id=" + sp_id));
		// 执行打印后的其它逻辑
		handlerService.handler("SalePriceApply", "print", "after",
				new Object[] { sp_id });
	}

	@Override
	public void auditSalePriceApply(int sp_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("SalePriceApply",
				"sp_statuscode", "sp_id=" + sp_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, sp_id);
		// 之前根据客户编号、物料号和币别定义的单价需要自动失效
		StringBuffer sb = new StringBuffer();
		List<Object[]> list = baseDao
				.getFieldsDatasByCondition(
						"SalePriceApply left join SalePriceApplyDetail on sp_id=spd_spid",
						new String[] { "spd_arcustcode", "spd_prodcode",
								"spd_currency", "sp_kind" }, "spd_spid="
								+ sp_id + " and spd_statuscode = 'VALID'");
		if (!list.isEmpty()) {
			for (Object[] objs : list) {
				List<Object[]> spds = baseDao
						.getFieldsDatasByCondition(
								"SalePriceApply left join SalePriceApplyDetail on sp_id=spd_spid",
								new String[] { "spd_id", "sp_code", "sp_id",
										"spd_detno" },
								"spd_arcustcode='"
										+ objs[0]
										+ "' AND spd_statuscode='VALID' AND spd_prodcode='"
										+ objs[1] + "' AND spd_currency='"
										+ objs[2] + "'" + "AND sp_kind='"
										+ objs[3] + "' and spd_spid <> "
										+ sp_id);
				for (Object[] spd : spds) {
					baseDao.updateByCondition(
							"SalePriceApplyDetail",
							"spd_statuscode='UNVALID',spd_status='"
									+ BaseUtil.getLocalMessage("UNVALID") + "'",
							"spd_id=" + spd[0]);
					sb.append("价格库原编号为<a href=\"javascript:openUrl('jsps/scm/sale/salePriceApply.jsp?formCondition=sp_idIS"
							+ spd[2]
							+ "&gridCondition=spd_spidIS"
							+ spd[2]
							+ "&whoami=SalePriceApply')\">"
							+ spd[1]
							+ "</a>&nbsp;第" + spd[3] + "行数据已自动失效!<hr>");
				}
			}
		}

		// 执行审核操作
		baseDao.updateByCondition(
				"SalePriceApply",
				"sp_statuscode='AUDITED',sp_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',sp_auditman='"
						+ SystemSession.getUser().getEm_name()
						+ "',sp_auditdate="
						+ DateUtil.parseDateToOracleString(null, new Date()),
				"sp_id=" + sp_id);
		baseDao.updateByCondition(
				"SalePriceApplyDetail",
				"spd_statuscode='VALID',spd_status='"
						+ BaseUtil.getLocalMessage("VALID") + "'", "spd_spid="
						+ sp_id);
		// 记录操作
		baseDao.logger.audit(caller, "sp_id", sp_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, sp_id);
		if (sb.length() > 0) {
			BaseUtil.showError("AFTERSUCCESS" + sb.toString());
		}
	}

	@Override
	public void resAuditSalePriceApply(int sp_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("SalePriceApply",
				"sp_statuscode", "sp_id=" + sp_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, sp_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"SalePriceApply",
				"sp_statuscode='ENTERING',sp_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',sp_auditman='',sp_auditdate=null", "sp_id="
						+ sp_id);
		baseDao.updateByCondition(
				"SalePriceApplyDetail",
				"spd_statuscode='UNVALID',spd_status='"
						+ BaseUtil.getLocalMessage("UNVALID") + "'",
				"spd_spid=" + sp_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "sp_id", sp_id);
		handlerService.afterResAudit(caller, sp_id);
	}

	@Override
	public void submitSalePriceApply(int sp_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("SalePriceApply",
				"sp_statuscode", "sp_id=" + sp_id);
		StateAssert.submitOnlyEntering(status);
		// 只能选择已审核的客户!
		Object code = baseDao.getFieldDataByCondition("SalePriceApplydetail",
				"spd_arcustcode", "spd_spid=" + sp_id);
		status = baseDao.getFieldDataByCondition("Customer",
				"cu_auditstatuscode", "cu_code='" + code + "'");
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("customer_onlyAudited")
					+ "<a href=\"javascript:openUrl('jsps/scm/sale/customer.jsp?formCondition=cu_codeIS"
					+ code + "')\">" + code + "</a>&nbsp;");
		}
		// 只能选择已审核的物料!
		List<Object> codes = baseDao.getFieldDatasByCondition(
				"SalePriceApplyDetail", "spd_prodcode", "spd_spid=" + sp_id);
		for (Object c : codes) {
			status = baseDao.getFieldDataByCondition("Product",
					"pr_statuscode", "pr_code='" + c + "'");
			if (!status.equals("AUDITED")) {
				BaseUtil.showError(BaseUtil
						.getLocalMessage("product_onlyAudited")
						+ "<a href=\"javascript:openUrl('jsps/scm/product/product.jsp?formCondition=pr_codeIS"
						+ c + "')\">" + c + "</a>&nbsp;");
			}
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, sp_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"SalePriceApply",
				"sp_statuscode='COMMITED',sp_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "sp_id="
						+ sp_id);
		baseDao.updateByCondition(
				"SalePriceApplyDetail",
				"spd_statuscode='VALID',spd_status='"
						+ BaseUtil.getLocalMessage("VALID") + "'", "spd_spid="
						+ sp_id);
		// 记录操作
		baseDao.logger.submit(caller, "sp_id", sp_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, sp_id);
	}

	@Override
	public void resSubmitSalePriceApply(int sp_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("SalePriceApply",
				"sp_statuscode", "sp_id=" + sp_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, sp_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"SalePriceApply",
				"sp_statuscode='ENTERING',sp_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "sp_id="
						+ sp_id);
		baseDao.updateByCondition(
				"SalePriceApplyDetail",
				"spd_statuscode='UNVALID',spd_status='"
						+ BaseUtil.getLocalMessage("UNVALID") + "'",
				"spd_spid=" + sp_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "sp_id", sp_id);
		handlerService.afterResSubmit(caller, sp_id);
	}

}
