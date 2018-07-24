package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;

public interface BorrowApplyDao {
	void restoreBorrowApply(int id);
	void deleteBorrowApply(int id);
	void restoreBorrowApplyWithQty(int andid, double uqty);
	/**
	 * 借货申请单转入借货出货单之前，判断thisqty ≤ qty - yqty
	 */
	void checkAdYqty(List<Map<Object, Object>> datas);
	void checkBADQty(int badid,Object baid);
}
