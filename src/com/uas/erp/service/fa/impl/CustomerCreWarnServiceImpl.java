package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.fa.CustomerCreWarnService;

@Service
public class CustomerCreWarnServiceImpl implements CustomerCreWarnService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCustomerCreWarn(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 保存CustomerCreWarn
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Customer",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存CustMonth
		Object[] cm_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			cm_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				cm_id[i] = baseDao.getSeqId("CustMonth_SEQ");
			}
		} else {
			cm_id[0] = baseDao.getSeqId("CustMonth_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore,
				"CustMonth", "cm_id", cm_id);
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "cu_id", store.get("cu_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void updateCustomerCreWarnById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的采购单资料!
		/*
		 * Object status = baseDao.getFieldDataByCondition("Customer",
		 * "cu_statuscode", "cu_id=" + store.get("cu_id"));
		 * if(!status.equals("ENTERING")){
		 * BaseUtil.showError(BaseUtil.getLocalMessage
		 * ("fa.ars.Customer.update_onlyEntering", language)); }
		 */
		// 更新采购计划下达数\本次下达数\状态
		// CustomerCreWarnDao.updateCustomerCreWarnPlan(Integer.parseInt((String)store.get("cu_id")));
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改CustomerCreWarn
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Customer",
				"cu_id");
		baseDao.execute(formSql);
		// 修改CustMonth
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"CustMonth", "cm_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("cm_id") == null || s.get("cm_id").equals("")
					|| s.get("cm_id").equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("CustMonth_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "CustMonth",
						new String[] { "cm_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "cu_id", store.get("cu_id"));
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
		// 更新上次采购价格、供应商
		// CustomerCreWarnDao.updatePreCustomerCreWarn((String)store.get("ab_code"),
		// (String)store.get("ab_date"));
		// 执行修改后的其它逻辑
		/*
		 * handlerService.handler("CustomerCreWarn", "save", "after", new
		 * Object[]{formStore, language});
		 */

	}

	@Override
	public void deleteCustomerCreWarn(int cu_id, String caller) {
		/*
		 * Object status = baseDao.getFieldDataByCondition("Customer",
		 * "ab_statuscode", "cu_id=" + cu_id); if(!status.equals("ENTERING")){
		 * BaseUtil.showError(BaseUtil.getLocalMessage(
		 * "scm.Customer.Customer.delete_onlyEntering", language)); }
		 */
		/*
		 * //执行删除前的其它逻辑 handlerService.handler("Customer", "delete", "before",
		 * new Object[]{cu_id, language, employee});
		 */
		handlerService.beforeDel(caller, cu_id);
		// 删除Customer
		baseDao.deleteById("Customer", "cu_id", cu_id);
		// 删除CustMonth
		baseDao.deleteById("CustMonth", "cm_custid", cu_id);
		// 记录操作
		baseDao.logger.delete(caller, "cu_id", cu_id);
		handlerService.afterDel(caller, cu_id);
		/*
		 * //执行删除后的其它逻辑 handlerService.handler("Customer", "delete", "after",
		 * new Object[]{cu_id, language, employee});
		 */

	}

	@Override
	public void printCustomerCreWarn(int cu_id, String caller) {

	}

	@Override
	public void auditCustomerCreWarn(int cu_id, String caller) {

	}

	@Override
	public void resAuditCustomerCreWarn(int cu_id, String caller) {

	}

	@Override
	public void submitCustomerCreWarn(int cu_id, String caller) {

	}

	@Override
	public void resSubmitCustomerCreWarn(int cu_id, String caller) {

	}

}
