package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.DataListDetail;
import com.uas.erp.service.common.SingleFormItemsService;
import com.uas.erp.service.hr.HrJobService;
import com.uas.erp.service.hr.JobDocumentListPowerService;
import com.uas.erp.service.hr.JobDocumentPowerService;
import com.uas.erp.service.hr.JobPowerService;

@Controller
public class HrJobController {
	@Autowired
	private HrJobService hrJobService;
	@Autowired
	private JobPowerService jobPowerService;
	@Autowired
	private JobDocumentPowerService jobDocumentPowerService;
	@Autowired
	private JobDocumentListPowerService jobDocumentListPowerService;
	@Autowired
	private SingleFormItemsService singleFormItemsService;

	/**
	 * 保存HrJob
	 */
	@RequestMapping("/hr/employee/saveHrJob.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrJobService.saveHrJob(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/employee/updateHrJob.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrJobService.updateHrJobById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/employee/deleteHrJob.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		hrJobService.deleteHrJob(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * @param caller
	 */
	@RequestMapping("/hr/employee/getHrJob.action")
	@ResponseBody
	public Map<String, Object> getHrJob(String caller, String utype) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap= jobPowerService.getDetailGrids(caller);
		modelMap.put("positionpower", jobPowerService.getPositionPowersByCaller(caller));
		modelMap.put("hrjob", hrJobService.getHrJobs());
		
		if (utype == null || "".equals(utype)) {
			modelMap.put("formdetail", jobPowerService.getFormDetails(caller));
			modelMap.put("relativeSearch", jobPowerService.getRelativeSearchs(caller));//关联查询
		} else if ("list".equals(utype)) {
			Map<String, List<DataListDetail>> obj = jobPowerService.getDataList(caller);
			modelMap.put("datalist", obj.get("datalist"));
			modelMap.put("relativedatalist", obj.get("relativedatalist"));
		}
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * @param caller
	 */
	@RequestMapping("/hr/employee/getHrRole.action")
	@ResponseBody
	public Map<String, Object> getHrRole(String caller, String utype) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//返回grid数据
		modelMap= jobPowerService.getDetailGrids(caller);
		//获取角色及角色权限
		modelMap.put("role", hrJobService.getRoles());
		modelMap.put("rolepower", jobPowerService.getRolePowersByCaller(caller));
		
		if (utype == null || "".equals(utype)) {
			modelMap.put("formdetail", jobPowerService.getFormDetails(caller));
			modelMap.put("relativeSearch", jobPowerService.getRelativeSearchs(caller));//关联查询
		} else if ("list".equals(utype)) {
			Map<String, List<DataListDetail>> obj = jobPowerService.getDataList(caller);
			modelMap.put("datalist", obj.get("datalist"));
			modelMap.put("relativedatalist", obj.get("relativedatalist"));
		}
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * @param caller
	 */
	@RequestMapping("/hr/employee/getSelfPower.action")
	@ResponseBody
	public Map<String, Object> getPersonalPowerSet(String caller, String utype, String emid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (utype == null || "".equals(utype)) {
			modelMap= jobPowerService.getDetailGrids(caller);
			modelMap.put("formdetail", jobPowerService.getFormDetails(caller));
			//modelMap.put("detailgrid", jobPowerService.getDetailGrids(caller));
		} else if ("list".equals(utype)) {
			modelMap.put("datalist", jobPowerService.getDataList(caller));
		}
		modelMap.put("personalpower", jobPowerService.getPersonalPowersByEm(caller, emid));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * @param id
	 *            jo_id
	 */
	@RequestMapping("/hr/employee/getHrJobLimits.action")
	@ResponseBody
	public Map<String, Object> getHrJobLimits(String caller, int id, String utype, Boolean _self) {
		Map<String, Object> modelMap = null;
		if (_self) {
			modelMap = hrJobService.getSelfLimitFieldsByCaller(caller, id, utype);
		} else {
			modelMap = hrJobService.getLimitFieldsByCaller(caller, id, utype);
		}
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * @param id
	 *            ro_id
	 */
	@RequestMapping("/hr/employee/getHrRoleLimits.action")
	@ResponseBody
	public Map<String, Object> getHrRoleLimits(String caller, int id, String utype, Boolean _self) {
		Map<String, Object> modelMap = null;
		if (_self) {
			modelMap = hrJobService.getSelfLimitFieldsByCaller(caller, id, utype);
		} else {
			//modelMap = hrJobService.getLimitFieldsByCaller(caller, id, utype);
			modelMap = hrJobService.getRoleLimitFieldsByCaller(caller, id, utype);
		}
		modelMap.put("success", true);
		return modelMap;
	}

	
	/**
	 * @param
	 */
	@RequestMapping("/hr/employee/saveHrJobLimits.action")
	@ResponseBody
	public Map<String, Object> saveHrJobLimits(HttpSession session, String data, String caller, String relativeCaller, int id, Boolean _self,Boolean islist) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jobPowerService.saveLimitFields(data, caller, relativeCaller, id, _self,islist);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * @param
	 */
	@RequestMapping("/hr/employee/saveHrRoleLimits.action")
	@ResponseBody
	public Map<String, Object> saveHrRoleLimits(HttpSession session, String data, String caller, String relativeCaller, int id, Boolean _self,Boolean islist) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jobPowerService.saveRoleLimitFields(data, caller, relativeCaller, id, _self,islist);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * @param
	 */
	@RequestMapping("/hr/employee/saveSpecialPower.action")
	@ResponseBody
	public Map<String, Object> saveSpecialPower(String caller, String specials, String limits, int id, Boolean _self) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (specials != null && specials.length() > 2)
			jobPowerService.saveSpecialPower(caller,specials, id, _self);
		if (limits != null && limits.length() > 2)
			singleFormItemsService.saveRelativeSearchLimit(limits, id, _self);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * @param
	 */
	@RequestMapping("/hr/employee/saveRoleSpecialPower.action")
	@ResponseBody
	public Map<String, Object> saveRoleSpecialPower(String caller, String specials, String limits, int id, Boolean _self) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (specials != null && specials.length() > 2)
			jobPowerService.saveRoleSpecialPower(caller,specials, id, _self);
		if (limits != null && limits.length() > 2)
			singleFormItemsService.saveRelativeSearchLimit(limits, id, _self);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * @param id
	 *            documentPowerID
	 */
	@RequestMapping("/hr/employee/getHrJob_dcp.action")
	@ResponseBody
	public Map<String, Object> getHrJob_DCP(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("hrjob", hrJobService.getHrJobs());
		modelMap.put("documentpositionpower", jobDocumentPowerService.getDocumentPositionPowersByDCPID(id));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * @param id
	 *            documentList ID
	 */
	@RequestMapping("/hr/employee/getHrJob_dcl.action")
	@ResponseBody
	public Map<String, Object> getHrJob_DCL(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("hrjob", hrJobService.getHrJobs());
		modelMap.put("documentlistpower", jobDocumentListPowerService.getDocumentListPowersByDCLID(id));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 权限全部覆盖
	 */
	@RequestMapping(value = "/hr/employee/vastPostPower.action")
	@ResponseBody
	public Map<String, Object> vastPostPower(HttpSession session, String caller, String to) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", jobPowerService.vastPostPower(caller, to));
		modelMap.put("success", true);
		return modelMap;
	}
	
    /**刷新个人权限、岗位权限*/
	@RequestMapping(value = "/hr/employee/vastRefreshPower.action")
	@ResponseBody
	public Map<String, Object> vastRefreshPower(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jobPowerService.vastRefreshPower();
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * @param caller
	 */
	@RequestMapping("/hr/employee/getHrJobs.action")
	@ResponseBody
	public Map<String, Object> getHrJob() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", hrJobService.getHrJobs());
		modelMap.put("success", true);
		return modelMap;
	}
}
