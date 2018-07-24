package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.ProductVendorCheckService;
@Service
public class ProductVendorCheckServiceImpl implements ProductVendorCheckService{
	
	@Autowired
	BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProductVendorCheckById(String formStore, String gridStore, String caller) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//执行修改前的其它逻辑;*/
		handlerService.handler(caller, "save", "before", new Object[]{gstore.get(0)});
		//记录操作
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "PRODUCTVENDOR", "pv_vendcode");
		for(Map<Object, Object> s:gstore){
			if(s.get("pv_id") == null || s.get("pv_id").equals("") || s.get("pv_id").equals("0") ||
					Integer.parseInt(s.get("pv_id").toString()) == 0||s.get("pv_vendcode").equals("")){//新添加的数据，id不存在
			}else{
				String sql = SqlUtil.getInsertSqlByMap(s, "PRODUCTVENDOR", new String[]{"pv_vendcode"}, new Object[]{null});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		baseDao.logger.update(caller, "pv_id", gstore.get(0).get("pv_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{gstore.get(0)});	
	}
}
