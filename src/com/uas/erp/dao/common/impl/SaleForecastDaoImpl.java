package com.uas.erp.dao.common.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.SaleForecastDao;

@Repository
public class SaleForecastDaoImpl extends BaseDao implements SaleForecastDao{

	static final String CHECK_YQTY = "SELECT sf_code,sd_detno,sd_qty FROM SaleForecastDetail left join SaleForecast on sd_sfid=sf_id WHERE sd_id=? and sd_qty<?";
	/**
	 * 报价单转入销售单之前，
	 * 1.判断通知单状态
	 * 2.判断thisqty ≤ qty +CQTY - yqty
	 */
	@Override
	public void checkSFyqty(List<Map<Object, Object>> datas) {
		int id = 0; 
		SqlRowList rs = null;
		boolean bool = false;
		Object[] sfs = null;
		for (Map<Object, Object> d : datas) {
			id = Integer.parseInt(d.get("sd_id").toString());
			sfs = getFieldsDataByCondition("SaleForecastDetail left join SaleForecast on sd_sfid=sf_id",
					"sf_code,sd_detno,nvl(sd_yqty,0)", "sd_id=" + id);
			if (sfs != null) {
				bool = checkIf("SaleForecast", "sf_code='" + sfs[0] + "' and (sf_statuscode='AUDITED' or sf_statuscode='PART2SA')");
				if (!bool) {
					BaseUtil.showError("销售预测单:" + sfs[0] + " 未审核通过,无法转销售订单!");
				}
				sfs[2]=sfs[2]==null?0:sfs[2];
				rs = queryForRowSet(CHECK_YQTY, id, Double.parseDouble(sfs[2].toString()) + 
						Double.parseDouble(d.get("sd_thisqty").toString()));
				if(rs.next()) {
					StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],销售预测单号:")
						.append(rs.getString("sf_code"))
						.append(",行号:")
						.append(rs.getInt("sd_detno"))
						.append(",预测数量:")
						.append(rs.getDouble("sd_qty"))
						.append(",已转订单数:")
						.append(sfs[2].toString())
						.append(",本次数:")
						.append(d.get("sd_thisqty"));
					BaseUtil.showError(sb.toString());
				}
			}
		}
	}
	/**
	 * 修改销售预测单状态
	 * sd_yqty：转了销售单未产生冲销单的数量
	 */
	@Override
	public void udpatestatus(int sdid){
		Object sfid = getFieldDataByCondition("SaleForecastDetail", "sd_sfid", "sd_id=" + sdid);
		int total = getCountByCondition("SaleForecastDetail", "sd_sfid=" + sfid);
		int aud = getCountByCondition("SaleForecastDetail", "sd_sfid=" + sfid +" AND nvl(sd_yqty,0)+nvl(sd_clashsaleqty,0)=0");
		int turn = getCountByCondition("SaleForecastDetail", "sd_sfid=" + sfid +" AND nvl(sd_yqty,0)=nvl(sd_qty,0)");
		String status = "PART2SA";
		if(aud == total) {
			status = "";
		} else if(turn == total) {
			status = "TURNSA";
		}
		updateByCondition("SaleForecast", "SF_TURNSTATUSCODE='" + status + "',SF_TURNSTATUS='" + 
				BaseUtil.getLocalMessage(status) + "'", "sf_id=" + sfid);
	}
}
