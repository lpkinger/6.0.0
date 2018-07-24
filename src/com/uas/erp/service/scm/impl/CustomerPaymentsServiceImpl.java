package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.CustomerPaymentsService;

@Service("customerPaymentsService")
public class CustomerPaymentsServiceImpl implements CustomerPaymentsService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void updateCustomerById(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行修改前的其它逻辑
		handlerService.beforeSave("CustomerPayments", new Object[] { store, gstore });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Customer", "cu_id");
		baseDao.execute(formSql);
		// 修改CustomerPayments
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "CustomerPayments", "cp_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("cp_id") == null || s.get("cp_id").equals("") || s.get("cp_id").equals("0")
					|| Integer.parseInt(s.get("cp_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("CustomerPayments_SEQ");
				s.put("cp_cuid", store.get("cu_id"));
				String sql = SqlUtil.getInsertSqlByMap(s, "CustomerPayments", new String[] { "cp_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 更新客户默认收款方式，把序号为1的更新进去.
		Object[] payment = baseDao.getFieldsDataByCondition("CustomerPayments", new String[] { "cp_paymentcode", "cp_payment" }, "cp_cuid="
				+ store.get("cu_id") + " and cp_isdefault='是'");
		if (payment != null) {
			baseDao.updateByCondition("customer", "cu_paymentscode='" + payment[0] + "', cu_payments='" + payment[1] + "'", "cu_id="
					+ store.get("cu_id"));
			if (baseDao.checkIf("Payments", "pa_code='" + payment[0] + "' and pa_creditcontrol='是' ")) {
				baseDao.updateByCondition("customer", "cu_enablecredit='是' ", "cu_id=" + store.get("cu_id"));
			}
		}
		if (baseDao.isDBSetting("Customer!Base", "creditControl")) {
			baseDao.execute("update customer set cu_enablecredit=(select pa_creditcontrol from payments where pa_code=cu_paymentscode and pa_class='收款方式') where cu_id="
					+ store.get("cu_id") + " and nvl(cu_paymentscode,' ')<>' '");
		}
		// 记录操作
		baseDao.logger.update("CustomerPayments", "cu_id", store.get("cu_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave("CustomerPayments", new Object[] { store, gstore });
	}
}
