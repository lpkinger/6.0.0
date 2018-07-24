package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.GCSService;
@Service
public class GCSServiceImpl  implements GCSService {
	
	@Autowired
	BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	
	
	public void saveGCSById(String formStore, String gridStore){
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//执行修改前的其它逻辑;
		handlerService.beforeSave("GCS", new Object[]{gstore.get(0)});
		Object[] gc_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			gc_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				gc_id[i] = baseDao.getSeqId("GCS_SEQ");
			}
		} else {
			gc_id[0] = baseDao.getSeqId("GCS_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "QUA_GCS", "gc_id", gc_id);
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.save("GCS", "gc_id", gstore.get(0).get("gc_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave("GCS", new Object[]{gstore.get(0)});
	}
}
