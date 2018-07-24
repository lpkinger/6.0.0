package com.uas.erp.dao.common.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.FormDao;
import com.uas.erp.model.Form;
import com.uas.erp.model.FormDetail;
import com.uas.erp.model.RelativeSearch;
import com.uas.erp.model.RelativeSearchLimit;

@Repository("formDao")
public class FormDaoImpl extends BaseDao implements FormDao {
	@Cacheable(value = "formpanel", key = "#sob + '@' + #caller",unless="#result==null")
	public Form getForm(final String caller, final String sob) {
		try {
			Form form = getJdbcTemplate().queryForObject("select *  from form where fo_caller=?",
					new BeanPropertyRowMapper<Form>(Form.class), caller);
			List<FormDetail> formDetails = getJdbcTemplate(form.getFo_table()).query(
					"select * from formdetail where fd_foid=? order by fd_detno", new BeanPropertyRowMapper<FormDetail>(FormDetail.class),
					form.getFo_id());
			form.setFormDetails(formDetails);// 取formpanel的items信息
			return form;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	@Cacheable(value = "relativesearch", key = "#sob + '@' + #caller + 'getRelativeSearchs'")
	public List<RelativeSearch> getRelativeSearchs(String caller, String sob) {
		try {
			List<RelativeSearch> searchs = getJdbcTemplate().query("select *  from RelativeSearch where rs_caller=? order by rs_detno",
					new BeanPropertyRowMapper<RelativeSearch>(RelativeSearch.class), caller);
			for (RelativeSearch s : searchs) {
				try {
					List<RelativeSearch.Form> forms = getJdbcTemplate().query(
							"select *  from RelativeSearchForm where rsf_rsid=? order by rsf_detno",
							new BeanPropertyRowMapper<RelativeSearch.Form>(RelativeSearch.Form.class), s.getRs_id());
					s.setForms(forms);
				} catch (EmptyResultDataAccessException e) {

				} catch (Exception e) {

				}
				try {
					List<RelativeSearch.Grid> grids = getJdbcTemplate().query(
							"select *  from RelativeSearchGrid where rsg_rsid=? order by rsg_detno",
							new BeanPropertyRowMapper<RelativeSearch.Grid>(RelativeSearch.Grid.class), s.getRs_id());
					s.setGrids(grids);
				} catch (EmptyResultDataAccessException e) {

				} catch (Exception e) {

				}
			}
			return searchs;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	@Cacheable(value = "relativesearch", key = "#sob + '@' + #id + 'getRelativeSearch'")
	public RelativeSearch getRelativeSearch(int id, String sob) {
		RelativeSearch search = null;
		try {
			search = getJdbcTemplate().queryForObject("select *  from RelativeSearch where rs_id=?",
					new BeanPropertyRowMapper<RelativeSearch>(RelativeSearch.class), id);
			try {
				List<RelativeSearch.Form> forms = getJdbcTemplate().query(
						"select *  from RelativeSearchForm where rsf_rsid=? order by rsf_detno",
						new BeanPropertyRowMapper<RelativeSearch.Form>(RelativeSearch.Form.class), id);
				search.setForms(forms);
			} catch (EmptyResultDataAccessException e) {

			} catch (Exception e) {

			}
			try {
				List<RelativeSearch.Grid> grids = getJdbcTemplate().query(
						"select *  from RelativeSearchGrid where rsg_rsid=? order by rsg_detno",
						new BeanPropertyRowMapper<RelativeSearch.Grid>(RelativeSearch.Grid.class), id);
				search.setGrids(grids);
			} catch (EmptyResultDataAccessException e) {

			} catch (Exception e) {

			}
		} catch (EmptyResultDataAccessException e) {

		}
		return search;
	}

	@CacheEvict(value = "formpanel", key = "#sob + '@' + #caller")
	@Override
	public void cacheEvict(final String sob, final String caller) {
	}

	@Override
	@Cacheable(value = "relativesearch", key = "#sob + '@' + #caller +'@' + #em_id + 'RelativeSearchLimit'")
	public List<RelativeSearchLimit> getRelativeSearchLimitsByEmpl(String caller, Integer em_id, String sob) {
		try {
			return getJdbcTemplate().query("select * from RelativeSearchLimit where rsl_caller=? and rsl_emid=? order by rsl_title",
					new BeanPropertyRowMapper<RelativeSearchLimit>(RelativeSearchLimit.class), caller, em_id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	@Cacheable(value = "relativesearch", key = "#sob + '@' + #caller +'@' + #jo_id + 'RelativeSearchLimit'")
	public List<RelativeSearchLimit> getRelativeSearchLimitsByJob(String caller, Integer jo_id, String sob) {
		try {
			return getJdbcTemplate().query("select * from RelativeSearchLimit where rsl_caller=? and rsl_joid=? order by rsl_title",
					new BeanPropertyRowMapper<RelativeSearchLimit>(RelativeSearchLimit.class), caller, jo_id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
