package com.uas.erp.service.hr;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

public interface StartExamService {
	public Map<String, Object> start(String password,String name,HttpSession session,String code);
	public Map<String, Object> getExam(int ex_id);
	public Map<String, Object> checkExam(int ex_id);
	public void submitExam(List<Map<Object, Object>> data);
	public void selScheme(String caller,String data);
	public void judgeExam(List<Map<Object, Object>> data);
}
