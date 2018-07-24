package com.uas.erp.service.plm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.plm.PhaseChangeService;


@Service
public class PhaseChangeServiceImpl implements PhaseChangeService{
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	
	@Override
	@Transactional
	public void savePhaseChange(String caller,String formStore,String param){
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		//检查是否存在未审核的项目阶段变更申请单
		boolean bool = baseDao.checkIf("PRJPHASECHANGE", "pc_statuscode<>'AUDITED' and pc_prjcode ='"+store.get("pc_prjcode")+"'");
		if (bool) {
			BaseUtil.showError("该项目已经存在一张未审核的项目阶段变更申请单，保存失败！");
		}
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 执行保存操作
		String code = baseDao.sGetMaxNumber("PRJPHASECHANGE", 2);
		store.put("pc_code", code);
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PRJPHASECHANGE", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		//进行中的阶段吧计划开始日期强制更新为原来的日期
		for(Map<Object, Object> map:grid){			
			if ("进行中".equals(map.get("pcd_phasestatus"))) {
				map.put("pcd_newphasestart", map.get("pcd_phasestart"));
			}
			map.put("pcd_id", baseDao.getSeqId("PRJPHASECHANGEDET_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "PRJPHASECHANGEDET");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save(caller, "pc_id", store.get("pc_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });

	}
	
	@Override
	@Transactional
	public void updatePhaseChange(String caller,String formStore,String param){
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		//进行中的阶段吧计划开始日期强制更新为原来的日期
		for (Map<Object, Object> map : gstore) {
			if ("进行中".equals(map.get("pcd_phasestatus"))) {
				map.put("pcd_newphasestart", map.get("pcd_phasestart"));
			}
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });

		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PRJPHASECHANGE", "pc_id");
		baseDao.execute(formSql);		
		List<String> gridSql = SqlUtil.getInsertOrUpdateSqlbyGridStore(gstore, "PRJPHASECHANGEDET", "pcd_id");
		baseDao.execute(gridSql);
		
		// 记录操作
		baseDao.logger.update(caller, "pc_id", store.get("pc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}
	
	@Override
	public void deletePhaseChange(String  caller,int pc_id) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[] { pc_id});
		
		// 删除PRJPHASECHANGE
		baseDao.deleteById("PRJPHASECHANGE", "pc_id", pc_id);
		// 删除PRJPHASECHANGEDET
		baseDao.deleteById("PRJPHASECHANGEDET", "pcd_pcid", pc_id);
		// 记录操作
		baseDao.logger.delete(caller, "pc_id", pc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[] { pc_id});
	}
	
	@Override
	public void submitPhaseChange(String  caller,int pc_id) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("PRJPHASECHANGE", "pc_statuscode,pc_prjcode", "pc_id=" + pc_id);
		StateAssert.submitOnlyEntering(status[0]);
		//检测新增的项目是否与原有项目名字一样
		List<Object[]> phases = baseDao.getFieldsDatasByCondition("prjphasechangedet",new String[]{"pcd_phaseid","pcd_phase"},"pcd_pcid="+pc_id+" and nvl(pcd_phaseid,0)=0");
		List<Object> newNames=new LinkedList<Object>();
		for(Object[] phase:phases){
				List<Object> names = baseDao.getFieldDatasByCondition("projectphase", "pp_phase", "pp_prjid=(select prj_id from project where prj_code='"+status[1]+"') order by pp_detno");
				for(Object name:names){
					if(phase[1].equals(name))
						BaseUtil.showError("新增的项目阶段名称不允许与原项目阶段名称重复!");
			}
				newNames.add(phase[1]);			
		}
		//检查新增名字是否重复
		if(newNames.size()>1){
			for(int i=0;i<newNames.size();i++){
				for(int j=i+1;j<newNames.size();j++){
					if(newNames.get(i).equals(newNames.get(j)))
						BaseUtil.showError("新增的项目阶段名字不能相同!");
				}
			}
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,new Object[] { pc_id });
		// 执行提交操作
		baseDao.submit("PRJPHASECHANGE", "pc_id=" + pc_id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pc_id", pc_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller,new Object[] { pc_id });
	}

	@Override
	public void resSubmitPhaseChange(String  caller,int pc_id) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("PRJPHASECHANGE", "pc_statuscode", "pc_id=" + pc_id);
		StateAssert.resSubmitOnlyCommited(status);
		//执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, pc_id);
		// 执行反提交操作
		baseDao.resOperate("PRJPHASECHANGE", "pc_id=" + pc_id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pc_id", pc_id);
		//执行提交后的其它逻辑
		handlerService.afterResSubmit(caller, pc_id);
	}
	
	@Override
	@Transactional
	public void auditPhaseChange(int pc_id,String  caller) {
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller,new Object[] { pc_id });
		// 执行审核操作
		baseDao.audit("PRJPHASECHANGE", "pc_id=" + pc_id, "pc_status", "pc_statuscode", "pc_auditdate","pc_auditman");		
		
		// 记录操作
		baseDao.logger.audit(caller, "pc_id", pc_id);
		//新增更新对应项目阶段的负责人、计划开始时间、计划完成时间
		List<String> sqls = new ArrayList<String>();
		boolean flag=false;
		int detno=0;
		Object prjId=baseDao.getFieldDataByCondition("project", "prj_id", "prj_code=(select pc_prjcode from PRJPHASECHANGE where pc_id="+pc_id+")");		
		List<Object[]> datas = baseDao.getFieldsDatasByCondition("projectphase", new String[]{"pp_detno","pp_status"}, "pp_prjid="+prjId+" order by pp_detno");
		for(Object[] data:datas){
			detno = Integer.parseInt(data[0].toString());
			if(!flag&&!"已完成".equals(data[1]))
				flag=true;
		}
		int pp_detno=detno;
		List<Object[]> phases = baseDao.getFieldsDatasByCondition("PRJPHASECHANGEDET", new String[]{"PCD_PHASEID","PCD_NEWHRPSCODE","PCD_NEWCHRPS","PCD_NEWPHASESTART","PCD_NEWPHASEEND","nvl(pcd_phasestatus,' ')","pcd_id"}, "PCD_PCID ="+pc_id);
		for (Object[] obj : phases) {
				if("0".equals(obj[0].toString())){
					String sql="";
					pp_detno++;
					Object id = baseDao.getFieldDataByCondition("dual", "PROJECTPHASE_SEQ.nextval", "1=1");
					sql+="insert into projectphase (pp_id,pp_prjid,pp_phase,pp_chargeperson,pp_chargepersoncode,pp_startdate,pp_enddate,pp_status,pp_detno) select "+id+","+prjId+",pcd_phase,PCD_NEWCHRPS,PCD_NEWHRPSCODE,PCD_NEWPHASESTART,PCD_NEWPHASEEND,pcd_phasestatus,"+pp_detno+" from PRJPHASECHANGEDET where pcd_id='"+obj[6]+"'";
					String update="update PRJPHASECHANGEDET set pcd_phaseid="+id+" where pcd_id="+obj[6];
					sqls.add(update);
					sqls.add(sql);	
				}
				if (obj[1]!=null&&!"".equals(obj[1].toString().trim())) {
					String sql = "";
					sql+="update PROJECTPHASE set pp_chargepersoncode = '"+obj[1]+"',pp_chargeperson = '"+obj[2]+"' where pp_id="+obj[0];
					sqls.add(sql);	
				}
				if (obj[3]!=null&&!"".equals(obj[3].toString().trim())) {
					String sql = "";
					sql+="update PROJECTPHASE set pp_startdate = "+DateUtil.parseDateToOracleString(Constant.YMD_HMS, obj[3].toString())+" where pp_id="+obj[0];
					sqls.add(sql);	
				}
				if (obj[4]!=null&&!"".equals(obj[4].toString().trim())) {
					String sql = "";
					sql+="update PROJECTPHASE set pp_enddate ="+DateUtil.parseDateToOracleString(Constant.YMD_HMS, obj[4].toString())+" where pp_id="+obj[0];
					sqls.add(sql);	
				}								
			}
		baseDao.execute(sqls);
		//更新新增的阶段
	
		if(!flag){
			baseDao.execute("update projectphase set pp_status='进行中',PP_REALSTARTDATE=sysdate where pp_prjid=(select prj_id from project where prj_code=(select pc_prjcode from PRJPHASECHANGE where pc_id="+pc_id+")) and pp_detno="+(detno+1));
		}
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller,new Object[] { pc_id });
	}

	@Override
	public List<Map<String, Object>> loadPhase(String prj_code) {
		// TODO Auto-generated method stub
		List<Map<String,Object>> phases=new LinkedList<Map<String,Object>>();
		Map<String,Object> phase=null;
		List<Object[]> datas = baseDao.getFieldsDatasByCondition("projectphase", new String[]{"PP_DETNO","PP_PHASE","PP_CHARGEPERSONCODE","PP_CHARGEPERSON","PP_STARTDATE","PP_ENDDATE","PP_STATUS","PP_ID"}, "pp_prjid=(select prj_id from project where prj_code='"+prj_code+"') and nvl(pp_status,' ')<>'已完成' order by pp_detno");
		for(Object[] data:datas){
			phase=new HashMap<String,Object>();
			phase.put("pcd_detno", data[0]);
			phase.put("pcd_phase", data[1]);
			phase.put("pcd_chrpscode", data[2]);
			phase.put("pcd_chrps", data[3]);
			phase.put("pcd_newhrpscode", data[2]);
			phase.put("pcd_newchrps", data[3]);
			phase.put("pcd_phasestart", data[4]);
			phase.put("pcd_phaseend", data[5]);
			phase.put("pcd_newphasestart", data[4]);
			phase.put("pcd_newphaseend", data[5]);
			phase.put("pcd_phasestatus", data[6]);
			phase.put("pcd_phaseid", data[7]);
			phases.add(phase);	
		}	
		return phases;
	}	
	
}
