package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ReturnDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.ReturnService;

@Service("ReturnService")
public class ReturnServiceImpl implements ReturnService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private ReturnDao ReturnDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveReturn(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存主表
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"CreditContractRegister", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存明细表
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"CreditContractRegisterDet");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save(caller, "ccr_id", store.get("ccr_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteReturn(int ccr_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition(
				"CreditContractRegister", "ccr_statuscode", "ccr_id=" + ccr_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { ccr_id });
		// 删除主表
		baseDao.deleteById("CreditContractRegister", "ccr_id", ccr_id);
		// 删除明细表
		baseDao.deleteById("CreditContractRegisterDet", "ccrd_ccrid", ccr_id);
		// 记录操作
		baseDao.logger.delete(caller, "ccr_id", ccr_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ccr_id);
	}

	@Override
	public void updateReturnById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 更新主表
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"CreditContractRegister", "ccr_id");
		baseDao.execute(formSql);
		// 更新明细表
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"CreditContractRegisterDet", "ccrd_id");
		for (Map<Object, Object> s : grid) {
			if (s.get("ccrd_id") == null || s.get("ccrd_id").equals("")
					|| s.get("ccrd_id").equals("0")
					|| Integer.parseInt(s.get("ccrd_id").toString()) == 0) {
				int id = baseDao.getSeqId("CreditContractRegisterDet_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s,
						"CreditContractRegisterDet",
						new String[] { "ccrd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 更新付款状态
		String sqlstr = "update CreditContractRegisterDet set ccrd_repaymentstatuscode='REPAYMENTED',ccrd_repaymentstatus='"
				+ BaseUtil.getLocalMessage("REPAYMENTED")
				+ "' where nvl(ccrd_actualsum,0)>0 and nvl(ccrd_actualsum,0) = nvl(ccrd_plansum,0) and ccrd_ccrid="
				+ store.get("ccr_id");
		String sqlstr1 = "update CreditContractRegisterDet set ccrd_repaymentstatuscode='PARTREPAYMENT',ccrd_repaymentstatus='"
				+ BaseUtil.getLocalMessage("PARTREPAYMENT")
				+ "' where nvl(ccrd_actualsum,0)>0 and nvl(ccrd_actualsum,0) < nvl(ccrd_plansum,0) and ccrd_ccrid="
				+ store.get("ccr_id");
		String sqlstr2 = "update CreditContractRegisterDet set ccrd_repaymentstatuscode='UNREPAYMENT',ccrd_repaymentstatus='"
				+ BaseUtil.getLocalMessage("UNREPAYMENT")
				+ "' where nvl(ccrd_actualsum,0)<=0 and ccrd_ccrid="
				+ store.get("ccr_id");
		baseDao.execute(sqlstr);
		baseDao.execute(sqlstr1);
		baseDao.execute(sqlstr2);
		// 更新剩余天数
		String sqls = "update CreditContractRegisterDet set ccrd_remaindays=to_date(to_char(ccrd_plandate,'yyyy-mm-dd'),'yyyy-mm-dd') -to_date(to_char(sysdate,'yyyy-mm-dd'),'yyyy-mm-dd') where ccrd_ccrid="
				+ store.get("ccr_id");
		baseDao.execute(sqls);
		// 记录操作
		baseDao.logger.update(caller, "ccr_id", store.get("ccr_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void auditReturn(int ccr_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition(
				"CreditContractRegister", "ccr_statuscode", "ccr_id=" + ccr_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ccr_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"CreditContractRegister",
				"ccr_statuscode='AUDITED',ccr_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'", "ccr_id="
						+ ccr_id);
		// 记录操作
		baseDao.logger.audit(caller, "ccr_id", ccr_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ccr_id);
	}

	@Override
	public void resAuditReturn(int ccr_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition(
				"CreditContractRegister", "ccr_statuscode", "ccr_id=" + ccr_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resAudit_onlyAudit"));
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, ccr_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"CreditContractRegister",
				"ccr_statuscode='ENTERING',ccr_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "ccr_id="
						+ ccr_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ccr_id", ccr_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, ccr_id);
	}

	@Override
	public void submitReturn(int ccr_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition(
				"CreditContractRegister", "ccr_statuscode", "ccr_id=" + ccr_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.submit_onlyEntering"));
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ccr_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"CreditContractRegister",
				"ccr_statuscode='COMMITED',ccr_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "ccr_id="
						+ ccr_id);
		// 记录操作
		baseDao.logger.submit(caller, "ccr_id", ccr_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ccr_id);
	}

	@Override
	public void resSubmitReturn(int ccr_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition(
				"CreditContractRegister", "ccr_statuscode", "ccr_id=" + ccr_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, ccr_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"CreditContractRegister",
				"ccr_statuscode='ENTERING',ccr_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "ccr_id="
						+ ccr_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ccr_id", ccr_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, ccr_id);
	}

	/**
	 * 转银行登记
	 */
	static final String CHECK_YQTY = "SELECT ccr_contractno,ccrd_repaymentcode,ccrd_detno,ccrd_thisturnamount,ccrd_actualsum,ccrd_turnedamount FROM CreditContractRegisterDet left join CreditContractRegister on ccrd_ccrid=ccr_id WHERE ccrd_id=? and ccrd_actualsum<?";

	@Override
	public String turnBankRegister(String caller, String data) {
		Object y = 0;
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Object[] objs = null;
		SqlRowList rs = null;
		StringBuffer sb = new StringBuffer();
		for (Map<Object, Object> map : maps) {
			int id = Integer.parseInt(map.get("ccrd_id").toString());
			double ccrd_thisturnamount = Double.parseDouble(map.get(
					"ccrd_thisturnamount").toString());
			objs = baseDao
					.getFieldsDataByCondition(
							"CreditContractRegisterDet left join CreditContractRegister on ccrd_ccrid=ccr_id",
							new String[] { "ccrd_repaymentcode", "ccrd_detno",
									"ccrd_thisturnamount", "ccrd_actualsum",
									"ccrd_turnedamount" }, "ccrd_id=" + id);
			// 已转金额
			y = baseDao.getFieldDataByCondition("CreditContractRegisterDet",
					"sum(nvl(ccrd_turnedamount,0))", "ccrd_id=" + id
							+ " and ccrd_detno=" + objs[3]);
			y = y == null ? 0 : y;
			rs = baseDao.queryForRowSet(CHECK_YQTY, id,
					Double.parseDouble(y.toString()) + ccrd_thisturnamount);
			if (rs.next()) {
				StringBuffer sb1 = new StringBuffer("[本次金额填写超出可转金额],还款单号:")
						.append(rs.getString("ccr_contractno")).append(",行号:")
						.append(rs.getInt("ccrd_detno")).append(",实际还款金额:")
						.append(rs.getDouble("ccrd_actualsum")).append(",已转数:")
						.append(y).append(",本次数:").append(ccrd_thisturnamount);
				BaseUtil.showError(sb1.toString());
			}
		}
		if (maps.size() > 0) {
			JSONObject j = null;
			String ar_code = null;
			Employee employee = SystemSession.getUser();
			for (Map<Object, Object> map : maps) {
				int ccrd_id = Integer.parseInt(map.get("ccrd_id").toString());
				double ccrd_thisturnamount = Double.parseDouble(map.get(
						"ccrd_thisturnamount").toString());
				j = ReturnDao.turnBankRegister1(ccrd_id, ccrd_thisturnamount);
				if (j != null) {
					ar_code = j.getString("ar_code");
					sb.append("转入成功,银行登记单号:"
							+ "<a href=\"javascript:openUrl('jsps/fa/gs/accountRegister.jsp?whoami=AccountRegister!Bank&formCondition=ar_idIS"
							+ j.get("ar_id") + "&gridCondition=ard_aridIS"
							+ j.get("ar_id") + "')\">" + ar_code + "</a>&nbsp;");
				}
				// 修改已转数量
				baseDao.updateByCondition("CreditContractRegisterDet",
						"ccrd_turnedamount=nvl(ccrd_turnedamount,0)+"
								+ ccrd_thisturnamount, "ccrd_id=" + ccrd_id);
				// 记录日志
				Object[] cts = baseDao.getFieldsDataByCondition(
						"CreditContractRegisterDet", "ccrd_ccrid,ccrd_detno",
						"ccrd_id=" + ccrd_id);
				baseDao.logMessage(new MessageLog(employee.getEm_name(),
						"转银行登记", BaseUtil.getLocalMessage("msg.turnSuccess")
								+ "," + BaseUtil.getLocalMessage("msg.detail")
								+ cts[1], caller + "|ccrd_id=" + cts[0]));
			}
		}
		return sb.toString();
	}
}