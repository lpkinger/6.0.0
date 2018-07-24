package com.uas.opensys.service;
import java.util.List;
import java.util.Map;

import com.uas.erp.model.CurNavigation;
public interface CurBaseService {
 public List<CurNavigation> getCurNavigation();
 public void updateCurInfo(String tablename,String cu_code,String cu_name,Object cu_uu,String condition);
 public Map<String, Object> getCurNotify(String condition);//List<Map<String, Object>>
}
