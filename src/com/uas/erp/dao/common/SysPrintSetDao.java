package com.uas.erp.dao.common;

import java.util.List;

import com.uas.erp.model.SysPrintSet;

public interface SysPrintSetDao {
	SysPrintSet getSysPrintSet(String caller,String reportname);
	List<SysPrintSet> getData(String condition, int page, int pageSize);
}
