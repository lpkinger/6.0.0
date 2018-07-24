package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.SendNotifyChangeDao;
import com.uas.erp.service.scm.SendNotifyChangeService;

@Service("sendNotifyChangeService")
public class SendNotifyChangeServiceImpl implements SendNotifyChangeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private SendNotifyChangeDao sendNotifyChangeDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveSendNotifyChange(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("SendNotifyChange", "sc_code='" + store.get("sc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		// 保存SendNotifyChange
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "SendNotifyChange", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存SendNotifyChangeDetail
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "SendNotifyChangeDetail", "scd_id");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "sc_id", store.get("sc_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, grid });
	}

	@Override
	public void deleteSendNotifyChange(int sc_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("SendNotifyChange", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { sc_id });
		// 删除SendNotifyChange
		baseDao.deleteById("SendNotifyChange", "sc_id", sc_id);
		// 删除SendNotifyChangeDetail
		baseDao.deleteById("SendNotifyChangedetail", "scd_scid", sc_id);
		// 记录操作
		baseDao.logger.delete(caller, "sc_id", sc_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { sc_id });
	}

	@Override
	public void updateSendNotifyChangeById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("SendNotifyChange", "sc_statuscode", "sc_id=" + store.get("sc_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		// 修改Inquiry
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "SendNotifyChange", "sc_id");
		baseDao.execute(formSql);
		// 修改InquiryDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "SendNotifyChangeDetail", "scd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("scd_id") == null || s.get("scd_id").equals("") || s.get("scd_id").equals("0")
					|| Integer.parseInt(s.get("scd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("SENDNOTIFYCHANGEDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "SendNotifyChangeDetail", new String[] { "scd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "sc_id", store.get("sc_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
	}

	@Override
	public String[] printSendNotifyChange(int sc_id, String caller, String reportName, String condition) {
		// 执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[] { sc_id });
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 记录操作
		baseDao.logger.print(caller, "sc_id", sc_id);
		// 执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[] { sc_id });
		return keys;
	}

	@Override
	public void auditSendNotifyChange(int sc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("SendNotifyChange", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.auditOnlyCommited(status);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(sc_code) from SendNotifyChange left join SendNotifyChangedetail on sc_id=scd_scid where nvl(sc_statuscode,' ')='COMMITED' and sc_id<>?"
						+ " and (sc_sncode,scd_snddetno) in (select sc_sncode,scd_snddetno from  SendNotifyChange left join SendNotifyChangedetail on sc_id=scd_scid where sc_id=?)"
								, String.class, sc_id, sc_id);
		if (dets != null) {
			BaseUtil.showError("有已提交的销售通知变更单，不允许审核!变更单号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(scd_detno) from SendNotifyChange left join SendNotifyChangedetail on sc_id=scd_scid where  sc_id=? and  not exists (select snd_code,snd_pdno from SendNotifyDetail where snd_code=sc_sncode and snd_pdno=scd_snddetno and snd_statuscode='AUDITED') "
								, String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("通知单号+通知单行号不存在或者状态不等于已审核，不允许审核!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(scd_detno) from SendNotifyChangeDetail left join SendNotifydetail on scd_sndid=snd_id where scd_scid=? and nvl(scd_qty,0)<nvl(snd_yqty,0)"
								, String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("新数量小于通知单已转数，不允许审核!行号：" + dets);
		}
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { sc_id });
		// 信息自动反馈到通知单
		String sncode = sendNotifyChangeDao.turnSendNotify(sc_id);
		// 执行审核操作
		baseDao.audit("SendNotifyChange", "sc_id=" + sc_id, "sc_status", "sc_statuscode", "sc_auditdate", "sc_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "sc_id", sc_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { sc_id });
		BaseUtil.showErrorOnSuccess("信息已自动反馈到通知单&nbsp;&nbsp;"
				+ "<a href=\"javascript:openUrl('jsps/scm/sale/sendNotify.jsp?formCondition=sn_codeIS" + sncode
				+ "&gridCondition=snd_codeIS" + sncode + "')\">点击查看</a>&nbsp;");
	}

	@Override
	public void resAuditSendNotifyChange(int sc_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("SendNotifyChange", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("SendNotifyChange", "sc_id=" + sc_id, "sc_status", "sc_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "sc_id", sc_id);
	}

	@Override
	public void submitSendNotifyChange(int sc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("SendNotifyChange", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.submitOnlyEntering(status);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(sc_code) from SendNotifyChange left join SendNotifyChangedetail on sc_id=scd_scid where nvl(sc_statuscode,' ')='COMMITED' and sc_id<>?"
						+ " and (sc_sncode,scd_snddetno) in (select sc_sncode,scd_snddetno from  SendNotifyChange left join SendNotifyChangedetail on sc_id=scd_scid where sc_id=?)"
								, String.class, sc_id, sc_id);
		if (dets != null) {
			BaseUtil.showError("有已提交的销售通知变更单，不允许提交!变更单号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(scd_detno) from SendNotifyChange left join SendNotifyChangedetail on sc_id=scd_scid where sc_id=? and not exists(select snd_code,snd_pdno from SendNotifyDetail where snd_code=sc_sncode and snd_pdno=scd_snddetno and snd_statuscode='AUDITED') "
								, String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("通知单号+通知单行号不存在或者状态不等于已审核，不允许提交!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(scd_detno) from SendNotifyChangeDetail left join SendNotifydetail on scd_sndid=snd_id where scd_scid=? and nvl(scd_qty,0)<nvl(snd_yqty,0)"
								, String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("新数量小于通知单已转数，不允许提交!行号：" + dets);
		}
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { sc_id });
		// 执行提交操作
		baseDao.submit("SendNotifyChange", "sc_id=" + sc_id, "sc_status", "sc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "sc_id", sc_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { sc_id });
	}

	@Override
	public void resSubmitSendNotifyChange(int sc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("SendNotifyChange", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { sc_id });
		// 执行反提交操作
		baseDao.resOperate("SendNotifyChange", "sc_id=" + sc_id, "sc_status", "sc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "sc_id", sc_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { sc_id });
	}
}
