package com.uas.erp.dao.common;

import java.util.List;

import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.DataStoreDetail;
import com.uas.erp.model.ExcelFx;
import com.uas.erp.model.ExcelTemplate;
import com.uas.erp.model.ExcelTemplateDetail;

public interface ExcelDao {
  ExcelTemplate getExcelTemplateById(int id);
  List<ExcelTemplateDetail> getExcelTemplteDetails(ExcelTemplate template);
  List<ExcelTemplateDetail> getExcelTemplteDetails(String condition);
  List<DataStoreDetail> getDataStoreDetails(String condition);
  List<ExcelTemplateDetail> getExcelTemplteDetails(ExcelTemplate template,String sheetname);
  List<ExcelTemplateDetail> getExcelTemplteDetails(ExcelTemplate template,String sheetname,int i);
  List<ExcelFx> getExcelFxs(String condition);
  SqlRowList getSqlRowListByDetails(String tablename,String condition,List<ExcelTemplateDetail> details);
  Object getExcelFxData(ExcelTemplateDetail detail ,String cellCondition, String baseMonth);
  @SuppressWarnings("rawtypes")
  List getSheets(int id);
  ExcelFx getExcelFx(String condition);
}
