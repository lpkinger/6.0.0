package com.uas.erp.controller.cost;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Employee;
import com.uas.erp.service.cost.InventoryAccountService;

@Controller
public class InventoryAccountController {

	@Autowired
	private InventoryAccountService inventoryAccountService;

	// *************************存货核算前检测*************************

	/**
	 * 同成本期间的库存期间是否已冻结
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_before_a.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_a(HttpSession session,
			String type, Integer month, Integer pmonth, String start,
			String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", inventoryAccountService.co_chk_before_a(type,
				employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当期是否有未过账的出入库单
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_before_b.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_b(HttpSession session,
			String type, Integer month, Integer pmonth, String start,
			String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", inventoryAccountService.co_chk_before_b(type, employee,
				month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当期是否有未审核的生产报废单
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_before_c.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_c(HttpSession session,
			String type, Integer month, Integer pmonth, String start,
			String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", inventoryAccountService.co_chk_before_c(type, employee,
				month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 是否有非无值仓原材料单价为0
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_before_d.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_d(HttpSession session,
			String type, Integer month, Integer pmonth, String start,
			String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", inventoryAccountService.co_chk_before_d(type, employee,
				month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 出入库单单据中文状态是否有异常的
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_before_e.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_e(HttpSession session,
			String type, Integer month, Integer pmonth, String start,
			String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", inventoryAccountService.co_chk_before_e(type, employee,
				month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 是否有工单的成品物料编号不存在
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_before_f.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_f(HttpSession session,
			String type, Integer month, Integer pmonth, String start,
			String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", inventoryAccountService.co_chk_before_f(type, employee,
				month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 是否有工单用料表的物料号不存在
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_before_g.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_g(HttpSession session,
			String type, Integer month, Integer pmonth, String start,
			String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", inventoryAccountService.co_chk_before_g(type, employee,
				month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月是否有出入库单料号不存在
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_before_h.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_h(HttpSession session,
			String type, Integer month, Integer pmonth, String start,
			String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", inventoryAccountService.co_chk_before_h(type, employee,
				month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月出入库单制作了凭证的
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_before_i.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_i(HttpSession session,
			String type, Integer month, Integer pmonth, String start,
			String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", inventoryAccountService.co_chk_before_i(type, employee,
				month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月有出入库凭证编号但是凭证在当月不存在
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_before_j.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_j(HttpSession session,
			String type, Integer month, Integer pmonth, String start,
			String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", inventoryAccountService.co_chk_before_j(type, employee,
				month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月采购验收单、采购验退单、委外验收单、委外验退单生成应付暂估/应付发票并制作了凭证的
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_before_k.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_k(HttpSession session,
			String type, Integer month, Integer pmonth, String start,
			String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", inventoryAccountService.co_chk_before_k(type, employee,
				month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月出货单、销售退货单生成应收发票并制作了结转主营业务成本凭证的
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_before_l.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_l(HttpSession session,
			String type, Integer month, Integer pmonth, String start,
			String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", inventoryAccountService.co_chk_before_l(type, employee,
				month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月发出商品制作了凭证的
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_before_m.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_m(HttpSession session,
			String type, Integer month, Integer pmonth, String start,
			String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", inventoryAccountService.co_chk_before_m(type, employee,
				month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月采购验收单、委外验收单汇率与当月月度汇率是否一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_before_n.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_n(HttpSession session,
			String type, Integer month, Integer pmonth, String start,
			String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", inventoryAccountService.co_chk_before_n(type, employee,
				month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}
}
