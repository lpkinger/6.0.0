package com.uas.mobile.service;
import java.util.List;
import com.uas.mobile.model.MobileTask;
public interface CanlendarTaskService {
  List<MobileTask> getCanlendarTask(String condition,String sessionId);
}
