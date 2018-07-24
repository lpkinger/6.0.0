package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.service.scm.PriceBatchUpdateService;

@Service("priceBatchUpdateService")
public class PriceBatchUpdateServiceImpl implements PriceBatchUpdateService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void cleanFailed(int emid, String caller) {
		baseDao.execute("delete from PRICEBATCHUPDATE where pbu_emid=" + emid + " and nvl(pbu_error,' ')<>' '");
		baseDao.execute("delete from UPDATEMAINFORM where em_id=" + emid
				+ " and not exists (select 1 from PRICEBATCHUPDATE where pbu_emid=em_id)");
	}

	@Override
	public void delete(int emid, String caller) {
		int count = baseDao.getCount("select count(1) from PRICEBATCHUPDATE where nvl(pbu_status,0)>0 and pbu_emid=" + emid);
		if (count > 0) {
			BaseUtil.showError("明细行有已更新数据，单据不允许删除！");
		}
		// 删除
		baseDao.deleteById("UPDATEMAINFORM", "em_id", emid);
		baseDao.execute("delete from PRICEBATCHUPDATE where pbu_emid=" + emid);
		// 记录操作
		baseDao.logger.delete(caller, "em_id", emid);
	}

	@Override
	public void updatePriceBatchById(String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		Object emid = store.get("em_id");
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "PRICEBATCHUPDATE", "pbu_id");
		StringBuffer sb = new StringBuffer();
		Employee employee = SystemSession.getUser();
		for (Map<Object, Object> s : gstore) {
			Object piclass = s.get("pbu_piclass");
			Object[] pd = baseDao.getFieldsDataByCondition("Prodiodetail", new String[] { "nvl(pd_id,0)", "nvl(pd_sendprice,0)",
					"nvl(pd_orderprice,0)", "nvl(pd_taxrate,0)" }, "pd_piclass='" + piclass + "' and pd_inoutno='" + s.get("pbu_inoutno")
					+ "' and pd_pdno=" + s.get("pbu_pdno"));
			if (pd == null || Integer.parseInt(pd[0].toString()) == 0) {
				sb.append(piclass + "号[" + s.get("pbu_inoutno") + "]行号[" + s.get("pbu_pdno") + "]不存在!<br>");
			} else {
				if (s.get("pbu_id") == null || s.get("pbu_id").equals("") || s.get("pbu_id").toString().equals("0")) {// 新添加的数据，id不存在
					int id = baseDao.getSeqId("PRICEBATCHUPDATE_SEQ");
					s.put("pbu_pdid", pd[0]);
					s.put("pbu_emid", emid);
					s.put("pbu_emcode", employee.getEm_code());
					s.put("pbu_emname", employee.getEm_name());
					s.put("pbu_oldrate", pd[3]);
					if ("出货单".equals(piclass) || "销售退货单".equals(piclass)) {
						s.put("pbu_oldprice", pd[1]);
					} else {
						s.put("pbu_oldprice", pd[2]);
					}
					String sql = SqlUtil.getInsertSqlByMap(s, "PRICEBATCHUPDATE", new String[] { "pbu_id" }, new Object[] { id });
					gridSql.add(sql);
				}
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
		baseDao.execute(gridSql);
		baseDao.execute("update PRICEBATCHUPDATE set pbu_pdid=(select pd_id from prodiodetail where pd_inoutno=pbu_inoutno and pd_pdno=pbu_pdno and pd_piclass=pbu_piclass) where pbu_emid="
				+ emid);
		baseDao.logger.update(caller, "em_id", store.get("em_id"));
	}

	@Override
	public void batchUpdateBill(int em_id) {
		int yearmonth = 0;
		boolean useBillOutAP = baseDao.isDBSetting("useBillOutAP");
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(pbu_piclass||'号['||pbu_inoutno||']行号['||pbu_pdno||']重复') from (select pbu_piclass,pbu_inoutno,pbu_pdno, count(1) c from PRICEBATCHUPDATE where pbu_emid=? and nvl(pbu_status,0)=0 and nvl(pbu_error,' ')=' ' group by pbu_piclass,pbu_inoutno,pbu_pdno) where c>1",
						String.class, em_id);
		if (dets != null) {
			BaseUtil.showError(dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(pbu_piclass||'号['||pbu_inoutno||']行号['||pbu_pdno||']不存在') from (select pbu_piclass,pbu_inoutno,pbu_pdno from PRICEBATCHUPDATE where pbu_emid=? and nvl(pbu_status,0)=0 and nvl(pbu_error,' ')=' ' and not exists (select 1 from prodiodetail where pbu_piclass=pd_piclass and pbu_inoutno=pd_inoutno and pbu_pdno=pd_pdno))",
						String.class, em_id);
		if (dets != null) {
			BaseUtil.showError(dets);
		}
		SqlRowList rs = baseDao.queryForRowSet("select * from PRICEBATCHUPDATE where nvl(pbu_status,0)=0 and pbu_emid=" + em_id);
		while (rs.next()) {
			Long pdid = rs.getGeneralLong("pbu_pdid");
			int pbu_id = rs.getGeneralInt("pbu_id");
			String piclass = rs.getString("pbu_piclass");
			String piinoutno = rs.getString("pbu_inoutno");
			String error = null;
			SqlRowList rs1 = null;
			int count = 0;
			baseDao.execute("update PRICEBATCHUPDATE set pbu_error=null where pbu_id=" + pbu_id);
			if (useBillOutAP) {
				rs1 = baseDao
						.queryForRowSet("select distinct to_char(pi_date,'yyyymm') ym from prodinout,prodiodetail where pi_id=pd_piid and pd_id="
								+ pdid);
				if (rs1.next()) {
					yearmonth = rs1.getGeneralInt("ym");
					count = baseDao.getCount("select count(*) from periodsdetail where pd_code='MONTH-P' and pd_detno=" + yearmonth
							+ " and pd_status>0");
					if (count > 0) {
						error = "明细出入库单日期所属库存期间" + yearmonth + "已结账";
						baseDao.execute("update PRICEBATCHUPDATE set pbu_error='" + error + "' where pbu_id=" + pbu_id);
						continue;
					}
				}
				rs1 = baseDao
						.queryForRowSet(
								"select distinct to_char(ab_date,'yyyymm') ym, ab_code code from apbill left join apbilldetail on ab_id=abd_abid where abd_pdid=?",
								pdid);
				if (rs1.next()) {
					yearmonth = rs1.getGeneralInt("ym");
					count = baseDao.getCount("select count(*) from periodsdetail where pd_code='MONTH-V' and pd_detno=" + yearmonth
							+ " and pd_status>0");
					if (count > 0) {
						error = "关联的应付发票[" + rs1.getObject("code") + "]所属应付期间" + yearmonth + "已结账";
						baseDao.execute("update PRICEBATCHUPDATE set pbu_error='" + error + "' where pbu_id=" + pbu_id);
						continue;
					}
				}
				rs1 = baseDao
						.queryForRowSet(
								"select distinct ab_code code from apbill left join apbilldetail on ab_id=abd_abid where abd_pdid=? and nvl(ab_vouchercode,' ')<>' ' and nvl(ab_vouchercode,' ')<>'UNNEED'",
								pdid);
				if (rs1.next()) {
					error = "关联的应付发票[" + rs1.getObject("code") + "]已制作凭证";
					baseDao.execute("update PRICEBATCHUPDATE set pbu_error='" + error + "' where pbu_id=" + pbu_id);
					continue;
				}
				rs1 = baseDao
						.queryForRowSet(
								"select distinct abd_code code from apbilldetail where abd_pdid=? and exists (select 1 from Paypleasedetaildet where abd_code=ppdd_billcode)",
								pdid);
				if (rs1.next()) {
					error = "关联的应付发票[" + rs1.getObject("code") + "]出现在付款申请中";
					baseDao.execute("update PRICEBATCHUPDATE set pbu_error='" + error + "' where pbu_id=" + pbu_id);
					continue;
				}
				rs1 = baseDao
						.queryForRowSet(
								"select distinct abd_code code from apbilldetail where abd_pdid=? and exists (select 1 from PayBalanceDetail where abd_code=pbd_ordercode)",
								pdid);
				if (rs1.next()) {
					error = "关联的应付发票[" + rs1.getObject("code") + "]出现在付款类单据中";
					baseDao.execute("update PRICEBATCHUPDATE set pbu_error='" + error + "' where pbu_id=" + pbu_id);
					continue;
				}
				rs1 = baseDao
						.queryForRowSet(
								"select distinct abd_code code from apbilldetail where abd_pdid=? and exists (select 1 from billoutapdetail where abd_code=ard_ordercode and abd_detno=ard_orderdetno)",
								pdid);
				if (rs1.next()) {
					error = "关联的应付发票[" + rs1.getObject("code") + "]出现在开票记录中";
					baseDao.execute("update PRICEBATCHUPDATE set pbu_error='" + error + "' where pbu_id=" + pbu_id);
					continue;
				}
			} else {
				rs1 = baseDao
						.queryForRowSet("select distinct to_char(pi_date,'yyyymm') ym from prodinout,prodiodetail where pi_id=pd_piid and pd_id="
								+ pdid);
				if (rs1.next()) {
					yearmonth = rs1.getGeneralInt("ym");
					count = baseDao.getCount("select count(*) from periodsdetail where pd_code='MONTH-P' and pd_detno=" + yearmonth
							+ " and pd_status>0");
					if (count > 0) {
						error = "明细出入库单日期所属库存期间" + yearmonth + "已结账";
						baseDao.execute("update PRICEBATCHUPDATE set pbu_error='" + error + "' where pbu_id=" + pbu_id);
						continue;
					}
				}
				rs1 = baseDao
						.queryForRowSet(
								"select distinct abd_code code from apbilldetail where abd_pdid=? and nvl(abd_sourcekind,' ')='PRODIODETAIL'",
								pdid);
				if (rs1.next()) {
					error = "有关联的应付发票[" + rs1.getObject("code") + "]";
					baseDao.execute("update PRICEBATCHUPDATE set pbu_error='" + error + "' where pbu_id=" + pbu_id);
					continue;
				}
				rs1 = baseDao
						.queryForRowSet(
								"select distinct es_code code from estimate left join estimatedetail on es_id=esd_esid where esd_picode=? and esd_pdno=?",
								piinoutno, rs.getGeneralInt("pbu_pdno"));
				if (rs1.next()) {
					error = "有关联的应付暂估[" + rs1.getObject("code") + "]";
					baseDao.execute("update PRICEBATCHUPDATE set pbu_error='" + error + "' where pbu_id=" + pbu_id);
					continue;
				}
			}
			rs1 = baseDao
					.queryForRowSet(
							"select distinct ac_code code from apcheck left join apcheckdetail on ac_id=ad_acid where AD_PDID=? and nvl(ac_confirmstatus,' ')<>'不同意'",
							pdid);
			if (rs1.next()) {
				error = piclass + "[" + piinoutno + "]出现在应付对账单中";
				baseDao.execute("update PRICEBATCHUPDATE set pbu_error='" + error + "' where pbu_id=" + pbu_id);
				continue;
			}
			count = baseDao.getCount("select count(1) from PRICEBATCHUPDATE where pbu_id=" + pbu_id
					+ " and nvl(pbu_error,' ')=' ' AND nvl(pbu_status,0)=0");
			if (count > 0) {
				baseDao.execute("update prodiodetail set (pd_taxrate,pd_orderprice)=(select pbu_taxrate,pbu_orderprice from PRICEBATCHUPDATE where pbu_pdid=pd_id and pbu_id="
						+ pbu_id + ") where pd_id=" + pdid);
				baseDao.execute("update prodiodetail set pd_ordertotal=round(nvl(pd_orderprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2), pd_total=round(nvl(pd_price,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2),pd_taxtotal=round(pd_orderprice*(nvl(pd_inqty,0)+nvl(pd_outqty,0))*nvl(pd_taxrate,0)/(100+nvl(pd_taxrate,0)),2) where pd_id="
						+ pdid);
				baseDao.execute("update ProdInOut set pi_total=nvl((select round(sum(pd_ordertotal),2) from prodiodetail where pd_piid=pi_id),0) where pi_id=(select pd_piid from prodiodetail where pd_id="
						+ pdid + ")");
				baseDao.execute("update ProdInOut set pi_totalupper=L2U(nvl(pi_total,0)) where pi_id=(select pd_piid from prodiodetail where pd_id="
						+ pdid + ")");
				if (rs.getGeneralDouble("pbu_customprice") > 0) {
					baseDao.execute("update prodiodetail set pd_customprice=(select pbu_customprice from PRICEBATCHUPDATE where pbu_pdid=pd_id and pbu_id="
							+ pbu_id + ") where pd_id=" + pdid);
				}
				if (useBillOutAP) {
					baseDao.execute("update apbilldetail set (abd_price,abd_thisvoprice,abd_taxrate)=(select pd_orderprice,pd_orderprice,pd_taxrate from prodiodetail where pd_id=abd_pdid) "
							+ "where exists (select 1 from PRICEBATCHUPDATE where pbu_pdid=abd_pdid and pbu_id=" + pbu_id + ")");
					baseDao.execute("update apbilldetail set abd_amount=round(abd_thisvoprice*abd_qty,2),abd_apamount=round(abd_thisvoprice*abd_qty,2),abd_taxamount=round(abd_thisvoprice*abd_qty*abd_taxrate/(100+abd_taxrate),2),abd_noaramount=round(abd_thisvoprice*abd_qty/(1+abd_taxrate/100),2) "
							+ "where exists (select 1 from PRICEBATCHUPDATE where pbu_pdid=abd_pdid and pbu_id=" + pbu_id + ")");
					baseDao.execute("update apbill set ab_apamount=nvl((select round(sum(abd_apamount),2) from apbilldetail where ab_id=abd_abid),0) where exists (select 1 from apbilldetail,PRICEBATCHUPDATE where abd_pdid=pbu_pdid and ab_id=abd_abid and pbu_id="
							+ pbu_id + " )");
					baseDao.execute("update apbill set ab_taxsum=(select sum(round(((abd_thisvoprice*abd_qty*abd_taxrate/100)/(1+abd_taxrate/100)),2)) from apbilldetail where abd_abid=ab_id)+nvl(ab_differ,0) where exists (select 1 from apbilldetail,PRICEBATCHUPDATE where abd_pdid=pbu_pdid and ab_id=abd_abid and pbu_id="
							+ pbu_id + " )");
					baseDao.execute("update apbill set ab_paystatuscode='UNPAYMENT',ab_paystatus='未付款' where nvl(Ab_PayAmount,0)=0 and nvl(ab_apamount,0)<>0 and exists (select 1 from apbilldetail,PRICEBATCHUPDATE where abd_pdid=pbu_pdid and ab_id=abd_abid and pbu_id="
							+ pbu_id + " )");
					baseDao.execute("update apbill set ab_paystatuscode='PARTPAYMENT',ab_paystatus='部分付款' where nvl(Ab_PayAmount,0)<>0 and abs(nvl(ab_apamount,0))>abs(nvl(Ab_PayAmount,0)) and exists (select 1 from apbilldetail,PRICEBATCHUPDATE where abd_pdid=pbu_pdid and ab_id=abd_abid and pbu_id="
							+ pbu_id + " )");
					baseDao.execute("update apbill set ab_paystatuscode='FULLPAYMENT',ab_paystatus='已付款' where nvl(Ab_PayAmount,0)<>0 and abs(nvl(ab_apamount,0))=abs(nvl(Ab_PayAmount,0)) and exists (select 1 from apbilldetail,PRICEBATCHUPDATE where abd_pdid=pbu_pdid and ab_id=abd_abid and pbu_id="
							+ pbu_id + " )");
				}
				baseDao.execute("update PRICEBATCHUPDATE set pbu_status=99, pbu_updatedate=sysdate,pbu_sendstatus='待上传' where pbu_id="
						+ pbu_id);
				baseDao.logger.others("更新明细数据，行[" + rs.getGeneralInt("pbu_detno") + "]", "更新成功", "PriceBatch", "em_id", em_id);
			}
		}
		int fail = baseDao.getCountByCondition("PRICEBATCHUPDATE", "nvl(pbu_status,0)=0 and pbu_emid=" + em_id);
		int succ = baseDao.getCountByCondition("PRICEBATCHUPDATE", "pbu_status=99 and pbu_emid=" + em_id);
		BaseUtil.appendError("更新成功" + succ + "条，失败" + fail + "条，详情见【错误信息】列");
	}

	@Override
	public void batchUpdateOutBill(int em_id) {
		int yearmonth = 0;
		boolean useBillOutAR = baseDao.isDBSetting("autoCreateArBill");
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(pbu_piclass||'号['||pbu_inoutno||']行号['||pbu_pdno||']重复') from (select pbu_piclass,pbu_inoutno,pbu_pdno, count(1) c from PRICEBATCHUPDATE where pbu_emid=? and nvl(pbu_status,0)=0 and nvl(pbu_error,' ')=' ' group by pbu_piclass,pbu_inoutno,pbu_pdno) where c>1",
						String.class, em_id);
		if (dets != null) {
			BaseUtil.showError(dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(pbu_piclass||'号['||pbu_inoutno||']行号['||pbu_pdno||']不存在') from (select pbu_piclass,pbu_inoutno,pbu_pdno from PRICEBATCHUPDATE where pbu_emid=? and nvl(pbu_status,0)=0 and nvl(pbu_error,' ')=' ' and not exists (select 1 from prodiodetail where pbu_piclass=pd_piclass and pbu_inoutno=pd_inoutno and pbu_pdno=pd_pdno))",
						String.class, em_id);
		if (dets != null) {
			BaseUtil.showError(dets);
		}
		SqlRowList rs = baseDao.queryForRowSet("select * from PRICEBATCHUPDATE where nvl(pbu_status,0)=0 and pbu_emid=" + em_id);
		while (rs.next()) {
			Long pdid = rs.getGeneralLong("pbu_pdid");
			int pbu_id = rs.getGeneralInt("pbu_id");
			String piclass = rs.getString("pbu_piclass");
			String piinoutno = rs.getString("pbu_inoutno");
			String error = null;
			SqlRowList rs1 = null;
			int count = 0;
			baseDao.execute("update PRICEBATCHUPDATE set pbu_error=null where pbu_id=" + pbu_id);
			if (useBillOutAR) {
				rs1 = baseDao
						.queryForRowSet("select distinct to_char(pi_date,'yyyymm') ym from prodinout,prodiodetail where pi_id=pd_piid and pd_id="
								+ pdid);
				if (rs1.next()) {
					yearmonth = rs1.getGeneralInt("ym");
					count = baseDao.getCount("select count(*) from periodsdetail where pd_code='MONTH-P' and pd_detno=" + yearmonth
							+ " and pd_status>0");
					if (count > 0) {
						error = "明细出入库单日期所属库存期间" + yearmonth + "已结账";
						baseDao.execute("update PRICEBATCHUPDATE set pbu_error='" + error + "' where pbu_id=" + pbu_id);
						continue;
					}
				}
				rs1 = baseDao
						.queryForRowSet(
								"select distinct to_char(ab_date,'yyyymm') ym, ab_code code from arbill left join arbilldetail on ab_id=abd_abid where abd_pdid=?",
								pdid);
				if (rs1.next()) {
					yearmonth = rs1.getGeneralInt("ym");
					count = baseDao.getCount("select count(*) from periodsdetail where pd_code='MONTH-C' and pd_detno=" + yearmonth
							+ " and pd_status>0");
					if (count > 0) {
						error = "关联的应收发票[" + rs1.getObject("code") + "]所属应收期间" + yearmonth + "已结账";
						baseDao.execute("update PRICEBATCHUPDATE set pbu_error='" + error + "' where pbu_id=" + pbu_id);
						continue;
					}
				}
				rs1 = baseDao
						.queryForRowSet(
								"select distinct ab_code code from arbill left join arbilldetail on ab_id=abd_abid where abd_pdid=? and nvl(ab_vouchercode,' ')<>' ' and nvl(ab_vouchercode,' ')<>'UNNEED'",
								pdid);
				if (rs1.next()) {
					error = "关联的应收发票[" + rs1.getObject("code") + "]已制作凭证";
					baseDao.execute("update PRICEBATCHUPDATE set pbu_error='" + error + "' where pbu_id=" + pbu_id);
					continue;
				}
				rs1 = baseDao
						.queryForRowSet(
								"select distinct abd_code code from arbilldetail where abd_pdid=? and exists (select 1 from RecBalanceNoticeDetail where abd_code=rbd_abcode)",
								pdid);
				if (rs1.next()) {
					error = "关联的应收发票[" + rs1.getObject("code") + "]出现在回款通知单中";
					baseDao.execute("update PRICEBATCHUPDATE set pbu_error='" + error + "' where pbu_id=" + pbu_id);
					continue;
				}
				rs1 = baseDao
						.queryForRowSet(
								"select distinct abd_code code from arbilldetail where abd_pdid=? and exists (select 1 from RecBalanceDetail where abd_code=rbd_ordercode)",
								pdid);
				if (rs1.next()) {
					error = "关联的应收发票[" + rs1.getObject("code") + "]出现在收款类单据中";
					baseDao.execute("update PRICEBATCHUPDATE set pbu_error='" + error + "' where pbu_id=" + pbu_id);
					continue;
				}
				rs1 = baseDao
						.queryForRowSet(
								"select distinct abd_code code from arbilldetail where abd_pdid=? and exists (select 1 from billoutdetail where abd_code=ard_ordercode and abd_detno=ard_orderdetno)",
								pdid);
				if (rs1.next()) {
					error = "关联的应收发票[" + rs1.getObject("code") + "]出现在开票记录中";
					baseDao.execute("update PRICEBATCHUPDATE set pbu_error='" + error + "' where pbu_id=" + pbu_id);
					continue;
				}
			} else {
				rs1 = baseDao
						.queryForRowSet("select distinct to_char(pi_date,'yyyymm') ym from prodinout,prodiodetail where pi_id=pd_piid and pd_id="
								+ pdid);
				if (rs1.next()) {
					yearmonth = rs1.getGeneralInt("ym");
					count = baseDao.getCount("select count(*) from periodsdetail where pd_code='MONTH-P' and pd_detno=" + yearmonth
							+ " and pd_status>0");
					if (count > 0) {
						error = "明细出入库单日期所属库存期间" + yearmonth + "已结账";
						baseDao.execute("update PRICEBATCHUPDATE set pbu_error='" + error + "' where pbu_id=" + pbu_id);
						continue;
					}
				}
				rs1 = baseDao
						.queryForRowSet(
								"select distinct abd_code code from arbilldetail where abd_pdid=? and nvl(abd_sourcekind,' ')='PRODIODETAIL'",
								pdid);
				if (rs1.next()) {
					error = "有关联的应收发票[" + rs1.getObject("code") + "]";
					baseDao.execute("update PRICEBATCHUPDATE set pbu_error='" + error + "' where pbu_id=" + pbu_id);
					continue;
				}
				rs1 = baseDao
						.queryForRowSet(
								"select distinct gs_code code from GoodsSend left join GoodsSendDetail on gsd_gsid=gs_id left join prodiodetail on pd_id=gsd_pdid where gsd_picode=? and pd_pdno=?",
								piinoutno, rs.getGeneralInt("pbu_pdno"));
				if (rs1.next()) {
					error = "有关联的发出商品[" + rs1.getObject("code") + "]";
					baseDao.execute("update PRICEBATCHUPDATE set pbu_error='" + error + "' where pbu_id=" + pbu_id);
					continue;
				}
			}
			rs1 = baseDao
					.queryForRowSet(
							"select distinct ac_code code from archeck left join archeckdetail on ac_id=ad_acid where AD_PDID=? and nvl(ac_confirmstatus,' ')<>'不同意'",
							pdid);
			if (rs1.next()) {
				error = piclass + "[" + piinoutno + "]出现在应收对账单中";
				baseDao.execute("update PRICEBATCHUPDATE set pbu_error='" + error + "' where pbu_id=" + pbu_id);
				continue;
			}
			count = baseDao.getCount("select count(1) from PRICEBATCHUPDATE where pbu_id=" + pbu_id
					+ " and nvl(pbu_error,' ')=' ' AND nvl(pbu_status,0)=0");
			if (count > 0) {
				baseDao.execute("update prodiodetail set (pd_taxrate,pd_sendprice)=(select pbu_taxrate,pbu_sendprice from PRICEBATCHUPDATE where pbu_pdid=pd_id and pbu_id="
						+ pbu_id + ") where pd_id=" + pdid);
				baseDao.execute("update prodiodetail set pd_ordertotal=round(nvl(pd_sendprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2), pd_total=round(nvl(pd_price,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2),pd_taxtotal=round(pd_sendprice*(nvl(pd_inqty,0)+nvl(pd_outqty,0))*nvl(pd_taxrate,0)/(100+nvl(pd_taxrate,0)),2) where pd_id="
						+ pdid);
				baseDao.execute("update ProdInOut set pi_total=nvl((select round(sum(pd_ordertotal),2) from prodiodetail where pd_piid=pi_id),0) where pi_id=(select pd_piid from prodiodetail where pd_id="
						+ pdid + ")");
				baseDao.execute("update ProdInOut set pi_totalupper=L2U(nvl(pi_total,0)) where pi_id=(select pd_piid from prodiodetail where pd_id="
						+ pdid + ")");
				if (rs.getGeneralDouble("pbu_customprice") > 0) {
					baseDao.execute("update prodiodetail set pd_customprice=(select pbu_customprice from PRICEBATCHUPDATE where pbu_pdid=pd_id and pbu_id="
							+ pbu_id + ") where pd_id=" + pdid);
				}
				if (useBillOutAR) {
					baseDao.execute("update arbilldetail set (abd_price,abd_thisvoprice,abd_taxrate)=(select pd_sendprice,pd_sendprice,pd_taxrate from prodiodetail where pd_id=abd_pdid) "
							+ "where exists (select 1 from PRICEBATCHUPDATE where pbu_pdid=abd_pdid and pbu_id=" + pbu_id + ")");
					baseDao.execute("update arbilldetail set abd_amount=round(abd_thisvoprice*abd_qty,2),abd_aramount=round(abd_thisvoprice*abd_qty,2),abd_taxamount=round(abd_thisvoprice*abd_qty*abd_taxrate/(100+abd_taxrate),2),abd_noaramount=round(abd_thisvoprice*abd_qty/(1+abd_taxrate/100),2) "
							+ "where exists (select 1 from PRICEBATCHUPDATE where pbu_pdid=abd_pdid and pbu_id=" + pbu_id + ")");
					baseDao.execute("update arbill set ab_aramount=nvl((select round(sum(abd_aramount),2) from arbilldetail where ab_id=abd_abid),0) where exists (select 1 from arbilldetail,PRICEBATCHUPDATE where abd_pdid=pbu_pdid and ab_id=abd_abid and pbu_id="
							+ pbu_id + " )");
					baseDao.execute("update arbill set ab_taxamount=(select sum(round(((abd_thisvoprice*abd_qty*abd_taxrate/100)/(1+abd_taxrate/100)),2)) from arbilldetail where abd_abid=ab_id)+nvl(ab_differ,0) where exists (select 1 from arbilldetail,PRICEBATCHUPDATE where abd_pdid=pbu_pdid and ab_id=abd_abid and pbu_id="
							+ pbu_id + " )");
					baseDao.execute("update arbill set ab_paystatuscode='UNCOLLECT',ab_paystatus='未收款' where nvl(Ab_PayAmount,0)=0 and nvl(ab_aramount,0)<>0 and exists (select 1 from arbilldetail,PRICEBATCHUPDATE where abd_pdid=pbu_pdid and ab_id=abd_abid and pbu_id="
							+ pbu_id + " )");
					baseDao.execute("update arbill set ab_paystatuscode='PARTCOLLECT',ab_paystatus='部分收款' where nvl(Ab_PayAmount,0)<>0 and abs(nvl(ab_aramount,0))>abs(nvl(Ab_PayAmount,0)) and exists (select 1 from arbilldetail,PRICEBATCHUPDATE where abd_pdid=pbu_pdid and ab_id=abd_abid and pbu_id="
							+ pbu_id + " )");
					baseDao.execute("update arbill set ab_paystatuscode='FULLCOLLECT',ab_paystatus='已收款' where nvl(Ab_PayAmount,0)<>0 and abs(nvl(ab_aramount,0))=abs(nvl(Ab_PayAmount,0)) and exists (select 1 from arbilldetail,PRICEBATCHUPDATE where abd_pdid=pbu_pdid and ab_id=abd_abid and pbu_id="
							+ pbu_id + " )");
				}
				baseDao.execute("update PRICEBATCHUPDATE set pbu_status=99, pbu_updatedate=sysdate,pbu_sendstatus='待上传' where pbu_id="
						+ pbu_id);
				baseDao.logger.others("更新明细数据，行[" + rs.getGeneralInt("pbu_detno") + "]", "更新成功", "OutPriceBatch", "em_id", em_id);
			}
		}
		int fail = baseDao.getCountByCondition("PRICEBATCHUPDATE", "nvl(pbu_status,0)=0 and pbu_emid=" + em_id);
		int succ = baseDao.getCountByCondition("PRICEBATCHUPDATE", "pbu_status=99 and pbu_emid=" + em_id);
		BaseUtil.appendError("更新成功" + succ + "条，失败" + fail + "条，详情见【错误信息】列");
	}

	@Override
	@Transactional
	public void savePriceBatchById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "UPDATEMAINFORM"));
		StringBuffer sb = new StringBuffer();
		Employee employee = SystemSession.getUser();
		for (Map<Object, Object> s : grid) {
			Object piclass = s.get("pbu_piclass");
			Object[] pd = baseDao.getFieldsDataByCondition("Prodiodetail", new String[] { "nvl(pd_id,0)", "nvl(pd_sendprice,0)",
					"nvl(pd_orderprice,0)", "nvl(pd_taxrate,0)" }, "pd_piclass='" + piclass + "' and pd_inoutno='" + s.get("pbu_inoutno")
					+ "' and pd_pdno=" + s.get("pbu_pdno"));
			if (pd == null || Integer.parseInt(pd[0].toString()) == 0) {
				sb.append(piclass + "号[" + s.get("pbu_inoutno") + "]行号[" + s.get("pbu_pdno") + "]不存在!<br>");
			} else {
				s.put("pbu_id", baseDao.getSeqId("PRICEBATCHUPDATE_SEQ"));
				s.put("pbu_pdid", pd[0]);
				s.put("pbu_emid", store.get("em_id"));
				s.put("pbu_emcode", employee.getEm_code());
				s.put("pbu_emname", employee.getEm_name());
				s.put("pbu_oldrate", pd[3]);
				if ("出货单".equals(piclass) || "销售退货单".equals(piclass)) {
					s.put("pbu_oldprice", pd[1]);
				} else {
					s.put("pbu_oldprice", pd[2]);
				}
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "PRICEBATCHUPDATE");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "em_id", store.get("em_id"));
	}

	@Override
	public void auditPriceBatch(String caller, int em_id) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("UPDATEMAINFORM", "em_statuscode", "em_id=" + em_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { em_id });
		// 执行审核操作
		baseDao.audit("UPDATEMAINFORM", "em_id=" + em_id, "em_status", "em_statuscode", "em_auditdate", "em_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "em_id", em_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { em_id });
	}

	@Override
	public void resAuditPriceBatch(String caller, int em_id) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("UPDATEMAINFORM", "em_statuscode", "em_id=" + em_id);
		StateAssert.resAuditOnlyAudit(status);
		int count = baseDao.getCount("select count(1) from PRICEBATCHUPDATE where nvl(pbu_status,0)>0 and pbu_emid=" + em_id);
		if (count > 0) {
			BaseUtil.showError("明细行有已更新数据，单据不允许反审核！");
		}
		// 执行反审核前的其它逻辑
		handlerService.handler(caller, "resAudit", "before", new Object[] { em_id });
		// 执行反审核操作
		baseDao.resAudit("UPDATEMAINFORM", "em_id=" + em_id, "em_status", "em_statuscode", "em_auditman", "em_auditdate");
		// 记录操作
		baseDao.logger.resAudit(caller, "em_id", em_id);
		// 执行反审核后的其它逻辑
		handlerService.handler(caller, "resAudit", "after", new Object[] { em_id });
	}

	@Override
	public void submitPriceBatch(String caller, int em_id) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("UPDATEMAINFORM", "em_statuscode", "em_id=" + em_id);
		StateAssert.submitOnlyEntering(status);
		baseDao.execute("update pricebatchupdate set (pbu_oldprice,pbu_oldrate)=(select case when pd_piclass in ('出货单','销售退货单') then nvl(pd_sendprice,0) else nvl(pd_orderprice,0) end, pd_taxrate from prodiodetail where pbu_piclass=pd_piclass and pbu_inoutno=pd_inoutno and pbu_pdno=pd_pdno) where pbu_emid="
				+ em_id);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { em_id });
		// 执行提交操作
		baseDao.submit("UPDATEMAINFORM", "em_id=" + em_id, "em_status", "em_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "em_id", em_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { em_id });
	}

	@Override
	public void resSubmitPriceBatch(String caller, int em_id) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("UPDATEMAINFORM", "em_statuscode", "em_id=" + em_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { em_id });
		// 执行反提交操作
		baseDao.resOperate("UPDATEMAINFORM", "em_id=" + em_id, "em_status", "em_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "em_id", em_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { em_id });
	}
}
