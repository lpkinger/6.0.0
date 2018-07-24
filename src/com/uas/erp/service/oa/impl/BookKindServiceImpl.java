package com.uas.erp.service.oa.impl;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.BookKindService;
@Service("bookKindService")
public class BookKindServiceImpl implements BookKindService{
	@Autowired
   private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveBookKind(String formStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BookKind", "bk_code='" + store.get("bk_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("oa.publicAdmin.book.basicData.BookKind.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//保存BookKind
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BookKind", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
			//记录操作
		baseDao.logger.save(caller, "bk_id", store.get("bk_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
		
	}

	@Override
	public void deleteBookKind(int bk_id, String  caller) {
		baseDao.delCheck("BookKind", bk_id);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[]{bk_id});
		//删除BookKind
		baseDao.deleteById("BookKind", "bk_id", bk_id);
		//记录操作
		baseDao.logger.delete(caller, "bk_id", bk_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[]{bk_id});
		
	}

	@Override
	public void updateBookKindById(String formStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		//修改BookKind
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BookKind", "bk_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "bk_id", store.get("bk_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
		
	}

}
