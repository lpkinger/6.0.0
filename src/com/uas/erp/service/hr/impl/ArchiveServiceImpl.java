package com.uas.erp.service.hr.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.hr.ArchiveService;

@Service
public class ArchiveServiceImpl implements ArchiveService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveArchive(String formStore, String[] gridStore,
			String  caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		/**
		 * 问题反馈单号：2016120377
		 * UAS标准版：人员资料->人员档案 保存更新时若手机号（em_mobile）不为空或不为0检测是否已被使用
		 * @author wsy
		 */
		Object em_mobile = store.get("em_mobile");
		if(em_mobile != null && !"".equals(em_mobile) && !"0".equals(em_mobile)){
			int count = baseDao.getCountByCondition("Employee", "em_mobile='"+em_mobile+"'");
			if(count>0){
				BaseUtil.showError("该手机号已经存在，不允许保存！");
			}
		}
		handlerService.beforeSave(caller,new Object[]{store});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Employee", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore[1]);
		Object[] id = new Object[grid.size()];
		for(int i=0;i<grid.size();i++){
			id[i] = baseDao.getSeqId("Archivedetailedu_SEQ");
			grid.get(i).put("ad_id", id[i]);
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "Archivedetailedu");
		baseDao.execute(gridSql);
		//保存VoucherDetail
		grid = BaseUtil.parseGridStoreToMaps(gridStore[0]);
		id = new Object[grid.size()];
		for(int i=0;i<grid.size();i++){
			id[i] = baseDao.getSeqId("Archivedetailfamily_SEQ");
			grid.get(i).put("af_id", id[i]);
		}
		gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "Archivedetailfamily");
		baseDao.execute(gridSql);
		
		grid = BaseUtil.parseGridStoreToMaps(gridStore[2]);
		Object[] idx = new Object[grid.size()];
		for(int i=0;i<grid.size();i++){
			idx[i] = baseDao.getSeqId("Archiveposition_SEQ");
			grid.get(i).put("ap_id", idx[i]);
		}
		gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "Archiveposition");
		baseDao.execute(gridSql);
		
		grid = BaseUtil.parseGridStoreToMaps(gridStore[3]);
		idx = new Object[grid.size()];
		for(int i=0;i<grid.size();i++){
			idx[i] = baseDao.getSeqId("Archivedetailwork_SEQ");
			grid.get(i).put("aw_id", idx[i]);
		}
		gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "Archivedetailwork");
		baseDao.execute(gridSql);
		
		grid = BaseUtil.parseGridStoreToMaps(gridStore[4]);
		idx = new Object[grid.size()];
		for(int i=0;i<grid.size();i++){
			idx[i] = baseDao.getSeqId("archivereandpunish_SEQ");
			grid.get(i).put("ar_id", idx[i]);
		}
		gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "archivereandpunish");
		baseDao.execute(gridSql);
		
		grid = BaseUtil.parseGridStoreToMaps(gridStore[5]);
		idx = new Object[grid.size()];
		for(int i=0;i<grid.size();i++){
			idx[i] = baseDao.getSeqId("relation_SEQ");
			grid.get(i).put("re_id", idx[i]);
		}
		gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "relation");
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "em_id", store.get("em_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//插入一条合同记录
		insertContract(store, caller);
		//同步将or_id和em_id插入到表hrorgemployees中
		baseDao.deleteByCondition("HrorgEmployees","om_emid="+store.get("em_id"));
		if(store.get("em_defaultorid")!=null && !store.get("em_defaultorid").equals("")){
			insertHrorgEmp(Integer.parseInt(store.get("em_id").toString()),Integer.parseInt(store.get("em_defaultorid").toString()));
		}
		List<Object> jo_orgids=baseDao.getFieldDatasByCondition("job", "jo_orgid", "jo_id in(select job_id from empsjobs where emp_id="+store.get("em_id")+")");
		for (Object jo_orgid : jo_orgids) {
			if(jo_orgid!=null && !"".equals(jo_orgid.toString())){
			insertHrorgEmp(Integer.parseInt(store.get("em_id").toString()),Integer.parseInt(jo_orgid.toString()));}
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller,new Object[]{store});
	}

	@Override
	public void updateArchiveById(String formStore, String[] gridStore,
			String  caller) {	
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		/**
		 * 问题反馈单号：2016120377
		 * UAS标准版：人员资料->人员档案 保存更新时若手机号（em_mobile）不为空或不为0检测是否已被使用
		 * @author wsy
		 */
		Object em_mobile = store.get("em_mobile");
		if(em_mobile != null && !"".equals(em_mobile) && !"0".equals(em_mobile)){
			int count=baseDao.getCount("select count(1) from employee where em_mobile='"+em_mobile+"' and em_id<>"+store.get("em_id"));
			if(count>0){
				BaseUtil.showError("该手机号已被使用！");
			}
		}
		handlerService.beforeUpdate(caller,new Object[]{store});
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Employee", "em_id");
		baseDao.execute(formSql);
		
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore[1]);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(grid, "Archivedetailedu", "ad_id");
		for(Map<Object, Object> s:grid){
			if(s.get("ad_id") == null || s.get("ad_id").equals("") || s.get("ad_id").toString().equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("Archivedetailedu_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "Archivedetailedu", new String[]{"ad_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		
		grid = BaseUtil.parseGridStoreToMaps(gridStore[0]);
		gridSql = SqlUtil.getUpdateSqlbyGridStore(grid, "Archivedetailfamily", "af_id");
		for(Map<Object, Object> s:grid){
			if(s.get("af_id").toString().equals("0") || s.get("af_id") == null || s.get("af_id").equals("")){
				int id = baseDao.getSeqId("Archivedetailfamily_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "Archivedetailfamily", new String[]{"af_id"}, new Object[]{id});
				gridSql.add(sql);
			} 
		}
		baseDao.execute(gridSql);

		grid = BaseUtil.parseGridStoreToMaps(gridStore[2]);
		gridSql = SqlUtil.getUpdateSqlbyGridStore(grid, "Archiveposition", "ap_id");
		for(Map<Object, Object> s:grid){
			if(s.get("ap_id") == null || s.get("ap_id").equals("") || s.get("ap_id").toString().equals("0")){
				int id = baseDao.getSeqId("Archiveposition_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "Archiveposition", new String[]{"ap_id"}, new Object[]{id});
				gridSql.add(sql);
			} 
		}
		baseDao.execute(gridSql);
		
		grid = BaseUtil.parseGridStoreToMaps(gridStore[3]);
		gridSql = SqlUtil.getUpdateSqlbyGridStore(grid, "Archivedetailwork", "aw_id");
		for(Map<Object, Object> s:grid){
			if(s.get("aw_id") == null || s.get("aw_id").equals("") || s.get("aw_id").toString().equals("0")){
				int id = baseDao.getSeqId("Archivedetailwork_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "Archivedetailwork", new String[]{"aw_id"}, new Object[]{id});
				gridSql.add(sql);
			} 
		}
		baseDao.execute(gridSql);
		
		grid = BaseUtil.parseGridStoreToMaps(gridStore[4]);
		gridSql = SqlUtil.getUpdateSqlbyGridStore(grid, "archivereandpunish", "ar_id");
		for(Map<Object, Object> s:grid){
			if(s.get("ar_id") == null || s.get("ar_id").equals("") || s.get("ar_id").equals("0")){
				int id = baseDao.getSeqId("archivereandpunish_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "archivereandpunish", new String[]{"ar_id"}, new Object[]{id});
				gridSql.add(sql);
			} 
		}
		baseDao.execute(gridSql);
		
		grid = BaseUtil.parseGridStoreToMaps(gridStore[5]);
		gridSql = SqlUtil.getUpdateSqlbyGridStore(grid, "relation", "re_id");
		for(Map<Object, Object> s:grid){
			if(s.get("re_id") == null || s.get("re_id").equals("") || s.get("re_id").equals("0")){
				int id = baseDao.getSeqId("relation_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "relation", new String[]{"re_id"}, new Object[]{id});
				gridSql.add(sql);
			} 
		}
		baseDao.execute(gridSql);
		
		baseDao.logger.update(caller, "em_id", store.get("em_id"));
		//同步更新合同
		updateContract(store, caller);
		//同步将or_id和em_id插入到表hrorgemployees中
		baseDao.deleteByCondition("HrorgEmployees","om_emid="+store.get("em_id"));
		Object em_defaultorid=baseDao.getFieldDataByCondition("employee", "em_defaultorid", "em_id="+store.get("em_id"));
		if(em_defaultorid!=null && !em_defaultorid.equals("")){
		insertHrorgEmp(Integer.parseInt(store.get("em_id").toString()),Integer.parseInt(em_defaultorid.toString()));}
		List<Object> jo_orgids=baseDao.getFieldDatasByCondition("job", "jo_orgid", "jo_id in(select job_id from empsjobs where emp_id="+store.get("em_id")+")");
		for (Object jo_orgid : jo_orgids) {
			if(jo_orgid!=null && !jo_orgid.equals("")){
			insertHrorgEmp(Integer.parseInt(store.get("em_id").toString()),Integer.parseInt(jo_orgid.toString()));}
		}
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
	}

	@Override
	public void deleteArchive(int ar_id, String  caller) {
		
		handlerService.beforeDel(caller, new Object[]{ar_id});	
		baseDao.deleteById("Employee", "em_id", ar_id);
		
		baseDao.deleteById("Archivedetailfamily", "af_arid", ar_id);
		
		baseDao.deleteById("Archivedetailwork", "aw_arid", ar_id);
		
		baseDao.deleteById("Archivedetailedu", "ad_arid", ar_id);
		
		baseDao.deleteById("archiveposition", "ap_arid", ar_id);
		
		baseDao.deleteById("archivereandpunish", "ar_arid", ar_id);
		
		baseDao.deleteById("relation", "re_emid", ar_id);
		
		baseDao.logger.delete(caller, "em_id", ar_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{ar_id});	
	}
	
	private void insertHrorgEmp(int em_id, int or_id){ 
		 int count=baseDao.getCountByCondition("HrorgEmployees", "om_emid="+em_id+" and om_orid="+or_id);
		 if (count == 0){
				baseDao.execute("insert into HrorgEmployees(om_emid,om_orid) values ("+em_id+","+or_id+") ");
				Object or_subof=baseDao.getFieldDataByCondition("hrorg", "or_subof", "or_id="+or_id);
				if(or_subof!=null && Integer.parseInt(or_subof.toString())!=0){
					insertHrorgEmp(em_id,Integer.parseInt(or_subof.toString()));
				} 
		 }
	}
	
	private void insertContract(Map<Object, Object> store,String caller){
		StringBuffer sb=new StringBuffer();
		sb.append("insert into contract (co_id,co_code,co_title,co_depart,co_company,co_manager,co_connecter,co_phone,co_address,co_contractor,co_sex,");
		sb.append("co_card,co_conadd,co_conphone,co_conclass,co_begintime,co_endtime,co_probation,co_position,co_workaddress,co_salary,co_contratime,");
		sb.append("co_time,co_recordor,co_recordorid,co_contractorcode) values(");
		int id=baseDao.getSeqId("Contract_seq");
		sb.append(id+",");
		sb.append("'"+baseDao.sGetMaxNumber("Contract", 2)+"',");
		sb.append("'人事合同',");//co_title
		sb.append("'"+store.get("em_depart")+"',");
		sb.append("'易方',");//co_company
		sb.append("'',");//co_manager
		sb.append("'',");//co_connecter
		sb.append("'"+store.get("em_mobile")+"',");
		sb.append("'深圳易方',");//co_address是单位地址
		sb.append("'"+store.get("em_name")+"',");
		sb.append("'"+store.get("em_sex")+"',");
		sb.append("'"+store.get("em_iccode")+"',");
		sb.append("'"+store.get("em_address")+"',");
		sb.append("'"+store.get("em_tel")+"',");
		sb.append("'',");//co_conclass
		sb.append(DateUtil.parseDateToOracleString(null,store.get("em_indate")+"")+",");
		sb.append(DateUtil.parseDateToOracleString(null,store.get("em_cancellingdate")+"")+",");
		String co_probation="";//转正时间减入职时间得出试用期
		if((store.get("em_indate")!=null&&!"".equals(store.get("em_indate")+""))&&(store.get("em_zzdate")!=null&&!"".equals(store.get("em_zzdate")+""))){
			long indate=DateUtil.parseStringToDate(store.get("em_indate")+"", null).getTime();
			long zzdate=DateUtil.parseStringToDate(store.get("em_zzdate")+"", null).getTime();
			long d=(zzdate-indate)/(1000*60*60*24)+2;//+2为了补充二月的28天
			co_probation=Integer.parseInt(d+"")/30+"月";
		}
		sb.append("'"+co_probation+"',");
		sb.append("'"+store.get("em_position")+"',");
		sb.append("'深圳易方',");//co_workaddress
		sb.append("'"+store.get("em_salary")+"',");
		sb.append(DateUtil.parseDateToOracleString(null,store.get("em_indate")+"")+",");//签约时间co_contractime
		sb.append(DateUtil.parseDateToOracleString(null,new Date())+",");//co_date
		sb.append("'"+SystemSession.getUser().getEm_name()+"',");
		sb.append(SystemSession.getUser().getEm_id()+",");
		sb.append("'"+store.get("em_code")+"')");
		baseDao.execute(sb.toString());
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.insert"), 
				BaseUtil.getLocalMessage("msg.insertSuccess"), "contract|co_id=" + id));
	}
	private void updateContract(Map<Object, Object> store,String caller){
		StringBuffer sb=new StringBuffer();
		sb.append("update Contract set co_depart='"+store.get("em_depart")+"',");
		sb.append("co_phone='"+store.get("em_mobile")+"',");
		sb.append("co_contractor='"+store.get("em_name")+"',");
		sb.append("co_sex='"+store.get("em_sex")+"',");
		sb.append("co_card='"+store.get("em_iccode")+"',");
		sb.append("co_conadd='"+store.get("em_address")+"',");
		sb.append("co_begintime="+DateUtil.parseDateToOracleString(null,store.get("em_indate")+"")+",");
		sb.append("co_endtime="+DateUtil.parseDateToOracleString(null,store.get("em_cancellingdate")+"")+",");
		String co_probation="";//转正时间减入职时间得出试用期
		if((store.get("em_indate")!=null&&!"".equals(store.get("em_indate")+""))&&(store.get("em_zzdate")!=null&&!"".equals(store.get("em_zzdate")+""))){
			long indate=DateUtil.parseStringToDate(store.get("em_indate")+"", null).getTime();
			long zzdate=DateUtil.parseStringToDate(store.get("em_zzdate")+"", null).getTime();
			long d=(zzdate-indate)/(1000*60*60*24)+2;//+2为了补充二月的28天
			co_probation=Integer.parseInt(d+"")/30+"月";
		}
		sb.append("co_probation='"+co_probation+"',");
		sb.append("co_position='"+store.get("em_position")+"',");
		sb.append("co_salary='"+store.get("em_salary")+"',");
		sb.append("co_contratime="+DateUtil.parseDateToOracleString(null,store.get("em_indate")+"")+",");//签约时间co_contractime
		sb.append("co_time="+DateUtil.parseDateToOracleString(null,new Date())+",");
		sb.append("co_recordor='"+SystemSession.getUser().getEm_name()+"',");
		sb.append("co_recordorid="+SystemSession.getUser().getEm_id());
		sb.append(" where co_contractorcode='"+store.get("em_code")+"'");
		baseDao.execute(sb.toString());
		baseDao.logger.update(caller, "em_code", store.get("em_code"));
	}
}
