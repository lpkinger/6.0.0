package com.uas.erp.dao.common;

import java.util.Map;

public interface MakeNoticeDetailDao {
	Map<String, Object> turnMake(int id, double tqty);
	Map<String, Object> turnOutSource(int id, double tqty);
}
