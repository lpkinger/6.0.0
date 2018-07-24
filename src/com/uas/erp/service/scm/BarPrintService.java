package com.uas.erp.service.scm;

public interface BarPrintService {
	void saveBarPrint(String formStore, String gridStore, String caller);
	void updateBarPrint(String formStore, String gridStore, String caller);
	String Subpackage(int bp_id, double qty, String caller);
	String ClearSubpackage(int bp_id, String caller);
	String[] printBar(int bp_id, String caller, String reportName,String condition );
}
