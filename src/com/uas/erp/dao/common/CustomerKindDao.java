package com.uas.erp.dao.common;

import java.util.List;
import com.uas.erp.model.CustomerKind;

public interface CustomerKindDao {
	List<CustomerKind> getCustomerKindByParentId(int parentid);
}
