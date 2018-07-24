package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.VendorProductService;

@Service("vendorProductService")
public class VendorProductServiceImpl implements VendorProductService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void updateVendorProductById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行修改前的其它逻辑
		handlerService.handler("Vendor!ProductVendor", "save", "before", new Object[] { store, gstore });
		// 修改VendorProduct
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ProductVendor", "pv_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("pv_id") == null || s.get("pv_id").equals("") || s.get("pv_id").equals("0")
					|| Integer.parseInt(s.get("pv_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("PRODUCTVENDOR_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ProductVendor", new String[] { "pv_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute("update productvendor set pv_prodcode=(select pr_code from product where pv_prodid=pr_id) where pv_vendid=" + store.get("ve_id"));
		baseDao.execute("update productvendor set (pv_vendcode,pv_vendname)=(select ve_code,ve_name from vendor where ve_id=pv_vendid) where pv_vendid=" + store.get("ve_id"));
		// 记录操作
		baseDao.logger.update(caller, "ve_id", store.get("ve_id"));
		// 执行修改后的其它逻辑
		handlerService.handler("Vendor!ProductVendor", "save", "after", new Object[] { store, gstore });
	}
}
