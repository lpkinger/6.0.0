package com.uas.erp.service.hr.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.DataListDao;
import com.uas.erp.dao.common.HrJobDao;
import com.uas.erp.model.DataList;
import com.uas.erp.model.HRJob;
import com.uas.erp.model.Role;
import com.uas.erp.service.hr.HrJobService;

@Service
public class HrJobServiceImpl implements HrJobService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private HrJobDao hrJobDao;
	@Autowired
	private DataListDao dataListDao;

	@Override
	@CacheEvict(value = "hrjob", allEntries = true)
	public void saveHrJob(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);

		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "HrJob", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "jo_id", store.get("jo_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	@CacheEvict(value = "hrjob", allEntries = true)
	public void deleteHrJob(int jo_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { jo_id });
		// 删除
		baseDao.deleteById("HrJob", "jo_id", jo_id);
		// 记录操作
		baseDao.logger.delete(caller, "jo_id", jo_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { jo_id });
	}

	@Override
	@CacheEvict(value = "hrjob", allEntries = true)
	public void updateHrJobById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "HrJob", "jo_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "jo_id", store.get("jo_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public List<HRJob> getHrJobs() {
		return hrJobDao.getHrJobs(SpObserver.getSp());
	}

	@Override
	public Map<String, Object> getLimitFieldsByCaller(String caller, int jo_id, String utype) {
		Map<String, Object> map = new HashMap<String, Object>();
		if ("list".equals(utype)) {
			DataList datalist = dataListDao.getDataList(caller, SpObserver.getSp());
			map.put("limits", hrJobDao.getLimitFieldsByType(caller, null, 2, jo_id, SpObserver.getSp()));
			if (datalist != null && datalist.getDl_relative() != null) {
				map.put("relativelimits", hrJobDao.getLimitFieldsByType(datalist.getDl_relative(), null, 2, jo_id, SpObserver.getSp()));
			}
		} else{ 
			map.put("limits", hrJobDao.getLimitFieldsByCaller(caller, jo_id, SpObserver.getSp()));
		}
		return map;

	}

	@Override
	public Map<String, Object> getRoleLimitFieldsByCaller(String caller, Integer ro_id, String utype) {
		Map<String, Object> map = new HashMap<String, Object>();
		if ("list".equals(utype)) {
			DataList datalist = dataListDao.getDataList(caller, SpObserver.getSp());
			map.put("limits", hrJobDao.getRoleLimitFieldsByType(caller, null, 2, ro_id, SpObserver.getSp()));
			if (datalist != null && datalist.getDl_relative() != null) {
				map.put("relativelimits", hrJobDao.getRoleLimitFieldsByType(datalist.getDl_relative(), null, 2, ro_id, SpObserver.getSp()));
			}
		} else{ 
			map.put("limits", hrJobDao.getRoleLimitFieldsByCaller(caller, ro_id, SpObserver.getSp()));
		}
		return map;

	}
	
	public Map<String, Object> getSelfLimitFieldsByCaller(String caller, Integer em_id, String utype) {
		Map<String, Object> map = new HashMap<String, Object>();
		if ("list".equals(utype)) {
			DataList datalist = dataListDao.getDataList(caller, SpObserver.getSp());
			map.put("limits", hrJobDao.getSelfLimitFieldsByType(caller, 2, em_id, SpObserver.getSp()));
			if (datalist != null && datalist.getDl_relative() != null) {
				map.put("relativelimits", hrJobDao.getSelfLimitFieldsByType(datalist.getDl_relative(), 2, em_id, SpObserver.getSp()));
			}
		} else
			map.put("limits", hrJobDao.getSelfLimitFieldsByCaller(caller, em_id));
		return map;
	}

	@Override
	public List<Role> getRoles() {
		return hrJobDao.getRoles(SpObserver.getSp());
	}
}
