package com.uas.erp.service.drp.impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.drp.CargoApplicationCuService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CargoApplicationCuServiceImpl implements CargoApplicationCuService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCargoApplicationCu(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[] { store, grid });
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"CargoApplicationCu", new String[] {}, new Object[] {});
		baseDao.execute(formSql);

		// 保存Detail
		Object[] id = new Object[grid.size()];
		for (int i = 0; i < grid.size(); i++) {
			Map<Object, Object> map = grid.get(i);
			id[i] = baseDao.getSeqId("CARGOCUDETAIL_SEQ");
			map.put("cd_id", id[i]);
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"cargoCuDetail");
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
	public void updateCargoApplicationCuById(String formStore,
			String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"cargoApplicationCu", "ca_id");
		baseDao.execute(formSql);
		// 修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"CargoCuDetail", "cd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("cd_id") == null || s.get("cd_id").equals("")
					|| s.get("cd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("CARGOCUDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "CargoCuDetail",
						new String[] { "cd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "ca_id", store.get("ca_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteCargoApplicationCu(int ca_id, String caller) {

		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, ca_id);
		// 删除purchase
		baseDao.deleteById("cargoApplicationCu", "ca_id", ca_id);
		// 删除purchaseDetail
		baseDao.deleteById("cargoCuDetail", "cd_caid", ca_id);
		// 记录操作
		baseDao.logger.delete(caller, "ca_id", ca_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ca_id);
	}

	@Override
	public void auditCargoApplicationCu(int ca_id, String caller) {

		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("cargoApplicationCu",
				"ca_statuscode", "ca_id=" + ca_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ca_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"cargoApplicationCu",
				"ca_statuscode='AUDITED',ca_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',ca_auditer='"
						+ SystemSession.getUser().getEm_name()
						+ "',ca_auditdate=sysdate", "ca_id=" + ca_id);
		// 记录操作
		baseDao.logger.audit(caller, "ca_id", ca_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ca_id);

	}

	@Override
	public void resAuditCargoApplicationCu(int ca_id, String caller) {

		Object status = baseDao.getFieldDataByCondition("cargoApplicationCu",
				"ca_statuscode", "ca_id=" + ca_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, ca_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"cargoApplicationCu",
				"ca_statuscode='ENTERING',ca_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',ca_auditer='',ca_auditdate=null", "ca_id=" + ca_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ca_id", ca_id);
		handlerService.afterResAudit(caller, ca_id);
	}

	@Override
	public void submitCargoApplicationCu(int ca_id, String caller) {

		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("cargoApplicationCu",
				"ca_statuscode", "ca_id=" + ca_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ca_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"cargoApplicationCu",
				"ca_statuscode='COMMITED',ca_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "ca_id="
						+ ca_id);
		// 记录操作
		baseDao.logger.submit(caller, "ca_id", ca_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ca_id);

	}

	@Override
	public void resSubmitCargoApplicationCu(int ca_id, String caller) {

		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("cargoApplicationCu",
				"ca_statuscode", "ca_id=" + ca_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, ca_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"cargoApplicationCu",
				"ca_statuscode='ENTERING',ca_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "ca_id="
						+ ca_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ca_id", ca_id);
		handlerService.afterResSubmit(caller, ca_id);

	}

}
