package com.uas.erp.dao.common.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.DocumentPositionPowerDao;
import com.uas.erp.model.DocumentPositionPower;

@Repository("documentPositionPowerDao")
public class DocumentPositionPowerDaoImpl extends BaseDao implements DocumentPositionPowerDao {

	@Override
	public DocumentPositionPower getDocumentPositionPowerByJoId_DCPId(
			int jo_id, int dcp_id) {
		String sql = "select * from documentpositionpower where dpp_joid=? and dpp_dcpid=?";
		try {
			return getJdbcTemplate().queryForObject(sql,
					new BeanPropertyRowMapper<DocumentPositionPower>(DocumentPositionPower.class),
					new Object[] {jo_id, dcp_id });
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
