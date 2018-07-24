package com.uas.erp.service.oa.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.OfficialDocumentService;

@Service
public class OfficialDocumentServiceImpl implements OfficialDocumentService {

	@Autowired
	private BaseDao baseDao;
	@Override
	public void vastFile(String caller, String tablename, String[] field, int[] id) {
		String sql = "UPDATE " + tablename + " SET " + field[0] + "=1 ";
		for (int key : id) {
			baseDao.execute(sql + " WHERE " + field[1] + "=" +key);			
		}
	}

}
