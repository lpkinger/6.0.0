package com.uas.erp.dao.common.impl;

import java.util.List;
import net.sf.json.JSONObject;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.FormAttachDao;
import com.uas.erp.model.FormAttach;

@Repository
public class FormAttachDaoImpl extends BaseDao implements FormAttachDao{

	@Override
	public List<FormAttach> getFormAttachs(String caller, int keyvalue) {
		try{
			return getJdbcTemplate().query("SELECT * FROM formattach WHERE fa_caller=? AND fa_keyvalue=?", 
					new BeanPropertyRowMapper<FormAttach>(FormAttach.class), caller, keyvalue);
		} catch (EmptyResultDataAccessException e){
			return null;
		} catch (Exception e){
			return null;
		}
	}

	@Override
	public void saveAttach(FormAttach attach) {
		int id = getSeqId("FORMATTACH_SEQ");
		attach.setFa_id(id);
		save(attach);
	}

	@Override
	public JSONObject getFilePath(int fp_id) {
		SqlRowList rs = queryForRowSet("SELECT * FROM FilePath WHERE fp_id=" + fp_id);
		if(rs.next()){
			JSONObject o = new JSONObject();
			o.put("fp_id", fp_id);
			o.put("fp_path", rs.getString("fp_path"));
			o.put("fp_size", rs.getInt("fp_size"));
			o.put("fp_name", rs.getString("fp_name"));
			o.put("fp_date", rs.getGeneralTimestamp("fp_date"));
			o.put("fp_man", rs.getString("fp_man"));
			return o;
		}
		return null;
	}
	
}
