package com.uas.erp.core;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Employee;

/**
 * 密码加密解密
 * @author lidy
 * @since 2018-1-30
 *
 */
public class PasswordEncryUtil {
	//人员资料 密码  des加密默认key
	private final static String key = "96878265";
	
	/**
	 * 获取加密工具类DesUtil
	 * @return
	 * @throws Exception
	 */
	private static DesUtil getDesUtil() throws Exception{
		return new DesUtil(key);
	}
	
	/**
	 * 获取系统参数配置（是否启用密码加密）
	 * @return true:启用加密  ，false:不启用加密
	 */
	private static boolean getEncryConfig(){
		String sob = SpObserver.getSp();
		//切换默认账套进行查询
		SpObserver.putSp(BaseUtil.getXmlSetting("defaultSob"));
		
		BaseDao baseDao = (BaseDao) ContextUtil.getBean("baseDao");
		Object encryConfig = baseDao.getFieldDataByCondition("configs", "data", "caller='sys' and code='encryConfig'");
		//切换回原来的账套
		SpObserver.putSp(sob);
		
		if(encryConfig!=null&&encryConfig.equals("1")){
			return true;
		}else {
			return false;
		}
	}
	/**
	 * 给密码加盐
	 * @param password 密码
	 * @param mobile 手机号
	 * @return
	 */
	private static String setPasswordSalt(String password,String mobile){
		return mobile+"{"+password+"}";
	}
	
	/**
	 * 获取密码
	 * @param password
	 * @return
	 */
	private static String removePasswordSalt(String password){
		return password.substring(password.indexOf("{")+1, password.length()-1);
	}
	/**
	 * 对密码进行加密
	 * @param password 密码
	 * @param mobile   手机号
	 * @return 加密后的密码
	 * @throws Exception
	 */
	public static String encryptPassword(String password,String mobile){
		if(getEncryConfig()){			
			try {
				return getDesUtil().encrypt(setPasswordSalt(password,mobile));
			} catch (Exception e) {
				BaseUtil.showError(e.getMessage());
			}
			return password;
		}else{
			return password;
		}
	}
	
	/**
	 * 对密码进行解密
	 * @param password 加密的密码
	 * @return 明文密码
	 * @throws Exception
	 */
	public  static String decryptPassword(String password){
		if(getEncryConfig()){
			try {
				String pass = getDesUtil().decrypt(password);
				return removePasswordSalt(pass);
			} catch (Exception e) {
			}
			return password;
		}else{
			return password;
		}
	}
	/**
	 * 对密码进行解密
	 * @param employee 需要解密的employee实体类
	 * @return 密码已经解密的employee实体类
	 * @throws Exception
	 */
	public static Employee decryptEmployeePassword(Employee employee){
		employee.setEm_password(decryptPassword(employee.getEm_password()));
		return employee;
	}
}
