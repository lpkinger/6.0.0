package com.uas.erp.dao.common;

import java.util.List;
import com.uas.erp.model.DetailGrid;

public interface DetailGridDao {
	/**
	 * @param caller
	 * @param sob 帐套信息
	 * @return
	 */
	List<DetailGrid> getDetailGridsByCaller(String caller, String sob);
}
