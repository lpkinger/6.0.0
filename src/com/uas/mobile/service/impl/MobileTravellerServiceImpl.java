package com.uas.mobile.service.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uas.erp.dao.BaseDao;
import com.uas.mobile.service.MobileTravellerService;


@Service
public class MobileTravellerServiceImpl implements MobileTravellerService{
    
	@Autowired
	private BaseDao baseDao;
	
	@Override
	public List<Map<String,Object>> getBussinessTrip(String emcode) {
		boolean bool = baseDao.checkIf("employee", "em_code='"+emcode+"' and nvl(em_class,' ') <>'离职'");
		if(bool) {
			List<Map<String,Object>> feeplease = baseDao.queryForList("select fp_id,fp_preenddate,fp_prestartdate,fp_code,"
					+ "fp_v3,fp_people2 from feeplease where fp_people2='"+emcode+"' and fp_statuscode in ('AUDITED','COMMITED') order by fp_id desc"); 
			List<Map<String,Object>> temp = new ArrayList<Map<String,Object>>();
			for (Map<String, Object> map : feeplease) {
				List<Map<String,Object>> feepleasedetail = baseDao.queryForList("select fpd_id,fpd_res_type ,fpd_order_code,fpd_air_starting,fpd_citycode1,fpd_air_destination,fpd_citycode2," + 
						"fpd_train_starting,fpd_citycode3,fpd_train_destination,fpd_citycode4,fpd_hotel_city,fpd_citycode5,fpd_hotel_address,fpd_flight_code,fpd_start_time,fpd_end_time,fpd_status,fpd_order_type,fpd_seat,fpd_real_fee,fpd_expecte_fee," + 
						"fpd_level,fpd_all_time,fpd_pay_type,fpd_business_name,fpd_number,fpd_remark  from feepleasedetail where fpd_fpid="+map.get("FP_ID")+" order by fpd_id desc");
				map.put("reimbursement", feepleasedetail);
				temp.add(map);
			}
			return temp;
		}else {
			return null;
		}
	}
	 
}
