package com.uas.erp.service.excel.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.excel.ExcelFile;
import com.uas.erp.model.excel.ExcelSheet;
import com.uas.erp.service.excel.ExcelFileService;
import com.uas.erp.service.excel.ExcelSheetService;

import sun.nio.cs.MS1250;

@Service
public class ExcelFileServiceImpl implements ExcelFileService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private ExcelSheetService excelSheetService;

	@Override
	public ExcelFile getById(Integer fileId) {
		return baseDao.queryBean("select * from ExcelFile where fileid="+fileId, 
				ExcelFile.class);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void changeFileName(String id, String name, String description) {
		baseDao.updateByCondition("ExcelFile", 
				"filename='"+name+"' , filedesc='"+description+"' ,fileupdatetime=sysdate", 
				"fileid="+id);
	}

	@Override
	public Map<String, Object> getExcelInfo(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ExcelFile file  = getById(id);
		modelMap.put("success", true);
		modelMap.put("name", file.getFilename());
		modelMap.put("description", file.getFiledesc());
		modelMap.put("exname", "xls");
		return modelMap;
	}

	@Override
	public List<Map<String, Object>> getExcelsByTplsource(int filetplsource,int start, int end, String condition) {
		String sql="select fileid,filename,filedesc,filecolor,to_char(filecreatetime,'yyyy-mm-dd')  filecreatetime,to_char(fileupdatetime,'yyyy-mm-dd hh:mm:ss')  fileupdatetime,fileman,filestatus,filestatuscode,fileversion,fileuse,filetplsource,fileversionsource from ExcelFile where filetplsource="+filetplsource+" order by filecreatetime desc";
		if (StringUtil.hasText(condition)) {
			sql="select fileid,filename,filedesc,filecolor,to_char(filecreatetime,'yyyy-mm-dd')  filecreatetime,to_char(fileupdatetime,'yyyy-mm-dd hh:mm:ss')  fileupdatetime,fileman,filestatus,filestatuscode,fileversion,fileuse,filetplsource,fileversionsource from ExcelFile where filetplsource="+filetplsource+" AND "+condition+" order by filecreatetime desc";
		}
		String pagingsql = "SELECT * "+
				"FROM(SELECT ROWNUM rn,t.* "+
				"     FROM("+sql+") t"+
				"     WHERE ROWNUM<="+end+")"+
				"WHERE rn>"+start;
		return baseDao.queryForList(pagingsql);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void delete(int id) {
		baseDao.deleteById("ExcelFile", "fileid", id);
		List<ExcelSheet> excelSheets = excelSheetService.getExcelSheetsByFileId(id);
		for (ExcelSheet excelSheet : excelSheets) {
			//删sheet
			baseDao.deleteById("ExcelSheet", "sheetid", excelSheet.getsheetid());
			//删cell
			baseDao.deleteByCondition(excelSheet.getsheetcelltable(), "cellsheetid="+excelSheet.getsheetid());
		}
	}

	@Override
	public int getExcelCountByTplsource(int filetplsource, String condition) {
		String sql = "select count(*) from ExcelFile where filetplsource="+filetplsource;
		if (StringUtil.hasText(condition)) {
			sql = sql + " AND "+condition;
		}
		return baseDao.getCount(sql);
	}

}
