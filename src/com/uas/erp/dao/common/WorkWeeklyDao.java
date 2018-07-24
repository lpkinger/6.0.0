package com.uas.erp.dao.common;

import com.uas.erp.model.WorkWeekly;

public interface WorkWeeklyDao {
	WorkWeekly searchWorkWeekly(int id, String date);
}
