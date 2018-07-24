package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.VendorKindDao;
import com.uas.erp.core.HandlerService;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.VendorKind;
import com.uas.erp.service.scm.VendorKindService;


@Service("vendorKindService")
public class VendorKindServiceImpl implements VendorKindService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private VendorKindDao vendorKindDao;
	@Override
	public void saveVendorKind(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("VendorKind", "vk_kind='" + store.get("vk_kind") + "'");
		if(!bool){
			BaseUtil.showError("当前类型已经存在不允许新增");
		}
		//执行保存前的其它逻辑
		handlerService.handler("VendorKind", "save", "before", new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "VendorKind", new String[]{}, new Object[]{});
		baseDao.execute(formSql);	
		baseDao.logger.save("VendorKind", "vk_id", store.get("vk_id"));
		//执行保存后的其它逻辑
	    handlerService.handler("VendorKind", "save", "after", new Object[]{store});
	}
	@Override
	public void deleteVendorKind(int vk_id) {
		//执行删除前的其它逻辑
		handlerService.handler("VendorKind", "delete", "before", new Object[]{vk_id});
		baseDao.delCheck("VendorKind", vk_id);
		//删除
		baseDao.deleteById("vendorKind", "vk_id", vk_id);		
		//记录操作
		baseDao.logger.delete("VendorKind", "vk_id", vk_id);
		//执行删除后的其它逻辑
		handlerService.handler("VendorKind", "delete", "after", new Object[]{vk_id});
	}
	
	@Override
	public void updateVendorKindById(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		boolean bool=baseDao.checkIf("vendorkind", " vk_id="+store.get("vk_id")+" and vk_kind in (select ve_type from vendor)");
		if (bool) {
			BaseUtil.showError("类型存在于供应商资料中不允许修改!");
		}
		//执行修改前的其它逻辑
		handlerService.handler("VendorKind", "save", "before", new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "VendorKind", "vk_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save("VendorKind", "vk_id", store.get("vk_id"));
		//执行修改后的其它逻辑
		handlerService.handler("VendorKind", "save", "after", new Object[]{store});
	}
	@Override
	public List<JSONTree> getJsonTrees(int parentid) {
		List<JSONTree> tree = new ArrayList<JSONTree>();
		List<VendorKind> list = vendorKindDao.getVendorKindByParentId(parentid);
		for(VendorKind navigation:list){
			tree.add(new JSONTree(navigation));
		}
		return tree;
	}
	@Override
	public String getVendorCodeByKind(String kind) {
		Object[] objs = baseDao.getFieldsDataByCondition("VendorKind", "vk_maxnum,vk_length,vk_excode", "vk_kind='" + kind+"'");
		int ret =  Integer.parseInt(objs[0].toString());
		int length = Integer.parseInt(objs[1].toString());
		ret++;
		baseDao.updateByCondition("VendorKind", "vk_maxnum=" + ret, "vk_kind='" + kind+"'");
		String number = "";
		length -= String.valueOf(ret).length();
		if(length > 0) {
			for(int i = 0 ;i < length; i++) {
				number += "0";
			}
		}
		number += String.valueOf(ret);
		return objs[2]+number;
	}
	@Override
	public void banned(int id) {
		// TODO Auto-generated method stub
		baseDao.banned("VendorKind", "vk_id="+id, "vk_status", "vk_statuscode");
		baseDao.logger.banned("VendorKind", "vk_id", id);
	}
	@Override
	public void resBanned(int id) {
		// TODO Auto-generated method stub
		baseDao.banned("VendorKind", "vk_id="+id, "vk_status", "vk_statuscode");
		baseDao.logger.banned("VendorKind", "vk_id", id);
	}
}
