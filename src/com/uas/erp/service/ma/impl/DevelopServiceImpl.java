package com.uas.erp.service.ma.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.ExecModel;
import com.uas.erp.service.ma.DevelopService;

@Service
public class DevelopServiceImpl implements DevelopService {

	@Autowired
	private BaseDao baseDao;

	@Override
	public Map<String, Object> exec(String statement) {
		// TODO Auto-generated method stub
		Map<String,Object> map=new HashMap<String, Object>();
		if(StringUtil.hasText(statement)){
			ExecModel  exec=new ExecModel(statement);
		    exec.parse();
			if(exec.checkValid()){
				try {
					if(exec.isSELECT()){	
						List<Map<String,Object>> lists=baseDao.getJdbcTemplate().queryForList(exec.getLimitSql());
						map.put("list", lists);
					}else baseDao.execute(exec.getExecStatement());
					map.put("success", true);
					
				} catch (DataAccessException e) {
					// TODO: handle exception
				    map.put("error",e.getCause().getMessage());				    
				}
                  
			}else map.put("error","执行语句不规范！");
			String status=map.containsKey("error")?"failure":"success";
			exec.getLogSql(SystemSession.getUser().getEm_name(), status).execute();	    		    
		}else{
			map.put("error","执行语句不能为空！");
		}
		return map;
	}

}
