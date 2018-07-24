package com.uas.erp.service.scm;

public interface BarOrPackReportService {

	void updateReportFile(String formStore,String gridStore,String caller);

	void deleteReportFile(String callers,String caller);

}
