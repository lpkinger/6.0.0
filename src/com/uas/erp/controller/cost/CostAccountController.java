package com.uas.erp.controller.cost;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Employee;
import com.uas.erp.service.cost.CostAccountService;
import com.uas.erp.service.fa.MonthAccountService;

@Controller
public class CostAccountController {

	@Autowired
	private CostAccountService costAccountService;
	@Autowired
	private MonthAccountService monthAccountService;

	/**
	 * 期末对账
	 */
	@RequestMapping("/co/cost/monthAccount.action")
	@ResponseBody
	public Map<String, Object> monthAccount(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		JSONObject js = JSONObject.fromObject(condition);
		boolean chkun = js.getBoolean("chkun");
		if (chkun)
			monthAccountService.preWriteVoucher();
		modelMap.put("data", costAccountService.getCostAccount(chkun));
		modelMap.put("success", true);
		return modelMap;
	}

	// *************************成本核算前检测*************************
	/**
	 * 成本表：工单工作中心是否与工单一致
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_before_a.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_a(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_a(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月发生的领退补完工验收报废是否有工单号+序号不存在的
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_before_b.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_b(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_b(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 成本表：前期成本结余金额是否等于上个月的期末成本结余金额
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_before_c.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_c(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_c(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 成本表：报废期初结余金额是否等于上个月的期末报废结余金额
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_before_d.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_d(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_d(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 成本表：前期完工数是否等于上个月的期末完工数
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_before_e.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_e(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_e(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 月结表：期初数量与上个月期末结余数量比较
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_before_f.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_f(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_f(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 月结表：期初金额与上个月期末结余金额比较
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_before_g.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_g(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_g(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 月结表：报废期初余额与上个月期末报废结余金额比较
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_before_h.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_h(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_h(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月有完工数的制造单没工时的
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_before_i.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_i(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_i(type, employee, month, pmonth, start, end, all));
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
	@RequestMapping("co/cost/chk_before_j.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_j(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_j(type, employee, month, pmonth, start, end, all));
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
	@RequestMapping("co/cost/chk_before_k.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_k(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_k(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 拨出拨入是否平衡
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_before_l.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_l(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_l(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 销售拨出拨入是否平衡
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_before_m.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_m(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_m(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月出入库是否做了凭证
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_before_n.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_n(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_n(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月出入库有凭证编号但是凭证在当月不存在
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_before_o.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_o(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_o(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 出入库单据中文状态
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_before_p.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_p(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_p(type, employee, month, pmonth, start, end, all));
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
	@RequestMapping("co/cost/chk_before_q.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_q(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_q(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 是否有工单用料表的物料不存在
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_before_r.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_r(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_r(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 是否有委外工单有加工单价但是没有维护币别
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_before_s.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_s(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_s(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月委外验收、验退加工价跟委外单是否一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_before_s1.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_s1(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_s1(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月委外验收、验退单税率跟委外单是否一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_before_s2.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_s2(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_s2(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 成本表期初完工数是否正确
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_before_t.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_t(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_t(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月出入库单里料号不存在
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_before_u.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_u(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_u(type, employee, month, pmonth, start, end, all));
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
	@RequestMapping("co/cost/chk_before_v.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_v(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_v(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月采购验退单、委外验收单、委外验退单生成应付暂估/应付发票并制作了凭证的
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_before_w.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_w(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_w(type, employee, month, pmonth, start, end, all));
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
	@RequestMapping("co/cost/chk_before_x.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_x(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_x(type, employee, month, pmonth, start, end, all));
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
	@RequestMapping("co/cost/chk_before_y.action")
	@ResponseBody
	public Map<String, Object> co_chk_before_y(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_before_y(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	// *************************成本核算后检测*************************
	/**
	 * 成本表：工单工作中心是否与工单一致
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_a.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_a(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_a(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 成本表：工单类型是否与工单一致
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_b.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_b(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_b(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 成本表：产品编号是否不存在
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_c.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_c(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_c(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 成本表：工单工单数量是否与工单一致
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_d.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_d(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_d(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 成本表：本期完工数是否与实际关联的本期完工一致
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_e.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_e(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_e(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 成本表：本期报废数量、金额是否与用料月结表里一致（下面用料月结表也会跟实际单据比较）
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_f.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_f(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_f(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 成本表：本期领料金额是否等于∑（工单关联的用料月结表中本期领料金额+本期补料金额-本期退料金额），是否与工单关联的领退补单据一致
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_g.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_g(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_g(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 成本表：分摊费用按工作中心总和是否与费用表一致----按下面不同工作中心分组依次比较每一项
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_h.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_h(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_h(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 成本表：最终成本是否=总转出成本/本期完工数+加工价+上面几个分摊的单个费用
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_i.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_i(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_i(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 成本表：工单的状态是否准确
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_j.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_j(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_j(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 成本表：是否当月有发生领退补完工验收验退报废的工单都体现在成本表里
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_k.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_k(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_k(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 成本表：检查最终成本是否成功核算到完工入库、委外验收单里
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_l.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_l(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_l(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 成本表：委外加工单价跟委外单是否一致
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_m.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_m(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_m(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 月结表：检查月结表数据是否有异常
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_n.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_n(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_n(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 月结表：检查单位用量是否与工单用料表里一致
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_o.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_o(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_o(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 月结表：检查总用量是否与工单用料表里一致
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_p.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_p(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_p(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 月结表：检查本期领料数量、金额
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_q.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_q(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_q(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 月结表：检查本期补料数量、金额
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_r.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_r(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_r(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 月结表：检查本期退料数量、金额
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_s.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_s(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_s(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 月结表：检查单价cdm_price的逻辑(影响到退料核算)
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_t.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_t(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_t(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 月结表：检查本期报废数量
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_u.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_u(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_u(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 月结表：检查累计报废数量
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_v.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_v(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_v(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 月结表：检查本期成品入库数是否等于成本表本期完工数
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_w.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_w(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_w(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 月结表：检查实际单位用量，（总用量-前期转出数量）/期初未完工数
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_x.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_x(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_x(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 月结表：检查期末数量：期初数量+本期领料数量-本期退料数量+本期补料数量-本期转出数量-本期报废数量
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_y.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_y(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_y(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 月结表：期末金额：期初金额+本期领料金额-本期退料金额+本期补料金额-本期转出金额-本期报废金额
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_z.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_z(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_z(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 月结表：期末报废结余金额：本期报废金额+报废期初余额-本期报废转出金额
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_aa.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_aa(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_aa(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 检查月结表用料重复
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_ab.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_ab(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_ab(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 检查月结表料号不存在
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_ac.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_ac(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_ac(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 检查成本表领退补跟出入库差异
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_ad.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_ad(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_ad(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 有差异的工单、差异金额
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_ae.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_ae(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_ae(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 检查是否有期末完工数不等于期初完工数+本期完工数
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_af.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_af(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_af(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 检查是否有期末完工数大于工单数的
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_ag.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_ag(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_ag(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 检查加工价
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_ah.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_ah(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_ah(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 检查是否有最终成本负数的情况
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_ai.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_ai(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_ai(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 检查材料成本负数的情况
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_aj.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_aj(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_aj(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 检查标准工时是否与物料里一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_ak.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_ak(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_ak(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 检查总工时
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_al.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_al(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_al(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 检查存月结表有期末金额没有期末数量的情况
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_am.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_am(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_am(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 月结表：转出数量是否正确
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_an.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_an(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_an(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 成本表：本期报废转出成本与月结表本期报废转出金额是否一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/cost/chk_after_ao.action")
	@ResponseBody
	public Map<String, Object> co_chk_after_ao(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_after_ao(type, employee, month, pmonth, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	// *************************存货核算检测*************************

	/**
	 * 库存是否已经冻结
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_a.action")
	@ResponseBody
	public Map<String, Object> co_chk_a(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_a(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当期是否有未过账的出入库单据
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_b.action")
	@ResponseBody
	public Map<String, Object> co_chk_b(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_b(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月出入库单据料号是否存在
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_c.action")
	@ResponseBody
	public Map<String, Object> co_chk_c(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_c(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	// *************************存货核算按物料*************************

	/**
	 * 生产领料单、生产补料单、生产退料单、完工入库单、拆件入库单存货金额是否与总账科目一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_d.action")
	@ResponseBody
	public Map<String, Object> co_chk_d(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_d(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 委外领料单、委外补料单、委外退料单、委外验收单、委外验退单存货金额是否与总账科目一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_e.action")
	@ResponseBody
	public Map<String, Object> co_chk_e(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_e(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 其它出/入库单存货金额是否与总账科目一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_f.action")
	@ResponseBody
	public Map<String, Object> co_chk_f(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_f(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 盘盈、盘亏、报废单与相应凭证存货科目是否与总账科目一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_g.action")
	@ResponseBody
	public Map<String, Object> co_chk_g(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_g(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 拨入拨出单，销售拨入拨出单存货金额是否与总账科目一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_h.action")
	@ResponseBody
	public Map<String, Object> co_chk_h(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_h(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 采购验收单、采购验退单存货金额是否与总账科目一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_i.action")
	@ResponseBody
	public Map<String, Object> co_chk_i(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_i(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 发货单、退货单存货金额是否与总账科目一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_j.action")
	@ResponseBody
	public Map<String, Object> co_chk_j(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_j(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 期末汇总表【期末结存金额】与总账对应存货科目余额是否一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_k.action")
	@ResponseBody
	public Map<String, Object> co_chk_k(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_k(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 存货月结表：期初数量是否与上月期末数量一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_l.action")
	@ResponseBody
	public Map<String, Object> co_chk_l(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_l(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 存货月结表：期初金额是否与上月期末金额一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_m.action")
	@ResponseBody
	public Map<String, Object> co_chk_m(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_m(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 存货月结表：物料期末数量是否有数量无金额的情况
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_n.action")
	@ResponseBody
	public Map<String, Object> co_chk_n(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_n(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 存货月结表：物料期末金额是否有金额无数量的情况
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_o.action")
	@ResponseBody
	public Map<String, Object> co_chk_o(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_o(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 存货月结表：物料是否负数金额、负数数量的情况
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_p.action")
	@ResponseBody
	public Map<String, Object> co_chk_p(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_p(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 存货核算：所有的出入库单批次单价是否核算进去
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_q.action")
	@ResponseBody
	public Map<String, Object> co_chk_q(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_q(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 所有的出入库单据是否做了凭证
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_r.action")
	@ResponseBody
	public Map<String, Object> co_chk_r(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_r(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 是否存在无来源单据总账制作存货科目凭证及来源单据非出入库单、期初调整单、应付发票、应付暂估、发出商品、主营业务成本存货科目凭证
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_s.action")
	@ResponseBody
	public Map<String, Object> co_chk_s(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_s(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 是否存在非无值仓单价为负数的物料
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_t.action")
	@ResponseBody
	public Map<String, Object> co_chk_t(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_t(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 是否存在非无值仓单价为0数的物料
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_t1.action")
	@ResponseBody
	public Map<String, Object> co_chk_t1(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_t1(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 无值仓是否存在单价
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_u.action")
	@ResponseBody
	public Map<String, Object> co_chk_u(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_u(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 是否存在上月出库当月入库的情况
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_v.action")
	@ResponseBody
	public Map<String, Object> co_chk_v(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_v(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 所有出入库单据会计期间是否和凭证一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @param pmonth
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping("co/inventory/chk_w.action")
	@ResponseBody
	public Map<String, Object> co_chk_w(HttpSession session, String type, Integer month, Integer pmonth, String start, String end,
			Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_w(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 成本期间 与 库存期间一致
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/inventory/chk_x.action")
	@ResponseBody
	public Map<String, Object> co_chk_x(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", costAccountService.co_chk_x(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}
}
