package com.uas.erp.service.excel.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.excel.ExcelConf;
import com.uas.erp.service.excel.ExcelConfService;

@Service
public class ExcelConfServiceImpl implements ExcelConfService {

	@Autowired
	private BaseDao baseDao;
	
	//通过文件id找一个文件配置
	@Override
	public List<ExcelConf> find(Integer fileId) {
		return baseDao.query("select * from ExcelConf where confileid="+fileId, ExcelConf.class);
	}

}
