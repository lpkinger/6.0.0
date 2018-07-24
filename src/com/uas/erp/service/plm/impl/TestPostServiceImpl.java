package com.uas.erp.service.plm.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.PurchaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.plm.TestPostService;

@Service
public class TestPostServiceImpl implements TestPostService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private PurchaseDao purchaseDao;

	final static String INSERT_PRODIODETAIL = "INSERT INTO prodiodetail(pd_id,pd_inoutno,pd_piclass,pd_pdno,pd_ordercode,pd_orderdetno,pd_prodcode,pd_inqty,pd_orderprice"
			+ ",pd_taxrate,pd_taxtotal,pd_status,pd_orderid,pd_auditstatus,pd_accountstatuscode,pd_accountstatus,pd_whcode,pd_whname"
			+ ",pd_batchcode,pd_piid) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static String INSERT_PRODIODETAIL_OUT = "INSERT INTO prodiodetail(pd_id,pd_inoutno,pd_piclass,pd_pdno,pd_ordercode,pd_orderdetno,pd_prodcode,pd_outqty,pd_orderprice"
			+ ",pd_taxrate,pd_taxtotal,pd_status,pd_custprodcode,pd_orderid,pd_auditstatus,pd_accountstatuscode,pd_accountstatus,pd_whcode,pd_whname"
			+ ",pd_batchcode) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static String INSERT_PRODIO_VEND = "INSERT INTO ProdInOut(pi_id, pi_inoutno, pi_date, pi_class, pi_invostatus, pi_invostatuscode,pi_recordman, pi_recorddate, pi_cardcode,pi_title"
			+ ", pi_cardid, pi_status, pi_statuscode,pi_updatedate,pi_updateman,pi_printstatus,pi_printstatuscode,pi_currency,pi_payment,pi_rate) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	public String initPurchase(int count) {
		String str = String.valueOf(System.currentTimeMillis());
		long id = Long.parseLong(str.substring(str.length() - 8));
		SqlRowList rs = baseDao
				.queryForRowSet("SELECT ve_code,ve_name,ve_apvendcode,ve_apvendname,ve_buyerid,ve_buyername,ve_payment,ve_currency"
						+ " FROM (select ve_code,ve_name,nvl(ve_apvendcode,ve_code) ve_apvendcode,nvl(ve_apvendname,ve_name) ve_apvendname,ve_buyerid,"
						+ "ve_buyername,ve_payment,ve_currency,rownum r FROM Vendor WHERE ve_auditstatuscode='AUDITED' and ve_buyername is not null) tab WHERE tab.r<2");
		Map<Object, Object> pu = new HashMap<Object, Object>();
		String code = "TEST" + String.valueOf(id);
		String date = DateUtil.currentDateString(Constant.YMD_HMS);
		String vCode = null;
		String currency = null;
		Employee employee = SystemSession.getUser();
		if (rs.next()) {
			pu.put("pu_id", id);
			pu.put("pu_code", code);
			pu.put("pu_kind", "正常采购");
			vCode = rs.getString("ve_code");
			pu.put("pu_vendcode", vCode);
			pu.put("pu_vendname", rs.getString("ve_name"));
			pu.put("pu_receivecode", rs.getString("ve_apvendcode"));
			pu.put("pu_receivename", rs.getString("ve_apvendname"));
			pu.put("pu_buyerid", rs.getInt("ve_buyerid"));
			pu.put("pu_buyername", rs.getString("ve_buyername"));
			pu.put("pu_payments", rs.getString("ve_payment"));
			currency = rs.getString("ve_currency");
			pu.put("pu_currency", currency);
			pu.put("pu_statuscode", "AUDITED");
			pu.put("pu_status", BaseUtil.getLocalMessage("AUDITED"));
			pu.put("pu_date", date);
			pu.put("pu_delivery", date);
			pu.put("pu_indate", date);
			pu.put("pu_auditdate", date);
			pu.put("pu_auditman", employee.getEm_name());
			pu.put("pu_recordid", employee.getEm_id());
			pu.put("pu_recordman", employee.getEm_name());
			pu.put("pu_remark", "压力测试");
		}
		List<String> sqls = new ArrayList<String>();
		sqls.add(SqlUtil.getInsertSqlByMap(pu, "Purchase"));
		rs = baseDao.queryForRowSet("SELECT pr_code FROM (select pr_code,rownum r FROM Product WHERE pr_statuscode='AUDITED')"
				+ " tab WHERE tab.r<51");
		Map<Object, Object> pd = null;
		int index = 0;
		int qty = 5 * count;
		JSONArray arr = new JSONArray();
		JSONObject obj = null;
		List<Map<Object, Object>> list = initProduct(employee);
		for (Map<Object, Object> m : list) {
			pd = new HashMap<Object, Object>();
			pd.put("pd_id", id + index++);
			pd.put("pd_code", code);
			pd.put("pd_puid", id);
			pd.put("pd_detno", index);
			pd.put("pd_prodcode", m.get("pr_code"));
			pd.put("pd_qty", qty);
			pd.put("pd_rate", 17);
			pd.put("pd_price", 888);
			pd.put("pd_total", qty * 888);
			pd.put("pd_netprice", 888 / 1.17);
			pd.put("pd_taxtotal", qty * 888 / 1.17);
			pd.put("pd_delivery", "2013-12-31");
			sqls.add(SqlUtil.getInsertSqlByMap(pd, "PurchaseDetail"));
			obj = new JSONObject();
			obj.put("pd_id", pd.get("pd_id"));
			obj.put("pd_tqty", qty);
			obj.put("pu_vendcode", vCode);
			obj.put("pu_currency", currency);
			obj.put("pd_code", code);
			obj.put("pd_prodcode", m.get("pr_code"));
			obj.put("pd_price", 888);
			obj.put("pd_rate", 17);
			obj.put("pd_detno", index);
			arr.add(obj);
		}
		baseDao.execute(sqls);
		return arr.toString();
	}

	public String[] initProdIOPurc(int count, String data, String piclass, String caller) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		String[] codes = new String[count];
		String code = null;
		String vCode = "";
		String curr = "";
		int detno = 1;
		double qty = 0;
		Object[] whs = baseDao.getFieldsDataByCondition("WareHouse", "wh_code,wh_description", "1=1");
		Timestamp time = Timestamp.valueOf(DateUtil.currentDateString("yyyy-MM-dd HH:mm:ss"));
		String str = String.valueOf(System.currentTimeMillis());
		long id = Long.parseLong(str.substring(str.length() - 8));
		long batch = Long.parseLong(DateUtil.currentDateString(null).substring(2).replaceAll("-", "") + "90001");
		long pi = 0;
		Object[] objs = null;
		Object[] rate = null;
		Employee employee = SystemSession.getUser();
		for (int i = 0; i < count; i++) {
			code = null;
			detno = 1;
			pi = id + i;
			for (Map<Object, Object> m : store) {
				if (code == null) {
					vCode = m.get("pu_vendcode").toString();
					curr = m.get("pu_currency").toString();
					code = String.valueOf(pi);
					if (objs == null) {
						objs = baseDao.getFieldsDataByCondition("Vendor", new String[] { "ve_name", "ve_id", "ve_payment" }, "ve_code='"
								+ vCode + "'");
					}
					if (rate == null) {
						rate = baseDao.getFieldsDataByCondition("Currencys", new String[] { "cr_rate" }, "cr_name='" + curr + "'");
					}
					baseDao.execute(INSERT_PRODIO_VEND, pi, code, time, piclass, BaseUtil.getLocalMessage("ENTERING"), "ENTERING",
							employee.getEm_name(), time, vCode, objs[0], objs[1], BaseUtil.getLocalMessage("UNPOST"), "UNPOST", time,
							employee.getEm_name(), BaseUtil.getLocalMessage("UNPRINT"), "UNPRINT", curr, objs[2], rate[0]);
					codes[i] = code;
				}
				qty = Double.parseDouble(m.get("pd_tqty").toString()) / count;
				double price = Double.parseDouble(m.get("pd_price").toString());
				double r = Double.parseDouble(m.get("pd_rate").toString());
				baseDao.execute(INSERT_PRODIODETAIL, (id + i) * 1000 + detno, code, piclass, detno++, m.get("pd_code"), m.get("pd_detno"),
						m.get("pd_prodcode"), qty, price, r, price * qty, 0, m.get("pd_id"), "ENTERING", "UNACCOUNT",
						BaseUtil.getLocalMessage("UNACCOUNT"), whs[0], whs[1], String.valueOf(batch++), pi);
			}
		}
		return codes;
	}

	public void clearProdIOPurc(String code, String codes) {
		baseDao.deleteByCondition("Product", "pr_code in (select pd_prodcode from PurchaseDetail where pd_code ='" + code + "')");
		baseDao.deleteByCondition("Purchase", "pu_code='" + code + "'");
		baseDao.deleteByCondition("PurchaseDetail", "pd_code='" + code + "'");
		baseDao.deleteByCondition("ProdInOut", "pi_inoutno in (" + codes + ")");
		baseDao.deleteByCondition("ProdIoDetail", "pd_inoutno in (" + codes + ")");
	}

	@Override
	public String postProdIOPurc(String code) {
		// 执行过账操作
		Employee employee = SystemSession.getUser();
		Object obj = baseDao.getFieldDataByCondition("ProdInOut", "pi_class", "pi_inoutno='" + code + "'");
		baseDao.procedure("SP_GetCostPrice", new Object[] { obj, code });
		String res = baseDao.callProcedure("Sp_SplitProdOut", new Object[] { obj, code, String.valueOf(employee.getEm_id()) });
		if (res != null && !res.trim().equals("")) {
			return res;
		}
		// 存储过程
		res = baseDao.callProcedure("Sp_CommitProdInout", new Object[] { obj, code, String.valueOf(employee.getEm_id()) });
		if (res != null && !res.trim().equals("")) {
			return res;
		}
		baseDao.updateByCondition("ProdInOut", "pi_statuscode='POSTED',pi_status='" + BaseUtil.getLocalMessage("POSTED")
				+ "',pi_inoutman='" + employee.getEm_name() + "',pi_date1=" + DateUtil.parseDateToOracleString(null, new Date()),
				"pi_inoutno='" + code + "'");
		baseDao.updateByCondition("ProdIodetail", "pd_status=99,pd_auditstatus='POSTED'", "pd_inoutno='" + code + "'");
		return null;
	}

	private List<Map<Object, Object>> initProduct(Employee employee) {
		String str = String.valueOf(System.currentTimeMillis());
		long id = Long.parseLong(str.substring(str.length() - 8));
		List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
		Map<Object, Object> pr = new HashMap<Object, Object>();
		String code = "TEST" + String.valueOf(id);
		String date = DateUtil.currentDateString(Constant.YMD_HMS);
		List<String> sqls = new ArrayList<String>();
		for (int i = 0; i < 50; i++) {
			pr = new HashMap<Object, Object>();
			pr.put("pr_id", id + i);
			pr.put("pr_code", code + i);
			pr.put("pr_detail", "压力测试" + i);
			pr.put("pr_spec", "压力测试");
			pr.put("pr_kind", "测试类");
			pr.put("pr_statuscode", "AUDITED");
			pr.put("pr_whcode", "C01");
			pr.put("pr_docdate", date);
			pr.put("pr_sqr", employee.getEm_name());
			pr.put("pr_recordman", employee.getEm_name());
			list.add(pr);
			sqls.add(SqlUtil.getInsertSqlByMap(pr, "Product"));
		}
		baseDao.execute(sqls);
		return list;
	}

	@Override
	public String[] initProdIOPurcOut(int count, String data, String piclass, String caller) {
		List<Map<Object, Object>> store = BaseUtil.parseGridStoreToMaps(data);
		String[] codes = new String[count];
		String code = null;
		String vCode = "";
		String curr = "";
		int detno = 1;
		double qty = 0;
		Object[] whs = baseDao.getFieldsDataByCondition("WareHouse", "wh_code,wh_description", "1=1");
		Timestamp time = Timestamp.valueOf(DateUtil.currentDateString("yyyy-MM-dd HH:mm:ss"));
		String currentdate = DateUtil.currentDateString(Constant.YMD);
		String[] arr = currentdate.split("-");
		String currentyearmonth = arr[0] + "" + arr[1];
		for (int i = 0; i < count; i++) {
			code = null;
			detno = 1;
			for (Map<Object, Object> m : store) {
				if (code == null) {
					vCode = m.get("pu_vendcode").toString();
					curr = m.get("pu_currency").toString();
					JSONObject j = purchaseDao.newProdIO(curr, vCode, "采购验退单", "ProdInOut!PurcCheckout", currentyearmonth);
					if (j != null) {
						code = j.getString("pi_inoutno");
						codes[i] = code;
					}
				}
				qty = Double.parseDouble(m.get("pd_tqty").toString()) / count;
				Object[] objs = baseDao.getFieldsDataByCondition("PurchaseDetail", new String[] { "pd_code", "pd_detno", "pd_prodcode",
						"pd_qty", "pd_price", "pd_rate", "pd_custprodcode", "pd_id" }, "pd_id=" + m.get("pd_id"));
				double price = Double.parseDouble(objs[4].toString());
				double rate = Double.parseDouble(objs[5].toString());
				baseDao.execute(INSERT_PRODIODETAIL_OUT,
						new Object[] { baseDao.getSeqId("PRODIODETAIL_SEQ"), code, piclass, detno++, objs[0], objs[1], objs[2], qty, price,
								rate, price * qty, 0, objs[6], objs[7], "ENTERING", "UNACCOUNT", BaseUtil.getLocalMessage("UNACCOUNT"),
								whs[0], whs[1], baseDao.getBatchcode(caller) });
			}
		}
		return codes;
	}
}
