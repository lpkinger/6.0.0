package com.uas.api.serve.service;

import javax.servlet.http.HttpServletRequest;


public interface  WisdomParkAgreementService {
	
	String getAgreementContent(HttpServletRequest request, String type);

}