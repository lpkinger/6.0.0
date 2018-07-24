package com.uas.erp.service.oa;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.Employee;
import com.uas.erp.model.UserSession;

public interface PagingReleaseService {

	void save(String formStore, Employee employee);

	void updateStatus(int id, int status, String master);

	String getPaging( Employee employee);

	List<UserSession> getOnlineEmployeeByOrg(int orgid);

	String turnToHoitory();
	String confirmNotifyJprocess(int id, String source);

	List<Map<String,Object>> getUsersIsOnline();
	void pagingRelease(String mans,String msg);
	void pagingRelease(String mans, String title, String context);
	void pagingRelease(String mans,String msg, String caller,String keyValue);
	void paging(String mans, String title, String context,String type);
	String getPagingById(Integer id);
	void B2BMsg(String caller, String ids, String type);
}
