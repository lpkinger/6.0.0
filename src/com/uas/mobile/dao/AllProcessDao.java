package com.uas.mobile.dao;

import java.util.List;

import com.uas.mobile.model.AllProcess;

/**
 * 待办事宜Dao接口
 * @author suntg
 * @date 2014年9月9日 15:00:46
 */
public interface AllProcessDao {

	
	public List<AllProcess> getAllProcessByDealPersonCode(String employeeCode, String currentMaster);
	
	public List<AllProcess> getAllProcessSinceTime(String employeeCode, long time, String currentMaster);
	
	public List<AllProcess> getAllProcessSinceTimeByMaster(long time, String currentMaster);
	public List<AllProcess> getAllProcessSinceTimeByMaster2(long time, String currentMaster);
	
	public AllProcess getLastAllProcess(String employeeCode, String currentMaster);
	
	public long getLastTime(String employeeCode, String currentMaster);
	
	/**
	 * 获取账套内所有待办事宜的最大时间
	 * @param currentMaster
	 * @return
	 */
	public long getLastTimeByMaster(String currentMaster);
	
	public int getAllProcessCount(String employeeCode, String currentMaster);
	
	public int getMasterType(String currentMaster);
	
	public String getMasterSonCode(String currentMaster);
}
