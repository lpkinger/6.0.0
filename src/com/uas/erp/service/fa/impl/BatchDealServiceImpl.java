package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.lang.xwork.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.dao.common.APBillDao;
import com.uas.erp.dao.common.ARBillDao;
import com.uas.erp.dao.common.CustMonthDao;
import com.uas.erp.dao.common.PayPleaseDao;
import com.uas.erp.dao.common.ProdInOutDao;
import com.uas.erp.dao.common.VendMonthDao;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.AccountRegisterBankService;
import com.uas.erp.service.fa.BatchDealService;
import com.uas.erp.service.fa.BillOutAPService;
import com.uas.erp.service.fa.BillOutService;
import com.uas.erp.service.fa.PayBalanceService;
import com.uas.erp.service.fa.PreRecService;
import com.uas.webkit.sse.Emitter;

@Service("FaBatchDealService")
public class BatchDealServiceImpl implements BatchDealService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private ProdInOutDao prodInOutDao;
	@Autowired
	private PayPleaseDao payPleaseDao;
	@Autowired
	private CustMonthDao custMonthDao;
	@Autowired
	private VendMonthDao vendMonthDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private AccountRegisterBankService accountRegisterBankService;
	@Autowired
	private BillOutAPService billOutAPService;
	@Autowired
	private BillOutService billOutService;
	@Autowired
	private PayBalanceService payBalanceService;
	@Autowired
	private PreRecService preRecService;
	@Autowired
	private ARBillDao ARBillDao;
	@Autowired
	private APBillDao APBillDao;
	@Autowired
	private TransferRepository transferRepository;

	/**
	 * 开票前检测
	 * 
	 * @param abCode
	 *            指定了发票
	 * @param currency
	 * @param vendcode
	 */
	private void checkBeforeTurnArBill(List<Map<Object, Object>> maps, final String abCode, final String abCurrency, final String abCustCode) {
		List<String> errors = new ArrayList<String>();
		for (Map<Object, Object> map : maps) {
			String piclass = map.get("pi_class").toString().trim();
			Object gsdid = map.get("pd_togsdid").toString();
			Integer pdid = Integer.parseInt(map.get("pd_id").toString());
			Object currency = map.get("pi_currency");
			Object cardcode = map.get("pi_arcode");
			double tqty = Double.parseDouble(map.get("pd_thisvoqty").toString());
			if (tqty == 0) {
				errors.add(String.format("【%s】行【%s】的开票数量为0，不能开票!", map.get("pi_inoutno"), map.get("pd_pdno")));
				continue;
			}
			if (!StringUtils.isEmpty(abCode) && (!abCurrency.equals(currency.toString()) || !abCustCode.equals(cardcode.toString()))) {
				errors.add(BaseUtil.getLocalMessage("fa.ars.arbill.turn_onlyCurrencySame")
						+ "<a href=\"javascript:openUrl('jsps/fa/ars/arbill.jsp?formCondition=ab_codeIS" + abCode
						+ "&gridCondition=abd_codeIS" + abCode + "&whoami=ARBill!IRMA')\">" + abCode + "</a>&nbsp;");
				continue;
			}
			boolean hasError = false;
			Object pd_msg[] = null;
			if (piclass.equals("出货单") || piclass.equals("销售退货单")) {
				pd_msg = baseDao.getFieldsDataByCondition("ProdIODetail", new String[] { "nvl(pd_outqty,0)", "nvl(pd_inqty,0)",
						"nvl(pd_showinvoqty,0)", "nvl(pd_turngsqty,0)" }, "pd_id='" + pdid + "'");// 出库数量
																									// ,入库数量,已转数量
				if (Double.parseDouble((pd_msg[0].toString())) > 0 && Double.parseDouble((pd_msg[1].toString())) == 0) {
					if (Math.abs(Double.parseDouble((pd_msg[2].toString()))) + Math.abs(tqty)
							+ Math.abs(Double.parseDouble((pd_msg[3].toString()))) > Math.abs(Double.parseDouble((pd_msg[0].toString())))) {
						hasError = true;
					}
				}
				if (Double.parseDouble((pd_msg[1].toString())) > 0 && Double.parseDouble((pd_msg[0].toString())) == 0) {
					if (Math.abs(Double.parseDouble((pd_msg[2].toString()))) + Math.abs(tqty)
							+ Math.abs(Double.parseDouble((pd_msg[3].toString()))) > Math.abs(Double.parseDouble((pd_msg[1].toString())))) {
						hasError = true;
					}
				}
			} else if (piclass.equals("发出商品")) {
				pd_msg = baseDao.getFieldsDataByCondition("GoodsSendDetail", new String[] { "abs(nvl(gsd_qty,0))",
						"abs(nvl(gsd_showinvoqty,0))" }, "gsd_id='" + gsdid + "'");// 出库数量
				if (Math.abs(Double.parseDouble((pd_msg[1].toString()))) + Math.abs(tqty) > Math.abs(Double.parseDouble((pd_msg[0]
						.toString())))) {
					hasError = true;
				}
			}
			if (hasError)
				errors.add(String.format("【%s】行【%s】的本次开票数量超过最大可开票数量，不能开票!", map.get("pi_inoutno"), map.get("pd_pdno")));
		}
		if (errors.size() > 0) {
			BaseUtil.showError(CollectionUtil.toString(errors, "/n"));
		}
	}

	private void turnArBill(List<Map<Object, Object>> maps, final String abCode, Object differ, final String caller) {
		Object ab_id = baseDao.getFieldDataByCondition("ARBill", "ab_id", "ab_code='" + abCode + "'");
		// transferRepository.transfer(caller, maps, new
		// Key(Integer.parseInt(ab_id.toString()), abCode));
		final Employee employee = SystemSession.getUser();
		Object ifbypi = maps.get(0).containsKey("ifbypi") ? maps.get(0).get("ifbypi") : null; // 天派有这个
		int index = 1;
		int size = maps.size();
		for (Map<Object, Object> map : maps) {
			String piclass = map.get("pi_class").toString().trim();
			String gsdid = map.get("pd_togsdid").toString();
			Integer pdid = Integer.parseInt(map.get("pd_id").toString());
			double tqty = Double.parseDouble(map.get("pd_thisvoqty").toString());
			Double tprice = Double.parseDouble(map.get("pd_thisvoprice").toString());
			if (piclass.equals("出货单") || piclass.equals("销售退货单")) {
				// 转入明细
				prodInOutDao.toAppointedARBill(abCode, pdid, tqty, tprice, "PRODIODETAIL", String.valueOf(pdid), ifbypi);
				baseDao.updateByCondition("ProdIODetail", "pd_showinvoqty=nvl(pd_showinvoqty,0)+" + tqty, "pd_id=" + pdid);
			} else if (piclass.equals("发出商品")) {
				prodInOutDao.toAppointedARBill(abCode, pdid, tqty, tprice, "GOODSSEND", gsdid, ifbypi);
				baseDao.updateByCondition("GoodsSendDetail", "gsd_showinvoqty=nvl(gsd_showinvoqty,0)+" + tqty, "gsd_id=" + gsdid);
			}
			// 记录日志
			baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.turnARBill"), BaseUtil
					.getLocalMessage("msg.turnSuccess"), caller + "|pd_id=" + map.get("pd_id")));
			Emitter.progress((float) index++ / size);
		}
		baseDao.execute("update arbill set ab_custname=(select cu_name from customer where ab_custcode=cu_code) WHERE ab_id=" + ab_id);
		baseDao.execute("update arbilldetail set abd_code=(select ab_code from arbill where abd_abid=ab_id) WHERE abd_abid=" + ab_id);
		baseDao.execute("update arbilldetail set abd_aramount=ROUND(abd_thisvoprice*abd_qty,2),abd_noaramount=ROUND(abd_thisvoprice*abd_qty/(1+abd_taxrate/100),2) WHERE abd_abid="
				+ ab_id);
		baseDao.execute("update arbilldetail set abd_taxamount=NVL(abd_aramount,0)-NVL(abd_noaramount,0) WHERE abd_abid=" + ab_id);
		baseDao.execute("update arbill set ab_rate=(select nvl(cm_crrate,0) from currencysmonth where cm_crname=ab_currency and cm_yearmonth=ab_yearmonth) where ab_id="
				+ ab_id);
		baseDao.execute("UPDATE ARBill SET (ab_departmentcode,ab_departmentname,ab_cop)=(select pi_departmentcode,pi_departmentname,pi_cop from prodinout,prodiodetail where pi_id=pd_piid and pd_id=(select abd_pdid FROM ARBillDetail WHERE abd_abid=ab_id and abd_detno=1)) where ab_id="
				+ ab_id);
		baseDao.execute("update arbilldetail set (abd_custprodcode,abd_pocode)=(select pd_custprodcode,pd_pocode from prodiodetail where abd_pdid=pd_id) where abd_abid = "
				+ ab_id + " and nvl(abd_custprodcode,' ')=' '");
		// 更新ARBill主表的金额
		baseDao.execute("update arbill set ab_aramount=round((select sum(abd_aramount) from arbilldetail where abd_abid=ab_id),2) where ab_id="
				+ ab_id);
		baseDao.execute("update ARBill set ab_differ="
				+ differ
				+ ", ab_taxamount=nvl((select sum(round(((abd_thisvoprice*abd_qty*abd_taxrate/100)/(1+abd_taxrate/100)),2)) from arbilldetail where abd_abid=ab_id),0)+"
				+ differ + " where ab_id=" + ab_id);
	}

	@Transactional
	@Override
	public String vastTurnARBill(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Object code = null;// 指定发票单号
		Object tradecode = null;
		Object tradename = null;
		Object refno = null;
		Object currency = null;
		Object cardcode = null;
		Object date = null; // 指定开票日期
		Object sendtype = null;
		Object ifbypi = null; // 是否按PI号分组
		Object picode = null;
		Object differ = null; // 税金差异
		Object taxamount = null; // 税金合计
		int pdid = 0;
		String gsdid = "0";
		StringBuffer sb = new StringBuffer();
		Set<String> ab_codes = new HashSet<String>();
		String log = null;
		int index = 0;
		if (maps.size() > 0) {
			handlerService.handler(caller, "vastTurnARBill", "before", new Object[] { maps });
			code = maps.get(0).get("ab_code");
			date = maps.get(0).get("ab_date");
			tradecode = maps.get(0).get("ab_tradecode");
			tradename = maps.get(0).get("ab_tradename");
			refno = maps.get(0).get("ab_refno");
			sendtype = maps.get(0).get("ab_sendtype");
			differ = maps.get(0).containsKey("ab_differ") ? maps.get(0).get("ab_differ") : 0; // 税金差异
			taxamount = maps.get(0).containsKey("ab_taxamount") ? maps.get(0).get("ab_taxamount") : null; // 税金合计
			ifbypi = maps.get(0).containsKey("ifbypi") ? maps.get(0).get("ifbypi") : null; // 天派有这个
			if (sendtype == null) {
				BaseUtil.showError("请选择发票类型!");
			}
			int yearmonth = Integer.parseInt(DateUtil.currentDateString("yyyyMM"));
			if (StringUtil.hasText(date)) {
				yearmonth = Integer.parseInt(date.toString().replace("T", "").replace("-", "").substring(0, 6));
			}
			String dets = null;
			String pdidstr = "";
			List<String> detailids = new ArrayList<String>();
			for (Map<Object, Object> map : maps) {
				if ("出货单".equals(map.get("pi_class")) || "销售退货单".equals(map.get("pi_class"))) {
					pdidstr += "," + map.get("pd_id").toString();
				}
				detailids.add(map.get("pd_id").toString());
			}
			if (detailids.size() > 0) {
				// 明细行勾选可能超过1000，需要拆分
				List<List<String>> idList = CollectionUtil.split(detailids, 500);
				for (List<String> idls : idList) {
					String ids = CollectionUtil.toString(idls);
					dets = baseDao.getJdbcTemplate().queryForObject(
							"SELECT LOB_CONCAT('单号【'||PI_INOUTNO||'】行号【'||PD_PDNO||'】') FROM FA_BATCH_ARBILL_VIEW where pd_id in(" + ids
									+ ") and pi_class<>'发出商品' and to_char(pi_date,'yyyymm')<>" + yearmonth, String.class);
					if (dets != null) {
						BaseUtil.showError("勾选的出入库明细与开票日期不在同一个月，请先转发出商品再开票或修改开票日期！" + dets);
					}
					dets = baseDao.getJdbcTemplate().queryForObject(
							"SELECT LOB_CONCAT(pi_inoutno) from (Select distinct pi_inoutno FROM FA_BATCH_ARBILL_VIEW where pd_id in("
									+ ids + ") and to_char(pi_date,'yyyymm')>" + yearmonth + ")", String.class);
					if (dets != null) {
						BaseUtil.showError("开票日期必须大于等于单据日期月份！单号：" + dets);
					}
				}
			}
			if (pdidstr.length() > 0) {
				dets = baseDao.getJdbcTemplate().queryForObject(
						"select LOB_CONCAT(pi_inoutno) from (Select distinct pi_inoutno from prodiodetail "
								+ "left join prodinout on pd_piid=pi_id where pd_id in(" + pdidstr.substring(1)
								+ ") and nvl(pi_statuscode,' ')<>'POSTED')", String.class);
				if (dets != null) {
					BaseUtil.showError("单据未过账不能开票!单号：" + dets);
				}
			}
			// 指定了发票号
			if (code != null && !code.equals("")) {
				Object status[] = baseDao.getFieldsDataByCondition("ARBill", new String[] { "ab_auditstatuscode", "ab_statuscode",
						"ab_currency", "ab_custcode", "ab_cop", "ab_seller", "ab_paymentcode", "ab_departmentcode", "ab_id" }, "ab_code='"
						+ code + "'");
				if (status == null) {
					BaseUtil.showError("指定的发票号" + code + "不存在，不能开票！");
				}
				// 指定的发票状态是否[在录入]且[未过账]
				if (status[0].toString().equals("ENTERING") && status[1].toString().equals("UNPOST")) {

				} else {
					BaseUtil.showError(BaseUtil.getLocalMessage("fa.ars.arbill.turn_onlyEnteringAndUnpost")
							+ "<a href=\"javascript:openUrl('jsps/fa/ars/arbill.jsp?formCondition=ab_codeIS" + code
							+ "&gridCondition=abd_codeIS" + code + "&whoami=ARBill!IRMA')\">" + code + "</a>&nbsp;");
				}
				/* 判断各个明细行开票数量是否够用 */
				checkBeforeTurnArBill(maps, code.toString(), status[2].toString(), status[3].toString());
				/* 判断结束 */
				for (Map<Object, Object> map : maps) {
					gsdid = map.get("pd_togsdid").toString();
					pdid = Integer.parseInt(map.get("pd_id").toString());
					Object[] info = null;
					if ("发出商品".equals(map.get("pi_class"))) {
						info = baseDao.getFieldsDataByCondition("goodssenddetail LEFT JOIN goodssend ON gsd_gsid = gs_id", new String[] {
								"gs_seller", "gs_departmentcode", "gs_paymentscode" }, " gsd_id=" + gsdid);
					} else
						info = baseDao.getFieldsDataByCondition("prodinout left join prodiodetail on pi_id=pd_piid", new String[] {
								"pi_sellername", "pi_departmentcode", "pi_paymentcode" }, " pd_id=" + pdid);
					currency = map.get("pi_currency");
					cardcode = map.get("pi_arcode");
					Object cop = map.get("pi_cop");
					Object sellercode = info[0];
					Object departmentcode = info[1];
					Object payment = info[2];
					if (baseDao.isDBSetting("ARBill!IRMA", "batchdealcop")) {
						if (status[4] != null && !status[4].toString().equals(String.valueOf(cop))) {
							BaseUtil.showError(BaseUtil.getLocalMessage("fa.ars.arbill.turn_onlyCopSame"));
						}
					}
					if (baseDao.isDBSetting("ARBill!IRMA", "batchdealseller")) {
						if (status[5] != null && !status[5].toString().equals(sellercode)) {
							BaseUtil.showError(BaseUtil.getLocalMessage("fa.ars.arbill.turn_onlySellerSame"));
						}
					}
					if (baseDao.isDBSetting("ARBill!IRMA", "batchdealpayment")) {
						if (status[6] != null && !status[6].toString().equals(String.valueOf(payment))) {
							BaseUtil.showError(BaseUtil.getLocalMessage("fa.ars.arbill.turn_onlyPaymentSame"));
						}
					}
					if (baseDao.isDBSetting("ARBill!IRMA", "batchdealdepartmentcode")) {
						if (status[7] != null && !String.valueOf(status[7]).equals(departmentcode)) {
							BaseUtil.showError("与指定发票部门不一致");
						}
					}
					// 当前行是否与指定的发票单的客户和币别一致
					if (!status[2].equals(String.valueOf(currency)) || !status[3].equals(String.valueOf(cardcode))) {
						BaseUtil.showError(BaseUtil.getLocalMessage("fa.ars.arbill.turn_onlyCurrencySame"));
					}
					// 转单
					turnArBill(maps, code.toString(), differ, caller);
					Integer ab_id = baseDao.getFieldValue("arbill", "ab_id", "ab_code = '" + code + "'", Integer.class);
					handlerService.handler(caller, "turn", "after", new Object[] { ab_id });
					log = "转入成功,发票号:" + "<a href=\"javascript:openUrl('jsps/fa/ars/arbill.jsp?formCondition=ab_codeIS" + code
							+ "&gridCondition=abd_codeIS" + code + "&whoami=ARBill!IRMA')\">" + code + "</a>&nbsp;";
					sb.append(index + ":" + log + "<hr/>");
					ab_codes.add(code.toString());
				}
			} else {
				Map<Object, List<Map<Object, Object>>> group = null;
				group = BaseUtil.groupsMap(maps, new Object[] { "pi_arcode", "pi_currency", "pi_shr" });
				Set<Object> set = group.keySet();
				List<Map<Object, Object>> list = null;
				for (Object s : set) {
					list = group.get(s);
					CheckARBillDifference(list, caller);
					/* 判断各个明细行开票数量是否够用 */
					checkBeforeTurnArBill(list, null, null, null);
					String pclass = list.get(0).get("pi_class").toString();
					picode = ifbypi != null && list.get(0).containsKey("pi_shr") ? list.get(0).get("pi_shr") : null;
					Map<String, Object> config = new HashMap<String, Object>();
					if (pclass.trim().equals("出货单") || pclass.trim().equals("销售退货单")) {
						SqlRowList rs = baseDao
								.queryForRowSet(
										"select em_id,pi_sellercode,pi_sellername,pi_paymentcode,pi_payment,pi_currency,pi_rate,pi_arcode,cu_id,cu_name from prodinout left join prodiodetail on pi_id=pd_piid left join employee on em_code=pi_sellercode left join customer on pi_arcode=cu_code where pd_id=? and pi_class in ('出货单', '销售退货单')",
										list.get(0).get("pd_id"));
						if (rs.next()) {
							config.put("ab_sellerid", rs.getGeneralInt("em_id"));
							config.put("ab_sellercode", rs.getGeneralString("pi_sellercode"));
							config.put("ab_seller", rs.getGeneralString("pi_sellername"));
							config.put("ab_paymentcode", rs.getGeneralString("pi_paymentcode"));
							config.put("ab_payments", rs.getGeneralString("pi_payment"));
							config.put("ab_currency", rs.getGeneralString("pi_currency"));
							config.put("ab_rate", rs.getGeneralDouble("pi_rate"));
							config.put("ab_custcode", rs.getGeneralString("pi_arcode"));
							config.put("ab_custname", rs.getGeneralString("cu_name"));
							config.put("ab_custid", rs.getGeneralInt("cu_id"));
						} else {
							rs = baseDao
									.queryForRowSet(
											"select cu_name,cu_sellerid,cu_sellercode,cu_sellername,cu_paymentscode,cu_payments,cu_currency,cu_rate,cu_id from customer where cu_code=?",
											cardcode);
							if (rs.next()) {
								config.put("ab_sellerid", rs.getGeneralInt("cu_sellerid"));
								config.put("ab_sellercode", rs.getGeneralString("cu_sellercode"));
								config.put("ab_seller", rs.getGeneralString("cu_sellername"));
								config.put("ab_paymentcode", rs.getGeneralString("cu_paymentscode"));
								config.put("ab_payments", rs.getGeneralString("cu_payments"));
								config.put("ab_currency", rs.getGeneralString("cu_currency"));
								config.put("ab_rate", rs.getGeneralDouble("cu_rate"));
								config.put("ab_custcode", cardcode);
								config.put("ab_custname", rs.getGeneralString("cu_name"));
								config.put("ab_custid", rs.getGeneralInt("cu_id"));
							}
						}
					} else {
						SqlRowList rs = baseDao
								.queryForRowSet(
										"select em_id,gs_sellercode,gs_seller,gs_paymentscode,gs_payments,gs_currency,gs_rate,gs_custcode,cu_id,cu_name from goodssend left join goodssenddetail on gs_id=gsd_gsid left join employee on em_code=gs_sellercode left join customer on gs_custcode=cu_code where gsd_id=?",
										list.get(0).get("pd_togsdid"));
						if (rs.next()) {
							config.put("ab_sellerid", rs.getGeneralInt("em_id"));
							config.put("ab_sellercode", rs.getGeneralString("gs_sellercode"));
							config.put("ab_seller", rs.getGeneralString("gs_seller"));
							config.put("ab_paymentcode", rs.getGeneralString("gs_paymentscode"));
							config.put("ab_payments", rs.getGeneralString("gs_payments"));
							config.put("ab_currency", rs.getGeneralString("gs_currency"));
							config.put("ab_rate", rs.getGeneralDouble("gs_rate"));
							config.put("ab_custcode", rs.getGeneralString("gs_custcode"));
							config.put("ab_custname", rs.getGeneralString("cu_name"));
							config.put("ab_custid", rs.getGeneralInt("cu_id"));
						} else {
							rs = baseDao
									.queryForRowSet(
											"select cu_name,cu_sellerid,cu_sellercode,cu_sellername,cu_paymentscode,cu_payments,cu_currency,cu_rate,cu_id from customer where cu_code=?",
											cardcode);
							if (rs.next()) {
								config.put("ab_sellerid", rs.getGeneralInt("cu_sellerid"));
								config.put("ab_sellercode", rs.getGeneralString("cu_sellercode"));
								config.put("ab_seller", rs.getGeneralString("cu_sellername"));
								config.put("ab_paymentcode", rs.getGeneralString("cu_paymentscode"));
								config.put("ab_payments", rs.getGeneralString("cu_payments"));
								config.put("ab_currency", rs.getGeneralString("cu_currency"));
								config.put("ab_rate", rs.getGeneralDouble("cu_rate"));
								config.put("ab_custcode", cardcode);
								config.put("ab_custname", rs.getGeneralString("cu_name"));
								config.put("ab_custid", rs.getGeneralInt("cu_id"));
							}
						}
					}
					/* 判断结束 */
					code = prodInOutDao.newARBillWithCustomer(date, sendtype.toString(), tradecode, tradename, refno, config, ifbypi,
							picode, differ, taxamount);
					if (code != null) {
						index++;
						// 转单
						turnArBill(list, code.toString(), differ, caller);
						Integer ab_id = baseDao.getFieldValue("arbill", "ab_id", "ab_code = '" + code + "'", Integer.class);
						handlerService.handler(caller, "turn", "after", new Object[] { ab_id });
						log = "转入成功,发票号:" + "<a href=\"javascript:openUrl('jsps/fa/ars/arbill.jsp?formCondition=ab_codeIS" + code
								+ "&gridCondition=abd_codeIS" + code + "&whoami=ARBill!IRMA')\">" + code + "</a>&nbsp;";
						sb.append(index + ":" + log + "<hr/>");
						ab_codes.add(code.toString());
					}
				}
			}
		}
		// 修改发票状态
		String abcodes = CollectionUtil.toSqlString(ab_codes);
		SqlRowList rs = baseDao
				.queryForRowSet("select abd_id,abd_sourcekind,abd_sourcedetailid from arbill,arbilldetail where ab_id=abd_abid and ab_code in ("
						+ abcodes + ") ");
		while (rs.next()) {
			ARBillDao.updateSourceYqty(rs.getGeneralInt("abd_id"), rs.getGeneralInt("abd_sourcedetailid"), rs.getObject("abd_sourcekind"));
		}
		return sb.toString();
	}

	/**
	 * 开票前检测
	 * 
	 * @param abCode
	 *            指定了发票
	 * @param currency
	 * @param vendcode
	 */
	private void checkBeforeTurnApBill(List<Map<Object, Object>> maps, final String abCode, final String abCurrency, final String abVendCode) {
		List<String> errors = new ArrayList<String>();
		for (Map<Object, Object> map : maps) {
			String piclass = map.get("pi_class").toString().trim();
			Object esdid = map.get("pd_toesdid").toString();
			Integer pdid = Integer.parseInt(map.get("pd_id").toString());
			double tqty = Double.parseDouble(map.get("pd_thisvoqty").toString());
			if (tqty == 0) {
				errors.add(String.format("【%s】行【%s】的开票数量为0，不能开票!", map.get("pi_inoutno"), map.get("pd_pdno")));
				continue;
			}
			boolean hasError = false;
			Object pd_msg[] = null;
			Double pd_yqty = 0.0;
			if (piclass.equals("采购验收单") || piclass.equals("委外验收单") || piclass.equals("采购验退单") || piclass.equals("委外验退单")) {
				pd_msg = baseDao.getFieldsDataByCondition("ProdIODetail", new String[] { "nvl(pd_inqty,0)", "nvl(pd_outqty,0)",
						"nvl(pd_showinvoqty,0)", "nvl(pd_turnesqty,0)" }, "pd_id='" + pdid + "'");// 出库数量
				pd_yqty = baseDao.getSummaryByField("apbilldetail", "abd_qty", "abd_sourcekind='PRODIODETAIL' and abd_sourcedetailid="
						+ pdid);
				if (Double.parseDouble((pd_msg[0].toString())) > 0 && Double.parseDouble((pd_msg[1].toString())) == 0) {
					if (Math.abs(pd_yqty) + Math.abs(tqty) + Math.abs(Double.parseDouble((pd_msg[3].toString()))) > Math.abs(Double
							.parseDouble((pd_msg[0].toString())))) {
						hasError = true;
					}
				}
				if (Double.parseDouble((pd_msg[1].toString())) > 0 && Double.parseDouble((pd_msg[0].toString())) == 0) {
					if (Math.abs(pd_yqty) + Math.abs(tqty) + Math.abs(Double.parseDouble((pd_msg[3].toString()))) > Math.abs(Double
							.parseDouble((pd_msg[1].toString())))) {
						hasError = true;
					}
				}
			} else if (piclass.equals("应付暂估")) {
				pd_msg = baseDao.getFieldsDataByCondition("EstimateDetail", new String[] { "abs(nvl(esd_qty,0))",
						"abs(nvl(esd_showinvoqty,0))" }, "esd_id='" + esdid + "'");// 出库数量
				pd_yqty = baseDao.getSummaryByField("apbilldetail", "abd_qty", "abd_sourcekind='ESTIMATE' and abd_sourcedetailid=" + esdid);
				if (Math.abs(pd_yqty) + Math.abs(tqty) > Math.abs(Double.parseDouble((pd_msg[0].toString())))) {
					hasError = true;
				}
			}
			if (hasError) {
				errors.add(String.format("【%s】行【%s】的本次开票数量超过最大可开票数量，不能开票!", map.get("pi_inoutno"), map.get("pd_pdno")));
			}
		}
		if (errors.size() > 0) {
			BaseUtil.showError(CollectionUtil.toString(errors, "/n"));
		}
	}

	private void turnApBill(List<Map<Object, Object>> maps, final String abCode, double differ, final String caller) {
		Object ab_id = baseDao.getFieldDataByCondition("APBill", "ab_id", "ab_code='" + abCode + "'");
		// transferRepository.transfer(caller, maps, new
		// Key(Integer.parseInt(ab_id.toString()), abCode));
		final Employee employee = SystemSession.getUser();
		int index = 1;
		int size = maps.size();
		for (Map<Object, Object> map : maps) {
			String piclass = map.get("pi_class").toString().trim();
			String esdid = map.get("pd_toesdid").toString();
			Integer pdid = Integer.parseInt(map.get("pd_id").toString());
			Object date = map.get("ab_date");
			double tqty = Double.parseDouble(map.get("pd_thisvoqty").toString());
			Double tprice = Double.parseDouble(map.get("pd_thisvoprice").toString());
			if (piclass.equals("采购验收单") || piclass.equals("委外验收单") || piclass.equals("采购验退单") || piclass.equals("委外验退单")
					|| piclass.equals("用品验退单") || piclass.equals("用品验收单")) {
				// 转入明细
				prodInOutDao.toAppointedAPBill(abCode, pdid, tqty, tprice, "PRODIODETAIL", String.valueOf(pdid), date);
				baseDao.updateByCondition("ProdIODetail", "pd_showinvoqty=nvl(pd_showinvoqty,0)+" + tqty, "pd_id=" + pdid);
			} else if (piclass.equals("应付暂估")) {
				prodInOutDao.toAppointedAPBill(abCode, pdid, tqty, tprice, "ESTIMATE", esdid, date);
				baseDao.updateByCondition("EstimateDetail", "esd_showinvoqty=nvl(esd_showinvoqty,0)+" + tqty, "esd_id='" + esdid + "'");
			}
			// 记录日志
			baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.turnARBill"), BaseUtil
					.getLocalMessage("msg.turnSuccess"), caller + "|pd_id=" + map.get("pd_id")));
			Emitter.progress((float) index++ / size);
		}
		baseDao.execute("UPDATE apbilldetail SET abd_apamount=round(abd_thisvoprice*abd_qty,2), "
				+ "abd_noapamount=round(abd_qty*abd_thisvoprice/(1+abd_taxrate/100),2),"
				+ "abd_taxamount=round((abd_qty*abd_thisvoprice*abd_taxrate/100)/(1+abd_taxrate/100),2) WHERE abd_abid=" + ab_id);
		baseDao.execute("update apbill set ab_apamount=round(nvl((select sum(abd_apamount) from apbilldetail where abd_abid=ab_id),0),2) where ab_id="
				+ ab_id);
		baseDao.execute("update apbill set ab_differ=" + differ
				+ ", ab_taxsum=nvl((select sum(abd_taxamount) from apbilldetail where abd_abid=" + ab_id + "),0)+" + differ
				+ " where ab_id=" + ab_id);
	}

	@Transactional
	@Override
	public String vastTurnAPBill(String caller, String data) {
		String language = SystemSession.getLang();
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Object code = null;// 指定发票单号
		Object refno = null;
		Object myrefno = null;
		Object remark = null;
		Object currency = null;
		Object cardcode = null;
		Object date = null;
		int pdid = 0;
		double differ = 0;
		StringBuffer sb = new StringBuffer();
		String log = null;
		Set<String> ab_codes = new HashSet<String>();
		if (maps.size() > 0) {
			handlerService.handler(caller, "turnAPBill", "before", new Object[] { maps });
			code = maps.get(0).get("ab_code");
			date = maps.get(0).get("ab_date");
			refno = maps.get(0).get("ab_refno");
			remark = maps.get(0).get("ab_remark");
			Object df = maps.get(0).get("differ");
			myrefno = maps.get(0).get("ab_myrefno");

			int yearmonth = Integer.parseInt(DateUtil.currentDateString("yyyyMM"));
			if (StringUtil.hasText(date)) {
				yearmonth = Integer.parseInt(date.toString().replace("T", "").replace("-", "").substring(0, 6));
			}
			String dets = null;
			String pdidstr = "";
			List<String> detailids = new ArrayList<String>();
			for (Map<Object, Object> map : maps) {
				if (!"应付暂估".equals(map.get("pi_class"))) {
					pdidstr += "," + map.get("pd_id").toString();
				}
				detailids.add(map.get("pd_id").toString());
			}
			if (detailids.size() > 0) {
				// 明细行勾选可能超过1000，需要拆分
				List<List<String>> idList = CollectionUtil.split(detailids, 500);
				for (List<String> idls : idList) {
					String ids = CollectionUtil.toString(idls);
					dets = baseDao.getJdbcTemplate().queryForObject(
							"SELECT LOB_CONCAT('单号【'||PI_INOUTNO||'】行号【'||PD_PDNO||'】') FROM FA_BATCH_APBILL_VIEW where pd_id in(" + ids
									+ ") and pi_class<>'应付暂估' and to_char(pi_date,'yyyymm')<>" + yearmonth, String.class);
					if (dets != null) {
						BaseUtil.showError("勾选的出入库明细与开票日期不在同一个月，请先转暂估再开票或修改开票日期！" + dets);
					}
					dets = baseDao.getJdbcTemplate().queryForObject(
							"SELECT LOB_CONCAT(pi_inoutno) from (Select distinct pi_inoutno FROM FA_BATCH_APBILL_VIEW where pd_id in("
									+ ids + ") and to_char(pi_date,'yyyymm')>" + yearmonth + ")", String.class);
					if (dets != null) {
						BaseUtil.showError("开票日期必须大于等于单据日期月份！单号：" + dets);
					}
				}
			}
			if (pdidstr.length() > 0) {
				dets = baseDao.getJdbcTemplate().queryForObject(
						"select LOB_CONCAT(pi_inoutno) from (Select distinct pi_inoutno from prodiodetail "
								+ "left join prodinout on pd_piid=pi_id where pd_id in(" + pdidstr.substring(1)
								+ ") and nvl(pi_statuscode,' ')<>'POSTED')", String.class);
				if (dets != null) {
					BaseUtil.showError("单据未过账不能开票!单号：" + dets);
				}
			}
			if (df != null && !StringUtils.isWhitespace(df.toString()))
				differ = Double.valueOf(df.toString());
			// 指定了发票号
			if (code != null && !code.equals("")) {
				if (!baseDao.checkIf("APBill", "ab_code='" + code + "'"))
					BaseUtil.showError("当前发票号不存在!");
				Object status[] = baseDao.getFieldsDataByCondition("APBill", new String[] { "ab_auditstatuscode", "ab_statuscode",
						"ab_currency", "ab_vendcode", "ab_cop", "ab_buyer", "ab_paymentcode", "ab_departmentcode" }, "ab_code='" + code
						+ "'");
				// 指定的发票状态是否[在录入]且[未过账]
				if (!status[0].toString().equals("ENTERING") || status[1].toString().equals("POSTED")) {
					BaseUtil.showError(BaseUtil.getLocalMessage("fa.ars.apbill.turn_onlyEnteringAndUnpost", language)
							+ "<a href=\"javascript:openUrl('jsps/fa/ars/apbill.jsp?formCondition=ab_codeIS" + code
							+ "&gridCondition=abd_codeIS" + code + "&whoami=APBill!CWIM')\">" + code + "</a>&nbsp;");
				}
				/* 判断各个明细行开票数量是否够用 */
				checkBeforeTurnApBill(maps, code.toString(), status[2].toString(), status[3].toString());
				/* 判断结束 */
				boolean err1 = false;
				boolean err2 = false;
				boolean err3 = false;
				boolean err4 = false;
				boolean err5 = false;
				for (Map<Object, Object> map : maps) {
					Object[] info = null;
					pdid = Integer.parseInt(map.get("pd_id").toString());
					currency = map.get("pi_currency");
					cardcode = map.get("pi_receivecode");
					Object cop = map.get("pi_cop");
					if ("应付暂估".equals(map.get("pi_class"))) {
						info = baseDao.getFieldsDataByCondition("Estimate left join EstimateDetail on es_id=esd_esid", new String[] {
								"es_buyer", "es_departmentcode", "es_cop", "es_paymentscode" }, " esd_id=" + map.get("pd_toesdid"));
					} else {
						info = baseDao.getFieldsDataByCondition("prodinout left join prodiodetail on pi_id=pd_piid", new String[] {
								"pi_sellername", "pi_departmentcode", "pi_cop", "pi_paymentcode" }, " pd_id=" + pdid);
					}
					if (baseDao.isDBSetting("APBill!CWIM", "batchdealcop")) {
						if (status[4] != null && cop != null && !status[4].toString().equals(String.valueOf(cop))) {
							err1 = true;
						}
					}
					if (baseDao.isDBSetting("APBill!CWIM", "batchdealseller")) {
						if (status[5] != null && info[0] != null && !status[5].toString().equals(info[0])) {
							err2 = true;
						}
					}
					if (baseDao.isDBSetting("APBill!CWIM", "batchdealpayment")) {
						if (status[6] != null && info[3] != null && !status[6].toString().equals(String.valueOf(info[3]))) {
							err3 = true;
						}
					}
					if (baseDao.isDBSetting("APBill!CWIM", "batchdealdepartmentcode")) {
						if (status[7] != null && info[1] != null && !String.valueOf(status[7]).equals(info[1])) {
							err4 = true;
						}
					}
					// 当前行是否与指定的发票单的供应商和币别一致
					if (!status[2].toString().equals(currency.toString()) || !status[3].toString().equals(cardcode.toString())) {
						err5 = true;
					}
				}
				if (err1) {
					BaseUtil.showError(BaseUtil.getLocalMessage("fa.ars.arbill.turn_onlyCopSame")
							+ "<a href=\"javascript:openUrl('jsps/fa/ars/arbill.jsp?formCondition=ab_codeIS" + code
							+ "&gridCondition=abd_codeIS" + code + "&whoami=ARBill!IRMA')\">" + code + "</a>&nbsp;<hr/>");
				}
				if (err2) {
					BaseUtil.showError(BaseUtil.getLocalMessage("fa.ars.arbill.turn_onlySellerSame")
							+ "<a href=\"javascript:openUrl('jsps/fa/ars/arbill.jsp?formCondition=ab_codeIS" + code
							+ "&gridCondition=abd_codeIS" + code + "&whoami=ARBill!IRMA')\">" + code + "</a>&nbsp;<hr/>");
				}
				if (err3) {
					BaseUtil.showError(BaseUtil.getLocalMessage("fa.ars.arbill.turn_onlyPaymentSame")
							+ "<a href=\"javascript:openUrl('jsps/fa/ars/arbill.jsp?formCondition=ab_codeIS" + code
							+ "&gridCondition=abd_codeIS" + code + "&whoami=ARBill!IRMA')\">" + code + "</a>&nbsp;<hr/>");
				}
				if (err4) {
					BaseUtil.showError("与指定发票部门不一致");
				}
				if (err5) {
					BaseUtil.showError(BaseUtil.getLocalMessage("fa.ars.apbill.turn_onlyCurrencySame", language)
							+ "<a href=\"javascript:openUrl('jsps/fa/ars/apbill.jsp?formCondition=ab_codeIS" + code
							+ "&gridCondition=abd_codeIS" + code + "&whoami=APBill!CWIM')\">" + code + "</a>&nbsp;<hr/>");
				}
				// 转单
				turnApBill(maps, code.toString(), differ, caller);
				log = "转入成功,发票号:" + "<a href=\"javascript:openUrl('jsps/fa/ars/apbill.jsp?formCondition=ab_codeIS" + code
						+ "&gridCondition=abd_codeIS" + code + "&whoami=APBill!CWIM')\">" + code + "</a>&nbsp;";
				sb.append(log + "<hr/>");
				ab_codes.add(code.toString());
			} else {
				// 按供应商和币别分组
				Map<Object, List<Map<Object, Object>>> group = BaseUtil.groupsMap(maps, new Object[] { "pi_receivecode", "pi_currency" });
				Set<Object> set = group.keySet();
				List<Map<Object, Object>> list = null;
				Object[] strs = null;
				for (Object s : set) {
					list = group.get(s);
					strs = BaseUtil.parseStr2Array(s.toString(), "#");
					currency = strs.length >= 2 ? strs[1] : "";
					cardcode = strs.length >= 1 ? strs[0] : "";
					Object[] objs = baseDao.getFieldsDataByCondition("Vendor", new String[] { "ve_id", "ve_name" }, "ve_code='" + cardcode
							+ "'");
					if (objs == null || objs.length < 2) {
						objs = new Object[2];
						objs[0] = "0";
						objs[1] = "";
					}
					/* 判断各个明细行开票数量是否够用 */
					// System.out.println("判断各个明细行开票数量是否够用 start " +
					// System.currentTimeMillis());
					checkBeforeTurnApBill(list, null, null, null);
					// System.out.println("判断各个明细行开票数量是否够用 end " +
					// System.currentTimeMillis());
					/* 判断结束 */
					code = prodInOutDao.newAPBillWithVendor(Integer.parseInt(objs[0].toString()), cardcode.toString(), objs[1].toString(),
							currency.toString(), Double.parseDouble(String.valueOf(list.get(0).get("pi_rate"))), date, refno, remark);
					if (code != null) {
						// 转单
						turnApBill(list, code.toString(), differ, caller);
						String piclass = list.get(0).get("pi_class").toString();
						if (myrefno != null) {
							baseDao.execute("update apbill set ab_myrefno='" + myrefno + "' where ab_code='" + code + "'");
						}
						if ("应付暂估".equals(piclass)) {
							baseDao.execute("update apbill set (ab_paymentcode,ab_payments,ab_buyercode,ab_buyer,ab_departmentcode,ab_departmentname,ab_cop)=(select es_paymentscode,es_payments,"
									+ "es_buyercode,es_buyer,Es_Departmentcode,Es_Departmentname,es_cop from Estimate,EstimateDetail,apbilldetail where es_id=esd_esid "
									+ "and ab_id=abd_abid and abd_sourcekind='ESTIMATE' and abd_sourcedetailid=esd_id and abd_detno=1) where ab_code='"
									+ code + "'");
						} else {
							baseDao.execute("update apbill set (ab_paymentcode,ab_payments,ab_buyercode,ab_buyer,ab_departmentcode,ab_departmentname,ab_cop)=(select pi_paymentcode,pi_payment,"
									+ "pi_sellercode,pi_sellername,pi_departmentcode,pi_departmentname,pi_cop from prodinout,ProdIODetail,apbilldetail where pi_id=pd_piid "
									+ "and ab_id=abd_abid and abd_sourcekind='PRODIODETAIL' and abd_sourcedetailid=pd_id and abd_detno=1) where ab_code='"
									+ code + "'");
						}
						sb.append("转入成功,发票号:" + "<a href=\"javascript:openUrl('jsps/fa/ars/apbill.jsp?formCondition=ab_codeIS" + code
								+ "&gridCondition=abd_codeIS" + code + "&whoami=APBill!CWIM')\">" + code + "</a>&nbsp;<hr/>");
						ab_codes.add(code.toString());
					}
				}
			}
		}
		// 修改发票状态
		String abcodes = CollectionUtil.toSqlString(ab_codes);
		SqlRowList rs = baseDao
				.queryForRowSet("select abd_id,abd_sourcekind,abd_sourcedetailid from apbill,apbilldetail where ab_id=abd_abid and ab_code in ("
						+ abcodes + ") ");
		while (rs.next()) {
			APBillDao.updateSourceYqty(rs.getGeneralInt("abd_id"), rs.getGeneralInt("abd_sourcedetailid"), rs.getObject("abd_sourcekind"));
		}
		return sb.toString();
	}

	@Override
	public String vastARBillPost(String caller, String data) {
		// List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		return null;
	}

	@Override
	public String vastTurnBillAP(String caller, String data) {
		try {
			List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
			JSONObject code = null;
			StringBuffer sb = new StringBuffer();
			for (Map<Object, Object> s : store) {
				code = payPleaseDao.turnBillAP(Integer.parseInt(s.get("ppd_id").toString()));
				baseDao.execute("update paypleasedetail set ppd_status='已转支票',ppd_statuscode='TURNBA' where ppd_id="
						+ Integer.parseInt(s.get("ppd_id").toString()));
				baseDao.execute("update PayPlease set pp_paystatuscode='PAYMENTED',pp_paystatus='" + BaseUtil.getLocalMessage("PAYMENTED")
						+ "' where pp_id=(select ppd_ppid from PayPleaseDetail where ppd_id="
						+ Integer.parseInt(s.get("ppd_id").toString()) + ")");
				if (code != null) {
					sb.append("转入成功,应付票据号:" + "<a href=\"javascript:openUrl('jsps/fa/gs/billAP.jsp?formCondition=bap_idIS"
							+ code.getInt("bap_id") + "&whoami=BillAP')\">" + code.getString("bap_code") + "</a>&nbsp;");
				}
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "转入失败";
		}
	}

	@Autowired
	private VoucherDao voucherDao;

	@Override
	public String vastTurnRecBalance(String caller, String data) {
		// 若勾选了参数，存在未过账的预收冲应收单时，限制生成并提示“未过账的预收冲应收单**张，请先过账！”
		if (baseDao.isDBSetting(caller, "hasUnPostPreRec")) {
			int count = baseDao.getCountByCondition("RecBalance", "rb_kind='预收冲应收' and nvl(rb_statuscode,'UNPOST') = 'UNPOST'");
			if (count > 0) {
				BaseUtil.showError("未过账的预收冲应收单" + count + "张，请先过账！");
			}
		}
		try {
			String language = SystemSession.getLang();
			Employee employee = SystemSession.getUser();
			Map<Object, Object> stores = BaseUtil.parseFormStoreToMap(data);
			List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
			JSONObject code = null;
			StringBuffer sb = new StringBuffer();
			int yearmonth = Integer.parseInt(stores.get("cm_yearmonth").toString());
			int nowym = voucherDao.getNowPddetno("Month-C");
			if (yearmonth < nowym) {
				BaseUtil.showError("期间" + yearmonth + "已经结转,当前账期在:" + nowym + "<br>不能生成预收冲应收单，请修改日期，或反结转应收账.");
			}
			for (Map<Object, Object> s : store) {
				String cm_custcode = s.get("cm_custcode").toString();

				Object[] status = baseDao.getFieldsDataByCondition("customer", new String[] { "cu_auditstatuscode", "cu_statuscode" },
						"cu_code='" + cm_custcode + "'");
				Double cm_prepayend = Double.parseDouble(s.get("cm_prepayend").toString());
				/*
				 * Double cm_endamount =
				 * Double.parseDouble(s.get("cm_endamount").toString()); Double
				 * cm_prepaybalance =
				 * Double.parseDouble(s.get("cm_prepaybalance").toString());
				 * 
				 * if (cm_prepaybalance <= cm_prepayend && cm_prepaybalance <=
				 * cm_endamount) {
				 * 
				 * } else { BaseUtil.showError("本次冲账金额太大"); }
				 */
				if (!StringUtil.hasText(cm_custcode)) {
					BaseUtil.showError("客户编号不存在,不能生成预收冲应收单");
				}
				if (!status[0].equals("AUDITED")) {
					BaseUtil.showError("客户状态不是已审核,不能生成预收冲应收单");
				}
				code = custMonthDao.turnRecBalance(Integer.parseInt(s.get("cm_id").toString()),
						Double.parseDouble(s.get("cm_prepaybalance").toString()), cm_prepayend, language, employee);
				if (code != null) {
					sb.append("转入成功,转预收冲应收单号:" + "<a href=\"javascript:openUrl('jsps/fa/ars/recBalancePRDetail.jsp?formCondition=rb_idIS"
							+ code.getInt("rb_id") + "&gridCondition=rbd_rbidIS" + code.getInt("rb_id") + "&whoami=RecBalance!PTAR')\">"
							+ code.getString("rb_code") + "</a>&nbsp;<br>");
				}
			}
			return sb.toString();
		} catch (RuntimeException e) {
			return e.getMessage();
		} catch (Exception e) {
			return "转入失败";
		}
	}

	@Override
	public String vastTurnPayBalance(String caller, String data) {

		// 若勾选了参数，存在未过账的预付冲应付单时，限制生成并提示“未过账的预付冲应付单**张，请先过账！”
		if (baseDao.isDBSetting(caller, "hasUnPostPrePay")) {
			int count = baseDao.getCountByCondition("PAYBALANCE", "pb_kind='预付冲应付' and nvl(pb_statuscode,'UNPOST') = 'UNPOST'");
			if (count > 0) {
				BaseUtil.showError("未过账的预付冲应付单" + count + "张，请先过账！");
			}
		}
		String language = SystemSession.getLang();
		Employee employee = SystemSession.getUser();
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		JSONObject code = null;
		StringBuffer sb = new StringBuffer();
		for (Map<Object, Object> s : store) {
			int yearmonth = Integer.parseInt(s.get("vm_yearmonth").toString());
			int nowym = voucherDao.getNowPddetno("Month-V");
			if (yearmonth < nowym) {
				BaseUtil.showError("期间" + yearmonth + "已经结转,当前账期在:" + nowym + "<br>不能生成预付冲应收单，请修改日期，或反结转应付账.");
			}
			String vendcode = s.get("vm_vendcode").toString();
			String currency = s.get("vm_currency").toString();
			/*
			 * String vm_endamount = s.get("vm_endamount").toString(); String
			 * vm_prepayend = s.get("vm_prepayend").toString(); String
			 * vm_prepaybalance = s.get("vm_prepaybalance").toString(); if
			 * (Double.parseDouble(vm_prepaybalance) <=
			 * Double.parseDouble(vm_prepayend) &&
			 * Double.parseDouble(vm_prepaybalance) <=
			 * Double.parseDouble(vm_endamount)) { } else {
			 * BaseUtil.showError("本次冲账金额太大"); }
			 */
			if (vendcode != null && !vendcode.equals("")) {
				Object status[] = baseDao.getFieldsDataByCondition("Vendor", new String[] { "ve_auditstatus", "ve_status" }, "ve_code='"
						+ vendcode + "'");
				if (status[0].equals("已审核")) {

				} else {
					BaseUtil.showError("供应商编号" + vendcode + "状态不正确");
				}
			} else {
				BaseUtil.showError("供应商编号" + vendcode + "不存在");
			}
			if (currency != null && !currency.equals("")) {
				Object status[] = baseDao.getFieldsDataByCondition("currencys", new String[] { "cr_statuscode", "cr_status" }, "cr_name='"
						+ currency + "'");
				if (status == null) {
					BaseUtil.showError("币别" + currency + "不存在，不能开票");
				} else {
					if (status[0].equals("CANUSE")) {

					} else {
						BaseUtil.showError("币别" + currency + "状态不正确，不能开票");
					}
				}
			} else {
				BaseUtil.showError("币别" + currency + "不存在，不能开票");
			}
			String vmid = String.valueOf(s.get("vm_id"));
			double prepaybalance = Double.parseDouble(s.get("vm_prepaybalance").toString());
			code = vendMonthDao.turnPayBalance(vmid, prepaybalance, language, employee);
			if (code != null) {
				sb.append("转入成功,转预付冲应付单号:" + "<a href=\"javascript:openUrl('jsps/fa/arp/payBalancePRDetail.jsp?formCondition=pb_idIS"
						+ code.getInt("pb_id") + "&gridCondition=pbd_pbidIS" + code.getInt("pb_id") + "&whoami=PayBalance!Arp!PADW')\">"
						+ code.getString("pb_code") + "</a>&nbsp;<br>");
			}
		}
		return sb.toString();
	}

	@Override
	public String vastALMonthUpdate(String caller, String data) {
		return null;
	}

	@Override
	public String vastToPBorPP(String caller, String data) {
		try {
			// List<Map<Object, Object>> store =
			// BaseUtil.parseGridStoreToMaps(data);
			// JSONObject code = null;
			StringBuffer sb = new StringBuffer();
			// for (Map<Object, Object> s : store) {

			/*
			 * code = vendMonthDao.turnPayBalance(Integer.parseInt(s.get("vm_id"
			 * ).toString()),
			 * Double.parseDouble(s.get("vm_prepaybalance").toString()),
			 * language, employee); if (code != null) {
			 * sb.append("转入成功,转预付冲应付单号:" +
			 * "<a href=\"javascript:openUrl('jsps/fa/arp/payBalancePRDetail.jsp?formCondition=pb_idIS"
			 * + code.getInt("pb_id") + "&gridCondition=pbd_pbidIS" +
			 * code.getInt("pb_id") + "&whoami=PayBalance!Arp!PADW')\">" +
			 * code.getString("pb_code") + "</a>&nbsp;<br>"); }
			 */
			// }
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "转入失败";
		}
	}

	private void turnBillOut(List<Map<Object, Object>> maps, final String biCode, Object differ, Object bi_refno, final String caller) {
		Object id = baseDao.getFieldDataByCondition("BillOut", "bi_id", "bi_code='" + biCode + "'");
		int index = 1;
		int size = maps.size();
		int detno = 1;
		Object maxdetno = baseDao.getFieldDataByCondition("billoutdetail", "max(ard_detno)", "ard_biid=" + id);
		maxdetno = maxdetno == null ? 1 : Integer.parseInt(maxdetno.toString()) + 1;
		detno = Integer.parseInt(maxdetno.toString());
		for (Map<Object, Object> map : maps) {
			Integer abd_id = Integer.parseInt(map.get("abd_id").toString());
			double tqty = Double.parseDouble(map.get("abd_thisvoqty").toString());
			Double tprice = Double.parseDouble(map.get("abd_thisvoprice").toString());
			ARBillDao.turnBillOutDetail(biCode, abd_id, Integer.parseInt(id.toString()), detno++, tqty, tprice);
			Emitter.progress((float) index++ / size);
		}
		if (bi_refno != null) {
			baseDao.execute("update BillOut SET bi_refno='" + bi_refno + "' where bi_id=" + id);
		}
		baseDao.execute("update BillOut set bi_amount=round(nvl((select sum(round(ard_nowbalance,2)) from BillOutDetail where ard_biid=bi_id),0),2) where bi_id="
				+ id);
		baseDao.execute("update BillOut SET bi_taxdiffer=" + differ + " where bi_id=" + id);
		baseDao.execute("update BillOut set bi_taxamount=round(nvl((select sum(round(ard_taxamount,2)) from BillOutDetail where ard_biid=bi_id),2),2) + nvl(bi_taxdiffer,0) where bi_id="
				+ id);
	}

	@Override
	@Transactional
	public String vastTurnBillOut(String caller, String data) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		String language = SystemSession.getLang();
		Employee employee = SystemSession.getUser();
		// 判断本次数量
		ARBillDao.checkyqty(store);
		StringBuffer sb = new StringBuffer();
		String code = null;
		Object sendtype = null;
		Object bidate = null;
		String abid = "";
		Object differ = null; // 税金差异
		Object bi_refno = null; // 税票编号
		Object abcode = null; // 指定发票号
		int id = 0;
		if (store.size() > 0) {
			sendtype = store.get(0).get("ab_sendtype");
			bidate = store.get(0).get("bi_date");
			abcode = store.get(0).get("ab_code");
			bi_refno = store.get(0).containsKey("bi_refno") ? store.get(0).get("bi_refno") : null; // 税票编号
			differ = store.get(0).containsKey("differ") ? store.get(0).get("differ") : 0; // 税金差异
		}
		if (bidate != null) {
			Object yearmonth = baseDao.getFieldDataByCondition("PeriodsDetail", "min(pd_detno)", "pd_code='MONTH-C' and pd_status=0");
			if (DateUtil.getYearmonth(bidate.toString()) < Integer.parseInt(yearmonth.toString())) {
				BaseUtil.showError("开票日期必须大于等于当前应收账期!");
			}
		}
		String ids = BaseUtil.parseArray2Str(CollectionUtil.pluck(store, "abd_abid"), ",");
		if (baseDao.isDBSetting("BillOut", "batchdealcop")) {
			SqlRowList rs = baseDao
					.queryForRowSet("select count(1) from (select distinct ab_cop from arbill where ab_id in (" + ids + "))");
			if (rs.next() && rs.getInt(1) > 1) {
				BaseUtil.showError("选定发票的公司不同，不能合并生成！");
			}
		}
		if (baseDao.isDBSetting("BillOut", "batchdealseller")) {
			SqlRowList rs = baseDao.queryForRowSet("select count(1) from (select distinct ab_sellercode from arbill where ab_id in (" + ids
					+ "))");
			if (rs.next() && rs.getInt(1) > 1) {
				BaseUtil.showError("选定发票的业务员不同，不能合并生成！");
			}
		}
		if (baseDao.isDBSetting("BillOut", "batchdealpayment")) {
			SqlRowList rs = baseDao.queryForRowSet("select count(1) from (select distinct ab_paymentcode from arbill where ab_id in ("
					+ ids + "))");
			if (rs.next() && rs.getInt(1) > 1) {
				BaseUtil.showError("选定发票的收款方式不同，不能合并生成！");
			}
		}
		if (baseDao.isDBSetting("BillOut", "batchdealdepartmentcode")) {
			SqlRowList rs = baseDao.queryForRowSet("select count(1) from (select distinct ab_departmentcode from arbill where ab_id in ("
					+ ids + "))");
			if (rs.next() && rs.getInt(1) > 1) {
				BaseUtil.showError("选定发票的部门不同，不能合并生成！");
			}
		}
		if (abcode != null && !"".equals(abcode)) {
			Object[] billout = baseDao.getFieldsDataByCondition("BillOut", new String[] { "bi_statuscode", "bi_custcode", "bi_id",
					"bi_currency", "bi_cop", "bi_departmentcode", "bi_sellercode", "BI_PAYMENTSCODE" }, "bi_code='" + abcode + "'");
			if (billout == null) {
				BaseUtil.showError("指定的开票记录不存在或已删除!");
			} else if (!"ENTERING".equals(String.valueOf(billout[0]))) {
				BaseUtil.showError("指定的开票记录状态不等于[在录入]!");
			} else {
				String log1 = null;
				StringBuffer sb1 = new StringBuffer();
				for (Map<Object, Object> map : store) {
					if (map.get("ab_custcode") != null && billout[1] != null
							&& !billout[1].toString().equals(String.valueOf(map.get("ab_custcode")))) {
						log1 = "所选请开票记录客户：" + map.get("ab_custcode") + "，与指定开票记录的客户：" + billout[1] + "不一致!";
						sb1.append(log1).append("<hr>");
					}
					if (map.get("ab_currency") != null && billout[3] != null
							&& !billout[3].toString().equals(String.valueOf(map.get("ab_currency")))) {
						log1 = "所选开票记录币别：" + map.get("ab_currency") + "，与指定开票记录的币别:" + billout[3] + "不一致!";
						sb1.append(log1).append("<hr>");
					}
					if (baseDao.isDBSetting("BillOut", "batchdealcop")) {
						if (map.get("ab_cop") != null && billout[4] != null
								&& !billout[4].toString().equals(String.valueOf(map.get("ab_cop")))) {
							log1 = "所选开票记录公司：" + map.get("ab_cop") + "，与指定开票记录的公司:" + billout[4] + "不一致!";
							sb1.append(log1).append("<hr>");
						}
					}
					if (baseDao.isDBSetting("BillOut", "batchdealdepartmentcode")) {
						if (map.get("ab_departmentcode") != null && billout[5] != null
								&& !billout[5].toString().equals(String.valueOf(map.get("ab_departmentcode")))) {
							log1 = "所选开票记录部门：" + map.get("ab_departmentcode") + "，与指定开票记录的部门:" + billout[5] + "不一致!";
							sb1.append(log1).append("<hr>");
						}
					}
				}
				if (sb1.length() > 0) {
					BaseUtil.showError(sb1.toString());
				}
			}
			code = abcode.toString();
			id = Integer.parseInt(billout[2].toString());
			turnBillOut(store, code, differ, bi_refno, caller);
			sb.append("转入成功,应收发票记录号:" + "<a href=\"javascript:openUrl('jsps/fa/ars/billOut.jsp?formCondition=bi_idIS" + id
					+ "&gridCondition=ard_biidIS" + id + "')\">" + code + "</a>&nbsp;<hr>");
		} else {
			// 按客户分组
			Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupsMap(store, new Object[] { "ab_custcode", "ab_currency" });
			List<Map<Object, Object>> s = null;
			String vCode = "";
			String curr = "";
			Object[] bi = null;
			for (Object m : map.keySet()) {
				if (m != null) {
					vCode = m.toString().split("#")[0].toString();
					curr = m.toString().split("#")[1].toString();
					s = map.get(m);
					if (s.size() > 0) {
						abid = s.get(0).get("abd_abid").toString();
						differ = store.get(0).containsKey("differ") ? store.get(0).get("differ") : 0; // 税金差异
					}
					bi = ARBillDao.turnBillOut(language, vCode, curr, employee, bidate, abid, sendtype);
					if (bi != null) {
						code = bi[0].toString();
						baseDao.execute("update BillOut set bi_salekind='" + sendtype + "' where bi_id=" + id);
						turnBillOut(s, code, differ, bi_refno, caller);
						id = Integer.parseInt(bi[1].toString());
						sb.append("转入成功,应收发票记录号:" + "<a href=\"javascript:openUrl('jsps/fa/ars/billOut.jsp?formCondition=bi_idIS" + id
								+ "&gridCondition=ard_biidIS" + id + "')\">" + code + "</a>&nbsp;<hr>");
					}
				}
			}
		}
		return sb.toString();
	}

	private void turnBillOutAP(List<Map<Object, Object>> maps, final String biCode, Object differ, Object bi_refno, final String caller) {
		Object id = baseDao.getFieldDataByCondition("BillOutAP", "bi_id", "bi_code='" + biCode + "'");
		int index = 1;
		int size = maps.size();
		int detno = 1;
		Object maxdetno = baseDao.getFieldDataByCondition("billoutapdetail", "max(ard_detno)", "ard_biid=" + id);
		maxdetno = maxdetno == null ? 1 : Integer.parseInt(maxdetno.toString()) + 1;
		detno = Integer.parseInt(maxdetno.toString());
		for (Map<Object, Object> map : maps) {
			Integer abd_id = Integer.parseInt(map.get("abd_id").toString());
			double tqty = Double.parseDouble(map.get("abd_thisvoqty").toString());
			double tprice = Double.parseDouble(map.get("abd_thisvoprice").toString());
			APBillDao.turnBillOutAPDetail(biCode, abd_id, Integer.parseInt(id.toString()), detno++, tqty, tprice);
			Emitter.progress((float) index++ / size);
		}
		baseDao.execute("update BillOutAP set bi_amount=round(nvl((select sum(round(ard_nowbalance,2)) from BillOutAPDetail where ard_biid=bi_id),0),2) where bi_id="
				+ id);
		baseDao.execute("update BillOutAP SET bi_taxdiffer=" + differ + " where bi_id=" + id);
		baseDao.execute("update BillOutAP set bi_taxamount=round(nvl((select sum(round(ard_taxamount,2)) from BillOutAPDetail where ard_biid=bi_id),0),2) + nvl(bi_taxdiffer,0) where bi_id="
				+ id);
		if (bi_refno != null) {
			baseDao.execute("update BillOutAP SET bi_refno='" + bi_refno + "' where bi_id=" + id);
		}
		/**
		 * 更新部门公司
		 */
		baseDao.execute("update BillOutAP set (bi_departmentcode,bi_department,bi_cop)=(select ab_departmentcode,ab_departmentname,ab_cop  from  BILLOUTAPDETAIL left join APBill on ard_ordercode=ab_code where ard_biid=bi_id and ard_detno=1) where bi_id="
				+ id);
		handlerService.handler(caller, "turn", "after", new Object[] { id });
	}

	@Override
	@Transactional
	public String vastTurnBillOutAP(String caller, String data) {
		String language = SystemSession.getLang();
		Employee employee = SystemSession.getUser();
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		// 判断本次数量
		APBillDao.checkyqty(store);
		StringBuffer sb = new StringBuffer();
		String code = null;
		Object bidate = null;
		Object differ = null; // 税金差异
		Object abcode = null; // 指定发票号
		Object refno = null;
		int id = 0;
		if (store.size() > 0) {
			bidate = store.get(0).get("bi_date");
			abcode = store.get(0).get("ab_code");
			refno = store.get(0).get("ab_refno");
			differ = store.get(0).containsKey("differ") ? store.get(0).get("differ") : 0; // 税金差异
		}
		if (bidate != null) {
			Object yearmonth = baseDao.getFieldDataByCondition("PeriodsDetail", "min(pd_detno)", "pd_code='MONTH-V' and pd_status=0");
			if (DateUtil.getYearmonth(bidate.toString()) < Integer.parseInt(yearmonth.toString())) {
				BaseUtil.showError("开票日期必须大于等于当前应付账期!");
			}
		}
		String ids = BaseUtil.parseArray2Str(CollectionUtil.pluck(store, "abd_abid"), ",");
		if (baseDao.isDBSetting("BillOutAP", "batchdealcop")) {
			SqlRowList rs = baseDao
					.queryForRowSet("select count(1) from (select distinct ab_cop from apbill where ab_id in (" + ids + "))");
			if (rs.next() && rs.getInt(1) > 1) {
				BaseUtil.showError("选定发票的公司不同，不能合并生成！");
			}
		}
		if (baseDao.isDBSetting("BillOutAP", "batchdealseller")) {
			SqlRowList rs = baseDao.queryForRowSet("select count(1) from (select distinct ab_buyer from apbill where ab_id in (" + ids
					+ "))");
			if (rs.next() && rs.getInt(1) > 1) {
				BaseUtil.showError("选定发票的采购员不同，不能合并生成！");
			}
		}
		if (baseDao.isDBSetting("BillOutAP", "batchdealpayment")) {
			SqlRowList rs = baseDao.queryForRowSet("select count(1) from (select distinct ab_paymentcode from apbill where ab_id in ("
					+ ids + "))");
			if (rs.next() && rs.getInt(1) > 1) {
				BaseUtil.showError("选定发票的付款方式不同，不能合并生成！");
			}
		}
		if (baseDao.isDBSetting("BillOutAP", "batchdealdepartmentcode")) {
			SqlRowList rs = baseDao.queryForRowSet("select count(1) from (select distinct ab_departmentcode from apbill where ab_id in ("
					+ ids + "))");
			if (rs.next() && rs.getInt(1) > 1) {
				BaseUtil.showError("选定发票的部门不同，不能合并生成！");
			}
		}
		if (abcode != null && !"".equals(abcode)) {
			Object[] billout = baseDao.getFieldsDataByCondition("BillOutAP", new String[] { "bi_statuscode", "bi_vendcode", "bi_id",
					"bi_currency", "bi_cop", "bi_departmentcode" }, "bi_code='" + abcode + "'");
			if (billout == null) {
				BaseUtil.showError("指定的开票记录不存在或已删除!");
			} else if (!"ENTERING".equals(String.valueOf(billout[0]))) {
				BaseUtil.showError("指定的开票记录状态不等于[在录入]!");
			} else {
				String log1 = null;
				StringBuffer sb1 = new StringBuffer();
				for (Map<Object, Object> map : store) {
					if (map.get("ab_custcode") != null && billout[1] != null
							&& !billout[1].toString().equals(String.valueOf(map.get("ab_vendcode")))) {
						log1 = "所选请开票记录供应商：" + map.get("ab_custcode") + "，与指定开票记录的供应商：" + billout[1] + "不一致!";
						sb1.append(log1).append("<hr>");
					}
					if (map.get("ab_currency") != null && billout[3] != null
							&& !billout[3].toString().equals(String.valueOf(map.get("ab_currency")))) {
						log1 = "所选开票记录币别：" + map.get("ab_currency") + "，与指定开票记录的币别:" + billout[3] + "不一致!";
						sb1.append(log1).append("<hr>");
					}
					if (baseDao.isDBSetting("BillOutAP", "batchdealcop")) {
						if (map.get("ab_cop") != null && billout[4] != null
								&& !billout[4].toString().equals(String.valueOf(map.get("ab_cop")))) {
							log1 = "所选开票记录公司：" + map.get("ab_cop") + "，与指定开票记录的公司:" + billout[4] + "不一致!";
							sb1.append(log1).append("<hr>");
						}
					}
					if (baseDao.isDBSetting("BillOutAP", "batchdealdepartmentcode")) {
						if (map.get("ab_departmentcode") != null && billout[5] != null
								&& !billout[5].toString().equals(String.valueOf(map.get("ab_departmentcode")))) {
							log1 = "所选开票记录部门：" + map.get("ab_departmentcode") + "，与指定开票记录的部门:" + billout[5] + "不一致!";
							sb1.append(log1).append("<hr>");
						}
					}
				}
				if (sb1.length() > 0) {
					BaseUtil.showError(sb1.toString());
				}
			}
			code = abcode.toString();
			id = Integer.parseInt(billout[2].toString());
			turnBillOutAP(store, code, differ, refno, caller);
			sb.append("转入成功,应付发票记录号:" + "<a href=\"javascript:openUrl('jsps/fa/arp/billOutAP.jsp?formCondition=bi_idIS" + id
					+ "&gridCondition=ard_biidIS" + id + "')\">" + code + "</a>&nbsp;<hr>");
		} else {
			// 按供应商分组
			Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupsMap(store, new Object[] { "ab_vendcode", "ab_currency" });
			List<Map<Object, Object>> s = null;
			String vCode = "";
			String curr = "";
			Object[] bi = null;
			for (Object m : map.keySet()) {
				if (m != null) {
					vCode = m.toString().split("#")[0].toString();
					curr = m.toString().split("#")[1].toString();
					s = map.get(m);
					bi = APBillDao.turnBillOutAP(language, vCode, curr, employee, bidate, refno);
					if (bi != null) {
						code = bi[0].toString();
						id = Integer.parseInt(bi[1].toString());
						turnBillOutAP(s, code, differ, refno, caller);
						sb.append("转入成功,应付发票记录号:" + "<a href=\"javascript:openUrl('jsps/fa/arp/billOutAP.jsp?formCondition=bi_idIS" + id
								+ "&gridCondition=ard_biidIS" + id + "')\">" + code + "</a>&nbsp;<hr>");
					}
				}
			}
		}
		return sb.toString();
	}

	private void CheckARBillDifference(List<Map<Object, Object>> list, String caller) {
		String ids = BaseUtil.parseArray2Str(CollectionUtil.pluck(list, "pd_id"), ",");
		if (baseDao.isDBSetting("ARBill!IRMA", "batchdealcop")) {
			SqlRowList rs = baseDao
					.queryForRowSet("select count(1) from (select distinct pi_cop from prodinout left join prodiodetail on pi_id=pd_piid where pd_id in ("
							+ ids + "))");
			if (rs.next() && rs.getInt(1) > 1) {
				BaseUtil.showError("选定出入库单的公司不同,不能合并生成!");
			}
		}
		if (baseDao.isDBSetting("ARBill!IRMA", "batchdealseller")) {
			SqlRowList rs = baseDao
					.queryForRowSet("select count(1) from (select distinct pi_sellercode from prodinout left join prodiodetail on pi_id=pd_piid where pd_id in ("
							+ ids + "))");
			if (rs.next() && rs.getInt(1) > 1) {
				BaseUtil.showError("选定出入库单的业务员不同,不能合并生成!");
			}
		}
		if (baseDao.isDBSetting("ARBill!IRMA", "batchdealpayment")) {
			SqlRowList rs = baseDao
					.queryForRowSet("select count(1) from (select distinct pi_paymentcode from prodinout left join prodiodetail on pi_id=pd_piid where pd_id in ("
							+ ids + "))");
			if (rs.next() && rs.getInt(1) > 1) {
				BaseUtil.showError("选定出入库单的收款方式不同,不能合并生成!");
			}
		}
		if (baseDao.isDBSetting("ARBill!IRMA", "batchdealdepartmentcode")) {
			SqlRowList rs = baseDao
					.queryForRowSet("select count(1) from (select distinct pi_departmentcode from prodinout left join prodiodetail on pi_id=pd_piid where pd_id in ("
							+ ids + "))");
			if (rs.next() && rs.getInt(1) > 1) {
				BaseUtil.showError("选定出入库单的部门不同,不能合并生成!");
			}
		}
	}

	@Override
	public String vastSubmitAccountRegister(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		// 提交之前的判断
		String ids = CollectionUtil.pluckSqlString(maps, "ar_id");
		StringBuffer errBuffer = new StringBuffer();
		String errSn = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(ar_code) from AccountRegister where ar_id in (" + ids
						+ ") and nvl(ar_deposit,0)=0 and ar_type in ('应收款','预收款','应付退款','其它收款','应收票据收款')", String.class);
		if (errSn != null)
			errBuffer.append("收入金额不能为空！单号：<br>" + errSn.replace(",", "<br>")).append("<hr>");
		errSn = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(ar_code) from AccountRegister where ar_id in (" + ids
						+ ") and nvl(ar_payment,0)=0 and ar_type in ('预付款','应付款','应收退款','其它付款','应付票据付款','费用','转存')", String.class);
		if (errSn != null)
			errBuffer.append("支出金额不能为空！单号：<br>" + errSn.replace(",", "<br>")).append("<hr>");
		errSn = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(ar_code) from AccountRegister where ar_id in ("
								+ ids
								+ ") and nvl(ar_deposit,0)<>0 and ar_type in ('应收款','预收款','应付退款') and round(ar_araprate,8)<>round(ar_aramount/ar_deposit,8)",
						String.class);
		if (errSn != null)
			errBuffer.append("冲账汇率不正确！单号：<br>" + errSn.replace(",", "<br>")).append("<hr>");
		errSn = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(ar_code) from AccountRegister where ar_id in ("
								+ ids
								+ ") and nvl(ar_payment,0)<>0 and ar_type in ('预付款','应付款','应收退款') and round(ar_araprate,8)<>round(ar_aramount/ar_payment,8)",
						String.class);
		if (errSn != null)
			errBuffer.append("冲账汇率不正确！单号：<br>" + errSn.replace(",", "<br>")).append("<hr>");
		errSn = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(ar_code) from AccountRegister where ar_id in (" + ids
						+ ") and ar_type='转存' and (nvl(ar_category,' ')=' ' or nvl(ar_precurrency,' ')=' ' or nvl(ar_preamount,0)=0)",
				String.class);
		if (errSn != null)
			errBuffer.append("转存科目/转存币别/转存金额为空！单号：<br>" + errSn.replace(",", "<br>")).append("<hr>");
		errSn = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(ar_code) from AccountRegister where ar_id in (" + ids
						+ ") and ar_type='转存' and (nvl(ar_category,' ')=' ' or nvl(ar_precurrency,' ')=' ' or nvl(ar_preamount,0)=0)",
				String.class);
		if (errSn != null)
			errBuffer.append("转存科目/转存币别/转存金额为空！单号：<br>" + errSn.replace(",", "<br>")).append("<hr>");
		errSn = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(ar_code) from AccountRegister where ar_id in (" + ids
						+ ") and ar_type='转存' and nvl(ar_accountcurrency,' ') <> nvl(ar_precurrency,' ') and nvl(ar_prerate,0)=1",
				String.class);
		if (errSn != null)
			errBuffer.append("币别不一致，转存汇率为1！单号：<br>" + errSn.replace(",", "<br>")).append("<hr>");
		errSn = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(ar_code) from AccountRegister where ar_id in (" + ids
						+ ") and ar_type='转存' and nvl(ar_accountcurrency,' ') = nvl(ar_precurrency,' ') and nvl(ar_prerate,0)<>1",
				String.class);
		if (errSn != null)
			errBuffer.append("币别一致，转存汇率不为1！单号：<br>" + errSn.replace(",", "<br>")).append("<hr>");
		errSn = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(ar_code) from AccountRegister where ar_id in ("
								+ ids
								+ ") and ar_type not in ('应收票据收款','应付票据付款','费用','转存','其它收款','其它付款') and nvl(ar_accountcurrency,' ') <> nvl(ar_arapcurrency,' ') and nvl(ar_araprate,0)=1",
						String.class);
		if (errSn != null)
			errBuffer.append("币别不一致，转存汇率为1！单号：<br>" + errSn.replace(",", "<br>")).append("<hr>");
		errSn = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(ar_code) from AccountRegister where ar_id in ("
								+ ids
								+ ") and ar_type not in ('应收票据收款','应付票据付款','费用','转存','其它收款','其它付款') and nvl(ar_accountcurrency,' ') = nvl(ar_arapcurrency,' ') and nvl(ar_araprate,0)<>1",
						String.class);
		if (errSn != null)
			errBuffer.append("币别一致，冲账汇率不为1！单号：<br>" + errSn.replace(",", "<br>")).append("<hr>");
		Integer arid = 0;
		// 提交
		for (Map<Object, Object> m : maps) {
			arid = Integer.parseInt(m.get("ar_id").toString());
			accountRegisterBankService.submitAccountRegister(arid, "AccountRegister!Bank");
		}
		return errBuffer.toString();
	}

	@Override
	public String vastConfirmCheckRegister(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Integer arid = 0;
		for (Map<Object, Object> m : maps) {
			arid = Integer.parseInt(m.get("ar_id").toString());
			baseDao.execute("update accountregister set ar_checkstatus='已对账' where ar_id=" + arid);
			baseDao.logger.others("确认对账", "对账成功", "AccountRegister!Bank", "ar_id", arid);
		}
		return "对账成功";
	}

	@Override
	public String vastCancelCheckRegister(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Integer arid = 0;
		for (Map<Object, Object> m : maps) {
			arid = Integer.parseInt(m.get("ar_id").toString());
			baseDao.execute("update accountregister set ar_checkstatus=null where ar_id=" + arid + " and ar_checkstatus='已对账'");
			baseDao.logger.others("取消对账", "取消对账成功", "AccountRegister!Bank", "ar_id", arid);
		}
		return "取消对账成功";
	}

	@Override
	public void faPost(String caller, String from, String to, String pclass) {
		if (from == null) {
			BaseUtil.showError("请选定起始日期！");
		}
		if (to == null) {
			BaseUtil.showError("请选定截止日期！");
		}
		if (pclass == null || "".equals(pclass)) {
			BaseUtil.showError("请选定单据类型！");
		}
		String res = baseDao.callProcedure("FA_VASTPOST", new Object[] { pclass, from, to, SystemSession.getUser().getEm_id() });
		if (res == null || res.trim().equals("ok")) {
			baseDao.logger.others(pclass + "批量过账", "批量过账成功", "FaPost", "id", 1);
		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public String vastTurnARCheck(String caller, String data, String fromDate, String toDate) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		// 判断本次数量
		ARBillDao.checkqty(store);
		StringBuffer sb = new StringBuffer();
		String code = null;
		Object sourceid = 0;
		Object accode = StringUtil.hasText(store.get(0).get("ac_code")) ? store.get(0).get("ac_code") : null;
		String mm = null;
		Object[] ac = null;
		Object sourcetype = store.get(0).get("pi_type");
		boolean type = baseDao.isDBSetting("autoCreateArBill");
		if (type) {
			if ("PRODINOUT".equals(sourcetype)) {
				BaseUtil.showError("当前模式只能对应收发票进行对账！");
			}
		} else {
			if ("ARBILL".equals(sourcetype)) {
				BaseUtil.showError("当前模式只能对出入库单进行对账！");
			}
		}
		Object pi_fztype = StringUtil.hasText(store.get(0).get("pi_fztype")) ? store.get(0).get("pi_fztype") : "CC";
		if (StringUtil.hasText(accode)) {
			// 判断指定的采购单状态是否[在录入],指定采购单的请购类型是否与当前的一致
			ac = baseDao.getFieldsDataByCondition("archeck", new String[] { "ac_statuscode", "ac_custcode", "ac_id", "ac_currency",
					"ac_sellercode" }, "ac_code='" + accode + "'");
			if (ac == null) {
				BaseUtil.showError("指定的对账单不存在或已删除!");
			} else if (!"ENTERING".equals(String.valueOf(ac[0]))) {
				BaseUtil.showError("指定的对账单状态不等于[在录入]!");
			} else {
				String log1 = null;
				StringBuffer sb1 = new StringBuffer();
				for (Map<Object, Object> map : store) {
					if (StringUtil.hasText(map.get("pi_arcode"))) {
						if (!ac[1].toString().equals(String.valueOf(map.get("pi_arcode")))) {
							log1 = "所选单据客户[" + map.get("pi_arcode") + "]与指定对账单的客户[" + ac[1] + "]不一致!";
							sb1.append(log1).append("<hr>");
						}
					}
					if (StringUtil.hasText(map.get("pi_currency"))) {
						if (StringUtil.hasText(ac[3]) && !ac[3].toString().equals(String.valueOf(map.get("pi_currency")))) {
							log1 = "所选单据币别[" + map.get("pi_currency") + "]与指对账单的币别[" + ac[3] + "]不一致!";
							sb1.append(log1).append("<hr>");
						}
					}
					if ("CCS".equals(pi_fztype)) {
						if (StringUtil.hasText(map.get("pi_sellercode"))) {
							if (StringUtil.hasText(ac[4]) && !ac[4].toString().equals(String.valueOf(map.get("pi_sellercode")))) {
								log1 = "所选单据业务员[" + map.get("pi_sellercode") + "]与指对账单的业务员[" + ac[3] + "]不一致!";
								sb1.append(log1).append("<hr>");
							}
						}
					}
				}
				if (sb1.length() > 0) {
					BaseUtil.showError(sb1.toString());
				}
			}
		}
		Map<Object, List<Map<Object, Object>>> map = null;
		if ("CC".equals(pi_fztype)) {
			// 按客户+币别分组
			if (StringUtil.hasText(accode)) {
				ac = baseDao.getFieldsDataByCondition("archeck", new String[] { "ac_custcode", "ac_currency" }, "ac_code='" + accode + "'");
				mm = ac[0].toString() + "#" + ac[1].toString();
			}
			map = BaseUtil.groupsMap(store, new Object[] { "pi_arcode", "pi_currency" });
		} else if ("CCS".equals(pi_fztype)) {
			// 按客户+币别+业务员分组
			if (StringUtil.hasText(accode)) {
				ac = baseDao.getFieldsDataByCondition("archeck", new String[] { "ac_custcode", "ac_currency", "ac_sellercode" },
						"ac_code='" + accode + "'");
				mm = ac[0].toString() + "#" + ac[1].toString() + "#" + ac[2].toString();
			}
			map = BaseUtil.groupsMap(store, new Object[] { "pi_arcode", "pi_currency", "pi_sellercode" });
		}
		List<Map<Object, Object>> s = null;
		String vCode = "";
		String curr = "";
		int detno = 1;
		Object[] bi = null;
		int acid = 0;
		for (Object m : map.keySet()) {
			if (m != null) {
				vCode = m.toString().split("#")[0].toString();
				curr = m.toString().split("#")[1].toString();
				s = map.get(m);
				if (s.size() > 0) {
					sourceid = s.get(0).get("pd_piid");
					sourcetype = s.get(0).get("pi_type");
				}
				if (mm != null && mm.equals(m)) {
					bi = baseDao.getFieldsDataByCondition("archeck", new String[] { "ac_code", "ac_id" }, "ac_code='" + accode + "'");
				} else {
					bi = ARBillDao.turnARCheck(vCode, curr, sourceid, sourcetype);
				}
				if (bi != null) {
					code = bi[0].toString();
					acid = Integer.parseInt(bi[1].toString());
					Object maxdetno = baseDao.getFieldDataByCondition("archeckdetail", "max(ad_detno)", "ad_acid=" + acid);
					maxdetno = maxdetno == null ? 1 : Integer.parseInt(maxdetno.toString()) + 1;
					detno = Integer.parseInt(maxdetno.toString());
					for (Map<Object, Object> p : s) {
						ARBillDao.turnARCheckDetail(code, p.get("pd_id").toString(), acid, detno++,
								Double.parseDouble(p.get("pd_thisvoqty").toString()),
								Double.parseDouble(p.get("pd_thisvoprice").toString()), sourcetype);

					}
					baseDao.execute("update ARCHECK set ac_checkamount=round(nvl((select sum(ad_amount) from ARCHECKDETAIL where ad_acid=ac_id),0),2) where ac_id="
							+ acid);
					baseDao.execute("update ARCHECK set ac_fztype='" + pi_fztype + "' where ac_id=" + acid);
					if (fromDate != null) {
						baseDao.execute("update ARCHECK set ac_fromdate=to_date('" + fromDate + "','yyyy-mm-dd') where ac_id=" + acid);
					}
					if (toDate != null) {
						baseDao.execute("update ARCHECK set ac_todate=to_date('" + toDate + "','yyyy-mm-dd') where ac_id=" + acid);
					}
					sb.append("转入成功,应收对账单号:" + "<a href=\"javascript:openUrl('jsps/fa/ars/arCheck.jsp?formCondition=ac_idIS" + acid
							+ "&gridCondition=ad_acidIS" + acid + "')\">" + code + "</a>&nbsp;<hr>");
				}
			}
		}
		return sb.toString();
	}

	@Override
	public String vastTurnAPCheck(String caller, String data, String fromDate, String toDate) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		// 判断本次数量
		APBillDao.checkqty(store);
		StringBuffer sb = new StringBuffer();
		String code = null;
		Object sourceid = 0;
		Object sourcetype = store.get(0).get("pi_type");
		Object accode = StringUtil.hasText(store.get(0).get("ac_code")) ? store.get(0).get("ac_code") : null;
		Object ac[];
		String mm = null;
		Object[] bi = null;
		boolean type = baseDao.isDBSetting("autoCreateApBill");
		boolean groupTaxRate = baseDao.isDBSetting("APCheck", "groupTaxRate");
		if (type) {
			// if ("PRODINOUT".equals(sourcetype)) {
			// BaseUtil.showError("当前模式只能对应付发票进行对账！");
			// }
		} else {
			if ("APBILL".equals(sourcetype)) {
				BaseUtil.showError("当前模式只能对出入库单进行对账！");
			}
		}
		if (StringUtil.hasText(accode)) {
			ac = baseDao.getFieldsDataByCondition("apcheck", new String[] { "ac_statuscode", "ac_vendcode", "ac_currency" }, "ac_code='"
					+ accode + "'");
			if (ac == null) {
				BaseUtil.showError("指定的对账单不存在或已删除!");
			} else if (!"ENTERING".equals(String.valueOf(ac[0]))) {
				BaseUtil.showError("指定的对账单状态不等于[在录入]!");
			} else {
				String log1 = null;
				StringBuffer sb1 = new StringBuffer();
				for (Map<Object, Object> map : store) {
					if (StringUtil.hasText(map.get("pi_receivecode"))) {
						if (!ac[1].toString().equals(String.valueOf(map.get("pi_receivecode")))) {
							log1 = "所选单据供应商[" + map.get("pi_receivecode") + "]与指定对账单的供应商[" + ac[1] + "]不一致!";
							sb1.append(log1).append("<hr>");
						}
					}
					if (StringUtil.hasText(map.get("pi_currency"))) {
						if (StringUtil.hasText(ac[2]) && !ac[2].toString().equals(String.valueOf(map.get("pi_currency")))) {
							log1 = "所选单据币别[" + map.get("pi_currency") + "]与指对账单的币别[" + ac[2] + "]不一致!";
							sb1.append(log1).append("<hr>");
						}
					}
				}
				if (sb1.length() > 0) {
					BaseUtil.showError(sb1.toString());
				}
			}
		}
		Map<Object, List<Map<Object, Object>>> map = null;
		if (StringUtil.hasText(accode)) {
			ac = baseDao.getFieldsDataByCondition("apcheck", new String[] { "ac_vendcode", "ac_currency" }, "ac_code='" + accode + "'");
			mm = ac[0].toString() + "#" + ac[1].toString();
		}
		if (groupTaxRate) {
			// 按供应商+币别+税率分组
			map = BaseUtil.groupsMap(store, new Object[] { "pi_receivecode", "pi_currency", "pd_taxrate" });
		} else {
			// 按供应商+币别分组
			map = BaseUtil.groupsMap(store, new Object[] { "pi_receivecode", "pi_currency" });
		}
		List<Map<Object, Object>> s = null;
		String vCode = "";
		String curr = "";
		int detno = 1;
		int acid = 0;
		for (Object m : map.keySet()) {
			if (m != null) {
				vCode = m.toString().split("#")[0].toString();
				curr = m.toString().split("#")[1].toString();
				s = map.get(m);
				if (s.size() > 0) {
					sourceid = s.get(0).get("pd_piid");
					sourcetype = s.get(0).get("pi_type");
				}
				if (mm != null) {
					bi = baseDao.getFieldsDataByCondition("apcheck", new String[] { "ac_code", "ac_id" }, "ac_code='" + accode + "'");
				} else {
					bi = APBillDao.turnAPCheck(vCode, curr, sourceid, sourcetype);
				}
				if (bi != null) {
					code = bi[0].toString();
					acid = Integer.parseInt(bi[1].toString());
					Object maxdetno = baseDao.getFieldDataByCondition("apcheckdetail", "max(ad_detno)", "ad_acid=" + acid);
					maxdetno = maxdetno == null ? 1 : Integer.parseInt(maxdetno.toString()) + 1;
					detno = Integer.parseInt(maxdetno.toString());
					for (Map<Object, Object> p : s) {
						APBillDao.turnAPCheckDetail(code, p.get("pd_id").toString(), acid, detno++,
								Double.parseDouble(p.get("pd_thisvoqty").toString()),
								Double.parseDouble(p.get("pd_thisvoprice").toString()), p.get("pi_type").toString());

					}
					baseDao.execute("update APCHECK set ac_checkamount=(select sum(ad_amount) from APCHECKDETAIL where ad_acid=ac_id) where ac_id="
							+ acid);
					if (fromDate != null) {
						baseDao.execute("update APCHECK set ac_fromdate=to_date('" + fromDate + "','yyyy-mm-dd') where ac_id=" + acid);
					}
					if (toDate != null) {
						baseDao.execute("update APCHECK set ac_todate=to_date('" + toDate + "','yyyy-mm-dd') where ac_id=" + acid);
					}
					sb.append("转入成功,应付对账单号:" + "<a href=\"javascript:openUrl('jsps/fa/arp/apCheck.jsp?formCondition=ac_idIS" + acid
							+ "&gridCondition=ad_acidIS" + acid + "')\">" + code + "</a>&nbsp;<hr>");
				}
			}
		}
		return sb.toString();
	}

	@Override
	public void confirmPrePayAPBill(int vmid, double thisamount, String data1, String data2) {
		List<Map<Object, Object>> store1 = BaseUtil.parseGridStoreToMaps(data1);
		List<Map<Object, Object>> store2 = BaseUtil.parseGridStoreToMaps(data2);
		baseDao.execute("delete from CONFIRMPREPAY where CPP_VMID=" + vmid);
		baseDao.execute("delete from CONFIRMAPBILL where CAP_VMID=" + vmid);
		double amount1 = 0;
		double amount2 = 0;
		for (Map<Object, Object> m : store1) {
			amount1 += Double.parseDouble(m.get("pp_thisamount").toString());
		}
		for (Map<Object, Object> m : store2) {
			amount2 += Double.parseDouble(m.get("ab_thisamount").toString());
		}
		thisamount = NumberUtil.formatDouble(thisamount, 2);
		amount1 = NumberUtil.formatDouble(amount1, 2);
		amount2 = NumberUtil.formatDouble(amount2, 2);
		if (thisamount != amount1) {
			BaseUtil.showError("所选预付金额合计[" + amount1 + "]与填写冲账金额[" + thisamount + "]不相等！");
		}
		if (thisamount != amount2) {
			BaseUtil.showError("所选发票金额合计[" + amount2 + "]与填写冲账金额[" + thisamount + "]不相等！");
		}
		for (Map<Object, Object> m : store1) {
			baseDao.execute("insert into CONFIRMPREPAY(CPP_VMID,CPP_PPID,CPP_AMOUNY) values (" + vmid + ", " + m.get("pp_id") + ", "
					+ m.get("pp_thisamount") + ")");
		}
		for (Map<Object, Object> m : store2) {
			baseDao.execute("insert into CONFIRMAPBILL(CAP_VMID,CAP_ABID,CAP_AMOUNY) values (" + vmid + ", " + m.get("ab_id") + ", "
					+ m.get("ab_thisamount") + ")");
		}
	}

	@Override
	public void confirmPreRecARBill(int cmid, double thisamount, String data1, String data2) {
		List<Map<Object, Object>> store1 = BaseUtil.parseGridStoreToMaps(data1);
		List<Map<Object, Object>> store2 = BaseUtil.parseGridStoreToMaps(data2);
		baseDao.execute("delete from CONFIRMPREREC where CPR_CMID=" + cmid);
		baseDao.execute("delete from CONFIRMARBILL where CAR_CMID=" + cmid);
		double amount1 = 0;
		double amount2 = 0;
		for (Map<Object, Object> m : store1) {
			amount1 += Double.parseDouble(m.get("pr_thisamount").toString());
		}
		for (Map<Object, Object> m : store2) {
			amount2 += Double.parseDouble(m.get("ab_thisamount").toString());
		}
		thisamount = NumberUtil.formatDouble(thisamount, 2);
		amount1 = NumberUtil.formatDouble(amount1, 2);
		amount2 = NumberUtil.formatDouble(amount2, 2);
		if (thisamount != amount1) {
			BaseUtil.showError("所选预收金额合计[" + amount1 + "]与填写冲账金额[" + thisamount + "]不相等！");
		}
		if (thisamount != amount2) {
			BaseUtil.showError("所选发票金额合计[" + amount2 + "]与填写冲账金额[" + thisamount + "]不相等！");
		}
		for (Map<Object, Object> m : store1) {
			baseDao.execute("insert into CONFIRMPREREC(CPR_CMID,CPR_PRID,CPR_AMOUNY) values (" + cmid + ", " + m.get("pr_id") + ", "
					+ m.get("pr_thisamount") + ")");
		}
		for (Map<Object, Object> m : store2) {
			baseDao.execute("insert into CONFIRMARBILL(CAR_CMID,CAR_ABID,CAR_AMOUNY) values (" + cmid + ", " + m.get("ab_id") + ", "
					+ m.get("ab_thisamount") + ")");
		}
	}

	@Override
	public String vastARCheckConfirm(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Integer ac_id = 0;
		for (Map<Object, Object> m : maps) {
			ac_id = Integer.parseInt(m.get("ac_id").toString());
			baseDao.updateByCondition("ARCheck", "ac_confirmstatus='已确认', ac_confirmdate=sysdate", "ac_id=" + ac_id);
			baseDao.execute("update prodiodetail set PD_YCHECK=nvl((select sum(ad_qty) from archeckdetail where ad_pdid=pd_id and ad_sourcetype='PRODINOUT'),0) where pd_id in (select ad_pdid from archeckdetail where ad_acid="
					+ ac_id + " and ad_sourcetype='PRODINOUT')");
			// 记录操作
			baseDao.logger.others("确认对账", "确认成功", "ARCheck", "ac_id", ac_id);
		}
		return "确认对账成功";
	}

	@Override
	public void anticipateCollection(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Integer an_id = 0;
		String an_code = null;
		Object type = maps.get(0).get("an_type");
		if (type == null || "".equals(type)) {
			BaseUtil.showError("请先设置催收类型！");
		}
		Integer taskid = 0;
		Employee employee = SystemSession.getUser();
		String code = null;
		Object[] an = null;
		Object seller = null;
		Object pirecorder = null;
		for (Map<Object, Object> m : maps) {
			an_id = Integer.parseInt(m.get("an_id").toString());
			an_code = m.get("an_code").toString();
			baseDao.updateByCondition("Anticipate", "an_type='" + type + "'", "an_id=" + an_id);
			baseDao.updateByCondition("Anticipate", "AN_OPERATOR='" + employee.getEm_name() + "'", "an_id=" + an_id);
			baseDao.execute("update Anticipate set (an_tel,an_email)=(select em_tel,em_email from employee where em_code='"
					+ employee.getEm_code() + "') where an_id=" + an_id);
			// 记录操作
			baseDao.logger.others(type + "操作", "催收成功", "Anticipate", "an_id", an_id);
			an = baseDao.getFieldsDataByCondition("Anticipate left join Employee on em_code=an_sellercode", new String[] { "em_id",
					"an_sellercode", "an_sellername" }, "an_id=" + an_id);
			if (an != null) {
				// 产生任务
				seller = an[2];
				taskid = baseDao.getSeqId("PROJECTTASK_SEQ");
				code = baseDao.sGetMaxNumber("ProjectTask", 2);
				baseDao.execute("insert into ProjectTask (ID,recorder,recorddate,duration,enddate,name,startdate,resourcecode,"
						+ "resourcename,resourceemid,sourcecode,sourcelink,class,sysgen,handstatus,handstatuscode,description,"
						+ "status,statuscode,taskcode)values(" + taskid + ", '系统管理员',"
						+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",48,sysdate+2,'逾期应收单-'||'" + type
						+ "',sysdate,'" + an[1] + "','" + an[2] + "'," + an[0] + ",'" + an_code
						+ "','jsps/fa/fp/anticipate.jsp?formCondition=an_idIS" + an_id + "&gridCondition=and_anidIS" + an_id
						+ "','billtask',-1,'进行中','DOING','','已审核','AUDITED','" + code + "')");
				baseDao.execute("insert into resourceAssignment (ra_id,ra_units,ra_resourcecode,ra_resourcename,ra_taskid,"
						+ "ra_startdate,ra_enddate,ra_detno,ra_taskname,ra_emid,ra_type)values(RESOURCEASSIGNMENT_SEQ.nextval,100,'"
						+ an[1] + "','" + an[2] + "'," + taskid + "," + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())
						+ "," + DateUtil.parseDateToOracleString(Constant.YMD_HMS, DateUtil.overDate(new Date(), 2)) + ",1,'逾期应收单-'||'"
						+ type + "'," + an[0] + ",'billtask')");
			}
			if ("外部催收".equals(type)) {
				pirecorder = baseDao.getFieldDataByCondition(
						"(select pi_recordman from prodinout,anticipatedetail where and_picode=pi_inoutno and nvl(and_picode,' ')<>' ' and and_anid="
								+ an_id + " order by pi_date1 desc)", "pi_recordman", "rownum=1");
				if (pirecorder != null && !pirecorder.equals(seller)) {
					taskid = baseDao.getSeqId("PROJECTTASK_SEQ");
					code = baseDao.sGetMaxNumber("ProjectTask", 2);
					an = baseDao.getFieldsDataByCondition("Employee", new String[] { "max(em_id)", "max(em_code)", "max(em_name)" },
							"em_name='" + pirecorder + "'");
					baseDao.execute("insert into ProjectTask (ID,recorder,recorddate,duration,enddate,name,startdate,resourcecode,"
							+ "resourcename,resourceemid,sourcecode,sourcelink,class,sysgen,handstatus,handstatuscode,description,"
							+ "status,statuscode,taskcode)values(" + taskid + ", '系统管理员',"
							+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",48,sysdate+2,'逾期应收单-'||'" + type
							+ "',sysdate,'" + an[1] + "','" + an[2] + "'," + an[0] + ",'" + an_code
							+ "','jsps/fa/fp/anticipate.jsp?formCondition=an_idIS" + an_id + "&gridCondition=and_anidIS" + an_id
							+ "','billtask',-1,'进行中','DOING','','已审核','AUDITED','" + code + "')");
					baseDao.execute("insert into resourceAssignment (ra_id,ra_units,ra_resourcecode,ra_resourcename,ra_taskid,"
							+ "ra_startdate,ra_enddate,ra_detno,ra_taskname,ra_emid,ra_type)values(RESOURCEASSIGNMENT_SEQ.nextval,100,'"
							+ an[1] + "','" + an[2] + "'," + taskid + "," + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())
							+ "," + DateUtil.parseDateToOracleString(Constant.YMD_HMS, DateUtil.overDate(new Date(), 2)) + ",1,'逾期应收单-'||'"
							+ type + "'," + an[0] + ",'billtask')");
				}
			}
		}
	}
}
