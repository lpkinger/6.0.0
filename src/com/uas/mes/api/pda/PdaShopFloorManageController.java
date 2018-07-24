package com.uas.mes.api.pda;

/**
 * 生产备料接口
 * @data  2016年12月21日 下午2:19:44
 */
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.uas.mes.api.core.BaseApiController;
import com.uas.pda.service.PdaShopFloorManageService;

@Controller("api.pdaShopFloorManageController")
@RequestMapping("/api/pda/shopFloorManage")
public class PdaShopFloorManageController extends BaseApiController{
	@Autowired
	private PdaShopFloorManageService pdaShopFloorManageService;
	/**
	 * 根据输入的设备编号，获取制造单号和作业单号
	 * @param code
	 * @return
	 */
	@RequestMapping(value="/getMakeData.action",method = RequestMethod.GET)
	public ModelMap getMakeData(String devCode){
		Map<String, Object> data = new HashMap<String, Object>();
		data=  pdaShopFloorManageService.getMakeData(devCode);
		return success(data);
	}
	
	/**
	 * 根据输入的作业单号或者制造单号判断,获取 信息
	 * @param code
	 * @return
	 */
	@RequestMapping(value="/checkCode.action",method = RequestMethod.POST)
	public ModelMap checkCode(String devCode,String code){
		Map<String, Object> data = new HashMap<String, Object>();
		data = pdaShopFloorManageService.checkCode(devCode,code);
		return success(data);
	}
	/**
	 *  获取上料前台校验的缓存信息
	 * @param data
	 * @return
	 */
	@RequestMapping(value="/getCollectDetailData.action",method = RequestMethod.POST)
	public ModelMap getCollectDetailData(@RequestBody String data){
		List<Map<String, Object>> map =  pdaShopFloorManageService.getCollectDetailData(data);
		return success(map);
	}
	
	/**
	 * 获取料卷编号的信息
	 * @param data
	 * @return
	 */
	@RequestMapping(value="/getBarRemain.action",method = RequestMethod.POST)
	public ModelMap getBarRemain(@RequestBody String data){
		Map<String, Object> modelMap = pdaShopFloorManageService.getBarRemain(data);
		return success(modelMap);
	}
	
	
	/**
	 * 确认上料，判断
	 * @param data
	 * @return
	 */
	@RequestMapping(value="/loading.action",method = RequestMethod.POST)
	public ModelMap loading(String msl,String makeCraft){	
		Map<String, Object> modelMap = pdaShopFloorManageService.loading(msl,makeCraft);
		return success(modelMap);
	}
	
	/**
	 * 确认下料，判断
	 * @param data
	 * @return
	 */
	@RequestMapping(value="/cuttingStock.action",method = RequestMethod.POST)
	public  ModelMap cuttingStock(@RequestBody String data){		
		Map<String, Object> modelMap = pdaShopFloorManageService.cuttingStock(data);
		return success(modelMap);
		
	}
		
	/**
	 * 确认全部下料，判断
	 * @param data
	 * @return
	 */
	@RequestMapping(value="/cuttingAllStock.action",method = RequestMethod.POST)
	public  ModelMap cuttingAllStock(int mc_id,String mc_sourcecode){				
		pdaShopFloorManageService.cuttingAllStock(mc_id,mc_sourcecode);
		return success();
	}
		
	/**
	 * 接料，判断
	 * @param data
	 * @return
	 */
	@RequestMapping(value="/joinMaterial.action",method = RequestMethod.POST)
	public  ModelMap joinMaterial(@RequestBody  String data){	
		Map<String, Object> modelMap = pdaShopFloorManageService.joinMaterial(data);
		return success(modelMap);
	}
	
	/**
	 * 换料，判断
	 * @param data
	 * @return
	 */
	@RequestMapping(value="/changeMaterial.action",method = RequestMethod.POST)
	public  ModelMap changeMaterial(@RequestBody  String data){	
		Map<String, Object> modelMap = pdaShopFloorManageService.changeMaterial(data);
		return success(modelMap);
	}
	
	/**
	 * 料卷查询
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/queryData.action",method = RequestMethod.GET)
	public  ModelMap queryData(int  id){	
		List<Map<String, Object>> modelMap = pdaShopFloorManageService.queryData(id);
		return success(modelMap);
	}
	/**
	 * 校验
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/checkMakeSMTLocation.action",method = RequestMethod.GET)
	public  ModelMap checkMakeSMTLocation(int id){	
		List<Map<String, Object>> modelMap = pdaShopFloorManageService.checkMakeSMTLocation(id);
		return success(modelMap);
	}
	/**
	 * @param data
	 * 导入备料单
	 */	
	@RequestMapping(value="/importMPData.action",method = RequestMethod.POST)
	public ModelMap importMPData(@RequestBody String data){	
		pdaShopFloorManageService.importMPData(data);
		return success();
	}
}
