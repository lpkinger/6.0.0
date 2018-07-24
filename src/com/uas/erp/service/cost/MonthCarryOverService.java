package com.uas.erp.service.cost;

public interface MonthCarryOverService {
	void carryover(String caller, Integer param);
	void rescarryover(String caller, Integer param);
	int getCurrentYearmonth();
}
