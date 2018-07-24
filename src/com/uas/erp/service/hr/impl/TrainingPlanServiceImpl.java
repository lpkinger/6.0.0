package com.uas.erp.service.hr.impl;

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
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.hr.TrainingPlanService;

@Service
public class TrainingPlanServiceImpl implements TrainingPlanService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveTrainingPlan(String formStore, String gridStore, String caller) throws ParseException {	
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller,  new Object[]{store,gstore});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "TrainingPlan",  new String[]{}, new Object[]{});
		baseDao.execute(formSql);	
		for(Map<Object, Object> m:gstore){
			m.put("ti_id", baseDao.getSeqId("TRAININGCOURSEINSTANCE_SEQ"));
			m.put("ti_status", "未开始");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gstore, "TRAININGCOURSEINSTANCE");
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "tp_id", store.get("tp_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller,  new Object[]{store,gstore});
	}

	@Override
	public void updateTrainingPlanById(String formStore, String gridStore,
			String caller) throws ParseException {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[]{store,gstore});
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "TrainingPlan", "tp_id");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "TRAININGCOURSEINSTANCE", "ti_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("ti_id") == null || s.get("ti_id").equals("") || s.get("ti_id").toString().equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("TRAININGCOURSEINSTANCE_SEQ");
				s.put("ti_status", "未开始");
				String sql = SqlUtil.getInsertSqlByMap(s, "TRAININGCOURSEINSTANCE", new String[]{"ti_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "tp_id", store.get("tp_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store,gstore});
	}

	@Override
	public void deleteTrainingPlan(int tp_id, String caller) {		
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{tp_id});
		//删除purchase
		baseDao.deleteById("TrainingPlan", "tp_id", tp_id);
		//删除purchaseDetail
		baseDao.deleteById("TRAININGCOURSEINSTANCE", "ti_tpid", tp_id);
		//记录操作
		baseDao.logger.delete(caller, "tp_id", tp_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{tp_id});
	}

	@Override
	public void auditTrainingPlan(int tp_id, String caller) throws ParseException {		
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("TrainingPlan", "tp_statuscode", "tp_id=" + tp_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{tp_id});
		Object detno=baseDao.getFieldDataByCondition("trainingCourseInstance", "min(ti_detno)", "ti_tpid=" + tp_id);
		if(detno!=null){
			Object[] ob=baseDao.getFieldsDataByCondition("trainingCourseInstance",new String[]{"ti_tcname","ti_id"}, "ti_tpid="+tp_id+" and ti_detno="+detno);
			baseDao.execute("update trainingCourseInstance set ti_startdate=ti_prestartdate,ti_status='进行中' where ti_id="+ob[1]);
			baseDao.execute("update trainingPlan set tp_tcname='"+ob[0]+"' where tp_id="+tp_id);
		}
		//插入人员档案从表-内部培训
		Object emid=baseDao.getFieldDataByCondition("TrainingPlan left join employee on tp_emcode=em_code", "em_id","tp_id=" + tp_id);
		Object atdetno=baseDao.getFieldDataByCondition("ARCHIVETRAINING", "nvl(max(at_detno),0)+1", "at_emid="+emid);
		baseDao.execute("insert into ARCHIVETRAINING (at_id,at_detno,at_emid,at_tpcode,at_tpname,at_startdate) "
				+ "select ARCHIVETRAINING_SEQ.nextval,?,?,tp_code,tp_name,tp_begin from trainingplan where tp_id=?",new Object[] {atdetno,emid,tp_id});	
		//执行审核操作
		baseDao.audit("TrainingPlan", "tp_id=" + tp_id, "tp_status", "tp_statuscode", "tp_auditdate", "tp_auditer");
		//记录操作
		baseDao.logger.audit(caller, "tp_id", tp_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{tp_id});
	}

	@Override
	public void resAuditTrainingPlan(int tp_id, String caller) {
		int count=baseDao.getCount("select count(1) from ARCHIVETRAINING where (at_tpcode,at_emid)=(select tp_code,em_id "
				+ "from TrainingPlan left join employee on tp_emcode=em_code where tp_id="+tp_id+")");
		if(count>0){//删除人员档案从表内部培训记录
			baseDao.execute("delete ARCHIVETRAINING where (at_tpcode,at_emid)=(select tp_code,em_id from TrainingPlan left join employee on tp_emcode=em_code where tp_id=?)",tp_id);
		}
		Object status = baseDao.getFieldDataByCondition("TrainingPlan", "tp_statuscode", "tp_id=" + tp_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resAudit("TrainingPlan", "tp_id=" + tp_id, "tp_status", "tp_statuscode","tp_auditdate", "tp_auditer");
		//记录操作
		baseDao.logger.resAudit(caller, "tp_id", tp_id);
		handlerService.handler(caller, "resAudit", "after", new Object[]{tp_id});
	}

	@Override
	public void submitTrainingPlan(int tp_id, String caller) throws ParseException {
		
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("TrainingPlan", "tp_statuscode", "tp_id=" + tp_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{tp_id});
		/**
		 * 反馈编号：2017050219
		 * 个人培训计划：提交时限制人员编号和辅导员编号不能相同
		 * @author wsy
		 */
		boolean bool = baseDao.checkByCondition("TrainingPlan", "tp_emcode=tp_instructorcode and tp_id="+tp_id);
		if(!bool){
			BaseUtil.showError("人员编号和辅导员编号不能相同！");
		}
		Object date_n = baseDao.getFieldDataByCondition("TrainingPlan", "tp_begin", "tp_id=" + tp_id);
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd"); 
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		Date day=(Date) date_n;//计划开始时间
		Object[] ob=baseDao.getFieldsDataByCondition("AttendSystem",new String[]{"as_amstarttime","as_amendtime","as_pmstarttime","as_pmendtime",
				"(to_date(as_amendtime,'hh24:mi')-to_date(as_amstarttime,'hh24:mi'))*24","(to_date(as_pmstarttime,'hh24:mi')-to_date(as_amendtime,'hh24:mi'))*24",
				"(to_date(as_pmendtime,'hh24:mi')-to_date(as_pmstarttime,'hh24:mi'))*24"}, "1=1");   
		if(ob!=null){
			String sql="select ti_id,ti_period from trainingCourseInstance where ti_tpid="+tp_id+" order by ti_detno";
			SqlRowList rs=baseDao.queryForRowSet(sql);
			double h_work=8,n_rest=0;
			int flag=1;
			Date t_n=new Date(),t_2=new Date(),t_1=new Date(),t_4=new Date();
			h_work=Double.parseDouble(ob[4].toString())+Double.parseDouble(ob[6].toString());//工作时长	
			n_rest=Double.parseDouble(ob[5].toString());
			long t1 = sdf.parse(ob[0].toString()).getTime();
			t_n= new Date(t1);
			t_1=new Date(sdf.parse(ob[0].toString()).getTime());//上午上班
			t_2=new Date(sdf.parse(ob[1].toString()).getTime());//上午下班
			t_4=new Date(sdf.parse(ob[3].toString()).getTime());//下午下班
			int no=1;
			while(rs.next()){
				if(no==1){
					//明细行第一条预计起始时间
					if(day.getDay()==0){
						day=new Date(day.getTime()+86400000);//周日+1天
					}else if(day.getDay()==6){
						day=new Date(day.getTime()+86400000*2);//周六+2天
					}
					baseDao.execute("update TrainingCourseInstance set ti_prestartdate=to_date('"+df.format(day)+ob[0].toString()+"','yyyy-mm-dd hh24:mi') where ti_id="+rs.getInt("ti_id"));
					//预计结束时间
					double d=Math.floor(rs.getDouble("ti_period")/h_work);
					for(int i=0;i<d;i++){
						day=new Date(day.getTime()+86400000);
						if(day.getDay()==0){
							day=new Date(day.getTime()+86400000);//周日+1天
						}else if(day.getDay()==6){
							day=new Date(day.getTime()+86400000*2);//周六+2天
						}
					}
					double h=rs.getDouble("ti_period")%h_work;
					t_n=new Date((long) (t_n.getTime()+h*3600000));
					if(t_2.before(t_n)){
						flag=2;
						t_n=new Date((long) (t_n.getTime()+n_rest*3600000));
					}
					baseDao.updateByCondition("TrainingCourseInstance","ti_preenddate=to_date('"+df.format(day)+sdf.format(t_n.getTime())+"','yyyy-mm-dd hh24:mi')","ti_id="+rs.getInt("ti_id"));						
					no++;
				}else{
					baseDao.updateByCondition("TrainingCourseInstance","ti_prestartdate=to_date('"+df.format(day)+sdf.format(t_n.getTime())+"','yyyy-mm-dd hh24:mi')","ti_id="+rs.getInt("ti_id"));						
					double d=Math.floor(rs.getDouble("ti_period")/h_work);
					for(int i=0;i<d;i++){
						day=new Date(day.getTime()+86400000);
						if(day.getDay()==0){
							day=new Date(day.getTime()+86400000);//周日+1天
						}else if(day.getDay()==6){
							day=new Date(day.getTime()+86400000*2);//周六+2天
						}
					}
					double h=rs.getDouble("ti_period")%h_work;
					t_n=new Date((long) (t_n.getTime()+h*3600000));
					//第二条
					if(flag==1){
						if(t_2.before(t_n)){
							flag=2;
							t_n=new Date((long) (t_n.getTime()+n_rest*3600000));
							if(t_4.before(t_n)){
								day=new Date(day.getTime()+86400000);//+1天
								if(day.getDay()==0){
									day=new Date(day.getTime()+86400000);//周日+1天
								}else if(day.getDay()==6){
									day=new Date(day.getTime()+86400000*2);//周六+2天
								}
								t_n=new Date((long) (t_n.getTime()-t_4.getTime()+t_1.getTime()));
								flag=1;
							}
						}
					}else{
						if(t_4.before(t_n)){
							day=new Date(day.getTime()+86400000);//+1天
							if(day.getDay()==0){
								day=new Date(day.getTime()+86400000);//周日+1天
							}else if(day.getDay()==6){
								day=new Date(day.getTime()+86400000*2);//周六+2天
							}
							t_n=new Date((long) (t_n.getTime()-t_4.getTime()+t_1.getTime()));
							if(t_2.before(t_n)){
								t_n=new Date((long) (t_n.getTime()+n_rest*3600000));
							}else{
								flag=1;
							}
						}
					};
				}
				baseDao.updateByCondition("TrainingCourseInstance","ti_preenddate=to_date('"+df.format(day)+sdf.format(t_n.getTime())+"','yyyy-mm-dd hh24:mi')","ti_id="+rs.getInt("ti_id"));						
			}
		}else{
			BaseUtil.showError("考勤系统未设置");
		}
		
		//执行提交操作
		baseDao.submit("TrainingPlan", "tp_id=" + tp_id, "tp_status", "tp_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "tp_id", tp_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{tp_id});
	}

	@Override
	public void resSubmitTrainingPlan(int tp_id, String caller) {
		
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("TrainingPlan", "tp_statuscode", "tp_id=" + tp_id);
		StateAssert.resSubmitOnlyCommited(status);
		//执行反提交操作
		baseDao.resOperate("TrainingPlan", "tp_id=" + tp_id, "tp_status", "tp_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "tp_id", tp_id);
		handlerService.afterResSubmit(caller, new Object[] { tp_id});
	}

	@Override
	public List<Map<String, Object>> getTrainingCourse(String code) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		SqlRowList rs=baseDao.queryForRowSet("select ttd_tccode,ttd_tcname,ttd_lecturer,ttd_period,tc_link from TrainingCourseTemplatedet left join TrainingCourseTemplate on ttd_ttid=tt_id left join TrainingCourse on ttd_tccode=tc_code where tt_code='"+code+"' order by ttd_detno");
		while(rs.next()){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ti_tccode",rs.getString("ttd_tccode"));//课程编号
			map.put("ti_tcname",rs.getString("ttd_tcname"));//课程名称
			map.put("ti_ttname",rs.getString("ttd_lecturer"));//讲师
			map.put("ti_period",rs.getDouble("ttd_period"));//课时
			map.put("tc_link",rs.getString("tc_link"));//资源链接
			datas.add(map);
		}
		return datas;
	}
}
