package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fa.VoucherTPService;

/**
 * 凭证模板
 * 
 * @author yingp
 * 
 */
@Controller
public class VoucherTPController extends BaseController {

	@Autowired
	private VoucherTPService voucherTPService;

	/**
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/fa/gla/saveVoucherTP.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore, String param, String param2, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		voucherTPService.saveVoucherTP(formStore, param, param2, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/gla/deleteVoucherTP.action")
	@ResponseBody
	public Map<String, Object> deleteVoucher(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		voucherTPService.deleteVoucherTP(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/fa/gla/updateVoucherTP.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore, String param, String param2, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		voucherTPService.updateVoucherTP(formStore, param, param2, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 模板制作凭证
	 */
	@RequestMapping("/fa/gla/createvobytp.action")
	@ResponseBody
	public Map<String, Object> createVoByTP(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", voucherTPService.createVoucher(id));
		return modelMap;
	}
	
	/**
	 * 加载模板
	 */
	@RequestMapping("/fa/gla/getvotp.action")
	@ResponseBody
	public Map<String, Object> getTp(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", voucherTPService.getTp(id));
		return modelMap;
	}
	
	/**
	 * 从凭证添加到模板
	 */
	@RequestMapping("/fa/gla/copyvotp.action")
	@ResponseBody
	public Map<String, Object> createTpByVo(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", voucherTPService.createTpByVo(id));
		return modelMap;
	}

}
