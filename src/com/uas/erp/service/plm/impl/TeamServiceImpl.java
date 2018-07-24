package com.uas.erp.service.plm.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.TeamDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Team;
import com.uas.erp.service.plm.TeamService;
@Service
public class TeamServiceImpl implements TeamService {
@Autowired
private BaseDao baseDao;
@Autowired
private TeamDao teamDao;
@Autowired
private HandlerService handlerService;
	@Override
   public void saveTeam(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		if(baseDao.checkIf("Team", "team_prjid="+store.get("team_prjid")))
			BaseUtil.showError("当前项目已存在相应的项目团队!");
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Team", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		List<String> sqls = new ArrayList<String>();
		List<String> updatesqls = new ArrayList<String>();
		for(Map<Object, Object> s:gstore){
			if(s.get("tm_id") == null || s.get("tm_id").equals("") || s.get("tm_id").equals("0")){//新添加的数据，id不存在
				int tm_id = baseDao.getSeqId("Teammember_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "Teammember", new String[]{"tm_id"}, new Object[]{tm_id});
				sqls.add(sql);
				updatesqls.add("update TeamMember set tm_employeeid=(select em_id from employee where em_code='"+s.get("tm_employeecode")+"') where tm_id="+tm_id+"");
			}
		}
		baseDao.execute(sqls);
		baseDao.execute(updatesqls);
		baseDao.execute("update TeamMember set (tm_prjid,tm_name)=(select team_prjid,team_name from team where team_id="+store.get("team_id")+") where tm_teamid="+store.get("team_id"));
		//记录操作
		baseDao.logger.save("Team", "team_id", store.get("team_id"));
	}
	@Override
	public void deleteTeam(int team_id) {
		baseDao.deleteById("Team", "team_id", team_id);
		baseDao.deleteById("Teammember", "tm_teamid", team_id);
		baseDao.logger.delete("Team", "team_id", team_id);
	}
	@Override
	public void deleteDetail(int tm_id) {
		Object tm_teamid = baseDao.getFieldDataByCondition("teammember", "TM_TEAMID", "tm_id="+tm_id);
		Object team_pricode = baseDao.getFieldDataByCondition("team", "team_pricode", "team_id="+tm_teamid);
		baseDao.getFieldDataByCondition("ProjectMainTask left join team on pt_prjcode=team_pricode", "pt_prjcode", "pt_prjcode='"+team_pricode+"'");
		baseDao.deleteById("Teammember", "tm_id", tm_id);
		
	}
	@Override
	public void updateTeamById(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		if(baseDao.checkIf("Team", "team_prjid="+store.get("team_prjid") +" and  team_id!="+store.get("team_id")))
			BaseUtil.showError("当前项目已存在相应的项目团队!");
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Team", "team_id");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "Teammember", "tm_id");
		List<String> updatesqls = new ArrayList<String>();
		for(Map<Object, Object> s:gstore){
			if(s.get("tm_id") == null || s.get("tm_id").equals("") || s.get("tm_id").equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("Teammember_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "Teammember", new String[]{"tm_id"}, new Object[]{id});
				gridSql.add(sql);
				updatesqls.add("update TeamMember set tm_employeeid=(select em_id from employee where em_code='"+s.get("tm_employeecode")+"') where tm_id="+id+"");
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute(updatesqls);
		baseDao.execute("update TeamMember set (tm_prjid,tm_name)=(select team_prjid,team_name from team where team_id="+store.get("team_id")+") where tm_teamid="+store.get("team_id"));
		//记录操作
		baseDao.logger.update("Team", "team_id", store.get("team_id"));
	}
	@Override
	public void insert(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行保存前的其它逻辑
		handlerService.handler("Team", "save", "before", new Object[]{store});
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Team", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save("Team", "team_id", store.get("team_id"));
		//执行保存后的其它逻辑
		handlerService.handler("Team", "save", "after", new Object[]{store});
	}
	@Override
	public Team getTeamByCode(String code) {
		return teamDao.getTeamByCode(code);
	}
	/*
	 * 复制
	 */
	@Override
	public void copyTeam(int id,String code, String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object prjname=baseDao.getFieldDataByCondition("project","prj_name", "prj_code='"+code+"'");
		String copy_teamid=(String) store.get("team_id");
		boolean bool = baseDao.checkIf("TEAM", "team_pricode='" + code + "'");
		if (bool) {
			BaseUtil.showError("该项目编号已存在项目团队!");
			return;
		}
		// 修改
		int baseId = Integer.parseInt(store.get("team_id").toString());
		store.remove("team_id");
		store.remove("team_recorder");
		store.remove("team_recorddate");
		store.remove("team_name");
		store.remove("team_prjid");
		store.remove("team_pricode");
		store.remove("team_prjname");
		int teamid = baseDao.getSeqId("TEAM_SEQ");
		store.put("team_id", teamid);
		store.put("team_pricode", code);
		store.put("team_prjname", prjname);
		store.put("team_prjid",id);
		store.put("team_recorder", SystemSession.getUser().getEm_name());
		store.put("team_recorddate", DateUtil.currentDateString(null));
		String formSql = SqlUtil.getInsertSqlByMap(store, "TEAM");
		baseDao.execute(formSql);
		// 修改Detail
		List<String> gridSql = new ArrayList<String>();
		int seqId = 0;
		SqlRowList sl = baseDao.queryForRowSet("select *  from TeamMember where tm_teamid=" + baseId);
		Map<String, Object> modelMap = null;
		while (sl.next()) {
			seqId = baseDao.getSeqId("TeamMember_SEQ");
			modelMap = sl.getCurrentMap();
			modelMap.remove("tm_id");
			modelMap.remove("tm_teamid");
			modelMap.remove("team_prjid");
			modelMap.put("tm_id", seqId);
			modelMap.put("tm_teamid", teamid);
			String sql = SqlUtil.getInsertSqlByMap(modelMap, "TeamMember", new String[] {}, new Object[] {});
			gridSql.add(sql);
			}
		baseDao.execute(gridSql);
		baseDao.logger.copy("Team",copy_teamid,"team_id",teamid);
		BaseUtil.showError(BaseUtil.getLocalMessage("复制团队成功!TEAMID:")
				+ "<a href=\"javascript:openUrl('jsps/plm/team/team.jsp?formCondition=team_idIS" + teamid
				+ "&gridCondition=tm_teamidIS" + teamid + "')\">" + teamid + "</a>&nbsp;");	
	}
	/**
	 * 转会议申请单
	 */
	@Override
	public Map<String, Object> teamToMeeting(String caller, String id) {
		Object teamid = baseDao.getFieldDataByCondition("team", "team_id", "team_prjid="+id);
		if(teamid==null){
			BaseUtil.showError("该项目未建立项目团队!");
		}
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		
		String[] formFields = {"team_pricode","team_name","prj_name"};
		Object[] formDatas = baseDao.getFieldsDataByCondition("team left join project on team_prjid=prj_id", formFields, "team_prjid='"+id+"'");
		String prjName = formDatas[2].toString();
		StringBuilder employeeId = new StringBuilder();
		StringBuilder employeeName = new StringBuilder();
		//用标记ma_remark来标识该会议参会团队
		StringBuilder remark = new StringBuilder();
		remark.append(prjName); 
		//判断该团队是否存在没有审核的会议申请单，存在则直接显示对应的申请单，并且警告
		Object ma_id = baseDao.getFieldDataByCondition("Meetingroomapply", "ma_id", "ma_remark='"+remark+"' and (ma_status='在录入' or ma_status='已提交')");
		if(ma_id!=null){
			modelMap.put("exceptionInfo", "不能同时存在非审核的会议申请");
			modelMap.put("url","jsps/oa/meeting/meetingroomapply.jsp?formCondition=ma_idIS"+ma_id+"&gridCondition=md_maidIS"+ma_id+"");
			modelMap.put("success", true);
			modelMap.put("id", ma_id);
			return modelMap;
		}
		String[] gridFields = {"tm_employeecode","tm_employeename"};
		List<Object[]> gridDatas = baseDao.getFieldsDatasByCondition("TEAMMEMBER left join team on team_id=tm_teamid", gridFields, "team_prjid='"+id+"'");
		int gridDatasLen = gridDatas.size();
		for(int i = 0 ; i < gridDatasLen ; i++){
			String employeeCode = gridDatas.get(i)[0].toString();
			Object empId = baseDao.getFieldDataByCondition("employee", "em_id", "em_code='"+employeeCode+"'");
			//存在重复的人员，则跳过，继续循环
			if(employeeId.toString().contains(empId.toString())){
				continue;
			}
			employeeId.append("employee#"+empId.toString());
			employeeName.append(gridDatas.get(i)[1].toString());
			if(i!=gridDatasLen-1){
				employeeId.append(";");
				employeeName.append(";");
			}
		}
		//获取会议申请单的id和code
		ma_id = baseDao.getSeqId("MEETINGROOMAPPLY_SEQ");
		String code = baseDao.sGetMaxNumber("Meetingroomapply",2);
		Date nowDate = new Date(System.currentTimeMillis());
		Employee employee = SystemSession.getUser();
		baseDao.execute("insert into Meetingroomapply (ma_id,ma_code,ma_recorder,ma_recorddate,ma_status,ma_statuscode,ma_group,ma_groupid,ma_isturndoc,ma_remark,ma_theme) "
					+ "values ('"+ma_id+"','"+code+"','"+employee.getEm_name()+"',to_date('"+nowDate.toString()+"','YYYY-mm-dd'),'在录入','ENTERING','"+employeeName+"','"+employeeId+"','否','"+remark+"','"+prjName+"')");
		//把参会人员数据加入从表
		if(employeeId!=null&&!"".equals(employeeId.toString())){
			insertAllEmps(employeeId, ma_id);
		}
		modelMap.put("url","jsps/oa/meeting/meetingroomapply.jsp?formCondition=ma_idIS"+ma_id+"&gridCondition=md_maidIS"+ma_id+"");
		modelMap.put("id", ma_id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 把虚拟组中的包含的员工都插入到明细中
	 */
	private void insertAllEmps(Object groupString,Object maid){
		//group:job#8;employee#106;employee#107;org#5
		Set<String> jobs = new HashSet<String>();// 岗位
		Set<String> emps = new HashSet<String>();// 人员
		Set<String> orgs = new HashSet<String>();// 组织
		for (String group : groupString.toString().split(";")) {
			String[] g = group.split("#");
			if ("job".equals(g[0])) {
				jobs.add(g[1]);
			}
			if ("employee".equals(g[0])) {
				emps.add(g[1]);
			}
			if ("org".equals(g[0])) {
				orgs.add(g[1]);
			}
		}
		if (orgs.size() > 0) {
			for (String org : orgs) {
				List<Object> empids = baseDao
						.getFieldDatasByCondition(
								"HRORGEMPLOYEES left join employee on OM_EMID=em_id", "em_id",
								"em_class<>'离职' and OM_ORID=" + org);
				for (Object empid : empids) {
						emps.add(empid.toString());
				}
			}
		}
		if (jobs.size() > 0) {
			for (String job : jobs) {
				List<Object> empids = baseDao
						.getFieldDatasByCondition(
								"employee", "EM_id",
								"em_class<>'离职' and EM_DEFAULTHSID=" + job);
				for (Object empid : empids) {
					emps.add(empid.toString());
				}
			}
		}
		List<String> sqls = new ArrayList<String>();
		int detno = 1;
		for (String empid : emps) {
			String sqldetail = "insert into MeetingDetail(md_detno,md_maid,md_participantsid,md_participants,md_emcode,md_isnoticed,md_id,md_trueparter,MD_CONFIRMTIME) select "
					+ detno++
					+ ","
					+ maid
					+ ",em_id,em_name,em_code,-1,MeetingDetail_seq.nextval,em_name,null from employee where em_code not in (select md_emcode from MeetingDetail where md_maid="+maid+") and em_id="
					+ empid;
			sqls.add(sqldetail);
		}
		baseDao.execute(sqls);
	}
}	

