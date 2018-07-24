package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.VoucherStyle;

public interface VoucherCreateService {
	/**
	 * 制作凭证
	 * 
	 * @param vs_code
	 *            公式编号
	 * @param datas
	 *            待制作凭证的数据,用逗号分隔开
	 * @param mode
	 *            single or merge
	 * @param kind
	 *            单据类型
	 * @param yearmonth
	 *            期间
	 * @param vomode
	 *            AR,AP...
	 * @param employee
	 * @return 制作失败原因
	 */
	List<Map<String, Object>> create(String vs_code, String datas, String mode, String kind, int yearmonth, String vomode, String mergerway);

	String unCreate(String vs_code, String mode, String kind, String datas, String vomode);

	String unCreatePurcfee();

	void saveVs(String formStore, String gridStore, String assStore);

	void updateVs(String formStore, String gridStore, String assStore, String groupStore);

	List<?> getDigestSource(String code, String type);

	void createSql(Integer id, String type);

	/**
	 * 按类型获取凭证公式配置
	 * 
	 * @param vs_id
	 * @param vd_class
	 * @return
	 */
	VoucherStyle getVoucherStyleByClass(int vs_id, String vd_class);

	/**
	 * 保存
	 * 
	 * @param voucherStyle
	 */
	void saveVoucherStyle(VoucherStyle voucherStyle);
}
