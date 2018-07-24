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
import com.uas.erp.dao.common.ReceivableDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.ReceivableService;

@Service("ReceivableService")
public class ReceivableServiceImpl implements ReceivableService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private ReceivableDao ReceivableDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveReceivable(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// save
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"DebitContractRegister", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// save detail
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"DebitContractRegisterDet");
		baseDao.execute(gridSql);
		// 记录日志
		baseDao.logger.save(caller, "dcr_id", store.get("dcr_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteReceivable(int dcr_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition(
				"DebitContractRegister", "dcr_statuscode", "dcr_id=" + dcr_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { dcr_id });
		// delete
		baseDao.deleteById("DebitContractRegister", "dcr_id", dcr_id);
		// delete detail
		baseDao.deleteById("DebitContractRegisterDet", "dcrd_dcrid", dcr_id);
		// 记录操作
		baseDao.logger.delete(caller, "dcr_id", dcr_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, dcr_id);
	}

	@Override
	public void updateReceivableById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		/*
		 * //只能修改[在录入]的资料! Object status =
		 * baseDao.getFieldDataByCondition("DebitContractRegister",
		 * "dcr_statuscode", "dcr_id=" + store.get("dcr_id"));
		 * if(!status.equals("ENTERING")){
		 * BaseUtil.showError(BaseUtil.getLocalMessage
		 * ("common.update_onlyEntering", language)); }
		 */
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// update
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"DebitContractRegister", "dcr_id");
		baseDao.execute(formSql);
		// update detail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"DebitContractRegisterDet", "dcrd_id");
		for (Map<Object, Object> s : grid) {
			if (s.get("dcrd_id") == null || s.get("dcrd_id").equals("")
					|| s.get("dcrd_id").equals("0")
					|| Integer.parseInt(s.get("dcrd_id").toString()) == 0) {
				int id = baseDao.getSeqId("DebitContractRegisterDet_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s,
						"DebitContractRegisterDet", new String[] { "dcrd_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 更新收款状态
		String sqlstr = "update DebitContractRegisterDet set dcrd_receivestatuscode='COLLECTED',dcrd_receivestatus='"
				+ BaseUtil.getLocalMessage("COLLECTED")
				+ "' where nvl(dcrd_actualsum,0)>0  and nvl(dcrd_actualsum,0) = nvl(dcrd_plansum,0) and dcrd_dcrid="
				+ store.get("dcr_id");
		String sqlstr1 = "update DebitContractRegisterDet set dcrd_receivestatuscode='PARTCOLLECT',dcrd_receivestatus='"
				+ BaseUtil.getLocalMessage("PARTCOLLECT")
				+ "' where nvl(dcrd_actualsum,0)>0  and nvl(dcrd_actualsum,0) < nvl(dcrd_plansum,0) and dcrd_dcrid="
				+ store.get("dcr_id");
		String sqlstr2 = "update DebitContractRegisterDet set dcrd_receivestatuscode='UNCOLLECT',dcrd_receivestatus='"
				+ BaseUtil.getLocalMessage("UNCOLLECT")
				+ "' where nvl(dcrd_actualsum,0)<=0 and dcrd_dcrid="
				+ store.get("dcr_id");
		baseDao.execute(sqlstr);
		baseDao.execute(sqlstr1);
		baseDao.execute(sqlstr2);
		String sqls = "update DebitContractRegisterDet set dcrd_remaindays=to_date(to_char(dcrd_plandate,'yyyy-mm-dd'),'yyyy-mm-dd') -to_date(to_char(sysdate,'yyyy-mm-dd'),'yyyy-mm-dd') where dcrd_dcrid="
				+ store.get("dcr_id");
		baseDao.execute(sqls);

		// 记录操作
		baseDao.logger.update(caller, "dcr_id", store.get("dcr_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void auditReceivable(int dcr_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition(
				"DebitContractRegister", "dcr_statuscode", "dcr_id=" + dcr_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, dcr_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"DebitContractRegister",
				"dcr_statuscode='AUDITED',dcr_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'", "dcr_id="
						+ dcr_id);
		// 记录操作
		baseDao.logger.audit(caller, "dcr_id", dcr_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, dcr_id);
	}

	@Override
	public void resAuditReceivable(int dcr_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition(
				"DebitContractRegister", "dcr_statuscode", "dcr_id=" + dcr_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resAudit_onlyAudit"));
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, dcr_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"DebitContractRegister",
				"dcr_statuscode='ENTERING',dcr_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "dcr_id="
						+ dcr_id);
		// 记录操作
		handlerService.afterResAudit(caller, dcr_id);
	}

	@Override
	public void submitReceivable(int dcr_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition(
				"DebitContractRegister", "dcr_statuscode", "dcr_id=" + dcr_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.submit_onlyEntering"));
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, dcr_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"DebitContractRegister",
				"dcr_statuscode='COMMITED',dcr_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "dcr_id="
						+ dcr_id);
		// 记录操作
		baseDao.logger.submit(caller, "dcr_id", dcr_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, dcr_id);
	}

	@Override
	public void resSubmitReceivable(int dcr_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition(
				"DebitContractRegister", "dcr_statuscode", "dcr_id=" + dcr_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, dcr_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"DebitContractRegister",
				"dcr_statuscode='ENTERING',dcr_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "dcr_id="
						+ dcr_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "dcr_id", dcr_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, dcr_id);
	}

	/**
	 * 转银行登记
	 */
	static final String CHECK_YQTY = "SELECT dcr_contractno,dcrd_receivecode,dcrd_detno,dcrd_thisturnamount,dcrd_actualsum,dcrd_turnedamount FROM DebitContractRegisterDet left join DebitContractRegister on dcrd_dcrid=dcr_id WHERE dcrd_id=? and dcrd_actualsum<?";

	public String turnBankRegister(String caller, String data) {
		Object y = 0;
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Object[] objs = null;
		SqlRowList rs = null;
		StringBuffer sb = new StringBuffer();
		for (Map<Object, Object> map : maps) {
			int id = Integer.parseInt(map.get("dcrd_id").toString());
			double dcrd_thisturnamount = Double.parseDouble(map.get(
					"dcrd_thisturnamount").toString());
			objs = baseDao
					.getFieldsDataByCondition(
							"DebitContractRegisterDet left join DebitContractRegister on dcrd_dcrid=dcr_id",
							new String[] { "dcrd_receivecode", "dcrd_detno",
									"dcrd_thisturnamount", "dcrd_actualsum",
									"dcrd_turnedamount" }, "dcrd_id=" + id);
			// 已转金额
			y = baseDao.getFieldDataByCondition("DebitContractRegisterDet",
					"sum(nvl(dcrd_turnedamount,0))", "dcrd_id=" + id
							+ " and dcrd_detno=" + objs[3]);
			y = y == null ? 0 : y;
			rs = baseDao.queryForRowSet(CHECK_YQTY, id,
					Double.parseDouble(y.toString()) + dcrd_thisturnamount);
			if (rs.next()) {
				StringBuffer sb1 = new StringBuffer("[本次金额填写超出可转金额],还款单号:")
						.append(rs.getString("dcr_contractno")).append(",行号:")
						.append(rs.getInt("dcrd_detno")).append(",实际收款金额:")
						.append(rs.getDouble("dcrd_actualsum")).append(",已转数:")
						.append(y).append(",本次数:").append(dcrd_thisturnamount);
				BaseUtil.showError(sb1.toString());
			}
		}
		if (maps.size() > 0) {
			JSONObject j = null;
			String ar_code = null;
			Employee employee = SystemSession.getUser();
			for (Map<Object, Object> map : maps) {
				int dcrd_id = Integer.parseInt(map.get("dcrd_id").toString());
				double dcrd_thisturnamount = Double.parseDouble(map.get(
						"dcrd_thisturnamount").toString());
				j = ReceivableDao.turnBankRegister1(dcrd_id,
						dcrd_thisturnamount);
				if (j != null) {
					ar_code = j.getString("ar_code");
					sb.append("转入成功,银行登记单号:"
							+ "<a href=\"javascript:openUrl('jsps/fa/gs/accountRegister.jsp?whoami=AccountRegister!Bank&formCondition=ar_idIS"
							+ j.get("ar_id") + "&gridCondition=ard_aridIS"
							+ j.get("ar_id") + "')\">" + ar_code + "</a>&nbsp;");
				}
				// 修改已转数量
				baseDao.updateByCondition("DebitContractRegisterDet",
						"dcrd_turnedamount=nvl(dcrd_turnedamount,0)+"
								+ dcrd_thisturnamount, "dcrd_id=" + dcrd_id);
				// 记录日志
				Object[] cts = baseDao.getFieldsDataByCondition(
						"DebitContractRegisterDet", "dcrd_dcrid,dcrd_detno",
						"dcrd_id=" + dcrd_id);
				baseDao.logMessage(new MessageLog(employee.getEm_name(),
						"转银行登记", BaseUtil.getLocalMessage("msg.turnSuccess")
								+ "," + BaseUtil.getLocalMessage("msg.detail")
								+ cts[1], caller + "|dcrd_id=" + cts[0]));
			}
		}
		return sb.toString();
	}
}