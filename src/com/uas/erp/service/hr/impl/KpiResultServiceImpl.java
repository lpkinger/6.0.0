package com.uas.erp.service.hr.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.HandlerService;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.hr.KpiResultService;
@Service
public class KpiResultServiceImpl implements KpiResultService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public List<Map<String, Object>> getKpiResult(String condition) {
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		String sql="select ktd_id,kdb_title,kd_startkind,kdb_period,kt_beman,kt_score,kt_level,ktd_description,ktd_score_from,ktd_score_to,ktd_score,kt_bemanid,kt_id,"
				+ "kt_kdbid from Kpiresult left join kpiresultdetail on kt_id=ktd_ktid left join Kpidesign_bill on kt_kdbid=kdb_id left join KPIDESIGN on "
				+ "KDB_KDCODE=kd_code where ktd_id is not null and "+condition+" order by kt_kdbid,kt_bemanid,ktd_description";
		SqlRowList rs=baseDao.queryForRowSet(sql);
		String kt_kdbid="";
		String kt_bemanid="";
		String ktd_id="";
		int index = 0;
		while(rs.next()){
			int flag=0;
			if(!kt_kdbid.equals(rs.getString("kt_kdbid"))){
				kt_kdbid=rs.getString("kt_kdbid");
				flag++;
			}
			if(!kt_bemanid.equals(rs.getString("kt_bemanid"))){
				kt_bemanid=rs.getString("kt_bemanid");
				flag++;
			}
			if(flag>0){
				ktd_id=rs.getString("ktd_id");
			}
			Map<String, Object> item = new HashMap<String, Object>();
			if(ktd_id.equals(rs.getString("ktd_id"))){
				index++;
				item.put("index",index);
				item.put("kdb_title", rs.getString("kdb_title"));
				item.put("kd_startkind", rs.getString("kd_startkind"));
				item.put("kdb_period", rs.getString("kdb_period"));
				item.put("kt_beman", rs.getString("kt_beman"));
				item.put("kt_score", rs.getString("kt_score"));
				item.put("kt_level", rs.getString("kt_level"));
				item.put("ktd_description", rs.getString("ktd_description"));
				item.put("ktd_score_from", rs.getString("ktd_score_from"));
				item.put("ktd_score_to", rs.getString("ktd_score_to"));
				item.put("ktd_score", rs.getString("ktd_score"));
				item.put("kt_bemanid", rs.getString("kt_bemanid"));
				item.put("kt_id", rs.getString("kt_id"));
				item.put("kt_kdbid", rs.getString("kt_kdbid"));
				store.add(item);
			}else if(kt_kdbid.equals(rs.getString("kt_kdbid"))&&kt_bemanid.equals(rs.getString("kt_bemanid"))){
				item.put("index",index);
				item.put("kdb_title", "");
				item.put("kd_startkind","");
				item.put("kdb_period","");
				item.put("kt_beman","");
				item.put("kt_score","");
				item.put("kt_level","");
				item.put("ktd_description", rs.getString("ktd_description"));
				item.put("ktd_score_from", rs.getString("ktd_score_from"));
				item.put("ktd_score_to", rs.getString("ktd_score_to"));
				item.put("ktd_score", rs.getString("ktd_score"));
				item.put("kt_bemanid", rs.getString("kt_bemanid"));
				item.put("kt_id", rs.getString("kt_id"));
				item.put("kt_kdbid", rs.getString("kt_kdbid"));
				store.add(item);
			}
		}
		return store;
	}
	
}
