package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.BOMDetailDocService;


@Service("BOMDetailDocService")
public class BOMDetailDocServiceImpl implements BOMDetailDocService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveBOMDetailDoc(String gridStore, String caller) {
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		Object bdid = null;
		for(Map<Object, Object> map:grid){
			map.put("bdd_id", baseDao.getSeqId("BOMDETAILDOC_SEQ"));
			bdid = map.get("bdd_bdid");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "BOMDetailDOC");
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "bd_id", bdid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void deleteBOMDetailDoc(int bd_id, String caller) {	
		//执行删除前的其它逻辑
		handlerService.beforeDel("BOMDetailDoc",new Object[]{bd_id});
		/*//删除
		baseDao.deleteById("BOMDetail", "bd_id", bd_id);*/
		//删除Detail
		baseDao.deleteById("BOMDetailDOC", "bdd_bdid", bd_id);
		//记录操作
		baseDao.logger.delete(caller, "bd_id", bd_id);
		//执行删除后的其它逻辑
		handlerService.afterDel("BOMDetailDoc", new Object[]{bd_id});
	}
	
	@Override
	public void updateBOMDetailDocById(String gridStore, String caller) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//修改Detail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "BOMDetailDOC", "bdd_id");
		Object bdid = null;
		for(Map<Object, Object> s:gstore){
			if(s.get("bdd_id") == null || s.get("bdd_id").equals("") || s.get("bdd_id").equals("0") ||
					Integer.parseInt(s.get("bdd_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("BOMDETAILDOC_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "BOMDetailDOC", new String[]{"bdd_id"}, new Object[]{id});
				gridSql.add(sql);
				bdid = s.get("bdd_bdid");
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "bd_id", bdid);
	}
}
