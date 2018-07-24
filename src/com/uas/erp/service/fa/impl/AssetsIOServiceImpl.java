package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.fa.AssetsIOService;

@Service("assetsIOService")
public class AssetsIOServiceImpl implements AssetsIOService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveAssetsIO(String caller, String formStore, String gridStore) {
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 保存ProdInOut
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "AssetsIO",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存ProdioDetail
		Object[] aid_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			aid_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				aid_id[i] = baseDao.getSeqId("ASSETSIODETAIL_SEQ");
			}
		} else {
			aid_id[0] = baseDao.getSeqId("ASSETSIODETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore,
				"AssetsIODetail", "aid_id", aid_id);
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save(caller, "ai_id", store.get("ai_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteAssetsIO(String caller, int ai_id) {
		// 只能删除[未过帐]的单据
		Object status = baseDao.getFieldDataByCondition("AssetsIO",
				"ai_statuscode", "ai_id=" + ai_id);
		if (status.equals("POST")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("fa.fix.assetsIO.delete_onlyUnPost"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, ai_id);
		// 删除ProdInOut(修改ProdInOut状态为[已删除])
		baseDao.updateByCondition(
				"AssetsIO",
				"ai_statuscode='DELETED',ai_status='"
						+ BaseUtil.getLocalMessage("DELETED") + "'", "ai_id="
						+ ai_id);
		// 记录操作
		baseDao.logger.delete(caller, "ai_id", ai_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ai_id);
	}

	@Override
	public void updateAssetsIOById(String caller, String formStore,
			String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改ProdInOut
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "AssetsIO",
				"ai_id");
		// baseDao.execute(formSql);
		// 修改ProdioDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"AssetsIODetail", "aid_id");
		// baseDao.execute(gridSql);
		// 记录操作
		gridSql.add(formSql);
		for (Map<Object, Object> s : gstore) {
			// Object v1=s.get("aid_id");
			// String v= String.valueOf(s.get("aid_id")) ;
			// boolean b1 = v1.equals("0");
			// boolean b2 = v.equals("0");

			if (s.get("aid_id") == null || s.get("aid_id").equals("")
					|| s.get("aid_id").equals("0")
					|| String.valueOf(s.get("aid_id")).equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("ASSETSIODETAIL_SEQ");
				s.put("aid_id", id);
				s.put("aid_status", "ENTERING");
				String sql = SqlUtil.getInsertSqlByMap(s, "AssetsIODetail",
						new String[] { "aid_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		baseDao.logger.update(caller, "ai_id", store.get("ai_id"));
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
		// 执行修改后的其它逻辑
		// handlerService.handler(caller, "save", "after", new
		// Object[]{store.get("ai_id")});
	}

	@Override
	public void printAssetsIO(String caller, int ai_id) {/*
														 * //明细资料有[未审核]、[已禁用]、[已删除
														 * ]或不存在的产品!
														 * List<Object> codes =
														 * baseDao
														 * .getFieldDatasByCondition
														 * ("Product",
														 * "pr_code",
														 * "pr_code IN (SELECT pd_prodcode FROM prodiodetail WHERE "
														 * + "aid_aiid=" + ai_id
														 * +
														 * ") AND pr_statuscode IN ('ENTERING','UNAUDIT','FORBIDDEN','DELETED')"
														 * ); if(codes != null
														 * && !codes.isEmpty()){
														 * StringBuffer sb = new
														 * StringBuffer();
														 * for(Object c:codes){
														 * sb.append(
														 * "<a href=\"javascript:openUrl('jsps/scm/product/productBase.jsp?formCondition=pr_codeIS"
														 * + c + "')\">" + c +
														 * "</a>&nbsp;"); }
														 * BaseUtil
														 * .showError(BaseUtil
														 * .getLocalMessage(
														 * "scm.reserve.prodInOut.print_prodcode"
														 * ) + sb.toString()); }
														 * //执行打印前的其它逻辑
														 * handlerService
														 * .handler(caller,
														 * "print", "before",
														 * new Object[]{pi_id});
														 * //执行打印操作 //记录操作
														 * baseDao
														 * .logMessage(new
														 * MessageLog
														 * (employee.getEm_name
														 * (),
														 * BaseUtil.getLocalMessage
														 * ("msg.print"),
														 * BaseUtil
														 * .getLocalMessage
														 * ("msg.printSuccess"),
														 * "ProdInOut|pi_id=" +
														 * pi_id)); //执行打印后的其它逻辑
														 * handlerService
														 * .handler(caller,
														 * "print", "after", new
														 * Object[]{pi_id});
														 */
	}

	@Override
	public void auditAssetsIO(String caller, int ai_id) {
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
		handlerService.beforeAudit(caller, ai_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"AssetsIO",
				"ai_statuscode='AUDITED',ai_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'", "ai_id="
						+ ai_id);
		// 记录操作
		baseDao.logger.audit(caller, "ai_id", ai_id);
		handlerService.afterAudit(caller, ai_id);
		// 执行审核后的其它逻辑
		// handlerService.handler(caller, "audit", "after", new
		// Object[]{pi_id});
	}

	@Override
	public void resAuditAssetsIO(int ai_id, String caller) {
		handlerService.beforeResAudit(caller, ai_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"AssetsIO",
				"ai_statuscode='ENTERING',ai_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "ai_id="
						+ ai_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ai_id", ai_id);
		handlerService.afterResAudit(caller, ai_id);
	}

	@Override
	public void submitAssetsIO(String caller, int ai_id) {
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ai_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"AssetsIO",
				"ai_statuscode='COMMITED',ai_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "ai_id="
						+ ai_id);
		// 记录操作
		baseDao.logger.submit(caller, "ai_id", ai_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ai_id);
	}

	@Override
	public void resSubmitAssetsIO(int ai_id, String caller) {
		handlerService.beforeResSubmit(caller, ai_id);
		baseDao.updateByCondition(
				"AssetsIO",
				"ai_statuscode='ENTERING',ai_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "ai_id="
						+ ai_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ai_id", ai_id);
		handlerService.afterResSubmit(caller, ai_id);
	}
}
