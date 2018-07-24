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
import com.uas.erp.service.fs.AssetsLiabilitiesService;

@Service
public class AssetsLiabilitiesServiceImpl implements AssetsLiabilitiesService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveAssetsLiabilities(String formStore, String caller, String param1, String param2, String param3, String param4,
			String param5, String param6, String param7, String param8, String param9, String param10) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param1);
		List<Map<Object, Object>> grid2 = BaseUtil.parseGridStoreToMaps(param2);
		List<Map<Object, Object>> grid3 = BaseUtil.parseGridStoreToMaps(param3);
		List<Map<Object, Object>> grid4 = BaseUtil.parseGridStoreToMaps(param4);
		List<Map<Object, Object>> grid5 = BaseUtil.parseGridStoreToMaps(param5);
		List<Map<Object, Object>> grid6 = BaseUtil.parseGridStoreToMaps(param6);
		List<Map<Object, Object>> grid7 = BaseUtil.parseGridStoreToMaps(param7);
		List<Map<Object, Object>> grid8 = BaseUtil.parseGridStoreToMaps(param8);
		List<Map<Object, Object>> grid9 = BaseUtil.parseGridStoreToMaps(param9);
		List<Map<Object, Object>> grid10 = BaseUtil.parseGridStoreToMaps(param10);

		handlerService.handler(caller, "save", "before", new Object[] { store });

		boolean bool = baseDao.checkByCondition("AssetsLiabilities", "al_id = " + store.get("al_id"));
		String formSql = null;
		if (bool) {
			formSql = SqlUtil.getInsertSqlByFormStore(store, "AssetsLiabilities", new String[] {}, new Object[] {});
		} else {
			formSql = SqlUtil.getUpdateSqlByFormStore(store, "AssetsLiabilities", "al_id");
		}
		baseDao.execute(formSql);

		List<String> gridSql = new ArrayList<String>();
		// 主要客户应收账款
		if (param1 != null && !"".equals(param1)) {
			for (Map<Object, Object> m : grid) {
				m.put("ai_kind", "应收账款");
			}
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid, "AL_ACCOUNTINFOR", "ai_id"));
		}
		// 长期借款
		if (param2 != null && !"".equals(param2)) {
			for (Map<Object, Object> m : grid2) {
				m.put("ai_kind", "长期借款");
			}
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid2, "AL_ACCOUNTINFOR", "ai_id"));
		}
		// 其他应收账款
		if (param3 != null && !"".equals(param3)) {
			for (Map<Object, Object> m : grid3) {
				m.put("ai_kind", "其他应收账款");
			}
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid3, "AL_ACCOUNTINFOR", "ai_id"));
		}
		// 预付账款
		if (param4 != null && !"".equals(param4)) {
			for (Map<Object, Object> m : grid4) {
				m.put("ai_kind", "预付账款");
			}
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid4, "AL_ACCOUNTINFOR", "ai_id"));
		}
		// 存货
		if (param5 != null && !"".equals(param5)) {
			for (Map<Object, Object> m : grid5) {
				m.put("ai_kind", "存货");
			}
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid5, "AL_ACCOUNTINFOR", "ai_id"));
		}
		// 固定资产
		if (param6 != null && !"".equals(param6)) {
			for (Map<Object, Object> m : grid6) {
				m.put("ai_kind", "固定资产");
			}
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid6, "AL_ACCOUNTINFOR", "ai_id"));
		}
		// 短期借款-授信银行
		if (param7 != null && !"".equals(param7)) {
			for (Map<Object, Object> m : grid7) {
				m.put("ai_kind", "短期借款-授信银行");
			}
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid7, "AL_ACCOUNTINFOR", "ai_id"));
		}
		// 短期借款-贷款银行
		if (param8 != null && !"".equals(param8)) {
			for (Map<Object, Object> m : grid8) {
				m.put("ai_kind", "短期借款-贷款银行");
			}
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid8, "AL_ACCOUNTINFOR", "ai_id"));
		}
		// 应付账款-主要债权人
		if (param9 != null && !"".equals(param9)) {
			for (Map<Object, Object> m : grid9) {
				m.put("ai_kind", "应付账款");
			}
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid9, "AL_ACCOUNTINFOR", "ai_id"));
		}
		// 其他应付账款
		if (param10 != null && !"".equals(param10)) {
			for (Map<Object, Object> m : grid10) {
				m.put("ai_kind", "其他应付账款");
			}
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid10, "AL_ACCOUNTINFOR", "ai_id"));
		}

		baseDao.execute(gridSql);
		updateAssetsLiabilities(store.get("al_id"));
		// 记录日志
		baseDao.logger.save(caller, "al_id", store.get("al_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	void updateAssetsLiabilities(Object caid) {
		baseDao.execute("update AssetsLiabilities set al_arbalance=nvl((select max(ai_billamount) from AL_ACCOUNTINFOR where ai_alid=al_id and ai_kind='应收账款'),0) where al_id="
				+ caid);
		baseDao.execute("update AssetsLiabilities set al_shortbankloan=nvl((select sum(ai_leftamount) from AL_ACCOUNTINFOR where ai_alid=al_id and ai_kind='短期借款-授信银行'),0) where al_id="
				+ caid);
		baseDao.execute("update AssetsLiabilities set al_shortdebtamount=nvl((select sum(ai_leftamount) from AL_ACCOUNTINFOR where ai_alid=al_id and ai_kind='短期借款-贷款银行'),0) where al_id="
				+ caid);
		baseDao.execute("update AssetsLiabilities set al_shortbalance=nvl(al_shortbankloan,0) + nvl(al_shortdebtamount,0) where al_id="
				+ caid);
		baseDao.execute("update AssetsLiabilities set al_longbalance=nvl((select sum(ai_leftamount) from AL_ACCOUNTINFOR where ai_alid=al_id and ai_kind='长期借款'),0) where al_id="
				+ caid);
	}

	@Override
	public void saveAccountInforDet(String gridStore) {
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		try {
			baseDao.execute(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid, "AL_ACCOUNTINFORDETAIL", "aid_id"));
		} catch (Exception e) {
			BaseUtil.showError("保存失败，错误：" + e.getMessage());
		}
	}

	@Override
	public void saveFinancCondition(String formStore, String caller, String param1, String param2, String param3, String param4,
			String param5, String param6, String param7, String param8, String param9, String param10, String param11) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object cq_id = store.get("fc_caid");
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
		List<Map<Object, Object>> grid6 = BaseUtil.parseGridStoreToMaps(param6);
		List<Map<Object, Object>> grid7 = BaseUtil.parseGridStoreToMaps(param7);
		List<Map<Object, Object>> grid8 = BaseUtil.parseGridStoreToMaps(param8);
		List<Map<Object, Object>> grid9 = BaseUtil.parseGridStoreToMaps(param9);
		List<Map<Object, Object>> grid10 = BaseUtil.parseGridStoreToMaps(param10);
		List<Map<Object, Object>> grid11 = BaseUtil.parseGridStoreToMaps(param11);
		Object id = store.get("fc_caid");
		handlerService.handler(caller, "save", "before", new Object[] { store });
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
		String sql = null;
		boolean bool = baseDao.checkByCondition("FINANCCONDITION", "fc_caid = " + store.get("fc_caid"));
		if (bool) {
			sql = SqlUtil.getInsertSqlByMap(store, "FINANCCONDITION");
		} else {
			sql = SqlUtil.getUpdateSqlByFormStore(store, "FINANCCONDITION", "fc_caid");
		}
		baseDao.execute(sql);
		if (clobFields.size()>0) {
			baseDao.saveClob("FINANCCONDITION", clobFields, clobStrs, "fc_caid" + store.get("fc_caid"));
		}
		List<String> gridSql = new ArrayList<String>();
		// 主要客户应收账款
		if (param1 != null && !"".equals(param1)) {
			for (Map<Object, Object> m : grid) {
				m.put("ai_kind", "应收账款");
			}
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid, "AL_ACCOUNTINFOR", "ai_id"));
		}
		// 长期借款
		if (param2 != null && !"".equals(param2)) {
			for (Map<Object, Object> m : grid2) {
				m.put("ai_kind", "长期借款");
			}
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid2, "AL_ACCOUNTINFOR", "ai_id"));
		}
		// 其他应收账款
		if (param3 != null && !"".equals(param3)) {
			for (Map<Object, Object> m : grid3) {
				m.put("ai_kind", "其他应收账款");
			}
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid3, "AL_ACCOUNTINFOR", "ai_id"));
		}
		// 预付账款
		if (param4 != null && !"".equals(param4)) {
			for (Map<Object, Object> m : grid4) {
				m.put("ai_kind", "预付账款");
			}
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid4, "AL_ACCOUNTINFOR", "ai_id"));
		}
		// 存货
		if (param5 != null && !"".equals(param5)) {
			for (Map<Object, Object> m : grid5) {
				m.put("ai_kind", "存货");
			}
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid5, "AL_ACCOUNTINFOR", "ai_id"));
		}
		// 固定资产
		if (param6 != null && !"".equals(param6)) {
			for (Map<Object, Object> m : grid6) {
				m.put("ai_kind", "固定资产");
			}
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid6, "AL_ACCOUNTINFOR", "ai_id"));
		}
		// 短期借款-授信银行
		if (param7 != null && !"".equals(param7)) {
			for (Map<Object, Object> m : grid7) {
				m.put("ai_kind", "短期借款");
			}
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid7, "AL_ACCOUNTINFOR", "ai_id"));
		}
		// 应付账款-主要债权人
		if (param8 != null && !"".equals(param8)) {
			for (Map<Object, Object> m : grid8) {
				m.put("ai_kind", "应付账款");
			}
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid8, "AL_ACCOUNTINFOR", "ai_id"));
		}
		// 其他应付账款
		if (param9 != null && !"".equals(param9)) {
			for (Map<Object, Object> m : grid9) {
				m.put("ai_kind", "其他应付账款");
			}
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid9, "AL_ACCOUNTINFOR", "ai_id"));
		}
		// 银行流水情况审核
		if (param10 != null && !"".equals(param10)) {
			for (Map<Object, Object> m : grid10) {
				m.put("bf_caid", id);
			}
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid10, "BANKFLOWAUDIT", "bf_id"));
		}
		// 纳税情况审核
		if (param11 != null && !"".equals(param11)) {
			for (Map<Object, Object> m : grid11) {
				m.put("pt_caid", id);
			}
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid11, "PAYTAXESAUDIT", "pt_id"));
		}

		baseDao.execute(gridSql);
		// 记录日志
		baseDao.logger.save(caller, "fc_caid", id);
		baseDao.logger.others("更新财务情况", "更新成功", "CustomerQuota", "cq_id", id);
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

}
