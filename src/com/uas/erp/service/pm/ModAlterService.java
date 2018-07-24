package com.uas.erp.service.pm;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.FileUpload;

public interface ModAlterService {
	void saveModAlter(String formStore, String gridStore, String caller);
	void updateModAlterById(String formStore, String gridStore, String caller);
	void deleteModAlter(int al_id, String caller);
	String[] printModAlter(int al_id,  String caller, String reportName,
			String condition);
	void auditModAlter(int al_id, String caller);
	void resAuditModAlter(int al_id, String caller);
	void submitModAlter(int al_id, String caller);
	void resSubmitModAlter(int al_id, String caller);
	int turnFeePlease(int al_id, String caller);
	String turnMouldSale(int al_id);
	List<Map<String, Object>> turnPriceMould(int al_id);
	void uploadDetailFile(String params,String caller,String code,String keyvalue,String keyField);
	void deleteDetailFile(Integer id,String caller,String code,String keyvalue,String keyField);
}
