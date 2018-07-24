package com.uas.erp.dao.common.impl;

import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.DocumentPowerDao;
import com.uas.erp.model.DocumentPositionPower;
import com.uas.erp.model.DocumentPower;

@Repository("documentPowerDao")
public class DocumentPowerDaoImpl extends BaseDao implements DocumentPowerDao {

	@Override
	public DocumentPower queryDocumentPowerById(int id) {
		String sql = "select * from documentpower where dcp_id=?";
		try {
			return getJdbcTemplate().queryForObject(sql,
					new BeanPropertyRowMapper<DocumentPower>(DocumentPower.class),
					new Object[] { id });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<DocumentPositionPower> getDocumentPositionPowersByDCPID(
			int dcp_id) {
		try{
			return getJdbcTemplate().query("select * from documentpositionpower where dpp_dcpid=?", 
					new BeanPropertyRowMapper<DocumentPositionPower>(DocumentPositionPower.class), dcp_id);
		} catch (EmptyResultDataAccessException e){
			return null;
		} catch (Exception e){
			return null;
		}
	}

	@Override
	public DocumentPositionPower getDPPByDcpIdAndJoId(int dcp_id, int jo_id) {
		try{
			return getJdbcTemplate().queryForObject("select * from documentpositionpower where dpp_dcpid=? and dpp_joid=?", 
					new BeanPropertyRowMapper<DocumentPositionPower>(DocumentPositionPower.class), dcp_id, jo_id);
		} catch (EmptyResultDataAccessException e){
			return null;
		} catch (Exception e){
			return null;
		}
	}

}
