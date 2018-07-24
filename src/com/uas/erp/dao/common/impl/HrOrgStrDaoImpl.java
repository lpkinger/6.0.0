package com.uas.erp.dao.common.impl;

import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.HrOrgStrDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.HROrg;

@Repository
public class HrOrgStrDaoImpl extends BaseDao implements HrOrgStrDao {

	@Override
	public List<HROrg> getHrOrgbyParentId(int parentid) {

		try {
			List<HROrg> hrOrgs = getJdbcTemplate().query("select * from hrOrg where or_subof=? order by or_id",
					new BeanPropertyRowMapper<HROrg>(HROrg.class), parentid);
			return hrOrgs;

		} catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	@Override
	public List<HROrg> getAllHrOrgs(String condition) {
		try {
			StringBuffer sb = new StringBuffer("select * from hrOrg ");
			if (condition != null && condition.length() > 0) {
				sb.append(" WHERE ").append(condition);
			}
			sb.append(" order by or_id");
			List<HROrg> hrOrg = getJdbcTemplate().query(sb.toString(), new BeanPropertyRowMapper<HROrg>(HROrg.class));
			return hrOrg;

		} catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	@Override
	public HROrg getHrOrgByCode(String em_code) {
		final String sql = "select * from hrorg where or_headmancode = ?";
		HROrg or = null;
		try {
			or = getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<HROrg>(HROrg.class), em_code);
		} catch (DataAccessException e) {

			return null;
			/* e.printStackTrace(); */
		}
		return or;
	}

	@Override
	public HROrg getHrOrgByEmId(int em_id) {
		String sql = "select * from employee where em_id = ?";
		Employee employee = getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<Employee>(Employee.class),
				em_id);
		sql = "select * from HrOrg where or_id = ?";
		return getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<HROrg>(HROrg.class),
				employee.getEm_defaultorid());
	}

}
