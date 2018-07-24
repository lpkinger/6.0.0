package com.uas.mobile.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.api.serve.service.impl.ServeCommon;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DataListDao;
import com.uas.erp.dao.common.DbfindSetDao;
import com.uas.erp.dao.common.DbfindSetGridDao;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.model.DBFindSet;
import com.uas.erp.model.DBFindSetDetail;
import com.uas.erp.model.DBFindSetGrid;
import com.uas.erp.model.DataList;
import com.uas.erp.model.DataListDetail;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.model.EmpsJobs;
import com.uas.erp.model.GridFields;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.DbfindService;
import com.uas.mobile.dao.ListDao;
import com.uas.mobile.model.ListColumn;
import com.uas.mobile.model.ListConditions;
import com.uas.mobile.model.ListQuerySet;
import com.uas.mobile.model.ListView;
import com.uas.mobile.model.MobileQuery;
import com.uas.mobile.service.ListService;
import com.uas.mobile.service.PanelService;
import com.uas.pda.dao.PdaCommonDao;

@Service
public class ListServiceImpl extends ServeCommon implements ListService {
	@Autowired
	private ListDao listDao;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private DbfindService dbfindService;
	@Autowired
	private PanelService panelService;
	@Autowired
	private DataListDao dataListDao;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private PdaCommonDao pdaCommonDao;
	@Autowired
	private DbfindSetDao dbfindSetDao;
	@Autowired
	private DetailGridDao detailGridDao;
	@Autowired
	private DbfindSetGridDao dbfindSetGridDao;
	
	private String getJobs(Employee employee) {
		String condition = "";
		if (employee.getEm_defaulthsid()!=null) {
			condition =","+employee.getEm_defaulthsid();
		}
		
		for (EmpsJobs empsJob : employee.getEmpsJobs()) {
			condition += "," + empsJob.getJob_id();
		}
		condition = condition.substring(1); 
		if (!"".equals(condition)) {
			return condition;
		}
		
		return "";
	}
	
	@Override
	public ListView getListGridByCaller(String caller, String condition, int page, int pageSize, String orderby, Boolean _self, Integer _f,
			Employee employee, String currentMaster) {
		ListView ListView = new ListView();
		List<ListColumn> columns = new ArrayList<ListColumn>();
		List<GridFields> fields = new ArrayList<GridFields>();
		DataList dataList = listDao.getListView(caller, employee.getEm_master());
		List<DataListDetail> details = dataList.getDataListDetails();
		List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();
		Master master = employee.getCurrentMaster();
		// 多帐套，加帐套名称
		if (master != null && master.getMa_type() != 3 && master.getMa_soncode() != null) {
			fields.add(new GridFields("CURRENTMASTER"));
			columns.add(new ListColumn("CURRENTMASTER", "帐套", 80));
		}
		for (DataListDetail detail : details) {
			fields.add(new GridFields(detail));
			columns.add(new ListColumn(detail, "zh_CN"));
		}
		if (_self != null && _self) {
			String f = dataList.getDl_entryfield();
			if (f != null && f.trim().length() > 0) {
				Object emVal = employee.getEm_id();
				if (f.endsWith("@C")) {
					f = f.substring(0, f.lastIndexOf("@C"));
					emVal = employee.getEm_code();
				} else if (f.endsWith("@N")) {
					f = f.substring(0, f.lastIndexOf("@N"));
					emVal = employee.getEm_name();
				}
				if (condition != null && condition.trim().length() > 0) {
					condition += " AND ";
				}
				condition += f + "='" + emVal + "'";
			} else {
				BaseUtil.showErrorOnSuccess("无法限制列表权限!原因: 未配置录入人字段.");
			}
		}
		// 人事资料关联控制
		try {
			String conditionstr = listDao.getRelativesettings(caller, "datalist", employee.getEm_id());
			if ((condition == null || "".equals(condition)) && conditionstr != null)
				condition = conditionstr;
			condition += conditionstr != null ? " AND " + conditionstr : "";
		} catch (Exception e) {
		}
		if (!"1=2".equals(condition)) {
			listData = dataListDao.getDataListData(dataList, condition, employee, page, pageSize, _f, false, null,false);
		}
		ListView.setListdata(listData);
		ListView.setColumns(columns);
		ListView.setKeyField(dataList.getDl_keyfield());
		ListView.setPfField(dataList.getDl_pffield());
		return ListView;
	}

	@Override
	public List<ListConditions> getAllConditionsByCaller(String caller) {
		// TODO Auto-generated method stub
		List<ListQuerySet> lists = listDao.getListViewQuerySet(caller);
		List<ListConditions> conditions = new ArrayList<ListConditions>();
		for (ListQuerySet set : lists) {
			if (set.getLs_fixedvalue() == 0) {
				List<Map<String, Object>> maps = baseDao.getJdbcTemplate().queryForList(set.getQuerySql());
				if (set.getLs_type().equals("EM"))
					set.setDataStr(set.FormatEmData(maps));
				else
					set.setDataStr(maps);

			} else if (set.getLs_datasource() != null) {
				set.setDataStr(set.FormatFixedData(set.getLs_datasource()));
			}
			conditions.add(new ListConditions(set));
		}
		return conditions;
	}

	@Override
	public List<MobileQuery> getMobileQuerys(Employee employee) {
		// TODO Auto-generated method stub
		List<MobileQuery> querys = new ArrayList<MobileQuery>();
		try {
			querys = baseDao.getJdbcTemplate()
					.query("select * from mobilequery", new BeanPropertyRowMapper<MobileQuery>(MobileQuery.class));
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return querys;
	}

	@Override
	public List<Object> getCombByCaller(String caller, String field, Employee employee) {
		// TODO Auto-generated method stub
		List<Object> objs = baseDao.getFieldDatasByCondition("datalistcombo", "dlc_value", " upper(dlc_caller)='" + caller.toUpperCase()
				+ "' AND upper(dlc_fieldname)='" + field.toUpperCase() + "' ");

		return objs;
	}

	@Override
	public List<Map<String, Object>> getCombValueByCaller(String caller,String field) {
		String sql = "select dlc_value display,dlc_display value from datalistcombo where dlc_caller='" + caller + "' and upper(dlc_fieldname)='" + field.toUpperCase()
				+ "' order by dlc_detno asc";
		return baseDao.getJdbcTemplate().queryForList(sql);
	}
	
	@Override
	public List<Map<String, Object>> getAuditDetail(String caller) {
		SqlRowList rs = baseDao.queryForRowSet("select * from mobileAuditDetail where mad_mancaller='" + caller + "'");
		if (rs.next()) {// 多个从表
			return pdaCommonDao.changeKeyToLowerCase(rs.getResultList());
		} else {// 单个从表
			rs = baseDao
					.queryForRowSet("select fo_caller mad_caller,fo_caller mad_mancaller,fo_detailmainkeyfield mad_code, fo_detaildetnofield mad_detnocode, '单据明细' mad_name from form  where fo_caller='"
							+ caller + "' and fo_detailmainkeyfield is not null");
			if (rs.next()) {
				return pdaCommonDao.changeKeyToLowerCase(rs.getResultList());
			}
		}
		return null;
	}

	@Override
	public List<Map<Object, Object>> getDbfindGridByField(String caller,String field, String condition, int page, int pageSize) {
		try {
			String datas=dbfindService.getDbfindGridByField(caller, field, condition, page, pageSize,false).getDataString();
			return BaseUtil.parseGridStoreToMaps(datas);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Map<String,Object> getFormAndGridData(String caller,String id,String isprocess,String config,HttpSession session){
		boolean process = false;
		if(isprocess!=null&&"1".equals(isprocess)){
			process = true;
		}
		if(process){
			caller = getTableCaller(caller);
		}
		Map<String,Object> map = new HashMap<String,Object>();
		Object[] form = baseDao.getFieldsDataByCondition("form", new String[]{"fo_table","fo_keyfield","fo_detailkeyfield","fo_detailtable","fo_detailmainkeyfield","fo_id","fo_detaildetnofield","fo_detailgridorderby"}, "fo_caller='" + caller + "'");
		List<Object []> formFields = null;
		List<Object> gridFields = null;
		if(process){
			formFields = baseDao.getFieldsDatasByCondition("formdetail", new String []{"fd_field","fd_type","fd_logictype"},"fd_foid='" + form[5] + "' and nvl(fd_mobileused,1)<>0");
			gridFields = baseDao.getFieldDatasByCondition("detailgrid", "dg_field", "dg_caller='" + caller + "' and nvl(dg_mobileused,1)<>0");		
		}else{
			formFields = baseDao.getFieldsDatasByCondition("mobileformdetail", new String []{"mfd_field"}, "mfd_caller='" + caller + "'");
			gridFields = baseDao.getFieldDatasByCondition("mobiledetailgrid", "mdg_field", "mdg_caller='" + caller + "'");
		}
		
		String comboCaller = caller;
		List<Map<String,Object>> datas = null;
		if(comboCaller.indexOf("$Change")>-1){
			comboCaller = comboCaller.substring(0,comboCaller.indexOf("$Change"));
		}
		
		Map<String,Object> combos = getComboByCaller(comboCaller);
		datas=getFormData(formFields,form[0].toString(),form[1] + "=" + id,combos);	
		if(caller.indexOf("$Change")>-1) { //公共变更单，需要把变更后的数据返回，变更前的数据替换
			setCommonChangeData(datas,formFields,caller,id);
		} 	
		map.put("formdata", datas);		
		
		String detailCondition = form[4] + "=" + id;
		
		if(form[6]!=null){
			detailCondition += " order by " + form[6] + " asc";
		}else if(form[7]!=null&&!"".equals(form[7])){
			detailCondition += form[7];
		}
		
		datas = new ArrayList<Map<String,Object>>();
		if(form[3]!=null){
			datas = getGridData(gridFields,form[3].toString(),detailCondition,combos);
		}
		map.put("griddata",datas);
		
		List<Map<String,Object>> formMtFields = baseDao.queryForList("select FD_FIELD,FD_LOGICTYPE from form left join formdetail on fd_foid=fo_id where fo_caller='"+caller+"' and fd_type='MT'");
		
		Map<String,Object> configs = panelService.getFormAndGridDetail(caller, "1=1",isprocess,config,session);
		
		List<Map<String,Object>> formConfigUpper = getUpperFormConfig(configs,formMtFields);
		List<Map<String,Object>> gridConfigUpper = getUpperGridConfig(configs);
		
		List<Map<String,Object>> otherGrids = getOtherGrid(caller,id,process,config,session);
		
		map.put("formconfigs",formConfigUpper);
		map.put("gridconfigs",gridConfigUpper);
		map.put("othergrids", otherGrids);
		return map;
	}
	
	private void setCommonChangeData(List<Map<String,Object>> datas,List<Object []> formFields,String caller,String id){
		Object newDatas = baseDao.getFieldDataByCondition("COMMONCHANGELOG", "CL_DATA", "cl_caller='"+caller+"' and cl_id='"+id+"'");
		if(newDatas!=null){
			Map<Object, Object> log = BaseUtil.parseFormStoreToMap(newDatas.toString());
			Map<Object,Object> newData=new HashMap<Object,Object>();
			Map<String,Object> data=new HashMap<String,Object>();
			Set<Object> keys = log.keySet();
			for(Object key:keys){
				for(Object[] field:formFields){
					Object value = log.get(key);
					if (field.length>1) {
						Object fieldtype = field[1];
						String logicType = String.valueOf(field[2]);
						if (StringUtil.hasText(logicType)) {
							value = parseValue(value,fieldtype,logicType);
						}	
					}
					if((key).equals(field[0]+"-new")){
						newData.put(field[0],value);
					}else if((key).equals(field[0])){
						if(keys.contains(field[0]+"-new")){ //排除commonchangelog本身的字段
 							datas.get(0).put(field[0].toString(),value);
						}
					}
				}
			}
			data.put("change-new", newData);
			datas.add(data);
		}		
	}

	private String getTableCaller(String incaller){
		String flowCaller = incaller;
		String caller = incaller;
		List<Object> tableCallers = baseDao.getFieldDatasByCondition("form", "fo_caller", "fo_flowcaller='"+caller+"' and nvl(fo_isautoflow,0)=-1");
		for(Object tableCaller:tableCallers){
			if(tableCaller!=null&&!"".equals(tableCaller)){
				if(flowCaller.equals(tableCaller.toString())){
					caller = flowCaller;
					break;
				}else{
					caller = tableCaller.toString();
				}
			}				
		}
		return caller;
	}
	
	private List<Map<String,Object>> getOtherGrid(String caller,String id,boolean isprocess,String config,HttpSession session){
		List<Map<String,Object>> otherGrids = new ArrayList<Map<String,Object>>();
		SqlRowList rs = baseDao.queryForRowSet("select * from mobileauditdetail where mad_mancaller='"+caller+"' order by mad_id asc");
		while(rs.next()){
			String gridCaller = rs.getString("mad_caller");
			String table = baseDao.getFieldValue("detailgrid", "max(dg_table)", "dg_caller='"+gridCaller+"'", String.class);
			String detno = rs.getString("mad_detnocode");
			String condition = rs.getString("mad_code") + "='" + id + "'";
			if(detno!=null){
				condition += " order by " + detno + " asc";
			}
			List<Object> gridFields = null;
			if(isprocess){
				gridFields =baseDao.getFieldDatasByCondition("detailgrid", "dg_field",!"1".equals(config)? "dg_caller='" + gridCaller + "' and nvl(dg_mobileused,1)<>0":"dg_caller='" + gridCaller + "'");
			}else{
				gridFields = baseDao.getFieldDatasByCondition("mobiledetailgrid", "mdg_field", "mdg_caller='" + gridCaller + "'");
			}
			Map<String,Object> combos = getComboByCaller(gridCaller);
			List<Map<String,Object>> gridData = getGridData(gridFields,table,condition,combos);
			Map<String,Object> configs = panelService.getFormAndGridDetail(gridCaller, "1=1",isprocess?"1":null,config,session);
			List<Map<String,Object>> gridConfigUpper = getUpperGridConfig(configs);

			Map<String,Object> grid = new HashMap<String,Object>();
			grid.put("griddata", gridData);
			grid.put("gridconfigs", gridConfigUpper);
			grid.put("caller", gridCaller);
			grid.put("name", rs.getString("mad_name"));
			otherGrids.add(grid);
		}
		return otherGrids;
	}
	
	@SuppressWarnings("unchecked")
	private List<Map<String,Object>> getGridData(List<Object> gridFields,String table,String condition,Map<String,Object> combos){
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		if(gridFields.size()>0){
			String[] gridFieldArr = new String[gridFields.size()];
			int i=0;
			for(Object obj:gridFields){
				gridFieldArr[i] = obj.toString();
				i++;
			}
			List<Object[]> gridData = getFieldsDatasByCondition(table, gridFieldArr, condition);
			for(Object[] obj:gridData){
				Map<String,Object> modelMap = new HashMap<String,Object>();
				for(int j=0;j<obj.length;j++){
					String field = gridFieldArr[j];
					Object value = obj[j];
					if(combos.get(field)!=null){
						Map<String,Object> data = (Map<String,Object>)combos.get(field);
						if(value!=null){
							if(data.get(value)!=null){
								value = data.get(value);
							}
						}
					}
					modelMap.put(field,value);
				}				
				datas.add(modelMap);
			}
		}
		return datas;
	}
	
	private List<Object[]> getFieldsDatasByCondition(String tableName, String[] fields, String condition) {
		StringBuffer sql = new StringBuffer("SELECT ");
		sql.append(BaseUtil.parseArray2Str(fields, ","));
		sql.append(" FROM ");
		sql.append(tableName);
		sql.append(" WHERE ");
		sql.append(condition);
		List<Map<String, Object>> list = baseDao.getJdbcTemplate().queryForList(sql.toString());
		Iterator<Map<String, Object>> iter = list.iterator();
		List<Object[]> datas = new ArrayList<Object[]>();
		Object value = null;
		Map<String, Object> m = null;
		Object[] results = null;
		int length = fields.length;
		while (iter.hasNext()) {
			results = new Object[length];
			m = iter.next();
			for (int i = 0; i < length; i++) {
				if(fields[i].trim().contains(" ")){
					String strs[] = fields[i].split(" ");
					value = m.get(strs[strs.length-1].toUpperCase());
				}else{					
					value = m.get(fields[i].toUpperCase());
				}
				if (value != null && value.getClass().getSimpleName().toUpperCase().equals("TIMESTAMP")) {
					Timestamp time = (Timestamp) value;
					try {
						value = DateUtil.parseDateToString(new Date(time.getTime()), Constant.YMD_HMS);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				results[i] = value;
			}
			datas.add(results);
		}
		return datas;
	}
	
	@SuppressWarnings("unchecked")
	private List<Map<String,Object>> getFormData(List<Object []> formFields,String table,String condition,Map<String,Object> combos){
		List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();
		if(formFields.size()>0){
			String[] formFieldArr = new String[formFields.size()];
			int i=0;
			for(Object[] obj:formFields){
				formFieldArr[i] = obj[0].toString();
				i++;
			}
			List<Object[]> formData = getFieldsDatasByCondition(table, formFieldArr, condition);
			for(Object[] obj:formData){
				Map<String,Object> modelMap = new HashMap<String,Object>();
				for(int j=0;j<obj.length;j++){
					String field = formFieldArr[j];				
					Object value = obj[j];
					if(combos.get(field)!=null){
						Map<String,Object> data = (Map<String,Object>)combos.get(field);
						if(data.get(value)!=null){
							value = data.get(value);
						}
					}
					if (formFields.get(j).length>1) {
						Object fieldtype = formFields.get(j)[1];
						String logicType = String.valueOf(formFields.get(j)[2]);
						if (StringUtil.hasText(logicType)) {
							value = parseValue(value,fieldtype,logicType);
						}	
					}
					
					modelMap.put(field,value);
				}				
				datas.add(modelMap);
			}
		}
		return datas;
	}
	
	
	private Map<String,Object> getComboByCaller(String caller){
		Map<String,Object> combos = new HashMap<String,Object>();
		Map<String,Object> combo = null;
		String fieldName = "";
		SqlRowList rs = baseDao.queryForRowSet("select * from datalistcombo where dlc_caller='"+caller+"' order by dlc_fieldname,dlc_detno asc");
		while(rs.next()){
			if(!fieldName.equals(rs.getString("dlc_fieldname"))){
				fieldName = rs.getString("dlc_fieldname");
				combo = new HashMap<String,Object>();
				combos.put(fieldName, combo);
			}
			combo.put(rs.getString("dlc_display"), rs.getString("dlc_value"));
		}
		return combos;
	}
	
	@SuppressWarnings({"unchecked" })
	private List<Map<String,Object>> getUpperFormConfig(Map<String,Object> configs,List<Map<String,Object>> formMtFields){
		List<Map<String,Object>> formConfig = (List<Map<String,Object>>)configs.get("formdetail");
		List<Map<String,Object>> formConfigUpper = new ArrayList<Map<String,Object>>();
		for(int i=0;i<formConfig.size();i++){
			Map<String,Object> formMap = formConfig.get(i);
			Map<String,Object> upperCaseMap = new HashMap<String,Object>();
			Set<String> set = formMap.keySet();
			for(Map<String,Object> mtMap:formMtFields){
				if(formMap.get("fd_field").toString().toLowerCase().equals(mtMap.get("FD_FIELD").toString().toLowerCase())){
					formMap.put("fd_type", "MT");
				}
			}
			for(String key:set){
				upperCaseMap.put(key.toUpperCase(), formMap.get(key));
			}
			formConfigUpper.add(upperCaseMap);
		}
		return formConfigUpper;
	}
	
	@SuppressWarnings({ "unchecked"})
	private List<Map<String,Object>> getUpperGridConfig(Map<String,Object> configs){
		List<Map<String,Object>> gridConfig = (List<Map<String,Object>>)configs.get("gridetail");
		List<Map<String,Object>> gridConfigUpper = new ArrayList<Map<String,Object>>();
		for(int i=0;i<gridConfig.size();i++){
			Map<String,Object> gridMap = gridConfig.get(i);
			Map<String,Object> upperCaseMap = new HashMap<String,Object>();
			Set<String> set = gridMap.keySet();
			for(String key:set){
				upperCaseMap.put(key.toUpperCase(), gridMap.get(key));
			}
			gridConfigUpper.add(upperCaseMap);
		}
		return gridConfigUpper;
	}
	
	private Object parseValue(Object value, Object fieldType, String logicType){
		if (fieldType!=null&&"CBG".equals(fieldType.toString())) {
			String [] logics = logicType.split(";");
			String values = "";
			String [] vals = String.valueOf(value).split(";");
			for(int i=0; i<logics.length&&i<vals.length; i++){
				if("1".equals(vals[i])){
					values += logics[i]+";";
				}
			}
			if(values.length()>0){
				value = values.substring(0,values.length()-1);
			}
		}
		return value;
	}

	@Override
	public List<DBFindSetGrid> getGridDbfinds(String gridCaller,
			String gridField) {
		DBFindSet dbFindSet = null;
		List<DBFindSetGrid> lists = new ArrayList<DBFindSetGrid>();
		if (gridField != null && !gridField.equals("")) {
			String DbCaller = null;
			List<DetailGrid> details = detailGridDao.getDetailGridsByCaller(gridCaller, SpObserver.getSp());
			for (int i = 0; i < details.size(); i++) {
				if (details.get(i).getDg_field().equals(gridField)) {
					String functionname = details.get(i).getDg_findfunctionname();
					if (StringUtil.hasText(functionname)) {
						DbCaller = functionname.split("[|]")[0];
						dbFindSet = dbfindSetDao.getDbfind(DbCaller, SpObserver.getSp());
					}
					break;
				}
			}
			if (dbFindSet != null) {
				List<DBFindSetDetail> SetDetails = dbFindSet.getDbFindSetDetails();
				List<DBFindSetGrid> dbfindsetgrids = dbfindSetGridDao.getDbFindSetGridsByCaller(gridCaller);
				// 只取匹配的字段显示 否则不显示
				for (int j = 0; j < dbfindsetgrids.size(); j++) {
					String ds_dbfindfield = dbfindsetgrids.get(j).getDs_dbfindfield();
					for (DBFindSetDetail dbdetail : SetDetails) {
						for (int k = 0; k < (ds_dbfindfield == null ? "" : ds_dbfindfield).split(";").length; k++) {
							if (ds_dbfindfield.split(";")[k].equals(dbdetail.getDd_fieldname())
									|| dbdetail.getDd_fieldname().contains(" " + ds_dbfindfield.split(";")[k])) {
								lists.add(dbfindsetgrids.get(j));
								break;
							}
						}
					}
				}
			}
		} else {
			dbFindSet = dbfindSetDao.getDbfind(gridCaller, SpObserver.getSp());
			if (dbFindSet != null) {
				List<DBFindSetDetail> SetDetails = dbFindSet.getDbFindSetDetails();
				List<DBFindSetGrid> dbfindsetgrids = dbfindSetGridDao.getDbFindSetGridsByCaller(gridCaller);
				// 只取匹配的字段显示 否则不显示
				for (int j = 0; j < dbfindsetgrids.size(); j++) {
					String ds_dbfindfield = dbfindsetgrids.get(j).getDs_dbfindfield();
					for (DBFindSetDetail dbdetail : SetDetails) {
						// 不能用contains
						for (int k = 0; k < (ds_dbfindfield == null ? "" : ds_dbfindfield).split(";").length; k++) {
							if (ds_dbfindfield.split(";")[k].equals(dbdetail.getDd_fieldname())) {
								lists.add(dbfindsetgrids.get(j));
								break;
							}
						}
					}
				}
			}

		}
		return lists;
	}

	@Override
	public List<Map<String, Object>> getServices(String basePath, Employee employee, String kind, String type, boolean noControl) {
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		try {
			String powerCondition = "";
			if(!noControl){
				String jobs = getJobs(employee);
				powerCondition = " and (sv_caller in (select distinct pp_caller from positionpower where nvl(pp_add,0)+nvl(pp_see,0)>0 and pp_joid in(" + jobs + "))  or sv_caller in (select distinct pp_caller from personalpower where nvl(pp_add,0)+nvl(pp_see,0)>0 and pp_emid=" + employee.getEm_id()+"))";
			}
			
			SqlRowList rs = baseDao.queryForRowSet("SELECT ST_ID,ST_NAME,ST_DETNO,ST_TAG FROM SERVICETYPE WHERE ST_KIND = ? ORDER BY ST_DETNO",kind);
			while(rs.next()){
				Map<String, Object> serveType = new HashMap<String, Object>();
				serveType.put("st_id", rs.getString("st_id"));
				serveType.put("st_name", rs.getString("st_name"));
				serveType.put("st_tag", rs.getString("st_tag"));
				SqlRowList rs1 = null;
				if (type!=null) {
					rs1 = baseDao.queryForRowSet("select * from SERVICE where NVL(SV_ENABLE,0)<>0 and SV_STID = ? "+("app".equals(kind)?powerCondition:"")+" and nvl(sv_type,'common') = ? order by SV_DETNO", rs.getGeneralInt("st_id"),type);
				}else{
					rs1 = baseDao.queryForRowSet("select * from SERVICE where NVL(SV_ENABLE,0)<>0 and SV_STID = ? "+("app".equals(kind)?powerCondition:"")+" order by SV_DETNO", rs.getGeneralInt("st_id"));
				}
				List<Map<String, Object>> serves = new ArrayList<Map<String,Object>>();
				while(rs1.next()){
					Map<String, Object> serve = new HashMap<String, Object>();
					serve.put("sv_id", rs1.getString("sv_id"));
					serve.put("sv_name", rs1.getString("sv_name"));
					serve.put("sv_tag", rs1.getString("sv_tag"));
					serve.put("sv_caller", rs1.getString("sv_caller"));
					
					Map<String, Object> logourl = new HashMap<String, Object>();
					logourl.put("mobile", getLogoUrl(basePath, rs1.getString("sv_moblogo")));
					serve.put("sv_logourl", logourl);
					serves.add(serve);
				}
				serveType.put("serves", serves);
				if (serves.size()>0) {
					result.add(serveType);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误："+e.getMessage());
		}
		return result;
	}
}
