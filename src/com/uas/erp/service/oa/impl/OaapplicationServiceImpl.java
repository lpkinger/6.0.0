package com.uas.erp.service.oa.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.model.Key;
import com.uas.erp.service.oa.OaapplicationService;

@Service
public class OaapplicationServiceImpl implements OaapplicationService {

	static final String update = "update oaapplication set oa_isturn='1' where oa_id=?";

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Autowired
	private TransferRepository transferRepository;

	@Override
	public void saveOaapplication(String formStore, String gridStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Oaapplication", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		List<Map<Object, Object>> gStore = BaseUtil.parseGridStoreToMaps(gridStore);
		for (Map<Object, Object> map : gStore) {
			map.put("od_id", baseDao.getSeqId("OaapplicationDETAIL_SEQ"));
			// 计算金额
			map.put("od_totalprice", Double.parseDouble(map.get("od_number") + "") * Double.parseDouble(map.get("od_price") + ""));

		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gStore, "OaapplicationDetail");
		baseDao.execute(gridSql);
		baseDao.execute("update oaapplicationdetail set od_total=od_number where od_oaid=" + store.get("oa_id"));
		baseDao.execute("update oaapplicationdetail set od_code=(select oa_code from Oaapplication where od_oaid=oa_id) where od_oaid="
				+ store.get("oa_id") + " and not exists (select 1 from Oaapplication where od_code=oa_code)");
		try {
			// 记录操作
			baseDao.logger.save(caller, "oa_id", store.get("oa_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });

	}

	@Override
	public void updateOaapplicationById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Oaapplication", "oa_id");
		baseDao.execute(formSql);
		// 修改purchaseDetail
		List<String> gridSql = new ArrayList<String>();
		for (Map<Object, Object> s : gstore) {
			// 计算金额
			s.put("od_totalprice", Double.parseDouble(s.get("od_number") + "") * Double.parseDouble(s.get("od_price") + ""));
			if (s.get("od_id") == null || s.get("od_id").equals("") || s.get("od_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("OaapplicationDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "OaapplicationDetail", new String[] { "od_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		List<String> gridUpdateSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "OaapplicationDetail", "od_id");
		baseDao.execute(gridSql);
		baseDao.execute(gridUpdateSql);
		baseDao.execute("update oaapplicationdetail set od_code=(select oa_code from Oaapplication where od_oaid=oa_id) where od_oaid="
				+ store.get("oa_id") + " and not exists (select 1 from Oaapplication where od_code=oa_code)");
		baseDao.execute("update oaapplicationdetail set od_total=od_number where od_oaid=" + store.get("oa_id"));
		// 记录操作
		baseDao.logger.update(caller, "oa_id", store.get("oa_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });

	}

	@Override
	public void deleteOaapplication(int oa_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { oa_id });
		// 删除purchase
		baseDao.deleteById("Oaapplication", "oa_id", oa_id);
		// 删除purchaseDetail
		baseDao.deleteById("Oaapplicationdetail", "od_oaid", oa_id);
		// 记录操作
		baseDao.logger.delete(caller, "oa_id", oa_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { oa_id });

	}

	@Override
	public void auditOaapplication(int oa_id, String caller) {
		baseDao.execute("update oaapplicationdetail set od_code=(select oa_code from Oaapplication where od_oaid=oa_id) where od_oaid="
				+ oa_id + " and not exists (select 1 from Oaapplication where od_code=oa_code)");
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Oaapplication", "oa_statuscode", "oa_id=" + oa_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { oa_id });
		// 执行审核操作
		baseDao.audit("Oaapplication", "oa_id=" + oa_id, "oa_status", "oa_statuscode", "oa_auditdate", "oa_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "oa_id", oa_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { oa_id });

	}

	@Override
	public void resAuditOaapplication(int oa_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Oaapplication", "oa_statuscode", "oa_id=" + oa_id);
		StateAssert.resAuditOnlyAudit(status);
		// 明细行是否已转采购状态为已转采购，需要限制反审核
		List<Object[]> data = baseDao.getFieldsDatasByCondition("oaapplicationdetail", new String[] { "nvl(od_yqty,0)", "od_detno" },
				"od_oaid=" + oa_id);
		for (Object[] os : data) {
			if (0 != Double.parseDouble(os[0].toString())) {
				BaseUtil.showError("第" + os[1] + "行,已转采购,不允许反审核!");
			}
		}
		// 执行反审核操作
		baseDao.resAudit("Oaapplication", "oa_id=" + oa_id, "oa_status", "oa_statuscode", "oa_auditdate", "oa_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "oa_id", oa_id);
	}

	@Override
	public void submitOaapplication(int oa_id, String caller) {
		baseDao.execute("update oaapplicationdetail set od_code=(select oa_code from Oaapplication where od_oaid=oa_id) where od_oaid="
				+ oa_id + " and not exists (select 1 from Oaapplication where od_code=oa_code)");
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Oaapplication", "oa_statuscode", "oa_id=" + oa_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { oa_id });
		// 执行提交操作
		baseDao.submit("Oaapplication", "oa_id=" + oa_id, "oa_status", "oa_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "oa_id", oa_id);

		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { oa_id });

	}

	@Override
	public void resSubmitOaapplication(int oa_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		handlerService.beforeResSubmit(caller, new Object[] { oa_id });
		Object status = baseDao.getFieldDataByCondition("Oaapplication", "oa_statuscode", "oa_id=" + oa_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.resOperate("Oaapplication", "oa_id=" + oa_id, "oa_status", "oa_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "oa_id", oa_id);
		handlerService.afterResSubmit(caller, new Object[] { oa_id });
	}

	@Override
	public void turnOaPurchase(String formdata, String griddata, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formdata);
		String id = store.get("oa_id").toString();
		String code = store.get("oa_code").toString();
		baseDao.execute("update oaapplicationdetail set od_code=(select oa_code from Oaapplication where od_oaid=oa_id) where od_oaid="
				+ id + " and not exists (select 1 from Oaapplication where od_code=oa_code)");
		Object status = baseDao.getFieldDataByCondition("Oaapplication", "oa_statuscode", "oa_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		List<String> gridSql = getTurnOaPurchaseSql(store, griddata, code);
		baseDao.execute(gridSql);
		baseDao.execute(update, new Object[] { id });
	}

	public List<String> getTurnOaPurchaseSql(Map<Object, Object> store, String griddata, String code) {

		List<String> sqls = new ArrayList<String>();

		int formid = baseDao.getSeqId("oapurchase_SEQ");
		String purchaseCode = baseDao.sGetMaxNumber("oaPurchase", 2);
		String formSql = "insert into oaPurchase(op_code,op_status,op_statuscode,op_recordorid,op_recordor"
				+ ",op_date,op_id,op_isturn,op_department,op_appman,op_kind)values('" + purchaseCode + "','"
				+ BaseUtil.getLocalMessage("ENTERING") + "','ENTERING','" + SystemSession.getUser().getEm_id() + "'," + "'"
				+ SystemSession.getUser().getEm_name() + "',to_date('" + store.get("oa_date") + "','YYYY-MM-DD'),'" + formid + "','0','"
				+ store.get("oa_department") + "','" + store.get("oa_appmancode") + "','" + store.get("oa_kind") + "')";
		sqls.add(formSql);
		JSONArray grid = JSONArray.fromObject(griddata);
		JSONObject gridjson = new JSONObject();
		String gridSql = null;
		int i, j = 0;
		for (i = 0; i < grid.size(); i++) {
			gridjson = grid.getJSONObject(i);
			j = i + 1;
			gridSql = "insert into oaPurchasedetail(od_id,od_detno,od_oaid,od_procode,"
					+ "od_proname,od_prounit,od_neednumber,od_oacode,od_oadetno)values('" + baseDao.getSeqId("oapurchasedetail_SEQ")
					+ "','" + j + "','" + formid + "','" + gridjson.getString("od_procode") + "','" + gridjson.getString("od_proname")
					+ "','" + gridjson.getString("od_prounit") + "','" + gridjson.getInt("od_number") + "','" + code + "','"
					+ gridjson.getString("od_detno") + "')";
			sqls.add(gridSql);
		}
		return sqls;
	}

	@Override
	public String[] printOaapplication(int bd_id, String caller, String reportName, String condition) {
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 记录操作
		baseDao.logger.print(caller, "oa_id", bd_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, new Object[] { bd_id });
		return keys;
	}

	@Override
	public void turnYPOut(String formdata, String griddata, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formdata);
		String code = store.get("oa_code").toString();
		List<String> sqls = new ArrayList<String>();
		int formid = baseDao.getSeqId("ProdInout_SEQ");
		String inCode = baseDao.sGetMaxNumber("ProdInOut!GoodsPicking", 2);
		String formSql = "insert into ProdInout(pi_id,pi_inoutno,pi_class,pi_status,pi_statuscode,pi_invostatus,pi_invostatuscode"
				+ ",pi_operatorcode,pi_recordman,pi_recorddate,PI_SOURCETYPE) values (" + formid + ",'" + inCode + "','用品领用单','"
				+ BaseUtil.getLocalMessage("UNPOST") + "','UNPOST','" + BaseUtil.getLocalMessage("ENTERING") + "'," + "'ENTERING','"
				+ SystemSession.getUser().getEm_code() + "','" + SystemSession.getUser().getEm_name() + "',sysdate,'用品申请单')";
		sqls.add(formSql);
		JSONArray grid = JSONArray.fromObject(griddata);
		JSONObject gridjson = new JSONObject();
		String gridSql = null;
		int i, j = 0;
		for (i = 0; i < grid.size(); i++) {
			gridjson = grid.getJSONObject(i);
			j = i + 1;
			gridSql = "insert into prodiodetail(pd_id,pd_inoutno,pd_piclass,pd_piid,pd_pdno,pd_ordercode,pd_orderdetno,pd_prodcode"
					+ ",pd_outqty,pd_orderid,pd_auditstatus)values('" + baseDao.getSeqId("prodiodetail_SEQ") + "','" + inCode
					+ "','用品领用单'," + formid + "," + j + ",'" + code + "'," + gridjson.getDouble("od_detno") + ",'"
					+ gridjson.getString("od_procode") + "'," + gridjson.getDouble("od_total") + "," + gridjson.getDouble("od_id")
					+ ",'ENTERING')";
			sqls.add(gridSql);
		}
		baseDao.execute(sqls);
		baseDao.execute("update prodinout set (pi_departmentcode,pi_departmentname,pi_pdpname,pi_emcode,pi_emname,PI_SOURCECODE)=(select oa_departmentcode,oa_department,oa_pdpname,oa_appmancode,oa_appman,oa_code from Oaapplication where oa_code='"
				+ code + "') where pi_id=" + formid);
	}

	/**
	 * 转用品领用
	 */
	@Transactional
	@Override
	public String turnGoodPicking(String data, String caller) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		String log = null;
		Object[] objs = null;
		StringBuffer sb = new StringBuffer();
		for (Map<Object, Object> map : maps) {
			int odid = Integer.parseInt(map.get("od_id").toString());
			double tqty = Double.parseDouble(map.get("od_tqty").toString());
			objs = baseDao.getFieldsDataByCondition("oaapplicationdetail left join Oaapplication on oa_id=od_oaid", new String[] {
					"oa_code", "od_detno", "od_turnlyqty", "od_total" }, "od_id=" + odid + " AND nvl(od_turnlyqty, 0)+" + tqty
					+ ">od_total");
			if (objs != null) {
				sb.append("申请单号:" + objs[0] + ",行号:" + objs[1] + ",数量:" + objs[3] + ",无法转出.已转领用单数量:" + objs[2] + ",本次数量:" + tqty + "<hr/>");
				maps.remove(map);
				continue;
			}
		}
		if (maps.size() > 0) {
			// 转入领用单主记录
			Integer oa_id = baseDao.getFieldValue("oaapplicationdetail", "od_oaid", "od_id=" + maps.get(0).get("od_id"), Integer.class);
			int pi_id = 0;
			Key key = transferRepository.transfer("Oaapplication!ToGoodPicking", oa_id);
			if (key != null) {
				pi_id = key.getId();
				// 转入出货单明细
				transferRepository.transfer("Oaapplication!ToGoodPicking", maps, key);
				log = "用品领用号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + pi_id
						+ "&gridCondition=pd_piidIS" + pi_id + "&whoami=ProdInOut!GoodsPicking')\">" + key.getCode() + "</a>&nbsp;";
				baseDao.execute("update ProdIODetail set pd_total=round(nvl(pd_price,0)*nvl(pd_outqty,0),2) WHERE pd_piid=?", pi_id);
			}
		}
		return "转入成功<hr>" + log;
	}

	@Override
	public void endOaapplication(int oa_id, String caller) {
		// 只能对状态为[已审核]的单据进行结案操作!
		Object status = baseDao.getFieldDataByCondition("Oaapplication", "oa_statuscode", "oa_id=" + oa_id);
		StateAssert.end_onlyAudited(status);
		// 执行结案操作
		baseDao.updateByCondition("Oaapplication", "oa_statuscode='FINISH',oa_status='已结案'", "oa_id=" + oa_id);
		// 记录操作
		baseDao.logger.end(caller, "oa_id", oa_id);
	}

	@Override
	public void resEndOaapplication(int oa_id, String caller) {
		// 只能对状态为[已结案]的单据进行反结案操作!
		Object status = baseDao.getFieldDataByCondition("Oaapplication", "oa_statuscode", "oa_id=" + oa_id);
		StateAssert.resEnd_onlyAudited(status);
		// 执行反结案操作
		baseDao.updateByCondition("Oaapplication", "oa_statuscode='AUDITED',oa_status='已审核'", "oa_id=" + oa_id);
		// 记录操作
		baseDao.logger.resEnd(caller, "oa_id", oa_id);
	}
}
