package com.uas.erp.service.oa;

public interface TaskTemplateService {
  void  saveTaskTemplate(String formStore);
  void  updateTaskTemplate(String formStore);
  void  deleteTaskTemplate(int id);
  String getTaskNodes(String caller, int keyvalue);
  void loadTaskTemplate(String caller, int keyValue);
  void bannedTaskTemplate(int id);
  void resBannedTaskTemplate(int id);
}
