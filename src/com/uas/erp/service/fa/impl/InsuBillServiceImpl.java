package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.fa.InsuBillService;

@Service("insuBillService")
public class InsuBillServiceImpl implements InsuBillService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveInsuBill(String caller, String formStore, String gridStore) {
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// handlerService.handler(caller, "save", "before", new
		// Object[]{formStore,gridStore});
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 保存ProdInOut
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "InsuBill",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存ProdioDetail
		Object[] ibd_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			ibd_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				ibd_id[i] = baseDao.getSeqId("INSUBILLDETAIL_SEQ");
			}
		} else {
			ibd_id[0] = baseDao.getSeqId("INSUBILLDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore,
				"InsuBillDetail", "ibd_id", ibd_id);
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save(caller, "ib_id", store.get("ib_id"));
		handlerService.afterSave(caller, new Object[] { store, gstore });
		// 执行保存后的其它逻辑
		// handlerService.handler(caller, "save", "after", new
		// Object[]{store.get("ai_id")});
	}

	@Override
	public void deleteInsuBill(String caller, int ib_id) {
		// 只能删除[未过帐]的单据
		Object status = baseDao.getFieldDataByCondition("InsuBill",
				"ib_statuscode", "ib_id=" + ib_id);
		if (status.equals("POST")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("fa.fix.insuBill.delete_onlyUnPost"));
		}
		// 执行删除前的其它逻辑
		// handlerService.handler(caller, "delete", "before", new
		// Object[]{pi_id, employee});
		handlerService.beforeDel(caller, ib_id);
		// 删除ProdInOut(修改ProdInOut状态为[已删除])
		baseDao.updateByCondition(
				"InsuBill",
				"ib_statuscode='DELETED',ib_status='"
						+ BaseUtil.getLocalMessage("DELETED") + "'", "ib_id="
						+ ib_id);
		// 记录操作
		baseDao.logger.delete(caller, "ib_id", ib_id);
		handlerService.afterDel(caller, ib_id);
		// 执行删除后的其它逻辑
		// handlerService.handler(caller, "delete", "after", new Object[]{ai_id,
		// employee});
	}

	@Override
	public void updateInsuBillById(String caller, String formStore,
			String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 执行修改前的其它逻辑
		// handlerService.handler(caller, "save", "before", new
		// Object[]{store.get("ai_id")});
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改ProdInOut
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "InsuBill",
				"ib_id");
		// baseDao.execute(formSql);
		// 修改ProdioDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"InsuBillDetail", "ibd_id");
		// baseDao.execute(gridSql);
		// 记录操作
		gridSql.add(formSql);

		for (Map<Object, Object> s : gstore) {
			// Object v1=s.get("aid_id");
			// String v= String.valueOf(s.get("aid_id")) ;

			// boolean b1 = v1.equals("0");
			// boolean b2 = v.equals("0");

			if (s.get("ibd_id") == null || s.get("ibd_id").equals("")
					|| s.get("ibd_id").equals("0")
					|| String.valueOf(s.get("ibd_id")).equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("INSUBILLDETAIL_SEQ");
				s.put("ibd_id", id);
				// s.put("ibd_status", "ENTERING");
				String sql = SqlUtil.getInsertSqlByMap(s, "InsuBillDetail",
						new String[] { "ibd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		baseDao.logger.update(caller, "ib_id", store.get("ib_id"));
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
		// 执行修改后的其它逻辑
		// handlerService.handler(caller, "save", "after", new
		// Object[]{store.get("ai_id")});
	}

	@Override
	public void printInsuBill(String caller, int ib_id) {

	}

	@Override
	public void auditInsuBill(String caller, int ib_id) {
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
		// handlerService.handler(caller, "audit", "before", new
		// Object[]{pi_id});
		handlerService.beforeAudit(caller, ib_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"InsuBill",
				"ib_statuscode='AUDITED',ib_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'", "ib_id="
						+ ib_id);
		// 记录操作
		baseDao.logger.audit(caller, "ib_id", ib_id);
		handlerService.afterAudit(caller, ib_id);
		// 执行审核后的其它逻辑
		// handlerService.handler(caller, "audit", "after", new
		// Object[]{pi_id});
	}

	@Override
	public void resAuditInsuBill(int ib_id, String caller) {
		handlerService.beforeResAudit(caller, ib_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"InsuBill",
				"ib_statuscode='ENTERING',ib_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "ib_id="
						+ ib_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ib_id", ib_id);
		handlerService.afterResAudit(caller, ib_id);
	}

	@Override
	public void submitInsuBill(String caller, int ib_id) {
		// 执行提交前的其它逻辑
		// handlerService.handler(caller, "commit", "before", new
		// Object[]{pi_id});
		handlerService.beforeSubmit(caller, ib_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"InsuBill",
				"ib_statuscode='COMMITED',ib_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "ib_id="
						+ ib_id);
		// 记录操作
		baseDao.logger.submit(caller, "ib_id", ib_id);
		handlerService.afterSubmit(caller, ib_id);
		// 执行提交后的其它逻辑
		// handlerService.handler(caller, "commit", "after", new
		// Object[]{pi_id});
	}

	@Override
	public void resSubmitInsuBill(int ib_id, String caller) {
		// 执行反提交操作
		handlerService.beforeResSubmit(caller, ib_id);
		baseDao.updateByCondition(
				"InsuBill",
				"ib_statuscode='ENTERING',ib_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "ib_id="
						+ ib_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ib_id", ib_id);
		handlerService.afterResSubmit(caller, ib_id);
	}

}
