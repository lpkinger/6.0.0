package com.uas.erp.service.common.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlMap;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.dao.common.FormDao;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Form;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.common.CommonChangeService;

@Service
public class CommonChangeServiceImpl implements CommonChangeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private FormDao formDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private DetailGridDao detailGridDao;
	private static String logtable = "commonchangelog";
	private static String logkeyfield = "cl_id";

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveCommonChange(String caller, String formStore, String GridStore) {
		Employee employee = SystemSession.getUser();
		String language = SystemSession.getLang();
		Map<Object, Object> formdata = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[]{formdata});
		//formdata=checkBycaller(formdata,caller);//对界面信息进行处理
		Map<Object, Object> logdata = getLogData(formdata, caller);
		baseDao.execute(SqlUtil.getInsertSqlByMap(logdata, logtable));
		updateFormData(formdata, caller);
		handlerService.afterSave(caller, new Object[]{formdata});
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.save", language), BaseUtil.getLocalMessage(
				"msg.saveSuccess", language), caller + "|" + logkeyfield + "=" + formdata.get(logkeyfield)));
	}

	@Override
	public void updateCommonChange(String caller, String formStore, String GridStore) {
		Employee employee = SystemSession.getUser();
		String language = SystemSession.getLang();
		Map<Object, Object> formdata = BaseUtil.parseFormStoreToMap(formStore);
		Map<Object, Object> logdata = getLogData(formdata, caller);
		handlerService.beforeUpdate(caller, new Object[]{formdata});
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(logdata, logtable, logkeyfield));
		updateFormData(formdata, caller);
		handlerService.afterUpdate(caller, new Object[]{formdata});
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.update", language), BaseUtil
				.getLocalMessage("msg.updateSuccess", language), caller + "|" + logkeyfield + "=" + formdata.get(logkeyfield)));
	}

	@Override
	public void deleteCommonChange(int id, String caller) {
		Employee employee = SystemSession.getUser();
		String language = SystemSession.getLang();
		baseDao.deleteById(logtable, logkeyfield, id);
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.delete", language), BaseUtil
				.getLocalMessage("msg.deleteSuccess", language), caller + "|" + logkeyfield + "=" + id));
	}

	@SuppressWarnings("rawtypes")
	private Map<Object, Object> getLogData(Map<Object, Object> map, String caller) {
		Set<Object> key = map.keySet();
		String[] fields = null;
		Map<Object, Object> LogData = new HashMap<Object, Object>();
		SqlRowList sl = baseDao
				.queryForRowSet("select  wmsys.wm_concat(fd_field) from formdetail  left join form on fd_foid=fo_id where fo_caller='"
						+ caller + "' and upper(fd_table)='COMMONCHANGELOG'");
		if (sl.next()) {
			fields = sl.getString(1).split(",");
		}
		for (Iterator it = key.iterator(); it.hasNext();) {
			String s = (String) it.next();
			for (String field : fields) {
				if (s.equals(field)) {
					LogData.put(s, map.get(s));
					break;
				}
			}
		}
		return LogData;
	}

	private void updateFormData(final Map<Object, Object> formdata, final String caller) {
		final String keyField = baseDao.getFieldValue("FormDetail left join form on fd_foid=fo_id", "fd_field", "fo_caller='" + caller
				+ "' and Fd_Logictype='changeKeyField'", String.class);
		if (keyField == null || NumberUtil.isEmpty(formdata.get(keyField))) {
			BaseUtil.showError("需更新表未设置主键，或主键值为0！");
		}

		final String codeField = baseDao.getFieldValue("FormDetail left join form on fd_foid=fo_id", "fd_field", "fo_caller='" + caller
				+ "' and Fd_Logictype='changeCodeField'", String.class);
		//客户资料变更
		if(("Customer!Base$Change".equals(caller)||"Customer$Change".equals(caller))&&formdata.get("cu_code")!=null){
			String cucode=formdata.get("cu_code").toString();
			//如果应收客户编号跟客户编号一致,“变更后应收客户名称”=“变更后客户名称”
			if(formdata.get("cu_name-new")!=null&&formdata.get("cu_arcode-new")!=null&&formdata.get("cu_arcode-new").toString().equals(cucode)){
				formdata.put("cu_arname-new", formdata.get("cu_name-new"));
			}
			//如果收货客户编号跟客户编号一致,“变更后收货客户名称”=“变更后客户名称”
			if(formdata.get("cu_name-new")!=null&&formdata.get("cu_shcustcode-new")!=null&&formdata.get("cu_shcustcode-new").toString().equals(cucode)){
				formdata.put("cu_shcustname-new", formdata.get("cu_name-new"));
			}
		}
		/**
		 * 不在baseDao外使用OracleLobHandler <br>
		 * 使用SqlMap处理，自动判断lob字段
		 * 
		 * @since 2016-4-1 13:32:53
		 */
		SqlMap map = new SqlMap("COMMONCHANGELOG", "CL_ID", formdata.get(logkeyfield));
		map.set("CL_KEYFIELD", keyField);
		map.set("CL_CODEVALUE", formdata.get(codeField));
		map.set("CL_DATA", JSONObject.fromObject(formdata).toString());
		map.set("CL_KEYVALUE", formdata.get(keyField));
		map.set("CL_CALLER", caller);
		baseDao.execute(map);
	}

	@Override
	public void submitCommonChange(String caller, int id) {
		updateChangetype(caller,id);
		Object[] status = baseDao.getFieldsDataByCondition(logtable, new String[] { "CL_STATUSCODE", "CL_KEYVALUE", "CL_CODEVALUE" },
				logkeyfield + "=" + id);
		String language = SystemSession.getLang();
		Employee employee = SystemSession.getUser();
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError("只能对在录入状态下的单据进行提交！");
		}
		int count = baseDao.getCountByCondition(logtable, "cl_caller='" + caller + "' and cl_statuscode = 'COMMITED' and cl_keyvalue="
				+ status[1] + " and cl_id<>" + id);
		if (count > 0) {
			BaseUtil.showError("[" + status[2] + "]只能存在一张已提交未审核的变更单");
		}
		Map<Object, Object> updatemap = formatData(caller, id);
		if ("Vendor$Change".equals(caller)) {
			if (updatemap.containsKey("ve_uu")) {
				Object veid = updatemap.get("ve_id");
				Object ve_uu = updatemap.get("ve_uu");
				if (StringUtil.hasText(ve_uu)) {
					String veuu = baseDao.getFieldValue("Vendor", "ve_uu", "ve_id=" + veid, String.class);
					if (!ve_uu.equals(veuu)) {
						int i = baseDao.getCount("select count(*) from purchase left join Vendor on pu_vendcode=ve_code where ve_id="
								+ veid + " and pu_sendstatus='已上传'");
						if (i > 0) {
							BaseUtil.showError("存在已上传的采购单，不允许变更供应商UU号！");
						}
						i = baseDao.getCount("select count(*) from inquiry left join Vendor on in_vendcode=ve_code where ve_id=" + veid
								+ " and in_sendstatus='已上传'");
						if (i > 0) {
							BaseUtil.showError("存在已上传的询价单，不允许变更供应商UU号！");
						}
					}
				}
			}
		}else if("Customer!Base$Change".equals(caller)||"Customer$Change".equals(caller)){
			updateFormData(BaseUtil.parseFormStoreToMap(baseDao.getFieldDataByCondition(logtable, "cl_data",  "cl_id=" + id).toString()), caller);
		}else if(caller.equals("Product!Base$Change")||caller.equals("Product!Check$Change")||caller.equals("Product!Feature$Change")||
				caller.equals("Product!Finance$Change")||caller.equals("Product!Plan$Change")||caller.equals("Product!Purchase$Change")||
				caller.equals("Product!Reserve$Change")||caller.equals("Product!Sale$Change")){
			checkProduct(updatemap);//物料关键属性的值有效性校验		
		}
		handlerService.handler(caller, "commit", "before", new Object[] { id });
		baseDao.updateByCondition(logtable, "cl_status='已提交',cl_statuscode='COMMITED'", "cl_id=" + id);
		handlerService.handler(caller, "commit", "after", new Object[] { id });
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.submit", language), BaseUtil
				.getLocalMessage("msg.submitSuccess", language), caller + "|" + logkeyfield + "=" + id));
	}

	@Override
	public void resSubmitCommonChange(String caller, int id) {
		Object status = baseDao.getFieldDataByCondition(logtable, "CL_STATUSCODE", logkeyfield + "=" + id);
		String language = SystemSession.getLang();
		Employee employee = SystemSession.getUser();
		if (!status.equals("COMMITED")) {
			BaseUtil.showError("只能对已提交状态下的单据进行提交！");
		}
		handlerService.handler(caller, "resCommit", "before", new Object[] { id });
		baseDao.updateByCondition(logtable, "cl_status='在录入',cl_statuscode='ENTERING'", "cl_id=" + id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { id });
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.resSubmit", language), BaseUtil
				.getLocalMessage("msg.resSubmitSuccess", language), caller + "|" + logkeyfield + "=" + id));
	}

	@Override
	public void auditCommonChange(int id, String caller) {
		Employee employee = SystemSession.getUser();
		Object[] data = baseDao.getFieldsDataByCondition(logtable, new String[] { "CL_STATUSCODE", "CL_KEYFIELD", "CL_KEYVALUE",
				"CL_CODEVALUE" }, logkeyfield + "=" + id);
		if (!data[0].equals("COMMITED")) {
			BaseUtil.showError("只能对已提交状态下的单据进行审核！");
		}
		int count = baseDao.getCountByCondition(logtable, "cl_caller='" + caller + "' and cl_statuscode = 'COMMITED' and cl_keyvalue="
				+ data[2] + " and cl_id<>" + id);
		if (count > 0) {
			BaseUtil.showError("[" + data[3] + "]只能存在一张已提交未审核的变更单");
		}
		handlerService.handler(caller, "audit", "before", new Object[] { id });
		Map<Object, Object> updatemap = formatData(caller, id);
		//变更业务员时同时更新客户资料里面的业务员信息
		if ("Customer$Change".equals(caller) || "Customer!Base$Change".equals(caller)) {
			if (updatemap.containsKey("cu_sellercode")) {
				Object cuid = updatemap.get("cu_id");
				Object sellercode = updatemap.get("cu_sellercode");
				if (StringUtil.hasText(sellercode)) {
					int i = baseDao.getCount("select count(*) from CustomerDistr where cd_cuid=" + cuid + " and cd_sellercode='"
							+ sellercode + "'");
					if (i == 0) {
						Object maxdetno = baseDao.getFieldDataByCondition("CustomerDistr", "max(cd_detno)", "cd_cuid=" + cuid);
						count = maxdetno == null ? 0 : Integer.valueOf(maxdetno.toString());
						baseDao.execute(
								"Insert into CustomerDistr(cd_id, cd_detno, cd_cuid, cd_sellercode, cd_seller, cd_custcode) values (?,?,?,?,?,?)",
								new Object[] { baseDao.getSeqId("CUSTOMERDISTR_SEQ"), count + 1, cuid, sellercode,
										updatemap.get("cu_sellername"), String.valueOf(data[3]) });
					}
				}
			}
			/**
			 * @author wsy
			 * 单据编号：2017031048
			 * 客户资料变更表中增加可以变更客服代表更新客户资料中的客服代表，并且变更了客服代表之后插入到客户分配表中去。
			 */
			if (updatemap.containsKey("cu_servicecode")) {
				Object cuid = updatemap.get("cu_id");
				Object cu_servicecode = updatemap.get("cu_servicecode");
				if (StringUtil.hasText(cu_servicecode)) {
					int i = baseDao.getCount("select count(*) from CustomerDistr where cd_cuid=" + cuid + " and cd_sellercode='"
							+ cu_servicecode + "'");
					if (i == 0) {
						Object maxdetno = baseDao.getFieldDataByCondition("CustomerDistr", "max(cd_detno)", "cd_cuid=" + cuid);
						count = maxdetno == null ? 0 : Integer.valueOf(maxdetno.toString());
						baseDao.execute(
								"Insert into CustomerDistr(cd_id, cd_detno, cd_cuid, cd_sellercode, cd_seller, cd_custcode) values (?,?,?,?,?,?)",
								new Object[] { baseDao.getSeqId("CUSTOMERDISTR_SEQ"), count + 1, cuid, cu_servicecode,
										updatemap.get("cu_servicename"), String.valueOf(data[3]) });
					}
				}
			}
		}
		if ("Vendor$Change".equals(caller)) {
			if (updatemap.containsKey("ve_uu")) {
				Object veid = updatemap.get("ve_id");
				Object ve_uu = updatemap.get("ve_uu");
				if (StringUtil.hasText(ve_uu)) {
					String veuu = baseDao.getFieldValue("Vendor", "ve_uu", "ve_id=" + veid, String.class);
					if (!ve_uu.equals(veuu)) {
						int i = baseDao.getCount("select count(*) from purchase left join Vendor on pu_vendcode=ve_code where ve_id="
								+ veid + " and pu_sendstatus='已上传'");
						if (i > 0) {
							BaseUtil.showError("存在已上传的采购单，不允许变更供应商UU号！");
						}
						i = baseDao.getCount("select count(*) from inquiry left join Vendor on in_vendcode=ve_code where ve_id=" + veid
								+ " and in_sendstatus='已上传'");
						if (i > 0) {
							BaseUtil.showError("存在已上传的询价单，不允许变更供应商UU号！");
						}
					}
				}
			}
			
			if (updatemap.containsKey("ve_name")) {
				Object ve_name = updatemap.get("ve_name");
				// 检查供应商名称名称前后是否有空格
				String patternAll = "^\\s+.*\\s+$";
				String patternBefore = "^\\s+.*$";
				String patternAfter = "^.*\\s+$";
				if (ve_name.toString().matches(patternAll) || ve_name.toString().matches(patternBefore) || ve_name.toString().matches(patternAfter)) {
					//去除供应商名称的前后空格
					updatemap.put("ve_name", ve_name.toString().trim());
				}
			}
		}
		Form form = formDao.getForm(caller, SpObserver.getSp());
		Object table = baseDao.getFieldDataByCondition("formdetail", "fd_table",
				"fd_logictype='changeKeyField' and fd_foid=" + form.getFo_id());
		String upperTable = String.valueOf(table).toUpperCase();
		Object[] ob=null;//物料资料变更，变更物料名称规格且变更的物料上传状态为已上传的，更新物料上传状态为待上传
		/**
		 * @author wsy
		 * 物料资料变更 修改物料资料上传状态
		 * 
		 */
		if(upperTable.contains("PRODUCT")){
			ob=baseDao.getFieldsDataByCondition("Product", new String[]{"pr_sendstatus","pr_code"},"pr_id="+updatemap.get("pr_id"));
			if(ob!=null&&"已上传".equals(ob[0])&&ob[1]!=null&&!"".equals(ob[1])){
				baseDao.updateByCondition("Product","pr_sendstatus='待上传' ","pr_id="+updatemap.get("pr_id")+" and nvl(pr_groupcode,' ')<>'用品'");
			}
		}
		/**物料基础资料变更，更新物料上传状态 
		 * 2017/12/27 wuyx 
		 * */
		if ("Product!Base$Change".equals(caller)) {
			if(upperTable.contains("PRODUCT")){
				ob=baseDao.getFieldsDataByCondition("Product", new String[]{"pr_b2csendstatus","pr_code"},"pr_id="+updatemap.get("pr_id"));
				if(ob!=null&&ob[1]!=null&&!"".equals(ob[1])){
					baseDao.updateByCondition("Product","pr_b2csendstatus='' ","pr_id="+updatemap.get("pr_id"));
				}
			}
		}
		/**物料采购资料变更，更新物料上传状态 
		 * 2017/12/27 wuyx 
		 * */
		if ("Product!Purchase$Change".equals(caller)) {
			if(upperTable.contains("PRODUCT")){
				ob=baseDao.getFieldsDataByCondition("Product", new String[]{"pr_b2csendstatus","pr_code"},"pr_id="+updatemap.get("pr_id"));
				if(ob!=null&&ob[1]!=null&&!"".equals(ob[1])){
					baseDao.updateByCondition("Product","pr_b2csendstatus='',pr_b2cinitproddtstatus='',pr_b2cinitstatus=''","pr_id="+updatemap.get("pr_id"));
				}
			}
		}
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(updatemap, upperTable, String.valueOf(data[1])));
		baseDao.updateByCondition(logtable, "cl_status='已审核',cl_statuscode='AUDITED',cl_auditman='" + employee.getEm_name()
				+ "',cl_auditdate=sysdate", "cl_id=" + id);
		updateOriginalInfo(caller,updatemap);
		handlerService.handler(caller, "audit", "after", new Object[] { id });
		//供应商资料变更
		if("Vendor$Change".equals(caller)){
			String sql = "update vendor a set ve_apvendname=(select ve_name from vendor b where a.ve_apvendcode=b.ve_code) where ve_apvendcode ='"+updatemap.get("ve_apvendcode")+"'";
			baseDao.execute(sql);
		}
		//客户资料变更
		if("Customer$Change".equals(caller) || "Customer!Base$Change".equals(caller)){
			List<String> list = new ArrayList<String>();
			String sql1 = "update customer a set cu_arname=(select cu_name from customer b where a.cu_arcode=b.cu_code) where cu_arcode ='"+updatemap.get("cu_arcode")+"'";
			String sql2 = "update customer a set cu_shcustname=(select cu_name from customer b where a.cu_shcustcode=b.cu_code) where cu_shcustcode ='"+updatemap.get("cu_shcustcode")+"'";
			list.add(sql1);
			list.add(sql2);
			baseDao.execute(list);
		}
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.audit"), BaseUtil
				.getLocalMessage("msg.auditSuccess"), caller + "|" + logkeyfield + "=" + id));
	}

	@Override
	public void resAuditCommonChange(String caller, int id) {
		// TODO Auto-generated method stub

	}

	private Map<Object, Object> formatData(String caller, int keyValue) {
		Object[] data = baseDao.getFieldsDataByCondition(logtable, new String[] { "cl_data", "cl_keyfield", "cl_keyvalue" }, logkeyfield
				+ "=" + keyValue);
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(String.valueOf(data[0]));
		Map<Object, Object> updatemap = new HashMap<Object, Object>();
		String[] fields = null;
		SqlRowList sl = baseDao
				.queryForRowSet("select  wmsys.wm_concat(fd_field) from formdetail  left join form on fd_foid=fo_id where fo_caller='"
						+ caller
						+ "' and upper(nvl(fd_table,' '))<>'COMMONCHANGELOG' and nvl(fd_logictype,' ')<>'changeCodeField' and nvl(fd_logictype,' ')<>'changeKeyField'");
		if (sl.next()) {
			fields = sl.getString(1).split(",");
		}
		for (String field : fields) {
			updatemap.put(field, map.get(field + "-new"));
		}
		updatemap.put(data[1], data[2]);
		return updatemap;
	}

	@Override
	public void updateChangetype(String caller,int id) {   
		Employee employee = SystemSession.getUser();
		String _master=employee!=null?employee.getEm_master():SpObserver.getSp();
		Object[] ob=baseDao.getFieldsDataByCondition("Form", new String[]{"fo_table","fo_keyfield","fo_detailtable","fo_detailmainkeyfield"}, "fo_caller='"+caller+"'");
		String formtable=ob[0]==null?"":ob[0].toString().split(" ")[0];//主表表名
		String keyfield=ob[1]==null?"":ob[1].toString();
		String gridtable=ob[2]==null?"":ob[2].toString().split(" ")[0];//明细表表名
		String detailmainkeyfield=ob[3]==null?"":ob[3].toString();
		Map<Object, Object> formdata=new HashMap<Object, Object>();//主表数据
		List<Map<Object, Object>> griddata = new ArrayList<Map<Object,Object>>();//从表数据
		/**获取变更设置 */
		List<Object[]> changetype=baseDao.getFieldsDatasByCondition("changetypedet left join changetype on ctd_ctid=ct_id", 
				new String[]{"ctd_value","ctd_type","ctd_fields","ct_field"}, "ct_caller='"+caller+"' order by ctd_priority");
		if(changetype.size()>0){
			if(caller.endsWith("$Change")){//公共变更单 
				formdata=BaseUtil.parseFormStoreToMap(baseDao.getFieldDataByCondition(ob[0].toString(), "cl_data", keyfield+"="+id).toString());
				formtable="commonchangelog";
			}else{
				if(!"".equals(formtable)&&!"".equals(keyfield)){
					Form form = formDao.getForm(caller, SpObserver.getSp());
					formdata= BaseUtil.parseFormStoreToMap(baseDao.getFormData(form, keyfield+"="+id).toString());
				}
				if(!"".equals(gridtable)&&!"".equals(detailmainkeyfield)){
					List<DetailGrid> detailGrids = detailGridDao.getDetailGridsByCaller(caller, _master);
					griddata=BaseUtil.parseGridStoreToMaps(baseDao.getDataStringByDetailGrid(detailGrids, detailmainkeyfield+"="+id, null, null));
				}
			}
			if(!"".equals(formtable)&&!"".equals(keyfield)){
				String typefield=changetype.get(0)[3].toString();//记录变更字段
				boolean flag = true;
				for(int i=0;i<changetype.size() && flag;i++){
					Object[] type=changetype.get(i);
					String [] fieldsArr=type[2].toString().split(";");//比较字段
					String typeValue=type[0].toString();//变更类型值
					if("FORM".equals(type[1].toString())){//变更字段为主表字段
						for(String fields:fieldsArr){
							String[] fieldArr=fields.split(",");
							if(formdata.get(fieldArr[0])!=null&&formdata.get(fieldArr[1])!=null&&!formdata.get(fieldArr[0]).toString().equals(formdata.get(fieldArr[1]))){
								baseDao.updateByCondition(formtable,typefield+"='"+typeValue+"'",keyfield+"="+id);
								if(caller.endsWith("$Change")){//更新 commonchangelog cl_data
									formdata.put(typefield, typeValue);
									baseDao.execute("update commonchangelog set cl_data='"+JSONObject.fromObject(formdata).toString()+"' where cl_id="+id);
								}
								flag=false;
								break;
							}
						}
					}else if("GRID".equals(type[1].toString())){//变更字段为明细字段
						for(int m=0;m<fieldsArr.length&& flag;m++){
							String[] fieldArr=fieldsArr[m].split(",");
							for(Map<Object, Object> gdata:griddata){
								if(gdata.get(fieldArr[0])!=null&&gdata.get(fieldArr[1])!=null&&!gdata.get(fieldArr[0]).toString().equals(gdata.get(fieldArr[1]).toString())){
									baseDao.updateByCondition(formtable,typefield+"='"+typeValue+"'",keyfield+"="+id);
									flag=false;
									break;
								}
							}
						}
					}
				}
				if(flag){//caller有设置变更类型，但未找到对应的变更类型值，设置为其它变更
					baseDao.updateByCondition(formtable,typefield+"='其它变更'",keyfield+"="+id);
				}
			}
		}
	}	
	private void updateOriginalInfo(String caller,Map<Object, Object> store){
		if(caller.equals("Vendor$Change")){
			baseDao.updateByCondition("Vendor","ve_updatedate=sysdate ", "ve_id="+store.get("ve_id"));	
		}else if(caller.equals("Customer$Change")){
			baseDao.updateByCondition("Customer","cu_updatedate=sysdate ", "cu_id="+store.get("cu_id"));	
		}else if(caller.equals("Product!Base$Change")||caller.equals("Product!Check$Change")||caller.equals("Product!Feature$Change")||
				caller.equals("Product!Finance$Change")||caller.equals("Product!Plan$Change")||caller.equals("Product!Purchase$Change")||
				caller.equals("Product!Reserve$Change")||caller.equals("Product!Sale$Change")){
			baseDao.updateByCondition("Product","pr_updatedate=sysdate ", "pr_id="+store.get("pr_id"));
		}
	}
	/*
	 *物料关键属性的值有效性校验
	 */
	private void checkProduct(Map<Object, Object> updatemap){
		List<String> manutype = new ArrayList<String>(Arrays.asList("PURCHASE","MAKE","OSMAKE", "CUSTOFFER"));//生产类型
		List<String> dhzc = new ArrayList<String>(Arrays.asList("MPS","MRP","OTH"));//计划类型
		List<String> supplytype = new ArrayList<String>(Arrays.asList("PUSH","PULL","VIRTUAL"));//供应类型
		List<String> acceptmethod=new ArrayList<String>(Arrays.asList("0","1","检验","不检验"));//接收方式
		if(updatemap.get("pr_manutype")!=null&&!"".equals(updatemap.get("pr_manutype"))
				&&!manutype.contains(updatemap.get("pr_manutype"))){
			BaseUtil.showError("生产类型 数据("+updatemap.get("pr_manutype")+")不存在");
		}
		if(updatemap.get("pr_dhzc")!=null&&!"".equals(updatemap.get("pr_dhzc"))
				&&!dhzc.contains(updatemap.get("pr_dhzc"))){
			BaseUtil.showError("计划类型 数据("+updatemap.get("pr_dhzc")+")不存在");
		}
		if(updatemap.get("pr_supplytype")!=null&&!"".equals(updatemap.get("pr_supplytype"))
				&&!supplytype.contains(updatemap.get("pr_supplytype"))){
			BaseUtil.showError("供应类型 数据("+updatemap.get("pr_supplytype")+")不存在");
		}
		if(updatemap.get("pr_acceptmethod")!=null&&!"".equals(updatemap.get("pr_acceptmethod"))
				&&!acceptmethod.contains(updatemap.get("pr_acceptmethod"))){
			BaseUtil.showError("接收方式 数据("+updatemap.get("pr_acceptmethod")+")不存在");
		}
	};	
	}
