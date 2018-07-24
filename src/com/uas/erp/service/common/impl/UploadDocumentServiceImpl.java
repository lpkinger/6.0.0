package com.uas.erp.service.common.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.uas.erp.service.common.UploadDocumentService;
import com.uas.erp.service.oa.DocumentListService;

@Service
public class UploadDocumentServiceImpl implements UploadDocumentService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private DocumentListService documentListService;
	
	
	/**
	 * 保存
	 */
	public void saveUploadDocument(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("uploaddocument", "ud_code='" + store.get("ud_code") + "'");
		if (!bool) {
			BaseUtil.showError("当前编号的记录已经存在,不能新增!");
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		// 保存uploaddocument
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "uploaddocument", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		//保存uploaddocdetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "uploaddocdetail", "udd_id");
		for (Map<Object, Object> map : grid) {
			Object uddId = map.get("udd_id");
			if (uddId == null || uddId.equals("") || uddId.equals("0") || Integer.parseInt(uddId.toString()) == 0) {// 新添加的数据，id不存在
				baseDao.execute(SqlUtil.getInsertSql(map, "uploaddocdetail", "udd_id"));
			}
		}
		baseDao.execute(gridSql);
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, grid });
		
	}
	
	/**
	 * 删除
	 */
	public void deleteUploadDocument(int ud_id, String caller) {
		// 只能删除在录入!
		Object status = baseDao.getFieldDataByCondition("uploaddocument", "ud_statuscode", "ud_id=" + ud_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { ud_id });
		baseDao.delCheck("Purchase", ud_id);
		// 删除uploaddocdetail
		String deleteSql = "delete from uploaddocdetail where udd_udid = '" +ud_id + "'";
		baseDao.execute(deleteSql);
		// 删除uploaddocument
		baseDao.deleteById("uploaddocument", "ud_id", ud_id);
		// 记录操作
		baseDao.logger.delete(caller, "ud_id", ud_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { ud_id });
		//删除已上传的文件?
	}
	
	/**
	 * 更新
	 */
	public void updateUploadDocument(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("uploaddocument", "ud_statuscode", "ud_id=" + store.get("ud_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		// 修改uploaddocument
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "uploaddocument", "ud_id");
		baseDao.execute(formSql);
		
		// 修改uploaddocdetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "uploaddocdetail", "udd_id");
		for (Map<Object, Object> map : gstore) {
			Object uddId = map.get("udd_id");
			if (uddId == null || uddId.equals("") || uddId.equals("0") || Integer.parseInt(uddId.toString()) == 0) {// 新添加的数据，id不存在
				baseDao.execute(SqlUtil.getInsertSql(map, "uploaddocdetail", "udd_id"));
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "ud_id", store.get("ud_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
		
	}
	
	/**
	 * 提交
	 */
	public void submitUploadDocumentById(int udId, String caller) {
		// 只能对状态为[在录入]的单据进行提交操作!
		Object status = baseDao.getFieldDataByCondition("uploaddocument", "ud_statuscode", "ud_id=" + udId);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { udId });
		// 执行提交操作
		baseDao.submit("uploaddocument", "ud_id=" + udId, "ud_status", "ud_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "ds_id", udId);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { udId });
	}
	
	/**
	 * 反提交
	 */
	public void resSubmitUploadDocument(int udId, String caller) {
		// 只能对状态为[已提交]的单据进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("uploaddocument", "ud_statuscode", "ud_id=" + udId);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { udId });
		// 执行反提交操作
		baseDao.resOperate("uploaddocument", "ud_id=" + udId, "ud_status", "ud_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ud_id", udId);
		handlerService.handler(caller, "resCommit", "after", new Object[] { udId });
	}
	
	/**
	 * 审核
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void auditUploadDocument(int udId, String caller) {
		// 只能对状态为[已提交]的单据进行审核操作!
		Object status = baseDao.getFieldDataByCondition("uploaddocument", "ud_statuscode", "ud_id=" + udId);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { udId });
		// 执行审核操作
		baseDao.audit("uploaddocument", "ud_id=" + udId, "ud_status", "ud_statuscode", "ud_auditdate", "ud_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "ud_id", udId);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { udId });
		//对文件进行归档
		List<Object> uddCodeList = baseDao.getFieldDatasByCondition("uploaddocdetail", "udd_code", "udd_udid = " + udId);
		if(uddCodeList != null && !uddCodeList.isEmpty()){
			for(Object uddCode : uddCodeList){
				int start = uddCode.toString().lastIndexOf("-") + 1;
				String fpid = uddCode.toString().substring(start);
				String prefixCode = uddCode.toString().substring(0,start-1);
				String formStore = generateJson(caller,fpid,prefixCode,udId);
				documentListService.save(caller, formStore);
			}
		}
	}
	
	/**
	 * 反审核
	 */
	public void resAuditUploadDocument(int udId, String caller) {
		// 只能对状态为[已审核]的单据进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("uploaddocument", "ud_statuscode", "ud_id=" + udId);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, new Object[] { udId });
		baseDao.resAuditCheck("uploaddocument", udId);
		// 执行反审核操作
		baseDao.resAudit("uploaddocument", "ud_id=" + udId, "ud_status", "ud_statuscode", "ud_auditdate", "ud_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "ud_id", udId);
		handlerService.afterResAudit(caller, new Object[] { udId });
	}
	
	
	private String generateJson(String caller,String fpid,String prefixCode, int udId){
		int dl_id = baseDao.getSeqId("DocumentList_SEQ");
		int id = 0;
		if(prefixCode.contains("null") || !prefixCode.contains("-")){
			String directory = String.valueOf(baseDao.getFieldDataByCondition("uploadDocument", "ud_directory", "ud_id='"+udId+"'"));
			String[] directorys = directory.split("/");
			for(int j = 0; j < directorys.length; j++){
				id = Integer.parseInt(String.valueOf(baseDao.getFieldDataByCondition("documentlist", "dl_id", "dl_parentId="+id+" and dl_name='"+directorys[j]+"' and dl_kind=-1 and dl_statuscode='AUDITED'")));
			}
		}else{
			String[] codes = prefixCode.split("-");
			for(int i = 0; i < codes.length; i++){
				String sql = "select dl_id from documentlist where dl_statuscode = 'AUDITED' and dl_parentid = " + id + " and dl_prefixcode='" +codes[i]+ "'";
				List<Map<String,Object>> list = baseDao.queryForList(sql);
				if(list != null && !list.isEmpty()){
					id = Integer.parseInt(list.get(0).get("dl_id").toString());
				}
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
		formStore.put("dl_code", prefixCode);
		formStore.put("dl_virtualpath", "");
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
		return formStore.toJSONString();
	}
	
}
