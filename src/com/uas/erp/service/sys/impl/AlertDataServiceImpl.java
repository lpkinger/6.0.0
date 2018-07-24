package com.uas.erp.service.sys.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.sys.AlertDataService;
@Service
public class AlertDataServiceImpl implements AlertDataService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void revert(int id, String caller,String AD_CAUSE,String AD_SOLUTION) {
		int countByCondition = baseDao.getCountByCondition("alert_data", "ad_status='CLOSED' and ad_id ="+id);
		if(countByCondition>0){
			BaseUtil.showError("单据已被确认，不能回复");
		}
		Employee employee = SystemSession.getUser();
		String em_type = employee.getEm_type();
		if(!"admin".equals(em_type)){   //如果是管理员账号，则都可以回复
			int isexpire = baseDao.getCountByCondition("alert_data left join Alert_Receiver on ad_id = ar_adid", "nvl(AR_ISEXPIRED,0)=-1 and AR_RECEMANCODE='"+employee.getEm_code()+"' and ar_adid ="+id);
			if(isexpire>0){
				BaseUtil.showError("单据已过期，已推送到上一级，您已不能回复");
			}
		}
		//回复信息，更新当前单据的状态
		baseDao.execute("update alert_data set ad_cause = ?,ad_solution= ? ,ad_man=?,ad_date=sysdate where ad_id=?",AD_CAUSE,AD_SOLUTION,SystemSession.getUser().getEm_name(),id);
		baseDao.logger.others("回复", SystemSession.getUser().getEm_name()+"回复成功", caller, "ad_id", id);
	}

	@Override
	public void confirm(int id, String caller, String AD_CAUSE, String AD_SOLUTION) {
		Employee employee = SystemSession.getUser();
		String em_type = employee.getEm_type();
		if(!"admin".equals(em_type)){   //如果是管理员账号，则都可以确认
			int isexpire = baseDao.getCountByCondition("alert_data left join Alert_Receiver on ad_id = ar_adid", "nvl(AR_ISEXPIRED,0)=-1 and AR_RECEMANCODE='"+employee.getEm_code()+"' and ar_adid ="+id);
			if(isexpire>0){
				BaseUtil.showError("单据已过期，已推送到上一级，您已不能确认");
			}
			/**
			 * 获取确认按钮的层级设置
			 */
			String confirmLevel = baseDao.getDBSetting("AlertData","confirmLevel");
			int level = 1;
			if(confirmLevel!=null){
				level = (int)Math.floor(Double.parseDouble(confirmLevel));
			}
			if(level>1){
				Object pushlevel = baseDao.getFieldDataByCondition("alert_data left join Alert_Receiver on ad_id = ar_adid", "MAX(AR_PUSHLEVEL)", "AR_RECEMANCODE='"+employee.getEm_code()+"' and ar_adid ="+id);
				if(pushlevel!=null){
					int pushLevel = (int)Math.floor(Double.parseDouble(pushlevel.toString()));
					if(pushLevel<level){
						BaseUtil.showError("预警信息只能由第"+level+"层级或以上的人员来确认");
					}
				}
			}
		}
		baseDao.execute("update alert_data set ad_status ='CLOSED' , ad_cause = ?,ad_solution= ? ,ad_man=?,ad_date=sysdate where ad_id=?",AD_CAUSE,AD_SOLUTION,SystemSession.getUser().getEm_name(),id);
		baseDao.logger.others("确认", "确认成功", caller, "ad_id", id);
	}

	@Override
	public List<Map<String, Object>> getAlertData(Employee employee, String condition, String likestr, Integer page,
			Integer pageSize) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int start = ((page - 1) * pageSize + 1);
		int end = page * pageSize;
		list = baseDao.queryForList("select * From (select AD_ID, AD_RESULT, AD_STATUS, AD_ITEMNUMBER, AD_CREATEITEM, AD_CAUSE, AD_SOLUTION, AD_MAN, AD_DATE, AD_LEVEL,ROW_NUMBER() OVER(ORDER BY AD_DATE DESC) RN  from  alert_data left join Alert_Receiver on ad_id= ar_adid where AR_RECEIVEMAN='"+SystemSession.getUser().getEm_name()+"'"+condition+") where "
				+" RN>="+start+" and RN<"+end);
		return list;
	}

	@Override
	public void dealRevert(String caller, String data) {
		List<Map<Object,Object>> gridStore = BaseUtil.parseGridStoreToMaps(data);
		for(Map<Object,Object> m:gridStore ){
			baseDao.execute("update alert_data set ad_cause = ? ,ad_solution=?,ad_man = ?,ad_date= sysdate where ad_id =?",m.get("ad_cause"),m.get("ad_solution"),SystemSession.getUser().getEm_name(),m.get("ad_id"));
		}
	}

	/*@Override
	public int getAlertDataTotal(Employee employee, String condition, String likestr, Integer page, Integer pageSize) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<String, Object> getAlertDataCount(Employee employee) {
		// TODO Auto-generated method stub
		return null;
	}*/

}
