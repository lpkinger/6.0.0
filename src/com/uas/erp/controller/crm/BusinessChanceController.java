package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.derby.tools.sysinfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.crm.BusinessChanceService;
@Controller
public class BusinessChanceController {
	@Autowired
	private BusinessChanceService BusinessChanceService;
	@Autowired
	private BaseDao baseDao;
	
	@RequestMapping("/crm/chance/turnCustomer.action")  
	@ResponseBody 
	public Map<String, Object> turnCustomer(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<String, Object> model = BusinessChanceService.turnCustomer(id);
		modelMap.put("id", model.get("id"));
		modelMap.put("config", model.get("config"));
		modelMap.put("success", true);
		return modelMap;
	}

	
	/**
	 * 保存（商机保存时验证最大可领取商机数）
	 */
	@RequestMapping("/crm/chance/saveBusinessChance.action")  
	@ResponseBody 
	public Map<String, Object> save(  HttpServletRequest request,String formStore,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if(baseDao.isDBSetting("BusinessBasis", "businessGetLimit")){
			Map<Object, Object> fstore = BaseUtil.parseFormStoreToMap(formStore);
			Object data = fstore.get("bc_doman");			
			if (data!=null) {
				String bc_recorder=data.toString();
				BusinessChanceService.isBusinesslimit(bc_recorder);				
			}
		}
		BusinessChanceService.saveBusinessChance( formStore, caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}
	
	
	
	/**
	 * 修改
	 */
	@RequestMapping("/crm/chance/updateBusinessChance.action")  
	@ResponseBody 
	public Map<String, Object> update( String formStore,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<Object, Object> fstore = BaseUtil.parseFormStoreToMap(formStore);
		if(baseDao.isDBSetting("BusinessBasis", "businessGetLimit")){
			Object data = fstore.get("bc_doman");
			String code = fstore.get("bc_code").toString();		
			Object oldbc_doman = baseDao.getFieldDataByCondition("businesschance", "bc_doman", "bc_code='"+code+"'");
			if (!data.equals(oldbc_doman)) {
				if(data!=null&&data!=""&&!data.equals("")){	
					String bc_recorder=data.toString();
					BusinessChanceService.isBusinesslimit(bc_recorder);	
					/*String bcd_code=baseDao.sGetMaxNumber("BUSINESSCHANCEDATA", 2);
					String sql = ("Insert into BUSINESSCHANCEDATA (BCD_ID,BCD_BCID,BCD_MAN,BCD_DATE,BCD_REMARK,BCD_STATUS,BCD_STATUSCODE,BCD_BSCODE,BCD_BSNAME,BCD_CODE,BCD_COUNT,BCD_TYPE) select BUSINESSCHANCEDATA_seq.nextval,bc_id,'"+data+"',sysdate,'商机界面更改跟进人','已审核','AUDITED',bs_code,bc_currentprocess,'"+bcd_code+"',1,'分配商机' from BUSINESSCHANCE LEFT JOIN BUSINESSCHANCESTAGE ON BC_CURRENTPROCESS=BS_NAME where bc_code='"+code+"'");		
					baseDao.execute(sql);*/
				}
				String sqlselect = "select  sourcecode from projecttask where taskorschedule='Schedule' and sourcecode='"
						+ code
						+ "' and nvl(sourcecode,' ')<>' ' and nvl(handstatus,' ')<>'已完成' and to_char(startdate,'YYYYMMDD')>=TO_CHAR(SYSDATE, 'YYYYMMDD')";
				int count = baseDao.getCount(sqlselect);
				if (count > 0) {
					String delsql = "delete from projecttask where taskorschedule='Schedule' and sourcecode='" + code + "'";
					baseDao.execute(delsql);
				}	
					
										
				}				
			}	
		if(baseDao.isDBSetting("BusinessChance", "BC_DescriptionLimit")){
			BusinessChanceService.DescriptionLimit(fstore);
		}		
		BusinessChanceService.updateBusinessChance(formStore, caller);		
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/crm/chance/deleteBusinessChance.action")  
	@ResponseBody 
	public Map<String, Object> deleteBusinessChance( int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BusinessChanceService.deleteBusinessChance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/crm/chance/submitBusinessChance.action")  
	@ResponseBody 
	public Map<String, Object> submitBusinessChance(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BusinessChanceService.submitBusinessChance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/crm/chance/resSubmitBusinessChance.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitBusinessChance(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BusinessChanceService.resSubmitBusinessChance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/crm/chance/auditBusinessChance.action")  
	@ResponseBody 
	public Map<String, Object> auditBusinessChance( int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BusinessChanceService.auditBusinessChance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/crm/chance/resAuditBusinessChance.action")  
	@ResponseBody 
	public Map<String, Object> resAuditBusinessChance( int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BusinessChanceService.resAuditBusinessChance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	
	/**
	 * 结案
	 */
	@RequestMapping("/crm/chance/endBusinessChance.action")
	@ResponseBody
	public Map<String, Object> endBusinessChance(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		BusinessChanceService.endBusinessChance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反结案
	 */
	@RequestMapping("/crm/chance/resEndBusinessChance.action")
	@ResponseBody
	public Map<String, Object> resEndBusinessChance(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		BusinessChanceService.resEndBusinessChance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 送样
	 */
	@RequestMapping("/crm/chance/SendSample.action")
	@ResponseBody
	public Map<String, Object> SendSample(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = BusinessChanceService.SendSample(id, caller);
		modelMap.put("success", true);
		modelMap.put("log", log);
		return modelMap;
	}
	/**
	 * 报价
	 */
	@RequestMapping("/crm/chance/Quote.action")
	@ResponseBody
	public Map<String, Object> Quote(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = BusinessChanceService.Quote(id, caller);
		modelMap.put("success", true);
		modelMap.put("log", log);
		return modelMap;
	}
	/**
	 * 下单
	 */
	@RequestMapping("/crm/chance/PlaceOrder.action")
	@ResponseBody
	public Map<String, Object> PlaceOrder(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = BusinessChanceService.PlaceOrder(id, caller);
		modelMap.put("success", true);
		modelMap.put("log", log);
		return modelMap;
	}
	/**
	 * 出货
	 */
	@RequestMapping("/crm/chance/Shipment.action")
	@ResponseBody
	public Map<String, Object> Shipment(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = BusinessChanceService.Shipment(id, caller);
		modelMap.put("success", true);
		modelMap.put("log", log);
		return modelMap;
	}
	
	/**
	 * 商机批量收回
	 */
	@RequestMapping("/crm/chance/callBack.action")  
	@ResponseBody 
	public Map<String, Object> callBack(String ids, String caller,String bcdids) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BusinessChanceService.callBack(ids,caller,bcdids);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 商机批量转移-转移到新的商机库
	 */
	@RequestMapping("/crm/chance/transfer.action")  
	@ResponseBody 
	public Map<String, Object> transfer(String ids,String bd_name, String caller,String bcdids) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BusinessChanceService.transfer(ids,bd_name,caller,bcdids);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 商机批量分配
	 */
	@RequestMapping("/crm/chance/busDistribute.action")  
	@ResponseBody 
	public Map<String, Object> busDistribute(String ids, String em_code,String em_name, String caller,String bcdids) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if(baseDao.isDBSetting("BusinessBasis", "businessGetLimit")){
			BusinessChanceService.isBusinesslimit(em_name);
	
		}	
		Object bb_dogap=baseDao.getFieldDataByCondition("businessbasis", "bb_dogap", "1=1");
		Object bd_orgrecovertime=baseDao.getFieldDataByCondition("businessbasis", "bd_orgrecovertime", "1=1");
		
		String [] stringArr= ids.split(","); 
		for(int i=0;i<stringArr.length;i++){
			Object time=baseDao.getFieldDataByCondition("BusinessChanceData", "(sysdate-max(bcd_date))", "bcd_man='"+em_name+"' and bcd_count=-1 and BCD_BCID='"+stringArr[i]+"'");			
			if(bb_dogap!=null&&time!=null){
				if(Integer.parseInt(String.valueOf(bb_dogap))>Integer.parseInt(String.valueOf(time).split("\\.")[0])){				
					BaseUtil.showError("商机在规定时间内不允许一个人重复跟进!");
				}

			}
			List<Object[]> orgargument=baseDao.getFieldsDatasByCondition("BusinessChanceData", new String[]{"(sysdate-bcd_date)","bcd_man"}, " bcd_count=-1 and bcd_bcid='"+stringArr[i]+"' order  by bcd_date desc");
			if(bd_orgrecovertime!=null&&!"".equals(bd_orgrecovertime)
					&&Integer.parseInt(bd_orgrecovertime.toString())!=0
						&&orgargument!=null&&!"".equals(orgargument)&&orgargument.size()>0){		
					Object manname=orgargument.get(0)[1];
					Object mandata=orgargument.get(0)[0];
					String sql="select count(1) from employee where em_code='"+em_code+"' and em_defaultorid =(select em_defaultorid from employee where em_name='"+manname+"')";
					if(baseDao.getCount(sql)>0&&Integer.parseInt(bd_orgrecovertime.toString())>=Integer.parseInt(String.valueOf(mandata).split("\\.")[0])){
						BaseUtil.showError("同组织的人人员N天不允许重复跟进有同一商机！");
					};
			}
			
			String sqlselect = "select  sourcecode from projecttask where taskorschedule='Schedule' and sourcecode=(select bc_code from BusinessChance where bc_id='"+stringArr[i]+"')"
					
					+ " and nvl(sourcecode,' ')<>' ' and nvl(handstatus,' ')<>'已完成' and to_char(startdate,'YYYYMMDD')>=TO_CHAR(SYSDATE, 'YYYYMMDD')";
			int count = baseDao.getCount(sqlselect);
			if (count > 0) {
				String delsql = "delete from projecttask where taskorschedule='Schedule' and sourcecode=(select bc_code from BusinessChance where bc_id='"+stringArr[i]+"')";
				baseDao.execute(delsql);
			}
		}
		BusinessChanceService.busDistribute(ids,em_code,em_name,caller,bcdids);					
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 商机库删除
	 */
	@RequestMapping("/crm/chance/deleteBusinessDataBase.action")  
	@ResponseBody 
	public Map<String, Object> BusinessDataBase( int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BusinessChanceService.deleteBusinessDataBase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//根据商机阶段获取阶段要点
	@RequestMapping("/crm/chance/getpoint.action")
	@ResponseBody
	public Map<String,Object> getPoint(String parameters){
		Map<String,Object> modelMap = new HashMap<String,Object>();
			modelMap = BusinessChanceService.getPoint(parameters);
			modelMap.put("success", true);
		return modelMap;
	}
	
	//商机动态获取阶段要点信息
	@RequestMapping("/crm/chance/getpointsanddata.action")
	@ResponseBody
	public Map<String,Object> getPointAndData(String parameters){
		Map<String,Object> modelMap = new HashMap<String,Object>();
			modelMap = BusinessChanceService.getPointAndData(parameters);
			modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 商机批批量冻结
	 */
	@RequestMapping("/crm/chance/businessChanceLock.action")  
	@ResponseBody 
	public Map<String, Object> businessChanceLock(String ids, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BusinessChanceService.businessChanceLock(ids,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 商机批批量重启
	 */
	@RequestMapping("/crm/chance/businessChanceRestart.action")  
	@ResponseBody 
	public Map<String, Object> businessChanceRestart(String ids, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BusinessChanceService.businessChanceRestart(ids,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**  平台商机  获取列表数据
     *   @param condition 列表查询条件
     * */
	@RequestMapping(value = "/crm/chance/getBBClist.action")
	@ResponseBody
	public Map<String, Object> getBBClist(HttpServletRequest req,String condition,int page,int pageSize) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", BusinessChanceService.getBBClist(condition,page,pageSize));
		return modelMap;
	}
	
	/**  平台商机分配
     *   @param stores 多条列表数据
     * */
	@RequestMapping(value = "/crm/chance/chooseBusinessChance.action")
	@ResponseBody
	public Map<String, Object> chooseBusinessChance(HttpServletRequest req,String stores) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log",BusinessChanceService.chooseBusinessChance(stores,"1"));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**  平台商机转报价
     *   @param formstore 多条列表数据
     * */
	@RequestMapping("/crm/chance/businessChanceTrunQuotationDown.action")  
	@ResponseBody 
	public Map<String, Object> TrunQuotationDown(String formstore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log",BusinessChanceService.TrunQuotationDown(formstore));
		modelMap.put("success", true);
		return modelMap;
	}
}
