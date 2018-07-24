package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.SaleRemarkService;

@Service("SaleRemarkService")
public class SaleRemarkServiceImpl implements SaleRemarkService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveSaleRemark(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		//执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store, grid});
		//保存SaleRemark
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "SaleRemark", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//保存SaleRemarkDetail
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "SaleRemarkDetail", "srd_id");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save(caller, "sr_id", store.get("sr_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store, grid});
	}
	@Override
	public void deleteSaleRemark(int sr_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{sr_id});
		//删除SaleRemark
		baseDao.deleteById("SaleRemark", "sr_id", sr_id);
		//删除SaleRemarkDetail
		baseDao.deleteById("SaleRemarkdetail", "srd_srid", sr_id);
		//记录操作
		baseDao.logger.delete(caller, "sr_id", sr_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{sr_id});
	}
	
	@Override
	public void updateSaleRemarkById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store, gstore});
		//修改SaleRemark
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "SaleRemark", "sr_id");
		baseDao.execute(formSql);
		//修改SaleRemarkDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "SaleRemarkDetail", "srd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("srd_id") == null || s.get("srd_id").equals("") || s.get("srd_id").equals("0") ||
					Integer.parseInt(s.get("srd_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("SALEREMARKDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "SaleRemarkDetail", new String[]{"srd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "sr_id", store.get("sr_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store, gstore});
	}
}
