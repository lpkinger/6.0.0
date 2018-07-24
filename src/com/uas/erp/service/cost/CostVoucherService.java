package com.uas.erp.service.cost;

public interface CostVoucherService {
	/**
	 * 制作费用结转凭证制作
	 * 
	 * @param employee
	 * @param language
	 * @param makeCatecode
	 *            制造费用科目
	 * @param makeToCatecode
	 *            生产成本制造费用科目
	 * @param account
	 * @return
	 */
	String makeCreate(String makeCatecode, String makeToCatecode, Boolean account, String materialsCatecode, Boolean account2,
			String manMakeCatecode, Boolean account3);

	/**
	 * 主营成本结转凭证制作
	 * 
	 * @param employee
	 * @param language
	 * @param account
	 * @return
	 */
	String mainCreate(Boolean account);

	/**
	 * 主营成本结转凭证取消
	 * 
	 * @param employee
	 * @param language
	 * @return
	 */
	String unCreate();

	/**
	 * 生产成本-工程成本结转凭证制作
	 * 
	 * @param enCatecode
	 *            生产成本-工程成本科目
	 * @param gsCatecode
	 *            发出商品-工程科目
	 * @param account
	 * @return
	 */
	String engineeringCreate(String enCatecode, String gsCatecode, Boolean account);

}
