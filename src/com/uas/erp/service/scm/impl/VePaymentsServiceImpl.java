package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.VePaymentsService;

@Service("vePaymentsService")
public class VePaymentsServiceImpl implements VePaymentsService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveVePayments(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("VePayments", "vp_code='" + store.get("vp_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler("VePayments", "save", "before", new Object[] { store, grid });
		// 保存Payments
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "VePayments", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存PaymentsDetail
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "VePaymentsDetail", "vpd_id");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "vp_id", store.get("vp_id"));
		// 执行保存后的其它逻辑
		handlerService.handler("VePayments", "save", "after", new Object[] { store, grid });
	}

	@Override
	public void deleteVePayments(int vp_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler("VePayments", "delete", "before", new Object[] { vp_id });
		// 删除Payments
		baseDao.deleteById("VePayments", "vp_id", vp_id);
		// 删除PaymentsDetail
		baseDao.deleteById("VePaymentsdetail", "vpd_vpid", vp_id);
		// 记录操作
		baseDao.logger.delete(caller, "vp_id", vp_id);
		// 执行删除后的其它逻辑
		handlerService.handler("VePayments", "delete", "after", new Object[] { vp_id });
	}

	@Override
	public void updateVePaymentsById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行修改前的其它逻辑
		handlerService.handler("VePayments", "save", "before", new Object[] { store, gstore });
		// 修改Payments
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "VePayments", "vp_id");
		baseDao.execute(formSql);
		// 修改PaymentsDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "VePaymentsDetail", "vpd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("vpd_id") == null || s.get("vpd_id").equals("") || s.get("vpd_id").equals("0")
					|| Integer.parseInt(s.get("vpd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("VEPAYMENTSDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "VePaymentsDetail", new String[] { "vpd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "vp_id", store.get("vp_id"));
		// 执行修改后的其它逻辑
		handlerService.handler("Payments!SalePaymentsDetail", "save", "after", new Object[] { store, gstore });
	}
	
	@Override
	public void updateVendorBankById(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);	
		//执行修改前的其它逻辑
		handlerService.handler("VendorBank", "save", "before", new Object[]{store, gstore});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Vendor", "ve_id");
		baseDao.execute(formSql);
		//修改ProductUnit
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "vepaymentsdetail", "vpd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("vpd_id") == null || s.get("vpd_id").equals("") || s.get("vpd_id").equals("0") ||
					Integer.parseInt(s.get("vpd_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("VEPAYMENTSDETAIL_SEQ");
				s.put("vpd_veid",  store.get("ve_id"));
				s.put("vpd_vecode",  store.get("ve_code"));
				String sql = SqlUtil.getInsertSqlByMap(s, "vepaymentsdetail", new String[]{"vpd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update("VendorBank", "ve_id", store.get("ve_id"));
		baseDao.execute("update vendor set (ve_bank,ve_bankaccount,ve_bankman,ve_contact,ve_currency,ve_taxrate,ve_bankaddress,ve_bankcode)=(select vpd_bank,vpd_bankaccount,vpd_bankman,vpd_contact,vpd_currency,vpd_taxrate,vpd_bankaddress,vpd_bankcode from vepaymentsdetail where vpd_veid=ve_id and vpd_remark='是' and nvl(vpd_bank,' ')<>' ') where ve_id="+store.get("ve_id"));
		//执行修改后的其它逻辑
		handlerService.handler("VendorBank", "save", "after", new Object[]{store, gstore});
	}

}
