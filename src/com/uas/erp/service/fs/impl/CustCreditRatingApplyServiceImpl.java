package com.uas.erp.service.fs.impl;

import java.util.List;
import java.util.Map;

import org.drools.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Master;
import com.uas.erp.service.fs.CustCreditRatingApplyService;

@Service("custCreditRatingApplyService")
public class CustCreditRatingApplyServiceImpl implements CustCreditRatingApplyService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCustCreditRatingApply(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String type = "信用评级申请";
		if ("CustCreditRatingApply!Moral".equals(caller)) {
			type = "企业信用风险";
		}
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("CustCreditRatingApply", "cra_code='" + store.get("cra_code") + "' and cra_type='" + type + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存PriceMould
		baseDao.execute(SqlUtil.getInsertSqlByFormStore(store, "CustCreditRatingApply", new String[] {}, new Object[] {}));
		// 记录操作
		baseDao.logger.save(caller, "cra_id", store.get("cra_id"));
		Master master = SystemSession.getUser().getCurrentMaster();
		baseDao.execute("update CustCreditRatingApply set cra_cuvecode='" + master.getMa_uu() + "', cra_cuvename='"
				+ master.getMa_function() + "' WHERE cra_id=" + store.get("cra_id")
				+ " AND NVL(cra_cuvename,' ')=' ' AND NVL(cra_issyscust,0)<>0");
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void updateCustCreditRatingApply(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[] { store });
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "CustCreditRatingApply", "cra_id"));
		// 记录操作
		baseDao.logger.update(caller, "cra_id", store.get("cra_id"));
		Master master = SystemSession.getUser().getCurrentMaster();
		baseDao.execute("update CustCreditRatingApply set cra_cuvecode='" + master.getMa_uu() + "', cra_cuvename='"
				+ master.getMa_function() + "' WHERE cra_id=" + store.get("cra_id")
				+ " AND NVL(cra_cuvename,' ')=' ' AND NVL(cra_issyscust,0)<>0");
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void deleteCustCreditRatingApply(int cra_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { cra_id });
		// 删除主表内容
		baseDao.deleteById("CustCreditRatingApply", "cra_id", cra_id);
		baseDao.logger.delete(caller, "cra_id", cra_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { cra_id });
	}

	@Override
	public void submitCustCreditRatingApply(int cra_id, String caller) {
		Master master = SystemSession.getUser().getCurrentMaster();
		baseDao.execute("update CustCreditRatingApply set cra_cuvecode='" + master.getMa_uu() + "', cra_cuvename='"
				+ master.getMa_function() + "' WHERE cra_id=" + cra_id + " AND NVL(cra_cuvename,' ')=' ' AND NVL(cra_issyscust,0)<>0");
		// 只能对状态为[在录入]的表单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("CustCreditRatingApply", new String[] { "cra_statuscode", "cra_ascode",
				"to_char(cra_date,'yyyymm')", "cra_yearmonth", "cra_cuvename" }, "cra_id=" + cra_id);
		StateAssert.submitOnlyEntering(status[0]);
		if (!StringUtil.hasText(status[2])) {
			BaseUtil.showError("申请日期不能为空！");
		}
		if (!StringUtil.hasText(status[3]) && "CustCreditRatingApply".equals(caller)) {
			BaseUtil.showError("财务报表年份不能为空！");
		}
		if (!StringUtil.hasText(status[4])) {
			BaseUtil.showError("请选择客户！");
		}
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(cu_name) from CustomerInfor where cu_name=? and cu_statuscode<>'AUDITED'", String.class, status[4]);
		if (dets != null) {
			BaseUtil.showError("客户[" + dets + "]状态不等于已审核！");
		}
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { cra_id });
		if ("CustCreditRatingApply".equals(caller)) {
			String res = baseDao.callProcedure("SP_COUNTFAITEMS", new Object[] { status[3], cra_id });
			if (StringUtil.hasText(res) && !res.equals("OK")) {
				BaseUtil.showError(res);
			} else {
				Object yearmonth = baseDao.getFieldDataByCondition("FAITEMS", "MAX(FI_YEAR)", "SUBSTR(FI_YEAR,0,4)=" + status[3]
						+ " AND FI_CUNAME='" + status[4] + "'");
				if (yearmonth != null) {
					baseDao.procedure("SP_COUNTCREDITTARGETSITEMS", new Object[] { yearmonth, status[4] });
				}
			}
			res = baseDao.callProcedure("SP_COUNTSYSCREDIT", new Object[] { cra_id, status[1], status[3] });
			if (StringUtil.hasText(res) && !res.equals("OK")) {
				BaseUtil.showError(res);
			}
		}

		// 执行提交操作
		baseDao.submit("CustCreditRatingApply", "cra_id=" + cra_id, "cra_status", "cra_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "cra_id", cra_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { cra_id });
	}

	@Override
	public void resSubmitCustCreditRatingApply(int cra_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("CustCreditRatingApply", "cra_statuscode", "cra_id=" + cra_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { cra_id });

		// 执行反提交操作
		baseDao.resOperate("CustCreditRatingApply", "cra_id=" + cra_id, "cra_status", "cra_statuscode");

		// 清除CustCreditTargets表中申请单ID为当前申请单中数据
		baseDao.deleteByCondition("CustCreditTargets", "cct_craid=?", cra_id);

		// 清除系统评定、人工评定、最终评定结果
		baseDao.updateByCondition("CustCreditRatingApply", "cra_sysscore =null,cra_sysdate=null,cra_level=null,cra_manscore =null,"
				+ "cra_mandate=null,cra_manstatus=null,cra_score=null,cra_creditrating=null", "cra_id =" + cra_id);

		// 记录操作
		baseDao.logger.resSubmit(caller, "cra_id", cra_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { cra_id });
	}

	@Override
	public void auditCustCreditRatingApply(int cra_id, String caller) {
		// 只能对已提交进行审核操作
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select cra_statuscode,cra_creditrating,cra_cuvename,cra_sysscore,cra_manscore,as_finance,as_nofinance from CustCreditRatingApply,AssessScheme where cra_ascode=as_code and cra_id=?",
						cra_id);
		if (rs.next()) {
			StateAssert.auditOnlyCommited(rs.getObject("cra_statuscode"));
			double cra_sysscore = rs.getGeneralDouble("cra_sysscore");
			double cra_manscore = rs.getGeneralDouble("cra_manscore");
			double as_finance = rs.getGeneralDouble("as_finance"); // 财务占比
			double as_nofinance = rs.getGeneralDouble("as_nofinance"); // 非财务占比
			if (as_finance != 0) {
				cra_sysscore = cra_sysscore * as_finance / 100;
			}
			if (as_nofinance != 0) {
				cra_manscore = cra_manscore * as_nofinance / 100;
			}
			baseDao.execute("update CustCreditRatingApply set CRA_SCORE= ROUND(" + (cra_sysscore + cra_manscore) + ",2) where cra_id="
					+ cra_id);
			baseDao.execute("UPDATE CustCreditRatingApply SET CRA_CREDITRATING=(SELECT cr_code FROM CreditRatings WHERE nvl(cr_scorebegin,0)<=CRA_SCORE AND nvl(cr_scoreend,0)>=CRA_SCORE) WHERE CRA_ID="
					+ cra_id);
			SqlRowList maxLevel = baseDao.queryForRowSet("select cr_level,cct_ctdetno,cct_ctname,ctc_display,cr_code FROM "
					+ "CustCreditTargets  inner join  CreditTargetsCombo on cct_ctid = ctc_ctid and cct_itemvalue=ctc_value "
					+ "inner join CreditRatings on ctc_maxlevel = cr_code where nvl(ctc_maxlevel,' ')<>' ' and cct_craid = ? "
					+ "order by cr_level desc", cra_id);
			if (maxLevel.hasNext()) {
				Object level = baseDao.getFieldDataByCondition("CreditRatings", "cr_level", "cr_code='" + rs.getString("cra_creditrating")
						+ "'");
				if (level != null) {
					String allowLevel = null;
					String remark = "";
					while (maxLevel.next()) {
						if (Integer.parseInt(level.toString()) < maxLevel.getGeneralInt("cr_level")) {
							if (allowLevel == null) {
								allowLevel = maxLevel.getString("cr_code");
							}
							remark += "指标序号:" + maxLevel.getString("cct_ctdetno") + "，" + maxLevel.getString("cct_ctname") + "为"
									+ maxLevel.getString("ctc_display") + "时，允许的最高等级为" + maxLevel.getString("cr_code") + "；\n";
						}
					}
					if (allowLevel != null) {
						baseDao.updateByCondition("CustCreditRatingApply", "cra_creditrating = '" + allowLevel + "',cra_tip = '" + remark
								+ "'", "cra_id=" + cra_id);
					}
				}
			}
			// 执行审核前的其它逻辑
			handlerService.handler(caller, "audit", "before", new Object[] { cra_id });

			baseDao.audit("CustCreditRatingApply", "cra_id=" + cra_id, "cra_status", "cra_statuscode", "cra_auditdate", "cra_auditman");

			// 更新最终评定状态为已评定
			baseDao.updateByCondition("CustCreditRatingApply", "cra_finalstatus = '已评定',cra_valid = 'VALID'", "cra_id = " + cra_id);

			// 更新保理进度
			if ("CustCreditRatingApply".equals(caller)) {
				baseDao.updateByCondition("FINBUSINAPPLY", "FS_STATUS = '应收账款转让',FS_CRDRATDATE = sysdate",
						"FS_CUSTNAME = '" + rs.getString("cra_cuvename") + "' and FS_ACCEPTDATE is null");
				Master master = SystemSession.getUser().getCurrentMaster();
				if (master != null && !StringUtils.isEmpty(master.getMa_soncode())) {// 父级账套
					String[] syncSon = baseDao.getDBSettingArray("Customer!Infor", "syncSon");
					if (StringUtil.hasText(syncSon)) {
						for (String obj : syncSon) {
							baseDao.execute("update " + obj
									+ ".FINBUSINAPPLY set FS_STATUS = '应收账款转让',FS_CRDRATDATE = sysdate where FS_CUSTNAME = '"
									+ rs.getString("cra_cuvename") + "' and FS_ACCEPTDATE is null");
						}
					}
				}
			}

			// 记录操作
			baseDao.logger.audit(caller, "cra_id", cra_id);
			// 执行审核后的其它逻辑
			handlerService.handler(caller, "audit", "after", new Object[] { cra_id });
		}
	}

	@Override
	public void resAuditCustCreditRatingApply(int cra_id, String caller) {
		// 只能对状态为[已审核]的采购单进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("CustCreditRatingApply", "cra_statuscode,cra_cuvename", "cra_id=" + cra_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		baseDao.resAuditCheck("CustCreditRatingApply", cra_id);
		handlerService.beforeResAudit(caller, new Object[] { cra_id });
		// 执行反审核操作
		baseDao.resAudit("CustCreditRatingApply", "cra_id=" + cra_id, "cra_status", "cra_statuscode", "cra_auditman", "cra_auditdate");
		// 清除CustCreditTargets表中申请单ID为当前申请单中数据
		baseDao.deleteByCondition("CustCreditTargets", "cct_craid=?", cra_id);
		// 清除系统评定、人工评定、最终评定结果
		baseDao.updateByCondition(
				"CustCreditRatingApply",
				"cra_sysscore =null,cra_sysdate=null,cra_level=null,cra_manscore =null,cra_mandate=null,cra_manstatus=null,cra_score=null,cra_creditrating=null,cra_finalstatus=null,cra_description2=null,cra_valid = null",
				"cra_id =" + cra_id);
		// 更新保理进度
		if ("CustCreditRatingApply".equals(caller)) {
			baseDao.updateByCondition("FINBUSINAPPLY", "FS_STATUS = '收集材料',FS_CRDRATDATE = null", "FS_CUSTNAME = '" + status[1]
					+ "' and FS_ACCEPTDATE is null");
			Master master = SystemSession.getUser().getCurrentMaster();
			if (master != null && !StringUtils.isEmpty(master.getMa_soncode())) {// 父级账套
				String[] syncSon = baseDao.getDBSettingArray("Customer!Infor", "syncSon");
				if (StringUtil.hasText(syncSon)) {
					for (String obj : syncSon) {
						baseDao.execute("update " + obj
								+ ".FINBUSINAPPLY set FS_STATUS = '收集材料',FS_CRDRATDATE = null where FS_CUSTNAME = '" + status[1] + "' and FS_ACCEPTDATE is null");
					}
				}
			}
		}

		// 记录操作
		baseDao.logger.resAudit(caller, "cra_id", cra_id);
		handlerService.afterResAudit(caller, new Object[] { cra_id });
	}

	@Override
	public String getDisplay(String caller, Integer craid, String type) {
		String condition = null;
		Float score = (float) 0;

		if (craid == null || "".equals(craid)) {
			condition = "1=2";
		} else {
			condition = "cra_id =" + craid;
		}

		Object[] cra = baseDao.getFieldsDataByCondition("CustCreditRatingApply", new String[] { "cra_cuvename", "cra_yearmonth",
				"cra_score", "cra_creditrating" }, condition);
		if (cra != null && "CustCreditRatingApply!Moral".equals(caller)) {
			boolean bool = baseDao.checkByCondition("CustCreditTargets", "CCT_CRAID = " + craid);
			if (bool) {
				baseDao.execute("insert into CustCreditTargets(CCT_ID,CCT_CRAID,CCT_ASDID,CCT_CUSTCODE,CCT_CUSTNAME,CCT_CTDETNO,CCT_CTID,"
						+ "CCT_CTNAME,CCT_ITEMVALUE,CCT_TYPE,CCT_SCORE) SELECT CUSTCREDITTARGETS_SEQ.nextval, " + craid + ", asd_id, "
						+ "CRA_CUVECODE, CRA_CUVENAME, ASD_CTDETNO,asd_ctid, asd_ctname, null, ASD_TYPE, 0 from CustCreditRatingApply "
						+ "left join AssessScheme on as_code=CRA_ASCODE left join AssessSchemeDetail on asd_asid=as_id where cra_id = ?",
						craid);
			}
		}

		condition = "cct_craid=" + craid + " and nvl(ct_isleaf,0)<>0";

		if (type != null && !"".equals(type)) {
			condition += " and cct_type ='" + type + "'";
		}

		score = baseDao.queryForObject("select nvl(sum(cct_score),0) from CustCreditTargets left join CreditTargets on cct_ctid=ct_id "
				+ "where " + condition, Float.class);

		if (score == null) {
			score = (float) 0;
		}
		StringBuffer dis = new StringBuffer();
		if ("CustCreditRatingApply".equals(caller)) {
			if (cra[1] == null || "".equals(cra[1]) || "0".equals(cra[1].toString())) {
				cra[1] = DateUtil.getYearmonth();
			}
			String year = cra[1].toString().substring(0, 4);
			dis.append("<div id=\"container\"><div id=\"content\"><p style=\"font-size:20px\"><font color=\"blue\"><b>" + cra[0] + "</b> "
					+ year + "年财务报表</font></p>");

			if ("FINANCE".equals(type)) {
				dis.append("<p style=\"font-size:16px\"><font color=\"blue\">得分:</font><font color=\"black\"><b>" + score + "</b></font>"
						+ "</p></div></div>");
			} else if (cra[3] != null && !"".equals(cra[3])) {
				if (type != null && !"".equals(type)) {
					dis.append("<p style=\"font-size:16px\"><font color=\"blue\">得分:</font><font color=\"black\"><b>" + score
							+ "</b></font>  ");
				} else {
					dis.append("<p style=\"font-size:16px\">");
				}
				dis.append("<font color=\"blue\">综合评级结果:" + "评级总分 </font><font color=\"black\"><b>" + cra[2] + "</b></font>"
						+ "<font color=\"blue\">,信用等级  </font><font color=\"black\"><b>" + cra[3] + "</b></font></p></div></div>");
			}
		} else {
			dis.append("<div id=\"container\"><div id=\"content\"><p style=\"font-size:20px\"><font color=\"blue\"><b>" + cra[0]
					+ "</b></font></p>");

			if (type != null && !"".equals(type)) {
				dis.append("<p style=\"font-size:16px\"><font color=\"blue\">得分:</font><font color=\"black\"><b>" + score + "</b></font>  ");
			} else {
				dis.append("<p style=\"font-size:16px\">");
			}
		}

		return dis.toString();
	}

	@Override
	public void saveCustCreditTargets(String datas) {
		List<String> gridsql = SqlUtil.getUpdateSqlbyGridStore(datas, "CustCreditTargets", "cct_id");
		baseDao.execute(gridsql);
	}

	@Override
	public void MeasureScore(int craid, String type) {
		String res = null;
		if ("FINANCE".equals(type)) {
			res = baseDao.callProcedure("SP_COUNTOTHERCREDIT", new Object[] { craid });
		} else {
			res = baseDao.callProcedure("SP_COUNTUNFACREDIT", new Object[] { craid });
			// 更新评估人和评估日期
			baseDao.updateByCondition("CustCreditTargets", "cct_man='" + SystemSession.getUser().getEm_name() + "'",
					"cct_type= 'NOFINANCE' and cct_craid=" + craid);
		}
		if (StringUtil.hasText(res) && !res.equals("OK")) {
			BaseUtil.showError(res);
		}
	}
}
