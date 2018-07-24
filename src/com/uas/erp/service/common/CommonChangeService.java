package com.uas.erp.service.common;


public interface CommonChangeService {
 public void saveCommonChange(String caller,String formStore,String GridStore);
 public void updateCommonChange(String caller,String formStore,String GridStore);
 public void deleteCommonChange(int id ,String caller);
 public void submitCommonChange(String caller, int id);
 public void resSubmitCommonChange(String caller, int id);
public void resAuditCommonChange(String caller, int id);
public void auditCommonChange(int id, String caller);
public void updateChangetype( String caller,int id);
 
}
