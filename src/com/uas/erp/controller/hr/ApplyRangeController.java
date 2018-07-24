package com.uas.erp.controller.hr;

import com.uas.erp.model.CheckBoxTree;
import com.uas.erp.service.hr.ApplyRangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ApplyRangeController {
	@Autowired
	private ApplyRangeService applyRangeService;

	@RequestMapping("/hr/attendance/getAllHrOrgsTreeWDM.action")
	@ResponseBody 
	public Map<String, Object> getAllHrOrgsTree(String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<CheckBoxTree> tree = applyRangeService.getAllHrOrgsTree(caller);
		modelMap.put("tree",tree);
		return modelMap;
	}

    @RequestMapping("/hr/attendance/AttendRangeSet.action")
    @ResponseBody
    public Map<String, Object> turnRepairWork(String caller, int aiid, String ids) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        applyRangeService.setEmpAttendItem(aiid, ids,caller);
		modelMap.put("success", true);
        return modelMap;
    }

}
