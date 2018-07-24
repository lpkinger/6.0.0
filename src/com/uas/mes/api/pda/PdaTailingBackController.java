package com.uas.mes.api.pda;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.mes.api.core.BaseApiController;
import com.uas.pda.service.PdaTailingBackService;

/**
 * 尾料还仓
 * @author XiaoST
 *
 */
@RestController("api.pdaTailingBackController")
@RequestMapping("/api/pda/tailingBack")
public class PdaTailingBackController extends BaseApiController {
	@Autowired
	private PdaTailingBackService pdaTailingBackService;
	/**
	 * 尾料条码还仓，根据条码获取预计剩余数
	 * @return
	 */
	@RequestMapping(value="/getForcastRemain.action",method = RequestMethod.GET)
	public ModelMap getForcastRemain(String code){
		if (StringUtils.isEmpty(code))
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		Map<String, Object> data = pdaTailingBackService.getForcastRemain(code);
		return success(data);
	}
	
	/**
	 * 确认还仓
	 * @param data
	 */
	@RequestMapping(value="/tailingBack.action",method = RequestMethod.POST)
	public ModelMap tailingBack(@RequestBody String data){
		 pdaTailingBackService.tailingBack(data);
		return success();
	}
}
