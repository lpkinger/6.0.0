package com.uas.erp.service.ma;

import java.util.List;
import java.util.Map;

import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.SearchTemplate;

public interface SearchTemplateService {
	/**
	 * 保存
	 * 
	 * @param caller
	 *            查询界面的caller
	 * @param title
	 *            方案描述
	 * @param datas
	 *            方案配置的字段
	 * @param condition
	 *            方案默认条件
	 * @param sorts
	 *            排序
	 * @param limits
	 *            权限约束
	 * @param language
	 * @param employee
	 */
	void save(String caller, String title, String datas, String condition, String sorts, String limits);

	/**
	 * 查找
	 * 
	 * @param caller
	 * @return
	 */
	List<SearchTemplate> getSearchTemplates(String caller);

	/**
	 * 导出（相对getSearchTemplates方法，还包括一些其他配置）
	 * 
	 * @param caller
	 * @return
	 */
	List<SearchTemplate> exportSearchTemplates(String caller);

	/**
	 * 保存
	 * 
	 * @param templates
	 */
	void saveSearchTemplates(List<SearchTemplate> templates);

	Integer getLastSearchLog(String caller);

	/**
	 * @param caller
	 *            查询界面的caller
	 * @param sId
	 *            方案ID
	 * @param datas
	 *            方案配置的字段
	 * @param condition
	 *            方案默认条件
	 * @param sorts
	 *            排序
	 * @param limits
	 *            权限约束
	 * @param preHook
	 *            查询前钩子
	 */
	void update(String caller, Integer sId, String datas, String condition, String sorts, String limits, String preHook);

	/**
	 * 修改方案描述
	 * 
	 * @param title
	 *            新标题
	 * @param sId
	 *            方案ID
	 */
	void updateTitle(String title, Integer sId);

	/**
	 * （未经任何修改的）复制方案
	 * 
	 * @param title
	 *            新方案标题
	 * @param sId
	 *            源ID
	 */
	void copy(String title, Integer sId);

	void delete(String caller, Integer sId);

	void log(String caller, Integer sId);

	/**
	 * 传入表名，获得表的关联sql
	 * 
	 * @param tables
	 * @return
	 */
	String getRelation(String tables);

	/**
	 * @param sId
	 *            方案ID
	 * @param filter
	 *            过滤条件
	 * @param sorts
	 *            排序
	 * @param start
	 * @param end
	 * @return
	 */
	SqlRowList getData(Integer sId, Map<String, Object> filter, String sorts, Integer start, Integer end);

	String checkCaller(String caller, String title);

	void duplTemp(String caller, Integer sId, String title);

	void saveAppuse(Integer st_id, Integer check);
}
