package com.uas.erp.service.scm;

import java.util.List;
import java.util.Map;

public interface TenderService {
	
	Map<String, Object> getTenderList(String page,String limit,String search,String date,String status);

	Map<String, Object> getTenderEstimate(String id);
	
	Map<String, Object> getTenderList(String page,String limit,String search,String date);
	
	Map<String, Object> getTenderCustList(String page,String limit,String search,String date,String status);
	
	Map<String, Object> getTender(String id);
	
	boolean isSubmit(String caller);
	
	Map<Object, Object> saveorPublicTender(String caller, String formStore,String gridStore, String gridStore1,int isPublish);
	
	void publicTender(Integer id, String caller);
	
	void deleteTender(int id, String caller);
	
	void deleteProd(int tenderProdId);
	
	void removeSaleTender(int id, Long vendUU, String caller);
	
	Map<String, Object> getTenderPublic(String id);
	
	Object addTenderItems(String id);
	
	Map<String, Object> getTenderSubmission(String id);
	
	void saveSaleTender(String caller, String formStore, String enBaseInfo,String gridStore,String attachs);

	void auditSaleTender(int id, String caller);

	void resAuditSaleTender(int id, String caller);

	void submitSaleTender(int id, String caller);

	void resSubmitSaleTender(int id, String caller);
	
	void saveEstimateTender(String caller, String formStore,String gridStore);
	
	void submitEstimateTender(int id, String caller);
	
	void resSubmitEstimateTender(int id, String caller);
	
	void auditEstimateTender(int id, String caller);
	
	String turnPurchase(String caller,String fromStore,String param,List<Long> vendUUs);

	void resSubmitTender(int id, String caller);

	Map<String, Object> getJProcessByForm(String finds);
	
}
