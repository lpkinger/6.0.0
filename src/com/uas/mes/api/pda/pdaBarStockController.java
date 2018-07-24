package com.uas.mes.api.pda;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.mes.api.core.BaseApiController;
import com.uas.pda.service.pdaBarStockService;

@RestController("api.pdaBarStockController")
@RequestMapping("/api/pda/barStock")
public class pdaBarStockController extends BaseApiController{

	@Autowired
	private pdaBarStockService pdaBarStockService;
	
	//获取最近5条已提交的条码补生成单据
	@RequestMapping(value = "/getBarStockList.action", method = RequestMethod.GET)
	public ModelMap getBarStockList (Integer page ,Integer pagesize) {
		if (page == null  || pagesize==null ||page == 0  || pagesize==0) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		List<Map<String, Object>> map = pdaBarStockService.getBarStockList(page, pagesize);
		return success(map);
	}
	
	//模糊查询单据编号
	@RequestMapping(value = "/search.action", method = RequestMethod.GET)
	public ModelMap search (String condition,Integer page ,Integer pagesize) {
		if (("").equals(condition) || condition == null){
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		List<Map<String, Object>> map = pdaBarStockService.search(condition, page ,pagesize);
		return success(map);
	}
	
	//获取需要补生成条码的物料数量汇总
		@RequestMapping(value = "/getBarStockByProdcode.action", method = RequestMethod.GET)
		public ModelMap getBarStockByProdcode (Integer id,Integer page,Integer pagesize,String condition) {
			if (id == null|| id == 0) {
				throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
			}
			Map<String, Object> map = pdaBarStockService.getBarStockByProdcode(id,page,pagesize,condition);
			return success(map);
		}
		
		//获取补生成条码的批次
		@RequestMapping(value = "/getBarStockBatch.action", method = RequestMethod.GET)
		public ModelMap getBarStockBatch (Integer id,String bsd_prodcode) {
			if (id == null ||id == 0 || StringUtils.isEmpty(bsd_prodcode)) {
				throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
			}
			List<Map<String, Object>> map = pdaBarStockService.getBarStockBatch(id,bsd_prodcode);
			return success(map);
		}
		
		//生成条码按钮点击事件
		@RequestMapping(value = "/newBarcode.action", method = RequestMethod.POST)
		public ModelMap newBarcode(HttpServletRequest request,Integer id,String bsd_prodcode,boolean ifprint, String data) {
			if (id == 0 || id==null || StringUtils.isEmpty(data) || StringUtils.isEmpty(bsd_prodcode)) {
				throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
			}
			return success(pdaBarStockService.newBarcode(request,id,bsd_prodcode,ifprint,data));
		}
		
		//获取该单据已生成条码的批次
		@RequestMapping(value = "/getHaveStockBatch.action", method = RequestMethod.GET)
		public ModelMap getHaveStockBatch (Integer  id){
			if (id == null ) {
				throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
			}
			return success(pdaBarStockService.getHaveStockBatch(id));
		}
		//勾选需要打印的批次
		@RequestMapping(value = "/printBarcode.action", method = RequestMethod.POST)
		public ModelMap printBarcode (HttpServletRequest request,Integer id,boolean ifAll, String data){
			if (id == null ) {
				throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
			}
			return success(pdaBarStockService.printBarcode(request,id, ifAll,data));
		}
		
		//修改在库条码的数量
		@RequestMapping(value = "/modifyNumber.action", method = RequestMethod.POST)
		public ModelMap modifyNumber (String barcode,double nowqty){
			if (barcode == null || nowqty<=0) {
				throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
			}
			return success(pdaBarStockService.modifyNumber(barcode,nowqty));
		}

}
