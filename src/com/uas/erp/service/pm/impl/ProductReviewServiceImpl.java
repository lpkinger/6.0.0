package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.ProductReviewService;

@Service("productReviewService")
public class ProductReviewServiceImpl implements ProductReviewService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProductReview(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ProductReview",
				"pv_code='" + store.get("pv_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_bocodeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存ProductReview
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "ProductReview"));
		baseDao.execute(SqlUtil
				.getInsertSqlbyList(grid, "ProductReviewDetail", "pvd_id"));
		// 记录操作
		baseDao.logger.save(caller, "pv_id", store.get("pv_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void deleteProductReview(int pv_id, String caller) {
		// 只能删除在录入的采购单!
		Object status = baseDao.getFieldDataByCondition("ProductReview",
				"pv_statuscode", "pv_id=" + pv_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { pv_id });
		// 删除NeedFeature
		baseDao.deleteByCondition("NeedFeature", "nf_id in (select nf_id from NeedFeature where"
			+ " nf_pvdid in (select pvd_id from ProductReviewdetail where pvd_pvid = '" + pv_id + "'))");
		// 删除ProductReview
		baseDao.deleteById("ProductReview", "pv_id", pv_id);
		// 删除purchaseDetail
		baseDao.deleteById("ProductReviewdetail", "pvd_pvid", pv_id);
		// 记录操作
		baseDao.logger.delete(caller, "pv_id", pv_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { pv_id });
	}

	@Override
	public void updateProductReviewById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的单据!
		Object status = baseDao.getFieldDataByCondition("ProductReview",
				"pv_statuscode", "pv_id=" + store.get("pv_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 修改ProductReview
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "ProductReview", "pv_id"));
		// 修改ProductReviewDetail
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "ProductReviewDetail", "pvd_id"));
		// 记录操作
		baseDao.logger.update(caller, "pv_id", store.get("pv_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void auditProductReview(int pv_id, String caller) {
		// 只能对状态为[已提交]的单据进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ProductReview",
				"pv_statuscode", "pv_id=" + pv_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { pv_id });
		// 执行审核操作
		baseDao.audit("ProductReview", "pv_id=" + pv_id, "pv_status", "pv_statuscode",
				"pv_auditdate", "pv_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "pv_id", pv_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { pv_id });
	}

	@Override
	public void resAuditProductReview(int pv_id, String caller) {
		// 只能对状态为[已审核]的单据进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ProductReview",
				"pv_statuscode", "pv_id=" + pv_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("ProductReview", "pv_id=" + pv_id, "pv_status",
				"pv_statuscode", "pv_auditdate", "pv_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "pv_id", pv_id);
	}

	@Override
	public void submitProductReview(int pv_id, String caller) {
		// 只能对状态为[在录入]的单据进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ProductReview",
				"pv_statuscode", "pv_id=" + pv_id);
		StateAssert.submitOnlyEntering(status);
		checkProd(pv_id, caller);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { pv_id });
		// 执行提交操作
		baseDao.submit("ProductReview", "pv_id=" + pv_id, "pv_status", "pv_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pv_id", pv_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { pv_id });
	}
	
	private void checkProd(int pv_id, String caller) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pvd_detno) from ProductReviewDetail,ProductReview where pvd_pvid=pv_id and pvd_pvid=? and nvl(pvd_prodcode,' ')=' ' and nvl(pv_itemtype,' ')='SELF'",
						String.class, pv_id);
		if (dets != null) {
			if (baseDao.isDBSetting(caller, "allowProdNull"))
				BaseUtil.appendError("当前项目类型物料编号为空！行号：" + dets);
			else
				BaseUtil.showError("当前项目类型物料编号为空！行号：" + dets);
		}
	}

	@Override
	public void resSubmitProductReview(int pv_id, String caller) {
		// 只能对状态为[已提交]的单据进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ProductReview",
				"pv_statuscode", "pv_id=" + pv_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { pv_id });
		// 执行反提交操作
		baseDao.resOperate("ProductReview", "pv_id=" + pv_id, "pv_status",
				"pv_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pv_id", pv_id);
		handlerService.afterResSubmit(caller, new Object[] { pv_id });
	}

	@Override
	public void setNeedSpec(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		int pvd_id = Integer.valueOf(store.get("pvd_id").toString());
		Object status = baseDao.getFieldDataByCondition("ProductReviewDetail left join ProductReview on pvd_pvid=pv_id",
				"pv_statuscode", "pvd_id=" + pvd_id);
		StateAssert.updateOnlyEntering(status);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "NeedFeature", "nf_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("nf_id") == null || s.get("nf_id").equals("") || s.get("nf_id").equals("0")
					|| Integer.parseInt(s.get("nf_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("NEEDFEATURE_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "NeedFeature", new String[] { "nf_id"}, new Object[] { id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute("update ProductREVIEWDetail set pvd_needspec=(select wm_concat(nf_fename||'|'||nf_valuename) from (select nf_fename,nf_valuename from NeedFeature where nf_pvdid=? and nvl(nf_fecode,' ')<>' ' and nvl(nf_valuecode,' ')<>' ')) where pvd_id=?", pvd_id, pvd_id);
		baseDao.execute("update ProductREVIEWDetail set pvd_isstandard=-1 where pvd_id=? and nvl(pvd_ftcode,' ')<>' ' and pvd_needspec=(select fp_description2 from FeatureProduct where pvd_ftcode=fp_code)", pvd_id);
		baseDao.execute("update ProductREVIEWDetail set pvd_isstandard=0 where pvd_id=? and nvl(pvd_ftcode,' ')<>' ' and pvd_needspec <> (select fp_description2 from FeatureProduct where pvd_ftcode=fp_code)", pvd_id);
	}
	
	@Override
	public void deleteNeedSpec(int pvd_id, String caller) {
		// 只能删除在录入的采购单!
		Object status = baseDao.getFieldDataByCondition("ProductReviewDetail left join ProductReview on pvd_pvid=pv_id",
				"pv_statuscode", "pvd_id=" + pvd_id);
		StateAssert.delOnlyEntering(status);
		// 删除
		baseDao.deleteById("NeedFeature", "nf_pvdid", pvd_id);
		baseDao.execute("update ProductREVIEWDetail set pvd_needspec=null where pvd_id=" + pvd_id);
		baseDao.execute("update ProductREVIEWDetail set pvd_isstandard=0 where pvd_id=" + pvd_id);
	}
}
