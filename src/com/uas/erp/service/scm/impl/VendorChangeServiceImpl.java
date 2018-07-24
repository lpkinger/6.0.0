package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.VendorChangeDao;
import com.uas.erp.service.scm.VendorChangeService;

@Service("vendorChangeService")
public class VendorChangeServiceImpl implements VendorChangeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private VendorChangeDao VendorChangeDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveVendorChange(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("VendorChange", "vc_code='" + store.get("vc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		// 保存VendorChange
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "VendorChange", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存VendorChangeDetail
		for (Map<Object, Object> m : grid) {
			m.put("vcd_id", baseDao.getSeqId("VENDORCHANGEDETAIL_SEQ"));
			m.put("vcd_code", store.get("vc_code"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "VendorChangeDetail");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "vc_id", store.get("vc_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, grid });
	}

	@Override
	public void deleteVendorChange(String caller, int vc_id) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("VendorChange", "vc_statuscode", "vc_id=" + vc_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { vc_id });
		// 删除VendorChange
		baseDao.deleteById("VendorChange", "vc_id", vc_id);
		// 删除VendorChangeDetail
		baseDao.deleteById("VendorChangedetail", "vcd_vcid", vc_id);
		// 记录操作
		baseDao.logger.delete(caller, "vc_id", vc_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { vc_id });
	}

	@Override
	public void updateVendorChangeById(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("VendorChange", "vc_statuscode", "vc_id=" + store.get("vc_id"));
		StateAssert.updateOnlyEntering(status);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("VendorChange", "vc_code='" + store.get("vc_code") + "' and vc_id<>" + store.get("vc_id"));
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		// 修改VendorChange
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "VendorChange", "vc_id");
		baseDao.execute(formSql);
		// 修改VendorChangeDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "VendorChangeDetail", "vcd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("vcd_id") == null || s.get("vcd_id").equals("") || s.get("vcd_id").equals("0")
					|| Integer.parseInt(s.get("vcd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("VENDORCHANGEDETAIL_SEQ");
				s.put("vcd_id", id);
				s.put("vcd_code", store.get("pu_code"));
				String sql = SqlUtil.getInsertSqlByMap(s, "VendorChangeDetail", new String[] { "vcd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "vc_id", store.get("vc_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
	}

	@Override
	public void auditVendorChange(int vc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("VendorChange", "vc_statuscode", "vc_id=" + vc_id);
		StateAssert.auditOnlyCommited(status);
		// 同一供应商只能存在一张已提交未审核的变更单
		List<Object[]> sa = baseDao.getFieldsDatasByCondition("VendorChangeDetail", new String[] { "vcd_vendcode" }, "vcd_vcid=" + vc_id);
		for (Object[] c : sa) {
			int count = baseDao.getCountByCondition("VendorChangeDetail left join VendorChange on vcd_vcid=vc_id", "vcd_vendcode='" + c[0]
					+ "' and vc_statuscode = 'COMMITED'");
			if (count > 1) {
				BaseUtil.showError("供应商[" + c[0] + "]只能存在一张已提交未审核的变更单");
			}
		}
		// 执行审核前的其它逻辑
		handlerService.handler("VendorChange", "audit", "before", new Object[] { vc_id });
		// 信息自动反馈到供应商资料
		List<String> ve_codes = new ArrayList<String>();
		// 更新供应商资料的数据
		try {
			ve_codes = VendorChangeDao.turnVendor(vc_id);// ?
		} catch (Exception ex) {
			BaseUtil.showError(ex.toString());
			return;
		}
		// 执行审核操作
		baseDao.audit("VendorChange", "vc_id=" + vc_id, "vc_status", "vc_statuscode", "vc_auditdate", "vc_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "vc_id", vc_id);
		// 执行审核后的其它逻辑
		handlerService.handler("VendorChange", "audit", "after", new Object[] { vc_id });
		StringBuffer sb = new StringBuffer();
		for (String c : ve_codes) {
			sb.append("<a href=\"javascript:openUrl('jsps/scm/purchase/vendor.jsp?formCondition=ve_codeIS" + c + "')\">单号:" + c
					+ ",点击查看</a>&nbsp;");
		}
		if (sb.length() > 0) {
			BaseUtil.showErrorOnSuccess("信息已自动反馈到供应商&nbsp;&nbsp;<br>" + sb.toString());
		}
	}

	@Override
	public void resAuditVendorChange(int vc_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("VendorChange", "vc_statuscode", "vc_id=" + vc_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("VendorChange", "vc_id=" + vc_id, "vc_status", "vc_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "vc_id", vc_id);
	}

	@Override
	public void submitVendorChange(int vc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("VendorChange", "vc_statuscode", "vc_id=" + vc_id);
		StateAssert.submitOnlyEntering(status);
		// 同一供应商只能存在一张已提交未审核的变更单
		List<Object[]> sa = baseDao.getFieldsDatasByCondition("VendorChangeDetail", new String[] { "vcd_vendcode" }, "vcd_vcid=" + vc_id);
		for (Object[] c : sa) {
			int count = baseDao.getCountByCondition("VendorChangeDetail left join VendorChange on vcd_vcid=vc_id", "vcd_vendcode='" + c[0]
					+ "' and vc_statuscode = 'COMMITED'");
			if (count > 1) {
				BaseUtil.showError("供应商[" + c[0] + "]只能存在一张已提交未审核的变更单");
			}
		}
		// 执行提交前的其它逻辑
		handlerService.handler("VendorChange", "commit", "before", new Object[] { vc_id });
		// 执行提交操作
		baseDao.submit("VendorChange", "vc_id=" + vc_id, "vc_status", "vc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "vc_id", vc_id);
		// 执行提交后的其它逻辑
		handlerService.handler("VendorChange", "commit", "after", new Object[] { vc_id });
	}

	@Override
	public void resSubmitVendorChange(int vc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("VendorChange", "vc_statuscode", "vc_id=" + vc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler("VendorChange", "resCommit", "before", new Object[] { vc_id });
		// 执行反提交操作
		baseDao.resOperate("VendorChange", "vc_id=" + vc_id, "vc_status", "vc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "vc_id", vc_id);
		handlerService.handler("VendorChange", "resCommit", "after", new Object[] { vc_id });
	}

	@Override
	public String[] printVendorChange(String caller, int vc_id, String reportName, String condition) {
		// 执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[] { vc_id });
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 记录操作
		baseDao.logger.print(caller, "vc_id", vc_id);
		// 执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[] { vc_id });
		return keys;
	}
}
