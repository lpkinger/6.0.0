package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.PlmpreproductService;

@Service
public class PlmpreproductServiceImpl implements PlmpreproductService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void savePlmpreproduct(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[] {store, grid});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Plmpreproduct", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "Plmpreproductdet", "ppd_id");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "pp_id", store.get("pp_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store, grid});
	}

	@Override
	public void updatePlmpreproductById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[] {store, gstore});
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Plmpreproduct", "pp_id");
		baseDao.execute(formSql);
		// 修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "Plmpreproductdet", "ppd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("ppd_id") == null || s.get("ppd_id").equals("")
					|| s.get("ppd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("Plmpreproductdet_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "Plmpreproductdet",
						new String[] { "ppd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "pp_id", store.get("pp_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store, gstore});
	}

	@Override
	public void deletePlmpreproduct(int pp_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, pp_id);
		// 删除purchase
		baseDao.deleteById("Plmpreproduct", "pp_id", pp_id);
		// 删除purchaseDetail
		baseDao.deleteById("Plmpreproductdet", "ppd_ppid", pp_id);
		// 记录操作
		baseDao.logger.delete(caller, "pp_id", pp_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, pp_id);
	}

	@Override
	public void auditPlmpreproduct(int pp_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Plmpreproduct", "pp_statuscode", "pp_id=" + pp_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, pp_id);
		// 执行审核操作
		baseDao.audit("Plmpreproduct", "pp_id=" + pp_id, "pp_status", "pp_statuscode", "pp_auditdate", "pp_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "pp_id", pp_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, pp_id);
	}

	@Override
	public void resAuditPlmpreproduct(int pp_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Plmpreproduct", "pp_statuscode", "pp_id=" + pp_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("Plmpreproduct", "pp_id=" + pp_id, "pp_status", "pp_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "pp_id", pp_id);

	}

	@Override
	public void submitPlmpreproduct(int pp_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Plmpreproduct", "pp_statuscode", "pp_id=" + pp_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pp_id);
		// 执行提交操作
		baseDao.submit("Plmpreproduct", "pp_id=" + pp_id, "pp_status", "pp_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pp_id", pp_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pp_id);
	}

	@Override
	public void resSubmitPlmpreproduct(int pp_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Plmpreproduct", "pp_statuscode", "pp_id=" + pp_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, pp_id);
		// 执行反提交操作
		baseDao.resOperate("Plmpreproduct", "pp_id=" + pp_id, "pp_status", "pp_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pp_id", pp_id);
		handlerService.afterResSubmit(caller, pp_id);
	}
}
