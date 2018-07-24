package com.uas.erp.service.fa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.fa.AssetsKindService;

@Service("assetsKindService")
public class AssetsKindServiceImpl implements AssetsKindService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveAssetsKind(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存AssetsKind
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "AssetsKind", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.execute(
				"update assetskind set ak_useyears=round(nvl(ak_usemonths,0)/12,8) where ak_id=? and nvl(ak_useyears,0)=0 and nvl(ak_usemonths,0)<>0",
				store.get("ak_id"));
		handlerService.afterSave(caller, new Object[] { store });
		// 记录操作
		baseDao.logger.save(caller, "ak_id", store.get("ak_id"));

	}

	@Override
	public void updateAssetsKindById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("AssetsKind", "ak_statuscode", "ak_id=" + store.get("ak_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改AssetsKind
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "AssetsKind", "ak_id");
		baseDao.execute(formSql);
		baseDao.execute(
				"update assetskind set ak_useyears=round(nvl(ak_usemonths,0)/12,8) where ak_id=? and nvl(ak_useyears,0)=0 and nvl(ak_usemonths,0)<>0",
				store.get("ak_id"));
		// 记录操作
		baseDao.logger.update(caller, "ak_id", store.get("ak_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });

	}

	@Override
	public void deleteAssetsKind(int ak_id, String caller) {
		// 只能删除在录入的采购单!
		Object status = baseDao.getFieldDataByCondition("AssetsKind", "ak_statuscode", "ak_id=" + ak_id);
		StateAssert.delOnlyEntering(status);
		baseDao.delCheck("AssetsKind", ak_id);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, ak_id);
		// 删除AssetsKind
		baseDao.deleteById("AssetsKind", "ak_id", ak_id);
		// 记录操作
		baseDao.logger.delete(caller, "ak_id", ak_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ak_id);
	}

	void checkCate(Object ak_id) {
		SqlRowList rs = baseDao.queryForRowSet("select ak_facatecode,ak_deprecatecode from AssetsKind where ak_id=?",
				new Object[] { ak_id });
		if (rs.next()) {
			String error = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
					String.class, rs.getObject("ak_facatecode"));
			if (error != null) {
				BaseUtil.showError("填写的固定资产科目不存在，或者状态不等于已审核，或者不是末级科目！");
			}
			error = baseDao.getJdbcTemplate().queryForObject(
					"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
					String.class, rs.getObject("ak_deprecatecode"));
			if (error != null) {
				BaseUtil.showError("填写的累计折旧科目不存在，或者状态不等于已审核，或者不是末级科目！");
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code NOT IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('MonthAccount!AS','fixCatecode'), chr(10))))",
							String.class, rs.getObject("ak_facatecode"));
			if (error != null) {
				BaseUtil.showError("固定资产科目编号不是参数设置中的科目！");
			}
			error = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code NOT IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('MonthAccount!AS','deCatecode'), chr(10))))",
							String.class, rs.getObject("ak_deprecatecode"));
			if (error != null) {
				BaseUtil.showError("累计折旧科目编号不是参数设置中的科目！");
			}
		}
	}

	@Override
	public void auditAssetsKind(int ak_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("AssetsKind", "ak_statuscode", "ak_id=" + ak_id);
		StateAssert.auditOnlyCommited(status);
		checkCate(ak_id);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ak_id);
		// 执行审核操作
		baseDao.updateByCondition("AssetsKind", "ak_statuscode='AUDITED',ak_status='" + BaseUtil.getLocalMessage("AUDITED")
				+ "',ak_auditer='" + SystemSession.getUser().getEm_name() + "',ak_auditdate=sysdate", "ak_id=" + ak_id);
		// 记录操作
		baseDao.logger.audit(caller, "ak_id", ak_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ak_id);
	}

	@Override
	public void resAuditAssetsKind(int ak_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("AssetsKind", "ak_statuscode", "ak_id=" + ak_id);
		StateAssert.resAuditOnlyAudit(status);
		baseDao.resAuditCheck("AssetsKind", ak_id);
		handlerService.beforeResAudit(caller, ak_id);
		// 执行反审核操作
		baseDao.updateByCondition("AssetsKind", "ak_statuscode='ENTERING',ak_status='" + BaseUtil.getLocalMessage("ENTERING")
				+ "',ak_auditer='',ak_auditdate=null", "ak_id=" + ak_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ak_id", ak_id);
		handlerService.afterResAudit(caller, ak_id);
	}

	@Override
	public void submitAssetsKind(int ak_id, String caller) {
		baseDao.execute(
				"update assetskind set ak_useyears=round(nvl(ak_usemonths,0)/12,8) where ak_id=? and nvl(ak_useyears,0)=0 and nvl(ak_usemonths,0)<>0",
				ak_id);
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("AssetsKind", "ak_statuscode", "ak_id=" + ak_id);
		StateAssert.submitOnlyEntering(status);
		checkCate(ak_id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ak_id);
		// 执行提交操作
		baseDao.updateByCondition("AssetsKind", "ak_statuscode='COMMITED',ak_status='" + BaseUtil.getLocalMessage("COMMITED") + "'",
				"ak_id=" + ak_id);
		// 记录操作
		baseDao.logger.submit(caller, "ak_id", ak_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ak_id);
	}

	@Override
	public void resSubmitAssetsKind(int ak_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("AssetsKind", "ak_statuscode", "ak_id=" + ak_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, ak_id);
		// 执行反提交操作
		baseDao.updateByCondition("AssetsKind", "ak_statuscode='ENTERING',ak_status='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"ak_id=" + ak_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ak_id", ak_id);
		handlerService.afterResSubmit(caller, ak_id);
	}
}
