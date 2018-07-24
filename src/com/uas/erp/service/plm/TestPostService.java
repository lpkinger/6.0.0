package com.uas.erp.service.plm;

public interface TestPostService {
	String initPurchase(int count);

	String[] initProdIOPurc(int count, String data, String piclass, String caller);

	String[] initProdIOPurcOut(int count, String data, String piclass, String caller);

	String postProdIOPurc(String code);

	void clearProdIOPurc(String code, String codes);
}
