package com.uas.erp.service.oa.impl;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.AgencyService;

@Service
public class AgencyServiceImpl implements AgencyService
{

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void save(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller,new Object[]{store});
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Agency", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save(caller, "ay_id", store.get("ay_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller,new Object[]{store});
	}
	@Override
	public void updateAgency(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[]{store});	
		//修改BasicData
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Agency", "ay_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "ay_id", store.get("ay_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[]{store});		
	}

	@Override
	public void deleteAgency(int ay_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{ay_id,caller});
		//删除purchase
		baseDao.deleteById("Agency", "ay_id", ay_id);
		//记录操作
		baseDao.logger.delete(caller, "ay_id", ay_id);;
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{ay_id,caller});
	}
}
