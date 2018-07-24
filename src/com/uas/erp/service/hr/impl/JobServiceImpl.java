package com.uas.erp.service.hr.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.PasswordEncryUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Job;
import com.uas.erp.model.Master;
import com.uas.erp.service.hr.JobService;
import com.uas.sso.entity.UserView;
import com.uas.sso.util.AccountUtils;

@Service
public class JobServiceImpl implements JobService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	@CacheEvict(value = "hrjob", allEntries = true)
	public void saveJob(String formStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Job", "jo_code='" + store.get("jo_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller,  new Object[] { store});	
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Job", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.execute("update job  set jo_orgid=nvl((select or_id from hrorg  where or_code=jo_orgcode),0) where jo_id="+ store.get("jo_id"));
		baseDao.execute("update job A set jo_subof=(select jo_id from job B where A.jo_pcode=B.jo_code) where jo_id="+ store.get("jo_id"));
		baseDao.execute("update Job A set jo_level=(nvl((select jo_level from Job B where A.jo_subof=B.jo_id),0) + 1) where jo_id="+ store.get("jo_id"));
		try {
			// 记录操作
			baseDao.logger.save(caller, "jo_id", store.get("jo_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 执行保存后的其它逻辑
		handlerService.afterSave(caller,  new Object[] { store});
	}

	@Override
	@CacheEvict(value = "hrjob", allEntries = true)
	public void deleteJob(int jo_id, String  caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { jo_id});
		int count = baseDao.getCount("select count(*) from Job where jo_subof=" + jo_id);
		if(count > 0){
			BaseUtil.showError("该岗位有下级岗位，不允许删除！");
		}
		if(!baseDao.checkByCondition("Employee", "nvl(em_class,' ')<>'离职' and nvl(em_defaulthsid,0)="+jo_id)||
			!baseDao.checkByCondition("EMPSJOBS left join employee on emp_id=em_id", "nvl(em_class,' ')<>'离职' and job_id="+jo_id)){
			BaseUtil.showError("该岗位存在相应的人员信息，不允许删除!");
		}
		// 删除
		baseDao.deleteById("Job", "jo_id", jo_id);
		// 记录操作
		baseDao.logger.delete(caller, "jo_id", jo_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { jo_id});
	}

	@Override
	@CacheEvict(value = "hrjob", allEntries = true)
	public void updateJobById(String formStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,  new Object[] { store});
		// 修改	
		Object joid=store.get("jo_id");
		Object jocode=baseDao.getFieldValue("job", "jo_code", "jo_id='"+joid+"'", String.class);
		if(jocode!=null && !jocode.equals(store.get("jo_code"))){
			boolean bool=baseDao.checkIf("job", "nvl(jo_pcode,' ')=(select jo_code from job where jo_id="+joid+")");
			if(bool) BaseUtil.showError("当前岗位存在子岗位，不允许修改编号!");
			boolean embool=baseDao.checkIf("employee", "nvl(em_defaulthscode,' ')=(select jo_code from job where jo_id="+joid+")");
			if(embool) BaseUtil.showError("当前岗位存在相应人员，不允许修改编号!");			
		}
		baseDao.updateByCondition("job","jo_parentname='"+store.get("jo_name")+"'", "nvl(jo_subof,0)='"+joid+"'");
		baseDao.updateByCondition("employee","em_defaulthsname='"+store.get("jo_name")+"',em_position='"+store.get("jo_name")+"'", "nvl(em_defaulthsid,0)='"+joid+"'");
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Job", "jo_id");
		baseDao.execute(formSql);
		baseDao.execute("update job  set jo_orgid=nvl((select or_id from hrorg  where or_code=jo_orgcode),0) where jo_id="+ store.get("jo_id"));
		baseDao.execute("update job A set jo_subof=(select jo_id from job B where A.jo_pcode=B.jo_code) where jo_id="+ store.get("jo_id"));
		baseDao.execute("update Job A set jo_level=(nvl((select jo_level from Job B where A.jo_subof=B.jo_id),0) + 1) where jo_id="+ store.get("jo_id"));
		// 记录操作
		baseDao.logger.update(caller, "jo_id", store.get("jo_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller,  new Object[] { store});
	}

	@Override
	public List<Job> getJobs(String orgid,String isStandard) {
		// TODO Auto-generated method stub
		try{
			String tablename="Job";
			if(isStandard!=null){
				Employee em=SystemSession.getUser();
				String installtype="Make";
				try{
					installtype=em.getCurrentMaster().getMa_installtype();
				}catch(Exception e){		
				}
				tablename="UAS_STANDARD_"+installtype+".JOB";
			}
			String querySql="select * from  "+tablename+" "+(orgid!=null?" where jo_orgid="+orgid:"")+" order by jo_id asc"; 
			List<Job> jobs = baseDao.getJdbcTemplate().query(querySql,new BeanPropertyRowMapper<Job>(Job.class));
			return jobs;
		}catch (EmptyResultDataAccessException e){
			e.printStackTrace();
			return null;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}

	}
	@Override
	public void saveJobs(String created) {
		// TODO Auto-generated method stub
		List<Map<Object,Object>> creates=BaseUtil.parseGridStoreToMaps(created);
		List<String> sqls=new ArrayList<String>();
		for(Map<Object,Object> c:creates){
			c.put("jo_id",baseDao.getSeqId("JOB_SEQ"));
			sqls.add(SqlUtil.getInsertSqlByMap(c,"JOB"));
		}
		baseDao.execute(sqls);
	}
	@Override
	public void saveEmployees(String created,String enUU) {
		List<Map<Object,Object>> creates=BaseUtil.parseGridStoreToMaps(created);
		String emUU;
		for(Map<Object,Object> c:creates){
			Object id=baseDao.getSeqId("EMPLOYEE_SEQ");
			int count = baseDao.getCountByCondition("EMPLOYEE", "em_mobile='"+c.get("em_mobile")+"'");
			if(count>0){
				BaseUtil.showError("该手机号已经存在，请更换手机号！");
			}
			Object orname = baseDao.getFieldDataByCondition("hrorg", "or_name", "or_id='"+c.get("em_defaultorid")+"'");
			c.put("em_id",id);
			c.put("em_type", "normal");
			c.put("em_class", "正式");
			//密码加密
			c.put("em_password", PasswordEncryUtil.encryptPassword("111111", String.valueOf(c.get("em_mobile"))));
			c.put("em_statuscode", "AUDITED");
			c.put("em_status", "已审核");
			c.put("em_defaultorname", orname);
			try {
				//同步信息
				UserView user = new UserView();
				user.setVipName(c.get("em_name").toString());
				user.setMobile(c.get("em_mobile").toString());
				user.setEmail(c.get("em_email").toString());
				UserView users =  AccountUtils.addUser(Long.parseLong(enUU), user);
				
				if(users!=null){
					c.put("em_uu",users.getUserUU().toString());
					c.put("em_imid", users.getImId());
				}
				baseDao.execute(SqlUtil.getInsertSqlByMap(c,"EMPLOYEE"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public void updateJobs(String jsonData) {
		// TODO Auto-generated method stub
		List<Map<Object,Object>> updates=BaseUtil.parseGridStoreToMaps(jsonData);
		baseDao.execute(SqlUtil.getUpdateSqlbyGridStore(updates, "JOB", "jo_id"));
	}

	@Override
	public List<Job> getJobsWithStandard() {
		// TODO Auto-generated method stub
		try{
			String querySql="select job.*,Cp_Fromid fromid,cp_fromcode fromcode,cp_fromname fromname from  Job left join copypower on jo_id=cp_toid where  nvl(cp_haschange,1)=1 order by jo_id asc"; 
			List<Job> jobs = baseDao.getJdbcTemplate().query(querySql,new BeanPropertyRowMapper<Job>(Job.class));
			return jobs;
		}catch (EmptyResultDataAccessException e){
			e.printStackTrace();
			return null;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Map<String, String> getEmployees(int id) {
		Map<String, String> map = new HashMap<String, String>();
		SqlRowList rs=baseDao.queryForRowSet("select em_id,em_name from employee where em_class<>'离职' and em_defaulthsid="+id);
		StringBuilder ids = new StringBuilder();
		StringBuilder names = new StringBuilder();
		while(rs.next()){
			ids.append(rs.getInt("em_id")).append(",");
			names.append(rs.getString("em_name")).append(",");
		}
		if(names.toString().length()>0){
			map.put("value",names.toString().substring(0,names.length()-1));
			map.put("value1",ids.toString().substring(0,ids.length()-1));
		}else{
			map.put("value","");
			map.put("value1","");
		}
		return map;
	}

	@Override
	@CacheEvict(value = {"OrgJobEmployees","hrjob"}, allEntries = true)
	public void bannedJob(int id, String caller) {
		handlerService.handler(caller,"banned","before",new Object[]{id});
		int count = baseDao.getCount("select count(*) from Job where nvl(jo_statuscode,' ')<>'DISABLE' and jo_subof=" +  id);
		StringBuffer sb=new StringBuffer();
		if(count > 0){
			sb.append("该岗位有未禁用的下级岗位，不允许禁用！").append("<hr>");
		}
		if(!baseDao.checkByCondition("empsjobs left join employee on emp_id=em_id", "nvl(em_class,' ')<>'离职' and job_id="+id)||!baseDao.checkByCondition("Employee", "nvl(em_class,' ')<>'离职' and nvl(em_defaulthsid,0)="+ id)){
			sb.append("该岗位存在相应的人员信息，不允许禁用！").append("<hr>");
		}
		if(sb.length()>0){
			BaseUtil.showError(sb.toString());
		}
		baseDao.banned("job", "jo_id="+id, "jo_status","jo_statuscode");
		baseDao.logger.banned(caller, "jo_id",id);
		handlerService.handler(caller,"banned","after",new Object[]{id});
	}

	@Override
	@CacheEvict(value = {"OrgJobEmployees","hrjob"}, allEntries = true)
	public void resBannedJob(int id, String caller) {
		// 只能反禁用已禁用的单据!
		Object status = baseDao.getFieldDataByCondition("Job", "jo_statuscode", "jo_id=" +id);
		if (!"DISABLE".equals(status)) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resBanned_onlyBanned"));
		}
		handlerService.handler(caller, "resBanned", "before", new Object[] { id });
		// 反禁用(修改物料状态为在录入)
		baseDao.resOperate("Job", "jo_id=" + id, "jo_status", "jo_statuscode");
		// 记录操作
		baseDao.logger.resBanned(caller, "jo_id", id);
		// 执行反禁用后的其它逻辑
		handlerService.handler(caller, "resBanned", "after", new Object[] {id });		
	}
	@Override
	public void updateEmployees(String jsonData) {
		List<Map<Object,Object>> updates=BaseUtil.parseGridStoreToMaps(jsonData);
		for(Map<Object,Object> update:updates ){
			String em_mobile = (String) update.get("em_mobile");
			String em_mobileOld =  (String) baseDao.getFieldDataByCondition("Employee", "em_mobile", "em_id = "+update.get("em_id").toString());
			if(!em_mobile.equals(em_mobileOld)){
				BaseUtil.showError("手机号不允许修改！！！");
			}
		}
		baseDao.execute(SqlUtil.getUpdateSqlbyGridStore(updates, "EMPLOYEE", "em_id"));
	}

	@Override
	public List<Job> getJobsByCondition(String condition) {
		try{
			String tablename="Job";
			String querySql="select * from  "+tablename+" "+(" where "+condition); 
			List<Job> jobs = baseDao.getJdbcTemplate().query(querySql,new BeanPropertyRowMapper<Job>(Job.class));
			return jobs;
		}catch (EmptyResultDataAccessException e){
			e.printStackTrace();
			return null;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	//SAAS初始化获取所有岗位
	public List<Job> getSaasJobs(String isStandard) {
		// TODO Auto-generated method stub
		try{
			String tablename="Job";
			if(isStandard!=null){
				Employee em=SystemSession.getUser();
				String installtype="Make";
				try{
					installtype=em.getCurrentMaster().getMa_installtype();
				}catch(Exception e){		
				}
				tablename="UAS_STANDARD_"+installtype+".JOB";
			}
			String querySql="select * from  "+tablename+" order by jo_id asc"; 
			List<Job> jobs = baseDao.getJdbcTemplate().query(querySql,new BeanPropertyRowMapper<Job>(Job.class));
			return jobs;
		}catch (EmptyResultDataAccessException e){
			e.printStackTrace();
			return null;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public void saveSaasJobs(String gridStore) {
		// TODO Auto-generated method stub
		List<Map<Object, Object>> maps = BaseUtil
				.parseGridStoreToMaps(gridStore);
		List<String> sqls = new ArrayList<String>();
		// 可能存在更新或者删除的操作
		for (int i = 0; i < maps.size(); i++) {
			Map<Object, Object> map = maps.get(i);
			// 如果Id 是存在的 则保存否则更新
			Object id = map.get("jo_id");
			if (id != null && !id.equals("null")) {
				sqls.add(SqlUtil.getUpdateSqlByFormStore(map, "job",
						"jo_id"));
			} else {
				if(baseDao.checkIf("Job", "jo_name='"+map.get("jo_name")+"'")){
					BaseUtil.showError("该岗位名称已存在,请重新输入!");
				}else{
					id = baseDao.getSeqId("JOB_SEQ");
					map.remove("jo_id");
					map.put("jo_id", id);
					map.put("jo_code",baseDao.sGetMaxNumber("Job", 1));
					sqls.add(SqlUtil.getInsertSqlByMap(map, "job"));
				}
			}
		}
		baseDao.execute(sqls); 
	}

	@Override
	public String[] getInfo(String id) {
		// TODO Auto-generated method stub
		String[] info=new String[3];
		SqlRowList sqlRowList=baseDao.queryForRowSet("select jo_description,JO_POWERDES,jo_name from Job where jo_id="+id+"");
		while (sqlRowList.next()){
			info[0]=sqlRowList.getString("jo_description");
			info[1]=sqlRowList.getString("jo_powerdes");
			info[2]=sqlRowList.getString("jo_name");
		}
		return info;
	}

	@Override
	public String getSaasEmployees(int parseInt) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> emp=baseDao.queryForList("select em_name,em_position,em_defaultorname,em_mobile,em_code,em_email from employee where em_class<>'离职' and em_defaulthsid=?", parseInt);
		return BaseUtil.parseGridStore2Str(emp);
	}

	@Override
	public void deleteSaasJob(int id) {
		// TODO Auto-generated method stub
		int count = baseDao.getCount("select count(*) from Job where jo_subof=" + id);
		if(count > 0){
			BaseUtil.showError("该岗位有下级岗位，不允许删除！");
			/*return 2;*/
		}
		else if(!baseDao.checkByCondition("Employee", "nvl(em_class,' ')<>'离职' and nvl(em_defaulthsid,0)="+id)||
			!baseDao.checkByCondition("EMPSJOBS left join employee on emp_id=em_id", "nvl(em_class,' ')<>'离职' and job_id="+id)){
			/*return 1;*/
			BaseUtil.showError("该岗位存在相应的人员信息，不允许删除!");
		}
			// 删除
			baseDao.deleteById("Job", "jo_id", id);
			/*return 0;*/
	}

	@Override
	public void updateDescription(String jo_id, String jo_power, String jo_description) {
		// TODO Auto-generated method stub
		String sql="update job set jo_description='"+jo_description+"',jo_powerdes='"+jo_power+"' where jo_id="+jo_id;
		baseDao.execute(sql);
	}

	@Override
	public void submitJob(int jo_id, String caller) {
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,new Object[] { jo_id });
		// 执行提交操作
		baseDao.submit("job", "jo_id=" + jo_id, "jo_status", "jo_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "jo_id", jo_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller,new Object[] { jo_id });	
	}

	@Override
	public void resSubmitJob(int jo_id, String caller) {
		//执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, jo_id);
		// 执行反提交操作
		baseDao.resOperate("job", "jo_id=" + jo_id, "jo_status", "jo_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "jo_id", jo_id);
		//执行提交后的其它逻辑
		handlerService.afterResSubmit(caller, jo_id);	
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@CacheEvict(value = { "positionpower","limitfields","specialpower"}, allEntries = true)
	public void auditJob(int jo_id, String caller) {
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller,new Object[] { jo_id });
		// 执行审核操作
		baseDao.execute("update job set jo_statuscode='AUDITED',jo_status='"+BaseUtil.getLocalMessage("AUDITED")+"' where jo_id=" + jo_id);
		
		//岗位资料审核同步角色权限
		boolean bol = baseDao.checkIf("configs", "code='defaultPower' and caller='sys'");
		if(bol){
			String out = baseDao.callProcedure("SP_SYNCROLEPOWERTOJOB", new Object[]{jo_id});
		}
		
		// 记录操作
		baseDao.logger.audit(caller, "jo_id", jo_id);
		
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller,new Object[] { jo_id });
	}

	@Override
	public void resAuditJob(int jo_id, String caller) {
		handlerService.beforeResAudit(caller, new Object[]{jo_id});
		// 执行反审核操作
		baseDao.execute("update job set jo_statuscode='ENTERING',jo_status='"+BaseUtil.getLocalMessage("ENTERING")+"' where jo_id=" + jo_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "jo_id", jo_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller,new Object[] { jo_id });
	}
}
