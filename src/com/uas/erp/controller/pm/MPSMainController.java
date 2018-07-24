package com.uas.erp.controller.pm;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.ExcelUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.GridPanel;
import com.uas.erp.service.pm.MPSMainService;

@Controller
public class MPSMainController {
	@Autowired
	private MPSMainService mPSMainService;

	@RequestMapping("pm/mps/saveMPS.action")
	@ResponseBody
	public Map<String, Object> saveProjectPlan(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mPSMainService.saveMPS(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("pm/mps/updateMPS.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mPSMainService.updateMPSById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("pm/mps/deleteMPS.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mPSMainService.deleteMPS(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("pm/mps/submitMPS.action")
	@ResponseBody
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mPSMainService.submitMPS(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("pm/mps/resSubmitMPS.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mPSMainService.resSubmitMPS(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("pm/mps/aduitMPS.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mPSMainService.auditMPS(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("pm/mps/resAuditMPS.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mPSMainService.resAuditMPS(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("pm/mps/autoLoad.action")
	@ResponseBody
	public Map<String, Object> autoLoad(String caller, int id) {
		Map<String, Object> map = new HashMap<String, Object>();
		mPSMainService.autoLoadData(id, caller);
		map.put("success", true);
		return map;
	}

	@RequestMapping("pm/source/LoadSource.action")
	@ResponseBody
	public Map<String, Object> LoadSource(int keyValue, String mainCode, String caller, String detailcaller, String Store,
			String gridStore, String kind) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mPSMainService.LoadData(keyValue, mainCode, caller, detailcaller, Store, gridStore, kind);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("pm/mps/loadSaleDetailDet.action")
	@ResponseBody
	public Map<String, Object> loadSaleDetailDet(int keyValue, String type, String caller, String data, String condition) {
		Map<String, Object> map = new HashMap<String, Object>();
		mPSMainService.loadSaleDetailDet(keyValue, type, caller, data, condition);
		map.put("success", true);
		return map;
	}

	@RequestMapping("pm/mps/RunMrp.action")
	@ResponseBody
	public Map<String, Object> RunMrp(String caller, String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String str = mPSMainService.RunMrp(code, caller);
		modelMap.put("message", str);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 
	 * @param caller 
	 * @param mainCode MRP单号
	 * @param gridStore 从表数据
	 * @param toWhere
	 * @param toCode
	 * @param condition
	 * @param maKind
	 * @param purcaseCop 采购账套
	 * @param apKind 请购类型
	 * @return
	 */
	@RequestMapping("pm/MPSMain/NeedThrow.action")
	@ResponseBody
	public Map<String, Object> NeedThrow(String caller, String mainCode, String gridStore, String toWhere, String toCode, String condition,String maKind,String purcaseCop,String apKind) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<String, Object> resmap = new HashMap<String, Object>();
		resmap = mPSMainService.NeedThrow(caller, mainCode, gridStore, toWhere, toCode, condition,maKind,purcaseCop,apKind);
		modelMap.put("success", true);
		modelMap.put("resmap", resmap);
		return modelMap;
	}

	@RequestMapping("pm/mps/deleteAllDetails.action")
	@ResponseBody
	public Map<String, Object> deleteAllDetails(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mPSMainService.deleteAllDetails(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("pm/source/sourceCount.action")
	@ResponseBody
	public Map<String, Object> getDataListCount(String caller, String condition, String distinct) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("count", mPSMainService.getCountByCaller(caller, condition));
		return modelMap;
	}

	@RequestMapping(value = "pm/source/source.action")
	@ResponseBody
	public Map<String, Object> getDataListGrid(HttpServletRequest req, String caller, String condition) {
		Boolean _self = (Boolean) req.getAttribute("_self");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// int enid = (Integer) session.getAttribute("en_uu");
		GridPanel gridPanel = mPSMainService.getDataListGridByCaller(caller, condition, _self);
		modelMap.put("fields", gridPanel.getGridFields());
		modelMap.put("columns", gridPanel.getGridColumns());
		modelMap.put("data", gridPanel.getDataString());
		// grid行选择之后，要从selModel里面得到数据的字段，例如pu_id
		modelMap.put("keyField", gridPanel.getKeyField());
		// 同上，与主表字段对应的从表字段，方便从表的数据查询
		modelMap.put("pfField", gridPanel.getPfField());
		modelMap.put("url", gridPanel.getUrl());
		modelMap.put("relative", gridPanel.getRelative());
		modelMap.put("vastbutton", gridPanel.getVastbutton());
		return modelMap;
	}

	@RequestMapping("pm/deskproduct/getMPSPRonorderCount.action")
	@ResponseBody
	public Map<String, Object> getMakeCommitCount(String caller, String condition, String distinct) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("count", mPSMainService.getMPSPRonorder(caller, condition));
		return modelMap;
	}

	/**
	 * 生成excel导出
	 * 
	 * @throws IOException
	 */
	@RequestMapping("/pm/deskproduct/pronorder/create.xls")
	public ModelAndView createExcel(HttpServletResponse response, HttpServletRequest request, String caller, String title, String columns,
			String condition) throws IOException {
		condition = new String(condition.getBytes("ISO-8859-1"), "UTF-8");
		condition = condition.replace("%3D", "=");
		columns = new String(columns.getBytes("ISO-8859-1"), "UTF-8");
		GridPanel gridPanel = mPSMainService.getMPSPRonorder(caller, condition, false, 1, ExcelUtil.maxSize);
		return new ModelAndView(new ExcelUtil(BaseUtil.parseGridStoreToMaps(columns), BaseUtil.parseGridStoreToMaps(gridPanel
				.getDataString()), title, SystemSession.getUser(), null).getView());
	}

	@RequestMapping(value = "pm/deskproduct/getMPSPRonorder.action")
	@ResponseBody
	public Map<String, Object> getMakeCommits(HttpServletRequest req, String caller, String condition, int page, int pageSize) {
		Boolean _self = (Boolean) req.getAttribute("_self");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// int enid = (Integer) session.getAttribute("en_uu");
		GridPanel gridPanel = mPSMainService.getMPSPRonorder(caller, condition, _self, page, pageSize);
		modelMap.put("fields", gridPanel.getGridFields());
		modelMap.put("columns", gridPanel.getGridColumns());
		modelMap.put("data", gridPanel.getDataString());
		// grid行选择之后，要从selModel里面得到数据的字段，例如pu_id
		modelMap.put("keyField", gridPanel.getKeyField());
		// 同上，与主表字段对应的从表字段，方便从表的数据查询
		modelMap.put("pfField", gridPanel.getPfField());
		modelMap.put("url", gridPanel.getUrl());
		modelMap.put("relative", gridPanel.getRelative());
		modelMap.put("vastbutton", gridPanel.getVastbutton());
		return modelMap;
	}

	/**
	 * 下达替代料
	 * */
	@RequestMapping(value = "pm/mps/turnReplaceProd.action")
	@ResponseBody
	public Map<String, Object> turnReplaceProd(String caller, String data, String apdata, String purchasecode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = mPSMainService.turnReplaceProd(data, apdata, purchasecode, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转下达制造状态
	 * */
	@RequestMapping(value = "pm/mps/turnmake.action")
	@ResponseBody
	public Map<String, Object> mpsdesk_turnmake(String caller, String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String str = mPSMainService.mpsdesk_turnmake(code, caller);
		modelMap.put("message", str);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转下达请购状态
	 * */
	@RequestMapping(value = "pm/mps/turnpurchase.action")
	@ResponseBody
	public Map<String, Object> mpsdesk_turnpurchase(String caller, String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String str = mPSMainService.mpsdesk_turnpurchase(code, caller);
		modelMap.put("message", str);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转下达请购状态
	 * */
	@RequestMapping(value = "pm/mps/turnpurchaseforecast.action")
	@ResponseBody
	public Map<String, Object> mpsdesk_turnpurchaseforecast(String caller, String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String str = mPSMainService.mpsdesk_turnpurchaseforecast(code, caller);
		modelMap.put("message", str);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 取数量合计
	 * */
	@RequestMapping(value = "pm/mps/getSum.action")
	@ResponseBody
	public Map<String, Object> getSum(HttpSession session, String fields, String caller, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String data = mPSMainService.getSum(fields, caller, condition);
		modelMap.put("data", data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * MRP供应的内部交易转请购
	 * */
	@RequestMapping(value = "pm/mps/turnsupplytoneed.action")
	@ResponseBody
	public Map<String, Object> turnSupplyToNeedNeedThrow(String caller, String mainCode, String gridStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String data = mPSMainService.turnSupplyToNeed(caller, gridStore, mainCode);
		modelMap.put("data", data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 在订单异动分析的时候获得小于当前计划编号的最大的计划编号
	 * */
	@RequestMapping(value = "pm/mps/getMaxCode.action")
	@ResponseBody
	public Map<String, Object> getMaxMCode(String caller, String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String data = mPSMainService.getMaxMcode(caller, code);
		modelMap.put("data", data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 订单异动分析,对比本计划与前一计划的差异数
	 * */
	@RequestMapping(value = "pm/mps/OrderAnalysis.action")
	@ResponseBody
	public Map<String, Object> orderAnalysis(String condition, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", mPSMainService.getGridData(caller, condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 库存运算及上架
	 * 
	 * @param caller
	 * @param code
	 * @return
	 * @author XiaoST 2016年8月17日 上午11:50:20
	 */
	@RequestMapping("pm/mps/RunMrpAndGoods.action")
	@ResponseBody
	public Map<String, Object> RunMrpAndGoods(String caller, String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String str = mPSMainService.RunMrpAndGoods(code, caller);
		modelMap.put("message", str);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转上架
	 * 
	 * @param caller
	 * @param mainCode
	 * @param gridStore
	 * @param toWhere
	 * @param toCode
	 * @param condition
	 * @return
	 */
	@RequestMapping("pm/MPSMain/TurnGoodsUp.action")
	@ResponseBody
	public Map<String, Object> TurnGoodsUp(String caller, String mainCode, String gridStore, String toCode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("log", mPSMainService.TurnGoodsUp(caller, mainCode, gridStore, toCode));
		return modelMap;
	}

	/**
	 * 上架批量界面，用户修改锁库数量更新productonhand po_lockqty字段
	 * 
	 * @return
	 */
	@RequestMapping("pm/mps/updatePoLockqty.action")
	@ResponseBody
	public Map<String, Object> updatePoLockqty(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		mPSMainService.updatePoLockqty(caller, data);
		return modelMap;
	}

	/**
	 * 转ERP器件入库申请
	 * 
	 * @param caller
	 * @param gridStore
	 * @return
	 */
	@RequestMapping("pm/MPSMain/TurnDeviceInApply.action")
	@ResponseBody
	public Map<String, Object> TurnDeviceInApply(String caller, String gridStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("log", mPSMainService.TurnDeviceInApply(caller, gridStore));
		return modelMap;
	}
	
	/**
	 * 获取MRP工作台 显示的警告
	 * 
	 * @return
	 */
	@RequestMapping("pm/mps/getSeriousWarn.action")
	@ResponseBody
	public Map<String, Object> getSeriousWarn(String caller, String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> warn = mPSMainService.getSeriousWarn(caller, code);
		modelMap.put("data", warn);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * @author wsy
	 * 反馈编号：2017110437
	 */
	@RequestMapping("pm/MPSMain/ThrowCancel.action")
	@ResponseBody
	public Map<String,Object> throwCancle(String caller,String gridStore){
		Map<String,Object> modelMap = new HashMap<String,Object>();
		String log = mPSMainService.throwCancle(gridStore);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
}
