package com.uas.erp.service.plm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlMap;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.service.plm.ProjectGanttTaskService;
import com.uas.erp.service.plm.ProjectRequestService;


@Service
public class ProjectRequestServiceImpl implements ProjectRequestService{
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	@Autowired
	private ProjectGanttTaskService projectGanttTaskService;
	
	@Override
	@Transactional
	public void planMainTask(int id){
		
		Object[] data = baseDao.getFieldsDataByCondition("Project",new String[]{"prj_auditstatuscode","prj_id"}, "prj_id=" + id);
		if (data!=null && !data[0].equals("AUDITED")) {
			BaseUtil.showError("状态非已审核");
		}
		boolean bool=baseDao.checkIf("project", "prj_turnstatus='-1' and prj_id="+id);
		if(bool){
			BaseUtil.showError("项目已转产品任务书，不允许重复生成！");
		}
		int ptid= baseDao.getSeqId("PROJECTMAINTASK_SEQ");
		String code =  baseDao.sGetMaxNumber("PROJECTMAINTASK", 2);
		String insertSql = "insert into projectmaintask (pt_id,pt_code,pt_prjid,pt_prjcode,pt_prjname,pt_orger,pt_orgerdept,pt_statuscode,pt_status,pt_recorder,pt_recorddate,pt_startdate,pt_enddate,pt_producttype) select "+ptid+",'"+code+"',prj_id,prj_code,prj_name,prj_assignto,prj_organigerdep,'ENTERING','在录入','"+SystemSession.getUser().getEm_name()+"',sysdate,prj_start,prj_end,prj_producttype from project where prj_id="+id;
		baseDao.execute(insertSql);
		
		List<Map<String,Object>> list = baseDao.queryForList("select * from projecttask_temp left join project on prj_producttypecode=prjtypecode_ where prj_id="+id);
		boolean checkTask = true;
		if(list.size()<=0){
			checkTask=false;
		}
		//自动载入任务
		boolean autoLoadTask = baseDao.isDBSetting("ProjectRequest", "autoLoadTask");
		if(autoLoadTask && checkTask){
			List<String> sqls = new ArrayList<String>();
			sqls.add("update projecttask a set (resourcename,resourcecode,resourceemid,startdate,enddate,duration,RESOURCEUNITS,PHASEID,PHASENAME)=(select PP_CHARGEPERSON,PP_CHARGEPERSONCODE,em_id,trunc(pp_startdate),trunc(pp_enddate)+1-(1/(24*60*60)),trunc(pp_enddate)+1-trunc(pp_startdate),100,pp_id,pp_phase from projectphase left join employee on em_code=PP_CHARGEPERSONCODE where pp_prjid="+id+" and trim(pp_phase)=trim(name)) where exists (select 1 from projectphase where pp_prjid="+id+" and trim(pp_phase)=trim(name)) and NVL(taskclass,' ')<>'pretask'  and prjplanid="+id);
			sqls.add("update resourceassignment set (ra_resourcecode,ra_resourcename,ra_emid,ra_startdate,ra_enddate)=(select resourcecode,resourcename,resourceemid,startdate,enddate from projecttask where id=ra_taskid) where exists (select 1 from projectphase where pp_prjid="+id+" and trim(pp_phase)=trim(ra_taskname))  and ra_prjid="+id);
			sqls.add("insert into  resourceassignment(ra_id,ra_taskid,ra_emid,ra_resourcecode,ra_resourcename,ra_detno,ra_status,ra_statuscode,ra_units,ra_type,ra_taskname,ra_startdate,ra_enddate,ra_prjid,ra_prjname) select resourceassignment_seq.nextval,id,resourceemid,resourcecode,resourcename,1,handstatus,handstatuscode,100,'projecttask',name,startdate,enddate,prjplanid,PRJPLANNAME from projecttask a  where exists (select 1 from projectphase where pp_prjid="+id+" and trim(pp_phase)=trim(name)) and NVL(taskclass,' ')<>'pretask' and not exists (select 1 from resourceassignment where ra_taskid=id) and prjplanid="+id+" and resourcecode is not null");
			projectGanttTaskService.LoadTaskNode(ptid, null, "ProjectRequest", DateUtil.currentDateString(Constant.YMD));
			baseDao.execute(sqls);
		}
			
		//单据配置逻辑:下推产品开发任务书自动审核启动
		boolean flag = baseDao.isDBSetting("ProjectRequest", "autoAudited");
		if(flag && checkTask){
			int count = baseDao.getCount("select count(*) from projecttask where prjplanid="+id+" and nvl(taskclass,' ')<>'pretask'");
			if(count>0){
				projectGanttTaskService.TurnTask(ptid, "Project");
			}
		}
		
		//更新单据的已下达任务书状态为-1
		baseDao.execute("update project set prj_turnstatus='-1' where prj_id=" + id);
		
		baseDao.logger.turn("转产品任务书", "ProjectRequest", "prj_id", id);
		
		BaseUtil.appendError("生成成功,任务单号:" + "<a href=\"javascript:openUrl('jsps/plm/task/projectgantttask.jsp?formCondition=pt_idIS"+ptid+"&gridCondition=ptidIS"+ptid+"&prjplanid="+id+"')\">" + code + "</a>&nbsp;");
	
	}
	
	@Override
	@Transactional
	public void saveProjectRequest(String caller,String formStore,String params1,String params2,String params3){
		Employee employee = SystemSession.getUser();
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//检查产品类型是否存在
		if(store.get("prj_producttypecode")!=null&&!"".equals(store.get("prj_producttypecode"))){
			boolean productbol = baseDao.checkIf("plmproducttype", "pt_code='"+store.get("prj_producttypecode")+"'");
			if(!productbol){
				BaseUtil.showError("产品类型不存在，请重新选择");
			}
		}
		
		Object prjname = store.get("prj_name");
		if(prjname!=null&&!"".equals(prjname)){
			boolean UnLimitName = baseDao.isDBSetting("PreProject", "UnLimitName");
			if(!UnLimitName) {
				boolean bool = baseDao.checkIf("project", "prj_name='" + prjname + "' and prj_id<>" + store.get("prj_id"));
				if(bool){
					BaseUtil.showError("该项目名称已存在，请重新修改");
				}
			}
		}else{
			BaseUtil.showError("项目名称不允许为空");
		}	
		
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(params1);

		//判断阶段计划是否重复
		for(int i=0;i<grid.size();i++){
			Map<Object, Object> map = grid.get(i);
			Object phase = map.get("pp_phase");
			if(phase!=null&&!"".equals(phase)) {
			for(int j=i+1;j<grid.size();j++){
				Map<Object, Object> nextMap = grid.get(j);
				Object nextPhase = nextMap.get("pp_phase");
				if(phase.toString().equals(String.valueOf(nextPhase)) && nextPhase!=null){
					BaseUtil.showError("项目阶段重复，序号:" + map.get("pp_detno") + "," + nextMap.get("pp_detno"));
				}
			}
			}else {
				grid.remove(i);
				i--;
			}
		}
		
		
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PROJECT", new String[] {}, new Object[] {});
		baseDao.execute(formSql);

		if(!String.valueOf(store.get("prj_mainproid")).equals("")) {
			int maxdetno = baseDao.getFieldValue("PROJECT","nvl(max(prj_number5),0)+1", "prj_mainproid="+store.get("prj_mainproid"), Integer.class);
			baseDao.execute("update project set prj_number5="+maxdetno+" where prj_id="+store.get("prj_id"));
		}
		//设置立项分类
		if("PreProject".equals(caller)) {
			baseDao.execute("update project set prj_prstatus='预立项',prj_prstatuscode='pretask' where prj_id="+store.get("prj_id"));
		}else {
			baseDao.execute("update project set prj_prstatus='正式立项',prj_prstatuscode='normaltask' where prj_id="+store.get("prj_id"));
		}
		
		
		//保存projectphase
		for(Map<Object, Object> m:grid){
			m.put("pp_id", baseDao.getSeqId("PROJECTPHASE_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "PROJECTPHASE");
		baseDao.execute(gridSql);
		
		//更新子项目明细表
		List<Map<Object, Object>> projectsobstore = BaseUtil.parseGridStoreToMaps(params2);
		List<String> gridSqlProjectSob = SqlUtil.getUpdateSqlbyGridStore(params2, "Project", "prj_id");
		for(Map<Object, Object> s:projectsobstore){
			if(s.get("prj_id") == null || s.get("prj_id").equals("") || s.get("prj_id").equals("0") ||
					Integer.parseInt(s.get("prj_id").toString()) == 0){//新添加的数据，id不存在
				boolean UnLimitName = baseDao.isDBSetting("PreProject", "UnLimitName");
				if(!UnLimitName) {
					boolean bool = baseDao.checkIf("Project", "prj_name='"+s.get("prj_name")+"'");
					if(bool) {
						BaseUtil.showError("该子项目名称已存在，请不要重复添加！");
					}
				}
				s.put("prj_code", (s.get("prj_code")!=null && !s.get("prj_code").equals(""))?(s.get("prj_code")):(baseDao.sGetMaxNumber("Project",2)));
				s.put("prj_id", baseDao.getSeqId("Project_seq"));
				s.put("prj_person", employee.getEm_name());
				s.put("prj_recordate", DateUtil.currentDateString(Constant.YMD_HMS));
				s.put("prj_mainproid", store.get("prj_id"));
				s.put("prj_mainprocode", store.get("prj_code"));
				s.put("prj_mainproname", store.get("prj_name"));
				s.put("prj_producttypecode", store.get("prj_producttypecode"));
				s.put("prj_producttype", store.get("prj_producttype"));
				s.put("prj_prstatus", "正式立项");
				s.put("prj_prstatuscode", "normaltask");
				s.put("prj_class", "立项申请书");
				//自动将主项目的相关信息带入子项目中--欧盛
				boolean autoLoadInformation = baseDao.isDBSetting(caller, "autoLoadInformation");
				if(autoLoadInformation) {
					s.put("prj_type", store.get("prj_type"));
					s.put("prj_customercode",store.get("prj_customercode"));
					s.put("prj_customername", store.get("prj_customername"));
					s.put("prj_assigntocode", store.get("prj_assigntocode"));
					s.put("prj_assignto", store.get("prj_assignto"));
					s.put("prj_organigerdep",store.get("prj_organigerdep"));
					s.put("prj_start", store.get("prj_start"));
					s.put("prj_end", store.get("prj_end"));
					s.put("prj_sourcetype", store.get("prj_sourcetype"));
					s.put("prj_sourcecode", store.get("prj_sourcecode"));
					s.put("prj_text14", store.get("prj_text14"));
				}
				String sql = SqlUtil.getInsertSqlByMap(s, "Project");
				gridSqlProjectSob.add(sql);
			}else {
				if(s.get("prj_code")==null || s.get("prj_code").equals("")){
					s.put("prj_code", baseDao.sGetMaxNumber("Project",2));
				}
				SqlMap  sql = SqlUtil.getSqlMap(s, "Project", "prj_id",true);
				gridSqlProjectSob.add(sql.getUpdateSql(false));
			}
		}
		baseDao.execute(gridSqlProjectSob);
		
		//生成项目团队
		boolean isTeam = baseDao.checkIf("team", "team_prjid="+store.get("prj_id"));
		boolean isPhase = baseDao.checkIf("projectphase", "pp_prjid="+store.get("prj_id"));
		if(!isTeam&&isPhase) {
			String teamcode = baseDao.sGetMaxNumber("Team",2);
			String teamsql = "insert into team(team_status,team_statuscode,team_code,team_name,team_pricode,team_prjname,team_recorder,team_recorddate,team_id,team_prjid)"
					+ " values('在录入','ENTERING','"+teamcode+"',?,?,?,?,sysdate,team_seq.nextval,?)";
			baseDao.execute(teamsql, new Object[] {store.get("prj_name"),store.get("prj_code"),store.get("prj_name"),store.get("prj_person"),store.get("prj_id")});
		}

		//更新项目团队表
		List<String> gridSqlTeams = SqlUtil.getUpdateSqlbyGridStore(params3, "Teammember", "tm_id");
		List<Map<Object, Object>> Teamsstore = BaseUtil.parseGridStoreToMaps(params3);
		Object newTeamId = baseDao.getFieldDataByCondition("Team", "team_id", "team_prjid="+store.get("prj_id"));
		for(Map<Object, Object> s:Teamsstore){
			if(s.get("tm_id") == null || s.get("tm_id").equals("") || s.get("tm_id").equals("0") ||
					Integer.parseInt(s.get("tm_id").toString()) == 0){//新添加的数据，id不存在
				s.put("tm_id", baseDao.getSeqId("Teammember_seq"));
				s.put("tm_teamid", newTeamId);
				String sql = SqlUtil.getInsertSqlByMap(s, "Teammember");
				gridSqlTeams.add(sql);
			}
		}
		baseDao.execute(gridSqlTeams);
		
		//载入任务
		int id = Integer.parseInt(store.get("prj_id").toString());
		if("PreProject".equals(caller)) {
			baseDao.procedure("SP_LOADPREPROJECTTASK", new Object[]{id});
		}
		
		// 记录操作
		baseDao.logger.save(caller, "prj_id", store.get("prj_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });

	}
	
	@Override
	@Transactional
	public void updateProjectRequest(String caller,String formStore,String params1,String params2,String params3,String params4){
		Employee employee = SystemSession.getUser();
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(params1);

		//检查产品类型是否存在
		if(store.get("prj_producttypecode")!=null&&!"".equals(store.get("prj_producttypecode"))){
			boolean productbol = baseDao.checkIf("plmproducttype", "pt_code='"+store.get("prj_producttypecode")+"'");
			if(!productbol){
				BaseUtil.showError("产品类型不存在，请重新选择");
			}
		}
		
		Object prjname = store.get("prj_name");
		if(prjname!=null&&!"".equals(prjname)){
			boolean UnLimitName = baseDao.isDBSetting("PreProject", "UnLimitName");
			if(!UnLimitName) {
				boolean bool = baseDao.checkIf("project", "prj_name='" + prjname + "' and prj_id<>" + store.get("prj_id"));
				if(bool){
					BaseUtil.showError("该项目名称已存在，请重新修改");
				}
			}
		}else{
			BaseUtil.showError("项目名称不允许为空");
		}	
		
		//判断是否更换产品类型
		Object typecode = baseDao.getFieldDataByCondition("project", "prj_producttypecode", "prj_id="+store.get("prj_id"));
		int id = Integer.parseInt(store.get("prj_id").toString());
		
		
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });

		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PROJECT", "prj_id");
		baseDao.execute(formSql);
		if(!String.valueOf(store.get("prj_mainproid")).equals("")) {
			String maxdetno = baseDao.getFieldValue("PROJECT","nvl(max(prj_number5),0)+1", "prj_mainproid="+store.get("prj_mainproid"), String.class);
			baseDao.execute("update project set prj_number5='"+maxdetno+"' where prj_id="+store.get("prj_id"));
		}
		
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(params1, "PROJECTPHASE", "pp_id");
		//判断阶段计划是否重复
		for(int i=0;i<gstore.size();i++){
			Map<Object, Object> map = gstore.get(i);
			Object phase = map.get("pp_phase");
			if(phase!=null&&!"".equals(phase)) {
			for(int j=i+1;j<gstore.size();j++){
				Map<Object, Object> nextMap = gstore.get(j);
				Object nextPhase = nextMap.get("pp_phase");
				if(phase.toString().equals(String.valueOf(nextPhase)) && nextPhase!=null){
					BaseUtil.showError("项目阶段重复，序号:" + map.get("pp_detno") + "," + nextMap.get("pp_detno"));
				}
			}
			}else {
				gstore.remove(i);
				i--;
			}
		}
		for(Map<Object, Object> s:gstore){
			if(s.get("pp_id") == null || s.get("pp_id").equals("") || s.get("pp_id").equals("0") ||
					Integer.parseInt(s.get("pp_id").toString()) == 0){//新添加的数据，id不存在
				s.put("pp_id", baseDao.getSeqId("PROJECTPHASE_SEQ"));
				String sql = SqlUtil.getInsertSqlByMap(s, "PROJECTPHASE");
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		
		//更新子项目明细表
		List<Map<Object, Object>> projectsobstore = BaseUtil.parseGridStoreToMaps(params2);
		List<String> gridSqlProjectSob = SqlUtil.getUpdateSqlbyGridStore(params2, "Project", "prj_id");
		for(Map<Object, Object> s:projectsobstore){
			if(s.get("prj_id") == null || s.get("prj_id").equals("") || s.get("prj_id").equals("0") ||
					Integer.parseInt(s.get("prj_id").toString()) == 0){//新添加的数据，id不存在
				boolean UnLimitName = baseDao.isDBSetting("PreProject", "UnLimitName");
				if(!UnLimitName) {
					boolean bool = baseDao.checkIf("Project", "prj_name='"+s.get("prj_name")+"'");
					if(bool) {
						BaseUtil.showError("该子项目名称已存在，请不要重复添加！");
					}
				}
				s.put("prj_code", (s.get("prj_code")!=null && !s.get("prj_code").equals(""))?(s.get("prj_code")):(baseDao.sGetMaxNumber("Project",2)));
				s.put("prj_id", baseDao.getSeqId("Project_seq"));
				s.put("prj_person", employee.getEm_name());
				s.put("prj_recordate", DateUtil.currentDateString(Constant.YMD_HMS));
				s.put("prj_mainproid", store.get("prj_id"));
				s.put("prj_mainprocode", store.get("prj_code"));
				s.put("prj_mainproname", store.get("prj_name"));
				s.put("prj_producttype", store.get("prj_producttype"));
				s.put("prj_producttypecode", store.get("prj_producttypecode"));
				s.put("prj_prstatus", "正式立项");
				s.put("prj_prstatuscode", "normaltask");
				s.put("prj_class", "立项申请书");
				//自动将主项目的相关信息带入子项目中--欧盛
				boolean autoLoadInformation = baseDao.isDBSetting(caller, "autoLoadInformation");
				if(autoLoadInformation) {
					s.put("prj_type", store.get("prj_type"));
					s.put("prj_customercode",store.get("prj_customercode"));
					s.put("prj_customername", store.get("prj_customername"));
					s.put("prj_assigntocode", store.get("prj_assigntocode"));
					s.put("prj_assignto", store.get("prj_assignto"));
					s.put("prj_organigerdep",store.get("prj_organigerdep"));
					s.put("prj_start", store.get("prj_start"));
					s.put("prj_end", store.get("prj_end"));
					s.put("prj_sourcetype", store.get("prj_sourcetype"));
					s.put("prj_sourcecode", store.get("prj_sourcecode"));
					s.put("prj_text14", store.get("prj_text14"));
				}
				String sql = SqlUtil.getInsertSqlByMap(s, "Project");
				gridSqlProjectSob.add(sql);
			}else {
				if(s.get("prj_code")==null || s.get("prj_code").equals("")){
					s.put("prj_code", baseDao.sGetMaxNumber("Project",2));
				}
				SqlMap  sql = SqlUtil.getSqlMap(s, "Project", "prj_id",true);
				gridSqlProjectSob.add(sql.getUpdateSql(false));
			}
		}
		String conditionTeam = "team_prjid="+store.get("prj_id")+" and team_pricode='"+store.get("prj_code")+"'" + " and team_prjname='"+ 
		        store.get("prj_name")+"'";
		boolean boolTeam = baseDao.checkIf("Team", conditionTeam);
		if(!boolTeam) {
			baseDao.execute("update Team set team_pricode='"+store.get("prj_code")+"'" + ",team_prjname='"+store.get("prj_name")+"'" + ",team_name='"+store.get("prj_name")+"' where team_prjid="+store.get("prj_id"));
		}
		baseDao.execute(gridSqlProjectSob);
		
		//生成项目团队
		boolean isTeam = baseDao.checkIf("team", "team_prjid="+store.get("prj_id"));
		boolean isPhase = baseDao.checkIf("projectphase", "pp_prjid="+store.get("prj_id"));
		if(!isTeam&&isPhase) {
			String teamcode = baseDao.sGetMaxNumber("Team",2);
			String teamsql = "insert into team(team_status,team_statuscode,team_code,team_name,team_pricode,team_prjname,team_recorder,team_recorddate,team_id,team_prjid)"
					+ " values('在录入','ENTERING','"+teamcode+"',?,?,?,?,sysdate,team_seq.nextval,?)";
			baseDao.execute(teamsql, new Object[] {store.get("prj_name"),store.get("prj_code"),store.get("prj_name"),store.get("prj_person"),store.get("prj_id")});
		}
		
		//更新项目团队表
		List<String> gridSqlTeams = SqlUtil.getUpdateSqlbyGridStore(params3, "Teammember", "tm_id");
		List<Map<Object, Object>> Teamsstore = BaseUtil.parseGridStoreToMaps(params3);
		Object newTeamId = baseDao.getFieldDataByCondition("Team", "team_id", "team_prjid="+store.get("prj_id"));
		for(Map<Object, Object> s:Teamsstore){
			if(s.get("tm_id") == null || s.get("tm_id").equals("") || s.get("tm_id").equals("0") ||
					Integer.parseInt(s.get("tm_id").toString()) == 0){//新添加的数据，id不存在
				s.put("tm_id", baseDao.getSeqId("Teammember_seq"));
				s.put("tm_teamid", newTeamId);
				String sql = SqlUtil.getInsertSqlByMap(s, "Teammember");
				gridSqlTeams.add(sql);
			}
		}
		baseDao.execute(gridSqlTeams);
		
		
		if(!store.get("prj_producttypecode").equals(typecode)&&"PreProject".equals(caller)){
			baseDao.execute("update project set prj_producttypecode='"+store.get("prj_producttypecode")+"',prj_producttype='"+store.get("prj_producttype")+"' where prj_mainproid="+id);
			baseDao.procedure("SP_LOADPREPROJECTTASK", new Object[]{id});
		}
		
		//参数配置：主项目的负责人复制给子项目
		boolean autoLoadInformation = baseDao.isDBSetting(caller, "autoLoadInformation");
		if(autoLoadInformation) {
			baseDao.execute("update project a set (prj_assignto,prj_assigntocode) = (select b.prj_assignto,b.prj_assigntocode " + 
					"from project b where b.prj_id=a.prj_mainproid) where a.prj_mainproid="+ store.get("prj_id"));
		}
		
		// 记录操作
		baseDao.logger.update(caller, "prj_id", store.get("prj_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}
	
	@Override
	public void deleteProjectRequest(String  caller,int prj_id) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[] { prj_id});
				
		List<String> sqls = new ArrayList<String>();
		
		Object[] source = baseDao.getFieldsDataByCondition("project", new String[]{"prj_sourcetype","prj_sourcecode"}, "prj_id=" + prj_id);
		if(source!=null&&!"".equals(source)){
			if(source[0]!=null&&source[1]!=null){
				if("需求单".equals(source[0])){
					baseDao.execute("update prjrequest set pr_auditstatus=null where pr_code='" + source[1] + "'");
				}else if("预立项".equals(source[0])){
					//更新需求单状态
					sqls.add("update prjrequest set pr_auditstatus='已转立项' where pr_code=(select pp_prcode from preproject where pp_code='"+source[1]+"')");					
					//更新预立项任务书中的项目编号为空
					sqls.add("update preproject set pp_prjcode=null where pp_prjcode=(select prj_code from project where prj_id="+prj_id+")");
				}
			}
		}
				
		//如果是反审核之后进行的删除，则删除相应的文件目录
		boolean fileExist = baseDao.checkIf("projectdoc", "pd_prjid=" + prj_id);
		if(fileExist){
			sqls.add("delete from projectdoc where pd_prjid=" + prj_id);
		}
			
		baseDao.execute(sqls);
		
		Object mainproid = baseDao.getFieldDataByCondition("Project", "prj_mainproid", "prj_id="+prj_id);
		
		baseDao.deleteById("PROJECT", "prj_id", prj_id);
		
		//删除考虑到子项目的项目序号要更新
		boolean boolSobProject = baseDao.checkIf("Project", "prj_mainproid is not null");
		if(boolSobProject) {
			List<Object[]> sobData= baseDao.getFieldsDatasByCondition("Project", new String[]{"prj_number5","prj_id"},
					"prj_mainproid="+mainproid+" order by prj_number5 asc");
			for(int i =0;i<sobData.size();i++) {
				Object[] data = sobData.get(i);
				baseDao.execute("update project set prj_number5=? where prj_id=?",new Object[] {i+1,data[1]});
			}
		}
		
		baseDao.deleteById("PROJECTPHASE", "pp_prjid", prj_id);
		
		baseDao.deleteByCondition("resourceassignment", "ra_taskid in (select id from projecttask where prjplanid="+prj_id+")",new Object[] {});
		
		baseDao.deleteById("projecttask","prjplanid",prj_id);
		
		baseDao.deleteById("Teammember", "tm_prjid",prj_id);
		
		baseDao.deleteById("Team", "team_prjid",prj_id);
		
		baseDao.deleteById("Dependency", "de_prjid",prj_id);
		
		baseDao.deleteByCondition("Projecttaskattach", "ptt_taskid in (select id from projecttask where prjplanid="+prj_id+")",new Object[] {});
	
		baseDao.execute("update project set prj_mainproid=null,prj_mainprocode=null,prj_mainproname=null where prj_mainproid="+prj_id);
		// 记录操作
		baseDao.logger.delete(caller, "prj_id", prj_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[] { prj_id});
	}
	
	@Override
	@Transactional
	public void auditProjectRequest(int prj_id,String  caller) {
		Employee employee = SystemSession.getUser();
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller,new Object[] { prj_id });
		// 执行审核操作
		if(("PreProject").equals(caller)) {
			baseDao.audit("PROJECT", "prj_id=" + prj_id, "prj_preaudit", "prj_preauditcode","prj_preauditdate","prj_preauditman");
		}else if("ProjectRequest".equals(caller)) {
			baseDao.audit("PROJECT", "prj_id=" + prj_id, "prj_auditstatus", "prj_auditstatuscode","prj_auditdate","prj_auditman");
		}
		
		if (baseDao.isDBSetting("ProjectRequest", "assProject")) {
			SqlRowList prj = baseDao.queryForRowSet("select * from Project where prj_id=?", prj_id);
			if (prj.next()) {
				SqlRowList rs = baseDao.queryForRowSet("select * from asskind where ak_code='Otp'");
				if (rs.next()) {
					int akdid = baseDao.getSeqId("ASSKINDDETAIL_SEQ");
					int akid = rs.getGeneralInt("ak_id");
					int count = baseDao.getCount("select count(1) from asskindDetail where AKD_ASSCODE='" + prj.getObject("prj_code")
							+ "' and AKD_ASSNAME='" + prj.getObject("prj_name") + "' and AKD_AKID=" + akid);
					if (count == 0) {
						baseDao.execute(
								"insert into asskinddetail(akd_id,AKD_AKID,AKD_DETNO,AKD_ASSNAME,AKD_ASSCODE,AKD_STATUS) values(?,?,?,?,?,'CANUSE')",
								new Object[] { akdid, akid,
										baseDao.getFieldDataByCondition("asskinddetail", "max(nvl(akd_detno,0))+1", "akd_akid=" + akid),
										prj.getObject("prj_name"), prj.getObject("prj_code") });
					}
				} else {
					int akid = baseDao.getSeqId("ASSKIND_SEQ");
					baseDao.execute(
							"insert into asskind(AK_ID,AK_CODE,AK_NAME,AK_TABLE,AK_DBFIND,AK_ASSCODE,AK_ASSNAME,AK_ADDKIND,AK_STATUS,AK_RECORDDATE,AK_RECORDMAN,AK_EMID) values (?,'Otp','项目','AssKindDetail','AssKindDetail','akd_asscode','akd_assname','项目','VALID',sysdate,?,?)",
							new Object[] { akid, SystemSession.getUser().getEm_name(), SystemSession.getUser().getEm_id() });
					int akdid = baseDao.getSeqId("ASSKINDDETAIL_SEQ");
					baseDao.execute(
							"insert into asskinddetail(akd_id,AKD_AKID,AKD_DETNO,AKD_ASSNAME,AKD_ASSCODE,AKD_STATUS) values(?,?,1,?,?,'CANUSE')",
							new Object[] { akdid, akid, prj.getObject("prj_name"), prj.getObject("prj_code") });
				}
			}
		}
		
		//更新项目状态为未启动
		List<String> sqls = new ArrayList<String>();
		String prjSql = "update project set prj_status='未启动',prj_statuscode='UNDOING' where prj_id=" + prj_id;
		sqls.add(prjSql);
		

		//生成文件目录
		boolean fileExist = baseDao.checkIf("projectdoc", "pd_prjid=" + prj_id);
		if(!fileExist){
			Object[] productType = baseDao.getFieldsDataByCondition("project left join plmproducttype on pt_code=prj_producttypecode", new String[]{"pt_code","prj_code"}, "prj_id=" + prj_id);
			if(productType!=null){
				if(productType[0]!=null){ //产品类型存在
					//检查对应产品类型的产品文件是否存在
					boolean prodFile = baseDao.checkIf("prjdoc_temp", "nvl(prjtypecode_,' ')='"+productType[0]+"'");
					if(prodFile){
						String fileSql = "insert into projectdoc(pd_id,pd_code,pd_detno,pd_virtualpath,pd_name,pd_remark,pd_kind,pd_parentid,pd_prjid,pd_tempid) select projectdoc_seq.nextval,code_,detno_,'/"+productType[1]+"'||virtualpath_,name_,remark_,kind_,parentid_,"+prj_id+",id_ from prjdoc_temp where prjtypecode_='"+productType[0]+"'";
						String updatedoc = "update projectdoc a set pd_parentid=(select pd_id from projectdoc b  where a.pd_parentid=b.pd_tempid and pd_prjid='"+prj_id+"') "
								+ "where pd_prjid="+prj_id+" and a.pd_parentid in (select pd_tempid from projectdoc where pd_prjid='"+prj_id+"' )";
						sqls.add(fileSql);
						sqls.add(updatedoc);	
						
						//在文档管理里面生成目录
						int dirid = baseDao.getSeqId("DOCUMENTLIST_SEQ");
						int detnum;
						//获取序号
						Object detno = baseDao.getFieldDataByCondition("documentlist", "max(dl_detno)", "dl_parentid=-1");
						if(detno==null){
							detnum = 1;
						}else{
							detnum = Integer.parseInt(detno.toString()) + 1;
						}
						
						Object[] prj = baseDao.getFieldsDataByCondition("project", new String[]{"prj_code","prj_remark"}, "prj_id=" + prj_id);
						String dir = "insert into documentlist(dl_id,dl_virtualpath,dl_name,dl_remark,dl_createtime,dl_creator,dl_detno,dl_needflowchildren,dl_parentid,dl_style,dl_kind,dl_status,dl_statuscode,dl_prjid) values(" + dirid 
								+ ",'/项目文档/"+prj[0]+"','"+prj[0]+"','"+prj[1]+"',sysdate,'" + employee.getEm_name() + "'," + detnum 
								+ ",0,-1,'目录',-1,'已审核','AUDITED',"+prj_id+")";
						sqls.add(dir);
						String insertDoc = "insert into documentlist(dl_id,dl_virtualpath,dl_name,dl_remark,dl_createtime,dl_creator,dl_detno,dl_needflowchildren,dl_parentid,dl_style,dl_kind,dl_status,dl_statuscode,dl_prjdocid,dl_prjid) select documentlist_seq.nextval"
								+ ",'/项目文档'||pd_virtualpath,pd_name,pd_remark,sysdate,'" + employee.getEm_name() + "'," + detnum 
								+ ",0,pd_parentid,'目录',-1,'已审核','AUDITED',pd_id,"+prj_id+" from projectdoc left join project on pd_prjid=prj_id where prj_id=" 
								+ prj_id + " and pd_kind=-1";
						sqls.add(insertDoc);
						String condition = "dl_prjdocid in (select pd_id from projectdoc where pd_prjid="+prj_id+")";
						String updatedir = "update documentlist a set dl_parentid=(select dl_id from documentlist b  where a.dl_parentid=b.dl_prjdocid and "+condition+") where "
								+ condition + " and a.dl_parentid in (select dl_prjdocid from documentlist where "+condition+")";
						sqls.add(updatedir);
						String updatechild = "update documentlist a set dl_parentid=" + dirid + " where dl_parentid=0 and " + condition;
						sqls.add(updatechild);						
					}
				}
			}

		}
		
		//更新第一个阶段计划实际开始日期为审核日期,阶段计划状态标识为进行中
		String phaseSql = "update projectphase set pp_realstartdate=sysdate,pp_status='进行中' where pp_id=(select pp_id from (select a.*,rownum rn from (select pp_id,pp_phase,pp_detno from projectphase where pp_prjid="+prj_id+" order by pp_detno asc)a) where rn=1)";	
		sqls.add(phaseSql);
		
		//除第一个阶段计划外其它阶段计划状态标识为未启动
		baseDao.execute(sqls);
		
				
		// 记录操作
		baseDao.logger.audit(caller, "prj_id", prj_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller,new Object[] { prj_id });
	
		//参数配置：审核后自动执行转正式项目
		if("PreProject".equals(caller)){
			boolean autoActiveTurn = baseDao.isDBSetting(caller, "autoActiveTurn");
			if(autoActiveTurn) {
				turnProject(String.valueOf(prj_id));
			}
		}
	
		//参数配置：自动生成项目团队
		if("ProjectRequest".equals(caller)){
			boolean f = baseDao.isDBSetting(caller, "autoCreateTeammember");
			if(f) {
					List<Object> chargepersoncode = baseDao.getFieldDatasByCondition("projectphase","distinct pp_chargepersoncode", "pp_prjid="+prj_id);
					for(int i =0;i<chargepersoncode.size();i++) {
						boolean employeecode = baseDao.checkIf("teammember", "tm_prjid="+prj_id+" and tm_employeecode='"+chargepersoncode.get(i)+"'");
						if(!employeecode) {
							baseDao.execute("insert into teammember(tm_id,tm_employeecode,tm_employeename,tm_employeeid,tm_employeejob,tm_teamid,tm_functional,tm_prjid) "
									+ "select teammember_seq.nextval,pp_chargepersoncode,pp_chargeperson,em_id,em_defaulthsid,team_id,nvl(pp_functional,'项目成员'),pp_prjid from projectphase left join employee on "
									+ "pp_chargepersoncode=em_code left join team on pp_prjid=team_prjid where pp_prjid="+prj_id +" and rownum=1 and pp_chargepersoncode='"+chargepersoncode.get(i)+"'");
							baseDao.execute("update teammember set tm_detno=(select nvl(max(tm_detno),0)+1 from teammember where tm_prjid="+prj_id+") where tm_prjid="+prj_id+" and tm_employeecode='"+chargepersoncode.get(i)+"'");
						}
					}
				}
		}
		
		//参数配置：主项目的负责人复制给子项目
		boolean autoLoadInformation = baseDao.isDBSetting(caller, "autoLoadInformation");
		if(autoLoadInformation) {
			baseDao.execute("update project a set (prj_assignto,prj_assigntocode) = (select b.prj_assignto,b.prj_assigntocode " + 
					"from project b where b.prj_id=a.prj_mainproid) where a.prj_mainproid="+prj_id);
		}
		
		//参数配置:是否直接转任务书
		if("ProjectRequest".equals(caller)){
			boolean f = baseDao.isDBSetting(caller, "turnMainTask");
			if(f){
				planMainTask(prj_id);
			}
		}
	}
	
	@Override
	public void resAuditProjectRequest(String  caller,int prj_id) {
		//检查是否已下达任务书
		boolean book = baseDao.checkIf("projectmaintask", "pt_prjid=" + prj_id);
		if(book){
			BaseUtil.showError("项目已下达任务书，不允许反审核！");
		}
		boolean phase = baseDao.checkIf("projectphase", "pp_prjid=" + prj_id + " and pp_status='已完成'");
		if(phase){
			BaseUtil.showError("项目已启动，不允许反审核!");
		}
		
		handlerService.beforeResAudit(caller, new Object[]{prj_id});
		// 执行反审核操作
		if(("PreProject").equals(caller)) {
			baseDao.resAudit("PROJECT", "prj_id=" + prj_id, "prj_preaudit", "prj_preauditcode","prj_preauditdate","prj_preauditman");
		}else if("ProjectRequest".equals(caller)) {
			baseDao.resAudit("PROJECT", "prj_id=" + prj_id, "prj_auditstatus", "prj_auditstatuscode","prj_auditdate","prj_auditman");
		}

		if (baseDao.isDBSetting("Project", "assProject")) {
			Object prjcode = baseDao.getFieldDataByCondition("Project", "prj_code", "prj_id="+prj_id);
			Object akid = baseDao.getFieldDataByCondition("asskind", "ak_id", "ak_code='Otp'");
			if (prjcode!=null&&akid!=null) {
				baseDao.deleteByCondition("asskinddetail", "akd_akid=? and akd_asscode=?",akid,prjcode);
			}
		}
		
		List<String> sqls = new ArrayList<String>();
		//更新第一个项目阶段的实际开始时间和状态为空
		sqls.add("update projectphase set pp_status=null,pp_realstartdate=null where pp_id=(select pp_id from (select pp_id from projectphase where pp_prjid="+prj_id+" order by pp_detno asc)a where rownum=1)");
		//更新其他的项目阶段的状态为空
		sqls.add("update projectphase set pp_status=null where pp_id in (select pp_id from (select pp_id,rownum rn from projectphase where pp_prjid="+prj_id+" order by pp_detno asc)a where rn>1)");
		//删除生成的文件
		sqls.add("delete from documentlist where dl_prjid=" + prj_id);	
		sqls.add("delete from projectdoc where pd_prjid=" + prj_id);		
		
		baseDao.execute(sqls);
		// 记录操作
		baseDao.logger.resAudit(caller, "prj_id", prj_id);
		//执行审核后的其它逻辑
		handlerService.afterResAudit(caller, new Object[] { prj_id });
	}

	@Override
	public void submitProjectRequest(String  caller,int prj_id) {
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,new Object[] { prj_id });
		// 执行提交操作
		if(("PreProject").equals(caller)) {
			baseDao.submit("PROJECT", "prj_id=" + prj_id, "prj_preaudit", "prj_preauditcode");
		}else if("ProjectRequest".equals(caller)) {
			baseDao.submit("PROJECT", "prj_id=" + prj_id, "prj_auditstatus", "prj_auditstatuscode");
		}
		// 记录操作
		baseDao.logger.submit(caller, "prj_id", prj_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller,new Object[] { prj_id });
	}

	@Override
	public void resSubmitProjectRequest(String  caller,int prj_id) {
		//执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, prj_id);
		// 执行反提交操作
		if(("PreProject").equals(caller)) {
			baseDao.resOperate("PROJECT", "prj_id=" + prj_id, "prj_preaudit", "prj_preauditcode");
		}else if("ProjectRequest".equals(caller)) {
			baseDao.resOperate("PROJECT", "prj_id=" + prj_id, "prj_auditstatus", "prj_auditstatuscode");
		}
		// 记录操作
		baseDao.logger.resSubmit(caller, "prj_id", prj_id);
		//执行提交后的其它逻辑
		handlerService.afterResSubmit(caller, prj_id);
	}

	@Override
	public Map<String,Object> getProjectPhase(String productType){
		Map<String,Object> modelMap = new HashMap<String,Object>();
		Map<String,Object> map = null;
		int i=1;
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		List<Object[]> datas = baseDao.getFieldsDatasByCondition("projectphase_temp", new String[]{"ph_name_temp","ph_remark_temp"}, "prjtypecode_=(select pt_code from plmproducttype where pt_code='"+productType+"')  order by ph_detno_temp asc");
		for(Object[] data:datas){
			map = new HashMap<String,Object>();
			map.put("pp_phase",data[0]);
			map.put("pp_remark",data[1]);
			map.put("pp_detno", i);
			i++;
			list.add(map);
		}
		modelMap.put("data", list);
		return modelMap;
	}
	
	@Override
	public int getIdByCode(String formCondition){
		Object id = baseDao.getFieldDataByCondition("project", "prj_id", formCondition);
		if(id==null){
			BaseUtil.showError("该编号的项目不存在");
		}else{
			return Integer.parseInt(id.toString());
		}
		return 0;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void turnProject(String id) {
		Object[] projectdata = baseDao.getFieldsDataByCondition("Project", 
				new String[] {"prj_prstatuscode"}, "prj_id="+id);
		String prstatus = projectdata[0].toString();
		if("pretask".equals(prstatus)) {
			//转立项的主项目保留在录入状态
			boolean nonautomaticAudit = baseDao.isDBSetting("PreProject", "nonautomaticAudit");
			if(nonautomaticAudit) {
				String sql = "update project set prj_prstatus='正式立项',prj_isturnpro=-1,prj_auditstatus='在录入',prj_auditstatuscode='ENTERING' where prj_id="+id;
				baseDao.execute(sql);
			}else {
				String sql = "update project set prj_prstatus='正式立项',prj_isturnpro=-1,prj_auditstatus='已审核',prj_auditstatuscode='AUDITED' where prj_id="+id;
				baseDao.execute(sql);
			}
			
			
			List<Object> sobId = baseDao.getFieldDatasByCondition("project", "prj_id", "prj_mainproid="+id);
			//子项目阶段与主项目阶段相同
			List<Object[]> projectRequest = baseDao.getFieldsDatasByCondition("projectphase", new String[] {"pp_detno","pp_phase","pp_remark"}, "pp_prjid="+id);
			for (Object[] objects : projectRequest) {
				for(Object objs : sobId) {
					String sobsql = "insert into  projectphase(pp_id,pp_prjid,pp_detno,pp_phase,pp_remark) "
							+ "values(projectphase_seq.nextval,"+objs.toString()+",?,?,?)";
					baseDao.execute(sobsql, new Object[] {Integer.parseInt(objects[0].toString()),objects[1],objects[2]});
				}
			}
			//参数配置:自动为子项目带入主项目的项目阶段开始日期和结束日期
			boolean f = baseDao.isDBSetting("PreProject", "autoCopyDate");
			if(f){
				baseDao.execute("update projectphase a set pp_startdate=(select b.pp_startdate from projectphase b  where pp_prjid="+id+" and b.pp_detno=a.pp_detno)" + 
						",pp_enddate=(select c.pp_enddate from projectphase c where  pp_prjid="+id+" and c.pp_detno=a.pp_detno) where pp_prjid in (select distinct prj_id from project where prj_mainproid="+id+")");
			}
			
			//子项目团队与主项目相同
			/*List<Object[]> teammember = baseDao.getFieldsDatasByCondition("teammember", new String[] {"tm_detno","tm_employeecode","tm_employeename","tm_functional","tm_remark","tm_employeejob","tm_teamid","tm_employeeid"}, "tm_prjid="+id);*/
			Object[] team = baseDao.getFieldsDataByCondition("team", new String[] {"team_name","team_recorder",
					"team_recorddate","team_status","team_remark","team_statuscode"}, "team_prjid="+id);
			if (team!=null && team.length>0) {
				for(Object objs : sobId) {
					int teamid = baseDao.getSeqId("Team_seq");
					String teamcode = baseDao.sGetMaxNumber("Team",2);
					Object[] projectsob = baseDao.getFieldsDataByCondition("project", new String[] {"prj_code","prj_name"}, "prj_id="+objs);
					String teamsql = "insert into team( team_id,team_prjid,team_code,team_name,team_pricode,team_prjname,team_recorder,team_recorddate, "
							+ "team_status , team_remark , team_statuscode ) values("+teamid+","+objs.toString()+","+teamcode+",?,?,?,?,to_date(?,'yyyy-MM-dd HH24:MI:SS'),?,?,?)";
					baseDao.execute(teamsql, new Object[] {projectsob[1],projectsob[0],projectsob[1],team[1],team[2],team[3],team[4],team[5]});
				}
			}
			for(Object objs : sobId) {
				List<Object[]> teammember = baseDao.getFieldsDatasByCondition("teammember", new String[] {"tm_detno","tm_employeecode","tm_employeename","tm_functional","tm_remark","tm_employeejob","tm_employeeid"}, "tm_prjid="+id);
				for (Object[] objects : teammember) {
					String sobsql = "insert into  teammember(tm_id,tm_prjid,tm_detno,tm_employeecode,tm_employeename,tm_functional,tm_remark,tm_employeejob,tm_employeeid,tm_teamid) "
							+ "values(teammember_seq.nextval,"+objs.toString()+",?,?,?,?,?,?,?,?)";
					String sobTeamid = baseDao.getFieldValue("team", "team_id", "team_prjid="+objs.toString(), String.class);
					baseDao.execute(sobsql, objects[0].toString(),objects[1],objects[2],objects[3],objects[4],objects[5],objects[6].toString(),sobTeamid);
				}
			}
			//参数配置:自动为子项目带入主项目的项目负责人
			boolean a = baseDao.isDBSetting("PreProject", "autoCopyAssignto");
			if(a){
				baseDao.execute("update projectphase a set pp_chargepersoncode=(select b.pp_chargepersoncode from projectphase b  where pp_prjid="+id+" and b.pp_detno=a.pp_detno)" + 
						",pp_chargeperson=(select c.pp_chargeperson from projectphase c where  pp_prjid="+id+" and c.pp_detno=a.pp_detno),pp_functional=(select d.pp_functional from projectphase d  where pp_prjid="+id+
						" and d.pp_detno=a.pp_detno) where pp_prjid in (select distinct prj_id from project where prj_mainproid="+id+")");
			}
			
			//更新子项目为单据状态
			for(Object objs : sobId) {
				baseDao.execute("update project set prj_auditstatus='在录入',prj_auditstatuscode='ENTERING' where prj_id="+objs);
			}
			//触发自动审批
			handlerService.handler("PreProject", "turnProRequest", "before",new Object[] {Integer.parseInt(id)});
			
			Object code= baseDao.getFieldDataByCondition("Project", "prj_code", "prj_id="+id);
			BaseUtil.appendError("转入成功,项目申请单号:" + "<a href=\"javascript:openUrl('jsps/plm/request/ProjectRequest.jsp?whoami=ProjectRequest&formCondition=prj_codeIS"+code+"')\">" + code.toString() + "</a>&nbsp;");
		}
	}

	@Override
	public boolean isProjectSobHaveData(String id,String caller) {
		if("PreProject".equals(caller)) {
			return false;
		}else if("ProjectRequest".equals(caller)) {
			Object data = baseDao.getFieldDataByCondition("project", "prj_mainproid","prj_mainproid="+id.split("=")[1]);
			if(data!=null) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isProjectTaskHaveData(String id,String caller) {
		if("ProjectRequest".equals(caller)) {
			boolean bool = baseDao.checkIf("projecttask", "prjplanid="+id.split("=")[1]+" and taskclass='pretask'");
			return bool;
		}
		return false;
	}

	@Override
	public int setMainProjectRule(String maincode) {
		if(maincode!=null) {
			String code=baseDao.getFieldValue("Project","prj_mainprocode", "prj_code='"+maincode+"'", String.class);
			if(code==null || code.length()<=0) {
				return 1;
			}
		}
		return -1;
	}
	
}
