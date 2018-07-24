package com.uas.erp.service.common.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.common.CommonService;
import com.uas.erp.service.common.DocumentSetService;
import com.uas.erp.service.oa.DocumentListService;

@Service
public class DocumentSetServiceImpl implements DocumentSetService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private DocumentListService documentListService;
	
	/**
	 * 保存
	 * @param formStore
	 * @param caller
	 */
	@Override
	public void saveDocSetting(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		boolean bool = baseDao.checkByCondition("documentset", "ds_code='" + store.get("ds_code") + "'");
		if (!bool) {
			BaseUtil.showError("归档编号已存在!");
		}
		String condition = String.valueOf(store.get("ds_condition")).trim();
		if("".equals(condition) || "null".equals(condition)){
			int ruleCount = baseDao.getCount("select count(*) from documentset where ds_caller='"+store.get("ds_caller")+"' and ds_attachfield='"+store.get("ds_attachfield")+"'"); 
			if(ruleCount > 0){
				BaseUtil.showError("已存在Caller、附件字段、条件三者不为空的规则,不允许新增Caller、附件字段相同且条件为空的规则!");
			}
		}
		int count = baseDao.getCount("select count(*) from documentset where ds_caller='"+store.get("ds_caller")+"' and ds_attachfield='"+store.get("ds_attachfield")+"' and ds_condition=''");
		if(count > 0){
			BaseUtil.showError("已存在Caller、附件字段相同且条件为空的规则,不允许新增Caller、附件字段相同的其他规则!");
		}
		String sql = "select count(*) from documentset where ds_caller='" + store.get("ds_caller") + "' and DS_ATTACHFIELD = '" + store.get("ds_attachfield") + "' and ds_condition='"+store.get("ds_condition")+"'";
		count = baseDao.getCount(sql);
		if(count > 0){
			BaseUtil.showError("已存在Caller、附件字段、条件三者相同的规则,请重新设置!");
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store });
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "documentset", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}
	
	/**
	 * 删除
	 * @param ds_id
	 * @param caller
	 */
	@Override
	public void deleteDocSetting(int ds_id, String caller) {
		// 只能删除在录入的归档规则!
		Object status = baseDao.getFieldDataByCondition("documentset", "ds_statuscode", "ds_id=" + ds_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { ds_id });
		baseDao.delCheck("documentset", ds_id);
		// 删除
		baseDao.deleteById("documentset", "ds_id", ds_id);
		// 记录操作
		baseDao.logger.delete(caller, "ds_id", ds_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { ds_id });
	}
	
	/**
	 * 修改
 	 * @param formStore
	 * @param caller
	 */
	@Override
	public void updateDocSettingById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改在录入的归档规则!
		Object status = baseDao.getFieldDataByCondition("documentset", "ds_statuscode", "ds_id=" + store.get("ds_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store });
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "documentset", "ds_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "ds_id", store.get("ds_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}
	
	/**
	 * 提交
 	 * @param ds_id
	 * @param caller
	 */
	@Override
	public void submitDocSetting(int ds_id, String caller) {
		// 只能对状态为[在录入]的归档规则进行提交操作!
		Object status = baseDao.getFieldDataByCondition("documentset", "ds_statuscode", "ds_id=" + ds_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { ds_id });
		// 执行提交操作
		baseDao.submit("documentset", "ds_id=" + ds_id, "ds_status", "ds_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "ds_id", ds_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { ds_id });
	}
	
	/**
	 * 反提交
 	 * @param ds_id
	 * @param caller
	 */
	@Override
	public void resSubmitDocSetting(int ds_id, String caller) {
		// 只能对状态为[已提交]的归档规则进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("documentset", "ds_statuscode", "ds_id=" + ds_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { ds_id });
		// 执行反提交操作
		baseDao.resOperate("documentset", "ds_id=" + ds_id, "ds_status", "ds_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ds_id", ds_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { ds_id });
	}
	
	/**
	 * 审核
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void auditDocSetting(int dsId, String caller) {
		// 只能对状态为[已提交]的归档规则进行审核操作!
		Object status = baseDao.getFieldDataByCondition("documentset", "ds_statuscode", "ds_id=" + dsId);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { dsId });
		// 执行审核操作
		baseDao.audit("documentset", "ds_id=" + dsId, "ds_status", "ds_statuscode", "ds_auditdate", "ds_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "ds_id", dsId);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { dsId });
		String foCaller = (String) baseDao.getFieldDataByCondition("documentset", "ds_caller", "ds_id= " + dsId);//通过ds_id查找规则对应的fo_caller
		//若启用历史归档，通过fo_caller将所有附件归档。
		try {
			historyDocumentManage(foCaller, dsId);
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * 反审核
	 */
	@Override
	public void resAuditDocSetting(int dsId, String caller) {
		// 只能对状态为[已审核]的采购单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("documentset", "ds_statuscode", "ds_id=" + dsId);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, new Object[] { dsId });
		baseDao.resAuditCheck("documentset", dsId);
		// 执行反审核操作
		baseDao.resAudit("documentset", "ds_id=" + dsId, "ds_status", "ds_statuscode", "ds_auditdate", "ds_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "ds_id", dsId);
		handlerService.afterResAudit(caller, new Object[] { dsId });
	}
	
	/**
	 * 审核之后，单据文件归档
	 */
	public boolean documentManage(int id, String caller){
		try {
			//该caller是否已设置归档规则且已启用
			String getCountSql = "select count(*) from documentset where ds_caller = '" + caller + "' and ds_isuse = 1";
			int count = baseDao.getCount(getCountSql);
			if(count > 0){
				//通过caller查找表名和fo_id
				Object[] fieldsValue = baseDao.getFieldsDataByCondition("form", new String[] { "fo_id", "fo_table" }, "fo_caller= '" + caller + "'");
				List<Object> dsAttach = baseDao.getFieldDatasByCondition("documentset", "ds_attachfield", "ds_caller = '" + caller + "'");
				for(Object obj: dsAttach){
					//查找附件的字段名
					String getAttachFieldSql = "select fd_field from formdetail where fd_type='MF' and fd_foid= " + fieldsValue[0] + " ";
					List<Map<String,Object>> attachFieldList = baseDao.queryForList(getAttachFieldSql);
					if(attachFieldList != null && !attachFieldList.isEmpty()){
						for(int i = 0; i < attachFieldList.size(); i++){	//若存在多个附件字段
							String attachField = String.valueOf(attachFieldList.get(i).get("fd_field"));
							if(obj.toString().equals(attachField)){
								//查找设置的ds_condition
								String condition = String.valueOf(baseDao.getFieldDataByCondition("documentset", "ds_condition", "ds_caller='"+caller+"' and ds_attachfield='"+attachField+"'"));
								//查找单据设置的文档标签
								String labels = getLabel("documentset", caller, attachField);
								//查找表的主键名
								String getPrimaryKeySql = "select fo_keyfield from form where fo_id= " + fieldsValue[0];
								List<Map<String,Object>> primaryKeyList = baseDao.queryForList(getPrimaryKeySql);
								if(primaryKeyList != null && !primaryKeyList.isEmpty()){
									String primaryKey = String.valueOf(primaryKeyList.get(0).get("fo_keyfield"));
									String labelInfo = "";
									if(labels != null){
										String[] label = labels.split("#");
										//构造sql语句
										StringBuilder sb = new StringBuilder();
										sb.append("select fd_caption,fd_field ");
										
										sb.append(" from (select * from formdetail where fd_foid = " + fieldsValue[0] + " and fd_field in (");
										for(int k = 0; k < label.length; k++){
											if(k == label.length-1){
												sb.append("'"+ label[k] + "'");
											}else{
												sb.append("'"+ label[k] + "',");
											}
										}
										sb.append(")), " + fieldsValue[1] + " where " + primaryKey + "= " + id);
										List<Map<String,Object>> labelInfoList = baseDao.queryForList(sb.toString());
										StringBuilder sb2 = new StringBuilder();
										sb2.append("select ");
										for(int j = 0; j < label.length; j ++){
											if(j != label.length - 1){
												sb2.append(label[j] + ",");
											}else{
												sb2.append(label[j]);
											}
										}
										sb2.append(" from " + (String) fieldsValue[1] + " where " + primaryKey + " = " + id);
										List<Map<String,Object>> valueList = baseDao.queryForList(sb2.toString());
										if(valueList != null && !valueList.isEmpty()){
											StringBuilder sb3 = new StringBuilder();
											for(Map<String,Object> map : labelInfoList){
												sb3.append(map.get("FD_CAPTION") + " : " );
												Object value = valueList.get(0).get(map.get("FD_FIELD").toString().toUpperCase());
												if(value == null || "".equals(value)){
													sb3.append("[空值],");
												}else{
													sb3.append(value.toString() + ",");
												}
												/*String value = (String) valueList.get(0).get(map.get("FD_FIELD").toString().toUpperCase());
												SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
												if(value == null || "".equals(value)){
													sb3.append(" ,");
												}else{
													try {
														Date dateValue = fmt.parse(value);
														sb3.append(dateValue + ",");
													} catch (ParseException e) {
														sb3.append(value + ",");
													}
												}*/
											}
										labelInfo = sb3.substring(0, sb3.length()-1).toString();
										}
										
									}
									
									//查找单据的附件
									String getAttachIdSql = "select "+attachField+" from " + (String) fieldsValue[1] + " where " + primaryKey + "=" + id + " ";
									if(!"".equals(condition) && !"null".equals(condition) && condition != null){
										getAttachIdSql += " and " + condition;
									}
									List<Map<String,Object>> attachIdList = baseDao.queryForList(getAttachIdSql);
									//如果存在附件
									if(attachIdList != null && !attachIdList.isEmpty()){
										String attachId = String.valueOf(attachIdList.get(0).get(attachField));
										if(attachId != null && !"".equals(attachId)){
											String[] ids = attachId.split(";");
											for(String fpid : ids){
												String formStore = generateJson(caller,fpid,labelInfo,attachField,id);
												documentListService.save(caller, formStore);
											}
										}
										
									}// end if(attachIdList)
								}// end if(primaryKeyList)
							}
						}
					} // end if(attachFieldList)
				}
			}//end if(count>0)
		} catch (Exception e) {
			return false;
		}
		return true;
		
	}
	private String generateJson(String caller,String fpid,String labelInfo,String attachField,int valueId){
		int dl_id = baseDao.getSeqId("DocumentList_SEQ");
		//获取前缀码
		String prefixCode = String.valueOf(baseDao.getFieldDataByCondition("documentset", "ds_prefixcode", "ds_caller='" + caller +"' and ds_attachfield = '" + attachField + "'"));
		//通过前缀码获取目录所在的位置
		int id = 0;
		if(prefixCode.contains("null") || !prefixCode.contains("-")){
			String directory = String.valueOf(baseDao.getFieldDataByCondition("documentset", "ds_directory", "ds_caller='"+caller+"' and ds_attachfield='"+attachField+"'"));
			String[] directorys = directory.split("/");
			for(int j = 0; j < directorys.length; j++){
				id = Integer.parseInt(String.valueOf(baseDao.getFieldDataByCondition("documentlist", "dl_id", "dl_parentId="+id+" and dl_name='"+directorys[j]+"' and dl_kind=-1 and dl_statuscode='AUDITED'")));
			}
		}else{
			String[] codes = prefixCode.split("-");
			for(int i = 0; i < codes.length; i++){
				id = Integer.parseInt(String.valueOf(baseDao.getFieldDataByCondition("documentlist", "dl_id", "dl_parentid = " + id + " and dl_prefixcode='" +codes[i]+ "'")));
			}
		}
		int dl_parentid = id;
		String dl_style = "";
		int dl_kind = 0;
		String dl_status = "在录入";
		String dl_statuscode = "ENTERING";
		//构造formStore
		JSONObject formStore = new JSONObject();
		formStore.put("dl_id", dl_id);
		formStore.put("dl_virtualpath", "");
		formStore.put("dl_code", prefixCode);
		formStore.put("dl_fpid", fpid+";");
		formStore.put("dl_remark", "");
		formStore.put("dl_createtime", "");
		formStore.put("dl_creator", "");
		formStore.put("dl_parentid", dl_parentid);
		formStore.put("dl_style", dl_style);
		formStore.put("dl_kind", dl_kind);
		formStore.put("dl_status", dl_status);
		formStore.put("dl_statuscode", dl_statuscode);
		formStore.put("dl_creator",SystemSession.getUser().getEm_name());
		formStore.put("dl_createtime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		formStore.put("dl_labelinfo",labelInfo);
		if(valueId != -1){
			String url = (String) getURL(caller,valueId).get("dl_linked");
			formStore.put("dl_linked", url);
		}
		formStore.put("dl_displayname", (String)getURL(caller,valueId).get("dl_displayname"));
		return formStore.toJSONString();
		
	}
	
	/**
	 * 历史归档
	 */
	private void historyDocumentManage(String caller,int dsId){
		//是否启用历史归档
		//DS_ISHISTORYUSE
		String getCountSql = "select count(*) from documentset where ds_caller = '" + caller + "' and ds_isuse = 1 and ds_ishistoryuse = 1";
		int count = baseDao.getCount(getCountSql);
		if(count > 0){
			Object[] fieldsValue = baseDao.getFieldsDataByCondition("form", new String[] { "fo_id", "fo_table" }, "fo_caller= '" + caller + "'");
			List<Object> dsAttach = baseDao.getFieldDatasByCondition("documentset", "ds_attachfield", "ds_caller = '" + caller + "'");
			int primaryKeyValue = -1;
			for(Object obj: dsAttach){
				//查找附件的字段名
				String getAttachFieldSql = "select fd_field from formdetail where fd_type='MF' and fd_foid= " + fieldsValue[0] + " ";
				List<Map<String,Object>> attachFieldList = baseDao.queryForList(getAttachFieldSql);
				if(attachFieldList != null && !attachFieldList.isEmpty()){
					for(int i = 0; i < attachFieldList.size(); i++){	//若存在多个附件字段
						String attachField = String.valueOf(attachFieldList.get(i).get("fd_field"));
						if(obj.toString().equals(attachField)){
							//查找单据设置的文档标签
							String labels = getLabel("documentset", caller, attachField);
							//查找该caller下的所有附件
							String sql = "select "+attachField+" from " + (String)fieldsValue[1] + " where " + attachField + " is not null and pu_statuscode = 'AUDITED'"; 
							List<Map<String,Object>> attachValuesList = baseDao.queryForList(sql);
							for(Map<String,Object> map : attachValuesList){
								String attach = String.valueOf(map.get(attachField));
								String labelInfo = "";
								
								//查找表的主键名
								String getPrimaryKeySql = "select fo_keyfield from form where fo_id= " + fieldsValue[0];
								List<Map<String,Object>> primaryKeyList = baseDao.queryForList(getPrimaryKeySql);
								if(primaryKeyList != null && !primaryKeyList.isEmpty()){
									String primaryKey = String.valueOf(primaryKeyList.get(0).get("fo_keyfield"));
									primaryKeyValue = Integer.parseInt(primaryKey);
								}
								
								
								if(labels != null){
									String[] label = labels.split("#");
									//构造sql语句
									StringBuilder sb = new StringBuilder();
									sb.append("select fd_caption,fd_field ");
									
									sb.append(" from (select * from formdetail where fd_foid = " + fieldsValue[0] + " and fd_field in (");
									for(int k = 0; k < label.length; k++){
										if(k == label.length-1){
											sb.append("'"+ label[k] + "'");
										}else{
											sb.append("'"+ label[k] + "',");
										}
									}
									sb.append(")), " + fieldsValue[1] + " where " + attachField + "= '" + attach + "'");
									List<Map<String,Object>> labelInfoList = baseDao.queryForList(sb.toString());
									StringBuilder sb2 = new StringBuilder();
									sb2.append("select ");
									for(int j = 0; j < label.length; j ++){
										if(j != label.length - 1){
											sb2.append(label[j] + ",");
										}else{
											sb2.append(label[j]);
										}
									}
									sb2.append(" from " + (String) fieldsValue[1] + " where " + attachField + " = '" + attach + "'");
									List<Map<String,Object>> valueList = baseDao.queryForList(sb2.toString());
									if(valueList != null && !valueList.isEmpty()){
										StringBuilder sb3 = new StringBuilder();
										for(Map<String,Object> map1 : labelInfoList){
											sb3.append(map1.get("FD_CAPTION") + " : " );
											String value = String.valueOf(valueList.get(0).get(map1.get("FD_FIELD").toString().toUpperCase()));
											if(value == null || "".equals(value) || "null".equalsIgnoreCase(value)){
												sb3.append("[空值],");
											}else{
												sb3.append(value + ",");
											}
										}
										labelInfo = sb3.substring(0, sb3.length()-1).toString();
									}
									
								}
								String[] attachs = attach.split(";");
								for(String attachValue : attachs){
									//归档
									String formStore = generateJson(caller,attachValue,labelInfo,attachField,primaryKeyValue);
									documentListService.save(caller, formStore);
								}
							}
						}
					}
					
				}
			}
			
		}
		
	}
	
	/**
	 * 单据反审核前，判断该单据是否存在已归档文件。若存在，给出错误提示。
	 * @param caller
	 * @param id
	 */
	public boolean beforeResAudit(String caller, int id){
		try {
			//通过caller查找表名和fo_id
			Object[] fieldsValue = baseDao.getFieldsDataByCondition("form", new String[] { "fo_id", "fo_table" }, "fo_caller= '" + caller + "'");
			//查找附件的字段名
			String getAttachFieldSql = "select fd_field from formdetail where fd_type='MF' and fd_foid= " + fieldsValue[0] + " ";
			List<Map<String,Object>> attachFieldList = baseDao.queryForList(getAttachFieldSql);
			if(attachFieldList != null && !attachFieldList.isEmpty()){
				String attachField = String.valueOf(attachFieldList.get(0).get("fd_field"));		
				//查找表的主键名
				String getPrimaryKeySql = "select fo_keyfield from form where fo_id= " + fieldsValue[0];
				List<Map<String,Object>> primaryKeyList = baseDao.queryForList(getPrimaryKeySql);
				if(primaryKeyList != null && !primaryKeyList.isEmpty()){
					String primaryKey = String.valueOf(primaryKeyList.get(0).get("fo_keyfield"));
					String getAttachValueSql = "select " + attachField + " from " + (String) fieldsValue[1] + " where " + primaryKey + "= " + id;
					List<Map<String,Object>> AttachValueList = baseDao.queryForList(getAttachValueSql);
					if(AttachValueList != null && !AttachValueList.isEmpty()){
						String attachValue = String.valueOf(AttachValueList.get(0).get(attachField));
						if(attachValue != null && !"".equals(attachValue)){
							String[] attachs = attachValue.split(";");
							int count = 0;
							for(String attach : attachs){
								String sql = "select count(*) from documentlist where dl_fpid = '" + attach + "'";
								count = count + baseDao.getCount(sql);
							}
							if(count > 0){
								BaseUtil.showErrorOnSuccess("该单据存在已归档文件，请前往文档管理删除！");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	private String getLabel(String table, String caller, String attachField){
		String getLabelSql = "select ds_label from " + table + " where ds_caller = '" + caller + "' and ds_attachfield = '" + attachField + "'";
		List<Map<String,Object>> list = baseDao.queryForList(getLabelSql);
		if(list != null && !list.isEmpty()){
			String label = String.valueOf(list.get(0).get("ds_label"));
			return label;
		}
		return null;
	}
	
	private Map<String,Object> getURL(String caller, int id){
		Map<String,Object> map = new HashMap<String, Object>();
		//构造单据链接
		StringBuilder url = new StringBuilder();
		String sql = "SELECT SN_URL,SN_ADDURL,SN_DISPLAYNAME FROM SysNavigation WHERE SN_CALLER = '"+caller+"' AND NOT instr(SN_URL,'datalist.jsp') > 0 GROUP BY SN_URL,SN_ADDURL,SN_DISPLAYNAME";
		List<Map<String,Object>> list = baseDao.queryForList(sql);
		String firstString = String.valueOf(list.get(0).get("SN_URL"));
		int end = firstString.indexOf("?");
		if(end != -1){		//是否包含问号
//			url.append(firstString.substring(0,end));
			url.append(firstString + "&formCondition=");
		}else{
//			url.append(firstString);
			url.append(firstString + "?formCondition=");
		}
//		url.append("?formCondition=");
		Object keyField = baseDao.getFieldDataByCondition("FORM", "fo_keyfield", "FO_CALLER = '" + caller + "'");
		url.append(keyField + "IS" + id + "&gridCondition=");
		String getMainFieldSql = "SELECT dg_field FROM DETAILGRID WHERE DG_CALLER = '"+caller+"' and dg_logictype = 'mainField'";
		List<Map<String,Object>> mainFieldList = baseDao.queryForList(getMainFieldSql);
		if(mainFieldList != null && !mainFieldList.isEmpty()){
			String mainField = String.valueOf(mainFieldList.get(0).get("DG_FIELD"));
			url.append(mainField + "IS" + id);
		}else{
			url.append("nullIS" + id);
		}
		map.put("dl_linked", url.toString());
		String displayname = String.valueOf(list.get(0).get("SN_DISPLAYNAME"));
		if(displayname.contains("(")){
			map.put("dl_displayname", displayname.substring(0,displayname.indexOf("(")));
		}else{
			map.put("dl_displayname", displayname);
		}
		return map;
	}
	
}
