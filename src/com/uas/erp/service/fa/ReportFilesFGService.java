package com.uas.erp.service.fa;

public interface ReportFilesFGService {
	void saveReportFilesFG(String formStore,String gridStore,String caller);

	void updateReportFilesFG(String formStore,String gridStore,String caller);

	void deleteReportFilesFG(int fo_id,String caller);


}
