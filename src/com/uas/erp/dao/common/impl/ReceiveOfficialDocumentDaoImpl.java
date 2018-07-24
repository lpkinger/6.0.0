package com.uas.erp.dao.common.impl;

import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.ReceiveOfficialDocumentDao;
import com.uas.erp.model.ReceiveOfficialDocument;

@Service("receiveODDao")
public class ReceiveOfficialDocumentDaoImpl extends BaseDao implements
		ReceiveOfficialDocumentDao {

	@Override
	public ReceiveOfficialDocument findRODById(int id) {
		String sql = "select * from receiveofficialdocument where rod_id=?";
		return getJdbcTemplate().queryForObject(sql,
				new BeanPropertyRowMapper<ReceiveOfficialDocument>(ReceiveOfficialDocument.class),
				new Object[] { id });
	}
	@Override
	public void delete(int rod_id) {
		try {
			getJdbcTemplate().execute(
					"delete from ReceiveOfficialDocument where rod_id = " + rod_id);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	@Override
	public List<ReceiveOfficialDocument> getByCondition(String condition, int page, int pageSize) {
		try {
			return getJdbcTemplate().query("select * from ReceiveOfficialDocument where " + condition, 
					new BeanPropertyRowMapper<ReceiveOfficialDocument>(ReceiveOfficialDocument.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int getSearchCount(String condition) {
		return getCountByCondition("ReceiveOfficialDocument", condition);
	}
	@Override
	public List<ReceiveOfficialDocument> getList(int page, int pageSize) {
		try {
			return getJdbcTemplate("ReceiveOfficialDocument").
					query("select * from ReceiveOfficialDocument ", 
							new BeanPropertyRowMapper<ReceiveOfficialDocument>(ReceiveOfficialDocument.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public int getListCount() {
		return getCountByTable("ReceiveOfficialDocument");
	}

}
