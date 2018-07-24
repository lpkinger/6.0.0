package com.uas.erp.service.ma;

public interface PeriodsService {
	void addPeriods(Integer param);
	boolean per_chk(String type, Integer month, String start, String end);
	int getCurrentYearmonth();
}
