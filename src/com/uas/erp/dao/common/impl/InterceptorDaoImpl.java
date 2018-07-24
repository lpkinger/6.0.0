package com.uas.erp.dao.common.impl;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.InterceptorDao;
import com.uas.erp.model.Configs;
import com.uas.erp.model.Interceptors;

@Repository
public class InterceptorDaoImpl extends BaseDao implements InterceptorDao {

	@Override
	@Cacheable(value = "interceptors", key = "#sob + '@' + #caller + '.' + #type + '.' + #turn + '.' + #enable")
	public List<Interceptors> getInterceptorsByCallerAndType(String sob, String caller, String type, short turn, short enable) {
		try {
			List<Interceptors> interceptors = getJdbcTemplate().query(
					"select * from Interceptors where caller=? and type=? and turn=? and enable=? order by detno",
					new BeanPropertyRowMapper<Interceptors>(Interceptors.class), caller, type, turn, enable);
			return interceptors;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<Interceptors> getInterceptorsByCaller(String caller,HttpSession session) {
		try {
			Object printtype = session.getAttribute("en_admin");
			List<Interceptors> interceptors;
			if(printtype!=null && printtype.equals("jasper")){
				interceptors = getJdbcTemplate().query("select * from Interceptors where caller=? and type<>'print' order by type,turn,detno",
						new BeanPropertyRowMapper<Interceptors>(Interceptors.class), caller);
			}else{
				interceptors = getJdbcTemplate().query("select * from Interceptors where caller=? order by type,turn,detno",
						new BeanPropertyRowMapper<Interceptors>(Interceptors.class), caller);
			}
			return interceptors;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
