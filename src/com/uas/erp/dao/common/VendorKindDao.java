package com.uas.erp.dao.common;

import java.util.List;
import com.uas.erp.model.VendorKind;

public interface VendorKindDao {
	List<VendorKind> getVendorKindByParentId(int parentid);
}
