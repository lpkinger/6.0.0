package com.uas.erp.dao.common;

import com.uas.erp.model.WorkDaily;

public interface WorkDailyDao {
	
	WorkDaily searchWorkDaily(int id, String date);

}
