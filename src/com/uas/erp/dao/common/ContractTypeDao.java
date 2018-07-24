package com.uas.erp.dao.common;

import java.util.List;
import java.util.Set;

import com.uas.erp.model.ContractType;
import com.uas.erp.model.ProductKind;

public interface ContractTypeDao {
	List<ContractType> getContractTypeByParentId(int parentid);
	Set<ContractType> getContractTypeBySearch(String search);
}
