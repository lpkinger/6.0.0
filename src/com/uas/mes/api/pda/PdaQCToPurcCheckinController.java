package com.uas.mes.api.pda;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.mes.api.core.BaseApiController;
import com.uas.pda.service.PdaQCToPurcCheckinService;

/**
 * 转单
 * @data  2018年4月24日 上午午08:19:14
 *
 */
@RestController("api.pdaQCToPurcCheckinController")
@RequestMapping("/api/pda/QCToPurcCheckin")
public class PdaQCToPurcCheckinController extends BaseApiController{

	@Autowired
	private PdaQCToPurcCheckinService pdaQCToPurcCheckinService;
	
	/**
	 * 根据条码获取到收料单的信息
	 * 
	 * @param bar_code
	 * @return
	 */

	@RequestMapping(value = "/getDataByBar.action", method = RequestMethod.GET)
	public ModelMap getBarcodeData(String bar_code) {
		if (StringUtils.isEmpty(bar_code))
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请采集条码");
		return success(pdaQCToPurcCheckinService.getDataByBar(bar_code));
	}

	/**
	 * 检验单的信息
	 * 
	 * @param VE_ID
	 * @return
	 */
	@RequestMapping(value = "/turnPurcStorage.action", method = RequestMethod.POST)
	public ModelMap turnPurcStorage(Integer ve_id,String okwh,String ngwh) {
		return success(pdaQCToPurcCheckinService.turnPurcStorage(ve_id,okwh,ngwh));
	}
	
	/**
	 * 检验单
	 * 
	 * @param caller
	 * @return
	 */
	@RequestMapping(value="/getNeedGetList.action",method = RequestMethod.GET)
	public ModelMap getNeedGetList(String caller,String code,Integer page,Integer pageSize){
		if(page == null || page == 0 || pageSize == null || pageSize == 0){
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		List<Map<String, Object>> map = pdaQCToPurcCheckinService.getHaveList(caller,code,page,pageSize);
		return success(map);
	}
	
}