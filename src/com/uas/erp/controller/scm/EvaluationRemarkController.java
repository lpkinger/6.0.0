package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.EvaluationRemarkService;

@Controller
public class EvaluationRemarkController extends BaseController {
	@Autowired
	private EvaluationRemarkService evaluationRemarkService;

	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/updateEvaluationRemark.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		evaluationRemarkService.updateEvaluationById(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}	
}
