package com.uas.erp.service.ma.impl;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.api.crypto.util.FlexJsonUtils;
import com.uas.b2b.model.TenderAttach;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Employee;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.FilePathService;
import com.uas.erp.service.common.FormAttachService;
import com.uas.sso.entity.AppCreateInfo;
import com.uas.sso.entity.UserSpaceView;
import com.uas.sso.entity.UserView;
import com.uas.sso.util.AccountUtils;
@Service
public class CreateAccountBookImpl implements com.uas.erp.service.ma.CreateAccountBook{
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private FilePathService filePathService;
	
	@Autowired
	private FormAttachService formAttachService;
	
	@Autowired
	private EmployeeService employeeService;
	
	@Override
	public JSONObject validBusinessCode(String businessCode) {
		boolean success = false;
		String name = "";
		String exceptionInfo = "";
		try {
			String str = AccountUtils.validBusinessCode(businessCode);
			AppCreateInfo appCreateInfo = strToAppCreateInfo(str);
			name = appCreateInfo.getName();
			success = true;
		} catch (Exception e) {
			exceptionInfo = e.getMessage();
			e.printStackTrace();
		}
		JSONObject res = new JSONObject();
		res.put("success", success);
		res.put("exceptionInfo", exceptionInfo);
		res.put("companyName", name);
		return res;
	}

	@Override
	public JSONObject validBusinessName(String businessName) {
		boolean success = false;
		boolean inCloud = false;
		String uuid = "";
		String businessCode = "";
		boolean hasCreate = false;
		String exceptionInfo = "";
		try {
			String str = AccountUtils.validName(businessName);
			AppCreateInfo appCreateInfo = strToAppCreateInfo(str);
			inCloud = appCreateInfo.isInCloud();
			uuid = appCreateInfo.getEnuu();
			businessCode = appCreateInfo.getBusinessCode();
			hasCreate = appCreateInfo.isHasCreate();
			success = true;
		} catch (Exception e) {
			exceptionInfo = e.getMessage();
			e.printStackTrace();
		}
		JSONObject res = new JSONObject();
		res.put("success", success);
		res.put("inCloud", inCloud);
		res.put("uuid", uuid);
		res.put("businessCode", businessCode);
		res.put("hasCreate", hasCreate);
		res.put("exceptionInfo", exceptionInfo);
		return res;
	}

	@Override
	public JSONObject applyCloud(Map<Object, Object> businessInfo, Map<Object, Object> accountInfo) {
		boolean success = false;
		String exceptionInfo = "";
		boolean inCloud = false;
		String uuid = "";
		JSONObject res = new JSONObject();
		res.put("success", success);
		
		JSONObject validBusinessName = validBusinessName(businessInfo.get("companyName").toString());
		if((Boolean) validBusinessName.get("inCloud")){
			res.put("inCloud", true);
			res.put("uuid", validBusinessName.get("uuid"));
			return res;
		}
		
		UserSpaceView userSpaceView = new UserSpaceView();
		userSpaceView.setSpaceName((String) businessInfo.get("companyName"));
		userSpaceView.setRegAddress((String) businessInfo.get("companyAddr"));
		userSpaceView.setBusinessCode((String) businessInfo.get("licenseNo"));
		userSpaceView.setBusinessCodeImage(businessInfo.get("licensePath").toString());
		userSpaceView.setAdmin(getAdmin((String) businessInfo.get("managerPhone")));
		List<UserView> users = null;
		if(accountInfo.get("EMPLOYEE").equals("1")) {
			users = getEmployees();
		}
		try {
			String str = AccountUtils.applyApp(userSpaceView, users);
			AppCreateInfo appCreateInfo = strToAppCreateInfo(str);
			inCloud = appCreateInfo.isInCloud();
			uuid = appCreateInfo.getEnuu();
			
			success = true;
		} catch (Exception e) {
			exceptionInfo = e.getMessage();
			e.printStackTrace();
		}
		
		res.put("exceptionInfo", exceptionInfo);
		res.put("inCloud", inCloud);
		res.put("uuid", uuid);
		return res;
	}

	public TenderAttach getAttach(String id) {
		TenderAttach tAttach = new TenderAttach();
		Object basePath = baseDao.getFieldDataByCondition("Enterprise", "en_erpurl", "en_id="+SystemSession.getUser().getEm_enid());
		if (id!=null&&!"".equals(id)) {
			JSONArray files = formAttachService.getFiles(id);
			if (files.size() > 0) {
				JSONObject obj = files.getJSONObject(0);
				Long Id = obj.getLong("fp_id");
				String path = obj.getString("fp_path");
				Integer size = obj.getInt("fp_size");
				String name = obj.getString("fp_name");
				if (path.startsWith("B2B://")) {// 文件在云平台
					tAttach.setId(Long.parseLong((path.substring(11))));
					tAttach.setName(name);
					tAttach.setSize(size);
				} else if (path.startsWith("http:")||path.startsWith("https:")|| path.startsWith("ftp:") || path.startsWith("sftp:")) {// 文件存放在文件系统
					tAttach.setName(name);
					tAttach.setSize(size);
					tAttach.setPath(path);
				} else {
					tAttach.setName(name);
					tAttach.setSize(size);
					if (basePath.toString().endsWith("/")) {
						tAttach.setPath(basePath+"common/downloadbyId.action?id="+Id);
					}else {
						tAttach.setPath(basePath+"/common/downloadbyId.action?id="+Id);
					}
					
				}
			}
		}
		return tAttach;
	}
	/**
	 * 通过手机号获取管理员信息
	 * @param mobile
	 * @return
	 */
	private UserView getAdmin(String mobile){
		Employee employee = employeeService.getEmployeeByEmTel(mobile);
		UserView user = new UserView();
		if(employee!=null){
			user.setMobile(employee.getEm_mobile());
			user.setEmail(employee.getEm_email());
			user.setVipName(employee.getEm_name());
			user.setIdCard(employee.getEm_iccode());
		}
		return user;
	}
	
	public List<UserView> getEmployees() {
		List<UserView> users = new ArrayList<UserView>();
		try{
			String querySql = "select * from EMPLOYEE";
			List<Employee> result = baseDao.getJdbcTemplate().query(querySql, new BeanPropertyRowMapper<Employee>(Employee.class));
			for(Employee e: result) {
				UserView user = new UserView();
				user.setMobile(e.getEm_mobile());
				user.setEmail(e.getEm_email());
				user.setVipName(e.getEm_name());
				user.setIdCard(e.getEm_iccode());
				
				users.add(user);
			}
		}catch(EmptyResultDataAccessException e){
			return null;
		}
		return users;
	}
	
	private AppCreateInfo strToAppCreateInfo(String str) throws UnsupportedEncodingException {
		AppCreateInfo appCreateInfo = FlexJsonUtils.fromJson(str, AppCreateInfo.class);
		return appCreateInfo;
	}

	@Override
	public JSONObject saveAccountInfo(HttpSession session, Map<Object, Object> businessInfo, Map<Object, Object> accountInfo) {
		boolean success = false;
		String exceptionInfo = "";
		JSONObject data = new JSONObject();
		
		try{
			String ML_RECORDER = session.getAttribute("em_name").toString();
			String ML_RECORDERTEL = businessInfo.get("managerPhone").toString();
			String ML_RECORDEREMAIL = businessInfo.get("managerEmail").toString();
			String ML_RECORDERID = session.getAttribute("em_uu").toString();
			String ML_SOURCE = accountInfo.get("refer_sys").toString();
			String ML_ACOUNTDATE = DateUtil.parseDateToOracleString("yyyy-mm-dd",accountInfo.get("fa_account_period").toString());
			String ML_TABLES = getTables(accountInfo);
			String ML_BUSINESSUUID = accountInfo.get("uuid").toString();
			String ML_BUSINESSNAME = businessInfo.get("companyName").toString();
			String ML_BUSINESSSHORTNAME = businessInfo.get("companyShortName").toString();
			String ML_LICENSE = businessInfo.get("licensePath").toString();
			String ML_LICENSENO = businessInfo.get("licenseNo").toString();
			String ML_ADDR = businessInfo.get("companyAddr").toString();
			String ML_ACCOUNTDESC = businessInfo.get("companyShortName").toString();
			String ML_ACCOUNTNAME = getSpell(businessInfo.get("companyShortName").toString());
			String ML_PASSWORD = "111111";
			
			List<JSONObject> info = getAccountInfo(" ML_RECORDERID = " + ML_RECORDERID + " AND ML_ACTIVE = 'false'");
			if(info.size() == 1){
				JSONObject res = info.get(0);
				String ML_ID = res.get("ML_ID").toString();
				
				baseDao.updateByCondition(" MASTERAPPLYLOG",
						" ML_RECORDDATE = (select sysdate from dual), "
						+ " ML_SOURCE = '"+ML_SOURCE + "',"
						+ " ML_ACOUNTDATE = "+ML_ACOUNTDATE + ","
						+ " ML_TABLES = '"+ML_TABLES+"',"
						+ " ML_RECORDERTEL = '"+ML_RECORDERTEL+"',"
						+ " ML_RECORDEREMAIL = '"+ML_RECORDEREMAIL+"',"
						+ " ML_BUSINESSUUID = '"+ML_BUSINESSUUID+"',"
						+ " ML_BUSINESSNAME = '"+ML_BUSINESSNAME+"',"
						+ " ML_BUSINESSSHORTNAME = '"+ML_BUSINESSSHORTNAME+"',"
						+ " ML_LICENSE = '"+ML_LICENSE+"',"
						+ " ML_LICENSENO = '"+ML_LICENSENO+"',"
						+ " ML_ADDR = '"+ML_ADDR+"'"
						+ " ML_ACCOUNTDESC = '"+ML_ACCOUNTDESC+"'"
						+ " ML_ACCOUNTNAME = '"+ML_ACCOUNTNAME+"'"
						+ " ML_PASSWORD = '"+ML_PASSWORD+"'",
						" ML_ID=" + ML_ID);
			}else {
				baseDao.execute(" INSERT INTO MASTERAPPLYLOG "
						+ " (ML_RECORDER,ML_RECORDERTEL,ML_RECORDEREMAIL,ML_RECORDERID,ML_RECORDDATE,ML_SOURCE,ML_TABLES,ML_ACOUNTDATE,"
						+ " ML_BUSINESSUUID,ML_ACCOUNTID,ML_ACTIVE,ML_BUSINESSNAME,ML_BUSINESSSHORTNAME,"
						+ " ML_LICENSE,ML_LICENSENO,ML_ADDR,ML_ACCOUNTDESC,ML_ACCOUNTNAME,ML_PASSWORD) " 
						+ " VALUES('"+ML_RECORDER+"','"+ML_RECORDERTEL+"','"+ML_RECORDEREMAIL+"','"+ML_RECORDERID+"',(select sysdate from dual),"
						+ " '"+ML_SOURCE+"','"+ML_TABLES+"',"+ML_ACOUNTDATE+","
						+ " '"+"','"+"','"+false+"','"+ML_BUSINESSNAME+"',"
						+ " '"+ML_BUSINESSSHORTNAME+"','"+ML_LICENSE+"','"+ML_LICENSENO+"','"+ML_ADDR+"',"
						+ " '"+ML_ACCOUNTDESC+"','"+ML_ACCOUNTNAME+"','"+ML_PASSWORD+"')");
			}
			
			List<JSONObject> ninfo = getAccountInfo(" ML_RECORDERID = " + ML_RECORDERID + " AND ML_ACTIVE = 'false'");
			JSONObject nres = ninfo.get(0);
			
			data.put("newAccountBookDesc",nres.get("ML_ACCOUNTDESC").toString());
			data.put("newAccountBookName",nres.get("ML_ACCOUNTNAME").toString());
			data.put("managerID",nres.get("ML_RECORDERTEL").toString());
			data.put("managerName",nres.get("ML_RECORDER").toString());
			data.put("managerPassword",nres.get("ML_PASSWORD").toString());
			data.put("newAccountBookID",nres.get("ML_ID").toString());

			success = true;
		}catch(Exception e) {
			e.printStackTrace();
			exceptionInfo = e.getMessage();
		}
		
		JSONObject res = new JSONObject();
		
		res.put("success", success);
		res.put("data", data);
		res.put("exceptionInfo", exceptionInfo);
		return res;
	}
	
	private String getTables(Map<Object, Object> accountInfo) {
		String tables = " ";
		
		Iterator<Map.Entry<Object, Object>> entries = accountInfo.entrySet().iterator();
		while(entries.hasNext()) {
			Map.Entry<Object, Object> entry = entries.next();
			if(!entry.getValue().equals("0")) {
				if(!entry.getKey().equals("uuid") && !entry.getKey().equals("refer_sys") && !entry.getKey().equals("fa_account_period")) {
					tables += entry.getKey() + ",";
				}
			}
		}
		tables = tables.substring(0,tables.length() - 1).trim();
		
		return tables;
	}

	public List<JSONObject> getAccountInfo(String condition) {
		String[] fields = new String[]{"ML_RECORDER", "ML_RECORDERID", "ML_RECORDERTEL", "ML_RECORDEREMAIL", "ML_RECORDDATE", "ML_SOURCE", "ML_TABLES", "ML_ACOUNTDATE", "ML_ID", "ML_BUSINESSUUID", "ML_ACCOUNTID", "ML_ACTIVE", "ML_BUSINESSNAME", "ML_BUSINESSSHORTNAME", "ML_LICENSE", "ML_LICENSENO", "ML_ADDR", "ML_ACCOUNTDESC", "ML_ACCOUNTNAME", "ML_PASSWORD"};
		
		List<JSONObject> result = baseDao.getFieldsJSONDatasByCondition("MASTERAPPLYLOG", fields, condition);
		return result;
	}

	@Override
	public JSONObject getStep(HttpSession session) {
		String recorderID = session.getAttribute("em_uu").toString();
		
		List<JSONObject> result = getAccountInfo(" ML_RECORDERID = " + recorderID + " AND ML_ACTIVE = 'false'");
		JSONObject res = new JSONObject();
		res.put("success", "success");
		if(result.size() == 0) {
			res.put("data", null);
			return res;
		}
		JSONObject r = result.get(0);
		String licensePath = r.get("ML_LICENSE").toString();
		TenderAttach attach = getAttach(licensePath);
		r.put("ML_LICENSENAME", attach.getName());
		
		res.put("data", r);
		return res;
	}

	@Override
	public JSONObject active(String accountID) {
		JSONObject res = new JSONObject();
		try{
			String ML_ID = accountID;
			baseDao.updateByCondition(" MASTERAPPLYLOG",
					" ML_ACTIVE = 'true' ",
					" ML_ID=" + ML_ID);
			res.put("success", "success");
		}catch(Exception e) {
			res.put("exceptionInfo", e.getMessage());
		}
		
		return res;
	}
	
	@Override
	public JSONObject getSource() {
		String sp = SpObserver.getSp();
		JSONObject res = new JSONObject();
		String[] fields = new String[]{"MA_NAME", "MA_FUNCTION"};
		String condition = " MA_NAME='" + sp + "'";
		List<JSONObject> result = baseDao.getFieldsJSONDatasByCondition("master", fields, condition);
		
		res.put("sourceId", result.get(0).get("MA_NAME"));
		res.put("sourceName", result.get(0).get("MA_FUNCTION"));
		return res;
	}
	
	/**
     * 得到 全拼
     * 
     * @param src
     * @return
     */
    public static String getSpell(String src) {
        char[] t1 = null;
        t1 = src.toCharArray();
        String[] t2 = new String[t1.length];
        HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
        t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        t3.setVCharType(HanyuPinyinVCharType.WITH_V);
        String t4 = "";
        int t0 = t1.length;
        try {
            for (int i = 0; i < t0; i++) {
                // 判断是否为汉字字符
                if (java.lang.Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
                    t4 += t2[0];
                } else {
                    t4 += java.lang.Character.toString(t1[i]);
                }
            }
            return t4;
        } catch (BadHanyuPinyinOutputFormatCombination e1) {
            e1.printStackTrace();
        }
        return t4;
    }
	
}
