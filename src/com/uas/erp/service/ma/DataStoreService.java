package com.uas.erp.service.ma;

public interface DataStoreService {
  void save(String formStore, String gridStore);
  void delete(int id);
  void update(String formStore, String param);
  String getFieldsByTable(int id);
  String getExcelFxsByTable(int id);
}
