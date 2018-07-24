package com.uas.pda.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.uas.erp.model.Page;


public interface PdaCommonService {
	public Page<Map<String, Object>> getProdInData(String inoutno,String whcode, int pi_id);
	public String saveBarcode (String data);
	public void checkMakeSerial(String ms_code,String makeCode,String inoutno);
	public void getPackageCode(String pr_fqty,String pa_outboxcode);
	public List<Map<String, Object>> getWhcode(String inoutNo);
	public void clearGet(int id, String whcode);
	public List<Map<String, Object>> getHaveSubmitList(int bi_piid,String whcode);
	public Map<String, Object> getBarIoCheck(String json);
	public void updateBarIoQty(int id,String json);
	public List<Map<String, Object>> getBarIoBoxCheck(String json);
	public List<Map<String, Object>> getNeedGetList(int id, String whcode,String type);
	public String returnPdfUrl(HttpServletRequest resuest ,String caller,String id,String reportName);
	public List<Map<String, Object>> getFieldsDatas (String field, String caller, String condition);
}
