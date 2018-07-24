package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.service.pm.MakeBaseService;

@Controller
public class MakeBaseController extends BaseController {
	@Autowired
	private MakeBaseService makeBaseService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/pm/make/saveMakeBase.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.saveMakeBase(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/make/deleteMakeBase.action")
	@ResponseBody
	public Map<String, Object> deleteMakeBase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.deleteMakeBase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/pm/make/updateMakeBase.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.updateMakeBase(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/make/submitMakeBase.action")
	@ResponseBody
	public Map<String, Object> submitMakeBase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.submitMakeBase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/make/resSubmitMakeBase.action")
	@ResponseBody
	public Map<String, Object> resSubmitMakeBase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.resSubmitMakeBase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/make/auditMakeBase.action")
	@ResponseBody
	public Map<String, Object> auditMakeBase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.auditMakeBase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/make/resAuditMakeBase.action")
	@ResponseBody
	public Map<String, Object> resAuditMakeBase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.resAuditMakeBase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	// 打印ACTION
	@RequestMapping("/pm/make/printMakeBase.action")
	@ResponseBody
	public Map<String, Object> print(String caller, int id, String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = makeBaseService.printMakeBase(id, caller, reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 批准
	 */
	@RequestMapping("/pm/make/checkMakeBase.action")
	@ResponseBody
	public Map<String, Object> aproveMakeBase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.approveMakeBase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反批准
	 */
	@RequestMapping("/pm/make/resCheckMakeBase.action")
	@ResponseBody
	public Map<String, Object> resAproveMakeBase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.resApproveMakeBase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 结案
	 */
	@RequestMapping("/pm/make/endMakeBase.action")
	@ResponseBody
	public Map<String, Object> endMakeBase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.endMakeBase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反结案
	 */
	@RequestMapping("/pm/make/resEndMakeBase.action")
	@ResponseBody
	public Map<String, Object> resEndMakeBase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.resEndMakeBase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 计算用料
	 */
	@RequestMapping("/pm/make/setMakeMaterial.action")
	@ResponseBody
	public Map<String, Object> setMakeMaterial(String caller, String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.setMakeMaterial(code, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * BOM用量匹配
	 */
	@RequestMapping("/pm/make/MakeMaterialCheck.action")
	@ResponseBody
	public Map<String, Object> makeMaterialCheck(String caller, String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.makeMaterialCheck(code, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 替代关系 保存
	 * */
	@RequestMapping("/pm/make/saveMakeSubMaterial.action")
	@ResponseBody
	public Map<String, Object> saveMakeSubMaterial(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.saveMakeSubMaterial(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/***
	 * 替代关系 删除
	 */
	@RequestMapping("/pm/make/deleteMakeSubMaterial.action")
	@ResponseBody
	public Map<String, Object> deleteMakeSubMaterial(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.deleteMakeSubMaterial(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 用料修改 --保存
	 * */
	@RequestMapping("/pm/make/saveModifyMaterial.action")
	@ResponseBody
	public Map<String, Object> saveModifyMaterial(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Object id = makeBaseService.saveModifyMaterial(formStore, caller);
		modelMap.put("success", true);
		modelMap.put("Id", id);
		return modelMap;
	}

	/**
	 * 用料修改 -- 删除
	 * */
	@RequestMapping("/pm/make/deleteModifyMaterial.action")
	@ResponseBody
	public Map<String, Object> deleteModifyMaterial(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.deleteModifyMaterial(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 计算本次可领料数
	 */
	@RequestMapping("/pm/make/calThisQty.action")
	@ResponseBody
	public Map<String, Object> calThisQty(String caller, String ids) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.calThisQty(ids, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 计算在制数
	 */
	@RequestMapping("/pm/make/calOnlineQty.action")
	@ResponseBody
	public Map<String, Object> calOnlineQty(String caller, String ids,String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.calOnlineQty(ids, caller,data);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 计算退料数
	 */
//	@RequestMapping("/pm/make/calBackQty.action")
//	@ResponseBody
//	public Map<String, Object> calBackQty(String caller, String data) {
//		Map<String, Object> modelMap = new HashMap<String, Object>();
//		makeBaseService.calBackQty(data, caller);
//		modelMap.put("success", true);
//		return modelMap;
//	}

	/**
	 * 计算本次可补料数
	 */
	@RequestMapping("/pm/make/calAddQty.action")
	@ResponseBody
	public Map<String, Object> calAddQty(String caller, String ids) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.calAddQty(ids, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 制造单批量结案
	 */
	@RequestMapping("/pm/make/vastCloseMake.action")
	@ResponseBody
	public Map<String, Object> vastCloseMake(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.vastCloseMake(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 制造单批量强制结案
	 */
	@RequestMapping("/pm/make/vastEnforceEndMake.action")
	@ResponseBody
	public Map<String, Object> vastEnforceEndMake(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.vastEnforceEndMake(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 制造单批量批准
	 */
	@RequestMapping("/pm/make/vastApproveMake.action")
	@ResponseBody
	public Map<String, Object> vastApproveMake(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.vastApproveMake(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 制造单批量还原冻结
	 */
	@RequestMapping("/pm/make/vastResStart.action")
	@ResponseBody
	public Map<String, Object> vastResStart(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.vastResStart(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 结案单据批量重启
	 * 
	 * @param session
	 * @param data
	 * @return
	 */
	@RequestMapping("/pm/make/vastfinishResStart.action")
	@ResponseBody
	public Map<String, Object> vastfinishResStart(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.vastFinishResStart(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/pm/make/setLSThisQty.action")
	@ResponseBody
	public Map<String, Object> setLSThisQty(String caller, String ma_id, Integer qty, String wipwhcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		makeBaseService.setLSThisqty(caller, ma_id, qty, wipwhcode);
		return modelMap;
	}

	/**
	 * 刷新工单数量
	 */
	@RequestMapping("/pm/make/refreshqty.action")
	@ResponseBody
	public Map<String, Object> refreshqty(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.refreshqty(id, caller, 0);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * yaozx 13-07-18
	 * 
	 * @param session
	 * @param mf_code
	 * @return
	 */
	@RequestMapping("/pm/make/checkmfcode.action")
	@ResponseBody
	public Map<String, Object> checkmfcode(String caller, String mf_code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", makeBaseService.checkmfcode(mf_code, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 工单分拆
	 * */
	@RequestMapping("pm/make/splitMake.action")
	@ResponseBody
	public Map<String, Object> splitMake(String caller, String formdata, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.splitMake(formdata, data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 强制结案
	 */
	@RequestMapping("/pm/make/enforceEndMake.action")
	@ResponseBody
	public Map<String, Object> endPurchase(String caller, int id, String remark) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.enforceEndMake(id, caller, remark);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改委外信息 OSVendor
	 * 
	 * @param vend
	 *            委外商号
	 * @param curr
	 *            币别
	 * @param taxr
	 *            税率
	 * @param price
	 *            加工单价
	 * @param ma_servicer
	 *            是否免费加工
	 */
	@RequestMapping("/pm/make/updateOSInfoVendor.action")
	@ResponseBody
	public Map<String, Object> updateUU(String caller, Integer id, String vend, String curr, String taxr, String price, String paymc,
			String ma_servicer, String paym, String remark, String apvend) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.updateOSVendor(id, vend, curr, taxr, price, paymc, paym, ma_servicer, remark, apvend, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/pm/make/vastcostCloseMake.action")
	@ResponseBody
	public Map<String, Object> vastcostCloseMake(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.vastCloseMake(data, caller);
		makeBaseService.vastupdatemakecoststatus(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/pm/make/vastcostrestartMake.action")
	@ResponseBody
	public Map<String, Object> vastcostrestartMake(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.vastcostrestartMake(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新备注
	 * */
	@RequestMapping("/pm/make/updateRemark.action")
	@ResponseBody
	public Map<String, Object> updateUU(String caller, int id, String remark) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.updateRemark(id, remark, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新线别
	 * */
	@RequestMapping("/pm/make/updateTeamcode.action")
	@ResponseBody
	public Map<String, Object> updateTeamcode(String caller, int id, String value) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.updateTeamcode(id, value, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新用料表仓库
	 * */
	@RequestMapping("/pm/make/updateMaterialWH.action")
	@ResponseBody
	public Map<String, Object> updateMaterialWH(int id, String whcode, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.updateMaterialWH(id, whcode, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 制造单转委外单
	 * */
	@RequestMapping("/pm/make/turnOSMake.action")
	@ResponseBody
	public Map<String, Object> turnOSMake(int id, String kind, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.turnOSMake(caller, id, kind);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 委外单转制造单
	 * */
	@RequestMapping("/pm/make/turnOSToMake.action")
	@ResponseBody
	public Map<String, Object> turnOSToMake(String caller, int id, String kind) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.turnOSToMake(caller, id, kind);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 拿到编号
	 */
	@RequestMapping("/pm/make/getCodeString.action")
	@ResponseBody
	public Map<String, Object> getCode(String caller, String table, int type, String conKind) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("code", makeBaseService.getCodeString(caller, table, type, conKind));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 替代关系维护 设替代料为主料
	 */
	@RequestMapping("/pm/make/setMain.action")
	@ResponseBody
	public Map<String, Object> setMain(String caller, int mmid, int detno) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.setMain(mmid, detno, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新流程类型
	 * 
	 * @param caller
	 * @param id
	 *            工单ID
	 * @param value
	 *            流程类型
	 * @return
	 */
	@RequestMapping("/pm/make/updateMaStyle.action")
	@ResponseBody
	public Map<String, Object> updateMaStyle(String caller, int id, String value) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.updateMaStyle(id, value, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改委外交货地点 OSVendor
	 * 
	 * @param address
	 *            交货地点
	 */
	@RequestMapping("/pm/make/updateShiPAddress.action")
	@ResponseBody
	public Map<String, Object> updateShiPAddress(Integer id, String address, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.updateShiPAddress(id, address, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打开Mrp
	 */
	@RequestMapping("/pm/make/openMrp.action")
	@ResponseBody
	public Map<String, Object> openMrb(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.openMrp(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 关闭Mrp
	 */
	@RequestMapping("/pm/make/CloseMrp.action")
	@ResponseBody
	public Map<String, Object> closeMrp(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.closeMrp(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取工单用料表中的跳层BOM
	 * @param ma_id 制造单ID
	 */
	@RequestMapping("/pm/make/getPastBom.action")
	@ResponseBody
	public Map<String, Object> getPastBom(String caller, Long ma_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", makeBaseService.getPastBom(ma_id, caller));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 取消BOM跳层记录日志
	 * @param mm_id 用料表ID
	 */
	@RequestMapping("/pm/make/disableBomPast.action")
	@ResponseBody
	public Map<String, Object> disableBomPast(String caller, Long mm_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.disableBomPast(mm_id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 制造单批量冻结
	 */
	@RequestMapping("/pm/make/vastFreeze.action")
	@ResponseBody
	public Map<String, Object> vastFreeze(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeBaseService.vastFreeze(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 特殊出库批量核销
	 */
	@RequestMapping("/pm/make/vastWriteoff.action")
	@ResponseBody
	public Map<String, Object> vastWriteoff(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log=makeBaseService.vastWriteoff(data, caller);
		modelMap.put("success", true);
		modelMap.put("log",log);
		return modelMap;
	}
	
	/**
	 * 制造单结案 的生成退料单
	 */
	@RequestMapping("/pm/make/createReturnMake.action")
	@ResponseBody
	public Map<String, Object> createReturnMake(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log=makeBaseService.createReturnMake(data, caller);
		modelMap.put("success", true);
		modelMap.put("log",log);
		return modelMap;
	}
}
