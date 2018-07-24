package com.uas.erp.service.wisdomPark.impl;


import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Master;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.common.EnterpriseService;

@Lazy(false)
@Component
@EnableScheduling
public class EndActivityTask implements SchedulingConfigurer{
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	protected EnterpriseService enterpriseService;

	private ScheduledTaskRegistrar taskRegistrar;
	
	private static Map<String,Map<Integer,ScheduledFuture<?>>> futuress = new ConcurrentHashMap<String, Map<Integer,ScheduledFuture<?>>>();

	protected List<Master> getMasters() {
		return enterpriseService.getMasters();
	}
	
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		try{
			this.taskRegistrar = taskRegistrar;
			if ("true".equals(BaseUtil.getXmlSetting("task.status"))) {
				//等待定时器初始化完成
				Timer timer=new Timer();//实例化Timer类
				timer.schedule(new TimerTask(){
					public void run(){
					  initTaskMap();
					  this.cancel();
					 }
				},60000);//1分钟
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private synchronized void initTaskMap(){
		if(!futuress.isEmpty()){
			return ;
		}
	
		String sob = SpObserver.getSp();
		for (Master master : getMasters()) {
			
			if (!StringUtil.hasText(master.getMa_ccwebsite())) {
				continue;
			}
			
			SpObserver.putSp(master.getMa_name());
			try {
				String now = DateUtil.format(new Date(), Constant.YMD_HMS);
				List<Integer>  acids = baseDao.getFieldValues("ActivityCenter", "ac_id", "to_char(ac_enddate,'" + Constant.ORACLE_YMD_HMS + "') <= '" + now + "' and ac_status = '进行中'", Integer.class);
				for (Integer acid : acids) {
					baseDao.updateByCondition("ActivityCenter", "ac_status = '已结束'", "ac_id = " + acid);
					baseDao.logMessage(new MessageLog("项目启动", "结束刷新", "成功结束", "ActivityCenter|ac_id=" + acid));
				}
				Map<Integer,ScheduledFuture<?>> futures = futuress.get(master.getMa_name());
				if(futures==null){
					futures = new ConcurrentHashMap<Integer, ScheduledFuture<?>>();
				}
				SqlRowList rs = baseDao.queryForRowSet("select ac_id,ac_enddate from ActivityCenter where ac_status = '进行中' order by ac_enddate");
				while (rs.next()) {
					Date endDate = rs.getDate("ac_enddate");
					Integer id = rs.getGeneralInt("ac_id");
					
					ScheduledFuture<?> future =  this.taskRegistrar.getScheduler().schedule(new Runnable(){
						@Override
						public void run(){
							endActivity();
						}
					},endDate);
					
					futures.put(id,future);
					
				}
				futuress.put(master.getMa_name(), futures);
			}catch(BadSqlGrammarException e){
				
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		SpObserver.putSp(sob);
		
	}
	
	public static synchronized ScheduledFuture<?> getFuture(String master, Integer id){
		Map<Integer,ScheduledFuture<?>> futures = futuress.get(master);
		if(futures!=null){
			return futures.get(id);
		}
		return null;
	}
	
	public static synchronized void closeTask(String master, Integer id){
		Map<Integer,ScheduledFuture<?>> futures = futuress.get(master);
		if(futures!=null){
			ScheduledFuture<?> future  = futures.get(id);
			if (future!=null) {
				boolean bool = future.cancel(true);
				if(bool){
					futures.remove(id);
				}
			}
		}
	}

	public synchronized void addTask(Date time, Integer id) {
		String sob = SpObserver.getSp();
		Map<Integer,ScheduledFuture<?>> futures = futuress.get(sob);
		if(futures==null){
			futures = new ConcurrentHashMap<Integer, ScheduledFuture<?>>();
		}
		futures.put(id, this.taskRegistrar.getScheduler().schedule(new Runnable(){
			@Override
			public void run(){
				endActivity();
			}
		},time));
		futuress.put(sob, futures);
	}
	
	private synchronized void endActivity(){
		//执行任务
		try {
			String now = DateUtil.format(new Date(), Constant.YMD_HMS);
			List<Integer> acids = baseDao.getFieldValues("ActivityCenter", "ac_id", "to_char(ac_enddate,'" + Constant.ORACLE_YMD_HMS + "') <= '" + now + "' and ac_status = '进行中'",Integer.class);
			for (Integer acid : acids) {
				baseDao.updateByCondition("ActivityCenter", "ac_status = '已结束'", "ac_id = " + acid);
				baseDao.logMessage(new MessageLog("定时任务", "到时结束", "成功结束", "ActivityCenter|ac_id=" + acid));
				//取消定时任务
				String sob = SpObserver.getSp();
				closeTask(sob, acid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
