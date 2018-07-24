package com.uas.mobile.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Enterprise;
import com.uas.erp.model.Form;
import com.uas.erp.model.FormDetail;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.mobile.dao.PanelDao;
import com.uas.mobile.model.ListColumn;
import com.uas.mobile.model.Panel;
import com.uas.mobile.model.PanelItem;
import com.uas.mobile.service.PanelService;
@Service
public class PanelServiceImpl implements PanelService {
	@Autowired
	private PanelDao panelDao;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private EnterpriseService enterpriseService;
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateMobileDefault(String caller,String formStore){
		Map<Object,Object> map = BaseUtil.parseFormStoreToMap(formStore);
		Set<Object> set = map.keySet();
		List<String> sqls = new ArrayList<String>();
		for(Object obj:set){
			Object value = map.get(obj);
			String upperField = obj.toString().toUpperCase();
			sqls.add("update mobileformdetail set mfd_isdefault=" + value + " where upper(mfd_field)='" + upperField
					+ "' and mfd_caller='"+caller+"'");
			sqls.add("update mobiledetailgrid set mdg_isdefault=" + value + " where upper(mdg_field)='" + upperField
					+ "' and mdg_caller='"+caller+"'");
			sqls.add("update formdetail set fd_mobileused="+value+" where upper(fd_field)='"+upperField+"' and fd_foid=(select fo_id from form where fo_caller='"+caller+"')");
			sqls.add("update detailgrid set dg_mobileused="+value+" where upper(dg_field)='"+upperField+"' and dg_caller='"+caller+"'");
		}
		baseDao.execute(sqls);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateMobileused(String caller,String formStore){
		Map<Object,Object> map = BaseUtil.parseFormStoreToMap(formStore);
		Set<Object> set = map.keySet();
		List<String> sqls = new ArrayList<String>();
		for(Object obj:set){
			Object value = map.get(obj);
			sqls.add("update formdetail set fd_mobileused=" + value + " where upper(fd_field)='" + obj.toString().toUpperCase() 
					+ "' and fd_foid=(select fo_id from form where fo_caller='"+caller+"')");
			sqls.add("update detailgrid set dg_mobileused=" + value + " where upper(dg_field)='" + obj.toString().toUpperCase()
					+ "' and dg_caller='"+caller+"'");
		}
		baseDao.execute(sqls);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteMobileFields(String caller,String fields){
		List<String> sqls = new ArrayList<String>();
		if(fields!=null&&!"".equals(fields)&&caller!=null&&!"".equals(caller)){
			String[] field = fields.split(",");		
			StringBuffer sb = new StringBuffer();
			for(String str:field){
				sb.append(",'" + str.toUpperCase() + "'");				
			}
			sqls.add("delete from mobileformdetail where mfd_caller='"+caller+"' and upper(mfd_field) in (" + sb.substring(1) + ")");
			sqls.add("delete from mobiledetailgrid where mdg_caller='"+caller+"' and upper(mdg_field) in (" + sb.substring(1) + ")");
		}
		baseDao.execute(sqls);
	}
	
	@Override
	public Panel getPanelByCaller(String caller, String formcondition,String gridcondition,String emcode) {
		// TODO Auto-generated method stub
		Panel panel=new Panel();Employee employee=null;
		List<PanelItem> items=new ArrayList<PanelItem>(); 
		List<ListColumn> listItems=new ArrayList<ListColumn>(); 
		Form form=panelDao.getMobileForm(caller);
		List<FormDetail>details=form.getFormDetails();
		for(FormDetail detail:details){
			items.add(new PanelItem(detail,null));
		}
		panel.setPanelItems(items);
		panel.setFormdata(baseDao.getFormData(form, formcondition));
		List<DetailGrid> detailgrids=panelDao.getPanelDetailsByCaller(caller);
		for(DetailGrid detail:detailgrids){
			listItems.add(new ListColumn(detail,null));
		}
		panel.setColumns(listItems);
		if(emcode!=null) employee=employeeDao.getEmployeeByEmCode(emcode);
		if(listItems.isEmpty()){
			
		}else{
		  		panel.setListdata(baseDao.getDetailGridData(detailgrids, gridcondition, employee, 1,5000));  		
		}
		return panel;
	}
	@Override
	public Map<String, Object> getProductDetail(String code) {
		// TODO Auto-generated method stub
		return baseDao.getJdbcTemplate().queryForMap("select pr_code 物料编号,pr_detail 物料名称,pr_spec 物料规格,pr_unit 单位,pr_kind 类型,nvl(pr_material,'未认可') 承认状态,nvl(po_onhand,0) 总库存,nvl(po_onhand,0)-nvl(Po_Defectonhand,0) 良品库存,nvl(Po_Defectonhand,0) 不良品库存  from product left join productonhand on pr_code=po_prodcode where pr_code='"+code+"'");
		
	}
	
	@Override
	public Map<String,Object> getFormAndGridDetail(String caller,String condition,String isprocess,String config,HttpSession session){
		Map<String,Object> detailData = new HashMap<String,Object>();
		//取formdetail数据
		List<Object[]> formDatas = null;
		List<Object[]> gridDatas = null;
		List<Map<String,Object>> foData = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> detailGridData = new ArrayList<Map<String,Object>>();
		
		//存放逻辑类型
		List<Map<String,Object>> logicData = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> gridLogicData = new ArrayList<Map<String,Object>>();
		
		boolean process = false;
		if(isprocess!=null&&"1".equals(isprocess)){
			process = true;
		}
		
		if(condition!=null){
			try{
				if(!process){
					formDatas = baseDao.getFieldsDatasByCondition("(mobileformdetail left join form on fo_caller=mfd_caller left join formdetail on fd_foid=fo_id and upper(fd_field)=upper(mfd_field))", new String[]{"FD_DETNO","FD_CAPTION","FD_FIELD","FD_TYPE","FD_ALLOWBLANK","FD_GROUP","MFD_ISDEFAULT","fd_dbfind","fd_id","fd_logictype","FD_FIELDLENGTH","fd_appwidth","fd_readonly","fd_defaultvalue","fd_render"},"mfd_caller='" + caller + "' and nvl(fd_field,' ')<>' ' and " + condition + " order by fd_group,fd_detno");
					gridDatas = baseDao.getFieldsDatasByCondition("(mobiledetailgrid left join detailgrid on dg_caller=mdg_caller and upper(dg_field)=upper(mdg_field))", new String[]{"DG_SEQUENCE","DG_CAPTION","DG_FIELD","DG_TYPE","DG_LOGICTYPE","mdg_isdefault","dg_id","DG_MAXLENGTH","nvl(dg_dbbutton,0)","dg_appwidth","dg_width","dg_mobileused","DG_FINDFUNCTIONNAME","dg_renderer"}, "mdg_caller='" + caller + "' and nvl(dg_field,' ')<>' ' and " + condition + " order by dg_sequence");
				}else{
					
					formDatas = baseDao.getFieldsDatasByCondition("(form left join formdetail on fd_foid=fo_id)", new String[]{"FD_DETNO","FD_CAPTION","FD_FIELD","nvl(FD_TYPE,'S')","FD_ALLOWBLANK","FD_GROUP","-1","fd_dbfind","fd_id","fd_logictype","FD_FIELDLENGTH","fd_appwidth","fd_readonly","fd_defaultvalue","fd_render","fd_mobileused"},"1".equals(config)?"fo_caller='" + caller + "' and nvl(fd_field,' ')<>' '  and " + condition + " order by fd_detno":"fo_caller='" + caller + "' and nvl(fd_field,' ')<>' ' and nvl(fd_mobileused,1)<>0 and " + condition + " order by fd_detno");
					gridDatas = baseDao.getFieldsDatasByCondition("detailgrid", new String[]{"DG_SEQUENCE","DG_CAPTION","DG_FIELD","NVL(DG_TYPE,'S')","DG_LOGICTYPE","-1","dg_id","DG_MAXLENGTH","nvl(dg_dbbutton,0)","dg_appwidth","dg_width","dg_mobileused","DG_FINDFUNCTIONNAME","dg_renderer"},"1".equals(config)?"dg_caller='" + caller + "' and nvl(dg_field,' ')<>' '  and " + condition + " order by dg_sequence" :"dg_caller='" + caller + "' and nvl(dg_field,' ')<>' ' and nvl(dg_mobileused,1)<>0 and " + condition + " order by dg_sequence");					
				}
				
			}catch(Exception e){
				e.printStackTrace();
				BaseUtil.showError("参数错误");
			}
			if(formDatas!=null){
				Map<String,Object> fdData = null;
				for(Object[] data:formDatas){
					fdData = new HashMap<String,Object>();
					fdData.put("fd_detno", data[0]);
					fdData.put("fd_caption",data[1]);
					fdData.put("fd_field", data[2]);
					//增加字段长度
					if(data[10]=="null"||null==data[10]){data[10]=0;};
					fdData.put("fd_maxlength", data[10]);
					
					String language = SystemSession.getLang();
					language = language == null ? "zh_CN" : language;
					Object value = "";
					if (data[13]!=null) {
						value = decodeDefaultValue(session, String.valueOf(data[13]), language);
					}
					
					String changeType = null;
					if(data[3]!=null){
						changeType = changeFieldType("fieldtype",data[3].toString(),"form");
					}
					
					if(data[7]!=null){
						if(!"F".equals(data[7].toString())){
							changeType = changeFieldType("dbfind",data[7].toString(),"form");
							fdData.put("fd_type", changeType);
						}else{
							fdData.put("fd_type", data[3]==null?"":changeType);
						}
					}else{
						fdData.put("fd_type", data[3]==null?"":changeType);
					}
					if(changeType!=null&&("C".equals(changeType)||"EC".equals(changeType))){
						String comboCaller = caller;
						if(comboCaller.indexOf("$Change")>-1){
							comboCaller = comboCaller.substring(0,comboCaller.indexOf("$Change"));
						}
						
						fdData.put("COMBOSTORE", getComboStore(comboCaller,data[2].toString(),data[3].toString()));
					}
					
					if(data[9]!=null&&!"".equals(data[9])){
						Map<String,Object> map = new HashMap<String,Object>();
						String logicType = data[9].toString().toLowerCase();
						map.put("logicType", logicType);
						map.put("type",changeType);
						logicData.add(map);
						value = parseValue(value,changeType,logicType);
					}
					
					fdData.put("fd_defaultvalue", value);
					fdData.put("fd_allowblank", "T".equals(data[4])?"T":"F");
					fdData.put("fd_group", data[5]);
					if (changeType!=null&&"H".equals(changeType)) {
						fdData.put("mfd_isdefault", 0);
					}else {
						fdData.put("mfd_isdefault", data[6]);
					}
					fdData.put("fd_id", data[8]);
					fdData.put("fd_appwidth", data[11]);
					fdData.put("fd_logictype", data[9]);
					fdData.put("fd_readonly", "T".equals(data[12])?"T":"F");
					fdData.put("fd_dbfind", data[7]);
					fdData.put("fd_render", data[14]);
					foData.add(fdData);
				}
			}
			
			//form逻辑类型转换
			if(logicData.size()>0){
				for(Map<String,Object> map:logicData){
					String logicType = map.get("logicType").toString();
					String type = map.get("type").toString();
					for(Map<String,Object> formMap:foData){
						if(StringUtil.hasText(formMap.get("fd_field"))){
							if(logicType.equals(formMap.get("fd_field").toString().toLowerCase())){
								formMap.remove("fd_type");
								formMap.put("fd_type", type);
							}							
						}
					}
				}
			}
			
			if(gridDatas!=null&&gridDatas.size()>0){
				Map<String,Object> gdData = null;
				for(Object[] data:gridDatas){
					gdData = new HashMap<String,Object>();
					gdData.put("dg_sequence", data[0]);
					gdData.put("dg_caption", data[1]);
					gdData.put("dg_field", data[2]);
					if(data[7]=="null"||null==data[7]){data[7]=0;};
					gdData.put("dg_maxlength", data[7]);
					
					String changeType = null;
					if(data[3]!=null){
						changeType = changeFieldType(null,data[3].toString(),"grid");
						
						if(data[8]!=null&&!"".equals(data[8])){
							if(!"0".equals(data[8].toString())){
								changeType = "DF";
								if("-2".equals(data[8].toString())){
									changeType = "M";
								}
							}
						}
					}
					gdData.put("COMBOSTORE",getComboStore(caller,data[2].toString(),data[3].toString()));
					gdData.put("dg_type", data[3]==null?"":changeType);
					
					if(data[4]!=null&&!"".equals(data[4])){
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("logicType", data[4].toString().toLowerCase());
						map.put("type",changeType);
						gridLogicData.add(map);
					}
					
					gdData.put("dg_logictype", data[4]);
					
					
					gdData.put("mdg_isdefault", data[5]);
					gdData.put("gd_id", data[6]);
					gdData.put("dg_appwidth", data[9]);
					gdData.put("dg_width", data[10]);
					gdData.put("dg_findfunctionname", data[12]);
					gdData.put("dg_renderer", data[13]);
					detailGridData.add(gdData);
				}
			}
			
			//grid逻辑类型转换
			/*if(gridLogicData.size()>0){
				for(Map<String,Object> map:logicData){
					String logicType = map.get("logicType").toString();
					System.out.println("logicType="+logicType);
					
					String type = map.get("type").toString();
					for(Map<String,Object> gridMap:detailGridData){
						System.out.println("fd_field="+gridMap.get("fd_field"));
						if(logicType.equals(gridMap.get("fd_field").toString().toLowerCase())){
							gridMap.remove("fd_type");
							gridMap.put("fd_type", type);
						}
					}
				}
			}*/
		}
		detailData.put("formdetail", foData);
		detailData.put("gridetail", detailGridData);
		
		return detailData;
	}
	
	public List<Map<String,Object>> getComboStore(String caller,String field,String type){
		List<Map<String,Object>> comboStore = new ArrayList<Map<String,Object>>();
		if(caller!=null&&field!=null&&("C".equals(type)||"combo".equals(type))){
			comboStore = baseDao.queryForList("select dlc_value,dlc_display,dlc_detno from datalistcombo where dlc_caller='"+caller+"' and dlc_fieldname='"+field+"' order by dlc_detno asc");
		}
		if("yncolumn".equals(type)||"tfcolumn".equals(type)||"YN".equals(type)||"B".equals(type)){
			Map<String,Object> confirm = new HashMap<String,Object>();
			Map<String,Object> cancel = new HashMap<String,Object>();
			String value = "-1";
			if("tfcolumn".equals(type)||"B".equals(type)){
				value = "1";
			}
			confirm.put("DLC_VALUE", "是");
			confirm.put("DLC_DISPLAY", value);
			confirm.put("DLC_DETNO", "0");
			cancel.put("DLC_VALUE", "否");
			cancel.put("DLC_DISPLAY", "0");
			cancel.put("DLC_DETNO", "1");
			comboStore.add(confirm);
			comboStore.add(cancel);
		}
		return comboStore;
	}
	
	@Override
	public void updateDetailData(String formStore, String gridStore){
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		if(store.size()>0){
			//执行修改前的其它逻辑
			handlerService.beforeUpdate("Form", new Object[]{store});
			//修改Form
			String formSql = SqlUtil.getUpdateSqlByFormStore(store, "FORMDETAIL", "fd_id");
			baseDao.execute(formSql);
			//记录操作
			baseDao.logger.save("Form", "fd_id", store.get("fd_id"));
			//执行修改后的其它逻辑
			handlerService.afterSave("Form", new Object[]{store});
		}
		
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		if(gstore.size()>0){
			List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "DETAILGRID", "dg_id");
			baseDao.execute(gridSql);
			// 执行修改后的其它逻辑
			handlerService.afterUpdate("DetailGrid", new Object[] { gstore });
			
		}
	}
	
	//从UAS自定义类型到移动端所用类型
	private String changeFieldType(String field,String type,String formOrGrid){
		String changeType = type;
		if("form".equals(formOrGrid)){
			if("fieldtype".equals(field)){
				
				if("S".equals(type)){	
					changeType = "SS";
				}else if("IN".equals(type)){
					changeType = "N";
				}else if("SN".equals(type)){
					changeType = "N";
				}else if("DT".equals(type)){
					changeType = "D";
				}else if("TF".equals(type)){
					changeType = "D";
				}else if("T".equals(type)){
					changeType = "MS";
				}else if("Html".equals(type)){
					changeType = "MS";
				}else if("CF".equals(type)){
					changeType = "SS";
				}else if("CDHM".equals(type)){
					changeType = "D";
				}else if("MT".equals(type)){
					changeType = "SF";
				}else if("DHMC".equals(type)){
					changeType = "D";
				}else if("CBC".equals(type)){
					changeType = "CBG";
				}else if("TA".equals(type)){
					changeType = "MS";
				}else if("MF".equals(type)){
					changeType = "FF";
				}
		}else if("dbfind".equals(field)){
			if("form".equals(formOrGrid)){
				if("T".equals(type)){
					changeType = "SF";
				}else if("AT".equals(type)){
					changeType = "MF";
				}else if("M".equals(type)){
					changeType = "MF";
				}
			}
		}
		}else if("grid".equals(formOrGrid)){
			if("text".equals(type)){
				changeType = "S";
			}else if("numbercolumn".equals(type)){
				changeType = "N";
			}else if("floatcolumn".equals(type)){
				changeType = "N";
			}else if("floatcolumn4".equals(type)){
				changeType = "N";
			}else if("floatcolumn6".equals(type)){
				changeType = "N";
			}else if("combo".equals(type)){
				changeType = "C";
			}else if("yncolumn".equals(type)){
				changeType = "C";
			}else if("tfcolumn".equals(type)){
				changeType = "C";
			}else if("datecolumn".equals(type)){
				changeType = "D";
			}else if("datetimecolumn".equals(type)){
				changeType = "D";
			}else if("datetimecolumn2".equals(type)){
				changeType = "D";
			}else if("texttrigger".equals(type)){
				changeType = "MS";
			}else if("textareafield".equals(type)){
				changeType = "MS";
			}else if ("checkcolumn-1".equals(type)){
				changeType = "YN";
			}else if("checkcolumn".equals(type)){
				changeType = "B";
			}
		}
		return changeType;
	}
	
	/**
	 * 将数据库里面的defaultvalue转化成实际要显示的值
	 * 
	 * @param value
	 *            formDetail.getFd_defaultvalue()
	 */
	private Object decodeDefaultValue(HttpSession session, Object value, String language) {
		
		if (session.getAttribute("em_id")==null) {
			//加人员信息到session;
			Employee employee = (Employee) session.getAttribute("employee");
			addSessionEmployee(session, employee);
		}
		
		if (value != null && !value.equals("null")) {
			String val = value.toString();
			if (val.contains("getCurrentDate()")) {
				return DateUtil.parseDateToString(null, Constant.YMD);
			} else if (val.contains("getCurrentTime()")) {
				return DateUtil.parseDateToString(null, Constant.YMD_HMS);
			} else if (val.contains("session:")) {
				Object obj = session.getAttribute(val.trim().split(":")[1]);
				return (obj == null) ? "" : obj;
			} else if (val.contains("getLocal(")) {
				Object obj = BaseUtil.getLocalMessage(val.substring(val.indexOf("(") + 1, val.lastIndexOf(")")), language);
				return (obj == null) ? "" : obj;
			}
			return value;
		} else {
			return "";
		}
	}
	
	private void addSessionEmployee(HttpSession session, Employee employee) {
		Enterprise enterprise = enterpriseService.getEnterprise();
		if (enterprise != null) {
			session.setAttribute("en_admin", enterprise.getEn_Admin());
		}
		session.setAttribute("en_uu", enterprise.getEn_uu());
		session.setAttribute("en_name", enterprise.getEn_Name());
		session.setAttribute("em_uu", employee.getEm_id());
		session.setAttribute("em_id", employee.getEm_id());
		session.setAttribute("em_name", employee.getEm_name());
		session.setAttribute("em_code", employee.getEm_code());
		session.setAttribute("em_position", employee.getEm_position());
		session.setAttribute("em_defaulthsid", employee.getEm_defaulthsid());
		session.setAttribute("em_defaultorid", employee.getEm_defaultorid());
		session.setAttribute("em_defaultorname", employee.getEm_defaultorname());
		session.setAttribute("em_depart", employee.getEm_depart());
		session.setAttribute("em_departmentcode", employee.getEm_departmentcode());
		session.setAttribute("em_type", employee.getEm_type());
		session.setAttribute("em_saledepart", employee.getEm_saledepart());
		session.setAttribute("em_mobile", employee.getEm_mobile());
		session.setAttribute("username", employee.getEm_code());
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
	public Map<String, Object> getFormPanel(HttpSession session,String caller) {
			Map<String,Object> detailData = new HashMap<String,Object>();
			//取formdetail数据
			List<Object[]> formDatas = null;
			List<Map<String,Object>> foData = new ArrayList<Map<String,Object>>();
			
			//存放逻辑类型
			List<Map<String,Object>> logicData = new ArrayList<Map<String,Object>>();

				try{
						formDatas = baseDao.getFieldsDatasByCondition("(mobileformdetail left join form on fo_caller=mfd_caller left join formdetail on fd_foid=fo_id and upper(fd_field)=upper(mfd_field))", new String[]{"FD_DETNO","FD_CAPTION","FD_FIELD","FD_TYPE","FD_ALLOWBLANK","FD_GROUP","MFD_ISDEFAULT","fd_dbfind","fd_id","fd_logictype","FD_FIELDLENGTH","fd_appwidth","fd_readonly","fd_defaultvalue"},"mfd_caller='" + caller + "' and nvl(fd_field,' ')<>' ' and nvl(fd_mobileused,0) <>0 order by fd_group,fd_detno");
					
				}catch(Exception e){
					e.printStackTrace();
					BaseUtil.showError("参数错误");
				}
				if(formDatas!=null){
					Map<String,Object> fdData = null;
					for(Object[] data:formDatas){
						fdData = new HashMap<String,Object>();
						fdData.put("fd_detno", data[0]);
						fdData.put("fd_caption",data[1]);
						fdData.put("fd_field", data[2]);
						//增加字段长度
						if(data[10]=="null"||null==data[10]){data[10]=0;};
						fdData.put("fd_maxlength", data[10]);
						
						String language = SystemSession.getLang();
						language = language == null ? "zh_CN" : language;
						Object value = "";
						if (data[13]!=null) {
					    		value = decodeDefaultValue(session, String.valueOf(data[13]), language);
					    	if(("session:em_code").equals(data[13].toString())){
					    		value = SystemSession.getUser().getEm_code();
					    	}else if(("session:em_name").equals(data[13].toString())){
					    		value = SystemSession.getUser().getEm_name();
					    	}					    	
						}
						String changeType = null;
						if(data[3]!=null){
							changeType = changeFieldType("fieldtype",data[3].toString(),"form");
						}
						
						if(data[7]!=null){
							if(!"F".equals(data[7].toString())){
								changeType = changeFieldType("dbfind",data[7].toString(),"form");
								fdData.put("fd_type", changeType);
							}else{
								fdData.put("fd_type", data[3]==null?"":changeType);
							}
						}else{
							fdData.put("fd_type", data[3]==null?"":changeType);
						}
						if(changeType!=null&&("C".equals(changeType)||"EC".equals(changeType))){
							String comboCaller = caller;
							if(comboCaller.indexOf("$Change")>-1){
								comboCaller = comboCaller.substring(0,comboCaller.indexOf("$Change"));
							}
							
							fdData.put("COMBOSTORE", getComboStore(comboCaller,data[2].toString(),data[3].toString()));
						}
						
						if(data[9]!=null&&!"".equals(data[9])){
							Map<String,Object> map = new HashMap<String,Object>();
							String logicType = data[9].toString().toLowerCase();
							map.put("logicType", logicType);
							map.put("type",changeType);
							logicData.add(map);
							value = parseValue(value,changeType,logicType);
						}
						
						fdData.put("fd_defaultvalue", value);
						fdData.put("fd_allowblank", "T".equals(data[4])?"T":"F");
						fdData.put("fd_group", data[5]);
						if (changeType!=null&&"H".equals(changeType)) {
							fdData.put("mfd_isdefault", 0);
						}else {
							fdData.put("mfd_isdefault", data[6]);
						}
						fdData.put("fd_id", data[8]);
						fdData.put("fd_appwidth", data[11]);
						fdData.put("fd_logictype", data[9]);
						fdData.put("fd_readonly", "T".equals(data[12])?"T":"F");
						fdData.put("fd_dbfind", data[7]);
						foData.add(fdData);
					}
				}
				
				//form逻辑类型转换
				if(logicData.size()>0){
					for(Map<String,Object> map:logicData){
						String logicType = map.get("logicType").toString();
						String type = map.get("type").toString();
						for(Map<String,Object> formMap:foData){
							if(StringUtil.hasText(formMap.get("fd_field"))){
								if(logicType.equals(formMap.get("fd_field").toString().toLowerCase())){
									formMap.remove("fd_type");
									formMap.put("fd_type", type);
								}							
							}
						}
					}
				}
			detailData.put("formdetail", foData);
			
			return detailData;
	}

	@Override
	public Map<String, Object> getGridPanel(String caller,String condition) {
		Map<String,Object> detailData = new HashMap<String,Object>();
		detailData.put("gridItem", getGridPanelByCaller(caller,"",condition,SystemSession.getUser().getEm_code()).getColumns());
		if(("DeviceBatch!Stock").equals(caller) && condition != null && !("").equals(condition)){
			SqlRowList rs = baseDao.queryForRowSet("select * from DeviceChange where "+condition);
			if(rs.next()){
				detailData.put("gridData", rs.getResultList());
			}
		}else if(("Device").equals(caller) && condition != null && !("").equals(condition)){
			SqlRowList rs = baseDao.queryForRowSet("select * from DeviceChange where "+condition);
			if(rs.next()){
				detailData.put("gridData", rs.getResultList());
			}
		}else if(("DeviceResume").equals(caller) && condition != null && !("").equals(condition)){
			SqlRowList rs = baseDao.queryForRowSet("select * from DeviceChange where "+condition);
			if(rs.next()){
				detailData.put("gridData", rs.getResultList());
			}
		}
		
		return detailData;
	
	}

	
	private Panel getGridPanelByCaller(String caller, String formCondition, String gridCondition, String emcode) {
		// TODO Auto-generated method stub
		Panel panel=new Panel();
		List<ListColumn> listItems=new ArrayList<ListColumn>(); 
		
	
		List<DetailGrid> detailgrids=panelDao.getPanelDetailsByCaller(caller);
		for(DetailGrid detail:detailgrids){
			listItems.add(new ListColumn(detail,null));
		}
		panel.setColumns(listItems);
		if(emcode!=null) {
		}
		return panel;
	
	}

	@Override
	public Map<String, Object> getFormPanelAndData(HttpSession session, String caller, Integer id, String condition) {

		Map<String,Object> detailData = new HashMap<String,Object>();
		List<Object[]> formDatas = null;
		List<Map<String,Object>> foData = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> logicData = new ArrayList<Map<String,Object>>();
			try{
					formDatas = baseDao.getFieldsDatasByCondition("(mobileformdetail left join form on fo_caller=mfd_caller left join formdetail on fd_foid=fo_id and upper(fd_field)=upper(mfd_field))", new String[]{"FD_DETNO","FD_CAPTION","FD_FIELD","FD_TYPE","FD_ALLOWBLANK","FD_GROUP","MFD_ISDEFAULT","fd_dbfind","fd_id","fd_logictype","FD_FIELDLENGTH","fd_appwidth","fd_readonly","fd_defaultvalue"},"mfd_caller='" + caller + "' and nvl(fd_field,' ')<>' ' and nvl(fd_mobileused,0) <>0 order by fd_group,fd_detno");
				
			}catch(Exception e){
				e.printStackTrace();
				BaseUtil.showError("参数错误");
			}
			if(formDatas!=null){
				Map<String,Object> fdData = null;
				for(Object[] data:formDatas){
					fdData = new HashMap<String,Object>();
					fdData.put("fd_detno", data[0]);
					fdData.put("fd_caption",data[1]);
					fdData.put("fd_field", data[2]);
					//增加字段长度
					if(data[10]=="null"||null==data[10]){data[10]=0;};
					fdData.put("fd_maxlength", data[10]);
					
					String language = SystemSession.getLang();
					language = language == null ? "zh_CN" : language;
					Object value = "";
					if (data[13]!=null) {
				    		value = decodeDefaultValue(session, String.valueOf(data[13]), language);
				    	if(("session:em_code").equals(data[13].toString())){
				    		value = SystemSession.getUser().getEm_code();
				    	}else if(("session:em_name").equals(data[13].toString())){
				    		value = SystemSession.getUser().getEm_name();
				    	}					    	
					}
					String changeType = null;
					if(data[3]!=null){
						changeType = changeFieldType("fieldtype",data[3].toString(),"form");
					}
					
					if(data[7]!=null){
						if(!"F".equals(data[7].toString())){
							changeType = changeFieldType("dbfind",data[7].toString(),"form");
							fdData.put("fd_type", changeType);
						}else{
							fdData.put("fd_type", data[3]==null?"":changeType);
						}
					}else{
						fdData.put("fd_type", data[3]==null?"":changeType);
					}
					if(changeType!=null&&("C".equals(changeType)||"EC".equals(changeType))){
						String comboCaller = caller;
						if(comboCaller.indexOf("$Change")>-1){
							comboCaller = comboCaller.substring(0,comboCaller.indexOf("$Change"));
						}
						
						fdData.put("COMBOSTORE", getComboStore(comboCaller,data[2].toString(),data[3].toString()));
					}
					
					if(data[9]!=null&&!"".equals(data[9])){
						Map<String,Object> map = new HashMap<String,Object>();
						String logicType = data[9].toString().toLowerCase();
						map.put("logicType", logicType);
						map.put("type",changeType);
						logicData.add(map);
						value = parseValue(value,changeType,logicType);
					}
					
					fdData.put("fd_defaultvalue", value);
					fdData.put("fd_allowblank","T".equals(data[4])?"T":"F");
					fdData.put("fd_group", data[5]);
					if (changeType!=null&&"H".equals(changeType)) {
						fdData.put("mfd_isdefault", 0);
					}else {
						fdData.put("mfd_isdefault", data[6]);
					}
					fdData.put("fd_id", data[8]);
					fdData.put("fd_appwidth", data[11]);
					fdData.put("fd_logictype", data[9]);
					fdData.put("fd_readonly", "T".equals(data[12])?"T":"F");
					fdData.put("fd_dbfind", data[7]);
					foData.add(fdData);
				}
			}
			
			//form逻辑类型转换
			if(logicData.size()>0){
				for(Map<String,Object> map:logicData){
					String logicType = map.get("logicType").toString();
					String type = map.get("type").toString();
					for(Map<String,Object> formMap:foData){
						if(StringUtil.hasText(formMap.get("fd_field"))){
							if(logicType.equals(formMap.get("fd_field").toString().toLowerCase())){
								formMap.remove("fd_type");
								formMap.put("fd_type", type);
							}							
						}
					}
				}
			}
		detailData.put("formdetail", foData);
		SqlRowList rs = baseDao.queryForRowSet("select fo_table,FO_DETAILCONDITION,fo_keyfield from form where fo_caller = ?",caller);
		String table = "";
		String defaultCondition = "";
		String keyFiled = "";
		if(rs.next()){
			table = rs.getString("fo_table");
			defaultCondition = rs.getString("FO_DETAILCONDITION");
			keyFiled = rs.getString("fo_keyfield");
		}
		if(defaultCondition == null || ("").equals(defaultCondition)){
			rs = baseDao.queryForRowSet("select * from "+table+" where "+keyFiled+" ="+id +" and "+condition);
			if(rs.next()){
				detailData.put("formData", rs.getCurrentMap());
			}
		}else{
			rs = baseDao.queryForRowSet("select * from "+table+" where "+keyFiled+" ="+id +" and "+defaultCondition +" and "+condition);
			if(rs.next()){
				detailData.put("formData", rs.getCurrentMap());
			}
		}
		return detailData;
	}

	@Override
	public Map<String, Object> getGridPanelandDataPage(String caller, String condition, int page, int pageSize) {
		Map<String,Object> detailData = new HashMap<String,Object>();
		detailData.put("gridItem", getGridPanelByCaller(caller,"",condition,SystemSession.getUser().getEm_code()).getColumns());
		int start = ((page - 1) * pageSize + 1);
		int end = page * pageSize;
		if(("DeviceBatch!Stock").equals(caller) && condition != null && !("").equals(condition)){
			SqlRowList rs = baseDao.queryForRowSet("select * from (select tt.*,rownum rn from "
				+ " (select * from DeviceChange where "+condition +" order by dc_detno asc )tt where rownum<=?) where rn>=?",end,start);
			if(rs.next()){
				detailData.put("gridData", rs.getResultList());
			}
		}
		return detailData;
	
	}

	@Override
	public Map<String, Object> getFormConfig(String caller) {
		Map<String,Object> config = new HashMap<String,Object>();
		if(caller!=null&&!caller.equals("")){
			Object[] configData = baseDao.getFieldsDataByCondition("form", "fo_keyfield,fo_detailkeyfield,fo_detailmainkeyfield,fo_statusfield,fo_statuscodefield", "fo_caller='"+caller+"'");
			if(configData!=null){
				config.put("fo_keyfield",configData[0]);
				config.put("fo_detailkeyfield",configData[1]);
				config.put("fo_detailmainkeyfield",configData[2]);
				config.put("fo_statusfield",configData[3]);
				config.put("fo_statuscodefield",configData[4]);
			}
		}
		return config;
	}
	
	
}
