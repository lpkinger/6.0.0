package com.uas.erp.dao.common.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
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
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlMap;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ApplicationDao;

@Repository
public class ApplicationDaoImpl extends BaseDao implements ApplicationDao {
	@Autowired
	private BaseDao baseDao;
	
	static final String TURNPURC = "SELECT ap_code,ap_delivery,ap_costcenter,ap_vendid,ap_vendcode,ap_vendname,ap_cop"
			+ ",ap_kind,ap_type,nvl(ap_buyername, ve_buyername) ap_buyername,nvl(em_id,ve_buyerid) em_id FROM application left join vendor on ve_code=ap_vendcode left join employee on ap_buyercode=em_code WHERE ap_id=?";
	static final String INSERTPURC = "INSERT INTO purchase(pu_id,pu_code,pu_status,pu_statuscode,pu_source,pu_sourcecode,pu_delivery,pu_costcenter,pu_vendid,pu_vendcode,pu_vendname"
			+ ",pu_cop,pu_kind,pu_recordid,pu_recordman,pu_indate,pu_type,pu_buyerid,pu_buyername) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERTBASEPURC = "INSERT INTO purchase(pu_id,pu_code,pu_status,pu_statuscode"
			+ ",pu_recordid,pu_recordman,pu_indate,pu_type) VALUES (?,?,?,?,?,?,?,?)";
	static final String INSERTPURCWITHVENDOR = "INSERT INTO purchase(pu_id,pu_code,pu_status,pu_statuscode,pu_recordid,pu_recordman,pu_indate,pu_vendid"
			+ ", pu_vendcode,pu_vendname,pu_buyerid,pu_buyername,pu_type,pu_currency,pu_kind,pu_isinit,pu_rate,pu_paymentscode,pu_payments,pu_receivecode"
			+ ",pu_receivename,pu_printstatus,pu_printstatuscode,pu_buyercode) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'UNPRINT',?)";
	static final String TURNPURCDETAIL = "SELECT ad_prodcode,ad_vendid,ad_vendor,ad_vendname,ad_qty,ad_price,ad_total,ad_delivery,ad_id,ad_yqty,ap_pleamanid,ap_pleamanname FROM applicationdetail left join application on ad_apid=ap_id"
			+ " WHERE ad_apid=?";

	static final String TURNBASEPURCDETAIL = "SELECT ad_prodcode,ad_vendid,ad_vendor,ad_vendname,ad_qty,ad_price,ad_total,ad_delivery,ap_id,ap_code,ad_remark,ap_pleamanid,ap_pleamanname,ad_bonded,ap_remark,ad_use FROM applicationdetail left join application on ad_apid=ap_id"

			+ " WHERE ad_id=?";
	static final String TURNBASEPURCDETAIL_REP = "SELECT mr_repcode ad_prodcode,mr_veid ad_vendid,mr_vendor ad_vendor, mr_vendname ad_vendname,ad_qty,0 ad_price,ad_total,ad_delivery,ap_id,ap_code,ad_remark,ap_pleamanid,ap_pleamanname,ad_bonded,ap_remark,ad_use FROM mrpreplace left join applicationdetail on mr_mdid=ad_mdid left join application on ad_apid=ap_id"

			+ " WHERE mr_id=?";
	static final String GETPUBYSOURCE = "select pu_code from Purchase where pu_sourcecode=(select ap_code from application left join applicationdetail on ap_id=ad_apid where ad_id=?)";
	static final String GETVENDOR = "SELECT ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid,ppd_price,ppd_rate,ppd_id FROM (SELECT ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid,ppd_price,ppd_rate,ppd_id FROM PurchasePriceDetail,Currencys,PurchasePrice WHERE "
			+ "ppd_currency=cr_name and pp_id=ppd_ppid and pp_statuscode='AUDITED' and ppd_statuscode='VALID' and pp_kind='采购' and ppd_prodcode=(SELECT ad_prodcode FROM applicationdetail where ad_id=?) and ppd_lapqty<=(select round(ad_qty/(case when nvl(pr_purcrate,0)=0 then 1 else pr_purcrate end),2) from applicationdetail left join product on ad_prodcode=pr_code where ad_id=?) and nvl(ppd_todate, nvl(pp_todate, sysdate)) + 1>sysdate order by ppd_price*cr_rate*(1-nvl(ppd_rate,0)/(100+nvl(ppd_rate,0))) asc,ppd_id desc) where rownum<2";
	/* 取认定合格的供应商 */
	static final String GETAPPVENDOR = "SELECT ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid,ppd_price,ppd_rate,ppd_id FROM (SELECT ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid,ppd_price,ppd_rate,ppd_id FROM PurchasePriceDetail,Currencys,PurchasePrice,product WHERE "
			+ "ppd_currency=cr_name and pp_id=ppd_ppid and ppd_prodcode=pr_code and pp_statuscode='AUDITED' and ppd_statuscode='VALID' and (nvl(ppd_appstatus,' ')='合格' or pr_material<>'已认可') and pp_kind='采购' and ppd_prodcode=(SELECT ad_prodcode FROM applicationdetail where ad_id=?) and ppd_lapqty<=(select round(ad_qty/(case when nvl(pr_purcrate,0)=0 then 1 else pr_purcrate end),2) from applicationdetail left join product on ad_prodcode=pr_code where ad_id=?) and nvl(ppd_todate, nvl(pp_todate, sysdate)) + 1>sysdate order by ppd_price*cr_rate*(1-nvl(ppd_rate,0)/(100+nvl(ppd_rate,0))) asc,ppd_id desc) where rownum<2";
	static final String SETVENDOR = "UPDATE ApplicationDetail SET ad_ifvendrate=0,ad_vendor=?,ad_vendname=?,ad_currency=?,ad_vendid=?,ad_barcode=?,ad_purcprice=?,ad_rate=?,ad_ppdid=? WHERE ad_id=?";
	static final String REPLACE_GETVENDOR = "SELECT ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid,ppd_id FROM (SELECT ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid,ppd_id FROM PurchasePriceDetail,Currencys,PurchasePrice WHERE "
			+ "ppd_currency=cr_name and pp_id=ppd_ppid and pp_statuscode='AUDITED' and ppd_statuscode='VALID' and pp_kind='采购' and ppd_prodcode=(SELECT mr_repcode FROM mrpreplace where mr_id=?) and ppd_lapqty<=(select mr_needqty from mrpreplace where mr_id=?) and nvl(ppd_todate, nvl(pp_todate, sysdate)) + 1>sysdate order by ppd_price*cr_rate*(1-nvl(ppd_rate,0)/(100+nvl(ppd_rate,0))) asc,ppd_id desc) where rownum<2";
	static final String REPLACE_SETVENDOR = "UPDATE mrpreplace SET mr_ifvendrate=0,mr_vendor=?,mr_vendname=?,mr_ppdid=? WHERE mr_id=?";
	static final String APPREPLACE_GETVENDOR="SELECT ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid,ppd_id FROM (SELECT ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid,ppd_id FROM PurchasePriceDetail,Currencys,PurchasePrice WHERE "
			+ " ppd_currency=cr_name and pp_id=ppd_ppid and pp_statuscode='AUDITED' and ppd_statuscode='VALID' and pp_kind='采购' and ppd_prodcode=(SELECT ar_repcode FROM ApplicationReplace where ar_id=?) "
			+ " and ppd_lapqty<=(select ar_needqty from ApplicationReplace where ar_id=?) and nvl(ppd_todate, nvl(pp_todate, sysdate)) + 1>sysdate order by ppd_price*cr_rate*(1-nvl(ppd_rate,0)/(100+nvl(ppd_rate,0))) asc,ppd_id desc) where rownum<2";
	
	/* 按最新有效定价自动获取供应商 */
	static final String GETVENDORBYDATE = "SELECT ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid,ppd_price,ppd_rate,ppd_id FROM (SELECT ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid,ppd_price,ppd_rate,ppd_id FROM PurchasePriceDetail,Currencys,PurchasePrice WHERE "
			+ "ppd_currency=cr_name and pp_id=ppd_ppid and pp_statuscode='AUDITED' and ppd_statuscode='VALID' and pp_kind='采购' and ppd_prodcode=(SELECT ad_prodcode FROM applicationdetail where ad_id=?) and ppd_lapqty<=(select ad_qty from applicationdetail where ad_id=?) and nvl(ppd_todate, nvl(pp_todate, sysdate)) + 1>sysdate order by pp_indate desc,ppd_id desc) where rownum<2";
	static final String REPLACE_GETVENDORBYDATE = "SELECT ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid,ppd_id FROM (SELECT ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid,ppd_id FROM PurchasePriceDetail,Currencys,PurchasePrice WHERE "
			+ "ppd_currency=cr_name and pp_id=ppd_ppid and pp_statuscode='AUDITED' and ppd_statuscode='VALID' and pp_kind='采购' and ppd_prodcode=(SELECT mr_repcode FROM mrpreplace where mr_id=?) and ppd_lapqty<=(select mr_needqty from mrpreplace where mr_id=?) and nvl(ppd_todate, nvl(pp_todate, sysdate)) + 1>sysdate order by pp_indate desc,ppd_id desc) where rownum<2";
	static final String APPREPLACE_GETVENDORBYDATE="SELECT ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid FROM (SELECT ppd_vendcode,ppd_vendname,ppd_currency,ppd_vendid FROM PurchasePriceDetail,Currencys,PurchasePrice WHERE "
			+ " ppd_currency=cr_name and pp_id=ppd_ppid and pp_statuscode='AUDITED' and ppd_statuscode='VALID' and pp_kind='采购' and ppd_prodcode=(SELECT ar_repcode FROM ApplicationReplace where ar_id=?) "
			+ " and ppd_lapqty<=(select ar_needqty from ApplicationReplace where ar_id=?) and nvl(ppd_todate, nvl(pp_todate, sysdate)) + 1>sysdate order by  pp_indate desc,ppd_id desc) where rownum<2";
	
	@Override
	@Transactional
	public int turnPurchase(int id) {
		try {
			SqlRowList list = queryForRowSet(TURNPURC, id);
			int puid = 0;
			SqlMap map = null;
			while (list.next()) {
				puid = getSeqId("PURCHASE_SEQ");
				String code = sGetMaxNumber("Purchase", 2);
				String sourcecode = list.getString("AP_CODE").toString();
				boolean bool = execute(
						INSERTPURC,
						new Object[] { puid, code, BaseUtil.getLocalMessage("ENTERING"), "ENTERING", id, sourcecode,
								list.getDate("AP_DELIVERY"), list.getString("AP_COSTCENTER"), list.getInt("AP_VENDID"),
								list.getString("AP_VENDCODE"), list.getString("AP_VENDNAME"), list.getString("AP_COP"),
								list.getString("AP_KIND"), SystemSession.getUser().getEm_id(), SystemSession.getUser().getEm_name(),
								Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), list.getString("AP_TYPE"),
								list.getObject("em_id"), list.getObject("ap_buyername") });
				if (bool) {
					list = queryForRowSet(TURNPURCDETAIL, id);
					int count = 1;
					while (list.next()) {
						float qty = list.getFloat("AD_QTY") - list.getFloat("AD_YQTY");// 实际应转入值
						double price = list.getString("AD_PRICE") == null ? 0 : list.getDouble("AD_PRICE");
						if (qty > 0) {
							map = new SqlMap("purchasedetail");
							map.set("pd_id", getSeqId("PURCHASEDETAIL_SEQ"));
							map.set("pd_puid", puid);
							map.set("pd_code", code);
							map.set("pd_detno", count++);
							map.set("pd_prodcode", list.getString("AD_PRODCODE"));
							map.set("pd_vendid", list.getInt("AD_VENDID"));
							map.set("pd_vendcode", list.getString("AD_VENDOR"));
							map.set("pd_vendname", list.getString("AD_VENDNAME"));
							map.set("pd_qty", qty);
							map.set("pd_price", price);
							map.set("pd_total", price * qty);
							map.set("pd_delivery", list.getDate("AD_DELIVERY"));
							map.set("pd_source", id);
							map.set("pd_sourcecode", sourcecode);
							map.set("pd_sourcedetail", list.getInt("AD_ID"));
							map.set("pd_sellercode", list.getObject("ap_pleamanid"));
							map.set("pd_seller", list.getObject("ap_pleamanname"));
							map.execute();
						}
					}
				}
			}
			return puid;
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
			return 0;
		}
	}

	@Override
	public String newPurchase(String type) {
		int puid = getSeqId("PURCHASE_SEQ");
		String code = sGetMaxNumber("Purchase", 2);
		boolean bool = execute(INSERTBASEPURC,
				new Object[] { puid, code, BaseUtil.getLocalMessage("ENTERING"), "ENTERING", SystemSession.getUser().getEm_id(),
						SystemSession.getUser().getEm_name(), Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), type });
		if (bool) {
			return code;
		}
		return null;
	}

	@Override
	public JSONObject newPurchaseWithVendor(String type, int vendid, String vendcode, String vendname, String conKind, String currency) {
		int puid = getSeqId("PURCHASE_SEQ");
		String code = sGetMaxNumber("Purchase", 2);
		Object[] objs = getFieldsDataByCondition("Vendor", new String[] { "ve_buyerid", "ve_buyername", "ve_paymentcode", "ve_payment",
				"ve_apvendcode", "ve_apvendname", "ve_currency", "ve_rate", "ve_buyercode", "ve_shipment","nvl(ve_ifdeliveryonb2b,0)" }, "ve_code='" + vendcode + "'");

		// 如果PurchaseKind中有设置对应的前缀码 用新前缀码替换旧前缀码
		Object newLCode = getFieldDataByCondition("PurchaseKind", "pk_excode", "pk_name='" + conKind + "'");
		if (newLCode != null) {
			if (!newLCode.toString().equals("")) {
				// 修改前缀
				code = newLCode + code;
			}
		}
		//采购订单逻辑配置增加一个逻辑配置“供应商启用B2B收料，采购PO前缀增加 ”
		String B2BPrefix=baseDao.getDBSetting("Purchase", "VendorUseB2BAddPrefix");
		if("-1".equals(objs[10].toString())&&B2BPrefix!=null&&!"".equals(B2BPrefix)){
			code = B2BPrefix + code;
		}
		Object rate = objs[7];
		if (currency == null || "".equals(currency)) {
			// 获取供应商币别
			currency = String.valueOf(objs[6]);
		} else {
			rate = getFieldDataByCondition("Currencys", "cr_rate", "cr_name='" + currency + "'");
		}
		boolean bool = execute(INSERTPURCWITHVENDOR,
				new Object[] { puid, code, BaseUtil.getLocalMessage("ENTERING"), "ENTERING", SystemSession.getUser().getEm_id(),
						SystemSession.getUser().getEm_name(), Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), vendid,
						vendcode, vendname, objs[0], objs[1], type, currency, conKind, "0", rate, objs[2], objs[3], objs[4], objs[5],
						BaseUtil.getLocalMessage("UNPRINT"), objs[8]});
		if (bool) {
			execute("update purchase set pu_transport='"+objs[9]+"' where pu_id=" + puid + " and nvl(pu_transport,' ')=' '");
			JSONObject j = new JSONObject();
			j.put("pu_id", puid);
			j.put("pu_code", code);
			return j;
		}
		return null;
	}

	@Override
	public String getPuCodeBySourceCode(int id) {
		SqlRowList rs = queryForRowSet(GETPUBYSOURCE, new Object[] { id });
		if (rs.next()) {
			return rs.getString(1);
		}
		return null;
	}

	/**
	 * 从PurchasePrice获取供应商，币别
	 */
	@Override
	@Transactional
	public void getVendor(int[] id) {
		String sqlstr = "", getsqlstr = null,getreplacesqlstr=REPLACE_GETVENDOR,appreplaceStr=APPREPLACE_GETVENDOR;
		Boolean isvendorate = isDBSetting("vendorRate"); // 是否启用供应商比例分配
		Boolean getvendorbydate =isDBSetting("Application!ToPurchase!Deal", "getvendorBydate");
		Boolean isgetrate = false;
		if (isDBSetting("Application!ToPurchase!Deal", "onlyQualifiedPrice")) {
			getsqlstr = GETAPPVENDOR;
		} else{
			getsqlstr = GETVENDOR;
		}
		for (int idx : id) {
			String ad_barcode = "采购开发";
			isgetrate = false;
			SqlRowList rs;
			if (isvendorate) { // 启用分配则优先从比例分配表获取供应商
				rs = queryForRowSet("select * from (SELECT ad_id,ad_prodcode,ad_vendor,pv_vendcode,ve_name,pv_currency,ve_id,pv_price,pv_taxrate"
						+ ",rank() over (PARTITION BY ad_id order by pv_setrate-100*(NVL(pv_nowthisqty,0)+ad_qty)/(0.1+NVL(pv_nowallqty,0)+ad_qty)desc) mm FROM applicationdetail "
						+ " left join ProductVendorRate on ad_prodcode=pv_prodcode and NVL(pv_action,' ')<>'无效'  left join vendor on pv_vendcode=ve_code where ad_id="
						+ idx + " ) where mm=1 and pv_vendcode<>' '");
				if (rs.next()) {
					ad_barcode = "采购员";
					isgetrate = true;
					execute(SETVENDOR,
							new Object[] { rs.getObject("pv_vendcode"), rs.getObject("ve_name"), rs.getObject("pv_currency"),
									rs.getObject("ve_id"), ad_barcode,rs.getObject("pv_price"),rs.getObject("pv_taxrate"), null,idx });
				} else {
					execute("UPDATE ApplicationDetail SET ad_vendor=null,ad_vendname=null,ad_currency=null,ad_vendid=0,ad_barcode=null WHERE ad_id="
							+ idx);
				}
				// 获取替代料供应商
				sqlstr = "MERGE INTO mrpreplace USING (select * from (SELECT mr_id,mr_repcode,mr_vendor,pv_vendcode,ve_name,pv_currency,ve_id"
						+ ",rank() over (PARTITION BY mr_id order by pv_setrate-100*(NVL(pv_nowthisqty,0)+ad_qty)/(0.1+NVL(pv_nowallqty,0)+ad_qty)desc) mm FROM mrpreplace left join applicationdetail on mr_mdid=ad_mdid "
						+ " left join ProductVendorRate on mr_repcode=pv_prodcode and NVL(pv_action,' ')<>'无效' left join vendor on pv_vendcode=ve_code where mr_mdid in (select ad_mdid "
						+ " from applicationdetail where ad_id="
						+ idx
						+ " and ad_mdid>0) ) where mm=1)src "
						+ " on (src.mr_id=mrpreplace.mr_id) when matched then update set mr_ifvendrate=-1,mr_vendor=src.pv_vendcode,mr_vendname=src.ve_name "
						+ " WHERE mr_mdid in (select ad_mdid from applicationdetail where ad_id=" + idx + " and ad_mdid>0)";
				execute(sqlstr);

				// 原本更新到MRPReplace表的业务,也加到ApplicationReplace表
				sqlstr = "MERGE INTO ApplicationReplace USING (select * from (SELECT ar_id,ar_repcode,ar_vendor,pv_vendcode,ve_name,pv_currency,ve_id"
						+ ",rank() over (PARTITION BY ar_id order by pv_setrate-100*(NVL(pv_nowthisqty,0)+ad_qty)/(0.1+NVL(pv_nowallqty,0)+ad_qty)desc) mm FROM ApplicationReplace left join applicationdetail on ar_mdid=ad_mdid "
						+ " left join ProductVendorRate on ar_repcode=pv_prodcode and NVL(pv_action,' ')<>'无效' left join vendor on pv_vendcode=ve_code where ar_mdid in (select ad_mdid "
						+ " from applicationdetail where ad_id="
						+ idx
						+ " and ad_mdid>0) ) where mm=1)src "
						+ " on (src.ar_id=ApplicationReplace.ar_id) when matched then update set ar_ifvendrate=-1,ar_vendor=src.pv_vendcode,ar_vendname=src.ve_name "
						+ " WHERE ar_mdid in (select ad_mdid from applicationdetail where ad_id=" + idx + " and ad_mdid>0)";
				execute(sqlstr);
			}
			if (!isgetrate) {// 不启用比例或未获取到供应商，则取价格库最低价供应商
				if (getvendorbydate){
					getsqlstr=GETVENDORBYDATE;
					getreplacesqlstr=REPLACE_GETVENDORBYDATE;
					appreplaceStr=APPREPLACE_GETVENDORBYDATE;
				} 
				rs = queryForRowSet(getsqlstr, idx, idx);
				if (rs.next()) {
					ad_barcode = "采购员";
					execute(SETVENDOR, new Object[] { rs.getObject(1), rs.getObject(2), rs.getObject(3), rs.getObject(4) ,ad_barcode, rs.getObject(5),rs.getObject(6),rs.getObject("ppd_id"), idx });
				} else {
					execute("update applicationdetail set ad_ppdid=0,ad_vendor=null,ad_vendname=null,ad_currency=null,ad_vendid=0,ad_barcode='"
							+ ad_barcode + "' where ad_id=" + idx);
				}

				rs = queryForRowSet("select mr_repcode,mr_mdid,mr_id from applicationdetail left join mrpreplace on ad_mdid=mr_mdid where ad_id="
						+ idx + " and mr_id>0 and NVL(mr_vendor,' ')=' '");
				while (rs.next()) {
					SqlRowList rs2 = queryForRowSet(getreplacesqlstr, rs.getObject("mr_id"), rs.getObject("mr_id"));
					if (rs2.next()) {
						execute(REPLACE_SETVENDOR, new Object[] { rs2.getObject(1), rs2.getObject(2),rs2.getObject("ppd_id"), rs.getObject("mr_id") });
					} else {
						execute("UPDATE mrpreplace SET mr_vendor=null,mr_vendname=null,mr_ppdid=0 WHERE mr_id=" + rs.getObject("mr_id"));
					}
				}
				// 原本更新到MRPReplace表的业务,也加到ApplicationReplace表
				rs = queryForRowSet("select ar_id from  ApplicationReplace  where ar_adid ="
						+ idx + " and ar_id>0 ");
				while (rs.next()) {
					SqlRowList rs2 = queryForRowSet(appreplaceStr,rs.getObject("ar_id"), rs.getObject("ar_id"));
					if (rs2.next()) {
						execute("UPDATE ApplicationReplace SET ar_ifvendrate=0,ar_vendor=?,ar_vendname=?,ar_currency=? WHERE ar_id=?",
								new Object[] { rs2.getObject(1), rs2.getObject(2),rs2.getObject(3), rs.getObject("ar_id") });
					} else {
						execute("UPDATE ApplicationReplace SET ar_vendor=null,ar_vendname=null WHERE ar_id=" + rs.getObject("ar_id"));
					}
				}
			}
		}
	}

	/**
	 * 修改请购单状态
	 */
	@Override
	public void checkAdQty(int adid) {
		Object apid = getFieldDataByCondition("ApplicationDetail", "ad_apid", "ad_id=" + adid);
		int count = getCountByCondition("ApplicationDetail", "ad_apid=" + apid);
		int yCount = getCountByCondition("ApplicationDetail", "ad_apid=" + apid + " AND ad_yqty>=ad_qty AND NVL(ad_yqty,0)>0");
		int nCount = getCountByCondition("ApplicationDetail", "ad_apid=" + apid + " AND NVL(ad_yqty,0)=0");
		String status = "PART2PU";
		if (nCount == count) {
			status = "";
		} else if (yCount == count) {
			status = "TURNPURC";
		}
		execute("UPDATE Application set ap_turnstatuscode=?,ap_turnstatus=? where ap_id=?", status, BaseUtil.getLocalMessage(status), apid);
	}

	/**
	 * 请购单整批抛转
	 */
	@Transactional
	@Override
	public synchronized String[] postApplication(int[] id, String from, String to) {
		String[] log = new String[id.length];
		// 先取当前dataSource下Application的数据
		List<SqlRowList> lists = new ArrayList<SqlRowList>();// Application
		List<SqlRowList> childLists = new ArrayList<SqlRowList>();// ApplicationDetail
		SqlRowList rs = null;
		for (int i : id) {
			rs = queryForRowSet("SELECT * FROM Application where ap_id=" + i);
			lists.add(rs);
			rs = queryForRowSet("SELECT * FROM ApplicationDetail where ad_apid=" + i);
			childLists.add(rs);
		}
		// 再切换dataSource
		SpObserver.putSp(to);
		Map<String, Object> map;
		String source;
		boolean bool;
		String sql;
		List<String> sqls;
		int count = 0;
		JSONObject json;
		Object oldId;
		Object newId;
		Object oldCode;
		try {
			for (SqlRowList list : lists) {
				json = new JSONObject();
				if (list.next()) {
					map = list.getCurrentMap();
					oldId = map.get("AP_ID");
					oldCode = map.get("AP_CODE");
					source = from + "." + oldCode;// 帐套编号.单号
					bool = checkByCondition("Application", "ap_source='" + source + "'");// 判断来源是否已存在
					if (bool) {
						sqls = new ArrayList<String>();
						map.put("AP_SOURCEID", oldId);
						map.put("AP_SOURCE", source);
						newId = getSeqId("APPLICATION_SEQ");
						map.put("AP_ID", newId);// ID和CODE重新生成
						map.put("AP_CODE", sGetMaxNumber("Application", 2));
						sql = SqlUtil.getInsertSqlByFormStore(map, "Application", new String[] {}, new Object[] {});
						sqls.add(sql);
						rs = childLists.get(count);
						while (rs.next()) {
							map = rs.getCurrentMap();
							map.put("AD_SOURCE", source);
							map.put("AD_SOURCEID", oldId);
							map.put("AD_SOURCECODE", oldCode);
							map.put("AD_APID", newId);
							map.put("AD_ID", getSeqId("APPLICATIONDETAIL_SEQ"));
							sql = SqlUtil.getInsertSqlByFormStore(map, "ApplicationDetail", new String[] {}, new Object[] {});
							sqls.add(sql);
						}
						try {
							execute(sqls);
							json.put("id", oldId);
							json.put("success", true);
						} catch (Exception e) {
							json.put("id", oldId);
							json.put("success", false);
							json.put("error", e.getMessage());
						}
					} else {
						json.put("id", oldId);
						json.put("success", false);
						json.put("error", "该单据已经抛转过帐套" + to);
					}
				}
				log[count] = json.toString();
				count++;
			}
		} catch (Exception e) {
			BaseUtil.showError(e.getMessage());
		}
		return log;
	}

	static final String CHECK_YQTY = "SELECT ad_code,ad_detno,ad_qty FROM ApplicationDetail WHERE ad_id=? and ad_qty<?";

	/**
	 * 请购转入采购之前， 1.判断请购状态 2.判断thisqty ≤ qty - yqty
	 */
	@Override
	public void checkAdYqty(List<Map<Object, Object>> datas) {
		Object y = 0;
		Object[] aps = null;
		// datas 按照ad_id分组 
		Map<Object, List<Map<Object, Object>>> groupDatas = BaseUtil.groupMap(datas, "ad_id");
		Set<Object> adIdSet= groupDatas.keySet();
		for(Object ad_id : adIdSet){
			List<Map<Object, Object>> datasNew = groupDatas.get(ad_id);
			int sum_ad_tqty = 0;
			for (Map<Object, Object> d : datasNew) {//计算本次下单数
				sum_ad_tqty += Double.valueOf(String.valueOf(d.get("ad_tqty")));
			}
			aps = getFieldsDataByCondition("ApplicationDetail left join Application on ad_apid=ap_id",
					"ap_code,ad_detno,ap_statuscode,ad_qty,ad_yqty", "ad_id=" + ad_id);
			if (aps != null) {
				if (!aps[2].equals("AUDITED")) {
					BaseUtil.showError("请购单:" + aps[0] + " 未审核通过,无法转采购单!");
				}
				y = getFieldDataByCondition("PurchaseDetail", "sum(nvl(pd_qty,0))", "pd_sourcedetail=" + ad_id);
				y = y == null ? 0 : y;
				if(!baseDao.isDBSetting("Purchase","AllowOut")){
					if (Double.parseDouble(aps[3].toString()) < Double.parseDouble(y.toString()) + sum_ad_tqty) {
						StringBuffer sb = new StringBuffer("本次数量填写超出可转数量,请购单号:").append(aps[0]).append(",行号:").append(aps[1]).append(",请购数:")
								.append(aps[3]).append(",已转采购数:").append(y).append(",本次数:").append(sum_ad_tqty);
						BaseUtil.showError(sb.toString());
					}
				}
			}
		
		}
		
	}
}
