package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fa.FundDataService;
import com.uas.erp.service.fa.FundPlanService;
@Service
public class FundPlanServiceImpl implements FundPlanService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;	

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void save(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "FundPlan", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.logger.save(caller, "fp_id", store.get("fp_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });		
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void update(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, grid });
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "FundPlan", "fp_id");
		baseDao.execute(formSql);
		baseDao.logger.update(caller, "fp_id", store.get("fp_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, grid });		
	}

	@Override
	public void delete(int fp_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { fp_id });
		// 删除
		baseDao.deleteById("FundPlan", "fp_id", fp_id);
		//删除明细
		baseDao.deleteById("FundPlanDetail", "fpd_fpid", fp_id);
		// 记录操作
		baseDao.logger.delete(caller, "fp_id", fp_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { fp_id });		
	}

}
