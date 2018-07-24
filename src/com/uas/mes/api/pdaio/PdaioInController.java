package com.uas.mes.api.pdaio;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mockrunner.util.common.StringUtil;
import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.mes.api.core.BaseApiController;
import com.uas.pdaio.service.PdaioInService;

@RestController("api.pdaioInController")
@RequestMapping("/api/pdaio/pdaioIn")
public class PdaioInController extends BaseApiController{

	@Autowired
	private PdaioInService pdaioInService;
	
	/**
	 *新增单据
	 * 
	 * @param pi_class,pi_cardcode,pi_whcode
	 * @return
	 */

	@RequestMapping(value = "/newProdinout.action", method = RequestMethod.POST)
	public ModelMap addProdinout(String pi_class,String pi_cardcode,String pi_whcode,HttpSession session) {
		if (StringUtils.isEmpty(pi_class)){			
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "单据类型不能为空");
		}
		return success(pdaioInService.addProdinout(pi_class,pi_cardcode,pi_whcode,session));
		
	}
	//获取仓库信息
	@RequestMapping(value = "/getWhcode.action", method = RequestMethod.GET)
	public ModelMap getWhcode(String condition) {
		return success(pdaioInService.getWhcode(condition));
		
	}
	//获取供应商信息
	@RequestMapping(value = "/getVendor.action", method = RequestMethod.GET)
	public ModelMap getVendor(String condition,Integer page,Integer pageSize) {
		return success(pdaioInService.getVendor(condition,page,pageSize));
		
	}
    //确认入库
	@RequestMapping(value = "/newProdiodetail.action", method = RequestMethod.POST)
	public ModelMap addProdiodetail(String inoutno) {
		if (StringUtils.isEmpty(inoutno)){			
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "单号不能为空");
		}	
		return success(pdaioInService.addProdiodetail(inoutno));
		
	}
	//扫描条码
	@RequestMapping(value = "/getBarcodeInfo.action", method = RequestMethod.GET)
	public ModelMap getBarcodeInfo(String inoutno,String barcode,Integer allowRepeat) {
		if (StringUtils.isEmpty(barcode)){			
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "条码不能为空");
		}	
		if (StringUtils.isEmpty(inoutno)){			
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "单据不存在");
		}	
		ModelMap modelMap = new ModelMap();
		Map<String, Object> modelValue = new HashMap<>();
		modelValue = pdaioInService.getBarcodeInfo(inoutno,barcode,allowRepeat);
		if(("-1").equals(modelValue.get("repeat")) ){
			modelMap.put("success", false);
			modelMap.put("repeat",modelValue.get("repeat"));
			modelMap.put("barcode", barcode);
			return modelMap;
		}else{			
			return success(modelValue);
		}
		
	}
	//单据列表
	@RequestMapping(value = "/getProdinoutList.action", method = RequestMethod.GET)
	public ModelMap getProdinoutList(String condition,Integer page,Integer pageSize) {
		return success(pdaioInService.getProdinoutList(condition,page,pageSize));
		
	}
	// 删除单据
	@RequestMapping(value = "/deleteInoutAndDetail.action", method = RequestMethod.POST)
	public ModelMap deleteInoutAndDetail(Integer piid) {
		if (StringUtils.isEmpty(piid) || piid <= 0){			
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "单据不存在");
		}	
		return success(pdaioInService.deleteInoutAndDetail(piid));
		
	}
	//条码明细
	@RequestMapping(value = "/getBarcodeDetail.action", method = RequestMethod.GET)
	public ModelMap getBarcodeDetail(Integer piid,Integer page,Integer pageSize,String condition) {
		if (StringUtils.isEmpty(piid) || piid<=0){			
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "单据不存在");
		}	
		return success(pdaioInService.getBarcodeDetail(piid,page,pageSize,condition));
		
	}
	//数量汇总
	@RequestMapping(value = "/getProdInoutQtySum.action", method = RequestMethod.GET)
	public ModelMap getProdInoutQtySum(Integer piid,Integer page,Integer pageSize) {
		if (StringUtils.isEmpty(piid) || piid<=0){			
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "单据不存在");
		}	
		return success(pdaioInService.getProdInoutQtySum(piid,page,pageSize));
		
	}
	
	// 删除单据
	@RequestMapping(value = "/deleteBarcode.action", method = RequestMethod.POST)
	public ModelMap deleteBarcode(Integer piid,String type,Integer biid) {
		if (StringUtils.isEmpty(piid) || piid <= 0){			
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "单据不存在");
		}	
		return success(pdaioInService.deleteBarcode(piid,type,biid));
		
	}

	// 撤销入库
	@RequestMapping(value = "/revokeBarcode.action", method = RequestMethod.POST)
	public ModelMap revokeBarcode(String inoutno) {
		return success(pdaioInService.revokeBarcode(inoutno));
		
	}
	
	//或者当前人的最新一张未入库单据
	@RequestMapping(value = "/getLatestProdinout.action", method = RequestMethod.GET)
	public ModelMap getLatestProdinout(String emcode) {
		Map<String, Object> modelMap = pdaioInService.getLatestProdinout(emcode);
		if(modelMap == null || ("").equals(modelMap)){
			ModelMap map = new ModelMap();
			map.put("success", false);
			map.put("data", null);
			return map;
		}else{			
			return success(modelMap);
		}
	}
	
	//更换供应商
	@RequestMapping(value = "/updatePiCardcde.action", method = RequestMethod.POST)
	public ModelMap updatePiCardcde(Integer piid,String newVendor) {
		return success(pdaioInService.updatePiCardcde(piid,newVendor));
	}
}
