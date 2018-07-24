package com.uas.erp.dao.common.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.SaleForeCastChangeDao;

@Repository
public class SaleForeCastChangeDaoImpl extends BaseDao implements SaleForeCastChangeDao {
	static final String TURNSALEFORECAST = "SELECT sc_sfid FROM saleforecastchange WHERE sc_id=?";
	static final String UPDATESALEFORECAST = "update saleforecast where sf_id=?";
	static final String TURNSALEFORECASTDETAIL = "SELECT * From saleforecastchangedetail WHERE scd_mainid=?";
	static final String UPDATESALEFORECASTDETAIL = "UPDATE saleforecastdetail SET sd_qty=?,sd_needdate=?,sd_enddate=?"
			+ " WHERE sd_sfid=? AND sd_detno=?";

	@Override
	@Transactional
	public int turnSaleForecast(int id) {
		SqlRowList rs = queryForRowSet(TURNSALEFORECAST, new Object[] { id });
		int sfid = 0;
		int leadday = 0;
		String Aheadday = "";
//		Aheadday = getDBSetting("ForeCastEndDateAhead");// -1~+N
//		Boolean haveenddate = isDBSetting("haveEndDate");// Y/N
		// 获取配置表的有效期延迟天数
		Aheadday = getDBSetting("SaleForecast", "lateDays");// -1~+N
		boolean haveenddate = isDBSetting("SaleForecast", "haveEndDate");// Y/N
//		if (Aheadday == null || Aheadday.equals("")) {
//			Aheadday = "0";
//		}
//		leadday = Integer.parseInt(Aheadday);
		if (!haveenddate) {
			leadday = 365;// 不考虑失效日期，则有效期为365天。
		} else {
			if (Aheadday == null || Aheadday.equals("")) {
				Aheadday = "0";
			}
			leadday = Integer.parseInt(Aheadday);
		}
		if (rs.next()) {
			sfid = rs.getInt("sc_sfid");
			boolean bool = true;
			if (bool) {
				rs = queryForRowSet(TURNSALEFORECASTDETAIL, new Object[] { id });
				while (rs.next()) {
					execute(UPDATESALEFORECASTDETAIL,
							new Object[] { rs.getObject("scd_newqty"), rs.getObject("scd_newdelivery"), rs.getObject("scd_newenddate"),
									sfid, rs.getObject("scd_pddetno") });
					execute("update SaleForecast set sf_updatedate=sysdate,sf_updateman='" + SystemSession.getUser().getEm_name()
							+ "' where sf_id =" + sfid);
					execute("update Saleforecastdetail set sd_changremark='销售预测变更'||to_char(sysdate,'YYYYMMDD') where sd_sfid=" + sfid + " and sd_detno=" + rs.getObject("scd_pddetno"));
					int argCount = getCountByCondition("user_tab_columns",
							"table_name='SALEFORECASTCHANGEDETAIL' and column_name in ('SCD_NEWPRODCODE','SCD_PRODCODE')");
					if (argCount == 2) {
						if (StringUtil.hasText(rs.getObject("scd_newprodcode"))) {
							if (!rs.getObject("scd_newprodcode").equals(rs.getObject("scd_prodcode"))) {
								execute("update Saleforecastdetail set sd_prodcode='" + rs.getObject("scd_newprodcode")
										+ "' where sd_sfid=" + sfid + " and sd_detno=" + rs.getObject("scd_pddetno"));
								execute("update Saleforecastdetail set sd_prodid=(select pr_id from Product where pr_code=sd_prodcode) where sd_sfid="
										+ sfid + " and sd_detno=" + rs.getObject("scd_pddetno"));
							}
						}
					}
					if (haveenddate && leadday != -1) {
						execute("update Saleforecastdetail set sd_enddate=sd_needdate+" + leadday + " where sd_sfid=" + sfid
								+ " and sd_detno=" + rs.getObject("scd_pddetno"));
					}
					if(StringUtil.hasText(rs.getObject("scd_newenddate")) && StringUtil.hasText(rs.getObject("scd_oldenddate")) && !rs.getObject("scd_newenddate").toString().equals(rs.getObject("scd_oldenddate").toString())){
						execute("update Saleforecastdetail set sd_enddate=? where sd_sfid=? and sd_detno=?",
								new Object[] { rs.getObject("scd_newenddate"),sfid, rs.getObject("scd_pddetno") });
					}
//					if (!haveenddate) {// 不启用预测有效期，则默认两年后有效
//						execute("update Saleforecastdetail set sd_enddate=add_months(sd_needdate,24) where sd_sfid=" + sfid
//								+ " and sd_detno=" + rs.getObject("scd_pddetno"));
//					}
				}
			}
		}
		return sfid;
	}

}
