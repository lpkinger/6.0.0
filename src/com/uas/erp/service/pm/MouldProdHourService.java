package com.uas.erp.service.pm;

public interface MouldProdHourService {


	void resAuditMouldProdHour(int id, String caller);

	void auditMouldProdHour(int id, String caller);

	void resSubmitMouldProdHour(int id, String caller);

	void submitMouldProdHour(int id, String caller);

	void printMouldProdHour(int id, String caller);

	void updateMouldProdHourById(String formStore, String param, String caller);

	void deleteMouldProdHour(int id, String caller);

	void saveMouldProdHour(String formStore, String param, String caller);

}
