package com.uas.erp.service.ma.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.uas.b2b.model.Enterprise;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.PasswordEncryUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.dao.common.EnterpriseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.ma.EnterpriseService;
import com.uas.sso.AccountConfig;
import com.uas.sso.entity.UserSpaceView;
import com.uas.sso.entity.UserView;

@Service
public class EnterpriseServiceImpl implements EnterpriseService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private EnterpriseDao enterpriseDao;
	@Autowired
	private EmployeeDao employeeDao;
	static BASE64Encoder encoder = new sun.misc.BASE64Encoder();
	static BASE64Decoder decoder = new sun.misc.BASE64Decoder();

	@Override
	public void updateEnterpriseById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//企业名称、营业执照不能为空
		if(store.get("en_name")==null||store.get("en_name").toString().trim().equals("")){
			BaseUtil.showError("企业名称不能为空!");
		}
		if(store.get("en_businesscode")==null||store.get("en_businesscode").toString().trim().equals("")){
			BaseUtil.showError("营业执照不能为空!");
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		if (store.get("en_admin") != null && "pdf".equals(store.get("en_admin"))) {
			if ((store.get("en_printurl") != null && !"".equals(store.get("en_printurl")) && !store.get("en_printurl").toString().contains("/print_uas")) || (store.get("en_url") != null && !"".equals(store.get("en_url")) && !store.get("en_url").toString().contains("/print_uas"))) {
				BaseUtil.showError("打印用：【pdf】时，打印地址应包含【/print_uas】,请修改【打印地址】或【打印用】不选择pdf");
			}
		}
		int count = baseDao.getCount("select count(*) from employee where em_name='" + store.get("en_adminname") + "' and nvl(em_mobile,' ')<>' '");
		
		//设置企业办公地址
		SqlRowList rs = baseDao.queryForRowSet("select en_shortname,en_address from enterprise where en_id=" + store.get("en_id"));
		if(rs.next()){
			if(rs.getString("en_shortname")!=null&&!"".equals(rs.getString("en_shortname"))&&rs.getString("en_address")!=null&&!"".equals(rs.getString("en_address"))){
				generateComaddressset(rs.getString("en_shortname"),rs.getString("en_address"),store);	
			}
		}
		store.put("en_whichsystem", SystemSession.getUser().getCurrentMaster().getMa_name());
		if (count == 0) {
			String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Enterprise", "en_id");
			baseDao.execute(formSql);
		} else {
			Object[] objs = employeeDao.getFieldsEmployeeByCondition(new String[] {"em_mobile","em_password","em_uu","em_id","em_email"}, "nvl(em_name,' ')='" + store.get("en_adminname").toString() + "'");
			if (objs != null) {
				store.remove("en_adminuupassword");
				store.remove("en_adminuu");
				store.remove("en_adminphone");
				store.put("en_adminphone", objs[0]);
				store.put("en_adminuupassword", objs[1]);
				store.put("en_adminuu", objs[2]);
				String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Enterprise", "en_id");
				baseDao.execute(formSql);
				//改为使用新版账号中心的接口进行企业信息的更新和获取UU号
				Map<String, Object> backInfo = updateEnterpriseToAccountCenter(store.get("en_name").toString(), store.get("en_businesscode").toString(), String.valueOf(objs[0]), StringUtil.nvl(objs[4], null), String.valueOf(objs[1]), StringUtil.nvl(store.get("en_adminname"), null),StringUtil.nvl(objs[2], null),StringUtil.nvl(store.get("en_erpurl"), null));
				if (backInfo != null && backInfo.get("success")!=null && backInfo.get("success").equals(true) && backInfo.get("content") != null) {
					// 更新企业资料的企业UU号
					Map<String, Object> content = (Map<String, Object>) backInfo.get("content");
					baseDao.execute("update enterprise set en_uu='" + content.get("spaceUU") + "' where en_name='" + store.get("en_name").toString() + "'");
					String sob = BaseUtil.getXmlSetting("defaultSob");// 默认账套
					String ma_user = SystemSession.getUser().getEm_master();// 当前账套MA_ACCESSSECRET
					//更新企业UU号和企业密钥
					baseDao.execute("update " + sob + ".master set "
							+ "(ma_uu,ma_accesssecret) "
							+ "=(select en_uu,'" + content.get("accessSecret") + "' from  " + "Enterprise where en_id=" + store.get("en_id") + ") where ma_user='" + ma_user + "'");
					String ma_env = "test";
					if (store.get("en_b2benable") != null && store.get("en_b2bwebsite") != null) {// 配置了是否启用B2B,与B2B网址
						if (store.get("en_b2bwebsite").equals("http://uas.ubtob.com")) {
							ma_env = "prod";
						}
						baseDao.execute("update " + sob + ".master set (ma_B2benable,ma_B2bwebsite,ma_env) =(select  abs(nvl(En_B2benable,0)),nvl(En_B2bwebsite,' '),'" + ma_env + "' from  " + "Enterprise where en_id=" + store.get("en_id") + ") where ma_user='" + ma_user + "'");// 将是否启用B2B,与B2B网址更新到默认账套master表
					}
					// 同步管理员信息到后台管理以及平台
					/*
					 * String log=vastOpenMobile(objs[3]); if(log!=null){
					 * BaseUtil.showError(log); }
					 */
					// 根据供应商名称批量获取供应商UU信息
					vastOpenVendorUU();
					// 根据客户名称批量获取客户UU信息
					vastOpenCustUU();
					// 记录操作
					baseDao.logger.update(caller, "en_id", store.get("en_id"));
					// 执行修改后的其它逻辑
				} else if (backInfo != null && backInfo.get("error")!=null && backInfo.get("error").equals(true) && backInfo.get("errMsg") != null) {
					BaseUtil.showError(backInfo.get("errMsg").toString());
				} else {
					BaseUtil.showError("同步企业信息到账户中心失败");
				}
			} else {
				BaseUtil.showError("企业信息或者管理员名称在人事信息中不存在验证不通过,不能开通平台!");
			}
		}
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	/**
	 * 通过账号中心接口修改企业信息
	 * 1、如果企业UU号不存在，则通过企业名称和营业执照号 获取企业UU、企业密钥
	 * 2、如果企业UU号、企业密钥存在，且通过账号中心校验，则更新企业信息（企业名称、营业执照号、ERP外网地址、管理员信息）
	 * @param en_name  企业名称
	 * @param en_businesscode  营业执照号
	 * @param em_mobile  管理员手机号
	 * @param em_email  管理员邮箱
	 * @param em_password  管理员密码
	 * @param en_adminname  管理员姓名
	 * @param en_erpurl  ERP外网地址
	 * @return
	 */
	private Map<String, Object> updateEnterpriseToAccountCenter(String en_name, String en_businesscode, String em_mobile, String em_email, String em_password, String en_adminname,String em_uu, String en_erpurl){
		if(em_mobile==null||em_mobile.matches("")){
			BaseUtil.showError("管理员手机号格式不正确,无法更新企业信息到优软云");
		}else{
			UserSpaceView userSpace = new UserSpaceView();
			UserView admin = new UserView();
			userSpace.setSpaceName(en_name);
			userSpace.setBusinessCode(en_businesscode);
			userSpace.setWebsite(en_erpurl);
			if(em_uu!=null&&!em_uu.equals("0")){
				admin.setUserUU(Long.parseLong(em_uu));
			}
			admin.setEmail(em_email);
			admin.setMobile(em_mobile);
			admin.setPassword(em_password);
			admin.setVipName(en_adminname);
			Master master = SystemSession.getUser().getCurrentMaster();
			if((master.getMa_accesssecret()!=null&&!master.getMa_accesssecret().equals(""))
				&&(master.getMa_uu()!=null&&master.getMa_uu()!=0)){
				//如果企业UU和企业密钥都不为空，则把企业UU传过去
				userSpace.setSpaceUU(master.getMa_uu());
			}
			userSpace.setAdmin(admin);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJson(userSpace));
			try {
				Response response = HttpUtil.sendPostRequest(AccountConfig.getSpaceSaveUrl()+"/erp/updateSpace", params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					Map<String, Object> backInfo = FlexJsonUtil.fromJson(response.getResponseText(), HashMap.class);
					if (backInfo != null) {
						return backInfo;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static Map<String, Object> checkEnterPrise(String en_name, String en_businesscode, String em_mobile, String em_password, String en_adminname) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("enName", en_name);
		params.put("enBusinesscode", en_businesscode);
		params.put("emMobile", em_mobile);
		params.put("emPassword", em_password);
		params.put("emName", en_adminname);
		try {
			Response response = HttpUtil.sendGetRequest("http://uas.ubtob.com/public/queriable/members/UserEnterprise", params, false);
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				Map<String, Object> backInfo = FlexJsonUtil.fromJson(response.getResponseText(), HashMap.class);
				if (backInfo != null) {
					return backInfo;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void regB2BEnterprise(int enid) {
		Enterprise enterprise = new Enterprise();
		List<String> sqls = new ArrayList<String>();
		Long ma_masterid = SystemSession.getUser().getCurrentMaster().getMa_manageid();
		Object[] objs = baseDao.getFieldsDataByCondition("enterprise left join employee on en_adminname=em_name", "en_name,en_shortname,en_businesscode,en_adminname,en_adminphone,em_email,em_password", "en_id=" + enid);
		if (objs != null) {
			if (objs[0] == null || objs[1] == null || objs[2] == null) {
				BaseUtil.showError("企业名称、简称、营业执照号都不能为空，请检查后再设置开通!");
			} else {
				if (objs[3] == null || objs[4] == null || objs[5] == null) {
					BaseUtil.showError("企业管理员名称以及管理员人事信息中手机号与邮箱都不能为空，请检查后再设置开通!");
				} else {
					enterprise.setEnName(objs[0].toString());
					enterprise.setEnShortname(objs[1].toString());
					enterprise.setEnBussinessCode(objs[2].toString());
					enterprise.setEnAdminName(objs[3].toString());
					enterprise.setEnAdminTel(objs[4].toString());
					enterprise.setEnAdminEmail(objs[5].toString());
					//密码解密
					enterprise.setEnAdminPassword(PasswordEncryUtil.decryptPassword(objs[6].toString()));
					enterprise.setEnMasterId(ma_masterid);
					if (enterprise != null) {
						Map<String, Object> regInfos = regEnterprise(enterprise);
						if (regInfos != null && regInfos.get("ok").equals(true)) {
							sqls.add("update enterprise set en_uu='" + regInfos.get("enUU") + "',en_adminuu='" + regInfos.get("emUU") + "',en_b2benable=-1,en_b2bwebsite='http://uas.ubtob.com' where en_id=" + enid);
							sqls.add("update employee set em_uu='" + regInfos.get("emUU") + "' where em_name='" + objs[3].toString() + "'");
						} else {
							BaseUtil.showError(regInfos.get("error").toString());
						}
						if (sqls.size() > 0) {
							baseDao.execute(sqls);
						}
						// 注册后直接开通平台数据传输
						String sob = BaseUtil.getXmlSetting("defaultSob");// 默认账套
						String ma_user = SystemSession.getUser().getEm_master();// 当前账套MA_ACCESSSECRET
						Object[] masterInfo = baseDao.getFieldsDataByCondition("enterprise", "en_name,en_uu,en_b2benable,en_b2bwebsite", "en_id=" + enid);
						if (masterInfo != null) {// 配置了是否启用B2B,与B2B网址
							baseDao.execute("update " + sob + ".master set (ma_B2benable,ma_B2bwebsite,ma_uu,ma_accesssecret) =(select  abs(nvl(en_b2benable,0)),nvl(En_b2bwebsite,' '),en_uu,'" + regInfos.get("enSecret") + "' from  " + "Enterprise where en_id=" + enid + ") where ma_user='" + ma_user + "'");// 将是否启用B2B,与B2B网址更新到默认账套master表
						}
						// 根据供应商名称批量获取供应商UU信息
						vastOpenVendorUU();
						// 根据客户名称批量获取客户UU信息
						vastOpenCustUU();
					}
				}
			}
		}
	}

	public Map<String, Object> regEnterprise(Enterprise enterprise) {
		if (enterprise != null) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("data", FlexJsonUtil.toJson(enterprise));
			try {
				Response response = HttpUtil.sendPostRequest("http://uas.ubtob.com/public/queriable/erpRegister", params, false);
				/*
				 * Response response = HttpUtil.sendPostRequest(
				 * "http://localhost:8080/platform-b2b/public/queriable/erpRegister"
				 * , params,false);
				 */
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					Map<String, Object> backInfo = FlexJsonUtil.fromJson(response.getResponseText(), HashMap.class);
					if (backInfo != null) {
						return backInfo;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} finally {

			}
		}
		return null;
	}

	public void vastOpenVendorUU() {
		StringBuffer log = new StringBuffer();
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.getMa_b2bwebsite() != null) {
			List<Object> objs = baseDao.getFieldDatasByCondition("Vendor", "ve_name", " nvl(ve_uu,' ')=' ' and ve_auditstatuscode='AUDITED' and nvl(ve_b2benable,0)=0");
			List<String> sqls = new ArrayList<String>();
			if (objs != null) {
				for (Object m : objs) {
					if (m != null) {
						HashMap<String, String> params = new HashMap<String, String>();
						params.put("data", m.toString());
						try {
							Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/public/queriable/batch/members", params, false);
							if (response.getStatusCode() == HttpStatus.OK.value()) {
								//Map<String, Object> backInfo = FlexJsonUtil.fromJson(response.getResponseText(), HashMap.class);
								//接口返回对象修改导致更新出错
								JSONObject  backInfo=JSONObject.fromObject(response.getResponseText());				
								if (backInfo!=null && !backInfo.isNullObject()) {
									JSONObject o=backInfo.getJSONObject(m.toString());
									int count = baseDao.getCount("select count(*) from vendor where nvl(ve_uu,' ')='" + o.get("uu") +"'");
									if (count == 0) {
									  sqls.add("update vendor set ve_emailkf='已获取', ve_uu=" +  o.get("uu") + ",ve_b2benable=1 where ve_name='" + m + "'");
									}
								} else {
									log.append(m + ",");
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
			}
			if (log.length() > 1) {
				log.append("更新失败");
			}
			if (sqls.size() > 0) {
				baseDao.execute(sqls);
			}
		}
		// System.out.println("vendor:"+log.toString()) ;
	}

	public void vastOpenCustUU() {
		StringBuffer log = new StringBuffer();
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.getMa_b2bwebsite() != null) {
			List<Object> objs = baseDao.getFieldDatasByCondition("Customer", "cu_name", " nvl(cu_uu,0)=0 and cu_auditstatuscode='AUDITED' and nvl(cu_b2benable,0)=0");
			List<String> sqls = new ArrayList<String>();
			if (objs != null) {
				for (Object m : objs) {
					if (m != null) {
						HashMap<String, String> params = new HashMap<String, String>();
						params.put("data", m.toString());
						try {
							Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/public/queriable/batch/members", params, false);
							if (response.getStatusCode() == HttpStatus.OK.value()) {
								Map<String, Object> backInfo = FlexJsonUtil.fromJson(response.getResponseText(), HashMap.class);
								if (backInfo.size() > 0) {
									for (String name : backInfo.keySet()) {
										int count = baseDao.getCount("select count(*) from customer where nvl(cu_uu,0)=" + backInfo.get(name) + "");
										if (count == 0) {
											sqls.add("update customer set cu_checkuustatus='已获取', cu_uu=" + backInfo.get(name) + ",cu_b2benable=1 where cu_name='" + name + "'");
										}
									}
								} else {
									log.append(m + ",");
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
			}
			if (log.length() > 1) {
				log.append("更新失败");
			}
			if (sqls.size() > 0) {
				baseDao.execute(sqls);
			}
		}
	}

	@Override
	public void saveLogo(MultipartFile file) {
		try {
			final byte[] bytes = file.getBytes();
			baseDao.saveBlob("enterprise", "en_logo", bytes, "1=1");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Blob getLogo() {
		return enterpriseDao.getLogo();
	}

	@Override
	public boolean hasLogo() {
		try {
			return baseDao.checkIf("enterprise", "en_logo is not null");
		} catch (Exception e) {
			return false;
		}
	}

	@CacheEvict(value = "master", allEntries = true)
	@Override
	public void setMasterInfo(String param, String caller) {

		String current_master = SpObserver.getSp();
		/* 根据默认账套的ma_name获取ma_function */
		String defaultSob_ma_name = BaseUtil.getXmlSetting("defaultSob");
		SpObserver.putSp(defaultSob_ma_name);
		Object defaultSob_ma_function = baseDao.getFieldDataByCondition("Master", "ma_function", "ma_name='" + defaultSob_ma_name + "'");
		SpObserver.putSp(current_master);
		/* 判断当前账套是不是主账套 */
		if (!current_master.equals(BaseUtil.getXmlSetting("defaultSob"))) {
			BaseUtil.showError("账套操作只允许在主账套(" + defaultSob_ma_function + ")下进行");
		}
		final List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param);

		for (Map<Object, Object> map : grid) {
			Object ma_id = baseDao.getFieldDataByCondition("master", "ma_id", "ma_function='" + map.get("ma_function") + "'");
			if (ma_id != null) {
				// 判断根据查出的ma_id和传入的是否相同来判断这个账套名称是否修改过，如果相同则表示未修改
				if (!map.get("ma_id").toString().equals(ma_id.toString())) {
					BaseUtil.showError("已存在账套名称为" + map.get("ma_function") + "的账套");
				}
			}
		}
		baseDao.getJdbcTemplate().batchUpdate("update master set ma_function=?, ma_enable=? where ma_id=?", new BatchPreparedStatementSetter() {
			@Override
			public int getBatchSize() {
				return grid.size();
			}

			@Override
			public void setValues(PreparedStatement arg0, int arg1) throws SQLException {
				String default_sob = BaseUtil.getXmlSetting("defaultSob");
				int en_able = 1;
				// 判断传入的值是false也就是禁用的时候判断是不是主账套，如果是主账套则不禁用
				if (grid.get(arg1).get("ma_enable").equals(false) && !grid.get(arg1).get("ma_name").equals(default_sob)) {
					en_able = 0;
				}
				arg0.setString(1, (String) grid.get(arg1).get("ma_function"));
				arg0.setInt(2, en_able);
				arg0.setInt(3, (Integer) grid.get(arg1).get("ma_id"));
			}
		});
		// 记录操作日志
		List<String> sqls = new ArrayList<String>();
		for (Map<Object, Object> map : grid) {
			if (map.get("ma_enable").equals(false)) {
				sqls.add((baseDao.logger.getMessageLog("禁用账套" + map.get("ma_function"), "成功", caller, "ma_id", map.get("ma_id"))).getSql());
			} else {
				sqls.add((baseDao.logger.getMessageLog("启用账套" + map.get("ma_function"), "成功", caller, "ma_id", map.get("ma_id"))).getSql());
			}
		}
		baseDao.execute(sqls);
	}

	/**
	 * saas初始化更新企业信息
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saasupdateEnterpriseById(String formStore, String caller) {
		// TODO Auto-generated method stub
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		String enshortname = store.get("en_shortname").toString();
		String enaddress = store.get("en_address").toString();
		
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		/*if (store.get("en_admin") != null && "pdf".equals(store.get("en_admin"))) {
			if ((store.get("en_printurl") != null && !"".equals(store.get("en_printurl")) && !store.get("en_printurl").toString().contains("/print_uas")) || (store.get("en_url") != null && !"".equals(store.get("en_url")) && !store.get("en_url").toString().contains("/print_uas"))) {
				BaseUtil.showError("打印用：【pdf】时，打印地址应包含【/print_uas】,请修改【打印地址】或【打印用】不选择pdf");
			}
		}*/
		int count = baseDao.getCount("select count(*) from employee where em_name='" + store.get("en_adminname") + "' and nvl(em_mobile,' ')<>' '");
		if (count == 0) {
			//插入企业办公地址设置			
			generateComaddressset(enshortname,enaddress,store);
			
			String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Enterprise", "en_id");
			baseDao.execute(formSql);
		} else {
			//判断是否有企业办公地址设置
			SqlRowList rs = baseDao.queryForRowSet("select en_shortname,en_address from enterprise where en_id=" + store.get("en_id"));
			if(rs.next()){
				generateComaddressset(rs.getString("en_shortname"),rs.getString("en_address"),store);		
			}
			Object[] objs = employeeDao.getFieldsEmployeeByCondition(new String[] {"em_mobile","em_password","em_uu","em_id"}, "nvl(em_name,' ')='" + store.get("en_adminname").toString() + "'");
			if (objs != null) {
				store.remove("en_adminuupassword");
				store.remove("en_adminuu");
				store.remove("en_adminphone");
				store.put("en_adminphone", objs[0]);
				store.put("en_adminuupassword", objs[1]);
				store.put("en_adminuu", objs[2]);
				String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Enterprise", "en_id");
				baseDao.execute(formSql);
			}
			handlerService.afterUpdate(caller, new Object[] { store });
		}
	}
	
	private void generateComaddressset(String shortname,String workaddr,Map<Object, Object> store){
		String newShortname = store.get("en_shortname").toString();
		String newWorkAddr = store.get("en_address").toString(); 
		
		//获取经纬度
		double lng = 0;
		double lat = 0;
		String url="http://api.map.baidu.com/geocoder/v2/?address="+newWorkAddr.trim()+"&output=json&ak=YOgN88tPirPFHBOk32EjRNQIeGh1z1n6";
		String json = loadJSON(url);
		if(json!=null&&json.length()>0){
			JSONObject obj = JSONObject.fromObject(json);
	        if(obj.get("status").toString().equals("0")){
	        	lng = obj.getJSONObject("result").getJSONObject("location").getDouble("lng");
	        	lat = obj.getJSONObject("result").getJSONObject("location").getDouble("lat");
	        }			
		}		
		
		String validRange = "200";
		String condition = "cs_shortname='"+shortname.replaceAll("'", "''").trim()+"' and cs_workaddr='"+workaddr.replaceAll("'", "''").trim()+"'";
		Employee emp = SystemSession.getUser();
		boolean bool = baseDao.checkIf("comaddressset",condition);
		if(bool){
			//更新
			baseDao.execute("update comaddressset set cs_shortname='"+newShortname.replaceAll("'", "''").trim()+"',cs_workaddr='"+newWorkAddr.replaceAll("'", "'").trim()+"',"
					+ "cs_longitude='"+lng+"',cs_latitude='"+lat+"' where " + condition);
		}else{
			//插入
			String code = baseDao.sGetMaxNumber("COMADDRESSSET", 2);
			baseDao.execute("insert into comaddressset(cs_id,cs_code,cs_recorder,cs_recorddate,cs_status,cs_statuscode,cs_shortname,"
					+ "cs_workaddr,cs_longitude,cs_latitude,cs_validrange) select COMADDRESSSET_SEQ.nextval,'"+code+"','"+emp.getEm_name()+"'" 
					+ ",sysdate,'在录入','ENTERING','"+newShortname.replaceAll("'", "''").trim()+"','"+newWorkAddr.replaceAll("'", "''").trim()+"','"+lng+"','"+lat+"',"+validRange+" from dual");
		}				
		
	}
	
	private String loadJSON (String url) {
        StringBuilder json = new StringBuilder();
        try {
            URL oracle = new URL(url);
            URLConnection yc = oracle.openConnection();
            yc.setConnectTimeout(60000); //毫秒
            yc.setReadTimeout(60000); //毫秒
            BufferedReader in = new BufferedReader(new InputStreamReader(
                                        yc.getInputStream()));
            String inputLine = null;
            while ( (inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
        return json.toString();
	}
}