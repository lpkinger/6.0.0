package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.InvoiceService;

@Service("invoiceService")
public class InvoiceServiceImpl implements InvoiceService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveInvoice(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave("Invoice", new Object[] { store, grid });
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Invoice", "in_code='" + store.get("in_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.common.save_codeHasExist"));
		}
		// 保存Invoice
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Invoice", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		Double total = 0.0;
		// 保存InvoiceDetail
		for (Map<Object, Object> s : grid) {
			s.put("id_id", baseDao.getSeqId("InvoiceDETAIL_SEQ"));
			s.put("id_code", store.get("in_code"));
			Object qty = s.get("id_qty");
			Object price = s.get("id_price");
			total = NumberUtil.formatDouble(Double.parseDouble(qty.toString()) * Double.parseDouble(price.toString()), 2);
			if (qty != null && price != null) {
				s.put("id_total", total);
			}
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "InvoiceDetail");
		baseDao.execute(gridSql);
		Object in_id = store.get("in_id");
		baseDao.execute("update Invoice set in_total=(select sum(id_total) from InvoiceDetail where id_inid=" + in_id + ") where in_id="
				+ in_id);
		baseDao.execute("update Invoice set in_totalupper=L2U(in_total),in_totalupperenhkd=L2U(in_total/(case when nvl(in_rate,0)=0 then 1 else in_rate end)) where in_id="
				+ in_id);
		if (baseDao.isDBSetting("Invoice", "shCustUse")) {
			baseDao.updateByCondition("Invoice", "in_receiveid=in_receivecode,in_cop=(select en_shortname from enterprise)", "in_id="
					+ in_id);
		}
		baseDao.logger.save("Invoice", "in_id", store.get("in_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave("Invoice", new Object[] { store, grid });
	}

	@Override
	public void deleteInvoice(int in_id) {
		// 只能删除在录入的单据!
		Object[] status = baseDao.getFieldsDataByCondition("Invoice", new String[] { "in_statuscode", "in_code" }, "in_id=" + in_id);
		StateAssert.delOnlyEntering(status[0]);
		// 执行删除前的其它逻辑
		handlerService.beforeDel("Invoice", in_id);
		Object piid = baseDao.getFieldDataByCondition("Packing", "nvl(pi_id,0)", "pi_code='" + status[1] + "'");
		if (piid != null) {
			int pi_id = Integer.parseInt(piid.toString());
			baseDao.deleteById("Packing", "pi_id", pi_id);
			baseDao.deleteById("packingdetail", "pd_piid", pi_id);
		}
		baseDao.execute("update prodinout set pi_packingcode=null,pi_invoicecode=null where" + " pi_invoicecode='" + status[1]
				+ "' and pi_class in ('出货单','销售退货单','拨出单')");
		// 删除Invoice
		baseDao.deleteById("Invoice", "in_id", in_id);
		// 删除InvoiceDetail
		baseDao.deleteById("Invoicedetail", "id_inid", in_id);
		// 记录操作
		baseDao.logger.delete("Invoice", "in_id", in_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel("Invoice", in_id);
	}

	@Override
	public void updateInvoiceById(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("Invoice", "in_statuscode", "in_id=" + store.get("in_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeSave("Invoice", new Object[] { store, gstore });
		// 修改Invoice
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Invoice", "in_id");
		baseDao.execute(formSql);
		// 修改InvoiceDetail
		Double total = 0.0;
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "InvoiceDetail", "id_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("id_id") == null || s.get("id_id").equals("") || s.get("id_id").equals("0")
					|| Integer.parseInt(s.get("id_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("InvoiceDETAIL_SEQ");
				Object qty = s.get("id_qty");
				Object price = s.get("id_price");
				total = NumberUtil.formatDouble(Double.parseDouble(qty.toString()) * Double.parseDouble(price.toString()), 2);
				if (qty != null && price != null) {
					s.put("id_total", total);
				}
				s.put("id_code", store.get("in_code"));
				String sql = SqlUtil.getInsertSqlByMap(s, "InvoiceDetail", new String[] { "id_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		Object in_id = store.get("in_id");
		baseDao.execute("update invoicedetail set id_total=round(id_price*id_qty,2) where id_inid=?", in_id);
		baseDao.execute("update Invoice set in_total=(select sum(id_total) from InvoiceDetail where id_inid=" + in_id + ") where in_id="
				+ in_id);
		baseDao.execute("update Invoice set in_totalupper=L2U(in_total),in_totalupperenhkd=L2U(in_total/(case when nvl(in_rate,0)=0 then 1 else in_rate end)) where in_id="
				+ in_id);
		if (baseDao.isDBSetting("Invoice", "shCustUse")) {
			baseDao.updateByCondition("Invoice", "in_receiveid=in_receivecode,in_cop=(select en_shortname from enterprise)", "in_id="
					+ in_id);
		}
		// 记录操作
		baseDao.logger.update("Invoice", "in_id", store.get("in_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave("Invoice", new Object[] { store, gstore });
	}

	@Override
	public String[] printInvoice(int in_id, String reportName, String condition) {
		// 执行打印前的其它逻辑
		handlerService.beforePrint("Invoice", in_id);
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 记录操作
		baseDao.print("Invoice", "in_id=" + in_id, "IN_PRINTSTATUS", "IN_PRINTSTATUSCODE");
		baseDao.logger.print("Invoice", "in_id", in_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint("Invoice", in_id);
		return keys;
	}

	@Override
	public void auditInvoice(int in_id) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("Invoice", new String[] { "in_statuscode", "in_code" }, "in_id=" + in_id);
		StateAssert.auditOnlyCommited(status[0]);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit("Invoice", in_id);
		// 执行审核操作
		baseDao.audit("Invoice", "in_id=" + in_id, "in_status", "in_statuscode", "in_auditdate", "in_auditman");
		Object relativecode = baseDao.getFieldDataByCondition("Invoice", "in_relativecode", "in_id=" + in_id);
		if (relativecode != null) {
			relativecode = "'" + relativecode.toString().replaceAll(",", "','") + "'";
		}
		baseDao.execute("update arbill set ab_refno='" + status[1] + "' where ab_fromcode in (" + relativecode + ")");
		// 记录操作
		baseDao.logger.audit("Invoice", "in_id", in_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit("Invoice", in_id);
	}

	@Override
	public void resAuditInvoice(int in_id) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Invoice", "in_statuscode", "in_id=" + in_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("Invoice", "in_id=" + in_id, "in_status", "in_statuscode");
		// 记录操作
		baseDao.logger.resAudit("Invoice", "in_id", in_id);
		handlerService.afterResAudit("Invoice", in_id);
	}

	@Override
	public void submitInvoice(int in_id) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Invoice", "in_statuscode", "in_id=" + in_id);
		StateAssert.submitOnlyEntering(status);
		Object relativecode = baseDao.getFieldDataByCondition("Invoice", "in_relativecode", "in_id=" + in_id);
		if (relativecode != null) {
			relativecode = "'" + relativecode.toString().replaceAll(",", "','") + "'";
		}
		baseDao.execute("update Invoice set in_total=(select sum(round(id_total,2)) from InvoiceDetail where id_inid=" + in_id
				+ ") where in_id=" + in_id);
		baseDao.execute("update Invoice set in_totalupper=L2U(in_total),in_totalupperenhkd=L2U(in_total/(case when nvl(in_rate,0)=0 then 1 else in_rate end)) where in_id="
				+ in_id);
		Object intotal = baseDao.getFieldDataByCondition("Invoice", "round(in_total,2)", "in_id=" + in_id);
		int isbcbill = 0;
		if(baseDao.isDBSetting("Packing","isSpecialPacking")){
			isbcbill = Integer.parseInt(baseDao.getFieldDataByCondition("Invoice", "nvl(isbcbill,0)", "in_id=" + in_id).toString());
		}
		if(isbcbill == 0) {
			SqlRowList sl = baseDao
					.queryForRowSet("select round(sum(round((nvl(pd_outqty,0)+nvl(pd_inqty,0))*pd_sendprice,2)),2) from prodiodetail  where pd_inoutno in ("
							+ relativecode + ")");
			if (sl.next()) {
				Object inouttotal = sl.getObject(1);
				if (inouttotal != null && intotal != null
						&& Math.abs(Double.parseDouble(inouttotal.toString()) - Double.parseDouble(intotal.toString())) > 0.2) {
					BaseUtil.showError("出货单金额[" + inouttotal + "]和发票金额[" + intotal + "]不一致");
				}
			}
		}
		// 只能选择已审核的客户!
		Object code = baseDao.getFieldDataByCondition("Invoice", "in_custcode", "in_id=" + in_id);
		status = baseDao.getFieldDataByCondition("Customer", "cu_auditstatuscode", "cu_code='" + code + "'");
		if (status != null && !status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("customer_onlyAudited")
					+ "<a href=\"javascript:openUrl('jsps/scm/sale/customer.jsp?formCondition=cu_codeIS" + code + "')\">" + code
					+ "</a>&nbsp;");
		}
		// 只能选择已审核的物料!
		List<Object> codes = baseDao.getFieldDatasByCondition("InvoiceDetail", "id_prodcode", "id_inid=" + in_id);
		for (Object c : codes) {
			status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_code='" + c + "'");
			if (!status.equals("AUDITED")) {
				BaseUtil.showError(BaseUtil.getLocalMessage("product_onlyAudited")
						+ "<a href=\"javascript:openUrl('jsps/scm/product/product.jsp?formCondition=pr_codeIS" + c + "')\">" + c
						+ "</a>&nbsp;");
			}
		}
		int count = baseDao.getCountByCondition("InvoiceDetail", "id_inid=" + in_id + " and nvl(id_discount,0)>0");
		if (count > 0) {
			baseDao.execute("update Invoice set in_havediscount='是' where in_id=" + in_id);
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit("Invoice", in_id);
		// 执行提交操作
		baseDao.submit("Invoice", "in_id=" + in_id, "in_status", "in_statuscode");
		if (baseDao.isDBSetting("Invoice", "shCustUse")) {
			baseDao.updateByCondition("Invoice", "in_receiveid=in_receivecode,in_cop=(select en_shortname from enterprise)", "in_id="
					+ in_id);
		}
		// 记录操作
		baseDao.logger.submit("Invoice", "in_id", in_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit("Invoice", in_id);
	}

	@Override
	public void resSubmitInvoice(int in_id) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Invoice", "in_statuscode", "in_id=" + in_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit("Invoice", in_id);
		// 执行反提交操作
		baseDao.resOperate("Invoice", "in_id=" + in_id, "in_status", "in_statuscode");
		// 记录操作
		baseDao.logger.resSubmit("Invoice", "in_id", in_id);
		handlerService.afterResSubmit("Invoice", in_id);
	}

	@Override
	public void getSalePrice(int in_id) {
		String sql = "update InvoiceDetail set id_price=(select sd_costingprice from saledetail where sd_code=id_ordercode and sd_detno=id_orderdetno) where id_inid="+in_id;
		baseDao.execute(sql);
		baseDao.execute("update InvoiceDetail set id_total=round(id_price*id_qty,2) where id_inid="+in_id);
		baseDao.execute("update Invoice set in_total=(select sum(id_total) from InvoiceDetail where id_inid=" + in_id + ") where in_id="
				+ in_id);
		baseDao.execute("update Invoice set in_totalupper=L2U(in_total),in_totalupperenhkd=L2U(in_total/(case when nvl(in_rate,0)=0 then 1 else in_rate end)) where in_id="
				+ in_id);
		baseDao.execute("update invoice set in_saleprice='是' where in_id="+in_id+"");
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void savePreInvoice(String gridStore) {
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(grid, "PREINVOICE", "pi_id"));
		Object in_id = null;
		if(grid.size()>0) {
			in_id = grid.get(0).get("pi_inid");
		}
		Object in_status = baseDao.getFieldDataByCondition("Invoice", "in_status", "in_id="+in_id+"");
		if("已提交".equals(in_status.toString()) || "已审核".equals(in_status.toString())){
			BaseUtil.showError("销售发票已提交或已审核，不允许修改预收明细！");
		}
		if(in_id != null){
			SqlRowList rs = baseDao.queryForRowSet("select pi_prcode,pi_prddetno from preinvoice where pi_inid="+in_id+"");
			String pi_prcode = "";
			int pi_prddetno = 0;
			while(rs.next()){
				pi_prcode = rs.getString("pi_prcode");
				pi_prddetno = rs.getInt("pi_prddetno");
				Double pi_amount = baseDao.getSummaryByField("preinvoice", "pi_amount", "pi_prcode='"+pi_prcode+"' and pi_prddetno="+pi_prddetno+"");
				Object prd_nowbalance = baseDao.getFieldDataByCondition("PreRecDetail", "nvl(prd_nowbalance,0)", "prd_code='"+pi_prcode+"' and prd_detno="+pi_prddetno+"");
				if(pi_amount>Double.parseDouble(prd_nowbalance.toString())){
					BaseUtil.showError("预收明细中预收单号：["+pi_prcode+"],预收序号["+pi_prddetno+"]的预收总金额："+pi_amount+"不能超过预收账款中的预收金额："+prd_nowbalance+"");
				}
			}
		}
	}
}
