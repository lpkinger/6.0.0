package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.SalePriceService;

@Service("salePriceService")
public class SalePriceServiceImpl implements SalePriceService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveSalePrice(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("SalePrice", "sp_code='" + store.get("sp_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		// 保存SalePrice
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "SalePrice", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存SalePriceDetail
		for (int i = 0; i < grid.size(); i++) {
			Map<Object, Object> map = grid.get(i);
			map.put("spd_id", baseDao.getSeqId("SALEPRICEDETAIL_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "SalePriceDetail");
		baseDao.execute(gridSql);
		useDefaultTax(caller, store.get("sp_id"));
		baseDao.execute("update salepricedetail set spd_code=(select sp_code from saleprice where spd_spid=sp_id) where spd_spid="
				+ store.get("sp_id") + " and not exists (select 1 from saleprice where spd_code=sp_code)");
		// 记录操作
		baseDao.logger.save(caller, "sp_id", store.get("sp_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, grid });
	}

	@Override
	public void deleteSalePrice(int sp_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("SalePrice", "sp_statuscode", "sp_id=" + sp_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { sp_id });
		Object qucode = baseDao.getFieldDataByCondition("SalePrice", "sp_source", "sp_id=" + sp_id);
		// 删除SalePrice
		baseDao.deleteById("SalePrice", "sp_id", sp_id);
		// 删除SalePriceDetail
		baseDao.deleteById("SalePricedetail", "spd_spid", sp_id);
		if (qucode != null) {
			int count = baseDao.getCountByCondition("SalePriceDetail", "spd_spid=" + sp_id + " and nvl(spd_qdid,0)<>0");
			if (count == 0) {
				baseDao.updateByCondition("QUOTATION", "qu_pricestatus=null", "qu_code='" + qucode + "'");
			}
		}
		// 记录操作
		baseDao.logger.delete(caller, "sp_id", sp_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { sp_id });
	}

	/**
	 * 计算上次价格，当前物料价格浮动率
	 */
	private void getLastPrice(Object id) {
		SqlRowList rs = baseDao
				.queryForRowSet("SELECT * FROM SalePriceDetail LEFT JOIN SalePrice on " + "sp_id=spd_spid WHERE sp_id=?", id);
		Object price = null;
		while (rs.next()) {
			price = baseDao
					.getFieldValue(
							"(select round(nvl(ppd_price,0)/(1+ppd_rate/100),8) ppd_price from PurchasePriceDetail LEFT JOIN PurchasePrice on pp_id=ppd_ppid where ppd_prodcode='"
									+ rs.getString("spd_prodcode")
									+ "' and ppd_currency='"
									+ rs.getString("spd_currency")
									+ "' and pp_kind='采购' and pp_statuscode='AUDITED' and ppd_statuscode='VALID' order by pp_auditdate desc)",
							"ppd_price", "rownum<2", Object.class);
			if (price != null && Double.parseDouble(price.toString()) != 0) {
				baseDao.updateByCondition("SalePriceDetail", "spd_profitrate=round((1-" + price
						+ "/(spd_price/(1+(spd_taxrate/100))))*100,2)", "spd_id=" + rs.getInt("spd_id") + " and nvl(spd_profitrate,0)=0");
			}
		}
	}

	@Override
	public void updateSalePriceById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("SalePrice", "sp_statuscode", "sp_id=" + store.get("sp_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		// 修改SalePrice
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "SalePrice", "sp_id");
		baseDao.execute(formSql);
		// 修改SalePriceDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "SalePriceDetail", "spd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("spd_id") == null || s.get("spd_id").equals("") || s.get("spd_id").equals("0")
					|| Integer.parseInt(s.get("spd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("SALEPRICEDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "SalePriceDetail", new String[] { "spd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		useDefaultTax(caller, store.get("sp_id"));
		getLastPrice(store.get("sp_id"));
		baseDao.execute("update salepricedetail set spd_code=(select sp_code from saleprice where spd_spid=sp_id) where spd_spid="
				+ store.get("sp_id") + " and not exists (select 1 from saleprice where spd_code=sp_code)");
		// 记录操作
		baseDao.logger.update(caller, "sp_id", store.get("sp_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
	}

	@Override
	public void printSalePrice(int sp_id, String caller) {
		// 执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[] { sp_id });
		// 执行打印操作
		baseDao.logger.print(caller, "sp_id", sp_id);
		// 执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[] { sp_id });
	}

	@Override
	public void auditSalePrice(int sp_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("SalePrice", "sp_statuscode", "sp_id=" + sp_id);
		StateAssert.auditOnlyCommited(status);
		baseDao.execute("update salepricedetail set spd_code=(select sp_code from saleprice where spd_spid=sp_id) where spd_spid=" + sp_id
				+ " and not exists (select 1 from saleprice where spd_code=sp_code)");
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { sp_id });
		// 执行审核操作
		baseDao.audit("SalePrice", "sp_id=" + sp_id, "sp_status", "sp_statuscode", "sp_auditdate", "sp_auditman");
		baseDao.updateByCondition("SalePriceDetail", "spd_statuscode='VALID',spd_status='" + BaseUtil.getLocalMessage("VALID") + "'",
				"spd_spid=" + sp_id);
		// 记录操作
		baseDao.logger.audit(caller, "sp_id", sp_id);
		auditSalePriceAfter(caller, sp_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { sp_id });
	}

	@Override
	public void resAuditSalePrice(int sp_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("SalePrice", "sp_statuscode", "sp_id=" + sp_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("SalePrice", "sp_id=" + sp_id, "sp_status", "sp_statuscode");
		baseDao.updateByCondition("SalePriceDetail", "spd_statuscode='UNVALID',spd_status='" + BaseUtil.getLocalMessage("UNVALID") + "'",
				"spd_spid=" + sp_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "sp_id", sp_id);
	}

	@Override
	public void submitSalePrice(int sp_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("SalePrice", "sp_statuscode", "sp_id=" + sp_id);
		StateAssert.submitOnlyEntering(status);
		baseDao.execute("update salepricedetail set spd_code=(select sp_code from saleprice where spd_spid=sp_id) where spd_spid=" + sp_id
				+ " and not exists (select 1 from saleprice where spd_code=sp_code)");
		baseDao.execute("update salepricedetail set spd_prodcode=upper(spd_prodcode) where spd_spid=" + sp_id);
		getLastPrice(sp_id);
		// 只能选择已审核的物料!
		List<Object> codes = baseDao.getFieldDatasByCondition("SalePriceDetail", "spd_prodcode", "spd_spid=" + sp_id);
		for (Object c : codes) {
			status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_code='" + c + "'");
			if (status != null && !status.equals("AUDITED")) {
				BaseUtil.showError(BaseUtil.getLocalMessage("product_onlyAudited")
						+ "<a href=\"javascript:openUrl('jsps/scm/product/product.jsp?formCondition=pr_codeIS" + c + "')\">" + c
						+ "</a>&nbsp;");
			}
		}
		useDefaultTax(caller, sp_id);
		allowZeroTax(caller, sp_id);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { sp_id });
		// 执行提交操作
		baseDao.submit("SalePrice", "sp_id=" + sp_id, "sp_status", "sp_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "sp_id", sp_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { sp_id });
	}

	@Override
	public void resSubmitSalePrice(int sp_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("SalePrice", "sp_statuscode", "sp_id=" + sp_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { sp_id });
		// 执行反提交操作
		baseDao.resOperate("SalePrice", "sp_id=" + sp_id, "sp_status", "sp_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "sp_id", sp_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { sp_id });
	}

	@Override
	public void abatesalepricestatus(int id) {
		Object[] sp = baseDao.getFieldsDataByCondition("SalePriceDetail", new String[] { "spd_spid", "spd_detno" }, "spd_id=" + id);
		baseDao.updateByCondition("SalePriceDetail", "spd_statuscode='UNVALID', spd_status='" + BaseUtil.getLocalMessage("UNVALID") + "'",
				"spd_id=" + id);
		baseDao.logger.others("转失效", "行" + sp[1], "SalePrice", "sp_id", sp[0]);
	}

	@Override
	public void resabatesalepricestatus(int id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(distinct sp_code) from SalePrice, SalePriceDetail where sp_id=spd_spid and spd_id=? and to_char(sp_todate,'yyyymmdd')<to_char(sysdate,'yyyymmdd')",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("当前单有效截止日期已过期，不允许进行转有效操作！");
		}
		Object[] sp = baseDao.getFieldsDataByCondition("SalePriceDetail", new String[] { "spd_spid", "spd_detno" }, "spd_id=" + id);
		baseDao.updateByCondition("SalePriceDetail", "spd_statuscode='VALID', spd_status='" + BaseUtil.getLocalMessage("VALID") + "'",
				"spd_id=" + id);
		baseDao.logger.others("转有效", "行" + sp[1], "SalePrice", "sp_id", sp[0]);
	}

	// 税率强制等于币别表的默认税率
	private void useDefaultTax(String caller, Object sp_id) {
		if (baseDao.isDBSetting(caller, "useDefaultTax")) {
			baseDao.execute(
					"update SalePriceDetail set spd_taxrate=(select cr_taxrate from currencys where spd_currency=cr_name and cr_statuscode='CANUSE') where spd_spid=?",
					sp_id);
		}
	}

	// 本位币允许税率为0
	private void allowZeroTax(String caller, Object sp_id) {
		if (!baseDao.isDBSetting("Sale", "allowZeroTax")) {
			String currency = baseDao.getDBSetting("defaultCurrency");
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select WM_CONCAT(spd_detno) from SalePriceDetail where nvl(spd_taxrate,0)=0 and spd_currency='" + currency
							+ "' and spd_spid=?", String.class, sp_id);
			if (dets != null) {
				BaseUtil.showError("本位币税率为0，不允许提交!行号：" + dets);
			}
		}
	}

	public void auditSalePriceAfter(String caller, Object sp_id) {
		if (baseDao.isDBSetting("SalePrice", "productRate")) {
			// 状态是已审核
			SqlRowList rs = baseDao.queryForRowSet("select * from SalePriceDetail where spd_spid=? and nvl(spd_discount,0)<>0", sp_id);
			while (rs.next()) {
				if (StringUtil.hasText(rs.getObject("spd_arcustcode"))) {
					Object[] cu = baseDao.getFieldsDataByCondition("Customer", new String[] { "cu_id", "cu_name" },
							"cu_code='" + rs.getObject("spd_arcustcode") + "'");
					if (cu != null) {
						int pdr_id = baseDao.getSeqId("PRODUCTRATE_SEQ");
						String pdr_code = baseDao.sGetMaxNumber("ProductRate", 2);
						baseDao.execute(
								"Insert into ProductRate(pdr_id, pdr_custid, pdr_statuscode, pdr_status, pdr_departmentname, pdr_departmentcode, pdr_auditdate, pdr_auditman, pdr_emname, pdr_emid, pdr_code) values (?,?,'AUDITED','已审核',?,?,sysdate,?,?,?,? )",
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
		}
		// 自动失效客户编号、料号、币别、税率的价格库
		StringBuffer sb1 = new StringBuffer();
		String method = baseDao.getDBSetting("SalePrice", "autoSalePrice");
		if (method != null && !"0".equals(method)) {
			List<Object[]> list = baseDao.getFieldsDatasByCondition("SalePriceDetail", new String[] { "spd_arcustcode", "spd_prodcode",
					"spd_currency", "spd_taxrate", "spd_pricetype" }, "spd_spid=" + sp_id + " and spd_statuscode = 'VALID'");// 客户编号、料号、币别、税率、取价类型
			if (!list.isEmpty()) {
				for (Object[] objs : list) {
					List<Object[]> spds = null;
					// 失效原则：取价类型+客户编号+物料编号+币别+税率
					if ("2".equals(method)) {
						spds = baseDao.getFieldsDatasByCondition("SalePrice left join SalePriceDetail on sp_id=spd_spid", new String[] {
								"spd_id", "sp_code", "sp_id", "spd_detno" }, "nvl(spd_arcustcode,' ')='" + StringUtil.nvl(objs[0], " ")
								+ "' AND spd_statuscode='VALID'" + " AND nvl(spd_prodcode,' ')='" + StringUtil.nvl(objs[1], " ")
								+ "' AND nvl(spd_currency,' ')='" + StringUtil.nvl(objs[2], " ") + "' AND spd_taxrate=" + objs[3]
								+ " and nvl(spd_pricetype,' ')='" + StringUtil.nvl(objs[4], " ") + "' and spd_spid <> " + sp_id);
					}
					// 失效原则：客户编号+物料编号+币别+税率
					if ("1".equals(method)) {
						spds = baseDao.getFieldsDatasByCondition("SalePrice left join SalePriceDetail on sp_id=spd_spid", new String[] {
								"spd_id", "sp_code", "sp_id", "spd_detno" }, "nvl(spd_arcustcode,' ')='" + StringUtil.nvl(objs[0], " ")
								+ "' AND spd_statuscode='VALID'" + " AND nvl(spd_prodcode,' ')='" + StringUtil.nvl(objs[1], " ")
								+ "' AND nvl(spd_currency,' ')='" + StringUtil.nvl(objs[2], " ") + "' AND spd_taxrate=" + objs[3]
								+ " and spd_spid <> " + sp_id);
					}
					for (Object[] spd : spds) {
						baseDao.updateByCondition("SalePriceDetail",
								"spd_statuscode='UNVALID',spd_status='" + BaseUtil.getLocalMessage("UNVALID") + "'", "spd_id=" + spd[0]);
						sb1.append("价格库原编号为<a href=\"javascript:openUrl('jsps/scm/sale/salePrice.jsp?formCondition=sp_idIS" + spd[2]
								+ "&gridCondition=spd_spidIS" + spd[2] + "&whoami=SalePrice')\">" + spd[1] + "</a>&nbsp;第" + spd[3]
								+ "行数据已自动失效!<hr>");
					}
				}
			}
		}
		StringBuffer sb2 = new StringBuffer();
		// 自动失效同物料同单价的费用比例
		if (baseDao.isDBSetting("ProductRate", "autoProductRate")) {
			SqlRowList rs = baseDao
					.queryForRowSet(
							"select * from SalePriceDetail,customer where spd_spid=? and spd_arcustcode=cu_code and nvl(spd_discount,0)=0 and nvl(spd_arcustcode,' ')<>' '",
							sp_id);
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
							+ "&whoami=ProductRate')\">" + rs1.getObject("pdr_code") + "</a>&nbsp;第" + rs1.getGeneralInt("pdrd_detno")
							+ "行数据已自动失效!<hr>");
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
