package com.uas.erp.service.common.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.service.common.FilePathService;

@Service
public class FilePathServiceImpl implements FilePathService {
	@Autowired
	private BaseDao baseDao;

	@Override
	public int saveFilePath(String path, int size, String fileName, Employee employee) {
		int id = baseDao.getSeqId("EMAILFILEPATH");
		/**
		 * 文件名含单引号无法下载*/
		fileName=fileName.replaceAll(",", "，");
		baseDao.execute("INSERT INTO filepath(fp_id,fp_path,fp_size,fp_man,fp_date,fp_name) values(" + id + ",'" + path + "'," + size
				+ ",'" + employee.getEm_name() + "'," + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",'" + fileName
				+ "')");
		return id;
	}

	@Override
	public String getFilepath(int id) {
		String path = null;
		Object res = baseDao.getFieldDataByCondition("filepath", "fp_path", "fp_id=" + id);
		if (res != null) {
			path = (String) res;
		}
		return path;
	}

	@Override
	public List<String> getFilesPath(Object[] ids) {
		return baseDao.queryForList("select fp_path from filepath where fp_id in (" + CollectionUtil.toString(ids) + ")", String.class);
	}

	@Override
	public List<Map<String, Object>> getData() {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		SqlRowList rs = baseDao.queryForRowSet("select * from sys_ftp");
		while(rs.next()){
			Map<String,Object> map = new HashMap<String,Object>();
			int sf_id = rs.getInt("sf_id");
			String sf_name = rs.getString("sf_name");
			Object sf_defaultpath = rs.getObject("sf_defaultpath")==null?'无':rs.getString("sys_defaultpath");
			map.put("display", sf_name+"(默认路径:"+sf_defaultpath+")");
			map.put("value", sf_id);
			list.add(map);
		}
		return list;
	}
	
	@Override
	public List<Map<String, Object>> getDetailData(int id) {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		SqlRowList rs = baseDao.queryForRowSet("select * from sys_ftpdetail where sfd_sfid=?",id);
		while(rs.next()){
			Map<String,Object> map = new HashMap<String,Object>();
			Object sfd_path = rs.getObject("sfd_path");
			Object sfd_id = rs.getInt("sfd_id");
			map.put("display", sfd_path);
			map.put("value", sfd_id);
			list.add(map);
		}
		return list;
	}

}
