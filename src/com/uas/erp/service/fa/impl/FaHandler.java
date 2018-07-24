package com.uas.erp.service.fa.impl;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hsqldb.lib.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.APBillDao;
import com.uas.erp.dao.common.APCheckDao;
import com.uas.erp.dao.common.ARCheckDao;
import com.uas.erp.dao.common.BillOutAPDao;
import com.uas.erp.dao.common.BillOutDao;
import com.uas.erp.dao.common.PayPleaseDao;
import com.uas.erp.service.fa.AutoDepreciationService;

/**
 * @author yingp
 *
 */
/**
 * @author yingp
 * 
 */
@Service("FaHandler")
public class FaHandler {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private BillOutDao billOutDao;
	@Autowired
	private BillOutAPDao billOutAPDao;
	@Autowired
	private APCheckDao APCheckDao;
	@Autowired
	private ARCheckDao ARCheckDao;
	@Autowired
	private APBillDao APBillDao;
	@Autowired
	private AutoDepreciationService autoDepreciationService;
	@Autowired
	private PayPleaseDao payPleaseDao;

	/**
	 * 应收发票维护中 删除其明细行之前进行的操作
	 * 
	 * @param condition
	 * @param language
	 * @author madan
	 */
	public void arbill_return_deletedetail(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select abd_qty, abd_sourcedetailid, abd_aramount, abd_pdinoutno, abd_sourcekind, abd_adid from arbilldetail where abd_id=?",
						id);
		if (rs.next()) {
			Object qty = rs.getGeneralDouble("abd_qty");
			int adid = rs.getGeneralInt("abd_adid");
			if (adid != 0) {
				baseDao.updateByCondition("ARCheckDetail", "ad_yqty=nvl(ad_yqty,0)-(" + qty + ")", "ad_id=" + adid);
				int ac_id = baseDao.getFieldValue("ARCheckDetail", "ad_acid", "ad_id=" + adid, Integer.class);
				ARCheckDao.updateBillStatus(ac_id);
			}
			if (!StringUtil.isEmpty(rs.getGeneralString("abd_sourcekind"))) {
				int sourceid = rs.getGeneralInt("abd_sourcedetailid");
				Object picode = rs.getObject("abd_pdinoutno");
				if ("PRODIODETAIL".equals(rs.getGeneralString("abd_sourcekind"))) {
					baseDao.updateByCondition("prodiodetail", "pd_showinvoqty=pd_showinvoqty-(" + qty + "),pd_auditstatus='PARTAR'",
							"pd_id=" + sourceid);
					baseDao.updateByCondition("prodiodetail", "pd_showinvototal=0,pd_auditstatus='AUDITED'", "pd_showinvoqty=0 and pd_id="
							+ sourceid);
					int count = baseDao.getCountByCondition("prodiodetail", "pd_inoutno='" + picode + "'");
					int nqty = baseDao.getCountByCondition("prodiodetail", "pd_showinvoqty=0 and pd_inoutno='" + picode + "'");
					String status = nqty == count ? "" : "PARTAR";
					String s = "".equals(status) ? "" : BaseUtil.getLocalMessage(status);
					baseDao.updateByCondition("prodinout", "pi_billstatus='" + s + "',pi_billstatuscode='" + status + "'", "pi_inoutno='"
							+ picode + "'");
				} else if ("GOODSSEND".equals(rs.getGeneralString("abd_sourcekind"))) {
					baseDao.updateByCondition("goodssenddetail", "gsd_showinvoqty=gsd_showinvoqty-(" + qty + "),gsd_statuscode='PARTAR'",
							"gsd_id=" + sourceid);
					baseDao.updateByCondition("goodssenddetail", "gsd_showinvototal=0,gsd_statuscode='AUDITED'",
							"gsd_showinvoqty=0 and gsd_id=" + sourceid);
					int count = baseDao.getCountByCondition("goodssenddetail", "gsd_picode='" + picode + "'");
					int nqty = baseDao.getCountByCondition("goodssenddetail", "gsd_showinvoqty=0 and gsd_picode='" + picode + "'");
					String status = nqty == count ? "" : "PARTAR";
					String s = "".equals(status) ? "" : BaseUtil.getLocalMessage(status);
					baseDao.updateByCondition("goodssend", "gs_invostatus='" + s + "',gs_invostatuscode='" + status + "'", "gs_inoutno='"
							+ picode + "'");
				}
			}
		}
	}

	/**
	 * XIONGCY
	 */

	public void prodinout_deletedetail(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("EstimateDetail", new String[] { "esd_pdid", "esd_qty" }, "esd_id=" + id);
		if (objs != null) {
			baseDao.updateByCondition("prodiodetail", "pd_turnesqty = pd_turnesqty -  " + objs[1], "pd_id =" + objs[0]);
		}
	};

	/**
	 * 删除发出商品明细，需根据来源号恢复出入库转发出商品数据
	 */

	public void goodssend_deletedetail(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("GoodsSendDetail", new String[] { "gsd_pdid", "gsd_qty" }, "gsd_id=" + id);
		if (objs != null) {
			baseDao.updateByCondition("prodiodetail", "pd_turngsqty = pd_turngsqty -  " + objs[1], "pd_id =" + objs[0]);
		}
	};

	/**
	 * 应付发票维护 删除其明细行之前进行的操作
	 * 
	 * @param condition
	 * @param language
	 * @author madan
	 */
	public void apbill_return_deletedetail(Integer id) {
		SqlRowList rs = baseDao.queryForRowSet("SELECT abd_sourcedetailid, abd_sourcekind, abd_adid from apbilldetail where abd_id=?", id);
		if (rs.next()) {
			APBillDao.apbill_return_deletedetail(id, rs.getGeneralInt("abd_sourcedetailid"), rs.getObject("abd_sourcekind"),
					rs.getGeneralInt("abd_adid"));
		}
	}

	/**
	 * fa->APBill->delete->before 应付发票删除后，还原出入库单数据
	 * 
	 * @author madan
	 */
	public void apbill_returnap_delete(Integer id) {

	}

	/**
	 * fa->ARBill->delete->before 应收发票删除后，还原出入库单数据
	 * 
	 * @author madan
	 */
	public void arbill_returnar_delete(Integer id) {
		SqlRowList rs = baseDao.queryForRowSet("SELECT abd_id from arbilldetail where abd_abid=?", id);
		while (rs.next()) {
			arbill_return_deletedetail(rs.getInt(1));
		}
	}

	/**
	 * 收款单保存之前判断发票金额的总和是否大于发票可选金额
	 * 
	 * @param store
	 *            form data
	 * @param gstore
	 *            grid date
	 * @param language
	 */
	public void recbalance_pbil_beforesave(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {

		List<String> list = new ArrayList<String>();
		for (Map<Object, Object> map : gstore) {
			Double amount = Double.parseDouble(map.get("rbd_nowbalance").toString());
			String ab_code = map.get("rbd_ordercode").toString();

			Object[] billMsg = baseDao.getFieldsDataByCondition("arbill", new String[] { "round(nvl(ab_aramount,0),2)" }, "ab_code='"
					+ ab_code + "'");

			Object[] returnMsg = baseDao.getFieldsDataByCondition("recbalancedetail", new String[] { "round(sum(rbd_nowbalance),2)" },
					"rbd_ordercode='" + ab_code + "' and rbd_rbid <> '" + store.get("rb_id") + "'");
			Double remsg = 0.0;
			if (returnMsg != null) {
				remsg = Double.parseDouble(returnMsg[0].toString());
			}
			if (NumberUtil.formatDouble(amount + remsg, 2) > Double.parseDouble(billMsg[0].toString())) {
				list.add(ab_code);
			}
		}
		if (list.size() > 0) {
			String rMsg = "发票单号:";
			for (String s : list) {
				rMsg = rMsg + s + " ";
			}
			rMsg = rMsg + "合计超过发票金额,不能保存";

			BaseUtil.showError(rMsg);
		}
	}

	/**
	 * 出入库单据凭证制作前检测
	 * 
	 * @param datas
	 *            单据
	 * @param mode
	 *            single,merge
	 * @param kind
	 *            pi_class
	 */
	public void voucherCreate_prodinout(String datas, String mode, String kind) {
		if ("merge".equals(mode)) {
			datas = "SELECT pi_inoutno FROM prodinout WHERE " + datas;
		}
		// 检查存货科目
		String err = baseDao.getJdbcTemplate().queryForObject(
				"select WMSYS.WM_CONCAT(pd_inoutno||'物料'||pd_prodcode) from prodiodetail left join product on pr_code=pd_prodcode where pi_inoutno in("
						+ datas + ") and pd_piclass=? and pr_stockcatecode is null", String.class);
		if (err != null) {
			BaseUtil.showError("物料有误，或未设置存货科目:" + err);
		}
	}

	/**
	 * 工作内容申请明细提交时检测标准工作编号是否相同
	 * 
	 * @param store
	 * 
	 */
	public void empWork_commit_before_checkSetcode(Integer id) {
		String err = baseDao.getJdbcTemplate().queryForObject(
				"select ewd_wrscode from EmpWorkDetail where ewd_wrscode  is not null and EWD_EWID='" + id
						+ "' group by ewd_wrscode  having count(ewd_wrscode)>1", String.class);
		if (err != null) {
			BaseUtil.showError("明细行的标准工作内容编号不能相同！");
		}
	}

	/**
	 * 其它出入库单凭证制作 检查科目是否设置
	 * 
	 * @param datas
	 * @param mode
	 * @param kind
	 */
	public void voucherCreate_cateSet(String datas, String mode, String kind) {
		if ("其它入库单".equals(kind) || "其它出库单".equals(kind)) {
			if ("merge".equals(mode)) {
				datas = "SELECT pi_inoutno FROM prodinout WHERE " + datas;
			}
			String err = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select WMSYS.WM_CONCAT(pi_inoutno) from prodinout where pi_inoutno in("
									+ datas
									+ ") and (pi_class,pi_type,pi_departmentcode) not in (select pc_class,pc_type,pc_departmentcode from prodiocateset)",
							String.class);
			if (err != null) {
				BaseUtil.showError("其它出入库科目未设置，单号:" + err);
			}
		}
	}

	/**
	 * fa->arbill->commit->before
	 * 
	 * @author madan 应收发票提交之前：判断收款方式编号是否存在
	 */
	public void arbill_commit_before_paymentcheck(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("ARBill left join customer on ab_custcode=cu_code", new String[] {
				"ab_paymentcode", "cu_id" }, "ab_id=" + id);
		int count = baseDao.getCountByCondition("Payments", "pa_code = '" + objs[0] + "'");
		if (count == 0) {
			count = baseDao.getCountByCondition("CustomerPayments", "cp_cuid=" + objs[1] + " and cp_paymentcode='" + objs[0] + "'");
			if (count == 0) {
				// 收款方式不存在，不允许提交
				BaseUtil.showError("收款方式不存在，不允许提交!");
			}
		}
	}

	/**
	 * fa->billar->audit->after
	 * 
	 * @author XIONGCY 应收票据审核后更新 收款委托书 实际收款金额，差异金额和 收款状态
	 */
	public void billar_audit_after_updatedelegation(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("BillAR left join category on bar_othercatecode=ca_code", new String[] {
				"bar_custcode", "bar_sellercode", "bar_doublebalance" }, "bar_id=" + id);
		// 更新 收款委托书 实际收款金额和 差异金额
		String sql = "update DelegationLetter set dgl_acutalamount=nvl(dgl_acutalamount,0)+" + objs[2] + " where dgl_receivecustcode='"
				+ objs[0] + "' and dgl_sellercode='" + objs[1]
				+ "' and dgl_receivestatuscode in ('UNCOLLECT','PARTCOLLECT') and nvl(dgl_statuscode,' ')<>'FINISH' ";
		baseDao.execute(sql);
		String sqls = "update DelegationLetter set dgl_difference=dgl_acutalamount-dgl_parvalue where dgl_receivecustcode='" + objs[0]
				+ "' and dgl_sellercode='" + objs[1]
				+ "' and dgl_receivestatuscode in ('UNCOLLECT','PARTCOLLECT') and nvl(dgl_statuscode,' ')<>'FINISH' ";
		baseDao.execute(sqls);
		// 更新 收款状态
		String sql1 = "update DelegationLetter set dgl_receivestatuscode='COLLECTED',dgl_receivestatus='"
				+ BaseUtil.getLocalMessage("COLLECTED") + "' where  nvl(dgl_acutalamount,0)>0.95*dgl_parvalue and dgl_receivecustcode='"
				+ objs[0] + "' and dgl_sellercode='" + objs[1]
				+ "' and dgl_receivestatuscode in ('UNCOLLECT','PARTCOLLECT') and nvl(dgl_statuscode,' ')<>'FINISH' ";
		baseDao.execute(sql1);
		// 更新 收款状态
		String sql2 = "update DelegationLetter set dgl_receivestatuscode='PARTCOLLECT',dgl_receivestatus='"
				+ BaseUtil.getLocalMessage("PARTCOLLECT")
				+ "' where  nvl(dgl_acutalamount,0)<0.95*dgl_parvalue and nvl(dgl_acutalamount,0)>0 and dgl_receivecustcode='" + objs[0]
				+ "' and dgl_sellercode='" + objs[1]
				+ "' and dgl_receivestatuscode in ('UNCOLLECT','PARTCOLLECT') and nvl(dgl_statuscode,' ')<>'FINISH' ";
		baseDao.execute(sql2);

	}

	/**
	 * fa->AccountRegister!Bank->account->after
	 * 
	 * @author XIONGCY 银行登记过账后更新 收款委托书 实际收款金额，差异金额和 收款状态
	 */
	public void accountregister_account_after_updatedelegation(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("AccountRegister left join Category on ar_accountcode=ca_code", new String[] {
				"ar_custcode", "ar_sellercode", "ar_aramount", "ar_type" }, "ar_id=" + id);
		if (objs[3].equals("应收款") || objs[3].equals("预收款")) {
			// 更新 收款委托书 实际收款金额和 差异金额
			String sql = "update DelegationLetter set dgl_acutalamount=nvl(dgl_acutalamount,0)+" + objs[2] + " where dgl_receivecustcode='"
					+ objs[0] + "' and dgl_sellercode='" + objs[1]
					+ "' and dgl_receivestatuscode in ('UNCOLLECT','PARTCOLLECT') and nvl(dgl_statuscode,' ')<>'FINISH' ";
			baseDao.execute(sql);
			String sqls = "update DelegationLetter set dgl_difference=dgl_acutalamount-dgl_parvalue where dgl_receivecustcode='" + objs[0]
					+ "' and dgl_sellercode='" + objs[1]
					+ "' and dgl_receivestatuscode in ('UNCOLLECT','PARTCOLLECT') and nvl(dgl_statuscode,' ')<>'FINISH' ";
			baseDao.execute(sqls);
			// 更新 收款状态
			String sql1 = "update DelegationLetter set dgl_receivestatuscode='COLLECTED',dgl_receivestatus='"
					+ BaseUtil.getLocalMessage("COLLECTED")
					+ "' where  nvl(dgl_acutalamount,0)>0.95*dgl_parvalue and dgl_receivecustcode='" + objs[0] + "' and dgl_sellercode='"
					+ objs[1] + "' and dgl_receivestatuscode in ('UNCOLLECT','PARTCOLLECT')  and nvl(dgl_statuscode,' ')<>'FINISH' ";
			baseDao.execute(sql1);
			// 更新 收款状态
			String sql2 = "update DelegationLetter set dgl_receivestatuscode='PARTCOLLECT',dgl_receivestatus='"
					+ BaseUtil.getLocalMessage("PARTCOLLECT")
					+ "' where  nvl(dgl_acutalamount,0)<0.95*dgl_parvalue and nvl(dgl_acutalamount,0)>0 and dgl_receivecustcode='"
					+ objs[0] + "' and dgl_sellercode='" + objs[1]
					+ "' and dgl_receivestatuscode in ('UNCOLLECT','PARTCOLLECT') and nvl(dgl_statuscode,' ')<>'FINISH' ";
			baseDao.execute(sql2);
		}

	}

	/**
	 * fa->payplease->save->after
	 * 
	 * @author madan 付款申请保存之后：更新前期应付款金额 ppd_beginamount (恒晨应付申请)
	 */
	public void payplease_save_after_updatebegin(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore,
			ArrayList<Map<Object, Object>> gstor2) {

		List<Object[]> list = baseDao.getFieldsDatasByCondition("paypleasedetaildet t left join paypleasedetail on ppd_id=ppdd_ppdid",
				new String[] { "ppdd_ppdid", "to_char(min(ppdd_billdate),'yyyy-mm-dd')", "max(ppd_vendcode)", "max(ppd_currency)" },
				"ppd_ppid='" + store.get("pp_id") + "' group by ppdd_ppdid");

		for (Object[] os : list) {

			baseDao.execute("update paypleasedetail set ppd_beginamount = "
					+ "(select sum(ab_apamount-ab_payamount) from apbill where ab_date < to_date('" + os[1].toString()
					+ "', 'yyyy-mm-dd') and abs(ab_apamount-ab_payamount) > 0 and ab_vendcode='" + os[2].toString() + "' and ab_currency='"
					+ os[3].toString() + "' and ab_statuscode ='POSTED') " + "where ppd_id=" + os[0].toString() + "");
		}

	}

	/**
	 * fa->payplease!YF->save->after
	 * 
	 * @author madan 付款申请保存之后：更新前期应付款金额 ppd_beginamount (恒晨预付申请)
	 */
	public void payplease_save_after_updatebeginPre(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore,
			ArrayList<Map<Object, Object>> gstor2) {

		List<Object[]> list = baseDao.getFieldsDatasByCondition("paypleasedetaildet t left join paypleasedetail on ppd_id=ppdd_ppdid",
				new String[] { "ppdd_ppdid", "to_char(min(ppdd_billdate),'yyyy-mm-dd')", "max(ppd_vendcode)", "max(ppd_currency)" },
				"ppd_ppid='" + store.get("pp_id") + "' group by ppdd_ppdid");

		for (Object[] os : list) {

			baseDao.execute("update paypleasedetail set ppd_beginamount = "
					+ "(select sum(pp_jsamount-pp_vmamount) from prepay where pp_date < to_date('" + os[1].toString()
					+ "', 'yyyy-mm-dd') and abs(pp_jsamount-pp_vmamount) > 0 and pp_vendcode='" + os[2].toString() + "' and pp_currency='"
					+ os[3].toString() + "' and pp_statuscode ='POSTED') " + "where ppd_id=" + os[0].toString() + "");
		}

	}

	/**
	 * fa->payplease->save->after
	 * 
	 * @author madan 付款申请保存之后：更新申请金额（宇声帐套）
	 */
	public void payplease_save_after_updateamount(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore,
			ArrayList<Map<Object, Object>> gstor2) {
		baseDao.execute(
				"update PayPleaseDetail set ppd_applyamount=round((select sum(ppdd_thisapplyamount) from paypleasedetaildet where ppdd_ppdid=ppd_id),2) where ppd_ppid=? and exists (select 1 from paypleasedetaildet where ppdd_ppdid=ppd_id)",
				store.get("pp_id"));
		baseDao.execute("update PayPleaseDetail set ppd_auditamount=ppd_applyamount where ppd_ppid=" + store.get("pp_id"));
		baseDao.execute("update PayPlease set pp_total=round((select sum(ppd_applyamount) from paypleasedetail where ppd_ppid=pp_id),2) where pp_id="
				+ store.get("pp_id"));
	}

	/**
	 * fa->payplease->submit->after
	 * 
	 * @author madan 付款申请单提交计算Lockamount 并判断
	 */
	public void payplease_submit_after_lockamount(Integer pp_id) {
		Object code = baseDao.getFieldDataByCondition("PayPlease", "pp_code", "pp_id='" + pp_id + "'");
		String res = baseDao.callProcedure("SP_APLOCKAMOUNT", new Object[] { code });
		if (res.trim().equals("OK") || res == null) {
			SqlRowList rs = baseDao.queryForRowSet("select ppdd_billcode,ab_apamount,ab_payamount,ab_lockamount,ppdd_thisapplyamount "
					+ "from paypleasedetaildet left join apbill on ppdd_billcode=ab_code left join paypleasedetail on ppdd_ppdid=ppd_id "
					+ "where round(abs(nvl(ab_apamount,0)),2)<round(abs(nvl(ab_payamount,0)+nvl(ab_lockamount,0)),2) " + "and ppd_ppid=?",
					pp_id);
			StringBuffer sb = new StringBuffer();
			String abcode = null;
			String dets = null;
			while (rs.next()) {
				abcode = rs.getGeneralString("ppdd_billcode");
				sb.append("本次申请金额大于发票金额：<hr/>发票[" + abcode + "]存在金额被锁定:" + "<hr/>");
				dets = baseDao
						.queryForObject(
								"select wmsys.wm_concat(rb_kind||'['||rb_code||']行号['||rbap_detno||']金额['||rbap_nowbalance||']') from RECBALANCEAP,RecBalance where rbap_rbid=rb_id and rbap_ordercode=? and rb_kind='应收冲应付' and rb_statuscode='UNPOST'",
								String.class, abcode);
				if (dets != null) {
					sb.append(dets + "<hr/>");
				}
				dets = baseDao
						.queryForObject(
								"select wmsys.wm_concat(pb_kind||'['||pb_code||']行号['||pbd_detno||']金额['||pbd_nowbalance||']') from paybalancedetail,paybalance where pbd_pbid=pb_id and pbd_ordercode=? and pb_statuscode='UNPOST'",
								String.class, abcode);
				if (dets != null) {
					sb.append(dets + "<hr/>");
				}
				dets = baseDao
						.queryForObject(
								"select wmsys.wm_concat('银行登记['||ar_code||']行号['||ard_detno||']金额['||ard_nowbalance||']') from ACCOUNTREGISTERDETAIL,accountregister where ard_arid=ar_id and ard_ordercode=? and nvl(ar_statuscode,' ')<>'POSTED'",
								String.class, abcode);
				if (dets != null) {
					sb.append(dets + "<hr/>");
				}
				dets = baseDao
						.queryForObject(
								"select wmsys.wm_concat('付款申请['||pp_code||']行号['||ppdd_detno||']金额['||(ppdd_thisapplyamount-NVL(ppdd_turnamount,0))||']') from payplease,paypleasedetaildet where ppdd_ppid=pp_id and ppdd_billcode=? and nvl(pp_statuscode,' ')<>'FINISH' and pp_paystatus<>'已付款' and abs(nvl(ppdd_thisapplyamount,0))>abs(NVL(ppdd_turnamount,0))",
								String.class, abcode);
				if (dets != null) {
					sb.append(dets + "<hr/>");
				}
			}
			if (sb.length() > 0) {
				BaseUtil.showError(sb.toString());
			}
		} else {
			BaseUtil.showError(res);
		}
	}

	/**
	 * fa->payplease->submit->after
	 * 
	 * @author madan 计算Lockamount，限制本次申请金额不能大于已开票未付款金额
	 */
	public void payplease_submit_after_billlockamount(Integer pp_id) {
		if (baseDao.isDBSetting("autoCreateApBill") && baseDao.isDBSetting("useBillOutAP")) {
			Object code = baseDao.getFieldDataByCondition("PayPlease", "pp_code", "pp_id='" + pp_id + "'");
			String res = baseDao.callProcedure("SP_APLOCKAMOUNT", new Object[] { code });
			if (res.trim().equals("OK") || res == null) {
				SqlRowList rs = baseDao
						.queryForRowSet(
								"select ppdd_billcode,ab_apamount,ab_payamount,ab_lockamount,ppdd_thisapplyamount "
										+ "from paypleasedetaildet left join apbill on ppdd_billcode=ab_code left join paypleasedetail on ppdd_ppdid=ppd_id "
										+ "where round(abs(nvl(ab_invoamount,0)),2)<round(abs(nvl(ab_payamount,0)+nvl(ab_lockamount,0)),2) "
										+ "and ppd_ppid=?", pp_id);
				StringBuffer sb = new StringBuffer();
				String abcode = null;
				while (rs.next()) {
					abcode = rs.getGeneralString("ppdd_billcode");
					sb.append("本次申请金额大于发票已开票金额：<hr/>发票[" + abcode + "]<hr/>");
				}
				if (sb.length() > 0) {
					BaseUtil.showError(sb.toString());
				}
			} else {
				BaseUtil.showError(res);
			}
		}
	}

	/**
	 * 固定资产卡片 保存之前取最大值
	 * 
	 * @param store
	 * @param gstore
	 * @param language
	 */
	public void assetscard_save_after_getMaxNumber(HashMap<Object, Object> store) {
		if (store.containsKey("type")) {
			if (store.get("type").equals("save")) {
				@SuppressWarnings("unchecked")
				Map<Object, Object> map = (Map<Object, Object>) store.get("data");
				int id = Integer.parseInt(map.get("ac_id").toString());
				int kind = Integer.parseInt(map.get("ac_kindid").toString());
				Object[] o = null;
				if (kind == -1) {
					o = baseDao.getFieldsDataByCondition("assetskind", new String[] { "ak_leadcode", "ak_maxnumber" }, "1=1");
				} else {
					o = baseDao.getFieldsDataByCondition("assetskind", new String[] { "ak_leadcode", "ak_maxnumber" }, "ak_id='" + kind
							+ "'");
				}
				String number = String.valueOf((Integer.parseInt(o[1].toString()) + 1));
				for (int i = 0; i < 6 - number.length(); i++) {
					number = "0" + number;
				}
				String code = null;
				if (o[0] == null) {
					code = "" + number;
				} else {
					code = o[0] + number;
				}
				if (code != null && !code.equals("")) {
					baseDao.execute("UPDATE ASSETSCARD SET ac_code='" + code + "' WHERE ac_id='" + id + "'");
					if (kind == -1) {
						baseDao.execute("update assetskind set ak_maxnumber=ak_maxnumber+1");
					} else {
						baseDao.execute("update assetskind set ak_maxnumber=ak_maxnumber+1 where ak_id ='" + kind + "'");
					}
				}
			}
			if (store.get("type").equals("update")) {
				@SuppressWarnings("unchecked")
				Map<Object, Object> map = (Map<Object, Object>) store.get("data");
				String code1 = map.get("ac_code").toString();
				if (code1 == null || code1.trim().equals("")) {
					int id = Integer.parseInt(map.get("ac_id").toString());
					int kind = Integer.parseInt(map.get("ac_kindid").toString());
					Object[] o = null;
					if (kind == -1) {
						o = baseDao.getFieldsDataByCondition("assetskind", new String[] { "ak_leadcode", "ak_maxnumber" }, "1=1");
					} else {
						o = baseDao.getFieldsDataByCondition("assetskind", new String[] { "ak_leadcode", "ak_maxnumber" }, "ak_id='" + kind
								+ "'");
					}
					String number = String.valueOf((Integer.parseInt(o[1].toString()) + 1));
					for (int i = 0; i < 6 - number.length(); i++) {
						number = "0" + number;
					}
					String code = null;
					if (o[0] == null) {
						code = "" + number;
					} else {
						code = o[0] + number;
					}
					if (code != null && !code.equals("")) {
						baseDao.execute("UPDATE ASSETSCARD SET ac_code='" + code + "' WHERE ac_id='" + id + "'");
						if (kind == -1) {
							baseDao.execute("update assetskind set ak_maxnumber=ak_maxnumber+1");
						} else {
							baseDao.execute("update assetskind set ak_maxnumber=ak_maxnumber+1 where ak_id ='" + kind + "'");
						}
					}
				}
			}
		}
	}

	/**
	 * 卡片类型 保存,修改 更新maxnumbers表中的相关类型 leadcode maxnumber
	 * 
	 * @param store
	 * @param gstore
	 * @param language
	 */
	public void assetskind_save_after_updateMaxNumber(HashMap<Object, Object> store) {

	}

	/**
	 * BatchDeal->vastTurnARBill 应收发票批量开票前，判断业务员是否一样(宇声)
	 */
	public void batchDeal_vastTurnARBill_before(ArrayList<HashMap<Object, Object>> maps) {
		String ids = BaseUtil.parseArray2Str(CollectionUtil.pluck(maps, "pd_id"), ",");
		SqlRowList rs = baseDao
				.queryForRowSet("select count(1) from (select distinct pi_sellercode from prodinout left join prodiodetail on pi_id=pd_piid where pd_id in ("
						+ ids + "))");
		if (rs.next() && rs.getInt(1) > 1) {
			BaseUtil.showError("选定出入库单的业务员不同,不能合并生成应收发票!");
		}
	}

	/**
	 * XIONGCY BatchDeal->vastTurnAPBill
	 */

	public void batchDeal_vastTurnAPBill_before(ArrayList<HashMap<Object, Object>> maps) {
		String ids = BaseUtil.parseArray2Str(CollectionUtil.pluck(maps, "pd_id"), ",");
		SqlRowList rs = baseDao
				.queryForRowSet("select count(1) from (select distinct pi_cop from prodinout left join prodiodetail on pi_id=pd_piid where pd_id in ("
						+ ids + "))");
		if (rs.next() && rs.getInt(1) > 1) {
			BaseUtil.showError("选定出入库单的所属公司不同,不能开票!");
		}
		SqlRowList rs1 = baseDao
				.queryForRowSet("select count(1) from (select distinct pi_departmentcode from prodinout left join prodiodetail on pi_id=pd_piid where pd_id in ("
						+ ids + "))");
		if (rs1.next() && rs1.getInt(1) > 1) {
			BaseUtil.showError("选定出入库单的所属部门不同,不能开票!");
		}
		SqlRowList rs2 = baseDao
				.queryForRowSet("select count(1) from (select distinct pi_sellercode from prodinout left join prodiodetail on pi_id=pd_piid where pd_id in ("
						+ ids + "))");
		if (rs2.next() && rs2.getInt(1) > 1) {
			BaseUtil.showError("选定出入库单的业务员不同,不能开票!");
		}
		SqlRowList rs3 = baseDao
				.queryForRowSet("select count(1) from (select distinct pi_paymentcode from prodinout left join prodiodetail on pi_id=pd_piid where pd_id in ("
						+ ids + "))");
		if (rs3.next() && rs3.getInt(1) > 1) {
			BaseUtil.showError("选定出入库单付款方式不同,不能开票!");
		}

		SqlRowList rs4 = baseDao
				.queryForRowSet("select count(1) from (select distinct es_cop from Estimate left join EstimateDetail on es_id=esd_esid where esd_pdid in ("
						+ ids + "))");
		if (rs4.next() && rs4.getInt(1) > 1) {
			BaseUtil.showError("选定应付暂估所属公司不同,不能开票!");
		}
		SqlRowList rs5 = baseDao
				.queryForRowSet("select count(1) from (select distinct es_departmentcode from Estimate left join EstimateDetail on es_id=esd_esid where esd_pdid in ("
						+ ids + "))");
		if (rs5.next() && rs5.getInt(1) > 1) {
			BaseUtil.showError("选定应付暂估部门不同,不能开票!");
		}
		SqlRowList rs6 = baseDao
				.queryForRowSet("select count(1) from (select distinct es_buyercode from Estimate left join EstimateDetail on es_id=esd_esid where esd_pdid in ("
						+ ids + "))");
		if (rs6.next() && rs6.getInt(1) > 1) {
			BaseUtil.showError("选定应付暂估采购员不同,不能开票!");
		}
		SqlRowList rs7 = baseDao
				.queryForRowSet("select count(1) from (select distinct es_paymentscode from Estimate left join EstimateDetail on es_id=esd_esid where esd_pdid in ("
						+ ids + "))");
		if (rs7.next() && rs7.getInt(1) > 1) {
			BaseUtil.showError("选定应付暂估付款方式不同,不能开票!");
		}

	}

	/**
	 * arbill->billout->delete->before 应收发票记录单删除前，还原应收发票的数据
	 * 
	 * @author madan
	 */
	public void arbill_billout_delete(Integer id) {
		billOutDao.deleteBillOut(id);
	}

	/**
	 * fa->arbill->billout->deletedetail->before 应收发票记录单明细删除前，还原应收发票的数据
	 * 
	 * @author madan
	 */
	public void arbill_billout_deletedetail(Integer id) {
		billOutDao.restoreARBill(id);
	}

	/**
	 * 应收发票记录单明细修改前，修改数量要反馈到应收发票
	 * 
	 * @author madan 2014-3-14 11:54:55
	 */
	static final String CHECK_YQTY = "SELECT abd_code,abd_detno,abd_qty FROM ARBillDetail WHERE abd_id=? and abs(abd_qty)<?";

	public void arbill_billout_save_qty(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object ardid = null;// billout明细ID
		Object qty = null;
		Integer abd_id = null;// 发票明细ID
		double tQty = 0;// 收料通知单修改数量
		double tQtyold = 0;
		double newqty = 0;
		double ardqty = 0;
		SqlRowList rs = null;
		Object aq = 0;
		for (Map<Object, Object> s : gstore) {
			ardid = s.get("ard_id");
			tQty = Math.abs(Double.parseDouble(s.get("ard_nowqty").toString()));
			tQtyold = Double.parseDouble(s.get("ard_nowqty").toString());
			ardqty = Double.parseDouble(s.get("ard_qty").toString());
			if (ardqty < 0 && tQtyold > 0) {
				BaseUtil.showError("发票数量为负数的，开票数量不能为正数！");
			}
			if (ardqty > 0 && tQtyold < 0) {
				BaseUtil.showError("发票数量为正数的，开票数量不能为负数！");
			}
			if (s.get("ard_id") != null && Integer.parseInt(s.get("ard_id").toString()) != 0) {
				Object[] objs1 = baseDao.getFieldsDataByCondition("BillOutDetail", new String[] { "ard_adid", "ard_nowqty", "ard_detno" },
						"ard_id=" + ardid + " and nvl(ard_adid,0) <>0");
				if (objs1 != null) {
					qty = baseDao.getFieldDataByCondition("BillOutDetail", "sum(nvl(ard_nowqty,0))", "ard_adid=" + objs1[0]
							+ " AND ard_id <>" + ardid);
					aq = baseDao.getFieldDataByCondition("ARCheckDetail", "ad_qty", "ad_id=" + objs1[0]);
					qty = qty == null ? 0 : qty;
					aq = aq == null ? 0 : aq;
					if (Math.abs(Double.parseDouble(String.valueOf(aq))) < Math.abs(Double.parseDouble(String.valueOf(qty)) + tQty)) {
						BaseUtil.showError("行["
								+ objs1[2]
								+ "]开票数量超出对账数,超出数量:"
								+ Math.abs((Double.parseDouble(String.valueOf(qty)) + tQty)
										- Math.abs(Double.parseDouble(String.valueOf(aq)))));
					}
					baseDao.updateByCondition("ARCheckDetail", "ad_yqty=" + (Double.parseDouble(String.valueOf(qty)) + tQtyold), "ad_id="
							+ objs1[0]);
					int ac_id = baseDao.getFieldValue("ARCheckDetail", "ad_acid", "ad_id=" + objs1[0], Integer.class);
					ARCheckDao.updateBillStatus(ac_id);
				}
				Object[] objs = baseDao.getFieldsDataByCondition("BillOutDetail", new String[] { "nvl(ard_orderid,0)", "nvl(ard_nowqty,0)",
						"ard_ordercode", "nvl(ard_orderdetno,0)" }, "ard_id=" + ardid + " and nvl(ard_orderid,0)<>0 and nvl(ard_adid,0)=0");
				if (objs != null && objs[0] != null) {
					abd_id = Integer.parseInt(String.valueOf(objs[0]));
					if (abd_id != null && abd_id > 0) {
						qty = baseDao.getFieldDataByCondition("BillOutDetail", "sum(nvl(ard_nowqty,0))", "ard_orderid=" + abd_id
								+ "and ard_ordercode='" + objs[2] + "' and nvl(ard_adid,0)=0 AND ard_id <>" + ardid);
						qty = qty == null ? 0 : qty;
						newqty = Math.abs(Double.parseDouble(qty.toString()));
						rs = baseDao.queryForRowSet(CHECK_YQTY, abd_id, newqty + tQty);
						if (rs.next()) {
							StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],应收发票号:").append(rs.getString("abd_code")).append(",行号:")
									.append(rs.getInt("abd_detno")).append(",发票数量:").append(rs.getDouble("abd_qty")).append(",已转数量:")
									.append(qty).append(",本次数量:").append(tQtyold);
							BaseUtil.showError(sb.toString());
						}
						baseDao.updateByCondition("ARBillDetail", "abd_yqty=" + (Double.parseDouble(String.valueOf(qty)) + tQtyold),
								"abd_id=" + abd_id);
					}
				}
			}
		}
	}

	/**
	 * apbill->billoutap->delete->before 应付发票记录单删除前，还原应付发票的数据
	 * 
	 * @author madan
	 */
	public void apbill_billoutap_delete(Integer id) {
		billOutAPDao.deleteBillOutAP(id);
	}

	/**
	 * fa->apbill->billoutap->deletedetail->before 应付发票记录单明细删除前，还原应付发票的数据
	 * 
	 * @author madan
	 */
	public void apbill_billoutap_deletedetail(Integer id) {
		billOutAPDao.restoreAPBill(id);
	}

	/**
	 * 应付发票记录单明细修改前，修改数量要反馈到应付发票
	 * 
	 * @author madan 2014-3-14 11:54:55
	 */
	static final String CHECK_AP_YQTY = "SELECT abd_code,abd_detno,abd_qty FROM APBillDetail WHERE abd_id=? and abs(abd_qty)<?";

	public void apbill_billoutap_save_qty(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> gstore) {
		Object ardid = null;// billout明细ID
		Object qty = null;
		Integer abd_id = null;// 发票明细ID
		double tQty = 0;// 收料通知单修改数量
		double tQtyold = 0;
		double newqty = 0;
		double ardqty = 0;
		SqlRowList rs = null;
		Object aq = 0;
		for (Map<Object, Object> s : gstore) {
			ardid = s.get("ard_id");
			tQty = Math.abs(Double.parseDouble(s.get("ard_nowqty").toString()));
			tQtyold = Double.parseDouble(s.get("ard_nowqty").toString());
			ardqty = Double.parseDouble(s.get("ard_qty").toString());
			if (ardqty < 0 && tQtyold > 0) {
				BaseUtil.showError("发票数量为负数的，开票数量不能为正数！");
			}
			if (ardqty > 0 && tQtyold < 0) {
				BaseUtil.showError("发票数量为正数的，开票数量不能为负数！");
			}
			if (s.get("ard_id") != null && Integer.parseInt(s.get("ard_id").toString()) != 0) {
				Object[] objs1 = baseDao.getFieldsDataByCondition("BillOutAPDetail",
						new String[] { "ard_adid", "ard_nowqty", "ard_detno" }, "ard_id=" + ardid + " and nvl(ard_adid,0) <>0");
				if (objs1 != null) {
					qty = baseDao.getFieldDataByCondition("BillOutAPDetail", "sum(nvl(ard_nowqty,0))", "ard_adid=" + objs1[0]
							+ " AND ard_id <>" + ardid);
					aq = baseDao.getFieldDataByCondition("APCheckDetail", "ad_qty", "ad_id=" + objs1[0]);
					qty = qty == null ? 0 : qty;
					aq = aq == null ? 0 : aq;
					if (Math.abs(Double.parseDouble(String.valueOf(aq))) < Math.abs(Double.parseDouble(String.valueOf(qty)) + tQty)) {
						BaseUtil.showError("行["
								+ objs1[2]
								+ "]开票数量超出对账数,超出数量:"
								+ Math.abs((Double.parseDouble(String.valueOf(qty)) + tQty)
										- Math.abs(Double.parseDouble(String.valueOf(aq)))));
					}
					baseDao.updateByCondition("APCheckDetail", "ad_yqty=" + (Double.parseDouble(String.valueOf(qty)) + tQtyold), "ad_id="
							+ objs1[0]);
					int ac_id = baseDao.getFieldValue("APCheckDetail", "ad_acid", "ad_id=" + objs1[0], Integer.class);
					APCheckDao.updateBillStatus(ac_id);
				}
				Object[] objs = baseDao.getFieldsDataByCondition("BillOutAPDetail", new String[] { "nvl(ard_orderid,0)",
						"nvl(ard_nowqty,0)", "ard_ordercode", "nvl(ard_orderdetno,0)" }, "ard_id=" + ardid
						+ " and nvl(ard_orderid,0)<>0 and nvl(ard_adid,0)=0");
				if (objs != null && objs[0] != null) {
					abd_id = Integer.parseInt(String.valueOf(objs[0]));
					if (abd_id != null && abd_id > 0) {
						qty = baseDao.getFieldDataByCondition("BillOutAPDetail", "sum(nvl(ard_nowqty,0))", "ard_orderid=" + abd_id
								+ "and ard_ordercode='" + objs[2] + "' and nvl(ard_adid,0)=0 AND ard_id <>" + ardid);
						qty = qty == null ? 0 : qty;
						newqty = Math.abs(Double.parseDouble(qty.toString()));
						rs = baseDao.queryForRowSet(CHECK_AP_YQTY, abd_id, newqty + tQty);
						if (rs.next()) {
							StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],应付发票号:").append(rs.getString("abd_code")).append(",行号:")
									.append(rs.getInt("abd_detno")).append(",发票数量:").append(rs.getDouble("abd_qty")).append(",已转数量:")
									.append(qty).append(",本次数量:").append(tQtyold);
							BaseUtil.showError(sb.toString());
						}
						baseDao.updateByCondition("APBillDetail", "abd_yqty=" + (Double.parseDouble(String.valueOf(qty)) + tQtyold),
								"abd_id=" + abd_id);
					}
				}
			}
		}
	}

	/**
	 * fa->accountregister->submit->before 银行登记：类型为其它收款 ，其它付款，费用时，辅助核算没填，不允许提交
	 * 
	 * @author madan
	 */
	public void accountregister_submit_before_asscheck(Integer ar_id) {
		StringBuffer sb = new StringBuffer();
		String sql = " select ard_id,ard_detno from accountregisterdetail left join accountregister on ard_arid=ar_id left join Category on ca_code=ard_catecode where ar_id="
				+ ar_id + " and ar_type in ('其它付款','其它收款','费用') and nvl(ca_asstype,' ')<>' '";
		SqlRowList sqlRowList = baseDao.queryForRowSet(sql);
		while (sqlRowList.next()) {
			int count = baseDao.getCountByCondition("AccountRegisterDetailAss", "ars_ardid=" + sqlRowList.getInt("ard_id"));
			if (count <= 0) {
				sb.append("行").append(sqlRowList.getInt("ard_detno")).append("  ");
			}
		}
		if (sb.length() > 0)
			BaseUtil.showErrorOnSuccess("没有设置辅助核算，不允许提交!<br>" + sb.toString());
	}

	/**
	 * fa->recbalance->post->before 应收单据：辅助核算没填，不允许进行当前操作!
	 * 
	 * @author madan
	 */
	public void recbalance_post_before_asscheck(Integer rb_id) {
		StringBuffer sb = new StringBuffer();
		String sql = " select rb_id from RecBalance left join Category on ca_code=rb_catecode where rb_id=" + rb_id
				+ " and nvl(ca_asstype,' ')<>' '";
		int count = 0;
		SqlRowList sqlRowList = baseDao.queryForRowSet(sql);
		if (sqlRowList.next()) {
			count = baseDao.getCountByCondition("RecBalanceAss", "ass_conid=" + sqlRowList.getInt("rb_id"));
			if (count <= 0) {
				BaseUtil.showError("主表没有设置辅助核算，不允许进行当前操作!");
			}
		}
		sql = " select rbd_id,rbd_detno from RecBalanceDetail left join Category on ca_code=rbd_catecode where rbd_rbid=" + rb_id
				+ " and nvl(ca_asstype,' ')<>' '";
		sqlRowList = baseDao.queryForRowSet(sql);
		while (sqlRowList.next()) {
			count = baseDao.getCountByCondition("RecBalanceDetailAss", "dass_condid=" + sqlRowList.getInt("rbd_id"));
			if (count <= 0) {
				sb.append("行").append(sqlRowList.getInt("rbd_detno")).append("  ");
			}
		}
		if (sb.length() > 0)
			BaseUtil.showErrorOnSuccess("从表没有设置辅助核算，不允许进行当前操作!<br>" + sb.toString());
	}

	/**
	 * fa->paybalance->post->before 应付单据：辅助核算没填，不允许进行当前操作
	 * 
	 * @author madan
	 */
	public void paybalance_post_before_asscheck(Integer pb_id) {
		String sql = " select pb_id from PayBalance left join Category on ca_code=pb_catecode where pb_id=" + pb_id
				+ " and nvl(ca_asstype,' ')<>' '";
		int count = 0;
		SqlRowList sqlRowList = baseDao.queryForRowSet(sql);
		if (sqlRowList.next()) {
			count = baseDao.getCountByCondition("PayBalanceAss", "ass_conid=" + sqlRowList.getInt("pb_id"));
			if (count <= 0) {
				BaseUtil.showError("主表没有设置辅助核算，不允许进行当前操作!");
			}
		}
		sql = " select pbd_id,pbd_detno from PayBalanceDetail left join Category on ca_code=pbd_catecode where pbd_pbid=" + pb_id
				+ " and nvl(ca_asstype,' ')<>' '";
		StringBuffer sb = new StringBuffer();
		sqlRowList = baseDao.queryForRowSet(sql);
		while (sqlRowList.next()) {
			count = baseDao.getCountByCondition("PayBalanceDetailAss", "dass_condid=" + sqlRowList.getInt("pbd_id"));
			if (count <= 0) {
				sb.append("行").append(sqlRowList.getInt("pbd_detno")).append("  ");
			}
		}
		if (sb.length() > 0)
			BaseUtil.showErrorOnSuccess("从表没有设置辅助核算，不允许进行当前操作!<br>" + sb.toString());
	}

	/**
	 * fa->PrePay->post->before 预付单据：辅助核算没填，不允许进行当前操作
	 * 
	 * @author madan
	 */
	public void prepay_post_before_asscheck(Integer pp_id) {
		String sql = " select pp_id from PrePay left join Category on ca_code=pp_accountcode where pp_id=" + pp_id
				+ " and nvl(ca_asstype,' ')<>' '";
		int count = 0;
		StringBuffer sb = new StringBuffer();
		SqlRowList sqlRowList = baseDao.queryForRowSet(sql);
		if (sqlRowList.next()) {
			count = baseDao.getCountByCondition("PrePayAss", "ass_conid=" + sqlRowList.getInt("pp_id"));
			if (count <= 0) {
				BaseUtil.showError("主表没有设置辅助核算，不允许进行当前操作!");
			}
		}
		sql = " select ppd_id,ppd_detno from PrePayDetail left join Category on ca_code=ppd_catecode where ppd_ppid=" + pp_id
				+ " and nvl(ca_asstype,' ')<>' '";
		sqlRowList = baseDao.queryForRowSet(sql);
		while (sqlRowList.next()) {
			count = baseDao.getCountByCondition("PrePayDetailAss", "dass_condid=" + sqlRowList.getInt("ppd_id"));
			if (count <= 0) {
				sb.append("行").append(sqlRowList.getInt("ppd_detno")).append("  ");
			}
		}
		if (sb.length() > 0)
			BaseUtil.showErrorOnSuccess("从表没有设置辅助核算，不允许进行当前操作!<br>" + sb.toString());
	}

	/**
	 * fa->PreRec->post->before 预收单据：辅助核算没填，不允许进行当前操作
	 * 
	 * @author madan
	 */
	public void prerec_post_before_asscheck(Integer pr_id) {
		String sql = " select pr_id from PreRec left join Category on ca_code=prd_catecode where pr_id=" + pr_id
				+ " and nvl(ca_asstype,' ')<>' '";
		int count = 0;
		StringBuffer sb = new StringBuffer();
		SqlRowList sqlRowList = baseDao.queryForRowSet(sql);
		if (sqlRowList.next()) {
			count = baseDao.getCountByCondition("PreRecAss", "ass_conid=" + sqlRowList.getInt("pr_id"));
			if (count <= 0) {
				BaseUtil.showError("主表没有设置辅助核算，不允许进行当前操作!");
			}
		}
		sql = " select prd_id,prd_detno from PreRecDetail left join Category on ca_code=prd_catecode where prd_prid=" + pr_id
				+ " and nvl(ca_asstype,' ')<>' '";
		sqlRowList = baseDao.queryForRowSet(sql);
		while (sqlRowList.next()) {
			count = baseDao.getCountByCondition("PreRecDetailAss", "dass_condid=" + sqlRowList.getInt("prd_id"));
			if (count <= 0) {
				sb.append("行").append(sqlRowList.getInt("prd_detno")).append("  ");
			}
		}
		if (sb.length() > 0)
			BaseUtil.showErrorOnSuccess("从表没有设置辅助核算，不允许进行当前操作!<br>" + sb.toString());
	}

	/**
	 * fa->apbill->post->before 其它应付单：辅助核算没填，不允许进行当前操作
	 * 
	 * @author madan
	 */
	public void apbill_post_before_asscheck(Integer ab_id) {
		String sql = " select abd_id,abd_detno from APBillDetail left join Category on ca_code=abd_catecode where abd_abid=" + ab_id
				+ " and nvl(ca_asstype,' ')<>' '";
		StringBuffer sb = new StringBuffer();
		SqlRowList sqlRowList = baseDao.queryForRowSet(sql);
		while (sqlRowList.next()) {
			int count = baseDao.getCountByCondition("APBillDetailAss", "dass_condid=" + sqlRowList.getInt("abd_id"));
			if (count <= 0) {
				sb.append("行").append(sqlRowList.getInt("abd_detno")).append("  ");
			}
		}
		if (sb.length() > 0)
			BaseUtil.showErrorOnSuccess("从表没有设置辅助核算，不允许进行当前操作!<br>" + sb.toString());
	}

	/**
	 * fa->arbill->post->before 其它应收单：辅助核算没填，不允许进行当前操作
	 * 
	 * @author madan
	 */
	public void arbill_post_before_asscheck(Integer ab_id) {
		String sql = " select abd_id,abd_detno from ARBillDetail left join Category on ca_code=abd_catecode where abd_abid=" + ab_id
				+ " and nvl(ca_asstype,' ')<>' '";
		SqlRowList sqlRowList = baseDao.queryForRowSet(sql);
		StringBuffer sb = new StringBuffer();
		while (sqlRowList.next()) {
			int count = baseDao.getCountByCondition("ARBillDetailAss", "dass_condid=" + sqlRowList.getInt("abd_id"));
			if (count <= 0) {
				sb.append("行").append(sqlRowList.getInt("abd_detno")).append("  ");
			}
		}
		if (sb.length() > 0)
			BaseUtil.showErrorOnSuccess("从表没有设置辅助核算，不允许进行当前操作!<br>" + sb.toString());
	}

	/**
	 * 固定资产卡片：保存更新之后， 更新月折旧率ac_monthrate=(1-残值率)/折旧年限(国扬)
	 * 
	 * @author madan 2014-7-9 14:28:40
	 **/
	public void assetscard_save_after_monthrate(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> grid) {
		Object acid = store.get("ac_id");
		baseDao.execute("update AssetsCard set ac_monthrate=(1-ac_crate)/ac_useyears where ac_id=?", acid);
		baseDao.execute("update AssetsCard set ac_monthtotal=round(ac_monthrate*ac_oldvalue,2) where ac_id=?", acid);
	}

	/**
	 * 应收发票：提交之前， 判断主、被动产品不能开在同一张发票里（宇声）
	 * 
	 * @author madan 2014-8-16 09:44:47
	 **/
	public void arbill_commit_before_jitypecheck(Integer id) {
		int count = baseDao
				.getCount("select count(distinct pr_jitype) from arbill,arbilldetail,product where ab_id=abd_abid and abd_prodcode=pr_code and ab_id="
						+ id);
		if (count > 1) {
			BaseUtil.showError("主、被动产品不能开在同一张发票里!");
		}
	}

	/**
	 * 应收发票：提交之前，RockChip与其它品牌不能开在同一张发票里 （宇声）
	 * 
	 * @author madan 2014-8-16 09:47:55
	 **/
	public void arbill_commit_before_RockChipcheck(Integer id) {
		int count = baseDao
				.getCount("select count(*) from arbill,arbilldetail,product where ab_id=abd_abid and abd_prodcode=pr_code and pr_brand='RockChip' and ab_id="
						+ id);
		if (count > 0) {
			count = baseDao
					.getCount("select count(distinct pr_brand) from arbill,arbilldetail,product where ab_id=abd_abid and abd_prodcode=pr_code and ab_id="
							+ id);
			if (count > 1) {
				BaseUtil.showError("RockChip与其它品牌不能开在同一张发票里!");
			}
		}
	}

	/**
	 * 应收发票:提交之前判断主被动器件不能在同一张发票里!(宇声)
	 * 
	 * @author madan 2014-9-25 17:05:54
	 */
	public void arbill_commit_before_pr_jitype(Integer id) {
		int count = baseDao.getCount("SELECT count(distinct pr_jitype) from ARBILLDETAIL,product where abd_abid=" + id
				+ " and abd_prodcode=pr_code and nvl(ab_seller,' ') not like 'POPO%'");
		if (count > 1) {
			BaseUtil.showError("主被动器件不能在同一张发票里,不能提交!");
		}
	}

	/**
	 * 预付申请单:采购单号或者委外单号未填写，不允许提交
	 * 
	 * @author madan 2015-05-18 15:19:27
	 */
	public void paypleaseyf_commit_before_codecheck(Integer id) {
		int count = baseDao
				.getCount("select count(*) from paypleasedetaildet where PPDD_PPDID in (select ppd_id from paypleasedetail where ppd_ppid="
						+ id + ")");
		if (count == 0) {
			BaseUtil.showError("从表二明细行采购单号或者委外单号未填写，不允许进行当前操作!");
		}
		int count1 = baseDao
				.getCount("select count(*) from paypleasedetaildet left join paypleasedetail on PPDD_PPDID=ppd_id where ppd_ppid=" + id
						+ " and (nvl(PPDD_PUCODE,' ')<>' ' or nvl(PPDD_MAKECODE,' ')<>' ')");
		if (count > count1) {
			BaseUtil.showError("从表二明细行采购单号或者委外单号未填写，不允许进行当前操作!");
		}
	}

	/**
	 * 银行登记:删除时把金额返回费用报销!(易方)
	 */
	public void AccountRegister_delete_before_return(Integer id) {
		baseDao.execute(
				"update FeePlease set fp_n1=nvl((select sum(case when ar_sourcetype='还款申请单' then ar_deposit else ar_payment end) from AccountRegister where ar_sourceid=fp_id and ar_id<>? and ar_sourcetype in ('费用报销单','借款申请单','还款申请单')),0) where exists (select 1 from AccountRegister where ar_id=? and ar_sourceid=fp_id and ar_sourcetype in ('费用报销单','借款申请单','还款申请单'))",
				id, id);
		baseDao.execute(
				"update FeePlease set fp_v7=case when nvl(fp_n1,0)=0 then '未支付' else '部分支付' end where exists (select 1 from AccountRegister where ar_id=? and ar_sourceid=fp_id and ar_sourcetype in ('费用报销单','借款申请单','还款申请单'))",
				id);
	}

	/**
	 * 固定资产存放位置维护：已被使用不允许删除
	 * 
	 * @param id
	 */
	public void assetsLocation_delbefore_usecheck(Integer id) {
		baseDao.delCheck("AssetsLocation", id);
	}

	/**
	 * 应收票据异动：删除明细前，已结账的期间不允许删除明细行
	 * 
	 * @author madan 2015-2-7 10:14:22
	 */
	public void billarchange_deletedetail(Integer id) {
		Object[] date = baseDao.getFieldsDataByCondition("BillARChange left join BillARChangeDetail on brc_id=brd_brcid", "brc_date",
				"brd_id=" + id);
		baseDao.checkCloseMonth("MONTH-B", date[0]);
	}

	/**
	 * 应付票据异动：删除明细前，已结账的期间不允许删除明细行
	 * 
	 * @author madan 2015-2-7 10:16:39
	 */
	public void billapchange_deletedetail(Integer id) {
		Object[] date = baseDao.getFieldsDataByCondition("BillAPChange left join BillAPChangeDetail on bpc_id=bpd_bpcid", "bpc_date",
				"bpd_id=" + id);
		baseDao.checkCloseMonth("MONTH-B", date[0]);
	}

	/**
	 * 折旧单，资产增加单，资产减少单：删除明细前，已结账的期间不允许删除明细行
	 * 
	 * @author madan 2015-2-7 10:22:00
	 */
	public void assetsDepreciation_deletedetail(Integer id) {
		Object[] date = baseDao.getFieldsDataByCondition("AssetsDepreciation left join AssetsDepreciationDetail on dd_deid=de_id",
				new String[] { "de_date" }, "dd_id=" + id);
		baseDao.checkCloseMonth("MONTH-F", date[0]);
	}

	/**
	 * 银行登记：删除明细前，已结账的期间不允许删除明细行
	 * 
	 * @author madan 2015-2-9 18:04:45
	 */
	public void accountregister_deletedetail(Integer id) {
		Object[] date = baseDao.getFieldsDataByCondition("AccountRegisterDetail left join AccountRegister on ard_arid=ar_id",
				new String[] { "ar_date" }, "ard_id=" + id);
		baseDao.checkCloseMonth("MONTH-B", date[0]);
	}

	/**
	 * 应付退款：删除明细前，已结账的期间不允许删除明细行
	 * 
	 * @author XIONGCY
	 */
	public void paybalanceTK_deletedetail(Integer id) {
		Object[] date = baseDao.getFieldsDataByCondition("PayBalanceDetail left join PayBalance on pbd_pbid=pb_id",
				new String[] { "pb_date" }, "pbd_id=" + id);
		baseDao.checkCloseMonth("MONTH-V", date[0]);
	}

	/**
	 * 预付退款单：删除明细前，已结账的期间不允许删除明细行
	 * 
	 * @author XIONGCY
	 */
	public void prepayTK_deletedetail(Integer id) {
		Object[] date = baseDao.getFieldsDataByCondition("PrePayDetail left join PrePay on ppd_ppid=pp_id", new String[] { "pb_date" },
				"ppd_id=" + id);
		baseDao.checkCloseMonth("MONTH-V", date[0]);
	}

	/**
	 * 期初调整单：删除明细前，已结账的期间不允许删除明细行
	 * 
	 * @author madan 2015-03-12 15:28:03
	 */
	public void productWHMonthAdjust_deletedetail(Integer id) {
		Object[] date = baseDao.getFieldsDataByCondition("ProductWHMonthAdjust left join ProductWHMonthAdjustdetail on pwa_id=pwd_pwaid",
				"pwa_date", "pwd_id=" + id);
		baseDao.checkCloseMonth("MONTH-P", date[0]);
	}

	/**
	 * 凭证：只能打印非在录入的凭证
	 * 
	 * @author madan 2015-05-26 11:50:29
	 */
	public void voucher_print_before_statuscheck(Integer id) {
		Object status = baseDao.getFieldDataByCondition("Voucher", "vo_statuscode", "vo_id=" + id);
		if ("ENTERING".equals(status)) {
			BaseUtil.showError("只能打印非在录入状态的凭证！");
		}
	}

	/**
	 * 凭证：只能打印已审核的凭证
	 * 
	 * @author madan 2015-06-30 15:52:28
	 */
	public void voucher_print_before_auditcheck(Integer id) {
		Object status = baseDao.getFieldDataByCondition("Voucher", "vo_statuscode", "vo_id=" + id);
		if (!"AUDITED".equals(status)) {
			BaseUtil.showError("只能打印已审核的凭证！");
		}
	}

	/**
	 * 凭证：只能打印已记账的凭证
	 * 
	 * @author madan 2015-11-09 16:41:18
	 */
	public void voucher_print_before_accountcheck(Integer id) {
		Object status = baseDao.getFieldDataByCondition("Voucher", "vo_statuscode", "vo_id=" + id);
		if (!"ACCOUNT".equals(status)) {
			BaseUtil.showError("只能打印已记账的凭证！");
		}
	}

	/**
	 * 预/付款申请单：只能打印已审核的单据
	 * 
	 * @author madan 2015-06-29 10:10:08
	 */
	public void payplease_print_before_statuscheck(Integer id) {
		Object status = baseDao.getFieldDataByCondition("PayPlease", "pp_statuscode", "pp_id=" + id);
		if (!"AUDITED".equals(status)) {
			BaseUtil.showError("只能打印已审核的单据！");
		}
	}

	/**
	 * 报表数据：更新之前判断期间是否小于总账期间
	 * 
	 * @author madan 2015-09-09 15:03:17
	 */
	public void FAReport_savebefore_periodscheck(HashMap<Object, Object> store) {
		Object yearmonth = baseDao.getFieldDataByCondition("PeriodsDetail", "min(PD_DETNO)", "pd_code='MONTH-A' and pd_status=0");
		if (yearmonth != null && yearmonth != "") {
			if (Integer.parseInt(yearmonth.toString()) > Integer.parseInt(store.get("fr_yearmonth").toString())) {
				BaseUtil.showError("期间小于总账期间，不允许进行更新操作！");
			}
		}
	}

	/**
	 * 应收对账单：删除明细前，还原来源单已转数量
	 * 
	 * @author madan 2015-09-14 14:54:18
	 */
	public void archeck_deletedetail_resoreqty(Integer id) {
		ARCheckDao.restoreARBill(id);
	}

	/**
	 * 应付对账单：删除明细前，还原来源单已转数量
	 * 
	 * @author madan 2015-09-14 14:54:44
	 */
	public void apcheck_deletedetail_resoreqty(Integer id) {
		APCheckDao.restoreAPBill(id);
	}

	public void payplease_commit_paydateupdate(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select pp_date,case when nvl(ppd_paymethodcode,' ')=' ' then ve_paymentcode else ppd_paymethodcode end paycode from PayPlease,PayPleaseDetail,vendor where pp_id=ppd_ppid and ppd_vendcode=ve_code and pp_id=? and pp_date is not null",
						id);
		if (rs.next()) {
			Date res = baseDao.callbackProcedure("SP_GETPAYDATE", Date.class, Types.DATE, rs.getObject(1), rs.getObject(2), 0);
			if (res != null) {
				baseDao.execute("update payplease set pp_paydate=" + DateUtil.parseDateToOracleString(null, res) + " where pp_id=" + id);
			}
		}
	}

	/**
	 * 应收开票记录：删除时，还原应收对账单数量 2016-01-07 17:41:02
	 * 
	 * @author mad
	 */
	public void billout_delete_yqtyupdate(Integer id) {
		SqlRowList rs = baseDao.queryForRowSet("select ard_id from BillOutDetail where ard_biid=?", id);
		while (rs.next()) {
			billout_deletedetail_yqtyupdate(rs.getInt("ard_id"));
		}
	}

	/**
	 * 应收开票记录：删除明细时，还原应收对账单数量 2016-01-07 17:46:14
	 * 
	 * @author mad
	 */
	public void billout_deletedetail_yqtyupdate(Integer id) {
		Object[] objs = baseDao.getFieldsDataByCondition("BillOutDetail", new String[] { "ard_adid", "ard_nowqty" }, "ard_id=" + id
				+ " and nvl(ard_adid,0)<>0");
		if (objs != null && objs[0] != null) {
			baseDao.updateByCondition("ARCheckDetail", "ad_yqty=nvl(ad_yqty,0)-(" + objs[1] + ")", "ad_id=" + objs[0]);
			int ac_id = baseDao.getFieldValue("ARCheckDetail", "ad_acid", "ad_id=" + objs[0], Integer.class);
			ARCheckDao.updateBillStatus(ac_id);
		}
	}

	/**
	 * 收款类单据：明细发票本次收款金额大于已开票金额，不能提交过账
	 * 
	 * @author mad
	 */
	public void recbalance_commit_invoamount(Integer id) {
		if (baseDao.isDBSetting("useBillOutAR")) {
			SqlRowList rs = baseDao
					.queryForRowSet(
							"select rbd_nowbalance,rbd_ordercode, nvl(ab_invoamount,0) ab_invoamount, nvl(ab_payamount,0) ab_payamount from (select round(sum(nvl((case when rb_kind='应收退款单' then -1 else 1 end)*rbd_nowbalance,0)),2) rbd_nowbalance, rbd_ordercode from RECBALANCE,RECBALANCEDETAIL where rbd_rbid=rb_id and rbd_rbid=? and nvl(rbd_ordercode,' ')<>' ' group by rbd_ordercode) left join arbill on rbd_ordercode=ab_code",
							id);
			Object y = 0;
			String log = null;
			StringBuffer sb = new StringBuffer();
			while (rs.next()) {
				String ordercode = rs.getGeneralString("rbd_ordercode");
				double invoamount = rs.getGeneralDouble("ab_invoamount");
				double nowbalance = rs.getGeneralDouble("rbd_nowbalance");
				double payamount = rs.getGeneralDouble("ab_payamount");
				if (invoamount == 0) {
					if (nowbalance != 0) {
						sb.append("发票[" + ordercode + "]已开票金额为0，不能进行当前操作！").append("<hr>");
					}
				} else {
					y = baseDao.getFieldDataByCondition("RECBALANCE left join RECBALANCEDETAIL on rbd_rbid=rb_id",
							"sum(nvl((case when rb_kind='应收退款单' then -1 else 1 end)*rbd_nowbalance,0))", "rbd_rbid<>" + id
									+ " and rbd_ordercode='" + ordercode + "' and nvl(rb_statuscode,' ')<>'POSTED'");
					y = y == null ? 0 : y;
					if (Math.abs(NumberUtil.formatDouble(nowbalance + payamount + Double.parseDouble(y.toString()), 2)) > Math
							.abs(NumberUtil.formatDouble(invoamount, 2))) {
						log = "本次结算金额累计大于发票已开票金额！发票[" + ordercode + "]，结算金额累计["
								+ NumberUtil.formatDouble((nowbalance + payamount + Double.parseDouble(y.toString())), 2) + "]，发票已开票金额["
								+ NumberUtil.formatDouble(invoamount, 2) + "]";
						if (log != null) {
							sb.append(log).append("<hr>");
						}
					}
				}
			}
			if (sb.length() > 0) {
				BaseUtil.showError(sb.toString());
			}
		}
	}

	/**
	 * 付款类单据：明细发票本次收款金额大于已开票金额，不能提交过账
	 * 
	 * @author mad
	 */
	public void paybalance_commit_invoamount(Integer id) {
		if (baseDao.isDBSetting("useBillOutAP")) {
			SqlRowList rs = baseDao
					.queryForRowSet(
							"select pbd_nowbalance,pbd_ordercode, nvl(ab_invoamount,0) ab_invoamount, nvl(ab_payamount,0) ab_payamount from (select round(sum(nvl((case when pb_kind='应付退款单' then -1 else 1 end)*pbd_nowbalance,0)),2) pbd_nowbalance, pbd_ordercode from PAYBALANCE,PAYBALANCEDETAIL where pbd_pbid=pb_id and pbd_pbid=? and nvl(pbd_ordercode,' ')<>' ' group by pbd_ordercode) left join apbill on pbd_ordercode=ab_code",
							id);
			Object y = 0;
			String log = null;
			StringBuffer sb = new StringBuffer();
			while (rs.next()) {
				String ordercode = rs.getGeneralString("pbd_ordercode");
				double invoamount = rs.getGeneralDouble("ab_invoamount");
				double nowbalance = rs.getGeneralDouble("pbd_nowbalance");
				double payamount = rs.getGeneralDouble("ab_payamount");
				if (invoamount == 0) {
					if (nowbalance != 0) {
						sb.append("发票[" + ordercode + "]已开票金额为0，不能进行当前操作！").append("<hr>");
					}
				} else {
					y = baseDao.getFieldDataByCondition("PAYBALANCE left join PAYBALANCEDETAIL on pbd_pbid=pb_id",
							"sum(nvl((case when pb_kind='应付退款单' then -1 else 1 end)*pbd_nowbalance,0))", "pbd_pbid<>" + id
									+ " and pbd_ordercode='" + ordercode + "' and nvl(pb_statuscode,' ')<>'POSTED'");
					y = y == null ? 0 : y;
					if (Math.abs(NumberUtil.formatDouble(nowbalance + payamount + Double.parseDouble(y.toString()), 2)) > Math
							.abs(NumberUtil.formatDouble(invoamount, 2))) {
						log = "本次结算金额累计大于发票已开票金额！发票单号[" + ordercode + "]，结算金额累计["
								+ NumberUtil.formatDouble((nowbalance + payamount + Double.parseDouble(y.toString())), 2) + "]，发票已开票金额["
								+ NumberUtil.formatDouble(invoamount, 2) + "]";
						if (log != null) {
							sb.append(log).append("<hr>");
						}
					}
				}
			}
			if (sb.length() > 0) {
				BaseUtil.showError(sb.toString());
			}
		}
	}

	/**
	 * 预收冲应收批量作业：筛选之前刷新应收总账数据
	 */
	public void custmonth_refresh_querybefore(String condition) {
		int yearmonth = autoDepreciationService.getCurrentYearmonthAR();
		String res = baseDao.callProcedure("SP_RefreshCustMonth", new Object[] { yearmonth });
		if (res.equals("OK")) {

		} else {
			BaseUtil.showError(res);
		}
	}

	/**
	 * 预付冲应付批量作业：筛选之前刷新应付总账数据
	 */
	public void vendmonth_refresh_querybefore(String condition) {
		int yearmonth = autoDepreciationService.getCurrentYearmonthAP();
		String res = baseDao.callProcedure("SP_RefreshVendMonth", new Object[] { yearmonth });
		if (res.equals("OK")) {

		} else {
			BaseUtil.showError(res);
		}
	}

	/**
	 * 银行登记：锁定金额+发票已付金额大于发票金额,不能过账
	 */
	public void accountregister_account_before(Integer id) {
		Object type = baseDao.getFieldDataByCondition("AccountRegister", "ar_type", "ar_id=" + id);
		if ("应付款".equals(type.toString())) {
			SqlRowList rs = baseDao
					.queryForRowSet(
							"select ard_ordercode,ab_apamount,ab_payamount,ab_lockamount "
									+ "from AccountRegisterDetail left join apbill on ard_ordercode=ab_code "
									+ "where nvl(ard_ordercode,' ')<>' ' and round(abs(nvl(ab_apamount,0)),2)<round(abs(nvl(ab_payamount,0)+nvl(ab_lockamount,0)),2) and ard_arid=?",
							id);
			StringBuffer sb = new StringBuffer();
			String abcode = null;
			String dets = null;
			while (rs.next()) {
				abcode = rs.getGeneralString("ard_ordercode");
				sb.append("发票[" + abcode + "]存在金额被锁定:" + "<hr/>");
				dets = baseDao
						.queryForObject(
								"select wmsys.wm_concat(rb_kind||'['||rb_code||']行号['||rbap_detno||']金额['||rbap_nowbalance||']') from RECBALANCEAP,RecBalance where rbap_rbid=rb_id and rbap_ordercode=? and rb_kind='应收冲应付' and rb_statuscode='UNPOST'",
								String.class, abcode);
				if (dets != null) {
					sb.append(dets + "<hr/>");
				}
				dets = baseDao
						.queryForObject(
								"select wmsys.wm_concat(pb_kind||'['||pb_code||']行号['||pbd_detno||']金额['||pbd_nowbalance||']') from paybalancedetail,paybalance where pbd_pbid=pb_id and pbd_ordercode=? and pb_statuscode='UNPOST'",
								String.class, abcode);
				if (dets != null) {
					sb.append(dets + "<hr/>");
				}
				dets = baseDao
						.queryForObject(
								"select wmsys.wm_concat('银行登记['||ar_code||']行号['||ard_detno||']金额['||ard_nowbalance||']') from ACCOUNTREGISTERDETAIL,accountregister where ard_arid=ar_id and ard_ordercode=? and nvl(ar_statuscode,' ')<>'POSTED'",
								String.class, abcode);
				if (dets != null) {
					sb.append(dets + "<hr/>");
				}
				dets = baseDao
						.queryForObject(
								"select wmsys.wm_concat('付款申请['||pp_code||']行号['||ppdd_detno||']金额['||(ppdd_thisapplyamount-NVL(ppdd_turnamount,0))||']') from payplease,paypleasedetaildet where ppdd_ppid=pp_id and ppdd_billcode=? and nvl(pp_statuscode,' ')<>'FINISH' and pp_paystatus<>'已付款' and abs(nvl(ppdd_thisapplyamount,0))>abs(NVL(ppdd_turnamount,0))",
								String.class, abcode);
				if (dets != null) {
					sb.append(dets + "<hr/>");
				}
			}
			if (sb.length() > 0) {
				BaseUtil.showError(sb.toString());
			}
		}
	}

	/**
	 * 付款单类冲账单据：锁定金额+发票已付金额大于发票金额,不能过账
	 */
	public void paybalance_account_before(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select pbd_ordercode,ab_apamount,ab_payamount,ab_lockamount "
								+ "from paybalancedetail left join apbill on pbd_ordercode=ab_code "
								+ "where nvl(pbd_ordercode,' ')<>' ' and round(abs(nvl(ab_apamount,0)),2)<round(abs(nvl(ab_payamount,0)+nvl(ab_lockamount,0)),2) and pbd_pbid=?",
						id);
		StringBuffer sb = new StringBuffer();
		String abcode = null;
		while (rs.next()) {
			abcode = rs.getGeneralString("pbd_ordercode");
			sb.append("发票[" + abcode + "]存在金额被锁定:" + "<hr/>");
			String dets = baseDao
					.queryForObject(
							"select wmsys.wm_concat(rb_kind||'['||rb_code||']行号['||rbap_detno||']金额['||rbap_nowbalance||']') from RECBALANCEAP,RecBalance where rbap_rbid=rb_id and rbap_ordercode=? and rb_kind='应收冲应付' and rb_statuscode='UNPOST'",
							String.class, abcode);
			if (dets != null) {
				sb.append(dets + "<hr/>");
			}
			dets = baseDao
					.queryForObject(
							"select wmsys.wm_concat(pb_kind||'['||pb_code||']行号['||pbd_detno||']金额['||pbd_nowbalance||']') from paybalancedetail,paybalance where pbd_pbid=pb_id and pbd_ordercode=? and pb_statuscode='UNPOST'",
							String.class, abcode);
			if (dets != null) {
				sb.append(dets + "<hr/>");
			}
			dets = baseDao
					.queryForObject(
							"select wmsys.wm_concat('银行登记['||ar_code||']行号['||ard_detno||']金额['||ard_nowbalance||']') from ACCOUNTREGISTERDETAIL,accountregister where ard_arid=ar_id and ard_ordercode=? and nvl(ar_statuscode,' ')<>'POSTED'",
							String.class, abcode);
			if (dets != null) {
				sb.append(dets + "<hr/>");
			}
			dets = baseDao
					.queryForObject(
							"select wmsys.wm_concat('付款申请['||pp_code||']行号['||ppdd_detno||']金额['||(ppdd_thisapplyamount-NVL(ppdd_turnamount,0))||']') from payplease,paypleasedetaildet where ppdd_ppid=pp_id and ppdd_billcode=? and nvl(pp_statuscode,' ')<>'FINISH' and pp_paystatus<>'已付款' and abs(nvl(ppdd_thisapplyamount,0))>abs(NVL(ppdd_turnamount,0))",
							String.class, abcode);
			if (dets != null) {
				sb.append(dets + "<hr/>");
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
	}

	/**
	 * 应收冲应付：锁定金额+发票已付金额大于发票金额,不能过账
	 */
	public void rebalance_account_before(Integer id) {
		Object kind = baseDao.getFieldDataByCondition("recbalance", "rb_kind", "rb_id=" + id);
		if ("应收冲应付".equals(kind.toString())) {

			SqlRowList rs = baseDao
					.queryForRowSet(
							"select rbap_ordercode,ab_apamount,ab_payamount,ab_lockamount "
									+ "from RECBALANCEAP left join apbill on rbap_ordercode=ab_code "
									+ "where nvl(rbap_ordercode,' ')<>' ' and round(abs(nvl(ab_apamount,0)),2)<round(abs(nvl(ab_payamount,0)+nvl(ab_lockamount,0)),2) and rbap_rbid=?",
							id);
			StringBuffer sb = new StringBuffer();
			String abcode = null;
			String dets = null;
			while (rs.next()) {
				abcode = rs.getGeneralString("rbap_ordercode");
				sb.append("发票[" + abcode + "]存在金额被锁定:" + "<hr/>");
				dets = baseDao
						.queryForObject(
								"select wmsys.wm_concat(rb_kind||'['||rb_code||']行号['||rbap_detno||']金额['||rbap_nowbalance||']') from RECBALANCEAP,RecBalance where rbap_rbid=rb_id and rbap_ordercode=? and rb_kind='应收冲应付' and rb_statuscode='UNPOST'",
								String.class, abcode);
				if (dets != null) {
					sb.append(dets + "<hr/>");
				}
				dets = baseDao
						.queryForObject(
								"select wmsys.wm_concat(pb_kind||'['||pb_code||']行号['||pbd_detno||']金额['||pbd_nowbalance||']') from paybalancedetail,paybalance where pbd_pbid=pb_id and pbd_ordercode=? and pb_statuscode='UNPOST'",
								String.class, abcode);
				if (dets != null) {
					sb.append(dets + "<hr/>");
				}
				dets = baseDao
						.queryForObject(
								"select wmsys.wm_concat('银行登记['||ar_code||']行号['||ard_detno||']金额['||ard_nowbalance||']') from ACCOUNTREGISTERDETAIL,accountregister where ard_arid=ar_id and ard_ordercode=? and nvl(ar_statuscode,' ')<>'POSTED'",
								String.class, abcode);
				if (dets != null) {
					sb.append(dets + "<hr/>");
				}
				dets = baseDao
						.queryForObject(
								"select wmsys.wm_concat('付款申请['||pp_code||']行号['||ppdd_detno||']金额['||(ppdd_thisapplyamount-NVL(ppdd_turnamount,0))||']') from payplease,paypleasedetaildet where ppdd_ppid=pp_id and ppdd_billcode=? and nvl(pp_statuscode,' ')<>'FINISH' and pp_paystatus<>'已付款' and abs(nvl(ppdd_thisapplyamount,0))>abs(NVL(ppdd_turnamount,0))",
								String.class, abcode);
				if (dets != null) {
					sb.append(dets + "<hr/>");
				}
			}
			if (sb.length() > 0) {
				BaseUtil.showError(sb.toString());
			}
		}
	}

	/**
	 * 付款类单据：删除明细后，还原付款申请单状态
	 */
	public void paybalance_payplease_deletedetail(Integer id) {
		Object[] obj = baseDao.getFieldsDataByCondition("paybalancedetail", new String[] { "pbd_ppddid", "nvl(pbd_nowbalance,0)" },
				"pbd_id=" + id);
		if (obj != null && obj[0] != null) {
			baseDao.updateByCondition("PayPleaseDetailDet", "ppdd_turnamount=nvl(ppdd_turnamount,0)-" + obj[1], "ppdd_id=" + obj[0]);
		}
	}

	/**
	 * 预付款单：删除明细后，还原付款申请单状态
	 */
	public void prepay_payplease_deletedetail(Integer id) {
		Object[] obj = baseDao.getFieldsDataByCondition("prepaydetail", new String[] { "ppd_ppddid", "nvl(ppd_nowbalance,0)" }, "ppd_id="
				+ id);
		if (obj != null && obj[0] != null) {
			baseDao.updateByCondition("PayPleaseDetailDet", "ppdd_turnamount=nvl(ppdd_turnamount,0)-" + obj[1], "ppdd_id=" + obj[0]);
		}
	}

	/**
	 * 银行登记：类型为应付款、预付款，删除明细后，还原来源付款申请单状态
	 */
	public void accountregister_payplease_deletedetail(Integer id) {
		Object[] obj = baseDao.getFieldsDataByCondition("accountregister left join accountregisterdetail on ar_id=ard_arid", new String[] {
				"ard_orderid", "nvl(ard_nowbalance,0)", "ar_type", "ard_ppddid" }, "ard_id=" + id
				+ " and ar_sourcetype ='付款申请' and ar_type in ('应付款','预付款')");
		if (obj != null) {
			if ("应付款".equals(obj[2]) && obj[0] != null) {
				baseDao.updateByCondition("PayPleaseDetailDet", "ppdd_turnamount=nvl(ppdd_turnamount,0)-" + obj[1], "ppdd_id=" + obj[0]);
			}
			if ("预付款".equals(obj[2]) && obj[3] != null) {
				baseDao.updateByCondition("PayPleaseDetailDet", "ppdd_turnamount=nvl(ppdd_turnamount,0)-" + obj[1], "ppdd_id=" + obj[3]);
			}
		}
	}

	/**
	 * 科目：同一父级科目下不能建相同描述的子科目
	 */
	public void category_saveorupdate_before(HashMap<Object, Object> store) {
		Object capcode = store.get("ca_pcode");
		String dets = null;
		if (capcode != null && !"".equals(capcode.toString())) {
			dets = baseDao
					.queryForObject(
							"select wmsys.wm_concat(ca_code) from Category where ca_statuscode<>'DISABLE' and ca_pcode =? and ca_name =? and ca_id <>?",
							String.class, capcode, store.get("ca_name"), store.get("ca_id"));
			if (dets != null) {
				BaseUtil.showError("科目名称与同父级科目的科目" + dets + "名称重复!");
			}
		} else {
			dets = baseDao
					.queryForObject(
							"select wmsys.wm_concat(ca_code) from Category where ca_statuscode<>'DISABLE' and nvl(ca_pcode,' ') =' ' and ca_name =? and ca_id <>?",
							String.class, store.get("ca_name"), store.get("ca_id"));
			if (dets != null) {
				BaseUtil.showError("科目名称重复，重复科目" + dets);
			}
		}
	}

	/**
	 * 付款单：同供应商+币别---付款申请的申请金额不能大于截止当前日期应付发票的应付金额-付款金额合计
	 */
	public void payplease_commit_before(Integer id) {
		Object[] vend = baseDao.getFieldsDataByCondition("PayPleaseDetail", "ppd_vendcode,ppd_currency,ppd_applyamount", "ppd_ppid=" + id);
		if (vend != null) {
			Double balance = baseDao
					.queryForObject(
							"select ROUND(nvl(sum(nvl(ab_apamount,0)-nvl(ab_payamount,0)),0),2) from APBill where ab_statuscode='POSTED' and abs(nvl(ab_apamount,0))>abs(nvl(ab_payamount,0)) and ab_currency = ? and ab_vendcode = ?",
							Double.class, vend[1], vend[0]);
			if (Double.parseDouble(vend[2].toString()) > balance) {
				BaseUtil.showError("当前供应商" + vend[0] + "币别" + vend[1] + "的申请金额" + Float.parseFloat(vend[2].toString()) + "大于应付余额" + balance
						+ "，不允许提交！");
			}
		}
	}

	/**
	 * 付款申请：保存更新之后，根据明细行一付款方式和最小发票日期计算付款日期
	 */
	public void payplease_save_paydateupdate(HashMap<Object, Object> store, ArrayList<Map<Object, Object>> grid) {
		Object id = store.get("pp_id");
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select min(ab_date), min(paycode) from (select pp_date,case when nvl(ppd_paymethodcode,' ')=' ' then ve_paymentcode else ppd_paymethodcode end paycode, ab_date from PayPlease,PayPleaseDetail,vendor,paypleasedetaildet,apbill where pp_id=ppd_ppid and ppd_vendcode=ve_code and ppdd_ppdid=ppd_id and ppdd_billcode=ab_code and pp_id=? and ab_date is not null)",
						id);
		if (rs.next()) {
			if (rs.getObject(1) != null) {
				Date res = baseDao.callbackProcedure("SP_GETPAYDATE", Date.class, Types.DATE, rs.getObject(1), rs.getObject(2), 0);
				if (res != null) {
					baseDao.execute("update payplease set pp_paydate=" + DateUtil.parseDateToOracleString(null, res) + " where pp_id=" + id);
				}
			}
		}
	}

	/**
	 * 付款申请：提交之前，根据明细行一付款方式和最小发票日期计算付款日期
	 */
	public void payplease_commit_paydateupdate2(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select min(ab_date), min(paycode) from (select pp_date,case when nvl(ppd_paymethodcode,' ')=' ' then ve_paymentcode else ppd_paymethodcode end paycode, ab_date from PayPlease,PayPleaseDetail,vendor,paypleasedetaildet,apbill where pp_id=ppd_ppid and ppd_vendcode=ve_code and ppdd_ppdid=ppd_id and ppdd_billcode=ab_code and pp_id=? and ab_date is not null)",
						id);
		if (rs.next()) {
			if (rs.getObject(1) != null) {
				Date res = baseDao.callbackProcedure("SP_GETPAYDATE", Date.class, Types.DATE, rs.getObject(1), rs.getObject(2), 0);
				if (res != null) {
					baseDao.execute("update payplease set pp_paydate=" + DateUtil.parseDateToOracleString(null, res) + " where pp_id=" + id);
				}
			}
		}
	}

	/**
	 * 付款申请：提交之前，限制只能在付款日期前4天进行提交
	 */
	public void payplease_commit_paydatecheck(Integer id) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select pp_paydate,ppd_paymethodcode from PayPlease,PayPleaseDetail,vendor where pp_id=ppd_ppid and ppd_vendcode=ve_code and pp_id=? and pp_date is not null and nvl(ppd_paymethodcode,' ') not in ('FK006','FK007') AND pp_paydate-sysdate>4",
						id);
		if (rs.next()) {
			BaseUtil.showError("只能在付款日期前4天进行提交!");
		}
	}

	/**
	 * 预收单据：明细订单业务员与主表业务员不一致，不允许进行当前操作
	 * 
	 * @author madan
	 */
	public void prerec_post_before_sellercheck(Integer pr_id) {
		String dets = baseDao
				.queryForObject(
						"select wm_concat(prd_detno) from PreRec left join PreRecDetail on pr_id=prd_prid left join sale on prd_ordercode=sa_code where nvl(prd_ordercode,' ')<>' ' and nvl(pr_sellercode,' ')<>nvl(sa_sellercode,' ') and prd_prid=?",
						String.class, pr_id);
		if (dets != null) {
			BaseUtil.showError("明细订单业务员与主表业务员不一致，不允许进行当前操作！行号：" + dets);
		}
	}

	/**
	 * 收款类单据：明细订单业务员与发票业务员不一致，不允许进行当前操作
	 * 
	 * @author madan
	 */
	public void recbalance_post_before_sellercheck(Integer rb_id) {
		String dets = baseDao
				.queryForObject(
						"select wm_concat(rbd_detno) from RecBalance left join RecBalanceDetail on rb_id=rbd_rbid left join arbill on rbd_ordercode=ab_code where nvl(rbd_ordercode,' ')<>' ' and nvl(rb_seller,' ')<>nvl(ab_seller,' ') and rbd_rbid=?",
						String.class, rb_id);
		if (dets != null) {
			BaseUtil.showError("明细发票业务员与主表业务员不一致，不允许进行当前操作！行号：" + dets);
		}
		dets = baseDao
				.queryForObject(
						"select wm_concat(rbpd_detno) from RecBalance left join RECBALANCEPRDETAIL on rb_id=rbpd_rbid left join PreRec on rbpd_ordercode=pr_code where nvl(rbpd_ordercode,' ')<>' ' and nvl(rb_seller,' ')<>nvl(pr_seller,' ') and rbpd_rbid=?",
						String.class, rb_id);
		if (dets != null) {
			BaseUtil.showError("明细预收单业务员与主表业务员不一致，不允许进行当前操作！行号：" + dets);
		}
	}

	/**
	 * 应付发票：审核之后才能过账
	 */
	public void apbill_post_before_needAudited(Integer ab_id) {
		String status = baseDao.getFieldValue("APBill", "ab_auditstatuscode", "ab_id = " + ab_id, String.class);
		if (status == null || !"AUDITED".equals(status)) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.post_onlyAudit"));
		}
	}

	/**
	 * 应付开票记录：审核之后才能过账
	 */
	public void billoutap_post_before_needAudited(Integer bi_id) {
		String status = baseDao.getFieldValue("BillOutAP", "bi_statuscode", "bi_id = " + bi_id, String.class);
		if (status == null || !"AUDITED".equals(status)) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.post_onlyAudit"));
		}
	}

	/**
	 * 收款类单据：明细发票已收金额与实际已收金额不相等，不允许进行当前操作
	 */
	public void recbalance_post_before_recamountcheck(Integer rb_id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(rbd_detno) from (select ab_payamount,ab_code,rbd_detno from RecBalanceDetail,arbill where rbd_ordercode is not null and ab_code=rbd_ordercode and rbd_rbid=? and ab_class<>'初始化') "
								+ "left join (select SUM(case when RB_KIND='应收退款单' then -1 else 1 end*NVL(RBD_NOWBALANCE,0)) RBD_NOWBALANCE,RBD_ORDERCODE from RECBALANCE,RECBALANCEDETAIL where RB_ID=RBD_RBID and NVL(RBD_ORDERCODE,' ')<>' ' and NVL(RB_STATUSCODE,' ')='POSTED' group by RBD_ORDERCODE) "
								+ "on ab_code=RBD_ORDERCODE where nvl(ab_payamount,0)<>nvl(RBD_NOWBALANCE,0)", String.class, rb_id);
		if (dets != null) {
			BaseUtil.showError("明细发票已收金额与实际已收金额不相等，不允许进行当前操作！行号：" + dets);
		}
	}

	/**
	 * fa->billoutap->resAccounted->before
	 * 
	 * @author madan 应付开票记录：反过账之后发票已开票金额小于发票已付款金额+付款申请锁定金额，不允许反过账
	 */
	public void billoutap_resAccounted_before_billlockamount(Integer bi_id) {
		if (baseDao.isDBSetting("autoCreateApBill") && baseDao.isDBSetting("useBillOutAP")) {
			StringBuffer sb = new StringBuffer();
			String abcode = null;
			SqlRowList billcode = baseDao
					.queryForRowSet(
							"select sum(nvl(ard_nowbalance,0)) ard_nowbalance, ard_ordercode from BillOutAPDetail where ard_biid=? and nvl(ard_ordercode,' ')<>' ' group by ard_ordercode",
							bi_id);
			while (billcode.next()) {
				abcode = billcode.getGeneralString("ard_ordercode");
				baseDao.procedure("SP_APLOCKAMOUNT2", new Object[] { abcode });
				String dets = baseDao.getJdbcTemplate().queryForObject(
						"select wm_concat(ab_code) from apbill where nvl(ab_payamount,0)+nvl(ab_lockamount,0)>nvl(ab_invoamount,0)-"
								+ billcode.getGeneralDouble("ard_nowbalance") + " and ab_code=?", String.class, abcode);
				if (dets != null) {
					sb.append("<hr/>发票[" + abcode + "]<hr/>");
				}
			}
			if (sb.length() > 0) {
				BaseUtil.showError("反过账之后发票已开票金额小于发票已付款金额+付款申请锁定金额：" + sb.toString());
			}
		}
	}
}
