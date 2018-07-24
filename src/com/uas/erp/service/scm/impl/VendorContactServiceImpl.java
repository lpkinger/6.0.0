package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.VendorContactService;

@Service("vendorContactService")
public class VendorContactServiceImpl implements VendorContactService{
	
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void updateVendor(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改操作
		String sql = SqlUtil.getUpdateSqlByFormStore(store, "Vendor", "ve_id");
		baseDao.execute(sql);
		//记录操作
		baseDao.logger.update("Vendor", "ve_id", store.get("ve_id"));
	}

	@Override
	public void saveVendContact(String formStore, String caller) {
		// TODO Auto-generated method stub
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store});
		SqlRowList rs = baseDao.queryForRowSet("select * from vendorcontact where vc_name='"+store.get("vc_name")+"' and vc_mobile='"+store.get("vc_mobile")+"' and vc_vecode='"+store.get("vc_vecode")+"'");
		if(rs.next()){
			BaseUtil.showError(""+store.get("vc_name")+store.get("vc_mobile")+"已经存在此供应商联系人列表中，不允许重复添加！");
		}
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "VendorContact", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		int countnum=baseDao.getCount("select count(*) from VendorContact where nvl(vc_isvendor,0)<>0 and vc_id="+store.get("vc_id"));
		if(countnum>0){
			baseDao.execute("update vendor set (ve_contact,ve_degree,ve_mobile,ve_email)=(select vc_name,vc_job,vc_mobile,vc_officeemail from VendorContact where vc_id="+store.get("vc_id")+" )  where ve_id="+store.get("vc_veid"));
			baseDao.execute("update VendorContact set vc_isvendor=0 where vc_id<>"+store.get("vc_id")+" and vc_veid="+store.get("vc_veid"));
		}
		// 记录操作
		baseDao.logger.save(caller, "vc_id", store.get("vc_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void updateVendContact(String formStore, String caller) {		
		// TODO Auto-generated method stub
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[]{store});
		SqlRowList rs = baseDao.queryForRowSet("select * from vendorcontact where vc_name='"+store.get("vc_name")+"' and vc_mobile='"+store.get("vc_mobile")+"' and vc_vecode='"+store.get("vc_vecode")+"' and vc_id <>" + store.get("vc_id"));
		if(rs.next()){
			BaseUtil.showError(""+store.get("vc_name") + " " + store.get("vc_mobile") + "已经存在此供应商联系人列表中，不允许重复添加！");
		}
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "VendorContact", "vc_id");
		baseDao.execute(formSql);
		int countnum=baseDao.getCount("select count(*) from VendorContact where nvl(vc_isvendor,0)<>0 and vc_id="+store.get("vc_id"));
		if(countnum>0){
			baseDao.execute("update vendor set (ve_contact,ve_degree,ve_mobile,ve_email)=(select vc_name,vc_job,vc_mobile,vc_officeemail from VendorContact where vc_id="+store.get("vc_id")+" )  where ve_id="+store.get("vc_veid"));
			baseDao.execute("update VendorContact set vc_isvendor=0 where vc_id<>"+store.get("vc_id")+" and vc_veid="+store.get("vc_veid"));
		}
		//记录操作
		baseDao.logger.update(caller, "vc_id", store.get("vc_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}
}
