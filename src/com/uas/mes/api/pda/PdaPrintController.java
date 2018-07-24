package com.uas.mes.api.pda;

/**
 * 打印接口
 * @data  2016年12月21日 下午2:19:44
 */

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.model.Employee;
import com.uas.mes.api.core.BaseApiController;
import com.uas.pda.service.PdaCommonService;
import com.uas.pda.service.PdaPrintService;

@RestController("api.pdaPrintController")
@RequestMapping("/api/pda/print")
public class PdaPrintController extends BaseApiController{
  
	@Autowired 
	private PdaPrintService pdaPrintService;
    @Autowired 
   	private  PdaCommonService pdaCommonService;
	
	/**
	 * PDA打印
	 * @param data  打印数据
	 * @param printIp  打印机Ip
	 * @param port  打印机端口
	 * @param dpi  打印机分辨率
	 * @return
	 */
	@RequestMapping(value="/labelPrint.action",method = RequestMethod.GET)
	public ModelMap labelPrint(HttpServletRequest request,String data){
		List<Map<Object, Object>> gstore =  BaseUtil.parseGridStoreToMaps(data);
		StringBuffer sb=new StringBuffer();
		String str=null;
		if(gstore.get(0)!=null && !("").equals(gstore.get(0))){
			for (Map<Object, Object> map : gstore) {
				Object bar_id=map.get("BAR_ID");
				sb.append(bar_id+",");
			}
				str=sb.toString().substring(0,sb.length()-1);
			}
		return success(pdaCommonService.returnPdfUrl(request, "Barcode!Print", str, ""));
	}
	
	/**
	 * PDA打印设置
	 * @param printIp 打印机IP
	 * @param port 打印机端口
	 * @param dpi
	 * @return
	 */
	@RequestMapping(value="/setDefaultPrint.action",method = RequestMethod.POST)
	public ModelMap setDefaultPrint(@RequestBody String data,HttpSession session){
		Employee em = (Employee) session.getAttribute("employee");
		int em_id = em.getEm_id();
		pdaPrintService.setDefaultPrint(data,em_id);
		return success();
	}
	
	/**
	 * 获取PDA打印设置
	 * @param data  打印的数据
	 * @param printIp 打印机IP
	 * @param port 打印机端口
	 * @param dpi
	 * @return
	 */
	@RequestMapping(value="/getDefaultPrint.action",method = RequestMethod.GET)
	public  ModelMap getDefaultPrint(HttpSession session){
		Employee em = (Employee) session.getAttribute("employee");
		int em_id = em.getEm_id();
		Map<String, Object> map = pdaPrintService.getDefaultPrint(em_id);
		return success(map);
	}
	
	/**
	 *测试打印,根据DPI和打印数据，返回打印zpl指令
	 * @param dpi
	 * @return
	 */
	@RequestMapping(value="/zplPrint.action",method = RequestMethod.POST)
	public  ModelMap zplPrint(String caller,String dpi,String data){
		return success(pdaPrintService.zplPrint(caller,dpi,data));
	}
	
	/**
	 *打印类型
	 * @param dpi
	 * @return
	 */
	@RequestMapping(value="/getPrintType.action",method = RequestMethod.POST)
	public  ModelMap zplPrintType(String type){
		return success(type);
	}
	
	/**
	 *打印类型
	 * @param 条码信息
	 * @return
	 */
	@RequestMapping(value="/vendorZplPrint.action",method = RequestMethod.POST)
	public  ModelMap vendorZplPrint(String caller,String data){
		return success(pdaPrintService.vendorZplPrint(caller,data));
	}
}
