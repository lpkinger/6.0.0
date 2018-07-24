package com.uas.erp.dao.common;

import com.uas.erp.model.Transfer;

public interface TransferDao {

	/**
	 * 转单配置
	 * 
	 * @param caller
	 *            转单方案caller
	 * @param mode
	 *            模式
	 * @return
	 */
	Transfer getTransfer(String sob, String caller, String mode);

}
