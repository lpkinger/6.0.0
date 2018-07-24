package com.uas.mobile.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.dao.BaseDao;
import com.uas.mobile.service.APPMessageCenterService;


@Service
public class APPMessageCenterServiceImpl implements APPMessageCenterService {

	@Autowired
	private BaseDao baseDao;
	
	@Override
	public Integer queryAllCount(String emcode,String type) {
		String cond="";
		if(type==null || "".equals(type)) cond="em_code='"+emcode+"'";
		else if(type.equals("common"))cond="em_code='"+emcode+"' and pr_from is null";
		else cond="em_code='"+emcode+"' and pr_from='"+type+"'";
		Object count=baseDao.getCountByCondition("pagingrelease left join pagingreleasedetail on prd_prid=pr_id left join employee on prd_recipientid=em_id and prd_recipient=em_name", cond);
		return Integer.parseInt(count.toString());
	}

	@Override
	public List<Map<String, Object>> queryEmNews(String emcode) {
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		List<Object[]> list = baseDao.getFieldsDatasByCondition(" PAGINGRELEASE right join (select COUNT(*) cou,max(pr_id)pr_id from pagingrelease left join pagingreleasedetail on prd_prid=pr_id left join employee "
				+ "on prd_recipientid=em_id and prd_recipient=em_name where em_code='"+emcode+"' group by pr_from)tab on tab.pr_id=PAGINGRELEASE.pr_id", new String[] {
				"cou","pr_context","pr_from","pr_date" },"1=1");
		List<Map<String, Object>> detail = null;
		String type = null;
		for (Object[] obj : list) { 
			String title="";
			if(obj[2]!=null && obj[2]!=""){
				type = obj[2].toString();
				if(obj[2].equals("crm"))title="CRM提醒";
				if(obj[2].equals("note"))title="通知公告";
				if(obj[2].equals("kpi"))title="考勤提醒";
				if(obj[2].equals("meeting"))title="会议提醒";
				if(obj[2].equals("process"))title="审批知会";
				if(obj[2].equals("task"))title="任务提醒";
				if(obj[2].equals("job"))title="稽核提醒";
				if(obj[2].equals("b2b"))title="B2B提醒";
				if(obj[2].equals("system"))title="知会消息";
			}else {
				type = "common";
				title = "普通知会";
			}
			map = new HashMap<String, Object>();
			map.put("count", Integer.parseInt(String.valueOf(obj[0])));
			map.put("title", title);
			map.put("lastMessage", obj[1]);
			map.put("lastTime", obj[3]);
			map.put("type", type);
			
			//获取具体内容
			detail = queryEmNewsDetails(emcode,type);
			map.put("detail", detail);
			
			lists.add(map);
		}
		
		
		
		
		return lists;
	}

	@Override
	public List<Map<String, Object>> queryEmNewsDetails(String emcode,String type) {
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		String cond="";
		if(type!=null && type.equals("common")) cond=" and pr_from is null ";
		else cond=" and pr_from='"+type+"' ";
		List<Object[]> list = baseDao.getFieldsDatasByCondition("pagingrelease left join pagingreleasedetail on prd_prid=pr_id left join employee "
				+ "on prd_recipientid=em_id and prd_recipient=em_name", new String[] {
				"pr_releaser","pr_date","pr_context","pr_title","pr_id","pr_caller","pr_keyvalue","pr_codevalue"},"em_code='"+emcode+"' "+cond+" order by pr_date asc");
		for (Object[] obj : list) { 
			Object title="";
			if(obj[3]==null || "".equals(obj[3])){
				if(type.equals("crm"))title="CRM提醒";
				if(type.equals("note"))title="通知公告";
				if(type.equals("kpi"))title="考勤提醒";
				if(type.equals("meeting"))title="会议提醒";
				if(type.equals("process"))title="审批知会";
				if(type.equals("task"))title="任务提醒";
				if(type.equals("job"))title="稽核提醒";
				if(type.equals("b2b"))title="B2B提醒";
				if(type.equals("system"))title="知会消息";
				if(type.equals("common"))title="普通知会";
			}else title=obj[3];
			map = new HashMap<String, Object>();
			map.put("id", obj[4]);
			map.put("title", title);
			map.put("subTitle", obj[2]);
			map.put("createTime", obj[1]);
			map.put("releaser", obj[0]);
			map.put("caller", obj[5]);
			map.put("keyValue", obj[6]);
			map.put("code", obj[7]);
			
			lists.add(map);
		}

		//推送过的消息推送状态改为已推送
		if(type!=null && type.equals("common")){
			baseDao.execute("update ICQHISTORYdetail set IHD_STATUS=-1 where IHD_IHID in(select ih_id from ICQHISTORY where ih_from is null) and IHD_RECEIVEID=(select em_id from employee where em_code='"+emcode+"')");
			baseDao.execute("delete from PAGINGRELEASEDETAIL where PRD_PRID in (SELECT pr_id FROM PAGINGRELEASE where pr_from is null) and prd_recipientid=(select em_id from employee where em_code='"+emcode+"')");
		}else{
			baseDao.execute("update ICQHISTORYdetail set IHD_STATUS=-1 where IHD_IHID in(select ih_id from ICQHISTORY where ih_from='"+type+"') and IHD_RECEIVEID=(select em_id from employee where em_code='"+emcode+"')");
			baseDao.execute("delete from PAGINGRELEASEDETAIL where PRD_PRID in (SELECT pr_id FROM PAGINGRELEASE where pr_from='"+type+"') and prd_recipientid=(select em_id from employee where em_code='"+emcode+"')");
		}
		return lists;
	}
}
