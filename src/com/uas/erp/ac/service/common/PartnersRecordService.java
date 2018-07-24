package com.uas.erp.ac.service.common;

import java.util.Map;

import org.springframework.ui.ModelMap;

public interface PartnersRecordService {

	/**
	 * 通过营业执照号查询过滤后的信息
	 * 
	 * @param businessCode
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getAllPartnersInfosByBusinessCode(String keyword, Integer start, Integer pageNumber,
			Integer pageSize, Integer statusCode) throws Exception;

	/**
	 * 分页获取企业列表
	 * 
	 * @param keyword
	 * @param start
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getUserSpaceDetails(String keyword, Integer start, Integer pageNumber, Integer pageSize)
			throws Exception;

	public Map<String, Object> invite(String formStore) throws Exception;

	public Map<String, Object> getNewPartners(String keyword, Integer start,
			Integer page, Integer limit, Integer statusCode) throws Exception;

	public Map<String, Object> sync();

	public Map<String, Object> addprevendor(String info);
}
