package com.uas.erp.dao.common.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.VoucherDetailAss;

@Repository
public class VoucherDaoImpl extends BaseDao implements VoucherDao {

	/**
	 * 取凭证号
	 * 
	 * @param currentMonth
	 *            月份
	 * @param lead
	 *            凭证字
	 * @param fromNumber
	 *            从{fromNumber}开始找
	 */
	@Override
	public String getVoucherNumber(String currentMonth, String lead, Integer fromNumber) {
		lead = !StringUtil.hasText(lead) ? " " : lead;
		SqlRowList list = queryForRowSet(
				"select nvl(min(b),0) from (select tab.a, rownum b from (SELECT vo_number a FROM Voucher WHERE vo_yearmonth=? AND vo_number>? AND nvl(vo_lead,' ')=? order by vo_number) tab) where a>b",
				currentMonth, fromNumber == null ? 0 : fromNumber, lead);
		Integer num = 1;
		if (list.next()) {
			num = list.getGeneralInt(1);
			if (num == 0) {
				Object max = getFieldDataByCondition("Voucher", "nvl(max(vo_number),0) + 1", "vo_yearmonth=" + currentMonth
						+ " AND nvl(vo_lead,' ')='" + lead + "' AND vo_number>" + (fromNumber == null ? 0 : fromNumber));
				if (max != null) {
					num = Integer.parseInt(max.toString());
				}
			}
		}
		// 再判断vo_number是否存在
		int count = getCountByCondition("Voucher", "vo_yearmonth=" + currentMonth + " AND nvl(vo_lead,' ')='" + lead + "' AND vo_number="
				+ num);
		if (count > 0) {
			return getVoucherNumber(currentMonth, lead, num);
		}
		return String.valueOf(num);
	}

	/**
	 * get YearMonth
	 * 
	 * @param periods
	 *            "Month-A"
	 */
	@Override
	public int getPeriodsFromDate(String periods, String date) {
		StringBuffer sb = new StringBuffer("SELECT pd_detno FROM PeriodsDetail WHERE ");
		sb.append("pd_code='");
		sb.append(periods.toUpperCase());
		sb.append("' AND pd_enddate >= ");
		sb.append(DateUtil.parseDateToOracleString(null, date));
		sb.append(" ORDER BY pd_detno");
		SqlRowList list = queryForRowSet(sb.toString());
		if (list.next()) {
			return list.getInt("pd_detno");
		} else {
			String[] d = date.split("-");
			String year = d[0];
			String month = d[1];
			String ym = year + month;
			String start = DateUtil.getMinMonthDate(date);
			String end = DateUtil.getMaxMonthDate(date);
			execute("INSERT INTO PeriodsDetail(pd_code,pd_detno,pd_startdate,pd_enddate,pd_status,pd_year) VALUES (?,?,?,?,0,?)", periods,
					ym, DateUtil.parse(start, null), DateUtil.parse(end, null), year);
			return Integer.parseInt(ym);
		}
	}

	public Map<String, Object> getPeriodsDate(String periods, Integer date) {
		StringBuffer sb = new StringBuffer(
				"SELECT pe_volead,pd_detno,pd_startdate,pd_enddate FROM Periods left join PeriodsDetail on pe_code=pd_code WHERE ");
		sb.append("pd_code='");
		sb.append(periods.toUpperCase());
		sb.append("' AND pd_detno=");
		sb.append(date);
		SqlRowList list = queryForRowSet(sb.toString());
		if (list.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("vo_lead", list.getString(1));
			map.put("pd_detno", list.getObject(2));
			map.put("pd_startdate", DateUtil.parseDateToOracleString(null, Timestamp.valueOf(list.getObject(3).toString())));
			map.put("pd_enddate", DateUtil.parseDateToOracleString(null, Timestamp.valueOf(list.getObject(4).toString())));
			return map;
		}
		return null;
	}

	/**
	 * @param period
	 *            PeriodsDetail.pd_code
	 */
	public Map<String, Object> getJustPeriods(String period) {
		SqlRowList list = queryForRowSet(
				"Select * From (Select * From Periodsdetail Where Pd_Code=? And Pd_Status=0 Order By Substr(Pd_Detno,1,6),Pd_Detno) where rownum=1",
				period.toUpperCase());
		Map<String, Object> data = null;
		if (list.next()) {
			data = list.getCurrentMap();
		} else {
			data = new HashMap<String, Object>();
			data.put("PD_DETNO", 0);
			data.put("PD_STARTDATE", new Date());
			data.put("PD_ENDDATE", new Date());
		}
		if (data != null) {
			// 前一个期间
			Integer pre = getJdbcTemplate()
					.queryForObject(
							"Select Pd_Detno From (Select Pd_Detno From Periodsdetail Where Pd_Code=? And Pd_Status=99 Order By Substr(Pd_Detno,1,6) Desc) Where Rownum=1",
							Integer.class, period.toUpperCase());
			if (pre != null) {
				data.put("PreYearmonth", pre);
			} else {
				data.put("PreYearmonth", data.get("PD_DETNO"));
			}
		}
		return data;
	}

	static final String trim_ass = "delete from voucherdetailass where vds_id in (select vds_id from voucherdetail left join voucherdetailass on vds_vdid=vd_id left join category on ca_code=vd_catecode where vd_void=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,vds_asstype)=0)";
	static final String del_ass = "delete from voucherdetailass where vds_vdid in (select vd_id from voucher left join voucherdetail on vd_void=vo_id left join category on ca_code=vd_catecode where vo_id=? and nvl(ca_asstype,' ')=' ')";
	static final String update_detail = "update voucherdetail set vd_debit=round(nvl(vd_debit,0),2),vd_doubledebit=round(nvl(vd_doubledebit,0),4),vd_credit=round(nvl(vd_credit,0),2),vd_doublecredit=round(nvl(vd_doublecredit,0),4) where vd_void=?";
	static final String valid_cate_err = "select wmsys.wm_concat(vd_detno) from voucherdetail where vd_void=? and nvl(vd_catecode,' ') not in (select ca_code from category)";
	static final String valid_cate = "select wmsys.wm_concat(vd_detno) from voucherdetail where vd_void=? and nvl(vd_catecode,' ')=' ' order by vd_detno";
	static final String valid_debitorcredit = "select wmsys.wm_concat(vd_detno) from voucherdetail where vd_void=? and (vd_debit*vd_credit>0.01 or vd_doubledebit*vd_doublecredit>0.01) order by vd_detno";
	static final String valid_nodebitorcredit = "select wmsys.wm_concat(vd_detno) from voucherdetail where vd_void=? and vd_debit=0 and vd_credit=0 and vd_doubledebit=0 and vd_doublecredit=0 order by vd_detno";
	static final String valid_cate_leaf = "select wmsys.wm_concat(vd_detno) from voucherdetail left join category on ca_code=vd_catecode where vd_void=? and abs(ca_isleaf)<>1 order by vd_detno";
	static final String valid_cate_fx = "select wmsys.wm_concat(vd_detno) from voucher left join voucherdetail on vo_id=vd_void left join category on ca_code=vd_catecode where vo_id=? and vo_explanation not like '%结转%' and nvl(ca_class,' ') like '损益%' and (((nvl(vd_debit,0)<>0 or nvl(vd_doubledebit,0)<>0) and nvl(ca_type,0)=1) or ((nvl(vd_credit,0)<>0 or nvl(vd_doublecredit,0)<>0) and nvl(ca_type,0)=0)) order by vd_detno";
	static final String valid_multiass = "select wmsys.wm_concat(vd_detno) from (select count(1) c,vd_detno,vds_asstype from voucherdetail left join voucherdetailass on vds_vdid=vd_id where vd_void=? and nvl(vds_asscode,' ')<>' ' group by vd_detno,vds_asstype) where c>1 order by vd_detno";
	static final String valid_ass = "select wmsys.wm_concat(vd_detno) from voucherdetail left join voucherdetailass on vds_vdid=vd_id left join category on ca_code=vd_catecode where vd_void=? and nvl(ca_assname,' ')<>' ' and (nvl(vds_asstype,' ')=' ' or nvl(vds_asscode,' ')=' ' or nvl(vds_assname,' ')=' ') order by vd_detno";
	static final String valid_asserr = "select wmsys.wm_concat(vd_detno) from voucherdetail left join voucherdetailass on vds_vdid=vd_id left join category on ca_code=vd_catecode where vd_void=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,vds_asstype)=0 order by vd_detno";
	static final String valid_doubleerr = "select wmsys.wm_concat(vd_detno) from voucherdetail left join category on ca_code=vd_catecode where vd_void=? and abs(ca_currencytype)=1 and nvl(vd_explanation,' ')<>'汇兑损益' and nvl(vd_explanation,' ')<>'调汇差' and (nvl(vd_currency,' ')<>' ' or nvl(vd_doubledebit,0)+nvl(vd_doublecredit,0)<>0) and (abs(nvl(vd_doubledebit,0)*nvl(vd_rate,0)-vd_debit)>0.01 or abs(nvl(vd_doublecredit,0)*nvl(vd_rate,0)-vd_credit)>0.01) order by vd_detno";
	static final String valid_currerr = "select wmsys.wm_concat(vd_detno) from voucherdetail left join category on ca_code=vd_catecode where vd_void=? and abs(ca_currencytype)=1 and nvl(vd_explanation,' ')<>'汇兑损益' and nvl(vd_explanation,' ')<>'调汇差' and (nvl(vd_currency,' ')=' ' or nvl(vd_doubledebit,0)+nvl(vd_doublecredit,0)=0) and (nvl(vd_doubledebit,0)*nvl(vd_rate,0)-vd_debit<0.01 or nvl(vd_doublecredit,0)*nvl(vd_rate,0)-vd_credit<0.01) order by vd_detno";
	static final String valid_currtype = "select wm_concat(vd_detno) from voucher left join voucherdetail on vo_id=vd_void left join category on vd_catecode=ca_code where vo_id=? and abs(ca_currencytype)=1 and nvl(vo_currencytype,0)=0";
	static final String valid_currerr_2 = "select wm_concat(vd_detno) from voucher left join voucherdetail on vo_id=vd_void left join category on vd_catecode=ca_code where vo_id=? and abs(ca_currencytype)=1 and (abs(ca_iscash)=1 or abs(ca_isbank)=1) and abs(nvl(vo_currencytype,0))=1 and vd_currency<>ca_currency";
	static final String update_err = "update voucher set vo_errstring=? where vo_id=?";
	static final String update_explan = "update voucher set vo_explanation=(select * from (select vd_explanation from voucherdetail where vd_void=? and nvl(vd_explanation,' ')<>' ' order by vd_detno) where rownum=1) where vo_id=?";
	static final String update_total = "update voucher set vo_total=(select sum(nvl(vd_debit,0)) from voucherdetail where vd_void=?) where vo_id=?";
	static final String update_totalupper = "update voucher set vo_totalupper=L2U(vo_total) where vo_id=?";
	static final String update_cashflow = "update voucher set vo_iscashflow=(select max(abs(nvl(ca_cashflow,0))) from voucherdetail left join category on ca_code=vd_catecode where vd_void=?) where vo_id=?";

	/**
	 * valid voucher 判断凭证合法性
	 * 
	 * @param vId
	 *            Voucher.vo_id
	 */
	@Override
	public void validVoucher(int vId) {
		execute("update VoucherDetail set VD_CATENAME=(select ca_name from category where VD_CATECODE=ca_code) where nvl(VD_CATECODE,' ')<>' ' and vd_void="
				+ vId);
		// 无明细行
		StringBuffer error = new StringBuffer();
		int count = getCountByCondition("VoucherDetail", "vd_void=" + vId);
		if (count == 0) {
			error.append("无明细行");
		} else {
			execute(trim_ass, vId);
			execute(del_ass, vId);
			// 更新明细
			execute(update_detail, vId);
			// 科目错误
			String str = getJdbcTemplate().queryForObject(valid_cate_err, String.class, vId);
			if (str != null) {
				error.append("科目错误,行:").append(str).append(";");
			}
			// 科目为空
			str = getJdbcTemplate().queryForObject(valid_cate, String.class, vId);
			if (str != null) {
				error.append("科目为空,行:").append(str).append(";");
			}
			// 既有借方,又有贷方
			str = getJdbcTemplate().queryForObject(valid_debitorcredit, String.class, vId);
			if (str != null) {
				error.append("既有借方,又有贷方,行:").append(str).append(";");
			}
			// 借、贷未填
			str = getJdbcTemplate().queryForObject(valid_nodebitorcredit, String.class, vId);
			if (str != null) {
				error.append("借、贷未填,行:").append(str).append(";");
			}
			// 有下级科目
			str = getJdbcTemplate().queryForObject(valid_cate_leaf, String.class, vId);
			if (str != null) {
				error.append("有下级科目,行:").append(str).append(";");
			}
			if (isDBSetting("Voucher", "catetypeCheck")) {
				// 科目方向与凭证发生额方向不一致
				str = getJdbcTemplate().queryForObject(valid_cate_fx, String.class, vId);
				if (str != null) {
					error.append("科目方向与凭证发生额方向不一致,行:").append(str).append(";");
				}
			}
			// 核算项重复
			str = getJdbcTemplate().queryForObject(valid_multiass, String.class, vId);
			if (str != null) {
				error.append("核算项重复,行:").append(str).append(";");
			}
			// 辅助核算不完善
			str = getJdbcTemplate().queryForObject(valid_ass, String.class, vId);
			if (str != null) {
				error.append("辅助核算不完善,行:").append(str).append(";");
			}
			// 核算项错误
			str = getJdbcTemplate().queryForObject(valid_asserr, String.class, vId);
			if (str != null) {
				error.append("核算项错误,行:").append(str).append(";");
			}
			// 核算项不存在
			str = "";
			SqlRowList rs = queryForRowSet(
					"select 'select '||vd_detno||',count(1) from '||ak_table||' where '||ak_asscode||'='''||vds_asscode||''' and '||AK_ASSNAME||'='''||VDS_ASSNAME||'''' from voucherdetailass left join asskind on vds_asstype=ak_name left join voucherdetail on vds_vdid=vd_id where vd_void=? order by vd_detno",
					vId);
			while (rs.next()) {
				SqlRowList rd = queryForRowSet(rs.getString(1));
				if (rd.next() && rd.getInt(2) == 0) {
					if (StringUtil.hasText(str)) {
						str = str + ",";
					}
					str += rd.getInt(1);
				}
			}
			if (str.length() > 0)
				error.append("核算编号+核算名称不存在,行:").append(str).append(";");
			str = getJdbcTemplate().queryForObject(valid_doubleerr, String.class, vId);
			if (str != null) {
				error.append("外币填写不正确,行:").append(str).append(";");
			}
			str = getJdbcTemplate().queryForObject(valid_currerr, String.class, vId);
			if (str != null) {
				error.append("外币未填写,行:").append(str).append(";");
			}
			// 银行、现金科目，币别要与科目里面一致
			str = getJdbcTemplate().queryForObject(valid_currerr_2, String.class, vId);
			if (str != null) {
				error.append("外币填写与科目资料不一致,行:").append(str).append(";");
			}
			// 不平衡
			Double diff = getJdbcTemplate().queryForObject("select abs(sum(vd_debit)-sum(vd_credit)) from voucherdetail where vd_void=?",
					Double.class, vId);
			if (diff >= 0.005) {
				error.append("不平衡;");
			}
			// 判断和来源单据期间不一致
			if (!checkMonthDate(vId))
				error.append("与来源单据期间不一致");
			// 更新主记录
			execute(update_explan, vId, vId);// 摘要
			execute(update_total, vId, vId);// total
			execute(update_totalupper, vId);// total
			execute(update_cashflow, vId, vId);// cashflow
		}
		if (error.length() > 0) {
			String errStr = error.toString();
			errStr = errStr.length() > 100 ? errStr.substring(0, 100) + "..." : errStr;
			execute(update_err, errStr, vId);// errstring
		} else {
			execute(update_err, null, vId);
		}
		// 现金流量金额计算
		String sql = "SELECT round(sum(nvl(vd_debit,0)),2),round(sum(nvl(vd_credit,0)),2),abs(ca_cashflow) ca_cashflow from VoucherDetail,Category,voucher where vd_catecode=ca_code and vd_void=vo_id and nvl(ca_cashflow,0)<>0 and vd_void=? group by abs(ca_cashflow)";
		SqlRowList rs = queryForRowSet(sql, vId);
		// double amount = 0;
		// double leaveAmount = 0;
		// double thisAmount = 0;
		if (rs.next()) {
			double vd_debit = rs.getGeneralDouble(1, 2);
			double vd_credit = rs.getGeneralDouble(2, 2);
			if (vd_debit != 0 || vd_credit != 0) {
				sql = "UPDATE VoucherDetail SET vd_creditcashflow=NVL(vd_credit,0),vd_debitcashflow=NVL(vd_debit,0) where vd_void=?";
				execute(sql, vId);
			}
			// // 借方对应科目
			// if (vd_debit != 0) {
			// amount = getSummaryByField(
			// "VoucherDetail left join Category on vd_catecode=ca_code",
			// "NVL(vd_credit,0)-NVL(vd_debit,0)",
			// "NVL(ca_cashflow,0)=0 and vd_void=" + vId);
			// if (vd_debit == amount) {
			// sql =
			// "UPDATE VoucherDetail a SET vd_creditcashflow=NVL(vd_credit,0),vd_debitcashflow=NVL(vd_debit,0) where exists (select 1 FROM Category WHERE a.vd_catecode=ca_code AND NVL(ca_cashflow,0)=0) and a.vd_void=?";
			// execute(sql, vId);
			// } else {
			// leaveAmount = vd_debit;
			// thisAmount = 0;
			// SqlRowList credit = queryForRowSet(
			// "select vd_credit,vd_detno FROM VoucherDetail,Category WHERE vd_catecode=ca_code and nvl(ca_cashflow,0)=0 and vd_void=? ORDER BY vd_detno",
			// vId);
			// while (credit.next()) {
			// thisAmount = credit
			// .getGeneralDouble("vd_credit", 2);
			// if (thisAmount < leaveAmount) {
			// leaveAmount = leaveAmount - thisAmount;
			// execute("UPDATE VoucherDetail SET vd_creditcashflow=? WHERE vd_void=? AND vd_detno=?",
			// thisAmount, vId,
			// credit.getInt("vd_detno"));
			// } else {
			// thisAmount = leaveAmount;
			// execute("UPDATE VoucherDetail SET vd_creditcashflow=? WHERE vd_void=? AND vd_detno=?",
			// thisAmount, vId,
			// credit.getInt("vd_detno"));
			// }
			// }
			// }
			// }
			// // 贷方对应科目
			// if (vd_credit != 0) {
			// amount = getSummaryByField(
			// "VoucherDetail left join Category on vd_catecode=ca_code",
			// "nvl(vd_debit-vd_credit,0)",
			// "NVL(ca_cashflow,0)=0 and vd_void=" + vId);
			// if (vd_credit == amount) {
			// sql =
			// "UPDATE VoucherDetail a SET vd_creditcashflow=NVL(vd_credit,0),vd_debitcashflow=NVL(vd_debit,0) where exists (select 1 FROM Category WHERE a.vd_catecode=ca_code AND NVL(ca_cashflow,0)=0) and a.vd_void=?";
			// execute(sql, vId);
			// } else {
			// leaveAmount = vd_credit;
			// thisAmount = 0;
			// SqlRowList debit = queryForRowSet(
			// "select vd_debit,vd_detno FROM VoucherDetail,Category WHERE vd_catecode=ca_code and nvl(ca_cashflow,0)=0 and vd_void=? ORDER BY vd_detno",
			// vId);
			// while (debit.next()) {
			// thisAmount = debit.getGeneralDouble("vd_debit", 2);
			// if (thisAmount < leaveAmount) {
			// leaveAmount = leaveAmount - thisAmount;
			// execute("UPDATE VoucherDetail SET vd_debitcashflow=? WHERE vd_void=? AND vd_detno=?",
			// thisAmount, vId,
			// debit.getInt("vd_detno"));
			// } else {
			// thisAmount = leaveAmount;
			// execute("UPDATE VoucherDetail SET vd_debitcashflow=? WHERE vd_void=? AND vd_detno=?",
			// thisAmount, vId,
			// debit.getInt("vd_detno"));
			// }
			// }
			// }
			// }
		}
	}

	@Override
	public int getNowPddetno(String periods) {
		SqlRowList rs = queryForRowSet(
				"Select Pd_Detno From (Select Pd_Detno From Periodsdetail Where Pd_Code=? And Pd_Status=0 Order By Substr(Pd_Detno,1,6),Pd_Detno) where rownum=1",
				periods.toUpperCase());
		if (rs.next()) {
			return rs.getInt(1);
		} else {
			return 0;
		}
	}

	@Override
	public int getNowPddetnoByType(String type) {
		SqlRowList rs = queryForRowSet(
				"Select Pd_Detno From (Select Pd_Detno From Periodsdetail Left Join Periods On Pe_Code=Pd_Code Where Pe_Type=? And Pd_Status = 0 Order By Substr(Pd_Detno,1,6),Pd_Detno) where rownum=1",
				type.toUpperCase());
		if (rs.next()) {
			return rs.getInt(1);
		} else {
			return 0;
		}
	}

	@Override
	public int getEndPddetno(String periods) {
		SqlRowList rs = queryForRowSet(
				"Select Pd_Detno From (Select Pd_Detno From Periodsdetail Where Pd_Code=? And Pd_Status=99 Order By Substr(Pd_Detno,1,6) Desc) where rownum=1",
				periods.toUpperCase());
		if (rs.next()) {
			return rs.getInt(1);
		} else {
			return 0;
		}
	}

	@Override
	public String unCreate(String vs_code, String mode, String datas, String vo_code, String vo_source) {
		SqlRowList rs = queryForRowSet("SELECT * FROM VoucherStyle WHERE vs_code=?", vs_code);
		if (rs.next()) {
			String tab = rs.getString("vs_pritable");
			String prifield = rs.getString("vs_prikey1");
			String voucfield = rs.getString("vs_voucfield");
			if ("merge".equals(mode)) {
				datas = "SELECT " + prifield + " FROM " + tab + " WHERE " + datas;
			}
			rs = queryForRowSet("SELECT vo_code FROM Voucher WHERE vo_code=? AND nvl(vo_statuscode,' ')='ACCOUNT'", vo_code);
			if (rs.next()) {
				if (rs.getString(1) != null)
					return "凭证:" + rs.getString(1) + " 已记账,不能取消凭证!";
			}
			rs = queryForRowSet("SELECT vo_id FROM Voucher WHERE vo_code=? AND nvl(vo_statuscode,' ')<>'ACCOUNT'", vo_code);
			if (rs.next()) {
				if (rs.getInt(1) > 0) {
					String condition = prifield + " IN(" + datas + ")";
					if ("PRODINOUT".equals(vs_code)) {
						condition += " AND pi_class='" + vo_source + "'";
					}
					updateByCondition(tab, voucfield + "=null", condition);
					deleteByCondition("VoucherBill", "vb_void=" + rs.getInt(1));
				}
			}
		}
		return null;
	}

	/**
	 * 单据期间与凭证期间不一致
	 * 
	 * @param VId
	 * @return false 期间不一致
	 */
	private boolean checkMonthDate(int voucherId) {
		SqlRowList rs = queryForRowSet("SELECT * FROM VoucherStyle WHERE vs_code=(select max(vb_vscode) from VoucherBill where vb_void=?)",
				voucherId);
		if (rs.next() && rs.getString("vs_datefield") != null) {
			int errDate = getCount("select count(1) from " + rs.getString("vs_pritable") + " left join voucher on "
					+ rs.getString("vs_voucfield") + "=vo_code where vo_id=" + voucherId + " and to_char(" + rs.getString("vs_datefield")
					+ ",'yyyymm')<>vo_yearmonth");
			return errDate == 0;
		}
		return true;
	}

	@Override
	public List<VoucherDetailAss> getAssByVoucherId(int vo_id) {
		return query(
				"select * from voucherdetailass where exists (select 1 from voucherdetail where vds_vdid=vd_id and vd_void=?) order by vds_detno",
				VoucherDetailAss.class, vo_id);
	}
}
