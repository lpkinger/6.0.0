package com.uas.erp.service.oa;

import java.util.Map;

public interface CustomMessageService {
	void save(String formStore,String gridStore);
	Map<String,Object> getModule(String module,String caller);
}
