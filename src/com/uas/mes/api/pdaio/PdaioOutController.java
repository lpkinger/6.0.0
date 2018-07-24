package com.uas.mes.api.pdaio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.mes.api.core.BaseApiController;
import com.uas.pda.service.PdaOutMaterialService;
import com.uas.pdaio.service.PdaioInService;
import com.uas.pdaio.service.PdaioOutService;

@RestController("api.pdaioOutController")
@RequestMapping("/api/pdaio/pdaioOut")
public class PdaioOutController extends BaseApiController{

	@Autowired
	private PdaioOutService pdaioOutService;
	
	/**
	 * 根据输入的出库单号进行模糊查询
	 * @param inoutno
	 * @return
	 */
	@RequestMapping(value="/fuzzySearch.action",method = RequestMethod.GET)
	public ModelMap fuzzySearch(String inoutNo){		
		if (StringUtils.isEmpty(inoutNo)) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "单号不允许为空");
		}
		List<Map<String, Object>> map = pdaioOutService.fuzzySearch(inoutNo);
		return success(map);
	}
	
	/**
	 * 根据客户端页面中的原材料出库单号，以及仓库获取单据
	 * @param inoutNo
	 * @param whcode
	 * @return
	 */
	@RequestMapping(value="/getProdOut.action",method = RequestMethod.GET)
	public ModelMap getProdOut(String inoutNo,String whcode){
		if (inoutNo == null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "单号不允许为空");
		}
		Map<String, Object> map = pdaioOutService.getProdOut(inoutNo);
		return success(map);
	}
	/**
	 * 根据输入的出库单号获取需要采集的下条数据
	 * @param inoutno
	 * @return
	 */
	@RequestMapping(value="/getNextData.action",method = RequestMethod.GET)
	public ModelMap getNextData(Integer pi_id){	
		if(pi_id == 0){
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "单据必须存在");
		}
		return success(pdaioOutService.getNextData(pi_id));
	}
	
	//单据列表
	@RequestMapping(value = "/getProdinoutList.action", method = RequestMethod.GET)
	public ModelMap getProdinoutList(String condition,Integer page,Integer pageSize) {
		return success(pdaioOutService.getProdinoutList(condition,page,pageSize));
		
	}
	
	//条码明细
	@RequestMapping(value = "/getBarcodeDetail.action", method = RequestMethod.GET)
	public ModelMap getBarcodeDetail(Integer piid,Integer page,Integer pageSize,String condition) {
		if (StringUtils.isEmpty(piid) || piid<=0){			
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "单据不存在");
		}	
		return success(pdaioOutService.getBarcodeDetail(piid,page,pageSize,condition));
		
	}
	//数量汇总
	@RequestMapping(value = "/getProdInoutQtySum.action", method = RequestMethod.GET)
	public ModelMap getProdInoutQtySum(Integer piid,Integer page,Integer pageSize) {
		if (StringUtils.isEmpty(piid) || piid<=0){			
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "单据不存在");
		}	
		return success(pdaioOutService.getProdInoutQtySum(piid,page,pageSize));
		
	}
	
	// 删除条码
	@RequestMapping(value = "/deleteBarcode.action", method = RequestMethod.POST)
	public ModelMap deleteBarcode(Integer piid,String type,Integer biid) {
		if (StringUtils.isEmpty(piid) || piid <= 0){			
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "单据不存在");
		}	
		return success(pdaioOutService.deleteBarcode(piid,type,biid));
		
	}
	
	//采集条码
	@RequestMapping(value = "/collectBarcode.action", method = RequestMethod.POST)
	public ModelMap collectBarcode(Integer pi_id,String barcode) {
		if (StringUtils.isEmpty(barcode)){			
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "条码不允许为空");
		}	
		return success(pdaioOutService.collectBarcode(pi_id,barcode));
	}
	
	//撤销出库
	@RequestMapping(value = "/revokeBarcode.action", method = RequestMethod.POST)
	public ModelMap revokeBarcode(Integer pi_id,String barcode) {
		if (StringUtils.isEmpty(barcode)){			
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "条码不允许为空");
		}	
		return success(pdaioOutService.revokeBarcode(pi_id,barcode));
	}
	
	//获取单据状态
	@RequestMapping(value = "/getProdOutStatus.action", method = RequestMethod.GET)
	public ModelMap getProdOutStatus(String ids) {
		if (StringUtils.isEmpty(ids)){			
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要参数");
		}	
		return success(pdaioOutService.getProdOutStatus(ids));
	}
}
