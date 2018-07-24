package com.uas.erp.service.scm;

import java.util.List;
import java.util.Map;

public interface AutoInquiryService {

	void saveAutoInquiry(String formStore, String caller);

	void deleteAutoInquiry(int id, String caller);

	void updateAutoInquiry(String formStore,String param, String caller,String sign);
	
	String updateInquiryProd(String data,String caller);
	
	String inquiryTurnPrice(String data,String caller);
	
	List<Map<String, Object>> getGridStore();
}
