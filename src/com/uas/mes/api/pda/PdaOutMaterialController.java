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
import com.uas.pda.service.PdaCommonService;
import com.uas.pda.service.PdaOutMaterialService;

@RestController("api.pdaOutMaterialController")
@RequestMapping("/api/pda/outMaterial")
public class PdaOutMaterialController extends BaseApiController {

	@Autowired
	private PdaOutMaterialService pdaOutMaterialService;
	@Autowired
	private PdaCommonService  pdaCommonService;
	/**
	 * 根据输入的出库单号，和仓库进行模糊查询
	 * @param inoutno
	 * @param whcode
	 * @return
	 */
	@RequestMapping(value="/fuzzySearch.action",method = RequestMethod.GET)
	public ModelMap fuzzySearch(String inoutNo,String whcode){		
		if (inoutNo == null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		List<Map<String, Object>> map = pdaOutMaterialService.fuzzySearch(inoutNo,whcode);
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
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		List<Map<String, Object>> map = pdaOutMaterialService.getProdOut(inoutNo,whcode);
		return success(map);
	}

	/**
	 * 根据出库单号获取需要出库采集的信息数据,根据物料数量汇总
	 * @param id  出货单id
	 * @param whcode
	 * @param type   按物料数量汇总 byProdcode  按批次数量汇总byBatch
	 * @return
	 */
	@RequestMapping(value="/getNeedGetList.action",method = RequestMethod.GET)
	public ModelMap getNeedGetList(Integer id,String whcode,String type){
		if (id == 0 ||type==null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		List<Map<String, Object>> map = pdaOutMaterialService.getNeedGetList(id,whcode,type);
		return success(map);
	}
	

	/**
	 * 原材料出库按照批次数量汇总采集条码，将符合条件的条码插入值barcodeIo表中
	 * @param barcode 条码号
	 * @param id      出库单ID
	 * @param whcode  仓库
	 * @param type   类型  条码号 barcode  外箱号 package
	 * @return 将采集成功的条码信息返回值客户端显示
	 */
	@RequestMapping(value="/outByProdcode.action",method = RequestMethod.GET)
	public ModelMap outByProdcode(String barcode,int id,String whcode,String type,boolean msdcheck){	
		if (barcode == null || id == 0|| type==null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		Map<String, Object> map = pdaOutMaterialService.outByProdcode(barcode,id,whcode,type,msdcheck);
		return success(map);
	}
		
	/**
	 * 按照批次数量汇总原材料出库采集条码，将符合条件的条码插入值barcodeIo表中
	 * @param barcode 条码号
	 * @param id      出库单ID
	 * @param whcode  仓库
	 * @param type 采集的是条码或箱号  条码号 barcode  外箱号 package
	 * @return 将采集成功的条码信息返回值客户端显示
	 */
	@RequestMapping(value="/outByBatch.action",method = RequestMethod.GET)
	public ModelMap outByBatch(String barcode,int id,String whcode,String type,boolean msdcheck){	
		if (barcode == null || id == 0 || type==null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		Map<String, Object> map = pdaOutMaterialService.outByBatch(barcode,id,whcode,type,msdcheck,"");
		return success(map);
	}
	
	/**
	 * 拆分条码出库按照批次数量汇总原材料出库采集条码，将符合条件的条码插入值barcodeIo表中
	 * @param barcode 条码号
	 * @param id      出库单ID
	 * @param whcode  仓库
	 * @return 将采集成功的条码信息返回值客户端显示
	 */
	@RequestMapping(value="/outByBatchBreaking.action",method = RequestMethod.POST)
	public ModelMap outByBatchBreaking(int id,String whcode,String barcode,Double or_remain,Double bar_remain){	
		if (barcode == null || id == 0 || or_remain == 0 || bar_remain == 0) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		Map<String, Object> map = pdaOutMaterialService.outByBatchBreaking(id,whcode,barcode,or_remain,bar_remain);
		return success(map);
	}
	
	/**
	 * 根据出入库单ID 获取已经提交的采集信息
	 * @param bi_pdid
	 * @return
	 */
	@RequestMapping(value="/pda/getHaveSubmitList.action",method = RequestMethod.GET)
	public ModelMap getHaveSubmitList (int bi_piid,String whcode){
		List<Map<String, Object>> map = pdaCommonService.getHaveSubmitList(bi_piid,whcode);
		return success(map);
	}
	
	/**
	 * 原材料出库采集条码，自由采集模式
	 * @param barcode 条码号或箱号
	 * @param type    采集，箱号或条码号
	 * @return 将采集成功的条码信息返回值客户端显示
	 */
	@RequestMapping(value="/freeOut.action",method = RequestMethod.GET)
	public ModelMap freeOut(String barcode,String type){	
		if(barcode == null || type == null){
		    throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		return success(pdaOutMaterialService.freeOut(barcode,type));
	}
	/**
	 按物料汇总
	 */
	@RequestMapping(value="/getNextByProdcode.action",method = RequestMethod.GET)
	public ModelMap getNextByProdcode(int pi_id,String pd_whcode){	
		if(pd_whcode == null || pi_id==0){
		    throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		return success(pdaOutMaterialService.getNextByProdcode(pi_id,pd_whcode));
	}	
	
	/**
	 按批次汇总
	 */
	@RequestMapping(value="/getNextByBatch.action",method = RequestMethod.GET)
	public ModelMap getNextByBatch(int pi_id,String pd_whcode){	
		if(pd_whcode == null || pi_id==0){
		    throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		return success(pdaOutMaterialService.getNextByBatch(pi_id,pd_whcode));
	}
	
	/**
	 材料出库撤销备料
	 */
	@RequestMapping(value="/deleteDetail.action",method = RequestMethod.GET)
	public ModelMap deleteDetail (Integer bi_piid,String barcode ,String outboxcode, String whcode){	
		if(whcode == null || bi_piid==0){
		    throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		return success(pdaOutMaterialService.deleteDetail(bi_piid,barcode,outboxcode,whcode));
	}	
	
	/**
	 获取数据信息
	 */
	@RequestMapping(value="/getBarcodeData.action",method = RequestMethod.GET)
	public ModelMap getBarcodeData (String barcode){	
		if(barcode == null || ("").equals(barcode)){
		    throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		return success(pdaOutMaterialService.getBarcodeData(barcode));
	}	
	/**
	 修改条码数量
	 */
	@RequestMapping(value="/updateBarCodeQty.action",method = RequestMethod.POST)
	public ModelMap updateBarCodeQty( String barcode,double nowqty){	
		if(barcode == null || ("").equals(barcode) || nowqty==0){
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		return success(pdaOutMaterialService.updateBarCodeQty( barcode,nowqty));
	}	
	
	/**
	 特殊出库
	 */
	@RequestMapping(value="/specialOut.action",method = RequestMethod.POST)
	public ModelMap specialOut  (String barcode,String reason ,Integer  id ,String wh_code){	
		if(barcode == null || ("").equals(barcode) || id== null || id==0){
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		return success(pdaOutMaterialService.specialOut(barcode,reason,id,wh_code));
	}	
	
	
	/**
	 * 多出库单同时备料
	 * 根据出库单号获取需要出库采集的信息数据,
	 * @param int []id  出货单id数组
	 * @param type   按物料数量汇总 byProdcode  按批次数量汇总byBatch
	 * @return
	 */
	@RequestMapping(value="/getNeedGetListDeal.action",method = RequestMethod.GET)
	public ModelMap getNeedGetListDeal(String ids,String type,Integer page,Integer pagesize){
		if (ids == null || type==null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		List<Map<String,Object>> map = pdaOutMaterialService.getNeedGetListDeal(ids,type,page,pagesize);
		return success(map);
	}
	
	/**
	 * 多出库单同时备料
	 * 按照批次数量汇总原材料出库采集条码，将符合条件的条码插入值barcodeIo表中
	 * @param barcode 条码号
	 * @param ids   出库单ID ,用逗号隔开
	 * @param type 采集的是条码或箱号  条码号 barcode  外箱号 package
	 * @return 将采集成功的条码信息返回值客户端显示
	 */
	@RequestMapping(value="/outByBatchDeal.action",method = RequestMethod.GET)
	public ModelMap outByBatchDeal(String ids,String barcode,String type,boolean msdcheck){	
		if (barcode == null || ids == null|| type==null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		return success(pdaOutMaterialService.outByBatchDeal(barcode,ids,type,msdcheck));
	}
	
	/**
	 * 多出库单同时备料
	 * 按批次汇总
	 */
	@RequestMapping(value="/getNextByBatchDeal.action",method = RequestMethod.GET)
	public ModelMap getNextByBatchDeal(String ids){	
		if(ids==null){
		    throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		return success(pdaOutMaterialService.getNextByBatchDeal(ids));
	}	
	
	/**
	 * 多出库单同时备料
	 * 材料出库撤销备料
	 */
	@RequestMapping(value="/deleteDetailDeal.action",method = RequestMethod.GET)
	public ModelMap deleteDetailDeal(String ids,String barcode ,String outboxcode){	
		if(ids == null){
		    throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		return success(pdaOutMaterialService.deleteDetailDeal(ids,barcode,outboxcode));
	}	
	
	/**
	 * 多工单备料
	 *特殊出库
	 */
	@RequestMapping(value="/specialOutDeal.action",method = RequestMethod.POST)
	public ModelMap specialOutDeal (String barcode,String reason ,String ids){	
		if(barcode == null || ("").equals(barcode)){
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		return success(pdaOutMaterialService.specialOutDeal(barcode,reason,ids));
	}	
	
	/**
	 * 多工单备料
	 * 获取已备料数据
	 * @return
	 */
	@RequestMapping(value="/getHaveSubmitListDeal.action",method = RequestMethod.GET)
	public ModelMap getHaveSubmitListDeal (String ids,Integer page,Integer pagesize){
		return success(pdaOutMaterialService.getHaveSubmitListDeal(ids,page,pagesize));
	}
	
	/**
	 * 多工单备料
	 * 拆分
	 * @return
	 */
	@RequestMapping(value="/outByBatchBreakingDeal.action",method = RequestMethod.POST)
	public ModelMap outByBatchBreakingDeal (String ids,String barcode,Double or_remain,Double bar_remain){
		return success(pdaOutMaterialService.outByBatchBreakingDeal(ids,barcode,or_remain,bar_remain,"barcode"));
	}
	
	/**
	 * 新增，多工单，单个工单都用该方法
	 * 特殊出库
	 */
	@RequestMapping(value="/specialOutBreaking.action",method = RequestMethod.POST)
	public ModelMap specialOutBreaking (String ids,String barcode,Double or_remain,Double bar_remain,String reason){	
		if(ids== null ||barcode == null || ("").equals(barcode) || reason==null){
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		return success(pdaOutMaterialService.specialOutBreaking(ids,barcode,or_remain,bar_remain,reason));
	}	
	
	/**
	 * 根据客户端页面中传来的id来返回状态
	 * @return
	 */
	@RequestMapping(value="/getProdOutStatus.action",method = RequestMethod.GET)
	public ModelMap getProdOutStatus(String ids){
		if (ids == null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		List<Map<String, Object>> map = pdaOutMaterialService.getProdOutStatus(ids);
		return success(map);
	}
}
