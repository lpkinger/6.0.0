package com.uas.erp.service.scm;

import java.util.Map;

public interface PreProductService {
	void savePreProduct(String formStore);
	void updatePreProductById(String formStore);
	void deletePreProduct(int pre_id);
	String auditPreProduct(int pre_id, String caller);
	void resAuditPreProduct(int pre_id, String caller);
	void submitPreProduct(int pre_id, String caller);
	void resSubmitPreProduct(int pre_id, String caller); 
	int turnFormal(int pre_id);
	String getkind(String code);
	int turninquiry(int pre_id);
	int turnsample(int pre_id);
	int getPreCount(String pre_spec,int pre_id);
	void checkProdCode(Object pre_id, Object prcode);
}
