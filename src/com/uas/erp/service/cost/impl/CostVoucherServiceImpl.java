package com.uas.erp.service.cost.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.service.cost.CostVoucherService;

@Service
public class CostVoucherServiceImpl implements CostVoucherService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private VoucherDao voucherDao;

	@Override
	public String makeCreate(String makeCatecode, String makeToCatecode, Boolean account, String materialsCatecode, Boolean account2,
			String manMakeCatecode, Boolean account3) {
		Map<String, Object> periods = voucherDao.getJustPeriods("MONTH-A");
		String yearmonth = String.valueOf(periods.get("PD_DETNO"));
		if (makeCatecode == null || makeCatecode.length() == 0)
			makeCatecode = baseDao.getDBSetting("makeCatecode");// 制造费用科目
		if (makeCatecode == null || makeCatecode.length() == 0) {
			return "制造费用科目未设置，不能进行生产成本结转!";
		}
		boolean isAllAccounted = baseDao
				.checkByCondition("Voucher", "vo_yearmonth=" + yearmonth + " AND nvl(vo_statuscode,' ')<>'ACCOUNT'");
		if (!isAllAccounted) {
			return "本月还有未记账的凭证，不能进行生产成本结转!";
		}
		Object[] vo = baseDao.getFieldsDataByCondition("Voucher left join VoucherDetail on vo_id=vd_void", "vo_id,vo_code", "vo_yearmonth="
				+ yearmonth + " AND vo_explanation like '%结转制造费用%' and vd_catecode='" + makeToCatecode + "'");
		if (vo != null && vo[1] != null) {
			return "制造费用已经结转,凭证号:" + "<a href=\"javascript:openUrl('jsps/fa/ars/voucher.jsp?formCondition=vo_idIS" + vo[0]
					+ "&gridCondition=vd_voidIS" + vo[0] + "')\">" + vo[1] + "</a>,不能再次结转!";
		}
		String sql = "SELECT cs_catecode,cs_debit,cs_credit,cs_doubledebit,cs_doublecredit,ca_currency,ca_currencytype,ca_asstype FROM CategorySET,Category "
				+ "WHERE cs_catecode=ca_code AND abs(ca_isleaf)=1 AND ca_code like '" + makeCatecode + "%' AND cs_debit+cs_credit<>0";
		SqlRowList rs = baseDao.queryForRowSet(sql);
		if (rs.hasNext()) {
			List<String> sqls = new ArrayList<String>();
			int vo_id = baseDao.getSeqId("VOUCHER_SEQ");
			String vo_code = baseDao.sGetMaxNumber("Voucher", 2);
			sqls.add(addVoucherSql(yearmonth, vo_id, vo_code, "MONTH-T"));
			String asstype = null;
			Object assname = null;
			int vd_detno = 1;
			double debit = 0;
			double credit = 0;
			double vd_debit = 0;
			double vd_credit = 0;
			double vd_doubledebit = 0;
			double vd_doublecredit = 0;
			String vd_currency = "";
			double total = 0;
			double vd_rate = 1;
			int vd_id = 0;
			while (rs.next()) {
				asstype = rs.getString("ca_asstype");
				if (asstype != null) {
					if (asstype.indexOf("#") > 0) {
						sql = "select ass, str_concat(vds_vdid) vd_id from (select str_concat(vds_asstype || '#' || vds_asscode || '#' || vds_assname) ass,vds_vdid from (select vds_vdid,vds_asstype,vds_asscode,vds_assname from voucherdetailass left join voucherdetail on vds_vdid=vd_id where vd_catecode='"
								+ rs.getGeneralString("cs_catecode")
								+ "' and vd_void in(select vo_id from voucher where vo_yearmonth="
								+ yearmonth + ") order by vds_asstype,vds_asscode,vds_assname) group by vds_vdid) group by ass";
						SqlRowList rsl = baseDao.queryForRowSet(sql);
						String[] ass = null;
						while (rsl.next()) {
							ass = rsl.getGeneralString(1).split(",");
							debit = baseDao.getJdbcTemplate().queryForObject(
									"select round(SUM(vd_debit-vd_credit),2) from VoucherDetail Where vd_id IN(" + rsl.getObject(2) + ")",
									Double.class);
							credit = 0;
							total += debit - credit;
							// 放贷方
							vd_debit = credit;
							vd_credit = debit;
							vd_id = baseDao.getSeqId("VoucherDetail_SEQ");
							sqls.add("INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,vd_credit) VALUES ("
									+ vd_id
									+ ","
									+ vo_id
									+ ","
									+ vd_detno++
									+ ",'"
									+ rs.getGeneralString("cs_catecode")
									+ "','结转制造费用',"
									+ vd_debit + "," + vd_credit + ")");
							for (int i = 0, len = ass.length; i < len; i++) {
								String[] s = ass[i].split("#");
								sqls.add("INSERT INTO VoucherDetailAss(vds_id,vds_vdid,vds_detno,vds_asstype,vds_asscode,vds_assname,vds_type) VALUES(VoucherDetailAss_SEQ.nextval,"
										+ vd_id + "," + (i + 1) + ",'" + s[0] + "','" + s[1] + "','" + s[2] + "','Voucher')");
							}
						}
					} else {
						sql = "SELECT * FROM CategoryAss WHERE ca_catecode='" + rs.getGeneralString("cs_catecode")
								+ "' AND ca_debit+ca_credit>0";
						SqlRowList rsl = baseDao.queryForRowSet(sql);
						assname = baseDao.getFieldDataByCondition("AssKind", "ak_name", "ak_code='" + asstype + "'");
						while (rsl.next()) {
							debit = rsl.getGeneralDouble("ca_debit", 2);
							credit = rsl.getGeneralDouble("ca_credit", 2);
							total += debit - credit;
							// 放贷方
							vd_credit = debit != 0 ? debit : credit * -1;
							vd_debit = 0;
							vd_id = baseDao.getSeqId("VoucherDetail_SEQ");
							sqls.add("INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,"
									+ "vd_credit) VALUES (" + vd_id + "," + vo_id + "," + vd_detno++ + ",'"
									+ rsl.getGeneralString("ca_catecode") + "','结转制造费用'," + vd_debit + "," + vd_credit + ")");
							sqls.add("INSERT INTO VoucherDetailAss(vds_id,vds_vdid,vds_detno,vds_asstype,vds_asscode,vds_assname,vds_type) VALUES(VoucherDetailAss_SEQ.nextval,"
									+ vd_id
									+ ",1,'"
									+ assname
									+ "','"
									+ rsl.getGeneralString("ca_asscode")
									+ "','"
									+ rsl.getGeneralString("ca_assname") + "','Voucher')");
						}
					}
				} else {
					debit = rs.getGeneralDouble("cs_debit", 2);
					credit = rs.getGeneralDouble("cs_credit", 2);
					total += debit - credit;
					// 放贷方
					vd_credit = debit != 0 ? debit : credit * -1;
					vd_debit = 0;
					vd_doubledebit = 0;
					vd_doublecredit = 0;
					vd_currency = "";
					vd_rate = 0;
					if (!"0".equals(rs.getObject("ca_currencytype"))) {// 复币
						debit = rs.getGeneralDouble("cs_doubledebit", 2);
						credit = rs.getGeneralDouble("cs_doublecredit", 2);
						vd_currency = rs.getGeneralString("ca_currency");
						if (debit > 0) {
							vd_doublecredit = debit;
							vd_rate = NumberUtil.formatDouble(vd_credit / debit, 10);
						}
						if (credit > 0) {
							vd_doubledebit = credit;
							vd_rate = NumberUtil.formatDouble(vd_debit / credit, 10);
						}
					}
					sqls.add("INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,"
							+ "vd_credit,vd_doubledebit,vd_doublecredit,vd_currency,vd_rate) VALUES (VoucherDetail_SEQ.nextval," + vo_id
							+ "," + vd_detno++ + ",'" + rs.getGeneralString("cs_catecode") + "','结转制造费用'," + vd_debit + "," + vd_credit
							+ "," + vd_doubledebit + "," + vd_doublecredit + ",'" + vd_currency + "'," + vd_rate + ")");
				}
			}
			if (account2) {
				sql = "SELECT cs_catecode,cs_debit,cs_credit,cs_doubledebit,cs_doublecredit,ca_currency,ca_currencytype,ca_asstype FROM CategorySET,Category "
						+ "WHERE cs_catecode=ca_code AND abs(ca_isleaf)=1 AND ca_code like '"
						+ materialsCatecode
						+ "%' AND cs_debit+cs_credit<>0";
				rs = baseDao.queryForRowSet(sql);
				while (rs.next()) {
					asstype = rs.getString("ca_asstype");
					if (asstype != null) {
						if (asstype.indexOf("#") > 0) {
							sql = "select ass, str_concat(vds_vdid) vd_id from (select str_concat(vds_asstype || '#' || vds_asscode || '#' || vds_assname) ass,vds_vdid from (select vds_vdid,vds_asstype,vds_asscode,vds_assname from voucherdetailass left join voucherdetail on vds_vdid=vd_id where vd_catecode='"
									+ rs.getGeneralString("cs_catecode")
									+ "' and vd_void in(select vo_id from voucher where vo_yearmonth="
									+ yearmonth + ") order by vds_asstype,vds_asscode,vds_assname) group by vds_vdid) group by ass";
							SqlRowList rsl = baseDao.queryForRowSet(sql);
							String[] ass = null;
							while (rsl.next()) {
								ass = rsl.getGeneralString(1).split(",");
								debit = baseDao.getJdbcTemplate().queryForObject(
										"select round(SUM(vd_debit-vd_credit),2) from VoucherDetail Where vd_id IN(" + rsl.getObject(2)
												+ ")", Double.class);
								credit = 0;
								total += debit - credit;
								// 放贷方
								vd_debit = credit;
								vd_credit = debit;
								vd_id = baseDao.getSeqId("VoucherDetail_SEQ");
								sqls.add("INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,vd_credit) VALUES ("
										+ vd_id
										+ ","
										+ vo_id
										+ ","
										+ vd_detno++
										+ ",'"
										+ rs.getGeneralString("cs_catecode")
										+ "','结转制造费用'," + vd_debit + "," + vd_credit + ")");
								for (int i = 0, len = ass.length; i < len; i++) {
									String[] s = ass[i].split("#");
									sqls.add("INSERT INTO VoucherDetailAss(vds_id,vds_vdid,vds_detno,vds_asstype,vds_asscode,vds_assname,vds_type) VALUES(VoucherDetailAss_SEQ.nextval,"
											+ vd_id + "," + (i + 1) + ",'" + s[0] + "','" + s[1] + "','" + s[2] + "','Voucher')");
								}
							}
						} else {
							sql = "SELECT * FROM CategoryAss WHERE ca_catecode='" + rs.getGeneralString("cs_catecode")
									+ "' AND ca_debit+ca_credit>0";
							SqlRowList rsl = baseDao.queryForRowSet(sql);
							assname = baseDao.getFieldDataByCondition("AssKind", "ak_name", "ak_code='" + asstype + "'");
							while (rsl.next()) {
								debit = rsl.getGeneralDouble("ca_debit", 2);
								credit = rsl.getGeneralDouble("ca_credit", 2);
								total += debit - credit;
								// 放贷方
								vd_credit = debit != 0 ? debit : credit * -1;
								vd_debit = 0;
								vd_id = baseDao.getSeqId("VoucherDetail_SEQ");
								sqls.add("INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,"
										+ "vd_credit) VALUES (" + vd_id + "," + vo_id + "," + vd_detno++ + ",'"
										+ rsl.getGeneralString("ca_catecode") + "','结转制造费用'," + vd_debit + "," + vd_credit + ")");
								sqls.add("INSERT INTO VoucherDetailAss(vds_id,vds_vdid,vds_detno,vds_asstype,vds_asscode,vds_assname,vds_type) VALUES(VoucherDetailAss_SEQ.nextval,"
										+ vd_id
										+ ",1,'"
										+ assname
										+ "','"
										+ rsl.getGeneralString("ca_asscode")
										+ "','"
										+ rsl.getGeneralString("ca_assname") + "','Voucher')");
							}
						}
					} else {
						debit = rs.getGeneralDouble("cs_debit", 2);
						credit = rs.getGeneralDouble("cs_credit", 2);
						total += debit - credit;
						// 放贷方
						vd_credit = debit != 0 ? debit : credit * -1;
						vd_debit = 0;
						vd_doubledebit = 0;
						vd_doublecredit = 0;
						vd_currency = "";
						vd_rate = 0;
						if (!"0".equals(rs.getObject("ca_currencytype"))) {// 复币
							debit = rs.getGeneralDouble("cs_doubledebit", 2);
							credit = rs.getGeneralDouble("cs_doublecredit", 2);
							vd_currency = rs.getGeneralString("ca_currency");
							if (debit > 0) {
								vd_doublecredit = debit;
								vd_rate = NumberUtil.formatDouble(vd_credit / debit, 10);
							}
							if (credit > 0) {
								vd_doubledebit = credit;
								vd_rate = NumberUtil.formatDouble(vd_debit / credit, 10);
							}
						}
						sqls.add("INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,"
								+ "vd_credit,vd_doubledebit,vd_doublecredit,vd_currency,vd_rate) VALUES (VoucherDetail_SEQ.nextval,"
								+ vo_id + "," + vd_detno++ + ",'" + rs.getGeneralString("cs_catecode") + "','结转制造费用'," + vd_debit + ","
								+ vd_credit + "," + vd_doubledebit + "," + vd_doublecredit + ",'" + vd_currency + "'," + vd_rate + ")");
					}
				}
			}
			if (account3) {
				sql = "SELECT cs_catecode,cs_debit,cs_credit,cs_doubledebit,cs_doublecredit,ca_currency,ca_currencytype,ca_asstype FROM CategorySET,Category "
						+ "WHERE cs_catecode=ca_code AND abs(ca_isleaf)=1 AND ca_code like '"
						+ manMakeCatecode
						+ "%' AND cs_debit+cs_credit<>0";
				rs = baseDao.queryForRowSet(sql);
				while (rs.next()) {
					asstype = rs.getString("ca_asstype");
					if (asstype != null) {
						if (asstype.indexOf("#") > 0) {
							sql = "select ass, str_concat(vds_vdid) vd_id from (select str_concat(vds_asstype || '#' || vds_asscode || '#' || vds_assname) ass,vds_vdid from (select vds_vdid,vds_asstype,vds_asscode,vds_assname from voucherdetailass left join voucherdetail on vds_vdid=vd_id where vd_catecode='"
									+ rs.getGeneralString("cs_catecode")
									+ "' and vd_void in(select vo_id from voucher where vo_yearmonth="
									+ yearmonth + ") order by vds_asstype,vds_asscode,vds_assname) group by vds_vdid) group by ass";
							SqlRowList rsl = baseDao.queryForRowSet(sql);
							String[] ass = null;
							while (rsl.next()) {
								ass = rsl.getGeneralString(1).split(",");
								debit = baseDao.getJdbcTemplate().queryForObject(
										"select round(SUM(vd_debit-vd_credit),2) from VoucherDetail Where vd_id IN (" + rsl.getObject(2)
												+ ")", Double.class);
								credit = 0;
								total += debit - credit;
								// 放贷方
								vd_debit = credit;
								vd_credit = debit;
								vd_id = baseDao.getSeqId("VoucherDetail_SEQ");
								sqls.add("INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,vd_credit) VALUES ("
										+ vd_id
										+ ","
										+ vo_id
										+ ","
										+ vd_detno++
										+ ",'"
										+ rs.getGeneralString("cs_catecode")
										+ "','结转制造费用'," + vd_debit + "," + vd_credit + ")");
								for (int i = 0, len = ass.length; i < len; i++) {
									String[] s = ass[i].split("#");
									sqls.add("INSERT INTO VoucherDetailAss(vds_id,vds_vdid,vds_detno,vds_asstype,vds_asscode,vds_assname,vds_type) VALUES(VoucherDetailAss_SEQ.nextval,"
											+ vd_id + "," + (i + 1) + ",'" + s[0] + "','" + s[1] + "','" + s[2] + "','Voucher')");
								}
							}
						} else {
							sql = "SELECT * FROM CategoryAss WHERE ca_catecode='" + rs.getGeneralString("cs_catecode")
									+ "' AND ca_debit+ca_credit>0";
							SqlRowList rsl = baseDao.queryForRowSet(sql);
							assname = baseDao.getFieldDataByCondition("AssKind", "ak_name", "ak_code='" + asstype + "'");
							while (rsl.next()) {
								debit = rsl.getGeneralDouble("ca_debit", 2);
								credit = rsl.getGeneralDouble("ca_credit", 2);
								total += debit - credit;
								// 放贷方
								vd_credit = debit != 0 ? debit : credit * -1;
								vd_debit = 0;
								vd_id = baseDao.getSeqId("VoucherDetail_SEQ");
								sqls.add("INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,"
										+ "vd_credit) VALUES (" + vd_id + "," + vo_id + "," + vd_detno++ + ",'"
										+ rsl.getGeneralString("ca_catecode") + "','结转制造费用'," + vd_debit + "," + vd_credit + ")");
								sqls.add("INSERT INTO VoucherDetailAss(vds_id,vds_vdid,vds_detno,vds_asstype,vds_asscode,vds_assname,vds_type) VALUES(VoucherDetailAss_SEQ.nextval,"
										+ vd_id
										+ ",1,'"
										+ assname
										+ "','"
										+ rsl.getGeneralString("ca_asscode")
										+ "','"
										+ rsl.getGeneralString("ca_assname") + "','Voucher')");
							}
						}
					} else {
						debit = rs.getGeneralDouble("cs_debit", 2);
						credit = rs.getGeneralDouble("cs_credit", 2);
						total += debit - credit;
						// 放贷方
						vd_credit = debit != 0 ? debit : credit * -1;
						vd_debit = 0;
						vd_doubledebit = 0;
						vd_doublecredit = 0;
						vd_currency = "";
						vd_rate = 0;
						if (!"0".equals(rs.getObject("ca_currencytype"))) {// 复币
							debit = rs.getGeneralDouble("cs_doubledebit", 2);
							credit = rs.getGeneralDouble("cs_doublecredit", 2);
							vd_currency = rs.getGeneralString("ca_currency");
							if (debit > 0) {
								vd_doublecredit = debit;
								vd_rate = NumberUtil.formatDouble(vd_credit / debit, 10);
							}
							if (credit > 0) {
								vd_doubledebit = credit;
								vd_rate = NumberUtil.formatDouble(vd_debit / credit, 10);
							}
						}
						sqls.add("INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,"
								+ "vd_credit,vd_doubledebit,vd_doublecredit,vd_currency,vd_rate) VALUES (VoucherDetail_SEQ.nextval,"
								+ vo_id + "," + vd_detno++ + ",'" + rs.getGeneralString("cs_catecode") + "','结转制造费用'," + vd_debit + ","
								+ vd_credit + "," + vd_doubledebit + "," + vd_doublecredit + ",'" + vd_currency + "'," + vd_rate + ")");
					}
				}
			}
			// 产生本年利润的分录
			vd_debit = NumberUtil.formatDouble(total, 2);
			vd_credit = 0;
			// if (total < 0) {// 统一放借方
			// vd_debit *= -1;
			// }
			sqls.add("INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,"
					+ "vd_credit) VALUES (VoucherDetail_SEQ.nextval," + vo_id + "," + vd_detno++ + ",'" + makeToCatecode + "','结转制造费用',"
					+ vd_debit + "," + vd_credit + ")");
			baseDao.execute(sqls.toArray(new String[sqls.size()]));
			// 判断凭证是否合法
			voucherDao.validVoucher(vo_id);
			String error = baseDao.getJdbcTemplate().queryForObject("select vo_errstring from Voucher Where vo_id=?", String.class, vo_id);
			String codeStr = "<br><a href=\"javascript:openUrl('jsps/fa/ars/voucher.jsp?formCondition=vo_idIS" + vo_id
					+ "&gridCondition=vd_voidIS" + vo_id + "')\">" + vo_code + "</a>";
			if (error != null && error.trim().length() > 0) {
				return "产生的凭证有问题，请打开凭证查看!<br>" + codeStr;
			} else {
				if (account) {
					baseDao.updateByCondition("Voucher", "vo_status='" + BaseUtil.getLocalMessage("AUDITED")
							+ "',vo_statuscode='AUDITED',vo_checkby='" + SystemSession.getUser().getEm_name() + "'", "vo_id=" + vo_id);
					baseDao.callProcedure("SP_WriteVoucher", new Object[] { yearmonth });
					return "已成功产生生产成本结转凭证并记账!<br>" + codeStr;
				} else {
					return "已成功产生生产成本结转凭证!<br>" + codeStr;
				}
			}
		}
		return "未产生生产成本结转凭证!";
	}

	private String addVoucherSql(String yearmonth, int vo_id, String vo_code, String pdcode) {
		Map<String, Object> periods = voucherDao.getPeriodsDate(pdcode, Integer.parseInt(yearmonth));
		String lead = StringUtil.valueOf(periods.get("vo_lead"));
		String vo_number = voucherDao.getVoucherNumber(yearmonth, lead, null);
		return "INSERT INTO Voucher(vo_id,vo_code,vo_yearmonth,vo_lead,vo_number,vo_emid,vo_recordman,vo_status,"
				+ "vo_statuscode,vo_recorddate,vo_explanation,vo_currencytype,vo_printstatus,vo_date)" + " VALUES (" + vo_id + ",'"
				+ vo_code + "'," + yearmonth + ",'" + (lead == null ? "" : lead) + "'," + vo_number + ","
				+ SystemSession.getUser().getEm_id() + ",'" + SystemSession.getUser().getEm_name() + "','"
				+ BaseUtil.getLocalMessage("ENTERING") + "','ENTERING',sysdate,'结转制造费用',0,'未打印'," + periods.get("pd_enddate") + ")";
	}

	@Override
	public String mainCreate(Boolean account) {
		Map<String, Object> periods = voucherDao.getJustPeriods("MONTH-A");// 直接取总账的期间
		String yearmonth = String.valueOf(periods.get("PD_DETNO"));
		// 先查找有没有已经生成的主营业务成本凭证
		Object[] vo = baseDao.getFieldsDataByCondition("Voucher", "vo_id,vo_code", "vo_yearmonth=" + yearmonth
				+ " AND (vo_explanation like '%主营业务成本%' or vo_source like '%主营业务成本%')");
		if (vo != null && vo[1] != null) {
			return "主营业务成本已经结转,凭证号:" + "<a href=\"javascript:openUrl('jsps/fa/ars/voucher.jsp?formCondition=vo_idIS" + vo[0]
					+ "&gridCondition=vd_voidIS" + vo[0] + "')\">" + vo[1] + "</a>,不能再次结转!";
		}
		String vsCode = "ArBillCostSum";
		String condition = "ab_class='应收发票' and to_char(ab_date,'yyyymm')=" + yearmonth;
		if (baseDao.isDBSetting("useBillOutAR")) {
			vsCode = "BillOutCostSum";
			condition = "to_char(bi_date,'yyyymm')=" + yearmonth;
			// 有未记账的发票、票据
			String error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(bi_code) from (select bi_code from billout where to_char(bi_date,'yyyymm')=? and bi_statuscode<>'POSTED') where rownum < 20",
							String.class, yearmonth);
			if (error != null)
				return "还有未记账的开票记录：<br>" + error;
		} else {
			String error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(ab_code) from (select ab_code from arbill where to_char(ab_date,'yyyymm')=? and ab_class='应收发票' and ab_statuscode<>'POSTED') where rownum < 20",
							String.class, yearmonth);
			if (error != null)
				return "还有未记账的发票：<br>" + error;
		}
		String result = baseDao.callProcedure("FA_VOUCHERCREATE", new Object[] { yearmonth, vsCode, condition, "merge", "主营业务成本", "GL",
				SystemSession.getUser().getEm_id(), SystemSession.getUser().getEm_name() });
		if (result != null && result.length() > 0) {
			return result;
		}
		vo = baseDao.getFieldsDataByCondition("Voucher", "vo_id,vo_code", "vo_yearmonth=" + yearmonth
				+ " AND (vo_explanation like '%主营业务成本%' or vo_source like '%主营业务成本%')");
		if (vo != null && vo[1] != null) {
			String codeStr = "<a href=\"javascript:openUrl('jsps/fa/ars/voucher.jsp?formCondition=vo_idIS" + vo[0]
					+ "&gridCondition=vd_voidIS" + vo[0] + "')\">" + vo[1] + "</a>";
			voucherDao.validVoucher(Integer.parseInt(String.valueOf(vo[0])));
			String error = baseDao.getJdbcTemplate().queryForObject("select vo_errstring from Voucher Where vo_id=?", String.class, vo[0]);
			if (error != null && error.trim().length() > 0) {
				return "产生的凭证有问题，请打开凭证查看!<br>" + codeStr;
			} else {
				if (account) {
					baseDao.updateByCondition("Voucher", "vo_status='" + BaseUtil.getLocalMessage("AUDITED")
							+ "',vo_statuscode='AUDITED',vo_checkby='" + SystemSession.getUser().getEm_name() + "'", "vo_id=" + vo[0]);
					baseDao.callProcedure("SP_WriteVoucher", new Object[] { yearmonth });
					return "已成功产生主营业务成本结转凭证并记账!<br>" + codeStr;
				} else {
					return "主营业务成本结转成功,凭证号:" + codeStr;
				}
			}
		}
		return "未产生主营业务成本结转凭证!";
	}

	@Override
	public String unCreate() {
		Map<String, Object> periods = voucherDao.getJustPeriods("MONTH-A");
		String yearmonth = String.valueOf(periods.get("PD_DETNO"));
		Object[] vo = baseDao.getFieldsDataByCondition("Voucher", "vo_id,vo_code,vo_statuscode", "vo_yearmonth=" + yearmonth
				+ " AND (vo_explanation like '%主营业务成本%' or vo_source like '%主营业务成本%')");
		if (vo == null) {
			return "本期" + yearmonth + "的主营业务成本未结转凭证!";
		}
		if ("ACCOUNT".equals(vo[2])) {// 已记账的凭证，不允许取消
			return "本期" + yearmonth + "主营业务成本结转的凭证" + vo[1] + "已记账，不允许取消!";
		}
		if (baseDao.isDBSetting("Voucher", "unCreateAudit")) {
			if ("AUDITED".equals(vo[2])) {// 已记账的凭证，不允许取消
				return "本期" + yearmonth + "主营业务成本结转的凭证" + vo[1] + "已审核，不允许取消!";
			}
		}
		baseDao.deleteByCondition("VoucherDetailAss", "vds_vdid in (select vd_id from VoucherDetail where vd_void=" + vo[0] + ")");
		baseDao.deleteByCondition("VoucherDetail", "vd_id in (select vd_id from VoucherDetail where vd_void=" + vo[0] + ")");
		baseDao.deleteByCondition("VoucherFlow", "vf_voucherid =" + vo[0]);
		// 存在先做完主营业务凭证，再切换开票模式的情况
		SqlRowList rs = baseDao.queryForRowSet("select * from voucherstyle where vs_code='ArBillCostSum' or vs_code='BillOutCostSum'");
		while (rs.next()) {
			baseDao.updateByCondition(rs.getGeneralString("vs_pritable"), rs.getGeneralString("vs_voucfield") + "=null",
					rs.getGeneralString("vs_voucfield") + "='" + vo[1] + "'");
		}
		baseDao.deleteByCondition("Voucher", "vo_id =" + vo[0]);
		baseDao.deleteByCondition("VoucherBill", "vb_void =" + vo[0]);
		return null;
	}

	@Override
	public String engineeringCreate(String enCatecode, String gsCatecode, Boolean account) {
		Map<String, Object> periods = voucherDao.getJustPeriods("MONTH-A");
		String yearmonth = String.valueOf(periods.get("PD_DETNO"));
		if (enCatecode == null || enCatecode.length() == 0)
			enCatecode = baseDao.getDBSetting("EngineeringFeeClose", "enCatecode");// 生产成本-工程成本科目
		if (enCatecode == null || enCatecode.length() == 0) {
			return "生产成本-工程成本科目未设置，不能进行工程成本结转!";
		}
		boolean isAllAccounted = baseDao
				.checkByCondition("Voucher", "vo_yearmonth=" + yearmonth + " AND nvl(vo_statuscode,' ')<>'ACCOUNT'");
		if (!isAllAccounted) {
			return "本月还有未记账的凭证，不能进行工程成本结转!";
		}
		Object[] vo = baseDao.getFieldsDataByCondition("Voucher left join VoucherDetail on vo_id=vd_void", "vo_id,vo_code", "vo_yearmonth="
				+ yearmonth + " AND vo_explanation like '%结转生产成本-工程成本%' and vd_catecode='" + gsCatecode + "'");
		if (vo != null && vo[1] != null) {
			return "当前期间已产生结转生产成本-工程成本凭证,凭证号:" + "<a href=\"javascript:openUrl('jsps/fa/ars/voucher.jsp?formCondition=vo_idIS" + vo[0]
					+ "&gridCondition=vd_voidIS" + vo[0] + "')\">" + vo[1] + "</a>,不能再次结转!";
		}
		String sql = "SELECT cs_catecode,cs_debit,cs_credit,cs_doubledebit,cs_doublecredit,ca_currency,ca_currencytype,ca_asstype FROM CategorySET,Category "
				+ "WHERE cs_catecode=ca_code AND abs(ca_isleaf)=1 AND ca_code like '" + enCatecode + "%' AND cs_debit+cs_credit<>0";
		SqlRowList rs = baseDao.queryForRowSet(sql);
		if (rs.hasNext()) {
			List<String> sqls = new ArrayList<String>();
			int vo_id = baseDao.getSeqId("VOUCHER_SEQ");
			String vo_code = baseDao.sGetMaxNumber("Voucher", 2);
			sqls.add(addVoucherSql(yearmonth, vo_id, vo_code, "MONTH-A"));
			String asstype = null;
			Object assname = null;
			int vd_detno = 1;
			double debit = 0;
			double credit = 0;
			double vd_debit = 0;
			double vd_credit = 0;
			double vd_doubledebit = 0;
			double vd_doublecredit = 0;
			String vd_currency = "";
			double total = 0;
			double vd_rate = 1;
			int vd_id = 0;
			boolean issum = true;
			while (rs.next()) {
				asstype = rs.getString("ca_asstype");
				if (asstype != null) {
					if (asstype.indexOf("#") > 0) {
						sql = "select ass, str_concat(vds_vdid) vd_id from (select str_concat(vds_asstype || '#' || vds_asscode || '#' || vds_assname) ass,vds_vdid from (select vds_vdid,vds_asstype,vds_asscode,vds_assname from voucherdetailass left join voucherdetail on vds_vdid=vd_id where vd_catecode='"
								+ rs.getGeneralString("cs_catecode")
								+ "' and vd_void in(select vo_id from voucher where vo_yearmonth="
								+ yearmonth + ") order by vds_asstype,vds_asscode,vds_assname) group by vds_vdid) group by ass";
						SqlRowList rsl = baseDao.queryForRowSet(sql);
						String[] ass = null;
						while (rsl.next()) {
							ass = rsl.getGeneralString(1).split(",");
							debit = baseDao.getJdbcTemplate().queryForObject(
									"select round(SUM(vd_debit-vd_credit),2) from VoucherDetail Where vd_id IN(" + rsl.getObject(2) + ")",
									Double.class);
							credit = 0;
							total += debit - credit;
							// 放贷方
							vd_debit = credit;
							vd_credit = debit;
							vd_id = baseDao.getSeqId("VoucherDetail_SEQ");
							sqls.add("INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,vd_credit) VALUES ("
									+ vd_id
									+ ","
									+ vo_id
									+ ","
									+ vd_detno++
									+ ",'"
									+ rs.getGeneralString("cs_catecode")
									+ "','结转生产成本-工程成本'," + vd_debit + "," + vd_credit + ")");
							for (int i = 0, len = ass.length; i < len; i++) {
								String[] s = ass[i].split("#");
								sqls.add("INSERT INTO VoucherDetailAss(vds_id,vds_vdid,vds_detno,vds_asstype,vds_asscode,vds_assname,vds_type) VALUES(VoucherDetailAss_SEQ.nextval,"
										+ vd_id + "," + (i + 1) + ",'" + s[0] + "','" + s[1] + "','" + s[2] + "','Voucher')");
							}
						}
						sql = "SELECT VDS_ASSCODE sacode,sum(vd_debit-vd_credit) amount FROM VOUCHERDETAILASS LEFT JOIN VOUCHERDETAIL ON VDS_VDID=VD_ID WHERE VD_CATECODE='"
								+ rs.getGeneralString("cs_catecode")
								+ "' and VDS_ASSTYPE='销售合同' and vd_void in (select vo_id from voucher where vo_yearmonth="
								+ yearmonth
								+ ") group by VDS_ASSCODE";
						SqlRowList rs2 = baseDao.queryForRowSet(sql);
						if (rs2.hasNext()) {
							issum = false;
							while (rs2.next()) {
								String sacode = rs2.getGeneralString("sacode");
								Double amount = rs2.getGeneralDouble("amount");
								int vd_id2 = baseDao.getSeqId("VoucherDetail_SEQ");
								sqls.add("INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,"
										+ "vd_credit) VALUES (" + vd_id2 + "," + vo_id + "," + vd_detno++ + ",'" + gsCatecode
										+ "','结转生产成本-工程成本'," + amount + ",0)");
								SqlRowList gsass = baseDao.queryForRowSet(
										"select ca_assname from category where ca_code=? and nvl(ca_assname,' ')<>' '", gsCatecode);
								if (gsass.next()) {
									String assStr = gsass.getString("ca_assname");
									String[] codes = assStr.split("#");
									int detno = 1;
									for (String gsassname : codes) {
										int vds_id = baseDao.getSeqId("VOUCHERDETAILASS_SEQ");
										sqls.add("INSERT INTO VoucherDetailAss(vds_id,vds_vdid,vds_detno,vds_asstype,vds_asscode,vds_assname,vds_type) VALUES("
												+ vds_id + "," + vd_id2 + "," + detno + ",'" + gsassname + "',null,null,'Voucher')");
										if ("销售合同".equals(gsassname)) {
											sqls.add("update VoucherDetailAss set vds_asscode='" + sacode + "', vds_assname='" + sacode
													+ "' where vds_id=" + vds_id);
										}
										if ("客户往来".equals(gsassname)) {
											sqls.add("update VoucherDetailAss set (vds_asscode, vds_assname)=(select ct_varchar50_26,ct_varchar50_6 from CUSTOMTABLE where ct_caller='GL0008' and ct_varchar50_1='"
													+ sacode + "') where vds_id=" + vds_id);
										}
										detno = detno + 1;
									}
								}
							}
						}
					} else {
						sql = "SELECT * FROM CategoryAss WHERE ca_catecode='" + rs.getGeneralString("cs_catecode")
								+ "' AND ca_debit+ca_credit>0";
						SqlRowList rsl = baseDao.queryForRowSet(sql);
						assname = baseDao.getFieldDataByCondition("AssKind", "ak_name", "ak_code='" + asstype + "'");
						while (rsl.next()) {
							debit = rsl.getGeneralDouble("ca_debit", 2);
							credit = rsl.getGeneralDouble("ca_credit", 2);
							total += debit - credit;
							// 放贷方
							vd_credit = debit != 0 ? debit : credit * -1;
							vd_debit = 0;
							vd_id = baseDao.getSeqId("VoucherDetail_SEQ");
							sqls.add("INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,"
									+ "vd_credit) VALUES (" + vd_id + "," + vo_id + "," + vd_detno++ + ",'"
									+ rsl.getGeneralString("ca_catecode") + "','结转生产成本-工程成本'," + vd_debit + "," + vd_credit + ")");
							sqls.add("INSERT INTO VoucherDetailAss(vds_id,vds_vdid,vds_detno,vds_asstype,vds_asscode,vds_assname,vds_type) VALUES(VoucherDetailAss_SEQ.nextval,"
									+ vd_id
									+ ",1,'"
									+ assname
									+ "','"
									+ rsl.getGeneralString("ca_asscode")
									+ "','"
									+ rsl.getGeneralString("ca_assname") + "','Voucher')");
							int vd_id2 = baseDao.getSeqId("VoucherDetail_SEQ");
							sqls.add("INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,"
									+ "vd_credit) VALUES (" + vd_id2 + "," + vo_id + "," + vd_detno++ + ",'" + gsCatecode
									+ "','结转生产成本-工程成本'," + (debit - credit) + ",0)");
						}
						sql = "SELECT ca_asscode sacode,ca_debit-ca_credit amount FROM CategoryAss WHERE ca_catecode='"
								+ rs.getGeneralString("cs_catecode") + "' and ca_ASSTYPE='销售合同' AND ca_debit+ca_credit>0";
						SqlRowList rs2 = baseDao.queryForRowSet(sql);
						if (rs2.hasNext()) {
							issum = false;
							while (rs2.next()) {
								String sacode = rs2.getGeneralString("sacode");
								Double amount = rs2.getGeneralDouble("amount");
								int vd_id2 = baseDao.getSeqId("VoucherDetail_SEQ");
								sqls.add("INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,"
										+ "vd_credit) VALUES (" + vd_id2 + "," + vo_id + "," + vd_detno++ + ",'" + gsCatecode
										+ "','结转生产成本-工程成本'," + amount + ",0)");
								SqlRowList gsass = baseDao.queryForRowSet(
										"select ca_assname from category where ca_code=? and nvl(ca_assname,' ')<>' '", gsCatecode);
								if (gsass.next()) {
									String assStr = gsass.getString("ca_assname");
									String[] codes = assStr.split("#");
									int detno = 1;
									for (String gsassname : codes) {
										int vds_id = baseDao.getSeqId("VOUCHERDETAILASS_SEQ");
										sqls.add("INSERT INTO VoucherDetailAss(vds_id,vds_vdid,vds_detno,vds_asstype,vds_asscode,vds_assname,vds_type) VALUES("
												+ vds_id + "," + vd_id2 + "," + detno + ",'" + gsassname + "',null,null,'Voucher')");
										if ("销售合同".equals(gsassname)) {
											sqls.add("update VoucherDetailAss set vds_asscode='" + sacode + "', vds_assname='" + sacode
													+ "' where vds_id=" + vds_id);
										}
										if ("客户往来".equals(gsassname)) {
											sqls.add("update VoucherDetailAss set (vds_asscode, vds_assname)=(select cu_code,ct_varchar50_6 from CUSTOMTABLE left join customer on ct_varchar50_6=cu_name where ct_caller='GL0008' and ct_varchar50_1='"
													+ sacode + "') where vds_id=" + vds_id);
										}
										detno = detno + 1;
									}
								}
							}
						}
					}
				} else {
					debit = rs.getGeneralDouble("cs_debit", 2);
					credit = rs.getGeneralDouble("cs_credit", 2);
					total += debit - credit;
					// 放贷方
					vd_credit = debit != 0 ? debit : credit * -1;
					vd_debit = 0;
					vd_doubledebit = 0;
					vd_doublecredit = 0;
					vd_currency = "";
					vd_rate = 0;
					if (!"0".equals(rs.getObject("ca_currencytype"))) {// 复币
						debit = rs.getGeneralDouble("cs_doubledebit", 2);
						credit = rs.getGeneralDouble("cs_doublecredit", 2);
						vd_currency = rs.getGeneralString("ca_currency");
						if (debit > 0) {
							vd_doublecredit = debit;
							vd_rate = NumberUtil.formatDouble(vd_credit / debit, 10);
						}
						if (credit > 0) {
							vd_doubledebit = credit;
							vd_rate = NumberUtil.formatDouble(vd_debit / credit, 10);
						}
					}
					sqls.add("INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,"
							+ "vd_credit,vd_doubledebit,vd_doublecredit,vd_currency,vd_rate) VALUES (VoucherDetail_SEQ.nextval," + vo_id
							+ "," + vd_detno++ + ",'" + rs.getGeneralString("cs_catecode") + "','结转生产成本-工程成本'," + vd_debit + ","
							+ vd_credit + "," + vd_doubledebit + "," + vd_doublecredit + ",'" + vd_currency + "'," + vd_rate + ")");
				}
			}
			if (issum) {
				// 产生本年利润的分录
				vd_debit = NumberUtil.formatDouble(total, 2);
				vd_credit = 0;
				sqls.add("INSERT INTO VoucherDetail(vd_id,vd_void,vd_detno,vd_catecode,vd_explanation,vd_debit,"
						+ "vd_credit) VALUES (VoucherDetail_SEQ.nextval," + vo_id + "," + vd_detno++ + ",'" + gsCatecode
						+ "','结转生产成本-工程成本'," + vd_debit + "," + vd_credit + ")");
			}
			baseDao.execute(sqls.toArray(new String[sqls.size()]));
			baseDao.execute("update voucherdetail set vd_detno=10000+vd_detno where vd_void=" + vo_id);
			baseDao.execute("update voucherdetail a set vd_detno=(select r from (select rownum r,vd_id from (select vd_id from voucherdetail left join voucher on vd_void=vo_id where vd_void="
					+ vo_id + " order by case when vd_debit=0 then 1 else 0 end,vd_detno)) t where a.vd_id=t.vd_id) where vd_void=" + vo_id);
			// 判断凭证是否合法
			voucherDao.validVoucher(vo_id);
			String error = baseDao.getJdbcTemplate().queryForObject("select vo_errstring from Voucher Where vo_id=?", String.class, vo_id);
			String codeStr = "<br><a href=\"javascript:openUrl('jsps/fa/ars/voucher.jsp?formCondition=vo_idIS" + vo_id
					+ "&gridCondition=vd_voidIS" + vo_id + "')\">" + vo_code + "</a>";
			if (error != null && error.trim().length() > 0) {
				return "产生的凭证有问题，请打开凭证查看!<br>" + codeStr;
			} else {
				if (account) {
					baseDao.updateByCondition("Voucher", "vo_status='" + BaseUtil.getLocalMessage("AUDITED")
							+ "',vo_statuscode='AUDITED',vo_checkby='" + SystemSession.getUser().getEm_name() + "'", "vo_id=" + vo_id);
					baseDao.callProcedure("SP_WriteVoucher", new Object[] { yearmonth });
					return "已成功产生结转生产成本-工程成本凭证并记账!<br>" + codeStr;
				} else {
					return "已成功产生结转生产成本-工程成本凭证!<br>" + codeStr;
				}
			}
		}
		return "未产生生产成本结转凭证!";
	}
}
