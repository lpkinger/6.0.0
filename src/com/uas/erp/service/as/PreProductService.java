package com.uas.erp.service.as;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.Page;

public interface PreProductService {
	List<?> getPreProduct(Map<String, Object> filters);

	List<?> getPreProductDetail(String code);

	String applyToProdIO(String data);

	String applyDelete(String data);

	Page<Map<String, Object>> getApplyList(int page, int start, int limit,
			Map<String, Object> filters);
}
