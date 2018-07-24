package com.uas.erp.service.crm.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;


@Service("CrmHandler")
public class CrmHandlerService {
	@Autowired
	private BaseDao baseDao;

	
	public void contact_save_checkLinkmanUU(HashMap<Object, Object> store) {
		Object enUU=baseDao.getFieldDataByCondition("customer","cu_uu", "cu_code='"+store.get("ct_cucode").toString()+"'");
		if(enUU!=null&&!"".equals(enUU)){
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("enUU", enUU.toString());
			params.put("userTel", store.get("ct_mobile").toString());
			Response response = null;
			try {
				response = HttpUtil.sendGetRequest("http://www.ubtob.com/public/queriable/userUUByTelAndEnUU", params, false);
			} catch (Exception e) {
					e.printStackTrace();
			}
			if (response.getStatusCode() == HttpStatus.OK.value()) {
					Map<String, Object> backInfo = FlexJsonUtil.fromJson(response.getResponseText(), HashMap.class);
					if (backInfo != null) {
						String err=backInfo.get("error")==null?"":backInfo.get("error").toString();
						if(!"".equals(err)){
							BaseUtil.showError("当前客户已经匹配了UU号，联系人资料也必须维护个人UU:"+err);
						}
					}
				}
		}
	}
	public void updatetotal_beforedetail(Integer id,String caller){
      Object[] data= baseDao.getFieldsDataByCondition("merchandisingdetail", "mhd_mhid,mhd_sumtotal", "mhd_id="+id);
      baseDao.updateByCondition("merchandising", "mh_total=mh_total-"+data[1],"mh_id="+data[0]);
	}
	/**
	 * 判断拜访报告的录入是否被分配给报告上的客户了
	 * 
	 * @param store
	 * @param gstore
	 * @param language
	 */
	public void visitRecord_impcust_recordor(Integer id) {
		Object custcode = baseDao.getFieldDataByCondition("VisitRecord", "vr_cuuu", "vr_id=" + id);
		Object recordman = baseDao.getFieldDataByCondition("VisitRecord", "vr_recorder", "vr_id=" + id);
		int count = 0;
		count = baseDao
				.getCount("select count(*) from CustomerImpDistrApply left join CustomerImpDistrApplydet on ca_id=cad_caid left join customer on cad_cuid=cu_id where cu_code='"
						+ custcode.toString() + "' and cad_seller='" + recordman.toString() + "'");
		
		if (count > 0) {

		} else {
			BaseUtil.showError(BaseUtil.getLocalMessage("crm.visitRecord.cust_recordor"));
		}
	}

	/**
	 * 判断拜访报告的明细的洽谈对象，推广项目，客户项目是否必填了
	 * 
	 * @param store
	 * @param gstore
	 * @param language
	 */
	public void visitRecord_detail_need(Integer id) {
		int count = 0, projectcount = 0, cuprojectcount = 0;
		count = baseDao.getCount("select count(*) from CuPlayers where cup_vrid=" + id);
		if (count > 0) {
			projectcount = baseDao.getCount("select count(*) from ProductInfo where pi_vrid=" + id);
			if (projectcount > 0) {
				cuprojectcount = baseDao.getCount("select count(*) from VisitFeedBack where fb_vrid=" + id);
				if (cuprojectcount > 0) {

				} else {
					BaseUtil.showError(BaseUtil.getLocalMessage("crm.visitRecord.cuproject_need"));
				}
			} else {
				BaseUtil.showError(BaseUtil.getLocalMessage("crm.visitRecord.project_need"));
			}
		} else {
			BaseUtil.showError(BaseUtil.getLocalMessage("crm.visitRecord.cuplayer_need"));
		}
	}

	/**
	 * 判断拜访报告的录入是否被分配给报告上的供应商了
	 * 
	 * @param store
	 * @param gstore
	 * @param language
	 */
	public void visitRecord_vendor_recordor(Integer id) {
		Object custcode = baseDao.getFieldDataByCondition("VisitRecord", "vr_cuuu", "vr_id=" + id);
		Object recordman = baseDao.getFieldDataByCondition("VisitRecord", "vr_recorder", "vr_id=" + id);
		int count = 0;
		count = baseDao.getCount("select count(*) from vendorDistr left join vendor on ve_id=vd_veid  where nvl(ve_code,' ')='"
				+ custcode.toString() + "' and vd_person='" + recordman.toString() + "'");
		if (count > 0) {

		} else {
			BaseUtil.showError(BaseUtil.getLocalMessage("crm.visitRecord.vendor_recordor"));
		}
	}
	/**
	 * 当商机阶段=项目机会报备，根据采购负责人的姓名+职务+部门+手机号 如果相同，不让提交
	 * 
	 * @param store
	 * @param gstore
	 * @param language
	 */
	public void BusinessChanceData_submit(Integer id){/*
			Employee employee = SystemSession.getUser();
			String bcd_man=employee.getEm_name();
			Object bcd_bsname=baseDao.getFieldDataByCondition("BusinessChanceData", "bcd_bsname", "bcd_man='"+bcd_man+"'");
			if("项目报备阶段".equals(bcd_bsname)){
				Object[] bcd_column=baseDao.getFieldsDataByCondition("BusinessChanceData", "bcd_column3,bcd_column4,bcd_column5,bcd_column6", "bcd_man='"+bcd_man+"' and bcd_bsname='项目报备阶段' and bcd_id='"+id+"'");
				if(bcd_column[0]!=null&&bcd_column[1]!=null&&bcd_column[2]!=null&&bcd_column[3]!=null){
					int count = baseDao.getCount("select count(*) from BusinessChanceData where bcd_bsname='项目报备阶段' and bcd_column3='" + bcd_column[0]
							+ "' and bcd_column4='" + bcd_column[1] + "' and bcd_column5='" + bcd_column[2] + "' and bcd_column6='"
							+ bcd_column[3]+"'");
					 if(count>1){
						 BaseUtil.showError("存在相同的设备经理姓名 、手机号、部门、职务。不能提交");
					 }	
				}				
			}
		
	*/}
	public void BusinessChance_save(HashMap<Object, Object> store){
		if((store.get("bc_custname")!=null&&!"".equals(store.get("bc_custname").toString()))||(store.get("bc_desc6")!=null&&!"".equals(store.get("bc_desc6").toString()))){
			boolean a= baseDao.checkByCondition("BusinessChance", "((bc_custname is not null and bc_custname='"+store.get("bc_custname")+"') or (bc_desc6 is not null and bc_desc6='"+store.get("bc_desc6")+"')) and NVL(bc_lockstatuscode,' ')<>'LOCKED' and bc_status<>'在录入' and bc_id<>'"+store.get("bc_id")+"'");		
			if(!a){
				BaseUtil.showError("企业名称或者营业执照相同,限制保存!");
			}else{
				if(!(store.get("bc_domancode")==null||"".equals(store.get("bc_domancode").toString().trim()))){					
					Object[] lockdate = baseDao.getFieldsDataByCondition("BusinessChance", "to_date(sysdate)-to_date(bc_lockdate),bc_domancode", "((bc_custname is not null and bc_custname='"+store.get("bc_custname")+"') or (bc_desc6 is not null and bc_desc6='"+store.get("bc_desc6")+"')) and NVL(bc_lockstatuscode,' ')='LOCKED' and bc_domancode is not null and bc_id<>'"+store.get("bc_id")+"' order by bc_lockdate desc");
					if(lockdate!=null){
						if(lockdate[1]!=null){
							Object defaultoridOld = baseDao.getFieldDataByCondition("employee", "em_defaultorid", "em_code='"+lockdate[1]+"'");
							Object defaultoridNew = baseDao.getFieldDataByCondition("employee", "em_defaultorid", "em_code='"+store.get("bc_domancode")+"'");
							if(defaultoridOld.toString().equals(defaultoridNew.toString())){
								Object orgrecovertime =  baseDao.getFieldDataByCondition("BusinessBasis", "bd_orgrecovertime", "1=1");
								if(Integer.parseInt(lockdate[0].toString())<=Integer.parseInt(orgrecovertime.toString())){
									BaseUtil.showError("企业名称或者营业执照相同或者不符合同一组织N天不允许跟进限制,限制保存!");
								}
							}
						}
					}
				}
			}
		}
	}
	public void BusinessChance_update(HashMap<Object, Object> store){
		if((store.get("bc_custname")!=null&&!"".equals(store.get("bc_custname").toString()))||(store.get("bc_desc6")!=null&&!"".equals(store.get("bc_desc6").toString()))){
			boolean a= baseDao.checkByCondition("BusinessChance", "((bc_custname is not null and bc_custname='"+store.get("bc_custname")+"') or (bc_desc6 is not null and bc_desc6='"+store.get("bc_desc6")+"')) and NVL(bc_lockstatuscode,' ')<>'LOCKED' and bc_id<>'"+store.get("bc_id")+"'");		
			if(!a){
				BaseUtil.showError("企业名称或者营业执照相同,限制保存!");
			}else{
				if(!(store.get("bc_domancode")==null||"".equals(store.get("bc_domancode").toString().trim()))){					
					Object[] lockdate = baseDao.getFieldsDataByCondition("BusinessChance", "to_date(sysdate)-to_date(bc_lockdate),bc_domancode", "((bc_custname is not null and bc_custname='"+store.get("bc_custname")+"') or (bc_desc6 is not null and bc_desc6='"+store.get("bc_desc6")+"')) and NVL(bc_lockstatuscode,' ')='LOCKED' and bc_domancode is not null and bc_id<>'"+store.get("bc_id")+"' order by bc_lockdate desc");
					if(lockdate!=null){
						if(lockdate[1]!=null){
							Object defaultoridOld = baseDao.getFieldDataByCondition("employee", "em_defaultorid", "em_code='"+lockdate[1]+"'");
							Object defaultoridNew = baseDao.getFieldDataByCondition("employee", "em_defaultorid", "em_code='"+store.get("bc_domancode")+"'");
							if(defaultoridOld.toString().equals(defaultoridNew.toString())){
								Object orgrecovertime =  baseDao.getFieldDataByCondition("BusinessBasis", "bd_orgrecovertime", "1=1");
								if(Integer.parseInt(lockdate[0].toString())<=Integer.parseInt(orgrecovertime.toString())){
									BaseUtil.showError("企业名称或者营业执照相同,限制保存!");
								}
							}
						}
					}
				}
			}
		}
	}
	public void BusinessDataBase_save(Integer id){
		Object bd_prop=baseDao.getFieldDataByCondition("BUSINESSDATABASE", "bd_prop", "bd_id="+id);
		if("管理员分配".equals(bd_prop.toString())){
			baseDao.updateByCondition("BUSINESSDATABASE", "bd_browseid=null","bd_id="+id);
		}
	}
	
	//审核后注册优软云
	public void BC_sendToZz(Integer bc_id) {
		Object tel = baseDao.getFieldDataByCondition("BusinessChance", "bc_tel", "bc_id=" + bc_id);
		if (tel != null) {
			https_post("http://login.uuzcc.com/index/ubtob/login_reg/mobile/" + tel);
		}
	}

	public String https_post(String urlStr) {
		String res = null;
		try {
			//huangx SSL验证不通过时，默认为TRUE
			HttpsURLConnection.setDefaultHostnameVerifier(new CrmHandlerService().new NullHostNameVerifier());
			SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            
			URL obj = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
			conn.setReadTimeout(5000);
			conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
			conn.addRequestProperty("User-Agent", "Mozilla");
			conn.addRequestProperty("Referer", "google.com");

			boolean redirect = false;

			int status = conn.getResponseCode();
			if (status != HttpURLConnection.HTTP_OK) {
				if (status == HttpURLConnection.HTTP_MOVED_TEMP
						|| status == HttpURLConnection.HTTP_MOVED_PERM
						|| status == HttpURLConnection.HTTP_SEE_OTHER)
					redirect = true;
			}
			// 遇到转发时，再次转发
			if (redirect) {
				String newUrl = conn.getHeaderField("Location");
				String cookies = conn.getHeaderField("Set-Cookie");
				conn = (HttpURLConnection) new URL(newUrl).openConnection();
				conn.setRequestProperty("Cookie", cookies);
				conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
				conn.addRequestProperty("User-Agent", "Mozilla");
				conn.addRequestProperty("Referer", "google.com");
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String inputLine;
			StringBuffer html = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				html.append(inputLine);
			}
			in.close();
			res = html.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//如果已存在相应的用户,表中记录数据,后一步就不进行短信发送
		if (res != null) {
			Map<Object, Object> map = BaseUtil.parseFormStoreToMap(res);
			String tel = urlStr.substring(urlStr.lastIndexOf("/") +1);
			if(map.get("msg") != null && map.get("msg").equals("用户已存在")) {
				baseDao.execute("begin execute immediate ' insert into registerinzc select registerinzc_seq.nextval,''" + tel + "'',-1 from dual where not exists (select 1 from registerinzc where phone = ''" + tel + "'')';"
						+ "execute immediate ' update registerinzc set isregister=-1 where phone=''" + tel + "'' '; end;");
			}
		}
		return res;
	}
	static TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            // TODO Auto-generated method stub
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            // TODO Auto-generated method stub
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            // TODO Auto-generated method stgg35ub
            return null;
        }
    } };
	public class NullHostNameVerifier implements HostnameVerifier {
        /*
         * (non-Javadoc)
         * 
         * @see javax.net.ssl.HostnameVerifier#verify(java.lang.String,
         * javax.net.ssl.SSLSession)
         */
        @Override
        public boolean verify(String arg0, SSLSession arg1) {
            // TODO Auto-generated method stub
        	//验证不通过默认设置为true
            return true;
        }
    }
    
    //商机统一上传至众创需求平台
    public void BC_toZzRequirement(HashMap map,  ArrayList list) {
		List<Object> ids = baseDao.getFieldDatasByCondition("businesschance", "bc_id", "nvl(bc_exists_user,0)=0");
		Object btobman = baseDao.getFieldDataByCondition("BUSINESSBASIS", "bb_ubtobman", "1=1");
		List<String> update = new ArrayList<String>();
		for (Object id : ids) {
			if (StringUtil.hasText(btobman)) {
				Object[] data = baseDao.getFieldsDataByCondition("BusinessChance", "bc_description,bc_remark,"
						+ "bc_tel,bc_domancode,bc_contact,bc_needsourcecode_user,bc_code,bc_projecttypeid_user,bc_contact,bc_custname", "bc_id ="+ id);
				//商机跟进人与平台跟进人相同
				if (data[3] != null && data[3].equals(btobman)) {
					Map<String,Object> params = new HashMap<String,Object>();
					//发送至众创平台
					Map<String, Object> jsons = getJsons(data[6]);
					String msg = "txt_title=" + data[0] + "&tar_content=" + data[1] + "&txt_mobile=" + data[2] + "&indus_id="+ data[7] +"&is_need=" + (data[5] == null ? 0 : data[5]) + ""
							+ "&hardware=" + jsons.get("hard") + "&software=" + jsons.get("soft") +"&cloud=" + jsons.get("cloud") + "&customer="
							+ (data[8] == null ? "" : data[8]) + "&customer_company=" + (data[9] == null ? "" : data[9]);
					params.put("data", msg);
					post("https://zb.uuzcc.com/index.php?do=interface&id=4", params, "form");
					params.clear();
					
				//发短信至联系人
					if (data[2] != null && !"".equals(data[2]) && data[2].toString().length() > 6) {
						int count = baseDao.getCount("select count(1) from registerinzc where phone ='" + data[2] + "' and isregister =-1");
						if (count == 0) {
							String password = data[2].toString();
							Object contact = data[4] == null ? data[2] : data[4];
							password = "zc" + password.substring(password.length()-6);
							params.put("templateId", "c048f398-eb33-4680-af72-6fc5b8773d10");
							params.put("receiver", data[2]);
							params.put("params", new String[]{String.valueOf(contact), String.valueOf(data[0]), data[2].toString(), password});
							post("http://message.ubtob.com/sms/send", params, "json");
						}
					}
				update.add("update businesschance set bc_exists_user=-1 where bc_id=" + id);
				}
			}
		}
		baseDao.execute(update);
    }
    
	//获取众创对应参数类型参数
	private Map<String, Object> getJsons(Object bc_code) {
		Map<String, Object> params = new HashMap<String, Object>();
		JSONObject soft = new JSONObject();
		JSONObject hard = new JSONObject();
		JSONObject cloud = new JSONObject();
		String[] auth = new String[]{"UL认证", "CE认证", "3C认证", "FCC认证"};
		String[] softtype = new String[]{"app开发", "微信小程序", "微信公众号", "pc端"};
		Object[] data = baseDao.getFieldsDataByCondition("businesschance  left join projecttype$zz on pt_id = BC_PROJECTTYPEID_USER",
				"pt_issoft,bc_hardsize_user,bc_authenticate_user,bc_hardadditional_user,bc_softtype_user,bc_softadditional_user,bc_cloudadditional_user", "bc_code='" + bc_code + "'");
		if (data != null && data[0] !=null && data[0].toString().equals("0")) {
			hard.put("size", data[1] == null ? "" : data[1]);
			hard.put("authenticate", praseCBG(data[2], auth));
			hard.put("additional", prase2Array(data[3]));
			soft.put("type", praseCBG(data[4], softtype));
			soft.put("additional", prase2Array(data[5]));
			cloud.put("additional", prase2Array(data[6]));
		}
		params.put("soft", soft);
		params.put("hard", hard);
		params.put("cloud", cloud);
		return params;
	}
	
	private JSONArray prase2Array(Object data) {
		JSONArray res = new JSONArray();
		if (data != null) {
			String[] strings = data.toString().split(";");
			for (String s : strings) {
				res.add(s);
			}
		}
		return res;
	}
	
	//解析复选框"1;1;-1;-1"形式至具体json数组
	private JSONArray praseCBG(Object str, String[] args) {
		JSONArray result = new JSONArray();
		if (str != null) {
			String[] split =  str.toString().split(";");
			for (int i = 0 ; i<split.length; i++) {
				if (split[i].equals("1")) {
					result.add(args[i]);
				}
			}
		}
		return result;
	}
	
	private String post(String str, Map<String,Object> params, String datatype) {
		String result = null;
		OutputStreamWriter out = null;
		String msg = null;
		boolean redirect = false;
		try{
			if  (str.contains("https://")) {
				HttpsURLConnection.setDefaultHostnameVerifier(new CrmHandlerService().new NullHostNameVerifier());
				SSLContext sc = SSLContext.getInstance("TLS");
	            sc.init(null, trustAllCerts, new SecureRandom());
	            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			}
			URL url=new URL(str);
		    HttpURLConnection connection = (HttpURLConnection) url.openConnection(); 
            connection.setDoOutput(true);  
            connection.setDoInput(true);  
            connection.setUseCaches(false);  
            connection.setInstanceFollowRedirects(true);  
            connection.setRequestMethod("POST"); // 设置请求方式  
            connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式  
            if ("json".equals(datatype)) {
            	connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8"); // 设置发送数据的格式  
            	connection.connect();  
            	out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8"); // utf-8编码  
            	msg = BaseUtil.parseMap2Str(params);
            } else {
            	connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"); // 设置发送数据的格式  
            	connection.connect();  
                out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8"); // utf-8编码  
                msg = String.valueOf(params.get("data"));
            }      
        	out.append(msg);  
            out.flush();  
            out.close();  
  
            int code = connection.getResponseCode();  
        	if (code != HttpURLConnection.HTTP_OK) {
				if (code == HttpURLConnection.HTTP_MOVED_TEMP
						|| code == HttpURLConnection.HTTP_MOVED_PERM
						|| code == HttpURLConnection.HTTP_SEE_OTHER)
					redirect = true;
			}
    		// 遇到转发时，再次转发
			if (redirect) {
				String newUrl = connection.getHeaderField("Location");
				String cookies = connection.getHeaderField("Set-Cookie");
				connection = (HttpURLConnection) new URL(newUrl).openConnection();
				connection.setRequestProperty("Cookie", cookies);
				connection.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
				connection.addRequestProperty("User-Agent", "Mozilla");
				connection.addRequestProperty("Referer", "google.com");
			}  
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer html = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				html.append(inputLine);
			}
			in.close();
			result = html.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}  		
		return result;
	}
    
}
