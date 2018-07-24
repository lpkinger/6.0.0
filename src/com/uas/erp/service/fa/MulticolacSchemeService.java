package com.uas.erp.service.fa;

import java.util.Map;


public interface MulticolacSchemeService {
	void saveMulticolacScheme(String formStore,String param,String caller);
	void updateMulticolacScheme(String formStore,String param,String param2,String caller);
	void deleteMulticolacScheme(int id, String caller);
	void submitMulticolacScheme(int id, String caller);
	void resSubmitMulticolacScheme(int id, String caller);
	void auditMulticolacScheme(int id, String caller);
	void resAuditMulticolacScheme(int id, String caller);
	Map<Object, Object> autoArrange(String formStore, String caller);
}
