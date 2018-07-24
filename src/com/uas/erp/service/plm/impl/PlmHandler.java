package com.uas.erp.service.plm.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
@Service("PlmHandler")
public class PlmHandler {
	@Autowired
	private BaseDao baseDao;
	
	public void workRecord_commit_before_checkhours(HashMap<Object, Object> store) {
		if(store.get("wr_hours")!=null){	
			Object date=store.get("wr_recorddate")!=null?String.valueOf(store.get("wr_recorddate")).substring(0, 10):DateUtil.parseDateToString(new Date(), Constant.YMD);
			double wrhours =Double.parseDouble(store.get("wr_hours").toString());//更改为double类型
			double alreadytime=NumberUtil.formatDouble(baseDao.getSummaryByField("WorkRecord", "wr_hours", "wr_recorderemid="+store.get("wr_recorderemid")+" and to_char(WR_RECORDDATE,'yyyy-mm-dd')='"+date+"'"),1);
			double WorkingHours = NumberUtil.formatDouble(baseDao.getDBSetting("WorkRecord", "WorkingHours").toString(),2);
			if(wrhours+alreadytime>WorkingHours){
				BaseUtil.showError("工作报告每日累计时数不能超过:"+WorkingHours +"小时");
			}
		}
	}
  	public void taskchange_audit_updateParentTask(Integer id,String lauguage){
	  	SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Map<String, Object> task=baseDao.getJdbcTemplate().queryForMap("select startdate,enddate,parentid  from ProjectTask where id=?",new Object[]{id});
		if(Integer.parseInt(task.get("parentid").toString())!=0){
			StringBuffer sb=new StringBuffer();
		    Map<String,Object> parenttask=baseDao.getJdbcTemplate().queryForMap("select startdate,enddate,parentid from ProjectTask where id=?",new Object[]{task.get("parentid")});
			//如果开始时间和结束时间与 父任务的开始时间 结束时间有出入 则需要更新相应的任务
		    try {
				Date start1=format.parse(task.get("startdate").toString());
				Date start2=format.parse(parenttask.get("startdate").toString());
				Date end1=format.parse(task.get("enddate").toString());
				Date end2=format.parse(parenttask.get("enddate").toString());
				if(start1.compareTo(start2)<0){
					sb.append("startdate=to_date('"+task.get("startdate").toString()+"','yyyy-MM-dd'),");
				}else if(end1.compareTo(end2)>0){
					sb.append("enddate=to_date('"+task.get("enddate").toString()+"','yyyy-MM-dd'),");
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		     if(sb.length()>0){
		    	 String updatestr=sb.toString().substring(0, sb.toString().lastIndexOf(",")-1);
		    	 int findid=Integer.parseInt(parenttask.get("id").toString());
		    	 baseDao.execute("UPDATE ProjectTask set "+ updatestr+"  where id="+parenttask.get("id"));
		    	 if((Integer)parenttask.get("parentid")!=0){
		    		 taskchange_audit_updateParentTask(findid,lauguage);
		    	 }
		     }
		}    
  	} 
  	/**
  	 * 费用申请单明细删除前
  	 */
  	public void projectFeePlease_deletedetail(Integer id, String language){
  		Object[] pfd = baseDao.getFieldsDataByCondition("ProjectFeePleaseDetail", new String[]{"pfd_amount", "pfd_pfid"}, "pfd_id=" + id);
  		baseDao.updateByCondition("ProjectFeePlease", "pf_amount=pf_amount-" + pfd[0], "pf_id=" + pfd[1]);
  	}
  	/**
  	 *  项目评估表提交前 评估金额不能小参考金额
  	 *  pe_amount 评估金额
  	 *  pe_redamount 参考金额
  	 */
  	public void projectEvaluation_submit_checkAmount(Integer id){
  		int count=baseDao.getCount("select count(1) from ProjectEvaluation where nvl(pe_amount,0)<nvl(pe_refamount,0) "
  				+ "and pe_id="+id);
		if(count>0){
			BaseUtil.showError("评估金额不能小于参考金额");
		}
  	}
  	/**
  	 * 项目评审提交时，里程碑内容必须填写
  	 * 里程碑的结束时间不能早于开始时间，下一个阶段的开始时间不能早于上阶段的结束时间！
  	 * @throws ParseException 
  	 */
  	public void projectReview_submit_checkMilepost(Integer id) throws ParseException{
		int countnum = baseDao
				.getCount("select count(*) from projectphase where pp_prid="+ id);
		if (countnum == 0) {
			BaseUtil.showError("必须填写里程碑里的项目阶段！");
		}else{
			//判断同一阶段结束时间不能早于开始时间
	  		int countnum1 = baseDao
					.getCount("select count(*) from projectphase left join projectreview on pp_prid=pr_id where pp_enddate-pp_startdate<0 and pp_prid="
							+ id);
			if (countnum1 > 0) {
				BaseUtil.showError("结束时间比开始时间早,不允许提交操作！");
			}
			//判断下一阶段的开始时间不能早于上阶段的结束时间
			String startdate="";
			String enddate="";
			String datestart="";
			String dateend="";
			int detno1=0;
			int detno2=0;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
			SqlRowList rs = baseDao.queryForRowSet("select pp_detno,to_char(pp_startdate,'yyyy-mm-dd') pp_startdate,to_char(pp_enddate,'yyyy-mm-dd') pp_enddate from PROJECTPHASE where pp_prid="+ id+"order by pp_detno");
			while (rs.next()) {
				detno1=rs.getInt("pp_detno");
				datestart=rs.getString("pp_startdate");
				dateend=rs.getString("pp_enddate");
				if(!"".equals(startdate)&&sdf.parse(datestart).before(sdf.parse(enddate))){
					BaseUtil.showError("行号"+detno1+"的起始日期不能早于行号"+detno2+"的结束日期！");
				}
				startdate=datestart;
				enddate=dateend;
				detno2=detno1;
			}
		}
	}
  	/**
  	 * 物料认定单审核后，需要把审核时间更新到物料资料中（承认日期），供应商编号供应商名称也要带到物料资料中（主供应商），还有生产厂型号带到物料资料中的供应商物料号
  	 *  PA_AUDITDATE审核时间->PR_SQRQ承认日期
  	 *  PA_PROVIDECODE供应商编号->PR_MAINVENDCODE主供应商 
  	 *  PA_PROVIDE供应商名->PR_VENDNAME供应商名
  	 *  PA_PROVIDEPRODCODE生产厂型号->PR_VENDPRODCODE供应商物料号
  	 * @throws ParseException 
  	 */
  	public void ProductApproval_audit_updateProduct(Integer id) {
  		String pa_prodcode=(String)baseDao.getFieldDataByCondition("productapproval", "pa_prodcode", "pa_id='"+id+"'");
  		String sql="update product a set (pr_sqrq,pr_mainvendcode,pr_vendname,PR_VENDPRODCODE)=(select b.pa_auditdate,b.pa_providecode,b.pa_provide,b.pa_provideprodcode from productapproval b where b.pa_prodcode='"+pa_prodcode+"') where a.pr_code='"+pa_prodcode+"'";
  		baseDao.execute(sql);
  	}
 
	/**
  	 * 项目申请单提交时，项目阶段内容必须填写
  	 * 项目阶段的结束时间不能早于开始时间，下一个阶段的开始时间不能早于上阶段的结束时间！
  	 * @throws ParseException 
  	 */
  	public void projectRequest_submit_checkProjectphase(Integer id) throws ParseException{
		int countnum = baseDao
				.getCount("select count(*) from projectphase where pp_prjid="+ id);
		if (countnum == 0) {
			BaseUtil.showError("必须填写项目阶段！");
		}else{
			//判断同一阶段结束时间不能早于开始时间
	  		int countnum1 = baseDao
					.getCount("select count(*) from projectphase left join project on pp_prjid=prj_id where pp_enddate-pp_startdate<0 and pp_prjid="
							+ id);
			if (countnum1 > 0) {
				BaseUtil.showError("结束时间比开始时间早,不允许提交操作！");
			}
			//判断下一阶段的开始时间不能早于上阶段的结束时间
			String startdate="";
			String enddate="";
			String datestart="";
			String dateend="";
			int detno1=0;
			int detno2=0;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
			SqlRowList rs = baseDao.queryForRowSet("select pp_detno,to_char(pp_startdate,'yyyy-mm-dd') pp_startdate,to_char(pp_enddate,'yyyy-mm-dd') pp_enddate from projectphase where pp_prjid="+ id+"order by pp_detno");
			while (rs.next()) {
				detno1=rs.getInt("pp_detno");
				datestart=rs.getString("pp_startdate");
				dateend=rs.getString("pp_enddate");
				if(!"".equals(startdate)&&sdf.parse(datestart).before(sdf.parse(enddate))){
					BaseUtil.showError("行号"+detno1+"的起始日期不能早于行号"+detno2+"的结束日期！");
				}
				startdate=datestart;
				enddate=dateend;
				detno2=detno1;
			}
		}
	}
  	
  	/**
  	 * 项目阶段变更申请单提交时，项目阶段内容必须填写
  	 * 下一个阶段的开始时间不能早于上阶段的结束时间！
  	 * @throws ParseException 
  	 */
  	public void phaseChange_submit_checkPhase(Integer id) throws ParseException{
  		try {
  			//除去新增项目的原有项目
  			List<Map<String,Object>> phases = baseDao.queryForList("SELECT PCD_PHASEID,PCD_NEWPHASESTART,PCD_NEWPHASEEND,PCD_DETNO,PCD_PHASE,PP_DETNO FROM PRJPHASECHANGEDET INNER JOIN PROJECTPHASE ON　PCD_PHASEID = PP_ID　WHERE PCD_PCID ="+id+" AND PP_ID IS NOT NULL ORDER BY PP_DETNO ASC");
  			if (phases.size()>0) {
			//判断下一阶段的开始时间不能早于上阶段的结束时间
			Object prjid = baseDao.getFieldDataByCondition("PRJPHASECHANGE inner join PROJECT on pc_prjcode=prj_code", "prj_id", "pc_id ="+id);
			SqlRowList rs = null;
			Date front = null;
			Date after = null;
			for (int i=0;i<phases.size();i++) {				
				Object detno = phases.get(i).get("PP_DETNO");
				rs = baseDao.queryForRowSet("select * from (select rownum rn, t.* from (select pp_phase,pp_enddate,pp_detno from projectphase where pp_prjid="+ prjid+" and pp_detno <"+detno+" order by pp_detno desc) t) where rn<2");
				if (rs.next()) {
					Date newstart = (Date)phases.get(i).get("PCD_NEWPHASESTART");
					if (i>0&&phases.get(i-1).get("PP_DETNO").equals(rs.getObject("pp_detno"))) {
						front = (Date) phases.get(i-1).get("PCD_NEWPHASEEND");
						if (front!=null&&newstart.before(front)) {
							BaseUtil.showError("明细行序号："+phases.get(i).get("PCD_DETNO")+",\""+phases.get(i).get("PCD_PHASE")+"\"的计划开始日期不能早于上一阶段 :明细行序号："+phases.get(i-1).get("PCD_DETNO")+",\""+phases.get(i-1).get("PCD_PHASE")+"\"的计划结束日期，提交失败！");
						}	
					}else {
						front = rs.getDate("pp_enddate");
						if (front!=null&&newstart.before(front)) {
							BaseUtil.showError("明细行序号："+phases.get(i).get("PCD_DETNO")+",\""+phases.get(i).get("PCD_PHASE")+"\"的计划开始日期不能早于上一阶段 \""+rs.getString("pp_phase")+"\"的计划结束日期，提交失败！");
						}	
					}					
				}
				rs = baseDao.queryForRowSet("select * from (select rownum rn, t.* from (select pp_phase,pp_startdate,pp_detno from projectphase where pp_prjid="+ prjid+" and pp_detno >"+detno+" order by pp_detno asc) t) where rn<2");
				if (rs.next()) {
					Date newend = (Date) phases.get(i).get("PCD_NEWPHASEEND");
					if (i+1<phases.size()&&phases.get(i+1).get("PP_DETNO").equals(rs.getObject("pp_detno"))) {
						after = (Date) phases.get(i+1).get("PCD_NEWPHASESTART");
						if (after!=null&&newend.after(after)) {
							BaseUtil.showError("明细行序号："+phases.get(i).get("PCD_DETNO")+",\""+phases.get(i).get("PCD_PHASE")+"\"的计划结束日期不能晚于下一阶段: 明细行序号："+phases.get(i+1).get("PCD_DETNO")+",\""+phases.get(i+1).get("PCD_PHASE")+"\"的计划开始日期，提交失败！");
						}	
					}else {
						after = rs.getDate("pp_startdate");
						if (after!=null&&newend.after(after)) {
							BaseUtil.showError("明细行序号："+phases.get(i).get("PCD_DETNO")+",\""+phases.get(i).get("PCD_PHASE")+"\"的计划结束日期不能晚于下一阶段\""+rs.getString("pp_phase")+"\"的计划开始日期，提交失败！");
						}
					}
				}
			}
  			}
  		} catch (Exception e) {
  			BaseUtil.showError(e.getMessage());
		}
	}
	/**
  	 * 项目申请单提交时，项目阶段的时间段必须处于项目整个时间段之内
  	 * @throws ParseException 
  	 */
  	public void projectRequest_submit_checkProjecTimeRange(Integer id) throws ParseException{
  		SqlRowList rs = baseDao.queryForRowSet("select wm_concat(prj_id) res from project where prj_id="+id+" and (prj_start>(select min(pp_startdate) from projectphase where pp_prjid=prj_id) or prj_end<(select max(pp_enddate) from projectphase where pp_prjid=prj_id))");
  		if(rs.next()){
  			if(rs.getString("res")!=null){
  				BaseUtil.showError("项目阶段的时间段必须处于项目整个时间段之内");
  			}
  		}
  	}
  	/**
  	 * 预立项任务转正式立项的时候,转入的主项目和子项目都审批通过
  	 * 
  	 */
  	public void preProject_TurnProjectRequest_Audit(Integer id) {
  		Employee employee = SystemSession.getUser();
  		boolean bool = baseDao.checkIf("project", "prj_id="+id);
  		if(bool) {
  			Object[] prstatuscode=baseDao.getFieldsDataByCondition("project",new String[] {"prj_preauditman","prj_preauditmancode"}, "prj_id="+id);
  			baseDao.execute("update project set prj_auditman=?,prj_auditmancode=?,prj_auditstatus='已审核',prj_auditstatuscode='AUDITED',prj_auditdate=sysdate where prj_id="+id,
  					new Object[] {prstatuscode[0],prstatuscode[1]});
  			List<Object[]> sobId = baseDao.getFieldsDatasByCondition("project", new String[] {"prj_id"}, "prj_mainproid="+id);
  			for (Object[] objects : sobId) {
  				baseDao.execute("update project set prj_status='未启动',prj_statuscode='UNDOING',prj_person=?,prj_recordate=sysdate,prj_auditman=?,prj_auditmancode=?,prj_auditstatus='已审核',prj_auditstatuscode='AUDITED',prj_auditdate=sysdate where prj_id="+objects[0],
  	  					new Object[] {prstatuscode[0],prstatuscode[0],prstatuscode[1]});
  				baseDao.execute("update Team set team_pricode=(select prj_code from project where prj_id="+objects[0]+") where team_prjid="+objects[0]);
  				
  				//将子项目的项目阶段与主项目相同
  				List<Object[]> projectphase = baseDao.getFieldsDatasByCondition("projectphase", new String[] {"pp_chargepersoncode", "pp_chargeperson","pp_startdate","pp_enddate","pp_realstartdate","pp_realenddate"},"pp_prjid="+id);
  				for (Object[] objects2 : projectphase) {
  					baseDao.execute("update projectphase set pp_chargepersoncode=?,pp_chargeperson=?,pp_startdate=to_date(?,'yyyy-MM-dd HH24:MI:SS'),pp_enddate=to_date(?,'yyyy-MM-dd HH24:MI:SS'),pp_realstartdate=to_date(?,'yyyy-MM-dd HH24:MI:SS'),pp_realenddate=to_date(?,'yyyy-MM-dd HH24:MI:SS') where pp_prjid="+objects[0],objects2);
				}
  			    //生成文件目录
  				boolean fileExist = baseDao.checkIf("projectdoc", "pd_prjid=" + objects[0]);
  				int prj_id = Integer.parseInt(objects[0].toString());
  				List<String> sqls = new ArrayList<String>();
  				if(!fileExist){
  					Object[] productType = baseDao.getFieldsDataByCondition("project left join plmproducttype on pt_code=prj_producttypecode", new String[]{"pt_code","prj_code"}, "prj_id=" + prj_id);
  					if(productType!=null){
  						if(productType[0]!=null){ 
  							//产品类型存在
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
  				baseDao.execute(sqls);
			}
  		}
  	}
  	/**
  	 * 子项目阶段时间必须在主项目阶段时间内
  	 * @throws ParseException 
  	 */
  	public void sobProjectTime_MustBeIn_MainProjectTime(Integer id) throws ParseException {
  		List<Object[]> mainProject = baseDao.getFieldsDatasByCondition("Project left join projectphase on pp_prjid=prj_id",
  				new String[] {"prj_mainproid","pp_startdate","pp_enddate","pp_detno"}, "prj_id="+id);
  		StringBuffer string = new StringBuffer();
  		for (Object[] objects : mainProject) {
			Object[] ProjectTime = baseDao.getFieldsDataByCondition("Project left join projectphase on pp_prjid=prj_id", 
					new String[] {"pp_startdate","pp_enddate"}, "prj_id="+objects[0]+" and pp_detno="+objects[3]);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date sobStart = format.parse(objects[1].toString());
			Date sobEnd = format.parse(objects[2].toString());
			Date mainStart = format.parse(String.valueOf(ProjectTime[0]));
			Date mainEnd = format.parse(String.valueOf(ProjectTime[1]));
			if(sobStart.compareTo(mainStart)<0 || sobEnd.compareTo(mainEnd)>0) {
				string.append("阶段"+objects[3].toString()+",");
			}
		}
  		if(string.length()>0) {
  			string.deleteCharAt(string.length()-1);
  			string.append("的时间不在主项目对应阶段区间内，限制提交！");
  			BaseUtil.showError(string.toString());
  		}
  		
  	}
}
