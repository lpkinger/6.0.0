package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.fa.ExchangeGlService;

@Service
public class ExchangeGlServiceImpl implements ExchangeGlService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private VoucherDao voucherDao;

	@Override
	public String exchange(String yearmonth, String ca_code, Boolean account, String data) {
		// 保存CurrencysMonth, 并写入Currencys
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		List<String> sqls = new ArrayList<String>();
		for (Map<Object, Object> map : store) {
			sqls.add("UPDATE CurrencysMonth SET cm_endrate=" + map.get("cm_endrate") + " WHERE cm_yearmonth=" + yearmonth
					+ " AND cm_crname='" + map.get("cm_crname") + "'");
			sqls.add("UPDATE Currencys SET cr_vorate=" + map.get("cm_endrate") + " WHERE cr_name='" + map.get("cm_crname") + "'");
		}
		baseDao.execute(sqls);
		List<String> result = baseDao.callProcedureWithOut("fa_gl_exchange.create_exchange", new Object[] { ca_code,
				SystemSession.getUser().getEm_id(), SystemSession.getUser().getEm_code() }, new Integer[] { 1, 2, 3 }, new Integer[] { 4,
				5, 6 });
		if (StringUtil.hasText(result.get(2))) {
			return result.get(2);
		} else {
			if (result.get(0) != null) {
				int vo_id = Integer.parseInt(result.get(0));
				String codeStr = "<a href=\"javascript:openUrl('jsps/fa/ars/voucher.jsp?formCondition=vo_idIS" + vo_id
						+ "&gridCondition=vd_voidIS" + vo_id + "')\">" + result.get(1) + "</a><br>";
				voucherDao.validVoucher(vo_id);
				String error = baseDao.getJdbcTemplate().queryForObject("select vo_errstring from Voucher Where vo_id=?", String.class,
						vo_id);
				// 判断凭证是否合法
				if (error != null && error.trim().length() > 0) {
					return "产生的凭证有问题，请打开凭证查看!<br>" + codeStr;
				} else {
					if (account) {
						baseDao.updateByCondition("Voucher", "vo_status='" + BaseUtil.getLocalMessage("AUDITED")
								+ "',vo_statuscode='AUDITED',vo_checkby='" + SystemSession.getUser().getEm_name() + "'", "vo_id =" + vo_id);
						baseDao.callProcedure("SP_WriteVoucher", new Object[] { yearmonth });
						return "已成功产生汇兑损益凭证并记账!<br>" + codeStr;
					} else {
						return "已成功产生汇兑损益凭证!<br>" + codeStr;
					}
				}
			} else {
				return "没有需要产生凭证的数据！";
			}
		}
		// // 汇兑损益前，判断是否还有未记账的凭证
		// boolean hasUnAccount = baseDao.checkIf("Voucher", "vo_yearmonth=" +
		// yearmonth + " AND vo_statuscode<>'ACCOUNT'");
		// if (hasUnAccount) {
		// BaseUtil.showError("本月还有未记账的凭证!");
		// }
		// // 先查找有没有已经生成的汇兑损益凭证
		// SqlRowList checkRes = baseDao.queryForRowSet(
		// "select vo_id,vo_code from voucher where vo_yearmonth=? AND (vo_explanation like '%汇兑损益%' or vo_source like '%汇兑损益%')",
		// yearmonth);
		// if (checkRes.hasNext()) {
		// StringBuffer err = new StringBuffer("汇兑损益已经制作,不能再次制作!凭证:<br>");
		// while (checkRes.next()) {
		// err.append("<a href=\"javascript:openUrl('jsps/fa/ars/voucher.jsp?formCondition=vo_idIS").append(checkRes.getObject(1))
		// .append("&gridCondition=vd_voidIS").append(checkRes.getObject(1)).append("')\">").append(checkRes.getString(2))
		// .append("</a><br>");
		// }
		// return err.toString();
		// }
		// // 保存CurrencysMonth, 并写入Currencys
		// List<Map<Object, Object>> store =
		// BaseUtil.parseGridStoreToMaps(data);
		// List<String> sqls = new ArrayList<String>();
		// for (Map<Object, Object> map : store) {
		// sqls.add("UPDATE CurrencysMonth SET cm_endrate=" +
		// map.get("cm_endrate") + " WHERE cm_yearmonth=" + yearmonth
		// + " AND cm_crname='" + map.get("cm_crname") + "'");
		// sqls.add("UPDATE Currencys SET cr_vorate=" + map.get("cm_endrate") +
		// " WHERE cr_name='" + map.get("cm_crname") + "'");
		// }
		// baseDao.execute(sqls);
		// // 不包括辅助核算的外币核算科目
		// String defaultCurrency = baseDao.getDBSetting("defaultCurrency");
		// boolean bool1 = false;
		// boolean bool2 = false;
		// String sql =
		// "SELECT CategorySetCurrency.*,cr_vorate FROM CategorySetCurrency,Category,Currencys "
		// +
		// "WHERE nvl(ca_class,' ')<>'损益类' and nvl(ca_class,' ')<>'损益' and abs(nvl(ca_checkrate,0))=1 AND ca_code=csc_catecode AND abs(ca_currencytype)=1 AND abs(ca_isleaf)=1 AND csc_currency=cr_name AND "
		// + "nvl(ca_asstype,' ')=' ' AND nvl(csc_currency,' ')<>? AND "
		// +
		// "(ROUND(csc_doubledebit*cr_vorate,2)<>csc_debit OR ROUND(csc_doublecredit*cr_vorate,2)<>csc_credit)";
		// SqlRowList rs = baseDao.queryForRowSet(sql, defaultCurrency);
		// String catecode = null;
		// String error1 = null;
		// String error2 = null;
		// String codeStr = "";
		// String idStr = "";
		// if (rs.hasNext()) {
		// int vo_id = baseDao.getSeqId("VOUCHER_SEQ");
		// idStr += vo_id;
		// String vo_code = baseDao.sGetMaxNumber("Voucher", 2);
		// sqls.add(addVoucherSql(SystemSession.getUser(),
		// SystemSession.getLang(), yearmonth, vo_id, vo_code));
		// double total = 0;
		// int vd_detno = 1;
		// double debit = 0;
		// double credit = 0;
		// double vd_debit = 0;
		// double vd_credit = 0;
		// while (rs.next()) {
		// catecode = rs.getString("csc_catecode");
		// debit = 0;
		// credit = 0;
		// if ((rs.getGeneralDouble("csc_doubledebit") -
		// rs.getGeneralDouble("csc_doublecredit")) *
		// rs.getGeneralDouble("cr_vorate") > rs
		// .getGeneralDouble("csc_debit") - rs.getGeneralDouble("csc_credit")) {
		// credit = (rs.getGeneralDouble("csc_doubledebit") -
		// rs.getGeneralDouble("csc_doublecredit"))
		// * rs.getGeneralDouble("cr_vorate") - rs.getGeneralDouble("csc_debit")
		// + rs.getGeneralDouble("csc_credit");
		// credit = NumberUtil.formatDouble(credit, 2);
		// }
		// if ((rs.getGeneralDouble("csc_doublecredit") -
		// rs.getGeneralDouble("csc_doubledebit")) *
		// rs.getGeneralDouble("cr_vorate") > rs
		// .getGeneralDouble("csc_credit") - rs.getGeneralDouble("csc_debit")) {
		// debit = (rs.getGeneralDouble("csc_doublecredit") -
		// rs.getGeneralDouble("csc_doubledebit"))
		// * rs.getGeneralDouble("cr_vorate") -
		// rs.getGeneralDouble("csc_credit") + rs.getGeneralDouble("csc_debit");
		// debit = NumberUtil.formatDouble(debit, 2);
		// }
		// if (debit + credit == 0) {
		// debit = 0;
		// credit = 0;
		// }
		// if (debit != 0 || credit != 0) {
		// total += debit - credit;
		// vd_debit = credit > 0 ? credit : 0;
		// vd_credit = debit > 0 ? debit : 0;
		// sqls.add("INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,"
		// +
		// "vd_credit,vd_currency,vd_yearmonth) VALUES (VoucherDetail_SEQ.nextval,"
		// + vo_id + "," + vd_detno++ + ",'"
		// + catecode + "','汇兑损益'," + vd_debit + "," + vd_credit + ",'" +
		// rs.getString("csc_currency") + "'," + yearmonth
		// + ")");
		// }
		// }
		// // 产生汇兑损益的分录
		// if (total != 0) {
		// total = NumberUtil.formatDouble(total, 2);
		// vd_debit = total;
		// vd_credit = 0;
		// sqls.add("INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,"
		// + "vd_credit,vd_yearmonth) VALUES (VoucherDetail_SEQ.nextval," +
		// vo_id + "," + vd_detno++ + ",'" + ca_code
		// + "','汇兑损益'," + vd_debit + "," + vd_credit + "," + yearmonth + ")");
		// }
		// baseDao.execute(sqls);
		// codeStr +=
		// "<a href=\"javascript:openUrl('jsps/fa/ars/voucher.jsp?formCondition=vo_idIS"
		// + vo_id + "&gridCondition=vd_voidIS"
		// + vo_id + "')\">" + vo_code + "</a><br>";
		// voucherDao.validVoucher(vo_id);
		// error1 =
		// baseDao.getJdbcTemplate().queryForObject("select vo_errstring from Voucher Where vo_id=?",
		// String.class, vo_id);
		// } else {
		// bool1 = true;
		// }
		// // 辅助核算
		// sql =
		// "SELECT CategoryASS.*, Currencys.cr_vorate FROM CategoryASS INNER JOIN "
		// +
		// "Category ON CategoryASS.ca_catecode = Category.ca_code INNER JOIN "
		// + "Currencys ON CategoryASS.ca_currency = Currencys.cr_name "
		// +
		// "WHERE (abs(Category.ca_currencytype)=1) and nvl(ca_class,' ')<>'损益类' and nvl(ca_class,' ')<>'损益' and abs(nvl(ca_checkrate,0))=1 AND "
		// +
		// "nvl(Category.ca_asstype,' ')<>' ' AND nvl(CategoryASS.ca_currency,' ')<>? "
		// +
		// "AND (ROUND(CategoryASS.ca_doubledebit * Currencys.cr_vorate, 2) <> CategoryASS.ca_debit OR "
		// +
		// "ROUND(CategoryASS.ca_doublecredit * Currencys.cr_vorate, 2) <> CategoryASS.ca_credit)";
		// rs = baseDao.queryForRowSet(sql, defaultCurrency);
		// if (rs.hasNext()) {
		// int vo_id = baseDao.getSeqId("VOUCHER_SEQ");
		// if (idStr.length() > 0) {
		// idStr += ",";
		// }
		// idStr += vo_id;
		// String vo_code = baseDao.sGetMaxNumber("Voucher", 2);
		// sqls = new ArrayList<String>();
		// sqls.add(addVoucherSql(SystemSession.getUser(),
		// SystemSession.getLang(), yearmonth, vo_id, vo_code));
		// double total = 0;
		// int vd_detno = 1;
		// int vd_id = 0;
		// double debit = 0;
		// double credit = 0;
		// double vd_debit = 0;
		// double vd_credit = 0;
		// while (rs.next()) {
		// catecode = rs.getString("ca_catecode");
		// debit = 0;
		// credit = 0;
		// if ((rs.getGeneralDouble("ca_doubledebit") -
		// rs.getGeneralDouble("ca_doublecredit")) *
		// rs.getGeneralDouble("cr_vorate") > rs
		// .getGeneralDouble("ca_debit") - rs.getGeneralDouble("ca_credit")) {
		// credit = (rs.getGeneralDouble("ca_doubledebit") -
		// rs.getGeneralDouble("ca_doublecredit"))
		// * rs.getGeneralDouble("cr_vorate") - rs.getGeneralDouble("ca_debit")
		// + rs.getGeneralDouble("ca_credit");
		// credit = NumberUtil.formatDouble(credit, 2);
		// }
		// if ((rs.getGeneralDouble("ca_doublecredit") -
		// rs.getGeneralDouble("ca_doubledebit")) *
		// rs.getGeneralDouble("cr_vorate") > rs
		// .getGeneralDouble("ca_credit") - rs.getGeneralDouble("ca_debit")) {
		// debit = (rs.getGeneralDouble("ca_doublecredit") -
		// rs.getGeneralDouble("ca_doubledebit"))
		// * rs.getGeneralDouble("cr_vorate") - rs.getGeneralDouble("ca_credit")
		// + rs.getGeneralDouble("ca_debit");
		// debit = NumberUtil.formatDouble(debit, 2);
		// }
		//
		// if (debit + credit == 0) {
		// debit = 0;
		// credit = 0;
		// }
		// if (debit != 0 || credit != 0) {
		// total += debit - credit;
		// vd_debit = credit > 0 ? credit : 0;
		// vd_credit = debit > 0 ? debit : 0;
		// vd_id = baseDao.getSeqId("VoucherDetail_SEQ");
		// sqls.add("INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,"
		// + "vd_credit,vd_currency,vd_yearmonth) VALUES (" + vd_id + "," +
		// vo_id + "," + vd_detno++ + ",'" + catecode
		// + "','汇兑损益'," + vd_debit + "," + vd_credit + ",'" +
		// rs.getString("ca_currency") + "'," + yearmonth + ")");
		// sqls.add("INSERT INTO VoucherDetailAss(vds_id,vds_vdid,vds_detno,vds_asstype,vds_asscode,vds_assname,vds_type) VALUES(VoucherDetailAss_SEQ.nextval,"
		// + vd_id
		// + ",1,'"
		// + rs.getString("ca_asstype")
		// + "','"
		// + rs.getString("ca_asscode")
		// + "','"
		// + rs.getString("ca_assname") + "','Voucher')");
		// }
		// }
		// // 产生汇兑损益的分录
		// total = NumberUtil.formatDouble(total, 2);
		// if (total != 0) {
		// vd_debit = total;
		// vd_credit = 0;
		// sqls.add("INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,"
		// + "vd_credit,vd_yearmonth) VALUES (VoucherDetail_SEQ.nextval," +
		// vo_id + "," + vd_detno++ + ",'" + ca_code
		// + "','汇兑损益'," + vd_debit + "," + vd_credit + "," + yearmonth + ")");
		// }
		// baseDao.execute(sqls);
		// codeStr +=
		// "<a href=\"javascript:openUrl('jsps/fa/ars/voucher.jsp?formCondition=vo_idIS"
		// + vo_id + "&gridCondition=vd_voidIS"
		// + vo_id + "')\">" + vo_code + "</a><br>";
		// voucherDao.validVoucher(vo_id);
		// error2 =
		// baseDao.getJdbcTemplate().queryForObject("select vo_errstring from Voucher Where vo_id=?",
		// String.class, vo_id);
		// } else {
		// bool2 = true;
		// }
		// if (bool1 && bool2) {
		// return "没有需要产生汇兑损益凭证的数据!";
		// }
		// // 判断凭证是否合法
		// if ((error1 != null && error1.trim().length() > 0) || (error2 != null
		// && error2.trim().length() > 0)) {
		// return "产生的凭证有问题，请打开凭证查看!<br>" + codeStr;
		// } else {
		// if (codeStr.length() == 0) {
		// return "未产生汇兑损益凭证!";
		// } else {
		// if (account) {
		// baseDao.updateByCondition("Voucher", "vo_status='" +
		// BaseUtil.getLocalMessage("AUDITED")
		// + "',vo_statuscode='AUDITED',vo_checkby='" +
		// SystemSession.getUser().getEm_name() + "'", "vo_id IN (" + idStr
		// + ")");
		// baseDao.callProcedure("SP_WriteVoucher", new Object[] { yearmonth });
		// return "已成功产生汇兑损益凭证并记账!<br>" + codeStr;
		// } else {
		// return "已成功产生汇兑损益凭证!<br>" + codeStr;
		// }
		// }
		// }
	}

	private String addVoucherSql(Employee employee, String language, String yearmonth, int vo_id, String vo_code) {
		Map<String, Object> periods = voucherDao.getPeriodsDate("MONTH-A", Integer.parseInt(yearmonth));
		String lead = StringUtil.valueOf(periods.get("vo_lead"));
		String vo_number = voucherDao.getVoucherNumber(yearmonth, lead, null);
		return "INSERT INTO Voucher(vo_id,vo_code,vo_yearmonth,vo_lead,vo_number,vo_emid,vo_recordman,vo_status,"
				+ "vo_statuscode,vo_recorddate,vo_explanation,vo_currencytype,vo_printstatus,vo_date)" + " VALUES (" + vo_id + ",'"
				+ vo_code + "'," + yearmonth + ",'" + (lead == null ? "" : lead) + "'," + vo_number + "," + employee.getEm_id() + ",'"
				+ employee.getEm_name() + "','" + BaseUtil.getLocalMessage("ENTERING", language) + "','ENTERING',sysdate,'汇兑损益',0,'未打印',"
				+ periods.get("pd_enddate") + ")";
	}
}
