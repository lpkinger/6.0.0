package com.uas.erp.dao.common.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.ContractTypeDao;
import com.uas.erp.model.ContractType;

@Repository
public class ContractTypeDaoImpl extends BaseDao implements ContractTypeDao{

	@Override
	public List<ContractType> getContractTypeByParentId(int parentid) {
		try{
			@SuppressWarnings({ "unchecked", "rawtypes" })
			List<ContractType> sns = getJdbcTemplate().query(
					"select * from ContractType where ct_subof=? order by ct_id ", new BeanPropertyRowMapper(ContractType.class), parentid);
			return sns;
		} catch(EmptyResultDataAccessException exception){
			return null;
		}
	}
	@Override
	public Set<ContractType> getContractTypeBySearch(String search) {
		try {
			StringBuffer sb = new StringBuffer();
			String[] names = null;
			String where = "";
			if (search.contains("&&")) {
				names = search.split("&&");
				for (String name : names) {
					sb.append(" ct_name LIKE '%" + name + "%' AND");
				}
				where +=  sb.substring(0, sb.lastIndexOf("AND")) ;
			} else if (search.contains("##")) {
				names = search.split("##");
				for (String name : names) {
					sb.append(" ct_name LIKE '%" + name + "%' OR ");
				}
				where +=  sb.substring(0, sb.lastIndexOf("OR")) ;
			} else {
				where += " ct_name LIKE '%" + search + "%'";
			}
			List<ContractType> sns = getJdbcTemplate().query("SELECT * FROM ContractType WHERE " + where +" order by ct_id",
					new BeanPropertyRowMapper<ContractType>(ContractType.class));
			Set<ContractType> set = new HashSet<ContractType>();
			for (ContractType sn : sns) {
				set.addAll(getContractType(sn, null));
			}
			return set;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	private Set<ContractType> getContractType(ContractType ContractType, Set<ContractType> set) {
		if (set == null) {
			set = new HashSet<ContractType>();
		}
		if (ContractType.getCt_subof() == 0) {
			set.add(ContractType);
			return set;
		}
		try {
			set.add(ContractType);
			ContractType sn = getJdbcTemplate().queryForObject("select * from ContractType where ct_id=?",
					new BeanPropertyRowMapper<ContractType>(ContractType.class), ContractType.getCt_subof());
			return getContractType(sn, set);
		} catch (EmptyResultDataAccessException e) {
			// 非父节点， 但是却找不到父节点
			return new HashSet<ContractType>();
		}
	}
}
