package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.fa.CashFlowService;

@Service("cashFlowService")
public class CashFlowServiceImpl implements CashFlowService {
	@Autowired
	private BaseDao baseDao;

	/**
	 * 刷新现金流量表
	 * 
	 * @param yearmonth
	 */
	private void refreshCashFlow(String yearmonth) {
		baseDao.execute("DELETE FROM CashFlowSum WHERE cfs_yearmonth='" + yearmonth + "'");
		// 非现金类(借) 0.1
		baseDao.execute("INSERT INTO CashFlowSum(cfs_catecode, cfs_name, cfs_subof, cfs_typename, cfs_debit, cfs_credit, cfs_yearmonth) "
				+ "SELECT t1,t2,t3,t4,t5,t6,t7 FROM (SELECT vd_catecode t1, ca_name t2, ca_pcode t3, '非现金类(借)' t4, 0 t5, sum(vd_creditcashflow) t6, '"
				+ yearmonth
				+ "' t7 from Voucher,VoucherDetail,Category "
				+ "WHERE vo_id = vd_void and vd_catecode = ca_code and vo_yearmonth='"
				+ yearmonth
				+ "' and "
				+ "NVL(vo_iscashflow,0)<>0 AND NVL(ca_cashflow,0)=0 AND NVL(vd_creditcashflow,0)<>0 group by vd_catecode,ca_pcode,ca_name) ");
		// 非现金类(贷)0.2
		baseDao.execute("INSERT INTO CashFlowSum(cfs_catecode, cfs_name, cfs_subof, cfs_typename, cfs_debit, cfs_credit, cfs_yearmonth) "
				+ "SELECT t1,t2,t3,t4,t5,t6,t7 FROM (SELECT vd_catecode t1, ca_name t2, ca_pcode t3, '非现金类(贷)' t4, sum(vd_debitcashflow) t5, 0 t6, '"
				+ yearmonth + "' t7 from Voucher,VoucherDetail,Category "
				+ "WHERE vo_id = vd_void and vd_catecode = ca_code and vo_yearmonth='" + yearmonth + "' AND "
				+ "NVL(vo_iscashflow,0)<>0 AND NVL(ca_cashflow,0)=0 and NVL(vd_debitcashflow,0)<>0 group by vd_catecode,ca_pcode,ca_name)");
		// 现金类(借)0.3
		baseDao.execute("INSERT INTO CashFlowSum(cfs_catecode, cfs_name, cfs_subof, cfs_typename, cfs_debit, cfs_credit, cfs_yearmonth) "
				+ "SELECT t1,t2,t3,t4,t5,t6,t7 FROM (SELECT vd_catecode t1, ca_name t2, ca_pcode t3, '现金类(借)' t4, 0 t5, sum(vd_credit) t6, '"
				+ yearmonth
				+ "' t7 from Voucher,VoucherDetail,Category "
				+ "WHERE vo_id = vd_void and vd_catecode=ca_code and NVL(vo_iscashflow,0)<>0 and vo_yearmonth='"
				+ yearmonth
				+ "' AND vo_code not in (select vo_code from voucher,VoucherDetail,Category where vo_id = vd_void and vd_catecode=ca_code and"
				+ " vo_yearmonth='" + yearmonth + "' and NVL(vo_iscashflow,0)<>0 and NVL(ca_cashflow,0)=0)"
				+ " AND NVL(vd_credit,0)<>0 group by vd_catecode,ca_pcode,ca_name)");
		// 现金类(贷)0.4
		baseDao.execute("INSERT INTO CashFlowSum(cfs_catecode, cfs_name, cfs_subof, cfs_typename, cfs_debit, cfs_credit, cfs_yearmonth) "
				+ "SELECT t1,t2,t3,t4,t5,t6,t7 FROM (SELECT vd_catecode t1, ca_name t2, ca_pcode t3, '现金类(贷)' t4, sum(vd_debit) t5, 0 t6, '"
				+ yearmonth + "' t7 from Voucher,VoucherDetail,Category "
				+ "WHERE vo_id = vd_void and vd_catecode=ca_code and NVL(vo_iscashflow,0)<>0 and vo_yearmonth='" + yearmonth + "' AND "
				+ " vo_code not in (select vo_code from voucher,VoucherDetail,Category where vo_id = vd_void and vd_catecode=ca_code and"
				+ " vo_yearmonth='" + yearmonth + "' and NVL(vo_iscashflow,0)<>0 and NVL(ca_cashflow,0)=0)"
				+ " and NVL(vd_debit,0)<>0 group by vd_catecode,ca_pcode,ca_name)");

		// 插入顶级科目--非现金类(借)0.1
		baseDao.execute("INSERT INTO CashFlowSum(cfs_catecode,cfs_yearmonth,cfs_name,cfs_debit,cfs_credit,cfs_subof,cfs_typename) "
				+ "SELECT DISTINCT ca_code t1,'" + yearmonth
				+ "' t2,ca_name t3,0 t4,0 t5,ca_pcode t6,'非现金类(借)' t7 FROM Category WHERE ca_isleaf=0 AND ca_code NOT IN "
				+ "(SELECT cfs_catecode FROM CashFlowSum WHERE cfs_typename='非现金类(借)' AND cfs_yearmonth='" + yearmonth + "') ");

		// '插入顶级科目--非现金类(贷)0.2
		baseDao.execute("INSERT INTO CashFlowSum(cfs_catecode,cfs_yearmonth,cfs_name,cfs_debit,cfs_credit,cfs_subof,cfs_typename) "
				+ "SELECT DISTINCT ca_code t1,'" + yearmonth
				+ "' t2,ca_name t3,0 t4,0 t5,ca_pcode t6,'非现金类(贷)' t7 FROM Category WHERE ca_isleaf=0 AND ca_code NOT IN "
				+ "(SELECT cfs_catecode FROM CashFlowSum WHERE cfs_typename='非现金类(贷)'  AND cfs_yearmonth='" + yearmonth + "') ");

		// 插入顶级科目--现金类(借)0.3
		baseDao.execute("INSERT INTO CashFlowSum(cfs_catecode,cfs_yearmonth,cfs_name,cfs_debit,cfs_credit,cfs_subof,cfs_typename) "
				+ "SELECT DISTINCT ca_code t1,'" + yearmonth
				+ "' t2,ca_name t3,0 t4,0 t5,ca_pcode t6,'现金类(借)' t7 FROM Category WHERE ca_isleaf=0 AND ca_code NOT IN "
				+ "(SELECT cfs_catecode FROM CashFlowSum WHERE cfs_typename='现金类(借)' AND cfs_yearmonth='" + yearmonth + "')");

		// 插入顶级科目--现金类(贷)0.4
		baseDao.execute("INSERT INTO CashFlowSum(cfs_catecode,cfs_yearmonth,cfs_name,cfs_debit,cfs_credit,cfs_subof,cfs_typename) "
				+ "SELECT DISTINCT ca_code t1,'" + yearmonth
				+ "' t2,ca_name t3,0 t4,0 t5,ca_pcode t6,'现金类(贷)' t7 FROM Category WHERE ca_isleaf=0 AND ca_code NOT IN "
				+ "(SELECT cfs_catecode FROM CashFlowSum WHERE cfs_typename='现金类(贷)' AND cfs_yearmonth='" + yearmonth + "')");

		SqlRowList rs = baseDao.queryForRowSet("SELECT ca_id,ca_code FROM Category WHERE ca_isleaf=0 ORDER BY ca_level DESC");
		while (rs.next()) {
			baseDao.execute("UPDATE CashFlowSum SET cfs_credit=(SELECT ROUND(SUM(NVL(cfs_credit,0)),2) "
					+ "FROM CashFlowSum WHERE cfs_yearmonth='" + yearmonth + "' AND cfs_catecode IN (SELECT ca_code FROM Category "
					+ "WHERE ca_subof='" + rs.getGeneralString(1) + "') and cfs_typename='非现金类(借)') WHERE cfs_catecode='"
					+ rs.getGeneralString(2) + "' and cfs_typename='非现金类(借)' AND cfs_yearmonth='" + yearmonth + "'");
			baseDao.execute("UPDATE CashFlowSum SET cfs_debit=(SELECT ROUND(SUM(NVL(cfs_debit,0)),2) FROM CashFlowSum "
					+ "WHERE cfs_yearmonth='" + yearmonth + "' AND cfs_catecode IN (SELECT ca_code FROM Category WHERE ca_subof='"
					+ rs.getGeneralString(1) + "') and cfs_typename='非现金类(贷)') WHERE cfs_catecode='" + rs.getGeneralString(2)
					+ "' and cfs_typename='非现金类(贷)' AND cfs_yearmonth='" + yearmonth + "'");
			baseDao.execute("UPDATE CashFlowSum SET cfs_credit=(SELECT ROUND(SUM(NVL(cfs_credit,0)),2) "
					+ "FROM CashFlowSum WHERE cfs_yearmonth='" + yearmonth + "' AND cfs_catecode IN (SELECT ca_code FROM Category "
					+ "WHERE ca_subof='" + rs.getGeneralString(1) + "') and cfs_typename='现金类(借)') WHERE cfs_catecode='"
					+ rs.getGeneralString(2) + "' and cfs_typename='现金类(借)' AND cfs_yearmonth='" + yearmonth + "'");
			baseDao.execute("UPDATE CashFlowSum SET cfs_debit=(SELECT ROUND(SUM(NVL(cfs_debit,0)),2) FROM CashFlowSum "
					+ "WHERE cfs_yearmonth='" + yearmonth + "' AND cfs_catecode IN (SELECT ca_code FROM Category WHERE ca_subof='"
					+ rs.getGeneralString(1) + "') and cfs_typename='现金类(贷)') WHERE cfs_catecode='" + rs.getGeneralString(2)
					+ "' and cfs_typename='现金类(贷)' AND cfs_yearmonth='" + yearmonth + "'");
		}
		baseDao.execute("INSERT INTO CashFlowSum(cfs_subof,cfs_typename,cfs_name,cfs_catecode,cfs_yearmonth,cfs_debit,cfs_credit) values('0','非现金类(借)','非现金类(借)','0.1','"
				+ yearmonth + "',0,0)");
		baseDao.execute("INSERT INTO CashFlowSum(cfs_subof,cfs_typename,cfs_name,cfs_catecode,cfs_yearmonth,cfs_debit,cfs_credit) values('0','非现金类(贷)','非现金类(贷)','0.2','"
				+ yearmonth + "',0,0)");
		baseDao.execute("INSERT INTO CashFlowSum(cfs_subof,cfs_typename,cfs_name,cfs_catecode,cfs_yearmonth,cfs_debit,cfs_credit) values('0','现金类(借)','现金类(借)','0.3','"
				+ yearmonth + "',0,0)");
		baseDao.execute("INSERT INTO CashFlowSum(cfs_subof,cfs_typename,cfs_name,cfs_catecode,cfs_yearmonth,cfs_debit,cfs_credit) values('0','现金类(贷)','现金类(贷)','0.4','"
				+ yearmonth + "',0,0)");

		baseDao.execute("INSERT INTO CashFlowSum(cfs_subof,cfs_typename,cfs_name,cfs_catecode,cfs_yearmonth,cfs_debit,cfs_credit) values('','全部','全部','0','"
				+ yearmonth + "',0,0)");
		baseDao.execute("UPDATE CashFlowSum SET cfs_subof='0.1' WHERE cfs_typename='非现金类(借)' AND NVL(cfs_subof,' ')=' ' and cfs_yearmonth='"
				+ yearmonth + "' ");
		baseDao.execute("UPDATE CashFlowSum SET cfs_subof='0.2' WHERE cfs_typename='非现金类(贷)' AND NVL(cfs_subof,' ')=' ' and cfs_yearmonth='"
				+ yearmonth + "' ");
		baseDao.execute("UPDATE CashFlowSum SET cfs_subof='0.3' WHERE cfs_typename='现金类(借)' AND NVL(cfs_subof,' ')=' ' and cfs_yearmonth='"
				+ yearmonth + "'");
		baseDao.execute("UPDATE CashFlowSum SET cfs_subof='0.4' WHERE cfs_typename='现金类(贷)' AND NVL(cfs_subof,' ')=' ' and cfs_yearmonth='"
				+ yearmonth + "' ");

		baseDao.execute(
				"UPDATE CashFlowSum A SET (cfs_debit,cfs_credit)=(select sum(cfs_debit),sum(cfs_credit) from CashFlowSum B where B.cfs_subof=A.cfs_catecode and B.cfs_yearmonth=A.cfs_yearmonth) where cfs_subof='0' and cfs_yearmonth=?",
				yearmonth);

		baseDao.execute("UPDATE CashFlowSum SET (cfs_debit,cfs_credit)=(select sum(cfs_debit),sum(cfs_credit) from CashFlowSum where cfs_yearmonth='"
				+ yearmonth
				+ "' and cfs_catecode in (select ca_code from category where ca_isleaf=1)) where cfs_typename='全部' and cfs_yearmonth='"
				+ yearmonth + "'");
		baseDao.execute("DELETE FROM CashFlowSum WHERE cfs_yearmonth='" + yearmonth
				+ "' and NVL(cfs_subof,' ')<>' ' and NVL(cfs_debit,0)=0 and NVL(cfs_credit,0)=0");
	}

	public List<Map<String, Object>> getCashFlow(String yearmonth, String type, String catecode) {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		// 根节点：全部、非现金、现金..
		if (type == null && catecode == null) {
			SqlRowList rs = baseDao.queryForRowSet("select * from cashflowsum where cfs_yearmonth=? and cfs_typename='全部'", yearmonth);
			if (rs.next()) {
				Map<String, Object> root = new HashMap<String, Object>();
				root.put("cfs_catecode", rs.getObject("cfs_catecode"));
				root.put("cfs_name", rs.getObject("cfs_name"));
				root.put("cfs_typename", rs.getObject("cfs_typename"));
				root.put("cfs_debit", rs.getObject("cfs_debit"));
				root.put("cfs_credit", rs.getObject("cfs_credit"));
				root.put("leaf", false);
				root.put("expanded", true);
				List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
				SqlRowList rd = baseDao.queryForRowSet(
						"select * from cashflowsum where cfs_yearmonth=? and cfs_subof ='0' order by cfs_catecode", yearmonth);
				while (rd.next()) {
					Map<String, Object> node = new HashMap<String, Object>();
					node.put("cfs_catecode", rd.getObject("cfs_catecode"));
					node.put("cfs_name", rd.getObject("cfs_name"));
					node.put("cfs_typename", rd.getObject("cfs_typename"));
					node.put("cfs_debit", rd.getObject("cfs_debit"));
					node.put("cfs_credit", rd.getObject("cfs_credit"));
					node.put("leaf", false);
					children.add(node);
				}
				root.put("children", children);
				data.add(root);
			}
		} else {
			SqlRowList rd = baseDao
					.queryForRowSet(
							"select * from cashflowsum left join category on cfs_catecode=ca_code where cfs_yearmonth=? and cfs_subof=? and cfs_typename=? order by cfs_catecode",
							yearmonth, catecode, type);
			while (rd.next()) {
				Map<String, Object> node = new HashMap<String, Object>();
				node.put("cfs_catecode", rd.getObject("cfs_catecode"));
				node.put("cfs_name", rd.getObject("cfs_name"));
				node.put("cfs_typename", rd.getObject("cfs_typename"));
				node.put("cfs_debit", rd.getObject("cfs_debit"));
				node.put("cfs_credit", rd.getObject("cfs_credit"));
				node.put("leaf", rd.getGeneralInt("ca_isleaf") != 0);
				node.put("ca_defaultcashcode", rd.getObject("ca_defaultcashcode"));
				node.put("ca_defaultcashflow", rd.getObject("ca_defaultcashflow"));
				data.add(node);
			}
		}
		return data;
	}

	@Override
	public List<Map<String, Object>> cashFlowSum(String yearmonth) {
		refreshCashFlow(yearmonth);
		return getCashFlow(yearmonth, null, null);
	}

	@Override
	public String cashFlowSet(String caller, String data) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Object catecode = null;
		Object flowcode = null;
		Object flowname = null;
		StringBuffer sb = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		String log = "";
		String log1 = "";
		String log2 = "";
		Map<Object, String> errors = new HashMap<Object, String>();
		if (maps.size() > 0) {
			catecode = maps.get(0).get("vd_catecode");
			flowcode = maps.get(0).get("flowcode");
			flowname = maps.get(0).get("flowname");
		}
		if (catecode != null && !catecode.equals("")) {
			String insertSql = null;
			String deleteSql = null;
			String updateSql = null;
			List<String> insqls = new ArrayList<String>();
			List<String> desqls = new ArrayList<String>();
			List<String> upsqls = new ArrayList<String>();
			baseDao.execute("delete from VoucherFlow where not exists (select 1 from voucherdetail where vd_void=vf_voucherid and vd_id=vf_vdid)");
			for (Map<Object, Object> map : maps) {
				Object[] cashflow = baseDao.getFieldsDataByCondition(
						"VoucherDetail left join Category on vd_catecode=ca_code left join Voucher on vo_id=vd_void", new String[] {
								"ca_cashflow", "vo_code", "vd_detno" }, "vd_id=" + map.get("vd_id"));

				if (cashflow != null && StringUtil.hasText(cashflow[0]) && "1".equals(cashflow[0].toString())) {
					sb2.append(",凭证：" + cashflow[1] + ",行号：" + cashflow[2]);
					break;
				}
				SqlRowList rs = baseDao
						.queryForRowSet("select vo_code from voucher where vo_id="
								+ map.get("vd_void")
								+ " and (nvl(vo_iscashflow,0)=0 or (nvl(vo_iscashflow,0)=1 and not exists (select 1 from voucherdetail left join category on vd_catecode=ca_code where vd_void=vo_id and nvl(ca_cashflow,0)=0)))");
				while (rs.next()) {
					errors.put(map.get("vd_void"), rs.getString("vo_code"));
					break;
				}

				deleteSql = "DELETE FROM VoucherFlow WHERE vf_vdid =" + map.get("vd_id");
				desqls.add(deleteSql);
				insertSql = "INSERT INTO VoucherFlow(vf_id, vf_voucherid, vf_detno,vf_flowid,vf_flowcode,vf_flowname,vf_inamount,vf_outamount,vf_vdid) "
						+ "VALUES (VOUCHERFLOW_SEQ.NEXTVAL,'"
						+ map.get("vd_void")
						+ "','"
						+ map.get("vd_detno")
						+ "','0','"
						+ flowcode
						+ "','"
						+ flowname
						+ "','"
						+ map.get("vd_creditcashflow")
						+ "','"
						+ map.get("vd_debitcashflow")
						+ "','"
						+ map.get("vd_id") + "' )  ";
				insqls.add(insertSql);
				updateSql = "update VoucherFlow set (vf_flowcode,vf_flowname)=(select ca_defaultcashcode,ca_defaultcashflow from category,voucherdetail where vd_catecode=ca_code and vf_vdid=vd_id and nvl(ca_defaultcashcode,' ')<>' ') where vf_vdid ="
						+ map.get("vd_id") + " and nvl(vf_flowcode,' ')=' '";
				upsqls.add(updateSql);
				upsqls.add("update VoucherDetail set (vd_flowcode,vd_flowname)=(select vf_flowcode,vf_flowname from VoucherFlow where vd_void=vf_voucherid and vf_vdid=vd_id and nvl(vf_flowcode,' ')<>' ') where vd_id ="
						+ map.get("vd_id"));
				upsqls.add(updateSql);
			}
			baseDao.execute(desqls);
			baseDao.execute(insqls);
			baseDao.execute(upsqls);
		} else {
			BaseUtil.showError("没有选择科目");
		}
		if (sb2.length() > 0) {
			log1 = "现金流量相关行无需设置：" + sb2.substring(1);
		}
		if (!errors.isEmpty()) {
			Iterator<Entry<Object, String>> it = errors.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Object, String> entry = (Entry<Object, String>) it.next();
				sb.append(",");
				sb.append(entry.getValue());
			}
			log2 = "现金流量相关科目无需设置现金流量项目！凭证号：" + sb.substring(1);
		}
		if (log1.length() > 0 || log2.length() > 0) {
			log = log1 + "\n" + log2;
		} else {
			log = "设置成功！";
		}
		return log;
	}

	// 清除无效数据
	@Override
	public void cleanInvalid(String yearmonth) {
		if ("".equals(yearmonth) || " ".equals(yearmonth) || yearmonth == null) {
			BaseUtil.showError("没有选择期间！");
			return;
		}
		int m = Integer.parseInt(yearmonth);
		// 1、明细行都为0或空，主表为1
		SqlRowList rs1 = baseDao
				.queryForRowSet("select vo_id from Voucher where  not exists(select 1 from voucherdetail left join category on vd_catecode=ca_code where ca_cashflow=1 and vd_void=vo_id) and  vo_iscashflow=1 and vo_yearmonth="
						+ m);
		while (rs1.next()) {
			int id = rs1.getInt("vo_id");
			baseDao.execute("update voucher set vo_iscashflow=0 where  vo_id=" + id);
			baseDao.execute("update VOUCHERDETAIL set VD_DEBITCASHFLOW=0,VD_CREDITCASHFLOW=0,VD_FLOWCODE=null,VD_FLOWNAME=null where VD_VOID ="
					+ id);
			baseDao.execute("delete  from VOUCHERFLOW where vf_voucherid =" + id);
		}
		// 2、明细行都为1，主表为1
		SqlRowList rs2 = baseDao
				.queryForRowSet("select vo_id from Voucher where  not exists(select 1 from voucherdetail left join category on vd_catecode=ca_code where nvl(ca_cashflow,0)=0 and vd_void=vo_id) and  vo_iscashflow=1 and vo_yearmonth="
						+ m);
		while (rs2.next()) {
			int id = rs2.getInt("vo_id");
			baseDao.execute("update VOUCHERDETAIL set VD_DEBITCASHFLOW=0,VD_CREDITCASHFLOW=0,VD_FLOWCODE=null,VD_FLOWNAME=null where VD_VOID ="
					+ id);
			baseDao.execute("delete  from VOUCHERFLOW where vf_voucherid =" + id);
		}
	}
}
