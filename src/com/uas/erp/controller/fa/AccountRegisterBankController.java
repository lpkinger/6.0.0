package com.uas.erp.controller.fa;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
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
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.common.FilePathService;
import com.uas.erp.service.fa.AccountRegisterBankService;

@Controller
public class AccountRegisterBankController extends BaseController {
	@Autowired
	private AccountRegisterBankService accountRegisterBankService;
	@Autowired
	private FilePathService filePathService;

	/**
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/fa/gs/saveAccountRegister.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param, String param2, String param3) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] params = new String[] { param, param2, param3 };
		accountRegisterBankService.saveAccountRegister(formStore, params, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/gs/deleteAccountRegister.action")
	@ResponseBody
	public Map<String, Object> deleteAccountRegister(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountRegisterBankService.deleteAccountRegister(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/fa/gs/updateAccountRegister.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param, String param2, String param3) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] params = new String[] { param, param2, param3 };
		accountRegisterBankService.updateAccountRegisterById(formStore, params, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/gs/submitAccountRegister.action")
	@ResponseBody
	public Map<String, Object> submitAccountRegister(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountRegisterBankService.submitAccountRegister(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/gs/resSubmitAccountRegister.action")
	@ResponseBody
	public Map<String, Object> resSubmitAccountRegister(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountRegisterBankService.resSubmitAccountRegister(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/gs/printAccountRegister.action")
	@ResponseBody
	public Map<String, Object> printAccountRegister(String caller, int id, String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = accountRegisterBankService.printAccountRegister(id, caller, reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 记账
	 */
	@RequestMapping("/fa/gs/accountAccountRegister.action")
	@ResponseBody
	public Map<String, Object> accountAccountRegister(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountRegisterBankService.accountedAccountRegister(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反记账
	 */
	@RequestMapping("/fa/gs/resAccountAccountRegister.action")
	@ResponseBody
	public Map<String, Object> resAccountAccountRegister(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountRegisterBankService.resAccountedAccountRegister(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除明细时，计算凭证状态
	 */
	@RequestMapping("/fa/gs/validAccountRegister.action")
	@ResponseBody
	public Map<String, Object> validAccountRegister(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("errstring", accountRegisterBankService.validAccountRegister(id));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 根据父节点加载子节点
	 */
	@RequestMapping(value = "/fa/gs/getCategoryBankTree.action")
	@ResponseBody
	public Map<String, Object> getTreeByParentId(String caller, int parentid, String master) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<JSONTree> tree = accountRegisterBankService.getJsonTrees(parentid, master);
		modelMap.put("tree", tree);
		return modelMap;
	}

	/**
	 * 转付款单
	 */
	@RequestMapping("/fa/gs/arTurnPayBalance.action")
	@ResponseBody
	public Map<String, Object> turnPayBalance(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int pbid = accountRegisterBankService.turnPayBalance(id, caller);
		modelMap.put("id", pbid);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转收款单
	 */
	@RequestMapping("/fa/gs/arTurnRecBalance.action")
	@ResponseBody
	public Map<String, Object> turnRecBalance(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int pbid = accountRegisterBankService.turnRecBalance(id, caller);
		modelMap.put("id", pbid);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新备注
	 * */
	@RequestMapping("/fa/gs/updateRemark.action")
	@ResponseBody
	public Map<String, Object> updateUU(String caller, int id, String remark) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountRegisterBankService.updateRemark(id, remark, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 复制
	 */
	@RequestMapping("/fa/gs/copyAccountRegister.action")
	@ResponseBody
	public Map<String, Object> copyAccountRegister(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ar", accountRegisterBankService.copyAccountRegister(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/fa/gs/ImportRegisterExcel.action")
	@ResponseBody
	public Map<String, Object> ImportExcel(String caller, int id, int fileId) {
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
			boolean bool = accountRegisterBankService.ImportExcel(id, wbs, filePath.substring(filePath.lastIndexOf("/") + 1));
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

	@RequestMapping("/fa/gs/updateType.action")
	@ResponseBody
	public Map<String, Object> updateType(String caller, String custcode, String custname, String sellercode, String sellername,
			String arapcurrency, String araprate, String aramount, String vendcode, String vendname, String category, String description,
			String precurrency, String prerate, String preamount, String payment, String deposit, String id, String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountRegisterBankService.updateType(custcode, custname, sellercode, sellername, arapcurrency, araprate, aramount, vendcode,
				vendname, category, description, precurrency, prerate, preamount, payment, deposit, id, type, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 刷新
	 */
	@RequestMapping("/fa/gs/copyAccountRegister/refreshQuery.action")
	@ResponseBody
	public Map<String, Object> refreshQuery(String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountRegisterBankService.refreshQuery(condition);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转冲应收款单
	 */
	@RequestMapping("/fa/gs/arTurnRecBalanceIMRE.action")
	@ResponseBody
	public Map<String, Object> turnRecBalanceIMRE(HttpSession session, int id, String custcode, String thisamount) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int pbid = accountRegisterBankService.turnRecBalanceIMRE(id, custcode, thisamount);
		modelMap.put("id", pbid);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 暂收款结案
	 */
	@RequestMapping("/fa/gs/endRecAmount.action")
	@ResponseBody
	public Map<String, Object> endRecAmount(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountRegisterBankService.endRecAmount(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获取银行登记辅助核算
	 * 
	 * @param ar_id
	 * @return
	 */
	@RequestMapping(value = "/fa/gs/getAccountRegisterAss.action")
	@ResponseBody
	public ModelMap getAccountRegisterAss(int ar_id) {
		return success(accountRegisterBankService.findAss(ar_id));
	}
	
	/**
	 * 导入辅助核算
	 * */
	@RequestMapping(value = "/fa/gs/ImportExcel.action")
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
			boolean bool = accountRegisterBankService.ImportExcel(id, wbs, filePath.substring(filePath.lastIndexOf("/") + 1), caller);
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

}
