package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.VendorPerformanceAssessService;

/**
 * 标准界面的基本逻辑
 */
@Service
public class VendorPerformanceAssessSeriveImpl implements VendorPerformanceAssessService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveVPA(String caller, String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[] { store });
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "VendorPerformanceAssess", new String[] {},
				new Object[] {});
		baseDao.execute(formSql);
		// 合计各项得分
		baseDao.updateByCondition("VendorPerformanceAssess",
				"VPA_ZLZHDF = VPA_LLPBHGL + VPA_SXBLL + VPA_KHSXCS + VPA_CXZDYC + "
						+ "VPA_8DHFJSL + VPA_YCFFL + VPA_GFPHD,VPA_JSZHDF = VPA_WLSFFSJSZLWT+VPA_KKXYQ+VPA_GCSL+VPA_YPHGL+VPA_JSZLWZQK+VPA_JSFX+VPA_JSBZ+VPA_JSZC"
						+ ",VPA_CGKFZHDF=VPA_FWZC+VPA_DYZC+VPA_ZFTJ+VPA_CBJJ,VPA_CGZHDF=VPA_FWXL+VPA_NGPCL+VPA_DJHCS+VPA_WLJF",
				"vpa_id = "+store.get("vpa_id"));
		// 合计各权重得分
		baseDao.updateByCondition("VendorPerformanceAssess", "VPA_ZLQZDF = 0.3*VPA_ZLZHDF,VPA_JSQZDF"
				+ "=VPA_JSZHDF*0.3,VPA_CGJCGKFQZDF = VPA_CGKFZHDF*0.2+VPA_CGZHDF*0.2", "vpa_id = "+store.get("vpa_id"));
		// 合计总分
		baseDao.updateByCondition("VendorPerformanceAssess", "VPA_ZJDF=VPA_CGJCGKFQZDF+VPA_ZLQZDF+VPA_JSQZDF", "vpa_id = "+store.get("vpa_id"));
		baseDao.logger.save(caller, "vpa_id", store.get("vpa_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void updateVPA(String caller, String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[] { store });
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "VendorPerformanceAssess", "vpa_id");
		baseDao.execute(formSql);
		// 合计各项得分
		baseDao.updateByCondition("VendorPerformanceAssess",
				"VPA_ZLZHDF = VPA_LLPBHGL + VPA_SXBLL + VPA_KHSXCS + VPA_CXZDYC + "
						+ "VPA_8DHFJSL + VPA_YCFFL + VPA_GFPHD,VPA_JSZHDF = VPA_WLSFFSJSZLWT+VPA_KKXYQ+VPA_GCSL+VPA_YPHGL+VPA_JSZLWZQK+VPA_JSFX+VPA_JSBZ+VPA_JSZC"
						+ ",VPA_CGKFZHDF=VPA_FWZC+VPA_DYZC+VPA_ZFTJ+VPA_CBJJ,VPA_CGZHDF=VPA_FWXL+VPA_NGPCL+VPA_DJHCS+VPA_WLJF",
						"vpa_id = "+store.get("vpa_id"));
		// 合计各权重得分
		baseDao.updateByCondition("VendorPerformanceAssess", "VPA_ZLQZDF = 0.3*VPA_ZLZHDF,VPA_JSQZDF"
				+ "=VPA_JSZHDF*0.3,VPA_CGJCGKFQZDF = VPA_CGKFZHDF*0.2+VPA_CGZHDF*0.2", "vpa_id = "+store.get("vpa_id"));
		// 合计总分
		baseDao.updateByCondition("VendorPerformanceAssess", "VPA_ZJDF=VPA_CGJCGKFQZDF+VPA_ZLQZDF+VPA_JSQZDF", "vpa_id = "+store.get("vpa_id"));
		// 记录操作
		baseDao.logger.update(caller, "vpa_id", store.get("vpa_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void deleteVPA(String caller, int id) {
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { id });
		// 删除主表内容
		baseDao.deleteById("VendorPerformanceAssess", "vpa_id", id);
		baseDao.logger.delete(caller, "vpa_id", id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { id });
	}

	@Override
	public void auditVPA(int id, String caller) {
		// 只能对已提交进行审核操作
		Object status = baseDao.getFieldDataByCondition("VendorPerformanceAssess", "vpa_statuscode", "vpa_id=" + id);
		StateAssert.auditOnlyCommited(status);
		// 审核前合计各项数值
		// 合计各项得分
		baseDao.updateByCondition("VendorPerformanceAssess",
				"VPA_ZLZHDF = VPA_LLPBHGL + VPA_SXBLL + VPA_KHSXCS + VPA_CXZDYC + "
						+ "VPA_8DHFJSL + VPA_YCFFL + VPA_GFPHD,VPA_JSZHDF = VPA_WLSFFSJSZLWT+VPA_KKXYQ+VPA_GCSL+VPA_YPHGL+VPA_JSZLWZQK+VPA_JSFX+VPA_JSBZ+VPA_JSZC"
						+ ",VPA_CGKFZHDF=VPA_FWZC+VPA_DYZC+VPA_ZFTJ+VPA_CBJJ,VPA_CGZHDF=VPA_FWXL+VPA_NGPCL+VPA_DJHCS+VPA_WLJF",
						"vpa_id = "+id);
		// 合计各权重得分
		baseDao.updateByCondition("VendorPerformanceAssess", "VPA_ZLQZDF = 0.3*VPA_ZLZHDF,VPA_JSQZDF"
				+ "=VPA_JSZHDF*0.3,VPA_CGJCGKFQZDF = VPA_CGKFZHDF*0.2+VPA_CGZHDF*0.2", "vpa_id = "+id);
		// 合计总分
		baseDao.updateByCondition("VendorPerformanceAssess", "VPA_ZJDF=VPA_CGJCGKFQZDF+VPA_ZLQZDF+VPA_JSQZDF", "vpa_id = "+id);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { id });
		baseDao.audit("VendorPerformanceAssess", "vpa_id=" + id, "vpa_status", "vpa_statuscode", "vpa_auditdate",
				"vpa_auditor");
		// 更新上传状态
		baseDao.updateByCondition("VendorPerformanceAssess", "vpa_sendstatus='待上传'", "vpa_id = "+id);
		// 记录操作
		baseDao.logger.audit(caller, "vpa_id", id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { id });
	}

	@Override
	public void resAuditVPA(String caller, int id) {
		// 只能对状态为[已审核]的表单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("VendorPerformanceAssess", "vpa_statuscode", "vpa_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("VendorPerformanceAssess", "vpa_id=" + id, "vpa_status", "vpa_statuscode", "vpa_auditdate",
				"vpa_auditor");
		baseDao.resOperate("VendorPerformanceAssess", "vpa_id=" + id, "vpa_status", "vpa_statuscode");
		// 更新上传状态
		baseDao.updateByCondition("VendorPerformanceAssess", "vpa_sendstatus=null", "vpa_id = "+id);
		// 记录操作
		baseDao.logger.resAudit(caller, "vpa_id", id);
	}

	@Override
	public void submitVPA(String caller, int id) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("VendorPerformanceAssess", "vpa_statuscode", "vpa_id=" + id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { id });
		// 执行提交操作
		baseDao.submit("VendorPerformanceAssess", "vpa_id=" + id, "vpa_status", "vpa_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "vpa_id", id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { id });
		// 提交后合计各项数值
		// 合计各项得分
		baseDao.updateByCondition("VendorPerformanceAssess",
				"VPA_ZLZHDF = VPA_LLPBHGL + VPA_SXBLL + VPA_KHSXCS + VPA_CXZDYC + "
						+ "VPA_8DHFJSL + VPA_YCFFL + VPA_GFPHD,VPA_JSZHDF = VPA_WLSFFSJSZLWT+VPA_KKXYQ+VPA_GCSL+VPA_YPHGL+VPA_JSZLWZQK+VPA_JSFX+VPA_JSBZ+VPA_JSZC"
						+ ",VPA_CGKFZHDF=VPA_FWZC+VPA_DYZC+VPA_ZFTJ+VPA_CBJJ,VPA_CGZHDF=VPA_FWXL+VPA_NGPCL+VPA_DJHCS+VPA_WLJF",
						"vpa_id = "+id);
		// 合计各权重得分
		baseDao.updateByCondition("VendorPerformanceAssess", "VPA_ZLQZDF = 0.3*VPA_ZLZHDF,VPA_JSQZDF"
				+ "=VPA_JSZHDF*0.3,VPA_CGJCGKFQZDF = VPA_CGKFZHDF*0.2+VPA_CGZHDF*0.2", "vpa_id = "+id);
		// 合计总分
		baseDao.updateByCondition("VendorPerformanceAssess", "VPA_ZJDF=VPA_CGJCGKFQZDF+VPA_ZLQZDF+VPA_JSQZDF", "vpa_id = "+id);
	}

	@Override
	public void resSubmitVPA(String caller, int id) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("VendorPerformanceAssess", "vpa_statuscode", "vpa_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { id });
		// 执行反提交操作
		baseDao.resOperate("VendorPerformanceAssess", "vpa_id=" + id, "vpa_status", "vpa_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "vpa_id", id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { id });
	}
}
