package com.uas.mes.api.pda;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.mes.api.core.BaseApiController;
import com.uas.pda.service.PdaLocaTransService;

/**
 * 储位转移接口
 * @data  2016年12月21日 下午2:19:44
 */
@RestController("api.pdaLocaTransController")
@RequestMapping("/api/pda/transfer")
public class PdaLocaTransController extends BaseApiController{
	
	@Autowired
	private PdaLocaTransService pdaLocaTransService;

	/**
	 * 根据仓库，【条码号或者外箱编号】获取需要进行储位转移的物料，条码等信息
	 * 
	 * @param whcode
	 * @param code
	 * @param type
	 * @return
	 */
	@RequestMapping(value = "/getCodeData.action", method = RequestMethod.GET)
	public ModelMap getCodeData(String whcode, String code, String type) {
		if (whcode == null || code == null || type == null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		return success(pdaLocaTransService.getCodeData(whcode, code, type));
	}

	/**
	 * 确认储位转移
	 * 
	 * @param data
	 * @param location
	 */
	@RequestMapping(value = "/locaTransfer.action", method = RequestMethod.POST)
	public ModelMap locaTransfer(String location,String data) {
		if (location == null || data == null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		pdaLocaTransService.locaTransfer(data, location);
		return success();
	}

	@RequestMapping("/getCodeWhcode.action")
	public Map<String,Object> getCodeWhcode(String code, String type){
		if(code == null){
			BaseUtil.showError("请采集数据!");
		}
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", pdaLocaTransService.getCodeWhcode(code, type));
		return modelMap;
	}
	
	
	@RequestMapping("/whcodeTransfer.action")
	public void whcodeTransfer(String data, String whcode){
		
		pdaLocaTransService.whcodeTransfer(data, whcode);
		
	}
	
}
