package com.uas.erp.service.drp.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.model.MessageLog;
import com.uas.erp.service.drp.CargoApplicationService;

@Service
public class CargoApplicationServiceImpl implements CargoApplicationService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCargoApplication(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[] { store, grid });
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"CargoApplication", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存Detail
		Object[] id = new Object[grid.size()];
		for (int i = 0; i < grid.size(); i++) {
			Map<Object, Object> map = grid.get(i);
			id[i] = baseDao.getSeqId("CARGOCUDETAIL_SEQ");
			map.put("cd_id", id[i]);
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"cargoDetail");
		baseDao.execute(gridSql);

		try {
			// 记录操作
			baseDao.logger.save(caller, "ca_id", store.get("ca_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void updateCargoApplicationById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"CargoApplication", "ca_id");
		baseDao.execute(formSql);
		// 修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"CargoDetail", "cd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("cd_id") == null || s.get("cd_id").equals("")
					|| s.get("cd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("CARGODETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "CargoDetail",
						new String[] { "cd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "ca_id", store.get("ca_id"));
		// 执行修改后的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteCargoApplication(int ca_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, ca_id);
		// 删除purchase
		baseDao.deleteById("CargoApplication", "ca_id", ca_id);
		// 删除purchaseDetail
		baseDao.deleteById("cargoCuDetail", "cd_caid", ca_id);
		// 记录操作
		baseDao.logger.delete(caller, "ca_id", ca_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ca_id);
	}

	@Override
	public void submitCargoApplication(int ca_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("CargoApplication",
				"ca_statuscode", "ca_id=" + ca_id);
		StateAssert.submitOnlyEntering(status);
		// 执行审核前的其它逻辑
		handlerService.beforeSubmit(caller, ca_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"CargoApplication",
				"ca_statuscode='COMMITED',ca_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "ca_id="
						+ ca_id);
		// 记录操作
		baseDao.logger.submit(caller, "ca_id", ca_id);
		// 执行审核后的其它逻辑
		handlerService.afterSubmit(caller, ca_id);
	}

	@Override
	public void resSubmitCargoApplication(int ca_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("CargoApplication",
				"ca_statuscode", "ca_id=" + ca_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, ca_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"CargoApplication",
				"ca_statuscode='ENTERING',ca_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "ca_id="
						+ ca_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ca_id", ca_id);
		handlerService.afterResSubmit(caller, ca_id);

	}

	@Override
	public void auditCargoApplication(int ca_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("CargoApplication",
				"ca_statuscode", "ca_id=" + ca_id);
		StateAssert.auditOnlyCommited(status);
		// 执行提交前的其它逻辑
		handlerService.beforeAudit(caller, ca_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"CargoApplication",
				"ca_statuscode='AUDITED',ca_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',ca_auditer='"
						+ SystemSession.getUser().getEm_name()
						+ "',ca_auditdate=sysdate", "ca_id=" + ca_id);
		// 记录操作
		baseDao.logger.audit(caller, "ca_id", ca_id);
		// 执行提交后的其它逻辑
		handlerService.afterAudit(caller, ca_id);
	}

	@Override
	public void resAuditCargoApplication(int ca_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("CargoApplication",
				"ca_statuscode", "ca_id=" + ca_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, ca_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"CargoApplication",
				"ca_statuscode='ENTERING',ca_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',ca_auditer='',ca_auditer=null", "ca_id=" + ca_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ca_id", ca_id);
		handlerService.afterResAudit(caller, ca_id);
	}

	@Override
	public int turnFXSale(int id, String caller) {
		int said = 0;
		// 判断该请购单是否已经转入过采购单
		Object code = baseDao.getFieldDataByCondition("CargoApplication",
				"ca_code", "ca_id=" + id);
		// 根据UU号更新客户编号和名称
		String sqlstr = "update CargoApplication set (ca_custcode,ca_custname)=(select cu_code,cu_name from customer where ca_custuu=cu_uu) where ca_id='"
				+ id + "'";
		baseDao.execute(sqlstr);
		code = baseDao.getFieldDataByCondition("Sale", "sa_id",
				"sa_sourcecode='" + code + "'");
		if (code != null && !code.equals("")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("scm.sale.Sale.haveturn"));
		} else {
			final Object[] formdata = baseDao.getFieldsDataByCondition(
					"CargoApplication", new String[] { "ca_code",
							"ca_custcode", "ca_custname", "ca_custuu",
							"ca_date", "ca_recordername", "ca_currency",
							"ca_rate", "ca_toplace", "ca_payments" }, "ca_id="
							+ id);
			String formSql = "INSERT INTO sale (sa_id,sa_code,sa_custcode,sa_custname,sa_payments,sa_recorder,sa_recorddate,"
					+ "sa_currency,sa_rate,sa_sourcecode,sa_source,sa_statuscode,sa_status) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
			said = baseDao.getSeqId("Sale_SEQ");
			final String sacode = baseDao.sGetMaxNumber("Sale", 2);
			final int sa_id = said;
			baseDao.getJdbcTemplate().update(formSql,
					new PreparedStatementSetter() {
						@Override
						public void setValues(PreparedStatement ps)
								throws SQLException {
							ps.setInt(1, sa_id);
							ps.setString(2, sacode);
							ps.setString(3, formdata[1] + "");
							ps.setString(4, formdata[2] + "");
							ps.setString(5, formdata[3] + "");
							ps.setString(6, formdata[4] + "");// pi_departmentname
							ps.setDate(7, new java.sql.Date(
									new java.util.Date().getTime()));
							ps.setString(8, formdata[6] + "");
							ps.setDouble(9,
									Double.parseDouble(formdata[7] + ""));
							ps.setString(10, formdata[0] + "");
							ps.setString(11, "配货");
							ps.setString(12, "ENTERING");
							ps.setString(13, "在录入");
						}
					});
			List<Object[]> gridData = baseDao.getFieldsDatasByCondition(
					"CargoDetail", new String[] { "cd_detno", "cd_prcode",
							"cd_qty", "cd_price", "cd_remark", "cd_rate",
							"cd_original", "cd_clientmodel", "cd_detail" },
					"cd_caid=" + id);
			String gridSql = "INSERT INTO SaleDetail (sd_detno,sd_prodcode,sd_qty,sd_price,sd_remark,sd_taxrate,sd_said,sd_id,sd_code,sd_original,sd_custprodcode,sd_detail"
					+ ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
			for (final Object[] o : gridData) {
				baseDao.getJdbcTemplate().update(gridSql,
						new PreparedStatementSetter() {
							@Override
							public void setValues(PreparedStatement ps)
									throws SQLException {
								ps.setInt(1, Integer.parseInt(o[0] + ""));
								ps.setString(2, o[1] + "");
								ps.setDouble(3, Double.parseDouble(o[2] + ""));
								ps.setDouble(4, Double.parseDouble(o[3] + ""));
								ps.setString(5, o[4] + "");
								ps.setDouble(6, Double.parseDouble(o[5] + ""));
								ps.setInt(7, sa_id);
								ps.setInt(8, baseDao.getSeqId("SaleDetail_Seq"));
								ps.setString(9, sacode);
								ps.setString(10, o[6] + "");
								ps.setString(11, o[7] + "");
								ps.setString(12, o[8] + "");
							}

						});
			}
			// 修改请购单状态
			baseDao.updateByCondition("CargoApplication",
					"ca_statuscode='TURNSALE',ca_status='已转销售单'", "ca_id=" + id);
			// 记录操作
			baseDao.logMessage(new MessageLog(SystemSession.getUser()
					.getEm_name(), "配货申请转销售订单", BaseUtil
					.getLocalMessage("msg.turnSuccess"),
					"CargoApplication|ca_id=" + id));
		}
		return said;
	}

}
