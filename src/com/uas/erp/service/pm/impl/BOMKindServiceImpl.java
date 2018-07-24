package com.uas.erp.service.pm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.BOMKindService;



@Service("BOMKindService")
public class BOMKindServiceImpl implements BOMKindService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveBOMKind(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		/*//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BOMDetail", "bd_code='" + store.get("bd_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist", language));
		}*/
		//执行保存前的其它逻辑
		handlerService.beforeSave("BOMKind",new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BOMKind", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "bk_id", store.get("bk_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}		
		//执行保存后的其它逻辑
		handlerService.afterSave("BOMKind",new Object[]{store});
	}
	
	@Override
	public void deleteBOMKind(int bk_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel("BOMKind", new Object[]{bk_id});
		//删除
		baseDao.deleteById("BOMKind", "bk_id", bk_id);
		//记录操作
		baseDao.logger.delete(caller,"bk_id", bk_id);
		//执行删除后的其它逻辑bd_id
		handlerService.afterDel("BOMKind",new Object[]{bk_id});
	}
	
	@Override
	public void updateBOMKindById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeSave("BOMKind", new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BOMKind", "bk_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "bk_id", store.get("bk_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave("BOMKind", new Object[]{store});
	}
}
