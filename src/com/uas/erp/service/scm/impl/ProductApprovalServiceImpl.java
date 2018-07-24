package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.ProductApprovalService;

@Service
public class ProductApprovalServiceImpl implements ProductApprovalService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	@Transactional
	public void saveProductApproval(String formStore, String param1, String param2, String param3, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid1 = BaseUtil.parseGridStoreToMaps(param1);
		List<Map<Object, Object>> grid2 = BaseUtil.parseGridStoreToMaps(param2);
		List<Map<Object, Object>> grid3 = BaseUtil.parseGridStoreToMaps(param3);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ProductApproval", "pa_code='" + store.get("pa_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存product
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ProductApproval", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// productApprovalDetail,pad_paid,pad_id
		for (Map<Object, Object> map : grid1) {
			map.put("pad_id", baseDao.getSeqId("productApprovalDetail_seq"));
		}
		List<String> grid1Sqls = SqlUtil.getInsertSqlbyGridStore(grid1, "productApprovalDetail");
		baseDao.execute(grid1Sqls);
		// prodApprovalDetail,prd_paid,prd_id
		for (Map<Object, Object> map : grid2) {
			map.put("prd_id", baseDao.getSeqId("prodApprovalDetail_seq"));
		}
		List<String> grid2Sqls = SqlUtil.getInsertSqlbyGridStore(grid2, "prodApprovalDetail");
		baseDao.execute(grid2Sqls);
		// prodAppDetail,ppd_paid,ppd_id
		for (Map<Object, Object> map : grid3) {
			map.put("ppd_id", baseDao.getSeqId("prodAppDetail_seq"));
		}
		List<String> grid3Sqls = SqlUtil.getInsertSqlbyGridStore(grid3, "prodAppDetail");
		baseDao.execute(grid3Sqls);
		baseDao.logger.save(caller, "pa_id", store.get("pa_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	@Transactional
	public void updateProductApprovalById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("ProductApproval", "pa_statuscode", "pa_id=" + store.get("pa_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProductApproval", "pa_id");
		baseDao.execute(formSql);
		// productApprovalDetail,pad_paid,pad_id
		/*
		 * List<Map<Object, Object>> grid1 =
		 * BaseUtil.parseGridStoreToMaps(param1); List<String> grid1Sqls =
		 * SqlUtil.getUpdateSqlbyGridStore(grid1, "productApprovalDetail",
		 * "pad_id"); for (Map<Object, Object> map : grid1) { if
		 * (map.get("pad_id") == null || map.get("pad_id").equals("") ||
		 * map.get("pad_id").toString().equals("0")) { map.put("pad_id",
		 * baseDao.getSeqId("productApprovalDetail_seq")); String sql =
		 * SqlUtil.getInsertSqlByMap(map, "productApprovalDetail");
		 * grid1Sqls.add(sql); } } baseDao.execute(grid1Sqls); //
		 * prodApprovalDetail,prd_paid,prd_id List<Map<Object, Object>> grid2 =
		 * BaseUtil.parseGridStoreToMaps(param2); List<String> grid2Sqls =
		 * SqlUtil.getUpdateSqlbyGridStore(grid2, "prodApprovalDetail",
		 * "prd_id"); for (Map<Object, Object> map : grid2) { if
		 * (map.get("prd_id") == null || map.get("prd_id").equals("") ||
		 * map.get("prd_id").toString().equals("0")) { map.put("prd_id",
		 * baseDao.getSeqId("prodApprovalDetail_seq")); String sql =
		 * SqlUtil.getInsertSqlByMap(map, "prodApprovalDetail");
		 * grid2Sqls.add(sql); } } baseDao.execute(grid2Sqls); //
		 * prodAppDetail,ppd_paid,ppd_id List<Map<Object, Object>> grid3 =
		 * BaseUtil.parseGridStoreToMaps(param3); List<String> grid3Sqls =
		 * SqlUtil.getUpdateSqlbyGridStore(grid3, "prodAppDetail", "ppd_id");
		 * for (Map<Object, Object> map : grid3) { if (map.get("ppd_id") == null
		 * || map.get("ppd_id").equals("") ||
		 * map.get("ppd_id").toString().equals("0")) { map.put("ppd_id",
		 * baseDao.getSeqId("prodAppDetail_seq")); String sql =
		 * SqlUtil.getInsertSqlByMap(map, "prodAppDetail"); grid3Sqls.add(sql);
		 * } } baseDao.execute(grid3Sqls);
		 */
		// 记录操作
		baseDao.logger.update(caller, "pa_id", store.get("pa_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteProductApproval(int pa_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("ProductApproval", "pa_statuscode", "pa_id=" + pa_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, pa_id);
		Object[] sa = baseDao.getFieldsDataByCondition("ProductApproval", new String[] { "pa_sacode", "pa_sdid" }, "pa_id=" + pa_id);
		if (sa != null && sa[0] != null && !"".equals(sa[0].toString())) {
			baseDao.execute("update Sampleapply set sa_isturn=0 where sa_code='" + sa[0] + "'");
			baseDao.execute("update SampleapplyDetail set sd_turnprostatus=null where sd_id=" + sa[1]);
		}
		// maz  认定单删除后回写送样单转认定状态为空  2017080696
		Object ss = baseDao.getFieldDataByCondition("ProductApproval", "pa_sscode", "pa_id=" + pa_id);
		if(ss != null && !"".equals(ss)){
			baseDao.execute("update SendSample set ss_approstatus='' where ss_code='"+ss+"'");
		}
		// 删除
		baseDao.deleteById("ProductApproval", "pa_id", pa_id);
		// 记录操作
		baseDao.logger.delete(caller, "pa_id", pa_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, pa_id);
	}

	@Override
	public void auditProductApproval(int pa_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ProductApproval", "pa_statuscode", "pa_id=" + pa_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, pa_id);
		// 执行审核操作
		Object pr_id = baseDao.getFieldDataByCondition("ProductApproval left join Product on pa_prodcode=pr_code", "pr_id", "pa_id="
				+ pa_id);
		// 更新物料状态
		Object appstatus = baseDao.getFieldDataByCondition("ProductApproval", "pa_finalresult", "pa_id=" + pa_id);
		if ("合格".equals(appstatus)) {
			if (pr_id != null) {
				baseDao.updateByCondition("Product", "pr_material='已认可'", "pr_id=" + pr_id);
				baseDao.execute("update product set (pr_level,PR_RDBG)=(select pa_yxdj,pa_code from productapproval where pa_id=" + pa_id
						+ ") where pr_id=" + pr_id);
			}
			// 更新价格库认可状态
			baseDao.execute("update purchasepricedetail set ppd_appstatus='合格' where (ppd_prodcode,ppd_vendcode)=(select pa_prodcode,pa_providecode from productapproval where pa_id="
					+ pa_id + ")");
		}
		baseDao.audit("ProductApproval", "pa_id=" + pa_id, "pa_status", "pa_statuscode", "pa_auditdate", "pa_auditor");
		baseDao.updateByCondition("ProductApproval", "pa_auditorid='" + SystemSession.getUser().getEm_id() + "',pa_sendstatus='待上传'",
				"pa_id=" + pa_id);
		// 记录操作
		baseDao.logger.audit(caller, "pa_id", pa_id);
		// 执行删除后的其它逻辑
		handlerService.afterAudit(caller, pa_id);
	}

	@Override
	public void resAuditProductApproval(int pa_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("ProductApproval", "pa_statuscode,pa_sendstatus", "pa_id=" + pa_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		if (status[1] != null && status[1].equals("已上传")) {
			BaseUtil.showError("已经上传到平台,不允许反审核！");
		}
		// 执行反审核操作
		baseDao.resOperate("ProductApproval", "pa_id=" + pa_id, "pa_status", "pa_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "pa_id", pa_id);
	}

	@Override
	public void submitProductApproval(int pa_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ProductApproval", "pa_statuscode", "pa_id=" + pa_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pa_id);
		// 执行提交操作
		baseDao.submit("ProductApproval", "pa_id=" + pa_id, "pa_status", "pa_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pa_id", pa_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pa_id);
	}

	@Override
	public void resSubmitProductApproval(int pa_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ProductApproval", "pa_statuscode", "pa_id=" + pa_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, pa_id);
		// 执行反提交操作
		baseDao.resOperate("ProductApproval", "pa_id=" + pa_id, "pa_status", "pa_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pa_id", pa_id);
		handlerService.afterResSubmit(caller, pa_id);
	}

	@Override
	public void saveproductApprovalDetail(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.handler(caller, "save", "before", new Object[] { store });
		// 修改ProductApproval
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProductApproval", "pa_id");
		baseDao.execute(formSql);
		// 修改productApprovalDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "productApprovalDetail", "pad_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("pad_id") == null || s.get("pad_id").equals("") || s.get("pad_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("productApprovalDetail_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "productApprovalDetail", new String[] { "pad_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "pa_id", store.get("pa_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void saveprodApprovalDetail(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.handler(caller, "save", "before", new Object[] { store });
		// 修改ProductApproval
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProductApproval", "pa_id");
		baseDao.execute(formSql);
		// 修改productApprovalDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "prodApprovalDetail", "prd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("prd_id") == null || s.get("prd_id").equals("") || s.get("prd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("prodApprovalDetail_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "prodApprovalDetail", new String[] { "prd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "pa_id", store.get("pa_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void saveprodAppDetail(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.handler(caller, "save", "before", new Object[] { store });
		// 修改ProductApproval
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProductApproval", "pa_id");
		baseDao.execute(formSql);
		// 修改productApprovalDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "prodAppDetail", "ppd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("ppd_id") == null || s.get("ppd_id").equals("") || s.get("ppd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("prodAppDetail_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "prodAppDetail", new String[] { "ppd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "pa_id", store.get("pa_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void saveApprovalResult(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProductApproval", "pa_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "pa_id", store.get("pa_id"));
	}

	@Override
	public void saveProductApproval(String formStore, String caller) {
		// TODO Auto-generated method stub
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存sale
		String formSql = SqlUtil.getInsertSqlByMap(store, "ProductApproval");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "pa_id", store.get("pa_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}
}
