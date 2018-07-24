package com.uas.erp.service.scm;

public interface BomPriceService {


	void resAuditBomPrice(String caller,int id);

	void auditBomPrice(String caller,int id);

	void resSubmitBomPrice(String caller,int id);

	void submitBomPrice(String caller,int id);

	void updateBomPrice(String caller,String formStore, String param);

	void deleteBomPrice(String caller,int id);

	void saveBomPrice(String formStore, String param, String caller);

	void evlBomCostPrice(String caller, int id);

	void b2cBomPrice(String caller, int id);

	String turnB2cInquiry(String caller, int id, String gridId);

}
