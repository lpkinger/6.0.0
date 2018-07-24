package com.uas.erp.service.pm.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.pm.FeatureValueSetService;

@Service
public class FeatureValueSetServiceImpl implements FeatureValueSetService {
	@Autowired
	private BaseDao baseDao;

	@Override
	public String getDataFieldByCondition(String tablename, String field,
			String condition, String caller) {
		Object ob = baseDao.getFieldDataByCondition(tablename, field, condition);
		return ob == null ? null : ob.toString();
	}

	@Override
	public void updateDataFieldByCondition(String tablename, String[] field,
			String[] fieldvalue, String condition, String caller) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < fieldvalue.length; i++) {
			sb.append(field[i] + "='" + fieldvalue[i] + "'");
			if (i != fieldvalue.length - 1) {
				sb.append(",");
			}
		}
		Object oldvalue = null, newvalue;
		if (tablename.equalsIgnoreCase("saledetail")) {
			oldvalue = baseDao.getFieldDataByCondition("saledetail", "NVL(sd_specdescription,' ')", condition);
		}
		// 保存
		baseDao.updateByCondition(tablename, sb.toString(), condition);
		// 销售订单的特征值修改后记录日志，并对物料的计划员和采购员发出寻呼
		if (tablename.equalsIgnoreCase("saledetail")) {
			newvalue = baseDao.getFieldDataByCondition("saledetail", "NVL(sd_specdescription,' ')", condition);
			if (!oldvalue.equals(newvalue)) {
				Object[] objs = baseDao.getFieldsDataByCondition("saledetail", new String[] { "sd_said", "sd_detno" },
						condition);
				String[] newarr = newvalue.toString().split("\\|");
				String[] oldarr = oldvalue.toString().split("\\|");
				String addvalue = "", changestr = "";
				for (String s : newarr) {
					if (oldvalue.toString().split(":").length > 1) {
						for (String s2 : oldarr) {
							if (s.split(":")[0].equals(s2.split(":")[0])) {
								if (!s.split(":")[1].equals(s2.split(":")[1])) {
									changestr += "变更" + s.split(":")[0] + "为:" + s.split(":")[1] + "原值:"
											+ s2.split(":")[1];
								}
							}
						}
					}
					// 新录入特征值
					if (!("|" + oldvalue.toString()).contains("|" + s.split(":")[0] + ":")) {
						addvalue += "," + s;
					}
				}
				addvalue = addvalue.equals("") ? "" : "录入：" + addvalue.substring(1);
				if (oldvalue.toString().split(":").length > 1) {
					for (String s : oldarr) {
						if (!("|" + newvalue.toString()).contains("|" + s.split(":")[0] + ":")) {
							changestr += "删除:" + s;
						}
					}
				}

				if (!addvalue.equals("") || !changestr.equals("")) {
					// 记录操作
					baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "保存特征值序号:" + objs[1],
							addvalue + changestr.toString(), "Sale!Abnormal|sa_id=" + objs[0]));
					// 记录操作
					baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "保存特征值序号:" + objs[1],
							addvalue + changestr.toString(), "Sale|sa_id=" + objs[0]));
				}
				if (!changestr.equals("")) {
					// 发寻呼
					SqlRowList rs = baseDao
							.queryForRowSet("select em_id,em_name,sd_code from saledetail left join product on pr_code=sd_prodcode left join employee on (em_code=pr_planercode or em_code=pr_buyercode) where "
									+ condition);
					while (rs.next()) {
						if (rs.getInt("em_id") > 0) {
							List<String> sqls = new ArrayList<String>();
							int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
							int prd_id = baseDao.getSeqId("PAGINGRELEASEDETAIL_SEQ");
							sqls.add("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_releaserid,pr_context)values('"
									+ pr_id
									+ "','"
									+ SystemSession.getUser().getEm_name()
									+ "',"
									+ DateUtil.parseDateToOracleString(Constant.YMD, new Date())
									+ ",'"
									+ SystemSession.getUser().getEm_id()
									+ "','非正常销售单"
									+ rs.getString("sd_code")
									+ "特征值修改:"
									+ changestr + "')");
							sqls.add("insert into pagingreleasedetail(prd_id,prd_prid,PRD_RECIPIENTID,PRD_RECIPIENT) values('"
									+ prd_id
									+ "','"
									+ pr_id
									+ "','"
									+ rs.getString("em_id")
									+ "','"
									+ rs.getString("em_name") + "')");
							
							//保存到历史消息表
							int IH_ID=baseDao.getSeqId("ICQHISTORY_SEQ");
							sqls.add("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
									+ "select "+IH_ID+",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
									+ " where pr_id="+pr_id);
							sqls.add("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
									+ "select ICQHISTORYdetail_seq.nextval,"+IH_ID+",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid="+pr_id+"and ("+IH_ID+",prd_recipient,prd_recipientid) not in (select IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID from ICQHISTORYdetail)");
							baseDao.execute(sqls);
						}
					}
				}
			}

		}
	}

	@Override
	public String getRealCode(String prodcode, String specdescription, String fromwhere,
			String caller) {
		String flag = "";
		SqlRowList rs = baseDao
				.queryForRowSet("select fe_code,fd_valuecode from feature,featuredetail where fe_id=fd_feid and '|"
						+ specdescription
						+ "|' like '%|'||fe_code||':'||fd_valuecode||'|%' and fd_statuscode='DISABLE'");
		if (rs.next()) {
			BaseUtil.showError("特征ID:" + rs.getObject("fe_code") + ",特征值:" + rs.getObject("fd_valuecode") + "已经禁用");
		}
		addRelation(prodcode, specdescription, fromwhere);
		Object id = baseDao.getFieldDataByCondition("BOM", "bo_id", "BO_MOTHERCODE='" + prodcode + "'");
		if (id != null) {
			String errMsg = baseDao.callProcedure("MM_SetProdBomStruct", new Object[] { id, specdescription });
			if (errMsg != null && errMsg != "") {
				BaseUtil.showError(errMsg);
			} else {
				flag = (String) baseDao.getFieldDataByCondition("Product", "pr_code", "pr_specdescription='"
						+ specdescription + "' and pr_refno='" + prodcode + "'");
				List<Object> list = baseDao.getFieldDatasByCondition(
						"BOMStruct left join product on pr_code=bs_soncode", "bs_soncode",
						"bs_topbomid='" + id + "' and bs_topmothercode='" + flag + "' and pr_specvalue='NOTSPECIFIC'");
				String procodestr = "";
				for (Object o : list) {
					procodestr += "," + o.toString();
				}
				if (!procodestr.equals("")) {
					if (!"Product".equalsIgnoreCase(fromwhere)) {
						BaseUtil.showError("物料：" + procodestr.substring(1) + "对应的特征没有定义实体料号");
					}
				}
			}
		} else {
			flag = baseDao.callProcedure("bGetFeatureProdCode", new String[] { prodcode, specdescription }, 1);
		}
		if (flag == null || flag.equals("")) {
			BaseUtil.showError("特征或料号错误，不能产生料号");
		} else if (flag.equals(prodcode)) {
			BaseUtil.showError("选择的特征值未指定实体料号");
		}
		return flag;
	}

	public void addRelation(String prodcode, String specdescription, String fromwhere) {
		List<Object[]> list = baseDao.getFieldsDatasByCondition("ProdFeature left join feature on fe_code=pf_fecode",
				new String[] { "fe_code", "fe_relation", "fe_name" }, "pf_prodcode='" + prodcode
						+ "' and nvl(fe_relation,' ')<>' ' order by pf_detno");
		for (Object[] obs : list) {
			if ("ALLOW".equalsIgnoreCase(obs[1].toString()) || "可用".equalsIgnoreCase(obs[1].toString())) {
				List<Object[]> list2 = baseDao
						.getFieldsDatasByCondition(
								"FeatureRelation",
								new String[] { "fr_relavalue", "fr_relaname", "fr_value" },
								"fr_fecode='"
										+ obs[0]
										+ "' and '"
										+ specdescription
										+ "' like '%|' || fr_fecode || ':' || fr_valuecode || '|%' and nvl(fr_status,' ')<>'已禁用' and "
										+ "'|" + specdescription
										+ "|' not like '%|' || fr_relacode || ':' || fr_relavaluecode || '|%'");
				if (list2 != null && list2.size() > 0) {
					BaseUtil.showError("特征[" + list2.get(0)[1].toString() + "]所选值不在特征[" + obs[2].toString()
							+ "]的值定义的允许范围内");
				}
			}
			if ("FORBID".equalsIgnoreCase(obs[1].toString()) || "禁用".equalsIgnoreCase(obs[1].toString())) {
				List<Object[]> list2 = baseDao
						.getFieldsDatasByCondition(
								"FeatureRelation",
								new String[] { "fr_relavalue", "fr_relaname", "fr_value" },
								"fr_fecode='"
										+ obs[0]
										+ "' and '"
										+ specdescription
										+ "' like '%|' || fr_fecode || ':' || fr_valuecode || '|%' and nvl(fr_status,' ')<>'已禁用' and "
										+ "'|" + specdescription
										+ "|'  like '%|' || fr_relacode || ':' || fr_relavaluecode || '|%'");

				if (list2 != null && list2.size() > 0) {
					BaseUtil.showError("特征[" + list2.get(0)[1].toString() + "]所选值:" + list2.get(0)[0].toString()
							+ " 在特征[" + obs[2].toString() +
							"]的值[" + list2.get(0)[2].toString() + "]的定义里禁止");
				}
			}
		}
	}

	@Override
	public Object[] getDataFieldsByCondition(String tablename, String[] field,
			String condition, String caller) {
		return baseDao.getFieldsDataByCondition(tablename, field, condition);
	}
}
