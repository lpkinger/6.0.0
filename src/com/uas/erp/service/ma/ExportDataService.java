package com.uas.erp.service.ma;

import com.uas.erp.model.ExportData;
import com.uas.erp.model.GridPanel;


public interface ExportDataService {
	boolean saveExportData(String formStore);

	boolean testExportData(String formStore);

	GridPanel getExportDetails(int id);

	ExportData downLoadAsExcel(int id);

	void delteExportData(int id);

	void updateExportData(String formStore);
}
