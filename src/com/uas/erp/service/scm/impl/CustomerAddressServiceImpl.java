package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.CustomerAddressService;

@Service("customerAddressService")
public class CustomerAddressServiceImpl implements CustomerAddressService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void updateCustomerById(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);	
		//执行修改前的其它逻辑
		handlerService.handler("CustomerAddress", "save", "before", new Object[]{store, gstore});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Customer", "cu_id");
		baseDao.execute(formSql);
		//修改ProductUnit
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "CustomerAddress", "ca_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("ca_id") == null || s.get("ca_id").equals("") || s.get("ca_id").equals("0") ||
					Integer.parseInt(s.get("ca_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("CUSTOMERADDRESS_SEQ");
				s.put("ca_cuid",  store.get("cu_id"));
				String sql = SqlUtil.getInsertSqlByMap(s, "CustomerAddress", new String[]{"ca_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute("update customer set cu_add1=(select ca_address from CustomerAddress where ca_cuid=cu_id and ca_remark='是' and nvl(ca_address,' ')<>' ') where cu_id="+store.get("cu_id"));
		baseDao.execute("update customer set cu_shadd=(select ca_shcustname from CustomerAddress where ca_cuid=cu_id and ca_remark='是' and nvl(ca_shcustname,' ')<>' ') where cu_id="+store.get("cu_id"));
		//记录操作
		baseDao.logger.update("CustomerAddress", "cu_id", store.get("cu_id"));
		//执行修改后的其它逻辑
		handlerService.handler("CustomerAddress", "save", "after", new Object[]{store, gstore});
	}

	@Override
	public int getCustomerid(String code) {
		Object id = baseDao.getFieldDataByCondition("customer","cu_id", "cu_code='"+code+"'");
		id = id==null?0:id;
		return Integer.valueOf(id.toString());
	}
}
