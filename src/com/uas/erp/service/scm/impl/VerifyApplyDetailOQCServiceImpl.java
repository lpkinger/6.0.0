package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.VerifyApplyDetailOQCService;

@Service
public class VerifyApplyDetailOQCServiceImpl implements VerifyApplyDetailOQCService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveVerifyApplyDetailOQC(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("QUA_VerifyApplyDetail", "ve_code='" + store.get("ve_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "QUA_VerifyApplyDetail", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.logger.save(caller, "ve_id", store.get("ve_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void updateVerifyApplyDetailOQCById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("QUA_VerifyApplyDetail", "ve_statuscode", "ve_id=" + store.get("ve_id"));
		StateAssert.updateOnlyEntering(status);
		Integer vad_qty = Integer.valueOf(store.get("vad_qty").toString());
		Object[] objects = null;
		if("销售订单".equals(store.get("ve_sourcetype"))){
			objects = baseDao.getFieldsDataByCondition("saledetail", new String[] { "sd_oqcyqty",
			"sd_id" }, "sd_detno=" + store.get("vad_detno") + " and sd_code='" + store.get("vad_code") + "'");
		} else if ("出货通知单".equals(store.get("ve_sourcetype"))){
			objects = baseDao.getFieldsDataByCondition("sendnotifydetail", new String[] { "snd_oqcyqty",
			"snd_id" }, "snd_pdno=" + store.get("ve_senddetno") + " and snd_code='" + store.get("ve_sendcode") + "'");
		}
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "QUA_VerifyApplyDetail", "ve_id");
		baseDao.execute(formSql);
		if(objects !=null ){
			if("销售订单".equals(store.get("ve_sourcetype"))){
				baseDao.updateByCondition("saledetail", "sd_oqcyqty=nvl(sd_oqcyqty,0)+" + vad_qty + "-" + Double.valueOf(objects[0].toString()),
						"sd_id=" + objects[1]);
			} else if ("出货通知单".equals(store.get("ve_sourcetype"))){
				baseDao.updateByCondition("sendnotifydetail", "snd_oqcyqty=nvl(snd_oqcyqty,0)+" + vad_qty + "-" + Double.valueOf(objects[0].toString()),
						"snd_id=" + objects[1]);
			}
		}
		// 记录操作
		baseDao.logger.update(caller, "ve_id", store.get("ve_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void deleteVerifyApplyDetailOQC(int ve_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("QUA_VerifyApplyDetail", "ve_statuscode", "ve_id=" + ve_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { ve_id });
		// 删除QUA_VerifyApplyDetail
		Object[] objects = baseDao.getFieldsDataByCondition("QUA_VerifyApplyDetail", new String[] { "vad_qty", "vad_code",
				"vad_detno", "ve_sourcetype", "ve_sendcode", "ve_senddetno" }, "ve_id=" + ve_id);
		baseDao.deleteById("QUA_VerifyApplyDetail", "ve_id", ve_id);
		if (objects[0] != null) {
			if("销售订单".equals(objects[3])){
				Object sdid = baseDao.getFieldDataByCondition("saledetail", "sd_id", "sd_detno=" + objects[2] + " and sd_code='" + objects[1] + "'");
				baseDao.updateByCondition("saledetail", "sd_oqcyqty=nvl(sd_oqcyqty,0)-" + Double.valueOf(objects[0].toString()), "sd_id="
						+ sdid);
			} else if("出货通知单".equals(objects[3])){
				Object sndid = baseDao.getFieldDataByCondition("sendnotifydetail", "snd_id", "snd_pdno=" + objects[5] + " and snd_code='" + objects[4] + "'");
				baseDao.updateByCondition("sendnotifydetail", "snd_oqcyqty=nvl(snd_oqcyqty,0)-" + Double.valueOf(objects[0].toString()), "snd_id="
						+ sndid);
			}
		}
		// 记录操作
		baseDao.logger.delete(caller, "ve_id", ve_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { ve_id });

	}

	@Override
	public void auditVerifyApplyDetailOQC(int ve_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("QUA_VerifyApplyDetail", "ve_statuscode", "ve_id=" + ve_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { ve_id });
		// 执行审核操作
		baseDao.audit("QUA_VerifyApplyDetail", "ve_id=" + ve_id, "ve_status", "ve_statuscode", "VE_AUDITDATE", "VE_AUDITMAN");
		// 记录操作
		baseDao.logger.audit(caller, "ve_id", ve_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { ve_id });
	}

	@Override
	public void resAuditVerifyApplyDetailOQC(int ve_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("QUA_VerifyApplyDetail", "ve_statuscode", "ve_id=" + ve_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("QUA_VerifyApplyDetail", "ve_id=" + ve_id, "ve_status", "ve_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "ve_id", ve_id);
	}

	@Override
	public void submitVerifyApplyDetailOQC(int ve_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("QUA_VerifyApplyDetail", "ve_statuscode", "ve_id=" + ve_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { ve_id });
		// 执行提交操作
		baseDao.submit("QUA_VerifyApplyDetail", "ve_id=" + ve_id, "ve_status", "ve_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "ve_id", ve_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { ve_id });
	}

	@Override
	public void resSubmitVerifyApplyDetailOQC(int ve_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("QUA_VerifyApplyDetail", "ve_statuscode", "ve_id=" + ve_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { ve_id });
		// 执行反提交操作
		baseDao.resOperate("QUA_VerifyApplyDetail", "ve_id=" + ve_id, "ve_status", "ve_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ve_id", ve_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { ve_id });

	}

	@Override
	public void updatePMC(Integer id, String pmc, String caller) {
		baseDao.updateByCondition("Qua_verifyApplydetail", "ve_makedate=to_date('" + pmc + "', 'yyyy-mm-dd')", "ve_id=" + id);
		// 记录操作
		baseDao.logger.others("修改PMC回复日期", "msg.updateSuccess", caller, "ve_id", id);
	}
}
