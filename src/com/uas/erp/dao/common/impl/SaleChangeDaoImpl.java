package com.uas.erp.dao.common.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.SaleChangeDao;
import com.uas.erp.dao.common.SaleDao;

@Repository
public class SaleChangeDaoImpl extends BaseDao implements SaleChangeDao{
	
	@Autowired
	private SaleDao saleDao;
	static final String SALECHANGEDETAIL = "SELECT SaleChangeDetail.*,pa_id,pa_name FROM SaleChangeDetail left join Payments on " + 
			" pa_code=scd_newpayments WHERE scd_scid=?";
	static final String UPDATESALE = "update sale set sa_plandelivery=?,sa_paymentscode=?,sa_updateman=?" +
			",sa_updatedate=?,sa_payments=?,sa_paymentsid=? where sa_code=?";
	
	@Override
	@Transactional
	public List<String> catchSale(int id) {
		SqlRowList rs = queryForRowSet(SALECHANGEDETAIL, id);
		String sacode = null;
		List<String> codes = new ArrayList<String>();
		while(rs.next()){
			sacode = rs.getString("scd_sacode");
			codes.add(sacode);
		}
		return codes;
	}
	@Override
	public void updateSaleStatus(String sa_code) {
		Object outtype = getFieldDataByCondition("SaleKind left join sale on sk_name=sa_kind", "sk_outtype", "sa_code='" + sa_code + "'");
		int total = 0;
		int aud = 0;
		int turn = 0;
		String sta = "";
		total = getCountByCondition("SaleDetail", "sd_code='" + sa_code + "'");
		//更新转通知状态
		if(("TURNSN").equals(outtype) ){			
			aud = getCountByCondition("SaleDetail", "sd_code='" + sa_code + "' AND nvl(sd_yqty,0)=0");
			turn = getCountByCondition("SaleDetail", "sd_code='" + sa_code
					+ "' AND nvl(sd_yqty,0)=nvl(sd_qty,0) and nvl(sd_yqty,0)>0");
			sta = "PART2SN";
			if (aud == total) {
				sta = "";
			} else if (turn == total) {
				sta = "TURNSN";
			}
			updateByCondition("Sale", "sa_turnstatuscode='" + sta + "',sa_turnstatus='" + BaseUtil.getLocalMessage(sta)
					+ "'", "sa_code='" + sa_code + "'");
			
		}
		//更新出货状态
		aud = getCountByCondition("SaleDetail", "sd_code='" + sa_code + "' AND nvl(sd_sendqty,0)=0");
		turn = getCountByCondition("SaleDetail", "sd_code='" + sa_code+ "' AND nvl(sd_sendqty,0)=nvl(sd_qty,0) and nvl(sd_sendqty,0)>=0");
		sta = "PARTOUT";
		if (aud == total) {
			sta = "";
		} else if (turn == total) {
			sta = "TURNOUT";
		} 
		updateByCondition("Sale", "sa_sendstatuscode='" + sta + "',sa_sendstatus='" + BaseUtil.getLocalMessage(sta)
				+ "'", "sa_code='" + sa_code + "'");
	}
}
