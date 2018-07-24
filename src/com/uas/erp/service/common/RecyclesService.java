package com.uas.erp.service.common;

import java.util.Map;

import com.uas.erp.model.Employee;

public interface RecyclesService {
	Map<String, Object> getRecycles(int id, String language, Employee employee);
}
