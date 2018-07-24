package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.CommonReplaceService;

@Service
public class CommonReplaceServiceImpl implements CommonReplaceService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveCommonReplaceService(String gridStore, String caller) {
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore); 
		for(Map<Object, Object> map:grid){
			map.put("pre_id", baseDao.getSeqId("PRODREPLACE_SEQ")); 
		}
		handlerService.beforeSave(caller,new Object[]{grid});
		Object pre_soncodeid = grid.get(0).get("pre_soncodeid");
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "ProdReplace");
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "pre_soncodeid", pre_soncodeid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller,new Object[]{grid});
	}

	@Override
	public void updateCommonReplaceServiceById(String formStore,String gridStore,
			String caller) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeUpdate(caller,new Object[]{store,gstore});
		//修改Detail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "ProdReplace", "pre_id");
		Object pre_soncodeid = gstore.get(0).get("pre_soncodeid");
		for(Map<Object, Object> s:gstore){
			if(s.get("pre_id") == null || s.get("pre_id").equals("") || s.get("pre_id").equals("0") ||
					Integer.parseInt(s.get("pre_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("PRODREPLACE_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ProdReplace", new String[]{"pre_id"}, new Object[]{id});
				gridSql.add(sql); 
			}
		} 
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "pre_soncodeid", pre_soncodeid);
		handlerService.afterUpdate(caller,new Object[]{store,gstore});
		
	}

	@Override
	public void deleteCommonReplaceService(int pr_id, String caller) { 
		//执行删除前的其它逻辑
		handlerService.beforeDel("ProdReplace",new Object[]{pr_id});
		//删除明细
		baseDao.deleteById("PRODREPLACE", "pre_soncodeid", pr_id);
		//记录操作
		baseDao.logger.delete(caller, "pre_soncodeid", pr_id);
		//执行删除后的其它逻辑
		handlerService.afterDel("ProdReplace",new Object[]{pr_id});	}

}
