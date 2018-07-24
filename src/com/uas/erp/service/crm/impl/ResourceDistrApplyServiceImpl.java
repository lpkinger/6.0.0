package com.uas.erp.service.crm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.crm.ResourceDistrApplyService;

@Service
public class ResourceDistrApplyServiceImpl implements ResourceDistrApplyService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void audit(int ra_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ResourceDistrApply",
				"ra_statuscode", "ra_id=" + ra_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ra_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"ResourceDistrApply",
				"ra_statuscode='AUDITED',ra_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',ra_auditor='"
						+ SystemSession.getUser().getEm_name()
						+ "',ra_auditdate=sysdate", "ra_id=" + ra_id);
		// 反应到客户分配表中
		String insertSql = "INSERT INTO ResourceDistr(rd_id,rd_prid,rd_sellercode,rd_seller,rd_detno) SELECT"
				+ " ResourceDistr_SEQ.nextval,rad_prid,rad_sellercode,rad_seller,rad_detno from ResourceDistrApplyDet where rad_raid="
				+ ra_id;
		baseDao.execute(insertSql);
		// 记录操作
		baseDao.logger.audit(caller, "ra_id", ra_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ra_id);
	}

	@Override
	public void resAudit(int ra_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("ResourceDistrApply",
				"ra_statuscode", "ra_id=" + ra_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, ra_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"ResourceDistrApply",
				"ra_statuscode='ENTERING',ra_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',ra_auditor='',ra_auditdate=null", "ra_id=" + ra_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ra_id", ra_id);
		handlerService.afterResAudit(caller, ra_id);
	}

	@Override
	public void deleteResourceDistrApply(int ra_id, String caller) {
		handlerService.beforeDel(caller, ra_id);
		// 删除purchase
		baseDao.deleteById("ResourceDistrApplyDet", "rad_raid", ra_id);
		baseDao.deleteById("ResourceDistrApply", "ra_id", ra_id);
		// 记录操作
		baseDao.logger.delete(caller, "ra_id", ra_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ra_id);
	}

	@Override
	public void saveResourceDistrApply(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		for (Map<Object, Object> m : gstore) {
			m.put("rad_id", baseDao.getSeqId("ResourceDistrApplyDet_SEQ"));
			m.put("rad_prid", store.get("ra_prid"));
		}
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"ResourceDistrApply", new String[] {}, new String[] {});
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gstore,
				"ResourceDistrApplyDet");
		baseDao.execute(gridSql);
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "ra_id", store.get("ra_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void updateResourceDistrApply(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"ResourceDistrApply", "ra_id");
		// 修改MProjectPlanDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"ResourceDistrApplyDet", "rad_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("rad_id") == null || s.get("rad_id").equals("")
					|| s.get("rad_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("ResourceDistrApplyDet_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s,
						"ResourceDistrApplyDet", new String[] { "rad_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(formSql);
		baseDao.execute(gridSql);
		// 如果主记录改变了客户，反应到明细上
		baseDao.updateByCondition("ResourceDistrApplyDet",
				"rad_prid=" + store.get("ra_prid"),
				"rad_raid=" + store.get("ra_id"));
		// 记录操作
		baseDao.logger.update(caller, "ra_id", store.get("ra_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

}
