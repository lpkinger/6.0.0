package com.uas.erp.controller.fa;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.common.FilePathService;
import com.uas.erp.service.fa.VoucherService;

@Controller
public class VoucherController extends BaseController {
	@Autowired
	private VoucherService voucherService;
	@Autowired
	private FilePathService filePathService;

	/**
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/fa/ars/saveVoucher.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore, String param, String param2, String param3, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] params = new String[] { param, param2, param3 };
		voucherService.saveVoucher(formStore, params, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/ars/deleteVoucher.action")
	@ResponseBody
	public Map<String, Object> deleteVoucher(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		voucherService.deleteVoucher(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/fa/ars/updateVoucher.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore, String param, String param2, String param3, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] params = new String[] { param, param2, param3 };
		voucherService.updateVoucherById(formStore, params, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/ars/submitVoucher.action")
	@ResponseBody
	public Map<String, Object> submitVoucher(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		voucherService.submitVoucher(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/ars/resSubmitVoucher.action")
	@ResponseBody
	public Map<String, Object> resSubmitVoucher(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		voucherService.resSubmitVoucher(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/ars/auditVoucher.action")
	@ResponseBody
	public Map<String, Object> auditVoucher(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		voucherService.auditVoucher(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/ars/resAuditVoucher.action")
	@ResponseBody
	public Map<String, Object> resAuditVoucher(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		voucherService.resAuditVoucher(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/ars/printVoucher.action")
	@ResponseBody
	public Map<String, Object> printPurchase(HttpSession session, int id, String reportName, String condition, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = voucherService.printVoucher(id, reportName, condition, caller);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 复制
	 */
	@RequestMapping("/fa/ars/copyVoucher.action")
	@ResponseBody
	public Map<String, Object> copyVoucher(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("voucher", voucherService.copyVoucher(id));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除明细时，计算凭证状态
	 */
	@RequestMapping("/fa/ars/validVoucher.action")
	@ResponseBody
	public Map<String, Object> validVoucher(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("errstring", voucherService.validVoucher(id));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 凭证号断号重排
	 */
	@RequestMapping("/fa/ars/insertBreakVoNumber.action")
	@ResponseBody
	public Map<String, Object> insertBreakVoNumber(HttpSession session, String data, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		voucherService.insertBreakVoNumber(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 凭证批量审核
	 */
	@RequestMapping(value = "/fa/ars/voucher/vastAudit.action")
	@ResponseBody
	public Map<String, Object> vastAudit(HttpSession session, String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		voucherService.vastAudit(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 凭证批量取消审核
	 */
	@RequestMapping(value = "/fa/ars/voucher/vastUnAudit.action")
	@ResponseBody
	public Map<String, Object> vastUnAudit(HttpSession session, String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		voucherService.vastUnAudit(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 凭证批量记账
	 */
	@RequestMapping("/fa/ars/accountVoucher.action")
	@ResponseBody
	public Map<String, Object> account(HttpSession session, Integer month, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("result", voucherService.accountVoucher(month, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 凭证批量取消记账
	 */
	@RequestMapping("/fa/ars/resAccountVoucher.action")
	@ResponseBody
	public Map<String, Object> resAccount(HttpSession session, Integer month, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("result", voucherService.resAccountVoucher(month, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 导入辅助核算
	 * */
	@RequestMapping(value = "/fa/ars/ImportExcel.action")
	@ResponseBody
	public Map<String, Object> ImportExcel(HttpSession session, int id, int fileId, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		InputStream is = null;
		String filePath = filePathService.getFilepath(fileId);
		Workbook wbs = null;
		try {
			String ft = filePath.substring(filePath.lastIndexOf(".") + 1);
			is = new FileInputStream(new File(filePath));
			if (ft.equals("xls")) {
				wbs = new HSSFWorkbook(is);
			} else if (ft.equals("xlsx")) {
				wbs = new XSSFWorkbook(is);

			} else {
				modelMap.put("error", "excel文件的格式不太规范,导入失败<hr>可以尝试将文件另存为,然后导入");
				return modelMap;
			}
			boolean bool = voucherService.ImportExcel(id, wbs, filePath.substring(filePath.lastIndexOf("/") + 1), caller);
			if (bool) {
				// Excel 解析成功之后要删除
				File file = new File(filePath);
				// 路径为文件且不为空则进行删除
				if (file.isFile() && file.exists()) {
					file.delete();
				}
			}
			modelMap.put("success", true);
			return modelMap;

		} catch (Exception e) {
			e.printStackTrace();
			modelMap.put("error", e.getMessage());
			return modelMap;
		} finally {
			try {
				is.close();
			} catch (IOException e) {

			}
		}
	}

	/**
	 * 根据{状态}取对应的凭证数量
	 */
	@RequestMapping(value = "/fa/ars/getVoucherCount.action")
	@ResponseBody
	public Map<String, Object> getVoucherCount() throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", voucherService.getVoucherCount());
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获取凭证辅助核算
	 * 
	 * <pre>
	 * 原先common / getFieldsDatas.action获取数据方式有问题
	 * </pre>
	 * 
	 * @param vo_id
	 * @return
	 */
	@RequestMapping(value = "/fa/ars/getVoucherAss.action")
	@ResponseBody
	public ModelMap getVoucherAss(int vo_id) {
		return success(voucherService.findAss(vo_id));
	}

	/**
	 * 审计期间设置
	 */
	@RequestMapping(value = "/fa/gla/auditDuring.action")
	@ResponseBody
	public Map<String, Object> auditDuring(int year, boolean myear, boolean eyear) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		voucherService.auditDuring(year, myear, eyear);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 红冲
	 */
	@RequestMapping("/fa/ars/rushRedVoucher.action")
	@ResponseBody
	public Map<String, Object> rushRedVoucher(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("voucher", voucherService.rushRedVoucher(id));
		modelMap.put("success", true);
		return modelMap;
	}
}
