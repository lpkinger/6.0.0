package com.uas.erp.dao.common.impl;

import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.VendorKindDao;
import com.uas.erp.model.VendorKind;

@Repository
public class VendorKindDaoImpl extends BaseDao implements VendorKindDao{

	@Override
	public List<VendorKind> getVendorKindByParentId(int parentid) {
		try{
			@SuppressWarnings({ "unchecked", "rawtypes" })
			List<VendorKind> sns = getJdbcTemplate().query(
					"select * from VendorKind where vk_subof=? order by vk_id ", new BeanPropertyRowMapper(VendorKind.class), parentid);
			return sns;
		} catch(EmptyResultDataAccessException exception){
			return null;
		}
	}
	
}
