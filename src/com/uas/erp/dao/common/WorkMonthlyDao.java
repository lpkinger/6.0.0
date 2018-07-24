package com.uas.erp.dao.common;

import com.uas.erp.model.WorkMonthly;

public interface WorkMonthlyDao {
	WorkMonthly searchWorkMonthly(int id, String date);
}
