package com.uas.mobile.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.drools.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.dao.common.SearchDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.EmpsJobs;
import com.uas.erp.model.SearchTemplate;
import com.uas.erp.model.SysNavigation;
import com.uas.mobile.service.QueryInfoService;

@Service("queryInfoService")
public class QueryInfoServiceImpl implements QueryInfoService {

	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private SearchDao searchDao;

	@Override
	public Map<String, Object> getInfoByCode(String emcode,String condition) {
		Employee emp= employeeDao.getEmployeeByEmcode(emcode);
		if (emp==null)BaseUtil.showError("员工编号不正确");
		Map<String,Object> data=new HashMap<String,Object>();
		Map<String,Object> model=null;
		Map<String,Object> node=null;
		List<Object> list=null;
		List<Object> models=new LinkedList<Object>();
		Object Id = baseDao.getFieldDataByCondition("SYSNAVIGATION", "sn_id", "SN_DISPLAYNAME='公司报表管理' and sn_using=1 and sn_parentid=0 ");
		List<Object[]> reportNames = baseDao.getFieldsDatasByCondition("sysprintset",new String[]{"reportname","caller"}, "reportname is not null AND CALLER IS NOT NULL");
		if(Id!=null){
			List<SysNavigation> sysNav = getSysNav(emp,Id.toString(),"1=1");		
			for(SysNavigation s:sysNav){
				if("F".equals(s.getSn_isleaf())) {
					List<SysNavigation> nav = getSysNav(emp,String.valueOf(s.getSn_Id()),"1=1");//	倒数第2层节点	
					for(SysNavigation sn:nav){
						model=new HashMap<String,Object>();
						list=new LinkedList<Object>();
						model.put("modelName", sn.getSn_DisplayName());
						List<SysNavigation> leafs = getSysNav(emp,String.valueOf(sn.getSn_Id()),"1=1");//叶子节点
							for(SysNavigation leaf:leafs){
								int count = baseDao.getCount("select count(*) from form where FO_MOBILEUSED=-1 and fo_caller='"+leaf.getSn_caller()+"'");
								if(count>0){
									String report=null;
									node=new HashMap<String,Object>();
									node.put("title", leaf.getSn_DisplayName());
									node.put("caller",leaf.getSn_caller());
									for(Object[] name:reportNames){
										if(name[1].equals(leaf.getSn_caller())){
											report=name[0].toString();
											break;
										}	
									}
									node.put("reportName",report==null?"":report );
									list.add(node);
								}						
							}
							model.put("list",list);
							if(list.size()>0)
							models.add(model);
					}				
				}
			}
		}else{
			//没有公司报表管理模块时
			Map<Integer,Integer> parentId=new LinkedHashMap<Integer,Integer>();
			List<Object> callers = baseDao.getFieldDatasByCondition("form", "fo_caller", "FO_MOBILEUSED=-1");
			List<SysNavigation> reports=expendReport("0",emp,"1=1",callers);//获取全部报表界
			for(SysNavigation s:reports){	
				int i=s.getSn_ParentId();
				parentId.put(i,i);
			}
			if(parentId.size()>0){
				List<Integer> ids=new LinkedList<Integer>();
				Set<Entry<Integer,Integer>> set = parentId.entrySet();
				Iterator<Entry<Integer, Integer>> it = set.iterator();
				while(it.hasNext()) {
					Entry<Integer, Integer> ex = it.next();
					ids.add(ex.getKey());//获得每个报表叶子节点的父节点
				}
				for(Integer id:ids){
					model=new HashMap<String,Object>();
					Object model1 = baseDao.getFieldDataByCondition("sysnavigation", "sn_displayname", "sn_id="+id);
					model.put("modelName", model1==null?"":model1);//每个报表模块的名字
					list=new LinkedList<Object>();
					List<SysNavigation> nav = getSysNav(emp,id.toString(),"1=1");
					for(SysNavigation s:nav){//又得到该报表的界面
						if(s.getSn_caller()!=null){
						for(Object caller:callers){
								if(s.getSn_caller().equals(caller)){
									String report=null;
									node=new HashMap<String,Object>();
									node.put("title", s.getSn_DisplayName());
									node.put("caller",s.getSn_caller());
									for(Object[] name:reportNames){
										if(name[1].equals(s.getSn_caller())){
											report=name[0].toString();
											break;
										}	
									}
									node.put("reportName",report==null?"":report );
									list.add(node);
									break;
									}							
							}
						}
					}
					model.put("list",list);
					if(list.size()>0)
						models.add(model);
				}
			}
		}		
		data.put("data", models);
		return data;
	}
	
	private List<SysNavigation> expendReport(String parentId,Employee emp,String con,List<Object> callers){
		List<SysNavigation> nav = getSysNav(emp,parentId,con);
		List<SysNavigation> leaf=new LinkedList<SysNavigation>();
		for(SysNavigation s:nav){
			if("F".equals(s.getSn_isleaf())){
				leaf.addAll(expendReport(String.valueOf(s.getSn_Id()),emp,con,callers));
			}else{
				if(s.getSn_caller()!=null){
					for(Object caller:callers){
						if(s.getSn_caller().equals(caller)){
							leaf.add(s);
							break;
						}	
					}						
				}
			}
		}
		return leaf;		
	}

	private List<SysNavigation> getSysNav(Employee employee,String parentId,String condition){
		StringBuffer sb = new StringBuffer();
		sb.append("select * from sysnavigation where ");
		sb.append("sn_parentid=? and sn_using=1 and case when sn_isleaf='T' then nvl(sn_url,' ') else 'T' end <> ' '");
		if (!"admin".equals(employee.getEm_type()) ) {
			sb.append(" and (case when sn_isleaf='F' then to_char(sn_id) else sn_caller end in(select distinct pp_caller from positionpower where (pp_alllist=1 or pp_selflist=1 or pp_see=1) and pp_joid in(");
			StringBuffer jobIds = new StringBuffer(String.valueOf(employee.getEm_defaulthsid()));
			if (employee.getEmpsJobs() != null) {
				for (EmpsJobs empsJob : employee.getEmpsJobs()) {
					jobIds.append(",").append(empsJob.getJob_id());
				}
			}
			sb.append(jobIds).append("))");
			sb.append(" or case when sn_isleaf='F' then to_char(sn_id) else sn_caller end in(select distinct pp_caller from personalpower where (pp_alllist=1 or pp_selflist=1 or pp_see=1) and pp_emid=");
			sb.append(employee.getEm_id()).append("))");
		}
		if (condition != null && !"".equals(condition)) {
			sb.append(" AND ");
			sb.append(condition);
		}
		sb.append(" order by sn_detno");
		List<SysNavigation> sns = baseDao.getJdbcTemplate().query(sb.toString(), new BeanPropertyRowMapper<SysNavigation>(SysNavigation.class),
					parentId);	
		return sns;
	}
	
	@Override
	public Map<String,Object> getReportCondition(String caller,String title){
		Map<String,Object> map=new HashMap<String,Object>();
		List<Object> list=new LinkedList<Object>();
		Map<String,Object> field=null;
		Object con = baseDao.getFieldDataByCondition("sysprintset", " DISTINCT DEFAULTCONDITION", "caller='"+caller+"'");
		map.put("defaultCondition", con==null?"":con);
		List<Object[]> datas = baseDao.getFieldsDatasByCondition("formdetail", new String[]{"fd_caption","fd_field","fd_type","fd_readOnly"}, "fd_foid=(select fo_id from form where fo_caller='"+caller+"')");
		for(Object[] obj:datas){
			field=new HashMap<String,Object>();
			if("C".equals(obj[2])||"EC".equals(obj[2])) {
				List<Object[]> comboxs = baseDao.getFieldsDatasByCondition("datalistcombo",new String[]{"dlc_display","dlc_value"}, "dlc_fieldname='"+obj[1]+"' and dlc_caller='"+caller+"' order by dlc_detno");
				List<Object> pros=new LinkedList<Object>();
				for(Object[] combox:comboxs){
					Map<String,Object> pro=new HashMap<String,Object>();
					pro.put("display", combox[0]);
					pro.put("value", combox[1]);
					pros.add(pro);
				}
				if(pros.size()>0)
					field.put("properties", pros);
			}
			field.put("title",obj[0]==null?"":obj[0]);
			field.put("field",obj[1]==null?"":obj[1]);
			field.put("type",obj[2]==null?"":obj[2]);
			field.put("readOnly",obj[3]==null?"":obj[3]);
			if(!"T".equals(obj[3]))
			list.add(field);
		}
		map.put("listCondition",list);
		return map;
	}
	
	@Override
	public Map<String,Object> getQueryJsp(String code) {
		// TODO Auto-generated method stub
		Employee emp= employeeDao.getEmployeeByEmcode(code);
		Map<String,Object> model=null;
		List<Object> schs=null;
		Map<String,Object> map=new HashMap<String,Object>();
		List<Object> lists=new LinkedList<Object>();
		Map<Integer,Integer> parentId=new LinkedHashMap<Integer,Integer>();
		if (emp==null)BaseUtil.showError("员工编号不正确");
		List<SysNavigation> list = expend("0",emp,"1=1");
		for(SysNavigation s:list){	
			int i=s.getSn_ParentId();
			parentId.put(i,i);
		}
		if(parentId.size()>0){
			List<Integer> ids=new LinkedList<Integer>();
			Set<Entry<Integer,Integer>> set = parentId.entrySet();
			Iterator<Entry<Integer, Integer>> it = set.iterator();
			while(it.hasNext()) {
				Entry<Integer, Integer> ex = it.next();
				ids.add(ex.getKey());//获得每个查询叶子节点的父节点
			}
		
			for(Integer id:ids){
				model=new HashMap<String,Object>();
				List<Object> nodes=new LinkedList<Object>();
				Map<String,Object> node=null;
				Object[] data = baseDao.getFieldsDataByCondition("sysnavigation", "sn_displayname", "sn_id="+id);
				model.put("modelName", data[0]==null?"":data[0]);//每个查询模块的名字
				//model.put("caller",data[1]==null?"":data[1]);
				List<SysNavigation> nav = getSysNav(emp,id.toString(),"1=1");
				for(SysNavigation s:nav){//又得到该模块的查询界面
					String url=null;
					if(s.getSn_Url()!=null&&(url=s.getSn_Url()).contains("search.jsp")){
					node=new HashMap<String,Object>();
					schs=new LinkedList<Object>();
					String caller=null;
					node.put("title", s.getSn_DisplayName());
					node.put("caller", s.getSn_caller());
					if(url!=null)
						caller=(url.length()>url.indexOf("=")+1)?url.substring(url.indexOf("=")+1,url.contains("&")?url.indexOf("&"):url.length()):"";
						List<Object[]> sch = baseDao.getFieldsDatasByCondition("searchtemplate", new String[]{"st_id","st_title"}, "st_caller='"+caller+"' and st_appuse=1");		
					for(Object[] obj:sch){//每个APP查询界面的方案
						Map<String,Object> scheme=new HashMap<String,Object>();
						scheme.put("scheme", obj[1]);
						scheme.put("schemeId",obj[0]);
						schs.add(scheme);
					}		
					node.put("schemes", schs);
					if(schs.size()>0)
					nodes.add(node);	
				}
				}
				model.put("list",nodes);
				if(nodes.size()>0)
				lists.add(model);
			}
		}
		map.put("data", lists);
		return map;
	}
	
	private List<SysNavigation> expend(String parentId,Employee emp,String con){
		List<SysNavigation> nav = getSysNav(emp,parentId,con);
		List<SysNavigation> leaf=new LinkedList<SysNavigation>();
		for(SysNavigation s:nav){
			if("F".equals(s.getSn_isleaf())){
				leaf.addAll(expend(String.valueOf(s.getSn_Id()),emp,con));
			}else{
				if(s.getSn_Url().contains("jsps/common/search.jsp"))
				leaf.add(s);
			}
		}
		return leaf;		
	}
	
	@Override
	public Map<String, Object> getSchemeConditin(String caller, String id) {
		// TODO Auto-generated method stub
		Map<String,Object> map=new HashMap<String,Object>();
		Map<String,Object> data=null;
		List<Object> list=new LinkedList<Object>();
		String type="S";
		List<Object[]> cons = baseDao.getFieldsDatasByCondition("searchtemplategrid",new String[]{"stg_text","stg_field","stg_type","stg_value","stg_width","stg_appcondition","stg_mode","stg_detno","stg_table","stg_format"} , "stg_appuse=1 and  stg_stid="+id);
			for(Object[] con:cons){
				data=new HashMap<String,Object>();
				if(con[2]!=null){
					if("NUMBER".equals(con[2])){
						type="Ym".equals(con[9])?"YM":"N";
					}
					else if("DATE".equals(con[2])){
						type=con[3]==null?"D":"CD";
					}else if("Ym".equals(con[9])){
						type="YM";
					}	
					else type="S";
				}
				if(con[6]!=null){
					if("combobox".equals(con[6].toString()))
						type="C";
					else if("checkboxgroup".equals(con[6].toString()))
						type="CBG";
					else if("radiogroup".equals(con[6].toString()))
						type="R";	
				}
				if("YM".equals(type)){
					if(con[1]!=null&&con[1].toString().contains("YM_VIEW_PARAM$"))
						type="YMV";
				}
				List<Object[]> comboxs = baseDao.getFieldsDatasByCondition("SearchTemplateProp", new String[]{"display","value"}, "st_id="+id+" and stg_field=upper('"+con[1]+"')");
				List<Object> pros=new LinkedList<Object>();
				for(Object[] com:comboxs){
					Map<String,Object> pro= new HashMap<String,Object>();
					pro.put("display", com[0]);
					pro.put("value", com[1]);
					pros.add(pro);
				}
				if(pros.size()>0)
				data.put("properties", pros);	
				data.put("caption", con[0]==null?"":con[0]);
				data.put("field", con[1]==null?"":con[1]);
				data.put("type", type);
				data.put("defaultValue", con[3]);
				data.put("width", con[4]==null?"":con[4]);
				data.put("appCondition","1".equals(con[5].toString())?true:false);
				data.put("position","COL_"+con[7] );
				data.put("table",con[8]);
				if(con[0]!=null)
				list.add(data);
			} 	
		map.put("data",list);
		return map;
	}
	
	@Override
	public Map<String,Object> getSchemeResult(String caller, Integer id, int pageIndex,
			int pageSize, String filterCondition) {
		// TODO Auto-generated method stub
		int start=(pageIndex-1)*pageSize+1;
		int end=pageIndex*pageSize;
		Map<String,Object> lines=new HashMap<String,Object>();
		List<Map<String,Object>> fieldsMap=new LinkedList<Map<String,Object>>();
		SearchTemplate st = searchDao.getSearchTemplate(id, SpObserver.getSp());
		if(st==null)BaseUtil.showError("该方案不存在!");
		String condition =st.getSt_condition();
		String limitCondition = parseLimits(st.getSt_limits());
		if (!StringUtils.isEmpty(filterCondition)) {
			if (!StringUtils.isEmpty(condition)) {
				condition = "(" + condition + ") AND (" + filterCondition + ")";
			} else
				condition = filterCondition;
		}
		if (!StringUtils.isEmpty(limitCondition)) {
			if (!StringUtils.isEmpty(condition)) {
				condition = "(" + condition + ") AND (" + limitCondition + ")";
			} else
				condition = limitCondition;
		}
		String sql=st.getSql(condition, st.getSt_sorts(), start, end);
		StringBuffer sb =new StringBuffer(sql.replace("A WHERE ROWNUM <= "+end, "A "));
		sb.append(" and rn<="+end);
		List<Map<String,Object>> lists = baseDao.queryForRowSet(sql).getResultList();
		//字段转换
		List<Object[]> fields = baseDao.getFieldsDatasByCondition("searchtemplategrid", new String[]{"stg_detno","stg_field"},  "stg_stid="+id+" order by stg_detno");
		Map<String,Object> data=null;
		for(Map<String,Object> map:lists){
			data=new HashMap<String,Object>();
			Set<String> keys = map.keySet();
			for(String key:keys){
				for(Object[] field:fields){
					if(("COL_"+field[0]).equals(key)){
						data.put(field[1].toString(),map.get(key));
					}
				}
			}
		if(data.size()>0){
			fieldsMap.add(data);
			}
		}
		lines.put("listdata", fieldsMap);
		//合计字段统计
		Map<String,Object> sum=new HashMap<String,Object>();
		Object table = baseDao.getFieldDataByCondition("searchtemplate", "st_tablesql", "st_id="+id);
		List<Object[]> sumField = baseDao.getFieldsDatasByCondition("searchtemplategrid", new String[]{"stg_field","stg_table","STG_FORMULA"}, "stg_stid="+id+" and  stg_sum=1 and stg_type like 'NUMBER%'");
		for(Object[] field:sumField){
			String sfield=field[1]==null?field[2].toString():field[1]+"."+field[0];
			Double amount = baseDao.getSummaryByField(table.toString(), sfield, condition==null?"rownum<30001":condition+"and rownum<30001");
			sum.put(field[0].toString(),amount);
		}
		if(sum.size()>0){
			lines.put("summaryField",sum);
		}
		return lines;
	}
	
	/**
	 * 权限约束，解析成SQL
	 * 
	 * @return
	 */
	private static String parseLimits(String limits) {
		if (limits != null && !"".equals(limits)) {
			Employee employee = SystemSession.getUser();
			if (limits.startsWith("CU")) {
				String custCol = null;
				String emCol = null;
				if (limits.contains(",")) {
					custCol = limits.substring(3, limits.indexOf(","));
					emCol = limits.substring(limits.indexOf(",") + 1, limits.lastIndexOf(")"));
				} else {
					custCol = limits.substring(3, limits.indexOf(")"));
				}
				return (emCol != null ? emCol + "='" + employee.getEm_code() + "' AND " : "")
						+ "EXISTS (SELECT 1 FROM CustomerDistr C_1 WHERE C_1.CD_CUSTCODE=" + custCol + " AND C_1.CD_SELLERCODE='"
						+ employee.getEm_code() + "')";
			} else if (limits.startsWith("VE")) {
				String vendCol = null;
				String emCol = null;
				if (limits.contains(",")) {
					vendCol = limits.substring(3, limits.indexOf(","));
					emCol = limits.substring(limits.indexOf(",") + 1, limits.lastIndexOf(")"));
				} else {
					vendCol = limits.substring(3, limits.indexOf(")"));
				}
				return (emCol != null ? emCol + "='" + employee.getEm_code() + "' AND " : "")
						+ "EXISTS (SELECT 1 FROM VendorDistr V_1 WHERE V_1.VD_VECODE=" + vendCol + " AND V_1.VD_PERSONCODE='"
						+ employee.getEm_code() + "')";
			} else if (limits.startsWith("EM")) {
				String emCol = limits.substring(limits.indexOf("(") + 1, limits.lastIndexOf(")"));
				return emCol + "='" + employee.getEm_code() + "'";
			}
		}
		return null;
	}
	
}
