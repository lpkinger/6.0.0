package com.uas.erp.service.oa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.DossierService;
@Service
public class DossierServiceImpl implements DossierService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	public void saveDossier(String formStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Dossier", "do_id='" + store.get("do_id") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller,new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Dossier", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "do_id", store.get("do_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		//执行保存后的其它逻辑
		handlerService.afterSave(caller,new Object[]{store});
	}
	@Override
	public void deleteDossier(int do_id, String  caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[]{do_id});
		//删除Knowledge
		baseDao.deleteById("Dossier", "do_id", do_id);		
		//记录操作
		baseDao.logger.delete(caller, "do_id", do_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[]{do_id});
	}

	@Override
	public void updateDossier(String formStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Dossier", "do_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "do_id", store.get("do_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
	}
}

