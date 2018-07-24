package com.uas.erp.service.cost;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.Employee;

public interface CostAccountService {

	List<Map<String, Object>> getCostAccount(boolean chkun);

	// *************************成本核算前检测*************************
	/**
	 * 同成本期间的库存期间是否已经冻结
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_a(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 当月发生的领退补完工验收报废是否有工单号+序号不存在的
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_b(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 成本表：期初成本结余金额是否等于上个月的期末成本结余金额
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_c(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 成本表：报废期初结余金额是否等于上个月的期末报废结余金额
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_d(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 成本表：期初完工数是否等于上个月的期末完工数
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_e(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 月结表：期初数量与上个月期末结余数量比较
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_f(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 月结表：期初金额与上个月期末结余金额比较
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_g(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 月结表：报废期初余额与上个月期末报废结余金额比较
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_h(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 当月制造工单没工时的
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_i(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 当期是否有未过账的出入库单
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_j(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 是否有非无值仓原材料单价为0
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_k(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 拨出拨入是否平衡
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_l(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 销售拨出拨入是否平衡
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_m(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 当月出入库是否做了凭证
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_n(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 当月出入库有凭证编号但是凭证在当月不存在
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_o(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 出入库单据中文状态
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_p(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 是否有工单的成品物料编号不存在
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_q(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 是否有工单用料表的物料不存在
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_r(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 是否有委外工单有加工单价但是没有维护币别
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_s(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 当月委外验收、验退加工价跟委外单是否一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_s1(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 当月委外验收、验退单税率跟委外单是否一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_s2(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 成本表期初完工数是否正确
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_t(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 当月出入库单里料号不存在
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_u(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 当期是否有未审核的生产报废单
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_v(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 当月发生的采购验退单、委外验收单、委外验退单生成应付发票并制作了凭证的
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_w(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 当月出货单、销售退货单生成应收发票并制作了结转主营业务成本凭证的
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_x(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 当月发出商品制作了凭证的
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_y(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	// *************************成本核算后检测*************************

	/**
	 * 成本表：工单工作中心是否与工单一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_a(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 成本表：工单类型是否与工单一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_b(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 成本表：产品编号是否不存在
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_c(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 成本表：工单工单数量是否与工单一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_d(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 成本表：本期完工数是否与实际关联的本期完工一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_e(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 成本表：本期报废数量、金额是否与用料月结表里一致（下面用料月结表也会跟实际单据比较）
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_f(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 成本表：本期领料金额是否等于∑（工单关联的用料月结表中本期领料金额+本期补料金额-本期退料金额），是否与工单关联的领退补单据一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_g(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 成本表：分摊费用按工作中心总和是否与费用表一致----按下面不同工作中心分组依次比较每一项
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_h(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 成本表：最终成本是否=总转出成本/本期完工数+加工价+上面几个分摊的单个费用
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_i(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 成本表：工单的状态是否准确
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_j(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 成本表：是否当月有发生领退补完工验收验退报废的工单都体现在成本表里
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_k(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 成本表：检查最终成本是否成功核算到完工入库、委外验收单里
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_l(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 成本表：委外加工单价跟委外单是否一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_m(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 月结表：检查月结表数据是否有异常
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_n(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 月结表：检查单位用量是否与工单用料表里一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_o(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 月结表：检查总用量是否与工单用料表里一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_p(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 月结表：检查本期领料数量、金额
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_q(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 月结表：检查本期补料数量、金额
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_r(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 月结表：检查本期退料数量、金额
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_s(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 月结表：检查单价cdm_price的逻辑(影响到退料核算)
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_t(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 月结表：检查本期报废数量
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_u(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 月结表：检查累计报废数量
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_v(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 月结表：检查本期成品入库数是否等于成本表本期完工数
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_w(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 月结表：检查实际单位用量，（总用量-前期转出数量）/期初未完工数
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_x(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 月结表：检查期末数量：期初数量+本期领料数量-本期退料数量+本期补料数量-本期转出数量-本期报废数量
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_y(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 月结表：期末金额：期初金额+本期领料金额-本期退料金额+本期补料金额-本期转出金额-本期报废金额
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_z(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 月结表：期末报废结余金额：本期报废金额+报废期初余额-本期报废转出金额
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_aa(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 检查月结表用料重复
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_ab(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 检查月结表料号不存在
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_ac(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 检查成本表领退补跟出入库差异
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_ad(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 有差异的工单、差异金额
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_ae(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 检查是否有期末完工数不等于期初完工数+本期完工数
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_af(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 检查是否有期末完工数大于工单数的
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_ag(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 检查加工价
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_ah(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 检查是否有最终成本负数的情况
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_ai(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 检查材料成本负数的情况
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_aj(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 检查标准工时是否与物料里一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_ak(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 检查总工时
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_al(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 检查有期末金额没有期末数量的情况
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_am(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 月结表：转出数量是否正确
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_an(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 成本表：本期报废转出成本与月结表本期报废转出金额是否一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_after_ao(String type, Employee employee, Integer month, Integer pmonth, String start, String end, Boolean all);

	/**
	 * 库存是否已经冻结
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_a(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当期是否有未过账的出入库单据
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_b(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 当月出入库单据料号是否存在
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_c(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 生产领料单、生产补料单、生产退料单、完工入库单、拆件入库单存货金额是否与总账科目一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_d(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 委外领料单、委外补料单、委外退料单、委外验收单、委外验退单存货金额是否与总账科目一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_e(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 其他出/入库单存货金额是否与总账科目一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_f(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 盘盈、盘亏、报废单与相应凭证存货科目是否与总账科目一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_g(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 拨入拨出单，销售拨入拨出单存货金额是否与总账科目一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_h(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 采购验收单、采购验退单存货金额是否与总账科目一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_i(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 发货单、退货单存货金额是否与总账科目一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_j(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 期末汇总表【期末结存金额】与总账对应存货科目余额是否一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_k(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 存货月结表：期初数量是否与上月期末数量一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_l(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 存货月结表：期初金额是否与上月期末金额一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_m(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 存货月结表：物料期末数量是否有数量无金额的情况
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_n(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 存货月结表：物料期末金额是否有金额无数量的情况
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_o(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 存货月结表：物料是否负数金额、负数数量的情况
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_p(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 存货核算：所有的出入库单批次单价是否核算进去
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_q(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 所有的出入库单据是否做了凭证
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_r(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 是否存在总账直接制作存货科目凭证或非出入库单、应付发票、主营业务成本结转制作的存货科目凭证
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_s(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 是否存在非无值仓单价为负数的物料
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_t(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 是否存在非无值仓单价为0的物料
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_t1(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 无值仓是否存在单价
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_u(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 是否存在上月出库当月入库的情况
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_v(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 所有出入库单据会计期间是否和凭证一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_w(String type, Employee employee, Integer month, String start, String end, Boolean all);

	/**
	 * 成本期间 与 库存期间一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_x(String type, Employee employee, Integer month, String start, String end, Boolean all);

}
