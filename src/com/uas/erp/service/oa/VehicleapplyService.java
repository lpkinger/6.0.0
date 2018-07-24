package com.uas.erp.service.oa;

import net.sf.json.JSONObject;

public interface VehicleapplyService {
	
	void saveVehicleapply(String formStore, String  caller);
	
	void updateVehicleapplyById(String formStore, String  caller);
	
	void deleteVehicleapply(int va_id, String  caller);
	
	void auditVehicleapply(int va_id, String  caller);
	
	void resAuditVehicleapply(int va_id, String  caller);
	
	void submitVehicleapply(int va_id, String  caller);
	
	void resSubmitVehicleapply(int va_id, String  caller);
	
	void turnVehicle(JSONObject formStore ,String caller);

	void backUpdateVehicle(String formStore, String  caller);

	String turnReturnVehicle(String caller, String data);

	String[] printVehicleapply(int va_id, String caller, String reportName,
			String condition);
	
	String[] printVehiclereturn(int vr_id, String caller, String reportName,
			String condition);

	void refreshSendTime(String caller, String formStore);
	
}
