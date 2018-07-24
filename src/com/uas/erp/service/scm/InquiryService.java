package com.uas.erp.service.scm;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.uas.b2b.model.InquiryDetailDet;

public interface InquiryService {
	/**
	 * @param formStore
	 * @param gridStore
	 * @param dets
	 *            分段询价
	 * @param caller
	 */
	void saveInquiry(String formStore, String gridStore, String dets, String caller);

	/**
	 * @param formStore
	 * @param gridStore
	 * @param dets
	 *            分段询价
	 * @param caller
	 */
	void updateInquiryById(String formStore, String gridStore, String dets, String caller);

	/**
	 * 取分段报价信息
	 * 
	 * @return
	 */
	List<Map<String, Object>> getStepDet(int in_id);

	void deleteInquiry(int in_id, String caller);

	void printInquiry(int in_id, String caller);

	void auditInquiry(int in_id, String caller);

	void resAuditInquiry(int in_id, String caller);

	void submitInquiry(int in_id, String caller);

	void resSubmitInquiry(int in_id, String caller);

	int turnPurcPrice(int in_id, String caller);

	List<InquiryDetailDet> findReplyByInid(int id);

	void updateInfo(int id, String purpose, String remark, String caller);

	void nullifyInquiry(int in_id, String caller);

	JSONObject copyInquiry(int id, String caller);
	
	void agreeInquiryPrice(int id,String param);
	
	List<Map<String, Object>> getAllPurc(String caller, String condition);
	
	List<Map<String, Object>> getBom(String caller, String param);
	
	String startIQ(String caller, String formStore, String param1 , String param2, String param3);
}
