package com.uas.erp.service.scm.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.uas.b2b.core.PSHttpUtils;
import com.uas.b2b.model.InvitationRecord;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.exception.SystemException;
import com.uas.erp.core.support.ICallable;
import com.uas.erp.core.support.MergeTask;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.dao.common.ApplicationDao;
import com.uas.erp.dao.common.BatchDao;
import com.uas.erp.dao.common.InvoiceDao;
import com.uas.erp.dao.common.MakeDao;
import com.uas.erp.dao.common.OaPurchaseDao;
import com.uas.erp.dao.common.ProdInOutDao;
import com.uas.erp.dao.common.PurchaseDao;
import com.uas.erp.dao.common.QUAMRBDao;
import com.uas.erp.dao.common.QUAVerifyApplyDetailDao;
import com.uas.erp.dao.common.QuotationDao;
import com.uas.erp.dao.common.SaleDao;
import com.uas.erp.dao.common.SaleForecastDao;
import com.uas.erp.dao.common.SendNotifyDao;
import com.uas.erp.dao.common.VerifyApplyDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Key;
import com.uas.erp.model.Master;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.common.AccountCenterService;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.oa.SendMailService;
import com.uas.erp.service.scm.BatchDealService;
import com.uas.erp.service.scm.ProdInOutService;
import com.uas.erp.service.scm.SendNotifyService;

@Service("ScmBatchDealService")
public class BatchDealServiceImpl implements BatchDealService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private ApplicationDao applicationDao;
	@Autowired
	private PurchaseDao purchaseDao;
	@Autowired
	private QuotationDao quotationDao;
	@Autowired
	private SendNotifyDao sendNotifyDao;
	@Autowired
	private SaleDao saleDao;
	@Autowired
	private MakeDao makeDao;
	@Autowired
	private VerifyApplyDao verifyApplyDao;
	@Autowired
	private QUAVerifyApplyDetailDao QUAVerifyApplyDetailDao;
	@Autowired
	private ProdInOutDao prodInOutDao;
	@Autowired
	private InvoiceDao invoiceDao;
	@Autowired
	private SaleForecastDao saleForecastDao;
	@Autowired
	private QUAMRBDao QUAMRBDao;
	@Autowired
	private BatchDao batchDao;
	@Autowired
	private ProdInOutService prodInOutService;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private TransferRepository transferRepository;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private EnterpriseService enterpriseService;
	@Autowired
	private SendMailService sendMailService;
	@Autowired
	private SendNotifyService sendNotifyService;
	@Autowired
	private AccountCenterService accountCenterService;
	@Autowired
	private OaPurchaseDao oapurchaseDao;
	/**
	 * 请购转采购
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	// 如果有事务,那么加入事务,没有的话新建一个。
	public String vastTurnPurc(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		// 判断本次数量
		applicationDao.checkAdYqty(maps);
		// 整批转采购单
		JSONObject j = null;
		Object code = null;
		Object vCode = null;
		Object SetCode = null;
		Object SetCode_rate = null;
		String type = null;
		StringBuffer sb = new StringBuffer();
		String log = null;
		int index = 0;
		Map<String, List<Map<Object, Object>>> vends = new HashMap<String, List<Map<Object, Object>>>();
		String pointedVendor = null;
		String pointedVendorCurrency = null;
		Set<String> pu_codes = new HashSet<String>();
		String adidstr = "";
		for (Map<Object, Object> map : maps) {
			adidstr += "," + map.get("ad_id").toString();
		}
		if (!adidstr.equals("")) {
			adidstr = adidstr.substring(1);
			SqlRowList rs = baseDao
					.queryForRowSet("select  count(1) n from (select distinct NVL(pk_mrp,0) kind from application,applicationdetail,purchasekind where ap_id=ad_apid and ad_id in ("
							+ adidstr + ") and ap_kind=pk_name)");
			if (rs.next()) {
				if (rs.getInt("n") > 1) {
					BaseUtil.showError("参与MRP运算的请购必须与不参与的请购分开下达!");
				}
			}
			//不同采购账套的请购单不允许一起转采购
			rs = baseDao.queryForRowSet("select count(1) n from (select DISTINCT nvl(AP_PURCHASECOP,' ') from application left join applicationdetail on ap_id = ad_apid where ad_id in ("+adidstr+"))");
			if(rs.next()&&rs.getInt("n")>1){
				BaseUtil.showError("不同采购账套的请购单不允许一起转采购,请分开转采购!");
			}
			rs = baseDao
					.queryForRowSet("select  count(1) n from (select distinct NVL(pk_iflack,0) kind from application,applicationdetail,purchasekind where ap_id=ad_apid and ad_id in ("
							+ adidstr + ") and ap_kind=pk_name)");
			if (rs.next()) {
				if (rs.getInt("n") > 1) {
					BaseUtil.showError("参与缺料运算的请购必须与不参与的请购分开下达!");
				}
			}
			if (baseDao.isDBSetting("CopCheck")) {
				rs = baseDao
						.queryForRowSet("select  count(1) n from (select distinct ap_cop from application,applicationdetail where ap_id=ad_apid and ad_id in ("
								+ adidstr + ") )");
				if (rs.next()) {
					if (rs.getInt("n") > 1) {
						BaseUtil.showError("所属公司不一致的请购单不允许合并下达到一张采购单中!");
					}
				}
			}
			if (baseDao.isDBSetting(caller, "allowDifferentKind")) {
				rs = baseDao
						.queryForRowSet("select  count(1) n from (select distinct NVL(pk_mrp,0),nvl(pk_iflack,0) from application,applicationdetail,purchasekind where ap_id=ad_apid and ad_id in ("
								+ adidstr + ") and ap_kind=pk_name)");
				if (rs.next()) {
					if (rs.getInt("n") > 1) {
						BaseUtil.showError("采购类型中的[参与MRP运算]+[参与缺料运算]同时一致的情况下才可以合并下达到一张采购单中!");
					}
				}
			} else {
				rs = baseDao
						.queryForRowSet("select  count(1) n from (select distinct ap_kind from application,applicationdetail where ap_id=ad_apid and ad_id in ("
								+ adidstr + ") )");
				if (rs.next()) {
					if (rs.getInt("n") > 1) {
						BaseUtil.showError("不同请购类型不能下达到一张采购单中!");
					}
				}
			}
			if (baseDao.isDBSetting(caller, "mrpSeparateFactory")) {
				rs = baseDao
						.queryForRowSet("select  count(1) n from (select distinct ad_factory from application,applicationdetail where ap_id=ad_apid and ad_id in ("
								+ adidstr + ") )");
				if (rs.next()) {
					if (rs.getInt("n") > 1) {
						BaseUtil.showError("不同的所属工厂不能下达到一张采购单中!");
					}
				}
			}
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat('请购单号：'||ad_code||',行：'||ad_detno||',明细状态：'||ad_status) from ApplicationDetail where nvl(ad_statuscode,' ') in ('FINISH','NULLIFIED','FREEZE') and ad_id in ("
									+ adidstr + ")", String.class);
			if (dets != null) {
				BaseUtil.showError("选中的明细行已结案、已冻结、已作废，不允许转入采购单!" + dets);
			}
		}
		if (maps.size() > 0) {
			// 指定了采购单
			if (StringUtil.hasText(maps.get(0).get("pu_code"))) {
				code = maps.get(0).get("pu_code");
				// 判断指定的采购单状态是否[在录入],指定采购单的请购类型是否与当前的一致
				Object[] pu = baseDao.getFieldsDataByCondition("Purchase", new String[] { "pu_statuscode", "pu_kind", "pu_id",
						"pu_vendcode", "pu_currency", "pu_cop" }, "pu_code='" + code + "'");
				if (pu == null) {
					BaseUtil.showError("指定的采购单不存在或已删除!");
				} else if (!"ENTERING".equals(String.valueOf(pu[0]))) {
					BaseUtil.showError("指定的采购单状态不等于[在录入]!");
				} else {
					String log1 = null;
					StringBuffer sb1 = new StringBuffer();
					for (Map<Object, Object> map : maps) {
						if (StringUtil.hasText(map.get("ad_vendor"))) {
							if (!pu[3].toString().equals(String.valueOf(map.get("ad_vendor")))) {
								log1 = "所选请购单供应商：" + map.get("ad_vendor") + "，与指定采购单的供应商：" + pu[3] + "不一致!";
							}
							if (log1 != null) {
								sb1.append(log1).append("<hr>");
							}
						}
						if (StringUtil.hasText(map.get("ad_currency"))) {
							if (StringUtil.hasText(pu[4]) && !pu[4].toString().equals(String.valueOf(map.get("ad_currency")))) {
								log1 = "所选请购单币别：" + map.get("ad_currency") + "，与指定采购单的币别:" + pu[4] + "不一致!";
							}
							if (log1 != null) {
								sb1.append(log1).append("<hr>");
							}
						}
						if (!baseDao.isDBSetting(caller, "allowDifferentKind")) {
							if (!pu[1].toString().equals(String.valueOf(map.get("ap_kind")))) {
								log1 = "所选请购单合同类型：" + map.get("ap_kind") + "，与指定采购单的合同类型:" + pu[1] + "不一致!";
							}
						}
						if (log1 != null) {
							sb1.append(log1).append("<hr>");
						}
						if (baseDao.isDBSetting("CopCheck")) {
							if (!pu[5].toString().equals(String.valueOf(map.get("ap_cop")))) {
								log1 = "所选请购单所属公司：" + map.get("ap_cop") + "，与指定采购单的所属公司:" + pu[5] + "不一致!";
							}
						}
						if (log1 != null) {
							sb1.append(log1).append("<hr>");
						}
					}
					if (sb1.length() > 0) {
						BaseUtil.showError(sb1.toString());
					}
				}
				// 转入明细
				transferRepository.transfer(caller, maps, new Key(Integer.parseInt(pu[2].toString()), code.toString()));
				log = "转入成功,采购单号:" + "<a href=\"javascript:openUrl('jsps/scm/purchase/purchase.jsp?formCondition=pu_idIS" + pu[2]
						+ "&gridCondition=pd_puidIS" + pu[2] + "')\">" + code + "</a>&nbsp;";
				sb.append(log).append("<hr>");
				pu_codes.add(code.toString());
				splitBeiPin(Integer.parseInt(pu[2].toString()), code.toString());
			} else {
				Boolean groupByTaxrate = baseDao.isDBSetting("Application!ToPurchase!Deal", "groupByTaxrate");
				for (Map<Object, Object> map : maps) {
					// 币别取价格表币别
					type = map.get("ap_type") == null ? null : map.get("ap_type").toString();
					// 如果没指定，看是否指定了供应商
					vCode = map.get("ap_vendcode");
					Object taxRate = null;
					if (vCode == null || vCode.equals("")) {
						// 没有指定供应商，按照明细供应商号+币别分组
						vCode = map.get("ad_vendor");
						Object Currency = map.get("ad_currency");
						if (Currency != null && !Currency.equals("")) {
							vCode = vCode + "#" + Currency;
						}
						if (groupByTaxrate) {
							if (Currency == null || "".equals(Currency)) {
								Currency = baseDao.getFieldDataByCondition("vendor", "ve_currency", "ve_code='" + map.get("ad_vendor")
										+ "'");
							}
							// 取价原则：抓取最近一次采购单单价  maz  2018060670  改成radio形式 增加取采购验收单单价
							String PriceByPurc = baseDao.getDBSetting("Purchase", "getPriceByPurc");
							if ("1".equals(PriceByPurc)) {// 取价原则：抓取最近一次采购单单价
								SqlRowList rs = baseDao
										.queryForRowSet(
												"SELECT pd_rate FROM (select nvl(pd_rate,0) pd_rate from PurchaseDetail LEFT JOIN Purchase on pu_id=pd_puid "
														+ "where pd_prodcode=? and pu_currency=? and pu_vendcode=? and pu_statuscode='AUDITED' and pu_auditdate is not null "
														+ "order by pu_auditdate desc) WHERE rownum<2", map.get("ad_prodcode"), Currency,
												map.get("ad_vendor"));
								if (rs.next()) {
									taxRate = rs.getGeneralDouble("pd_rate");
									vCode = vCode + "#" + taxRate;
								}
							} else if ("2".equals(PriceByPurc)){
								SqlRowList pd = baseDao.queryForRowSet(
										"SELECT pd_price,pd_customprice,pd_taxrate,pd_netprice FROM (select nvl(pd_orderprice,0) pd_price,nvl(pd_customprice,0) pd_customprice,nvl(pd_taxrate,0) pd_taxrate,nvl(pd_netprice,0) pd_netprice "
												+ "from ProdIODetail LEFT JOIN ProdInOut on pd_piid=pi_id where pd_prodcode=? and pi_currency=? and pi_cardcode=? and pi_statuscode='POSTED' order by pi_date desc) WHERE rownum<2",
												map.get("ad_prodcode"), Currency, map.get("ad_vendor"));
								if (pd.next()) {
									taxRate = pd.getGeneralDouble("pd_taxrate");
									vCode = vCode + "#" + taxRate;
								}
							} else {
								JSONObject js = purchaseDao.getPurchasePrice(map.get("ad_vendor").toString(), map.get("ad_prodcode")
										.toString(), Currency.toString(), "采购", Double.parseDouble(map.get("ad_tqty").toString()),
										"sysdate");
								if (js != null) {
									taxRate = js.getDouble("pd_rate");
									vCode = vCode + "#" + taxRate;
								}
							}
						}
						List<Map<Object, Object>> list = null;
						if (!vends.containsKey(vCode)) {
							list = new ArrayList<Map<Object, Object>>();
						} else {
							list = vends.get(vCode);
						}
						list.add(map);
						vends.put(vCode.toString(), list);
					} else {
						// 指定了供应商
						if (pointedVendor == null) {
							pointedVendor = maps.get(0).get("ap_vendcode").toString();
							Object[] objs = baseDao.getFieldsDataByCondition("Vendor", new String[] { "ve_id", "ve_name", "ve_currency" },
									"ve_code='" + pointedVendor + "'");
							if (objs == null) {
								BaseUtil.showError("指定供应商[" + pointedVendor + "]不存在！");
							}
							if (objs[2] == null) {
								BaseUtil.showError("供应商资料没有填写默认币别.");
							} else {
								SetCode = pointedVendor + "#" + objs[2];
								SetCode_rate = pointedVendor + "#" + objs[2];
								pointedVendorCurrency = objs[2].toString();
							}
						}
						if (groupByTaxrate) {
							String PriceByPurc = baseDao.getDBSetting("Purchase", "getPriceByPurc");
							if ("1".equals(PriceByPurc)) {// 取价原则：抓取最近一次采购单单价
								SqlRowList rs = baseDao
										.queryForRowSet(
												"SELECT pd_rate FROM (select nvl(pd_rate,0) pd_rate from PurchaseDetail LEFT JOIN Purchase on pu_id=pd_puid "
														+ "where pd_prodcode=? and pu_currency=? and pu_vendcode=? and pu_statuscode='AUDITED' and pu_auditdate is not null "
														+ "order by pu_auditdate desc) WHERE rownum<2", map.get("ad_prodcode"), pointedVendorCurrency,
														pointedVendor);
								if (rs.next()) {
									taxRate = rs.getDouble("pd_rate");
									SetCode_rate = SetCode + "#" + taxRate;
								}
							} else if ("2".equals(PriceByPurc)){
								SqlRowList pd = baseDao.queryForRowSet(
										"SELECT pd_price,pd_customprice,pd_taxrate,pd_netprice FROM (select nvl(pd_orderprice,0) pd_price,nvl(pd_customprice,0) pd_customprice,nvl(pd_taxrate,0) pd_taxrate,nvl(pd_netprice,0) pd_netprice "
												+ "from ProdIODetail LEFT JOIN ProdInOut on pd_piid=pi_id where pd_prodcode=? and pi_currency=? and pi_cardcode=? and pi_statuscode='POSTED' order by pi_date desc) WHERE rownum<2",
												map.get("ad_prodcode"), pointedVendorCurrency, pointedVendor);
								if (pd.next()) {
									taxRate = pd.getDouble("pd_taxrate");
									SetCode_rate = SetCode + "#" + taxRate;
								}
							} else {
								JSONObject js = purchaseDao.getPurchasePrice(pointedVendor, map.get("ad_prodcode").toString(),
										pointedVendorCurrency, "采购", Double.parseDouble(map.get("ad_tqty").toString()), "sysdate");
								if (js != null) {
									taxRate = js.getDouble("pd_rate");
									SetCode_rate = SetCode + "#" + taxRate;
								}
							}
						}
						if (pointedVendor != null) {
							List<Map<Object, Object>> list = null;
							if (!vends.containsKey(SetCode_rate)) {
								list = new ArrayList<Map<Object, Object>>();
							} else {
								list = vends.get(SetCode_rate);
							}
							list.add(map);
							vends.put(SetCode_rate.toString(), list);

						}
					}
				}
				// 按供应商分组的转入操作
				Set<String> mapSet = vends.keySet();
				String conKind;
				String vendcode = "";
				String currency = "";
				for (String s : mapSet) {
					List<Map<Object, Object>> list = vends.get(s);
					conKind = String.valueOf(list.get(0).get("ap_kind"));
					if (s.contains("#")) {
						vendcode = s.split("#")[0];
						currency = s.split("#")[1];
					} else
						vendcode = s;
					Object[] objs = baseDao.getFieldsDataByCondition("Vendor", new String[] { "ve_id", "ve_name", "ve_currency" },
							"ve_code='" + vendcode + "'");
					if (objs == null) {
						BaseUtil.showError("供应商[" + vendcode + "]不存在！");
					}
					objs[0] = objs[0] == null ? "" : objs[0];
					objs[1] = objs[1] == null ? "" : objs[1];
					objs[2] = objs[2] == null ? "" : objs[2];
					currency = currency.equals("") ? objs[2].toString() : currency;
					j = applicationDao.newPurchaseWithVendor(type, Integer.parseInt(objs[0].toString()), vendcode, objs[1].toString(),
							conKind, currency);
					if (j != null) {
						int pu_id = j.getInt("pu_id");
						code = j.getString("pu_code");
						index++;
						if (baseDao.isDBSetting("Application!ToPurchase!Deal", "defaultGetPrice")) {
							baseDao.execute("update purchase set pu_getprice=0,pu_mainmark='非标准' where pu_id=" + pu_id);
						}
						pu_codes.add(code.toString());
						// 转入明细
						transferRepository.transfer(caller, list, new Key(pu_id, code.toString()));
						log = "转入成功,采购单号:" + "<a href=\"javascript:openUrl('jsps/scm/purchase/purchase.jsp?formCondition=pu_idIS" + pu_id
								+ "&gridCondition=pd_puidIS" + pu_id + "')\">" + code + "</a>&nbsp;";
						sb.append(index).append(": ").append(log).append("<hr>");
						splitBeiPin(pu_id, code);
					}
				}
			}
			for (Map<Object, Object> map : maps) {
				int mrid = 0;
				if (map.containsKey("mr_id")) {
					mrid = Integer.parseInt(map.get("mr_id").toString());
				}
				double tqty = Double.parseDouble(map.get("ad_tqty").toString());
				if (mrid > 0) {
					baseDao.updateByCondition("MrpReplace", "mr_purcqty=nvl(mr_purcqty,0)+" + tqty, "mr_id=" + mrid);
				}
			}
			// applicationreplace
			for (Map<Object, Object> map : maps) {
				int arid = 0;
				if (map.containsKey("ar_id")) {
					arid = Integer.parseInt(map.get("ar_id").toString());
				}
				double tqty = Double.parseDouble(map.get("ad_tqty").toString());
				if (arid > 0) {
					baseDao.updateByCondition("applicationreplace", "ar_purcqty=nvl(ar_purcqty,0)+" + tqty, "ar_id=" + arid);
				}
			}
		}
		String pucodes = CollectionUtil.toSqlString(pu_codes);
		// 检查是否有超请购数量下达采购的
		if (!baseDao.isDBSetting("Purchase", "AllowOut")) {
			SqlRowList rs0 = baseDao
					.queryForRowSet("select ad_prodcode from purchase left join purchasedetail on pu_id=pd_puid left join applicationdetail on ad_id=pd_sourcedetail where pu_code in ("
							+ pucodes + ") and ad_qty<ad_yqty");
			if (rs0.next()) {
				BaseUtil.showError("物料：" + rs0.getString("ad_prodcode") + "超请购数量下达");
			}
		}
		// 修改请购单状态
		for (Map<Object, Object> map : maps) {
			int adid = Integer.parseInt(map.get("ad_id").toString());
			applicationDao.checkAdQty(adid);
		}
		baseDao.execute("update purchase set (pu_receivecode,pu_receivename)=(select ve_apvendcode,ve_apvendname from vendor where ve_code=pu_vendcode) where pu_code in ("
				+ pucodes + ") and nvl(pu_receivecode,' ')=' '");
		baseDao.execute("update purchase set pu_currency=(select ve_currency from vendor where ve_code=pu_vendcode) where pu_code in ("
				+ pucodes + ") and nvl(pu_currency,' ')=' '");

		baseDao.execute("update purchase set (pu_custcode,pu_custname)=(select ve_custcode,ve_custname from vendor where pu_vendcode=ve_code and nvl(ve_custcode,' ')<>' '),"// 更新客户信息
				+ "(pu_buyerid,pu_buyercode,pu_buyername)=(select max(em_id),max(em_code),max(em_name) from product left join purchasedetail on pd_prodcode=pr_code left join employee "
				+ "on em_name=pr_buyername where pd_puid=pu_id and nvl(pr_buyername,' ')<>' '),"// 按物料的采购员信息更新采购单
				+ "pu_cop=(select max(ap_cop) from purchasedetail,application where pd_puid=pu_id and pd_source=ap_id),"
				+ "pu_rate=(select cm_crrate from currencysmonth where cm_crname=pu_currency and cm_yearmonth=to_char(pu_date,'yyyymm'))"
				+ " where pu_code in (" + pucodes + ")");

		// 更新交货地址
		baseDao.execute("update purchase set pu_shipaddresscode=(select en_deliveraddr from enterprise where nvl(en_deliveraddr,' ')<>' ') where pu_code in ("
				+ pucodes + ") and nvl(pu_shipaddresscode,' ')=' '");
		// 按请购单的采购员信息更新采购单
		baseDao.execute("update purchase set (pu_buyerid,pu_buyercode,pu_buyername)=(select max(em_id),max(em_code),max(em_name) from application left join purchasedetail on pd_source=ap_id left join employee on em_code=ap_buyercode where pd_puid=pu_id and nvl(ap_buyercode,' ')<>' ') where pu_code in ("
				+ pucodes
				+ ") and pu_id in (select pd_puid from purchasedetail where pd_source in (select ap_id from application where nvl(ap_buyercode,' ')<>' '))");
		baseDao.execute("update purchase set pu_buyerid=" + SystemSession.getUser().getEm_id() + ",pu_buyername='"
				+ SystemSession.getUser().getEm_name() + "',pu_buyercode='" + SystemSession.getUser().getEm_code() + "' where pu_code in ("
				+ pucodes + ") and pu_buyercode is null");
		baseDao.execute("update purchasedetail set pd_appdate=(select ap_auditdate from application where pd_sourcecode=ap_code) where pd_code in ("
				+ pucodes + ")");
		// 取价原则：抓取最近一次采购单单价  maz  2018060670  改成radio形式 增加取采购验收单单价
		String PriceByPurc = baseDao.getDBSetting("Purchase", "getPriceByPurc");
		if ("1".equals(PriceByPurc)) {
			SqlRowList rs = baseDao
					.queryForRowSet("select pu_id from purchase where pu_code in (" + pucodes + ") and nvl(pu_getprice,0)=0");
			while (rs.next()) {
				purchaseDao.getLastPrice(rs.getGeneralInt(1));
			}
		} else if ("2".equals(PriceByPurc)){
			SqlRowList rs = baseDao
					.queryForRowSet("select pu_id from purchase where pu_code in (" + pucodes + ") and nvl(pu_getprice,0)=0");
			while (rs.next()){
				SqlRowList rs1 = baseDao.queryForRowSet(
						"SELECT * FROM PurchaseDetail LEFT JOIN Purchase on pu_id=pd_puid WHERE pu_id=? and nvl(pd_price,0)=0 and nvl(pu_getprice,0)=0",
						rs.getInt("pu_id"));
				SqlRowList pd = baseDao.queryForRowSet(
						"SELECT pd_price,pd_customprice,pd_taxrate,pd_netprice FROM (select nvl(pd_orderprice,0) pd_price,nvl(pd_customprice,0) pd_customprice,nvl(pd_taxrate,0) pd_taxrate,nvl(pd_netprice,0) pd_netprice "
								+ "from ProdIODetail LEFT JOIN ProdInOut on pd_piid=pi_id where pd_prodcode=? and pi_currency=? and pi_cardcode=? and pi_statuscode='POSTED' order by pi_date desc) WHERE rownum<2",
						rs1.getString("pd_prodcode"), rs1.getString("pu_currency"), rs1.getString("pu_vendcode"));
				if (pd.next()) {
					baseDao.updateByCondition("PurchaseDetail",
							"pd_price=" + pd.getGeneralDouble("pd_price") + ",pd_bgprice=" + pd.getGeneralDouble("pd_customprice") + ", pd_rate="
									+ pd.getGeneralDouble("pd_taxrate") + ", pd_netprice=" + pd.getGeneralDouble("pd_netprice"),
							"pd_id=" + rs1.getGeneralInt("pd_id"));
				}
			}
		}else {
			// 修改新增的采购单的单价
			SqlRowList rs = baseDao.queryForRowSet("select pu_code from purchase where pu_code in (" + pucodes
					+ ") and abs(nvl(pu_getprice,0))=1");
			while (rs.next()) {
				purchaseDao.getPrice(rs.getString(1));
			}
		}
		Object buyercode = maps.get(0).get("ap_buyercode");
		if(StringUtil.hasText(buyercode)){ //指定了采购员 欣康宁 2018040473 maz
			Object[] buyer = baseDao.getFieldsDataByCondition("employee",new String[]{"em_id","em_name"}, "em_code='"+buyercode+"'");
			baseDao.execute("update purchase set pu_buyercode='"+buyercode+"',pu_buyername='"+buyer[1]+"',pu_buyerid="+buyer[0]+" where pu_code in ("+ pucodes + ")");
		}
		/**
		 * @author wsy 双单位
		 */
		baseDao.execute("update purchasedetail set pd_purcqty=Round(pd_qty/(select case when nvl(pr_purcrate,0)=0 then 1 else pr_purcrate end from product where pr_code=pd_prodcode), 2) where pd_code in ("
				+ pucodes + ")");
		baseDao.execute("update PurchaseDetail set pd_total=round(pd_price*(case when nvl(pd_purcqty,0)=0 then pd_qty else pd_purcqty end),2),pd_netprice=round(nvl(pd_price,0)/(1+nvl(pd_rate,0)/100),8),"
				+ "pd_taxtotal=round(nvl(pd_netprice,0)*nvl((case when nvl(pd_purcqty,0)=0 then pd_qty else pd_purcqty end),0),2)"
				+ " where pd_code in (" + pucodes + ")");
		baseDao.execute("update Purchase set (pu_total,pu_taxtotal)=(select sum(pd_total),sum(pd_taxtotal) from PurchaseDetail where PurchaseDetail.pd_puid = Purchase.pu_id)"
				+ "where pu_code in (" + pucodes + ")");
		baseDao.execute("update Purchase set pu_totalupper=L2U(nvl(pu_total,0)) WHERE pu_code in (" + pucodes + ")");
		handlerService.handler(caller, "turnPur", "after", new Object[] { pucodes });
		return sb.toString();
	}

	private void splitBeiPin(int pu_id, Object code) {
		boolean bool = baseDao.isDBSetting("Purchase", "NoSplit");
		SqlRowList rs = baseDao
				.queryForRowSet("select purchasedetail.*,trunc(nvl(pd_qty,0)*0.01*nvl(PR_BPLOSSRATE,0),nvl(pr_precision,0)) beipinQty from purchasedetail left join purchase on pd_puid=pu_id left join product on pd_prodcode=pr_code where nvl(PR_BPLOSSRATE,0)>0 and pd_cansup is null and pu_id="
						+ pu_id);
		if (bool) {
			baseDao.updateByCondition(
					"purchasedetail",
					"pd_beipin=trunc(0.01*(Round(pd_qty/(select case when nvl(pr_purcrate,0)=0 then 1 else pr_purcrate end from product where pr_code=pd_prodcode), 2))*(select nvl(PR_BPLOSSRATE,0) from product where pr_code=pd_prodcode),(select nvl(pr_precision,0) from product where pr_code=pd_prodcode))",
					"pd_puid=" + pu_id);
		} else {
			int detno = Integer.parseInt(baseDao.getFieldDataByCondition("purchasedetail", "max(pd_detno)+1", "pd_puid=" + pu_id)
					.toString());
			while (rs.next()) {
				int pd_id = baseDao.getSeqId("PURCHASEDETAIL_SEQ");
				double beipin = rs.getDouble("beipinQty");
				if (beipin > 0) {
					baseDao.updateByCondition("purchasedetail", "pd_qty=pd_qty-'" + rs.getDouble("beipinQty") + "',pd_cansup='已拆分备品数到独立行:"
							+ detno + "',pd_mark='大货'", "pd_id=" + rs.getInt("pd_id"));
					baseDao.execute("insert into purchasedetail(pd_id,pd_puid,pd_code,pd_detno,pd_prodcode,pd_qty,pd_delivery,pd_price,pd_total,pd_sourcecode,pd_status,pd_auditstatus,pd_cansup,pd_beipin,pd_mark,pd_sourcedetail,pd_source,pd_remark) "
							+ "values ("
							+ pd_id
							+ ","
							+ pu_id
							+ ",'"
							+ code
							+ "',"
							+ detno
							+ ",'"
							+ rs.getString("pd_prodcode")
							+ "',"
							+ rs.getDouble("beipinQty")
							+ ",to_date(substr('"
							+ rs.getString("pd_delivery")
							+ "',0,10),'yyyy-mm-dd'),0,0,'"
							+ rs.getString("pd_sourcecode")
							+ "','ENTERING','在录入','拆分来源明细序号："
							+ rs.getInt("pd_detno")
							+ "','"
							+ rs.getDouble("beipinQty")
							+ "','备品',"
							+ rs.getInt("pd_sourcedetail")
							+ ",'"
							+ rs.getInt("pd_source")
							+ "','"
							+ (rs.getString("pd_remark") == null ? "备品数" : (rs.getString("pd_remark") + ",备品数")) + "')");
					detno++;
				}
			}
		}

	}

	/**
	 * 请购转分配
	 */
	@Override
	public void vastTurnDistribute(String caller, int[] id) {
		// 整批转分配

		// 记录日志
	}

	/**
	 * 报价单转销售单
	 */
	@Override
	public String vastTurnSale(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		// 判断本次数量
		quotationDao.checkQdYqty(maps);
		// 整批转销售订
		JSONObject j = null;
		Object code = null;
		Object cuCode = null;
		StringBuffer sb = new StringBuffer();
		Set<String> sa_codes = new HashSet<String>();
		String log = null;
		int index = 0;
		Map<String, List<Map<Object, Object>>> cust = new HashMap<String, List<Map<Object, Object>>>();
		String pointedCust = null;
		for (Map<Object, Object> map : maps) {
			// 是否指定了销售单
			if (StringUtil.hasText(maps.get(0).get("sa_code"))) {
				// 判断指定的销售单状态是否[在录入]
				Object[] sa = baseDao.getFieldsDataByCondition("Sale", new String[] { "sa_id", "sa_statuscode" }, "sa_code='" + code + "'");
				if (sa == null) {
					BaseUtil.showError("指定的销售订单不存在或已删除!");
				} else if (!"ENTERING".equals(sa[1])) {
					BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.quotation.turn_onlyEntering")
							+ "<a href=\"javascript:openUrl('jsps/scm/sale/sale.jsp?formCondition=sa_codeIS" + code
							+ "&gridCondition=sd_codeIS" + code + "')\">" + code + "</a>&nbsp;");
				} else {
					transferRepository.transfer(caller, maps, new Key(Integer.parseInt(sa[0].toString()), code.toString()));
					log = "转入成功,销售单号:" + "<a href=\"javascript:openUrl('jsps/scm/sale/sale.jsp?formCondition=sa_idIS" + sa[0]
							+ "&gridCondition=sd_saidIS" + sa[0] + "')\">" + code + "</a>&nbsp;";
					sb.append(log).append("<hr>");
					sa_codes.add(code.toString());
				}
			} else {
				// 如果没指定，看是否指定了客户
				cuCode = map.get("cu_code");
				if (code == null || code.equals("")) {
					// 没有指定客户，按照明细客户+币别+收款方式分组
					cuCode = map.get("qu_custcode");
					Object Currency = map.get("qu_currency");
					Object payments = map.get("qu_paymentscode");
					if (Currency != null && !Currency.equals("")) {
						cuCode = cuCode + "#" + Currency + "#" + payments;
					}
					List<Map<Object, Object>> list = null;
					if (!cust.containsKey(cuCode)) {
						list = new ArrayList<Map<Object, Object>>();
					} else {
						list = cust.get(cuCode);
					}
					list.add(map);
					cust.put(cuCode.toString(), list);
				} else {
					// 指定了客户
					if (pointedCust == null) {
						pointedCust = cuCode.toString();
						Object[] objs = baseDao.getFieldsDataByCondition("Customer", new String[] { "cu_id", "cu_name", "cu_currency",
								"cu_paymentscode" }, "cu_code='" + pointedCust + "'");
						if (objs == null) {
							BaseUtil.showError("指定客户[" + pointedCust + "]不存在！");
						}
						cuCode = cuCode + "#" + objs[2] + "#" + objs[3];
						List<Map<Object, Object>> list = null;
						if (!cust.containsKey(cuCode)) {
							list = new ArrayList<Map<Object, Object>>();
						} else {
							list = cust.get(cuCode);
						}
						list.add(map);
						cust.put(cuCode.toString(), list);
					}
				}
				// 按客户分组的转入操作
				Set<String> mapSet = cust.keySet();
				String custcode = "";
				String currency = "";
				String payments = "";
				for (String s : mapSet) {
					if (s.contains("#")) {
						custcode = s.split("#")[0];
						currency = s.split("#")[1];
						payments = s.split("#")[2];
					} else {
						custcode = s;
						Object[] objs = baseDao.getFieldsDataByCondition("Customer", new String[] { "cu_id", "cu_name", "cu_currency",
								"cu_paymentscode" }, "cu_code='" + custcode + "'");
						objs[0] = objs[0] == null ? "" : objs[0];
						objs[1] = objs[1] == null ? "" : objs[1];
						objs[2] = objs[2] == null ? "" : objs[2];
						currency = currency.equals("") ? objs[2].toString() : currency;
						payments = currency.equals("") ? objs[3].toString() : payments;
						j = quotationDao.newSaleWithCustomer(Integer.parseInt(objs[0].toString()), custcode, objs[1].toString(), currency,
								payments);
						if (j != null) {
							int sa_id = j.getInt("sa_id");
							code = j.getString("sa_code");
							index++;
							sa_codes.add(code.toString());
							// 转入明细
							transferRepository.transfer(caller, maps, new Key(sa_id, code.toString()));
							log = "转入成功,销售单号:" + "<a href=\"javascript:openUrl('jsps/scm/sale/sale.jsp?formCondition=sa_idIS" + sa_id
									+ "&gridCondition=sd_saidIS" + sa_id + "')\">" + code + "</a>&nbsp;";
							sb.append(index).append(": ").append(log).append("<hr>");
						}
					}
				}
			}
		}
		// 修改报价单状态
		for (Map<Object, Object> map : maps) {
			int sdid = Integer.parseInt(map.get("qd_id").toString());
			quotationDao.checkAdQty(sdid);
		}
		baseDao.execute("update SaleDetail set sd_total=round(sd_qty*sd_price,2),sd_costprice=round(sd_price/(1+sd_taxrate/100),6),sd_taxtotal=round(sd_qty*sd_price/(1+sd_taxrate/100),2) where sd_code in ("
				+ sa_codes + ")");
		baseDao.execute("update Sale set sa_total=(select nvl(sum(nvl(sd_total,0)),0) from saledetail where sd_said=sa_id) where sa_code in ("
				+ sa_codes + ")");
		return sb.toString();
	}

	/**
	 * 采购单批量转收料单
	 */
	@Override
	public String vastTurnAccept(String caller, String data, String formParam) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Map<Object, Object> formStore = BaseUtil.parseFormStoreToMap(formParam);
		StringBuffer sb = new StringBuffer();
		int index = 0;
		String ids = "";
		String sign = "";
		if (maps.size() > 0) {
			// maz 2018030057 2018-03-09 增加英唐电子特殊参数判断rohs报告不齐全，限制转单
			if (baseDao.isDBSetting("Purchase!ToAccept!Deal", "RoHSTurn") && StringUtil.hasText(maps.get(0).get("pd_prodcode"))) {
				for (Map<Object, Object> map : maps) {
					SqlRowList rs = baseDao
							.queryForRowSet("select pr_code from product where pr_manutype='PURCHASE' and (pr_rohs='否' or pr_rohs is null) and pr_code='"
									+ map.get("pd_prodcode") + "'");
					if (rs.next()) {
						sign = sign + rs.getString("pr_code") + ",";
					}
				}
				if (sign != null && !"".equals(sign)) {
					sign = sign.substring(0, sign.length() - 1);
					BaseUtil.showError("存在RoHS报告不齐全的物料，不能转单，料号:" + sign);
				}
			}
			// 判断采购单状态、本次数量限制
			purchaseDao.checkPdYqty(maps);
			handlerService.handler(caller, "turnVerifyApply", "before", new Object[] { maps });
			String log = null;
			Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(maps, new Object[] { "pu_vendcode", "pu_receivecode",
					"pu_currency", "pu_paymentscode" });
			// 按客户分组的转入操作
			Set<Object> mapSet = groups.keySet();
			List<Map<Object, Object>> items;
			for (Object s : mapSet) {
				items = groups.get(s);
				// 转入通知单主记录
				Integer pu_id = baseDao.getFieldValue("PurchaseDetail", "pd_puid", "pd_id=" + items.get(0).get("pd_id"), Integer.class);
				Key key = transferRepository.transfer(caller, pu_id);
				if (key != null) {
					int va_id = key.getId();
					ids = ids + "," + va_id;
					index++;
					// 转入明细
					transferRepository.transfer(caller, items, key);
					baseDao.execute("update VerifyApplyDetail set vad_unitpackage=vad_qty where vad_vaid=" + va_id);
					baseDao.execute(
							"update VerifyApply set va_rate=(SELECT nvl(cm_crrate,0) from Currencysmonth where va_currency=cm_crname and cm_yearmonth=to_char(va_date,'yyyymm')) where va_id=?",
							va_id);
					baseDao.execute(
							"update VerifyApplydetail set vad_price=(select round(price+price*amount/case when total=0 then 1 else total end,8) from (select vad_id,(vad_orderprice*va_rate/(1+vad_taxrate/100)) price,(select sum(vad_qty*vad_orderprice*va_rate*(1+nvl(vad_taxrate,0)/100)) from VERIFYAPPLYDetail pp1 left join VERIFYAPPLY p1 on pp1.vad_vaid=p1.va_id where p1.va_id=VERIFYAPPLYdetail.vad_vaid) total,nvl((select sum(pd_rate*pd_amount) from ProdChargeDetailAN A where A.pd_anid=VERIFYAPPLYdetail.vad_vaid),0) amount from VERIFYAPPLYDetail left join VERIFYAPPLY on vad_vaid=va_id where vad_vaid=?) B where B.vad_id=VERIFYAPPLYdetail.vad_id) where vad_vaid=? and nvl(vad_price,0)=0",
							va_id, va_id);
					baseDao.execute(
							"update VerifyApplydetail set vad_total=round(vad_price*vad_qty,2),vad_ordertotal=round(vad_orderprice*vad_qty,2),vad_plancode=round(vad_orderprice*vad_qty*(select va_rate from VERIFYAPPLY where va_id=vad_vaid),2) where vad_vaid=?",
							va_id);
					baseDao.execute("update VerifyApplydetail set vad_barcode=round(vad_total-nvl(vad_plancode,0),2) where vad_vaid=?",
							va_id);
					baseDao.execute(
							"update VerifyApply set va_total=round((select sum(vad_orderprice*vad_qty) from VERIFYAPPLYdetail where va_id=vad_vaid),2) where va_id=?",
							va_id);
					if (formStore != null && formStore.get("pu_refcode") != null) {
						baseDao.execute("update VerifyApply set va_sendcode='" + formStore.get("pu_refcode") + "' where va_id=?", va_id);
					}
					/**
					 * @author wsy 双单位
					 */
					baseDao.execute(
							"update VerifyApplydetail set vad_purcqty=round(vad_qty/(select pd_qty from purchasedetail where pd_code=vad_pucode and pd_detno=vad_pudetno)*(select case when nvl(pd_purcqty,0)=0 then pd_qty else pd_purcqty end from purchasedetail where pd_code=vad_pucode and pd_detno=vad_pudetno),2) where vad_vaid=?",
							va_id);
					baseDao.execute("update purchasedetail set pd_ypurcqty=nvl(pd_ypurcqty,0)+(select nvl(vad_purcqty,0) from VerifyApplydetail where vad_pucode=pd_code and vad_pudetno=pd_detno and vad_vaid="
							+ va_id + ") where pd_puid=" + pu_id);
					log = "转入成功,收料单号:" + "<a href=\"javascript:openUrl('jsps/scm/purchase/verifyApply.jsp?formCondition=va_idIS" + va_id
							+ "&gridCondition=vad_vaidIS" + va_id + "&whoami=VerifyApply')\">" + key.getCode() + "</a>";
					sb.append(index).append(": ").append(log).append("<hr>");
				}
			}
			// 修改采购单状态
			for (Map<Object, Object> map : maps) {
				int pdid = Integer.parseInt(map.get("pd_id").toString());
				purchaseDao.udpatestatus(pdid);
			}
			handlerService.handler(caller, "turnPordIO", "after", new Object[] { maps, ids });
			return sb.toString();
		}
		return null;
	}

	/**
	 * 批量冻结
	 */
	@Override
	public void vastFreeze(String caller, int[] id) {
		// 整批冻结
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield", "fo_statusfield",
				"fo_statuscodefield" }, "fo_caller='" + caller + "'");
		for (int key : id) {
			// 工单冻结前需判断是否有在制物料
			if (caller.equals("Make!FREEZE!Deal")) {
				// 计算在制
				makeDao.setMMOnlineQTY(String.valueOf(key), null);
				SqlRowList sl0 = baseDao
						.queryForRowSet("select count(1) as c,wm_concat(mm_detno) as detno from makematerial where mm_maid=" + key
								+ " and mm_onlineqty>0");
				if (sl0.next()) {
					if (sl0.getInt("c") > 0) {
						BaseUtil.showErrorOnSuccess("明细行序号:" + sl0.getString("detno") + "有在制物料，不能冻结!");
						continue;
					}
				}
			}
			baseDao.updateByCondition((String) objs[0], objs[3] + "='FREEZE'," + objs[2] + "='" + BaseUtil.getLocalMessage("FREEZE") + "'",
					objs[1] + "=" + key);
		}
		// 记录日志
	}

	/**
	 * 批量抛转
	 */
	@Override
	public void vastPost(String caller, int[] id) {
		// 整批抛转

		// 记录日志
	}

	/**
	 * 批量作废
	 */
	@Override
	public void vastCancel(String caller, int[] id) {
		// 整批作废
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield", "fo_statuscodefield",
				"fo_statusfield" }, "fo_caller='" + caller + "'");
		for (int key : id) {
			baseDao.updateByCondition((String) objs[0], objs[3] + "='NULLIFIED'," + objs[2] + "='" + BaseUtil.getLocalMessage("NULLIFIED")
					+ "'", objs[1] + "=" + key);
		}
		// 记录日志
	}

	/**
	 * 批量反过账
	 */
	@Override
	public void vastResPost(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Object pd_id = null;
		for (Map<Object, Object> map : maps) {
			pd_id = map.get("pd_id");
			SqlRowList rs = baseDao.queryForRowSet(
					"SELECT pd_inoutno From ProdIODetail WHERE (pd_auditstatus='TURNAR' or pd_auditstatus='PARTAR') AND pd_id=?", pd_id);
			if (rs.next()) {
				BaseUtil.showError("单据[" + rs.getString("pd_inoutno") + "]已开票或者部分开票，不允许反过账！");
			} else {
				Object[] objs = baseDao.getFieldsDataByCondition("ProdInOut left join ProdIODetail on pi_id=pd_piid",
						new String[] { "pi_id" }, "pd_id=" + pd_id);
				baseDao.updateByCondition("ProdIODetail", "pd_status='0',pd_auditstatus='ENTERING'", "pd_id=" + pd_id);
				baseDao.updateByCondition("ProdInOut", "pi_statuscode='UNPOST',pi_status='" + BaseUtil.getLocalMessage("UNPOST")
						+ "',pi_invostatuscode='ENTERING',pi_invostatus='" + BaseUtil.getLocalMessage("ENTERING") + "'", "pi_id=" + objs[0]);
			}
		}
	}

	/**
	 * 批量删除
	 */
	@Override
	public void vastDelete(String caller, int[] id) {
		// 整批删除
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield" }, "fo_caller='" + caller + "'");
		for (int key : id) {
			baseDao.deleteById((String) objs[0], (String) objs[1], key);
		}
		// 记录日志
	}

	/**
	 * 批量发出
	 */
	@Override
	public void vastSend(String caller, int[] id) {
		// 整批发出
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield", "fo_statusfield",
				"fo_statuscodefield" }, "fo_caller='" + caller + "'");
		for (int key : id) {
			baseDao.updateByCondition((String) objs[0], objs[3] + "='SENDED'," + objs[2] + "='" + BaseUtil.getLocalMessage("SENDED") + "'",
					objs[1] + "=" + key);
		}
		// 记录日志
	}

	/**
	 * 销售单转出货通知单(订单界面)
	 */
	public String turnSendNotify(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		// 判断本次数量
		saleDao.checkAdYqty(maps);
		String ids = CollectionUtil.pluckSqlString(maps, "sd_id");
		// 存在未审批变更单
		String codes = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(sa_code) from (select distinct sa_code from sale left join saledetail on sa_id=sd_said where sd_id in("
								+ ids
								+ ") and exists (select 1 from SaleChangeDetail left join SaleChange on sc_id=scd_scid where scd_sacode=sa_code and sc_statuscode<>'AUDITED' and (sc_type<>'DELIVERY' and sc_type<>'交期变更')))",
						String.class);
		if (codes != null) {
			BaseUtil.showError("存在待审批的销售变更单，不能进行转出操作!销售单号：" + codes);
		}
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat('订单号：'||sd_code||'订单行号：'||sd_detno) from SaleDetail where nvl(sd_statuscode, ' ')<>'AUDITED' and sd_id in ("
						+ ids + ")", String.class);
		if (dets != null) {
			BaseUtil.showError("明细行状态不等于已审核，不能进行转出操作!" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select distinct wm_concat('订单号：'||sa_code||'客户：'||sa_custcode) from sale left join SaleDetail on sd_said=sa_id where nvl(sa_custcode, ' ')<>' ' and sd_id in ("
								+ ids + ") and sa_custcode in (select cu_code from customer where cu_status='挂起')", String.class);
		if (dets != null) {
			BaseUtil.showError("订单客户已挂起，不能进行转出操作!" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(distinct pd_ordercode) from ProdIODetail where nvl(pd_ordercode, ' ')<>' ' and pd_piclass='出货单' and nvl(pd_snid,0)=0 and nvl(pd_sdid,0)<>0 and pd_ordercode in (select sd_code from saledetail where sd_id in ("
								+ ids + "))", String.class);
		if (dets != null) {
			BaseUtil.showError("销售订单已转过出货单,不能进行转出操作!销售单号：" + dets);
		}
		// 订单类型允许转通知单
		codes = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('销售单'||sa_code||'类型:'||sa_kind) from (select distinct sa_code,sa_kind from sale left join saledetail on sa_id=sd_said left join SaleKind on sk_name=sa_kind where sd_id in("
								+ ids + ") and sk_outtype='TURNOUT')", String.class);
		if (codes != null) {
			BaseUtil.showError("存在只能转出货单的订单类型！" + codes);
		}
		handlerService.handler("Sale", "turnSendNotify", "before", new Object[] { maps });
		// 转入通知单主记录
		Integer sa_id = baseDao.getFieldValue("SaleDetail", "sd_said", "sd_id=" + maps.get(0).get("sd_id"), Integer.class);
		Key key = transferRepository.transfer(caller, sa_id);
		int sn_id = key.getId();
		// 转入明细
		transferRepository.transfer(caller, maps, key);
		Object whcode = maps.get(0).containsKey("sd_whcode") ? maps.get(0).get("sd_whcode") : null;
		if (whcode != null) {
			baseDao.execute("update sendnotifydetail set SND_WAREHOUSECODE='" + whcode + "' where snd_snid=" + sn_id);
			baseDao.execute("update sendnotifydetail set snd_warehouse=(select wh_description from warehouse where SND_WAREHOUSECODE=wh_code) where snd_snid="
					+ sn_id);
		}
		// 修改交期
		Object delivery = maps.get(0).get("sd_delivery");
		if (delivery != null) {
			baseDao.updateByCondition("SendNotify",
					"sn_deliverytime=" + DateUtil.parseDateToOracleString(Constant.YMD, delivery.toString()), "sn_id=" + sn_id);
		} else {
			baseDao.updateByCondition(
					"SendNotify",
					"sn_deliverytime=(select max(sd_delivery) from saledetail left join SendNotifyDetail on sd_id=snd_sdid where snd_snid=sn_id)",
					"sn_id=" + sn_id);
		}
		// 地址
		baseDao.execute(
				"update sendnotify set sn_toplace=(select cu_add1 from customer where sn_custcode=cu_code) where sn_id=? and nvl(sn_toplace,' ')=' '",
				sn_id);
		// 部门
		baseDao.execute(
				"update sendnotify set (sn_departmentcode,sn_departmentname)=(select dp_code,em_depart from employee left join department on EM_DEPARTMENTCODE=dp_code where em_code=sn_sellercode) where sn_id=? and nvl(sn_departmentcode,' ')=' '",
				sn_id);
		// 金额
		baseDao.execute(
				"update SendNotifyDetail set snd_total=round(snd_outqty*snd_sendprice,2),snd_netprice=round(snd_sendprice/(1+snd_taxrate/100),6),snd_taxtotal=round(snd_outqty*snd_sendprice/(1+snd_taxrate/100),2) where snd_snid=?",
				sn_id);
		// 仓库
		baseDao.execute(
				"update SendNotifyDetail set snd_warehouse=(select wh_description from warehouse where wh_code=snd_warehousecode) where snd_snid=? and snd_warehousecode is not null",
				sn_id);
		handlerService.handler("Sale", "turnSendNotify", "after", new Object[] { maps });
		// 修改销售单状态
		for (Map<Object, Object> map : maps) {
			int sdid = Integer.parseInt(map.get("sd_id").toString());
			saleDao.updateturnstatus(sdid);
		}
		autoAudit(sn_id, key.getCode());
		return "转入成功,出货通知单号:" + "<a href=\"javascript:openUrl('jsps/scm/sale/sendNotify.jsp?formCondition=sn_idIS" + sn_id
				+ "&gridCondition=snd_snidIS" + sn_id + "')\">" + key.getCode() + "</a>&nbsp;";
	}

	/**
	 * 出货通知单转出货单(通知单界面)
	 * 
	 * @param type
	 *            {出货单,其它出库单，换货出库单，拨出单}
	 * @author yingp
	 */
	@Override
	public String turnProdOut(String caller, String data, String tocaller) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String ids = CollectionUtil.pluckSqlString(maps, "snd_id");
		// 存在未审批变更单
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(snd_pdno) from sendnotifydetail where snd_id in("
								+ ids
								+ ") and exists (select 1 from SaleChangeDetail left join SaleChange on sc_id=scd_scid where scd_sacode=snd_ordercode and sc_statuscode<>'AUDITED' and (sc_type<>'DELIVERY' and sc_type<>'交期变更'))",
						String.class);
		if (dets != null) {
			BaseUtil.showError("存在待审批的销售变更单，不能进行转出操作!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(snd_pdno) from sendnotifydetail where snd_id in("
								+ ids
								+ ") and exists (select 1 from SendNotifyChange left join SendNotifyChangeDetail on sc_id=scd_scid where sc_sncode=snd_code and scd_snddetno=snd_pdno and sc_statuscode<>'AUDITED')",
						String.class);
		if (dets != null) {
			BaseUtil.showError("存在待审批的通知变更单，不能进行转出操作!行号：" + dets);
		}
		if(baseDao.isDBSetting("Sale", "zeroOutWhenHung")){
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select distinct wm_concat('通知单号：'||sn_code||'客户：'||sn_custcode) from sendnotify left join SendNotifyDetail on snd_snid=sn_id where nvl(sn_custcode, ' ')<>' ' and snd_id in ("
									+ ids + ") and sn_custcode in (select cu_code from customer where cu_status='挂起') "
					+ "and not exists (select 1 from saledetail where sd_code=snd_ordercode and sd_detno=snd_orderdetno and nvl(sd_price,0)=0)", String.class);
			if (dets != null) {
				BaseUtil.showError("通知单客户已挂起，不能进行转出操作!" + dets);
			}
		}else{
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select distinct wm_concat('通知单号：'||sn_code||'客户：'||sn_custcode) from sendnotify left join SendNotifyDetail on snd_snid=sn_id where nvl(sn_custcode, ' ')<>' ' and snd_id in ("
									+ ids + ") and sn_custcode in (select cu_code from customer where cu_status='挂起')", String.class);
			if (dets != null) {
				BaseUtil.showError("通知单客户已挂起，不能进行转出操作!" + dets);
			}
		}		
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(snd_pdno) from SendNotifyDetail where nvl(snd_statuscode, ' ')<>'AUDITED' and snd_id in (" + ids + ")",
				String.class);
		if (dets != null) {
			BaseUtil.showError("明细行状态不等于已审核，不能进行转出操作!" + dets);
		}
		if ("SendNotify!ToProdIN!Deal".equals(caller)) {
			String caller1 = caller.substring(0, 10);
			handlerService.handler(caller1, "turnProdIO", "before", new Object[] { maps });
		}
		String log = null;
		String type = "出货单";
		if ("ProdInOut!OtherOut".equals(tocaller)) {
			type = "其它出库单";
		} else if ("ProdInOut!ExchangeOut".equals(tocaller)) {
			type = "换货出库单";
		} else if ("ProdInOut!AppropriationOut".equals(tocaller)) {
			type = "拨出单";
		}
		Object snid = maps.get(0).get("snd_snid");
		if (snid == null) {
			snid = baseDao.getFieldDataByCondition("SendNotifyDetail", "snd_snid", "snd_id=" + maps.get(0).get("snd_id"));
		}
		// 修改原表单的状态
		if (type.equals("拨出单")) {
			baseDao.updateByCondition("SendNotify", "sn_zbc='已转拨出单'", "sn_id=" + snid);
		}
		// 判断本次数量、状态
		sendNotifyDao.checkAdYqty(maps, type);
		Object[] objs = null;
		StringBuffer sb = new StringBuffer();
		/*
		 * for (Map<Object, Object> map : maps) { int sndid =
		 * Integer.parseInt(map.get("snd_id").toString()); double tqty =
		 * Double.parseDouble(map.get("snd_tqty").toString()); objs =
		 * baseDao.getFieldsDataByCondition
		 * ("SendNotifyDetail left join SendNotify on sn_id=snd_snid", new
		 * String[] { "sn_code", "snd_pdno", "snd_yqty", "snd_outqty" },
		 * "snd_id=" + sndid + " AND nvl(snd_yqty, 0)+" + tqty + ">snd_outqty");
		 * if (objs != null) { sb.append("通知单号:" + objs[0] + ",行号:" + objs[1] +
		 * ",通知单数量:" + objs[3] + ",无法转出.已转" + type + "数量:" + objs[2] + ",本次数量:"
		 * + tqty + "<hr/>");
		 * maps.remove(map);//list集合的高级for循环中不能使用remove方法，会报异常 continue; } }
		 */
		Iterator<Map<Object, Object>> it = maps.iterator();
		while (it.hasNext()) {
			Map<Object, Object> map = it.next();
			int sndid = Integer.parseInt(map.get("snd_id").toString());
			double tqty = Double.parseDouble(map.get("snd_tqty").toString());
			objs = baseDao.getFieldsDataByCondition("SendNotifyDetail left join SendNotify on sn_id=snd_snid", new String[] { "sn_code",
					"snd_pdno", "snd_yqty", "snd_outqty" }, "snd_id=" + sndid + " AND nvl(snd_yqty, 0)+" + tqty + ">snd_outqty");
			if (objs != null) {
				sb.append("通知单号:" + objs[0] + ",行号:" + objs[1] + ",通知单数量:" + objs[3] + ",无法转出.已转" + type + "数量:" + objs[2] + ",本次数量:"
						+ tqty + "<hr/>");
				it.remove();
			}
		}
		if (maps.size() > 0) {
			// 转入出货单主记录
			Integer sn_id = baseDao.getFieldValue("SendNotifyDetail", "snd_snid", "snd_id=" + maps.get(0).get("snd_id"), Integer.class);
			int pi_id = 0;
			if ("出货单".equals(type)) {
				Key key = transferRepository.transfer(tocaller, sn_id);
				pi_id = key.getId();
				// 转入出货单明细
				transferRepository.transfer(tocaller, maps, key);
				log = "出货单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + pi_id
						+ "&gridCondition=pd_piidIS" + pi_id + "&whoami=ProdInOut!Sale')\">" + key.getCode() + "</a>&nbsp;";
			}
			if ("其它出库单".equals(type)) {
				Key key = transferRepository.transfer(tocaller, sn_id);
				pi_id = key.getId();
				// 转入其它出库单明细
				transferRepository.transfer(tocaller, maps, key);
				log = "换货出库单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + pi_id
						+ "&gridCondition=pd_piidIS" + pi_id + "&whoami=ProdInOut!OtherOut')\">" + key.getCode() + "</a>&nbsp;";
			}
			if ("换货出库单".equals(type)) {
				Key key = transferRepository.transfer(tocaller, sn_id);
				pi_id = key.getId();
				// 转入换货出库单明细
				transferRepository.transfer(tocaller, maps, key);
				log = "换货出库单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + pi_id
						+ "&gridCondition=pd_piidIS" + pi_id + "&whoami=ProdInOut!ExchangeOut')\">" + key.getCode() + "</a>&nbsp;";
			}
			if ("拨出单".equals(type)) {
				Key key = transferRepository.transfer(tocaller, sn_id);
				pi_id = key.getId();
				// 转入拨出单明细
				transferRepository.transfer(tocaller, maps, key);
				log = "拨出单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + pi_id
						+ "&gridCondition=pd_piidIS" + pi_id + "&whoami=ProdInOut!AppropriationOut')\">" + key.getCode() + "</a>&nbsp;";

			}
			// 地址
			baseDao.execute(
					"update ProdInOut set pi_address=(select cu_add1 from customer where pi_cardcode=cu_code) where pi_id=? and nvl(pi_address,' ')=' '",
					pi_id);
			baseDao.execute(
					"update ProdIODetail set (pd_whcode,pd_whname)=(select pr_whcode,wh_description from product left join warehouse on pr_whcode=wh_code where pd_prodcode=pr_code) where pd_piid=? and nvl(pd_whcode,' ')=' '",
					pi_id);
			baseDao.execute(
					"update prodinout set (pi_whcode,pi_whname)=(select pd_whcode,pd_whname from prodiodetail where pd_piid=pi_id and nvl(pd_whcode,' ')<>' ' and rownum<2) where nvl(pi_whcode,' ')=' ' and pi_id=?",
					pi_id);
			baseDao.execute("update prodinout set (pi_purposename,pi_expresscode,pi_fax)=(select max(ca_person),max(ca_phone),max(ca_fax) from CustomerAddress left join customer on ca_cuid=cu_id where cu_code=pi_cardcode and ca_address=pi_address)  where pi_id="
					+ pi_id);
			baseDao.execute(
					"update ProdInOut set pi_rate=nvl((select cm_crrate from currencysmonth where cm_yearmonth=to_char(pi_date,'yyyymm') and cm_crname=pi_currency),1) where pi_id=? and nvl(pi_currency,' ')<>' '",
					pi_id);
			baseDao.execute(
					"update ProdInOut set pi_total=(SELECT round(sum(nvl(pd_sendprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0))),2) FROM ProdIODetail WHERE pd_piid=pi_id) where pi_id=?",
					pi_id);
			baseDao.updateByCondition(
					"ProdIODetail",
					"pd_netprice=round(pd_sendprice/(1+pd_taxrate/100),8),pd_nettotal=round(pd_sendprice*pd_outqty/(1+nvl(pd_taxrate,0)/100),2)",
					"pd_piid=" + pi_id);
			baseDao.execute(
					"update ProdIODetail set pd_taxtotal=round(pd_sendprice*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2), pd_nettotal=round(pd_netprice*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2) WHERE pd_piid=?",
					pi_id);
			baseDao.updateByCondition("ProdInOut", "pi_totalupper=L2U(nvl(pi_total,0))", "pi_id=" + pi_id);
		}
		// 修改出货通知单状态
		for (Map<Object, Object> map : maps) {
			int sndid = Integer.parseInt(map.get("snd_id").toString());
			sendNotifyDao.checkSNDQty(sndid, null);
		}
		handlerService.handler(caller, "turnProdIO", "after", new Object[] { maps });
		if (log == null) {
			return "没有需要转出的数据";
		}
		return "转入成功<hr>" + log;
	}

	/**
	 * 出货通知单转出货单（批量界面）
	 */
	@Override
	public String vastTurnProdIN(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		if (maps.size() > 0) {
			StringBuffer sb = new StringBuffer();
			int index = 0;
			// 判断本次数量
			sendNotifyDao.checkAdYqty(maps, "出货单");
			String ids = CollectionUtil.pluckSqlString(maps, "snd_id");
			// 存在未审批变更单
			String codes = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select WM_CONCAT(sn_code) from (select distinct sn_code from SendNotify left join SendNotifydetail on sn_id=snd_snid where snd_id in("
									+ ids
									+ ") and exists (select 1 from SendNotifyChangeDetail left join SendNotifyChange on sc_id=scd_scid where sc_sncode=sn_code and scd_snddetno=snd_pdno and sc_statuscode<>'AUDITED'))",
							String.class);
			if (codes != null) {
				BaseUtil.showError("存在待审批的通知单变更单，不能进行转出操作!通知单号：" + codes);
			}
			codes = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select WM_CONCAT(snd_ordercode) from (select distinct snd_ordercode from SendNotifydetail where snd_id in("
									+ ids
									+ ") and exists (select 1 from SaleChangeDetail left join SaleChange on sc_id=scd_scid where scd_sacode=snd_ordercode and scd_sddetno=snd_orderdetno and sc_statuscode<>'AUDITED' and (sc_type<>'DELIVERY' and sc_type<>'交期变更')))",
							String.class);
			if (codes != null) {
				BaseUtil.showError("存在待审批的销售变更单，不能进行转出操作!通知单号：" + codes);
			}
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat('通知单号：'||snd_code||'行号：'||snd_pdno) from SendNotifyDetail where nvl(snd_statuscode, ' ')<>'AUDITED' and snd_id in ("
							+ ids + ")", String.class);
			if (dets != null) {
				BaseUtil.showError("明细行状态不等于已审核，不能进行转出操作!" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select distinct wm_concat('通知单号：'||sn_code||'客户：'||sn_custcode) from sendnotify left join SendNotifyDetail on snd_snid=sn_id where nvl(sn_custcode, ' ')<>' ' and snd_id in ("
									+ ids + ") and sn_custcode in (select cu_code from customer where cu_status='挂起')", String.class);
			if (dets != null) {
				BaseUtil.showError("通知单客户已挂起，不能进行转出操作!" + dets);
			}
			handlerService.handler(caller, "turnProdIO", "before", new Object[] { maps });
			if (baseDao.isDBSetting("CopCheck")) {
				SqlRowList rs = baseDao
						.queryForRowSet("select  count(1) n from (select distinct sn_cop from sendnotify,sendnotifydetail where sn_id=snd_snid and snd_id in ("
								+ ids + ") )");
				if (rs.next()) {
					if (rs.getInt("n") > 1) {
						BaseUtil.showError("所属公司不一致的出货通知单不允许合并下达到一张出货单中!");
					}
				}
			}
			if (baseDao.isDBSetting(caller, "allowSeller")) {
				SqlRowList rs = baseDao
						.queryForRowSet("select  count(1) n from (select distinct sn_sellercode from sendnotify,sendnotifydetail where sn_id=snd_snid and snd_id in ("
								+ ids + ") )");
				if (rs.next()) {
					if (rs.getInt("n") > 1) {
						BaseUtil.showError("业务员不一致的出货通知单不允许合并下达到一张出货单中!");
					}
				}
			}
			Object code = maps.get(0).get("pi_inoutno");
			String log = "";
			// 指定了出货单
			if (StringUtil.hasText(code)) {
				SqlRowList rs = baseDao
						.queryForRowSet(
								"select pi_id,pi_invostatuscode,pi_statuscode,pi_cop,pi_cardcode,pi_receivecode,pi_arcode,pi_currency,pi_paymentcode from ProdInOut where pi_inoutno=? and pi_class='出货单'",
								code);
				if (rs.next()) {
					// 判断指定的出货通知单状态是否[在录入]
					if (!"ENTERING".equals(rs.getString(2))) {
						BaseUtil.showError("只能指定[在录入]的出货单!");
					}
					if (!"UNPOST".equals(rs.getString(3))) {
						BaseUtil.showError("只能指定[未过账]的出货单!");
					}
					// 客户、收货客户、应收客户、币别、收款方式不同，限制转单
					StringBuffer errBuffer = new StringBuffer();
					String errSn = baseDao.getJdbcTemplate().queryForObject(
							"select wm_concat(sn_code) from sendnotify where sn_id in (select snd_snid from sendnotifydetail where snd_id in ("
									+ ids + ")) and sn_custcode<>?", String.class, rs.getString("pi_cardcode"));
					if (errSn != null)
						errBuffer.append("您选择的通知单的客户，与指定的出货单的客户不一致！通知单：<br>" + errSn.replace(",", "<br>")).append("<hr>");
					errSn = baseDao.getJdbcTemplate().queryForObject(
							"select wm_concat(sn_code) from sendnotify where sn_id in (select snd_snid from sendnotifydetail where snd_id in ("
									+ ids + ")) and sn_shcustcode<>?", String.class, rs.getString("pi_receivecode"));
					if (errSn != null)
						errBuffer.append("您选择的通知单的收货客户，与指定的出货单的收货客户不一致！通知单：<br>" + errSn.replace(",", "<br>")).append("<hr>");
					errSn = baseDao.getJdbcTemplate().queryForObject(
							"select wm_concat(sn_code) from sendnotify where sn_id in (select snd_snid from sendnotifydetail where snd_id in ("
									+ ids + ")) and sn_arcustcode<>?", String.class, rs.getString("pi_arcode"));
					if (errSn != null)
						errBuffer.append("您选择的通知单的应收客户，与指定的出货单的应收客户不一致！通知单：<br>" + errSn.replace(",", "<br>")).append("<hr>");
					errSn = baseDao.getJdbcTemplate().queryForObject(
							"select wm_concat(sn_code) from sendnotify where sn_id in (select snd_snid from sendnotifydetail where snd_id in ("
									+ ids + ")) and sn_currency<>?", String.class, rs.getString("pi_currency"));
					if (errSn != null)
						errBuffer.append("您选择的通知单的币别，与指定的出货单的币别不一致！通知单：<br>" + errSn.replace(",", "<br>")).append("<hr>");
					errSn = baseDao.getJdbcTemplate().queryForObject(
							"select wm_concat(sn_code) from sendnotify where sn_id in (select snd_snid from sendnotifydetail where snd_id in ("
									+ ids + ")) and sn_paymentscode<>?", String.class, rs.getString("pi_paymentcode"));
					if (errSn != null)
						errBuffer.append("您选择的通知单的收款方式，与指定的出货单的收款方式不一致！通知单：<br>" + errSn.replace(",", "<br>")).append("<hr>");
					int pi_id = rs.getInt(1);
					if (baseDao.isDBSetting("CopCheck")) {
						errSn = baseDao.getJdbcTemplate().queryForObject(
								"select wm_concat(sn_code) from sendnotify where sn_id in (select snd_snid from sendnotifydetail where snd_id in ("
										+ ids + ")) and sn_cop<>?", String.class, rs.getString("pi_cop"));
						if (errSn != null)
							errBuffer.append("您选择的通知单的所属公司，与指定的出货单的所属公司不一致！通知单：<br>" + errSn.replace(",", "<hr>"));
					}
					if (errBuffer.length() > 0)
						BaseUtil.showError(errBuffer.toString());
					// 转入明细
					transferRepository.transfer("ProdInOut!Sale", maps, new Key(pi_id, code.toString()));
					// 金额
					baseDao.execute(
							"update ProdIODetail set (pd_whcode,pd_whname)=(select pr_whcode,wh_description from product left join warehouse on pr_whcode=wh_code where pd_prodcode=pr_code) where pd_piid=? and nvl(pd_whcode,' ')=' '",
							pi_id);
					baseDao.execute(
							"update prodinout set (pi_whcode,pi_whname)=(select pd_whcode,pd_whname from prodiodetail where pd_piid=pi_id and nvl(pd_whcode,' ')<>' ' and rownum<2) where nvl(pi_whcode,' ')=' ' and pi_id=?",
							pi_id);
					baseDao.execute("update prodinout set (pi_purposename,pi_expresscode,pi_fax)=(select max(ca_person),max(ca_phone),max(ca_fax) from CustomerAddress left join customer on ca_cuid=cu_id where cu_code=pi_cardcode and ca_address=pi_address)  where pi_id="
							+ pi_id);
					baseDao.execute(
							"update ProdIODetail set pd_taxtotal=round(pd_sendprice*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2), pd_nettotal=round(pd_netprice*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2) WHERE pd_piid=?",
							pi_id);
					baseDao.execute("update ProdIODetail set pd_ordertotal=round(pd_outqty*pd_sendprice,2) where pd_piid=?", pi_id);
					baseDao.updateByCondition(
							"ProdIODetail",
							"pd_netprice=round(pd_sendprice/(1+pd_taxrate/100),8),pd_nettotal=round(pd_sendprice*pd_outqty/(1+nvl(pd_taxrate,0)/100),2)",
							"pd_piid=" + pi_id);
					baseDao.execute(
							"update ProdInOut set pi_total=(SELECT round(sum(nvl(pd_sendprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0))),2) FROM ProdIODetail WHERE pd_piid=pi_id) where pi_id=?",
							pi_id);
					baseDao.updateByCondition("ProdInOut", "pi_totalupper=L2U(nvl(pi_total,0))", "pi_id=" + pi_id);
					log = "出货单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + pi_id
							+ "&gridCondition=pd_piidIS" + pi_id + "&whoami=ProdInOut!Sale')\">" + code + "</a>&nbsp;";
					sb.append(log).append("<hr>");
				} else {
					BaseUtil.showError("指定出货单号不存在!");
				}
			} else {// 未指定出货通知单
				Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(maps, new Object[] { "sn_custcode", "sn_shcustcode",
						"sn_currency", "sn_arcustcode", "sn_paymentscode" });
				// 按客户分组的转入操作
				Set<Object> mapSet = groups.keySet();
				List<Map<Object, Object>> items;
				for (Object s : mapSet) {
					items = groups.get(s);
					// 转入通知单主记录
					Integer sn_id = baseDao.getFieldValue("SendNotifyDetail", "snd_snid", "snd_id=" + items.get(0).get("snd_id"),
							Integer.class);
					Key key = transferRepository.transfer("ProdInOut!Sale", sn_id);
					if (key != null) {
						int pi_id = key.getId();
						index++;
						// 转入明细
						transferRepository.transfer("ProdInOut!Sale", items, key);
						// 地址
						baseDao.execute(
								"update ProdInOut set pi_address=(select cu_add1 from customer where pi_cardcode=cu_code) where pi_id=? and nvl(pi_address,' ')=' '",
								pi_id);
						baseDao.execute("update prodinout set (pi_purposename,pi_expresscode,pi_fax)=(select max(ca_person),max(ca_phone),max(ca_fax) from CustomerAddress left join customer on ca_cuid=cu_id where cu_code=pi_cardcode and ca_address=pi_address)  where pi_id="
								+ pi_id);
						baseDao.execute(
								"update ProdIODetail set pd_taxtotal=round(pd_sendprice*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2), pd_nettotal=round(pd_netprice*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2) WHERE pd_piid=?",
								pi_id);
						baseDao.execute("update ProdIODetail set pd_ordertotal=round(pd_outqty*pd_sendprice,2) where pd_piid=?", pi_id);
						baseDao.updateByCondition(
								"ProdIODetail",
								"pd_netprice=round(pd_sendprice/(1+pd_taxrate/100),8),pd_nettotal=round(pd_sendprice*pd_outqty/(1+nvl(pd_taxrate,0)/100),2)",
								"pd_piid=" + pi_id);
						baseDao.execute(
								"update ProdInOut set pi_total=(SELECT round(sum(nvl(pd_sendprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0))),2) FROM ProdIODetail WHERE pd_piid=pi_id) where pi_id=?",
								pi_id);
						baseDao.updateByCondition("ProdInOut", "pi_totalupper=L2U(nvl(pi_total,0))", "pi_id=" + pi_id);
						log = "出货单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + pi_id
								+ "&gridCondition=pd_piidIS" + pi_id + "&whoami=ProdInOut!Sale')\">" + key.getCode() + "</a>&nbsp;";
						sb.append(index).append(": ").append(log).append("<hr>");
					}
				}
			}
			// 修改出货通知单状态
			for (Map<Object, Object> map : maps) {
				int sndid = Integer.parseInt(map.get("snd_id").toString());
				sendNotifyDao.checkSNDQty(sndid, null);
			}
			return sb.toString();
		}
		return null;
	}

	/**
	 * 请购单整批结案
	 */
	@Override
	public void vastEndApplication(String caller, int[] id) {

	}

	/**
	 * 销售订单转出货单(批量界面)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public String vastTurnProdIN2(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		// 修改销售单出库状态
		for (Map<Object, Object> map : maps) {
			int sdid = Integer.parseInt(map.get("sd_id").toString());
			baseDao.execute("Insert into tt_sale_sdid (SD_ID) values ("+sdid+")");
		}
		if (maps.size() > 0) {
			saleDao.checkYqty(maps, "出货单");
			StringBuffer sb = new StringBuffer();
			//String ids = CollectionUtil.pluckSqlString(maps, "sd_id");
			String ids = "select sd_id from tt_sale_sdid";
			// sa_ordertypenvl(sa_ordertype,' ')<>'B2C'
			String codes = baseDao.getJdbcTemplate().queryForObject(
					"select WM_CONCAT(sa_code) from (select distinct sa_code from sale left join saledetail on sa_id=sd_said where sd_id in("
							+ ids + ") and nvl(sa_ordertype,' ')='B2C')", String.class);
			if (codes != null) {
				BaseUtil.showError("通过优软商城自动生成的销售订单只能通过'转商城发货'进行出货操作!销售单号：" + codes);
			}
			codes = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select WM_CONCAT(sa_code) from (select distinct sa_code from sale left join saledetail on sa_id=sd_said where sd_id in("
									+ ids
									+ ") and sa_code in (select distinct scd_sacode from SaleChangeDetail left join SaleChange on sc_id=scd_scid where sc_statuscode<>'AUDITED'))",
							String.class);
			if (codes != null) {
				BaseUtil.showError("当有在待审批的销售变更单，不能进行转出操作!销售单号：" + codes);
			}			
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat('订单号：'||sd_code||'订单行号：'||sd_detno) from SaleDetail where nvl(sd_statuscode, ' ')<>'AUDITED' and sd_id in ("
							+ ids + ")", String.class);
			if (dets != null) {
				BaseUtil.showError("明细行状态不等于已审核，不能进行转出操作!" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select WM_CONCAT(distinct snd_ordercode) from SendNotifyDetail where nvl(snd_ordercode, ' ')<>' ' and snd_ordercode in (select sd_code from saledetail where sd_id in ("
									+ ids + "))", String.class);
			if (dets != null) {
				BaseUtil.showError("销售订单已转过通知单,不能进行转出操作!销售单号：" + dets);
			}
			if(baseDao.isDBSetting("Sale", "zeroOutWhenHung")){
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select distinct wm_concat('订单号：'||sa_code||'客户：'||sa_custcode) from sale left join SaleDetail on sd_said=sa_id where nvl(sa_custcode, ' ')<>' ' and sd_id in ("
										+ ids + ") and sa_custcode in (select cu_code from customer where cu_status='挂起') and nvl(sd_price,0)<>0", String.class);
				if (dets != null) {
					BaseUtil.showError("订单客户已挂起，且销售订单单价不为0,不能进行转出操作!" + dets);
				}		
			}else{
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select distinct wm_concat('订单号：'||sa_code||'客户：'||sa_custcode) from sale left join SaleDetail on sd_said=sa_id where nvl(sa_custcode, ' ')<>' ' and sd_id in ("
										+ ids + ") and sa_custcode in (select cu_code from customer where cu_status='挂起')", String.class);
				if (dets != null) {
					BaseUtil.showError("订单客户已挂起，不能进行转出操作!" + dets);
				}	
			}			
			if (baseDao.isDBSetting("CopCheck")) {
				SqlRowList rs = baseDao
						.queryForRowSet("select  count(1) n from (select distinct sa_cop from sale,saledetail where sa_id=sd_said and sd_id in ("
								+ ids + ") )");
				if (rs.next()) {
					if (rs.getInt("n") > 1) {
						BaseUtil.showError("所属公司不一致的出货通知单不允许合并下达到一张出货单中");
					}
				}
			}
			//欣康宁 客户po不同不能做到同一个出货单上
			if (baseDao.isDBSetting("Sale" ,"PoCheck")) {
				SqlRowList rs = baseDao
						.queryForRowSet("select  count(1) n from (select distinct sa_code,sa_pocode from sale,saledetail where sa_id=sd_said and sd_id in ("
								+ ids + ") )");
				if (rs.next()) {
					if (rs.getInt("n") > 1) {
						BaseUtil.showError("所属客户PO号不一致的销售订单不允许合并下达到一张出货单中");
					}
				}
			}
			Object code = maps.get(0).get("pi_inoutno");
			Object whcode = maps.get(0).containsKey("wh_code") ? maps.get(0).get("wh_code") : null;
			String log = "";
			handlerService.handler(caller, "TurnProdIN2", "before", new Object[] { maps });
			// 指定了出货单
			if (StringUtil.hasText(code)) {
				SqlRowList rs = baseDao
						.queryForRowSet(
								"select pi_id,pi_invostatuscode,pi_statuscode,pi_cop,pi_cardcode,pi_receivecode,pi_arcode,pi_currency,pi_paymentcode from ProdInOut where pi_inoutno=? and pi_class='出货单'",
								code);
				if (rs.next()) {
					// 判断指定的出货通知单状态是否[在录入]
					if (!"ENTERING".equals(rs.getString(2))) {
						BaseUtil.showError("只能指定[在录入]的出货单!");
					}
					if (!"UNPOST".equals(rs.getString(3))) {
						BaseUtil.showError("只能指定[未过账]的出货单!");
					}
					// 客户、收货客户、应收客户、币别、收款方式不同，限制转单
					StringBuffer errBuffer = new StringBuffer();
					String errSn = baseDao.getJdbcTemplate().queryForObject(
							"select wm_concat(sa_code) from sale where sa_id in (select sd_said from saledetail where sd_id in (" + ids
									+ ")) and sa_custcode<>?", String.class, rs.getString("pi_cardcode"));
					if (errSn != null)
						errBuffer.append("您选择的订单的客户，与指定的出货单的客户不一致！订单：<br>" + errSn.replace(",", "<br>")).append("<hr>");
					errSn = baseDao.getJdbcTemplate().queryForObject(
							"select wm_concat(sa_code) from sale where sa_id in (select sd_said from saledetail where sd_id in (" + ids
									+ ")) and sa_shcustcode<>?", String.class, rs.getString("pi_receivecode"));
					if (errSn != null)
						errBuffer.append("您选择的订单的收货客户，与指定的出货单的收货客户不一致！订单：<br>" + errSn.replace(",", "<br>")).append("<hr>");
					errSn = baseDao.getJdbcTemplate().queryForObject(
							"select wm_concat(sa_code) from sale where sa_id in (select sd_said from saledetail where sd_id in (" + ids
									+ ")) and sa_apcustcode<>?", String.class, rs.getString("pi_arcode"));
					if (errSn != null)
						errBuffer.append("您选择的订单的应收客户，与指定的出货单的应收客户不一致！订单：<br>" + errSn.replace(",", "<br>")).append("<hr>");
					errSn = baseDao.getJdbcTemplate().queryForObject(
							"select wm_concat(sa_code) from sale where sa_id in (select sd_said from saledetail where sd_id in (" + ids
									+ ")) and sa_currency<>?", String.class, rs.getString("pi_currency"));
					if (errSn != null)
						errBuffer.append("您选择的订单的币别，与指定的出货单的币别不一致！订单：<br>" + errSn.replace(",", "<br>")).append("<hr>");
					errSn = baseDao.getJdbcTemplate().queryForObject(
							"select wm_concat(sa_code) from sale where sa_id in (select sd_said from saledetail where sd_id in (" + ids
									+ ")) and sa_paymentscode<>?", String.class, rs.getString("pi_paymentcode"));
					if (errSn != null)
						errBuffer.append("您选择的订单的收款方式，与指定的出货单的收款方式不一致！订单：<br>" + errSn.replace(",", "<br>")).append("<hr>");
					int pi_id = rs.getInt(1);
					if (baseDao.isDBSetting("CopCheck")) {
						errSn = baseDao.getJdbcTemplate().queryForObject(
								"select wm_concat(sa_code) from sale where sn_id in (select sd_said from saledetail where sd_id in (" + ids
										+ ")) and sa_cop<>?", String.class, rs.getString("pi_cop"));
						if (errSn != null)
							errBuffer.append("您选择的订单的所属公司，与指定的出货单的所属公司不一致！订单：<br>" + errSn.replace(",", "<hr>"));
					}
					if (errBuffer.length() > 0)
						BaseUtil.showError(errBuffer.toString());
					// 转入明细
					transferRepository.transfer(caller, maps, new Key(pi_id, code.toString()));
					baseDao.execute(
							"update ProdIODetail set (pd_whcode,pd_whname)=(select pr_whcode,wh_description from product left join warehouse on pr_whcode=wh_code where pd_prodcode=pr_code) where pd_piid=? and nvl(pd_whcode,' ')=' '",
							pi_id);
					baseDao.execute("update prodinout set pi_purposename=((select max(ca_person) from CustomerAddress left join customer on ca_cuid=cu_id where cu_code=pi_cardcode and ca_address=pi_address))  where pi_purposename is null  and  pi_id="
							+ pi_id);
					baseDao.execute("update prodinout set pi_expresscode=((select max(ca_phone) from CustomerAddress left join customer on ca_cuid=cu_id where cu_code=pi_cardcode and ca_address=pi_address))  where pi_expresscode is null  and  pi_id="
							+ pi_id);
					baseDao.execute("update prodinout set pi_fax=((select max(ca_fax) from CustomerAddress left join customer on ca_cuid=cu_id where cu_code=pi_cardcode and ca_address=pi_address))  where pi_fax is null  and  pi_id="
							+ pi_id);
					baseDao.execute(
							"update ProdIODetail set pd_netprice=ROUND(pd_sendprice/(1 + pd_taxrate/ 100),6), pd_taxtotal=round(pd_sendprice*pd_outqty,2), pd_ordertotal=round(pd_outqty*pd_sendprice,2) where pd_piid=?",
							pi_id);
					baseDao.execute("update ProdIODetail set pd_nettotal=round(pd_outqty*pd_netprice,2) where pd_piid=?", pi_id);
					log = "出货单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + pi_id
							+ "&gridCondition=pd_piidIS" + pi_id + "&whoami=ProdInOut!Sale')\">" + code + "</a>&nbsp;";
					sb.append(log).append("<hr>");
				} else {
					/**
					 * 指定的出货单不存在时，按照未指定出货单逻辑转单 将指定的单号赋给生成的第一张出货单
					 * 
					 * @wusy
					 */
					int index = 0;
					Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(maps, new Object[] { "sa_custcode", "sa_shcustcode",
							"sa_currency", "sa_apcustcode", "sa_paymentscode" });
					// 按客户分组的转入操作
					Set<Object> mapSet = groups.keySet();
					List<Map<Object, Object>> items;
					for (Object s : mapSet) {
						items = groups.get(s);
						// 转入通知单主记录
						Integer sa_id = baseDao.getFieldValue("SaleDetail", "sd_said", "sd_id=" + items.get(0).get("sd_id"), Integer.class);
						Key key = transferRepository.transfer(caller, sa_id);
						if (key != null) {
							int pi_id = key.getId();
							String pi_inoutno = key.getCode();
							index++;
							// 转入明细
							transferRepository.transfer(caller, items, key);
							if (index == 1) {
								pi_inoutno = code.toString().trim();
								baseDao.execute("update prodinout set pi_inoutno='" + pi_inoutno + "' where pi_id=" + pi_id + "");
								baseDao.execute("update ProdIODetail set pd_inoutno='" + pi_inoutno + "' where pd_piid=" + pi_id + "");
							}
							baseDao.execute(
									"update ProdIODetail set (pd_whcode,pd_whname)=(select pr_whcode,wh_description from product left join warehouse on pr_whcode=wh_code where pd_prodcode=pr_code) where pd_piid=? and nvl(pd_whcode,' ')=' '",
									pi_id);
							baseDao.execute("update Prodinout set (pi_whcode,pi_whname)=(select pd_whcode,pd_whname from ProdIODetail where pd_piid="
									+ pi_id + " and pd_pdno=1) where pi_id=" + pi_id + "");
							if (whcode != null && !"".equals(whcode)) {
								baseDao.execute("update ProdIODetail set pd_whcode='" + whcode + "' where pd_piid=?", pi_id);
								baseDao.execute(
										"update ProdIODetail set pd_whname=(select wh_description from warehouse where pd_whcode=wh_code) where pd_piid=?",
										pi_id);
								baseDao.execute("update Prodinout set pi_whcode='" + whcode + "' where pi_id=?", pi_id);
								baseDao.execute(
										"update Prodinout set pi_whname=(select wh_description from warehouse where pi_whcode=wh_code) where pi_id=?",
										pi_id);
							}
							// 地址
							baseDao.execute(
									"update ProdInOut set pi_address=(select cu_add1 from customer where pi_cardcode=cu_code) where pi_id=? and nvl(pi_address,' ')=' '",
									pi_id);
							baseDao.execute("update prodinout set pi_purposename=((select max(ca_person) from CustomerAddress left join customer on ca_cuid=cu_id where cu_code=pi_cardcode and ca_address=pi_address))  where pi_purposename is null  and  pi_id="
									+ pi_id);
							baseDao.execute("update prodinout set pi_expresscode=((select max(ca_phone) from CustomerAddress left join customer on ca_cuid=cu_id where cu_code=pi_cardcode and ca_address=pi_address))  where pi_expresscode is null  and  pi_id="
									+ pi_id);
							baseDao.execute("update prodinout set pi_fax=((select max(ca_fax) from CustomerAddress left join customer on ca_cuid=cu_id where cu_code=pi_cardcode and ca_address=pi_address))  where pi_fax is null  and  pi_id="
									+ pi_id);
							baseDao.execute(
									"update ProdIODetail set pd_netprice=ROUND(pd_sendprice/(1 + pd_taxrate/ 100),6), pd_taxtotal=round(pd_sendprice*pd_outqty/(1+pd_taxrate/100),2), pd_ordertotal=round(pd_outqty*pd_sendprice,2) where pd_piid=?",
									pi_id);
							baseDao.execute("update ProdIODetail set pd_nettotal=round(pd_outqty*pd_netprice,2) where pd_piid=?", pi_id);
							log = "转入成功,出货单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
									+ pi_id + "&gridCondition=pd_piidIS" + pi_id + "&whoami=ProdInOut!Sale')\">" + pi_inoutno
									+ "</a>&nbsp;";
							sb.append(index).append(": ").append(log).append("<hr>");
							handlerService.handler(caller, "turn", "after", new Object[] { pi_id });
						}
					}
					/* BaseUtil.showError("指定出货单号不存在!"); */
				}
			} else {// 未指定出货通知单
				int index = 0;
				Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(maps, new Object[] { "sa_custcode", "sa_shcustcode",
						"sa_currency", "sa_apcustcode", "sa_paymentscode" });
				// 按客户分组的转入操作
				Set<Object> mapSet = groups.keySet();
				List<Map<Object, Object>> items;
				for (Object s : mapSet) {
					items = groups.get(s);
					// 转入通知单主记录
					Integer sa_id = baseDao.getFieldValue("SaleDetail", "sd_said", "sd_id=" + items.get(0).get("sd_id"), Integer.class);
					Key key = transferRepository.transfer(caller, sa_id);
					if (key != null) {
						int pi_id = key.getId();
						index++;
						// 转入明细
						transferRepository.transfer(caller, items, key);
						baseDao.execute(
								"update ProdIODetail set (pd_whcode,pd_whname)=(select pr_whcode,wh_description from product left join warehouse on pr_whcode=wh_code where pd_prodcode=pr_code) where pd_piid=? and nvl(pd_whcode,' ')=' '",
								pi_id);
						baseDao.execute("update Prodinout set (pi_whcode,pi_whname)=(select pd_whcode,pd_whname from ProdIODetail where pd_piid="
								+ pi_id + " and pd_pdno=1) where pi_id=" + pi_id + "");
						if (whcode != null && !"".equals(whcode)) {
							baseDao.execute("update ProdIODetail set pd_whcode='" + whcode + "' where pd_piid=?", pi_id);
							baseDao.execute(
									"update ProdIODetail set pd_whname=(select wh_description from warehouse where pd_whcode=wh_code) where pd_piid=?",
									pi_id);
							baseDao.execute("update Prodinout set pi_whcode='" + whcode + "' where pi_id=?", pi_id);
							baseDao.execute(
									"update Prodinout set pi_whname=(select wh_description from warehouse where pi_whcode=wh_code) where pi_id=?",
									pi_id);
						}
						// 地址
						baseDao.execute(
								"update ProdInOut set pi_address=(select cu_add1 from customer where pi_cardcode=cu_code) where pi_id=? and nvl(pi_address,' ')=' '",
								pi_id);
						baseDao.execute("update prodinout set pi_purposename=((select max(ca_person) from CustomerAddress left join customer on ca_cuid=cu_id where cu_code=pi_cardcode and ca_address=pi_address))  where pi_purposename is null  and  pi_id="
								+ pi_id);
						baseDao.execute("update prodinout set pi_expresscode=((select max(ca_phone) from CustomerAddress left join customer on ca_cuid=cu_id where cu_code=pi_cardcode and ca_address=pi_address))  where pi_expresscode is null  and  pi_id="
								+ pi_id);
						baseDao.execute("update prodinout set pi_fax=((select max(ca_fax) from CustomerAddress left join customer on ca_cuid=cu_id where cu_code=pi_cardcode and ca_address=pi_address))  where pi_fax is null  and  pi_id="
								+ pi_id);
						baseDao.execute(
								"update ProdIODetail set pd_netprice=ROUND(pd_sendprice/(1 + pd_taxrate/ 100),6), pd_taxtotal=round(pd_sendprice*pd_outqty/(1+pd_taxrate/100),2), pd_ordertotal=round(pd_outqty*pd_sendprice,2) where pd_piid=?",
								pi_id);
						baseDao.execute("update ProdIODetail set pd_nettotal=round(pd_outqty*pd_netprice,2) where pd_piid=?", pi_id);
						log = "转入成功,出货单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + pi_id
								+ "&gridCondition=pd_piidIS" + pi_id + "&whoami=ProdInOut!Sale')\">" + key.getCode() + "</a>&nbsp;";
						sb.append(index).append(": ").append(log).append("<hr>");
						handlerService.handler(caller, "turn", "after", new Object[] { pi_id });
					}
				}
			}
			// 修改销售单出库状态
			for (Map<Object, Object> map : maps) {
				int sdid = Integer.parseInt(map.get("sd_id").toString());
				saleDao.udpatestatus(sdid);
			}
			return sb.toString();
		}
		return null;
	}

	/**
	 * 销售单转出货通知单(批量界面)
	 */
	@Override
	@Transactional
	public String vastTurnSendNotify(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		if (maps.size() > 0) {
			// 判断本次数量
			saleDao.checkAdYqty(maps);
			String ids = CollectionUtil.pluckSqlString(maps, "sd_id");
			// 存在未审批变更单
			String codes = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select WM_CONCAT(sa_code) from (select distinct sa_code from sale left join saledetail on sa_id=sd_said where sd_id in("
									+ ids
									+ ") and exists (select 1 from SaleChangeDetail left join SaleChange on sc_id=scd_scid where scd_sacode=sa_code and sc_statuscode<>'AUDITED' and (sc_type<>'DELIVERY' and sc_type<>'交期变更')))",
							String.class);
			if (codes != null) {
				BaseUtil.showError("存在待审批的销售变更单，不能进行转出操作!销售单号：" + codes);
			}
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat('订单号：'||sd_code||'订单行号：'||sd_detno) from SaleDetail where nvl(sd_statuscode, ' ')<>'AUDITED' and sd_id in ("
							+ ids + ")", String.class);
			if (dets != null) {
				BaseUtil.showError("明细行状态不等于已审核，不能进行转出操作!" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select WM_CONCAT(distinct pd_ordercode) from ProdIODetail where nvl(pd_ordercode, ' ')<>' ' and pd_piclass='出货单' and nvl(pd_snid,0)=0 and nvl(pd_sdid,0)<>0 and pd_ordercode in (select sd_code from saledetail where sd_id in ("
									+ ids + "))", String.class);
			if (dets != null) {
				BaseUtil.showError("销售订单已转过出货单,不能进行转出操作!销售单号：" + dets);
			}
			if(baseDao.isDBSetting("Sale", "zeroOutWhenHung")){
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select distinct wm_concat('订单号：'||sa_code||'客户：'||sa_custcode) from sale left join SaleDetail on sd_said=sa_id where nvl(sa_custcode, ' ')<>' ' and sd_id in ("
										+ ids + ") and sa_custcode in (select cu_code from customer where cu_status='挂起') and nvl(sd_price,0)<>0", String.class);
				if (dets != null) {
					BaseUtil.showError("订单客户已挂起，且订单单价不为0,不能进行转出操作!" + dets);
				}		
			}else{
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select distinct wm_concat('订单号：'||sa_code||'客户：'||sa_custcode) from sale left join SaleDetail on sd_said=sa_id where nvl(sa_custcode, ' ')<>' ' and sd_id in ("
										+ ids + ") and sa_custcode in (select cu_code from customer where cu_status='挂起')", String.class);
				if (dets != null) {
					BaseUtil.showError("订单客户已挂起，不能进行转出操作!" + dets);
				}
			}
			// 订单类型允许转通知单
			codes = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select WM_CONCAT('销售单'||sa_code||'类型:'||sa_kind) from (select distinct sa_code,sa_kind from sale left join saledetail on sa_id=sd_said left join SaleKind on sk_name=sa_kind where sd_id in("
									+ ids + ") and sk_outtype='TURNOUT')", String.class);
			if (codes != null) {
				BaseUtil.showError("存在只能转出货单的订单类型！" + codes);
			}
			if (baseDao.isDBSetting("CopCheck")) {
				SqlRowList rs = baseDao
						.queryForRowSet("select  count(1) n from (select distinct sa_cop from sale,saledetail where sa_id=sd_said and sd_id in ("
								+ ids + ") )");
				if (rs.next()) {
					if (rs.getInt("n") > 1) {
						BaseUtil.showError("所属公司不一致的销售订单不允许合并下达到一张出货通知单中!");
					}
				}
			}
			handlerService.handler(caller, "turnSendNotify", "before", new Object[] { maps });
			Object code = maps.get(0).get("sn_code");
			int index = 0;
			StringBuffer sb = new StringBuffer();
			String log = null;
			// 指定了出货通知单
			if (StringUtil.hasText(code)) {
				SqlRowList rs = baseDao
						.queryForRowSet(
								"select sn_id,sn_statuscode,sn_cop,sn_custcode,sn_shcustcode,sn_currency,sn_paymentscode,sn_arcustcode from SendNotify where sn_code=?",
								code);
				if (rs.next()) {
					int sn_id = rs.getInt(1);
					String log1 = null;
					StringBuffer sb1 = new StringBuffer();
					for (Map<Object, Object> map : maps) {
						if (!rs.getString("sn_custcode").equals(String.valueOf(map.get("sa_custcode")))) {
							log1 = "订单[" + map.get("sa_code") + "]客户：" + map.get("sa_custcode") + "，与指定通知单客户："
									+ rs.getString("sn_custcode") + "不一致!";
							if (log1 != null) {
								sb1.append(log1).append("<hr>");
							}
						}
						if (StringUtil.hasText(rs.getObject("sn_shcustcode")) && StringUtil.hasText(map.get("sa_shcustcode"))) {
							if (!rs.getString("sn_shcustcode").equals(String.valueOf(map.get("sa_shcustcode")))) {
								log1 = "订单[" + map.get("sa_code") + "]收货客户：" + map.get("sa_shcustcode") + "，与指定通知单收货客户："
										+ rs.getString("sn_shcustcode") + "不一致!";
								if (log1 != null) {
									sb1.append(log1).append("<hr>");
								}
							}
						}
						if (StringUtil.hasText(rs.getObject("sn_arcustcode"))) {
							if (!rs.getString("sn_arcustcode").equals(String.valueOf(map.get("sa_apcustcode")))) {
								log1 = "订单[" + map.get("sa_code") + "]应收客户：" + map.get("sa_apcustcode") + "，与指定通知单应收客户："
										+ rs.getString("sn_arcustcode") + "不一致!";
								if (log1 != null) {
									sb1.append(log1).append("<hr>");
								}
							}
						}
						if (StringUtil.hasText(rs.getObject("sn_currency"))) {
							if (!rs.getString("sn_currency").equals(String.valueOf(map.get("sa_currency")))) {
								log1 = "订单[" + map.get("sa_code") + "]币别：" + map.get("sa_currency") + "，与指定通知单币别："
										+ rs.getString("sn_currency") + "不一致!";
								if (log1 != null) {
									sb1.append(log1).append("<hr>");
								}
							}
						}
						if (StringUtil.hasText(rs.getObject("sn_paymentscode"))) {
							if (!rs.getString("sn_paymentscode").equals(String.valueOf(map.get("sa_paymentscode")))) {
								log1 = "订单[" + map.get("sa_code") + "]收款方式：" + map.get("sa_paymentscode") + "，与指定通知单收款方式："
										+ rs.getString("sn_paymentscode") + "不一致!";
								if (log1 != null) {
									sb1.append(log1).append("<hr>");
								}
							}
						}
						if (baseDao.isDBSetting("CopCheck")) {
							if (!rs.getString("sn_cop").equals(String.valueOf(map.get("sa_cop")))) {
								log1 = "订单[" + map.get("sa_code") + "]所属公司：" + map.get("sa_cop") + "，与指定通知单所属公司：" + rs.getString("sn_cop")
										+ "不一致!";
								if (log1 != null) {
									sb1.append(log1).append("<hr>");
								}
							}
						}
					}
					if (sb1.length() > 0) {
						BaseUtil.showError(sb1.toString());
					}
					// 判断指定的出货通知单状态是否[在录入]
					if (!"ENTERING".equals(rs.getString(2))) {
						BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.sendnotify.turn_onlyEntering")
								+ "<a href=\"javascript:openUrl('jsps/scm/sale/sendNotify.jsp?formCondition=sn_idIS" + sn_id
								+ "&gridCondition=snd_snidIS" + sn_id + "')\">" + code + "</a>&nbsp;");
					} else {
						// 转入明细
						transferRepository.transfer(caller, maps, new Key(sn_id, code.toString()));
						// 金额
						baseDao.execute(
								"update SendNotifyDetail set snd_total=round(snd_outqty*snd_sendprice,2),snd_netprice=round(snd_sendprice/(1+snd_taxrate/100),6),snd_taxtotal=round(snd_outqty*snd_sendprice/(1+snd_taxrate/100),2) where snd_snid=?",
								sn_id);
						// 仓库
						baseDao.execute(
								"update SendNotifyDetail set snd_warehouse=(select wh_description from warehouse where wh_code=snd_warehousecode) where snd_snid=? and snd_warehousecode is not null",
								sn_id);
						log = "出货通知单号:" + "<a href=\"javascript:openUrl('jsps/scm/sale/sendNotify.jsp?formCondition=sn_idIS" + sn_id
								+ "&gridCondition=snd_snidIS" + sn_id + "')\">" + code + "</a>&nbsp;";
						autoAudit(sn_id, code.toString());
						sb.append(index).append(": ").append(log).append("<hr>");
					}
				} else {
					BaseUtil.showError("指定通知单号不存在!");
				}
			} else {// 未指定出货通知单
				Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(maps, new Object[] { "sa_custcode", "sa_shcustcode",
						"sa_currency", "sa_apcustcode", "sa_paymentscode" });
				if (baseDao.isDBSetting(caller, "groupBySeller")) {
					groups = BaseUtil.groupsMap(maps, new Object[] { "sa_custcode", "sa_shcustcode", "sa_currency", "sa_apcustcode",
							"sa_paymentscode", "sa_departmentcode", "sa_sellercode" });
				}
				// 按客户分组的转入操作
				Set<Object> mapSet = groups.keySet();
				List<Map<Object, Object>> items;
				for (Object s : mapSet) {
					items = groups.get(s);
					// 转入通知单主记录
					Integer sa_id = baseDao.getFieldValue("SaleDetail", "sd_said", "sd_id=" + items.get(0).get("sd_id"), Integer.class);
					Key key = transferRepository.transfer(caller, sa_id);
					if (key != null) {
						index++;
						int sn_id = key.getId();
						// 转入明细
						transferRepository.transfer(caller, items, key);
						// 修改交期
						Object delivery = maps.get(0).get("sd_delivery");
						if (delivery != null) {
							baseDao.updateByCondition("SendNotify",
									"sn_deliverytime=" + DateUtil.parseDateToOracleString(Constant.YMD, delivery.toString()), "sn_id="
											+ sn_id);
						} else {
							baseDao.updateByCondition(
									"SendNotify",
									"sn_deliverytime=(select max(sd_delivery) from saledetail left join SendNotifyDetail on sd_id=snd_sdid where snd_snid=sn_id)",
									"sn_id=" + sn_id);
						}
						// 地址
						baseDao.execute(
								"update sendnotify set sn_toplace=(select cu_add1 from customer where sn_custcode=cu_code) where sn_id=? and nvl(sn_toplace,' ')=' '",
								sn_id);
						baseDao.execute("update sendnotify set sn_warehousecode='" + items.get(0).get("sd_whcode")
								+ "',sn_warehouseid=(select wh_id from warehouse where wh_code='" + items.get(0).get("sd_whcode")
								+ "') where sn_id=" + sn_id);
						// 部门
						/*
						 * baseDao.execute(
						 * "update sendnotify set (sn_departmentcode,sn_departmentname)=(select dp_code,em_depart from employee left join department on EM_DEPARTMENTCODE=dp_code where em_code=sn_sellercode) where sn_id=?"
						 * , sn_id);
						 */
						// 金额
						baseDao.execute(
								"update SendNotifyDetail set snd_total=round(snd_outqty*snd_sendprice,2),snd_netprice=round(snd_sendprice/(1+snd_taxrate/100),6),snd_taxtotal=round(snd_outqty*snd_sendprice/(1+snd_taxrate/100),2) where snd_snid=?",
								sn_id);
						// 仓库
						baseDao.execute(
								"update SendNotifyDetail set snd_warehousecode='" + items.get(0).get("sd_whcode") +"' where snd_snid=? and snd_warehousecode is null and snd_pdno=1",
								sn_id);
						baseDao.execute(
								"update SendNotifyDetail set snd_warehouse=(select wh_description from warehouse where wh_code=snd_warehousecode) where snd_snid=? and snd_warehousecode is not null",
								sn_id);
						log = "转入成功，出货通知单号:" + "<a href=\"javascript:openUrl('jsps/scm/sale/sendNotify.jsp?formCondition=sn_idIS" + sn_id
								+ "&gridCondition=snd_snidIS" + sn_id + "')\">" + key.getCode() + "</a><hr>";
						sb.append(index).append(": ").append(log).append("<hr>");
						autoAudit(sn_id, key.getCode());
					}
				}
			}
			// 修改销售单状态
			for (Map<Object, Object> map : maps) {
				int sdid = Integer.parseInt(map.get("sd_id").toString());
				saleDao.updateturnstatus(sdid);
			}

			return sb.toString();
		}
		return null;
	}

	/**
	 * 收料单批量入库
	 */
	@Override
	public String detailTurnStorage(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		// 按供应商号分组
		Map<Object, List<Map<Object, Object>>> set = BaseUtil.groupsMap(maps, new Object[] { "va_vendcode", "pu_currency" });
		StringBuffer sb = new StringBuffer();
		caller = "VerifyApply!in!Deal".equals(caller) ? "VerifyApply" : "VerifyApply!OS";
		for (Object obj : set.keySet()) {
			// 分供应商入库
			JSONObject j = verifyApplyDao.detailTurnStorage(caller, set.get(obj));
			if (j != null) {
				sb.append("转入成功,验收单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
						+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=ProdInOut!PurcCheckin')\">"
						+ j.get("pi_inoutno") + "</a>&nbsp;<hr>");
			}
		}
		return sb.toString();
	}

	/**
	 * 销售订单排定交期更改
	 */
	@Override
	public void vastSaveSale(String caller, String data) {
		// 销售订单排定交期更改
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> map : maps) {
			if (map.get("sd_pmcdate") != null) {
				baseDao.updateByCondition("SaleDetail", "sd_pmcdate=to_date('" + map.get("sd_pmcdate") + "','yyyy-MM-dd HH24:mi:ss')",
						"sd_id=" + map.get("sd_id"));
			}
			if (map.get("sd_delivery") != null) {
				baseDao.updateByCondition("SaleDetail", "sd_delivery=to_date('" + map.get("sd_delivery") + "','yyyy-MM-dd HH24:mi:ss')",
						"sd_id=" + map.get("sd_id"));
			}
		}
	}

	/**
	 * 出货单客户签收
	 */
	@Override
	public void vastSignin(String caller, String data) {
		// 出货单客户签收
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String status = BaseUtil.getLocalMessage("SIGNIN");
		for (Map<Object, Object> map : maps) {
			baseDao.updateByCondition("ProdInOut", "pi_date2=to_date('" + map.get("pi_date2") + "','yyyy-mm-dd hh:mm:ss')", "pi_shr='"
					+ map.get("pi_shr") + "',pi_invostatuscode='SIGNIN',pi_invostatus='" + status + "',pi_id=" + map.get("pi_id"));
		}
	}

	/**
	 * 采购检验单批量转入库
	 */
	public String purcTurnStorage(String caller, List<Map<Object, Object>> maps) {
		StringBuffer sb = new StringBuffer();
		String call = null;
		List<JSONObject> okR = null;
		List<JSONObject> ngR = null;
		List<Map<Object, Object>> ok = new ArrayList<Map<Object, Object>>();
		List<Map<Object, Object>> ng = new ArrayList<Map<Object, Object>>();
		// 判断明细状态是否是已审核
		// QUAVerifyApplyDetailDao.checkstatus(maps);
		double _okqty = 0;
		double _ngqty = 0;
		int vedid = 0;
		int isok = 0;
		int isng = 0;
		Map<Object, Object> n = null;
		String vedIds = CollectionUtil.pluckSqlString(maps, "ved_id");
		String ids = CollectionUtil.pluckSqlString(maps, "ve_id");
		//问题反馈 2018050419
		try {
			baseDao.execute("select ved_id from QUA_VerifyApplyDetailDet where ved_id in ("+vedIds+") for update");
		} catch (Exception e) {
			BaseUtil.showError("当前单据有其他人正在操作，不能执行当前操作");
		}
		Object ve_fztype = maps.get(0).containsKey("ve_fztype") ? maps.get(0).get("ve_fztype") : "0";
		// 更新ok,ng状态
		baseDao.execute("delete from QUA_VerifyApplyDetailDet where ved_id in(" + vedIds
				+ ") and nvl(ved_okqty,0)=0 and nvl(ved_ngqty,0)=0 ");
		baseDao.execute("update Qua_verifyapplydetaildet set ved_isok=1 where  ved_id in("
				+ vedIds
				+ ") and ved_okqty<=nvl((select sum(pd_inqty) from prodiodetail where pd_qcid=ved_id and pd_piclass='采购验收单'),0) and nvl(ved_okqty,0)>0 ");
		baseDao.execute("update Qua_verifyapplydetaildet set ved_isng=1 where  ved_id in("
				+ vedIds
				+ ") and ved_ngqty<=nvl((select sum(pd_inqty) from prodiodetail where pd_qcid=ved_id and pd_piclass='不良品入库单'),0) and nvl(ved_ngqty,0)>0 ");
		baseDao.execute("update Qua_verifyapplydetaildet set ved_statuscode='TURNIN',ved_status='已入库' where ved_id in(" + vedIds
				+ ") and ved_isok=1 and ved_isng=1  ");
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(distinct ve_code) from QUA_VerifyApplyDetail where ve_id in ("
								+ ids
								+ ") and ve_class='采购检验单' and nvl(ve_ordercode,' ')<>' '  and not exists (select pd_code,pd_detno from PurchaseDetail,purchase where pd_puid=pu_id and pd_code=ve_ordercode and pd_detno=ve_orderdetno and pu_statuscode='AUDITED' and nvl(pd_mrpstatuscode,' ') not in ('FINISH','FREEZE','NULLIFIED'))",
						String.class);
		if (dets != null) {
			BaseUtil.showError("存在采购单号+采购序号不存在或者状态不等于审核的检验单！检验单号：" + dets);
		}
		// 转入库
		for (Map<Object, Object> m : maps) {
			vedid = Integer.parseInt(m.get("ved_id").toString());
			Object[] veds = baseDao.getFieldsDataByCondition("Qua_verifyapplydetaildet", new String[] { "nvl(ved_okqty,0)", "ved_isok",
					"nvl(ved_ngqty,0)", "ved_isng" }, "ved_id=" + vedid);
			_okqty = Double.parseDouble(veds[0].toString());
			isok = Integer.parseInt(veds[1].toString());
			_ngqty = Double.parseDouble(veds[2].toString());
			isng = Integer.parseInt(veds[3].toString());
			if (_okqty > 0 && isok == 0) {
				m.put("qty", _okqty);
				m.put("wh", m.get("pr_whcode"));
				m.put("ve_id", m.get("ved_veid"));
				if (m.get("pr_whcode") == null || "".equals(m.get("pr_whcode"))) {
					BaseUtil.showError("请指定良品仓!");
				}
				ok.add(m);
			}
			if (_ngqty > 0 && isng == 0) {
				n = new HashMap<Object, Object>();
				n.put("qty", _ngqty);
				if (m.get("wh_code") == null || "".equals(m.get("wh_code"))) {
					BaseUtil.showError("请指定不良品仓!");
				}
				n.put("wh", m.get("wh_code"));
				n.put("ve_id", m.get("ved_veid"));
				n.put("ved_id", m.get("ved_id"));
				n.put("vad_code", m.containsKey("vad_code") ? m.get("vad_code") : "");
				ng.add(n);
			}
		}
		if (ok.size() > 0 && isok == 0) {
			call = "ProdInOut!PurcCheckin";// 检验单批量转入库单
			if (!"0".equals(ve_fztype)) {
				okR = QUAVerifyApplyDetailDao.detailTurnStorageByVacode(call, "采购验收单", ok, true);
			} else {
				okR = QUAVerifyApplyDetailDao.detailTurnStorage(call, "采购验收单", ok, true);
			}

		}
		if (ng.size() > 0 && isng == 0) {
			call = "ProdInOut!DefectIn";
			if (!"0".equals(ve_fztype)) {
				ngR = QUAVerifyApplyDetailDao.detailTurnStorageByVacode(call, "不良品入库单", ng, false);
			} else {
				ngR = QUAVerifyApplyDetailDao.detailTurnStorage(call, "不良品入库单", ng, false);
			}
		}
		if (okR != null && okR.size() > 0) {
			call = "ProdInOut!PurcCheckin";
			for (JSONObject j : okR) {
				baseDao.execute("update prodinout set (pi_currency,pi_rate)=(select pu_currency,pu_rate from purchase,prodiodetail where pd_piid=pi_id and pd_ordercode=pu_code and pd_pdno=1) where pi_id="
						+ j.get("pi_id") + " and nvl(pi_currency,' ')=' '");
				sb.append("转入成功,入库单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
						+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + call + "')\">" + j.get("pi_inoutno")
						+ "</a>&nbsp;<hr>");
				baseDao.execute("UPDATE prodiodetail SET pd_location=(SELECT pr_location FROM product WHERE pd_prodcode=pr_code) where pd_piid="
						+ j.get("pi_id") + " and  nvl(pd_location,' ')=' ' ");
				// 新增把purchasedetail中的报关价pd_bgprice 赋值到采购验收单的pd_customprice maz
				// 2018010130
				baseDao.execute("UPDATE prodiodetail SET (pd_prjcode,pd_prjname,pd_customprice)=(SELECT pd_prjcode,pd_prjname,pd_bgprice FROM purchasedetail WHERE pd_code=pd_ordercode and pd_detno=pd_orderdetno) where pd_piid="
						+ j.get("pi_id"));
				baseDao.execute("update prodinout set pi_printstatus='未打印',pi_printstatuscode='UNPRINT' where pi_id='" + j.get("pi_id")
						+ "'");
				handlerService.handler(caller, "turn", "after", new Object[] { j.get("pi_id") });
				/**
				 * 双单位 更新采购单位入库数量转入（按照入库数/物料的单位比重新计算）
				 */
				baseDao.execute("update prodiodetail set pd_ordertotal = pd_orderprice*(case when nvl(pd_purcinqty,0)=0 then pd_inqty else pd_purcinqty end) where pd_piid="
						+ j.get("pi_id"));
			}
			baseDao.execute("UPDATE QUA_VERIFYAPPLYDETAIL SET VE_INGOODQTY=NVL((SELECT SUM(VED_OKQTY) FROM QUA_VERIFYAPPLYDETAILDET WHERE VED_VEID=VE_ID AND VED_ISOK=1),0) where exists (select 1 from QUA_VERIFYAPPLYDETAILDET where ved_id in("
					+ vedIds + ") and ved_veid=ve_id)");
		}
		if (ngR != null && ngR.size() > 0) {
			call = "ProdInOut!DefectIn";
			for (JSONObject j : ngR) {
				baseDao.execute("update prodinout set (pi_currency,pi_rate)=(select pu_currency,pu_rate from purchase,prodiodetail where pd_piid=pi_id and pd_ordercode=pu_code and pd_pdno=1) where pi_id="
						+ j.get("pi_id") + " and nvl(pi_currency,' ')=' '");
				sb.append("转入成功,不良品入库单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
						+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + call + "')\">" + j.get("pi_inoutno")
						+ "</a>&nbsp;<hr>");
				baseDao.execute("UPDATE prodiodetail SET pd_location=(SELECT pr_location FROM product WHERE pd_prodcode=pr_code) where pd_piid="
						+ j.get("pi_id") + " and nvl(pd_location,' ')=' '  ");
				baseDao.execute("update prodinout set pi_printstatus='未打印',pi_printstatuscode='UNPRINT' where pi_id='" + j.get("pi_id")
						+ "'");
				handlerService.handler(caller, "turn", "after", new Object[] { j.get("pi_id") });
				/**
				 * 双单位 更新采购单位入库数量转入（按照入库数/物料的单位比重新计算）
				 */
				baseDao.execute("update prodiodetail set pd_ordertotal = pd_orderprice*(case when nvl(pd_purcinqty,0)=0 then pd_inqty else pd_purcinqty end) where pd_piid="
						+ j.get("pi_id"));
			}
			baseDao.execute("UPDATE QUA_VERIFYAPPLYDETAIL SET ve_inbadqty=NVL((SELECT SUM(VED_NGQTY) FROM QUA_VERIFYAPPLYDETAILDET WHERE VED_VEID=VE_ID AND VED_ISNG=1),0) where exists (select 1 from QUA_VERIFYAPPLYDETAILDET where ved_id in("
					+ vedIds + ") and ved_veid=ve_id)");
		}
		if (sb.length() == 0)
			return "转入失败!";
		return sb.toString();
	}

	/**
	 * 委外检验单批量转入库
	 */
	public String oSTurnStorage(String caller, List<Map<Object, Object>> maps) {
		StringBuffer sb = new StringBuffer();
		String call = null;
		List<JSONObject> okR = null;
		List<JSONObject> ngR = null;
		List<Map<Object, Object>> ok = new ArrayList<Map<Object, Object>>();
		List<Map<Object, Object>> ng = new ArrayList<Map<Object, Object>>();
		double okqty = 0;
		double ngqty = 0;
		double _okqty = 0;
		double _ngqty = 0;
		int veid = 0;
		int vedid = 0;
		int isok = 0;
		int isng = 0;
		Object ve_fztype = maps.get(0).containsKey("ve_fztype") ? maps.get(0).get("ve_fztype") : "0";
		for (Map<Object, Object> m : maps) {
			vedid = Integer.parseInt(m.get("ved_id").toString());
			Object wwqty = baseDao.getSummaryByField("prodiodetail", "pd_inqty", "pd_qcid=" + vedid + " and pd_piclass='委外验收单'");
			Object blqty = baseDao.getSummaryByField("prodiodetail", "pd_inqty", "pd_qcid=" + vedid + " and pd_piclass='不良品入库单'");
			baseDao.execute("update Qua_verifyapplydetaildet set ved_isok=1,ved_statuscode='TURNIN',ved_status='已入库' where ved_okqty<="
					+ wwqty + " and nvl(ved_okqty,0)>0 and ved_id=" + vedid);
			baseDao.execute("update Qua_verifyapplydetaildet set ved_isng=1,ved_statuscode='TURNIN',ved_status='已入库' where ved_ngqty<="
					+ blqty + " and nvl(ved_ngqty,0)>0 and ved_id=" + vedid);
			Object[] veds = baseDao.getFieldsDataByCondition("Qua_verifyapplydetaildet", new String[] { "ved_okqty", "ved_isok",
					"ved_ngqty", "ved_isng" }, "ved_id=" + vedid);
			_okqty = Double.parseDouble(veds[0].toString());
			isok = Integer.parseInt(veds[1].toString());
			okqty += _okqty;
			_ngqty = Double.parseDouble(veds[2].toString());
			isng = Integer.parseInt(veds[3].toString());
			ngqty += _ngqty;
			if (veid == 0) {
				veid = Integer.parseInt(String.valueOf(m.get("ved_veid")));
			}
			if (_okqty > 0 && isok == 0) {
				m.put("qty", _okqty);
				m.put("wh", m.get("pr_whcode"));
				m.put("ve_id", m.get("ved_veid"));
				if (m.get("pr_whcode") == null || "".equals(m.get("pr_whcode"))) {
					BaseUtil.showError("请指定良品仓!");
				}
				ok.add(m);
			}
			if (_ngqty > 0 && isng == 0) {
				Map<Object, Object> n = new HashMap<Object, Object>();
				n.put("qty", _ngqty);
				if (m.get("wh_code") == null || "".equals(m.get("wh_code"))) {
					BaseUtil.showError("请指定不良品仓!");
				}
				n.put("wh", m.get("wh_code"));
				n.put("ve_id", m.get("ve_id"));
				n.put("ved_id", m.get("ved_id"));
				n.put("vad_code", m.containsKey("vad_code") ? m.get("vad_code") : "");
				ng.add(n);
			}
		}
		if (ok.size() > 0 && isok == 0) {
			call = "ProdInOut!OutsideCheckIn";
			if (!"0".equals(ve_fztype)) {
				okR = QUAVerifyApplyDetailDao.detailTurnStorageOsByVacode(call, "委外验收单", ok, true);
			} else {
				okR = QUAVerifyApplyDetailDao.detailTurnStorageOs(call, "委外验收单", ok, true);
			}

		}
		if (ng.size() > 0 && isng == 0) {
			call = "ProdInOut!DefectIn";
			if (!"0".equals(ve_fztype)) {
				ngR = QUAVerifyApplyDetailDao.detailTurnStorageOsByVacode(call, "不良品入库单", ng, false);
			} else {
				ngR = QUAVerifyApplyDetailDao.detailTurnStorageOs(call, "不良品入库单", ng, false);
			}
		}
		if (okR != null && okR.size() > 0) {
			call = "ProdInOut!OutsideCheckIn";
			for (JSONObject j : okR) {
				if ("委外验收".equals(j.get("intype"))) {
					baseDao.execute("update prodinout set (pi_currency,pi_rate)=(select ma_currency,ma_rate from make,prodiodetail where pd_piid=pi_id and pd_ordercode=ma_code and nvl(pd_intype,' ')='委外验收' and pd_pdno=1) where pi_id="
							+ j.get("pi_id") + " and nvl(pi_currency,' ')=' '");
					baseDao.execute("update prodinout set (pi_receivecode,pi_receivename)=(select MA_APVENDCODE,MA_APVENDNAME from make,prodiodetail where pd_piid=pi_id and pd_ordercode=ma_code and pd_pdno=1 and nvl(ma_apvendcode,' ')<>' ') where pi_id="
							+ j.get("pi_id"));
				} else if ("工序验收".equals(j.get("intype"))) {
					baseDao.execute("update prodinout set (pi_currency,pi_rate)=(select mc_currency,mc_rate from makecraft,prodiodetail where pd_piid=pi_id and pd_ordercode=mc_code and nvl(pd_intype,' ')='工序验收' and pd_pdno=1) where pi_id="
							+ j.get("pi_id") + " and nvl(pi_currency,' ')=' '");
				}
				baseDao.execute("update prodinout set (pi_receivecode,pi_receivename)=(select VE_APVENDCODE,VE_APVENDNAME from VENDOR where VE_CODE=PI_CARDCODE) where pi_id="
						+ j.get("pi_id") + " AND NVL(pi_receivecode,' ')=' '");
				baseDao.execute("update prodinout set pi_printstatus='未打印',pi_printstatuscode='UNPRINT' where pi_id='" + j.get("pi_id")
						+ "'");
				// 委外验收成功之后增加业务
				handlerService.handler(caller, "turn", "after", new Object[] { j.get("pi_id") });
				sb.append("转入成功,入库单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
						+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + call + "')\">" + j.get("pi_inoutno")
						+ "</a>&nbsp;<hr>");
			}
			baseDao.updateByCondition("QUA_VerifyApplyDetail", "ve_ingoodqty=nvl(ve_ingoodqty,0)+" + okqty, "ve_id=" + veid);
		}
		if (ngR != null && ngR.size() > 0) {
			call = "ProdInOut!DefectIn";
			for (JSONObject j : ngR) {
				if ("委外验收".equals(j.get("intype"))) {
					baseDao.execute("update prodinout set (pi_currency,pi_rate)=(select ma_currency,ma_rate from make,prodiodetail where pd_piid=pi_id and pd_ordercode=ma_code and nvl(pd_intype,' ')='委外验收' and pd_pdno=1) where pi_id="
							+ j.get("pi_id") + " and nvl(pi_currency,' ')=' '");
					baseDao.execute("update prodinout set (pi_receivecode,pi_receivename)=(select MA_APVENDCODE,MA_APVENDNAME from make,prodiodetail where pd_piid=pi_id and pd_ordercode=ma_code and pd_pdno=1 and nvl(ma_apvendcode,' ')<>' ') where pi_id="
							+ j.get("pi_id"));
				} else if ("工序验收".equals(j.get("intype"))) {
					baseDao.execute("update prodinout set (pi_currency,pi_rate)=(select mc_currency,mc_rate from makecraft,prodiodetail where pd_piid=pi_id and pd_ordercode=mc_code and nvl(pd_intype,' ')='工序验收' and pd_pdno=1) where pi_id="
							+ j.get("pi_id") + " and nvl(pi_currency,' ')=' '");
				}
				baseDao.execute("update prodinout set (pi_receivecode,pi_receivename)=(select VE_APVENDCODE,VE_APVENDNAME from VENDOR where VE_CODE=PI_CARDCODE) where pi_id="
						+ j.get("pi_id") + " AND NVL(pi_receivecode,' ')=' '");
				baseDao.execute("update prodinout set pi_printstatus='未打印',pi_printstatuscode='UNPRINT' where pi_id='" + j.get("pi_id")
						+ "'");
				sb.append("转入成功,不良品入库单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
						+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + call + "')\">" + j.get("pi_inoutno")
						+ "</a>&nbsp;<hr>");
			}
			baseDao.updateByCondition("QUA_VerifyApplyDetail", "ve_inbadqty=nvl(ve_inbadqty,0)+" + ngqty, "ve_id=" + veid);
		}
		return sb.toString();
	}

	/**
	 * 采购单转采购验收单
	 */
	@Override
	public String detailTurnPurcProdIO(String caller, String data, String formParam) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		Map<Object, Object> formStore = BaseUtil.parseFormStoreToMap(formParam);
		String sign = "";
		if (store.size() > 0) {
			// maz 2018030057 2018-03-09 增加英唐电子特殊参数判断rohs报告不齐全，限制转单
			if (baseDao.isDBSetting("Purchase!ToCheckAccept!Deal", "RoHSTurn") && StringUtil.hasText(store.get(0).get("pd_prodcode"))) {
				for (Map<Object, Object> map : store) {
					SqlRowList rs = baseDao
							.queryForRowSet("select pr_code from product where pr_manutype='PURCHASE' and (pr_rohs='否' or pr_rohs is null) and pr_code='"
									+ map.get("pd_prodcode") + "'");
					if (rs.next()) {
						sign = sign + rs.getString("pr_code") + ",";
					}
				}
				if (sign != null && !"".equals(sign)) {
					sign = sign.substring(0, sign.length() - 1);
					BaseUtil.showError("存在RoHS报告不齐全的物料，不能转单，料号:" + sign);
				}
			}
			// 判断采购单状态、本次数量限制
			purchaseDao.checkqty(store);
			String adidstr = "";
			StringBuffer sb = new StringBuffer();
			int index = 0;
			String log = null;
			for (Map<Object, Object> map : store) {
				adidstr += "," + map.get("pd_id").toString();
			}
			if (!adidstr.equals("")) {
				adidstr = adidstr.substring(1);
				String dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat('采购单号：'||pd_code||',行：'||pd_detno||',明细状态：'||pd_mrpstatus) from PurchaseDetail where nvl(pd_mrpstatuscode,' ') in ('FINISH','NULLIFIED','FREEZE') and pd_id in ("
										+ adidstr + ")", String.class);
				if (dets != null) {
					BaseUtil.showError("选中的明细行已结案、已冻结，不允许转入验收单!" + dets);
				}
			}
			Object code = store.get(0).containsKey("pi_inoutno") ? store.get(0).get("pi_inoutno") : null;
			Object pi_invocode = store.get(0).containsKey("pi_invocode") ? store.get(0).get("pi_invocode") : null;// 供应商发票号
			// 指定了采购验收单
			if (StringUtil.hasText(code)) {
				SqlRowList rs = baseDao
						.queryForRowSet(
								"select pi_id,pi_invostatuscode,pi_statuscode,pi_cop,pi_cardcode,pi_receivecode,pi_currency,pi_paymentcode from ProdInOut where pi_inoutno=? and pi_class='采购验收单'",
								code);
				if (rs.next()) {
					// 判断指定的出货通知单状态是否[在录入]
					if (!"ENTERING".equals(rs.getString(2))) {
						BaseUtil.showError("只能指定[在录入]的采购验收单!");
					}
					if (!"UNPOST".equals(rs.getString(3))) {
						BaseUtil.showError("只能指定[未过账]的采购验收单!");
					}
					// 供应商、应付供应商、币别、付款款方式不同，限制转单
					StringBuffer errBuffer = new StringBuffer();
					String errSn = baseDao.getJdbcTemplate().queryForObject(
							"select wm_concat(distinct pu_code) from purchase, purchasedetail where pd_puid=pu_id and pd_id in (" + adidstr
									+ ") and pu_vendcode<>?", String.class, rs.getString("pi_cardcode"));
					if (errSn != null)
						errBuffer.append("您选择的采购单的供应商，与指定的采购验收单的供应商不一致！采购单：<br>" + errSn.replace(",", "<br>")).append("<hr>");
					errSn = baseDao.getJdbcTemplate().queryForObject(
							"select wm_concat(distinct pu_code) from purchase, purchasedetail where pd_puid=pu_id and pd_id in (" + adidstr
									+ ") and pu_receivecode<>?", String.class, rs.getString("pi_receivecode"));
					if (errSn != null)
						errBuffer.append("您选择的采购单的应付供应商，与指定的采购验收单的应付供应商不一致！采购单：<br>" + errSn.replace(",", "<br>")).append("<hr>");
					errSn = baseDao.getJdbcTemplate().queryForObject(
							"select wm_concat(distinct pu_code) from purchase, purchasedetail where pd_puid=pu_id and pd_id in (" + adidstr
									+ ") and pu_currency<>?", String.class, rs.getString("pi_currency"));
					if (errSn != null)
						errBuffer.append("您选择的采购单的币别，与指定的采购验收单的币别不一致！采购单：<br>" + errSn.replace(",", "<br>")).append("<hr>");
					errSn = baseDao.getJdbcTemplate().queryForObject(
							"select wm_concat(distinct pu_code) from purchase, purchasedetail where pd_puid=pu_id and pd_id in (" + adidstr
									+ ") and pu_paymentscode<>?", String.class, rs.getString("pi_paymentcode"));
					if (errSn != null)
						errBuffer.append("您选择的采购单的付款方式，与指定的采购验收单的付款方式不一致！采购单：<br>" + errSn.replace(",", "<br>")).append("<hr>");
					int pi_id = rs.getInt(1);
					if (baseDao.isDBSetting("CopCheck")) {
						errSn = baseDao.getJdbcTemplate().queryForObject(
								"select wm_concat(distinct pu_code) from purchase, purchasedetail where pd_puid=pu_id and pd_id in ("
										+ adidstr + ") and pu_cop<>?", String.class, rs.getString("pi_cop"));
						if (errSn != null)
							errBuffer.append("您选择的采购单的所属公司，与指定的采购验收单的所属公司不一致！采购单：<br>" + errSn.replace(",", "<hr>"));
					}
					if (errBuffer.length() > 0)
						BaseUtil.showError(errBuffer.toString());
					// 转入明细
					transferRepository.transfer(caller, store, new Key(pi_id, code.toString()));
					baseDao.execute(
							"update prodiodetail set pd_prodid=(select pr_id from product where pd_prodcode=pr_code) where pd_piid=?",
							pi_id);
					baseDao.updateByCondition("ProdIODetail",
							"pd_orderprice=(select pd_price from PurchaseDetail where pd_code=pd_ordercode and pd_detno=pd_orderdetno)",
							"pd_piid=" + pi_id + " and nvl(pd_orderprice,0)=0");
					baseDao.updateByCondition(
							"ProdIODetail",
							"pd_ordertotal=round(nvl(pd_orderprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2), pd_total=round(nvl(pd_price,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2)",
							"pd_piid=" + pi_id);
					baseDao.updateByCondition("ProdInOut",
							"pi_total=(SELECT round(sum(nvl(pd_orderprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0))),2) FROM ProdIODetail WHERE pd_piid="
									+ pi_id + ")", "pi_id=" + pi_id);
					baseDao.updateByCondition("ProdInOut", "pi_sendcode='" + formStore.get("pu_refcode") + "'", "pi_id=" + pi_id);
					baseDao.updateByCondition(
							"ProdIODetail",
							"pd_taxtotal=round(pd_orderprice*(nvl(pd_inqty,0)+nvl(pd_outqty,0))*nvl(pd_taxrate,0)/(100+nvl(pd_taxrate,0)),2)",
							"pd_piid=" + pi_id);
					baseDao.updateByCondition("ProdInOut", "pi_totalupper=L2U(nvl(pi_total,0))", "pi_id=" + pi_id);
					/**
					 * @author wsy 双单位
					 */
					baseDao.execute(
							"update prodiodetail set pd_purcinqty=round(pd_inqty/(select pd_qty from purchasedetail where pd_code=pd_ordercode and pd_detno=pd_orderdetno)*(select case when nvl(pd_purcqty,0)=0 then pd_qty else pd_purcqty end from purchasedetail where pd_code=pd_ordercode and pd_detno=pd_orderdetno),6) where pd_piid=?",
							pi_id);
					baseDao.execute("MERGE INTO purchasedetail USING (select nvl(pd_purcinqty,0) qty,pd_ordercode,pd_orderdetno from prodiodetail where pd_id in (select  max(pd_id)  from prodiodetail  where pd_piid="
							+ pi_id
							+ "  group by  pd_ordercode,pd_orderdetno) ) T ON ( purchasedetail.pd_code=t.pd_ordercode and purchasedetail.pd_detno=t.pd_orderdetno) WHEN MATCHED THEN UPDATE SET pd_ypurcqty=nvl(pd_ypurcqty,0)+t.qty where  pd_id in (select pd_orderid from prodiodetail where pd_piid="
							+ pi_id + ")");
					log = "转入成功,验收单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + pi_id
							+ "&gridCondition=pd_piidIS" + pi_id + "&whoami=ProdInOut!PurcCheckin')\">" + code + "</a>";
					sb.append(log).append("<hr>");

					// 2018010426 号问题 xzx 2018/1/24
					handlerService.handler(caller, "turnProd", "after", new Object[] { pi_id });
				} else {
					BaseUtil.showError("指定出货单号不存在!");
				}

			} else {// 未指定出货通知单
				Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(store, new Object[] { "pu_vendcode", "pu_receivecode",
						"pu_currency", "pu_paymentscode" });
				Set<Object> mapSet = groups.keySet();
				List<Map<Object, Object>> items;
				for (Object s : mapSet) {
					items = groups.get(s);
					Integer pu_id = baseDao.getFieldValue("PurchaseDetail", "pd_puid", "pd_id=" + items.get(0).get("pd_id"), Integer.class);
					Key key = transferRepository.transfer(caller, pu_id);
					if (key != null) {
						int piid = key.getId();
						index++;
						// 转入明细
						transferRepository.transfer(caller, items, key);
						if (pi_invocode != null) {
							baseDao.execute("update prodinout set pi_invocode='" + pi_invocode + "' where pi_id=?", piid);
						}
						baseDao.execute("update prodinout set pi_type=(select pu_type from purchase where pu_id=?) where pi_id=?", pu_id,
								piid);
						baseDao.execute(
								"update prodinout set pi_rate=(select cm_crrate from currencysMonth where cm_crname=pi_currency and cm_yearmonth=to_char(pi_date,'yyyymm')) where pi_id=?",
								piid);
						baseDao.execute(
								"update prodiodetail set pd_prodid=(select pr_id from product where pd_prodcode=pr_code) where pd_piid=?",
								piid);
						baseDao.updateByCondition(
								"ProdIODetail",
								"pd_orderprice=(select pd_price from PurchaseDetail where pd_code=pd_ordercode and pd_detno=pd_orderdetno)",
								"pd_piid=" + piid + " and nvl(pd_orderprice,0)=0");
						/**
						 * @author wsy 双单位 采购单转采购验收单：生成的采购金额按照采购单位入库数量来算
						 */
						baseDao.execute(
								"update prodiodetail set pd_purcinqty=round(pd_inqty/(select pd_qty from purchasedetail where pd_code=pd_ordercode and pd_detno=pd_orderdetno)*(select case when nvl(pd_purcqty,0)=0 then pd_qty else pd_purcqty end from purchasedetail where pd_code=pd_ordercode and pd_detno=pd_orderdetno),6) where pd_piid=?",
								piid);
						baseDao.updateByCondition(
								"ProdIODetail",
								"pd_ordertotal=round(nvl(pd_orderprice,0)*(nvl((case when nvl(pd_purcinqty,0)=0 then pd_inqty else pd_purcinqty end),0)+nvl((case when nvl(pd_purcoutqty,0)=0 then pd_outqty else pd_purcoutqty end),0)),2), pd_total=round(nvl(pd_price,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2)",
								"pd_piid=" + piid);
						baseDao.updateByCondition("ProdInOut",
								"pi_total=(SELECT round(sum(nvl(pd_orderprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0))),2) FROM ProdIODetail WHERE pd_piid="
										+ piid + ")", "pi_id=" + piid);
						baseDao.updateByCondition("ProdInOut", "pi_sendcode='" + formStore.get("pu_refcode") + "'", "pi_id=" + piid);
						baseDao.updateByCondition(
								"ProdIODetail",
								"pd_taxtotal=round(pd_orderprice*(nvl(pd_inqty,0)+nvl(pd_outqty,0))*nvl(pd_taxrate,0)/(100+nvl(pd_taxrate,0)),2)",
								"pd_piid=" + piid);
						baseDao.updateByCondition("ProdInOut", "pi_totalupper=L2U(nvl(pi_total,0))", "pi_id=" + piid);
						// baseDao.execute("update purchasedetail set pd_ypurcqty=nvl(pd_ypurcqty,0)+(select nvl(pd_purcqty,0) from ProdIODetail  where pd_ordercode=pd_code and pd_orderdetno=pd_detno and pd_piid="+piid+") where pd_puid="+pu_id);
						/**
						 * 双单位 更新采购单位已转数
						 */
						baseDao.execute("MERGE INTO purchasedetail USING (select nvl(pd_purcinqty,0) qty,pd_ordercode,pd_orderdetno from prodiodetail where pd_id in (select  max(pd_id)  from prodiodetail  where pd_piid="
								+ piid
								+ "  group by  pd_ordercode,pd_orderdetno) ) T ON ( purchasedetail.pd_code=t.pd_ordercode and purchasedetail.pd_detno=t.pd_orderdetno) WHEN MATCHED THEN UPDATE  SET   pd_ypurcqty=nvl(pd_ypurcqty,0)+t.qty where  pd_id in (select pd_orderid from prodiodetail where pd_piid="
								+ piid + ")");
						log = "转入成功,验收单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + piid
								+ "&gridCondition=pd_piidIS" + piid + "&whoami=ProdInOut!PurcCheckin')\">" + key.getCode() + "</a>";
						sb.append(index).append(": ").append(log).append("<hr>");

						// 2018010426 号问题 xzx 2018/1/24
						handlerService.handler(caller, "turnProd", "after", new Object[] { piid });

					}
				}
			}
			handlerService.handler(caller, "turnProdIO", "after", new Object[] { store });
			// 修改采购单入库状态
			for (Map<Object, Object> map : store) {
				int pdid = Integer.parseInt(map.get("pd_id").toString());
				/**
				 * 双单位 更新采购单位已转数
				 */
				// baseDao.execute("update purchasedetail set pd_ypurcqty=(select sum(nvl(pd_purcinqty,0)) from ProdIODetail  where pd_ordercode=pd_code and pd_orderdetno=pd_detno and pd_piid="+doublepiid+") where pd_id="+pdid+" ");
				purchaseDao.udpateturnstatus(pdid);
			}
			return sb.toString();
		}
		return null;
	}

	@Override
	public String turnMake(String data) {
		try {
			List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
			StringBuffer sb = new StringBuffer();
			for (Map<Object, Object> s : store) {
				Object[] obs = baseDao.getFieldsDataByCondition(
						"saledetail left Join product on sd_prodcode=pr_code",
						"sd_code,sd_detno",
						"(nvl(pr_manutype,' ') not in('MAKE','OSMAKE') or nvl(sd_statuscode,' ') in ('FINISH','FREEZE') or "
								+ "nvl(pr_dhzc,' ')<>'MPS' or nvl(sd_qty,0)-nvl(sd_tomakeqty,0)<" + s.get("sd_tqty") + ") and sd_id="
								+ s.get("sd_id"));
				if (obs != null) {
					BaseUtil.showError("订单[" + obs[0] + "]明细行" + obs[1] + "不满足下达工单条件，请重新选择！");
				}
			}
			for (Map<Object, Object> s : store) {
				StringBuffer result = saleDao.turnMake(Integer.parseInt(s.get("sd_id").toString()),
						Double.parseDouble(String.valueOf(s.get("sd_tqty"))));
				sb.append(result);
			}
			return "转入成功：<br>" + sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "转入失败";
		}
	}

	/**
	 * 收料通知单整批转收料单
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public String vastTurnVerifyApply(String caller, String data) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		int index = 0;
		SqlRowList rs = null;
		String log = null;
		Object sendcode = null;
		Object receivecode = null;
		Object paymentscode = null;
		String ids = "";
		// 按PO供应商+PO应付供应商+币别+收料通知单+付款方式分组
		Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(store, new Object[] { "an_vendcode", "pu_currency",
				"pu_paymentscode", "pu_receivecode", "an_sendcode" });
		Set<Object> mapSet = groups.keySet();
		List<Map<Object, Object>> items;
		for (Object s : mapSet) {
			items = groups.get(s);
			paymentscode = items.get(0).get("pu_paymentscode");
			sendcode = items.get(0).get("an_sendcode");
			receivecode = items.get(0).get("pu_receivecode");
			Integer an_id = baseDao.getFieldValue("AcceptNotifyDetail", "and_anid", "and_id=" + items.get(0).get("and_id"), Integer.class);
			Key key = transferRepository.transfer(caller, an_id);
			if (key != null) {
				int vaid = key.getId();
				ids = ids + "," + vaid;
				index++;
				baseDao.execute("update verifyApply set va_receivecode='" + receivecode + "',va_paymentscode='" + paymentscode
						+ "',va_sendcode='" + sendcode + "' where va_id=" + vaid);
				baseDao.execute("update verifyApply set va_receivename=(select ve_name from vendor where ve_code=va_receivecode) where nvl(va_receivecode,' ')<>' ' and va_id="
						+ vaid);
				baseDao.execute("update verifyApply set va_payments=(select pa_name from payments where pa_code=va_paymentscode and pa_class='付款方式') where nvl(va_paymentscode,' ')<>' ' and va_id="
						+ vaid);
				// 转入明细
				transferRepository.transfer(caller, items, key);
				for (Map<Object, Object> item : items) {
					rs = baseDao
							.queryForRowSet("select pd_id,pd_puid,and_inqty,and_yqty,an_code,an_id from AcceptNotifydetail left join AcceptNotify "
									+ "on an_id=and_anid left join purchasedetail on and_ordercode=pd_code and and_orderdetno=pd_detno where and_id="
									+ item.get("and_id"));
					if (rs.next()) {
						if (rs.getDouble("and_inqty") < rs.getDouble("and_yqty")) {
							BaseUtil.showError("转入失败,通知单：" + rs.getString("an_code") + "超数,请刷新界面数据");
						}
						baseDao.execute("update PURCHASEDETAIL set pd_yqty=nvl(pd_yqty,0)+" + item.get("and_tqty") + " where pd_id="
								+ rs.getInt("pd_id"));
						purchaseDao.udpateturnstatus(rs.getInt("pd_id"));
						// 将通知单中的条码转入到收料单
						// 判断通知单是否有条码
						Double bar_qty = baseDao.getJdbcTemplate().queryForObject(
								"select NVL(sum(nvl(ban_qty,0)),0) qty from BarAcceptNotify where ban_andid=?", Double.class,
								item.get("and_id"));
						if (bar_qty > 0) {// 有条码
							// 判断本次明细转入的数量是否等于收料通知单内该行明细的数量
							// 判断明细行条码数量是否等于通知单内该行明细的数量
							if (Double.valueOf(item.get("and_tqty").toString()).equals(rs.getDouble("and_inqty"))
									&& bar_qty.equals(rs.getDouble("and_inqty"))) {// 都相等，将通知单条码转入
								baseDao.execute("insert into VerifyApplyDetailP(vadp_id,vadp_vadid,vadp_vaddetno,vadp_vacode,vadp_prodcode,vadp_prodid,"
										+ "vadp_qty,vadp_barcode,vadp_vendcode,vadp_vendname,vadp_madedate,vadp_outboxcode,vadp_outboxid) "
										+ " select VERIFYAPPLYDETAILP_SEQ.nextval,vad_id,vad_detno,vad_code,ban_prodcode,ban_prodid, "
										+ " ban_qty,ban_barcode,ban_vendcode,vad_vendname,vad_madedate,ban_outboxcode,ban_outboxid "
										+ " from barAcceptNotify left join acceptNotifydetail on and_id=ban_andid left join verifyapplydetail on vad_andid=and_id where vad_vaid="
										+ vaid + " and and_id=" + item.get("and_id"));
							}
						}
					}
				}
				// 更新采购收料单的所属公司，取采购单的所属公司
				SqlRowList rs2 = baseDao.queryForRowSet(
						"select * from (select pu_cop from VerifyApplydetail left join purchase on pu_code=vad_pucode where vad_vaid=?"
								+ " and nvl(pu_cop,' ')<>' ' order by pu_id) where rownum<2", vaid);
				while (rs2.next()) {
					Object vacop = baseDao.getFieldDataByCondition("VerifyApply", "va_cop", "va_id=" + vaid);
					Object pucop = baseDao.getFieldDataByCondition(
							"(select pu_cop from VerifyApplydetail left join purchase on pu_code=vad_pucode where vad_vaid=" + vaid
									+ " and nvl(pu_cop,' ')<>' ' order by pu_id)", "pu_cop", "rownum<2");
					if (vacop == null) {
						baseDao.updateByCondition("VerifyApply", "va_cop='" + pucop + "'", "va_id=" + vaid);
					}
				}
				// 修改收料通知单状态
				/*
				 * baseDao.updateByCondition("AcceptNotify",
				 * "an_statuscode='TURNVA',an_status='" +
				 * BaseUtil.getLocalMessage("TURNVA") + "'  ", "an_id=" + an_id+
				 * " and not exists (select 1 from AcceptNotifydetail where and_anid=an_id and and_inqty-NVL(and_yqty,0)>0) "
				 * );
				 */log = "转入成功,收料单号:" + "<a href=\"javascript:openUrl('jsps/scm/purchase/verifyApply.jsp?formCondition=va_idIS" + vaid
						+ "&gridCondition=vad_vaidIS" + vaid + "&whoami=VerifyApply')\">" + key.getCode() + "</a>&nbsp;";
				sb.append(index).append(": ").append(log).append("<hr>");
			}
		}
		// 修改报价单状态
		for (Map<Object, Object> map : store) {
			int and_id = Integer.parseInt(map.get("and_id").toString());
			verifyApplyDao.updateAccStatus(and_id);
		}
		handlerService.handler(caller, "turnPordIO", "after", new Object[] { store, ids });
		return sb.toString();
	}

	/**
	 * 入库单转出库单(入库单界面)
	 * 
	 * @author mad
	 */
	@Override
	public String turnDefectOut(String caller, String data, String type) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String piclass = null;
		if ("ProdInOut!DefectOut".equals(type)) {
			piclass = "不良品出库单";
		} else if ("ProdInOut!ExchangeOut".equals(type)) {
			piclass = "换货出库单";
		} else if ("ProdInOut!OtherPurcOut".equals(type)) {
			piclass = "其它采购出库单";
		} else if ("ProdInOut!OtherOut".equals(type)) {
			piclass = "其它出库单";
		} else if ("ProdInOut!AppropriationOut".equals(type)) {
			piclass = "拨出单";
		} else if ("ProdInOut!Sale".equals(type)) {
			piclass = "出货单";
		} else if ("ProdInOut!CustReturnOut".equals(type)) {
			piclass = "客退返修机出库单";
		} else if ("ProdInOut!PurcCheckout".equals(type)) {
			piclass = "采购验退单";
		} else if ("ProdInOut!ReturnTurnSale".equals(type)) {
			piclass = "出货单";
		}
		StringBuffer sb = new StringBuffer();
		Object y = 0;
		SqlRowList rs = null;
		for (Map<Object, Object> map : maps) {
			int pdid = Integer.parseInt(map.get("pd_id").toString());
			double tqty = Double.parseDouble(map.get("pd_tqty").toString());
			y = baseDao.getFieldDataByCondition("ProdIODetail", "sum(nvl(pd_outqty,0))", "pd_ioid=" + pdid);
			y = y == null ? 0 : y;
			rs = baseDao.queryForRowSet("SELECT pd_inoutno,pd_pdno,pd_inqty FROM ProdIODetail WHERE pd_id=? and pd_inqty<?", pdid,
					Double.parseDouble(y.toString()) + tqty);
			if (rs.next()) {
				sb = new StringBuffer("[本次数量填写超出可转数量],入库单号:").append(rs.getString("pd_inoutno")).append(",行号:")
						.append(rs.getInt("pd_pdno")).append(",入库数量:").append(rs.getDouble("pd_inqty")).append(",已转数:").append(y)
						.append(",本次数:").append(tqty).append("<hr/>");
			}
			if ("ProdInOut!ReturnTurnSale".equals(type)) {
				if (!"".equals(map.get("pd_ordercode").toString()) && map.get("pd_ordercode").toString() != "null"
						&& map.get("pd_ordercode").toString() != null || !"".equals(map.get("pd_ordercode"))) {
					Object sk_outtype = baseDao.getFieldDataByCondition("salekind", "sk_outtype",
							"sk_name=(select sa_kind from Sale where sa_code='" + map.get("pd_ordercode") + "')");
					if (!"TURNOUT".equals(sk_outtype)) {
						BaseUtil.showError("销售订单:" + map.get("pd_ordercode").toString() + " 不是转出货单类型，不能转出货单");
					}
				}
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
		if (maps.size() > 0) {
			JSONObject j = null;
			String pi_inoutno = null;
			String newpi_inoutno = null;
			int detno = 1;
			int piid = 0;
			for (Map<Object, Object> map : maps) {
				if (pi_inoutno == null) {
					Object pi_id = baseDao.getFieldDataByCondition("ProdIODetail", "pd_piid", "pd_id=" + map.get("pd_id"));
					j = prodInOutDao.newProdDefectOut(Integer.parseInt(pi_id.toString()), piclass, type);
					if (j != null) {
						pi_inoutno = j.getString("pi_inoutno");
						piid = j.getInt("pi_id");
						if ("ProdInOut!Sale".equals(type) || "ProdInOut!ReturnTurnSale".equals(type)) {
							baseDao.execute("update prodinout set (pi_arcode, pi_arname, pi_receivecode, pi_receivename, pi_paymentcode, pi_payment)=(select cu_arcode, cu_arname, cu_shcustcode, cu_shcustname, cu_paymentscode, cu_payments from customer where cu_code=pi_cardcode) where pi_id="
									+ piid);
							baseDao.execute(
									"update ProdInOut set pi_address=(select cu_add1 from Customer where cu_code=pi_cardcode) where pi_id=? and nvl(pi_address,' ')=' '",
									piid);
							baseDao.execute(
									"update ProdInOut set (pi_purposename,pi_expresscode)=(select ca_person,ca_phone from CustomerAddress left join customer on ca_cuid=cu_id  where cu_code=pi_cardcode and ca_remark='是')  where pi_id=?",
									piid);
							baseDao.execute(
									"update ProdInOut set pi_rate=nvl((select cm_crrate from currencysmonth where cm_yearmonth=to_char(pi_date,'yyyymm') and cm_crname=pi_currency),1) where pi_id=? and nvl(pi_currency,' ')<>' '",
									piid);
							baseDao.execute(
									"update ProdIODetail set pd_taxtotal=round(pd_sendprice*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2), pd_nettotal=round(pd_netprice*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2) WHERE pd_piid=?",
									pi_id);
							baseDao.updateByCondition(
									"ProdIODetail",
									"pd_netprice=round(pd_sendprice/(1+pd_taxrate/100),8),pd_nettotal=round(pd_sendprice*pd_outqty/(1+nvl(pd_taxrate,0)/100),2)",
									"pd_piid=" + pi_id);
							baseDao.execute(
									"update ProdInOut set pi_total=(SELECT round(sum(nvl(pd_sendprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0))),2) FROM ProdIODetail WHERE pd_piid=pi_id) where pi_id=?",
									piid);
							baseDao.updateByCondition("ProdInOut", "pi_totalupper=L2U(nvl(pi_total,0))", "pi_id=" + pi_id);
						}
						if ("ProdInOut!OtherOut".equals(type)) {
							baseDao.execute("update ProdInOut set pi_type=null where pi_id=?", piid);
						}
						if ("ProdInOut!ReturnTurnSale".equals(type)) {
							sb.append("转入成功," + piclass + "号:"
									+ "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + j.get("pi_id")
									+ "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=ProdInOut!Sale')\">" + pi_inoutno
									+ "</a>&nbsp;");
						} else {
							sb.append("转入成功," + piclass + "号:"
									+ "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + j.get("pi_id")
									+ "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + type + "')\">" + pi_inoutno + "</a>&nbsp;");
						}
					}
				}
				if (pi_inoutno != null) {
					int pdid = Integer.parseInt(map.get("pd_id").toString());
					double tqty = Double.parseDouble(map.get("pd_tqty").toString());
					if ("ProdInOut!AppropriationOut".equals(type)) {
						prodInOutDao.toAppointedAppropriationOut(piid, pdid, tqty, detno++);
					} else if ("ProdInOut!OtherOut".equals(type) || "ProdInOut!CustReturnOut".equals(type)
							|| "ProdInOut!OtherPurcOut".equals(type)) {
						prodInOutDao.toAppointedProdOtherOut(piid, pdid, tqty, detno++);
					} else if ("ProdInOut!Sale".equals(type)) {
						prodInOutDao.toAppointedProdSaleOut(piid, pdid, tqty, detno++);
					} else if ("ProdInOut!ReturnTurnSale".equals(type)) {
						prodInOutDao.toAppointedProdSaleReturnOut(piid, pdid, tqty, detno++);
					} else {
						prodInOutDao.toAppointedProdDefectOut(piid, pdid, tqty, detno++);
						if (baseDao.isDBSetting("ProdInOut!PurcCheckin", "isJoint")) { // 锤子科技按开票数量拆分验退单参数
							String msg = Joint(piid, pdid, tqty, detno - 1, newpi_inoutno);
							newpi_inoutno = msg;
						}
					}
					// 修改ProdInOutDetail状态
					baseDao.updateByCondition("ProdIODetail", "pd_yqty=nvl(pd_yqty,0)+" + tqty, "pd_id=" + pdid);
					// 记录日志
					Object[] cts = baseDao.getFieldsDataByCondition("ProdIODetail", "pd_piid,pd_pdno", "pd_id=" + pdid);
					/*
					 * baseDao.logger.turn("转不良品出库", "ProdInOut!DefectIn",
					 * "pi_id", cts[0]);
					 */
					if ("ProdIN!ToProdOtherOut!Deal".equals(caller) && "ProdInOut!OtherOut".equals(type)) {
						baseDao.logger.turnDetail("msg.turnProdIO!OtherOut", "ProdInOut!OtherIn", "pi_id", cts[0], cts[1] + ",数量：" + tqty);
					} else if ("ProdIN!ToProdOtherPurcOut!Deal".equals(caller) && "ProdInOut!OtherPurcOut".equals(type)) {
						baseDao.logger.turnDetail("msg.turnProdIO!OtherPurcOut", "ProdInOut!OtherPurcIn", "pi_id", cts[0], cts[1] + ",数量："
								+ tqty);
					} else if ("ProdIN!ToProdPurcOut!Deal".equals(caller) && "ProdInOut!PurcCheckout".equals(type)) {
						// 生成日志
						baseDao.logger.turnDetail("转" + piclass, "ProdInOut!PurcCheckin", "pi_id", cts[0], cts[1] + ",数量：" + tqty);
					} else if ("ProdIN!ToProdDefectOut!Deal".equals(caller) && "ProdInOut!DefectOut".equals(type)) {
						// 记录日志
						baseDao.logger.turnDetail("转" + piclass, "ProdInOut!DefectIn", "pi_id", cts[0], cts[1] + ",数量：" + tqty);
					} else if ("ProdInOut!SaleReturn!ToSale".equals(caller) && "ProdInOut!ReturnTurnSale".equals(type)) {
						// 记录日志
						baseDao.logger.turnDetail("转" + piclass, "ProdInOut!SaleReturn", "pi_id", cts[0], cts[1] + ",数量：" + tqty);
					} else {
						baseDao.logger.turnDetail("msg.turnProdIO", caller, "pi_id", cts[0], cts[1]);
					}
				}
			}
			if (newpi_inoutno != null) {
				Object id = baseDao
						.getFieldDataByCondition("ProdInOut", "pi_id", "pi_inoutno='" + newpi_inoutno + "' and pi_class='采购验退单'");
				sb.append("<br>转入成功,对接的采购验退单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + id
						+ "&gridCondition=pd_piidIS" + id + "&whoami=ProdInOut!PurcCheckout')\">" + newpi_inoutno + "</a>&nbsp;");
			}
		}
		return sb.toString();
	}

	/**
	 * 出库单转入库单(出库单界面)
	 * 
	 * @author mad
	 */
	@Override
	public String turnDefectIn(String caller, String data, String type) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		int piid = 0;
		String piclass = null;
		if ("ProdInOut!OtherIn".equals(type)) {
			piclass = "其它入库单";
		} else if ("ProdInOut!OutReturn".equals(type)) {
			piclass = "借货归还单";
		} else if ("ProdInOut!SaleReturn".equals(type)) {
			piclass = "销售退货单";
		} else if ("ProdInOut!GoodsShutout".equals(type)) {
			piclass = "用品退仓单";
		}
		StringBuffer sb = new StringBuffer();
		Object y = 0;
		SqlRowList rs = null;
		List<Map<Object, Object>> removemaps = new ArrayList<Map<Object, Object>>();
		for (Map<Object, Object> map : maps) {
			int pdid = Integer.parseInt(map.get("pd_id").toString());
			double tqty = Double.parseDouble(map.get("pd_tqty").toString());
			y = baseDao.getFieldDataByCondition("ProdIODetail", "sum(nvl(pd_inqty,0))", "pd_ioid=" + pdid);
			y = y == null ? 0 : y;
			rs = baseDao.queryForRowSet("SELECT pd_inoutno,pd_pdno,pd_outqty FROM ProdIODetail WHERE pd_id=? and pd_outqty<?", pdid,
					Double.parseDouble(y.toString()) + tqty);
			if (rs.next()) {
				sb = new StringBuffer("[本次数量填写超出可转数量],出库单号:").append(rs.getString("pd_inoutno")).append(",行号:")
						.append(rs.getInt("pd_pdno")).append(",出库数量:").append(rs.getDouble("pd_outqty")).append(",已转数:").append(y)
						.append(",本次数:").append(tqty).append("<hr/>");
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
		maps.removeAll(removemaps);
		if (maps.size() > 0) {
			JSONObject j = null;
			String pi_inoutno = null;
			int detno = 1;
			for (Map<Object, Object> map : maps) {
				if (pi_inoutno == null) {
					Object pi_id = baseDao.getFieldDataByCondition("ProdIODetail", "pd_piid", "pd_id=" + map.get("pd_id"));
					j = prodInOutDao.newProdDefectOut(Integer.parseInt(pi_id.toString()), piclass, type);
					if (j != null) {
						pi_inoutno = j.getString("pi_inoutno");
						piid = j.getInt("pi_id");
						sb.append("转入成功," + piclass + "号:"
								+ "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + piid
								+ "&gridCondition=pd_piidIS" + piid + "&whoami=" + type + "')\">" + pi_inoutno + "</a>&nbsp;");
					}
				}
				if (pi_inoutno != null) {
					int pdid = Integer.parseInt(map.get("pd_id").toString());
					double tqty = Double.parseDouble(map.get("pd_tqty").toString());
					if ("ProdInOut!OtherIn".equals(type)) {
						prodInOutDao.toAppointedProdDefectIn(piid, pdid, tqty, detno++);
					} else if ("ProdInOut!OutReturn".equals(type)) {
						prodInOutDao.toAppointedProdOutReturn(piid, pdid, tqty, detno++);
					} else if ("ProdInOut!SaleReturn".equals(type)) {
						prodInOutDao.toAppointedProdSaleReturn(piid, pdid, tqty, detno++);
					} else {
						prodInOutDao.toAppointedProdDefectIn(piid, pdid, tqty, detno++);
					}
					// 修改ProdInOutDetail状态
					baseDao.updateByCondition("ProdIODetail", "pd_yqty=nvl(pd_yqty,0)+" + tqty, "pd_id=" + pdid);
					// 记录日志
					Object[] cts = baseDao.getFieldsDataByCondition("ProdIODetail", "pd_piid,pd_pdno", "pd_id=" + pdid);
					if ("ProdInOut!Sale!ToSaleReturn".equals(caller) && "ProdInOut!SaleReturn".equals(type)) {
						baseDao.logger.turnDetail("msg.turnProdIO!SaleReturn", "ProdInOut!Sale", "pi_id", cts[0], cts[1] + ",数量：" + tqty);
					} else {
						baseDao.logger.turnDetail("msg.turnProdIO", caller, "pi_id", cts[0], cts[1]);
					}
				}
			}
			if ("ProdInOut!OutReturn".equals(type)) {
				baseDao.execute("update prodinout set (pi_arcode, pi_arname, pi_receivecode, pi_receivename, pi_paymentcode, pi_payment)=(select cu_arcode, cu_arname, cu_shcustcode, cu_shcustname, cu_paymentscode, cu_payments from customer where cu_code=pi_cardcode) where pi_id="
						+ piid);
				baseDao.execute("update ProdIODetail set pd_ordertotal=round(pd_inqty*nvl(pd_sendprice,0),2) where pd_piid=" + piid);
				baseDao.execute(
						"update ProdInOut set pi_total=(SELECT round(sum(nvl(pd_sendprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0))),2) FROM ProdIODetail WHERE pd_piid=pi_id) where pi_id=?",
						piid);
				baseDao.updateByCondition("ProdInOut", "pi_totalupper=L2U(nvl(pi_total,0))", "pi_id=" + piid);
			} else if ("ProdInOut!OtherIn".equals(type) || "ProdInOut!GoodsShutout".equals(type)) {
				baseDao.execute("update ProdIODetail set pd_total=round(pd_inqty*nvl(pd_price,0),2) where pd_piid=" + piid);
			}
		}
		// 给INTERCEPTORS表添加一个‘turn’类型
		handlerService.handler(caller, "turn", "after", new Object[] { piid });
		return sb.toString();
	}

	@Override
	public String detailTurnIn(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		List<Map<Object, Object>> purc = CollectionUtil.filter(maps, CollectionUtil.INCLUDE, "ve_class", "采购检验单");
		List<Map<Object, Object>> os = CollectionUtil.filter(maps, CollectionUtil.INCLUDE, "ve_class", "委外检验单");
		baseDao.execute("update qua_verifyapplydetaildet set ved_code=(select ve_code from qua_verifyapplydetail where ved_veid=ve_id) where not exists (select 1 from qua_verifyapplydetail where ved_code=ve_code)");
		String result = "";
		if (os.size() > 0) {
			result = oSTurnStorage(caller, os); // 委外检验单
		}
		if (purc.size() > 0) {
			result += "<br>" + purcTurnStorage(caller, purc); // 采购检验单
		}
		return result;
	}

	@Override
	public String vastAPBillPost(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Object[] status = null;
		List<String> successList = new ArrayList<String>();
		for (Map<Object, Object> map : maps) {
			status = baseDao.getFieldsDataByCondition("APBill", new String[] { "ab_statuscode", "ab_date", "ab_yearmonth", "ab_code" },
					"ab_id='" + map.get("ab_id") + "'");
			if (status != null) {
				if (status[0].equals("UNPOST")) {
					String res = baseDao.callProcedure("Sp_CommiteAPBill", new Object[] { status[3], 1 });
					if (res != null && !res.trim().equals("")) {
						// 未成功过账的发票
						BaseUtil.showError(res);
					} else {
						// 已经成功过账的发票
						successList.add(status[3].toString());
					}
				}
			}
		}
		String returnMsg = "";
		if (successList.size() > 0) {
			returnMsg = "应付发票:";
			for (int i = 0; i < successList.size(); i++) {
				if (i == successList.size() - 1) {
					// list中最后一个数据
					returnMsg = returnMsg + successList.get(i);
				} else {
					returnMsg = returnMsg + successList.get(i) + ",";
				}

			}
			returnMsg = returnMsg + "过账成功!";
		} else {
			returnMsg = "没有成功过账的应付发票";
		}
		return returnMsg;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public String erpteCai(String caller, String data) {
		Employee employee = SystemSession.getUser();
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Object piid = maps.get(0).get("pd_piid");
		Object source = baseDao.getFieldDataByCondition("ProdInOut", "pi_inoutno", "pi_id=" + piid);
		Object[] objs = null;
		StringBuffer sb = new StringBuffer();
		Object qctype = maps.get(0).get("pd_qctype");
		for (Map<Object, Object> map : maps) {
			int pdid = Integer.parseInt(map.get("pd_id").toString());
			double tqty = Double.parseDouble(map.get("pd_tqty").toString());
			objs = baseDao.getFieldsDataByCondition("ProdIODetail left join ProdInOut on pi_id=pd_piid", new String[] { "pi_code",
					"pd_pdno", "pd_yqty", "pd_inqty", "pd_outqty" }, "pd_id=" + pdid + " AND nvl(pd_yqty, 0)+" + tqty
					+ ">nvl(pd_inqty,0)+nvl(pd_outqty,0)");
			if (objs != null) {
				sb.append("出入库单号:" + objs[0] + ",行号:" + objs[1] + ",入库数量:" + objs[3] + ",出库数量:" + objs[4] + ",无法转出,已转数量:" + objs[2]
						+ ",本次数量:" + tqty + "<hr/>");
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
		Map<String, Object> diffence = new HashMap<String, Object>();
		int pi_id = baseDao.getSeqId("PRODINOUT_SEQ");
		String pi_inoutno = baseDao.sGetMaxNumber("ProdInOut!DefectOut", 2);
		String inCheckcode = baseDao.sGetMaxNumber("ProdInOut!PurcCheckin", 2);
		String inMakecode = baseDao.sGetMaxNumber("ProdInOut!OutsideCheckIn", 2);
		diffence.put("pi_sourcecode", "'" + source + "'");
		diffence.put("pi_id", pi_id);
		diffence.put("pi_inoutno", "'" + pi_inoutno + "'");
		diffence.put("pi_class", "'不良品出库单'");
		diffence.put("pi_date", "sysdate");
		diffence.put("pi_recorddate", "sysdate");
		diffence.put("pi_recordman", "'" + employee.getEm_name() + "'");
		diffence.put("pi_updatedate", "sysdate");
		diffence.put("pi_updateman", "'" + employee.getEm_name() + "'");
		diffence.put("pi_operatorcode", "'" + employee.getEm_code() + "'");
		diffence.put("pi_invostatuscode", "'ENTERING'");
		diffence.put("pi_invostatus", "'" + BaseUtil.getLocalMessage("ENTERING") + "'");
		diffence.put("pi_statuscode", "'UNPOST'");
		if ("采购检验单".equals(qctype)) {
			diffence.put("pi_type", "'特采'");
		} else if ("委外检验单".equals(qctype)) {
			diffence.put("pi_type", "'OS'");
		}
		diffence.put("pi_status", "'" + BaseUtil.getLocalMessage("UNPOST") + "'");
		diffence.put("pi_printstatuscode", "'UNPRINT'");
		diffence.put("pi_printstatus", "'" + BaseUtil.getLocalMessage("UNPRINT") + "'");
		diffence.put("pi_inoutman", null);
		diffence.put("pi_date1", null);
		diffence.put("pi_fromcode", "'" + inCheckcode + "'");
		// 转入主表
		baseDao.copyRecord("ProdInOut", "ProdInOut", "pi_id=" + piid, diffence);
		// 转入从表
		diffence = new HashMap<String, Object>();
		diffence.put("pd_piid", pi_id);
		diffence.put("pd_qcid", 0);
		diffence.put("pd_inoutno", "'" + pi_inoutno + "'");
		diffence.put("pd_piclass", "'不良品出库单'");
		diffence.put("pd_auditstatus", "'ENTERING'");
		diffence.put("pd_status", 0);
		diffence.put("pd_inqty", 0);
		diffence.put("pd_yqty", 0);
		// 转采购验收单
		int inid = baseDao.getSeqId("PRODINOUT_SEQ");

		Map<String, Object> turnInStore = new HashMap<String, Object>();
		turnInStore.put("pi_sourcecode", "'" + source + "'");
		turnInStore.put("pi_id", inid);
		if ("采购检验单".equals(qctype)) {
			turnInStore.put("pi_inoutno", "'" + inCheckcode + "'");
			turnInStore.put("pi_class", "'采购验收单'");
			turnInStore.put("pi_type", "'特采'");
		} else if ("委外检验单".equals(qctype)) {
			turnInStore.put("pi_inoutno", "'" + inMakecode + "'");
			turnInStore.put("pi_class", "'委外验收单'");
			turnInStore.put("pi_type", "'OS'");
		}
		turnInStore.put("pi_recorddate", "sysdate");
		turnInStore.put("pi_date", "sysdate");
		turnInStore.put("pi_recordman", "'" + employee.getEm_name() + "'");
		turnInStore.put("pi_updatedate", "sysdate");
		turnInStore.put("pi_updateman", "'" + employee.getEm_name() + "'");
		turnInStore.put("pi_operatorcode", "'" + employee.getEm_code() + "'");
		turnInStore.put("pi_invostatuscode", "'ENTERING'");
		turnInStore.put("pi_invostatus", "'" + BaseUtil.getLocalMessage("ENTERING") + "'");
		turnInStore.put("pi_statuscode", "'UNPOST'");
		turnInStore.put("pi_status", "'" + BaseUtil.getLocalMessage("UNPOST") + "'");
		turnInStore.put("pi_printstatuscode", "'UNPRINT'");
		turnInStore.put("pi_printstatus", "'" + BaseUtil.getLocalMessage("UNPRINT") + "'");
		turnInStore.put("pi_inoutman", null);
		turnInStore.put("pi_date1", null);
		turnInStore.put("pi_fromcode", "'" + pi_inoutno + "'");
		baseDao.copyRecord("ProdInOut", "ProdInOut", "pi_id=" + piid, turnInStore);
		turnInStore = new HashMap<String, Object>();
		turnInStore.put("pd_piid", inid);
		if ("采购检验单".equals(qctype)) {
			turnInStore.put("pd_inoutno", "'" + inCheckcode + "'");
			turnInStore.put("pd_piclass", "'采购验收单'");
		} else if ("委外检验单".equals(qctype)) {
			turnInStore.put("pd_inoutno", "'" + inMakecode + "'");
			turnInStore.put("pd_piclass", "'委外验收单'");
		}
		turnInStore.put("pd_auditstatus", "'ENTERING'");
		turnInStore.put("pd_status", 0);
		turnInStore.put("pd_prodmadedate", "null");
		turnInStore.put("pd_batchcode", "null");
		turnInStore.put("pd_batchid", 0);
		turnInStore.put("pd_whcode", "null");
		turnInStore.put("pd_whname", "null");
		turnInStore.put("pd_qcid", 0);
		turnInStore.put("pd_yqty", 0);
		turnInStore.put("pd_id", "PRODIODETAIL_SEQ.nextval");
		if (maps.size() > 0) {
			int detno = 1;
			for (Map<Object, Object> map : maps) {
				int pdid = Integer.parseInt(map.get("pd_id").toString());
				double tqty = Double.parseDouble(map.get("pd_tqty").toString());
				diffence.put("pd_id", baseDao.getSeqId("PRODIODETAIL_SEQ"));
				diffence.put("pd_outqty", tqty);
				diffence.put("pd_pdno", detno);
				diffence.put("pd_orderid", pdid);
				diffence.put("pd_ioid", pdid);
				turnInStore.put("pd_pdno", detno);
				turnInStore.put("pd_orderid", pdid);
				turnInStore.put("pd_inqty", tqty);
				baseDao.copyRecord("ProdIODetail", "ProdIODetail", "pd_id=" + pdid, diffence);
				baseDao.copyRecord("ProdIODetail", "ProdIODetail", "pd_id=" + pdid, turnInStore);
				baseDao.updateByCondition("ProdIODetail", "pd_yqty=nvl(pd_yqty,0)+" + tqty, "pd_id=" + pdid);
				detno++;
			}
		}
		/**
		 * maz 2017100283 不良品入库单转特采生成的采购验收单从表的仓库取物料资料默认的仓库，主表仓库取明细第一行物料的仓库
		 * 17-10-26
		 */
		if ("采购检验单".equals(qctype)) {
			baseDao.execute("update ProdIODetail a set (pd_whcode,pd_whname)=(select pr_whcode,pr_whname from product where pr_code=a.pd_prodcode) where pd_piid="
					+ inid);
			Object[] wh = baseDao.getFieldsDataByCondition("(select pd_whcode,pd_whname from prodiodetail where pd_piid=" + inid
					+ " order by pd_pdno asc)", new String[] { "pd_whcode", "pd_whname" }, "rownum<2");
			baseDao.execute("update prodinout a set pi_whcode='" + wh[0] + "',pi_whname='" + wh[1] + "' where pi_id=" + inid);
		}
		// 判断已审核才允许过账
		if (baseDao.isDBSetting("ProdInOut!DefectOut", "postNeedAudit")) {
			baseDao.execute("update ProdInOut set pi_invostatuscode='AUDITED',pi_invostatus='" + BaseUtil.getLocalMessage("AUDITED")
					+ "' where pi_id=" + pi_id);
		}
		prodInOutService.postProdInOut(pi_id, "ProdInOut!DefectOut");
		// 修改ProdInOutDetail状态
		if ("采购检验单".equals(qctype)) {
			// 记录日志
			int pd_id = Integer.parseInt(maps.get(0).get("pd_id").toString());
			int tqty = Integer.parseInt(maps.get(0).get("pd_tqty").toString());
			Object[] cts = baseDao.getFieldsDataByCondition("ProdIODetail", "pd_piid,pd_pdno", "pd_id=" + pd_id);
			baseDao.logger.turnDetail("转特采", "ProdInOut!DefectIn", "pi_id", cts[0], cts[1] + ",数量：" + tqty);
			return "操作成功!不良品出库单:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=" + "pi_idIS" + pi_id
					+ "&gridCondition=pd_piidIS" + pi_id + "&whoami=ProdInOut!DefectOut')\">" + pi_inoutno + "</a>&nbsp;<hr> " + "采购验收单:"
					+ "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=" + "pi_idIS" + inid
					+ "&gridCondition=pd_piidIS" + inid + "&whoami=ProdInOut!PurcCheckin')\">" + inCheckcode + "</a>&nbsp;<hr>";
		} else if ("委外检验单".equals(qctype)) {
			return "操作成功!不良品出库单:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=" + "pi_idIS" + pi_id
					+ "&gridCondition=pd_piidIS" + pi_id + "&whoami=ProdInOut!DefectOut')\">" + pi_inoutno + "</a>&nbsp;<hr> " + "委外验收单:"
					+ "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=" + "pi_idIS" + inid
					+ "&gridCondition=pd_piidIS" + inid + "&whoami=ProdInOut!OutsideCheckIn')\">" + inMakecode + "</a>&nbsp;<hr>";
		}
		return null;
	}

	@Override
	public String vastTurnProdINCustomer(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		// 分客户
		Map<Object, List<Map<Object, Object>>> set = BaseUtil.groupMap(maps, "sd_id");
		StringBuffer sb = new StringBuffer();
		String log = null;
		int index = 1;
		for (Object obj : set.keySet()) {
			log = saleDao.turnProdInOutCustomer(caller, set.get(obj));
			sb.append(index++).append(":").append(log).append("<hr/>");
		}
		return sb.toString();
	}

	@Override
	public String notifyTurnCustomerCheck(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		// 分客户
		Map<Object, List<Map<Object, Object>>> set = BaseUtil.groupMap(maps, "snd_id");
		StringBuffer sb = new StringBuffer();
		String log = null;
		int index = 1;
		for (Object obj : set.keySet()) {
			log = sendNotifyDao.turnProdInOutCustomer(caller, set.get(obj));
			sb.append(index++).append(":").append(log).append("<hr/>");
		}
		return sb.toString();
	}

	@Override
	public List<Map<String, Object>> getProductWh(String codes, boolean useFactory,String caller) {
		if (StringUtil.hasText(codes)) {
			String condition = " and 1=1 ";
//			if (useFactory) {
//				String factory = SystemSession.getUser().getEm_factory();
//				if (StringUtil.hasText(factory)) {
//					condition = " and nvl(wh_factory,' ') in(' ','" + factory + "')";
//				}
//			}
			List<String> callers = new ArrayList<String>();
			callers.add("MakeMaterial!issue");
			callers.add("MakeMaterial!Scrap");
			callers.add("MakeMaterial!Give");
			callers.add("MakeMaterial!Return");
			callers.add("MakeMaterial!OS!issue");
			callers.add("MakeMaterial!OS!Scrap");
			callers.add("MakeMaterial!OS!Give");
			callers.add("MakeMaterial!OS!Return");
			callers.add("MultiMakeSendLS");
			callers.add("MakeSendLS");
			callers.add("MakeSendLS!Out");
			callers.add("MultiMakeSendLS!OS");
			callers.add("SendNotify!ToProdIN!Deal");
			int lastIndexOf = callers.lastIndexOf(caller);
			if(lastIndexOf!=-1&&baseDao.isDBSetting("sys", "showUserFactoryWh")){
				if(baseDao.isDBSetting(caller, "disableProductwh")){
					return null;
				}
				String factory = SystemSession.getUser().getEm_factory();
				if (StringUtil.hasText(factory)) {
					condition = " and nvl(wh_factory,' ') in(' ','" + factory + "')";
				}
			}
			SqlRowList rs = baseDao
					.queryForRowSet("select pw_prodcode,pw_whcode,wh_description,pw_onhand,pw_onhand-NVL(lockqty,0) freeonhand,po_defectonhand,po_mrponhand,(po_onhand-po_defectonhand)lp_qty,pd_qty from productwh left join warehouse on wh_code=pw_whcode left join "
							+ " (select pd_prodcode,pd_whcode,sum(pd_outqty)lockqty from prodiodetail where pd_status=0 and pd_outqty>0 and pd_batchid>0 group by pd_prodcode,pd_whcode)A"
							+ " on A.pd_prodcode=pw_prodcode and A.pd_whcode=pw_whcode left join productonhand on pw_prodcode=po_prodcode left join (select sum(pd_outqty)pd_qty,pd_prodcode,pd_whcode from prodiodetail where pd_outqty>0 and pd_status=0 group by pd_prodcode,pd_whcode)B on pw_prodcode=B.pd_prodcode and wh_code=B.pd_whcode where pw_prodcode in ("
							+ codes + ") and pw_onhand>0 " + condition);
			if (rs.hasNext()) {
				return rs.getResultList();
			}
		}
		return null;
	}

	@Override
	public String vastTurnPreProduct(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Employee employee = SystemSession.getUser();
		for (Map<Object, Object> map : maps) {
			int id = baseDao.getSeqId("PreProduct_SEQ");
			String code = baseDao.sGetMaxNumber("PreProduct", 2);
			String insertSQL = "insert into PreProduct (pre_id,pre_thisid,pre_recordman,pre_name2,pre_description2,pre_status,pre_statuscode) values("
					+ id
					+ ",'"
					+ code
					+ "','"
					+ employee.getEm_name()
					+ "','"
					+ map.get("ppd_name")
					+ "','"
					+ map.get("ppd_describe")
					+ "','在录入','ENTERING')";
			baseDao.execute(insertSQL);
			baseDao.updateByCondition("Plmpreproductdet", "ppd_isturn=1", "ppd_id=" + map.get("ppd_id"));

		}
		return "操作成功";
	}

	@Override
	@Transactional
	public String vastTurnOaapplicate(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		// 按供应商和类型分组
		Map<Object, List<Map<Object, Object>>> set = BaseUtil.groupsMap(maps, new Object[] { "od_vendcode", "oa_type" });
		Employee employee = SystemSession.getUser();
		StringBuilder sb = new StringBuilder();
		int inde = 1;
		List<String> sqls = new ArrayList<String>();
		Object opcode=null;
		String log = null;
		if (StringUtil.hasText(maps.get(0).get("op_code"))) {//指定采购单
			opcode = maps.get(0).get("op_code");
			Object[] op = baseDao.getFieldsDataByCondition("Oapurchase", new String[] { "op_statuscode", "op_vecode","op_id"}, "op_code='" + opcode + "'");
			if (op == null) {
				BaseUtil.showError("指定的用品采购单不存在或已删除!");
			} else if (!"ENTERING".equals(String.valueOf(op[0]))) {
				BaseUtil.showError("指定的用品采购单状态不等于[在录入]!");
			}
			for (Map<Object, Object> map : maps) {
				if (StringUtil.hasText(map.get("od_vendcode"))) {
					if (!op[1].toString().equals(String.valueOf(map.get("od_vendcode")))) {
						log = "所选请购单供应商：" + map.get("od_vendcode") + "，与指定采购单的供应商：" + op[1] + "不一致!";
					}
					if (log != null) {
						sb.append(log).append("<hr>");
					}
				}
				if (sb.length() > 0) {
					BaseUtil.showError(sb.toString());
				}
			}
			int id = Integer.parseInt(op[2].toString());
			Integer index = baseDao.queryForObject("select nvl(max(od_detno),0) from oapurchasedetail where od_oaid="+id, Integer.class)+1;
			for (Object key : set.keySet()) {
				for (Map<Object, Object> map : set.get(key)) {
					Object price = baseDao.getFieldDataByCondition("Product", "pr_avprice", "pr_code='" + map.get("od_procode") + "'");// pr_avprice
					String insertdetail = "insert into Oapurchasedetail(od_id,od_oaid,od_detno,od_oadetno,od_oacode,od_procode,od_proname,od_prounit,od_neednumber,"
							+ "od_price,od_needdate,od_appmancode,od_appman,od_departmentcode,od_department,od_remark)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					Object[] appman = baseDao.getFieldsDataByCondition("employee", new String[] { "em_code", "em_name" },
							"em_name='" + map.get("oa_appman") + "'");
					Object[] dept = baseDao.getFieldsDataByCondition("department", new String[] { "dp_code", "dp_name" },
							"dp_name='" + map.get("oa_department") + "'");
					if (map.get("od_needdate") == null) {
						map.put("od_needdate", DateUtil.currentDateString(Constant.YMD_HMS));
					} else {
						map.put("od_needdate", map.get("od_needdate") + " 00:00:00");
					}
					sqls.add("update oaapplicationdetail set od_yqty=(nvl(od_yqty,0)+" + map.get("od_tqty") + ") where od_id="
							+ map.get("od_id"));
					baseDao.execute(insertdetail, new Object[] { baseDao.getSeqId("Oapurchasedetail_SEQ"), id, index++, map.get("od_detno"),
							map.get("oa_code"), map.get("od_procode"), map.get("od_proname"), map.get("od_prounit"), map.get("od_tqty"), price,
							Timestamp.valueOf(map.get("od_needdate") + ""), appman[0], appman[1], dept[0], dept[1], map.get("od_remark") });
				}
				baseDao.execute("update oapurchasedetail set od_rate=(select cr_taxrate from currencys left join Oapurchase on op_currency=cr_name and cr_statuscode='CANUSE' where od_oaid=op_id)"
						+ " where od_oaid=" + id);
			}
			sb.append("转入成功,用品采购单号:<a href=\"javascript:openUrl('jsps/oa/appliance/oapurchase.jsp?formCondition=op_idIS"
					+ id + "&gridCondition=od_oaidIS" + id + "&whoami=Oapurchase')\">" + opcode + "</a>&nbsp;<hr/>");
			baseDao.execute(sqls);			
		}else {
			for (Object key : set.keySet()) {
				int index = 1;
				int id = baseDao.getSeqId("Oapurchase_SEQ");
				String code = baseDao.sGetMaxNumber("Oapurchase", 2);
				String insertSQL = "insert into Oapurchase(op_id,op_code,op_recordor,op_recordorid,op_status,op_statuscode,op_date,op_kind,op_vecode,op_vename,op_currency,op_department,op_departcode)values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
				for (Map<Object, Object> map : set.get(key)) {
					Object price = baseDao.getFieldDataByCondition("Product", "pr_avprice", "pr_code='" + map.get("od_procode") + "'");// pr_avprice
					String insertdetail = "insert into Oapurchasedetail(od_id,od_oaid,od_detno,od_oadetno,od_oacode,od_procode,od_proname,od_prounit,od_neednumber,"
							+ "od_price,od_needdate,od_appmancode,od_appman,od_departmentcode,od_department,od_remark)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					Object[] appman = baseDao.getFieldsDataByCondition("employee", new String[] { "em_code", "em_name" },
							"em_name='" + map.get("oa_appman") + "'");
					Object[] dept = baseDao.getFieldsDataByCondition("department", new String[] { "dp_code", "dp_name" },
							"dp_name='" + map.get("oa_department") + "'");
					if (map.get("od_needdate") == null) {
						map.put("od_needdate", DateUtil.currentDateString(Constant.YMD_HMS));
					} else {
						map.put("od_needdate", map.get("od_needdate") + " 00:00:00");
					}
					sqls.add("update oaapplicationdetail set od_yqty=(nvl(od_yqty,0)+" + map.get("od_tqty") + ") where od_id="
							+ map.get("od_id"));
					baseDao.execute(insertdetail, new Object[] { baseDao.getSeqId("Oapurchasedetail_SEQ"), id, index++, map.get("od_detno"),
							map.get("oa_code"), map.get("od_procode"), map.get("od_proname"), map.get("od_prounit"), map.get("od_tqty"), price,
							Timestamp.valueOf(map.get("od_needdate") + ""), appman[0], appman[1], dept[0], dept[1], map.get("od_remark") });
				}
				Object op_currency = baseDao.getFieldDataByCondition("Vendor", "ve_currency",
						"ve_code='" + set.get(key).get(0).get("od_vendcode") + "'");
				baseDao.execute(
						insertSQL,
						new Object[] { id, code, employee.getEm_name(), employee.getEm_id(), "在录入", "ENTERING",
								Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), set.get(key).get(0).get("oa_type"),
								set.get(key).get(0).get("od_vendcode"), set.get(key).get(0).get("od_vendname"), op_currency,employee.getEm_depart(),employee.getEm_departmentcode() });
				//自动取核价单价格
				String priceError = oapurchaseDao.getPrice(id);
				baseDao.execute("update oapurchasedetail set od_rate=(select cr_taxrate from currencys left join Oapurchase on op_currency=cr_name and cr_statuscode='CANUSE' where od_oaid=op_id)"
						+ " where od_oaid=" + id);
				baseDao.execute("update oapurchase set op_rate=(select cr_taxrate from currencys where op_currency=cr_name and cr_statuscode='CANUSE')"
						+ " where op_id=" + id);
				sb.append(inde++ + ":转入成功,用品采购单号:<a href=\"javascript:openUrl('jsps/oa/appliance/oapurchase.jsp?formCondition=op_idIS"
						+ id + "&gridCondition=od_oaidIS" + id + "&whoami=Oapurchase')\">" + code + "</a>&nbsp;<hr/>");
				if(priceError!=null){
					sb.append(priceError+"<hr/>");
				}
			}
			baseDao.execute(sqls);
		}
		return sb.toString();
	}

	@Override
	public String turnPaIn(String caller, String data) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		String code = null;
		JSONObject j = null;
		for (Map<Object, Object> map : store) {
			SqlRowList rs = baseDao.queryForRowSet(
					"select pd_inoutno,pd_pdno,pd_ordercode,pd_orderdetno from prodiodetail where pd_piid=? and pd_ordercode is not null",
					map.get("pi_id"));
			while (rs.next()) {
				Double a = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select sum(nvl(pd_outqty,0)-nvl(pd_inqty,0)) qty from prodiodetail where pd_piclass in ('出货单','销售退货单') and pd_ordercode=? and pd_orderdetno=?",
								new Object[] { rs.getObject("pd_ordercode"), rs.getObject("pd_orderdetno") }, Double.class);
				Double b = baseDao.getJdbcTemplate().queryForObject("select nvl(sd_qty,0) from saledetail where sd_code=? and sd_detno=?",
						new Object[] { rs.getObject("pd_ordercode"), rs.getObject("pd_orderdetno") }, Double.class);
				if (a > b) {
					sb.append("出货单:").append(rs.getObject("pd_inoutno"));
					sb.append("序号:").append(rs.getObject("pd_pdno"));
					sb.append("总出货数:").append(a);
					sb.append("订单数:").append(b).append("<br>");
				}
			}
		}
		if (sb.length() > 0)
			BaseUtil.showError("总出货数超过订单数，不允许转发票箱单！<br>" + sb.toString());
		// 按客户,币别，抬头单位,单据类型分组
		Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupsMap(store, new Object[] { "pi_cardcode", "pi_currency", "pi_custcode2",
				"pi_class" });
		List<Map<Object, Object>> s = null;
		Object pi_id = "";// 来源ID
		for (Object m : map.keySet()) {
			if (m != null) {
				s = map.get(m);
				pi_id = s.get(0).get("pi_id");
				Object[] piid = CollectionUtil.pluck(s, "pi_id");
				String piids = BaseUtil.parseArray2Str(piid, ",");
				String inoutno = baseDao.getJdbcTemplate().queryForObject(
						"select wmsys.wm_concat(pi_inoutno) from prodinout where pi_id in (" + piids + ")", String.class);
				SqlRowList rs = baseDao
						.queryForRowSet("select count(1) from (select distinct pi_sellercode from prodinout where pi_id in (" + piids
								+ "))");
				if (rs.next() && rs.getInt(1) > 1) {
					BaseUtil.showError("选定出入库单的业务员不同,不能合并生成发票箱单!");
				}
				/*
				 * rs = baseDao .queryForRowSet(
				 * "select count(1) from (select distinct pd_whcode from prodiodetail where pd_piid in ("
				 * + piids + "))"); if (rs.next() && rs.getInt(1) > 1) {
				 * BaseUtil.showError("选定出入库单的仓库不同,不能合并生成发票箱单!"); }
				 */
				// 如果指定了发票号
				if (s.get(0).get("in_code") != null && !"".equals(s.get(0).get("in_code").toString())) {
					code = s.get(0).get("in_code").toString();
					// 当前编号的记录已经存在,不能新增!
					boolean bool = baseDao.checkByCondition("Invoice", "in_code='" + code + "'");
					if (!bool) {
						BaseUtil.showError("指定单号已经存在,请重新指定单号!");
					}
					j = invoiceDao.newPaInwithno(pi_id, inoutno, code, caller);
					if (j != null) {
						baseDao.execute("update prodinout set pi_packingcode='" + code + "', pi_invoicecode='" + code
								+ "' where pi_id in (" + piids + ")");
						invoiceDao.detailTurnPaInDetail(code, piids, j.get("in_id"), j.get("pi_id"));
						baseDao.execute("update Invoice set in_total=(select sum(id_total) from InvoiceDetail where id_inid="
								+ j.get("in_id") + ") where in_id=" + j.get("in_id"));
						baseDao.execute("update Invoice set in_totalupper=L2U(in_total),in_totalupperenhkd=L2U(in_total/(case when nvl(in_rate,0)=0 then 1 else in_rate end)) where in_id="
								+ j.get("in_id"));
						baseDao.execute("update Packing set pi_total=(select sum(pd_total) from PackingDetail where pd_piid="
								+ j.get("pi_id") + ") where pi_id=" + j.get("pi_id"));
						baseDao.execute("update Packing set pi_totalupper=L2U(pi_total),pi_totalupperenhkd=L2U(pi_total/(case when nvl(pi_rate,0)=0 then 1 else pi_rate end)) where pi_id="
								+ j.get("pi_id"));
						int count = baseDao.getCountByCondition("ProdIODetail", "pd_piid in (" + piids + ")");
						int yCount = baseDao.getCountByCondition("ProdIODetail", "pd_piid in (" + piids
								+ ") and nvl(pd_batchcode,' ')<>' '");
						int xCount = baseDao
								.getCountByCondition("ProdIODetail", "pd_piid in (" + piids + ") and nvl(pd_batchcode,' ')=' '");
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
								+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=Packing')\">" + code
								+ "</a>&nbsp;<hr>");
						sb.append("转入成功,发票单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/invoice.jsp?formCondition=in_idIS"
								+ j.get("in_id") + "&gridCondition=id_inidIS" + j.get("in_id") + "&whoami=Invoice')\">" + code
								+ "</a>&nbsp;<hr>");
					}
				} else {
					j = invoiceDao.newPaIn(pi_id, inoutno, caller);
					if (j != null) {
						code = j.getString("code");
						baseDao.execute("update prodinout set pi_packingcode='" + code + "', pi_invoicecode='" + code
								+ "' where pi_id in (" + piids + ")");
						invoiceDao.detailTurnPaInDetail(code, piids, j.get("in_id"), j.get("pi_id"));
						baseDao.execute("update Invoice set in_total=(select sum(id_total) from InvoiceDetail where id_inid="
								+ j.get("in_id") + ") where in_id=" + j.get("in_id"));
						baseDao.execute("update Invoice set in_totalupper=L2U(in_total),in_totalupperenhkd=L2U(in_total/nvl(in_rate,1)) where in_id="
								+ j.get("in_id"));
						baseDao.execute("update Packing set pi_total=(select sum(pd_total) from PackingDetail where pd_piid="
								+ j.get("pi_id") + ") where pi_id=" + j.get("pi_id"));
						baseDao.execute("update Packing set pi_totalupper=L2U(pi_total),pi_totalupperenhkd=L2U(pi_total/nvl(pi_rate,1)) where pi_id="
								+ j.get("pi_id"));
						int count = baseDao.getCountByCondition("ProdIODetail", "pd_piid in (" + piids + ")");
						int yCount = baseDao.getCountByCondition("ProdIODetail", "pd_piid in (" + piids
								+ ") and nvl(pd_batchcode,' ')<>' '");
						int xCount = baseDao
								.getCountByCondition("ProdIODetail", "pd_piid in (" + piids + ") and nvl(pd_batchcode,' ')=' '");
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
								+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=Packing')\">" + code
								+ "</a>&nbsp;<hr>");
						sb.append("转入成功,发票单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/invoice.jsp?formCondition=in_idIS"
								+ j.get("in_id") + "&gridCondition=id_inidIS" + j.get("in_id") + "&whoami=Invoice')\">" + code
								+ "</a>&nbsp;<hr>");
					}
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 销售预测单批量转销售单
	 */
	@Override
	public String turnSale(String caller, String data) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		int index = 0;
		String log2 = null;
		// 判断本次数量
		saleForecastDao.checkSFyqty(store);
		if (store.size() > 0) {
			// 指定了销售订单
			if (StringUtil.hasText(store.get(0).get("sa_code"))) {
				Object sa_code = store.get(0).get("sa_code");
				// 判断指定的销售订单状态是否[在录入]
				Object[] pu = baseDao.getFieldsDataByCondition("Sale", new String[] { "sa_statuscode", "sa_custcode", "sa_cop", "sa_id" },
						"sa_code='" + sa_code + "'");
				if (pu == null) {
					BaseUtil.showError("指定的销售订单不存在或已删除!");
				} else if (!"ENTERING".equals(String.valueOf(pu[0]))) {
					BaseUtil.showError("指定的销售订单状态不等于[在录入]!");
				} else {
					String log1 = null;
					StringBuffer sb1 = new StringBuffer();
					for (Map<Object, Object> map : store) {
						if (StringUtil.hasText(map.get("sf_custcode"))) {

							if (!pu[1].toString().equals(String.valueOf(map.get("sf_custcode")))) {
								log1 = "所选销售预测单客户：" + map.get("sf_custcode") + "，与指定销售订单的客户：" + pu[1] + "不一致!";
							}
							if (log1 != null) {
								sb1.append(log1).append("<hr>");
							}
						}
						if (StringUtil.hasText(map.get("sf_cop"))) {
							if (StringUtil.hasText(pu[2]) && !pu[2].toString().equals(String.valueOf(map.get("sf_cop")))) {
								log1 = "所选销售预测单所属公司：" + map.get("sf_cop") + "，与指定销售订单的所属公司:" + pu[2] + "不一致!";
							}
							if (log1 != null) {
								sb1.append(log1).append("<hr>");
							}
						}
					}
					if (sb1.length() > 0) {
						BaseUtil.showError(sb1.toString());
					}
				}
				// 转入明细
				transferRepository.transfer(caller, store, new Key(Integer.parseInt(pu[3].toString()), sa_code.toString()));
				log2 = "转入成功,销售单号:" + "<a href=\"javascript:openUrl('jsps/scm/sale/sale.jsp?formCondition=sa_idIS" + pu[3]
						+ "&gridCondition=sd_saidIS" + pu[3] + "&whoami=Sale')\">" + sa_code + "</a>&nbsp;<hr>";
				/*
				 * log2 = "转入成功,销售订单单号:" +
				 * "<a href=\"javascript:openUrl('jsps/scm/purchase/purchase.jsp?formCondition=pu_idIS"
				 * + pu[2] + "&gridCondition=pd_puidIS" + pu[2] + "')\">" +
				 * sa_code + "</a>&nbsp;";
				 */
				sb.append(log2).append("<hr>");
			} else {
				for (Map<Object, Object> map : store) {
					if (map.get("sd_sfid") != null) {
						baseDao.execute("update saleforecast set sf_custcode=?,sf_custname=? where nvl(sf_custcode,' ')=' ' and sf_id = ?",
								map.get("sf_custcode"), map.get("sf_custname"), map.get("sd_sfid"));
					}
				}
				String log = null;
				String sakind = String.valueOf(store.get(0).get("sa_kind"));
				Object newLCode = baseDao.getFieldDataByCondition("SaleKind", "SK_EXCODE", "SK_NAME='" + sakind + "'");
				Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(store, new Object[] { "sf_custcode", "sf_currency",
						"sf_cop" });
				// 按客户分组的转入操作
				Set<Object> mapSet = groups.keySet();
				List<Map<Object, Object>> items;
				for (Object s : mapSet) {
					items = groups.get(s);
					// 转入通知单主记录
					Integer sf_id = baseDao.getFieldValue("SaleForecastDetail", "sd_sfid", "sd_id=" + items.get(0).get("sd_id"),
							Integer.class);
					Object sa_sellercode = baseDao.getFieldDataByCondition("SALEFORECAST LEFT JOIN CUSTOMER ON SALEFORECAST.SF_CUSTCODE=CUSTOMER.CU_CODE", "cu_sellercode", "sf_id="+sf_id);
					Key key = transferRepository.transfer(caller, sf_id);
					if (key != null) {
						index++;
						int sa_id = key.getId();
						String code = key.getCode();
						if (newLCode != null) {
							code = newLCode + code;
						}
						if(baseDao.isDBSetting("Sale","addClerkCode") && StringUtil.hasText(sa_sellercode)){
							code = code + sa_sellercode.toString();
						}
						// 转入明细
						transferRepository.transfer(caller, items, key);
						baseDao.execute("update sale set sa_kind='" + sakind + "' where sa_id=" + sa_id);
						baseDao.execute("update sale set sa_code='" + code + "' where sa_id=" + sa_id);
						baseDao.execute("update saledetail set sd_code='" + code + "' where sd_said=" + sa_id);
						log = "转入成功,销售单号:" + "<a href=\"javascript:openUrl('jsps/scm/sale/sale.jsp?formCondition=sa_idIS" + sa_id
								+ "&gridCondition=sd_saidIS" + sa_id + "&whoami=Sale')\">" + code + "</a>&nbsp;<hr>";
						sb.append(index).append(": ").append(log).append("<hr>");
					}
				}
				// 修改销售单状态
				for (Map<Object, Object> map : store) {
					int sdid = Integer.parseInt(map.get("sd_id").toString());
					saleForecastDao.udpatestatus(sdid);
				}
				/* return sb.toString(); */
			}
		}
		return sb.toString();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public String cancelPurchaseNotify(String data, String condition) {
		String statuscondition = "";
		String sql = "";
		Employee employee = SystemSession.getUser();
		statuscondition = " and NVL(pn_endqty,0)<pn_qty and pn_status<>'已取消' ";
		if (data != null) {
			StringBuffer sb = new StringBuffer();
			List<Map<Object, Object>> NeedStore = BaseUtil.parseGridStoreToMaps(data);
			sb.append("(");
			for (int i = 0; i < NeedStore.size(); i++) {
				sb.append(NeedStore.get(i).get("pn_id") + ",");
			}
			String range = sb.toString().substring(0, sb.toString().length() - 1) + ")";
			String idcondition = " pn_id in " + range;
			sql = "update purchasenotify set pn_status='" + BaseUtil.getLocalMessage("CANCELED")
					+ "',pn_statuscode='CANCELED',pn_sendstatus='待上传',pn_cmdremark='" + employee.getEm_name() + "勾选取消  "
					+ DateUtil.format(new Date(), Constant.YMD_HMS) + "' where " + idcondition + statuscondition;
			if (NeedStore.size() > 0)
				baseDao.execute(sql);
		} else if (condition != null && condition.equalsIgnoreCase("ALL")) {
			condition = " pn_status='未确认' and NVL(pn_endqty,0)=0 ";
			sql = "update purchasenotify set pn_status='" + BaseUtil.getLocalMessage("CANCELED")
					+ "',pn_statuscode='CANCELED',pn_sendstatus='待上传',pn_cmdremark='" + employee.getEm_name() + "取消未确认 "
					+ DateUtil.format(new Date(), Constant.YMD_HMS) + "' where " + condition;
			baseDao.execute(sql);
		} else if (condition != null && condition.length() > 4) {
			condition = " pn_id in (select pn_id from purchasenotify left join purchasedetail on pn_pdid=pd_id left join purchase on pu_id=pd_puid left join product on pn_prodid=pr_id where "
					+ condition + ") ";
			sql = "update purchasenotify set pn_status='" + BaseUtil.getLocalMessage("CANCELED")
					+ "',pn_statuscode='CANCELED',pn_sendstatus='待上传',pn_cmdremark='" + employee.getEm_name() + "筛选结果取消 "
					+ DateUtil.format(new Date(), Constant.YMD_HMS) + "' where " + condition + statuscondition;
			baseDao.execute(sql);
		}
		return "";
	}

	@Override
	public void changePurchaseNotifyDelivery(String data, String condition, String condParams) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String delivery = null;// 全部更新成一个交期delivery
		if (StringUtil.hasText(condParams)) {
			Map<Object, Object> params = BaseUtil.parseFormStoreToMap(condParams);
			delivery = StringUtil.valueOf(params.get("pn_date"));
		}
		if (condition == null) {// 按照勾选变更
			if (maps.size() == 0) {
				BaseUtil.showError("未勾选要变更的明细！");
			} else if (delivery != null) { // 批量变更成一个交期
				Object[] idObj = CollectionUtil.pluck(maps, "pn_id");
				StringBuffer cond = new StringBuffer();
				if (idObj.length > 1000) {
					for (int i = 0, j = idObj.length, k = j / 1000; i < k; i++) {
						int l = (i + 1) * 1000 > j ? (j - i * 1000) : 1000;
						Object[] obj = new Object[l];
						System.arraycopy(idObj, i * 1000, obj, 0, l);
						if (cond.length() > 0)
							cond.append(" or ");
						cond.append("pn_id in (").append(BaseUtil.parseArray2Str(obj, ",")).append(")");
					}
				} else {
					cond.append("pn_id in (").append(BaseUtil.parseArray2Str(idObj, ",")).append(")");
				}
				// 执行批量更新
				if (cond.length() > 0) {
					baseDao.updateByCondition("PurchaseNotify", "pn_delivery=to_date('" + delivery
							+ "','yyyy-mm-dd'),pn_sendstatus='待上传'，pn_cmdremark='原交期:'||to_char(trunc(sysdate),'YYYY-MM-DD')||'"
							+ SystemSession.getUser().getEm_name() + DateUtil.format(new Date(), Constant.YMD_HMS) + "'", cond.toString());
				}
			} else {// 按照明细行填写的日期更新
				for (Map<Object, Object> map : maps) {
					baseDao.updateByCondition("PurchaseNotify", "pn_delivery=to_date('" + map.get("pn_delivery")
							+ "','yyyy-mm-dd'),pn_sendstatus='待上传'，pn_cmdremark='原交期:'||to_char(trunc(sysdate),'YYYY-MM-DD')||'"
							+ SystemSession.getUser().getEm_name() + DateUtil.format(new Date(), Constant.YMD_HMS) + "'",
							"pn_id=" + map.get("pn_id"));
				}
			}
		} else if (condition != null && !condition.equals("") && condition.length() > 4) {// 按条件变更
			if (delivery == null) {
				BaseUtil.showError("请先选择变更交期！");
			}
			Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_detailtable", "fo_detailcondition" },
					"fo_caller='PurchaseNotify!Delivery'");
			baseDao.updateByCondition("PurchaseNotify", "pn_delivery=to_date('" + delivery
					+ "','yyyy-mm-dd'),pn_sendstatus='待上传'，pn_cmdremark='原交期:'||to_char(trunc(sysdate),'YYYY-MM-DD')||'"
					+ SystemSession.getUser().getEm_name() + DateUtil.format(new Date(), Constant.YMD_HMS) + "'",
					"pn_id in (select pn_id from " + objs[0] + " where " + objs[1] + " and " + condition + ")");
		}
	}

	final static String INSERT_PURCHASENOTIFY = "insert into purchasenotify(pn_id,pn_mdid,pn_mrpcode,pn_ordercode,pn_orderdetno,pn_vendcode,pn_vendname,pn_prodcode,pn_qty,pn_delivery,pn_prodid,pn_pdid,pn_status,pn_statuscode,pn_indate,pn_inman,pn_thisqty,pn_endqty,pn_thisbpqty)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	/**
	 * 手工投放供应商送货通知
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public String newPurchaseNotify(String caller, String data) {
		StringBuffer sb = new StringBuffer();
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String ids = CollectionUtil.pluckSqlString(maps, "pd_id");
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(distinct '采购单号：'||pd_code||'序号：'||pd_detno) from purchasedetail where pd_id in ("
								+ ids
								+ ") and exists (select 1 from PurchaseChangeDetail left join PurchaseChange on pcd_pcid=pc_id where pc_purccode=pd_code and pcd_pddetno=pd_detno and pc_statuscode not in ('AUDITED','CONFIRMED') and nvl(pcd_oldqty,0)<>nvl(pcd_newqty,0))",
						String.class);
		if (dets != null) {
			BaseUtil.showError("存在采购变更单未审核，不允许投放！" + dets);
		}
		if (data != null) {
			Employee employee = SystemSession.getUser();
			List<Map<Object, Object>> NeedStore = BaseUtil.parseGridStoreToMaps(data);
			for (int i = 0; i < NeedStore.size(); i++) {
				if (NeedStore.get(i).get("pd_thisqty") == null || Double.parseDouble(NeedStore.get(i).get("pd_thisqty").toString()) <= 0) {
					continue;
				}
				SqlRowList rs = baseDao
						.queryForRowSet("SELECT pd_qty-NVL(pd_yqty,0)-NVL(v_pd_turnqty,0)-nvl(pd_frozenqty,0)-nvl(v_tcqty,0) remainqty, NVL(v_pd_turnqty,0)-nvl(v_tcqty,0) pd_turnqty,pd_id,pu_statuscode,pu_code,pd_detno,pu_vendcode,pu_vendname,pd_prodcode,pd_thisqty,pr_id FROM PurchaseDetail left join scm_purchaseturnqty_view on pd_id=v_pd_id left join purchase on pd_puid=pu_id left join product on pd_prodcode=pr_code  where pd_id="
								+ NeedStore.get(i).get("pd_id"));
				if (rs.next()) {
					if (!rs.getString("pu_statuscode").equals("AUDITED")) {
						sb.append("PO:" + rs.getString("pu_code") + "序号:" + rs.getString("pd_detno") + "不是已审核状态");
						continue;
					}
					if (rs.getDouble("remainqty") < Double.parseDouble(NeedStore.get(i).get("pd_thisqty").toString())) {
						sb.append("PO:" + rs.getString("pu_code") + "序号:" + rs.getString("pd_detno") + "投放数量大于未通知数:"
								+ rs.getString("remainqty"));
						continue;
					}
					Map<Object, Object> map = new HashMap<Object, Object>();
					map.put("pn_id", baseDao.getSeqId("PURCHASENOTIFY_SEQ"));
					map.put("pn_mrpcode", null);
					map.put("pn_mdid", 0);
					map.put("pn_ordercode", rs.getObject("pu_code"));
					map.put("pn_orderdetno", rs.getObject("pd_detno"));
					map.put("pn_vendcode", rs.getObject("pu_vendcode"));
					map.put("pn_vendname", rs.getObject("pu_vendname"));
					map.put("pn_prodcode", rs.getObject("pd_prodcode"));
					map.put("pn_qty", NeedStore.get(i).get("pd_thisqty"));
					boolean bool = baseDao.isDBSetting(caller, "newPurchaseNotifyDate");
					if (bool) {
						map.put("pn_delivery", NeedStore.get(i).get("pd_delivery") == null ? rs.getObject("pd_delivery") : NeedStore.get(i)
								.get("pd_delivery"));
					} else {
						map.put("pn_delivery", DateUtil.currentDateString(null));
					}
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
				baseDao.logger.others("投放操作", "投放成功", "Purchase", "pd_id", rs.getObject("pd_id"));
			}
		} else {
			return "";
		}
		return sb.toString();
	}

	/**
	 * 设置供应商分配比例
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public String setVendorRate(String Mode) {
		// 执行运算存储过程
		// Date setdate = BaseUtil.parseStringToDate(SetEndDate, "yyyy-MM-dd");
		String str = baseDao.callProcedure("SP_SetProdVendorRate", new Object[] { Mode });
		if (str != null && !str.trim().equals("")) {
			// 提示错误信息
			BaseUtil.showError(str);
		}
		return "";
	}

	/**
	 * 检验单批量审核
	 */
	@Override
	public void vastAuditQua(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Employee employee = SystemSession.getUser();
		String ids = "";
		for (int i = 0; i < maps.size(); i++) {
			ids = ids + maps.get(i).get("ve_id") + ",";
		}
		ids = ids.substring(0, ids.length() - 1);
		handlerService.handler(caller, "vastAudit", "before", new Object[] { ids });
		for (Map<Object, Object> map : maps) {
			Object veid = map.get("ve_id");
			Object testman = maps.get(0).get("ve_testman");
			Object veresult = maps.get(0).get("ve_result");
			Object vestatus = map.get("ved_status");
			Object ve_code = map.get("ve_code");
			if (veresult != null) {
				if ("".equals(veresult.toString())) {
					BaseUtil.showError("请先选择检验结果再进行审批");
				} else if (!"合格".equals(veresult.toString()) && !"不合格".equals(veresult.toString())) {
					BaseUtil.showError("检验结果只能是合格或者不合格，请确定后再进行审批");
				}
			}
			if (vestatus != null && ("已审核".equals(vestatus.toString()) || "已入库".equals(vestatus.toString()))) {
				BaseUtil.showError("" + ve_code + "明细状态是已审核或已入库状态,不允许批量审核");
			}
			baseDao.updateByCondition(
					"QUA_VerifyApplyDetail",
					"ve_testman=(select ve_testman from (select ve_testman from qua_verifyapplydetail where nvl(ve_testman,' ')<>' ' and vad_prodcode=(select vad_prodcode from qua_verifyapplydetail where ve_id="
							+ veid + ") order by ve_auditdate desc) where rownum<2)", "ve_id=" + veid + " and nvl(ve_testman,' ')=' '");
			Object[] status = baseDao.getFieldsDataByCondition("QUA_VerifyApplyDetail", new String[] { "ve_statuscode", "ve_code",
					"ve_testman", "vad_code", "vad_detno" }, "ve_id=" + veid);
			// 检验上传状态
			String sendStatus = baseDao.getFieldValue("VerifyApplyDetail", "vad_sendstatus", "ve_code='" + status[1] + "'", String.class);
			StateAssert.onSendingLimit(sendStatus);
			int vedid = baseDao.getSeqId("QUA_VERIFYAPPLYDETAILDET_SEQ");
			int count = baseDao.getCountByCondition("Qua_verifyapplydetaildet", "ved_veid=" + veid);
			if (count <= 0) {
				if (veresult != null && "不合格".equals(veresult.toString())) {
					baseDao.execute("INSERT INTO QUA_VerifyApplyDetailDet(ved_id,ved_veid,ved_detno,ved_ngqty,"
							+ "ved_date,ved_testman,ved_checkdate,ved_checkqty,ved_statuscode,ved_status,ved_code,ved_samplingqty)"
							+ " select " + vedid + ", ve_id, 1, vad_qty, sysdate, '" + status[2] + "', sysdate, vad_qty, 'AUDITED', '"
							+ BaseUtil.getLocalMessage("AUDITED") + "', ve_code,ve_samplingaqty from QUA_VerifyApplyDetail where ve_id="
							+ veid);
				} else {
					baseDao.execute("INSERT INTO QUA_VerifyApplyDetailDet(ved_id,ved_veid,ved_detno,ved_okqty,"
							+ "ved_date,ved_testman,ved_checkdate,ved_checkqty,ved_statuscode,ved_status,ved_code,ved_samplingqty)"
							+ " select " + vedid + ", ve_id, 1, vad_qty, sysdate, '" + status[2] + "', sysdate, vad_qty, 'AUDITED', '"
							+ BaseUtil.getLocalMessage("AUDITED") + "', ve_code,ve_samplingaqty from QUA_VerifyApplyDetail where ve_id="
							+ veid);
				}
			}
			baseDao.updateByCondition("Qua_verifyapplydetail", "ve_statuscode='AUDITED',ve_status='" + BaseUtil.getLocalMessage("AUDITED")
					+ "',ve_result='合格', ve_auditman='" + employee.getEm_name() + "',ve_auditdate=sysdate", "ve_id=" + veid);
			baseDao.updateByCondition("Qua_verifyapplydetaildet",
					"ved_statuscode='AUDITED',ved_status='" + BaseUtil.getLocalMessage("AUDITED") + "'", "ved_veid=" + veid
							+ "and ved_statuscode = 'UNAUDIT'");
			baseDao.execute("update QUA_VerifyApplyDetail set (ve_brand,ve_oldfactory,ve_factoryspec)=(select pa_brand,pa_addressmark,pa_factoryspec from (select pa_brand,pa_addressmark,pa_factoryspec from ProductApproval where nvl(pa_statuscode,' ')='AUDITED' AND NVL(pa_finalresult,' ')='合格' and (pa_prodcode,pa_providecode) IN (select vad_prodcode,vad_vendcode from qua_verifyapplydetail where ve_id="
					+ veid + ") order by pa_auditdate desc) where rownum<2) where ve_id=" + veid + " and nvl(ve_brand,' ')=' '");
			baseDao.execute("update QUA_VerifyApplyDetail set ve_manudate=(select ve_manudate from (select ve_manudate,ve_id from qua_verifyapplydetail a where nvl(ve_statuscode,' ')='AUDITED' and to_char(ve_auditdate,'yyyymmdd')=to_char(sysdate,'yyyymmdd') and exists (select 1 from qua_verifyapplydetail b where a.vad_vendcode=b.vad_vendcode and a.vad_prodcode=b.vad_prodcode and ve_id="
					+ veid
					+ ") order by ve_auditdate desc) where nvl(ve_manudate,' ')<>' ' and ve_id<> "
					+ veid
					+ " and rownum<2) where ve_id=" + veid + " and nvl(ve_manudate,' ')=' '");
			QUAVerifyApplyDetailDao.updateverifyqty(Integer.parseInt(veid.toString()));
			if (testman != null && !"".equals(testman.toString())) {
				baseDao.updateByCondition("Qua_verifyapplydetail", "ve_testman='" + testman + "'", "ve_id=" + veid);
			}
			baseDao.updateByCondition("QUA_VerifyApplyDetailDet",
					"ved_testman=(select ve_testman from qua_verifyapplydetail where ved_veid=ve_id)", "ved_veid=" + veid
							+ " and nvl(ved_testman,' ')=' '");
			// 检验单审核后把相关数据反写回收料单中
			Object[] objs = baseDao.getFieldsDataByCondition("QUA_VerifyApplyDetail", new String[] { "vad_detno", "vad_code", "vad_qty",
					"ve_samplingngjgqty" }, "ve_id=" + veid);
			Object[] qty = baseDao.getFieldsDataByCondition("QUA_VerifyApplyDetailDet", new String[] { "sum(ved_checkqty)",
					"sum(ved_okqty)", "sum(ved_ngqty)", "sum(ved_samplingqty)", "sum(ved_samplingokqty)", "sum(ved_samplingngqty)" },
					"ved_veid=" + veid);
			baseDao.updateByCondition("QUA_VerifyApplyDetail", "ve_samplingqty=" + qty[3] + ",ve_samplingokqty=" + qty[4]
					+ ",ve_samplingngqty=" + qty[5], "ve_id=" + veid);
			baseDao.updateByCondition("VerifyApplyDetail", "ve_status='" + BaseUtil.getLocalMessage("AUDITED") + "',vad_jyqty=" + qty[0]
					+ ",ve_okqty=" + qty[1] + ",ve_notokqty=" + qty[2] + ", ve_auditdate=sysdate,vad_sendstatus='待上传', ve_auditman='"
					+ SystemSession.getUser().getEm_name() + "'", "vad_code='" + objs[1] + "' and vad_detno =" + objs[0]);
		}
	}

	@Override
	public String vastPreSaleFTSaleF(String caller, String data) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		String code = null;
		Double qty = (double) 0;
		String person = null;
		String sf_sellercode = null;
		String sf_sellername = null;
		String sf_code = "";// 预测单号
		Employee employee = SystemSession.getUser();
		Map<Object, List<Map<Object, Object>>> map = null;
		if (baseDao.isDBSetting(caller, "SaleQtyMerge")) {
			map = BaseUtil.groupsMap(store, new Object[] { "sd_prodcode", "sd_startdate", "sd_enddate" });
		} else {
			// 按供应商分组
			map = BaseUtil.groupsMap(store, new Object[] { "sd_prodcode", "sd_startdate", "sd_enddate", "sf_sellercode" });
		}
		List<Map<Object, Object>> s = null;
		List<String> sqlList = new ArrayList<String>();
		int detno = 1;
		int id = 0;
		int detailid = 0;
		if (map.size() > 0) {
			id = baseDao.getSeqId("SaleForecast_SEQ");
			code = baseDao.sGetMaxNumber("SaleForecast", 2);// 预测单号
			for (Object m : map.keySet()) {
				if (m != null) {
					String[] mm = m.toString().split("#");
					s = map.get(m);
					qty = (double) 0;
					detailid = baseDao.getSeqId("SaleForecastDetail_seq");
					Map<Object, Object> codemap = new HashMap<Object, Object>();
					for (Map<Object, Object> o : s) {
						qty += Double.valueOf(o.get("sd_qty").toString());
						person = person == null ? o.get("sd_person").toString() : person;
						/*
						 * sf_sellercode = sf_sellercode == null ?
						 * o.get("sf_sellercode").toString() : sf_sellercode;
						 * sf_sellername = sf_sellername == null ?
						 * o.get("sf_sellername").toString() : sf_sellername;
						 */
						sf_sellercode = o.get("sf_sellercode") != null ? String.valueOf(o.get("sf_sellercode")) : sf_sellercode;
						sf_sellername = o.get("sf_sellername") != null ? String.valueOf(o.get("sf_sellername")) : sf_sellername;
						if (!codemap.containsKey(o.get("sf_code").toString())) {
							codemap.put(o.get("sf_code").toString(), o.get("sf_code").toString());
						}
						sqlList.add("update PreSaleForecastDetail set sd_statuscode='TURNSF',sd_status='已转销售预测', sd_sourceid='" + detailid
								+ "',sd_source='" + code + "' where sd_id='" + o.get("sd_id").toString() + "'");
					}
					int in = 1;
					for (Object o : codemap.keySet()) {
						if (in == codemap.size()) {
							sf_code = sf_code + codemap.get(o);
						} else {
							sf_code = sf_code + codemap.get(o) + ",";
						}
						in++;
					}
					sqlList.add("insert into SaleForecastDetail ( SD_ID,SD_SFID,SD_DETNO,SD_PRODCODE,SD_QTY,SD_PERSON,sd_sellercode,sd_seller,SD_NEEDDATE,SD_STARTDATE,SD_ENDDATE,SD_SOURCE,sd_sourceqty,sd_sourcedate,SD_SOURCEKIND ) values ('"
							+ detailid
							+ "','"
							+ id
							+ "',"
							+ "'"
							+ detno
							+ "','"
							+ mm[0]
							+ "','"
							+ qty
							+ "','"
							+ person
							+ "','"
							+ sf_sellercode
							+ "','"
							+ sf_sellername
							+ "',to_date('"
							+ mm[1]
							+ "','yyyy-mm-dd'),to_date('"
							+ mm[1]
							+ "','yyyy-mm-dd'),to_date('"
							+ mm[2]
							+ "','yyyy-mm-dd'),'"
							+ sf_code
							+ "','"
							+ qty
							+ "',to_date('"
							+ mm[1]
							+ "','yyyy-mm-dd'),'业务员预测')");
					sf_code = "";
					detno++;
				}
			}
			sqlList.add("insert into SaleForecast (SF_ID,SF_CODE,SF_DATE,SF_STATUS,SF_STATUSCODE,SF_USERID,SF_USERNAME) values ('" + id
					+ "','" + code + "',SYSDATE,'在录入','ENTERING','" + employee.getEm_id() + "','" + employee.getEm_name() + "')");
		}
		baseDao.execute(sqlList);
		sb.append("转入成功,销售预测单号:" + "<a href=\"javascript:openUrl('jsps/scm/sale/saleForecast.jsp?formCondition=sf_idIS" + id
				+ "&gridCondition=sd_sfidIS" + id + "')\">" + code + "</a>&nbsp;<hr>");
		return sb.toString();
	}

	@Override
	public String vastTurnQUABatch(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String qba_code = null;
		JSONObject j = null;
		String log = null;
		Object ba_id = null;
		for (Map<Object, Object> map : maps) {
			ba_id = map.get("ba_id");
			if (qba_code == null) {
				j = batchDao.turnQUABatch();
				if (j != null)
					qba_code = j.getString("qba_code");
			}
			batchDao.toAppointedQUABatch(qba_code, Integer.parseInt(ba_id.toString()));
			baseDao.logger.turn("转库存检验单", "Batch", "ba_id", ba_id);
			if (log == null) {
				log = "转入成功,库存检验单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/quaBatch.jsp?formCondition=qba_idIS"
						+ j.get("qba_id") + "&gridCondition=qbd_qbaidIS" + j.get("qba_id") + "')\">" + qba_code + "</a>&nbsp;";
			}
		}
		return log;
	}

	@Override
	public String turnBoChu(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String pi_inoutno = null;
		int pi_id = 0;
		JSONObject j = null;
		String log = null;
		Object whcode = null;
		StringBuffer sb = new StringBuffer();
		for (Map<Object, Object> map : maps) {
			int qbd_id = Integer.parseInt(map.get("qbd_id").toString());
			Double tqty = Double.parseDouble(map.get("qbd_tngqty").toString());
			Object[] qbd = baseDao.getFieldsDataByCondition("QUABatchDetail left join QUABatch on qbd_qbaid=qba_id", new String[] {
					"qba_code", "qbd_detno" }, "qbd_qbaid=" + qbd_id);
			if (qbd != null) {
				Double qty = baseDao.getFieldValue("ProdIODetail", "nvl(sum(pd_outqty),0)", "pd_qbdid=" + qbd_id + " AND pd_piclass='拨出单'",
						Double.class);
				Double aq = baseDao.getFieldValue("QUABatchDetail", "nvl(qbd_ngqty,0)", "qbd_id=" + qbd_id, Double.class);
				if (aq < qty + tqty) {
					sb.append("检验单号：").append(qbd[0]).append("序号：").append(qbd[1]).append("超出数量：").append((qty + tqty - aq)).append("<br>");
				}
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError("调拨数量超出库存检验单明细行不合格数量！" + sb.toString());
		}
		for (Map<Object, Object> map : maps) {
			int qbd_id = Integer.parseInt(map.get("qbd_id").toString());
			int qba_id = Integer.parseInt(map.get("qba_id").toString());
			Double tqty = Double.parseDouble(map.get("qbd_tngqty").toString());
			whcode = map.get("wh_code");
			if (whcode == null) {
				BaseUtil.showError("请先填写拨入仓库！");
			}
			if (pi_inoutno == null) {
				j = batchDao.turnBoChu();
				if (j != null) {
					pi_inoutno = j.getString("pi_inoutno");
					pi_id = j.getInt("pi_id");
				}
			}
			batchDao.toAppointedBoChu(pi_id, pi_inoutno, qbd_id, tqty);
			if (whcode != null) {
				baseDao.execute("update ProdInOut set pi_purpose='" + whcode + "' where pi_id=" + pi_id);
				baseDao.execute("update ProdInOut set pi_purposename=(select wh_description from warehouse where pi_purpose=wh_code) where pi_id="
						+ pi_id);
				baseDao.execute("update ProdIODetail set pd_inwhcode='" + whcode + "' where pd_piid=" + pi_id);
				baseDao.execute("update ProdIODetail set pd_inwhname=(select wh_description from warehouse where pd_inwhcode=wh_code) where pd_piid="
						+ pi_id);
			}
			baseDao.execute("update ProdIODetail set pd_whname=(select wh_description from warehouse where pd_whcode=wh_code) where pd_piid="
					+ pi_id);
			baseDao.execute("update ProdInOut set (pi_whcode,pi_whname)=(select pd_whcode,pd_whname from ProdIODetail where pd_piid=pi_id and pd_pdno=1) where pi_id="
					+ pi_id);
			baseDao.execute("update QUABatchDetail set qbd_yngqty=nvl(qbd_yngqty,0) + " + tqty + " where qbd_id=" + qbd_id);
			baseDao.logger.turn("转拨出单", "QUABatch", "qba_id", qba_id);
			if (log == null) {
				log = "转入成功,拨出单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + pi_id
						+ "&gridCondition=pd_piidIS" + pi_id + "&whoami=ProdInOut!AppropriationOut')\">" + pi_inoutno + "</a>&nbsp;<hr>";
			}
		}
		return log;
	}

	@Override
	public String vastTurnMRBStorage(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		String call = null;
		List<JSONObject> okR = null;
		List<JSONObject> ngR = null;
		List<Map<Object, Object>> ok = new ArrayList<Map<Object, Object>>();
		List<Map<Object, Object>> ng = new ArrayList<Map<Object, Object>>();
		double _okqty = 0;
		double _ngqty = 0;
		int mdid = 0;
		int isok = 0;
		int isng = 0;
		String qctype = null;
		Map<Object, Object> n = null;
		// String vedIds = CollectionUtil.pluckSqlString(maps, "md_id");
		// 转入库
		for (Map<Object, Object> m : maps) {
			mdid = Integer.parseInt(m.get("md_id").toString());
			Object[] veds = baseDao.getFieldsDataByCondition("Qua_MRBdet left join Qua_MRB on md_mrid=mr_id", new String[] { "md_okqty",
					"md_isok", "md_ngqty", "md_isng", "mr_qctype" }, "md_id=" + mdid);
			_okqty = Double.parseDouble(veds[0].toString());
			isok = Integer.parseInt(veds[1].toString());
			_ngqty = Double.parseDouble(veds[2].toString());
			isng = Integer.parseInt(veds[3].toString());
			qctype = veds[4].toString();
			if (_okqty > 0 && isok == 0) {
				m.put("qty", _okqty);
				m.put("mr_id", m.get("md_mrid"));
				m.put("md_id", m.get("md_id"));
				m.put("mr_qctype", qctype);
				ok.add(m);
			}
			if (_ngqty > 0 && isng == 0) {
				n = new HashMap<Object, Object>();
				n.put("qty", _ngqty);
				n.put("mr_id", m.get("md_mrid"));
				n.put("md_id", m.get("md_id"));
				m.put("mr_qctype", qctype);
				ng.add(n);
			}
		}
		if (ok.size() > 0 && isok == 0) {
			call = "ProdInOut!DefectOut";// 不良品出库单
			if ("采购检验单".equals(qctype)) {
				okR = QUAMRBDao.detailTurnDefectOut(call, "不良品出库单", ok, true);
			} else if ("委外检验单".equals(qctype)) {
				okR = QUAMRBDao.detailTurnDefectOut2(call, "不良品出库单", ok, true);
			}
		}
		if (ng.size() > 0 && isng == 0) {
			call = "ProdInOut!DefectOut";// 不良品出库单
			if ("采购检验单".equals(qctype)) {
				ngR = QUAMRBDao.detailTurnDefectOut(call, "不良品出库单", ng, false);
			} else if ("委外检验单".equals(qctype)) {
				ngR = QUAMRBDao.detailTurnDefectOut2(call, "不良品出库单", ng, false);
			}
		}
		if (okR != null && okR.size() > 0) {
			call = "ProdInOut!DefectOut";// 不良品出库单
			for (JSONObject j : okR) {
				baseDao.execute("UPDATE prodiodetail SET pd_location=(SELECT pr_location FROM product WHERE pd_prodcode=pr_code) where pd_piid="
						+ j.get("pi_id") + "  and  nvl(pd_location,' ')=' '");
				JSONObject o = null;
				if ("采购检验单".equals(qctype)) {
					o = QUAMRBDao.turnProdioPurc(j);
				} else if ("委外检验单".equals(qctype)) {
					o = QUAMRBDao.turnProdioMake(j);
				}
				sb.append("转入成功,不良品出库单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
						+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + call + "')\">" + j.get("pi_inoutno")
						+ "</a>&nbsp;<hr>");
				try {
					/**
					 * @author wsy 反馈编号：2017040344 良品产生的不良品出库单过账前将单据状态更新成已审核
					 */
					baseDao.updateByCondition("ProdInOut", "pi_invostatus='已审核',pi_invostatuscode='AUDITED'", "pi_id=" + j.get("pi_id"));
					prodInOutService.postProdInOut(Integer.parseInt(j.get("pi_id").toString()), call);
				} catch (SystemException e) {
					sb.append(e.getMessage()).append("<hr>");
				} catch (Exception e) {
					// TODO: handle exception
				}

				if ("采购检验单".equals(qctype)) {
					sb.append("转入成功,采购验收单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
							+ o.get("pi_id") + "&gridCondition=pd_piidIS" + o.get("pi_id") + "&whoami=ProdInOut!PurcCheckin')\">"
							+ o.get("pi_inoutno") + "</a>&nbsp;<hr>");
				} else if ("委外检验单".equals(qctype)) {
					sb.append("转入成功,委外验收单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
							+ o.get("pi_id") + "&gridCondition=pd_piidIS" + o.get("pi_id") + "&whoami=ProdInOut!OutsideCheckIn')\">"
							+ o.get("pi_inoutno") + "</a>&nbsp;<hr>");
				}
			}
		}
		if (ngR != null && ngR.size() > 0) {
			call = "ProdInOut!DefectOut";
			for (JSONObject j : ngR) {
				sb.append("转入成功,不良品出库单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
						+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + call + "')\">" + j.get("pi_inoutno")
						+ "</a>&nbsp;<hr>");
				baseDao.execute("UPDATE prodiodetail SET pd_location=(SELECT pr_location FROM product WHERE pd_prodcode=pr_code) where  pd_piid= "
						+ j.get("pi_id") + " and nvl(pd_location,' ')=' '");
			}
		}
		if (sb.length() == 0)
			return "转入失败!";
		return sb.toString();
	}

	@Override
	public String batchToCheckOut(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		int index = 0;
		if (maps.size() > 0) {
			String log = null;
			Object vendcode = maps.get(0).get("ve_code");
			Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(maps, new Object[] { "pr_manutype" });
			// 按物料数量分组的转入操作（PURCHASE,MAKE）
			Set<Object> mapSet = groups.keySet();
			List<Map<Object, Object>> items;
			for (Object s : mapSet) {
				items = groups.get(s);
				Key key = null;
				// 转入验收单主记录
				Integer ba_id = Integer.parseInt(items.get(0).get("ba_id").toString());
				if ("PURCHASE".equals(items.get(0).get("pr_manutype"))) {
					key = transferRepository.transfer("Batch!ToPurcOut!Deal", ba_id);
				} else if ("MAKE".equals(items.get(0).get("pr_manutype"))) {
					key = transferRepository.transfer("Batch!ToOSCheckReturn!Deal", ba_id);
				}
				if (key != null) {
					int pi_id = key.getId();
					index++;
					// 转入明细
					if ("PURCHASE".equals(items.get(0).get("pr_manutype"))) {
						transferRepository.transfer("Batch!ToPurcOut!Deal", items, key);
						log = "转入成功,采购验退单:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + pi_id
								+ "&gridCondition=pd_piidIS" + pi_id + "&whoami=ProdInOut!PurcCheckout')\">" + key.getCode() + "</a>";
					} else if ("MAKE".equals(items.get(0).get("pr_manutype"))) {
						transferRepository.transfer("Batch!ToOSCheckReturn!Deal", items, key);
						log = "转入成功,委外验退单:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + pi_id
								+ "&gridCondition=pd_piidIS" + pi_id + "&whoami=ProdInOut!OutesideCheckReturn')\">" + key.getCode()
								+ "</a>";
					}
					baseDao.execute("update prodinout set pi_cardcode='" + vendcode + "' where pi_id=" + pi_id);
					baseDao.execute("update prodinout set (pi_cardid,pi_title,pi_paymentcode,pi_payment,pi_receivecode,pi_receivename)=(select ve_id,ve_name,ve_paymentcode,ve_payment,ve_apvendcode,ve_apvendname from vendor where ve_code=pi_cardcode) where pi_id="
							+ pi_id);
					sb.append(index).append(": ").append(log).append("<hr>");
				}
			}
			return sb.toString();
		}
		return null;
	}

	/**
	 * 委外补料单转应付发票
	 */
	@Override
	public String vastTurnAPBill(String caller, String data) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		int index = 0;
		String log = null;
		Set<String> ids = new HashSet<String>();
		// 按委外商分组
		Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(store, new Object[] { "ma_vendcode" });
		Set<Object> mapSet = groups.keySet();
		List<Map<Object, Object>> items;
		for (Object s : mapSet) {
			items = groups.get(s);
			Integer pi_id = baseDao.getFieldValue("ProdIODetail", "pd_piid", "pd_id=" + items.get(0).get("pd_id"), Integer.class);
			Key key = transferRepository.transfer(caller, pi_id);
			if (key != null) {
				int abid = key.getId();
				ids.add(String.valueOf(abid));
				index++;
				// 转入明细
				transferRepository.transfer(caller, items, key);
				baseDao.execute("update apbill set (ab_paymentcode,ab_payments)=(select ma_paymentscode,ma_payments from make,prodiodetail,apbilldetail,apbill where abd_pdid=pd_id and abd_abid=ab_id and pd_ordercode=ma_code and abd_detno=1 and ab_id="
						+ abid + ") where ab_id=" + abid + " and nvl(ab_paymentcode,' ')=' '");
				baseDao.execute("update apbill set ab_paymentid=(select pa_id from payments where ab_paymentcode=pa_code) where ab_id="
						+ abid);
				baseDao.execute("update apbillDetail set abd_qty=abd_qty*(-1),abd_thisvoqty=abd_thisvoqty*(-1) where abd_abid=" + abid);
				baseDao.execute("update apbillDetail set abd_apamount=round(abd_thisvoprice*abd_qty,2) where abd_abid=" + abid);
				baseDao.execute("update apbillDetail set abd_amount=abd_apamount,abd_noapamount=round(abd_apamount/(1+abd_taxrate/100),2),abd_taxamount=round(abd_apamount-abd_noapamount,2) where abd_abid="
						+ abid);
				log = "转入成功,发票编号:" + "<a href=\"javascript:openUrl('jsps/fa/ars/apbill.jsp?formCondition=ab_idIS" + abid
						+ "&gridCondition=abd_abidIS" + abid + "&whoami=APBill!CWIM')\">" + key.getCode() + "</a>&nbsp;";
				sb.append(index).append(": ").append(log).append("<hr>");
			}
		}
		String abids = CollectionUtil.toSqlString(ids);
		baseDao.execute("update apbill set ab_apamount=round((select sum(abd_apamount) from apbilldetail where abd_abid=ab_id),2) where ab_id in ("
				+ abids + ")");
		return sb.toString();
	}

	@Override
	public String vastTurnBarStockProfit(String caller, String data) {
		String log = "";
		StringBuffer sb = new StringBuffer();
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(store, new Object[] { "ba_whcode" });
		if (groups.size() > 1) {
			BaseUtil.showError("不同仓库不允许生成同一张条码盘盈单");
		}
		int bsId = baseDao.getSeqId("BARSTOCKTAKING_SEQ");
		String bsCode = baseDao.sGetMaxNumber("BarStocktaking", 2);
		// 插入BarStocktaking表
		String insertSQL = "insert into BarStockTaking (bs_id,bs_code,bs_class,bs_indate,bs_inman,bs_statuscode,bs_status,bs_whcode) values("
				+ bsId
				+ ",'"
				+ bsCode
				+ "','盘盈',SYSDATE,'"
				+ SystemSession.getUser().getEm_name()
				+ "','ENTERING','在录入','"
				+ store.get(0).get("ba_whcode") + "')";
		baseDao.execute(insertSQL);
		int detno = 1;
		for (Map<Object, Object> m : store) {
			// 插入BarStocktakingDetail
			int bsd_id = baseDao.getSeqId("BARSTOCKTAKINGDETAIL_SEQ");
			baseDao.execute("insert into BarStocktakingDetail(bsd_id,bsd_bsid,bsd_detno,bsd_prodcode,bsd_batchid,bsd_batchcode,bsd_inqty,bsd_custvendcode,bsd_ordercode)"
					+ "values("
					+ bsd_id
					+ ","
					+ bsId
					+ ","
					+ detno++
					+ ",'"
					+ m.get("ba_prodcode")
					+ "',"
					+ m.get("ba_id")
					+ ",'"
					+ m.get("ba_code") + "'," + m.get("v_thisqty") + ",'" + m.get("ba_custvendcode") + "','" + m.get("ba_ordercode") + "')");
		}
		log = "转入成功,条码盘盈单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/barStockProfit.jsp?formCondition=bs_idIS" + bsId
				+ "&gridCondition=bsd_bsidIS" + bsId + "')\">" + bsCode + "</a>&nbsp;";
		sb.append(log).append("<hr>");
		return sb.toString();
	}

	@Override
	@Transactional
	public void cancelApproveNum(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		List<String> sqls = new ArrayList<String>();
		Set<String> ids = new HashSet<String>();
		for (Map<Object, Object> map : maps) {
			sqls.add("update oaapplicationdetail set od_total=nvl(od_yqty,0) where od_id=" + map.get("od_id"));
			ids.add(map.get("oa_id").toString());
		}
		for (String id : ids) {// 加入日志
			sqls.add("INSERT INTO MessageLog(ml_date,ml_man,ml_content,ml_result,ml_search) VALUES(sysdate,'"
					+ SystemSession.getUser().getEm_name() + "','修改已批准数','修改成功','Oaapplication|oa_id=" + id + "')");
		}
		baseDao.execute(sqls);
	}

	@Override
	public void changePurcNotifyQty(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		List<String> sqls = new ArrayList<String>();
		Set<String> ids = new HashSet<String>();
		for (Map<Object, Object> map : maps) {
			Object qty = map.get("pn_qty");
			Object id = map.get("pn_id");
			Object[] oldqty = baseDao.getFieldsDataByCondition(
					"PurchaseNotify left join PurchaseDetail on pd_code=pn_ordercode and pd_detno=pn_orderdetno", new String[] {
							"NVL(pn_qty,0)", "NVL(pn_endqty,0)" }, "pn_id=" + id);
			if (Double.parseDouble(qty.toString()) > Double.parseDouble(oldqty[0].toString())) {
				BaseUtil.showError("数量只能减少，不能增加！");
			}
			if (Double.parseDouble(qty.toString()) < Double.parseDouble(oldqty[1].toString())) {
				BaseUtil.showError("数量不能小于已交货数！");
			}
			sqls.add("update PurchaseNotify set pn_qty=" + qty + ",pn_sendstatus='待上传',pn_cmdremark=pn_cmdremark||'原数:'||pn_qty||'"
					+ SystemSession.getUser().getEm_name() + DateUtil.format(new Date(), Constant.YMD_HMS) + "' where pn_id=" + id);
			ids.add(id.toString());
		}
		/*
		 * for(String id:ids){//加入日志 sqls.add(
		 * "INSERT INTO MessageLog(ml_date,ml_man,ml_content,ml_result,ml_search) VALUES(sysdate,'"
		 * +SystemSession.getUser()
		 * .getEm_name()+"','修改送货提醒数量','修改成功','PurchaseNotify|pn_id="+id+"')");
		 * }
		 */
		baseDao.execute(sqls);
	}

	@Override
	public void vastUpdateProdinoutDate(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Object newdate = maps.get(0).get("pi_newdate");
		if (newdate == null || "".equals(newdate)) {
			BaseUtil.showError("变更后日期不能为空！");
		} else {
			boolean bool = baseDao.checkIf("PeriodsDetail", "pd_code='MONTH-P' and pd_status=99 and pd_detno=to_char(to_date('" + newdate
					+ "','yyyy-mm-dd'), 'yyyymm')");
			if (bool) {
				BaseUtil.showError("变更后日期所属期间已结账！");
			}
			String piclass = null;
			String picaller = null;
			for (Map<Object, Object> map : maps) {
				piclass = map.get("pi_class").toString();
				if ("采购验收单".equals(piclass)) {
					picaller = "ProdInOut!PurcCheckin";
				} else if ("采购验退单".equals(piclass)) {
					picaller = "ProdInOut!PurcCheckout";
				} else if ("其它采购入库单".equals(piclass)) {
					picaller = "ProdInOut!OtherPurcIn";
				} else if ("完工入库单".equals(piclass)) {
					picaller = "ProdInOut!Make!In";
				} else if ("其它采购出库单".equals(piclass)) {
					picaller = "ProdInOut!OtherPurcOut";
				} else if ("换货出库单".equals(piclass)) {
					picaller = "ProdInOut!ExchangeOut";
				} else if ("换货入库单".equals(piclass)) {
					picaller = "ProdInOut!ExchangeIn";
				} else if ("出货单".equals(piclass)) {
					picaller = "ProdInOut!Sale";
				} else if ("委外领料单".equals(piclass)) {
					picaller = "ProdInOut!OutsidePicking";
				} else if ("研发退料单".equals(piclass)) {
					picaller = "ProdInOut!YFIN";
				} else if ("研发领料单".equals(piclass)) {
					picaller = "ProdInOut!YFOUT";
				} else if ("辅料入库单".equals(piclass)) {
					picaller = "ProdInOut!FLIN";
				} else if ("辅料出库单".equals(piclass)) {
					picaller = "ProdInOut!FLOUT";
				} else if ("借货出货单".equals(piclass)) {
					picaller = "ProdInOut!SaleBorrow";
				} else if ("借货归还单".equals(piclass)) {
					picaller = "ProdInOut!OutReturn";
				} else if ("委外补料单".equals(piclass)) {
					picaller = "ProdInOut!OSMake!Give";
				} else if ("不良品入库单".equals(piclass)) {
					picaller = "ProdInOut!DefectIn";
				} else if ("不良品出库单".equals(piclass)) {
					picaller = "ProdInOut!DefectOut";
				} else if ("库存初始化".equals(piclass)) {
					picaller = "ProdInOut!ReserveInitialize";
				} else if ("报废单".equals(piclass)) {
					picaller = "ProdInOut!StockScrap";
				} else if ("盘亏调整单".equals(piclass)) {
					picaller = "ProdInOut!StockLoss";
				} else if ("盘盈调整单".equals(piclass)) {
					picaller = "ProdInOut!StockProfit";
				} else if ("拆件入库单".equals(piclass)) {
					picaller = "ProdInOut!PartitionStockIn";
				} else if ("其它入库单".equals(piclass)) {
					picaller = "ProdInOut!OtherIn";
				} else if ("生产领料单".equals(piclass)) {
					picaller = "ProdInOut!Picking";
				} else if ("生产退料单".equals(piclass)) {
					picaller = "ProdInOut!Make!Return";
				} else if ("销售退货单".equals(piclass)) {
					picaller = "ProdInOut!SaleReturn";
				} else if ("委外验收单".equals(piclass)) {
					picaller = "ProdInOut!OutsideCheckIn";
				} else if ("委外验退单".equals(piclass)) {
					picaller = "ProdInOut!OutesideCheckReturn";
				} else if ("委外退料单".equals(piclass)) {
					picaller = "ProdInOut!OutsideReturn";
				} else if ("拨出单".equals(piclass)) {
					picaller = "ProdInOut!AppropriationOut";
				} else if ("拨入单".equals(piclass)) {
					picaller = "ProdInOut!AppropriationIn";
				} else if ("销售拨出单".equals(piclass)) {
					picaller = "ProdInOut!SaleAppropriationOut";
				} else if ("销售拨入单".equals(piclass)) {
					picaller = "ProdInOut!SalePutIn";
				} else if ("其它出库单".equals(piclass)) {
					picaller = "ProdInOut!OtherOut";
				} else if ("生产补料单".equals(piclass)) {
					picaller = "ProdInOut!Make!Give";
				}
				Object id = map.get("pi_id");
				baseDao.execute("update ProdInOut set pi_date=to_date('" + newdate + "','yyyy-mm-dd') where pi_id=" + id
						+ " and pi_statuscode<>'POSTED'");
				baseDao.logger.others("变更单据日期", "更新成功", picaller, "pi_id", id);
			}
		}
	}

	@Override
	public void vastUpdateMakeScrapDate(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Object newdate = maps.get(0).get("ms_newdate");
		if (newdate == null || "".equals(newdate)) {
			BaseUtil.showError("变更后日期不能为空！");
		} else {
			boolean bool = baseDao.checkIf("PeriodsDetail", "pd_code='MONTH-P' and pd_status=99 and pd_detno=to_char(to_date('" + newdate
					+ "','yyyy-mm-dd'), 'yyyymm')");
			if (bool) {
				BaseUtil.showError("变更后日期所属期间已结账！");
			}
			String piclass = null;
			String picaller = null;
			for (Map<Object, Object> map : maps) {
				piclass = map.get("ms_class").toString();
				if ("生产报废单".equals(piclass)) {
					picaller = "MakeScrap";
				} else if ("委外报废单".equals(piclass)) {
					picaller = "MakeScrap!OS";
				}
				Object id = map.get("ms_id");
				baseDao.execute("update MakeScrap set ms_date=to_date('" + newdate + "','yyyy-mm-dd') where ms_id=" + id
						+ " and ms_statuscode<>'AUDITED'");
				baseDao.logger.others("变更单据日期", "更新成功", picaller, "ms_id", id);
			}
		}
	}

	@Override
	public String vastPostToAccountCenter(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String emIds = CollectionUtil.pluckSqlString(maps, "em_id");
		try {
			final Master master = SystemSession.getUser().getCurrentMaster();
			List<Employee> employees = employeeService.getEmployeesByCondition("em_id in (" + emIds + ")");
			MergeTask<String, Employee> task = new MergeTask<String, Employee>(new ICallable<String, Employee>() {

				@Override
				public String call(Employee employee) throws Exception {
					String err = employeeService.postToAccountCenter(employee, master);
					return err == null ? null : "【" + employee.getEm_name() + "】：" + err;
				}

			});
			for (Employee employee : employees) {
				task.join(employee);
			}
			List<String> errs = task.execute();
			// 非saas
			if (BaseUtil.getXmlSetting("saas.domain") == null) {
				// 不考虑使用同步公式同步
				List<Master> masters = enterpriseService.getMasters();
				List<String> sqls = new ArrayList<String>();
				for (Master m : masters) {
					if (!m.equals(master)) {
						sqls.add("update " + m.getMa_user()
								+ ".employee a set (em_b2benable,em_imid,em_uu)=(select b.em_b2benable,b.em_imid,b.em_uu from "
								+ master.getMa_user() + ".employee b where b.em_id in (" + emIds
								+ ") and b.em_code=a.em_code and b.em_uu is not null) where a.em_code in (select em_code from "
								+ master.getMa_user() + ".employee where em_id in (" + emIds + ") and em_uu is not null) ");
					}
				}
				baseDao.execute(sqls);
			}
			if (!CollectionUtils.isEmpty(errs)) {
				return "以下用户的云账户操作失败：<br>" + CollectionUtil.toString(errs, "<br>");
			}
			// 记录日志
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < employees.size(); i++) {
				if (i == 0) {
					sb.append(employees.get(i).getEm_code() + ",");
				} else if (i % 10 != 0) {
					sb.append(employees.get(i).getEm_code() + ",");
				} else {
					baseDao.execute("INSERT INTO MessageLog(ml_id,ml_date,ml_man,ml_content,ml_result) "
							+ "values(MessageLog_seq.nextval,sysdate,'" + SystemSession.getUser().getEm_name() + "("
							+ SystemSession.getUser().getEm_code() + ")','手机App批量开通,员工编号:"
							+ sb.toString().substring(0, sb.toString().length() - 1) + "','批量开通成功')");
					sb.setLength(0);
					sb.append(employees.get(i).getEm_code() + ",");
				}
			}
			if (sb.length() > 0) {
				baseDao.execute("INSERT INTO MessageLog(ml_id,ml_date,ml_man,ml_content,ml_result) "
						+ "values(MessageLog_seq.nextval,sysdate,'" + SystemSession.getUser().getEm_name() + "("
						+ SystemSession.getUser().getEm_code() + ")','手机App批量开通,员工编号:"
						+ sb.toString().substring(0, sb.toString().length() - 1) + "','批量开通成功')");
			}
		} catch (EmptyResultDataAccessException e) {

		}
		return null;
	}

	static final String TEL_REGEXP = "^((\\(\\d{3}\\))|(\\d{3}\\-))?(13|15|17|18)\\d{9}$";

	public String checkEmployee(String condition) {
		String res = null;
		List<Object[]> objs = baseDao.getFieldsDatasByCondition("employee", new String[] { "em_name", "em_email", "em_mobile" }, condition);
		if (objs != null) {
			for (Object[] os : objs) {
				if (os[2] != null && (os[2].toString()).matches(TEL_REGEXP)) {
					int count = baseDao.getCount("select count(*) from employee where em_mobile='" + os[2] + "'");
					if (count > 1) {
						res = os[0] + "的手机号码在ERP中重复（包含离职的人事信息）！";
					} else {
						count = baseDao.getCount("select count(*) from employee where em_mobile='" + os[1] + "'");
						if (count > 1) {
							res = os[0] + "的邮箱地址在ERP中重复（包含离职的人事信息）！";
						}
					}
				} else {
					res = os[0] + "的手机号码格式不正确！";
				}
			}
		}
		return res;
	}

	@Override
	public String vastToQuotation(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		int index = 0;
		if (maps.size() > 0) {
			String log = null;
			Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(maps, new Object[] { "ev_sellercode", "ev_department",
					"ev_custcode" });
			Set<Object> mapSet = groups.keySet();
			List<Map<Object, Object>> items;
			for (Object s : mapSet) {
				items = groups.get(s);
				// 转入通知单主记录
				Integer ev_id = Integer.parseInt(items.get(0).get("ev_id").toString());
				Key key = transferRepository.transfer("Evaluation", ev_id);
				if (key != null) {
					int qu_id = key.getId();
					baseDao.execute("update quotation set qu_sellerid=(select qu_id from employee where em_code=qu_sellercode) where qu_id="
							+ qu_id);
					index++;
					// 转入明细
					transferRepository.transfer("Evaluation", items, key);
					log = "转入成功,报价单号:" + "<a href=\"javascript:openUrl('jsps/scm/sale/quotation.jsp?formCondition=qu_idIS" + qu_id
							+ "&gridCondition=qd_quidIS" + qu_id + "')\">" + key.getCode() + "</a>";
					sb.append(index).append(": ").append(log).append("<hr>");
				}
			}
			return sb.toString();
		}
		return null;
	}

	@Override
	public String vastOpenVendorUU(String caller, String data) {
		StringBuffer log = new StringBuffer();
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.getMa_b2bwebsite() != null) {
			List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
			List<String> sqls = new ArrayList<String>();
			for (Map<Object, Object> m : maps) {
				if (m.get("ve_name") != null) {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("data", m.get("ve_name").toString());
					try {
						Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/public/queriable/batch/members", params,
								true);
						if (response.getStatusCode() == HttpStatus.OK.value()) {
							Map<String, Object> backInfo = FlexJsonUtil.fromJson(response.getResponseText(), HashMap.class);
							if (backInfo.size() > 0) {
								for (String name : backInfo.keySet()) {
									@SuppressWarnings("unchecked")
									Map<String, Object> map = (Map<String, Object>) backInfo.get(name);
									Object ve_uu = map.get("uu");
									Object ve_webserver = map.get("businessCode");
									Object ve_legalman = map.get("corporation");
									Object ve_add1 = map.get("address");
									if (ve_uu != null && !"".equals(ve_uu.toString())) {
										int count = baseDao.getCount("select count(*) from vendor where nvl(ve_webserver,'')='"
												+ ve_webserver + "' and nvl(ve_uu,' ')='" + ve_uu + "'");
										if (count == 0) {
											sqls.add("update vendor set ve_emailkf='已获取', ve_uu=" + ve_uu
													+ ",ve_b2benable=1,ve_webserver='" + ve_webserver + "',ve_legalman='" + ve_legalman
													+ "',ve_add1='" + ve_add1 + "' where ve_name='" + name + "'");
										}
									}
								}
							} else {
								log.append(m.get("ve_name") + ",");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
			if (log.length() > 1) {
				log.append("更新失败");
			}
			if (sqls.size() > 0) {
				baseDao.execute(sqls);
			}
		}
		return log.toString();
	}

	@Override
	public String vastOpenCustUU(String caller, String data) {
		StringBuffer log = new StringBuffer();
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.getMa_b2bwebsite() != null) {
			List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
			List<String> sqls = new ArrayList<String>();
			for (Map<Object, Object> m : maps) {
				if (m.get("cu_name") != null) {
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("data", m.get("cu_name").toString());
					try {
						Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/public/queriable/batch/members", params,
								true);
						if (response.getStatusCode() == HttpStatus.OK.value()) {
							Map<String, Object> backInfo = FlexJsonUtil.fromJson(response.getResponseText(), HashMap.class);
							if (backInfo.size() > 0) {
								for (String name : backInfo.keySet()) {
									@SuppressWarnings("unchecked")
									Map<String, Object> map = (Map<String, Object>) backInfo.get(name);
									Object cu_uu = map.get("uu");
									Object cu_businesscode = map.get("businessCode");
									Object cu_lawman = map.get("corporation");
									Object cu_add1 = map.get("address");
									if (cu_uu != null && !"".equals(cu_uu.toString())) {
										int count = baseDao.getCount("select count(*) from customer where nvl(cu_businesscode,'')='"
												+ cu_businesscode + "' and nvl(cu_uu,' ')='" + cu_uu + "'");
										if (count == 0) {
											sqls.add("update Customer set cu_checkuustatus='已获取', cu_uu=" + cu_uu
													+ ",cu_b2benable=1,cu_businesscode='" + cu_businesscode + "',cu_lawman='" + cu_lawman
													+ "',cu_add1='" + cu_add1 + "' where ve_name='" + name + "'");
										}
									}
								}
							} else {
								log.append(m.get("cu_name") + ",");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
			if (log.length() > 1) {
				log.append("更新失败");
			}
			if (sqls.size() > 0) {
				baseDao.execute(sqls);
			}
		}
		return log.toString();
	}

	@Override
	public void vastLockBatch(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> map : maps) {
			Object id = map.get("ba_id");
			baseDao.execute("update Batch set ba_kind=-1 where ba_id=" + id + " and nvl(ba_remain,0)>0 and NVL(ba_kind,0)=0");
			baseDao.logger.others("批号锁库", "锁定成功", "Batch", "ba_id", id);
		}
	}

	@Override
	public void vastUnLockBatch(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Object adddays = maps.get(0).get("adddays");
		if (adddays == null || "".equals(adddays)) {
			BaseUtil.showError("有效期延长天数不能为空！");
		}
		for (Map<Object, Object> map : maps) {
			Object id = map.get("ba_id");
			baseDao.execute("update batch set BA_VALIDTIME=NVL(BA_VALIDTIME,ba_date)+" + adddays
					+ ",ba_kind=case when trunc(NVL(BA_VALIDTIME,ba_date)+" + adddays + ")>trunc(sysdate) then 0 else -1 end where ba_id="
					+ id);
			boolean bool = baseDao.checkIf("Batch", "ba_id=" + id + " and nvl(ba_kind,0)=0");
			if (bool) {
				baseDao.logger.others("批号解锁", "解锁成功", "Batch", "ba_id", id);
			}
		}
	}

	@Override
	public void vastCloseMRPProdio(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> map : maps) {
			Object id = map.get("pd_id");
			Object[] pi = baseDao.getFieldsDataByCondition("ProdioDetail", new String[] { "pd_piid", "pd_pdno", "pd_piclass" }, "pd_id="
					+ id);
			String picaller = null;
			if ("其它采购入库单".equals(pi[2].toString())) {
				picaller = "ProdInOut!OtherPurcIn";
			}
			if ("借货出货单".equals(pi[2].toString())) {
				picaller = "ProdInOut!SaleBorrow";
			}
			baseDao.execute("update ProdIODetail set pd_mrpclosed=-1 where pd_id=" + id);
			baseDao.logger.others("MRP关闭", "行" + pi[1], picaller, "pi_id", pi[0]);
		}
	}

	@Override
	public void vastOpenMRPProdio(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> map : maps) {
			Object id = map.get("pd_id");
			Object[] pi = baseDao.getFieldsDataByCondition("ProdioDetail", new String[] { "pd_piid", "pd_pdno", "pd_piclass" }, "pd_id="
					+ id);
			String picaller = null;
			if ("其它采购入库单".equals(pi[2].toString())) {
				picaller = "ProdInOut!OtherPurcIn";
			}
			if ("借货出货单".equals(pi[2].toString())) {
				picaller = "ProdInOut!SaleBorrow";
			}
			baseDao.execute("update ProdIODetail set pd_mrpclosed=0 where pd_id=" + id);
			baseDao.logger.others("MRP打开", "行" + pi[1], picaller, "pi_id", pi[0]);
		}
	}

	@Override
	public String VastTurnJobDuty(String caller, String data) {

		StringBuffer sb = new StringBuffer();
		Employee employee = SystemSession.getUser();
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		Object ew_defaulthscode = store.get(0).get("ew_defaulthscode");
		if (ew_defaulthscode == null || "".equals(ew_defaulthscode)) {
			BaseUtil.showError("岗位编号不能为空！");
		}

		int jwId = baseDao.getSeqId("JobWork_SEQ");
		String jbCode = baseDao.sGetMaxNumber("JobWork", 2);
		// 插入JobWork表
		Object jw_id = baseDao.getFieldDataByCondition("JobWork", "jw_id", "jw_defaulthscode=" + ew_defaulthscode);
		Object jw_code = baseDao.getFieldDataByCondition("JobWork", "jw_code", "jw_defaulthscode=" + ew_defaulthscode);

		if (jw_id == null || jw_id.equals("0") || jw_id.equals(null)) {
			String insertSQL = "insert into JobWork (jw_id,jw_code,jw_statuscode,jw_status,jw_recorder,jw_recorddate,jw_defaulthscode) values("
					+ jwId
					+ ",'"
					+ jbCode
					+ "','ENTERING','在录入','"
					+ employee.getEm_name()
					+ "',"
					+ DateUtil.parseDateToOracleString(null, new Date()) + ",'" + ew_defaulthscode + "')";
			baseDao.execute(insertSQL);
			baseDao.execute("update JobWork set jw_position=(select distinct em_position from employee where em_defaulthscode='"
					+ ew_defaulthscode + "') where jw_id=" + jwId);
			int detno = 1;
			for (Map<Object, Object> m : store) {
				// 插入JobWorkDetail
				int jbwid = baseDao.getSeqId("JobWorkDetail_SEQ");
				Object[] datas = baseDao.getFieldsDataByCondition("EmpWorkDetail", new String[] { "ewd_worktype", "ewd_jobduty",
						"ewd_jobcontent", "ewd_worktime" }, "ewd_id=" + m.get("ewd_id"));
				baseDao.execute("insert into JobWorkDetail(jwd_id,jwd_jwid,jwd_detno,jwd_worktype,jwd_jobduty,jwd_jobcontent,jwd_worktime)"
						+ "values(" + jbwid + "," + jwId + "," + detno++ + ",'" + datas[0] + "','" + datas[1] + "','" + datas[2] + "',"
						+ datas[3] + ")");
			}
			sb.append("转入成功,岗位工作内容单号:" + "<a href=\"javascript:openUrl('jsps/common/commonpage.jsp?whoami=JobWork&formCondition=jw_idIS"
					+ jwId + "&gridCondition=jwd_jwidIS" + jwId + "')\">" + jbCode + "</a>&nbsp;<hr>");
		} else {
			Object jwd_detno = baseDao.getFieldDataByCondition("JobWorkDetail", "max(jwd_detno)+1", "jwd_jwid=" + jw_id);
			int detno = Integer.parseInt(jwd_detno.toString());
			for (Map<Object, Object> m : store) {
				// 插入JobWorkDetail
				int jbwid = baseDao.getSeqId("JobWorkDetail_SEQ");
				Object[] datas = baseDao.getFieldsDataByCondition("EmpWorkDetail", new String[] { "ewd_worktype", "ewd_jobduty",
						"ewd_jobcontent", "ewd_worktime" }, "ewd_id=" + m.get("ewd_id"));
				baseDao.execute("insert into JobWorkDetail(jwd_id,jwd_jwid,jwd_detno,jwd_worktype,jwd_jobduty,jwd_jobcontent,jwd_worktime)"
						+ "values(" + jbwid + "," + jw_id + "," + detno++ + ",'" + datas[0] + "','" + datas[1] + "','" + datas[2] + "',"
						+ datas[3] + ")");
			}
			sb.append("转入成功,岗位工作内容单号:" + "<a href=\"javascript:openUrl('jsps/common/commonpage.jsp?whoami=JobWork&formCondition=jw_idIS"
					+ jw_id + "&gridCondition=jwd_jwidIS" + jw_id + "')\">" + jw_code + "</a>&nbsp;<hr>");
		}
		return sb.toString();
	}

	/**
	 * 借货出货单转续借申请单
	 * 
	 * @author mad
	 */
	@Override
	@Transactional
	public String turnRenewApply(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String ids = CollectionUtil.pluckSqlString(maps, "pd_id");
		// 存在未审批变更单
		String codes = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(ra_code) from (select distinct ra_code from renewapply left join renewapplydetail on ra_id=rad_raid where rad_pdid in("
						+ ids + ") and ra_statuscode<>'AUDITED')", String.class);
		if (codes != null) {
			BaseUtil.showError("存在待审批的续借申请单，不能进行转出操作!续借申请单号：" + codes);
		}
		// 转入通知单主记录
		Integer pi_id = baseDao.getFieldValue("ProdIODetail", "pd_piid", "pd_id=" + maps.get(0).get("pd_id"), Integer.class);
		Key key = transferRepository.transfer(caller, pi_id);
		int raid = key.getId();
		// 转入明细
		transferRepository.transfer(caller, maps, key);
		baseDao.execute("update renewapply set ra_sellerid=(select em_id from employee where em_code=ra_sellercode) where ra_id=" + raid);
		baseDao.execute("update renewapplydetail set RAD_AMOUNT=round(rad_qty*rad_price,2) where rad_raid=" + raid);
		return "转入成功,续借申请单号:" + "<a href=\"javascript:openUrl('jsps/scm/sale/renewApply.jsp?formCondition=ra_idIS" + raid
				+ "&gridCondition=rad_raidIS" + raid + "')\">" + key.getCode() + "</a>&nbsp;";
	}

	@Override
	public void vastAbateSalePrice(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> map : maps) {
			Object id = map.get("spd_id");
			Object[] sp = baseDao.getFieldsDataByCondition("SalePriceDetail", new String[] { "spd_spid", "spd_detno" }, "spd_id=" + id);
			baseDao.updateByCondition("SalePriceDetail", "spd_statuscode='UNVALID', spd_status='" + BaseUtil.getLocalMessage("UNVALID")
					+ "'", "spd_id=" + id);
			baseDao.logger.others("转失效", "行" + sp[1], "SalePrice", "sp_id", sp[0]);
		}
	}

	@Override
	public void vastResabateSalePrice(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> map : maps) {
			Object id = map.get("spd_id");
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(distinct sp_code) from SalePrice, SalePriceDetail where sp_id=spd_spid and spd_id=? and to_char(sp_todate,'yyyymmdd')<to_char(sysdate,'yyyymmdd')",
							String.class, id);
			if (dets != null) {
				BaseUtil.showError("单据有效截止日期已过期，不允许进行转有效操作！单据编号：" + dets);
			}
			Object[] sp = baseDao.getFieldsDataByCondition("SalePriceDetail", new String[] { "spd_spid", "spd_detno" }, "spd_id=" + id);
			baseDao.updateByCondition("SalePriceDetail", "spd_statuscode='VALID', spd_status='" + BaseUtil.getLocalMessage("VALID") + "'",
					"spd_id=" + id);
			baseDao.logger.others("转有效", "行" + sp[1], "SalePrice", "sp_id", sp[0]);
		}
	}

	@Override
	public void vastAbateProductRate(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> map : maps) {
			Object id = map.get("pdrd_id");
			Object[] sp = baseDao.getFieldsDataByCondition("ProductRateDetail", new String[] { "pdrd_pdrid", "pdrd_detno" }, "pdrd_id="
					+ id);
			baseDao.updateByCondition("ProductRateDetail", "pdrd_statuscode='UNVALID', pdrd_status='" + BaseUtil.getLocalMessage("UNVALID")
					+ "'", "pdrd_id=" + id);
			baseDao.logger.others("转失效", "行" + sp[1], "ProductRate", "pdr_id", sp[0]);
		}
	}

	@Override
	public void vastResabateProductRate(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> map : maps) {
			Object id = map.get("pdrd_id");
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(pdrd_detno) from ProductRateDetail where pdrd_pdrid=? and to_char(pdrd_enddate,'yyyymmdd')<to_char(sysdate,'yyyymmdd')",
							String.class, id);
			if (dets != null) {
				BaseUtil.showError("结束时间已过期，不允许进行转有效操作！行号：" + dets);
			}
			Object[] sp = baseDao.getFieldsDataByCondition("ProductRateDetail", new String[] { "pdrd_pdrid", "pdrd_detno" }, "pdrd_id="
					+ id);
			baseDao.updateByCondition("ProductRateDetail", "pdrd_statuscode='VALID', pdrd_status='" + BaseUtil.getLocalMessage("VALID")
					+ "'", "pdrd_id=" + id);
			baseDao.logger.others("转有效", "行" + sp[1], "ProductRate", "pdr_id", sp[0]);
		}
	}

	@Override
	public void prodInOutPost(String caller, String from, String to, String pclass) {
		StringBuffer sb = new StringBuffer();
		if (from == null) {
			BaseUtil.showError("请选定起始日期！");
		}
		if (to == null) {
			BaseUtil.showError("请选定截止日期！");
		}
		if (pclass == null || "".equals(pclass)) {
			BaseUtil.showError("请选定单据类型！");
		}
		int count = 0;
		SqlRowList rs = baseDao
				.queryForRowSet("select pi_id,ds_table,pi_inoutno,pi_date from prodinout,documentsetup where NVL(pi_statuscode,' ')='UNPOST' AND ds_name=pi_class and pi_class='"
						+ pclass
						+ "' and pi_date - 1 < to_date('"
						+ to
						+ "','yyyy-mm-dd hh24:mi:ss') and pi_date+1>to_date('"
						+ from
						+ "','yyyy-mm-dd hh24:mi:ss') and pi_class<>'拨入单' and rownum<=100");
		while (rs.next()) {
			try {
				prodInOutService.postProdInOut(rs.getGeneralInt("pi_id"), rs.getString("ds_table"));
				count++;
			} catch (Exception e) {
				sb.append(rs.getObject("pi_inoutno") + "</a>&nbsp;");
			}
		}
		if (count > 0) {
			baseDao.logger.others(pclass + "批量过账", "批量过账成功" + count + "条", "ProdInOutPost", "id", 1);
		}
		if (sb.length() > 0) {
			BaseUtil.appendError(sb.toString() + "过账失败！");
		}
	}

	/**
	 * wusy 转预测调整单
	 */
	@Override
	public String vastTurnForecastAdjust(String caller, String data) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		Map<Object, Object> firstData = store.get(0);
		Object[] obs = baseDao.getFieldsDataByCondition("PRESALEFORECASTDETAIL LEFT JOIN PRESALEFORECAST ON SF_ID=SD_SFID", new String[] {
				"sf_sellercode", "sf_sellername" }, "sd_id=" + firstData.get("sd_id"));
		StringBuffer sb = new StringBuffer();
		Employee employee = SystemSession.getUser();
		int detno = 1;
		int id = baseDao.getSeqId("PREFORECASTCLASH_SEQ");
		String code = baseDao.sGetMaxNumber("PreForecastClash", 2);
		for (Map<Object, Object> map : store) {
			int detailid = baseDao.getSeqId("PREFORECASTCLASHDETAIL_SEQ");
			Object qty = map.get("sd_qty");
			Object[] objs = baseDao.getFieldsDataByCondition("PreSaleForecastDetail", new String[] { "sd_prodcode", "sd_sourceqty",
					"sd_qty" }, "sd_id=" + map.get("sd_id"));
			String sql = "insert into PreForecastClashDetail(pfd_id,pfd_pfcid,pfd_detno,pfd_prodcode,pfd_sourceqty,pfd_oldqty,pfd_qty,pfd_sdid) "
					+ "values('"
					+ detailid
					+ "','"
					+ id
					+ "','"
					+ detno
					+ "','"
					+ objs[0]
					+ "',nvl("
					+ objs[1]
					+ ",0),'"
					+ objs[2]
					+ "','"
					+ qty + "'," + map.get("sd_id") + ") ";

			baseDao.execute(sql);
			detno++;
		}
		String sql2 = "insert into PreForecastClash(pfc_id,pfc_code,pfc_date,pfc_recorder,pfc_status,pfc_statuscode,pfc_sellercode,pfc_sellername) values('"
				+ id + "','" + code + "',SYSDATE,'" + employee.getEm_name() + "','在录入','ENTERING','" + obs[0] + "','" + obs[1] + "')";
		// baseDao.updateByCondition("PreForecastClash",
		// "pfc_sellercode='"+obs[0]+"',pfc_sellername='"+obs[1]+"'",
		// "pfc_id="+id);
		baseDao.execute(sql2);
		sb.append("转入成功,业务员预测调整单号:" + "<a href=\"javascript:openUrl('jsps/scm/sale/preForecastClash.jsp?formCondition=pfc_idIS" + id
				+ "&gridCondition=pfd_pfcidIS" + id + "')\">" + code + "</a>&nbsp;<hr>");
		return sb.toString();
	}

	@Override
	public String vastProdIOin2out(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		if (maps.size() > 0) {
			String cal = null;
			String type = null;
			String piclass = null;
			int sign = 0;
			Map<Object, List<Map<Object, Object>>> groups = new HashMap<Object, List<Map<Object, Object>>>();
			// 针对不良品入库转出库增加 按照同供应商 或者 同采购单进行批量转不良品出库单 maz 2018010441
			// 默认按单据分组pi_inoutno
			if ("ProdInOut!DefectIn!ToOut!Deal".equals(caller)) {
				cal = "ProdIN!ToProdDefectOut!Deal";
				type = "ProdInOut!DefectOut";
				piclass = "不良品出库单";
				if (baseDao.isDBSetting(caller, "turnMergeVendor") && baseDao.isDBSetting(caller, "turnMergePurchase")) {
					sign = 1;
				} else if (baseDao.isDBSetting(caller, "turnMergeVendor")) {
					sign = 1;
				} else if (baseDao.isDBSetting(caller, "turnMergePurchase")) {
					sign = 2;
				}
			}
			if (sign == 1) {
				groups = BaseUtil.groupsMap(maps, new Object[] { "pi_cardcode" });
			} else if (sign == 2) {
				groups = BaseUtil.groupsMap(maps, new Object[] { "pd_ordercode" });
			} else {
				groups = BaseUtil.groupsMap(maps, new Object[] { "pd_piid" });
			}
			Set<Object> mapSet = groups.keySet();
			List<Map<Object, Object>> items;
			for (Object s : mapSet) {
				items = groups.get(s);
				Object y = 0;
				SqlRowList rs = null;
				StringBuffer sb1 = new StringBuffer();
				String sourcecode = "";
				for (Map<Object, Object> map : items) {
					int pdid = Integer.parseInt(map.get("pd_id").toString());
					double tqty = Double.parseDouble(map.get("pd_tqty").toString());
					y = baseDao.getFieldDataByCondition("ProdIODetail", "sum(nvl(pd_outqty,0))", "pd_ioid=" + pdid);
					y = y == null ? 0 : y;
					rs = baseDao.queryForRowSet("SELECT pd_inoutno,pd_pdno,pd_inqty FROM ProdIODetail WHERE pd_id=? and pd_inqty<?", pdid,
							Double.parseDouble(y.toString()) + tqty);
					if (rs.next()) {
						sb1 = new StringBuffer("[本次数量填写超出可转数量],入库单号:").append(rs.getString("pd_inoutno")).append(",行号:")
								.append(rs.getInt("pd_pdno")).append(",入库数量:").append(rs.getDouble("pd_inqty")).append(",已转数:").append(y)
								.append(",本次数:").append(tqty).append("<hr/>");
					}
					if (sign != 0) {
						Object source = baseDao.getFieldDataByCondition("ProdInOut", "pi_inoutno", "pi_id=" + map.get("pd_piid"));
						if (!sourcecode.contains(source.toString())) {
							sourcecode = sourcecode + source.toString() + "#";
						}
					}
				}
				if (sourcecode != "") {
					sourcecode = sourcecode.substring(0, sourcecode.length() - 1);
				}
				if (sb1.length() > 0) {
					BaseUtil.showError(sb1.toString());
				}
				if (items.size() > 0) {
					Object pi_id = items.get(0).get("pd_piid");
					JSONObject j = prodInOutDao.newProdDefectOut(Integer.parseInt(pi_id.toString()), piclass, type);
					int detno = 1;
					if (j != null) {
						String pi_inoutno = j.getString("pi_inoutno");
						int piid = j.getInt("pi_id");
						if ("ProdInOut!Sale".equals(type)) {
							baseDao.execute("update prodinout set (pi_arcode, pi_arname, pi_receivecode, pi_receivename, pi_paymentcode, pi_payment)=(select cu_arcode, cu_arname, cu_shcustcode, cu_shcustname, cu_paymentscode, cu_payments from customer where cu_code=pi_cardcode) where pi_id="
									+ piid);
							baseDao.execute(
									"update ProdInOut set pi_address=(select cu_add1 from Customer where cu_code=pi_cardcode) where pi_id=? and nvl(pi_address,' ')=' '",
									piid);
							baseDao.execute(
									"update ProdInOut set (pi_purposename,pi_expresscode)=(select ca_person,ca_phone from CustomerAddress left join customer on ca_cuid=cu_id  where cu_code=pi_cardcode and ca_remark='是')  where pi_id=?",
									piid);
							baseDao.execute(
									"update ProdInOut set pi_rate=nvl((select cm_crrate from currencysmonth where cm_yearmonth=to_char(pi_date,'yyyymm') and cm_crname=pi_currency),1) where pi_id=? and nvl(pi_currency,' ')<>' '",
									piid);
						}
						if ("ProdInOut!OtherOut".equals(type)) {
							baseDao.execute("update ProdInOut set pi_type=null where pi_id=?", piid);
						}
						for (Map<Object, Object> map : items) {
							if (pi_inoutno != null) {
								int pdid = Integer.parseInt(map.get("pd_id").toString());
								double tqty = Double.parseDouble(map.get("pd_tqty").toString());
								if ("ProdInOut!AppropriationOut".equals(type)) {
									prodInOutDao.toAppointedAppropriationOut(piid, pdid, tqty, detno++);
								} else if ("ProdInOut!OtherOut".equals(type) || "ProdInOut!CustReturnOut".equals(type)
										|| "ProdInOut!OtherPurcOut".equals(type)) {
									prodInOutDao.toAppointedProdOtherOut(piid, pdid, tqty, detno++);
								} else if ("ProdInOut!Sale".equals(type)) {
									prodInOutDao.toAppointedProdSaleOut(piid, pdid, tqty, detno++);
								} else {
									prodInOutDao.toAppointedProdDefectOut(piid, pdid, tqty, detno++);
								}
								// 修改ProdInOutDetail状态
								baseDao.updateByCondition("ProdIODetail", "pd_yqty=nvl(pd_yqty,0)+" + tqty, "pd_id=" + pdid);
								// 记录日志
								Object[] cts = baseDao.getFieldsDataByCondition("ProdIODetail", "pd_piid,pd_pdno", "pd_id=" + pdid);
								if ("ProdIN!ToProdOtherOut!Deal".equals(cal) && "ProdInOut!OtherOut".equals(type)) {
									baseDao.logger.turnDetail("msg.turnProdIO!OtherOut", "ProdInOut!OtherIn", "pi_id", cts[0], cts[1]
											+ ",数量：" + tqty);
								} else if ("ProdIN!ToProdOtherPurcOut!Deal".equals(cal) && "ProdInOut!OtherPurcOut".equals(type)) {
									baseDao.logger.turnDetail("msg.turnProdIO!OtherPurcOut", "ProdInOut!OtherPurcIn", "pi_id", cts[0],
											cts[1] + ",数量：" + tqty);
								} else if ("ProdIN!ToProdPurcOut!Deal".equals(cal) && "ProdInOut!PurcCheckout".equals(type)) {
									// 生成日志
									baseDao.logger.turnDetail("转" + piclass, "ProdInOut!PurcCheckin", "pi_id", cts[0], cts[1] + ",数量："
											+ tqty);
								} else if ("ProdIN!ToProdDefectOut!Deal".equals(cal) && "ProdInOut!DefectOut".equals(type)) {
									// 更新不良品出库单来源单号 2018010441
									if (sourcecode != "") {
										baseDao.updateByCondition("PRODINOUT", "pi_sourcecode='" + sourcecode + "'", "pi_id=" + piid);
									}
									// 记录日志
									baseDao.logger.turnDetail("转" + piclass, "ProdInOut!DefectIn", "pi_id", cts[0], cts[1] + ",数量：" + tqty);
								} else {
									baseDao.logger.turnDetail("msg.turnProdIO", caller, "pi_id", cts[0], cts[1]);
								}
							}
						}
						baseDao.execute(
								"update ProdIODetail set pd_taxtotal=round(pd_sendprice*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2), pd_nettotal=round(pd_netprice*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2) WHERE pd_piid=?",
								pi_id);
						baseDao.updateByCondition(
								"ProdIODetail",
								"pd_netprice=round(pd_sendprice/(1+pd_taxrate/100),8),pd_nettotal=round(pd_sendprice*pd_outqty/(1+nvl(pd_taxrate,0)/100),2)",
								"pd_piid=" + pi_id);
						baseDao.execute(
								"update ProdInOut set pi_total=(SELECT round(sum(nvl(pd_sendprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0))),2) FROM ProdIODetail WHERE pd_piid=pi_id) where pi_id=?",
								piid);
						baseDao.updateByCondition("ProdInOut", "pi_totalupper=L2U(nvl(pi_total,0))", "pi_id=" + pi_id);
						sb.append(
								piclass + "号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
										+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=" + type + "')\">"
										+ pi_inoutno + "</a>&nbsp;").append("<hr>");
					}
				}
			}
			if (sb.length() > 0) {
				return sb.toString();
			}
		}
		return null;
	}

	/**
	 * 批量确认BUG
	 */
	@Override
	public String batchReplyBug(String caller, String data) {
		List<Map<Object, Object>> Store = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> store : Store) {
			Object[] checkList = baseDao.getFieldsDataByCondition(
					"CheckListDetail left join checkList on cld_clid=cl_id left join checklistbasedetail on cld_cbdid=cbd_id",
					new String[] { "cld_status", "cld_statuscode", "cl_prjplanname", "cl_prjplanid", "cld_newhandmanid",
							"cld_handdescription", "cld_name", "cld_cbdid", "cld_newtestmanid", "cld_newtestman", "cld_isconfirmed",
							"cld_analysis" }, "cld_id=" + store.get("cld_id"));
			store.put("cld_status", checkList[0]);
			store.put("cld_statuscode", checkList[1]);
			store.put("cl_prjplanname", checkList[2]);
			store.put("cl_prjplanid", checkList[3]);
			store.put("cld_newhandmanid", checkList[4]);
			store.put("cld_handresult", "-1");
			store.put("cld_name", checkList[6]);
			store.put("cld_cbdid", checkList[7]);
			store.put("cld_newtestmanid", checkList[8]);
			store.put("cld_newtestman", checkList[9]);
			store.put("cld_isconfirmed", checkList[10]);
			List<String> sqls = new ArrayList<String>();
			String language = SystemSession.getLang();
			handlerService.handler("Check", "save", "before", new Object[] { store });
			store.remove("cld_status");
			store.remove("cld_statuscode");
			store.remove("cl_prjplanname");
			store.remove("cl_prjplanid");
			// 插入语句
			int ch_id = baseDao.getSeqId("CHECKTABLE_SEQ");
			String description = null;
			String type = null;
			String statuscode = null;
			String status = null;
			int confirmed = -1;
			StringBuffer sb = new StringBuffer();
			Employee employee = SystemSession.getUser();
			if (Integer.parseInt(store.get("cld_newhandmanid").toString()) == employee.getEm_id()) {
				type = "Handle";
				description = "cld_handdescription";
				statuscode = "TESTING";
				status = BaseUtil.getLocalMessage(statuscode);
				confirmed = 0;
				baseDao.updateByCondition("CheckListBaseDetail", "cbd_status='" + BaseUtil.getLocalMessage("TESTING", language)
						+ "',cbd_statuscode='TESTING'", "cbd_name='" + store.get("cld_name") + "' and cbd_id=" + store.get("cld_cbdid")
						+ "");
				baseDao.updateByCondition("CHECKHISTORY", "ch_cbdstatus='" + BaseUtil.getLocalMessage("TESTING", language) + "'",
						"ch_cbdcode='" + store.get("cld_name") + "' and ch_cbdid=" + store.get("cld_cbdid") + "");
				// 给测试人员发寻呼
				int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
				int prd_id = baseDao.getSeqId("PAGINGRELEASEDETAIL_SEQ");
				sb.setLength(0);
				/*
				 * sb.append("任务提醒&nbsp;&nbsp;&nbsp;&nbsp;[" +
				 * DateUtil.parseDateToString(DateUtil.parseStringToDate(null,
				 * Constant.YMD_HMS), "MM-dd HH:mm") + "]</br>");
				 * sb.append("<a href=\"javascript:openGridUrl(" +
				 * store.get("cld_id") +
				 * ",''cld_id'',''ch_cldid'',''jsps/plm/test/check.jsp'',''Check单''"
				 * + ")\">" + store.get("cld_name") + "</a></br>");
				 * sb.append("你有新的待测试BUG快去看看吧!</br></br>");
				 */
				sb.append("(" + employee.getEm_name() + ")回复了你提出的BUG单!");
				sqls.add("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,pr_from)values('" + pr_id + "','"
						+ employee.getEm_name() + "'," + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",'"
						+ employee.getEm_id() + "','" + sb.toString() + "','task')");
				sqls.add("insert into pagingreleasedetail(prd_id,prd_prid,PRD_RECIPIENTID,PRD_RECIPIENT) values('" + prd_id + "','" + pr_id
						+ "','" + store.get("cld_newtestmanid") + "','" + store.get("cld_newtestman") + "')");

				// 保存到历史消息表
				int IH_ID = baseDao.getSeqId("ICQHISTORY_SEQ");
				sqls.add("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
						+ "select "
						+ IH_ID
						+ ",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
						+ " where pr_id=" + pr_id);
				sqls.add("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
						+ "select ICQHISTORYdetail_seq.nextval," + IH_ID
						+ ",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid=" + pr_id + "and ("
						+ IH_ID
						+ ",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");
			}
			store.put("cld_isconfirmed", confirmed);
			store.put("cld_status", status);
			store.put("cld_statuscode", statuscode);
			String formSql = SqlUtil.getUpdateSqlByFormStore(store, "CheckListDetail", "cld_id");
			String insertSql = "insert into checktable(ch_id,ch_cldid,ch_recorder,ch_recorddate,ch_description,ch_type,ch_detno) values('"
					+ ch_id + "','" + store.get("cld_id") + "','" + employee.getEm_name() + "',"
					+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",'" + store.get(description) + "','" + type + "','"
					+ ch_id + "')";
			sqls.add(insertSql);
			sqls.add(formSql);
			baseDao.execute(sqls);
			Object des = baseDao.getFieldDataByCondition("CheckListDetail", "cld_handdescription", "cld_id=" + store.get("cld_id"));
			if (des == "" || des == null || des.equals("")) {
				baseDao.updateByCondition("CheckListDetail", "cld_handdescription='已处理'", "cld_id=" + store.get("cld_id"));
				baseDao.updateByCondition("checktable", "ch_description='已处理'", "ch_cldid=" + store.get("cld_id"));
			}
			// 新增一个分析
			baseDao.updateByCondition("CheckListDetail", "cld_analysis='" + store.get("cld_analysis") + "',cld_newhanddate=sysdate",
					"cld_id=" + store.get("cld_id"));
			// 查询确认时间是否为空 则更新相应的确认时间
			baseDao.updateByCondition("CheckListDetail",
					"cld_confirmdate=" + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()), "cld_id=" + store.get("cld_id")
							+ " and  cld_confirmdate is null");
			baseDao.logger.update("Check", "cld_id", store.get("cld_id"));
			handlerService.handler("Check", "save", "after", new Object[] { store });
		}
		StringBuffer sp = new StringBuffer();
		sp.append("批量回复BUG成功");
		return sp.toString();
	}

	/**
	 * 批量发送BUG邮件
	 */
	@Override
	public String vastSendBugMeg(String caller, String data) {
		List<Map<Object, Object>> Store = BaseUtil.parseGridStoreToMaps(data);
		Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(Store, new Object[] { "cl_prjplanname" });
		// 按项目分组的发送邮件操作
		Set<Object> mapSet = groups.keySet();
		List<Map<Object, Object>> items;
		Employee employee = SystemSession.getUser();
		for (Object s : mapSet) {
			StringBuffer sbtitle = new StringBuffer();
			StringBuffer sbcontextdetail = new StringBuffer();
			String emails = "";
			items = groups.get(s);
			// 取项目团队收邮件的人及邮箱
			List<Object[]> objects = baseDao.getFieldsDatasByCondition(
					"teammember left join project on prj_id=tm_prjid left join Employee on tm_employeeid=em_id", new String[] {
							"tm_isreceive", "tm_employeeid", "em_email", "em_name" }, " prj_name='" + s + "' and tm_isreceive='true'");
			if (objects.isEmpty()) {
				BaseUtil.showError("该项目没有邮箱接收人");
			}
			for (Object[] team : objects) {
				if (team[2] == null || "null".equals(team[2]) || "".equals(team[2])) {
					BaseUtil.showError("项目邮件接收人邮箱未设置，无法发送邮件，" + team[3] + "没有设置邮箱");
				}
				if (emailFormat(team[2].toString()) == false) {
					BaseUtil.showError("接收人邮箱格式有误，无法发送邮件," + team[3] + "的邮箱为：" + team[2].toString());
				}
				emails = emails + team[2].toString() + ";";
			}
			Object testman[] = baseDao.getFieldsDataByCondition("Employee", new String[] { "em_position", "em_depart", "em_name" },
					"em_id=" + employee.getEm_id());
			sbtitle.append(DateUtil.getCurrentDate() + s + "BUG汇总");
			sbcontextdetail.append("<font size='5'>大家好:</font><br><font size='4'>&nbsp;&nbsp;&nbsp;&nbsp;发件人：" + testman[2]
					+ "&nbsp;&nbsp;" + testman[0] + "&nbsp;&nbsp;" + testman[1]
					+ "</font><br><font size='4'>&nbsp;&nbsp;&nbsp;&nbsp;以下是项目(" + s + ")的BUG汇总：</font><br>");
			int i = 1;
			for (Map<Object, Object> store : items) {
				sbcontextdetail.append("<font size='4'>" + i + "、BUG名称(" + store.get("cld_name") + "),BUG描述("
						+ store.get("cld_testdescription") + "),处理人:(" + store.get("cld_newhandman") + "),问题等级("
						+ store.get("cld_needlevel") + ")</font><br>");
				baseDao.updateByCondition("CheckListDetail", "cld_sendmeg='-1',cld_sendtime=to_date('" + DateUtil.getCurrentDate()
						+ "','yyyy-mm-dd')", "cld_id=" + store.get("cld_id"));
				i++;
			}
			sbcontextdetail.append("<font size='5'>&nbsp;&nbsp;&nbsp;&nbsp;请相关处理人尽快回复BUG计划，谢谢！</font>");
			String title = sbtitle.toString();
			String contextdetail = sbcontextdetail.toString();
			sendMailService.sendSysMail(title, contextdetail, emails);
		}
		return "发送邮件成功";
	}

	/**
	 * 批量确认BUG
	 */
	@Override
	public String confirmBug(String caller, String data) {
		List<Map<Object, Object>> Store = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> store : Store) {
			if ("".equals(store.get("cld_analysis")) || store.get("cld_handstartdate") == null || store.get("cld_handenddate") == null) {
				BaseUtil.showError("明细行勾选数据填写数据不完整，请填写完整后再确认");
			}
			int start = DateUtil.compare(DateUtil.getCurrentDate(), store.get("cld_handstartdate").toString());
			if (start == 1) {
				BaseUtil.showError("预计开始时间不能早于当前时间");
			}
			int end = DateUtil.compare(store.get("cld_handstartdate").toString(), store.get("cld_handenddate").toString());
			if (end == 1) {
				BaseUtil.showError("预计结束时间不能早于预计开始时间");
			}
			int i = 0;
			try {
				i = DateUtil.countDates(store.get("cld_handstartdate").toString(), store.get("cld_handenddate").toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			baseDao.updateByCondition("CheckListDetail", "cld_isconfirm='-1',cld_analysis='" + store.get("cld_analysis")
					+ "',cld_handstartdate=to_date('" + store.get("cld_handstartdate") + "','yyyy-mm-dd'),cld_handenddate=to_date('"
					+ store.get("cld_handenddate") + "','yyyy-mm-dd'),cld_handdays='" + i + "'", "cld_id=" + store.get("cld_id"));
		}
		return "批量确认BUG成功";
	}

	/**
	 * 判断邮箱格式是否正确
	 * 
	 * @param email
	 * @return true or false
	 */
	public static boolean emailFormat(String email) {
		boolean tag = true;
		final String pattern1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		final Pattern pattern = Pattern.compile(pattern1);
		final Matcher mat = pattern.matcher(email);
		if (!mat.find()) {
			tag = false;
		}
		return tag;
	}

	@Override
	public String turnIdentify(String data) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		for (Map<Object, Object> map : store) {
			Object pr_id = map.get("pr_id");
			Object pr_code = map.get("pr_code");
			int count = baseDao.getCountByCondition("ProductApproval", "pa_prodcode='" + pr_code
					+ "' and (pa_status<>'已审核' or pa_finalresult='合格') ");
			if (count > 0) {
				Object pa_code = baseDao.getFieldDataByCondition("ProductApproval", "pa_code", "pa_prodcode='" + pr_code + "'");
				BaseUtil.showError("物料:" + pr_code + "当前已经存在待确认的认定单" + pa_code + "，不能重复转认定单");
			}
			Key key = transferRepository.transfer("Product!ToIdentify", pr_id);
			sb.append("转认定单成功:" + "<a href=\"javascript:openUrl('jsps/scm/product/ProductApproval.jsp?formCondition=pa_idIS" + key.getId()
					+ "')\">" + key.getCode() + "</a>&nbsp;<hr>");
			/**
			 * @author wsy 反馈编号：2017040658 物料认定单添加转认定日志记录
			 */
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "转认定操作", "已转认定", "ProductApproval|pa_id=" + key.getId()));
		}
		return sb.toString();
	}

	/**
	 * 索菱 物料申请单转物料出库单
	 */
	@Override
	public String applyVastTurnOut(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		// 未指定出货通知单
		Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(maps, new Object[] { "ama_code" });
		// 按申请单号分组的转入操作
		Set<Object> mapSet = groups.keySet();
		List<Map<Object, Object>> items;
		int index = 0;
		StringBuffer sb = new StringBuffer();
		String log = null;
		for (Object s : mapSet) {
			items = groups.get(s);
			// 转入单主记录
			Integer am_id = baseDao.getFieldValue("AS_MAKEAPPLYDETAIL", "amad_amaid", "amad_id=" + items.get(0).get("amad_id"),
					Integer.class);
			Key key = transferRepository.transfer(caller, am_id);
			if (key != null) {
				index++;
				int amo_id = key.getId();
				// 转入明细
				transferRepository.transfer(caller, items, key);
				log = "转入成功，物料出库号:" + "<a href=\"javascript:openUrl('jsps/as/port/materielout.jsp?formCondition=amo_idIS" + amo_id
						+ "&gridCondition=amod_amoidIS" + amo_id + "')\">" + key.getCode() + "</a><hr>";
				sb.append(index).append(": ").append(log).append("<hr>");
			}
		}
		for (Map<Object, Object> map : maps) {
			int amad_id = Integer.parseInt(map.get("amad_id").toString());
			baseDao.updateByCondition("AS_MAKEAPPLYDETAIL", "amad_tqty=(nvl(amad_tqty,0)+'" + map.get("amad_aqty") + "')", "amad_id="
					+ amad_id);
		}
		return sb.toString();

	}

	/**
	 * 索菱 物料出库单转物料归还单
	 */
	@Override
	public String outVastTurnReturn(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		// 未指定出货通知单
		Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(maps, new Object[] { "amo_code" });
		// 按出库单号分组的转入操作
		Set<Object> mapSet = groups.keySet();
		List<Map<Object, Object>> items;
		int index = 0;
		StringBuffer sb = new StringBuffer();
		String log = null;
		for (Object s : mapSet) {
			items = groups.get(s);
			// 转入单主记录
			Integer am_id = baseDao
					.getFieldValue("AS_MAKEOUTDETAIL", "amod_amoid", "amod_id=" + items.get(0).get("amod_id"), Integer.class);
			Key key = transferRepository.transfer(caller, am_id);
			if (key != null) {
				index++;
				int amr_id = key.getId();
				// 转入明细
				transferRepository.transfer(caller, items, key);
				log = "转入成功，物料归还单号:" + "<a href=\"javascript:openUrl('jsps/as/port/materielreturn.jsp?formCondition=amr_idIS" + amr_id
						+ "&gridCondition=amrd_amridIS" + amr_id + "')\">" + key.getCode() + "</a><hr>";
				sb.append(index).append(": ").append(log).append("<hr>");
			}
		}
		for (Map<Object, Object> map : maps) {
			int amod_id = Integer.parseInt(map.get("amod_id").toString());
			baseDao.updateByCondition("AS_MAKEOUTDETAIL", "amod_tqty=(nvl(amod_tqty,0)+'" + map.get("amod_aqty") + "')", "amod_id="
					+ amod_id);
		}
		return sb.toString();

	}

	/**
	 * @author wsy 问题反馈编号：2017030165 通用批量更新方法。通过配置批量处理界面，引用按钮erpSaveButton。
	 */
	@Override
	public void commonBatchUpdate(String data, String caller) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(data);
		List<String> gridSql = null;
		Object fo_flowcaller = baseDao.getFieldDataByCondition("form", "fo_flowcaller", "fo_caller='" + caller + "'");
		String dg_field = baseDao.getFieldDataByCondition("detailgrid", "dg_field",
				"dg_caller='" + caller + "' and dg_logictype='keyField'").toString();
		if (fo_flowcaller != null && !"".equals(fo_flowcaller)) {
			SqlRowList rs = baseDao.queryForRowSet("select * from form where fo_caller=?", fo_flowcaller);
			while (rs.next()) {
				String fo_keyfield = rs.getString("fo_keyfield");
				String fo_detailkeyfield = rs.getString("fo_detailkeyfield");
				String fTable = null;
				if (dg_field.equalsIgnoreCase(fo_keyfield)) {
					fTable = rs.getString("fo_table");
					fTable = fTable.split(" ")[0];
					gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, fTable, dg_field);
					for (Map<Object, Object> map : gstore) {
						baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "更新操作", "更新成功", "" + fo_flowcaller + "|"
								+ fo_keyfield + "=" + map.get(dg_field)));
					}
				} else if (dg_field.equalsIgnoreCase(fo_detailkeyfield)) {
					String dTable = rs.getString("fo_detailtable");
					dTable = dTable.split(" ")[0];
					gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, dTable, dg_field);
					for (Map<Object, Object> map : gstore) {
						Object key = baseDao.getFieldDataByCondition(dTable, rs.getString("fo_detailmainkeyfield"), fo_detailkeyfield = map
								.get(fo_detailkeyfield).toString());
						baseDao.logger.getMessageLog("更新操作", "更新成功", fo_flowcaller.toString(), fo_keyfield, key.toString());
					}
				} else {
					BaseUtil.showError("配置有误,未找到主键!");
				}
			}
		} else {
			BaseUtil.showError("请正确配置fo_flowcaller（审批流程）");
		}
		baseDao.execute(gridSql);
	}

	@Override
	public String ApplyToOut(String data, String caller) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		// 未指定出货通知单
		Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(maps, new Object[] { "sa_code" });
		// 按申请单号分组的转入操作
		Set<Object> mapSet = groups.keySet();
		List<Map<Object, Object>> items;
		int index = 0;
		StringBuffer sb = new StringBuffer();
		String log = null;
		for (Object s : mapSet) {
			items = groups.get(s);
			// 转入单主记录
			Integer sa_id = baseDao.getFieldValue("AS_STANDBYDETAIL", "sad_said", "sad_id=" + items.get(0).get("sad_id"), Integer.class);
			Key key = transferRepository.transfer(caller, sa_id);
			if (key != null) {
				index++;
				int sn_id = key.getId();
				// 转入明细
				transferRepository.transfer(caller, items, key);
				log = "转入成功，备用机出库单号:" + "<a href=\"javascript:openUrl('jsps/as/port/StandbyOut.jsp?formCondition=so_idIS" + sn_id
						+ "&gridCondition=sod_soidIS" + sn_id + "')\">" + key.getCode() + "</a><hr>";
				sb.append(index).append(": ").append(log).append("<hr>");
			}
		}
		for (Map<Object, Object> map : maps) {
			int sad_id = Integer.parseInt(map.get("sad_id").toString());
			baseDao.updateByCondition("AS_STANDBYDETAIL", "sad_out=(nvl(sad_out,0)+'" + map.get("sad_tqty") + "')", "sad_id=" + sad_id);
		}
		return sb.toString();
	}

	@Override
	public String OutToReturn(String data, String caller) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		// 未指定出货通知单
		Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(maps, new Object[] { "so_code" });
		// 按申请单号分组的转入操作
		Set<Object> mapSet = groups.keySet();
		List<Map<Object, Object>> items;
		int index = 0;
		StringBuffer sb = new StringBuffer();
		String log = null;
		for (Object s : mapSet) {
			items = groups.get(s);
			// 转入单主记录
			Integer sa_id = baseDao.getFieldValue("AS_STANDBYOUTDETAIL", "sod_soid", "sod_id=" + items.get(0).get("sod_id"), Integer.class);
			Key key = transferRepository.transfer(caller, sa_id);
			if (key != null) {
				index++;
				int sn_id = key.getId();
				// 转入明细
				transferRepository.transfer(caller, items, key);
				log = "转入成功，备用机归还单号:" + "<a href=\"javascript:openUrl('jsps/as/port/StandbyBack.jsp?formCondition=sb_idIS" + sn_id
						+ "&gridCondition=sbd_sbidIS" + sn_id + "')\">" + key.getCode() + "</a><hr>";
				sb.append(index).append(": ").append(log).append("<hr>");
			}
		}
		for (Map<Object, Object> map : maps) {
			int sod_id = Integer.parseInt(map.get("sod_id").toString());
			baseDao.updateByCondition("AS_STANDBYOUTDETAIL", "sod_yzqty=(nvl(sod_yzqty,0)+'" + map.get("sod_tqty") + "')", "sod_id="
					+ sod_id);
		}
		return sb.toString();
	}

	/**
	 * 怡海能达在途在库解锁
	 */
	@Override
	public String Deblock(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Employee employee = SystemSession.getUser();
		Object en_whichsystem = baseDao.getFieldDataByCondition("Enterprise", "en_whichsystem", "1=1");
		// 只能操作当前帐套的内容
		for (Map<Object, Object> check : maps) {
			if (!en_whichsystem.equals(check.get("en_whichsystem"))) {
				BaseUtil.showError("只能操作当前帐套(" + en_whichsystem + ")的数据");
			}
		}
		for (Map<Object, Object> map : maps) {
			if ("在途".equals(map.get("type"))) {
				SqlRowList rs = baseDao.queryForRowSet("select ob_qty from onorderbooking where ob_qty<" + map.get("ob_tqty")
						+ " and ob_id=" + map.get("ob_id") + "");
				if (rs.next()) {
					BaseUtil.showError("解锁数量超过锁定数量,请确认后再进行解锁操作");
				}
				// 插入在途日志表
				baseDao.execute("insert into ONORDERBOOKINGLOG "
						+ "(OOL_ID,OOL_INDATE,OOL_STARTDATE,OOL_PDID,OOL_PUCODE,OOL_PUDETNO,OOL_PRODCODE,OOL_DELIVERY,OOL_DELIVERY_DEFAULT,OOL_QTY,OOL_SFCODE,OOL_SFDETNO,OOL_FORECASTID,OOL_SACODE,OOL_SADETNO,OOL_SALEID,OOL_IFREPLY,OOL_CLEAROR,OOL_CLEARDATE,OOL_REMARK) "
						+ "select OB_ID,OB_INDATE,OB_STARTDATE ,OB_PDID,OB_PUCODE,OB_PUDETNO,OB_PRODCODE,OB_DELIVERY,OB_DELIVERY_DEFAULT,"
						+ map.get("ob_tqty") + ",OB_SFCODE,OB_SFDETNO,OB_FORECASTID,OB_SACODE,OB_SADETNO,OB_SALEID,OB_IFREPLY,'"
						+ employee.getEm_name() + "',sysdate,'解锁操作' " + "from onorderbooking where ob_id=" + map.get("ob_id") + "");
				baseDao.updateByCondition("ONORDERBOOKING", "ob_qty=ob_qty-" + map.get("ob_tqty") + "", "ob_id=" + map.get("ob_id"));
			} else if ("在库".equals(map.get("type"))) {
				SqlRowList rs = baseDao.queryForRowSet("select ob_qty from onhandbooking where ob_qty<" + map.get("ob_tqty")
						+ " and ob_id=" + map.get("ob_id") + "");
				if (rs.next()) {
					BaseUtil.showError("解锁数量超过锁定数量,请确认后再进行解锁操作");
				}
				baseDao.execute("insert into ONHANDBOOKINGLOG "
						+ "(OHL_ID,OHL_INDATE,OHL_STARTDATE,OHL_ENDDATE,OHL_PIDATE,OHL_INOUTNO,OHL_PDNO,OHL_BATCHCODE,OHL_PRODCODE,OHL_QTY,OHL_SENDQTY,OHL_SOURCEID,OHL_SFCODE,OHL_SFDETNO,OHL_FORECASTID,OHL_SACODE,OHL_SADETNO,OHL_SALEID,OHL_CLEAROR,OHL_CLEARDATE,OHL_REMARK,OHL_BAID) "
						+ "select OB_ID,OB_INDATE,OB_STARTDATE,OB_ENDDATE,OB_PIDATE,OB_INOUTNO,OB_PDNO,OB_BATCHCODE,OB_PRODCODE,"
						+ map.get("ob_tqty")
						+ ",OB_SENDQTY,OB_SOURCEID,OB_SFCODE,OB_SFDETNO,OB_FORECASTID,OB_SACODE,OB_SADETNO,OB_SALEID,'"
						+ employee.getEm_name() + "',sysdate,'解锁操作',OB_BAID " + "from onhandbooking where ob_id=" + map.get("ob_id") + "");
				baseDao.updateByCondition("ONHANDBOOKING", "ob_qty=ob_qty-" + map.get("ob_tqty") + "", "ob_id=" + map.get("ob_id"));
			}
			// 怡海能达锁定明细:增加参数ob_application是否自动生成采购申请
			if ("1".equals(map.get("ob_application"))) {
				// 当类型是销售订单时
				int sa_id = Integer.parseInt(map.get("ob_saleid").toString());
				if (sa_id > 0) {
					SqlRowList rs = baseDao.queryForRowSet("select ad_id,ad_detno,ad_qty,ad_apid from applicationdetail where ad_sourceid="
							+ sa_id + "");
					if (rs.next()) {
						int newqty = rs.getInt("ad_qty") + Integer.parseInt(map.get("ob_tqty").toString());
						baseDao.updateByCondition("applicationdetail", "ad_qty=" + newqty + "", "ad_id=" + rs.getInt("ad_id"));
						baseDao.logger.others("解锁操作", "数量变更:行" + rs.getInt("ad_detno") + ":" + rs.getInt("ad_qty") + "---->" + newqty + "",
								"Application", "ap_id", rs.getObject("ad_apid"));
					} else {
						int ap_id = baseDao.getSeqId("APPLICATION_SEQ");
						int ad_id = baseDao.getSeqId("APPLICATIONDETAIL_SEQ");
						String ap_code = baseDao.sGetMaxNumber("application", 2);
						Object cu_shortname = baseDao.getFieldDataByCondition("sale left join customer on sa_custcode=cu_code",
								"cu_shortname", "sa_id=" + sa_id);
						Object[] pr_info = baseDao.getFieldsDataByCondition("product", new String[] { "nvl(pr_leadtime,0)",
								"nvl(pr_zxbzs,0)", "nvl(pr_zxdhl,0)" }, "pr_code='" + map.get("pr_code") + "'");
						String sqlform = "insert into application(ap_id,ap_code,ap_date,ap_status,ap_statuscode,ap_kind,ap_departcode,ap_departname,"
								+ "ap_pleamanid,ap_pleamanname,ap_buyercode,ap_buyername,ap_recorderid,ap_recorder,ap_recorddate,ap_printstatus,"
								+ "ap_printstatuscode,ap_cushortname_user) values ("
								+ ap_id
								+ ",'"
								+ ap_code
								+ "',sysdate,'已审核','AUDITED','一般请购','"
								+ employee.getEm_departmentcode()
								+ "','"
								+ employee.getEm_depart()
								+ "',"
								+ ""
								+ employee.getEm_id()
								+ ",'"
								+ employee.getEm_name()
								+ "','"
								+ employee.getEm_code()
								+ "','"
								+ employee.getEm_name()
								+ "',"
								+ employee.getEm_id()
								+ ",'"
								+ employee.getEm_name() + "'," + "sysdate,'未打印','UNPRINT','" + cu_shortname + "')";
						String sqlgrid = "insert into applicationdetail(ad_id,ad_apid,ad_detno,ad_prodcode,ad_qty,ad_source,ad_sourceid,"
								+ "ad_delivery,ad_leadtime,ad_minpack,ad_minorder,ad_code,ad_status,ad_statuscode) values (" + ad_id + ","
								+ ap_id + ",1,'" + map.get("pr_code") + "','" + map.get("ob_tqty") + "','" + map.get("sa_code") + "',"
								+ map.get("ob_saleid") + ",sysdate," + "" + pr_info[0] + "," + pr_info[1] + "," + pr_info[2] + ",'"
								+ ap_code + "','已审核','AUDITED')";
						baseDao.execute(sqlform);
						baseDao.execute(sqlgrid);
						baseDao.logger.others("解锁操作", "自动生成采购申请:行1:0---->" + map.get("ob_tqty") + "", "Application", "ap_id", ap_id);
					}
				} else {
					// 当类型是销售预测
					Object sf_id = baseDao.getFieldDataByCondition("saleforecast left join saleforecastdetail on sf_id=sd_sfid", "sf_id",
							"sf_code='" + map.get("sf_code") + "' and sd_detno=" + map.get("ob_sfdetno") + "");
					SqlRowList rs = baseDao.queryForRowSet("select ad_id,ad_detno,ad_qty,ad_apid from applicationdetail where ad_sourceid="
							+ sf_id + "");
					if (rs.next()) {
						int newqty = rs.getInt("ad_qty") + Integer.parseInt(map.get("ob_tqty").toString());
						baseDao.updateByCondition("applicationdetail", "ad_qty=" + newqty + "", "ad_id=" + rs.getInt("ad_id"));
						baseDao.logger.others("解锁操作", "数量变更:行" + rs.getInt("ad_detno") + ":" + rs.getInt("ad_qty") + "---->" + newqty + "",
								"Application", "ap_id", rs.getObject("ad_apid"));
					} else {
						int ap_id = baseDao.getSeqId("APPLICATION_SEQ");
						int ad_id = baseDao.getSeqId("APPLICATIONDETAIL_SEQ");
						String ap_code = baseDao.sGetMaxNumber("application", 2);
						Object cu_shortname = baseDao.getFieldDataByCondition("sale left join customer on sa_custcode=cu_code",
								"cu_shortname", "sa_id=" + sa_id);
						Object[] pr_info = baseDao.getFieldsDataByCondition("product", new String[] { "nvl(pr_leadtime,0)",
								"nvl(pr_zxbzs,0)", "nvl(pr_zxdhl,0)" }, "pr_code='" + map.get("pr_code") + "'");
						String sqlform = "insert into application(ap_id,ap_code,ap_date,ap_status,ap_statuscode,ap_kind,ap_departcode,ap_departname,"
								+ "ap_pleamanid,ap_pleamanname,ap_buyercode,ap_buyername,ap_recorderid,ap_recorder,ap_recorddate,ap_printstatus,"
								+ "ap_printstatuscode,ap_cushortname_user) values ("
								+ ap_id
								+ ",'"
								+ ap_code
								+ "',sysdate,'已审核','AUDITED','一般请购','"
								+ employee.getEm_departmentcode()
								+ "','"
								+ employee.getEm_depart()
								+ "',"
								+ ""
								+ employee.getEm_id()
								+ ",'"
								+ employee.getEm_name()
								+ "','"
								+ employee.getEm_code()
								+ "','"
								+ employee.getEm_name()
								+ "',"
								+ employee.getEm_id()
								+ ",'"
								+ employee.getEm_name() + "'," + "sysdate,'未打印','UNPRINT','" + cu_shortname + "')";
						String sqlgrid = "insert into applicationdetail(ad_id,ad_apid,ad_detno,ad_prodcode,ad_qty,ad_source,ad_sourceid,"
								+ "ad_delivery,ad_leadtime,ad_minpack,ad_minorder,ad_code,ad_status,ad_statuscode) values (" + ad_id + ","
								+ ap_id + ",1,'" + map.get("pr_code") + "','" + map.get("ob_tqty") + "','" + sf_id + "',"
								+ map.get("ob_saleid") + ",sysdate," + "" + pr_info[0] + "," + pr_info[1] + "," + pr_info[2] + ",'"
								+ ap_code + "','已审核','AUDITED')";
						baseDao.execute(sqlform);
						baseDao.execute(sqlgrid);
						baseDao.logger.others("解锁操作", "自动生成采购申请:行1:0---->" + map.get("ob_tqty") + "", "Application", "ap_id", ap_id);
					}
				}
			}
		}
		return "解锁成功";
	}

	/**
	 * 怡海能达在途在库拆分
	 */
	@Override
	public String splitDeblock(String formdata, String data, String caller) {
		Map<Object, Object> formmap = BaseUtil.parseFormStoreToMap(formdata);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(data);
		Employee employee = SystemSession.getUser();
		for (Map<Object, Object> gridmap : grid) {
			if ("在途".equals(gridmap.get("type"))) {
				String sql = "select * from onorderbooking where ob_id=" + gridmap.get("ob_id") + "";
				SqlRowList rs = baseDao.queryForRowSet(sql);
				if (rs.next()) {
					// 插入到在途表
					Map<String, Object> newMap = new HashMap<String, Object>();
					newMap.put("ob_id", baseDao.getSeqId("onorderbooking_SEQ"));
					newMap.put("ob_indate", rs.getDate("ob_indate"));
					newMap.put("ob_startdate", rs.getDate("ob_startdate"));
					newMap.put("ob_enddate", rs.getDate("ob_enddate"));
					newMap.put("ob_pdid", rs.getInt("ob_pdid"));
					newMap.put("ob_pucode", rs.getString("ob_pucode"));
					newMap.put("ob_pudetno", rs.getInt("ob_pudetno"));
					newMap.put("ob_pucode", rs.getString("ob_pucode"));
					if (formmap.get("ob_delivery") == null) {
						newMap.put("ob_delivery_default", rs.getDate("ob_delivery"));
					} else {
						newMap.put("ob_delivery_default", formmap.get("ob_delivery").toString().substring(0, 10));
					}
					newMap.put("ob_delivery", rs.getDate("ob_delivery"));
					newMap.put("ob_qty", gridmap.get("ob_tqty"));
					newMap.put("ob_sfcode", formmap.get("ob_sfcode"));
					newMap.put("ob_sfdetno", formmap.get("ob_sfdetno"));
					newMap.put("ob_forecastid", formmap.get("ob_forecastid"));
					newMap.put("ob_sacode", formmap.get("ob_sacode"));
					newMap.put("ob_sadetno", formmap.get("ob_sddetno"));
					newMap.put("ob_saleid", formmap.get("ob_saleid"));
					newMap.put("ob_ifreply", rs.getString("ob_ifreply"));
					baseDao.execute(SqlUtil.getInsertSqlByMap(newMap, "onorderbooking"));
					// 插入在途日志表
					baseDao.execute("insert into ONORDERBOOKINGLOG "
							+ "(OOL_ID,OOL_INDATE,OOL_STARTDATE,OOL_PDID,OOL_PUCODE,OOL_PUDETNO,OOL_PRODCODE,OOL_DELIVERY,OOL_DELIVERY_DEFAULT,OOL_QTY,OOL_SFCODE,OOL_SFDETNO,OOL_FORECASTID,OOL_SACODE,OOL_SADETNO,OOL_SALEID,OOL_IFREPLY,OOL_CLEAROR,OOL_CLEARDATE,OOL_REMARK) "
							+ "select OB_ID,OB_INDATE,OB_STARTDATE ,OB_PDID,OB_PUCODE,OB_PUDETNO,OB_PRODCODE,OB_DELIVERY,OB_DELIVERY_DEFAULT,"
							+ gridmap.get("ob_tqty") + ",OB_SFCODE,OB_SFDETNO,OB_FORECASTID,OB_SACODE,OB_SADETNO,OB_SALEID,OB_IFREPLY,'"
							+ employee.getEm_name() + "',sysdate,'解锁操作' " + "from onorderbooking where ob_id=" + gridmap.get("ob_id") + "");
					// 更新之前的在途表
					baseDao.updateByCondition("onorderbooking", "ob_qty=ob_qty-" + gridmap.get("ob_tqty") + "",
							"ob_id=" + gridmap.get("ob_id"));
				} else {
					BaseUtil.showError("该条数据已被删除，不能拆分");
				}
			} else if ("在库".equals(gridmap.get("type"))) {
				String sql = "select * from onhandbooking where ob_id=" + gridmap.get("ob_id") + "";
				SqlRowList rs = baseDao.queryForRowSet(sql);
				if (rs.next()) {
					// 插入在库表
					Map<String, Object> newMap = new HashMap<String, Object>();
					newMap.put("ob_id", baseDao.getSeqId("onhandbooking_SEQ"));
					newMap.put("ob_indate", rs.getDate("ob_indate"));
					newMap.put("ob_startdate", rs.getDate("ob_startdate"));
					newMap.put("ob_enddate", rs.getDate("ob_enddate"));
					newMap.put("ob_pidate", rs.getDate("ob_pidate"));
					newMap.put("ob_inoutno", rs.getString("ob_inoutno"));
					newMap.put("ob_pdno", rs.getString("ob_pdno"));
					newMap.put("ob_batchcode", rs.getString("ob_batchcode"));
					newMap.put("ob_prodcode", rs.getString("ob_prodcode"));
					newMap.put("ob_qty", gridmap.get("ob_tqty"));
					newMap.put("ob_sendqty", rs.getGeneralInt("ob_sendqty"));
					newMap.put("ob_sourceid", rs.getInt("ob_sourceid"));
					newMap.put("ob_sfcode", formmap.get("ob_sfcode"));
					newMap.put("ob_sfdetno", formmap.get("ob_sfdetno"));
					newMap.put("ob_forecastid", formmap.get("ob_forecastid"));
					newMap.put("ob_sacode", formmap.get("ob_sacode"));
					newMap.put("ob_sadetno", formmap.get("ob_sddetno"));
					newMap.put("ob_saleid", formmap.get("ob_saleid"));
					baseDao.execute(SqlUtil.getInsertSqlByMap(newMap, "onhandbooking"));
					// 插入在库日志表
					baseDao.execute("insert into ONHANDBOOKINGLOG "
							+ "(OHL_ID,OHL_INDATE,OHL_STARTDATE,OHL_ENDDATE,OHL_PIDATE,OHL_INOUTNO,OHL_PDNO,OHL_BATCHCODE,OHL_PRODCODE,OHL_QTY,OHL_SENDQTY,OHL_SOURCEID,OHL_SFCODE,OHL_SFDETNO,OHL_FORECASTID,OHL_SACODE,OHL_SADETNO,OHL_SALEID,OHL_CLEAROR,OHL_CLEARDATE,OHL_REMARK,OHL_BAID) "
							+ "select OB_ID,OB_INDATE,OB_STARTDATE,OB_ENDDATE,OB_PIDATE,OB_INOUTNO,OB_PDNO,OB_BATCHCODE,OB_PRODCODE,"
							+ gridmap.get("ob_tqty")
							+ ",OB_SENDQTY,OB_SOURCEID,OB_SFCODE,OB_SFDETNO,OB_FORECASTID,OB_SACODE,OB_SADETNO,OB_SALEID,'"
							+ employee.getEm_name() + "',sysdate,'解锁操作' ,OB_BAID" + "from onhandbooking where ob_id="
							+ gridmap.get("ob_id") + "");
					// 更新之前的在库表
					baseDao.updateByCondition("onhandbooking", "ob_qty=ob_qty-" + gridmap.get("ob_tqty") + "",
							"ob_id=" + gridmap.get("ob_id"));
				} else {
					BaseUtil.showError("该条数据已被删除，不能拆分");
				}
			}
		}
		return "拆分成功";
	}

	@Override
	public String handLocked(String caller, String data, String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gStore = BaseUtil.parseGridStoreToMaps(data);
		List<String> sqls = new ArrayList<String>();
		Object en_whichsystem = baseDao.getFieldDataByCondition("Enterprise", "en_whichsystem", "1=1");
		// 只能操作当前帐套的内容
		if (!en_whichsystem.equals(store.get("en_whichsystem"))) {
			BaseUtil.showError("只能操作当前帐套(" + en_whichsystem + ")的数据");
		}
		// 本次锁定数
		double qty = 0;
		String type = "";
		for (Map<Object, Object> map : gStore) {
			String sql = null;
			String sqllog = null;
			if ("在途".equals(map.get("type_"))) {
				int ob_id = baseDao.getSeqId("ONORDERBOOKING_SEQ");
				// 已审核采购单（pd_qty-nvl(pd_acceptqty,0)）
				double pu_qty = baseDao.getFieldValue("PurchaseDetail left join purchase on pd_puid=pu_id", "pd_qty-nvl(pd_acceptqty,0)",
						"pd_code='" + map.get("code_") + "' and pd_detno=" + map.get("pd_detno") + " and pu_status='已审核' ", Double.class);
				// 已锁数量
				double ob_qty = baseDao.getFieldValue("ONORDERBOOKING", "nvl(sum(ob_qty),0)", "ob_pucode='" + map.get("code_")
						+ "' and OB_PUDETNO=" + map.get("pd_detno") + " ", Double.class);
				if (Double.parseDouble(map.get("tqty").toString()) > (pu_qty - ob_qty)) {
					BaseUtil.showError("锁定采购数量不能超过采购单在途未锁数量，采购单号：" + map.get("code_") + "");
				}
				if ("销售订单".equals(store.get("type"))) {
					sql = "insert into OnorderBooking(OB_ID,OB_INDATE,OB_STARTDATE,OB_PUCODE,OB_PUDETNO,OB_PDID,OB_PRODCODE,OB_QTY,OB_SACODE,OB_SADETNO,OB_SALEID,OB_REMARK) values("
							+ ob_id
							+ ",sysdate,sysdate,'"
							+ map.get("code_")
							+ "',"
							+ map.get("pd_detno")
							+ ","
							+ map.get("id_")
							+ ",'"
							+ map.get("prodcode_")
							+ "',"
							+ map.get("tqty")
							+ ",'"
							+ store.get("code")
							+ "',"
							+ store.get("detno")
							+ ","
							+ store.get("id") + ",'手工加锁')";
					sqllog = "insert into ONORDERBOOKINGLOG(OOL_ID,OOL_INDATE,OOL_STARTDATE,OOL_PDID,OOL_PUCODE,OOL_PUDETNO,OOL_PRODCODE,OOL_QTY,OOL_SACODE,OOL_SADETNO,OOL_SALEID,OOL_CLEAROR,OOL_CLEARDATE,OOL_REMARK) values ("
							+ ob_id
							+ ",sysdate,sysdate,"
							+ map.get("id_")
							+ ",'"
							+ map.get("code_")
							+ "',"
							+ map.get("pd_detno")
							+ ",'"
							+ map.get("prodcode_")
							+ "',"
							+ map.get("tqty")
							+ ",'"
							+ store.get("code")
							+ "',"
							+ store.get("detno")
							+ ","
							+ store.get("id") + ",'" + SystemSession.getUser().getEm_name() + "',sysdate,'加锁操作')";
				} else if ("销售预测".equals(store.get("type"))) {
					sql = "insert into OnorderBooking(OB_ID,OB_INDATE,OB_STARTDATE,OB_PUCODE,OB_PUDETNO,OB_PDID,OB_PRODCODE,OB_QTY,OB_SFCODE,OB_SFDETNO,OB_FORECASTID,OB_REMARK) values("
							+ ob_id
							+ ",sysdate,sysdate,'"
							+ map.get("code_")
							+ "',"
							+ map.get("pd_detno")
							+ ","
							+ map.get("id_")
							+ ",'"
							+ map.get("prodcode_")
							+ "',"
							+ map.get("tqty")
							+ ",'"
							+ store.get("code")
							+ "',"
							+ store.get("detno")
							+ ","
							+ store.get("id") + ",'手工加锁')";
					sqllog = "insert into ONORDERBOOKINGLOG(OOL_ID,OOL_INDATE,OOL_STARTDATE,OOL_PDID,OOL_PUCODE,OOL_PUDETNO,OOL_PRODCODE,OOL_QTY,OOL_SFCODE,OOL_SFDETNO,OOL_FORECASTID,OOL_CLEAROR,OOL_CLEARDATE,OOL_REMARK) values ("
							+ ob_id
							+ ",sysdate,sysdate,"
							+ map.get("id_")
							+ ",'"
							+ map.get("code_")
							+ "',"
							+ map.get("pd_detno")
							+ ",'"
							+ map.get("prodcode_")
							+ "',"
							+ map.get("tqty")
							+ ",'"
							+ store.get("code")
							+ "',"
							+ store.get("detno")
							+ ","
							+ store.get("id") + ",'" + SystemSession.getUser().getEm_name() + "',sysdate,'加锁操作')";
				}
				sqls.add(sqllog);
				sqls.add(sql);
			}
			if ("库存".equals(map.get("type_"))) {
				int ob_id = baseDao.getSeqId("ONHANDBOOKING_SEQ");
				// 剩余库存
				double ba_qty = baseDao.getFieldValue("batch", "nvl(ba_remain,0)", "ba_code='" + map.get("code_") + "' and ba_prodcode='"
						+ map.get("prodcode_") + "'", Double.class);
				// 已锁数量
				double ob_qty = baseDao.getFieldValue("ONHANDBOOKING", "nvl(sum(ob_qty),0)", "OB_BATCHCODE='" + map.get("code_")
						+ "' and OB_PRODCODE='" + map.get("prodcode_") + "' ", Double.class);
				if (Double.parseDouble(map.get("tqty").toString()) > (ba_qty - ob_qty)) {
					BaseUtil.showError("锁定批次数量不能超过批次剩余未锁库存（剩余库存-已锁数量），批号：" + map.get("code_") + "");
				}
				if ("销售订单".equals(store.get("type"))) {
					sql = "insert into ONHANDBOOKING(OB_ID,OB_INDATE,OB_STARTDATE,OB_BATCHCODE,OB_PDNO,OB_PRODCODE,OB_QTY,OB_SACODE,OB_SADETNO,OB_SALEID,OB_REMARK1,OB_BAID) values("
							+ ob_id
							+ ",sysdate,sysdate,'"
							+ map.get("code_")
							+ "',"
							+ map.get("pd_detno")
							+ ",'"
							+ map.get("prodcode_")
							+ "',"
							+ map.get("tqty")
							+ ",'"
							+ store.get("code")
							+ "',"
							+ store.get("detno")
							+ ","
							+ store.get("id")
							+ ",'手工加锁'," + map.get("id_") + ")";
					sqllog = "insert into ONHANDBOOKINGLOG(OHL_ID,OHL_INDATE,OHL_STARTDATE,OHL_PDNO,OHL_BATCHCODE,OHL_PRODCODE,OHL_QTY,OHL_SACODE,OHL_SADETNO,OHL_SALEID,OHL_CLEAROR,OHL_CLEARDATE,OHL_REMARK,OHL_BAID) values ("
							+ ob_id
							+ ",sysdate,sysdate,"
							+ map.get("pd_detno")
							+ ",'"
							+ map.get("code_")
							+ "','"
							+ map.get("prodcode_")
							+ "',"
							+ map.get("tqty")
							+ ",'"
							+ store.get("code")
							+ "',"
							+ store.get("detno")
							+ ","
							+ store.get("id")
							+ ",'"
							+ SystemSession.getUser().getEm_name() + "',sysdate,'加锁操作'," + map.get("id_") + ")";
				} else if ("销售预测".equals(store.get("type"))) {
					sql = "insert into ONHANDBOOKING(OB_ID,OB_INDATE,OB_STARTDATE,OB_BATCHCODE,OB_PDNO,OB_PRODCODE,OB_QTY,OB_SFCODE,OB_SFDETNO,OB_FORECASTID,OB_REMARK1,OB_BAID) values("
							+ ob_id
							+ ",sysdate,sysdate,'"
							+ map.get("code_")
							+ "',"
							+ map.get("pd_detno")
							+ ",'"
							+ map.get("prodcode_")
							+ "',"
							+ map.get("tqty")
							+ ",'"
							+ store.get("code")
							+ "',"
							+ store.get("detno")
							+ ","
							+ store.get("id")
							+ ",'手工加锁'," + map.get("id_") + ")";
					sqllog = "insert into ONHANDBOOKINGLOG(OHL_ID,OHL_INDATE,OHL_STARTDATE,OHL_PDNO,OHL_BATCHCODE,OHL_PRODCODE,OHL_QTY,OHL_SFCODE,OHL_SFDETNO,OHL_FORECASTID,OHL_CLEAROR,OHL_CLEARDATE,OHL_REMARK,OHL_BAID) values ("
							+ ob_id
							+ ",sysdate,sysdate,"
							+ map.get("pd_detno")
							+ ",'"
							+ map.get("code_")
							+ "','"
							+ map.get("prodcode_")
							+ "',"
							+ map.get("tqty")
							+ ",'"
							+ store.get("code")
							+ "',"
							+ store.get("detno")
							+ ","
							+ store.get("id")
							+ ",'"
							+ SystemSession.getUser().getEm_name() + "',sysdate,'加锁操作'," + map.get("id_") + ")";
				}
				sqls.add(sql);
			}
			qty = qty + Double.parseDouble(map.get("tqty").toString());
		}
		Object qty1 = null;
		Object qty2 = null;
		Object qty3 = null;
		Object qty4 = null;
		if ("销售订单".equals(store.get("type"))) {
			type = "Sale";
			// 锁定在途数量
			qty1 = baseDao.getFieldDataByCondition("ONORDERBOOKING", "nvl(sum(ob_qty),0)", "nvl(ob_saleid,0)=" + store.get("id"));
			// 锁定库存数量
			qty2 = baseDao.getFieldDataByCondition("ONHANDBOOKING", "nvl(sum(ob_qty),0)", "nvl(ob_saleid,0)=" + store.get("id"));
			// 销售订单未出货数
			qty3 = baseDao.getFieldDataByCondition("saledetail", "sd_qty-nvl(sd_sendqty,0)", "sd_id=" + store.get("id"));
			// 对应采购单数量
			qty4 = baseDao.getFieldDataByCondition("purchasedetail", "nvl(sum(pd_qty),0)",
					"pd_sourcedetail=(select nvl(ad_id,0) from applicationdetail where nvl(ad_source,' ')='Sale' and nvl(ad_sourceid,0)="
							+ store.get("id") + " and nvl(pd_status,'ENTERING') in ('ENTERING','COMMITED'))");

		} else if ("销售预测".equals(store.get("type"))) {
			type = "SaleForecast";
			// 锁定在途数量
			qty1 = baseDao.getFieldDataByCondition("ONORDERBOOKING", "nvl(sum(ob_qty),0)", "nvl(OB_FORECASTID,0)=" + store.get("id"));
			// 锁定库存数量
			qty2 = baseDao.getFieldDataByCondition("ONHANDBOOKING", "nvl(sum(ob_qty),0)", "nvl(OB_FORECASTID,0)=" + store.get("id"));
			// 销售订单未出货数
			qty3 = baseDao.getFieldDataByCondition("SaleForecastDetail ", "nvl(sd_qty,0)", "sd_id=" + store.get("id"));
			// 对应采购单数量
			qty4 = baseDao.getFieldDataByCondition("purchasedetail", "nvl(sum(pd_qty),0)",
					"pd_sourcedetail=(select nvl(ad_id,0) from applicationdetail where nvl(ad_source,' ')='SaleForecast' and nvl(ad_sourceid,0)="
							+ store.get("id") + " and nvl(pd_status,'ENTERING') in ('ENTERING','COMMITED'))");

		}
		if (Double.parseDouble(qty3.toString()) < (Double.parseDouble(qty1.toString()) + Double.parseDouble(qty2.toString()) + qty + Double
				.parseDouble(qty4.toString()))) {
			BaseUtil.showError("本次锁定数：" + qty + "+已锁在途数：" + qty1 + "+已锁库存数:" + qty2 + "+采购单明细状态为在录入或已提交的数量：" + qty4 + " 大于销售订单未出货数量："
					+ qty3 + "");
		}
		if ("true".equals(store.get("pr_ispubsale").toString())) {
			// 请购单未转数量
			Object[] obj = baseDao.getFieldsDataByCondition("applicationdetail left join application on ad_apid=ap_id", new String[] {
					"ad_detno", "ad_apid", "ad_qty", "ad_qty-nvl(ad_yqty,0)" },
					"ad_source='" + type + "' and ad_sourceid=" + store.get("id"));
			if (obj != null) {
				obj[3] = Double.parseDouble((obj[3] == null ? "0" : obj[3]).toString()) - qty < 0 ? (obj[3] == null ? "0" : obj[3]) : qty;
				if ("销售订单".equals(store.get("type"))) {
					sqls.add("update applicationdetail set ad_qty=(ad_qty-(" + obj[3] + ")) where ad_source='Sale' and ad_sourceid="
							+ store.get("id"));
				} else if ("销售预测".equals(store.get("type"))) {
					sqls.add("update applicationdetail set ad_qty=(ad_qty-(" + obj[3]
							+ ")) where ad_source='SaleForecast' and ad_sourceid=" + store.get("id"));
				}
				baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "扣减采购申请操作", "扣减成功，扣减行号：" + obj[0] + ",扣减前数量："
						+ obj[2] + ",扣减后数量：" + (Integer.parseInt(obj[2].toString()) - qty) + "", "Application|ap_id=" + obj[1]));
			}
		}
		baseDao.execute(sqls);
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "加锁操作", "加锁成功", null));
		return "锁定成功";
	}

	/**
	 * 怡海能达在途在库借调
	 */
	@Override
	public String LendTry(String formdata, String data, String caller) {
		List<Map<Object, Object>> form = BaseUtil.parseGridStoreToMaps(formdata);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(data);
		int a = 1;
		int b = 1;
		int c = 0;
		for (Map<Object, Object> f : form) {
			int y = Integer.parseInt(f.get("tqty").toString());
			String sql = "select * from onorderbooking where ob_id=" + f.get("ob_id") + "";
			for (Map<Object, Object> g : grid) {
				SqlRowList rs = baseDao.queryForRowSet(sql);
				if (a >= b) {
					if (c == 0) {
						int x = Integer.parseInt(g.get("ob_tqty").toString());
						if (x >= y) {
							if ("在途".equals(g.get("type"))) {
								if (rs.next()) {
									String sqlorder = "select * from onorderbooking where ob_id=" + g.get("ob_id") + "";
									SqlRowList rsorder = baseDao.queryForRowSet(sqlorder);
									if (rsorder.next()) {
										baseDao.updateByCondition("onorderbooking", "ob_qty=(ob_qty-" + y + ")", "ob_id=" + g.get("ob_id"));
										Map<String, Object> newMap = new HashMap<String, Object>();
										newMap.put("ob_id", baseDao.getSeqId("onorderbooking_SEQ"));
										newMap.put("ob_indate", rsorder.getDate("ob_indate"));
										newMap.put("ob_startdate", rsorder.getDate("ob_startdate"));
										newMap.put("ob_enddate", rsorder.getDate("ob_enddate"));
										newMap.put("ob_pdid", rsorder.getInt("ob_pdid"));
										newMap.put("ob_pucode", rsorder.getString("ob_pucode"));
										newMap.put("ob_pudetno", rsorder.getInt("ob_pudetno"));
										newMap.put("ob_delivery_default", rsorder.getDate("ob_delivery"));
										newMap.put("ob_delivery", rsorder.getDate("ob_delivery"));
										newMap.put("ob_qty", y);
										newMap.put("ob_prodcode", rsorder.getString("ob_prodcode"));
										newMap.put("ob_sfcode", rs.getString("ob_sfcode"));
										newMap.put("ob_sfdetno", rs.getInt("ob_sfdetno"));
										newMap.put("ob_forecastid", rs.getInt("ob_forecastid"));
										newMap.put("ob_sacode", rs.getString("ob_sacode"));
										newMap.put("ob_sadetno", rs.getInt("ob_sadetno"));
										newMap.put("ob_saleid", rs.getInt("ob_saleid"));
										newMap.put("ob_ifreply", rsorder.getString("ob_ifreply"));
										baseDao.execute(SqlUtil.getInsertSqlByMap(newMap, "onorderbooking"));
										baseDao.updateByCondition("onorderbooking", "ob_qty=(ob_qty-" + y + ")", "ob_id=" + f.get("ob_id"));
										Map<String, Object> newMap1 = new HashMap<String, Object>();
										newMap1.put("ob_id", baseDao.getSeqId("onorderbooking_SEQ"));
										newMap1.put("ob_indate", rs.getDate("ob_indate"));
										newMap1.put("ob_startdate", rs.getDate("ob_startdate"));
										newMap1.put("ob_enddate", rs.getDate("ob_enddate"));
										newMap1.put("ob_pdid", rs.getInt("ob_pdid"));
										newMap1.put("ob_pucode", rs.getString("ob_pucode"));
										newMap1.put("ob_pudetno", rs.getInt("ob_pudetno"));
										newMap1.put("ob_delivery_default", rs.getDate("ob_delivery"));
										newMap1.put("ob_delivery", rs.getDate("ob_delivery"));
										newMap1.put("ob_qty", y);
										newMap1.put("ob_prodcode", rsorder.getString("ob_prodcode"));
										newMap1.put("ob_sfcode", rsorder.getString("ob_sfcode"));
										newMap1.put("ob_sfdetno", rsorder.getInt("ob_sfdetno"));
										newMap1.put("ob_forecastid", rsorder.getInt("ob_forecastid"));
										newMap1.put("ob_sacode", rsorder.getString("ob_sacode"));
										newMap1.put("ob_sadetno", rsorder.getInt("ob_sadetno"));
										newMap1.put("ob_saleid", rsorder.getInt("ob_saleid"));
										newMap1.put("ob_ifreply", rs.getString("ob_ifreply"));
										baseDao.execute(SqlUtil.getInsertSqlByMap(newMap1, "onorderbooking"));
									}
								}
							} else if ("在库".equals(g.get("type"))) {
								if (rs.next()) {
									String sqlhand = "select * from onhandbooking where ob_id=" + g.get("ob_id") + "";
									SqlRowList rshand = baseDao.queryForRowSet(sqlhand);
									if (rshand.next()) {
										baseDao.updateByCondition("onhandbooking", "ob_qty=(ob_qty-" + y + ")", "ob_id=" + g.get("ob_id"));
										Map<String, Object> newMap = new HashMap<String, Object>();
										newMap.put("ob_id", baseDao.getSeqId("onhandbooking_SEQ"));
										newMap.put("ob_indate", rshand.getDate("ob_indate"));
										newMap.put("ob_startdate", rshand.getDate("ob_startdate"));
										newMap.put("ob_enddate", rshand.getDate("ob_enddate"));
										newMap.put("ob_pidate", rshand.getDate("ob_pidate"));
										newMap.put("ob_inoutno", rshand.getString("ob_inoutno"));
										newMap.put("ob_pdno", rshand.getString("ob_pdno"));
										newMap.put("ob_batchcode", rshand.getString("ob_batchcode"));
										newMap.put("ob_prodcode", rshand.getString("ob_prodcode"));
										newMap.put("ob_qty", y);
										newMap.put("ob_sendqty", rshand.getGeneralInt("ob_sendqty"));
										newMap.put("ob_sourceid", rshand.getInt("ob_sourceid"));
										newMap.put("ob_sfcode", rs.getString("ob_sfcode"));
										newMap.put("ob_sfdetno", rs.getInt("ob_sfdetno"));
										newMap.put("ob_forecastid", rs.getInt("ob_forecastid"));
										newMap.put("ob_sacode", rs.getString("ob_sacode"));
										newMap.put("ob_sadetno", rs.getInt("ob_sadetno"));
										newMap.put("ob_saleid", rs.getInt("ob_saleid"));
										baseDao.execute(SqlUtil.getInsertSqlByMap(newMap, "onhandbooking"));
										baseDao.updateByCondition("onorderbooking", "ob_qty=(ob_qty-" + y + ")", "ob_id=" + f.get("ob_id"));
										Map<String, Object> newMap1 = new HashMap<String, Object>();
										newMap1.put("ob_id", baseDao.getSeqId("onorderbooking_SEQ"));
										newMap1.put("ob_indate", rs.getDate("ob_indate"));
										newMap1.put("ob_startdate", rs.getDate("ob_startdate"));
										newMap1.put("ob_enddate", rs.getDate("ob_enddate"));
										newMap1.put("ob_pdid", rs.getInt("ob_pdid"));
										newMap1.put("ob_pudetno", rs.getInt("ob_pudetno"));
										newMap1.put("ob_pucode", rs.getString("ob_pucode"));
										newMap1.put("ob_delivery_default", rs.getDate("ob_delivery"));
										newMap1.put("ob_delivery", rs.getDate("ob_delivery"));
										newMap1.put("ob_qty", y);
										newMap1.put("ob_prodcode", rshand.getString("ob_prodcode"));
										newMap1.put("ob_sfcode", rshand.getString("ob_sfcode"));
										newMap1.put("ob_sfdetno", rshand.getInt("ob_sfdetno"));
										newMap1.put("ob_forecastid", rshand.getInt("ob_forecastid"));
										newMap1.put("ob_sacode", rshand.getString("ob_sacode"));
										newMap1.put("ob_sadetno", rshand.getInt("ob_sadetno"));
										newMap1.put("ob_saleid", rshand.getInt("ob_saleid"));
										newMap1.put("ob_ifreply", rs.getString("ob_ifreply"));
										baseDao.execute(SqlUtil.getInsertSqlByMap(newMap1, "onorderbooking"));
									}
								}
							}
							c = x - y;
							y = 0;
						} else if (x < y) {
							if ("在途".equals(g.get("type"))) {
								if (rs.next()) {
									String sqlorder = "select * from onorderbooking where ob_id=" + g.get("ob_id") + "";
									SqlRowList rsorder = baseDao.queryForRowSet(sqlorder);
									if (rsorder.next()) {
										baseDao.updateByCondition("onorderbooking", "ob_qty=(ob_qty-" + x + ")", "ob_id=" + g.get("ob_id"));
										Map<String, Object> newMap = new HashMap<String, Object>();
										newMap.put("ob_id", baseDao.getSeqId("onorderbooking_SEQ"));
										newMap.put("ob_indate", rsorder.getDate("ob_indate"));
										newMap.put("ob_startdate", rsorder.getDate("ob_startdate"));
										newMap.put("ob_enddate", rsorder.getDate("ob_enddate"));
										newMap.put("ob_pdid", rsorder.getInt("ob_pdid"));
										newMap.put("ob_pudetno", rsorder.getInt("ob_pudetno"));
										newMap.put("ob_pucode", rsorder.getString("ob_pucode"));
										newMap.put("ob_delivery_default", rsorder.getDate("ob_delivery"));
										newMap.put("ob_delivery", rsorder.getDate("ob_delivery"));
										newMap.put("ob_qty", x);
										newMap.put("ob_prodcode", rsorder.getString("ob_prodcode"));
										newMap.put("ob_sfcode", rs.getString("ob_sfcode"));
										newMap.put("ob_sfdetno", rs.getInt("ob_sfdetno"));
										newMap.put("ob_forecastid", rs.getInt("ob_forecastid"));
										newMap.put("ob_sacode", rs.getString("ob_sacode"));
										newMap.put("ob_sadetno", rs.getInt("ob_sadetno"));
										newMap.put("ob_saleid", rs.getInt("ob_saleid"));
										newMap.put("ob_ifreply", rsorder.getString("ob_ifreply"));
										baseDao.execute(SqlUtil.getInsertSqlByMap(newMap, "onorderbooking"));
										baseDao.updateByCondition("onorderbooking", "ob_qty=(ob_qty-" + x + ")", "ob_id=" + f.get("ob_id"));
										Map<String, Object> newMap1 = new HashMap<String, Object>();
										newMap1.put("ob_id", baseDao.getSeqId("onorderbooking_SEQ"));
										newMap1.put("ob_indate", rs.getDate("ob_indate"));
										newMap1.put("ob_startdate", rs.getDate("ob_startdate"));
										newMap1.put("ob_enddate", rs.getDate("ob_enddate"));
										newMap1.put("ob_pdid", rs.getInt("ob_pdid"));
										newMap1.put("ob_pudetno", rs.getInt("ob_pudetno"));
										newMap1.put("ob_pucode", rs.getString("ob_pucode"));
										newMap1.put("ob_delivery_default", rs.getDate("ob_delivery"));
										newMap1.put("ob_delivery", rs.getDate("ob_delivery"));
										newMap1.put("ob_qty", x);
										newMap1.put("ob_prodcode", rsorder.getString("ob_prodcode"));
										newMap1.put("ob_sfcode", rsorder.getString("ob_sfcode"));
										newMap1.put("ob_sfdetno", rsorder.getInt("ob_sfdetno"));
										newMap1.put("ob_forecastid", rsorder.getInt("ob_forecastid"));
										newMap1.put("ob_sacode", rsorder.getString("ob_sacode"));
										newMap1.put("ob_sadetno", rsorder.getInt("ob_sadetno"));
										newMap1.put("ob_saleid", rsorder.getInt("ob_saleid"));
										newMap1.put("ob_ifreply", rs.getString("ob_ifreply"));
										baseDao.execute(SqlUtil.getInsertSqlByMap(newMap1, "onorderbooking"));
									}
								}
							} else if ("在库".equals(g.get("type"))) {
								if (rs.next()) {
									String sqlhand = "select * from onhandbooking where ob_id=" + g.get("ob_id") + "";
									SqlRowList rshand = baseDao.queryForRowSet(sqlhand);
									if (rshand.next()) {
										baseDao.updateByCondition("onhandbooking", "ob_qty=(ob_qty-" + x + ")", "ob_id=" + g.get("ob_id"));
										Map<String, Object> newMap = new HashMap<String, Object>();
										newMap.put("ob_id", baseDao.getSeqId("onhandbooking_SEQ"));
										newMap.put("ob_indate", rshand.getDate("ob_indate"));
										newMap.put("ob_startdate", rshand.getDate("ob_startdate"));
										newMap.put("ob_enddate", rshand.getDate("ob_enddate"));
										newMap.put("ob_pidate", rshand.getDate("ob_pidate"));
										newMap.put("ob_inoutno", rshand.getString("ob_inoutno"));
										newMap.put("ob_pdno", rshand.getString("ob_pdno"));
										newMap.put("ob_batchcode", rshand.getString("ob_batchcode"));
										newMap.put("ob_prodcode", rshand.getString("ob_prodcode"));
										newMap.put("ob_qty", x);
										newMap.put("ob_sendqty", rshand.getGeneralInt("ob_sendqty"));
										newMap.put("ob_sourceid", rshand.getInt("ob_sourceid"));
										newMap.put("ob_sfcode", rs.getString("ob_sfcode"));
										newMap.put("ob_sfdetno", rs.getInt("ob_sfdetno"));
										newMap.put("ob_forecastid", rs.getInt("ob_forecastid"));
										newMap.put("ob_sacode", rs.getString("ob_sacode"));
										newMap.put("ob_sadetno", rs.getInt("ob_sadetno"));
										newMap.put("ob_saleid", rs.getInt("ob_saleid"));
										baseDao.execute(SqlUtil.getInsertSqlByMap(newMap, "onhandbooking"));
										baseDao.updateByCondition("onorderbooking", "ob_qty=(ob_qty-" + x + ")", "ob_id=" + f.get("ob_id"));
										Map<String, Object> newMap1 = new HashMap<String, Object>();
										newMap1.put("ob_id", baseDao.getSeqId("onorderbooking_SEQ"));
										newMap1.put("ob_indate", rs.getDate("ob_indate"));
										newMap1.put("ob_startdate", rs.getDate("ob_startdate"));
										newMap1.put("ob_enddate", rs.getDate("ob_enddate"));
										newMap1.put("ob_pdid", rs.getInt("ob_pdid"));
										newMap1.put("ob_pudetno", rs.getInt("ob_pudetno"));
										newMap1.put("ob_pucode", rs.getString("ob_pucode"));
										newMap1.put("ob_delivery_default", rs.getDate("ob_delivery"));
										newMap1.put("ob_delivery", rs.getDate("ob_delivery"));
										newMap1.put("ob_qty", x);
										newMap1.put("ob_prodcode", rshand.getString("ob_prodcode"));
										newMap1.put("ob_sfcode", rshand.getString("ob_sfcode"));
										newMap1.put("ob_sfdetno", rshand.getInt("ob_sfdetno"));
										newMap1.put("ob_forecastid", rshand.getInt("ob_forecastid"));
										newMap1.put("ob_sacode", rshand.getString("ob_sacode"));
										newMap1.put("ob_sadetno", rshand.getInt("ob_sadetno"));
										newMap1.put("ob_saleid", rshand.getInt("ob_saleid"));
										newMap1.put("ob_ifreply", rs.getString("ob_ifreply"));
										baseDao.execute(SqlUtil.getInsertSqlByMap(newMap1, "onorderbooking"));
									}
								}
							}
							y = y - x;
						}
					} else if (c > 0) {
						if (c > y) {
							if ("在途".equals(g.get("type"))) {
								if (rs.next()) {
									String sqlorder = "select * from onorderbooking where ob_id=" + g.get("ob_id") + "";
									SqlRowList rsorder = baseDao.queryForRowSet(sqlorder);
									if (rsorder.next()) {
										baseDao.updateByCondition("onorderbooking", "ob_qty=(ob_qty-" + y + ")", "ob_id=" + g.get("ob_id"));
										Map<String, Object> newMap = new HashMap<String, Object>();
										newMap.put("ob_id", baseDao.getSeqId("onorderbooking_SEQ"));
										newMap.put("ob_indate", rsorder.getDate("ob_indate"));
										newMap.put("ob_startdate", rsorder.getDate("ob_startdate"));
										newMap.put("ob_enddate", rsorder.getDate("ob_enddate"));
										newMap.put("ob_pdid", rsorder.getInt("ob_pdid"));
										newMap.put("ob_pudetno", rsorder.getInt("ob_pudetno"));
										newMap.put("ob_pucode", rsorder.getString("ob_pucode"));
										newMap.put("ob_delivery_default", rsorder.getDate("ob_delivery"));
										newMap.put("ob_delivery", rsorder.getDate("ob_delivery"));
										newMap.put("ob_qty", y);
										newMap.put("ob_prodcode", rsorder.getString("ob_prodcode"));
										newMap.put("ob_sfcode", rs.getString("ob_sfcode"));
										newMap.put("ob_sfdetno", rs.getInt("ob_sfdetno"));
										newMap.put("ob_forecastid", rs.getInt("ob_forecastid"));
										newMap.put("ob_sacode", rs.getString("ob_sacode"));
										newMap.put("ob_sadetno", rs.getInt("ob_sadetno"));
										newMap.put("ob_saleid", rs.getInt("ob_saleid"));
										newMap.put("ob_ifreply", rsorder.getString("ob_ifreply"));
										baseDao.execute(SqlUtil.getInsertSqlByMap(newMap, "onorderbooking"));
										baseDao.updateByCondition("onorderbooking", "ob_qty=(ob_qty-" + y + ")", "ob_id=" + f.get("ob_id"));
										Map<String, Object> newMap1 = new HashMap<String, Object>();
										newMap1.put("ob_id", baseDao.getSeqId("onorderbooking_SEQ"));
										newMap1.put("ob_indate", rs.getDate("ob_indate"));
										newMap1.put("ob_startdate", rs.getDate("ob_startdate"));
										newMap1.put("ob_enddate", rs.getDate("ob_enddate"));
										newMap1.put("ob_pdid", rs.getInt("ob_pdid"));
										newMap1.put("ob_pudetno", rs.getInt("ob_pudetno"));
										newMap1.put("ob_pucode", rs.getString("ob_pucode"));
										newMap1.put("ob_delivery_default", rs.getDate("ob_delivery"));
										newMap1.put("ob_delivery", rs.getDate("ob_delivery"));
										newMap1.put("ob_qty", y);
										newMap1.put("ob_prodcode", rsorder.getString("ob_prodcode"));
										newMap1.put("ob_sfcode", rsorder.getString("ob_sfcode"));
										newMap1.put("ob_sfdetno", rsorder.getInt("ob_sfdetno"));
										newMap1.put("ob_forecastid", rsorder.getInt("ob_forecastid"));
										newMap1.put("ob_sacode", rsorder.getString("ob_sacode"));
										newMap1.put("ob_sadetno", rsorder.getInt("ob_sadetno"));
										newMap1.put("ob_saleid", rsorder.getInt("ob_saleid"));
										newMap1.put("ob_ifreply", rs.getString("ob_ifreply"));
										baseDao.execute(SqlUtil.getInsertSqlByMap(newMap1, "onorderbooking"));
									}
								}
							} else if ("在库".equals(g.get("type"))) {
								if (rs.next()) {
									String sqlhand = "select * from onhandbooking where ob_id=" + g.get("ob_id") + "";
									SqlRowList rshand = baseDao.queryForRowSet(sqlhand);
									if (rshand.next()) {
										baseDao.updateByCondition("onhandbooking", "ob_qty=(ob_qty-" + y + ")", "ob_id=" + g.get("ob_id"));
										Map<String, Object> newMap = new HashMap<String, Object>();
										newMap.put("ob_id", baseDao.getSeqId("onhandbooking_SEQ"));
										newMap.put("ob_indate", rshand.getDate("ob_indate"));
										newMap.put("ob_startdate", rshand.getDate("ob_startdate"));
										newMap.put("ob_enddate", rshand.getDate("ob_enddate"));
										newMap.put("ob_pidate", rshand.getDate("ob_pidate"));
										newMap.put("ob_inoutno", rshand.getString("ob_inoutno"));
										newMap.put("ob_pdno", rshand.getString("ob_pdno"));
										newMap.put("ob_batchcode", rshand.getString("ob_batchcode"));
										newMap.put("ob_prodcode", rshand.getString("ob_prodcode"));
										newMap.put("ob_qty", y);
										newMap.put("ob_sendqty", rshand.getGeneralInt("ob_sendqty"));
										newMap.put("ob_sourceid", rshand.getInt("ob_sourceid"));
										newMap.put("ob_sfcode", rs.getString("ob_sfcode"));
										newMap.put("ob_sfdetno", rs.getInt("ob_sfdetno"));
										newMap.put("ob_forecastid", rs.getInt("ob_forecastid"));
										newMap.put("ob_sacode", rs.getString("ob_sacode"));
										newMap.put("ob_sadetno", rs.getInt("ob_sadetno"));
										newMap.put("ob_saleid", rs.getInt("ob_saleid"));
										baseDao.execute(SqlUtil.getInsertSqlByMap(newMap, "onhandbooking"));
										baseDao.updateByCondition("onorderbooking", "ob_qty=(ob_qty-" + y + ")", "ob_id=" + f.get("ob_id"));
										Map<String, Object> newMap1 = new HashMap<String, Object>();
										newMap1.put("ob_id", baseDao.getSeqId("onorderbooking_SEQ"));
										newMap1.put("ob_indate", rs.getDate("ob_indate"));
										newMap1.put("ob_startdate", rs.getDate("ob_startdate"));
										newMap1.put("ob_enddate", rs.getDate("ob_enddate"));
										newMap1.put("ob_pdid", rs.getInt("ob_pdid"));
										newMap1.put("ob_pudetno", rs.getInt("ob_pudetno"));
										newMap1.put("ob_pucode", rs.getString("ob_pucode"));
										newMap1.put("ob_delivery_default", rs.getDate("ob_delivery"));
										newMap1.put("ob_delivery", rs.getDate("ob_delivery"));
										newMap1.put("ob_qty", y);
										newMap1.put("ob_prodcode", rshand.getString("ob_prodcode"));
										newMap1.put("ob_sfcode", rshand.getString("ob_sfcode"));
										newMap1.put("ob_sfdetno", rshand.getInt("ob_sfdetno"));
										newMap1.put("ob_forecastid", rshand.getInt("ob_forecastid"));
										newMap1.put("ob_sacode", rshand.getString("ob_sacode"));
										newMap1.put("ob_sadetno", rshand.getInt("ob_sadetno"));
										newMap1.put("ob_saleid", rshand.getInt("ob_saleid"));
										newMap1.put("ob_ifreply", rs.getString("ob_ifreply"));
										baseDao.execute(SqlUtil.getInsertSqlByMap(newMap1, "onorderbooking"));
									}
								}
							}
							c = c - y;
							y = 0;
						} else if (c <= y) {
							if ("在途".equals(g.get("type"))) {
								if (rs.next()) {
									String sqlorder = "select * from onorderbooking where ob_id=" + g.get("ob_id") + "";
									SqlRowList rsorder = baseDao.queryForRowSet(sqlorder);
									if (rsorder.next()) {
										baseDao.updateByCondition("onorderbooking", "ob_qty=(ob_qty-" + c + ")", "ob_id=" + g.get("ob_id"));
										Map<String, Object> newMap = new HashMap<String, Object>();
										newMap.put("ob_id", baseDao.getSeqId("onorderbooking_SEQ"));
										newMap.put("ob_indate", rsorder.getDate("ob_indate"));
										newMap.put("ob_startdate", rsorder.getDate("ob_startdate"));
										newMap.put("ob_enddate", rsorder.getDate("ob_enddate"));
										newMap.put("ob_pdid", rsorder.getInt("ob_pdid"));
										newMap.put("ob_pudetno", rsorder.getInt("ob_pudetno"));
										newMap.put("ob_pucode", rsorder.getString("ob_pucode"));
										newMap.put("ob_delivery_default", rsorder.getDate("ob_delivery"));
										newMap.put("ob_delivery", rsorder.getDate("ob_delivery"));
										newMap.put("ob_qty", c);
										newMap.put("ob_prodcode", rsorder.getString("ob_prodcode"));
										newMap.put("ob_sfcode", rs.getString("ob_sfcode"));
										newMap.put("ob_sfdetno", rs.getInt("ob_sfdetno"));
										newMap.put("ob_forecastid", rs.getInt("ob_forecastid"));
										newMap.put("ob_sacode", rs.getString("ob_sacode"));
										newMap.put("ob_sadetno", rs.getInt("ob_sadetno"));
										newMap.put("ob_saleid", rs.getInt("ob_saleid"));
										newMap.put("ob_ifreply", rsorder.getString("ob_ifreply"));
										baseDao.execute(SqlUtil.getInsertSqlByMap(newMap, "onorderbooking"));
										baseDao.updateByCondition("onorderbooking", "ob_qty=(ob_qty-" + c + ")", "ob_id=" + f.get("ob_id"));
										Map<String, Object> newMap1 = new HashMap<String, Object>();
										newMap1.put("ob_id", baseDao.getSeqId("onorderbooking_SEQ"));
										newMap1.put("ob_indate", rs.getDate("ob_indate"));
										newMap1.put("ob_startdate", rs.getDate("ob_startdate"));
										newMap1.put("ob_enddate", rs.getDate("ob_enddate"));
										newMap1.put("ob_pdid", rs.getInt("ob_pdid"));
										newMap1.put("ob_pudetno", rs.getInt("ob_pudetno"));
										newMap1.put("ob_pucode", rs.getString("ob_pucode"));
										newMap1.put("ob_delivery_default", rs.getDate("ob_delivery"));
										newMap1.put("ob_delivery", rs.getDate("ob_delivery"));
										newMap1.put("ob_qty", c);
										newMap1.put("ob_prodcode", rsorder.getString("ob_prodcode"));
										newMap1.put("ob_sfcode", rsorder.getString("ob_sfcode"));
										newMap1.put("ob_sfdetno", rsorder.getInt("ob_sfdetno"));
										newMap1.put("ob_forecastid", rsorder.getInt("ob_forecastid"));
										newMap1.put("ob_sacode", rsorder.getString("ob_sacode"));
										newMap1.put("ob_sadetno", rsorder.getInt("ob_sadetno"));
										newMap1.put("ob_saleid", rsorder.getInt("ob_saleid"));
										newMap1.put("ob_ifreply", rs.getString("ob_ifreply"));
										baseDao.execute(SqlUtil.getInsertSqlByMap(newMap1, "onorderbooking"));
									}
								}
							} else if ("在库".equals(g.get("type"))) {
								if (rs.next()) {
									String sqlhand = "select * from onhandbooking where ob_id=" + g.get("ob_id") + "";
									SqlRowList rshand = baseDao.queryForRowSet(sqlhand);
									if (rshand.next()) {
										baseDao.updateByCondition("onhandbooking", "ob_qty=(ob_qty-" + c + ")", "ob_id=" + g.get("ob_id"));
										Map<String, Object> newMap = new HashMap<String, Object>();
										newMap.put("ob_id", baseDao.getSeqId("onhandbooking_SEQ"));
										newMap.put("ob_indate", rshand.getDate("ob_indate"));
										newMap.put("ob_startdate", rshand.getDate("ob_startdate"));
										newMap.put("ob_enddate", rshand.getDate("ob_enddate"));
										newMap.put("ob_pidate", rshand.getDate("ob_pidate"));
										newMap.put("ob_inoutno", rshand.getString("ob_inoutno"));
										newMap.put("ob_pdno", rshand.getString("ob_pdno"));
										newMap.put("ob_batchcode", rshand.getString("ob_batchcode"));
										newMap.put("ob_prodcode", rshand.getString("ob_prodcode"));
										newMap.put("ob_qty", c);
										newMap.put("ob_sendqty", rshand.getGeneralInt("ob_sendqty"));
										newMap.put("ob_sourceid", rshand.getInt("ob_sourceid"));
										newMap.put("ob_sfcode", rs.getString("ob_sfcode"));
										newMap.put("ob_sfdetno", rs.getInt("ob_sfdetno"));
										newMap.put("ob_forecastid", rs.getInt("ob_forecastid"));
										newMap.put("ob_sacode", rs.getString("ob_sacode"));
										newMap.put("ob_sadetno", rs.getInt("ob_sadetno"));
										newMap.put("ob_saleid", rs.getInt("ob_saleid"));
										baseDao.execute(SqlUtil.getInsertSqlByMap(newMap, "onhandbooking"));
										baseDao.updateByCondition("onorderbooking", "ob_qty=(ob_qty-" + c + ")", "ob_id=" + f.get("ob_id"));
										Map<String, Object> newMap1 = new HashMap<String, Object>();
										newMap1.put("ob_id", baseDao.getSeqId("onorderbooking_SEQ"));
										newMap1.put("ob_indate", rs.getDate("ob_indate"));
										newMap1.put("ob_startdate", rs.getDate("ob_startdate"));
										newMap1.put("ob_enddate", rs.getDate("ob_enddate"));
										newMap1.put("ob_pdid", rs.getInt("ob_pdid"));
										newMap1.put("ob_pucode", rs.getString("ob_pucode"));
										newMap1.put("ob_pudetno", rs.getInt("ob_pudetno"));
										newMap1.put("ob_delivery_default", rs.getDate("ob_delivery"));
										newMap1.put("ob_delivery", rs.getDate("ob_delivery"));
										newMap1.put("ob_qty", c);
										newMap1.put("ob_prodcode", rshand.getString("ob_prodcode"));
										newMap1.put("ob_sfcode", rshand.getString("ob_sfcode"));
										newMap1.put("ob_sfdetno", rshand.getInt("ob_sfdetno"));
										newMap1.put("ob_forecastid", rshand.getInt("ob_forecastid"));
										newMap1.put("ob_sacode", rshand.getString("ob_sacode"));
										newMap1.put("ob_sadetno", rshand.getInt("ob_sadetno"));
										newMap1.put("ob_saleid", rshand.getInt("ob_saleid"));
										newMap1.put("ob_ifreply", rs.getString("ob_ifreply"));
										baseDao.execute(SqlUtil.getInsertSqlByMap(newMap1, "onorderbooking"));
									}
								}
							}
							y = y - c;
						}

					}
				}
				a++;
			}
			b++;
		}
		String sql = "select ob_pucode,ob_sacode,ob_sadetno,ob_pudetno,count(1) from onorderbooking group by ob_pucode,ob_sacode,ob_sadetno,ob_pudetno having count(1)>1";
		SqlRowList rs = baseDao.queryForRowSet(sql);
		while (rs.next()) {
			String newsql = "select max(ob_indate),max(ob_startdate),max(ob_enddate),max(ob_pdid),max(ob_pucode),max(ob_pudetno),"
					+ "max(ob_prodcode),max(ob_delivery),sum(ob_qty) ob_qty,max(ob_sfcode),max(ob_sfdetno),max(ob_forecastid),"
					+ "max(ob_sacode),max(ob_sadetno),max(ob_saleid),max(ob_ifreply),max(OB_DELIVERY_DEFAULT),max(ob_remark) "
					+ "from onorderbooking where ob_pucode='" + rs.getString("ob_pucode") + "' and ob_sacode='" + rs.getString("ob_sacode")
					+ "' " + "and ob_sadetno=" + rs.getInt("ob_sadetno") + " and ob_pudetno=" + rs.getInt("ob_pudetno") + "";
			SqlRowList newrs = baseDao.queryForRowSet(newsql);
			if (newrs.next()) {
				int newid = baseDao.getSeqId("onorderbooking_SEQ");
				Map<String, Object> newMap1 = new HashMap<String, Object>();
				newMap1.put("ob_id", newid);
				newMap1.put("ob_indate", newrs.getDate("max(ob_indate)"));
				newMap1.put("ob_startdate", newrs.getDate("max(ob_startdate)"));
				newMap1.put("ob_enddate", newrs.getDate("max(ob_enddate)"));
				newMap1.put("ob_pdid", newrs.getInt("max(ob_pdid)"));
				newMap1.put("ob_pucode", newrs.getString("max(ob_pucode)"));
				newMap1.put("ob_pudetno", newrs.getInt("max(ob_pudetno)"));
				newMap1.put("ob_delivery_default", newrs.getDate("max(ob_delivery_default)"));
				newMap1.put("ob_delivery", newrs.getDate("max(ob_delivery)"));
				newMap1.put("ob_qty", newrs.getInt("ob_qty"));
				newMap1.put("ob_prodcode", newrs.getString("max(ob_prodcode)"));
				newMap1.put("ob_sfcode", newrs.getString("max(ob_sfcode)"));
				newMap1.put("ob_sfdetno", newrs.getInt("max(ob_sfdetno)"));
				newMap1.put("ob_forecastid", newrs.getInt("max(ob_forecastid)"));
				newMap1.put("ob_sacode", newrs.getString("max(ob_sacode)"));
				newMap1.put("ob_sadetno", newrs.getInt("max(ob_sadetno)"));
				newMap1.put("ob_saleid", newrs.getInt("max(ob_saleid)"));
				newMap1.put("ob_ifreply", newrs.getString("max(ob_ifreply)"));
				baseDao.execute(SqlUtil.getInsertSqlByMap(newMap1, "onorderbooking"));
				String deletesql = "delete onorderbooking where ob_pucode='" + rs.getString("ob_pucode") + "' and ob_sacode='"
						+ rs.getString("ob_sacode") + "' " + "and ob_sadetno=" + rs.getInt("ob_sadetno") + " and ob_pudetno="
						+ rs.getInt("ob_pudetno") + " and ob_id<>" + newid + "";
				baseDao.execute(deletesql);
			}
		}
		return "OK";
	}

	@Override
	public String purchaseToPrePay(String data, String caller) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		int index = 0;
		if (maps.size() > 0) {
			String log = null;
			Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(maps, new Object[] { "pu_vendcode", "pu_currency" });
			Set<Object> mapSet = groups.keySet();
			List<Map<Object, Object>> items;
			for (Object s : mapSet) {
				items = groups.get(s);
				// 转入通知单主记录
				Integer pu_id = Integer.parseInt(items.get(0).get("pu_id").toString());
				Key key = transferRepository.transfer("PurchaseToPrePay", pu_id);
				if (key != null) {
					int pp_id = key.getId();
					baseDao.execute("update PrePay set pp_indate=sysdate,pp_recorder='" + SystemSession.getUser().getEm_name()
							+ "',pp_buyer='" + SystemSession.getUser().getEm_name() + "' where pp_id=" + pp_id);
					index++;
					// 转入明细
					transferRepository.transfer("PurchaseToPrePay", items, key);
					baseDao.execute("update PrePay set pp_amount=(select sum(ppd_nowbalance) from PrePayDetail where ppd_ppid=" + pp_id
							+ "),pp_vmamount=(select sum(ppd_nowbalance) from PrePayDetail where ppd_ppid=" + pp_id + ") where pp_id="
							+ pp_id + "");
					log = "转入成功，预付账款号:"
							+ "<a href=\"javascript:openUrl('jsps/fa/arp/prepay.jsp?whoami=PrePay!Arp!PAMT&formCondition=pp_idIS" + pp_id
							+ "&gridCondition=ppd_ppidIS" + pp_id + "')\">" + key.getCode() + "</a><hr>";
					sb.append(index).append(": ").append(log).append("<hr>");
				}
			}
			return sb.toString();
		}
		return null;
	}

	@Override
	public String turnOtherProdIO(String caller, String pi_inoutno, String pi_class, int pi_id) {
		StringBuffer sb = new StringBuffer();
		int newpiid = 0;
		String error = "";
		Object[] obj = baseDao.getFieldsDataByCondition("documentsetup", "ds_table,ds_inorout", "ds_name='" + pi_class + "'");
		Object[] obj2 = baseDao.getFieldsDataByCondition("Prodinout", "pi_class,pi_inoutno", "pi_id=" + pi_id);
		String turnCaller = (String) obj[0];
		if ("".equals(pi_inoutno)) {
			String inOrOut = (String) obj[1];
			boolean isIn = true;
			if ("-IN".equals(inOrOut) || "OUT".equals(inOrOut)) {
				isIn = false;
			}
			String newpi_inoutno = baseDao.sGetMaxNumber(turnCaller, 2);
			newpiid = baseDao.getSeqId("PRODINOUT_SEQ");
			if (isIn) {
				baseDao.execute(
						"insert into prodinout (pi_id,pi_inoutno,pi_class,pi_whcode,pi_date,pi_recorddate,pi_currency,pi_rate,pi_cardcode,pi_title,pi_Status,pi_statuscode,pi_printstatus,pi_printstatuscode,pi_recordman,pi_cop,pi_departmentcode,pi_departmentname,pi_invostatus,pi_invostatuscode) select ?,?,?,?,sysdate,sysdate,pi_currency,pi_rate,pi_cardcode,pi_title,'未过账','UNPOST','未打印','UNPRINT',?,pi_cop,pi_departmentcode,pi_departmentname,'在录入','ENTERING' from  prodinout where pi_id=?",
						newpiid, newpi_inoutno, pi_class, null, SystemSession.getUser().getEm_name(), pi_id);
				baseDao.execute(
						"insert into prodiodetail (pd_id,pd_piid,pd_inoutno,pd_piclass,pd_pdno,pd_ordercode,"
								+ " pd_orderid,pd_orderdetno,pd_description,pd_plancode,pd_prodcode,pd_prodid,pd_prodmadedate,"
								+ " pd_inqty,pd_outqty,pd_price,pd_avprice,pd_remark,pd_seller,pd_sellercode,pd_pocode,"
								+ " pd_accountstatus,pd_accountstatuscode,pd_salecode,pd_custcode,pd_sourcebatch)"
								+ " select PRODIODETAIL_SEQ.nextval,?,?,?,pd_pdno,pd_ordercode,pd_orderid,pd_orderdetno,pd_description,pd_plancode,"
								+ " pd_prodcode,pd_prodid,ba_date,pd_outqty,0,pd_price,pd_avprice,pd_remark,pd_seller,pd_sellercode,"
								+ " pd_pocode,'未核算','UNACCOUNT',pd_salecode,pd_custcode ,case when NVL(ba_sourcebatch,' ')<>' ' then ba_sourcebatch else ba_code end bi_sourcebatch from prodiodetail left join batch on pd_batchid=ba_id where pd_piid=?",
						newpiid, newpi_inoutno, pi_class, pi_id);
				error = turnBarcode(newpi_inoutno, pi_class, pi_id, "").get("error").toString();
			} else {
				baseDao.execute(
						"insert into prodinout (pi_id,pi_inoutno,pi_class,pi_whcode,pi_whname,pi_date,pi_recorddate,pi_currency,pi_rate,pi_cardcode,pi_title,pi_Status,pi_statuscode,pi_printstatus,pi_printstatuscode,pi_recordman,pi_cop,pi_departmentcode,pi_departmentname,pi_invostatus,pi_invostatuscode) select ?,?,?,pi_whcode,pi_whname,sysdate,sysdate,pi_currency,pi_rate,pi_cardcode,pi_title,'未过账','UNPOST','未打印','UNPRINT',?,pi_cop,pi_departmentcode,pi_departmentname,'在录入','ENTERING' from  prodinout where pi_id=?",
						newpiid, newpi_inoutno, pi_class, SystemSession.getUser().getEm_name(), pi_id);
				baseDao.execute(
						"insert into prodiodetail (pd_id,pd_piid,pd_inoutno,pd_piclass,pd_pdno,pd_ordercode,"
								+ " pd_orderid,pd_orderdetno,pd_description,pd_plancode,pd_batchcode,pd_prodcode,pd_prodid,pd_prodmadedate,"
								+ " pd_inqty,pd_outqty,pd_price,pd_avprice,pd_remark,pd_seller,pd_sellercode,pd_pocode,pd_whcode,pd_whname,"
								+ " pd_accountstatus,pd_accountstatuscode,pd_salecode,pd_custcode)"
								+ " select PRODIODETAIL_SEQ.nextval,?,?,?,pd_pdno,pd_ordercode,pd_orderid,pd_orderdetno,pd_description,pd_plancode,"
								+ " pd_batchcode,pd_prodcode,pd_prodid,sysdate,0,pd_inqty,pd_price,pd_avprice,pd_remark,pd_seller,pd_sellercode,"
								+ " pd_pocode,pd_whcode,pd_whname,'未核算','UNACCOUNT',pd_salecode,pd_custcode from prodiodetail where pd_piid=?",
						newpiid, newpi_inoutno, pi_class, pi_id);
			}
			sb.append("转入成功," + error + pi_class + "号:"
					+ "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + newpiid
					+ "&gridCondition=pd_piidIS" + newpiid + "&whoami=" + turnCaller + "')\">" + newpi_inoutno + "</a>&nbsp;");
			/**
			 * lidy 2017090233
			 * 采购验收单转单按钮判断是否为盘盈、盘亏、报废、其他出库、其他入库，是这5种的时pd_ordercode
			 * 、pd_orderdetno清空 && 把pi_cardcode、pi_title也清空
			 */
			if ("盘盈调整单".equals(pi_class) || "盘亏调整单".equals(pi_class) || "其它入库单".equals(pi_class) || "其它出库单".equals(pi_class)
					|| "报废单".equals(pi_class)) {
				baseDao.updateByCondition("prodiodetail", "pd_ordercode=null,pd_orderdetno=null", "pd_piid=" + newpiid);
				baseDao.updateByCondition("Prodinout", "pi_cardcode=null,pi_title=null", "pi_id=" + newpiid);
			}
			baseDao.logger.turn("转" + pi_class + ":" + newpi_inoutno, caller, "pi_id", pi_id);
			baseDao.logger.turn("由" + obj2[0] + ":" + obj2[1] + "转入", turnCaller, "pi_id", newpiid);
		} else {
			error = turnBarcode(pi_inoutno, pi_class, pi_id, "barcodeio").get("error").toString();
			if (error != null && !("").equals(error)) {
				BaseUtil.showError(error);
			}
			Integer newPiid = Integer.valueOf(turnBarcode(pi_inoutno, pi_class, pi_id, "barcodeio").get("newpiid").toString());
			sb.append("转入条码成功," + pi_class + "号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS"
					+ newPiid + "&gridCondition=pd_piidIS" + newPiid + "&whoami=" + turnCaller + "')\">" + pi_inoutno + "</a>&nbsp;");
			baseDao.logger.turn("由" + obj2[0] + ":" + obj2[1] + "转入条码信息", turnCaller, "pi_id", newPiid);
		}
		handlerService.handler(caller, "turn", "after", new Object[] { pi_id });
		return sb.toString();
	}

	private Map<String, Object> turnBarcode(String pi_inoutno, String pi_class, int pi_id, String type) {
		int newpiid = 0;
		String error = "";
		SqlRowList rs = null;
		Map<String, Object> map = new HashMap<String, Object>();
		if (("barcodeio").equals(type)) {
			rs = baseDao.queryForRowSet("select pi_id from prodinout where pi_inoutno=? and pi_class= ?", pi_inoutno, pi_class);
			if (rs.next()) {
				newpiid = rs.getInt("pi_id");
			} else {
				error = "条码转入单号不存在或该单已存在条码!";
			}
		}
		rs = baseDao.queryForRowSet(
				"select * from prodinout left join barcodeio on bi_piid = pi_id where pi_inoutno=? and nvl(bi_piid,0)=0 and pi_class=?",
				pi_inoutno, pi_class);
		if (rs.next()) {
			newpiid = rs.getInt("pi_id");
			SqlRowList rs2 = null;
			rs2 = baseDao
					.queryForRowSet(
							"select A.pd_pdno pd_pdno,A.pd_prodcode pd_prodcode,A.pd_outqty pd_outqty,B.pd_inqty from prodiodetail A left join prodiodetail B on B.pd_piid=? and A.pd_pdno=B.pd_pdno where A.pd_piid=? and (A.pd_prodcode<>B.pd_prodcode or A.pd_outqty<>B.pd_inqty or B.pd_id is null)",
							newpiid, pi_id);
			if (rs2.next()) {
				error = "单号" + pi_inoutno + "明细行行号[" + rs2.getInt("pd_pdno") + "]不存在或物料编号不是" + rs2.getString("pd_prodcode") + "或入库数量不为"
						+ rs2.getInt("pd_outqty") + "!";
				map.put("newpiid", newpiid);
				map.put("error", error);
				return map;
			}
			rs2 = baseDao
					.queryForRowSet(
							"select A.pd_pdno pd_pdno,A.pd_prodcode,A.pd_outqty,B.pd_inqty from prodiodetail A left join prodiodetail B on B.pd_piid=? and A.pd_pdno=B.pd_pdno where A.pd_piid=? and B.pd_id is null",
							pi_id, newpiid);
			if (rs2.next()) {
				error = "单号" + pi_inoutno + "明细行行号[" + rs2.getInt("pd_pdno") + "]多余!";
				map.put("newpiid", newpiid);
				map.put("error", error);
				return map;
			}
			rs2 = baseDao.queryForRowSet("select pd_batchcode from prodiodetail where pd_piid = ? "
					+ " group by pd_batchcode  having count(pd_batchcode)>1 ", pi_id);
			if (rs2.next()) {
				error = "批次[" + rs2.getString("pd_batchcode") + "]存在多行,条码不允许转单!";
				map.put("newpiid", newpiid);
				map.put("error", error);
				return map;
			}
			baseDao.execute(
					"insert into barcodeio(bi_id,bi_barcode,bi_vendbarcode,bi_outboxcode,bi_piid,bi_inoutno,bi_pdno,bi_status,bi_printstatus,"
							+ " bi_prodcode,bi_inqty,bi_madedate,bi_prodid,bi_validdate,bi_piclass,bi_oldbarcode,bi_inman,bi_indate,bi_type,bi_sourcebatch)"
							+ " select barcodeio_seq.nextval,bi_barcode,bar_vendbarcode,bi_outboxcode,?,?,bi_pdno,0,0,"
							+ " bi_prodcode,bi_outqty,bar_madedate,bi_prodid,bi_validdate,?,bi_oldbarcode,?,sysdate,'转单' ,case when ba_sourcebatch<>' ' then ba_sourcebatch else ba_code end bi_sourcebatch"
							+ " from  barcodeio left join barcode on bar_id=bi_barid left join batch on ba_id=bar_batchid"
							+ " where bi_piid=?", newpiid, pi_inoutno, pi_class, SystemSession.getUser().getEm_name(), pi_id);

			// 针对只转条码的这种，更新pd_sourcebatch
			baseDao.execute(
					"update prodiodetail A set pd_sourcebatch = (select case when NVL(ba_sourcebatch,' ')<>' ' then ba_sourcebatch else ba_code end bi_sourcebatch from prodiodetail B left join batch on B.pd_batchid=ba_id "
							+ " where A.pd_pdno = B.pd_pdno and A.pd_prodcode = B.pd_prodcode and B.pd_piid = ? ) where A.pd_piid = ? ",
					pi_id, newpiid);
			baseDao.execute(
					"update barcodeio set (bi_pdid,bi_pdno)=(select pd_id,pd_pdno from prodiodetail where bi_sourcebatch=pd_sourcebatch and pd_piid=? ) where bi_piid = ?",
					newpiid, newpiid);
			baseDao.execute(
					"update prodiodetail set pd_barcodeinqty=nvl((select sum(bi_inqty) from barcodeio where bi_piid=? and bi_pdid=pd_id),0) where pd_piid=?",
					newpiid, newpiid);
		} else {
			error = "条码转入单号不存在或该单已存在条码!";
		}
		map.put("newpiid", newpiid);
		map.put("error", error);
		return map;
	}

	@Override
	public String vastCloseToAccountCenter(String caller, String data) throws Exception {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String emUus = CollectionUtil.pluckSqlString(maps, "em_uu");
		String emIds = CollectionUtil.pluckSqlString(maps, "em_id");
		emUus = emUus.replaceAll("'", "");
		Master master = SystemSession.getUser().getCurrentMaster();
		Long en_uu = master.getMa_uu();
		String ma_accesssecret = master.getMa_accesssecret();
		String b2burl = null;
		if (master.getMa_b2bwebsite() == null || "".equals(master.getMa_b2bwebsite())) {
			b2burl = "http://uas.ubtob.com";
		} else {
			b2burl = master.getMa_b2bwebsite();
		}
		if (en_uu != null && en_uu > 0) {
			List<Employee> employeeList = employeeService.getEmployeesByCondition("em_id in (" + emIds + ")");
			try{
				for (Employee employeeNew : employeeList) {
					accountCenterService.unbind(employeeNew, master);
				}
			}catch(Exception e){
				BaseUtil.showError(e.getMessage());
			}
			Response response = HttpUtil.sendDeleteRequest(b2burl + "/erp/account/user/" + emUus + "?access_id=" + en_uu + "", null, true,
					ma_accesssecret);
			if (response.getStatusCode() == 200) {
				baseDao.execute("update employee set em_uu=null,em_imid=null,em_b2benable=0 where em_id in(" + emIds + ")");
			} else {
				BaseUtil.showError("操作失败！");
			}
		} else {
			baseDao.execute("update employee set em_uu=null,em_imid=null,em_b2benable=0 where em_id in(" + emIds + ")");
		}
		return "操作成功！";
	}

	/**
	 * maz 借货出货单批量转借货归还单 2017070873
	 */
	@Override
	public String batchTurnReturn(String caller, String data, String type) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		int piid = 0;
		type = "ProdInOut!OutReturn";
		String piclass = "借货归还单";
		Object y = 0;
		SqlRowList rs = null;
		List<Map<Object, Object>> removemaps = new ArrayList<Map<Object, Object>>();
		for (Map<Object, Object> map : maps) {
			int pdid = Integer.parseInt(map.get("pd_id").toString());
			double tqty = Double.parseDouble(map.get("pd_tqty").toString());
			y = baseDao.getFieldDataByCondition("ProdIODetail", "sum(nvl(pd_inqty,0))", "pd_ioid=" + pdid);
			y = y == null ? 0 : y;
			rs = baseDao.queryForRowSet("SELECT pd_inoutno,pd_pdno,pd_outqty FROM ProdIODetail WHERE pd_id=? and pd_outqty<?", pdid,
					Double.parseDouble(y.toString()) + tqty);
			if (rs.next()) {
				sb = new StringBuffer("[本次数量填写超出可转数量],出库单号:").append(rs.getString("pd_inoutno")).append(",行号:")
						.append(rs.getInt("pd_pdno")).append(",出库数量:").append(rs.getDouble("pd_outqty")).append(",已转数:").append(y)
						.append(",本次数:").append(tqty).append("<hr/>");
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
		maps.removeAll(removemaps);
		if (maps.size() > 0) {
			JSONObject j = null;
			int detno = 1;
			Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(maps, new Object[] { "pi_sellername", "pi_outtype",
					"pi_cardcode", "pi_departmentcode", "pi_emcode", "pi_currency" });
			// 分组的转入操作
			Set<Object> mapSet = groups.keySet();
			List<Map<Object, Object>> items;
			for (Object s : mapSet) {
				items = groups.get(s);
				String pi_inoutno = null;
				for (Map<Object, Object> item : items) {
					if (pi_inoutno == null) {
						Object pi_id = baseDao.getFieldDataByCondition("ProdIODetail", "pd_piid", "pd_id=" + item.get("pd_id"));
						j = prodInOutDao.newProdDefectOut(Integer.parseInt(pi_id.toString()), piclass, type);
						if (j != null) {
							pi_inoutno = j.getString("pi_inoutno");
							piid = j.getInt("pi_id");
							sb.append("转入成功," + piclass + "号:"
									+ "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + piid
									+ "&gridCondition=pd_piidIS" + piid + "&whoami=" + type + "')\">" + pi_inoutno + "</a>&nbsp<hr>");
						}
					}
					if (pi_inoutno != null) {
						int pdid = Integer.parseInt(item.get("pd_id").toString());
						double tqty = Double.parseDouble(item.get("pd_tqty").toString());
						prodInOutDao.toAppointedProdOutReturn(piid, pdid, tqty, detno++);
						// 修改ProdInOutDetail状态
						baseDao.updateByCondition("ProdIODetail", "pd_yqty=nvl(pd_yqty,0)+" + tqty, "pd_id=" + pdid);
						// 记录日志
						Object[] cts = baseDao.getFieldsDataByCondition("ProdIODetail", "pd_piid,pd_pdno", "pd_id=" + pdid);
						baseDao.logger.turnDetail("msg.turnProdIO", caller, "pi_id", cts[0], cts[1]);
					}
				}
			}
			baseDao.execute("update prodinout set (pi_arcode, pi_arname, pi_receivecode, pi_receivename, pi_paymentcode, pi_payment)=(select cu_arcode, cu_arname, cu_shcustcode, cu_shcustname, cu_paymentscode, cu_payments from customer where cu_code=pi_cardcode) where pi_id="
					+ piid);
			baseDao.execute("update ProdIODetail set pd_ordertotal=round(pd_inqty*nvl(pd_sendprice,0),2) where pd_piid=" + piid);
			baseDao.execute(
					"update ProdInOut set pi_total=(SELECT round(sum(nvl(pd_sendprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0))),2) FROM ProdIODetail WHERE pd_piid=pi_id) where pi_id=?",
					piid);
			baseDao.updateByCondition("ProdInOut", "pi_totalupper=L2U(nvl(pi_total,0))", "pi_id=" + piid);
		}
		return sb.toString();
	}

	@Override
	public String batchPLXG(String data, String caller, String type) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sx = new StringBuffer();
		for (Map<Object, Object> map : maps) {
			Object kind = map.get("ppd_type");
			Object[] a = baseDao.getFieldsDataByCondition("PurchasePriceDetail", "ppd_appstatus,ppd_status", "ppd_id=" + map.get("ppd_id"));
			if (kind != null && "PLWD".equals(kind)) {
				if (a[0] != null && "未认定".equals(a[0])) {
					BaseUtil.showError("已是未认定状态,不允许未认定操作");
				}
				baseDao.execute("update PurchasePriceDetail set ppd_appstatus='未认定',ppd_outtype='" + map.get("ppd_outtype")
						+ "' where ppd_id=" + map.get("ppd_id"));
			} else if (kind != null && "PLSX".equals(kind)) {
				if (a[1] != null && "无效".equals(a[1])) {
					BaseUtil.showError("已是无效状态,不允许无效操作");
				}
				if (map.get("ppd_id") != null && !"".equals(map.get("ppd_id"))) {
					sx.append(map.get("ppd_id") + ",");
				}
				baseDao.execute("update PurchasePriceDetail set ppd_unvaliddate=sysdate,ppd_status='无效',ppd_statuscode='UNVALID',ppd_outtype='"
						+ map.get("ppd_outtype") + "' where ppd_id=" + map.get("ppd_id"));
			} else if (kind != null && "PLRD".equals(kind)) {
				if (a[0] != null && "合格".equals(a[0])) {
					BaseUtil.showError("已是合格状态,不允许合格操作");
				}
				baseDao.execute("update PurchasePriceDetail set ppd_appstatus='合格',ppd_intype='" + map.get("ppd_intype")
						+ "' where ppd_id=" + map.get("ppd_id"));
			} else if (kind != null && "PLCX".equals(kind)) {
				if (a[1] != null && "有效".equals(a[1])) {
					BaseUtil.showError("已是有效状态,不允许有效操作");
				}
				baseDao.execute("update PurchasePriceDetail set ppd_status='有效',ppd_statuscode='VALID',ppd_intype='"
						+ map.get("ppd_intype") + "' where ppd_id=" + map.get("ppd_id"));
			} else if ("".equals(kind) || kind == null) {
				BaseUtil.showError("请选择操作类型");
			}
		}
		// 单据编号：2018030428    失效时，邮件通知供应商
		if (baseDao.isDBSetting("unvalidPriceInform")) {
			if (sx.length() > 0) {
				try{
					baseDao.callProcedure("SP_UNVALIDPRICE", new Object[]{sx.substring(0, sx.length() - 1)});
				} catch (Exception e) {
				}
			}
		}
		return "批量操作成功";
	}

	@Override
	public void batchHungCustomer(String caller, String data) {

		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		for (Map<Object, Object> map : maps) {
			Object id = map.get("cu_id");
			boolean bool = baseDao.checkIf("Customer", "cu_auditstatuscode = 'AUDITED'");
			if (bool) {
				// 挂起
				baseDao.updateByCondition("Customer", "cu_statuscode='HUNG',cu_status='挂起'", "cu_id=" + id);
			}
		}
	}

	@Override
	public String batchTurnSale(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		int index = 0;
		if (maps.size() > 0) {
			String log = null;
			Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(maps,
					new Object[] { "qu_custcode", "qu_currency", "qu_kind" });
			Set<Object> mapSet = groups.keySet();
			List<Map<Object, Object>> items;
			for (Object s : mapSet) {
				items = groups.get(s);
				index++;
				// 转入销售订单主记录
				Integer qu_id = Integer.parseInt(items.get(0).get("qu_id").toString());
				Object code = baseDao.getFieldDataByCondition("quotation", "qu_code", "qu_id=" + qu_id);
				code = baseDao.getFieldDataByCondition("sale", "sa_code", "sa_sourcecode='" + code + "'");
				if (code != null && !code.equals("")) {
					log = BaseUtil.getLocalMessage("scm.sale.quotation.haveturn")
							+ "<a href=\"javascript:openUrl('jsps/scm/sale/sale.jsp?whoami=Sale&formCondition=sa_codeIS" + code
							+ "&gridCondition=sd_codeIS" + code + "')\">" + code + "</a>&nbsp;";
				} else {
					Key key = transferRepository.transfer("Quotation", qu_id);
					if (key != null) {
						int sa_id = key.getId();

						// 转入明细
						transferRepository.transfer("Quotation", items, key);

						baseDao.execute("update SaleDetail set sd_total=round(sd_qty*sd_price,2),sd_costprice=round(sd_price/(1+sd_taxrate/100),6),"
								+ "sd_taxtotal=round(sd_qty*sd_price/(1+sd_taxrate/100),2) where sd_said in (" + sa_id + ")");
						baseDao.execute("update Sale set sa_total=(select nvl(sum(nvl(sd_total,0)),0) from saledetail where sd_said=sa_id) where sa_id in ("
								+ sa_id + ")");
						log = "转入成功,销售订单号:" + "<a href=\"javascript:openUrl('jsps/scm/sale/sale.jsp?whoami=Sale&formCondition=sa_idIS"
								+ sa_id + "&gridCondition=sd_saidIS" + sa_id + "')\">" + key.getCode() + "</a>";
					}
				}
				sb.append(index).append(": ").append(log).append("<hr>");
			}
			// 修改报价单状态
			for (Map<Object, Object> map : maps) {
				int sdid = Integer.parseInt(map.get("qd_id").toString());
				quotationDao.checkAdQty(sdid);
			}
		}
		return sb.toString();
	}

	@Override
	public String vastTurnSaleReturn(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		int piid = 0;
		String type = "ProdInOut!SaleReturn";
		String piclass = "销售退货单";
		Object y = 0;
		SqlRowList rs = null;

		for (Map<Object, Object> map : maps) {
			int pdid = Integer.parseInt(map.get("pd_id").toString());
			double tqty = Double.parseDouble(map.get("pd_tqty").toString());
			y = baseDao.getFieldDataByCondition("ProdIODetail", "sum(nvl(pd_inqty,0))", "pd_ioid=" + pdid);
			y = y == null ? 0 : y;
			rs = baseDao.queryForRowSet("SELECT pd_inoutno,pd_pdno,pd_outqty FROM ProdIODetail WHERE pd_id=? and pd_outqty<?", pdid,
					Double.parseDouble(y.toString()) + tqty);
			if (rs.next()) {
				sb = new StringBuffer("[本次数量填写超出可转数量],出货单号:").append(rs.getString("pd_inoutno")).append(",行号:")
						.append(rs.getInt("pd_pdno")).append(",出库数量:").append(rs.getDouble("pd_outqty")).append(",已转数:").append(y)
						.append(",本次数:").append(tqty).append("<hr/>");
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}

		if (maps.size() > 0) {
			JSONObject j = null;
			int detno = 1;
			Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(maps, new Object[] { "pi_cardcode", "pi_currency",
					"pi_payment", "pi_sellername", "pi_arcode", "pi_receivecode" });
			// 分组的转入操作
			Set<Object> mapSet = groups.keySet();
			List<Map<Object, Object>> items;
			for (Object s : mapSet) {
				items = groups.get(s);
				String pi_inoutno = null;
				for (Map<Object, Object> item : items) {
					if (pi_inoutno == null) {
						Object pi_id = baseDao.getFieldDataByCondition("ProdIODetail", "pd_piid", "pd_id=" + item.get("pd_id"));
						j = prodInOutDao.newProdDefectOut(Integer.parseInt(pi_id.toString()), piclass, type);
						if (j != null) {
							pi_inoutno = j.getString("pi_inoutno");
							piid = j.getInt("pi_id");
							sb.append("转入成功," + piclass + "号:"
									+ "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + piid
									+ "&gridCondition=pd_piidIS" + piid + "&whoami=" + type + "')\">" + pi_inoutno + "</a>&nbsp<hr>");
						}
					}
					if (pi_inoutno != null) {
						int pdid = Integer.parseInt(item.get("pd_id").toString());
						double tqty = Double.parseDouble(item.get("pd_tqty").toString());
						prodInOutDao.toAppointedProdSaleReturn(piid, pdid, tqty, detno++);
						// 修改ProdInOutDetail状态
						baseDao.updateByCondition("ProdIODetail", "pd_yqty=nvl(pd_yqty,0)+" + tqty, "pd_id=" + pdid);
						// 记录日志
						Object[] cts = baseDao.getFieldsDataByCondition("ProdIODetail", "pd_piid,pd_pdno", "pd_id=" + pdid);
						baseDao.logger.turnDetail("msg.turnProdIO!SaleReturn", "ProdInOut!Sale", "pi_id", cts[0], cts[1] + ",数量：" + tqty);
					}
				}
			}
		}
		return sb.toString();
	}

	@Override
	public String vastTurnPurcCheckout(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		int piid = 0;
		String type = "ProdInOut!PurcCheckout";
		String piclass = "采购验退单";
		Object y = 0;
		SqlRowList rs = null;

		for (Map<Object, Object> map : maps) {
			int pdid = Integer.parseInt(map.get("pd_id").toString());
			double tqty = Double.parseDouble(map.get("pd_tqty").toString());
			y = baseDao.getFieldDataByCondition("ProdIODetail", "sum(nvl(pd_outqty,0))", "pd_ioid=" + pdid);
			y = y == null ? 0 : y;
			rs = baseDao.queryForRowSet("SELECT pd_inoutno,pd_pdno,pd_inqty FROM ProdIODetail WHERE pd_id=? and pd_inqty<?", pdid,
					Double.parseDouble(y.toString()) + tqty);
			if (rs.next()) {
				sb = new StringBuffer("[本次数量填写超出可转数量],采购验收单号:").append(rs.getString("pd_inoutno")).append(",行号:")
						.append(rs.getInt("pd_pdno")).append(",入库数量:").append(rs.getDouble("pd_inqty")).append(",已转数:").append(y)
						.append(",本次数:").append(tqty).append("<hr/>");
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}

		if (maps.size() > 0) {
			JSONObject j = null;
			int detno = 1;
			Map<Object, List<Map<Object, Object>>> groups = BaseUtil.groupsMap(maps, new Object[] { "pi_cardcode", "pi_currency",
					"pi_payment", "pi_receivecode" });
			// 分组的转入操作
			Set<Object> mapSet = groups.keySet();
			List<Map<Object, Object>> items;
			for (Object s : mapSet) {
				items = groups.get(s);
				String pi_inoutno = null;
				for (Map<Object, Object> item : items) {
					if (pi_inoutno == null) {
						Object pi_id = baseDao.getFieldDataByCondition("ProdIODetail", "pd_piid", "pd_id=" + item.get("pd_id"));
						j = prodInOutDao.newProdDefectOut(Integer.parseInt(pi_id.toString()), piclass, type);
						if (j != null) {
							pi_inoutno = j.getString("pi_inoutno");
							piid = j.getInt("pi_id");
							sb.append("转入成功," + piclass + "号:"
									+ "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + piid
									+ "&gridCondition=pd_piidIS" + piid + "&whoami=" + type + "')\">" + pi_inoutno + "</a>&nbsp<hr>");
						}
					}
					if (pi_inoutno != null) {
						int pdid = Integer.parseInt(item.get("pd_id").toString());
						double tqty = Double.parseDouble(item.get("pd_tqty").toString());
						prodInOutDao.toAppointedProdDefectOut(piid, pdid, tqty, detno++);
						// 修改ProdInOutDetail状态
						baseDao.updateByCondition("ProdIODetail", "pd_yqty=nvl(pd_yqty,0)+" + tqty, "pd_id=" + pdid);
						// 记录日志
						Object[] cts = baseDao.getFieldsDataByCondition("ProdIODetail", "pd_piid,pd_pdno", "pd_id=" + pdid);
						baseDao.logger.turnDetail("转" + piclass, "ProdInOut!PurcCheckin", "pi_id", cts[0], cts[1] + ",数量：" + tqty);
					}
				}
			}
		}
		return sb.toString();
	}

	@Override
	public String batchReplaceRateChange(String data, String caller, String type) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Object code = baseDao.sGetMaxNumber("ReplaceRateChange", 2);
		Employee employee = SystemSession.getUser();
		int id = baseDao.getSeqId("ReplaceRateChange_SEQ");
		int detno = 1;
		baseDao.execute("Insert into ReplaceRateChange (RC_ID,RC_CODE,RC_INDATE,RC_INMAN,RC_STATUS,RC_STATUSCODE,RC_AUDITMAN,RC_AUDITDATE,RC_REMARK,RC_ATTACH,RC_TYPE) values ("
				+ id + ",'" + code + "',sysdate,'" + employee.getEm_name() + "','在录入','ENTERING',null,null,null,null,null)");
		for (Map<Object, Object> map : maps) {
			int rd_id = baseDao.getSeqId("ReplaceRateChangeDetail_SEQ");
			baseDao.execute("Insert into ReplaceRateChangedetail (RD_ID,RD_RCID,RD_DETNO,RD_GROUPCODE,RD_PRODCODE,RD_NEWRATE,RD_OLDRATE,RD_REMARK) values ("
					+ rd_id
					+ ","
					+ id
					+ ","
					+ detno
					+ ",'"
					+ map.get("prr_groupcode")
					+ "','"
					+ map.get("prr_prodcode")
					+ "',null,'"
					+ map.get("prr_rate") + "',null)");
			detno += 1;
		}
		return "批量操作成功,变更单号:" + "<a href=\"javascript:openUrl('jsps/scm/purchase/replaceRateChange.jsp?formCondition=rc_idIS" + id
				+ "&gridCondition=rd_rcidIS" + id + "')\">" + code + "</a>&nbsp;";
	}

	@Override
	public String turnMakeExp(String caller, String data) {
		List<Map<Object, Object>> list = BaseUtil.parseGridStoreToMaps(data);
		String ids = "";
		String log = null;
		StringBuffer sb = new StringBuffer();
		Object vendcode = list.get(0).get("ma_vendcode");// 批处理界面主表委外商编号
		Object vendname = list.get(0).get("ma_vendname");// 批处理界面主表委外商名称
		Object mecode = list.get(0).get("ma_expcode");// 批处理界面指定委外PO号
		boolean bool = false;
		for (Map<Object, Object> map : list) {
			ids = ids + "," + map.get("ma_id");
		}
		if (ids != null) {
			ids = ids.substring(1);
			// String det = baseDao.getFieldDataByCondition(tableName, field,
			// condition)("select wm_concat(ma_code) from make where ma_id in ("+ids+") and nvl(ma_vendcode,' ')=' '");
			String det = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(ma_code) from make where ma_id in (" + ids + ") and nvl(ma_vendcode,' ')=' '", String.class);
			if (det != null && !"".equals(det) && (vendcode == null || "".equals(vendcode))) {
				BaseUtil.showError("委外单中存在委外商为空，主表需要指定委外商号！委外单号：" + det);
			}
			if (vendcode != null && !"".equals(vendcode)) {
				det = baseDao.getJdbcTemplate().queryForObject(
						"select wm_concat(ma_code) from make where ma_id in (" + ids + ") and nvl(ma_vendcode,'" + vendcode + "')<>'"
								+ vendcode + "'", String.class);
				if (det != null && !"".equals(det)) {
					BaseUtil.showError("您指定的供应商与委外单已指定供应商不一致，不能下达");
				}
			}
			if (mecode != null && !"".equals(mecode)) {
				Object me_vendcode = baseDao.getFieldDataByCondition("makeexp", "me_vendcode", "me_code='" + mecode + "'");
				if (me_vendcode != null && !"".equals(me_vendcode.toString())) {
					det = baseDao.getJdbcTemplate().queryForObject(
							"select wm_concat(ma_code) from make where ma_id in (" + ids + ") and nvl(ma_vendcode,'" + me_vendcode
									+ "')<>'" + me_vendcode + "' ", String.class);
					if (det != null && !"".equals(det)) {
						BaseUtil.showError("委外单中存在委外供应商与指定委外PO单委外供应商不同，不能下达，委外单号：" + det);
					}
					if (vendcode != null && !"".equals(vendcode) && !vendcode.equals(me_vendcode)) {
						BaseUtil.showError("指定的委外商号与指定的委外PO单中的委外商号不一致！");
					}
				} else {
					BaseUtil.showError("指定委外PO单委外商为空，不允许下达！");
				}
			}
			String sql = "select * from make where ma_id in (" + ids + ")";
			List<Map<String, Object>> listMake = baseDao.getJdbcTemplate().queryForList(sql);
			if (vendcode != null && !"".equals(vendcode)) {// 指定供应商 则只生成一张委外PO单
				if (mecode != null && !"".equals(mecode)) {// 指定委外PO单
					Object me_id = baseDao.getFieldDataByCondition("makeexp", "me_id", "me_code='" + mecode + "'");
					baseDao.execute("update make set ma_expcode='" + mecode + "' where ma_id in (" + ids + ")");
					/*
					 * baseDao.execute(
					 * "update makeexp set (me_vendcode,me_vendname,me_apvendcode,me_apvendname,me_currency,me_rate,me_paymentscode,me_payments)=(select ma_vendcode,ma_vendname,ma_apvendcode,ma_apvendname,ma_currency,ma_rate,ma_paymentscode,ma_payments from make where ma_expcode=me_code and nvl(ma_apvendcode,' ')<>' ' and rownum=1) where me_code='"
					 * +mecode+"'");
					 */
					log = "下达成功,委外PO号:" + "<a href=\"javascript:openUrl('jsps/scm/purchase/makeexp.jsp?formCondition=me_idIS" + me_id
							+ "&gridCondition=ma_expcodeIS" + mecode + "')\">" + mecode + "</a>&nbsp;";
					sb.append(log).append("<br>");
				} else {
					int me_id = baseDao.getSeqId("MAKEEXP_SEQ");
					String me_code = baseDao.sGetMaxNumber("MakeExp", 2);
					String insertSql = "insert into MakeExp(me_id,me_code,me_date,me_inman,me_status,me_statuscode,me_vendcode,me_vendname,me_apvendcode,me_apvendname,me_currency,me_rate,me_paymentscode,me_payments,me_shipaddresscode,me_remark,me_indate,me_vendremark) select "
							+ me_id
							+ ",'"
							+ me_code
							+ "',sysdate,'"
							+ SystemSession.getUser().getEm_name()
							+ "','在录入','ENTERING',"
							+ "'"
							+ vendcode
							+ "',"
							+ "'"
							+ vendname
							+ "',"
							+ "'"
							+ vendcode
							+ "',"
							+ "'"
							+ vendname
							+ "',"
							+ "ve_currency,"
							+ "cm_crrate,"
							+ "ve_paymentcode,"
							+ "ve_payment,"
							+ "null,null,sysdate,null from vendor left join CURRENCYSMONTH ON CM_CRNAME=VE_CURRENCY where ve_code='"
							+ vendcode + "' and CM_YEARMONTH=TO_CHAR(SYSDATE,'YYYYMM') ";
					baseDao.execute(insertSql);
					baseDao.execute("update make set ma_expcode='" + me_code + "' where ma_id in (" + ids + ")");
					baseDao.execute("update makeexp set (me_apvendcode,me_apvendname,me_currency,me_rate,me_paymentscode,me_payments)=(select ma_apvendcode,ma_apvendname,ma_currency,ma_rate,ma_paymentscode,ma_payments from make where ma_expcode=me_code and nvl(ma_apvendcode,' ')<>' ' and rownum=1) where me_code='"
							+ me_code + "'");
					log = "下达成功,委外PO号:" + "<a href=\"javascript:openUrl('jsps/scm/purchase/makeexp.jsp?formCondition=me_idIS" + me_id
							+ "&gridCondition=ma_expcodeIS" + me_code + "')\">" + me_code + "</a>&nbsp;";
					sb.append(log).append("<br>");
				}

			} else {
				if (mecode != null && !"".equals(mecode)) {// 指定委外PO单
					Object me_id = baseDao.getFieldDataByCondition("makeexp", "me_id", "me_code='" + mecode + "'");
					baseDao.execute("update make set ma_expcode='" + mecode + "' where ma_id in (" + ids + ")");
					baseDao.execute("update makeexp set (me_vendcode,me_vendname,me_apvendcode,me_apvendname,me_currency,me_rate,me_paymentscode,me_payments)=(select ma_vendcode,ma_vendname,ma_apvendcode,ma_apvendname,ma_currency,ma_rate,ma_paymentscode,ma_payments from make where ma_expcode=me_code and nvl(ma_apvendcode,' ')<>' ' and rownum=1) where me_code='"
							+ mecode + "'");
					log = "下达成功,委外PO号:" + "<a href=\"javascript:openUrl('jsps/scm/purchase/makeexp.jsp?formCondition=me_idIS" + me_id
							+ "&gridCondition=ma_expcodeIS" + mecode + "')\">" + mecode + "</a>&nbsp;";
					sb.append(log).append("<br>");
				} else {
					// 根据供应商分组
					Map<Object, List<Map<String, Object>>> group = groupMap(listMake, "ma_vendcode");
					Set<Object> vendcodes = group.keySet();
					for (Object ma_vendcode : vendcodes) {
						int me_id = baseDao.getSeqId("MAKEEXP_SEQ");
						String me_code = baseDao.sGetMaxNumber("MakeExp", 2);
						List<Map<String, Object>> makeInfo = group.get(ma_vendcode);
						String insertSql = "insert into MakeExp(me_id,me_code,me_date,me_inman,me_status,me_statuscode,me_vendcode,me_vendname,me_apvendcode,me_apvendname,me_currency,me_rate,me_paymentscode,me_payments,me_shipaddresscode,me_remark,me_indate,me_vendremark) select "
								+ me_id
								+ ",'"
								+ me_code
								+ "',sysdate,'"
								+ SystemSession.getUser().getEm_name()
								+ "','在录入','ENTERING',"
								+ "ve_code,"
								+ "ve_name,"
								+ "ve_code,"
								+ "ve_name,"
								+ "ve_currency,"
								+ "cm_crrate,"
								+ "ve_paymentcode,"
								+ "ve_payment,"
								+ "null,null,sysdate,null from vendor left join CURRENCYSMONTH ON CM_CRNAME=VE_CURRENCY where ve_code='"
								+ ma_vendcode + "' and CM_YEARMONTH=TO_CHAR(SYSDATE,'YYYYMM')";
						baseDao.execute(insertSql);
						baseDao.execute("update makeexp set (me_apvendcode,me_apvendname,me_currency,me_rate,me_paymentscode,me_payments)=(select ma_apvendcode,ma_apvendname,ma_currency,ma_rate,ma_paymentscode,ma_payments from make where ma_expcode=me_code and nvl(ma_apvendcode,' ')<>' ' and rownum=1) where me_code='"
								+ me_code + "'");
						for (Map<String, Object> map : makeInfo) {
							baseDao.execute("update make set ma_expcode='" + me_code + "' where ma_id=" + map.get("ma_id") + "");
						}
						log = "下达成功,委外PO号:" + "<a href=\"javascript:openUrl('jsps/scm/purchase/makeexp.jsp?formCondition=me_idIS" + me_id
								+ "&gridCondition=ma_expcodeIS" + me_code + "')\">" + me_code + "</a>&nbsp;";
						sb.append(log).append("<br>");
					}
				}
			}
		}
		return sb.toString();
	}

	public Map<Object, List<Map<String, Object>>> groupMap(List<Map<String, Object>> maps, String groupField) {
		Map<Object, List<Map<String, Object>>> set = new HashMap<Object, List<Map<String, Object>>>();
		List<Map<String, Object>> list = null;
		for (Map<String, Object> map : maps) {
			Object key = map.get(groupField);
			if (set.containsKey(key)) {
				list = set.get(key);
			} else {
				list = new ArrayList<Map<String, Object>>();
			}
			list.add(map);
			set.put(key, list);
		}
		return set;
	}

	@Override
	public String turnPaInXY(String caller, String data) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		String code = null;
		JSONObject j = null;
		Map<Object, Object> maps = new HashMap<Object, Object>();
		String inids = "";
		boolean blean = baseDao.isDBSetting(caller, "onlyGenerateInvoice");
		for (Map<Object, Object> map : store) {
			maps.put(map.get("pi_inoutno"), map.get("pi_feeexpensexy_user"));
			SqlRowList rs = baseDao.queryForRowSet(
					"select pd_inoutno,pd_pdno,pd_ordercode,pd_orderdetno from prodiodetail where pd_piid=? and pd_ordercode is not null",
					map.get("pi_id"));
			while (rs.next()) {
				Double a = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select sum(nvl(pd_outqty,0)-nvl(pd_inqty,0)) qty from prodiodetail where pd_piclass in ('出货单','销售退货单') and pd_ordercode=? and pd_orderdetno=?",
								new Object[] { rs.getObject("pd_ordercode"), rs.getObject("pd_orderdetno") }, Double.class);
				Double b = baseDao.getJdbcTemplate().queryForObject("select nvl(sd_qty,0) from saledetail where sd_code=? and sd_detno=?",
						new Object[] { rs.getObject("pd_ordercode"), rs.getObject("pd_orderdetno") }, Double.class);
				if (a > b) {
					sb.append("出货单:").append(rs.getObject("pd_inoutno"));
					sb.append("序号:").append(rs.getObject("pd_pdno"));
					sb.append("总出货数:").append(a);
					sb.append("订单数:").append(b).append("<br>");
				}
			}
		}
		if (sb.length() > 0)
			BaseUtil.showError("总出货数超过订单数，不允许转发票箱单！<br>" + sb.toString());
		// 按客户,币别，抬头单位,单据类型分组
		Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupsMap(store, new Object[] { "pi_cardcode", "pi_currency", "pi_custcode2",
				"pi_class" });
		List<Map<Object, Object>> s = null;
		Object pi_id = "";// 来源ID
		for (Object m : map.keySet()) {
			if (m != null) {
				s = map.get(m);
				pi_id = s.get(0).get("pi_id");
				Object[] piid = CollectionUtil.pluck(s, "pi_id");
				String piids = BaseUtil.parseArray2Str(piid, ",");
				String inoutno = baseDao.getJdbcTemplate().queryForObject(
						"select wmsys.wm_concat(pi_inoutno) from prodinout where pi_id in (" + piids + ")", String.class);
				SqlRowList rs = baseDao
						.queryForRowSet("select count(1) from (select distinct pi_sellercode from prodinout where pi_id in (" + piids
								+ "))");
				if (rs.next() && rs.getInt(1) > 1) {
					BaseUtil.showError("选定出入库单的业务员不同,不能合并生成发票箱单!");
				}
				/*
				 * rs = baseDao .queryForRowSet(
				 * "select count(1) from (select distinct pd_whcode from prodiodetail where pd_piid in ("
				 * + piids + "))"); if (rs.next() && rs.getInt(1) > 1) {
				 * BaseUtil.showError("选定出入库单的仓库不同,不能合并生成发票箱单!"); }
				 */
				// 如果指定了发票号
				if (s.get(0).get("in_code") != null && !"".equals(s.get(0).get("in_code").toString())) {
					code = s.get(0).get("in_code").toString();
					// 当前编号的记录已经存在,不能新增!
					boolean bool = baseDao.checkByCondition("Invoice", "in_code='" + code + "'");
					if (!bool) {
						BaseUtil.showError("指定单号已经存在,请重新指定单号!");
					}
					j = invoiceDao.newPaInwithno(pi_id, inoutno, code, caller);
					if (j != null) {
						inids = inids + "," + j.get("in_id");
						baseDao.execute("update prodinout set pi_packingcode='" + code + "', pi_invoicecode='" + code
								+ "' where pi_id in (" + piids + ")");
						invoiceDao.detailTurnPaInDetail(code, piids, j.get("in_id"), j.get("pi_id"));
						baseDao.execute("update Invoice set in_total=(select sum(id_total) from InvoiceDetail where id_inid="
								+ j.get("in_id") + ") where in_id=" + j.get("in_id"));
						baseDao.execute("update Invoice set in_totalupper=L2U(in_total),in_totalupperenhkd=L2U(in_total/(case when nvl(in_rate,0)=0 then 1 else in_rate end)) where in_id="
								+ j.get("in_id"));
						baseDao.execute("update Packing set pi_total=(select sum(pd_total) from PackingDetail where pd_piid="
								+ j.get("pi_id") + ") where pi_id=" + j.get("pi_id"));
						baseDao.execute("update Packing set pi_totalupper=L2U(pi_total),pi_totalupperenhkd=L2U(pi_total/(case when nvl(pi_rate,0)=0 then 1 else pi_rate end)) where pi_id="
								+ j.get("pi_id"));
						int count = baseDao.getCountByCondition("ProdIODetail", "pd_piid in (" + piids + ")");
						int yCount = baseDao.getCountByCondition("ProdIODetail", "pd_piid in (" + piids
								+ ") and nvl(pd_batchcode,' ')<>' '");
						int xCount = baseDao
								.getCountByCondition("ProdIODetail", "pd_piid in (" + piids + ") and nvl(pd_batchcode,' ')=' '");
						String status = "部分有货";
						if (yCount == count) {
							status = "齐货";
						}
						if (xCount == count) {
							status = "无货";
						}
						baseDao.updateByCondition("Invoice", "in_stockstatus='" + status + "'", "in_id=" + j.get("in_id"));
						baseDao.updateByCondition("Packing", "pi_stockstatus='" + status + "'", "pi_id=" + j.get("pi_id"));
						if (blean) {
							baseDao.deleteByCondition("PackingDetail", "pd_piid=" + j.get("pi_id") + "");
							baseDao.deleteByCondition("Packing", "pi_id=" + j.get("pi_id") + "");
						} else {
							sb.append("转入成功,装箱单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/packing.jsp?formCondition=pi_idIS"
									+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=Packing')\">" + code
									+ "</a>&nbsp;<hr>");
						}
						sb.append("转入成功,发票单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/invoice.jsp?formCondition=in_idIS"
								+ j.get("in_id") + "&gridCondition=id_inidIS" + j.get("in_id") + "&whoami=Invoice')\">" + code
								+ "</a>&nbsp;<hr>");
						Set<Object> set = maps.keySet();
						for (Object obj : set) {
							String sql = "update InvoiceDetail set ID_XYFY_USER='"
									+ maps.get(obj)
									+ "' where id_inid="
									+ j.get("in_id")
									+ " and (id_ordercode,id_orderdetno) in (select pd_ordercode,pd_orderdetno from PRODIODETAIL where pd_inoutno='"
									+ obj + "')";
							baseDao.execute(sql);
						}
					}
				} else {
					j = invoiceDao.newPaIn(pi_id, inoutno, caller);
					if (j != null) {
						inids = inids + "," + j.get("in_id");
						code = j.getString("code");
						baseDao.execute("update prodinout set pi_packingcode='" + code + "', pi_invoicecode='" + code
								+ "' where pi_id in (" + piids + ")");
						invoiceDao.detailTurnPaInDetail(code, piids, j.get("in_id"), j.get("pi_id"));
						baseDao.execute("update Invoice set in_total=(select sum(id_total) from InvoiceDetail where id_inid="
								+ j.get("in_id") + ") where in_id=" + j.get("in_id"));
						baseDao.execute("update Invoice set in_totalupper=L2U(in_total),in_totalupperenhkd=L2U(in_total/nvl(in_rate,1)) where in_id="
								+ j.get("in_id"));
						baseDao.execute("update Packing set pi_total=(select sum(pd_total) from PackingDetail where pd_piid="
								+ j.get("pi_id") + ") where pi_id=" + j.get("pi_id"));
						baseDao.execute("update Packing set pi_totalupper=L2U(pi_total),pi_totalupperenhkd=L2U(pi_total/nvl(pi_rate,1)) where pi_id="
								+ j.get("pi_id"));
						int count = baseDao.getCountByCondition("ProdIODetail", "pd_piid in (" + piids + ")");
						int yCount = baseDao.getCountByCondition("ProdIODetail", "pd_piid in (" + piids
								+ ") and nvl(pd_batchcode,' ')<>' '");
						int xCount = baseDao
								.getCountByCondition("ProdIODetail", "pd_piid in (" + piids + ") and nvl(pd_batchcode,' ')=' '");
						String status = "部分有货";
						if (yCount == count) {
							status = "齐货";
						}
						if (xCount == count) {
							status = "无货";
						}
						baseDao.updateByCondition("Invoice", "in_stockstatus='" + status + "'", "in_id=" + j.get("in_id"));
						baseDao.updateByCondition("Packing", "pi_stockstatus='" + status + "'", "pi_id=" + j.get("pi_id"));
						// baseDao.updateByCondition("InvoiceDetail",
						// "ID_XYFY_USER", "id_inid="+j.get("in_id"));
						if (blean) {
							baseDao.deleteByCondition("PackingDetail", "pd_piid=" + j.get("pi_id") + "");
							baseDao.deleteByCondition("Packing", "pi_id=" + j.get("pi_id") + "");
						} else {
							sb.append("转入成功,装箱单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/packing.jsp?formCondition=pi_idIS"
									+ j.get("pi_id") + "&gridCondition=pd_piidIS" + j.get("pi_id") + "&whoami=Packing')\">" + code
									+ "</a>&nbsp;<hr>");
						}
						sb.append("转入成功,发票单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/invoice.jsp?formCondition=in_idIS"
								+ j.get("in_id") + "&gridCondition=id_inidIS" + j.get("in_id") + "&whoami=Invoice')\">" + code
								+ "</a>&nbsp;<hr>");
						Set<Object> set = maps.keySet();
						for (Object obj : set) {
							String sql = "update InvoiceDetail set ID_XYFY_USER='"
									+ maps.get(obj)
									+ "' where id_inid="
									+ j.get("in_id")
									+ " and (id_ordercode,id_orderdetno) in (select pd_ordercode,pd_orderdetno from PRODIODETAIL where pd_inoutno='"
									+ obj + "')";
							baseDao.execute(sql);
						}
					}
				}
			}
		}
		if (inids.length() > 0) {
			inids = inids.substring(1);
			baseDao.callProcedure("USER_SP_COUNTPRICE", new Object[] { inids });
		}
		return sb.toString();
	}

	public void autoAudit(int sn_id, String code) {
		try {
			boolean bool = baseDao.isDBSetting("Sale!ToAccept!Deal", "autoAudit");
			if (bool) {
				sendNotifyService.submitSendNotify(sn_id, "SendNotify");
				sendNotifyService.auditSendNotify(sn_id, "SendNotify");
			}
		} catch (Exception e) {
			BaseUtil.appendError("出货通知单：" + "<a href=\"javascript:openUrl('jsps/scm/sale/sendNotify.jsp?formCondition=sn_idIS" + sn_id
					+ "&gridCondition=snd_snidIS" + sn_id + "')\">" + code + "</a>&nbsp;" + "自动审核失败,失败原因:" + e.getMessage() + " ");
		}
	}

	@Override
	public String vastEmpowerProdSaler(String caller, String data, String ps_emcode) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		Employee employee = SystemSession.getUser();
		Object[] powerMan = baseDao.getFieldsDataByCondition("employee", "em_uu,em_name", "em_code = '" + ps_emcode + "'");
		if (powerMan != null) {
			String em_uu = String.valueOf(powerMan[0]);
			if (!(em_uu.equals("null") || em_uu.equals("0") || em_uu.equals(""))) {
				for (Map<Object, Object> map : store) {
					try {
						Object pr_code = map.get("pr_code");
						String condition = String.valueOf(" ps_emcode='" + ps_emcode + "' and ps_prcode = '" + pr_code + "'");
						String psId = String.valueOf(baseDao.getFieldDataByCondition("productsaler", "ps_id", condition));
						if (!psId.equals("null")) {
							baseDao.updateByCondition("productsaler",
									" ps_emstatus = '是',ps_uploadstatus=' ',ps_updata = sysdate,ps_recordcode = '" + employee.getEm_code()
											+ "'", " nvl(ps_emstatus,' ') = '否' and ps_id = " + psId);
						} else {
							int ps_id = baseDao.getSeqId("productsaler_seq");
							baseDao.execute("insert into productsaler (ps_id,ps_prcode,ps_prbrand,ps_emcode,ps_emname,PS_EMSTATUS,ps_data,ps_updata,ps_recordcode)"
									+ " select "
									+ ps_id
									+ ",'"
									+ pr_code
									+ "','"
									+ map.get("pr_brand")
									+ "','"
									+ ps_emcode
									+ "','"
									+ powerMan[1]
									+ "','是'"
									+ ",sysdate,sysdate,'"
									+ employee.getEm_code()
									+ "' from employee where em_code = '" + ps_emcode + "'");
						}
					} catch (Exception e) {
						BaseUtil.showError(e.getMessage());
					}
				}
				sb.append("上传成功！");
			} else {
				BaseUtil.showError("您当前尚未注册优软云，请注册后尝试上传。");
			}
		} else {
			BaseUtil.showError("当前操作业务员账号不存在，请重新选择。");
		}
		return sb.toString();
	}

	@Override
	public String vastUnPowerProdSaler(String caller, String data) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb = new StringBuffer();
		Employee employee = SystemSession.getUser();
		for (Map<Object, Object> map : store) {
			try {
				String condition = String.valueOf(" ps_emcode='" + String.valueOf(map.get("ps_emcode")) + "' and ps_prcode = '"
						+ String.valueOf(map.get("pr_code")) + "'");
				String psId = String.valueOf(baseDao.getFieldDataByCondition("productsaler", "ps_id", condition
						+ " and nvl(PS_EMSTATUS,' ') = '是'"));
				if (!(psId.equals("null"))) {
					baseDao.updateByCondition("productsaler",
							" ps_emstatus = '否',ps_uploadstatus=' ', ps_updata = sysdate,ps_recordcode = '" + employee.getEm_code() + "'",
							"nvl(ps_emstatus,' ') = '是' and ps_id = " + psId);
				}
			} catch (Exception e) {
				BaseUtil.showError(e.getMessage());
			}
		}
		sb.append("下架成功！");
		return sb.toString();
	}

	/**
	 * maz 锤子科技按开票数量拆分验退单
	 * 
	 * @param pi_id
	 */
	public String Joint(int pi_id, int pd_id, double tqty, int detno, String newpi_inoutno) {
		Object[] pd = baseDao.getFieldsDataByCondition("ProdIODetail", "pd_inqty,pd_abdyqty,pd_yqty", "pd_id=" + pd_id);
		Double rqty = (pd[0] == null ? 0 : Double.parseDouble(pd[0].toString()))
				- (pd[1] == null ? 0 : Double.parseDouble(pd[1].toString()));
		Double yqty = pd[2] == null ? 0 : Double.parseDouble(pd[2].toString());
		baseDao.execute("update prodinout set pi_isjoint='否' where pi_id=" + pi_id);
		if (rqty >= (tqty + yqty)) {
			baseDao.execute("update prodinout set pi_isjoint = '是' where pi_id=" + pi_id);
		} else if (rqty > yqty) {
			if (newpi_inoutno == null) {
				Map<String, Object> diffence = new HashMap<String, Object>();
				String pi_inoutno = baseDao.sGetMaxNumber("ProdInOut!PurcCheckout", 2);
				diffence.put("pi_inoutno", "'" + pi_inoutno + "'");
				diffence.put("pi_id", baseDao.getSeqId("PRODINOUT_SEQ"));
				diffence.put("pi_isjoint", "'是'");
				diffence.put("pi_abinvostatus", "null");
				baseDao.copyRecord("ProdInOut", "ProdInOut", "pi_id=" + pi_id, diffence);
				newpi_inoutno = pi_inoutno;
			}
			Object newpi_id = baseDao.getFieldDataByCondition("ProdInOut", "pi_id", "pi_inoutno='" + newpi_inoutno
					+ "' and pi_class='采购验退单'");
			Object oldpd_id = baseDao.getFieldDataByCondition("ProdIODetail", "pd_id", "pd_piid=" + pi_id + " and pd_pdno=" + detno);
			Map<String, Object> diffence1 = new HashMap<String, Object>();
			Double outqty = rqty - yqty;
			Object newpd_id = baseDao.getSeqId("PRODIODETAIL_SEQ");
			diffence1.put("pd_id", newpd_id);
			diffence1.put("pd_outqty", outqty);
			diffence1.put("pd_inoutno", "'" + newpi_inoutno + "'");
			diffence1.put("pd_piid", newpi_id);
			baseDao.copyRecord("ProdIODetail", "ProdIODetail", "pd_id=" + oldpd_id, diffence1);
			Double qty = tqty - outqty;
			baseDao.execute("update prodiodetail set pd_outqty = " + qty + " where pd_id=" + oldpd_id);
			baseDao.execute("update prodiodetail set pd_ordertotal=pd_orderprice*pd_outqty,pd_total=pd_price*pd_outqty where pd_id in ("
					+ oldpd_id + "," + newpd_id + ")");
			baseDao.execute("update prodiodetail set pd_taxtotal=pd_ordertotal*pd_taxrate/100 where pd_id in (" + oldpd_id + "," + newpd_id
					+ ")");
		}
		return newpi_inoutno;
	}
	
	public String ProdJoinPLZC(String caller,String data){
		Employee employee = SystemSession.getUser();
		String result = baseDao.callProcedure("SP_VASTTURN", new Object[] { data,employee.getEm_name()});
		if (result != null && !"".equals(result)) {
			BaseUtil.showError(result);
		}
		return "批量操作成功";
	}

	@Override
	public String inviteVendors(String caller, String data) {
		String res ="";
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		List<InvitationRecord> invitationList = new ArrayList<InvitationRecord>();
		if(!CollectionUtil.isEmpty(store)){
			Employee employee  = SystemSession.getUser();
			Master master = employee.getCurrentMaster();
			if(!StringUtil.hasText(employee.getEm_uu())){
				BaseUtil.showError("您还不是优软云的个人用户，请联系管理员开通！");
			}
			if(!master.b2bEnable()){
				BaseUtil.showError("您的企业还未注册优软云，请到企业信息中注册企业优软云！");
			}
			for (Map<Object, Object> map : store) {
				InvitationRecord invita = new InvitationRecord();
				invita.setVendname(String.valueOf(map.get("ve_name")));//被邀请的企业名称
				invita.setVendusertel(String.valueOf(map.get("ve_tel")));//被邀请的用户联系方式
				invita.setVendusername(String.valueOf(map.get("ve_contact")));//被邀请的用户姓名
				invita.setVenduseremail(String.valueOf(map.get("ve_email")));//被邀请的用户邮箱
				invitationList.add(invita);
			}
			String b2burl =master.getMa_b2bwebsite();
			if(!StringUtil.hasText(b2burl)){
				b2burl =  "http://uas.ubtob.com";
			}
			b2burl+="/public/invitation/invite/batch";
			try {
				JSONObject params = new JSONObject();
				params.put("invitations", com.alibaba.fastjson.JSONArray.toJSONString(invitationList));
				params.put("userUU", employee.getEm_uu());
				params.put("enUU", master.getMa_uu());
				params.put("source","UAS");
				com.uas.b2b.core.PSHttpUtils.Response response = PSHttpUtils.sendPostRequest(b2burl, params);
				if(response.getStatusCode()==HttpStatus.OK.value()){
					String resData = response.getResponseText();
					if(StringUtil.hasText(resData)){
						System.out.println("resData: "+resData);
						res = resData;
					}
				}else{
					if(StringUtil.hasText(response.getResponseText())){
						BaseUtil.showError(response.getResponseText());
					}else{
						BaseUtil.showError("程序错误。错误码："+response.getStatusCode());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError(e.getMessage());
			}
		}
		return res;
	}
}
