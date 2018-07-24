package com.uas.erp.controller.fa;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fa.VoucherCreateService;

@Controller
public class VoucherCreateController extends BaseController {

	@Autowired
	private VoucherCreateService voucherCreateService;

	/**
	 * 凭证制作
	 */
	@RequestMapping("fa/vc/createVoucher.action")
	@ResponseBody
	public Map<String, Object> createVoucher(HttpSession session, String vs_code, String datas, String mode, String kind, int yearmonth,
			String vomode, String mergerway) {
		return success(voucherCreateService.create(vs_code, datas, mode, kind, yearmonth, vomode, mergerway));
	}

	/**
	 * 凭证取消
	 */
	@RequestMapping("fa/vc/unCreateVoucher.action")
	@ResponseBody
	public Map<String, Object> unCreateVoucher(HttpSession session, String vs_code, String mode, String kind, String datas, String vomode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("error", voucherCreateService.unCreate(vs_code, mode, kind, datas, vomode));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 采购费用凭证取消
	 */
	@RequestMapping("fa/gla/unCreatePurcfee.action")
	@ResponseBody
	public Map<String, Object> unCreatePurcfee(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("error", voucherCreateService.unCreatePurcfee());
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 凭证制作公式
	 */
	@RequestMapping("fa/vc/saveVoucherStyle.action")
	@ResponseBody
	public Map<String, Object> saveVoucherStyle(HttpSession session, String formStore, String param, String param2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();

		voucherCreateService.saveVs(formStore, param, param2);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 凭证制作公式
	 */
	@RequestMapping("fa/vc/updateVoucherStyle.action")
	@ResponseBody
	public Map<String, Object> updateVoucherStyle(HttpSession session, String formStore, String param, String param2, String param3) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		voucherCreateService.updateVs(formStore, param, param2, param3);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/vc/getDigestSource.action")
	@ResponseBody
	public Map<String, Object> getDigestSource(HttpSession session, String code, String type) throws UnsupportedEncodingException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", voucherCreateService.getDigestSource(code, new String(type.getBytes("iso8859-1"), "utf-8")));
		return modelMap;
	}

	/**
	 * 生成凭证制作SQL
	 */
	@RequestMapping(value = "/fa/vc/createSql.action")
	@ResponseBody
	public Map<String, Object> createSql(int id, String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		voucherCreateService.createSql(id, type);
		modelMap.put("success", true);
		return modelMap;
	}
}
