package com.uas.mobile.dao.impl;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Form;
import com.uas.erp.model.FormDetail;
import com.uas.mobile.dao.PanelDao;
@Repository("panelDao")
public class PanelDaoImpl extends BaseDao implements PanelDao {
	@Cacheable(value = "formpanel", key = "#sob + '@' + #caller + 'getMobileForm'")
	public Form getMobileForm(String caller) {
		try {
			Form form = getJdbcTemplate().queryForObject("select *  from form where fo_caller=?",
					new BeanPropertyRowMapper<Form>(Form.class), caller);
			List<FormDetail> formDetails = getJdbcTemplate(form.getFo_table()).query(
					"select * from formdetail where fd_foid=?  and nvl(fd_mobileused,0)<>0 order by fd_detno",
					new BeanPropertyRowMapper<FormDetail>(FormDetail.class), form.getFo_id());
			form.setFormDetails(formDetails);
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
	@Cacheable(value="gridpanel",key="#caller + 'getPanelDetailsByCaller'")
	public List<DetailGrid> getPanelDetailsByCaller(String caller) {
			try{
				List<DetailGrid> list = getJdbcTemplate().query(
						"SELECT * FROM detailgrid WHERE dg_caller=?  and nvl(dg_mobileused,0)<>0 ORDER BY dg_sequence ", 
						new BeanPropertyRowMapper<DetailGrid>(DetailGrid.class), caller);
				return  list;
			} catch (EmptyResultDataAccessException e){
				return null;
			} catch (Exception e){
				e.printStackTrace();
				return null;
			}
		}
	}
