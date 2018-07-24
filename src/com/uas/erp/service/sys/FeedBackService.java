package com.uas.erp.service.sys;

import java.util.List;
import java.util.Map;

import org.springframework.ui.Model;

import com.uas.erp.model.Employee;
import com.uas.erp.model.FeedBackFlow;
import com.uas.erp.model.JSONTree;

public interface FeedBackService {

	void saveFeedback(String formStore, String param, String language,
			Employee employee);
	void deleteFeedback(int id, String language, Employee employee);
	void updateFeedback(String formStore, String param, String language,
			Employee employee);
	void reply(int id, String comment, String language, Employee employee);
	int feedbackTurnBug(String language, Employee employee, int id);
	void changestatus(String language, Employee employee, int id);
	void endFeedback(String language, Employee employee, int id);
	void canceltask(String language, Employee employee, int id);
	void submit(int id, String language, Employee employee);
	void reSubmit(int id, String language, Employee employee);
	void audit(int id);
	void resAudit(int id, String language, Employee employee);
	void backPlan(String data);
	void confirm(String data, Integer _customer, Integer _process);
	void changeHandler(String data);
	List<Map<String, Object>> getDay_count(String condition);
	List<Map<String, Object>> getWeek_count(String condition);
	List<Map<String, Object>> getMonth_count(String condition);
	FeedBackFlow getCurrentNode(String kind, String position);
	void processConfirm(String data,String step);
	void CustomerAudit(int id);
	List<Map<String, Object>> getFeedback(String condition);
	List<JSONTree> getJSONTreeByParentId(int parentId, String kind,String condition, Integer _noc);
}
