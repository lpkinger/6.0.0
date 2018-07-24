package com.uas.erp.manage.service.common;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.uas.b2b.model.TaskLog;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Enterprise;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.manage.model.CustomerInfo;
import com.uas.manage.model.VendorInfo;

/**
 * 作为客户EPR,将供应商、客户信息传入平台
 * 
 */
@Component
@EnableAsync
@EnableScheduling
public class VendorInfoTransferTask {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private EnterpriseService enterpriseService;
	
	private static List<Master> masters = null;

	public void execute() {
		if (masters == null) {
			masters = enterpriseService.getMasters();
		}
		String sob = SpObserver.getSp();
		for (Master master : masters) {
			if (master.b2bEnable()) {
				SpObserver.putSp(master.getMa_name());
				Enterprise enterprise = null;
				upLoadVendorInfos(enterprise);
				upLoadCustomerInfos(enterprise);
				updateVendUU();
			}
		}
		SpObserver.putSp(sob);		
	}
	
	private boolean upLoadCustomerInfos(Enterprise enterprise) {
		List<CustomerInfo> customerInfos = getCustomerInfosUpLoad();
		if(!CollectionUtils.isEmpty(customerInfos)){
			HashMap<String, String> params = new HashMap<String, String>();
			String idStr = CollectionUtil.getKeyString(customerInfos, ",");
			params.put("data", FlexJsonUtil.toJsonArrayDeep(customerInfos));
			try {
				beforeUploadedCustomers(idStr);
				Response response = HttpUtil.sendPostRequest(Constant.manageHost()+
						"/public/enterpriseInfo/upload", params, true);
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					onUploadedCustomersSuccess(idStr);
				} else {
					onUploadedCustomersFailed(idStr);
				}
				baseDao.save(new TaskLog("上传没有UU号的客户信息", customerInfos.size(), response));
			} catch (Exception e) {
				onUploadedCustomersFailed(idStr);
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	private void beforeUploadedCustomers(String idStr) {
		baseDao.execute("update customer set cu_sendstatus = '上传中' where cu_id in (" + idStr + ")");
	}

	private void onUploadedCustomersFailed(String idStr) {
		baseDao.execute("update customer set cu_sendstatus = '待上传' where cu_id in (" + idStr + ")"
				+ " and cu_sendstatus = '上传中'");
	}

	private void onUploadedCustomersSuccess(String idStr) {
		baseDao.execute("update customer set cu_sendstatus = '已上传' where cu_id in (" + idStr + ")");
	}

	/**
	 * 获取需要上传的客户信息 
	 */
	public List<CustomerInfo> getCustomerInfosUpLoad(){
		try{
			String sqlstr="regexp_like(cu_mobile, '^((\\(\\d{3}\\))|(\\d{3}\\-))?(13|15|17|18)\\d{9}$') and regexp_like(cu_email, '^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w{2,3}){1,3})$')";
			List<CustomerInfo> customerInfo = baseDao.getJdbcTemplate()
					.query("select * from (select cu_id id, cu_name otherName, cu_contact contact, cu_mobile contactMobile, "
							+ "cu_email contactEmail, cu_businessCode businessCode from customer where (cu_sendstatus = '待上传' or nvl(cu_sendstatus,' ')=' ') and nvl(cu_uu,0) = 0 and nvl(cu_auditstatuscode,' ')='AUDITED' and nvl(cu_contact,' ')<>' ' and nvl(cu_mobile,' ')<>' ' and nvl(cu_email,' ')<>' ' and nvl(cu_businessCode,' ')<>' ' and ("+sqlstr+")) where rownum <300 ",new BeanPropertyRowMapper<CustomerInfo>(CustomerInfo.class));
			Enterprise enterprise = enterpriseService.getEnterprise();
			for(int i=0; i<customerInfo.size(); i++){
				customerInfo.get(i).setEnUU(Long.parseLong(enterprise.getEn_uu() + ""));
				customerInfo.get(i).setOtherType("cust");
			}
			return customerInfo;
		}catch(EmptyResultDataAccessException e){
			e.printStackTrace();
			return null;			
		}
	}
	/**
	 * 获取需要上传的供应商信息 
	 */
	public List<VendorInfo> getVendorInfosUpLoad(){
		try{
			String sqlstr="regexp_like(ve_mobile, '^((\\(\\d{3}\\))|(\\d{3}\\-))?(13|15|17|18)\\d{9}$') and regexp_like(ve_email, '^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w{2,3}){1,3})$')";
			List<VendorInfo> vendorInfos = baseDao.getJdbcTemplate()
					.query("select * from (select ve_id id, ve_name otherName, ve_contact contact,ve_mobile contactMobile, ve_email contactEmail, ve_webserver businessCode from vendor where (ve_sendstatus = '待上传' or nvl(ve_sendstatus,' ')=' ') and nvl(ve_uu,' ') = ' ' and nvl(ve_auditstatuscode,' ')='AUDITED' and nvl(ve_contact,' ')<>' ' and nvl(ve_mobile,' ')<>' '"
							+ " and nvl(ve_email,' ')<>' ' and nvl(ve_webserver,' ')<>' ' and "
							+ " ("+sqlstr+")) where rownum <300",new BeanPropertyRowMapper<VendorInfo>(VendorInfo.class));
			Enterprise enterprise = enterpriseService.getEnterprise();
			for(int i=0; i<vendorInfos.size(); i++){
				vendorInfos.get(i).setEnUU(Long.parseLong(enterprise.getEn_uu() + ""));
				vendorInfos.get(i).setOtherType("vendor");
			}
			return vendorInfos;
		}catch(EmptyResultDataAccessException e){
			e.printStackTrace();
			return null;			
		}
	}

	/**
	 * 上传之前，修改供应商信息的上传状态
	 */
	public void beforeUploadedVendInfos(String idStr) {
		baseDao.execute("update vendor set ve_sendstatus = '上传中' where ve_id in (" + idStr + ")");
	}

	/**
	 * 上传失败后，修改供应商信息的上传状态
	 */
	public void onUploadedVendInfosFailed(String idStr) {
		baseDao.execute("update vendor set ve_sendstatus = '待上传' where ve_id in (" + idStr + ")"
				+ " and ve_sendstatus = '上传中'");
	}

	/**
	 * 上传成功后，修改供应商信息的上传状态
	 */
	public void onUploadedVendInfosSuccess(String idStr) {
		baseDao.execute("update vendor set ve_sendstatus = '已上传' where ve_id in (" + idStr + ")");
	}

	/**
	 * 下载成功修改供应商信息的上传状态 
	 */
	public void onDownloadedVendInfosSuccess(String idStr) {
		baseDao.execute("update vendor set ve_sendstatus = '已下载' where ve_id in (" + idStr + ")");		
	} 	
	
	/**
	 * 上传未注册的供应商、客户信息到管理平台
	 */
	public boolean upLoadVendorInfos (Enterprise enterprise){
		List<VendorInfo> vendorInfos = getVendorInfosUpLoad();
		if(!CollectionUtils.isEmpty(vendorInfos)){
			HashMap<String, String> params = new HashMap<String, String>();
			String idStr = CollectionUtil.getKeyString(vendorInfos, ",");
			params.put("data", FlexJsonUtil.toJsonArrayDeep(vendorInfos));
			try {
				//Constant.manageHost();
				beforeUploadedVendInfos(idStr);
				Response response = HttpUtil.sendPostRequest(Constant.manageHost()+
						"/public/enterpriseInfo/upload", params, true);
				if (response.getStatusCode() == HttpStatus.OK.value()) {// 上传成功修改单据状态
					onUploadedVendInfosSuccess(idStr);
				} else {
					onUploadedVendInfosFailed(idStr);
				}
				baseDao.save(new TaskLog("上传没有UU号的供应商信息", vendorInfos.size(), response));
			} catch (Exception e) {
				onUploadedVendInfosFailed(idStr);
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * 从后台管理平台获取待上传的供应商、客户信息，然后更新UAS中供应商、客户UU号 
	 */
	private boolean updateVendUU () {
		Enterprise enter = enterpriseService.getEnterprise();
		Long enUU = (long)enter.getEn_uu();
		List<String> sqls = new ArrayList<String>();
		try {		
			Response response = HttpUtil.sendGetRequest(Constant.manageHost()+
						"/public/enterpriseInfo/registered?enUU=" + enUU, null, true);
			String data = response.getResponseText();
			if (StringUtil.hasText(data)){
				List<VendorInfo> vendorInfos = FlexJsonUtil.fromJsonArray(data, VendorInfo.class);
				for(VendorInfo vi : vendorInfos){
					if(vi.getOtherType().equals("vendor")){				
						sqls.add("update vendor set ve_sendstatus = '已下载',ve_uu='"+vi.getOtherUU()+"' where ve_name='"+vi.getOtherName()+"'");
					}else{						
						sqls.add("update customer set cu_sendstatus = '已下载',cu_uu="+vi.getOtherUU()+" where cu_name='"+vi.getOtherName()+"'");
					}											
				}
				if(sqls.size()>0){
					baseDao.execute(sqls);
				}
				baseDao.save(new TaskLog("更新供应商、客户UU号", vendorInfos.size(), response));
			}			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}	
		return true;
	}
	
	 private String getVendorContext(String id,String title){
	    	StringBuffer sb=new StringBuffer();
	    	sb.append("有新的供应商注册UU，请注意确认更新!</br>");
	    	sb.append("<span style=\"font-weight:bold\" >标题:</span><a style=\"padding-left:10px;\" href=\"javascript:openUrl(''jsps/scm/purchase/vendor.jsp?formCondition=ve_idIS"+id+"'')\">"+title+"</a></br>");
	    	//sb.append("<span style=\"font-weight:bold\">标题:</span><a style=\"padding-left:10px;\" href=\"javascript:openUrl(''jsps/common/batchDeal.jsp?whoami=Vendor!CheckUU'')\">"+title+"</a></br>");
	    	return sb.toString();
	 }
	 
	 private String getCustomerContext(String id,String title){
	    	StringBuffer sb=new StringBuffer();
	    	sb.append("有新的客户注册UU，请注意确认更新!</br>");
	    	sb.append("<span style=\"font-weight:bold\" >标题:</span><a style=\"padding-left:10px;\" href=\"javascript:openUrl(''jsps/scm/sale/customerBase.jsp?formCondition=cu_idIS"+id+"'')\">"+title+"</a></br>");
	    	return sb.toString();
	 }
}

