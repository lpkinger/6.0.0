package com.uas.mobile.controller.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FileUtil;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Employee;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.common.FilePathService;
import com.uas.mobile.model.CardLog;
import com.uas.mobile.model.Enterprise;
import com.uas.mobile.model.WorkDate;
import com.uas.mobile.service.SignCardLogService;
import com.uas.mobile.service.UserService;

/**
 * @author :LiuJie 时间: 2015年2月11日 下午2:08:53
 * @注释:考勤签到
 */
@Controller("signCardLogController")
public class SignCardLogController {
	@Autowired
	private EnterpriseService enterpriseService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private UserService userService;
	@Autowired
	private FilePathService filePathService;
	@Autowired
	private SignCardLogService signCardLogService;

	/*
	 * 获取员工上下班时间
	 */
	@RequestMapping("/mobile/getWorkDate.action")
	@ResponseBody
	public Map<String, Object> getWorkDateTime(HttpServletRequest request,
			String emcode, String date) {

		Map<String, Object> map = signCardLogService.getDutyTime(emcode, date);
		map.put("sessionId", request.getSession().getId());
		return map;
	}

	/*
	 * 获取班次
	 */
	@RequestMapping("/mobile/getAllWorkDate.action")
	@ResponseBody
	public Map<String, Object> getAllWorkDate(HttpServletRequest request,
			String emcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> list = null;
		list = signCardLogService.getAllWorkDate(emcode);
		modelMap.put("listdata", list);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/*
	 * 保存班次
	 */
	@RequestMapping("/mobile/saveWorkDate.action")
	@ResponseBody
	public Map<String, Object> saveWorkDateTime(HttpServletRequest request,
			String caller, String formStore) {
		Map<String, Object> map = signCardLogService.saveWorkDateTime(caller,
				formStore);
		map.put("success", true);
		map.put("sessionId", request.getSession().getId());
		return map;
	}

	/*
	 * 更新班次
	 */
	@RequestMapping("/mobile/updateWorkDate.action")
	@ResponseBody
	public Map<String, Object> updateWorkDateTime(HttpServletRequest request,
			String caller, String formStore) {
		Map<String, Object> map = signCardLogService.updateWorkDateTime(caller,
				formStore);
		map.put("sessionId", request.getSession().getId());
		map.put("success", true);
		return map;
	}

	// 删除班次
	@RequestMapping("/mobile/deleteWorkDate.action")
	@ResponseBody
	public Map<String, Object> deleteWorkDate(HttpServletRequest request,
			String caller, String wdcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = signCardLogService.deleteWorkDate(caller, wdcode);
		modelMap.put("sessionId", request.getSession().getId());
		modelMap.put("success", true);
		return modelMap;
	}
	//更新排班记录 接口
		@RequestMapping("/mobile/updateEmpWorkDate.action")
		@ResponseBody
		public Map<String, Object> updateEmpWorkDate(HttpServletRequest request,
				String deptcodes, String emcodes,String date,String workcode) {
			Map<String, Object> modelMap = new HashMap<String, Object>();
			modelMap = signCardLogService.updateEmpWorkDate(deptcodes, emcodes,date,workcode);
			modelMap.put("sessionId", request.getSession().getId());
			modelMap.put("success", true);
			return modelMap;
		}
		//删除排班记录接口
		@RequestMapping("/mobile/deleteEmpWorkDate.action")
		@ResponseBody
		public Map<String,Object> deleteEmpWorkDate(HttpServletRequest request,String deptcodes, String emcodes,String date,String workcode,String flag){
			Map<String,Object> modelMap = new HashMap<String,Object>();
			modelMap = signCardLogService.deleteEmpworkDate(deptcodes,emcodes,date,workcode,flag);
			modelMap.put("sessionId",request.getSession().getId());
			modelMap.put("success", true);
			return modelMap;
		}
	// 我的考勤规划接口
	@RequestMapping("/mobile/myComPlan.action")
	@ResponseBody
	public Map<String, Object> myComPlan(HttpServletRequest request,
			String emcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> list = null;
		list = signCardLogService.myComPlan(emcode);
		modelMap.put("data", list);
		modelMap.put("sessionId", request.getSession().getId());
		// modelMap.put("success",true);
		return modelMap;
	}

	/*
	 * 获取冲突人员和冲突部门
	 */
	@RequestMapping("/mobile/getManAndDefaultor.action")
	@ResponseBody
	public Map<String, Object> getManAndDefaultor(HttpServletRequest request,
			String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> list = null;
		list = signCardLogService.getManAndDefaultor(formStore);
		modelMap.put("listdata", list);
		modelMap.put("sessionId", request.getSession().getId());
		modelMap.put("success", true);
		return modelMap;
	}

	/*
	 * 保存打卡签到记录
	 */
	@RequestMapping("/mobile/saveCardLog.action")
	@ResponseBody
	public Map<String, Object> saveSignCardLogs(HttpServletRequest request,
			String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap=signCardLogService.saveCardLog(formStore, caller);
		//modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/*
	 * 保存外勤计划
	 */
	@RequestMapping("/mobile/addOutPlan.action")
	@ResponseBody
	public Map<String, Object> saveOutPlan(HttpServletRequest request,
			String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = signCardLogService.saveOutPlan(formStore, caller, param);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/*
	 * 保存外勤目的地
	 */
	@RequestMapping("/mobile/addOutAddress.action")
	@ResponseBody
	public Map<String, Object> saveOutAddress(HttpServletRequest request,
			String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = signCardLogService.saveOutAddress(formStore, caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/*
	 * 获取外勤目的地
	 */
	@RequestMapping(value = "/mobile/getOutAddressDate.action")
	@ResponseBody
	public Map<String, Object> getOutAddressDate(HttpServletRequest request,
			String condition, int pageIndex, int pageSize) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> modelMap = signCardLogService
				.getOutAddressDate(condition, pageIndex, pageSize);
		map.put("data", modelMap);
		return map;
	}

	/*
	 * 保存更新外勤设置
	 */
	@RequestMapping("/mobile/updateOutSet.action")
	@ResponseBody
	public Map<String, Object> updateOutSet(HttpServletRequest request,
			String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = signCardLogService.updateOutSet(formStore, caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/*
	 * 获取外勤设置
	 */
	@RequestMapping("/mobile/getOutSet.action")
	@ResponseBody
	public Map<String, Object> getOutSet(HttpServletRequest request,
			String condition) {

		Map<String, Object> map = signCardLogService.getOutSet(condition);
		map.put("sessionId", request.getSession().getId());
		return map;
	}

	/**
	 * @author Administrator
	 * @功能:获取员工班次信息，位置信息，签到成功后保存签到信息
	 * @param:en_code:员工号 LATITUDE:定位的纬度地址 LONGITUDE：定位的经度 isProxy:是否为代签
	 */
	@RequestMapping("/mobile/saveSign.action")
	@ResponseBody
	public Map<String, Object> signEventFromMobile(String en_code,
			String latitude, String longitude, String status, String isProxy,
			int imageId, String master) {
		SpObserver.putSp(master);
		CardLog cardLog = new CardLog();
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (en_code != null && latitude != null && longitude != null
				&& status != null && isProxy != null) {
			// 根据en_code查询班次信息，查询公司信息
			Employee employee = employeeService.getEmployeeByName(en_code);
			Date date = new Date();
			if (employee == null)
				modelMap.put("success", false);
			cardLog.setCl_cardcode(employee.getEm_cardcode());
			cardLog.setCl_emid(employee.getEm_id());
			cardLog.setCl_time(date);
			cardLog.setCl_status(status);
			cardLog.setCl_emcode(employee.getEm_code());
			cardLog.setCl_latitude(latitude);
			cardLog.setCl_longitude(longitude);
			if (isProxy.equals("true")) {
				cardLog.setCl_isproxy(1);
				cardLog.setCl_imageforproxy(imageId);
			} else {
				cardLog.setCl_isproxy(0);
			}
			userService.saveCardLogs(cardLog);
			modelMap.put("success", true);
			modelMap.put("result", "签到成功！");
			modelMap.put("time", new SimpleDateFormat("HH:mm:ss").format(date));
		} else {
			modelMap.put("success", false);
			modelMap.put("result", "签到失败！");
		}

		return modelMap;
	}

	// 查询员工的签到记录
	@RequestMapping("/mobile/getSignToMobile.action")
	@ResponseBody
	public Map<String, Object> selectSignToMobile(String en_code, int page,
			int pageSize) {
		System.err.println(page);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<CardLog> cLogs = null;
		int start = 0;
		int end = 0;
		if (page == 0)
			page = 1;
		start = (page - 1) * pageSize;
		end = page * pageSize;
		modelMap.put("success", false);
		if (en_code != null) {
			System.out.println("start=" + start + "\nend=" + end);
			cLogs = userService.selectCardLogsForEnCode(en_code, start, end);
			if (cLogs != null) {
				modelMap.put("success", true);
				modelMap.put("cardLogs", cLogs);
			}
		}
		return modelMap;
	}

	// 获取班次信息
	@RequestMapping("/mobile/getWorkDateToMobile.action")
	@ResponseBody
	public Map<String, Object> getWorkDateToMobile(String en_code, String master) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		WorkDate workDate = null;
		Employee employee = null;
		Enterprise enterprise = null;
		String en_longitude = null;
		String en_latitude = null;
		String en_distance = null;
		modelMap.put("success", false);
		if (en_code != null) {
			SpObserver.putSp(master);
			workDate = userService.getWorkDates(en_code);
			employee = employeeService.getEmployeeByName(en_code);
			if (employee != null) {
				enterprise = userService.getEnterprise(employee.getEm_enid());
				en_longitude = enterprise.getEn_longitude();
				en_latitude = enterprise.getEn_latitude();
				en_distance = enterprise.getEn_distanceforallow();

			}
		}
		if (workDate != null && en_longitude != null && en_latitude != null
				&& en_distance != null) {
			modelMap.put("success", true);
			modelMap.put("workDate", workDate);
			modelMap.put("latitude", en_latitude);
			modelMap.put("longitude", en_longitude);
			modelMap.put("distance", en_distance);
		}
		return modelMap;
	}

	/**
	 * 图片上传
	 */
	@RequestMapping("/mobile/sign_uploadPic.action")
	public @ResponseBody
	String uploadPic(String latitude, String longitude, String status,
			String isProxy, String em_code, MultipartFile img, String master) {
		SpObserver.putSp(master);
		return upload(latitude, longitude, status, isProxy, em_code, img,
				master);
	}

	public String upload(String latitude, String longitude, String status,
			String isProxy, String em_code, MultipartFile img, String master) {
		String filename = img.getOriginalFilename();
		long size = img.getSize();
		if (size > 104857600) {
			return "{error: '文件过大'}";
		}
		String path = FileUtil.saveFile(img, em_code);
		Employee employee = employeeService.getEmployeeByName(em_code);
		int id = filePathService.saveFilePath(path, (int) size, filename,
				employee);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String result = null;
		modelMap = signEventFromMobile(em_code, latitude, longitude, status,
				isProxy, id, master);
		if ((Boolean) modelMap.get("success")) {
			result = "代签成功！";
			return result;
		} else {
			result = "代签失败！";
			return result;

		}
	}

	// 判断是否是系统管理员
	@RequestMapping("/mobile/ifadmin.action")
	@ResponseBody
	public Map<String, Object> ifAdmin(HttpServletRequest request, String emcode) {
		Map<String, Object> modelMap = null;
		modelMap = signCardLogService.ifAdmin(emcode);
		modelMap.put("sessionId", request.getSession().getId());
		modelMap.put("success", true);
		return modelMap;
	}

	// 保存考勤设置
	@RequestMapping("/mobile/saveconfigs.action")
	@ResponseBody
	public Map<String, Object> saveConfigs(HttpServletRequest request,
			String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = signCardLogService.saveConfigs(caller, formStore);
		modelMap.put("sessionId", request.getSession().getId());
		modelMap.put("success", true);
		return modelMap;
	}

	// 保存考勤地址设置
	@RequestMapping("/mobile/savecomaddressset.action")
	@ResponseBody
	public Map<String, Object> savecomaddressset(HttpServletRequest request,
			String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = signCardLogService.savecomaddressset(caller, formStore);
		modelMap.put("sessionId", request.getSession().getId());
		modelMap.put("success", true);
		return modelMap;
	}

	// 更新考勤地址设置
	@RequestMapping("/mobile/updatecomaddressset.action")
	@ResponseBody
	public Map<String, Object> updatecomaddressset(HttpServletRequest request,
			String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = signCardLogService.updatecomaddressset(caller, formStore);
		modelMap.put("sessionId", request.getSession().getId());
		modelMap.put("success", true);
		return modelMap;
	}

	// 删除考勤地址设置
	@RequestMapping("/mobile/deletecomaddressset.action")
	@ResponseBody
	public Map<String, Object> deletecomaddressset(HttpServletRequest request,
			String caller, String id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = signCardLogService.deletecomaddressset(caller, id);
		modelMap.put("sessionId", request.getSession().getId());
		modelMap.put("success", true);
		return modelMap;
	}

	// 获取考勤地址设置
	@RequestMapping("/mobile/getcomaddressset.action")
	@ResponseBody
	public Map<String, Object> getcomaddressset(HttpServletRequest request,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> list = null;
		list = signCardLogService.getcomaddressset(condition);
		modelMap.put("listdata", list);
		modelMap.put("sessionId", request.getSession().getId());
		modelMap.put("success", true);
		return modelMap;
	}

	// 获取考勤设置
	@RequestMapping("/mobile/getconfigs.action")
	@ResponseBody
	public Map<String, Object> getConfigs(HttpServletRequest request,
			String condition) {
		Map<String, Object> modelMap = null;
		modelMap = signCardLogService.getConfigs(condition);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	// 更新考勤设置
	@RequestMapping("/mobile/updateconfigs.action")
	@ResponseBody
	public Map<String, Object> updateConfigs(HttpServletRequest request,
			String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		signCardLogService.updateConfigs(caller, formStore);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	// 自动打卡开关设置接口
	@RequestMapping("/mobile/autoCardLog.action")
	@ResponseBody
	public Map<String, Object> autoCardLog(HttpServletRequest request,
			String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		signCardLogService.autoCardLog(caller, formStore);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	// 判断用户在当前时间是否有有请假单或者外勤计划接口
	@RequestMapping("/mobile/ifInCompany.action")
	@ResponseBody
	public Map<String, Object> ifInCompany(HttpServletRequest request,
			String emcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("isOffline", signCardLogService.ifInCompany(emcode) ? "1"
				: "0");
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	// 判断用户是否需要考勤
	@RequestMapping("/mobile/ifneedsigncard.action")
	@ResponseBody
	public Map<String, Object> ifNeedSignCard(HttpServletRequest request,
			String emcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("isNeedSignCard",
				signCardLogService.ifNeedSignCard(emcode) ? "1" : "0");
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	// 个人考勤统计接口
	@RequestMapping("/mobile/getPersonAttend.action")
	@ResponseBody
	public Map<String, Object> getPersonAttend(HttpServletRequest request,
			String emcode, String yearmonth) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> list = null;
		list = signCardLogService.getPersonAttend(emcode, yearmonth);
		modelMap.put("listdata", list);
		modelMap.put("sessionId", request.getSession().getId());
		modelMap.put("success", true);
		return modelMap;
	}

	// 团队考勤统计接口
	@RequestMapping("/mobile/getTeamAttend.action")
	@ResponseBody
	public Map<String, Object> getTeamAttend(HttpServletRequest request,
			String emcode, String yearmonth) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = signCardLogService.getTeamAttend(emcode, yearmonth);
		modelMap.put("sessionId", request.getSession().getId());
		modelMap.put("success", true);
		return modelMap;
	}
	
	//获取某月有效打卡记录
	@RequestMapping("/mobile/getEffectiveWorkdata.action")
	@ResponseBody
	public Map<String, Object> effectiveWorkdata(String em_code, String date) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		try{
			modelMap.put("success", true);
			modelMap.put("listDatas", signCardLogService.effectiveWorkdata(em_code, date));
		}catch (Exception e){
			modelMap.put("success", false);
		}
		return modelMap;
	}
	
}
