package com.uas.erp.service.ma.impl;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.InterceptorDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Interceptors;
import com.uas.erp.service.ma.InterceptorService;

@Service
public class InterceptorServiceImpl implements InterceptorService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private InterceptorDao interceptorDao;

	@Override
	public List<Interceptors> getInterceptorsByCaller(String caller,HttpSession session) {
		return interceptorDao.getInterceptorsByCaller(caller,session);
	}

	@Override
	@CacheEvict(value = "interceptors", allEntries = true)
	public void saveInterceptors(List<Map<Object, Object>> updated) {
		baseDao.execute(SqlUtil.getUpdateSqlbyGridStore(updated, "Interceptors", "id"));
		log(updated);
	}
	
	private void log(List<Map<Object,Object>> updated){
		String sql=null;
		Employee user=SystemSession.getUser();
		StringBuffer ids=new StringBuffer();
		for(Map<Object,Object> update:updated){
			ids.append(update.get("id")+",");
		}
		if(ids.length()>0){
			sql= "insert into messagelog(ml_id,ml_date,ml_man,ml_content,ml_result,ml_search) "
				+ " select messagelog_seq.nextval,sysdate,'"+user.getEm_name()+"("+user.getEm_code()+")',type,act,caller||'|Config Caller' from ( "  
				+ " select ' 更新为:' " 
				+ " ||(case enable " 
				+ " when 1 then '开启' else '关闭' end)||'  '||title act,caller,type  from (" 
				+ " SELECT Interceptors.*,rownum rn " 
				+ " FROM Interceptors  where id in ("+ids.substring(0, ids.length()-1)+")" 
				+ " ))";
			baseDao.execute(sql);
		}
	}

	@Override
	public List<Interceptors> getInterceptorsByCallerAndType(String caller, String type, String turn) {
		return interceptorDao.getInterceptorsByCallerAndType(SpObserver.getSp(), caller, type, "before".equals(turn) ? Interceptors.BEFORE
				: Interceptors.AFTER, Constant.YES);
	}

	@Override
	public List<Interceptors> getInterceptorsByCondition(String condition) {
		try {
			List<Interceptors> interceptors = baseDao.getJdbcTemplate().query("select * from Interceptors where "+condition+" order by type,turn,detno",
					new BeanPropertyRowMapper<Interceptors>(Interceptors.class));
			return interceptors;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
