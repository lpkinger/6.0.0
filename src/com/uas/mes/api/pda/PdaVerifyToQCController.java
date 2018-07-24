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
import com.uas.pda.service.PdaVerifyToQCService;

/**
 * 转单
 * @data  2018年4月24日 上午午08:19:14
 *
 */
@RestController("api.pdaVerifyToQCController")
@RequestMapping("/api/pda/verifyToQC")
public class PdaVerifyToQCController extends BaseApiController{

	@Autowired
	private PdaVerifyToQCService pdaVerifyToQCService;
	
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
		return success(pdaVerifyToQCService.getDataByBar(bar_code));
	}

	/**
	 * 收料单列表
	 * 
	 * @param caller
	 * @return
	 */
	@RequestMapping(value="/getNeedGetList.action",method = RequestMethod.GET)
	public ModelMap getNeedGetList(String caller,String code,Integer page,Integer pageSize){
		if(page == null || page == 0 || pageSize == null || pageSize == 0){
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		List<Map<String, Object>> map = pdaVerifyToQCService.getHaveList(caller,code,page,pageSize);
		return success(map);
	}
		
}
