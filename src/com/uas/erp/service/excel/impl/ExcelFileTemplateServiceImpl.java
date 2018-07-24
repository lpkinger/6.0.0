package com.uas.erp.service.excel.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.impl.jam.mutable.MPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Maps;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.excel.ExcelDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.excel.ExcelCell;
import com.uas.erp.model.excel.ExcelFile;
import com.uas.erp.model.excel.ExcelFileTemplate;
import com.uas.erp.model.excel.ExcelSheetObj;
import com.uas.erp.model.excel.ExcelSheetSpan;
import com.uas.erp.model.excel.ExcelSheet;
import com.uas.erp.model.excel.Triple;
import com.uas.erp.service.excel.ExcelCellService;
import com.uas.erp.service.excel.ExcelConfService;
import com.uas.erp.service.excel.ExcelElementService;
import com.uas.erp.service.excel.ExcelFileTemplateService;
import com.uas.erp.service.excel.ExcelSheetService;
@Service
public class ExcelFileTemplateServiceImpl implements ExcelFileTemplateService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private ExcelDao excelDao;
	
	
	@Autowired
	private ExcelSheetService excelSheetService;
	@Autowired
	private ExcelCellService excelCellService;
	@Autowired
	private ExcelElementService excelElementService;
	@Autowired
	private ExcelConfService excelConfService;
	
	//创建一个模板
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public int createTemplate(String filename, String desc, int subof, Boolean isCategory,Employee employee) {
		int fileid_tpl = baseDao.getSeqId("EXCELFILE_COMMON_SEQ");
		//保存类别或文件
		ExcelFileTemplate fileTemplate = new ExcelFileTemplate();
		fileTemplate.setFileid_tpl(fileid_tpl);
		fileTemplate.setFilename_tpl(filename);
		fileTemplate.setFiledesc_tpl(desc);
		fileTemplate.setFilecreatetime_tpl(new Date());
		fileTemplate.setFilesubof_tpl(subof);
		fileTemplate.setFileman_tpl(employee.getEm_name());
		fileTemplate.setFilecategory_tpl(isCategory);
		baseDao.save(fileTemplate,"EXCELFILE_TEMPLATE");
		if (!isCategory) {
			//保存3个默认sheet			
			ExcelSheet sheetTemplate1 = 
			new ExcelSheet(baseDao.getSeqId("EXCELSHEET_SEQ"), fileid_tpl, "sheet1", 1, true, "EXCELCELL");
			baseDao.save(sheetTemplate1,"EXCELSHEET");
			ExcelSheet sheetTemplate2 = 
			new ExcelSheet(baseDao.getSeqId("EXCELSHEET_SEQ"), fileid_tpl, "sheet2", 2, false, "EXCELCELL");
			baseDao.save(sheetTemplate2,"EXCELSHEET");
			ExcelSheet sheetTemplate3 = 
			new ExcelSheet(baseDao.getSeqId("EXCELSHEET_SEQ"), fileid_tpl, "sheet3", 3, false, "EXCELCELL");
			baseDao.save(sheetTemplate3,"EXCELSHEET");
		}
		return fileid_tpl;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void changeFileName(String id, String name, String description) {
		baseDao.updateByCondition("ExcelFile_Template", 
				"filename_tpl='"+name+"' , filedesc_tpl='"+description+"' ,fileupdatetime_tpl=sysdate", 
				"fileid_tpl="+id);
	}

	public ExcelFileTemplate getById(Integer fileId) {
		return baseDao.queryBean("select * from ExcelFile_Template where fileid_tpl="+fileId, 
				ExcelFileTemplate.class);
	}

	@Override
	public List<JSONTree> getExcelTreeBySubof(int subof, String condition) {
		String sql = "select * from ExcelFile_Template where filesubof_tpl="+subof;
		List<ExcelFileTemplate> fileTemplates = baseDao.query(sql, ExcelFileTemplate.class);
		List<JSONTree> tree = new ArrayList<JSONTree>();
		for (ExcelFileTemplate fileTemplate : fileTemplates) {
			tree.add(new JSONTree(fileTemplate));
		}
		return tree;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void delete(int id, Boolean isCategory) {
/*		List<ExcelFileTemplate> list = baseDao.query(
		"select * from EXCELFILE_TEMPLATE  start with fileid_tpl = "+id+" connect by filesubof_tpl = prior fileid_tpl", ExcelFileTemplate.class);
		for (ExcelFileTemplate excelFileTemplate : list) {
			//判断是否为目录
			if (excelFileTemplate.getFilecategory_tpl()) {
				//判断目录下是否还有模板
				int count = baseDao.getCount("select count(*) from EXCELFILE_TEMPLATE where FILESUBOF_TPL = "+id);
				if (count==1) {
					BaseUtil.showError("该目录下存在模板文件，不可删除！");
				}
				
				baseDao.deleteById("ExcelFile_Template", "fileid_tpl", excelFileTemplate.getFileid_tpl());
			}else {
				//判断该模板文件是否有实例文件
				int count = baseDao.getCount("select count(*) from EXCELFILE where filetplsource = "+id);
				if (count==1) {
					BaseUtil.showError("该模板下存在实例文件，不可删除！");
				}
				
				//删文件
				baseDao.deleteById("ExcelFile_Template", "fileid_tpl", excelFileTemplate.getFileid_tpl());
				List<ExcelSheet> excelSheets = excelSheetService.getExcelSheetsByFileId(excelFileTemplate.getFileid_tpl());
				for (ExcelSheet excelSheet : excelSheets) {
					//删sheet
					baseDao.deleteById("ExcelSheet", "sheetid", excelSheet.getsheetid());
					//删cell
					baseDao.deleteByCondition(excelSheet.getsheetcelltable(), "cellsheetid="+excelSheet.getsheetid());
				}
			}
		}*/
		
		ExcelFileTemplate excelFileTemplate = getById(id);
		//判断是否为目录
		if (excelFileTemplate.getFilecategory_tpl()) {
			//判断目录下是否还有模板
			int count = baseDao.getCount("select count(*) from EXCELFILE_TEMPLATE where FILESUBOF_TPL = "+id);
			if (count==1) {
				BaseUtil.showError("该目录下存在模板文件，不可删除！");
			}
			
			baseDao.deleteById("ExcelFile_Template", "fileid_tpl", excelFileTemplate.getFileid_tpl());
		}else {
			//判断该模板文件是否有实例文件
			int count = baseDao.getCount("select count(*) from EXCELFILE where filetplsource = "+id);
			if (count==1) {
				BaseUtil.showError("该模板下存在实例文件，不可删除！");
			}
			
			//删文件
			baseDao.deleteById("ExcelFile_Template", "fileid_tpl", excelFileTemplate.getFileid_tpl());
			List<ExcelSheet> excelSheets = excelSheetService.getExcelSheetsByFileId(excelFileTemplate.getFileid_tpl());
			for (ExcelSheet excelSheet : excelSheets) {
				//删sheet
				baseDao.deleteById("ExcelSheet", "sheetid", excelSheet.getsheetid());
				//删cell
				baseDao.deleteByCondition(excelSheet.getsheetcelltable(), "cellsheetid="+excelSheet.getsheetid());
			}
		}
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public int newFromTpl(String filecaller,Employee employee) {
		//模板文件
		ExcelFileTemplate fileTemplate = getByCaller(filecaller);
		//实例文件
		int id = baseDao.getSeqId("EXCELFILE_COMMON_SEQ");
		ExcelFile file = new ExcelFile();
		file.setFileid(id);
		file.setFilename(fileTemplate.getFilename_tpl()+"的新增实例文件");
		file.setFilecreatetime(new Date());
		file.setFileupdatetime(new Date());
		file.setFileman(employee.getEm_name());
		file.setFilestatus("在录入");
		file.setFilestatuscode("ENTERING");
		file.setFiletplsource(fileTemplate.getFileid_tpl());
		//保存file
		baseDao.save(file,"ExcelFile");
		//保存sheet及其相关的cell,element
		excelSheetService.copyFile(fileTemplate,file);
		return id;
	}

	@Override
	public Map<String, Object> getExcelInfo(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ExcelFileTemplate fileTemplate  = getById(id);
		modelMap.put("success", true);
		modelMap.put("name", fileTemplate.getFilename_tpl());
		modelMap.put("description", fileTemplate.getFiledesc_tpl());
		modelMap.put("exname", "xls");
		return modelMap;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void update(String filename, String desc, int id,String caller,Employee employee) {
		baseDao.updateByCondition("EXCELFILE_TEMPLATE", 
		"filename_tpl='"+filename+"'"+" , filedesc_tpl='"+desc+"'"+",filecaller_tpl='"+caller+"'", 
		"fileid_tpl="+id);
	}

	@Override
	public ExcelFileTemplate getByCaller(String caller) {
		return baseDao.queryBean("select * from ExcelFile_Template where FILECALLER_TPL='"+caller+"'", 
				ExcelFileTemplate.class);
	}

}
