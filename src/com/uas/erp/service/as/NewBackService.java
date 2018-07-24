package com.uas.erp.service.as;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.Page;

public interface NewBackService {
	List<?> getNewBack(Map<String, Object> filters);

	List<?> getNewBackDetail(String code);

	String newBackToProdIO(String data);

	String newBackDelete(String data);

	Page<Map<String, Object>> getNewBackList(int page, int start, int limit,
			Map<String, Object> filters);
}
