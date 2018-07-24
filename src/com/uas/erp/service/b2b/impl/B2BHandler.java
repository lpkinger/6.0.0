package com.uas.erp.service.b2b.impl;

import org.hsqldb.lib.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.dao.BaseDao;

@Service("B2BHandler")
public class B2BHandler {

	@Autowired
	private BaseDao baseDao;

	/**
	 * 送货提醒单转出货界面，查询前，更新送货提醒单关联的订单号、订单序号等。 <br>
	 * 存在送货提醒单转到卖家系统的时候，saledown还未转订单的情况。
	 * 
	 * @param condition
	 */
	public void updateSaleNotifyDownBeforeQuery(String condition) {
		baseDao.execute("update SaleNotifyDown set (sn_ordercode,sn_orderdetno,sn_orderqty,sn_prodcode)=(select sale.sa_code,saledetail.sd_detno,saledetail.sd_qty,saledowndetail.sd_prodcode from saledowndetail left join saledown on saledowndetail.sd_said=saledown.sa_id left join saledetail on saledetail.sd_sourceid=saledowndetail.sd_id left join sale on sale.sa_id=saledetail.sd_said and sale.sa_sourcetype='CUSTPO' and sale.sa_pocode=saledown.sa_code where saledown.sa_code=SaleNotifyDown.sn_pocode and saledowndetail.sd_detno=SaleNotifyDown.sn_podetno) where sn_ordercode is null and sn_pocode is not null and sn_podetno is not null "
				+ (!StringUtil.isEmpty(condition) ? (" and " + condition) : ""));
	}

}
