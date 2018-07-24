package com.uas.erp.service.bench.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.JSONUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.BenchDao;
import com.uas.erp.dao.common.DataListComboDao;
import com.uas.erp.dao.common.PowerDao;
import com.uas.erp.model.Bench;
import com.uas.erp.model.Bench.BenchBusiness;
import com.uas.erp.model.Bench.BenchButton;
import com.uas.erp.model.Bench.BenchScene;
import com.uas.erp.model.Bench.BenchSceneGrid;
import com.uas.erp.model.Bench.SceneButton;
import com.uas.erp.model.DataListCombo;
import com.uas.erp.model.Employee;
import com.uas.erp.model.EmpsJobs;
import com.uas.erp.model.GridColumns;
import com.uas.erp.model.GridFields;
import com.uas.erp.model.Master;
import com.uas.erp.model.PersonalPower;
import com.uas.erp.model.PositionPower;
import com.uas.erp.service.bench.BenchService;
import com.uas.erp.service.common.DataListService;

@Service("benchService")
public class BenchServiceImpl implements BenchService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private DataListComboDao dataListComboDao;
	
	@Autowired
	private DataListService dataListService;
	
	@Autowired
	private BenchDao benchDao;
	
	@Autowired
	private PowerDao powerDao;
	
	@Override
	public Bench getBench(Employee employee, String bccode, boolean isCloud, boolean noControl, String Condition) {
		Bench bench = null;
		try {
			String sob = SpObserver.getSp();
			if (isCloud) {
				SpObserver.putSp(Constant.UAS_CLOUD);
				bench = getBench(bccode, employee, Condition, noControl);
				SpObserver.putSp(sob);
			} else {
				bench = getBench(bccode, employee, Condition, noControl);
			}
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError(e.getMessage());
		}
		return bench;
	}

	@Override
	public Map<String, Object> getBenchSceneConfig(Employee employee, String bscode, String condition, Integer page, Integer pageSize, boolean isCloud, boolean noControl) {
		BenchScene benchScene = null;
		String sob = SpObserver.getSp();
		try {
			if (isCloud) {
				SpObserver.putSp(Constant.UAS_CLOUD);
				benchScene = benchDao.getBenchScene(bscode,sob);
				if (benchScene==null) {
					throw new Exception("此工作台场景未配置！");
				}
				if (noControl) {
					benchScene.setSceneButtons(benchDao.getSceneButtons(bscode, sob));
				}else {
					List<SceneButton> sceneButtons = benchDao.getSceneButtonsByPower(bscode, employee);
					benchScene.setSceneButtons(sceneButtons);
				}
				SpObserver.putSp(sob);
	
			} else {
				benchScene = benchDao.getBenchScene(bscode,sob);
				if (benchScene==null) {
					throw new Exception("此工作台场景未配置！");
				}
				if (noControl) {
					benchScene.setSceneButtons(benchDao.getSceneButtons(bscode, sob));
				}else {
					List<SceneButton> sceneButtons = benchDao.getSceneButtonsByPower(bscode, employee);
					benchScene.setSceneButtons(sceneButtons);
				}
			}
			Map<String, Object> map = new HashMap<String, Object>();
			if (benchScene.getBs_islist()==0) {
				Object[] datalist_config = baseDao.getFieldsDataByCondition("datalistconfig left join datalistconfig$emp on id_=id$emp", "name_,filterjson_,id_", "caller_='"+bscode+"' and emcode$emp='"+employee.getEm_code()+"'");
				if(datalist_config==null){
					datalist_config = baseDao.getFieldsDataByCondition("datalistconfig", "name_,filterjson_,id_", "caller_='"+bscode+"' and isNorm_=-1 and isdefault_=-1");
				}
				map = getSceneGrid(benchScene,employee,datalist_config);
				for (SceneButton sceneButton : benchScene.getSceneButtons()) {
					String sbcon = sceneButton.getSb_condition();
					String con = benchDao.getRelativesettings(sceneButton.getSb_relativecaller(), "batchdeal", employee.getEm_id());
					if (con!=null) {
						if (StringUtil.hasText(sbcon)) {
							sbcon += " and "+con;
						}else {
							sbcon = con;
						}
					}
					sceneButton.setSb_condition(sbcon);
				}
				
				map.put("keyField", benchScene.getBs_keyfield());
				map.put("batchSet", benchScene.getBs_batchset());
				
				if(benchScene.getBenchSceneGrids()!=null&&benchScene.getBenchSceneGrids().size()>0){
					/** 添加其他约束条件 */
					condition = appendCondition(benchScene, condition, employee);
					/**添加权限条件，看自己/看所有 */
					Boolean _jobemployee = false;
					if (!noControl) {
						_jobemployee = isJobEmployee(benchScene.getBs_caller(), employee);
						condition = appendPowerCondition(benchScene, employee, condition, null, _jobemployee, false);
					}
					condition = getCondition(benchScene, employee, condition);
					if(datalist_config!=null){
						List<Map<Object, Object>> filterlist = JSONUtil.toMapList(datalist_config[1].toString());
						if (condition != null && condition.trim().length() > 0) {
							condition += " AND ";
						}
						String defaultfiltercondition = appendFilterCondition(filterlist);
						condition += defaultfiltercondition;
						map.put("defaultFilterCondition",defaultfiltercondition);
		
					}
					
					map.put("data", benchDao.getSceneGridData(benchScene, condition, employee, page, pageSize, null, _jobemployee));
					map.put("summarydata", benchDao.getSummaryData(benchScene, condition, _jobemployee));
					String sql = benchScene.getSearchSql(condition, employee);
					if(_jobemployee){
						sql = benchDao.getSqlWithJobEmployee(employee) + sql;			
					}
					map.put("count", baseDao.getCount(sql));
					
				}else {
					throw new Exception("此场景的列表未配置！");
				}
			}
			map.put("scenebuttons", benchScene.getSceneButtons());
			return map;
		} catch (Exception e) {
			BaseUtil.showError(e.getMessage());
		}
		return null;
	}
	
	@Override
	public Map<String, Object> getBenchSceneGridData(Employee employee, String bscode, String condition, Integer page, Integer pageSize, String orderby, boolean isCloud, boolean noControl,boolean fromHeader) {
		BenchScene benchScene = null;
		String sob = SpObserver.getSp();
		try{
			if (isCloud) {
				SpObserver.putSp(Constant.UAS_CLOUD);
				benchScene = benchDao.getBenchScene(bscode,sob);
				SpObserver.putSp(sob);
	
			} else {
				benchScene = benchDao.getBenchScene(bscode,sob);
			}
			if (benchScene==null) {
				throw new Exception("此工作台场景未配置！");
			}
			Map<String, Object> map = new HashMap<String, Object>();
			if(benchScene.getBenchSceneGrids()!=null&&benchScene.getBenchSceneGrids().size()>0){
				/** 添加其他约束条件 */
				condition = appendCondition(benchScene, condition, employee);
				/**添加权限条件，看自己/看所有 */
				Boolean _jobemployee = false;
				if (!noControl) {
					_jobemployee = isJobEmployee(benchScene.getBs_caller(), employee);
					condition = appendPowerCondition(benchScene, employee, condition, null, _jobemployee, false);
				}
				condition = getCondition(benchScene, employee, condition);
				map.put("data", benchDao.getSceneGridData(benchScene, condition, employee, page, pageSize, orderby, _jobemployee));
				map.put("summarydata", benchDao.getSummaryData(benchScene, condition, _jobemployee));
				String sql = benchScene.getSearchSql(condition, employee);
				if(_jobemployee){
					sql = benchDao.getSqlWithJobEmployee(employee) + sql;			
				}
				map.put("count", baseDao.getCount(sql));
			}else {
				throw new Exception("此场景的列表未配置！");
			}
			return map;
		} catch (Exception e) {
			BaseUtil.showError(e.getMessage());
		}
		return null;
	}
	
	private Map<String, Object> getSceneGrid(BenchScene benchScene,Employee employee,Object[] datalist_config){
		Map<String, Object> sceneGrid = new HashMap<String, Object>();
		List<GridColumns> columns = new ArrayList<GridColumns>();
		GridColumns column = null;
		List<GridFields> fields = new ArrayList<GridFields>();
		GridFields field = null;
		List<DataListCombo> combos = dataListComboDao.getComboxsByCaller(benchScene.getBs_code(),employee.getEm_master());
		String language = SystemSession.getLang();
		Master master = employee.getCurrentMaster();
		// 多帐套，加帐套名称
		if (master != null && master.getMa_type() != 3 && master.getMa_soncode() != null) {
			fields.add(new GridFields("CURRENTMASTER"));
			columns.add(new GridColumns("CURRENTMASTER", "帐套", 80));
		}
		for(BenchSceneGrid bsGrid:benchScene.getBenchSceneGrids()){
			field = new GridFields(bsGrid.getSg_field(), bsGrid.getSg_type());
			fields.add(field);
			if(datalist_config!=null&&datalist_config[1]!=null){
				String name_ = datalist_config[0].toString();
				List<Map<Object, Object>> filterlist = JSONUtil.toMapList(datalist_config[1].toString());
				int id_ = datalist_config[2]==null?0:Integer.parseInt(datalist_config[2].toString());
				column = new GridColumns(bsGrid, combos, language,name_,filterlist,id_);
			}else{
				column = new GridColumns(bsGrid,combos,language);
			}
			
			columns.add(column);
		}
		
		sceneGrid.put("fields", fields);
		sceneGrid.put("columns", columns);
		
		return sceneGrid;
	}
	
	
	public String appendPowerCondition(BenchScene benchScene, Employee employee, String condition,Boolean self, Boolean _jobemployee, Boolean isCount){
		boolean _self = false;
		if (self!=null&&self) {
			_self = true;
		}else if (self==null) {
			String caller = benchScene.getBs_caller();
			if(StringUtil.hasText(caller)){
				boolean bool = checkJobPower(caller, PositionPower.ALL_LIST, employee)||powerDao.getSelfPowerByType(caller, PersonalPower.ALL_LIST, employee);// 岗位权限表--all;|| 个人权限表--all;
				_self = !bool;
			}/*else{
				_self = true;
			}*/
		}
		
		if (_jobemployee || _self) {// 只查看自己录入的数据
			String con = "";
			String f = benchScene.getBs_selffield();
			if (f != null && f.trim().length() > 0) {
				con = powerDao.getRecorderCondition(condition, f, employee, _jobemployee);
			} else {
				if(isCount){
					con = "1=2";
				}else{
					BaseUtil.showErrorOnSuccess("无法限制权限!原因: 未配置自我字段.");
				}
			}
			if (StringUtil.hasText(condition)) {
				condition += " AND ("+con+")";
			}else {
				condition = con;
			}
		}
		return condition;
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
	
	//是否可以查看岗位下属数据
	@Override
	public boolean isJobEmployee(String caller, Employee employee){
		// 先看是否有查看所有的权限
		boolean bool = checkJobPower(caller, PositionPower.ALL_LIST, employee);
		if(!bool){
			boolean checkDefaultHrJobPowerExists = powerDao.checkDefaultHrJobPowerExists();
			if(checkDefaultHrJobPowerExists){
				return true;
			}else{
				if(!StringUtil.hasText(caller)){
					return false;
				}
				return checkJobEmployeePower(caller, PersonalPower.JOBEMPLOYEE_LIST, employee);
			}
		}
		return false;
	}
	
	//检查是否有"浏览岗位下属"权限
	private boolean checkJobEmployeePower(String caller, String powerType, Employee employee) {
		String sob = employee.getEm_master();
		Integer jobId = employee.getEm_defaulthsid();
		boolean jobEmployeeExists = powerDao.checkJobEmployeeExists(jobId);
		boolean bool = false;
		if(!jobEmployeeExists){
			for (EmpsJobs empsJob : employee.getEmpsJobs()) {
				jobEmployeeExists = powerDao.checkJobEmployeeExists(empsJob.getJob_id());
				if (jobEmployeeExists)
					break;
			}			
		}
		if(!jobEmployeeExists){
			return false;
		}else{
			// 默认岗位设置
			bool = powerDao.getJobEmployeePowerByType(caller,powerType,sob,employee.getEm_defaulthsid());
			if (!bool && employee.getEmpsJobs() != null) {
				// 按员工岗位关系取查找权限
				for (EmpsJobs empsJob : employee.getEmpsJobs()) {
					bool = powerDao.getJobEmployeePowerByType(caller,powerType,sob,empsJob.getJob_id());
					if (bool)
						break;
				}
			}			
			if(!bool){
				//检查个人权限
				bool = powerDao.getJobEmployeePowerByType(caller, PersonalPower.JOBEMPLOYEE_LIST, employee.getEm_master(), employee);
			}			
		}
		return bool;
	}
	
	@Override
	public String appendCondition(BenchScene benchScene, String condition, Employee employee) {
		// 人事资料关联控制
		String conditionstr = benchDao.getRelativesettings(benchScene.getBs_code(), "scenegrid", employee.getEm_id());
		if ((condition == null || "".equals(condition)) && conditionstr != null)
			condition = conditionstr;
		else
			condition += conditionstr != null ? " AND " + conditionstr : "";
	
		// 设置的约束关系
		String limitcondition = baseDao.getLimitCondition(benchScene.getBs_table(), employee.getEm_id());
		if ((condition == null || "".equals(condition)) && !"".equals(limitcondition))
			condition = limitcondition;
		else
			condition += !"".equals(limitcondition) ? (" AND " + limitcondition) : "";
		return condition;
	}
	
	public String getCondition(BenchScene benchScene,Employee employee,String condition){
		String con = benchScene.getBs_condition();
		if (con!=null) {
			con = con.replaceAll("@EMID", employee.getEm_id().toString());
			con = con.replaceAll("@EMCODE", "'"+employee.getEm_code().toString()+"'");
			con = con.replaceAll("@EMNAME", "'"+employee.getEm_name().toString()+"'");
		}
		condition = (con == null || "".equals(con)) ? condition : ("(" + con + ")" + ((condition == null || "".equals(condition)) ? "" : " AND (" + condition + ")"));
		return condition;
	}
	
	public JSONObject getFlowchartConfig(String bccode) {
		JSONObject result = new JSONObject();
		result.put("data", benchDao.getFlowchartConfig(bccode));
		return result;
	}
	
	private Bench getBench(String bccode, Employee employee, String Condition, boolean noControl) throws Exception{
		Bench bench = null;
		String sob = employee.getEm_master();
		bench = benchDao.getBench(bccode, sob);
		if (bench==null) {
			throw new Exception("此工作台未配置！");
		}else {
			Map<String, List<BenchButton>> benchButtons = null;
			
			List<BenchBusiness> benchBusinesses = null;
			List<BenchBusiness> hideBusinesses = new ArrayList<Bench.BenchBusiness>();
			
			if (noControl) {
				benchButtons = benchDao.getBenchButtons(bccode, sob);
				benchBusinesses = benchDao.getSelfBenchBusinesses(bccode, employee);
				if (benchBusinesses.size()==0) {
					benchBusinesses = benchDao.getBenchBusinesses(bccode, employee);
				}else{
					hideBusinesses = benchDao.getHideBusinesses(bccode, employee);
				}
			}else {
				benchButtons = benchDao.getBenchButtonsByPower(bccode, employee);
				benchBusinesses = benchDao.getSelfBenchBusinessesByPower(bccode, employee);
				if (benchBusinesses.size()==0) {
					benchBusinesses = benchDao.getBenchBusinessesByPower(bccode, employee);
				}else{
					hideBusinesses = benchDao.getHideBusinessesByPower(bccode, employee);
				}
			}
			
			bench.setBenchButtons(benchButtons);
			getBenchBusinesses(bccode, employee, Condition, noControl, benchBusinesses,false);
			getBenchBusinesses(bccode, employee, Condition, noControl, hideBusinesses,true);
			bench.setBenchBusinesses(benchBusinesses);
			bench.setHideBusinesses(hideBusinesses);
		}
		return bench;
	}
	
	private void getBenchBusinesses(String bccode, Employee employee, String Condition, boolean noControl,List<BenchBusiness> benchBusinesses,boolean Show){
		String sob = employee.getEm_master();
		for (BenchBusiness benchBusiness : benchBusinesses) {
			List<BenchScene> benchScenes = null;
			boolean show = false;
			if (noControl) {
				benchScenes = benchDao.getSelfBenchScenes(bccode, benchBusiness.getBb_code(), employee);
				show = Show||(baseDao.getCountByCondition("BenchSceneEmps inner join BenchScene on be_bscode = bs_code", "be_bccode = '"+bccode+"' and bs_bbcode = '"+benchBusiness.getBb_code()+"' and be_emid = " + employee.getEm_id() + " and be_iscount<>0 and nvl(bs_enable,0) <> 0")>1);
				if (benchScenes.size()==0) {
					benchScenes = benchDao.getBenchScenes(bccode, benchBusiness.getBb_code(),sob);
					show = Show||(baseDao.getCountByCondition("BenchScene", "bs_bccode = '"+bccode+"' and bs_bbcode = '"+benchBusiness.getBb_code()+"' and bs_iscount<>0 and nvl(bs_enable,0) <> 0")>1);
				}
			}else{
				benchScenes = benchDao.getSelfBenchScenesByPower(bccode, benchBusiness.getBb_code(), employee);
				show = Show||benchDao.isSelfShow(bccode, benchBusiness.getBb_code(), employee);
				if (benchScenes.size()==0) {
					benchScenes = benchDao.getBenchScenesByPower(bccode, benchBusiness.getBb_code(), employee);
					show = Show||benchDao.isShow(bccode, benchBusiness.getBb_code(), employee);
				}
			}
			
			if (benchScenes!=null&&benchScenes.size()>0) {
				int total = 0;
				for (BenchScene benchScene : benchScenes) {
					boolean bool = benchScene.getBs_islist()==-1;
					if (benchScene.getBs_iscount()!=0) {
						int count = 0;
						String condition = Condition==null?"":Condition;
						String bscode = benchScene.getBs_code();
						String fixCondition = benchScene.getBs_fixcond();
						if (StringUtil.hasText(fixCondition)) {
							String urlcondition = getUrlParam(fixCondition,"urlcondition");
							if (urlcondition!=null) {
								condition += "".equals(condition)?urlcondition:" and ("+urlcondition+")";
							}
						}
						try {
							Boolean _jobemployee = false;
							
							if (bool) {
								String caller = benchScene.getBs_caller();
								if (StringUtil.hasText(caller)) {
									if (!noControl) {
										_jobemployee = isJobEmployee(caller, employee);
									}
									count = dataListService.getCountByCaller(caller, condition, "true".equals(getUrlParam(fixCondition,"_self")),false, Constant.UAS_CLOUD.equals(getUrlParam(fixCondition,"_config")), _jobemployee);
								}
							}else{
								/** 添加其他约束条件 */
								condition = appendCondition(benchScene, condition, employee);
								/**添加权限条件，看自己/看所有 */
								
								if (!noControl) {
									_jobemployee = isJobEmployee(benchScene.getBs_caller(), employee);
									condition = appendPowerCondition(benchScene, employee, condition, null, _jobemployee, true);
								}
								condition = getCondition(benchScene, employee, condition);
								Object[] datalist_config = baseDao.getFieldsDataByCondition("datalistconfig left join datalistconfig$emp on id_=id$emp", "name_,filterjson_,id_", "caller_='"+bscode+"' and emcode$emp='"+employee.getEm_code()+"'");
								if(datalist_config==null){
									datalist_config = baseDao.getFieldsDataByCondition("datalistconfig", "name_,filterjson_,id_", "caller_='"+bscode+"' and isNorm_=-1 and isdefault_=-1");
								}
								if(datalist_config!=null){
									List<Map<Object, Object>> filterlist = JSONUtil.toMapList(datalist_config[1].toString());
									if (condition != null && condition.trim().length() > 0) {
										condition += " AND ";
									}
									String defaultfiltercondition = appendFilterCondition(filterlist);
									condition += defaultfiltercondition;
				
								}
								
								String sql = benchScene.getSearchSql(condition, employee);
								if(_jobemployee){
									sql = benchDao.getSqlWithJobEmployee(employee) + sql;			
								}
								count = baseDao.getCount(sql);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						total += count;
						
						if (show) {
							benchScene.setCount(count);
						}
						if (count>0&&benchBusiness.getActive()==null) {
							benchBusiness.setActive(bscode);
						}
					}
				}
				benchBusiness.setCount(total);
				if (benchBusiness.getActive()==null) {
					benchBusiness.setActive(benchScenes.get(0).getBs_code());
				}
				benchBusiness.setBenchScenes(benchScenes);
			}
		}
	}
	
	private String appendFilterCondition(List<Map<Object, Object>> filterlist){
		String condition = "";
		for(Map<Object,Object> map : filterlist){
			String originalxtype = map.get("originalxtype").toString();
			String type = map.get("type").toString();
			String value = map.get("value").toString();
			String column_value = map.get("column_value").toString();
			if (condition != null && condition.trim().length() > 0) {
				condition += " AND ";
			}
			if("textfield".equals(originalxtype)){
				if("direct".equals(type)){
					condition += column_value + "='" + value+"'";
				}else if("nodirect".equals(type)){
					condition += "nvl("+column_value + ",' ')<>'" + value+"'";
				}else if("vague".equals(type)){
					condition +="instr("+column_value+",'"+value+"')>0";
				}else if("novague".equals(type)){
					condition += "(instr("+column_value+",'"+value+"')=0 or "+column_value+" is null)";
				}else if("head".equals(type)){
					condition += "instr("+column_value+",'"+value+"')=1";
				}else if("end".equals(type)){
					condition += "instr("+column_value+",'"+value+"',-1,1)=LENGTH("+column_value+")-length('"+value+"')+1 and LENGTH("+column_value+")>=length('"+value+"')";
				}else if("null".equals(type)){
					condition += column_value + " is null";
				}
			}else if("numberfield".equals(originalxtype)){
				if("~".equals(type)||value.indexOf("~")>-1){
					String[] arr = value.split("~");
					condition += column_value + " between " + arr[0] + " and "+arr[1]+" ";
				}else if("!=".equals(type)){
					condition += "("+column_value + type + value + " or "+column_value +" is null) ";
				}else{
					condition += column_value + type + value;
				}
			}else if("datefield".equals(originalxtype)){
				SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd");
				try {
					if("~".equals(type)||value.indexOf("~")>-1){
						String[] arr = value.split("~");
						condition += "to_char(" + column_value + ",'yyyy-MM-dd') between '" + sdf.format(sdf.parse(arr[0])) + "' and '"+ sdf.format(sdf.parse(arr[1])) +"'";
					}else if(">=".equals(type)||value.indexOf(">=")==0){
						condition += "to_char(" + column_value + ",'yyyy-MM-dd')>='" + sdf.format(sdf.parse(value)) + "' ";
					}else if("<=".equals(type)||value.indexOf("<=")==0){
						condition += "to_char(" + column_value + ",'yyyy-MM-dd')<='" + sdf.format(sdf.parse(value)) + "' ";
					}else{
						condition += "to_char(" + column_value + ",'yyyy-MM-dd')='" + sdf.format(sdf.parse(value)) + "' ";
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if("combo".equals(originalxtype)){
				if("-所有-".equals(value)){
					condition += " 1=1 ";
				}else if("-无-".equals(value)){
					condition +="nvl(to_char("+column_value+"),' ')=' '";
				}else{
					condition += "instr("+column_value+",'"+value+"')=1";
				}
			}
		}
		return condition;
	}
	
	String getUrlParam(String condition,String name){
		if (condition==null) {
			return null;
		}
		Pattern pattern = Pattern.compile("(^|&)"+name+"=([^&]*)(&|$)");
		Matcher matcher = pattern.matcher(condition);
		if(matcher.find()){
			return parseUrl(matcher.group(2));
		}
	    return null;   
	}
	
	String parseUrl(String condition) {
		Employee employee = SystemSession.getUser();
		if (contains(condition, "session:em_uu", true)) { 
            condition = condition.replaceAll("session:em_uu", String.valueOf(employee.getEm_uu()));
        }
        if (contains(condition, "session:em_code", true)) { 
            condition = condition.replaceAll("session:em_code", "'" + employee.getEm_code() + "'");
        }
        if (contains(condition, "sysdate", true)) { 
            condition = condition.replaceAll("sysdate", "to_date('" + DateUtil.getCurrentDate() + "','yyyy-mm-dd')");
        }
        if (contains(condition, "session:em_name", true)) {
            condition = condition.replaceAll("session:em_name", "'" + employee.getEm_name() + "'");
        }
        if (contains(condition, "session:em_type", true)) {
            condition = condition.replaceAll("session:em_type", "'" + employee.getEm_type() + "'");
        }
        if (contains(condition, "session:em_id", true)) {
            condition = condition.replaceAll("session:em_id",String.valueOf(employee.getEm_id()));
        }
        if (contains(condition, "session:em_depart", true)) {
            condition = condition.replaceAll("session:em_depart",String.valueOf(employee.getEm_id()));
        }
        if (contains(condition, "session:em_defaulthsid", true)) {
            condition = condition.replaceAll("session:em_defaulthsid",String.valueOf(employee.getEm_defaulthsid()));
        }
		return condition;
	}
	
	boolean contains(String string, String substr, boolean isIgnoreCase){
		if (string == null || substr == null) return false;
		if (isIgnoreCase) {
			string = string.toLowerCase();
			substr = substr.toLowerCase();
		}
		return string.indexOf(substr) > -1;
	}

	/**
	 * 搜索工作台内容
	 **/
	@Override
	public List<Map<String, Object>> searchBench(String benchcode, boolean isCloud,
			boolean noControl, String search) {
		List<Map<String, Object>> bench = null;
		Employee employee = SystemSession.getUser();
		try {
			if (isCloud) {
				SpObserver.putSp(Constant.UAS_CLOUD);
				bench = searchBench(benchcode, employee, search, noControl);
				SpObserver.putSp(employee.getEm_master());
			} else {
				bench = searchBench(benchcode, employee, search, noControl);
			}
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError(e.getMessage());
		}
		return bench;
	}
	
	private List<Map<String, Object>> searchBench(String benchcode, Employee employee,
			 String search, boolean noControl){
		List<Map<String, Object>> bench = new ArrayList<Map<String,Object>>();
		
		if (benchcode!=null&&!"".equals(benchcode)) {
			bench.addAll(benchDao.searchBenchScene(benchcode, employee, search, noControl));
			bench.addAll(benchDao.searchBenchButton(benchcode, employee, search, noControl));
		}else{
			List<String> benchcodes = baseDao.queryForList("select bc_code from bench where nvl(bc_used,0)=-1 order by bc_detno", String.class);
			for (String code : benchcodes) {
				bench.addAll(benchDao.searchBenchScene(code, employee, search, noControl));
				bench.addAll(benchDao.searchBenchButton(code, employee, search, noControl));
			}
		}
		
		return bench;
	}

	/**
	 * 判断对应工作台、业务、场景是否存在
	 **/
	@Override
	public boolean isExist(boolean isCloud, boolean noControl, Employee employee, String bench, String business, String scene) {
		boolean isExist = false;
		try {
			String sob = SpObserver.getSp();
			if (isCloud) {
				SpObserver.putSp(Constant.UAS_CLOUD);
				isExist = benchDao.isExist(noControl, employee, bench, business, scene);
				SpObserver.putSp(sob);
			} else {
				isExist = benchDao.isExist(noControl, employee, bench, business, scene);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isExist;
	}
	
}
