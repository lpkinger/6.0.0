package com.uas.erp.core.support;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.ContextUtil;
import com.uas.erp.core.exception.SystemException;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.ScheduleTask;

@Lazy(false)
@Component
@EnableScheduling
public class SpringDynamicCronTask implements SchedulingConfigurer{
	
	@Autowired
	private BaseDao baseDao;

	private static List<TriggerTask> taskList = new ArrayList<TriggerTask>();

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		//在数据库中查询定时任务配置列表，得到list
		try{
			if ("true".equals(BaseUtil.getXmlSetting("task.status"))) {
				//task.status 定时任务状态设置:为空或为false默认不启用，通过管理平台发布自动修改为ture
				initTaskList();
			}
			taskRegistrar.setTriggerTasksList(taskList);
		}catch(Exception e){
			
		}
	}
	
	public void initTaskList(){
		String sql = "SELECT * FROM SYS_SCHEDULETASK WHERE ENABLE_ = -1";
		List<ScheduleTask> list = baseDao.query(sql, ScheduleTask.class);
		//遍历taskList,将每个task添加到要处理任务列表中
		if(list != null && list.size() > 0){
			for(final ScheduleTask task : list){
				taskList.add(
						new TriggerTask(
								new Runnable(){
									@Override
									public void run(){
										//通过反射执行相应的任务
										try {
											baseDao.execute("insert into SYS_SCHEDULETASKLOG(date_,remark_,scheduleid_) values"
													+ "(sysdate,'执行开始',"+task.getId_()+")");
											String beanName = task.getBean_();
											Object bean = ContextUtil.getBean(beanName);
											if(bean == null){
												throw new SystemException("当前填写bean错误或不存在！");
											}else{
												Method method = bean.getClass().getMethod(task.getFunction_());
												method.invoke(bean);
											}
											//记录操作日志
											baseDao.execute("insert into SYS_SCHEDULETASKLOG(date_,remark_,scheduleid_) values"
												+ "(sysdate,'执行结束',"+task.getId_()+")");
										} catch (Exception e) {
											if (e.getCause() != null) {
												baseDao.execute("insert into SYS_SCHEDULETASKLOG(date_,remark_,scheduleid_) values"
														+ "(sysdate,'执行失败："+e.getCause().getMessage()+"',"+task.getId_()+")");
											}else
												baseDao.execute("insert into SYS_SCHEDULETASKLOG(date_,remark_,scheduleid_) values"
													+ "(sysdate,'执行失败: "+e.getMessage()+"',"+task.getId_()+")");
										}
									}
								},new Trigger(){
									public Date nextExecutionTime(TriggerContext triggerContext) {
										CronTrigger trigger = new CronTrigger(task.getCron_());  
						                Date nextExec = trigger.nextExecutionTime(triggerContext);  
						                return nextExec;  
									}
								}
						)
				);
			}
		}
	}
	
}
