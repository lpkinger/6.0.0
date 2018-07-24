package com.uas.erp.service.common.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.ContextUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.UserAgentUtil;
import com.uas.erp.core.exception.SystemException;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.EnterpriseDao;
import com.uas.erp.dao.common.PowerDao;
import com.uas.erp.dao.common.SysPrintSetDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.EmpsJobs;
import com.uas.erp.model.Enterprise;
import com.uas.erp.model.PersonalPower;
import com.uas.erp.model.SysPrintSet;
import com.uas.erp.service.common.JasperReportPrintService;
import com.uas.erp.service.scm.ProdInOutService;


@Service
public class JasperReportPrintSeriveImpl implements JasperReportPrintService {
	@Autowired
	private SysPrintSetDao sysPrintSetDao;
	@Autowired
	private EnterpriseDao enterpriseDao;
	@Autowired
	private ProdInOutService prodInOutService;
	@Autowired
	private PowerDao powerDao;
	@Autowired
	private BaseDao baseDao;
	@Override
	public Map<String, Object> print(int id, String caller, String reportname,boolean isProdIO,HttpServletRequest request) {
		SysPrintSet sysPrintSet=sysPrintSetDao.getSysPrintSet(caller, reportname);//打印设置
		Enterprise enterprise = enterpriseDao.getEnterprise();//企业信息
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield","fo_statuscodefield",
				"fo_codefield"},"fo_caller='" + caller + "'");// 先根据caller拿到对应table、主键、状态码字段
		String tab="",keyField="",condition="",statuscodefield="",codeField="";
		if (objs != null) {
			tab=objs[0]==null?"":objs[0].toString();
			if (tab.contains(" ")) {
				tab = tab.substring(0, tab.indexOf(" "));//表名
			}
			keyField=objs[1]==null?"":objs[1].toString();//主键
			statuscodefield=objs[2]==null?"":objs[2].toString();//状态码
			codeField=objs[3]==null?"":objs[3].toString();//code
			condition="where "+keyField+"="+id;//条件：主键字段=id
		}
		Object postStatus =null;
		String title =sysPrintSet.getTitle()==null?"":sysPrintSet.getTitle().toString();
		if(isProdIO){
			prodInOutService.printCheck(id, caller);
			postStatus=baseDao.getFieldDataByCondition("Prodinout","pi_statuscode", "pi_id="+id);
		}
		List<String> Sqls = new ArrayList<String>();
		if(!"".equals(tab)&&!"".equals(keyField)){
			if(!"".equals(codeField)){
				 Object code=baseDao.getFieldDataByCondition(tab, codeField, keyField+"="+id);//取code值
				 title = code==null ? title : title+code.toString();
			}
			//状态限制
			if(!"".equals(statuscodefield)){//状态码字段存在
				String status = baseDao.getFieldValue(tab, statuscodefield, keyField+"=" + id, String.class);
				if(sysPrintSet.getNeedaudit()==-1){//已审核才能打印
					StateAssert.printOnlyAudited(status);
				}
				if(isProdIO&&sysPrintSet.getNopost()==-1 && "POSTED".equals(postStatus)){//已过帐不许打印
					BaseUtil.showError("已过账的单据不允许打印！");
				}
				if(isProdIO&&sysPrintSet.getNeedenoughstock()!=0){//库存不足不能打印
					if (!"POSTED".equals(postStatus)) {
						String sql = "select pd_prodcode,pw_onhand,pw_whcode from (select pd_prodcode,(case when NVL(pd_whcode,' ')=' ' then pi_whcode else pd_whcode end) as whcode,sum(pd_outqty) as outqty from  prodinout,ProdIODetail  where pi_id=pd_piid  and pd_piid='"
								+ id+ "' group by pd_prodcode,  (case when NVL(pd_whcode,' ')=' ' then pi_whcode else pd_whcode end)) A left join productwh on pw_prodcode=pd_prodcode and pw_whcode=whcode where NVL(pw_onhand,0)<outqty ";
						SqlRowList rs = baseDao.queryForRowSet(sql);
						while (rs.next()) {
							SqlRowList rs1 = baseDao.queryForRowSet(
									"select wm_concat(pd_pdno) from prodiodetail where pd_piid=? and pd_prodcode=? and pd_whcode=?", id,
									rs.getObject("pd_prodcode"), rs.getObject("pw_whcode"));
							if (rs1.next()) {
								BaseUtil.showError("库存不足不允许打印，行号：" + rs1.getString(1));
							}
						}
					}
				}
			}
			//不允许多次打印
			if(sysPrintSet.getAllowmultiple()==0&&sysPrintSet.getCountfield()!=null&&!"".equals(sysPrintSet.getCountfield())){
				Object count=baseDao.getFieldDataByCondition(tab,"nvl("+sysPrintSet.getCountfield()+",0)", keyField+"="+id);
				if(Integer.parseInt(count.toString())>1){
					Employee employee = (Employee) request.getSession().getAttribute("employee");
					boolean printRepeatJob = checkJobPower(caller, PersonalPower.PRINT_REPEAT, employee);
					boolean printRepeatSelf = powerDao.getSelfPowerByType(caller, PersonalPower.PRINT_REPEAT, employee);
					if(!printRepeatJob && !printRepeatSelf) {
						BaseUtil.showError("该单据不允许多次打印!");
					}
				}
			}
			//打印前执行逻辑
			if(sysPrintSet.getHandlermethod()!=null&&!"".equals(sysPrintSet.getHandlermethod())){
				executrHandlermethod(sysPrintSet.getHandlermethod(),new Object[] {id , caller});
			}
			//更新打印次数
			if(sysPrintSet.getCountfield()!=null&&!"".equals(sysPrintSet.getCountfield())){
				Sqls.add("update "+tab+" set "+sysPrintSet.getCountfield()+"=nvl("+sysPrintSet.getCountfield()+",0)+1 where "+keyField+"="+id);
			}
			//更新打印状态状态码
			if(sysPrintSet.getStatusfield()!=null&&!"".equals(sysPrintSet.getStatusfield()))
				Sqls.add("update "+tab+" set "+sysPrintSet.getStatusfield()+"='已打印' where "+keyField+"="+id);
			if(sysPrintSet.getStatuscodefield()!=null&&!"".equals(sysPrintSet.getStatuscodefield()))
				Sqls.add("update "+tab+" set "+sysPrintSet.getStatuscodefield()+"='PRINTED' where "+keyField+"="+id);
		}	
		baseDao.execute(Sqls);
		if(sysPrintSet.getDefaultcondition()!=null){
			condition=condition+" and "+sysPrintSet.getDefaultcondition();
		}
		condition = appendCondition(sysPrintSet,  condition);//支持数据权限
		// 记录操作
		baseDao.logger.print(caller,keyField, id);
		Map<String, Object> params = new HashMap<String, Object>();
		String printtype=(!StringUtil.hasText(reportname))?baseDao.getFieldValue("reportfiles", "printtype", "caller='" + caller + "'", String.class)
				:
				baseDao.getFieldValue("reportfiles", "printtype", "caller='" + caller + "' and file_name='" + reportname + "'", String.class);
		boolean accessible = UserAgentUtil.accessible(request, enterprise.getEn_printurl());
		if (accessible) {
			params.put("printurl", enterprise.getEn_printurl());//打印地址
		} else {
			params.put("printurl", enterprise.getEn_Url());
		}
		if(printtype != null && "jasper".equals(printtype)){
			boolean accessible1 = UserAgentUtil.accessible(request, enterprise.getEn_intrajasperurl());
			if (accessible1) {
				params.put("printurl", enterprise.getEn_intrajasperurl());//打印地址
			} else {
				params.put("printurl", enterprise.getEn_extrajasperurl());
			}
		}
		params.put("userName", enterprise.getEn_whichsystem());//当前账套的用户名
		params.put("reportName", sysPrintSet.getReportname());//报表的名称
		params.put("whereCondition",condition);//条件
		params.put("printtype",sysPrintSet.getPrinttype()==null?"":sysPrintSet.getPrinttype());//输出方式
		params.put("title",title);//标题
		
		return params;
	}
	
	private boolean checkJobPower(String caller, String powerType, Employee employee) {
		String sob = employee.getEm_master();
		// 默认岗位设置
		boolean bool = powerDao.getPowerByType(caller, powerType, sob, employee.getEm_defaulthsid());
		if (!bool && employee.getEmpsJobs() != null) {
			// 按员工岗位关系取查找权限
			for (EmpsJobs empsJob : employee.getEmpsJobs()) {
				bool = powerDao.getPowerByType(caller, powerType, sob, empsJob.getJob_id());
				if (bool)
					break;
			}
		}
		return bool;
	}
	
	private String appendCondition(SysPrintSet sysPrintSet,String condition){
		Employee employee = SystemSession.getUser();
		String tablename=sysPrintSet.getTablename();
		if(tablename!=null && !"".equals(tablename)){
			// 设置的数据权限
			String limitcondition =baseDao.getLimitCondition(tablename, employee.getEm_id());
			if ((condition == null || "".equals(condition)) && !"".equals(limitcondition)){
				condition = limitcondition;
			}else{
				condition += !"".equals(limitcondition) ? (" AND " + limitcondition) : "";
			}
		}		
		return condition;
	}
	@Override
	public Map<String, Object> print(String param, String caller,String reportname,
			HttpServletRequest request) {
		if ("customzl".equals(caller)&&"ESTIMATE_custom".equals(reportname)//应付暂估
				&&baseDao.isDBSetting("sys", "autoCreateApBill") && baseDao.isDBSetting("sys", "useBillOutAP")) {
			reportname="ESTIMATE_custom_auto";
		}
		if ("customzl".equals(caller)&&"GOODSSEND_custom".equals(reportname)&&//发出商品账龄
				baseDao.isDBSetting("sys", "autoCreateArBill") && baseDao.isDBSetting("sys", "useBillOutAR")) {
			reportname="GOODSSEND_custom_auto";
		}
		SysPrintSet sysPrintSet=sysPrintSetDao.getSysPrintSet(caller,reportname);
		Enterprise enterprise = enterpriseDao.getEnterprise();
		Map<Object,Object> defaultMethodparams=BaseUtil.parseFormStoreToMap(param);
		executeDefault(caller,defaultMethodparams,reportname);
		Map<String, Object> params = new HashMap<String, Object>();
		if(enterprise.getEn_Admin() == null || !"jasper".equals(enterprise.getEn_Admin())){
			boolean accessible = UserAgentUtil.accessible(request, enterprise.getEn_intrajasperurl());
			if (accessible) {
				params.put("printurl", enterprise.getEn_intrajasperurl());//打印地址
			} else {
				params.put("printurl", enterprise.getEn_extrajasperurl());
			}
		}else{
			boolean accessible = UserAgentUtil.accessible(request, enterprise.getEn_printurl());
			if (accessible) {
				params.put("printurl", enterprise.getEn_printurl());//打印地址
			} else {
				params.put("printurl", enterprise.getEn_Url());
			}
		}
		String condition= sysPrintSet.getDefaultcondition()==null?"":sysPrintSet.getDefaultcondition();
		condition = appendCondition(sysPrintSet,  condition);//支持数据权限
		
		params.put("userName", enterprise.getEn_whichsystem().toUpperCase());//当前账套的用户名
		params.put("reportName", sysPrintSet.getReportname());//报表的名称
		params.put("whereCondition",condition);//条件
		params.put("printtype",sysPrintSet.getPrinttype()==null?"":sysPrintSet.getPrinttype());//输出方式
		params.put("title",sysPrintSet.getTitle()==null?"":sysPrintSet.getTitle());//标题
		return params;
	}
	@Override
	public Map<String, Object> batchPrint(String ids, String caller,String reportname,
			HttpServletRequest request) {
		SysPrintSet sysPrintSet=sysPrintSetDao.getSysPrintSet(caller,reportname);
		Enterprise enterprise = enterpriseDao.getEnterprise();
		Map<Object,Object> defaultMethodparams=new HashMap<Object, Object>();
		defaultMethodparams.put("ids",ids);
		executeDefault(caller,defaultMethodparams,"");
		System.out.println(caller.substring(0,caller.indexOf("!BatchPrint")));
		Object[] objs = baseDao.getFieldsDataByCondition("form", new String[] { "fo_table", "fo_keyfield"},"fo_caller='" + caller.substring(0,caller.indexOf("!BatchPrint")) + "'");// 先根据caller拿到对应table、主键、状态码字段
		String tab="",keyField="",condition="";
		if (objs != null) {
			tab=objs[0]==null?"":objs[0].toString();
			if (tab.contains(" ")) {
				tab = tab.substring(0, tab.indexOf(" "));
			}
			keyField=objs[1]==null?"":objs[1].toString();
		}
		//打印前执行逻辑
		//打印前执行逻辑
		if(sysPrintSet.getHandlermethod()!=null&&!"".equals(sysPrintSet.getHandlermethod())){
			executrHandlermethod(sysPrintSet.getHandlermethod(),new Object[] {ids, caller });
		}
		List<String> Sqls = new ArrayList<String>();
		if(!"".equals(tab)&&!"".equals(keyField)){
			condition="where "+keyField +" in ("+ids+")";
			//更新打印次数
			if(sysPrintSet.getCountfield()!=null&&!"".equals(sysPrintSet.getCountfield())){
				Sqls.add("update "+tab+" set "+sysPrintSet.getCountfield()+"=nvl("+sysPrintSet.getCountfield()+",0)+1 where "+keyField+" in ("+ids+")");
			}
			//更新打印状态状态码
			if(sysPrintSet.getStatusfield()!=null&&!"".equals(sysPrintSet.getStatusfield()))
				Sqls.add("update "+tab+" set "+sysPrintSet.getStatusfield()+"='已打印' where "+keyField+" in ("+ids+")");
			if(sysPrintSet.getStatuscodefield()!=null&&!"".equals(sysPrintSet.getStatuscodefield()))
				Sqls.add("update "+tab+" set "+sysPrintSet.getStatuscodefield()+"='PRINTED' where "+keyField+" in ("+ids+")");
			//记录日志
			baseDao.execute("insert into messagelog(ML_ID,ML_DATE,ML_MAN,ML_CONTENT,ML_RESULT,ML_SEARCH) "
					+ "select messageLog_SEQ.nextval,sysdate,'"+SystemSession.getUser().getEm_name()+"','批量打印','打印成功',"
							+ "'"+caller.substring(0,caller.indexOf("!BatchPrint"))+"|"+keyField+"='||"+keyField
							+" from "+tab+" where "+keyField+" in("+ids+")");//插入日志
		}
		baseDao.execute(Sqls);
		Map<String, Object> params = new HashMap<String, Object>();
		if(sysPrintSet.getDefaultcondition()!=null&&!"".equals(sysPrintSet.getDefaultcondition())){
			condition=condition+" and "+sysPrintSet.getDefaultcondition();
		}
		condition = appendCondition(sysPrintSet,  condition);//支持数据权限
		if(enterprise.getEn_Admin() == null ||!"jasper".equals(enterprise.getEn_Admin())){
			boolean accessible = UserAgentUtil.accessible(request, enterprise.getEn_intrajasperurl());
			if (accessible) {
				params.put("printurl", enterprise.getEn_intrajasperurl());//打印地址
			} else {
				params.put("printurl", enterprise.getEn_extrajasperurl());
			}
		}else{
			boolean accessible = UserAgentUtil.accessible(request, enterprise.getEn_printurl());
			if (accessible) {
				params.put("printurl", enterprise.getEn_printurl());//打印地址
			} else {
				params.put("printurl", enterprise.getEn_Url());
			}
		}
		params.put("userName", enterprise.getEn_whichsystem().toUpperCase());//当前账套的用户名
		params.put("reportName", sysPrintSet.getReportname());//报表的名称
		params.put("whereCondition",condition);//条件
		params.put("printtype",sysPrintSet.getPrinttype()==null?"":sysPrintSet.getPrinttype());//输出方式
		params.put("title",sysPrintSet.getTitle()==null?"":sysPrintSet.getTitle());//标题
		return params;
	}
	/**
	 * bom成本计算 打印前执行方法
	 * @param id
	 */
	public void bomPrint(Integer id,String caller){
	    String SQLStr = null, thisMonthercode = null;
		Object ob = baseDao.getFieldDataByCondition("bom", "bo_mothercode", "bo_id="+id);
		if(ob != null){
			thisMonthercode = ob.toString();
		}
		// 取出当月汇率
		double ThisUSDRate =Double.parseDouble(baseDao.getFieldDataByCondition("Currencys","nvl(max(cr_rate),0)"," cr_name='USD' and nvl(cr_status,' ')<>'已禁用'").toString());
		double ThisHKDRate =Double.parseDouble(baseDao.getFieldDataByCondition("Currencys","nvl(max(cr_rate),0)"," cr_name='HKD' and nvl(cr_status,' ')<>'已禁用'").toString());
		/*if (ThisUSDRate == 0) {
			BaseUtil.showError("币别表未设置美金汇率!");
		}  xzx 2017090191号问题反馈*/
		SQLStr = "update BomStruct  set bs_osprice=0 where bs_topbomid=" + id + " and bs_osprice is null ";
		baseDao.execute(SQLStr);
		SQLStr = "merge into BomStruct using (select cr_rate,cr_name from currencys where nvl(cr_status,' ')<>'已禁用') src on( bs_currency=cr_name) when matched then update set bs_l=(CASE WHEN bs_currency='RMB' then bs_purcprice/(1+bs_rate/100) ELSE bs_purcprice*cr_rate  END) where bs_topbomid="
				+ id + " and bs_topmothercode='"+thisMonthercode+"' and (nvl(bs_sonbomid,0)=0 or nvl(bs_sonbomid,0) in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ))  ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_m=bs_l*bs_baseqty where bs_topbomid=" + id + " and bs_topmothercode='"+thisMonthercode+"' and (nvl(bs_sonbomid,0)=0 or nvl(bs_sonbomid,0) in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ))";
		baseDao.execute(SQLStr);
		SQLStr = "merge into BomStruct using(select cr_rate,cr_name from currencys where nvl(cr_status,' ')<>'已禁用') src on(bs_currency=cr_name)when matched then update set bs_j=(CASE WHEN bs_currency='RMB' then bs_purcprice ELSE bs_purcprice*cr_rate END) where bs_topbomid="
				+ id + " and bs_topmothercode='"+thisMonthercode+"' and (nvl(bs_sonbomid,0)=0 or nvl(bs_sonbomid,0) in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 )) ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_k=bs_j*bs_baseqty where bs_topbomid=" + id + " and (nvl(bs_sonbomid,0)=0 or nvl(bs_sonbomid,0) in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 )) ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_n=CASE WHEN bs_currency='RMB' then bs_l ELSE bs_purcprice END where bs_topbomid=" + id + " and bs_topmothercode='"+thisMonthercode+"' and (nvl(bs_sonbomid,0)=0 or nvl(bs_sonbomid,0) in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 )) ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_o=bs_n*bs_baseqty where bs_topbomid=" + id + " and bs_topmothercode='"+thisMonthercode+"' and (nvl(bs_sonbomid,0)=0 or nvl(bs_sonbomid,0) in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 )) ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_remark='' where  bs_topbomid=" + id+" and bs_topmothercode='"+thisMonthercode+"' ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_usdrate=" + ThisUSDRate + ",bs_hkdrate=" + ThisHKDRate
				+ " where bs_topbomid=" + id+" and bs_topmothercode='"+thisMonthercode+"' ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_remark='father' where bs_topbomid=" + id + " and bs_topmothercode='"+thisMonthercode+"' and nvl(bs_sonbomid,0)>0 ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_remark='father' where bs_topbomid=" + id + " and bs_soncode='" + thisMonthercode + "' ";
		baseDao.execute(SQLStr);
		SQLStr = "update BomStruct set bs_currency='RMB',bs_purcprice=bs_osprice,bs_purcpricermb=0,bs_totalpurcpricermb=0,bs_totalpurcpriceusd=0 where bs_topbomid="
				+ id + " and bs_topmothercode='"+thisMonthercode+"' and (nvl(bs_sonbomid,0)>0 or bs_soncode='" + thisMonthercode + "') and nvl(bs_sonbomid,0) not in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ) ";
		baseDao.execute(SQLStr);
		SQLStr = "select bs_idcode,bs_soncode from BomStruct where bs_topbomid=" + id
				+ " and bs_topmothercode='"+thisMonthercode+"' and nvl(bs_sonbomid,0)>0 and nvl(bs_sonbomid,0) not in (select bo_id from bom left join bomlevel on bl_code=bo_level where nvl(bo_level,' ')='外购件BOM' or nvl(bl_ifpurchase,0)<>0 ) ORDER BY bs_level";
		SqlRowList rs = baseDao.queryForRowSet(SQLStr);
		while (rs.next()) {//bs_osprice 在存储过程中计算出来的值是含税的委外单价
			SQLStr = "SELECT sum(nvl(bs_m,0)),sum(nvl(bs_k,0)),sum(bs_o) from BomStruct WHERE bs_topbomid=" + id
					+ " and bs_topmothercode='"+thisMonthercode+"' and  bs_mothercode='" + rs.getString("bs_soncode") + "' ";
			SqlRowList rsthis = baseDao.queryForRowSet(SQLStr);
			if (rsthis.next()) {
				SQLStr = "update bomstruct set bs_m=round((" + rsthis.getString(1) + "+nvl(bs_osprice,0)/(1+nvl(bs_rate,0)/100)),8)*bs_baseqty,bs_k=round((" + rsthis.getString(2) + "+nvl(bs_osprice,0)),8)*bs_baseqty,bs_o="
						+ rsthis.getString(3) + " where bs_topbomid="+id+" and bs_idcode=" + rs.getString("bs_idcode");
				baseDao.execute(SQLStr);
			}
		}
		// 当前计算的主件自身
		SQLStr = "SELECT bs_topmothercode from BomStruct WHERE bs_topbomid=" + id
				+ " and bs_topmothercode='"+thisMonthercode+"' and bs_mothercode='" + thisMonthercode + "' ";
		SqlRowList rss = baseDao.queryForRowSet(SQLStr);
		if (rss.next()) {//bs_m 不含税成本 ，bs_k 含税成本
				Object a = baseDao.getFieldDataByCondition("BomStruct", "round(sum(nvl(bs_m,0)),8)", " bs_topbomid=" + id
						+ " and bs_mothercode='" + thisMonthercode + "'");
				Object b = baseDao.getFieldDataByCondition("BomStruct", "round(sum(nvl(bs_k,0)),8)", " bs_topbomid=" + id
						+ " and bs_mothercode='" + thisMonthercode + "'");
				Object c = baseDao.getFieldDataByCondition("BomStruct", "round(sum(nvl(bs_o,0)),8)", " bs_topbomid=" + id
						+ " and bs_mothercode='" + thisMonthercode + "'");
				SQLStr = "update bomstruct set bs_m=round((?+nvl(bs_osprice,0)/(1+nvl(bs_rate,0)/100)),8)*bs_baseqty,bs_k=round((?+nvl(bs_osprice,0)),8)*bs_baseqty,bs_o=? where bs_topbomid="+id+" and bs_soncode='"
						+ thisMonthercode + "' ";
				baseDao.execute(SQLStr,new Object[]{a,b,c});
		}
		SQLStr = "update BomStruct set bs_m=0 where bs_topbomid=" + id + " and bs_m is null ";
		baseDao.execute(SQLStr);
		//调用存储过程每个客户特殊的代码更新
		baseDao.procedure("SP_COSTCOUNT_AFTER", new Object[] { id});
		
    }
	public void executrHandlermethod(String handlermethod, Object[] args){//打印前执行方法
		@SuppressWarnings("rawtypes")
		Class[] argsClass = new Class[args.length];
		for (int i = 0, j = args.length; i < j; i++) {
			argsClass[i] = args[i].getClass();
		}
		try {
			Object object = ContextUtil.getBean("jasperReportPrintSeriveImpl");
			Method method = object.getClass().getMethod(handlermethod, argsClass);
			method.invoke(object, args);
		}  catch (Exception e) {
			if (e.getCause() != null) {
				String exName = e.getCause().getClass().getSimpleName();
				if (exName.equals("RuntimeException") || exName.equals("SystemException"))
					BaseUtil.showError(e.getCause().getMessage());
			}
			e.printStackTrace();
		}
	};
	public void executeDefault(String caller ,Map<Object,Object> params,String reportname){//执行默认的方法或存储过程
		Date now = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String SQLStr = "";
		String todate=params.get("todate")==null?"":params.get("todate").toString();
		String fromdate=params.get("fromdate")==null?"":params.get("fromdate").toString();
		String enddate=params.get("enddate")==null?"":params.get("enddate").toString();
		String idS=params.get("idS")==null?"":params.get("idS").toString();
		String dateRange=params.get("dateRange")==null?"":params.get("dateRange").toString();
		int bo_id = params.get("bo_id") == null?0:Integer.valueOf(params.get("bo_id").toString());
		Object ob=baseDao.getFieldDataByCondition("form","fo_flowcaller","fo_caller='"+caller+"'");
		String flowcaller = ob==null ? "":ob.toString();
		try{
			if("customzl".equals(caller)&&"PwAgeAll_custom".equals(reportname)){//自定义库存帐龄表
				baseDao.procedure("UPDATEADDTEMP", new Object[] { todate});
			}
			if("customzl".equals(caller)&&"ARAgeAll_custom".equals(reportname)){//自定义帐龄表--应收帐龄表
				baseDao.procedure("UPDATEYSZL", new Object[] { todate });
			}
			if("customzl".equals(caller)&&"APAgeAll_custom".equals(reportname)){//自定义帐龄表--应付帐龄表
				baseDao.procedure("UPDATEYFZL", new Object[] { todate });
			}
			if("customzl".equals(caller)&&"APAgeAll_Due_custom".equals(reportname)){//自定义帐龄表--到期应付帐龄表
				baseDao.procedure("UPDATEYFZL", new Object[] { todate }) ;	
			}
			if("customzl".equals(caller)&&"ARAgeAll_Due_custom".equals(reportname)){//自定义帐龄表--到期应收帐龄表
				baseDao.procedure("UPDATEYSZL", new Object[] { todate });
			}
			if("customzl".equals(caller)&&"PRERECAgeAll_custom".equals(reportname)){//自定义帐龄表--预收帐龄表
				baseDao.procedure("UPDATEPRERECZL", new Object[] { todate });
			}
			if("customzl".equals(caller)&&"PREPAYAgeAll_custom".equals(reportname)){//自定义帐龄表--预付帐龄表
				baseDao.procedure("UPDATEPREPAYZL", new Object[] { todate });
			}
			if("customzl".equals(caller)&&"GOODSSEND_custom".equals(reportname)){//自定义帐龄表--发出商品账龄
				baseDao.procedure("UPDATEGSINVOQTY", new Object[] { todate });
			}
			if("customzl".equals(caller)&&"GOODSSEND_custom_auto".equals(reportname)){//自定义帐龄表--发出商品账龄(自动生成应收发票)
				baseDao.procedure("UPDATEYSTEMQTY", new Object[] { todate });
			}
			if("customzl".equals(caller)&&"ESTIMATE_custom".equals(reportname)){//自定义帐龄表--应付暂估账龄
				baseDao.procedure("UPDATEESINVOQTY", new Object[] { todate });
			}
			if("customzl".equals(caller)&&"ESTIMATE_custom_auto".equals(reportname)){//自定义帐龄表--应付暂估账龄(自动生成应付发票)
				baseDao.procedure("UPDATEYFTEMQTY", new Object[] { todate });
			}
			if("customzl".equals(caller)&&"ARBill_OTRS_custom".equals(reportname)){//自定义帐龄表--其它应收账龄表
				baseDao.callProcedure("SP_REFRESHAGING.refresh_ar", new Object[] {todate.substring(0,4)+todate.substring(5,7)});
			}
			if("customzl".equals(caller)&&"APBill_OTRS_custom".equals(reportname)){//自定义帐龄表--其它应付账龄表
				baseDao.callProcedure("SP_REFRESHAGING.refresh_ap", new Object[] { todate.substring(0,4)+todate.substring(5,7) });
			}
			if("customzl".equals(caller)&&"ARAgeAll_ledger_custom".equals(reportname)){//自定义帐龄表--应收账龄表（总账）表
				baseDao.procedure("UPDATEYSZL", new Object[] { todate });
			}
			if("customzl".equals(caller)&&"APAgeAll_ledger_custom".equals(reportname)){//自定义帐龄表--应付账龄表（总账）表
				baseDao.procedure("UPDATEYFZL", new Object[] { todate });
			}
			//出入库明细表(过账日期)
			if ("ProdInOut!GZDetail!Print".equals(caller) ||"ProdInOut!GZDetail!Print".equals(flowcaller)) {
				baseDao.procedure("UPDATEJIECUN", new Object[] { fromdate, todate });
			}
			//派车费用统计
			if ("CarCostDetail!Print".equals(caller)||"CarCostDetail!Print".equals(flowcaller)) {
				baseDao.procedure("CARCOSTTOTAL", new Object[] { todate, fromdate, enddate });
			}
			//应收账龄分析表	应收发票汇总表 	应收帐龄表?
			if ("ARBill!CSAA!Print".equals(caller) || "ARBill!YSFP!Print".equals(caller) ||"ARBill!CSAA!Print".equals(flowcaller)) {
				baseDao.procedure("UPDATEYSZL", new Object[] { todate });
			}
			//应付账龄分析表	应付发票汇总表	
			if ("APBill!CSAA!Print".equals(caller) || "APBill!YFFP!Print".equals(caller) ||"APBill!CSAA!Print".equals(flowcaller)) {
				baseDao.procedure("UPDATEYFZL", new Object[] { todate });
			}
			//库存帐龄表
			if ("ProdInOut!ZNQuantity!Print".equals(caller) ||"ProdInOut!ZNQuantity!Print".equals(flowcaller)) {
				baseDao.procedure("UPDATEBAREMAIN", new Object[] { todate });
			}
			//未开票明细表	应收发票明细表
			if ("Receipt!NotKPDetail!Print".equals(caller) || "ARBill!YSFPDetail!Print".equals(caller)
					||"Receipt!NotKPDetail!Print".equals(flowcaller)) {
				baseDao.procedure("UPDATEYSTEMQTY", new Object[] { todate });
			}
			//应付未开票明细表   应付发票明细表
			if ("APBill!NOTKPDETAIL!Print".equals(caller) || "APBill!YFFPDetail!Print".equals(caller)
					||"APBill!NOTKPDETAIL!Print".equals(flowcaller)) {
				baseDao.procedure("UPDATEYFTEMQTY", new Object[] { todate });
			}
			//销售订单达成率表
			if ("Sale!SaleDC!Print".equals(caller)||"Sale!SaleDC!Print".equals(flowcaller)) {
				baseDao.procedure("UPDATESALEDETAILDCDATE", new Object[] { todate });
			}
			//期间库存表
			if ("ProdInOut!QijianProdcut!Print".equals(caller)||"ProdInOut!QijianProdcut!Print".equals(flowcaller)) {
				baseDao.procedure("updateProductWH", new Object[] { fromdate, todate });
			}
			//库存周转率分析表(仓库物料)
			if ("ProductWH!TurnoverFTY!Print".equals(caller)||"ProductWH!TurnoverFTY!Print".equals(flowcaller)) {
				baseDao.procedure("UPDATEKCFTY", new Object[] { fromdate, todate });
			}
			//库存周转率分析表(品牌)
			if ("ProductWH!Turnover!Print".equals(caller)||"ProductWH!Turnover!Print".equals(flowcaller)) {
				baseDao.procedure("UPDATEKCZZL", new Object[] { fromdate, todate });
			}
			//产品分析表
			if ("Sale!productANL!PrintDet".equals(caller)||"Sale!productANL!PrintDet".equals(flowcaller)) {
				baseDao.procedure("UPDATEPRODIOTEMP", new Object[] { fromdate, todate });
			}
			//集团库存账龄表(按品牌)	 集团库存账龄表(按物料) 
			if ("ProductSYB!brandtotal!group".equals(caller) || "ProductSYB!ProductTotal!group".equals(caller)
					||"ProductSYB!brandtotal!group".equals(flowcaller)) {
				baseDao.procedure("UPDATEADDTEMP", new Object[] { todate });
			}
			//资金日报表
			if("ALMONTH_TEMP".equals(caller)||"ALMONTH_TEMP".equals(flowcaller)){
				baseDao.procedure("SP_PRINT_ALMONTH", new Object[] { fromdate,todate });
			}
			//生产报废明细表
			if ("Make!Declare!Print".equals(caller)||"Make!Declare!Print".equals(flowcaller)) {
				baseDao.procedure("UPDATEMAKESCRAPE", new Object[] {});
			}
			//瑞萨数据读取
			if("ProductWH!PSIDAT!Print".equals(caller)||"ProductWH!PSIDAT!Print".equals(flowcaller)){
				baseDao.procedure("SP_DOALL", new Object[] { todate });
			}
			//集团各销售部利润读取
			if("repquery_jasper!group".equals(caller)||"repquery_jasper!group".equals(flowcaller)){
				String str =baseDao.callProcedure("SP_SALEDEPARTPRINT",new Object[] { fromdate,todate });
				if (str != null && !str.trim().equals("")) {
					BaseUtil.showError(str);
				}
			}
			//月COSTDOWN统计表
			if("Purchase!PriceCostDown!Print".equals(caller)||"Purchase!PriceCostDown!Print".equals(flowcaller)){
				baseDao.procedure("sp_updatepreprice",new Object[] { fromdate,todate,enddate });
			}
			//月COSTDOWN统计表(优软云)
			if("Purchase!PriceCostDownB2B!Print".equals(caller)||"Purchase!PriceCostDownB2B!Print".equals(flowcaller)){
				baseDao.procedure("sp_updatepreprice",new Object[] { fromdate,todate,enddate });
			}
			//客户信用执行报表
			if ("CustCreditReport_VIEW!Print".equals(caller)||"CustCreditReport_VIEW!Print".equals(flowcaller)) {
				baseDao.procedure("sp_CustCreditReport", new Object[] { todate });
			}
			//批号期间库存
			if ("Batch!Reserve!Print".equals(caller)||"Batch!Reserve!Print".equals(flowcaller)) {
				baseDao.procedure("UPDATEBAREMAIN", new Object[] { todate });
			}
			//预付账款明细表
			if ("PREPAY!CSAA!Print".equals(caller)||"PREPAY!CSAA!Print".equals(flowcaller)) {
				baseDao.procedure("UPDATEPREPAYZL", new Object[] { todate });
			}
			//借货未归还明细表
			if ("ProdInOut!BorrowUnReturn".equals(caller)||"ProdInOut!BorrowUnReturn".equals(flowcaller)) {
				baseDao.procedure("SP_UPDATENOWSUMQTY", new Object[] { todate });
			}
			//预收账龄分析表
			if ("PREREC!AgeAll!Print".equals(caller)||"PREREC!AgeAll!Print".equals(flowcaller)) {
				baseDao.procedure("UPDATEPRERECZL", new Object[] { todate });
			}
			//BOM配套表打印 BOM多级展开
			if("BOMStruct!bomptb!Print".equals(caller)||"BOMStruct!bomptb!Print".equals(flowcaller)){
				String str = baseDao.callProcedure("MM_BATCHBOMSTRUCT_ALL", new Object[] { SystemSession.getUser().getEm_id() });
				if (str != null && !str.trim().equals("")) {
					BaseUtil.showError(str);
				}
			}
			if ("Make!CKKCM!Print".equals(caller)||"Make!CKKCM!Print".equals(flowcaller)) {//在制仓库库存表(制单)
				SQLStr = "UPDATE MakeMaterial SET mm_wipqty=0,mm_wipamount=0 where mm_maid  in (select ma_id from make where ma_status<>'已结案' or (ma_status='已结案' and trunc(ma_actenddate)>to_date('"
						+ todate + "','yyyy-MM-dd'))) and (NVL(mm_wipqty,1)<>0 or NVL(mm_wipamount,1)<>0)";
				baseDao.execute(SQLStr);
				SQLStr = "UPDATE Make SET ma_wipqty=0 where  ma_status<>'已结案' or (ma_status='已结案' and trunc(ma_actenddate)>to_date('" + todate
						+ "','yyyy-MM-dd')) and nvl(ma_wipqty,1)<>0 ";
				baseDao.execute(SQLStr);
				if (format.parse(todate).getTime() < now.getTime()) {
					SQLStr = "UPDATE MakeMaterial SET mm_wipqty=nvl((SELECT SUM(pd_outqty-pd_inqty) FROM ProdInOut,ProdIODetail WHERE pd_piid=pi_id  AND pd_ordercode=mm_code AND pd_orderdetno=mm_detno AND trunc(pi_date)>to_date('"
							+ todate
							+ "','yyyy-MM-dd') AND pd_status>10  AND pi_class IN ('委外领料单','委外补料单','委外退料单','生产领料单','生产补料单','生产退料单')),0)-(select NVL(SUM(md_qty),0) from makescrap inner join makescrapdetail on ms_id=md_msid where md_mmcode=mm_code and md_detno=md_mmdetno and md_status=99 and trunc(ms_date)>to_date('"
							+ todate
							+ "','yyyy-MM-dd')) where mm_maid  in (select ma_id from make where ma_status<>'已结案' or (ma_status='已结案' and trunc(ma_actenddate)>to_date('"
						+ todate + "','yyyy-MM-dd')))";
					baseDao.execute(SQLStr);
					SQLStr = "UPDATE Make SET ma_wipqty=nvl((SELECT SUM(pd_inqty-pd_outqty) FROM ProdInOut,ProdIODetail WHERE pd_piid=pi_id AND  pd_ordercode=ma_code AND trunc(pi_date)>to_date('"
							+ todate
							+ "','yyyy-MM-dd') AND pd_status>10 AND pi_class IN ('完工入库单','委外验收单','委外验退单')),0) where  ma_status<>'已结案' or (ma_status='已结案' and trunc(ma_actenddate)>to_date('"
							+ todate + "','yyyy-MM-dd')) ";
					baseDao.execute(SQLStr);
				} else {
					SQLStr = "UPDATE MakeMaterial SET mm_wipqty=nvl((SELECT SUM(pd_outqty-pd_inqty) FROM ProdInOut,ProdIODetail WHERE pd_piid=pi_id AND  pd_ordercode=mm_code AND pd_orderdetno=mm_detno AND trunc(pi_date)>to_date('"
							+ now.getTime()
							+ "','yyyy-MM-dd') AND pd_status>10  AND pi_class IN ('委外领料单','委外补料单','委外退料单','生产领料单','生产补料单','生产退料单')),0)-(select NVL(SUM(md_qty),0) from makescrap inner join makescrapdetail on ms_id=md_msid where md_mmcode=mm_code and md_detno=md_mmdetno and md_status=99 and trunc(ms_date)>to_date('"
							+ now.getTime()
							+ "','yyyy-MM-dd')) where mm_maid  in (select ma_id from make where ma_status<>'已结案' or (ma_status='已结案' and trunc(ma_actenddate)>to_date('"
							+ now.getTime() + "','yyyy-MM-dd')))";
					baseDao.execute(SQLStr);
					SQLStr = "UPDATE Make SET ma_wipqty=nvl((SELECT SUM(pd_inqty-pd_outqty) FROM ProdInOut,ProdIODetail WHERE pd_piid=pi_id AND pd_ordercode=ma_code AND trunc(pi_date)>to_date('"
							+ now.getTime()
							+ "','yyyy-MM-dd') AND pd_status>10 AND pi_class IN ('完工入库单','委外验收单','委外验退单')),0) where  ma_status<>'已结案' or (ma_status='已结案' and trunc(ma_actenddate)>to_date('"
							+ now.getTime() + "','yyyy-MM-dd')) ";
					baseDao.execute(SQLStr);
				}
			}
			//制造单备料单(A4)
			if ("Make!StockLP!Print".equals(caller)||"Make!StockLP!Print".equals(flowcaller)) {// 更新打印状态
					baseDao.updateByCondition("make", "ma_printstatus='已打印'", "ma_code in('" + idS + "')");
			}
			//在制仓库库存表(汇总)
			if ("Make!CKKCS!Print".equals(caller)||"Make!CKKCS!Print".equals(flowcaller)) {
				SQLStr = "UPDATE MakeMaterial SET mm_wipqty=0,mm_wipamount=0 where mm_code  in (select ma_code from make where ma_status<>'已结案' or (ma_status='已结案' and trunc(ma_actenddate)>to_date('"
						+ todate + "','yyyy-MM-dd'))) and (NVL(mm_wipqty,1)<>0 or NVL(mm_wipamount,1)<>0)";
				baseDao.execute(SQLStr);
				SQLStr = "UPDATE Make SET ma_wipqty=0 where  ma_status<>'已结案' or (ma_status='已结案' and trunc(ma_actenddate)>to_date('" + todate
						+ "','yyyy-MM-dd')) and nvl(ma_wipqty,1)<>0 ";
				baseDao.execute(SQLStr);
					if (format.parse(todate).getTime() < now.getTime()) {
						SQLStr = "UPDATE MakeMaterial SET mm_wipqty=nvl((SELECT SUM(pd_outqty-pd_inqty) FROM ProdInOut,ProdIODetail WHERE pd_piid=pi_id AND pd_ordercode=mm_code AND pd_orderdetno=mm_detno AND trunc(pi_date)>to_date('"
								+ todate
								+ "','yyyy-MM-dd') AND pd_status>10  AND pi_class IN ('委外领料单','委外补料单','委外退料单','生产领料单','生产补料单','生产退料单')),0)-(select NVL(SUM(md_qty),0) from makescrap inner join makescrapdetail on ms_id=md_msid where md_mmcode=mm_code and md_detno=md_mmdetno and md_status=99 and trunc(ms_date)>to_date('"
								+ todate
								+ "','yyyy-MM-dd')) where mm_maid  in (select ma_id from make where ma_status<>'已结案' or (ma_status='已结案' and trunc(ma_actenddate)>to_date('"
								+ todate + "','yyyy-MM-dd')))";
						baseDao.execute(SQLStr);
						SQLStr = "UPDATE Make SET ma_wipqty=nvl((SELECT SUM(pd_inqty-pd_outqty) FROM ProdInOut,ProdIODetail WHERE pd_piid=pi_id AND pd_ordercode=ma_code AND trunc(pi_date)>to_date('"
								+ todate
								+ "','yyyy-MM-dd') AND pd_status>10 AND pi_class IN ('完工入库单','委外验收单','委外验退单')),0) where  ma_status<>'已结案' or (ma_status='已结案' and trunc(ma_actenddate)>to_date('"
								+ todate + "','yyyy-MM-dd')) ";
						baseDao.execute(SQLStr);
					} else {
						SQLStr = "UPDATE MakeMaterial SET mm_wipqty=nvl((SELECT SUM(pd_outqty-pd_inqty) FROM ProdInOut,ProdIODetail WHERE pd_piid=pi_id AND pd_ordercode=mm_code AND pd_orderdetno=mm_detno AND trunc(pi_date)>to_date('"
								+ now.getTime()
								+ "','yyyy-MM-dd') AND pd_status>10  AND pi_class IN ('委外领料单','委外补料单','委外退料单','生产领料单','生产补料单','生产退料单')),0)-(select NVL(SUM(md_qty),0) from makescrap inner join makescrapdetail on ms_id=md_msid where md_mmcode=mm_code and md_detno=md_mmdetno and md_status=99 and trunc(ms_date)>to_date('"
								+ now.getTime()
								+ "','yyyy-MM-dd')) where mm_maid  in (select ma_id from make where ma_status<>'已结案' or (ma_status='已结案' and trunc(ma_actenddate)>to_date('"
								+ now.getTime() + "','yyyy-MM-dd')))";
						baseDao.execute(SQLStr);
						SQLStr = "UPDATE Make SET ma_wipqty=nvl((SELECT SUM(pd_inqty-pd_outqty) FROM ProdInOut,ProdIODetail WHERE pd_piid=pi_id AND pd_ordercode=ma_code AND trunc(pi_date)>to_date('"
								+ now.getTime()
								+ "','yyyy-MM-dd') AND pd_status>10 AND pi_class IN ('完工入库单','委外验收单','委外验退单')),0) where  ma_status<>'已结案' or (ma_status='已结案' and trunc(ma_actenddate)>to_date('"
								+ now.getTime() + "','yyyy-MM-dd')) ";
						baseDao.execute(SQLStr);
					}
			}
			//工单调拨查询
			if ("Make!MAKETB!Print".equals(caller)&&idS != null && !idS.equals("")) {
				SQLStr = "UPDATE MakeMaterial SET mm_onhand=NVL((SELECT SUM(pw_onhand) FROM ProductWH WHERE "
						+ "pw_whcode='2110' AND pw_prodcode=mm_prodcode),0) WHERE NVL(mm_processcode,' ')<>' ' " + "AND mm_code IN ("
						+ idS + ")";
				baseDao.execute(SQLStr);
				SQLStr = "UPDATE MakeMaterial SET mm_onhandjg=nvl((SELECT SUM(pw_onhand) FROM ProductWH WHERE "
						+ "pw_whcode='1104' AND pw_prodcode=mm_prodcode),0) WHERE nvl(mm_processcode,' ')<>' ' " + "AND mm_code IN ("
						+ idS + ")";
				baseDao.execute(SQLStr);
				SQLStr = "UPDATE MakeMaterial SET mm_halfonhand=nvl((SELECT SUM(pw_onhand) FROM ProductWH WHERE "
						+ "pw_whcode='2108' AND pw_prodcode=mm_prodcode),0) WHERE nvl(mm_processcode,' ')<>' ' " + "AND mm_code IN ("
						+ idS + ")";
				baseDao.execute(SQLStr);
			}
			//工单素材查询
			if ("Make!MAKESC!Print".equals(caller)&&idS != null && !idS.equals("")) {
				SQLStr = "UPDATE MakeMaterial SET mm_onhand=nvl((SELECT SUM(pw_onhand) FROM ProductWH WHERE "
						+ "pw_whcode='2110' AND pw_prodcode=mm_prodcode),0) WHERE nvl(mm_processcode,' ')<>' ' " + "AND mm_code IN ("
						+ idS + ")";
				baseDao.execute(SQLStr);
				SQLStr = "UPDATE MakeMaterial SET mm_onhandjg=nvl((SELECT SUM(pw_onhand) FROM ProductWH WHERE "
						+ "pw_whcode='1104' AND pw_prodcode=mm_prodcode),0) WHERE nvl(mm_processcode,' ')<>' ' " + "AND mm_code IN ("
						+ idS + ")";
				baseDao.execute(SQLStr);
				SQLStr = "UPDATE MakeMaterial SET mm_halfonhand=nvl((SELECT SUM(pw_onhand) FROM ProductWH WHERE "
						+ "pw_whcode='2108' AND pw_prodcode=mm_prodcode),0) WHERE nvl(mm_processcode,' ')<>' ' " + "AND mm_code IN ("
						+ idS + ")";
				baseDao.execute(SQLStr);
			}
			//客户订单明细表
			if ("Sale!Detail!Print".equals(caller)||"Sale!Detail!Print".equals(flowcaller)) {
				SQLStr = "update saledetail set sd_finishqty=nvl((select sum(NVL(ma_madeqty,0)) from make where ma_prodcode=sd_prodcode and ma_salecode=sd_code and ma_saledetno=sd_detno),0) where sd_said in(select sa_id from sale "
						+ " where  sa_date  " + dateRange + ")";
				baseDao.execute(SQLStr);
				SQLStr = "update saledetail set sd_bzfinishqty=nvl((select sum(ma_madeqty) from make where ma_salecode=sd_code and ma_saledetno=sd_detno and ma_wcname='包装'),0) where  sd_said in(select sa_id from sale "
						+ " where  sa_date  " + dateRange + ")";
				baseDao.execute(SQLStr);
				SQLStr = "update saledetail set sd_zzfinishqty=nvl((select sum(ma_madeqty) from make where ma_salecode=sd_code and ma_saledetno=sd_detno and ma_wcname in ('制造一组装','制造二组装')),0) where sd_said in(select sa_id from sale "
						+ " where  sa_date  " + dateRange + ")";
				baseDao.execute(SQLStr);
				SQLStr = "update saledetail set sd_hhfinishqty=nvl((select sum(ma_madeqty) from make where ma_salecode=sd_code and ma_saledetno=sd_detno and ma_wcname='后焊'),0) where sd_said in(select sa_id from sale "
						+ " where  sa_date  " + dateRange + ")";
				baseDao.execute(SQLStr);
				SQLStr = "update saledetail set sd_smtfinishqty=nvl((select sum(ma_madeqty) from make where ma_salecode=sd_code and ma_saledetno=sd_detno and ma_wcname='SMT'),0) where sd_said in(select sa_id from sale "
						+ " where  sa_date  " + dateRange + ")";
				baseDao.execute(SQLStr);
				SQLStr = "update saledetail set sd_lastfinishdate=(select max(ba_date) from batch where ba_prodcode=sd_prodcode and ba_salecode=sd_code) where sd_said in(select sa_id from sale "
						+ " where  sa_date  " + dateRange + ")";
				baseDao.execute(SQLStr);
				SQLStr = "update saledetail set sd_lastfinishdate=' ' where sd_lastfinishdate<to_date('2000-1-1','yyyy-MM-dd')";
				baseDao.execute(SQLStr);
			}
			//未发货客户订单明细表
			if ("Sale!OutDetail!Print".equals(caller)||"Sale!OutDetail!Print".equals(flowcaller)) {
				SQLStr = "update saledetail set sd_finishqty=nvl((select sum(NVL(ma_madeqty,0)) from make where ma_prodcode=sd_prodcode and ma_salecode=sd_code and ma_saledetno=sd_detno),0) where sd_said in(select sa_id from sale "
						+ " where  sa_date  " + dateRange + ")";
				baseDao.execute(SQLStr);
				SQLStr = "update saledetail set sd_bzfinishqty=nvl((select sum(ma_madeqty) from make where ma_salecode=sd_code and ma_saledetno=sd_detno and ma_wcname='包装'),0) where  sd_said in(select sa_id from sale "
						+ " where  sa_date  " + dateRange + ")";
				baseDao.execute(SQLStr);
				SQLStr = "update saledetail set sd_zzfinishqty=nvl((select sum(ma_madeqty) from make where ma_salecode=sd_code and ma_saledetno=sd_detno and ma_wcname in ('制造一组装','制造二组装')),0) where sd_said in(select sa_id from sale "
						+ " where  sa_date  " + dateRange + ")";
				baseDao.execute(SQLStr);
				SQLStr = "update saledetail set sd_hhfinishqty=nvl((select sum(ma_madeqty) from make where ma_salecode=sd_code and ma_saledetno=sd_detno and ma_wcname='后焊'),0) where sd_said in(select sa_id from sale "
						+ " where  sa_date  " + dateRange + ")";
				baseDao.execute(SQLStr);
				SQLStr = "update saledetail set sd_smtfinishqty=nvl((select sum(ma_madeqty) from make where ma_salecode=sd_code and ma_saledetno=sd_detno and ma_wcname='SMT'),0) where sd_said in(select sa_id from sale "
						+ " where  sa_date  " + dateRange + ")";
				baseDao.execute(SQLStr);
				SQLStr = "update saledetail set sd_lastfinishdate=(select max(ba_date) from batch where ba_prodcode=sd_prodcode and ba_salecode=sd_code) where sd_said in(select sa_id from sale "
						+ " where  sa_date  " + dateRange + ")";
				baseDao.execute(SQLStr);
				SQLStr = "update saledetail set sd_lastfinishdate=' ' where sd_lastfinishdate<to_date('2000-1-1','yyyy-MM-dd')";
				baseDao.execute(SQLStr);		
			}
			//毛利润分析(按订单) 毛利润分析明细表
			if ("Sale!Orders!Print".equals(caller) || "Sale!MLR!Print".equals(caller)
					||"Sale!Orders!Print".equals(flowcaller)) {
				SQLStr = "merge into product using (select cd_prodcode,max(cd_bomcost) cd_bomcost from (select distinct cd_prodcode,cd_bomcost from (select cd_prodcode,cd_bomcost,cd_yearmonth,rank() over(partition by cd_prodcode order by cd_yearmonth desc) row_id from costdetail where nvl(cd_bomcost,0) > 0) where row_id=1) group by cd_prodcode) costdetail on (pr_code=cd_prodcode) when matched then update set pr_costtemp=cd_bomcost";
				baseDao.execute(SQLStr);
			}
			//超期欠款表
			if ("Receipt!CQQKDetail!Print".equals(caller)||"Receipt!CQQKDetail!Print".equals(flowcaller)) {
				String sql = "UPDATE ARBill SET ab_thispayamount=nvl(ab_payamount,0)-nvl((select sum(rbd_nowbalance) from recbalance,recbalancedetail where to_char(rb_date,'yyyymm')>'"
						+ todate + "' and rb_id=rbd_rbid and rb_status='已过账' and rbd_ordercode=ab_code),0)";
				baseDao.execute(sql);
			}
			if("ProdInOut!Sale!BatchPrint".equals(caller)&&idS != null && !idS.equals("")){
				baseDao.execute("update ProdIODetail set pd_ordertotal=round(nvl(pd_sendprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2),"
						+ "pd_total=round(nvl(pd_price,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2), pd_netprice=round(pd_sendprice/(1+nvl(pd_taxrate,0)/100),6) "
						+ "where pd_piid in(" + idS + ")");
				baseDao.execute("update  ProdIODetail set pd_netprice=round(pd_sendprice/(1+pd_taxrate/100),8),"
						+ "pd_nettotal=round(pd_sendprice*(nvl(pd_inqty,0)+nvl(pd_outqty,0))/(1+nvl(pd_taxrate,0)/100),2) "
						+ "where pd_piid in(" + idS + ")");
				baseDao.execute("update ProdInOut set pi_total=(SELECT sum(round(nvl(pd_sendprice,0)*(nvl(pd_inqty,0)+nvl(pd_outqty,0)),2)) FROM ProdIODetail "
						+ "WHERE pd_piid=pi_id) where pi_id in(" + idS + ")");
				baseDao.execute("update prodiodetail set pd_customprice=pd_sendprice, pd_taxamount=pd_ordertotal where pd_piclass in ('出货单','销售退货单') "
						+ "and pd_piid in(" + idS + ") and nvl(pd_customprice,0)=0");
				baseDao.execute("update ProdInOut set pi_totalupper=L2U(nvl(pi_total,0)) where pi_id in(" + idS + ")");
			}
			if("BOM!BOMCostDetail!Print".equals(caller)){//BOM成本计算，汇总打印
				bomPrint(bo_id,caller);
			}
			if("MakeSN".equals(caller)){
				boolean bool = (Boolean) params.get("isRepair");
				if(bool){
					baseDao.execute("update MakeSNList set msl_printstatus = -1 where msl_id in ("+idS+")");
				}else{
					baseDao.execute("update MakeSNList set msl_printstatus = -1 where msl_maid in ("+idS+")");
				}
			}
			if("ProdInOut!BarcodeIn".equals(caller)||"ProdInOut!BarcodeIn".equals(flowcaller)){
				String data=params.get("data")==null?"":params.get("data").toString();
				if(data==null || ("").equals(data)){
					baseDao.execute("update barcodeio set bi_printstatus = 1 where bi_piid ="+idS);					
				}else{
					baseDao.execute("update barcodeio set bi_printstatus = 1 where bi_piid ="+idS+" and bi_id in("+data+")");
				}
			}
			if("BarStock!BarcodePrint".equals(caller)||"BarStock!BarcodePrint".equals(flowcaller)){
				String data=params.get("data")==null?"":params.get("data").toString();
				if(data==null || ("").equals(data)){
					baseDao.execute("update barstocktakingdetaildet set bdd_printstatus = 1 where bdd_bsid ="+idS);					
				}else{
					baseDao.execute("update barstocktakingdetaildet set bdd_printstatus = 1 where bdd_bsid ="+idS+" and bdd_id in("+data+")");
				}
			}
			//物料收发明细表
			if("Prod!InOut!Detail!Print".equals(caller)||"Prod!InOut!Detail!Print".equals(flowcaller)){
				if(fromdate.isEmpty()||todate.isEmpty()){
					BaseUtil.showError("期间不能为空");
				}
				String str = baseDao.callProcedure("SP_PRINT_PRODINOUTDETAIL", new Object[] {fromdate,todate });
				if (str != null && !str.trim().equals("")) {
					BaseUtil.showError(str);
				}
			}
			//出入库汇总表（分仓库）
			if("ProdInOut!Whcode!Print".equals(caller)||"ProdInOut!Whcode!Print".equals(flowcaller)){
				if(todate.isEmpty()){
					BaseUtil.showError("期间不能为空");
				}
				String str = baseDao.callProcedure("SP_GREFRESHPRODMONTHNEW_PRINT", new Object[] {todate });
				if (str != null && !str.trim().equals("")) {
					BaseUtil.showError(str);
				}
			}
			//利润表
			if ("PROFITS".equals(caller)||"PROFITS".equals(flowcaller)) {
				String str =baseDao.callProcedure("SP_SALEDEPARTPRINT",new Object[] { todate });
				if (str != null && !str.trim().equals("")) {
					BaseUtil.showError(str);
				}
			}

		}catch(SystemException ex){
			BaseUtil.showError(ex.getMessage());
		}catch(Exception ex){
			System.out.println(ex.toString());
		}
	}
	@Override
	public Map<String, Object> getData(String condition, int page, int pageSize) {
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("data", sysPrintSetDao.getData(condition, page, pageSize));
		return map;
	}
	@Override
	public int getCount(String condition) {
		if("".equals(condition)){
			condition="1=1";
		}
		return baseDao.getCount("select count(1) from sysprintset where "+condition);
	}
	@Override
	public void save(String param) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "SysprintSet", "id");
		for (Map<Object, Object> s : gstore) {
			if(s.get("id") == null || s.get("id").equals("") || s.get("id").equals("0")
					|| Integer.parseInt(s.get("id").toString()) == 0){
				s.put("id", baseDao.getSeqId("SYSPRINTSET_SEQ"));
				String sql = SqlUtil.getInsertSqlByMap(s, "SysprintSet");
				gridSql.add(sql);
			}
		}		
		baseDao.execute(gridSql);
		//根据caller分组，记录大于一条且没有设置默认时，更新分组中id最小的记录为默认=-1
		baseDao.execute("update sysprintset set ISDEFAULT=-1 where id in(select min(id) from sysprintset group by "
				+ "caller having count(1)>1  and instr(WMSYS.WM_CONCAT(ISDEFAULT),-1)=0)");
		//根据caller分组，默认记录大于一条，更新分组中id不是最大的记录为默认=0
		baseDao.execute("update sysprintset set ISDEFAULT=0 where caller in(select caller from sysprintset  where "
				+ "ISDEFAULT=-1 group by caller having count(1)>1) and id not in(select max(id) from sysprintset "
				+ "where ISDEFAULT=-1 group by caller having count(1)>1 )");
	}
	@Override
	public void delete(int id) {
		baseDao.deleteByCondition("Sysprintset","id="+id);//删除设置
	}
	@Override
	public List<Map<String, Object>> getFields(String caller) {//按条件打印
		SqlRowList rs = baseDao.queryForRowSet("select id,title,reportname from  sysprintset where caller='" + caller + "'");
		return rs.getResultList();
	}

	public void setPrintType(HttpSession session){
		Object printtype=baseDao.getFieldDataByCondition("Enterprise","en_admin","1=1");
		session.setAttribute("en_admin", printtype);
	}
	/*
	 * 打印前配置方法
	 * */
	public void batchprintcheck(String ids,String caller){
		String res  = baseDao.callProcedure("SP_BATCHPRINTCHECK",
				new Object[] { ids,caller,SystemSession.getUser().getEm_code()});
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
	  }
	public String JasperGetReportnameByProcedure(String ids,String caller,String reportname){
		try {
			String res = baseDao.callProcedure("SP_JASPER", new Object[]{ ids,caller,reportname});
			//System.out.println(res.indexOf("#"));根据#号的位置来判断是否有配置报表
			if (res.indexOf("#")<=0){
				return "";
			}
			return res;
		} catch (Exception e) {
			// TODO: handle exception
			return "";
		}
	}
}
