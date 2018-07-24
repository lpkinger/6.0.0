package com.uas.erp.service.cost;

import com.uas.erp.model.Employee;

public interface InventoryAccountService {

	// *************************存货核算前检测*************************
	/**
	 * 同成本期间的库存期间是否已冻结
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_a(String type, Employee employee, Integer month, String start, String end, Boolean all);
	
	/**
	 * 当期是否有未过账的出入库单
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_b(String type, Employee employee, Integer month, String start, String end, Boolean all);
	
	/**
	 * 当期是否有未审核的生产报废单
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_c(String type, Employee employee, Integer month, String start, String end, Boolean all);
	
	/**
	 * 是否有非无值仓原材料单价为0
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_d(String type, Employee employee, Integer month, String start, String end, Boolean all);
	
	/**
	 * 出入库单单据中文状态是否有异常的
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_e(String type, Employee employee, Integer month, String start, String end, Boolean all);
	
	/**
	 * 是否有工单的成品物料编号不存在
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_f(String type, Employee employee, Integer month, String start, String end, Boolean all);
	
	/**
	 * 是否有工单用料表的物料号不存在
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_g(String type, Employee employee, Integer month, String start, String end, Boolean all);
	
	/**
	 * 当月是否有出入库单料号不存在
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_h(String type, Employee employee, Integer month, String start, String end, Boolean all);
	
	/**
	 * 当月出入库单制作了凭证的
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_i(String type, Employee employee, Integer month, String start, String end, Boolean all);
	
	/**
	 * 当月有出入库凭证编号但是凭证在当月不存在
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_j(String type, Employee employee, Integer month, String start, String end, Boolean all);
	
	/**
	 * 当月采购验收单、采购验退单、委外验收单、委外验退单生成应付暂估/应付发票并制作了凭证的
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_k(String type, Employee employee, Integer month, String start, String end, Boolean all);
	
	/**
	 * 当月出货单、销售退货单生成应收发票并制作了结转主营业务成本凭证的
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_l(String type, Employee employee, Integer month, String start, String end, Boolean all);
	
	/**
	 * 当月发出商品制作了凭证的
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_m(String type, Employee employee, Integer month, String start, String end, Boolean all);
	
	/**
	 * 当月采购验收单、委外验收单汇率与当月月度汇率是否一致
	 * 
	 * @param type
	 * @param employee
	 * @param month
	 * @param start
	 * @param end
	 * @return
	 */
	boolean co_chk_before_n(String type, Employee employee, Integer month, String start, String end, Boolean all);
	
}
