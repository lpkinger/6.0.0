package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;

public interface SaleForecastDao {
	void checkSFyqty(List<Map<Object, Object>> datas);
	void udpatestatus(int sdid);
}
