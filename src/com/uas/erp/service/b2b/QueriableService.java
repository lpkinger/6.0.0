package com.uas.erp.service.b2b;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.QueriableMember;
import com.uas.erp.model.QueriableUser;

public interface QueriableService {

	/**
	 * 查询平台注册企业
	 * 
	 * @param name
	 *            供应商名称
	 * @param shortName
	 *            简称
	 * @param uu
	 *            UU号
	 * @return
	 */
	Map<String, Object> findMembersByVendor(String name, String shortName, Long uu);

	/**
	 * 查询平台注册企业
	 * 
	 * @param uu
	 * @return
	 */
	QueriableMember findMemberByUU(long uu);

	/**
	 * 查询平台注册用户
	 * 
	 * @param enUU
	 * @param userUU
	 * @return
	 */
	QueriableUser findUserByUU(long enUU, long userUU);

	List<Map<String, String>> findEnterprisesByKey(String key);

}
