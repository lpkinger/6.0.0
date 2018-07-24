package com.uas.erp.service.ma.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.PasswordEncryUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlMap;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.ma.EncryptService;
import com.uas.erp.service.oa.SendMailService;

@Service
public class EncryptServiceImpl implements EncryptService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private EnterpriseService enterpriseService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private SendMailService sendMailService;
	
	/**
	 * 获取当前账套和configs
	 */
	public Map<String, Object> getSob(){
		Map<String, Object> map = new HashMap<String, Object>();
		String currentSob = SpObserver.getSp();
		String defaultSob = BaseUtil.getXmlSetting("defaultSob");
		SpObserver.putSp(defaultSob);			//切换账套
		if(currentSob.equals(defaultSob)){
			map.put("success", true);
			Object encryConfig = baseDao.getFieldDataByCondition("configs", "data", "code = 'encryConfig' and caller = 'sys'");
			if(encryConfig != null && !"".equals(encryConfig)){
				map.put("configs", encryConfig);
			}
		}else{
			map.put("success", false);
			String sobName = String.valueOf(baseDao.getFieldDataByCondition("master", "ma_function", "ma_user = '"+defaultSob+"'"));
			map.put("message", "请切换到：" + sobName + "操作!");
			Object encryConfig = baseDao.getFieldDataByCondition("configs", "data", "code = 'encryConfig' and caller = 'sys'");
			if(encryConfig != null){
				map.put("configs", encryConfig);
			}
		}
		SpObserver.putSp(currentSob);		//账套切回
		return map;
	}
	
	/**
	 * 更新配置信息，加、解密密码
	 */
	@Transactional
	public void updateConfigs(String value){
		String currentSob = SpObserver.getSp();
		String defaultSob = BaseUtil.getXmlSetting("defaultSob");
		if(currentSob.equals(defaultSob)){			//当前账套等于默认账套时
			//遍历所有账套
			List<Master> masterList = enterpriseService.getMasters();
			if("1".equals(value)){
				setConfigs(value);		//更新是否启用状态
				for(Master master : masterList){
					batchEncrypt(master.getMa_name());
				}
				//记录日志
				baseDao.execute("insert into messagelog(ml_id,ml_date,ml_man,ml_content,ml_result) "
						+ "values(messagelog_seq.nextval,sysdate,'"+SystemSession.getUser().getEm_name()+"("+SystemSession.getUser().getEm_code()+")','启用密码加密','加密成功')");
			}else{
				Object data = baseDao.getFieldDataByCondition("configs", "data", "caller = 'sys' and code = 'encryConfig'");
				if(data != null && "1".equals(data)){
					for(Master master : masterList){
						batchDecrypt(master.getMa_name());
					}
					setConfigs(value);		//更新是否启用状态
				}
				setConfigs(value);	
				//记录日志
				baseDao.execute("insert into messagelog(ml_id,ml_date,ml_man,ml_content,ml_result) "
						+ "values(messagelog_seq.nextval,sysdate,'"+SystemSession.getUser().getEm_name()+"("+SystemSession.getUser().getEm_code()+")','取消密码加密','解密成功')");
			}
		}
	}
	
	/**
	 * 加密
	 * @throws Exception
	 */
	public void batchEncrypt(String ma_name){
		//将系统的密码加密
		SqlMap map = null;
		List<SqlMap> sqls = new ArrayList<SqlMap>();
		String[] strArray = {"em_id","em_password","em_mobile"};
		List<Object[]> fields = baseDao.getFieldsDatasByCondition(ma_name+".employee", strArray, "1=1");
		for(Object[] obj : fields){
			map = new SqlMap(ma_name+".employee","em_id");
			map.set("em_id",String.valueOf(obj[0]));
			String password = String.valueOf(obj[1]);
			password = PasswordEncryUtil.encryptPassword(password, String.valueOf(obj[2]));
			map.set("em_password", password);
			sqls.add(map);
		}
		baseDao.batchExecute(sqls, new ArrayList<String>());
	}
	
	/**
	 * 解密
	 * @throws Exception
	 */
	public void batchDecrypt(String ma_name){
		//将系统的密码解密
		SqlMap map = null;
		List<SqlMap> sqls = new ArrayList<SqlMap>();
		String[] strArray = {"em_id","em_password","em_mobile"};
		List<Object[]> fields = baseDao.getFieldsDatasByCondition(ma_name+".employee", strArray, "1=1");
		for(Object[] obj : fields){
			map = new SqlMap(ma_name+".employee","em_id");
			map.set("em_id",String.valueOf(obj[0]));
			String password = String.valueOf(obj[1]);
			password = PasswordEncryUtil.decryptPassword(password);
			map.set("em_password", password);
			sqls.add(map);
		}
		baseDao.batchExecute(sqls, new ArrayList<String>());
	}
	
	/**
	 * 更新是否启用状态
	 * @param value
	 */
	private void setConfigs(String value){
		//更新configs表字段信息
		int count = baseDao.getCount("select count(1) from configs where caller = 'sys' and code = 'encryConfig'");
		if(count > 0){
			baseDao.updateByCondition("configs", "data = '" + value + "'", "caller = 'sys' and code = 'encryConfig'");
		}else{
			int id = baseDao.getSeqId("configs_seq");
			String sql = "insert into configs (code,title,data_type,data,class_,method,caller,dbfind,multi,id,editable,help) "
					+ "values('encryConfig','已启用密码加密','YN',"+value+",null,null,'sys',null,0,"+id+",0,'是否已启用密码加密')";
			baseDao.execute(sql);
		}
	}
	
}
