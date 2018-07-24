package com.uas.erp.service.plm;

import java.util.List;
import java.util.Map;

public interface ProjectWeeklyReportService {
	
	List<Map<String, Object>> autoGetGridData(String man,String prjcode);
	
	void savePrjWkReport(String formStore, String gridStore, String caller);
	
	void updatePrjWkReport(String formStore, String gridStore, String caller);

	void deletePrjWkReport(int wr_id, String caller);

	void auditPrjWkReport(int wr_id, String caller);

	void resAuditPrjWkReport(int wr_id, String caller);

	void submitPrjWkReport(int wr_id, String caller);

	void resSubmitPrjWkReport(int wr_id, String caller);
	

}
