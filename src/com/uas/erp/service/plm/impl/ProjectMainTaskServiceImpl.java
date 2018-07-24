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
import com.uas.erp.service.plm.ProjectMainTaskService;

@Service
public class ProjectMainTaskServiceImpl implements ProjectMainTaskService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private TaskUtilService taskUtilService;

	@Override
	public void saveProjectMainTask(String formStore, String param, String caller) {
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
	public void deleteProjectMainTask(int id, String caller) {
		// 先删除已分配的任务
		handlerService.handler(caller, "delete", "before", new Object[] { id });
		baseDao.deleteByCondition("ResourceAssignment", "ra_taskid in (select id from projecttask where ptid=" + id + ")");
		baseDao.deleteByCondition("ProjectTask", "ptid=" + id);
		baseDao.deleteById("ProjectMainTask", "pt_id", id);
		baseDao.logger.delete(caller, "pt_id", id);
		handlerService.handler(caller, "delete", "after", new Object[] { id });
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateProjectMainTask(String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<String> sqls = new ArrayList<String>();
		List<Map<Object, Object>> gridstores = BaseUtil.parseGridStoreToMaps(param);
		String updateSql = SqlUtil.getUpdateSqlByFormStore(store, "ProjectMainTask", "pt_id");
		sqls.add(updateSql);
		String code = "PT_" + baseDao.sGetMaxNumber("ProjectTask", 2) + "_";
		int index = 0;
		for (Map<Object, Object> grid : gridstores) {
			Object id = grid.get("id");
			grid.put("prjplanid", store.get("pt_prjid"));
			grid.put("prjplanname", store.get("pt_prjname"));
			index++;
			code = code.substring(0, code.lastIndexOf("_")) + "_" + index;
			grid.put("taskcode", code);
			grid.put("taskcolor", "FF9900");
			grid.put("baselinestartdate", grid.get("startdate"));
			grid.put("baselineenddate", grid.get("enddate"));
			grid.put("recorder", SystemSession.getUser().getEm_name());
			grid.put("recorddate", DateUtil.parseDateToString(new Date(), Constant.YMD));
			grid.put("class", "projecttask");
			grid.remove("handstatus");
			grid.remove("handstatuscode");
			if (id != null && id.equals("0")) {
				grid.remove("id");
				grid.put("id", baseDao.getSeqId("PROJECTTASK_SEQ"));
				sqls.add(SqlUtil.getInsertSqlByMap(grid, "PROJECTTASK"));
			} else
				sqls.add(SqlUtil.getUpdateSqlByFormStore(grid, "ProjectTask", "id"));
		}
		baseDao.execute(sqls);
		if(!baseDao.isDBSetting("ProjectMainTask","noAutoDate"))  taskUtilService.updateDate(store.get("pt_id"), store.get("pt_taskstartdate").toString(), null);
		baseDao.execute("update projecttask set name = REPLACE(name, chr(10), '') where ptid=?",new Object[]{store.get("pt_id")});
		baseDao.logger.update(caller, "pt_id", store.get("pt_id"));
	}

	@Override
	public void submitProjectMainTask(int id, String caller) {
		handlerService.handler(caller, "commit", "before", new Object[] { id });
		Object status = baseDao.getFieldDataByCondition("ProjectMainTask", "pt_statuscode", "pt_id=" + id);
		StateAssert.submitOnlyEntering(status);
		/**
		 * 校验资源分配比例是否够100
		 * */
		List<Object[]>datas=baseDao.getFieldsDatasByCondition("PROJECTTASK", new String[]{"Resourcecode","Resourceunits"},  "ptid=" + id+" ORDER BY DETNO");
		String resourcecode="",resouceunits="";Object []units=null;int sumunit=0;
		for(int i=0;i<datas.size();i++){
			if(datas.get(i)[0]!=null && datas.get(i)[1]!=null){
				resourcecode=String.valueOf(datas.get(i)[0]);
				resouceunits=String.valueOf(datas.get(i)[1]);	
				sumunit=0;
				if(resourcecode.indexOf(",")>0){
					units=resouceunits.split(",");
					if(resourcecode.split(",").length!=units.length) BaseUtil.showError("第"+(i+1)+"行资源分配不对!");

					for(int j=0;j<units.length;j++){
						sumunit+=Integer.parseInt(units[j].toString());
					}
				}else {
					sumunit=Integer.parseInt(String.valueOf(resouceunits));
				}
			}
			if(sumunit!=100) BaseUtil.showError("第"+(i+1) +"行资源分配不对!");
		}
		// 执行反审核操作
		baseDao.submit("ProjectMainTask", "pt_id=" + id, "pt_status", "pt_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pt_id", id);
		handlerService.handler(caller, "commit", "after", new Object[] { id });

	}

	@Override
	public void resSubmitProjectMainTask(int id, String caller) {
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
	public void resAuditProjectMainTask(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("ProjectMainTask", "pt_statuscode", "pt_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("ProjectMainTask", "pt_id=" + id, "pt_status", "pt_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "pt_id", id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void auditProjectMainTask(int id, String caller) {
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
		SqlRowList sl = baseDao.queryForRowSet("select * from projectTask where ptid=" + id);
		// 结案之后重新生成...
		List<String> sqls = new ArrayList<String>();
		Map<String, Object> map = null;
		String[] resourcecode = null;
		String[] resourceemid = null;
		String[] resourcename = null;
		String[] resourceunits = null;
		String resourcetimerate = null;
		StringBuffer sb = new StringBuffer();
		Map<Object, Object> findMap = new HashMap<Object, Object>();
		Object prjid = null;
		while (sl.next()) {
			map = sl.getCurrentMap();
			Object mainValue = map.get("id");
			Object taskname = map.get("name");
			prjid = map.get("prjplanid");
			Object prjplanname = map.get("prjplanname");
			Object startdate = map.get("startdate");
			Object enddate = map.get("enddate");
			Object isneedattach = map.get("isneedattach");
			findMap.put(map.get("detno").toString(), mainValue + "#" + map.get("pretaskdetno"));
			resourcecode = map.get("resourcecode").toString().split(",");
			resourceemid = map.get("resourceemid").toString().split(",");
			resourcename = map.get("resourcename").toString().split(",");
			resourceunits = map.get("resourceunits").toString().split(",");
			resourcetimerate = map.get("resourcetimerate").toString();
			sb.setLength(0);
			sb.append("任务提醒&nbsp;&nbsp;&nbsp;&nbsp;["
					+ DateUtil.parseDateToString(DateUtil.parseStringToDate(null, "yyyy-MM-dd HH:mm:ss"), "MM-dd HH:mm") + "]</br>");
			sb.append("你有新的任务快去看看吧!</br></br>");
			if (!(map.get("pretaskdetno") != null && !map.get("pretaskdetno").equals("0") && !map.get("pretaskdetno").equals(""))) {
				baseDao.updateByCondition(
						"ProjectTask",
						"handstatuscode='DOING',handstatus='" + BaseUtil.getLocalMessage("DOING") + "',realstartdate="
								+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()), "ID='" + map.get("ID") + "'");
				for (int i = 1; i < resourcecode.length + 1; i++) {
					sb.setLength(0);
					sb.append("insert into ResourceAssignment (ra_id,ra_detno,ra_taskid,ra_prjid,ra_prjname,ra_resourcecode,ra_resourcename,ra_emid,ra_taskname,ra_units,ra_startdate,ra_enddate,ra_needattach,ra_statuscode,ra_status,ra_basestartdate,ra_holdtime,ra_type)values(");
					sb.append("'"
							+ baseDao.getSeqId("RESOURCEASSIGNMENT_SEQ")
							+ "','"
							+ i
							+ "','"
							+ mainValue
							+ "','"
							+ prjid
							+ "','"
							+ prjplanname
							+ "','"
							+ resourcecode[i - 1]
									+ "','"
									+ resourcename[i - 1]
											+ "','"
											+ resourceemid[i - 1]
													+ "','"
													+ taskname
													+ "','"
													+ resourceunits[i - 1]
															+ "',"
															+ DateUtil.parseDateToOracleString(Constant.YMD_HMS,
																	DateUtil.parseStringToDate(startdate.toString(), Constant.YMD_HMS))
																	+ ","
																	+ DateUtil.parseDateToOracleString(Constant.YMD_HMS,
																			DateUtil.parseStringToDate(enddate.toString(), Constant.YMD_HMS)) + ",'" + isneedattach + "','START','"
																			+ BaseUtil.getLocalMessage("START") + "',"
																			+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + "," + resourcetimerate + ",'projecttask')");
					sqls.add(sb.toString());
							/*SqlRowList stopsl = baseDao
							.queryForRowSet("select ra_id,ra_taskid from ResourceAssignment where ra_statuscode='START' AND ra_emid="
									+ resourceemid[i - 1]);
					while (stopsl.next()) {
						taskUtilService.stopTask(stopsl.getInt("ra_id"));
					}*/
				}
			}
		}
		// 设置级联关系
		for (Object key : findMap.keySet()) {
			String[] value = findMap.get(key).toString().split("#");
			if (value[1] != null && !value[1].equals("null")) {
				String detno = value[1];
				for (int j = 0; j < detno.split(",").length; j++) {
					sqls.add("insert into dependency (de_id,de_from ,de_to ,de_prjid,de_type)values(" + baseDao.getSeqId("DEPENDENCY_SEQ")
							+ "," + findMap.get(detno.split(",")[j]).toString().split("#")[0] + "," + value[0] + "," + prjid + ",2)");
				}
			}
		}
		baseDao.execute(sqls);
		baseDao.updateByCondition("ProjectMainTask", "pt_statuscode='DOING',pt_status='" + BaseUtil.getLocalMessage("DOING")
				+ "'", "pt_id=" + id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void LoadTaskNode(int id, String type, String caller) {
		SqlRowList sl = baseDao.queryForRowSet("select * from prjtasknode where tn_productkind='" + type + "'");
		List<String> sqls = new ArrayList<String>();
		// 删除之前载入的
		Object[] datas = baseDao.getFieldsDataByCondition("ProjectMainTask", new String[] { "pt_prjid", "pt_prjname" }, "pt_id=" + id);
		baseDao.deleteByCondition("ProjectTask", "ptid='" + id + "'");
		StringBuffer sb = new StringBuffer();
		while (sl.next()) {
			sb.setLength(0);
			sb.append("insert into projectTask (id,ptid,name,pretaskdetno,detno,duration,prjplanid,prjplanname) values(");
			sb.append(baseDao.getSeqId("PROJECTTASK_SEQ") + "," + id + ",'" + sl.getString("tn_name") + "',");
			Object predetno = sl.getObject("tn_pretaskdetno");
			if (predetno != null) {
				sb.append("'" + predetno + "',");
			} else
				sb.append("null,");
			sb.append(sl.getObject("tn_detno") + ",'" + sl.getFloat("tn_duration") + "',");
			sb.append("'" + datas[0] + "','" + datas[1] + "')");
			sqls.add(sb.toString());
		}
		baseDao.execute(sqls);
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
		/**判断是否自动计算开始结束时间*/
		if(!baseDao.isDBSetting( "ProjectMainTask","noAutoDate")) taskUtilService.updateDate(id, startdate, null);
		baseDao.execute("update ProjectMainTask set pt_taskstartdate=" + DateUtil.parseDateToOracleString(Constant.YMD, startdate)
				+ " where pt_id=" + id);
		return true;
	}
}
