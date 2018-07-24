package com.uas.erp.controller.common;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.client.methods.HttpPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.model.Employee;
import com.uas.erp.model.FileUpload;
import com.uas.erp.model.SubsFormulaDet;
import com.uas.erp.service.common.ChartsService;

@Controller
@RequestMapping("/common/charts")
public class ChartsController {
	
	@Autowired
	private ChartsService chartsService;
	
	/**
	 * 获取订阅号
	 * */
	@RequestMapping(value="/getSubsNums.action")
	@ResponseBody
	public Map<String,Object> getSubsNum(HttpSession session,String condition) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (condition==null || condition.equals("")) condition="1=1";
		map.put("subsNums", chartsService.getSubsNums(condition));
		return map;
	}
	
	/**
	 * 获取订阅项目明细表--展示列表时调用
	 * */
	@RequestMapping(value="/getSubsFormulaDet.action")
	@ResponseBody
	public Map<String,Object> getSubsFormulaDet(HttpSession session,Integer formulaId,Integer insId) {		
		Map<String, Object> map = new HashMap<String, Object>();		
		List<SubsFormulaDet>  formulaDets=chartsService.getSubsFormulaDet(formulaId);
		map.put("formulaDets", formulaDets);
		map.put("datas", chartsService.getSubsDataDetail(formulaId,insId));
		return map;
	}
	
	/**
	 * 根据ids获取数据
	 * */
	@RequestMapping(value="/getSubsDatas.action")
	@ResponseBody
	public Map<String,Object> getSubsDatas(HttpSession session,Integer numId,Integer mainId,Integer insId,int emId) {
		Map<String, Object> map = new HashMap<String, Object>();
		//Employee employee = (Employee) session.getAttribute("employee");
		map.put("subsDatas", chartsService.getSubsData(numId,mainId,insId,emId));
		return map;
	}
	
	@RequestMapping("/mobileCharts.action")
	@ResponseBody
	public ModelAndView CustomerService(HttpServletRequest req,HttpSession session,Integer numId,Integer mainId,Integer insId,String title) {
		Map<String,Object> params=new HashMap<String,Object>();
		Employee employee=(Employee)session.getAttribute("employee");
		if(employee==null) BaseUtil.showError("会话已断开!");
		String sessionId = req.getParameter("sessionId");
		params.put("numId",numId);
		params.put("mainId",mainId);
		params.put("insId",insId);
		params.put("title",title);
		params.put("emId",employee.getEm_id());
		params.put("isMobile",sessionId!=null?1:0);
		return new ModelAndView("mobile/charts",params);
	}
	
	/**
	 * 保存
	 */
	@RequestMapping("/save.action")
	@ResponseBody
	public Map<String, Object> save(String caller,  String formStore, String param,String param1, String param2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chartsService.save(formStore, param, caller,param1, param2);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete.action")
	@ResponseBody
	public Map<String, Object> delete(String caller,  int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chartsService.delete(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param,String param1, String param2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chartsService.update(formStore, param,caller,param1, param2);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/submit.action")
	@ResponseBody
	public Map<String, Object> submit(String caller,  int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chartsService.submit(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反提
	 */
	@RequestMapping("/resSubmit.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller,  int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chartsService.resSubmit(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 审核
	 */
	@RequestMapping("/audit.action")
	@ResponseBody
	public Map<String, Object> audit(String caller,  int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chartsService.audit(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反审
	 */
	@RequestMapping("/resAudit.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller,  int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chartsService.resAudit(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 禁用
	 */
	@RequestMapping("/bannedCharts.action")
	@ResponseBody
	public Map<String, Object> bannedCharts(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chartsService.bannedCharts(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反禁用
	 */
	@RequestMapping("/resBannedCharts.action")
	@ResponseBody
	public Map<String, Object> resBannedCharts(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chartsService.resBannedCharts(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 订阅项检测
	 */
	@RequestMapping("/testSubsFormula.action")
	@ResponseBody
	public Map<String, Object> testSubsFormula(HttpSession session,int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		chartsService.testSubsFormula(employee,id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 订阅项批量检测
	 */
	@RequestMapping("/batchTestSubsFormula.action")
	@ResponseBody
	public Map<String, Object> batchTestSubsFormula(HttpSession session,String ids) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		String failIds = chartsService.batchTestSubsFormula(employee,ids);
		modelMap.put("ids", failIds);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取预览数据 getPreviewDatas.action
	 */
	@RequestMapping("/getPreviewDatas.action")
	@ResponseBody
	public Map<String, Object> getPreviewDatas(HttpSession session,int id,String caller,String params) {
		Map<String, Object> modelMap = new HashMap<String, Object>();		
		modelMap.put("datas",chartsService.getPreviewDatas(id,caller,params));
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	/**
	 * APP取消订阅
	 */
	@RequestMapping("/removeSubsMans.action")
	@ResponseBody
	public Map<String, Object> removeSubsMans(HttpSession session,String numIds,String emcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chartsService.removeSubsMans(numIds,emcode);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 我的订阅批量取消
	 */
	@RequestMapping("/vastCancelSubsApply.action")
	@ResponseBody
	public Map<String, Object> vastCancelSubsApply(HttpSession session,String datas,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chartsService.vastCancelSubsApply(caller,datas);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 订阅批量申请
	 */
	@RequestMapping("/vastAddSubsApply.action")
	@ResponseBody
	public Map<String, Object> vastAddSubsApply(HttpSession session,String ids,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee=(Employee)session.getAttribute("employee");
		String log=chartsService.vastAddSubsApply(caller,ids,employee);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 订阅申请单审核
	 */
	@RequestMapping("/auditSubsApply.action")
	@ResponseBody
	public Map<String, Object> auditSubsApply(String caller,  int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chartsService.auditSubsApply(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 订阅申请单反审
	 */
	@RequestMapping("/resAuditSubsApply.action")
	@ResponseBody
	public Map<String, Object> resAuditSubsApply(String caller,  int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chartsService.resAuditSubsApply(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//APP端接口
	/**
	 * 我的订阅
	 */
	@RequestMapping("/getPersonalSubs.action")
	@ResponseBody
	public Map<String, Object> getPersonalSubs(String em_code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> datas=chartsService.getPersonalSubs(em_code);
		modelMap.put("datas", datas);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 可申请订阅APP接口
	 */
	@RequestMapping("/getApplySubs.action")
	@ResponseBody
	public Map<String, Object> getApplySubs(String em_code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> datas=chartsService.getApplySubs(em_code);
		modelMap.put("datas", datas);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 可申请订阅UAS端接口
	 */
	@RequestMapping("/getPersonalApplySubs.action")
	@ResponseBody
	public Map<String, Object> getPersonalApplySubs(String em_code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> datas=chartsService.getPersonalApplySubs(em_code);
		modelMap.put("datas", datas);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/mobilePreview.action")
	@ResponseBody
	public ModelAndView DYpreview(HttpSession session,Integer id) throws Exception {
		Map<String,Object> params=new HashMap<String,Object>();
		Employee employee=(Employee)session.getAttribute("employee");
		if(employee==null) BaseUtil.showError("会话已断开!");
		String title= chartsService.getPreviewTitle(id);
		params.put("title",title);
		params.put("id",id);
		params.put("emId",employee.getEm_id());
		return new ModelAndView("mobile/DYpreview",params);
	}
	
	/**
	 *  获取订阅号预览数据详细
	 */
	@RequestMapping(value="/getPreviewMain.action")
	@ResponseBody
	public Map<String, Object> getPreviewMain(HttpSession session,Integer id) {
		Map<String,Object> map=new HashMap<String,Object>();
		List<Object[]> details = chartsService.getPreviewMain(id);
		map.put("details",details);
		map.put("mainImg",chartsService.getMainImg(id));
		return map;
	}
	
	@RequestMapping("/mobilePreviews.action")
	@ResponseBody
	public ModelAndView DYpreviews(HttpSession session,Integer id) throws Exception {
		Map<String,Object> params=new HashMap<String,Object>();
		Employee employee=(Employee)session.getAttribute("employee");
		if(employee==null) BaseUtil.showError("会话已断开!");
		params.put("id",id);
		params.put("emId",employee.getEm_id());
		return new ModelAndView("mobile/DYpreviews",params);
	}
	
	/**
	 *  获取订阅项预览数据详细
	 */
	@RequestMapping(value="/getPreviewsMain.action")
	@ResponseBody
	public Object[] getPreviewsMain(HttpSession session,Integer id) {
		Object[] details = chartsService.getPreviewsMain(id);
		return details;
	}
	

	//保存图片
	@RequestMapping("/saveimage.action")
	@ResponseBody
	public  String SaveImage(FileUpload Image,Integer id,String table) {
		chartsService.SaveImage(Image.getFile(),id,table);
		return "{success:true}";
	}
	
	//获取图片
	@RequestMapping("/getImage.action")
	@ResponseBody
	public void getImage(Integer id,HttpServletResponse response,String table ) {
		OutputStream os = null;
		ByteArrayInputStream bais =null;
		try {
			os = response.getOutputStream();
			response.setCharacterEncoding("utf-8");
			response.setContentType("image/jpeg");
			byte[] bytes=chartsService.getImage(id,table);
			if(bytes!=null){
				bais= new ByteArrayInputStream(bytes); 
				BufferedImage bi =ImageIO.read(bais);  
				ImageIO.write(bi, "png", os);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				os.flush();
				os.close();
				if (bais!=null) {
					bais.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	@RequestMapping("/getSubsData.action")
	@ResponseBody
	public Map<String, Object> getSubsData(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",chartsService.getSubsData(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}
}
