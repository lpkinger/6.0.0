package com.uas.erp.dao.common;

import java.util.List;

import com.uas.erp.model.Form;
import com.uas.erp.model.RelativeSearch;
import com.uas.erp.model.RelativeSearchLimit;

public interface FormDao {

	/**
	 * 清除form缓存
	 * 
	 * @param sob
	 * @param caller
	 */
	void cacheEvict(String sob, String caller);

	/**
	 * @param caller
	 * @param sob
	 *            帐套信息
	 * @return
	 */
	Form getForm(String caller, String sob);

	/**
	 * Form--关联查询
	 * 
	 * @param caller
	 * @param sob
	 * @return
	 */
	List<RelativeSearch> getRelativeSearchs(String caller, String sob);

	/**
	 * Form--关联查询
	 * 
	 * @param id
	 *            rs_id
	 * @param sob
	 * @return
	 */
	RelativeSearch getRelativeSearch(int id, String sob);

	/**
	 * 个人权限
	 * 
	 * @param caller
	 * @param em_id
	 * @param jo_id
	 * @param sob
	 * @return
	 */
	List<RelativeSearchLimit> getRelativeSearchLimitsByEmpl(String caller, Integer em_id, String sob);

	/**
	 * 岗位权限
	 * 
	 * @param caller
	 * @param em_id
	 * @param jo_id
	 * @param sob
	 * @return
	 */
	List<RelativeSearchLimit> getRelativeSearchLimitsByJob(String caller, Integer jo_id, String sob);

}
