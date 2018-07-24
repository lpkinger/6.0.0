package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.ProdAccountService;

@Controller
public class ProdAccountController {

	@Autowired
	private ProdAccountService prodAccountService;

	/************** 存货核算二 **************/

	/**
	 * 是否有未制作凭证的单据
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @return
	 */
	@RequestMapping("scm/reserve/chk_a.action")
	@ResponseBody
	public Map<String, Object> chk_a(String type, Integer month) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", prodAccountService.scm_chk_a(type, month));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 是否有出入库单凭证号异常的单据
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @return
	 */
	@RequestMapping("scm/reserve/chk_b.action")
	@ResponseBody
	public Map<String, Object> chk_b(String type, Integer month) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", prodAccountService.scm_chk_b(type, month));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 拨出拨入是否平衡
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @return
	 */
	@RequestMapping("scm/reserve/chk_c.action")
	@ResponseBody
	public Map<String, Object> chk_c(String type, Integer month) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", prodAccountService.scm_chk_c(type, month));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 销售拨出拨入是否平衡
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @return
	 */
	@RequestMapping("scm/reserve/chk_d.action")
	@ResponseBody
	public Map<String, Object> chk_d(String type, Integer month) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", prodAccountService.scm_chk_d(type, month));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应付发票中成本单价跟出入库成本单价是否一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @return
	 */
	@RequestMapping("scm/reserve/chk_e.action")
	@ResponseBody
	public Map<String, Object> chk_e(String type, Integer month) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", prodAccountService.scm_chk_e(type, month));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 暂估单成本单价跟出入库单成本单价是否一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @return
	 */
	@RequestMapping("scm/reserve/chk_f.action")
	@ResponseBody
	public Map<String, Object> chk_f(String type, Integer month) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", prodAccountService.scm_chk_f(type, month));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 验收单据数量与当月开票+暂估是否一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @return
	 */
	@RequestMapping("scm/reserve/chk_g.action")
	@ResponseBody
	public Map<String, Object> chk_g(String type, Integer month) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", prodAccountService.scm_chk_g(type, month));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应收发票成本单价跟出入库单成本单价是否一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @return
	 */
	@RequestMapping("scm/reserve/chk_h.action")
	@ResponseBody
	public Map<String, Object> chk_h(String type, Integer month) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", prodAccountService.scm_chk_h(type, month));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 发出商品成本价跟出入库单成本单价是否一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @return
	 */
	@RequestMapping("scm/reserve/chk_i.action")
	@ResponseBody
	public Map<String, Object> chk_i(String type, Integer month) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", prodAccountService.scm_chk_i(type, month));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 出货单据数量与当月开票+发出商品是否一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @return
	 */
	@RequestMapping("scm/reserve/chk_j.action")
	@ResponseBody
	public Map<String, Object> chk_j(String type, Integer month) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", prodAccountService.scm_chk_j(type, month));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * -检查其它出入库单据的单据类型+小类+部门是否设置了对方科目
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @return
	 */
	@RequestMapping("scm/reserve/chk_k.action")
	@ResponseBody
	public Map<String, Object> chk_k(String type, Integer month) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", prodAccountService.scm_chk_k(type, month));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 检查其它出入库基础科目设置是否有重复的-----提出出重复项目的pc_class\pc_type\pc_departmentcode
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @return
	 */
	@RequestMapping("scm/reserve/chk_l.action")
	@ResponseBody
	public Map<String, Object> chk_l(String type, Integer month) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", prodAccountService.scm_chk_l(type, month));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 检查是否有出入库单据出、入数量都不为0
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @return
	 */
	@RequestMapping("scm/reserve/chk_m.action")
	@ResponseBody
	public Map<String, Object> chk_m(String type, Integer month) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", prodAccountService.scm_chk_m(type, month));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 检查是否有料号不存在
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @return
	 */
	@RequestMapping("scm/reserve/chk_n.action")
	@ResponseBody
	public Map<String, Object> chk_n(String type, Integer month) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", prodAccountService.scm_chk_n(type, month));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 检查是否有物料的存货科目没有设置
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @return
	 */
	@RequestMapping("scm/reserve/chk_o.action")
	@ResponseBody
	public Map<String, Object> chk_o(String type, Integer month) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", prodAccountService.scm_chk_o(type, month));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 当期是否有未过账的出入库单据
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @return
	 */
	@RequestMapping("scm/reserve/chk_p.action")
	@ResponseBody
	public Map<String, Object> chk_p(String type, Integer month) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", prodAccountService.scm_chk_p(type, month));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 当期是否有无值仓有成本单价单据
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @return
	 */
	@RequestMapping("scm/reserve/chk_q.action")
	@ResponseBody
	public Map<String, Object> chk_q(String type, Integer month) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", prodAccountService.scm_chk_q(type, month));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 检查库存月结表金额与存货科目金额是否一致 
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @return
	 */
	@RequestMapping("scm/reserve/chk_r.action")
	@ResponseBody
	public Map<String, Object> chk_r(String type, Integer month) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", prodAccountService.scm_chk_r(type, month));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 存货模块金额与总账模块金额是否一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @return
	 */
	@RequestMapping("scm/reserve/chk_t.action")
	@ResponseBody
	public Map<String, Object> chk_t(String type, Integer month) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", prodAccountService.scm_chk_t(type, month));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 检查应付暂估与存货科目金额是否一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @return
	 */
	@RequestMapping("scm/reserve/chk_u.action")
	@ResponseBody
	public Map<String, Object> chk_u(String type, Integer month) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", prodAccountService.scm_chk_u(type, month));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 检查应付发票（当月验收验退当月开票）与存货科目金额是否一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @return
	 */
	@RequestMapping("scm/reserve/chk_v.action")
	@ResponseBody
	public Map<String, Object> chk_v(String type, Integer month) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", prodAccountService.scm_chk_v(type, month));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 检查应收发出商品与存货科目金额是否一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @return
	 */
	@RequestMapping("scm/reserve/chk_w.action")
	@ResponseBody
	public Map<String, Object> chk_w(String type, Integer month) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", prodAccountService.scm_chk_w(type, month));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 检查应收发票（当月出货退货当月开票）与存货科目金额是否一致
	 * 
	 * @param session
	 * @param type
	 * @param month
	 * @return
	 */
	@RequestMapping("scm/reserve/chk_x.action")
	@ResponseBody
	public Map<String, Object> chk_x(String type, Integer month) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", prodAccountService.scm_chk_x(type, month));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 当月所有生产报废单是否审核
	 * 
	 * @param type
	 * @param month
	 * @return
	 */
	@RequestMapping("scm/reserve/chk_y.action")
	@ResponseBody
	public Map<String, Object> chk_y(String type, Integer month) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", prodAccountService.scm_chk_y(type, month));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 是否有工单已完工未结案的
	 * 
	 * @param type
	 * @param month
	 * @return
	 */
	@RequestMapping("scm/reserve/chk_z.action")
	@ResponseBody
	public Map<String, Object> chk_z(String type, Integer month) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", prodAccountService.scm_chk_z(type, month));
		modelMap.put("success", true);
		return modelMap;
	}
}
