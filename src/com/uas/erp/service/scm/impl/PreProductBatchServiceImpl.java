package com.uas.erp.service.scm.impl;

import java.util.ArrayList;
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
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.service.scm.PreProductBatchService;

@Service
public class PreProductBatchServiceImpl implements PreProductBatchService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	static final String TURNPRODUCT = "INSERT INTO product(pr_id,pr_sourcecode,pr_code,pr_detail,pr_spec,pr_whcode,pr_whname,pr_supplytype,pr_manutype,pr_dhzc,pr_zxbzs,pr_leadtime,pr_kind,pr_kind2,pr_kind3,pr_acceptmethod,pr_buyercode,pr_buyername,pr_location,"
			+ "pr_statuscode,pr_status,pr_checkstatuscode,pr_checkstatus,pr_docdate,pr_brand,pr_unit,pr_zxdhl) select ?,pbd_thisid,pbd_prodcode,pbd_prodname,pbd_spec,pbd_whcode,pbd_whname,pbd_supplytype,pbd_manutype,pbd_dhzc,pbd_zxbzs,pbd_leadtime,pbd_kind,pbd_kind2,pbd_kind3,"
			+ "pbd_acceptmethod,pbd_buyercode,pbd_buyername,pbd_location,'AUDITED','已审核','AUDITED','已审核',sysdate,pbd_brand,pbd_unit,pbd_zxdhl from PreProductBatchDet where pbd_id=?";

	@Override
	public void savePreProductBatch(String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> prodgrid = BaseUtil.parseGridStoreToMaps(param);
		handlerService.handler(caller, "save", "before", new Object[] { store });
		List<String> sqls = new ArrayList<String>();
		String prodkind = "";
		for (Map<Object, Object> prodmap : prodgrid) {
			int prodid = baseDao.getSeqId("PreProductBatchDet_SEQ");
			prodmap.put("pbd_id", prodid);
			if (StringUtil.hasText(prodmap.get("pbd_kind"))) {
				prodkind = prodmap.get("pbd_kind").toString();
				if (StringUtil.hasText(prodmap.get("pbd_kind2"))) {
					prodkind = prodmap.get("pbd_kind2").toString();
					if (StringUtil.hasText(prodmap.get("pbd_kind3"))) {
						prodkind = prodmap.get("pbd_kind3").toString();
					}
				}
				Object[] pk = baseDao.getFieldsDataByCondition("ProductKind", new String[] { "pk_manutype", "pk_dhzc", "pk_supplytype",
						"pk_buyercode", "pk_buyername", "pk_whcode", "pk_whname", "pk_acceptmethod", "pk_location" }, "pk_name='"
						+ prodkind + "'");
				if (pk == null) {
					BaseUtil.showError("行" + prodmap.get("pbd_detno") + "填写的种类不存在，请确认");
				}
				prodmap.put("pbd_manutype", pk[0]);
				prodmap.put("pbd_dhzc", pk[1]);
				prodmap.put("pbd_supplytype", pk[2]);
				prodmap.put("pbd_buyercode", pk[3]);
				prodmap.put("pbd_buyername", pk[4]);
				prodmap.put("pbd_whcode", pk[5]);
				prodmap.put("pbd_whname", pk[6]);
				prodmap.put("pbd_acceptmethod", pk[7]);
				prodmap.put("pbd_location", pk[8]);
			}
		}
		sqls.add(SqlUtil.getInsertSqlByMap(store, "PreProductBatch"));
		// 保存InquiryDetail
		sqls.addAll(SqlUtil.getInsertSqlbyGridStore(prodgrid, "PreProductBatchDet"));
		baseDao.execute(sqls);
		baseDao.logger.save(caller, "pb_id", store.get("pb_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
		baseDao.updateByCondition("PreProductBatchDet", "pbd_thisid='" + store.get("pb_code") + "'", "pbd_pbid=" + store.get("pb_id"));
	}

	@Override
	public void updatePreProductBatchById(String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> prodgrid = BaseUtil.parseGridStoreToMaps(param);
		List<String> sqls = new ArrayList<String>();
		handlerService.handler(caller, "save", "before", new Object[] { store });
		sqls.add(SqlUtil.getUpdateSqlByFormStore(store, "PreProductBatch", "pb_id"));
		String prodkind = "";
		int pk_level = 0;
		if (prodgrid.size() > 0) {
			for (Map<Object, Object> s : prodgrid) {
				if (StringUtil.hasText(s.get("pbd_kind"))) {
					prodkind = s.get("pbd_kind").toString();
					pk_level = 1;
					if (StringUtil.hasText(s.get("pbd_kind2"))) {
						prodkind = s.get("pbd_kind2").toString();
						pk_level = 2;
						if (StringUtil.hasText(s.get("pbd_kind3"))) {
							prodkind = s.get("pbd_kind3").toString();
							pk_level = 3;
						}
					}
					Object[] pk = baseDao.getFieldsDataByCondition("ProductKind", new String[] { "pk_manutype", "pk_dhzc", "pk_supplytype",
							"pk_buyercode", "pk_buyername", "pk_whcode", "pk_whname", "pk_acceptmethod", "pk_location" }, "pk_name='"
							+ prodkind + "' and pk_level=" + pk_level);
					if (pk == null) {
						BaseUtil.showError("行" + s.get("pbd_detno") + "填写的种类不存在，请确认");
					}
					s.put("pbd_manutype", pk[0]);
					s.put("pbd_dhzc", pk[1]);
					s.put("pbd_supplytype", pk[2]);
					s.put("pbd_buyercode", pk[3]);
					s.put("pbd_buyername", pk[4]);
					s.put("pbd_whcode", pk[5]);
					s.put("pbd_whname", pk[6]);
					s.put("pbd_acceptmethod", pk[7]);
					s.put("pbd_location", pk[8]);
				}
				if (s.get("pbd_id") == null || s.get("pbd_id").equals("") || s.get("pbd_id").equals("0")
						|| Integer.parseInt(s.get("pbd_id").toString()) <= 0) {
					int id = baseDao.getSeqId("PreProductBatchDet_SEQ");
					s.put("pbd_id", id);
					String sql = SqlUtil.getInsertSqlByMap(s, "PreProductBatchDet", new String[] { "pbd_id" }, new Object[] { id });
					sqls.add(sql);
				}
			}
			sqls.addAll(SqlUtil.getInsertOrUpdateSql(prodgrid, "PreProductBatchDet", "pbd_id"));
		}
		baseDao.execute(sqls);
		// 记录操作
		baseDao.logger.update(caller, "pb_id", store.get("pb_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void deletePreProductBatch(int pb_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { pb_id });
		// 删除主表内容
		baseDao.deleteById("PreProductBatch", "pb_id", pb_id);
		baseDao.deleteById("PreProductBatchdet", "pbd_pbid", pb_id);
		baseDao.logger.delete(caller, "pb_id", pb_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { pb_id });
	}

	@Override
	public String auditPreProductBatch(int pb_id, String caller) {
		// 只能对已提交进行审核操作
		Object status = baseDao.getFieldDataByCondition("PreProductBatch", "pb_statuscode", "pb_id=" + pb_id);
		Employee employee = SystemSession.getUser();
		StateAssert.auditOnlyCommited(status);
		List<Object[]> newprod = baseDao.getFieldsDatasByCondition("PreProductBatchDet", new String[] { "pbd_prodcode", "pbd_prodname",
				"pbd_spec", "pbd_detno", "pbd_id" }, "pbd_pbid=" + pb_id);
		handlerService.beforeAudit("PreProductBatch", pb_id);
		String msg = "";
		List<Object[]> pk = baseDao.getFieldsDatasByCondition("PreProductBatchDet", new String[] { "pbd_prodcode", "pbd_kind", "pbd_kind2",
				"pbd_kind3", "pbd_detno" }, "pbd_pbid=" + pb_id);
		String prodkind = "";
		for (Object[] s : pk) {
			if (StringUtil.hasText(s[0]) && StringUtil.hasText(s[1])) {
				prodkind = s[1].toString();
				Object codelength = baseDao.getFieldDataByCondition("ProductKind", "pk_codelength", "pk_name='" + prodkind + "'");
				if (StringUtil.hasText(codelength) && Integer.parseInt(codelength.toString()) != 0
						&& s[0].toString().length() > Integer.parseInt(codelength.toString())) {
					BaseUtil.showError("行" + s[4] + "的物料编号超出该物料种类的编码长度，请进行调整");
				}
			}
		}
		if (newprod != null) {
			// 编号不能带空格
			SqlRowList rs = baseDao
					.queryForRowSet("select pbd_detno from PREPRODUCTBATCHDET where nvl(pbd_prodcode, ' ')=' ' and pbd_pbid=" + pb_id);
			if (rs.next()) {
				msg = msg + "第" + rs.getInt("pbd_detno") + "行物料编号为空，不能审核<hr>";
			} else {
				rs = baseDao.queryForRowSet("select pbd_detno from PREPRODUCTBATCHDET where instr(pbd_prodcode, ' ')>0 and pbd_pbid="
						+ pb_id);
				if (rs.next()) {
					msg = msg + "第" + rs.getInt("pbd_detno") + "行物料编号带空格，不能审核<hr>";
				}
			}
			// 同一张单 明细不能存在两个物料编号一样的情况
			rs = baseDao.queryForRowSet("select * from (select max(pbd_detno)detno,pbd_prodcode from PREPRODUCTBATCHDET where pbd_pbid="
					+ pb_id + " group by pbd_prodcode having count(1)>1)");
			if (rs.next()) {
				msg = msg + "第" + rs.getInt("detno") + "行物料编号：" + rs.getString("pbd_prodcode") + "在该明细行出现重复,不允许审核!<hr>";
			}
			// 申请的物料编号 不能在物料资料中存在
			rs = baseDao
					.queryForRowSet("select pr_code from product where pr_code in (select pbd_prodcode from PREPRODUCTBATCHDET where pbd_pbid="
							+ pb_id + ")");
			if (rs.next()) {
				msg = msg + "物料编号在物料资料表中已存在!物料编号：" + rs.getString("pr_code") + "<hr>";
			}
			if (!"".equals(msg)) {
				BaseUtil.showError(msg);
			}
			// 根据物料资料中参数配置：允许物料名称+规格+规格参数重复 检查存在名称、规格一样的物料
			checkProdName(pb_id);
		}
		// 转正式物料
		String result = baseDao.callProcedure("SP_VASTTURNPROD", new Object[] { pb_id, employee.getEm_name() });
		if (result != null && !"".equals(result)) {
			BaseUtil.showError(result);
		}
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { pb_id });
		// 执行审核操作
		baseDao.audit("PreProductBatch", "pb_id=" + pb_id, "pb_status", "pb_statuscode", "pb_auditdate", "pb_auditor");
		// 记录操作
		baseDao.logger.audit(caller, "pb_id", pb_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { pb_id });
		return "批量转物料资料成功!";
	}

	@Override
	public void submitPreProductBatch(int pb_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("PreProductBatch", "pb_statuscode", "pb_id=" + pb_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { pb_id });
		catchCombo(pb_id);
		// 除去空格
		baseDao.execute("update PreProductBatchDet set pbd_prodcode=trim(pbd_prodcode) where pbd_prodcode is not null and  pbd_pbid="
				+ pb_id);
		List<Object[]> pk = baseDao.getFieldsDatasByCondition("PreProductBatchDet", new String[] { "pbd_prodcode", "pbd_kind", "pbd_kind2",
				"pbd_kind3", "pbd_detno" }, "pbd_pbid=" + pb_id);
		String prodkind = "";
		String msg = "";
		List<String> notAllow = new ArrayList<String>();
		for (Object[] s : pk) {
			if (StringUtil.hasText(s[0]) && StringUtil.hasText(s[1])) {
				prodkind = s[1].toString();
				Object codelength = baseDao.getFieldDataByCondition("ProductKind", "pk_codelength", "pk_name='" + prodkind + "'");
				if (StringUtil.hasText(codelength) && Integer.parseInt(codelength.toString()) != 0
						&& s[0].toString().length() > Integer.parseInt(codelength.toString())) {
					BaseUtil.showError("行" + s[4] + "的物料编号超出该物料种类的编码长度，请进行调整");
				}
			}
			if (StringUtil.hasText(s[0])) {
				// 编号不能带空格
				if (s[0].toString().contains(" ")) {
					BaseUtil.showError("第" + s[4] + "行物料编号为空或者带空格，不能提交<hr>");
				}
				// 同一张单 明细不能存在两个物料编号一样的情况
				String dets = baseDao.getJdbcTemplate().queryForObject(
						"select WM_CONCAT(pbd_detno) from PreProductBatchDet where pbd_prodcode=? and pbd_pbid=? and pbd_detno<>?",
						String.class, s[0], pb_id, s[4]);
				if (dets != null) {
					msg = msg + "第" + s[4] + "和第" + dets + "行物料编号重复,已删除本次第" + s[4] + "行物料信息<hr>";
					baseDao.execute("delete PreProductBatchDet  where pbd_pbid=" + pb_id + " and pbd_detno=" + s[4]);
					continue;
				}
				// 申请的物料编号 不能在物料资料中存在
				dets = baseDao.getJdbcTemplate().queryForObject("select WM_CONCAT(pr_code) from Product where pr_code=?", String.class,
						s[0]);
				if (dets != null) {
					msg = msg + "物料编号在物料资料表中已存在!物料编号：" + dets + " ,已删除本次第" + s[4] + "行物料信息<hr>";
					notAllow.add("delete PreProductBatchDet  where pbd_pbid=" + pb_id + " and pbd_detno=" + s[4]);
					continue;
				}
				// 判断本次提交的物料编号是否和已提交未审核的其它批量新物料申请中物料编号重复
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select WM_CONCAT(pb_code) from PreProductBatchDet left join PreProductBatch on pbd_pbid=pb_id where pb_id<>? and pb_status='已提交' and pbd_prodcode=?",
								String.class, pb_id, s[0]);
				if (dets != null) {
					msg = msg + "第" + s[4] + "行物料编号在批量新物料申请单:" + dets + "中已存在,已删除本次第" + s[4] + "行物料信息<hr>";
					notAllow.add("delete PreProductBatchDet  where pbd_pbid=" + pb_id + " and pbd_detno=" + s[4]);
					continue;
				}
				// 判断本次提交的物料编号是否和新物料申请时（含在录入的）相同
				dets = baseDao.getJdbcTemplate().queryForObject("select WM_CONCAT(pre_thisid)  from PreProduct  where pre_code=?",
						String.class, s[0]);
				if (dets != null) {
					msg = msg + "第" + s[4] + "行物料编号在新物料申请单:" + dets + "中已存在,已删除本次第" + s[4] + "行物料信息<hr>";
					notAllow.add("delete PreProductBatchDet  where pbd_pbid=" + pb_id + " and pbd_detno=" + s[4]);
					continue;
				}
			}
		}
		if (!"".equals(msg)) {
			baseDao.execute(notAllow);
			BaseUtil.appendError(msg);
		}
		// 根据物料资料中参数配置：允许物料名称+规格+规格参数重复 检查存在名称、规格一样的物料
		checkProdName(pb_id);
		// 执行提交操作
		baseDao.submit("PreProductBatch", "pb_id=" + pb_id, "pb_status", "pb_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pb_id", pb_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { pb_id });
	}

	@Override
	public void resSubmitPreProductBatch(int pb_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("PreProductBatch", "pb_statuscode", "pb_id=" + pb_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { pb_id });
		// 执行反提交操作
		baseDao.resOperate("PreProductBatch", "pb_id=" + pb_id, "pb_status", "pb_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pb_id", pb_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { pb_id });
	}

	@Override
	public void resAuditPreProductBatch(int pb_id, String caller) {
		// 只能对状态为[已审核]的表单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("PreProductBatch", "pb_statuscode", "pb_id=" + pb_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("PreProductBatch", "pb_id=" + pb_id, "pb_status", "pb_statuscode", "pb_auditdate", "pb_auditor");
		baseDao.resOperate("PreProductBatch", "pb_id=" + pb_id, "pb_status", "pb_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "pb_id", pb_id);
	}

	// 抓取编号
	@Override
	public void catchProdCode(int pb_id, String caller) {
		List<Object[]> grid = baseDao.getFieldsDatasByCondition("preproductbatchdet left join productkind on pbd_kind=pk_name",
				new String[] { "pbd_id", "pk_code", "pk_length", "pk_maxnum", "pk_id", "pbd_detno" }, "pbd_pbid=" + pb_id
						+ " order by pbd_detno");
		for (Object[] a : grid) {
			Object maxnumb = baseDao.getFieldDataByCondition("preproductbatchdet left join productkind on pbd_kind=pk_name", "pk_maxnum",
					"pbd_id=" + a[0]);
			String length = a[2] == null ? "0" : a[2].toString();
			int b = String.valueOf(maxnumb).equals("0") ? 1 : String.valueOf(maxnumb).length();
			int c = Integer.parseInt(length);
			String code = a[1] == null ? "" : a[1].toString();
			if (b > c) {
				BaseUtil.showError("行" + a[5] + "当前流水号数值长度大于流水号设置长度,请调整流水号长度");
			} else {
				int d = c - b;
				for (int i = 0; i < d; i++) {
					code = code + "0";
				}
				code = code + (Integer.parseInt(maxnumb.toString()) + 1);
				baseDao.execute("update preproductbatchdet set pbd_prodcode='" + code + "' where pbd_id=" + a[0]);
				baseDao.execute("update productkind set pk_maxnum='" + (Integer.parseInt(maxnumb.toString()) + 1) + "' where pk_id=" + a[4]);
			}
		}
	}

	// 检查物料名称+规格+规格参数是否重复
	private void checkProdName(int id) {
		// 获取物料资料中的参数配置: 允许物料名称+规格+规格参数重复。 反馈编号：2017120082 @author:lidy
		String checkProdName = baseDao.getDBSetting("Product", "checkProdName");
		// 同一张单 明细不能存在两个名称、规格一样的情况
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select WMSYS.WM_CONCAT('('||detno||')') from "
						+ "(select WMSYS.WM_CONCAT(pbd_detno) detno from PreProductBatch left join PREPRODUCTBATCHDET on pbd_pbid=pb_id  "
						+ "where pb_id=? group by pbd_prodname,pbd_spec having count(1)>1) order by detno", String.class, id);
		if (dets != null) {
			if (checkProdName == null || "0".equals(checkProdName)) {
				BaseUtil.showError("明细行存在物料名称和规格重复的数据，行号：" + dets + "<hr>");
			} else {
				BaseUtil.appendError("明细行存在物料名称和规格重复的数据，行号：" + dets + "<hr>");
			}
		}
		// 不同的单，状态为已提交的，明细不能存在两个名称、规格一样的情况
		String code = baseDao.getJdbcTemplate().queryForObject(
				"select  WMSYS.WM_CONCAT('<br>申请单号:'||pb_code||',行号:'||pbd_detno) from "
						+ "PreProductBatch left join PREPRODUCTBATCHDET on pbd_pbid=pb_id "
						+ "where pb_id<>? and pb_statuscode='COMMITED' " + "and (pbd_prodname,pbd_spec) in "
						+ "(select pbd_prodname,pbd_spec " + "from PreProductBatch left join PREPRODUCTBATCHDET on pbd_pbid=pb_id "
						+ "where pb_id=?) order by pb_code,pbd_detno", String.class, id, id);
		if (code != null) {
			if (checkProdName == null || "0".equals(checkProdName)) {
				BaseUtil.showError("在其他已提交的批量物料申请存在重复的名称和规格:" + code + "<hr>");
			} else {
				BaseUtil.appendError("在其他已提交的批量物料申请存在重复的名称和规格:" + code + "<hr>");
			}
		}
		// 判断名称规格是否存在重复
		code = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"with tmp_ as (select * from PreProductBatch left join PREPRODUCTBATCHDET "
								+ "on pbd_pbid=pb_id where pb_id=?) select  WMSYS.WM_CONCAT(pr_code) from Product where nvl(pr_statuscode,' ')<>'DISABLE' "
								+ "and (nvl(pr_detail,' '),nvl(pr_spec,' ')) in (select nvl(pbd_prodname,' '),nvl(pbd_spec,' ') from tmp_)",
						String.class, id);
		if (code != null) {
			if (checkProdName == null || "0".equals(checkProdName)) {
				BaseUtil.showError("在物料资料中已经存在相同规格名称的物料，物料编号:" + code + "<hr>");
			} else {
				BaseUtil.appendError("在物料资料中已经存在相同规格名称的物料，物料编号:" + code + "<hr>");
			}
		}

		// 判断新物料申请中名称规格是否存在重复
		code = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"with tmp_ as (select * from PreProductBatch left join PREPRODUCTBATCHDET on "
								+ "pbd_pbid=pb_id where pb_id=?) select  WMSYS.WM_CONCAT(pre_thisid) from PreProduct where nvl(pre_statuscode,' ')='AUDITED' "
								+ "and (nvl(pre_detail,' '),nvl(pre_spec,' ')) in (select nvl(pbd_prodname,' '),nvl(pbd_spec,' ') from tmp_)",
						String.class, id);
		if (code != null) {
			if (checkProdName == null || "0".equals(checkProdName)) {
				BaseUtil.showError("在新物料申请资料中已经存在相同规格名称的申请，申请单号:" + code + "<hr>");
			} else {
				BaseUtil.appendError("在新物料申请资料中已经存在相同规格名称的申请，申请单号:" + code + "<hr>");
			}
		}
	}

	// 生产类型、接收方式根据下拉框设置实际值进行更新
	private void catchCombo(int pb_id) {
		List<Object[]> gridStore = baseDao.getFieldsDatasByCondition("PREPRODUCTBATCHDET", new String[] { "pbd_manutype",
				"pbd_acceptmethod", "pbd_id", "pbd_detno" }, "pbd_pbid=" + pb_id);
		for (Object[] grid : gridStore) {
			SqlRowList rs = baseDao
					.queryForRowSet("select * from datalistcombo where dlc_caller='PreProductBatch' and dlc_fieldname='pbd_manutype' and dlc_display<>'"
							+ grid[0] + "' and dlc_value='" + grid[0] + "'");
			if (rs.next()) {
				baseDao.execute("update Preproductbatchdet set pbd_manutype=(select dlc_display from datalistcombo where dlc_value='"
						+ grid[0] + "' and dlc_caller='PreProductBatch' and dlc_fieldname='pbd_manutype' and rownum<2) where pbd_id="
						+ grid[2]);
			}
			rs = baseDao
					.queryForRowSet("select * from datalistcombo where dlc_caller='PreProductBatch' and dlc_fieldname='pbd_acceptmethod' and dlc_display<>'"
							+ grid[1] + "' and dlc_value='" + grid[1] + "'");
			if (rs.next()) {
				baseDao.execute("update Preproductbatchdet set pbd_acceptmethod=(select dlc_display from datalistcombo where dlc_value='"
						+ grid[1] + "' and dlc_caller='PreProductBatch' and dlc_fieldname='pbd_acceptmethod' and rownum<2) where pbd_id="
						+ grid[2]);
			}
		}
	}
}
