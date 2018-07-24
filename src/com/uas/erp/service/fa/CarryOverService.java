package com.uas.erp.service.fa;

/**
 * 结转生成凭证
 * 
 * @author yingp
 * 
 */
public interface CarryOverService {

	/**
	 * 本月完工结转
	 */
	public String makeMonthComplete(Boolean account);

	/**
	 * 研发费用结转
	 */
	public String researchCost(Boolean account);

}
