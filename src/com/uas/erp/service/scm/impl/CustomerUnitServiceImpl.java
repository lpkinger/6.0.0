package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.CustomerUnitService;

@Service
public class CustomerUnitServiceImpl implements CustomerUnitService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void updateCustomerUnitById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);	
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store, gstore});
		//修改ProductUnit
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "CUSTOMERSHDW", "cs_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("cs_id") == null || s.get("cs_id").equals("") || s.get("cs_id").equals("0") ||
					Integer.parseInt(s.get("cs_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("CUSTOMERSHDW_SEQ");
				s.put("cs_cuid",  store.get("cu_id"));
				String sql = SqlUtil.getInsertSqlByMap(s, "CUSTOMERSHDW", new String[]{"cs_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "cs_cuid", store.get("cu_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store, gstore});
	}

	@Override
	public int getCustomerid(String code) {
		return 0;
	}
}
