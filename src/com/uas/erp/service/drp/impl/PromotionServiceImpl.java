package com.uas.erp.service.drp.impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.drp.PromotionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PromotionServiceImpl implements PromotionService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void savePromotion(String formStore, String gridStore, String caller) {
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[] { store, grid });
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"promotion", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存Detail
		Object[] id = new Object[grid.size()];
		for (int i = 0; i < grid.size(); i++) {
			Map<Object, Object> map = grid.get(i);
			id[i] = baseDao.getSeqId("PROMOTIONDETAIL_SEQ");
			map.put("ptd_id", id[i]);
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"promotiondetail");
		baseDao.execute(gridSql);

		try {
			// 记录操作
			baseDao.logger.save(caller, "pt_id", store.get("pt_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });

	}

	@Override
	public void updatePromotionById(String formStore, String gridStore,
			String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"promotion", "pt_id");
		baseDao.execute(formSql);
		// 修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"promotiondetail", "ptd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("ptd_id") == null || s.get("ptd_id").equals("")
					|| s.get("ptd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("PROMOTIONDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "promotiondetail",
						new String[] { "ptd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "pt_id", store.get("pt_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void deletePromotion(int pt_id, String caller) {

		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, pt_id);
		// 删除purchase
		baseDao.deleteById("promotion", "pt_id", pt_id);
		// 删除purchaseDetail
		baseDao.deleteById("promotiondetail", "ptd_ptid", pt_id);
		// 记录操作
		baseDao.logger.delete(caller, "pt_id", pt_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, pt_id);

	}

	@Override
	public void auditPromotion(int pt_id, String caller) {

		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("promotion",
				"pt_statuscode", "pt_id=" + pt_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, pt_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"promotion",
				"pt_statuscode='AUDITED',pt_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',pt_auditer='"
						+ SystemSession.getUser().getEm_name()
						+ "',pt_auditdate=sysdate", "pt_id=" + pt_id);
		// 记录操作
		baseDao.logger.audit(caller, "pt_id", pt_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, pt_id);

	}

	@Override
	public void resAuditPromotion(int pt_id, String caller) {

		Object status = baseDao.getFieldDataByCondition("promotion",
				"pt_statuscode", "pt_id=" + pt_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, pt_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"promotion",
				"pt_statuscode='ENTERING',pt_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',pt_auditer='',pt_auditdate=null", "pt_id=" + pt_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "pt_id", pt_id);
		handlerService.afterResAudit(caller, pt_id);

	}

	@Override
	public void submitPromotion(int pt_id, String caller) {

		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("promotion",
				"pt_statuscode", "pt_id=" + pt_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pt_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"promotion",
				"pt_statuscode='COMMITED',pt_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "pt_id="
						+ pt_id);
		// 记录操作
		baseDao.logger.submit(caller, "pt_id", pt_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pt_id);

	}

	@Override
	public void resSubmitPromotion(int pt_id, String caller) {

		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("promotion",
				"pt_statuscode", "pt_id=" + pt_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, pt_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"promotion",
				"pt_statuscode='ENTERING',pt_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "pt_id="
						+ pt_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "pt_id", pt_id);
		handlerService.afterResSubmit(caller, pt_id);

	}

}
