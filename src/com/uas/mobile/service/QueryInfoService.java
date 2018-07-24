package com.uas.mobile.service;

import java.util.List;
import java.util.Map;

import com.uas.erp.dao.SqlRowList;

public interface QueryInfoService {

	Map<String, Object> getInfoByCode(String emcode, String condition);

	Map<String, Object> getReportCondition(String caller, String title);

	Map<String, Object> getQueryJsp(String emcode);

	Map<String,Object> getSchemeConditin(String caller, String id);

	Map<String,Object> getSchemeResult(String caller, Integer id, int pageIndex,
			int pageSize, String condition);

}