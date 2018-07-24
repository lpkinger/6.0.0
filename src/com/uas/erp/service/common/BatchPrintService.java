package com.uas.erp.service.common;

import com.uas.erp.model.Employee;

public interface BatchPrintService {
	String[] printBatch(String idS, String language, Employee employee,String reportName,String condition,String title,String todate,String dateFW,String fromdate,String enddate);
}
