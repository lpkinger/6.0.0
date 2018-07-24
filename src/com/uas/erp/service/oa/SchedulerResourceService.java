package com.uas.erp.service.oa;

import java.util.List;
import java.util.Map;
public interface SchedulerResourceService {
    Map<String, List<Map<String, Object>>> getSchedulerResourceData(String caller);
}
