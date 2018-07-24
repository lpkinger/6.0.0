package com.uas.erp.service.oa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.BookService;
@Service("bookService")
public class BookServiceImpl implements BookService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveBook(String formStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Book", "bo_code='" + store.get("bo_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("oa.publicAdmin.book.bookManage.Book.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//保存Book
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Book", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
			//记录操作
		baseDao.logger.save(caller, "bo_id", store.get("bo_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
		
	}
	@Override
	public void deleteBook(int bo_id, String  caller) {
		//只能删除在录入的采购单!
		Object status = baseDao.getFieldDataByCondition("Book", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{bo_id});
		//删除Book
		baseDao.deleteById("Book", "bo_id", bo_id);
		//记录操作
		baseDao.logger.delete(caller, "bo_id", bo_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{bo_id});
		
	}
	@Override
	public void updateBookdById(String formStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能修改!
		boolean bool = baseDao.checkByCondition("Book", "bo_code='" + store.get("bo_code") + "' and bo_id<>"+store.get("bo_id"));
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("oa.publicAdmin.book.bookManage.Book.save_codeHasExist"));
		}
		//只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("Book", "bo_statuscode", "bo_id=" + store.get("bo_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[]{store});
		//修改Book
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Book", "bo_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "bo_id", store.get("bo_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[]{store});
	}
	@Override
	public void submitBook(int bo_id, String  caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Book", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{bo_id});
		//执行提交操作
		baseDao.submit("Book", "bo_id=" + bo_id, "bo_status", "bo_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "bo_id", bo_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{bo_id});
		
	}
	@Override
	public void resSubmitBook(int bo_id, String  caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
                 StateAssert.resSubmitOnlyCommited(caller);
				//执行反提交操作
				baseDao.resOperate("Book", "bo_id=" + bo_id, "bo_status", "bo_statuscode");
				//记录操作
				baseDao.logger.resSubmit(caller, "bo_id", bo_id);
		
	}
	@Override
	public void auditBook(int bo_id, String  caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Book", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.afterAudit(caller,new Object[]{bo_id});
		//执行审核操作
		baseDao.audit("Book", "bo_id=" + bo_id, "bo_status", "bo_statuscode", "bo_auditdate", "bo_auditman");
		//记录操作
		baseDao.logger.audit(caller, "bo_id", bo_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{bo_id});
		
	}
	@Override
	public void resAuditBook(int bo_id, String  caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
				Object status = baseDao.getFieldDataByCondition("Book", "bo_statuscode", "bo_id=" + bo_id);
				StateAssert.resAuditOnlyAudit(status);
					//执行反审核操作
					baseDao.resOperate("Book", "bo_id=" + bo_id, "bo_status", "bo_statuscode");
					//记录操作
					baseDao.logger.resAudit(caller, "bo_id", bo_id);
			}
	@Override
	public String turnBanned(String caller, String data) {
		List<Map<Object,Object>> maps=BaseUtil.parseGridStoreToMaps(data);
		String ids = CollectionUtil.pluckSqlString(maps, "bo_id");
		baseDao.execute("update book set bo_status='已禁用',bo_statuscode='DISABLE' where bo_id in("+ids+")");		
		return "处理成功";
	}
		
}
