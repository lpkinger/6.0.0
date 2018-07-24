package com.uas.erp.controller.crm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.crm.VisitRecordService;

@Controller
public class VisitRecordController extends BaseController {
	@Autowired
	private VisitRecordService visitRecordService;

	@Autowired
	private BaseDao baseDao;
	
	/**
	 * 保存
	 */
	@RequestMapping("/crm/customermgr/saveVisitRecord.action")
	@ResponseBody
	public Map<String, Object> save(HttpServletRequest request,String formStore, String param1,
			String param2, String param3, String param4, String param5,
			String param6, String param7, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] params = new String[] { param1, param2, param3, param4,
				param5, param6, param7 };
		visitRecordService.saveVisitRecord(formStore, params, caller);
		
		
		//更新客户最后跟进时间和商机阶段
		Map<Object,Object> map = BaseUtil.parseFormStoreToMap(formStore);
		List<String> updateSql = new ArrayList<String>();
		if(map.get("vr_visittime")!=null){
			updateSql.add("update customer set cu_lastdate=to_date('"+map.get("vr_visittime")+"','yyyy-mm-dd hh24:mi:ss') where cu_code='"+map.get("vr_cuuu")+"'");
		}	
		if(map.get("vr_nichestep")!=null){
			//如果商机阶段大于客户的商机阶段，则客户的商机阶段
			Object stage = baseDao.getFieldDataByCondition("businesschancestage","bs_detno", "bs_name='" + map.get("vr_nichestep") + "'");
			Object cuStage = baseDao.getFieldDataByCondition("businesschancestage left join customer on bs_name=cu_nichestep", "bs_detno", "cu_code='" + map.get("vr_cuuu")+"'");
			if(stage!=null&&cuStage!=null){
				if (Integer.parseInt(cuStage.toString()) < Integer.parseInt(stage.toString())) {				
					updateSql.add("update customer set cu_nichestep='" + map.get("vr_nichestep") + "' where cu_code='"+map.get("vr_cuuu")+"'");
				}
			}
		}
		baseDao.execute(updateSql);
		
		modelMap.put("sessionId", request.getSession().getId());
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/crm/customermgr/saveVisitRecord2.action")
	@ResponseBody
	public Map<String, Object> save2(String formStore, String param1,
			String param2, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] params = new String[] { "", "", "", "", param1, "", param2 };
		visitRecordService.saveVisitRecord(formStore, params, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 根据填写的客户和录入人，获取符合条件的最新记录。再一次插入数据库中，简化了录入工作
	 */
	@RequestMapping("/crm/customermgr/autoSaveVisitRecord.action")
	@ResponseBody
	public Map<String, Object> autoSave(String vr_cuuu, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		int vr_id = visitRecordService.autoSave(caller, vr_cuuu);
		modelMap.put("success", true);
		modelMap.put("vr_id", vr_id);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/crm/customermgr/deleteVisitRecord.action")
	@ResponseBody
	public Map<String, Object> deleteVisitRecord(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		visitRecordService.deleteVisitRecord(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/crm/customermgr/updateVisitRecord.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param1,
			String param2, String param3, String param4, String param5,
			String param6, String param7, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] params = new String[] { param1, param2, param3, param4,
				param5, param6, param7 };
		visitRecordService.updateVisitRecordById(formStore, params, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/crm/customermgr/updateVisitRecord2.action")
	@ResponseBody
	public Map<String, Object> update2(String formStore, String param1,
			String param2, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] params = new String[] { "", "", "", "", param1, "", param2 };
		visitRecordService.updateVisitRecordById(formStore, params, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新亮点
	 */
	@RequestMapping("/crm/customermgr/updateVisitRecordGood.action")
	@ResponseBody
	public Map<String, Object> updateGood(int id, String vr_good, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		visitRecordService.updateGood(id, vr_good, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新评价评分
	 */
	@RequestMapping("/crm/customermgr/updateVisitRecordPingjia.action")
	@ResponseBody
	public Map<String, Object> updatePingjia(int id, String vr_newtitle,
			String vr_purpose, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		visitRecordService.updatePingjia(id, vr_newtitle, vr_purpose, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/crm/customermgr/submitVisitRecord.action")
	@ResponseBody
	public Map<String, Object> submitVisitRecord(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		visitRecordService.submitVisitRecord(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/crm/customermgr/resSubmitVisitRecord.action")
	@ResponseBody
	public Map<String, Object> resSubmitVisitRecord(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		visitRecordService.resSubmitVisitRecord(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/crm/customermgr/auditVisitRecord.action")
	@ResponseBody
	public Map<String, Object> auditVisitRecord(int id, String vr_newtitle,
			String vr_purpose, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		visitRecordService.auditVisitRecord(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/customermgr/resAuditVisitRecord.action")
	@ResponseBody
	public Map<String, Object> resAuditVisitRecord(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		visitRecordService.resAuditVisitRecord(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转差旅报销申请
	 */
	@RequestMapping("/crm/customermgr/turnFeePlease.action")
	@ResponseBody
	public Map<String, Object> turnFeePlease(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = visitRecordService.turnFeePlease(id, caller);
		modelMap.put("success", true);
		modelMap.put("log", log);
		return modelMap;
	}
}
