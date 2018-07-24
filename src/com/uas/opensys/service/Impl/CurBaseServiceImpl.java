package com.uas.opensys.service.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.CurNavigation;
import com.uas.opensys.service.CurBaseService;
@Service
public class CurBaseServiceImpl implements CurBaseService {
	@Autowired
	private BaseDao  baseDao;
	@Override
	public List<CurNavigation> getCurNavigation() {
		List<CurNavigation> navigations=baseDao.getJdbcTemplate().query(
				"SELECT * FROM CURNAVIGATION ORDER BY CN_SUBOF,CN_DETNO", 
				new BeanPropertyRowMapper<CurNavigation>(CurNavigation.class));
		List<CurNavigation> alreadyNavs=new ArrayList<CurNavigation>();
		for(CurNavigation nav:navigations){
			if(nav.getCn_subof()==0){
				formatNavigations(navigations,nav);
				alreadyNavs.add(nav);
			}
			
		}
		return alreadyNavs;
	}
    private void formatNavigations(List<CurNavigation> navigations,CurNavigation nav){
    	/**
    	 * 客户服务模块只考虑两层结构*/
    	List<CurNavigation> children=new ArrayList<CurNavigation>();
    	for(CurNavigation n:navigations){
    		if(n.getCn_subof()==nav.getCn_id()){
    			children.add(n);
    	    }
        }
    	nav.setChildren(children);
    }
    /*
     * 客户服务客户编号名称更新
     * tablename 表名   cu_code 客户编号 cu_name客户名称 cu_uu客户UU condition更新条件
     */
	@Override
	public void updateCurInfo(String tablename, String cu_code, String cu_name,
			Object cu_uu,String condition) {
		if(cu_uu!=null&&!"".equals(cu_uu)){
			baseDao.execute("update "+tablename+" set ("+cu_code+","+cu_name +")= (select cu_code,cu_name from customer where cu_uu="+cu_uu+") where "+condition);
		}
		
	}
	@Override
	public  Map<String, Object> getCurNotify(String condition) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("data", baseDao.queryForList("select * from CurNotify where cn_status='未确认' and "+condition));		  		
		return map;
	}
}
