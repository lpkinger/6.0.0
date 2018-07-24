package com.uas.erp.dao.common.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.ProductKindDao;
import com.uas.erp.model.ProductKind;


@Repository
public class ProductKindDaoImpl extends BaseDao implements ProductKindDao{

	@Override
	public List<ProductKind> getProductKindByParentId(int parentid,String allKind) {
		try{
			String querySql="select * from productkind where pk_subof=? and pk_effective='有效' order by pk_id ";
			if(allKind!=null && "true".equals(allKind)){
				querySql="select * from productkind where pk_subof=?  order by pk_id";
			}
			List<ProductKind> sns = getJdbcTemplate().query(querySql,
					new BeanPropertyRowMapper<ProductKind>(ProductKind.class), parentid);
			return sns;
		} catch(EmptyResultDataAccessException exception){
			return null;
		}
	}

	@Override
	public Set<ProductKind> getProductKindBySearch(String search) {
		try {
			StringBuffer sb = new StringBuffer();
			String[] names = null;
			String where = "";
			if (search.contains("&&")) {
				names = search.split("&&");
				for (String name : names) {
					sb.append(" pk_name LIKE '%" + name + "%' AND");
				}
				where +=  sb.substring(0, sb.lastIndexOf("AND")) ;
			} else if (search.contains("##")) {
				names = search.split("##");
				for (String name : names) {
					sb.append(" pk_name LIKE '%" + name + "%' OR ");
				}
				where +=  sb.substring(0, sb.lastIndexOf("OR")) ;
			} else {
				where += " pk_name LIKE '%" + search + "%'";
			}
			List<ProductKind> sns = getJdbcTemplate().query("SELECT * FROM ProductKind WHERE " + where +" order by pk_id",
					new BeanPropertyRowMapper<ProductKind>(ProductKind.class));
			Set<ProductKind> set = new HashSet<ProductKind>();
			for (ProductKind sn : sns) {
				set.addAll(getProductKind(sn, null));
			}
			return set;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	private Set<ProductKind> getProductKind(ProductKind productkind, Set<ProductKind> set) {
		if (set == null) {
			set = new HashSet<ProductKind>();
		}
		if (productkind.getPk_subof() == 0) {
			set.add(productkind);
			return set;
		}
		try {
			set.add(productkind);
			ProductKind sn = getJdbcTemplate().queryForObject("select * from productkind where pk_id=?",
					new BeanPropertyRowMapper<ProductKind>(ProductKind.class), productkind.getPk_subof());
			return getProductKind(sn, set);
		} catch (EmptyResultDataAccessException e) {
			// 非父节点， 但是却找不到父节点
			return new HashSet<ProductKind>();
		}
	}
	
}
