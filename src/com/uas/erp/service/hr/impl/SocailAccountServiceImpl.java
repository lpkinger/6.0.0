package com.uas.erp.service.hr.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.SocailAccountService;

@Service
public class SocailAccountServiceImpl implements SocailAccountService {
	
	static final String updateSEmpl = " update employee set EM_ACCIMOUNT='是',EM_ACCUMUCARD=? where em_id=?";

	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void saveSocailAccount(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);		
		handlerService.beforeSave(caller, new Object[]{store});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Employee", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "em_id", store.get("em_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updateSocailAccountById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeUpdate(caller, new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Employee", "em_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "em_id", store.get("em_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
	}

	@Override
	public void deleteSocailAccount(int em_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{em_id});
		//删除
		baseDao.deleteById("Employee", "em_id", em_id);
		//记录操作
		baseDao.logger.delete(caller, "em_id", em_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{em_id});
	}

	@Override
	public void vastSocailAccount(String  caller, String[] mark, int[] id) {	
		String emAccumucard;
		int idValue;
		try {
			for(int i=0;i<id.length;i++){
				emAccumucard = mark[i];
				idValue = id[i];
				baseDao.execute(updateSEmpl, new Object[]{emAccumucard,idValue});
			}
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
		}
	}
}
