package com.uas.erp.service.oa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.SynergyDao;
import com.uas.erp.model.Synergy;
import com.uas.erp.service.oa.SynergyService;

@Service
public class SynergyServiceImpl implements SynergyService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private SynergyDao synergyDao;
	@Override
	public void saveSynergy(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Synergy", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save(caller, "sy_id", store.get("sy_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}
	@Override
	public void deleteSynergy(int sy_id, String  caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[]{sy_id});
		//删除purchase
		baseDao.deleteById("Synergy", "sy_id", sy_id);
		//记录操作
		baseDao.logger.delete(caller, "sy_id", sy_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[]{sy_id});
		
	}
	@Override
	public void submitSynergy(int sy_id, String  caller) {
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,  new Object[]{sy_id});
		//执行提交操作
		baseDao.submit("Synergy", "sy_id=" + sy_id, "sy_status", "sy_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "in_id", sy_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller,  new Object[]{sy_id});
	}
	@Override
	public void updateSynergy(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[]{store});
		//修改BasicData
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Synergy", "sy_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "sy_id", store.get("sy_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[]{store});
		
	}
	@Override
	public void deleteById(int sy_id) {
		synergyDao.delete(sy_id);
		
	}
	@Override
	public Synergy getSynergyById(int id) {
		return synergyDao.getSynergyById(id);
	}
	
	@Override
	public List<Synergy> getList(int page, int pageSize) {
		return synergyDao.getList(page, pageSize);
	}
	@Override
	public int getListCount() {
		return synergyDao.getListCount();
	}
	
	@Override
	public List<Synergy> getByCondition(String condition, int page, int pageSize) {
		return synergyDao.getByCondition(condition, page, pageSize);
	}
	@Override
	public int getSearchCount(String condition) {
		return synergyDao.getSearchCount(condition);
	}
	

}
