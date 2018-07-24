package com.uas.erp.service.fa.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.BillError;
import com.uas.erp.model.DataListDetail;
import com.uas.erp.model.Employee;
import com.uas.erp.model.GridColumns;
import com.uas.erp.service.fa.CheckAccountService;

@Service
public class CheckAccountServiceImpl implements CheckAccountService {

	@Autowired
	private VoucherDao voucherDao;
	@Autowired
	private BaseDao baseDao;

	// ar_chk_b
	private static final String PROD_POSTED = "select pi_inoutno,pi_class from prodinout where pi_class in('出货单','销售退货单') and to_char(pi_date,'yyyymmdd')>=? and to_char(pi_date,'yyyymmdd')<=? and pi_statuscode<>'POSTED'";
	// ar_chk_b
	private static final String AB_POSTED = "select ab_code,ab_class from arbill where to_char(ab_date,'yyyymmdd')>=? and to_char(ab_date,'yyyymmdd')<=? and ab_statuscode<>'POSTED'";
	// ar_chk_b
	private static final String RB_POSTED = "select rb_code,rb_kind from recbalance where to_char(rb_date,'yyyymmdd')>=? and to_char(rb_date,'yyyymmdd')<=? and rb_statuscode<>'POSTED'";
	// ar_chk_b
	private static final String GS_POSTED = "select gs_code,gs_class from goodssend where to_char(gs_date,'yyyymmdd')>=? and to_char(gs_date,'yyyymmdd')<=? and gs_statuscode<>'POSTED'";
	private static final String PRE_REC_POSTED = "select pr_code,pr_kind from prerec where to_char(pr_date,'yyyymmdd')>=? and to_char(pr_date,'yyyymmdd')<=? and pr_statuscode<>'POSTED'";
	private static final String BI_POSTED = "select bi_code,'应收开票记录' from billout where to_char(bi_date,'yyyymmdd')>=? and to_char(bi_date,'yyyymmdd')<=? and bi_statuscode<>'POSTED'";
	// ar_chk_c
	private static final String AB_VOUC = "select ab_code,ab_class from arbill where to_char(ab_date,'yyyymmdd')>=? and to_char(ab_date,'yyyymmdd')<=? and NVL(ab_vouchercode,' ')=' ' and ab_statuscode='POSTED'";
	// ar_chk_c
	private static final String RB_VOUC = "select rb_code,rb_kind from recbalance where to_char(rb_date,'yyyymmdd')>=? and to_char(rb_date,'yyyymmdd')<=? and NVL(rb_vouchercode,' ')=' 'and rb_statuscode='POSTED'";
	// ar_chk_c
	private static final String PR_VOUC = "select pr_code,pr_kind from prerec where to_char(pr_date,'yyyymmdd')>=? and to_char(pr_date,'yyyymmdd')<=? and NVL(pr_vouchercode,' ')=' ' and pr_statuscode='POSTED'";
	// ar_chk_c
	private static final String GS_VOUC = "select gs_code,gs_class from goodssend where to_char(gs_date,'yyyymmdd')>=? and to_char(gs_date,'yyyymmdd')<=? and NVL(gs_vouchercode,' ')=' ' and gs_statuscode='POSTED'";
	private static final String BI_VOUC = "select bi_code,'应收开票记录' from billout where to_char(bi_date,'yyyymmdd')>=? and to_char(bi_date,'yyyymmdd')<=? and NVL(bi_vouchercode,' ')=' ' and bi_statuscode='POSTED'";
	// ar_chk_d
	private static final String PROD_QTY = "select pi_inoutno || '(明细第' || pd_pdno || '条)',pi_class,'出入库：'||(nvl(pd_outqty,0)-nvl(pd_inqty,0))||'<br>转发出商品：'||NVL(pd_turngsqty,0)||'<br>开票：'||(NVL(pd_invoqty,0)+nvl(pd_checkqty,0)) from prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymmdd')>=? and pi_class in ('出货单','销售退货单') and to_char(pi_date,'yyyymmdd')<=? and pi_statuscode='POSTED' and nvl(pd_outqty,0)-nvl(pd_inqty,0)<>NVL(pd_turngsqty,0)+NVL(pd_invoqty,0)+nvl(pd_checkqty,0)";
	// ar_chk_f
	private static final String VOUC_SOURCE = "select distinct '凭证号(' || vo_number || ')',vo_code from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vd_catecode in(@CODES) and NVL(vo_source,' ')=' ' and vo_explanation not in ('汇兑损益')";
	// ar_chk_g
	private static final String ARBILL_QTY_SUM = "select sum(abd_qty) from arbill,arbilldetail where ab_id=abd_abid and to_char(ab_date,'yyyymmdd')>=? and to_char(ab_date,'yyyymmdd')<=? and ab_class='应收发票' and abd_sourcekind='PRODIODETAIL' and ab_statuscode='POSTED'";
	// ar_chk_g
	private static final String PROD_VOQTY_SUM = "select sum(pd_invoqty) from prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymmdd')>=? and pi_class in ('出货单','销售退货单') and to_char(pi_date,'yyyymmdd')<=? and pi_statuscode='POSTED'";
	// ar_chk_h
	private static final String GS_QTY_SUM = "select sum(gsd_qty) from goodssend,goodssenddetail where gs_id=gsd_gsid and to_char(gs_date,'yyyymm')=? and NVL(gsd_pdid,0)<>0";
	// ar_chk_h
	private static final String PROD_GSQTY_SUM = "select sum(pd_turngsqty) from prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymm')=? and pi_class in ('出货单','销售退货单') and pi_statuscode='POSTED'";
	// ar_chk_j
	private static final String PREREC_AMOUNT = "select pr_cmcurrency,ROUND(SUM(case when pr_kind='预收退款单' or pr_kind='预收退款' then -1 else 1 end * NVL(pr_jsamount,0)),2) FROM PreRec where to_char(pr_date,'yyyymmdd')>=? and to_char(pr_date,'yyyymmdd')<=? and pr_statuscode='POSTED' group by pr_cmcurrency";
	// ar_chk_j
	private static final String CM_PREPAYNOW = "select cm_currency,ROUND(sum(cm_prepaynow),2) from custmonth where cm_yearmonth=? group by cm_currency";
	// ar_chk_k
	private static final String RB_AMOUNT = "SELECT rb_currency,ROUND(SUM(ROUND(rb_amount,2)),2) FROM RecBalance WHERE rb_kind='预收冲应收' and to_char(rb_date,'yyyymmdd')>=? and to_char(rb_date,'yyyymmdd')<=? and rb_statuscode='POSTED' group by rb_currency";
	// ar_chk_k
	private static final String CM_PREPAYBALANCE = "select cm_currency,ROUND(sum(cm_prepaybalance),2) from custmonth where cm_yearmonth=? group by cm_currency";
	// ar_chk_l
	private static final String GS_AMOUNT = "select gs_currency,ROUND(sum(round(gsd_qty*gsd_costprice,2)),2) from goodssend,goodssenddetail where gs_id=gsd_gsid and gs_statuscode='POSTED' and to_char(gs_date,'yyyymm')=? group by gs_currency";
	private static final String GS_AMOUNT1 = "select ab_currency,ROUND(sum(round(abd_costprice*abd_qty,2)),2) from arbill,arbilldetail where ab_id=abd_abid and ab_statuscode='POSTED' and nvl(ab_class,' ') not in ('应收款转销','其它应收单') and to_char(ab_date,'yyyymm')=? group by ab_currency";
	// ar_chk_l
	private static final String CM_GSAMOUNT = "select cm_currency,ROUND(sum(cm_gsnowamount),2) from custmonth where cm_yearmonth=? group by cm_currency";
	// ar_chk_m
	private static final String GS_SEND_SUM = "select gs_currency,ROUND(sum(round(gsd_qty*gsd_sendprice,2)),2) from goodssend,goodssenddetail where gs_id=gsd_gsid and gs_statuscode='POSTED' and to_char(gs_date,'yyyymm')=? group by gs_currency";
	private static final String GS_SEND_SUM2 = "select ab_currency,ROUND(SUM(round(abd_price*abd_qty,2)),2) from arbill,arbilldetail where ab_id=abd_abid and ab_statuscode='POSTED' and nvl(ab_class,' ') not in ('应收款转销','其它应收单') and to_char(ab_date,'yyyymm')=? group by ab_currency";
	// ar_chk_m
	private static final String CM_GSAMOUNTS = "select cm_currency,ROUND(sum(cm_gsnowamounts),2) from custmonth where cm_yearmonth=? group by cm_currency";
	// ar_chk_n
	private static final String AB_COST_SUM = "select ab_currency,ROUND(sum(round(abd_qty*abd_costprice,2)),2) from arbill,arbilldetail where ab_id=abd_abid and to_char(ab_date,'yyyymm')=? and ab_class='应收发票' and abd_sourcekind='GOODSSEND' and ab_statuscode='POSTED' group by ab_currency";
	private static final String AB_COST_SUM2 = "select bi_currency,ROUND(SUM(round(ard_costprice*ard_nowqty,2)),2) from Billout,Billoutdetail where bi_id=ard_biid and to_char(bi_date,'yyyymm')=? and bi_statuscode='POSTED' group by bi_currency";
	// ar_chk_n
	private static final String CM_INVOAMOUNT = "select cm_currency,ROUND(sum(cm_gsinvoamount),2) from custmonth where cm_yearmonth=? group by cm_currency";
	// ar_chk_o
	private static final String AB_GOODSEND_AMOUNT = "select ab_currency,ROUND(sum(abd_qty*NVL(abd_price,0)),2) from arbill,arbilldetail where ab_id=abd_abid and to_char(ab_date,'yyyymm')=? and ab_class='应收发票' and abd_sourcekind='GOODSSEND' and ab_statuscode='POSTED' group by ab_currency";
	private static final String AB_GOODSEND_AMOUNT2 = "select bi_currency,ROUND(SUM(ard_nowbalance),2) from Billout,Billoutdetail where bi_id=ard_biid and to_char(bi_date,'yyyymm')=? and bi_statuscode='POSTED' group by bi_currency";
	// ar_chk_o
	private static final String CM_INVOAMOUNTS = "select cm_currency,ROUND(sum(cm_gsinvoamounts),2) from custmonth where cm_yearmonth=? group by cm_currency";
	// ar_chk_p
	private static final String AB_ARAMOUNT = "select ab_currency,ROUND(sum(abd_aramount),2) from arbill,arbilldetail where ab_id=abd_abid and to_char(ab_date,'yyyymmdd')>=? and to_char(ab_date,'yyyymmdd')<=? and ab_statuscode='POSTED' group by ab_currency";
	// ar_chk_p
	private static final String CM_AMOUNT = "select cm_currency,ROUND(sum(cm_nowamount),2) from custmonth where cm_yearmonth=? group by cm_currency";
	// ar_chk_q
	private static final String RB_CMAMOUNT = "select rb_cmcurrency,ROUND(sum(case when rb_kind='应收退款单' then -1 else 1 end * nvl(rb_aramount,0)),2) from recbalance where to_char(rb_date,'yyyymmdd')>=? and to_char(rb_date,'yyyymmdd')<=? and rb_statuscode='POSTED' group by rb_cmcurrency";
	// ar_chk_q
	private static final String CM_PAYAMOUNT = "select cm_currency,ROUND(sum(cm_payamount),2) from custmonth where cm_yearmonth=? group by cm_currency";

	@Override
	public Map<String, Object> getYearMonth(String type, String votype) {
		if (type == null && votype != null) {
			Object t = baseDao.getFieldDataByCondition("Periods", "pe_code", "pe_type='" + votype + "'");
			if (t == null) {
				return null;
			}
			type = t.toString();
		}
		return voucherDao.getJustPeriods(type);
	}

	@Override
	public List<BillError> getBillErrors(String type, Boolean all) {
		try {
			int maxSize = all == null || all == false ? 100 : 50000;
			return baseDao.getJdbcTemplate().query(
					"SELECT t.* FROM (select * from BillError WHERE be_type=? order by be_id) t where rownum <= ?",
					new BeanPropertyRowMapper<BillError>(BillError.class), type, maxSize);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uas.erp.service.fa.CheckAccountService#chk_a(java.lang.Integer)
	 */
	@Override
	public boolean ar_chk_a(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		Map<String, Object> map = getYearMonth("MONTH-A", null);
		boolean bool = String.valueOf(month).equals(map.get("PD_DETNO").toString());
		if (!bool) {
			baseDao.execute(getErrorSql("", "", type, employee.getEm_name(), "收账款期间:" + month + "<br>总账期间:" + map.get("PD_DETNO")));
		}
		return bool;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uas.erp.service.fa.CheckAccountService#chk_b(java.lang.Integer)
	 */
	@Override
	public boolean ar_chk_b(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未过账";
		clearByCaller(type);
		int count = 0;
		count += isError(PROD_POSTED, new Object[] { start, end }, type, employee, remark, all);
		count += isError(AB_POSTED, new Object[] { start, end }, type, employee, remark, all);
		count += isError(RB_POSTED, new Object[] { start, end }, type, employee, remark, all);
		count += isError(GS_POSTED, new Object[] { start, end }, type, employee, remark, all);
		count += isError(PRE_REC_POSTED, new Object[] { start, end }, type, employee, remark, all);
		count += isError(BI_POSTED, new Object[] { start, end }, type, employee, remark, all);
		return count == 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uas.erp.service.fa.CheckAccountService#ar_chk_c(com.uas.erp.model
	 * .Employee, java.lang.Integer)
	 */
	@Override
	public boolean ar_chk_c(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未制作凭证";
		clearByCaller(type);
		int count = 0;
		count += isError(AB_VOUC, new Object[] { start, end }, type, employee, remark, all);
		count += isError(RB_VOUC, new Object[] { start, end }, type, employee, remark, all);
		count += isError(PR_VOUC, new Object[] { start, end }, type, employee, remark, all);
		count += isError(GS_VOUC, new Object[] { start, end }, type, employee, remark, all);
		count += isError(BI_VOUC, new Object[] { start, end }, type, employee, remark, all);
		return count == 0;
	}

	final static String reset_arbill_yqty_1 = "update prodiodetail set pd_turngsqty=nvl((select sum(gsd_qty) from goodssenddetail where gsd_pdid=pd_id and gsd_picode=pd_inoutno),0) where pd_piid in (select pi_id from prodinout where to_char(pi_date,'yyyymm')=? and pi_class in ('出货单','销售退货单'))";
	// 本次开票数量的字段更改为abd_qty
	final static String reset_arbill_yqty_2 = "update prodiodetail set pd_showinvoqty=nvl((select sum(abd_qty) from arbilldetail where abd_sourcedetailid=pd_id and abd_sourcekind='PRODIODETAIL'),0) where pd_piid in (select pi_id from prodinout where to_char(pi_date,'yyyymm')=? and pi_class in ('出货单','销售退货单'))";
	// 本次开票数量的字段更改为abd_qty
	final static String reset_arbill_yqty_4 = "update prodiodetail set pd_invoqty=nvl((select sum(abd_qty) from arbilldetail where abd_sourcedetailid=pd_id and abd_sourcekind='PRODIODETAIL' and abd_status>0),0) where pd_piid in (select pi_id from prodinout where to_char(pi_date,'yyyymm')=? and pi_class in ('出货单','销售退货单'))";

	final static String reset_arbill_yqty_3 = "Update Goodssenddetail Set Gsd_Showinvoqty=Nvl((Select Sum(Abd_Qty) From Arbilldetail Where Abd_Sourcedetailid=Gsd_Id And Abd_Sourcekind='GOODSSEND'),0) where gsd_gsid in (select gs_id from goodssend where to_char(gs_date,'yyyymm')=?)";
	// 更新来源对账单转发票数量
	final static String reset_arbill_yqty_5 = "update prodiodetail set pd_checkqty=nvl((select sum(abd_qty) from arbilldetail where nvl(abd_adid,0)<>0 and abd_pdid=pd_id),0) where pd_piid in (select pi_id from prodinout where to_char(pi_date,'yyyymm')=? and pi_class in('出货单','销售退货单') and pi_statuscode='POSTED')";

	@Override
	public boolean ar_chk_d(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未开票或未转发出商品";
		clearByCaller(type);
		// 刷新已转数量
		baseDao.execute(reset_arbill_yqty_1, month);
		baseDao.execute(reset_arbill_yqty_2, month);
		baseDao.execute(reset_arbill_yqty_4, month);
		baseDao.execute(reset_arbill_yqty_3, month);
		baseDao.execute(reset_arbill_yqty_5, month);
		int count = isError(PROD_QTY, new Object[] { start, end }, type, employee, remark, all);
		return count == 0;
	}

	final static String BE = "INSERT INTO BillError(be_code,be_class,be_type,be_date,be_checker,be_remark) VALUES(";

	/**
	 * 清除之前检验记录
	 * 
	 * @param caller
	 */
	private void clearByCaller(String caller) {
		baseDao.deleteByCondition("BillError", "be_type='" + caller + "'");
	}

	/**
	 * 检测错误<br>
	 * 如果有错误数据，写入到BillError
	 * 
	 * @param sql
	 * @param args
	 * @param caller
	 * @param employee
	 * @param remark
	 * @param url
	 * @return
	 */
	private int isError(String sql, Object[] args, String type, Employee employee, String remark, Boolean all) {
		SqlRowList rs = baseDao.queryForRowSet(sql, args);
		int maxSize = all == null || all == false ? 100 : 50000;// 默认只记录前100条
		if (rs.hasNext()) {
			List<String> sqls = new ArrayList<String>();
			while (rs.next()) {
				String rem = rs.getString(3);
				rem = rem == null ? remark : rem;
				sqls.add(getErrorSql(rs.getString(1), rs.getString(2), type, employee.getEm_name(), rem));
				if (sqls.size() == maxSize)
					break;
			}
			baseDao.execute(sqls);
			return sqls.size();
		}
		return 0;
	}

	private String getErrorSql(String be_code, String be_class, String be_type, String be_checker, String be_remark) {
		StringBuffer sb = new StringBuffer(BE);
		sb.append("'").append(be_code).append("','").append(be_class).append("','").append(be_type).append("',sysdate,'")
				.append(be_checker).append("','").append(be_remark).append("')");
		return sb.toString();
	}

	private static final String AB_CHECK_E1 = "select '行号：'||abd_detno,ab_code, '(销售价) '||pd_piclass||'：'||round(pd_sendprice,6)||',发票：'||nvl(round(abd_price,6),0) from arbill,arbilldetail,prodiodetail where ab_id=abd_abid and nvl(abd_sourcedetailid,0)=pd_id and nvl(abd_sourcekind,' ')='PRODIODETAIL' and to_char(ab_date,'yyyymm')=? and ab_class='应收发票' and nvl(round(abd_price,6),0)<>nvl(round(pd_sendprice,6),0)";
	private static final String AB_CHECK_E2 = "select '行号：'||abd_detno,ab_code, '(成本价) '||pd_piclass||'：'||round(pd_price,6)||',发票：'||nvl(round(abd_costprice,6),0) from arbill,arbilldetail,prodiodetail where ab_id=abd_abid and nvl(abd_sourcedetailid,0)=pd_id and nvl(abd_sourcekind,' ')='PRODIODETAIL' and to_char(ab_date,'yyyymm')=? and ab_class='应收发票' and nvl(round(abd_costprice,6),0)<>nvl(round(pd_price,6),0)";
	private static final String AB_CHECK_E3 = "select '行号：'||abd_detno,ab_code, '(销售价) '||abd_sourcetype||'：'||round(gsd_sendprice,6)||',发票：'||nvl(round(abd_price,6),0) from arbill,arbilldetail,GoodsSendDetail where ab_id=abd_abid and nvl(abd_sourcedetailid,0)=gsd_id and nvl(abd_sourcekind,' ')='GOODSSEND' and to_char(ab_date,'yyyymm')=? and ab_class='应收发票' and nvl(round(abd_price,6),0)<>nvl(round(gsd_sendprice,6),0)";
	private static final String AB_CHECK_E4 = "select '行号：'||abd_detno,ab_code, '(成本价) '||abd_sourcetype||'：'||round(gsd_costprice,6)||',发票：'||nvl(round(abd_costprice,6),0) from arbill,arbilldetail,GoodsSendDetail where ab_id=abd_abid and nvl(abd_sourcedetailid,0)=gsd_id and nvl(abd_sourcekind,' ')='GOODSSEND' and to_char(ab_date,'yyyymm')=? and ab_class='应收发票' and nvl(round(abd_costprice,6),0)<>nvl(round(gsd_costprice,6),0)";

	@Override
	public boolean ar_chk_e(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "价格不相等";
		clearByCaller(type);
		int count = 0;
		count += isError(AB_CHECK_E1, new Object[] { month }, type, employee, remark, all);
		count += isError(AB_CHECK_E2, new Object[] { month }, type, employee, remark, all);
		count += isError(AB_CHECK_E3, new Object[] { month }, type, employee, remark, all);
		count += isError(AB_CHECK_E4, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean ar_chk_f(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = 0;
		String[] arCates = baseDao.getDBSettingArray("MonthAccount", "arCatecode");
		if (arCates != null && arCates.length > 0) {
			String remark = "应收来源为空";
			count += isError(VOUC_SOURCE.replace("@CODES", CollectionUtil.toSqlString(arCates)), new Object[] { month }, type, employee,
					remark, all);
		}
		String[] prCates = baseDao.getDBSettingArray("MonthAccount", "preRecCatecode");
		if (prCates != null && prCates.length > 0) {
			String remark = "预收来源为空";
			count += isError(VOUC_SOURCE.replace("@CODES", CollectionUtil.toSqlString(prCates)), new Object[] { month }, type, employee,
					remark, all);
		}
		String[] gsCates = baseDao.getDBSettingArray("MonthAccount", "gsCatecode");
		if (gsCates != null && gsCates.length > 0) {
			String remark = "发出商品来源为空";
			count += isError(VOUC_SOURCE.replace("@CODES", CollectionUtil.toSqlString(gsCates)), new Object[] { month }, type, employee,
					remark, all);
		}
		return count == 0;
	}

	@Override
	public boolean ar_chk_g(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		Double arSum = baseDao.getJdbcTemplate().queryForObject(ARBILL_QTY_SUM, Double.class, start, end);
		Double prodSum = baseDao.getJdbcTemplate().queryForObject(PROD_VOQTY_SUM, Double.class, start, end);
		boolean bool = NumberUtil.compare(arSum, prodSum) == 0;
		if (!bool) {
			baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), "总开票数量:" + arSum + "<br>出货单、退货单的开票数量:" + prodSum));
		}
		return bool;
	}

	@Override
	public boolean ar_chk_h(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		Double gsSum = 0.0;
		gsSum = baseDao.getJdbcTemplate().queryForObject(GS_QTY_SUM, Double.class, month);
		Double prodSum = baseDao.getJdbcTemplate().queryForObject(PROD_GSQTY_SUM, Double.class, month);
		boolean bool = NumberUtil.compare(gsSum, prodSum) == 0;
		if (!bool) {
			baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), "总发出商品数量:" + gsSum + "<br>出货单、退货单的发出商品数量:"
					+ prodSum));
		}
		return bool;
	}

	@Override
	public boolean ar_chk_i(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		// TODO
		return true;
	}

	/**
	 * 分币别比较金额
	 * 
	 * @param sql1
	 * @param objs1
	 * @param desc1
	 * @param sql2
	 * @param objs2
	 * @param desc2
	 * @return 如果两者有差异，返回错误信息，否则为空
	 */
	private String compare(String sql1, Object[] objs1, String desc1, String sql2, Object[] objs2, String desc2) {
		SqlRowList rs = baseDao.queryForRowSet(sql1, objs1);
		Map<String, Double> preAmount = new HashMap<String, Double>();
		while (rs.next()) {
			if (rs.getKeys().size() == 1) {
				preAmount.put(" ", rs.getGeneralDouble(1));
			} else
				preAmount.put(rs.getString(1), rs.getGeneralDouble(2));
		}
		rs = baseDao.queryForRowSet(sql2, objs2);
		Map<String, Double> cmPreAmount = new HashMap<String, Double>();
		while (rs.next()) {
			if (rs.getKeys().size() == 1) {
				cmPreAmount.put(" ", rs.getGeneralDouble(1));
			} else
				cmPreAmount.put(rs.getString(1), rs.getGeneralDouble(2));
		}
		boolean bool = true;
		Set<String> currencys = preAmount.keySet();
		for (String cr : currencys) {
			Double rec = preAmount.get(cr);
			Double cm = cmPreAmount.get(cr);
			cm = cm == null ? 0 : cm;
			if (Math.abs(NumberUtil.formatDouble(rec, 2) - NumberUtil.formatDouble(cm, 2)) > 0) {
				bool = false;
				break;
			}
		}
		if (bool) {
			currencys = cmPreAmount.keySet();
			for (String cr : currencys) {
				Double cm = cmPreAmount.get(cr);
				Double rec = preAmount.get(cr);
				rec = rec == null ? 0 : rec;
				if (Math.abs(NumberUtil.formatDouble(rec, 2) - NumberUtil.formatDouble(cm, 2)) > 0) {
					bool = false;
					break;
				}
			}
		}
		if (!bool) {
			String pre = CollectionUtil.toString(preAmount);
			if (pre == null || pre.length() == 0)
				pre = "(无)";
			String cm = CollectionUtil.toString(cmPreAmount);
			if (cm == null || cm.length() == 0)
				cm = "(无)";
			return desc1 + ":<br>" + pre + "<br>" + desc2 + ":<br>" + cm;
		}
		return null;
	}

	@Override
	public boolean ar_chk_j(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String remark = compare(PREREC_AMOUNT, new Object[] { start, end }, "预收款、预收退款单", CM_PREPAYNOW, new Object[] { month }, "应收总账本期预收");
		if (remark != null) {
			baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
			return false;
		}
		return true;
	}

	@Override
	public boolean ar_chk_k(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String remark = compare(RB_AMOUNT, new Object[] { start, end }, "预收冲账", CM_PREPAYBALANCE, new Object[] { month }, "应收总账本期预收冲账");
		if (remark != null) {
			baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
			return false;
		}
		return true;
	}

	@Override
	public boolean ar_chk_l(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		if (baseDao.isDBSetting("autoCreateArBill") && baseDao.isDBSetting("useBillOutAR")) {
			String remark = compare(GS_AMOUNT1, new Object[] { month }, "发出商品", CM_GSAMOUNT, new Object[] { month }, "应收总账本期发出商品");
			if (remark != null) {
				baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
				return false;
			}
		} else {
			String remark = compare(GS_AMOUNT, new Object[] { month }, "发出商品", CM_GSAMOUNT, new Object[] { month }, "应收总账本期发出商品");
			if (remark != null) {
				baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean ar_chk_m(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		if (baseDao.isDBSetting("autoCreateArBill") && baseDao.isDBSetting("useBillOutAR")) {
			String remark = compare(GS_SEND_SUM2, new Object[] { month }, "发出商品", CM_GSAMOUNTS, new Object[] { month }, "应收总账本期发出商品");
			if (remark != null) {
				baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
				return false;
			}
		} else {
			String remark = compare(GS_SEND_SUM, new Object[] { month }, "发出商品", CM_GSAMOUNTS, new Object[] { month }, "应收总账本期发出商品");
			if (remark != null) {
				baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean ar_chk_n(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		if (baseDao.isDBSetting("autoCreateArBill") && baseDao.isDBSetting("useBillOutAR")) {
			String remark = compare(AB_COST_SUM2, new Object[] { month }, "开票数据中涉及发出商品的（成本价）", CM_INVOAMOUNT, new Object[] { month },
					"应收总账本期发出商品转开票");
			if (remark != null) {
				baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
				return false;
			}
		} else {
			String remark = compare(AB_COST_SUM, new Object[] { month }, "开票数据中涉及发出商品的（成本价）", CM_INVOAMOUNT, new Object[] { month },
					"应收总账本期发出商品转开票");
			if (remark != null) {
				baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean ar_chk_o(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		if (baseDao.isDBSetting("autoCreateArBill") && baseDao.isDBSetting("useBillOutAR")) {
			String remark = compare(AB_GOODSEND_AMOUNT2, new Object[] { month }, "开票数据中涉及发出商品的（销售价）", CM_INVOAMOUNTS,
					new Object[] { month }, "应收总账本期发出商品转开票");
			if (remark != null) {
				baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
				return false;
			}
		} else {
			String remark = compare(AB_GOODSEND_AMOUNT, new Object[] { month }, "开票数据中涉及发出商品的（销售价）", CM_INVOAMOUNTS,
					new Object[] { month }, "应收总账本期发出商品转开票");
			if (remark != null) {
				baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean ar_chk_p(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String remark = compare(AB_ARAMOUNT, new Object[] { start, end }, "发票、其它应收单", CM_AMOUNT, new Object[] { month }, "应收总账本期应收");
		if (remark != null) {
			baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
			return false;
		}
		return true;
	}

	@Override
	public boolean ar_chk_q(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String remark = compare(RB_CMAMOUNT, new Object[] { start, end }, "收款单、退款单、结算单", CM_PAYAMOUNT, new Object[] { month }, "应收总账的本期收款");
		if (remark != null) {
			baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
			return false;
		}
		return true;
	}

	// 主营业务收入比较
	final static String BILLOUT_INCOME = "SELECT sum(round(bi_amount*cm_crrate,2)-round(nvl(bi_taxamount,0)*cm_crrate,2)) FROM BillOut,currencysmonth WHERE bi_currency=cm_crname and cm_yearmonth=? AND to_char(bi_date,'yyyymm')=?";

	final static String ARBILL_INCOME = "select ROUND(SUM((abd_aramount-nvl(abd_taxamount,0))*cm_crrate)-max(nvl(ab_differ,0)),2) from arbill,arbilldetail,CurrencysMonth,product where ab_id=abd_abid and cm_crname=ab_currency and abd_prodcode=pr_code and to_char(ab_date,'yyyymm')=cm_yearmonth and to_char(ab_date,'yyyymm')=? and ab_statuscode='POSTED' and ab_class='应收发票' and pr_incomecatecode in (@CATE)";
	final static String ARBILL_INCOME1 = "select sum(amount) from (select ROUND(SUM((abd_aramount-nvl(abd_taxamount,0))*cm_crrate)-max(nvl(ab_differ,0)),2) amount from Arbill,ArbillDetail,CurrencysMonth,warehouse,customer where ab_id=abd_abid and abd_whcode=wh_code AND ab_currency=cm_crname and ab_custcode=cu_code and to_char(ab_date,'yyyymm')=cm_yearmonth and to_char(ab_date,'yyyymm')=? and ab_statuscode='POSTED' and ab_class='应收发票' and wh_salecatecode in (@CATE) GROUP BY ab_currency,cm_crrate,ab_remark,ab_refno,ab_custcode,cu_name,cu_shortname,wh_salecatecode)";

	final static String CM_BILLOUT_INCOME = "select sum(vd_credit) from voucher left join voucherdetail on vo_id=vd_void where vd_catecode in (@CODES) and vo_yearmonth=? and vo_source='应收开票记录'";

	final static String CM_ARBILL_INCOME = "select sum(vd_credit) from voucher left join voucherdetail on vo_id=vd_void where vd_catecode in (@CODES) and vo_yearmonth=? and vo_source='应收发票'";

	@Override
	public boolean ar_chk_r(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String[] cateCodes = baseDao.getDBSettingArray("MakeCostClose", "incomeCatecode");
		if (cateCodes == null || cateCodes.length == 0)
			BaseUtil.showError("请先设置主营业务收入科目");
		String cateStr = CollectionUtil.toSqlString(cateCodes);
		if (cateCodes != null) {
			if (baseDao.isDBSetting("autoCreateArBill") && baseDao.isDBSetting("useBillOutAR")) {
				String remark = compare(BILLOUT_INCOME, new Object[] { month, month }, "发票销售总额",
						CM_BILLOUT_INCOME.replace("@CODES", cateStr), new Object[] { month }, "主营业务收入");
				if (remark != null) {
					baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
					return false;
				}
			} else {
				String remark = null;
				if (baseDao.isDBSetting("MonthAccount!scm", "ifWHCateCode")) {
					remark = compare(ARBILL_INCOME1.replace("@CATE", cateStr), new Object[] { month }, "发票销售总额",
							CM_ARBILL_INCOME.replace("@CODES", cateStr), new Object[] { month }, "主营业务收入");
				} else {
					remark = compare(ARBILL_INCOME.replace("@CATE", cateStr), new Object[] { month }, "发票销售总额",
							CM_ARBILL_INCOME.replace("@CODES", cateStr), new Object[] { month }, "主营业务收入");
				}
				if (remark != null) {
					baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
					return false;
				}
			}
		}
		return true;
	}

	// 主营业务成本比较
	final static String BILLOUT_COST = "select ROUND(SUM(round(ard_costprice*ard_nowqty,2)),2) from BillOut left join BillOutDetail on bi_id=ard_biid where to_char(bi_date,'yyyymm')=? and bi_statuscode='POSTED'";

	final static String ARBILL_COST = "select ROUND(SUM(round(abd_costprice*abd_qty,2)),2) from arbill,arbilldetail,Product where ab_id=abd_abid and abd_prodcode=pr_code and to_char(ab_date,'yyyymm')=? and ab_statuscode='POSTED' and ab_class='应收发票' and pr_costcatecode in (@CATE)";
	final static String ARBILL_COST1 = "select sum(amount) from (select ROUND(SUM(round(abd_costprice*abd_qty,2)),2) amount from Arbill,ArbillDetail,Currencys,warehouse,customer where ab_id=abd_abid and abd_whcode=wh_code AND ab_currency=cr_name and ab_custcode=cu_code and to_char(ab_date,'yyyymm')=? and ab_statuscode='POSTED' and ab_class='应收发票' and wh_costcatecode in (@CATE) GROUP BY wh_costcatecode,ab_custcode,cu_name)";

	final static String CM_BILLOUT_COST = "select sum(vd_debit) from voucher left join voucherdetail on vo_id=vd_void where vd_catecode in (@CODES) and vo_yearmonth=? and vo_source='主营业务成本'";

	final static String CM_ARBILL_COST = "select sum(vd_debit) from voucher left join voucherdetail on vo_id=vd_void where vd_catecode in (@CODES) and vo_yearmonth=? and vo_source in ('主营业务成本','应收发票')";

	@Override
	public boolean ar_chk_s(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String[] cateCodes = baseDao.getDBSettingArray("MakeCostClose", "costCatecode");
		if (cateCodes == null || cateCodes.length == 0)
			BaseUtil.showError("请先设置主营业务成本科目");
		String cateStr = CollectionUtil.toSqlString(cateCodes);
		if (cateCodes != null) {
			if (baseDao.isDBSetting("autoCreateArBill") && baseDao.isDBSetting("useBillOutAR")) {
				String remark = compare(BILLOUT_COST, new Object[] { month }, "发票成本总额", CM_BILLOUT_COST.replace("@CODES", cateStr),
						new Object[] { month }, "主营业务成本");
				if (remark != null) {
					baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
					return false;
				}
			} else {
				String remark = null;
				if (baseDao.isDBSetting("MonthAccount!scm", "ifWHCateCode")) {
					remark = compare(ARBILL_COST1.replace("@CATE", cateStr), new Object[] { month }, "发票成本总额",
							CM_ARBILL_COST.replace("@CODES", cateStr), new Object[] { month }, "主营业务成本");
				} else {
					remark = compare(ARBILL_COST.replace("@CATE", cateStr), new Object[] { month }, "发票成本总额",
							CM_ARBILL_COST.replace("@CODES", cateStr), new Object[] { month }, "主营业务成本");
				}
				if (remark != null) {
					baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean ar_chk_t(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String gsdate = "select '发票编号：'||ab_code,'发票行号：'||abd_detno,'发出商品：'||gs_code||':'||to_char(gs_date,'yyyy-mm-dd')||',发票：'||ab_code||':'||to_char(ab_date,'yyyy-mm-dd') "
				+ "from (select distinct gs_code,gs_date,ab_code,ab_date,abd_detno from arbilldetail left join arbill on abd_abid=ab_id left join goodssenddetail on abd_sourcedetailid=gsd_id left join goodssend on gs_id=gsd_gsid "
				+ "where nvl(abd_sourcekind,' ')='GOODSSEND' and to_char(ab_date,'yyyymm')=? and to_char(gs_date,'yyyymm')>=?)";
		String remark = "发出商品日期期间大于当月发票日期期间";
		clearByCaller(type);
		int count = isError(gsdate, new Object[] { month, month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean ar_chk_u(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String pidate = "select '发票编号：'||ab_code,'发票行号：'||abd_detno,'出入库：'||pi_inoutno||':'||to_char(pi_date,'yyyy-mm-dd')||',发票：'||ab_code||':'||to_char(ab_date,'yyyy-mm-dd') "
				+ "from (select distinct pi_inoutno,pi_class,pi_date,ab_code,ab_date,abd_detno "
				+ "from arbilldetail left join arbill on abd_abid=ab_id left join prodiodetail on abd_sourcedetailid=pd_id left join prodinout on pi_id=pd_piid "
				+ "where nvl(abd_sourcekind,' ')='PRODIODETAIL' and to_char(ab_date,'yyyymm')=? and to_char(pi_date,'yyyymm')<>?)";
		String remark = "出入库单日期期间不等于当月发票日期期间";
		clearByCaller(type);
		int count = isError(pidate, new Object[] { month, month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean ar_chk_v(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String AR = "select '客户编号：'||am_asscode, am_catecode, '应收系统：'||cm_endamount||',总账系统：'||(am_doubleenddebit-am_doubleendcredit)||',差额：'||(am_doubleenddebit-am_doubleendcredit-cm_endamount) from (SELECT '应收' am_catecode, custcode am_asscode, max(cu_name) am_assname, currency am_currency, sum(cm_endamount) cm_endamount, sum(am_doubleenddebit) am_doubleenddebit, sum(am_doubleendcredit) am_doubleendcredit FROM ( SELECT (CASE WHEN NVL(cm_custcode,' ')=' ' THEN am_asscode ELSE cm_custcode END) custcode, (CASE WHEN NVL(cm_custname,' ')=' ' THEN am_assname ELSE cm_custname END) custname, (CASE WHEN NVL(cm_currency,' ')=' ' THEN am_currency ELSE cm_currency END) currency, (CASE WHEN NVL(cm_yearmonth,0)=0 THEN am_yearmonth ELSE cm_yearmonth END) yearmonth, nvl(cm_endamount,0) cm_endamount, nvl(am_doubleenddebit,0) am_doubleenddebit, nvl(am_doubleendcredit,0) am_doubleendcredit FROM CustMonth full join (select sum(NVL(am_doubleenddebit,0)) am_doubleenddebit,sum(NVL(am_doubleendcredit,0)) am_doubleendcredit,am_yearmonth,am_asscode,am_assname,am_currency from AssMonth where am_catecode in (@CODE) GROUP BY am_yearmonth,am_asscode,am_assname,am_currency) on cm_yearmonth = am_yearmonth AND cm_custcode = am_asscode and cm_currency = am_currency ) LEFT JOIN CUSTOMER ON custcode = cu_code WHERE yearmonth =? group by custcode, currency order by custcode) where abs(am_doubleenddebit-am_doubleendcredit-cm_endamount)>0.002";
		String AR2 = "select '客户编号：'||am_asscode, am_catecode, '应收系统：'||cm_endamount||',总账系统：'||(am_doubleenddebit-am_doubleendcredit)||',差额：'||(am_doubleenddebit-am_doubleendcredit-cm_endamount) from (SELECT '应收' am_catecode, custcode am_asscode, max(cu_name) am_assname, currency am_currency, sum(cm_endamount) cm_endamount, sum(am_doubleenddebit) am_doubleenddebit, sum(am_doubleendcredit) am_doubleendcredit FROM ( SELECT (CASE WHEN NVL(cm_custcode,' ')=' ' THEN am_asscode ELSE cm_custcode END) custcode, (CASE WHEN NVL(cm_custname,' ')=' ' THEN am_assname ELSE cm_custname END) custname, (CASE WHEN NVL(cm_currency,' ')=' ' THEN am_currency ELSE cm_currency END) currency, (CASE WHEN NVL(cm_yearmonth,0)=0 THEN am_yearmonth ELSE cm_yearmonth END) yearmonth, nvl(cm_endamount,0)-nvl(cm_gsendamounts,0) cm_endamount, nvl(am_doubleenddebit,0) am_doubleenddebit, nvl(am_doubleendcredit,0) am_doubleendcredit FROM CustMonth full join (select sum(NVL(am_doubleenddebit,0)) am_doubleenddebit,sum(NVL(am_doubleendcredit,0)) am_doubleendcredit,am_yearmonth,am_asscode,am_assname,am_currency from AssMonth where am_catecode in (@CODE) GROUP BY am_yearmonth,am_asscode,am_assname,am_currency) on cm_yearmonth = am_yearmonth AND cm_custcode = am_asscode and cm_currency = am_currency ) LEFT JOIN CUSTOMER ON custcode = cu_code WHERE yearmonth =? group by custcode, currency order by custcode) where abs(am_doubleenddebit-am_doubleendcredit-cm_endamount)>0.002";
		clearByCaller(type);
		String[] arCates = baseDao.getDBSettingArray("MonthAccount", "arCatecode");
		if (arCates != null && arCates.length > 0) {
			if (baseDao.isDBSetting("autoCreateArBill") && baseDao.isDBSetting("useBillOutAR")) {
				String remark = "当月应收账款科目余额与应收总账应收期末余额不一致";
				int count = isError(AR2.replace("@CODE", CollectionUtil.toSqlString(arCates)), new Object[] { month }, type, employee,
						remark, all);
				return count == 0;
			} else {
				String remark = "当月应收账款科目余额与应收总账应收期末余额不一致";
				int count = isError(AR.replace("@CODE", CollectionUtil.toSqlString(arCates)), new Object[] { month }, type, employee,
						remark, all);
				return count == 0;
			}
		}
		return true;
	}

	@Override
	public boolean ar_chk_w(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String AR_PRE = "select '客户编号：'||am_asscode, am_catecode, '应收系统：'||cm_endamount||',总账系统：'||(am_doubleendcredit-am_doubleenddebit)||',差额：'||(am_doubleendcredit-am_doubleenddebit-cm_endamount) from (SELECT '预收' am_catecode, custcode am_asscode, max(cu_name) am_assname, currency am_currency, sum(cm_prepayend) cm_endamount, sum(am_doubleenddebit) am_doubleenddebit, sum(am_doubleendcredit) am_doubleendcredit FROM ( SELECT (CASE WHEN NVL(cm_custcode,' ')=' ' THEN am_asscode ELSE cm_custcode END) custcode, (CASE WHEN NVL(cm_custname,' ')=' ' THEN am_assname ELSE cm_custname END) custname, (CASE WHEN NVL(cm_currency,' ')=' ' THEN am_currency ELSE cm_currency END) currency, (CASE WHEN NVL(cm_yearmonth,0)=0 THEN am_yearmonth ELSE cm_yearmonth END) yearmonth, NVL(cm_prepayend,0) cm_prepayend, NVL(am_doubleenddebit,0) am_doubleenddebit, NVL(am_doubleendcredit,0) am_doubleendcredit FROM CustMonth full join (select sum(NVL(am_doubleenddebit,0)) am_doubleenddebit,sum(NVL(am_doubleendcredit,0)) am_doubleendcredit,am_yearmonth,am_asscode,am_assname,am_currency from AssMonth where am_catecode in (@CODE) GROUP BY am_yearmonth,am_asscode,am_assname,am_currency) on cm_yearmonth = am_yearmonth AND cm_custcode = am_asscode and cm_currency = am_currency ) LEFT JOIN CUSTOMER ON custcode = cu_code WHERE yearmonth =? group by custcode, currency order by custcode) where abs(am_doubleendcredit-am_doubleenddebit-cm_endamount)>0.002";
		clearByCaller(type);
		String[] preRecCates = baseDao.getDBSettingArray("MonthAccount", "preRecCatecode");
		if (preRecCates != null && preRecCates.length > 0) {
			String remark = "当月预收账款科目余额与应收总账预收期末余额不一致";
			int count = isError(AR_PRE.replace("@CODE", CollectionUtil.toSqlString(preRecCates)), new Object[] { month }, type, employee,
					remark, all);
			return count == 0;
		}
		return true;
	}

	@Override
	public boolean ar_chk_x(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String AR_GS = "select ' ', cm_catecode, '应收系统：'||cm_endamount||',总账系统：'||(cm_doubleenddebit-cm_doubleendcredit)||',差额：'||(cm_doubleenddebit-cm_doubleendcredit-cm_endamount) from (select '发出商品' cm_catecode, sum(cm_doubleenddebit) cm_doubleenddebit, sum(cm_doubleendcredit) cm_doubleendcredit, sum(cm_gsendamount) cm_endamount from (select (CASE WHEN NVL(fa.cm_yearmonth, 0) = 0 THEN gl.cm_yearmonth ELSE fa.cm_yearmonth END) yearmonth,NVL(gl.cm_enddebit, 0) cm_doubleenddebit, NVL(gl.cm_endcredit, 0) cm_doubleendcredit, NVL(fa.cm_gsendamount, 0) cm_gsendamount from((select sum(NVL(cm_gsendamount, 0)) cm_gsendamount, cm_yearmonth from custmonth group by cm_yearmonth) fa full join (select sum(NVL(cm_enddebit, 0)) cm_enddebit,sum(NVL(cm_endcredit, 0)) cm_endcredit, cm_yearmonth from catemonth where cm_catecode in (@CODE) group by cm_yearmonth) gl on fa.cm_yearmonth = gl.cm_yearmonth)) where yearmonth = ?) where abs(cm_doubleenddebit-cm_doubleendcredit-cm_endamount)>0.002";
		clearByCaller(type);
		String[] gsCates = baseDao.getDBSettingArray("MonthAccount", "gsCatecode");
		if (gsCates != null && gsCates.length > 0) {
			String remark = "当月发出商品科目余额与应收总账发出商品期末余额(成本金额)不一致";
			int count = isError(AR_GS.replace("@CODE", CollectionUtil.toSqlString(gsCates)), new Object[] { month }, type, employee,
					remark, all);
			return count == 0;
		}
		return true;
	}

	@Override
	public boolean ar_chk_y(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String ar = "select '客户：'||cm_custcode,'币别：'||cm_currency,'应收：'||cm_endamount||',预收：'||cm_prepayend from custmonth where cm_yearmonth=? and nvl(cm_endamount,0)<>0 and nvl(cm_prepayend,0)<>0 ";
		String ar1 = "select '客户：'||cm_custcode,'币别：'||cm_currency,'应收：'||(cm_endamount-nvl(cm_gsendamounts,0))||',预收：'||cm_prepayend from custmonth where cm_yearmonth=? and nvl(cm_endamount,0)-nvl(cm_gsendamounts,0)<>0 and nvl(cm_prepayend,0)<>0 ";
		String remark = "当月的 预收和应收同时有余额";
		clearByCaller(type);
		if (baseDao.isDBSetting("useBillOutAR")) {
			int count = isError(ar1, new Object[] { month }, type, employee, remark, all);
			return count == 0;
		} else {
			int count = isError(ar, new Object[] { month }, type, employee, remark, all);
			return count == 0;
		}

	}

	// ******************************应付******************************
	// ap_chk_b
	private static final String AP_PROD_POSTED = "select pi_inoutno,pi_class from prodinout where pi_class in ('采购验收单','采购验退单','委外验收单','委外验退单') and to_char(pi_date,'yyyymmdd')>=? and to_char(pi_date,'yyyymmdd')<=? and pi_statuscode='UNPOST'";
	private static final String AP_AB_POSTED = "select ab_code,ab_class from apbill where to_char(ab_date,'yyyymmdd')>=? and to_char(ab_date,'yyyymmdd')<=? and ab_statuscode='UNPOST' order by ab_class,ab_code";
	private static final String AP_PB_POSTED = "select pb_code,pb_kind from paybalance where to_char(pb_date,'yyyymmdd')>=? and to_char(pb_date,'yyyymmdd')<=? and pb_statuscode='UNPOST' order by pb_kind,pb_code";
	private static final String AP_PP_POSTED = "select pp_code,pp_type from prepay where to_char(pp_date,'yyyymmdd')>=? and to_char(pp_date,'yyyymmdd')<=? and pp_statuscode='UNPOST' order by pp_type,pp_code";
	private static final String AP_ES_POSTED = "select es_code,es_class from ESTIMATE where to_char(es_date,'yyyymmdd')>=? and to_char(es_date,'yyyymmdd')<=? and es_statuscode='UNPOST'";
	private static final String AP_BP_POSTED = "select bi_code,'应付开票记录' from billoutap where to_char(bi_date,'yyyymmdd')>=? and to_char(bi_date,'yyyymmdd')<=? and bi_statuscode<>'POSTED'";
	// ap_chk_c
	private static final String AP_AB_VOUC = "select ab_code,ab_class from apbill where to_char(ab_date,'yyyymmdd')>=? and to_char(ab_date,'yyyymmdd')<=? and NVL(ab_vouchercode,' ')=' ' and ab_statuscode='POSTED' and nvl(ab_apamount,0)<>0";
	private static final String AP_PB_VOUC = "select pb_code,pb_kind from paybalance where to_char(pb_date,'yyyymmdd')>=? and to_char(pb_date,'yyyymmdd')<=? and NVL(pb_vouchcode,' ')=' 'and pb_statuscode='POSTED' and nvl(pb_amount,0)<>0";
	private static final String AP_PP_VOUC = "select pp_code,pp_type from prepay where to_char(pp_date,'yyyymmdd')>=? and to_char(pp_date,'yyyymmdd')<=? and NVL(pp_vouchercode,' ')=' ' and pp_statuscode='POSTED' and nvl(pp_amount,0)<>0";
	private static final String AP_ES_VOUC = "select es_code,es_class from ESTIMATE where to_char(es_date,'yyyymmdd')>=? and to_char(es_date,'yyyymmdd')<=? and NVL(es_vouchercode,' ')=' ' and es_statuscode='POSTED' and nvl(es_orderamount,0)<>0";
	private static final String AP_BP_VOUC = "select bi_code,'应付开票记录' from billoutap where to_char(bi_date,'yyyymmdd')>=? and to_char(bi_date,'yyyymmdd')<=? and NVL(bi_vouchercode,' ')=' ' and bi_statuscode='POSTED' and nvl(bi_amount,0)<>0";
	// ap_chk_d
	private static final String AP_PROD_QTY = "select pd_inoutno,pd_piclass,pd_pdno from prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymm')=? and pi_class in ('采购验收单','采购验退单','委外验收单','委外验退单') and nvl(pd_inqty,0)-nvl(pd_outqty,0)<>NVL(pd_turnesqty,0)+NVL(pd_showinvoqty,0)+nvl(pd_checkqty,0)";
	// ap_chk_f
	private static final String AP_VOUC_SOURCE = "select '凭证号(' || vo_number || ')',vo_code from voucher,voucherdetail where vo_id=vd_void and vo_yearmonth=? and vd_catecode in(@CODES) and NVL(vo_source,' ')=' ' and nvl(vo_explanation,' ') <>'汇兑损益'";
	// ap_chk_g
	private static final String AP_ARBILL_QTY_SUM = "select nvl(sum(abd_qty),0) from apbill,apbilldetail where ab_id=abd_abid and to_char(ab_date,'yyyymmdd')>=? and to_char(ab_date,'yyyymmdd')<=? and ab_class='应付发票' and abd_sourcekind='PRODIODETAIL' and ab_statuscode='POSTED'";
	private static final String AP_PROD_VOQTY_SUM = "select nvl(sum(pd_invoqty),0) from prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymmdd')>=? and pi_class in ('采购验收单','采购验退单','委外验收单','委外验退单', '用品验收单', '用品验退单') and to_char(pi_date,'yyyymmdd')<=? and pi_statuscode='POSTED'";
	// ap_chk_h
	private static final String AP_ES_QTY_SUM = "select nvl(sum(esd_qty), 0) from ESTIMATE,ESTIMATEdetail where es_id=esd_esid and es_statuscode='POSTED' and to_char(es_date,'yyyymmdd')>=? and to_char(es_date,'yyyymmdd')<=? and NVL(esd_pdid,0)<>0 and esd_picode is not null and esd_piclass<>'模具验收报告'";
	private static final String AP_PROD_ESQTY_SUM = "select nvl(sum(pd_turnesqty), 0) from prodinout,prodiodetail where pi_id=pd_piid and to_char(pi_date,'yyyymmdd')>=? and pi_class in ('采购验收单','采购验退单','委外验收单','委外验退单') and to_char(pi_date,'yyyymmdd')<=? and pi_statuscode='POSTED'";
	// ap_chk_j
	private static final String AP_PREPAY_AMOUNT = "select pp_vmcurrency,ROUND(nvl(SUM(NVL(pp_jsamount,0)*case when pp_type='预付退款' or pp_type='预付退款单' then -1 else 1 end), 0),2) FROM PrePay where to_char(pp_date,'yyyymmdd')>=? and to_char(pp_date,'yyyymmdd')<=? and pp_statuscode='POSTED' group by pp_vmcurrency";
	private static final String AP_VM_PREPAYNOW = "select vm_currency,ROUND(nvl(sum(vm_prepaynow), 0),2) from vendmonth where vm_yearmonth=? group by vm_currency";
	// ap_chk_k
	private static final String AP_PB_AMOUNT = "SELECT pb_currency,ROUND(nvl(SUM(ROUND(pb_amount,2)), 0),2) FROM PayBalance WHERE pb_kind='预付冲应付' and to_char(pb_date,'yyyymmdd')>=? and to_char(pb_date,'yyyymmdd')<=? and pb_statuscode='POSTED' group by pb_currency";
	private static final String AP_VM_PREPAYAMOUNT = "select vm_currency,ROUND(nvl(sum(vm_prepaybalance), 0),2) from vendmonth where vm_yearmonth=? group by vm_currency";
	// ap_chk_l
	private static final String AP_ES_AMOUNT = "SELECT ES_CURRENCY,ROUND(nvl(SUM(AMOUNT),0),2) FROM (SELECT ES_VENDCODE,ES_CURRENCY,ROUND(SUM(round(esd_qty*esd_orderprice/(1+NVL(esd_taxrate,0)/100),2)),2) AMOUNT FROM ESTIMATE,ESTIMATEDETAIL,CURRENCYSMONTH WHERE ES_ID=ESD_ESID AND ES_CURRENCY=CM_CRNAME AND CM_YEARMONTH=? AND ES_STATUSCODE='POSTED' AND TO_CHAR(ES_DATE,'yyyymm')=? GROUP BY ES_VENDCODE,ES_CURRENCY) GROUP BY ES_CURRENCY";
	// 考虑手工做发票的情况
	private static final String AP_ES_AMOUNT2 = "SELECT AB_CURRENCY,ROUND(nvl(SUM(AMOUNT),0),2) FROM (SELECT AB_VENDCODE,AB_CURRENCY,ROUND(SUM(round(abd_thisvoqty*abd_price/(1+NVL(abd_taxrate,0)/100),2)),2) AMOUNT FROM APBill,APBillDetail,CURRENCYSMONTH WHERE ab_ID=abD_abID AND ab_CURRENCY=CM_CRNAME AND CM_YEARMONTH=? AND ab_statusCODE='POSTED' AND TO_CHAR(ab_DATE,'yyyymm')=? and (abd_sourcetype is null or abd_sourcetype in ('采购验收单','采购验退单','委外验收单','委外验退单','模具验收单','模具验退单','模具验收报告','用品验收单','用品验退单')) GROUP BY ab_VENDCODE,ab_CURRENCY) GROUP BY ab_CURRENCY";
	private static final String AP_VM_ESAMOUNT = "select vm_currency,ROUND(nvl(sum(vm_esnowamount),0),2) from vendmonth where vm_yearmonth=? group by vm_currency";
	// ap_chk_m
	private static final String AP_ES_ORDER_SUM = "select es_currency,ROUND(nvl(sum(esd_qty*esd_orderprice),0),2) from ESTIMATE,ESTIMATEdetail where es_id=esd_esid and es_statuscode='POSTED' and to_char(es_date,'yyyymm')=? group by es_currency";
	private static final String AP_ES_ORDER_SUM2 = "select ab_currency,ROUND(nvl(sum(abd_qty*abd_price),0),2) from apbill,apbilldetail where ab_id=abd_abid and ab_statuscode='POSTED' and to_char(ab_date,'yyyymm')=? group by ab_currency";
	private static final String AP_VM_ESAMOUNTS = "select vm_currency,ROUND(nvl(sum(vm_esnowamounts),0),2) from vendmonth where vm_yearmonth=? group by vm_currency";
	// ap_chk_n
	private static final String AP_AB_COST_SUM = "SELECT AB_CURRENCY,sum(amount) from(SELECT AB_CURRENCY,ab_vendcode,ROUND(SUM(round(abd_qty*(CASE WHEN NVL(ESD_PICLASS,' ')='委外验收单' OR NVL(ESD_PICLASS,' ')='委外验退单' THEN ESD_ORDERPRICE/(1+NVL(ESD_TAXRATE,0)/100) ELSE (abd_price/(1+nvl(abd_taxrate,0)/100)) END),2)),2) amount FROM APBILL,APBILLDETAIL,CURRENCYSMONTH,ESTIMATEDETAIL WHERE AB_ID=ABD_ABID AND abd_sourcedetailid=esd_id AND cm_crname=ab_currency and cm_yearmonth=? AND TO_CHAR(AB_DATE,'yyyymm')=? AND AB_CLASS='应付发票' AND ABD_SOURCEKIND='ESTIMATE' AND AB_STATUSCODE='POSTED' GROUP BY AB_CURRENCY,ab_vendcode) GROUP BY AB_CURRENCY";
	private static final String AP_AB_COST_SUM2 = "SELECT BI_CURRENCY,sum(amount) from(SELECT BI_CURRENCY,BI_vendcode,ROUND(SUM(ROUND(ard_nowqty*abd_price*100/(100+nvl(ard_taxrate,0)) ,2)),2) amount FROM billoutap,billoutapdetail,apbilldetail WHERE bi_id=ard_biid and ard_orderid=abd_id AND bi_statuscode='POSTED' AND TO_CHAR(BI_DATE,'yyyymm')=? GROUP BY BI_CURRENCY,BI_vendcode) GROUP BY BI_CURRENCY";
	private static final String AP_VM_ESINVOAMOUNT = "select vm_currency,ROUND(nvl(sum(vm_esinvoamount),0),2) from vendmonth where vm_yearmonth=? group by vm_currency";
	// ap_chk_o
	private static final String AP_AB_AMOUNT = "select ab_currency,ROUND(nvl(sum(abd_qty*abd_price),0),2) from apbill,apbilldetail where ab_id=abd_abid and to_char(ab_date,'yyyymmdd')>=? and to_char(ab_date,'yyyymmdd')<=? and ab_class='应付发票' and abd_sourcekind='ESTIMATE' and ab_statuscode='POSTED' group by ab_currency";
	private static final String AP_VM_ESINVOAMOUNTS = "select vm_currency,ROUND(nvl(sum(vm_esinvoamounts),0),2) from vendmonth where vm_yearmonth=? group by vm_currency";
	// ap_chk_p
	private static final String AP_AB_APAMOUNT = "select AB_CURRENCY,NVL(SUM(ROUND(AB_APAMOUNT,2)),0) from APBILL where TO_CHAR(AB_DATE,'yyyymm')=? and ab_statuscode='POSTED' group by ab_currency";
	private static final String AP_VM_AMOUNT = "select vm_currency,ROUND(nvl(sum(vm_nowamount),0),2) from vendmonth where vm_yearmonth=? group by vm_currency";
	// ap_chk_q
	private static final String AP_PB_VMAMOUNT = "select pb_vmcurrency,ROUND(nvl(sum(amount),0),2) from (select pb_vendcode,pb_vmcurrency,ROUND(sum(case when pb_kind='预付冲应付' then pb_jsamount else pb_apamount end * case when pb_kind='应付退款单' or pb_kind='应付退款' then -1 else 1 end),2) amount from paybalance where to_char(pb_date,'yyyymmdd')>=? and to_char(pb_date,'yyyymmdd')<=? and pb_statuscode='POSTED' group by pb_vendcode,pb_vmcurrency) group by pb_vmcurrency";
	private static final String AP_VM_PAYAMOUNT = "select vm_currency,ROUND(nvl(sum(vm_payamount),0),2) from vendmonth where vm_yearmonth=? group by vm_currency";

	@Override
	public boolean ap_chk_a(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		Map<String, Object> map = getYearMonth("MONTH-A", null);
		boolean bool = String.valueOf(month).equals(map.get("PD_DETNO").toString());
		if (!bool) {
			baseDao.execute(getErrorSql("", "", type, employee.getEm_name(), "付账款期间:" + month + "<br>总账期间:" + map.get("PD_DETNO")));
		}
		return bool;
	}

	@Override
	public boolean ap_chk_b(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未过账";
		clearByCaller(type);
		int count = 0;
		count += isError(AP_PROD_POSTED, new Object[] { start, end }, type, employee, remark, all);
		count += isError(AP_AB_POSTED, new Object[] { start, end }, type, employee, remark, all);
		count += isError(AP_PB_POSTED, new Object[] { start, end }, type, employee, remark, all);
		count += isError(AP_PP_POSTED, new Object[] { start, end }, type, employee, remark, all);
		count += isError(AP_ES_POSTED, new Object[] { start, end }, type, employee, remark, all);
		count += isError(AP_BP_POSTED, new Object[] { start, end }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean ap_chk_c(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未制作凭证";
		clearByCaller(type);
		int count = 0;
		count += isError(AP_AB_VOUC, new Object[] { start, end }, type, employee, remark, all);
		count += isError(AP_PB_VOUC, new Object[] { start, end }, type, employee, remark, all);
		count += isError(AP_PP_VOUC, new Object[] { start, end }, type, employee, remark, all);
		count += isError(AP_ES_VOUC, new Object[] { start, end }, type, employee, remark, all);
		count += isError(AP_BP_VOUC, new Object[] { start, end }, type, employee, remark, all);
		return count == 0;
	}

	final static String reset_apbill_yqty_1 = "update prodiodetail set pd_turnesqty=nvl((select sum(esd_qty) from estimatedetail where esd_pdid=pd_id and esd_picode=pd_inoutno),0) where pd_piid in (select pi_id from prodinout where to_char(pi_date,'yyyymm')=? and pi_class in ('采购验收单','采购验退单','委外验收单','委外验退单'))";
	// abd_qty表示发票本次开票数量，对应出入库单pd_showinvoqty（发票未过账）、pd_invoqty（发票已过账）
	// abd_thisvoqty表示出入库单数量，对应出入库单pd_inqty-pd_outqty
	final static String reset_apbill_yqty_2 = "update prodiodetail set pd_showinvoqty=nvl((select sum(abd_qty) from apbilldetail where abd_sourcedetailid=pd_id and nvl(abd_adid,0)=0 and abd_sourcekind='PRODIODETAIL'),0) where pd_piid in (select pi_id from prodinout where to_char(pi_date,'yyyymm')=? and pi_class in ('采购验收单','采购验退单','委外验收单','委外验退单'))";
	final static String reset_apbill_yqty_3 = "update estimatedetail set esd_showinvoqty=nvl((select sum(abd_qty) from apbilldetail where abd_sourcedetailid=esd_id and abd_sourcekind='ESTIMATE'),0) where esd_esid in (select es_id from estimate where to_char(es_date,'yyyymm')=?)";
	// final static String reset_apbill_yqty_4 =
	// "update prodiodetail set pd_esqty=nvl((select sum(esd_qty) from estimatedetail where esd_pdid=pd_id and esd_picode=pd_inoutno and esd_status>0),0) where exists (select 1 from prodinout where pd_piid=pi_id and to_char(pi_date,'yyyymm')=? and pi_class in ('采购验收单','采购验退单','委外验收单','委外验退单'))";
	// 更新来源对账单转发票数量
	final static String reset_apbill_yqty_5 = "update prodiodetail set pd_checkqty=nvl((select sum(abd_qty) from apbilldetail where nvl(abd_adid,0)<>0 and abd_pdid=pd_id),0) where pd_piid in (select pi_id from prodinout where to_char(pi_date,'yyyymm')=? and pi_class in('采购验收单','委外验收单','采购验退单','委外验退单') and pi_statuscode='POSTED')";

	@Override
	public boolean ap_chk_d(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未开票或未暂估";
		clearByCaller(type);
		// 刷新已转数
		baseDao.execute(reset_apbill_yqty_1, month);
		baseDao.execute(reset_apbill_yqty_2, month);
		baseDao.execute(reset_apbill_yqty_3, month);
		// baseDao.execute(reset_apbill_yqty_4, month);
		baseDao.execute(reset_apbill_yqty_5, month);
		int count = isError(AP_PROD_QTY, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean ap_chk_e(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		// TODO
		return true;
	}

	@Override
	public boolean ap_chk_f(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = 0;
		String[] apCates = baseDao.getDBSettingArray("MonthAccount!AP", "apCatecode");
		if (apCates != null && apCates.length > 0) {
			String remark = "应付款来源为空";
			count += isError(AP_VOUC_SOURCE.replace("@CODES", CollectionUtil.toSqlString(apCates)), new Object[] { month }, type, employee,
					remark, all);
		}
		String[] ppCates = baseDao.getDBSettingArray("MonthAccount!AP", "prePayCatecode");
		if (ppCates != null && ppCates.length > 0) {
			String remark = "预付款来源为空";
			count += isError(AP_VOUC_SOURCE.replace("@CODES", CollectionUtil.toSqlString(ppCates)), new Object[] { month }, type, employee,
					remark, all);
		}
		String[] esCates = baseDao.getDBSettingArray("MonthAccount!AP", "esCatecode");
		if (esCates != null && esCates.length > 0) {
			String remark = "暂估来源为空";
			count += isError(AP_VOUC_SOURCE.replace("@CODES", CollectionUtil.toSqlString(esCates)), new Object[] { month }, type, employee,
					remark, all);
		}
		return count == 0;
	}

	@Override
	public boolean ap_chk_g(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		Double arSum = baseDao.getJdbcTemplate().queryForObject(AP_ARBILL_QTY_SUM, Double.class, start, end);
		Double prodSum = baseDao.getJdbcTemplate().queryForObject(AP_PROD_VOQTY_SUM, Double.class, start, end);
		boolean bool = NumberUtil.compare(arSum, prodSum) == 0;
		if (!bool) {
			baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), "总开票数量:" + arSum + "<br>验收单、验退单的开票数量:" + prodSum));
		}
		return bool;
	}

	@Override
	public boolean ap_chk_h(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		Double gsSum = baseDao.getJdbcTemplate().queryForObject(AP_ES_QTY_SUM, Double.class, start, end);
		Double prodSum = baseDao.getJdbcTemplate().queryForObject(AP_PROD_ESQTY_SUM, Double.class, start, end);
		boolean bool = NumberUtil.compare(gsSum, prodSum) == 0;
		if (!bool) {
			baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), "总的暂估数量:" + gsSum + "<br>验收单、验退单 的暂估数量:" + prodSum));
		}
		return bool;
	}

	@Override
	public boolean ap_chk_i(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		// TODO
		return true;
	}

	@Override
	public boolean ap_chk_j(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String remark = compare(AP_PREPAY_AMOUNT, new Object[] { start, end }, "预付款、预付退款", AP_VM_PREPAYNOW, new Object[] { month },
				"应付总账本期预付");
		if (remark != null) {
			baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
			return false;
		}
		return true;
	}

	@Override
	public boolean ap_chk_k(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String remark = compare(AP_PB_AMOUNT, new Object[] { start, end }, "预付冲账", AP_VM_PREPAYAMOUNT, new Object[] { month }, "应付总账本期预付冲账");
		if (remark != null) {
			baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
			return false;
		}
		return true;
	}

	@Override
	public boolean ap_chk_l(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		if (baseDao.isDBSetting("autoCreateApBill") && baseDao.isDBSetting("useBillOutAP")) {
			String remark = compare(AP_ES_AMOUNT2, new Object[] { month, month }, "暂估", AP_VM_ESAMOUNT, new Object[] { month }, "应付总账本期暂估");
			if (remark != null) {
				baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
				return false;
			}
		} else {
			String remark = compare(AP_ES_AMOUNT, new Object[] { month, month }, "暂估", AP_VM_ESAMOUNT, new Object[] { month }, "应付总账本期暂估");
			if (remark != null) {
				baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean ap_chk_m(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		if (baseDao.isDBSetting("autoCreateApBill") && baseDao.isDBSetting("useBillOutAP")) {
			String remark = compare(AP_ES_ORDER_SUM2, new Object[] { month }, "暂估(采购价)", AP_VM_ESAMOUNTS, new Object[] { month },
					"应付总账本期暂估");
			if (remark != null) {
				baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
				return false;
			}
		} else {
			String remark = compare(AP_ES_ORDER_SUM, new Object[] { month }, "暂估(采购价)", AP_VM_ESAMOUNTS, new Object[] { month }, "应付总账本期暂估");
			if (remark != null) {
				baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean ap_chk_n(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		if (baseDao.isDBSetting("autoCreateApBill") && baseDao.isDBSetting("useBillOutAP")) {
			String remark = compare(AP_AB_COST_SUM2, new Object[] { month }, "开票数据中 涉及暂估的", AP_VM_ESINVOAMOUNT, new Object[] { month },
					"应付总账本期暂估转开票");
			if (remark != null) {
				baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
				return false;
			}
		} else {
			String remark = compare(AP_AB_COST_SUM, new Object[] { month, month }, "开票数据中 涉及暂估的", AP_VM_ESINVOAMOUNT,
					new Object[] { month }, "应付总账本期暂估转开票");
			if (remark != null) {
				baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean ap_chk_o(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String remark = compare(AP_AB_AMOUNT, new Object[] { start, end }, "开票数据中 涉及暂估的（采购价）", AP_VM_ESINVOAMOUNTS, new Object[] { month },
				"应付总账本期暂估转开票");
		if (remark != null) {
			baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
			return false;
		}
		return true;
	}

	@Override
	public boolean ap_chk_p(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String remark = compare(AP_AB_APAMOUNT, new Object[] { month }, "发票、其它应付单 ", AP_VM_AMOUNT, new Object[] { month }, "应付总账本期应付");
		if (remark != null) {
			baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
			return false;
		}
		return true;
	}

	@Override
	public boolean ap_chk_q(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String remark = compare(AP_PB_VMAMOUNT, new Object[] { start, end }, "付款单、退款单、结算单", AP_VM_PAYAMOUNT, new Object[] { month },
				"应付总账本期付款");
		if (remark != null) {
			baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
			return false;
		}
		return true;
	}

	/**
	 * 当月应付账款科目余额与应付总账应付期末余额是否一致
	 */
	@Override
	public boolean ap_chk_r(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String AP = "select '供应商编号：'||am_asscode, '应付', '应付系统：'||vm_endamount||',总账系统：'||(am_doubleendcredit-am_doubleenddebit)||',差额：'||(am_doubleendcredit-am_doubleenddebit-vm_endamount) from (select vm_vendcode,vm_vendname,vm_currency,sum(vm_endamount) vm_endamount from VendMonth where vm_yearmonth=? group by vm_vendcode,vm_vendname,vm_currency) vm left join (select am_asscode,am_currency,sum(am_doubleenddebit) am_doubleenddebit,sum(am_doubleendcredit) am_doubleendcredit from AssMonth where am_catecode in (@CODE) and am_yearmonth=? group by am_asscode,am_currency) am on vm.vm_vendcode = am.am_asscode and vm.vm_currency = am.am_currency left join Vendor ON vm.vm_vendcode = ve_code where abs(am_doubleendcredit-am_doubleenddebit-vm_endamount)>0.002";
		String AP2 = "select '供应商编号：'||am_asscode, '应付', '应付系统：'||vm_endamount||',总账系统：'||(am_doubleendcredit-am_doubleenddebit)||',差额：'||(am_doubleendcredit-am_doubleenddebit-vm_endamount) from (SELECT vm_vendcode,vm_vendname,vm_currency,sum(nvl(vm_endamount,0)-nvl(vm_esendamounts,0)) vm_endamount from VendMonth where vm_yearmonth=? group by vm_vendcode,vm_vendname,vm_currency) vm left join (select am_asscode,am_currency,sum(am_doubleenddebit) am_doubleenddebit,sum(am_doubleendcredit) am_doubleendcredit from AssMonth where am_catecode in (@CODE) and am_yearmonth=? group by am_asscode,am_currency) am on vm.vm_vendcode = am.am_asscode and vm.vm_currency = am.am_currency left join Vendor ON vm.vm_vendcode = ve_code where abs(am_doubleendcredit-am_doubleenddebit-vm_endamount)>0.002";
		clearByCaller(type);
		String[] arCates = baseDao.getDBSettingArray("MonthAccount!AP", "apCatecode");
		if (arCates != null && arCates.length > 0) {
			if (baseDao.isDBSetting("autoCreateApBill") && baseDao.isDBSetting("useBillOutAP")) {
				String remark = "当月应付账款科目余额与应付总账应付期末余额不一致";
				int count = isError(AP2.replace("@CODE", CollectionUtil.toSqlString(arCates)), new Object[] { month, month }, type,
						employee, remark, all);
				return count == 0;
			} else {
				String remark = "当月应付账款科目余额与应付总账应付期末余额不一致";
				int count = isError(AP.replace("@CODE", CollectionUtil.toSqlString(arCates)), new Object[] { month, month }, type,
						employee, remark, all);
				return count == 0;
			}
		}
		return true;
	}

	/**
	 * 当月预付账款科目余额与预付总账预付期末余额是否一致
	 */
	@Override
	public boolean ap_chk_s(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String AP_PRE = "select '供应商编号：'||am_asscode, am_catecode, '应付系统：'||vm_endamount||',总账系统：'||(am_doubleenddebit-am_doubleendcredit)||',差额：'||(am_doubleenddebit-am_doubleendcredit-vm_endamount) from (SELECT '预付' am_catecode, vendcode am_asscode, max(ve_name) am_assname, currency am_currency, sum(vm_prepayend) vm_endamount, sum(am_doubleenddebit) am_doubleenddebit, sum(am_doubleendcredit) am_doubleendcredit FROM ( SELECT (CASE WHEN NVL(vm_vendcode,' ')=' ' THEN am_asscode ELSE vm_vendcode END) vendcode, (CASE WHEN NVL(vm_vendname,' ')=' ' THEN am_assname ELSE vm_vendname END) vendname, (CASE WHEN NVL(vm_currency,' ')=' ' THEN am_currency ELSE vm_currency END) currency, (CASE WHEN NVL(vm_yearmonth,0)=0 THEN am_yearmonth ELSE vm_yearmonth END) yearmonth, NVL(vm_prepayend,0) vm_prepayend, NVL(am_doubleenddebit,0) am_doubleenddebit, NVL(am_doubleendcredit,0) am_doubleendcredit FROM VendMonth full join (select sum(NVL(am_doubleenddebit,0)) am_doubleenddebit,sum(NVL(am_doubleendcredit,0)) am_doubleendcredit,am_yearmonth,am_asscode,am_assname,am_currency from AssMonth where am_catecode in (@CODE) GROUP BY am_yearmonth,am_asscode,am_assname,am_currency) on vm_yearmonth = am_yearmonth AND vm_vendcode = am_asscode and vm_currency = am_currency ) LEFT JOIN Vendor ON vendcode = ve_code WHERE yearmonth =? group by vendcode, currency order by vendcode) where abs(am_doubleenddebit-am_doubleendcredit-vm_endamount)>0.002";
		clearByCaller(type);
		String[] preRecCates = baseDao.getDBSettingArray("MonthAccount!AP", "prePayCatecode");
		if (preRecCates != null && preRecCates.length > 0) {
			String remark = "当月预付账款科目余额与预付总账预付期末余额不一致";
			int count = isError(AP_PRE.replace("@CODE", CollectionUtil.toSqlString(preRecCates)), new Object[] { month }, type, employee,
					remark, all);
			return count == 0;
		}
		return true;
	}

	/**
	 * 当月应付暂估科目余额与应付总账应付暂估余额(采购价除税)是否一致
	 */
	@Override
	public boolean ap_chk_t(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String AP_ES = "select '供应商编号：'||am_asscode||'，币别：'||currency, am_catecode, '应付系统：'||vm_endamount||',总账系统：'||(am_doubleendcredit-am_doubleenddebit)||',差额：'||(am_doubleendcredit-am_doubleenddebit-vm_endamount) from (select '应付暂估' am_catecode, vendcode am_asscode, max(ve_name) am_assname, currency, sum(am_doubleenddebit) am_doubleenddebit, sum(am_doubleendcredit) am_doubleendcredit, sum(vm_esendamount) vm_endamount from (SELECT (CASE WHEN NVL(vm_currency, ' ') = ' ' THEN am_currency ELSE vm_currency END) currency, (CASE WHEN NVL(vm_vendcode, ' ') = ' ' THEN am_asscode ELSE vm_vendcode END) vendcode, (CASE WHEN NVL(vm_yearmonth, 0) = 0 THEN am_yearmonth ELSE vm_yearmonth END) yearmonth, NVL(am_doubleenddebit, 0) am_doubleenddebit, NVL(am_doubleendcredit, 0) am_doubleendcredit, NVL(vm_esendamount, 0) vm_esendamount FROM ((select sum(nvl(vm_esendamount,0)) vm_esendamount, vm_vendcode, vm_currency, vm_yearmonth from vendmonth group by vm_vendcode, vm_currency, vm_yearmonth) full join (select sum(NVL(am_doubleenddebit, 0)) am_doubleenddebit, sum(NVL(am_doubleendcredit, 0)) am_doubleendcredit, am_currency, am_asscode, am_yearmonth from assmonth where am_catecode in (@CODE) group by am_asscode, am_currency, am_yearmonth) on vm_yearmonth = am_yearmonth and vm_vendcode = am_asscode and vm_currency = am_currency)) left join vendor on vendcode=ve_code where yearmonth =? group by vendcode,currency order by vendcode) where abs(am_doubleendcredit-am_doubleenddebit-vm_endamount)>0.002";
		clearByCaller(type);
		String[] esCates = baseDao.getDBSettingArray("MonthAccount!AP", "esCatecode");
		if (esCates != null && esCates.length > 0) {
			String remark = "当月应付暂估科目余额与应付总账应付暂估余额(采购价除税)不一致";
			int count = isError(AP_ES.replace("@CODE", CollectionUtil.toSqlString(esCates)), new Object[] { month }, type, employee,
					remark, all);
			return count == 0;
		}
		return true;
	}

	/**
	 * 当月的 预付和应付同时有余额
	 */
	@Override
	public boolean ap_chk_u(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String ap1 = "select '供应商：'||vm_vendcode,'币别：'||vm_currency,'应付：'||vm_endamount||',预付：'||vm_prepayend from vendmonth where vm_yearmonth=? and nvl(vm_endamount,0)<>0 and nvl(vm_prepayend,0)<>0 ";
		String ap = "select '供应商：'||vm_vendcode,'币别：'||vm_currency,'应付：'||(vm_endamount-nvl(vm_esendamounts,0))||',预付：'||vm_prepayend from vendmonth where vm_yearmonth=? and nvl(vm_endamount,0)-nvl(vm_esendamounts,0)<>0 and nvl(vm_prepayend,0)<>0 ";
		String remark = "当月的 预付和应付同时有余额";
		clearByCaller(type);
		if (baseDao.isDBSetting("useBillOutAP")) {
			int count = isError(ap, new Object[] { month }, type, employee, remark, all);
			return count == 0;
		} else {
			int count = isError(ap1, new Object[] { month }, type, employee, remark, all);
			return count == 0;
		}
	}

	@Override
	public boolean fix_chk_a(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		Map<String, Object> map1 = getYearMonth("MONTH-A", null);
		Map<String, Object> map2 = getYearMonth("MONTH-F", null);
		boolean bool = String.valueOf(map1.get("PD_DETNO").toString()).equals(map2.get("PD_DETNO").toString());
		if (!bool) {
			baseDao.execute(getErrorSql("", "", type, employee.getEm_name(),
					"固定资产期间:" + map2.get("PD_DETNO") + "<br>总账期间:" + map1.get("PD_DETNO")));
		}
		return bool;
	}

	@Override
	public boolean fix_chk_b(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		boolean bool = true;
		clearByCaller(type);
		int count = baseDao.getCount("select count(*) from AssetsDepreciation where de_class='折旧单' and to_char(de_date,'yyyymm')=" + month);
		if (count < 1) {
			baseDao.execute(getErrorSql("", "", type, employee.getEm_name(), "未计提折旧"));
			bool = false;
		}
		if (count > 2) {
			baseDao.execute(getErrorSql("", "", type, employee.getEm_name(), "当前月份有多张折旧单"));
			bool = false;
		}
		return bool;
	}

	@Override
	public boolean fix_chk_c(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未审核";
		clearByCaller(type);
		int count = 0;
		count += isError("select ac_code,'卡片' from AssetsCard where ac_statuscode<>'AUDITED' and to_char(ac_date,'yyyymm')=?",
				new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean fix_chk_d(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未审核";
		clearByCaller(type);
		int count = 0;
		count += isError("select acc_code,'卡片变更单' from AssetsCardChange where acc_statuscode<>'AUDITED'", new Object[] {}, type, employee,
				remark, all);
		return count == 0;
	}

	@Override
	public boolean fix_chk_e(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未过账";
		clearByCaller(type);
		int count = 0;
		count += isError(
				"select de_code,de_class from AssetsDepreciation where de_class in ('资产增加单','资产减少单','折旧单') and de_statuscode<>'POSTED' and to_char(de_date,'yyyymm')=?",
				new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean fix_chk_f(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未生成凭证";
		clearByCaller(type);
		int count = 0;
		count += isError(
				"select ac_code,'卡片' from AssetsCard  where ac_statuscode='AUDITED' and to_char(ac_date,'yyyymm')=? and nvl(ac_vouchercode,' ')=' '",
				new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean fix_chk_g(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未生成凭证";
		clearByCaller(type);
		int count = 0;
		count += isError(
				"select de_code,de_class from AssetsDepreciation  where de_statuscode='POSTED' and to_char(de_date,'yyyymm')=? and nvl(de_vouchercode,' ')=' '",
				new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean fix_chk_h(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String[] fixCates = baseDao.getDBSettingArray("MonthAccount!AS", "fixCatecode");
		if (fixCates != null && fixCates.length > 0) {
			Double endOldCount = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select sum(round(nvl(ac_oldvalue,0),2)) amount from assetscard where to_char(ac_date,'yyyymm')<=? and ac_statuscode='AUDITED' and ac_accatecode in("
									+ CollectionUtil.toSqlString(fixCates) + ")", Double.class, month);
			Double endDiffCount = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select sum(round(case de_class when '资产减少单' then 1 else -1 end*nvl(dd_amount,0),2)) amount from assetscard left join AssetsDepreciationDetail on ac_code =dd_accode inner join  AssetsDepreciation on dd_deid = de_id where to_char(ac_date,'yyyymm')<=? and ac_statuscode='AUDITED' and to_char(de_date,'yyyymm')>? and de_statuscode = 'POSTED' and de_class in ('资产减少单','资产增加单') and ac_accatecode in("
									+ CollectionUtil.toSqlString(fixCates) + ")", Double.class, month, month);
			Double cmCount = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select nvl(sum(round(nvl(cm_enddebit,0),2))-sum(round(nvl(cm_endcredit,0),2)),0) from catemonth where cm_yearmonth =? and cm_catecode in ("
									+ CollectionUtil.toSqlString(fixCates) + ")", Double.class, month);
			if (NumberUtil.compare(
					NumberUtil.formatDouble((endOldCount == null ? 0 : endOldCount) + (endDiffCount == null ? 0 : endDiffCount), 2),
					NumberUtil.formatDouble(cmCount == null ? 0 : cmCount, 2)) != 0) {
				baseDao.execute(getErrorSql(
						"期间:" + month,
						"",
						type,
						employee.getEm_name(),
						"原值金额合计:" + NumberUtil.formatDouble(endOldCount + endDiffCount, 2) + "<br>固定资产科目的本期余额:"
								+ NumberUtil.formatDouble(cmCount, 2)));
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean fix_chk_i(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String[] deCates = baseDao.getDBSettingArray("MonthAccount!AS", "deCatecode");
		if (deCates != null && deCates.length > 0) {
			String sql1 = "select sum(ac_totaldepreciation) from AssetsCard where ac_statuscode = 'AUDITED' and to_char(ac_date,'yyyymm')<=?";
			String sql2 = "select sum(cm_enddebit + cm_endcredit) from catemonth where cm_yearmonth =? and cm_catecode in ("
					+ CollectionUtil.toSqlString(deCates) + ")";
			String sql3 = "select sum(dd_amount) from AssetsDepreciationDetail,AssetsDepreciation where dd_deid=de_id and to_char(de_date,'yyyymm')>? and de_statuscode='POSTED'";
			double sum1 = 0;
			double sum2 = 0;
			double sum3 = 0;
			SqlRowList rs1 = baseDao.queryForRowSet(sql1, new Object[] { month });
			SqlRowList rs2 = baseDao.queryForRowSet(sql2, new Object[] { month });
			SqlRowList rs3 = baseDao.queryForRowSet(sql3, new Object[] { month });
			if (rs1.next()) {
				sum1 = rs1.getGeneralDouble(1);
			}
			if (rs2.next()) {
				sum2 = rs2.getGeneralDouble(1);
			}
			if (rs3.next()) {
				sum3 = rs3.getGeneralDouble(1);
			}
			if (NumberUtil.compare((sum1 - sum3), sum2) != 0) {
				baseDao.execute(getErrorSql("期间:" + month, "", type, employee.getEm_name(), "累计折旧金额合计:" + (sum1 - sum3)
						+ "<br>总账累计折旧科目的本期余额:" + sum2));
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean fix_chk_j(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未存在于折旧单中";
		clearByCaller(type);
		int count = 0;
		count += isError(
				"select '卡片：'||ac_code,' ' from assetscard where ac_statuscode='AUDITED' AND to_char(ac_date,'yyyymm')<? and ac_usemonth>nvl(ac_ymonth,0) and nvl(ac_oldvalue,0)<>0 and ac_code not in (select dd_accode from AssetsDepreciationDetail left join AssetsDepreciation on dd_deid=de_id where de_statuscode='POSTED' and to_char(de_date,'yyyymm')=?)",
				new Object[] { month, month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean fix_chk_k(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		int count = 0;
		clearByCaller(type);
		String[] fixCatecode = baseDao.getDBSettingArray("MonthAccount!AS", "fixCatecode");
		if (fixCatecode != null && fixCatecode.length > 0) {
			String remark = "卡片手工制作的凭证";
			count += isError(
					"select '凭证:'||vo_code,'凭证号：'||vo_number from Voucher left join Voucherdetail on vd_void=vo_id where nvl(vo_source,' ')=' ' and vo_yearmonth=? and vd_catecode in ("
							+ CollectionUtil.toSqlString(fixCatecode) + ")", new Object[] { month }, type, employee, remark, all);
		}
		return count == 0;
	}

	@Override
	public boolean fix_chk_l(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		int count = 0;
		clearByCaller(type);
		String[] deCatecode = baseDao.getDBSettingArray("MonthAccount!AS", "deCatecode");
		if (deCatecode != null && deCatecode.length > 0) {
			String remark = "折旧单手工制作的凭证";
			count += isError(
					"select '凭证:'||vo_code,'凭证号：'||vo_number from Voucher left join Voucherdetail on vd_void=vo_id where nvl(vo_source,' ')=' ' and vo_yearmonth=? and vd_catecode in ("
							+ CollectionUtil.toSqlString(deCatecode) + ")", new Object[] { month }, type, employee, remark, all);
		}
		return count == 0;
	}

	// *************************银行现金对账检查************************
	// 1当月银行现金单据是否全部记账
	@Override
	public boolean gs_chk_a(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未记账";
		clearByCaller(type);
		int count = 0;
		count += isError("select ar_code,ar_status from accountregister where to_char(ar_date,'yyyymm')=? and ar_statuscode<>'POSTED'",
				new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	// 2当月银行现金单据是否全部做了凭证
	@Override
	public boolean gs_chk_b(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未生成凭证";
		clearByCaller(type);
		int count = 0;
		count += isError(
				"select ar_code,ar_status from accountregister where to_char(ar_date,'yyyymm')=? and nvl(ar_statuscode,' ')='POSTED' and nvl(ar_type,' ') in ('其它收款','其它付款','费用','转存') and nvl(ar_vouchercode,' ')=' '",
				new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	// 3银行现金余额是否出现负数
	@Override
	public boolean gs_chk_c(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "出现负数";
		clearByCaller(type);
		int count = 0;
		count += isError(
				"select '科目号:'||ca_code,'期间：'||am_yearmonth,'科目余额：'||am_nowbalance from ALMonth left join category on am_accountcode=ca_id where nvl(am_nowbalance,0)<0 and am_yearmonth=?",
				new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	// 4票据资金系统期间与总账期间是否一致
	@Override
	public boolean gs_chk_d(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		Map<String, Object> map1 = getYearMonth("MONTH-A", null);
		Map<String, Object> map2 = getYearMonth("MONTH-B", null);
		boolean bool = String.valueOf(map1.get("PD_DETNO").toString()).equals(map2.get("PD_DETNO").toString());
		if (!bool) {
			baseDao.execute(getErrorSql("", "", type, employee.getEm_name(),
					"票据期间:" + map2.get("PD_DETNO") + "<br>总账期间:" + map1.get("PD_DETNO")));
		}
		return bool;
	}

	// 5预收款、预收退款类型的银行登记关联的预收款、预收退款单是否存在、是否已记账5
	@Override
	public boolean gs_chk_e(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未记账";
		clearByCaller(type);
		int count = 0;
		count += isError(
				"select ar_code,ar_type,ar_type||'单号：'||ar_checkno from AccountRegister where to_char(ar_date,'yyyymm')=? and ar_statuscode='POSTED' and ar_type in ('预收款','预收退款单') and nvl(ar_checkno,' ')<>' ' and nvl(ar_checkno,' ') not in (select pr_code from PreRec where pr_statuscode='POSTED')",
				new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	// 6应收款、应收退款类型的银行登记关联的收款单、收款退款单是否存在、是否已记账6
	@Override
	public boolean gs_chk_f(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未记账";
		clearByCaller(type);
		int count = 0;
		count += isError(
				"select ar_code,ar_type,ar_type||'单号：'||ar_checkno from AccountRegister where to_char(ar_date,'yyyymm')=? and ar_statuscode='POSTED' and ar_type in ('应收款','应收退款') and nvl(ar_checkno,' ')<>' ' and nvl(ar_checkno,' ') not in (select rb_code from RecBalance where rb_statuscode='POSTED')",
				new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	// 7预付款、预付退款类型的银行登记关联的预付款、预付退款单是否存在、是否已记账
	@Override
	public boolean gs_chk_g(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未记账";
		clearByCaller(type);
		int count = 0;
		count += isError(
				"select ar_code,ar_type,ar_type||'单号：'||ar_checkno from AccountRegister where to_char(ar_date,'yyyymm')=? and ar_statuscode='POSTED' and ar_type in ('预付款','预付退款') and nvl(ar_checkno,' ')<>' ' and nvl(ar_checkno,' ') not in (select pp_code from PrePay where pp_statuscode='POSTED')",
				new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	// 8应付款、应付退款类型的银行登记关联的付款单、付款退款单是否存在、是否已记账
	@Override
	public boolean gs_chk_h(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未记账";
		clearByCaller(type);
		int count = 0;
		count += isError(
				"select ar_code,ar_type,ar_type||'单号：'||ar_checkno from AccountRegister where to_char(ar_date,'yyyymm')=? and ar_statuscode='POSTED' and ar_type in ('应付款','应付退款') and nvl(ar_checkno,' ')<>' ' and nvl(ar_checkno,' ') not in (select pb_code from PayBalance where pb_statuscode='POSTED')",
				new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	// 9转存类型的银行登记是否平衡
	@Override
	public boolean gs_chk_i(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String sql1 = "select count(*) from AccountRegister where to_char(ar_date,'yyyymm')=? and ar_statuscode='POSTED' and ar_type ='转存'";
		String sql2 = "select count(*) from AccountRegister where to_char(ar_date,'yyyymm')=? and ar_statuscode='POSTED' and ar_type ='自动转存' ";

		double sum1 = 0;
		double sum2 = 0;
		SqlRowList rs1 = baseDao.queryForRowSet(sql1, new Object[] { month });
		SqlRowList rs2 = baseDao.queryForRowSet(sql2, new Object[] { month });

		if (rs1.next()) {
			sum1 = rs1.getGeneralDouble(1);

		}
		if (rs2.next()) {
			sum2 = rs2.getGeneralDouble(1);
		}
		if (NumberUtil.compare(sum1, sum2) != 0) {
			baseDao.execute(getErrorSql("存款类型的银行登记不平衡", "期间:" + month, type, employee.getEm_name(), "转存:" + sum1 + "<br>自动转存:" + sum2));
			return false;
		}
		return true;
	}

	// 10银行各账户余额(银行存款总账查询)与总账对应科目原币余额是否一致
	@Override
	public boolean gs_chk_j(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		// String remark = "不一致";
		String sql = "select distinct (case when NVL(am_catecode,' ')=' ' then cm_catecode else am_catecode end),round(NVL(am_nowbalance,0),2),round(NVL(cm_doubleenddebit-cm_doubleendcredit,0),2),"
				+ "(case when NVL(am_catename,' ')=' ' then cm_catename else am_catename end) "
				+ "from (select ca_code am_catecode,am_nowbalance,am_yearmonth,ca_name am_catename from Almonth left join Category on Am_Accountcode=Ca_Id where am_yearmonth=?) full join "
				+ "(select cm_catecode,cm_yearmonth,cm_doubleenddebit,cm_doubleendcredit,ca_name cm_catename from catemonth left join category on cm_catecode=ca_code where cm_yearmonth=? "
				+ "And nvl(ca_iscashbank,0)<>0 and nvl(ca_isleaf,0)<>0 and nvl(ca_statuscode,' ')<>'DISABLE') on am_catecode=cm_catecode and am_yearmonth=cm_yearmonth "
				+ "where cm_yearmonth=? AND round(NVL(am_nowbalance,0),2)<>round(NVL(cm_doubleenddebit-cm_doubleendcredit,0),2)";
		SqlRowList rs = baseDao.queryForRowSet(sql, new Object[] { month, month, month });
		if (rs.hasNext()) {
			List<String> sqls = new ArrayList<String>();
			while (rs.next()) {
				sqls.add(getErrorSql(rs.getString(1), rs.getString(4), type, employee.getEm_name(), "期末余额:" + rs.getGeneralDouble(2)
						+ "<br>银行总账余额:" + rs.getGeneralDouble(3)));
			}
			baseDao.execute(sqls);
			return false;
		} else {
			return true;
		}

	}

	// 11 所有应付票据是否已审核
	@Override
	public boolean gs_chk_k(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未审核";
		clearByCaller(type);
		int count = 0;
		count += isError("select bap_code,'应付票据' from BillAP where to_char(bap_date,'yyyymm')=? and bap_statuscode<>'AUDITED'",
				new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	// 12所有应付票据异动单是否已过账
	@Override
	public boolean gs_chk_l(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未过账";
		clearByCaller(type);
		int count = 0;
		count += isError("select bpc_code,'应付票据异动单' from BillAPChange where to_char(bpc_date,'yyyymm')=? and bpc_statuscode<>'POSTED'",
				new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	// 13所有应收票据是否已审核
	@Override
	public boolean gs_chk_m(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未审核";
		clearByCaller(type);
		int count = 0;
		count += isError("select bar_code,'应收票据' from BillAR where to_char(bar_date,'yyyymm')=? and bar_statuscode<>'AUDITED'",
				new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	// 14 所有应收票据异动单是否已过账
	@Override
	public boolean gs_chk_n(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未过账";
		clearByCaller(type);
		int count = 0;
		count += isError("select brc_code,'应收票据异动单' from BillARChange where to_char(brc_date,'yyyymm')=? and brc_statuscode<>'POSTED'",
				new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	private static final String GS_CHK_O1 = "select '应收票据', bar_code, rb_kind||'单号：'||rb_code from BillAR, RecBalance where bar_id=rb_sourceid and rb_source='应收票据' and to_char(bar_date,'yyyymm')=? and bar_statuscode='AUDITED' and rb_statuscode<>'POSTED'";
	private static final String GS_CHK_O2 = "select '应收票据', bar_code, pr_kind||'单号：'||pr_code from BillAR, PreRec where bar_id=pr_sourceid and pr_source='应收票据' and pr_statuscode<>'POSTED' and to_char(bar_date,'yyyymm')=? and bar_statuscode='AUDITED'";

	// 15应收票据是否有关联的收款单或预收单，是否已过账
	@Override
	public boolean gs_chk_o(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未过账";
		clearByCaller(type);
		int count = 0;
		count += isError(GS_CHK_O1, new Object[] { month }, type, employee, remark, all);
		count += isError(GS_CHK_O2, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	private static final String GS_CHK_P1 = "select '应付票据', bap_code, pb_kind||'单号：'||pb_code from BillAP, PayBalance where bap_id=pb_sourceid and pb_source='应付票据' and to_char(bap_date,'yyyymm')=? and bap_statuscode='AUDITED' and pb_statuscode<>'POSTED'";
	private static final String GS_CHK_P2 = "select '应付票据', bap_code, pp_type||'单号：'||pp_code from BillAP, PrePay where bap_id=pp_sourceid and pp_source='应付票据' and pp_statuscode<>'POSTED' and to_char(bap_date,'yyyymm')=? and bap_statuscode='AUDITED'";

	// 16应付票据是否有关联的付款单或预付单，是否已过账
	@Override
	public boolean gs_chk_p(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未记账";
		clearByCaller(type);
		int count = 0;
		count += isError(GS_CHK_P1, new Object[] { month }, type, employee, remark, all);
		count += isError(GS_CHK_P2, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	// 17应收票据异动类型为收款、贴现的，是否有关联的银行登记，是否已记账
	@Override
	public boolean gs_chk_q(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未记账";
		clearByCaller(type);
		int count = 0;
		count += isError(
				"select brc_kind,brc_code,'银行登记单号：'||ar_code from BillARChange,AccountRegister where brc_code=ar_source and ar_sourcetype='应收票据异动' and to_char(brc_date,'yyyymm')=? and brc_statuscode='POSTED' and brc_kind in ('收款','贴现') and ar_statuscode<>'POSTED'",
				new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	private static final String GS_CHK_R1 = "select brc_kind, brc_code, pb_kind||'单号：'||pb_code from BillARChange, PayBalance where brc_id=pb_sourceid and pb_source ='背书转让' and to_char(brc_date,'yyyymm')=? and pb_statuscode<>'POSTED' and brc_statuscode='POSTED'";
	private static final String GS_CHK_R2 = "select brc_kind, brc_code, pp_type||'单号：'||pp_code from BillARChange, PrePay where brc_id=pp_sourceid and pp_source='背书转让' and to_char(brc_date,'yyyymm')=? and pp_statuscode<>'POSTED' and brc_statuscode='POSTED'";

	// 18应收票据异动类型为背书转让的，是否有关联的付款单或预付单，是否已过账 --------待确认，暂不写
	@Override
	public boolean gs_chk_r(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未过账";
		clearByCaller(type);
		int count = 0;
		count += isError(GS_CHK_R1, new Object[] { month }, type, employee, remark, all);
		count += isError(GS_CHK_R2, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	// 19应付票据异动类型为兑现的，是否有关联的银行登记，是否已记账
	@Override
	public boolean gs_chk_s(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未记账";
		clearByCaller(type);
		int count = 0;
		count += isError(
				"select bpc_code,'应付票据异动' from BillAPChange where to_char(bpc_date,'yyyymm')=? and bpc_statuscode='POSTED' and bpc_kind in ('兑现') and bpc_code not in (select ar_source from accountregister where ar_type='应付票据付款' and ar_statuscode='POSTED')",
				new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	// 当月应付票据科目余额与应付票据票面余额是否一致
	@Override
	public boolean gs_chk_t(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String[] bapCates = baseDao.getDBSettingArray("CheckAccount!GS", "bapCatecode");
		if (bapCates != null && bapCates.length > 0) {
			String sql = "select vendcode,currency,amount1,amount2,ve_name,amount3 from (select "
					+ " (case when NVL(bap_vendcode,' ')=' ' then  am_asscode else bap_vendcode end) vendcode, "
					+ " (case when NVL(bap_currency,' ')=' ' then  am_currency else bap_currency end) currency, "
					+ " NVL(amount1,0) amount1, "
					+ "  NVL(amount2,0) amount2, "
					+ "  NVL(amount3,0) amount3 "
					+ "    from (select bap_vendcode,bap_currency,sum(bap_leftamount) amount1 from BillAP where bap_statuscode='AUDITED' and to_char(bap_date,'yyyymm')<=? group by bap_vendcode,bap_currency) "
					+ " full join (select am_asscode,am_currency,sum(am_doubleendcredit-am_doubleenddebit) amount2 from assmonth where am_yearmonth= ? and am_catecode in ("
					+ CollectionUtil.toSqlString(bapCates)
					+ ") group by am_asscode,am_currency) on am_asscode=bap_vendcode and bap_currency = am_currency"
					+ " left join (select bpd_vendcode, bpd_catecurrency, sum(bpd_amount) amount3 from BillAPChange,BillAPChangeDetail where bpc_id=bpd_bpcid and bpc_statuscode='POSTED' and to_char(bpc_date,'yyyymm')>? and exists (select 1 from billap where bpd_bapid=bap_id and to_char(bap_date,'yyyymm')<=?) group by bpd_vendcode,bpd_catecurrency)"
					+ " on  bpd_vendcode=bap_vendcode and bpd_catecurrency=bap_currency)"
					+ " left join vendor on vendcode=ve_code where abs(amount1+amount3-amount2) > 1";
			SqlRowList rs = baseDao.queryForRowSet(sql, new Object[] { month, month, month, month });
			if (rs.hasNext()) {
				List<String> list = new ArrayList<String>();
				while (rs.next()) {
					list.add(getErrorSql("供应商号:" + rs.getGeneralString(1), "币别:" + rs.getGeneralString(2), type, employee.getEm_name(),
							"票面余额合计:" + (rs.getGeneralDouble(3) + rs.getGeneralDouble(6)) + "<br>期末余额:" + rs.getGeneralString(4)
									+ "<br>供应商名称:" + rs.getGeneralString(5)));
				}
				baseDao.execute(list);
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	// 当月应收票据科目余额与应收票据票面余额是否一致
	@Override
	public boolean gs_chk_u(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String[] barCates = baseDao.getDBSettingArray("CheckAccount!GS", "barCatecode");
		if (barCates != null && barCates.length > 0) {
			String sql = "select custcode,currency,amount1,amount2,cu_name,amount3 from (select "
					+ " (case when NVL(bar_custcode,' ')=' ' then  am_asscode else bar_custcode end) custcode, "
					+ "  (case when NVL(bar_currency,' ')=' ' then  am_currency else bar_currency end) currency, "
					+ " NVL(amount1,0) amount1, "
					+ " NVL(amount2,0) amount2, "
					+ "  NVL(amount3,0) amount3 "
					+ " from (select bar_custcode,bar_currency,sum(bar_leftamount) amount1 from BillAR  where bar_statuscode='AUDITED' and to_char(bar_date,'yyyymm')<=? group by bar_custcode,bar_currency) "
					+ " full join (select am_asscode,am_currency,sum(am_doubleenddebit-am_doubleendcredit) amount2 from assmonth where am_yearmonth= ? and am_catecode in ("
					+ CollectionUtil.toSqlString(barCates)
					+ ") group by am_asscode,am_currency "
					+ " ) on am_asscode=bar_custcode and bar_currency = am_currency"
					+ " left join (select brd_custcode, brd_catecurrency, sum(brd_amount) amount3 from BillARChange,BillARChangeDetail where brc_id=brd_brcid and brc_statuscode='POSTED' and to_char(brc_date,'yyyymm')>? and exists (select 1 from billar where brd_barid=bar_id and to_char(bar_date,'yyyymm')<=?) group by brd_custcode,brd_catecurrency)"
					+ " on  brd_custcode=bar_custcode and brd_catecurrency=bar_currency)"
					+ " left join Customer on custcode=cu_code where abs(amount1+amount3-amount2) > 1";
			SqlRowList rs = baseDao.queryForRowSet(sql, new Object[] { month, month, month, month });
			if (rs.hasNext()) {
				List<String> list = new ArrayList<String>();
				while (rs.next()) {
					list.add(getErrorSql("客户编号:" + rs.getGeneralString(1), "币别:" + rs.getGeneralString(2), type, employee.getEm_name(),
							"票面余额合计:" + (rs.getGeneralDouble(3) + rs.getGeneralDouble(6)) + "<br>期末余额:" + rs.getGeneralString(4)
									+ "<br>客户名称:" + rs.getGeneralString(5)));
				}
				baseDao.execute(list);

				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	private static final String GS_CHK_V1 = "select bpc_kind, bpc_code, pb_kind||'单号：'||pb_code from BillAPChange, PayBalance where bpc_id=pb_sourceid and pb_source in ('应付票据退票','应付票据作废') and to_char(bpc_date,'yyyymm')=? and pb_statuscode<>'POSTED' and bpc_statuscode='POSTED'";
	private static final String GS_CHK_V2 = "select bpc_kind, bpc_code, pp_type||'单号：'||pp_code from BillAPChange, PrePay where bpc_id=pp_sourceid and pp_source in ('应付票据退票','应付票据作废') and to_char(bpc_date,'yyyymm')=? and pp_statuscode<>'POSTED' and bpc_statuscode='POSTED'";

	// 22应付票据异动类型为退票、作废的,是否有关联的应付退款单、预付退款单,是否已记账
	@Override
	public boolean gs_chk_v(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未过账";
		clearByCaller(type);
		int count = 0;
		count += isError(GS_CHK_V1, new Object[] { month }, type, employee, remark, all);
		count += isError(GS_CHK_V2, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	private static final String GS_CHK_W1 = "select brc_kind, brc_code, rb_kind||'单号：'||rb_code from BillARChange, RecBalance where brc_id=rb_sourceid and rb_source in ('应收票据退票','应收票据作废') and to_char(brc_date,'yyyymm')=? and rb_statuscode<>'POSTED' and brc_statuscode='POSTED'";
	private static final String GS_CHK_W2 = "select brc_kind, brc_code, pr_kind||'单号：'||pr_code from BillARChange, PreRec where brc_id=pr_sourceid and pr_source in ('应收票据退票','应收票据作废') and to_char(brc_date,'yyyymm')=? and pr_statuscode<>'POSTED' and brc_statuscode='POSTED'";

	// 23应收票据异动类型为退票、作废的，是否有关联的应收退款单、预收退款单,是否已记账
	@Override
	public boolean gs_chk_w(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未过账";
		clearByCaller(type);
		int count = 0;
		count += isError(GS_CHK_W1, new Object[] { month }, type, employee, remark, all);
		count += isError(GS_CHK_W2, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	// ******************************总账******************************

	@Override
	public boolean gla_chk_a(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "未记账";
		clearByCaller(type);
		int count = 0;
		count += isError(
				"select vo_code,'凭证号（'||vo_number||'）','录入人：'||vo_recordman||',凭证状态：'||vo_status from Voucher where vo_yearmonth=? and vo_statuscode<>'ACCOUNT'",
				new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean gla_chk_b(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "凭证号重复";
		clearByCaller(type);
		int count = 0;
		count += isError(
				"select vo_code,'凭证号（'||vo_number||'）','录入人：'||vo_recordman||',凭证字：'||vo_lead from Voucher where vo_lead||vo_number in (select vo_lead||vo_number from Voucher where vo_yearmonth=? group by vo_lead||vo_number having count(*)>1) and vo_yearmonth=?",
				new Object[] { month, month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean gla_chk_c(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		boolean err = false;
		SqlRowList rs = baseDao.queryForRowSet("select distinct vo_lead from voucher where vo_yearmonth=?", month);
		while (rs.next()) {
			String lead = rs.getString(1);
			if (lead == null)
				lead = " ";
			String remark = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(num) from (select column_value as num from table(rowinterval(1, nvl((select max(vo_number) from voucher where vo_yearmonth=? and nvl(vo_lead,' ')=?), 0)))) where num not in (select vo_number from voucher where vo_yearmonth=? and nvl(vo_lead,' ')=?)",
							String.class, month, lead, month, lead);
			if (remark != null) {
				baseDao.execute(getErrorSql("期间:" + month, lead, type, employee.getEm_name(), "断号：" + remark));
				err = true;
			}
		}
		return !err;
	}

	@Override
	public boolean gla_chk_d(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = baseDao
				.getCount("select count(1) from (select vo_code  from voucher,voucherdetail where vd_void=vo_id and vo_yearmonth=" + month
						+ " and vo_statuscode='ACCOUNT' and vd_explanation='结转制造费用' group by vo_code)");
		if (count > 0) {
			return true;
		} else {
			baseDao.execute(getErrorSql("期间:" + month, "", type, employee.getEm_name(), "没有摘要为[结转制造费用]的凭证"));
			return false;
		}
	}

	@Override
	public boolean gla_chk_e(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = baseDao
				.getCount("select count(1) from (select vo_code  from voucher,voucherdetail where vd_void=vo_id and vo_yearmonth=" + month
						+ " and vo_statuscode='ACCOUNT' and vd_explanation='汇兑损益' group by vo_code)");
		if (count > 0) {
			return true;
		} else {
			baseDao.execute(getErrorSql("期间:" + month, "", type, employee.getEm_name(), "没有摘要为[汇兑损益]的凭证"));
			return false;
		}
	}

	@Override
	public boolean gla_chk_f(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = baseDao
				.getCount("select count(1) from (select vo_code  from voucher,voucherdetail where vd_void=vo_id and vo_yearmonth=" + month
						+ " and vo_statuscode='ACCOUNT' and vd_explanation='结转损益' group by vo_code)");
		if (count > 0) {
			return true;
		} else {
			baseDao.execute(getErrorSql("期间:" + month, "", type, employee.getEm_name(), "没有摘要为[结转损益]的凭证"));
			return false;
		}
	}

	@Override
	public boolean gla_chk_g(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String sql = "select '期间:'||cm_yearmonth, '科目:'||cm_catecode, '借方余额:'||cm_enddebit||',贷方余额:'||cm_endcredit from catemonth,category where cm_catecode=ca_code and ca_class='损益' and cm_yearmonth=? and (nvl(cm_enddebit,0)<>0 or nvl(cm_endcredit,0)<>0)";
		clearByCaller(type);
		String remark = "当月余额不全部为0";
		int count = 0;
		count += isError(sql, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean gla_chk_h(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String sql = "select ca_description, '科目：'||cm_catecode, '科目方向：'||balancetype||'，余额方向：'||cm_debitorcredit from (select cm_yearmonth,cm_catecode,ca_description, (case when nvl(ca_balancetype,0)=0 then '借' when nvl(ca_balancetype,0)=1 then '贷' end) balancetype, cm_debitorcredit from catemonth,category where cm_catecode=ca_code and ca_class<>'损益' and cm_yearmonth=? and nvl(cm_debitorcredit,' ') in ('借','贷') and nvl(ca_balancetype,0)<>2 and (case when nvl(ca_balancetype,0)=0 then '借' when nvl(ca_balancetype,0)=1 then '贷' end)<>cm_debitorcredit)";
		clearByCaller(type);
		String remark = "科目余额方向不一致";
		int count = 0;
		count += isError(sql, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean gla_chk_i(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String sql1 = "select '凭证编号：'||vo_code, '科目：'||vd_catecode, '科目方向：借，行号：'||vd_detno from voucher,voucherdetail,category where vo_id=vd_void and vd_catecode=ca_code and ca_class like '%损益%' and vo_yearmonth=? and nvl(ca_type,0)=0 and nvl(vd_credit,0)>0 and ca_code in (@CATE)";
		String sql2 = "select '凭证编号：'||vo_code, '科目：'||vd_catecode, '科目方向：贷，行号：'||vd_detno from voucher,voucherdetail,category where vo_id=vd_void and vd_catecode=ca_code and ca_class like '%损益%' and vo_yearmonth=? and nvl(ca_type,0)=1 and nvl(vd_debit,0)>0 and ca_code in (@CATE)";
		String[] cateCodes = baseDao.getDBSettingArray("MakeFeeClose", "makeCatecode");
		if (cateCodes == null || cateCodes.length == 0) {
			BaseUtil.showError("制造费用科目未设置.");
		}
		String cateStr = CollectionUtil.toSqlString(cateCodes);
		clearByCaller(type);
		String remark = "凭证科目科目性质不一致";
		int count = 0;
		count += isError(sql1.replace("@CATE", cateStr), new Object[] { month }, type, employee, remark, all);
		count += isError(sql2.replace("@CATE", cateStr), new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean gla_chk_j(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String sql1 = "select '应收',pd_detno from periodsdetail where pd_code='MONTH-C' and pd_detno=? and nvl(pd_status,0)<>99";
		String sql2 = "select '应付',pd_detno from periodsdetail where pd_code='MONTH-V' and pd_detno=? and nvl(pd_status,0)<>99";
		String sql3 = "select '固定资产',pd_detno from periodsdetail where pd_code='MONTH-F' and pd_detno=? and nvl(pd_status,0)<>99";
		String sql4 = "select '票据资金',pd_detno from periodsdetail where pd_code='MONTH-B' and pd_detno=? and nvl(pd_status,0)<>99";
		String sql5 = "select '库存',pd_detno from periodsdetail where pd_code='MONTH-P' and pd_detno=? and nvl(pd_status,0)<>99";
		String sql6 = "select '成本',pd_detno from periodsdetail where pd_code='MONTH-T' and pd_detno=? and nvl(pd_status,0)<>99";
		clearByCaller(type);
		String remark = "模块未结账";
		int count = 0;
		count += isError(sql1, new Object[] { month }, type, employee, remark, all);
		count += isError(sql2, new Object[] { month }, type, employee, remark, all);
		count += isError(sql3, new Object[] { month }, type, employee, remark, all);
		count += isError(sql4, new Object[] { month }, type, employee, remark, all);
		count += isError(sql5, new Object[] { month }, type, employee, remark, all);
		count += isError(sql6, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	// *************************成本期末对账检查*************************
	@Override
	public boolean cost_chk_a(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String cost = "select 'RMB', round(sum((cd_nowendqty-nvl(cd_nowendqtynocost,0))*cd_fee4),2) from costdetail where cd_yearmonth=?";
		String ManuFactFee = "select 'RMB', nvl(sum(nvl(mf_laboramount,0)),0) from ManuFactFee where mf_yearmonth=?";
		String remark = compare(cost, new Object[] { month }, "成本表", ManuFactFee, new Object[] { month }, "直接人工制造费用维护");
		if (remark != null) {
			baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
			return false;
		}
		return true;
	}

	@Override
	public boolean cost_chk_b(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String cost = "select 'RMB', round(sum((cd_nowendqty-nvl(cd_nowendqtynocost,0))*cd_fee2),2) from costdetail where cd_yearmonth=?";
		String ManuFactFee = "select 'RMB', nvl(sum(nvl(mf_amount,0)),0) from ManuFactFee where mf_yearmonth=?";
		String remark = compare(cost, new Object[] { month }, "成本表", ManuFactFee, new Object[] { month }, "直接人工制造费用维护");
		if (remark != null) {
			baseDao.execute(getErrorSql("期间：" + month, "", type, employee.getEm_name(), remark));
			return false;
		}
		return true;
	}

	@Override
	public boolean cost_chk_c(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String[] cates = baseDao.getDBSettingArray("CheckAccount!COST", "DirectMaterialsCatecode");
		String cost = "select 'RMB', nvl(sum(nvl(cd_endcostamount,0)+nvl(cd_endscrapamount,0)),0) from costdetail where cd_yearmonth=? and cd_maketype='MAKE'";
		String ManuFactFee = "select 'RMB', nvl(sum(nvl(cm_enddebit,0)-nvl(cm_endcredit,0)),0) from CateMonth where cm_yearmonth=? and cm_catecode in (@CODES)";
		if (cates != null && cates.length > 0) {
			String remark = compare(cost, new Object[] { month }, "成本表", ManuFactFee.replace("@CODES", CollectionUtil.toSqlString(cates)),
					new Object[] { month }, "生产成本-直接材料科目余额");
			if (remark != null) {
				baseDao.execute(getErrorSql("期间：" + month, "科目号：" + CollectionUtil.toString(cates), type, employee.getEm_name(), remark));
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean cost_chk_d(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String[] cates = baseDao.getDBSettingArray("CheckAccount!COST", "ProcessingCatecode");
		String cost = "select 'RMB', nvl(sum(nvl(cd_endcostamount,0)+nvl(cd_endscrapamount,0)),0) from costdetail where cd_yearmonth=? and cd_maketype='OS'";
		String ManuFactFee = "select 'RMB', nvl(sum(nvl(cm_enddebit,0)-nvl(cm_endcredit,0)),0) from CateMonth where cm_yearmonth=? and cm_catecode in (@CODES)";
		if (cates != null && cates.length > 0) {
			String remark = compare(cost, new Object[] { month }, "成本表", ManuFactFee.replace("@CODES", CollectionUtil.toSqlString(cates)),
					new Object[] { month }, "委托加工物料科目余额");
			if (remark != null) {
				baseDao.execute(getErrorSql("期间：" + month, "科目号：" + CollectionUtil.toString(cates), type, employee.getEm_name(), remark));
				return false;
			}
		}
		return true;
	}

	@Override
	public List<Map<String, Object>> getCheckItems(String module, Boolean isCheck) {
		List<Map<String, Object>> checkAccounts = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		SqlRowList rs = baseDao.queryForRowSet("select * from SYS_CHECKITEM where module_=? order by detno_", module);
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("code_", rs.getString("code_"));
			map.put("module_", rs.getString("module_"));
			map.put("detno_", rs.getString("detno_"));
			map.put("title_", rs.getString("title_"));
			map.put("execute_", rs.getString("execute_"));
			String billoutmode = rs.getString("billoutmode_");
			String code = billoutmode.replace("not", "use");
			// 根据系统参数配置决定是否启用检测项
			if (!"all".equals(billoutmode)) {
				if (("AR".equals(module) || "AP".equals(module)) && !billoutmode.endsWith(module)) {
					billoutmode = billoutmode.substring(0, billoutmode.length() - 2) + module;
					code = billoutmode.replace("not", "use");
					baseDao.updateByCondition("SYS_CHECKITEM", "billoutmode_='" + billoutmode + "'", "code_='" + rs.getString("code_")
							+ "'");
				}
				if (billoutmode.startsWith("use") && baseDao.isDBSetting(code)
						|| (!billoutmode.startsWith("use") && !baseDao.isDBSetting(code))) {
					map.put("enable_", true);
					baseDao.updateByCondition("SYS_CHECKITEM", "enable_=1", "code_='" + rs.getString("code_") + "'");
				} else {
					map.put("enable_", false);
					baseDao.updateByCondition("SYS_CHECKITEM", "enable_=0", "code_='" + rs.getString("code_") + "'");
				}
			} else {
				map.put("enable_", rs.getInt("enable_") == 1);
			}
			map.put("man_", rs.getString("man_"));
			map.put("billoutmode_", billoutmode);
			Timestamp time = rs.getTimestamp("date_");
			String date = null;
			try {
				date = DateUtil.parseDateToString(new Date(time.getTime()), Constant.YMD);
				map.put("date_", date);
			} catch (Exception e) {
				e.printStackTrace();
			}
			checkAccounts.add(map);
		}
		if (isCheck != null && isCheck) {
			for (Map<String, Object> map : checkAccounts) {
				if (!(Boolean) map.get("enable_")) {
					datas.add(map);
				}
			}
		}
		checkAccounts.removeAll(datas);

		return checkAccounts;
	}

	@Override
	public Map<String, Object> saveCheckItem(Employee employee, String CheckItem) {
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(CheckItem);
		Map<String, Object> data = new HashMap<String, Object>();
		String sql = null;
		if (map.get("enable_") == null || "".equals(map.get("enable_")) || "null".equals(map.get("enable_"))
				|| !Boolean.parseBoolean(map.get("enable_").toString())) {
			map.put("enable_", 0);
		} else {
			map.put("enable_", 1);
		}

		String billoutmode = (String) map.get("billoutmode_");
		String code = billoutmode.replace("not", "use");
		boolean checked = Integer.parseInt(map.get("enable_").toString()) == 1;
		// 根据系统参数配置判断是否启用检测项
		if (!"all".equals(billoutmode)) {
			if (billoutmode.startsWith("use") && baseDao.isDBSetting(code)
					|| (!billoutmode.startsWith("use") && !baseDao.isDBSetting(code))) {
				if (!checked) {
					BaseUtil.showError("序号" + map.get("detno_") + "开票记录模式和系统参数设置一致，不允许关闭该检测项！");
				}
			} else {
				if (checked) {
					BaseUtil.showError("序号" + map.get("detno_") + "开票记录模式和系统参数设置不一致，不允许启用该检测项！");
				}
			}
		}
		map.put("man_", employee.getEm_name());
		map.put("date_", DateUtil.format(new Date(), Constant.YMD));
		String Code = baseDao.sGetMaxNumber("SYS_CHECKITEM", 2);
		map.put("code_", Code);
		sql = SqlUtil.getInsertSqlByMap(map, "SYS_CHECKITEM");
		try {
			baseDao.execute(sql);
		} catch (Exception e) {
			BaseUtil.showError("保存失败，错误：" + e.getMessage());
		}
		SqlRowList rs = baseDao.queryForRowSet("select * from SYS_CHECKITEM where code_=?", Code);
		while (rs.next()) {
			data.put("code_", rs.getString("code_"));
			data.put("module_", rs.getString("module_"));
			data.put("detno_", rs.getString("detno_"));
			data.put("title_", rs.getString("title_"));
			data.put("execute_", rs.getString("execute_"));
			data.put("enable_", rs.getInt("enable_") == 1);
			data.put("man_", rs.getString("man_"));
			data.put("billoutmode_", rs.getString("billoutmode_"));
			Timestamp time = rs.getTimestamp("date_");
			String date = null;
			try {
				date = DateUtil.parseDateToString(new Date(time.getTime()), Constant.YMD);
				data.put("date_", date);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	@Override
	public void saveCheckItems(Employee employee, String CheckItems) {
		List<Map<Object, Object>> list = BaseUtil.parseGridStoreToMaps(CheckItems);
		List<String> sqls = new ArrayList<String>();
		for (Map<Object, Object> map : list) {
			if (map.get("enable_") == null || "".equals(map.get("enable_")) || "null".equals(map.get("enable_"))
					|| !Boolean.parseBoolean(map.get("enable_").toString())) {
				map.put("enable_", 0);
			} else {
				map.put("enable_", 1);
			}
			String billoutmode = (String) map.get("billoutmode_");
			String code = billoutmode.replace("not", "use");
			boolean checked = Integer.parseInt(map.get("enable_").toString()) == 1;
			// 根据系统参数配置判断是否启用检测项
			if (!"all".equals(billoutmode)) {
				if (billoutmode.startsWith("use") && baseDao.isDBSetting(code)
						|| (!billoutmode.startsWith("use") && !baseDao.isDBSetting(code))) {
					if (!checked) {
						BaseUtil.showError("序号" + map.get("detno_") + "开票记录模式和系统参数设置一致，不允许关闭该检测项！");
					}
				} else {
					if (checked) {
						BaseUtil.showError("序号" + map.get("detno_") + "开票记录模式和系统参数设置不一致，不允许启用该检测项！");
					}
				}
			}
			map.put("man_", employee.getEm_name());
			map.put("date_", DateUtil.format(new Date(), Constant.YMD));
			if (!StringUtil.hasText(map.get("code_"))) {
				map.put("code_", baseDao.sGetMaxNumber("SYS_CHECKITEM", 2));
				sqls.add(SqlUtil.getInsertSqlByMap(map, "SYS_CHECKITEM"));
			} else {
				sqls.add(SqlUtil.getUpdateSqlByFormStore(map, "SYS_CHECKITEM", "code_"));
			}
		}
		try {
			baseDao.execute(sqls);
			if (baseDao.isDBSetting("MonthAccount!scm", "ifWHCateCode")) {
				baseDao.execute("UPDATE SYS_CHECKITEM  SET ENABLE_=1 WHERE CODE_='STF007'");
				baseDao.execute("UPDATE SYS_CHECKITEM  SET ENABLE_=0 WHERE CODE_='STF006'");
			} else {
				baseDao.execute("UPDATE SYS_CHECKITEM  SET ENABLE_=0 WHERE CODE_='STF007'");
				baseDao.execute("UPDATE SYS_CHECKITEM  SET ENABLE_=1 WHERE CODE_='STF006'");
			}
		} catch (Exception e) {
			BaseUtil.showError("保存失败，错误：" + e.getMessage());
		}
	}

	@Override
	public List<Map<String, Object>> getParamSets(String checkcode) {
		List<Map<String, Object>> paramSets = new ArrayList<Map<String, Object>>();
		SqlRowList rs = baseDao.queryForRowSet("select * from SYS_CHECKVARIABLE where check_code=?", checkcode);
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("key_", rs.getString("key_"));
			map.put("getkeySql_", rs.getString("getkeySql_"));
			map.put("data_type", rs.getString("data_type"));
			paramSets.add(map);
		}
		return paramSets;
	}

	@Override
	public void saveParamSets(String checkcode, String ParamSets) {
		List<Map<Object, Object>> list = BaseUtil.parseGridStoreToMaps(ParamSets);
		List<String> sqls = new ArrayList<String>();
		// 先删除原有的设置数据
		baseDao.deleteByCondition("SYS_CHECKVARIABLE", "check_code=?", checkcode);

		// 加上检测号
		for (Map<Object, Object> map : list) {
			map.put("check_code", checkcode);
			sqls.add(SqlUtil.getInsertSqlByMap(map, "SYS_CHECKVARIABLE"));
		}
		try {
			baseDao.execute(sqls);
		} catch (Exception e) {
			BaseUtil.showError("保存失败，错误：" + e.getMessage());
		}
	}

	@Override
	public List<Map<String, Object>> getErrorSets(String checkcode) {
		List<Map<String, Object>> errorSets = new ArrayList<Map<String, Object>>();
		SqlRowList rs = baseDao.queryForRowSet("select * from SYS_ERRORCONFIG where check_code=? order by detno_", checkcode);
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("field_", rs.getString("field_"));
			map.put("desc_", rs.getString("desc_"));
			map.put("type_", rs.getString("type_"));
			map.put("width_", rs.getString("width_"));
			map.put("render_", rs.getString("render_"));
			errorSets.add(map);
		}
		return errorSets;
	}

	@Override
	public void saveErrorSets(String checkcode, String ErrorSets) {
		List<Map<Object, Object>> list = BaseUtil.parseGridStoreToMaps(ErrorSets);
		List<String> sqls = new ArrayList<String>();
		int count = 0;
		// 先删除原有的设置数据
		baseDao.deleteByCondition("SYS_ERRORCONFIG", "check_code=?", checkcode);

		// 加上检测号
		for (Map<Object, Object> map : list) {
			count++;
			map.put("check_code", checkcode);
			map.put("detno_", count);
			sqls.add(SqlUtil.getInsertSqlByMap(map, "SYS_ERRORCONFIG"));
		}
		try {
			baseDao.execute(sqls);
		} catch (Exception e) {
			BaseUtil.showError("保存失败，错误：" + e.getMessage());
		}
	}

	@Override
	public void deleteSet(String checkcode, String key, Boolean isParamSet) {
		try {
			if (isParamSet) {
				baseDao.deleteByCondition("SYS_CHECKVARIABLE", "check_code=? and key_=?", checkcode, key);
			} else {
				baseDao.deleteByCondition("SYS_ERRORCONFIG", "check_code=? and field_=?", checkcode, key);
			}
		} catch (Exception e) {
			BaseUtil.showError("删除失败，错误：" + e.getMessage());
		}
	}

	@Override
	public void getCheckItemStatus(String module, String billoutmode, Boolean checked) {
		String code = billoutmode.replace("not", "use");
		// 根据系统参数配置决定是否启用检测项
		if (!"all".equals(billoutmode)) {
			if (billoutmode.startsWith("use") && baseDao.isDBSetting(code)
					|| (!billoutmode.startsWith("use") && !baseDao.isDBSetting(code))) {
				if (!checked) {
					BaseUtil.showError("当前开票模式不允许关闭此检测项!");
				}
			} else {
				if (checked) {
					BaseUtil.showError("当前开票模式不允许启用此检测项!");
				}
			}
		}
	}

	public Map<String, Object> checkAccounts(String module, String yearmonth) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Integer> oks = new ArrayList<Integer>();
		String res = null;
		res = baseDao.callProcedure("SP_CHECKACCOUNTS", new Object[] { module, yearmonth });
		if (StringUtil.hasText(res) && !res.equals("OK")) {
			BaseUtil.showError(res);
		}
		int checks = 0, OKs = 0, errors = 0;

		List<Object> codes = baseDao.getFieldDatasByCondition("SYS_CHECKITEM", "code_", "module_='" + module
				+ "' and enable_ =1 order by detno_");
		checks = codes.size();
		for (Object code : codes) {
			Integer count = baseDao.getCountByCondition("SYS_CHECKERRDATA", "check_code='" + code + "'");
			if (count == 0) {
				oks.add(1);
				OKs++;
			} else {
				oks.add(0);
				errors++;
			}
		}
		map.put("checks", checks);
		map.put("OKS", OKs);
		map.put("errors", errors);
		map.put("oks", oks);
		return map;
	}

	@Override
	public Map<String, Object> getShowDetailGrid(String checkcode) {
		Map<String, Object> map = new HashMap<String, Object>();
		int width = 0;
		String fields = "";
		String data = "";
		SqlRowList rs = null;
		rs = baseDao.queryForRowSet("select data_ from SYS_CHECKERRDATA where check_code =? order by id_ asc", checkcode);
		while (rs.next()) {
			data += "," + rs.getString(1);

		}
		data = "[" + data.substring(1) + "]";
		rs = baseDao.queryForRowSet("select * from SYS_ERRORCONFIG where check_code =? order by detno_ asc", checkcode);
		List<GridColumns> columns = new ArrayList<GridColumns>();
		while (rs.next()) {
			fields += ",'" + rs.getString("field_") + "' ";
			DataListDetail detailgrid = new DataListDetail();
			detailgrid.setDld_caption(rs.getString("desc_"));
			detailgrid.setDld_field(rs.getString("field_"));
			detailgrid.setDld_fieldtype(rs.getString("type_"));
			detailgrid.setDld_width(rs.getInt("width_"));
			detailgrid.setDld_editable(0);
			detailgrid.setDld_render(rs.getString("render_"));
			width += rs.getInt("width_");
			Float flex = (float) 0;
			detailgrid.setDld_flex(flex);
			String language = SystemSession.getLang();
			columns.add(new GridColumns(detailgrid, null, language));
		}
		fields = "[" + fields.substring(1) + "]";
		map.put("fields", fields);
		map.put("data", data);
		map.put("columns", columns);
		map.put("width", width);
		return map;
	}

	@Override
	public void refreshEndData(String month, String module) {
		String res = baseDao.callProcedure("SP_REFRESHENDDATA", new Object[] { month, module });
		if (StringUtil.hasText(res) && !res.equals("OK")) {
			BaseUtil.showError(res);
		}
	}
}
