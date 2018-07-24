package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MoveProductDetailService;

@Controller
public class MoveProductDetailController extends BaseController {
	@Autowired
	private MoveProductDetailService moveProductDetailService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/make/saveMoveProductDetail.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = moveProductDetailService
				.saveMoveProductDetail(formStore, param, caller);
		if(!"".equals(log)){
			modelMap.put("exceptionInfo", "AFTERSUCCESS"+log);
		}else{
			modelMap.put("success", true);
		}
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/pm/make/deleteMoveProductDetail.action")
	@ResponseBody
	public Map<String, Object> deleteMoveProductDetail(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		moveProductDetailService.deleteMoveProductDetail(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/make/updateMoveProductDetail.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = moveProductDetailService.updateMoveProductDetailById(formStore, param,
				caller);
		if(!"".equals(log)){
			modelMap.put("exceptionInfo", "AFTERSUCCESS"+log);
		}else{
			modelMap.put("success", true);
		}
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/make/submitMoveProductDetail.action")
	@ResponseBody
	public Map<String, Object> submitMoveProductDetail(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		moveProductDetailService.submitMoveProductDetail(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/make/resSubmitMoveProductDetail.action")
	@ResponseBody
	public Map<String, Object> resSubmitMoveProductDetail(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		moveProductDetailService.resSubmitMoveProductDetail(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/make/auditMoveProductDetail.action")
	@ResponseBody
	public Map<String, Object> auditMoveProductDetail(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		moveProductDetailService.auditMoveProductDetail(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/make/resAuditMoveProductDetail.action")
	@ResponseBody
	public Map<String, Object> resAuditMoveProductDetail(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		moveProductDetailService.resAuditMoveProductDetail(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 挪料单载入
	 */
	@RequestMapping("/pm/make/moveProduct.action")
	@ResponseBody
	public Map<String, Object> moveProduct(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id",
				moveProductDetailService.moveProduct(formStore, caller));
		modelMap.put("success", true);
		return modelMap;
	}
}
