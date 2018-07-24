package com.uas.erp.service.drp.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;


import com.uas.erp.service.drp.ModelContrastService;
@Service
public class ModelContrastServiceImpl implements ModelContrastService{
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	@Override
	public void updateModelContrast(String formStore, String gridStore,
			 String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[]{store,gstore});
		//修改ModelContrast
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ModelContrast", "mc_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("mc_id") == null || s.get("mc_id").equals("") || s.get("mc_id").toString().equals("0")
					){//新添加的数据，id不存在
				s.put("mc_prcode", store.get("pr_code"));
				s.put("mc_prname", store.get("pr_detail"));
				s.put("mc_sendstatus", "待上传");
				int id = baseDao.getSeqId("ModelContrast_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ModelContrast", new String[]{"mc_id"},
                        new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "mc_id", store.get("mc_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store,gstore});
		
	}

}
