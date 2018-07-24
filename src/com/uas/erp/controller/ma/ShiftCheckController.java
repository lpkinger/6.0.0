package com.uas.erp.controller.ma;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.ma.ShiftCheckService;

@Controller
public class ShiftCheckController {
	
	@Autowired
	private ShiftCheckService shiftCheckService;
	/**
	 * 采购单数量与来源请购单已转数量一致
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping("ma/shiftcheck/chk_a.action")
	@ResponseBody
	public Map<String, Object> ma_chk_a(String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", shiftCheckService.ma_chk_a(type));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 收料单数量与来源采购单已转收料数一致
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping("ma/shiftcheck/chk_b.action")
	@ResponseBody
	public Map<String, Object> ma_chk_b(String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", shiftCheckService.ma_chk_b(type));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 检验单检验数量、合格数、不合格数与收料单的一致
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping("ma/shiftcheck/chk_c.action")
	@ResponseBody
	public Map<String, Object> ma_chk_c(String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", shiftCheckService.ma_chk_c(type));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 采购验收单数量与检验单入良品仓数一致
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping("ma/shiftcheck/chk_d.action")
	@ResponseBody
	public Map<String, Object> ma_chk_d(String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", shiftCheckService.ma_chk_d(type));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 采购验收单(已过账)数量与采购单已验收数量一致
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping("ma/shiftcheck/chk_e.action")
	@ResponseBody
	public Map<String, Object> ma_chk_e(String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", shiftCheckService.ma_chk_e(type));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 不良品入库单数量与检验单入不良品仓数一致
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping("ma/shiftcheck/chk_f.action")
	@ResponseBody
	public Map<String, Object> ma_chk_f(String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", shiftCheckService.ma_chk_f(type));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 不良品入库单(已过账)数量与采购单不良入库数一致
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping("ma/shiftcheck/chk_g.action")
	@ResponseBody
	public Map<String, Object> ma_chk_g(String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", shiftCheckService.ma_chk_g(type));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 通知单数量与订单已转数量一致
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping("ma/shiftcheck/chk_h.action")
	@ResponseBody
	public Map<String, Object> ma_chk_h(String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", shiftCheckService.ma_chk_h(type));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 出货单(包含未过账)数量与来源通知单已转数量一致
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping("ma/shiftcheck/chk_i.action")
	@ResponseBody
	public Map<String, Object> ma_chk_i(String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", shiftCheckService.ma_chk_i(type));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 出货单(已过账)数量与来源订单已发货数量一致
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping("ma/shiftcheck/chk_j.action")
	@ResponseBody
	public Map<String, Object> ma_chk_j(String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", shiftCheckService.ma_chk_j(type));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 领料单数量(未过账)与工单已转领料数一致
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping("ma/shiftcheck/chk_k.action")
	@ResponseBody
	public Map<String, Object> ma_chk_k(String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", shiftCheckService.ma_chk_k(type));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 领料单数量(已过账)与工单已领料数一致
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping("ma/shiftcheck/chk_l.action")
	@ResponseBody
	public Map<String, Object> ma_chk_l(String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", shiftCheckService.ma_chk_l(type));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 补料单数量(未过账)与工单已转补料数一致
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping("ma/shiftcheck/chk_m.action")
	@ResponseBody
	public Map<String, Object> ma_chk_m(String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", shiftCheckService.ma_chk_m(type));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 补料单数量(已过账)与工单已补料数一致
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping("ma/shiftcheck/chk_n.action")
	@ResponseBody
	public Map<String, Object> ma_chk_n(String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ok", shiftCheckService.ma_chk_n(type));
		modelMap.put("success", true);
		return modelMap;
	}
}
