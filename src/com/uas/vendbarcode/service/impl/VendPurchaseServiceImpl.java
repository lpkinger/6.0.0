package com.uas.vendbarcode.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.vendbarcode.service.VendPurchaseService;
@Service
public class VendPurchaseServiceImpl implements VendPurchaseService {

	@Autowired
	private BaseDao baseDao;
	
	@Override
	public Map<String, Object> getPurchaseList(String caller,String condition,Integer page,Integer start,Integer pageSize,Object vendcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Object total = null;
		int start1 = ((page - 1) * pageSize + 1);
		int end = page * pageSize;
		SqlRowList rs = null;
		if(condition == null || ("").equals(condition)){
			condition = " 1=1";
		}
		if(("VendPurchase").equals(caller)){
			total = baseDao.getFieldDataByCondition("purchase", "count(1)", " pu_statuscode='AUDITED' and pu_vendcode ='"+vendcode+"' and "+condition);
			rs = baseDao.queryForRowSet("select * from (select tt.*,rownum rn from (select pu_id,pu_code,pu_vendcode,pu_vendname,pu_status,pu_statuscode,to_char(pu_date,'yyyy-MM-dd') pu_date,pu_buyercode,pu_buyername,pu_vendcontact,pu_refcode,pu_shipaddresscode from purchase where pu_statuscode='AUDITED' and pu_vendcode =?  and "+condition+" order by pu_id desc)tt "
					+" where rownum<=? )where rn>=?",vendcode,end,start1);
		}else if(("VendPurchase!UnDelivery").equals(caller)){
			total = baseDao.getFieldDataByCondition("purchasedetail  left join purchase on pd_puid=pu_id left join product on pd_prodcode = pr_code "
						+" left join scm_purchaseturnqty_view on pd_id = v_pd_id", "count(count(1))", " pu_statuscode = 'AUDITED' and pu_vendcode ='"+vendcode+"' and  "+condition+" AND nvl(pd_qty,0)>nvl(pd_acceptqty,0) and nvl(pd_qty,0)>nvl(pd_yqty,0) and "
						+" nvl(pd_qty,0)  - nvl(V_PD_TURNACCEPTNOTIFY,0)>0 group by pu_id");
			rs = baseDao.queryForRowSet("select * from (select tt.*,rownum rn from (select pu_id,pu_code,pu_vendcode,pu_vendname,pu_status,pu_statuscode,to_char(pu_date,'yyyy-MM-dd') pu_date,pu_buyercode,pu_buyername,pu_vendcontact,pu_refcode,pu_shipaddresscode from purchasedetail  left join purchase on pd_puid=pu_id left join product on pd_prodcode = pr_code "
						+" left join scm_purchaseturnqty_view on pd_id = v_pd_id where pu_statuscode = 'AUDITED' and pu_vendcode =? and "+ condition+" AND nvl(pd_qty,0)>nvl(pd_acceptqty,0) and nvl(pd_qty,0)>nvl(pd_yqty,0) and "
						+" nvl(pd_qty,0) - nvl(V_PD_TURNACCEPTNOTIFY,0)>0 group by pu_id,pu_code,pu_vendcode,pu_vendname,pu_status,pu_statuscode,pu_date,pu_buyercode,pu_buyername,pu_vendcontact,pu_refcode,pu_shipaddresscode order by pu_id desc)tt  where rownum<=? )where rn>=?",vendcode,end,start1);
		}
		if(rs.next()){			
			modelMap.put("datas", rs.getResultList());
			modelMap.put("total", total);
		}
		return modelMap;
	}

	@Override
	public Map<String, Object> getPurchaseForm(String caller, Integer id) {
		SqlRowList rs = baseDao.queryForRowSet("select pu_id,pu_code,to_char(pu_date,'yyyy-MM-dd HH24:mi:ss') pu_date,pu_vendcode,pu_vendname,to_char(pu_delivery,'yyyy-MM-dd HH24:mi:ss') pu_delivery,pu_shipaddresscode,pu_statuscode from purchase where pu_id = ?",id);
		if(rs.next()){
			return rs.getCurrentMap();
		}else{
			return null;
		}
	}

	@Override
	public List<Map<String, Object>> getPurchaseGrid(String caller, Integer id) {
		SqlRowList rs = baseDao.queryForRowSet("select * from purchasedetail left join product on pd_prodcode = pr_code where pd_puid = ?",id);
		if(rs.next()){
			return rs.getResultList();
		}else{			
			return null;
		}
	}
}
