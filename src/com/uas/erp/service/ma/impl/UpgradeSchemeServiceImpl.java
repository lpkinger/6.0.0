package com.uas.erp.service.ma.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.ma.UpgradeSchemeService;
@Service
public class UpgradeSchemeServiceImpl implements UpgradeSchemeService {
	@Autowired BaseDao baseDao;
	@Override
	public List<Map<String, Object>> check(String ids) {	
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Object callers=baseDao.getFieldDataByCondition("sysnavigation", "LOB_CONCAT(''''||sn_caller||'''')", "sn_id in("+ids+")");
		//SQL语句
		Map<String, Object> map_sql = new HashMap<String, Object>();
		Set<Object>  sql_=new HashSet<Object>();
		map_sql.put("title", "SQL语句");
		map_sql.put("type","sql_");
		map_sql.put("data", sql_);
		result.add(map_sql);
		
		/*type :form_
		 * 检查配置：form  formdetail  dbfindsetui  relativesearch  relativesearchform  relativesearchgrid  
		 * 		  jprocessdeploy   jprocessset  sysprintset
		 */
		Set<Object>  combo_=new HashSet<Object>();
		Map<String, Object> map_form = new HashMap<String, Object>();
		Set<Object>  form_=new HashSet<Object>();
		Set<Object>  table_=new HashSet<Object>();
		List<Object[]> configs_form=baseDao.getFieldsDatasByCondition("form",new String[] { "fo_caller","fo_table","fo_detailtable"},"fo_caller in("+callers+")");
		String tab="";
		for (Object[] obj : configs_form) {
			if(obj[0]!=null&&!"".equals(obj[0])) form_.add(obj[0]);
			if(obj[1]!=null&&!"".equals(obj[1])) {
				tab=obj[1].toString().toUpperCase();
				if (tab.contains(" ")) {
					tab=tab.substring(0, tab.indexOf(" "));
				}
				table_.add(tab);
			}
			if(obj[2]!=null&&!"".equals(obj[2])) {
				tab=obj[2].toString().toUpperCase();
				if (tab.contains(" ")) {
					tab=tab.substring(0, tab.indexOf(" "));
				}
				table_.add(tab);
			}
		}
		map_form.put("title","涉及的Form配置(Form、FormDetail、DbfindSetUI、RelativeSearch、RelativeSearchForm、RelativeSearchGrid)");
		map_form.put("type","form_");
		map_form.put("data", form_);
		result.add(map_form);
		/*type :detailgrid_
		 * 检查配置：detailgrid  dbfindset  dbfindsetdetail  dbfindsetgrid  gridbutton 
		 */
		Map<String, Object> map_detailgrid = new HashMap<String, Object>();
		Set<Object>  detailgrid_=new HashSet<Object>();
		List<Object> callers_detailgrid=baseDao.getFieldDatasByCondition("detailgrid","dg_caller","dg_caller in("+callers+")");
		for (Object caller : callers_detailgrid) {
			detailgrid_.add(caller);
		}
		map_detailgrid.put("title", "涉及的Grid配置(DetailGrid、DbfindSet、DbfindSetDetail、DbfindSetGrid、gridbutton)");
		map_detailgrid.put("type","detailgrid_");
		map_detailgrid.put("data", detailgrid_);
		result.add(map_detailgrid);
		
		//datalist  datalistdetail
		Map<String, Object> map_datalist = new HashMap<String, Object>();
		Set<Object>  datalist_=new HashSet<Object>();
		//List<Object> callers_datalist
		List<Object[]> configs_datalist=baseDao.getFieldsDatasByCondition("datalist",new String[] {"dl_caller","dl_relative"},"dl_caller in("+callers+")");
		for (Object[] obj : configs_datalist) {
			if(obj[0]!=null&&!"".equals(obj[0])) datalist_.add(obj[0]);
			if(obj[1]!=null&&!"".equals(obj[1])) {
				datalist_.add(obj[1]);
				combo_.add(obj[1]);
			}
		}
		map_datalist.put("title", "涉及的列表配置(DataList、DataListDetail)");
		map_datalist.put("type","datalist_");
		map_datalist.put("data", datalist_);
		result.add(map_datalist);
		//datalistcombo
		Map<String, Object> map_combo = new HashMap<String, Object>();
		//Set<Object>  combo_=new HashSet<Object>();
		List<Object> callers_datalistcombo=baseDao.getFieldDatasByCondition("datalistcombo","dlc_caller","dlc_caller in("+callers+")");
		for (Object caller : callers_datalistcombo) {
			combo_.add(caller);
		}
		map_combo.put("title", "涉及的下拉配置(datalistcombo)");
		map_combo.put("type","combo_");
		map_combo.put("data", combo_);
		result.add(map_combo);
		//PostStyle、PostStyleStep、PostStyleDetail
		Map<String, Object> map_poststyle = new HashMap<String, Object>();
		Set<Object>  poststyle_=new HashSet<Object>();
		List<Object> callers_poststyle=baseDao.getFieldDatasByCondition("PostStyle","ps_caller","ps_caller in("+callers+")");
		for (Object caller : callers_poststyle) {
			poststyle_.add(caller);
		}
		map_poststyle.put("title", "涉及的同步配置(PostStyle、PostStyleStep、PostStyleDetail)");
		map_poststyle.put("type","poststyle_");
		map_poststyle.put("data", poststyle_);
		result.add(map_poststyle);
		//Transfers、TransferDetail
		Map<String, Object> map_transfers = new HashMap<String, Object>();
		Set<Object>  transfers_=new HashSet<Object>();
		List<Object> callers_transfers=baseDao.getFieldDatasByCondition("Transfers","tr_caller","tr_caller in("+callers+")");
		for (Object caller : callers_transfers) {
			transfers_.add(caller);
		}
		map_transfers.put("title", "涉及的转单配置(Transfers、TransferDetail)");
		map_transfers.put("type","transfers_");
		map_transfers.put("data", transfers_);
		result.add(map_transfers);
		//涉及的出入库配置(DocumentSetup)
		Map<String, Object> map_documentsetup = new HashMap<String, Object>();
		Set<Object>  documentsetup_=new HashSet<Object>();
		List<Object> callers_documentsetup=baseDao.getFieldDatasByCondition("DocumentSetup","ds_table","ds_table in("+callers+")");
		for (Object caller : callers_documentsetup) {
			documentsetup_.add(caller);
		}
		map_documentsetup.put("title", "涉及的出入库配置(DocumentSetup)");
		map_documentsetup.put("type","documentsetup_");
		map_documentsetup.put("data", documentsetup_);
		result.add(map_documentsetup);
		//涉及的初始化配置(Initialize、InitDetail)
		Map<String, Object> map_initialize = new HashMap<String, Object>();
		Set<Object>  initialize_=new HashSet<Object>();
		List<Object> callers_initialize=baseDao.getFieldDatasByCondition("initialize","in_caller","in_caller in("+callers+")");
		for (Object caller : callers_initialize) {
			initialize_.add(caller);
		}
		map_initialize.put("title", "涉及的初始化配置(Initialize、InitDetail)");
		map_initialize.put("type","initialize_");
		map_initialize.put("data", initialize_);
		result.add(map_initialize);
		//涉及的系统参数配置(Configs、ConfigProp)
		Map<String, Object> map_configs = new HashMap<String, Object>();
		Set<Object>  configs_=new HashSet<Object>();
		List<Object> callers_configs=baseDao.getFieldDatasByCondition("configs","caller","caller in("+callers+")");
		for (Object caller : callers_configs) {
			configs_.add(caller);
		}
		map_configs.put("title", "涉及的系统参数配置(Configs、ConfigProp)");
		map_configs.put("type","configs_");
		map_configs.put("data", configs_);
		result.add(map_configs);
		//涉及的业务逻辑配置(Interceptors)
		Map<String, Object> map_interceptors = new HashMap<String, Object>();
		Set<Object>  interceptors_=new HashSet<Object>();
		List<Object> callers_interceptors=baseDao.getFieldDatasByCondition("interceptors","caller","caller in("+callers+")");
		for (Object caller : callers_interceptors) {
			interceptors_.add(caller);
		}
		map_interceptors.put("title", "涉及的业务逻辑配置(Interceptors)");
		map_interceptors.put("type","interceptors_");
		map_interceptors.put("data", interceptors_);
		result.add(map_interceptors);
		//涉及的特殊权限(SysSpecialPower)
		Map<String, Object> map_sysspecialpower = new HashMap<String, Object>();
		Set<Object>  sysspecialpower_=new HashSet<Object>();
		List<Object> callers_sysspecialpowerer=baseDao.getFieldDatasByCondition("sysspecialpower","ssp_caller","ssp_caller in("+callers+")");
		for (Object caller : callers_sysspecialpowerer) {
			sysspecialpower_.add(caller);
		}
		map_sysspecialpower.put("title", "涉及的特殊权限(SysSpecialPower)");
		map_sysspecialpower.put("type","sysspecialpower_");
		map_sysspecialpower.put("data", sysspecialpower_);
		result.add(map_sysspecialpower);
		//涉及的查询方案配置(SearchTemplate、SearchTemplateGrid、SearchTemplateProp)
		Map<String, Object> map_searchtemplate = new HashMap<String, Object>();
		Set<Object>  searchtemplate_=new HashSet<Object>();
		List<Object> callers_searchtemplate=baseDao.getFieldDatasByCondition("SearchTemplate","st_caller","st_caller in("+callers+")");
		for (Object caller : callers_searchtemplate) {
			searchtemplate_.add(caller);
		}
		map_searchtemplate.put("title", "涉及的查询方案配置(SearchTemplate、SearchTemplateGrid、SearchTemplateProp)");
		map_searchtemplate.put("type","searchtemplate_");
		map_searchtemplate.put("data", searchtemplate_);
		result.add(map_searchtemplate);
		//涉及的表、视图(DataCascade、DataRelation、DataLink)
		Map<String, Object> map_table = new HashMap<String, Object>();
		map_table.put("title", "涉及的表、视图(DataCascade、DataRelation、DataLink)");
		map_table.put("type","table_");
		map_table.put("data", table_);
		result.add(map_table);
		//涉及的其他对象(函数、过程、程序包)
		Map<String, Object> map_object = new HashMap<String, Object>();
		Set<Object>  object_=new HashSet<Object>();
		map_object.put("title", "涉及的其他对象(函数、过程、程序包)");
		map_object.put("type","object_");
		map_object.put("data", object_);
		result.add(map_object);
		
		return result;
	}
	@Override
	public void saveUpgradeScheme(String param) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(param);
		store.put("status_", "在录入");
		store.put("statuscode_", "ENTERING");
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "UPGRADE_SCHEME", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
	}
	
}
