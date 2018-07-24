package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;

public interface ProjectDocPowerDao {

	void powerForManage(Object id,Integer _noc);
	String powerForScan(Object id,Integer _noc);
	void powerForRead(Object id,Integer _noc);
	String powerForUpload(Object id,Integer _noc);
	void powerForDown(Object id,Integer _noc);
	
	List<Object[]>  getFileList(Boolean superPower,String condition, boolean canRead);
	
	List<Map<String, Object>> getFilePowers(Integer docid,Integer prjid);
	void saveFilePowers(Boolean appyforChilds,List<Map<Object, Object>> store);
	
	void powerForAddRoot(Object prjid, Integer _noc);
	
	List<Object[]> getFileList(Boolean superPower,String formCondition,String condition,String search);
	
	Object getCountFile(Boolean superPower,String formCondition,String search);
}
