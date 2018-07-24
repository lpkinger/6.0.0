package com.uas.mobile.service;

import java.util.List;
import java.util.Map;

public interface APPMessageCenterService {

	Integer queryAllCount(String emcode, String type);

	List<Map<String, Object>> queryEmNews(String emcode);

	List<Map<String, Object>>  queryEmNewsDetails(String emcode, String type);

}
