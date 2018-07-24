package com.uas.erp.dao.common.impl;

import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.InstructionDao;
import com.uas.erp.model.Instruction;

@Service("instructionDao")
public class InstructionDaoImpl extends BaseDao implements InstructionDao {

	@Override
	public Instruction getInstructionById(int id) {
		String sql = "select * from Instruction where in_id=?";
		return getJdbcTemplate().queryForObject(sql,
				new BeanPropertyRowMapper<Instruction>(Instruction.class),
				new Object[] { id });
	}
	@Override
	public void delete(int in_id) {
		try {
			getJdbcTemplate().execute(
					"delete from Instruction where in_id = " + in_id);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	@Override
	public List<Instruction> getByCondition(String condition, int page, int pageSize) {
		try {
			return getJdbcTemplate().query("select * from Instruction where " + condition, 
					new BeanPropertyRowMapper<Instruction>(Instruction.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int getSearchCount(String condition) {
		return getCountByCondition("Instruction", condition);
	}
	@Override
	public List<Instruction> getList(int page, int pageSize) {
		try {
			return getJdbcTemplate("Instruction").
					query("select * from Instruction ", 
							new BeanPropertyRowMapper<Instruction>(Instruction.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public int getListCount() {
		return getCountByTable("Instruction");
	}

}
