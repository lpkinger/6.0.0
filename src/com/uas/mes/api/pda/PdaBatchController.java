package com.uas.mes.api.pda;

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
import com.uas.pda.service.PdaBatchService;

/**
 * 条码拆分和合并接口
 * @data  2016年12月21日 下午2:19:44
 *
 */
@RestController("api.pdaBatchController")
@RequestMapping("/api/pda/batch")
public class PdaBatchController extends BaseApiController{

	@Autowired
	private PdaBatchService pdaBatchService;
	
	/**
	 * 得到需要合并或者分拆的数据
	 * 
	 * @param code
	 * @param whcode
	 * @return
	 */

	@RequestMapping(value = "/getBarcodeData.action", method = RequestMethod.GET)
	public ModelMap getBarcodeData(String code, boolean pr_ismsd) {
		if (StringUtils.isEmpty(code))
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		return success(pdaBatchService.getBarcodeData(code, pr_ismsd));
	}

	/**
	 * 批次分拆
	 * 
	 * @param data
	 */
	@RequestMapping(value = "/breakingBatch.action", method = RequestMethod.POST)
	public ModelMap breakingBatch(String or_barcode, Double or_remain,Double bar_remain) {
		if (or_barcode == null || or_remain == null || bar_remain == null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		return success(pdaBatchService.breakingBatch(or_barcode, or_remain, bar_remain,""));
	}

	/**
	 * 批次合并
	 * 
	 * @param data
	 */
	@RequestMapping(value = "/combineBatch.action", method = RequestMethod.POST)
	public ModelMap combineBatch(@RequestBody String data, Double total_remain) {
		if (data == null || total_remain == null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		return success(pdaBatchService.combineBatch(data, total_remain));
	}

	/**
	 * 获取需要进行包装拆分的数据
	 * 
	 * @param data
	 * @return
	 */
	@RequestMapping(value="/searchPackageData.action", method = RequestMethod.POST)
	public ModelMap searchPackageData(String data) {
		return success(pdaBatchService.searchPackageData(data));
	}

	/**
	 * 包装箱号拆分
	 * 
	 * @param data
	 * @param newOr_qty
	 * @param new_qty
	 * @return
	 */
	@RequestMapping(value = "/breakingPackage.action", method = RequestMethod.POST)
	public ModelMap breakingPackage(@RequestBody String data, String param) {
		if (data == null || param == null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		return success(pdaBatchService.breakingPackage(data, param));
	}

	@RequestMapping(value = "/getOutboxCode.action", method = RequestMethod.GET)
	public ModelMap getOutboxCode(String pr_code) {
		if (StringUtils.isEmpty(pr_code))
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		return success(pdaBatchService.outboxCodeMethod(pr_code));
	}
	/**
	 * 获取要拆分外箱号的数据
	 * @param code
	 * @return
	 */
	@RequestMapping(value = "/getOutBoxData.action", method = RequestMethod.GET)
	public  ModelMap getOutBoxData(String outBox){
		if (StringUtils.isEmpty(outBox))
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		return success(pdaBatchService.getOutBoxData(outBox));
	}
	
	/**
	 * 根据来源条码获取子条码
	 * @param barcode 原条码code
	 */
	@RequestMapping(value = "/getSonBarcode.action", method = RequestMethod.GET)
	public ModelMap getSonBarcode(String barcode){
		if(StringUtils.isEmpty(barcode))
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		return success(pdaBatchService.getSonBarcode(barcode));
	}
	
	
	/**
	 * 条码撤销拆分
	 * @param sourceid 原条码id（只能是在库有效才可以）
	 * @param bar_ids 拆分条码ID
	 */
	@RequestMapping(value = "/backBreaking.action", method = RequestMethod.POST)
	public  ModelMap backBreaking(int sourceid,String bar_ids){
		if(sourceid == 0 || StringUtils.isEmpty(bar_ids))
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		return success(pdaBatchService.backBreaking(sourceid,bar_ids));
	}
	
	
	
}
