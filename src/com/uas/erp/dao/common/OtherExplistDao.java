package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;

import com.uas.erp.dao.SqlRowList;

/**
 * @author madan
 * 
 */
public interface OtherExplistDao {

	void updateStatus(int mdid);

	void checkYqty(List<Map<Object, Object>> datas);

	void restoreSourceYqty(SqlRowList oldpd, SqlRowList newpd);

}
