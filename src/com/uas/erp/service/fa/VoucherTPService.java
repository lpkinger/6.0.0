package com.uas.erp.service.fa;

import java.util.Map;

import net.sf.json.JSONObject;

public interface VoucherTPService {

	void saveVoucherTP(String formStore, String gridStore, String assStore, String caller);

	void updateVoucherTP(String formStore, String gridStore, String assStore, String caller);

	void deleteVoucherTP(int vo_id, String caller);

	/**
	 * 制作凭证
	 * 
	 * @param vo_id
	 * @return
	 */
	JSONObject createVoucher(int vo_id);

	/**
	 * 加载模板
	 * 
	 * @param vo_id
	 * @return
	 */
	Map<String, Object> getTp(int vo_id);

	/**
	 * 从凭证添加到模板
	 * 
	 * @return
	 */
	JSONObject createTpByVo(int id);

}
