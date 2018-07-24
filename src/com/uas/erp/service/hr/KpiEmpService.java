package com.uas.erp.service.hr;

public interface KpiEmpService {
	void saveKpiEmp(String formStore, String gridStore,String caller);
	String show(String gridStore, String condition);
}
