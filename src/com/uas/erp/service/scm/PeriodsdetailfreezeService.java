package com.uas.erp.service.scm;

public interface PeriodsdetailfreezeService {
	void Periodsdetailfreeze(String pd_detno, String caller);
	void Periodsdetailcancelfreeze(String caller);
	String getFreezeDetno();
}
