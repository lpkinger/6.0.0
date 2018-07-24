package com.uas.erp.dao.common.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.SaleDao;
import com.uas.erp.dao.common.SendNotifyChangeDao;
import com.uas.erp.dao.common.SendNotifyDao;

@Repository
public class SendNotifyChangeDaoImpl extends BaseDao implements SendNotifyChangeDao{
	static final String TURNDETAIL = "SELECT * FROM SendNotifyChangeDetail left join SendNotifyChange on scd_scid=sc_id WHERE scd_scid=?";
	static final String UPDATEDETAIL = "UPDATE SendNotifyDetail SET snd_outqty=?,snd_total=?,snd_taxtotal=? WHERE snd_id=?";
	@Autowired
	private SaleDao saleDao;
	@Autowired
	private SendNotifyDao sendNotifyDao;
	@Override
	@Transactional
	public String turnSendNotify(int id) {
		SqlRowList rs = queryForRowSet(TURNDETAIL, new Object[]{id});
		String sncode = null;
		Object sd_id = null;
		Object sndid = 0;
		Object qty = 0;
		Object aq = 0;
		Object r = 0;
		int flag = 0;
		List<String> sqls = new ArrayList<String>();
		while(rs.next()){
			//判断数量是否超订单数量
			sndid = rs.getObject("scd_sndid");
			double p = NumberUtil.formatDouble(rs.getDouble("scd_sendprice"), 6);
			double tax =rs.getDouble("scd_taxrate");
			double newqty =rs.getDouble("scd_qty");
			sd_id = getFieldDataByCondition("SendNotifyDetail", "snd_sdid", "snd_id=" + sndid);
			sncode = getFieldDataByCondition("SendNotifyDetail", "snd_code", "snd_id=" + sndid).toString();
			if(sd_id != null && Integer.parseInt(sd_id.toString()) > 0) {
				qty = getFieldDataByCondition("SendNotifyDetail", "sum(snd_outqty)", "snd_sdid=" + sd_id + 
						" AND snd_id <> " + sndid +" AND NVL(SND_STATUSCODE,' ')<>'FINISH'");
				/**
				 * 反馈编号：2017060155
				 * 计算销售订单已转数中销售退货数量时如果pd_sdId<>0　取pd_sdid，否则取pd_orderid
				 * @author wsy
				 */
				r = getFieldDataByCondition("ProdIODetail left join ProdInOut on pd_piid=pi_id", "sum(pd_inqty)", 
						"pd_piclass='销售退货单' and pi_statuscode='POSTED' and (case when nvl(pd_sdid,0)<>0 then pd_sdid else nvl(pd_orderid,0) end)=" + sd_id);
				qty = qty == null ? 0 : qty;
				r = r == null ? 0 : r;
				aq = getFieldDataByCondition("SaleDetail", "sd_qty", "sd_id=" + sd_id);
				if(Double.parseDouble(aq.toString()) + Double.parseDouble(r.toString()) < 
						Double.parseDouble(qty.toString()) + newqty ) {
						BaseUtil.showError("新数量超出原订单数量，原订单数["+Double.parseDouble(aq.toString())+"]，变更后通知单数["+(Double.parseDouble(qty.toString())+newqty)+"]，销售退货数量["+Double.parseDouble(r.toString())+"]，超出数量[" +
								(Double.parseDouble(qty.toString()) + newqty - Double.parseDouble(r.toString()) - Double.parseDouble(aq.toString()))+"]");
				} else {
					flag = 1;
				}
			}
			execute(UPDATEDETAIL, new Object[]{rs.getObject("scd_qty"),NumberUtil.formatDouble(p*newqty, 3), 
					NumberUtil.formatDouble(p*newqty / (1 + tax/100), 3), sndid});
			if(flag == 1) {
				updateByCondition("SaleDetail", "sd_yqty=" + 
					(Double.parseDouble(qty.toString()) + newqty - Double.parseDouble(r.toString())), "sd_id=" + sd_id);
			}
			int argCount = getCountByCondition("user_tab_columns",
					"table_name='SENDNOTIFYCHANGEDETAIL' and column_name in ('SCD_NEWPAYMENTSCODE','SCD_PAYMENTSCODE')");
			if (argCount == 2) {
				if (rs.getObject("scd_paymentscode") != rs.getObject("scd_newpaymentscode")) {
					// 更新收款方式
					sqls.add("update SendNotify set sn_paymentscode='" + rs.getObject("scd_newpaymentscode") + "' where sn_code='" + rs.getObject("sc_sncode") + "'");
					sqls.add("update SendNotify set (sn_payments,sn_payment)=(select pa_id ,pa_name from payments where pa_code=sn_paymentscode AND pa_class='收款方式') where sn_code='" + rs.getObject("sc_sncode") + "'");
				}
			}
			execute(sqls);
			saleDao.updateturnstatus(Integer.parseInt(sd_id.toString()));
			sendNotifyDao.checkSNDQty(Integer.parseInt(sndid.toString()), null);
		}
		return sncode;
	}	
}
