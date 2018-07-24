package com.uas.erp.service.plm;

public interface ProjectTemplateService {
	void saveProjectTemplate(String formStore); 
	void updateProjectTemplate(String formStore);
	void deleteProjectTemplate(int id);
    String getProjectTemplateData(String caller, String condition);
}
