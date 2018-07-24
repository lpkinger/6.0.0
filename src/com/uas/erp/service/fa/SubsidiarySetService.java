package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;

public interface SubsidiarySetService {
	List<Map<String, Object>> getSubsidiarySet(Boolean isCheck);

	List<Map<String, Object>> getShareholdersRateSet(String checkcode);

	void saveSubsidiarySet(String CheckItems);

	void saveShareholdersRateSet(String checkcode, String ParamSets);

}
