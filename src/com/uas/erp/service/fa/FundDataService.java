package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;

public interface FundDataService {
	
	List<Map<String, Object>> autogetItems(Integer id,String kind);
	
	void save(String formStore, String gridStore, String caller);

	void update(String formStore, String gridStore, String caller);

	void delete(int fd_id, String caller);

}
