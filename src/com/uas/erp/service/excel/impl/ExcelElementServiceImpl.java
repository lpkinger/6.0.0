package com.uas.erp.service.excel.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.excel.ExcelElementObj;
import com.uas.erp.model.excel.ExcelElement;
import com.uas.erp.model.excel.ExcelSheet;
import com.uas.erp.service.excel.ExcelElementService;

@Service
public class ExcelElementServiceImpl implements ExcelElementService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Override
	public long getCountBySheetElement(Integer sheetId) {
		return baseDao.queryForObject(
				"select count(eleid) from ExcelElement where elesheetid = "+sheetId, Long.class);
	}
	
	//创建一个Excel sheet元素
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public ExcelElement create(Map<String, Object> jsonObj, ExcelSheet sheet) {
		String name = jsonObj.get("name").toString();
		String eType = jsonObj.get("ftype").toString();
		String json = jsonObj.get("json").toString();

		ExcelElement excelElementTemplate = new ExcelElement(name, eType, json);
		excelElementTemplate.setElesheetid(sheet.getsheetid());
		excelElementTemplate.setEleid(baseDao.getSeqId("ExcelElement_SEQ"));
		baseDao.save(excelElementTemplate,"ExcelElement");
		return excelElementTemplate;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void update(Map<String, Object> jsonObj, ExcelSheet sheetTab) {
		String name = jsonObj.get("name").toString();
		String eType = jsonObj.get("ftype").toString();
		String json = jsonObj.get("json").toString();
		baseDao.updateByCondition("ExcelElement", "elecontent='"+json+"'", 
	    "elesheetid = "+sheetTab.getsheetid()+" AND elename = '"+name+"'");
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void remove(Map<String, Object> jsonObj, ExcelSheet sheet) {
		String name = jsonObj.get("name").toString();
		baseDao.deleteByCondition("ExcelElement", 
				"elesheetid="+sheet.getsheetid()+" AND elename='"+name+"'");
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void createUpdate(Map<String, Object> jsonObj, ExcelSheet sheet) {
		String name = jsonObj.get("name").toString();
		String eType = jsonObj.get("ftype").toString();
		String json = jsonObj.get("json").toString();
		Integer tabId = sheet.getsheetid();
		List<ExcelElement> elements =  getElementsByName(tabId, eType, name);
		if(0 < elements.size()){
			baseDao.updateByCondition("ExcelElement", "elecontent='"+json+"'", 
				    "elesheetid = "+sheet.getsheetid()+" AND elename = '"+name+"'");
		}else{
			ExcelElement excelElementTemplate = new ExcelElement(name, eType, json);
			excelElementTemplate.setElesheetid(sheet.getsheetid());
			excelElementTemplate.setEleid(baseDao.getSeqId("ExcelElement_SEQ"));
			baseDao.save(excelElementTemplate,"ExcelElement");
		}
	}

	private List<ExcelElement> getElementsByName(Integer tabId, String eType, String name) {
		return baseDao.query("select * from ExcelElement where elesheetid="+tabId+" AND eletype='"+eType+"' AND elename='"+name+"'", 
				ExcelElement.class);
	}
	
	
	//载入一个元素
	@Override
	public Map<String, Object> loadElementOnDemand(String fileId, String sheetId, Integer startElementId,
			Integer size) {
		Integer tabId = Integer.parseInt(sheetId);
		List<ExcelElementObj> tabElementObjs = loadSheetElementOnDemand(tabId, startElementId, size);
		Integer nextStartElementId = 0;
		if (tabElementObjs.size() == size)
			nextStartElementId = tabElementObjs.get(tabElementObjs.size() - 1).getId();
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("total", tabElementObjs.size());
		modelMap.put("results", tabElementObjs);
		modelMap.put("startElementId", nextStartElementId);
		return modelMap;
	}
	//载入一个元素
	public List<ExcelElementObj> loadSheetElementOnDemand(Integer tabId, Integer startElementId, Integer size) {
		List<ExcelElement> elements = loadElementsOnDemand(startElementId, size, tabId);
		List<ExcelElementObj> elementObjs = new ArrayList<>(elements.size());
		for(ExcelElement element:elements){
			elementObjs.add(new ExcelElementObj(element));
		}
		return elementObjs;
	}

	private List<ExcelElement> loadElementsOnDemand(Integer startElementId, Integer size, Integer tabId) {
		String sql = " select t.* from (select * from ExcelElement where eleid > "+startElementId+" and elesheetid = "+tabId+" order by eleid ASC) t where rownum<"+size;
		return baseDao.query(sql, ExcelElement.class);
	}

	@Override
	public List<ExcelElement> findElementsBySheet(int sheetId) {
		return baseDao.query("select * from ExcelElement where elesheetid="+sheetId, ExcelElement.class);
	}

	@Override
	public void batchInsert(List<ExcelElement> copiedElements) {
		for (ExcelElement excelElement : copiedElements) {
			baseDao.save(excelElement,"EXCELELEMENT");
		}
	}
}
