package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.BarCodeService;

@Service
public class BarCodeServiceImpl implements BarCodeService {

	@Autowired
	private BaseDao baseDao;

	@Override
	public List<Map<String, Object>> getBar(String codes) {
		SqlRowList rs = baseDao
				.queryForRowSet("select bar_code, bar_pucode, bar_vendcode, bar_indate, bar_validdate, bar_madedate from BarCode where bar_code in("
						+ codes + ")");
		if (rs.hasNext()) {
			return rs.getResultList();
		}
		return null;
	}

}
