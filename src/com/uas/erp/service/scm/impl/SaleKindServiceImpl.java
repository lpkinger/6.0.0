package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.SaleKindService;

@Service
public class SaleKindServiceImpl implements SaleKindService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveSaleKind(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("SaleKind", "sk_code='" + store.get("sk_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store});
		//判断sk_ifb2c  是否优软商场订单
		if(store.get("sk_ifb2c") != null && store.get("sk_ifb2c").equals("-1")){//判断是否有其他销售类型设置  是否优软商场订单 为是
			Object ob = baseDao.getFieldDataByCondition("salekind", "sk_name", "nvl(sk_ifb2c,0)=-1");
			if(ob!= null){
				BaseUtil.showError("存在其他优软商场订单的销售类型["+ob.toString()+"]");
			}
		}
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "SaleKind", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "sk_id", store.get("sk_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}
	@Override
	public void deleteSaleKind(int sk_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{sk_id});
		baseDao.delCheck("SaleKind", sk_id);
		//删除
		baseDao.deleteById("SaleKind", "sk_id", sk_id);
		//记录操作
		baseDao.logger.delete(caller, "sk_id", sk_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{sk_id});
	}
	@Override
	public void updateSaleKindById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store});
		//判断sk_ifb2c  是否优软商场订单
		if(store.get("sk_ifb2c") != null && store.get("sk_ifb2c").equals("-1")){//判断是否有其他销售类型设置  是否优软商场订单 为是
			Object ob = baseDao.getFieldDataByCondition("salekind", "sk_name", "nvl(sk_ifb2c,0)=-1 and sk_id<>"+store.get("sk_id"));
			if(ob!= null){
				BaseUtil.showError("存在其他优软商城订单的销售类型["+ob.toString()+"]");
			}
		}
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "SaleKind", "sk_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "sk_id", store.get("sk_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}
}
