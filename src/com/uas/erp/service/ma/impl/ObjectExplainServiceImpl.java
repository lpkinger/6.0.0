package com.uas.erp.service.ma.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.ma.ObjectExplainService;

@Service
public class ObjectExplainServiceImpl implements ObjectExplainService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public Map<String,Object> getData(String condition){
		Map<String,Object> map = new HashMap<String,Object>();
		SqlRowSet rs = baseDao.getJdbcTemplate().queryForRowSet("select name_,title_,desc_,interface_,standard_,man_,to_char(date_,'yyyy-mm-dd HH24:mi:ss') date_,attach_ from object$explain where " + condition);
		while(rs.next()){
			map.put("name_", rs.getString("name_"));
			map.put("title_", rs.getString("title_"));
			map.put("desc_",rs.getString("desc_"));
			map.put("interface_", rs.getString("interface_"));
			map.put("man_",rs.getString("man_"));
			map.put("date_", rs.getString("date_"));
			map.put("attach_", rs.getString("attach_"));
			map.put("standard_",rs.getInt("standard_"));
		}
		return map;
	}
	
	@Override
	public void saveObjectExplain(String formStore){
		Employee employee = SystemSession.getUser();
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		boolean bol = baseDao.checkIf("object$explain", "name_='"+store.get("name_")+"'");
		if(bol){ //更新
			if(store.get("man_")==null||"".equals(store.get("man_"))){
				store.put("man_", employee.getEm_name());
			}
			store.put("date_", DateUtil.currentDateString(Constant.YMD_HMS));
			String sql = SqlUtil.getUpdateSqlByFormStore(store, "object$explain", "name_");
			baseDao.execute(sql);
			baseDao.logger.update("ObjectExplain", "name_", store.get("name_"));		
		}else{ //保存
			store.put("man_", employee.getEm_name());
			store.put("date_", DateUtil.currentDateString(Constant.YMD_HMS));
			String sql = SqlUtil.getInsertSqlByMap(store, "object$explain");
			baseDao.execute(sql);
			baseDao.logger.save("ObjectExplain", "name_", store.get("name_"));
		}

	}
}
