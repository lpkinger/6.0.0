package com.uas.erp.controller.plm;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.plm.MileStoneService;
@Controller
public class MileStoneController{
	@Autowired
	private MileStoneService mileStoneService;
	@RequestMapping("plm/task/saveMileStone.action")
	@ResponseBody
	public Map<String ,Object> saveMileStone(HttpSession session,String formStore,String param){
		Map<String,Object> modelMap=new HashMap<String, Object>();
		mileStoneService.saveMileStone(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("plm/task/deleteMileStone.action")  
	@ResponseBody 
	public Map<String, Object> deleteMileStone(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mileStoneService.deleteMileStone(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除明细行某一条数据
	 */
	@RequestMapping("plm/task/deleteDetailMileStone.action")  
	@ResponseBody 
	public Map<String, Object> deleteDetail(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mileStoneService.deleteMileStone(id);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("plm/task/updateMileStone.action")  
	@ResponseBody 
	public Map<String, Object> updateMileStone(HttpSession session, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mileStoneService.updateMileStone(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/task/submitMileStone.action")
	@ResponseBody
	public Map<String, Object> submitTask(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mileStoneService.submitMileStone(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/task/resSubmitMileStone.action")
	@ResponseBody
	public Map<String, Object> resSubmitTask(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mileStoneService.resSubmitMileStone(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/task/auditMileStone.action")
	@ResponseBody
	public Map<String, Object> auditTask(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mileStoneService.auditMileStone(id);
		modelMap.put("success", true);
		return modelMap;
	}

}
