package com.uas.erp.service.ma;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.DataLimit;
import com.uas.erp.model.DataLimitDetail;
import com.uas.erp.model.DataLimitInstace;

public interface DataLimitService {
  List <DataLimit> getDataLimits(Integer _all);
  DataLimitInstace getDataLimitInstace(Integer empid_,Integer jobid_,Integer limitid_);
  List<Map<String,Object>> getSourceData(Integer limitId_, String condition);
  Object InstanceDataLimit(String formData, String updates, String inserts);
  List<DataLimitDetail> getLimitDetails(Integer instanceId_);
  void CopyLimitPower(String data);
  void deleteLimitPower(String data);
}
