package com.uas.erp.service.scm.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.api.b2c_erp.seller.model.Invoice;
import com.uas.api.b2c_erp.seller.model.InvoiceDetail;
import com.uas.b2c.service.seller.SaleInvoiceService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.exception.SystemException;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlMap;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.InvoiceDao;
import com.uas.erp.dao.common.MakeDao;
import com.uas.erp.dao.common.OtherExplistDao;
import com.uas.erp.dao.common.ProdInOutDao;
import com.uas.erp.dao.common.PurchaseDao;
import com.uas.erp.dao.common.VerifyApplyDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.model.PagingRelease;
import com.uas.erp.model.ProdChargeDetail;
import com.uas.erp.service.b2c.GoodsChangeService;
import com.uas.erp.service.scm.BatchDealService;
import com.uas.erp.service.scm.ProdInOutService;
import com.uas.erp.service.scm.ProductBatchUUIdService;
import com.uas.erp.service.scm.SaleClashService;

@Service("prodInOutService")
public class ProdInOutServiceImpl implements ProdInOutService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private SaleClashService saleClashService;
	@Autowired
	private ProdInOutDao prodInOutDao;
	@Autowired
	private VerifyApplyDao verifyApplyDao;
	@Autowired
	private SaleInvoiceService saleInvoiceService;
	@Autowired
	private InvoiceDao invoiceDao;
	@Autowired
	private MakeDao makeDao;
	@Autowired
	private ProductBatchUUIdService productBatchUUIdService;
	@Autowired
	private GoodsChangeService goodsChangeService;
	@Autowired
	private PurchaseDao purchaseDao;
	@Autowired
	private OtherExplistDao otherExplistDao;
	@Autowired
	private BatchDealService batchDealService;
	@Autowired
	private ScmHandler sh;
	static final String getID = "select va_id from verifyapply where va_code=?";
	static final String getDetail = "select pd_inqty,pd_ordercode,pd_orderdetno,pd_vacode from ProdIODetail where pd_piid=?";
	static final String updateVerifyDetail = " update verifyapplydetail set vad_qty=nvl(vad_qty)+? where vad_vaid=? and vad_pucode=? and vad_pudetno=?";
	final static String INSERT_PRODIO = "INSERT INTO ProdInOut(pi_id, pi_inoutno, pi_date, pi_class, pi_invostatus, pi_invostatuscode,"
			+ "pi_recordman, pi_recorddate, pi_statuscode, pi_status) VALUES (?,?,?,?,?,?,?,?,?,?)";
	final static String INSERT_DETAIL_F = "INSERT INTO ProdIODetail(pd_id,pd_piid,pd_inoutno,pd_piclass,pd_pdno,pd_status,pd_auditstatus,"
			+ "pd_inqty,pd_ordercode,pd_wccode,pd_prodcode,pd_batchcode,pd_prodid, pd_qcid) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static String INSERT_PRODIO_DEL = "insert into prodinout_del(pi_id,pi_inoutno,pi_class,pi_whcode,pi_date,pi_paydate,pi_recorddate,"
			+ "pi_currency,pi_rate,pi_cardcode,pi_title,pi_total,pi_taxtotal,pi_havepay,pi_belongs,pi_inoutman,pi_payment,pi_transport,"
			+ "pi_fromcode,pi_tocode,pi_relativeplace,pi_invocode,pi_purpose,pi_status,pi_printstatus,pi_checkstatus,pi_remark,pi_operatorcode,"
			+ "pi_recordman,pi_sellercode,pi_cop,pi_totalupper,pi_vouchercode,pi_whname,pi_purposename,pi_receivecode,pi_receivename,pi_expresscode,"
			+ "pi_invostatus,pi_updateman,pi_updatedate,pi_auditman,pi_auditdate,pi_type,pi_departmentcode,pi_departmentname,pi_emcode,pi_emname,pi_shr,"
			+ "pi_idcode,pi_groupcode,pi_printman,pi_sendcode,pi_code,fin_code,pi_listcode,pi_cardid,pi_invostatuscode,pi_statuscode,pi_profitrate,pi_warehouseid,"
			+ "pi_date1,pi_date2,pi_address,pi_cgycode,pi_cgy,pi_operatcenter,pi_planpaydate,pi_refno,pi_printstatuscode,pi_billstatus,pi_billstatuscode,pi_arcode,pi_arname,"
			+ "pi_sourcecode,pi_enid,pi_sendstatus,pi_paymentcode,pi_cusaddresssid,pi_sellername,pi_ntbamount,pi_tduedate,pi_custcode2,pi_custname2,pi_emergency,pi_invoicecode,"
			+ "pi_invoiceremark,pi_merchandiser,pi_needhkdtotal,pi_packingcode,pi_packingremark,pi_payamount,pi_printrate,pi_remark2,pi_outtype,pi_count,pi_outamount,pi_outcredit,"
			+ "pi_bcid,pi_prjcode,pi_prjname,pi_prjkind,pi_logisticscompany,pi_logisticscode,pi_fax,pi_qsr,pi_attach,pi_ordertype,pi_chargeamount,pi_hassendedi,pi_pdastatus,syssendedidate_,"
			+ "pi_monthenddate,pi_intype,pi_unautopostin,pi_cancelreason,pi_caller) select pi_id,pi_inoutno,pi_class,pi_whcode,pi_date,pi_paydate,pi_recorddate,pi_currency,pi_rate,pi_cardcode,"
			+ "pi_title,pi_total,pi_taxtotal,pi_havepay,pi_belongs,pi_inoutman,pi_payment,pi_transport,pi_fromcode,pi_tocode,pi_relativeplace,pi_invocode,pi_purpose,pi_status,pi_printstatus,"
			+ "pi_checkstatus,pi_remark,pi_operatorcode,pi_recordman,pi_sellercode,pi_cop,pi_totalupper,pi_vouchercode,pi_whname,pi_purposename,pi_receivecode,pi_receivename,pi_expresscode,"
			+ "pi_invostatus,pi_updateman,pi_updatedate,pi_auditman,pi_auditdate,pi_type,pi_departmentcode,pi_departmentname,pi_emcode,pi_emname,pi_shr,pi_idcode,pi_groupcode,pi_printman,"
			+ "pi_sendcode,pi_code,fin_code,pi_listcode,pi_cardid,pi_invostatuscode,pi_statuscode,pi_profitrate,pi_warehouseid,pi_date1,pi_date2,pi_address,pi_cgycode,pi_cgy,pi_operatcenter,"
			+ "pi_planpaydate,pi_refno,pi_printstatuscode,pi_billstatus,pi_billstatuscode,pi_arcode,pi_arname,pi_sourcecode,pi_enid,pi_sendstatus,pi_paymentcode,pi_cusaddresssid,pi_sellername,"
			+ "pi_ntbamount,pi_tduedate,pi_custcode2,pi_custname2,pi_emergency,pi_invoicecode,pi_invoiceremark,pi_merchandiser,pi_needhkdtotal,pi_packingcode,pi_packingremark,pi_payamount,"
			+ "pi_printrate,pi_remark2,pi_outtype,pi_count,pi_outamount,pi_outcredit,pi_bcid,pi_prjcode,pi_prjname,pi_prjkind,pi_logisticscompany,pi_logisticscode,pi_fax,pi_qsr,pi_attach,"
			+ "pi_ordertype,pi_chargeamount,pi_hassendedi,pi_pdastatus,syssendedidate_,pi_monthenddate,pi_intype,pi_unautopostin,pi_cancelreason,? from prodinout where pi_id=?";
	final static String INSERT_PRODIODET_DEL = "insert into prodiodetail_del(pd_id,pd_inoutno,pd_piclass,pd_pdno,pd_ordercode,pd_orderdetno,pd_description,pd_plancode,pd_batchcode,"
			+ "pd_prodcode,pd_prodmadedate,pd_inqty,pd_outqty,pd_notinqty,pd_notoutqty,pd_orderprice,pd_price,pd_avprice,pd_sendprice,pd_taxrate,pd_taxtotal,pd_total,pd_remark,"
			+ "pd_location,pd_purcqty,pd_discount,pd_seller,pd_orderqty,pd_sdid,pd_custprodcode,pd_sellercode,pd_pocode,pd_whcode,pd_whname,pd_barcode,pd_netprice,pd_length,pd_width,"
			+ "pd_beipin,pd_invoqty,pd_thisvoqty,pd_bsqty,pd_cartons,pd_outerboxgw,pd_outerboxnw,pd_mmid,pd_beipininqty,pd_beipinoutqty,pd_wccode,pd_reply,pd_replydate,pd_vendorreply,"
			+ "pd_vendorreplydate,pd_textbox,pd_nxlh,pd_departmentcode,pd_departmentname,pd_discountamount,pd_discountprice,pd_flowcode,pd_accountstatus,pd_taxamount,pd_taxprice,pd_piid,"
			+ "pd_orderid,pd_warehouseid,pd_roughprice,pd_profitrate,pd_invocode,pd_vacode,pd_snid,pd_status,pd_auditstatus,pd_arbillqty,pd_macode,pd_prodjitype,pd_thisvoprice,pd_thistaxtotal,pd_sncode,"
			+ "pd_totaloutqty,pd_department,pd_returnqty,pd_invototal,pd_monthinvoqty,pd_petotal,pd_inwhcode,pd_inwhname,pd_accountstatuscode,pd_nettotal,pd_color,pd_bomcost,pd_qcid,pd_notinqty1,"
			+ "pd_outqty1,pd_showqty,pd_ordertotal,pd_showprice,pd_surplusamount,pd_turngsqty,pd_togsdid,pd_showinvototal,pd_showinvoqty,pd_batchid,pd_prodid,pd_whid,pd_flowid,pd_pricekind,pd_cdid,"
			+ "pd_toesdid,pd_turnesqty,pd_yqty,pd_tqty,pd_ioid,pd_customprice,pd_unitpackage,pd_esqty,pd_gsqty,pd_bonded,pd_model,pd_bgxh,pd_anid,pd_skstatus,pd_beginqty,pd_nowsumqty,pd_mdprice,pd_mrid,"
			+ "pd_mrok,pd_auditstatuscode,pd_cartonno,pd_outboxheight,pd_outboxlength,pd_outboxwidth,pd_scaleremark,pd_paymentcode,pd_laborprice,pd_manufactprice,pd_radid,pd_acceptqty,pd_isreplenishment,"
			+ "pd_custprodspec,pd_forecastdetno,pd_stdid,pd_qctype,pd_noticeid,pd_sfdqty,pd_barcodeinqty,pd_barcodeoutqty,pd_qbdid,pd_bcid,pd_ycheck,pd_checkqty,pd_vendorrate,pd_padid,pd_bbamount,pd_cyamount,"
			+ "pd_mrpclosed,pd_originalqty,pd_originaldetno,pd_prjcode,pd_prjname,pd_vendercode,pd_vendername,pd_salecode,pd_custcode,pd_custname,pd_remark2,pd_remark3,pd_mantissapackage,pd_inlocation,pd_sourcebatch,"
			+ "pd_fee,pd_cgprice,pd_cgcurrency,pd_custproddetail,pd_commissionrate,pd_bqty,pd_zqty,pd_jobcode,pd_purcrate,pd_mcid,pd_purcinqty,pd_purcoutqty,pd_saledetno,pd_topmothercode) select pd_id,pd_inoutno,pd_piclass,"
			+ "pd_pdno,pd_ordercode,pd_orderdetno,pd_description,pd_plancode,pd_batchcode,pd_prodcode,pd_prodmadedate,pd_inqty,pd_outqty,pd_notinqty,pd_notoutqty,pd_orderprice,pd_price,pd_avprice,pd_sendprice,pd_taxrate,"
			+ "pd_taxtotal,pd_total,pd_remark,pd_location,pd_purcqty,pd_discount,pd_seller,pd_orderqty,pd_sdid,pd_custprodcode,pd_sellercode,pd_pocode,pd_whcode,pd_whname,pd_barcode,pd_netprice,pd_length,pd_width,pd_beipin,"
			+ "pd_invoqty,pd_thisvoqty,pd_bsqty,pd_cartons,pd_outerboxgw,pd_outerboxnw,pd_mmid,pd_beipininqty,pd_beipinoutqty,pd_wccode,pd_reply,pd_replydate,pd_vendorreply,pd_vendorreplydate,pd_textbox,pd_nxlh,pd_departmentcode,"
			+ "pd_departmentname,pd_discountamount,pd_discountprice,pd_flowcode,pd_accountstatus,pd_taxamount,pd_taxprice,pd_piid,pd_orderid,pd_warehouseid,pd_roughprice,pd_profitrate,pd_invocode,pd_vacode,pd_snid,pd_status,"
			+ "pd_auditstatus,pd_arbillqty,pd_macode,pd_prodjitype,pd_thisvoprice,pd_thistaxtotal,pd_sncode,pd_totaloutqty,pd_department,pd_returnqty,pd_invototal,pd_monthinvoqty,pd_petotal,pd_inwhcode,pd_inwhname,pd_accountstatuscode,"
			+ "pd_nettotal,pd_color,pd_bomcost,pd_qcid,pd_notinqty1,pd_outqty1,pd_showqty,pd_ordertotal,pd_showprice,pd_surplusamount,pd_turngsqty,pd_togsdid,pd_showinvototal,pd_showinvoqty,pd_batchid,pd_prodid,pd_whid,pd_flowid,pd_pricekind,"
			+ "pd_cdid,pd_toesdid,pd_turnesqty,pd_yqty,pd_tqty,pd_ioid,pd_customprice,pd_unitpackage,pd_esqty,pd_gsqty,pd_bonded,pd_model,pd_bgxh,pd_anid,pd_skstatus,pd_beginqty,pd_nowsumqty,pd_mdprice,pd_mrid,pd_mrok,pd_auditstatuscode,"
			+ "pd_cartonno,pd_outboxheight,pd_outboxlength,pd_outboxwidth,pd_scaleremark,pd_paymentcode,pd_laborprice,pd_manufactprice,pd_radid,pd_acceptqty,pd_isreplenishment,pd_custprodspec,pd_forecastdetno,pd_stdid,pd_qctype,"
			+ "pd_noticeid,pd_sfdqty,pd_barcodeinqty,pd_barcodeoutqty,pd_qbdid,pd_bcid,pd_ycheck,pd_checkqty,pd_vendorrate,pd_padid,pd_bbamount,pd_cyamount,pd_mrpclosed,pd_originalqty,pd_originaldetno,pd_prjcode,pd_prjname,"
			+ "pd_vendercode,pd_vendername,pd_salecode,pd_custcode,pd_custname,pd_remark2,pd_remark3,pd_mantissapackage,pd_inlocation,pd_sourcebatch,pd_fee,pd_cgprice,pd_cgcurrency,pd_custproddetail,pd_commissionrate,pd_bqty,"
			+ "pd_zqty,pd_jobcode,pd_purcrate,pd_mcid,pd_purcinqty,pd_purcoutqty,pd_saledetno,pd_topmothercode from prodiodetail where pd_piid=?";

	@Transactional
	@Override
	public void saveProdInOut(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		Object pi_id = store.get("pi_id");
		handlerService.beforeSave(caller, new Object[] { store, grid });
		store.put("pi_statuscode", "UNPOST");
		store.put("pi_status", BaseUtil.getLocalMessage("UNPOST"));
		store.put("pi_printstatuscode", "UNPRINT");
		store.put("pi_printman", SystemSession.getUser().getEm_name());
		store.put("pi_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
		checkFreezeMonth(caller, store.get("pi_date"));
		checkCloseMonth(store.get("pi_date"));
		checkFirstMonth(caller, store.get("pi_date"));
		// 保存ProdInOut
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "ProdInOut"));
		if (!baseDao.isDBSetting("ProdInOut!PurcCheckin", "allowUpdatetRate")) {
			baseDao.execute(
					"update prodinout set pi_rate=nvl((select cm_crrate from currencysmonth where cm_yearmonth=to_char(pi_date,'yyyymm') and cm_crname=pi_currency),1) where pi_id=? and nvl(pi_currency,' ')<>' '",
					pi_id);
		}
		String code = store.get("pi_inoutno").toString();
		boolean isProdIn = baseDao.isProdIn(caller);
		// 保存ProdioDetail
		for (Map<Object, Object> map : grid) {
			map.put("pd_auditstatus", "ENTERING");
			map.put("pd_inoutno", code);
			map.put("pd_piclass", store.get("pi_class"));
			if (!StringUtil.hasText(map.get("pd_whcode"))) {
				map.put("pd_whcode", store.get("pi_whcode"));
				map.put("pd_whname", store.get("pi_whname"));
				map.put("pd_accountstatuscode", "UNACCOUNT");
				map.put("pd_accountstatus", BaseUtil.getLocalMessage("UNACCOUNT"));
			}
			// 0表示未过账;99表示已过帐
			map.put("pd_status", 0);
			if (isProdIn && !StringUtil.hasText(map.get("pd_batchcode"))) {
				map.put("pd_batchcode", baseDao.getBatchcode(caller));
			}
			if (!"ProdInOut!OtherOut".equals(caller) && !"ProdInOut!ExchangeOut".equals(caller) && !"ProdInOut!Sale".equals(caller)) {
				map.put("pd_id", baseDao.getSeqId("PRODIODETAIL_SEQ"));
			}
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "ProdIODetail");
		if (prodInOutDao.isIn(caller)) {
			String err = baseDao
					.executeWithCheck(
							gridSql,
							null,
							"select LOB_CONCAT(err) from (select '同一物料同仓库不能同时入两次相同的批号！行：'||wm_concat(pd_pdno)||'<br>' err from ProdIODetail where "
									+ "pd_piid=" + pi_id
									+ "  and pd_batchcode is not null group by pd_batchcode,pd_whcode,pd_prodcode having count(1) > 1)",
							"select '批号已存在，不能重复入库！行：'||det from (select wm_concat(pd_pdno) det from ProdIODetail where pd_piid="
									+ pi_id
									+ " and pd_batchcode is not null and exists (select 1 from batch where ba_code=pd_batchcode and ba_prodcode=pd_prodcode and ba_whcode=pd_whcode and (nvl(ba_remain,0)<>0 or nvl(ba_inqty,0)<>0))) where det is not null",
							" select '退料类型水口料入库,明细行物料必须全部为水口料,行：'||pdno from (select wm_concat(pd_pdno) pdno from prodinout left join prodiodetail on pd_piid=pi_id "
									+ " left join product on pr_code=pd_prodcode where pd_piid=" + pi_id
									+ " and pi_class='生产退料单' and pi_intype='水口料入库' and  nvl(pr_putouttoint,0)=0"
									+ ") where pdno is not null");
			if (err != null)
				BaseUtil.showError(err);

		} else {
			if (!"ProdInOut!OtherOut".equals(caller) && !"ProdInOut!ExchangeOut".equals(caller) && !"ProdInOut!Sale".equals(caller)) {
				baseDao.execute(gridSql);
			}
		}
		/**
		 * @ 20170120新增参数限制主表仓库与明细行仓库必须一致
		 */
		if (baseDao.isDBSetting("warehouseCheck") || baseDao.isDBSetting(caller, "warehouseCheck")) {
			Object pi = store.get("pi_whcode");
			Object pin = store.get("pi_purpose");
			if (grid.isEmpty()) {
				Object[] pdwhcode = baseDao.getFieldsDataByCondition("PRODIODETAIL", new String[] { "pd_whcode", "pd_inwhcode" },
						"pd_piid=" + store.get("pi_id"));
				if (pdwhcode != null
						&& ((pdwhcode[0] != null && !pdwhcode[0].equals(pi)) || (pdwhcode[1] != null && !pdwhcode[1].equals(pin)))) {
					BaseUtil.showError("明细行仓库与当前单主表仓库不一致，不允许进行当前操作！");
				}
			} else {
				for (Map<Object, Object> s : grid) {
					Object pd = s.get("pd_whcode");
					Object pdn = s.get("pd_inwhcode");
					if ((pd != null && !pd.equals(pi)) || (pdn != null && !pdn.equals(pin))) {
						BaseUtil.showError("明细行仓库与当前单主表仓库不一致，不允许进行当前操作！");
					}
				}
			}
		}
		/**
		 * @add 20170117 新增限制， 生产领料单、生产补料单、生产退料单、完工入库单保存时明细行的工单类型必须是制造工单
		 *      委外领料单、委外补料单、委外退料单、委外验收单、委外验退单保存时明细行的工单类型必须是委外工单
		 */
		checkMaTaskType(pi_id, caller);

		if ("ProdInOut!Sale".equals(caller) || "ProdInOut!SaleReturn".equals(caller)) {
			baseDao.execute("update ProdIODetail set pd_sdid=(select sd_id from saledetail where pd_ordercode=sd_code and pd_orderdetno=sd_detno) where nvl(pd_ordercode,' ')<>' ' and pd_piclass in ('出货单','销售退货单') and pd_piid="
					+ store.get("pi_id"));
		}
		baseDao.execute("update ProdIODetail set pd_inoutno=(select pi_inoutno from prodinout where pd_piid=pi_id) where pd_piid="
				+ store.get("pi_id") + " and not exists (select 1 from prodinout where pd_inoutno=pi_inoutno)");
		baseDao.getEndDate(caller, store.get("pi_id"));
		useDefaultTax(caller, store.get("pi_id"));
		if ("ProdInOut!SaleBorrow".equals(caller)) {
			baseDao.execute("update prodiodetail set pd_reply='未归还' where pd_piid=" + store.get("pi_id") + " and pd_piclass='借货归还单'");
		}
		getTotal(store.get("pi_id"), caller);
		updatepdPrice(Integer.parseInt(store.get("pi_id").toString()), caller);
		if (caller.equals("ProdInOut!Make!Return") || caller.equals("ProdInOut!OutsideReturn")) {
			baseDao.execute("update prodiodetail a set pd_prodmadedate=(select min(ba_date) from prodiodetail b left join batch on b.pd_batchid=ba_id where a.pd_ordercode=b.pd_ordercode and a.pd_prodcode=b.pd_prodcode and nvl(b.pd_batchcode,' ')<>' ' and ((b.pd_piclass ='生产领料单' and a.pd_piclass='生产退料单') or (b.pd_piclass ='委外领料单' and a.pd_piclass='委外退料单'))) where pd_piid="
					+ pi_id + " and pd_prodmadedate is null");
		} else {
			baseDao.execute("update prodiodetail set pd_prodmadedate=(select pi_date from prodinout where pd_piid=pi_id) where pd_piid="
					+ pi_id + " and pd_prodmadedate is null");
		}
		// pd_salecode更新
		if ("ProdInOut!Sale".equals(caller) || "ProdInOut!SaleReturn".equals(caller) || "ProdInOut!SaleAppropriationOut".equals(caller)) {
			// 出货单、退货单、销售拨出单pd_salecode=pd_ordercode
			baseDao.execute("update prodiodetail set pd_salecode=pd_ordercode where pd_piid=?", store.get("pi_id"));
		} else if ("ProdInOut!Make!In".equals(caller) || "ProdInOut!OutsideCheckIn".equals(caller) || "ProdInOut!DefectIn".equals(caller)) {
			// 完工入库单、委外验收单、不良品入库单
			baseDao.execute("update prodiodetail set pd_salecode=(select max(ma_salecode) from make where ma_code=pd_ordercode) "
					+ "where pd_piid=?", store.get("pi_id"));
		} else if ("ProdInOut!PurcCheckin".equals(caller)) {// 采购验收单根据采购单号、序号，更新入库销售单号、序号
			baseDao.execute(
					"update prodiodetail set (pd_salecode,pd_saledetno,pd_topmothercode)=(select d.pd_salecode,d.pd_saledetno,d.pd_topmothercode from purchase left join purchasedetail d on pu_id=pd_puid where "
							+ " pd_ordercode=pu_code and pd_orderdetno=pd_detno) where pd_piid=?", store.get("pi_id"));
		}
		// @add20171103生产退料单、委外退料单、拆件入库单、完工入库单、委外验收单,根据工单的订单号、订单序号能匹配到销售单号和序号（如果是预测单则不锁批记录）则更新到入库单明细的销售单号、序号
		if ("ProdInOut!OutsideReturn".equals(caller) || "ProdInOut!OutsideCheckIn".equals(caller) || "ProdInOut!Make!In".equals(caller)
				|| "ProdInOut!PartitionStockIn".equals(caller) || "ProdInOut!Make!Return".equals(caller)) {
			baseDao.execute(
					"update prodiodetail set (pd_salecode,pd_saledetno,pd_topmothercode)=(select ma_salecode,ma_saledetno,ma_topmothercode from make where pd_ordercode=ma_code)"
							+ " where pd_piid=? and exists (select 1 from make left join sale on sa_code=ma_salecode left join saledetail on sd_said=sa_id and sd_detno=ma_saledetno where pd_ordercode=ma_code and sd_detno>0)",
					pi_id);

		}
		// 记录操作
		baseDao.logger.save(caller, "pi_id", store.get("pi_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
		if ("ProdInOut!OtherIn".equals(caller) || "ProdInOut!OtherOut".equals(caller)) {// 欧盛其他出入库单，主表项目编号更新到明细设备编号中
			if (baseDao.isDBSetting(caller, "writePjToTopmother")) {
				baseDao.execute("update prodiodetail set pd_topmothercode=? where pd_piid=?", store.get("pi_prjcode"), pi_id);
			}
		}
		// 20171219，其它出库单、拨出单保存更新出库数不允许超可用库存
		if (baseDao.isDBSetting(caller, "checkCanUseOnhand")) {// 采用存储过程
			String res = baseDao.callProcedure("SP_CHECKCANUSEONHAND", new Object[] { store.get("pi_class"), pi_id });
			if (res != null && !res.trim().equals("")) {
				BaseUtil.showError(res);
			}
		}
		// @add20170525 保存,更新之后批号不为空或者批号id 大于0的，批号ID和批号不对应的 更新批号id，
		baseDao.execute(
				"UPDATE PRODIODETAIL SET PD_BATCHID=(SELECT nvl(max(ba_id),0) FROM BATCH WHERE BA_CODE=PD_BATCHCODE AND BA_WHCODE=PD_WHCODE and ba_prodcode=pd_prodcode)"
						+ " where pd_piid=? and (nvl(pd_batchcode,' ')<>' ' or nvl(pd_batchid,0)>0) and pd_outqty>0"
						+ " and (pd_batchcode,nvl(pd_batchid,0)) not in (select ba_code,ba_id from batch)", store.get("pi_id"));

		if ("ProdInOut!Sale".equals(caller)) {
			tipSellerBatch(caller, store.get("pi_id"));
		}
		// maz 其它出库单、借货出货单的客户物料编号 保存后抓取客户编号、名称和规格 2017080360
		if ("ProdInOut!OtherOut".equals(caller) || "ProdInOut!SaleBorrow".equals(caller)) {
			baseDao.execute("update ProdIODetail a set (pd_custprodcode,pd_custproddetail,pd_custprodspec)=(select pc_custprodcode,pc_custproddetail,pc_custprodspec from productcustomer where a.pd_prodcode=pc_prodcode and pc_custcode='"
					+ store.get("pi_cardcode") + "') where pd_piid=" + store.get("pi_id") + "");
		}
		// 出库类型单据更新出库单备料状态
		updatePdaStatus(caller, store.get("pi_id"));

		// 重新刷新对应单据的领料状态和明细的已转数
		if ("ProdInOut!Picking".equals(caller) || "ProdInOut!OutsidePicking".equals(caller)) {
			String sql = "select distinct mm_maid,mm_id from prodiodetail left join makematerial on mm_code=pd_ordercode and "
					+ " mm_detno=pd_orderdetno where pd_piid=? and mm_id is not null";
			SqlRowList rl = baseDao.queryForRowSet(sql, store.get("pi_id"));
			List<Integer> mmids = new ArrayList<Integer>();
			List<Integer> maids = new ArrayList<Integer>();
			while (rl.next()) {
				maids.add(rl.getInt("mm_maid"));
				mmids.add(rl.getInt("mm_id"));
			}
			if (maids.size() > 0 && mmids.size() > 0) {
				refreshmaketurn(BaseUtil.parseList2Str(mmids, ",", true), BaseUtil.parseList2Str(maids, ",", true));
			}
		}
		if (baseDao.isDBSetting(caller, "getPriceByNoOrder")) {
			getSalePrice(store.get("pi_id"));
		}
	}

	@Override
	public void deleteProdInOut(String caller, int pi_id) {
		try {
			baseDao.execute("select pi_id from prodinout where pi_id=? for update nowait", pi_id);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			BaseUtil.showError("当前单据有其他人正在操作！");
		}
		// 只能删除[在录入]的供应商资料
		Object[] status = baseDao.getFieldsDataByCondition("ProdInOut", new String[] { "pi_invostatuscode", "pi_statuscode",
				"pi_sourcecode" }, "pi_id=" + pi_id);
		int count = baseDao.getCountByCondition("ProdIODetail", "pd_status=99 AND pd_piid=" + pi_id);
		StateAssert.delOnlyEntering(status[0]);
		if (count > 0) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.deletedetail_onlyEntering"));
		}
		// maz 其它入库单、采购验收单删除时 如果是送样单转入的 回写转单状态 2017080696
		if ("ProdInOut!OtherIn".equals(caller) || "ProdInOut!PurcCheckin".equals(caller)) {
			Pattern pattern = Pattern.compile("[0-9]*");
			Object ss_id = status[2] == null ? 0 : status[2];
			Matcher isNum = pattern.matcher(ss_id.toString());
			ss_id = isNum.matches() ? status[2] : 0;
			SqlRowList rs = baseDao.queryForRowSet("select ss_code from SendSample where ss_id=" + ss_id);
			if (rs.next()) {
				baseDao.execute("update SendSample set ss_condition='' where ss_code='" + rs.getObject("ss_code") + "'");
			}
		}
		// 出入库单上传状态，处于上传过程中的单据，不允许操作
		String sendStatus = baseDao.getFieldValue("ProdInOut", "pi_sendstatus", "pi_id=" + pi_id, String.class);
		StateAssert.onSendingLimit(sendStatus);
		checkVoucher(pi_id);
		SqlRowList pd = null;
		if ("ProdInOut!Sale".equals(caller)) {// 出货单
			pd = baseDao
					.queryForRowSet(
							"select pd_orderdetno,pd_ordercode,pd_outqty from ProdInOut left join ProdIODetail on pd_piid=pi_id where pi_id=? and pd_piclass='出货单' and nvl(pd_ordercode,' ')<>' ' and nvl(pd_orderdetno,0)<>0 and nvl(pd_snid,0)=0",
							pi_id);
		}
		if ("ProdInOut!ProcessIn".equals(caller) || "ProdInOut!ProcessOut".equals(caller)) {// 加工验收单、加工验退单
			pd = baseDao
					.queryForRowSet(
							"select pd_orderdetno,pd_ordercode,pd_id,nvl(pd_inqty,0)+nvl(pd_outqty,0) pd_qty from ProdInOut left join ProdIODetail on pd_piid=pi_id where pi_id=? and nvl(pd_ordercode,' ')<>' ' and nvl(pd_orderdetno,0)<>0",
							pi_id);
		}
		// @add 20170523如果是出库类型的单据有已备料数据，不允许删除，需要提示用户先撤销备料数据
		if (prodInOutDao.isOut(caller)) {// 是否为出库类型单据
			SqlRowList rs = baseDao.queryForRowSet("select count(1)cn from barcodeio where bi_piid=?", pi_id);
			if (rs.next() && rs.getInt("cn") > 0) {
				BaseUtil.showError("出库单存在出库条码采集不允许删除，请先撤销备料数据再删除单据！");
			}
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, pi_id);
		if ("ProdInOut!StockLoss".equals(caller)) {// 盘亏调整单
			SqlRowList rs = baseDao.queryForRowSet(
					"select nvl(pd_stdid,0) from ProdInOut left join ProdIODetail on pd_piid=pi_id where pi_id=?", pi_id);
			while (rs.next()) {
				if (rs.getInt(1) > 0) {
					baseDao.updateByCondition("StockTakingDetail", "std_outcode=null", "std_id=" + rs.getInt(1));
				}
			}
		}
		if ("ProdInOut!StockProfit".equals(caller)) {// 盘盈调整单
			SqlRowList rs = baseDao.queryForRowSet(
					"select nvl(pd_stdid,0) from ProdInOut left join ProdIODetail on pd_piid=pi_id where pi_id=?", pi_id);
			while (rs.next()) {
				if (rs.getInt(1) > 0) {
					baseDao.updateByCondition("StockTakingDetail", "std_incode=null", "std_id=" + rs.getInt(1));
				}
			}
		} else if ("ProdInOut!DefectOut".equals(caller) || "ProdInOut!PurcCheckin".equals(caller)) {
			// 来源为收料单
			baseDao.execute("update verifyapplydetail set vad_yqty=0 where vad_code=(select pi_sourcecode from ProdInOut where pi_id="
					+ pi_id + " and PI_REFNO='采购收料单' and pi_class='采购验收单')");
			baseDao.execute("update verifyapply set va_turnstatus=null,va_turnstatuscode=null where va_code=(select pi_sourcecode from ProdInOut where pi_id="
					+ pi_id + " and PI_REFNO='采购收料单' and pi_class='采购验收单')");
			// 特采产生的 不良品出库和采购验收单
			Object[] objs = baseDao.getFieldsDataByCondition("ProdInOut", new String[] { "pi_type", "pi_fromcode" }, "pi_id=" + pi_id
					+ " and pi_sourcecode is not null");
			if (objs != null && "特采".equals(objs[0])) {
				boolean bool = baseDao.checkIf("ProdInOut", "pi_inoutno='" + objs[1]
						+ "' and (pi_invostatuscode<>'ENTERING' OR pi_statuscode='POSTED')");
				if (bool) {
					BaseUtil.showError("当前来源为特采单据存在非在录入的关联单据 编号为:" + objs[1] + " 无法删除!");
				} else {
					String queryField = "ProdInOut!DefectOut".equals(caller) ? "pd_outqty" : "pd_inqty";
					baseDao.updateByCondition("prodiodetail ", "pd_yqty=NVL(pd_yqty,0)-NVL((select " + queryField
							+ " from prodiodetail b where pd_id=b.pd_orderid and pd_piid=" + pi_id + "),0)",
							"pd_id in (select pd_orderid from prodiodetail where pd_piid=" + pi_id + ")");
					baseDao.deleteByCondition("ProdInOut", "pi_inoutno='" + objs[1] + "'");
					baseDao.deleteByCondition("ProdIODetail", "pd_inoutno='" + objs[1] + "'");
				}
			}
		} else if ("ProdInOut!GoodsIn".equals(caller)) {// 用品验收单
			// 还原到用品采购
			List<Object[]> data = baseDao.getFieldsDatasByCondition("prodiodetail", new String[] { "pd_inqty", "pd_orderid" }, "pd_piid="
					+ pi_id);
			List<String> sqls = new ArrayList<String>();
			for (Object[] os : data) {
				String sql = "update OAPurchaseDetail set od_yqty=nvl(od_yqty,0)-" + os[0] + " where od_id=" + os[1];
				sqls.add(sql);
			}
			baseDao.execute(sqls);
		}
		if ("ProdInOut!GoodsPicking".equals(caller)) {// 用品领用单
			List<Object[]> gridData = baseDao.getFieldsDatasByCondition("ProdIODetail", new String[] { "nvl(pd_outqty,0)", "pd_orderid" },
					"nvl(pd_orderid,0)<>0 and pd_piid=" + pi_id);
			for (Object[] d : gridData) {
				baseDao.execute("update oaapplicationdetail set od_turnlyqty=nvl(od_turnlyqty,0)-" + d[0] + "where od_id=" + d[1]);
			}
		}
		if ("ProdInOut!OtherIn".equals(caller) || "ProdInOut!OtherOut".equals(caller)) {// 其它出库单、其它入库单
			baseDao.execute("update prodinout a set pi_relativeplace=null where exists (select * from prodinout b where b.pi_relativeplace=a.pi_inoutno and b.pi_id="
					+ pi_id + ") and pi_class in ('其它入库单','其它出库单')");
		}
		if ("ProdInOut!OtherPurcOut".equals(caller) || "ProdInOut!OtherPurcIn".equals(caller)) {// 其它采购出库单、其它采购入库单
			baseDao.execute("update prodinout a set pi_relativeplace=null where exists (select * from prodinout b where b.pi_relativeplace=a.pi_inoutno and b.pi_id="
					+ pi_id + ") and pi_class in ('其它采购出库单','其它采购入库单')");
		}
		if ("ProdInOut!SaleAppropriationOut".equals(caller) || "ProdInOut!SaleAppropriationIn".equals(caller)) {// 销售拨出单、销售拨入单
			baseDao.execute("update prodinout a set pi_tocode=null where exists (select * from prodinout b where b.pi_tocode=a.pi_inoutno and b.pi_id="
					+ pi_id + ") and pi_class in ('销售拨入单','销售拨出单')");
		}
		if ("ProdInOut!AppropriationIn".equals(caller) || "ProdInOut!AppropriationOut".equals(caller)) {// 拨入单、拨出单
			baseDao.execute("update prodinout a set pi_relativeplace=null where exists (select * from prodinout b where b.pi_relativeplace=a.pi_inoutno and b.pi_id="
					+ pi_id + ") and pi_class in ('拨入单','拨出单')");
		}
		if ("ProdInOut!ExchangeOut".equals(caller) || "ProdInOut!ExchangeIn".equals(caller)) {// 换货入库单、换货出库单
			baseDao.execute("update prodinout a set pi_relativeplace=null where exists (select * from prodinout b where b.pi_relativeplace=a.pi_inoutno and b.pi_id="
					+ pi_id + ") and pi_class in ('换货入库单','换货出库单')");
		}
		// 删除前取参数配置是否勾选了作废，如果勾选先插入_del表
		Object configs_ = baseDao.getFieldDataByCondition("configs", "data", "code='cancelProdInOut' and caller='sys'");
		if (StringUtil.hasText(configs_) && "1".equals(configs_)) {
			baseDao.execute(INSERT_PRODIO_DEL, new Object[] { caller, pi_id });
			baseDao.execute(INSERT_PRODIODET_DEL, new Object[] { pi_id });
			baseDao.execute("update prodinout_del set pi_status='已作废',pi_invostatus='已作废',pi_invostatuscode='Cancel',pi_statuscode='Cancel',pi_caller='"
					+ caller + "' where pi_id=" + pi_id);
		}
		// @add 20180504如果是入库类型的话，存在条码barcodeio的，条码也需要删除
		if (prodInOutDao.isIn(caller)) {// 是否为入库类型单据
			int countBarcodeio = baseDao.getCount("select count(1) cn from barcodeio where bi_piid=" + pi_id);
			if (countBarcodeio > 0) {
				baseDao.execute("delete from barcodeio where bi_piid = ?", pi_id);
			}
		}
		baseDao.deleteById("ProdInOut", "pi_id", pi_id);
		baseDao.deleteByCondition("ProdIODetail", "pd_piid=" + pi_id);
		if ("ProdInOut!Sale".equals(caller)) {// 出货单
			Object y = 0;
			Object r = 0;
			Object sacode = null;
			Object sddetno = 0;
			Object[] sd = null;
			while (pd.next()) {
				sacode = pd.getObject("pd_ordercode");
				sddetno = pd.getObject("pd_orderdetno");
				sd = baseDao.getFieldsDataByCondition("Sale left join SaleDetail on sd_said=sa_id",
						new String[] { "sd_id", "nvl(sd_qty,0)" }, "sa_code='" + sacode + "' and sd_detno=" + sddetno);
				if (sd != null) {
					y = baseDao.getFieldDataByCondition("ProdIODetail", "sum(nvl(pd_outqty,0))", "pd_piclass='出货单' and pd_sdid=" + sd[0]);
					r = baseDao.getFieldDataByCondition("ProdIODetail left join ProdInOut on pd_piid=pi_id", "sum(nvl(pd_inqty,0))",
							"pd_piclass='销售退货单' and pi_statuscode='POSTED' and pd_ordercode='" + sacode + "' and pd_orderdetno=" + sddetno);
					y = y == null ? 0 : y;
					r = r == null ? 0 : r;
					if (Double.parseDouble(sd[1].toString()) < Double.parseDouble(y.toString()) + pd.getGeneralDouble("pd_outqty")
							- Double.parseDouble(r.toString())) {
						baseDao.execute("update saledetail set sd_yqty="
								+ (Double.parseDouble(y.toString()) - Double.parseDouble(r.toString())) + " where sd_id=" + sd[0]);
					}
				}
			}
		}
		if ("ProdInOut!ProcessIn".equals(caller) || "ProdInOut!ProcessOut".equals(caller)) {// 加工验收单、加工验退单
			otherExplistDao.restoreSourceYqty(pd, null);
		}
		// 记录操作
		baseDao.logger.delete(caller, "pi_id", pi_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, pi_id);
	}

	@Transactional
	@Override
	public void updateProdInOutById(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object[] status = baseDao.getFieldsDataByCondition("ProdInOut", "pi_invostatuscode,pi_statuscode,pi_printstatuscode,pi_date",
				"pi_id=" + store.get("pi_id"));
		if (status != null) {
			StateAssert.updateOnlyEntering(status[0]);
		} else {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.updatedetail_onlyEntering"));
		}
		int count = baseDao.getCountByCondition("ProdIODetail", "pd_status=99 AND pd_piid=" + store.get("pi_id"));
		if (count > 0) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.updatedetail_onlyEntering"));
		}
		Object pi_id = store.get("pi_id");
		checkVoucher(pi_id);
		// 未过账
		if (!"UNPOST".equals(status[1])) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyUnPost"));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		checkFreezeMonth(caller, store.get("pi_date"));
		checkCloseMonth(store.get("pi_date"));
		checkFirstMonth(caller, store.get("pi_date"));
		// 修改ProdInOut
		if (!StringUtil.hasText(status[2])) {
			store.put("pi_printstatuscode", "UNPRINT");
			store.put("pi_printman", SystemSession.getUser().getEm_name());
			store.put("pi_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
		}
		store.put("pi_updateman", SystemSession.getUser().getEm_name());
		store.put("pi_updatedate", DateUtil.currentDateString(null));
		if (store.containsKey("pi_date")) {
			Object pi_date = store.get("pi_date");
			if (StringUtil.hasText(pi_date) && StringUtil.hasText(status[3])
					&& DateUtil.compare(status[3].toString(), pi_date.toString()) == 0) {
				store.remove("pi_date");
			}
		}
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProdInOut", "pi_id");
		baseDao.execute(formSql);
		if (!baseDao.isDBSetting("ProdInOut!PurcCheckin", "allowUpdatetRate")) {
			baseDao.execute(
					"update prodinout set pi_rate=nvl((select cm_crrate from currencysmonth where cm_yearmonth=to_char(pi_date,'yyyymm')"
							+ " and cm_crname=pi_currency),1) where pi_id=? and nvl(pi_currency,' ')<>' '", pi_id);
		}

		// @add 20170527 限制已有出库采集明细行不允许手动修改物料编号,批次号，仓库
		boolean isOut = prodInOutDao.isOut(caller);
		if (baseDao.isDBSetting("disUpHaveBarcode") && isOut) {
			for (Map<Object, Object> s : gstore) {
				if (!NumberUtil.isEmpty(s.get("pd_id"))) {
					SqlRowList rs = baseDao.queryForRowSet("select pd_pdno,pd_prodcode,pd_batchcode,pd_whcode from prodiodetail "
							+ " left join barcodeio on bi_piid=pd_piid and bi_batchid=pd_batchid where pd_id=? and bi_outqty>0",
							s.get("pd_id"));
					if (rs.next()) {
						if (s.get("pd_batchcode") == null
								|| (s.get("pd_batchcode") != null && !s.get("pd_batchcode").equals(rs.getString("pd_batchcode")))) {
							BaseUtil.showError("行号:" + rs.getString("pd_pdno") + " 已有出库采集不允许修改批次号，请先撤销出库再修改！");
						} else if (s.get("pd_whcode") == null
								|| (s.get(" pd_whcode") != null && !s.get("pd_whcode").equals(rs.getString("pd_whcode")))) {
							BaseUtil.showError("行号:" + rs.getString("pd_pdno") + " 已有出库采集不允许修改仓库，请先撤销出库再修改！");
						} else if (s.get("pd_prodcode") == null
								|| (s.get("pd_prodcode") != null && !s.get("pd_prodcode").equals(rs.getString("pd_prodcode")))) {
							BaseUtil.showError("行号:" + rs.getString("pd_pdno") + " 已有出库采集不允许修改物料编号，请先撤销出库再修改！");
						}
					}
				}
			}
		}
		SqlRowList oldpd = null;
		if ("ProdInOut!ProcessIn".equals(caller) || "ProdInOut!ProcessOut".equals(caller)) {// 加工验收单、加工验退单
			oldpd = baseDao
					.queryForRowSet(
							"select pd_orderdetno,pd_ordercode,pd_id,nvl(pd_inqty,0)+nvl(pd_outqty,0) pd_qty from ProdInOut left join ProdIODetail on pd_piid=pi_id where pi_id=? and nvl(pd_ordercode,' ')<>' ' and nvl(pd_orderdetno,0)<>0",
							pi_id);
		}
		String code = store.get("pi_inoutno").toString();
		count = baseDao.getCountByCondition("ProdInOut", "PI_REFNO='采购收料单' and pi_class='采购验收单' and pi_id=" + pi_id);
		boolean isProdIn = baseDao.isProdIn(caller);
		List<Integer> maids = new ArrayList<Integer>();
		List<Integer> mmids = new ArrayList<Integer>();
		List<String> updateSqls = new ArrayList<String>();
		List<String> updatePurcqtysqls = new ArrayList<String>();
		StringBuffer errBuff = new StringBuffer();
		for (Map<Object, Object> s : gstore) {
			s.put("pd_piclass", store.get("pi_class"));
			s.put("pd_inoutno", code);
			if (NumberUtil.isEmpty(s.get("pd_id"))) {// is new
				s.put("pd_auditstatus", "ENTERING");
				s.put("pd_accountstatuscode", "UNACCOUNT");
				s.put("pd_accountstatus", BaseUtil.getLocalMessage("UNACCOUNT"));
				s.put("pd_status", 0);
				// allow self-create
				if (!"ProdInOut!OtherOut".equals(caller) && !"ProdInOut!ExchangeOut".equals(caller) && !"ProdInOut!Sale".equals(caller)) {
					updateSqls.add(SqlUtil.getInsertSql(s, "ProdIoDetail", "pd_id"));
				}
				if ("ProdInOut!Picking".equals(caller) || "ProdInOut!OutsidePicking".equals(caller)) {
					SqlRowList madata = baseDao.queryForRowSet(
							"select ma_id,mm_id from MakeMaterial left join Make on ma_id=mm_maid WHERE ma_code=? and mm_detno=?",
							s.get("pd_ordercode"), s.get("pd_orderdetno"));
					if (madata.next()) {
						mmids.add(madata.getInt("mm_id"));
						maids.add(madata.getInt("ma_id"));
					}
				}
			} else {
				Object ioid = s.get("pd_ioid");
				if (!NumberUtil.isEmpty(ioid)) {
					Double tqty = Double.parseDouble(s.get("pd_inqty").toString()) + Double.parseDouble(s.get("pd_outqty").toString());
					Double yqty = baseDao.queryForObject(
							"select sum(nvl(pd_outqty,0) + nvl(pd_inqty,0)) from Prodiodetail where pd_ioid=? and pd_id<>?", Double.class,
							ioid, s.get("pd_id"));
					yqty = yqty == null ? 0 : yqty;
					tqty = tqty == null ? 0 : tqty;
					SqlRowList source = baseDao.queryForRowSet(
							"select nvl(pd_outqty,0)+nvl(pd_inqty,0) qty ,pd_piclass,pd_inoutno from Prodiodetail where pd_id=?", ioid);
					if (source.next()) {
						if (yqty + tqty > source.getGeneralDouble("qty")) {
							errBuff.append("行号｛" + s.get("pd_pdno") + "]填写数量超过来源单数量，无法转出.<br>" + source.getString("pd_piclass") + "["
									+ source.getString("pd_inoutno") + "]数量:" + source.getGeneralDouble("qty") + ",已转数量:" + yqty + ",本次数量:"
									+ tqty + "<hr/>");
						}
					}
				}
				if ("ProdInOut!Sale".equals(caller)) {
					Object pclass = baseDao.getFieldDataByCondition("prodinout", "pi_class",
							"pi_inoutno=(select PI_SOURCECODE from ProdInOut where pi_class='出货单' and pi_id='" + pi_id + "')");
					SqlRowList pd = baseDao
							.queryForRowSet(
									"select pd_orderdetno,pd_ordercode,pd_outqty from ProdInOut left join ProdIODetail on pd_piid=pi_id where pi_id=? and pd_piclass='出货单' and nvl(pd_ordercode,' ')<>' ' and nvl(pd_orderdetno,0)<>0",
									pi_id);
					if ("销售退货单".equals(pclass)) {
						updateReturnSale(s, pd);
					}
				}
				if ("ProdInOut!PurcCheckin".equals(caller) && count > 0) {
					Double tqty = Double.parseDouble(s.get("pd_inqty").toString());
					Double oldqty = baseDao.getSummaryByField("Prodiodetail", "nvl(pd_outqty,0) + nvl(pd_inqty,0)",
							"pd_id=" + s.get("pd_id"));
					if (!oldqty.equals(tqty)) {
						BaseUtil.showError("该单来源为采购收料单，不允许修改明细行数量！");
					}
				}
				/*
				 * 问题反馈2018030046 入库单 入库数量pd_inqty更新时，重新按照比例计算采购单位入库数量
				 */
				if (isProdIn
						&& baseDao.checkIf("prodiodetail", "pd_id=" + s.get("pd_id") + " and nvl(pd_inqty,0)<>nvl(" + s.get("pd_inqty")
								+ ",0)")) {
					updatePurcqtysqls.add("update prodiodetail set pd_purcinqty=nvl(pd_inqty,0) where pd_id=" + pi_id + ""
							+ " and exists (select 1 from product where pd_prodcode=pr_code and nvl(pr_purcunit,pr_unit)=pr_unit)");
					updatePurcqtysqls
							.add("update prodiodetail set pd_purcinqty=pd_inqty/(select case when nvl(pr_purcrate,0)<>0 then pr_purcrate else 1 end "
									+ "from product where pr_code=pd_prodcode) where pd_piid=" + pi_id);
				}
				updateSqls.add(SqlUtil.getUpdateSqlByFormStore(s, "ProdIODetail", "pd_id"));
				if ("ProdInOut!Picking".equals(caller) || "ProdInOut!OutsidePicking".equals(caller)) {
					SqlRowList madata = baseDao
							.queryForRowSet(
									"select ma_id,mm_id,pd_ordercode,pd_orderdetno,pd_outqty from prodiodetail left join make on ma_code=pd_ordercode left join makematerial on mm_maid=ma_id and mm_detno=pd_orderdetno where pd_id=?",
									s.get("pd_id"));
					if (madata.next()) {
						// 制造单号相等 并且 制造序号相等时，当领料数量修改过
						if (madata.getString("pd_ordercode").equals(s.get("pd_ordercode"))
								&& madata.getString("pd_orderdetno").equals(s.get("pd_orderdetno"))
								&& madata.getDouble("pd_outqty") != Double.valueOf((s.get("pd_outqty").toString()))) {
							maids.add(madata.getInt("ma_id"));
							mmids.add(madata.getInt("mm_id"));
						} else if (madata.getString("pd_ordercode").equals(s.get("pd_ordercode"))
								&& !madata.getString("pd_orderdetno").equals(s.get("pd_orderdetno"))) {// 制造单号相等
																										// ，当制造序号修改
							maids.add(madata.getInt("ma_id"));
							mmids.add(madata.getInt("mm_id"));
							SqlRowList mndata = baseDao.queryForRowSet(
									"select ma_id,mm_id from MakeMaterial left join Make on ma_id=mm_maid"
											+ " where ma_code=? and mm_detno=?", s.get("pd_ordercode"), s.get("pd_orderdetno"));
							if (mndata.next()) {
								mmids.add(mndata.getInt("mm_id"));
							}
						} else if (!madata.getString("pd_ordercode").equals(s.get("pd_ordercode"))) {// 制造单号修改
							maids.add(madata.getInt("ma_id"));
							mmids.add(madata.getInt("mm_id"));
							SqlRowList mndata = baseDao.queryForRowSet(
									"select ma_id,mm_id from MakeMaterial left join Make on ma_id=mm_maid"
											+ " where ma_code=? and mm_detno=?", s.get("pd_ordercode"), s.get("pd_orderdetno"));
							if (mndata.next()) {
								mmids.add(mndata.getInt("mm_id"));
								maids.add(mndata.getInt("ma_id"));
							}
						}
					}
				}
			}
		}
		/**
		 * @ 20170120新增参数限制主表仓库与明细行仓库必须一致
		 */
		if (baseDao.isDBSetting("warehouseCheck") || baseDao.isDBSetting(caller, "warehouseCheck")) {
			Object pi = store.get("pi_whcode");
			Object pin = store.get("pi_purpose");
			if (gstore.isEmpty()) {
				Object[] pdwhcode = baseDao.getFieldsDataByCondition("PRODIODETAIL", new String[] { "pd_whcode", "pd_inwhcode" },
						"pd_piid=" + store.get("pi_id"));
				if (pdwhcode != null
						&& ((pdwhcode[0] != null && !pdwhcode[0].equals(pi)) || (pdwhcode[1] != null && !pdwhcode[1].equals(pin)))) {
					BaseUtil.showError("明细行仓库与当前单主表仓库不一致，不允许进行当前操作！");
				}
			} else {
				for (Map<Object, Object> s : gstore) {
					Object pd = s.get("pd_whcode");
					Object pdn = s.get("pd_inwhcode");
					if ((pd != null && !pd.equals(pi)) || (pdn != null && !pdn.equals(pin))) {
						BaseUtil.showError("明细行仓库与当前单主表仓库不一致，不允许进行当前操作！");
					}
				}
			}
		}
		if (errBuff.length() > 0) {
			BaseUtil.showError(errBuff.toString());
		}
		// 当主表信息的委外类型为收口料入库时，校验明细表的水口料标识是否全部都为 是
		if (gstore.size() >= 0 && "水口料入库".equals(store.get("pi_intype")) && "ProdInOut!Make!Return".equals(caller)) {
			String sql = " select '退料类型水口料入库,明细行物料必须全部为水口料,行：'||pdno pdno from (select wm_concat(pd_pdno) pdno from prodinout left join prodiodetail on pd_piid=pi_id "
					+ " left join product on pr_code=pd_prodcode where pd_piid="
					+ pi_id
					+ " and pi_class='生产退料单' and pi_intype='水口料入库' and  nvl(pr_putouttoint,0)=0" + ") where pdno is not null";
			SqlRowList rowList = baseDao.queryForRowSet(sql);
			if (rowList.next()) {
				BaseUtil.showError(rowList.getString("pdno"));
			}
		}
		if (prodInOutDao.isIn(caller)) {
			String err = baseDao
					.executeWithCheck(
							updateSqls,
							null,
							"select LOB_CONCAT(err) from (select '同一物料同仓库不能同时入两次相同的批号！行：'||wm_concat(pd_pdno)||'<br>' err from ProdIODetail where"
									+ " pd_piid=" + pi_id
									+ " and pd_batchcode is not null group by pd_batchcode,pd_whcode,pd_prodcode having count(1) > 1)",
							"select '批号已存在，不能重复入库！行：'||det from (select wm_concat(pd_pdno) det from ProdIODetail where pd_piid="
									+ pi_id
									+ " and pd_batchcode is not null and exists (select 1 from batch where ba_code=pd_batchcode and ba_prodcode=pd_prodcode and ba_whcode=pd_whcode and (nvl(ba_remain,0)<>0 or nvl(ba_inqty,0)<>0))) where det is not null",
							" select '退料类型水口料入库,明细行物料必须全部为水口料,行：'||pdno from (select wm_concat(pd_pdno) pdno from prodinout left join prodiodetail on pd_piid=pi_id "
									+ " left join product on pr_code=pd_prodcode where pd_piid=" + pi_id
									+ " and pi_class='生产退料单' and pi_intype='水口料入库' and  nvl(pr_putouttoint,0)=0"
									+ ") where pdno is not null");
			if (err != null)
				BaseUtil.showError(err);
			baseDao.execute(updatePurcqtysqls);// 更新了库存数量的重新计算采购单位数量
		} else {
			baseDao.execute(updateSqls);
		}

		/**
		 * @add 20170117 新增限制， 生产领料单、生产补料单、生产退料单、完工入库单保存时明细行的工单类型必须是制造工单
		 *      委外领料单、委外补料单、委外退料单、委外验收单、委外验退单保存时明细行的工单类型必须是委外工单
		 */
		checkMaTaskType(pi_id, caller);
		if ("ProdInOut!Sale".equals(caller) || "ProdInOut!SaleReturn".equals(caller)) {
			baseDao.execute(
					"update ProdIODetail set pd_sdid=(select sd_id from saledetail where pd_ordercode=sd_code and pd_orderdetno=sd_detno) where nvl(pd_ordercode,' ')<>' ' and pd_piclass in ('出货单','销售退货单') and pd_piid=?",
					pi_id);
		}
		baseDao.execute(
				"update ProdIODetail set pd_inoutno=(select pi_inoutno from prodinout where pd_piid=pi_id) where pd_piid=? and not exists (select 1 from prodinout where pd_inoutno=pi_inoutno)",
				pi_id);
		useDefaultTax(caller, pi_id);
		if (caller.equals("ProdInOut!Make!Return") || caller.equals("ProdInOut!OutsideReturn")) {
			baseDao.execute(
					"update prodiodetail a set pd_prodmadedate=(select min(ba_date) from prodiodetail b left join batch on b.pd_batchid=ba_id where a.pd_ordercode=b.pd_ordercode and a.pd_prodcode=b.pd_prodcode and nvl(b.pd_batchcode,' ')<>' ' and ((b.pd_piclass ='生产领料单' and a.pd_piclass='生产退料单') or (b.pd_piclass ='委外领料单' and a.pd_piclass='委外退料单'))) where pd_piid=? and pd_prodmadedate is null",
					pi_id);
		} else {
			baseDao.execute(
					"update prodiodetail set pd_prodmadedate=(select pi_date from prodinout where pd_piid=pi_id) where pd_piid=? and pd_prodmadedate is null",
					pi_id);
		}
		if (isProdIn) {
			// 入库类单据:如果pd_location为空，默认等于物料资料里的仓位pr_location
			baseDao.execute(
					"update prodiodetail set pd_location=(select pr_location from product where pr_code=pd_prodcode) where pd_piid=? and nvl(pd_location,' ')=' '",
					pi_id);

			SqlRowList rs = baseDao.queryForRowSet("SELECT pd_id FROM ProdioDetail where pd_piid=? and nvl(pd_batchcode,' ')=' '", pi_id);
			while (rs.next()) {
				baseDao.execute("update prodiodetail set pd_batchcode=? where pd_id=?", baseDao.getBatchcode(caller), rs.getInt("pd_id"));
			}
		}
		baseDao.getEndDate(caller, pi_id);
		List<Object[]> detailField = baseDao.getFieldsDatasByCondition("ProdIoDetail", new String[] { "pd_id", "pd_batchcode" }, "pd_piid="
				+ store.get("pi_id") + " and nvl(pd_batchcode,' ')=' '");
		if (isProdIn) {
			for (Object[] object : detailField) {
				baseDao.updateByCondition("ProdIODetail", "pd_batchcode='" + baseDao.getBatchcode(caller) + "'", "pd_id=" + object[0]);
			}
		}

		if ("ProdInOut!SaleBorrow".equals(caller)) {
			baseDao.execute("update prodiodetail set pd_reply='未归还' where pd_piid=? and pd_piclass='借货归还单'", store.get("pi_id"));
		}
		if ("ProdInOut!PurcCheckout".equals(caller)) {
			baseDao.execute(
					"update prodiodetail set pd_orderprice=(select nvl(pd_price,0) from purchasedetail where pd_ordercode=pd_code and pd_orderdetno=pd_detno) where pd_piid=? and nvl(pd_orderprice,0)=0",
					store.get("pi_id"));
		}

		baseDao.execute(
				"update prodiodetail set pd_accountstatuscode='UNACCOUNT',pd_accountstatus='" + BaseUtil.getLocalMessage("UNACCOUNT")
						+ "' where pd_piid=?", store.get("pi_id"));
		// 更新时,更新明细的金额,一般都是更新后直接过账的
		if ("ProdInOut!GoodsIn".equals(caller)) {// 用品验收单
			// 只处理从申请单直接转过来的.即pd_orderid有值的
			String sql = "select od_id,od_neednumber,od_yqty,sum(NVL(pd_inqty,0)-NVL(pd_outqty,0)) pd_inqty,op_code,od_detno "
					+ " from ProdIODetail left join oapurchasedetail on pd_orderid=od_id left join Oapurchase on od_oaid=op_id"
					+ " where pd_orderid in (select pd_orderid from ProdIODetail where pd_piid="
					+ store.get("pi_id")
					+ " )and (pd_piclass='用品验收单' or (pd_piclass='用品验退单' and pd_status=99)) group by od_neednumber,od_yqty,od_id,op_code,od_detno having sum(NVL(pd_inqty,0)-NVL(pd_outqty,0))>od_neednumber";
			SqlRowList rs = baseDao.queryForRowSet(sql);
			if (rs.next()) {// 明细数量不能超过原单据的批准数量
				BaseUtil.showError("用品采购单" + rs.getString("op_code") + ",序号" + rs.getInt("od_detno") + "的需求数量为:"
						+ rs.getDouble("od_neednumber") + ".已转数为:" + rs.getDouble("pd_inqty") + ".超过了需求数,请核对后重试!");
			}
			String query = "select NVL(sum(NVL(pd_inqty,0)-NVL(pd_outqty,0)),0) pd_inqty,pd_orderid from ProdIODetail "
					+ "where pd_orderid in (select pd_orderid from ProdIODetail where pd_piid=" + store.get("pi_id")
					+ ") and (pd_piclass='用品验收单' or (pd_piclass='用品验退单' and pd_status=99)) group by pd_orderid";
			SqlRowList rs1 = baseDao.queryForRowSet(query);
			List<String> sqls = new ArrayList<String>();
			while (rs1.next()) {
				String updateSql = "update oapurchasedetail set od_yqty=" + rs1.getDouble("pd_inqty") + " where od_id="
						+ rs1.getInt("pd_orderid");
				sqls.add(updateSql);
			}
			baseDao.execute(sqls);
			baseDao.execute("update ProdIODetail set pd_ordertotal=nvl(pd_inqty,0)*nvl(pd_orderprice,0) where pd_piid=?",
					store.get("pi_id"));
		}
		// 还原用品申请单的转用品领用数量
		if ("ProdInOut!GoodsPicking".equals(caller)) {// 用品领用
			// 只处理从申请单直接转过来的.即pd_orderid有值的
			String sql = "select od_id,od_total,od_turnlyqty,sum(pd_outqty) pd_outqty,oa_code,od_detno "
					+ " from ProdIODetail  left join Oaapplicationdetail on pd_orderid=od_id left join Oaapplication on od_oaid=oa_id"
					+ " where pd_orderid in (select pd_orderid from ProdIODetail where pd_piid=" + store.get("pi_id")
					+ " and pd_piclass='用品领用单') group by od_total,od_turnlyqty,od_id,oa_code,od_detno having sum(pd_outqty)>od_total";
			SqlRowList rs = baseDao.queryForRowSet(sql);
			if (rs.next()) {// 明细数量不能超过原单据的批准数量
				BaseUtil.showError("用品申请单" + rs.getString("oa_code") + ",序号" + rs.getInt("od_detno") + "的批准数量为:" + rs.getDouble("od_total")
						+ ".已转数为:" + rs.getDouble("pd_outqty") + ".超过了批准数,请核对后重试!");
			}
			String query = "select sum(pd_outqty) pd_outqty,pd_orderid from ProdIODetail "
					+ "where pd_orderid in (select pd_orderid from ProdIODetail where pd_piid=" + store.get("pi_id")
					+ ") and pd_piclass='用品领用单' group by pd_orderid";
			SqlRowList rs1 = baseDao.queryForRowSet(query);
			List<String> sqls = new ArrayList<String>();
			while (rs1.next()) {
				String updateSql = "update Oaapplicationdetail set od_turnlyqty=" + rs1.getDouble("pd_outqty") + " where od_id="
						+ rs1.getInt("pd_orderid");
				sqls.add(updateSql);
			}
			baseDao.execute(sqls);
		}
		baseDao.execute(
				"update prodinout set (pi_whcode,pi_whname)=(select pd_whcode,pd_whname from prodiodetail where pd_piid=pi_id and nvl(pd_whcode,' ')<>' ' and rownum<2) where pi_id=? and nvl(pi_whcode,' ')= ' '",
				pi_id);
		getTotal(store.get("pi_id"), caller);
		updatepdPrice(Integer.parseInt(store.get("pi_id").toString()), caller);
		if ("ProdInOut!ReserveInitialize".equals(caller)) {
			baseDao.execute(
					"update ProdIODetail set (pd_whcode,pd_whname)=(select pi_whcode,pi_whname from prodinout where pd_piid=pi_id and nvl(pi_whcode,' ')<>' ') where pd_piid=?",
					pi_id);
		}
		if ("ProdInOut!GoodsIn".equals(caller)) {
			baseDao.execute(
					"update ProdIODetail set (pd_whcode,pd_whname)=(select pi_whcode,pi_whname from prodinout where pd_piid=pi_id and nvl(pi_whcode,' ')<>' ') where nvl(pd_whcode,' ')= ' ' and pd_piid=?",
					pi_id);
		}
		// pd_salecode更新
		if ("ProdInOut!Sale".equals(caller) || "ProdInOut!SaleReturn".equals(caller) || "ProdInOut!SaleAppropriationOut".equals(caller)) {
			// 出货单、退货单、销售拨出单pd_salecode=pd_ordercode
			baseDao.execute("update prodiodetail set pd_salecode=pd_ordercode where pd_piid=?", store.get("pi_id"));
		} else if ("ProdInOut!Make!In".equals(caller) || "ProdInOut!OutsideCheckIn".equals(caller) || "ProdInOut!DefectIn".equals(caller)) {
			// 完工入库单、委外验收单、不良品入库单
			baseDao.execute("update prodiodetail set pd_salecode=(select max(ma_salecode) from make where ma_code=pd_ordercode) "
					+ "where pd_piid=?", store.get("pi_id"));
		} else if ("ProdInOut!PurcCheckin".equals(caller)) {// @add20171103采购验收单根据采购单号、序号，更新入库销售单号、序号
			baseDao.execute(
					"update prodiodetail set (pd_salecode,pd_saledetno,pd_topmothercode)=(select d.pd_salecode,d.pd_saledetno,d.pd_topmothercode from purchase left join purchasedetail d on pu_id=d.pd_puid where "
							+ " pd_ordercode=pu_code and pd_orderdetno=d.pd_detno) where pd_piid=?", store.get("pi_id"));
		}
		// @add20171103生产退料单、委外退料单、拆件入库单、完工入库单、委外验收单,根据工单的订单号、订单序号能匹配到销售单号和序号（如果是预测单则不锁批记录）则更新到入库单明细的销售单号、序号
		if ("ProdInOut!OutsideReturn".equals(caller) || "ProdInOut!OutsideCheckIn".equals(caller) || "ProdInOut!Make!In".equals(caller)
				|| "ProdInOut!PartitionStockIn".equals(caller) || "ProdInOut!Make!Return".equals(caller)) {
			baseDao.execute(
					"update prodiodetail set (pd_salecode,pd_saledetno,pd_topmothercode)=(select ma_salecode,ma_saledetno,ma_topmothercode from make where pd_ordercode=ma_code)"
							+ " where pd_piid=? and exists (select 1 from make left join sale on sa_code=ma_salecode left join saledetail on sd_said=sa_id and sd_detno=ma_saledetno where pd_ordercode=ma_code and sd_detno>0)",
					pi_id);

		}
		if ("ProdInOut!ProcessIn".equals(caller) || "ProdInOut!ProcessOut".equals(caller)) {// 加工验收单、加工验退单
			SqlRowList newpd = baseDao
					.queryForRowSet(
							"select pd_orderdetno,pd_ordercode,pd_id,nvl(pd_inqty,0)+nvl(pd_outqty,0) pd_qty from ProdInOut left join ProdIODetail on pd_piid=pi_id where pi_id=? and nvl(pd_ordercode,' ')<>' ' and nvl(pd_orderdetno,0)<>0",
							pi_id);
			otherExplistDao.restoreSourceYqty(oldpd, newpd);
		}
		// 记录操作
		baseDao.logger.update(caller, "pi_id", store.get("pi_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
		if ("ProdInOut!OtherIn".equals(caller) || "ProdInOut!OtherOut".equals(caller)) {// 欧盛其他出入库单，主表项目编号更新到明细设备编号中
			if (baseDao.isDBSetting(caller, "writePjToTopmother")) {
				baseDao.execute("update prodiodetail set pd_topmothercode=? where pd_piid=?", store.get("pi_prjcode"), pi_id);
			}
		}
		// 20171219，其它出库单、拨出单保存更新出库数不允许超可用库存
		if (baseDao.isDBSetting(caller, "checkCanUseOnhand")) {// 采用存储过程
			String res = baseDao.callProcedure("SP_CHECKCANUSEONHAND", new Object[] { store.get("pi_class"), pi_id });
			if (res != null && !res.trim().equals("")) {
				BaseUtil.showError(res);
			}
		}
		// @add20170525 保存,更新之后批号不为空或者批号id 大于0的，批号ID和批号不对应的 更新批号id，
		baseDao.execute(
				"UPDATE PRODIODETAIL SET PD_BATCHID=(SELECT nvl(max(ba_id),0) FROM BATCH WHERE BA_CODE=PD_BATCHCODE AND BA_WHCODE=PD_WHCODE and ba_prodcode=pd_prodcode)"
						+ " where pd_piid=? and (nvl(pd_batchcode,' ')<>' ' or nvl(pd_batchid,0)>0) and pd_outqty>0"
						+ " and (pd_batchcode,nvl(pd_batchid,0)) not in (select ba_code,ba_id from batch)", store.get("pi_id"));
		if ("ProdInOut!Sale".equals(caller)) {
			tipSellerBatch(caller, store.get("pi_id"));
		}
		// maz 其它出库单、借货出货单的客户物料编号 更新后抓取客户编号、名称和规格 2017080360
		if ("ProdInOut!OtherOut".equals(caller) || "ProdInOut!SaleBorrow".equals(caller)) {
			baseDao.execute("update ProdIODetail a set (pd_custprodcode,pd_custproddetail,pd_custprodspec)=(select pc_custprodcode,pc_custproddetail,pc_custprodspec from productcustomer where a.pd_prodcode=pc_prodcode and pc_custcode='"
					+ store.get("pi_cardcode") + "') where pd_piid=" + store.get("pi_id") + "");
		}
		// 出库类型单据更新出库单备料状态
		updatePdaStatus(caller, store.get("pi_id"));
		// 重新刷新对应单据的领料状态和明细的已转数
		if (maids.size() > 0 && mmids.size() > 0 && ("ProdInOut!Picking".equals(caller) || "ProdInOut!OutsidePicking".equals(caller))) {
			refreshmaketurn(BaseUtil.parseList2Str(mmids, ",", true), BaseUtil.parseList2Str(maids, ",", true));
		}
		if (baseDao.isDBSetting(caller, "getPriceByNoOrder")) {
			getSalePrice(store.get("pi_id"));
		}
	}

	@Override
	public String[] printProdInOut(String caller, int pi_id, String reportName, String condition) {
		Object[] status = baseDao.getFieldsDataByCondition("ProdINout", new String[] { "pi_invostatuscode", "pi_statuscode" }, "pi_id="
				+ pi_id);
		// 判断已审核才允许打印
		if (baseDao.isDBSetting(caller, "printNeedAudit")) {
			StateAssert.printOnlyAudited(status[0]);
		}
		// 判断已过账不允许打印
		if (baseDao.isDBSetting(caller, "printNoPost") && "POSTED".equals(status[1])) {
			BaseUtil.showError("已过账的单据不允许打印！");
		}
		// 2018030244 maz 送货单号不一致不允许提交
		if ("ProdInOut!PurcCheckin".equals(caller) || "ProdInOut!DefectIn".equals(caller)) {
			if (baseDao.isDBSetting("VerifyApplyDetail!Deal", "turnByDeliver")) {
				checkSendCode(pi_id);
			}
		}
		// 库存不足不允许打印
		if (baseDao.isDBSetting(caller, "printNeedEnoughStock")) {
			if (!status[1].equals("POSTED")) {
				String sql = "select pd_prodcode,pw_onhand,pw_whcode from (select pd_prodcode,(case when NVL(pd_whcode,' ')=' ' then pi_whcode else pd_whcode end) as whcode,sum(pd_outqty) as outqty from  prodinout,ProdIODetail  where pi_id=pd_piid  and pd_piid='"
						+ pi_id
						+ "' group by pd_prodcode,  (case when NVL(pd_whcode,' ')=' ' then pi_whcode else pd_whcode end)) A left join productwh on pw_prodcode=pd_prodcode and pw_whcode=whcode where NVL(pw_onhand,0)<outqty ";
				SqlRowList rs = baseDao.queryForRowSet(sql);
				while (rs.next()) {
					SqlRowList rs1 = baseDao.queryForRowSet(
							"select wm_concat(pd_pdno) from prodiodetail where pd_piid=? and pd_prodcode=? and pd_whcode=?", pi_id,
							rs.getObject("pd_prodcode"), rs.getObject("pw_whcode"));
					if (rs1.next()) {
						BaseUtil.showError("库存不足不允许打印，行号：" + rs1.getString(1));
					}
				}
			}
		}
		/**
		 * 新增限制,拆件类型制造单不允许走完工入库单
		 * 
		 * @author XiaoST 2016年12月9日 下午4:26:09
		 */
		if ("ProdInOut!Make!In".equals(caller) || "ProdInOut!OutsideCheckIn".equals(caller)) {
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat('行号：'||pd_pdno||'工单：'||pd_ordercode) from ProdIODetail left join make on ma_code=pd_ordercode left join makekind ON mk_name=ma_kind where pd_piid=? and  mk_type='D' and rownum<20",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("拆件工单必须走拆件入库单！" + dets);
			}
		}
		printCheck(pi_id, caller);
		handlerService.beforePrint(caller, pi_id);
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 修改打印状态
		baseDao.print("ProdInOut", "pi_id=" + pi_id, "pi_printstatus", "pi_printstatuscode");
		baseDao.execute("update ProdInOut set pi_printman='" + SystemSession.getUser().getEm_name() + "' where pi_id=" + pi_id);
		// 记录操作
		baseDao.logger.print(caller, "pi_id", pi_id);
		// 记录打印次数
		baseDao.updateByCondition("ProdInout", "pi_count=nvl(pi_count,0)+1", "pi_id=" + pi_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, pi_id);
		return keys;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void auditProdInOut(int pi_id, String caller) {
		/**
		 * @ 20170120新增参数限制主表仓库与明细行仓库必须一致
		 */
		if (baseDao.isDBSetting("warehouseCheck") || baseDao.isDBSetting(caller, "warehouseCheck")) {
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from ProdIODetail left join ProdInOut on pd_piid=pi_id where pi_id=? and (nvl(pi_whcode,' ')<>nvl(pd_whcode,' ') or nvl(pi_purpose,' ')<>nvl(pd_inwhcode,' '))",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细行仓库与当前单主表仓库不一致，不允许进行当前操作！" + dets);
			}
		}
		/**
		 * @author wsy
		 *         其它入库单：如果选择了采购单号、采购序号后，提交、审核的时候要判断采购单号+采购序号+物料编号是否一致，不一致限制提交
		 *         、审核
		 */
		if ("ProdInOut!OtherIn".equals(caller)) {
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat('采购单号:'||pd_ordercode||'序号：'||pd_orderdetno||'物料编号：'||pd_prodcode) from prodiodetail left join prodinout on pd_piid=pi_id where (pd_ordercode,pd_orderdetno,pd_prodcode) not in (select pd_code,pd_detno,pd_prodcode from purchasedetail) and pd_piid=? and pi_class='其它入库单' and pd_ordercode is not null and pd_orderdetno is not null",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError(dets + "与采购单不一致,不允许提交");
			}
		}
		// 2018030244 maz 送货单号不一致不允许提交
		if ("ProdInOut!PurcCheckin".equals(caller) || "ProdInOut!DefectIn".equals(caller)) {
			if (baseDao.isDBSetting("VerifyApplyDetail!Deal", "turnByDeliver")) {
				checkSendCode(pi_id);
			}
		}
		// 只能对状态为[已提交]的单据进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("ProdInOut", new String[] { "pi_invostatuscode", "pi_date" }, "pi_id=" + pi_id);
		StateAssert.auditOnlyCommited(status[0]);
		baseDao.execute("update prodinout set (pi_whcode,pi_whname)=(select pd_whcode,pd_whname from prodiodetail where pd_piid=pi_id and nvl(pd_whcode,' ')<>' ' and rownum<2) where pi_id="
				+ pi_id + " and nvl(pi_whcode,' ')= ' '");
		checkCloseMonth(status[1]);
		copcheck(pi_id, caller);
		factorycheck(pi_id, caller);
		checkCommit(caller, pi_id);
		checkBatch(caller, pi_id);
		// 制造工单加工类型
		if ("ProdInOut!Make!In".equals(caller) || "ProdInOut!OutsideCheckIn".equals(caller)) {
			checkMakeKindType(caller, pi_id);
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, pi_id);
		// 执行审核操作
		baseDao.audit("ProdInOut", "pi_id=" + pi_id, "pi_invostatus", "pi_invostatuscode", "pi_auditdate", "pi_auditman");
		baseDao.audit("ProdIodetail", "pd_piid=" + pi_id, "pd_auditstatus", "PD_AUDITSTATUSCODE");
		// 不良品入库单审核后 根据pi_emergency字段自动转MRB单还是转不良品出库单 maz 2017120256
		if ("ProdInOut!DefectIn".equals(caller)) {
			List<Object[]> objs = baseDao.getFieldsDatasByCondition("ProdInOut left join ProdIODetail on pi_id=pd_piid", new String[] {
					"pi_emergency", "pd_id", "nvl(pd_inqty,0)", "nvl(pd_yqty,0)" }, "pi_id=" + pi_id);
			List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
			Map<String, Object> map = new HashMap<String, Object>();
			for (Object[] pi_emergency : objs) {
				Double tqty = Double.parseDouble(pi_emergency[2].toString()) - Double.parseDouble(pi_emergency[3].toString());
				map.put("pd_id", pi_emergency[1]);
				map.put("pd_tqty", tqty);
				data.add(map);
			}
			if (StringUtil.hasText(objs.get(0)[0]) && "TURNOUT".equals(objs.get(0)[0])) {
				batchDealService.turnDefectOut("ProdIN!ToProdDefectOut!Deal", BaseUtil.parseGridStore2Str(data), "ProdInOut!DefectOut");
			} else if (StringUtil.hasText(objs.get(0)[0]) && "TURNMRB".equals(objs.get(0)[0])) {
				turnMRB("ProdIO!ToMRB!Deal", BaseUtil.parseGridStore2Str(data));
			}
		}
		// 记录操作
		baseDao.logger.audit(caller, "pi_id", pi_id);
		// 执行打印后的其它逻辑
		handlerService.afterAudit(caller, pi_id);
	}

	@Override
	public void resAuditProdInOut(String caller, int pi_id) {
		// 只能对状态为[已审核]的单据进行反审核操作!
		Object[] objs = baseDao.getFieldsDataByCondition("ProdInOut", new String[] { "pi_invostatuscode", "nvl(pi_billstatuscode,' ')",
				"nvl(pi_statuscode,' ')", "pi_date" }, "pi_id=" + pi_id);
		if (!"AUDITED".equals(objs[0]) || "TURNAR".equals(objs[1]) || "PARTAR".equals(objs[1]) || "POSTED".equals(objs[2])) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.reserve.prodInOut.resAudit_onlyAudit"));
		}
		handlerService.beforeResAudit(caller, pi_id);
		String log = "";
		if ("ProdInOut!DefectIn".equals(caller)) {
			SqlRowList rs = baseDao.queryForRowSet("select pd_pdno from prodiodetail where pd_yqty>0 and pd_piid=" + pi_id);
			if (rs.next()) {
				log = log + rs.getObject("pd_pdno") + "、";
			}
		}
		if (log != "") {
			log = log.substring(0, log.length() - 1);
			BaseUtil.showError("明细行存在已转数量大于0的数据，不能反审核，行号" + log + "");
		}
		// 执行反审核操作
		baseDao.resAudit("ProdInOut", "pi_id=" + pi_id, "pi_invostatus", "pi_invostatuscode", "pi_auditdate", "pi_auditman");
		baseDao.resOperate("ProdIodetail", "pd_piid=" + pi_id, "pd_auditstatus", "PD_AUDITSTATUSCODE");
		// 记录操作
		baseDao.logger.resAudit(caller, "pi_id", pi_id);
		// 执行审核后的其它逻辑
		handlerService.afterResAudit(caller, pi_id);
	}

	@Override
	public void submitProdInOut(String caller, int pi_id) {
		try {
			baseDao.execute("select pi_id from prodinout where pi_id=? for update wait 3", pi_id);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			BaseUtil.showError("当前单据有其他人正在操作，请等待3秒后再试");
		}
		/**
		 * @ 20170120新增参数限制主表仓库与明细行仓库必须一致
		 */
		if (baseDao.isDBSetting("warehouseCheck") || baseDao.isDBSetting(caller, "warehouseCheck")) {
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from ProdIODetail left join ProdInOut on pd_piid=pi_id where pi_id=? and (nvl(pi_whcode,' ')<>nvl(pd_whcode,' ') or nvl(pi_purpose,' ')<>nvl(pd_inwhcode,' '))",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细行仓库与当前单主表仓库不一致，不允许进行当前操作！" + dets);
			}
		}
		// 只能对状态为[在录入]的单据进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("ProdInOut", new String[] { "pi_invostatuscode", "pi_date" }, "pi_id=" + pi_id);
		StateAssert.submitOnlyEntering(status[0]);
		baseDao.execute("update prodinout set (pi_whcode,pi_whname)=(select pd_whcode,pd_whname from prodiodetail where pd_piid=pi_id and nvl(pd_whcode,' ')<>' ' and rownum<2) where pi_id="
				+ pi_id + " and nvl(pi_whcode,' ')= ' '");
		checkFreezeMonth(caller, status[1]);
		checkCloseMonth(status[1]);
		checkFirstMonth(caller, status[1]);
		// 制造工单加工类型
		if ("ProdInOut!Make!In".equals(caller) || "ProdInOut!OutsideCheckIn".equals(caller)) {
			checkMakeKindType(caller, pi_id);
		}
		// 库存不足不允许提交
		if (baseDao.isDBSetting(caller, "commitNeedEnoughStock")) {
			String sql = "select pd_prodcode,pw_onhand,pw_whcode from (select pd_prodcode,(case when NVL(pd_whcode,' ')=' ' then pi_whcode else pd_whcode end) as whcode,sum(pd_outqty) as outqty from  prodinout,ProdIODetail  where pi_id=pd_piid  and pd_piid='"
					+ pi_id
					+ "' group by pd_prodcode,  (case when NVL(pd_whcode,' ')=' ' then pi_whcode else pd_whcode end)) A left join productwh on pw_prodcode=pd_prodcode and pw_whcode=whcode where NVL(pw_onhand,0)<outqty ";
			SqlRowList rs = baseDao.queryForRowSet(sql);
			while (rs.next()) {
				SqlRowList rs1 = baseDao.queryForRowSet(
						"select wm_concat(pd_pdno) pd_pdno from prodiodetail where pd_piid=? and pd_prodcode=? and pd_whcode=?", pi_id,
						rs.getObject("pd_prodcode"), rs.getObject("pw_whcode"));
				if (rs1.next()) {
					BaseUtil.showError("库存不足不允许提交，行号：" + rs1.getString("pd_pdno"));
				}
			}
		}
		/**
		 * @author wsy
		 *         其它入库单：如果选择了采购单号、采购序号后，提交、审核的时候要判断采购单号+采购序号+物料编号是否一致，不一致限制提交
		 *         、审核
		 */
		if ("ProdInOut!OtherIn".equals(caller)) {
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat('采购单号:'||pd_ordercode||'序号：'||pd_orderdetno||'物料编号：'||pd_prodcode) from prodiodetail left join prodinout on pd_piid=pi_id where (pd_ordercode,pd_orderdetno,pd_prodcode) not in (select pd_code,pd_detno,pd_prodcode from purchasedetail) and pd_piid=? and pi_class='其它入库单' and pd_ordercode is not null and pd_orderdetno is not null",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError(dets + "与采购单不一致,不允许提交");
			}
		}
		/*
		 * maz 库存不足不允许提交 包括所有已审核未过账的出库数量单据 2017080190
		 */
		if (prodInOutDao.isOut(caller) && baseDao.isDBSetting(caller, "commitCheckEnoughStock")) {
			// 批号为空的判断限制
			StringBuffer sb = new StringBuffer();
			String invostatus = "";
			String sign = baseDao.getDBSetting(caller, "commitCheckStockByStatus") == null ? "所有" : baseDao.getDBSetting(caller,
					"commitCheckStockByStatus");
			if ("所有".equals(sign)) {
				invostatus = "('AUDITED','COMMITED','ENTERING')";
			} else if ("已提交或已审核".equals(sign)) {
				invostatus = "('AUDITED','COMMITED')";
			} else if ("已审核".equals(sign)) {
				invostatus = "('AUDITED')";
			}
			SqlRowList rs = baseDao
					.queryForRowSet("select * from (select sum(outqty)outqty,pd_prodcode,whcode from ((select  sum(pd_outqty) as outqty,pd_prodcode,(case when NVL(pd_whcode,' ')=' ' then pi_whcode else pd_whcode end) as whcode from prodiodetail left join prodinout on pd_piid=pi_id where nvl(pd_outqty,0)>0 and nvl(pd_status,0)<99 "
							+ " and (pd_prodcode,(case when NVL(pd_whcode,' ')=' ' then pi_whcode else pd_whcode end)) in (select pd_prodcode,(case when NVL(pd_whcode,' ')=' ' then pi_whcode else pd_whcode end) from prodiodetail left join prodinout on pd_piid=pi_id where pd_piid="
							+ pi_id
							+ " ) and pi_invostatuscode in "
							+ invostatus
							+ " and pi_id<>"
							+ pi_id
							+ " "
							+ "group by pd_prodcode,(case when NVL(pd_whcode,' ')=' ' then pi_whcode else pd_whcode end)) union all (select  sum(pd_outqty) as outqty,pd_prodcode,(case when NVL(pd_whcode,' ')=' ' then pi_whcode else pd_whcode end) as whcode from prodiodetail left join prodinout on pd_piid=pi_id where  pi_id="
							+ pi_id
							+ " "
							+ "group by pd_prodcode,(case when NVL(pd_whcode,' ')=' ' then pi_whcode else pd_whcode end))) group by pd_prodcode,whcode) A left join productwh on pw_prodcode=pd_prodcode and pw_whcode=whcode where NVL(pw_onhand,0)<outqty");
			while (rs.next()) {
				SqlRowList rs1 = baseDao
						.queryForRowSet(
								"select wm_concat(pd_pdno)pd_pdno from prodiodetail where pd_piid=? and pd_prodcode=? and pd_whcode=? and pd_batchcode is null",
								pi_id, rs.getObject("pd_prodcode"), rs.getObject("whcode"));
				if (rs1.next() && rs1.getObject("pd_pdno") != null) {
					sb.append("行号:" + rs1.getObject("pd_pdno") + " 本次出库数大于仓库预计可出库数，不允许提交！");
				}
			}
			if (sb.length() > 0) {
				BaseUtil.showError(sb.toString());
			}
			// 选择了批号提交限制
			SqlRowList res = baseDao
					.queryForRowSet("select * from (select  sum(pd_outqty) as outqty,pd_prodcode,(case when NVL(pd_whcode,' ')=' ' then pi_whcode else pd_whcode end) as whcode,pd_batchcode from prodiodetail left join prodinout on pd_piid=pi_id where nvl(pd_outqty,0)>0 and  nvl(pd_status,0)<99 "
							+ " and (pd_prodcode,(case when NVL(pd_whcode,' ')=' ' then pi_whcode else pd_whcode end),pd_batchcode) in (select pd_prodcode,(case when NVL(pd_whcode,' ')=' ' then pi_whcode else pd_whcode end),pd_batchcode from prodiodetail left join prodinout on pd_piid=pi_id where pd_piid="
							+ pi_id
							+ " and nvl(pd_batchcode,' ')<>' ') and pi_invostatuscode in "
							+ invostatus
							+ " "
							+ "group by pd_prodcode,(case when NVL(pd_whcode,' ')=' ' then pi_whcode else pd_whcode end),pd_batchcode) A left join batch on ba_prodcode=pd_prodcode and ba_whcode=whcode and ba_code=pd_batchcode where NVL(ba_remain,0)<outqty");
			while (res.next()) {
				SqlRowList rs1 = baseDao
						.queryForRowSet(
								"select wm_concat(pd_pdno)pd_pdno from prodiodetail where pd_piid=? and pd_prodcode=? and pd_whcode=? and pd_batchcode=?",
								pi_id, res.getObject("pd_prodcode"), res.getObject("whcode"), res.getObject("pd_batchcode"));
				if (rs1.next()) {
					sb.append("行号:" + rs1.getObject("pd_pdno") + " 本次出库数大于批号预计可出库数，不允许提交！");
				}
			}
			if (sb.length() > 0) {
				BaseUtil.showError(sb.toString());
			}
		}
		// 生产退料单处理
		if (caller.equals("ProdInOut!Make!Return") || caller.equals("ProdInOut!OutsideReturn")) {
			boolean updateBOMCost = baseDao.isDBSetting("ProdInOut!OutsideReturn", "updateBOMCost");
			boolean mrpSeparateSeller = baseDao.isDBSetting("MpsDesk", "mrpSeparateSeller");
			if (caller.equals("ProdInOut!OutsideReturn")) {
				SqlRowList pi_list = baseDao
						.queryForRowSet("select pd_id,ma_code,ma_sourcekind ,pd_ordercode,pd_orderdetno from prodinout left join prodiodetail on pd_piid = pi_id "
								+ "left join make on ma_code= pd_ordercode where pi_class='委外退料单' and pi_id =" + pi_id);
				while (pi_list.next()) {
					if (updateBOMCost) {
						String update_sql = "update prodiodetail set pd_price = (select A.total from (select pd_ordercode,pd_orderdetno ,round(sum(nvl(pd_outqty,0)*nvl(pd_price,0))/sum(nvl(pd_outqty,0)),8) total from prodinout left join prodiodetail on pi_id =pd_piid where pi_status ='已过账' and "
								+ "(pi_class='委外领料单' or pi_class='委外补料单')  group by pd_ordercode,pd_orderdetno) A where A.pd_ordercode='"
								+ pi_list.getString("pd_ordercode")
								+ "' and A.pd_orderdetno="
								+ pi_list.getInt("pd_orderdetno")
								+ " ) "
								+ "where pd_piid=" + pi_id + " and pd_orderdetno =" + pi_list.getInt("pd_orderdetno");
						baseDao.execute(update_sql);
					}
					if (mrpSeparateSeller) {
						if ("Sale".equals(pi_list.getString("ma_sourcekind"))) {
							// 关联销售订单主表的sa_sellercode、sa_seller
							// 赋值到退料单上的pd_sellercode、pd_seller
							Object[] sellers = baseDao.getFieldsDataByCondition("make left join sale on ma_salecode =sa_code",
									new String[] { "sa_sellercode", "sa_seller" }, "ma_code ='" + pi_list.getString("pd_ordercode") + "'");
							baseDao.execute("update prodiodetail set pd_sellercode='" + sellers[0] + "' ,pd_seller='" + sellers[1]
									+ "' where pd_id=" + pi_list.getInt("pd_id"));
						} else if ("SaleForeCast".equals(pi_list.getString("ma_sourcekind"))) {
							// 关联销售预测明细行的sd_sellercode、sd_seller
							// 赋值到退料单上的pd_sellercode、pd_seller
							Object[] sellers = baseDao.getFieldsDataByCondition(
									"make left join SaleForeCast on ma_salecode = sf_code left join SaleForeCastdetail on sd_sfid = sf_id",
									new String[] { "sd_sellercode", "sd_seller" }, "ma_code='" + pi_list.getString("ma_code") + "'");
							baseDao.execute("update prodiodetail set pd_sellercode='" + sellers[0] + "',pd_seller='" + sellers[1]
									+ "' where pd_id=" + pi_list.getInt("pd_id"));
						} else {
							baseDao.execute("update prodiodetail set pd_sellercode='',pd_seller='' where pd_id=" + pi_list.getInt("pd_id"));
						}
					}
				}
			}
			dealOutOfMakeMaterial("工单外退料", pi_id);
			checkRepQty(pi_id);
		} else if (caller.equals("ProdInOut!PartitionStockIn")) {
			dealOutOfMakeMaterial("拆件入库", pi_id);
		}
		if ("ProdInOut!Sale".equals(caller)) {
			astrictSellerBatch(caller, pi_id);
		}
		baseDao.execute("update ProdIODetail set pd_sdid=(select sd_id from saledetail where pd_ordercode=sd_code and pd_orderdetno=sd_detno) where nvl(pd_ordercode,' ')<>' ' and pd_piclass in ('出货单','销售退货单') and pd_piid="
				+ pi_id);
		checkCommit(caller, pi_id);
		checkBatch(caller, pi_id);
		copcheck(pi_id, caller);
		factorycheck(pi_id, caller);
		boolean bool = false;
		if ("ProdInOut!StockScrap".equals(caller)) {
			Object enddate = baseDao.getFieldDataByCondition("PeriodsDetail", "to_char(pd_enddate,'yyyymmdd')",
					"pd_code='MONTH-P' and pd_detno=to_char(to_date('" + status[1] + "','yyyy-mm-dd hh24:mi:ss'), 'yyyymm')");
			if (enddate != null && !enddate.equals("")) {
				String sql = "merge into prodiodetail p using(select * from (select pd_prodcode,pd_price,ROW_NUMBER() OVER (partition by pd_prodcode order by pi_id desc) AS rn "
						+ "from prodiodetail left join prodinout on  pi_id=pd_piid where pd_prodcode in (select pd_prodcode from prodiodetail where pd_piid="
						+ pi_id
						+ ") "
						+ "and pd_status=99 and nvl(pd_price,0) > 0 and nvl(pd_inqty,0) > 0  and to_char(pi_date,'yyyymmdd')<='"
						+ enddate.toString()
						+ "' ) where rn<2 ) "
						+ "src on (p.pd_prodcode=src.pd_prodcode)  when matched then update set p.pd_orderprice=src.pd_price where p.pd_piid="
						+ pi_id;
				baseDao.execute(sql);
				baseDao.execute("update prodiodetail set pd_ordertotal=round(pd_orderprice*pd_outqty) where pd_piid=" + pi_id);
			}
		}
		baseDao.execute("update prodiodetail set pd_total=round(pd_price*pd_outqty,2) where pd_piid=" + pi_id
				+ " and pd_piclass='采购验退单' and nvl(pd_total,0)=0 ");
		useDefaultTax(caller, pi_id);
		allowZeroTax(caller, pi_id);
		if ("ProdInOut!Sale".equals(caller)) {
			// 判断明细物料数量，为0不让提交
			String emptyQty = baseDao.getJdbcTemplate().queryForObject(
					"Select wmsys.wm_concat(pd_pdno) from prodiodetail where pd_piid=? and nvl(pd_outqty,0)=0", String.class, pi_id);
			if (emptyQty != null) {
				BaseUtil.showError("出货数量为0，不允许提交，明细序号:" + emptyQty);
			}
		}
		if ("ProdInOut!GoodsOut".equals(caller) || "ProdInOut!GoodsIn".equals(caller)) {
			// 用品验收单,用品验退单提交时,验证明细和主记录的供应商是否一致
			Object[] vender = baseDao.getFieldsDataByCondition("ProdInOut", new String[] { "pi_cardcode", "pi_title" }, "pi_id=" + pi_id);
			List<Object[]> data = baseDao.getFieldsDatasByCondition("Oapurchase  left join prodiodetail on pd_ordercode=op_code",
					new String[] { "pd_ordercode", "nvl(op_vecode,'')", "pd_pdno" }, "pd_piid=" + pi_id);
			for (Object[] os : data) {
				if (os[1] == null || !os[1].toString().equals(vender[0].toString())) {
					BaseUtil.showError("第" + os[2] + "行,采购单<" + os[0] + ">的供应商与主记录供应商不一致!");
				}
			}
		}
		// 2018030244 maz 送货单号不一致不允许提交
		if ("ProdInOut!PurcCheckin".equals(caller) || "ProdInOut!DefectIn".equals(caller)) {
			if (baseDao.isDBSetting("VerifyApplyDetail!Deal", "turnByDeliver")) {
				checkSendCode(pi_id);
			}
		}
		if ("ProdInOut!OutReturn".equals(caller)) {
			// 判断明细借货归还单的还货数不能大于相应借货出货单行号的出货数
			SqlRowList rs = baseDao
					.queryForRowSet(
							"SELECT pd_ordercode,pd_orderdetno,sum(pd_inqty) pd_inqty FROM ProdioDetail where pd_piid=? group by pd_ordercode,pd_orderdetno",
							pi_id);
			String code = null;
			int detno = 0;
			Object count = null;
			StringBuffer sb = new StringBuffer();
			while (rs.next()) {
				code = rs.getString("pd_ordercode");
				detno = rs.getInt("pd_orderdetno");
				count = baseDao.getFieldDataByCondition("ProdioDetail", "sum(pd_inqty)", "pd_piclass='借货归还单' and pd_ordercode='" + code
						+ "' and pd_orderdetno=" + detno + " and pd_piid<>" + pi_id);
				if (count != null) {
					bool = baseDao.checkByCondition("ProdioDetail", "pd_inoutno='" + code + "' and pd_pdno=" + detno
							+ " and pd_piclass='借货出货单' and pd_outqty<" + (Double.parseDouble(count.toString()) + rs.getDouble("pd_inqty")));
					if (!bool) {
						sb.append("<br>");
						sb.append("不能超借货出货单数量归还,借货出货单号[");
						sb.append(code);
						sb.append("],明细行号[");
						sb.append(detno);
						sb.append("],本次归还数[");
						sb.append(rs.getDouble("pd_inqty"));
						sb.append("],已转归还数[");
						sb.append(count);
						sb.append("]");
					}
				}
			}
			if (sb.length() > 0) {
				BaseUtil.showError(sb.toString());
			}
		}
		int autoNewbatchcode = 0;// 判断是否需要自动生成批号
		SqlRowList rs0 = baseDao.queryForRowSet("select ds_inorout from DOCUMENTSETUP where ds_table=?", caller);
		if (rs0.next()) {
			String Code = rs0.getObject("ds_inorout").toString();
			if (Code.equals("IN") || Code.equals("-OUT")) {
				autoNewbatchcode = -1;
			}
		}
		SqlRowList rs = baseDao.queryForRowSet("SELECT pd_id FROM ProdioDetail where pd_piid=?", pi_id);
		if (autoNewbatchcode == -1) {
			while (rs.next()) {
				baseDao.execute("update prodiodetail set pd_batchcode=? where pd_id=? and nvl(pd_batchcode,' ')=' '",
						baseDao.getBatchcode(caller), rs.getInt("pd_id"));
			}
		}
		rs = baseDao.queryForRowSet("select ds_inorout from DOCUMENTSETUP where ds_table=?", caller);
		if (rs.next()) {
			String Code = rs.getObject("ds_inorout").toString();
			if (Code.equals("IN") || Code.equals("-OUT")) {
				String batch = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wmsys.wm_concat('行:'||pd_pdno||',批号:'||pd_batchcode) from prodiodetail left join batch on pd_batchcode=ba_code and ba_prodcode = pd_prodcode and ba_whcode = pd_whcode "
										+ "where pd_piid=? and nvl(ba_inqty,0)>0.001", String.class, pi_id);
				if (batch != null) {
					BaseUtil.showError("同一物料不能同时入两次相同的批号!" + batch);
				}
			}
		}
		getTotal(pi_id, caller);
		updatepdPrice(pi_id, caller);
		baseDao.getEndDate(caller, pi_id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pi_id);
		// 执行提交操作
		baseDao.submit("ProdInOut", "pi_id=" + pi_id, "pi_invostatus", "pi_invostatuscode");
		baseDao.submit("ProdIodetail", "pd_piid=" + pi_id, "pd_auditstatus", "PD_AUDITSTATUSCODE");
		/**
		 * 问题反馈编号：2017010242 处理：添加参数配置【自动生成客户物料对照表】，提交后根据判断插入到客户物料对照表中。
		 * 
		 * @author wsy
		 */
		// 自动生成客户物料对照表的参数配置
		if (baseDao.isDBSetting(caller, "autoProductCustomer")) {
			// 先取出出货单的这个客户的最大序号
			Object obj = baseDao.getFieldDataByCondition("ProductCustomer", "nvl(max(PC_DETNO),0)",
					"pc_custcode=(select pi_cardcode from PRODINOUT where pi_id=" + pi_id + ")");
			int detno = Integer.parseInt(obj.toString());
			String sql2 = "select DISTINCT cu_id,pr_id,pd_custprodcode,pd_custprodspec,pr_unit,pi_cardcode,pi_title,pd_prodcode from PRODINOUT left join ProdIODetail  on pd_piid=pi_id left join customer on pi_cardcode=cu_code left join Product on pd_prodcode=pr_code where pi_id='"
					+ pi_id
					+ "' and nvl(pd_custprodcode,' ')<>' 'and not exists (select 1 from ProductCustomer where pc_custcode=pi_cardcode and pc_prodcode=pd_prodcode)";
			SqlRowList rs1 = baseDao.queryForRowSet(sql2);
			while (rs1.next()) {
				baseDao.execute("insert into ProductCustomer(PC_ID,PC_CUSTID,PC_DETNO,PC_PRODID,PC_CUSTPRODCODE,"
						+ "PC_CUSTPRODSPEC,PC_CUSTPRODUNIT,PC_CUSTCODE,PC_CUSTNAME,PC_PRODCODE) " + "values(ProductCustomer_seq.nextval,"
						+ rs1.getString("cu_id") + "," + (++detno) + ",'" + rs1.getString("pr_id") + "'" + ",'"
						+ rs1.getString("pd_custprodcode") + "','" + rs1.getString("pd_custprodspec") + "','" + rs1.getString("pr_unit")
						+ "','" + rs1.getString("pi_cardcode") + "','" + rs1.getString("pi_title") + "','" + rs1.getString("pd_prodcode")
						+ "')");
			}
		}
		if ("ProdInOut!GoodsPicking".equals(caller)) {// 用品领用单
			String query = "select sum(pd_outqty) pd_outqty,pd_orderid from ProdIODetail "
					+ "where pd_orderid in (select pd_orderid from ProdIODetail where pd_piid=" + pi_id + ") group by pd_orderid";
			SqlRowList rs1 = baseDao.queryForRowSet(query);
			List<String> sqls = new ArrayList<String>();
			while (rs1.next()) {
				String updateSql = "update Oaapplicationdetail set od_turnlyqty=" + rs1.getDouble("pd_outqty") + " where od_id="
						+ rs1.getInt("pd_orderid");
				sqls.add(updateSql);
			}
			baseDao.execute(sqls);
		}
		// 记录操作
		baseDao.logger.submit(caller, "pi_id", pi_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pi_id);
	}

	@Override
	public void resSubmitProdInOut(String caller, int pi_id) {
		// 只能对状态为[已提交]的单据进行反提交操作!
		Object[] objs = baseDao.getFieldsDataByCondition("ProdInOut", new String[] { "pi_invostatuscode", "nvl(pi_billstatuscode,' ')",
				"nvl(pi_statuscode,' ')" }, "pi_id=" + pi_id);
		if (!"COMMITED".equals(objs[0]) || "TURNAR".equals(objs[1]) || "PARTAR".equals(objs[1]) || "POSTED".equals(objs[2])) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.reserve.prodInOut.resSubmit_onlyCommited"));
		}
		handlerService.beforeResSubmit(caller, pi_id);
		// 执行反提交操作
		baseDao.resOperate("ProdInOut", "pi_id=" + pi_id, "pi_invostatus", "pi_invostatuscode");
		baseDao.resOperate("ProdIodetail", "pd_piid=" + pi_id, "pd_auditstatus", "PD_AUDITSTATUSCODE");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pi_id", pi_id);
		// 执行审核后的其它逻辑
		handlerService.afterResSubmit(caller, pi_id);
	}

	@Override
	public void postProdInOut(int pi_id, String caller) {
		try {
			baseDao.execute("select pi_id from prodinout where pi_id=? for update", pi_id);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			BaseUtil.showError("当前单据有其他人正在操作");
		}
		if (baseDao.isDBSetting("warehouseCheck") || baseDao.isDBSetting(caller, "warehouseCheck")) {
			// 出入库单主表仓库与明细行仓库必须一致 2018020062 maz 增加判断拨出仓
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from ProdIODetail left join ProdInOut on pd_piid=pi_id where pi_id=? and (nvl(pi_whcode,' ')<>nvl(pd_whcode,' ') or nvl(pi_purpose,' ')<>nvl(pd_inwhcode,' '))",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细行仓库与当前单主表仓库不一致，不允许进行当前操作！" + dets);
			}
		}
		// 增加系统参数 出入库日期小于等于当前日期才允许过账 maz 2018020006
		if (baseDao.isDBSetting("sys", "limitPostAfter")) {
			Object pi_date = baseDao.getFieldDataByCondition("ProdInOut", "pi_date", "pi_id=" + pi_id);
			String sysdate = DateUtil.getCurrentDate();
			int sign = 0;
			sign = DateUtil.compare(pi_date.toString(), sysdate);
			if (sign == 1) {
				BaseUtil.showError("单据日期大于当前日期，不能过账");
			}
		}
		// 只能对状态为[未过账]的单据进行过账操作!
		Object[] status = baseDao.getFieldsDataByCondition("ProdInOut", new String[] { "pi_statuscode", "pi_invostatuscode", "pi_date",
				"pi_class", "pi_recordman", "pi_inoutno" }, "pi_id=" + pi_id);
		if (status[0].equals("POSTED")) {
			BaseUtil.showError("只能对未过账的" + status[3] + "[" + status[5] + "]进行过账操作！");
		}
		// 2018030244 maz 送货单号不一致不允许提交
		if ("ProdInOut!PurcCheckin".equals(caller) || "ProdInOut!DefectIn".equals(caller)) {
			if (baseDao.isDBSetting("VerifyApplyDetail!Deal", "turnByDeliver")) {
				checkSendCode(pi_id);
			}
		}
		// 明细中如果有属于物料等级属于库存不可用的，就提示这些物料当前等级库存不可用
		SqlRowList rowList = baseDao
				.queryForRowSet(
						"select wm_concat(pd_prodcode)  prodcode from (select distinct pd_prodcode from prodiodetail "
								+ "left join product on pd_prodcode=pr_code left join productlevel on pr_level=pl_levcode where pd_piid=? and pl_id>0 "
								+ "and pl_isuseable=0 ) where rownum<=20", pi_id);
		if (rowList.next()) {
			if (rowList.getString("prodcode") != null) {
				BaseUtil.showError(BaseUtil.getLocalMessage("这些物料当前等级库存不可用,物料编号：" + rowList.getString("prodcode")));
			}
		}
		baseDao.execute("update prodinout set (pi_whcode,pi_whname)=(select pd_whcode,pd_whname from prodiodetail where pd_piid=pi_id and nvl(pd_whcode,' ')<>' ' and rownum<2) where pi_id="
				+ pi_id + " and nvl(pi_whcode,' ')= ' '");
		// 判断已审核才允许过账
		if (baseDao.isDBSetting(caller, "postNeedAudit")) {
			if (!status[1].equals("AUDITED")) {
				BaseUtil.showError(BaseUtil.getLocalMessage(status[3] + "[" + status[5] + "]未审核，不允许过账！"));
			}
		}
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(distinct pi_class||'['||pi_inoutno||']') from prodinout left join prodiodetail a on pi_id=pd_piid where exists (select 1 from prodiodetail B WHERE B.PD_IOID=A.PD_ID AND B.PD_PIID=?) AND Pi_STATUSCODE='UNPOST'",
						String.class, pi_id);
		if (dets != null) {
			BaseUtil.showError("来源的" + dets + "未过账，本单据不允许过账！");
		}
		// 无来源订单不能直接过账
		if (baseDao.isDBSetting(caller, "isWuPO")) {
			int count = baseDao
					.getCount("select count(*) from ProdIODetail where (pd_ordercode is null or pd_orderdetno is null) and  pd_piid="
							+ pi_id);
			if (!status[1].equals("AUDITED") && count != 0) {
				BaseUtil.showError("无来源订单审核后才能过账");
			}
		}
		if (baseDao.isDBSetting("cgyCheck")) {
			/**
			 * maz 出入库单判断过账人是否在明细行仓库的仓管员资料表中存在,人员资料中查找管理员一样限制如果为非仓库员不允许过账
			 * 虚拟账号不限制 2017080135
			 */
			Object type = baseDao.getFieldDataByCondition("Employee", "em_code", "em_code='" + SystemSession.getUser().getEm_code() + "'");
			if (type != null) {
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from prodinout,prodiodetail where pi_id=pd_piid and pi_id=? and pd_id not in "
										+ "(select pd_id from prodinout,prodiodetail,warehouse,warehouseman where pi_id=pd_piid and pd_whcode=wh_code and wh_id=wm_whid "
										+ "and pi_id=? and wm_cgycode=?) and rownum<20", String.class, pi_id, pi_id,
								SystemSession.getUser().getEm_code());
				if (dets != null) {
					BaseUtil.showError("明细行仓库对应的仓管员与当前过账人不一致，不允许进行当前操作！" + dets);
				}
			}
		}
		// 出入库单上传状态，处于上传过程中的单据，不允许操作
		String sendStatus = baseDao.getFieldValue("ProdInOut", "pi_sendstatus", "pi_id=" + pi_id, String.class);
		StateAssert.onSendingLimit(sendStatus);
		boolean isProdIn = baseDao.isProdIn(caller);
		if (isProdIn) {
			SqlRowList rs = null;
			// 入库类单据:如果pd_location为空，默认等于物料资料里的仓位pr_location
			baseDao.execute(
					"update prodiodetail set pd_location=(select pr_location from product where pr_code=pd_prodcode) where pd_piid=? and nvl(pd_location,' ')=' '",
					pi_id);

			rs = baseDao.queryForRowSet("SELECT pd_id FROM ProdioDetail where pd_piid=? and nvl(pd_batchcode,' ')=' '", pi_id);
			while (rs.next()) {
				baseDao.execute("update prodiodetail set pd_batchcode=? where pd_id=?", baseDao.getBatchcode(caller), rs.getInt("pd_id"));
			}

			// 入库单有生成条码但条码对应的物料或数量或批次号与明细行不一致则不允许过账
			rs = baseDao
					.queryForRowSet(
							"select wm_concat(pd_pdno)no,count(1)cn from prodiodetail left join (select sum(bi_inqty)inqty,bi_pdno,max(bi_prodcode)bi_prodcode,max(bi_batchcode)bi_batchcode from barcodeio"
									+ " where bi_piid=? group by bi_pdno) on bi_pdno=pd_pdno where pd_piid=? and pd_inqty>0 and inqty>0 "
									+ " and (nvl(inqty,0)<>pd_inqty or bi_prodcode<>pd_prodcode or bi_batchcode<>pd_batchcode) and rownum<30",
							pi_id, pi_id);
			if (rs.next() && rs.getInt("cn") > 0) {
				BaseUtil.showError("条码与明细行中的物料或数量或批次号不一致，不允许过账，请先清除不一致条码!行号：" + rs.getString("no"));
			}
		} else {
			SqlRowList rs = null;
			rs = baseDao
					.queryForRowSet(
							"SELECT pd_id,ba_purcrate FROM ProdioDetail,batch where pd_batchid=ba_id and pd_piid=? and nvl(pd_batchid,0)<>0 and nvl(pd_outqty,0)>0",
							pi_id);
			while (rs.next()) {
				if (rs.getGeneralDouble("ba_purcrate") != 0) {
					baseDao.execute("update prodiodetail set pd_purcoutqty=round(nvl(pd_outqty,0)/" + rs.getGeneralDouble("ba_purcrate")
							+ ",8) where pd_id=" + rs.getObject("pd_id"));
				} else {
					baseDao.execute("update prodiodetail set pd_purcoutqty=nvl(pd_outqty,0) where pd_id=" + rs.getObject("pd_id"));
				}
			}

		}
		useDefaultTax(caller, pi_id);
		allowZeroTax(caller, pi_id);
		checkFreezeMonth(caller, status[2]);
		checkCloseMonth(status[2]);
		checkFirstMonth(caller, status[2]);
		checkCommit(caller, pi_id);
		checkBatch(caller, pi_id);
		copcheck(pi_id, caller);
		factorycheck(pi_id, caller);
		// 制造工单加工类型
		if ("ProdInOut!Make!In".equals(caller) || "ProdInOut!OutsideCheckIn".equals(caller)) {
			checkMakeKindType(caller, pi_id);
		}
		// 暂时不启用平台 销售仓模式checkWhIsB2C(caller, pi_id);
		/**
		 * 委外验收单,委外验退单 过账限制，判断明细行中的应付供应商或币别与主表是否一致，不一致则不允许过账;
		 */
		if ("ProdInOut!OutsideCheckIn".equals(caller) || "ProdInOut!OutesideCheckReturn".equals(caller)) {
			SqlRowList rs = baseDao
					.queryForRowSet(
							"select wm_concat(pd_pdno) c from (select nvl(nvl(ma_apvendcode,ve_apvendcode),ve_code) apvendcode, pi_receivecode,pd_pdno "
									+ "from prodiodetail left join prodinout on pd_piid=pi_id left join make on ma_code=pd_ordercode left join vendor "
									+ "on ma_vendcode=ve_code where pi_id=?) t where t.apvendcode <> t.pi_receivecode and rownum<30", pi_id);
			if (rs.next()) {
				if (rs.getObject("c") != null) {
					BaseUtil.showError("明细行中委外加工单的应付供应商与主表中的应付供应商不一致!");
				}
			}
			SqlRowList rs2 = baseDao.queryForRowSet("select wm_concat(pd_pdno) x from (select ma_currency,pi_currency,pd_pdno"
					+ " from prodiodetail left join prodinout on pd_piid=pi_id left join make on ma_code=pd_ordercode"
					+ " where pi_id=?) t where t.ma_currency <> t.pi_currency and rownum<30", pi_id);
			if (rs2.next()) {
				if (rs2.getObject("x") != null) {
					BaseUtil.showError("明细行中委外加工单的币别与主表中的币别不一致!");
				}
			}
		}
		// 完工入库，委外验收限制，入库数量不允许大于到当前pi_date的月份为止的（制造单生产的总数数量-已入库总数）
		if ("ProdInOut!Make!In".equals(caller) || "ProdInOut!OutsideCheckIn".equals(caller)) {
			// 存在pi_date 年月之后的 出入库单
			int ym = DateUtil.getYearmonth(status[2].toString());
			SqlRowList rs = baseDao.queryForRowSet("select A.pd_ordercode pd_ordercode,sum(A.pd_inqty)inqty from prodiodetail A "
					+ " where A.pd_piid=?  and exists (select 1 from prodiodetail B left join prodinout on pi_id=B.pd_piid "
					+ " where A.pd_ordercode=B.pd_ordercode and  to_char(pi_date,'yyyyMM')>? and "
					+ " pi_class in('生产领料单','生产退料单','生产补料单','委外补料单','委外领料单','委外退料单') and B.pd_status=99) group by A.pd_ordercode", pi_id,
					ym);
			while (rs.next()) {
				Object[] obs = baseDao
						.getFieldsDataByCondition(
								"make left join makematerial on mm_maid=ma_id",
								new String[] {
										" min(case when mm_oneuseqty*ma_qty>mm_qty+0.1 then ma_qty else ceil((nvl(mm_havegetqty, 0)-(select sum(nvl(pd_outqty,0)-nvl(pd_inqty,0)) from prodinout left join prodiodetail on pd_piid=pi_id "
												+ " where to_char(pi_date,'yyyyMM')>'"
												+ ym
												+ "' and pd_ordercode=ma_code and pd_orderdetno=mm_detno  and pi_class in('生产领料单','生产退料单','生产补料单','委外补料单','委外领料单','委外退料单'))"
												+ " -nvl(mm_scrapqty,0))*1.0/mm_oneuseqty)end) as canmadeqty",
										"nvl(max(ma_madeqty),0) as madeqty" }, " mm_code='" + rs.getString("pd_ordercode")
										+ "' and nvl(mm_materialstatus,' ')=' ' and mm_oneuseqty>0");
				if (obs != null) {// 比较
					if (rs.getDouble("inqty") > (Double.valueOf(obs[0].toString()).doubleValue() - Double.valueOf(obs[1].toString())
							.doubleValue())) {
						BaseUtil.showError("工单[" + rs.getString("pd_ordercode") + "]当前月份领料套数(" + obs[0] + ")不足完工数");
					}
				}
			}
		}
		/**
		 * 新增生产退料和委外退料判断，替代料退料数量不能大于替代料已领数量，禁用物料判断mm_updatetype='R',
		 * 
		 * @date 2016年10月17日 下午12:30:58
		 */
		if (("ProdInOut!Make!Return").equals(caller) || ("ProdInOut!OutsideReturn").equals(caller)) {
			checkRepQty(pi_id);
		}
		/**
		 * 新增限制,拆件类型制造单不允许走完工入库单
		 * 
		 * @author XiaoST 2016年12月9日 下午4:26:34
		 */
		if ("ProdInOut!Make!In".equals(caller) || "ProdInOut!OutsideCheckIn".equals(caller)) {
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat('行号：'||pd_pdno||'工单：'||pd_ordercode) from ProdIODetail left join make on ma_code=pd_ordercode left join makekind ON mk_name=ma_kind where pd_piid=? and  mk_type='D' and rownum<20",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("拆件工单必须走拆件入库单！" + dets);
			}
		}
		baseDao.getEndDate(caller, pi_id);
		String res = null;
		// 平台生成的销售订单转出货单过账必须等于销售订单明细序号和数量
		if ("ProdInOut!Sale".equals(caller)) {
			SqlRowList rs = baseDao
					.queryForRowSet(
							"select distinct sa_code,sum(pd_outqty) out_qty,pd_orderdetno from prodiodetail left join sale on sa_code=pd_ordercode where sa_ordertype='B2C' and nvl(sa_b2ccode,' ')<>' ' and pd_piid=? group by sa_code,pd_orderdetno",
							pi_id);
			while (rs.next()) {
				rs = baseDao
						.queryForRowSet(
								"select distinct pd_ordercode pd_ordercode from prodiodetail where pd_piid=? and (pd_orderdetno not in (select sd_detno from saledetail where sd_code=?))"
										+ " OR "
										+ rs.getDouble("out_qty")
										+ " <>(select sd_qty from saledetail where sd_code=? and sd_detno=?) and  pd_ordercode =?", pi_id,
								rs.getString("sa_code"), rs.getString("sa_code"), rs.getString("pd_orderdetno"), rs.getString("sa_code"));
				if (rs.next()) {
					BaseUtil.showError("通过优软商城平台生成的发货单必须一次性发货，销售单号[" + rs.getString("pd_ordercode") + "]");
				}
				// 维护物流公司和物流单号
				Object[] obs = baseDao.getFieldsDataByCondition("prodinout", new String[] { "pi_logisticscompany", "pi_logisticscode" },
						"pi_id=" + pi_id);
				if (obs[0] == null || obs[1] == null) {
					BaseUtil.showError("请维护出货单的物流公司和物料单号信息!");
				}
			}
		}
		// 委外领、退、补单过账前
		if ("ProdInOut!OutsidePicking".equals(caller) || "ProdInOut!OutsideReturn".equals(caller) || "ProdInOut!OSMake!Give".equals(caller)) {
			// 更新：如果主表的应付供应商空，则取第一个工单的应付供应商ma_apvendcode，空则取工单供应商ma_vendcode
			baseDao.execute("update prodinout  set (pi_receivecode,pi_receivename)=(select code,ve_name from "
					+ "(select nvl(ma_apvendcode,ma_vendcode)code,ve_name from prodiodetail left join make on "
					+ "pd_ordercode=ma_code left join  vendor on nvl(ma_apvendcode,ma_vendcode)=ve_code "
					+ "where pd_piid=? order by pd_pdno) where rownum=1 )" + "where nvl(pi_receivecode,' ')=' ' and pi_id=?", pi_id, pi_id);
			// 判断主表应付供应商与从表工单应付供应商是否一致
			SqlRowList pdnos = baseDao
					.queryForRowSet(
							"select wm_concat(pd_pdno) c from (select nvl(nvl(ma_apvendcode,ve_apvendcode),ve_code) apvendcode, pi_receivecode,pd_pdno "
									+ "from prodiodetail left join prodinout on pd_piid=pi_id left join make on ma_code=pd_ordercode left join vendor "
									+ "on ma_vendcode=ve_code where pi_id=?) t where t.apvendcode <> t.pi_receivecode and rownum<30", pi_id);
			if (pdnos.next()) {
				if (pdnos.getObject("c") != null) {
					BaseUtil.showError("行" + pdnos.getObject("c") + "工单应付供应商与主表应付供应商不一致，不能过账");
				}
			}
		}
		// @add20171103
		if ("ProdInOut!PurcCheckin".equals(caller)) {// 采购验收单根据采购单号、序号，更新入库销售单号、序号
			baseDao.execute(
					"update prodiodetail set (pd_salecode,pd_saledetno,pd_topmothercode)=(select d.pd_salecode,d.pd_saledetno,d.pd_topmothercode from purchase left join purchasedetail d on pu_id=pd_puid where "
							+ " pd_ordercode=pu_code and pd_orderdetno=pd_detno) where pd_piid=?", pi_id);
		}
		// 生产退料单、委外退料单、拆件入库单、完工入库单、委外验收单,根据工单的订单号、订单序号能匹配到销售单号和序号（如果是预测单则不锁批记录）则更新到入库单明细的销售单号、序号
		if ("ProdInOut!OutsideReturn".equals(caller) || "ProdInOut!OutsideCheckIn".equals(caller) || "ProdInOut!Make!In".equals(caller)
				|| "ProdInOut!PartitionStockIn".equals(caller) || "ProdInOut!Make!Return".equals(caller)) {
			baseDao.execute(
					"update prodiodetail set (pd_salecode,pd_saledetno,pd_topmothercode)=(select ma_salecode,ma_saledetno,nvl(ma_topmothercode,ma_prodcode)topmothercode from make where pd_ordercode=ma_code)"
							+ " where pd_piid=? and exists (select 1 from make left join sale on sa_code=ma_salecode left join saledetail on sd_said=sa_id and sd_detno=ma_saledetno where pd_ordercode=ma_code and sd_detno>0)",
					pi_id);

		}
		/*
		 * 退料数量不能大于维护的可退数量 String SQLStr_s = ""; SqlRowList rs_s; SQLStr_s=
		 * "select * from ProdIODetail left join make on ma_code=pd_ordercode"
		 * +" left join IO_MAKEMATERIAL_DETNO_VIEW on pd_orderdetno = mm_detno"
		 * +" where ma_id=mm_maid and pd_piid= '"+pi_id+"'"; rs_s =
		 * baseDao.queryForRowSet(SQLStr_s); if(rs_s.next()){ if
		 * (rs_s.getInt("pd_inqty")>rs_s.getInt("mm_havegetqty")) {
		 * BaseUtil.showError("工单序号[" + rs_s.getString("pd_orderdetno") +
		 * "]的退料数量不能大于维护的可退数量!'"); } }
		 */
		// 过账前的其它逻辑
		handlerService.beforePost(caller, pi_id);
		getTotal(pi_id, caller);// 调用过账存储过程前后都掉用一次gettotal方法，解决出入库单税率与发票不一致
		// 执行过账操作
		Object[] objs = baseDao.getFieldsDataByCondition("ProdInOut", new String[] { "pi_class", "pi_inoutno" }, "pi_id=" + pi_id);
		if ("ProdInOut!CostChange".equals(caller)) {
			res = baseDao.callProcedure("SP_PRODUCTCOSTADJUST", new Object[] { objs[0].toString(), objs[1].toString(), "" });
			if (res != null && !res.trim().equals("")) {
				BaseUtil.showError(res);
			}
		} else {
			baseDao.procedure("SP_GetCostPrice", new Object[] { objs[0].toString(), objs[1].toString() });
			res = baseDao.callProcedure("Sp_SplitProdOut",
					new Object[] { objs[0].toString(), objs[1].toString(), String.valueOf(SystemSession.getUser().getEm_name()) });

			if (res != null && !res.trim().equals("")) {
				// 重新添加提示限制信息
				BaseUtil.showErrorOnSuccess(res + "  " + objs[0].toString() + objs[1].toString() + "，过账失败");
				// BaseUtil.showError(res);
			}
			// @add 20170614 抓取批次号成功之后更新出库单据的备料状态
			updatePdaStatus(caller, pi_id);
			if (baseDao.isDBSetting(caller, "ifBatchCodeNotChange") && baseDao.isDBSetting(caller, "autoPostIn")) {
				// 拨出单过帐后产生的拨入单批号不变，同一物料同仓库不能同时入两次相同的批号
				SqlRowList rs1 = baseDao
						.queryForRowSet("select  count(1)n, wm_concat(pd_pdno)detno from (select pd_batchcode,pd_inwhcode,pd_prodcode,min(pd_pdno)pd_pdno,count(1)c from  ProdIODetail where pd_piid="
								+ pi_id + " and pd_batchcode<>' ' group by pd_batchcode,pd_inwhcode,pd_prodcode ) where c> 1");
				if (rs1.next()) {
					if (rs1.getInt("n") > 0) {
						BaseUtil.showError("拨出单过帐后产生的拨入单批号不变，同一物料同拨入仓库批号不能相同！行号：" + rs1.getString("detno"));
					}
				}
			}
			checkBatchRemain(pi_id);
			// 存储过程
			res = baseDao.callProcedure("Sp_CommitProdInout",
					new Object[] { objs[0].toString(), objs[1].toString(), String.valueOf(SystemSession.getUser().getEm_name()) });
			if (res != null && !res.trim().equals("")) {
				BaseUtil.showError(res);
			}
			if (baseDao.isDBSetting("autoCreateApBill")) {
				if ("采购验收单".equals(status[3]) || "采购验退单".equals(status[3]) || "委外验收单".equals(status[3]) || "委外验退单".equals(status[3])
						|| "用品验收单".equals(status[3]) || "用品验退单".equals(status[3])) {
					dets = baseDao.queryForObject("select wm_concat(ab_code) from apbill where ab_source=? and ab_statuscode<>'POSTED'",
							String.class, status[5]);
					if (dets != null) {
						BaseUtil.showError("发票" + dets + "未过账成功，请手工过账！");
					}
				}
			}
			if (baseDao.isDBSetting("autoCreateArBill")) {
				if ("出货单".equals(status[3]) || "销售退货单".equals(status[3])) {
					dets = baseDao.queryForObject(
							"select wm_concat(ab_code) from arbill where AB_SOURCECODE=? and ab_statuscode<>'POSTED'", String.class,
							status[5]);
					if (dets != null) {
						BaseUtil.showError("发票" + dets + "未过账成功，请手工过账！");
					}
				}
			}
		}
		getTotal(pi_id, caller);
		baseDao.updateByCondition("ProdInOut", "pi_statuscode='POSTED',pi_status='" + BaseUtil.getLocalMessage("POSTED")
				+ "',pi_inoutman='" + SystemSession.getUser().getEm_name() + "',pi_date1=sysdate,pi_sendstatus='待上传'", "pi_id=" + pi_id);
		if (!prodInOutDao.isIn(caller)) {// 出库类单据过账时，根据批号抓取batch.ba_vendorrate到prodiodetail.pd_vendorrate
			baseDao.execute("update prodiodetail set pd_vendorrate=(select ba_vendorrate from batch where ba_code=pd_batchcode"
					+ " and ba_whcode =pd_whcode and ba_prodcode=pd_prodcode ) where pd_piid=" + pi_id + " and nvl(pd_vendorrate,0)=0");
		}
		if (baseDao.isDBSetting("updateBusinessChance") && !"ProdInOut!GoodsIn".equals(caller)) {// 更新当前商机阶段
			Object bsname = baseDao.getFieldDataByCondition("BusinessChanceStage", "bs_name", "bs_relativeitem='Shipment'");
			Object date = baseDao.getFieldDataByCondition("ProdInOut", "to_char(pi_recorddate,'yyyy-MM-dd')", "pi_id=" + pi_id);

			List<Object[]> data = baseDao.getFieldsDatasByCondition("prodiodetail  left join ProdInOut on pd_piid=pi_id", new String[] {
					"pd_bcid", "pd_prodcode", "pd_pdno" }, "pd_piid=" + pi_id);
			for (Object[] os : data) {
				if (os[0] == null || os[0].equals("") || os[0].equals("0") || Integer.parseInt(os[0].toString()) == 0) {
					String sql = "update Prodiodetail set pd_bcid=nvl((select sd_bcid from saledetail "
							+ "where pd_ordercode=sd_code and pd_orderdetno=sd_detno),0) where pd_piid=" + pi_id + " and nvl(pd_bcid,0)=0";
					baseDao.execute(sql);
				}
				Object pd_bcid = baseDao.getFieldDataByCondition("Prodiodetail", "pd_bcid", " pd_prodcode='" + os[1] + "' and pd_piid="
						+ pi_id);
				if (pd_bcid == null || pd_bcid.equals("") || pd_bcid.equals("0") || Integer.parseInt(pd_bcid.toString()) == 0) {
					String sql = "update prodiodetail set pd_bcid=nvl((select max(bc_id) from prodinout,businesschance "
							+ "where pi_id=pd_piid and pi_cardcode=bc_custcode and pd_prodcode=bc_model and bc_status<>'已结案'),0) where nvl(pd_bcid,0)=0 and pd_piid="
							+ pi_id + " and nvl(pd_bcid,0)=0";
					baseDao.execute(sql);
				}
				Object bc_id = baseDao.getFieldDataByCondition("Prodiodetail", "pd_bcid", " pd_prodcode='" + os[1] + "' and pd_piid="
						+ pi_id);
				Integer bs_detno = baseDao.getFieldValue("businesschancestage", "bs_detno", "bs_name='" + bsname + "'", Integer.class);
				Integer bs_detno1 = baseDao.getFieldValue("businesschance left join businesschancestage on bs_name=bc_currentprocess",
						"nvl(bs_detno,0)", "bc_id=" + bc_id, Integer.class);
				if (bs_detno != null && bs_detno1 != null) {
					if (bs_detno >= bs_detno1) {
						baseDao.updateByCondition("BusinessChance", "bc_currentprocess='" + bsname + "',bc_desc" + bs_detno + "='" + bsname
								+ "',bc_date" + bs_detno + "=to_date('" + date.toString() + "','yyyy-MM-dd')", "bc_id=" + bc_id);
						if (bsname != null && Integer.parseInt(pd_bcid.toString()) != 0) {
							Object bscode = baseDao.getFieldDataByCondition("BusinessChanceStage", "bs_code", "bs_name='" + bsname + "'");
							// 插入一条记录到商机动态表
							int bcd_id = baseDao.getSeqId("BusinessChanceData_seq");
							String link = "jsps/scm/reserve/prodInOut.jsp?whoami=ProdInOut!Sale&formCondition=pi_idIS" + pi_id
									+ "&gridCondition=pd_piidIS" + pi_id;
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
									+ status[4]
									+ "','"
									+ "ENTERING" + "','" + BaseUtil.getLocalMessage("ENTERING") + "','" + status[5] + "','" + link + "')";
							baseDao.execute(contactSql);
						}
					}
				}

			}

		}
		// 记录操作
		baseDao.logger.post(caller, "pi_id", pi_id);
		// 执行过账后的其它逻辑
		handlerService.afterPost(caller, pi_id);
		// 调用冲销
		saleClashService.createSaleClash(pi_id, "ProdInOut");
		if ("ProdInOut!AppropriationOut".equals(caller) || "ProdInOut!SaleAppropriationOut".equals(caller)) {
			dets = null;
			if (baseDao.isDBSetting(caller, "autoPostIn")) {
				// 过账销售拨入单
				dets = prodInOutDao.turnProdIO(pi_id);
				// 记录操作
				baseDao.logger.others(BaseUtil.getLocalMessage("msg.turnProdIO"), BaseUtil.getLocalMessage("msg.turnSuccess"), caller,
						"pi_id", pi_id);
				//过账失败提醒
				boolean showTurnProdIOError = baseDao.checkIf("ProdInOut", 
						"pi_class='拨入单' and pi_statuscode='UNPOST' and pi_inoutno=(select pi_relativeplace from ProdInOut where pi_class='拨出单' and pi_id="+pi_id+")");
				if(showTurnProdIOError) {
			        StringBuffer sb = new StringBuffer();
					String formCondition = "pi_idIS" + pi_id;
					String gridCondition = "pd_piidIS" + pi_id;
					int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
					String url="<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?whoami=ProdInOut!AppropriationOut&formCondition=" + formCondition + "&gridCondition=" + gridCondition+ "&_noc=1')\">"+objs[1]+"</a>";
					Employee employee = SystemSession.getUser();
					sb.append("您的拨出单&nbsp;&nbsp;"+url+"自动过账失败"+employee.getEm_name()+"审批");
						

					PagingRelease Pr = new PagingRelease(pr_id, employee.getEm_name(), new Date(), employee.getEm_id(), sb.toString(), "system",
							String.valueOf(objs[1]),pi_id, caller, "知会消息");
					baseDao.save(Pr);
					//保存到历史消息表
					int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
					baseDao.execute("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
							+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
							+ " where pr_id="+pr_id);
					String sql = "insert into pagingreleasedetail(prd_id,prd_prid,PRD_RECIPIENTID,PRD_RECIPIENT) values(PAGINGRELEASEDETAIL_SEQ.nextval,'"
							+ pr_id + "','" + employee.getEm_id() + "','" + employee.getEm_name() + "')";
					baseDao.execute(sql);			
					baseDao.execute("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
							+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+"and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");
				}
			}
			if (dets != null) {
				BaseUtil.appendError("拨入单明细行仓库对应的仓管员与当前过账人不一致，不允许进行当前操作!行号：" + dets);
			}
			if (baseDao.isDBSetting(caller, "autoPostMakeLSSend")) {// MakeSendLS
																	// 拉式发料
																	// 拨出单过账,拨入也过账时自动过账领料单
				String SQLStr = "";
				String Outpiclass = "";
				SQLStr = "select pi_inoutno from prodinout where pi_id=" + pi_id + " and pi_statuscode='POSTED'";
				SqlRowList rs0 = baseDao.queryForRowSet(SQLStr);
				if (rs0.next()) {
					SQLStr = "select pi_id,pi_class,pi_inoutno from prodinout where pi_fromcode='" + rs0.getString("pi_inoutno")
							+ "' and pi_statuscode='UNPOST' ";
					SqlRowList rs = baseDao.queryForRowSet(SQLStr);
					if (rs.next()) {
						Outpiclass = rs.getObject("pi_class").toString();
						if (Outpiclass.equals("生产领料单")) {
							postProdInOut(rs.getInt("pi_id"), "ProdInOut!Picking");
						} else if (Outpiclass.equals("委外领料单")) {
							postProdInOut(rs.getInt("pi_id"), "ProdInOut!OutsidePicking");
						}
						SQLStr = "select pi_statuscode from prodinout where pi_id=" + rs.getInt("pi_id") + " and pi_statuscode='POSTED' ";
						rs0 = baseDao.queryForRowSet(SQLStr);
						if (rs0.next()) {
							BaseUtil.showErrorOnSuccess(Outpiclass + "：" + rs.getString("pi_inoutno") + "过账成功!");
						} else {
							BaseUtil.showErrorOnSuccess(Outpiclass + "：" + rs.getString("pi_inoutno") + "过账不成功!");
						}
					}
				}

			}
		}
		// 委外验收，验退，完工入库单，过账更新完工状态
		if ("ProdInOut!Make!In".equals(caller) || "ProdInOut!OutsideCheckIn".equals(caller)
				|| "ProdInOut!OutesideCheckReturn".equals(caller)) {
			SqlRowList rs = baseDao.queryForRowSet(
					"select distinct ma_id from  prodiodetail left join make on ma_code=pd_ordercode where pd_piid=?", pi_id);
			while (rs.next()) {// 更新完工状态
				makeDao.updateMakeFinishStatus(rs.getInt("ma_id"));
			}
		}
		// 出货单过账时候,如果B2C平台获取的销售订单出货，通知B2C发货
		if ("ProdInOut!Sale".equals(caller)) {
			/**
			 * @Tips 新增 2016年9月6日 下午2:14:40
			 *       自动扣减平台仓库存表goodspwonhand，更新上架单剩余可售数，已销售数 ，采用存储过程
			 */
			b2CSaleOrderSend(pi_id);
		}
		// 出库单过账后判断是否生成下架单
		if (!isProdIn) {
			autoGoodsOff(pi_id);
		}
		if ("ProdInOut!AppropriationOut".equals(caller)) {
			SqlRowList rs3 = baseDao
					.queryForRowSet(
							"select b.pi_id pi_id,a.pi_relativeplace pi_relativeplace from prodinout a left join prodinout  b on a.pi_relativeplace=b.pi_inoutno where a.pi_id=? and b.pi_class='拨入单'",
							pi_id);
			if (rs3.next()) {
				BaseUtil.showErrorOnSuccess("拨出单过账成功!系统产生的拨入单号：<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
						+ rs3.getInt("pi_id")
						+ "&gridCondition=pd_piidIS"
						+ rs3.getInt("pi_id")
						+ "&whoami=ProdInOut!AppropriationIn')\">"
						+ rs3.getString("pi_relativeplace") + "</a>&nbsp;");
			}
		}

		if ("ProdInOut!PurcCheckin".equals(caller)) {// 采购验收单过账根据采购单+采购单序号更新pd_vendorrate
														// ba_vendorrate
			SqlRowList pdRowList = baseDao
					.queryForRowSet("select pd_id,nvl(pd_batchid,0) pd_batchid,nvl(pd_ordercode,' ') pd_ordercode,nvl(pd_orderdetno,0) pd_orderdetno from prodiodetail where pd_piid="
							+ pi_id);
			while (pdRowList.next()) {
				baseDao.execute("update prodiodetail set pd_vendorrate=(select pd_vendorrate from purchasedetail where pd_code='"
						+ pdRowList.getObject("pd_ordercode") + "' and pd_detno='" + pdRowList.getObject("pd_orderdetno")
						+ "') where pd_id=" + pdRowList.getObject("pd_id"));
				baseDao.execute("update batch set ba_vendorrate=(select pd_vendorrate from purchasedetail where pd_code='"
						+ pdRowList.getObject("pd_ordercode") + "' and pd_detno='" + pdRowList.getObject("pd_orderdetno")
						+ "') where ba_id=" + pdRowList.getObject("pd_batchid"));
			}
		}
		if ("ProdInOut!Make!Give".equals(caller) || "ProdInOut!Make!Return".equals(caller) || "ProdInOut!Picking".equals(caller)
				|| "ProdInOut!OSMake!Give".equals(caller) || "ProdInOut!OutsideReturn".equals(caller)
				|| "ProdInOut!OutsidePicking".equals(caller)) {
			changeMaStatus(pi_id);
		}
		// 2017-08-30 重新根据pdno更新批号仓库属性
		if (isProdIn) {
			baseDao.execute(
					"update barcodeio set (bi_batchcode,bi_batchid,bi_whcode)=(select pd_batchcode,pd_batchid,pd_whcode from prodiodetail where bi_piid=pd_piid and pd_pdno=bi_pdno) where bi_piid=? and nvl(bi_status,0)=0",
					pi_id);
			baseDao.execute(
					"update batch set ba_hasbarcode=-1 where ba_id in (select pd_batchid from prodiodetail "
							+ " where pd_piid=?) and NVL(ba_hasbarcode,0)=0 and ba_id in (select bi_batchid from barcodeio where bi_inqty>0 and bi_status=0) ",
					pi_id);
		}
		/**
		 * @author wsy
		 *         其它入库单：如果选择了采购单号、采购序号后，提交、审核的时候要判断采购单号+采购序号+物料编号是否一致，不一致限制提交
		 *         、审核
		 */
		if ("ProdInOut!OtherIn".equals(caller)) {
			baseDao.execute("update purchasedetail set pd_beipinacceptqty=(nvl((select sum(nvl(pd_purcinqty,0)) from prodiodetail where pd_piclass='其它入库单' and pd_ordercode=pd_code and pd_orderdetno=pd_detno and pd_status=99),0))where (pd_code,pd_detno) in (select pd_ordercode,pd_orderdetno from prodiodetail where pd_piid="
					+ pi_id + " and pd_piclass='其它入库单')");
		}
	}

	void checkCloseMonth(Object pidate) {
		boolean bool = baseDao.checkIf("PeriodsDetail", "pd_code='MONTH-P' and pd_status=99 and pd_detno=to_char(to_date('" + pidate
				+ "','yyyy-mm-dd hh24:mi:ss'), 'yyyymm')");
		if (bool) {
			BaseUtil.showError("单据日期所属期间已结账，不允许进行当前操作！");
		}
	}

	void checkMakeKindType(String caller, Object pi_id) {
		if (baseDao.isDBSetting(caller, "makeKindType")) {
			String sql = "select count(1)cn from (select count(1) cn,  mk_type from make left join makekind on mk_name =ma_kind"
					+ " left join prodiodetail on pd_ordercode = ma_code where pd_piid=? group by mk_type) ";
			SqlRowList sqlRowList = baseDao.queryForRowSet(sql, pi_id);
			if (sqlRowList.next()) {
				if (sqlRowList.getInt("cn") > 1) {
					BaseUtil.showError("明细行制造单加工类型不同，不能使用同一张完工入库单");
				}
			}
		}
	}

	void checkFreezeMonth(String caller, Object pidate) {
		if (!"ProdInOut!CostChange".equals(caller)) {
			String freeze = baseDao.getDBSetting("freezeMonth");
			if (freeze != null && !freeze.equals("")) {
				if (Integer.parseInt(freeze) == DateUtil.getYearmonth(pidate.toString())) {
					BaseUtil.showError("单据日期所属期间已冻结，不允许进行当前操作！");
				}
			}
		}
	}

	void checkFirstMonth(String caller, Object pidate) {
		if ("ProdInOut!ReserveInitialize".equals(caller)) {
			Object first = baseDao.getFieldDataByCondition("periods", "nvl(PE_FIRSTDAY,0)", "PE_CODE='MONTH-P'");
			if ("0".equals(first.toString())) {
				BaseUtil.showError("请前往【初始化期间设置】确认开账期间！");
			} else {
				if (Integer.parseInt(first.toString()) != DateUtil.getYearmonth(pidate.toString())) {
					BaseUtil.showError("库存初始化单据只能发生在库存模块的开账期间！当前库存模块开账期间为[" + first + "]，请前往【初始化期间设置】确认开账期间！");
				}
			}
		}
	}

	@Override
	public void resPostProdInOut(String caller, int pi_id) {
		// 只能对状态为[已过账]的单据进行反过账操作!
		String dets = null;
		Object[] status = baseDao.getFieldsDataByCondition("ProdInOut", new String[] { "pi_statuscode", "pi_date", "pi_class",
				"pi_relativeplace", "pi_inoutno" }, "pi_id=" + pi_id);
		if (!"POSTED".equals(status[0])) {
			BaseUtil.showError("只能对已过账的" + status[2] + "进行反过账操作！");
		}
		if ("ProdInOut!AppropriationOut".equals(caller) || "ProdInOut!SaleAppropriationOut".equals(caller)) {
			if (!baseDao.isDBSetting(caller, "autoResPostIn")) {
				if (status[3] != null) {
					dets = baseDao
							.getJdbcTemplate()
							.queryForObject(
									"select wm_concat(pi_class||'['||pi_inoutno||']') from prodinout where pi_inoutno=? and pi_statuscode='POSTED'",
									String.class, status[3]);
					if (dets != null) {
						BaseUtil.showError("请先反过账对应的" + dets);
					}
				}
			}
		}
		// 出入库单上传状态，处于上传过程中的单据，不允许操作
		String sendStatus = baseDao.getFieldValue("ProdInOut", "pi_sendstatus", "pi_id=" + pi_id, String.class);
		StateAssert.onSendingLimit(sendStatus);
		if ("ProdInOut!DefectIn".equals(caller)) {// 不良品入库单
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(mr_code) from QUA_MRB left join Prodiodetail on pd_id=mr_veid where pd_piid=?", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("已转MRB的不良品入库单,不允许反过账！MRB单号：" + dets);
			}
		}
		if ("ProdInOut!SaleBorrow".equals(caller)) {// 借货出货单
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(distinct ra_code) from RenewApply left join RenewApplyDetail on ra_id=rad_raid left join Prodiodetail on pd_id=rad_pdid where pd_piid=?",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("已转续借申请单,不允许反过账！续借申请单号：" + dets);
			}
		}
		// 采购验收单、采购验退单、出货单、销售退货单已转其它应收单的不允许反过账
		if ("ProdInOut!PurcCheckin".equals(caller) || "ProdInOut!PurcCheckout".equals(caller) || "ProdInOut!Sale".equals(caller)
				|| "ProdInOut!SaleReturn".equals(caller)) {
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(ab_code) from arbill where ab_class='其它应收单' and AB_SOURCECODE='" + status[4]
							+ "' and ab_sourcetype='" + status[2] + "'", String.class);
			if (dets != null) {
				BaseUtil.showError("当前" + status[2] + "已产生其它应收单[" + dets + "],不允许反过账！");
			}
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat('行号：'||detno||'，'||piclass||'号：'||picode) from (select a.pd_pdno detno, b.pd_piclass piclass, b.pd_inoutno picode from ProdIODetail a left join prodiodetail b on a.pd_id=b.pd_ioid where a.pd_piid=? and nvl(a.pd_yqty,0)>0)",
						String.class, pi_id);
		if (dets != null) {
			BaseUtil.showError("已转出单据,不允许反过账！" + dets);
		}
		if ("ProdInOut!DefectOut".equals(caller)) {
			SqlRowList rs = baseDao
					.queryForRowSet(
							"select sum(pd_outqty),pd_ordercode,pd_orderdetno from prodiodetail where pd_piid=? group by pd_ordercode,pd_orderdetno",
							pi_id);
			while (rs.next()) {
				dets = baseDao.getJdbcTemplate().queryForObject(
						"select wm_concat(pd_detno) from PurchaseDetail where pd_code='" + rs.getObject("pd_ordercode") + "' and pd_detno="
								+ rs.getObject("pd_orderdetno") + " and nvl(pd_acceptqty,0) + nvl(pd_ngacceptqty,0) > nvl(pd_qty,0) - "
								+ rs.getGeneralDouble(1), String.class);
				if (dets != null) {
					BaseUtil.showError("采购单的合格入库数+不良入库数大于采购单数量,不允许反过账!采购单号：" + rs.getObject("pd_ordercode") + ",行号："
							+ rs.getObject("pd_orderdetno"));
				}
			}
		}
		if ("ProdInOut!PurcCheckin".equals(caller) || "ProdInOut!PurcCheckout".equals(caller)) {
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(pd_pdno) from ProdIODetail left join PurchaseDetail on pd_ordercode=pd_code and pd_orderdetno=pd_detno where "
							+ "pd_piid=? and nvl(pd_mrpstatuscode,' ') in ('FREEZE','FINISH') and pd_piclass in ('采购验收单','采购验退单') "
							+ "and nvl(pd_ordercode,' ')<>' '", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("采购单明细已冻结或者已结案，不允许进行当前操作!行号：" + dets);
			}
		}
		if ("ProdInOut!DefectIn".equals(caller) || "ProdInOut!DefectOut".equals(caller)) {
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(pd_pdno) from ProdIODetail left join PurchaseDetail on pd_ordercode=pd_code and pd_orderdetno=pd_detno where "
							+ "pd_piid=? and nvl(pd_mrpstatuscode,' ') in ('FREEZE','FINISH') and pd_piclass in ('不良品入库单','不良品出库单') "
							+ "and nvl(pd_ordercode,' ')<>' ' and nvl(pd_qctype,' ')='采购检验单'", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("采购单明细已冻结或者已结案，不允许进行当前操作!行号：" + dets);
			}
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(pd_pdno) from ProdIODetail left join Make on pd_ordercode=ma_code where "
							+ "pd_piid=? and nvl(ma_statuscode,' ') in ('FREEZE','FINISH') and pd_piclass in ('不良品入库单','不良品出库单') "
							+ "and nvl(pd_ordercode,' ')<>' ' and nvl(pd_qctype,' ')='委外检验单'", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细行委外单已冻结或者已结案，不允许进行当前操作!行号：" + dets);
			}
			// 不良品出货单反过账时 ，对应的采购验收单 是在录入 未过账的 就可以反过账否则限制
			Object fcode = baseDao.getFieldDataByCondition("prodinout", "pi_fromcode", "pi_id=" + pi_id);
			if (StringUtil.hasText(fcode)) {
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pi_inoutno) from prodinout where pi_inoutno=(select pi_fromcode from prodinout where pi_id=?) and pi_class='采购验收单' and pi_status='未过账' and pi_invostatus='在录入'",
								String.class, pi_id);
				Object[] fromcode = baseDao.getFieldsDataByCondition("prodinout", new String[] { "pi_inoutno", "pi_id" },
						"pi_inoutno=(select pi_fromcode from prodinout where pi_id=" + pi_id + ")");
				if (dets == null && fromcode != null) {
					BaseUtil.showError("关联的采购验收单"
							+ "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?whoami=ProdInOut!PurcCheckin&formCondition=pi_idIS"
							+ fromcode[1] + "&gridCondition=pd_piidIS" + fromcode[1] + "')\">" + fromcode[0] + "</a>&nbsp;"
							+ "不是在录入、未过账的状态,不允许进行当前操作");
				}
			}
		}
		if ("ProdInOut!PurcCheckout".equals(caller) || "ProdInOut!DefectOut".equals(caller)) {
			purchaseDao.updatePurcYNotifyQTY(0,
					"select pd_id from purchasedetail where (pd_code,pd_detno) in (select pd_ordercode,pd_orderdetno from prodiodetail where pd_piid="
							+ pi_id + ")");
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail left join PurchaseDetail on PurchaseDetail.pd_code=ProdIODetail.pd_ordercode and PurchaseDetail.pd_detno=ProdIODetail.pd_orderdetno "
									+ "where pd_piid=? and nvl(PurchaseDetail.pd_yqty,0)>nvl(PurchaseDetail.pd_qty,0)-nvl(PurchaseDetail.pd_frozenqty,0)-nvl(PurchaseDetail.pd_turnqty,0) "
									+ "and pd_piclass in ('采购验退单','不良品出库单') and nvl(ProdIODetail.pd_ordercode,' ')<>' '", String.class,
							pi_id);
			if (dets != null) {
				BaseUtil.showError("明细采购订单已转数大于采购订单数量-已冻结数量-已投放送货通知数，不允许进行当前操作!行号：" + dets);
			}
		}
		if (baseDao.isDBSetting("cgyCheck")) {
			/**
			 * maz 出入库单判断过账人是否在明细行仓库的仓管员资料表中存在,人员资料中查找管理员一样限制如果为非仓库员不允许过账
			 * 虚拟账号不限制 2017080135
			 */
			Object type = baseDao.getFieldDataByCondition("Employee", "em_code", "em_code='" + SystemSession.getUser().getEm_code() + "'");
			if (type != null) {
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_pdno) from prodinout,prodiodetail where pi_id=pd_piid and pi_id=? and pd_id not in "
										+ "(select pd_id from prodinout,prodiodetail,warehouse,warehouseman where pi_id=pd_piid and pd_whcode=wh_code and wh_id=wm_whid "
										+ "and pi_id=? and wm_cgycode=?)", String.class, pi_id, pi_id, SystemSession.getUser().getEm_code());
				if (dets != null) {
					BaseUtil.showError("明细行仓库对应的仓管员与当前过账人不一致，不允许进行当前操作!行号：" + dets);
				}
			}
		}
		checkFreezeMonth(caller, status[1]);
		checkCloseMonth(status[1]);
		checkFirstMonth(caller, status[1]);
		// 如冲销的预测单号、序号的明细状态为已结案，限制反过账并提示；
		saleClashService.getSaleClash(pi_id, "ProdInOut");
		if ("ProdInOut!DefectOut".equals(caller)) {// 不良品出库单
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_inoutno) from prodiodetail where pd_piclass='采购验收单' and pd_mrid in (select pd_mrid from prodiodetail where pd_piid=? and pd_mrid is not null)",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("存在对应的采购验收单，不允许反过账!验收单单号：" + dets);
			}
		}
		if ("ProdInOut!SaleBorrow".equals(caller)) {// 借货出货单
			String sql = "select count(*) from prodiodetail where pd_piid=" + pi_id + " and (pd_reply='已归还' or pd_reply='部分归还')";
			int count = baseDao.getCount(sql);
			if (count > 0) {
				BaseUtil.showError("明细已有归还，无法反过账！");
			}
		}
		if ("ProdInOut!SaleReturn".equals(caller)) {
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail left join SaleDetail on sd_code=pd_ordercode and sd_detno=pd_orderdetno where pd_piid=? and pd_piclass='销售退货单' and nvl(pd_ordercode,' ')<>' ' and nvl(pd_inqty,0) + nvl(sd_yqty,0) > nvl(sd_qty,0)",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("本次退货数量+订单已转数大于订单数量，不允许反过账!行号：" + dets);
			}
		}
		if ("ProdInOut!SaleAppropriationOut".equals(caller) || "ProdInOut!OtherOut".equals(caller)) {
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail where pd_piid=? and pd_piclass in ('销售拨出单','其它出库单') and (pd_plancode,pd_forecastdetno) in (select sd_code, sd_detno from saleforecastdetail where sd_statuscode='FINISH') and nvl(pd_plancode,' ')<>' '",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细行预测单号+预测序号状态等于已结案，不允许进行当前操作!行号：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail where pd_piid=? and pd_piclass in ('销售拨出单','其它出库单') and (pd_ordercode,pd_orderdetno) in (select sd_code, sd_detno from saledetail where sd_statuscode='FINISH') and nvl(pd_ordercode,' ')<>' '",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细行销售单号+销售序号状态等于已结案，不允许进行当前操作!行号：" + dets);
			}
		}
		if ("ProdInOut!PurcCheckin".equals(caller) || "ProdInOut!PurcCheckout".equals(caller) || "ProdInOut!OutsideCheckIn".equals(caller)
				|| "ProdInOut!OutesideCheckReturn".equals(caller) || "ProdInOut!GoodsIn".equals(caller)
				|| "ProdInOut!GoodsOut".equals(caller)) {
			baseDao.execute("update ProdIoDetail set pd_invoqty=nvl((select sum(abd_qty) from apbilldetail,apbill where abd_abid=ab_id and abd_pdid=pd_id and abd_sourcekind='PRODIODETAIL' and ab_statuscode='POSTED'"
					+ " and abd_pdid in(select pd_id from prodiodetail where pd_piid="
					+ pi_id
					+ ") group by abd_pdid,abd_sourcekind ),0) where pd_piid="
					+ pi_id
					+ " and pd_piclass in ('委外验收单','采购验收单','采购验退单','委外验退单','用品验退单','用品验收单')");
			baseDao.execute("update ProdIoDetail set pd_showinvoqty=nvl((select sum(abd_qty) from apbilldetail where abd_pdid=pd_id and nvl(abd_adid,0)=0 and abd_sourcekind='PRODIODETAIL' group by abd_pdid,abd_sourcekind ),0) where pd_piid="
					+ pi_id + " and pd_piclass in ('委外验收单','采购验收单','采购验退单','委外验退单','用品验退单','用品验收单')");
		}
		if ("ProdInOut!Sale".equals(caller) || "ProdInOut!SaleReturn".equals(caller)) {
			baseDao.execute("update ProdIoDetail set pd_invoqty=nvl((select sum(abd_qty) from arbilldetail,arbill where abd_abid=ab_id and abd_pdid=pd_id and abd_sourcekind='PRODIODETAIL' and ab_statuscode='POSTED' group by abd_pdid,abd_sourcekind ),0) where pd_piid="
					+ pi_id + " and pd_piclass in ('销售退货单','出货单')");
			baseDao.execute("update ProdIoDetail set pd_showinvoqty=nvl((select sum(abd_qty) from arbilldetail,arbill where abd_abid=ab_id and abd_pdid=pd_id and nvl(abd_adid,0)=0 and abd_sourcekind='PRODIODETAIL' group by abd_pdid,abd_sourcekind ),0) where pd_piid="
					+ pi_id + " and pd_piclass in ('销售退货单','出货单')");
		}
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select nvl(sum(abs(nvl(pd_showinvoqty,0))),0),nvl(sum(abs(nvl(pd_turngsqty,0))),0),nvl(sum(abs(nvl(pd_turnesqty,0))),0),nvl(sum(abs(nvl(pd_invoqty,0))),0),nvl(sum(abs(nvl(pd_gsqty,0))),0),nvl(sum(abs(nvl(pd_esqty,0))),0) from ProdIoDetail where pd_piid=?",
						pi_id);
		if (rs.next()) {
			if (rs.getGeneralDouble(1) != 0 || rs.getGeneralDouble(4) != 0) {
				BaseUtil.showError(BaseUtil.getLocalMessage("scm.reserve.prodInOut.resPost_haveTurnARAP"));
			}
			if (rs.getGeneralDouble(2) != 0 || rs.getGeneralDouble(5) != 0) {
				BaseUtil.showError(BaseUtil.getLocalMessage("scm.reserve.prodInOut.resPost_haveTurnGS"));
			}
			if (rs.getGeneralDouble(3) != 0 || rs.getGeneralDouble(6) != 0) {
				BaseUtil.showError(BaseUtil.getLocalMessage("scm.reserve.prodInOut.resPost_haveTurnES"));
			}
		}
		checkVoucher(pi_id);
		// 若入库单明细行物料号+批号+仓库出现在已过账的成本调整单中，且成本调整单制作了凭证的，限制反过账
		if (prodInOutDao.isIn(caller)) {
			SqlRowList rs_checkCostChange = baseDao
					.queryForRowSet(
							"select pd_pdno,WMSYS.WM_CONCAT(DISTINCT pi_inoutno) pi_inoutno from (select a.pd_pdno,a.pd_id,b.pi_inoutno "
									+ "from prodiodetail a left join (select pd_batchcode,pd_prodcode,pd_whcode,pi_vouchercode,pi_inoutno from prodiodetail "
									+ "left join prodinout on pd_piid=pi_id where  pi_class='成本调整单' and pi_statuscode='POSTED' and "
									+ "nvl(pi_vouchercode,' ')<>' ') b on a.pd_batchcode=b.pd_batchcode and a.pd_prodcode=b.pd_prodcode "
									+ "and a.pd_whcode=b.pd_whcode where a.pd_piid=?  and nvl(b.pi_vouchercode,' ')<>' ') "
									+ "group by pd_id,pd_pdno", pi_id);
			if (rs_checkCostChange.next()) {
				BaseUtil.showError("行" + rs_checkCostChange.getInt("pd_pdno") + "物料批次关联的成本调整单" + rs_checkCostChange.getString("pi_inoutno")
						+ "已制作凭证，请先取消凭证！");
			}
		}
		if ("ProdInOut!AppropriationOut".equals(caller) || "ProdInOut!SaleAppropriationOut".equals(caller)) {
			if (baseDao.isDBSetting(caller, "autoResPostIn")) {
				// 反过账拨入单
				boolean bool = prodInOutDao.resPostSaleProdIn(pi_id);
				if (bool) {
					// 记录操作
					baseDao.logger.others(BaseUtil.getLocalMessage("msg.turnProdIO"), BaseUtil.getLocalMessage("msg.turnSuccess"), caller,
							"pi_id", pi_id);
				} else {
					BaseUtil.showError("反过账拨入单时出现错误.");
				}
			} else {
				SqlRowList rs1 = baseDao.queryForRowSet("select pi_relativeplace,pi_class from prodinout where pi_id=?", pi_id);
				if (rs1.next()) {
					SqlRowList rs2 = baseDao
							.queryForRowSet(
									"select pi_inoutno,pi_class from prodinout where pi_inoutno=? and pi_statuscode='POSTED' and pi_class in ('拨入单','销售拨入单')",
									rs.getString("pi_relativeplace"));
					if (rs2.next()) {
						BaseUtil.showError("请先手工反过账" + rs2.getObject("pi_class") + "[" + rs2.getObject("pi_inoutno") + "].");
					}
				}
			}
		}
		// 反过账前的其它逻辑
		handlerService.beforeResPost(caller, pi_id);
		// 存储过程
		Object[] objs = baseDao.getFieldsDataByCondition("ProdInOut", new String[] { "pi_class", "pi_inoutno" }, "pi_id=" + pi_id);
		String res = baseDao.callProcedure("Sp_UnCommitProdInout", new Object[] { objs[0].toString(), objs[1].toString() });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
		baseDao.updateByCondition("ProdInOut", "pi_statuscode='UNPOST',pi_status='" + BaseUtil.getLocalMessage("UNPOST")
				+ "',pi_inoutman=null,pi_date1=null", "pi_id=" + pi_id);
		// 已经传达到平台了的，反过账后，直接删除平台的单据
		baseDao.updateByCondition("ProdInOut", "pi_sendstatus='上传中'", "pi_id=" + pi_id + " and pi_sendstatus='已上传'");
		if ("ProdInOut!SaleAppropriationOut".equals(caller) || "ProdInOut!AppropriationOut".equals(caller)) {
			baseDao.updateByCondition("ProdInOut", "pi_tocode=null", "pi_id=" + pi_id);
		}
		// 记录操作
		baseDao.logger.resPost(caller, "pi_id", pi_id);
		// 反过账后的其它逻辑
		handlerService.afterResPost(caller, pi_id);
		// 调用取消冲销
		saleClashService.cancelSaleClash(pi_id, "ProdInOut");
		// 委外验收，验退，完工入库单，反过账更新完工状态
		if ("ProdInOut!Make!In".equals(caller) || "ProdInOut!OutsideCheckIn".equals(caller)
				|| "ProdInOut!OutesideCheckReturn".equals(caller)) {
			rs = baseDao.queryForRowSet("select distinct ma_id from  prodiodetail left join make on ma_code=pd_ordercode where pd_piid=?",
					pi_id);
			while (rs.next()) {// 更新完工状态
				makeDao.updateMakeFinishStatus(rs.getInt("ma_id"));
			}
		}
		// 入库单反过账成功后将关联的成本调整单明细行删除，明细删除后单据无明细行的将成本调整单主记录也删除
		if (prodInOutDao.isIn(caller)) {
			SqlRowList rs_delCostChange = baseDao
					.queryForRowSet(
							"select a.pd_piclass pd_piclass,a.pd_inoutno pd_inoutno, b.pi_id pi_id,a.pd_batchcode pd_batchcode,a.pd_prodcode pd_prodcode,"
									+ "a.pd_whcode pd_whcode,nvl(a.pd_price,0) pd_price from  prodiodetail a left join (select pi_id,pd_batchcode,pd_prodcode,pd_whcode,pi_inoutno,pi_vouchercode  "
									+ "	from prodiodetail left join prodinout on pd_piid=pi_id where  pi_class='成本调整单' and pi_statuscode='POSTED'"
									+ "	and nvl(pi_vouchercode,' ')=' ') b on a.pd_batchcode=b.pd_batchcode and a.pd_prodcode=b.pd_prodcode	"
									+ "and a.pd_whcode=b.pd_whcode where a.pd_piid=?   and nvl(b.pi_id,0)>0 and nvl(b.pi_vouchercode,' ')=' '",
							pi_id);
			while (rs_delCostChange.next()) {
				// 更新批记录金额
				baseDao.execute("update batch set ba_total=" + rs_delCostChange.getObject("pd_price") + "*nvl(ba_remain,0) where ba_code='"
						+ rs_delCostChange.getString("pd_batchcode") + "' and " + " ba_prodcode ='"
						+ rs_delCostChange.getString("pd_prodcode") + "' and ba_whcode='" + rs_delCostChange.getString("pd_whcode") + "'");
				// 记录日志
				baseDao.execute("insert into messagelog(ml_id,ml_date,ml_man,ml_content,ML_RESULT,ML_SEARCH,code)"
						+ "select messagelog_seq.nextval,sysdate,'" + SystemSession.getUser().getEm_name() + "','"
						+ rs_delCostChange.getString("pd_piclass") + rs_delCostChange.getString("pd_inoutno")
						+ "反过账删除明细行'||pd_pdno,'删除成功',"
						+ "'ProdInOut!CostChange||pi_id='||pd_piid,pd_inoutno from prodiodetail where pd_piid="
						+ rs_delCostChange.getInt("pi_id") + " and  pd_batchcode='" + rs_delCostChange.getString("pd_batchcode")
						+ "' and pd_prodcode ='" + rs_delCostChange.getString("pd_prodcode") + "'" + " and pd_whcode='"
						+ rs_delCostChange.getString("pd_whcode") + "'");
				// 删除成本调整单明细
				baseDao.deleteByCondition(
						"prodiodetail",
						"pd_piid='" + rs_delCostChange.getInt("pi_id") + "' and " + "pd_batchcode='"
								+ rs_delCostChange.getString("pd_batchcode") + "' and pd_prodcode" + "='"
								+ rs_delCostChange.getString("pd_prodcode") + "' and pd_whcode='" + rs_delCostChange.getString("pd_whcode")
								+ "'");
				// 无明细行的将成本调整单主记录也删除
				baseDao.execute("insert into messagelog(ml_id,ml_date,ml_man,ml_content,ML_RESULT,ML_SEARCH )"
						+ "select messagelog_seq.nextval,sysdate,'" + SystemSession.getUser().getEm_name()
						+ "','反过账删除成本调整单'||PI_INOUTNO,'删除成功'," + "'" + caller + "|pi_id='||" + pi_id + " from prodinout where pi_id="
						+ rs_delCostChange.getInt("pi_id") + " " + "and not exists(select 1 from prodiodetail where pd_piid="
						+ rs_delCostChange.getInt("pi_id") + ")");// 记录日志
				baseDao.deleteByCondition("prodinout", "pi_id=" + rs_delCostChange.getInt("pi_id") + ""
						+ "and not exists(select 1 from prodiodetail where pd_piid=" + rs_delCostChange.getInt("pi_id") + ")");
			}
		}
		if ("ProdInOut!Make!Give".equals(caller) || "ProdInOut!Make!Return".equals(caller) || "ProdInOut!Picking".equals(caller)
				|| "ProdInOut!OSMake!Give".equals(caller) || "ProdInOut!OutsideReturn".equals(caller)
				|| "ProdInOut!OutsidePicking".equals(caller)) {
			changeMaStatus(pi_id);
		}
		/**
		 * @author wsy
		 *         其它入库单：如果选择了采购单号、采购序号后，提交、审核的时候要判断采购单号+采购序号+物料编号是否一致，不一致限制提交
		 *         、审核
		 */
		if ("ProdInOut!OtherIn".equals(caller)) {
			baseDao.execute("update purchasedetail set pd_beipinacceptqty=(nvl((select sum(nvl(pd_purcinqty,0)) from prodiodetail where pd_piclass='其它入库单' and pd_ordercode=pd_code and pd_orderdetno=pd_detno and pd_status=99),0))where (pd_code,pd_detno) in (select pd_ordercode,pd_orderdetno from prodiodetail where pd_piid="
					+ pi_id + " and pd_piclass='其它入库单')");
		}
	}

	void checkVoucher(Object id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pi_vouchercode) from ProdInOut where pi_id=? and nvl(pi_vouchercode,' ') <>' ' and pi_vouchercode<>'UNNEED'",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("已有凭证，不允许进行当前操作!凭证编号：" + dets);
		}
	}

	/**
	 * yaozx 13-07-16 检查重置批号是否显示
	 */
	@Override
	public boolean checkresetBatchCode(String caller) {
		Object object = baseDao.getFieldDataByCondition("documentsetup", "ds_inorout", "ds_table='" + caller + "'");
		if (object != null && ("OUT".equals(object) || "-IN".equals(object))) {
			return true;
		}
		return false;
	}

	/**
	 * yaozx 13-07-16 重置批号
	 */
	@Override
	public void resetBatchCode(String caller, int pi_id) {
		try {
			baseDao.execute("select pi_id from prodinout where pi_id=? for update wait 3", pi_id);
		} catch (Exception e) {
			BaseUtil.showError("当前单据有其他人正在操作，请等待3秒后再试");
		}
		String status = baseDao.getJdbcTemplate().queryForObject("SELECT pi_statuscode FROM ProdInOut WHERE pi_id=?", String.class, pi_id);
		if ("POSTED".equals(status)) {
			BaseUtil.showError("单据已过账，不允许重置批号!");
		} else {
			// 过账异常的单据
			boolean errPosted = baseDao.checkIf("ProdIODetail", "pd_piid=" + pi_id + " AND pd_status=99");
			if (errPosted) {
				BaseUtil.showError("单据异常，请联系管理员处理!");
			}
			SqlRowList rs = baseDao.queryForRowSet("select count(1) cn from barcodeio where bi_piid=? and bi_outqty>0", pi_id);
			if (rs.next() && rs.getInt("cn") > 0) {
				BaseUtil.showError("已有出库条码采集不允许重置批号！");
			}
			handlerService.handler(caller, "resetBatch", "before", new Object[] { pi_id });
			baseDao.updateByCondition("ProdIODetail", "pd_batchcode=null,pd_batchid=0", "nvl(pd_status,0)=0 and pd_piid=" + pi_id);
			baseDao.execute("update prodinout set pi_pdastatus='' where pi_id=? and nvl(pi_pdastatus,' ')<>' '", pi_id);
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "重置批号", "重置批号", caller + "|pi_id=" + pi_id));
		}
	}

	@Override
	public String turnExOut(String caller, int pi_id) {
		// 判断是否有转过换货出库单
		Object[] exOut = baseDao.getFieldsDataByCondition("ProdInOut", "pi_id,pi_inoutno",
				"pi_class='换货出库单' AND pi_relativeplace=(SELECT pi_inoutno FROM ProdInOut WHERE pi_id=" + pi_id + ")");
		if (exOut != null) {
			return "该单据已经转过换货出库单,单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + exOut[0]
					+ "&gridCondition=pd_piidIS" + exOut[0] + "&whoami=ProdInOut!ExchangeOut')\">" + exOut[1] + "</a>&nbsp;";
		}
		Map<String, Object> dif = new HashMap<String, Object>();
		// Copy
		int nId = baseDao.getSeqId("PRODINOUT_SEQ");
		dif.put("pi_id", nId);
		String code = baseDao.sGetMaxNumber("ProdInOut!ExchangeOut", 2);
		dif.put("pi_class", "'换货出库单'");
		dif.put("pi_inoutno", "'" + code + "'");
		dif.put("pi_relativeplace", "rs.pi_inoutno");
		dif.put("pi_recordman", "'" + SystemSession.getUser().getEm_name() + "'");
		dif.put("pi_invostatus", "'" + BaseUtil.getLocalMessage("ENTERING") + "'");
		dif.put("pi_invostatuscode", "'ENTERING'");
		dif.put("pi_status", "'" + BaseUtil.getLocalMessage("UNPOST") + "'");
		dif.put("pi_statuscode", "'UNPOST'");
		dif.put("pi_inoutman", "null");
		dif.put("pi_vouchercode", "null");
		dif.put("pi_date1", "null");
		dif.put("pi_printstatus", "'" + BaseUtil.getLocalMessage("UNPRINT") + "'");
		dif.put("pi_printstatuscode", "'UNPRINT'");
		dif.put("pi_recorddate", "sysdate");
		baseDao.copyRecord("ProdInOut", "ProdInOut", "pi_id=" + pi_id, dif);
		dif = new HashMap<String, Object>();
		dif.put("pd_id", "ProdIODetail_seq.nextval");
		dif.put("pd_piid", nId);
		dif.put("pd_piclass", "'换货出库单'");
		dif.put("pd_inoutno", "'" + code + "'");
		dif.put("pd_status", "0");
		dif.put("pd_outqty", "rs.pd_inqty");
		dif.put("pd_inqty", "0");
		dif.put("pd_batchcode", "null");
		dif.put("pd_batchid", "0");
		baseDao.copyRecord("ProdIODetail", "ProdIODetail", "pd_piid=" + pi_id, dif);
		return "转入成功,换货出库单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + nId
				+ "&gridCondition=pd_piidIS" + nId + "&whoami=ProdInOut!ExchangeOut')\">" + code + "</a>&nbsp;";
	}

	@Override
	public String turnProdinoutIn(int pi_id, String caller) {
		Object pi_tocode = baseDao.getFieldDataByCondition("prodinout", "pi_tocode", "pi_id=" + pi_id);
		if (pi_tocode != null) {
			// 查看单据有没有删除
			boolean bool = baseDao.checkIf("prodinout", "pi_inoutno='" + pi_tocode + "' and pi_class='其它入库单'");
			if (bool)
				return "该出库单已转入库单,不能重复转入库单！其他入库单号:" + pi_tocode;
		}
		Object[] object = baseDao.getFieldsDataByCondition("prodinout", new String[] { "pi_recordman", "pi_inoutno", "pi_departmentcode",
				"pi_departmentname", "pi_whcode", "pi_whname", "pi_emcode", "pi_emname" }, "pi_id=" + pi_id);
		Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
		int id = baseDao.getSeqId("prodinout_SEQ");
		int pddetno = 1;
		String code = baseDao.sGetMaxNumber("ProdInOut!OtherIn", 2);
		String insertSql = "insert into ProdInOut(pi_inoutno,pi_class,pi_date,pi_status,pi_statuscode,"
				+ "pi_departmentcode,pi_departmentname,pi_recorddate,pi_recordman,pi_invostatus,pi_invostatuscode,pi_remark,pi_id,pi_whcode,pi_whname,pi_emcode,pi_emname)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		String insertDetail = "insert into ProdIODetail(pd_id,pd_piid,pd_pdno,pd_prodcode,pd_inqty,pd_whcode,pd_batchcode,pd_price)values(?,?,?,?,?,?,?,?)";
		baseDao.execute(insertSql, new Object[] { code, "其它入库单", time, "未过账", "UNPOST", object[2], object[3], time, object[0], "在录入",
				"ENTERING", object[1], id, object[4], object[5], object[6], object[7] });
		List<Object[]> list = baseDao.getFieldsDatasByCondition("ProdIODetail", new String[] { "pd_prodcode", "pd_outqty", "pd_whcode",
				"pd_batchcode", "pd_price" }, "pd_piid=" + pi_id);
		for (Object[] objects : list) {
			baseDao.execute(insertDetail, new Object[] { baseDao.getSeqId("ProdIODetail_SEQ"), id, pddetno++, objects[0], objects[1],
					objects[2], objects[3], Float.parseFloat(objects[4].toString()) });
		}
		// 更新其它入库单号
		baseDao.updateByCondition("prodinout", "pi_tocode='" + code + "'", "pi_id=" + pi_id);
		return "转入成功,其他入库单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + id
				+ "&gridCondition=pd_piidIS" + id + "&whoami=ProdInOut!OtherIn')\">" + code + "</a>&nbsp;";
	}

	@Override
	public void updatepdPrice(int pi_id, String caller) {
		if (baseDao.isDBSetting("ProdInOut!PurcCheckin", "feeCharge")
				|| baseDao.isDBSetting("ProdInOut!PurcCheckin", "feeCharge_updateAll")) {
			if (baseDao.isDBSetting("ProdInOut!PurcCheckin", "feeCharge")) {// 华商龙成本单价不为0时不重新计算
				baseDao.execute(
						"update prodiodetail set pd_price=(select round(price+price*amount/total,8) from (select pd_id,pd_orderprice,(nvl(pd_orderprice,0)*nvl(pi_rate,0)/(1+nvl(pd_taxrate,0)/100)) price,(select sum((nvl(pd_inqty,0)+nvl(pd_outqty,0))*round(nvl(pd_orderprice,0)*nvl(pi_rate,0)/(1+nvl(pd_taxrate,0)/100),8)) from ProdIODetail pp1 left join ProdInOut p1 on pp1.pd_piid=p1.pi_id where p1.pi_id=prodiodetail.pd_piid) total,nvl((select sum(pd_rate*pd_amount) from ProdChargeDetail A where A.pd_piid=prodiodetail.pd_piid),0) amount from ProdIODetail left join ProdInOut on pd_piid=pi_id where pd_piid=?) B where B.pd_id=prodiodetail.pd_id and nvl(total,0)<>0) where pd_piid=? and pd_piclass in ('采购验收单','采购验退单') and nvl(pd_price,0)=0",
						pi_id, pi_id);
			} else {// 嘉豪采购单价会改 成本单价不为0时也要重新计算
				baseDao.execute(
						"update prodiodetail set pd_price=(select round(price+price*amount/total,8) from (select pd_id,pd_orderprice,(nvl(pd_orderprice,0)*nvl(pi_rate,0)/(1+nvl(pd_taxrate,0)/100)) price,(select sum((nvl(pd_inqty,0)+nvl(pd_outqty,0))*round(nvl(pd_orderprice,0)*nvl(pi_rate,0)/(1+nvl(pd_taxrate,0)/100),8)) from ProdIODetail pp1 left join ProdInOut p1 on pp1.pd_piid=p1.pi_id where p1.pi_id=prodiodetail.pd_piid) total,nvl((select sum(pd_rate*pd_amount) from ProdChargeDetail A where A.pd_piid=prodiodetail.pd_piid),0) amount from ProdIODetail left join ProdInOut on pd_piid=pi_id where pd_piid=?) B where B.pd_id=prodiodetail.pd_id and nvl(total,0)<>0) where pd_piid=? and pd_piclass in ('采购验收单','采购验退单') ",
						pi_id, pi_id);
			}

			baseDao.execute(
					"update prodiodetail set pd_total=round(nvl(pd_price,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2) where pd_piid=? and pd_piclass in ('采购验收单','采购验退单')",
					pi_id);
			baseDao.execute(
					"update prodiodetail set pd_bbamount=round(nvl(pd_orderprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0))*(select nvl(pi_rate,0) from prodinout where pi_id=pd_piid),2) where pd_piid=? and pd_piclass in ('采购验收单','采购验退单')",
					pi_id);
			baseDao.execute(
					"update prodiodetail set pd_cyamount=round(nvl(pd_price,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2)-nvl(PD_BBAMOUNT,0) where pd_piid=? and pd_piclass in ('采购验收单','采购验退单')",
					pi_id);
		} else if (baseDao.isDBSetting("ProdInOut!PurcCheckin", "allowUpdatetRate")) {
			// 计算成本单价、成本金额
			baseDao.execute(
					"update prodiodetail set pd_price=(select round(price+price*amount/total,8) from (select pd_id,pd_orderprice,(nvl(pd_orderprice,0)*nvl(pi_rate,0)/(1+nvl(pd_taxrate,0)/100)) price,(select sum((nvl(pd_inqty,0)+nvl(pd_outqty,0))*round(nvl(pd_orderprice,0)*nvl(pi_rate,0)/(1+nvl(pd_taxrate,0)/100),8)) from ProdIODetail pp1 left join ProdInOut p1 on pp1.pd_piid=p1.pi_id where p1.pi_id=prodiodetail.pd_piid) total,nvl((select sum(pd_rate*pd_amount) from ProdChargeDetail A where A.pd_piid=prodiodetail.pd_piid),0) amount from ProdIODetail left join ProdInOut on pd_piid=pi_id where pd_piid=?) B where B.pd_id=prodiodetail.pd_id and nvl(total,0)<>0) where pd_piid=? and pd_piclass in ('采购验收单','采购验退单')",
					pi_id, pi_id);
			baseDao.execute(
					"update prodiodetail set pd_total=round(nvl(pd_price,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2) where pd_piid=? and pd_piclass in('采购验收单','采购验退单')",
					pi_id);
		}
	}

	@Override
	public String turnTurnProdinoutReturn(String caller, int pi_id) {
		// 有已结案的订单就不允许转
		String finished = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(pd_ordercode||'行'||pd_orderdetno) from prodiodetail where pd_piid=? and exists(select 1 from saledetail where sd_code=pd_ordercode and sd_detno=pd_orderdetno and sd_statuscode='FINISH')",
						String.class, pi_id);
		if (finished != null)
			return "有已结案的订单 " + finished;
		baseDao.execute(
				"update prodiodetail set pd_whid=nvl((select wh_id from warehouse where wh_code=pd_whcode),0) where pd_piid=? and nvl(pd_whcode,' ')<>' '",
				pi_id);
		SqlRowList list = baseDao.queryForRowSet("select * from prodinout where pi_id=?", pi_id);
		SqlMap map = null;
		String code = baseDao.sGetMaxNumber("ProdInOut!SaleReturn", 2);
		int id = baseDao.getSeqId("prodinout_SEQ");
		while (list.next()) {
			map = new SqlMap("prodinout");
			map.set("pi_class", "销售退货单");
			map.set("pi_cgycode", list.getObject("pi_cgycode"));
			map.set("pi_payment", list.getObject("pi_payment"));
			map.set("pi_paymentcode", list.getObject("pi_paymentcode"));
			map.set("pi_remark2", list.getObject("pi_remark2"));
			map.set("pi_packingremark", list.getObject("pi_packingremark"));
			map.set("pi_invoiceremark", list.getObject("pi_invoiceremark"));
			map.set("pi_custname2", list.getObject("pi_custname2"));
			map.set("pi_custcode2", list.getObject("pi_custcode2"));
			map.set("pi_arcode", list.getObject("pi_arcode"));
			map.set("pi_arname", list.getObject("pi_arname"));
			map.set("pi_rate", list.getObject("pi_rate"));
			map.set("pi_currency", list.getObject("pi_currency"));
			map.set("pi_recorddate", Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)));
			map.set("pi_id", id);
			map.set("pi_whcode", list.getObject("pi_whcode"));
			map.set("pi_cardid", list.getObject("pi_cardid"));
			map.set("pi_recordman", SystemSession.getUser().getEm_name());
			map.set("pi_type", list.getObject("pi_type"));
			map.set("pi_transport", list.getObject("pi_transport"));
			map.set("pi_sellercode", list.getObject("pi_sellercode"));
			map.set("pi_sellername", list.getObject("pi_sellername"));
			// 现在退货单配置里面的业务员名称都是用的pi_belongs
			map.set("pi_belongs", list.getObject("pi_sellername"));
			map.set("pi_title", list.getObject("pi_title"));
			map.set("pi_cardcode", list.getObject("pi_cardcode"));
			map.set("pi_whname", list.getObject("pi_whname"));
			map.set("pi_inoutno", code);
			map.set("pi_cgy", list.getObject("pi_cgy"));
			map.set("pi_date", Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)));
			map.set("pi_invostatuscode", "ENTERING");
			map.set("pi_invostatus", "在录入");
			map.set("pi_statuscode", "UNPOST");
			map.set("pi_sendcode", list.getString("pi_inoutno"));
			map.set("pi_departmentcode", list.getObject("pi_departmentcode"));
			map.set("pi_departmentname", list.getObject("pi_departmentname"));
			map.execute();
		}
		list = baseDao.queryForRowSet("select * from ProdIODetail where pd_piid=?", pi_id);
		int detno = 1;
		while (list.next()) {
			map = new SqlMap("ProdIODetail");
			map.set("pd_pdno", detno++);
			map.set("pd_piid", id);
			map.set("pd_whcode", list.getObject("pd_whcode"));
			map.set("pd_whname", list.getObject("pd_whname"));
			map.set("pd_price", list.getObject("pd_price"));
			map.set("pd_total", list.getObject("pd_total"));
			map.set("pd_taxrate", list.getObject("pd_taxrate"));
			map.set("pd_piclass", list.getObject("pd_piclass"));
			map.set("pd_ordercode", list.getObject("pd_ordercode"));
			map.set("pd_orderdetno", list.getObject("pd_orderdetno"));
			map.set("pd_custprodcode", list.getObject("pd_custprodcode"));
			map.set("pd_prodcode", list.getObject("pd_prodcode"));
			map.set("pd_inqty", list.getObject("pd_outqty"));
			map.set("pd_whid", list.getObject("pd_whid"));
			map.set("pd_sendprice", list.getObject("pd_sendprice"));
			map.set("pd_id", baseDao.getSeqId("PRODIODETAIL_SEQ"));
			map.set("pd_discount", list.getObject("pd_discount"));
			map.execute();
		}
		return "转入成功,退货单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + id
				+ "&gridCondition=pd_piidIS" + id + "&whoami=ProdInOut!SaleReturn')\">" + code + "</a>&nbsp;<hr>";
	}

	@Override
	public void updatepdscaleremark(int pi_id, String field, String data) {
		baseDao.updateByCondition("ProdIODetail", "pd_scaleremark='" + data + "'", field + "=" + pi_id);
	}

	@Override
	public void updateProdInOutOtherInRemark(int pi_id, String remark, String caller) {
		baseDao.updateByCondition("ProdInOut", "pi_remark='" + remark + "'", "pi_id=" + pi_id);
	}

	@Override
	// @Transactional
	public String split(String caller, int pi_id, String piclass) {
		String newInoutno = null;
		int piid = 0;
		int detno = 0;
		if (baseDao.isDBSetting("sys", "splitWithhold")) {
			int num = baseDao
					.getCount("select count(1) from productwh where (pw_prodcode,pw_whcode) in (select pd_prodcode,pd_whcode from prodiodetail where pd_piid="
							+ pi_id
							+ " ) "
							+ "and nvl(pw_onhand,0)-nvl((select sum(nvl(pd_outqty,0)) from prodiodetail left join prodinout on pd_piid=pi_id where pd_piid<>"
							+ pi_id
							+ ""
							+ " and pd_status<99 and nvl(pi_invostatuscode,' ')<>'ENTERING' and pd_whcode=pw_whcode and pd_prodcode=pw_prodcode ),0)>0");
			if (num <= 0) {
				BaseUtil.showError("扣除预扣库存后无库存不能拆分！");
			}
		} else {
			int num = baseDao
					.getCount("select count(*) from ProdIODetail,ProductWh where pd_whcode=pw_whcode and pd_prodcode=pw_prodcode and pd_piid="
							+ pi_id + " and nvl(pw_onhand,0) > 0");
			if (num <= 0) {
				BaseUtil.showError("无库存不能拆分！");
			}
		}
		// 根据物料分组汇总需要出库的数量
		SqlRowList rs = baseDao
				.queryForRowSet("select pd_inoutno,pd_whcode,pd_prodcode,sum(pd_outqty) qty from ProdIODetail WHERE pd_piid=" + pi_id
						+ " and pd_piclass='" + piclass + "' group by pd_inoutno,pd_prodcode,pd_whcode");
		while (rs.next()) {
			// 获取每个物料在该仓库的库存数
			SqlRowList rs2 = baseDao.queryForRowSet("SELECT nvl(sum(ba_remain),0) remain from Batch where ba_whcode=? and ba_prodcode=?",
					rs.getObject("pd_whcode"), rs.getObject("pd_prodcode"));
			if (rs2.next()) {
				Double remain = rs2.getDouble("remain");
				if (baseDao.isDBSetting("sys", "splitWithhold")) {
					SqlRowList rs_outqty = baseDao
							.queryForRowSet(
									"SELECT nvl(sum(pd_outqty),0) sumoutqty from prodiodetail left join prodinout on pd_piid=pi_id"
											+ " where pd_piid<>? and pd_status<99 and nvl(pi_invostatuscode,' ')<>'ENTERING' and pd_whcode=? and pd_prodcode=?",
									pi_id, rs.getObject("pd_whcode"), rs.getObject("pd_prodcode"));
					if (rs_outqty.next()) {
						remain = remain - rs_outqty.getGeneralDouble("sumoutqty");
					}
				}
				String inoutno = rs.getString("pd_inoutno");
				String tcode = null;
				if (rs.getDouble("qty") > remain) {// 出库数量大于库存数，把多出数量拆到新出入库单
					if (newInoutno == null) {
						for (int i = 1; i < 100; i++) {// 循环获取新的单号，原单号基础上+“-”几
							if (inoutno.indexOf("-") > -1) {
								tcode = inoutno.substring(0, inoutno.indexOf("-") + 1) + i;
							} else {
								tcode = inoutno + "-" + i;
							}
							int count = baseDao.getCountByCondition("ProdIODetail ", "pd_inoutno='" + tcode + "' and pd_piclass='"
									+ piclass + "'");
							if (count == 0) {
								newInoutno = tcode;
								break;
							}
						}
						// 产生新出入库单
						Map<String, Object> diffence = new HashMap<String, Object>();
						piid = baseDao.getSeqId("PRODINOUT_SEQ");
						diffence.put("pi_id", piid);
						diffence.put("pi_inoutno", "'" + newInoutno + "'");
						baseDao.copyRecord("ProdInOut", "ProdInOut", "pi_id=" + pi_id, diffence);
					}
					// 拆分库存不够的物料到新的出入库单
					SqlRowList rs3 = baseDao.queryForRowSet(
							"SELECT * from ProdIODetail where pd_whcode=? and pd_prodcode=? and pd_piid=? order by pd_pdno",
							rs.getObject("pd_whcode"), rs.getObject("pd_prodcode"), pi_id);
					Double useqty = 0.0;
					while (rs3.next()) {
						useqty = useqty + rs3.getGeneralDouble("pd_outqty");
						if (useqty > remain && remain != 0) {
							Map<String, Object> diffence = new HashMap<String, Object>();
							int pdid = baseDao.getSeqId("PRODIODETAIL_SEQ");
							diffence.put("pd_id", pdid);
							diffence.put("pd_piid", piid);
							diffence.put("pd_inoutno", "'" + newInoutno + "'");
							diffence.put("pd_outqty", (useqty - remain));
							diffence.put("pd_pdno", ++detno);
							baseDao.copyRecord("ProdIODetail", "ProdIODetail", "pd_id=" + rs3.getGeneralInt("pd_id"), diffence);
							baseDao.execute("update ProdIODetail set pd_outqty=" + (remain - useqty + rs3.getGeneralDouble("pd_outqty"))
									+ " where pd_id=" + rs3.getGeneralInt("pd_id"));
							remain = 0.0;
						} else if (useqty - remain == 0 && remain != 0) {
							remain = 0.0;
						} else if (useqty > remain && remain == 0) {
							baseDao.execute("update ProdIODetail set pd_piid=" + piid + ",pd_inoutno='" + newInoutno + "',pd_pdno="
									+ (++detno) + " where pd_id=" + rs3.getGeneralInt("pd_id"));
						}
					}
				}
			}
		}
		if (newInoutno != null) {
			getTotal(pi_id, caller);
			getTotal(piid, caller);
			baseDao.logger.others("单据拆分", "拆分成功", caller, "pi_id", pi_id);
			return "成功将库存数量不够数据产生到新单据中,单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=" + "pi_idIS"
					+ piid + "&gridCondition=pd_piidIS" + piid + "&whoami=" + caller + "')\">" + newInoutno + "</a>&nbsp;<hr>";

		} else {
			BaseUtil.showError("库存数量可满足当前单据出库，不需要拆分");
		}
		return null;
	}

	static final String INSERTPRODIODETAILBAR = "INSERT INTO ProdIODetailBar(pdb_id,pdb_pdid,pdb_inoutno,pdb_pdno,pdb_detno,"
			+ "pdb_qty,pdb_vendcode,pdb_vendname,pdb_prodcode,pdb_batchcode,pdb_piclass)" + " values (?,?,?,?,?,?,?,?,?,?,?)";

	@Override
	public String Subpackage(int pi_id) {
		int barNum = 0;
		double packageqty = 0;
		double pdqty = 0;
		double remainQty = 0;
		double tqty = 0;
		Object picode = baseDao.getFieldDataByCondition("ProdInOut", "pi_inoutno", "pi_id=" + pi_id);
		int count = baseDao.getCountByCondition("ProdIODetailBar", "pdb_inoutno='" + picode + "'");
		if (count > 0) {
			BaseUtil.showError("已经有过分装明细,如果需要重新分装请通过[清除分装明细]按钮先清除后再进行分装!");
		}
		SqlRowList rs = baseDao
				.queryForRowSet(
						"SELECT pd_inoutno,pi_cardcode,pi_title,pd_id,pd_pdno,pd_inqty,pd_unitpackage,pd_batchcode,pd_prodcode,pd_piclass FROM ProdIODetail left join ProdInOut on pd_piid=pi_id where pd_piid=?",
						pi_id);
		while (rs.next()) {
			packageqty = rs.getDouble("pd_unitpackage");
			pdqty = rs.getDouble("pd_inqty");
			if (packageqty > 0 && pdqty > 0) {
				barNum = (int) (Math.ceil(pdqty / packageqty));
				remainQty = pdqty;
				for (int i = 1; i <= barNum; i++) {
					if (remainQty >= packageqty) {
						tqty = packageqty;
					} else {
						tqty = remainQty;
					}
					baseDao.execute(
							INSERTPRODIODETAILBAR,
							new Object[] { baseDao.getSeqId("PRODIODETAILBAR_SEQ"), rs.getInt("pd_id"), rs.getObject("pd_inoutno"),
									rs.getInt("pd_pdno"), i, tqty, rs.getObject("pi_cardcode"), rs.getObject("pi_title"),
									rs.getObject("pd_prodcode"), rs.getObject("pd_batchcode"), rs.getObject("pd_piclass") });
					remainQty = remainQty - tqty;
					if (remainQty <= 0) {
						break;
					}
				}
			}
		}
		return "分装确认成功!";
	}

	@Override
	public String ClearSubpackage(int pi_id) {
		Object picode = baseDao.getFieldDataByCondition("ProdInOut", "pi_inoutno", "pi_id=" + pi_id);
		baseDao.execute("delete from ProdIODetailBar where pdb_inoutno='" + picode + "'");
		return "清除分装明细成功!";
	}

	@Override
	public String[] printBar(int pi_id, String reportName, String condition, String caller) {
		double vadpsumqty = 0;
		SqlRowList rs = baseDao
				.queryForRowSet(
						"SELECT pd_id, pd_inoutno,pd_pdno,round(pd_inqty,2),pd_unitpackage FROM ProdIODetail left join ProdInOut on pd_piid=pi_id where pd_piid=?",
						pi_id);
		while (rs.next()) {
			vadpsumqty = Double.parseDouble(baseDao.getFieldDataByCondition("ProdIODetailBar", "round(sum(pdb_qty),2)",
					"pdb_pdid=" + rs.getInt("pd_id")).toString());
			if (rs.getDouble(4) != vadpsumqty) {
				BaseUtil.showError("当前序号" + rs.getObject("pd_pdno") + "的入库数量与分装明细总数不等,不能打印条码!");
			}
		}
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "条码打印", "条码打印整单", caller + "|pi_id=" + pi_id));
		return keys;
	}

	@Override
	public String SubpackageDetail(int pd_id, double qty) {
		int barNum = 0;
		double pdqty = 0;
		double remainQty = 0;
		double tqty = 0;
		Object status = baseDao.getFieldDataByCondition("ProdInOut left join ProdIODetail on pi_id=pd_piid", "pi_invostatuscode", "pd_id="
				+ pd_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError("只能对已审核的单进行分装确认!");
		}
		int count = baseDao.getCountByCondition("ProdIODetailBar", "pdb_pdid=" + pd_id);
		if (count > 0) {
			BaseUtil.showError("已经有过分装明细,如果需要重新分装请通过[清除分装明细]按钮先清除后再进行分装!");
		}
		baseDao.execute("update ProdIODetail set pd_unitpackage=? where pd_id=?", qty, pd_id);
		SqlRowList rs = baseDao
				.queryForRowSet(
						"SELECT pd_inoutno,pi_cardcode,pi_title,pd_id,pd_pdno,pd_inqty,pd_unitpackage,pd_batchcode,pd_prodcode,pd_piclass FROM ProdIODetail left join ProdInOut on pd_piid=pi_id where pd_id=?",
						pd_id);
		if (rs.next()) {
			pdqty = rs.getDouble("pd_inqty");
			if (qty > 0 && pdqty > 0) {
				barNum = (int) (Math.ceil(pdqty / qty));
				remainQty = pdqty;
				for (int i = 1; i <= barNum; i++) {
					if (remainQty >= qty) {
						tqty = qty;
					} else {
						tqty = remainQty;
					}
					baseDao.execute(
							INSERTPRODIODETAILBAR,
							new Object[] { baseDao.getSeqId("PRODIODETAILBAR_SEQ"), rs.getInt("pd_id"), rs.getObject("pd_inoutno"),
									rs.getInt("pd_pdno"), i, tqty, rs.getObject("pi_cardcode"), rs.getObject("pi_title"),
									rs.getObject("pd_prodcode"), rs.getObject("pd_batchcode"), rs.getObject("pd_piclass") });
					remainQty = remainQty - tqty;
					if (remainQty <= 0) {
						break;
					}
				}
			}
		}
		return "分装确认成功!";
	}

	@Override
	public String ClearSubpackageDetail(int pd_id) {
		baseDao.execute("delete from ProdIODetailBar where pdb_pdid=" + pd_id);
		return "清除分装明细成功!";
	}

	@Override
	public String[] PrintBarDetail(int pd_id, String reportName, String condition) {
		double vadpsumqty = 0;
		SqlRowList rs = baseDao
				.queryForRowSet(
						"SELECT pd_inoutno,pd_pdno,round(pd_inqty,2),pd_unitpackage,pd_piclass,pd_piid FROM ProdIODetail left join ProdInOut on pd_piid=pi_id where pd_id=?",
						pd_id);
		if (rs.next()) {
			String piclass = rs.getGeneralString("pd_piclass");
			vadpsumqty = Double.parseDouble(baseDao
					.getFieldDataByCondition("ProdIODetailBar", "round(sum(pdb_qty),2)", "pdb_pdid=" + pd_id).toString());
			if (rs.getDouble(3) != vadpsumqty) {
				BaseUtil.showError("当前序号" + rs.getObject("pd_pdno") + "的入库数量与分装明细总数不等,不能打印条码!");
			}
			String caller = null;
			if ("其它采购入库单".equals(piclass)) {
				caller = "ProdInOut!OtherPurcIn";
			} else if ("其它入库单".equals(piclass)) {
				caller = "ProdInOut!OtherIn";
			}
			// 执行打印操作
			String key = "12345678";
			String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "条码打印", "条码打印明细,行号：" + rs.getGeneralInt("pd_pdno"),
					caller + "|pi_id=" + rs.getGeneralInt("pd_piid")));
			return keys;
		}
		return null;
	}

	@Override
	public void catchBatch(String caller, int id) {
		Object[] o = baseDao.getFieldsDataByCondition("ProdInOut", new String[] { "pi_statuscode", "pi_class", "pi_inoutno" }, "pi_id="
				+ id);
		if (o.length == 3) {
			if (o[0].equals("POSTED")) {
				BaseUtil.showError("已过账的单据不能抓取批号");
			}
			String type = baseDao.getDBSetting("BarCodeSetting", "ProdOutType");
			// 【BUG】【反馈编号:2017020630】【生产领料单】【修改了对于空数据的判断方式】
			if ("byBatch".equals(type) || "byProdcode".equals(type)) {
				int cn = baseDao.getCount("select count(1) from barcodeio where bi_piid=" + id);
				if (cn > 0) {
					BaseUtil.showError("已有采集的条码，不允许抓取批号，如需操作请撤销已采集的数据");
				}
			}
			String res = baseDao.callProcedure("SP_SPLITPRODOUT",
					new Object[] { o[1].toString(), o[2].toString(), String.valueOf(SystemSession.getUser().getEm_name()) });
			if (res != null && !res.trim().equals("")) {
				BaseUtil.showError(res);
			}
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "抓取批号", "抓取批号", caller + "|pi_id=" + id));
			// 出库类型单据更新出库单备料状态
			updatePdaStatus(caller, id);
		}
	}

	/**
	 * 计算可用量
	 */
	@Override
	public void loadOnHandQty(int id) {
		SqlRowList rs = baseDao.queryForRowSet("SELECT pd_prodcode FROM ProdIODetail WHERE pd_piid=?", id);
		Object onhand = 0;
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			onhand = baseDao.getFieldDataByCondition("io_pwonhand_view", "pw_onhand", "pw_prodcode='" + rs.getString(1) + "'");
			if (onhand == null || Double.parseDouble(onhand.toString()) == 0) {
				sb.append("<br>物料:" + rs.getString(1) + "的良品库存为0!");
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showErrorOnSuccess(sb.toString());
		}
	}

	/**
	 * 更新工单用料表需求数和领料单本次出库数量
	 */
	@Override
	public void SetMMQTY(int pi_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("ProdInOut", "pi_statuscode", "pi_id=" + pi_id);
		if (status.equals("POSTED")) {
			BaseUtil.showError("单据已过账，禁止操作!");
		}
		SqlRowList rs = baseDao.queryForRowSet("SELECT count(1) n from prodiodetail where pd_piid=? and pd_tqty>0", pi_id);
		if (rs.next()) {
			if (rs.getInt("n") == 0) {
				BaseUtil.showError("必须先维护实际可发数!");
			}
		}
		rs = baseDao
				.queryForRowSet(
						"SELECT * from (SELECT pd_ordercode,pd_orderdetno,sum(case when pd_tqty>0 then pd_tqty else pd_outqty end) thisqty,max(mm_id)mmid,max(mm_qty) mmqty,max(NVL(mm_havegetqty,0)+NVL(mm_returnmqty,0)-NVL(mm_addqty,0))getqty,max(mm_oneuseqty)oneuseqty,max(ma_qty)maqty from prodiodetail left join make on pd_ordercode=ma_code left join makematerial on ma_id=mm_maid and mm_detno=pd_orderdetno where pd_piid=? and mm_id>0 group by pd_ordercode,pd_orderdetno) where round(getqty+thisqty,4)>round(mmqty,4)",
						pi_id);
		while (rs.next()) {
			double newmmqty = rs.getDouble("getqty") + rs.getDouble("thisqty");
			double balance = newmmqty - rs.getDouble("oneuseqty") * rs.getDouble("maqty");
			double mmqty = rs.getDouble("mmqty");
			balance = balance > 0 ? balance : 0;
			// 更新工单的需求数和备损数
			baseDao.execute("update makematerial set mm_qty=" + newmmqty + ",mm_netqty=case when mm_netqty>0 then mm_netqty else " + mmqty
					+ " end,mm_balance=" + balance + " where mm_id=?", rs.getInt("mmid"));

		}
		// 更新领料单出库数量为实际可发数量pd_yqty
		baseDao.execute("update prodiodetail set pd_outqty=pd_tqty where pd_piid=? and pd_tqty>0 and pd_status=0", pi_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "维护实际可发数", "维护实际可发数", caller + "|pi_id=" + pi_id));
	}

	/**
	 * 出货单转销售退货
	 */
	@Transactional
	@Override
	public String turnTurnProdinoutReturnnew(String caller, String data) {
		List<Map<Object, Object>> gStore = BaseUtil.parseGridStoreToMaps(data);
		Object pi_id = gStore.get(0).get("pd_piid");
		// 有已结案的订单就不允许转
		String finished = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(pd_ordercode||'行'||pd_orderdetno) from prodiodetail where pd_piid=? and exists(select 1 from saledetail where sd_code=pd_ordercode and sd_detno=pd_orderdetno and sd_statuscode='FINISH')",
						String.class, pi_id);
		if (finished != null)
			return "有已结案的订单 " + finished;
		baseDao.execute(
				"update prodiodetail set pd_whid=nvl((select wh_id from warehouse where wh_code=pd_whcode),0) where pd_piid=? and nvl(pd_whcode,' ')<>' '",
				pi_id);
		SqlRowList list = baseDao.queryForRowSet("select * from prodinout where pi_id=?", pi_id);
		String code = baseDao.sGetMaxNumber("ProdInOut!SaleReturn", 2);
		int id = baseDao.getSeqId("prodinout_SEQ");
		while (list.next()) {
			SqlMap map = new SqlMap("prodinout");
			map.set("pi_class", "销售退货单");
			map.set("pi_cgycode", list.getObject("pi_cgycode"));
			map.set("pi_payment", list.getObject("pi_payment"));
			map.set("pi_paymentcode", list.getObject("pi_paymentcode"));
			map.set("pi_remark2", list.getObject("pi_remark2"));
			map.set("pi_packingremark", list.getObject("pi_packingremark"));
			map.set("pi_invoiceremark", list.getObject("pi_invoiceremark"));
			map.set("pi_custname2", list.getObject("pi_custname2"));
			map.set("pi_custcode2", list.getObject("pi_custcode2"));
			map.set("pi_arcode", list.getObject("pi_arcode"));
			map.set("pi_arname", list.getObject("pi_arname"));
			map.set("pi_rate", list.getObject("pi_rate"));
			map.set("pi_currency", list.getObject("pi_currency"));
			map.set("pi_recorddate", Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)));
			map.set("pi_id", id);
			map.set("pi_whcode", list.getObject("pi_whcode"));
			map.set("pi_cardid", list.getObject("pi_cardid"));
			map.set("pi_recordman", SystemSession.getUser().getEm_name());
			map.set("pi_type", list.getObject("pi_type"));
			map.set("pi_transport", list.getObject("pi_transport"));
			map.set("pi_sellercode", list.getObject("pi_sellercode"));
			map.set("pi_sellername", list.getObject("pi_sellername"));
			// 现在退货单配置里面的业务员名称都是用的pi_belongs
			map.set("pi_belongs", list.getObject("pi_sellername"));
			map.set("pi_title", list.getObject("pi_title"));
			map.set("pi_cardcode", list.getObject("pi_cardcode"));
			map.set("pi_whname", list.getObject("pi_whname"));
			map.set("pi_inoutno", code);
			map.set("pi_cgy", list.getObject("pi_cgy"));
			map.set("pi_date", Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)));
			map.set("pi_invostatuscode", "ENTERING");
			map.set("pi_invostatus", "在录入");
			map.set("pi_statuscode", "UNPOST");
			map.set("pi_sendcode", list.getString("pi_inoutno"));
			map.set("pi_departmentcode", list.getObject("pi_departmentcode"));
			map.set("pi_departmentname", list.getObject("pi_departmentname"));
			map.execute();
		}
		int detno = 1;
		List<String> sqls = new ArrayList<String>();
		for (Map<Object, Object> store : gStore) {
			SqlMap map = new SqlMap("ProdIODetail");
			map.set("pd_pdno", detno++);
			map.set("pd_piid", id);
			map.set("pd_inoutno", code);
			map.set("pd_piclass", "销售退货单");
			map.set("pd_whcode", store.get("pd_whcode"));
			map.set("pd_whname", store.get("pd_whname"));
			map.set("pd_price", store.get("pd_price"));
			map.set("pd_total", store.get("pd_total"));
			map.set("pd_taxrate", store.get("pd_taxrate"));
			map.set("pd_ordercode", store.get("pd_ordercode"));
			map.set("pd_orderdetno", store.get("pd_orderdetno"));
			map.set("pd_custprodcode", store.get("pd_custprodcode"));
			map.set("pd_prodcode", store.get("pd_prodcode"));
			map.set("pd_prodid", store.get("pd_prodid"));
			map.set("pd_inqty", store.get("pd_tqty"));
			map.set("pd_whid", store.get("pd_whid"));
			map.set("pd_sendprice", store.get("pd_sendprice"));
			map.setSpecial("pd_id", "PRODIODETAIL_SEQ.nextval");
			map.set("pd_discount", store.get("pd_discount"));
			map.set("pd_ioid", store.get("pd_id"));
			map.execute();
			String updateSql = "update ProdIODetail set pd_yqty=nvl(pd_yqty,0)+" + store.get("pd_tqty") + " where pd_id="
					+ store.get("pd_id");
			sqls.add(updateSql);// 更新原明细中的已转数量
		}
		baseDao.execute(sqls);
		return "转入成功,退货单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + id
				+ "&gridCondition=pd_piidIS" + id + "&whoami=ProdInOut!SaleReturn')\">" + code + "</a>&nbsp;<hr>";
	}

	@Override
	public void updatebgxh(String data) {
		Map<Object, Object> formdata = BaseUtil.parseFormStoreToMap(data);
		int ISAllUpdate = Integer.parseInt(formdata.get("allupdate").toString());
		StringBuffer sb = new StringBuffer();
		Object pd_bgxh = formdata.get("pd_bgxh");
		Object caller = formdata.get("caller");
		if (pd_bgxh != null && !"".equals(pd_bgxh.toString()) && !"null".equals(pd_bgxh.toString())) {
			sb.append("pd_bgxh='").append(pd_bgxh).append("'");
		}
		String updateSql = "update ProdIODetail set " + sb.toString();
		Object pi_id = formdata.get("pd_piid");
		if (ISAllUpdate == 1) {
			updateSql = updateSql + " WHERE pd_piid =" + pi_id;
			SqlRowList rs = baseDao.queryForRowSet("select pd_pdno,pd_bgxh from ProdIODetail where pd_piid=? order by pd_pdno", pi_id);
			while (rs.next()) {
				// 记录操作
				if (pd_bgxh != null && !"".equals(pd_bgxh.toString()) && !"null".equals(pd_bgxh.toString())) {
					baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "更新报关型号", "行" + rs.getInt(1) + ":"
							+ rs.getString(2) + "=>" + pd_bgxh, caller + "|pi_id=" + pi_id));
				}
			}
		} else {
			updateSql = updateSql + " WHERE pd_id=" + formdata.get("pd_id");
			SqlRowList rs = baseDao.queryForRowSet("select pd_pdno,pd_bgxh,pd_piid from ProdIODetail where pd_id=?", formdata.get("pd_id"));
			if (rs.next()) {
				// 记录操作
				if (pd_bgxh != null && !"".equals(pd_bgxh.toString()) && !"null".equals(pd_bgxh.toString())) {
					baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "更新报关型号", "行" + rs.getInt(1) + ":"
							+ rs.getString(2) + "=>" + pd_bgxh, caller + "|pi_id=" + rs.getInt(3)));
				}
			}
		}
		baseDao.execute(updateSql);
	}

	@Override
	public void updateOrderCode(String data) {
		Map<Object, Object> formdata = BaseUtil.parseFormStoreToMap(data);
		Object pd_ordercode = formdata.get("pd_ordercode");
		Object pd_orderdetno = formdata.get("pd_orderdetno");
		Object caller = formdata.get("caller");
		SqlRowList rs = baseDao.queryForRowSet("select pd_pdno,pd_ordercode,pd_orderdetno,pd_piid from ProdIODetail where pd_id=?",
				formdata.get("pd_id"));
		if (rs.next()) {
			baseDao.execute("update ProdIODetail set pd_ordercode='" + pd_ordercode + "',pd_orderdetno=" + pd_orderdetno + ",pd_prodcode='"
					+ formdata.get("pd_prodcode") + "', pd_orderprice=" + formdata.get("pd_orderprice") + ",pd_taxrate="
					+ formdata.get("pd_taxrate") + " where pd_id=" + formdata.get("pd_id"));
			getTotal(rs.getInt("pd_piid"), caller.toString());
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "更新关联单号", "行" + rs.getObject("pd_pdno") + ":"
					+ rs.getString("pd_ordercode") + "=>" + pd_ordercode + "," + rs.getString("pd_orderdetno") + "=>" + pd_orderdetno,
					caller + "|pi_id=" + rs.getInt("pd_piid")));
		}
	}

	@Override
	public void updateBatchCode(String data) {
		Map<Object, Object> formdata = BaseUtil.parseFormStoreToMap(data);
		Object pd_batchcode = formdata.get("pd_batchcode");
		Object pd_batchid = formdata.get("pd_batchid");
		Object caller = formdata.get("caller");
		SqlRowList rs = baseDao.queryForRowSet("select pd_pdno,pd_batchcode,pd_batchid,pd_piid from ProdIODetail where pd_id=?",
				formdata.get("pd_id"));
		if (rs.next()) {
			baseDao.execute("update ProdIODetail set pd_batchcode='" + pd_batchcode + "',pd_batchid=" + pd_batchid + " where pd_id="
					+ formdata.get("pd_id"));
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "修改批号", "行" + rs.getObject("pd_pdno") + ":"
					+ rs.getString("pd_batchcode") + "=>" + pd_batchcode, caller + "|pi_id=" + rs.getInt("pd_piid")));
		}
	}

	@Override
	@Transactional
	public String turnMRB(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		Object y = 0;
		SqlRowList rs = null;
		for (Map<Object, Object> map : maps) {
			int pdid = Integer.parseInt(map.get("pd_id").toString());
			double tqty = Double.parseDouble(map.get("pd_tqty").toString());
			y = baseDao.getFieldDataByCondition("QUA_MRB", "sum(nvl(MR_INQTY,0))", "MR_VEID=" + pdid);
			y = y == null ? 0 : y;
			rs = baseDao.queryForRowSet("SELECT pd_inoutno,pd_pdno,pd_inqty FROM ProdIODetail WHERE pd_id=? and pd_inqty<?", pdid,
					Double.parseDouble(y.toString()) + tqty);
			if (rs.next()) {
				sb = new StringBuffer("[本次数量填写超出可转数量],入库单号:").append(rs.getString("pd_inoutno")).append(",行号:")
						.append(rs.getInt("pd_pdno")).append(",入库数量:").append(rs.getDouble("pd_inqty")).append(",已转数:").append(y)
						.append(",本次数:").append(tqty).append("<hr/>");
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
		if (maps.size() > 0) {
			JSONObject j = null;
			String mr_code = null;
			for (Map<Object, Object> map : maps) {
				int pdid = Integer.parseInt(map.get("pd_id").toString());
				handlerService.handler("ProdInOut!DefectIn", "turn", "before", new Object[] { pdid });
				double tqty = Double.parseDouble(map.get("pd_tqty").toString());
				j = prodInOutDao.newMRB(pdid, tqty);
				if (j != null) {
					mr_code = j.getString("mr_code");
					sb.append("转入成功,MRB单号:" + "<a href=\"javascript:openUrl('jsps/scm/qc/mrb.jsp?formCondition=mr_idIS" + j.get("mr_id")
							+ "&gridCondition=md_mridIS" + j.get("mr_id") + "')\">" + mr_code + "</a>&nbsp;");
				}
				// 修改ProdInOutDetail状态
				baseDao.updateByCondition("ProdIODetail", "pd_yqty=nvl(pd_yqty,0)+" + tqty, "pd_id=" + pdid);
				// 记录日志
				Object[] cts = baseDao.getFieldsDataByCondition("ProdIODetail", "pd_piid,pd_pdno", "pd_id=" + pdid);
				baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "转MRB单", BaseUtil
						.getLocalMessage("msg.turnSuccess") + "," + BaseUtil.getLocalMessage("msg.detail") + cts[1] + ",数量：" + tqty,
						"ProdInOut!DefectIn" + "|pd_id=" + cts[0]));
				// 转MRB单后逻辑
				handlerService.handler("ProdInOut!DefectIn", "turn", "after", new Object[] { j.get("mr_id") });
			}
		}
		return sb.toString();
	}

	// 税率强制等于币别表的默认税率
	private void useDefaultTax(String caller, Object pi_id) {
		if (baseDao.isDBSetting(caller, "useDefaultTax")) {
			baseDao.execute("update ProdIODetail set pd_taxrate=(select cr_taxrate from currencys left join ProdInOut on pi_currency=cr_name and cr_statuscode='CANUSE' where pd_piid=pi_id)"
					+ " where pd_piid=" + pi_id);
		}
		String defaultTax = baseDao.getDBSetting(caller, "defaultTax");
		if (defaultTax != null) {
			// 税率强制等于币别表的默认税率
			if ("1".equals(defaultTax)) {
				baseDao.execute("update ProdIODetail set pd_taxrate=(select cr_taxrate from currencys left join ProdInOut on pi_currency=cr_name and cr_statuscode='CANUSE' where pd_piid=pi_id)"
						+ " where pd_piid=" + pi_id);
			}
			// 税率强制等于供应商资料的默认税率
			if ("2".equals(defaultTax)) {
				if ("ProdInOut!PurcCheckin".equals(caller) || "ProdInOut!PurcCheckout".equals(caller)
						|| "ProdInOut!OutsideCheckIn".equals(caller) || "ProdInOut!OutesideCheckReturn".equals(caller)) {
					baseDao.execute("update ProdIODetail set pd_taxrate=(select nvl(ve_taxrate,0) from Vendor left join ProdInOut on pi_cardcode=ve_code and ve_auditstatuscode='AUDITED' where pi_id=pd_piid)"
							+ " where pd_piid=" + pi_id);
				}
				if ("ProdInOut!SaleReturn".equals(caller) || "ProdInOut!Sale".equals(caller)) {
					baseDao.execute("update ProdIODetail set pd_taxrate=(select nvl(cu_taxrate,0) from Customer left join ProdInOut on pi_cardcode=cu_code and cu_auditstatuscode='AUDITED' where pi_id=pd_piid)"
							+ " where pd_piid=" + pi_id);
				}
			}
		}
	}

	// 本位币允许税率为0
	private void allowZeroTax(String caller, Object pi_id) {
		String currency = baseDao.getDBSetting("defaultCurrency");
		boolean allowZeroTax = baseDao.isDBSetting(caller, "allowZeroTax");
		if ("ProdInOut!Sale".equals(caller) || "ProdInOut!SaleReturn".equals(caller)) {
			allowZeroTax = baseDao.isDBSetting("Sale", "allowZeroTax");
		}
		if ("ProdInOut!PurcCheckin".equals(caller) || "ProdInOut!PurcCheckout".equals(caller)) {
			allowZeroTax = baseDao.isDBSetting("Purchase", "allowZeroTax");
		}
		if ("ProdInOut!OutsideCheckIn".equals(caller) || "ProdInOut!OutesideCheckReturn".equals(caller)) {
			allowZeroTax = baseDao.isDBSetting("Make", "allowZeroTax");
		}
		if (!allowZeroTax) {
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select WM_CONCAT(pd_pdno) from ProdIODetail left join ProdInOut on pd_piid=pi_id where nvl(pd_taxrate,0)=0 and pi_currency='"
							+ currency + "' and pi_id=?", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("本位币税率为0，不允许进行当前操作!行号：" + dets);
			}
		}
	}

	/**
	 * 没有出现在用料表的物料，自动添加到用料表，单位用量和需求都是0
	 * 
	 * @param mm_remark
	 * @param pi_id
	 */
	private void dealOutOfMakeMaterial(String mm_remark, int pi_id) {
		int maxdetno = 0;
		String ordercode = "";
		String prodcode = "";
		String sqlStr = "";
		int maid = 0;
		String thestatus = baseDao.getFieldDataByCondition("Prodinout", "pi_statuscode", "pi_id='" + pi_id + "'").toString();
		if (!thestatus.equals("POSTED")) {
			// 自动更新工单序号
			sqlStr = "update ProdIODetail set pd_orderdetno=(select NVL(max(mm_detno),0) from makematerial where mm_code=pd_ordercode and mm_prodcode=pd_prodcode) where  pd_piid='"
					+ pi_id + "' and nvl(pd_orderdetno,0)=0 and nvl(pd_ordercode,' ')<>' ' ";
			baseDao.execute(sqlStr);
			// 如果不在主料范围，取替代关系表的
			sqlStr = "update ProdIODetail set pd_orderdetno=(select NVL(max(mm_detno),0) from makematerial,makematerialreplace where mm_code=pd_ordercode and mm_id=mp_mmid and mp_prodcode=pd_prodcode ) where  pd_piid='"
					+ pi_id + "' and nvl(pd_orderdetno,0)=0 and nvl(pd_ordercode,' ')<>' ' ";
			baseDao.execute(sqlStr);
			// 不存在用料表的料号自动添加到用料表
			String sql = "select pd_ordercode,pd_prodcode,sum(pd_outqty) as qty,ma_id,pd_id from ProdIODetail left join make on ma_code=pd_ordercode where pd_piid='"
					+ pi_id
					+ "' and nvl(pd_orderdetno,0)=0 and nvl(ma_code,' ')<>' ' and pd_prodcode not in (select mm_prodcode from makematerial where mm_code=pd_ordercode) and pd_prodcode not in (select mp_prodcode from makematerial,makematerialreplace where mm_id=mp_mmid and mm_code=pd_ordercode  ) group by pd_ordercode,pd_prodcode,ma_id,pd_id";
			SqlRowList rs = baseDao.queryForRowSet(sql);
			while (rs.next()) {
				try {
					ordercode = rs.getString("pd_ordercode");
					prodcode = rs.getString("pd_prodcode");
					maid = rs.getInt("ma_id");
					maxdetno = Integer.parseInt(baseDao.getFieldDataByCondition("makematerial", "max(mm_detno)", "mm_maid=" + maid)
							.toString()) + 1;
					// 20161228 工单外退料，增加MM_UPDATETYPE 赋值 R
					sqlStr = "insert into makematerial(mm_maid,mm_id,mm_code,mm_detno,mm_prodcode,mm_oneuseqty,mm_qty,mm_lostqty,mm_havegetqty,mm_supplytype,mm_remark,mm_wccode,mm_whcode,mm_balance,mm_updatetype,mm_updatedate)";
					sqlStr = sqlStr + "values('" + maid + "',makematerial_SEQ.nextval,'" + ordercode + "','" + maxdetno + "','" + prodcode
							+ "','0'";
					sqlStr = sqlStr + ",'0','0','0','','" + mm_remark + "','','','0','R',sysdate)";
					baseDao.execute(sqlStr);
					sqlStr = "update makematerial set mm_wccode=(select  max(ma_wccode) from make where mm_maid=ma_id )where mm_maid='"
							+ maid + "' and mm_detno=" + maxdetno;
					baseDao.execute(sqlStr);
					sqlStr = "update makematerial set (mm_supplytype,mm_whcode)=(select pr_supplytype,pr_whcode from product where pr_code='"
							+ prodcode + "') where mm_maid='" + maid + "' and mm_detno=" + maxdetno;
					baseDao.execute(sqlStr);
					sqlStr = "update ProdIODetail set pd_orderdetno=" + maxdetno + " where pd_id=" + rs.getInt("pd_id")
							+ " and pd_ordercode='" + ordercode + "' and pd_prodcode='" + prodcode + "' and nvl(pd_orderdetno,0)=0 ";
					baseDao.execute(sqlStr);
				} catch (Exception ex) {
					BaseUtil.showError(ex.toString());
				}
			}
		}
	}

	@Override
	public String turnYPOutREturnnew(String caller, String data) {
		List<Map<Object, Object>> gStore = BaseUtil.parseGridStoreToMaps(data);
		Object pi_id = gStore.get(0).get("pd_piid");
		String sql = "select * from prodinout where pi_id=?";
		SqlRowList list = baseDao.queryForRowSet(sql, pi_id);
		int id = baseDao.getSeqId("prodinout_seq");
		String code = baseDao.sGetMaxNumber("ProdInOut!GoodsReturn", 2);
		String ordercode = null;
		while (list.next()) {
			SqlMap map = new SqlMap("prodinout");
			ordercode = list.getString("pi_inoutno");
			map.set("pi_id", id);
			map.set("pi_inoutno", code);
			map.set("pi_class", "用品归还单");
			map.set("pi_date", Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)));
			map.set("pi_invostatus", "在录入");
			map.set("pi_whcode", list.getObject("pi_whcode"));
			map.set("pi_whname", list.getObject("pi_whname"));
			map.set("pi_paydate", Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)));
			map.set("pi_currency", list.getObject("pi_currency"));
			map.set("pi_rate", list.getObject("pi_rate"));
			map.set("pi_title", list.getObject("pi_title"));
			map.set("pi_cardcode", list.getObject("pi_cardcode"));
			map.set("pi_departmentcode", list.getObject("pi_departmentcode"));
			map.set("pi_departmentname", list.getObject("pi_departmentname"));
			map.set("pi_emcode", list.getObject("pi_emcode"));
			map.set("pi_emname", list.getObject("pi_emname"));
			map.set("pi_receivename", list.getObject("pi_receivename"));
			map.set("pi_status", "未过账");
			map.set("pi_statuscode", "UNPOST");
			map.set("pi_printstatus", "未打印");
			map.set("pi_printstatuscode", "UNPRINT");
			map.set("pi_transport", list.getObject("pi_transport"));
			map.set("pi_invostatuscode", "ENTERING");
			map.set("pi_recordman", SystemSession.getUser().getEm_name());
			map.set("pi_recorddate", Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)));
			map.set("pi_cardid", list.getObject("pi_cardid"));
			map.execute();
		}
		int detno = 1;
		List<String> sqls = new ArrayList<String>();
		for (Map<Object, Object> store : gStore) {
			SqlMap map = new SqlMap("ProdIODetail");
			map.set("pd_id", baseDao.getSeqId("ProdIODetail_seq"));
			map.set("pd_pdno", detno++);
			map.set("pd_ordercode", ordercode);
			map.set("pd_orderdetno", store.get("pd_pdno"));
			map.set("pd_prodcode", store.get("pd_prodcode"));
			map.set("pd_prodid", store.get("pd_prodid"));
			map.set("pd_prodmadedate", Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)));
			map.set("pd_inqty", store.get("pd_tqty"));
			map.set("pd_sendprice", store.get("pd_sendprice"));
			map.set("pd_price", store.get("pd_price"));
			map.set("pd_orderprice", store.get("pd_orderprice"));
			map.set("pd_ordertotal", store.get("pd_ordertotal"));
			map.set("pd_whcode", store.get("pd_whcode"));
			map.set("pd_whname", store.get("pd_whname"));
			map.set("pd_taxrate", store.get("pd_taxrate"));
			map.set("pd_status", "0");
			map.set("pd_piid", id);
			map.set("pd_orderid", pi_id);
			map.set("pd_ioid", store.get("pd_id"));
			map.execute();
			String updateSql = "update ProdIODetail set pd_yqty=nvl(pd_yqty,0)+" + store.get("pd_tqty") + " where pd_id="
					+ store.get("pd_id");
			sqls.add(updateSql);// 更新原明细中的已转数量
		}
		baseDao.execute(sqls);
		return "转入成功,归还单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + id
				+ "&gridCondition=pd_piidIS" + id + "&whoami=ProdInOut!GoodsReturn')\">" + code + "</a>&nbsp;<hr>";

	}

	@Override
	public void updateBorrowCargoType(int pi_id, String type, String remark, String caller) {
		baseDao.updateByCondition("ProdInOut", "pi_outtype='" + type + "'", "pi_id =" + pi_id);
		if (remark != null && !"".equals(remark)) {
			baseDao.updateByCondition("ProdInOut", "pi_remark='" + remark + "'", "pi_id =" + pi_id);
		}
		// 记录操作
		baseDao.logger.others("更新借货类型", "msg.updateSuccess", caller, "pi_id", pi_id);
	}

	@Override
	public String vastTurnIn(String data, String caller) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		String ids = CollectionUtil.pluckSqlString(store, "mm_id");
		StringBuffer sb = new StringBuffer();
		String whcode = (String) store.get(0).get("mm_whcode");
		int detno = 1;
		// 保存ProdInOut
		boolean bool = baseDao
				.checkIf("PeriodsDetail", "pd_code='MONTH-P' and pd_status=99 and pd_detno='" + DateUtil.getYearmonth() + "'");
		if (bool) {
			BaseUtil.showError("本期出入库已结账,请修改制单日期!");
		}
		String freeze = baseDao.getDBSetting("freezeMonth");
		if (freeze != null) {
			if (Integer.parseInt(freeze) > DateUtil.getYearmonth(DateUtil.currentDateString(Constant.YMD_HMS).toString())) {
				BaseUtil.showError("库存期间冻结不能制作这个月的出入库单据,请修改制单日期!");
			}
		}
		if (baseDao.isDBSetting("CopCheck")) {
			SqlRowList rs = baseDao
					.queryForRowSet("select  count(1) n from (select distinct ma_cop from make left join makematerial on ma_id=mm_maid where mm_id in ("
							+ ids + ") )");
			if (rs.next()) {
				if (rs.getInt("n") > 1) {
					BaseUtil.showError("所属公司不一致的制造单不允许合并下达到一张拆件入库单中!");
				}
			}
		}
		Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
		int id = baseDao.getSeqId("PRODINOUT_SEQ");
		String no = baseDao.sGetMaxNumber(caller, 2);
		baseDao.execute(INSERT_PRODIO, new Object[] { id, no, time, "拆件入库单", BaseUtil.getLocalMessage("ENTERING"), "ENTERING",
				SystemSession.getUser().getEm_name(), time, "UNPOST", BaseUtil.getLocalMessage("UNPOST") });
		int autoNewbatchcode = 0;// 判断是否需要自动生成批号
		SqlRowList rs0 = baseDao.queryForRowSet("select ds_inorout from DOCUMENTSETUP where ds_table=?", caller);
		if (rs0.next()) {
			String Code = rs0.getObject("ds_inorout").toString();
			if (Code.equals("IN") || Code.equals("-OUT")) {
				autoNewbatchcode = -1;
			}
		}
		for (Map<Object, Object> s : store) {
			// 保存ProdioDetail
			SqlMap map = new SqlMap("ProdIODetail");
			map.set("pd_ordercode", s.get("mm_code"));
			map.set("pd_orderdetno", s.get("mm_detno"));
			map.set("pd_pdno", detno++);
			map.set("pd_prodcode", s.get("mm_prodcode"));
			map.set("pd_inqty", s.get("mm_thisqty"));
			map.set("pd_id", baseDao.getSeqId("PRODIODETAIL_SEQ"));
			map.set("pd_auditstatus", "ENTERING");
			map.set("pd_inoutno", no);
			map.set("pd_piclass", "拆件入库单");
			map.set("pd_whcode", whcode);
			map.set("pd_accountstatuscode", "UNACCOUNT");
			map.set("pd_accountstatus", BaseUtil.getLocalMessage("UNACCOUNT"));
			map.set("pd_piid", id);

			// 0表示未过账;99表示已过帐
			map.set("pd_status", 0);
			if (autoNewbatchcode == -1) {
				map.set("pd_batchcode", baseDao.getBatchcode(caller));
			}
			map.execute();
		}
		if ("ProdInOut!SaleBorrow".equals(caller)) {
			baseDao.execute("update prodiodetail set pd_reply='未归还' where pd_piid=" + id + " and pd_piclass='借货归还单'");
		}
		// 更新
		sb.append("转入成功,入库单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + id
				+ "&gridCondition=pd_piidIS" + id + "&whoami=" + "ProdInOut!PartitionStockIn" + "')\">" + no + "</a>&nbsp;<hr>");

		return sb.toString();
	}

	private void copcheck(int pi_id, String caller) {
		if (baseDao.isDBSetting("CopCheck")) {
			// 出入库单：明细行采购单所属公司与当前单所属公司必须一致，可在提交、打印、审核、过账等操作前配置
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail left join ProdInOut on pd_piid=pi_id left join Purchase on pd_ordercode=pu_code where pi_id=? and nvl(pu_cop,' ')<>nvl(pi_cop,' ') and nvl(pd_ordercode,' ')<>' ' "
									+ " and pd_piclass in ('采购验收单','采购验退单')", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细行采购单所属公司与当前单所属公司不一致，不允许进行当前操作!行号：" + dets);
			}
			// 出入库单：明细行销售单所属公司与当前单所属公司必须一致，可在提交、打印、审核、过账等操作前配置
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail left join ProdInOut on pd_piid=pi_id left join Sale on pd_ordercode=sa_code where pi_id=? and nvl(sa_cop,' ')<>nvl(pi_cop,' ') and nvl(pd_ordercode,' ')<>' ' "
									+ "and pd_piclass in ('出货单','销售退货单','销售拨出单','销售拨入单')", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细行销售单所属公司与当前单所属公司不一致，不允许进行当前操作!行号：" + dets);
			}
			// 出货单：来源单所属公司与当前单所属公司不一致，不允许进行当前操作
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail left join ProdInOut on pd_piid=pi_id left join Sendnotify on pd_snid=sn_id where pi_id=? and nvl(sn_cop,' ')<>nvl(pi_cop,' ') and nvl(pd_snid,0)<>0 ",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细来源通知单所属公司与当前单所属公司不一致，不允许进行当前操作!行号：" + dets);
			}
			// 出入库单：明细行制造单所属公司与当前单所属公司必须一致，可在提交、打印、审核、过账等操作前配置
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail left join ProdInOut on pd_piid=pi_id left join Make on pd_ordercode=ma_code where pi_id=? and nvl(ma_cop,' ')<>nvl(pi_cop,' ') and nvl(pd_ordercode,' ')<>' ' "
									+ "and pd_piclass in  ('生产领料单','生产退料单','生产补料单','完工入库单','拆件入库单')", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细行制造单所属公司与当前单所属公司不一致，不允许进行当前操作!行号：" + dets);
			}
			// 出入库单：明细行委外单所属公司与当前单所属公司必须一致，可在提交、打印、审核、过账等操作前配置
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail left join ProdInOut on pd_piid=pi_id left join Make on pd_ordercode=ma_code where pi_id=? and nvl(ma_cop,' ')<>nvl(pi_cop,' ') and nvl(pd_ordercode,' ')<>' ' "
									+ "and pd_piclass in ('委外验收单','委外验退单','委外领料单','委外退料单','委外补料单')", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细行委外单所属公司与当前单所属公司不一致，不允许进行当前操作!行号：" + dets);
			}
			if ("ProdInOut!SaleAppropriationOut".equals(caller) || "ProdInOut!OtherOut".equals(caller)
					|| "ProdInOut!AppropriationOut".equals(caller)) {
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_pdno) from ProdIODetail left join ProdInOut on pd_piid=pi_id left join SaleForecast on pd_plancode=sf_code where pi_id=? and nvl(sf_cop,' ')<>nvl(pi_cop,' ') and nvl(pd_plancode,' ')<>' ' "
										+ "and pd_piclass in  ('销售拨出单','其它出库单')", String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("明细行销售预测单所属公司与当前单所属公司不一致，不允许进行当前操作!行号：" + dets);
				}
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_pdno) from ProdIODetail left join prodinout on pd_piid=pi_id left join Warehouse on pd_inwhcode=wh_code where pi_id=? and pi_class in ('销售拨出单','拨出单') and nvl(pi_cop,' ')<>nvl(wh_cop,' ')",
								String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("单据所属公司和明细行拨入仓库的所属公司不一致，不允许进行当前操作!行号：" + dets);
				}
			}
			// 出入库单限制界面的所属公司和仓库的所属公司必须一致，可在提交、打印、审核、过账等操作前配置
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail left join prodinout on pd_piid=pi_id left join Warehouse on pd_whcode=wh_code where nvl(pi_cop,' ')<>nvl(wh_cop,' ') and pi_id=? and pi_class not in ('成本调整单')",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("单据所属公司和明细行仓库的所属公司不一致，不允许进行当前操作!行号：" + dets);
			}
		}
	}

	private void factorycheck(int pi_id, String caller) {
		if (baseDao.isDBSetting("MpsDesk", "mrpSeparateFactory")) {
			// 采购验收单、采购验退单：明细行采购单所属工厂与仓库的所属工厂不一致，，不允许进行当前操作
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail left join PurchaseDetail on ProdIODetail.pd_ordercode=PurchaseDetail.pd_code and ProdIODetail.pd_orderdetno=PurchaseDetail.pd_detno left join Warehouse on ProdIODetail.pd_whcode=wh_code left join product on ProdIODetail.pd_prodcode=pr_code where pd_piid=? and nvl(PR_ISGROUPPURC,0)=0 and nvl(PurchaseDetail.pd_factory,' ')<>nvl(wh_factory,' ') and nvl(ProdIODetail.pd_ordercode,' ')<>' ' "
									+ " and pd_piclass in ('采购验收单','采购验退单')", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细行采购单所属工厂与仓库的所属工厂不一致，不允许进行当前操作!行号：" + dets);
			}
			// 生产领料单、退料单、补料单：明细行制造单所属工厂与仓库的所属工厂不一致，不允许进行当前操作
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail left join Make on pd_ordercode=ma_code left join Warehouse on pd_whcode=wh_code left join product on ProdIODetail.pd_prodcode=pr_code where pd_piid=? and nvl(PR_ISGROUPPURC,0)=0 and nvl(ma_factory,' ')<>nvl(wh_factory,' ') and nvl(pd_ordercode,' ')<>' ' "
									+ "and pd_piclass in  ('生产领料单','生产退料单','生产补料单')", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细行制造单所属工厂与仓库的所属工厂不一致，不允许进行当前操作!行号：" + dets);
			}
			// 委外领料单、退料单、补料单：明细行委外单所属工厂与仓库的所属工厂不一致，不允许进行当前操作
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail left join Make on pd_ordercode=ma_code left join Warehouse on pd_whcode=wh_code left join product on ProdIODetail.pd_prodcode=pr_code where pd_piid=? and nvl(PR_ISGROUPPURC,0)=0 and nvl(ma_factory,' ')<>nvl(wh_factory,' ') and nvl(pd_ordercode,' ')<>' ' "
									+ "and pd_piclass in ('委外领料单','委外退料单','委外补料单')", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细行委外单所属工厂与仓库的所属工厂不一致，不允许进行当前操作!行号：" + dets);
			}
		}
	}

	/**
	 * 计算pi_total
	 */
	private void getTotal(Object pi_id, String caller) {
		baseDao.execute("update prodiodetail set pd_purcinqty=nvl(pd_inqty,0) where pd_piid=" + pi_id
				+ " and exists (select 1 from product where pd_prodcode=pr_code and nvl(pr_purcunit,pr_unit)=pr_unit) ");
		baseDao.execute("update prodiodetail set pd_purcoutqty=nvl(pd_outqty,0) where pd_piid=" + pi_id
				+ " and exists (select 1 from product where pd_prodcode=pr_code and nvl(pr_purcunit,pr_unit)=pr_unit)");

		baseDao.execute("update prodiodetail set pd_purcinqty=pd_inqty/(select case when nvl(pr_purcrate,0)<>0 then pr_purcrate else 1 end from product where pr_code=pd_prodcode) where pd_piid="
				+ pi_id + " and nvl(pd_purcinqty,0)=0");

		if ("ProdInOut!PurcCheckin".equals(caller) || "ProdInOut!PurcCheckout".equals(caller) || "ProdInOut!OutsideCheckIn".equals(caller)
				|| "ProdInOut!OutesideCheckReturn".equals(caller)) {
			if ("ProdInOut!OutsideCheckIn".equals(caller) || "ProdInOut!OutesideCheckReturn".equals(caller)) {// 委外
				baseDao.updateByCondition("ProdIODetail", "pd_orderprice=(select ma_price from make where ma_code=pd_ordercode)",
						"pd_piid=" + pi_id + " and nvl(pd_orderprice,0)=0");
				// 强制取委外单主表中的税率
				baseDao.execute(
						"update ProdIODetail set pd_taxrate=(select nvl(ma_taxrate,0) from make where ma_code=pd_ordercode) where pd_ordercode is not null and pd_piid=?",
						pi_id);
			} else {// 采购
				baseDao.updateByCondition("ProdIODetail",
						"pd_orderprice=nvl((select pd_price from PurchaseDetail where pd_code=pd_ordercode and pd_detno=pd_orderdetno),0)",
						"pd_piid=" + pi_id + " and nvl(pd_orderprice,0)=0");
				// 强制取采购单明细表中的对应的税率
				baseDao.execute(
						"update ProdIODetail set pd_taxrate=(select nvl(pd_rate,0) from PurchaseDetail where pd_code=pd_ordercode and pd_detno=pd_orderdetno) where  pd_ordercode is not null and  pd_piid=?",
						pi_id);

			}
			baseDao.execute("update ProdIODetail set pd_ordertotal=round(pd_orderprice*(case when nvl(pd_purcinqty,0)+nvl(pd_purcoutqty,0)=0 then nvl(pd_inqty,0)+nvl(pd_outqty,0) else nvl(pd_purcinqty,0)+nvl(pd_purcoutqty,0) end),2) where pd_piid="
					+ pi_id);
			baseDao.execute("update ProdIODetail set pd_taxtotal=round(pd_orderprice*(case when nvl(pd_purcinqty,0)+nvl(pd_purcoutqty,0)=0 then nvl(pd_inqty,0)+nvl(pd_outqty,0) else nvl(pd_purcinqty,0)+nvl(pd_purcoutqty,0) end)*nvl(pd_taxrate,0)/(100+nvl(pd_taxrate,0)),2),pd_nettotal=round(pd_orderprice*(case when nvl(pd_purcinqty,0)+nvl(pd_purcoutqty,0)=0 then nvl(pd_inqty,0)+nvl(pd_outqty,0) else nvl(pd_purcinqty,0)+nvl(pd_purcoutqty,0) end)/(1+nvl(pd_taxrate,0)/100),2) where pd_id="
					+ pi_id);
			baseDao.updateByCondition("ProdIODetail", " pd_total=round(nvl(pd_price,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2)", "pd_piid="
					+ pi_id);
			baseDao.updateByCondition("ProdInOut",
					"pi_total = round(nvl((SELECT sum(round(pd_ordertotal,2)) FROM ProdIODetail WHERE pd_piid=pi_id),0),2)", "pi_id="
							+ pi_id);
			baseDao.updateByCondition("ProdInOut", "pi_totalupper=L2U(nvl(pi_total,0))", "pi_id=" + pi_id);
		} else if ("ProdInOut!Sale".equals(caller) || "ProdInOut!SaleReturn".equals(caller)) {
			baseDao.updateByCondition(
					"ProdIODetail",
					"pd_ordertotal=round(nvl(pd_sendprice,0)*(case when nvl(pd_purcinqty,0)+nvl(pd_purcoutqty,0)=0 then nvl(pd_inqty,0)+nvl(pd_outqty,0) else nvl(pd_purcinqty,0)+nvl(pd_purcoutqty,0) end),2),pd_total=round(nvl(pd_price,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2)",
					"pd_piid=" + pi_id);
			baseDao.updateByCondition(
					"ProdIODetail",
					"pd_netprice=round(pd_sendprice/(1+pd_taxrate/100),8),pd_nettotal=round(pd_sendprice*(case when nvl(pd_purcinqty,0)+nvl(pd_purcoutqty,0)=0 then nvl(pd_inqty,0)+nvl(pd_outqty,0) else nvl(pd_purcinqty,0)+nvl(pd_purcoutqty,0) end)/(1+nvl(pd_taxrate,0)/100),2)",
					"pd_piid=" + pi_id);
			baseDao.updateByCondition("ProdInOut",
					"pi_total=round(nvl((SELECT sum(round(pd_ordertotal,2)) FROM ProdIODetail WHERE pd_piid=pi_id),0),2)", "pi_id=" + pi_id);
			baseDao.execute("update prodiodetail set pd_customprice=pd_sendprice, pd_taxamount=pd_ordertotal "
					+ "where pd_piclass in ('出货单','销售退货单') and pd_piid=? and nvl(pd_customprice,0)=0", pi_id);
			baseDao.updateByCondition("ProdInOut", "pi_totalupper=L2U(nvl(pi_total,0))", "pi_id=" + pi_id);
			// 成品标准成本，用于报价毛利润统计
			baseDao.execute("UPDATE product set pr_cost=nvl((select price from ( select qd_prodcode,round(qd_factprice*qu_rate,2) price,"
					+ "     row_number() over(partition by qd_prodcode order by qu_auditdate desc) rn from  quotation,quotationdetail  "
					+ "where qu_id=qd_quid and qu_statuscode='AUDITED') t1 where rn=1 and qd_prodcode=pr_code),0) "
					+ "where nvl(pr_cost,0)=0 and pr_code in (select pd_prodcode from prodiodetail where pd_piid=?)", pi_id);
			baseDao.execute("UPDATE prodiodetail SET pd_showprice=nvl((select pr_cost from product where pd_prodcode=pr_code),0)"
					+ "WHERE  pd_piid=?", pi_id);
		} else {
			baseDao.updateByCondition("ProdIODetail", "pd_total=round(nvl(pd_price,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2)", "pd_piid="
					+ pi_id);
			baseDao.updateByCondition(
					"ProdInOut",
					"pi_total=(SELECT sum(round(nvl(pd_price,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2)) FROM ProdIODetail WHERE pd_piid=pi_id)",
					"pi_id=" + pi_id);
			baseDao.updateByCondition("ProdInOut", "pi_totalupper=L2U(nvl(pi_total,0))", "pi_id=" + pi_id);
		}
		if ("ProdInOut!Sale".equals(caller) || "ProdInOut!SaleReturn".equals(caller) || "ProdInOut!AppropriationOut".equals(caller)
				|| "ProdInOut!OtherOut".equals(caller) || "ProdInOut!ExchangeOut".equals(caller)) {
			Object cardcode = baseDao.getFieldDataByCondition("ProdInOut", "pi_cardcode", "pi_id=" + pi_id);
			baseDao.execute("update prodiodetail set (PD_CUSTPRODCODE,PD_CUSTPRODSPEC,pd_custproddetail)=(select max(pc_custprodcode),max(pc_custprodspec),max(pc_custproddetail) from ProductCustomer left join Product on pc_prodid=pr_id left join customer on pc_custid=cu_id where cu_code='"
					+ cardcode
					+ "' and pd_prodcode=pr_code) where pd_piid="
					+ pi_id
					+ " and nvl(pd_custprodcode,' ')=' ' and nvl(pd_custprodspec,' ')=' ' and pd_piclass in ('销售退货单', '出货单', '其它出库单', '拨出单', '换货出库单')");
		}

		if ("ProdInOut!CostChange".equals(caller)) {
			baseDao.execute(
					"update prodiodetail set pd_total=round((nvl(pd_price,0)-nvl(pd_orderprice,0))*nvl(pd_orderqty,0),2) where pd_piclass ='成本调整单' and pd_piid=?",
					pi_id);
		}
		// 入库类单据:如果pd_location为空，默认等于物料资料里的仓位pr_location
		if (prodInOutDao.isIn(caller)) {
			baseDao.execute(
					"update prodiodetail set pd_location=(select pr_location from product where pr_code=pd_prodcode) where pd_piid=? and nvl(pd_location,' ')=' '",
					pi_id);
		}
	}

	/*
	 * 提交，审核，过账之前的限制
	 */
	private void checkCommit(String caller, Object pi_id) {
		if (!caller.equals("ProdInOut!AppropriationOut") && !caller.equals("ProdInOut!AppropriationIn")) {
			baseDao.execute("update prodiodetail set pd_whcode=(select pi_whcode from prodinout where pd_piid=pi_id) where pd_piid="
					+ pi_id + " and NVL(pd_whcode,' ')=' '");
		}
		if (caller.equals("ProdInOut!Make!Return") || caller.equals("ProdInOut!OutsideReturn")) {
			baseDao.execute("update prodiodetail a set pd_prodmadedate=(select min(ba_date) from prodiodetail b left join batch on b.pd_batchid=ba_id where a.pd_ordercode=b.pd_ordercode and a.pd_prodcode=b.pd_prodcode and nvl(b.pd_batchcode,' ')<>' ' and ((b.pd_piclass ='生产领料单' and a.pd_piclass='生产退料单') or (b.pd_piclass ='委外领料单' and a.pd_piclass='委外退料单'))) where pd_piid="
					+ pi_id + " and pd_prodmadedate is null");
		} else {
			baseDao.execute("update prodiodetail set pd_prodmadedate=(select pi_date from prodinout where pd_piid=pi_id) where pd_piid="
					+ pi_id + " and pd_prodmadedate is null");
		}
		if ("ProdInOut!CostChange".equals(caller)) {
			baseDao.execute(
					"update prodiodetail set (pd_orderqty,pd_orderprice)=(select ba_remain,ba_price from batch where ba_code=pd_batchcode and pd_prodcode=ba_prodcode and ba_whcode=pd_whcode) where pd_piid=?",
					pi_id);
			baseDao.execute(
					"update prodiodetail set pd_total=round(nvl(pd_orderqty,0)*(nvl(pd_price,0)-nvl(pd_orderprice,0)),2) where pd_piid=?",
					pi_id);
		}
		baseDao.execute("update prodiodetail set pd_qctype=(select ve_class from qua_verifyapplydetail left join qua_verifyapplydetaildet on ved_veid=ve_id where pd_qcid=ved_id) where pd_piid="
				+ pi_id + " and nvl(pd_qcid,0)<>0");
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from ProdIODetail left  join product on pr_code=pd_prodcode  where pd_piid=? and NVL(pr_statuscode,' ')<>'AUDITED'",
						String.class, pi_id);
		if (dets != null) {
			BaseUtil.showError("明细行物料不存在或者状态不等于已审核，不允许进行当前操作！" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from ProdIODetail where pd_piid=? and nvl(pd_whcode,' ')=' '",
						String.class, pi_id);
		if (dets != null) {
			BaseUtil.showError("明细行仓库为空，不允许进行当前操作！" + dets);
		}
		/**
		 * 限制当仓库编号不为空时，明细行仓库至少有一行与主记录一致;为空则不判断
		 */

		/*
		 * 暂不启用该逻辑限制 int c = baseDao.getCountByCondition(
		 * "ProdInOut left join ProdIODetail on pi_id=pd_piid", "pi_id=" + pi_id
		 * +
		 * " and ((NVL(pd_whcode,' ')=nvl(pi_whcode,' ') AND pi_whcode IS NOT NULL) or pi_whcode is null )"
		 * ); if (c < 1) { BaseUtil.showError("无一明细仓库与主表仓库一致，不允许进行当前操作！"); }
		 */
		if (!caller.equals("ProdInOut!StockProfit") && !caller.equals("ProdInOut!StockLoss")) {
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail inner join Prodinout on pd_piid=pi_id left join product on pr_code=pd_prodcode where "
									+ "pd_piid=? and round(pd_outqty+pd_inqty,0)<>pd_outqty+pd_inqty and pd_status=0 and NVL(pr_precision,0)=0  and NVL(pr_groupcode,' ')<>'用品' ",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("计算精度是0的物料不能以小数出入库!行号：" + dets);
			}
		}
		if (baseDao.isDBSetting("warehouseCheck") || baseDao.isDBSetting(caller, "warehouseCheck")) {
			// 出入库单主记录与明细行仓库必须一致
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from ProdIODetail left join ProdInOut on pd_piid=pi_id where pi_id=? and (nvl(pi_whcode,' ')<>nvl(pd_whcode,' ') or nvl(pi_purpose,' ')<>nvl(pd_inwhcode,' '))",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细行仓库与当前单主表仓库不一致，不允许进行当前操作！" + dets);
			}
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(pd_piclass||':'||pd_inoutno||',行:'||pd_pdno||',仓库:'||pd_whcode) from prodiodetail left join warehouse on wh_code=pd_whcode where pd_piid=? and nvl(pd_whcode,' ')<>' ' and nvl(wh_statuscode,' ')='DISABLE'",
						String.class, pi_id);
		if (dets != null) {
			BaseUtil.showError("仓库已禁用，不允许进行当前操作！" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(pd_piclass||':'||pd_inoutno||',行:'||pd_pdno||',仓库:'||pd_whcode) from prodiodetail left join warehouse on wh_code=pd_whcode where pd_piid=? and nvl(pd_whcode,' ')<>' ' and pd_whcode not in (select wh_code from warehouse)",
						String.class, pi_id);
		if (dets != null) {
			BaseUtil.showError("仓库不存在，不允许进行当前操作！" + dets);
		}
		String maxprice = baseDao.getDBSetting(caller, "maxPrice");
		if (maxprice != null) {
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(pd_piclass||':'||pd_inoutno||',行:'||pd_pdno||',单价:'||pd_price) from prodiodetail where pd_piid=? and nvl(pd_price,0)>?",
							String.class, pi_id, maxprice);
			if (dets != null) {
				BaseUtil.showError("单价超过设置上限，不允许进行当前操作！" + dets);
			}
		}
		if ("ProdInOut!Picking".equals(caller) || "ProdInOut!Make!Give".equals(caller) || "ProdInOut!Make!Return".equals(caller)
				|| "ProdInOut!OutsidePicking".equals(caller) || "ProdInOut!OutsideReturn".equals(caller)
				|| "ProdInOut!OSMake!Give".equals(caller) || "ProdInOut!Make!In".equals(caller)
				|| "ProdInOut!PartitionStockIn".equals(caller) || "ProdInOut!OutsideCheckIn".equals(caller)
				|| "ProdInOut!OutesideCheckReturn".equals(caller)) {
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail where pd_piid=? and pd_piclass in ('生产领料单','生产补料单','生产退料单','委外领料单','委外退料单','委外补料单') and not exists (select 1 from make left join makematerial on ma_id=mm_maid where pd_ordercode=ma_code and pd_orderdetno=mm_detno)",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("工单+序号不存在，不允许进行当前操作！行号：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail left join Prodinout on pd_piid=pi_id left join make on ma_code=pd_ordercode where "
									+ "pd_piid=? and nvl(pi_cardcode,' ')<>nvl(ma_vendcode,' ') and pd_piclass in ('委外退料单','委外验退单','委外领料单') "
									+ "and nvl(pd_ordercode,' ')<>' ' and not exists (select 1 from makecraft where mc_code=pd_jobcode and mc_tasktype='工序委外单')",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细委外单委外商与单据委外商不一致，不允许进行当前操作!行号：" + dets);
			}

			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail where nvl(pd_jobcode,' ')<>' ' and not exists (select 1 from makecraft where pd_jobcode=mc_code "
									+ "and mc_tasktype='工序委外单') and pd_piclass in ('委外退料单','委外验退单','委外领料单') and pd_piid=?", String.class,
							pi_id);
			if (dets != null) {
				BaseUtil.showError("明细工序委外单不存在，不允许进行当前操作!行号：" + dets);
			}

			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail left join Prodinout on pd_piid=pi_id left join makecraft on mc_code=pd_jobcode where "
									+ "pd_piid=? and nvl(pi_cardcode,' ')<>nvl(mc_vendcode,' ') and pd_piclass in ('委外退料单','委外验退单','委外领料单') "
									+ "and nvl(pd_ordercode,' ')<>' ' and exists (select 1 from makecraft where mc_code=pd_jobcode and mc_tasktype='工序委外单') ",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("委外商与工序委外单委外商不一致，不允许进行当前操作!行号：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat('工单：'||pd_ordercode||'序号:'||pd_orderdetno) from (select pd_ordercode,pd_orderdetno,sum(pd_inqty) pd_inqty,max(mm_yqty)mm_yqty,max(mm_gqty)mm_gqty from ProdIODetail left join make on ma_code=pd_ordercode left join makematerial on ma_id=mm_maid and mm_detno=pd_orderdetno where pd_piid=? and pd_piclass='拆件入库单'  group by pd_ordercode,pd_orderdetno)A where mm_yqty+pd_inqty>mm_gqty and mm_gqty>0 ",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("拆件入库数不能大于工单允许可拆件入库数！" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat('行号：'||pd_pdno||'工单：'||pd_ordercode) from ProdIODetail left join make on ma_code=pd_ordercode  left join makekind ON mk_name=ma_kind where pd_piid=? and pd_piclass='拆件入库单'  and  mk_type='S'",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("拆件单明细中存在制造单的类型是标准！" + dets);
			}

			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from prodinout left join  ProdIODetail  on pd_piid=pi_id  left join make on ma_code=pd_ordercode  where  pd_piid=? and NVL(ma_statuscode,' ')<>'AUDITED' and pi_statuscode='UNPOST' and pd_piclass in ('完工入库单','拆件入库单','委外验收单','生产领料单','生产补料单','生产退料单','委外领料单','委外退料单','委外补料单') and nvl(pd_ordercode,' ')<>' ' ",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("工单不存在或者状态不等于已审核，不允许进行当前操作！行号：" + dets);
			}

			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail left join Prodinout on pd_piid=pi_id left join make on ma_code=pd_ordercode  left join MakeKind on mk_name=ma_kind "
									+ "where pd_piid=? and nvl(pd_whcode,' ')<>' ' and nvl(mk_whcodes,' ')<>' ' and pd_whcode not in (select column_value from table(parsestring(mk_whcodes,'#'))) "
									+ "and pd_piclass in ('完工入库单','委外验收单') and nvl(pd_ordercode,' ')<>' ' and nvl(ProdIODetail.pd_status,0)=0",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细仓库与工单类型允许入库仓库不一致，不允许进行当前操作!行号：" + dets);
			}
		}
		if ("ProdInOut!OutesideCheckReturn".equals(caller)) {
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail where pd_piid=? and pd_piclass in ('委外验退单') and not exists (select 1 from make where pd_ordercode=ma_code and pd_prodcode=ma_prodcode) "
									+ "and not exists (select 1 from makecraft where pd_jobcode=mc_code and mc_tasktype='工序委外单' and pd_prodcode=mc_prodcode)",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("委外单+物料编号不存在，不允许进行当前操作！行号：" + dets);
			}

			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail left join Prodinout on pd_piid=pi_id left join Make on ma_code=pd_ordercode where pd_piid=? and nvl(pi_currency,' ')<>nvl(ma_currency,' ')"
									+ " and not exists (select 1 from makecraft where pd_jobcode=mc_code and mc_tasktype='工序委外单')"
									+ " and pd_piclass in ('委外验退单') and nvl(pd_ordercode,' ')<>' '", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细委外单与单据币别不一致，不允许进行当前操作!行号：" + dets);
			}

			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(pd_pdno) from ProdIODetail left join Prodinout on pd_piid=pi_id left join MakeCraft on mc_makecode=pd_ordercode "
							+ " and mc_code =pd_jobcode where pd_piid=? and nvl(pi_currency,' ')<>nvl(mc_currency,' ')"
							+ " and exists (select 1 from makecraft where pd_jobcode=mc_code and mc_tasktype='工序委外单')"
							+ " and pd_piclass in ('委外验退单') and nvl(pd_ordercode,' ')<>' '", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细委外单与单据币别不一致，不允许进行当前操作!行号：" + dets);
			}
		}
		if ("ProdInOut!PurcCheckin".equals(caller) || "ProdInOut!PurcCheckout".equals(caller) || "ProdInOut!DefectIn".equals(caller)
				|| "ProdInOut!DefectOut".equals(caller)) {
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pi_inoutno) from ProdInOut where pi_id=? and pi_class in ('采购验收单','采购验退单') and (pi_cardcode,pi_title) not in (select ve_code,ve_name from vendor)",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("单据中供应商编号，供应商名称在供应商资料中不存在!");
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pi_inoutno) from ProdInOut where pi_id=? and pi_class in ('采购验收单','采购验退单') and (pi_receivecode,pi_receivename) not in (select ve_code,ve_name from vendor)",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("单据中应付供应商编号，应付供应商名称在供应商资料中不存在!");
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail where pd_piid=?"
									+ " and pd_piclass in ('采购验收单','采购验退单') and nvl(pd_ordercode,' ')<>' ' and  not exists (select pd_code,pd_detno from purchasedetail where pd_code=ProdIODetail.pd_ordercode and pd_detno=ProdIODetail.pd_orderdetno)",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("采购单号+采购序号不存在，不允许进行当前操作!行号：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail where pd_piid=?"
									+ " and  not exists(select pd_code,pd_detno from purchasedetail p  where p.pd_prodcode=ProdIODetail.pd_prodcode and p.pd_code=ProdIODetail.pd_ordercode and  p.pd_detno=ProdIODetail.pd_orderdetno ) and pd_piclass in ('采购验收单','采购验退单') and nvl(pd_ordercode,' ')<>' ' ",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("采购单号+采购序号不存在，不允许进行当前操作!行号：" + dets);
			}
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(pd_pdno) from ProdIODetail left join PurchaseDetail on pd_ordercode=pd_code and pd_orderdetno=pd_detno where "
							+ "pd_piid=? and nvl(pd_mrpstatuscode,' ') in ('FREEZE','FINISH') and pd_piclass in ('采购验收单','采购验退单') "
							+ "and nvl(pd_ordercode,' ')<>' '", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("采购单明细已冻结或者已结案，不允许进行当前操作!行号：" + dets);
			}
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(pd_pdno) from ProdIODetail left join PurchaseDetail on pd_ordercode=pd_code and pd_orderdetno=pd_detno where "
							+ "pd_piid=? and nvl(pd_mrpstatuscode,' ') in ('FREEZE','FINISH') and pd_piclass in ('不良品入库单','不良品出库单') "
							+ "and nvl(pd_ordercode,' ')<>' ' and nvl(pd_qctype,' ')='采购检验单'", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("采购单明细已冻结或者已结案，不允许进行当前操作!行号：" + dets);
			}
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(pd_pdno) from ProdIODetail left join Make on pd_ordercode=ma_code where "
							+ "pd_piid=? and nvl(ma_statuscode,' ') in ('FREEZE','FINISH') and pd_piclass in ('不良品入库单','不良品出库单') "
							+ "and nvl(pd_ordercode,' ')<>' ' and nvl(pd_qctype,' ')='委外检验单'", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细行委外单已冻结或者已结案，不允许进行当前操作!行号：" + dets);
			}
			if (!baseDao.isDBSetting(caller, "noLimitVendor")) {
				dets = baseDao.getJdbcTemplate().queryForObject(
						"select wm_concat(pd_pdno) from ProdIODetail left join Prodinout on pd_piid=pi_id left join Purchase on pu_code=pd_ordercode where "
								+ "pd_piid=? and nvl(pi_cardcode,' ')<>nvl(pu_vendcode,' ') and pd_piclass in ('采购验收单','采购验退单') "
								+ "and nvl(pd_ordercode,' ')<>' '", String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("明细采购单与单据供应商不一致，不允许进行当前操作!行号：" + dets);
				}
			}
			if (!baseDao.isDBSetting(caller, "allowARCust")) {
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_pdno) from ProdIODetail left join Prodinout on pd_piid=pi_id left join Purchase on pu_code=pd_ordercode where "
										+ "pd_piid=? and nvl(pi_receivecode,' ')<>nvl(pu_receivecode,' ') and pd_piclass in ('采购验收单','采购验退单') and nvl(pd_ordercode,' ')<>' '",
								String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("明细采购单与单据应付供应商不一致，不允许进行当前操作!行号：" + dets);
				}
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_pdno) from ProdIODetail left join Prodinout on pd_piid=pi_id left join Purchase on pu_code=pd_ordercode where pd_piid=? and nvl(pi_currency,' ')<>nvl(pu_currency,' ')"
										+ " and pd_piclass in ('采购验收单','采购验退单') and nvl(pd_ordercode,' ')<>' '", String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("明细采购单与单据币别不一致，不允许进行当前操作!行号：" + dets);
				}
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat('行号：'||pd_pdno||'收料合格数量：'||ved_okqty) from ProdIODetail left join QUA_VerifyApplyDetailDet on pd_qcid=ved_id where pd_piid=? and nvl(pd_qcid,0)<>0 and nvl(pd_inqty,0)>nvl(ved_okqty,0) and pd_piclass in ('采购验收单')",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细行入库数量大于收料合格数量!" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat('行号：'||pd_pdno||'收料不合格数量：'||ved_ngqty) from ProdIODetail left join QUA_VerifyApplyDetailDet on pd_qcid=ved_id where pd_piid=? and nvl(pd_qcid,0)<>0 and nvl(pd_inqty,0)>nvl(ved_ngqty,0) and pd_piclass in ('不良品入库单')",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细行入库数量大于收料不合格数量!" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail left join Prodinout on pd_piid=pi_id left join Purchase on pu_code=pd_ordercode left join purchasekind on pk_name=pu_kind "
									+ "where pd_piid=? and nvl(pd_whcode,' ')<>' ' and nvl(pk_whcodes,' ')<>' ' and pd_whcode not in (select column_value from table(parsestring(pk_whcodes,'#'))) "
									+ "and pd_piclass in ('采购验收单') and nvl(pd_ordercode,' ')<>' '", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细仓库与采购单类型允许入仓库不一致，不允许进行当前操作!行号：" + dets);
			}
			if ("ProdInOut!PurcCheckin".equals(caller)) {
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_pdno) from ProdIODetail left join PurchaseDetail on PurchaseDetail.pd_code=ProdIODetail.pd_ordercode and PurchaseDetail.pd_detno=ProdIODetail.pd_orderdetno "
										+ "where pd_piid=? and nvl(PurchaseDetail.pd_yqty,0)>nvl(PurchaseDetail.pd_qty,0)-nvl(PurchaseDetail.pd_frozenqty,0) "
										+ "and pd_piclass in ('采购验收单') and nvl(ProdIODetail.pd_ordercode,' ')<>' '", String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("明细采购订单已转数大于采购订单数量-已冻结数量，不允许进行当前操作!行号：" + dets);
				}
			}
		}
		if ("ProdInOut!SaleBorrow".equals(caller) || "ProdInOut!OtherPurcIn".equals(caller)) {
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from ProdIODetail left join Prodinout on pd_piid=pi_id left join BorrowCargoType on bt_name=pi_outtype "
									+ "where pd_piid=? and nvl(pd_whcode,' ')<>' ' and nvl(bt_whcodes,' ')<>' ' and pd_whcode not in (select column_value from table(parsestring(bt_whcodes,'#'))) "
									+ "and pd_piclass in ('借货出货单','其它采购入库单')", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细仓库与借货类型允许出入仓库不一致，不允许进行当前操作！" + dets);
			}
		}
		if ("ProdInOut!Sale".equals(caller) || "ProdInOut!SaleReturn".equals(caller) || "ProdInOut!OtherOut".equals(caller)
				|| "ProdInOut!ExchangeOut".equals(caller) || "ProdInOut!SaleAppropriationOut".equals(caller)) {
			if (baseDao.isDBSetting(caller, "allowDifProd")) {
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_pdno) from ProdIODetail where pd_piid=? and not exists(select sd_code,sd_detno from saledetail where sd_code=pd_ordercode and sd_detno=pd_orderdetno and sd_statuscode='AUDITED')"
										+ " and pd_piclass in ('出货单','销售退货单','其它出库单','换货出库单','销售拨出单') and nvl(pd_ordercode,' ')<>' ' and nvl(pd_ioid,0)=0",
								String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("销售单号+销售序号不存在或者状态不等于已审核，不允许进行当前操作!行号：" + dets);
				}
			} else if (baseDao.isDBSetting(caller, "SaleWithProdRelation")) {
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_pdno) from ProdIODetail where pd_piid=? and "
										+ "not exists (select sd_code,sd_detno from saledetail left join ProdRelation on (sd_prodcode=prr_soncode or (nvl(prr_ifonewa,0)=0 and sd_prodcode=prr_repcode)) "
										+ "where sd_code=pd_ordercode and sd_detno=pd_orderdetno and (nvl(sd_prodcode,' ')=nvl(pd_prodcode,' ') or nvl(prr_repcode ,' ')=nvl(pd_prodcode,' ') or nvl(prr_soncode ,' ')=nvl(pd_prodcode,' ')))"
										+ "	and pd_piclass in ('出货单','销售退货单','其它出库单','换货出库单','销售拨出单') and nvl(pd_ordercode,' ')<>' ' and nvl(pd_ioid,0)=0",
								String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("销售单号+销售序号+物料编号不存在或者状态不等于已审核，不允许进行当前操作!行号：" + dets);
				}
			} else {
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_pdno) from ProdIODetail where pd_piid=? and not exists(select sd_code,sd_detno from saledetail where sd_prodcode=pd_prodcode and sd_code=pd_ordercode and sd_detno=pd_orderdetno )"
										+ " and pd_piclass in ('出货单','销售退货单','其它出库单','换货出库单','销售拨出单') and nvl(pd_ordercode,' ')<>' ' and nvl(pd_ioid,0)=0",
								String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("销售单号+销售序号+物料编号不存在，不允许进行当前操作!行号：" + dets);
				}
			}
			if (baseDao.isDBSetting("useMachineNo")) {
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat('物料'||pd_prodcode||'总数量'||qty||'已采集数'||yqty) from (SELECT pd_piid, pd_prodcode, pd_inoutno, nvl(sum(nvl(pd_inqty,0)+nvl(pd_outqty,0)),0) qty from prodiodetail where pd_piid=? group by pd_piid,pd_inoutno,pd_prodcode) left join (select pim_piid,pim_prodcode,count(1) yqty from prodiomac group by pim_piid,pim_prodcode) on pim_piid=pd_piid and pim_prodcode=pd_prodcode where nvl(qty,0)<>nvl(yqty,0) and nvl(yqty,0)<>0",
								String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("单据+物料数量跟机器号数量不一致，不允许进行当前操作!行号：" + dets);
				}
			}
			if (!(baseDao.isDBSetting("ProdInOut!Sale", "custatus"))) {
				if (baseDao.isDBSetting("Sale", "zeroOutWhenHung")) {// 客户挂起时，订单单价为0不限制出货
					dets = baseDao
							.getJdbcTemplate()
							.queryForObject(
									"select wm_concat(pi_cardcode) from ProdInOut left join customer on pi_cardcode=cu_code where pi_id = ? and pi_class in ('出货单') "
											+ "and cu_status='挂起' and not exists (select 1 from prodiodetail left join saledetail on pd_ordercode=sd_code and pd_orderdetno=sd_detno"
											+ " where pd_piid=pi_id and nvl(pd_ordercode,' ')<>' ' and nvl(sd_price,0)=0)", String.class,
									pi_id);
					if (dets != null) {
						BaseUtil.showError("客户资料状态为挂起，且订单单价不为0，不允许进行当前操作!客户号：" + dets);
					}
				} else {
					dets = baseDao
							.getJdbcTemplate()
							.queryForObject(
									"select wm_concat(pi_cardcode) from ProdInOut left join customer on pi_cardcode=cu_code where pi_id = ? and pi_class in ('出货单','其它出库单','换货出库单','销售拨出单') and cu_status='挂起'",
									String.class, pi_id);
					if (dets != null) {
						BaseUtil.showError("客户资料状态为挂起，不允许进行当前操作!客户号：" + dets);
					}
				}

			}
			if (!baseDao.isDBSetting(caller, "allowARCust")) {
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_pdno) from ProdIODetail left join Prodinout on pd_piid=pi_id left join Sale on sa_code=pd_ordercode where pd_piid=? and nvl(pi_cardcode,' ')<>nvl(sa_custcode,' ')"
										+ " and pd_piclass in ('出货单','销售退货单','其它出库单','换货出库单','销售拨出单') and nvl(pd_ordercode,' ')<>' ' and nvl(pd_ioid,0)=0",
								String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("明细销售单与单据客户不一致，不允许进行当前操作!行号：" + dets);
				}
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_pdno) from ProdIODetail left join Prodinout on pd_piid=pi_id left join Sale on sa_code=pd_ordercode where pd_piid=? and nvl(pi_arcode,' ')<>nvl(sa_apcustcode,' ')"
										+ " and pd_piclass in ('出货单','销售退货单') and nvl(pd_ordercode,' ')<>' '", String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("明细销售单与单据应收客户不一致，不允许进行当前操作!行号：" + dets);
				}
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_pdno) from ProdIODetail left join Prodinout on pd_piid=pi_id left join Sale on sa_code=pd_ordercode where pd_piid=? and nvl(pi_currency,' ')<>nvl(sa_currency,' ')"
										+ " and pd_piclass in ('出货单','销售退货单') and nvl(pd_ordercode,' ')<>' '", String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("明细销售单与单据币别不一致，不允许进行当前操作!行号：" + dets);
				}
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pi_inoutno) from ProdInOut where pi_id=? and pi_class in ('出货单','销售退货单') and (pi_cardcode,pi_title) not in (select cu_code,cu_name from customer)",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("单据中客户编号，客户名称在客户资料中不存在!");
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pi_inoutno) from ProdInOut where pi_id=? and pi_class in ('出货单','销售退货单') and (pi_arcode,pi_arname) not in (select cu_code,cu_name from customer)",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("单据中应收客户编号，应收客户名称在客户资料中不存在!");
			}
			// 销售退货单：退货数量，如退货数量+其它未过账的退货单数量大于销售订单出货数
			/**
			 * 需考虑状态
			 */
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail b left join SaleDetail on sd_code=pd_ordercode and sd_detno=pd_orderdetno where pd_piid=? and pd_status<99 and pd_piclass='销售退货单' and nvl(pd_ordercode,' ')<>' ' and nvl(pd_inqty,0) + (select nvl(sum(nvl(a.pd_inqty,0)),0) from ProdIODetail a where a.pd_piclass='销售退货单' and a.pd_status<99 and a.pd_ordercode=b.pd_ordercode and a.pd_orderdetno=b.pd_orderdetno and a.pd_id <> b.pd_id) > nvl(sd_sendqty,0)",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("本次退货数+其它退货数合计大于订单的已发货数量，不允许进行当前操作！行号：" + dets);
			}
			if ("ProdInOut!SaleAppropriationOut".equals(caller) || "ProdInOut!OtherOut".equals(caller)) {
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from ProdIODetail where pd_piid=? and (pd_plancode,pd_forecastdetno) not in (select sd_code, sd_detno from saleforecastdetail,saleforecast where sf_id=sd_sfid and sf_code=pd_plancode and sd_detno=pd_forecastdetno and sd_statuscode='AUDITED') and nvl(pd_plancode,' ')<>' '",
								String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("明细行预测单号+预测序号不存在或者状态不等于已审核，不允许进行当前操作!！" + dets);
				}
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from ProdIODetail where pd_piid=? and (pd_plancode,pd_forecastdetno) in (select sd_code, sd_detno from saleforecastdetail where nvl(sd_qty,0)=0) and nvl(pd_plancode,' ')<>' '",
								String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("明细行预测单号+预测序号预测数量等于0，不允许进行当前操作！" + dets);
				}
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from ProdIODetail left join SaleDetail on pd_ordercode=sd_code and pd_orderdetno=sd_detno where pd_piid=? and pd_piclass in ('销售拨出单','其它出库单') and nvl(pd_outqty+pd_inqty,0)>nvl(sd_qty,0)-nvl(sd_yqty,0) and nvl(pd_ordercode,' ')<>' ' and nvl(pd_ioid,0)=0",
								String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("明细行销售单号+销售序号数量大于订单未发货数，不允许进行当前操作！" + dets);
				}
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from ProdIODetail where pd_piid=? and pd_piclass='销售拨出单' and pd_plancode not in (select a.sf_code from saleforecast a left join saleforecastkind b on (a.sf_kind=b.sf_name or a.sf_kind=b.sf_code) where a.sf_code=pd_plancode and sf_clashoption in ('SEND','发货冲销')) and nvl(pd_plancode,' ')<>' '",
								String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("预测单录入错误，该预测类型不属于发货冲销！" + dets);
				}
			}
		}
		if ("ProdInOut!AppropriationOut".equals(caller) || "ProdInOut!AppropriationIn".equals(caller)
				|| "ProdInOut!SaleAppropriationOut".equals(caller) || "ProdInOut!SaleAppropriationIn".equals(caller)) {
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail,warehouse w1,warehouse w2 where pd_piid=? and pd_whcode=w1.wh_code and pd_inwhcode=w2.wh_code and nvl(w1.wh_nocost,0)<>nvl(w2.wh_nocost,0)",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("有值和无值仓之间不能相互调拨!行号：" + dets);
			}
		}
		SqlRowList rs = baseDao.queryForRowSet("select * from prodiodetail where pd_piid=?", pi_id);
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			int pdid = rs.getInt("pd_id");
			int ioid = rs.getGeneralInt("pd_ioid");
			double tqty = rs.getGeneralDouble("pd_inqty") + rs.getGeneralDouble("pd_outqty");
			double yqty = 0.0;
			if (ioid > 0) {
				yqty = baseDao.getSummaryByField("Prodiodetail", "nvl(pd_outqty,0) + nvl(pd_inqty,0)", "pd_ioid=" + ioid + " and pd_id<>"
						+ pdid);
				Object[] source = baseDao.getFieldsDataByCondition("Prodiodetail", new String[] { "nvl(pd_outqty,0)", "nvl(pd_inqty,0)",
						"pd_piclass", "pd_inoutno" }, "pd_id=" + ioid);
				if (source != null) {
					if (yqty + tqty > Double.parseDouble(source[0].toString()) + Double.parseDouble(source[1].toString())) {
						sb.append("行号:" + rs.getInt("pd_pdno") + ",数量:" + tqty + ",无法转出." + source[2] + "[" + source[3] + "]已转数量:" + yqty
								+ ",本次数量:" + tqty + "<hr/>");
					}
				}
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
		// 保税信息判断
		if (baseDao.isDBSetting("ioWHBondedCheck")) {
			if ("ProdInOut!PurcCheckin".equals(caller) || "ProdInOut!PurcCheckout".equals(caller)) {
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_pdno) from ProdIODetail left join Prodinout on pd_piid=pi_id left join PurchaseDetail on pd_code=pd_ordercode and pd_detno=pd_orderdetno left join warehouse on ProdIODetail.pd_whcode=wh_code where pd_piid=? and nvl(PurchaseDetail.pd_bonded,0)<>nvl(wh_bonded,0)"
										+ " and pd_piclass in ('采购验收单','采购验退单') and nvl(pd_ordercode,' ')<>' '", String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("采购单的保税属性与仓库的保税属性不一致，不允许进行当前操作!行号：" + dets);
				}
			}
			if ("ProdInOut!OutesideCheckReturn".equals(caller) || "ProdInOut!OutsideCheckIn".equals(caller)) {
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_pdno) from ProdIODetail left join Prodinout on pd_piid=pi_id left join Make on ma_code=pd_ordercode left join warehouse on pd_whcode=wh_code where pd_piid=? and nvl(ma_bonded,0)<>nvl(wh_bonded,0)"
										+ " and pd_piclass in ('委外验退单','委外验收单') and nvl(pd_ordercode,' ')<>' '", String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("委外单的保税属性与仓库的保税属性不一致，不允许进行当前操作!行号：" + dets);
				}
			}
			if ("ProdInOut!Make!In".equals(caller) || "ProdInOut!PartitionStockIn".equals(caller)) {
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_pdno) from ProdIODetail left join Prodinout on pd_piid=pi_id left join Make on ma_code=pd_ordercode left join warehouse on pd_whcode=wh_code where pd_piid=? and nvl(ma_bonded,0)<>nvl(wh_bonded,0)"
										+ " and pd_piclass in ('完工入库单','拆件入库单') and nvl(pd_ordercode,' ')<>' '", String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("工单的保税属性与仓库的保税属性不一致，不允许进行当前操作!行号：" + dets);
				}
			}
			if ("ProdInOut!Make!Give".equals(caller) || "ProdInOut!Make!Return".equals(caller) || "ProdInOut!Picking".equals(caller)
					|| "ProdInOut!OSMake!Give".equals(caller) || "ProdInOut!OutsideReturn".equals(caller)
					|| "ProdInOut!OutsidePicking".equals(caller)) {
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from ProdIODetail left join Prodinout on pd_piid=pi_id left join Make on ma_code=pd_ordercode left join warehouse on pd_whcode=wh_code where pd_piid=? and nvl(ma_bonded,0)=0 and nvl(wh_bonded,0)<>0"
										+ " and pd_piclass in ('委外补料单','委外退料单','委外领料单') and nvl(pd_ordercode,' ')<>' '", String.class,
								pi_id);
				if (dets != null) {
					BaseUtil.showError("委外单保税属性是非保税，不能发生仓库保税属性是保税的单据！" + dets);
				}
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from ProdIODetail left join Prodinout on pd_piid=pi_id left join Make on ma_code=pd_ordercode left join warehouse on pd_whcode=wh_code where pd_piid=? and nvl(ma_bonded,0)=0 and nvl(wh_bonded,0)<>0"
										+ " and pd_piclass in ('生产补料单','生产退料单','生产领料单') and nvl(pd_ordercode,' ')<>' '", String.class,
								pi_id);
				if (dets != null) {
					BaseUtil.showError("工单保税属性是非保税，不能发生仓库保税属性是保税的单据！" + dets);
				}
			}
			if ("ProdInOut!SaleAppropriationOut".equals(caller) || "ProdInOut!AppropriationOut".equals(caller)) {
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from ProdIODetail left join Prodinout on pd_piid=pi_id left join warehouse a on a.wh_code=pd_whcode left join warehouse b on b.wh_code=pd_inwhcode where pd_piid=? and nvl(a.wh_bonded,0)<>nvl(b.wh_bonded,0)"
										+ " and pd_piclass in ('拨出单','销售拨出单') and nvl(pd_whcode,' ')<>' ' and nvl(pd_inwhcode,' ')<>' '",
								String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("拨出仓库与拨入仓库仓库的保税属性不一致，不能进行当前操作！" + dets);
				}
			}
		}

		// @add20180605 Q:2018050126 最后一张完工入库单或者委外验收单，提交、审核、过账之前限制，不允许存在未过账
		// 领、退、补单据,未审核的报废单，未过账的委外验退单
		if (("ProdInOut!OutsideCheckIn".equals(caller) || "ProdInOut!Make!In".equals(caller))
				&& baseDao.isDBSetting(caller, "checkRelBillsStatus")) {
			// 判断哪些工单是最后一张完工入库单
			rs = baseDao
					.queryForRowSet(
							"select pd_ordercode,pd_pdno from (select pd_ordercode,sum(pd_inqty)inqty,wm_concat(pd_pdno)pd_pdno from prodiodetail  where "
									+ "pd_piid=? and pd_piclass in('完工入库单','委外验收单') group by pd_ordercode) A left join make on ma_code=A.pd_ordercode "
									+ "where nvl(ma_madeqty,0)+inqty>=ma_qty", pi_id);
			String errors = null;
			while (rs.next()) {
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pi_class||':'||pi_inoutno) from prodiodetail,ProdInOut WHERE pd_piid=pi_id AND pi_id<>? and pd_ordercode = ? AND pi_statuscode = 'UNPOST' AND pd_status=0 AND (pd_piclass LIKE '生产%' OR pd_piclass LIKE '委外%' OR pd_piclass LIKE '完工%') and rownum<15",
								String.class, pi_id, rs.getString("pd_ordercode"));
				if (dets != null) {
					errors = "行号：" + rs.getString("pd_pdno") + ",工单:" + rs.getString("pd_ordercode") + "存在关联未过账单据!" + dets;
				}
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"SELECT wm_concat(ms_code) FROM MakeScrapdetail,MakeScrap WHERE md_msid=ms_id AND md_mmcode=? AND ms_statuscode in ('ENTERING','COMMITED') AND nvl(md_status,0)=0",
								String.class, rs.getString("pd_ordercode"));
				if (dets != null) {
					if (errors != null) {
						errors += "<br>未审核的报废单：" + dets;
					} else {
						errors = "行号：" + rs.getString("pd_pdno") + ",工单:" + rs.getString("pd_ordercode") + "存在关联未审核的报废单：" + dets;
					}
				}
				if (errors != null && !"".equals(errors)) {
					BaseUtil.showError(errors);
				}
			}
		}
	}

	/*
	 * 打印之前的限制
	 */
	private void checkPrint(String caller, Object pi_id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from ProdIODetail where pd_piid=? and not exists(select pr_code from product where pr_code=pd_prodcode and NVL(pr_statuscode,' ')='AUDITED')",
						String.class, pi_id);
		if (dets != null) {
			BaseUtil.showError("明细行物料不存在或者状态不等于已审核，不允许进行当前操作！" + dets);
		}
		if (baseDao.isDBSetting("warehouseCheck") || baseDao.isDBSetting(caller, "warehouseCheck")) {
			// 出入库单主记录与明细行仓库必须一致
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from ProdIODetail left join ProdInOut on pd_piid=pi_id where pi_id=? and (nvl(pi_whcode,' ')<>nvl(pd_whcode,' ') or nvl(pi_purpose,' ')<>nvl(pd_inwhcode,' '))",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细行仓库与当前单主表仓库不一致，不允许进行当前操作！" + dets);
			}
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(pd_piclass||':'||pd_inoutno||',行:'||pd_pdno||',仓库:'||pd_whcode) from prodiodetail left join warehouse on wh_code=pd_whcode where pd_piid=? and nvl(pd_whcode,' ')<>' ' and nvl(wh_statuscode,' ')='DISABLE'",
						String.class, pi_id);
		if (dets != null) {
			BaseUtil.showError("仓库已禁用，不允许进行当前操作！" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(pd_piclass||':'||pd_inoutno||',行:'||pd_pdno||',仓库:'||pd_whcode) from prodiodetail left join warehouse on wh_code=pd_whcode where pd_piid=? and nvl(pd_whcode,' ')<>' ' and pd_whcode not in (select wh_code from warehouse)",
						String.class, pi_id);
		if (dets != null) {
			BaseUtil.showError("仓库不存在，不允许进行当前操作！" + dets);
		}
		String maxprice = baseDao.getDBSetting(caller, "maxPrice");
		if (maxprice != null) {
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(pd_piclass||':'||pd_inoutno||',行:'||pd_pdno||',单价:'||pd_price) from prodiodetail where pd_piid=? and nvl(pd_price,0)>?",
							String.class, pi_id, maxprice);
			if (dets != null) {
				BaseUtil.showError("单价超过设置上限，不允许进行当前操作！" + dets);
			}
		}
		if ("ProdInOut!Picking".equals(caller) || "ProdInOut!Make!Give".equals(caller) || "ProdInOut!Make!Return".equals(caller)
				|| "ProdInOut!OutsidePicking".equals(caller) || "ProdInOut!OutsideReturn".equals(caller)
				|| "ProdInOut!OSMake!Give".equals(caller) || "ProdInOut!Make!In".equals(caller)
				|| "ProdInOut!PartitionStockIn".equals(caller) || "ProdInOut!OutsideCheckIn".equals(caller)
				|| "ProdInOut!OutesideCheckReturn".equals(caller)) {
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail where pd_piid=? and pd_piclass in ('生产领料单','生产补料单','生产退料单','委外领料单','委外退料单','委外补料单') and not exists (select 1 from make left join makematerial on ma_id=mm_maid where pd_ordercode=ma_code and pd_orderdetno=mm_detno)",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("工单+序号不存在，不允许进行当前操作！行号：" + dets);
			}
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(pd_pdno) from ProdIODetail left join Prodinout on pd_piid=pi_id left join make on ma_code=pd_ordercode where "
							+ "pd_piid=? and nvl(pi_cardcode,' ')<>nvl(ma_vendcode,' ') and pd_piclass in ('委外退料单','委外验退单','委外领料单') "
							+ "and nvl(pd_ordercode,' ')<>' ' and nvl(ProdIODetail.pd_status,0)=0", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细委外单委外商与单据委外商不一致，不允许进行当前操作!行号：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat('工单：'||pd_ordercode||'序号:'||pd_orderdetno) from (select pd_ordercode,pd_orderdetno,sum(pd_inqty) pd_inqty,max(mm_yqty)mm_yqty,max(mm_gqty)mm_gqty from ProdIODetail left join make on ma_code=pd_ordercode left join makematerial on ma_id=mm_maid and mm_detno=pd_orderdetno where pd_piid=? and pd_piclass='拆件入库单'  group by pd_ordercode,pd_orderdetno)A where mm_yqty+pd_inqty>mm_gqty and mm_gqty>0 ",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("拆件入库数不能大于工单允许可拆件入库数！" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat('行号：'||pd_pdno||'工单：'||pd_ordercode) from ProdIODetail left join make on ma_code=pd_ordercode  left join makekind ON mk_name=ma_kind where pd_piid=? and pd_piclass='拆件入库单'  and  mk_type='S'",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("拆件单明细中存在制造单的类型是标准！" + dets);
			}

			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from prodinout left join  ProdIODetail  on pd_piid=pi_id  left join make on ma_code=pd_ordercode  where  pd_piid=? and NVL(ma_statuscode,' ')<>'AUDITED' and nvl(ProdIODetail.pd_status,0)=0 and pd_piclass in ('完工入库单','拆件入库单','委外验收单','生产领料单','生产补料单','生产退料单','委外领料单','委外退料单','委外补料单') and nvl(pd_ordercode,' ')<>' ' ",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("工单不存在或者状态不等于已审核，不允许进行当前操作！行号：" + dets);
			}
		}
		if ("ProdInOut!OutesideCheckReturn".equals(caller)) {
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail where pd_piid=? and pd_piclass in ('委外验退单') and not exists (select 1 from make where pd_ordercode=ma_code and pd_prodcode=ma_prodcode)",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("委外单+物料编号不存在，不允许进行当前操作！行号：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail left join Prodinout on pd_piid=pi_id left join Make on ma_code=pd_ordercode where pd_piid=? and nvl(pi_currency,' ')<>nvl(ma_currency,' ')"
									+ " and pd_piclass in ('委外验退单') and nvl(pd_ordercode,' ')<>' ' and nvl(ProdIODetail.pd_status,0)=0",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细委外单与单据币别不一致，不允许进行当前操作!行号：" + dets);
			}
		}
		if ("ProdInOut!PurcCheckin".equals(caller) || "ProdInOut!PurcCheckout".equals(caller) || "ProdInOut!DefectIn".equals(caller)
				|| "ProdInOut!DefectOut".equals(caller)) {
			/*
			 * dets = baseDao .getJdbcTemplate() .queryForObject(
			 * "select wm_concat(pi_inoutno) from ProdInOut where pi_id=? and pi_class in ('采购验收单','采购验退单') and (pi_cardcode,pi_title) not in (select ve_code,ve_name from vendor)"
			 * , String.class, pi_id); if (dets != null) {
			 * BaseUtil.showError("单据中供应商编号，供应商名称在供应商资料中不存在!"); } dets = baseDao
			 * .getJdbcTemplate() .queryForObject(
			 * "select wm_concat(pi_inoutno) from ProdInOut where pi_id=? and pi_class in ('采购验收单','采购验退单') and (pi_receivecode,pi_receivename) not in (select ve_code,ve_name from vendor)"
			 * , String.class, pi_id); if (dets != null) {
			 * BaseUtil.showError("单据中应付供应商编号，应付供应商名称在供应商资料中不存在!"); }
			 */
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail where pd_piid=?"
									+ " and not exists (select pd_code,pd_detno from purchasedetail where pd_code=ProdIODetail.pd_ordercode and pd_detno=ProdIODetail.pd_orderdetno) and pd_piclass in ('采购验收单','采购验退单') and nvl(pd_ordercode,' ')<>' ' ",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("采购单号+采购序号不存在，不允许进行当前操作!行号：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail where  pd_piid=? and  not exists(select pd_code,pd_detno from purchasedetail p  where p.pd_prodcode=ProdIODetail.pd_prodcode and p.pd_code=ProdIODetail.pd_ordercode and  p.pd_detno=ProdIODetail.pd_orderdetno ) and pd_piclass in ('采购验收单','采购验退单') and nvl(pd_ordercode,' ')<>' '",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("采购单号+采购序号+物料编号不存在，不允许进行当前操作!行号：" + dets);
			}
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(pd_pdno) from ProdIODetail left join PurchaseDetail on pd_ordercode=pd_code and pd_orderdetno=pd_detno where "
							+ "pd_piid=? and nvl(pd_mrpstatuscode,' ') in ('FREEZE','FINISH') and pd_piclass in ('采购验收单','采购验退单') "
							+ "and nvl(pd_ordercode,' ')<>' ' and nvl(ProdIODetail.pd_status,0)=0", String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("采购单明细已冻结或者已结案，不允许进行当前操作!行号：" + dets);
			}
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(pd_pdno) from ProdIODetail left join PurchaseDetail on pd_ordercode=pd_code and pd_orderdetno=pd_detno where "
							+ "pd_piid=? and nvl(pd_mrpstatuscode,' ') in ('FREEZE','FINISH') and pd_piclass in ('不良品入库单','不良品出库单') "
							+ "and nvl(pd_ordercode,' ')<>' ' and nvl(pd_qctype,' ')='采购检验单' and nvl(ProdIODetail.pd_status,0)=0",
					String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("采购单明细已冻结或者已结案，不允许进行当前操作!行号：" + dets);
			}
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(pd_pdno) from ProdIODetail left join Make on pd_ordercode=ma_code where "
							+ "pd_piid=? and nvl(ma_statuscode,' ') in ('FREEZE','FINISH') and pd_piclass in ('不良品入库单','不良品出库单') "
							+ "and nvl(pd_ordercode,' ')<>' ' and nvl(pd_qctype,' ')='委外检验单' and nvl(ProdIODetail.pd_status,0)=0",
					String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细行委外单已冻结或者已结案，不允许进行当前操作!行号：" + dets);
			}
			if (!baseDao.isDBSetting(caller, "noLimitVendor")) {
				dets = baseDao.getJdbcTemplate().queryForObject(
						"select wm_concat(pd_pdno) from ProdIODetail left join Prodinout on pd_piid=pi_id left join Purchase on pu_code=pd_ordercode where "
								+ "pd_piid=? and nvl(pi_cardcode,' ')<>nvl(pu_vendcode,' ') and pd_piclass in ('采购验收单','采购验退单') "
								+ "and nvl(pd_ordercode,' ')<>' ' and nvl(ProdIODetail.pd_status,0)=0", String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("明细采购单与单据供应商不一致，不允许进行当前操作!行号：" + dets);
				}
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail left join Prodinout on pd_piid=pi_id left join Purchase on pu_code=pd_ordercode where "
									+ "pd_piid=? and nvl(pi_receivecode,' ')<>nvl(pu_receivecode,' ') and pd_piclass in ('采购验收单','采购验退单') and nvl(pd_ordercode,' ')<>' ' and nvl(ProdIODetail.pd_status,0)=0",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细采购单与单据应付供应商不一致，不允许进行当前操作!行号：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail left join Prodinout on pd_piid=pi_id left join Purchase on pu_code=pd_ordercode where pd_piid=? and nvl(pi_currency,' ')<>nvl(pu_currency,' ')"
									+ " and pd_piclass in ('采购验收单','采购验退单') and nvl(pd_ordercode,' ')<>' ' and nvl(ProdIODetail.pd_status,0)=0",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细采购单与单据币别不一致，不允许进行当前操作!行号：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat('行号：'||pd_pdno||'收料合格数量：'||ved_okqty) from ProdIODetail left join QUA_VerifyApplyDetailDet on pd_qcid=ved_id where pd_piid=? and nvl(pd_qcid,0)<>0 and nvl(pd_inqty,0)>nvl(ved_okqty,0) and pd_piclass in ('采购验收单') and nvl(ProdIODetail.pd_status,0)=0",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细行入库数量大于收料合格数量!" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat('行号：'||pd_pdno||'收料不合格数量：'||ved_ngqty) from ProdIODetail left join QUA_VerifyApplyDetailDet on pd_qcid=ved_id where pd_piid=? and nvl(pd_qcid,0)<>0 and nvl(pd_inqty,0)>nvl(ved_ngqty,0) and pd_piclass in ('不良品入库单') and nvl(ProdIODetail.pd_status,0)=0",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细行入库数量大于收料不合格数量!" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_pdno) from ProdIODetail left join Prodinout on pd_piid=pi_id left join Purchase on pu_code=pd_ordercode  left join purchasekind on pk_name=pu_kind "
									+ "where pd_piid=? and nvl(pd_whcode,' ')<>' ' and nvl(pk_whcodes,' ')<>' ' and pd_whcode not in (select column_value from table(parsestring(pk_whcodes,'#'))) "
									+ "and pd_piclass in ('采购验收单') and nvl(pd_ordercode,' ')<>' ' and nvl(ProdIODetail.pd_status,0)=0",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细仓库与采购单类型允许入仓库不一致，不允许进行当前操作!行号：" + dets);
			}
		}
		if ("ProdInOut!Sale".equals(caller) || "ProdInOut!SaleReturn".equals(caller) || "ProdInOut!OtherOut".equals(caller)
				|| "ProdInOut!ExchangeOut".equals(caller) || "ProdInOut!SaleAppropriationOut".equals(caller)) {
			if (baseDao.isDBSetting(caller, "allowDifProd")) {
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_pdno) from ProdIODetail where pd_piid=? and not exists (select sd_code,sd_detno from saledetail where sd_code=pd_ordercode and sd_detno=pd_orderdetno)"
										+ " and pd_piclass in ('出货单','销售退货单','其它出库单','换货出库单','销售拨出单') and nvl(pd_ordercode,' ')<>' ' and nvl(pd_ioid,0)=0 and nvl(ProdIODetail.pd_status,0)=0",
								String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("销售单号+销售序号不存在，不允许进行当前操作!行号：" + dets);
				}
			} else if (baseDao.isDBSetting(caller, "SaleWithProdRelation")) {
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_pdno) from ProdIODetail where pd_piid=? and "
										+ "not exists (select sd_code,sd_detno from saledetail left join ProdRelation on (sd_prodcode=prr_soncode or (nvl(prr_ifonewa,0)=0 and sd_prodcode=prr_repcode)) "
										+ "where sd_code=pd_ordercode and sd_detno=pd_orderdetno and (nvl(sd_prodcode,' ')=nvl(pd_prodcode,' ') or nvl(prr_repcode ,' ')=nvl(pd_prodcode,' ') or nvl(prr_soncode ,' ')=nvl(pd_prodcode,' ')))"
										+ "	and pd_piclass in ('出货单','销售退货单','其它出库单','换货出库单','销售拨出单') and nvl(pd_ordercode,' ')<>' ' and nvl(pd_ioid,0)=0",
								String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("销售单号+销售序号+物料编号不存在或者状态不等于已审核，不允许进行当前操作!行号：" + dets);
				}
			} else {
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_pdno) from ProdIODetail where pd_piid=? and not exists (select sd_code,sd_detno,sd_prodcode from saledetail where sd_code=pd_ordercode and sd_detno=pd_orderdetno and sd_prodcode=pd_prodcode)"
										+ " and pd_piclass in ('出货单','销售退货单','其它出库单','换货出库单','销售拨出单') and nvl(pd_ordercode,' ')<>' ' and nvl(pd_ioid,0)=0 and nvl(ProdIODetail.pd_status,0)=0",
								String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("销售单号+销售序号+物料编号不存在，不允许进行当前操作!行号：" + dets);
				}
			}
			if (!baseDao.isDBSetting(caller, "allowARCust")) {
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_pdno) from ProdIODetail left join Prodinout on pd_piid=pi_id left join Sale on sa_code=pd_ordercode where pd_piid=? and nvl(pi_cardcode,' ')<>nvl(sa_custcode,' ')"
										+ " and pd_piclass in ('出货单','销售退货单','其它出库单','换货出库单','销售拨出单') and nvl(pd_ordercode,' ')<>' ' and nvl(pd_ioid,0)=0 and nvl(ProdIODetail.pd_status,0)=0",
								String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("明细销售单与单据客户不一致，不允许进行当前操作!行号：" + dets);
				}
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_pdno) from ProdIODetail left join Prodinout on pd_piid=pi_id left join Sale on sa_code=pd_ordercode where pd_piid=? and nvl(pi_arcode,' ')<>nvl(sa_apcustcode,' ')"
										+ " and pd_piclass in ('出货单','销售退货单') and nvl(pd_ordercode,' ')<>' ' and nvl(ProdIODetail.pd_status,0)=0",
								String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("明细销售单与单据应收客户不一致，不允许进行当前操作!行号：" + dets);
				}
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_pdno) from ProdIODetail left join Prodinout on pd_piid=pi_id left join Sale on sa_code=pd_ordercode where pd_piid=? and nvl(pi_currency,' ')<>nvl(sa_currency,' ')"
										+ " and pd_piclass in ('出货单','销售退货单') and nvl(pd_ordercode,' ')<>' ' and nvl(ProdIODetail.pd_status,0)=0",
								String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("明细销售单与单据币别不一致，不允许进行当前操作!行号：" + dets);
				}
			}
			/*
			 * dets = baseDao .getJdbcTemplate() .queryForObject(
			 * "select wm_concat(pi_inoutno) from ProdInOut where pi_id=? and pi_class in ('出货单','销售退货单') and (pi_cardcode,pi_title) not in (select cu_code,cu_name from customer)"
			 * , String.class, pi_id); if (dets != null) {
			 * BaseUtil.showError("单据中客户编号，客户名称在客户资料中不存在!"); } dets = baseDao
			 * .getJdbcTemplate() .queryForObject(
			 * "select wm_concat(pi_inoutno) from ProdInOut where pi_id=? and pi_class in ('出货单','销售退货单') and (pi_arcode,pi_arname) not in (select cu_code,cu_name from customer)"
			 * , String.class, pi_id); if (dets != null) {
			 * BaseUtil.showError("单据中应收客户编号，应收客户名称在客户资料中不存在!"); }
			 */
			// 销售退货单：退货数量，如退货数量+其它未过账的退货单数量大于销售订单出货数
			/**
			 * 需要考虑单据过账状态
			 */
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from ProdIODetail b left join SaleDetail on sd_code=pd_ordercode and sd_detno=pd_orderdetno where pd_piid=? and b.pd_status<99 and pd_piclass='销售退货单' and nvl(pd_ordercode,' ')<>' ' and nvl(pd_inqty,0) + (select nvl(sum(nvl(a.pd_inqty,0)),0) from ProdIODetail a where a.pd_piclass='销售退货单' and a.pd_status<99 and a.pd_ordercode=b.pd_ordercode and a.pd_orderdetno=b.pd_orderdetno and a.pd_id <> b.pd_id) > nvl(sd_sendqty,0) and nvl(pd_status,0)=0",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("本次退货数+其它退货数合计大于订单的已发货数量，不允许进行当前操作！" + dets);
			}
			if ("ProdInOut!SaleAppropriationOut".equals(caller) || "ProdInOut!OtherOut".equals(caller)) {
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from ProdIODetail left join SaleDetail on pd_ordercode=sd_code and pd_orderdetno=sd_detno where pd_piid=? and pd_piclass in ('销售拨出单','其它出库单') and nvl(pd_outqty+pd_inqty,0)>nvl(sd_qty,0)-nvl(sd_yqty,0) and nvl(pd_ordercode,' ')<>' ' and nvl(pd_ioid,0)=0 and nvl(pd_status,0)=0",
								String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("明细行销售单号+销售序号数量大于订单未发货数，不允许进行当前操作！" + dets);
				}
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from ProdIODetail where pd_piid=? and pd_piclass='销售拨出单' and pd_plancode not in (select a.sf_code from saleforecast a left join saleforecastkind b on (a.sf_kind=b.sf_name or a.sf_kind=b.sf_code) where a.sf_code=pd_plancode and sf_clashoption in ('SEND','发货冲销')) and nvl(pd_plancode,' ')<>' '",
								String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("预测单录入错误，该预测类型不属于发货冲销！" + dets);
				}
			}
		}
		if ("ProdInOut!AppropriationOut".equals(caller) || "ProdInOut!AppropriationIn".equals(caller)
				|| "ProdInOut!SaleAppropriationOut".equals(caller) || "ProdInOut!SaleAppropriationIn".equals(caller)) {
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from ProdIODetail,warehouse w1,warehouse w2 where pd_piid=? and pd_whcode=w1.wh_code and pd_inwhcode=w2.wh_code and nvl(w1.wh_nocost,0)<>nvl(w2.wh_nocost,0)",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("有值和无值仓之间不能相互调拨！" + dets);
			}
		}
	}

	/**
	 * 校验批次是否重复
	 * 
	 * @param caller
	 * @param pi_id
	 */
	private void checkBatch(String caller, Object pi_id) {
		if (prodInOutDao.isIn(caller)) {
			// 入库单据，同一物料同仓库不能同时入两次相同的批号
			SqlRowList rs = baseDao
					.queryForRowSet("select  count(1)n, wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']')detno from (select pd_batchcode,pd_whcode,pd_prodcode,min(pd_pdno)pd_pdno,min(pd_inoutno)pd_inoutno,min(pd_piclass)pd_piclass,count(1)c from  ProdIODetail where pd_piid="
							+ pi_id + " and pd_batchcode<>' ' group by pd_batchcode,pd_whcode,pd_prodcode ) where c> 1");
			if (rs.next()) {
				if (rs.getInt("n") > 0) {
					BaseUtil.showError("同一物料同仓库不能同时入两次相同的批号！" + rs.getString("detno"));
				}
			}
			rs = baseDao
					.queryForRowSet("select count(1) n,wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']')detno "
							+ "from (select pd_piclass,pd_inoutno,pd_pdno from ProdIODetail where pd_piid="
							+ pi_id
							+ " and pd_batchcode is not null and exists (select 1 from batch where ba_code=pd_batchcode and ba_prodcode=pd_prodcode "
							+ "and ba_whcode=pd_whcode and (nvl(ba_remain,0)<>0 or nvl(ba_inqty,0)<>0))) where rownum<30");
			if (rs.next()) {
				if (rs.getInt("n") > 0) {
					BaseUtil.showError("批号已存在，不能重复入库！" + rs.getString("detno"));
				}
			}
		}
	}

	@Override
	public String updateWhCodeInfo(String data, String caller) {
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(data);
		String updatecondition = "pd_id=" + map.get("pd_id");
		Object[] infos = baseDao.getFieldsDataByCondition("prodinout left join prodiodetail on pi_id=pd_piid", new String[] {
				"pi_statuscode", "pd_piid" }, "pd_id=" + map.get("pd_id"));
		if ("POSTTED".equals(infos[0])) {
			BaseUtil.showError("当前单据已过账不允许修改仓库信息!");
		}
		if (map.get("isalldetail").equals(true)) {
			updatecondition = "pd_piid=" + infos[1];
		}

		baseDao.updateByCondition("Prodiodetail", "pd_whcode='" + map.get("whcode") + "',pd_whname='" + map.get("whname") + "'",
				updatecondition);
		baseDao.logger.others("修改仓库", "修改成功", caller, "pi_id", infos[1]);
		return "";
	}

	@Override
	public void getPrice(int pdid, int piid, String caller) {
		if ("ProdInOut!PurcCheckout".equals(caller)) {
			SqlRowList rs = baseDao
					.queryForRowSet(
							"select pd_pdno,pd_piid,pi_cardcode,pi_currency,pd_orderprice from ProdIODetail,ProdInOut where pd_piid=pi_id and pd_id=?",
							pdid);
			if (rs.next()) {
				if (baseDao.isDBSetting("Application!ToPurchase!Deal", "onlyQualifiedPrice")) {
					baseDao.execute("update prodiodetail set (pd_orderprice,pd_taxrate)=(select min(ppd_price),min(ppd_rate) from PurchasePriceDetail left join PurchasePrice on ppd_ppid=pp_id left join product on ppd_prodcode=pr_code where ppd_vendcode='"
							+ rs.getString("pi_cardcode")
							+ "' and ppd_prodcode=pd_prodcode and ppd_currency='"
							+ rs.getString("pi_currency")
							+ "' and pp_kind like '%采购' and to_char(ppd_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' and (nvl(ppd_appstatus,' ')='合格' or pr_material<>'已认可') and ppd_lapqty<=pd_outqty) where pd_id="
							+ pdid);
				} else {
					baseDao.execute("update prodiodetail set (pd_orderprice,pd_taxrate)=(select min(ppd_price),min(ppd_rate) from PurchasePriceDetail left join PurchasePrice on ppd_ppid=pp_id left join product on ppd_prodcode=pr_code where ppd_vendcode='"
							+ rs.getString("pi_cardcode")
							+ "' and ppd_prodcode=pd_prodcode and ppd_currency='"
							+ rs.getString("pi_currency")
							+ "' and pp_kind like '%采购' and to_char(ppd_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' and ppd_lapqty<=pd_outqty) where pd_id="
							+ pdid);
				}
				baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "抓取采购单价", "行" + rs.getObject("pd_pdno"), caller
						+ "|pi_id=" + piid));
			}
		}
		if ("ProdInOutApply!CGYT".equals(caller)) {
			SqlRowList rs = baseDao
					.queryForRowSet(
							"select pd_pdno,pd_piid,pi_cardcode,pi_currency,pd_orderprice from ProdIOApplyDetail ,ProdInOutApply where pd_piid=pi_id and pd_id=?",
							pdid);
			if (rs.next()) {
				if (baseDao.isDBSetting("Application!ToPurchase!Deal", "onlyQualifiedPrice")) {
					baseDao.execute("update ProdIOApplyDetail set (pd_orderprice,pd_taxrate)=(select min(ppd_price),min(ppd_rate) from PurchasePriceDetail left join PurchasePrice on ppd_ppid=pp_id left join product on ppd_prodcode=pr_code where ppd_vendcode='"
							+ rs.getString("pi_cardcode")
							+ "' and ppd_prodcode=pd_prodcode and ppd_currency='"
							+ rs.getString("pi_currency")
							+ "' and pp_kind like '%采购' and to_char(ppd_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' and (nvl(ppd_appstatus,' ')='合格' or pr_material<>'已认可') and ppd_lapqty<=pd_outqty) where pd_id="
							+ pdid);
				} else {
					baseDao.execute("update ProdIOApplyDetail set (pd_orderprice,pd_taxrate)=(select min(ppd_price),min(ppd_rate) from PurchasePriceDetail left join PurchasePrice on ppd_ppid=pp_id left join product on ppd_prodcode=pr_code where ppd_vendcode='"
							+ rs.getString("pi_cardcode")
							+ "' and ppd_prodcode=pd_prodcode and ppd_currency='"
							+ rs.getString("pi_currency")
							+ "' and pp_kind like '%采购' and to_char(ppd_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' and ppd_lapqty<=pd_outqty) where pd_id="
							+ pdid);
				}
				baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "抓取采购单价", "行" + rs.getObject("pd_pdno"), caller
						+ "|pi_id=" + piid));
			}
		}
	}

	@Override
	@Transactional
	public void generateBarcodeByZxbzs(int pi_id, String pi_class, String caller) {
		SqlRowList rs;
		Object status = baseDao.getFieldDataByCondition("ProdInOut", "pi_statuscode", "pi_id=" + pi_id);
		if (status.toString().equals("POSTED")) {
			BaseUtil.showError("单据已经过账，不允许生成条码!");
		}
		rs = baseDao
				.queryForRowSet("select count(0) cn ,wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') pd_no  from prodiodetail where pd_piid="
						+ pi_id + "  and pd_status=99");
		if (rs.next() && rs.getInt("cn") > 0) {
			BaseUtil.showError(rs.getString("pd_no") + ",已过帐，不能生成条码!");
		}
		// 后台在点击批量生成条码的时候提示是否已经生成了条码
		String pdno = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') pdno  from  barcodeio left join ProdIODetail on bi_pdid=pd_id  where pd_piid=?",
						String.class, pi_id);
		if (pdno != null) {
			BaseUtil.showError(pdno + "已经产生了条码，不能再批量生成！");
		}
		// 判断 明细行中的物料是否维护了最小包装数，并且维护的最小包装数值大于1
		rs = baseDao
				.queryForRowSet("select count(0) cn ,wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') pd_no from prodinout left join prodiodetail on pd_piid=pi_id "
						+ " left join product on pr_code=pd_prodcode where pi_id=" + pi_id + " and NVL(pr_zxbzs,0)<=1");
		if (rs.next() && rs.getInt("cn") > 0) {
			BaseUtil.showError("存在" + rs.getString("pd_no") + ",最小包装数小于等于1!");
		}
		rs = baseDao
				.queryForRowSet("select pi_id,pi_class,pi_inoutno,pd_id,pd_pdno,pd_batchcode,pd_batchid,NVL(pd_whcode,pi_whcode) pd_whcode,pd_prodcode,pd_inqty,"
						+ " to_char(pd_prodmadedate,'yyyy-MM-dd') pd_prodmadedate,to_char(pd_replydate,'yyyy-MM-dd') pd_replydate,pd_location,pr_id,pr_zxbzs from prodinout left join prodiodetail on pd_piid=pi_id "
						+ " left join product on pr_code=pd_prodcode where pi_id=" + pi_id);
		if (rs.next()) {
			List<String> sqls = new ArrayList<String>();
			for (Map<String, Object> map : rs.getResultList()) {
				// 首先求pd_inqty/pr_zxbzs
				double pd_inqty = Double.valueOf(String.valueOf(map.get("pd_inqty"))), aqty = 0;
				double pr_zxbzs = Double.valueOf(String.valueOf(map.get("pr_zxbzs")));
				String bar_code, bi_madedate = null, bi_validdate = null;
				int num = (int) (pd_inqty / pr_zxbzs);
				if (pd_inqty != pr_zxbzs) {
					aqty = (new BigDecimal(Double.toString(pd_inqty))).subtract(new BigDecimal(Double.toString(num * pr_zxbzs)))
							.doubleValue();
				}
				bi_validdate = DateUtil.parseDateToOracleString("yyyy-MM-dd", String.valueOf(map.get("pr_validdays")));
				bi_madedate = DateUtil.parseDateToOracleString("yyyy-MM-dd", String.valueOf(map.get("pd_prodmadedate")));
				if (num > 0) {
					for (int i = 0; i < num; i++) {
						bar_code = verifyApplyDao.barcodeMethod(map.get("pd_prodcode").toString(), "", 0);
						sqls.add("insert into barcodeio (bi_id,bi_barcode,bi_piid,bi_inoutno,bi_pdno,bi_pdid,bi_batchcode,bi_batchid,bi_status,bi_printstatus,"
								+ " bi_prodcode,bi_whcode,bi_inqty,bi_madedate,bi_validdate,bi_location,bi_prodid) "
								+ " values (BARCODEIO_SEQ.nextval,'"
								+ bar_code
								+ "',"
								+ pi_id
								+ ",'"
								+ map.get("pi_inoutno")
								+ "',"
								+ map.get("pd_pdno")
								+ ","
								+ map.get("pd_id")
								+ ",'"
								+ map.get("pd_batchcode")
								+ "',"
								+ map.get("pd_batchid")
								+ ",0,0,"
								+ "'"
								+ map.get("pd_prodcode")
								+ "','"
								+ map.get("pd_whcode")
								+ "',"
								+ pr_zxbzs
								+ ","
								+ bi_madedate
								+ ","
								+ bi_validdate + ",'" + map.get("pd_location") + "'," + map.get("pr_id") + ")");
					}
				}
				if (aqty != 0) {
					bar_code = verifyApplyDao.barcodeMethod(map.get("pd_prodcode").toString(), "", 0);
					sqls.add("insert into barcodeio (bi_id,bi_barcode,bi_piid,bi_inoutno,bi_pdno,bi_pdid,bi_batchcode,bi_batchid,bi_status,bi_printstatus,"
							+ " bi_prodcode,bi_whcode,bi_inqty,bi_madedate,bi_validdate,bi_location,bi_prodid) "
							+ " values (BARCODEIO_SEQ.nextval,'"
							+ bar_code
							+ "',"
							+ pi_id
							+ ",'"
							+ map.get("pi_inoutno")
							+ "',"
							+ map.get("pd_pdno")
							+ ","
							+ map.get("pd_id")
							+ ",'"
							+ map.get("pd_batchcode")
							+ "',"
							+ map.get("pd_batchid")
							+ ",0,0,"
							+ "'"
							+ map.get("pd_prodcode")
							+ "','"
							+ map.get("pd_whcode")
							+ "',"
							+ aqty
							+ ","
							+ bi_madedate
							+ ","
							+ bi_validdate
							+ ",'"
							+ map.get("pd_location") + "'," + map.get("pr_id") + ")");
				}
			}
			baseDao.execute(sqls);
		}
	}

	// 判断仓库是否为平台销售仓
	private void checkWhIsB2C(String caller, int pi_id) {
		// 判断仓库pd_whcode是否属于平台销售仓:wharehouse.wh_ifb2c<>0，如果是则必须维护pd_barcode字段；
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from prodiodetail left join warehouse on pd_whcode=wh_code where pd_piid=?"
								+ " and nvl(wh_ifb2c,0)<>0 and nvl(pd_barcode,' ')=' '", String.class, pi_id);
		if (dets != null) {
			BaseUtil.showError(dets + "中的仓库为平台销售仓，必须维护平台批号！");
		}
		SqlRowList rs = baseDao.queryForRowSet("select ds_inorout from DOCUMENTSETUP where ds_table=?", caller);
		if (rs.next()) {
			String Code = rs.getObject("ds_inorout").toString();
			if (Code.equals("IN") || Code.equals("-OUT")) {
				// 在平台销售仓的前提下，如果是入库单，判断pd_barcode是否存在于上架申请单从表的gd_barcode，如果不存在，则不允许过账
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from prodiodetail left join warehouse on pd_whcode=wh_code where pd_piid=?"
										+ " and not exists(select 1 from goodsup,goodsdetail where gu_id=gd_guid and gd_barcode=pd_barcode) "
										+ " and nvl(pd_inqty,0)>0 and nvl(wh_ifb2c,0)<>0", String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("明细行的平台批号不存在于对应的上架申请单中！" + dets);
				}
				// 在平台销售仓的前提下，如果是入库单，判断，上架申请单+序号+平台批号
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']')  from prodiodetail left join warehouse on pd_whcode=wh_code where pd_piid=?"
										+ " And not exists (select 1 from goodsup,goodsdetail where gu_id=gd_guid and gd_barcode=pd_barcode and gu_code=pd_ordercode and gd_detno=pd_orderdetno) "
										+ "and nvl(pd_inqty,0)>0 and nvl(wh_ifb2c,0)<>0", String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError("明细行单号+序号+平台批号不一致！" + dets);
				}
			} else if (Code.equals("OUT") || Code.equals("-IN")) {
				// 在平台销售仓的前提下,如果是出库单，判断出库数量是否大于库存数
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(no) from (select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') no, pd_whcode,sum(pd_outqty) outqty,pd_barcode,pd_prodcode from prodiodetail left  join warehouse"
										+ " on pd_whcode=wh_code where pd_piid =?"
										+ " and nvl(wh_ifb2c,0)<>0 group by pd_prodcode,pd_barcode,pd_whcode)T"
										+ " left join (select nvl(sum(ba_remain),0)remain,ba_prodcode,ba_whcode,ba_barcode from batch group by ba_prodcode,ba_whcode,ba_barcode) B"
										+ " on  pd_prodcode=ba_prodcode and pd_barcode=ba_barcode and pd_whcode=ba_whcode where remain<outqty",
								String.class, pi_id);
				if (dets != null) {
					BaseUtil.showError(dets + "中的出库数大于库存数！");
				}
			}
		}
	}

	/**
	 * 只有caller 等于生产领料单和委外领料单才调用该方法
	 * 
	 * @param mm_id
	 */
	private void refreshmaketurn(String mm_id, String ma_id) {
		List<String> str2List = BaseUtil.parseStr2List(mm_id, ",", true);
		// 有待优化
		if (str2List.size() > 500) {
			// 已转领料数
			baseDao.execute("update MakeMaterial set mm_totaluseqty=(select sum(nvl(pd_outqty,0)) from prodiodetail,prodinout "
					+ "where pd_piid=pi_id and pd_status=0 and pd_ordercode=mm_code and pd_orderdetno=mm_detno and pd_piclass in ('生产领料单', '委外领料单'))"
					+ " WHERE mm_maid in (" + ma_id + ")");
		} else {
			// 已转领料数
			baseDao.execute("update MakeMaterial set mm_totaluseqty=(select sum(nvl(pd_outqty,0)) from prodiodetail,prodinout "
					+ "where pd_piid=pi_id and pd_status=0 and pd_ordercode=mm_code and pd_orderdetno=mm_detno and pd_piclass in ('生产领料单', '委外领料单'))"
					+ " WHERE mm_maid in (" + ma_id + ") and mm_id in (" + mm_id + ")");
		}
		// 修改工单领料状态
		makeDao.updateMakeGetStatus(ma_id);
	}

	/**
	 * 只提示：只考虑当前业务员的可用批次
	 */
	private void tipSellerBatch(String caller, Object pi_id) {
		if (baseDao.isDBSetting(caller, "tipSellerBatch")) {
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from ProdIODetail left join ProdInOut on pd_piid=pi_id left join batch B on pd_batchcode=B.ba_code and pd_prodcode=B.ba_prodcode and pd_whcode=B.ba_whcode where pi_id=? and nvl(pd_batchcode,' ')<>' ' and exists(select 1 from batch C where C.ba_prodcode=pd_prodcode and C.ba_whcode=ProdIODetail.pd_whcode and nvl(C.ba_remain,0)>0 and trunc(C.ba_date)<trunc(B.ba_date) and NVL(C.ba_kind,0)=0 and ba_sellercode<>' ' and ba_sellercode=pi_sellercode and not exists(select 1 from ProdIODetail A where A.pd_batchcode=ba_code and A.pd_prodcode=ba_prodcode and A.pd_whcode=ba_whcode and nvl(a.pd_outqty,0)>0 and a.pd_status=0)) and rownum<20",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.appendError("明细行所选批号不是当前业务员的可用最早可用批次！" + dets);
			}
		}
	}

	/**
	 * 限制：只考虑当前业务员的可用批次
	 */
	private void astrictSellerBatch(String caller, Object pi_id) {
		if (baseDao.isDBSetting(caller, "astrictSellerBatch")) {
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pd_piclass||'['||pd_inoutno||']行号['||pd_pdno||']') from ProdIODetail left join ProdInOut on pd_piid=pi_id left join batch B on pd_batchcode=B.ba_code and pd_prodcode=B.ba_prodcode and pd_whcode=B.ba_whcode where pi_id=? and nvl(pd_batchcode,' ')<>' '"
									+ " and ((nvl(ba_sellercode,' ')<>' ' and nvl(pi_sellercode,' ')<>nvl(ba_sellercode,' ')) or  exists(select 1 from batch C where C.ba_prodcode=pd_prodcode and C.ba_whcode=ProdIODetail.pd_whcode  and nvl(C.ba_remain,0)>0 and trunc(C.ba_date)<trunc(B.ba_date) and NVL(C.ba_kind,0)=0 and ba_sellercode<>' ' and ba_sellercode=pi_sellercode and not exists(select 1 from ProdIODetail A where A.pd_batchcode=ba_code and A.pd_prodcode=ba_prodcode and A.pd_whcode=ba_whcode and nvl(a.pd_outqty,0)>0 and a.pd_status=0))) and rownum<20",
							String.class, pi_id);
			if (dets != null) {
				BaseUtil.showError("明细行所选批号不是当前业务员的可用最早可用批次！" + dets);
			}
		}
	}

	@Override
	public void createBill(String caller, int id) {
		SqlRowList rs = baseDao.queryForRowSet("select * from ProdInOut where pi_id=?", id);
		if (rs.next()) {
			if (!"POSTED".equals(rs.getObject("pi_statuscode"))) {
				BaseUtil.showError("单据未过账不能开票!");
			}
			if ("ProdInOut!Sale".equals(caller) || "ProdInOut!SaleReturn".equals(caller)) {
				if (baseDao.isDBSetting("autoCreateArBill")) {
					String res = null;
					// 其他方式自动产生发票
					if (baseDao.isDBSetting("createArBillOthers")) {
						baseDao.procedure(
								"SP_CREATEARBILLOTHERS",
								new Object[] { rs.getGeneralString("pi_class"), rs.getGeneralString("pi_inoutno"),
										String.valueOf(SystemSession.getUser().getEm_name()) });
					} else {
						String dets = baseDao.getJdbcTemplate().queryForObject(
								"select wm_concat(distinct abd_code) from arbilldetail where abd_pdinoutno='" + rs.getObject("pi_inoutno")
										+ "' and abd_sourcekind='PRODIODETAIL'", String.class);
						if (dets != null) {
							BaseUtil.showError("当前" + rs.getGeneralString("pi_class") + "已产生形式发票！发票号：" + dets);
						}
						List<String> result = baseDao.callProcedureWithOut(
								"Sp_ConvertProdIOToARBill",
								new Object[] { rs.getGeneralString("pi_class"), rs.getGeneralString("pi_inoutno"),
										String.valueOf(SystemSession.getUser().getEm_name()) }, new Integer[] { 1, 2, 3 }, new Integer[] {
										4, 5 });
						if (StringUtil.hasText(result.get(0)))
							throw new SystemException(result.get(0));
						// 存储过程
						res = baseDao.callProcedure("Sp_CommiteARBill", new Object[] { result.get(1), 0 });
						if (StringUtil.hasText(res)) {
							throw new SystemException(res);
						}
					}
				} else {
					BaseUtil.showError("没有启动自动生成形式发票功能，不能做此操作！");
				}
			}
			if ("ProdInOut!PurcCheckin".equals(caller) || "ProdInOut!PurcCheckout".equals(caller)
					|| "ProdInOut!OutesideCheckReturn".equals(caller) || "ProdInOut!OutsideCheckIn".equals(caller)
					|| "ProdInOut!GoodsOut".equals(caller) || "ProdInOut!GoodsIn".equals(caller) || "ProdInOut!ProcessIn".equals(caller)) {
				if (baseDao.isDBSetting("autoCreateApBill")) {
					String dets = baseDao.getJdbcTemplate().queryForObject(
							"select wm_concat(distinct abd_code) from apbilldetail where abd_pdinoutno='" + rs.getObject("pi_inoutno")
									+ "' and abd_sourcekind='PRODIODETAIL'", String.class);
					if (dets != null) {
						BaseUtil.showError("当前" + rs.getGeneralString("pi_class") + "已产生形式发票！发票号：" + dets);
					}
					String res = null;
					List<String> result = baseDao.callProcedureWithOut(
							"Sp_ConvertProdIOToAPBill",
							new Object[] { rs.getGeneralString("pi_class"), rs.getGeneralString("pi_inoutno"),
									String.valueOf(SystemSession.getUser().getEm_name()) }, new Integer[] { 1, 2, 3 },
							new Integer[] { 4, 5 });
					if (StringUtil.hasText(result.get(0)))
						throw new SystemException(result.get(0));
					// 存储过程
					res = baseDao.callProcedure("Sp_CommiteAPBill", new Object[] { result.get(1), 0 });
					if (StringUtil.hasText(res)) {
						throw new SystemException(res);
					}
				} else {
					BaseUtil.showError("没有启动自动生成形式发票功能，不能做此操作！");
				}
			}
		}
	}

	@Override
	public void catchBatchByOrder(Long pd_piid, Long pd_id, String caller) {
		// 首先检查目前的单据状态是否未过账，如果已过账要提示；检查是否有选中行；检查订单号是否为空，为空提示“抓取批号前，请先选择订单！
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select pd_pocode,pi_inoutno,pi_class, pi_statuscode,pd_pdno,pd_ordercode from prodinout left join prodiodetail on pd_piid=pi_id where pi_id=?"
								+ " and pd_id=?", pd_piid, pd_id);
		if (rs.next()) {
			if (rs.getString("pi_statuscode").equals("POSTED")) {
				BaseUtil.showError("单据已过帐,不允许按订单抓取批号!");
			}
			String sacode = null;
			if ("出货单".equals(rs.getObject("pi_class")) && StringUtil.hasText(rs.getObject("pd_ordercode"))) {
				sacode = rs.getString("pd_ordercode");
			} else if ("生产领料单".equals(rs.getObject("pi_class")) && StringUtil.hasText(rs.getObject("pd_pocode"))) {
				sacode = rs.getString("pd_pocode");
			} else {
				BaseUtil.showError("抓取批号前，请先选择订单!");
			}
			if (sacode != null) {// 执行
				// 调用存储过程抓取批号
				String res = baseDao.callProcedure("SP_SPLITPRODOUT_BYPOBATCH",
						new Object[] { rs.getString("pi_class"), rs.getString("pi_inoutno"), pd_id, sacode });
				if (res != null && !res.trim().equals("")) {
					BaseUtil.showError(res);
				}
				// 记录操作日志
				baseDao.logger.others("按订单抓取批号", "行号:" + rs.getString("pd_pdno") + "抓取批号成功", caller, "pi_id", pd_piid);
			} else {

			}
		} else {
			BaseUtil.showError("明细行不存在或者已删除,不允许按订单抓取批号!");
		}
	}

	@Override
	public void catchBatchByIncode(Long pi_id, String caller) {
		String mes = null;
		// 首先检查目前的单据状态是否未过账，如果已过账要提示；检查是否有选中行；检查委托方编号是否为空，为空提示“抓取批号前，请先选择委托方！
		SqlRowList rs = baseDao.queryForRowSet("select pi_inoutno, pi_class, pi_statuscode from prodinout where pi_id=" + pi_id);
		if (rs.next()) {
			if (rs.getString("pi_statuscode").equals("POSTED")) {
				BaseUtil.showError("单据已过帐,不允许" + mes + "抓取批号!");
			}
			String res = baseDao.callProcedure("SP_SPLITPRODOUT_BYINCODE",
					new Object[] { rs.getString("pi_class"), rs.getString("pi_inoutno"), SystemSession.getUser().getEm_name() });
			if (res != null && !res.trim().equals("")) {
				BaseUtil.showError(res);
			}
		}
		baseDao.logger.others(mes + "抓取批号", "抓取批号成功", caller, "pi_id", pi_id);
	}

	@Override
	public void catchBatchByClient(String type, Long pi_id, String caller) {
		String mes = null;
		if ("ByClient".equals(type)) {
			mes = "按委托方";
		} else if ("ByCust".equals(type)) {
			mes = "按客户";
		} else if ("ByOrder".equals(type)) {
			mes = "按订单";
		}
		// 首先检查目前的单据状态是否未过账，如果已过账要提示；检查是否有选中行；检查委托方编号是否为空，为空提示“抓取批号前，请先选择委托方！
		SqlRowList rs = baseDao.queryForRowSet("select pi_inoutno, pi_class, pi_statuscode from prodinout where pi_id=" + pi_id);
		if (rs.next()) {
			if (rs.getString("pi_statuscode").equals("POSTED")) {
				BaseUtil.showError("单据已过帐,不允许" + mes + "抓取批号!");
			}
			String res = baseDao.callProcedure("SP_SPLITPRODOUT_BYCLIENT",
					new Object[] { type, rs.getString("pi_class"), rs.getString("pi_inoutno"), SystemSession.getUser().getEm_name() });
			if (res != null && !res.trim().equals("")) {
				BaseUtil.showError(res);
			}
		}
		baseDao.logger.others(mes + "抓取批号", "抓取批号成功", caller, "pi_id", pi_id);
	}

	@Override
	public void splitProdIODetail(String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);
		int pd_id = Integer.parseInt(store.get("pd_id").toString());
		int piid = Integer.parseInt(store.get("pd_piid").toString());
		int basedetno = Integer.parseInt(store.get("pd_pdno").toString());
		double baseqty = 0, splitqty = 0;
		baseDao.execute("update ProdIODetail set pd_originalqty=nvl(pd_inqty,0)+nvl(pd_outqty,0),pd_originaldetno=pd_pdno where pd_id="
				+ pd_id + " and pd_originaldetno is null");
		SqlRowList cur = baseDao.queryForRowSet("select * from ProdIODetail where pd_id=" + pd_id);
		if (cur.next()) {
			baseqty = cur.getGeneralDouble("pd_inqty") + cur.getGeneralDouble("pd_outqty");
			piid = cur.getGeneralInt("pd_piid");
		} else {
			BaseUtil.showError("原始明细已不存在!无法拆分!");
		}
		for (Map<Object, Object> s : grid) {
			splitqty = NumberUtil.add(splitqty,
					Double.parseDouble(s.get("pd_inqty").toString()) + Double.parseDouble(s.get("pd_outqty").toString()));
		}
		if (splitqty != baseqty) {
			BaseUtil.showError("拆分后的总数跟当前序号总数不一致!");
		}
		SqlRowList sl = baseDao.queryForRowSet("select max(pd_pdno) from ProdIODetail where pd_piid=" + piid);
		int newdetno = 0;
		if (sl.next()) {
			newdetno = sl.getInt(1) == -1 ? basedetno + 1 : sl.getInt(1);
		}
		Object pdid = null;
		double sdqty = 0;
		int sddetno = 0;
		SqlRowList sl2 = null;
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(grid, "ProdIODetail", "pd_id");
		// 判断原始的序号 值不能
		for (Map<Object, Object> s : grid) {
			pdid = s.get("pd_id");
			sddetno = Integer.parseInt(s.get("pd_pdno").toString());
			sdqty = Double.parseDouble(s.get("pd_inqty").toString()) + Double.parseDouble(s.get("pd_outqty").toString());
			if (pdid == null || pdid.equals("") || pdid.equals("0") || Integer.parseInt(pdid.toString()) == 0) {// 新添加的数据，id不存在
				SqlMap map = new SqlMap("ProdIODetail");
				newdetno++;
				for (Object key : s.keySet()) {
					map.set(key.toString(), s.get(key));
				}
				map.setSpecial("pd_id", "PRODIODETAIL_SEQ.nextval");
				map.set("pd_pdno", newdetno);
				map.set("pd_yqty", 0);
				map.set("pd_ycheck", 0);
				map.set("pd_piid", piid);
				map.executeCopy("pd_id=" + pd_id);
			} else {
				// 说明是原来已经拆分的订单 更新数量和交货日期 前台判定会有问题
				sl2 = baseDao.queryForRowSet("select pd_inqty,pd_outqty,pd_yqty from ProdIODetail where pd_id=" + pdid);
				boolean b = baseDao.checkIf("ProdIODetail", "pd_id=" + pd_id + " AND pd_yqty>" + sdqty);
				if (b) {
					// 原始拆分后数量 不能小于
					BaseUtil.showError("原始拆分后的数量不能超过已转数量!");
				}
				if (sl2.next()) {
					if (sdqty < sl2.getInt("sd_yqty")) {
						BaseUtil.showError("序号 :[" + sddetno + "] ,拆分后的数量小于已转数量，不能拆分!");
					}
				} else
					BaseUtil.showError("序号 :[" + sddetno + "] ，明细数据已经不存在，不能拆分!");
			}
		}
		baseDao.execute(gridSql);
		getTotal(store.get("pi_id"), caller.split("-")[0]);
		// 记录操作
		baseDao.logger.others("拆分明细行", "明细行:" + basedetno + "=>被拆分", caller.split("-")[0], "pi_id", piid);
	}

	@Override
	public String turnPaIn(Long pi_id, String caller) {
		JSONObject j = null;
		String code = null;
		StringBuffer sb = new StringBuffer();
		SqlRowList rs = baseDao.queryForRowSet("select pi_inoutno,pi_invoicecode from ProdInOut where pi_id=?", pi_id);
		if (rs.next()) {
			if (StringUtil.hasText(rs.getObject("pi_invoicecode"))) {
				BaseUtil.showError("已转入发票箱单，不允许重复转！发票箱单:" + rs.getObject("pi_invoicecode"));
			}
			j = invoiceDao.newPaIn(pi_id, rs.getString("pi_inoutno"), caller);
			if (j != null) {
				code = j.getString("code");
				baseDao.execute("update prodinout set pi_packingcode='" + code + "', pi_invoicecode='" + code + "' where pi_id =" + pi_id);
				invoiceDao.detailTurnPaInDetail(code, pi_id.toString(), j.get("in_id"), j.get("pi_id"));
				baseDao.execute("update Invoice set in_total=(select sum(id_total) from InvoiceDetail where id_inid=" + j.get("in_id")
						+ ") where in_id=" + j.get("in_id"));
				baseDao.execute("update Invoice set in_totalupper=L2U(in_total),in_totalupperenhkd=L2U(in_total/(case when nvl(in_rate,0)=0 then 1 else in_rate end)) where in_id="
						+ j.get("in_id"));
				baseDao.execute("update Packing set pi_total=(select sum(pd_total) from PackingDetail where pd_piid=" + j.get("pi_id")
						+ ") where pi_id=" + j.get("pi_id"));
				baseDao.execute("update Packing set pi_totalupper=L2U(pi_total),pi_totalupperenhkd=L2U(pi_total/nvl(pi_rate,1)) where pi_id="
						+ j.get("pi_id"));
				int count = baseDao.getCountByCondition("ProdIODetail", "pd_piid=" + pi_id);
				int yCount = baseDao.getCountByCondition("ProdIODetail", "pd_piid=" + pi_id + " and nvl(pd_batchcode,' ')<>' '");
				int xCount = baseDao.getCountByCondition("ProdIODetail", "pd_piid=" + pi_id + " and nvl(pd_batchcode,' ')=' '");
				String status = "部分有货";
				if (yCount == count) {
					status = "齐货";
				}
				if (xCount == count) {
					status = "无货";
				}
				baseDao.updateByCondition("Invoice", "in_stockstatus='" + status + "'", "in_id=" + j.get("in_id"));
				baseDao.updateByCondition("Packing", "pi_stockstatus='" + status + "'", "pi_id=" + j.get("pi_id"));
				sb.append("转入成功,装箱单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/packing.jsp?formCondition=pi_idIS"
						+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=Packing')\">" + code + "</a>&nbsp;<hr>");
				sb.append("转入成功,发票单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/invoice.jsp?formCondition=in_idIS"
						+ j.get("in_id") + "&gridCondition=id_inidIS" + j.get("in_id") + "&whoami=Invoice')\">" + code + "</a>&nbsp;<hr>");
				handlerService.handler(caller, "turn", "after", new Object[] { Integer.parseInt(pi_id.toString()) });
			}
		}
		return sb.toString();
	}

	public void printCheck(int pi_id, String caller) {
		Object[] status = baseDao.getFieldsDataByCondition("ProdINout", new String[] { "pi_invostatuscode", "pi_statuscode" }, "pi_id="
				+ pi_id);
		// 明细资料有[未审核]、[已禁用]、[已删除]或不存在的产品!
		List<Object> codes = baseDao.getFieldDatasByCondition("Product", "pr_code",
				"pr_code IN (SELECT pd_prodcode FROM prodiodetail WHERE " + "pd_piid=" + pi_id
						+ ") AND pr_statuscode IN ('ENTERING','UNAUDIT','FORBIDDEN','DELETED')");
		if (codes != null && !codes.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			for (Object c : codes) {
				sb.append("<a href=\"javascript:openUrl('jsps/scm/product/productBase.jsp?formCondition=pr_codeIS" + c + "')\">" + c
						+ "</a>&nbsp;");
			}
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.reserve.prodInOut.print_prodcode") + sb.toString());
		}
		// 生产退料单处理
		if (caller.equals("ProdInOut!Make!Return") || caller.equals("ProdInOut!OutsideReturn")) {
			dealOutOfMakeMaterial("工单外退料", pi_id);
		} else if (caller.equals("ProdInOut!PartitionStockIn")) {
			dealOutOfMakeMaterial("拆件入库", pi_id);
		}
		if (caller.equals("ProdInOut!Make!Return") || caller.equals("ProdInOut!OutsideReturn")) {
			baseDao.execute("update prodiodetail a set pd_prodmadedate=(select min(ba_date) from prodiodetail b left join batch on b.pd_batchid=ba_id where a.pd_ordercode=b.pd_ordercode and a.pd_prodcode=b.pd_prodcode and nvl(b.pd_batchcode,' ')<>' ' and ((b.pd_piclass ='生产领料单' and a.pd_piclass='生产退料单') or (b.pd_piclass ='委外领料单' and a.pd_piclass='委外退料单'))) where pd_piid="
					+ pi_id + " and pd_prodmadedate is null");
		} else {
			baseDao.execute("update prodiodetail set pd_prodmadedate=(select pi_date from prodinout where pd_piid=pi_id) where pd_piid="
					+ pi_id + " and pd_prodmadedate is null");
		}
		baseDao.execute("update prodiodetail set pd_qctype=(select ve_class from qua_verifyapplydetail left join qua_verifyapplydetaildet on ved_veid=ve_id where pd_qcid=ved_id) where pd_piid="
				+ pi_id + " and nvl(pd_qcid,0)<>0");
		getTotal(pi_id, caller);
		if (!"POSTED".equals(status[1])) {
			checkPrint(caller, pi_id);
			copcheck(pi_id, caller);
			factorycheck(pi_id, caller);
		}
		/*
		 * maz 本次出库数大于仓库预计可出库数，不允许打印 2017080190
		 */
		if (prodInOutDao.isOut(caller) && baseDao.isDBSetting(caller, "printCheckEnoughStock")) {
			if (!"POSTED".equals(status[1])) {
				// 批号为空的判断限制
				StringBuffer sb = new StringBuffer();
				SqlRowList rs = baseDao
						.queryForRowSet("select * from (select  sum(pd_outqty) as outqty,pd_prodcode,(case when NVL(pd_whcode,' ')=' ' then pi_whcode else pd_whcode end) as whcode from prodiodetail left join prodinout on pd_piid=pi_id where nvl(pd_outqty,0)>0 "
								+ "and (pd_prodcode,(case when NVL(pd_whcode,' ')=' ' then pi_whcode else pd_whcode end)) in (select pd_prodcode,(case when NVL(pd_whcode,' ')=' ' then pi_whcode else pd_whcode end) from prodiodetail where pd_piid="
								+ pi_id
								+ " and nvl(pd_batchcode,' ')=' ' and pi_statuscode<>'POSTED') "
								+ "group by pd_prodcode,(case when NVL(pd_whcode,' ')=' ' then pi_whcode else pd_whcode end)) A left join productwh on pw_prodcode=pd_prodcode and pw_whcode=whcode where NVL(pw_onhand,0)<outqty");
				while (rs.next()) {
					SqlRowList rs1 = baseDao.queryForRowSet(
							"select wm_concat(pd_pdno)pd_pdno from prodiodetail where pd_piid=? and pd_prodcode=? and pd_whcode=?", pi_id,
							rs.getObject("pd_prodcode"), rs.getObject("pw_whcode"));
					if (rs1.next()) {
						sb.append("行号:" + rs1.getObject("pd_pdno") + " 本次出库数大于仓库预计可出库数，不允许打印！");
					}
				}
				if (sb.length() > 0) {
					BaseUtil.showError(sb.toString());
				}
				// 选择了批号打印限制
				SqlRowList res = baseDao
						.queryForRowSet("select * from (select  sum(pd_outqty) as outqty,pd_prodcode,(case when NVL(pd_whcode,' ')=' ' then pi_whcode else pd_whcode end) as whcode,pd_batchcode from prodiodetail left join prodinout on pd_piid=pi_id where nvl(pd_outqty,0)>0 "
								+ "and (pd_prodcode,(case when NVL(pd_whcode,' ')=' ' then pi_whcode else pd_whcode end),pd_batchcode) in (select pd_prodcode,(case when NVL(pd_whcode,' ')=' ' then pi_whcode else pd_whcode end),pd_batchcode from prodiodetail where pd_piid="
								+ pi_id
								+ " and nvl(pd_batchcode,' ')<>' ' and pi_statuscode<>'POSTED') "
								+ "group by pd_prodcode,(case when NVL(pd_whcode,' ')=' ' then pi_whcode else pd_whcode end),pd_batchcode) A left join batch on ba_prodcode=pd_prodcode and ba_whcode=whcode and ba_code=pd_batchcode where NVL(ba_remain,0)<outqty");
				while (res.next()) {
					SqlRowList rs1 = baseDao
							.queryForRowSet(
									"select wm_concat(pd_pdno)pd_pdno from prodiodetail where pd_piid=? and pd_prodcode=? and pd_whcode=? and pd_batchcode=?",
									pi_id, res.getObject("pd_prodcode"), res.getObject("ba_whcode"), res.getObject("pd_batchcode"));
					if (rs1.next()) {
						sb.append("行号:" + rs1.getObject("pd_pdno") + " 本次出库数大于批号预计可出库数，不允许打印！");
					}
				}
				if (sb.length() > 0) {
					BaseUtil.showError(sb.toString());
				}
			}
		}
	};

	/**
	 * 商城销售订单过账之后,自动上传至商城
	 * 
	 * @author XiaoST 2016年9月9日 下午4:45:47
	 * @param pi_id
	 */
	private void b2CSaleOrderSend(int pi_id) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select sa_b2ccode orderId,sysdate createTime, 0 total,sa_toplace jsonSdAddress,pd_inoutno sendcode,pi_logisticscompany,pi_logisticscode,pd_ordercode,pd_prodcode from prodinout left join prodiodetail on pd_piid=pi_id left join sale on sa_code=pd_ordercode left join saledetail on sd_said=sa_id and sd_detno=pd_orderdetno where pd_piid=? and sa_ordertype='B2C' and nvl(sa_b2ccode,' ')<>' '",
						pi_id);
		if (rs.next()) {
			Invoice invoice = new Invoice();
			invoice.setJsonSdAddress(rs.getString("jsonSdAddress"));
			invoice.setOrderId(rs.getLong("orderId"));
			invoice.setSendcode(rs.getString("sendcode"));
			invoice.setCompanyName(rs.getString("pi_logisticscompany"));
			invoice.setCompanyNumber(rs.getString("pi_logisticscode"));
			List<InvoiceDetail> details = new ArrayList<InvoiceDetail>();
			SqlRowList rs2 = baseDao.queryForRowSet("select pd_pdno,sd_price,sd_qty,sd_total from prodiodetail left join "
					+ "saledetail on sd_code=pd_ordercode and sd_detno=pd_orderdetno " + " where sd_code=?", rs.getString("pd_ordercode"));
			while (rs2.next()) {
				InvoiceDetail invoiceDetail = new InvoiceDetail();
				Object[] obs = baseDao.getFieldsDataByCondition("product", new String[] { "pr_code", "pr_unit" },
						"pr_code='" + rs.getString("pd_prodcode") + "'");
				double rate = 1;
				if (String.valueOf(obs[1]).equals("KG") || String.valueOf(obs[1]).equals("KPCS")) {
					rate = 0.001;
				}
				invoiceDetail.setDetno(Short.valueOf(rs2.getString("pd_pdno")));
				invoiceDetail.setPrice(rs2.getDouble("sd_price") / rate);
				invoiceDetail.setQty(rs2.getDouble("sd_qty") * rate);
				invoiceDetail.setTotal(rs2.getDouble("sd_total"));
				details.add(invoiceDetail);
			}
			invoice.setDetails(details);
			try {
				// 设置出货单待上传
				baseDao.execute("update prodinout set pi_sendstatus='上传中' where pi_id=?", pi_id);
				saleInvoiceService.send(invoice, SystemSession.getUser().getCurrentMaster());
				baseDao.execute("update prodinout set pi_sendstatus='已上传' where pi_id=?", pi_id);
			} catch (Exception e) {
				// 如果已经产生过任务，将任务先作废
				baseDao.execute("update b2c$task set ta_finishstatus='-1' where ta_docaller='ProdInOut!Sale' and ta_doid=?", pi_id);
				// 上传失败添加任务至B2CTASK
				baseDao.execute("insert into b2c$task(ta_id,ta_docaller,ta_docode,ta_doid,ta_actiontime) select "
						+ "B2C$TASK_SEQ.nextval,'ProdInOut!Sale',pi_inoutno,pi_id,sysdate from prodinout where pi_id=?", pi_id);
				baseDao.execute("update prodinout set pi_sendstatus='待上传' where pi_id=?", pi_id);
			}
		}
	}

	/**
	 * 出货单过账，自动生成下架单
	 * 
	 * @author XiaoST 2016年9月9日 下午4:44:35
	 * @param pi_id
	 */
	private void autoGoodsOff(int pi_id) {
		SqlRowList rs = baseDao.queryForRowSet("select go_prodcode,go_onhand-pw_onhand offqty,go_whcode from productwh "
				+ " left join goodspwonhand on pw_prodcode=go_prodcode and pw_whcode=go_whcode"
				+ " where go_onhand>0 and go_onhand>pw_onhand and (go_prodcode,go_whcode)"
				+ " in (select pd_prodcode,pd_whcode from prodiodetail where pd_piid=?)", pi_id);
		List<String> sqls = new ArrayList<String>();
		int detno = 1;
		String code = baseDao.sGetMaxNumber("GoodsChange", 2);
		int id = baseDao.getSeqId("GOODSCHANGE_SEQ");
		while (rs.next()) { // 按照上架单审核时间升序，先上架的先下架
			SqlRowList rs1 = baseDao
					.queryForRowSet(
							"select gd_qty,gd_whcode,gd_whname,gd_uuid,gd_b2bbatchcode,gd_barcode,gd_prodcode,gd_price,gd_madedate,gd_minbuyqty,gd_minpackqty,gd_remark,gd_usdprice,gd_deliverytime,gd_hkdeliverytime from goodsup left join goodsdetail on gd_guid=gu_id where gd_qty>0 and gd_prodcode=? and gd_whcode=? and gd_sendstatus='已上传' order by gu_auditdate asc",
							rs.getString("go_prodcode"), rs.getString("go_whcode"));
			double offqty = rs.getDouble("offqty");
			while (rs1.next()) {
				double gd_qty = rs.getDouble("gd_qty");
				if (offqty > 0) { // 将对应物料，选择变更数量和批次
					if (offqty >= gd_qty) {
						sqls.add("insert into goodschangedetail(gcd_id,gcd_gcid,gcd_detno,gcd_prodcode,"
								+ "gcd_barcode,gcd_offqty,gcd_whcode,gcd_whname,gcd_uuid,gcd_b2bbatchcode,gcd_oldprice,gcd_oldmadedate,gcd_oldminbuyqty,gcd_oldminpackqty,"
								+ "gcd_oldremark,gcd_oldusdprice,gcd_olddeliverytime,gcd_oldhkdeliverytime)"
								+ "values(goodschangedetail_seq.nextval,"
								+ id
								+ ","
								+ detno
								+ ",'"
								+ rs1.getString("gd_prodcode")
								+ "',"
								+ "'"
								+ rs1.getString("gd_barcode")
								+ "',"
								+ rs1.getDouble("gd_qty")
								+ ",'"
								+ rs1.getString("gd_whcode")
								+ "','"
								+ rs1.getString("gd_whname")
								+ "','"
								+ rs1.getString("gd_uuid")
								+ "','"
								+ rs1.getString("gd_b2bbatchcode")
								+ "',"
								+ rs1.getGeneralDouble("gd_price")
								+ ","
								+ DateUtil.parseDateToOracleString("yyyy-MM-dd HH:mm:ss", rs1.getDate("gd_madedate"))
								+ ","
								+ rs1.getGeneralDouble("gd_minbuyqty")
								+ ","
								+ rs1.getGeneralDouble("gd_minpackqty")
								+ ",'"
								+ rs1.getString("gd_remark")
								+ "','"
								+ rs1.getGeneralDouble("gd_usdprice")
								+ "','"
								+ rs1.getGeneralDouble("gd_deliverytime") + "','" + rs1.getGeneralDouble("gd_hkdeliverytime") + "')");
						offqty -= gd_qty;
					} else if (offqty < gd_qty) {
						sqls.add("insert into goodschangedetail(gcd_id,gcd_gcid,gcd_detno,gcd_prodcode,"
								+ "gcd_barcode,gcd_offqty,gcd_whcode,gcd_whname,gcd_uuid,gcd_b2bbatchcode)"
								+ "values(goodschangedetail_seq.nextval,"
								+ id
								+ ","
								+ detno
								+ ",'"
								+ rs1.getString("gd_prodcode")
								+ "',"
								+ "'"
								+ rs1.getString("gd_barcode")
								+ "',"
								+ offqty
								+ ",'"
								+ rs1.getString("gd_whcode")
								+ "','"
								+ rs1.getString("gd_whname")
								+ "','"
								+ rs1.getString("gd_uuid")
								+ "','"
								+ rs1.getString("gd_b2bbatchcode")
								+ "',"
								+ rs1.getGeneralDouble("gd_price")
								+ ","
								+ DateUtil.parseDateToOracleString("yyyy-MM-dd HH:mm:ss", rs1.getString("gd_madedate"))
								+ ","
								+ rs1.getGeneralDouble("gd_minbuyqty")
								+ ","
								+ rs1.getGeneralDouble("gd_minpackqty")
								+ ",'"
								+ rs1.getString("gd_remark")
								+ "','"
								+ rs1.getGeneralDouble("gd_usdprice")
								+ "','"
								+ rs1.getGeneralDouble("gd_deliverytime") + "','" + rs1.getGeneralDouble("gd_hkdeliverytime") + "')");
						offqty = 0;
					}
					detno++;
				} else {
					break;
				}
			}
			if (sqls.size() > 0) {
				Object[] obs = baseDao.getFieldsDataByCondition("prodinout", new String[] { "pi_inoutno", "pi_class" }, "pi_id=" + pi_id);
				// 转入主表
				baseDao.execute("insert into GoodsChange(gc_id,gc_code,gc_indate,gc_inman,gc_status," + "gc_statuscode,gc_type,gc_remark)"
						+ "values(" + id + ",'" + code + "',sysdate,'" + SystemSession.getUser().getEm_name() + "','"
						+ BaseUtil.getLocalMessage("COMMITED") + "','COMMITED','信息变更','" + obs[1] + "[" + obs[0] + "]过账自动生成下架单')");
				// 执行从表
				baseDao.execute(sqls);
				String str = "自动下架，单号:" + "<a href=\"javascript:openUrl('jsps/pm/mps/goodsChange.jsp?formCondition=gc_idIS" + id
						+ "&gridCondition=gcd_gcidIS" + id + "&whoami=GoodsChange')\">" + code + "</a>&nbsp;";
				try {
					goodsChangeService.auditGoodsChange(id, "GoodsChange");
					BaseUtil.showErrorOnSuccess(str + ",审核通过");
				} catch (Exception e) {
					BaseUtil.showErrorOnSuccess(str + ",审核未通过");
				}
			}
		}
	}

	/**
	 * 新增生产退料和委外退料判断，替代料退料数量不能大于替代料已领数量,主料退料不能大于主料已领数,工单外退料的情况不限制
	 * 
	 * @date 2016年10月17日 下午12:30:58
	 * @param pi_id
	 *            单据id
	 */
	private void checkRepQty(int pi_id) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select wm_concat(T.pdno) no from (select nvl(sum(pd_inqty),0) inqty,pd_ordercode,pd_orderdetno,pd_prodcode,max(pd_pdno)pdno "
								+ " from prodiodetail where pd_piid=? group by pd_ordercode,pd_orderdetno,pd_prodcode )T "
								+ " left join makematerial on mm_code=T.pd_ordercode and mm_detno=T.pd_orderdetno "
								+ " where T.pd_prodcode=mm_prodcode and mm_havegetqty-nvl(mm_haverepqty,0)<T.inqty and (nvl(mm_qty,0)>0 or (nvl(mm_qty,0)=0 and nvl(mm_updatetype,' ')<>'R')) and rownum<25",
						pi_id);
		if (rs.next() && rs.getObject("no") != null) {
			BaseUtil.showError("主料退料数不允许大于主料已领数，行号：" + rs.getString(1));
		}
		rs = baseDao
				.queryForRowSet(
						"select wm_concat(T.pdno) no from (select nvl(sum(pd_inqty),0) inqty,pd_ordercode,pd_orderdetno,pd_prodcode,max(pd_pdno)pdno "
								+ " from prodiodetail where pd_piid=? group by pd_ordercode,pd_orderdetno,pd_prodcode )T "
								+ " left join MakeMaterialreplace on mp_mmcode=T.pd_ordercode and mp_mmdetno=T.pd_orderdetno left join makematerial on mm_code=mp_mmcode and mm_detno=mp_mmdetno"
								+ " where T.pd_prodcode=mp_prodcode and T.inqty>nvl(mp_haverepqty,0) and (nvl(mm_qty,0)>0 or (nvl(mm_qty,0)=0 and nvl(mm_updatetype,' ')<>'R')) and rownum<25",
						pi_id);
		if (rs.next() && rs.getObject("no") != null) {
			BaseUtil.showError("替代料退料数不允许大于替代已领数，行号：" + rs.getString(1));
		}
	}

	@Override
	public List<ProdChargeDetail> createProdChargeByKinds(String piclass, final int piid) {
		final List<String> kinds = baseDao.queryForList("select pck_name from ProdChargeKind where pck_purpose=?", String.class, piclass);
		if (!CollectionUtil.isEmpty(kinds)) {
			baseDao.getJdbcTemplate()
					.batchUpdate(
							"insert into ProdChargeDetail(pd_id,pd_piid,pd_detno,pd_type,pd_amount,pd_rate,pd_doubleamount) values(ProdChargeDetail_seq.nextval,?,?,?,0,0,0)",
							new BatchPreparedStatementSetter() {

								@Override
								public void setValues(PreparedStatement ps, int index) throws SQLException {
									ps.setInt(1, piid);
									ps.setInt(2, index + 1);
									ps.setString(3, kinds.get(index));
								}

								@Override
								public int getBatchSize() {
									return kinds.size();
								}
							});
		}
		return baseDao.query("select * from ProdChargeDetail where pd_piid=?", ProdChargeDetail.class, piid);
	}

	@Override
	public void saveProdCharge(String gridStore, String caller) {
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		/**
		 * 问题反馈编号：2017010243 新功能需求。费用明细增加录入人字段，保存更新时记录该录入人。
		 * 
		 * @author wsy
		 */
		String emp = SystemSession.getUser().getEm_name();
		for (Map<Object, Object> map : grid) {
			map.remove("pd_recorder");
			map.put("pd_recorder", emp);
		}
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(grid, "ProdChargeDetail", "pd_id"));
		Object piid = 0;
		if (grid.size() > 0) {
			piid = grid.get(0).get("pd_piid");
		} else {
			BaseUtil.showError("请填写币别");
		}
		SqlRowList rs = baseDao.queryForRowSet("select to_char(pi_date,'yyyymm') pi_yearmonth from prodinout where pi_id=?", piid);
		if (rs.next()) {
			baseDao.execute("update ProdChargeDetail set pd_currency='" + baseDao.getDBSetting("defaultCurrency")
					+ "',pd_rate=1 where pd_piid=" + piid + " and nvl(pd_currency,' ')=' '");
			baseDao.execute("update ProdChargeDetail set pd_rate=nvl((select cm_crrate from currencysmonth where cm_crname=pd_currency and cm_yearmonth="
					+ rs.getObject("pi_yearmonth") + "),0) where pd_piid=" + piid);
			baseDao.execute("update ProdChargeDetail set pd_doubleamount=round(nvl(pd_amount,0)*(case when nvl(pd_rate,0)=0 then 1 else pd_rate end),2) where pd_piid="
					+ piid);
			baseDao.execute("update prodinout set pi_chargeamount=nvl((select sum(round(nvl(pd_doubleamount,0),2)) from ProdChargeDetail where pd_piid=pi_id),0) where pi_id="
					+ piid);
		}
	}

	/**
	 * 富为：采购验收单和出货单生成其它应收单
	 */
	@Override
	public void createOtherBill(String caller, int id) {
		SqlRowList rs = baseDao.queryForRowSet("select * from ProdInOut where pi_id=? and abs(nvl(pi_chargeamount,0))>0", id);
		/**
		 * 反馈编号：2017040636
		 * 采购验收单和出货单的费用明细生成其它应收单的主表的币别默认成本位币(取参数配置code='defaultCurrency')
		 * 
		 * @author wsy
		 */
		String currency = baseDao.getDBSetting("defaultCurrency");
		if (rs.next()) {
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(ab_code) from arbill where ab_class='其它应收单' and AB_SOURCECODE='" + rs.getObject("pi_inoutno")
							+ "' and ab_sourcetype='" + rs.getObject("pi_class") + "'", String.class);
			if (dets != null) {
				BaseUtil.showError("当前" + rs.getGeneralString("pi_class") + "已产生其它应收单[" + dets + "]！");
			}
			String abcode = baseDao.sGetMaxNumber("ARBill", 2);
			int abid = baseDao.getSeqId("ARBILL_SEQ");
			// 其它应收单主表
			baseDao.execute("INSERT INTO ARBill(ab_id,ab_code,ab_class,ab_profitrate,ab_sourceid,AB_SOURCECODE,ab_sourcetype,ab_custcode,"
					+ "ab_custname,ab_date,ab_currency,ab_cop,ab_rate,ab_status,ab_statuscode,ab_paystatus,ab_recorderid,ab_recorder,"
					+ "ab_seller,ab_sellercode,ab_invoamount,ab_invostatus, ab_paydate, ab_payamount,ab_payments,"
					+ "ab_auditstatuscode,ab_auditstatus,ab_printstatus,ab_printstatuscode,ab_paystatuscode,ab_yearmonth,"
					+ "ab_indate,ab_paymentcode,ab_fromcode,ab_refno,ab_departmentcode,ab_departmentname) "
					+ "select "
					+ abid
					+ ", '"
					+ abcode
					+ "', '其它应收单',pi_profitrate,pi_id,pi_inoutno,pi_class,pi_clientcode,pi_clientname,"
					+ "pi_date,'"
					+ currency
					+ "',pi_cop,pi_rate,'未过账','UNPOST','未收款','"
					+ SystemSession.getUser().getEm_id()
					+ "','"
					+ SystemSession.getUser().getEm_name()
					+ "',"
					+ "pi_sellername,pi_sellercode,0,'未开票',pi_paydate, 0,pi_payment,'ENTERING','在录入','未打印','UNPRINT','UNCOLLECT',to_char(pi_date,'yyyymm'),"
					+ "sysdate,pi_paymentcode,pi_inoutno,pi_invoicecode,pi_departmentcode,pi_departmentname "
					+ "from ProdInOut where pi_id=" + id);
			baseDao.execute("update ARBill set ab_custid=(select cu_id from customer where cu_code=ab_custcode) where ab_id=" + abid);
			baseDao.execute("UPDATE ARBill set ab_sellerid=(select em_id from employee where ab_sellercode=em_code) where ab_id=" + abid);
			baseDao.execute("update arbill set ab_rate=(select nvl(cm_crrate,0) from currencysmonth where cm_crname=ab_currency and cm_yearmonth=ab_yearmonth) where ab_id="
					+ abid);
			if (caller.equals("ProdInOut!PurcCheckin")) {
				baseDao.execute("update ARBill set ab_fw01_user='" + rs.getObject("pi_incode") + "' where ab_id=" + abid);
			}
			// 其它应收单明细
			/**
			 * @author wsy 反馈编号：2017040628
			 *         采购验收单和出货单生成其它应收单时，金额不为空时才生成到其它应收单明细中，加了条件PD_AMOUNT>0
			 */
			baseDao.execute("insert into arbilldetail(abd_id,abd_abid,abd_code,abd_detno,abd_cateid,abd_catecode,abd_catename,abd_catetype,abd_remark,abd_qty,abd_price,abd_thisvoprice,abd_aramount,abd_status) "
					+ "select arbilldetail_seq.nextval,"
					+ abid
					+ ",'"
					+ abcode
					+ "',rownum,ca_id,pck_catecode,pck_catename,ca_class,pd_type,1,pd_doubleamount,pd_doubleamount,pd_doubleamount,0 "
					+ "from prodinout left join prodchargedetail on pi_id=pd_piid left join prodchargekind on pi_class=pck_purpose and pd_type=pck_name "
					+ "left join category on ca_code=pck_catecode where pi_id=" + id + " and PD_AMOUNT>0");
			baseDao.execute("update arbill set ab_aramount=round((select sum(abd_aramount) from arbilldetail where abd_abid=ab_id),2) where ab_id="
					+ abid);
			baseDao.execute("update arbill set ab_taxamount=(select sum(round(((abd_thisvoprice*abd_qty*abd_taxrate/100)/(1+abd_taxrate/100)),2)) from arbilldetail where abd_abid=ab_id)+nvl(ab_differ,0) where ab_id="
					+ abid);
			if (caller.equals("ProdInOut!PurcCheckin")) {
				baseDao.execute("update ARBillDetail set abd_fw01_user='" + rs.getObject("pi_incode") + "' where abd_abid=" + abid);
			}
			if (caller.equals("ProdInOut!Sale")) {
				baseDao.execute("update ARBillDetail set abd_fw02_user='" + rs.getObject("pi_inoutno") + "' where abd_abid=" + abid);
			}
			/**
			 * 反馈编号：2017050063
			 * 捷达通：采购验收单、出货单生成其它应收单，若费用明细表中有金额的费用科目设置了【客户往来】辅助核算，
			 * 将出入库单主表委托方编号、委托方名称插入到生成的其它应收单明细行科目【辅助核算】表中
			 * 
			 * @author wsy
			 */
			baseDao.execute("insert into ArbillDetailAss(DASS_ID,DASS_CONDID,DASS_ASSNAME,DASS_CODEFIELD,DASS_NAMEFIELD,DASS_ASSTYPE) "
					+ "select ARBILLDETAILASS_SEQ.NEXTVAL,ABD_ID,ca_assname,'" + rs.getString("pi_clientcode") + "','"
					+ rs.getString("pi_clientname") + "',ca_asstype"
					+ " from ARBillDetail left join Category on abd_catecode=ca_code where abd_abid=" + abid + " and ca_asstype='Cust'");
		} else {
			BaseUtil.showError("没有费用无需产生其它应收单！");
		}
		// 执行操作之后的逻辑
		handlerService.handler(caller, "CreateOtherBill", "after", new Object[] { id });
	}

	/**
	 * 检查批号是否存在，批号数量是否足够
	 */
	private void checkBatchRemain(Object pi_id) {
		int count = baseDao.getCount("select count(1) from documentsetup where ds_name=(select pi_class from prodinout " + "where pi_id="
				+ pi_id + ") and (ds_inorout = '-IN' or ds_inorout = 'OUT')");
		if (count > 0) {
			String pdnos1 = baseDao.getJdbcTemplate().queryForObject(
					"select WMSYS.WM_CONCAT(pd_pdno) from (select pd_pdno  from prodiodetail left join batch on"
							+ " pd_batchcode=ba_code and ba_whcode = pd_whcode and ba_prodcode = pd_prodcode "
							+ "where pd_piid=? and ba_code is null order by pd_pdno) where rownum<20", String.class, pi_id);
			String pdnos2 = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select WMSYS.WM_CONCAT(pd_pdno) from (select WMSYS.WM_CONCAT(pd_pdno) pd_pdno,max(pd_batchcode),"
									+ "sum(pd_outqty),max(ba_remain),max(ba_code) from prodiodetail left join batch on pd_batchcode=ba_code and "
									+ "ba_whcode = pd_whcode and ba_prodcode = pd_prodcode where pd_piid=? group by pd_batchcode,pd_prodcode,pd_whcode"
									+ "  having sum(pd_outqty)>max(ba_remain) ) where rownum<20", String.class, pi_id);
			if (pdnos1 != null || pdnos2 != null) {
				String error1 = pdnos1 == null ? "" : "找不到您所指的批号，请修改批号或点击【重置批号】再过账！行号是：" + pdnos1 + "<br>";
				String error2 = pdnos2 == null ? "" : "批号的库存数小于出库数量，请修改批号或点击【重置批号】再过账!行号是:" + pdnos2 + "<br>";
				BaseUtil.showError(error1 + error2);
			}
		}
	}

	/**
	 * 检验单据类型
	 * 
	 * @param pi_id
	 * @param caller
	 */
	private void checkMaTaskType(Object pi_id, String caller) {
		// 新增限制:生产领料单、生产补料单、生产退料单、完工入库单保存时明细行的工单类型必须是制造工单
		if ("ProdInOut!Picking".equals(caller) || "ProdInOut!Make!Return".equals(caller) || "ProdInOut!Make!Give".equals(caller)
				|| "ProdInOut!Make!In".equals(caller)) {
			String err = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(pd_pdno) no from ProdIODetail left join make on ma_code = pd_ordercode "
							+ "where pd_piid=? and ma_tasktype='OS' and rownum<30", String.class, pi_id);
			if (err != null)
				BaseUtil.showError("工单类型必须为制造工单!行号:" + err);
		}
		// 新增限制:委外领料单、委外补料单、委外退料单、委外验收单、委外验退单保存时明细行的工单类型必须是委外工单
		// 工厂 使用工序的：制造单的作业单为工序委外单通过委外领料单发料 不需要此限制
		if ("ProdInOut!OutsidePicking".equals(caller) || "ProdInOut!OutsideReturn".equals(caller) || "ProdInOut!OSMake!Give".equals(caller)
				|| "ProdInOut!OutsideCheckIn".equals(caller) || "ProdInOut!OutesideCheckReturn".equals(caller)) {
			String err = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(pd_pdno) no from ProdIODetail left join make on ma_code = pd_ordercode left join makecraft on pd_jobcode=mc_code "
							+ "where pd_piid=? and ma_tasktype='MAKE' and nvl(MC_TASKTYPE,' ')<> '工序委外单' and rownum<30", String.class,
					pi_id);
			if (err != null)
				BaseUtil.showError("工单类型必须为委外工单!行号:" + err);
		}
	}

	/**
	 * 问题反馈编号:2017020101. 问题：出入库单的更新仓库按钮没有实现更新方法。
	 * 
	 * @author wsy
	 */
	@Override
	public void updateDetailWH(String pi_id, String codevalue, String value, String pd_inwhcode, String pd_inwhname, String caller) {
		// 只能对状态为[未过账]的单据进行过账操作!
		Object[] status = baseDao.getFieldsDataByCondition("ProdInOut", new String[] { "pi_statuscode", "pi_invostatuscode", "pi_date",
				"pi_class", "pi_recordman", "pi_inoutno" }, "pi_id=" + pi_id);
		if (status[0].equals("POSTED")) {
			BaseUtil.showError("只能对未过账的" + status[3] + "[" + status[5] + "]进行更新明细仓库操作！");
		}
		List<String> list = new ArrayList<String>();
		String sql = "update prodiodetail set pd_whcode='" + codevalue + "',pd_whname='" + value + "' where pd_piid=" + pi_id + "";
		list.add(sql);
		if ("ProdInOut!AppropriationOut".equals(caller) || "ProdInOut!SaleAppropriationOut".equals(caller)) {
			String s1 = "update prodiodetail set pd_inwhcode='" + pd_inwhcode + "',pd_inwhname='" + pd_inwhname + "' where pd_piid='"
					+ pi_id + "'";
			String s2 = "update prodinout set pi_purpose='" + pd_inwhcode + "',pi_purposename='" + pd_inwhname + "' where pi_id=" + pi_id
					+ "";
			list.add(s1);
			list.add(s2);
		}
		String sql2 = "update prodinout set pi_whcode='" + codevalue + "',pi_whname='" + value + "' where pi_id=" + pi_id + "";
		list.add(sql2);
		baseDao.execute(list);
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "更新明细仓库操作", "更新成功", "" + caller + "|pi_id=" + pi_id));
	}

	@Override
	public void checkStatus(int pi_id, String pi_inoutno, String pi_class, String caller) {
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(ab_code) from arbill where ab_class='其它应收单' and AB_SOURCECODE='" + pi_inoutno + "' and ab_sourcetype='"
						+ pi_class + "'", String.class);
		if (dets != null) {
			BaseUtil.showError("已经产生其它应收单，如果需要修改费用，请先删除其它应收单[" + dets + "]！");
		}
	}

	/**
	 * 销售退货单转的出货单的更新出货单明细出库数量，相应的销售退货单已转数量更新。
	 * 
	 * @param s
	 * @param pd
	 */
	public void updateReturnSale(Map<Object, Object> s, SqlRowList pd) {
		Object y = null;
		Object r = null;
		Object sacode = null;
		Object sddetno = 0;
		while (pd.next()) {
			sacode = pd.getObject("pd_ordercode");
			sddetno = pd.getObject("pd_orderdetno");
			Object pd_ioid = baseDao.getFieldDataByCondition("ProdIODetail", "pd_ioid", "pd_id='" + s.get("pd_id") + "'");
			y = baseDao.getFieldDataByCondition("ProdIODetail", "sum(nvl(pd_outqty,0))",
					"pd_ioid='" + pd_ioid + "' and pd_piclass='出货单' and pd_ordercode='" + sacode + "' and pd_orderdetno=" + sddetno
							+ " and pd_id<>" + s.get("pd_id"));
			r = baseDao.getFieldDataByCondition("ProdIODetail left join ProdInOut on pd_piid=pi_id", "nvl(pd_inqty,0)",
					"pd_piclass='销售退货单' and pi_statuscode='POSTED' and pd_ordercode='" + sacode + "' and pd_orderdetno=" + sddetno);
			y = y == null ? 0 : y;
			r = r == null ? 0 : r;
			if (Double.parseDouble(s.get("pd_outqty").toString()) + Double.parseDouble(y.toString()) > Double.parseDouble(r.toString())) {
				BaseUtil.showError("出库数量总数超过销售退货单退货数量");
			}
			baseDao.execute("update ProdIODetail set pd_yqty="
					+ (Double.parseDouble(y.toString()) + Double.parseDouble(s.get("pd_outqty").toString())) + " where pd_id='" + pd_ioid
					+ "'");
		}
	}

	/**
	 * 发送EDI
	 * 
	 * @param id
	 * @param caller
	 */
	public void sendEdi(String id, String caller) {
		String out = baseDao.callProcedure("SP_GENERATEEDI", new Object[] { id, caller });
		if (out != null) {
			BaseUtil.showError(out);
		}
		String keyField = baseDao.getFieldValue("form", "fo_keyfield", "fo_caller='" + caller + "'", String.class);
		if (keyField != null && !"".equals(keyField)) {
			baseDao.logger.others("发送EDI", "发送成功", caller, keyField, id);
		}
	}

	/**
	 * 退单
	 * 
	 * @param id
	 * @param caller
	 */
	public void cancelEdi(String id, String caller, String remark) {
		String out = baseDao.callProcedure("SP_CANCELEDI", new Object[] { id, caller, remark });
		if (out != null) {
			BaseUtil.showError(out);
		}
		String keyField = baseDao.getFieldValue("form", "fo_keyfield", "fo_caller='" + caller + "'", String.class);
		if (keyField != null && !"".equals(keyField)) {
			baseDao.logger.others("EDI退单", "发送成功", caller, keyField, id);
		}
	}

	@Override
	public void confirmIn(String caller, int pi_id) {
		Object[] status = baseDao.getFieldsDataByCondition("ProdInOut", new String[] { "pi_pdastatus", "pi_class", "pi_inoutno" }, "pi_id="
				+ pi_id);
		if ("已入库".equals(status[0])) {
			BaseUtil.showError("只能对未入库的" + status[1] + "进行确认入库操作！");
		}
		// 执行存储过程
		Object[] objs = baseDao.getFieldsDataByCondition("ProdInOut", new String[] { "pi_class", "pi_inoutno" }, "pi_id=" + pi_id);
		String res = baseDao.callProcedure("SP_BARCODEIO_IN", new Object[] { objs[0].toString(), objs[1].toString(), "" });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
		// 记录操作
		baseDao.execute(baseDao.logger.getMessageLog("确认入库", "确认入库成功", caller, "pi_id", pi_id).getSql());

	}

	// @add 20170524 出库类型的单据，保存，更新，获取批号之后更新备料状态
	private void updatePdaStatus(String caller, Object pi_id) {
		if (prodInOutDao.isOut(caller)) { // 出库类型
			// 如果所有的明细都没有条码则更新为空
			SqlRowList rs = baseDao.queryForRowSet("select count(1)cn from prodiodetail where pd_piid=? and nvl(pd_batchcode,' ')<>' '",
					pi_id);
			if (rs.next() && rs.getInt("cn") == 0) {
				baseDao.execute("update prodinout set pi_pdastatus='' where pi_id=? and nvl(pi_pdastatus,' ')<>' '", pi_id);
			} else {
				rs = baseDao.queryForRowSet("select count(1)cn from barcodeio where bi_piid=? and bi_outqty>0", pi_id);
				if (rs.next() && rs.getInt("cn") > 0) { // 有一行以上备料记录则是备料中
					baseDao.execute("update prodinout set pi_pdastatus ='备料中' where pi_id=?", pi_id);
					rs = baseDao
							.queryForRowSet(
									"select count(1) cn from (select pd_prodcode,pd_whcode,sum(pd_outqty)qty from prodiodetail left join batch on pd_batchid=ba_id "
											+ " where pd_piid=? and ba_hasbarcode<>0 group by pd_prodcode,pd_whcode)A left join (select bi_prodcode,bi_whcode,sum(bi_outqty)qty "
											+ " from barcodeio where bi_piid=? group by bi_prodcode,bi_whcode)B on (pd_prodcode=bi_prodcode and pd_whcode=bi_whcode) where A.qty>NVL(B.qty,0)",
									pi_id, pi_id);
					if (rs.next() && rs.getInt("cn") == 0) {
						baseDao.execute("update prodinout set pi_pdastatus ='已备料' where pi_id=?", pi_id);
					}
				} else {
					// --存在有条码的批号则更新成未备料
					baseDao.execute(
							"update prodinout set pi_pdastatus ='未备料' where pi_id=?  and exists(select 1 from prodiodetail,batch where pd_piid=? and pd_batchid=ba_id and ba_hasbarcode<>0)",
							pi_id, pi_id);
					// --所有批号都无条码则更新成无条码
					baseDao.execute(
							"update prodinout set pi_pdastatus ='无条码' where pi_id=? and not exists(select 1 from prodiodetail left join batch on ba_id=pd_batchid where pd_piid=?  and ba_hasbarcode=-1)",
							pi_id, pi_id);
				}
			}
		}
	}

	@Override
	public void feeShare(Long id, String caller) {
		double fee1 = 0;
		double fee2 = 0;
		Double rate = 0.0;
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select to_char(pi_date,'yyyymm') pi_yearmonth,pi_currency,pi_rate from prodinout left join ProdChargeDetail on pd_piid=pi_id where pi_id=?",
						id);
		if (rs.next()) {
			String currency = baseDao.getDBSetting("defaultCurrency");
			rate = rs.getGeneralDouble("pi_rate");
			if (rate == 0) {
				rate = 1.0;
			}
			baseDao.execute("update ProdChargeDetail set pd_currency='" + currency + "',pd_rate=1 where pd_piid=" + id
					+ " and nvl(pd_currency,' ')=' '");
			baseDao.execute("update ProdChargeDetail set pd_rate=nvl((select cm_crrate from currencysmonth where cm_crname=pd_currency and cm_yearmonth="
					+ rs.getObject("pi_yearmonth") + "),0) where pd_piid=" + id + " and nvl(pd_rate,0)=0");
			baseDao.execute("update ProdChargeDetail set pd_doubleamount=round(nvl(pd_amount,0)*(case when nvl(pd_rate,0)=0 then 1 else pd_rate end),2) where pd_piid="
					+ id);
			double amount = baseDao.getSummaryByField("ProdChargeDetail", "pd_doubleamount", "pd_piid=" + id);
			baseDao.execute("update prodinout set pi_chargeamount=" + amount + " where pi_id=" + id);
			double qty = baseDao.getSummaryByField("ProdIODetail", "pd_inqty", "pd_piid=" + id);
			if (qty != 0) {
				fee2 = NumberUtil.formatDouble(amount / qty, 8);
			}
			double pitotal = baseDao.getSummaryByField("ProdIODetail", "pd_ordertotal", "pd_piid=" + id);
			if (pitotal != 0) {
				fee1 = NumberUtil.formatDouble(amount / pitotal, 8);
			}
		}
		if ("2".equals(baseDao.getDBSetting(caller, "FeePrinciple"))) {// 数量
			baseDao.execute("update prodiodetail set pd_fee=" + fee2 + ",pd_price=" + fee2 + "+round(nvl(pd_orderprice,0)*" + rate
					+ "/(1+nvl(pd_taxrate,0)/100),8) where pd_piid=" + id);
		} else {
			baseDao.execute("update prodiodetail set pd_fee=" + fee1 + ",pd_price=" + fee1 + "+round(nvl(pd_orderprice,0)*" + rate
					+ "/(1+nvl(pd_taxrate,0)/100),8) where pd_piid=" + id);
		}
		baseDao.execute("update prodiodetail set pd_total=round(nvl(pd_price,0)*nvl(pd_inqty,0),2) where pd_piid=" + id);
	}

	@Override
	public String resPostCheck(String caller, int pi_id) {
		if (prodInOutDao.isIn(caller)) {
			Object pdnos = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select WMSYS.WM_CONCAT(DISTINCT a.pd_pdno) pd_pdno from "
									+ "prodiodetail a left join (select pd_batchcode,pd_prodcode,pd_whcode,pi_inoutno,pi_vouchercode "
									+ "from prodiodetail left join prodinout on pd_piid=pi_id where  pi_class='成本调整单' and pi_statuscode='POSTED'"
									+ " and nvl(pi_vouchercode,' ')=' ') b on a.pd_batchcode=b.pd_batchcode and a.pd_prodcode=b.pd_prodcode"
									+ "	and a.pd_whcode=b.pd_whcode where a.pd_piid=?  and b.pi_inoutno is not null and nvl(b.pi_vouchercode,' ')=' '",
							String.class, pi_id);
			if (pdnos != null) {
				return pdnos.toString();
			}
		}
		return "";
	}

	private void changeMaStatus(int pi_id) {
		String ids = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(ma_id) from (select distinct ma_id from ProdIODetail left join make on ma_code=pd_ordercode where pd_piid=?)",
						String.class, pi_id);
		if (ids != null) {
			makeDao.updateMakeGetStatus(ids);
		}
	}

	/**
	 * 西博泰科 采购验收转销售订单
	 */
	public int turnSale(int id, String caller) {
		Object pi_code = baseDao.getFieldDataByCondition("Prodinout", "pi_inoutno", "pi_id=" + id);
		Employee employee = SystemSession.getUser();
		Object[] cu = baseDao.getFieldsDataByCondition("customer", new String[] { "cu_code", "cu_name" }, "cu_code='AB17030003'");
		List<Object[]> griddata = baseDao.getFieldsDatasByCondition("ProdIODetail", new String[] { "pd_prodcode", "pd_inqty",
				"pd_orderprice", "pd_price", "pd_taxrate", "pd_ordertotal", "pd_total" }, "pd_piid=" + id);
		SqlRowList rs = baseDao.queryForRowSet("select * from sale where sa_sourcecode='" + pi_code + "'");
		if (rs.next()) {
			BaseUtil.showError("该采购验收单已经转入销售订单,订单号:<a href=\"javascript:openUrl('jsps/scm/sale/sale.jsp?formCondition=sa_codeIS"
					+ rs.getString("sa_code") + "&gridCondition=sd_codeIS" + rs.getString("sa_code") + "')\">" + rs.getString("sa_code")
					+ "</a>&nbsp;");
		}
		Object code = baseDao.sGetMaxNumber("Sale", 2);
		Object sa_id = baseDao.getSeqId("SALE_SEQ");
		int detno = 1;
		String formsql = "insert into Sale(sa_id,sa_code,sa_date,sa_status,sa_statuscode,sa_sourcetype,sa_sourcecode,sa_recorderid,sa_recorder,sa_custcode,sa_apcustcode,sa_shcustcode,sa_custname,sa_apcustname,sa_shcustname) values ("
				+ sa_id
				+ ",'"
				+ code
				+ "',sysdate,'在录入','ENTERING','采购验收单','"
				+ pi_code
				+ "',"
				+ employee.getEm_id()
				+ ",'"
				+ employee.getEm_name()
				+ "','"
				+ cu[0]
				+ "','"
				+ cu[0]
				+ "','"
				+ cu[0]
				+ "','"
				+ cu[1]
				+ "','"
				+ cu[1]
				+ "','"
				+ cu[1]
				+ "')";
		for (Object[] obj : griddata) {
			Object sd_id = baseDao.getSeqId("SALEDETAIL_SEQ");
			String gridsql = "insert into SaleDetail(sd_code,sd_detno,sd_id,sd_said,sd_prodcode,sd_qty,sd_costprice,sd_costingprice,sd_taxrate,sd_taxtotal,sd_price,sd_total) values ('"
					+ code
					+ "',"
					+ detno
					+ ","
					+ sd_id
					+ ","
					+ sa_id
					+ ",'"
					+ obj[0]
					+ "',"
					+ obj[1]
					+ ","
					+ obj[3]
					+ ","
					+ obj[3]
					+ ","
					+ obj[4] + "," + obj[6] + "," + obj[2] + "," + obj[5] + ")";
			detno++;
			baseDao.execute(gridsql);
		}
		baseDao.execute(formsql);
		baseDao.updateByCondition("Sale", "sa_auditdate='',sa_updatedate=''", "sa_id=" + sa_id);
		baseDao.logger.others("转单操作", "采购验收单转销售订单", "Sale", "sa_id", sa_id);
		return Integer.parseInt(sa_id.toString());
	}

	@Override
	public List<Map<Object, Object>> turnEdiToProdin(String ids) {
		String out = baseDao.callProcedure("SP_CSVTOPRODINOUT", new Object[] { ids, SystemSession.getUser().getEm_code() });
		if (out == null) {
			BaseUtil.showError("该发票号对应的入库单已存在");
		} else if ("false" == out) {
			BaseUtil.showError("程序错误");
		}
		List<Map<Object, Object>> outList = BaseUtil.parseGridStoreToMaps(out);
		return outList;
	}

	@Override
	public void markEdiAsDone(String ids, String caller) {
		baseDao.execute("update csvdatalog set import_=1 where cl_invoiceno in (select cl_invoiceno from csvdatalog where cl_id in (" + ids
				+ "))");
	}

	@Override
	public void catchBatchBySeller(Long pi_id, String caller) {
		Object[] o = baseDao.getFieldsDataByCondition("ProdInOut", new String[] { "pi_statuscode", "pi_class", "pi_inoutno" }, "pi_id="
				+ pi_id);
		if (o.length == 3) {
			if (o[0].equals("POSTED")) {
				BaseUtil.showError("已过账的单据不能抓取批号");
			}
			String type = baseDao.getDBSetting("BarCodeSetting", "ProdOutType");
			if ("byBatch".equals(type) || "byProdcode".equals(type)) {
				int cn = baseDao.getCount("select count(1) from barcodeio where bi_piid=" + pi_id);
				if (cn > 0) {
					BaseUtil.showError("已有采集的条码，不允许抓取批号，如需操作请撤销已采集的数据");
				}
			}
			String res = baseDao.callProcedure("SP_SPLITPRODOUT_BYSELLER",
					new Object[] { o[1].toString(), o[2].toString(), String.valueOf(SystemSession.getUser().getEm_name()) });
			if (res != null && !res.trim().equals("")) {
				BaseUtil.showError(res);
			}
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "按业务员抓取批号", "抓取批号成功", caller + "|pi_id=" + pi_id));
		}
	}

	@Override
	public String getCancelConfig(String caller) {
		Object data = baseDao.getFieldDataByCondition("configs", "data", "code='cancelProdInOut' and caller='sys'");
		if (StringUtil.hasText(data)) {
			return data.toString();
		} else {
			return "";
		}
	}

	@Override
	public void updateCancelReason(int id, String value, String caller) {
		baseDao.execute("update prodinout set pi_cancelreason='" + value + "' where pi_id=" + id);
	}

	/**
	 * 根据取价原则取销售价格表的价格给出货单。 maz 2018010522
	 */
	private void getSalePrice(Object id) {
		String datas = baseDao.getDBSetting("ProdInOut!Sale", "getPriceTenet");
		if (datas == null) {// 默认为客户+币别+料号
			datas = "A";
		}
		SqlRowList rs = baseDao.queryForRowSet(
				"SELECT * FROM ProdIODetail  LEFT JOIN ProdInOut on pi_id=pd_piid left join customer on pi_cardcode=cu_code WHERE pi_id=?",
				id);
		Object price = null;
		if (!StringUtil.hasText(rs.getObject("pd_ordercode")) && !StringUtil.hasText(rs.getObject("pd_orderdetno"))) {
			while (rs.next()) {
				if (datas.equals("A")) {
					price = baseDao.getFieldDataByCondition(
							"(select * from (select spd_price,spd_id from SalePriceDetail LEFT JOIN SalePrice on sp_id=spd_spid "
									+ "where spd_arcustcode='" + rs.getString("pi_cardcode") + "' and spd_prodcode='"
									+ rs.getString("pd_prodcode") + "' " + "and spd_currency='" + rs.getString("pi_currency")
									+ "' and spd_statuscode='VALID' " + "order by sp_indate desc) order by spd_id desc) ", "spd_price",
							"rownum<2");// 客户+币别+料号
				} else if (datas.equals("B")) {
					price = baseDao.getFieldDataByCondition(
							"(select * from (select spd_price,spd_id from SalePriceDetail LEFT JOIN SalePrice on sp_id=spd_spid "
									+ "where spd_pricetype='" + rs.getString("cu_pricetype") + "' and spd_prodcode='"
									+ rs.getString("pd_prodcode") + "' " + "and spd_currency='" + rs.getString("pi_currency")
									+ "' and spd_statuscode='VALID' " + "order by sp_indate desc) order by spd_id desc) ", "spd_price",
							"rownum<2");// 取价类型+币别+料号
				} else if (datas.equals("C")) {
					price = baseDao.getFieldDataByCondition(
							"(select * from (select spd_price,spd_id from SalePriceDetail LEFT JOIN SalePrice on sp_id=spd_spid "
									+ "where spd_prodcode='" + rs.getString("pd_prodcode") + "' " + "and spd_currency='"
									+ rs.getString("pi_currency") + "' and spd_statuscode='VALID' "
									+ "order by sp_indate desc) order by spd_id desc) ", "spd_price", "rownum<2");// 料号+币别
				} else if (datas.equals("D")) {
					price = baseDao.getFieldDataByCondition(
							"(select * from (select spd_price,spd_id from SalePriceDetail LEFT JOIN SalePrice on sp_id=spd_spid "
									+ "where spd_arcustcode='" + rs.getString("pi_cardcode") + "' and spd_prodcode='"
									+ rs.getString("pd_prodcode") + "' " + "and spd_currency='" + rs.getString("pi_currency")
									+ "' and spd_statuscode='VALID' and spd_taxrate=" + rs.getDouble("pd_taxrate") + " "
									+ "order by sp_indate desc) order by spd_id desc) ", "spd_price", "rownum<2");// 客户+币别+料号+税率
				}
				if (price != null && Double.parseDouble(price.toString()) != 0) {
					if (!StringUtil.hasText(rs.getObject("pd_purcprice"))
							|| (StringUtil.hasText(rs.getObject("pd_purcprice")) && "0".equals(rs.getObject("pd_purcprice").toString()))) {
						baseDao.updateByCondition("ProdIODetail", "pd_purcprice=" + price + ",pd_sendprice=" + price + "",
								"pd_id=" + rs.getInt("pd_id"));
					} else {
						baseDao.updateByCondition("ProdIODetail", "pd_purcprice=" + price + "", "pd_id=" + rs.getInt("pd_id"));
					}
				}
			}
		}
	}

	@Override
	public void getFittingData(String pr_code, String pi_id, String qty, String detno, String caller) {
		SqlRowList rs = null;
		rs = baseDao.queryForRowSet("select * from prodinout left join ProdIODetail on pi_id=pd_piid where pi_id='" + pi_id
				+ "' and PD_LOADFITTING='" + detno + "'");
		if (rs.next()) {
			BaseUtil.showError("序号：" + detno + "已经载入了配件，不允许重复载入！");
		}
		int detno2 = 0;
		rs = baseDao.queryForRowSet("select max(pd_pdno) det from prodinout left join ProdIODetail on pi_id=pd_piid where pi_id='" + pi_id
				+ "'");
		if (rs.next()) {
			detno2 = rs.getInt("det");
		}
		rs = baseDao
				.queryForRowSet("select fbd_prodcode,fbd_qty from fittingbom left join fittingbomdetail on fb_id=fbd_fbid where fb_prodcode='"
						+ pr_code + "'and fb_statuscode='AUDITED'");
		if (rs.hasNext()) {
			while (rs.next()) {
				int id = baseDao.getSeqId("PRODIODETAIL_SEQ");
				String prodCode = rs.getString("fbd_prodcode");
				double pdqty = NumberUtil.formatDouble(rs.getFloat("fbd_qty") * Integer.valueOf(qty), 4);
				String sql = "insert into prodiodetail (PD_ID,PD_PIID,pd_prodcode,PD_LOADFITTING,pd_outqty,pd_pdno) values(" + id + ","
						+ pi_id + ",'" + prodCode + "','" + detno + "'," + pdqty + "," + ++detno2 + " )";
				baseDao.execute(sql);
			}
		} else {
			BaseUtil.showError("序号：" + detno + "不存在配件！");
		}
	}

	// 检查送货单号是否一致
	public void checkSendCode(int pi_id) {
		SqlRowList rs = baseDao
				.queryForRowSet("select pd_pdno from prodinout left join prodiodetail on pi_id=pd_piid left join VerifyApply on va_code=pd_vacode where pi_id="
						+ pi_id + " and nvl(pi_sendcode,' ')<>nvl(va_sendcode,' ')");
		if (rs.next()) {
			BaseUtil.showError("行号:" + rs.getInt("pd_pdno") + "送货单号不一致，不允许操作");
		}
	}
}
