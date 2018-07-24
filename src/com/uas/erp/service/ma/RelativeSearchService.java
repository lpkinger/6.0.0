package com.uas.erp.service.ma;

import com.uas.erp.model.RelativeSearch;

public interface RelativeSearchService {

	void saveRelativeSearch(String formStore, String[] gridStore, String caller);

	void updateRelativeSearchById(String formStore, String[] gridStore, String caller);

	void deleteRelativeSearch(int id, String caller);

	/**
	 * 查找
	 * 
	 * @param id
	 * @return
	 */
	RelativeSearch getRelativeSearch(int id);

	/**
	 * 更新
	 * 
	 * @param relativeSearch
	 */
	void saveRelativeSearch(RelativeSearch relativeSearch);

}
