package com.uas.erp.service.hr.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.KpibillChangeService;

@Service
public class KpibillChangeServiceImpl implements KpibillChangeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveKpibillChange(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] {store});
		// 保存KBIChangeMan
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "KpibillChange",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "kbc_id", store.get("kbc_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store});
	}

	@Override
	public void deleteKpibillChange(int kbc_id, String caller) {
		// 只能删除在录入的!b
		Object status = baseDao.getFieldDataByCondition("KpibillChange",
				"kbc_statuscode", "kbc_id=" + kbc_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] {kbc_id});
		// 删除KBIChangeMan
		baseDao.deleteById("KpibillChange", "kbc_id", kbc_id);
		// 记录操作
		baseDao.logger.delete(caller, "kbc_id", kbc_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] {kbc_id});
	}

	@Override
	public void updateKpibillChange(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("KpibillChange","kbc_statuscode", "kbc_id=" + store.get("kbc_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[] {store});
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "KpibillChange","kbc_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "kbc_id", store.get("kbc_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[] {store});
	}

	@Override
	public void submitKpibillChange(int kbc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("KpibillChange","kbc_statuscode", "kbc_id=" + kbc_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] {kbc_id});
		// 执行提交操作
		baseDao.submit("KpibillChange", "kbc_id=" + kbc_id, "kbc_status", "kbc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "kbc_id", kbc_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] {kbc_id});
	}

	@Override
	public void resSubmitKpibillChange(int kbc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("KpibillChange","kbc_statuscode", "kbc_id=" + kbc_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.resOperate("KpibillChange", "kbc_id=" + kbc_id, "kbc_status", "kbc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "kbc_id", kbc_id);
	}

	@Override
	public void auditKpibillChange(int kbc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("KpibillChange","kbc_statuscode", "kbc_id=" + kbc_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] {kbc_id});
		// 执行审核操作
		baseDao.audit("KpibillChange", "kbc_id=" + kbc_id, "kbc_status", "kbc_statuscode", "kbc_auditdate", "kbc_auditer");
		//原评估单作废
		Object[] data=baseDao.getFieldsDataByCondition("KpibillChange",new String[]{"kbc_kbid","kbc_manid","kbc_man"}, "kbc_id=" + kbc_id);
		baseDao.updateByCondition("Kpibill","kb_manid="+data[1]+",kb_man='"+data[2]+"'", "kb_id='"+data[0]+"'");
		// 记录操作
		baseDao.logger.audit(caller, "kbc_id", kbc_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] {kbc_id});
	}

}
