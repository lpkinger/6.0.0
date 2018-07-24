package com.uas.erp.service.common;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;

import com.uas.erp.model.DeskTop;
import com.uas.erp.model.Employee;

public interface DeskTopService {

	List<DeskTop> getOwner(Employee employee);
	Map<String, Object> getData(String caller, String condition, String orderby, Integer count);
	Map<String, Object> getProcess_UnDo(int count);
	Map<String, Object> getDeskProcess(int count, String type, Model model,Integer isMobile, Employee employee);
	String setTotalCount(int count, String type);
	String setDetno(String nodes);
	Map<String, Object> getNews(int start,int end);
	Map<String, Object> getNote_Notice(int start,int end);
	Map<String, Object> getNote_Inform(int start,int end);
	Map<String, Object> getSubs(int count,String condition);
	Map<String, Object> getCustBirth(int count, String condition);
	void setDeskTop(String param);
	Map<String, Object> getFeedback(int count, String condition);
	Map<String, Object> getKpibill(int count, String condition);
	Map<String, Object> getBench(Employee employee, String portid);
	List<Map<String, Object>> getData(String caller, String condition, int pageSize);
	List<Map<String, Object>> getBenchSet(Employee employee);
	List<Map<String, Object>> getReports(Employee employee,String code);
	List<Map<String, Object>> getQuerys(Employee employee,String code);
	List<Map<String, Object>> getMyStore(Employee employee);
	void changeReports(int sn_id,Employee employee,String type);
	Map<String, Object> getDeskFlow(int count, String type, Model model,Integer isMobile, Employee employee);
}
