package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.MakeCraftPieceWorkService;

@Controller
public class MakeCraftPieceWorkController {
	
	@Autowired
	private MakeCraftPieceWorkService MakeCraftPieceWorkService ;
	/**
	 * 制造工序明细表的更新操作
	 *
	 * @param caller 表名
	 * 
	 * @param formStore 接收form的数据
	 * 
	 * @param gridStore 接收grid中的数据
	 * */
	@RequestMapping("/pm/make/updateMakeCraftPieceWork.action")
	@ResponseBody
	public Map<String, Object> update(String caller,String formStore,String param){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeCraftPieceWorkService.updateMakeCraftPieceWorkChange(caller,formStore,param);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/pm/make/deleteMakeCraftPieceWork.action")
	@ResponseBody
	public Map<String, Object> deleteDetail(String caller,int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeCraftPieceWorkService.deleteDetail(caller,id);		
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/pm/make/loadPeopleMakeCraftPieceWork.action")
	@ResponseBody
	public Map<String, Object> loadPeople(String makecode,String prodcode){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakeCraftPieceWorkService.loadPeople(makecode,prodcode);
		modelMap.put("success", true);
		return modelMap;
	}
}
