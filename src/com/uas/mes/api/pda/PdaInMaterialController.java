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
import com.uas.pda.service.PdaInMaterialService;

/**
 * 原材料入库接口
 * @data  2016年12月21日 下午2:19:44
 */
@RestController("api.pdaInMaterialController")
@RequestMapping("/api/pda/inMaterial")
public class PdaInMaterialController extends BaseApiController{

	@Autowired
	private PdaInMaterialService  pdaInMaterialService;
	
	/**
	 * 根据输入的入库单号，和仓库进行模糊查询
	 * @param inoutno
	 * @return
	 */
	@RequestMapping(value = "/fuzzySearch.action", method = RequestMethod.GET)
	public ModelMap fuzzySearch(String inoutNo, String whcode){
		if (StringUtils.isEmpty(inoutNo))
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		return success(pdaInMaterialService.fuzzySearch(inoutNo,whcode));
	}
	
	/**
	 * 根据客户端页面中的入库单号，以及仓库获取单据
	 * 获取需要入库采集的单据
	 * @param inoutNo
	 * @param whcode
	 * @return
	 */
	@RequestMapping(value="/getProdIn.action" , method = RequestMethod.GET)
	public ModelMap getProdIn(String inoutNo,String whcode){
		if (StringUtils.isEmpty(inoutNo))
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递入库单号");
		return success(pdaInMaterialService.getProdIn(inoutNo,whcode));
	}
	
	/**
	 * 根据原材料入库单ID 获取需要采集的数据prodiodetail,
	 * @param id
	 * @param whcode  仓库编号
	 * @return
	 */
	@RequestMapping(value = "/getNeedGetList.action", method = RequestMethod.GET)
	public ModelMap getNeedGetList (Integer id,String whcode){
		if (id == null || whcode == null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		return success(pdaInMaterialService.getNeedGetList(id,whcode));
	}
	/**
	 * 提交采集信息保存至barcodeIO表
	 * @param data
	 * @return
	 */
	@RequestMapping(value = "/saveBarcode.action", method = RequestMethod.POST)
	public ModelMap saveBarcode(String data){
		if (data == null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请勿传空数据");
		}
		return success(pdaInMaterialService.saveBarcode(data));
	}
	
	/**
	 * 原材料入库单个删除已提交数据
	 * @param bi_piid 入库单id
	 * @param barcode序列号
	 * @param outboxcode 箱号
	 * @param whcode  仓库
	 * @return
	 */
	@RequestMapping(value= "/deleteDetail.action", method = RequestMethod.GET)
	public ModelMap deleteDetail(Integer bi_piid,String barcode,String outboxcode,String whcode) {
		if (bi_piid == null || whcode == null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		return success(pdaInMaterialService.deleteDetail(bi_piid,barcode,outboxcode,whcode));
	}
	
	
	/**
	 * 抽查校验
	 * @param inoutNo
	 * @param whcode
	 * @return
	 */
	@RequestMapping("/getCheckProdIn.action")
	public ModelMap getCheckProdIn(String inoutNo,String whcode){
		return success(pdaInMaterialService.getCheckProdIn(inoutNo,whcode));
	}
	
	/**
	 * 确认入库
	 * @param bi_piid 入库单id
	 * @param barcode序列号
	 * @param outboxcode 箱号
	 * @param whcode  仓库
	 * @return
	 */
	@RequestMapping(value= "/confirmIn.action", method = RequestMethod.GET)
	public ModelMap confirmIn(Integer pi_id,String whcode) {
		if (pi_id == null || whcode == null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		return success(pdaInMaterialService.confirmIn(pi_id,whcode));
	}
	
	/**
	 * 数据绑定与校验，以前是saveBarcode方法
	 * @param pi_id 入库单id
	 * @param whcode 仓库
	 * @param code  框里面的值
	 * @return
	 */
	@RequestMapping(value= "/getCodeData.action", method = RequestMethod.GET)
	public ModelMap getCodeData(String type,Integer pi_id,String whcode,String code) {
		if (pi_id == null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		return success(pdaInMaterialService.getCodeData(type,pi_id,whcode,code));
	}
}
