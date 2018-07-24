package com.uas.erp.controller.fa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.uas.erp.core.ExcelUtil;
import com.uas.erp.model.BillError;
import com.uas.erp.model.Employee;
import com.uas.erp.service.fa.CheckAccountService;

@SuppressWarnings("deprecation")
@Controller
public class CheckAccountController {

	@Autowired
	private CheckAccountService checkAccountService;

	/**
	 * 获取当前期间、起始和结束时间
	 * 
	 * @param session
	 * @param type
	 *            MONTH-A
	 * @param votype
	 *            AR
	 * @return {HashMap} data
	 */
	@RequestMapping("fa/getMonth.action")
	@ResponseBody
	public Map<String, Object> getYearMonth(HttpSession session, String type, String votype) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", checkAccountService.getYearMonth(type, votype));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 查看检测项详细结果
	 * 
	 * @param session
	 * @param type
	 * @return
	 */
	@RequestMapping("fa/getBillError.action")
	@ResponseBody
	public Map<String, Object> getBillError(HttpSession session, String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", checkAccountService.getBillErrors(type, false));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 导出全部检测结果<br>
	 * <i>一般检测只记录前一百条错误项。当点击全部导出按钮后，先检测并记录全部错误项，然后导出</i>
	 * 
	 * @param session
	 * @param type
	 * @return
	 */
	@RequestMapping("fa/exportBillError.xls")
	public ModelAndView exportBillError(HttpSession session, String type, String title) {
		List<BillError> errors = checkAccountService.getBillErrors(type, true);
		Employee employee = (Employee) session.getAttribute("employee");
		List<Map<Object, Object>> columns = new ArrayList<Map<Object, Object>>();
		List<Map<Object, Object>> datas = new ArrayList<Map<Object, Object>>();
		Map<Object, Object> map1 = new HashMap<Object, Object>();
		map1.put("dataIndex", "be_class");
		map1.put("text", "单据");
		map1.put("width", "120");
		columns.add(map1);
		Map<Object, Object> map2 = new HashMap<Object, Object>();
		map2.put("dataIndex", "be_code");
		map2.put("text", "编号");
		map2.put("width", "150");
		columns.add(map2);
		Map<Object, Object> map3 = new HashMap<Object, Object>();
		map3.put("dataIndex", "be_remark");
		map3.put("text", "备注");
		map3.put("width", "300");
		columns.add(map3);
		for (BillError error : errors) {
			Map<Object, Object> map = new HashMap<Object, Object>();
			map.put("be_class", error.getBe_class());
			map.put("be_code", error.getBe_code());
			map.put("be_remark", error.getBe_remark());
			datas.add(map);
		}
		return new ModelAndView(new ExcelUtil(columns, datas, title, employee).getView());
	}

	// *************************应收期末对账检查************************
	/**
	 * 收帐款期间 与 总账期间一致
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/ars/chk_a.action")
	@ResponseBody
	public Map<String, Object> ar_chk_a(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ar_chk_a(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月的 出货单、退货单、发票、其它应收单、发出商品、收退款单、预收单、结算冲帐单已过账
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/ars/chk_b.action")
	@ResponseBody
	public Map<String, Object> ar_chk_b(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ar_chk_b(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月的 发票、其它应收单、发出商品、收退款单、预收单、结算单已制作凭证
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/ars/chk_c.action")
	@ResponseBody
	public Map<String, Object> ar_chk_c(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ar_chk_c(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月的 出货单、退货单已全部转开票或发出商品
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/ars/chk_d.action")
	@ResponseBody
	public Map<String, Object> ar_chk_d(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ar_chk_d(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月的发票的销售单价、成本单价与来源发出商品/出货单、销售退货单一致
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/ars/chk_e.action")
	@ResponseBody
	public Map<String, Object> ar_chk_e(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ar_chk_e(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月凭证中，应收款科目有手工录入的(来源为空的)
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/ars/chk_f.action")
	@ResponseBody
	public Map<String, Object> ar_chk_f(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ar_chk_f(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月的 总的开票数量与 出货单、退货单 的开票数量一致
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/ars/chk_g.action")
	@ResponseBody
	public Map<String, Object> ar_chk_g(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ar_chk_g(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月的 总的发出商品数量是否与 出货单、退货单 的发出商品数量一致
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/ars/chk_h.action")
	@ResponseBody
	public Map<String, Object> ar_chk_h(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ar_chk_h(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月开票数据中 涉及发出商品的 总的开票数量与 发出商品的开票数量一致
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/ars/chk_i.action")
	@ResponseBody
	public Map<String, Object> ar_chk_i(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ar_chk_i(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月预收款、预收退款与应收总账里本期预收的一致
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/ars/chk_j.action")
	@ResponseBody
	public Map<String, Object> ar_chk_j(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ar_chk_j(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月预收冲账与应收总账里本期预收冲账的一致
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/ars/chk_k.action")
	@ResponseBody
	public Map<String, Object> ar_chk_k(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ar_chk_k(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月发出商品(成本价)与应收总账里本期发出商品cm_gsnowamount的一致
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/ars/chk_l.action")
	@ResponseBody
	public Map<String, Object> ar_chk_l(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ar_chk_l(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月发出商品(销售价)与应收总账里本期发出商品cm_gsnowamounts的一致
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/ars/chk_m.action")
	@ResponseBody
	public Map<String, Object> ar_chk_m(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ar_chk_m(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月开票数据中 涉及发出商品的（成本价）与应收总账里本期发出商品转开票cm_gsinvoamount的一致
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/ars/chk_n.action")
	@ResponseBody
	public Map<String, Object> ar_chk_n(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ar_chk_n(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月开票数据中 涉及发出商品的（销售价）与应收总账里本期发出商品转开票cm_gsinvoamounts的一致
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/ars/chk_o.action")
	@ResponseBody
	public Map<String, Object> ar_chk_o(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ar_chk_o(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月的 发票、其它应收单 的总额与 应收总账本期应收一致
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/ars/chk_p.action")
	@ResponseBody
	public Map<String, Object> ar_chk_p(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ar_chk_p(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月的 收款单、退款单、结算单 的总额与 应收总账的本期收款一致
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/ars/chk_q.action")
	@ResponseBody
	public Map<String, Object> ar_chk_q(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ar_chk_q(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月的 发票销售总额 与 主营业务收入 的贷方一致
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/ars/chk_r.action")
	@ResponseBody
	public Map<String, Object> ar_chk_r(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ar_chk_r(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月的 发票成本总额 与 主营业务成本 的借方发生一致
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/ars/chk_s.action")
	@ResponseBody
	public Map<String, Object> ar_chk_s(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ar_chk_s(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月应收发票中来源的发出商品日期小于当月
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/ars/chk_t.action")
	@ResponseBody
	public Map<String, Object> ar_chk_t(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ar_chk_t(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月应收发票中来源的出入库日期不存在非当月的
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/ars/chk_u.action")
	@ResponseBody
	public Map<String, Object> ar_chk_u(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ar_chk_u(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月应收账款科目余额与应收总账应收期末余额一致
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/ars/chk_v.action")
	@ResponseBody
	public Map<String, Object> ar_chk_v(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ar_chk_v(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月预收账款科目余额与应收总账预收期末余额一致
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/ars/chk_w.action")
	@ResponseBody
	public Map<String, Object> ar_chk_w(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ar_chk_w(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月发出商品科目余额与应收总账发出商品期末余额(成本金额)一致
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/ars/chk_x.action")
	@ResponseBody
	public Map<String, Object> ar_chk_x(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ar_chk_x(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月的 预收和应收同时有余额
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/ars/chk_y.action")
	@ResponseBody
	public Map<String, Object> ar_chk_y(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ar_chk_y(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	// *************************应付期末对账检查*************************
	/**
	 * 付帐款期间 与 总账期间一致
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/arp/chk_a.action")
	@ResponseBody
	public Map<String, Object> ap_chk_a(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ap_chk_a(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月的 验收单、验退单、发票、其它应付单、暂估、付退款单、预付单、结算冲帐单已过账
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/arp/chk_b.action")
	@ResponseBody
	public Map<String, Object> ap_chk_b(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ap_chk_b(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月的 发票、其它应付单、暂估、付退款单、预付单、结算单已制作凭证
	 * 
	 * @param session
	 * @param month
	 *            应付期间
	 * @return
	 */
	@RequestMapping("fa/arp/chk_c.action")
	@ResponseBody
	public Map<String, Object> ap_chk_c(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ap_chk_c(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月的 验收单、验退单已全部转开票或暂估
	 * 
	 * @param session
	 * @param month
	 *            应付期间
	 * @return
	 */
	@RequestMapping("fa/arp/chk_d.action")
	@ResponseBody
	public Map<String, Object> ap_chk_d(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ap_chk_d(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月的 发票、暂估(全部未开票的) 的采购单价、成本单价与 验收单、验退单 的一致
	 * 
	 * @param session
	 * @param month
	 *            应付期间
	 * @return
	 */
	@RequestMapping("fa/arp/chk_e.action")
	@ResponseBody
	public Map<String, Object> ap_chk_e(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ap_chk_e(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月凭证中，应付款科目有手工录入的(来源为空的)
	 * 
	 * @param session
	 * @param month
	 *            应付期间
	 * @return
	 */
	@RequestMapping("fa/arp/chk_f.action")
	@ResponseBody
	public Map<String, Object> ap_chk_f(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ap_chk_f(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月的 总的开票数量与 验收单、验退单 的开票数量一致
	 * 
	 * @param session
	 * @param month
	 *            应付期间
	 * @return
	 */
	@RequestMapping("fa/arp/chk_g.action")
	@ResponseBody
	public Map<String, Object> ap_chk_g(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ap_chk_g(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月的 总的暂估数量是否与 验收单、验退单 的暂估数量一致
	 * 
	 * @param session
	 * @param month
	 *            应付期间
	 * @return
	 */
	@RequestMapping("fa/arp/chk_h.action")
	@ResponseBody
	public Map<String, Object> ap_chk_h(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ap_chk_h(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月开票数据中 涉及暂估的 总的开票数量与 暂估的开票数量一致
	 * 
	 * @param session
	 * @param month
	 *            应付期间
	 * @return
	 */
	@RequestMapping("fa/arp/chk_i.action")
	@ResponseBody
	public Map<String, Object> ap_chk_i(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ap_chk_i(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月预付款、预付退款与应付总账里本期预付的一致
	 * 
	 * @param session
	 * @param month
	 *            应付期间
	 * @return
	 */
	@RequestMapping("fa/arp/chk_j.action")
	@ResponseBody
	public Map<String, Object> ap_chk_j(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ap_chk_j(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月预付冲账与应付总账里本期预付冲账的一致
	 * 
	 * @param session
	 * @param month
	 *            应付期间
	 * @return
	 */
	@RequestMapping("fa/arp/chk_k.action")
	@ResponseBody
	public Map<String, Object> ap_chk_k(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ap_chk_k(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月暂估与应付总账里本期应付暂估增加一致
	 * 
	 * @param session
	 * @param month
	 *            应付期间
	 * @return
	 */
	@RequestMapping("fa/arp/chk_l.action")
	@ResponseBody
	public Map<String, Object> ap_chk_l(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ap_chk_l(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月暂估(采购价)与应付总账里本期暂估vm_esnowamounts的一致
	 * 
	 * @param session
	 * @param month
	 *            应付期间
	 * @return
	 */
	@RequestMapping("fa/arp/chk_m.action")
	@ResponseBody
	public Map<String, Object> ap_chk_m(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ap_chk_m(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月开票数据中涉及暂估的与应付总账里本期应付暂估减少一致
	 * 
	 * @param session
	 * @param month
	 *            应付期间
	 * @return
	 */
	@RequestMapping("fa/arp/chk_n.action")
	@ResponseBody
	public Map<String, Object> ap_chk_n(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ap_chk_n(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月开票数据中 涉及暂估的（采购价）与应付总账里本期暂估转开票vm_esinvoamounts的一致
	 * 
	 * @param session
	 * @param month
	 *            应付期间
	 * @return
	 */
	@RequestMapping("fa/arp/chk_o.action")
	@ResponseBody
	public Map<String, Object> ap_chk_o(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ap_chk_o(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月的 发票、其它应付单 的总额与 应付总账本期应付一致
	 * 
	 * @param session
	 * @param month
	 *            应付期间
	 * @return
	 */
	@RequestMapping("fa/arp/chk_p.action")
	@ResponseBody
	public Map<String, Object> ap_chk_p(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ap_chk_p(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月的 付款单、退款单、结算单 的总额与 应付总账的本期付款一致
	 * 
	 * @param session
	 * @param month
	 *            应付期间
	 * @return
	 */
	@RequestMapping("fa/arp/chk_q.action")
	@ResponseBody
	public Map<String, Object> ap_chk_q(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ap_chk_q(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月应付账款科目余额与应付总账应付期末余额是否一致
	 * 
	 * @param session
	 * @param month
	 *            应付期间
	 * @return
	 */
	@RequestMapping("fa/arp/chk_r.action")
	@ResponseBody
	public Map<String, Object> ap_chk_r(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ap_chk_r(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月预付账款科目余额与预付总账预付期末余额是否一致
	 * 
	 * @param session
	 * @param month
	 *            应付期间
	 * @return
	 */
	@RequestMapping("fa/arp/chk_s.action")
	@ResponseBody
	public Map<String, Object> ap_chk_s(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ap_chk_s(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月应付暂估科目余额与应付总账应付暂估余额(采购价除税)是否一致
	 * 
	 * @param session
	 * @param month
	 *            应付期间
	 * @return
	 */
	@RequestMapping("fa/arp/chk_t.action")
	@ResponseBody
	public Map<String, Object> ap_chk_t(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ap_chk_t(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月的 预付和应付同时有余额
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/arp/chk_u.action")
	@ResponseBody
	public Map<String, Object> ap_chk_u(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.ap_chk_u(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	// *************************固定资产对账检查************************

	/**
	 * 卡片资料是否完整
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/fix/chk_a.action")
	@ResponseBody
	public Map<String, Object> fix_chk_a(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.fix_chk_a(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 是否计提折旧
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/fix/chk_b.action")
	@ResponseBody
	public Map<String, Object> fix_chk_b(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.fix_chk_b(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 折旧单,资产增加、减少单是否过账
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/fix/chk_c.action")
	@ResponseBody
	public Map<String, Object> fix_chk_c(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.fix_chk_c(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 卡片变更单是否全部已审核
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/fix/chk_d.action")
	@ResponseBody
	public Map<String, Object> fix_chk_d(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.fix_chk_d(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月的资产增加单、资产减少单、折旧单是否过账
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/fix/chk_e.action")
	@ResponseBody
	public Map<String, Object> fix_chk_e(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.fix_chk_e(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月新增的已审核卡片是否已经生成凭证
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/fix/chk_f.action")
	@ResponseBody
	public Map<String, Object> fix_chk_f(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.fix_chk_f(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月的折旧单、资产增加单、资产减少单是否已经生成凭证
	 * 
	 * @param session
	 * @param month
	 * 
	 * @return
	 */
	@RequestMapping("fa/fix/chk_g.action")
	@ResponseBody
	public Map<String, Object> fix_chk_g(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.fix_chk_g(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当前期间固定资产所有卡片的原值金额合计与总账固定资产科目的本期余额是否一致
	 * 
	 * @param session
	 * @param month
	 * 
	 * @return
	 */
	@RequestMapping("fa/fix/chk_h.action")
	@ResponseBody
	public Map<String, Object> fix_chk_h(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.fix_chk_h(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当前期间固定资产所有卡片的累计折旧金额合计与总账累计折旧科目的本期余额是否一致
	 * 
	 * @param session
	 * @param month
	 * 
	 * @return
	 */
	@RequestMapping("fa/fix/chk_i.action")
	@ResponseBody
	public Map<String, Object> fix_chk_i(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.fix_chk_i(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月满足折旧条件的卡片是否都计提折旧
	 * 
	 * @param session
	 * @param month
	 * 
	 * @return
	 */
	@RequestMapping("fa/fix/chk_j.action")
	@ResponseBody
	public Map<String, Object> fix_chk_j(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.fix_chk_j(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 是否有手工录入固定资产科目的凭证
	 * 
	 * @param session
	 * @param month
	 * 
	 * @return
	 */
	@RequestMapping("fa/fix/chk_k.action")
	@ResponseBody
	public Map<String, Object> fix_chk_k(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.fix_chk_k(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 是否有手工录入累计折旧科目的凭证
	 * 
	 * @param session
	 * @param month
	 * 
	 * @return
	 */
	@RequestMapping("fa/fix/chk_l.action")
	@ResponseBody
	public Map<String, Object> fix_chk_l(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.fix_chk_l(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	// *************************银行现金对账检查************************

	/**
	 * 当月银行现金单据是否全部记账
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/gs/chk_a.action")
	@ResponseBody
	public Map<String, Object> gs_chk_a(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gs_chk_a(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月银行现金单据是否全部做了凭证
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/gs/chk_b.action")
	@ResponseBody
	public Map<String, Object> gs_chk_b(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gs_chk_b(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 银行现金余额是否出现负数
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/gs/chk_c.action")
	@ResponseBody
	public Map<String, Object> gs_chk_c(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gs_chk_c(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 票据资金系统期间与总账期间是否一致
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/gs/chk_d.action")
	@ResponseBody
	public Map<String, Object> gs_chk_d(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gs_chk_d(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 预收款、预收退款类型的银行登记关联的预收款、预收退款单是否存在、是否已记账5
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/gs/chk_e.action")
	@ResponseBody
	public Map<String, Object> gs_chk_e(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gs_chk_e(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应收款、应收退款类型的银行登记关联的收款单、收款退款单是否存在、是否已记账
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/gs/chk_f.action")
	@ResponseBody
	public Map<String, Object> gs_chk_f(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gs_chk_f(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 预付款、预付退款类型的银行登记关联的预付款、预付退款单是否存在、是否已记账
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/gs/chk_g.action")
	@ResponseBody
	public Map<String, Object> gs_chk_g(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gs_chk_g(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应付款、应付退款类型的银行登记关联的付款单、付款退款单是否存在、是否已记账
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/gs/chk_h.action")
	@ResponseBody
	public Map<String, Object> gs_chk_h(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gs_chk_h(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转存类型的银行登记是否平衡
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/gs/chk_i.action")
	@ResponseBody
	public Map<String, Object> gs_chk_i(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gs_chk_i(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月银行现金类科目余额与银行存款总账查询界面期末余额是否一致
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/gs/chk_j.action")
	@ResponseBody
	public Map<String, Object> gs_chk_j(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gs_chk_j(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 所有应付票据是否已审核
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/gs/chk_k.action")
	@ResponseBody
	public Map<String, Object> gs_chk_k(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gs_chk_k(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 所有应付票据异动单是否已过账
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/gs/chk_l.action")
	@ResponseBody
	public Map<String, Object> gs_chk_l(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gs_chk_l(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 所有应收票据是否已审核
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/gs/chk_m.action")
	@ResponseBody
	public Map<String, Object> gs_chk_m(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gs_chk_m(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 所有应收票据异动单是否已过账
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/gs/chk_n.action")
	@ResponseBody
	public Map<String, Object> gs_chk_n(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gs_chk_n(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应收票据是否有关联的收款单或预收单，是否已过账
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/gs/chk_o.action")
	@ResponseBody
	public Map<String, Object> gs_chk_o(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gs_chk_o(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应付票据是否有关联的付款单或预付单，是否已过账
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/gs/chk_p.action")
	@ResponseBody
	public Map<String, Object> gs_chk_p(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gs_chk_p(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应收票据异动类型为收款、贴现的，是否有关联的银行登记，是否已记账
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/gs/chk_q.action")
	@ResponseBody
	public Map<String, Object> gs_chk_q(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gs_chk_q(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应收票据异动类型为背书转让的，是否有关联的付款单或预付单，是否已过账
	 * 
	 * @param session
	 * @param month
	 * 
	 * @return
	 */
	@RequestMapping("fa/gs/chk_r.action")
	@ResponseBody
	public Map<String, Object> gs_chk_r(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gs_chk_r(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应付票据异动类型为兑现的，是否有关联的银行登记，是否已记账
	 * 
	 * @param session
	 * @param month
	 * 
	 * @return
	 */
	@RequestMapping("fa/gs/chk_s.action")
	@ResponseBody
	public Map<String, Object> gs_chk_s(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gs_chk_s(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月应付票据科目余额与应付票据票面余额是否一致
	 * 
	 * @param session
	 * @param month
	 * 
	 * @return
	 */
	@RequestMapping("fa/gs/chk_t.action")
	@ResponseBody
	public Map<String, Object> gs_chk_t(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gs_chk_t(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月应收票据科目余额与应收票据票面余额是否一致
	 * 
	 * @param session
	 * @param month
	 * 
	 * @return
	 */
	@RequestMapping("fa/gs/chk_u.action")
	@ResponseBody
	public Map<String, Object> gs_chk_u(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gs_chk_u(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应付票据异动类型为退票、作废的,是否有关联的应付退款单、预付退款单,是否已记账
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/gs/chk_v.action")
	@ResponseBody
	public Map<String, Object> gs_chk_v(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gs_chk_v(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应收票据异动类型为退票、作废的，是否有关联的应收退款单、预收退款单,是否已记账
	 * 
	 * @param session
	 * @param month
	 *            应收期间
	 * @return
	 */
	@RequestMapping("fa/gs/chk_w.action")
	@ResponseBody
	public Map<String, Object> gs_chk_w(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gs_chk_w(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	// *************************总账期末对账检查************************
	/**
	 * 总账系统当月所有凭证是否全部记账
	 * 
	 * @param session
	 * @param month
	 *            总账期间
	 * @return
	 */
	@RequestMapping("fa/gla/chk_a.action")
	@ResponseBody
	public Map<String, Object> gla_chk_a(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gla_chk_a(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 总账系统当月凭证是否有重号的
	 * 
	 * @param session
	 * @param month
	 *            总账期间
	 * @return
	 */
	@RequestMapping("fa/gla/chk_b.action")
	@ResponseBody
	public Map<String, Object> gla_chk_b(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gla_chk_b(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 总账系统当月凭证是否有断号的
	 * 
	 * @param session
	 * @param month
	 *            总账期间
	 * @return
	 */
	@RequestMapping("fa/gla/chk_c.action")
	@ResponseBody
	public Map<String, Object> gla_chk_c(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gla_chk_c(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 是否有摘要为结转制造费用的凭证
	 * 
	 * @param session
	 * @param month
	 *            总账期间
	 * @return
	 */
	@RequestMapping("fa/gla/chk_d.action")
	@ResponseBody
	public Map<String, Object> gla_chk_d(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gla_chk_d(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 是否有摘要为汇兑损益的凭证
	 * 
	 * @param session
	 * @param month
	 *            总账期间
	 * @return
	 */
	@RequestMapping("fa/gla/chk_e.action")
	@ResponseBody
	public Map<String, Object> gla_chk_e(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gla_chk_e(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 是否有摘要为结转损益的凭证
	 * 
	 * @param session
	 * @param month
	 *            总账期间
	 * @return
	 */
	@RequestMapping("fa/gla/chk_f.action")
	@ResponseBody
	public Map<String, Object> gla_chk_f(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gla_chk_f(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 科目类型为损益类的科目，当月余额是否全部为0
	 * 
	 * @param session
	 * @param month
	 *            总账期间
	 * @return
	 */
	@RequestMapping("fa/gla/chk_g.action")
	@ResponseBody
	public Map<String, Object> gla_chk_g(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gla_chk_g(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 科目余额表中科目余额的方向与科目属性中对应的科目余额方向是否一致
	 * 
	 * @param session
	 * @param month
	 *            总账期间
	 * @return
	 */
	@RequestMapping("fa/gla/chk_h.action")
	@ResponseBody
	public Map<String, Object> gla_chk_h(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gla_chk_h(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 当月损益类科目/制造费用科目在凭证中的借贷方向是否和科目性质一致（除结转损益、结转制造费用凭证）
	 * 
	 * @param session
	 * @param month
	 *            总账期间
	 * @return
	 */
	@RequestMapping("fa/gla/chk_i.action")
	@ResponseBody
	public Map<String, Object> gla_chk_i(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gla_chk_i(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 总账系统当月子模块是否已全部结账（包括应收、应付、固定资产、票据资金、库存、成本）
	 * 
	 * @param session
	 * @param month
	 *            总账期间
	 * @return
	 */
	@RequestMapping("fa/gla/chk_j.action")
	@ResponseBody
	public Map<String, Object> gla_chk_j(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.gla_chk_j(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	// *************************成本期末对账检查************************
	/**
	 * 成本表上直接人工的金额与直接人工制造费用维护界面是否一致
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_a.action")
	@ResponseBody
	public Map<String, Object> cost_chk_a(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.cost_chk_a(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 成本表上制造费用的金额与直接人工制造费用维护界面是否一致
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_b.action")
	@ResponseBody
	public Map<String, Object> cost_chk_b(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.cost_chk_b(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 总账生产成本科目余额与成本表上工单类型是制造单的期末结余金额+期末报废结余金额合计是否一致
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_c.action")
	@ResponseBody
	public Map<String, Object> cost_chk_c(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.cost_chk_c(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 总账委托加工物资科目余额与成本表上工单类型是委外加工单的期末结余金额+期末报废结余金额合计是否一致
	 * 
	 * @param session
	 * @param month
	 *            成本期间
	 * @return
	 */
	@RequestMapping("co/cost/chk_d.action")
	@ResponseBody
	public Map<String, Object> cost_chk_d(HttpSession session, String type, Integer month, String start, String end, Boolean all) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("ok", checkAccountService.cost_chk_d(type, employee, month, start, end, all));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取对应模块的检测项
	 * 
	 * @param module
	 *            所属模块
	 * @return
	 */
	@RequestMapping("fa/getCheckItems.action")
	@ResponseBody
	public Map<String, Object> getCheckItems(String module,Boolean isCheck) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", checkAccountService.getCheckItems(module,isCheck));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存一条检测项
	 * 
	 * @param session
	 * @param CheckItem
	 *            当前检测项数据
	 * @return
	 */
	@RequestMapping("fa/saveCheckItem.action")
	@ResponseBody
	public Map<String, Object> saveCheckItem(HttpSession session,String CheckItem) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("data",checkAccountService.saveCheckItem(employee,CheckItem));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存所有检测项
	 * 
	 * @param session
	 * @param CheckItems
	 *            检测项明细表数据
	 * @return
	 */
	@RequestMapping("fa/saveCheckItems.action")
	@ResponseBody
	public Map<String, Object> saveCheckItems(HttpSession session,String CheckItems) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		checkAccountService.saveCheckItems(employee,CheckItems);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取检测项参数设置
	 * 
	 * @param checkcode
	 *            检测号
	 * @return
	 */
	@RequestMapping("fa/getParamSets.action")
	@ResponseBody
	public Map<String, Object> getParamSets(String checkcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", checkAccountService.getParamSets(checkcode));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存检测项参数设置
	 * 
	 * @param checkcode
	 * 			检测号
	 * @param ParamSets   
	 * 			检测项参数设置明细表数据           
	 * @return
	 */
	@RequestMapping("fa/saveParamSets.action")
	@ResponseBody
	public Map<String, Object> saveParamSets(String checkcode,String ParamSets) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkAccountService.saveParamSets(checkcode,ParamSets);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取错误输出设置
	 * 
	 * @param checkcode
	 *            检测号
	 * @return
	 */
	@RequestMapping("fa/getErrorSets.action")
	@ResponseBody
	public Map<String, Object> getErrorSets(String checkcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", checkAccountService.getErrorSets(checkcode));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存错误输出设置
	 *
	 * @param checkcode
	 * 			检测号 
	 * @param ErrorSets
	 *          检测项错误输出设置明细表数据
	 * @return
	 */
	@RequestMapping("fa/saveErrorSets.action")
	@ResponseBody
	public Map<String, Object> saveErrorSets(String checkcode,String ErrorSets) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkAccountService.saveErrorSets(checkcode,ErrorSets);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除参数设置、错误输出设置项
	 *
	 * @param checkcode
	 * 			检测号 
	 * @param key
	 *          参数名/字段名
	 * @param isParamSet
	 *          是否参数设置
	 * @return
	 */
	@RequestMapping("fa/deleteSet.action")
	@ResponseBody
	public Map<String, Object> deleteSet(String checkcode,String key,Boolean isParamSet) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkAccountService.deleteSet(checkcode,key,isParamSet);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取检测项启用状态
	 *
	 * @param module
	 * 			模块 
	 * @param billoutmode
	 *          开票模式
	 * @param checked
	 *          当前状态
	 * @return
	 */
	@RequestMapping("fa/getCheckItemStatus.action")
	@ResponseBody
	public Map<String, Object> getCheckItemStatus(String module,String billoutmode,Boolean checked) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkAccountService.getCheckItemStatus(module,billoutmode,checked);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 期末结账检查
	 * 
	 * @param module
	 *           模块
	 * @return
	 */
	@RequestMapping("fa/checkAccounts.action")
	@ResponseBody
	public Map<String, Object> checkAccounts(String module,String yearmonth) {
		if (yearmonth==null) {
			String mod = module.substring(0, 2);
			Map<String, Object> data = checkAccountService.getYearMonth(null, mod);
			yearmonth = data.get("PD_DETNO").toString();
		}
		checkAccountService.refreshEndData(yearmonth,module);
		Map<String, Object> modelMap = checkAccountService.checkAccounts(module,yearmonth);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取错误详细信息
	 * 
	 * @param checkcode
	 *           检测项编号
	 * @return
	 */
	@RequestMapping("fa/getShowDetailGrid.action")
	@ResponseBody
	public Map<String, Object> getShowDetailGrid(String checkcode) {
		Map<String, Object> modelMap = checkAccountService.getShowDetailGrid(checkcode);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取错误详细信息
	 * 
	 * @param month
	 *           期间
	 * @param module
	 *           模块
	 * @return
	 */
	@RequestMapping("fa/refreshEndData.action")
	@ResponseBody
	public Map<String, Object> refreshEndData(String month,String module) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkAccountService.refreshEndData(month,module);
		modelMap.put("success", true);
		return modelMap;
	}
}
