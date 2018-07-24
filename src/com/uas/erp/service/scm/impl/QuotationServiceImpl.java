package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.b2b.model.QuotationDetailDet;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.MoneyUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.dao.common.QuotationDao;
import com.uas.erp.model.Key;
import com.uas.erp.service.scm.QuotationService;
import com.uas.erp.service.scm.SalePriceService;

@Service("quotationService")
public class QuotationServiceImpl implements QuotationService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private QuotationDao quotationDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private SalePriceService salePriceService;
	@Autowired
	private TransferRepository transferRepository;

	@Override
	public void saveQuotation(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Quotation", "qu_code='" + store.get("qu_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		// 保存Quotation
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Quotation", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存QuotationDetail
		StringBuffer error = new StringBuffer();
		Object[] qd_id = new Object[grid.size()];
		for (int i = 0; i < grid.size(); i++) {
			qd_id[i] = baseDao.getSeqId("QUOTATIONDETAIL_SEQ");
			grid.get(i).put("qd_id", baseDao.getSeqId("QUOTATIONDETAIL_SEQ"));
			grid.get(i).put("qd_statuscode", "ENTERING");
			/*
			 * // 到物料核价单取单价 Map<Object, Object> map = grid.get(i); Object price
			 * = baseDao.getFieldDataByCondition(
			 * "SalePriceDetail left join SalePrice on spd_spid=sp_id",
			 * "spd_price", "spd_prodcode='" + map.get("qd_prodcode") +
			 * "' AND spd_arcustcode='" + store.get("qu_custcode") +
			 * "' AND sp_fromdate<=sysdate and sp_todate>=sysdate AND spd_currency='"
			 * + store.get("qu_currency") + "' AND spd_statuscode='VALID'");
			 * Object tax = baseDao.getFieldDataByCondition(
			 * "SalePriceDetail left join SalePrice on spd_spid=sp_id",
			 * "spd_taxrate", "spd_prodcode='" + map.get("qd_prodcode") +
			 * "' AND spd_arcustcode='" + store.get("qu_custcode") +
			 * "' AND sp_fromdate<=sysdate and sp_todate>=sysdate AND spd_currency='"
			 * + store.get("qu_currency") + "' AND spd_statuscode='VALID'");
			 * price = price == null ? 0 : price; tax = tax == null ? 0 : tax;
			 * double p =
			 * NumberUtil.formatDouble(Double.parseDouble(price.toString()), 6);
			 * if (price != null && p != 0) { map.put("qd_price", p);
			 * map.put("qd_rate", Double.parseDouble(String.valueOf(tax))); }
			 * else { error.append("根据 客户编号:[" + store.get("qu_custcode") +
			 * "],物料编号:[" + map.get("qd_prodcode") + "],币别:[" +
			 * map.get("qd_pricetype") + "] 未找到对应单价，或单价为空值、0,或未审核等!<BR/>"); }
			 */
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "QuotationDetail");
		baseDao.execute(gridSql);
		defaultTax(caller, store.get("qu_id"));
		baseDao.execute("update quotationdetail set qd_code=(select qu_code from quotation where qd_quid=qu_id) where qd_quid="
				+ store.get("qu_id") + " and not exists (select 1 from quotation where qd_code=qu_code)");
		baseDao.logger.save(caller, "qu_id", store.get("qu_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, grid });
		// 修改主单据的总金额
		baseDao.execute("update quotation set qu_total=(select round(sum(qd_qty*qd_price),8) from quotationdetail where quotationdetail.qd_quid = quotation.qu_id) where qu_id="
				+ store.get("qu_id"));
		System.out.println(1);
		baseDao.execute("update quotationdetail set qd_grossprofit=round(((round(nvl(qd_price,0)*nvl(qd_qty,0)/(1+nvl(qd_rate,0)/100),8)-round(nvl(qd_factprice,0)*nvl(qd_qty,0),2))/round((nvl(qd_price,0)*nvl(qd_qty,0)/(1+nvl(qd_rate,0)/100)),8))*100,2) where qd_quid="
				+ store.get("qu_id") + " and nvl(qd_price,0)<>0 and nvl(qd_qty,0)<>0");
		Object total = baseDao.getFieldDataByCondition("quotation", "qu_total", "qu_id=" + store.get("qu_id"));
		if (total != null) {
			baseDao.execute("update quotation set qu_totalupper='" + MoneyUtil.toChinese(total.toString()) + "' WHERE qu_id="
					+ store.get("qu_id"));
		}
		quotation_commit_minus(store.get("qu_id"));
		if (error.length() > 0) {
			BaseUtil.showError("AFTERSUCCESS" + error.toString());
		}
	}

	@Override
	public void deleteQuotation(int qu_id, String caller) {
		// 只能删除在录入的单据!
		Object[] status = baseDao.getFieldsDataByCondition("Quotation", new String[] { "qu_statuscode", "qu_pricestatus", "qu_code" },
				"qu_id=" + qu_id);
		StateAssert.delOnlyEntering(status[0]);
		baseDao.delCheck("Quotation", qu_id);
		if (status[1] != null && !"".equals(status[1])) {
			BaseUtil.showError("已转价格库，不允许删除！");
		}
		boolean haveturn = baseDao.checkByCondition("sale", "sa_source='" + status[2] + "'");
		if (!haveturn) {
			BaseUtil.showError("已转销售订单，不允许删除！");
		}
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { qu_id });
		// 删除Quotation
		baseDao.deleteById("Quotation", "qu_id", qu_id);
		// 删除QuotationDetail
		baseDao.deleteById("Quotationdetail", "qd_quid", qu_id);
		// 记录操作
		baseDao.logger.delete(caller, "qu_id", qu_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { qu_id });
	}

	@Override
	public void updateQuotationById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("Quotation", "qu_statuscode", "qu_id=" + store.get("qu_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		// 修改Quotation
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Quotation", "qu_id");
		baseDao.execute(formSql);
		// 修改QuotationDetail
		/*
		 * StringBuffer error = new StringBuffer(); for (Map<Object, Object> s :
		 * gstore) { // 到物料核价单取单价 Object price =
		 * baseDao.getFieldDataByCondition(
		 * "SalePriceDetail left join SalePrice on spd_spid=sp_id", "spd_price",
		 * "spd_prodcode='" + s.get("qd_prodcode") + "' AND spd_arcustcode='" +
		 * store.get("qu_custcode") +
		 * "' AND sp_fromdate<=sysdate and sp_todate>=sysdate AND spd_currency='"
		 * + store.get("qu_currency") + "' AND spd_statuscode='VALID'"); Object
		 * tax = baseDao.getFieldDataByCondition(
		 * "SalePriceDetail left join SalePrice on spd_spid=sp_id",
		 * "spd_taxrate", "spd_prodcode='" + s.get("qd_prodcode") +
		 * "' AND spd_arcustcode='" + store.get("qu_custcode") +
		 * "' AND sp_fromdate<=sysdate and sp_todate>=sysdate AND spd_currency='"
		 * + store.get("qu_currency") + "' AND spd_statuscode='VALID'"); price =
		 * price == null ? 0 : price; tax = tax == null ? 0 : tax; double p =
		 * NumberUtil.formatDouble(Double.parseDouble(price.toString()), 6); if
		 * (price != null && p != 0) { s.put("qd_price", p); s.put("qd_rate",
		 * Double.parseDouble(String.valueOf(tax))); } else {
		 * error.append("根据 客户编号:[" + store.get("qu_custcode") + "],物料编号:[" +
		 * s.get("qd_prodcode") + "],币别:[" + s.get("qd_pricetype") +
		 * "] 未找到对应单价，或单价为空值、0,或未审核等!<BR/>"); } }
		 */
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "QuotationDetail", "qd_id");
		for (Map<Object, Object> s : gstore) {
			Object qdid = s.get("qd_id");
			if (qdid == null || qdid.equals("") || qdid.equals("0") || Integer.parseInt(qdid.toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("QUOTATIONDETAIL_SEQ");
				s.put("qd_id", id);
				s.put("qd_statuscode", "ENTERING");
				String sql = SqlUtil.getInsertSqlByMap(s, "QuotationDetail", new String[] { "qd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		defaultTax(caller, store.get("qu_id"));
		baseDao.execute("update quotationdetail set qd_code=(select qu_code from quotation where qd_quid=qu_id) where qd_quid="
				+ store.get("qu_id") + " and not exists (select 1 from quotation where qd_code=qu_code)");
		// 记录操作
		baseDao.logger.update(caller, "qu_id", store.get("qu_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
		// 修改主单据的总金额
		baseDao.execute("update quotation set qu_total=(select round(sum(qd_qty*qd_price),8) from quotationdetail where quotationdetail.qd_quid = quotation.qu_id) where qu_id="
				+ store.get("qu_id"));
		baseDao.execute("update quotationdetail set qd_grossprofit=round(((round(nvl(qd_price,0)*nvl(qd_qty,0)/(1+nvl(qd_rate,0)/100),2)-round(nvl(qd_factprice,0)*nvl(qd_qty,0),2))/round(nvl(qd_price,0)*nvl(qd_qty,0)/(1+nvl(qd_rate,0)/100),8))*100,2) where qd_quid="
				+ store.get("qu_id") + " and nvl(qd_price,0)<>0 and nvl(qd_qty,0)<>0");
		Object total = baseDao.getFieldDataByCondition("quotation", "qu_total", "qu_id=" + store.get("qu_id"));
		if (total != null) {
			baseDao.execute("update quotation set qu_totalupper='" + MoneyUtil.toChinese(total.toString()) + "' WHERE qu_id="
					+ store.get("qu_id"));
		}
		quotation_commit_minus(store.get("qu_id"));
	}

	@Override
	public String[] printQuotation(int qu_id, String caller, String reportName, String condition) {
		handlerService.handler(caller, "print", "before", new Object[] { qu_id });
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 修改打印状态
		// baseDao.print("quotation", "qu_id=" + qu_id, "qu_printstatus",
		// "qu_printstatuscode");
		// 记录操作
		baseDao.logger.print(caller, "qu_id", qu_id);
		// 执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[] { qu_id });
		return keys;
	}

	@Override
	public void auditQuotation(int qu_id, String caller) {
		baseDao.execute("update quotationdetail set qd_code=(select qu_code from quotation where qd_quid=qu_id) where qd_quid=" + qu_id
				+ " and not exists (select 1 from quotation where qd_code=qu_code)");
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Quotation", "qu_statuscode", "qu_id=" + qu_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { qu_id });
		// 执行审核操作
		baseDao.audit("Quotation", "qu_id=" + qu_id, "qu_status", "qu_statuscode", "qu_auditdate", "qu_auditman");
		baseDao.audit("QuotationDetail", "qd_quid=" + qu_id, "qd_status", "qd_statuscode");
		if (baseDao.isDBSetting("updateBusinessChance")) {
			// 更新当前商机阶段
			Object[] datas = baseDao.getFieldsDataByCondition("Quotation", "qu_bcid,qu_recorder,qu_code", "qu_id=" + qu_id);
			Object bsname = baseDao.getFieldDataByCondition("BusinessChanceStage", "bs_name", "bs_relativeitem='Quote'");
			Object date = baseDao.getFieldDataByCondition("Quotation", "to_char(qu_recorddate,'yyyy-MM-dd')", "qu_id=" + qu_id);
			List<Object[]> data = baseDao.getFieldsDatasByCondition("QuotationDetail  left join Quotation on qd_quid=qu_id", new String[] {
					"qd_bcid", "qd_prodcode", "qd_detno" }, "qd_quid=" + qu_id);
			for (Object[] os : data) {
				if (os[0] == null || os[0].equals("") || os[0].equals("0") || Integer.parseInt(os[0].toString()) == 0) {
					String sql = "update QuotationDetail set qd_bcid=nvl((select max(bc_id) from Quotation,businesschance"
							+ " where qu_id=qd_quid and qu_custcode=bc_custcode and qd_prodcode=bc_model and bc_status<>'已结案'),0) where "
							+ "qd_quid=" + qu_id + " and nvl(qd_bcid,0)=0";
					baseDao.execute(sql);
				}
				Object bc_id = baseDao.getFieldDataByCondition("QuotationDetail", "qd_bcid", " qd_prodcode='" + os[1] + "' and qd_quid="
						+ qu_id);
				Integer bs_detno = baseDao.getFieldValue("businesschancestage", "bs_detno", "bs_name='" + bsname + "'", Integer.class);
				Integer bs_detno1 = baseDao.getFieldValue("businesschance left join businesschancestage on bs_name=bc_currentprocess",
						"nvl(bs_detno,0)", "bc_id=" + bc_id, Integer.class);
				if (bs_detno != null && bs_detno1 != null) {
					if (bs_detno >= bs_detno1) {
						baseDao.updateByCondition("BusinessChance", "bc_currentprocess='" + bsname + "',bc_desc" + bs_detno + "='" + bsname
								+ "',bc_date" + bs_detno + "=to_date('" + date.toString() + "','yyyy-MM-dd')", "bc_id=" + bc_id);
						if (bsname != null && Integer.parseInt(bc_id.toString()) != 0) {
							Object bscode = baseDao.getFieldDataByCondition("BusinessChanceStage", "bs_code", "bs_name='" + bsname + "'");
							// 插入一条记录到商机动态表
							int bcd_id = baseDao.getSeqId("BusinessChanceData_seq");
							String link = "jsps/scm/sale/quotation.jsp?formCondition=qu_idIS" + qu_id + "&gridCondition=qd_quidIS" + qu_id;
							String contactSql = "insert into BusinessChanceData (bcd_id,bcd_bcid,bcd_code,bcd_bscode,bcd_bsname,bcd_date,bcd_man,bcd_statuscode,bcd_status,bcd_sourcecode,bcd_sourcelink) values ("
									+ bcd_id
									+ ","
									+ bc_id
									+ ",'"
									+ baseDao.sGetMaxNumber("BusinessChanceData", 2)
									+ "','"
									+ bscode
									+ "','"
									+ bsname
									+ "',"
									+ "to_date('"
									+ date.toString()
									+ "','yyyy-MM-dd')"
									+ ",'"
									+ datas[1]
									+ "','"
									+ "ENTERING" + "','" + BaseUtil.getLocalMessage("ENTERING") + "','" + datas[2] + "','" + link + "')";
							baseDao.execute(contactSql);
						}
					}
				}
			}
		}
		// 记录操作
		baseDao.logger.audit(caller, "qu_id", qu_id);
		if (baseDao.isDBSetting(caller, "autoSalePrice")) {
			toSalePrice(qu_id, caller);
		}
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { qu_id });
	}

	@Override
	public void resAuditQuotation(int qu_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("Quotation", new String[] { "qu_statuscode", "qu_pricestatus", "qu_code" },
				"qu_id=" + qu_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		if (status[1] != null && !"".equals(status[1])) {
			BaseUtil.showError("已转价格库，不允许反审核！");
		}
		boolean haveturn = baseDao.checkByCondition("sale", "sa_source='" + status[2] + "'");
		if (!haveturn) {
			BaseUtil.showError("已转销售订单，不允许反审核！");
		}
		baseDao.resAuditCheck("Quotation", qu_id);
		// 执行反审核操作
		baseDao.resOperate("Quotation", "qu_id=" + qu_id, "qu_status", "qu_statuscode");
		baseDao.resOperate("QuotationDetail", "qd_quid=" + qu_id, "qd_status", "qd_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "qu_id", qu_id);
	}

	@Override
	public void submitQuotation(int qu_id, String caller) {
		baseDao.execute("update quotationdetail set qd_code=(select qu_code from quotation where qd_quid=qu_id) where qd_quid=" + qu_id
				+ " and not exists (select 1 from quotation where qd_code=qu_code)");
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Quotation", "qu_statuscode", "qu_id=" + qu_id);
		StateAssert.submitOnlyEntering(status);
		defaultTax(caller, qu_id);
		quotation_commit_minus(qu_id);
		// 判断客户是否未审核或已禁用
		Object cust = baseDao.getFieldDataByCondition("Quotation", "qu_custcode", "qu_id=" + qu_id);
		status = baseDao.getFieldDataByCondition("Customer", "cu_auditstatuscode", "cu_code='" + cust + "'");
		Object prestatus = baseDao.getFieldDataByCondition("PreCustomer", "cu_auditstatuscode", "cu_code='" + cust + "'");
/*		if(baseDao.isDBSetting("Quotation","allowPreCust")){ // maz 允许报价单提交时客户为 已审核的客户预录入客户。2018040325
			status = status == null ? prestatus : status;
		}*/
		if(baseDao.isDBSetting("Quotation","allowPreCust")){ // zhoudw 允许报价单提交时 为对各种状态的预录入客户。
			status = status == null ? "AUDITED" : status;
		}
		if (status != null && status.equals("AUDITED")) {
			// 判断订单明细是否填写数量
			boolean bool = baseDao.checkByCondition("QuotationDetail", "qd_quid=" + qu_id + " AND (qd_qty is null OR qd_qty=0)");
			if (bool) {
				// 执行提交前的其它逻辑
				handlerService.handler(caller, "commit", "before", new Object[] { qu_id });
				// 自动获取单价
				StringBuffer error = new StringBuffer();
				/*
				 * List<Object[]> lists = baseDao.getFieldsDatasByCondition(
				 * "QuotationDetail left join Quotation on qu_id=qd_quid", new
				 * String[]{"qd_id", "qd_prodcode", "qu_custcode",
				 * "qd_pricetype", "qd_price", "qu_currency"}, "qu_id=" +
				 * qu_id); for(Object[] list:lists){ if(list[4] == null ||
				 * list[4].equals("") || list[4].equals("0") ||
				 * Float.parseFloat(list[4].toString()) == 0){ //到物料核价单取单价
				 * Object price = baseDao.getFieldDataByCondition(
				 * "SalePriceDetail left join SalePrice on spd_spid=sp_id",
				 * "spd_price", "spd_prodcode='" + list[1] + "' AND " +
				 * "sp_arcustcode='" + list[2] + "' AND spd_currency='" +
				 * list[5] + "' AND spd_statuscode='VALID'"); if(price != null
				 * && Float.parseFloat(price.toString()) != 0){ float p =
				 * Float.parseFloat(price.toString()); //修改明细的价格
				 * baseDao.updateByCondition("QuotationDetail", "qd_price=" + p,
				 * "qd_id=" + list[0]); } else { error.append("根据 物料编号:[" +
				 * list[1] + "],客户编号:[" + list[2] + "],币别:[" + list[5] +
				 * "] 未找到对应单价，或单价为空值、0,或未审核等!<BR/>"); } } }
				 */
				// 修改主单据的总金额
				baseDao.execute("update quotation set qu_total=(select round(sum(qd_qty*qd_price),8) from quotationdetail where quotationdetail.qd_quid = quotation.qu_id) where qu_id="
						+ qu_id);
				baseDao.execute("update quotationdetail set qd_grossprofit=round(((round(nvl(qd_price,0)*nvl(qd_qty,0)/(1+nvl(qd_rate,0)/100),2)-round(nvl(qd_factprice,0)*nvl(qd_qty,0),2))/round(nvl(qd_price,0)*nvl(qd_qty,0)/(1+nvl(qd_rate,0)/100),8))*100,2) where qd_quid="
						+ qu_id + " and nvl(qd_price,0)<>0 and nvl(qd_qty,0)<>0");
				Object total = baseDao.getFieldDataByCondition("quotation", "qu_total", "qu_id=" + qu_id);
				if (total != null) {
					baseDao.execute("update quotation set qu_totalupper='" + MoneyUtil.toChinese(total.toString()) + "' WHERE qu_id="
							+ qu_id);
				}
				// 执行提交操作
				baseDao.updateByCondition("Quotation", "qu_statuscode='COMMITED',qu_status='" + BaseUtil.getLocalMessage("COMMITED") + "'",
						"qu_id=" + qu_id);
				baseDao.updateByCondition("QuotationDetail", "qd_statuscode='COMMITED'", "qd_quid=" + qu_id);
				// 记录操作
				baseDao.logger.submit(caller, "qu_id", qu_id);
				// 执行提交后的其它逻辑
				handlerService.handler(caller, "commit", "after", new Object[] { qu_id });
				if (error.length() > 0) {
					BaseUtil.showError("AFTERSUCCESS" + error.toString());
				}
			} else {
				BaseUtil.showError("存在未填写数量的订单明细!");
			}
		} else {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.quotation.submit_cust")
					+ "<a href=\"javascript:openUrl('jsps/scm/sale/customerBase.jsp?formCondition=cu_codeIS" + cust + "')\">" + cust
					+ "</a>&nbsp;");
		}
	}

	@Override
	public void resSubmitQuotation(int qu_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Quotation", "qu_statuscode", "qu_id=" + qu_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { qu_id });
		// 执行反提交操作
		baseDao.resOperate("Quotation", "qu_id=" + qu_id, "qu_status", "qu_statuscode");
		baseDao.resOperate("QuotationDetail", "qd_quid=" + qu_id, "qd_status", "qd_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "qu_id", qu_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { qu_id });
	}

	@Override
	public void bannedQuotation(int qu_id, String caller) {
		// 执行禁用操作
		baseDao.banned("Quotation", "qu_id=" + qu_id, "qu_status", "qu_statuscode");
		baseDao.banned("QuotationDetail", "qd_quid=" + qu_id, "qd_status", "qd_statuscode");
		// 记录操作
		baseDao.logger.banned(caller, "qu_id", qu_id);
	}

	@Override
	public void resBannedQuotation(int qu_id, String caller) {
		// 执行反禁用操作
		baseDao.audit("Quotation", "qu_id=" + qu_id, "qu_status", "qu_statuscode");
		baseDao.audit("QuotationDetail", "qd_quid=" + qu_id, "qd_status", "qd_statuscode");
		// 记录操作
		baseDao.logger.resBanned(caller, "qu_id", qu_id);
	}

	@Override
	public int turnSale(int qu_id, String caller) {
		int said = 0;
		// 判断该报价单是否已经转入过销售单
		Object code = baseDao.getFieldDataByCondition("quotation", "qu_code", "qu_id=" + qu_id);
		code = baseDao.getFieldDataByCondition("sale", "sa_code", "sa_source='" + code + "'");
		if (code != null && !code.equals("")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.quotation.haveturn")
					+ "<a href=\"javascript:openUrl('jsps/scm/sale/sale.jsp?formCondition=sa_codeIS" + code + "&gridCondition=sd_codeIS"
					+ code + "')\">" + code + "</a>&nbsp;");
		} else {
			// 转销售
			Object custcode = baseDao.getFieldDataByCondition("quotation", "qu_custcode", "qu_id=" + qu_id);
			boolean bool = baseDao.checkByCondition("Customer", "cu_code='" + custcode + "' and cu_auditstatuscode='AUDITED'");
			if (bool) {
				BaseUtil.showError("不存在编号"+custcode+"已审核的客户资料");
			}
			
			said = quotationDao.turnSale(qu_id);
			Object sa_sellercode = baseDao.getFieldDataByCondition("Quotation", "qu_sellercode", "qu_id="+qu_id);
			if(baseDao.isDBSetting("Sale","addClerkCode") && StringUtil.hasText(sa_sellercode)){
				baseDao.updateByCondition("Sale","sa_code=sa_code||'"+sa_sellercode+"'", "sa_id="+said);
			}
			// 修改报价单状态
			baseDao.updateByCondition("quotation", "qu_turnstatuscode='TURNSA',qu_turnstatus='" + BaseUtil.getLocalMessage("TURNSA") + "'",
					"qu_id=" + qu_id);
			handlerService.handler("Quotation", "turnSale", "after", new Object[]{said});
		}
		return said;
	}

	@Override
	public int toSalePrice(int qu_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Quotation", "qu_pricestatus", "qu_id=" + qu_id);
		if (status != null && !"".equals(status)) {
			BaseUtil.showError("已转入过价格库，不允许重复转！");
		}
		Object custcode = baseDao.getFieldDataByCondition("quotation", "qu_custcode", "qu_id=" + qu_id);
		boolean bool = baseDao.checkByCondition("Customer", "cu_code='" + custcode + "' and cu_auditstatuscode='AUDITED'");
		if (bool) {
			BaseUtil.showError("不存在编号"+custcode+"已审核的客户资料");
		}
		Key key = transferRepository.transfer("Quotation!ToSalePrice", qu_id);
		int spid = key.getId();
		baseDao.execute("update quotation set qu_pricestatus='已转价格库' where qu_id=" + qu_id);
		// 转入明细
		transferRepository.transferDetail("Quotation!ToSalePrice", qu_id, key);
		String custStatus = baseDao.getDBSetting("Quotation", "SalePriceStatus");
		String statuscode = "AUDITED";
		if (custStatus != null) {
			// 状态是已审核
			if ("1".equals(custStatus)) {
				statuscode = "AUDITED";
			}
			// 状态是在录入
			if ("0".equals(custStatus)) {
				statuscode = "ENTERING";
			}
		}
		baseDao.updateByCondition("SalePrice", "sp_statuscode='" + statuscode + "',sp_status='" + BaseUtil.getLocalMessage(statuscode)
				+ "'", "sp_id=" + spid);
		baseDao.updateByCondition("SalePrice", "sp_auditman='" + SystemSession.getUser().getEm_name() + "', sp_auditdate=sysdate ",
				"sp_id=" + spid + " and sp_statuscode='AUDITED'");
		baseDao.updateByCondition("SalePrice", "sp_auditman=null, sp_auditdate=null ", "sp_id=" + spid + " and sp_statuscode='ENTERING'");
		boolean productRate = baseDao.isDBSetting("SalePrice", "productRate");
		if (custStatus != null && "1".equals(custStatus) && productRate) {
			SqlRowList rs = baseDao.queryForRowSet("select * from SalePriceDetail where spd_spid=? and nvl(spd_discount,0)<>0", spid);
			while (rs.next()) {
				if (StringUtil.hasText(rs.getObject("spd_arcustcode"))) {
					Object[] cu = baseDao.getFieldsDataByCondition("Customer", new String[] { "cu_id", "cu_name" },
							"cu_code='" + rs.getObject("spd_arcustcode") + "'");
					if (cu != null) {
						int pdr_id = baseDao.getSeqId("PRODUCTRATE_SEQ");
						String pdr_code = baseDao.sGetMaxNumber("ProductRate", 2);
						baseDao.execute(
								"Insert into ProductRate(pdr_id, pdr_custid, pdr_statuscode, pdr_status, pdr_departmentname, pdr_departmentcode, pdr_auditdate, pdr_auditman, pdr_emname, pdr_emid, pdr_code) values (?,?,'AUDITED','已审核',?,?,sysdate,?,?,?,?)",
								new Object[] { pdr_id, cu[0], SystemSession.getUser().getEm_depart(),
										SystemSession.getUser().getEm_departmentcode(), SystemSession.getUser().getEm_name(),
										SystemSession.getUser().getEm_name(), SystemSession.getUser().getEm_id(), pdr_code });
						baseDao.execute("Insert into ProductRateDetail(pdrd_id, pdrd_detno, pdrd_pdrid, pdrd_prodid,pdrd_prodcode,pdrd_startdate,pdrd_enddate,pdrd_rate, pdrd_saleprice,pdrd_salecurrency,pdrd_gross, pdrd_statuscode, pdrd_status) "
								+ "select "
								+ baseDao.getSeqId("PRODUCTRATEDETAIL_SEQ")
								+ ", 1, "
								+ pdr_id
								+ ", pr_id, spd_prodcode, sp_fromdate, nvl(sp_todate,to_date('2099-12-31','yyyy-mm-dd')), spd_discount, spd_price, spd_currency, spd_profitrate, 'VALID', '"
								+ BaseUtil.getLocalMessage("VALID")
								+ "' from SalePriceDetail,SalePrice,Product "
								+ "where spd_spid=sp_id and spd_prodcode=pr_code and spd_id=" + rs.getGeneralInt("spd_id"));
					}
				}
			}
			if ("AUDITED".equals(statuscode)) {
				StringBuffer sb1 = new StringBuffer();
				StringBuffer sb2 = new StringBuffer();
				// 自动失效价格库
				String method = baseDao.getDBSetting("SalePrice", "autoSalePrice");
				if (method != null && !"0".equals(method)) {
					List<Object[]> list = baseDao.getFieldsDatasByCondition("SalePriceDetail", new String[] { "spd_arcustcode",
							"spd_prodcode", "spd_currency", "spd_taxrate", "spd_pricetype" }, "spd_spid=" + spid
							+ " and spd_statuscode = 'VALID'");// 客户编号、料号、币别、税率、取价类型
					if (!list.isEmpty()) {
						for (Object[] objs : list) {
							List<Object[]> spds = null;
							// 失效原则：取价类型+客户编号+物料编号+币别+税率
							if ("2".equals(method)) {
								spds = baseDao.getFieldsDatasByCondition("SalePrice left join SalePriceDetail on sp_id=spd_spid",
										new String[] { "spd_id", "sp_code", "sp_id", "spd_detno" }, "nvl(spd_arcustcode,' ')='"
												+ StringUtil.nvl(objs[0], " ") + "' AND spd_statuscode='VALID'"
												+ " AND nvl(spd_prodcode,' ')='" + StringUtil.nvl(objs[1], " ")
												+ "' AND nvl(spd_currency,' ')='" + StringUtil.nvl(objs[2], " ") + "' AND spd_taxrate="
												+ objs[3] + " and nvl(spd_pricetype,' ')='" + StringUtil.nvl(objs[4], " ")
												+ "' and spd_spid <> " + spid);
							}
							// 失效原则：客户编号+物料编号+币别+税率
							if ("1".equals(method)) {
								spds = baseDao.getFieldsDatasByCondition("SalePrice left join SalePriceDetail on sp_id=spd_spid",
										new String[] { "spd_id", "sp_code", "sp_id", "spd_detno" }, "nvl(spd_arcustcode,' ')='"
												+ StringUtil.nvl(objs[0], " ") + "' AND spd_statuscode='VALID'"
												+ " AND nvl(spd_prodcode,' ')='" + StringUtil.nvl(objs[1], " ")
												+ "' AND nvl(spd_currency,' ')='" + StringUtil.nvl(objs[2], " ") + "' AND spd_taxrate="
												+ objs[3] + " and spd_spid <> " + spid);
							}
							for (Object[] spd : spds) {
								baseDao.updateByCondition("SalePriceDetail",
										"spd_statuscode='UNVALID',spd_status='" + BaseUtil.getLocalMessage("UNVALID") + "'", "spd_id="
												+ spd[0]);
								sb1.append("价格库原编号为<a href=\"javascript:openUrl('jsps/scm/sale/salePrice.jsp?formCondition=sp_idIS"
										+ spd[2] + "&gridCondition=spd_spidIS" + spd[2] + "&whoami=SalePrice')\">" + spd[1] + "</a>&nbsp;第"
										+ spd[3] + "行数据已自动失效!<hr>");
							}
						}
					}
				}
				// 自动失效同物料同单价的费用比例
				if (baseDao.isDBSetting("ProductRate", "autoProductRate")) {
					rs = baseDao
							.queryForRowSet(
									"select * from SalePriceDetail,customer where spd_spid=? and spd_arcustcode=cu_code and nvl(spd_discount,0)=0 and nvl(spd_arcustcode,' ')<>' '",
									spid);
					while (rs.next()) {
						if (rs.getGeneralInt("cu_id") == 0) {
							BaseUtil.showError("客户[" + rs.getObject("spd_arcustcode") + "]不存在！");
						}
						SqlRowList rs1 = baseDao
								.queryForRowSet(
										"select pdrd_id, pdr_code, pdr_id, pdrd_detno from ProductRate left join ProductRateDetail on pdrd_pdrid=pdr_id where pdrd_statuscode='VALID' and pdrd_prodcode=? and pdrd_saleprice=? and pdr_custid=?",
										rs.getObject("spd_prodcode"), rs.getGeneralDouble("spd_price"), rs.getObject("cu_id"));
						while (rs1.next()) {
							baseDao.updateByCondition("ProductRateDetail",
									"pdrd_statuscode='UNVALID',pdrd_status='" + BaseUtil.getLocalMessage("UNVALID") + "'",
									"pdrd_id=" + rs1.getGeneralInt("pdrd_id"));
							sb2.append("费用比例原编号为<a href=\"javascript:openUrl('jsps/scm/sale/productRate.jsp?formCondition=pdr_idIS"
									+ rs1.getGeneralInt("pdr_id") + "&gridCondition=pdrd_pdridIS" + rs1.getGeneralInt("pdr_id")
									+ "&whoami=ProductRate')\">" + rs1.getObject("pdr_code") + "</a>&nbsp;第"
									+ rs1.getGeneralInt("pdrd_detno") + "行数据已自动失效!<hr>");
						}
					}
				}
				if (sb1 != null && sb1.length() > 0) {
					BaseUtil.appendError(sb1.toString());
				}
				if (sb2 != null && sb2.length() > 0) {
					BaseUtil.appendError(sb2.toString());
				}
			}
		}
		return spid;
	}

	@Override
	public List<Map<String, Object>> getStepDet(Integer in_id) {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select * from QuotationDetailDet where qdd_qdid in (select qd_id from QuotationDetail where qd_quid=?) order by qdd_qdid,qdd_lapqty",
						in_id);
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("qdd_id", rs.getObject("qdd_id"));
			map.put("qdd_qdid", rs.getObject("qdd_qdid"));
			map.put("qdd_lapqty", rs.getObject("qdd_lapqty"));
			map.put("qdd_price", rs.getObject("qdd_price"));
			data.add(map);
		}
		return data;
	}

	@Override
	public List<QuotationDetailDet> findReplyByInid(int id) {
		try {
			return baseDao
					.getJdbcTemplate()
					.query("select QuotationDetailDet.* from QuotationDetailDet left join QuotationDetail on qd_id=qdd_qdid  where qd_quid=? order by qdd_qdid,qdd_lapqty",
							new BeanPropertyRowMapper<QuotationDetailDet>(QuotationDetailDet.class), id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public void saveZDquotation(String formStore, String gridStore, String dets, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> detGrid = BaseUtil.parseGridStoreToMaps(dets);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Quotation", "qu_code='" + store.get("qu_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		List<String> sqls = new ArrayList<String>();
		// 保存Inquiry
		sqls.add(SqlUtil.getInsertSqlByMap(store, "Quotation"));
		// 保存InquiryDetailDet
		Map<Object, List<Map<Object, Object>>> detList = BaseUtil.groupMap(detGrid, "qdd_qdid");
		for (Map<Object, Object> map : grid) {
			int id = baseDao.getSeqId("QuotationDetail_SEQ");
			detGrid = detList.get(map.get("qd_id"));
			if (detGrid != null) {
				for (Map<Object, Object> m : detGrid)
					m.put("qdd_qdid", id);
				sqls.addAll(SqlUtil.getInsertSqlbyList(detGrid, "QuotationDetailDet", "qdd_id"));
			} else {
				/*
				 * Double
				 * qd_price=Double.parseDouble(baseDao.getFieldDataByCondition
				 * ("QuotationDetail", "qd_price", " qd_id="+id).toString());
				 */
				sqls.add("insert into QuotationDetailDet(qdd_id,qdd_qdid,qdd_lapqty,qdd_price) values (InquiryDetailDet_seq.nextval," + id
						+ ",0," + map.get("qd_price") + ")");
			}
			map.put("qd_id", id);
			// 在customer 表中新增加的一个字段，cu_pricetype
			Boolean exist = baseDao.checkIf("USER_TAB_COLUMNS", "table_name='CUSTOMER' and column_name='CU_PRICETYPE'");
			if (exist) {// 现在有部分数据库没有加cu_pricetype列，
				Object fieldDataByCondition = baseDao.getFieldDataByCondition("customer", "cu_pricetype",
						"cu_code='" + store.get("qu_custcode") + "'");
				map.put("qd_custpricetype", fieldDataByCondition);
			}

		}
		// 保存InquiryDetail
		sqls.addAll(SqlUtil.getInsertSqlbyGridStore(grid, "QuotationDetail"));
		baseDao.execute(sqls);
		Object qu_id = store.get("qu_id");
		baseDao.logger.save(caller, "qu_id", qu_id);
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void deleteZDquotation(int qu_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Quotation", "qu_statuscode", "qu_id=" + qu_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, qu_id);
		baseDao.deleteByCondition("Quotationdetaildet", "qdd_qdid in (select qd_id from Quotationdetail where qd_quid=" + qu_id + ") ");
		// 删除Inquiry
		baseDao.deleteById("Quotation", "qu_id", qu_id);
		// 删除InquiryDetail
		baseDao.deleteById("Quotationdetail", "qd_quid", qu_id);
		// 记录操作
		baseDao.logger.delete(caller, "qu_id", qu_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, qu_id);
	}

	@Override
	public void updateZDquotation(String formStore, String gridStore, String dets, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> detGrid = BaseUtil.parseGridStoreToMaps(dets);
		// 只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("Quotation", "qu_statuscode", "qu_id=" + store.get("qu_id"));
		StateAssert.updateOnlyEntering(status);
		// 当前编号的记录已经存在,不能更新!
		boolean bool = baseDao.checkByCondition("Quotation", "qu_code='" + store.get("qu_code") + "' and qu_id<>" + store.get("qu_id"));
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		List<String> sqls = new ArrayList<String>();
		// 修改Inquiry
		sqls.add(SqlUtil.getUpdateSqlByFormStore(store, "Quotation", "qu_id"));
		// 修改InquiryDetail
		if (gstore.size() > 0) {
			Map<Object, List<Map<Object, Object>>> detList = BaseUtil.groupMap(detGrid, "qdd_qdid");
			for (Map<Object, Object> s : gstore) {
				if (s.get("qd_id") == null || s.get("qd_id").equals("") || s.get("qd_id").equals("0")
						|| Integer.parseInt(s.get("qd_id").toString()) <= 0) {
					int id = baseDao.getSeqId("QuotationDetail_SEQ");
					detGrid = detList.get(s.get("qd_id"));
					if (detGrid != null) {
						for (Map<Object, Object> m : detGrid)
							m.put("qdd_qdid", id);
						sqls.addAll(SqlUtil.getInsertSqlbyList(detGrid, "QuotationDetailDet", "qdd_id"));
					} else {
						sqls.add("insert into QuotationDetailDet(qdd_id,qdd_qdid,qdd_lapqty,qdd_price) values (QuotationDetailDet_seq.nextval,"
								+ id + ",0," + s.get("qd_price") + ")");
					}
					s.put("qd_id", id);
				} else {
					detGrid = detList.get(s.get("qd_id"));
					if (detGrid != null) {
						// 先删除无效行
						sqls.add("delete from QuotationDetailDet where qdd_qdid=" + s.get("qd_id") + " and qdd_id not in ("
								+ CollectionUtil.pluckSqlString(detGrid, "qdd_id") + ")");
						sqls.addAll(SqlUtil.getInsertOrUpdateSql(detGrid, "QuotationDetailDet", "qdd_id"));
					} else {
						sqls.add("delete from QuotationDetailDet where qdd_qdid=" + s.get("qd_id"));
						sqls.add("insert into QuotationDetailDet(qdd_id,qdd_qdid,qdd_lapqty,qdd_price) values (QuotationDetailDet.nextval,"
								+ s.get("qd_id") + ",0," + s.get("qd_price") + ")");
					}
				}
			}
			sqls.addAll(SqlUtil.getInsertOrUpdateSql(gstore, "QuotationDetail", "qd_id"));
		}
		baseDao.execute(sqls);
		// 更新明细记录为分段数量为0的报价
		baseDao.execute("update QuotationDetail set qd_price=(select qdd_price from quotationdetaildet where qdd_qdid=qd_id and nvl(qdd_lapqty,0)=0) where qd_quid="
				+ store.get("qu_id"));
		Object qu_id = store.get("qu_id");
		checkProduct(Integer.parseInt(qu_id.toString()));
		// 记录操作
		baseDao.logger.update(caller, "qu_id", qu_id);
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void submitZDquotation(int qu_id, String caller) {
		baseDao.execute("delete from QuotationDetailDet where not exists (select 1 from QuotationDetail where qd_id=qdd_qdid)");
		Object checkstatus = baseDao.getFieldDataByCondition("Quotation", "qu_statuscode", "qu_id=" + qu_id);
		if (!"ENTERING".equals(checkstatus)) {
			BaseUtil.showError("单据当前状态不允许提交!");
		}
		int count = baseDao.getCount("select count(1) from QuotationDetail where qd_quid=" + qu_id + " and nvl(qd_price,0)=0");
		if (count > 0) {
			BaseUtil.showError("价格未全部填写。请先填写价格并更新后，再执行提交操作!");
		}
		/*
		 * int count = 0; count = baseDao.getCount(
		 * "select count(*) from Quotationdetail where nvl(qd_price,0)>0 and qd_quid="
		 * + qu_id); if (count == 0) {
		 * BaseUtil.showError("没有供应商报价或者供应商报价全部为0，不能提交!"); }
		 */
		// Object status = null;
		/*
		 * List<Object> codes2 =
		 * baseDao.getFieldDatasByCondition("InquiryDetail", "id_vendcode",
		 * "id_inid=" + in_id); for (Object c : codes2) { status =
		 * baseDao.getFieldDataByCondition("Vendor", "ve_auditstatuscode",
		 * "ve_code='" + c + "'"); if (!status.equals("AUDITED")) {
		 * BaseUtil.showError(BaseUtil.getLocalMessage("vendor_onlyAudited") +
		 * "<a href=\"javascript:openUrl('jsps/scm/purchase/vendor.jsp?formCondition=ve_codeIS"
		 * + c + "')\">" + c + "</a>&nbsp;"); } }
		 */
		// 只能选择已审核的物料!
		/*
		 * List<Object> codes =
		 * baseDao.getFieldDatasByCondition("InquiryDetail", "id_prodcode",
		 * "id_inid=" + in_id); for (Object c : codes) { status =
		 * baseDao.getFieldDataByCondition("Product", "pr_statuscode",
		 * "pr_code='" + c + "'"); if (!status.equals("AUDITED")) {
		 * BaseUtil.showError(BaseUtil.getLocalMessage("product_onlyAudited") +
		 * "<a href=\"javascript:openUrl('jsps/scm/product/product.jsp?formCondition=pr_codeIS"
		 * + c + "')\">" + c + "</a>&nbsp;"); } }
		 */
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, qu_id);
		checkProduct(qu_id);
		// 执行提交操作
		baseDao.submit("Quotation", "qu_id=" + qu_id, "qu_status", "qu_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "qu_id", qu_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, qu_id);
	}

	@Override
	public void resSubmitZDquotation(int qu_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Quotation", "qu_statuscode", "qu_id=" + qu_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, qu_id);
		// 执行反提交操作
		baseDao.resOperate("Quotation", "qu_id=" + qu_id, "qu_status", "qu_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "qu_id", qu_id);
		handlerService.afterResSubmit(caller, qu_id);
	}

	@Override
	public void auditZDquotation(int qu_id, String caller) {
		handlerService.beforeAudit(caller, qu_id);
		// 执行审核操作
		baseDao.audit("Quotation", "qu_id=" + qu_id, "qu_status", "qu_statuscode", "qu_auditdate", "qu_auditman");
		toSalePrice(qu_id);
		baseDao.updateByCondition("Quotation", "qu_sendstatus='待上传'", "qu_id=" + qu_id);
		// 记录操作
		baseDao.logger.audit(caller, "qu_id", qu_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, qu_id);
	}

	@Override
	public void resAuditZDquotation(int qu_id, String caller) {
		Object[] status = baseDao.getFieldsDataByCondition("Quotation", "qu_statuscode", "qu_id=" + qu_id);
		if (!status[0].equals("AUDITED")) {
			BaseUtil.showError("当前状态不允许反审核!");
		}
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(qu_code) from Quotation where nvl(qu_sendstatus,' ')<>'待上传' and qu_id=" + qu_id, String.class);
		if (dets != null) {
			BaseUtil.showError("已上传至B2B平台，不允许反审核!" + dets);
		}
		// 判断该询价单是否已经转入过核价单
		Object code = baseDao.getFieldDataByCondition("Quotation", "qu_code", "qu_id=" + qu_id);
		Object sacode = baseDao.getFieldDataByCondition("SalePrice", "sp_code", "sp_source='" + code
				+ "' and sp_sourcetype='Quotation!ZD' ");
		if (sacode != null && !sacode.equals("")) {
			BaseUtil.showError("已转了销售定价单!");
		} else {
			// 执行反审核操作
			baseDao.resAudit("Quotation", "qu_id=" + qu_id, "qu_status", "qu_statuscode", "qu_auditdate", "qu_auditman");
			baseDao.updateByCondition("Quotation", "qu_sendstatus=''", "qu_id=" + qu_id);
			// 记录操作
			baseDao.logger.resAudit(caller, "qu_id", qu_id);
		}
	}

	/**
	 * 判断物料资料是否已按客户物料关系更新进去
	 */
	private void checkProduct(int qu_id) {
		baseDao.execute("update QuotationDetail set (qd_custprodcode,qd_custproddetail,qd_custprodspec)=(select pc_custprodcode,pc_custproddetail,pc_custprodspec from "
				+ "productcustomer where pc_custcode=(select qu_custcode from quotation where qd_quid= qu_id) and pc_prodcode=qd_prodcode) where  qd_quid = "
				+ qu_id);
		int countnum = baseDao
				.getCount("select count(*) from QuotationDetail where qd_quid=" + qu_id + " and nvl(qd_custprodcode,' ')=' '");
		if (countnum != 0)
			BaseUtil.showError("该物料还未建立【客户物料对照关系】");
	}

	/**
	 * 审核后将价格转入销售定价库
	 */
	public void toSalePrice(int qu_id) {
		Key key = transferRepository.transfer("Quotation!ZDToSalePrice", qu_id);
		int spid = key.getId();
		// 转入明细
		transferRepository.transferDetail("Quotation!ZDToSalePrice", qu_id, key);
		baseDao.execute("update SalePriceDetail set spd_pricetype=(select cu_pricetype from customer where cu_code=spd_arcustcode)"
				+ " where spd_spid=" + spid + " and nvl(spd_pricetype,' ')=' '");
		String custStatus = baseDao.getDBSetting("Quotation", "SalePriceStatus");
		String statuscode = "AUDITED";
		if (custStatus != null) {
			// 状态是已审核
			if ("1".equals(custStatus)) {
				statuscode = "AUDITED";
			}
			// 状态是在录入
			if ("0".equals(custStatus)) {
				statuscode = "ENTERING";
			}
		}
		baseDao.updateByCondition("SalePrice", "sp_statuscode='" + statuscode + "',sp_status='" + BaseUtil.getLocalMessage(statuscode)
				+ "'", "sp_id=" + spid);
		if ("AUDITED".equals(statuscode)) {
			baseDao.updateByCondition("SalePrice", "sp_auditman='" + SystemSession.getUser().getEm_name() + "', sp_auditdate=sysdate ",
					"sp_id=" + spid + " and sp_statuscode='AUDITED'");
			salePriceService.auditSalePriceAfter("SalePrice", spid);
		} else {
			baseDao.updateByCondition("SalePrice", "sp_auditman=null, sp_auditdate=null ", "sp_id=" + spid
					+ " and sp_statuscode='ENTERING'");
		}
	}

	// 税率默认
	private void defaultTax(String caller, Object qu_id) {
		String defaultTax = baseDao.getDBSetting("Quotation", "defaultTax");
		if (defaultTax != null) {
			// 税率强制等于币别表的默认税率
			if ("1".equals(defaultTax)) {
				baseDao.execute("update QuotationDetail set qd_rate=(select nvl(cr_taxrate,0) from currencys left join Quotation on qu_currency=cr_name and cr_statuscode='CANUSE' where qd_quid=qu_id)"
						+ " where qd_quid=" + qu_id);
			}
			// 税率强制等于客户资料的默认税率
			if ("2".equals(defaultTax)) {
				baseDao.execute("update QuotationDetail set qd_rate=(select nvl(cu_taxrate,0) from Customer left join Quotation on qu_custcode=cu_code and cu_auditstatuscode='AUDITED' where qu_id=qd_quid)"
						+ " where qd_quid=" + qu_id);
			}
		}
	}

	/**
	 * 报价单：是否负利润记算
	 */
	public void quotation_commit_minus(Object qu_id) {
		Object crrate = baseDao.getFieldDataByCondition(
				"Quotation left join currencysmonth on qu_currency=cm_crname and cm_yearmonth=to_char(qu_date,'yyyymm')", "cm_crrate",
				"qu_id=" + qu_id);
		if (crrate == null) {
			crrate = 1;
			BaseUtil.showError("月度汇率未设置！");
		}
		baseDao.execute("update Quotation set qu_rate=" + crrate + " where qu_id=" + qu_id + " and nvl(qu_rate,0)=0");
		String countMinus = baseDao.getDBSetting("Quotation", "countMinus");
		if (countMinus != null && !"0".equals(countMinus)) {
			List<Object[]> objs = baseDao.getFieldsDatasByCondition("Quotationdetail left join Quotation on qd_quid=qu_id", new String[] {
					"qd_id", "qd_prodcode", "qu_rate" }, " qu_id=" + qu_id);
			Double qu_rate = 0.0;
			for (Object[] os : objs) {
				if("1".equals(countMinus)){
					baseDao.updateByCondition("Quotationdetail",
							"QD_REFOPRICE=(select ba_price from (select ba_price from batch where ba_prodcode='" + os[1]
									+ "' and ba_remain>0 order by ba_date desc) where rownum=1)", "qd_id=" + os[0]);
				}else if("2".equals(countMinus)){
					baseDao.updateByCondition("Quotationdetail",
							"QD_REFOPRICE=(select pr_standardprice from product where pr_code='"+os[1]+"')", "qd_id=" + os[0]);
				}
				baseDao.updateByCondition("Quotationdetail", "QD_REFOPRICE=(select  price from (select round(nvl(pd_price,0)/(1+nvl(pd_rate,0)/100),8)*pu_rate price"
						+ " from purchasedetail left join purchase on pd_puid=pu_id where pd_prodcode='"+os[1]+"' and (pu_statuscode in('COMMITED','AUDITED') or "
								+ "nvl(pd_acceptqty,0)>0) order by pu_date desc) where rownum=1)", "qd_id=" + os[0]+" and nvl(QD_REFOPRICE,0)=0");
				qu_rate = Double.parseDouble(os[2].toString());
			}
			baseDao.execute("update Quotationdetail set qd_bodycost=round((qd_qty*qd_price*" + qu_rate
					+ "/(1+nvl(qd_rate,0)/100)*(1-nvl(qd_discount,0)/100)-qd_qty*QD_REFOPRICE)/(qd_qty*qd_price*" + qu_rate
					+ "/(1+nvl(qd_rate,0)/100)*(1-nvl(qd_discount,0)/100))*100,2) where nvl(qd_price,0)>0 and qd_quid=" + qu_id);
			int count = baseDao.getCount("select count(*) from Quotationdetail where qd_quid=" + qu_id + " and nvl(qd_bodycost,0)<0");
			String sa_minus = null;
			if (count > 0) {
				sa_minus = "是";
			} else {
				sa_minus = "否";
			}
			baseDao.execute("update Quotation set qu_minus='" + sa_minus + "' where qu_id=" + qu_id);
			baseDao.execute("update Quotationdetail set qd_minus='是' where nvl(qd_bodycost,0)<0 and qd_quid=" + qu_id);
			baseDao.execute("update Quotationdetail set qd_minus='否' where nvl(qd_bodycost,0)>=0 and qd_quid=" + qu_id);
		}
	}
}
