package com.uas.erp.service.drp.impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.drp.RepairreserveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: USOFTPC30 Date: 13-5-21 Time: 下午5:05 To
 * change this template use File | Settings | File Templates.
 */
@Service
public class RepairreserveServiceImpl implements RepairreserveService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveRepairreserve(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Make",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);

		Object[] mm_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			mm_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				mm_id[i] = baseDao.getSeqId("MAKEMATERIAL_SEQ");
			}
		} else {
			mm_id[0] = baseDao.getSeqId("MAKEMATERIAL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore,
				"MakeMaterial", "mm_id", mm_id);
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "ma_id", store.get("ma_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void updateRepairreserveById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });

		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Make",
				"ma_id");
		baseDao.execute(formSql);

		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"MakeMaterial", "mm_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("mm_id") == null || s.get("mm_id").equals("")
					|| s.get("mm_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("MAKEMATERIAL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "MakeMaterial",
						new String[] { "mm_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "ma_id", store.get("ma_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteRepairreserve(int ma_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, ma_id);
		// 删除purchase
		baseDao.deleteById("Make", "ma_id", ma_id);
		// 删除purchaseDetail
		baseDao.deleteById("MakeMaterial", "mm_maid", ma_id);
		// 记录操作
		baseDao.logger.delete(caller, "ma_id", ma_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ma_id);
	}

	@Override
	public void auditRepairreserve(int ma_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Make",
				"ma_statuscode", "ma_id=" + ma_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ma_id);
		// 执行审核操作
		baseDao.updateByCondition("Make", "ma_statuscode='AUDITED',ma_status='"
				+ BaseUtil.getLocalMessage("AUDITED") + "',MA_AUDITMAN='"
				+ SystemSession.getUser().getEm_name()
				+ "',MA_AUDITDATE=sysdate", "ma_id=" + ma_id);
		// 记录操作
		baseDao.logger.audit(caller, "ma_id", ma_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ma_id);
	}

	@Override
	public void resAuditRepairreserve(int ma_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Make",
				"ma_statuscode", "ma_id=" + ma_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, ma_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"Make",
				"ma_statuscode='ENTERING',ma_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',MA_AUDITMAN='',MA_AUDITDATE=null", "ma_id="
						+ ma_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ma_id", ma_id);
		handlerService.afterResAudit(caller, ma_id);
	}

	@Override
	public void submitRepairreserve(int ma_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Make",
				"ma_statuscode", "ma_id=" + ma_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ma_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"Make",
				"ma_statuscode='COMMITED',ma_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "ma_id="
						+ ma_id);
		// 记录操作
		baseDao.logger.submit(caller, "ma_id", ma_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ma_id);
	}

	@Override
	public void resSubmitRepairreserve(int ma_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Make",
				"ma_statuscode", "ma_id=" + ma_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, ma_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"Make",
				"ma_statuscode='ENTERING',ma_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "ma_id="
						+ ma_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ma_id", ma_id);
		handlerService.afterResSubmit(caller, ma_id);
	}
}
