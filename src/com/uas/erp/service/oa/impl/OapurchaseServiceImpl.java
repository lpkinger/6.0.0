package com.uas.erp.service.oa.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.dao.common.OaPurchaseDao;
import com.uas.erp.model.Key;
import com.uas.erp.service.oa.OapurchaseService;

@Service
public class OapurchaseServiceImpl implements OapurchaseService {

	static final String update = "update Oapurchase set op_isturn='1' where op_id=?";

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private OaPurchaseDao oapurchaseDao;

	@Autowired
	private HandlerService handlerService;

	@Autowired
	private TransferRepository transferRepository;

	@Override
	public void saveOapurchase(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Oapurchase", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		Object[] od_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			od_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				od_id[i] = baseDao.getSeqId("OapurchaseDETAIL_SEQ");
			}
		} else {
			od_id[0] = baseDao.getSeqId("OapurchaseDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "OapurchaseDetail", "od_id", od_id);
		baseDao.execute(gridSql);
		/*
		 * @Author: wuyx
		 * 反馈编号：2018060729
		 * 用品采购自动取价
		 * */
		Object op_id = store.get("op_id");
		boolean bool = String.valueOf(store.get("op_getprice")).equals("-1");// 是否自动获取单价
		StringBuffer error = new StringBuffer();
		if(bool){
			SqlRowList rs = baseDao.queryForRowSet("select * from oapurchasedetail,oapurchase where od_oaid=op_id and od_oaid="
					+ op_id);
			JSONObject obj = null;
			while (rs.next()) {
				double price = 0;
				// 到物料核价单取单价
				Object oqty = baseDao.getFieldDataByCondition("oapurchasedetail", "nvl(sum(od_neednumber),0)",
						" od_oaid=" + rs.getInt("od_oaid") + " and od_procode='" + rs.getString("od_procode") + "'");
				Object od_date = baseDao.getFieldDataByCondition("Oapurchase", "to_char(op_date,'yyyy-mm-dd')",
						"op_id=" + op_id);
				obj = oapurchaseDao.getOAPurchasePrice(rs.getString("op_vecode"), rs.getString("od_procode"),
						rs.getString("op_currency"),  Double.parseDouble(oqty.toString()),
						DateUtil.parseDateToOracleString(Constant.YMD, (String) od_date));
				if (obj != null) {
					price = NumberUtil.formatDouble(obj.getDouble("od_price"), 6);
					baseDao.execute("update OapurchaseDetail set "
							+ "od_price="+ price
							+ ",od_rate=" + obj.getDouble("od_rate")
							+ ",od_ppdid="+ obj.getDouble("od_ppdid") 
							+ " where od_id=" + rs.getInt("od_id"));
				} else {
					error.append("序号：[" + rs.getString("od_detno") + "],根据 物料编号:[" + rs.getGeneralString("od_prodcode") + "],供应商号:["
							+ rs.getGeneralString("op_vecode") + "],币别:[" + rs.getGeneralString("op_currency") + "] 在物料核价单未找到对应单价，或单价为空值、0等!<BR/>");
					baseDao.execute("update OapurchaseDetail set od_price=0,od_ppdid=0 where od_id=" + rs.getInt("od_id"));
				}
			}
		}
		baseDao.execute("update oapurchasedetail set od_rate=(select cr_taxrate from currencys left join Oapurchase on op_currency=cr_name and cr_statuscode='CANUSE' where od_oaid=op_id)"
				+ " where od_oaid=" + op_id);
		// 更新金额
		baseDao.execute("update OapurchaseDetail set od_total=round(od_price*od_neednumber,2) where od_oaid=" + op_id);
		baseDao.execute("update Oapurchase set op_total=(select round(sum(od_total),2) from OapurchaseDetail where od_oaid=op_id) where op_id="
				+ store.get("op_id"));
		baseDao.execute("update Oapurchase set op_totalupper=L2U(nvl(op_total,0)) WHERE op_id=" + op_id);
		baseDao.execute("update OapurchaseDetail set od_code=(select op_code from Oapurchase where od_oaid=op_id) where od_oaid="
				+ op_id + " and not exists (select 1 from Oapurchase where od_code=op_code)");
		// 记录操作
		baseDao.logger.save(caller, "op_id", store.get("op_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
		if (error.length() > 0) {
			BaseUtil.showErrorOnSuccess(error.toString());
		}
	}

	@Override
	public void updateOapurchaseById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		Object op_currency = baseDao.getFieldDataByCondition("Vendor", "ve_currency", "ve_code='" + store.get("op_vecode") + "'");
		store.put("op_currency", op_currency);
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Oapurchase", "op_id");
		baseDao.execute(formSql);
		// 修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "OapurchaseDetail", "od_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("od_id") == null || s.get("od_id").equals("") || s.get("od_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("OapurchaseDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "OapurchaseDetail", new String[] { "od_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		/*
		 * @Author: wuyx
		 * 反馈编号：2018060729
		 * 用品采购自动取价
		 * */
		Object op_id = store.get("op_id");
		boolean bool = String.valueOf(store.get("op_getprice")).equals("-1");// 是否自动获取单价
		StringBuffer error = new StringBuffer();
		if(bool){
			SqlRowList rs = baseDao.queryForRowSet("select * from oapurchasedetail,oapurchase where od_oaid=op_id and od_oaid="
					+ op_id);
			JSONObject obj = null;
			while (rs.next()) {
				double price = 0;
				// 到物料核价单取单价
				Object oqty = baseDao.getFieldDataByCondition("oapurchasedetail", "nvl(sum(od_neednumber),0)",
						" od_oaid=" + rs.getInt("od_oaid") + " and od_procode='" + rs.getString("od_procode") + "'");
				Object op_date = baseDao.getFieldDataByCondition("Oapurchase", "to_char(op_date,'yyyy-mm-dd')",
						"op_id=" + op_id);
				obj = oapurchaseDao.getOAPurchasePrice(rs.getString("op_vecode"), rs.getString("od_procode"),
						rs.getString("op_currency"),  Double.parseDouble(oqty.toString()),
						DateUtil.parseDateToOracleString(Constant.YMD, (String) op_date));
				if (obj != null) {
					price = NumberUtil.formatDouble(obj.getDouble("od_price"), 6);
					baseDao.execute("update OapurchaseDetail set "
							+ "od_price="+ price
							+ ",od_rate=" + obj.getDouble("od_rate")
							+ ",od_ppdid="+ obj.getDouble("od_ppdid") 
							+ " where od_id=" + rs.getInt("od_id"));
				} else {
					error.append("序号：[" + rs.getString("od_detno") + "],根据 物料编号:[" + rs.getGeneralString("od_prodcode") + "],供应商号:["
							+ rs.getGeneralString("op_vecode") + "],币别:[" + rs.getGeneralString("op_currency") + "] 在物料核价单未找到对应单价，或单价为空值、0等!<BR/>");
					baseDao.execute("update OapurchaseDetail set od_price=0,od_ppdid=0 where od_id=" + rs.getInt("od_id"));
				}
			}
		}
		// 更新金额
		baseDao.execute("update OapurchaseDetail set od_code=(select op_code from Oapurchase where od_oaid=op_id) where od_oaid=" + op_id
				+ " and not exists (select 1 from Oapurchase where od_code=op_code)");
		baseDao.execute("update OapurchaseDetail set od_total=round(od_price*od_neednumber,2) where od_oaid=" + op_id);
		baseDao.execute("update Oapurchase set op_total=(select round(sum(od_total),2) from OapurchaseDetail where od_oaid=op_id) where op_id="
				+ op_id);
		baseDao.execute("update Oapurchase set op_totalupper=L2U(nvl(op_total,0)) WHERE op_id=" + op_id);
		// 记录操作
		baseDao.logger.update(caller, "op_id", op_id);
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
		if (error.length() > 0) {
			BaseUtil.showErrorOnSuccess(error.toString());
		}
	}

	@Override
	public void deleteOapurchase(int op_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { op_id });
		baseDao.delCheck("Oapurchase", op_id);
		// 把之前申请单的已转状态还原
		List<Object[]> data = baseDao.getFieldsDatasByCondition("Oapurchasedetail", new String[] { "od_oadetno", "od_oacode",
				"od_neednumber" }, "od_oaid=" + op_id);
		for (Object[] o : data) {
			Object id = baseDao.getFieldDataByCondition("Oaapplication", "oa_id", "oa_code='" + o[1] + "'");
			baseDao.updateByCondition("oaapplicationdetail", "od_yqty=nvl(od_yqty,0)-" + o[2], "od_oaid=" + id + " and od_detno=" + o[0]);
		}
		// 删除purchase
		baseDao.deleteById("Oapurchase", "op_id", op_id);
		// 删除purchaseDetail
		baseDao.deleteById("Oapurchasedetail", "od_oaid", op_id);
		// 记录操作
		baseDao.logger.delete(caller, "op_id", op_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { op_id });

	}

	@Override
	public void auditOapurchase(int op_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Oapurchase", "op_statuscode", "op_id=" + op_id);
		StateAssert.auditOnlyCommited(status);
		baseDao.execute("update OapurchaseDetail set od_code=(select op_code from Oapurchase where od_oaid=op_id) where od_oaid=" + op_id
				+ " and not exists (select 1 from Oapurchase where od_code=op_code)");
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { op_id });
		// 执行审核操作
		baseDao.audit("Oapurchase", "op_id=" + op_id, "op_status", "op_statuscode", "op_auditdate", "op_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "op_id", op_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { op_id });
	}

	@Override
	public void resAuditOapurchase(int op_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Oapurchase", "op_statuscode", "op_id=" + op_id);
		StateAssert.resAuditOnlyAudit(status);
		baseDao.resAuditCheck("Oapurchase", op_id);
		// 明细行有已验收数量，限制不能反审核
		List<Object[]> data = baseDao.getFieldsDatasByCondition("oapurchasedetail",
				new String[] { "nvl(od_ysqty,0)", "od_detno", "od_id" }, "od_oaid=" + op_id);
		for (Object[] os : data) {
			if (0 != Double.parseDouble(os[0].toString())) {
				BaseUtil.showError("第" + os[1] + "行,已验收数不为0,不允许反审核!");
			}
			if (baseDao.getCount("select count(*) from OapurchaseChangeDet where ocd_odid=" + os[2]) != 0) {
				BaseUtil.showError("第" + os[1] + "行,出现在采购变更单中,不允许反审核!");
			}
		}
		// 执行反审核操作
		baseDao.resAudit("Oapurchase", "op_id=" + op_id, "op_status", "op_statuscode", "op_auditdate", "op_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "op_id", op_id);

	}

	@Override
	public void submitOapurchase(int op_id, String caller) {
		baseDao.execute("update OapurchaseDetail set od_code=(select op_code from Oapurchase where od_oaid=op_id) where od_oaid=" + op_id
				+ " and not exists (select 1 from Oapurchase where od_code=op_code)");
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Oapurchase", "op_statuscode", "op_id=" + op_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { op_id });
		// 更新金额
		baseDao.execute("update OapurchaseDetail set od_total=round(od_price*od_neednumber,2) where od_oaid=" + op_id);
		baseDao.execute("update Oapurchase set op_total=(select round(sum(od_total),2) from OapurchaseDetail where od_oaid=op_id) where op_id="
				+ op_id);
		baseDao.execute("update Oapurchase set op_totalupper=L2U(nvl(op_total,0)) WHERE op_id=" + op_id);
		// 明细行税率是否需要强制带供应商税率
		if (baseDao.isDBSetting(caller, "isDefaultTax")) {
			baseDao.execute("update oapurchasedetail set od_rate=(select ve_taxrate from vendor left join Oapurchase on ve_code=op_vecode where op_id="
					+ op_id + ") where od_oaid=" + op_id);
		}
		allowZeroTax(caller, op_id);
		// 执行提交操作
		baseDao.submit("Oapurchase", "op_id=" + op_id, "op_status", "op_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "op_id", op_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { op_id });

	}

	@Override
	public void resSubmitOapurchase(int op_id, String caller) {
		handlerService.beforeResSubmit(caller, new Object[] { op_id });
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Oapurchase", "op_statuscode", "op_id=" + op_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.resOperate("Oapurchase", "op_id=" + op_id, "op_status", "op_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "op_id", op_id);
		handlerService.afterResSubmit(caller, new Object[] { op_id });
	}

	@Override
	public void turnOaacceptance(String formdata, String griddata, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formdata);
		String id = store.get("op_id").toString();
		String code = store.get("op_code").toString();
		baseDao.execute("update OapurchaseDetail set od_code=(select op_code from Oapurchase where od_oaid=op_id) where od_oaid=" + id
				+ " and not exists (select 1 from Oapurchase where od_code=op_code)");
		Object status = baseDao.getFieldDataByCondition("Oapurchase", "op_statuscode", "op_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		List<String> sqls = getTurnOaacceptanceSql(store, griddata, caller, code);
		baseDao.execute(sqls);
		baseDao.execute(update, new Object[] { id });
	}

	@Override
	public List<String> getTurnOaacceptanceSql(Map<Object, Object> store, String griddata, String caller, String code) {
		List<String> sqls = new ArrayList<String>();
		int formid = baseDao.getSeqId("Oaacceptance_SEQ");
		String oaCode = baseDao.sGetMaxNumber("Oaacceptance", 2);
		String formSql = "insert into Oaacceptance(op_code,op_status,op_statuscode,op_oacode,op_recordorid,op_recordor"
				+ ",op_date,op_id,op_isturn)values('" + oaCode + "','" + BaseUtil.getLocalMessage("ENTERING") + "','ENTERING','"
				+ store.get("op_code") + "','" + SystemSession.getUser().getEm_id() + "'," + "'" + SystemSession.getUser().getEm_name()
				+ "',to_date('" + store.get("op_date") + "','YYYY-MM-DD'),'" + formid + "','0')";
		sqls.add(formSql);
		JSONArray grid = JSONArray.fromObject(griddata);
		JSONObject gridjson = new JSONObject();
		String gridSql = null;
		int i, j = 0;
		for (i = 0; i < grid.size(); i++) {
			gridjson = grid.getJSONObject(i);
			j = i + 1;
			gridSql = "insert into Oaacceptancedetail(od_id,od_detno,od_opid,od_procode,"
					+ "od_proname,od_prounit,od_neednumber,od_opcode,od_opdetno)values('" + baseDao.getSeqId("Oaacceptancedetail_SEQ")
					+ "','" + j + "','" + formid + "','" + gridjson.getString("od_procode") + "','" + gridjson.getString("od_proname")
					+ "','" + gridjson.getString("od_prounit") + "','" + gridjson.getInt("od_neednumber") + "','" + code + "','"
					+ gridjson.getString("od_detno") + "')";
			sqls.add(gridSql);
		}

		return sqls;
	}

	@Override
	public String beatchturnOaacceptance(String caller, String data) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		if (store.size() > 0) {
			// 判断采购单状态、本次数量限制
			oapurchaseDao.checkPdYqty(store);
			Object whcode = store.get(0).containsKey("wh_code") ? store.get(0).get("wh_code") : null;
			String adidstr = "";
			StringBuffer sb = new StringBuffer();
			int index = 0;
			String log = null;
			for (Map<Object, Object> map : store) {
				adidstr += "," + map.get("od_id").toString();
			}
			if (!adidstr.equals("")) {
				adidstr = adidstr.substring(1);
				String dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat('采购单号：'||op_code||'，行：'||od_detno) from (select op_code,od_detno from oapurchasedetail LEFT JOIN Oapurchase ON od_oaid=op_id where od_id in ("
										+ adidstr
										+ ") and exists (select ocd_opcode,ocd_oddetno from OapurchaseChangeDet,OapurchaseChange where ocd_ocid=oc_id and oc_statuscode<>'AUDITED' and ocd_opcode=op_code and ocd_oddetno=od_detno))",
								String.class);
				if (dets != null) {
					BaseUtil.showError("选中的明细行存在未审核的采购变更单，不允许转入操作！" + dets);
				}
			}
			Map<Object, List<Map<Object, Object>>> groups = null;
			groups = BaseUtil.groupMap(store, "op_vecode");
			Set<Object> mapSet = groups.keySet();
			List<Map<Object, Object>> items;
			for (Object s : mapSet) {
				items = groups.get(s);
				Integer pu_id = baseDao.getFieldValue("OAPurchaseDetail", "od_oaid", "od_id=" + items.get(0).get("od_id"), Integer.class);
				Key key = transferRepository.transfer(caller, pu_id);
				if (key != null) {
					int piid = key.getId();
					index++;
					// 转入明细
					transferRepository.transfer(caller, items, key);
					System.out.println(piid);
					baseDao.execute("update prodiodetail set pd_ordertotal=round(pd_orderprice*pd_inqty,2) where pd_piid=?", piid);
					baseDao.execute(
							"update prodiodetail set pd_price=round(pd_orderprice/(1+nvl(pd_taxrate,0)/100),8),pd_total=pd_ordertotal where pd_piid=?",
							piid);
					if (StringUtil.hasText(whcode)) {
						baseDao.execute("update prodinout set pi_whcode='" + whcode + "' where pi_id=" + piid);
						baseDao.execute("update prodinout set pi_whname=(select wh_description from warehouse where pi_whcode=wh_code) where pi_id="
								+ piid);
						baseDao.execute("update prodiodetail set (pd_whcode,pd_whname)=(select pi_whcode,pi_whname from prodinout where pd_piid=pi_id) where pd_piid="
								+ piid);
					}
					log = "转入成功,验收单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + piid
							+ "&gridCondition=pd_piidIS" + piid + "&whoami=ProdInOut!GoodsIn')\">" + key.getCode() + "</a>";
					sb.append(index).append(": ").append(log).append("<hr>");
				}
			}
			// 修改采购单入库状态
			for (Map<Object, Object> map : store) {
				Object idObject = map.get("od_id").toString();
				String sql = "select round(sum(od_neednumber),2),round(sum(od_yqty),2) from OAPurchaseDetail where od_oaid=" + idObject;
				SqlRowList rs1 = baseDao.queryForRowSet(sql);
				if (rs1.next()) {
					double od_neednumber = rs1.getGeneralDouble(1);
					double od_yqty = rs1.getGeneralDouble(2);
					if (od_neednumber == od_yqty) {
						baseDao.updateByCondition("OAPurchase", "op_isturn='已转收料'", "op_id=" + idObject);
					}
				}
			}
			return sb.toString();
		}
		return null;
	}

	@Override
	public String[] printoaPurchase(int op_id, String caller, String reportName, String condition) {
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 修改打印状态
		baseDao.print("Oapurchase", "op_id=" + op_id, "op_printstatus", "op_printstatuscode");
		return keys;
	}

	private void allowZeroTax(String caller, Object op_id) {
		if (!baseDao.isDBSetting("Oapurchase", "allowZeroTax")) {
			String currency = baseDao.getDBSetting("defaultCurrency");
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select WM_CONCAT(od_detno) from oapurchasedetail left join Oapurchase on od_oaid=op_id where nvl(od_rate,0)=0 and op_currency='"
							+ currency + "' and od_oaid=?", String.class, op_id);
			if (dets != null) {
				BaseUtil.showError("本位币税率为0，不允许提交!行号：" + dets);
			}
		}
	}

	@Override
	public void endOapurchase(int op_id, String caller) {
		// 只能对状态为[已审核]的单据进行结案操作!
		Object status = baseDao.getFieldDataByCondition("Oapurchase", "op_statuscode", "op_id=" + op_id);
		StateAssert.end_onlyAudited(status);
		// 执行结案操作
		baseDao.updateByCondition("Oapurchase", "op_statuscode='FINISH',op_status='已结案'", "op_id=" + op_id);
		// 记录操作
		baseDao.logger.end(caller, "op_id", op_id);

	}

	@Override
	public void resEndOapurchase(int op_id, String caller) {
		// 只能对状态为[已结案]的单据进行反结案操作!
		Object status = baseDao.getFieldDataByCondition("Oapurchase", "op_statuscode", "op_id=" + op_id);
		StateAssert.resEnd_onlyAudited(status);
		// 执行反结案操作
		baseDao.updateByCondition("Oapurchase", "op_statuscode='AUDITED',op_status='已审核'", "op_id=" + op_id);
		// 记录操作
		baseDao.logger.resEnd(caller, "op_id", op_id);
	}
}
