package com.uas.erp.service.fa.impl;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;


import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.ChangeSellerService;

@Service("changeSellerService")
public class ChangeSellerServiceImpl implements ChangeSellerService {
	@Autowired
	private BaseDao baseDao;


	@Override
	public void changeSeller(String condition,String caller) {
		JSONObject d = JSONObject.fromObject(condition);
		//boolean customer = d.getBoolean("customer"); // 客户资料
		boolean sale = d.getBoolean("sale"); // 销售订单
		boolean sendnotify = d.getBoolean("sendnotify"); // 出货通知单
		boolean prodio = d.getBoolean("prodio"); // 出货单/销售退货单
		boolean arbill = d.getBoolean("arbill"); // 应收发票
		boolean sellermonth = d.getBoolean("sellermonth"); 
		String sa_sellercode = String.valueOf(d.get("sellercode1"));
		String sn_sellercode = String.valueOf(d.get("sellercode2"));
		/*if (customer) {
			baseDao.execute("update customer set cu_sellercode='"+ sn_sellercode +
					"', cu_sellername=(select em_name from employee where em_code='"+
					sn_sellercode +"') where cu_sellercode='"+sa_sellercode+"'");
		}*/
		if (sale) {
			baseDao.execute("update sale set sa_sellercode='"+ sn_sellercode +
					"', sa_seller=(select em_name from employee where em_code='"+
					sn_sellercode +"') where sa_sellercode='"+sa_sellercode+"' and nvl(sa_sendstatuscode,' ') <> 'TURNOUT'");
		}
		if (sendnotify) {
			baseDao.execute("update SendNotify set sn_sellercode='"+ sn_sellercode +
					"', sn_sellername=(select em_name from employee where em_code='"+
					sn_sellercode +"') where sn_sellercode='"+sa_sellercode+"' and nvl(sn_status,' ') <> 'TURNOUT'");
		}
		if (prodio) {
			baseDao.execute("update ProdInOut set pi_sellercode='"+ sn_sellercode +
					"', pi_sellername=(select em_name from employee where em_code='"+
					sn_sellercode +"') where pi_sellercode='"+sa_sellercode+
					"' and nvl(pi_invoicecode,' ') <> ' ' and pi_class in ('出货单','销售退货单')");
		}
		if (arbill) {
			baseDao.execute("update arbill set (ab_sellerid, ab_seller)=(select em_id,em_name from employee where em_code='"+
					sn_sellercode +"') where ab_seller=(select em_name from employee where em_code='"+
					sn_sellercode +"') and abs(ab_aramount) > abs(ab_payamount)");
		}
		if (sellermonth) {
			int yearmonth = DateUtil.getYearmonth();
			SqlRowList rs = baseDao.queryForRowSet("select sm_custcode,sm_beginamount,sm_nowamount,sm_payamount,sm_endamount from sellermonth where sm_sellercode=? and sm_yearmonth=?", sa_sellercode,yearmonth);
			while(rs.next()){
				int count = baseDao.getCountByCondition("sellermonth", "sm_custcode='" + rs.getObject("sm_custcode") + "' and sm_yearmonth=" + yearmonth + " and sm_sellercode='" + sn_sellercode + "'");
				if(count <= 0){
					baseDao.execute("update sellermonth set (sm_sellerid,sm_sellercode,sm_sellername)=(select em_id,em_code,em_name from employee where em_code=?) where sm_sellercode=? and sm_yearmonth=? and sm_custcode=?",
							sn_sellercode,sa_sellercode,yearmonth,rs.getObject("sm_custcode"));
				} else {
					baseDao.execute("update sellermonth set sm_beginamount=sm_beginamount+"+rs.getDouble("sm_beginamount")+", sm_payamount=sm_payamount+"+rs.getDouble("sm_payamount")
							+",sm_nowamount=sm_nowamount+"+rs.getDouble("sm_nowamount")+",sm_endamount=sm_endamount+"+rs.getDouble("sm_endamount")+" where sm_sellercode='"+sn_sellercode
							+"' and sm_yearmonth="+yearmonth +" and sm_custcode='"+rs.getObject("sm_custcode")+"'");
					baseDao.execute("delete from sellermonth where sm_sellercode=? and sm_yearmonth=? and sm_custcode=?",
							sa_sellercode,yearmonth,rs.getObject("sm_custcode"));
				}
			}
		}
		try {
			// 记录操作
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.changeSeller"),
					BaseUtil.getLocalMessage("msg.changeSellerSuccess"), sa_sellercode + "->" + sn_sellercode));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
