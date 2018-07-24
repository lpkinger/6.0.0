package com.uas.erp.dao.common;

public interface MakeCraftDao {
	/**
	 * 每个工序采集之后更新makeCraftdetail,makeProcess,makeSerial
	 * 
	 * @param type
	 *            采集名称
	 * @param result
	 *            采集结果
	 * @param ms_code
	 *            序列号
	 * @param mc_code
	 *            作业单号
	 * @param step_code
	 *            工序编号
	 */
	void updateMakeMessage(String type, String result, String ms_code, String mc_code, String step_code);

	/**
	 * 判断采集点的工序是否符合该序列号的nextstepcode判断做成公共的方法
	 * 
	 * @param stepcode
	 *            根据资源编号带出的stepcode 当前工序
	 * @param ms_sncode
	 *            序列号
	 * @param mc_code
	 * @return
	 */
	boolean ifNextStepcode(String stepcode, String ms_sncode, String mc_code);

	/**
	 * 转根据作业单领料单
	 */
	void turnProdOut(String mc_code);

	/**
	 * 检验采集的是否为替代料
	 * 
	 * @param rep_code
	 * @param prod_code
	 * @return
	 */
	boolean checkRep(String prod_code, String rep_code);

	/**
	 * 判断工序是否已经记录过了
	 * 
	 * @param ms_sncode
	 * @param st_code
	 * @return
	 */
	boolean checkHaveGetStep(String ms_sncode, String st_code);

	/**
	 * 更新工序委外单转单状态
	 * 
	 * @param mc_id
	 */
	void updateStatus(Object mc_id);
}
