package com.uas.erp.service.pm.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

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
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.dao.common.AppMouldDao;
import com.uas.erp.model.Key;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.ARBillService;
import com.uas.erp.service.pm.AppMouldService;

@Service("appMouldService")
public class AppMouldServiceImpl implements AppMouldService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private AppMouldDao appMouldDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private TransferRepository transferRepository;
	@Autowired
	private ARBillService arBillService;

	@Override
	public void saveAppMould(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		baseDao.asserts.nonExistCode("AppMould", "app_code", store.get("app_code"));
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存AppMould
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "AppMould"));
		// 保存AppMouldDetail
		for (Map<Object, Object> s : grid) {
			s.put("ad_code", store.get("app_code"));
		}
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid, "AppMouldDetail", "ad_id"));
		baseDao.execute("update AppMouldDetail set ad_code=(select app_code from AppMould where ad_appid=app_id) where ad_appid="
				+ store.get("app_id") + " and not exists (select 1 from AppMould where ad_code=app_code)");
		baseDao.execute("update AppMould set (app_prjcode,app_prjname)=(select ps_prjcode,ps_prjname from productset,AppMouldDetail where ad_appid=app_id and ad_pscode=ps_code and ad_detno=1)"
				+ " where app_id=" + store.get("app_id") + " and nvl(app_prjcode,' ')=' '");
		// 获取物料明细
		getAppMouldDet(Integer.parseInt(store.get("app_id").toString()));
		// 记录操作
		baseDao.logger.save(caller, "app_code", store.get("app_code"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void deleteAppMould(int app_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("AppMould", "app_statuscode", "app_id=" + app_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { app_id });
		// 删除AppMould
		baseDao.deleteById("AppMould", "app_id", app_id);
		// 删除AppMouldDetail
		baseDao.deleteById("AppMoulddetail", "ad_appid", app_id);
		// 删除AppMouldDet
		baseDao.deleteById("AppMoulddet", "amd_appid", app_id);
		// 记录操作
		baseDao.logger.delete(caller, "app_id", app_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { app_id });
	}

	@Override
	public void updateAppMouldById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("AppMould", "app_statuscode", "app_id=" + store.get("app_id"));
		StateAssert.updateOnlyEntering(status);
		for (Map<Object, Object> s : gstore) {
			s.put("ad_code", store.get("app_code"));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改AppMould
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "AppMould", "app_id"));
		// 修改AppMouldDetail
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "AppMouldDetail", "ad_id"));
		baseDao.execute("update AppMouldDetail set ad_code=(select app_code from AppMould where ad_appid=app_id) where ad_appid="
				+ store.get("app_id") + " and not exists (select 1 from AppMould where ad_code=app_code)");
		baseDao.execute("update AppMould set (app_prjcode,app_prjname)=(select ps_prjcode,ps_prjname from productset,AppMouldDetail where ad_appid=app_id and ad_pscode=ps_code and nvl(ad_pscode,' ')<>' ' and rownum=1)"
				+ " where app_id=" + store.get("app_id") + " and nvl(app_prjcode,' ')=' '");
		// 获取物料明细
		getAppMouldDet(Integer.parseInt(store.get("app_id").toString()));
		// 记录操作
		baseDao.logger.update(caller, "app_id", store.get("app_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	private void getAppMouldDet(int app_id) {
		baseDao.deleteById("AppMoulddet", "amd_appid", app_id);
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select * from ProductSetDetail left join ProductSet on ps_id = psd_psid inner join AppMouldDetail on ad_pscode = ps_code where ad_appid =? order by ad_detno",
						app_id);
		List<Map<Object, Object>> maps = new ArrayList<Map<Object, Object>>();
		while (rs.next()) {
			Map<Object, Object> map = new HashMap<Object, Object>();
			int amd_id = baseDao.getSeqId("APPMOULDDET_SEQ");
			map.put("amd_id", amd_id);
			map.put("amd_appid", app_id);
			map.put("amd_adid", rs.getGeneralInt("ad_id"));
			map.put("amd_detno", rs.getCurrentIndex() + 1);
			map.put("amd_prodcode", rs.getString("psd_prodcode"));
			map.put("amd_remark", rs.getString("psd_remark"));
			map.put("amd_pscode", rs.getString("ad_pscode"));
			map.put("amd_psname", rs.getString("ps_name"));
			maps.add(map);
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(maps, "AppMoulddet"));
	}

	@Override
	public void printAppMould(int app_id, String caller) {
		// 只能打印审核后的单据!
		Object status = baseDao.getFieldDataByCondition("AppMould", "app_statuscode", "app_id=" + app_id);
		if (!status.equals("AUDITED") && !status.equals("PARTRECEIVED") && !status.equals("RECEIVED") && !status.equals("NULLIFIED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.print_onlyAudit"));
		}
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, new Object[] { app_id });
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "app_id", app_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, new Object[] { app_id });
	}

	@Override
	public void auditAppMould(int app_id, String caller) {
		baseDao.execute("update AppMouldDetail set ad_code=(select app_code from AppMould where ad_appid=app_id) where ad_appid=" + app_id
				+ " and not exists (select 1 from AppMould where ad_code=app_code)");
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("AppMould", "app_statuscode", "app_id=" + app_id);
		StateAssert.auditOnlyCommited(status);
		String dets = baseDao.queryForObject(
				"select wm_concat(distinct ad_pscode) from AppMouldDetail where ad_appid=? group by ad_pscode having count(*)>1",
				String.class, app_id);
		if (dets != null) {
			BaseUtil.showError("明细模具编号重复！" + dets);
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { app_id });
		// 执行审核操作
		baseDao.audit("AppMould", "app_id=" + app_id, "app_status", "app_statuscode", "app_auditdate", "app_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "app_id", app_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { app_id });
	}

	@Override
	public void resAuditAppMould(int app_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("AppMould", new String[] { "app_statuscode", "app_code" }, "app_id=" + app_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(msa_code) from MOD_SALE where msa_sourcecode=? and msa_sourcetype='开模申请单'", String.class, status[1]);
		if (dets != null) {
			BaseUtil.showError("已转模具销售单[" + dets + "]不允许反审核!");
		}
		dets = baseDao.getJdbcTemplate().queryForObject("select wm_concat(pd_code) from PriceMould where pd_appmouldcode=?", String.class,
				status[1]);
		if (dets != null) {
			BaseUtil.showError("已转模具报价单[" + dets + "]不允许反审核!");
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(ab_code) from arbill where AB_SOURCETYPE='开模申请单' and ab_sourcecode=?", String.class, status[1]);
		if (dets != null) {
			BaseUtil.showError("已转其它应收单[" + dets + "]不允许反审核!");
		}
		// 执行反审核操作
		baseDao.resOperate("AppMould", "app_id=" + app_id, "app_status", "app_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "app_id", app_id);
	}

	@Override
	public void submitAppMould(int app_id, String caller) {
		baseDao.execute("update AppMouldDetail set ad_code=(select app_code from AppMould where ad_appid=app_id) where ad_appid=" + app_id
				+ " and not exists (select 1 from AppMould where ad_code=app_code)");
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("AppMould", "app_statuscode", "app_id=" + app_id);
		StateAssert.submitOnlyEntering(status);
		String dets = baseDao.queryForObject(
				"select wm_concat(distinct ad_pscode) from AppMouldDetail where ad_appid=? group by ad_pscode having count(*)>1",
				String.class, app_id);
		if (dets != null) {
			BaseUtil.showError("明细模具编号重复！" + dets);
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { app_id });
		// 执行提交操作
		baseDao.submit("AppMould", "app_id=" + app_id, "app_status", "app_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "app_id", app_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { app_id });
	}

	@Override
	public void resSubmitAppMould(int app_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("AppMould", "app_statuscode", "app_id=" + app_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { app_id });
		// 执行反提交操作
		baseDao.resOperate("AppMould", "app_id=" + app_id, "app_status", "app_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "app_id", app_id);
		handlerService.afterResSubmit(caller, new Object[] { app_id });
	}

	@Override
	public String turnPriceMould(String data, String caller) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Object app_id = maps.get(0).get("ad_appid");
		Object[] objs = null;
		StringBuffer sb = new StringBuffer();
		for (Map<Object, Object> map : maps) {
			int ad_id = Integer.parseInt(map.get("ad_id").toString());
			objs = baseDao.getFieldsDataByCondition("AppMouldDetail", new String[] { "ad_statuscode", "ad_closestatuscode", "ad_detno" },
					"ad_id=" + ad_id);
			if (objs != null) {
				if ("FINISH".equals(objs[1])) {
					sb.append("存在已结案的明细行，不允许转单操作！行号：" + objs[2] + "<hr/>");
				}
				if ("TURNPM".equals(objs[0])) {
					sb.append("存在已转报价的明细行，不允许转单操作！行号：" + objs[2] + "<hr/>");
				}
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
		if (maps.size() > 0) {
			JSONObject j = null;
			String pd_code = null;
			int detno = 1;
			int pd_id = 0;
			for (Map<Object, Object> map : maps) {
				if (pd_code == null) {
					j = appMouldDao.turnPriceMould(app_id);
					if (j != null) {
						pd_code = j.getString("pd_code");
						pd_id = j.getInt("pd_id");
						sb.append("转入成功,模具报价单号:" + "<a href=\"javascript:openUrl('jsps/pm/mould/priceMould.jsp?formCondition=pd_idIS"
								+ pd_id + "&gridCondition=pdd_pdidIS" + pd_id + "')\">" + pd_code + "</a>&nbsp;<hr>");
					}
				}
				if (pd_code != null) {
					int ad_id = Integer.parseInt(map.get("ad_id").toString());
					boolean bool = baseDao.checkByCondition("AppMouldDet", "amd_appid = " + app_id);
					if (bool) {
						getAppMouldDet(Integer.parseInt(app_id.toString()));
					}
					appMouldDao.toAppointedPriceMould(pd_id, pd_code, ad_id, detno++);
				}
			}
			// 修改报价单状态
			for (Map<Object, Object> map : maps) {
				int ad_id = Integer.parseInt(map.get("ad_id").toString());
				appMouldDao.checkAdQty(ad_id);
			}
		}
		return sb.toString();
	}

	@Override
	public String turnMouldSale(int app_id) {
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(ad_detno) from AppMouldDetail where ad_appid=? and nvl(ad_closestatuscode,' ')='FINISH'", String.class,
				app_id);
		if (dets != null) {
			BaseUtil.showError("存在已结案的明细行，不允许转单操作!行号" + dets);
		}
		JSONObject j = null;
		StringBuffer sb = new StringBuffer();
		int msa_id = 0;
		// 判断该开模申请单是否已经转入过模具销售单
		Object code = baseDao.getFieldDataByCondition("AppMould", "app_code", "app_id=" + app_id);
		code = baseDao.getFieldDataByCondition("MOD_SALE", "msa_code", "msa_sourcecode='" + code + "' and msa_sourcetype='开模申请单'");
		if (code != null && !code.equals("")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("pm.mould.appmould.haveturnmodsale")
					+ "<a href=\"javascript:openUrl('jsps/pm/mould/mouldSale.jsp?formCondition=msa_codeIS" + code
					+ "&gridCondition=msd_codeIS" + code + "')\">" + code + "</a>&nbsp;");
		} else {
			j = appMouldDao.turnMouldSale(app_id);
			if (j != null) {
				msa_id = j.getInt("msa_id");
				sb.append("转入成功,模具销售单号:" + "<a href=\"javascript:openUrl('jsps/pm/mould/mouldSale.jsp?formCondition=msa_idIS" + msa_id
						+ "&gridCondition=msd_msaidIS" + msa_id + "')\">" + j.getString("msa_code") + "</a>&nbsp;");
				// 修改申请单状态
				baseDao.updateByCondition("AppMould",
						"APP_TURNSALECODE='TURNSA',APP_TURNSALE='" + BaseUtil.getLocalMessage("TURNSA") + "'", "app_id=" + app_id);
				// 记录操作
				baseDao.logger.others("转模具销售单", "msg.turnSuccess", "AppMould", "app_id", app_id);
			}
		}
		return sb.toString();
	}

	@Override
	public void updateOffer(String data) {
		Map<Object, Object> formdata = BaseUtil.parseFormStoreToMap(data);
		Object isoffer = formdata.get("isoffer");
		Object ad_id = formdata.get("ad_id");
		SqlRowList rs = baseDao.queryForRowSet("select ad_detno,ad_isoffer,ad_appid from AppMouldDetail where ad_id=?", ad_id);
		if (rs.next()) {
			// 记录操作
			if (isoffer != null && !"".equals(isoffer.toString()) && isoffer != rs.getObject(2)) {
				String oldoffer = null;
				String newoffer = null;
				if (rs.getInt(2) != 1) {
					oldoffer = "是";
				} else {
					oldoffer = "否";
				}
				if (Integer.parseInt(isoffer.toString()) != 1) {
					newoffer = "是";
				} else {
					newoffer = "否";
				}
				baseDao.execute("update AppMouldDetail set ad_isoffer=? where ad_id=?", isoffer, ad_id);
				baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), "更新是否报价", "行" + rs.getInt(1) + ":" + oldoffer
						+ "=>" + newoffer, "AppMould|ps_id=" + rs.getInt(3)));
			}
		}
	}

	// 开模申请单：客户付款为“是”，付款金额大于0的，审核之后自动产生其它应收单
	public void createARBill(int app_id) {
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select app_code,app_custcode,app_custname from AppMould where app_id=? and nvl(app_payamount,0)>0 and nvl(app_iscust,0)<>0",
						app_id);
		if (rs.next()) {
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(ab_code) from arbill where AB_SOURCETYPE='开模申请单' and ab_sourcecode=?", String.class,
					rs.getString("app_code"));
			if (dets != null) {
				BaseUtil.showError("已转其它应收单[" + dets + "]!");
			}
			String cate = baseDao.getDBSetting("AppMould", "mouldCate");
			if (!StringUtil.hasText(cate)) {
				BaseUtil.showError("转入其它应收单科目未设置！");
			}
			Key key = transferRepository.transfer("AppMould!ToARBill", app_id);
			// 转入明细
			int ab_id = key.getId();
			Object[] ca = baseDao.getFieldsDataByCondition("Category", new String[] { "ca_id", "ca_class", "ca_asstype", "ca_assname" },
					"ca_code='" + cate + "'");
			Object asscode = null;
			if (ca != null) {
				boolean assdetail = false;
				if (ca[2] != null) {
					SqlRowList ass = baseDao.queryForRowSet("select * from asskind where ak_code='" + ca[2] + "'");
					if (ass.next()) {
						int akid = ass.getGeneralInt("ak_id");
						int count = baseDao.getCount("select count(1) from asskindDetail where AKD_ASSNAME='"
								+ rs.getObject("app_custname") + "' and AKD_AKID=" + akid);
						if (count == 0) {
							assdetail = false;
						} else {
							asscode = baseDao.getFieldDataByCondition("asskindDetail", "akd_asscode", "akd_akid=" + akid
									+ " and AKD_ASSNAME='" + rs.getObject("app_custname") + "'");
							assdetail = true;
						}
					}
				}
				int abd_id = baseDao.getSeqId("ARBILLDETAIL_SEQ");
				baseDao.execute("Insert into ARBillDetail(abd_id,abd_abid,abd_code,abd_detno,abd_catecode,abd_cateid,abd_catetype,abd_aramount,"
						+ "abd_qty,abd_thisvoprice,abd_price,abd_remark) select "
						+ abd_id
						+ ","
						+ ab_id
						+ ", '"
						+ key.getCode()
						+ "',1,'"
						+ cate
						+ "',"
						+ ca[0]
						+ ",'"
						+ ca[1]
						+ "',app_payamount,1,app_payamount,app_payamount,app_remark from appmould where app_id=" + app_id);
				if (assdetail) {
					baseDao.execute("insert into arbilldetailass(DASS_ID,DASS_CONDID,DASS_ASSNAME,DASS_CODEFIELD,"
							+ "DASS_NAMEFIELD,DASS_ASSTYPE ) values (ARBILLDETAILASS_SEQ.NEXTVAL, " + abd_id + ",'" + ca[3] + "', '"
							+ asscode + "','" + rs.getString("app_custname") + "', '" + ca[2] + "')");
				}
				baseDao.execute("update arbill set ab_rate=(select cm_crrate from currencysmonth where ab_currency=cm_crname and ab_yearmonth=cm_yearmonth) where ab_id="
						+ ab_id);
				baseDao.execute("update arbilldetail set abd_noaramount=ROUND(abd_thisvoprice*abd_qty/(1+abd_taxrate/100),2) WHERE abd_abid="
						+ ab_id);
				baseDao.execute("update arbilldetail set abd_taxamount=NVL(abd_aramount,0)-NVL(abd_noaramount,0) WHERE abd_abid=" + ab_id);
				// 更新ARBill主表的金额
				baseDao.execute("update arbill set ab_taxamount=(select sum(round(((abd_thisvoprice*abd_qty*abd_taxrate/100)/(1+abd_taxrate/100)),2)) from arbilldetail where abd_abid=ab_id)+ab_differ where ab_id="
						+ ab_id);
				arBillService.postARBill(key.getId(), "ARBill!OTRS");
			} else {
				BaseUtil.showError("科目[" + cate + "]不存在！");
			}
		}
	}
}
