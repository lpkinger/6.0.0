package com.uas.erp.service.plm.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Relation;
import net.sf.mpxj.Task;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.plm.ProjectGanttTaskService;
@Service
public class ProjectGanttTaskServiceImpl implements ProjectGanttTaskService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private TaskUtilService taskUtilService;

	@Override
	public void saveProjectGanttTask(String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String formSql = SqlUtil.getInsertSqlByMap(store, "ProjectMainTask");
		baseDao.execute(formSql);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "ProjectTask", "id"); // 把ptid改为从表id
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "pt_id", store.get("pt_id"));
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteProjectGanttTask(int id, String caller) {
		// 先删除已分配的任务
		int prjId=baseDao.getFieldValue("ProjectMainTask", "pt_prjid", "pt_id="+id, Integer.class);
		handlerService.handler(caller, "delete", "before", new Object[] { id });
		baseDao.deleteByCondition("ResourceAssignment", "ra_taskid in (select id from projecttask where nvl(taskclass,' ') <> 'pretask' and prjplanid=" + prjId + ")");
		baseDao.deleteByCondition("ProjectTask", "nvl(taskclass,' ') <> 'pretask' and prjplanid="+prjId);
		baseDao.deleteById("ProjectMainTask", "pt_id", id);
		baseDao.deleteByCondition("Dependency", "de_prjid="+prjId);
		baseDao.updateByCondition("project", "prj_turnstatus=0", "prj_id="+prjId);
		baseDao.logger.delete(caller, "pt_id", id);
		handlerService.handler(caller, "delete", "after", new Object[] { id });
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateProjectGanttTask(String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);	
		baseDao.execute( SqlUtil.getUpdateSqlByFormStore(store, "ProjectMainTask", "pt_id"));
		baseDao.logger.update(caller, "pt_id", store.get("pt_id"));
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void submitProjectGanttTask(int id, String caller) {
		handlerService.handler(caller, "commit", "before", new Object[] { id });
		Object status = baseDao.getFieldDataByCondition("ProjectMainTask", "pt_statuscode", "pt_id=" + id);
		StateAssert.submitOnlyEntering(status);
		
		//检查明细行是否有任务，如果没有则不允许提交
		boolean bool = baseDao.checkIf("projecttask", "prjplanid=(select pt_prjid from projectmaintask where pt_id="+id+")");
		if(!bool){
			BaseUtil.showError("产品任务书明细行没有任务，不允许提交！");
		}
		
		// 执行提交操作
		baseDao.submit("ProjectMainTask", "pt_id=" + id, "pt_status", "pt_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pt_id", id);
		handlerService.handler(caller, "commit", "after", new Object[] { id });

	}

	@Override
	public void resSubmitProjectGanttTask(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("ProjectMainTask", "pt_statuscode", "pt_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { id });
		// 执行反审核操作
		baseDao.resOperate("ProjectMainTask", "pt_id=" + id, "pt_status", "pt_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pt_id", id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { id });
	}

	@Override
	public void resAuditProjectGanttTask(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("ProjectMainTask", "pt_statuscode", "pt_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("ProjectMainTask", "pt_id=" + id, "pt_status", "pt_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "pt_id", id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void auditProjectGanttTask(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("ProjectMainTask", "pt_statuscode", "pt_id=" + id);
		StateAssert.auditOnlyCommited(status);
		// 审核操作
		baseDao.audit("ProjectMainTask", "pt_id=" + id, "pt_status", "pt_statuscode");
		// 更新任务状态为 已审核
		baseDao.audit("ProjectTask", "ptid=" + id, "status", "statuscode");
		// 记录操作
		baseDao.logger.audit(caller, "pt_id", id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void TurnTask(int id, String caller) {
		Employee user = SystemSession.getUser();
		int prjId=baseDao.getFieldValue("PROJECTMAINTASK", "PT_PRJID", "PT_ID="+id, Integer.class);
		SqlRowList sl = baseDao.queryForRowSet("SELECT ID FROM  PROJECTTASK  Where Not Exists (select de_to from Dependency where de_prjid=? and de_to=id) and STARTDATE<=SYSDATE AND DURATION<>0  AND  prjplanid=?",new Object[]{prjId,prjId});
		List<Object> parentIds = baseDao.getFieldDatasByCondition("PROJECTTASK", "PARENTID", "Not Exists (select de_to from Dependency where de_prjid="+prjId+" and de_to=id) AND DURATION<>0 and prjplanid=" + prjId);
		boolean exits;
		List<String> sqls = new ArrayList<String>();
		while(sl.next()){
			sqls.add("UPDATE PROJECTTASK SET HANDSTATUS='"+ BaseUtil.getLocalMessage("DOING")+"',HANDSTATUSCODE='DOING',REALSTARTDATE=SYSDATE WHERE HANDSTATUSCODE<>'FINISHED' and ID="+sl.getObject("ID"));
			//主任务不进行激活
			exits = false;
			if(parentIds.size()>0){
				for(Object pid:parentIds){
					if(pid!=null){
						if(pid.toString().equals(sl.getObject("ID").toString())){
							exits = true;
							//更新主任务的资源为空
							sqls.add("UPDATE PROJECTTASK SET RESOURCECODE=NULL,RESOURCENAME=NULL,RESOURCEEMID=NULL,RESOURCEUNITS=0 WHERE ID=" + sl.getObject("ID"));
							//删除resourceassignment
							sqls.add("DELETE FROM RESOURCEASSIGNMENT WHERE RA_TASKID=" + sl.getObject("ID"));
						}						
					}
				}
			}
			if(!exits){
				sqls.add("UPDATE RESOURCEASSIGNMENT SET RA_STATUSCODE='DOING',RA_STATUS='"+BaseUtil.getLocalMessage("DOING")+"',RA_BASESTARTDATE=SYSDATE WHERE RA_TASKID="+sl.getObject("ID"));
				SqlRowList set = baseDao.queryForRowSet("select wm_concat(ra_id) from RESOURCEASSIGNMENT where RA_RESOURCEID is not null and ra_taskid="+sl.getObject("ID"));
				while(set.next()&&set.getString("wm_concat(ra_id)")!=null){
					Object mmid=baseDao.getFieldDataByCondition("MESSAGEMODEL left join MESSAGEROLE on mm_id=mr_mmid", "distinct mm_id", "MR_ISUSED=-1 AND MM_ISUSED=-1 and mm_caller='ProjectMainTask!Gantt'");
					//调用生成消息的存储过程
				if (mmid != null) {
					baseDao.callProcedure("SP_CREATEINFO",new Object[] { mmid,user.getEm_code(),set.getString("wm_concat(ra_id)"),DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) });
				}		
				}		
			}
		}
		baseDao.execute("update projecttask set Baselinestartdate=startdate,Baselineenddate=enddate  where prjplanid=? and Baselinestartdate is null and startdate is not null and enddate  is not null",prjId);
		baseDao.updateByCondition("ProjectMainTask", "pt_statuscode='DOING',pt_status='" + BaseUtil.getLocalMessage("DOING")+ "'", "pt_id=" + id);
		baseDao.updateByCondition("Project", "prj_statuscode='DOING',prj_status='" + BaseUtil.getLocalMessage("DOING")+ "'", "prj_id=" + prjId);
		baseDao.execute(sqls);
		
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void LoadTaskNode(int id, String type, String caller,String startdate) {
		Object[] msg = baseDao.getFieldsDataByCondition("project left join projectmaintask on pt_prjcode=prj_code", new String[]{"prj_code","prj_producttype","prj_name","prj_id","prj_producttypecode"}, "pt_id=" + id);
		
		//获取任务模板数据
		List<Map<String,Object>> list = baseDao.queryForList("select * from projecttask_temp where prjtypecode_='"+msg[4]+"'");
		if(list.size()<=0){
			BaseUtil.showError("任务书模板为空，载入失败!");
		}
		//检查项目是否建立项目团队
		boolean team = baseDao.checkIf("team", "team_pricode='"+msg[0]+"'");
		if(!team){
			BaseUtil.showError("项目未建立项目团队，载入失败！");
		}
		for(Map<String,Object> map:list){
			if(map.get("RESOURCEID_TEMP")!=null&&!"".equals(map.get("RESOURCEID_TEMP"))){
				//检查任务模板资源是否是未离职的人员
				boolean off = baseDao.checkIf("employee", "em_id in ("+map.get("RESOURCEID_TEMP")+") and nvl(em_class,' ')='离职'");
				if(off){
					BaseUtil.showError("任务书模板中，任务号"+map.get("DETNO_TEMP")+"对应的资源无效，载入失败！");
				}
			
				int idLength = map.get("RESOURCEID_TEMP").toString().split(",").length;
				//检查资源是否在项目团队中存在
				int teammember = baseDao.getCount("select count(1) from team left join teammember on team_id=tm_teamid where team_pricode='"+msg[0]+"' and tm_employeeid in ("+map.get("RESOURCEID_TEMP")+")");				
				if(idLength>teammember){
					BaseUtil.showError("任务书模板中，任务号"+map.get("DETNO_TEMP")+"对应的资源不存在，载入失败！");
				}
			}
		}
		
		//生成任务节点
		if(startdate!=null&&!"".equals(startdate)){
			baseDao.procedure("SP_LOADTASKNODE", new Object[] { id ,startdate});			
		}			


	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean ImportExcel(int id, Workbook wbs, String substring, String startdate) {
		// 清除所有 以前的数据
		// baseDao.deleteByCondition("ProjectTask","ptid="+id);
		int sheetnum = wbs.getNumberOfSheets();
		StringBuffer sb = new StringBuffer();
		Object textValue = "";
		List<String> sqls = new ArrayList<String>();
		Map<String, Object> map = null;
		SqlRowList mainsl = baseDao.queryForRowSet("select *  from ProjectMainTask where pt_id=" + id);
		// 获得最大的ID
		int MaxId = 0;
		SqlRowList sl = baseDao.queryForRowSet("select id  from projecttask where ptid=" + id + " order by id desc");
		if (sl.next()) {
			MaxId = sl.getInt(1);
		}
		if (mainsl.next()) {
			map = mainsl.getCurrentMap();
		}
		if (sheetnum > 0) {
			HSSFSheet sheet = (HSSFSheet) wbs.getSheetAt(0);
			// 再遍历行 从第2行开始
			for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
				HSSFRow row = sheet.getRow(i);
				if (row != null) {
					HSSFCell cel = row.getCell(8);
					String detno = String.valueOf(row.getCell(0).getNumericCellValue());
					detno = detno.substring(0, detno.indexOf("."));
					if (cel == null || !"更新".equals(cel.getStringCellValue())) {
						sb.setLength(0);
						sb.append("insert into ProjectTask(id,detno,name,pretaskdetno,resourcecode,resourcename,resourceemid,resourceunits,duration,RESOURCETIMERATE,tasktype,ptid,taskcode,recorder,recorddate,prjplanid,prjplanname,taskcolor,class) Values( ");
						sb.append(baseDao.getSeqId("PROJECTTASK_SEQ") + ",");
						for (int j = 0; j < 8; j++) {
							textValue = "";
							HSSFCell cell = row.getCell(j);
							if (cell != null) {
								switch (cell.getCellType()) {
								case HSSFCell.CELL_TYPE_NUMERIC:
									textValue = cell.getNumericCellValue();
									break;
								case HSSFCell.CELL_TYPE_STRING:
									textValue = cell.getStringCellValue();
									break;
								case HSSFCell.CELL_TYPE_BOOLEAN:
									textValue = cell.getBooleanCellValue();
									break;
								case HSSFCell.CELL_TYPE_FORMULA:
									textValue = cell.getCellFormula() + "";
									break;
								case HSSFCell.CELL_TYPE_BLANK:
									textValue = "";
									break;
								case HSSFCell.CELL_TYPE_ERROR:
									textValue = "";
									break;
								default:
									textValue = "";
									break;
								}
							}
							if (j == 3) {
								// 分配人的情况 最好是按编号找
								if (textValue == "") {
									BaseUtil.showError("提示第" + (i + 1) + "行 没有设置任务处理人员");
								} else {
									String arr[] = textValue.toString().split("#");
									String condition = "em_code in (";
									for (int k = 0; k < arr.length; k++) {
										condition += "'" + arr[k].trim() + "'";
										if (k < arr.length - 1) {
											condition += ",";
										}
									}
									condition += ")";
									List<Employee> employees = employeeDao.getEmployeesByConditon(condition);
									if (employees.size() != arr.length) {
										BaseUtil.showError("提示第" + (i + 1) + "行 设置的处理人员有误!可能名称不正确或不存在该员工!");
									} else {
										String resourcecode = "";
										String resourcename = "";
										String resourceemid = "";
										String resourceunits = "";
										//int unit = 100 / employees.size();
										for (int m = 0; m < employees.size(); m++) {
											resourcecode += employees.get(m).getEm_code();
											resourcename += employees.get(m).getEm_name();
											resourceemid += employees.get(m).getEm_id();
											if (m < employees.size() - 1) {
												resourcecode += ",";
												resourcename += ",";
												resourceemid += ",";											
											} 
										}
										sb.append("'" + resourcecode + "','" + resourcename + "','" + resourceemid + "',");
									}
								}

							} else if (j==4){
								String resourcecodes=row.getCell(3).getStringCellValue();String resourceunits="";
								if(!StringUtil.hasText(textValue)){
									int size=resourcecodes.split("#").length;
									int unit=100/size;									
									for (int m=0;m < size- 1;i++) {
										if (m < size - 1) {
											resourceunits += unit + ",";
										} else {
											resourceunits += 100 - 100 * (size - 1) / size;
										}								    	
									}
								}else if (textValue.toString().indexOf(".") > 0) {
									// 存在。0 则去掉
									textValue= textValue.toString().substring(0, textValue.toString().indexOf("."));

								}

								sb.append("'"+("".equals(resourceunits)?textValue:resourceunits)+"',");
							}else if (j == 2 || j==1) {
								if (textValue.toString().indexOf(".") > 0) {
									// 存在。0 则去掉
									sb.append("'" + textValue.toString().substring(0, textValue.toString().indexOf(".")) + "',");
								} else if (textValue.equals("")) {
									sb.append("null,");
								} else {
									sb.append("'" + textValue + "',");
								}
							} else if (j == 7) {
								if (textValue.toString().trim().equals("test")) {
									sb.append("'test',");
								} else
									sb.append("'normal',");
							} else if (textValue.equals("")) {
								sb.append("null,");
							} else {
								sb.append("'" + textValue + "',");
							}
						}
						sb.append(id + ",'" + detno + "','" + SystemSession.getUser().getEm_name() + "',"
								+ DateUtil.parseDateToOracleString(Constant.YMD, new Date()) + ",'" + map.get("pt_prjid") + "','"
								+ map.get("pt_prjname") + "','FF900','projecttask')");
						sqls.add(sb.toString());
					} else {
						// 修改操作
						sb.setLength(0);
						sb.append("update ProjectTask set ");
						for (int j = 0; j < row.getLastCellNum(); j++) {
							textValue = "";
							HSSFCell cell = row.getCell(j);
							if (cell != null) {
								switch (cell.getCellType()) {
								case HSSFCell.CELL_TYPE_NUMERIC:
									textValue = cell.getNumericCellValue();
									break;
								case HSSFCell.CELL_TYPE_STRING:
									textValue = cell.getStringCellValue();
									break;
								case HSSFCell.CELL_TYPE_BOOLEAN:
									textValue = cell.getBooleanCellValue();
									break;
								case HSSFCell.CELL_TYPE_FORMULA:
									textValue = cell.getCellFormula() + "";
									break;
								case HSSFCell.CELL_TYPE_BLANK:
									textValue = "";
									break;
								case HSSFCell.CELL_TYPE_ERROR:
									textValue = "";
									break;
								default:
									textValue = "";
									break;
								}
							}
							if (j == 1) {
								sb.append("name='" + textValue + "',");
							} else if (j == 2) {
								if (textValue.toString().indexOf(".") > 0) {
									// 存在。0 则去掉
									sb.append("pretaskdetno='" + textValue.toString().substring(0, textValue.toString().indexOf("."))
											+ "',");
								} else if (textValue.equals("")) {
									sb.append("pretaskdetno=null,");
								} else {
									sb.append("pretaskdetno='" + textValue + "',");
								}
							} else if (j == 3) {
								// 分配人的情况 最好是按编号找
								if (textValue == "") {
									BaseUtil.showError("提示第" + (i + 1) + "行 没有设置任务处理人员");
								} else {
									String arr[] = textValue.toString().split("#");
									String condition = "em_code in(";
									for (int k = 0; k < arr.length; k++) {
										condition += "'" + arr[k].trim() + "'";
										if (k < arr.length - 1) {
											condition += ",";
										}
									}
									condition += ")";
									List<Employee> employees = employeeDao.getEmployeesByConditon(condition);
									if (employees.size() != arr.length) {
										BaseUtil.showError("提示第" + (i + 1) + "行 设置的处理人员有误!可能名称不正确或不存在该员工!");
									} else {
										String resourcecode = "";
										String resourcename = "";
										String resourceemid = "";
										//String resourceunits = "";
										int unit = 100 / employees.size();
										for (int m = 0; m < employees.size(); m++) {
											resourcecode += employees.get(m).getEm_code();
											resourcename += employees.get(m).getEm_name();
											resourceemid += employees.get(m).getEm_id();
											/*	if (m < employees.size() - 1) {
												resourcecode += ",";
												resourcename += ",";
												resourceemid += ",";
												resourceunits += unit + ",";
											} else {
												resourceunits += 100 - 100 * (employees.size() - 1) / employees.size();
											}*/
										}
										sb.append("resourcecode='" + resourcecode + "',resourcename='" + resourcename + "',resourceemid='"
												+ resourceemid + "',");
									}
								}
							} else if (j == 4) {
								String resourcecodes=row.getCell(3).getStringCellValue();
								String resourceunits="";
								if(!StringUtil.hasText(textValue)){
									int size=resourcecodes.split("#").length;
									int unit=100/size;									
									for (int m=0;m < size- 1;i++) {
										if (m < size - 1) {
											resourceunits += unit + ",";
										} else {
											resourceunits += 100 - 100 * (size - 1) / size;
										}								    	
									}
								}
								textValue =textValue.toString().indexOf(".0")>0?textValue.toString().substring(0, textValue.toString().indexOf(".")):textValue;
								sb.append("resourceunits="+"'"+("".equals(resourceunits)?textValue:resourceunits)+"',");
							} else if (j == 5) {
								if (textValue != null) {
									sb.append("duration='" + (textValue.toString().indexOf(".0")>0?textValue.toString().substring(0, textValue.toString().indexOf(".")):textValue) + "',");
								}
							} else if (j == 6) {
								if (textValue != null) {
									sb.append("RESOURCETIMERATE='" + (textValue.toString().indexOf(".0")>0?textValue.toString().substring(0, textValue.toString().indexOf(".")):textValue) + "',");
								}
							} else if (j == 7) {
								if (textValue.toString().trim().equals("test")) {
									sb.append("tasktype='test',");
								} else
									sb.append("tasktype='normal',");
							}
						}
						sqls.add(sb.toString().substring(0, sb.toString().length() - 1) + " where ptid=" + id + " and detno=" + detno + "");
					}
				}
			}
		}
		baseDao.execute(sqls);
		if(!baseDao.isDBSetting( "ProjectMainTask","noAutoDate")) taskUtilService.updateDate(id, startdate, null);
		baseDao.execute("update ProjectMainTask set pt_taskstartdate=" + DateUtil.parseDateToOracleString(Constant.YMD, startdate)
				+ " where pt_id=" + id);
		// 设置前后置关系 可能在录入导入 可能批量更新
		SqlRowList dependency = baseDao
				.queryForRowSet("select detno,id,pretaskdetno,prjplanid  from projectmaintask left join projecttask   on pt_id=ptid where ptid="
						+ id + " and pt_statuscode='DOING'");
		Map<Object, Object> findMap = new HashMap<Object, Object>();
		Object prjid = null;
		while (dependency.next()) {
			prjid = dependency.getInt("prjplanid");
			findMap.put(dependency.getString("detno"), dependency.getInt("id") + "#" + dependency.getObject("pretaskdetno"));
		}
		sqls = new ArrayList<String>();
		// 设置级联关系
		for (Object key : findMap.keySet()) {
			String[] value = findMap.get(key).toString().split("#");
			if (value[1] != null && !value[1].equals("null") && Integer.parseInt(value[0]) > MaxId) {
				String detno = value[1];
				for (int j = 0; j < detno.split(",").length; j++) {
					sqls.add("insert into dependency (de_id,de_from ,de_to ,de_prjid,de_type)values(" + baseDao.getSeqId("DEPENDENCY_SEQ")
							+ "," + findMap.get(detno.split(",")[j]).toString().split("#")[0] + "," + value[0] + "," + prjid + ",2)");
				}
			}
		}
		baseDao.execute(sqls);
		/**
		 *分析后续更新进去的任务并触发 
		 * */
		Object statuscode=map.get("pt_statuscode");
		if("DOING".equals(statuscode)){
			taskUtilService.insertTaskAfter(MaxId, id);
		}	
		return true;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void End(int id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "END", "before", new Object[] { id });
		// 结案操作
		baseDao.updateByCondition("ProjectMainTask",
				"pt_statuscode='FINISHED',pt_status='" + BaseUtil.getLocalMessage("FINISHED") + "'", "pt_id=" + id);
		// 一个立项可能对应多张研发任务书 ...
		baseDao.updateByCondition("ProjectTask", "statuscode='FINISH',handstatus='" + BaseUtil.getLocalMessage("FINISH") + "'",
				"ptid=" + id);
		baseDao.updateByCondition("ResourceAssignment",
				"ra_auditstatuscode='FINISH',ra_auditstatus='" + BaseUtil.getLocalMessage("FINISH") + "'", "ra_ptid=" + id
				+ " AND ra_statuscode<>'FINISHED'");
		// 记录操作
		baseDao.logger.end(caller, "pt_id", id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "END", "after", new Object[] { id });
	}

	@Override
	public void resEnd(int id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "resEnd", "before", new Object[] { id });
		baseDao.audit("ProjectMainTask", "pt_id=" + id, "pt_status", "pt_statuscode");
		baseDao.updateByCondition("ProjectTask", "statuscode='FINISH',handstatus='" + BaseUtil.getLocalMessage("FINISH") + "'",
				"ptid=" + id);
		baseDao.audit("ResourceAssignment", "ra_ptid=" + id + " AND ra_statuscode<>'FINISHED'", "ra_auditstatus", "ra_auditstatuscode");
		// 记录操作
		baseDao.logger.resEnd(caller, "pt_id", id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "resEnd", "after", new Object[] { id });
	}

	@Override
	public boolean ImportMpp(int id, ProjectFile pf, String substring, String startdate) {
		List<String> sqls = new ArrayList<String>();
		Map<String, Object> map = null;
		int index = 1;
		int duration = 8;
		// 任务
		List<Task> tasks = pf.getAllTasks();
		// 资源分配
		// List<ResourceAssignment>
		// resourceassignment=pf.getAllResourceAssignments();
		// 获取所有资源
		// List<Resource> resources=pf.getAllResources();
		Object data = baseDao.getFieldDataByCondition("ProjectBaseData", "pd_dayhours", "1=1");
		if (data != null) {
			duration = (int) Integer.parseInt(data.toString());
		}
		;
		for (Task task : tasks) {
			List<Relation> relations = task.getPredecessors();
			if (task.getID() != 0) {
				map = new HashMap<String, Object>();
				String pretask = "";
				map.put("detno", index);
				map.put("name", task.getName() != null ? task.getName() : "");
				if (relations != null && !relations.isEmpty()) {
					for (Relation relation : relations) {
						if (relation.getType().name().equals("FS")) {
							pretask += relation.getSourceTask() + ",";
						}
					}
				}
				if (pretask.length() > 1) {
					pretask = "'" + pretask.substring(0, pretask.lastIndexOf(",")) + "'";
				} else
					pretask = null;
				sqls.add("insert into projecttask (id,detno,name,pretaskdetno,duration,ptid) values (PROJECTTASK_SEQ.nextval," + index
						+ ",'" + task.getName() + "'," + pretask + ","
						+ NumberUtil.formatDouble(duration * (task.getDuration().getDuration()), 1) + "," + id + ")");
				index++;
			}
		}
		baseDao.execute(sqls);
		return true;
	}
	
	@Override
	public Map<String,Object> getTaskCompletion(Integer taskId, Integer resourceEmpId){
		Map<String,Object> map = new HashMap<String,Object>();
		Object[] obj = baseDao.getFieldsDataByCondition("resourceassignment", 
				new String[]{"ra_units","ra_taskpercentdone","round((ra_units*ra_taskpercentdone/100),2)"}, 
					"ra_taskid=" + taskId + " and ra_emid=" + resourceEmpId);
		Map<String,Object> raMap = new HashMap<String,Object>();
		if(obj!=null){
			raMap.put("assignPercent", obj[0]);
			raMap.put("percentDone", obj[1]);
			raMap.put("taskPercent", obj[2]);			
		}
		map.put("taskMsg", raMap);
		
		List<Map<String,Object>> list = baseDao.queryForList("select wr_redcord,wr_percentdone,to_char(wr_recorddate,'yyyy-mm-dd HH24:mi:ss')wr_recorddate from workrecord where wr_taskid=" + taskId + " and wr_recorderemid=" + resourceEmpId + " and nvl(wr_statuscode,' ')<>'COMMITED' order by wr_recorddate desc");
		map.put("workRecord", list);
		return map;
	}
	
	@Override
	public void getPreTask(int id, String caller) {
		Employee user = SystemSession.getUser();
		/*int prjId=baseDao.getFieldValue("PROJECTMAINTASK", "PT_PRJID", "PT_ID="+id, Integer.class);*/
		SqlRowList sl = baseDao.queryForRowSet("SELECT ID FROM  PROJECTTASK  Where Not Exists (select de_to from Dependency where de_prjid=? and de_to=id) and (STARTDATE-1)<=SYSDATE AND  prjplanid=?",new Object[]{id,id});
		List<Object> parentIds = baseDao.getFieldDatasByCondition("PROJECTTASK", "PARENTID", "Not Exists (select de_to from Dependency where de_prjid="+id+" and de_to=id) and prjplanid=" + id);
		boolean exits;
		List<String> sqls = new ArrayList<String>();
		while(sl.next()){
			sqls.add("UPDATE PROJECTTASK SET HANDSTATUS='"+ BaseUtil.getLocalMessage("DOING")+"',HANDSTATUSCODE='DOING',REALSTARTDATE=SYSDATE WHERE HANDSTATUSCODE<>'FINISHED' and ID="+sl.getObject("ID"));
			//主任务不进行激活
			exits = false;
			if(parentIds.size()>0){
				for(Object pid:parentIds){
					if(pid!=null){
						if(pid.toString().equals(sl.getObject("ID").toString())){
							exits = true;
							//更新主任务的资源为空
							sqls.add("UPDATE PROJECTTASK SET RESOURCECODE=NULL,RESOURCENAME=NULL,RESOURCEEMID=NULL,RESOURCEUNITS=0 WHERE ID=" + sl.getObject("ID"));
							//删除resourceassignment
							sqls.add("DELETE FROM RESOURCEASSIGNMENT WHERE RA_TASKID=" + sl.getObject("ID"));
						}						
					}
				}
			}
			if(!exits){
				sqls.add("UPDATE RESOURCEASSIGNMENT SET RA_STATUSCODE='DOING',RA_STATUS='"+BaseUtil.getLocalMessage("DOING")+"',RA_BASESTARTDATE=SYSDATE WHERE RA_TASKID="+sl.getObject("ID"));
				SqlRowList set = baseDao.queryForRowSet("select wm_concat(ra_id) from RESOURCEASSIGNMENT where RA_RESOURCEID is not null and ra_taskid="+sl.getObject("ID"));
				while(set.next()&&set.getString("wm_concat(ra_id)")!=null){
					Object mmid=baseDao.getFieldDataByCondition("MESSAGEMODEL left join MESSAGEROLE on mm_id=mr_mmid", "distinct mm_id", "MR_ISUSED=-1 AND MM_ISUSED=-1 and mm_caller='ProjectMainTask!Gantt'");
					//调用生成消息的存储过程
				if (mmid != null) {
					baseDao.callProcedure("SP_CREATEINFO",new Object[] { mmid,user.getEm_code(),set.getString("wm_concat(ra_id)"),DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) });
				}		
				}		
			}
		}
		baseDao.execute("update projecttask set Baselinestartdate=startdate,Baselineenddate=enddate  where prjplanid=? and Baselinestartdate is null and startdate is not null and enddate  is not null",id);
		baseDao.updateByCondition("ProjectMainTask", "pt_statuscode='DOING',pt_status='" + BaseUtil.getLocalMessage("DOING")+ "'", "pt_id=" + id);
		baseDao.updateByCondition("Project", "prj_statuscode='DOING',prj_status='" + BaseUtil.getLocalMessage("DOING")+ "'", "prj_id=" + id);
		baseDao.execute(sqls);
	}
}
