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
import com.uas.pda.service.PdaCheckService;

/**
 * 货物核查接口
 * @data  2016年12月21日 下午2:19:44
 */

@RestController("api.pdaCheckController")
@RequestMapping("/api/pda/check")
public class PdaCheckController extends BaseApiController{

	@Autowired
	private PdaCheckService pdaCheckService;

	/**
	 * 物料库存核查
	 * 
	 * @param pr_code
	 * @param wh_code
	 * @return
	 */
	@RequestMapping(value = "/makeMaterialCheck.action", method = RequestMethod.GET)
	public ModelMap makeMaterialCheck(String pr_code, String wh_code) {
		if (pr_code == null || wh_code == null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		List<Map<String, Object>> page = pdaCheckService.makeMaterialCheck(pr_code, wh_code);
		return success(page);
	}

	/**
	 * 物料库存明细获取
	 * 
	 * @param data
	 * @return
	 */
	@RequestMapping(value = "/makeMaterialDetail.action", method = RequestMethod.GET)
	public ModelMap makeMaterialDetail(String pr_code, String bar_prodcode, String wh_code, String bar_location) {
		if (pr_code == null || bar_prodcode == null || wh_code == null || bar_location == null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		List<Map<String, Object>> page = pdaCheckService.makeMaterialDetail(pr_code,bar_prodcode,wh_code,bar_location);
		return success(page);
	}

	/**
	 * 条码信息核查
	 * 
	 * @param barcode
	 * @param whcode
	 * @return
	 */
	@RequestMapping(value = "/barcodeCheck.action", method = RequestMethod.GET)
	public ModelMap barcodeCheck(String barcode, String whcode) {
		if (barcode == null || whcode == null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		return success(pdaCheckService.barcodeCheck(barcode, whcode));
	}

	/**
	 * 包装信息核查
	 * 
	 * @param outboxCode外箱编号
	 * @return
	 */
	@RequestMapping(value = "/packageCheck.action", method = RequestMethod.GET)
	public ModelMap packageCheck(String outboxCode) {
		if (StringUtils.isEmpty(outboxCode))
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		return success(pdaCheckService.packageCheck(outboxCode));
	}

	/**
	 * 工单完工品核查
	 * 
	 * @param ma_code
	 * @return
	 */
	@RequestMapping(value = "/makeFinishCheck.action", method = RequestMethod.GET)
	public ModelMap makeFinishCheck(String makeCode) {
		if (StringUtils.isEmpty(makeCode))
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		return success(pdaCheckService.makeFinishCheck(makeCode));
	}

	/**
	 * 订单完工品核查
	 * 
	 * @param or_code
	 * @return
	 */
	@RequestMapping(value = "/orderFinishCheck.action", method = RequestMethod.GET)
	public ModelMap orderFinishCheck(String saleCode) {
		if (StringUtils.isEmpty(saleCode))
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		return success(pdaCheckService.orderFinishCheck(saleCode));
	}

	/**
	 * PO核查
	 * 
	 * @param or_code
	 * @return
	 */
	@RequestMapping("/checkPO.action")
	public ModelMap checkPO(String or_code) {
		if (StringUtils.isEmpty(or_code))
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		return success(pdaCheckService.checkPO(or_code));
	}
	
}
