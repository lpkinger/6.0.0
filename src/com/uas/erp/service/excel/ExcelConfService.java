package com.uas.erp.service.excel;

import java.util.List;

import com.uas.erp.model.excel.ExcelConf;

public interface ExcelConfService {
	List<ExcelConf> find(Integer documentId);
}
