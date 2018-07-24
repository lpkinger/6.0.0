package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.ProductCustomerService;

@Service
public class ProductCustomerServiceImpl implements ProductCustomerService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void updateProductCustomerById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store, gstore});
		//修改ProductCustomer
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ProductCustomer", "pc_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("pc_id") == null || s.get("pc_id").equals("") || s.get("pc_id").equals("0") ||
					Integer.parseInt(s.get("pc_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("PRODUCTCUSTOMER_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ProductCustomer", new String[]{"pc_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute("update productcustomer set (pc_custcode,pc_custname)=(select cu_code,cu_name from customer where cu_id=pc_custid) where pc_prodid=" + store.get("pr_id"));
		baseDao.execute("update productcustomer set pc_prodcode=(select pr_code from product where pc_prodid=pr_id) where pc_prodid=" + store.get("pr_id"));
		//记录操作
		baseDao.logger.update(caller, "pr_id", store.get("pr_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store, gstore});
	}
}
