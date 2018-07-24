package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.BOMDetailLocationService;


@Service("BOMDetailLocationService")
public class BOMDetailLocationServiceImpl implements BOMDetailLocationService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveBOMDetailLocation( String gridStore, String caller,String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		Object bdid = null;
		for(Map<Object, Object> map:grid){
			map.put("bdl_id", baseDao.getSeqId("BOMDETAILLOCATION_SEQ"));
			bdid = store.get("bd_id");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "BOMDetailLocation");
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "bd_id", bdid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void deleteBOMDetailLocation(int bd_id, String caller) {	
		//执行删除前的其它逻辑
		handlerService.beforeDel("BOMDetailLocation",new Object[]{bd_id});
		/*//删除
		baseDao.deleteById("BOMDetail", "bd_id", bd_id);*/
		//删除Detail
		baseDao.deleteById("BOMDetailLocation", "bdl_bdid", bd_id);
		//记录操作
		baseDao.logger.delete(caller,"bd_id", bd_id);
		//执行删除后的其它逻辑
		handlerService.afterDel("BOMDetailLocation",new Object[]{bd_id});
	}
	
	@Override
	public void updateBOMDetailLocationById( String gridStore, String caller,String formStore) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//修改Detail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "BOMDetailLocation", "bdl_id");
		Object bdid = null;
		for(Map<Object, Object> s:gstore){
			if(s.get("bdl_id") == null || s.get("bdl_id").equals("") || s.get("bdl_id").equals("0") ||
					Integer.parseInt(s.get("bdl_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("BOMDETAILLOCATION_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "BOMDetailLocation", new String[]{"bdl_id"}, new Object[]{id});
				gridSql.add(sql);
				bdid = s.get("bdl_bdid");
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "bd_id", bdid);
	}
}
