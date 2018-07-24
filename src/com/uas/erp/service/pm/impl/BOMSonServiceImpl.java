package com.uas.erp.service.pm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.BOMSonService;



@Service("BOMSonService")
public class BOMSonServiceImpl implements BOMSonService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveBOMSon(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		/*//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BOMDetail", "bd_code='" + store.get("bd_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist", language));
		}*/
		//执行保存前的其它逻辑
		handlerService.beforeSave("BOM!Son",new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BOMDetail", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "bd_id", store.get("bd_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}		
		//执行保存后的其它逻辑
		handlerService.afterSave("BOM!Son",new Object[]{store});
	}
	
	@Override
	public void deleteBOMSon(int bd_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel("BOM!Son",new Object[]{bd_id});
		//删除
		baseDao.deleteById("BOMDetail", "bd_id", bd_id);
		//记录操作
		baseDao.logger.delete(caller, "bd_id", bd_id);
		//执行删除后的其它逻辑bd_id
		handlerService.afterDel("BOM!Son",new Object[]{bd_id});
	}
	
	@Override
	public void updateBOMSonById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate("BOM!Son", new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BOMDetail", "bd_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "bd_id", store.get("bd_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate("BOM!Son", new Object[]{store});
	}
}
