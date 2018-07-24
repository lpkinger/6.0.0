package com.uas.erp.service.common;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.CommonUse;
import com.uas.erp.model.Employee;
import com.uas.erp.model.ShortCut;
import com.uas.erp.model.WorkBench;

public interface WorkBenchService {
	List<WorkBench> getWorkBench(Employee employee);

	void setWorkBench(Employee employee, String data);

	List<ShortCut> getShortCut(Employee employee);

	void setShortCut(Employee employee, String data);

	List<CommonUse> getCommonUses(Employee employee, Integer count);

	void setCommonUse(Integer id, String url, String addUrl, Employee employee,String caller);

	void deleteCommonUse(int cuid);

	void updateCommonUse(Employee employee, int cuid, int type);
	
	void lockCommonUse(Employee employee, int cuid, int type);

	Map<String,Object> getDatas(String type);
}
