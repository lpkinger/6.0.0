package com.uas.erp.service.hr.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.ReandpunishsetService;

@Service
public class ReandpunishsetServiceImpl implements ReandpunishsetService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveReandpunishset(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[]{store});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Reandpunishset", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "rs_id", store.get("rs_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updateReandpunishsetById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Reandpunishset", "rs_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "rs_id", store.get("rs_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
	}

	@Override
	public void deleteReandpunishset(int rs_id, String caller) {
		int count=baseDao.getCount("select count(1) from Reandpunish left join Reandpunishset on rp_class=rs_name and rp_type=rs_type where rs_id="+rs_id);
		if(count>0){
			BaseUtil.showError("此种类被使用不允许删除！");
		}
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{rs_id});
		//删除
		baseDao.deleteById("Reandpunishset", "rs_id", rs_id);
		//记录操作
		baseDao.logger.delete(caller, "rs_id", rs_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{rs_id});
	}
}
