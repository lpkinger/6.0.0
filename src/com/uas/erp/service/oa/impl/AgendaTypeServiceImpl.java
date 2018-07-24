package com.uas.erp.service.oa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.AgendaTypeDao;
import com.uas.erp.model.AgendaType;
import com.uas.erp.service.oa.AgendaTypeService;

@Service
public class AgendaTypeServiceImpl implements AgendaTypeService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private  AgendaTypeDao agendaTypeDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveAgendaType(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "AgendaType", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save(caller, "at_id", store.get("at_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}
	@Override
	public void updateAgendaType(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});	
		//修改BasicData
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "AgendaType", "at_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.delete(caller, "at_id", store.get("at_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});		
	}

	@Override
	public void deleteAgendaType(int at_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{at_id,caller});
		//删除AgendaType
		baseDao.deleteById("AgendaType", "at_id", at_id);
		//记录操作
		baseDao.logger.delete(caller, "at_id", at_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{at_id,caller});
	}
	@Override
	public void deleteById(int id) {
		agendaTypeDao.delete(id);		
	}
	@Override
	public List<AgendaType> getAll(int page, int pageSize) {
		return agendaTypeDao.getAll(page, pageSize);
	}
	@Override
	public int getAllCount(String caller) {
		return agendaTypeDao.getAllCount();
	}
	@Override
	public int getSearchCount(String name) {
		return agendaTypeDao.getSearchCount(name);
	}
	@Override
	public List<AgendaType> searchByName(String name, int page, int pageSize) {
		return agendaTypeDao.getByName(name, page, pageSize);
	}
	@Override
	public AgendaType getById(int id) {
		return agendaTypeDao.getById(id);
	}
	@Override
	public void vastDeleteAgendaType(int[] id, String caller) {
		boolean boolOA=false;
		for (int key : id) {
			 boolOA=baseDao.checkIf("Agenda","ag_atid="+key);
			if(boolOA){
				Object at_name=baseDao.getFieldDataByCondition("AgendaType", "at_name", "at_id="+key);
				BaseUtil.showError("类型:'"+at_name+"' 已被使用不允许删除");
			}
		}
		for (int key : id) {
				baseDao.deleteById("AgendaType", "at_id", key);
		}
	}
}
