package com.uas.erp.service.scm;

import net.sf.json.JSONObject;

public interface AvgCostCountService {

	void countAvgCost(Integer param);

	JSONObject turnCostChange(Integer param);
}
