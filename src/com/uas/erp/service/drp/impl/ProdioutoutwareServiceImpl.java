package com.uas.erp.service.drp.impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.drp.ProdioutoutwareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProdioutoutwareServiceImpl implements ProdioutoutwareService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProdioutoutware(String formStore, String gridStore,
			String caller) {
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[] { store, grid });
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"prodinout", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存Detail
		Object[] id = new Object[grid.size()];
		for (int i = 0; i < grid.size(); i++) {
			Map<Object, Object> map = grid.get(i);
			id[i] = baseDao.getSeqId("PRODIODETAIL_SEQ");
			map.put("pd_id", id[i]);
			map.put("pd_piclass", "售后退换货出库单");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"prodiodetail");
		baseDao.execute(gridSql);

		try {
			// 记录操作
			baseDao.logger.save(caller, "pi_id", store.get("pi_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });

	}

	@Override
	public void updateProdioutoutwareById(String formStore, String gridStore,
			String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"prodinout", "pi_id");
		baseDao.execute(formSql);
		// 修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"prodiodetail", "pd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("pd_id") == null || s.get("pd_id").equals("")
					|| s.get("pd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("PRODIODETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "prodiodetail",
						new String[] { "pd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "pi_id", store.get("pi_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteProdioutoutware(int pi_id, String caller) {

		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, pi_id);
		// 删除purchase
		baseDao.deleteById("prodinout", "pi_id", pi_id);
		// 删除purchaseDetail
		baseDao.deleteById("prodiodetail", "pd_piid", pi_id);
		// 记录操作
		baseDao.logger.delete(caller, "pi_id", pi_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, pi_id);

	}

	@Override
	public void auditProdioutoutware(int pi_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("prodinout",
				"pi_statuscode", "pi_id=" + pi_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, pi_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"prodinout",
				"pi_statuscode='AUDITED',pi_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',PI_AUDITMAN='"
						+ SystemSession.getUser().getEm_name()
						+ "',PI_AUDITDATE=sysdate", "pi_id=" + pi_id);
		// 记录操作
		baseDao.logger.audit(caller, "pi_id", pi_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, pi_id);

	}

	@Override
	public void resAuditProdioutoutware(int pi_id, String caller) {

		Object status = baseDao.getFieldDataByCondition("prodinout",
				"pi_statuscode", "pi_id=" + pi_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, pi_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"prodinout",
				"pi_statuscode='ENTERING',pi_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',PI_AUDITMAN='',PI_AUDITDATE=null", "pi_id="
						+ pi_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "pi_id", pi_id);
		handlerService.afterResAudit(caller, pi_id);

	}

	@Override
	public void submitProdioutoutware(int pi_id, String caller) {

		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("prodinout",
				"pi_statuscode", "pi_id=" + pi_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pi_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"prodinout",
				"pi_statuscode='COMMITED',pi_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "pi_id="
						+ pi_id);
		// 记录操作
		baseDao.logger.submit(caller, "pi_id", pi_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pi_id);

	}

	@Override
	public void resSubmitProdioutoutware(int pi_id, String caller) {

		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("prodinout",
				"pi_statuscode", "pi_id=" + pi_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, pi_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"prodinout",
				"pi_statuscode='ENTERING',pi_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "pi_id="
						+ pi_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "pi_id", pi_id);
		handlerService.afterResSubmit(caller, pi_id);

	}

}
