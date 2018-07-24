package com.uas.erp.dao.common;

public interface StockTakingDao {
	Object[] turnProdIO(String piclass, String whcode, String stcode, String caller);
	void turnProdIODetail(int stdid, int detno, String code, Object id, String piclass);
}
