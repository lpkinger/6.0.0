package com.uas.mes.api.pda;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.erp.service.common.SingleFormItemsService;
import com.uas.mes.api.core.BaseApiController;
import com.uas.pda.service.PdaCommonService;

/**
 * 获取配置基础数据等接口
 * @data  2016年12月21日 下午2:19:44
 */

@RestController("api.pdaCommonController")
@RequestMapping("/api/pda/common")
public class PdaCommonController extends BaseApiController{

	@Autowired
	private PdaCommonService pdaCommonService;
	@Autowired
	private SingleFormItemsService singleFormItemsService;
	
	/**
	 * 清空采集数据
	 * @param data
	 */
	@RequestMapping(value = "/clearGet.action", method = RequestMethod.POST)
	public ModelMap clearGet(Integer id ,String whcode){
		if (id == null || whcode == null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		pdaCommonService.clearGet(id,whcode);
		return success();
	}
	
	/**
	 * 拿到关于field的数据
	 * 
	 * @param caller
	 *            tablename
	 * @param field
	 *            待取值的字段
	 * @param condition
	 *            带入的条件
	 */
	@RequestMapping(value="/getFieldData.action",method = RequestMethod.POST)
	public ModelMap getFieldData(HttpSession session, String field, String caller, String condition) {
		Object ob = singleFormItemsService.getFieldData(caller, field, condition);
		return success(ob);
	}
	
	/**
	 * 根据出入库单ID 获取已经提交的采集信息，barcodeio表中数据
	 * @param bi_piid 出入库单ID
	 * @param whcode 仓库
	 * @return
	 */
	@RequestMapping(value = "/getHaveSubmitList.action", method = RequestMethod.GET)
	public ModelMap getHaveSubmitList(Integer bi_piid,String whcode) {
		if (bi_piid == null || whcode == null) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		return success(pdaCommonService.getHaveSubmitList(bi_piid,whcode));
	}
	
	
	/**
	 * 根据出入库单号，输入的条码号核对条码是否在单据中，以及数量是否正确
	 * 
	 * @return
	 */
	@RequestMapping("/getBarIoCheck.action")
	public ResponseEntity<ModelMap> getBarIoCheck(@RequestBody String json) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");
		ModelMap map = new ModelMap();
		map.put("message", pdaCommonService.getBarIoCheck(json));
		return new ResponseEntity<ModelMap>(map, headers, HttpStatus.OK);
	}


	/**
	 * 根据出入库单号，输入的条码号修改入库数量
	 * 
	 * @return
	 */
	@RequestMapping("/updateBarIoQty.action")
	public void updateBarIoQty(int id, @RequestBody String json) {
		pdaCommonService.updateBarIoQty(id, json);
	}

	/**
	 * 封装接口，将ERP的打印返回地址
	 * 
	 * @return
	 */
	@RequestMapping("/returnPdf.action")
	public ModelMap returnPdf(HttpServletRequest request, String id, String caller,String reportName) {
		return success(pdaCommonService.returnPdfUrl(request,caller,id,reportName));
	}
	
	/**
	 * 获取仓库信息
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getFieldsDatas.action",method = RequestMethod.POST)
	public ModelMap getFieldsDatas (String field, String caller, String condition) {
		if (condition == null || caller == null ) {
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		}
		List<Map<String, Object>> data = pdaCommonService.getFieldsDatas(field,caller,condition);
		return success(data);
	}

}
