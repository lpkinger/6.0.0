package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.CustomerKindDao;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.CustomerKind;
import com.uas.erp.service.scm.CustomerKindService;

@Service
public class CustomerKindServiceImpl implements CustomerKindService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	@Autowired
	private CustomerKindDao customerKindDao;

	@Override
	public void saveCustomerKind(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("CustomerKind", "ck_code='" + store.get("ck_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		boolean bool2 = baseDao.checkByCondition("CustomerKind", "ck_kind='" + store.get("ck_kind") + "'");
		if (!bool2) {
			BaseUtil.showError("当前类型已经存在不允许新增");
		}
		// 执行保存前的其它逻辑
		handlerService.handler("CustomerKind", "save", "before", new Object[] { formStore });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "CustomerKind", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.logger.update("CustomerKind", "ck_id", store.get("ck_id"));
		// 执行保存后的其它逻辑
		handlerService.handler("CustomerKind", "save", "after", new Object[] { formStore });
	}

	@Override
	public void deleteCustomerKind(int ck_id) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel("CustomerKind", ck_id);
		boolean bool=baseDao.checkIf("Customer", "nvl(cu_kind,' ')=(select ck_kind from CustomerKind where ck_id="+ck_id+")");
		if(bool) 	BaseUtil.showError("当前类型已经存在客户资料中不允许修改!");
		// 删除
		baseDao.deleteById("CustomerKind", "ck_id", ck_id);
		// 记录操作
		baseDao.logger.delete("CustomerKind", "ck_id", ck_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel("CustomerKind", ck_id);
	}

	@Override
	public void updateCustomerKindById(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeSave("CustomerKind", new Object[] { store });
		boolean bool=baseDao.checkIf("Customer", "nvl(cu_kind,' ')=(select ck_kind from CustomerKind where ck_id="+store.get("ck_id")+")");
		if(bool) 	BaseUtil.showError("当前类型已经存在客户资料中不允许修改!");
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "CustomerKind", "ck_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update("CustomerKind", "ck_id", store.get("ck_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave("CustomerKind", new Object[] { store });
	}

	@Override
	public List<JSONTree> getJsonTrees(int parentid) {
		List<JSONTree> tree = new ArrayList<JSONTree>();
		List<CustomerKind> list = customerKindDao.getCustomerKindByParentId(parentid);
		for (CustomerKind navigation : list) {
			tree.add(new JSONTree(navigation));
		}
		return tree;
	}

	@Override
	public String getCustomerKindNum(int id) {
		Object[] objs = baseDao.getFieldsDataByCondition("CustomerKind", "ck_maxnum,ck_length", "ck_id=" + id);
		int ret = Integer.parseInt(objs[0].toString());
		int length = Integer.parseInt(objs[1].toString());
		ret++;
		baseDao.updateByCondition("CustomerKind", "ck_maxnum=" + ret, "ck_id=" + id);
		String number = "";
		length -= String.valueOf(ret).length();
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				number += "0";
			}
		}
		number += String.valueOf(ret);
		return number;
	}

	@Override
	public void end(int id) {
		// TODO Auto-generated method stub
		baseDao.banned("CustomerKind", "ck_id="+id, "ck_status", "ck_statuscode");
		baseDao.logger.banned("CustomerKind", "ck_id", id);
	}

	@Override
	public void resEnd(int id) {
		// TODO Auto-generated method stub
		baseDao.resOperate("CustomerKind", "ck_id="+id, "ck_status", "ck_statuscode");
		baseDao.logger.resBanned("CustomerKind", "ck_id", id);
	}
}
