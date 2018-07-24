package com.uas.erp.service.fs.impl;

import java.util.ArrayList;
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
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.fs.CreditTargetsService;

@Service
public class CreditTargetsServiceImpl implements CreditTargetsService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public List<Map<String, Object>> getColItems() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		SqlRowList rs = baseDao.queryForRowSet("SELECT FIF_NAME,FIF_FIELD FROM Faitemsformula");
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("text", rs.getString("FIF_NAME"));
			map.put("tooltip", rs.getString("FIF_NAME"));
			map.put("data", rs.getString("FIF_FIELD"));
			list.add(map);
		}
		return list;
	}

	@Override
	public void saveCreditTargets(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		if (!StringUtil.hasText(store.get("ct_code"))) {
			String code = baseDao.sGetMaxNumber("CreditTargets", 2);
			store.put("ct_code", code);
		}

		createDetno(store);

		handlerService.handler(caller, "save", "before", new Object[] { store });

		String formSql = SqlUtil.getInsertSqlByFormStore(store, "CreditTargets", new String[] {}, new Object[] {});
		baseDao.execute(formSql);

		countStandard(store);
		baseDao.logger.save(caller, "ct_id", store.get("ct_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void updateCreditTargets(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);

		createDetno(store);

		handlerService.handler(caller, "save", "before", new Object[] { store });

		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "CreditTargets", "ct_id");
		baseDao.execute(formSql);
		countStandard(store);
		// 记录操作
		baseDao.logger.update(caller, "ct_id", store.get("ct_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void deleteCreditTargets(int ct_id, String caller) {
		Object status[] = baseDao.getFieldsDataByCondition("CreditTargets", new String[] { "ct_statuscode", "ct_type", "CT_FIELD",
				"nvl(ct_isleaf,0)" }, "ct_id=" + ct_id);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { ct_id });
		if (status[1] != null && status[2] != null && "FINANCE".equals(status[1]) && !"0".equals(status[3])) {
			baseDao.execute("update CREDITTARGETS set CT_FIELD=null where ct_id=" + ct_id);
			baseDao.execute("alter table CREDITTARGETSITEMS drop column " + status[2]);
		}
		// 删除主表内容
		baseDao.deleteById("CreditTargets", "ct_id", ct_id);
		baseDao.logger.delete(caller, "ct_id", ct_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { ct_id });
	}

	@Override
	public void submitCreditTargets(int ct_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("CreditTargets", "ct_statuscode", "ct_id=" + ct_id);
		StateAssert.submitOnlyEntering(status);

		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { ct_id });

		// 执行提交操作
		baseDao.submit("CreditTargets", "ct_id=" + ct_id, "ct_status", "ct_statuscode");

		// 记录操作
		baseDao.logger.submit(caller, "ct_id", ct_id);

		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { ct_id });
	}

	@Override
	public void resSubmitCreditTargets(int ct_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("CreditTargets", "ct_statuscode", "ct_id=" + ct_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { ct_id });

		// 执行反提交操作
		baseDao.resOperate("CreditTargets", "ct_id=" + ct_id, "ct_status", "ct_statuscode");

		// 记录操作
		baseDao.logger.resSubmit(caller, "ct_id", ct_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { ct_id });
	}

	@Override
	public void auditCreditTargets(int ct_id, String caller) {
		SqlRowList rs = baseDao.queryForRowSet("select ct_statuscode,ct_type,ct_isleaf,ct_field,ct_subof from CreditTargets where ct_id=?",
				ct_id);
		if (rs.next()) {
			StateAssert.auditOnlyCommited(rs.getObject("ct_statuscode"));
			// 执行审核前的其它逻辑
			handlerService.handler(caller, "audit", "before", new Object[] { ct_id });
			baseDao.audit("CreditTargets", "ct_id=" + ct_id, "ct_status", "ct_statuscode", "ct_auditdate", "ct_auditman");
			if (rs.getGeneralInt("ct_isleaf") != 0) {
				if ("FINANCE".equals(rs.getGeneralString("ct_type")) && !StringUtil.hasText(rs.getObject("ct_field"))) {
					int count = baseDao.getCount("select count(1) from CREDITTARGETS where ct_type='FINANCE' and nvl(ct_isleaf,0)<>0");
					for (int i = 0; i < 5; i++) {
						int argCount = baseDao.getCountByCondition("user_tab_columns",
								"table_name='CREDITTARGETSITEMS' and column_name in ('CTI_NUM" + count + "')");
						if (argCount > 0) {
							count = count + 1;
						} else {
							break;
						}
					}
					baseDao.execute("alter table CREDITTARGETSITEMS add CTI_NUM" + count + " number");
					baseDao.execute("update CREDITTARGETS set CT_FIELD='CTI_NUM" + count + "' where ct_id=" + ct_id);
				}
				// 计算父节点的标准分
				int subof = rs.getGeneralInt("ct_subof");
				if (subof != 0) {
					for (int i = 0; i < 5; i++) {
						if (subof != 0) {
							baseDao.execute("UPDATE CREDITTARGETS A SET CT_STANDARD=NVL((SELECT SUM(NVL(CT_STANDARD,0)) FROM CREDITTARGETS B WHERE A.CT_ID=B.CT_SUBOF),0) WHERE ct_id="
									+ subof);
							Object id = baseDao.getFieldDataByCondition("CREDITTARGETS", "ct_subof", "ct_id=" + subof);
							if (id != null) {
								subof = Integer.parseInt(id.toString());
							}
						} else {
							break;
						}
					}
				}
			}
			// 记录操作
			baseDao.logger.audit(caller, "ct_id", ct_id);
			// 执行审核后的其它逻辑
			handlerService.handler(caller, "audit", "after", new Object[] { ct_id });
		}
	}

	@Override
	public void resAuditCreditTargets(int ct_id, String caller) {
		// 只能对状态为[已审核]的采购单进行反审核操作!
		Object status[] = baseDao.getFieldsDataByCondition("CreditTargets", new String[] { "ct_statuscode", "ct_type", "CT_FIELD",
				"nvl(ct_isleaf,0)" }, "ct_id=" + ct_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		baseDao.resAuditCheck("CreditTargets", ct_id);
		handlerService.beforeResAudit(caller, new Object[] { ct_id });
		// 执行反审核操作
		baseDao.resAudit("CreditTargets", "ct_id=" + ct_id, "ct_status", "ct_statuscode", "ct_auditman", "ct_auditdate");
		// 记录操作
		baseDao.logger.resAudit(caller, "ct_id", ct_id);
		handlerService.afterResAudit(caller, new Object[] { ct_id });
	}

	// 如果序号为空，自动生成序号
	private void createDetno(Map<Object, Object> store) {

		if (store.get("ct_detno") == null || "".equals(store.get("ct_detno").toString())) {
			try {
				if (store.get("ct_subof") == null || "".equals(store.get("ct_subof").toString())) {
					store.put("ct_subof", 0);
				}
				// 如果是第一条第一层为条数+1，其它层为父序号+“.1*条数“
				int count = baseDao.getCountByCondition("CreditTargets",
						"ct_type ='" + store.get("ct_type") + "' and ct_subof=" + store.get("ct_subof"));

				if ("0".equals(store.get("ct_subof").toString())) {
					store.put("ct_detno", count + 1);
				} else {
					Object subdetno = baseDao.getFieldDataByCondition("CreditTargets", "ct_detno",
							"nvl(ct_detno,' ')<> ' '  and ct_type ='" + store.get("ct_type") + "' and ct_id=" + store.get("ct_subof"));
					if (subdetno == null) {
						Object detno = baseDao.getFieldDataByCondition(
								"(select rownum rn, t.* from (select ct_detno from CreditTargets where nvl(ct_detno,' ')<> ' ' and ct_type ='"
										+ store.get("ct_type") + "' and ct_subof=" + store.get("ct_subof") + " order by ct_id desc) t)",
								"ct_detno", "rn<2");
						if (detno != null && detno.toString().lastIndexOf(".") > -1) {
							subdetno = detno.toString().substring(0, detno.toString().lastIndexOf("."));
						}
					}

					if (subdetno != null) {
						String ct_detno = subdetno.toString() + "." + (count + 1);
						store.put("ct_detno", ct_detno);
					}
				}
			} catch (Exception e) {
				BaseUtil.showError("生成序号错误：" + e.getMessage());
			}
		}
	}

	// 子项标准分合计为父项标准分
	private void countStandard(Map<Object, Object> store) {
		if (store.get("ct_subof") != null && !"".equals(store.get("ct_subof").toString()) && !"0".equals(store.get("ct_subof").toString())) {
			try {
				Object count = baseDao.getFieldDataByCondition("CreditTargets", "sum(ct_standard)", "ct_type ='" + store.get("ct_type")
						+ "' and ct_subof=" + store.get("ct_subof"));
				baseDao.updateByCondition("CreditTargets", "ct_standard =" + count, "ct_id = " + store.get("ct_subof"));
			} catch (Exception e) {
				BaseUtil.showError("合计标准分错误：" + e.getMessage());
			}
		}
	}

	@Override
	public void saveItemsValue(String datas) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(datas);

		try {
			List<String> gridSql = SqlUtil.getInsertOrUpdateSqlbyGridStore(gstore, "CreditTargetsCombo", "ctc_id");
			baseDao.execute(gridSql);

			Object ctid = gstore.get(0).get("ctc_ctid");

			SqlRowList rs = baseDao.queryForRowSet("select * from CreditTargetsCombo where ctc_ctid = ?", ctid);

			// 生成评分公式说明和评分公式sql
			String assessdesc = "";
			while (rs.next()) {

				assessdesc += "；" + rs.getString("ctc_display");
				assessdesc += "(" + rs.getGeneralDouble("ctc_rate") + "%";

				Integer addscore = rs.getGeneralInt("ctc_addscore");
				if (addscore > 0) {
					assessdesc += ",加" + addscore + "分";
				} else if (addscore < 0) {
					assessdesc += ",扣" + Math.abs(addscore) + "分";
				}
				if (rs.getString("ctc_maxlevel") != null && !"".equals(rs.getString("ctc_maxlevel").toString().trim())
						&& !"0".equals(rs.getString("ctc_maxlevel").toString().trim())) {
					assessdesc += ",限定其最高等级不能超过" + rs.getString("ctc_maxlevel") + "级";
				}
				assessdesc += ")";
			}
			assessdesc = assessdesc.substring(1);
			String assesssql = "SELECT CCT_SCORE*nvl(CTC_RATE,0)/100+CTC_ADDSCORE as score FROM CUSTCREDITTARGETS left join CREDITTARGETSCOMBO on CCT_CTID=CTC_CTID and CUSTCREDITTARGETS.CCT_ITEMVALUE=CREDITTARGETSCOMBO.CTC_VALUE where CCT_ID=v_id";
			baseDao.updateByCondition("CreditTargets", "ct_assessdesc='" + assessdesc + "',ct_assesssql='" + assesssql + "'", "ct_id="
					+ ctid);
		} catch (Exception e) {
			BaseUtil.showError("保存失败,错误：" + e.getMessage());
		}
	}

	@Override
	public void deleteItemsValue(int ctid) {
		try {
			baseDao.deleteByCondition("CreditTargetsCombo", "ctc_ctid = " + ctid);
			// 清空评分公式说明和评分公式sql
			baseDao.updateByCondition("CreditTargets", "ct_assessdesc=null,ct_assesssql=null", "ct_id=" + ctid);
		} catch (Exception e) {
			BaseUtil.showError("删除失败,错误：" + e.getMessage());
		}
	}

	@Override
	public void testSQL(String sql) {
		try {
			sql = sql.replace("v_year", "0");
			baseDao.execute(sql);
		} catch (Exception e) {
			BaseUtil.showError("计算公式错误," + e.getMessage());
		}
	}
}
