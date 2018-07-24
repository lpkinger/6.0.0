package com.uas.erp.service.scm;

public interface ProdAccountService {
	/**
	 * 是否有未制作凭证的单据
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @return
	 */
	boolean scm_chk_a(String type, Integer month);

	/**
	 * 是否有出入库单凭证号异常的单据
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @return
	 */
	boolean scm_chk_b(String type, Integer month);

	/**
	 * 拨出拨入是否平衡
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @return
	 */
	boolean scm_chk_c(String type, Integer month);

	/**
	 * 销售拨出拨入是否平衡
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @return
	 */
	boolean scm_chk_d(String type, Integer month);

	/**
	 * 应付发票中成本单价跟出入库成本单价是否一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @return
	 */
	boolean scm_chk_e(String type, Integer month);

	/**
	 * 暂估单成本单价跟出入库单成本单价是否一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @return
	 */
	boolean scm_chk_f(String type, Integer month);

	/**
	 * 验收单据总数量与当月开票+暂估是否一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @return
	 */
	boolean scm_chk_g(String type, Integer month);

	/**
	 * 应收发票成本单价跟出入库单成本单价是否一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @return
	 */
	boolean scm_chk_h(String type, Integer month);

	/**
	 * 发出商品成本价跟出入库单成本单价是否一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @return
	 */
	boolean scm_chk_i(String type, Integer month);

	/**
	 * 出货单据总数量与当月开票+发出商品是否一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @return
	 */
	boolean scm_chk_j(String type, Integer month);
	
	/**
	 * 检查其它出入库单据的单据类型+小类+部门是否设置了对方科目
	 * @param type
	 * @param employee
	 * @param month
	 * @return
	 */
	boolean scm_chk_k(String type, Integer month);
	/**
	 * 检查其他出入库基础科目设置是否有重复的-----提出出重复项目的pc_class\pc_type\pc_departmentcode
	 * @param type
	 * @param employee
	 * @param month
	 * @return
	 */
	boolean scm_chk_l(String type, Integer month);
	/**
	 * 检查是否有出入库单据出、入数量都不为0
	 * @param type
	 * @param employee
	 * @param month
	 * @return
	 */
	boolean scm_chk_m(String type, Integer month);
	/**
	 * 检查是否有料号不存在
	 * @param type
	 * @param employee
	 * @param month
	 * @return
	 */
	boolean scm_chk_n(String type, Integer month);
	/**
	 * 检查是否有物料的存货科目没有设置
	 * @param type
	 * @param employee
	 * @param month
	 * @return
	 */
	boolean scm_chk_o(String type, Integer month);
	/**
	 * 当期是否有未过账的出入库单据
	 * @param type
	 * @param employee
	 * @param month
	 * @return
	 */
	boolean scm_chk_p(String type, Integer month);
	/**
	 * 当期是否有无值仓有成本单价单据
	 * @param type
	 * @param employee
	 * @param month
	 * @return
	 */
	boolean scm_chk_q(String type, Integer month);
	/**
	 * 检查库存月结表金额与存货科目金额是否一致
	 * @param type
	 * @param employee
	 * @param month
	 * @return
	 */
	boolean scm_chk_r(String type, Integer month);
	
	/**
	 * 存货模块金额与总账模块金额是否一致
	 * @param type
	 * @param employee
	 * @param month
	 * @return
	 */
	boolean scm_chk_t(String type, Integer month);
	/**
	 * 检查应付暂估与存货科目金额是否一致
	 * @param type
	 * @param employee
	 * @param month
	 * @return
	 */
	boolean scm_chk_u(String type, Integer month);
	/**
	 * 检查应付发票（当月验收验退当月开票）与存货科目金额是否一致
	 * @param type
	 * @param employee
	 * @param month
	 * @return
	 */
	boolean scm_chk_v(String type, Integer month);
	/**
	 * 检查应收发出商品与存货科目金额是否一致
	 * @param type
	 * @param employee
	 * @param month
	 * @return
	 */
	boolean scm_chk_w(String type, Integer month);
	/**
	 * 检查应收发票（当月出货退货当月开票）与存货科目金额是否一致
	 * @param type
	 * @param employee
	 * @param month
	 * @return
	 */
	boolean scm_chk_x(String type, Integer month);
	/**
	 * 当月所有生产报废单是否审核
	 * @param type
	 * @param employee
	 * @param month
	 * @return
	 */
	boolean scm_chk_y(String type, Integer month);
	
	/**
	 * 是否有工单已完工未结案的
	 * @param type
	 * @param employee
	 * @param month
	 * @return
	 */
	boolean scm_chk_z(String type, Integer month);
	
}
