package com.uas.mes.api.pda;
/**
 * 湿敏元件接口
 * @data  2016年12月21日 下午2:19:44
 */

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.uas.mes.api.core.BaseApiController;
import com.uas.pda.service.PdaMsdService;


@RestController("api.pdaMsdController")
@RequestMapping("/api/pda/msd")
public class PdaMsdController extends BaseApiController{
	@Autowired
	private PdaMsdService pdaMsdService;
	
	/**
	 * 根据
	 * @param code条码判断条码是否存在，是否湿敏元件，获取跟踪卡记录
	 * @return
	 */
	@RequestMapping(value="/getLog.action",method = RequestMethod.GET)
	public ModelMap getLog(String code){
		Map<String, Object> data = pdaMsdService.getLog(code);
		return success(data);
	}
	/**
	 * 确认入烘烤
	 * @param code
	 * @return
	 */
	@RequestMapping(value="/confirmInOven.action",method = RequestMethod.POST)
	public ModelMap confirmInOven(@RequestBody String data){
		pdaMsdService.confirmInOven(data);
		return success();
	}

	/**
	 * 确认出烘烤获取入烘烤记录，烘烤时长等信息
	 * @param code
	 * @return
	 */
	@RequestMapping("/pda/msd/getOvenTime.action")
	@ResponseBody
	public ModelMap getOvenTime(String code){
		return success(pdaMsdService.getOvenTime(code));
	}
	
	/**
	 * 确认出烘烤
	 * @param code
	 * @return
	 */
	@RequestMapping(value="/confirmOutOven.action",method = RequestMethod.GET)
	public ModelMap confirmOutOven(String code){		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");
		Map<String, Object> data = pdaMsdService.confirmOutOven(code);
		return success(data);
	}
	
	/**
	 * 根据条码号获取湿敏元件操作记录
	 * @param code
	 * @return
	 */
	@RequestMapping("/pda/out/loadMSDLog.action")
	@ResponseBody
	public ResponseEntity<ModelMap>  loadMSDLog(String code) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");
		ModelMap map = new ModelMap();
		map.put("message", pdaMsdService.loadMSDLog(code));
		return new ResponseEntity<ModelMap>(map, headers, HttpStatus.OK);
	}
}
