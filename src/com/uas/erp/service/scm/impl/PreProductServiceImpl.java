package com.uas.erp.service.scm.impl;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.dao.common.PreProductDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Key;
import com.uas.erp.model.Master;
import com.uas.erp.service.scm.PreProductService;
import com.uas.erp.service.scm.ProductSampleService;

@Service("preProductService")
public class PreProductServiceImpl implements PreProductService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private PreProductDao preProductDao;

	@Autowired
	private HandlerService handlerService;

	@Autowired
	private TransferRepository transferRepository;

	@Autowired
	private ProductSampleService productSampleService;

	@Override
	public void savePreProduct(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("PreProduct", "pre_code='" + store.get("pre_code") + "'");
		if (!bool) {
			// BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
			BaseUtil.showError("当前物料编号记录已存在,不能保存!");
		} else {
			bool = baseDao.checkByCondition("Product", "pr_code='" + store.get("pre_code") + "'");
			if (!bool) {
				// BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
				BaseUtil.showError("当前物料编号记录已存在,不能保存!");
			}
		}
		store.put("pre_code", store.get("pre_code").toString().replace("\t", ""));// 替换物料名称特殊字符TAB
		if (store.get("pre_code").toString().contains(" ")) {
			BaseUtil.showError("物料编号不能带空格");
		}
		// 检查物料编码长度是否符合大类中的长度规则设置
		checkCodeLength(store);
		checkProdCode(store.get("pre_id"), store.get("pre_code"));
		checkProdName(store.get("pre_thisid"), store.get("pre_code"), store.get("pre_detail"), store.get("pre_spec"),
				store.get("pre_parameter"));
		String kindname = store.get("pre_kind").toString();
		if (!StringUtil.hasText(store.get("pre_acceptmethod"))) {
			Object acceptmethod = baseDao.getFieldDataByCondition("ProductKind", "pk_acceptmethod", "pk_name='" + kindname + "'");
			if (acceptmethod != null) {
				store.put("pre_acceptmethod", acceptmethod);
			}
		}
		// 存货科目，销售科目，成本科目 只能是末级科目
		checkLeafCategory(store.get("pre_stockcatecode"));
		checkLeafCategory(store.get("pre_costcatecode"));
		checkLeafCategory(store.get("pre_incomecatecode"));
		// 执行保存前的其它逻辑
		handlerService.beforeSave("PreProduct", new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PreProduct", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 根据大中小类获取湿敏等级
		if (!StringUtil.hasText(store.get("pre_msdlevel"))) {
			getMsdlevel(store.get("pre_kind"), store.get("pre_kind2"), store.get("pre_kind3"), store.get("pre_id"));
		}
		baseDao.execute("update PreProduct set pre_code=upper(pre_code) where pre_id=" + store.get("pre_id"));
		baseDao.logger.save("PreProduct", "pre_id", store.get("pre_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave("PreProduct", new Object[] { store });
	}

	/**
	 * 问题反馈编号:2016110967 新功能需求:易连资料中心账套
	 * 
	 * @author wsy
	 */
	private void checkCodeLength(Map<Object, Object> store) {
		Object pre_code = store.get("pre_code");
		Object pre_kind = store.get("pre_kind");
		if (pre_code != null && !"".equals(pre_code) && pre_kind != null && !"".equals(pre_kind)) {
			Object pk_codelength = baseDao.getFieldDataByCondition("ProductKind", "pk_codelength", "pk_name='" + pre_kind
					+ "' and pk_level=1");
			int length = pk_codelength == null ? 0 : Integer.parseInt(pk_codelength.toString());
			if (length != 0) {
				if (pre_code.toString().getBytes().length != Integer.parseInt(pk_codelength.toString())) {
					BaseUtil.showError("物料编码长度不符合大类中的长度规则设置，不能保存!");
				}
			}
		}
	}

	public void checkProdCode(Object pre_id, Object prcode) {
		// 替换物料编号中为制表符
		baseDao.execute("update PreProduct set pre_code=replace(pre_code,chr(9),'') where pre_id=" + pre_id);
		// 判断物料编号在物料资料中是否存在重复
		String dets = baseDao.getJdbcTemplate().queryForObject("select WM_CONCAT(pr_code) from Product where pr_code=?", String.class,
				prcode);
		if (dets != null) {
			BaseUtil.showError("物料编号在物料资料表中已存在!物料编号：" + dets);
		}
		// 判断物料编号在新物料申请资料中是否存在重复
		dets = baseDao.getJdbcTemplate().queryForObject("select WM_CONCAT(pre_thisid) from PreProduct where pre_code=? and pre_id<>?",
				String.class, prcode, pre_id);
		if (dets != null) {
			BaseUtil.showError("物料编号在新物料申请资料表中已存在!申请单号：" + dets);
		}
	}

	void checkCate(Object id) {
		SqlRowList rs = baseDao.queryForRowSet("select * from PreProduct where pre_id=?", new Object[] { id });
		if (rs.next()) {
			String error = null;
			if (StringUtil.hasText(rs.getObject("pre_stockcatecode"))) {
				error = baseDao.getJdbcTemplate().queryForObject(
						"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
						String.class, rs.getObject("pre_stockcatecode"));
				if (error != null) {
					BaseUtil.showError("填写的存货科目不存在，或者状态不等于已审核，或者不是末级科目！");
				}
				error = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code NOT IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('MonthAccount!scm','stockCatecode'), chr(10))))",
								String.class, rs.getObject("pre_stockcatecode"));
				if (error != null) {
					BaseUtil.showError("存货科目不是【系统参数设置-->供应链-->库存管理系统-->库存期末处理-->库存对账】中的存货科目！");
				}
			}
			if (StringUtil.hasText(rs.getObject("pre_incomecatecode"))) {
				error = baseDao.getJdbcTemplate().queryForObject(
						"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
						String.class, rs.getObject("pre_incomecatecode"));
				if (error != null) {
					BaseUtil.showError("填写的收入科目不存在，或者状态不等于已审核，或者不是末级科目！");
				}
				error = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code NOT IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('MakeCostClose','incomeCatecode'), chr(10))))",
								String.class, rs.getObject("pre_incomecatecode"));
				if (error != null) {
					BaseUtil.showError("收入科目不是【系统参数设置-->成本会计管理-->成本核算系统-->成本凭证制作-->主营业务成本结转凭证制作】中的主营业务收入科目！");
				}
			}
			if (StringUtil.hasText(rs.getObject("pre_costcatecode"))) {
				error = baseDao.getJdbcTemplate().queryForObject(
						"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
						String.class, rs.getObject("pre_costcatecode"));
				if (error != null) {
					BaseUtil.showError("填写的成本科目不存在，或者状态不等于已审核，或者不是末级科目！");
				}
				error = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code NOT IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('MakeCostClose','costCatecode'), chr(10))))",
								String.class, rs.getObject("pre_costcatecode"));
				if (error != null) {
					BaseUtil.showError("成本科目不是【系统参数设置-->成本会计管理-->成本核算系统-->成本凭证制作-->主营业务成本结转凭证制作】中的主营业务成本科目！");
				}
			}
		}
	}

	private void checkProdName(Object precode, Object prcode, Object prname, Object prspec, Object parameter) {
		// 判断名称规格是否存在重复
		prname = checkParam(StringUtil.nvl(prname, " "));
		prspec = checkParam(StringUtil.nvl(prspec, " "));
		parameter = checkParam(StringUtil.nvl(parameter, " "));
		// 获取物料资料中的参数配置: 允许物料名称+规格+规格参数重复。 反馈编号：2017120082 @author:lidy
		String checkProdName = baseDao.getDBSetting("Product", "checkProdName");
		String code = baseDao.getFieldValue("Product", "pr_code", "pr_code <> '" + StringUtil.nvl(prcode, " ") + "' AND pr_detail='"
				+ StringUtil.nvl(prname, " ") + "' and nvl(pr_spec,' ')='" + StringUtil.nvl(prspec, " ") + "' and nvl(pr_speccs,' ')='"
				+ StringUtil.nvl(parameter, " ") + "' and nvl(pr_statuscode,' ')<>'DISABLE'", String.class);
		if (code != null) {
			if (checkProdName == null || "0".equals(checkProdName)) {
				BaseUtil.showError("在物料资料中已经存在相同规格名称的物料，物料编号:" + code + "<hr>");
			} else {
				BaseUtil.appendError("在物料资料中已经存在相同规格名称的物料，物料编号:" + code + "<hr>");
			}
		}
		// 判断名称规格是否存在重复
		code = baseDao.getFieldValue("PreProduct", "pre_thisid",
				"pre_thisid <> '" + StringUtil.nvl(precode, " ") + "' AND pre_detail='" + StringUtil.nvl(prname, " ") + "' and pre_spec='"
						+ StringUtil.nvl(prspec, " ") + "' and pre_parameter='" + StringUtil.nvl(parameter, " ") + "'", String.class);
		if (code != null) {
			if (checkProdName == null || "0".equals(checkProdName)) {
				BaseUtil.showError("在新物料申请资料中已经存在相同规格名称的申请，申请单号:" + code + "<hr>");
			} else {
				BaseUtil.appendError("在新物料申请资料中已经存在相同规格名称的申请，申请单号:" + code + "<hr>");
			}
		}
		if (StringUtil.hasText(prspec)) {
			// 只提示判断规格是否存在重复
			code = baseDao.getFieldValue("Product", "pr_code", "pr_code <> '" + StringUtil.nvl(prcode, " ") + "' and nvl(pr_spec,' ')='"
					+ StringUtil.nvl(prspec, " ") + "' and nvl(pr_statuscode,' ')<>'DISABLE'", String.class);
			if (code != null) {
				BaseUtil.appendError("在物料资料中已经存在相同规格的物料，物料编号:" + code + "<hr>");
			}
			code = baseDao.getFieldValue("PreProduct", "pre_thisid", "pre_thisid <> '" + StringUtil.nvl(precode, " ") + "' and pre_spec='"
					+ StringUtil.nvl(prspec, " ") + "'", String.class);
			if (code != null) {
				BaseUtil.appendError("在新物料申请资料中已经存在相同规格的申请，申请单号:" + code + "<hr>");
			}
		}
	}

	private void checkLeafCategory(Object ca_code) {
		if (ca_code != null && ca_code.toString().length() > 0) {
			Object leaf = baseDao.getFieldDataByCondition("Category", "ca_isleaf", "ca_code='" + ca_code + "'");
			if (leaf == null) {
				BaseUtil.showError("科目" + ca_code + "不存在!");
			} else if ("0".equals(leaf)) {
				BaseUtil.showError("科目" + ca_code + "不是末级科目!");
			}
		}
	}

	@Override
	public void deletePreProduct(int pre_id) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("PreProduct", "pre_statuscode", "pre_id=" + pre_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel("PreProduct", pre_id);
		// 删除
		baseDao.deleteById("PreProduct", "pre_id", pre_id);
		// 记录操作
		baseDao.logger.delete("PreProduct", "pre_id", pre_id);
		int count = baseDao.getCount("select count(*) from user_tab_columns where table_name='PREPRODFEATURE'");
		if (count > 0) {
			baseDao.execute("delete from PREPRODFEATURE where PPF_PRID=" + pre_id);
		}
		// 执行删除后的其它逻辑
		handlerService.afterDel("PreProduct", pre_id);
	}

	@Override
	public void updatePreProductById(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的单据!
		Object status = baseDao.getFieldDataByCondition("PreProduct", "pre_statuscode", "pre_id=" + store.get("pre_id"));
		StateAssert.updateOnlyEntering(status);
		store.put("pre_code", store.get("pre_code").toString().replace("\t", ""));// 替换物料名称特殊字符TAB
		if (store.get("pre_code").toString().contains(" ")) {
			BaseUtil.showError("物料编号不能带空格");
		}
		// 检查物料编码长度是否符合大类中的长度规则设置
		checkCodeLength(store);
		checkProdCode(store.get("pre_id"), store.get("pre_code"));
		// 判断规格名称是否存在重复
		checkProdName(store.get("pre_thisid"), store.get("pre_code"), store.get("pre_detail"), store.get("pre_spec"),
				store.get("pre_parameter"));
		// 存货科目，销售科目，成本科目 只能是末级科目
		checkLeafCategory(store.get("pre_stockcatecode"));
		checkLeafCategory(store.get("pre_costcatecode"));
		checkLeafCategory(store.get("pre_incomecatecode"));
		String kindname = store.get("pre_kind").toString();
		if (!StringUtil.hasText(store.get("pre_acceptmethod"))) {
			Object acceptmethod = baseDao.getFieldDataByCondition("ProductKind", "pk_acceptmethod", "pk_name='" + kindname + "'");
			if (acceptmethod != null) {
				store.put("pre_acceptmethod", acceptmethod);
			}
		}
		// 执行修改前的其它逻辑
		handlerService.beforeSave("PreProduct", new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PreProduct", "pre_id");
		baseDao.execute(formSql);
		// 根据大中小类获取湿敏等级
		if (!StringUtil.hasText(store.get("pre_msdlevel"))) {
			getMsdlevel(store.get("pre_kind"), store.get("pre_kind2"), store.get("pre_kind3"), store.get("pre_id"));
		}
		baseDao.execute("update PreProduct set pre_code=upper(pre_code) where pre_id=" + store.get("pre_id"));
		baseDao.execute("update PreProduct set pre_purcrate=1 where pre_id=" + store.get("pre_id") + " and nvl(pre_purcrate,0)=0 ");
		baseDao.execute("update PreProduct set pre_purcunit=pre_unit where pre_id=" + store.get("pre_id")
				+ " and nvl(pre_purcunit,' ')=' '");
		// 记录操作
		baseDao.logger.update("PreProduct", "pre_id", store.get("pre_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave("PreProduct", new Object[] { store });
	}

	@Override
	public String auditPreProduct(int pre_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("PreProduct", "pre_statuscode", "pre_id=" + pre_id);
		StateAssert.auditOnlyCommited(status);
		Object prcode = baseDao.getFieldDataByCondition("PreProduct", "pre_code", "pre_id=" + pre_id);
		if (prcode == null || prcode.equals("")) {
			BaseUtil.showError("物料编号为空，不能审核");
		} else {
			if (prcode.toString().contains(" ")) {
				BaseUtil.showError("物料编号带空格，不能审核");
			}
		}
		baseDao.execute("update PreProduct set pre_code=upper(pre_code) where pre_id=" + pre_id);
		baseDao.execute("update PreProduct set pre_purcrate=1 where pre_id=" + pre_id + " and nvl(pre_purcrate,0)=0 ");
		SqlRowList rs = baseDao.queryForRowSet("select * from PreProduct where pre_id=?", pre_id);
		if (rs.next()) {
			checkProdCode(pre_id, rs.getObject("pre_code"));
			// 判断规格名称是否存在重复
			checkProdName(rs.getObject("pre_thisid"), rs.getObject("pre_code"), rs.getObject("pre_detail"), rs.getObject("pre_spec"),
					rs.getObject("pre_parameter"));
		}
		checkCate(pre_id);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit("PreProduct", pre_id);
		// 执行审核操作
		baseDao.audit("PreProduct", "pre_id=" + pre_id, "pre_status", "pre_statuscode", "pre_checkdate", "pre_checkman");
		// 记录操作
		baseDao.logger.audit("PreProduct", "pre_id", pre_id);
		turnFormal(pre_id);
		// 转打样申请 2018040423
		Object tosample = baseDao.getFieldDataByCondition("PreProduct", "pre_tosample", "pre_id=" + pre_id);
		int id = 0;
		String code = null;
		String log = null;
		if (tosample != null && "-1".equals(tosample)) {
			Employee emp = SystemSession.getUser();
			id = baseDao.getSeqId("productsample_seq");
			code = baseDao.sGetMaxNumber("ProductSample", 2);
			baseDao.execute("insert into ProductSample (ps_id,ps_code,ps_recordor,ps_indate,ps_status,ps_statuscode,ps_prodcode,ps_prodname,ps_prodspec,ps_unit)"
					+ "select "
					+ id
					+ ",'"
					+ code
					+ "','"
					+ emp.getEm_name()
					+ "',sysdate,'在录入','ENTERING',pre_code,pre_detail,pre_spec,pre_unit from PreProduct where pre_id=" + pre_id);
			productSampleService.submitProductSample(id, "ProductSample");
			log = "审核成功!已生成打样单:<a href=\"javascript:openUrl('jsps/scm/product/ProductSample.jsp?formCondition=ps_idIS" + id
					+ "&gridCondition=pd_psidIS" + id + "')\">" + code + "</a>&nbsp;";
		}
		if (baseDao.isDBSetting(caller, "insertBrand")) {
			SqlRowList rs1 = baseDao.queryForRowSet("select pre_brand from Preproduct a where pre_id=" + pre_id
					+ " and not exists(select 1 from productbrand where replace(upper(pb_name),' ','')=replace(upper(a.pre_brand),' ','')) and pre_brand is not null");
			if (rs1.next()) {
				baseDao.execute("Insert into productbrand (PB_ID,PB_NAME,PB_RECORDER,PB_RECORDDATE,PB_STATUS,PB_STATUSCODE,PB_AUDITOR,PB_AUDITDATE,PB_REMARK,PB_RATE) values (productbrand_seq.nextval,'"
						+ rs.getString("pre_brand")
						+ "','admin',sysdate,'已审核','AUDITED','admin',sysdate,'"
						+ rs.getString("pre_brand")
						+ "',0)");
			}
		}
		// 执行审核后的其它逻辑
		handlerService.afterAudit("PreProduct", pre_id);
		return log;
	}

	@Override
	public void resAuditPreProduct(int pre_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("PreProduct", "pre_statuscode", "pre_id=" + pre_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("PreProduct", "pre_id=" + pre_id, "pre_status", "pre_statuscode", "pre_checkdate", "pre_checkman");
		// 记录操作
		baseDao.logger.resAudit("PreProduct", "pre_id", pre_id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void submitPreProduct(int pre_id, String caller) {
		baseDao.execute("update PreProduct set pre_code=upper(pre_code) where pre_id=" + pre_id);
		baseDao.execute("update PreProduct set pre_purcrate=1 where pre_id=" + pre_id + " and nvl(pre_purcrate,0)=0 ");
		SqlRowList rs = baseDao.queryForRowSet("select * from PreProduct where pre_id=?", pre_id);
		if (rs.next()) {
			checkProdCode(pre_id, rs.getObject("pre_code"));
			// 判断规格名称是否存在重复
			checkProdName(rs.getObject("pre_thisid"), rs.getObject("pre_code"), rs.getObject("pre_detail"), rs.getObject("pre_spec"),
					rs.getObject("pre_parameter"));
		}
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("PreProduct", "pre_statuscode", "pre_id=" + pre_id);
		StateAssert.submitOnlyEntering(status);
		checkCate(pre_id);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit("PreProduct", pre_id);
		// 执行提交操作
		baseDao.submit("PreProduct", "pre_id=" + pre_id, "pre_status", "pre_statuscode");
		// 记录操作
		baseDao.logger.submit("PreProduct", "pre_id", pre_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit("PreProduct", pre_id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void resSubmitPreProduct(int pre_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("PreProduct", "pre_statuscode", "pre_id=" + pre_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit("PreProduct", pre_id);
		// 执行反提交操作
		baseDao.resOperate("PreProduct", "pre_id=" + pre_id, "pre_status", "pre_statuscode");
		// 记录操作
		baseDao.logger.resSubmit("PreProduct", "pre_id", pre_id);
		handlerService.afterResSubmit("PreProduct", pre_id);
	}

	/**
	 * 转正式物料
	 */
	public int turnFormal(int pre_id) {
		int prid = 0;
		// 判断该新物料申请单是否已经转入过物料
		Object[] prefld = baseDao.getFieldsDataByCondition("preproduct", "pre_thisid,pre_code", "pre_id=" + pre_id);
		Object[] prfld = baseDao.getFieldsDataByCondition("Product", "pr_code,pr_sourcecode", "pr_code='" + prefld[1] + "'");
		if (prfld != null) {
			if (prfld[0] != null && !prfld[0].equals("")) {
				// 物料号已经存在
				if ((String) prfld[1] != (String) prefld[0]) {
					// 不是从此申请单转的物料
					BaseUtil.showError(BaseUtil.getLocalMessage("scm.product.preproduct.haveturn")
							+ "<a href=\"javascript:openUrl('jsps/scm/product/productBase.jsp?formCondition=pr_codeIS" + prfld[0] + "')\">"
							+ prfld[0] + "</a>&nbsp;");
				} else {
					prid = preProductDao.TurnProd(pre_id);
				}
			}
		} else {
			// 转
			prid = preProductDao.TurnProd(pre_id);
			// 修改转正式人
			baseDao.updateByCondition(
					"PreProduct",
					"pre_turnman='" + SystemSession.getUser().getEm_name() + "',pre_turndate="
							+ DateUtil.parseDateToOracleString(null, new Date()) + ",pre_statuscode='TURNFM',pre_status='"
							+ BaseUtil.getLocalMessage("TURNFM") + "'", "pre_id=" + pre_id);
			// 记录操作
			baseDao.logger.turn("msg.turnFormal", "PreProduct", "pre_id", pre_id);
		}
		if (prid > 0) {
			int count = baseDao.getCount("select count(*) from user_tab_columns where table_name='PREPRODFEATURE'");
			if (count > 0) {
				baseDao.execute("INSERT INTO PRODFEATURE(PF_ID , PF_PRODCODE, PF_DETNO, PF_FECODE, PF_FENAME, PF_PRID, PF_FORCODE, PF_VALUECODE, PF_VALUE) "
						+ "select PRODFEATURE_SEQ.nextval, PPF_PRODCODE, PPF_DETNO, PPF_FECODE, PPF_FENAME, "
						+ prid
						+ ", PPF_FORCODE, PPF_VALUECODE, PPF_VALUE " + " from PREPRODFEATURE where PPF_PRID=" + pre_id);
			}
			Employee employee = SystemSession.getUser();
			if (baseDao.isDBSetting("PreProduct", "autoSync")) {
				Object custatus = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_id=" + prid);
				if (custatus != null && "AUDITED".equals(custatus)) {
					Master master = employee.getCurrentMaster();
					if (master != null && master.getMa_soncode() != null) {// 资料中心
						String res = null;
						res = baseDao.callProcedure("SYS_POST", new Object[] { "Product!Post", SpObserver.getSp(), master.getMa_soncode(),
								String.valueOf(prid), employee.getEm_name(), employee.getEm_id() });
						if (res != null) {
							BaseUtil.appendError(res);
						}
					}
				}
			}
		}
		handlerService.handler("PreProduct", "turn", "after", new Object[] { prid });
		return prid;
	}

	@Override
	public String getkind(String code) {
		Object kind = baseDao.getFieldDataByCondition("ProductKind", "pk_acceptmethod", "pk_name='" + code + "'");
		if (kind != null) {
			return kind.toString();
		} else {
			return null;
		}

	}

	@Override
	public int turninquiry(int pre_id) {
		Object obj = baseDao.getFieldDataByCondition("preproduct", "pre_manutype", "pre_id=" + pre_id);
		if (obj != null && obj.equals("MAKE")) {
			BaseUtil.showError("生产类型的物料不能发出询价!");
		}
		Key key = transferRepository.transfer("PreProduct!ToInquity", pre_id);
		int id = key.getId();
		String os = null;
		// 转入明细
		transferRepository.transferDetail("PreProduct!ToInquity", pre_id, key);
		// 更新转入的询价单从表截止、生效日期
		String effectiveDays = baseDao.getDBSetting("PurchasePrice", "effectiveDays");
		baseDao.execute("update InquiryDetail set id_myfromdate=sysdate,id_mytodate=sysdate+nvl(" + effectiveDays + ",0) where id_inid="
				+ key.getId());
		// 更新原表字段
		baseDao.updateByCondition("preproduct", "pre_turninquiry='已转询价'", "pre_id=" + pre_id);
		if (obj != null && obj.equals("OSMAKE")) {
			os = "委外";
		} else if (obj != null && (obj.equals("PURCHASE") || obj.equals("CUSTOFFER"))) {
			os = "采购";
		}
		baseDao.updateByCondition("inquiry", "in_kind='" + os + "'", "in_id=" + id);
		return id;
	}

	@Override
	public int turnsample(int pre_id) {
		Key key = transferRepository.transfer("PreProduct!ToSample", pre_id);
		int id = key.getId();
		// 更新原表字段
		baseDao.updateByCondition("preproduct", "pre_turnsample='转打样'", "pre_id=" + pre_id);
		return id;
	}

	@Override
	public int getPreCount(String pre_spec, int pre_id) {
		pre_spec = checkParam(StringUtil.nvl(pre_spec, " "));
		String con = pre_id == 0 ? "pre_spec='" + pre_spec + "'" : "pre_spec='" + pre_spec + "' and pre_id<>" + pre_id;
		int count1 = baseDao.getCountByCondition("preproduct", con);
		int count2 = baseDao.getCountByCondition("product", "pr_spec='" + pre_spec + "'");
		return count1 + count2;
	}

	/**
	 * 
	 * @author wsy 反馈编号：2017040598
	 *         新物料申请保存、更新、提交、审核时，对含有单引号(')的物料名称、物料规格、规格参数做处理。
	 */
	public String checkParam(String param) {
		if (param.contains("'")) {
			param = param.replace("'", "''");
		}
		return param;
	}

	public void getMsdlevel(Object k1, Object k2, Object k3, Object id) {
		if (k1 != null && !"".equals(k1) && k2 != null && !"".equals(k2) && k3 != null && !"".equals(k3)) {
			SqlRowList rs = baseDao
					.queryForRowSet(
							"select k3.pk_id pk_id,k3.pk_leaf pk_leaf,k1.pk_code||k2.pk_code||k3.pk_code pk_code from productkind k1,productkind k2,productkind k3 where k1.pk_name=? and k1.pk_level=1 and k2.pk_name=? and k2.pk_level=2 and k3.pk_name=? and k3.pk_level=3 and k1.pk_id=k2.pk_subof and k2.pk_id=k3.pk_subof"
									+ " and k1.pk_effective='有效' and k2.pk_effective='有效' and k3.pk_effective='有效'", k1, k2, k3);
			if (rs.next()) {
				baseDao.execute("update preproduct set pre_msdlevel=(select pk_msdlevel from productkind where pk_id=" + rs.getInt("pk_id")
						+ ") where pre_id=" + id);
			}
		} else {
			Object[] objs = null;
			if ((k2 == null || "".equals(k2)) && (k1 != null && !"".equals(k1))) {
				objs = baseDao.getFieldsDataByCondition("ProductKind", "pk_id,pk_code", "pk_level=1 and pk_effective='有效' and pk_name='"
						+ k1 + "'");
			}
			if ((k3 == null || "".equals(k3)) && (k2 != null && !"".equals(k2)) && (k1 != null && !"".equals(k1))) {
				objs = baseDao
						.getFieldsDataByCondition(
								"(select k2.pk_id pk_id,k1.pk_code||k2.pk_code pk_code from productkind k1,productkind k2 where k2.pk_level=2 and k2.pk_effective='有效' and k2.pk_name='"
										+ k2
										+ "' and k1.pk_id=k2.pk_subof and k1.pk_name='"
										+ k1
										+ "' and k1.pk_level=1 and k1.pk_effective='有效')", "pk_id,pk_code", "1=1");
			}
			if (objs != null && objs[0] != null && objs[1] != null) {
				baseDao.execute("update preproduct set pre_msdlevel=(select pk_msdlevel from productkind where pk_id=" + objs[0]
						+ ") where pre_id=" + id);
			}
		}
	}
}
