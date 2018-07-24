package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.ReplaceBOMService;


@Service("replaceBOMService")
public class ReplaceBOMServiceImpl implements ReplaceBOMService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveReplaceBOM(String formStore, String gridStore, String   caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller,new Object[]{store,gstore});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BOMDetail", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		////保存Detail
		Object[] pre_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			pre_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				pre_id[i] = baseDao.getSeqId("PRODREPLACE_SEQ");
			}
		} else {
			pre_id[0] = baseDao.getSeqId("PRODREPLACE_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "ProdReplace", "pre_id", pre_id);
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "bd_id", store.get("bd_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller,new Object[]{store,gstore});
	}
	
	@Override
	public void deleteReplaceBOM(int bd_id, String   caller) {		
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{bd_id});
		//删除
		baseDao.deleteById("BOMDetail", "bd_id", bd_id);
		//删除明细
		baseDao.deleteById("PRODREPLACE", "pre_bdid", bd_id);
		//记录操作
		baseDao.logger.delete(caller, "bd_id", bd_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{bd_id});
	}
	
	@Override
	public void updateReplaceBOMById(String formStore, String gridStore, String   caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller,new Object[]{store,gstore});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BOMDetail", "bd_id");
		baseDao.execute(formSql);
		//修改Detail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ProdReplace", "pre_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("pre_id") == null || s.get("pre_id").equals("") || s.get("pre_id").equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("PRODREPLACE_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ProdReplace", new String[]{"pre_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "bd_id", store.get("bd_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller,new Object[]{store,gstore});
	}
}
