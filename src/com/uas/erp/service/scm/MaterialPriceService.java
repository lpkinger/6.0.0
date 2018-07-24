package com.uas.erp.service.scm;

import org.apache.poi.ss.usermodel.Workbook;

public interface MaterialPriceService {
	void saveMaterialPrice(String formStore, String gridStore, String caller);

	void updateMaterialPriceById(String formStore, String gridStore, String caller);

	void deleteMaterialPrice(int pp_id, String caller);

	void printMaterialPrice(int pp_id, String caller);

	void auditMaterialPrice(int pp_id, String caller);

	void resAuditMaterialPrice(int pp_id, String caller);

	void submitMaterialPrice(int pp_id, String caller);

	void resSubmitMaterialPrice(int pp_id, String caller);

	boolean ImportExcel(int id, Workbook wbs, String substring, String caller);

}
