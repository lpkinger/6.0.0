package com.uas.erp.service.drp.impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.drp.AllotApplicationCuService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AllotApplicationCuServiceImpl implements AllotApplicationCuService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveAllotApplicationCu(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[] { store, grid });
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"AllotApplicationCu", new String[] {}, new Object[] {});
		baseDao.execute(formSql);

		// 保存Detail
		Object[] id = new Object[grid.size()];
		for (int i = 0; i < grid.size(); i++) {
			Map<Object, Object> map = grid.get(i);
			id[i] = baseDao.getSeqId("ALLOTCUDETAIL_SEQ");
			map.put("ad_id", id[i]);
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"AllotCuDetail");
		baseDao.execute(gridSql);

		try {
			// 记录操作
			baseDao.logger.save(caller, "aa_id", store.get("aa_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });

	}

	@Override
	public void updateAllotApplicationCuById(String formStore,
			String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"AllotApplicationCu", "aa_id");
		baseDao.execute(formSql);
		// 修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"AllotCuDetail", "ad_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("ad_id") == null || s.get("ad_id").equals("")
					|| s.get("ad_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("ALLOTCUDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "AllotCuDetail",
						new String[] { "ad_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "aa_id", store.get("aa_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteAllotApplicationCu(int aa_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, aa_id);
		// 删除purchase
		baseDao.deleteById("AllotApplicationCu", "aa_id", aa_id);
		// 删除purchaseDetail
		baseDao.deleteById("AllotCuDetail", "ad_aaid", aa_id);
		// 记录操作
		baseDao.logger.delete(caller, "aa_id", aa_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, aa_id);

	}

	@Override
	public void auditAllotApplicationCu(int aa_id, String caller) {

		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("AllotApplicationCu",
				"aa_statuscode", "aa_id=" + aa_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, aa_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"AllotApplicationCu",
				"aa_statuscode='AUDITED',aa_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',aa_auditer='"
						+ SystemSession.getUser().getEm_name()
						+ "',aa_auditdate=sysdate", "aa_id=" + aa_id);
		// 记录操作
		baseDao.logger.audit(caller, "aa_id", aa_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, aa_id);

	}

	@Override
	public void resAuditAllotApplicationCu(int aa_id, String caller) {

		Object status = baseDao.getFieldDataByCondition("AllotApplicationCu",
				"aa_statuscode", "aa_id=" + aa_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, aa_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"AllotApplicationCu",
				"aa_statuscode='ENTERING',aa_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',aa_auditer='',aa_auditdate=null", "aa_id=" + aa_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "aa_id", aa_id);
		handlerService.afterResAudit(caller, aa_id);

	}

	@Override
	public void submitAllotApplicationCu(int aa_id, String caller) {

		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("allotApplicationCu",
				"aa_statuscode", "aa_id=" + aa_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, aa_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"allotApplicationCu",
				"aa_statuscode='COMMITED',aa_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "aa_id="
						+ aa_id);
		// 记录操作
		baseDao.logger.submit(caller, "aa_id", aa_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, aa_id);

	}

	@Override
	public void resSubmitAllotApplicationCu(int aa_id, String caller) {

		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("AllotApplicationCu",
				"aa_statuscode", "aa_id=" + aa_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, aa_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"AllotApplicationCu",
				"aa_statuscode='ENTERING',aa_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "aa_id="
						+ aa_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "aa_id", aa_id);
		handlerService.afterResSubmit(caller, aa_id);

	}

}
