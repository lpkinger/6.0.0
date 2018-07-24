package com.uas.erp.service.pm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.MakeKindService;

@Service
public class MakeKindServiceImpl implements MakeKindService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveMakeKind(String formStore,String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("MakeKind", "mk_id='" + store.get("mk_id") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//类型名称重复时，限制保存并提示；		
		Object precode = baseDao.getFieldDataByCondition("MakeKind", "mk_id", "mk_id<>'" + store.get("mk_id") + "' AND mk_name='" + store.get("mk_name") + "'");
		if (precode != null) {
			BaseUtil.showError("类型名称重复,不允许保存");
		}	
		int mrpkind=baseDao.getCountByCondition("MakeKind", "mk_id<>"+store.get("mk_id") +" and mk_ifmrpkind<>0 and mk_makind='"+ store.get("mk_makind") +"'");
		if (mrpkind>0) { 
			if (store.containsKey("mk_ifmrpkind") && !store.get("mk_ifmrpkind").toString().equals("0") && !store.get("mk_ifmrpkind").toString().equals("") ){
				BaseUtil.showError("制造或委外工单只能有一个类型设置为默认的MRP投放类型");
			} 
		} 
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MakeKind", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "mk_id", store.get("mk_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}	
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
		mrpkind=baseDao.getCountByCondition("MakeKind", "mk_ifmrpkind<>0");
		if (mrpkind==0) {
			BaseUtil.showErrorOnSuccess("必须设置一个类型是默认的MRP投放类型");
		} 
	}

	@Override
	public void updateMakeKindById(String formStore, String caller) { 
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//类型名称重复时，限制保存并提示；		
		Object precode = baseDao.getFieldDataByCondition("MakeKind", "mk_id", "mk_id<>'" + store.get("mk_id") + "' AND mk_name='" + store.get("mk_name") + "'");
		if (precode != null) {
			BaseUtil.showError("类型名称重复,不允许更新");
		}	
		int mrpkind=baseDao.getCountByCondition("MakeKind", "mk_id<>"+store.get("mk_id") +" and mk_ifmrpkind<>0 and mk_makind='"+ store.get("mk_makind") +"'");
		if (mrpkind>0) { 
			if (store.containsKey("mk_ifmrpkind") && !store.get("mk_ifmrpkind").toString().equals("0") && !store.get("mk_ifmrpkind").toString().equals("") ){
				BaseUtil.showError("制造或委外工单只能有一个类型设置为默认的MRP投放类型");
			} 
		} 
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller,new Object[]{store});		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MakeKind", "mk_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "mk_id", store.get("mk_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller,new Object[]{store});
		//类型名称重复时，限制保存并提示；		 
		mrpkind=baseDao.getCountByCondition("MakeKind", "mk_ifmrpkind<>0");
		if (mrpkind==0) {
			BaseUtil.showErrorOnSuccess("必须设置一个类型是默认的MRP投放类型");
		} 
	}

	@Override
	public void deleteMakeKind(int mk_id, String caller) {
		//类型被使用时，限制删除并提示；				
		String SQLStr = "";
		SqlRowList rs; 
		SQLStr="select count(0) cn from make left join MakeKind on  mk_name=ma_kind where mk_id='"+mk_id+"' and ma_statuscode='AUDITED'";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) { 
			if (rs.getInt("cn")>0){
				BaseUtil.showError("类型被使用时限制删除"); 
			}
		} 		
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{mk_id});
		//删除BOM
		baseDao.deleteById("MakeKind", "mk_id", mk_id);		
		//记录操作
		baseDao.logger.delete(caller, "mk_id", mk_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{mk_id});

	}

}
