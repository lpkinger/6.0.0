package com.uas.erp.dao.common.impl;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.CategoryStrDao;
import com.uas.erp.model.Category;
import com.uas.erp.model.Employee;
import com.uas.erp.model.HROrg;

@Repository
public class CategoryStrDaoImpl extends BaseDao implements CategoryStrDao {

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
	public List<Category> getAllCategorys(String condition) {
		try {
			StringBuffer sb = new StringBuffer("select * from Category where ca_statuscode='AUDITED'");
			if (condition != null && condition.length() > 0) {
				sb.append(" AND ").append(condition);
			}
			sb.append(" order by ca_id");
			List<Category> category = getJdbcTemplate().query(sb.toString(), new BeanPropertyRowMapper<Category>(Category.class));
			return category;
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
		Employee employee = getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<Employee>(Employee.class), em_id);
		sql = "select * from HrOrg where or_id = ?";
		return getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<HROrg>(HROrg.class), employee.getEm_defaultorid());
	}

	@Override
	public String getToUi(String key, String caller) {
		return null;
	}

	@Override
	public List<Category> getCategoryBank(int parentid) {
		try {
			List<Category> sns = getJdbcTemplate()
					.query("select t1.*,t2.cm_crrate ca_currencyrate from category t1 left join currencysmonth t2 on ca_currency=cm_crname and cm_yearmonth=to_char(sysdate,'yyyymm') left join currencys on cr_name=ca_currency where ca_subof=? and (ABS(nvl(ca_iscash,0)) = 1 or ABS(NVL(ca_isbank,0))=1) and ca_statuscode='AUDITED' order by ca_code ",
							new BeanPropertyRowMapper<Category>(Category.class), parentid);
			return sns;
		} catch (EmptyResultDataAccessException exception) {
			return null;
		}
	}
}
