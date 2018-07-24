package com.uas.erp.service.cost.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.service.cost.InventoryAccountService;

@Service
public class InventoryAccountServiceImpl implements InventoryAccountService {

	@Autowired
	private BaseDao baseDao;

	final static String BE = "INSERT INTO BillError(be_code,be_class,be_type,be_date,be_checker,be_remark) VALUES(";

	/**
	 * 检测错误<br>
	 * 如果有错误数据,写入到BillError
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
				sqls.add(getErrorSql(rs.getGeneralString(1), rs.getGeneralString(2), type, employee.getEm_name(), rem));
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

	/**
	 * 清除之前检验记录
	 * 
	 * @param caller
	 */
	private void clearByCaller(String caller) {
		baseDao.deleteByCondition("BillError", "be_type='" + caller + "'");
	}

	@Override
	public boolean co_chk_before_a(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		String freezemonth = baseDao.getDBSetting("freezeMonth");
		boolean bool = true;
		if (freezemonth != null) {
			if (!freezemonth.equals(month.toString())) {
				bool = false;
				baseDao.execute(getErrorSql("当前冻结期间：" + freezemonth, "", type, employee.getEm_name(), "期间未冻结"));
			}
		} else {
			bool = false;
			baseDao.execute(getErrorSql("", "", type, employee.getEm_name(), "无冻结期间"));
		}
		return bool;
	}

	@Override
	public boolean co_chk_before_b(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(
				"select pi_inoutno,pi_class,'日期：'||pi_date||',录入人：'||pi_recordman||',录入日期：'||pi_recorddate from Prodinout where pi_statuscode<>'POSTED' and to_char(pi_date,'yyyymm')=? order by pi_class,pi_inoutno",
				new Object[] { month }, type, employee, "未过账", all);
		return count == 0;
	}

	@Override
	public boolean co_chk_before_c(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError("select ms_code,ms_class from MakeScrap where nvl(ms_statuscode,0)<>'AUDITED' and to_char(ms_date,'yyyymm')=?",
				new Object[] { month }, type, employee, "报废单未审核", all);
		return count == 0;
	}

	static final String CO_CHK_D = "select pi_inoutno,pi_class,'行：'||pd_pdno||',仓库：'||pd_whcode from prodinout left join prodiodetail on pi_id=pd_piid left join warehouse on pd_whcode=wh_code where to_char(pi_date,'yyyymm')=? and abs(wh_nocost)=0 and nvl(pd_price,0)<=0 order by pi_class,pi_inoutno,pd_pdno";

	@Override
	public boolean co_chk_before_d(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(CO_CHK_D, new Object[] { month }, type, employee, "非无值仓0单价、单价为负数", all);
		return count == 0;
	}

	static final String CO_CHK_E = "select pi_inoutno,pi_class,'状态:'||pi_status from prodinout where pi_status<>'未过账' and pi_status<>'已过账' and to_char(pi_date,'yyyymm')=?";

	@Override
	public boolean co_chk_before_e(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "出入库单中文状态不正确";
		clearByCaller(type);
		int count = isError(CO_CHK_E, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	static final String CO_CHK_F = "select ma_code,ma_kind,ma_prodcode from make where ma_prodcode not in (select nvl(pr_code,' ') from product) and ma_code in(select cd_makecode from costdetail where cd_yearmonth=?) order by ma_code";

	@Override
	public boolean co_chk_before_f(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "工单的成品物料编号不存在";
		clearByCaller(type);
		int count = isError(CO_CHK_F, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	static final String CO_CHK_G = "select mm_code,'工单','行'||mm_detno||'物料:'||mm_prodcode from makematerial where mm_prodcode not in (select nvl(pr_code,' ') from product) and mm_code in(select cd_makecode from costdetail where cd_yearmonth=?) order by mm_code,mm_detno";

	@Override
	public boolean co_chk_before_g(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "工单用料表的物料不存在";
		clearByCaller(type);
		int count = isError(CO_CHK_G, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	@Override
	public boolean co_chk_before_h(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(
				"select pd_inoutno,pd_piclass,'行:'||pd_pdno||'<br>物料编号:'||pd_prodcode from ProdIODetail left join ProdInOut on pd_piid=pi_id"
						+ " where nvl(pd_status,0)<>0 and to_char(pi_date,'yyyymm')=? and not exists (select 1 from product where pr_code=pd_prodcode) order by pi_class,pi_inoutno,pd_pdno",
				new Object[] { month }, type, employee, "物料编号不存在", all);
		return count == 0;
	}

	static final String CO_CHK_I = "select pi_inoutno,pi_class,'凭证:'||pi_vouchercode from prodinout where to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' and nvl(pi_vouchercode,' ')<>' '";

	@Override
	public boolean co_chk_before_i(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "出入库单已做凭证";
		clearByCaller(type);
		int count = isError(CO_CHK_I, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	static final String CO_CHK_J = "select pi_inoutno,pi_class,'凭证:'||pi_vouchercode from prodinout where to_char(pi_date,'yyyymm')=? and pi_statuscode='POSTED' and nvl(pi_vouchercode,' ')<>' ' and nvl(pi_vouchercode,' ') not in (select vo_code from voucher where vo_yearmonth=?)";

	@Override
	public boolean co_chk_before_j(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "出入库单有凭证编号,但凭证在当月不存在";
		clearByCaller(type);
		int count = isError(CO_CHK_J, new Object[] { month, month }, type, employee, remark, all);
		return count == 0;
	}

	static final String CO_CHK_K1 = "select pi_inoutno,pi_class,'发票:'||ab_code||',凭证:'||ab_vouchercode from apbill left join apbilldetail on ab_id=abd_abid left join prodiodetail on abd_pdid=pd_id left join prodinout on pd_piid=pi_id where abd_sourcekind='PRODIODETAIL' and to_char(pi_date,'yyyymm')=? and pi_class in ('采购验收单','采购验退单','委外验收单','委外验退单') and ab_vouchercode is not null";
	static final String CO_CHK_K2 = "select pi_inoutno,pi_class,'暂估单号:'||es_code||',凭证:'||es_vouchercode from estimatedetail left join estimate on esd_esid=es_id left join prodiodetail on esd_pdid=pd_id left join prodinout on pd_piid=pi_id where to_char(pi_date,'yyyymm')=? and pi_class in ('采购验收单','采购验退单','委外验收单','委外验退单') and es_vouchercode is not null";

	@Override
	public boolean co_chk_before_k(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "生成应付发票并制作了凭证";
		clearByCaller(type);
		int count = 0;
		count += isError(CO_CHK_K1, new Object[] { month }, type, employee, remark, all);
		count += isError(CO_CHK_K2, new Object[] { month }, type, employee, remark, all);
		return count == 0;
	}

	static final String CO_CHK_L = "select pi_inoutno,pi_class,'发票:'||ab_code||',凭证:'||ab_costvouchercode from arbill left join arbilldetail on ab_id=abd_abid left join prodiodetail on abd_pdid=pd_id left join prodinout on pd_piid=pi_id where abd_sourcekind='PRODIODETAIL' and to_char(pi_date,'yyyymm')=? and pi_class in ('出货单','销售退货单') and ab_costvouchercode is not null";
	static final String CO_CHK_L1 = "select pi_inoutno,pi_class,'发票:'||ab_code||',凭证:'||ab_vouchercode from arbill left join arbilldetail on ab_id=abd_abid left join prodiodetail on abd_pdid=pd_id left join prodinout on pd_piid=pi_id where abd_sourcekind='PRODIODETAIL' and to_char(pi_date,'yyyymm')=? and pi_class in ('出货单','销售退货单') and ab_vouchercode is not null";

	@Override
	public boolean co_chk_before_l(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		String remark = "生成应收发票并制作了结转主营业务成本凭证";
		clearByCaller(type);
		int count = 0;
		count += isError(CO_CHK_L, new Object[] { month }, type, employee, remark, all);
		if (baseDao.isDBSetting("useBillOutAR")) {
			count += isError(CO_CHK_L1, new Object[] { month }, type, employee, remark, all);
		}
		return count == 0;
	}

	static final String CO_CHK_M = "select gs_code,gs_class,'凭证:'||gs_vouchercode from goodssend where to_char(gs_date,'yyyymm')=? and gs_vouchercode is not null";

	@Override
	public boolean co_chk_before_m(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(CO_CHK_M, new Object[] { month }, type, employee, "已制作凭证", all);
		return count == 0;
	}

	static final String CO_CHK_N = "select pi_inoutno,pi_class,'单据汇率：'||nvl(pi_rate,0)||'，月度汇率：'||nvl(cm_crrate,0) from ProdInOut,currencysmonth where pi_currency=cm_crname and to_char(pi_date,'yyyymm')=cm_yearmonth and cm_yearmonth=? and pi_class in ('采购验收单','委外验收单') and nvl(pi_rate,0) <> nvl(cm_crrate,0)";

	@Override
	public boolean co_chk_before_n(String type, Employee employee, Integer month, String start, String end, Boolean all) {
		clearByCaller(type);
		int count = isError(CO_CHK_N, new Object[] { month }, type, employee, "单据汇率与月度汇率不一致", all);
		return count == 0;
	}
}
