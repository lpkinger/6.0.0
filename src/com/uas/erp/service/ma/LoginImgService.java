package com.uas.erp.service.ma;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.uas.erp.model.Employee;

public interface LoginImgService {
	int save(String path, int size, String fileName, Employee employee);
	Map<String,Object> hasLoginImg();
	void deleteLoginImg();
	void getLoginImg(HttpServletResponse response, HttpServletRequest request) throws IOException, KeyManagementException, NoSuchAlgorithmException;
}
