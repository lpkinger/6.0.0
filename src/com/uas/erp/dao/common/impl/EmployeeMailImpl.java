package com.uas.erp.dao.common.impl;

import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.EmployeeMailDao;
import com.uas.erp.model.EmployeeMail;

@Repository
public class EmployeeMailImpl extends BaseDao implements EmployeeMailDao{

	@Override
	public List<EmployeeMail> getEmployeeMailByParentId(int parentid) {
		try{
			List<EmployeeMail> sns = getJdbcTemplate().query(
					"select * from EmployeeMail where emm_parentid=? order by emm_id ", 
					new BeanPropertyRowMapper<EmployeeMail>(EmployeeMail.class), parentid);
			return sns;
		} catch(EmptyResultDataAccessException exception){
			return null;
		}
	}
	
}
