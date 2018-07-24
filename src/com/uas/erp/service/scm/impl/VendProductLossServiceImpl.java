package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.VendProductLossService;
/**
 * 
 * @author zjh
 *
 */
@Service
public class VendProductLossServiceImpl implements VendProductLossService {
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void saveVendProductLoss(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//产品编号 pl_code +分段数(大于) pl_lapqty 都相同才需要限制保存、更新
		String code = store.get("vpl_code").toString();
		baseDao.asserts.isFalse("VendProdLoss", "vpl_code='"+code+"'and nvl(vpl_vendcode,'100000000')='"+StringUtil.nvl(store.get("vpl_vendcode"),"100000000")+"'", "当前单据的物料编号+委外商编号记录已存在，不允许保存!");
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] {store});
		// 保存AssistRequire
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "VendProdLoss", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.logger.save(caller, "vpl_id", store.get("vpl_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] {store});

	}

	@Override
	public void deleteVendProductLoss(int vpl_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{vpl_id});
		//删除主表内容
		baseDao.deleteById("VendProdLoss", "vpl_id", vpl_id);
		baseDao.logger.delete(caller, "vpl_id", vpl_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{vpl_id});

	}

	@Override
	public void updateVendProductLoss(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前物料编号前缀的记录已经存在,不能修改!
		String code = store.get("vpl_code").toString();
		baseDao.asserts.isFalse("VendProdLoss", "vpl_code='"+code+"'and nvl(vpl_vendcode,'100000000')='"+StringUtil.nvl(store.get("vpl_vendcode"),"100000000")+"'", "当前单据的物料编号+委外商编号记录已存在，不允许保存!");
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] {store});
		// 修改KBIAssess
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "VendProdLoss", "vpl_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "vpl_id", store.get("vpl_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] {store});

	}

}
