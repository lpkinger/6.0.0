package com.uas.erp.service.fa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.fa.VoucherDescriptionService;

@Service("voucherDescriptionService")
public class VoucherDescriptionServiceImpl implements VoucherDescriptionService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveVoucherDescription(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);

		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"VoucherDescription", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "vd_id", store.get("vd_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteVoucherDescription(int vd_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, vd_id);
		// 删除
		baseDao.deleteById("VoucherDescription", "vd_id", vd_id);
		// 记录操作
		baseDao.logger.delete(caller, "vd_id", vd_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, vd_id);
	}

	@Override
	public void updateVoucherDescriptionById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"VoucherDescription", "vd_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "vd_id", store.get("vd_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}
}
