package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.service.pm.BatchDealService;

@Controller("PmBatchDealController")
public class BatchDealController {
	@Autowired
	private BatchDealService batchDealService;

	/**
	 * 制造通知单批量转制造单
	 */
	@RequestMapping(value = "/pm/make/vastTurnMake.action")
	@ResponseBody
	public Map<String, Object> vastTurnMake(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.turnMake(data, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 制造单转生产领料单 委外单转委外领料单
	 * 
	 * @param wh
	 *            是否按仓库分组
	 * @param type
	 *            制造单、委外单...
	 */
	@RequestMapping("/pm/make/turnOut.action")
	@ResponseBody
	public Map<String, Object> turnOut(String data, boolean wh, String whman, String caller, String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.turnProdOut(data, wh, whman, type, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 生成拨出单，拨出仓库为mm_whcode，拨入仓库手工录入
	 * 
	 * @param inwhcode
	 *            拨入仓库编号
	 */
	@RequestMapping("pm/make/turnProdIOBC.action")
	@ResponseBody
	public Map<String, Object> turnProdIOBC(String data, String inwhcode, String caller, String whmancode, String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.turnProdIOBC(data, inwhcode, whmancode, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 制造单转生产退料单
	 * 
	 * @param wh
	 *            是否按仓库分组
	 * @param outtoint
	 *            是否水口料退料单
	 */
	@RequestMapping("/pm/make/turnIn.action")
	@ResponseBody
	public Map<String, Object> turnIn(String data, boolean wh, String caller, String type, boolean outtoint) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.turnProdIn(data, wh, type, caller, outtoint);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 制造单转生产报废单
	 * 
	 * @param wh
	 *            是否按仓库分组
	 */
	@RequestMapping("/pm/make/turnScrap.action")
	@ResponseBody
	public Map<String, Object> turnScrap(String data, String caller, String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.turnProdScrap(data, type, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 制造单转完工入库单
	 */
	@RequestMapping("/pm/make/turnMade.action")
	@ResponseBody
	public Map<String, Object> turnMade(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.turnMade(data, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 制造单转完工入库单——按流程单
	 */
	@RequestMapping("/pm/make/turnMadebyflow.action")
	@ResponseBody
	public Map<String, Object> turnMadebyflow(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.turnMadebyflow(data, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 物料mrp参数维护
	 * */
	@RequestMapping(value = "/pm/vastSaveProductMrpSet.action")
	@ResponseBody
	public Map<String, Object> vastSaveSale(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastSaveProductMrpSet(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/pm/mps/EndSaleForeCast.action")
	@ResponseBody
	public Map<String, Object> EndSaleForeCast(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.EndSaleForeCast(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/pm/mps/ResEndSaleForeCast.action")
	@ResponseBody
	public Map<String, Object> ResEndSaleForeCast(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.ResEndSaleForeCast(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 制造单转生产补料单 委外单转委外补料单
	 * 
	 * @param wh
	 *            是否按仓库分组
	 * @param type
	 *            制造单、委外单...
	 */
	@RequestMapping("/pm/make/turnAdd.action")
	@ResponseBody
	public Map<String, Object> turnAdd(String data, boolean wh, String caller, String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.turnProdAdd(data, wh, type, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 委外单批量转制造单
	 */
	@RequestMapping(value = "/pm/make/vastOSTurnMake.action")
	@ResponseBody
	public Map<String, Object> vastOSTurnMake(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.OSturnMake(data, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 制造单批量转FQC检验单
	 */
	@RequestMapping(value = "/pm/make/vastTurnQuaCheck.action")
	@ResponseBody
	public Map<String, Object> vastTurnQuaCheck(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.turnQuaCheck(data, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 生产检验单转完工入库单
	 */
	@RequestMapping("/pm/make/turnFinishIn.action")
	@ResponseBody
	public Map<String, Object> turnFinishIn(String data, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.turnFinishIn(data, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * maz 锤子科技 FQC转委外验收单、不良品入库
	 */
	@RequestMapping("/pm/make/OSturnFinishIn.action")
	@ResponseBody
	public Map<String, Object> OSturnFinishIn(String data, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.OSturnFinishIn(data, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 委外加工单批量转收料单
	 */
	@RequestMapping(value = "/pm/makeos/vastTurnAccept.action")
	@ResponseBody
	public Map<String, Object> vastTurnAccept(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.vastTurnAccept(data, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 拉式发料生成调拨单和领料单
	 * 
	 * @param bywhcode
	 *            是否按仓库产生独立出入库单据
	 * @param wipwhcode
	 *            线边仓编号
	 * @param maid
	 *            工单ID
	 */
	@RequestMapping("/pm/make/turnlssend.action")
	@ResponseBody
	public Map<String, Object> turnlssend(String caller, String data, boolean bywhcode, String wipwhcode, String maid,
			String departmentcode, String emcode, String cgycode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.turnlssend(data, bywhcode, wipwhcode, maid, departmentcode, emcode, cgycode, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 多工单拉式发料生成调拨单和领料单
	 * 
	 * @param bywhcode
	 *            是否按仓库产生独立出入库单据
	 * @param wipwhcode
	 *            线边仓编号
	 * @param maid
	 *            工单ID
	 */
	@RequestMapping("/pm/make/multiturnlssend.action")
	@ResponseBody
	public Map<String, Object> multiturnlssend(String caller, String data, boolean bywhcode, String wipwhcode, String maid,
			String departmentcode, String emcode, String cgycode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.multiturnlssend(data, bywhcode, wipwhcode, maid, departmentcode, emcode, cgycode, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 模具申请单批量生成模具采购单
	 */
	@RequestMapping(value = "/pm/mould/vastTurnPurMould.action")
	@ResponseBody
	public Map<String, Object> vastTurnPurMould(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.turnPurMould(data, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 模具采购单批量生成模具付款申请单
	 */
	@RequestMapping(value = "/pm/mould/vastToMouleFee.action")
	@ResponseBody
	public Map<String, Object> vastToMouleFee(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.vastToMouleFee(data, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 选择替代料批量确认投放数量
	 */
	@RequestMapping(value = "/pm/mrp/confirmThrowQty.action")
	@ResponseBody
	public Map<String, Object> confirmThrowQty(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.confirmThrowQty(data, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/pm/mould/productset/updatevend.action")
	@ResponseBody
	public Map<String, Object> updatevend(HttpSession session, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.updatevend(data);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/pm/mould/productset/updatecust.action")
	@ResponseBody
	public Map<String, Object> updatecust(HttpSession session, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.updatecust(data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 制造单批量下达作业
	 */
	@RequestMapping(value = "/scm/vastTurnMakeCraft.action")
	@ResponseBody
	public Map<String, Object> vastTurnMakeCraft(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.vastTurnMakeCraft(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 委外单转委外验收单
	 */
	@RequestMapping("/pm/make/turnProdIOMakeOS.action")
	@ResponseBody
	public Map<String, Object> turnProdIOMakeOS(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.turnProdIOMakeOS(data, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * refreshFeatureView
	 * */
	@RequestMapping("/pm/make/refreshFeatureView.action")
	@ResponseBody
	public Map<String, Object> refreshFeatureView(String caller, String ftcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.refreshFeatureView(ftcode, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * refreshFeatureViewProd
	 * */
	@RequestMapping("/pm/make/refreshFeatureViewProd.action")
	@ResponseBody
	public Map<String, Object> refreshFeatureViewProd(String caller, String ftcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.refreshFeatureViewProd(ftcode, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 商品批量下架
	 */
	@RequestMapping(value = "/pm/make/BatchGoodsOff.action")
	@ResponseBody
	public Map<String, Object> BatchGoodsOff(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.batchGoodsOff(data, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 制造单批量关闭
	 */
	@RequestMapping(value = "/pm/make/vastMakeClose.action")
	@ResponseBody
	public Map<String, Object> vastMakeClose(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastMakeClose(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 制造单批量打开
	 */
	@RequestMapping(value = "/pm/make/vastMakeOpen.action")
	@ResponseBody
	public Map<String, Object> vastMakeOpen(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastMakeOpen(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量更新替代料数量
	 */
	@RequestMapping(value = "/pm/make/updateMakeSubMaterial.action")
	@ResponseBody
	public Map<String, Object> updateMakeSubMaterial(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.updateMakeSubMaterial(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量设为主料
	 */
	@RequestMapping(value = "/pm/make/vastSetMain.action")
	@ResponseBody
	public Map<String, Object> vastSetMain(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastSetMain(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 加工委外单批量转加工验收单
	 */
	@RequestMapping(value = "/pm/mes/vastTurnProcessIn.action")
	@ResponseBody
	public Map<String, Object> vastTurnProcessIn(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.vastTurnProcessIn(data, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 工序委外单批量转收料单
	 */
	@RequestMapping(value = "/pm/mes/vastTurnAccept.action")
	@ResponseBody
	public Map<String, Object> vastMakeCraftTurnAccept(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.vastMakeCraftTurnAccept(data, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转补料单
	 */
	@RequestMapping("/pm/make/turnProdIOAdd.action")
	@ResponseBody
	public Map<String, Object> turnProdIOAdd(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.turnProdIOAdd(data, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转退料单
	 */
	@RequestMapping("/pm/make/turnProdIOReturn.action")
	@ResponseBody
	public Map<String, Object> turnProdIOReturn(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.turnProdIOReturn(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转领料单
	 */
	@RequestMapping("/pm/make/turnProdIOGet.action")
	@ResponseBody
	public Map<String, Object> turnProdIOGet(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.turnProdIOGet(data, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 制造单转生产报废单
	 */
	@RequestMapping("/pm/make/turnStockScrap.action")
	@ResponseBody
	public Map<String, Object> turnStockScrap(String data, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.turnStockScrap(data, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 车间作业单、工序委外单批量结案  maz 
	 */
	@RequestMapping("/pm/mes/batchEndMakeCraft.action")
	@ResponseBody
	public Map<String, Object> batchEndMakeCraft(String caller,String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.batchEndMakeCraft(caller , data);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 成套发料、补料界面的第二个grid的仓库更改反写回工单明细的仓库   
	 * @author zjh
	 */
	@RequestMapping("/pm/make/changeWhcode.action")
	@ResponseBody
	public Map<String, Object> changeWhcode(String isrep,String whcode,String mmid,String mpdetno) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.changeWhcode(isrep , whcode, mmid,mpdetno);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 制造ECN批量转取消执行
	 * @author zjh
	 */
	@RequestMapping("/pm/make/batchMakeECNCancelPerform.action")
	@ResponseBody
	public Map<String, Object> batchMakeECNCancelPerform(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.batchMakeECNCancelPerform(caller , data);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 制造ECN批量转执行
	 * @author zjh
	 */
	@RequestMapping("/pm/make/batchMakeECNTurnPerform.action")
	@ResponseBody
	public Map<String, Object> batchMakeECNTurnPerform(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.batchMakeECNTurnPerform(caller , data);
		modelMap.put("success", true);
		return modelMap;
	}
}
