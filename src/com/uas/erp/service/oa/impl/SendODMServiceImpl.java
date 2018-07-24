package com.uas.erp.service.oa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.ReceiveOfficialDocumentDao;
import com.uas.erp.dao.common.SendOfficialDocumentDao;
import com.uas.erp.model.ReceiveOfficialDocument;
import com.uas.erp.model.SendOfficialDocument;
import com.uas.erp.service.oa.SendODMService;

@Service("SendODMService")
public class SendODMServiceImpl implements SendODMService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private ReceiveOfficialDocumentDao receiveODDao;
	@Autowired
	private SendOfficialDocumentDao sendODDao;
	@Override
	public void saveSOD(String formStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("SendOfficialDocument", "sod_id='" + store.get("sod_id") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller,new Object[]{store});
		//保存
		store.put("sod_number", store.get("size1") + " (" + store.get("size2") + ") " + store.get("size3") + "号");
		store.remove("size1");
		store.remove("size2");
		store.remove("size3");
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "SendOfficialDocument", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "sod_id", store.get("sod_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//执行保存后的其它逻辑
		handlerService.afterSave(caller,new Object[]{store});

	}
	@Override
	public void deleteSOD(int sod_id, String  caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{sod_id});
		//删除
		baseDao.deleteById("SendOfficialDocument", "sod_id", sod_id);
		//记录操作
		baseDao.logger.delete(caller, "sod_id", sod_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{sod_id});
	}
	@Override
	public void updateSODById(String formStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		store.put("sod_number", store.get("size1") + " (" + store.get("size2") + ") " + store.get("size3") + "号");
		store.remove("size1");
		store.remove("size2");
		store.remove("size3");
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "SendOfficialDocument", "sod_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "sod_id", store.get("sod_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
	}
	@Override
	public void save(int rid, int sid, String  caller) {
		ReceiveOfficialDocument rod = receiveODDao.findRODById(rid);
		SendOfficialDocument sod = new SendOfficialDocument();
		sod.setSod_id(sid);
		sod.setSod_attach(rod.getRod_attach());
		sod.setSod_context(rod.getRod_context());
		sod.setSod_emergencydegree(rod.getRod_emergencyDegree());
//		sod.setSod_type(rod.getRod_type());
		sod.setSod_title(rod.getRod_title());
		sod.setSod_subject(rod.getRod_subject());
		sod.setSod_secretlevel(rod.getRod_secretLevel());
		sod.setSod_recipient_id(rod.getRod_recipient_id());
		sod.setSod_fw_organ(rod.getRod_unit());
		sod.setSod_drafter_id(SystemSession.getUser().getEm_id());
		sendODDao.insertSOD(sod);		
	}
	
	@Override
	public SendOfficialDocument getSODById(int id, String caller) {
		return sendODDao.getSODById(id);
	}
	
	@Override
	public void deleteById(int sod_id) {
		sendODDao.delete(sod_id);
		
	}
	
	@Override
	public List<SendOfficialDocument> getList(int page, int pageSize) {
		return sendODDao.getList(page, pageSize);
	}
	@Override
	public int getListCount() {
		return receiveODDao.getListCount();
	}
	
	@Override
	public List<SendOfficialDocument> getByCondition(String condition, int page, int pageSize) {
		return sendODDao.getByCondition(condition, page, pageSize);
	}
	@Override
	public int getSearchCount(String condition) {
		return sendODDao.getSearchCount(condition);
	}
	/**
	 * 公文提交
	 */
	@Override
	public void submitDraft(int id, String  caller) {
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{id});
		//提交
		baseDao.submit("SendOfficialDocument", "sod_id=" + id, "sod_status", "sod_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "sod_id", id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{id});
	}
	@Override
	public void resSubmitDraft(int id, String  caller) {
		//反提交
		baseDao.resOperate("SendOfficialDocument", "sod_id=" + id, "sod_status", "sod_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "sod_id", id);
	}
	@Override
	public void auditDraft(int id, String  caller) {
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller,  new Object[]{id});
		//审核
		baseDao.audit("SendOfficialDocument", "sod_id=" + id, "sod_status", "sod_statuscode", "sod_auditdate", "sod_auditman");
		//记录操作
		baseDao.logger.audit(caller, "sod_id", id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller,  new Object[]{id});
	}
	@Override
	public void resAuditDraft(int id, String  caller) {
		//执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, new Object[]{id});
		//反审核
		baseDao.resOperate("SendOfficialDocument", "sod_id=" + id, "sod_status", "sod_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "sod_id", id);
		//执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, new Object[]{id});
	}

}
