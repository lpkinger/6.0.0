package com.uas.erp.dao.common.impl;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.TransferDao;
import com.uas.erp.model.Transfer;

@Repository
public class TransferDaoImpl extends BaseDao implements TransferDao {

	@Override
	@Cacheable(value = "transfers", key = "#sob + '@' + #caller + '@' + #mode")
	public Transfer getTransfer(String sob, String caller, String mode) {
		try {
			Transfer transfer = getJdbcTemplate().queryForObject("select *  from Transfers where tr_caller=? and tr_mode=?",
					new BeanPropertyRowMapper<Transfer>(Transfer.class), caller, mode);
			List<Transfer.Detail> details = getJdbcTemplate().query("select * from TransferDetail where td_trid=?",
					new BeanPropertyRowMapper<Transfer.Detail>(Transfer.Detail.class), transfer.getTr_id());
			transfer.setDetails(details);
			return transfer;
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
