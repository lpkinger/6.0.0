package com.uas.erp.service.fa.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.PreapBillService;

@Service
public class PreapBillServiceImpl implements PreapBillService {
	@Autowired
	private BaseDao baseDao;

	@Override
	@Transactional
	public int turn(final int pb_id, String caller) {
		final Employee employee = SystemSession.getUser();
		final Object[] vendor = baseDao.getFieldsDataByCondition(
				"vendor left join PreapBill on pb_otherenid=ve_uu",
				new String[] { "ve_uu", "ve_code", "ve_name", "ve_payment",
						"ve_paymentcode", "ve_currency", "ve_buyername" },
				"pb_id=" + pb_id);
		// 更新PreapBill原纪录
		String sql = "update  PreapBill set pb_vendcode=?,pb_otherenname=?,pb_buyer=?,pb_payments=?,pb_currency=?,pb_sureman=?,pb_suredate=?,pb_surestatus=? where pb_id=?";
		baseDao.getJdbcTemplate().update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, vendor[1] + "");
				ps.setString(2, vendor[2] + "");
				ps.setString(3, vendor[6] + "");
				ps.setString(4, vendor[3] + "");
				ps.setString(5, vendor[5] + "");
				ps.setString(6, employee.getEm_name());
				ps.setDate(7, new java.sql.Date(new java.util.Date().getTime()));
				ps.setString(8, "已转发票");
				ps.setInt(9, pb_id);
			}
		});
		final Object[] PreapBill = baseDao.getFieldsDataByCondition(
				"PreapBill", new String[] { "pb_refno", "pb_apamount",
						"pb_taxsum" }, "pb_id=" + pb_id);
		final int ab_id = baseDao.getSeqId("APBILL_SEQ");
		// APBILL主纪录
		String insertSql = "insert into APBILL(ab_id,ab_code,ab_date,ab_yearmonth,ab_currency,ab_auditstatus,ab_vendcode,ab_buyer,ab_status,"
				+ "ab_paymentcode,ab_payments,ab_refno,ab_class,ab_recorder,ab_indate,ab_apamount,ab_printstatus,ab_auditstatuscode,"
				+ "ab_statuscode,ab_printstatuscode,ab_paystatuscode,ab_paystatus,ab_taxsum)"
				+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		baseDao.getJdbcTemplate().update(insertSql,
				new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps)
							throws SQLException {
						long time = new java.util.Date().getTime();
						Calendar cal = Calendar.getInstance();
						int year = cal.get(Calendar.YEAR);
						int month = cal.get(Calendar.MONTH) + 1;
						String my = year + "" + month;
						ps.setInt(1, ab_id);
						ps.setString(2, baseDao.sGetMaxNumber("APBill!CWIM", 2));
						ps.setDate(3, new java.sql.Date(time));
						ps.setInt(4, Integer.parseInt(my));
						ps.setString(5, vendor[5] + "");
						ps.setString(6, "在录入");
						ps.setString(7, vendor[1] + "");
						ps.setString(8, vendor[6] + "");// ab_buyer
						ps.setString(9, "未过账");
						ps.setString(10, vendor[4] + "");
						ps.setString(11, vendor[3] + "");
						ps.setString(12, PreapBill[0] + "");// ab_refno
						ps.setString(13, "应付发票");
						ps.setString(14, employee.getEm_name());
						ps.setDate(15, new java.sql.Date(time));
						ps.setDouble(16, Double.parseDouble(PreapBill[1] + ""));
						ps.setString(17, "未打印");// ab_printstatus
						ps.setString(18, "ENTERING");
						ps.setString(19, "UNPOST");
						ps.setString(20, "UNPRINT");
						ps.setString(21, "UNCOLLECT");
						ps.setString(22, "未收款");
						ps.setDouble(23, Double.parseDouble(PreapBill[2] + ""));
					}
				});
		// 取明细的数据
		List<Object[]> preapbilldetail = baseDao.getFieldsDatasByCondition(
				"preapbilldetail", new String[] { "pbd_detno", "pbd_ordercode",
						"pbd_orderdetno",
						"pbd_date",
						"pbd_prodcode",// 4
						"pbd_currency", "pbd_price", "pbd_thisvoqty",
						"pbd_amount", "pbd_taxrate", "pbd_qty",
						"pbd_thisvoprice", "pbd_apamount",// 12
						"pbd_noapamount", "pbd_taxamount", "pbd_remark" },
				"pbd_pbid=" + pb_id);
		String insertDetail = "insert into APBillDetail (abd_id,abd_abid,abd_detno,abd_ordercode,abd_orderdetno,abd_date,abd_currency,"
				+ "abd_prodcode,abd_price,abd_taxrate,abd_amount,abd_qty,abd_thisvoprice,abd_apamount,abd_noapamount,abd_taxamount,abd_remark)"
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		//
		for (final Object[] d : preapbilldetail) {
			baseDao.getJdbcTemplate().update(insertDetail,
					new PreparedStatementSetter() {
						@Override
						public void setValues(PreparedStatement ps)
								throws SQLException {
							ps.setInt(1, baseDao.getSeqId("APBillDetail_seq"));
							ps.setInt(2, ab_id);
							ps.setInt(3, Integer.parseInt(d[0] + ""));
							ps.setString(4, d[1] + "");
							ps.setString(5, d[2] + "");
							ps.setDate(
									6,
									new java.sql.Date(DateUtil
											.parseStringToDate(d[3] + "",
													Constant.YMD_HMS).getTime()));
							ps.setString(7, d[5] + "");
							ps.setString(8, d[4] + "");
							ps.setDouble(9, Double.parseDouble(d[6] + ""));
							ps.setDouble(10, Double.parseDouble(d[9] + ""));// abd_taxrate
							ps.setDouble(11, Double.parseDouble(d[8] + ""));
							ps.setDouble(12, Double.parseDouble(d[10] + ""));
							ps.setDouble(13, Double.parseDouble(d[11] + ""));
							ps.setDouble(14, Double.parseDouble(d[12] + ""));// abd_apamount
							ps.setDouble(15, Double.parseDouble(d[13] + ""));
							ps.setDouble(16, Double.parseDouble(d[14] + ""));
							ps.setString(17, d[15] + "");
						}
					});
		}
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), "转发票",
				BaseUtil.getLocalMessage("msg.turnSuccess"), "APBILL|ab_id="
						+ ab_id));
		return ab_id;
	}

}
