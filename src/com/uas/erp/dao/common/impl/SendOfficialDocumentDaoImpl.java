package com.uas.erp.dao.common.impl;

import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.SendOfficialDocumentDao;
import com.uas.erp.model.SendOfficialDocument;

@Service("sendODDao")
public class SendOfficialDocumentDaoImpl extends BaseDao implements
		SendOfficialDocumentDao {

	@Override
	public void insertSOD(SendOfficialDocument sod) {
		String sql = "insert into sendofficialdocument(sod_id,sod_attach,sod_context,sod_title,sod_subject," +
				"sod_emergencydegree,sod_secretlevel,sod_recipient_id,sod_fw_organ,sod_drafter_id) " +
				"values(?,?,?,?,?,?,?,?,?,?)";
		execute(sql, new Object[]{sod.getSod_id(),sod.getSod_attach(),sod.getSod_context(),
				sod.getSod_title(),sod.getSod_subject(),sod.getSod_emergencydegree(),
				sod.getSod_secretlevel(),sod.getSod_recipient_id(),sod.getSod_fw_organ(),sod.getSod_drafter_id()});		
	}
	
	@Override
	public SendOfficialDocument getSODById(int id){
		String sql = "select * from sendofficialdocument where sod_id = ?";
		return getJdbcTemplate().queryForObject(sql, new BeanPropertyRowMapper<SendOfficialDocument>(SendOfficialDocument.class), 
				new Object[]{id});
	}
	@Override
	public void delete(int sod_id) {
		try {
			getJdbcTemplate().execute(
					"delete from SendOfficialDocument where sod_id = " + sod_id);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	@Override
	public List<SendOfficialDocument> getByCondition(String condition, int page, int pageSize) {
		try {
			return getJdbcTemplate().query("select * from SendOfficialDocument where " + condition, 
					new BeanPropertyRowMapper<SendOfficialDocument>(SendOfficialDocument.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int getSearchCount(String condition) {
		return getCountByCondition("SendOfficialDocument", condition);
	}
	@Override
	public List<SendOfficialDocument> getList(int page, int pageSize) {
		try {
			return getJdbcTemplate("SendOfficialDocument").
					query("select * from SendOfficialDocument ", 
							new BeanPropertyRowMapper<SendOfficialDocument>(SendOfficialDocument.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public int getListCount() {
		return getCountByTable("SendOfficialDocument");
	}

}
