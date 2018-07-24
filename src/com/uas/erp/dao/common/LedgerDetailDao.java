package com.uas.erp.dao.common;

import com.uas.erp.model.LedgerFilter;

public interface LedgerDetailDao {

	LedgerFilter queryByFilter(final LedgerFilter filter);

	LedgerFilter queryByFilterMulti(final LedgerFilter filter);
}
