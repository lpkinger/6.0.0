package com.uas.erp.service.scm.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.api.b2c_erp.buyer.model.B2cOrder;
import com.uas.api.b2c_erp.buyer.model.B2cOrderDetail;
import com.uas.api.b2c_erp.buyer.model.Purchase;
import com.uas.api.b2c_erp.buyer.model.PurchaseDetail;
import com.uas.b2b.model.PurchaseReply;
import com.uas.b2b.model.PurchaseTender;
import com.uas.b2b.model.PurchaseTenderAnswer;
import com.uas.b2b.model.PurchaseTenderProd;
import com.uas.b2b.model.SaleTenderItem;
import com.uas.b2c.service.buyer.PurchaseOrderService;
import com.uas.b2c.service.common.GetGoodsReserveService;
import com.uas.b2c.service.seller.SendPurchaseToB2CService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.MoneyUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.dao.common.PurchaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Key;
import com.uas.erp.model.Master;
import com.uas.erp.model.MessageLog;
import com.uas.erp.model.TenderChange;
import com.uas.erp.service.common.JProcessService;
import com.uas.erp.service.common.SingleFormItemsService;
import com.uas.erp.service.oa.SendMailService;
import com.uas.erp.service.scm.ProductBatchUUIdService;
import com.uas.erp.service.scm.PurchaseService;

@Service("purchaseService")
public class PurchaseServiceImpl implements PurchaseService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private PurchaseDao purchaseDao;
	@Autowired
	private SingleFormItemsService singleFormItemsService;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private TransferRepository transferRepository;
	@Autowired
	private SendMailService sendMailService;
	@Autowired
	private GetGoodsReserveService getGoodsReserveService;
	@Autowired
	private SendPurchaseToB2CService sendPurchaseToB2CService;
	@Autowired
	private ProductBatchUUIdService productBatchUUIdService;

	@Autowired
	private JProcessService jprocessService;
	@Autowired
	private PurchaseOrderService purchaseOrderService;

	@Override
	public void savePurchase(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Purchase", "pu_code='" + store.get("pu_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.purchase.save_pucodeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		store.put("pu_printstatuscode", "UNPRINT");
		store.put("pu_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
		// 缺省应付供应商
		if (store.get("pu_receivecode") == null || store.get("pu_receivecode").toString().trim().equals("")) {
			store.put("pu_receivecode", store.get("pu_vendcode"));
			store.put("pu_receivename", store.get("pu_vendname"));
		}
		// 采购订单逻辑配置增加一个逻辑配置“供应商启用B2B收料，采购PO前缀增加 ”
		String B2BPrefix = baseDao.getDBSetting("Purchase", "VendorUseB2BAddPrefix");
		int ifdeliveryonb2b = Integer.parseInt(baseDao.getFieldDataByCondition("vendor", "nvl(ve_ifdeliveryonb2b,0)",
				"ve_code='" + store.get("pu_vendcode") + "'").toString());
		if (B2BPrefix != null && !"".equals(B2BPrefix) && ifdeliveryonb2b == -1) {
			store.put("pu_code", B2BPrefix + store.get("pu_code"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		// 保存purchase
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Purchase", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存purchaseDetail
		bool = store.get("pu_getprice").toString().equals("-1");// 是否自动获取单价getlastprice
		String getlastprice = baseDao.getDBSetting("Purchase", "getPriceByPurc");// 取价原则：抓取最近一次采购单单价  maz  2018060670  改成radio形式 增加取采购验收单单价
		StringBuffer error = new StringBuffer();
		JSONObject obj = null;
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "PURCHASEDETAIL", "pd_id");
		for (Map<Object, Object> map : grid) {
			map.put("pd_status", "ENTERING");
			map.put("pd_auditstatus", BaseUtil.getLocalMessage("ENTERING"));
			map.put("pd_code", store.get("pu_code"));
			Object pdid = map.get("pd_id");
			if (pdid == null || pdid.equals("") || pdid.equals("0") || Integer.parseInt(pdid.toString()) == 0) {// 新添加的数据，id不存在
				if (map.get("pd_sourcedetail") != null && !"".equals(map.get("pd_sourcedetail")) && !"0".equals(map.get("pd_sourcedetail"))) {
					purchaseDao.restoreYqty(Double.parseDouble(map.get("pd_qty").toString()),
							Integer.valueOf(map.get("pd_sourcedetail").toString()));
				}
				baseDao.execute(SqlUtil.getInsertSql(map, "PURCHASEDETAIL", "pd_id"));
			} else {
				purchaseDao.restoreApplicationWithQty(Integer.parseInt(pdid.toString()), Double.parseDouble(map.get("pd_qty").toString()));
			}
		}
		baseDao.execute(gridSql);
		SqlRowList rs = baseDao.queryForRowSet("select * from purchasedetail,purchase where pd_puid=pu_id and pd_puid="
				+ store.get("pu_id"));
		while (rs.next()) {
			if ("1".equals(getlastprice)) {
				if (rs.getGeneralDouble("pd_price") == 0) {
					SqlRowList pd = baseDao
							.queryForRowSet(
									"SELECT pd_price,pd_bgprice,pd_rate,pd_netprice FROM (select nvl(pd_price,0) pd_price,nvl(pd_bgprice,0) pd_bgprice,nvl(pd_rate,0) pd_rate,nvl(pd_netprice,0) pd_netprice "
											+ "from PurchaseDetail LEFT JOIN Purchase on pu_id=pd_puid where pd_prodcode=? and pu_currency=? and pu_vendcode=? and pu_statuscode='AUDITED' order by pu_auditdate desc) WHERE rownum<2",
									rs.getString("pd_prodcode"), rs.getString("pu_currency"), rs.getString("pu_vendcode"));
					if (pd.next()) {
						baseDao.updateByCondition(
								"PurchaseDetail",
								"pd_price=" + pd.getGeneralDouble("pd_price") + ", pd_bgprice=" + pd.getGeneralDouble("pd_bgprice")
										+ ", pd_rate=" + pd.getGeneralDouble("pd_rate") + ", pd_netprice="
										+ pd.getGeneralDouble("pd_netprice"), "pd_id=" + rs.getGeneralInt("pd_id")
										+ " and nvl(pd_price,0)=0 and pd_qty-NVL(pd_beipin,0)>0");
					}
				}
			} else if ("2".equals(getlastprice)){
				SqlRowList pd = baseDao.queryForRowSet(
						"SELECT pd_price,pd_customprice,pd_taxrate,pd_netprice FROM (select nvl(pd_orderprice,0) pd_price,nvl(pd_customprice,0) pd_customprice,nvl(pd_taxrate,0) pd_taxrate,nvl(pd_netprice,0) pd_netprice "
								+ "from ProdIODetail LEFT JOIN ProdInOut on pd_piid=pi_id where pd_prodcode=? and pi_currency=? and pi_cardcode=? and pi_statuscode='POSTED' order by pi_date desc) WHERE rownum<2",
						rs.getString("pd_prodcode"), rs.getString("pu_currency"), rs.getString("pu_vendcode"));
				if (pd.next()) {
					baseDao.updateByCondition("PurchaseDetail",
							"pd_price=" + pd.getGeneralDouble("pd_price") + ",pd_bgprice=" + pd.getGeneralDouble("pd_customprice") + ", pd_rate="
									+ pd.getGeneralDouble("pd_taxrate") + ", pd_netprice=" + pd.getGeneralDouble("pd_netprice"),
							"pd_id=" + rs.getGeneralInt("pd_id"));
				}
			} else {
				if (bool && !"B2C".equals(rs.getObject("pu_ordertype"))) {
					double price = 0;
					double total = 0;
					double tax = 0;
					double qty = Double.parseDouble(rs.getString("pd_qty"));
					double nettotal = 0;
					double netprice = 0;
					// 到物料核价单取单价
					Object oqty = baseDao.getFieldDataByCondition("PurchaseDetail", "nvl(sum(pd_qty),0)",
							" pd_puid=" + rs.getInt("pd_puid") + " and pd_prodcode='" + rs.getString("pd_prodcode") + "'");
					Object pu_date = baseDao.getFieldDataByCondition("Purchase", "to_char(pu_date,'yyyy-mm-dd')",
							"pu_id=" + store.get("pu_id"));
					if ("模材".equals(store.get("pu_kind"))) {
						obj = purchaseDao.getMCPurchasePrice(rs.getString("pu_vendcode"), rs.getString("pd_prodcode"),
								rs.getString("pu_currency"), rs.getInt("pd_id"), Double.parseDouble(oqty.toString()));
					} else {
						obj = purchaseDao.getPurchasePrice(rs.getString("pu_vendcode"), rs.getString("pd_prodcode"),
								rs.getString("pu_currency"), "采购", Double.parseDouble(oqty.toString()),
								DateUtil.parseDateToOracleString(Constant.YMD, (String) pu_date));
					}
					if (obj != null) {
						price = NumberUtil.formatDouble(obj.getDouble("pd_price"), 6);
						total = NumberUtil.formatDouble(qty * price, 2);
						tax = obj.getDouble("pd_rate");
						nettotal = NumberUtil.formatDouble(qty * price / (1 + tax / 100), 2);
						netprice = NumberUtil.formatDouble(price / (1 + tax / 100), 6);
						baseDao.execute("update purchasedetail set pd_price=" + price + ",pd_rate=" + obj.getDouble("pd_rate")
								+ ",pd_total=" + total + ",pd_taxtotal=" + nettotal + ",pd_netprice=" + netprice + ",pd_ppdid="
								+ obj.getDouble("pd_ppdid") + " where pd_qty-NVL(pd_beipin,0)>0 and pd_id=" + rs.getInt("pd_id"));
					} else {
						error.append("序号：[" + rs.getString("pd_detno") + "],根据 物料编号:[" + rs.getString("pd_prodcode") + "],供应商号:["
								+ rs.getString("pu_vendcode") + "],币别:[" + rs.getString("pu_currency") + "] 在物料核价单未找到对应单价，或单价为空值、0等!<BR/>");
						baseDao.execute("update purchasedetail set pd_price=0,pd_ppdid=0 where pd_id=" + rs.getInt("pd_id"));
					}
				}
			}
		}
		useDefaultTax(caller, store.get("pu_id"));
		baseDao.logger.save(caller, "pu_id", store.get("pu_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, grid });
		// 修改主单据的总金额
		getTotal(store.get("pu_id"));
		baseDao.execute("update Purchase set (pu_vendremarkcode,pu_vendremark)=(select vr_code,vr_description from (select vr_code,vr_description from vendorremark where vr_vendcode='"
				+ store.get("pu_vendcode")
				+ "' order by vr_indate desc) where rownum<2) where pu_id="
				+ store.get("pu_id")
				+ " and nvl(pu_vendremarkcode,' ')=' '");
		baseDao.execute("update Purchase set (pu_paymentsid,pu_paymentscode,pu_payments)=(select pa_id,ve_paymentcode,ve_payment from Payments left join Vendor on ve_paymentcode=pa_code where pu_vendcode=ve_code) where pu_id="
				+ store.get("pu_id") + " and nvl(pu_paymentscode,' ')=' '");
		if (baseDao.isDBSetting("Purchase", "NoSplit")) {
			baseDao.execute("update purchasedetail set pd_beipin = trunc(0.01*pd_purcqty*(select nvl(PR_BPLOSSRATE,0) from product where pr_code=pd_prodcode),(select nvl(pr_precision,0) from product where pr_code=pd_prodcode)) where pd_puid="
					+ store.get("pu_id") + "");
		}
		if (error.length() > 0) {
			BaseUtil.showErrorOnSuccess(error.toString());
		}
	}

	@Override
	public void deletePurchase(int pu_id, String caller) {
		// 只能删除在录入的采购单!
		Object status = baseDao.getFieldDataByCondition("Purchase", "pu_statuscode", "pu_id=" + pu_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { pu_id });
		baseDao.delCheck("Purchase", pu_id);
		//修改销售订单转采购单字段
		baseDao.updateByCondition("sale", "sa_turnpurchase=null", "sa_code=(select PU_SOURCECODE from purchase where pu_id=" + pu_id + ") and sa_turnpurchase='已转采购'");
		// 删除purchaseDetail
		purchaseDao.deletePurchase(pu_id);
		// 删除purchase
		baseDao.deleteById("purchase", "pu_id", pu_id);
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
		// 采购订单逻辑配置增加一个逻辑配置“供应商启用B2B收料，采购PO前缀增加 ”
		String B2BPrefix = baseDao.getDBSetting("Purchase", "VendorUseB2BAddPrefix");
		int ifdeliveryonb2b_old = -2, ifdeliveryonb2b_new = -2;
		if (B2BPrefix != null && !"".equals(B2BPrefix) && !"".equals(store.get("pu_vendcode").toString())) {
			ifdeliveryonb2b_old = Integer.parseInt(baseDao.getFieldDataByCondition("purchase left join vendor on pu_vendcode=ve_code",
					"nvl(ve_ifdeliveryonb2b,0)", "pu_id=" + store.get("pu_id")).toString());
			ifdeliveryonb2b_new = Integer.parseInt(baseDao.getFieldDataByCondition("vendor", "nvl(ve_ifdeliveryonb2b,0)",
					"ve_code='" + store.get("pu_vendcode") + "'").toString());
		}
		// 缺省应付供应商
		if (store.get("pu_receivecode") == null || store.get("pu_receivecode").toString().trim().equals("")) {
			store.put("pu_receivecode", store.get("pu_vendcode"));
			store.put("pu_receivename", store.get("pu_vendname"));
		}
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Purchase", "pu_id");
		baseDao.execute(formSql);
		if (ifdeliveryonb2b_old > -2) {
			if (ifdeliveryonb2b_old - ifdeliveryonb2b_new == -1) {// 由启用B2B收料供应商改为不启用
				baseDao.execute("update purchase set pu_code=substr(pu_code,length('" + B2BPrefix + "')+1) where pu_code like '"
						+ B2BPrefix + "%' and  pu_id=" + store.get("pu_id"));
			} else if (ifdeliveryonb2b_old - ifdeliveryonb2b_new == 1) {// 由不启用B2B收料供应商改为启用
				baseDao.execute("update purchase set pu_code=pu_code||'" + B2BPrefix + "' where pu_id=" + store.get("pu_id"));
			}
		}
		// 修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "PurchaseDetail", "pd_id");
		for (Map<Object, Object> map : gstore) {
			map.put("pd_status", "ENTERING");
			map.put("pd_auditstatus", BaseUtil.getLocalMessage("ENTERING"));
			map.put("pd_code", store.get("pu_code"));
			Object pdid = map.get("pd_id");
			if (pdid == null || pdid.equals("") || pdid.equals("0") || Integer.parseInt(pdid.toString()) == 0) {// 新添加的数据，id不存在
				if (map.get("pd_sourcedetail") != null && !"".equals(map.get("pd_sourcedetail")) && !"0".equals(map.get("pd_sourcedetail"))) {
					purchaseDao.restoreYqty(Double.parseDouble(map.get("pd_qty").toString()),
							Integer.valueOf(map.get("pd_sourcedetail").toString()));
				}
				baseDao.execute(SqlUtil.getInsertSql(map, "PURCHASEDETAIL", "pd_id"));
			} else {
				purchaseDao.restoreApplicationWithQty(Integer.parseInt(pdid.toString()), Double.parseDouble(map.get("pd_qty").toString()));
			}
		}
		baseDao.execute(gridSql);
		boolean bool = store.get("pu_getprice").toString().equals("-1");// 是否自动获取单价
		String getlastprice = baseDao.getDBSetting("Purchase", "getPriceByPurc");// 取价原则：抓取最近一次采购单单价  maz  2018060670  改成radio形式 增加取采购验收单单价
		StringBuffer error = new StringBuffer();
		JSONObject obj = null;
		SqlRowList rs = baseDao.queryForRowSet("select * from purchasedetail,purchase where pd_puid=pu_id and pd_puid="
				+ store.get("pu_id"));
		while (rs.next()) {
			if ("1".equals(getlastprice)) {
				if (rs.getGeneralDouble("pd_price") == 0) {
					SqlRowList pd = baseDao
							.queryForRowSet(
									"SELECT pd_price,pd_bgprice,pd_rate,pd_netprice FROM (select nvl(pd_price,0) pd_price,nvl(pd_bgprice,0) pd_bgprice,nvl(pd_rate,0) pd_rate,nvl(pd_netprice,0) pd_netprice "
											+ "from PurchaseDetail LEFT JOIN Purchase on pu_id=pd_puid where pd_prodcode=? and pu_currency=? and pu_vendcode=? and pu_statuscode='AUDITED' order by pu_auditdate desc) WHERE rownum<2",
									rs.getString("pd_prodcode"), rs.getString("pu_currency"), rs.getString("pu_vendcode"));
					if (pd.next()) {
						baseDao.updateByCondition(
								"PurchaseDetail",
								"pd_price=" + pd.getGeneralDouble("pd_price") + ", pd_bgprice=" + pd.getGeneralDouble("pd_bgprice")
										+ ", pd_rate=" + pd.getGeneralDouble("pd_rate") + ", pd_netprice="
										+ pd.getGeneralDouble("pd_netprice"), "pd_id=" + rs.getGeneralInt("pd_id")
										+ " and nvl(pd_price,0)=0 and pd_qty-NVL(pd_beipin,0)>0");
					}
				}
			} else if ("2".equals(getlastprice)){
				SqlRowList pd = baseDao.queryForRowSet(
						"SELECT pd_price,pd_customprice,pd_taxrate,pd_netprice FROM (select nvl(pd_orderprice,0) pd_price,nvl(pd_customprice,0) pd_customprice,nvl(pd_taxrate,0) pd_taxrate,nvl(pd_netprice,0) pd_netprice "
								+ "from ProdIODetail LEFT JOIN ProdInOut on pd_piid=pi_id where pd_prodcode=? and pi_currency=? and pi_cardcode=? and pi_statuscode='POSTED' order by pi_date desc) WHERE rownum<2",
						rs.getString("pd_prodcode"), rs.getString("pu_currency"), rs.getString("pu_vendcode"));
				if (pd.next()) {
					baseDao.updateByCondition("PurchaseDetail",
							"pd_price=" + pd.getGeneralDouble("pd_price") + ",pd_bgprice=" + pd.getGeneralDouble("pd_customprice") + ", pd_rate="
									+ pd.getGeneralDouble("pd_taxrate") + ", pd_netprice=" + pd.getGeneralDouble("pd_netprice"),
							"pd_id=" + rs.getGeneralInt("pd_id"));
				}
			} else {
				if (bool && !"B2C".equals(rs.getObject("pu_ordertype"))) {
					double price = 0;
					double total = 0;
					double tax = 0;
					double qty = Double.parseDouble(rs.getString("pd_qty"));
					double nettotal = 0;
					double netprice = 0;
					// 到物料核价单取单价
					Object oqty = baseDao.getFieldDataByCondition("PurchaseDetail", "nvl(sum(pd_qty),0)",
							" pd_puid=" + rs.getInt("pd_puid") + " and pd_prodcode='" + rs.getString("pd_prodcode") + "'");
					Object pu_date = baseDao.getFieldDataByCondition("Purchase", "to_char(pu_date,'yyyy-mm-dd')",
							"pu_id=" + store.get("pu_id"));
					if ("模材".equals(store.get("pu_kind"))) {
						obj = purchaseDao.getMCPurchasePrice(rs.getString("pu_vendcode"), rs.getString("pd_prodcode"),
								rs.getString("pu_currency"), rs.getInt("pd_id"), Double.parseDouble(oqty.toString()));
					} else {
						obj = purchaseDao.getPurchasePrice(rs.getString("pu_vendcode"), rs.getString("pd_prodcode"),
								rs.getString("pu_currency"), "采购", Double.parseDouble(oqty.toString()),
								DateUtil.parseDateToOracleString(Constant.YMD, (String) pu_date));
					}
					if (obj != null) {
						price = NumberUtil.formatDouble(obj.getDouble("pd_price"), 6);
						total = NumberUtil.formatDouble(qty * price, 2);
						tax = obj.getDouble("pd_rate");
						nettotal = NumberUtil.formatDouble(qty * price / (1 + tax / 100), 2);
						netprice = NumberUtil.formatDouble(price / (1 + tax / 100), 6);
						baseDao.execute("update purchasedetail set pd_price=" + price + ",pd_rate=" + obj.getDouble("pd_rate")
								+ ",pd_total=" + total + ",pd_taxtotal=" + nettotal + ",pd_netprice=" + netprice + ",pd_ppdid="
								+ obj.getDouble("pd_ppdid") + " where pd_qty-NVL(pd_beipin,0)>0 and pd_id=" + rs.getInt("pd_id"));
					} else {
						error.append("序号：[" + rs.getString("pd_detno") + "],根据 物料编号:[" + rs.getString("pd_prodcode") + "],供应商号:["
								+ rs.getString("pu_vendcode") + "],币别:[" + rs.getString("pu_currency") + "] 在物料核价单未找到对应单价，或单价为空值、0等!<BR/>");
						baseDao.execute("update purchasedetail set pd_price=0,pd_ppdid=0 where pd_id=" + rs.getInt("pd_id"));
					}
				}
			}
		}

		useDefaultTax(caller, store.get("pu_id"));
		// 记录操作
		baseDao.logger.update(caller, "pu_id", store.get("pu_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
		// 修改主单据的总金额
		getTotal(store.get("pu_id"));
		baseDao.execute("update Purchase set (pu_vendremarkcode,pu_vendremark)=(select vr_code,vr_description from (select vr_code,vr_description from vendorremark where vr_vendcode='"
				+ store.get("pu_vendcode")
				+ "' order by vr_indate desc) where rownum<2) where pu_id="
				+ store.get("pu_id")
				+ " and nvl(pu_vendremarkcode,' ')=' '");
		baseDao.execute("update Purchase set (pu_paymentsid,pu_paymentscode,pu_payments)=(select pa_id,ve_paymentcode,ve_payment from Payments left join Vendor on ve_paymentcode=pa_code where pu_vendcode=ve_code) where pu_id="
				+ store.get("pu_id") + " and nvl(pu_paymentscode,' ')=' '");
		if (baseDao.isDBSetting(caller, "GetB2BReserve")) {
			String msg = getGoodsReserve(Integer.valueOf(store.get("pu_id").toString()));
			if (msg != null) {
				error.append(msg);
			}
		}
		if (baseDao.isDBSetting("Purchase", "NoSplit")) {
			baseDao.execute("update purchasedetail set pd_beipin = trunc(0.01*pd_purcqty*(select nvl(PR_BPLOSSRATE,0) from product where pr_code=pd_prodcode),(select nvl(pr_precision,0) from product where pr_code=pd_prodcode)) where pd_puid="
					+ store.get("pu_id") + "");
		}
		if (error.length() > 0) {
			BaseUtil.showErrorOnSuccess(error.toString());
		}
	}

	private void getTotal(Object pu_id) {
		baseDao.execute("update PurchaseDetail set pd_bgprice=pd_price where pd_puid=" + pu_id + " and nvl(pd_bgprice,0)=0");
		/**
		 * @author wsy 双单位
		 * 
		 */
		baseDao.execute("update purchasedetail set pd_purcqty=round(pd_qty/(select case when nvl(pr_purcrate,0)=0 then 1 else pr_purcrate end from product where pr_code=pd_prodcode),8) where exists (select count(1) from product where pr_code=pd_prodcode and (nvl(pr_purcrate,0)=0 or nvl(pr_purcrate,0)=1)) and pd_puid="
				+ pu_id);

		// 判断是否有使用累计分段数量的需要重新取价
		if (baseDao.checkIf("PURCHASEDETAIL left join purchasepricedetail on ppd_id=pd_ppdid", "pd_puid=" + pu_id
				+ " and nvl(pd_ppdid,0)>0 and (nvl(ppd_accuqty,0)>0 or nvl(ppd_nextaccuqty,0)>0)")) {
			purchaseDao.getPriceByAccuqty(Integer.parseInt(pu_id.toString()));
		}

		baseDao.execute("update PurchaseDetail set pd_total=round(pd_price*(case when nvl(pd_purcqty,0)=0 then nvl(pd_qty,0) else pd_purcqty end),2) where pd_puid="
				+ pu_id);

		baseDao.execute("update Purchase set pu_total=(select sum(pd_total) from PurchaseDetail where PurchaseDetail.pd_puid = Purchase.pu_id) where pu_id="
				+ pu_id);
		baseDao.execute("update purchasedetail set pd_netprice=round(nvl(pd_price,0)/(1+nvl(pd_rate,0)/100),8) where pd_puid=" + pu_id);
		/**
		 * @author wsy 双单位
		 */
		baseDao.execute("update purchasedetail set pd_taxtotal=round(nvl(pd_netprice,0)*nvl((case when nvl(pd_purcqty,0)=0 then nvl(pd_qty,0) else pd_purcqty end),0),2) where pd_puid="
				+ pu_id);
		baseDao.execute("update Purchase set pu_taxtotal=(select sum(pd_taxtotal) from PurchaseDetail where PurchaseDetail.pd_puid = Purchase.pu_id) where pu_id="
				+ pu_id);
		baseDao.execute("update purchasedetail set pd_code=(select pu_code from purchase where pd_puid=pu_id) where pd_puid=" + pu_id
				+ " and not exists (select 1 from purchase where pd_code=pu_code)");
		baseDao.execute("update Purchase set pu_totalupper=L2U(nvl(pu_total,0)) WHERE pu_id=" + pu_id);
	}

	private void checkB2CPurchaseOrder(Object pu_id) {
		final String qtysum = "select sum((case upper(nvl(pr_unit,' ')) when 'KPCS' then pd_qty*1000 when 'KG' then pd_qty*1000 else pd_qty end) ) qty "
				+ " from purchasedetail left join  product on pd_prodcode = pr_code " + " where pd_code=?";
		if (!baseDao.checkIf("configs", "code='purchaseDataCon' and CALLER='Mall' and data<>0")) {
			BaseUtil.showError("您尚未开通开启采购互通功能，请前往数据互通设置勾选相关功能");
		}
		Object[] purchase = baseDao.getFieldsDataByCondition("purchase", new String[] { "pu_code", "pu_vendcode", "pu_pocode",
				"pu_getprice", "pu_freight" }, "pu_id = " + pu_id);
		if (String.valueOf(purchase[3]).equals("-1")) {
			BaseUtil.showError("商城类型采购单不支持自动取价");
		}

		String pu_code = String.valueOf(purchase[0]);
		String b2cpu_orderid = String.valueOf(purchase[2]);
		List<Map<String, Object>> b2cPurchaseOrderMap = baseDao
				.queryForList(
						"select b2cpu_id,b2cpu_qty,b2cpu_price,b2cpu_fare,b2cpu_taxes,b2cporderid,b2cpd_tax from b2C$purchaseorder left join b2C$purchaseorderdetail on B2CPD_PUID = B2CPU_ID where b2cpu_status = 503 and B2CPD_DETNO = 1 and b2cpu_orderid=?",
						b2cpu_orderid);
		if (!CollectionUtil.isEmpty(b2cPurchaseOrderMap)) {
			for (int i = 0; i < b2cPurchaseOrderMap.size(); i++) {
				String b2cpu_id = String.valueOf(b2cPurchaseOrderMap.get(i).get("b2cpu_id"));
				Double b2cpu_fare = Double.valueOf(String.valueOf(b2cPurchaseOrderMap.get(i).get("b2cpu_fare")));
				if (b2cpu_fare - Double.valueOf(String.valueOf(purchase[4])) != 0) {
					BaseUtil.showError("当前运费与实际对应商城订单不符，请核对后提交。");
				}
				// 更新采购单 商城订单id
				baseDao.execute("update purchase set pu_b2ccode = ? where pu_pocode = ?",
						String.valueOf(b2cPurchaseOrderMap.get(i).get("b2cporderid")), b2cpu_orderid);
				Object b2cpe_enbussinesscode = baseDao.getFieldDataByCondition("b2c$PorderEn", "b2cpe_enbussinesscode", "b2cpe_puid = "
						+ b2cpu_id);
				// 采购订单供应商编号+商城订单供应商营业执照号
				boolean checkvendor = baseDao.checkIf("vendor", "ve_webserver = '" + String.valueOf(b2cpe_enbussinesscode)
						+ "' and VE_CODE ='" + String.valueOf(purchase[1]) + "'");
				// 供应商
				if (!checkvendor) {
					BaseUtil.showError("供应商信息不符（营业执照号与商城订单【" + b2cpu_orderid + "】不一致）请核对后重新提交");
				}
				// 物料总数量
				Double qty = baseDao.queryForObject(qtysum, Double.class, pu_code);
				if ((qty - Double.valueOf(String.valueOf(b2cPurchaseOrderMap.get(i).get("b2cpu_qty")))) != 0) {
					BaseUtil.showError("物料总数量与对应商城订单【" + b2cpu_orderid + "】不符，请确认后重新提交");
				}
				// 主表价格校验
				getTotal(pu_id);
				Object[] putotalafter = baseDao.getFieldsDataByCondition("purchase", new String[] { "pu_total", "pu_taxtotal" }, "pu_id = "
						+ pu_id);
				// 采购单含税金额 = 商城采购含税金额-运费
				Double pu_total = Double.valueOf(String.valueOf(putotalafter[0]));
				Double pu_taxtotal = Double.valueOf(String.valueOf(putotalafter[1]));
				Double b2cpu_price = Double.valueOf(String.valueOf(b2cPurchaseOrderMap.get(i).get("b2cpu_price")));
				Double b2cpu_taxes = Double.valueOf(String.valueOf(b2cPurchaseOrderMap.get(i).get("b2cpu_taxes")));
				Double b2cpd_tax = Double.valueOf(String.valueOf(b2cPurchaseOrderMap.get(i).get("b2cpd_tax")));
				Double b2cpd_taxprice = (b2cpu_price - b2cpu_fare) / (1 + b2cpd_tax * 0.01);
				DecimalFormat fnum = new DecimalFormat("##0.00");
				String dd = fnum.format(b2cpd_taxprice);
				if ((b2cpu_price - b2cpu_fare - pu_total) != 0) {
					BaseUtil.showError("单据含税金额与对应商城订单【" + b2cpu_orderid + "】不符，请确认后重新提交");
				}
				// 采购单不含税金额 = 商城采购含税金额-运费-税金
				if ((Double.valueOf(fnum.format(b2cpd_taxprice)) - pu_taxtotal) != 0) {
					BaseUtil.showError("单据不含税金额与对应商城订单【" + b2cpu_orderid + "】不符，或因税率不符，请确认后重新提交");
				}
			}
		} else {
			BaseUtil.showError("未查询到您关联的商城订单【" + b2cpu_orderid + "】或您没有开启采购互通功能");
		}

		// 根据 原厂型号、品牌进行分组 ，比对可以匹配的物料
		// BaseUtil.showErrorOnSuccess("商城订单未同步或不存在或您没有开启采购互通功能");
	}

	@Override
	public String[] printPurchase(int pu_id, String caller, String reportName, String condition) {
		// 判断已审核才允许打印
		if (baseDao.isDBSetting(caller, "printNeedAudit")) {
			String status = baseDao.getFieldValue("Purchase", "pu_statuscode", "pu_id=" + pu_id, String.class);
			StateAssert.printOnlyAudited(status);
		}
		getTotal(pu_id);
		// 执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[] { pu_id });
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 修改打印状态
		baseDao.print("Purchase", "pu_id=" + pu_id, "pu_printstatus", "pu_printstatuscode");
		// 记录操作
		baseDao.logger.print(caller, "pu_id", pu_id);
		// 记录打印次数
		baseDao.updateByCondition("Purchase", "pu_count=nvl(pu_count,0)+1", "pu_id=" + pu_id);
		// 执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[] { pu_id });
		return keys;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void auditPurchase(int pu_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("Purchase", new String[] { "pu_statuscode", "pu_vendcode", "pu_ordertype" },
				"pu_id=" + pu_id);
		StateAssert.auditOnlyCommited(status[0]);
		allowZeroTax(caller, pu_id);
		baseDao.execute("update Purchase set (pu_vendremarkcode,pu_vendremark)=(select vr_code,vr_description from (select vr_code,vr_description from vendorremark where vr_vendcode='"
				+ status[1] + "' order by vr_indate desc) where rownum<2) where pu_id=" + pu_id + " and nvl(pu_vendremarkcode,' ')=' '");
		baseDao.execute("update Purchase set (pu_paymentsid,pu_paymentscode,pu_payments)=(select pa_id,ve_paymentcode,ve_payment from Payments left join Vendor on ve_paymentcode=pa_code where pu_vendcode=ve_code) where pu_id="
				+ pu_id + " and nvl(pu_paymentscode,' ')=' '");
		baseDao.execute("update Purchasedetail a set pd_prattach=(select pr_attach from product where pr_code=a.pd_prodcode) where pd_puid="
				+ pu_id);

		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pd_detno) from purchasedetail left join product on pd_prodcode=pr_code where round(pd_qty,nvl(pr_precision,0))<>pd_qty and pd_puid="
								+ pu_id, String.class);
		if (dets != null) {
			BaseUtil.showError("当前物料采购数量不符合物料精度，请修改！序号：" + dets);
		}
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { pu_id });
		// 执行审核操作
		baseDao.audit("Purchase", "pu_id=" + pu_id, "pu_status", "pu_statuscode", "pu_auditdate", "pu_auditman");
		baseDao.audit("PurchaseDetail", "pd_puid=" + pu_id, "pd_auditstatus", "pd_status");
		// 临时供应商审核PO后失效采购定价
		Object[] obj = baseDao.getFieldsDataByCondition("Purchase,vendor", new String[] { "pu_vendcode", "ve_style", "pu_code" }, "pu_id="
				+ pu_id + " and pu_vendcode=ve_code");
		if (obj[1] != null && (obj[1].equals("临时") || obj[1].equals("TEMP"))) {
			baseDao.execute("update purchasepricedetail set ppd_unvaliddate=sysdate,ppd_status='无效',ppd_statuscode='UNVALID',ppd_remark=to_char(sysdate,'yyyy-mm-dd HH24:mi:ss')||'临时供应商失效定价，采购单号"
					+ obj[2]
					+ "' where ppd_vendcode='"
					+ obj[0]
					+ "' and ppd_status='有效' and ppd_prodcode  in (select pd_prodcode from purchasedetail where pd_puid=" + pu_id + ")");
		}
		// 记录操作
		baseDao.logger.audit(caller, "pu_id", pu_id);
		/**
		 * @author wsy 反馈编号：2017070251 采购单审核 自动生成物料供应商资料表
		 */
		Object pv_detno = baseDao.getFieldDataByCondition("ProductVendor", "nvl(max(pv_detno),0)",
				"pv_vendcode=(select pu_vendcode from Purchase where pu_id=" + pu_id + ")");
		int detno = Integer.parseInt(pv_detno.toString());
		String sql2 = "select DISTINCT ve_id,pr_id,pd_prodvendcode,pr_unit,pu_vendcode,pu_vendname,pd_prodcode,pr_detail from purchase left join purchasedetail  on pd_puid=pu_id left join vendor on pu_vendcode=ve_code left join Product on pd_prodcode=pr_code where pu_id='"
				+ pu_id
				+ "' and nvl(pd_prodvendcode,' ')<>' 'and not exists (select 1 from ProductVendor where pv_vendcode=pu_vendcode and pv_vendprodcode=pd_prodvendcode and pv_prodcode=pd_prodcode)";
		SqlRowList rs1 = baseDao.queryForRowSet(sql2);
		while (rs1.next()) {
			baseDao.execute("insert into ProductVendor(pv_id,pv_vendid,pv_detno,pv_prodid,pv_vendprodcode,"
					+ "pv_vendcode,pv_vendname,pv_prodcode) " + "values(ProductVendor_seq.nextval," + rs1.getString("ve_id") + ","
					+ (++detno) + ",'" + rs1.getString("pr_id") + "'" + ",'" + rs1.getString("pd_prodvendcode") + "','"
					+ rs1.getString("pu_vendcode") + "','" + rs1.getString("pu_vendname") + "','" + rs1.getString("pd_prodcode") + "')");
		}
		/**
		 * @author wsy 反馈编号：2017040088 审核时将当前日期更新到对应供应商资料中最近交易日期
		 */
		baseDao.updateByCondition("Vendor", "ve_transdate=sysdate", "ve_code='" + status[1] + "'");
		String querySql = "select count(1) from purchase where pu_vendcode=(select pu_vendcode from purchase where pu_id=" + pu_id
				+ ") and nvl(pu_no,0)>0";
		int count = baseDao.getCount(querySql) + 1;
		baseDao.updateByCondition("purchase", "pu_sendstatus='待上传',pu_no=" + count, "pu_id=" + pu_id);
		int creatbill = baseDao.getCountByCondition("Purchase left join purchasekind on pu_kind=pk_name", "pu_id=" + pu_id
				+ " and nvl(pk_createbill,0)=1");
		int bill = baseDao.getCountByCondition("APBILL", "ab_sourceid=" + pu_id + " and ab_source='采购单'");
		if (bill == 0) {
			if (creatbill > 0) {
				Key key = transferRepository.transfer("Purchase!ToAPBill", pu_id);
				// 转入明细
				transferRepository.transferDetail("Purchase!ToAPBill", pu_id, key);
				baseDao.execute("update apbilldetail set abd_code=(select ab_code from apbill where abd_abid=ab_id) where abd_abid="
						+ key.getId() + " and not exists (select 1 from apbill where abd_code=ab_code)");
				baseDao.execute("UPDATE apbilldetail SET abd_thisvoprice=abd_price WHERE abd_abid=" + key.getId()
						+ " and nvl(abd_thisvoprice,0)=0");
				baseDao.execute("UPDATE apbilldetail SET abd_qty=abd_thisvoqty WHERE abd_abid=" + key.getId() + " and nvl(abd_qty,0)=0");
				baseDao.execute("UPDATE apbilldetail SET abd_apamount=round(abd_thisvoprice*abd_qty,2) WHERE abd_abid=" + key.getId());
				baseDao.execute("UPDATE apbilldetail SET abd_noapamount=round(abd_qty*abd_thisvoprice/(1+abd_taxrate/100),2) WHERE abd_abid="
						+ key.getId());
				baseDao.execute("UPDATE apbilldetail SET abd_taxamount=round((abd_qty*abd_thisvoprice*abd_taxrate/100)/(1+abd_taxrate/100),2) WHERE abd_abid="
						+ key.getId());
				baseDao.execute("update apbill set ab_taxsum=(select sum(round(((abd_thisvoprice*abd_qty*abd_taxrate/100)/(1+abd_taxrate/100)),2)) from apbilldetail where abd_abid="
						+ key.getId() + ")+nvl(ab_differ,0) where ab_id=" + key.getId());
				baseDao.execute("update apbill set ab_apamount=round(nvl((select sum(abd_apamount) from apbilldetail where abd_abid="
						+ key.getId() + "),0),2) where ab_id=" + key.getId());
				baseDao.execute("update purchasedetail set pd_mrpstatuscode='FINISH',pd_mrpstatus='已结案',pd_enddate=sysdate where pd_puid="
						+ pu_id);
				baseDao.execute("update purchase set pu_statuscode='FINISH',pu_status='已结案',pu_enddate=sysdate where pu_id=" + pu_id);

			}
		}
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { pu_id });
		if (baseDao.isDBSetting(caller, "AuditedAutoEmail")) {
			if (baseDao.isDBSetting(caller, "NoUseB2B")) {
				purchase_audit_sendMail(pu_id);
				baseDao.logger.others("邮件发送", "发送邮件成功", caller, "pu_id", pu_id);
			} else {
				// 过滤掉未维护UU号，并且未验证的供应商
				int countnum = baseDao
						.getCount("select count(*) from vendor where nvl(ve_uu,' ')<>' ' and nvl(ve_b2benable,0)=1 and ve_code=(select pu_vendcode from purchase where pu_id="
								+ pu_id + ")");
				if (countnum == 1) {
					purchase_audit_sendMail(pu_id);
					baseDao.logger.others("邮件发送", "发送邮件成功", caller, "pu_id", pu_id);
				}
			}
		}
		baseDao.auditInsertTaskman("PurchaseReply", pu_id);
		baseDao.auditInsertTaskman("NOPurchaseReply", pu_id);
		// 是否配置自动生成送货提醒，默认为否 与手动投放送货提醒逻辑一致PurchaseNotify
		if (baseDao.isDBSetting(caller, "AuditedAutoSendRemind")) {
			if (status[1] != null) {
				Object ve_ifdeliveryonb2b = baseDao.getFieldDataByCondition("vendor", "ve_ifdeliveryonb2b",
						"ve_code='" + status[1].toString() + "'");
				if (ve_ifdeliveryonb2b != null && "-1".equals(ve_ifdeliveryonb2b.toString())) {
					purchase_audit_sendRemind(pu_id);
				}
			}
		}
		// 更新物料资料中的最近采购日期
		baseDao.execute("update product set pr_recentpurcdate=(select pu_date from purchase where pu_id=" + pu_id
				+ ") where exists (select 1 from purchasedetail where pd_prodcode=pr_code and pd_puid=" + pu_id + ")");
		// 审核自动将平台采购数据传送至器件库
		// sendToB2CPlatform(pu_id);
		// 审核后商城类型订单自动生成预付款申请单
		if (String.valueOf(status[2]).toUpperCase().equals("B2C")) {
			boolean autoTurn = baseDao.isDBSetting("B2CSetting", "autoTurnToPayPleaseNew");
			if (!autoTurn) {
				purchaseOrderService.createprepay(pu_id);
			}
		}
	}

	@Override
	public void resAuditPurchase(int pu_id, String caller) {
		SqlRowList rs = baseDao.queryForRowSet("select pd_id from purchasedetail where pd_puid=?", pu_id);
		while (rs.next()) {
			purchaseDao.udpatestatus(rs.getGeneralInt("pd_id"));
			purchaseDao.udpateturnstatus(rs.getGeneralInt("pd_id"));
		}
		// 只能对状态为[已审核]的采购单进行反审核操作!
		Object[] objs = baseDao.getFieldsDataByCondition("Purchase", new String[] { "pu_statuscode", "pu_turnstatuscode",
				"pu_acceptstatuscode", "pu_ordertype" }, "pu_id=" + pu_id);
		if (objs[1] != null || objs[2] != null || !"AUDITED".equals(objs[0])) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.purchase.resAudit_onlyAudit"));
		}
		// 判断该单据上是否上传到B2B，已上传，则不允许反审核，需要变更的话，走变更单流程
		String sendStatus = baseDao.getFieldValue("Purchase", "pu_sendstatus", "pu_id=" + pu_id, String.class);
		StateAssert.onSendingLimit(sendStatus);
		StateAssert.onSendedLimit(sendStatus);
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(pd_detno) from Purchasedetail where pd_puid=? and nvl(pd_yqty,0)>0 ", String.class, pu_id);
		if (dets != null) {
			BaseUtil.showError("已转其它单据，不允许反审核!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pd_detno) from PurchaseDetail where nvl(pd_mrpstatuscode, ' ') in ('FINISH','FREEZE','NULLIFIED') and pd_puid=?",
						String.class, pu_id);
		if (dets != null) {
			BaseUtil.showError("明细行已结案、已冻结、已作废，不允许反审核!行号：" + dets);
		}
		// 已经生成送货提醒单不允许反审核
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pd_detno) from purchasedetail where pd_puid=? and exists(select 1 from purchaseNotify where pn_pdid=pd_id and (pn_statuscode<>'CANCELED' OR nvl(pn_endqty,0)>0))",
						String.class, pu_id);
		if (dets != null) {
			BaseUtil.showError("明细行已经生成有效的送货提醒不允许反审核!行号：" + dets);
		}

		// 已经生成银行登记转存单不允许反审核
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(ar_code) from AccountRegister where ar_type = '转存' and nvl(ar_source,' ') = (select pu_code from Purchase where pu_id = ?)",
						String.class, pu_id);
		if (dets != null) {
			BaseUtil.showError("已经生成银行登记转存单不允许反审核!单号：" + dets);
		}
		handlerService.beforeResAudit(caller, new Object[] { pu_id });
		baseDao.resAuditCheck("Purchase", pu_id);
		// 执行反审核操作
		baseDao.resAudit("Purchase", "pu_id=" + pu_id, "pu_status", "pu_statuscode", "pu_auditdate", "pu_auditman");
		baseDao.resOperate("PurchaseDetail", "pd_puid=" + pu_id, "pd_auditstatus", "pd_status");
		// 记录操作
		baseDao.logger.resAudit(caller, "pu_id", pu_id);
		handlerService.afterResAudit(caller, new Object[] { pu_id });
	}

	@Override
	public void submitPurchase(int pu_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("Purchase", new String[] { "pu_statuscode", "pu_vendcode", "pu_currency",
				"pu_date", "pu_ordertype" }, "pu_id=" + pu_id);
		StateAssert.submitOnlyEntering(status[0]);
		if (StringUtil.hasText(status[2])) {
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select WM_CONCAT(pu_code) from Purchase,vendor where pu_id=? and ve_code=pu_receivecode and nvl(ve_onecurr,0)<>0 and nvl(pu_currency,' ')<>nvl(ve_currency,' ')",
							String.class, pu_id);
			if (dets != null) {
				BaseUtil.showError("以非应付供应商资料定义的币别下达采购，不允许提交!");
			}
			int count = baseDao
					.getCount("select count(*) from currencysmonth,purchase where cm_crname=pu_currency and cm_yearmonth=to_char(pu_date,'yyyymm') and pu_id="
							+ pu_id);
			if (count == 0) {
				BaseUtil.showError("币别[" + status[2] + "]未设置月度汇率，不能提交！");
			}
		} else {
			BaseUtil.showError("采购单币别为空，不能提交！");
		}
		// 判断明细行物料是否有未审核的，有未审核不让提交
		String selectSQL = "select pd_detno,pd_prodcode,pr_status,pr_code from purchasedetail left join product on pr_code=pd_prodcode where pd_puid="
				+ pu_id + " and NVL(pr_statuscode,' ')<>'AUDITED' ";
		SqlRowList rs = baseDao.queryForRowSet(selectSQL);
		int detno = 0;
		while (rs.next()) {
			detno = rs.getInt("pd_detno");
			if (rs.getObject("pr_code") != null) {
				BaseUtil.showError("序号" + String.valueOf(detno) + "物料未审核，不能提交！");
				return;
			} else {
				BaseUtil.showError("序号" + String.valueOf(detno) + "物料不存在，不能提交！");
				return;
			}
		}
		/*
		 * String dets = baseDao.getJdbcTemplate().queryForObject(
		 * "select wmsys.wm_concat(pd_detno) from purchasedetail left join product on pd_prodcode=pr_code  where pd_puid ="
		 * + pu_id + " and pr_supplytype='VIRTUAL' ", String.class); if (dets !=
		 * null) { BaseUtil.showError("序号" + dets + "物料为虚拟件，不能下达请购！"); return; }
		 */
		// 应付供应商为空时，抓供应商资料的缺省值
		baseDao.execute("UPDATE Purchase SET (pu_receivecode,pu_receivename)=(SELECT nvl(ve_apvendcode,ve_code),nvl(ve_apvendname,ve_name) FROM Vendor WHERE ve_code=pu_vendcode) WHERE pu_id="
				+ pu_id + " AND NVL(pu_receivecode,' ')=' '");
		baseDao.execute("update Purchase set pu_vendcode=upper(ltrim(rtrim(pu_vendcode))) where pu_id=" + pu_id);
		baseDao.execute("update PurchaseDetail set pd_prodcode=upper(ltrim(rtrim(pd_prodcode))) where pd_puid=" + pu_id);
		// 供应商是否存在
		rs = baseDao.queryForRowSet("SELECT pu_vendcode FROM Purchase WHERE pu_id=? AND pu_vendcode not in (SELECT ve_code FROM Vendor)",
				pu_id);
		if (rs.next() && rs.getString("pu_vendcode") != null) {
			BaseUtil.showError(BaseUtil.getLocalMessage("vendor_not_exist") + "<br>" + rs.getString("pu_vendcode"));
		}
		// 只能选择已审核的供应商!
		Object code = baseDao.getFieldDataByCondition("Purchase", "pu_vendcode", "pu_id=" + pu_id);
		Object auditstatus = baseDao.getFieldDataByCondition("Vendor", "ve_auditstatuscode", "ve_code='" + code + "'");
		if (!"AUDITED".equals(auditstatus)) {
			BaseUtil.showError(BaseUtil.getLocalMessage("vendor_onlyAudited")
					+ "<a href=\"javascript:openUrl('jsps/scm/purchase/vendor.jsp?formCondition=ve_codeIS" + code + "')\">" + code
					+ "</a>&nbsp;");
		}
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(pd_detno) from PurchaseDetail where to_char(pd_delivery,'yyyymmdd')<to_char(sysdate,'yyyymmdd') and pd_puid=?",
						String.class, pu_id);
		if (dets != null) {
			BaseUtil.showError("需求日期小于系统日期，不允许提交!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pd_detno) from purchasedetail left join product on pd_prodcode=pr_code where round(pd_qty,nvl(pr_precision,0))<>pd_qty and pd_puid="
								+ pu_id, String.class);
		if (dets != null) {
			BaseUtil.showError("当前物料采购数量不符合物料精度，请修改！序号：" + dets);
		}
		baseDao.execute("update vendor a set ve_apvendname=(select ve_name from vendor where a.ve_apvendcode=ve_code) where ve_code='"
				+ code + "' and nvl(ve_apvendname,' ')=' '");
		baseDao.execute("update Purchase set (pu_vendremarkcode,pu_vendremark)=(select vr_code,vr_description from (select vr_code,vr_description from vendorremark where vr_vendcode='"
				+ status[1] + "' order by vr_indate desc) where rownum<2) where pu_id=" + pu_id + " and nvl(pu_vendremarkcode,' ')=' '");
		baseDao.execute("update Purchase set (pu_paymentsid,pu_paymentscode,pu_payments)=(select pa_id,ve_paymentcode,ve_payment from Payments left join Vendor on ve_paymentcode=pa_code where pu_vendcode=ve_code) where pu_id="
				+ pu_id + " and nvl(pu_paymentscode,' ')=' '");
		// 订单类型为B2C时核对订单信息
		String ordertype = String.valueOf(status[4]);
		if (ordertype.toUpperCase().equals("B2C")) {
			String pu_pocode = baseDao.queryForObject("select pu_pocode from purchase where pu_id = ?", String.class, pu_id);
			if (pu_pocode == null) {
				BaseUtil.showError("商城类型采购单需补齐商城单号！");
			}
			checkB2CPurchaseOrder(pu_id);
		}
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { pu_id });
		// 13-11-18 根据采购类型表中设置的是否允许采购单价为空的情况
		// 判断单价是否为空（purchasekind.pk_allownullprice）
		Object allownullprice = baseDao.getFieldDataByCondition("purchase left join purchasekind on pu_kind=pk_name", "PK_ALLOWNULLPRICE",
				" pu_kind is not null and pu_id=" + pu_id);
		if (allownullprice != null && allownullprice.toString().equals("0")) {
			String detnos = baseDao.getJdbcTemplate().queryForObject(
					"select WM_CONCAT(pd_detno) from PurchaseDetail where pd_qty-NVL(pd_beipin,0)>0 and nvl(PD_PRICE,0)=0 and pd_puid=?",
					String.class, pu_id);
			if (detnos != null) {
				BaseUtil.showError("行号：" + detnos + " 的单价为0,不允许提交!");
			}
		}
		useDefaultTax(caller, pu_id);
		allowZeroTax(caller, pu_id);
		// 执行提交操作
		baseDao.submit("Purchase", "pu_id=" + pu_id, "pu_status", "pu_statuscode");
		baseDao.submit("PurchaseDetail", "pd_puid=" + pu_id, "pd_auditstatus", "pd_status");
		// 记录操作
		baseDao.logger.submit(caller, "pu_id", pu_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { pu_id });
		getTotal(pu_id);
		if (baseDao.isDBSetting("Purchase", "NoSplit")) {
			baseDao.execute("update purchasedetail set pd_beipin = trunc(0.01*pd_purcqty*(select nvl(PR_BPLOSSRATE,0) from product where pr_code=pd_prodcode),(select nvl(pr_precision,0) from product where pr_code=pd_prodcode)) where pd_puid="
					+ pu_id + "");
		}
		if (baseDao.isDBSetting(caller, "GetB2BReserve")) {
			String msg = getGoodsReserve(pu_id);
			if (msg != null) {
				BaseUtil.showErrorOnSuccess(msg);
			}
		}
		if (baseDao.isDBSetting(caller, "VendorUUUncheckRemind")) {
			int count = baseDao.getCount("select count(1) from vendor where ve_code='" + status[1]
					+ "' and ve_uu is not null and nvl(ve_b2benable,0)=0");
			if (count > 0) {
				BaseUtil.showErrorOnSuccess("当前供应商资料中维护了UU号，但是B2B检验=否，如需开通B2B平台业务，请点击按钮【供应商UU设置】");
			}
		}
	}

	@Override
	public void resSubmitPurchase(int pu_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Purchase", "pu_statuscode", "pu_id=" + pu_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { pu_id });
		// 执行反提交操作
		baseDao.resOperate("Purchase", "pu_id=" + pu_id, "pu_status", "pu_statuscode");
		baseDao.resOperate("PurchaseDetail", "pd_puid=" + pu_id, "pd_auditstatus", "pd_status");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pu_id", pu_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { pu_id });
	}

	public void getPrice(int pu_id) {
		purchaseDao.getPrice(pu_id);
	}

	public void getStandardPrice(int pu_id) {
		baseDao.execute(
				"update PurchaseDetail set pd_price=(select pr_standardprice from product where pd_prodcode=pr_code) where pd_puid=?",
				pu_id);
	}

	@Override
	public void vastDeletePurc(int[] id, String caller) {
		SqlRowList rs = baseDao.queryForRowSet("SELECT pd_code,pd_detno,pd_yqty,pd_acceptqty,pd_puid FROM PurchaseDetail WHERE pd_puid in("
				+ BaseUtil.parseArray2Str(NumberUtil.toIntegerArray(id), ",") + ")");
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			if (rs.getDouble("pd_yqty") > 0 || rs.getDouble("pd_acceptqty") > 0) {
				sb.append("采购单号[");
				sb.append(rs.getObject("pd_code"));
				sb.append("],序号[");
				sb.append(rs.getInt("pd_detno"));
				sb.append("]中已转出或者已验收，不允许删除！");
			} else {
				baseDao.deleteByCondition("Purchase", "pu_id=" + rs.getInt("pd_puid"));
				baseDao.deleteByCondition("PurchaseDetail", "pd_puid=" + rs.getInt("pd_puid"));
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showErrorOnSuccess(sb.toString());
		}
	}

	@Override
	public JSONObject copyPurchase(int id, String caller) {
		Employee employee = SystemSession.getUser();
		Map<String, Object> dif = new HashMap<String, Object>();
		// Copy Purcahse
		int nId = baseDao.getSeqId("PURCHASE_SEQ");
		dif.put("pu_id", nId);
		dif.put("pu_date", "sysdate");
		dif.put("pu_indate", "sysdate");
		String code = baseDao.sGetMaxNumber("Purcahse", 2);
		dif.put("pu_code", "'" + code + "'");
		dif.put("pu_recordid", employee.getEm_id());
		dif.put("pu_recordman", "'" + employee.getEm_name() + "'");
		dif.put("pu_status", "'" + BaseUtil.getLocalMessage("ENTERING") + "'");
		dif.put("pu_statuscode", "'ENTERING'");
		dif.put("pu_auditman", "null");
		dif.put("pu_auditdate", "null");
		dif.put("pu_turnstatus", "null");
		dif.put("pu_turnstatuscode", "null");
		dif.put("pu_acceptstatus", "null");
		dif.put("pu_acceptstatuscode", "null");
		dif.put("pu_printstatus", "null");
		baseDao.copyRecord("Purcahse", "Purcahse", "pu_id=" + id, dif);
		// Copy PurcahseDetail
		dif = new HashMap<String, Object>();
		dif.put("pd_id", "purchasedetail_seq.nextval");
		dif.put("pd_puid", nId);
		dif.put("pd_yqty", 0);
		dif.put("pd_acceptqty", 0);
		dif.put("pd_ngacceptqty", 0);
		dif.put("pd_backqty", 0);
		dif.put("pd_source", "null");
		dif.put("pd_sourcecode", "null");
		dif.put("pd_sourcedetail", 0);
		dif.put("pd_mrpstatus", "null");
		dif.put("pd_mrpstatuscode", "null");
		dif.put("pd_enddate", "null");
		dif.put("pd_b2cbatchcode", "null");
		dif.put("pd_b2ccode", "null");
		dif.put("pd_enddate", "null");
		dif.put("pd_turnqty", 0);
		dif.put("pd_frozenqty", 0);
		dif.put("pd_textbox", "null");
		dif.put("pd_lastacceptdate", "null");
		dif.put("pd_originaldetno", 0);
		dif.put("pd_originalqty", 0);
		dif.put("pd_mmid", 0);
		dif.put("pd_mtid", 0);
		dif.put("pd_code", "'" + code + "'");
		baseDao.copyRecord("PurcahseDetail", "PurcahseDetail", "pd_puid=" + id, dif);
		JSONObject obj = new JSONObject();
		obj.put("id", nId);
		obj.put("code", code);
		return obj;
	}

	@Override
	public void getMakeVendorPrice(int ma_id, String caller) {
		String vendcode = "", currency = "";
		double taxrate = 0;
		SqlRowList rs = baseDao.queryForRowSet("select * from Make where  ma_id=" + ma_id + " and ma_tasktype='OS' ");
		if (rs.next()) {
			if (rs.getString("ma_statuscode").equals("FINISH")) {
				BaseUtil.showError("已经结案工单不能更新委外商");
			}
			if (rs.getObject("ma_madeqty") != null && rs.getDouble("ma_madeqty") > 0) {
				BaseUtil.showError("已有验收数量的委外单不能更新委外商信息");
			}
			// 到物料核价单取单价
			JSONObject obj = null;
			obj = purchaseDao.getPriceVendor(rs.getString("ma_prodcode"), "委外", rs.getDouble("ma_qty"));
			if (obj != null) {
				double price = obj.getDouble("price");
				vendcode = obj.getString("vendcode");
				currency = obj.getString("currency");
				taxrate = obj.getDouble("taxrate");
				baseDao.updateByCondition("Make", "ma_vendcode='" + vendcode + "', ma_currency='" + currency + "',ma_taxrate= " + taxrate
						+ ", ma_price=round(" + price + ",8), ma_total=round(" + price + "*ma_qty,2) ", "ma_id =" + ma_id);
				baseDao.execute("update make set (ma_paymentscode,ma_payments,ma_vendname,ma_rate)=(select ve_paymentcode,ve_payment,ve_name,cm_crrate "
						+ "from vendor left join currencysmonth on cm_crname=ve_currency where ve_code=ma_vendcode and cm_yearmonth=to_char(ma_date,'yyyymm') ) where ma_id="
						+ ma_id);
				int argCount = baseDao.getCountByCondition("user_tab_columns",
						"table_name='MAKE' and column_name in ('MA_APVENDCODE','MA_APVENDNAME')");
				if (argCount == 2) {
					baseDao.execute("update make set (MA_APVENDCODE,MA_APVENDNAME)=(select ve_apvendcode,ve_apvendname from vendor where ve_code=ma_vendcode) where ma_id="
							+ ma_id);
				}
				baseDao.execute("update make set ma_pricetype='取最低价' where ma_id=" + ma_id);
				// 记录操作
				baseDao.logger.others("委外信息变更-取最低价", "msg.saveSuccess", "Make", "ma_id", ma_id);
			}
		}
	}

	@Override
	public void getVendorPrice(int ma_id, String vendcode, String curr, String caller) {
		double taxrate = 0;
		SqlRowList rs = baseDao.queryForRowSet("select * from Make where  ma_id=" + ma_id + " and ma_tasktype='OS' ");
		if (rs.next()) {
			if (rs.getString("ma_statuscode").equals("FINISH")) {
				BaseUtil.showError("已经结案工单不能更新委外商");
			}
			if (rs.getObject("ma_madeqty") != null && rs.getDouble("ma_madeqty") > 0) {
				BaseUtil.showError("已有验收数量的委外单不能更新委外商信息");
			}
			// 到物料核价单取单价
			JSONObject obj = null;
			obj = purchaseDao.getPurchasePrice(vendcode, rs.getString("ma_prodcode"), curr, "委外", rs.getDouble("ma_qty"), "sysdate");
			if (obj == null) {
				BaseUtil.showError("当前供应商获取不到有效单价！");
			} else {
				double price = obj.getDouble("pd_price");
				taxrate = obj.getDouble("pd_rate");
				baseDao.updateByCondition("Make", "ma_vendcode='" + vendcode + "', ma_currency='" + curr + "',ma_taxrate= " + taxrate
						+ ", ma_price=round(" + price + ",8), ma_total=round(" + price + "*ma_qty,2) ", "ma_id =" + ma_id);
				baseDao.execute("update make set (ma_paymentscode,ma_payments,ma_vendname,ma_rate)=(select ve_paymentcode,ve_payment,ve_name,cm_crrate from vendor left join currencysmonth on cm_crname=ve_currency where ve_code=ma_vendcode and cm_yearmonth=to_char(ma_date,'yyyymm')) where ma_id="
						+ ma_id);
				int argCount = baseDao.getCountByCondition("user_tab_columns",
						"table_name='MAKE' and column_name in ('MA_APVENDCODE','MA_APVENDNAME')");
				if (argCount == 2) {
					baseDao.execute("update make set (MA_APVENDCODE,MA_APVENDNAME)=(select ve_apvendcode,ve_apvendname from vendor where ve_code=ma_vendcode) where ma_id="
							+ ma_id);
				}
				baseDao.execute("update make set ma_pricetype='按供应商取价' where ma_id=" + ma_id);
				// 记录操作
				baseDao.logger.others("委外信息变更-按供应商取价", "msg.saveSuccess", "Make", "ma_id", ma_id);
			}
		}
	}

	@Override
	public void syncPurc(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String ids = BaseUtil.parseArray2Str(CollectionUtil.pluck(maps, "pu_id"), ",");
		SqlRowList rs = baseDao.queryForRowSet("select pu_id from purchase where pu_id in (" + ids
				+ ") and pu_statuscode='AUDITED' and pu_receivecode='02.01.028' and nvl(pu_sync,' ')=' '");
		while (rs.next()) {
			purchaseDao.syncPurcToSqlServer(rs.getInt(1));
		}
	}

	@Override
	public void updateVendorBackInfo(String data, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(data);
		Object[] datas = baseDao.getFieldsDataByCondition("Purchasedetail", new String[] { "pd_detno", "pd_qty", "pd_puid", "pd_qtyreply",
				"pd_code" }, "pd_id=" + store.get("pd_id"));
		/*
		 * Double replyqty = 0.0; if (datas[3] != null) { replyqty =
		 * Double.parseDouble(datas[3].toString()); } else { replyqty = 0.0; }
		 */
		/*
		 * 2016-12-22 dyl 回复数量不做累加处理
		 */
		boolean bool = Double.parseDouble(datas[1].toString()) < Double.parseDouble(store.get("pd_qtyreply").toString());// +
																															// replyqty;
		if (bool)
			BaseUtil.showError("回复数量不能大于采购数!");
		baseDao.execute("update purchasedetail set pd_qtyreply=" + store.get("pd_qtyreply") + ",pd_isok='" + store.get("pd_isok")
				+ "',pd_deliveryreply='" + store.get("pd_deliveryreply") + "',pd_replydetail='" + store.get("pd_replydetail")
				+ "' where pd_id=" + store.get("pd_id"));
		int prid = baseDao.getSeqId("PurchaseReply_seq");
		baseDao.execute("insert into purchaseReply (pr_qty,pr_delivery,pr_date,pr_recorder,pr_pucode,pr_pddetno,pr_remark,pr_sendstatus,pr_type,pr_id)values("
				+ store.get("pd_qtyreply")
				+ ",to_date('"
				+ store.get("pd_deliveryreply")
				+ "','yyyy-mm-dd'),sysdate,'"
				+ SystemSession.getUser().getEm_name()
				+ "','"
				+ datas[4]
				+ "',"
				+ datas[0]
				+ ",'"
				+ store.get("pd_replydetail")
				+ "','待上传','采购主动回复'," + prid + ")");
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "更新供应商回复信息", "更新成功,序号:" + datas[0], "Purchase|pu_id="
				+ datas[2]));
	}

	@Override
	public void resetSyncStatus(String caller, Integer id) {
		purchaseDao.resetPurcSyncStatus(id);
	}

	@Override
	public void refreshqty(Integer id, String caller) {
		// 已收料数量等于收料单总数sum（vad_qty）-PO退料数pd_backqty（验退和不良出）
		baseDao.execute("update purchasedetail set pd_yqty=NVL((select NVL(sum(vad_qty),0) from verifyapplydetail where vad_pucode=pd_code and vad_pudetno=pd_detno and vad_class='采购入库申请单'),0)-NVL(pd_backqty,0) where pd_puid="
				+ id);
		baseDao.execute("update purchasedetail set pd_yqty=NVL(pd_acceptqty,0)+NVL(pd_ngacceptqty,0) where pd_puid=" + id
				+ " and NVL(pd_acceptqty,0)+NVL(pd_ngacceptqty,0)>NVL(pd_yqty,0)");
	}

	@Override
	public void purchasedataupdate(int id, String caller) {
		String sqlstrb = "update purchasedetail PDD set PDD.pd_b=nvl((select sum(PDS.pd_qty-nvl(PDS.pd_acceptqty,0)) from Purchase,PurchaseDetail PDS where pu_id=PDS.pd_puid and PDS.pd_prodcode=PDD.pd_prodcode and nvl(pu_statuscode,' ')<>'FINISH'  and nvl(pu_statuscode,' ')<>'ENTERING' and nvl(PDS.pd_mrpstatuscode,' ')<>'FINISH'),0) where pd_puid="
				+ id;
		String sqlstrc = "update purchasedetail set pd_c=NVL((select sum(pw_onhand) from productwh where pw_prodcode=pd_prodcode),0) where pd_puid="
				+ id;
		String sqlstrd = "update purchasedetail set pd_d=NVL((select sum(ad_qty-nvl(ad_yqty,0)) from application,applicationdetail where ap_id=ad_apid and ad_prodcode=pd_prodcode and nvl(ap_statuscode,' ')<>'FINISH' and nvl(ap_statuscode,' ')<>'ENTERING' and nvl(ad_statuscode,' ')<>'FINISH' and nvl(ad_mrpstatuscode,' ')<>'FINISH'),0) where pd_puid="
				+ id;
		// String
		// sqlstre="update saleforecastdetail set sd_e=NVL((select sum(pw_onhand) from productwh,warehouse where pw_whcode=wh_code and pw_prodcode=pd_prodcode and nvl(wh_type,' ')='不良品仓'),0) where sd_sfid="+id;
		String sqlstrf = "update purchasedetail set pd_f=NVL((select round(sum(pd_outqty)/3,2) from prodinout,prodiodetail where pi_id=prodiodetail.pd_piid and prodiodetail.pd_prodcode=purchasedetail.pd_prodcode and pi_class<>'拨出单' and pi_statuscode='POSTED' and dateadd('M',3,pi_date)>=sysdate ),0) where pd_puid="
				+ id;
		// String
		// sqlstrg="update saleforecastdetail set sd_g=NVL((select sum(sd_qty-nvl(sd_sendqty,0)) from sale,saledetail where sale.sa_id=saledetail.sd_said and saledetail.sd_prodcode=saleforecastdetail.sd_prodcode and nvl(sa_statuscode,' ')<>'FINISH' and nvl(sa_statuscode,' ')<>'ENTERING' and nvl(saledetail.sd_statuscode,' ')<>'FINISH'),0) where sd_sfid="+id;
		List<String> sqls = new ArrayList<String>();
		sqls.add(sqlstrb);
		sqls.add(sqlstrc);
		sqls.add(sqlstrd);
		// sqls.add(sqlstre);
		sqls.add(sqlstrf);
		// sqls.add(sqlstrg);
		baseDao.execute(sqls);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void splitPurchase(String formdata, String data, String caller) {
		Map<Object, Object> formmap = BaseUtil.parseFormStoreToMap(formdata);
		int pd_id = Integer.parseInt(formmap.get("pd_id").toString());
		int puid = Integer.parseInt(formmap.get("pd_puid").toString());
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(pd_detno) from PurchaseDetail where pd_puid=?"
								+ " and exists (select 1 from PurchaseChange left join PurchaseChangedetail on pc_id=pcd_pcid where pc_purccode=pd_code and pcd_pddetno=pd_detno and nvl(pc_needvendcheck,0)=0 and pc_statuscode in ('COMMITED','ENTERING'))",
						String.class, puid);
		if (dets != null) {
			BaseUtil.showError("采购单号+采购行号存在未审核的采购变更单，不允许拆分!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(pd_detno) from PurchaseDetail where pd_puid=?"
								+ " and exists (select 1 from PurchaseChange left join PurchaseChangedetail on pc_id=pcd_pcid where pc_purccode=pd_code and pcd_pddetno=pd_detno and nvl(pc_needvendcheck,0)<>0 and nvl(pc_agreed,0)=0 and pc_statuscode in ('COMMITED','ENTERING','TO_CONFIRM'))",
						String.class, puid);
		if (dets != null) {
			BaseUtil.showError("采购单号+采购行号存在未审核的采购变更单或者供应商未确认，不允许拆分!行号：" + dets);
		}
		String SQLStr = "";
		/*
		 * boolean isSync = baseDao.checkIf("purchase", "pu_id=" + puid +
		 * " and nvl(pu_sync,' ')<>' '"); if (isSync) {
		 * BaseUtil.showError("订单已抛转，无法拆分!"); }
		 */
		int basedetno = Integer.parseInt(formmap.get("pd_detno").toString());
		Map<String, Object> currentMap = new HashMap<String, Object>();
		Object pd_qty = null;
		String pd_deliveryreply = "";
		baseDao.execute("update purchaseDetail set pd_originalqty=pd_qty,pd_originaldetno=pd_detno where pd_id=" + pd_id
				+ " and pd_originaldetno is null");
		SqlRowList cur = baseDao.queryForRowSet("select * from purchasedetail where pd_id=" + pd_id);
		if (cur.next()) {
			currentMap = cur.getCurrentMap();
			pd_qty = cur.getDouble("pd_qty");
			pd_deliveryreply = cur.getObject("pd_deliveryreply") == null ? "无" : cur.getObject("pd_deliveryreply").toString();
		} else
			BaseUtil.showError("原始明细已不存在!无法拆分!");
		SqlRowList sl = baseDao.queryForRowSet("select max(pd_detno) from purchaseDetail where pd_puid=" + puid);
		int newdetno = 0;
		if (sl.next()) {
			newdetno = sl.getInt(1) == -1 ? basedetno + 1 : sl.getInt(1);
		}
		List<Map<Object, Object>> gridmaps = BaseUtil.parseGridStoreToMaps(data);
		Map<Object, Object> map = new HashMap<Object, Object>();
		Object sdid = null;
		double pdqty = 0;
		int pddetno = 0;
		double sumqty = 0;
		SqlRowList sl2 = null;
		for (int i = 0; i < gridmaps.size(); i++) {
			sumqty = sumqty + Double.parseDouble(gridmaps.get(i).get("pd_qty").toString());
		}
		if (sumqty != Double.parseDouble(currentMap.get("pd_qty").toString())) {
			BaseUtil.showError("拆分后总数必须保持一致!");
		}
		/**
		 * 更新已发送货通知数
		 * */
		purchaseDao.updatePurcYNotifyQTY(0, "" + pd_id);
		Object newpd_qty = null;
		Object newpd_deliveryreply = null;
		String newdetnos = "";
		// 判断原始的序号 值不能
		for (int i = 0; i < gridmaps.size(); i++) {
			map = gridmaps.get(i);
			sdid = map.get("pd_id");
			pddetno = Integer.parseInt(map.get("pd_detno").toString());
			pdqty = Double.parseDouble(map.get("pd_qty").toString());
			if (sdid != null && Integer.parseInt(sdid.toString()) != 0) {
				newpd_qty = pdqty;
				newpd_deliveryreply = map.get("pd_deliveryreply");
				sl2 = baseDao
						.queryForRowSet("select pd_qty,NVL(pd_acceptqty,0)pd_acceptqty,NVL(pd_yqty,0)pd_yqty,pd_code,pd_turnqty from purchasedetail where pd_id="
								+ sdid);
				if (sl2.next()) {
					boolean b = baseDao.checkIf("AcceptNotifyDetail", "and_ordercode='" + sl2.getString("pd_code")
							+ "' AND and_orderdetno=" + basedetno);
					if (b) {
						BaseUtil.showError("本序号已经转收料通知单，不能拆分!");
					}
					if (pdqty < sl2.getDouble("pd_yqty") + sl2.getDouble("pd_turnqty") || pdqty < sl2.getDouble("pd_acceptqty")) {
						BaseUtil.showError("原始拆分后的数量小于已收料数量:" + sl2.getInt("pd_yqty") + "+已通知收料数：" + sl2.getInt("pd_turnqty") + "!");
					}
					SQLStr = "update purchasedetail set pd_qty=" + pdqty + ",pd_delivery=to_date('" + map.get("pd_delivery").toString()
							+ "','yyyy-MM-dd'),pd_total=round(pd_price*" + pdqty + ",2),pd_taxtotal=round(pd_netprice*" + pdqty + ",2)"
							+ " where pd_id=" + sdid;
					baseDao.execute(SQLStr);
					// 供应商回复更新
					SQLStr = "update purchasedetail set pd_replydetail='" + map.get("pd_replydetail").toString() + "',pd_isok='"
							+ map.get("pd_isok") + "',pd_qtyreply=" + map.get("pd_qtyreply").toString();
					if (map.get("pd_deliveryreply") != null) {
						SQLStr = SQLStr + ",pd_deliveryreply='" + map.get("pd_deliveryreply") + "'";
					}
					if (map.get("pd_sellercode") != null) {// 采购单 “拆分及交期回复”
															// 支持业务员拆分
															// 反馈：2016110885
						SQLStr = SQLStr + ",pd_sellercode='" + map.get("pd_sellercode").toString() + "',pd_seller='"
								+ map.get("pd_seller").toString() + "'";
					}
					SQLStr = SQLStr + " where pd_id=" + sdid;
					baseDao.execute(SQLStr);
				} else {
					BaseUtil.showError("序号 :[" + pddetno + "] ，明细数据已经不存在，不能拆分!");
				}

			} else {

				boolean bool = true;
				while (bool) {
					newdetno++;
					bool = baseDao.checkIf("purchasedetail", "pd_puid=" + puid + " AND pd_detno=" + newdetno);
					if (!bool)
						break;
				}
				currentMap.remove("pd_delivery");
				currentMap.put("pd_delivery", map.get("pd_delivery").toString());
				currentMap.remove("pd_sellercode");
				currentMap.remove("pd_seller");
				currentMap.remove("pd_deliveryreply");
				currentMap.remove("pd_replydetail");
				currentMap.put("pd_replydetail", map.get("pd_replydetail").toString());
				currentMap.remove("pd_qtyreply");
				currentMap.put("pd_qtyreply", map.get("pd_qtyreply"));
				currentMap.remove("pd_isok");
				currentMap.put("pd_isok", map.get("pd_isok"));
				currentMap.remove("pd_detno");
				currentMap.put("pd_detno", newdetno);
				currentMap.remove("pd_id");
				currentMap.put("pd_id", baseDao.getSeqId("PURCHASEDETAIL_SEQ"));
				currentMap.remove("pd_qty");
				currentMap.put("pd_qty", pdqty);
				currentMap.remove("pd_acceptqty");
				currentMap.put("pd_acceptqty", 0);
				currentMap.remove("pd_cancelqty");
				currentMap.put("pd_cancelqty", 0);
				currentMap.remove("pd_yqty");
				currentMap.put("pd_yqty", 0);
				currentMap.remove("pd_tqty");
				currentMap.put("pd_tqty", 0);
				currentMap.remove("pd_turnqty");
				currentMap.put("pd_turnqty", 0);
				currentMap.remove("pd_backqty");
				currentMap.put("pd_backqty", 0);
				currentMap.remove("pd_ngacceptqty");
				currentMap.put("pd_ngacceptqty", 0);
				currentMap.remove("pd_totested");
				currentMap.put("pd_totested", 0);
				currentMap.remove("pd_reconhand");
				currentMap.put("pd_reconhand", 0);
				currentMap.remove("pd_beipinacceptqty");
				currentMap.put("pd_beipinacceptqty", 0);
				currentMap.remove("pd_frozenqty");
				currentMap.put("pd_frozenqty", 0);
				currentMap.remove("pd_originaldetno");
				currentMap.put("pd_originaldetno", basedetno);
				currentMap.remove("pd_total");
				currentMap.put(
						"pd_total",
						NumberUtil.formatDouble(
								pdqty
										* Double.parseDouble((currentMap.get("pd_price") == null ? "0" : currentMap.get("pd_price"))
												.toString()), 2));
				currentMap.remove("pd_taxtotal");
				currentMap.put(
						"pd_taxtotal",
						NumberUtil.formatDouble(
								pdqty
										* Double.parseDouble((currentMap.get("pd_netprice") == null ? "0" : currentMap.get("pd_netprice"))
												.toString()), 2));
				currentMap.remove("pd_ypurcqty");
				currentMap.put("pd_ypurcqty", 0);
				baseDao.execute(SqlUtil.getInsertSqlByMap(currentMap, "purchasedetail"));
				if (map.get("pd_deliveryreply") != null) {
					baseDao.execute("update purchasedetail set  pd_deliveryreply='" + map.get("pd_deliveryreply").toString()
							+ "' where pd_puid=" + puid + " and pd_detno=" + newdetno);
				}
				if (map.get("pd_sellercode") != null) {
					baseDao.execute("update purchasedetail set  pd_sellercode='" + map.get("pd_sellercode").toString() + "',pd_seller='"
							+ map.get("pd_seller").toString() + "' where pd_puid=" + puid + " and pd_detno=" + newdetno);
				}
				newdetnos = newdetnos + "," + newdetno;
			}

		}

		// 调用拆分收业务逻辑配置
		// Map<Object, Object> param = new HashMap<Object, Object>();
		map.put("pu_id", puid);
		map.put("basedetno", basedetno);
		map.put("newdetnos", newdetnos);
		handlerService.handler("Purchase", "split", "after", new Object[] { map });
		/**
		 * 问题反馈单号：2017010110 处理：修改记录日志
		 * 
		 * @author wsy
		 */
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "订单拆分", "明细行:" + basedetno + "=>被拆分,原数量：" + pd_qty
				+ "、原回复日期：" + pd_deliveryreply + ";新数量:" + newpd_qty + "、新回复日期："
				+ (newpd_deliveryreply == null ? "无" : newpd_deliveryreply) + "", "Purchase|pu_id=" + puid));
	}

	@Override
	public void b2bPurchase(int pu_id, String caller) {
		// 只能对状态为[已审核]的订单进行同步操作!
		Object status = baseDao.getFieldDataByCondition("Purchase", "pu_statuscode", "pu_id=" + pu_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("只能同步已审核的采购单"));
		}
		// 执行同步操作
		baseDao.updateByCondition("Purchase", "pu_sendstatus='待上传'", "pu_id=" + pu_id);
		// 记录操作
		baseDao.logger.others("同步至B2B", "msg.Success", "Purchase", "pu_id", pu_id);
	}

	// 税率强制等于币别表的默认税率
	private void useDefaultTax(String caller, Object pu_id) {
		String defaultTax = baseDao.getDBSetting("Purchase", "defaultTax");
		if (defaultTax != null) {
			// 税率强制等于币别表的默认税率
			if ("1".equals(defaultTax)) {
				baseDao.execute("update purchasedetail set pd_rate=(select cr_taxrate from currencys left join Purchase on pu_currency=cr_name and cr_statuscode='CANUSE' where pd_puid=pu_id)"
						+ " where pd_puid=" + pu_id);
			}
			// 税率强制等于供应商资料的默认税率
			if ("2".equals(defaultTax)) {
				baseDao.execute("update purchasedetail set pd_rate=(select nvl(ve_taxrate,0) from Vendor left join Purchase on pu_vendcode=ve_code and ve_auditstatuscode='AUDITED' where pu_id=pd_puid)"
						+ " where pd_puid=" + pu_id);
			}
		}
	}

	// 本位币允许税率为0
	private void allowZeroTax(String caller, Object pu_id) {
		String currency = baseDao.getDBSetting("defaultCurrency");
		if (!baseDao.isDBSetting("Purchase", "allowZeroTax")) {
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select WM_CONCAT(pd_detno) from PurchaseDetail left join Purchase on pd_puid=pu_id where nvl(pd_rate,0)=0 and pu_currency='"
							+ currency + "' and pd_puid=?", String.class, pu_id);
			if (dets != null) {
				BaseUtil.showError("本位币税率为0，不允许提交、审核!行号：" + dets);
			}
		}
		//增加采购类型 iszerorate字段来限制是否允许0税率 maz 2018070190
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(pd_detno) from PurchaseDetail left join Purchase on pd_puid=pu_id left join purchasekind on pu_kind=pk_name where nvl(pd_rate,0)=0 and pk_iszerorate=0 and pu_currency<>'"+currency+"' and pd_puid=?", String.class, pu_id);
		if (dets != null) {
			BaseUtil.showError("采购类型不允许0税率，明细行存在0税率，不允许提交、审核!行号：" + dets);
		}
	}

	@Override
	public List<PurchaseReply> findReplyByPuid(int id) {
		try {
			return baseDao
					.getJdbcTemplate()
					.query("select PurchaseReply.* from PurchaseReply left join PurchaseDetail on pr_pucode=pd_code and pr_pddetno=pd_detno where pd_puid=? order by pd_detno,pr_date",
							new BeanPropertyRowMapper<PurchaseReply>(PurchaseReply.class), id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public void vastClosePurchaseDetail(String language, Employee employee, String caller, String data) {
		Object id;
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> m : maps) {
			id = m.get("pd_id");
			baseDao.updateByCondition("purchasedetail", "pd_endstatus='待上传'", "pd_id=" + id);
		}
		singleFormItemsService.vastResCloseDetail(language, employee, caller, data);
	}

	public void purchase_audit_sendMail(Integer id) {
		Object email = baseDao.getFieldDataByCondition("Purchase left join Vendor on pu_vendcode=ve_code", "ve_email", "pu_id=" + id);
		if (email == null || "".equals(email.toString().trim()) || "null".equals(email.toString().trim())) {
			return;
			// BaseUtil.showError("供应商邮箱为空，无法发送邮件!");
		}
		// 标题和内容一致
		String encop = baseDao.getFieldDataByCondition("enterprise", "en_name", "1=1").toString();
		Object[] objs = baseDao.getFieldsDataByCondition("purchase", new String[] { "pu_code", "pu_vendname" }, "pu_id=" + id);
		String title = "请查看采购订单，订单编号：" + objs[0];
		String contextdetail = "<P class=MsoNormal style='MARGIN: 0cm 0cm 0pt'><SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'>"
				+ objs[1]
				+ "，您好！：<SPAN lang=EN-US><?xml:namespace prefix = 'o' ns = 'urn:schemas-microsoft-com:office:office' /><o:p></o:p></SPAN></SPAN></P>"
				+ "<P class=MsoNormal style='MARGIN: 0cm 0cm 0pt'><SPAN lang=EN-US style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'><SPAN style='mso-spacerun: yes'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				+ " </SPAN></SPAN><SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'>您有一张来自于<SPAN style='COLOR: blue'>公司名称（<SPAN lang=EN-US>"
				+ encop
				+ "</SPAN>）</SPAN>的新订单<SPAN lang=EN-US>(</SPAN>订单编号：<SPAN lang=EN-US style='COLOR: blue'>"
				+ objs[0]
				+ ")</SPAN>"
				+ "<SPAN lang=EN-US>,</SPAN>及时登入优软商务平台查取您的订单<SPAN lang=EN-US>!<o:p></o:p></SPAN></SPAN></P><P class=MsoNormal style='MARGIN: 0cm 0cm 0pt'><SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'>登入平台的地址：<SPAN lang=EN-US><A href='http://www.ubtob.com/'><FONT color=#0000ff>www.ubtob.com</FONT></A>"
				+ "<o:p></o:p></SPAN></SPAN></P><P class=MsoNormal style='MARGIN: 0cm 0cm 0pt'><SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑'>如在使用平台过程中，遇到任何操作问题，请及时与深圳市优软科技有限公司客服人员（谭小姐）联系，联系电话：<SPAN lang=EN-US>0755-26996828<o:p></o:p></SPAN></SPAN></P>"
				+ "<SPAN style='FONT-SIZE: 14pt; FONT-FAMILY: '微软雅黑','sans-serif'; mso-bidi-font-family: 微软雅黑; mso-font-kerning: 1.0pt; mso-ansi-language: EN-US; mso-fareast-language: ZH-CN; mso-bidi-language: AR-SA'>致敬！</SPAN>";
		sendMailService.sendSysMail(title, contextdetail, email.toString());
	}

	@Override
	public void endPurchase(String data, String caller) {
		singleFormItemsService.vastClosePurchaseDetail(SystemSession.getLang(), SystemSession.getUser(), caller, data);
	}

	private void purchase_audit_sendRemind(int pu_id) {
		Employee employee = SystemSession.getUser();
		SqlRowList rs = baseDao
				.queryForRowSet("SELECT pd_qty,pd_id,pu_statuscode,pu_code,pd_detno,pu_vendcode,pu_vendname,pd_prodcode,pd_thisqty,pr_id FROM PurchaseDetail left join purchase on pd_puid=pu_id left join product on pd_prodcode=pr_code  where pu_id="
						+ pu_id);
		while (rs.next()) {
			Map<Object, Object> map = new HashMap<Object, Object>();
			map.put("pn_id", baseDao.getSeqId("PURCHASENOTIFY_SEQ"));
			map.put("pn_mrpcode", null);
			map.put("pn_mdid", 0);
			map.put("pn_ordercode", rs.getObject("pu_code"));
			map.put("pn_orderdetno", rs.getObject("pd_detno"));
			map.put("pn_vendcode", rs.getObject("pu_vendcode"));
			map.put("pn_vendname", rs.getObject("pu_vendname"));
			map.put("pn_prodcode", rs.getObject("pd_prodcode"));
			map.put("pn_qty", rs.getDouble("pd_qty"));
			map.put("pn_delivery", DateUtil.currentDateString(null));
			map.put("pn_prodid", rs.getObject("pr_id"));
			map.put("pn_pdid", rs.getObject("pd_id"));
			map.put("pn_status", "未确认");
			map.put("pn_statuscode", "UNCONFIRM");
			map.put("pn_indate", DateUtil.parseDateToString(new Date(), Constant.YMD_HMS));
			map.put("pn_inman", employee.getEm_name());
			map.put("pn_thisqty", 0);
			map.put("pn_endqty", 0);
			map.put("pn_thisbpqty", 0);
			baseDao.execute(SqlUtil.getInsertSqlByMap(map, "PURCHASENOTIFY"));
		}
		baseDao.logger.others("投放操作", "投放成功", "Purchase", "pu_id", pu_id);
	}

	private String getGoodsReserve(int pu_id) {
		// 提交之前进行提示
		SqlRowList rs = baseDao.queryForRowSet("select distinct pr_uuid from purchase left join purchasedetail on pu_id=pd_puid "
				+ "left join Product on pd_prodcode=pr_code where pu_id=? and nvl(pr_uuid,' ')<>' '"
				+ "and pr_uuid not in (select go_uuid from B2C$GoodsOnhand where ROUND(TO_NUMBER(sysdate-go_synctime) * 24)<1)", pu_id);
		if (rs.next()) {
			StringBuffer strs = new StringBuffer();
			for (Map<String, Object> map : rs.getResultList()) {
				strs.append(map.get("pr_uuid") + ",");
			}
			String uuids = strs.substring(0, strs.length() - 1);
			getGoodsReserveService.getGoodsOnhand(uuids);
			getGoodsReserveService.getGoodsBatch(uuids);
		}
		// 对比单价 最小订购量区间
		String msg = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat('序号'||T.pd_detno||'物料在平台的最低单价为'||T.go_minprice) from "
								+ "(select round(pu_rate *nvl(pd_price,0)/(1+nvl(pd_rate,0)/100),8) pd_price,pr_uuid,pd_prodcode,pd_detno,go_minprice from Purchase left join "
								+ "PurchaseDetail on pu_id=pd_puid left join product on pr_code=pd_prodcode "
								+ "left join B2C$GoodsOnhand on pr_uuid=go_uuid and go_prodcode=pd_prodcode where pu_id=?"
								+ " and pd_qty>go_minbuyqty)T  where T.go_minprice<T.pd_price and rownum<15", String.class, pu_id);
		if (msg != null) {
			return msg;
		}
		return null;
	}

	// 审核之后将平台采购单中的数据传送值B2C标准器件库
	private void sendToB2CPlatform(int pu_id) {
		// 判断是否为pu_ordertype=B2C
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select pu_code,pu_id ,pu_shipaddresscode,pu_buyername,pu_currency from purchase where pu_id=? and pu_ordertype='B2C' and nvl(pu_sendstatus,' ') not in ('已上传','上传中')",
						pu_id);
		if (rs.next()) {
			Purchase purchase = new Purchase();
			purchase.setId(rs.getLong("pu_id"));
			// 获取采购员姓名，采购员电话employee em_tel,em_mobile, enuu 企业UU,email,name,tel
			SqlRowList rs2 = baseDao.queryForRowSet(
					"select em_tel,em_mobile,em_email,en_uu from employee left join enterprise on en_id=em_enid where em_id=?",
					SystemSession.getUser().getEm_id());
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("area", rs.getString("pu_shipaddresscode"));
			map.put("name", rs.getString("pu_buyername"));
			map.put("email", rs2.getString("em_email"));
			map.put("tel", rs2.getString("em_mobile"));
			map.put("enuu", rs2.getString("en_uu"));
			purchase.setShipAddress(FlexJsonUtil.toJson(map));
			purchase.setTotal(0.0);
			purchase.setCreateTime(new Date());
			purchase.setCurrencyName(rs.getString("pu_currency"));
			List<PurchaseDetail> purchaseDetail = new ArrayList<PurchaseDetail>();
			SqlRowList rs3 = baseDao
					.queryForRowSet(
							"select pb_b2bbatchcode,pb_price,pb_qty,pb_price*pb_qty total,go_unit,pb_erpunit,pb_id from B2C$PURCHASEBATCH left join B2C$GOODSONHAND on go_uuid=pb_uuid and go_prodcode=pb_prodcode where pb_puid=?",
							pu_id);
			while (rs3.next()) {
				PurchaseDetail detail = new PurchaseDetail();
				double rate = productBatchUUIdService.getUnitRate(rs3.getString("pb_erpunit"), rs3.getString("go_unit"));
				detail.setBatchCode(rs3.getString("pb_b2bbatchcode"));
				detail.setDetno((short) 0);
				detail.setId(rs3.getLong("pb_id"));
				detail.setPrice(rs3.getDouble("pb_price") / rate);
				detail.setTotal(rs3.getDouble("total"));
				detail.setQty(rs3.getDouble("pb_qty") * rate);
				purchaseDetail.add(detail);
			}
			purchase.setDetails(purchaseDetail);
			try {
				baseDao.execute("update purchase set pu_sendstatus='上传中' where pu_id=?", pu_id);
				B2cOrder order = sendPurchaseToB2CService.save(purchase, SystemSession.getUser().getCurrentMaster());
				for (B2cOrderDetail detail : order.getOrderDetails()) {
					baseDao.execute("update purchasedetail set pd_b2ccode=?,pd_price=? where pd_puid=? and pd_b2cbatchcode=?",
							order.getId(), detail.getPrice(), pu_id, detail.getBatchCode());
				}
				getTotal(pu_id);
				boolean bool = baseDao.execute("update purchase set pu_sendstatus='已上传',pu_total=" + order.getPrice() + ",pu_taxtotal="
						+ (order.getPrice() - order.getTaxes()) + " where pu_id=?", pu_id);
				// 根据配置选择是否生成预付款申请单
				if (bool) {
					boolean autoTurn = baseDao.isDBSetting("B2CSetting", "autoTurnToPayPlease");
					if (autoTurn) {
						turnToPayPlease(pu_id);
					}
				}
			} catch (Exception e) {
				baseDao.execute("update purchase set pu_sendstatus='待上传' where pu_id=?", pu_id);
				// 添加任务至b2c$task 表
				baseDao.execute("insert into b2c$task(ta_id,ta_docaller,ta_docode,ta_doid,ta_actiontime) select "
						+ "B2C$TASK_SEQ.nextval,'Purchase',pu_code,pu_id,sysdate from purchase where pu_id=?", pu_id);
			}
		}
	}

	// 转存前判断
	@Override
	public void turnBankRegister(int id) {

		// 已经生成了银行转存单，不能重复生成!
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(ar_code) from AccountRegister where ar_type = '转存' and nvl(ar_source,' ') = (select pu_code from Purchase where pu_id = ?)",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("已经生成了银行转存单:" + dets.replace(',', '、') + ",不能重复生成!");
		} else {
			Object total = baseDao.getFieldDataByCondition("purchaseDetail", "nvl(SUM(pd_qty*pd_bgprice),0)", "pd_puid = " + id);
			baseDao.execute("update Purchase set pu_total = ? where pu_id = ?", total, id);
		}
	}

	// 采购单转存银行登记
	@Override
	public String confirmTurnBankRegister(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		Object[] pu = baseDao.getFieldsDataByCondition("Purchase", new String[] { "pu_projectcode", "pu_vendcode", "pu_vendname",
				"pu_remark", "pu_cop" }, "pu_id = " + store.get("pu_id"));
		Object prjname = null;
		StringBuffer result = new StringBuffer();
		String code = "";
		if (pu[0] != null) {
			prjname = baseDao.getFieldDataByCondition("Project", "prj_name", "prj_code = '" + pu[0] + "'");
		}
		final String INSERTACCOUNTREGISTER = "insert into AccountRegister(ar_id,ar_code,ar_cateid,ar_type,ar_date,ar_recorddate,"
				+ "ar_payment,ar_accountcode,ar_accountname,ar_accountcurrency,ar_othercateid,ar_category,ar_catedesc,ar_precurrency,ar_sourcetype,"
				+ "ar_source,ar_sourceid,ar_departmentcode,ar_departmentname,ar_prjcode,ar_prjname,ar_vendcode,ar_vendname,ar_emid,ar_recordman,"
				+ "ar_status,ar_statuscode,ar_poststatus,ar_poststatuscode,ar_memo,ar_cop) values(?,?,?,?,sysdate,sysdate,?,?,?,?,?,?,?,?,?,?,?,"
				+ "?,?,?,?,?,?,?,?,?,'ENTERING',?,'UNPOST',?,?)";

		Employee employee = SystemSession.getUser();
		try {
			// 遍历明细转存到银行登记
			for (Map<Object, Object> g : grid) {
				Object ar_id = baseDao.getSeqId("AccountRegister_SEQ");
				Object ar_code = baseDao.sGetMaxNumber("AccountRegister", 2);
				Object currency = baseDao.getFieldDataByCondition("Category", "ca_currency", "ca_id = " + g.get("ca_id"));
				Object precurrency = baseDao.getFieldDataByCondition("Category", "ca_currency", "ca_id = " + g.get("ca_tocaid"));
				boolean bool = baseDao.execute(
						INSERTACCOUNTREGISTER,
						new Object[] { ar_id, ar_code, g.get("ca_id"), "转存", g.get("ca_tobalance"), g.get("ca_code"),
								g.get("ca_description"), currency, g.get("ca_tocaid"), g.get("ca_tocacode"), g.get("ca_tocatedesc"),
								precurrency, "采购单", store.get("pu_code"), store.get("pu_id"), store.get("pu_departmentcode"),
								store.get("pu_departmentname"), pu[0], prjname, pu[1], pu[2], employee.getEm_id(), employee.getEm_name(),
								BaseUtil.getLocalMessage("ENTERING"), BaseUtil.getLocalMessage("UNPOST"), pu[3], pu[4] });

				// 插入成功后更新
				if (bool) {
					baseDao.execute("update purchase set pu_transferbank = '已生成' where pu_id =" + store.get("pu_id"));
					baseDao.execute("update accountregister set ar_accountrate=(select cm_crrate from currencysmonth where cm_crname="
							+ "ar_accountcurrency and cm_yearmonth=to_char(ar_date,'yyyymm')) where ar_id=" + ar_id);
					baseDao.execute("update accountregister set ar_prerate=(select cm_crrate from currencysmonth where cm_crname="
							+ "ar_precurrency and cm_yearmonth=to_char(ar_date,'yyyymm')) where ar_id=" + ar_id);
					baseDao.execute("update accountregister set ar_preamount=((ar_prerate/ar_accountrate)*ar_payment) where ar_id=" + ar_id);
				}

				result.append("银行登记的转存单生成成功,银行登记单号:"
						+ "<a href=\"javascript:openUrl('jsps/fa/gs/accountRegister.jsp?whoami=AccountRegister"
						+ "!Bank&formCondition=ar_idIS" + ar_id + "')\">" + ar_code + "</a>&nbsp;");
				result.append("<hr>");

				code += ar_code;
				code += "、";

			}
			code = code.substring(0, code.length() - 1);
			// 生成日志
			baseDao.logger.others("生成银行转存单", "成功生成，单号：" + code, "Purchase", "pu_id", store.get("pu_id"));

		} catch (Exception e) {
			BaseUtil.showError("转存失败!" + e.getMessage());
		}
		return result.toString();

	}

	// 生成预付款申请单
	public void turnToPayPlease(int pu_id) {

		final String PayPlease = "INSERT INTO PAYPLEASE(pp_id,pp_code,pp_date,pp_applyid,pp_apply,pp_status,pp_statuscode,pp_total,"
				+ "pp_paystatus,pp_paystatuscode,pp_type,pp_printstatus,pp_printstatuscode) values (?,?,sysdate,?,?,?,?,?,?,?,?,?,?)";
		final String PayPleaseDetail = "INSERT INTO PAYPLEASEDETAIL(ppd_id ,ppd_ppid,ppd_detno,ppd_vendcode,ppd_vendname,ppd_paymethod,"
				+ "ppd_bankname,ppd_bankaccount,ppd_vendid,ppd_currency,ppd_applyamount) values (?,?,?,?,?,?,?,?,?,?,?)";
		final String PayPleaseDetailDet = "INSERT INTO PAYPLEASEDETAILDET(ppdd_id,ppdd_ppdid,ppdd_detno,ppdd_currency,ppdd_pucode,"
				+ "ppdd_billdate, ppdd_billamount,ppdd_account, ppdd_paymethodid,ppdd_thisapplyamount,ppdd_paymethod,"
				+ "ppdd_type) values (?,?,?,?,?,to_date(?,'yyyy-MM-dd HH24:mi:ss'),?,?,?,?,?,?)";
		Employee employee = SystemSession.getUser();
		try {
			// 从采购单获取需要的字段
			Object[] pu = baseDao.getFieldsDataByCondition("PURCHASE", new String[] { "pu_total", "pu_vendcode", "pu_currency", "pu_code",
					"pu_date", "nvl(pu_prepayamount,0)", "pu_paymentsid", "nvl(pu_total,0)-nvl(pu_prepayamount,0) as amount",
					"pu_payments", "pu_type" }, "pu_id = " + pu_id);

			if (pu != null) {
				int pp_id = baseDao.getSeqId("PayPlease_SEQ");

				// 保存PayPlease
				baseDao.execute(PayPlease,
						new Object[] { pp_id, baseDao.sGetMaxNumber("PayPlease", 2), employee.getEm_id(), employee.getEm_name(), "在录入",
								"ENTERING", pu[0], "未付款", "UNPAYMENT", "预付款", "未打印", "UNPRINT" });

				// 保存PayPleaseDetail
				int ppd_id = baseDao.getSeqId("PayPleaseDetail_SEQ");
				Object[] ve = baseDao.getFieldsDataByCondition("vendor", new String[] { "ve_name", "ve_payment", "ve_bank",
						"ve_bankaccount", "ve_id" }, "ve_code = '" + pu[1] + "'");
				baseDao.execute(PayPleaseDetail, new Object[] { ppd_id, pp_id, 1, pu[1], ve[0], ve[1], ve[2], ve[3], ve[4], pu[2], pu[0] });

				// 保存PayPleaseDetailDet
				baseDao.execute(PayPleaseDetailDet, new Object[] { baseDao.getSeqId("PayPleaseDetailDet_SEQ"), ppd_id, 1, pu[2], pu[3],
						pu[4], pu[0], pu[5], pu[6], pu[7], pu[8], pu[9] });

				// 生成日志
				baseDao.logger.turn("商城类型采购单成功上传商城转预付款申请单", "Purchase", "pu_id", pu_id);
			}
		} catch (Exception e) {
			BaseUtil.showError(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void dataReply(String pucode, String detno, String qty, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		int newqty = 0;
		List<String> sqls = new ArrayList<String>();
		for (Map<Object, Object> map : maps) {
			int id = baseDao.getSeqId("PURCHASEREPLY_SEQ");
			String pr_delivery = map.get("pr_delivery").toString().substring(0, 10);
			String sql = "insert into purchasereply(pr_qty,pr_delivery,pr_date,pr_remark,pr_pucode,pr_pddetno,pr_recorder,pr_type,pr_id) values("
					+ map.get("pr_qty")
					+ ",to_date('"
					+ pr_delivery
					+ "','yyyy-mm-dd'),sysdate,'"
					+ map.get("pr_remark")
					+ "','"
					+ pucode
					+ "','" + detno + "','" + SystemSession.getUser().getEm_name() + "','采购主动回复'," + id + ")";
			sqls.add(sql);
			newqty += Integer.parseInt(map.get("pr_qty").toString());

		}
		if (Integer.parseInt(qty) != newqty) {
			BaseUtil.showError("总数量必须等于" + qty);
		}
		baseDao.execute("update purchasereply set pr_ifoverdate=-1 where pr_pucode='" + pucode + "' and pr_pddetno=" + detno + "");
		baseDao.execute(sqls);
		Object dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WMSYS.WM_CONCAT('交期：'||to_char(PR_DELIVERY,'YYYY-MM-DD')||' 数量：'||pr_qty) from(select * from PURCHASEREPLY where pr_pucode=? and PR_PDDETNO=? and nvl(PR_IFOVERDATE,0)=0 order by PR_DELIVERY)",
						String.class, pucode, detno);
		Object PR_DELIVERY = baseDao.getFieldDataByCondition("PURCHASEREPLY", "max(PR_DELIVERY)", "pr_pucode='" + pucode
				+ "' and PR_PDDETNO='" + detno + "' and nvl(PR_IFOVERDATE,0)=0 ");
		baseDao.updateByCondition("purchasedetail", "pd_replydetail='" + dets + "',pd_deliveryreply=substr('" + PR_DELIVERY + "',1,10)",
				"pd_code='" + pucode + "' and pd_detno='" + detno + "'");
	}

	@Override
	public Map<String, Object> getContractProcess(int id) {
		Map<String, Object> result = new HashMap<String, Object>();
		Object[] tender = baseDao.getFieldsDataByCondition("Purchase", new String[] { "pu_sourceid", "pu_sourcecode" },
				"pu_source = '招投标' and pu_id=" + id);
		if (tender == null) {
			BaseUtil.showError("此采购订单不是招投单的采购合同！");
		}
		Employee employee = SystemSession.getUser();
		Master master = employee.getCurrentMaster();
		HashMap<String, String> params = new HashMap<String, String>();
		try {
			params.put("id", String.valueOf(tender[0]));
			Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/erp/tender/detail?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String data = response.getResponseText();
				if (StringUtil.hasText(data)) {
					PurchaseTender purchaseTender = FlexJsonUtil.fromJson(data, PurchaseTender.class);
					if (purchaseTender.getIfAll() != null && purchaseTender.getIfAll() == 1) {
						List<Map<String, Object>> vendors = new ArrayList<Map<String, Object>>();
						PurchaseTenderProd prod = purchaseTender.getPurchaseTenderProds().get(0);
						for (SaleTenderItem sItem : prod.getSaleTenderItems()) {
							Map<String, Object> vendor = new HashMap<String, Object>();
							if (NumberUtil.nvl(sItem.getTotalMoney(), 0).doubleValue() != 0) {
								vendor.put("saleId", sItem.getSaleId());
								vendor.put("enName", sItem.getEnName());
								vendor.put("applyStatus", sItem.getApplyStatus());
								vendor.put("vendUU", sItem.getVendUU());
								vendors.add(vendor);
							}
						}
						purchaseTender.setPurchaseTenderProds(null);
						result.put("tenderform", purchaseTender);
						result.put("vendors", vendors);
					} else {
						Map<Long, Map<String, Object>> Vendors = new HashMap<Long, Map<String, Object>>();
						for (PurchaseTenderProd prod : purchaseTender.getPurchaseTenderProds()) {
							for (SaleTenderItem sItem : prod.getSaleTenderItems()) {
								if (sItem.getPrice() != null) {
									if (!Vendors.containsKey(sItem.getSaleId())
											|| (sItem.getApplyStatus() != null && sItem.getApplyStatus() == 1)) {
										Map<String, Object> vendor = new HashMap<String, Object>();
										vendor.put("saleId", sItem.getSaleId());
										vendor.put("enName", sItem.getEnName());
										vendor.put("applyStatus", sItem.getApplyStatus());
										vendor.put("vendUU", sItem.getVendUU());
										Vendors.put(sItem.getSaleId(), vendor);
									}
								}
							}
						}
						purchaseTender.setPurchaseTenderProds(null);
						Collection<Map<String, Object>> valueCollection = Vendors.values();
						List<Map<String, Object>> vendors = new ArrayList<Map<String, Object>>(valueCollection);
						result.put("vendors", vendors);
						result.put("tenderform", purchaseTender);
					}
				}
			} else {
				throw new Exception("连接平台失败！" + response.getStatusCode());
			}

			PurchaseTenderAnswer answer = baseDao.queryBean("select ID,CODE,ENNAME,TENDERCODE,TENDERTITLE,QUESTIONENDDATE,RECORDER,"
					+ "INDATE,AUDITMAN,AUDITDATE,REMARK,REPLYDATE,STATUS,AUDITSTATUS,AUDITSTATUSCODE,ATTACHS from "
					+ "TENDERANSWER WHERE　TENDERCODE = ?", PurchaseTenderAnswer.class, tender[1]);
			List<Map<String, Object>> keyList = new ArrayList<Map<String, Object>>();
			Map<String, Object> map = new HashMap<String, Object>(2);
			map.put("caller", "Tender");
			map.put("keyValue", tender[0]);
			keyList.add(map);
			map = new HashMap<String, Object>(2);
			map.put("caller", "TenderEstimate");
			map.put("keyValue", tender[0]);
			keyList.add(map);
			if (answer != null) {
				answer.setTenderId(Long.parseLong(tender[0].toString()));
				map = new HashMap<String, Object>(2);
				map.put("caller", "TenderAnswer");
				map.put("keyValue", answer.getId());
				keyList.add(map);
			}
			result.put("tenderanswer", answer);
			result.put("nodes", getJProcessByForm(keyList));

			List<TenderChange> tenderChanges = baseDao
					.getJdbcTemplate()
					.query("select TC_STATUSCODE,TC_AUDITMAN,TC_AUDITDATE,"
							+ "TC_ID,TC_CODE,TC_TTCODE,TC_TTTITLE,TC_TYPE,TC_OLDENDTIME,TC_NEWENDTIME,TC_RECORDMAN,TC_RECORDTIME,TC_STATUS,"
							+ "TC_CHANGEREASON,TC_TTCOMPANY,ID TC_TTID from TenderChange,TENDER where tc_ttcode = code and tc_statuscode = 'AUDITED' and tc_ttcode = ? "
							+ "order by tc_id desc", new BeanPropertyRowMapper<TenderChange>(TenderChange.class), tender[1]);
			List<Map<String, Object>> changeList = new ArrayList<Map<String, Object>>(tenderChanges.size());
			for (TenderChange tc : tenderChanges) {
				Map<String, Object> tenderChange = new HashMap<String, Object>(2);
				tenderChange.put("form", tc);
				tenderChange.put("nodes", jprocessService.getJprocessNode("TenderChange", tc.getTc_id().intValue(), "current"));
				changeList.add(tenderChange);
			}
			result.put("tenderChanges", changeList);

		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误：" + e.getMessage());
		}

		return result;
	}

	private Map<String, Object> getJProcessByForm(List<Map<String, Object>> keyList) {
		Map<String, Object> nodes = new HashMap<String, Object>();
		for (Map<String, Object> map : keyList) {
			String caller = (String) map.get("caller");
			int keyValue = Integer.parseInt(map.get("keyValue").toString());
			nodes.put(caller, jprocessService.getJprocessNode(caller, keyValue, "current"));
		}
		return nodes;
	}

	@Override
	public void updateGridDetailReplyDate(String id, String date) {
		baseDao.execute("update purchasedetail set pd_deliveryreply='" + date + "' where pd_puid='" + id + "'");
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "采购单明细批量更新回复日期", "更新成功", "Purchase|pu_id=" + id));
	}

	@Override
	public void getLastMakePrice(int ma_id, String caller,String prodcode) {
		double total =0;
		
		//求汇率
		Object maqty = baseDao.getFieldDataByCondition("make", "nvl(ma_qty,0)", "ma_id ="+ma_id+"");
		
		Object[] obj = baseDao.getFieldsDataByCondition("make", "ma_statuscode,nvl(ma_madeqty,0) ma_madeqty", "ma_id="+ma_id);
		if ("FINISH".equals(obj[0])) {
			BaseUtil.showError("已经结案工单不能更新委外商");
		}
		if (obj[1] != null && Double.parseDouble(obj[1].toString()) > 0) {
			BaseUtil.showError("已有验收数量的委外单不能更新委外商信息");
		}
		SqlRowList rs = baseDao.queryForRowSet("select a.*  from make a where a.ma_prodcode='"+prodcode+"' "
				+ "and a.ma_tasktype='OS' and nvl(a.ma_price,0)<>0"
						+" and a.ma_status ='已审核' and nvl(a.ma_id,0)<>"+ma_id+" ORDER BY a.ma_date DESC");
		if (rs.next()) {
			baseDao.execute("update make set ma_vendcode=?,ma_vendname=?,ma_apvendcode=?,ma_apvendname=?,ma_currency=?,ma_paymentscode=?"
					+ ",ma_payments=?,ma_price=?,ma_taxrate=? where ma_id=?",rs.getString("ma_vendcode"),rs.getString("ma_vendname"),rs.getString("ma_apvendcode"),
					rs.getString("ma_apvendname"),rs.getString("ma_currency"),rs.getString("ma_paymentscode"),rs.getString("ma_payments"),rs.getDouble("ma_price"),rs.getDouble("ma_taxrate"),ma_id);	
			//求汇率
			Object hl = baseDao.getFieldDataByCondition("CurrencysMonth", "nvl(cm_crrate,0)", "cm_yearmonth = To_Char(Sysdate, 'YYYYMM') and cm_crname='"+rs.getString("ma_currency")+"'");
			//计算ma_total、ma_totalupper
			total = Double.parseDouble(maqty.toString())*rs.getDouble("ma_price")*Double.parseDouble(hl.toString());
			baseDao.execute("update make set ma_total="+total+",ma_totalupper='"+MoneyUtil.toChinese(total)+"' where ma_id=" + ma_id);
			baseDao.execute("update make set ma_pricetype='按上次委外单' where ma_id=" + ma_id);
			// 记录操作
			baseDao.logger.others("委外信息变更-按上次委外单", "msg.saveSuccess", "Make", "ma_id", ma_id);
		}
	}
}
