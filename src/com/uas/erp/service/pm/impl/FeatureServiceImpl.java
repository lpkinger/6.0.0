package com.uas.erp.service.pm.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.FeatureService;

@Service
public class FeatureServiceImpl implements FeatureService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveFeature(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave("Feature",new Object[] { store, grid});
		int count = baseDao.getCountByCondition("Feature", "fe_name='" + store.get("fe_name") +"' and fe_id<>" + store.get("fe_id"));
		if(count > 0){
			BaseUtil.showError("特征名称[" + store.get("fe_name") + "]已经存在！");
		}
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Feature", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		Object[] id = new Object[grid.size()];
		for (int i = 0; i < grid.size(); i++) {
			id[i] = baseDao.getSeqId("FEATUREDETAIL_SEQ");
			grid.get(i).put("fd_id", id[i]);
			grid.get(i).put("fd_value", grid.get(i).get("fd_value").toString().trim());
			grid.get(i).put("fd_feid", store.get("fe_id"));
			grid.get(i).put("fd_code", store.get("fe_code"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "Featuredetail");
		baseDao.execute(gridSql);
		baseDao.execute("update FeatureDetail set fd_value=rtrim(ltrim(replace(fd_value,'  ',' ')))  where fd_feid=" + store.get("fe_id"));
		// 记录操作
		baseDao.logger.save(caller, "fe_id", store.get("fe_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave("Feature",new Object[] { store, grid});
	}

	@Override
	public void updateFeatureById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行修改前的其它逻辑
		handlerService.beforeSave("Feature",new Object[] { store, grid });
		int count = baseDao.getCountByCondition("Feature", "fe_name='" + store.get("fe_name") +"' and fe_id<>" + store.get("fe_id"));
		if(count > 0){
			BaseUtil.showError("特征名称[" + store.get("fe_name") + "]已经存在！");
		}
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Feature", "fe_id");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(grid, "Featuredetail", "fd_id");
		for (Map<Object, Object> s : grid) {
			if (s.get("fd_id") == null || s.get("fd_id").equals("") || s.get("fd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("FEATUREDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "Featuredetail",
						new String[] { "fd_id", "fd_code", "fd_feid" },
						new Object[] { id, store.get("fe_code"), store.get("fe_id") });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute("update FeatureDetail set fd_value=rtrim(ltrim(replace(fd_value,'  ',' ')))  where fd_feid="
				+ store.get("fe_id"));
		baseDao.execute("begin for rec in (select fd_valuecode, fd_code, fd_value from featuredetail) loop "
				+ "update FeatureProductDetail set fpd_fevalue = rec.fd_value "
				+ "where fpd_fecode = rec.fd_code and fpd_fevaluecode = rec.fd_valuecode and fpd_fevalue <>rec.fd_value and fpd_fecode='"
				+ store.get("fe_code") + "';" + "end loop;end;");
		baseDao.logger.save(caller, "fe_id", store.get("fe_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave("Feature",new Object[] { store, grid });
	}

	@Override
	public void deleteFeature(int fe_id, String caller) {

		String fecode = (String) baseDao.getFieldDataByCondition("Feature", "fe_code", "fe_id=" + fe_id);
		List<Object[]> list = baseDao.getFieldsDatasByCondition("ProdFeature", new String[] { "pf_id", "pf_prodcode" },
				"pf_fecode='" + fecode + "'");
		if (list != null && list.size() > 0) {
			BaseUtil.showError("已经被物料[" + list.get(0)[1] + "]使用，特征项不能删除");
		}
		handlerService.beforeDel("Feature",new Object[] { fe_id});
		baseDao.deleteById("Feature", "fe_id", fe_id);

		baseDao.deleteById("Featuredetail", "fd_feid", fe_id);

		baseDao.logger.delete(caller, "fe_id", fe_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel("Feature",new Object[] { fe_id});

	}

	@Override
	public void auditFeature(int fe_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Feature", "fe_statuscode", "fe_id=" + fe_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit("Feature",new Object[] { fe_id});		// 执行审核操作
		baseDao.audit("Feature", "fe_id="+ fe_id, "fe_status", "fe_statuscode", "fe_auditdate", "fe_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "fe_id",fe_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit("Feature",new Object[] { fe_id});
	}

	@Override
	public void resAuditFeature(int fe_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Feature", "fe_statuscode", "fe_id=" + fe_id);
		StateAssert.resAuditOnlyAudit(status);
		SqlRowList rs = baseDao
				.queryForRowSet("select pr_code from Product left join prodfeature on pf_prodcode=pr_refno left join feature on fe_code=pf_fecode where fe_id='"
						+ fe_id + "' ");
		if (rs.next()) {
			BaseUtil.showError("已经产生实体料号[" + rs.getString("pr_code") + "],不能反审核");
		}
		// 执行反审核操作
		baseDao.updateByCondition("Feature",
				"fe_statuscode='ENTERING',fe_status='" + BaseUtil.getLocalMessage("ENTERING") + "'", "fe_id="
						+ fe_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "fe_id",fe_id);
	}

	@Override
	public void submitFeature(int fe_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Feature", "fe_statuscode", "fe_id=" + fe_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit("Feature", new Object[] { fe_id});
		// 执行提交操作
		baseDao.submit("Feature", "fe_id="+ fe_id, "fe_status", "fe_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "fe_id", fe_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit("Feature", new Object[] { fe_id});
	}

	@Override
	public void resSubmitFeature(int fe_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Feature", "fe_statuscode", "fe_id=" + fe_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit("Feature", new Object[] { fe_id});		// 执行反提交操作
		baseDao.updateByCondition("Feature",
				"fe_statuscode='ENTERING',fe_status='" + BaseUtil.getLocalMessage("ENTERING") + "'", "fe_id="
						+ fe_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "fe_id", fe_id);
		handlerService.afterResSubmit("Feature", new Object[] { fe_id});
	}

	@Override
	public int updateFeatureNameById(int id, String name, String caller) {
		int flag = baseDao.getCountByCondition("Feature", "fe_name='" + name + "'");
		if (flag != 0) {
			return 1;
		} else {
			baseDao.updateByCondition("Feature", "fe_name='" + name + "'", "fe_id=" + id);
			return 0;
		}
	}

	@Override
	public void addFeatureDetail(String param, String caller) {
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);
		Object[] id = new Object[grid.size()];
		int maxdetno = 0;
		for (int i = 0; i < grid.size(); i++) {
			if (i == 0) {
				maxdetno = Integer.parseInt(baseDao.getFieldDataByCondition("Featuredetail", "NVL(max(fd_detno),0)",
						"fd_code='" + grid.get(i).get("fd_code") + "'").toString());
			}
			maxdetno = maxdetno + 1;
			id[i] = baseDao.getSeqId("FEATUREDETAIL_SEQ");
			grid.get(i).put("fd_id", id[i]);
			grid.get(i).remove("fd_detno");
			grid.get(i).put("fd_detno", maxdetno);
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "Featuredetail");
		baseDao.execute(gridSql);

	}

	@Override
	public void addFeatureRelation(String param, String caller) {
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);
		Object[] id = new Object[grid.size()];
		for (int i = 0; i < grid.size(); i++) {
			id[i] = baseDao.getSeqId("FEATURERELATION_SEQ");
			grid.get(i).put("fr_id", id[i]);
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "FeatureRelation");
		baseDao.execute(gridSql);

	}

	@Override
	public void updateRemark(int id, String remark, String caller) {
		baseDao.updateByCondition("FeatureDetail", "fd_remark='" + remark + "'", "fd_id=" + id);
		baseDao.logger.others("修改明细备注", "更新成功", caller, "fd_id", id);
	}

	@Override
	public Object checkName(String name, String caller) {
		int flag = baseDao.getCountByCondition("Feature", "fe_name='" + name + "'");
		if (flag != 0) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public void updateDetailStatus(int id, String status, String caller) {
		baseDao.updateByCondition("FeatureDetail", "fd_status='" + status + "'", "fd_id=" + id);
	}

	@Override
	public void updateByCondition(String tablename, String condition, String update, String caller) {
		baseDao.updateByCondition(tablename, update, condition);

	}

	@Override
	public void bannedDetails(int id, String caller) {
		baseDao.updateByCondition("FeatureDetail",
				"fd_statuscode='DISABLE',fd_status='" + BaseUtil.getLocalMessage("DISABLE") + "',fd_log='"
						+ SystemSession.getUser().getEm_name() + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())
						+ "禁用'", "fd_id="
						+ id);
	}

}
