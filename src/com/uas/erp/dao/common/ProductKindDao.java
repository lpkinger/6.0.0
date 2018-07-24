package com.uas.erp.dao.common;

import java.util.List;
import java.util.Set;

import com.uas.erp.model.ProductKind;

public interface ProductKindDao {
	List<ProductKind> getProductKindByParentId(int parentid, String allKind);

	Set<ProductKind> getProductKindBySearch(String search);
}
