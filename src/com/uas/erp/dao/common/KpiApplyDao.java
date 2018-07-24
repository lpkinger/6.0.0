package com.uas.erp.dao.common;

import java.util.List;
import com.uas.erp.model.DetailGrid;

public interface KpiApplyDao {
	/**
	 * @param caller
	 * @param sob 帐套信息
	 * @return
	 */
	List<DetailGrid> getGridsByCaller(String caller,String ktcode, int c, String sob);
}
