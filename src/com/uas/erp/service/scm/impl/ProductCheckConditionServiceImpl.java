package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.ProductCheckConditionService;

@Service
public class ProductCheckConditionServiceImpl implements ProductCheckConditionService{
	@Autowired
	BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProductCheckConditionById(String formStore, String gridStore) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//执行修改前的其它逻辑;
		handlerService.handler("Product", "save", "before", new Object[]{gstore.get(0)});
		Object[] pr_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			pr_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				pr_id[i] = baseDao.getSeqId("PRODUCT_SEQ");
			}
		} else {
			pr_id[0] = baseDao.getSeqId("PRODUCT_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "PRODUCT", "pr_id", pr_id);
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.save("Product", "pr_id", gstore.get(0).get("pr_id"));
		//执行修改后的其它逻辑
		handlerService.handler("Product", "save", "after", new Object[]{gstore.get(0)});
	}
}
