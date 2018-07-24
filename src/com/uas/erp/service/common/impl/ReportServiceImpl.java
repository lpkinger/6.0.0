package com.uas.erp.service.common.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.Des;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.UserAgentUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.EnterpriseDao;
import com.uas.erp.dao.common.FormDao;
import com.uas.erp.dao.common.SysPrintSetDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Enterprise;
import com.uas.erp.model.Form;
import com.uas.erp.model.MessageLog;
import com.uas.erp.model.SysPrintSet;
import com.uas.erp.service.common.ReportService;

@Service
public class ReportServiceImpl implements ReportService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private EnterpriseDao enterpriseDao;
	@Autowired
	private FormDao formDao;
	@Autowired
	private SysPrintSetDao sysprintsetdao;

	@Override
	public String getReportPath(String caller) {
		return baseDao.getFieldValue("reportfiles", "file_path", "caller='" + caller + "'", String.class);
	}
	
	@Override
	public Object[] getReportPathAndCondition(String caller) {
		return baseDao.getFieldsDataByCondition("reportfiles", new String[]{"file_path","condition"},  "caller='" + caller+"'");
	}
	@Override
	public String getReportPath(String caller, String reportName) {
		if (!StringUtil.hasText(reportName))
			return getReportPath(caller);
		return baseDao
				.getFieldValue("reportfiles", "file_path", "caller='" + caller + "' and file_name='" + reportName + "'", String.class);
	}

	@Override
	public List<Map<String, Object>> getDatasFields(String caller) {
		SqlRowList rs = baseDao.queryForRowSet("select title,condition,file_name,id from  reportfiles where caller='" + caller + "'");
		return rs.getResultList();
	}

	private String getUrlQuietly(HttpServletRequest request, String innerUrl, String outerUrl) {
		if (UserAgentUtil.accessible(request, innerUrl)) {
			return innerUrl;
		}
		return outerUrl;
	}

	@Override
	public Map<String, Object> print(int id, String caller, String reportName, String condition, HttpServletRequest request) {
		Employee employee = SystemSession.getUser();
		Enterprise enterprise = enterpriseDao.getEnterprise();
		if(enterprise.getEn_printurl()==null){
			BaseUtil.showError("内网打印地址未配置，请在企业信息中维护内网打印地址。");
		}
		String jasperprinttype="",whichsystem="";
		String printUrl = getUrlQuietly(request, enterprise.getEn_printurl(), enterprise.getEn_Url());
		Map<String, Object> params = new HashMap<String, Object>();
		if (caller != null) {
			//判断输出方式是否为pdf
			Form form = formDao.getForm(caller, employee.getEm_master());
			String rptName = getReportPath(caller, reportName);
			Object defaultCondition = form.getFo_helpdoc();
			String printtype=(!StringUtil.hasText(reportName))?baseDao.getFieldValue("reportfiles", "printtype", "caller='" + caller + "'", String.class)
			:
			baseDao.getFieldValue("reportfiles", "printtype", "caller='" + caller + "' and file_name='" + reportName + "'", String.class);
			if(printtype!=null && "jasper".equals(printtype)){
				SysPrintSet sysprintset=sysprintsetdao.getSysPrintSet(caller, reportName);
				rptName=sysprintset.getReportname();
				String defaultcondition=sysprintset.getDefaultcondition();
				String keyfield=(String) baseDao.getFieldDataByCondition("form", "fo_keyfield", "fo_caller='"+caller+"'");
				if(defaultcondition!="" && defaultcondition!=null){
					condition="Where "+keyfield+"='"+id+"' and "+defaultcondition;
				}else{
					condition="Where "+keyfield+"='"+id+"'";
				}
				printUrl=getUrlQuietly(request, enterprise.getEn_intrajasperurl(), enterprise.getEn_extrajasperurl());
				jasperprinttype=sysprintset.getPrinttype();
				if (defaultCondition == null||"".equals(defaultCondition)) {
					if(enterprise.getEn_Admin() != null&&"pdf".equals(enterprise.getEn_Admin())&&
							form.getFo_pagetype()!=null &&"default".equals(form.getFo_pagetype())){//维护界面pdf打印Fo_helpdoc不能为空
						BaseUtil.showError("Form打印数据源缺失");
					}
					defaultCondition = "";
				} else {
					defaultCondition = defaultCondition.toString() + String.valueOf(id);
				}
				whichsystem=enterprise.getEn_whichsystem().toUpperCase();
			}else{
				if(rptName!= null){
					Object condition_rp=baseDao.getFieldDataByCondition("reportfiles", "pdfCondition", "caller='" + caller + "' and file_name='" + reportName + "'");//reportfiles表中配置pdf条件
					if(condition_rp!=null&&!"".equals(condition_rp)){
						defaultCondition = condition_rp.toString();
					}
				}
				if (defaultCondition == null||"".equals(defaultCondition)) {
					if(enterprise.getEn_Admin() != null&&"pdf".equals(enterprise.getEn_Admin())&&
							form.getFo_pagetype()!=null &&"default".equals(form.getFo_pagetype())){//维护界面pdf打印Fo_helpdoc不能为空
						BaseUtil.showError("Form打印数据源缺失");
					}
					defaultCondition = "";
				} else {
					defaultCondition = defaultCondition.toString() + String.valueOf(id);
				}
				// 判断打印模式，取报表文件路径
				if (enterprise.getEn_Admin() != null &&"pdf".equals(enterprise.getEn_Admin())) {
					// pdf打印
					// rptName=rptName==null?form.getFo_reportname() : rptName;
					if(rptName == null){
						BaseUtil.showError("找不到报表文件，请通过Form维护中报表设置配置报表信息");
					}
					rptName = rptName == null ? reportName : rptName;
				} else {
					// windows打印
					// rptName=reportName;
					if (StringUtils.hasText(reportName)) {
						rptName = reportName;
					}
				}
				// rptName=reportName;;
				Des des = new Des();
				try {
					rptName = des.toHexString(des.encrypt(rptName, "12345678")).toUpperCase();
				} catch (Exception e) {
				}
				whichsystem=enterprise.getEn_whichsystem();
			}
			params.put("printtype", printtype);
			params.put("reportname", rptName);
			params.put("condition", condition);
			params.put("whichsystem", whichsystem);
			params.put("printUrl", printUrl);
			params.put("isbz", enterprise.getEn_Admin());
			params.put("defaultCondition", defaultCondition);
			params.put("jasperprinttype", jasperprinttype==null?"":jasperprinttype);
			baseDao.execute("insert into printLog(pl_id,pl_caller,pl_reportname,pl_printid,pl_man,pl_date) values(PRINTLOG_SEQ.nextval,'"
					+ caller + "','" + reportName + "'," + id + ",'" + employee.getEm_name() + "',sysdate)");
		}
		return params;
	}

	@Override
	public Map<String, Object> printMT(int id, String caller, String reportName, String condition, HttpServletRequest request) {
		Map<String, Object> params = new HashMap<String, Object>();
		Object keyField = baseDao.getFieldDataByCondition("Form", "fo_keyfield", "fo_caller='" + caller + "'");// 获得单据主键用于记录日志
		int count = baseDao.getCount("select count(1) from printLog where pl_caller='" + caller + "' and pl_printid=" + id);
		if (count > 0) {
			params = print(id, caller, reportName, condition, request);
		} else {
			BaseUtil.showError("当前单据未执行过打印操作，不能执行当前操作。");
		}
		// 记录操作
		try {
			baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.printMT"), BaseUtil
					.getLocalMessage("msg.printMTSuccess"), caller + "|" + keyField + "=" + id));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return params;
	}

}
