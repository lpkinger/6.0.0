package com.uas.erp.service.plm.impl;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.plm.MileStoneService;
@Service(value="mileStoneService")
public class MileStoneServiceImpl implements MileStoneService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveMileStone(String formStore,String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave("MileStone", new Object[] { gstore, gstore });
		for(Map<Object, Object> m : gstore) {
			m.put("msd_id", baseDao.getSeqId("MileStoneDetail_SEQ"));
		}
		String formSql=SqlUtil.getInsertSqlByFormStore(store, "MileStone", new String[]{}, new String[]{});
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gstore, "MileStoneDetail");
		baseDao.execute(gridSql);
		baseDao.execute(formSql);
		baseDao.logger.save("MileStone", "ms_id", store.get("ms_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave("MileStone", new Object[] { gstore, gstore });
	}

	@Override
	public void deleteMileStone(int ms_id) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel("MileStone", ms_id);
		// 删除purchase
		baseDao.deleteById("MileStoneDetail", "msd_msid", ms_id);
		baseDao.deleteById("MileStone", "ms_id", ms_id);
		// 记录操作
		baseDao.logger.delete("MileStone", "ms_id", ms_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel("MileStone", ms_id);
	}

	@Override
	public void updateMileStone(String formStore,
			String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave("MileStone", new Object[] { gstore, gstore });
		String formSql=SqlUtil.getUpdateSqlByFormStore(store, "MileStone", "ms_id");
		// 修改MProjectPlanDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "MileStoneDetail", "msd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("msd_id") == null || s.get("msd_id").equals("") || s.get("msd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("MileStoneDetail_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "MileStoneDetail", new String[] { "msd_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(formSql);
		baseDao.execute(gridSql);
		baseDao.logger.update("MileStone", "ms_id", store.get("ms_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave("MileStone", new Object[] { gstore, gstore });
	}

	@Override
	public void submitMileStone(int ms_id) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MileStone", "ms_statuscode", "ms_id=" + ms_id);
		StateAssert.submitOnlyEntering(status);
		handlerService.beforeSubmit("MileStone", ms_id);
		// 执行反提交操作
		baseDao.submit("MileStone", "ms_id=" + ms_id, "ms_status", "ms_statuscode");
		// 记录操作
		baseDao.logger.submit("MileStone", "ms_id", ms_id);
		handlerService.afterSubmit("MileStone", ms_id);
	}

	@Override
	public void resSubmitMileStone(int ms_id) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MileStone","ms_statuscode", "ms_id=" + ms_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit("MileStone", ms_id);
		// 执行反提交操作
		baseDao.resOperate("MileStone", "ms_id=" + ms_id, "ms_status", "ms_statuscode");
		// 记录操作
		baseDao.logger.resSubmit("MileStone", "ms_id", ms_id);
		handlerService.afterResSubmit("MileStone", ms_id);
	}

	@Override
	public void auditMileStone(int ms_id) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("MileStone", "ms_statuscode", "ms_id=" + ms_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit("MileStone", ms_id);
		// 执行审核操作
		baseDao.audit("MileStone", "ms_id=" + ms_id, "ms_status", "ms_statuscode", "ms_auditdate", "ms_auditer");
		// 记录操作
		baseDao.logger.audit("MileStone", "ms_id", ms_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit("MileStone", ms_id);
	}

	@Override
	public void resAuditMileStone(int ms_id) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("MileStone", "ms_statuscode", "ms_id=" + ms_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("MileStone", "ms_id=" + ms_id, "ms_status", "ms_statuscode");
		baseDao.logger.resAudit("MileStone", "ms_id", ms_id);
	}

}
