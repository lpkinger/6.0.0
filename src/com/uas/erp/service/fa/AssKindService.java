package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;

public interface AssKindService {
	void saveAssKind(String formStore, String caller);

	void updateAssKindById(String formStore, String caller);

	void deleteAssKind(int ak_id, String caller);

	/**
	 * @return 核算类型
	 */
	List<Map<String, Object>> getAssKind();
}
