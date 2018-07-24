package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.VoucherDescriptionService;

@Controller
public class VoucherDescriptionController extends BaseController {
	@Autowired
	private VoucherDescriptionService voucherDescriptionService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/fa/ars/saveVoucherDescription.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore,
			String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		voucherDescriptionService.saveVoucherDescription(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/ars/deleteVoucherDescription.action")
	@ResponseBody
	public Map<String, Object> deleteVoucherDescription(HttpSession session,
			int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		voucherDescriptionService.deleteVoucherDescription(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/fa/ars/updateVoucherDescription.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore,
			String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		voucherDescriptionService.updateVoucherDescriptionById(formStore,
				caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
