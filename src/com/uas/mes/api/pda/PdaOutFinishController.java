package com.uas.mes.api.pda;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.mes.api.core.BaseApiController;
import com.uas.pda.service.PdaOutFinishService;

/**
 * 完工品出库接口
 * @data  2016年12月21日 下午2:19:44
 */

@RestController("api.pdaOutFinishController")
@RequestMapping("/api/pda/outFinish")
public class PdaOutFinishController extends BaseApiController{
	@Autowired
	private PdaOutFinishService pdaOutFinishService;
	
	/**
	 * 根据输入的完工品出库单号模糊查询
	 * 
	 * @param inoutno
	 * @return
	 */
	@RequestMapping(value="/fuzzySearch.action",method = RequestMethod.GET)
	public ModelMap fuzzySearch(String inoutNo){		
		if (inoutNo == null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		List<Map<String, Object>> page = pdaOutFinishService.fuzzySearch(inoutNo);
		return success(page);
	}
	
	/**
	 * 根据客户端页面中的完工品出库单号，以及仓库获取单据
	 * @param inoutNo
	 * @return
	 */
	@RequestMapping(value="/getProdOut.action",method = RequestMethod.GET)
	public ModelMap getProdOut(String inoutNo,String pi_class){
		if (inoutNo == null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		List<Map<String, Object>> page = pdaOutFinishService.getProdOut(inoutNo,pi_class);
		return success(page);
	}
	
	/**
	 * 完工品出库获取需要采集数据
	 * @param 出库单id
	 * @param 仓库编号
	 * @return
	 */
	@RequestMapping(value="/getNeedGetList.action",method = RequestMethod.GET)
	public ModelMap getNeedGetList(Long id){				
		if (id == 0 || id == null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		Map<String, Object> page = pdaOutFinishService.getNeedGetList(id);
		return success(page);
	}	
	/**
	 * 完工出库清除所有采集数据
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/clearGet.action",method = RequestMethod.POST)
	public ModelMap clearGet(Long id){		
		if (id == 0 || id == null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		pdaOutFinishService.clearGet(id);
		return success();	
	}
	
	/**
	 * 保存数据
	 * @param barcode 采集的数据
	 * @param id  单据id
	 * @param kind  数据类型 条码，箱号 ，或者栈板号
	 * @return
	 */
	@RequestMapping(value="/save.action",method = RequestMethod.POST)
	public ModelMap save(String barcode,int id,String kind){
		if (barcode == null || kind == null || id == 0) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		Map<String, Object> map = pdaOutFinishService.save(barcode,id,kind);
		return success(map);
	}
	

	/**
	 * 完工出库获取已经采集采集数据
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/getHaveSubmitList.action",method = RequestMethod.GET)
	public ModelMap getHaveSubmitList(Long id){		
		if (id == null || id == 0) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		List<Map<String, Object>> map = pdaOutFinishService.getHaveSubmitList(id);
		return success(map);
	}
	
	/**
	 * 完工出库单个删除已提交数据
	 * @param bi_piid 入库单id
	 * @param barcode序列号
	 * @param outboxcode 箱号,栈板
	 * @return
	 */
	@RequestMapping(value="/deleteDetail.action",method = RequestMethod.GET)
	public ModelMap deleteDetail(Long bi_piid,String barcode,String outboxcode) {
		if (bi_piid == null || bi_piid == 0) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		pdaOutFinishService.deleteDetail(bi_piid,barcode,outboxcode);
		return success();
	}
	

	/**
	 * 完工出库批量提交数据
	 * @param whcode  仓库
	 * @data 采集的数据 [{},{}]格式 list
	 * @return
	 */
	@RequestMapping(value="/saveAll.action",method = RequestMethod.POST)
	public ModelMap saveAll(String data){	
		if(data == null){
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		Map<Object,Object> map = pdaOutFinishService.saveAll(data);
		return success(map);
	}	
	
}
