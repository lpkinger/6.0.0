package com.uas.erp.service.scm;

import java.util.List;
import java.util.Map;

public interface B2CPurchaseService {

	Map<Object, List<Map<String, Object>>> getReserveByUUid(String pr_code);

	/**
	 * 
	 * @param param 批次信息
	 * @param data 请购转采购信息
	 * @param caller caller
	 * @param currency  币别
	 * @return
	 */
	String comfirmB2CPurchase(String param, String data, String caller,String currency);

}
