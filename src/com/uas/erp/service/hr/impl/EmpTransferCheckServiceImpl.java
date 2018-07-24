package com.uas.erp.service.hr.impl;


import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.EmpTransferCheckService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EmpTransferCheckServiceImpl implements EmpTransferCheckService {

	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void save(String formStore, String gridStore,String   caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		if(store.get("ec_caller")!=null&&"Turnposition".equals(store.get("ec_caller"))){//员工调动申请单
			Object[] ob1=baseDao.getFieldsDataByCondition("Emptransfercheck", new String[]{"ec_id","ec_code"}, "ec_codevalue='"+store.get("ec_codevalue")+"' and ec_caller='Turnposition'");
			if(ob1!=null){
				BaseUtil.showError("此员工调动申请的任务交接单已存在，单号："
						+ "<a href=\"javascript:openUrl('jsps/hr/emplmana/employee/empTransferCheck.jsp?formCondition=ec_idIS" + ob1[0] +  "&gridCondition=ecd_ecidIS"+ob1[0]+"')\">" + ob1[1] + "</a>&nbsp;");	
			}
			Object[] ob=baseDao.getFieldsDataByCondition("Turnposition", 
					new String[]{"tp_id","tp_emcode","tp_emname"}, "tp_code='"+store.get("ec_codevalue")+"'");
			if(ob==null){
				BaseUtil.showError("单号为"+store.get("ec_codevalue")+"的员工调动申请单不存在，请重新确认");
			}else{
				store.put("ec_keyvalue", ob[0]);
				store.put("ec_emcode", ob[1]);
				store.put("ec_emname", ob[2]);
			}
		}
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Emptransfercheck",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
	}

	@Override
	public void updateEmpTransferCheckById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[]{store,gstore});
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "EmpTransferCheck", "ec_id");
		baseDao.execute(formSql);
		List<String> gridSql = new ArrayList<String>();
		for(Map<Object, Object> s:gstore){
			gridSql.add("update EmpTransferCheckdet set ecd_emid='"+s.get("ecd_emid")+"' ,ecd_emcode='"+
					s.get("ecd_emcode")+"', ecd_emname='"+s.get("ecd_emname")+"' where ecd_id="+s.get("ecd_id"));
			gridSql.add("update EmpTransferCheckDET set ecd_change=-1 where ecd_id="+s.get("ecd_id"));
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "ec_id", store.get("ec_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store,gstore});
	}

	@Override
	public void deleteEmpTransferCheck(int id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] {id});
		// 删除emptransfercheck
		baseDao.deleteById("emptransfercheck", "ec_id",id);
		// 删除emptransfercheckdet
		baseDao.deleteById("emptransfercheckdet", "ecd_ecid", id);
		// 记录操作
		baseDao.logger.delete(caller, "ec_id", id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] {id});
	}

	@Override
	public void auditEmpTransferCheck(int id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("EmpTransferCheck", "ec_statuscode", "ec_id=" + id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] {id });
		// 执行审核操作
		List<Object[]> obs=baseDao.getFieldsDatasByCondition("EMPTRANSFERCHECKDET LEFT JOIN EMPTRANSFERCHECKSET ON ECD_SETID=ID",
				new String[] {"type_rel","ecd_master","ecd_table","field_rel","ecd_emid","ecd_emcode","ecd_emname",
				"keyfield_rel","ecd_keyvalue","ecd_updatesql"},"ecd_ecid="+id+" and allowupdate='true'");
		if(obs!=null){
			List<String> gridSql = new ArrayList<String>();
			for(Object[] ob : obs){
				String type=ob[0].toString();
				String sql="";
				String table=ob[2].toString();
				if(ob[1]!=null&& !"".equals(ob[1].toString()) ){
					table=ob[1].toString()+"."+table;
				}
				if("ID".equals(type)){
					 sql = "update "+table+" set "+ob[3].toString()+"='"+ob[4].toString()+"' where "+ob[7].toString()+"='"+ob[8].toString()+"'";
				}else if("CODE".equals(type)){
					sql = "update "+table+" set "+ob[3].toString()+"='"+ob[5].toString()+"' where "+ob[7].toString()+"='"+ob[8].toString()+"'";
				}else if("NAME".equals(type)){
					sql = "update "+table+" set "+ob[3].toString()+"='"+ob[6].toString()+"' where "+ob[7].toString()+"='"+ob[8].toString()+"'";
				}
				gridSql.add(sql);
				if(ob[9]!=null&&!"".equals(ob[9].toString())){
					gridSql.add(ob[9].toString());
				}
			}
			baseDao.execute(gridSql);
		}
		baseDao.audit("EmpTransferCheck", "ec_id=" + id, "ec_status", "ec_statuscode");
		// 记录操作
		baseDao.logger.audit(caller, "ec_id", id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] {id });
	}

	@Override
	public void resAuditEmpTransferCheck(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("EmpTransferCheck", "ec_statuscode", "ec_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("EmpTransferCheck", "wo_id=" + id, "ec_status", "ec_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "wo_id", id);
	}

	@Override
	public void submitEmpTransferCheck(int id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("EmpTransferCheck", "ec_statuscode", "ec_id=" + id);
		StateAssert.submitOnlyEntering(status);
		/*int count=baseDao.getCount("select count(1) from emptransfercheckdet where ecd_change=0 and ecd_ecid="+id);
		if(count>0){
			BaseUtil.showError("明细行有未变更数据，不能提交");
		}*/
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{id});
		Object dets = baseDao.getFieldDataByCondition("EmpTransferCheckdet left join EmpTransferCheckSet on ecd_setid=id", "WMSYS.WM_CONCAT(EMPTRANSFERCHECKDET.ECD_DETNO)", "ECD_ECID="+id+" and nvl(allowupdate,'false')='true' and ecd_emcode is null");
		if(dets!=null){
			BaseUtil.showError("序号为"+dets+"的变更后员工编号不允许为空！");
		}
		//执行提交操作
		baseDao.submit("EmpTransferCheck", "ec_id=" +id, "ec_status", "ec_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "ec_id", id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{id});
	}

	@Override
	public void resSubmitEmpTransferCheck(int id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("EmpTransferCheck","ec_statuscode", "ec_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] {id});
		// 执行反提交操作
		baseDao.resOperate("EmpTransferCheck", "ec_id=" + id, "ec_status", "ec_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ec_id", id);
		handlerService.afterResSubmit(caller, new Object[] {id});
	}

	@Override
	public void check(int id, String caller) {
		baseDao.deleteByCondition("EmpTransferCheckdet","ecd_ecid="+id);
		Object[] ob=baseDao.getFieldsDataByCondition("EmpTransferCheck", 
				new String[]{"ec_keyvalue","ec_caller"}, "ec_id="+id);
		if(ob!=null&&ob[0]!=null&&ob[1]!=null&&!"".equals(ob[0])&&!"".equals(ob[1])){
			baseDao.callProcedure("SP_EmpTransferCheck",  new Object[] {id,ob[1],ob[0]});
		}
		baseDao.updateByCondition("EmpTransferCheck","ec_check=-1","ec_id="+id);
	}

	@Override
	public void turnEmpTransferCheck(int id, String caller) {
		Object[] ob=null;
		if("Turnposition".equals(caller)){
			ob=baseDao.getFieldsDataByCondition("Turnposition", new String[]{"tp_code","tp_emcode","tp_emname"}, "tp_id="+id);
		}else if("Turnover".equals(caller)){
			ob=baseDao.getFieldsDataByCondition("Turnover", new String[]{"to_code","to_applymancode","to_applyman"}, "to_id="+id);			
		}
		int count=baseDao.getCountByCondition("EmpTransferCheck","(ec_caller='"+caller+"' or ec_caller like '"+caller+"!%') and EC_CODEVALUE='"+ob[0]+"'");
		if(count >0){
			BaseUtil.showError("已经存在人员任务异动交接单，请先删除再重新转！");
		}
		List<Object> callers=baseDao.getFieldDatasByCondition("EmpTransferCheckset", "caller", "caller='"+caller+"' or caller like '"+caller+"!%'");
		/**
		 * @author wsy
		 * 反馈编号：2017040625
		 * 人员异动申请单转任务交接时检测是否有交接设置，没有设置时提示【没有交接设置，生成交接单失败，请先维护交接设置】
		 */
		if(callers.size()==0){
			BaseUtil.showError("没有交接设置，生成交接单失败，请先维护交接设置!");
		}
		for (Object c : callers) {
			baseDao.execute("Insert into EmpTransferCheck (EC_ID,EC_CALLER,EC_KEYVALUE,EC_CODEVALUE,EC_CODE,EC_STATUS,EC_STATUSCODE,EC_EMCODE,EC_EMNAME) "
					+ "values (EmpTransferCheck_SEQ.NEXTVAL,'"+c+"','"+id+"','"+ob[0]+"','"+baseDao.sGetMaxNumber("EmpTransferCheck", 2)+"','在录入','ENTERING','"+ob[1]+"','"+ob[2]+"')");
		}
	}
}
