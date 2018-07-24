package com.uas.erp.service.fa.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ARCheckDao;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.ARBillService;

@Service("arBillService")
public class ARBillServiceImpl implements ARBillService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private VoucherDao voucherDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private ARCheckDao arCheckDao;

	@Override
	public void saveARBill(String caller, String formStore, String gridStore, String assStore, String assMainStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> ass = BaseUtil.parseGridStoreToMaps(assStore);
		handlerService.beforeSave(caller, new Object[] { store, grid, ass });
		// 删除不是主表字段
		if (store.containsKey("cu_name")) {
			store.remove("cu_name");
		}
		if (store.containsKey("ca_asstype")) {
			store.remove("ca_asstype");
		}
		if (store.containsKey("ca_assname")) {
			store.remove("ca_assname");
		}
		int yearmonth = voucherDao.getPeriodsFromDate("Month-C", store.get("ab_date").toString());
		store.put("ab_yearmonth", yearmonth);
		// 主表form中添加的默认信息
		store.put("ab_statuscode", "UNPOST");
		store.put("ab_status", BaseUtil.getLocalMessage("UNPOST"));
		store.put("ab_printstatuscode", "UNPRINT");
		store.put("ab_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
		store.put("ab_paystatuscode", "UNCOLLECT");
		store.put("ab_paystatus", BaseUtil.getLocalMessage("UNCOLLECT"));
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "ARBill"));
		Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(ass, "dass_condid");
		int id;
		for (Map<Object, Object> map : grid) {
			if (map.containsKey("ca_asstype")) {
				map.remove("ca_asstype");
			}
			if (map.containsKey("ca_assname")) {
				map.remove("ca_assname");
			}
			if (map.containsKey("pr_detail")) {
				map.remove("pr_detail");
			}
			if (map.containsKey("pd_invototal")) {
				map.remove("pd_invototal");
			}
			if (map.containsKey("abd_totalbillprice")) {
				map.remove("abd_totalbillprice");
			}
			if (map.containsKey("gsd_amount")) {
				map.remove("gsd_amount");
			}
			if (map.containsKey("gsd_invototal")) {
				map.remove("gsd_invototal");
			}

			id = baseDao.getSeqId("ARBILLDETAIL_SEQ");
			ass = list.get(String.valueOf(map.get("abd_id")));
			if (ass != null) {
				for (Map<Object, Object> m : ass) {// PreRecDetailAss
					m.put("dass_condid", id);
					m.put("dass_id", baseDao.getSeqId("ARBILLDETAILASS_SEQ"));
				}
				baseDao.execute(SqlUtil.getInsertSqlbyGridStore(ass, "ARBillDetailAss"));
			}
			map.put("abd_id", id);
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(grid, "ARBillDetail"));
		Object ab_id = store.get("ab_id");
		getTotal(ab_id, caller);
		// 根据收款方式计算应收日期
		Object cu_duedays = baseDao.getFieldDataByCondition("customer", "nvl(cu_duedays,0)", "cu_code='" + store.get("ab_custcode") + "'");
		String res = baseDao.callProcedure("SP_GETPAYDATE_CUST",
				new Object[] { Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), store.get("ab_paymentcode"), cu_duedays,
						store.get("ab_custcode") });
		baseDao.updateByCondition("arbill", "ab_paydate='" + res + "'", " ab_id=" + ab_id);
		// 记录操作
		baseDao.logger.save(caller, "ab_id", ab_id);
		handlerService.afterSave(caller, new Object[] { store, grid, ass });
	}

	void getTotal(Object id, String caller) {
		baseDao.execute("update ARBill set ab_rate=(select cm_crrate from currencysmonth where cm_yearmonth=to_char(ab_date,'yyyymm') and ab_currency=cm_crname) where ab_id="
				+ id);
		baseDao.execute("update arbilldetail set abd_code=(select ab_code from arbill where abd_abid=ab_id) where abd_abid=" + id
				+ " and not exists (select 1 from arbill where abd_code=ab_code)");
		baseDao.execute("update arbill set ab_sellercode=(select max(em_code) from employee where em_name=ab_seller) where ab_id=" + id
				+ " and nvl(ab_sellercode,' ')=' ' and nvl(ab_seller,' ')<>' '");
		if ("ARBill!OTRS".equals(caller)) {
			baseDao.execute("update arbilldetail set abd_aramount=ROUND(abd_price*abd_qty,2) WHERE abd_abid=" + id);
			baseDao.execute("update arbilldetail set abd_noaramount=ROUND(abd_price*abd_qty/(1+abd_taxrate/100),2) WHERE abd_abid=" + id);
			baseDao.execute("update arbill set ab_taxamount=(select sum(round(((abd_price*abd_qty*abd_taxrate/100)/(1+abd_taxrate/100)),2)) from arbilldetail where abd_abid="
					+ id + ")+ab_differ where ab_id=" + id);
		} else {
			baseDao.execute("update arbilldetail set abd_aramount=ROUND(abd_thisvoprice*abd_qty,2), abd_amount=round(nvl(abd_price,0)*nvl(abd_thisvoqty,0),2) WHERE abd_abid="
					+ id);
			baseDao.execute("update arbilldetail set abd_noaramount=ROUND(abd_thisvoprice*abd_qty/(1+abd_taxrate/100),2) WHERE abd_abid="
					+ id);
			baseDao.execute("update arbill set ab_taxamount=(select sum(round(((abd_thisvoprice*abd_qty*abd_taxrate/100)/(1+abd_taxrate/100)),2)) from arbilldetail where abd_abid="
					+ id + ")+ab_differ where ab_id=" + id);
		}
		baseDao.execute("update arbilldetail set abd_taxamount=NVL(abd_aramount,0)-NVL(abd_noaramount,0) WHERE abd_abid=" + id);
		// 更新ARBill主表的金额
		baseDao.execute("update arbill set ab_aramount=round((select sum(abd_aramount) from arbilldetail where abd_abid=" + id
				+ "),2) where ab_id=" + id);

	}

	void checkVoucher(Object id) {
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(ab_costvouchercode) from ARBill where ab_id=? and nvl(ab_costvouchercode,' ') <>' '", String.class, id);
		if (dets != null) {
			BaseUtil.showError("已有成本凭证，不允许进行当前操作!凭证编号：" + dets);
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(ab_vouchercode) from ARBill where ab_id=? and ab_vouchercode is not null and ab_vouchercode<>'UNNEED'",
				String.class, id);
		if (dets != null) {
			BaseUtil.showError("已有凭证，不允许进行当前操作!凭证编号：" + dets);
		}
	}

	private void checkAss(int id) {
		baseDao.execute(
				"delete from ARBillDetailAss where DASS_ID in (select DASS_ID from arbilldetail left join ARBillDetailAss on DASS_CONDID=abd_id left join category on ca_code=abd_catecode where abd_abid=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,DASS_ASSNAME)=0)",
				id);
		baseDao.execute(
				"delete from ARBillDetailAss where DASS_CONDID in (select abd_id from arbill left join arbilldetail on abd_abid=ab_id left join category on ca_code=abd_catecode where ab_id=? and nvl(ca_asstype,' ')=' ')",
				id);
		// 辅助核算不完善
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(abd_detno) from arbilldetail left join ARBillDetailAss on DASS_CONDID=abd_id left join category on ca_code=abd_catecode where abd_abid=? and nvl(ca_assname,' ')<>' ' and (nvl(DASS_ASSTYPE,' ')=' ' or nvl(DASS_ASSNAME,' ')=' ' or nvl(DASS_CODEFIELD,' ')=' ') order by abd_detno",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("明细行辅助核算不完善，不允许进行当前操作!行号：" + dets);
		}
		// 核算项重复
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(abd_detno) from (select count(1) c,abd_detno,DASS_ASSTYPE from arbilldetail left join ARBillDetailAss on DASS_CONDID=abd_id where abd_abid=? and nvl(DASS_ASSTYPE,' ')<>' ' group by abd_detno,DASS_ASSTYPE) where c>1 order by abd_detno",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("明细行辅助核算核算项重复，不允许进行当前操作!行号：" + dets);
		}
		// 核算项错误
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(abd_detno) from arbilldetail left join ARBillDetailAss on DASS_CONDID=abd_id left join category on ca_code=abd_catecode where abd_abid=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,DASS_ASSNAME)=0 order by abd_detno",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("明细行核算项错误，不允许进行当前操作!行号：" + dets);
		}
		// 核算项不存在
		String str = "";
		StringBuffer error = new StringBuffer();
		SqlRowList rs1 = baseDao
				.queryForRowSet(
						"select 'select '||abd_detno||',count(1) from '||ak_table||' where '||ak_asscode||'='''||DASS_CODEFIELD||''' and '||AK_ASSNAME||'='''||DASS_NAMEFIELD||'''' from ARBillDetailAss left join asskind on DASS_ASSNAME=ak_name left join arbilldetail on DASS_CONDID=abd_id where abd_abid=? order by abd_detno",
						id);
		while (rs1.next()) {
			SqlRowList rd = baseDao.queryForRowSet(rs1.getString(1));
			if (rd.next() && rd.getInt(2) == 0) {
				if (StringUtil.hasText(str))
					str = str + ",";
				str += rd.getInt(1);
			}
		}
		if (str.length() > 0) {
			error.append("核算编号+核算名称不存在,行:").append(str).append(";");
			BaseUtil.showError(error.toString());
		}
	}

	@Override
	public void updateARBillById(String caller, String formStore, String gridStore, String assStore, String assMainStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		List<Map<Object, Object>> ass = BaseUtil.parseGridStoreToMaps(assStore);
		// 只能修改[在录入]的资料!
		handlerService.beforeUpdate(caller, new Object[] { store, gstore, ass });
		Object status[] = baseDao.getFieldsDataByCondition("ARBill", new String[] { "ab_auditstatuscode", "ab_statuscode" }, "ab_id="
				+ store.get("ab_id"));
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyUnPost"));
		}
		Object ab_id = store.get("ab_id");
		checkVoucher(ab_id);
		checkOnUpdate(store, gstore);
		int nowym = voucherDao.getNowPddetno("Month-C");
		int yearmonth = voucherDao.getPeriodsFromDate("Month-C", store.get("ab_date").toString());
		if (yearmonth < nowym) {
			BaseUtil.showError("发票当前账期已结账，不能更新");
		}
		if (!"ARBill!OTRS".equals(caller)) {
			List<Object[]> objs = baseDao.getFieldsDatasByCondition("Arbill left join arbilldetail on ab_id = abd_abid", new String[] {
					"abd_pdid", "abd_sourcekind", "abd_sourcedetailid", "abd_currency", "ab_custcode" }, "abd_abid=" + store.get("ab_id"));
			for (Object os[] : objs) {
				if (os[1] != null && os[1].equals("PRODIODETAIL")) {
					Object picurrency = baseDao.getFieldDataByCondition("prodiodetail left join prodinout  on pi_id=pd_piid",
							"pi_currency", "pd_id=" + os[0]);
					Object pi_arcode = baseDao.getFieldDataByCondition("prodiodetail left join prodinout  on pi_id=pd_piid", "pi_arcode",
							"pd_id=" + os[0]);
					if (picurrency.equals(store.get("ab_currency"))) {

					} else {
						BaseUtil.showError("出入库单关联客户与发票主表币别不一致，不能更新");
					}
					if (pi_arcode.equals(store.get("ab_custcode"))) {

					} else {
						BaseUtil.showError("出入库单关联币别与发票主表客户不一致，不能更新");
					}
				} else if (os[1] != null && os[1].equals("GOODSSEND")) {
					Object gscurrency = baseDao.getFieldDataByCondition("GOODSSENDdetail left join GOODSSEND  on gs_id=gsd_gsid",
							"gs_currency", "gsd_id=" + os[2]);
					Object gscustcode = baseDao.getFieldDataByCondition("GOODSSENDdetail left join GOODSSEND  on gs_id=gsd_gsid",
							"gs_custcode", "gsd_id=" + os[2]);
					if (gscurrency.equals(store.get("ab_currency"))) {

					} else {
						BaseUtil.showError("发出商品关联客户与发票主表币别不一致，不能更新");
					}
					if (gscustcode.equals(store.get("ab_custcode"))) {

					} else {
						BaseUtil.showError("发出商品关联币别与发票主表客户不一致，不能更新");
					}
				}
			}
		}
		store.put("ab_yearmonth", yearmonth);
		if (store.containsKey("cu_name")) {
			store.remove("cu_name");
		}
		if (store.containsKey("ca_asstype")) {
			store.remove("ca_asstype");
		}
		if (store.containsKey("ca_assname")) {
			store.remove("ca_assname");
		}
		for (Map<Object, Object> g : gstore) {
			if (!"ARBill!OTRS".equals(caller)) {
				if ("PRODIODETAIL".equals(g.get("abd_sourcekind"))) {
					Object pidate = baseDao.getFieldDataByCondition("prodiodetail left join prodinout on pi_id=pd_piid", "pi_date",
							"pd_id=" + g.get("abd_pdid"));
					int yearmonthdet = voucherDao.getPeriodsFromDate("Month-C", (pidate.toString()).substring(0, 10));
					if (yearmonthdet != yearmonth) {
						BaseUtil.showError("出入库单日期期间" + yearmonthdet + "不等于当前期间：" + yearmonth + "<br>请修改日期，或反结转应收帐.");
					}
				} else if ("GOODSSEND".equals(g.get("abd_sourcekind"))) {
					Object gsdate = baseDao.getFieldDataByCondition("GOODSSENDdetail left join GOODSSEND on gs_id=gsd_gsid", "gs_date",
							"gsd_id=" + g.get("abd_sourcedetailid"));
					int yearmonthdet = voucherDao.getPeriodsFromDate("Month-C", (gsdate.toString()).substring(0, 10));
					if (yearmonthdet >= yearmonth) {
						BaseUtil.showError("发出商品日期期间" + yearmonthdet + "大于等于当前期间：" + yearmonth + "<br>请修改日期，或反结转应收帐.");
					}
				}
			}
			if (g.containsKey("ca_asstype")) {
				g.remove("ca_asstype");
			}
			if (g.containsKey("ca_assname")) {
				g.remove("ca_assname");
			}
			if (g.containsKey("pr_detail")) {
				g.remove("pr_detail");
			}
			if (g.containsKey("pd_invototal")) {
				g.remove("pd_invototal");
			}
			if (g.containsKey("abd_totalbillprice")) {
				g.remove("abd_totalbillprice");
			}
			if (g.containsKey("gsd_amount")) {
				g.remove("gsd_amount");
			}
			if (g.containsKey("gsd_invototal")) {
				g.remove("gsd_invototal");
			}
		}
		if ("ARBill!IRMA".equals(caller)) {
			for (Map<Object, Object> map : gstore) {
				String sourcekind = map.get("abd_sourcekind").toString();
				int gsd_id = Integer.parseInt(map.get("abd_sourcedetailid").toString());
				int abd_id = Integer.parseInt(map.get("abd_id").toString());
				String selectqty = "select abd_qty,abd_aramount,abd_adid from arbilldetail where abd_id='" + abd_id + "'";
				SqlRowList rs = baseDao.queryForRowSet(selectqty);
				// 数据库里保存的本次开票数量
				double abd_qty_old = 0;
				int adid = 0;
				// 数据库里保存的本次开票价税金额
				if (rs.next()) {
					abd_qty_old = rs.getDouble(1);
					adid = rs.getGeneralInt("abd_adid");
				}
				int pd_id = Integer.parseInt(map.get("abd_pdid").toString());
				// 修改后的本次开票数量
				double abd_qty = Double.parseDouble(map.get("abd_qty").toString());
				// 发货数量
				double source_qty = Double.parseDouble(map.get("abd_thisvoqty").toString()); // 发货数量
				if (adid == 0) {
					String selectinvoqty = "";
					if (sourcekind.trim().equals("GOODSSEND")) {
						selectinvoqty = "select gsd_showinvoqty from goodssenddetail where gsd_id= '" + gsd_id + "'";
					} else {
						selectinvoqty = "select pd_showinvoqty from prodiodetail where pd_id = '" + pd_id + "'";
					}
					SqlRowList rs1 = baseDao.queryForRowSet(selectinvoqty);
					// 已转发票数
					double pd_invoqty = 0;
					if (rs1.next()) {
						pd_invoqty = rs1.getDouble(1);
					}
					if (abd_qty >= 0) {
						double bo = source_qty - pd_invoqty + abd_qty_old - abd_qty;
						if (abd_qty != abd_qty_old) {
							if (bo < 0) {
								// 修改后的发票数+已开票数大于发货数量 报错
								BaseUtil.showError(BaseUtil.getLocalMessage("fa.ars.arbill.turnArbillQtyissmaill"));
							} else {
								// 修改后的发票数+已开票数 小于等于发货数量 可进行正常修改
								// 根据单据中的来源id 查找prodiodetail 表中对应的数据
								double sheyu = abd_qty_old - abd_qty;
								String updatesqlm = "";
								String updatesqld = "";
								String updatesqla = "";
								if (sourcekind.trim().equals("GOODSSEND")) {
									updatesqlm = "update goodssend set gs_invostatuscode='PARTAR',gs_invostatus='"
											+ BaseUtil.getLocalMessage("PARTAR") + "' "
											+ "where gs_id = (select gsd_gsid from goodssenddetail where gsd_id = '" + gsd_id + "')";
									updatesqld = "update goodssenddetail set gsd_statuscode ='PARTAR',gsd_showinvoqty = gsd_showinvoqty-("
											+ sheyu + ") where gsd_id = '" + gsd_id + "'";
									updatesqla = "update arbilldetail set abd_qty='" + abd_qty + "' where abd_id ='" + abd_id + "'";

								} else {
									updatesqlm = "update prodinout set pi_billstatuscode='PARTAR',pi_billstatus='"
											+ BaseUtil.getLocalMessage("PARTAR") + "' "
											+ "where pi_id = (select pd_piid from prodiodetail where pd_id = '" + pd_id + "')";
									updatesqld = "update prodiodetail set pd_auditstatus ='PARTAR',pd_showinvoqty = pd_showinvoqty-("
											+ sheyu + ") where pd_id = '" + pd_id + "'";
									updatesqla = "update arbilldetail set abd_qty='" + abd_qty + "' where abd_id ='" + abd_id + "'";
								}

								List<String> sqllist = new ArrayList<String>();
								sqllist.add(updatesqlm);
								sqllist.add(updatesqld);
								sqllist.add(updatesqla);
								baseDao.execute(sqllist);
							}
						}
					} else {
						double bo = source_qty - pd_invoqty + abd_qty_old - abd_qty;
						if (abd_qty != abd_qty_old) {
							if (bo > 0) {
								// 修改后的发票数+已开票数大于发货数量 报错
								BaseUtil.showError(BaseUtil.getLocalMessage("fa.ars.arbill.turnArbillQtyissmaill"));
							} else {
								// 修改后的发票数+已开票数 小于等于发货数量 可进行正常修改
								// 根据单据中的来源id 查找prodiodetail 表中对应的数据
								double sheyu = abd_qty_old - abd_qty;
								String updatesqlm = "update prodinout set pi_billstatuscode='PARTAR',pi_billstatus='"
										+ BaseUtil.getLocalMessage("PARTAR") + "' "
										+ "where pi_id = (select pd_piid from prodiodetail where pd_id = '" + pd_id + "')";
								String updatesqld = "update prodiodetail set pd_auditstatus ='PARTAR',pd_showinvoqty = pd_showinvoqty-("
										+ sheyu + ") where pd_id = '" + pd_id + "'";
								String updatesqla = "update arbilldetail set abd_qty='" + abd_qty + "' where abd_id ='" + abd_id + "'";
								List<String> sqllist = new ArrayList<String>();
								sqllist.add(updatesqlm);
								sqllist.add(updatesqld);
								sqllist.add(updatesqla);
								baseDao.execute(sqllist);
							}
						}
					}
				} else {
					Object qty = baseDao.getFieldDataByCondition("ARBillDetail", "sum(nvl(abd_qty,0))", "abd_adid=" + adid
							+ " AND abd_id <>" + abd_id);
					Object aq = baseDao.getFieldDataByCondition("ARCheckDetail", "ad_qty", "ad_id=" + adid);
					qty = qty == null ? 0 : qty;
					aq = aq == null ? 0 : aq;
					if (Math.abs(Double.parseDouble(String.valueOf(aq))) < Math.abs(Double.parseDouble(String.valueOf(qty)))
							+ Math.abs(abd_qty)) {
						BaseUtil.showError("开票数量超出对账数,超出数量:"
								+ (Math.abs(Double.parseDouble(String.valueOf(qty))) + Math.abs(abd_qty) - Math.abs(Double
										.parseDouble(String.valueOf(aq)))));
					}
					baseDao.updateByCondition("ARCheckDetail", "ad_yqty=" + ((Double.parseDouble(String.valueOf(qty))) + (abd_qty)),
							"ad_id=" + adid);
					int ac_id = baseDao.getFieldValue("ARCheckDetail", "ad_acid", "ad_id=" + adid, Integer.class);
					arCheckDao.updateBillStatus(ac_id);
				}
			}
		}
		// 修改ARBill
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "ARBill", "ab_id"));
		Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(ass, "dass_condid");

		List<String> gridSql = new ArrayList<String>();
		if (gstore.size() > 0) {
			for (Map<Object, Object> s : gstore) {
				if (s.get("abd_id") == null || s.get("abd_id").equals("") || s.get("abd_id").equals("0")
						|| Integer.parseInt(s.get("abd_id").toString()) <= 0) {
					int id = baseDao.getSeqId("ARBILLDETAIL_SEQ");
					ass = list.get(String.valueOf(s.get("abd_id")));
					if (ass != null) {
						for (Map<Object, Object> m : ass) {// VoucherDetailAss
							m.put("dass_condid", id);
							m.put("dass_id", baseDao.getSeqId("ARBILLDETAILASS_SEQ"));
						}
						baseDao.execute(SqlUtil.getInsertSqlbyGridStore(ass, "ARBillDetailAss"));
					}
					s.put("abd_id", id);
					gridSql.add(SqlUtil.getInsertSqlByMap(s, "ARBillDetail"));
				} else {
					gridSql.add(SqlUtil.getUpdateSqlByFormStore(s, "ARBillDetail", "abd_id"));
					// 科目有修改的情况下，先删除之前科目的辅助核算
					gridSql.add("delete from ARBillDetailAss where dass_condid="
							+ s.get("abd_id")
							+ " and instr(nvl((select ca_assname from category left join ARBillDetail on ca_code=abd_catecode where abd_id=dass_condid and ca_assname is not null),' '), dass_assname) = 0");
				}
			}
			for (Object key : list.keySet()) {
				Integer id = Integer.parseInt(String.valueOf(key));
				if (id > 0) {
					ass = list.get(key);
					if (ass != null) {
						for (Map<Object, Object> map : ass) {
							// 科目修改的情况下，辅助核算类型可能一样
							if (!StringUtil.hasText(map.get("dass_id")) || Integer.parseInt(String.valueOf(map.get("dass_id"))) <= 0) {
								gridSql.add("delete from ARBillDetailAss where dass_condid=" + map.get("dass_condid")
										+ " and dass_asstype='" + map.get("dass_asstype") + "'");
							}
						}
						List<String> sqls = SqlUtil.getInsertOrUpdateSqlbyGridStore(ass, "ARBillDetailAss", "dass_id");
						gridSql.addAll(sqls);
					}
				}
			}
			baseDao.execute(gridSql);
		} else {
			Set<Object> items = list.keySet();
			for (Object i : items) {
				ass = list.get(String.valueOf(i));
				if (ass != null) {
					List<String> sqls = SqlUtil.getUpdateSqlbyGridStore(ass, "ARBillDetailAss", "dass_id");
					for (Map<Object, Object> m : ass) {
						if (m.get("dass_id") == null || m.get("dass_id").equals("") || m.get("dass_id").equals("0")
								|| Integer.parseInt(m.get("dass_id").toString()) <= 0) {
							m.put("dass_id", baseDao.getSeqId("ARBILLDETAILASS_SEQ"));
							sqls.add(SqlUtil.getInsertSqlByMap(m, "ARBillDetailAss"));
						}
					}
					baseDao.execute(sqls);
				}
			}
		}
		// 记录操作
		baseDao.logger.update(caller, "ab_id", ab_id);
		getTotal(ab_id, caller);
		Object cu_duedays = baseDao.getFieldDataByCondition("customer", "nvl(cu_duedays,0)", "cu_code='" + store.get("ab_custcode") + "'");
		String res = baseDao.callProcedure("SP_GETPAYDATE_CUST",
				new Object[] { Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), store.get("ab_paymentcode"), cu_duedays,
						store.get("ab_custcode") });
		baseDao.updateByCondition("arbill", "ab_paydate='" + res + "'", " ab_id=" + ab_id);
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore, ass });
	}

	@Override
	public void deleteARBill(int ab_id, String caller) {
		Object status[] = baseDao.getFieldsDataByCondition("ARBill", new String[] { "ab_auditstatuscode", "ab_statuscode" }, "ab_id="
				+ ab_id);
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		if (!status[1].equals("UNPOST")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(ab_sourcetype||'['||ab_source||']') from arbill where ab_class='其它应收单' and ab_id=" + ab_id
						+ " and ab_sourcetype='项目收入确认'", String.class);
		if (dets != null) {
			BaseUtil.showError("请在来源:" + dets + "中进行反审核操作！");
		}
		checkVoucher(ab_id);
		baseDao.delCheck("ARBill", ab_id);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, ab_id);
		// 删除ARBill
		baseDao.deleteById("ARBill", "ab_id", ab_id);
		// 删除ARBillDetail
		baseDao.deleteById("ARBilldetail", "abd_abid", ab_id);
		// 记录操作
		baseDao.logger.delete(caller, "ab_id", ab_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ab_id);
	}

	@Override
	public String[] printARBill(String caller, int ab_id, String reportName, String condition) {
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, ab_id);
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		baseDao.updateByCondition("ARBill", "ab_printstatuscode='PRINTED',ab_printstatus='" + BaseUtil.getLocalMessage("PRINTED") + "'",
				"ab_id=" + ab_id);
		// 记录操作
		baseDao.logger.print(caller, "ab_id", ab_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, ab_id);
		return keys;
	}

	@Override
	public void auditARBill(int ab_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ARBill", "ab_auditstatuscode", "ab_id=" + ab_id);
		StateAssert.auditOnlyCommited(status);
		checkYearmonth(ab_id);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ab_id);
		// 执行审核操作
		baseDao.audit("ARBill", "ab_id=" + ab_id, "ab_auditstatus", "ab_auditstatuscode");
		// 记录操作
		baseDao.logger.audit(caller, "ab_id", ab_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ab_id);
	}

	@Override
	public void resAuditARBill(int ab_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] objs = baseDao
				.getFieldsDataByCondition("ARBill", new String[] { "ab_auditstatuscode", "ab_statuscode" }, "ab_id=" + ab_id);
		if (!objs[0].equals("AUDITED") || objs[1].equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		checkYearmonth(ab_id);
		handlerService.beforeResAudit(caller, ab_id);
		// 执行反审核操作
		baseDao.resOperate("ARBill", "ab_id=" + ab_id, "ab_auditstatus", "ab_auditstatuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "ab_id", ab_id);
		handlerService.afterResAudit(caller, ab_id);
	}

	/**
	 * 更新操作前检验
	 * 
	 * @param store
	 *            主记录
	 * @param grid
	 *            明细数据
	 */
	private void checkOnUpdate(Map<Object, Object> store, List<Map<Object, Object>> grid) {
		int myYM = voucherDao.getPeriodsFromDate("Month-C", store.get("ab_date").toString());
		int nowYM = voucherDao.getNowPddetno("Month-C");// 当前期间
		if (myYM < nowYM) {
			BaseUtil.showError("期间" + myYM + "已经结转,当前期间:" + nowYM + "<br>请修改日期，或反结转应收账.");
		}
	}

	/**
	 * 判断主表发票期间是否小于当前账期，否则不允许任何操作
	 * 
	 * @param ab_id
	 */
	private void checkYearmonth(int ab_id) {
		Integer yearmonth = baseDao.getJdbcTemplate().queryForObject("select to_char(ab_date,'yyyymm') from arbill where ab_id=?",
				Integer.class, ab_id);
		int nowym = voucherDao.getNowPddetno("Month-C");// 当前期间
		if (yearmonth < nowym) {
			BaseUtil.showError("期间" + yearmonth + "已经结转,当前期间:" + nowym + "<br>请修改日期，或反结转应收账.");
		}
		// 判断来源单据的发票期间与发票客户是否一致
		// 来源是出入库单
		String errMonths = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat('行'||abd_detno||'出入库期间:'||to_char(pi_date,'yyyymm')) from arbilldetail left join arbill on abd_abid=ab_id left join prodiodetail on abd_pdid=pd_id and abd_sourcekind='PRODIODETAIL' left join prodinout on pd_piid=pi_id where ab_id=? and to_char(ab_date,'yyyymm')<>to_char(pi_date,'yyyymm')",
						String.class, ab_id);
		if (errMonths != null)
			BaseUtil.showError("发票期间与出入库单不一致，行：" + errMonths);
		// 来源是发出商品
		errMonths = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat('行'||abd_detno||'发出商品期间:'||to_char(gs_date,'yyyymm')) from arbilldetail left join arbill on abd_abid=ab_id left join goodssenddetail on abd_sourcedetailid=gsd_id and abd_sourcekind='GOODSSEND' left join goodssend on gsd_gsid=gs_id where ab_id=? and to_char(ab_date,'yyyymm')<=to_char(gs_date,'yyyymm')",
						String.class, ab_id);
		if (errMonths != null)
			BaseUtil.showError("发票期间应大于发出商品期间，行：" + errMonths);

		// 判断来源单据的客户与发票客户是否一致
		// 来源是出入库单
		String errCusts = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(abd_detno) from arbilldetail left join arbill on abd_abid=ab_id left join prodiodetail on abd_pdid=pd_id and abd_sourcekind='PRODIODETAIL' left join prodinout on pd_piid=pi_id where ab_id=? and ab_custcode<>pi_arcode",
						String.class, ab_id);
		if (errCusts != null)
			BaseUtil.showError("来源单据的客户与发票客户不一致,行：" + errCusts);
		// 来源是发出商品
		errCusts = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(abd_detno) from arbilldetail left join arbill on abd_abid=ab_id left join goodssenddetail on abd_sourcedetailid=gsd_id and abd_sourcekind='GOODSSEND' left join goodssend on gsd_gsid=gs_id where ab_id=? and ab_custcode<>gs_custcode",
						String.class, ab_id);
		if (errCusts != null)
			BaseUtil.showError("来源单据的客户与发票客户不一致,行：" + errCusts);
		// 判断来源单据的币别与发票的是否一致
		// 来源是出入库单
		String errCurrs = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(abd_detno) from arbilldetail left join arbill on abd_abid=ab_id left join prodiodetail on abd_pdid=pd_id and abd_sourcekind='PRODIODETAIL' left join prodinout on pd_piid=pi_id where ab_id=? and ab_currency<>pi_currency",
						String.class, ab_id);
		if (errCurrs != null)
			BaseUtil.showError("来源单据的币别与发票的不一致,行：" + errCurrs);
		// 来源是发出商品
		errCurrs = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(abd_detno) from arbilldetail left join arbill on abd_abid=ab_id left join goodssenddetail on abd_sourcedetailid=gsd_id and abd_sourcekind='GOODSSEND' left join goodssend on gsd_gsid=gs_id where ab_id=? and ab_currency<>gs_currency",
						String.class, ab_id);
		if (errCurrs != null)
			BaseUtil.showError("来源单据的币a别与发票的不一致,行：" + errCurrs);

	}

	@Override
	public void submitARBill(int ab_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ARBill", "ab_auditstatuscode", "ab_id=" + ab_id);
		StateAssert.submitOnlyEntering(status);
		checkYearmonth(ab_id);
		checkAss(ab_id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ab_id);
		getTotal(ab_id, caller);
		// 执行提交操作
		baseDao.submit("ARBill", "ab_id=" + ab_id, "ab_auditstatus", "ab_auditstatuscode");
		// 记录操作
		baseDao.logger.submit(caller, "ab_id", ab_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ab_id);

	}

	@Override
	public void resSubmitARBill(int ab_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ARBill", "ab_auditstatuscode", "ab_id=" + ab_id);
		StateAssert.resSubmitOnlyCommited(status);
		// checkYearmonth(ab_id);
		handlerService.beforeResSubmit(caller, ab_id);
		// 执行反提交操作
		baseDao.resOperate("ARBill", "ab_id=" + ab_id, "ab_auditstatus", "ab_auditstatuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ab_id", ab_id);
		handlerService.afterResSubmit(caller, ab_id);
	}

	@Override
	public void postARBill(int ab_id, String caller) {
		getTotal(ab_id, caller);
		Object[] status = baseDao.getFieldsDataByCondition("ARBill", new String[] { "ab_statuscode", "ab_date", "ab_yearmonth",
				"ab_aramount" }, "ab_id=" + ab_id);
		if (status[0].equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.post_onlyUnPost"));
		}
		if (caller.trim().equals("APBill!OTDW")) {
			if ("0".equals(status[3]) || status[3] == null) {
				BaseUtil.showError("应付金额为0，不允许过账！");
			}
		}
		if (caller.trim().equals("ARBill!IRMA")) {
			int yearmonth = Integer.parseInt(status[2].toString());
			int dateint = 0;
			String date = status[1].toString();
			date = date.replace("-", "");
			dateint = Integer.parseInt(date.substring(0, 6));
			if (yearmonth != dateint) {
				BaseUtil.showError(BaseUtil.getLocalMessage("fa.ars.arbill.post_onlyYearmonthDateSame"));
			}
		}
		checkAss(ab_id);
		// 过账前的其它逻辑
		handlerService.beforePost(caller, ab_id);
		// 执行过账操作
		Object obj = baseDao.getFieldDataByCondition("ARBill", "ab_code", "ab_id=" + ab_id);
		// 存储过程
		String res = baseDao.callProcedure("Sp_CommiteARBill", new Object[] { obj, 1 });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
		baseDao.updateByCondition("ARBill", "ab_statuscode='POSTED',ab_status='" + BaseUtil.getLocalMessage("POSTED") + "'", "ab_id="
				+ ab_id);
		baseDao.updateByCondition("ARBillDetail", "abd_status=99,abd_statuscode='POSTED'", "abd_abid=" + ab_id);
		baseDao.execute("update prodiodetail set pd_invoqty=nvl((select sum(abd_qty) from arbilldetail where abd_sourcedetailid=pd_id and abd_sourcekind='PRODIODETAIL' and abd_status>0),0) "
				+ "where (pd_piid,pd_piclass) in (select abd_sourcedetailid,abd_sourcetype from arbilldetail where abd_abid=" + ab_id + ")");
		// 记录操作
		baseDao.logger.post(caller, "ab_id", ab_id);
		// 更新过账人和过账日期
		baseDao.updateByCondition("ARBill", "ab_postman='" + SystemSession.getUser().getEm_name() + "',ab_postdate=sysdate", "ab_id="
				+ ab_id);
		// 执行过账后的其它逻辑
		handlerService.afterPost(caller, ab_id);
	}

	@Override
	public void resPostARBill(int ab_id, String caller) {
		// 只能对状态为[已过账]的单据进行反过账操作!
		Object[] status = baseDao.getFieldsDataByCondition("ARBill", new String[] { "ab_statuscode", "ab_code" }, "ab_id=" + ab_id);
		StateAssert.resPostOnlyPosted(status[0]);
		handlerService.beforeResPost(caller, ab_id);
		checkVoucher(ab_id);
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(AB_SOURCECODE) from arbill where ab_id=? and ab_class='其它应收单' and AB_SOURCETYPE='应收开票记录'", String.class,
				ab_id);
		if (dets != null) {
			BaseUtil.showError("需要在来源应收开票记录[" + dets + "]中进行反记账操作!");
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(distinct ac_code) from archeck left join archeckdetail on ac_id=ad_acid where AD_SOURCECODE='"
						+ status[1] + "' and nvl(AD_SOURCETYPE,' ')='ARBILL'", String.class);
		if (dets != null) {
			BaseUtil.showError("已转应收对账单[" + dets + "]，不允许进行反记账操作!");
		}
		// 执行反过账操作
		Object obj = baseDao.getFieldDataByCondition("ARBill", "ab_code", "ab_id=" + ab_id);
		if (caller != null && caller.trim().equals("ARBill!IRMA")) {
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(distinct rb_kind) from recbalancedetail left join recbalance on rbd_rbid = rb_id where rbd_ordercode=? and nvl(rbd_ordercode,' ') <>' '",
							String.class, obj.toString());
			if (dets != null) {
				BaseUtil.showError(dets + "明细行中添加了这张发票,不能反过账!");
			}
			dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(distinct ARD_biid) from BILLOUTDETAIL where ARD_ORDERCODE=? and nvl(ARD_ORDERCODE,' ') <>' '",
					String.class, obj.toString());
			if (dets != null) {
				BaseUtil.showError("开票记录明细行中添加了这张发票,不能反过账!");
			}
		}
		// 存储过程
		String res = baseDao.callProcedure("Sp_UnCommiteARBill", new Object[] { obj, 1 });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
		baseDao.updateByCondition("ARBill",
				"ab_auditstatuscode='ENTERING',ab_statuscode='UNPOST',ab_auditstatus='" + BaseUtil.getLocalMessage("ENTERING")
						+ "',ab_status='" + BaseUtil.getLocalMessage("UNPOST") + "'", "ab_id=" + ab_id);
		baseDao.updateByCondition("ARBillDetail", "abd_status=0,abd_statuscode='ENTERING'", "abd_abid=" + ab_id);
		baseDao.execute("update prodiodetail set pd_invoqty=nvl((select sum(abd_qty) from arbilldetail where abd_sourcedetailid=pd_id and abd_sourcekind='PRODIODETAIL' and abd_status>0),0) "
				+ "where (pd_piid,pd_piclass) in (select abd_sourcedetailid,abd_sourcetype from arbilldetail where abd_abid=" + ab_id + ")");
		// 记录操作
		baseDao.logger.resPost(caller, "ab_id", ab_id);

		// 更新过账人和过账日期
		baseDao.updateByCondition("ARBill", "ab_postman=null,ab_postdate=null", "ab_id=" + ab_id);
		handlerService.afterResPost(caller, ab_id);
	}

	@Override
	public String vastPostARBill(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		List<String> rMsg = new ArrayList<String>();
		List<String> wMsg = new ArrayList<String>();
		Object[] status = null;
		String ab_id = null;
		String ab_code = null;
		for (Map<Object, Object> map : maps) {
			ab_id = map.get("ab_id").toString();
			ab_code = map.get("ab_code").toString();
			status = baseDao.getFieldsDataByCondition("ARBill", new String[] { "ab_statuscode", "ab_date", "ab_yearmonth" }, "ab_id="
					+ ab_id);
			if (status[0].equals("POSTED")) {
				wMsg.add(ab_code);
			} else {
				int yearmonth = Integer.parseInt(status[2].toString());
				int dateint = 0;
				String date = status[1].toString();

				date = date.replace("-", "");
				dateint = Integer.parseInt(date.substring(0, 6));
				if (yearmonth != dateint) {
					wMsg.add(ab_code);
				} else {
					Object obj = baseDao.getFieldDataByCondition("ARBill", "ab_code", "ab_id=" + ab_id);
					// //存储过程
					String res = baseDao.callProcedure("Sp_CommiteARBill", new Object[] { obj, 1 });
					if (res == null || res.trim().equals("null")) {
						rMsg.add(ab_code);
					} else {
						wMsg.add(ab_code);
					}
				}
			}
		}
		String returnMsg = "";
		if (wMsg.size() > 0) {
			returnMsg = "单据:";
			for (String s : wMsg) {
				returnMsg = returnMsg + s + " ";
			}
			returnMsg = returnMsg + "过账失败,请检查!";
		} else {
			returnMsg = "批量过账成功";
		}

		return returnMsg;

	}

	@Override
	public void createVoucherARO(String abcode, String abdate, String caller) {
		// 只能对状态为[已过账]的单据进行凭证制作!
		Object status = baseDao.getFieldDataByCondition("ARBill", "ab_statuscode", "ab_code='" + abcode + "' ");
		if (!status.equals("POSTED")) {
			BaseUtil.showError("只能对已过账的单据进行凭证制作!");
		}
		// 调用存储过程
		int yearmonth = voucherDao.getPeriodsFromDate("MONTH-C", abdate);
		String res = baseDao.callProcedure("FA_VOUCHERCREATE", new Object[] { yearmonth, "ARBill", "'" + abcode + "'", "single", "其它应收单",
				"AR", SystemSession.getUser().getEm_id(), SystemSession.getUser().getEm_name() });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
	}

	@Override
	@Transactional
	public String confirmXJSK(int ab_id, String catecode, String caller) {
		String res = null;
		Object arcode = baseDao.getFieldDataByCondition("accountregister", "ar_code", "ar_sourceid=" + ab_id + " and AR_SOURCETYPE='应收发票'");
		if (arcode != null && !"".equals(arcode.toString())) {
			BaseUtil.showError("当前单据已经确认过收款，关联的收款单号:" + arcode);
		}
		Object[] date = baseDao.getFieldsDataByCondition("arbill", new String[] { "ab_date" }, "ab_id=" + ab_id);
		baseDao.checkCloseMonth("MONTH-C", date[0]);
		// 生成应收款单
		int arid = baseDao.getSeqId("ACCOUNTREGISTER_SEQ");
		String code = baseDao.sGetMaxNumber("AccountRegister", 2);
		if (!StringUtil.hasText(catecode)) {
			catecode = baseDao.getDBSetting("confirmXJSK");
			if (StringUtil.hasText(catecode)) {
				baseDao.execute("update ARBill set ab_catecode='" + catecode
						+ "', ab_catename=(select ca_name from category where ca_code='" + catecode
						+ "') where nvl(ab_catecode,' ')=' ' and ab_id = " + ab_id);
			} else
				BaseUtil.showError("现金收款科目未填写！");
		} else {
			baseDao.execute("update ARBill set ab_catecode='" + catecode + "', ab_catename=(select ca_name from category where ca_code='"
					+ catecode + "') where nvl(ab_catecode,' ')=' ' and ab_id = " + ab_id);
		}
		Object[] cate = baseDao.getFieldsDataByCondition("category", new String[] { "ca_id", "ca_name" }, "ca_code='" + catecode + "'");
		baseDao.execute("INSERT INTO accountregister(ar_id, ar_code, ar_date, ar_type, ar_accountcode, ar_accountname, ar_accountcurrency, ar_accountrate,"
				+ "ar_custcode, ar_custname, ar_sellercode, ar_sellername, ar_departmentcode, ar_departmentname, ar_deposit,"
				+ "ar_arapcurrency, ar_araprate, ar_aramount, ar_recordman, ar_recorddate, ar_memo, ar_status, ar_statuscode, "
				+ "ar_cateid, ar_sellerid, ar_sourceid, ar_source, AR_SOURCETYPE, ar_vouchercode)"
				+ " select "
				+ arid
				+ ",'"
				+ code
				+ "',ab_date, '应收款', '"
				+ catecode
				+ "','"
				+ cate[1]
				+ "',ab_currency,ab_rate,ab_custcode,ab_custname,"
				+ "'',ab_seller,ab_departmentcode,ab_departmentname,ab_aramount,'',1,0,'"
				+ SystemSession.getUser().getEm_name()
				+ "', sysdate,'现金收款','"
				+ BaseUtil.getLocalMessage("COMMITED")
				+ "','COMMITED','"
				+ cate[0]
				+ "',0,'"
				+ ab_id
				+ "',ab_code,'应收发票','UNNEED' from arbill where ab_id=" + ab_id);
		baseDao.execute("INSERT INTO accountregisterdetail(ard_id, ard_arid, ard_detno, ard_currency, ard_ordercode, ard_orderdetno, ard_ordertype, ard_orderamount,ard_nowbalance,ard_orderid,ard_havepay,ard_credit)"
				+ " select accountregisterdetail_seq.nextval,"
				+ arid
				+ ",rownum, ab_currency, ab_code, 0,'应收发票', nvl(ab_aramount,0), nvl(ab_aramount,0), ab_id, ab_payamount, 0"
				+ " from ARBill where ab_id=" + ab_id);
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select * from accountregister,category where ar_id=? and ar_accountcode=ca_code and (ca_asstype like '%Dept%') and nvl(ar_departmentcode,' ')<>' '",
						arid);
		while (rs.next()) {
			int counts = baseDao.getCountByCondition("AccountRegisterAss", "ass_assname='部门' and ASS_CONID=" + arid);
			if (counts == 0) {
				baseDao.execute("insert into AccountRegisterAss(ASS_ID,ASS_CONID,ASS_ASSNAME,ASS_CODEFIELD,ASS_NAMEFIELD,ASS_ASSTYPE) "
						+ "select AccountRegisterAss_seq.nextval,ar_id,'部门', ar_departmentcode,ar_departmentname,'Dept' from accountregister where ar_id="
						+ arid + " and nvl(ar_departmentcode,' ')<>' '");
			} else {
				baseDao.execute("update AccountRegisterAss set ASS_CODEFIELD='" + rs.getObject("ar_departmentcode") + "',ASS_NAMEFIELD='"
						+ rs.getObject("ar_departmentname") + "' where ASS_CONID=" + arid
						+ " and nvl(ASS_CODEFIELD,' ')=' ' and ASS_ASSNAME='部门'");
			}
		}
		SqlRowList rs1 = baseDao
				.queryForRowSet(
						"select * from accountregister,category where ar_id=? and ar_accountcode=ca_code and REGEXP_LIKE(ca_asstype, '(#|^)Otp') and nvl(ar_prjcode,' ')<>' '",
						arid);
		while (rs1.next()) {
			int counts = baseDao.getCountByCondition("AccountRegisterAss", "ass_assname='项目' and ASS_CONID=" + arid);
			if (counts == 0) {
				baseDao.execute("insert into AccountRegisterAss(ASS_ID,ASS_CONID,ASS_ASSNAME,ASS_CODEFIELD,ASS_NAMEFIELD,ASS_ASSTYPE) "
						+ "select AccountRegisterAss_seq.nextval,ar_id,'项目', ar_prjcode,ar_prjname,'Otp' from accountregister where ar_id="
						+ arid + " and nvl(ar_prjcode,' ')<>' '");
			} else {
				baseDao.execute("update AccountRegisterAss set ASS_CODEFIELD='" + rs1.getObject("ar_prjcode") + "',ASS_NAMEFIELD='"
						+ rs1.getObject("ar_prjname") + "' where ASS_CONID=" + arid
						+ " and nvl(ASS_CODEFIELD,' ')=' ' and ASS_ASSNAME='项目'");
			}
		}
		baseDao.execute(
				"update accountregisterdetail set ard_currency= (select ar_accountcurrency from accountregister where ard_arid=ar_id) where ard_arid=?",
				arid);
		baseDao.execute("update accountregisterdetail set ard_credit= ard_orderamount where ard_arid=?", arid);
		baseDao.execute(
				"update accountregister set ar_sellername=(select cu_sellername from customer where cu_code=ar_custcode) where ar_id=? and nvl(ar_sellername,' ')=' '",
				arid);
		baseDao.execute(
				"update accountregister set (ar_sellerid,ar_sellercode)=(select max(em_id),max(em_code) from employee where em_name=ar_sellername) where ar_id=?",
				arid);
		baseDao.execute("update accountregister set ar_arapcurrency=ar_accountcurrency,ar_aramount=ar_deposit where ar_id=?", arid);
		res = baseDao.callProcedure("SP_COMMITEREGISTER", new Object[] { code, String.valueOf(SystemSession.getUser().getEm_id()) });
		if (res != null && !res.trim().equals("")) {
			if ("OK".equals(res.toUpperCase())) {
				// 记录操作
				baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "记账", "银行收款记账,编号:" + code, "ARBill!IRMA|ab_id="
						+ ab_id));
				baseDao.execute("update arbill set ab_pricekind='现金收款' where ab_id=?", ab_id);
			} else {
				BaseUtil.showError(res);
			}
		}
		baseDao.logger.others("确认现金收款", "确认成功", caller, "ab_id", ab_id);
		return "确认现金收款成功!";
	}

	@Override
	public String confirmYJSK(int ab_id, String caller) {
		String res = null;
		Object arcode = baseDao.getFieldDataByCondition("accountregister", "ar_code", "ar_sourceid=" + ab_id + " and AR_SOURCETYPE='应收发票'");
		if (arcode != null && !"".equals(arcode.toString())) {
			BaseUtil.showError("当前单据已经确认过收款，关联的银行登记单号:" + arcode);
		}
		Object[] date = baseDao.getFieldsDataByCondition("arbill", new String[] { "ab_date" }, "ab_id=" + ab_id);
		baseDao.checkCloseMonth("MONTH-C", date[0]);
		// 生成应收款单
		int arid = baseDao.getSeqId("ACCOUNTREGISTER_SEQ");
		String code = baseDao.sGetMaxNumber("AccountRegister", 2);
		String catecode = baseDao.getDBSetting("confirmYJSK");
		if (!StringUtil.hasText(catecode)) {
			BaseUtil.showError("样机收款科目未填写！");
		}
		Object[] cate = baseDao.getFieldsDataByCondition("category", new String[] { "ca_id", "ca_name" }, "ca_code='" + catecode + "'");
		baseDao.execute("INSERT INTO accountregister(ar_id, ar_code, ar_date, ar_type, ar_accountcode, ar_accountname, ar_accountcurrency, ar_accountrate,"
				+ "ar_custcode, ar_custname, ar_sellercode, ar_sellername, ar_departmentcode, ar_departmentname, ar_deposit,"
				+ "ar_arapcurrency, ar_araprate, ar_aramount, ar_recordman, ar_recorddate, ar_memo, ar_status, ar_statuscode, "
				+ "ar_cateid, ar_sellerid, ar_sourceid, ar_source, AR_SOURCETYPE,ar_vouchercode)"
				+ " select "
				+ arid
				+ ",'"
				+ code
				+ "',ab_date, '应收款', '"
				+ catecode
				+ "','"
				+ cate[1]
				+ "',ab_currency,ab_rate,ab_custcode,ab_custname,"
				+ "'',ab_seller,ab_departmentcode,ab_departmentname,ab_aramount,'RMB',1,0,'"
				+ SystemSession.getUser().getEm_name()
				+ "', sysdate, '样机收款', '"
				+ BaseUtil.getLocalMessage("COMMITED")
				+ "','COMMITED','"
				+ cate[0]
				+ "',0,'"
				+ ab_id
				+ "',ab_code,'应收发票','UNNEED' from arbill where ab_id=" + ab_id);
		baseDao.execute("INSERT INTO accountregisterdetail(ard_id, ard_arid, ard_detno, ard_currency, ard_ordercode, ard_orderdetno, ard_ordertype, ard_orderamount,ard_nowbalance,ard_orderid,ard_havepay)"
				+ " select accountregisterdetail_seq.nextval,"
				+ arid
				+ ",rownum, ab_currency, ab_code, 0,'应收发票', nvl(ab_aramount,0), nvl(ab_aramount,0), ab_id, ab_payamount"
				+ " from ARBill where ab_id=" + ab_id);
		baseDao.execute(
				"update accountregisterdetail set ard_currency= (select ar_accountcurrency from accountregister where ard_arid=ar_id) where ard_arid=?",
				arid);
		baseDao.execute("update accountregisterdetail set ard_credit= ard_orderamount where ard_arid=?", arid);
		baseDao.execute(
				"update accountregister set ar_sellername=(select cu_sellername from customer where cu_code=ar_custcode) where ar_id=? and nvl(ar_sellername,' ')=' '",
				arid);
		baseDao.execute(
				"update accountregister set (ar_sellerid,ar_sellercode)=(select max(em_id),max(em_code) from employee where em_name=ar_sellername) where ar_id=?",
				arid);
		baseDao.execute("update accountregister set ar_aramount=ar_deposit where ar_id=?", arid);
		res = baseDao.callProcedure("SP_COMMITEREGISTER", new Object[] { code, String.valueOf(SystemSession.getUser().getEm_id()) });
		if (res != null && !res.trim().equals("")) {
			if ("OK".equals(res.toUpperCase())) {
				// 记录操作
				baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "记账", "银行收款记账,编号:" + code, "ARBill!IRMA|ab_id="
						+ ab_id));
				baseDao.execute("update arbill set ab_pricekind='样机收款' where ab_id=?", ab_id);
			} else {
				BaseUtil.showError(res);
			}
		}
		baseDao.logger.others("确认样机收款", "确认成功", caller, "ab_id", ab_id);
		return "确认样机收款成功!";
	}

	@Override
	public String cancelXJSK(int ab_id, String caller) {
		String res = null;
		Object code = baseDao.getFieldDataByCondition("arbill", "ab_vouchercode", "ab_id=" + ab_id);
		if (StringUtil.hasText(code) && !"UNNEED".equals(code)) {
			BaseUtil.showError("当前发票已经生成凭证，不能取消现金收款!凭证号:" + code);
		}
		Object[] date = baseDao.getFieldsDataByCondition("arbill", new String[] { "ab_date" }, "ab_id=" + ab_id);
		baseDao.checkCloseMonth("MONTH-C", date[0]);
		code = baseDao.getFieldDataByCondition("accountregister", "ar_code", "ar_sourceid=" + ab_id + " and AR_SOURCETYPE='应收发票'");
		if (code != null && !"".equals(code.toString())) {
			Object[] rbcode = baseDao.getFieldsDataByCondition("recbalance", new String[] { "rb_code", "rb_statuscode" },
					"rb_sourceid=(select ar_id from accountregister where ar_code='" + code + "')");
			if (rbcode != null) {
				if (rbcode[1] != "POSTED") {
					res = baseDao.callProcedure("SP_UNCOMMITEREGISTER",
							new Object[] { code, String.valueOf(SystemSession.getUser().getEm_id()) });
					if (res != null && !res.trim().equals("")) {
						if ("OK".equals(res.toUpperCase())) {
							// 记录操作
							baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "反记帐", "银行收款反记帐,编号:" + code,
									"ARBill!IRMA|ab_id=" + ab_id));
							baseDao.execute("delete from AccountRegister where ar_code=?", code);
							baseDao.execute("update arbill set ab_pricekind='',ab_payamount=0 where ab_id=?", ab_id);
						} else {
							BaseUtil.showError(res);
						}
					}
				} else {
					res = baseDao.callProcedure("Sp_UnCommiteRec", new Object[] { rbcode[0] });
					if (res != null && !res.trim().equals("")) {
						if ("OK".equals(res.toUpperCase())) {
							res = baseDao.callProcedure("SP_UNCOMMITEREGISTER",
									new Object[] { code, String.valueOf(SystemSession.getUser().getEm_id()) });
							if (res != null && !res.trim().equals("")) {
								if ("OK".equals(res.toUpperCase())) {
									// 记录操作
									baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "反记帐", "银行收款反记帐,编号:" + code,
											"ARBill!IRMA|ab_id=" + ab_id));
									baseDao.execute("delete from AccountRegister where ar_code=?", code);
									baseDao.execute("update arbill set ab_pricekind='',ab_payamount=0 where ab_id=?", ab_id);
								} else {
									BaseUtil.showError(res);
								}
							}
						} else {
							BaseUtil.showError(res);
						}
					}
				}
			} else {
				baseDao.execute("delete from AccountRegister where ar_code=?", code);
				baseDao.execute("update arbill set ab_pricekind='',ab_payamount=0 where ab_id=?", ab_id);
			}
		}
		baseDao.logger.others("取消收款", "取消成功", caller, "ab_id", ab_id);
		return "取消成功!";
	}

	@Override
	public String[] printVoucherCodeARBill(String caller, int ab_id, String reportName, String condition) {
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, ab_id);
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "ab_id", ab_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, ab_id);
		return keys;
	}

	@Override
	public void updateTaxcode(String caller, int ab_id, String ab_refno, String ab_remark) {
		baseDao.updateByCondition("ARBill", "ab_refno='" + ab_refno + "',ab_remark='" + ab_remark + "'", "ab_id=" + ab_id);
		baseDao.logger.others("更新税票信息", "更新成功", caller, "ab_id", ab_id);
	}

	/**
	 * 复制银行登记
	 */
	public JSONObject copyARBill(int id, String caller) {
		Map<String, Object> dif = new HashMap<String, Object>();
		// Copy 银行登记
		int nId = baseDao.getSeqId("ARBILL_SEQ");
		String code = baseDao.sGetMaxNumber("ARBill", 2);
		dif.put("ab_id", nId);
		dif.put("ab_code", "'" + code + "'");
		dif.put("ab_inputname", "'" + SystemSession.getUser().getEm_name() + "'");
		dif.put("ab_auditstatus", "'" + BaseUtil.getLocalMessage("ENTERING") + "'");
		dif.put("ab_auditstatuscode", "'ENTERING'");
		dif.put("ab_indate", "sysdate");
		dif.put("ab_source", "null");
		dif.put("ab_sourcecode", "null");
		dif.put("ab_sourcetype", "null");
		dif.put("ab_sourceid", 0);
		dif.put("ab_payamount", 0);
		dif.put("ab_costvouchercode", "null");
		dif.put("ab_vouchercode", "null");
		dif.put("ab_printstatus", "'" + BaseUtil.getLocalMessage("UNPRINT") + "'");
		dif.put("ab_printstatuscode", "'UNPRINT'");
		dif.put("ab_paystatus", "'" + BaseUtil.getLocalMessage("UNCOLLECT") + "'");
		dif.put("ab_paystatuscode", "'UNCOLLECT'");
		dif.put("ab_status", "'" + BaseUtil.getLocalMessage("UNPOST") + "'");
		dif.put("ab_statuscode", "'UNPOST'");
		baseDao.copyRecord("ARBill", "ARBill", "ab_id=" + id, dif);
		// Copy 银行登记明细
		SqlRowList list = baseDao.queryForRowSet("SELECT abd_id FROM ARBillDetail WHERE abd_abid=?", id);
		SqlRowList ass = null;
		Integer dId = null;
		while (list.next()) {
			dif = new HashMap<String, Object>();
			dId = baseDao.getSeqId("ARBILLDETAIL_SEQ");
			dif.put("abd_id", dId);
			dif.put("abd_abid", nId);
			dif.put("abd_code", "'" + code + "'");
			dif.put("abd_status", 0);
			dif.put("abd_statuscode", null);
			dif.put("abd_adid", 0);
			dif.put("abd_ycheck", 0);
			dif.put("abd_yqty", 0);
			dif.put("abd_sourceid", 0);
			dif.put("abd_source", null);
			dif.put("ABD_IOSOURCE", null);
			dif.put("ABD_IOSOURCEID", 0);
			dif.put("ABD_INVOQTY", 0);
			baseDao.copyRecord("ARBillDetail", "ARBillDetail", "abd_id=" + list.getInt("abd_id"), dif);
			// Copy 明细辅助核算
			ass = baseDao.queryForRowSet("SELECT dass_id FROM arbilldetailass WHERE dass_condid=?", list.getInt("abd_id"));
			while (ass.next()) {
				dif = new HashMap<String, Object>();
				dif.put("dass_id", baseDao.getSeqId("ARBILLDETAILASS_SEQ"));
				dif.put("dass_condid", dId);
				baseDao.copyRecord("ARBillDetailAss", "ARBillDetailAss", "dass_id=" + ass.getInt("dass_id"), dif);
			}
		}
		JSONObject obj = new JSONObject();
		obj.put("ab_id", nId);
		obj.put("ab_code", code);
		return obj;
	}

	@Override
	public String getOrderType(String caller, int id, String code) {
		String kind = "";
		if ("PayBalance".equals(caller)) {
			kind = baseDao.queryForObject("select ab_class from apbill where ab_code=?", String.class, code);
			if (kind != null) {
				baseDao.execute("update paybalancedetail set pbd_ordertype='" + kind
						+ "' where nvl(pbd_ordercode,' ')<>' ' and nvl(pbd_ordertype,' ')=' ' and pbd_id=" + id);
			}
		} else if ("RecBalance!PBIL".equals(caller)) {
			kind = baseDao.queryForObject("select ab_class from ARBill where ab_code=?", String.class, code);
			if (kind != null) {
				baseDao.execute("update RecBalanceDetail set rbd_ordertype='" + kind
						+ "' where nvl(rbd_ordercode,' ')<>' ' and nvl(rbd_ordertype,' ')=' ' and rbd_id=" + id);
			}
		}
		return kind;
	}

	@Override
	public List<Map<String, Object>> findAss(int ab_id, String type) {
		List<Map<String, Object>> ass = new ArrayList<Map<String, Object>>();
		String sql = "select * from " + type + "BillDetailAss where exists (select 1 from " + type
				+ "BillDetail where dass_condid=abd_id and abd_abid=?) order by dass_id";
		SqlRowList rs = baseDao.queryForRowSet(sql, ab_id);
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("dass_id", rs.getGeneralInt("dass_id"));
			map.put("dass_condid", rs.getGeneralInt("dass_condid"));
			map.put("dass_asstype", rs.getGeneralString("dass_asstype"));
			map.put("dass_assname", rs.getGeneralString("dass_assname"));
			map.put("dass_codefield", rs.getGeneralString("dass_codefield"));
			map.put("dass_namefield", rs.getGeneralString("dass_namefield"));
			ass.add(map);
		}
		return ass;
	}

	@Override
	public String vastCheckARBill(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Integer abid = 0;
		String ids = CollectionUtil.pluckSqlString(maps, "ab_id");
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"SELECT LOB_CONCAT(ab_code) FROM arbill where ab_id in(" + ids
						+ ") and ab_vouchercode is not null and ab_vouchercode<>'UNNEED'", String.class);
		if (dets != null) {
			BaseUtil.showError("发票已制作凭证，不允许确认对账！" + dets);
		}
		int count = baseDao.getCount("select count(distinct to_char(ab_date,'yyyymm')) from arbill where ab_id in (" + ids + ")");
		if (count > 1) {
			BaseUtil.showError("勾选的应收发票要在同一个月！");
		}
		Object yearmonth = baseDao.getFieldDataByCondition("arbill", "max(to_char(ab_date,'yyyymm'))", "ab_id in (" + ids + ")");
		Object maxno = baseDao.getFieldDataByCondition("arbill", "max(nvl(ab_checkcounts,0))", "to_char(ab_date,'yyyymm')=" + yearmonth);
		maxno = maxno == null ? 0 : maxno;
		int detno = Integer.parseInt(maxno.toString()) + 1;
		for (Map<Object, Object> m : maps) {
			abid = Integer.parseInt(m.get("ab_id").toString());
			baseDao.execute("update arbill set ab_confirmstatus='已对账', ab_checkcounts=" + detno + " where ab_id=" + abid);
			baseDao.logger.others("确认对账", "确认成功", "ARBill!IRMA", "ab_id", abid);
		}
		return "对账成功";
	}

	@Override
	public String vastResCheckARBill(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String ids = CollectionUtil.pluckSqlString(maps, "ab_id");
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"SELECT LOB_CONCAT(ab_code) FROM arbill where ab_id in(" + ids
						+ ") and ab_vouchercode is not null and ab_vouchercode<>'UNNEED'", String.class);
		if (dets != null) {
			BaseUtil.showError("发票已制作凭证，不允许取消对账！" + dets);
		}
		Integer abid = 0;
		for (Map<Object, Object> m : maps) {
			abid = Integer.parseInt(m.get("ab_id").toString());
			baseDao.execute("update arbill set ab_confirmstatus=null, ab_checkcounts=0 where ab_id=" + abid);
			baseDao.logger.others("取消对账", "取消成功", "ARBill!IRMA", "ab_id", abid);
		}
		return "取消对账成功";
	}
}
