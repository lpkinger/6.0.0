package com.uas.erp.service.crm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.crm.CustomerDistrService;

@Service
public class CustomerDistrServiceImpl implements CustomerDistrService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCustomerDistr(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		for (Map<Object, Object> s : gstore) {
			s.put("cd_id", baseDao.getSeqId("CustomerDistr_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertOrUpdateSql(gstore,
				"CustomerDistr", "cd_id");
		gridSql.add("update CustomerDistr set cd_custcode=(select cu_code from customer where cu_id=cd_cuid) where cd_cuid="
				+ store.get("cu_id"));
		String check = baseDao
				.executeWithCheck(
						gridSql,
						null,
						"select wm_concat(cd_sellercode) from  CustomerDistr where cd_cuid="
								+ store.get("cu_id")
								+ "  group  by  cd_sellercode  having  count(cd_sellercode) > 1");
		if (check != null) {
			BaseUtil.showError("明细行业务员编号重复");
		}
		try {
			// 记录操作
			baseDao.logger.save(caller, "cu_id", store.get("cu_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteCustomerDistr(int cu_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, cu_id);
		// 删除purchase
		baseDao.deleteById("CustomerDistr", "cd_cuid", cu_id);
		// 记录操作
		baseDao.logger.delete(caller, "cu_id", cu_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, cu_id);
	}

	@Override
	public void updateCustomerDistr(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		List<String> gridSql = SqlUtil.getInsertOrUpdateSql(gstore,
				"CustomerDistr", "cd_id");
		gridSql.add("update CustomerDistr set cd_custcode=(select cu_code from customer where cu_id=cd_cuid) where cd_cuid="
				+ store.get("cu_id"));
		// 明细行业务员编号重复限制更新
		String check = baseDao
				.executeWithCheck(
						gridSql,
						null,
						"select wm_concat(cd_sellercode) from  CustomerDistr where cd_cuid="
								+ store.get("cu_id")
								+ "  group  by  cd_sellercode  having  count(cd_sellercode) > 1");
		if (check != null) {
			BaseUtil.showError("明细行业务员编号重复");
		}
		Object[] data = baseDao.getFieldsDataByCondition("CustomerDistr", new String[]{"cd_sellercode","cd_seller","cd_id"}, 
				"cd_cuid="+store.get("cu_id")+" and cd_remark='是'");
		if(data!=null && data.length>0) {
			baseDao.execute("update customer set cu_sellercode=?,cu_sellername=?,cu_sellerid=? where cu_id="+store.get("cu_id"),data);
		}
		// 记录操作
		baseDao.logger.update(caller, "cu_id", store.get("cu_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

}
