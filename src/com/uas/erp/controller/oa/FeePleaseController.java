package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.oa.FeePleaseService;

@Controller
public class FeePleaseController {
	@Autowired
	private FeePleaseService feePleaseService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/oa/fee/saveFeePlease.action")
	@ResponseBody
	public Map<String, Object> save(HttpServletRequest request, String formStore, String param, String param2, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("fpd_data", feePleaseService.saveFeePlease(formStore, param, param2, caller));
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/oa/fee/updateFeePlease.action")
	@ResponseBody
	public Map<String, Object> update(HttpServletRequest request, String formStore, String param, String param2, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("fpd_data", feePleaseService.updateFeePlease(formStore, param, param2, caller));
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/fee/deleteFeePlease.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feePleaseService.deleteFeePlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核FeePlease
	 */
	@RequestMapping("/oa/fee/auditFeePlease.action")
	@ResponseBody
	public Map<String, Object> audit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feePleaseService.auditFeePlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/fee/resAuditFeePlease.action")
	@ResponseBody
	public Map<String, Object> resAudit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feePleaseService.resAuditFeePlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/fee/submitFeePlease.action")
	@ResponseBody
	public Map<String, Object> submit(HttpServletRequest request, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feePleaseService.submitFeePlease(id, caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/fee/resSubmitFeePlease.action")
	@ResponseBody
	public Map<String, Object> resSubmit(HttpServletRequest request, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feePleaseService.resSubmitFeePlease(id, caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 结案
	 */
	@RequestMapping("/oa/fee/endFeePlease.action")
	@ResponseBody
	public Map<String, Object> endFeePlease(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feePleaseService.endFeePlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反结案
	 */
	@RequestMapping("/oa/fee/resEndFeePlease.action")
	@ResponseBody
	public Map<String, Object> resEndFeePlease(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feePleaseService.resEndFeePlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/oa/fee/printFeePlease.action")
	@ResponseBody
	public Map<String, Object> printFeePlease(int id, String reportName, String condition, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = feePleaseService.printFeePlease(id, reportName, condition, caller);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 出差申请单转差旅费报销单(出差申请单页面)
	 */
	@RequestMapping("/oa/feeplease/turnCLFBX.action")
	@ResponseBody
	public Map<String, Object> turnCLFBX(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<String,Object> data = feePleaseService.turnCLFBX(id, caller);
		modelMap.put("id", data.get("fp_id"));
		modelMap.put("sob", data.get("sobName"));
		modelMap.put("code", data.get("fp_code"));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转费用报销单
	 */
	@RequestMapping("/oa/feeplease/turnFYBX.action")
	@ResponseBody
	public Map<String, Object> turnFYBX(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int fpid = feePleaseService.turnFYBX(id, "FeePlease!FYBX");
		modelMap.put("id", fpid);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 业务招待费转费用报销单
	 */
	@RequestMapping("/oa/feeplease/turnFYBX2.action")
	@ResponseBody
	public Map<String, Object> turnFYBX2(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int fpid = feePleaseService.turnFYBX2(id, caller);
		modelMap.put("id", fpid);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转银行付款申请单
	 */
	@RequestMapping("/oa/feeplease/turnYHFKSQ.action")
	@ResponseBody
	public Map<String, Object> turnYHFKSQ(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int fpid = feePleaseService.turnYHFKSQ(id, "FeePlease!YHFKSQ");
		modelMap.put("id", fpid);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转业务招待费报销单
	 */
	@RequestMapping("/oa/feeplease/turnYWZDBX.action")
	@ResponseBody
	public Map<String, Object> turnYWZDBX(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int fpid = feePleaseService.turnYWZDBX(id, "FeePlease!YWZDBX");
		modelMap.put("id", fpid);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转费用报销单2
	 */
	@RequestMapping("/oa/feeplease/jksqturnFYBX.action")
	@ResponseBody
	public Map<String, Object> jksqturnFYBX(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int fpid = feePleaseService.jksqturnFYBX(id, "FeePlease!FYBX");
		modelMap.put("id", fpid);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * OA单据批量转费用报销单
	 */
	@RequestMapping("/oa/feeplease/vastTurnFYBX.action")
	@ResponseBody
	public Map<String, Object> vastTurnFYBX(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", feePleaseService.vastTurnFYBX(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 公章用印申请单转费用报销单
	 */
	@RequestMapping("/oa/feeplease/sealTurnFYBX.action")
	@ResponseBody
	public Map<String, Object> sealTurnFYBX(int id, String caller, double thisturnamount) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", feePleaseService.sealTurnFYBX(id, caller, thisturnamount));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 确认申请
	 */
	@RequestMapping("/oa/fee/confirmFeePlease.action")
	@ResponseBody
	public Map<String, Object> confirmAsk4Leave(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feePleaseService.confirmFeePlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转银行登记
	 */
	@RequestMapping("/oa/fee/turnBankRegister.action")
	@ResponseBody
	public Map<String, Object> turnBankRegister(int id, String paymentcode, String payment, double thispayamount, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("log", feePleaseService.turnBankRegister(id, paymentcode, payment, thispayamount, caller));
		return modelMap;
	}

	/**
	 * 转应付票据
	 */
	@RequestMapping("/oa/fee/turnBillAP.action")
	@ResponseBody
	public Map<String, Object> turnBillAP(int id, String paymentcode, String payment, double thispayamount, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("log", feePleaseService.turnBillAP(id, paymentcode, payment, thispayamount, caller));
		return modelMap;
	}

	/**
	 * 转应收票据异动
	 */
	@RequestMapping("/oa/fee/turnBillARChange.action")
	@ResponseBody
	public Map<String, Object> turnBillARChange(int id, String paymentcode, String payment, double thispayamount, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("log", feePleaseService.turnBillARChange(id, paymentcode, payment, thispayamount, caller));
		return modelMap;
	}

	/**
	 * 出差申请单更新实际天数
	 * */
	@RequestMapping("oa/fee/updatefactdays.action")
	@ResponseBody
	public Map<String, Object> updatefactdays(String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feePleaseService.updateFactdays(data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 根据{id}取对应的编号
	 * 
	 * @param table
	 *            编号前缀(不含年月流水码)
	 */
	@RequestMapping("oa/fee/getContractTypeNum.action")
	@ResponseBody
	public Map<String, Object> getContractTypeNum(Integer id, String table, String k1, String k2, String k3, String k4) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (id != null) {
			modelMap.put("number", feePleaseService.getContractTypeNum(id, table));
		} else
			modelMap.put("number", feePleaseService.getContractTypeNumByKind(k1, k2, k3, k4));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 根据父节点加载子节点
	 */
	@RequestMapping("oa/fee/getContractTypeTree.action")
	@ResponseBody
	public Map<String, Object> getTreeByParentId(int parentid) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<JSONTree> tree = feePleaseService.getJsonTrees(parentid);
		modelMap.put("tree", tree);
		return modelMap;
	}

	/**
	 * 按搜索条件加载树
	 */
	@RequestMapping(value = "/oa/fee/searchContractTypeTree.action")
	@ResponseBody
	public Map<String, Object> getTreeBySearch(HttpSession session, String search) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		List<JSONTree> tree = feePleaseService.getJSONTreeBySearch(search, employee);
		modelMap.put("tree", tree);
		return modelMap;
	}

	/**
	 * 获取当前账号费用报销单的银行信息
	 * */
	@RequestMapping("oa/fee/getFeeAccount.action")
	@ResponseBody
	public Map<String, Object> getFeeAccount(String emcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("info", feePleaseService.getFeeAccount(emcode));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 客户地址保存
	 */
	@RequestMapping("/oa/feeplease/saveOutAddress.action")
	@ResponseBody
	public Map<String, Object> saveOutAddress(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feePleaseService.saveOutAddress(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取单据来源账套
	 * @param caller
	 * @param condition
	 * @return
	 */
	@RequestMapping("/oa/feeplease/getFromSob.action")
	@ResponseBody
	public Map<String, Object> getFromSob(String caller,String condition){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("sob", feePleaseService.getFromSob(condition));
		modelMap.put("success", true);
		return modelMap;
	}
}
