package com.uas.erp.service.oa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.ReceiveOfficialDocumentDao;
import com.uas.erp.model.ReceiveOfficialDocument;
import com.uas.erp.service.oa.ReceiveODMService;

@Service("ReceiveODMService")
public class ReceiveODMServiceImpl implements ReceiveODMService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private ReceiveOfficialDocumentDao receiveODDao;
	@Override
	public void saveROD(String formStore, String  caller) {
//		formStore = formStore.substring(0, formStore.lastIndexOf("}"));
//		formStore = formStore + ",\"rod_registrant_id\":\"" + employee.getEm_id() +"\"}";
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ReceiveOfficialDocument", "rod_id='" + store.get("rod_id") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ReceiveOfficialDocument", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "rod_id", store.get("rod_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});

	}
	@Override
	public void deleteROD(int rod_id, String  caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{rod_id});
		//删除
		baseDao.deleteById("ReceiveOfficialDocument", "rod_id", rod_id);
		//记录操作
		baseDao.logger.delete(caller, "rod_id", rod_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{rod_id});
	}
	@Override
	public void updateRODById(String formStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ReceiveOfficialDocument", "rod_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "rod_id", store.get("rod_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
	}
	@Override
	public ReceiveOfficialDocument getRODById(int id, String  caller) {
		return receiveODDao.findRODById(id);
	}
	@Override
	public void submitROD(int rod_id, String  caller) {
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,new Object[]{rod_id});
		//执行提交操作
		baseDao.submit("ReceiveOfficialDocument", "rod_id=" + rod_id, "rod_status", "rod_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "rod_id", rod_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller,new Object[]{rod_id});
	}
	
	@Override
	public void deleteById(int rod_id) {
		receiveODDao.delete(rod_id);
		
	}
	
	@Override
	public List<ReceiveOfficialDocument> getList(int page, int pageSize) {
		return receiveODDao.getList(page, pageSize);
	}
	@Override
	public int getListCount() {
		return receiveODDao.getListCount();
	}
	
	@Override
	public List<ReceiveOfficialDocument> getByCondition(String condition, int page, int pageSize) {
		return receiveODDao.getByCondition(condition, page, pageSize);
	}
	@Override
	public int getSearchCount(String condition) {
		return receiveODDao.getSearchCount(condition);
	}

}
