package com.uas.erp.controller.drp;

import com.uas.erp.model.DataListCombo;
import com.uas.erp.service.drp.AskRepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AskRepairController {
	@Autowired
	private AskRepairService askRepairService;
	/**
	 * 保存
	 */
	@RequestMapping("/drp/aftersale/saveAskRepair.action")
	@ResponseBody
	public Map<String, Object> save( String formStore, String param,String caller) {
		
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		askRepairService.saveAskRepair(formStore, param, caller );
		modelMap.put("success", true);
		return modelMap;
	}

    /**
	 * 修改
	 */
	@RequestMapping("/drp/aftersale/updateAskRepair.action")
	@ResponseBody
	public Map<String, Object> update( String formStore, String param,String caller) {
		
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		askRepairService.updateAskRepair(formStore, param,  caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/drp/aftersale/deleteAskRepair.action")
	@ResponseBody
	public Map<String, Object> delete( int id,String caller) {
		
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		askRepairService.deleteAskRepair(id,caller  );
		modelMap.put("success", true);
		return modelMap;
	}

    /**
	 * 提交
	 */
	@RequestMapping("/drp/aftersale/submitAskRepair.action")
	@ResponseBody
	public Map<String, Object> submitAskRepair( int id,String caller) {
		
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		askRepairService.submitAskRepair(id, caller );
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/drp/aftersale/resSubmitAskRepair.action")
	@ResponseBody
	public Map<String, Object> resSubmitAskRepair( int id,String caller) {
		
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		askRepairService.resSubmitAskRepair(id, caller );
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/drp/aftersale/auditAskRepair.action")
	@ResponseBody
	public Map<String, Object> auditAskRepair( int id,String caller) {
		
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		askRepairService.auditAskRepair(id,caller  );
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/drp/aftersale/resAuditAskRepair.action")
	@ResponseBody
	public Map<String, Object> resAuditAskRepair( int id,String caller) {
		
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		askRepairService.resAuditAskRepair(id, caller );
		modelMap.put("success", true);
		return modelMap;
	}

    /**
     * 获取派工单类型
     * @return
     */
    @RequestMapping("/drp/aftersale/CustomerRepair2OrderType.action")
	@ResponseBody
    public Map<String, Object> getRepairOrderType( String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<DataListCombo> combos = askRepairService.getRepairOrderType(caller);
        modelMap.put("rotype", combos);
		modelMap.put("success", true);
		return modelMap;
    }

    /**
     *
     * @param session
     * @param crid 报修单ID
     * @param em_uu 维修人UU
     * @param em_name 维修人姓名
     * @param rotype 单据类型
     * @param crdids 报修单明细ID列表
     * @return
     */
    @RequestMapping("/drp/aftersale/turnRepairOrder.action")
    @ResponseBody
    public Map<String, Object> turnRepairOrder( int crid, int em_uu, String em_name, String rotype, String crdids,String caller) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        
        askRepairService.turnRepairOrder(caller, crid, em_uu, em_name, rotype, crdids);
		modelMap.put("success", true);
        return modelMap;
    }
    
    /**
    *
    * @param session
    * @param crid 报修单ID
    * @param em_uu 维修人UU
    * @param em_name 维修人姓名
    * @param rotype 单据类型
    * @param crdids 报修单明细ID列表
    * @return
    */
   @RequestMapping("/drp/aftersale/batchTurnRepairOrder.action")
   @ResponseBody
   public Map<String, Object> batchTurnRepairOrder( String caller, String data) {
       Map<String, Object> modelMap = new HashMap<String, Object>();
       
       String log= askRepairService.batchTurnRepairOrder( caller, data);
       modelMap.put("log", log);
	   modelMap.put("success", true);
       return modelMap;
   }
   /**
	 * 批量转分检 报修单批量转分检
	 */
	@RequestMapping(value = "/drp/vastTurnPartCheck.action")
	@ResponseBody
	public Map<String, Object> vastTurnOaapplicate( String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", askRepairService.batchTurnPartCheck(data,  caller));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 确认申请
	 */
	@RequestMapping("/drp/confirmCustomerRepair.action")  
	@ResponseBody 
	public Map<String, Object> confirm( int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		askRepairService.confirmCustomerRepair(id, caller );
		modelMap.put("success", true);
		return modelMap;
	}
}
