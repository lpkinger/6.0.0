package com.uas.erp.service.common;

import org.apache.poi.hssf.usermodel.HSSFSheet;
public interface BatchUpdateService {
   String importExcel(String caller,HSSFSheet sheet);
}
