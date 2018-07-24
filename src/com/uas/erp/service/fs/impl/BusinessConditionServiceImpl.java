package com.uas.erp.service.fs.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fs.BusinessConditionService;

@Service
public class BusinessConditionServiceImpl implements BusinessConditionService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveBusinessCondition(String formStore, String caller, String param1, String param2, String param3, String param4,
			String param5) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object cq_id = store.get("bc_id");
		// 只能对状态为[在录入]的表单进行资料完善!
		Object status = baseDao.getFieldDataByCondition("CustomerQuota", "cq_statuscode", "cq_id=" + cq_id);
		if (!"ENTERING".equals(status)) {
			BaseUtil.showError("客户额度申请不是【在录入】状态，不允许进行资料完善！");
		}
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param1);
		List<Map<Object, Object>> grid2 = BaseUtil.parseGridStoreToMaps(param2);
		List<Map<Object, Object>> grid3 = BaseUtil.parseGridStoreToMaps(param3);
		List<Map<Object, Object>> grid4 = BaseUtil.parseGridStoreToMaps(param4);
		List<Map<Object, Object>> grid5 = BaseUtil.parseGridStoreToMaps(param5);

		handlerService.handler(caller, "save", "before", new Object[] { store, grid, grid2, grid3, grid4, grid5 });
		Object value = null;
		List<String> clobFields = new ArrayList<String>();
		List<String> clobStrs = new ArrayList<String>();
		for (Object field : store.keySet()) {
			value = store.get(field);
			if (value != null) {
				String val = value.toString();
				if (val.length() > 2000) {
					clobFields.add(field.toString());
					clobStrs.add(val);
				}
			}
		}
		boolean bool = baseDao.checkByCondition("BusinessCondition", "bc_id = " + store.get("bc_id"));
		String formSql = null;
		if (bool) {
			formSql = SqlUtil.getInsertSqlByMap(store, "BusinessCondition");
		} else {
			formSql = SqlUtil.getUpdateSqlByFormStore(store, "BusinessCondition", "bc_id");
		}
		baseDao.execute(formSql);
		if (clobFields.size()>0) {
			baseDao.saveClob("BusinessCondition", clobFields, clobStrs, "bc_id=" + store.get("bc_id"));
		}
		List<String> gridSql = new ArrayList<String>();
		// 更新BC_ProductMix
		if (param1 != null && !"".equals(param1)) {
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid, "BC_ProductMix", "pm_id"));
		}

		// 更新BC_UPDOWNCUST
		if (param2 != null && !"".equals(param2)) {
			for (Map<Object, Object> m : grid2) {
				m.put("udc_kind", "上游");
			}
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid2, "BC_UPDOWNCUST", "udc_id"));
		}
		// 更新BC_UPDOWNCUST
		if (param3 != null && !"".equals(param3)) {
			for (Map<Object, Object> m : grid3) {
				m.put("udc_kind", "下游");
			}
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid3, "BC_UPDOWNCUST", "udc_id"));
		}
		// 更新BC_ProposedFinance
		if (param4 != null && !"".equals(param4)) {
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid4, "BC_ProposedFinance", "pf_id"));
		}
		// 更新BC_YearDeal
		if (param5 != null && !"".equals(param5)) {
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid5, "BC_YearDeal", "yd_id"));
		}
		baseDao.execute(gridSql);

		// 记录日志
		baseDao.logger.save(caller, "bc_id", store.get("bc_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, grid, grid2, grid3, grid4, grid5 });
	}

}
