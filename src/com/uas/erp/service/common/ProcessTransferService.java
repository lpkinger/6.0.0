package com.uas.erp.service.common;

import com.uas.erp.model.Employee;
public interface ProcessTransferService {
  void saveProcessTransfer(String formStore,String language,Employee employee);
  void deleteProcessTransfer(int id,String language,Employee employee);
  void updateProcessTransfer(String formStore,String langugae,Employee employee);
  void disabledProcessTransfer(int id,String language,Employee employee);
  void abledProcessTransfer(int id,String language,Employee employee);
}
