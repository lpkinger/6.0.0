package com.uas.erp.service.fs.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.fs.LoadedPlanSetService;

@Service
public class LoadedPlanSetServiceImpl implements LoadedPlanSetService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void saveLoadedPlanSet(String formStore, String param, String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		
		//保存FSLOADEDPLANSET
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "FSLOADEDPLANSET"));
		
		//保存FSLOADEDPLANSETDETAIL
		for(Map<Object, Object> m : gstore){
			m.put("psd_id", baseDao.getSeqId("FSLOADEDPLANSETDETAIL_SEQ"));
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(gstore, "FSLOADEDPLANSETDETAIL"));
		
		addForm(store.get("ps_id"));
		
		baseDao.logger.save(caller, "ps_id", store.get("ps_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
	}

	@Override
	public void updateLoadedPlanSet(String formStore, String param,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		
		handlerService.handler(caller, "update", "before", new Object[] { store, gstore });
		
		//更新FSLOADEDPLANSET
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "FSLOADEDPLANSET", "ps_id"));
		
		//更新FSLOADEDPLANSETDETAIL
		baseDao.execute(SqlUtil.getInsertOrUpdateSqlbyGridStore(gstore, "FSLOADEDPLANSETDETAIL", "psd_id"));
		
		addForm(store.get("ps_id"));
		
		// 记录操作
		baseDao.logger.update(caller, "ps_id", store.get("ps_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "update", "after", new Object[] { store, gstore });
		
	}
	
	private void addForm(Object id){
		List<String> sqls = new ArrayList<String>();
		SqlRowList rs = baseDao.queryForRowSet("select psd_caller,psd_type from FSLOADEDPLANSETDETAIL where psd_psid = ? order by psd_detno", id);
		while (rs.next()) {
			boolean noExist = baseDao.checkByCondition("form", "fo_caller = '"+rs.getString("psd_caller")+"'");
			if (noExist) {
				int foid = baseDao.getSeqId("FORM_SEQ");
				sqls.add("Insert into FORM (FO_ID,FO_TABLE,FO_TITLE,FO_CALLER,FO_SEQ,FO_KEYFIELD,FO_BUTTON4ADD,FO_BUTTON4RW,FO_STATUSFIELD,FO_STATUSCODEFIELD,FO_RECORDERFIELD,FO_ISAUTOFLOW,FO_PAGETYPE) values ("+foid+",'FSLOADEDPLANTABLE','"+rs.getString("psd_type")+"','"+rs.getString("psd_caller")+"','FSLOADEDPLANTABLE_SEQ','pt_id','erpSaveButton','erpUpdateButton#erpSubmitButton#erpResSubmitButton#erpAuditButton#erpResAuditButton','pt_status','pt_statuscode','pt_recorder@N',0,'default')");
				sqls.add("Insert into FORMDETAIL (FD_ID,FD_FOID,FD_TABLE,FD_CAPTION,FD_FIELD,FD_TYPE,FD_READONLY,FD_ALLOWBLANK,FD_COLUMNWIDTH,FD_DEFAULTVALUE,FD_CAPTIONFAN,FD_CAPTIONEN,FD_DBFIND,FD_DETNO,FD_FIELDLENGTH,FD_ISNEEDENCRYPT,FD_CHECK,FD_MOBILEUSED,FD_MODIFY,FD_ISFIXED,FD_APPWIDTH) values (FORMDETAIL_SEQ.nextval,"+foid+",'FSLOADEDPLANTABLE','ID','pt_id','H','F','F',1,null,'ID','ID','F',1,120,0,0,0,'F',0,0)");
				sqls.add("Insert into FORMDETAIL (FD_ID,FD_FOID,FD_TABLE,FD_CAPTION,FD_FIELD,FD_TYPE,FD_READONLY,FD_ALLOWBLANK,FD_COLUMNWIDTH,FD_DEFAULTVALUE,FD_CAPTIONFAN,FD_CAPTIONEN,FD_DBFIND,FD_DETNO,FD_FIELDLENGTH,FD_ISNEEDENCRYPT,FD_CHECK,FD_MOBILEUSED,FD_MODIFY,FD_ISFIXED,FD_APPWIDTH) values (FORMDETAIL_SEQ.nextval,"+foid+",'FSLOADEDPLANTABLE','状态码','pt_statuscode','H','F','T',1,'ENTERING','状态码','状态码','F',2,100,0,0,0,'F',0,0)");
				sqls.add("Insert into FORMDETAIL (FD_ID,FD_FOID,FD_TABLE,FD_CAPTION,FD_FIELD,FD_TYPE,FD_READONLY,FD_ALLOWBLANK,FD_COLUMNWIDTH,FD_DEFAULTVALUE,FD_CAPTIONFAN,FD_CAPTIONEN,FD_DBFIND,FD_DETNO,FD_FIELDLENGTH,FD_ISNEEDENCRYPT,FD_CHECK,FD_MOBILEUSED,FD_MODIFY,FD_ISFIXED,FD_APPWIDTH) values (FORMDETAIL_SEQ.nextval,"+foid+",'FSLOADEDPLANTABLE','状态','pt_status','S','T','T',1,'getLocal(ENTERING)','状态','状态','F',3,100,0,0,0,'F',0,0)");
				sqls.add("Insert into FORMDETAIL (FD_ID,FD_FOID,FD_TABLE,FD_CAPTION,FD_FIELD,FD_TYPE,FD_READONLY,FD_ALLOWBLANK,FD_COLUMNWIDTH,FD_DEFAULTVALUE,FD_CAPTIONFAN,FD_CAPTIONEN,FD_DBFIND,FD_DETNO,FD_FIELDLENGTH,FD_ISNEEDENCRYPT,FD_CHECK,FD_MOBILEUSED,FD_MODIFY,FD_ISFIXED,FD_APPWIDTH) values (FORMDETAIL_SEQ.nextval,"+foid+",'FSLOADEDPLANTABLE','录入人','pt_recorder','S','F','T',1,'session:em_name','录入人','录入人','F',4,50,0,0,0,'F',0,0)");
				sqls.add("Insert into FORMDETAIL (FD_ID,FD_FOID,FD_TABLE,FD_CAPTION,FD_FIELD,FD_TYPE,FD_READONLY,FD_ALLOWBLANK,FD_COLUMNWIDTH,FD_DEFAULTVALUE,FD_CAPTIONFAN,FD_CAPTIONEN,FD_DBFIND,FD_DETNO,FD_FIELDLENGTH,FD_ISNEEDENCRYPT,FD_CHECK,FD_MOBILEUSED,FD_MODIFY,FD_ISFIXED,FD_APPWIDTH) values (FORMDETAIL_SEQ.nextval,"+foid+",'FSLOADEDPLANTABLE','录入日期','pt_recorddate','D','F','T',1,'getCurrentDate()','录入日期','录入日期','F',5,7,0,0,0,'F',0,0)");
				sqls.add("Insert into FORMDETAIL (FD_ID,FD_FOID,FD_TABLE,FD_CAPTION,FD_FIELD,FD_TYPE,FD_READONLY,FD_ALLOWBLANK,FD_COLUMNWIDTH,FD_DEFAULTVALUE,FD_CAPTIONFAN,FD_CAPTIONEN,FD_DBFIND,FD_DETNO,FD_FIELDLENGTH,FD_ISNEEDENCRYPT,FD_CHECK,FD_MOBILEUSED,FD_MODIFY,FD_ISFIXED,FD_APPWIDTH) values (FORMDETAIL_SEQ.nextval,"+foid+",'FSLOADEDPLANTABLE','审核日期','pt_auditdate','D','T','T',1,null,'审核日期','审核日期','F',6,7,0,0,0,'F',0,0)");
				sqls.add("Insert into FORMDETAIL (FD_ID,FD_FOID,FD_TABLE,FD_CAPTION,FD_FIELD,FD_TYPE,FD_READONLY,FD_ALLOWBLANK,FD_COLUMNWIDTH,FD_DEFAULTVALUE,FD_CAPTIONFAN,FD_CAPTIONEN,FD_DBFIND,FD_DETNO,FD_FIELDLENGTH,FD_ISNEEDENCRYPT,FD_CHECK,FD_MOBILEUSED,FD_MODIFY,FD_ISFIXED,FD_APPWIDTH) values (FORMDETAIL_SEQ.nextval,"+foid+",'FSLOADEDPLANTABLE','审核人','pt_auditman','S','T','T',1,null,'审核人','审核人','F',7,20,0,0,0,'F',0,0)");
			}
		}
		baseDao.execute(sqls);
	}

	@Override
	public void deleteLoadedPlanSet(int id, String caller) {
		
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { id });
		
		// 删除主表内容
		baseDao.deleteById("FSLOADEDPLANSET", "ps_id", id);
		//删除从表
		baseDao.deleteById("FSLOADEDPLANSETDETAIL", "psd_psid", id);
		
		baseDao.logger.delete(caller, "ps_id", id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { id });
		
	}

}
