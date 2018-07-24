package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;


import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.ProdKindService;
@Service("prodKindService")
public class ProdKindServiceImpl implements ProdKindService {
	@Autowired
	BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void updateProdKindById(String formStore, String gridStore,
			String language) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//执行修改前的其它逻辑;*/
		/*handlerService.handler("ParaSetup", "save", "before", new Object[]{gstore.get(0), language});*/
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ProdKind", "pk_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("pk_id") == null || s.get("pk_id").equals("") || s.get("pk_id").equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("ProdKind_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ProdKind", new String[]{"pk_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.update", language), 
				BaseUtil.getLocalMessage("msg.updateSuccess", language), "ProdKind|pk_id=" + gstore.get(0).get("pk_id")));
		//执行修改后的其它逻辑
		/*handlerService.handler("ParaSetup", "save", "after", new Object[]{gstore.get(0), language});*/
		
		
		
		
	}

}
