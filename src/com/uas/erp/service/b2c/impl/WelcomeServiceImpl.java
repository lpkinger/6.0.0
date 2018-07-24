package com.uas.erp.service.b2c.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.b2c.WelcomeService;
@Service
public class WelcomeServiceImpl implements WelcomeService {
	@Autowired
	private BaseDao baseDao;
	private static Master currentMaster = null;
	@Override
	public boolean getWelcomeStatus(Employee employee) {
		try {
			Object visitedres = baseDao.getFieldDataByCondition("CommonUse", "cu_count", "cu_emid="+employee.getEm_id()+" and cu_description ='商城官网' and rownum = 1 order by cu_lock desc");
				if(visitedres!=null&& Integer.parseInt(visitedres.toString())>0){
				return false;
			}else{
				 return true;
			 }
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean setWelcomeStatus(Employee employee,String url) {
		try {
			Object[] cumsg = baseDao.getFieldsDataByCondition("CommonUse","cu_id,cu_count","cu_emid="+employee.getEm_id()+" and cu_description='商城官网'");
			if(cumsg!=null){
				baseDao.execute("update CommonUse set cu_count = "+(Integer.parseInt(cumsg[1].toString())+1)+", url = '"+url+"' where cu_emid="+employee.getEm_id()+" and cu_description='商城官网' ");
			}else{
				baseDao.execute("insert into CommonUse (CU_ID,CU_DESCRIPTION,CU_SNID,CU_COUNT,CU_EMID,CU_URL,CU_LOCK) values(CommonUse_seq.nextval,'商城官网',0,1,"+employee.getEm_id()+",'"+url+"',0)");
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	@Override
	public String isTureMaster() {
		if(SystemSession.getUser()!=null){
			Employee em =  SystemSession.getUser();
			currentMaster = em.getCurrentMaster();
			String em_type = em.getEm_class();
			if(em_type.equals("admin_virtual")||em_type.equals("customer_virtual")){
				return"当前账户状态异常无法访问优软商城，请切换至正式账户访问。";
			}
			if(currentMaster.getMa_uu()==null){//没有企业uu号
				return "您所在企业尚未注册优软云，请前往企业资料进行注册！";
			}else if(currentMaster.getMa_accesssecret()==null){//没有企业密钥
				return "您尚未开通通过UAS访问优软商城功能，如需开通，请联系优软售后。";
			}else if(currentMaster.getEnv()==null){//环境字段为空
				return "您尚未开通通过UAS访问优软商城功能，如需开通，请联系优软售后。";
			}else if(currentMaster.getEnv().equals("test")){//环境为测试环境
				return "您尚未开通通过UAS访问优软商城功能，如需开通，请联系优软售后。";
			}else{
				return "Yes";
			}
		}else return "您没有登录或登录失效，请重新登录";
	}
}