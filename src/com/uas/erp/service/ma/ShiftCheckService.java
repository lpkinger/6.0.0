package com.uas.erp.service.ma;

/**
 * @author yingp
 *
 */
public interface ShiftCheckService {
	/**
	 * 采购单数量与来源请购单已转数量一致
	 * @param type
	 * @return
	 */
	boolean ma_chk_a(String type);
	/**
	 * 收料单数量与来源采购单已转收料数一致
	 * @param type
	 * @return
	 */
	boolean ma_chk_b(String type);
	/**
	 * 检验单检验数量、合格数、不合格数与收料单的一致
	 * @param type
	 * @return
	 */
	boolean ma_chk_c(String type);
	/**
	 * 采购验收单数量与检验单入良品仓数一致
	 * @param type
	 * @return
	 */
	boolean ma_chk_d(String type);
	/**
	 * 采购验收单(已过账)数量与采购单已验收数量一致
	 * @param type
	 * @return
	 */
	boolean ma_chk_e(String type);
	/**
	 * 不良品入库单数量与检验单入不良品仓数一致
	 * @param type
	 * @return
	 */
	boolean ma_chk_f(String type);
	/**
	 * 不良品入库单(已过账)数量与采购单不良入库数一致
	 * @param type
	 * @return
	 */
	boolean ma_chk_g(String type);
	/**
	 * 通知单数量与订单已转数量一致
	 * @param type
	 * @return
	 */
	boolean ma_chk_h(String type);
	/**
	 * 出货单(包含未过账)数量与来源通知单已转数量一致
	 * @param type
	 * @return
	 */
	boolean ma_chk_i(String type);
	/**
	 * 出货单(已过账)数量与来源订单已发货数量一致
	 * @param type
	 * @return
	 */
	boolean ma_chk_j(String type);
	/**
	 * 领料单数量(未过账)与工单已转领料数一致
	 * @param type
	 * @return
	 */
	boolean ma_chk_k(String type);
	/**
	 * 领料单数量(已过账)与工单已领料数一致
	 * @param type
	 * @return
	 */
	boolean ma_chk_l(String type);
	/**
	 * 补料单数量(未过账)与工单已转补料数一致
	 * @param type
	 * @return
	 */
	boolean ma_chk_m(String type);
	/**
	 * 补料单数量(已过账)与工单已补料数一致
	 * @param type
	 * @return
	 */
	boolean ma_chk_n(String type);
}
