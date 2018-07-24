package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.ProductUnitService;

@Service("productUnitService")
public class ProductUnitServiceImpl implements ProductUnitService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void updateProductById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);	
		//执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store, gstore});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Product", "pr_id");
		baseDao.execute(formSql);
		//修改ProductUnit
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ProductUnit", "pu_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("pu_id") == null || s.get("pu_id").equals("") || s.get("pu_id").equals("0") ||
					Integer.parseInt(s.get("pu_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("PRODUCTUNIT_SEQ");
				s.put("pu_prid",  store.get("pr_id"));
				String sql = SqlUtil.getInsertSqlByMap(s, "ProductUnit", new String[]{"pu_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "pr_id", store.get("pr_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store, gstore});
	}
}
