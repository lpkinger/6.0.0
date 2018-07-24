package com.uas.erp.dao.common.impl;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.FileUploadTempDao;
import com.uas.erp.model.FileUploadTemp;

@Repository("fileUploadTempDao")
public class FileUploadTempDaoImpl extends BaseDao implements FileUploadTempDao {

	@Override
	public List<FileUploadTemp> getFileUploadById(Integer id) {
		String sql = "select * from FILEUPLOADTEMP where fl_id=?";
		try {
			return getJdbcTemplate().query(sql, new BeanPropertyRowMapper<FileUploadTemp>(FileUploadTemp.class),id);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

}
