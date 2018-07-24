package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.GridPanel;

public interface AlMonthQueryService {
	GridPanel getArQuery(String condition);

	List<Map<String, Object>> getArDetailById(String condition);

	void refreshArQuery(int yearmonth);

	void refreshQuery(String condition);

	List<Map<String, Object>> getArDayDetail(String condition);

}
