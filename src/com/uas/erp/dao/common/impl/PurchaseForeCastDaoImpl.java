package com.uas.erp.dao.common.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.PurchaseForeCastDao;
@Repository
public class PurchaseForeCastDaoImpl  extends BaseDao implements PurchaseForeCastDao {
	static final String SETVENDOR = "UPDATE PURCHASEFORECASTDETAIL SET PFD_vendcode=?,PFD_vendname=?,PFD_currency=? WHERE PFD_id=?";
	static final String GETVENDOR = "SELECT ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid FROM (SELECT ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid FROM PurchasePriceDetail,Currencys,PurchasePrice WHERE " + 
			"ppd_currency=cr_name and pp_id=ppd_ppid and pp_statuscode='AUDITED' and ppd_statuscode='VALID' and pp_kind='采购' and ppd_prodcode=(SELECT PFD_PRODCODE FROM PURCHASEFORECASTDETAIL where PFD_id=?) and nvl(ppd_todate, nvl(pp_todate, sysdate)) + 1>sysdate order by ppd_price*cr_rate*(1-nvl(ppd_rate,0)/(100+nvl(ppd_rate,0)))) where rownum<2";
	static final String GETVENDORAPP = "SELECT ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid FROM (SELECT ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid FROM PurchasePriceDetail,Currencys,PurchasePrice,product WHERE " + 
			"ppd_currency=cr_name and pp_id=ppd_ppid and ppd_prodcode=pr_code and pp_statuscode='AUDITED' and ppd_statuscode='VALID' and (nvl(ppd_appstatus,' ')='合格' or pr_material<>'已认可') and pp_kind='采购' and ppd_prodcode=(SELECT PFD_PRODCODE FROM PURCHASEFORECASTDETAIL where PFD_id=?) and nvl(ppd_todate, nvl(pp_todate, sysdate)) + 1>sysdate order by ppd_price*cr_rate*(1-nvl(ppd_rate,0)/(100+nvl(ppd_rate,0)))) where rownum<2";
    static final String CONFIRM="UPDATE PURCHASEFORECASTDETAIL SET PFD_THROWSTATUS='已确认',PFD_THROWSTATUSCODE='CONFIRMED',pfd_sendstatus='待上传' WHERE PFD_ID=?";
	@Override
     @Transactional
	 public void getVendor(int[] id) { 
		String sqlstr = null;
		if (isDBSetting("Application!ToPurchase!Deal", "onlyQualifiedPrice")) {
			sqlstr = GETVENDORAPP;
		} else {
			sqlstr = GETVENDOR;
		}
		for(int idx:id){
			SqlRowList rs = queryForRowSet(sqlstr, idx);
			if(rs.next()){
				execute(SETVENDOR, new Object[]{rs.getObject(1),rs.getObject(2),rs.getObject(3),idx});
			}
		}
	}
	@Override
	public void confirm(int[] id) { 
		// TODO Auto-generated method stub
		for(int idx:id){
			execute(CONFIRM,new Object[]{idx});
			execute("UPDATE PURCHASEFORECAST set pf_sendstatus='待上传' where pf_id in (select pfd_pfid from PURCHASEFORECASTDETAIL where pfd_id="+idx+") ");
		}
	}
}
