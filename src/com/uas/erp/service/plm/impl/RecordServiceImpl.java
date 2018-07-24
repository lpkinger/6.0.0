package com.uas.erp.service.plm.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DataListComboDao;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.dao.common.FormAttachDao;
import com.uas.erp.dao.common.FormDao;
import com.uas.erp.dao.common.RecordDao;
import com.uas.erp.model.DataListCombo;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Form;
import com.uas.erp.model.FormAttach;
import com.uas.erp.model.FormDetail;
import com.uas.erp.model.FormItems;
import com.uas.erp.model.FormPanel;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.common.FormAttachService;
import com.uas.erp.service.plm.RecordService;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class RecordServiceImpl implements RecordService {
	@Autowired
	private FormDao formDao;
	@Autowired
	private DataListComboDao dataListComboDao;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private FormAttachDao formAttachDao;
	@Autowired
	private RecordDao recordDao;
	@Autowired
	private DetailGridDao detailGridDao;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private TaskUtilService taskUtilService;
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	@Autowired
	private FormAttachService  formAttachService;

	@Override
	public FormPanel getFormItemsByCaller(String caller, String condition, String language) {

		FormPanel formPanel = new FormPanel();
		Form form = formDao.getForm(caller, SpObserver.getSp());
		SqlRowList rs = baseDao
				.queryForRowSet("SELECT * from ResourceAssignment  left join workrecord on ra_id=wr_raid left join project on ra_prjid=prj_id "
						+ "left join projecttask on ra_taskid=id where "
						+ condition);
		StringBuffer sb = new StringBuffer("{");
		if (rs.next()) {
			Object laststartdate = rs.getObject("ra_laststartdate");
			Object basestartdate = rs.getObject("ra_basestartdate");
			//添加项目编号
			sb.append("prj_code:");
			sb.append("\"" + rs.getGeneralString("prj_code") + "\"");
			//添加任务的状态
			sb.append(",status:");
			sb.append("\"" + rs.getGeneralString("status") + "\"");
			sb.append(",statuscode:");
			sb.append("\"" + rs.getGeneralString("statuscode") + "\"");			
			sb.append(",wr_prjid:");
			sb.append(rs.getInt("ra_prjid"));
			sb.append(",wr_prjname:");
			sb.append("\"" + rs.getGeneralString("ra_prjname") + "\"");
			sb.append(",wr_taskid:");
			sb.append(rs.getInt("ra_taskid"));
			sb.append(",wr_taskname:");
			sb.append("\"" + rs.getGeneralString("ra_taskname") + "\"");
			sb.append(",wr_assignpercent:");
			sb.append(rs.getInt("ra_units"));
			sb.append(",wr_taskstartdate:");
			sb.append("\""
					+ DateUtil.parseDateToString(
							DateUtil.parseStringToDate(rs.getObject("ra_startdate")!=null?rs.getObject("ra_startdate").toString():null, Constant.YMD_HMS),
							Constant.YMD_HMS) + "\"");
			sb.append(",wr_taskenddate:");
			sb.append("\""
					+ DateUtil.parseDateToString(
							DateUtil.parseStringToDate(rs.getObject("ra_enddate")!=null?rs.getObject("ra_enddate").toString():null, Constant.YMD_HMS),
							Constant.YMD_HMS) + "\"");
			sb.append(",wr_taskpercentdone:");
			sb.append(rs.getObject("ra_taskpercentdone"));
			sb.append(",wr_needattach:");
			sb.append(rs.getInt("ra_needattach"));
			sb.append(",wr_raid:");
			sb.append(rs.getInt("ra_id"));
			sb.append(",wr_type:\"");
			sb.append(rs.getObject("ra_type"));
			sb.append("\",ra_statuscode:");
			sb.append("\"" + rs.getString("ra_statuscode") + "\"");
			sb.append(",ra_status:");
			sb.append("\"" + rs.getString("ra_status") + "\"");
			sb.append(",ra_taskusehours:");
			sb.append("\"" + rs.getFloat("ra_taskusehours") + "\"");
			sb.append(",ra_worktype:");
			sb.append("\"" + rs.getString("ra_worktype") + "\"");
			if (laststartdate != null) {
				sb.append(",ra_laststartdate:");
				sb.append("\""
						+ DateUtil.parseDateToString(
								DateUtil.parseStringToDate(laststartdate.toString(), Constant.YMD_HMS),
								Constant.YMD_HMS) + "\"");
			}
			if (basestartdate != null) {
				sb.append(",ra_basestartdate:");
				sb.append("\""
						+ DateUtil.parseDateToString(
								DateUtil.parseStringToDate(basestartdate.toString(), Constant.YMD_HMS),
								Constant.YMD_HMS) + "\"");
			}
			sb.append(",wr_assignto:");
			sb.append("\"" + rs.getGeneralString("prj_assignto") + "\"");
			sb.append(",wr_assigndept:");
			sb.append("\"" + rs.getGeneralString("prj_organigerdep") + "\"");
			SqlRowList slnew = baseDao
					.queryForRowSet("select description,tasktype,realenddate,point,pretaskdetno,ptid from projecttask where ID="
							+ rs.getInt("ra_taskid"));
			if (slnew.next()) {
				String description = slnew.getGeneralString("description");
				sb.append(",description:");
				sb.append("\"" + description + "\"");
				sb.append(",tasktype:");
				sb.append("\"" + slnew.getGeneralString("tasktype") + "\"");
				sb.append(",pretaskdetno:");
				sb.append("\"" + slnew.getObject("pretaskdetno") + "\"");
				Object realendate = slnew.getObject("realenddate");
				if (realendate != null) {
					sb.append(",wr_finishdate:");
					sb.append("\""
							+ DateUtil.parseDateToString(
									DateUtil.parseStringToDate(realendate.toString(), Constant.YMD_HMS),
									Constant.YMD_HMS) + "\"");
					sb.append(",ra_taskpoint:");
					sb.append("\"" + slnew.getFloat("point") + "\"");
				}
				sb.append(",ptid:");
				sb.append("\"" + slnew.getObject("ptid") + "\"");
			}
		}
		form.setDataString(sb.substring(0, sb.length()) + "}");
		List<DataListCombo> combos = dataListComboDao.getComboxsByCaller(caller, SpObserver.getSp());
		List<FormDetail> formDetails = form.getFormDetails();
		List<FormItems> items = new ArrayList<FormItems>();
		Map<String, List<FormDetail>> map = new HashMap<String, List<FormDetail>>();// form分组
		for (FormDetail formDetail : formDetails) {
			if (!map.containsKey(formDetail.getFd_group())) {
				List<FormDetail> list = new ArrayList<FormDetail>();
				list.add(formDetail);
				map.put(formDetail.getFd_group(), list);
			} else {
				List<FormDetail> list = map.get(formDetail.getFd_group());
				list.add(formDetail);
				map.put(formDetail.getFd_group(), list);
			}
		}
		int count = 1;
		for (FormDetail formDetail : formDetails) {
			if (formDetail.getFd_type() != null) {
				if (formDetail.getFd_type().equals("MT")) {
					if (formDetail.getFd_logictype() != null && !formDetail.getFd_logictype().equals("")) {
					items.add(new FormItems(count, formDetail.getFd_group(), formDetail, combos));
					}
				} else {
					items.add(new FormItems(count, formDetail.getFd_group(), formDetail, combos));
				}
			}
		}
		formPanel.setData(form.getDataString());
		formPanel.setItems(items);
		formPanel.setFo_id(form.getFo_id());
		formPanel.setButtons(form.getFo_button4add());
		return formPanel;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveWorkRecord(String formStore, String language, Employee employee) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Object fileStore = store.get("fileStore");
		handlerService.handler("WorkRecord", "commit", "before",new Object[]{store});
		
		//检测工作描述是否配置及字数
		String data = baseDao.getDBSetting("WorkRecord", "Description");
		if(data!=null){
			try{
				int description = Integer.parseInt(data);
				if(description<=0){
					BaseUtil.showError("请在参数配置中配置有效的字数(正整数)");
				}
				String wr_description = store.get("wr_redcord").toString();
				if(wr_description.length()<description){
					BaseUtil.showError("工作描述字数少于"+description+"个");
				}
			}catch(NumberFormatException e){
				BaseUtil.showError("请在参数配置中配置有效的字数(正整数)");
			}
		}
		
		//保存workrecord
		store.remove("fileStore");
		store.remove("ra_laststartdate");
		store.remove("ra_taskusehours");
		store.remove("ra_basestartdate");
		store.remove("ra_statuscode");
		store.remove("ra_worktype");
		Object wrtaskpercentdone = baseDao.getFieldDataByCondition("resourceassignment", "nvl(ra_taskpercentdone,0)", "ra_id=" + store.get("wr_raid")); //取累计完成率
		store.put("wr_taskpercentdone",wrtaskpercentdone.toString());
		String formSql = SqlUtil.getInsertSqlByMap(store, "WORKRECORD");
		baseDao.execute(formSql);

		//保存文件
		if(fileStore!=null&&!"".equals(fileStore)){
			if(!"[]".equals(fileStore)){
				List<Map<Object, Object>> gstoreList = BaseUtil.parseGridStoreToMaps(fileStore.toString());			
				for(Map<Object,Object> map:gstoreList){
					map.put("ptt_flag", -1);
				}				
				List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstoreList, "PROJECTTASKATTACH", "ptt_id");
				baseDao.execute(gridSql);				
			}
		}
		
		//检测form配置是否配置自动触发审批流
		boolean autoFlow = baseDao.checkIf("form", "fo_caller='WorkRecord' and fo_isautoflow=-1");
		if(autoFlow){
			//判断上一次提交是否未审批
			boolean notAudit = baseDao.checkIf("workrecord", "wr_recorderemid=" + employee.getEm_id() + " and wr_taskid=" + store.get("wr_taskid") + " and wr_statuscode!='AUDITED'");
			if(notAudit){
				BaseUtil.showError("本任务上一次提交未审批，保存失败!");
			}		
			updateTask(Integer.parseInt(store.get("wr_id").toString()),"WorkRecord","save"); 
		}else{
			updateTask(Integer.parseInt(store.get("wr_id").toString()),"WorkRecord","audit");
		}	
	}
	
	
	//更新任务
	public void updateTask(int id,String caller,String processType){
		SqlRowSet rs = baseDao.getJdbcTemplate().queryForRowSet("select wr_taskid,wr_assignpercent,wr_percentdone,wr_prjid,wr_raid,to_char(wr_recorddate,'yyyy-mm-dd HH24:mi:ss') wr_recorddate,wr_recorder from workrecord where wr_id=" + id);
		if(rs.next()){
			int keyValue = rs.getInt("wr_taskid");
			int prjid = rs.getInt("wr_prjid");
			int assignpercent = rs.getInt("wr_assignpercent");		
			double thispercentdone = NumberUtil.formatDouble(rs.getString("wr_percentdone").toString(),2);
			Object datas[] = baseDao.getFieldsDataByCondition("ProjectTask", new String[]{"nvl(percentdone,0)","tasktype","preconditioncode","backconditioncode"}, "id="+rs.getInt("wr_taskid"));
			double alreadydone=NumberUtil.formatDouble(baseDao.getSummaryByField("WorkRecord", "wr_percentdone", "wr_raid='"+rs.getInt("wr_raid")+"'"),2);		
			double taskpercentdone=Double.parseDouble(datas[0].toString());	
			
			Object[] obj = baseDao.getFieldsDataByCondition("resourceassignment", new String[]{"ra_laststartdate","ra_basestartdate","ra_worktype","ra_taskusehours","ra_statuscode","ra_startdate"}, "ra_id=" + rs.getInt("wr_raid"));
			float taskusehours = Float.parseFloat((obj[3]==null?"":obj[3]).toString());
			
			float time = 0;
			Object findstartdate = obj[0] != null && !obj[0].equals("") ? obj[0] : obj[1]!= null && !obj[1].equals("") ?obj[1]:obj[5];
			// 单据日报类型
			Object type = obj[2];
			float usehours = getTime(findstartdate.toString(), DateUtil.parseDateToString(new Date(), Constant.YMD_HMS))
					+ Float.parseFloat((obj[3]==null?"":obj[3]).toString());
			if ((obj[4]==null?"":obj[4]).toString().equals("STOP")) {
				time = taskusehours;
			} else
				time = taskusehours + usehours;
			taskpercentdone = NumberUtil.formatDouble(NumberUtil.formatDouble(assignpercent * thispercentdone, 2)/ 100 + taskpercentdone,2);
			if (taskpercentdone > 100 || taskpercentdone>99.9) {
				taskpercentdone = 100;
			}
			if(taskpercentdone == 100){
				/**若任务属于测试任务*/
				if("test".equals(datas[1])){
					boolean bool=baseDao.checkIf("CHECKLISTBASE","cb_taskid="+keyValue+" AND cb_statuscode='AUDITED' or (cb_taskid="+keyValue+" and cb_statuscode='FINISH')");
					if(!bool) BaseUtil.showError("测试任务提交完成时需完成相应的测试清单!");
				}	
				//判断业务单据关联时 是否有相关内容
				Object code = baseDao.getFieldDataByCondition("projecttask", "preconditioncode", "id="+keyValue);
				if(code!=null){
					baseDao.callProcedure("SP_TASKINTERCEPTOR", new Object[]{code,keyValue});
					List<Map<String,Object>> maps=baseDao.queryForList("select tr_id,tr_keyid,tr_keycode,tr_class,tr_status,tr_remark,tr_render from taskrelation where tr_taskid="+keyValue);
					if(maps==null||maps.size()==0){
						BaseUtil.showError("未有关联业务数据,请关联后再进行提交!");
					}
				}
			}
			List<String> sqls = new ArrayList<String>();
			
			if("save".equals(processType)){ //只进行保存		
				return;			
			}

			if (taskpercentdone == 100){			
				taskUtilService.InsertintoRecordTime(keyValue, findstartdate.toString(), type.toString());
				taskUtilService.CompleteTask(keyValue, type.toString());		
			}else{
				sqls.add("UPDATE RESOURCEASSIGNMENT SET ra_laststartdate=to_date('"+rs.getString("wr_recorddate")+"','yyyy-mm-dd HH24:mi:ss')"
						+ ",ra_taskusehours='" + time
						+ "', ra_taskpercentdone=round('" + (alreadydone) + "',2) WHERE ra_id='" + rs.getInt("wr_raid") + "'");

				sqls.add("update projecttask set percentdone="+taskpercentdone+",usehours="+time+"  where id='" + keyValue + "'");				
			}
			//处理附件
			List<Map<String,Object>> data = baseDao.queryForList("select * from projecttaskattach where ptt_taskid=" + keyValue + " and ptt_flag=-1");
			if(data.size()>0){
				List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
				for(Map<String,Object> map:data){
					Map<String,Object> lowerMap = new HashMap<String,Object>();
					Set<String> set = map.keySet();
					for(String str:set){
						lowerMap.put(str.toLowerCase(), map.get(str));
						}
					Object docid=lowerMap.get("ptt_prjdocid");
					String filepath=lowerMap.get("ptt_filepath")==null?"":lowerMap.get("ptt_filepath").toString();
					//同个主项目内相同编号文件的docid
					List<Object> docids = baseDao.getFieldDatasByCondition("projectdoc", "pd_id", "pd_code=(select pd_code from projectdoc where pd_id="+docid+") and (pd_taskid <>"+keyValue+" or pd_taskid is null ) and  pd_prjid is not null and "
							+ "pd_prjid in(select prj_id from project where PRJ_MAINPROID="+prjid+" union  select PRJ_MAINPROID from project where prj_id="+prjid+" union  select "+prjid+" from dual union select prj_id from project where PRJ_MAINPROID=(select PRJ_MAINPROID from project where prj_id="+prjid+"))");
					if(docids.size()>0)
					docSync(docids,filepath,prjid,rs.getString("wr_recorder"),rs.getString("wr_recorddate"));
					list.add(lowerMap);
				}
				String gstore = BaseUtil.parseGridStore2Str(list);
				updateTaskFiles(gstore,rs.getString("wr_recorder"),rs.getString("wr_recorddate"));				
			}
			baseDao.execute(sqls);
			if (taskpercentdone == 100){
				taskUtilService.triggerTask(keyValue);
				//任务提交完成率为100 时判断
				commitPhase(keyValue);				
			}
			//消息模板配置
			Object mmid=baseDao.getFieldDataByCondition("MESSAGEMODEL left join MESSAGEROLE on mm_id=mr_mmid", "distinct mm_id", "MR_ISUSED=-1 AND MM_ISUSED=-1 and mm_caller='WorkRecord'");
				//调用生成消息的存储过程
			if (mmid != null) {
				SqlRowSet set = baseDao.getJdbcTemplate().queryForRowSet("select em_code from employee where em_name='"+rs.getString("wr_recorder")+"'");
				baseDao.callProcedure("SP_CREATEINFO",new Object[] { mmid,set.next()?set.getString("em_code"):"admin",id,DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) });
			}	
		}
		handlerService.beforeAudit(caller, id);
		baseDao.audit("workrecord", "wr_id="+id, "wr_status", "wr_statuscode");
		handlerService.afterAudit(caller, id);
		baseDao.logger.audit(caller, "wr_id", id);
		
	}
	
	//主项目内同编号文档同步
	private  void docSync(List<Object> ids,String filepath,int prjid,String employee,String time){		
		JSONObject obj=null;
		List<String> gridSql=new LinkedList<String>();
		String fpid = filepath.substring(filepath.indexOf(";")+1);
		String attach = "";
		String attachname="";
		int dlid = 0;
		Object path = null;
		Object size = null;
		Object style = null;
		JSONArray arr=formAttachService.getFiles(fpid);
		for (int i=0;i<arr.size();i++){	
			obj=arr.getJSONObject(i);
			path = obj.get("fp_path");
			size = obj.get("fp_size");
			attachname = String.valueOf(obj.get("fp_name"));
			style = attachname.substring(attachname.lastIndexOf(".")+1);		
			attach = "'" + attachname + "','" + path + "','" + size + "','" + String.valueOf(obj.get("fp_id"))+";','" + attachname.substring(attachname.lastIndexOf(".")+1) + "'";
		}
		for(Object id:ids){
			//判断是否是第一次上传
			boolean bool = baseDao.checkIf("documentlist", "nvl(dl_prjdocid,0)="+id);
			if(!bool){ //第一次上传
				dlid = baseDao.getSeqId("DOCUMENTLIST_SEQ");
				Object parentid = baseDao.getFieldDataByCondition("documentlist", "dl_id", "dl_prjdocid=(select pd_parentid from projectdoc where pd_id="+id+")");
				String insertdoc = "insert into documentlist(dl_id,dl_virtualpath,dl_createtime,dl_creator,dl_parentid,dl_kind,dl_status,dl_statuscode,dl_name,dl_filepath,dl_size,dl_fpid,dl_style,dl_prjdocid,dl_prjid) select "+dlid+",'/项目文档'||pd_virtualpath,to_date('"+time+"','yyyy-mm-dd HH24:mi:ss'),'" 
						+ employee + "'," + parentid + ",0,'已审核','AUDITED'," + attach + ","+id+","+prjid+" from projectdoc where pd_id=" + id;
				gridSql.add(insertdoc);
			}else{
				Object doclid = baseDao.getFieldDataByCondition("documentlist", "dl_id", "dl_prjdocid=" + id);
				dlid = Integer.parseInt(doclid.toString());
				String docUpdate = "update documentlist set dl_detno=dl_detno+1,dl_version=dl_version+1,dl_createtime=to_date('"+time+"','yyyy-mm-dd HH24:mi:ss'),dl_creator='" + employee + "',dl_name='" + attachname + "',dl_filepath='" + path + "',dl_size=" + size + ",dl_fpid='" + fpid + "',dl_style='" + style + "' where dl_id=" + doclid;
				gridSql.add(docUpdate);
			}
			//插入版本
			Object detno=baseDao.getFieldDataByCondition("DOCUMENTVERSION", "max(dv_detno)+1", "dv_dlid="+dlid);
			detno=detno==null?1:detno;
			int dvid=baseDao.getSeqId("DOCUMENTVERSION_SEQ");
				String versionsql = "INSERT INTO documentversion(dv_id,dv_dlid,dv_name,dv_filepath,dv_man,dv_explain,dv_detno,dv_size,dv_fpid) VALUES(" + dvid + "," + dlid + ",'"+attachname+"','"+path+"','" + employee + "','" + SystemSession.getLang() + "'," + detno + "," + size +",'" + fpid + "')";
			gridSql.add(versionsql);					
			//更新文档检查表
			String update = "update projectdoc set pd_checked=1,pd_filepath='"+filepath+"',pd_operator='"+employee+"',pd_operatime=to_date('"+time+"','yyyy-mm-dd HH24:mi:ss') where pd_id=" + id;		
			gridSql.add(update);				
		}
		baseDao.execute(gridSql);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void auditWorkRecord(int id,String caller){
		updateTask(id,"WorkRecord","audit");
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateWorkRecord(String formStore, String language) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "WorkRecord", "wr_id");
		// 更新ProjectTask 和 ResourceAssignment中完成率的数据
		StringBuffer sb1 = new StringBuffer("UPDATE PROJECTTASK SET ");
		formStore = formStore.substring(formStore.indexOf("{") + 1, formStore.lastIndexOf("}"));
		// String[] strs = formStore.split("\",\"");
		int keyValue = Integer.parseInt(store.get("wr_taskid").toString());
		int assignpercent = Integer.parseInt(store.get("wr_assignpercent").toString());
		double percentdone = NumberUtil.formatDouble(store.get("wr_percentdone").toString(),2);
		double taskpercentdone = NumberUtil.formatDouble(store.get("wr_taskpercentdone").toString(),2);
		int basepercentdone = Integer.parseInt(store.get("wr_basepercentdone").toString());
		percentdone = (assignpercent * percentdone) / 100 + taskpercentdone - basepercentdone * assignpercent / 100;
		sb1.append("percentdone");
		if (percentdone > 100 || percentdone == 100) {
			if (Integer.parseInt(store.get("wr_needattach").toString()) != 0) {
				List<FormAttach> attachs = formAttachDao.getFormAttachs("WorkRecord",
						Integer.parseInt(store.get("wr_id").toString()));
				if (attachs == null) {
					BaseUtil.showError("该任务需要交附件，请先上传附件");
				}
			}
			sb1.append("='" + 100 + "'");
			long start = getTime(store.get("wr_taskstartdate").toString());
			long end = getTime(store.get("wr_taskenddate").toString());
			long record = getTime(store.get("wr_recorddate").toString());
			sb1.append(" ,point");
			if (record > end) {
				float done1 = (float) 0;
				// 存在延时的情况且时间没有太超
				if (end == start) {
					done1 = NumberUtil.subFloat((float) ((record / 100000 - end / 100000) * 100 / 691.2), 1);

				} else
					done1 = NumberUtil.subFloat(
							(float) ((record / 100000 - end / 100000) * 100 / ((end / 100000 - start / 100000) * 0.8)),
							1);
				int done = (int) (done1 + 0.5);
				if (done < 100) {
					sb1.append("='" + (100 - done) + "'");
				} else
					sb1.append("='" + 0 + "'");
			} else {
				sb1.append("='" + 100 + "'");
			}
		} else {
			sb1.append("='" + percentdone + "'");
		}
		String sql = sb1.substring(0, sb1.length()) + "  WHERE id='" + keyValue + "'";
		String sqlupdate = "UPDATE RESOURCEASSIGNMENT SET ra_taskpercentdone='" + percentdone + "' WHERE ra_taskid='"
				+ keyValue + "'";
		List<String> sqls = new ArrayList<String>();
		sqls.add(sql);
		sqls.add(sqlupdate);
		sqls.add(formSql);
		baseDao.execute(sqls);
	}

	private long getTime(String str) {
		try {
			return formatter.parse(str).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return new Date().getTime();
		}
	}

	@Override
	public List<FormAttach> getFormAttachs(String condition) {
		SqlRowList rs = baseDao.queryForRowSet("SELECT ra_taskid from ResourceAssignment left join workrecord on wr_raid=ra_id where " + condition);
		if (rs.next()) {
			return formAttachDao.getFormAttachs("ProjectTask", rs.getInt("ra_taskid"));
		} else
			return null;
	}

	@Override
	public List<JSONTree> getJSONResource(int id, String language) {
		return recordDao.getJSONResource(id);
	}

	@Override
	public List<JSONTree> getJSONRecord(String condition, String language) {
		// TODO Auto-generated method stub
		return recordDao.getJSONRecord(condition);
	}

	public float getTime(String start, String end) {
		Date startDate = DateUtil.parseStringToDate(start, Constant.YMD_HMS);
		Date endDate = DateUtil.parseStringToDate(end, Constant.YMD_HMS);
		Calendar c = Calendar.getInstance();
		int result = 0;
		for (long begin = startDate.getTime(); begin <= endDate.getTime(); begin += 86400000) {
			c.setTimeInMillis(begin);
			if (c.get(Calendar.DAY_OF_WEEK) != 1 && c.get(Calendar.DAY_OF_WEEK) != 7) {
				result++;
			}
		}
		float minite = 0;
		if (result == 0) {
			minite = (endDate.getTime() - startDate.getTime()) / 60000;
			return NumberUtil.subFloat((float) ((float) minite / 60), 2);
		} else {
			minite = NumberUtil.subFloat(
					(float) (endDate.getTime() - 86400000 * (result - 1) - startDate.getTime()) / 60000, 2);
			// 精确到分保留两位小数 四舍五入
			return (result - 1) * 8 + NumberUtil.subFloat((float) ((float) minite / 60 + 0.005), 2);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void submitWorkRecord(int id, String language, Employee employee) {
		// TODO Auto-generated method stub
		// 提交完成反提交的任务
		Object basestartdate = baseDao.getFieldDataByCondition("ResourceAssignment", "ra_basestartdate", "ra_taskid="
				+ id + " AND ra_emid=" + employee.getEm_id());
		taskUtilService.InsertintoRecordTime(id, basestartdate.toString().substring(0, 19), "RESSUBMITSTART");
		taskUtilService.CompleteTask(id, "SUBMIT");
		taskUtilService.resStartTask(id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void resSubmitWorkRecord(int id, String language, Employee employee) {
		// 反提交任务
		/*SqlRowList sl = baseDao.queryForRowSet("select pretaskdetno,prjplanid from projectTask where ID=" + id);
		baseDao.updateByCondition("ResourceAssignment", "ra_statuscode='RESSUBMITSTOP',ra_status='反提交停止'", "ra_taskid="
				+ id);
		// taskUtilService.stopTask(id,language);
		if (sl.next()) {
			Object predetno = sl.getObject(1);
			if (predetno != null) {
				String condition = "detno in ('" + predetno.toString().trim().replaceAll(",", "','") + "')";
				SqlRowList startsl = baseDao.queryForRowSet("select ID from ProjectTask where prjplanid="
						+ sl.getObject(2) + " AND " + condition);
				while (startsl.next()) {
					// startTask(startsl.getInt(1), language);
					baseDao.updateByCondition("ProjectTask",
							"handstatus='反提交激活',handstatuscode='RESACTIVE',remark='" + employee.getEm_name()
							+ "|反提交'", "ID=" + startsl.getInt(1));
					baseDao.updateByCondition("resourceAssignment",
							"ra_worktype='resSubmit',ra_statuscode='RESACTIVE',ra_status='反提交激活',ra_basestartdate="
									+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()), "ra_taskid="
											+ startsl.getInt(1));
				}
			}
		}*/
		
		handlerService.beforeResSubmit("WorkRecord", id);
		baseDao.execute("delete workrecord where wr_id=" + id);
		handlerService.afterResSubmit("WorkRecord", id);
		baseDao.logger.resSubmit("WorkRecord", "wr_id", id);
	}

	@Override
	public String getRecordData(int id, Employee employee, String language) {
		return baseDao.getDataStringByDetailGrid(
				detailGridDao.getDetailGridsByCaller("TaskRecordTime", SpObserver.getSp()), "tr_taskid=" + id, null, null);

	}

	@Override
	public void updateBillRecord(Integer wr_raid, String wr_redcord, String language, Employee employee) {
		List<String> sqls = new ArrayList<String>();
		sqls.add("insert into workrecord(wr_id,wr_raid,wr_redcord,wr_recorder,wr_recorderemid,wr_recorddate,wr_status,wr_statuscode,wr_taskpercentdone,wr_percentdone) values(workrecord_seq.nextval,"
				+ wr_raid+ ",'"+ wr_redcord+ "','"+ employee.getEm_name()+ "',"+ employee.getEm_id()
				+ ","+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+",'" + BaseUtil.getLocalMessage("AUDITED", language) + "','AUDITED',100,100)");
		sqls.add("update workrecord set (wr_taskid,wr_taskname,wr_taskstartdate,wr_taskenddate)=(select ra_taskid,ra_taskname,ra_startdate,ra_enddate from resourceassignment where ra_id=wr_raid) where wr_raid="
				+ wr_raid);
		baseDao.execute(sqls);
	}

	@Override
	public void endBillTask(Integer ra_id, Integer taskId, String record,String language, Employee employee) {
		List<String> sqls = new ArrayList<String>();
		if (taskId == null || taskId == 0) {
			taskId = baseDao.getJdbcTemplate().queryForObject("select ra_taskid from resourceassignment where ra_id=?",
					Integer.class, ra_id);
		}
		if (record==null || record.equals("")) BaseUtil.showError("回复信息不能为空！");
		if(baseDao.checkIf("ProjectTask", "id="+taskId+" and class='agendatask' and sourcelink is not null and prjplanid is not null")){
			BaseUtil.showError("当前任务关联项目推广计划不能直接结束，需完成相应的拜访报告才能结束!");
		}
		sqls.add("insert into workrecord(wr_id,wr_redcord,wr_recorddate,wr_status,wr_statuscode,wr_taskpercentdone,wr_percentdone,wr_raid,wr_recorder,wr_recorderemid,wr_taskid,Wr_Progress) select workrecord_seq.nextval,'"+record+"',"+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+",'"
				+ BaseUtil.getLocalMessage("AUDITED", language)
				+ "','AUDITED',100,100,ra_id,ra_resourcename,ra_emid,ra_taskid,'reply' from resourceassignment where ra_taskid="
				+ taskId);
		int tasktype=baseDao.getFieldValue("ProjectTask", "type", "id="+taskId, Integer.class);
		//tasktype=1   需要提出人确认 否则不需要
		if(tasktype==0){
			sqls.add("update resourceassignment set ra_taskpercentdone=100,ra_status='已完成',ra_statuscode='FINISHED',ra_enddate="+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+" where ra_taskid="
					+ taskId);
			sqls.add("update ProjectTask set handstatus='已完成',handstatuscode='FINISHED',usehours=round((sysdate-startdate)*24,2),percentdone=100 where id="
					+ taskId);
		}else {
			sqls.add("update resourceassignment set ra_status='待确认',ra_statuscode='UNCONFIRMED',ra_enddate="+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+" where ra_taskid="
					+ taskId);
			sqls.add("update ProjectTask set handstatus='待确认',handstatuscode='UNCONFIRMED',usehours=round((sysdate-startdate)*24,2),percentdone=100 where id="
					+ taskId);
		}
		baseDao.execute(sqls);
		//如果任务不需确认，完成后给发起人发消息
		if(tasktype==0){
			SqlRowList rs = baseDao.queryForRowSet(
					"select * from ProjectTask left join employee on recorder=em_name where id=?", taskId);
			if (rs.next()) {
				StringBuffer sb = new StringBuffer();
				sb.append(employee.getEm_name()+"已完成");
				sb.append("<a style=\"color:blue\" href=\"javascript:openUrl(''jsps/plm/record/billrecord.jsp?formCondition=idIS");
				sb.append(taskId);
				sb.append("&gridCondition=ra_taskidIS");
				sb.append(taskId);
				sb.append("'') \">"+rs.getGeneralString("name")+"</a>任务<br>");			
				sb.append(record);
				int pr_id = baseDao.getSeqId("pagingrelease_seq");
				baseDao.execute("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,pr_from)values('"
						+ pr_id + "','" + employee.getEm_name() + "',"+DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())+",'" + employee.getEm_id() + "','"
						+ sb.toString() + "','task')");
				baseDao.execute("insert into pagingreleasedetail(prd_id,prd_prid,prd_recipientid,prd_recipient) values(pagingreleasedetail_seq.nextval"
						+ "," + pr_id + "," + rs.getGeneralInt("em_id") + ",'" + rs.getGeneralString("recorder") + "')");						
				//保存到历史消息表
				int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
				baseDao.execute("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
						+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
						+ " where pr_id="+pr_id);
				baseDao.execute("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
						+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+"and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");
			}
		}
	}

	@Override
	public void changeBillTask(Integer ra_id, Integer em_id, String language, Employee employee) {
		Object[] objs=baseDao.getFieldsDataByCondition("resourceassignment", new String []{"ra_taskId","ra_emid","ra_resourcecode","ra_resourcename"},"ra_id="+ra_id);
		if(objs!=null && objs[0]!=null && objs[1]!=null && objs[2]!=null && objs[3]!=null){
			if (Integer.parseInt(objs[1].toString())==em_id && !objs[3].equals("") ) {
				BaseUtil.showError("该任务录入时已经分配给了【" + objs[3] + "】,您无需变更.<br>在【" + objs[3] + "】处理完该任务后，会自动结束您的任务.");
			}
			Object[] toem=baseDao.getFieldsDataByCondition("employee", new String []{"em_code","em_name"},"em_id="+em_id +" and nvl(em_class,' ')<>'离职'");
			if (toem!=null && toem[0]!=null && toem[1]!=null){
			baseDao.execute("update resourceassignment set ra_emid=" + em_id
					+ ",ra_resourcecode='"+toem[0]+"',ra_resourcename='"+toem[1]+"' where ra_id=" + ra_id);		
			Object[] tasks= baseDao.getFieldsDataByCondition("ProjectTask",new String[]{"name","recorderid","recorder"},"id="+objs[0]);
			if (tasks!=null && tasks[0]!=null && tasks[1]!=null && tasks[2]!=null) {
				baseDao.execute("insert into workrecord(wr_id,wr_raid,wr_redcord,wr_recorder,wr_recorderemid,wr_recorddate,wr_status,wr_statuscode,wr_taskpercentdone,wr_percentdone) values(workrecord_seq.nextval,"
						+ ra_id
						+ ",'【"
						+ employee.getEm_name()
						+ "】将任务委托给【"
						+ toem[1]
						+ "】','"
						+ employee.getEm_name()
						+ "',"
						+ employee.getEm_id()
						+ ","+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+",'"
						+ BaseUtil.getLocalMessage("AUDITED", language)
						+ "','AUDITED',100,100)");
				
				int pr_id = baseDao.getSeqId("pagingrelease_seq");
				
				//任务执行人变更任务时给发起人发消息
				if(objs[1].toString().equals(employee.getEm_id().toString()) && !tasks[1].toString().equals(employee.getEm_id().toString())){
				baseDao.execute("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,pr_from)values('"
						+ pr_id + "','" + employee.getEm_name() + "',"+DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())+",'" + employee.getEm_id() + "','"
						+ employee.getEm_name()+"将您发起的任务"+tasks[0]+"转交给"+toem[1]+"处理！','task')");
				baseDao.execute("insert into pagingreleasedetail(prd_id,prd_prid,prd_recipientid,prd_recipient) values(pagingreleasedetail_seq.nextval"
						+ "," + pr_id + "," +tasks[1] + ",'" + tasks[2]+ "')");}
				// 任务发起人变更任务时给上一次执行人发消息
				else if (tasks[1].toString().equals(employee.getEm_id().toString()) && !objs[1].toString().equals(employee.getEm_id().toString())){
				baseDao.execute("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,pr_from)values('"
							+ pr_id + "','" + employee.getEm_name() + "',"+DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())+",'" + employee.getEm_id() + "','"
							+ employee.getEm_name()+"将发给您的任务"+tasks[0]+"转交给"+toem[1]+"处理！','task')");
				baseDao.execute("insert into pagingreleasedetail(prd_id,prd_prid,prd_recipientid,prd_recipient) values(pagingreleasedetail_seq.nextval"
							+ "," + pr_id + "," +objs[1] + ",'" + objs[3]+ "')");}
				//保存到历史消息表
				int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
				baseDao.execute("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
						+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
						+ " where pr_id="+pr_id);
				baseDao.execute("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
						+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+"and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");

			}
		 }
		}
	}

	@Override
	public void confirmBillTask(Integer ra_id, Integer taskId, String record,String language, Employee employee) {
		// TODO Auto-generated method stub
		List<String> sqls = new ArrayList<String>();
		if (taskId == null || taskId == 0) {
			taskId = baseDao.getJdbcTemplate().queryForObject("select ra_taskid from resourceassignment where ra_id=?",
					Integer.class, ra_id);
		}
		if (record==null || record.equals("")) BaseUtil.showError("回复信息不能为空！");
		sqls.add("insert into workrecord(wr_id,wr_redcord,wr_recorddate,wr_status,wr_statuscode,wr_taskpercentdone,wr_percentdone,wr_raid,wr_recorder,wr_recorderemid,wr_taskid,wr_progress) select workrecord_seq.nextval,'"+record+"',"+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+",'"
				+ BaseUtil.getLocalMessage("AUDITED", language)
				+ "','AUDITED',100,100,ra_id,'"+employee.getEm_name()+"','"+employee.getEm_id()+"',ra_taskid,'confirm' from resourceassignment where ra_taskid="
				+ taskId);
		sqls.add("update resourceassignment set ra_taskpercentdone=100,ra_status='已完成',ra_statuscode='FINISHED',ra_enddate=sysdate where ra_taskid="
				+ taskId);
		sqls.add("update ProjectTask set handstatus='已完成',handstatuscode='FINISHED',usehours=round((sysdate-startdate)*24,2),percentdone=100,realenddate=sysdate where id="
				+ taskId);
		SqlRowList rs = baseDao.queryForRowSet(
				"select ra_emid,ra_resourcename,ra_taskname from resourceassignment  where ra_id=?", ra_id);
		if (rs.next()) {
			StringBuffer sb = new StringBuffer();
			sb.append("任务<a style=\"color:blue\" href=\"javascript:openUrl(''jsps/plm/record/billrecord.jsp?formCondition=idIS");
			sb.append(taskId);
			sb.append("&gridCondition=ra_taskidIS");
			sb.append(taskId);
			sb.append("'')\">");
			sb.append(rs.getGeneralString("ra_taskname"));
			sb.append("</a>");
			sb.append("&nbsp;&nbsp;于");
			sb.append(DateUtil.format(null, "MM-dd HH:mm"));
			sb.append("由【");
			sb.append(employee.getEm_name());
			sb.append("】确认完成 ");			
			int pr_id = baseDao.getSeqId("pagingrelease_seq");
			baseDao.execute("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,pr_from)values('"
					+ pr_id + "','" + employee.getEm_name() + "',"+DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())+",'" + employee.getEm_id() + "','"
					+ sb.toString() + "','task')");
			baseDao.execute("insert into pagingreleasedetail(prd_id,prd_prid,prd_recipientid,prd_recipient) values(pagingreleasedetail_seq.nextval"
					+ "," + pr_id + "," + rs.getGeneralInt("ra_emid") + ",'" + rs.getGeneralString("ra_resourcename") + "')");
			//保存到历史消息表
			int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
			baseDao.execute("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
					+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
					+ " where pr_id="+pr_id);
			baseDao.execute("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
					+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+"and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");
		}
		baseDao.execute(sqls);	
	}

	@Override
	public void noConfirmBillTask(Integer ra_id, Integer taskId, String record,String language, Employee employee) {
		// TODO Auto-generated method stub
		List<String> sqls = new ArrayList<String>();
		if (taskId == null || taskId == 0) {
			taskId = baseDao.getJdbcTemplate().queryForObject("select ra_taskid from resourceassignment where ra_id=?",
					Integer.class, ra_id);
		}
		if (record==null || record.equals("")) BaseUtil.showError("回复信息不能为空！");
		sqls.add("insert into workrecord(wr_id,wr_redcord,wr_recorddate,wr_status,wr_statuscode,wr_taskpercentdone,wr_percentdone,wr_raid,wr_recorder,wr_recorderemid,wr_taskid,Wr_Progress) select workrecord_seq.nextval,'"+record+"',"+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+",'"
				+ BaseUtil.getLocalMessage("AUDITED", language)
				+ "','AUDITED',100,100,ra_id,'"+employee.getEm_name()+"','"+employee.getEm_id()+"',ra_taskid,'noconfirm' from resourceassignment where ra_taskid="
				+ taskId);
		sqls.add("update resourceassignment set ra_taskpercentdone=0,ra_status='已启动',ra_statuscode='START',ra_enddate="+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+" where ra_taskid="
				+ taskId);
		sqls.add("update ProjectTask set handstatus='已审核',handstatuscode='AUDITED',usehours=round((sysdate-startdate)*24,2),percentdone=100 where id="
				+ taskId);
		baseDao.execute(sqls);
		SqlRowList rs = baseDao.queryForRowSet("select ra_emid,ra_resourcename,ra_taskname from resourceassignment  where ra_id=?", ra_id);
		if (rs.next()) {
			StringBuffer sb = new StringBuffer();
			sb.append(employee.getEm_name()+"驳回了");
			sb.append("<a style=\"color:blue\" href=\"javascript:openUrl(''jsps/plm/record/billrecord.jsp?formCondition=idIS");
			sb.append(taskId);
			sb.append("&gridCondition=ra_taskidIS");
			sb.append(taskId);
			sb.append("'')\">");
			sb.append(rs.getGeneralString("ra_taskname"));			
			sb.append("</a>");
			sb.append("任务");
			int pr_id = baseDao.getSeqId("pagingrelease_seq");
			baseDao.execute("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context,pr_from)values('"
					+ pr_id + "','" + employee.getEm_name() + "',"+DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())+",'" + employee.getEm_id() + "','"+ sb.toString()+"','task')");
			baseDao.execute("insert into pagingreleasedetail(prd_id,prd_prid,prd_recipientid,prd_recipient) values(pagingreleasedetail_seq.nextval"
					+ "," + pr_id + "," + rs.getGeneralInt("ra_emid") + ",'" + rs.getGeneralString("ra_resourcename") + "')");
			//保存到历史消息表
			int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
			baseDao.execute("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
					+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
					+ " where pr_id="+pr_id);
			baseDao.execute("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
					+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+"and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");

		}		
	}
    
	@Override
	public List<Map<String, Object>> getMsg(Integer ra_id) {
		// TODO Auto-generated method stub
		//String sql="select WR_TASKID,WR_REDCORD 内容,WR_RECORDER 录入人,to_char(WR_RECORDDATE,'yyyy-mm-dd hh24:mi:ss') 时间,WR_RECORDEREMID 录入人ID,WR_PROGRESS from workrecord where WR_TASKID="+ra_id+"";
		String sql="select WR_REDCORD 内容,WR_RECORDER 录入人,to_char(WR_RECORDDATE,'yyyy-mm-dd hh24:mi:ss') 时间  from workrecord where WR_TASKID="+ra_id+"";
		return baseDao.getJdbcTemplate().queryForList(sql);
	}
	private void commitPhase(int taskId){
		try {
			Object phaseId=baseDao.getFieldDataByCondition("projecttask", "phaseid", "id="+taskId);
			if(phaseId!=null && Integer.parseInt(String.valueOf(phaseId))!=0){
				//判断前面阶段是否已完成
				int prjId=baseDao.getFieldValue("ProjectPhase", "pp_prjid", "pp_id="+phaseId, Integer.class);
				//boolean bool=baseDao.checkByCondition("ProjectPhase"," nvl(pp_status,' ')<>'已完成' and  pp_detno < (select pp_detno from ProjectPhase  where pp_id="+phaseId+") and  pp_prjid="+prjId);
				//if(bool){
				baseDao.updateByCondition("ProjectPhase", "pp_status='已完成',pp_realenddate=sysdate", "pp_id="+phaseId);				
				Object detno=baseDao.getFieldDataByCondition("ProjectPhase","min(pp_detno)", "pp_detno>(select pp_detno from ProjectPhase where pp_id="+phaseId+") and  pp_prjid="+prjId);
				baseDao.updateByCondition("ProjectPhase","pp_status='进行中',pp_realstartdate=sysdate", "pp_detno="+detno+" and pp_prjid="+prjId+" and nvl(pp_status,' ')<>'已完成'");
				baseDao.execute("update project set prj_phase =(select pp_phase from ProjectPhase where pp_detno="+detno+" and pp_prjid="+prjId+") where  prj_id="+prjId);
				//}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}	
	}
	
	@Override
	public Map<String,Object> getTaskFiles(Integer id) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		Map<String,Object> map = null;
		if(id!=null){		
			List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
			List<Object[]> files = baseDao.getFieldsDatasByCondition("projecttaskattach", new String[]{"ptt_filename","ptt_filepath","ptt_id"}, "ptt_taskid=" + id);
			if(files.size()>0){
				for(Object[] file:files){
					map = new HashMap<String,Object>();
					map.put("ptt_filename", file[0]);
					map.put("ptt_filepath", file[1]);
					map.put("ptt_id", file[2]);
					datas.add(map);
				}
			}
			modelMap.put("files", datas);
		}
		return modelMap;
	}
	

	public void updateTaskFiles(String gstore,String employee,String time){
		List<Map<Object, Object>> gstoreList = BaseUtil.parseGridStoreToMaps(gstore);

		List<String> gridSql = new ArrayList<String>();;
		
		Object taskid = null;
		//更新文件检查表
		for(Map<Object, Object> map:gstoreList){
			taskid = baseDao.getFieldDataByCondition("projecttaskattach", "ptt_taskid", "ptt_id=" + map.get("ptt_id"));
			//更新到文档管理
			JSONObject obj=null;
			String filepath = map.get("ptt_filepath").toString();
			String fpid = filepath.substring(filepath.indexOf(";")+1);
			String attach = "";
			String attachname="";
			int dlid = 0;
			Object path = null;
			Object size = null;
			Object style = null;
			JSONArray arr=formAttachService.getFiles(fpid);
			for (int i=0;i<arr.size();i++){	
				obj=arr.getJSONObject(i);
				path = obj.get("fp_path");
				size = obj.get("fp_size");
				attachname = String.valueOf(obj.get("fp_name"));
				style = attachname.substring(attachname.lastIndexOf(".")+1);		
				attach = "'" + attachname + "','" + path + "','" + size + "','" + String.valueOf(obj.get("fp_id"))+";','" + attachname.substring(attachname.lastIndexOf(".")+1) + "'";
			}
			Object[] docid = baseDao.getFieldsDataByCondition("projecttaskattach left join projectdoc on pd_id=ptt_prjdocid", new String[]{"ptt_prjdocid","pd_prjid"}, "ptt_id=" + map.get("ptt_id"));
			//判断是否是第一次上传
			boolean bool = baseDao.checkIf("documentlist", "nvl(dl_prjdocid,0)="+docid[0]);
			if(!bool){ //第一次上传
				dlid = baseDao.getSeqId("DOCUMENTLIST_SEQ");
				Object parentid = baseDao.getFieldDataByCondition("documentlist", "dl_id", "dl_prjdocid=(select pd_parentid from projectdoc where pd_id="+docid[0]+")");
				String insertdoc = "insert into documentlist(dl_id,dl_virtualpath,dl_createtime,dl_creator,dl_parentid,dl_kind,dl_status,dl_statuscode,dl_name,dl_filepath,dl_size,dl_fpid,dl_style,dl_prjdocid,dl_prjid) select "+dlid+",'/项目文档'||pd_virtualpath,to_date('"+time+"','yyyy-mm-dd HH24:mi:ss'),'" 
						+ employee + "'," + parentid + ",0,'已审核','AUDITED'," + attach + ","+docid[0]+","+docid[1]+" from projectdoc where pd_id=" + docid[0];
				gridSql.add(insertdoc);
			}else{
				Object doclid = baseDao.getFieldDataByCondition("documentlist", "dl_id", "dl_prjdocid=" + docid[0]);
				dlid = Integer.parseInt(doclid.toString());
				String docUpdate = "update documentlist set dl_detno=dl_detno+1,dl_version=dl_version+1,dl_createtime=to_date('"+time+"','yyyy-mm-dd HH24:mi:ss'),dl_creator='" + employee + "',dl_name='" + attachname + "',dl_filepath='" + path + "',dl_size=" + size + ",dl_fpid='" + fpid + "',dl_style='" + style + "' where dl_id=" + doclid;
				gridSql.add(docUpdate);
			}
			//插入版本
			Object detno=baseDao.getFieldDataByCondition("DOCUMENTVERSION", "max(dv_detno)+1", "dv_dlid="+dlid);
			detno=detno==null?1:detno;
			int dvid=baseDao.getSeqId("DOCUMENTVERSION_SEQ");
				String versionsql = "INSERT INTO documentversion(dv_id,dv_dlid,dv_name,dv_filepath,dv_man,dv_explain,dv_detno,dv_size,dv_fpid) VALUES(" + dvid + "," + dlid + ",'"+attachname+"','"+path+"','" + employee + "','" + SystemSession.getLang() + "'," + detno + "," + size +",'" + fpid + "')";
			gridSql.add(versionsql);
					
			//更新文档检查表
			String update = "update projectdoc set pd_checked=1,pd_filepath='"+map.get("ptt_filepath")+"',pd_taskname=(select name from projecttask where id=(select ptt_taskid from projecttaskattach where ptt_id="+map.get("ptt_id")+")),pd_operator='"+employee+"',pd_operatime=to_date('"+time+"','yyyy-mm-dd HH24:mi:ss') where pd_id=" + docid[0];		
			gridSql.add(update);
			
			//还原状态
			gridSql.add("update projecttaskattach set ptt_flag=0 where ptt_id="+map.get("ptt_id"));
			
		}	
		baseDao.execute(gridSql);
		//更新projecttask文件上传状态
		Object docids = baseDao.getFieldDataByCondition("projecttask", "prjdocid", "id=" + taskid);
		if(docids!=null){
			String[] docidstr = docids.toString().split(",");
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<docidstr.length;i++){
				sb.append("," + docidstr[i] + "," + (i+1));
			}
			Object docstatus = baseDao.getFieldDataByCondition("projectdoc", "wm_concat(nvl(pd_checked,0))", "pd_taskid="+taskid+" and pd_id in ("+docids+") order by decode(pd_id"+sb.toString()+")");
			String ptaskUp = "update projecttask set prjdocstatus='"+docstatus+"' where id=" + taskid;
			baseDao.execute(ptaskUp);
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void submitWorkRecordFlow(int id){
		boolean autoFlow = baseDao.checkIf("form", "fo_caller='WorkRecord' and fo_isautoflow=-1");	
		//触发审批流
		if(autoFlow){
			handlerService.beforeSubmit("WorkRecord", id);
			baseDao.submit("WorkRecord", "wr_id=" + id, "wr_status", "wr_statuscode");
			//记录操作
			baseDao.logger.submit("WorkRecord", "wr_id", id);
			handlerService.afterSubmit("WorkRecord", id);
		}		
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void taskTransfer(String ids,String from,String to){
		String out = baseDao.callProcedure("SP_TASKTRANSFER", new Object[]{ids,from,to});
		if(out!=null){
			BaseUtil.showError(out);
		}
	}

	@Override
	public List<Map<String, Object>> loadRelationData(String id) {
		List<Map<String,Object>> maps=new LinkedList<Map<String,Object>>();
		Object code = baseDao.getFieldDataByCondition("projecttask", "preconditioncode", "id="+id);
		if(code!=null&&!"".equals(code)){
			baseDao.callProcedure("SP_TASKINTERCEPTOR", new Object[]{code,id});
			maps=baseDao.queryForList("select tr_id,tr_keyid,tr_keycode,tr_class,tr_status,tr_remark,tr_render from taskrelation where tr_taskid="+id);
		}
		return maps;
	}
}
