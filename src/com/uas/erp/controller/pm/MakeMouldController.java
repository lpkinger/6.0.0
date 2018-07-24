package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MakeMouldService;

@Controller
public class MakeMouldController extends BaseController{
	
	@Autowired
	private MakeMouldService MakeMouldService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/pm/mould/saveMakeMould.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.saveMakeMould(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/mould/deleteMakeMould.action")
	@ResponseBody
	public Map<String, Object> deleteMakeMould(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.deleteMakeMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/pm/mould/updateMakeMould.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.updateMakeMould(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mould/submitMakeMould.action")
	@ResponseBody
	public Map<String, Object> submitMakeMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.submitMakeMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mould/resSubmitMakeMould.action")
	@ResponseBody
	public Map<String, Object> resSubmitMakeMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.resSubmitMakeMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mould/auditMakeMould.action")
	@ResponseBody
	public Map<String, Object> auditMakeMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.auditMakeMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mould/resAuditMakeMould.action")
	@ResponseBody
	public Map<String, Object> resAuditMakeMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.resAuditMakeMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	// 打印ACTION
	@RequestMapping("/pm/mould/printMakeMould.action")
	@ResponseBody
	public Map<String, Object> print(String caller, int id, String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = MakeMouldService.printMakeMould(id, caller, reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 批准
	 */
	@RequestMapping("/pm/mould/checkMakeMould.action")
	@ResponseBody
	public Map<String, Object> aproveMakeMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.approveMakeMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反批准
	 */
	@RequestMapping("/pm/mould/resCheckMakeMould.action")
	@ResponseBody
	public Map<String, Object> resAproveMakeMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.resApproveMakeMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 结案
	 */
	@RequestMapping("/pm/mould/endMakeMould.action")
	@ResponseBody
	public Map<String, Object> endMakeMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.endMakeMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反结案
	 */
	@RequestMapping("/pm/mould/resEndMakeMould.action")
	@ResponseBody
	public Map<String, Object> resEndMakeMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.resEndMakeMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 计算用料
	 */
	@RequestMapping("/pm/mould/setMakeMaterial.action")
	@ResponseBody
	public Map<String, Object> setMakeMaterial(String caller, String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.setMakeMaterial(code, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * BOM用量匹配
	 */
	@RequestMapping("/pm/mould/MakeMaterialCheck.action")
	@ResponseBody
	public Map<String, Object> makeMaterialCheck(String caller, String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.makeMaterialCheck(code, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 替代关系 保存
	 * */
	@RequestMapping("/pm/mould/saveMakeSubMaterial.action")
	@ResponseBody
	public Map<String, Object> saveMakeSubMaterial(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.saveMakeSubMaterial(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/***
	 * 替代关系 删除
	 */
	@RequestMapping("/pm/mould/deleteMakeSubMaterial.action")
	@ResponseBody
	public Map<String, Object> deleteMakeSubMaterial(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.deleteMakeSubMaterial(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 用料修改 --保存
	 * */
	@RequestMapping("/pm/mould/saveModifyMaterial.action")
	@ResponseBody
	public Map<String, Object> saveModifyMaterial(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Object id = MakeMouldService.saveModifyMaterial(formStore, caller);
		modelMap.put("success", true);
		modelMap.put("Id", id);
		return modelMap;
	}

	/**
	 * 用料修改 -- 删除
	 * */
	@RequestMapping("/pm/mould/deleteModifyMaterial.action")
	@ResponseBody
	public Map<String, Object> deleteModifyMaterial(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.deleteModifyMaterial(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 计算本次可领料数
	 */
	@RequestMapping("/pm/mould/calThisQty.action")
	@ResponseBody
	public Map<String, Object> calThisQty(String caller, String ids) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.calThisQty(ids, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 计算在制数
	 */
	@RequestMapping("/pm/mould/calOnlineQty.action")
	@ResponseBody
	public Map<String, Object> calOnlineQty(String caller, String ids) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.calOnlineQty(ids, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 计算本次可补料数
	 */
	@RequestMapping("/pm/mould/calAddQty.action")
	@ResponseBody
	public Map<String, Object> calAddQty(String caller, String ids) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.calAddQty(ids, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 制造单批量结案
	 */
	@RequestMapping("/pm/mould/vastCloseMake.action")
	@ResponseBody
	public Map<String, Object> vastCloseMake(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.vastCloseMake(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 制造单批量强制结案
	 */
	@RequestMapping("/pm/mould/vastEnforceEndMake.action")
	@ResponseBody
	public Map<String, Object> vastEnforceEndMake(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.vastEnforceEndMake(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 制造单批量批准
	 */
	@RequestMapping("/pm/mould/vastApproveMake.action")
	@ResponseBody
	public Map<String, Object> vastApproveMake(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.vastApproveMake(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 制造单批量还原冻结
	 */
	@RequestMapping("/pm/mould/vastResStart.action")
	@ResponseBody
	public Map<String, Object> vastResStart(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.vastResStart(data, caller);
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
	@RequestMapping("/pm/mould/vastfinishResStart.action")
	@ResponseBody
	public Map<String, Object> vastfinishResStart(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.vastFinishResStart(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/pm/mould/setLSThisQty.action")
	@ResponseBody
	public Map<String, Object> setLSThisQty(String caller, String ma_id, Integer qty, String wipwhcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		MakeMouldService.setLSThisqty(caller, ma_id, qty, wipwhcode);
		return modelMap;
	}

	/**
	 * 刷新工单数量
	 */
	@RequestMapping("/pm/mould/refreshqty.action")
	@ResponseBody
	public Map<String, Object> refreshqty(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.refreshqty(id, caller, 0);
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
	@RequestMapping("/pm/mould/checkmfcode.action")
	@ResponseBody
	public Map<String, Object> checkmfcode(String caller, String mf_code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", MakeMouldService.checkmfcode(mf_code, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 工单分拆
	 * */
	@RequestMapping("pm/mould/splitMake.action")
	@ResponseBody
	public Map<String, Object> splitMake(String caller, String formdata, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.splitMake(formdata, data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 强制结案
	 */
	@RequestMapping("/pm/mould/enforceEndMake.action")
	@ResponseBody
	public Map<String, Object> endPurchase(String caller, int id, String remark) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.enforceEndMake(id, caller, remark);
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
	@RequestMapping("/pm/mould/updateOSInfoVendor.action")
	@ResponseBody
	public Map<String, Object> updateUU(String caller, Integer id, String vend, String curr, String taxr, String price, String paymc,
			String ma_servicer, String paym, String remark, String apvend) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.updateOSVendor(id, vend, curr, taxr, price, paymc, paym, ma_servicer, remark, apvend, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/pm/mould/vastcostCloseMake.action")
	@ResponseBody
	public Map<String, Object> vastcostCloseMake(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.vastCloseMake(data, caller);
		MakeMouldService.vastupdatemakecoststatus(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/pm/mould/vastcostrestartMake.action")
	@ResponseBody
	public Map<String, Object> vastcostrestartMake(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.vastcostrestartMake(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新备注
	 * */
	@RequestMapping("/pm/mould/updateRemark.action")
	@ResponseBody
	public Map<String, Object> updateUU(String caller, int id, String remark) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.updateRemark(id, remark, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新线别
	 * */
	@RequestMapping("/pm/mould/updateTeamcode.action")
	@ResponseBody
	public Map<String, Object> updateTeamcode(String caller, int id, String value) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.updateTeamcode(id, value, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新用料表仓库
	 * */
	@RequestMapping("/pm/mould/updateMaterialWH.action")
	@ResponseBody
	public Map<String, Object> updateMaterialWH(int id, String whcode, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.updateMaterialWH(id, whcode, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 制造单转委外单
	 * */
	@RequestMapping("/pm/mould/turnOSMake.action")
	@ResponseBody
	public Map<String, Object> turnOSMake(int id, String kind, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.turnOSMake(caller, id, kind);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 委外单转制造单
	 * */
	@RequestMapping("/pm/mould/turnOSToMake.action")
	@ResponseBody
	public Map<String, Object> turnOSToMake(String caller, int id, String kind) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.turnOSToMake(caller, id, kind);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 拿到编号
	 */
	@RequestMapping("/pm/mould/getCodeString.action")
	@ResponseBody
	public Map<String, Object> getCode(String caller, String table, int type, String conKind) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("code", MakeMouldService.getCodeString(caller, table, type, conKind));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 替代关系维护 设替代料为主料
	 */
	@RequestMapping("/pm/mould/setMain.action")
	@ResponseBody
	public Map<String, Object> setMain(String caller, int mmid, int detno) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.setMain(mmid, detno, caller);
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
	@RequestMapping("/pm/mould/updateMaStyle.action")
	@ResponseBody
	public Map<String, Object> updateMaStyle(String caller, int id, String value) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.updateMaStyle(id, value, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改委外交货地点 OSVendor
	 * 
	 * @param address
	 *            交货地点
	 */
	@RequestMapping("/pm/mould/updateShiPAddress.action")
	@ResponseBody
	public Map<String, Object> updateShiPAddress(Integer id, String address, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.updateShiPAddress(id, address, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打开Mrp
	 */
	@RequestMapping("/pm/mould/openMrp.action")
	@ResponseBody
	public Map<String, Object> openMrb(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.openMrp(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 关闭Mrp
	 */
	@RequestMapping("/pm/mould/CloseMrp.action")
	@ResponseBody
	public Map<String, Object> closeMrp(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.closeMrp(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取工单用料表中的跳层BOM
	 * @param ma_id 制造单ID
	 */
	@RequestMapping("/pm/mould/getPastBom.action")
	@ResponseBody
	public Map<String, Object> getPastBom(String caller, Long ma_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", MakeMouldService.getPastBom(ma_id, caller));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 取消BOM跳层记录日志
	 * @param mm_id 用料表ID
	 */
	@RequestMapping("/pm/mould/disableBomPast.action")
	@ResponseBody
	public Map<String, Object> disableBomPast(String caller, Long mm_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.disableBomPast(mm_id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 制造单批量冻结
	 */
	@RequestMapping("/pm/mould/vastFreeze.action")
	@ResponseBody
	public Map<String, Object> vastFreeze(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeMouldService.vastFreeze(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 特殊出库批量核销
	 */
	@RequestMapping("/pm/mould/vastWriteoff.action")
	@ResponseBody
	public Map<String, Object> vastWriteoff(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log=MakeMouldService.vastWriteoff(data, caller);
		modelMap.put("success", true);
		modelMap.put("log",log);
		return modelMap;
	}
	
	/**
	 * 制造单结案 的生成退料单
	 */
	@RequestMapping("/pm/mould/createReturnMake.action")
	@ResponseBody
	public Map<String, Object> createReturnMake(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log=MakeMouldService.createReturnMake(data, caller);
		modelMap.put("success", true);
		modelMap.put("log",log);
		return modelMap;
	}
}
