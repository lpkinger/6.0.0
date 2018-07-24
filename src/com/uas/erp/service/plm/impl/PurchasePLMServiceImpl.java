package com.uas.erp.service.plm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.MoneyUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.PurchaseDao;
import com.uas.erp.service.plm.PurchasePLMService;

@Service("purchasePLMService")
public class PurchasePLMServiceImpl implements PurchasePLMService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private PurchaseDao purchaseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void savePurchase(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Purchase", "pu_code='" + store.get("pu_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.purchase.save_pucodeHasExist"));
		}
		// 缺省应付供应商
		if (store.get("pu_receivecode") == null || store.get("pu_receivecode").toString().trim().equals("")) {
			store.put("pu_receivecode", store.get("pu_vendcode"));
			store.put("pu_receivename", store.get("pu_vendname"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		// 保存purchase
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Purchase", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存purchaseDetail
		Object[] pd_id = new Object[grid.size()];
		bool = store.get("pu_getprice").toString().equals("-1");// 是否自动获取单价
		StringBuffer error = new StringBuffer();
		for (int i = 0; i < grid.size(); i++) {
			Map<Object, Object> map = grid.get(i);
			pd_id[i] = baseDao.getSeqId("PURCHASEDETAIL_SEQ");
			map.put("pd_id", pd_id[i]);
			map.put("pd_status", "ENTERING");
			if (map.get("pd_price") == null || Double.parseDouble(map.get("pd_price").toString()) == 0) {
				if (bool) {
					// 到物料核价单取单价
					Object price = baseDao.getFieldDataByCondition("PurchasePriceDetail", "ppd_price",
							"ppd_prodcode='" + map.get("pd_prodcode") + "' AND ppd_vendcode='" + map.get("pd_vendcode") + "' AND "
									+ "ppd_currency='" + store.get("pu_currency") + "'");
					price = price == null ? 0 : price;
					if (price != null && Double.parseDouble(price.toString()) != 0) {
						double p = Double.parseDouble(price.toString());
						map.put("pd_price", p);
						// 金额
						map.put("pd_total", Double.parseDouble(map.get("pd_qty").toString()) * p);
						// 不含税金额
						map.put("pd_taxtotal",
								Double.parseDouble(map.get("pd_qty").toString()) * p
										/ (1 + Double.parseDouble(map.get("pd_rate").toString()) / 100));
					} else {
						error.append("根据 物料编号:[" + map.get("pd_prodcode") + "],供应商号:[" + map.get("pd_vendcode") + "],币别:["
								+ store.get("pu_currency") + "] 在物料核价单未找到对应单价，或单价为空值、0等!<BR/>");
					}
				}
			} else {
				// 金额
				double total = Double.parseDouble(map.get("pd_qty").toString()) * Double.parseDouble(map.get("pd_price").toString());
				map.put("pd_total", NumberUtil.formatDouble(total, 3));
				// 不含税金额
				total = Double.parseDouble(map.get("pd_qty").toString()) * Double.parseDouble(map.get("pd_price").toString())
						/ (1 + Double.parseDouble(map.get("pd_rate").toString()) / 100);
				map.put("pd_taxtotal", NumberUtil.formatDouble(total, 3));
			}
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "PurchaseDetail");
		baseDao.execute(gridSql);
		// 修改主单据的总金额
		baseDao.execute("update Purchase set pu_total=(select sum(pd_total) from PurchaseDetail where PurchaseDetail.pd_puid = Purchase.pu_id) where pu_id="
				+ store.get("pu_id"));
		baseDao.execute("update Purchase set pu_taxtotal=(select sum(pd_taxtotal) from PurchaseDetail where PurchaseDetail.pd_puid = Purchase.pu_id) where pu_id="
				+ store.get("pu_id"));
		Object total = baseDao.getFieldDataByCondition("Purchase", "pu_total", "pu_id=" + store.get("pu_id"));
		if (total != null) {
			baseDao.execute("update Purchase set pu_totalupper='" + MoneyUtil.toChinese(total.toString()) + "' WHERE pu_id="
					+ store.get("pu_id"));
		}
		baseDao.logger.save(caller, "pu_id", store.get("pu_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, grid });
		if (error.length() > 0) {
			BaseUtil.showError("AFTERSUCCESS" + error.toString());
		}
	}

	@Override
	public void deletePurchase(int pu_id, String caller) {
		// 只能删除在录入的采购单!
		Object status = baseDao.getFieldDataByCondition("Purchase", "pu_statuscode", "pu_id=" + pu_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { pu_id });
		// 删除purchase
		baseDao.deleteById("purchase", "pu_id", pu_id);
		// 删除purchaseDetail
		purchaseDao.deletePurchase(pu_id);
		// 还原请购单
		// 记录操作
		baseDao.logger.delete(caller, "pu_id", pu_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { pu_id });
	}

	@Override
	public void updatePurchaseById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("Purchase", "pu_statuscode", "pu_id=" + store.get("pu_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		// 缺省应付供应商
		if (store.get("pu_receivecode") == null || store.get("pu_receivecode").toString().trim().equals("")) {
			store.put("pu_receivecode", store.get("pu_vendcode"));
			store.put("pu_receivename", store.get("pu_vendname"));
		}
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Purchase", "pu_id");
		baseDao.execute(formSql);
		// 修改purchaseDetail
		boolean bool = store.get("pu_getprice").toString().equals("-1");// 是否自动获取单价
		StringBuffer error = new StringBuffer();
		for (Map<Object, Object> s : gstore) {
			if (s.get("pd_price") == null || Double.parseDouble(s.get("pd_price").toString()) == 0) {
				if (bool) {
					// 到物料核价单取单价
					Object price = baseDao.getFieldDataByCondition("PurchasePriceDetail", "ppd_price",
							"ppd_prodcode='" + s.get("pd_prodcode") + "' AND ppd_vendcode='" + store.get("pu_vendcode") + "' AND "
									+ "ppd_currency='" + store.get("pu_currency") + "'");
					if (price != null && Double.parseDouble(price.toString()) != 0) {
						double p = Double.parseDouble(price.toString());
						s.put("pd_price", p);
						// 金额
						double total = Double.parseDouble(s.get("pd_qty").toString()) * p;
						s.put("pd_total", NumberUtil.formatDouble(total, 3));
						// 不含税金额
						total = Double.parseDouble(s.get("pd_qty").toString()) * p
								/ (1 + Double.parseDouble(s.get("pd_rate").toString()) / 100);
						s.put("pd_taxtotal", NumberUtil.formatDouble(total, 3));
					} else {
						error.append("根据 物料编号:[" + s.get("pd_prodcode") + "],供应商号:[" + s.get("pd_vendcode") + "],币别:["
								+ store.get("pu_currency") + "] 在物料核价单未找到对应单价，或单价为空值、0等!<BR/>");
					}
				}
			} else {
				// 金额
				double total = Double.parseDouble(s.get("pd_qty").toString()) * Double.parseDouble(s.get("pd_price").toString());
				s.put("pd_total", NumberUtil.formatDouble(total, 3));
				// 不含税金额
				total = Double.parseDouble(s.get("pd_qty").toString()) * Double.parseDouble(s.get("pd_price").toString())
						/ (1 + Double.parseDouble(s.get("pd_rate").toString()) / 100);
				s.put("pd_taxtotal", NumberUtil.formatDouble(total, 3));
			}
		}
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "PurchaseDetail", "pd_id");
		for (Map<Object, Object> s : gstore) {
			Object pdid = s.get("pd_id");
			if (pdid == null || pdid.equals("") || pdid.equals("0") || Integer.parseInt(pdid.toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("PURCHASEDETAIL_SEQ");
				s.put("pd_id", id);
				s.put("pd_status", "ENTERING");
				String sql = SqlUtil.getInsertSqlByMap(s, "PurchaseDetail", new String[] { "pd_id" }, new Object[] { id });
				gridSql.add(sql);
			} else {
				// 原采购数量 - 现提交数量 = 变更数量
				Object obj = baseDao.getFieldDataByCondition("PurchaseDetail", "pd_qty", "pd_id=" + pdid);
				// 修改请购单已转数量及状态
				purchaseDao.restoreApplicationWithQty(Integer.parseInt(pdid.toString()),
						Double.parseDouble(obj.toString()) - Double.parseDouble(s.get("pd_qty").toString()));
			}
		}
		baseDao.execute(gridSql);
		// 修改主单据的总金额
		baseDao.execute("update Purchase set pu_total=(select sum(pd_total) from PurchaseDetail where PurchaseDetail.pd_puid = Purchase.pu_id) where pu_id="
				+ store.get("pu_id"));
		baseDao.execute("update Purchase set pu_taxtotal=(select sum(pd_taxtotal) from PurchaseDetail where PurchaseDetail.pd_puid = Purchase.pu_id) where pu_id="
				+ store.get("pu_id"));
		Object total = baseDao.getFieldDataByCondition("Purchase", "pu_total", "pu_id=" + store.get("pu_id"));
		if (total != null) {
			baseDao.execute("update Purchase set pu_totalupper='" + MoneyUtil.toChinese(total.toString()) + "' WHERE pu_id="
					+ store.get("pu_id"));
		}
		// 记录操作
		baseDao.logger.update(caller, "pu_id", store.get("pu_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
		if (error.length() > 0) {
			BaseUtil.showError("AFTERSUCCESS" + error.toString());
		}
	}

	@Override
	public void printPurchase(int pu_id, String caller) {
		// 判断已审核才允许打印
		if (baseDao.isDBSetting(caller, "printNeedAudit")) {
			String status = baseDao.getFieldValue("Purchase", "pu_statuscode", "pu_id=" + pu_id, String.class);
			StateAssert.printOnlyAudited(status);
		}
		// 执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[] { pu_id });
		// 执行打印操作
		baseDao.print("Purchase", "pu_id=" + pu_id, "pu_printstatus", "pu_printstatuscode");
		// 记录操作
		baseDao.logger.print(caller, "pu_id", pu_id);
		// 执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[] { pu_id });
	}

	@Override
	public void auditPurchase(int pu_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Purchase", "pu_statuscode", "pu_id=" + pu_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { pu_id });
		// 执行审核操作
		baseDao.audit("Purchase", "pu_id=" + pu_id, "pu_status", "pu_statuscode", "pu_auditdate", "pu_auditman");
		baseDao.audit("PurchaseDetail", "pd_puid=" + pu_id, "pd_auditstatus", "pd_status");
		// 记录操作
		baseDao.logger.audit(caller, "pu_id", pu_id);
		// 执行审核后的其它逻辑
		handlerService.handler("Purchase", "audit", "after", new Object[] { pu_id });
	}

	@Override
	public void resAuditPurchase(int pu_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Purchase", "pu_statuscode", "pu_id=" + pu_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("Purchase", "pu_id=" + pu_id, "pu_status", "pu_statuscode");
		baseDao.resOperate("PurchaseDetail", "pd_puid=" + pu_id, "pd_auditstatus", "pd_status");
		// 记录操作
		baseDao.logger.resAudit(caller, "pu_id", pu_id);
	}

	@Override
	public void submitPurchase(int pu_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Purchase", "pu_statuscode", "pu_id=" + pu_id);
		StateAssert.submitOnlyEntering(status);
		// 只能选择已审核的供应商!
		Object code = baseDao.getFieldDataByCondition("Purchase", "pu_vendcode", "pu_id=" + pu_id);
		status = baseDao.getFieldDataByCondition("Vendor", "ve_auditstatuscode", "ve_code='" + code + "'");
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("vendor_onlyAudited")
					+ "<a href=\"javascript:openUrl('jsps/scm/purchase/vendor.jsp?formCondition=ve_codeIS" + code + "')\">" + code
					+ "</a>&nbsp;");
		}
		// 只能选择已审核的物料!
		List<Object> codes = baseDao.getFieldDatasByCondition("PurchaseDetail", "pd_prodcode", "pd_puid=" + pu_id);
		for (Object c : codes) {
			status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_code='" + c + "'");
			if (!status.equals("AUDITED")) {
				BaseUtil.showError(BaseUtil.getLocalMessage("product_onlyAudited")
						+ "<a href=\"javascript:openUrl('jsps/scm/product/product.jsp?formCondition=pr_codeIS" + c + "')\">" + c
						+ "</a>&nbsp;");
			}
		}
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { pu_id });
		// 缺省应付供应商
		baseDao.updateByCondition("Purchase", "pu_receivecode=pu_vendcode,pu_receivename=pu_vendname", "pu_id=" + pu_id
				+ " and (pu_receivecode is null or trim(pu_receivecode)='')");
		String sql = " update purchasedetail set pd_prodid=(select pr_id from product where pr_code=pd_prodcode) where pd_puid=" + pu_id;
		baseDao.execute(sql);
		// 执行提交操作
		baseDao.submit("Purchase", "pu_id=" + pu_id, "pu_status", "pu_statuscode");
		baseDao.submit("PurchaseDetail", "pd_puid=" + pu_id, "pd_auditstatus", "pd_status");
		// 自动获取单价
		StringBuffer error = new StringBuffer();
		List<Object[]> lists = baseDao.getFieldsDatasByCondition("PurchaseDetail left join Purchase on pu_id=pd_puid", new String[] {
				"pd_id", "pd_prodcode", "pd_vendcode", "pu_currency", "pd_price", "pu_getprice" }, "pu_id=" + pu_id);
		for (Object[] list : lists) {
			if ((list[5] != null && list[5].toString().equals("-1"))
					&& (list[4] == null || list[4].equals("") || list[4].equals("0") || Float.parseFloat(list[4].toString()) == 0)) {
				// 到物料核价单取单价
				Object price = baseDao.getFieldDataByCondition("PurchasePriceDetail", "ppd_price", "ppd_prodcode='" + list[1]
						+ "' AND ppd_vendcode='" + list[2] + "' AND " + "ppd_currency='" + list[3] + "'");
				if (price != null && Double.parseDouble(price.toString()) != 0) {
					double p = Double.parseDouble(price.toString());
					// 修改采购明细的价格
					baseDao.updateByCondition("PurchaseDetail", "pd_price=" + p + ",pd_total=trim(to_char(pd_qty*" + p
							+ ", '99999999999999.999')),pd_taxtotal=" + "trim(to_char(pd_qty*" + p
							+ "/(1+pd_rate/100),'99999999999999.999'))", "pd_id=" + list[0]);
				} else {
					error.append("根据 物料编号:[" + list[1] + "],供应商号:[" + list[2] + "],币别:[" + list[3] + "] 在物料核价单未找到对应单价，或单价为空值、0等!<BR/>");
				}
			} else {
				// 修改采购明细的金额
				baseDao.updateByCondition("PurchaseDetail",
						"pd_total=trim(to_char(pd_qty*nvl(pd_price,0),'99999999999999.999')),pd_taxtotal="
								+ "trim(to_char(pd_qty*nvl(pd_price,0)/(1+pd_rate/100),'99999999999999.999'))", "pd_id=" + list[0]
								+ " and (pd_total is null or pd_total=0)");
			}
		}
		// 修改主单据的总金额
		baseDao.execute("update Purchase set pu_total=(select sum(pd_total) from PurchaseDetail where PurchaseDetail.pd_puid = Purchase.pu_id) where pu_id="
				+ pu_id);
		baseDao.execute("update Purchase set pu_taxtotal=(select sum(pd_taxtotal) from PurchaseDetail where PurchaseDetail.pd_puid = Purchase.pu_id) where pu_id="
				+ pu_id);
		Object total = baseDao.getFieldDataByCondition("Purchase", "pu_total", "pu_id=" + pu_id);
		if (total != null) {
			baseDao.execute("update Purchase set pu_totalupper='" + MoneyUtil.toChinese(total.toString()) + "' WHERE pu_id=" + pu_id);
		}
		// 记录操作
		baseDao.logger.submit(caller, "pu_id", pu_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { pu_id });
		if (error.length() > 0) {
			BaseUtil.showError("AFTERSUCCESS" + error.toString());
		}
	}

	@Override
	public void resSubmitPurchase(int pu_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Purchase", "pu_statuscode", "pu_id=" + pu_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.resOperate("Purchase", "pu_id=" + pu_id, "pu_status", "pu_statuscode");
		baseDao.resOperate("PurchaseDetail", "pd_puid=" + pu_id, "pd_auditstatus", "pd_status");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pu_id", pu_id);
	}

	@Override
	public void endPurchase(int pu_id, String caller) {
		// 只能对状态为[已审核]的订单进行结案操作!
		Object status = baseDao.getFieldDataByCondition("Purchase", "pu_statuscode", "pu_id=" + pu_id);
		StateAssert.end_onlyAudited(status);
		// 结案
		baseDao.updateByCondition("Purchase", "pu_statuscode='FINISH',pu_status='" + BaseUtil.getLocalMessage("FINISH") + "',pu_enddate=sysdate",
				"pu_id=" + pu_id);
		baseDao.updateByCondition("PurchaseDetail", "PD_MRPSTATUSCODE='FINISH',PD_MRPSTATUS='" + BaseUtil.getLocalMessage("FINISH") + "',pd_enddate=sysdate",
				"pd_puid=" + pu_id);
		// 记录操作
		baseDao.logger.end(caller, "pu_id", pu_id);
	}

	@Override
	public void resEndPurchase(int pu_id, String caller) {
		// 只能对状态为[已结案]的订单进行反结案操作!
		Object status = baseDao.getFieldDataByCondition("Purchase", "pu_statuscode", "pu_id=" + pu_id);
		if (!status.equals("FINISH")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resEnd_onlyEnd"));
		}
		// 反结案
		baseDao.updateByCondition("Purchase",
				"pu_statuscode='AUDITED',pu_status='" + BaseUtil.getLocalMessage("AUDITED") + "',pu_enddate=null", "pu_id=" + pu_id);
		baseDao.updateByCondition("PurchaseDetail",	"PD_MRPSTATUSCODE=null, PD_MRPSTATUS=null,pd_enddate=null", "pd_puid=" + pu_id);
		// 记录操作
		baseDao.logger.resEnd(caller, "pu_id", pu_id);
	}
}
