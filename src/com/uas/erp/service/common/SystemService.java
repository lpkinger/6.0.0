package com.uas.erp.service.common;

import java.util.Map;
import java.util.Set;

import com.uas.erp.model.Employee;

public interface SystemService {
	/**
	 * 清除系统数据库锁定进程
	 */
	void killDbLock();

	/**
	 * 清除系统缓存
	 * 
	 * @param caches
	 *            缓存名
	 * @param all
	 *            是否包括全部账套
	 */
	void removeCache(String caches, boolean all);

	Map<String, Object> getSvnLogs(Integer page, Integer limit, String filter);

	String getSvnVersion();

	/**
	 * 更新数据库序列LAST_NUMBER
	 */
	String updateSeqNumber();

	/**
	 * 更新数据库编号MaxNumners
	 * 
	 * @return
	 */
	String updateMaxnum();

	/**
	 * 导航
	 * 
	 * @param caller
	 * @param path
	 * @param spath
	 * @return
	 */
	Object[] getSysNavigation(String caller, String path, String spath);

	/**
	 * Form 一般情况下usoftCaller==currentCaller
	 * 
	 * @param usoftCaller
	 * @param currentCaller
	 * @return
	 */
	Object[] getForm(String usoftCaller, String currentCaller);

	/**
	 * DetailGrid 一般情况下usoftCaller==currentCaller
	 * 
	 * @param usoftCaller
	 * @param currentCaller
	 * @return
	 */
	Object[] getGrid(String usoftCaller, String currentCaller);

	/**
	 * DbfindSetUI
	 * 
	 * @param usoftCaller
	 * @param usoftForm
	 * @param currentCaller
	 * @param currentForm
	 * @return
	 */
	Object[] getDbfindsetui(String usoftCaller, Object usoftForm, String currentCaller, Object currentForm);

	/**
	 * DbfindSet
	 * 
	 * @param usoftCaller
	 * @param usoftGrid
	 * @param currentCaller
	 * @param currentGrid
	 * @return
	 */
	Object[] getDbfindset(String usoftCaller, Object usoftGrid, String currentCaller, Object currentGrid);

	/**
	 * DbfindSetGrid
	 * 
	 * @param usoftCaller
	 * @param currentCaller
	 * @return
	 */
	Object[] getDbfindsetgrid(String usoftCaller, String currentCaller);

	Object[] getDatalistCombo(String usoftCaller, String currentCaller);

	Object[] getDatalist(String usoftCaller, String currentCaller);

	Object[] getTableDesc(Set<String> tableNames);

	Object[] getDataDictionary(Set<String> tableNames);

	Object[] getTriggers(Set<String> tableNames);

	Object[] getIndexes(Set<String> tableNames);

	void saveReDoLog(String url, String params, Employee employee);
}
