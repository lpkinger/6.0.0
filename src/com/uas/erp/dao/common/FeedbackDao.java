package com.uas.erp.dao.common;

import com.uas.erp.model.Employee;

public interface FeedbackDao {

	void replyCommetnt(int id, String comment, String sendname,Employee employee);

	int turnBuglist(int id, String language, Employee employee);

}
