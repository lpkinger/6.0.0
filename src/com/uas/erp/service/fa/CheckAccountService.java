package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.BillError;
import com.uas.erp.model.Employee;

public interface CheckAccountService {

	/**
	 * 获取当前期间、起始和结束时间
	 * 
	 * @param type
	 * @return
	 */
	Map<String, Object> getYearMonth(String type, String votype);

	/**
	 * 检测项详细结果
	 * 
	 * @param type
	 *            be_type
	 * @return
	 */
	List<BillError> getBillErrors(String type, Boolean all);

	// *************************应收期末对账检查*************************

	/**
	 * 收帐款期间 与 总账期间一致
	 * 
	 * @param month
	 *            应收期间
	 */
	boolean ar_chk_a(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月的 出货单、退货单、发票、其它应收单、发出商品、收退款单、预收单、结算冲帐单已过账
	 * 
	 * @param month
	 *            应收期间
	 */
	boolean ar_chk_b(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月的 发票、其它应收单、发出商品、收退款单、预收单、结算单已制作凭证
	 * 
	 * @param month
	 *            应收期间
	 */
	boolean ar_chk_c(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月的 出货单、退货单已全部转开票或发出商品
	 * 
	 * @param month
	 *            应收期间
	 */
	boolean ar_chk_d(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月的 发票、发出商品(全部未开票的) 的销售单价、成本单价与 出货单、退货单 的一致
	 * 
	 * @param month
	 *            应收期间
	 */
	boolean ar_chk_e(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月凭证中，应收款科目有手工录入的(来源为空的)
	 * 
	 * @param month
	 *            应收期间
	 */
	boolean ar_chk_f(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月的 总的开票数量与 出货单、退货单 的开票数量一致
	 * 
	 * @param month
	 *            应收期间
	 */
	boolean ar_chk_g(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月的 总的发出商品数量是否与 出货单、退货单 的发出商品数量一致
	 * 
	 * @param month
	 *            应收期间
	 */
	boolean ar_chk_h(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月开票数据中 涉及发出商品的 总的开票数量与 发出商品的开票数量一致
	 * 
	 * @param month
	 *            应收期间
	 */
	boolean ar_chk_i(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月预收款、预收退款与应收总账里本期预收的一致
	 * 
	 * @param month
	 *            应收期间
	 */
	boolean ar_chk_j(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月预收冲账与应收总账里本期预收冲账的一致
	 * 
	 * @param month
	 *            应收期间
	 */
	boolean ar_chk_k(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月发出商品(成本价)与应收总账里本期发出商品cm_gsnowamount的一致
	 * 
	 * @param month
	 *            应收期间
	 */
	boolean ar_chk_l(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月发出商品(销售价)与应收总账里本期发出商品cm_gsnowamounts的一致
	 * 
	 * @param month
	 *            应收期间
	 */
	boolean ar_chk_m(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月开票数据中 涉及发出商品的（成本价）与应收总账里本期发出商品转开票cm_gsinvoamount的一致
	 * 
	 * @param month
	 *            应收期间
	 */
	boolean ar_chk_n(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月开票数据中 涉及发出商品的（销售价）与应收总账里本期发出商品转开票cm_gsinvoamounts的一致
	 * 
	 * @param month
	 *            应收期间
	 */
	boolean ar_chk_o(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月的 发票、其它应收单 的总额与 应收总账本期应收一致
	 * 
	 * @param month
	 *            应收期间
	 */
	boolean ar_chk_p(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月的 收款单、退款单、结算单 的总额与 应收总账的本期收款一致
	 * 
	 * @param month
	 *            应收期间
	 */
	boolean ar_chk_q(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月的 发票销售总额 与 主营业务收入 的贷方一致
	 * 
	 * @param month
	 *            应收期间
	 */
	boolean ar_chk_r(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月的 发票成本总额 与 主营业务成本 的借方发生一致
	 * 
	 * @param month
	 *            应收期间
	 */
	boolean ar_chk_s(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月应收发票中来源的发出商品是否全部是日期小于当月的
	 * 
	 * @param month
	 *            应收期间
	 */
	boolean ar_chk_t(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月应收发票中来源的出入库是否存在日期非当月的
	 * 
	 * @param month
	 *            应收期间
	 */
	boolean ar_chk_u(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月应收账款科目余额与应收总账应收期末余额一致
	 * 
	 * @param month
	 *            应收期间
	 */
	boolean ar_chk_v(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月预收账款科目余额与应收总账预收期末余额一致
	 * 
	 * @param month
	 *            应收期间
	 */
	boolean ar_chk_w(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月发出商品科目余额与应收总账发出商品期末余额(成本金额)一致
	 * 
	 * @param month
	 *            应收期间
	 */
	boolean ar_chk_x(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月的 预收和应收同时有余额
	 * 
	 * @param month
	 *            应收期间
	 */
	boolean ar_chk_y(String type, Employee employee, Integer month, String start, String end, Boolean all);

	// *************************应付期末对账检查*************************
	/**
	 * 付帐款期间 与 总账期间一致
	 * 
	 * @param month
	 *            应付期间
	 */
	boolean ap_chk_a(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月的 验收单、验退单、发票、其它应付单、暂估、付退款单、预付单、结算冲帐单已过账
	 * 
	 * @param month
	 *            应付期间
	 */
	boolean ap_chk_b(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月的 发票、其它应付单、暂估、付退款单、预付单、结算单已制作凭证
	 * 
	 * @param month
	 *            应付期间
	 */
	boolean ap_chk_c(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月的 验收单、验退单已全部转开票或暂估
	 * 
	 * @param month
	 *            应付期间
	 */
	boolean ap_chk_d(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月的 发票、暂估(全部未开票的) 的采购单价、成本单价与 验收单、验退单 的一致
	 * 
	 * @param month
	 *            应付期间
	 */
	boolean ap_chk_e(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月凭证中，应付款科目有手工录入的(来源为空的)
	 * 
	 * @param month
	 *            应付期间
	 */
	boolean ap_chk_f(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月的 总的开票数量与 验收单、验退单 的开票数量一致
	 * 
	 * @param month
	 *            应付期间
	 */
	boolean ap_chk_g(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月的 总的暂估数量是否与 验收单、验退单 的暂估数量一致
	 * 
	 * @param month
	 *            应付期间
	 */
	boolean ap_chk_h(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月开票数据中 涉及暂估的 总的开票数量与 暂估的开票数量一致
	 * 
	 * @param month
	 *            应付期间
	 */
	boolean ap_chk_i(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月预付款、预付退款与应付总账里本期预付的一致
	 * 
	 * @param month
	 *            应付期间
	 */
	boolean ap_chk_j(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月预付冲账与应付总账里本期预付冲账的一致
	 * 
	 * @param month
	 *            应付期间
	 */
	boolean ap_chk_k(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月暂估与应付总账里本期应付暂估增加一致
	 * 
	 * @param month
	 *            应付期间
	 */
	boolean ap_chk_l(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月暂估(采购价)与应付总账里本期暂估vm_esnowamounts的一致
	 * 
	 * @param month
	 *            应付期间
	 */
	boolean ap_chk_m(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月开票数据中涉及暂估的与应付总账里本期应付暂估减少一致
	 * 
	 * @param month
	 *            应付期间
	 */
	boolean ap_chk_n(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月开票数据中 涉及暂估的（采购价）与应付总账里本期暂估转开票vm_esinvoamounts的一致
	 * 
	 * @param month
	 *            应付期间
	 */
	boolean ap_chk_o(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月的 发票、其它应付单 的总额与 应付总账本期应付一致
	 * 
	 * @param month
	 *            应付期间
	 */
	boolean ap_chk_p(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月的 付款单、退款单、结算单 的总额与 应付总账的本期付款一致
	 * 
	 * @param month
	 *            应付期间
	 */
	boolean ap_chk_q(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月应付账款科目余额与应付总账应付期末余额是否一致
	 */
	boolean ap_chk_r(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月预付账款科目余额与预付总账预付期末余额是否一致
	 */
	boolean ap_chk_s(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月应付暂估科目余额与应付总账应付暂估余额(采购价除税)是否一致
	 */
	boolean ap_chk_t(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月的 预付和应付同时有余额
	 */
	boolean ap_chk_u(String type, Employee employee, Integer month, String start, String end, Boolean all);

	// *************************固定资产对账检查************************

	/**
	 * 卡片资料是否完整
	 * 
	 * @param month
	 *            固定资产期间
	 */
	boolean fix_chk_a(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 是否计提折旧
	 * 
	 * @param month
	 *            固定资产期间
	 */
	boolean fix_chk_b(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 折旧单,资产增加、减少单是否过账
	 * 
	 * @param month
	 *            固定资产期间
	 */
	boolean fix_chk_c(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 卡片,折旧单,资产增加、减少单是否制作凭证
	 * 
	 * @param month
	 *            固定资产期间
	 */
	boolean fix_chk_d(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月卡片相关单据是否全部审核
	 * 
	 * @param month
	 *            固定资产期间
	 */
	boolean fix_chk_e(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 净值是否出现负数
	 * 
	 * @param month
	 *            固定资产期间
	 */
	boolean fix_chk_f(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean fix_chk_g(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean fix_chk_h(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean fix_chk_i(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean fix_chk_j(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean fix_chk_k(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean fix_chk_l(String type, Employee employee, Integer month, String start, String end, Boolean all);

	// *************************银行现金对账检查************************

	/**
	 * 当月银行现金单据是否全部记账
	 * 
	 * @param month
	 *            银行期间
	 */
	boolean gs_chk_a(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月银行现金单据是否全部做了凭证
	 * 
	 * @param month
	 *            银行期间
	 */
	boolean gs_chk_b(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 银行现金余额是否出现负数
	 * 
	 * @param month
	 *            银行期间
	 */
	boolean gs_chk_c(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 银行现金单据会计期间是否和凭证一致
	 * 
	 * @param month
	 *            银行期间
	 */
	boolean gs_chk_d(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 银行各账户余额（期末平衡表）与总账对应科目原币余额是否一致
	 * 
	 * @param month
	 *            银行期间
	 */
	boolean gs_chk_e(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gs_chk_f(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gs_chk_g(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gs_chk_h(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gs_chk_i(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gs_chk_j(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gs_chk_k(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gs_chk_l(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gs_chk_m(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gs_chk_n(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gs_chk_o(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gs_chk_p(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gs_chk_q(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gs_chk_r(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gs_chk_s(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gs_chk_t(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gs_chk_u(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gs_chk_v(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gs_chk_w(String type, Employee employee, Integer month, String start, String end, Boolean all);

	// *************************总账期末对账检查*************************
	boolean gla_chk_a(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gla_chk_b(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gla_chk_c(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gla_chk_d(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gla_chk_e(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gla_chk_f(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gla_chk_g(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gla_chk_h(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gla_chk_i(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean gla_chk_j(String type, Employee employee, Integer month, String start, String end, Boolean all);

	// *************************成本期末对账检查*************************
	boolean cost_chk_a(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean cost_chk_b(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean cost_chk_c(String type, Employee employee, Integer month, String start, String end, Boolean all);

	boolean cost_chk_d(String type, Employee employee, Integer month, String start, String end, Boolean all);
	
	// *************************期末对账设置*************************
	List<Map<String,Object>> getCheckItems(String module,Boolean isCheck);
	
	Map<String,Object> saveCheckItem(Employee employee,String CheckItem);
	
	void saveCheckItems(Employee employee,String CheckItems);
	
	List<Map<String,Object>> getParamSets(String checkcode);
	
	void saveParamSets(String checkcode,String ParamSets);
	
	List<Map<String,Object>> getErrorSets(String checkcode);
	
	void saveErrorSets(String checkcode,String ErrorSets);
	
	void deleteSet(String checkcode,String key,Boolean isParamSet);
	
	void getCheckItemStatus(String module,String billoutmode,Boolean checked);
	
	// *************************期末结账*************************
	Map<String, Object> checkAccounts(String module,String yearmonth);
	
	Map<String, Object> getShowDetailGrid(String checkcode);
	
	void refreshEndData(String month,String module);
}
