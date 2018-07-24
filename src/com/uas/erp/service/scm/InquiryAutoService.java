package com.uas.erp.service.scm;

import java.util.List;
import java.util.Map;

public interface InquiryAutoService {
	List<Map<String, Object>> getStepDet(int in_id);
	
	int turnPurcPrice(int in_id, String caller);
	
	void agreeAutoPrice(int id, String param);
	
	void submitInquiryAuto(int id,String caller);
	
	void resSubmitInquiryAuto(int id,String caller);
	
	void deleteAutoDet(int id,String caller);
	
	void deleteAuto(int id,String caller);
}
