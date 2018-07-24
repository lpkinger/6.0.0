package com.uas.erp.service.scm;

import java.util.List;
import java.util.Map;

public interface ScmMonthAccountService {
	List<Map<String, Object>> getScmAccount(boolean chkun);

	List<Map<String, Object>> getScmAccountDetail(int yearmonth, String catecode, boolean chkun);

	List<Map<String, Object>> getDifferAll(int yearmonth, boolean chkun);
}
