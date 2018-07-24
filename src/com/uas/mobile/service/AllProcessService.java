package com.uas.mobile.service;

import java.util.List;

import com.uas.mobile.model.AllProcess;

/**
 * 待办事宜处理Service接口
 * @author suntg
 * @date 2014年9月9日 11:33:36
 */
public interface AllProcessService {
	public List<AllProcess> getAllProcessByDealPersonCode(String employeeCode, String currentMaster);
	public List<AllProcess> getAllProcessSince(String employeeCode, long time, String currentMaster);
	public long getLastTime(String employeeCode, String currentMaster);
	public AllProcess getLastAllProcess(String employeeCode, String currentMaster);
	public void setURL(AllProcess allProcess);//设置URL
	public void setUrlWithList(List<AllProcess> allProcesses);
	public int getAllProcessCount(String employeeCode, String currentMaster);
}
