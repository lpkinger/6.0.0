package com.uas.erp.service.drp.impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.drp.ExchangeCuService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ExchangeCuServiceImpl implements ExchangeCuService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveExchangeCu(String formStore, String gridStore, String caller) {
		// 保存Detail
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[] { store, grid });
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ExchangeCu",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		Object[] id = new Object[grid.size()];
		for (int i = 0; i < grid.size(); i++) {
			Map<Object, Object> map = grid.get(i);
			id[i] = baseDao.getSeqId("EXCHANGECUDETAIL_SEQ");
			map.put("ecd_id", id[i]);
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"ExchangeCuDetail");
		baseDao.execute(gridSql);

		try {
			// 记录操作
			baseDao.logger.save(caller, "ec_id", store.get("ec_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });

	}

	@Override
	public void updateExchangeCuById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ExchangeCu",
				"ec_id");
		baseDao.execute(formSql);
		// 修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"ExchangeCuDetail", "ecd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("ecd_id") == null || s.get("ecd_id").equals("")
					|| s.get("ecd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("EXCHANGECUDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ExchangeCuDetail",
						new String[] { "ecd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "ec_id", store.get("ec_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteExchangeCu(int ec_id, String caller) {

		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, ec_id);
		// 删除purchase
		baseDao.deleteById("ExchangeCu", "ec_id", ec_id);
		// 删除purchaseDetail
		baseDao.deleteById("ExchangeCuDetail", "ecd_ecid", ec_id);
		// 记录操作
		baseDao.logger.delete(caller, "ec_id", ec_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ec_id);

	}

	@Override
	public void auditExchangeCu(int ec_id, String caller) {

		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ExchangeCu",
				"ec_statuscode", "ec_id=" + ec_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ec_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"ExchangeCu",
				"ec_statuscode='AUDITED',ec_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',ec_auditer='"
						+ SystemSession.getUser().getEm_name()
						+ "',ec_auditdate=sysdate", "ec_id=" + ec_id);
		// 记录操作
		baseDao.logger.audit(caller, "ec_id", ec_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ec_id);

	}

	@Override
	public void resAuditExchangeCu(int ec_id, String caller) {

		Object status = baseDao.getFieldDataByCondition("ExchangeCu",
				"ec_statuscode", "ec_id=" + ec_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, ec_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"ExchangeCu",
				"ec_statuscode='ENTERING',ec_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',ec_auditer='',ec_auditdate=null", "ec_id=" + ec_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ec_id", ec_id);
		handlerService.afterResAudit(caller, ec_id);

	}

	@Override
	public void submitExchangeCu(int ec_id, String caller) {

		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("exchangeCu",
				"ec_statuscode", "ec_id=" + ec_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ec_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"exchangeCu",
				"ec_statuscode='COMMITED',ec_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "ec_id="
						+ ec_id);
		// 记录操作
		baseDao.logger.submit(caller, "ec_id", ec_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ec_id);

	}

	@Override
	public void resSubmitExchangeCu(int ec_id, String caller) {

		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ExchangeCu",
				"ec_statuscode", "ec_id=" + ec_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, ec_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"ExchangeCu",
				"ec_statuscode='ENTERING',ec_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "ec_id="
						+ ec_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ec_id", ec_id);
		handlerService.afterResSubmit(caller, ec_id);

	}

}
