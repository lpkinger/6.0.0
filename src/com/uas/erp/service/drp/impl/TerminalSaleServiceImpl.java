package com.uas.erp.service.drp.impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.drp.TerminalSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TerminalSaleServiceImpl implements TerminalSaleService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveTerminalSale(String formStore, String gridStore,
			String caller) {
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[] { store, grid });
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"terminalSale", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存Detail
		Object[] id = new Object[grid.size()];
		for (int i = 0; i < grid.size(); i++) {
			Map<Object, Object> map = grid.get(i);
			id[i] = baseDao.getSeqId("TERMINALSALEDETAIL_SEQ");
			map.put("tsd_id", id[i]);
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"terminalSaleDetail");
		baseDao.execute(gridSql);

		try {
			// 记录操作
			baseDao.logger.save(caller, "ts_id", store.get("ts_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });

	}

	@Override
	public void updateTerminalSaleById(String formStore, String gridStore,
			String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"terminalSale", "ts_id");
		baseDao.execute(formSql);
		// 修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"terminalSaleDetail", "tsd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("tsd_id") == null || s.get("tsd_id").equals("")
					|| s.get("tsd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("TERMINALSALEDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "terminalSaleDetail",
						new String[] { "tsd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "ts_id", store.get("ts_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteTerminalSale(int ts_id, String caller) {

		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, ts_id);
		// 删除purchase
		baseDao.deleteById("terminalSale", "ts_id", ts_id);
		// 删除purchaseDetail
		baseDao.deleteById("terminalSaleDetail", "tsd_tsid", ts_id);
		// 记录操作
		baseDao.logger.delete(caller, "ts_id", ts_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ts_id);

	}

	@Override
	public void auditTerminalSale(int ts_id, String caller) {

		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("terminalSale",
				"ts_statuscode", "ts_id=" + ts_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ts_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"terminalSale",
				"ts_statuscode='AUDITED',ts_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',ts_auditer='"
						+ SystemSession.getUser().getEm_name()
						+ "',ts_auditdate=sysdate", "ts_id=" + ts_id);
		// 记录操作
		baseDao.logger.audit(caller, "ts_id", ts_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ts_id);

	}

	@Override
	public void resAuditTerminalSale(int ts_id, String caller) {

		Object status = baseDao.getFieldDataByCondition("terminalSale",
				"ts_statuscode", "ts_id=" + ts_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, ts_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"terminalSale",
				"ts_statuscode='ENTERING',ts_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',ts_auditer='',ts_auditdate=null", "ts_id=" + ts_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ts_id", ts_id);
		handlerService.afterResAudit(caller, ts_id);

	}

	@Override
	public void submitTerminalSale(int ts_id, String caller) {

		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("terminalSale",
				"ts_statuscode", "ts_id=" + ts_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ts_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"terminalSale",
				"ts_statuscode='COMMITED',ts_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "ts_id="
						+ ts_id);
		// 记录操作
		baseDao.logger.submit(caller, "ts_id", ts_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ts_id);

	}

	@Override
	public void resSubmitTerminalSale(int ts_id, String caller) {

		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("terminalSale",
				"ts_statuscode", "ts_id=" + ts_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, ts_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"terminalSale",
				"ts_statuscode='ENTERING',ts_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "ts_id="
						+ ts_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ts_id", ts_id);
		handlerService.afterResSubmit(caller, ts_id);

	}

}
