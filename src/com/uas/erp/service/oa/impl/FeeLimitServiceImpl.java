package com.uas.erp.service.oa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.FeeLimitService;

@Service
public class FeeLimitServiceImpl implements FeeLimitService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveFeeLimit(String formStore, String gridStore,
			String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] {store,grid });
		// 保存FeeLimit
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "FeeLimit",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存Contact
		for (Map<Object, Object> s : grid) {
			s.put("fld_id", baseDao.getSeqId("FeeLimitdetail_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"FeeLimitdetail");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "fl_id", store.get("fl_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store,grid });
	}

	@Override
	public void deleteFeeLimit(int fl_id, String  caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("FeeLimit",
				"fl_statuscode", "fl_id=" + fl_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] {fl_id});
		// 删除FeeLimit
		baseDao.deleteById("FeeLimit", "fl_id", fl_id);
		// 删除Contact
		baseDao.deleteById("FeeLimitdetail", "fld_flid", fl_id);
		// 记录操作
		baseDao.logger.delete(caller, "fl_id", fl_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] {fl_id});
	}

	@Override
	public void updateFeeLimitById(String formStore, String gridStore,
			String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("FeeLimit",
				"fl_statuscode", "fl_id=" + store.get("fl_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[] {store,gstore});
		// 修改FeeLimit
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "FeeLimit",
				"fl_id");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"FeeLimitdetail", "fld_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("fld_id") == null || s.get("fld_id").equals("")
					|| s.get("fld_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("FeeLimitdetail_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "FeeLimitdetail",
						new String[] { "fld_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.delete(caller, "fl_id", store.get("fl_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[] {store,gstore});

	}

	@Override
	public void submitFeeLimit(int fl_id, String  caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("FeeLimit",
				"fl_statuscode", "fl_id=" + fl_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] {fl_id});
		// 执行提交操作
		baseDao.submit("FeeLimit", "fl_id=" + fl_id, "fl_status", "fl_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "fl_id", fl_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] {fl_id});

	}

	@Override
	public void resSubmitFeeLimit(int fl_id, String  caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("FeeLimit",
				"fl_statuscode", "fl_id=" + fl_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] {fl_id});
		// 执行反提交操作
		baseDao.resOperate("FeeLimit", "fl_id=" + fl_id, "fl_status", "fl_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "fl_id", fl_id);
		handlerService.afterResSubmit(caller, new Object[] {fl_id});

	}

	@Override
	public void auditFeeLimit(int fl_id, String  caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("FeeLimit",
				"fl_statuscode", "fl_id=" + fl_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] {fl_id});
		// 执行审核操作
		baseDao.updateByCondition(
				"FeeLimit",
				"fl_statuscode='AUDITED',fl_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'",
				"fl_id=" + fl_id);
		// 记录操作
		baseDao.logger.audit(caller, "fl_id", fl_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] {fl_id});

	}

	@Override
	public void resAuditFeeLimit(int fl_id, String  caller) {
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, new Object[] {fl_id});
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("FeeLimit",
				"fl_statuscode", "fl_id=" + fl_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("FeeLimit", "fl_id=" + fl_id, "fl_status", "fl_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "fl_id", fl_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, new Object[] {fl_id});

	}

}
