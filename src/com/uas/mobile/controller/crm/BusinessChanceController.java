package com.uas.mobile.controller.crm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Key;
import com.uas.erp.service.common.EmployeeService;
import com.uas.mobile.service.BusinessChanceService;

@Controller("mobileBusinessChanceController")
public class BusinessChanceController {

	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private BusinessChanceService businessChanceService;
	
	@Autowired
	private BaseDao baseDao;
	
	@RequestMapping("/mobile/crm/isTurnToCustomer.action")
	@ResponseBody
	public Map<String,Object> isTurnToCustomer (String bc_code) {
		Map<String, Object> model = new HashMap<String, Object>();
		boolean config = baseDao.isDBSetting("usePreCustomer");
		Boolean  iflag=true;
		if (config) {	
			/*Object count=baseDao.getFieldDataByCondition("PreCustomer", "count(1)", "cu_nichecode='"+bc_code+"'");
			if(!count.toString().equals("0")){
				iflag=false;				
			}*/			
		}else{			
			Object count=baseDao.getFieldDataByCondition("customer", "count(1)", "cu_nichecode='"+bc_code+"'");
			if(!count.toString().equals("0")){
				iflag=false;
			}
		}
		model.put("success", iflag);
		return model;
	}

	
	/**根据员工编号和商机阶段获取商机列表
	 * @param currentdate 
	 * @return
	 */
	@RequestMapping("/mobile/crm/getBusinessChancebyMonthAndProcess.action")
	@ResponseBody
	public Map<String,Object> getBusinessChanceByMonthAndProcess(HttpServletRequest request,String emcode,String currentdate,String currentprocess,int page,int pageSize){
		Employee employee=(Employee)request.getSession().getAttribute("employee");
		if(employee==null) BaseUtil.showError("会话已断开!");
		
		int end = page*pageSize;
		int start = end - pageSize;
		
		if (currentdate==null) currentdate="1=1";
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("listdata", businessChanceService.getBusinessChanceByMonthAndProcess(emcode,currentdate,currentprocess,start,end));
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}
	
	/**获取商机列表
	 * @param currentdate 
	 * @return
	 */
	@RequestMapping("/mobile/crm/getBusinessChancebyMonth.action")
	@ResponseBody
	public Map<String, Object> getBusinessChancebyMonth(HttpServletRequest request,String currentdate) {
		Employee employee=(Employee)request.getSession().getAttribute("employee");
		if(employee==null) BaseUtil.showError("会话已断开!");
		
		if (currentdate==null) currentdate="1=1";
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("chances", businessChanceService.getBusinessChancebyMonth(currentdate,employee));
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}
	
	//创建商机界面
	/**获取商机来源bc_from/获取商机库bc_nichehouse
	 * @param caller 
	 * @return
	 */
	@RequestMapping("/mobile/crm/getBusinessChanceCombo.action")
	@ResponseBody
	public Map<String, Object> getBusinessChanceCombo(HttpServletRequest request,String caller,String field) {
		Employee employee=(Employee)request.getSession().getAttribute("employee");
		if(employee==null) BaseUtil.showError("会话已断开!");
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("combos", businessChanceService.getBusinessChanceCombo(employee,caller,field));
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}
	
	/**获取商机阶段
	 * @param condition 
	 * @return
	 */
	@RequestMapping("/mobile/crm/getBusinessChanceStage.action")
	@ResponseBody
	public Map<String, Object> getBusinessChanceStage(HttpServletRequest request,String condition) {
		Employee employee=(Employee)request.getSession().getAttribute("employee");
		if(employee==null) BaseUtil.showError("会话已断开!");
		
		if (condition==null) condition="1=1";
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("stages", businessChanceService.getBusinessChanceStage(condition));
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}
	
	/**获取跟进人bc_doman
	 * @param condition 
	 * @return
	 */
	@RequestMapping("/mobile/crm/getBusinessChanceRecorder.action")
	@ResponseBody
	public Map<String, Object> getBusinessChanceRecorder(HttpServletRequest request,String condition) {
		Employee employee=(Employee)request.getSession().getAttribute("employee");
		if(employee==null) BaseUtil.showError("会话已断开!");
		
		if (condition==null) condition="1=1";
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("recorders", businessChanceService.getBusinessChanceRecorder(condition));
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}
	
	/**更新跟进人(抢商机时更新跟进人)
	 * @param String code
	 * @param caller 
	 * @return
	 */
	@RequestMapping("/mobile/crm/updateBusinessChanceDoman.action")
	@ResponseBody
	public Map<String, Object> updateBusinessChanceDoman(HttpServletRequest request,
			String bc_code,String bc_doman,String bc_domancode,int type) {
		Employee employee=(Employee)request.getSession().getAttribute("employee");
		if(employee==null) BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		
		if(baseDao.isDBSetting("BusinessBasis", "businessGetLimit")){
			businessChanceService.isBusinesslimit(bc_doman);
		}
		
		updateDoman(bc_code,bc_doman,bc_domancode,type);			
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}
	public void updateDoman(String bc_code,String bc_doman,String bc_domancode,int type) {

		boolean flag = baseDao.checkIf("USER_TAB_COLUMNS", "TABLE_NAME = 'HRORG' and column_name = 'AGENTNAME'");
		
		if(flag){
			Employee employee = SystemSession.getUser();
			Object agentname=baseDao.getFieldDataByCondition("hrorg", "agentname", " nvl(or_status,' ')<>'已禁用' and exists (select 1 from  job where jo_code='"+employee.getEm_defaulthscode()+"' and nvl(ISAGENT,0)=-1) and or_code=(select em_defaultorcode from employee where em_code='"+bc_domancode+"')");
			if(agentname!=null&&agentname!=""&&!agentname.equals("")){
				Object BC_AGENCY = baseDao.getFieldDataByCondition("businesschance", "BC_AGENCY", "bc_code='"+bc_code+"'");		
				Object BD_AGENCY=baseDao.getFieldDataByCondition("businessdatabase", "BD_AGENCY","BD_NAME=( select BC_NICHEHOUSE from businesschance where bc_code='"+bc_code+"')");	
				Object BD_PROP=baseDao.getFieldDataByCondition("businessdatabase", "BD_PROP","BD_NAME=( select BC_NICHEHOUSE from businesschance where bc_code='"+bc_code+"')");		
				Object ISAGENT=baseDao.getFieldDataByCondition("job", "nvl(ISAGENT,0)", "JO_CODE=(select EM_DEFAULTHSCODE from employee where em_code='"+bc_domancode+"')");			
				if(BC_AGENCY!=null&&BC_AGENCY!=""&&!BC_AGENCY.equals("")
						&&BD_AGENCY!=null&&BD_AGENCY!=""&&!BD_AGENCY.equals("")
						&&BD_PROP!=null&&BD_PROP!=""&&!BD_PROP.equals("")){

					if((BC_AGENCY.equals(agentname)||(BD_AGENCY.equals(agentname)&&BD_PROP.toString().equals("可领取可分配")))&&(ISAGENT.toString().equals("-1"))){
						
						System.out.println("商机符合代理商，抢占");
						businessChanceService.updateBusinessChanceDoman(bc_code,bc_doman,bc_domancode);						
						businessChanceService.updateBusinessChanceDataMsg(bc_code, bc_doman, bc_domancode,type);	
	
					}else {
						BaseUtil.showError("你没有权限抢占该商机");
					}	
				}else{
					BaseUtil.showError("商机录入的数据有误,请重新输入");
					
				}
			}else {
				System.out.println("人员表中所属代理商是空的，随便抢");
				businessChanceService.updateBusinessChanceDoman(bc_code,bc_doman,bc_domancode);
				businessChanceService.updateBusinessChanceDataMsg(bc_code, bc_doman, bc_domancode,type);
				
			}
	
		}else {
			System.out.println("不存在代理商配置的字段");
			businessChanceService.updateBusinessChanceDoman(bc_code,bc_doman,bc_domancode);
			businessChanceService.updateBusinessChanceDataMsg(bc_code, bc_doman, bc_domancode,type);
			
		}	
	}

	//商机失效界面接口  商机处理表BusinessChanceData(商机跟进失效等等接口)
	/**保存商机处理记录
	 * @param gridStore
	 * @param caller 
	 * @return
	 */
	@RequestMapping("/mobile/crm/updatebusinessChanceData.action")
	@ResponseBody
	public Map<String, Object> updatebusinessChanceData(HttpServletRequest request,String gridStore, String caller) {
		Employee employee=(Employee)request.getSession().getAttribute("employee");
		if(employee==null) BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		businessChanceService.updatebusinessChanceData(gridStore,caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}
	
	/**商机失效 更新商机状态为失效
	 * @param formStore
	 * @param caller 
	 * @return
	 */
	@RequestMapping("/mobile/crm/abateBusinessChance.action")
	@ResponseBody
	public Map<String, Object> abateBusinessChance(HttpServletRequest request,int bcd_id, String caller) {
		Employee employee=(Employee)request.getSession().getAttribute("employee");
		if(employee==null) BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		businessChanceService.abateBusinessChance(bcd_id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
		//商机转移界面接口
		/**转移到商机库 bc_nichehouse
		 * @param String code
		 * @param caller 
		 * @return
		 */
		@RequestMapping("/mobile/crm/updateBusinessChanceHouse.action")
		@ResponseBody
		public Map<String, Object> updateBusinessChanceHouse(HttpServletRequest request,String bc_code,String bc_nichehouse) {
			Employee employee=(Employee)request.getSession().getAttribute("employee");
			if(employee==null) BaseUtil.showError("会话已断开!");
			Map<String, Object> modelMap = new HashMap<String, Object>();
			businessChanceService.updateBusinessChanceHouse(bc_code,bc_nichehouse);
			modelMap.put("success", true);
			return modelMap;
		}
		
		/**更新客户
		 * @param String bc_custcode
		 * @param caller 
		 * @return
		 */
		@RequestMapping("/mobile/crm/updateBusinessChanceCust.action")
		@ResponseBody
		public Map<String, Object> updateBusinessChanceCust(HttpServletRequest request,String bc_code,String cu_code,String cu_name) {
			Employee employee=(Employee)request.getSession().getAttribute("employee");
			if(employee==null) BaseUtil.showError("会话已断开!");
			Map<String, Object> modelMap = new HashMap<String, Object>();
			businessChanceService.updateBusinessChanceCust(bc_code,cu_code,cu_name);
			modelMap.put("success", true);
			modelMap.put("sessionId", request.getSession().getId());
			return modelMap;
		}
		
		/**商机释放接口bc_type 
		 * @return
		 */
		@RequestMapping("/mobile/crm/updateBusinessChanceType.action")
		@ResponseBody
		public Map<String, Object> updateBusinessChanceType(HttpServletRequest request,String bc_code,String bc_nichehouse) {
			Employee employee=(Employee)request.getSession().getAttribute("employee");
			if(employee==null) BaseUtil.showError("会话已断开!");
			Map<String, Object> modelMap = new HashMap<String, Object>();
			businessChanceService.updateBusinessChanceType(bc_code,bc_nichehouse);
			modelMap.put("success", true);
			return modelMap;
		}
		/**按条件获取商机列表 
		 * @return
		 */
		@RequestMapping("/mobile/crm/getNicheByCondition.action")
		@ResponseBody
		public Map<String, Object> getNicheList(HttpServletRequest request,String bc_domancode,int pageIndex ) {
			Employee employee=(Employee)request.getSession().getAttribute("employee");
			if(employee==null) BaseUtil.showError("会话已断开!");
			
			Map<String, Object> modelMap = new HashMap<String, Object>();
			modelMap.put("lista", businessChanceService.getnichedata(bc_domancode,0,pageIndex));
			modelMap.put("listb", businessChanceService.getnichedata(bc_domancode,1,pageIndex));
			modelMap.put("listc", businessChanceService.getnichedata(bc_domancode,2,pageIndex));
			modelMap.put("listd", businessChanceService.getnichedata(bc_domancode,3,pageIndex));
			modelMap.put("counta", businessChanceService.nichlecount(bc_domancode, 0));
			modelMap.put("countb", businessChanceService.nichlecount(bc_domancode, 1));
			modelMap.put("countc", businessChanceService.nichlecount(bc_domancode, 2));
			modelMap.put("countd", businessChanceService.nichlecount(bc_domancode, 3));
			return modelMap;
		}
		/** 商机添加日程更新商机跟进时间 
		 * @return
		 */
		@RequestMapping("/mobile/crm/updateLastdate.action")
		@ResponseBody
		public Map<String, Object> updateLastdate(HttpServletRequest request,String bc_code) {
			Employee employee=(Employee)request.getSession().getAttribute("employee");
			if(employee==null) BaseUtil.showError("会话已断开!");
			Map<String, Object> modelMap = new HashMap<String, Object>();
			businessChanceService.updateLastdate(bc_code);
			modelMap.put("success", true);
			modelMap.put("sessionId", request.getSession().getId());
			return modelMap;
			}
		/**获取商机库
		 * @param caller 
		 * @return
		 */
		@RequestMapping("/mobile/crm/getNichehouse.action")
		@ResponseBody
		public Map<String, Object> getNichehouse(HttpServletRequest request) {
			Employee employee=(Employee)request.getSession().getAttribute("employee");
			if(employee==null) BaseUtil.showError("会话已断开!");
			
			Map<String, Object> modelMap = new HashMap<String, Object>();
			modelMap.put("combos", businessChanceService.getNichehouse());
			modelMap.put("success", true);
			modelMap.put("sessionId", request.getSession().getId());
			return modelMap;
		}
		@RequestMapping("/mobile/crm/isSysAdmin.action")
		@ResponseBody
		public Map<String, Object> isadmin(HttpServletRequest request,String em_code) {
			Employee employee=(Employee)request.getSession().getAttribute("employee");
			if(employee==null) BaseUtil.showError("会话已断开!");
			Map<String, Object> modelMap = new HashMap<String, Object>();
			String result=businessChanceService.isadmin(em_code);
			modelMap.put("result", result);
			modelMap.put("sessionId", request.getSession().getId());
			return modelMap;
		}
		
		/*
		 * 获取阶段要点
		 */
		@RequestMapping("/mobile/crm/getStagePoints.action")
		@ResponseBody
		public Map<String, Object> getStagePoints(HttpServletRequest request,String bccode,String currentStep) {
			Employee employee=(Employee)request.getSession().getAttribute("employee");
			if(employee==null) BaseUtil.showError("会话已断开!");
			Map<String, Object> modelMap = new HashMap<String, Object>();
			modelMap = businessChanceService.getStagePoints(bccode,currentStep);
			//modelMap.put("sessionId", request.getSession().getId());
			return modelMap;
		}
		/*
		 * 添加联系人接口
		 */
		@RequestMapping("/mobile/crm/addContactPerson.action")
		@ResponseBody
		public Map<String, Object> addContactPerson(HttpServletRequest request,String caller,String formStore){
			Map<String, Object> modelMap = new HashMap<String,Object>();
			modelMap=businessChanceService.addContactPerson(caller,formStore);
			modelMap.put("sessionId", request.getSession().getId());
			modelMap.put("success", true);
			return modelMap;
		}
		/*
		 * 商机界面搜索接口
		 */
		@RequestMapping("/mobile/crm/searchData.action")
		@ResponseBody
		public Map<String,Object> searchData(HttpServletRequest request,String stringSearch,int page){
			Map<String,Object> modelMap=new HashMap<String,Object>();
			int pageSize=20;
			int end = page*pageSize;
			int start = end - pageSize;
			modelMap.put("listdata", businessChanceService.searchData(stringSearch,start,end));
			modelMap.put("sessionId", request.getSession().getId());
			modelMap.put("success", true);
			return modelMap;
		}
}
