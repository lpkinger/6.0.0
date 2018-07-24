package com.uas.mobile.controller.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.DbfindSetUiDao;
import com.uas.erp.model.DBFindSetUI;
import com.uas.erp.model.DataListCombo;
import com.uas.erp.model.Dbfind;
import com.uas.erp.model.GridColumns;
import com.uas.erp.model.GridFields;
import com.uas.erp.service.common.DbfindService;
import com.uas.mobile.model.Employee;
import com.uas.mobile.service.DeviceService;

@Controller
public class DeviceController {
	@Autowired
	private DeviceService deviceService;
	@Autowired
	private DbfindService dbfindService;
	@Autowired
	private DbfindSetUiDao dbfindSetUiDao;
	
	@RequestMapping(value = "/mobile/device/getDeviceInfo.action")
	@ResponseBody
	public Map<String,Object> getDeviceInfo(String decode) {
		if (decode == null || ("").equals(decode)) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请输入设备编号");
		}
		Map<String,Object> map=new HashMap<String, Object>(); 
		map.put("data", deviceService.getgetDeviceInfo(decode));
		map.put("success", true);
		return map;
	}
	
	@RequestMapping(value = "/mobile/device/getCheckQty.action")
	@ResponseBody
	public Map<String,Object> getCheckQty(String caller,Integer id) {
		if (caller == null || ("").equals(caller) || id == 0) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		Map<String,Object> map=new HashMap<String, Object>(); 
		map.put("data", deviceService.getCheckQty(caller,id));
		map.put("success", true);
		return map;
	}
	
	@RequestMapping(value = "/mobile/device/deviceStock.action")
	@ResponseBody
	public Map<String,Object> deviceStock(String caller,Integer id,String de_code) {
		if (caller == null || ("").equals(caller) || id == 0) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}else if(de_code == null || ("").equals(de_code) ){
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请输入设备编号");
		}
		Map<String,Object> map=new HashMap<String, Object>(); 
		map.put("data",  deviceService.deviceStock(caller,id,de_code));
		map.put("success", true);
		return map;
	}
	
	@RequestMapping(value = "/mobile/device/saveAndSubmitDeviceStock.action")
	@ResponseBody
	public Map<String,Object> saveAndSubmitDeviceStock(String caller,String formStore) {
		if (caller == null || ("").equals(caller)) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的caller");
		}else if(formStore == null || ("").equals(formStore)){
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的数据");
		}
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = deviceService.saveAndSubmitDeviceStock(caller,formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping(value = "/mobile/device/saveAndSubmitDeviceChange.action")
	@ResponseBody
	public Map<String,Object> saveAndSubmitDeviceChange(String caller,String formStore) {
		if (caller == null || ("").equals(caller)) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的caller");
		}else if(formStore == null || ("").equals(formStore)){
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的数据");
		}
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = deviceService.saveAndSubmitDeviceChange(caller,formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//设备管理更新提交方法
	@RequestMapping("/mobile/device/updateAndSubmitDeviceChange.action")
	@ResponseBody
	public Map<String,Object> updateAndSubmitDeviceChange(HttpServletRequest request,String caller,String formStore){			
		if(request.getSession().getAttribute("employee")==null) BaseUtil.showError("会话已断开!");
		Map<String,Object> modelMap=new HashMap<String,Object>();
		modelMap=deviceService.updateAndSubmitDeviceChange(caller,formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/mobile/device/confirmDeal.action")
	@ResponseBody
	public Map<String,Object> confirmDeal(String caller,Integer id) {
		if (caller == null || ("").equals(caller)) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的caller");
		}else if(id == null || ("").equals(id) || id == 0){
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的数据");
		}
		Map<String, Object> modelMap = new HashMap<String, Object>();
		 deviceService.confirmDeal(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping(value = "/mobile/device/getDevInfomation.action")
	@ResponseBody
	public Map<String,Object> getDevInfomation(String decode) {
		if (decode == null || ("").equals(decode) ) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "设备编号不能为空");
		}
		Map<String,Object> map=new HashMap<String, Object>(); 
		map.put("data",  deviceService.getDevInfomation(decode));
		map.put("success", true);
		return map;
	}
	
	//线体生产设备需求匹配
	@RequestMapping(value = "/mobile/device/getDevModelInfo.action")
	@ResponseBody
	public Map<String,Object> getDevModelInfo(String centercode,String linecode,String workshop,String devmodel) {
		if (centercode == null || ("").equals(centercode) ) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "部门不能为空");
		}else if(linecode == null || ("").equals(linecode) ){
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "线别不能为空");
		}else if(workshop == null || ("").equals(workshop) ){
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "车间不能为空");
		}else if(devmodel == null || ("").equals(devmodel) ){
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "机型不能为空");
		}
		Map<String,Object> map=new HashMap<String, Object>(); 
		map.put("data",  deviceService.getDevModelInfo(centercode,linecode,workshop,devmodel));
		map.put("success", true);
		return map;
	}

	/**
	 * 手机端放大镜模糊查询
	 * @param table
	 * @param fields
	 * @return
	 */
	@RequestMapping(value = "/mobile/device/getSearchData.action")
	@ResponseBody
	public Map<String, Object> getSearchData(String condition,String name,String caller) {
		String data="";
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<String> result=dbfindService.getSearchData("","",condition,"1=1",name,caller,"form","true");
		if(result!=null && !result.isEmpty() && result.size()>1){
			data=result.get(4);
		}else if(result!=null && !result.isEmpty() && result.size()==1){
			data=result.get(0);
		}
		DBFindSetUI dbFindSetUI = null;
		String master = SpObserver.getSp();
		dbFindSetUI = dbfindSetUiDao.getDbFindSetUIByField(caller, name, master);
		List<Dbfind> dbfinds = new ArrayList<Dbfind>();
		String type = dbFindSetUI.getDs_type();
		String callers = dbFindSetUI.getDs_dlccaller();
		String[] names = dbFindSetUI.getDs_findtoui().split("#");
		if (type != null && callers != null) {
				String[] callerArr = dbFindSetUI.getDs_dlccaller().split(",");
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < callerArr.length; i++) {
					sb.append("'").append(callerArr[i].toString()).append("',");
				}
				callers = sb.substring(0, sb.length() - 1).toString();
		} else {
			String[] ff = null;
			for (int i = 0, len = names.length; i < len; i++) {
				ff = names[i].split(",");
				dbfinds.add(new Dbfind(ff.length > 1 ? ff[1] : null, ff[0]));
			}
		}
		modelMap.put("dbfinds",dbfinds);
		modelMap.put("data", data);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 *  设备送检单APP反提交
	 */
	@RequestMapping(value = "/mobile/device/deviceInspectRes.action")
	@ResponseBody
	public Map<String, Object> commonres(HttpServletRequest req,String caller, int id) {
		Map<String, Object> map = new HashMap<String, Object>();
		deviceService.deviceInspectRes(caller,id);
		map.put("sessionId", req.getSession().getId());
		map.put("success", true);
		return map;
	}
	
	/**
	 * 转报废单
	 */
	@RequestMapping("/mobile/device/turnScrap.action")
	@ResponseBody
	public Map<String, Object> turnScrap(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", deviceService.turnScrap(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 确认盘亏按钮
	 */
	@RequestMapping("/mobile/device/lossDevice.action")
	@ResponseBody
	public Map<String, Object> lossDevice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", deviceService.lossDevice(caller, id));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取设备明细
	 */
	@RequestMapping("/mobile/device/getDeviceAttribute.action")
	@ResponseBody
	public Map<String, Object> getDeviceAttribute(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", deviceService.getDeviceAttribute(caller, id));
		modelMap.put("success", true);
		return modelMap;
	}
}
