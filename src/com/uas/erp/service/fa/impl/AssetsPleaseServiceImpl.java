package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.fa.AssetsPleaseService;

@Service("assetsPleaseService")
public class AssetsPleaseServiceImpl implements AssetsPleaseService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveAssetsPlease(String caller, String formStore,
			String gridStore) {
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 保存ProdInOut
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "AssetsPlease",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存ProdioDetail
		Object[] apd_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			apd_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				apd_id[i] = baseDao.getSeqId("ASSETSPLEASEDETAIL_SEQ");
			}
		} else {
			apd_id[0] = baseDao.getSeqId("ASSETSPLEASEDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore,
				"AssetsPleaseDetail", "apd_id", apd_id);
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save(caller, "ap_id", store.get("ap_id"));
		handlerService.afterSave(caller, new Object[] { store, gstore });
		// 执行保存后的其它逻辑
		// handlerService.handler(caller, "save", "after", new
		// Object[]{store.get("ai_id")});
	}

	@Override
	public void deleteAssetsPlease(String caller, int ap_id) {
		// 只能删除[未过帐]的单据
		Object status = baseDao.getFieldDataByCondition("AssetsPlease",
				"ap_statuscode", "ap_id=" + ap_id);
		if (status.equals("POST")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("fa.fix.assetsPlease.delete_onlyUnPost"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, ap_id);
		// 删除ProdInOut(修改ProdInOut状态为[已删除])
		baseDao.updateByCondition(
				"AssetsPlease",
				"ap_statuscode='DELETED',ap_status='"
						+ BaseUtil.getLocalMessage("DELETED") + "'", "ap_id="
						+ ap_id);
		// 记录操作
		baseDao.logger.delete(caller, "ap_id", ap_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ap_id);
	}

	@Override
	public void updateAssetsPleaseById(String caller, String formStore,
			String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改ProdInOut
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "AssetsPlease",
				"ap_id");
		// baseDao.execute(formSql);
		// 修改ProdioDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"AssetsPleaseDetail", "apd_id");
		// baseDao.execute(gridSql);
		// 记录操作
		gridSql.add(formSql);

		for (Map<Object, Object> s : gstore) {
			// Object v1=s.get("aid_id");
			// String v= String.valueOf(s.get("aid_id")) ;

			// boolean b1 = v1.equals("0");
			// boolean b2 = v.equals("0");

			if (s.get("apd_id") == null || s.get("apd_id").equals("")
					|| s.get("apd_id").equals("0")
					|| String.valueOf(s.get("apd_id")).equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("ASSETSPLEASEDETAIL_SEQ");
				s.put("apd_id", id);
				// s.put("apd_status", "ENTERING");
				String sql = SqlUtil.getInsertSqlByMap(s, "AssetsPleaseDetail",
						new String[] { "apd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		baseDao.logger.update(caller, "ap_id", store.get("ap_id"));
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
		// 执行修改后的其它逻辑
		// handlerService.handler(caller, "save", "after", new
		// Object[]{store.get("ai_id")});
	}

	@Override
	public void printAssetsPlease(String caller, int ap_id) {

	}

	@Override
	public void auditAssetsPlease(String caller, int ap_id) {
		// 明细资料有[未审核]、[已禁用]、[已删除]或不存在的产品!
		// List<Object> codes = baseDao.getFieldDatasByCondition("Product",
		// "pr_code", "pr_code IN (SELECT pd_prodcode FROM prodiodetail WHERE "
		// + "pd_piid=" + pi_id +
		// ") AND pr_statuscode IN ('ENTERING','UNAUDIT','FORBIDDEN','DELETED')");
		// if(codes != null && !codes.isEmpty()){
		// StringBuffer sb = new StringBuffer();
		// for(Object c:codes){
		// sb.append("<a href=\"javascript:openUrl('jsps/scm/product/productBase.jsp?formCondition=pr_codeIS"
		// + c + "')\">" + c + "</a>&nbsp;");
		// }
		// BaseUtil.showError(BaseUtil.getLocalMessage("scm.reserve.prodInOut.audit_prodcode")
		// + sb.toString());
		// }
		// 执行过账前的其它逻辑
		handlerService.beforeAudit(caller, ap_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"AssetsPlease",
				"ap_statuscode='AUDITED',ap_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',ap_auditer='"
						+ SystemSession.getUser().getEm_name()
						+ "',ap_auditdate=sysdate", "ap_id=" + ap_id);
		// 记录操作
		baseDao.logger.audit(caller, "ap_id", ap_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ap_id);
		// handlerService.handler(caller, "audit", "after", new
		// Object[]{pi_id});
	}

	@Override
	public void resAuditAssetsPlease(int ap_id, String caller) {
		// 执行反审核操作
		handlerService.beforeResAudit(caller, ap_id);
		baseDao.updateByCondition(
				"AssetsPlease",
				"ap_statuscode='ENTERING',ap_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',ap_auditer='',ap_auditdate=null", "ap_id=" + ap_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ap_id", ap_id);
		handlerService.afterResAudit(caller, ap_id);
	}

	@Override
	public void submitAssetsPlease(String caller, int ap_id) {
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ap_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"AssetsPlease",
				"ap_statuscode='COMMITED',ap_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "ap_id="
						+ ap_id);
		// 记录操作
		baseDao.logger.submit(caller, "ap_id", ap_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ap_id);
	}

	@Override
	public void resSubmitAssetsPlease(int ap_id, String caller) {
		handlerService.beforeResSubmit(caller, ap_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"AssetsPlease",
				"ap_statuscode='ENTERING',ap_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "ap_id="
						+ ap_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ap_id", ap_id);
		handlerService.afterResSubmit(caller, ap_id);
	}
}
