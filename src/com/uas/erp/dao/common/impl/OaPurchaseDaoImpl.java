package com.uas.erp.dao.common.impl;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.MoneyUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.OaPurchaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;

@Repository
public class OaPurchaseDaoImpl  extends BaseDao implements OaPurchaseDao {
	@Autowired
	private BaseDao baseDao;
	static final String OAPURCHASEDETAIL = "SELECT od_procode,od_proname,od_prounit,od_neednumber,op_date,op_code,od_detno,op_vecode,op_vename,op_id,od_price,od_id,od_rate FROM "
			+ "OAPurchaseDetail left join OAPurchase on od_oaid=op_id WHERE od_id=?";
	
	static final String GETVENDER = " select ve_apvendcode,ve_apvendname from vendor where ve_code=?";
	
	static final String INSERTOAPURCHASE="insert into Oapurchase(op_id,op_code,op_recordor,op_status,op_statuscode,op_date,op_department,op_appman,op_kind)values(?,?,?,?,?,?,?,?,?)";
	
	static final String INSERTOAPURCHASEDETAIL="insert into Oapurchasedetail(od_id,od_oaid,od_detno,od_oadetno,od_oacode,od_procode,od_proname,od_prounit,od_neednumber,od_price,od_needdate)values(?,?,?,?,?,?,?,?,?,?,?)";
	
	static final String OAINSERTOAACCEPTANCEDETAIL ="insert into Oaacceptancedetail(od_id,od_detno,od_opid,od_procode," +
			"od_proname,od_prounit,od_neednumber,od_opcode,od_opdetno,od_qty,od_price,od_sourcedetail,od_rate) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	static final String OAINSERTOAACCEPTANCE = "insert into Oaacceptance(op_code,op_status,op_statuscode,op_oacode,op_recordorid,op_recordor,op_date,op_id,op_vecode,op_vename,op_type,op_isinstore) values (?,?,?,?,?,?,?,?,?,?,?,?)";
	
	static final String INSERTBASEPRODIO = "INSERT INTO prodinout(pi_id,pi_inoutno,pi_class,pi_invostatus,pi_invostatuscode"
			+ ",pi_operatorcode,pi_recordman,pi_recorddate,pi_statuscode,pi_status,pi_updatedate,pi_updateman,pi_cardcode,pi_title) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	static final String INSERPRODINDETAIL = "INSERT INTO prodiodetail(pd_id,pd_inoutno,pd_piclass,pd_piid,pd_pdno,pd_ordercode,pd_orderdetno,pd_prodcode"
			+ ",pd_inqty,pd_orderprice,pd_taxrate,pd_orderid,pd_auditstatus) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	static final String CHECK_YQTY = "SELECT op_code,od_detno,od_neednumber FROM OAPurchaseDetail,OAPurchase WHERE od_oaid=op_id and od_id=? and nvl(od_neednumber,0)<?";
	static final String OAPURCHASE_PRICE_APPSTATUS ="select ppd_price,ppd_rate,ppd_id from PurchasePriceDetail left join PurchasePrice on ppd_ppid=pp_id left join product on ppd_prodcode=pr_code where ppd_vendcode=? and ppd_prodcode=? and ppd_currency=? and pp_kind = '用品' and to_char(ppd_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' and (nvl(ppd_appstatus,' ')='合格' or pr_material<>'已认可') and ppd_lapqty<=round(?/case when nvl(pr_purcrate,0)=0 then 1 else pr_purcrate end,2) order by ppd_price ";
	static final String OAPURCHASE_PRICE ="select ppd_price,ppd_rate,ppd_id from PurchasePriceDetail left join PurchasePrice on ppd_ppid=pp_id left join product on ppd_prodcode=pr_code where ppd_vendcode=? and ppd_prodcode=? and ppd_currency=? and pp_kind = '用品' and to_char(ppd_fromdate,'yyyymmdd')<=to_char(sysdate,'yyyymmdd') and to_char(ppd_todate,'yyyymmdd')>=to_char(sysdate,'yyyymmdd') AND pp_statuscode='AUDITED' AND ppd_statuscode='VALID' and ppd_lapqty<=round(?/case when nvl(pr_purcrate,0)=0 then 1 else pr_purcrate end,2) order by ppd_price ";
	static final String OP_TOTAL = "update Oapurchase set op_total=(select round(sum(od_total),2) from OapurchaseDetail where od_oaid=op_id) where op_id=?";
	
	
	@Override
	public void checkPdYqty(List<Map<Object, Object>> datas) {
		Object y = 0;// 已转收料单数量
		Object r = 0;// 验退数量
		SqlRowList rs = null;
		boolean bool = false;
		Object[] pus = null;
		int id = 0;
		for (Map<Object, Object> d : datas) {
			id = Integer.parseInt(d.get("od_id").toString());
			pus = getFieldsDataByCondition("OAPurchaseDetail left join OAPurchase on od_oaid=op_id", "op_code,od_detno",
					"od_id=" + id);
			if (pus != null) {
				bool = checkIf("OAPurchase", "op_code='" + pus[0] + "' and op_statuscode='AUDITED'");
				if (!bool) {
					BaseUtil.showError("用品采购单:" + pus[0] + " 未审核通过,无法转收料单!");
				}
				y = getFieldDataByCondition("ProdIODetail", "sum(nvl(pd_inqty,0))", "pd_ordercode='" + pus[0]
						+ "' and pd_piclass='用品验收单' and  pd_orderdetno=" + pus[1]);
				y = y == null ? 0 : y;
				r = getFieldDataByCondition("ProdIODetail", "sum(nvl(pd_outqty,0))", "pd_ordercode='" + pus[0]
						+ "' and pd_orderdetno=" + pus[1] + " and pd_status>0 and pd_piclass='用品验退单'");
				r = r == null ? 0 : r;
				rs = queryForRowSet(CHECK_YQTY, id, Double.parseDouble(y.toString())- Double.parseDouble(r.toString()));
				if (rs.next()) {
					StringBuffer sb = new StringBuffer("本次数量填写超出可转数量,采购单号:").append(rs.getString("op_code"))
							.append(",行号:").append(rs.getInt("od_detno")).append(",采购数:")
							.append(rs.getDouble("od_neednumber")).append(",已转验收数:").append(y).append(",已验退数:").append(r)
							.append(",本次数:").append(d.get("od_tqty"));
					BaseUtil.showError(sb.toString());
				}
			}
		}
	}
	

	@Override
	@Transactional
	public String turnAccept(String caller, List<Map<Object, Object>> maps,
			Employee employee, String language) {
		int count = 1;
		String code = null;
		int vaid = 0;
		Set<String> codes = new HashSet<String>();// 根据采购明细ID，找出对应的哪些采购主表的code
		Set<Object> ids = new HashSet<Object>();// 根据采购明细ID，找出对应的哪些采购主表的code
		String log = null;
		for (Map<Object, Object> map : maps) {
			SqlRowList rs = queryForRowSet(OAPURCHASEDETAIL, new Object[] { map.get("od_id") });
			if (rs.next()) {
				if (code == null) {
					code = sGetMaxNumber("ProdInOut!GoodsIn", 2);
					vaid = getSeqId("ProdInout_SEQ");
					Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
					String op_vecode=rs.getObject("op_vecode")==null?"":rs.getObject("op_vecode").toString();
					String op_vename=rs.getObject("op_vename")==null?"":rs.getObject("op_vename").toString();
					getJdbcTemplate().update(INSERTBASEPRODIO,
							new Object[] {vaid, code, "用品验收单", BaseUtil.getLocalMessage("ENTERING"),
							"ENTERING", employee.getEm_code(), employee.getEm_name(), time,
							"UNPOST", BaseUtil.getLocalMessage("UNPOST"), time,
							employee.getEm_name(),op_vecode,op_vename});
				}
				int vadid = getSeqId("ProdInoutdetail_SEQ");
				String pucode = rs.getObject("op_code").toString();					
				getJdbcTemplate().update(INSERPRODINDETAIL,
						new Object[] { vadid,code,"用品验收单", vaid, count++,rs.getString("op_code"), rs.getString("od_detno"),rs.getString("od_procode"),Double.parseDouble(map.get("od_tqty").toString()),rs.getDouble("od_price"),rs.getObject("od_rate"),rs.getObject("od_id"),"ENTERING"});
				// 转成功就修改purchaseDetail的[已转数量]
				Object qt = getFieldDataByCondition("OAPurchaseDetail", "od_yqty", "od_id=" + map.get("od_id"));
				qt = qt == null ? 0 : qt;
				Double yqty = Double.parseDouble(qt.toString()) + Double.parseDouble(map.get("od_tqty").toString());
				updateByCondition("OAPurchaseDetail", "od_yqty=" + yqty, "od_id=" + map.get("od_id"));
				// 按采购单号分组
				if (!codes.contains(pucode)) {
					codes.add(pucode);
				}
				if(!ids.contains(rs.getObject("op_id"))){
					ids.add(rs.getObject("op_id"));
				}
				// 记录日志
				logMessage(new MessageLog(employee.getEm_name(), BaseUtil.getLocalMessage("msg.turnVerifyApply",
						language), BaseUtil.getLocalMessage("msg.turnSuccess", language) + ","
						+ BaseUtil.getLocalMessage("msg.detail", language) + rs.getInt("od_detno"), "OAPurchase|op_id="
						+ rs.getInt("op_id")));
			}
		}
		log = "转入成功,收料单号:" + "<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?formCondition=pi_idIS" + vaid
							+ "&gridCondition=pd_piidIS" + vaid + "&whoami=ProdInOut!GoodsIn')\">" + code + "</a>";
		Iterator<Object> iteratorID = ids.iterator();
		while(iteratorID.hasNext()){
			Object idObject = iteratorID.next();
			String sql ="select round(sum(od_neednumber),2),round(sum(od_yqty),2) from OAPurchaseDetail where od_oaid="+idObject; 
			SqlRowList rs1 = queryForRowSet(sql);
			if(rs1.next()){
				double od_neednumber = rs1.getGeneralDouble(1);
				double od_yqty = rs1.getGeneralDouble(2);
				if(od_neednumber==od_yqty){
					updateByCondition("OAPurchase", "op_isturn='已转收料'", "op_id="+idObject);
				}
			}
		}
		return log;
	}


	@Override
	public void deleteById(String employee, String language, int op_id) {
		List<Object[]> objs = getFieldsDatasByCondition("Oaacceptancedetail", new String[] { "od_id", "od_qty" },
				"od_opid=" + op_id);
		for (Object[] obj : objs) {
			if (Integer.parseInt(obj[1].toString()) > 0) {
				// 还原请购明细及请购单
				restoreApplication(Integer.parseInt(obj[0].toString()), language);
			}
			deleteByCondition("Oaacceptancedetail", "od_id=" + obj[0]);
		}
	}

	
	/**
	 * 采购验收单单删除时，修改请购单状态、数量等
	 */
	public void restoreApplication(int pdid, String language) {
		Object[] objs = getFieldsDataByCondition("Oaacceptancedetail", new String[] { "od_sourcedetail", "od_qty",
				"od_opcode" }, "od_id=" + pdid);
		if (objs != null && objs[2] != null) {
			updateByCondition("OApurchasedetail", "od_yqty=nvl(od_yqty,0)-" + Integer.parseInt(objs[1].toString()),
					"od_id=" + objs[0]);
		}
	}


	@Override
	public JSONObject newOAPurchaseWithVendor(String type, int parseInt,
			String vendcode, String vendname,  String currency) {
	/*	int opid = getSeqId("OAPURCHASE_SEQ");
		String code = sGetMaxNumber("OAPurchase", 2);
		Object[] objs = getFieldsDataByCondition("Vendor", new String[] { "ve_buyerid", "ve_buyername",
				"ve_paymentcode", "ve_payment", "ve_apvendcode", "ve_apvendname", "ve_currency", "ve_rate" },
				"ve_code='" + vendcode + "'");
		Object rate = objs[7];
		if (currency == null || "".equals(currency)) {
			// 获取供应商币别
			currency = String.valueOf(objs[6]);
		} else {
			rate = getFieldDataByCondition("Currencys", "cr_rate", "cr_name='" + currency + "'");
		}
		boolean bool = execute(
				INSERTOAPURCHASE,
				new Object[] { opid, code, SystemSession.getUser().getEm_name(), "在录入", "ENTERING",
						Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), depart, appman, opkind});
		if (bool) {
			JSONObject j = new JSONObject();
			j.put("pu_id", puid);
			j.put("pu_code", code);
			return j;
		}*/
		return null;
	}


	@Override
	public void toAppointedOAPurchase(Object code, int adid, double tqty) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 到物料核价单取用品采购单价
	 * pu_date 取价时间: oracle的时间格式字符串，to_date()或者sysdate
	 * @return {JSONObject} {od_price: 0.00,od_rate: 0.00}
	 */
	@Override
	public JSONObject getOAPurchasePrice(String vendcode, String prodcode, String currency, double qty, String od_date) {
		/* 取setting表查看取价取供应商原则，需不需要认定 */
		String sqlstr = null;
		if (isDBSetting("OAapplicaitonToPurchase!Deal", "onlyQualifiedPrice")) {
			sqlstr = OAPURCHASE_PRICE_APPSTATUS;
		} else {
			sqlstr = OAPURCHASE_PRICE;
		}
		sqlstr = sqlstr.replaceAll("sysdate", od_date);
		SqlRowList rs = queryForRowSet(sqlstr, vendcode, prodcode, currency, qty);
		if (rs.next()) {
			JSONObject obj = new JSONObject();
			obj.put("od_price", rs.getGeneralDouble("ppd_price"));
			obj.put("od_rate", rs.getGeneralDouble("ppd_rate"));
			obj.put("od_ppdid", rs.getGeneralDouble("ppd_id"));
			return obj;
		}
		return null;
	}
	@Override
	public String getPrice(int op_id) {
		StringBuffer error = new StringBuffer();
		List<Object[]> objects = getFieldsDatasByCondition("oapurchasedetail left join oapurchase on od_oaid = op_id", new String[] {
				"od_procode", "op_vecode", "op_currency", "od_neednumber", "od_id" }, "od_oaid=" + op_id);
		if (objects.size() > 0) {
			JSONObject js = null;
			for (Object[] obj : objects) {
				Object opty = getFieldDataByCondition("oapurchasedetail", "sum(od_neednumber)",
						" od_oaid=" + op_id + " and od_procode='" + String.valueOf(obj[0]) + "'");
				Object op_date = getFieldDataByCondition("oapurchase", "to_char(op_date,'yyyy-mm-dd')", "op_id="+op_id);
				//String vendcode, String prodcode, String currency, double qty, String od_date)
				js = getOAPurchasePrice(String.valueOf(obj[1]), String.valueOf(obj[0]), String.valueOf(obj[2]), 
						Double.parseDouble(opty.toString()),DateUtil.parseDateToOracleString(Constant.YMD, (String)op_date));
				double price = 0;
				double qty = Double.parseDouble(obj[3].toString());
				if (js != null) {
					price = NumberUtil.formatDouble(js.getDouble("od_price"), 6);
					baseDao.execute("update OapurchaseDetail set "
							+ "od_price="+ price
							+ ",od_rate=" + js.getDouble("od_rate")
							+ ",od_ppdid="+ js.getDouble("od_ppdid")
							+ ",od_total="+NumberUtil.formatDouble(qty * price, 2)
							+ " where od_id=" + obj[4]);
				} else {
					error.append("根据 物料编号:[" + obj[0] + "],供应商号:["
							+ obj[1] + "],币别:[" + obj[2]+ "] 在物料核价单未找到对应单价，或单价为空值、0等!<BR/>");
					baseDao.execute("update OapurchaseDetail set od_price=0,od_ppdid=0 where od_id=" + obj[4]);
				}
			}
			// 主表金额
			execute(OP_TOTAL, op_id);
			baseDao.execute("update Oapurchase set op_totalupper=L2U(nvl(op_total,0)) WHERE op_id=" + op_id);
			if (error.length() > 0) {
				return error.toString();
			}
		}
		return null;
	}
}
