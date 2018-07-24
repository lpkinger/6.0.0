package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;

public interface PurMouldDao {
	int turnYSReport(int id);

	int turnFeePlease(int id, double aldamount);

	void udpatestatus(int pdid);

	void checkPdYamount(List<Map<Object, Object>> datas);
}
