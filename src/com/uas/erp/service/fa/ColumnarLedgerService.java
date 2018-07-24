package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.ColumnarLedgerFilter;

public interface ColumnarLedgerService {

	/**
	 * 获取本次查询核算号
	 * 
	 * @param filter
	 * @return
	 */
	ColumnarLedgerFilter getCurrentAsscode(ColumnarLedgerFilter filter);

	/**
	 * 多栏账方案生成动态grid列
	 * 
	 * @param filter
	 * @return
	 */
	Object[] getGridColumnsByMulticolacScheme(ColumnarLedgerFilter filter);

	List<Map<String, Object>> getColumnarLedger(ColumnarLedgerFilter filter);

}
