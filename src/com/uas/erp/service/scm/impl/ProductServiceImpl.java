package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.uas.api.crypto.util.SecretUtil;
import com.uas.b2c.model.B2CUtil;
import com.uas.b2c.service.common.B2CProdService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ProductDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.scm.ProductService;
import com.uas.remoting.hessian.MultiProxyFactoryBean;

@Service
public class ProductServiceImpl implements ProductService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private ProductDao productDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private B2CUtil b2cUtil;
	@Autowired
	private B2CProdService b2cProductService;

	@Override
	public void saveProduct(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		if (store.get("pr_code").toString().contains(" ")) {
			BaseUtil.showError("物料编号不能带空格");
		}
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Product", "pr_code='" + store.get("pr_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 检查物料编码长度是否符合大类中的长度规则设置
		checkCodeLength(store);
		checkProdCode(store.get("pr_id"), store.get("pr_code"));
		checkProdName(store.get("pr_id"), store.get("pr_code"), store.get("pr_detail"), store.get("pr_spec"), store.get("pr_speccs"));
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store });
		// 保存product
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Product", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.logger.save(caller, "pr_id", store.get("pr_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	/**
	 * 问题反馈编号:2016110967 新功能需求:易连资料中心账套
	 * 
	 * @author wsy
	 */
	private void checkCodeLength(Map<Object, Object> store) {
		Object pr_code = store.get("pr_code");
		Object pr_kind = store.get("pr_kind");
		if (pr_code != null && !"".equals(pr_code) && pr_kind != null && !"".equals(pr_kind)) {
			Object pk_codelength = baseDao.getFieldDataByCondition("ProductKind", "pk_codelength", "pk_name='" + pr_kind
					+ "' and pk_level=1");
			int length = pk_codelength == null ? 0 : Integer.parseInt(pk_codelength.toString());
			if (length != 0) {
				if (pr_code.toString().getBytes().length != Integer.parseInt(pk_codelength.toString())) {
					BaseUtil.showError("物料编码长度不符合大类中的长度规则设置，不能保存!");
				}
			}
		}
	}

	void checkCate(Object id) {
		SqlRowList rs = baseDao.queryForRowSet("select * from Product where pr_id=?", new Object[] { id });
		if (rs.next()) {
			String error = null;
			if (StringUtil.hasText(rs.getObject("pr_stockcatecode"))) {
				error = baseDao.getJdbcTemplate().queryForObject(
						"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
						String.class, rs.getObject("pr_stockcatecode"));
				if (error != null) {
					BaseUtil.showError("填写的存货科目不存在，或者状态不等于已审核，或者不是末级科目！");
				}
				error = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code NOT IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('MonthAccount!scm','stockCatecode'), chr(10))))",
								String.class, rs.getObject("pr_stockcatecode"));
				if (error != null) {
					BaseUtil.showError("存货科目不是【系统参数设置-->供应链-->库存管理系统-->库存期末处理-->库存对账】中的存货科目！");
				}
			}
			if (StringUtil.hasText(rs.getObject("pr_incomecatecode"))) {
				error = baseDao.getJdbcTemplate().queryForObject(
						"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
						String.class, rs.getObject("pr_incomecatecode"));
				if (error != null) {
					BaseUtil.showError("填写的收入科目不存在，或者状态不等于已审核，或者不是末级科目！");
				}
				error = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code NOT IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('MakeCostClose','incomeCatecode'), chr(10))))",
								String.class, rs.getObject("pr_incomecatecode"));
				if (error != null) {
					BaseUtil.showError("收入科目不是【系统参数设置-->成本会计管理-->成本核算系统-->成本凭证制作-->主营业务成本结转凭证制作】中的主营业务收入科目！");
				}
			}
			if (StringUtil.hasText(rs.getObject("pr_costcatecode"))) {
				error = baseDao.getJdbcTemplate().queryForObject(
						"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
						String.class, rs.getObject("pr_costcatecode"));
				if (error != null) {
					BaseUtil.showError("填写的成本科目不存在，或者状态不等于已审核，或者不是末级科目！");
				}
				error = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wmsys.wm_concat(ca_code) from Category where ca_code=? and ca_code NOT IN (SELECT COLUMN_VALUE FROM TABLE(parseString(getconfig('MakeCostClose','costCatecode'), chr(10))))",
								String.class, rs.getObject("pr_costcatecode"));
				if (error != null) {
					BaseUtil.showError("成本科目不是【系统参数设置-->成本会计管理-->成本核算系统-->成本凭证制作-->主营业务成本结转凭证制作】中的主营业务成本科目！");
				}
			}
		}
	}

	private void checkProdCode(Object pr_id, Object prcode) {
		if (prcode == null || prcode.equals("")) {
			BaseUtil.showError("物料编号为空，不能进行当前操作");
		} else {
			if (prcode.toString().contains(" ")) {
				BaseUtil.showError("物料编号带空格，不能进行当前操作");
			}
		}
		baseDao.execute("update product set pr_code=upper(pr_code) where pr_id=" + pr_id);
		// 判断物料编号在物料资料中是否存在重复
		String dets = baseDao.getJdbcTemplate().queryForObject("select WM_CONCAT(pr_code) from Product where pr_code=? and pr_id<>?",
				String.class, prcode, pr_id);
		if (dets != null) {
			BaseUtil.showError("物料编号在物料资料表中已存在!物料编号：" + dets);
		}
		// 判断物料编号在新物料申请资料中是否存在重复
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT(pre_thisid) from PreProduct where pre_code=? and pre_thisid<>(select nvl(pr_sourcecode,' ') from product where pr_id=?)",
						String.class, prcode, pr_id);
		if (dets != null) {
			BaseUtil.showError("物料编号在新物料申请资料表中已存在!申请单号：" + dets);
		}
	}

	private void checkProdName(Object prid, Object prcode, Object prname, Object prspec, Object parameter) {
		// 获取物料资料中的参数配置: 允许物料名称+规格+规格参数重复。 反馈编号：2017120082 @author:lidy
		String checkProdName = baseDao.getDBSetting("Product", "checkProdName");
		// 判断名称规格是否存在重复
		String code = baseDao.getFieldValue(
				"Product",
				"pr_code",
				"pr_id <> " + prid + " AND nvl(pr_detail,' ')='" + StringUtil.nvl(prname, " ") + "' and nvl(pr_spec,' ')='"
						+ StringUtil.nvl(prspec, " ") + "' and nvl(pr_speccs,' ')='" + StringUtil.nvl(parameter, " ")
						+ "' and nvl(pr_statuscode,' ')<>'DISABLE'", String.class);
		if (code != null) {
			if (checkProdName == null || "0".equals(checkProdName)) {
				BaseUtil.showError("在物料资料中已经存在相同规格名称的物料，物料编号:" + code + "<hr>");
			} else {
				BaseUtil.appendError("在物料资料中已经存在相同规格名称的物料，物料编号:" + code + "<hr>");
			}
		}
		// 判断名称规格是否存在重复
		code = baseDao.getFieldValue("PreProduct", "pre_thisid",
				"pre_code <> '" + StringUtil.nvl(prcode, " ") + "' AND pre_detail='" + StringUtil.nvl(prname, " ") + "' and pre_spec='"
						+ StringUtil.nvl(prspec, " ") + "' and pre_parameter='" + StringUtil.nvl(parameter, " ") + "'", String.class);
		if (code != null) {
			if (checkProdName == null || "0".equals(checkProdName)) {
				BaseUtil.showError("在新物料申请资料中已经存在相同规格名称的申请，申请单号:" + code + "<hr>");
			} else {
				BaseUtil.appendError("在新物料申请资料中已经存在相同规格名称的申请，申请单号:" + code + "<hr>");
			}
		}
	}

	@Override
	public void deleteProduct(int pr_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_id=" + pr_id);
		StateAssert.delOnlyEntering(status);
		// 是否已产生业务数据
		baseDao.delCheck("product", pr_id);
		if ("ProductYP".equals(caller)) {// 用品基本资料
			// 如果在用品申请单中存在,则限制删除
			int count = baseDao
					.getCount("select count(*) from oaapplicationdetail where od_procode=(select pr_code from product where pr_id=" + pr_id
							+ ")");
			if (count > 0) {
				BaseUtil.showError("该用品正在使用禁止删除!");
			}
		}
		// 删除前将新物料申请的转正式物料状态更新
		Object os = baseDao.getFieldDataByCondition("product", "pr_code", " pr_id=" + pr_id);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { pr_id });
		// 删除
		baseDao.execute("delete from Productonhand where po_prodcode=(select pr_code from product where pr_id=" + pr_id + ")");
		baseDao.execute("delete from PRODFEATURE where PF_PRID=" + pr_id);
		// 删除
		baseDao.deleteById("Product", "pr_id", pr_id);
		// 更新执行
		if (os != null) {
			baseDao.execute("update preproduct set pre_status='已审核',pre_statuscode='AUDITED' where pre_code='" + os + "'");
		}
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master != null && master.getMa_type() == 1) {// 资料中心
			String groupName = BaseUtil.getXmlSetting("defaultSob");
			String allMasters = baseDao.getJdbcTemplate().queryForObject("select ma_soncode from " + groupName + ".master where ma_name=?",
					String.class, groupName);
			baseDao.execute("declare v_masters varchar2(1000);v_array str_table_type;v_m varchar2(30);v_sql varchar2(100);begin v_masters := '"
					+ allMasters
					+ "';v_array := parsestring(v_masters,',');for i in v_array.first()..v_array.last() loop v_m := v_array(i);v_sql := 'update '||v_m||'.Product set pr_statuscode='DISABLE' where pr_id="
					+ pr_id + "';execute immediate v_sql;end loop;COMMIT;end;");
		}
		// 记录操作
		baseDao.logger.delete(caller, "pr_id", pr_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { pr_id });
	}

	@Override
	public void updateProductById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_id=" + store.get("pr_id"));
		StateAssert.updateOnlyEntering(status);
		// 检查物料编码长度是否符合大类中的长度规则设置
		checkCodeLength(store);
		// 判断规格名称是否存在重复
		checkProdCode(store.get("pr_id"), store.get("pr_code"));
		checkProdName(store.get("pr_id"), store.get("pr_code"), store.get("pr_detail"), store.get("pr_spec"), store.get("pr_speccs"));
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Product", "pr_id");
		baseDao.execute(formSql);
		baseDao.execute("update product set pr_purcrate=1 where pr_id=" + store.get("pr_id") + " and nvl(pr_purcrate,0)=0 ");
		baseDao.execute("update product set pr_purcunit=pr_unit where pr_id=" + store.get("pr_id") + " and nvl(pr_purcunit,' ')=' '");
		// 记录操作
		baseDao.logger.update(caller, "pr_id", store.get("pr_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void auditProduct(int pr_id, String caller) {
		baseDao.execute("update product set pr_purcrate=1 where pr_id=" + pr_id + " and nvl(pr_purcrate,0)=0 ");
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("Product", new String[] { "pr_statuscode", "pr_code", "pr_detail", "pr_spec",
				"pr_speccs" }, "pr_id=" + pr_id);
		StateAssert.auditOnlyCommited(status[0]);
		checkProdCode(pr_id, status[1]);
		checkProdName(pr_id, status[1], status[2], status[3], status[4]);
		checkCate(pr_id);
		// 判断属于虚拟特征件的是否有特征项
		String SQLStr = "SELECT count(1) n FROM  product where pr_id='" + pr_id
				+ "' and pr_specvalue='NOTSPECIFIC' and pr_id not in (select pf_prid from prodfeature ) ";
		SqlRowList rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("n") > 0) {
				BaseUtil.showError("虚拟特征件必须定义特征项,才能提交!'");
			}
		}
		Object prcode = baseDao.getFieldDataByCondition("Product", "pr_code", "pr_id=" + pr_id);
		if (prcode == null || prcode.equals("")) {
			BaseUtil.showError("物料编号为空，不能审核");
		} else {
			if (prcode.toString().contains(" ")) {
				BaseUtil.showError("物料编号带空格，不能审核");
			}
		}
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { pr_id });
		// 执行审核操作
		baseDao.audit("Product", "pr_id=" + pr_id, "pr_status", "pr_statuscode", "pr_auditdate", "pr_auditman");
		baseDao.updateByCondition("Product", "pr_sendstatus='待上传'", "pr_id=" + pr_id);
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
		if (baseDao.isDBSetting(caller, "insertBrand")) {
			SqlRowList rs1 = baseDao.queryForRowSet("select pr_brand from product a where pr_id=" + pr_id
					+ " and not exists(select 1 from productbrand where replace(upper(pb_name),' ','')=replace(upper(a.pr_brand),' ','')) and pr_brand is not null");
			if (rs1.next()) {
				
				baseDao.execute("Insert into productbrand (PB_ID,PB_NAME,PB_RECORDER,PB_RECORDDATE,PB_STATUS,PB_STATUSCODE,PB_AUDITOR,PB_AUDITDATE,PB_REMARK,PB_RATE) values (productbrand_seq.nextval,'"
						+ rs1.getString("pr_brand")
						+ "','admin',sysdate,'已审核','AUDITED','admin',sysdate,'"
						+ rs1.getString("pr_brand")
						+ "',0)");
			}
		}
		// 记录操作
		baseDao.logger.audit(caller, "pr_id", pr_id);
		// 执行保存后的其它逻辑
		handlerService.afterAudit(caller, pr_id);
	}

	@Override
	public void resAuditProduct(int pr_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_id=" + pr_id);
		StateAssert.resAuditOnlyAudit(status);
		// 物料资料正在上传
		String sendStatus = baseDao.getFieldValue("product", "pr_sendstatus", "pr_id=" + pr_id, String.class);
		StateAssert.onSendingLimit(sendStatus);
		if ("ProductYP".equals(caller)) {// 用品基本资料
			// 如果在用品申请单中存在,则限制反审核
			int count = baseDao
					.getCount("select count(*) from oaapplicationdetail where od_procode=(select pr_code from product where pr_id=" + pr_id
							+ ")");
			if (count > 0) {
				BaseUtil.showError("该用品正在使用禁止反审核!");
			}
		}
		// 是否已产生业务数据
		baseDao.resAuditCheck("product", pr_id);
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, pr_id);
		// 执行反审核操作
		baseDao.resOperate("Product", "pr_id=" + pr_id, "pr_status", "pr_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "pr_id", pr_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, pr_id);
	}

	@Override
	public void submitProduct(int pr_id, String caller) {
		baseDao.execute("update product set pr_purcrate=1 where pr_id=" + pr_id + " and nvl(pr_purcrate,0)=0 ");
		// 只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("Product", new String[] { "pr_statuscode", "pr_code", "pr_detail", "pr_spec",
				"pr_speccs" }, "pr_id=" + pr_id);
		StateAssert.submitOnlyEntering(status[0]);
		checkProdCode(pr_id, status[1]);
		checkProdName(pr_id, status[1], status[2], status[3], status[4]);
		checkCate(pr_id);
		// 判断属于虚拟特征件的是否有特征项
		String SQLStr = "SELECT count(1) n FROM  product where pr_id='" + pr_id
				+ "' and pr_specvalue='NOTSPECIFIC' and pr_id not in (select pf_prid from prodfeature ) ";
		SqlRowList rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("n") > 0) {
				BaseUtil.showError("虚拟特征件必须定义特征项,才能提交!'");
			}
		}
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
	public void resSubmitProduct(int pr_id, String caller) {
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

	/**
	 * 物料资料整批抛转
	 */
	@Override
	public String[] postProduct(int[] id, int ma_id_f, int ma_id_t) {
		String from = baseDao.getFieldDataByCondition("master", "ma_name", "ma_id=" + ma_id_f).toString();
		String to = baseDao.getFieldDataByCondition("master", "ma_name", "ma_id=" + ma_id_t).toString();
		return productDao.postProduct(id, from, to);
	}

	@Override
	public void bannedProduct(int pr_id, String remark, String caller) {
		// 只能禁用未删除的单据!
		Object status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_id=" + pr_id);
		String pr_code = String.valueOf(baseDao.getFieldDataByCondition("Product", "pr_code", "nvl(pr_groupcode,' ')<>'用品' and pr_id="
				+ pr_id));
		if ("DELETED".equals(status)) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.banned_onlyCanuse"));
		}
		// maz 只能禁用已审核的单据 2017070720
		if (!"AUDITED".equals(status)) {
			BaseUtil.showError("只能禁用已审核的单据");
		}
		// 必须先禁用BOM或者禁用子件料号之后才能禁用物料
		String SQLStr = "";
		SqlRowList rs;
		Object prcode = baseDao.getFieldDataByCondition("Product", "pr_code", "pr_id=" + pr_id);
		SQLStr = "select count(1) num, wm_concat(bo_id) bo_id from bom,bomdetail where bo_id=bd_bomid and bd_soncode='" + prcode.toString()
				+ "' and bo_statuscode='AUDITED' and NVL(bd_usestatus,' ')<>'DISABLE' and rownum<=30";
		rs = baseDao.queryForRowSet(SQLStr);
		if (rs.next()) {
			if (rs.getInt("num") > 0) {
				BaseUtil.showError("必须先禁用BOM或者禁用子件才允许禁用物料，相关BOM：" + rs.getString("bo_id"));
			}
		}
		// 禁用(修改物料状态为已禁用)
		handlerService.handler(caller, "banned", "before", new Object[] { pr_id });
		// 禁用(修改物料状态为已禁用)
		baseDao.banned("Product", "pr_id=" + pr_id, "pr_status", "pr_statuscode");
		/**
		 * @author wsy 物料资料禁用，更新物料上传状态为待上传
		 */
		baseDao.updateByCondition("Product", "pr_remark_base='" + remark + "',pr_sendstatus='待上传'",
				"nvl(pr_groupcode,' ')<>'用品' and pr_id=" + pr_id);
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
		if (!pr_code.equals("null")) {
			/**
			 * maz 物料禁用后调用平台接口禁用b2b平台物料 2017060456
			 */
			bannedB2B(pr_code);
			// wuyx 20180208 同步禁用商城对应物料 并进行下架操作
			bannedB2C(pr_code);
		}
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
		// 反禁用(修改物料状态为已审核) maz 2017070720
		baseDao.audit("Product", "pr_id=" + pr_id, "pr_status", "pr_statuscode");
		// 记录操作
		baseDao.logger.resBanned(caller, "pr_id", pr_id);
		// 执行反禁用后的其它逻辑
		handlerService.handler(caller, "resBanned", "after", new Object[] { pr_id });
		// wuyx 20180208 物料反禁用 启用平台、上传对应物料
		String pr_code = String.valueOf(baseDao.getFieldDataByCondition("Product", "pr_code", "nvl(pr_groupcode,' ')<>'用品' and pr_id="
				+ pr_id));
		if (!pr_code.equals("null")) {
			resBannedB2B(pr_code);
			resBannedB2C(pr_code);
		}
	}

	@Override
	public void updateProductStatus(int id, String value, String crman, String remark, String date, String caller, String mfile) {
		if (baseDao.isDBSetting("Product", "updateMfile")) {
			if (mfile.length() == 0 && !value.equals("未认可")) {
				BaseUtil.showError("更新承认状态时必须有附件！！！");
			}
		}
		Object[] obj = baseDao.getFieldsDataByCondition("product", "pr_material,pr_crman,pr_admitstatus,pr_sqrq,pr_attach", "pr_id=" + id);
		baseDao.updateByCondition("product", "pr_material='" + value + "',pr_crman='" + crman + "',pr_admitstatus='" + remark
				+ "',pr_attach='" + mfile + "',pr_sqrq=to_date('" + date + "','yyyy-mm-dd')", "pr_id=" + id);
		// 更新承认状态的日志记录
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "更新承认状态原承认状态:" + obj[0] + ",承认人:" + obj[1] + ",承认日期:"
				+ obj[3] + ",承认备注:" + obj[2] + ",为新承认状态:" + value + ",新承认人:" + crman + ",新承认日期:" + date + ",新承认备注:" + remark + ",", "成功",
				"Product|pr_id=" + id));
	}

	@Override
	public void updateProductLevel(int id, String value, String remark, String caller) {
		Object[] obj = baseDao.getFieldsDataByCondition("product", "pr_level,pr_grade", "pr_id=" + id);
		baseDao.updateByCondition("product", "pr_level='" + value + "',pr_grade='" + remark + "'", "pr_id=" + id);
		// 更新物料等级的日志信息保存起来
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "更新原物料等级:" + obj[0] + ",物料等级备注:" + obj[1] + "为新物料等级:"
				+ value + ",新物料等级备注:" + remark + ";", "成功", "Product|pr_id=" + id));
	}

	@Override
	public int prodturnsample(int pr_id, String caller) {
		Employee employee = SystemSession.getUser();
		Object[] data = baseDao.getFieldsDataByCondition("product", new String[] { "pr_code", "pr_detail", "pr_spec", "pr_unit",
				"pr_projectcode" }, "pr_id=" + pr_id);// ps_envrequire是环保要求
		int id = baseDao.getSeqId("ProductSample_SEQ");
		String code = baseDao.sGetMaxNumber("ProductSample", 2);

		String sql = "INSERT INTO ProductSample(ps_id,ps_code,ps_prodcode,ps_prodname,ps_prodspec,ps_unit,ps_status,ps_statuscode,ps_recordor,ps_indate,ps_envrequire,ps_appman,ps_appmanid) VALUES("
				+ id
				+ ",'"
				+ code
				+ "','"
				+ data[0]
				+ "','"
				+ data[1]
				+ "','"
				+ data[2]
				+ "','"
				+ data[3]
				+ "','在录入','ENTERING','"
				+ employee.getEm_name()
				+ "',"
				+ DateUtil.parseDateToOracleString(null, new Date())
				+ ",'"
				+ data[4]
				+ "','"
				+ employee.getEm_name() + "'," + employee.getEm_id() + ")";
		baseDao.execute(sql);
		// 更新原表字段
		baseDao.updateByCondition("product", "pr_turnsample='转打样'", "pr_id=" + pr_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), "转打样", "转打样", "Product|pr_id=" + pr_id));
		return id;
	}

	/**
	 * 物料标准单价变更
	 */
	@Override
	public void changeStandardPrice(Employee employee, String caller, String data) {
		try {
			String updateman = employee.getEm_name();
			String updatedate = DateUtil.parseDateToOracleString(Constant.YMD, new Date());
			List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
			for (Map<Object, Object> map : maps) {
				Object beforeprice = baseDao.getFieldDataByCondition("Product ", "pr_standardprice", "pr_id=" + map.get("pr_id"));
				Object afterprice = map.get("pr_standardprice");
				String sql = "update Product set pr_standardprice =" + afterprice + ",pr_updatedate=" + updatedate + ",pr_updateman ='"
						+ updateman + "' where pr_id =" + map.get("pr_id");
				baseDao.execute(sql);
				String info = "时间:" + DateUtil.getCurrentDate() + ",更新人:" + updateman + ",更新前单价:" + beforeprice + ",更新后单价" + afterprice;
				baseDao.logger.others("物料标准单价变更", info, "Product", "pr_id", map.get("pr_id"));
			}
		} catch (Exception e) {
			BaseUtil.showError("保存失败！");
		}
	}

	@Override
	public String turnTender(Integer id, String caller, String title, String qty) {
		String msg = "";
		Employee employee = SystemSession.getUser();
		try {
			Master master = employee.getCurrentMaster();
			HashMap<String, String> params = new HashMap<String, String>();
			Map<String, Object> tender = new HashMap<String, Object>();
			List<Map<String, Object>> tenderProds = new ArrayList<Map<String, Object>>(1);
			Object Id = null;
			Object code = null;
			tender.put("id", null);
			tender.put("code", "ZB" + DateUtil.format(new Date(), "yyMMddHHmmss"));
			tender.put("title", title);
			tender.put("date", new Date());
			tender.put("isPublish", 0);
			tender.put("ifOpen", 1);
			tender.put("ifTax", 1);
			tender.put("payment", "现金");
			tender.put("currency", "RMB");
			tender.put("invoiceType", 2);
			Object[] objs = baseDao.getFieldsDataByCondition("Product", "pr_detail,pr_spec,pr_unit,pr_brand", "pr_id = " + id);
			if (objs != null) {
				Map<String, Object> tenderProd = new HashMap<String, Object>();
				tenderProd.put("index", 1);
				tenderProd.put("prodTitle", objs[0]);
				tenderProd.put("prodCode", objs[1]);
				tenderProd.put("unit", objs[2]);
				tenderProd.put("brand", objs[3]);
				tenderProd.put("prodTitle", objs[0]);
				if (qty.indexOf(".") > -1) {
					tenderProd.put("qty", Long.parseLong(qty.substring(0, qty.indexOf("."))));
				} else {
					tenderProd.put("qty", qty);
				}

				tenderProds.add(tenderProd);
				tender.put("purchaseTenderProds", tenderProds);
			} else {
				throw new Exception("此项目(物料)不存在！");
			}

			params.put("tender", FlexJsonUtil.toJsonDeep(tender));
			if (params.size() > 0) {
				Response response = HttpUtil.sendPostRequest(master.getMa_b2bwebsite() + "/erp/tender/save?access_id=" + master.getMa_uu(),
						params, true, master.getMa_accesssecret());
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					String data = response.getResponseText();
					if (StringUtil.hasText(data)) {
						Map<Object, Object> ID = BaseUtil.parseFormStoreToMap(data);
						Id = ID.get("id");
						code = ID.get("code");
						// 执行保存操作
						String formSql = "insert into Tender(id,code,pt_title,pt_recordman,pt_indate,pt_status,pt_statuscode,tt_status,tt_statuscode) values (?,?,?,?,sysdate,?,'ENTERING',?,'ENTERING')";
						baseDao.execute(formSql, Id, code, title, employee.getEm_name(), BaseUtil.getLocalMessage("ENTERING"),
								BaseUtil.getLocalMessage("ENTERING"));
						baseDao.logger.save("Tender", "id", Id);
					}
				} else {
					throw new Exception("连接平台失败！" + response.getStatusCode());
				}
			}
			baseDao.logger.turn("转招标", caller, "pr_id", id);
			msg = "<a href=\"javascript:openUrl('jsps/scm/purchase/tender.jsp?formCondition=idIS" + Id + "')\">" + code + "</a>";
		} catch (Exception e) {
			BaseUtil.showError("转招标单失败，错误：" + e.getMessage());
		}
		return "转招标单成功，招标单号" + msg;
	}

	@Override
	public String getCodePostfix(String caller) {
		Object code = baseDao.getFieldDataByCondition("configs", "DATA", "caller='" + caller + "' and code='CodePostfix'");
		if (StringUtil.hasText(code) && code.toString().contains(" ")) {
			BaseUtil.showError("您设置的后缀码字段存在空格,请修改后再生成编号");
		}
		if (StringUtil.hasText(code)) {
			return code.toString();
		} else {
			return "";
		}
	}

	private void bannedB2B(String pr_code) {
		Master master = SystemSession.getUser().getCurrentMaster();
		HashMap<String, String> params = new HashMap<String, String>();
		try {
			params.put("data", String.valueOf(pr_code));
			Response response = HttpUtil.sendPostRequest(
					master.getMa_b2bwebsite() + "/erp/product/updateB2bEnabled?access_id=" + master.getMa_uu(), params, true,
					master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
			} else {
				BaseUtil.showError("连接平台失败！" + response.getStatusCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
			// BaseUtil.showError("发布失败，错误："+e.getMessage());
		}
	}

	private void bannedB2C(String pr_code) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (b2cUtil.isB2CMAll(master)) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			SpObserver.putSp(master.getMa_name());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				b2cProductService.setB2cEnable(pr_code, 2);
			} catch (Exception e) {
				BaseUtil.showError(e.getMessage());
			}
		}
	}

	private void resBannedB2C(String pr_code) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (b2cUtil.isB2CMAll(master)) {
			SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
			SpObserver.putSp(master.getMa_name());
			MultiProxyFactoryBean.setProxy(master.getEnv());
			try {
				String res = b2cProductService.setB2cEnable(pr_code, 1);
				if (res.equals("fail"))
					baseDao.updateByCondition("Product", " pr_sendstatus='待上传'", " pr_code='" + pr_code + "'");
			} catch (Exception e) {
				BaseUtil.showError(e.getMessage());
			}
		}
	}

	private void resBannedB2B(String pr_code) {
		Master master = SystemSession.getUser().getCurrentMaster();
		HashMap<String, String> params = new HashMap<String, String>();
		try {
			params.put("data", pr_code);
			String b2burl = "http://uas.ubtob.com";
			if (!(master.getMa_b2bwebsite() == null || "".equals(master.getMa_b2bwebsite()))) {
				b2burl = master.getMa_b2bwebsite();
			}
			Response response = HttpUtil.sendPostRequest(b2burl + "/erp/product/updateB2bEnabled/audited?access_id=" + master.getMa_uu(),
					params, true, master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String res = response.getResponseText();
				if (res.equals("0"))
					baseDao.updateByCondition("Product", " pr_sendstatus='待上传'", "pr_code='" + pr_code + "'");
			} else {
				BaseUtil.showError("连接平台失败！" + response.getStatusCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
