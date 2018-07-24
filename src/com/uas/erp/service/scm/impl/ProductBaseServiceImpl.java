package com.uas.erp.service.scm.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.ProductDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.scm.ProductBaseService;

@Service
public class ProductBaseServiceImpl implements ProductBaseService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private ProductDao productDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProductBase(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Product", "pr_code='" + store.get("pr_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		store.put("pr_checkstatuscode", "UNAPPROVED");
		store.put("pr_checkstatus", BaseUtil.getLocalMessage("UNAPPROVED"));
		store.put("pr_admitstatuscode", "UNADMIT");
		store.put("pr_admitstatus", BaseUtil.getLocalMessage("UNADMIT"));
		checkProdCode(store.get("pr_code"), store.get("pr_id"), store.get("pr_sourcecode"));
		checkProdName(store.get("pr_code"), store.get("pr_detail"), store.get("pr_spec"), store.get("pr_speccs"));
		// 保存product
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Product", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		boolean bool2 = baseDao.checkByCondition("Productonhand", "po_id=" + store.get("pr_id"));
		if (bool2) {
			baseDao.execute("insert into productonhand set po_id=" + store.get("pr_id") + ", po_prodcode='" + store.get("pr_code") + "'");
		}
		baseDao.logger.save(caller, "pr_id", store.get("pr_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	private void checkProdCode(Object prcode, Object prid, Object pr_sourcecode) {
		// 判断物料编号在物料资料中是否存在重复
		if (prcode == null || prcode.equals("") || prcode.toString().contains(" ")) {
			BaseUtil.showError("物料编号为空或者带空格");
		}
		// 替换物料编号中为制表符
		baseDao.execute("update product set pr_code=replace(pr_code,chr(9),'') where pr_id=" + prid);
		// 将物料编号转换为大写
		baseDao.execute("update product set pr_code=upper(pr_code) where pr_id=" + prid);
		String dets = baseDao.getJdbcTemplate().queryForObject("select WM_CONCAT(pr_code) from Product where pr_code=? and pr_id<>?",
				String.class, prcode, prid);
		if (dets != null) {
			BaseUtil.showError("物料编号在物料资料表中已存在!物料编号：" + dets);
		}
		// 判断物料编号在新物料申请资料中是否存在重复
		dets = baseDao.getJdbcTemplate().queryForObject("select WM_CONCAT(pre_thisid) from PreProduct where pre_code=? and pre_thisid<>?",
				String.class, prcode, pr_sourcecode);
		if (dets != null) {
			BaseUtil.showError("物料编号在新物料申请资料表中已存在!申请单号：" + dets);
		}
	}

	private void checkProdName(Object prcode, Object prname, Object prspec, Object parameter) {
		// 判断名称规格是否存在重复
		String code = baseDao.getFieldValue("Product", "pr_code", "pr_code <> '" + StringUtil.nvl(prcode, " ")
				+ "' AND nvl(pr_detail,' ')='" + StringUtil.nvl(prname, " ") + "' and nvl(pr_spec,' ')='" + StringUtil.nvl(prspec, " ")
				+ "' and nvl(pr_speccs,' ')='" + StringUtil.nvl(parameter, " ") + "' and nvl(pr_statuscode,' ')<>'DISABLE'", String.class);
		if (code != null) {
			BaseUtil.showError("在物料资料中已经存在相同规格名称的物料，物料编号:" + code + "<hr>");
		}
		// 判断名称规格是否存在重复
		code = baseDao.getFieldValue("PreProduct", "pre_thisid",
				"pre_code <> '" + StringUtil.nvl(prcode, " ") + "' AND pre_detail='" + StringUtil.nvl(prname, " ") + "' and pre_spec='"
						+ StringUtil.nvl(prspec, " ") + "' and pre_parameter='" + StringUtil.nvl(parameter, " ") + "'", String.class);
		if (code != null) {
			BaseUtil.showError("在新物料申请资料中已经存在相同规格名称的申请，申请单号:" + code + "<hr>");
		}
		if (StringUtil.hasText(prspec)) {
			// 提示判断规格是否存在重复
			code = baseDao.getFieldValue("Product", "pr_code", "pr_code <> '" + StringUtil.nvl(prcode, " ") + "' and pr_spec='"
					+ StringUtil.nvl(prspec, " ") + "' and nvl(pr_statuscode,' ')<>'DISABLE'", String.class);
			if (code != null) {
				BaseUtil.appendError("在物料资料中已经存在相同规格的物料，物料编号:" + code + "<hr>");
			}
			code = baseDao.getFieldValue("PreProduct", "pre_thisid", "pre_code <> '" + StringUtil.nvl(prcode, " ") + "' and pre_spec='"
					+ StringUtil.nvl(prspec, " ") + "'", String.class);
			if (code != null) {
				BaseUtil.appendError("在新物料申请资料中已经存在相同规格的申请，申请单号:" + code + "<hr>");
			}
		}
	}

	@Override
	public void deleteProductBase(int pr_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_id=" + pr_id);
		StateAssert.delOnlyEntering(status);
		// 是否已产生业务数据
		baseDao.delCheck("product", pr_id);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, pr_id);
		// 删除
		baseDao.execute("delete from Productonhand where po_prodcode=(select pr_code from product where pr_id=" + pr_id + ")");
		baseDao.execute("delete from PRODFEATURE where PF_PRID=" + pr_id);
		// 删除
		baseDao.deleteById("Product", "pr_id", pr_id);
		// 记录操作
		baseDao.logger.delete(caller, "pr_id", pr_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, pr_id);
	}

	@Override
	public void updateProductBaseById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_id=" + store.get("pr_id"));
		StateAssert.updateOnlyEntering(status);
		checkProdCode(store.get("pr_code"), store.get("pr_id"), store.get("pr_sourcecode"));
		checkProdName(store.get("pr_code"), store.get("pr_detail"), store.get("pr_spec"), store.get("pr_speccs"));
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Product", "pr_id");
		baseDao.execute(formSql);
		baseDao.execute("update product set pr_purcrate=1 where pr_id=" + store.get("pr_id") + " and nvl(pr_purcrate,0)=0 ");
		// 接收方式修改为检验{0}时,生成检验单
		// 记录操作
		baseDao.logger.update(caller, "pr_id", store.get("pr_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void auditProductBase(int pr_id, String caller) {
		baseDao.execute("update product set pr_purcrate=1 where pr_id=" + pr_id + " and nvl(pr_purcrate,0)=0 ");
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("Product", new String[] { "pr_statuscode", "pr_code", "pr_detail", "pr_spec",
				"pr_speccs", "pr_sourcecode" }, "pr_id=" + pr_id);
		StateAssert.auditOnlyCommited(status[0]);
		checkProdCode(status[1], pr_id, status[5]);
		checkProdName(status[1], status[2], status[2], status[4]);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, pr_id);
		// 执行审核操作
		baseDao.audit("Product", "pr_id=" + pr_id, "pr_status", "pr_statuscode", "pr_auditdate", "pr_auditman");
		Employee employee = SystemSession.getUser();
		if (baseDao.isDBSetting("PreProduct", "autoSync")) {
			Object custatus = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_id=" + pr_id);
			if (custatus != null && "AUDITED".equals(custatus)) {
				Master master = employee.getCurrentMaster();
				if (master != null && master.getMa_soncode() != null) {// 资料中心
					String res = null;
					res = baseDao.callProcedure("SYS_POST", new Object[] { "Product!Post", SpObserver.getSp(), master.getMa_soncode(),
							String.valueOf(pr_id), employee.getEm_name(), employee.getEm_id() });
					if (res != null) {
						BaseUtil.appendError(res);
					}
				}
			}
		}
		// 记录操作
		baseDao.logger.audit(caller, "pr_id", pr_id);
		// 执行保存后的其它逻辑
		handlerService.afterAudit(caller, pr_id);
	}

	@Override
	public void resAuditProductBase(int pr_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_id=" + pr_id);
		StateAssert.resAuditOnlyAudit(status);
		// 是否已产生业务数据
		baseDao.resAuditCheck("product", pr_id);
		// 执行反审核操作
		baseDao.resAudit("Product", "pr_id=" + pr_id, "pr_status", "pr_statuscode", "pr_auditdate", "pr_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "pr_id", pr_id);
	}

	@Override
	public void submitProductBase(int pr_id, String caller) {
		baseDao.execute("update product set pr_purcrate=1 where pr_id=" + pr_id + " and nvl(pr_purcrate,0)=0 ");
		// 只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("Product", new String[] { "pr_statuscode", "pr_code", "pr_detail", "pr_spec",
				"pr_speccs", "pr_sourcecode" }, "pr_id=" + pr_id);
		StateAssert.submitOnlyEntering(status[0]);
		checkProdCode(status[1], pr_id, status[5]);
		checkProdName(status[1], status[2], status[2], status[4]);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pr_id);
		// 执行提交操作
		baseDao.submit("Product", "pr_id=" + pr_id, "pr_status", "pr_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pr_id", pr_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pr_id);
	}

	@Override
	public void resSubmitProductBase(int pr_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_id=" + pr_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, pr_id);
		// 执行反提交操作
		baseDao.resOperate("Product", "pr_id=" + pr_id, "pr_status", "pr_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pr_id", pr_id);
		handlerService.afterResSubmit(caller, pr_id);
	}

	@Override
	public void bannedProduct(int pr_id, String caller) {
		// 只能禁用未删除的单据!
		Object status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_id=" + pr_id);
		if ("DELETED".equals(status)) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.banned_onlyCanuse"));
		}
		// 执行禁用前的其它逻辑
		handlerService.handler(caller, "banned", "before", new Object[] { pr_id });
		// 禁用(修改物料状态为已禁用)
		baseDao.banned("Product", "pr_id=" + pr_id, "pr_status", "pr_statuscode");
		Employee employee = SystemSession.getUser();
		if (baseDao.isDBSetting("PreProduct", "autoSync")) {
			Object custatus = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_id=" + pr_id);
			if (custatus != null && "AUDITED".equals(custatus)) {
				Master master = employee.getCurrentMaster();
				if (master != null && master.getMa_soncode() != null) {// 资料中心
					String res = null;
					res = baseDao.callProcedure("SYS_POST", new Object[] { "Product!Post", SpObserver.getSp(), master.getMa_soncode(),
							String.valueOf(pr_id), employee.getEm_name(), employee.getEm_id() });
					if (res != null) {
						BaseUtil.appendError(res);
					}
				}
			}
		}
		// 记录操作
		baseDao.logger.banned(caller, "pr_id", pr_id);
		// 执行禁用后的其它逻辑
		handlerService.handler(caller, "banned", "after", new Object[] { pr_id });
	}

	@Override
	public void resBannedProduct(int pr_id, String caller) {
		// 只能反禁用已禁用的单据!
		Object status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_id=" + pr_id);
		if (!"DISABLE".equals(status)) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resBanned_onlyBanned"));
		}
		// 执行反禁用前的其它逻辑
		handlerService.handler(caller, "resBanned", "before", new Object[] { pr_id });
		// 反禁用(修改物料状态为在录入)
		baseDao.resOperate("Product", "pr_id=" + pr_id, "pr_status", "pr_statuscode");
		// 记录操作
		baseDao.logger.resBanned(caller, "pr_id", pr_id);
		// 执行反禁用后的其它逻辑
		handlerService.handler(caller, "resBanned", "after", new Object[] { pr_id });
	}

	@Override
	public int copyProduct(int pr_id, String caller, String newcode, String newname, String newspec) {
		Employee employee = SystemSession.getUser();
		int prid = 0;
		String pr_oldcode = baseDao.getJdbcTemplate().queryForObject("select pr_code from Product where pr_id=?", String.class, pr_id);
		prid = baseDao.getSeqId("PRODUCT_SEQ");
		if (newcode == null || newcode.equals("") || newcode.toString().contains(" ")) {
			BaseUtil.showError("物料编号为空或者带空格");
		}
		String prcode = newcode.toUpperCase();
		String dets = baseDao.getJdbcTemplate().queryForObject("select WM_CONCAT(pr_code) from Product where pr_code=? and pr_id<>?",
				String.class, prcode, prid);
		if (dets != null) {
			BaseUtil.showError("物料编号在物料资料表中已存在!物料编号：" + dets);
		}
		// 判断物料编号在新物料申请资料中是否存在重复
		dets = baseDao.getJdbcTemplate().queryForObject("select WM_CONCAT(pre_thisid) from PreProduct where pre_code=?", String.class,
				prcode);
		if (dets != null) {
			BaseUtil.showError("物料编号在新物料申请资料表中已存在!申请单号：" + dets);
		}
		
		//复制时考虑参数配置: 允许物料名称+规格+规格参数重复。     反馈编号：2018060016   @author:wuyx
		String checkProdName = baseDao.getDBSetting("Product", "checkProdName");
		dets = baseDao.getJdbcTemplate()
				.queryForObject("select WM_CONCAT(pr_code) from Product where pr_detail=? and pr_spec=? and pr_id<>?", String.class,
						newname, newspec, prid);
		if (dets != null) {
			if(checkProdName==null||"0".equals(checkProdName)){
				BaseUtil.showError("物料名称+物料规格在物料资料表中已存在!物料编号：" + dets);
			}else{
				BaseUtil.appendError("物料名称+物料规格在物料资料表中已存在!物料编号：" + dets);
			}
		}
		// 物料复制
		Map<String, Object> diffence = new HashMap<String, Object>();
		diffence.put("pr_id", prid);
		diffence.put("pr_code", "'" + prcode + "'");
		diffence.put("pr_detail", "'" + newname + "'");
		diffence.put("pr_spec", "'" + newspec + "'");
		diffence.put("pr_docdate", "sysdate");
		diffence.put("pr_recordman", "'" + employee.getEm_name() + "'");
		diffence.put("pr_statuscode", "'ENTERING'");
		diffence.put("pr_status", "'" + BaseUtil.getLocalMessage("ENTERING") + "'");
		diffence.put("pr_sourcecode", "null");
		diffence.put("pr_sqr", "'" + employee.getEm_name() + "'");
		diffence.put("pr_sendstatus", "'待上传'");
		diffence.put("pr_mrponhand", 0);
		diffence.put("pr_mrponorder", 0);
		diffence.put("pr_mrpcommited", 0);
		diffence.put("pr_material", "'未认可'");
		diffence.put("pr_admitstatus", "null");
		// 转入主表
		baseDao.copyRecord("Product", "Product", "pr_id=" + pr_id, diffence);
		handlerService.handler(caller, "copy", "after", new Object[] { pr_id, prid });
		// 记录操作
		baseDao.logger.copy(caller, pr_oldcode, "pr_id", prid);
		return prid;
	}

	@Override
	public void SubmitStandard(int id, String caller) {
		// 触发流程
		Object data = baseDao.getFieldDataByCondition("Product", "pr_standardized", "pr_id=" + id);
		String ProcessCaller = "ProductBin";
		if ("-1".equals(data)) {
			ProcessCaller = "ProductBout";
		}
		baseDao.updateByCondition("Product", "pr_standardstatus='COMMITED'", "pr_id=" + id);
		handlerService.handler(ProcessCaller, "commit", "after", new Object[] { id });
	}

	@Override
	public void resSubmitNoStandard(int id, String caller) {
		Object data = baseDao.getFieldDataByCondition("Product", "pr_standardized", "pr_id=" + id);
		baseDao.updateByCondition("Product", "pr_standardstatus='ENTERING'", "pr_id=" + id);
		String ProcessCaller = "ProductBin";
		if ("-1".equals(data)) {
			ProcessCaller = "ProductBout";
		}
		handlerService.handler(ProcessCaller, "resCommit", "after", new Object[] { id });
	}

	@Override
	public void saveCustprod(String param, String caller) {
		List<Map<Object, Object>> items = BaseUtil.parseGridStoreToMaps(param);
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(items, "CustomerProduct", "cp_id"));
		baseDao.logger.others("修改客户物料", "msg.updateSuccess", "CustomerProduct", "cp_custcode", items.get(0).get("cp_custcode"));
	}

	@Override
	public void updateStandard(int id, String caller) {
		Object data = baseDao.getFieldDataByCondition("Product", "pr_standardized", "pr_id=" + id);
		if ("-1".equals(data)) {
			baseDao.updateByCondition("Product", "pr_standardized=0", "pr_id=" + id);
		} else
			baseDao.updateByCondition("Product", "pr_standardized=-1", "pr_id=" + id);
	}
}
