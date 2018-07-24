package com.uas.erp.service.as;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.Page;

public interface HeXiaoService {
	List<?> getHexiao(Map<String, Object> filters);

	List<?> getHexiaoDetail(String code);

	String hexiaoToProdIO(String data);

	String hexiaoDelete(String data);

	Page<Map<String, Object>> getHexiaoList(int page, int start, int limit,
			Map<String, Object> filters);
}
