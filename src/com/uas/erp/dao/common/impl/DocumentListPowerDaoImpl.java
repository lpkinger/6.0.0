package com.uas.erp.dao.common.impl;

import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.DocumentListPowerDao;
import com.uas.erp.model.DocumentListPower;

@Repository("documentListPowerDao")
public class DocumentListPowerDaoImpl extends BaseDao implements DocumentListPowerDao {

	@Override
	public DocumentListPower getDLPByJoId_DclId(int jo_id, int dcl_id) {
		String sql = "select * from DocumentListPower where dlp_joid=? and dlp_dclid=?";
		try {
			return getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<DocumentListPower>(DocumentListPower.class), 
					new Object[] { jo_id, dcl_id });
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public List<DocumentListPower> getDocumentListPowersByDCLID(int dcl_id) {
		try {
			return getJdbcTemplate().query("select * from documentlistpower where dlp_dclid=?", 
					new BeanPropertyRowMapper<DocumentListPower>(DocumentListPower.class), dcl_id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

}
