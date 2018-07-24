package com.uas.erp.service.ma;

import java.util.Map;

public interface  ObjectExplainService {
	Map<String,Object> getData(String condition);
	void saveObjectExplain(String formStore);
}
