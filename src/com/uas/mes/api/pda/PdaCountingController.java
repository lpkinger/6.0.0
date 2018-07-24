package com.uas.mes.api.pda;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.erp.model.Page;
import com.uas.mes.api.core.BaseApiController;
import com.uas.pda.service.PdaCountingService;

/**
 * 盘点接口
 * @data  2016年12月21日 下午2:19:44
 */
@RestController("api.pdaCountingController")
@RequestMapping("/api/pda/counting")
public class PdaCountingController extends BaseApiController{

	@Autowired
	private PdaCountingService pdaCountingService;

	/**
	 * 根据盘点底稿编号获取盘点编号，仓库，ID等信息
	 * 
	 * @param st_code
	 * @return
	 */
	@RequestMapping("/getCountingData.action")
	public Page<Map<String, Object>> getCountingData(String st_code) {
		return pdaCountingService.getCountingData(st_code);
	}

	/**
	 * barcode表根据bar_code和bar_whcode
	 * 
	 * @param bar_code
	 * @param bar_whcode
	 * @return
	 */
	@RequestMapping("/getBarData.action")
	public ResponseEntity<ModelMap> getBarData(String bar_code, String bar_whcode, String st_code) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");
		ModelMap map = new ModelMap();
		map.put("data", pdaCountingService.getBarData(bar_code, bar_whcode, st_code));
		return new ResponseEntity<ModelMap>(map, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/saveBarcode.action", method = RequestMethod.POST)
	public ModelMap saveBarcode(@RequestBody String json) {
		if (StringUtils.isEmpty(json))
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS, "请传递必要的参数");
		pdaCountingService.saveBarcode(json);
		return success();
	}

	/**
	 * 完工盘点，根据输入的序列号获取归属工单，剩余数，料号，名称等信息
	 * 
	 * @param code
	 * @param whcode
	 * @return
	 */
	@RequestMapping("/serialSearch.action")
	public ResponseEntity<ModelMap> serialSearch(String code, String whcode, String st_code) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");
		ModelMap map = new ModelMap();
		map.put("message", pdaCountingService.serialSearch(code, whcode, st_code));
		return new ResponseEntity<ModelMap>(map, headers, HttpStatus.OK);
	}

	/**
	 * 完工盘点，根据输入包装箱号获取箱内总数，料号，名称等信息
	 * 
	 * @param code
	 * @param whcode
	 * @return
	 */
	@RequestMapping("/outboxSearch.action")
	public ResponseEntity<ModelMap> outboxSearch(String code, String whcode, String st_code) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json; charset=utf-8");
		ModelMap map = new ModelMap();
		map.put("message", pdaCountingService.outboxSearch(code, whcode, st_code));
		return new ResponseEntity<ModelMap>(map, headers, HttpStatus.OK);
	}
}
