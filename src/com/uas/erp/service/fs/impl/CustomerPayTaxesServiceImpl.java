package com.uas.erp.service.fs.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fs.CustomerPayTaxesService;

@Service
public class CustomerPayTaxesServiceImpl implements CustomerPayTaxesService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void updateCustomerPayTaxesById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);

		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });

		// 修改ProductCustomer
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "CustomerPayTaxes", "ct_id");
		for (Map<Object, Object> s : gstore) {
			s.put("ct_updatedate", DateUtil.getCurrentDate());
			if (s.get("ct_id") == null || s.get("ct_id").equals("") || s.get("ct_id").equals("0")
					|| Integer.parseInt(s.get("ct_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("CUSTOMERPAYTAXES_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "CustomerPayTaxes", new String[] { "ct_id" }, new Object[] { id });
				gridSql.add(sql);
			}else {
				gridSql.add(SqlUtil.getUpdateSqlByFormStore(s, "CustomerPayTaxes", "ct_id"));
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "cu_id", store.get("cu_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

}
