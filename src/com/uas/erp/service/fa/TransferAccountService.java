package com.uas.erp.service.fa;

import java.util.Map;



public interface TransferAccountService {
/*	
	Map<String, Object> singleTransferRequest(String ip,String class_,String code);	
	
	Map<String, Object> searchRequest(String ip,String class_,String code);*/
	
	Map<String, Object> postRequests(String ip, String data,String psw, String trnCode, 
			String banktype);
}
