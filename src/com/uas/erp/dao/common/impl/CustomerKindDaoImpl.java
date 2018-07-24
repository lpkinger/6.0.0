package com.uas.erp.dao.common.impl;

import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.CustomerKindDao;
import com.uas.erp.model.CustomerKind;

@Repository
public class CustomerKindDaoImpl extends BaseDao implements CustomerKindDao{

	@Override
	public List<CustomerKind> getCustomerKindByParentId(int parentid) {
		try{
			@SuppressWarnings({ "unchecked", "rawtypes" })
			List<CustomerKind> sns = getJdbcTemplate().query(
					"select * from CustomerKind where ck_subof=? order by ck_id ", new BeanPropertyRowMapper(CustomerKind.class), parentid);
			return sns;
		} catch(EmptyResultDataAccessException exception){
			return null;
		}
	}
	
}
