package com.uas.mes.api.pda;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.mes.api.core.BaseApiController;
import com.uas.pda.service.PdaInFinishService;

/**
 * 完工品入库接口
 * @data  2016年12月21日 下午2:19:44
 */
@RestController("api.pdaInFinishController")
@RequestMapping("/api/pda/inFinish")
public class PdaInFinishController extends BaseApiController{
	
	@Autowired
	private PdaInFinishService  pdaInFinishService;
	
	/**
	 * 根据输入的入库单号，和仓库进行模糊查询
	 * 
	 * @param inoutno
	 * @return
	 */
	@RequestMapping(value = "/fuzzySearch.action", method = RequestMethod.GET)
	public ModelMap fuzzySearch(String inoutNo, String whcode){
		if (StringUtils.isEmpty(inoutNo))
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		return success(pdaInFinishService.fuzzySearch(inoutNo,whcode));
	}
	
	/**
	 * 根据客户端页面中的入库单号，以及仓库获取单据
	 * 获取需要入库采集的单据
	 * @param inoutNo
	 * @param whcode
	 * @return
	 */
	@RequestMapping(value = "/getProdIn.action", method = RequestMethod.GET)
	public ModelMap getProdIn(String inoutNo,String whcode){
		if (inoutNo == null || whcode == null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		return success(pdaInFinishService.getProdIn(inoutNo,whcode));
	}
	
	/**
	 * 完工品入库获取需要采集数据
	 * @param 入库单id
	 * @param 仓库编号
	 * @return
	 */
	@RequestMapping(value = "/getNeedGetList.action", method = RequestMethod.GET)
	public ModelMap getNeedGetList(Long id,String whcode){				
		if ((id == null && whcode == null) || id == null )
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		return success(pdaInFinishService.getNeedGetList(id,whcode));
	}	
	
	/**
	 * 完工入库单个采集数据入库
	 * @param id 入库单id
	 * @param whcode
	 * @barcode 采集的数据
	 * @kind  序列号 sncode ， 箱号 package，栈板号 pallet
	 * @return
	 */
	@RequestMapping(value = "/save.action", method = RequestMethod.POST)
	public ModelMap save(Long id,String whcode,String barcode,String kind){
		if (id == null || whcode == null || barcode == null || kind == null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		return success(pdaInFinishService.save(id,whcode,barcode,kind));
	}	
	
	/**
	 * 完工入库单个删除已提交数据
	 * @param bi_piid 入库单id
	 * @param barcode序列号
	 * @param outboxcode 箱号
	 * @param whcode  仓库
	 * @return
	 */
	@RequestMapping(value = "/deleteDetail.action", method = RequestMethod.GET)
	public ModelMap deleteDetail(Long bi_piid,String barcode,String outboxcode,String whcode) {
		if (bi_piid == null || (barcode == null && outboxcode == null )|| whcode == null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		pdaInFinishService.deleteDetail(bi_piid,barcode,outboxcode,whcode);
		return success();
	}
		
	/**
	 * 完工入库批量提交数据
	 * @param whcode  仓库
	 * @data 采集的数据 [{},{}]格式 list
	 * @return
	 */
	@RequestMapping(value = "/saveAll.action", method = RequestMethod.POST)
	public ModelMap saveAll(String data){				
		if (StringUtils.isEmpty(data))
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		return success(pdaInFinishService.saveAll(data));
	}
}
