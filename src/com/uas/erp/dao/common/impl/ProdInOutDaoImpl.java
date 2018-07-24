package com.uas.erp.dao.common.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlMap;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ProdInOutDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.scm.ProdInOutService;

@Repository
public class ProdInOutDaoImpl extends BaseDao implements ProdInOutDao {

	@Autowired
	private ProdInOutService prodInOutService;
	// 发货单转发出商品时 根据发货单pi_id 查询发货单主表信息
	static final String NEWGOODSSEND = "SELECT PI_ID,PI_INOUTNO,PI_CLASS,PI_WHCODE,PI_DATE,PI_PAYDATE,PI_RECORDDATE,PI_CURRENCY,PI_RATE,PI_CARDCODE,PI_TITLE,PI_TOTAL,PI_TAXTOTAL,"
			+ "PI_HAVEPAY,PI_BELONGS,PI_INOUTMAN,PI_PAYMENT,PI_TRANSPORT,PI_FROMCODE,PI_TOCODE,PI_RELATIVEPLACE,PI_INVOCODE,PI_PURPOSE,PI_STATUS,PI_PRINTSTATUS,PI_CHECKSTATUS,PI_REMARK,PI_OPERATORCODE,"
			+ "PI_RECORDMAN,PI_SELLERCODE,PI_COP,PI_TOTALUPPER,PI_VOUCHERCODE,PI_WHNAME,PI_PURPOSENAME,PI_RECEIVECODE,PI_RECEIVENAME,PI_EXPRESSCODE,PI_INVOSTATUS,PI_UPDATEMAN,PI_UPDATEDATE,PI_AUDITMAN,"
			+ "PI_AUDITDATE,PI_TYPE,PI_DEPARTMENTCODE,PI_DEPARTMENTNAME,PI_EMCODE,PI_EMNAME,PI_SHR,PI_IDCODE,PI_GROUPCODE,PI_PRINTMAN,PI_SENDCODE,PI_CODE,FIN_CODE,PI_LISTCODE,PI_CARDID,PI_INVOSTATUSCODE,"
			+ "PI_STATUSCODE,PI_PROFITRATE,PI_WAREHOUSEID,PI_DATE1,PI_DATE2,PI_ADDRESS,PI_CGYCODE,PI_CGY,PI_OPERATCENTER,PI_PLANPAYDATE,PI_REFNO,PI_PRINTSTATUSCODE,PI_BILLSTATUS,PI_BILLSTATUSCODE,PI_ARCODE,"
			+ "PI_ARNAME,PI_SOURCECODE FROM PRODINOUT WHERE PI_ID=?";
	// 发货单转发出商品时 根据发货单pd_id 查询发货单从表的信息
	static final String NEWGOODSSENDDETAIL = "SELECT PD_ID,PD_INOUTNO,PD_PICLASS,PD_PDNO,PD_ORDERCODE,PD_ORDERDETNO,PD_DESCRIPTION,PD_PLANCODE,PD_BATCHCODE,PD_PRODCODE,PD_PRODMADEDATE,PD_OUTQTY,PD_INQTY,(NVL(PD_OUTQTY,0))-(NVL(PD_INQTY,0))-(NVL(PD_INVOQTY,0))-(NVL(PD_TURNGSQTY,0)) SHOWQTY,"
			+ "PD_NOTINQTY,PD_NOTOUTQTY,PD_ORDERPRICE,PD_SENDPRICE,PD_PRICE,PD_AVPRICE,PD_SENDPRICE,  PD_TAXTOTAL,PD_TOTAL,PD_REMARK,PD_LOCATION,PD_PURCQTY,PD_DISCOUNT,PD_SELLER,PD_ORDERQTY,PD_SDID,PD_CUSTPRODCODE,"
			+ "PD_SELLERCODE,PD_POCODE,PD_WHCODE,PD_WHNAME,PD_BARCODE,PD_NETPRICE,PD_LENGTH,PD_WIDTH,PD_BEIPIN,PD_INVOQTY,PD_THISVOQTY,PD_BSQTY,PD_CARTONS,PD_OUTERBOXGW,PD_OUTERBOXNW,PD_MMID,PD_BEIPININQTY,"
			+ "PD_BEIPINOUTQTY,PD_WCCODE,PD_REPLY,PD_REPLYDATE,PD_VENDORREPLY,PD_VENDORREPLYDATE,PD_TEXTBOX,PD_NXLH,PD_DEPARTMENTCODE,PD_DEPARTMENTNAME,PD_DISCOUNTAMOUNT,PD_DISCOUNTPRICE,PD_FLOWCODE,PD_ACCOUNTSTATUS,"
			+ "PD_TAXAMOUNT,PD_TAXPRICE,PD_PIID,PD_ORDERID,PD_WAREHOUSEID,PD_ROUGHPRICE,PD_PROFITRATE,PD_INVOCODE,PD_VACODE,PD_SNID,PD_STATUS,PD_AUDITSTATUS,PD_ARBILLQTY,PD_MACODE,PD_PRODJITYPE,PD_THISVOPRICE,"
			+ "PD_THISTAXTOTAL,PD_SNCODE,PD_TOTALOUTQTY,PD_DEPARTMENT,PD_RETURNQTY,PD_INVOTOTAL,PD_MONTHINVOQTY,PD_PETOTAL,PD_INWHCODE,PD_INWHNAME,PD_ACCOUNTSTATUSCODE,PD_NETTOTAL,PD_COLOR,PD_BOMCOST,PD_QCID,"
			+ "PD_NOTINQTY1,PD_OUTQTY1,PD_SHOWQTY,PD_ORDERTOTAL,PD_SHOWPRICE,PD_SURPLUSAMOUNT,PD_TURNGSQTY FROM PRODIODETAIL WHERE PD_ID=?";

	// 发货单转发出商品时 根据发货单主表信息 对 发出商品 主表 插入数据
	static final String INSERTNEWGOODSSEND = "INSERT INTO GOODSSEND (GS_ID,GS_CODE,GS_CLASS,GS_CUSTID,GS_CUSTCODE,GS_CUSTNAME,GS_SELLERID,GS_SELLER,GS_DATE,GS_PAYDATE,GS_CURRENCY,GS_RATE,GS_AMOUNT,GS_PAYMENTS,"
			+ "GS_VOUCHERCODE,GS_RECORDERID,GS_STATUS,GS_INVOSTATUS,GS_STATUSCODE,GS_INVOSTATUSCODE,GS_COP,GS_INDATE,GS_REMARK,GS_RECORDER,GS_INOUTNO,GS_AUDITSTATUS,GS_AUDITSTATUSCODE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	// 发货单转发出商品时 根据发货单从表信息 对 发出商品 从表 插入数据
	static final String INSERTNEWGOODSSENDDETAIL = "INSERT INTO GOODSSENDDETAIL (GSD_ID,GSD_GSID,GSD_CODE,GSD_DETNO,GSD_PRODCODE,GSD_ORDERCODE,GSD_ORDERID,GSD_ORDERDETNO,GSD_QTY,GSD_ORDERPRICE,GSD_SENDPRICE,GSD_COSTPRICE,GSD_AMOUNT,"
			+ "GSD_TAXRATE,GSD_TAXAMOUNT,GSD_STATUS,GSD_STATUSCODE,GSD_POCODE,GSD_CUSTPRODCODE,GSD_DISCOUNT,GSD_REMARK,GSD_PDID,GSD_INVOQTY,GSD_PIID,GSD_PICODE,GSD_SHOWINVOQTY,GSD_SHOWINVOTOTAL) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	static final String TURNARBILL = "SELECT pi_inoutno,pi_class,pi_cardid,pi_cardcode,pi_title,pi_paydate,pi_currency,pi_rate,pi_total,pi_havepay,pi_payment,pi_cop"
			+ ",pi_transport,pi_totalupper,pi_sellercode,pi_vouchercode FROM prodinout WHERE pi_id=?";
	static final String INSERTARBILL = "INSERT INTO arbill(ab_id,ab_code,ab_sourceid,ab_source,ab_class,ab_custid,ab_custcode,ab_custname,ab_paydate,ab_currency"
			+ ",ab_rate,ab_aramount,ab_payamount,ab_payments,ab_cop,ab_shipment,ab_totalupper,ab_date,ab_recorderid,ab_status,ab_indate,ab_statuscode,ab_yearmonth"
			+ ",ab_sellerid,ab_seller,ab_vouchercode,ab_voyearmonth,ab_recorder) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERTBASEARBILL = "INSERT INTO arbill(ab_id,ab_code,ab_status,ab_statuscode"
			+ ",ab_recorderid,ab_indate,ab_class,ab_recorder) VALUES (?,?,?,?,?,?,?,?)";
	static final String INSERTARBILLWITHCUSTOMER = "INSERT INTO arbill(ab_id,ab_code,ab_auditstatus,ab_auditstatuscode"
			+ ",ab_recorderid,ab_indate,ab_custid, ab_custcode,ab_class,ab_sellerid,ab_sellercode,ab_seller,ab_currency,ab_rate,ab_payments"
			+ ",ab_printstatuscode,ab_printstatus,ab_paystatuscode,ab_paystatus,ab_statuscode,ab_status,ab_recorder,ab_paymentcode"
			+ ",ab_date,ab_yearmonth,ab_sendtype,ab_tradecode,ab_tradename,ab_refno,ab_custname,ab_differ,ab_taxamount) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERTARBILLWITHCUSTOMER_HAVEPI = "INSERT INTO arbill(ab_id,ab_code,ab_auditstatus,ab_auditstatuscode"
			+ ",ab_recorderid,ab_indate,ab_custid, ab_custcode,ab_class,ab_sellerid,ab_sellercode,ab_seller,ab_currency,ab_rate,ab_payments"// 31
			+ ",ab_printstatuscode,ab_printstatus,ab_paystatuscode,ab_paystatus,ab_statuscode,ab_status,ab_recorder,ab_paymentcode"
			+ ",ab_date,ab_yearmonth,ab_sendtype,ab_tradecode,ab_tradename,ab_refno,ab_custname,ab_picode,ab_differ,ab_taxamount) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERTAPBILLWITHVENDOR = "INSERT INTO apbill(ab_id,ab_code,ab_auditstatus,ab_auditstatuscode"
			+ ",ab_recorderid,ab_indate,ab_vendid, ab_vendcode,ab_class,ab_buyerid,ab_buyer,ab_currency,ab_rate,"
			+ "ab_payments,ab_printstatuscode,ab_printstatus,ab_paystatuscode,ab_paystatus,ab_statuscode,ab_status,ab_recorder,"
			+ "ab_paymentcode,ab_date,ab_yearmonth, ab_refno, ab_remark, ab_vendname) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	static final String TURNARBILLDETAIL = "SELECT pd_id,pd_inoutno,pd_prodcode, nvl(pd_inqty,0)-nvl(pd_outqty,0),nvl(pd_orderprice,0) pd_orderprice,nvl(pd_total,0) pd_total,nvl(pd_rate,0) pd_rate,nvl(pd_taxamount,0) pd_taxamount,"
			+ "nvl(pd_invototal,0) pd_invototal,pd_description,pd_pocode,nvl(pd_orderqty,0) pd_orderqty,pd_pdno,pd_custprodcode,pd_cartons,pd_outerboxnw,pd_outerboxgw,nvl(pd_netprice,0) pd_netprice, "
			+ "pd_discount,pi_id,pi_inoutno,pi_date,pi_currency,pd_thisvoprice,nvl(pd_invoqty,0) pd_invoqty,pd_ordercode,pd_batchcode"
			+ " FROM prodiodetail left join prodinout on pd_piid=pi_id WHERE pd_piid=?";
	static final String INSERTARBILLDETAIL = "INSERT INTO arbilldetail(abd_id,abd_abid,abd_detno,abd_prodid,abd_pdid,abd_pdinoutno,abd_prodcode,abd_thisvoqty,abd_price"
			+ ",abd_amount,abd_taxrate,abd_taxamount,abd_aramount,abd_description,abd_pocode,abd_orderdetno,abd_custprodcode,abd_cartons,abd_outerboxnw"
			+ ",abd_outerboxgw,abd_costprice,abd_discount,abd_statuscode,abd_status,abd_ordercode,abd_code,abd_date,abd_currency,abd_qty,abd_thisvoprice,abd_sourcetype,abd_sourcekind,abd_sourcedetailid,abd_pidetno)"
			+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERTARBILLDETAIL_HAVEPI = "INSERT INTO arbilldetail(abd_id,abd_abid,abd_detno,abd_prodid,abd_pdid,abd_pdinoutno,abd_prodcode,abd_thisvoqty,abd_price"
			+ ",abd_amount,abd_taxrate,abd_taxamount,abd_aramount,abd_description,abd_pocode,abd_orderdetno,abd_custprodcode,abd_cartons,abd_outerboxnw"
			+ ",abd_outerboxgw,abd_costprice,abd_discount,abd_statuscode,abd_status,abd_ordercode,abd_code,abd_date,abd_currency,abd_qty,abd_thisvoprice,abd_sourcetype,abd_sourcekind,abd_sourcedetailid,abd_pidetno,abd_picode)"
			+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String GETARBYSOURCE = "select ab_code from arbill where ar_source=(select pi_inoutno from prodinout left join prodiodetail on pi_id=pd_piid where pd_id=?)";

	static final String TURNBASEARBILLDETAIL = "SELECT pd_id,pd_inoutno,pd_prodcode, nvl(pd_outqty,0)-nvl(pd_inqty,0) show,ABS(nvl(pd_sendprice,0)) showprice,nvl(pd_total,0) pd_total,nvl(pd_taxrate,0) pd_taxrate,nvl(pd_taxamount,0) pd_taxamount,"
			+ "nvl(pd_invototal,0) pd_invototal,pd_description,pd_pocode,nvl(pd_orderqty,0) pd_orderqty,pd_pdno,pd_custprodcode,pd_cartons,pd_outerboxnw,pd_outerboxgw,nvl(pd_netprice,0) pd_netprice,"
			+ "pd_discount,pi_id,pi_inoutno,pi_date,pi_currency,nvl(pd_thisvoprice,0) pd_thisvoprice,nvl(pd_invoqty,0) pd_invoqty,pd_ordercode,pd_batchcode,pd_orderdetno,pd_piclass,pd_price"
			+ " FROM prodiodetail left join prodinout on pd_piid=pi_id WHERE pd_id=?";

	static final String TURNBASEARBILLDETAIL_HAVEPI = "SELECT pd_id,pd_inoutno,pd_prodcode, nvl(pd_outqty,0)-nvl(pd_inqty,0) show,ABS(nvl(pd_sendprice,0)) showprice,nvl(pd_total,0) pd_total,nvl(pd_taxrate,0) pd_taxrate,nvl(pd_taxamount,0) pd_taxamount,"
			+ "nvl(pd_invototal,0) pd_invototal,pd_description,pd_pocode,nvl(pd_orderqty,0) pd_orderqty,pd_pdno,pd_custprodcode,pd_cartons,pd_outerboxnw,pd_outerboxgw,nvl(pd_netprice,0) pd_netprice,"
			+ "pd_discount,pi_id,pi_inoutno,pi_date,pi_currency,nvl(pd_thisvoprice,0) pd_thisvoprice,nvl(pd_invoqty,0) pd_invoqty,pd_ordercode,pd_batchcode,pd_orderdetno,pd_piclass,pd_price,pi_shr"
			+ " FROM prodiodetail left join prodinout on pd_piid=pi_id WHERE pd_id=?";

	static final String TURNBASEARBILLDETAIL_GOODSSEND = "SELECT gsd_pdid pd_id,gsd_picode pd_inoutno,gsd_prodcode pd_prodcode, gsd_qty show,gsd_sendprice showprice,nvl(gsd_taxrate,0) pd_taxrate,nvl(gsd_taxamount,0) pd_taxamount,"
			+ "nvl(gsd_invototal,0) pd_invototal,'' pd_description,0 pd_discount,0 pd_invoqty,"
			+ "gsd_piid pi_id,gsd_picode pi_inoutno,gs_date pi_date,gs_currency pi_currency,gsd_pocode pd_pocode,gsd_ordercode pd_ordercode,gsd_orderdetno pd_orderdetno,'发出商品' pd_piclass,gsd_costprice pd_price"
			+ " FROM goodssend left join goodssenddetail on gs_id=gsd_gsid WHERE gsd_id=?";

	static final String TURNBASEARBILLDETAIL_GOODSSEND_HAVEPI = "SELECT gsd_pdid pd_id,gsd_picode pd_inoutno,gsd_prodcode pd_prodcode, gsd_qty show,gsd_sendprice showprice,nvl(gsd_taxrate,0) pd_taxrate,nvl(gsd_taxamount,0) pd_taxamount,"
			+ "nvl(gsd_invototal,0) pd_invototal,'' pd_description,0 pd_discount,0 pd_invoqty,"
			+ "gsd_piid pi_id,gsd_picode pi_inoutno,gs_date pi_date,gs_currency pi_currency,gsd_ordercode pd_ordercode,gsd_pocode pd_pocode,gsd_orderdetno pd_orderdetno,'发出商品' pd_piclass,gsd_costprice pd_price,gs_picode pi_shr"
			+ " FROM goodssend left join goodssenddetail on gs_id=gsd_gsid WHERE gsd_id=?";

	static final String TURNBASEAPBILLDETAIL = "SELECT pd_id,pd_inoutno,pd_prodcode, nvl(pd_inqty,0)-nvl(pd_outqty,0) show,round(nvl(pd_orderprice,0),8) showprice,nvl(pd_total,0) pd_total,nvl(pd_taxrate,0) pd_taxrate,nvl(pd_taxamount,0) pd_taxamount,"
			+ "nvl(pd_invototal,0) pd_invototal,pd_description,pd_pocode,nvl(pd_orderqty,0) pd_orderqty,pd_pdno,pd_custprodcode,pd_cartons,pd_outerboxnw,pd_outerboxgw,nvl(pd_netprice,0) pd_netprice, "
			+ "pd_discount,pi_id,pi_inoutno,pi_date,pi_currency,nvl(pd_thisvoprice,0) pd_thisvoprice,nvl(pd_invoqty,0) pd_invoqty,pd_ordercode,pd_orderdetno,pd_piclass,pd_price"
			+ " FROM prodiodetail left join prodinout on pd_piid=pi_id WHERE pd_id=?";

	static final String TURNBASEAPBILLDETAIL_ESTIMATE = "SELECT esd_pdid pd_id,esd_picode pd_inoutno,esd_prodcode pd_prodcode, esd_qty show,round(esd_orderprice,8) showprice,nvl(esd_taxrate,0) pd_taxrate,nvl(esd_taxamount,0) pd_taxamount,"
			+ "nvl(esd_invototal,0) pd_invototal,'' pd_description,0 pd_discount,0 pd_invoqty,"
			+ "esd_piid pi_id,esd_picode pi_inoutno,es_date pi_date,es_currency pi_currency,esd_ordercode pd_ordercode,esd_orderdetno pd_orderdetno,'应付暂估' pd_piclass,esd_costprice pd_price"
			+ " FROM estimate left join estimatedetail on es_id=esd_esid WHERE esd_id=?";

	static final String INSERTAPBILLDETAIL = "insert into apbilldetail (abd_id,abd_abid,abd_detno,abd_prodid,abd_pdid,abd_pdinoutno,abd_prodcode,abd_thisvoqty,abd_price,abd_amount,"
			+ " abd_taxrate,abd_apamount,abd_description,abd_orderdetno,abd_costprice,abd_discount,abd_statuscode,abd_status,abd_date,abd_ordercode,abd_code,abd_currency,"
			+ " abd_qty,abd_thisvoprice,abd_sourcetype,abd_sourcekind,abd_sourcedetailid,abd_pidetno) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	static final String TURNPRODIO = "SELECT pi_inoutno,pi_cop,pi_type,pi_departmentname,pi_departmentcode"
			+ ",pi_cardid,pi_cardcode,pi_title,pi_emcode,pi_emname from ProdInOut WHERE pd_piid=?";
	static final String INSERTPRODIO = "insert into apbilldetail (pi_id,pi_inoutno,pi_class,pi_invostatus,pi_invostatuscode,pi_updatedate,pi_updateman"
			+ ",pi_cop,pi_recordman,pi_fromcode,pi_type,pi_departmentname,pi_departmentcode,pi_cardid,pi_cardcode,pi_title"
			+ ",pi_emcode,pi_emname,pi_recorddate,pi_date) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	@Override
	public String checkOutqty(int pi_id) {
		String sql = "select  * from (SELECT pd_ordercode,pd_orderdetno,sum(pd_outqty) as pd_outqty,max(pd_pdno)as pd_pdno "
				+ " FROM  ProdIODetail where  pd_piid=" + pi_id + "' group by pd_ordercode,pd_orderdetno )A "
				+ " left join makematerial on mm_code=pd_ordercode and mm_detno=pd_orderdetno "
				+ " where nvl(mm_addqty,0)+pd_outqty>nvl(mm_scrapqty,0)";
		SqlRowList set = queryForRowSet(sql);
		StringBuffer sb = new StringBuffer();
		while (set.next()) {
			sb.append("{pd_pdno:" + set.getInt("pd_pdno"));
			sb.append(",mm_scrapqty:\"" + set.getInt("mm_scrapqty"));
			sb.append(",mm_addqty:\"" + set.getInt("mm_addqty"));
			sb.append(",pd_outqty:\"" + set.getInt("pd_outqty"));
			sb.append("\"},");
		}
		return sb.length() > 0 ? sb.toString().substring(0, sb.lastIndexOf(",") - 1) : null;
	}

	@Override
	@Transactional
	public int turnARBill(int id) {
		try {
			Employee employee = SystemSession.getUser();
			SqlRowList rs = queryForRowSet(TURNARBILL, new Object[] { id });
			int abid = 0;
			if (rs.next()) {
				abid = getSeqId("ARBILL_SEQ");
				String code = sGetMaxNumber("ARBill", 2);
				String sourcecode = rs.getString(1);
				int yearmonth = DateUtil.getYearmonth();
				Object[] seller = getFieldsDataByCondition("employee", new String[] { "em_id", "em_name" }, "em_code='" + rs.getObject(15)
						+ "'");
				Object voucher = getFieldDataByCondition("voucher", "vo_yearmonth", "vo_code='" + rs.getObject(16) + "'");
				boolean bool = execute(
						INSERTARBILL,
						new Object[] { abid, code, id, sourcecode, rs.getObject(2), rs.getObject(3), rs.getObject(4), rs.getObject(5),
								rs.getObject(6), rs.getObject(7), rs.getObject(8), rs.getObject(9), rs.getObject(10), rs.getObject(11),
								rs.getObject(12), rs.getObject(13), rs.getObject(14),
								Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), employee.getEm_id(),
								BaseUtil.getLocalMessage("ENTERING"), Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)),
								"ENTERING", yearmonth, seller[0], seller[1], rs.getObject(16), voucher, employee.getEm_name() });
				if (bool) {
					rs = queryForRowSet(TURNARBILLDETAIL, new Object[] { id });
					int count = 1;
					while (rs.next()) {
						int qty = rs.getInt(6) - rs.getInt(23);// 实际应转入值
						if (qty > 0) {
							int abdid = getSeqId("ARBILLDETAIL_SEQ");
							Double price = Double.parseDouble(rs.getObject("pd_price").toString());
							execute(INSERTARBILLDETAIL,
									new Object[] { abdid, abid, count++, rs.getObject("pd_id"), rs.getObject("pd_inoutno"),
											rs.getObject("pd_prodcode"), rs.getObject("show"), price, qty * price, rs.getObject("pd_rate"),
											rs.getObject("pd_taxamount"), rs.getObject("pd_invototal"), rs.getObject("pd_description"),
											rs.getObject("pd_pocode"), rs.getObject("pd_orderdetno"), rs.getObject("pd_custprodcode"),
											rs.getObject("pd_cartons"), rs.getObject("pd_outerboxnw"), rs.getObject("pd_outerboxgw"),
											rs.getObject("pd_netprice"), rs.getObject("pd_discount"), "ENTERING", 0,
											rs.getObject("pd_ordercode"), code,
											Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), rs.getObject("pi_currency"),
											qty, price, rs.getObject("pd_invoqty"), rs.getObject("pd_piclass") });
						}
					}
				}
			}
			return abid;
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
			return 0;
		}
	}

	@Override
	public String newARBill(String type) {
		Employee employee = SystemSession.getUser();
		int abid = getSeqId("ARBILL_SEQ");
		String code = sGetMaxNumber("ARBill", 2);
		boolean bool = execute(
				INSERTBASEARBILL,
				new Object[] { abid, code, BaseUtil.getLocalMessage("ENTERING"), "ENTERING", employee.getEm_id(),
						Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), type });
		if (bool) {
			return code;
		}
		return null;
	}

	@Override
	public String getARCodeBySourceCode(int id) {
		SqlRowList rs = queryForRowSet(GETARBYSOURCE, new Object[] { id });
		if (rs.next()) {
			return rs.getString(1);
		}
		return null;
	}

	/*
	 * 出入库单 发出商品开票 (non-Javadoc)
	 * 
	 * @see
	 * com.uas.erp.dao.common.ProdInOutDao#toAppointedARBill(java.lang.String,
	 * int, double, java.lang.String, com.uas.erp.model.Employee,
	 * java.lang.Double, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public void toAppointedARBill(String ab_code, int pd_id, double qty, Double price, String sourcekind, String detailid, Object ifbypi) {
		// DecimalFormat 所加载的格式 用于修改数字的format
		DecimalFormat df = new DecimalFormat("#.00");
		Object abid = getFieldDataByCondition("ARBill", "ab_id", "ab_code='" + ab_code + "'");
		SqlRowList rs = null;
		if ("GOODSSEND".equals(sourcekind)) {
			rs = queryForRowSet(ifbypi == null ? TURNBASEARBILLDETAIL_GOODSSEND : TURNBASEARBILLDETAIL_GOODSSEND_HAVEPI,
					new Object[] { detailid });
		} else {
			rs = queryForRowSet(ifbypi == null ? TURNBASEARBILLDETAIL : TURNBASEARBILLDETAIL_HAVEPI, new Object[] { pd_id });
		}
		BigDecimal bqty = new BigDecimal(Double.toString(qty));
		BigDecimal bprice = new BigDecimal(price.toString());

		bqty = bqty.setScale(3, BigDecimal.ROUND_HALF_UP);
		bprice = bprice.setScale(9, BigDecimal.ROUND_HALF_UP);
		BigDecimal tot = bqty.multiply(bprice).setScale(2, BigDecimal.ROUND_HALF_UP);
		String total = tot.toString();
		if (rs.next()) {
			double allqty = 0;
			if ("GOODSSEND".equals(sourcekind)) {
				Object selqty = getFieldDataByCondition("GoodsSendDetail", "gsd_qty", "gsd_id=" + detailid);
				allqty = selqty == null ? 0 : Double.parseDouble(selqty.toString());
			} else {
				allqty = Double.parseDouble(rs.getObject("show").toString());
			}
			int abdid = getSeqId("ARBILLDETAIL_SEQ");
			Object count = getFieldDataByCondition("ARBillDetail", "max(abd_detno)", "abd_code='" + ab_code + "'");
			count = count == null ? 0 : count;
			int detno = Integer.parseInt(count.toString());
			Double sendprice = rs.getGeneralDouble("showprice");
			if (ifbypi == null) {
				execute(INSERTARBILLDETAIL,
						new Object[] { abdid, abid, ++detno, rs.getObject("pd_id"), rs.getObject("pd_id"), rs.getObject("pd_inoutno"),
								rs.getObject("pd_prodcode"), allqty, sendprice,
								df.format(Double.parseDouble(rs.getObject("show").toString()) * sendprice), rs.getObject("pd_taxrate"),
								rs.getObject("pd_taxamount"), total, rs.getObject("pd_description"), rs.getObject("pd_pocode"),
								rs.getObject("pd_orderdetno"), rs.getObject("pd_custprodcode"), rs.getObject("pd_cartons"),
								rs.getObject("pd_outerboxnw"), rs.getObject("pd_outerboxgw"), rs.getObject("pd_price"),
								rs.getObject("pd_discount"), "ENTERING", 0, rs.getObject("pd_ordercode"), ab_code,
								rs.getTimestamp("pi_date"), rs.getObject("pi_currency"), qty, price, rs.getObject("pd_piclass"),
								sourcekind, detailid, rs.getObject("pd_pdno") });
			} else {
				execute(INSERTARBILLDETAIL_HAVEPI,
						new Object[] { abdid, abid, ++detno, rs.getObject("pd_id"), rs.getObject("pd_id"), rs.getObject("pd_inoutno"),
								rs.getObject("pd_prodcode"), allqty, sendprice,
								df.format(Double.parseDouble(rs.getObject("show").toString()) * sendprice), rs.getObject("pd_taxrate"),
								rs.getObject("pd_taxamount"), total, rs.getObject("pd_description"), rs.getObject("pd_pocode"),
								rs.getObject("pd_orderdetno"), rs.getObject("pd_custprodcode"), rs.getObject("pd_cartons"),
								rs.getObject("pd_outerboxnw"), rs.getObject("pd_outerboxgw"), rs.getObject("pd_price"),
								rs.getObject("pd_discount"), "ENTERING", 0, rs.getObject("pd_ordercode"), ab_code,
								rs.getTimestamp("pi_date"), rs.getObject("pi_currency"), qty, price, rs.getObject("pd_piclass"),
								sourcekind, detailid, rs.getObject("pd_pdno"), rs.getObject("pi_shr") });
			}

		}
	}

	/*
	 * @Override public void toAppointedARBill(String ab_code, int pd_id, double
	 * qty, Double price, String sourcekind, String detailid,Object ifbypi) {
	 * DecimalFormat df = new DecimalFormat("#.00"); Object abid =
	 * getFieldDataByCondition("ARBill", "ab_id", "ab_code='" + ab_code + "'");
	 * SqlRowList rs = null; if ("GOODSSEND".equals(sourcekind)) { rs =
	 * queryForRowSet(ifbypi == null ? TURNBASEARBILLDETAIL_GOODSSEND :
	 * TURNBASEARBILLDETAIL_GOODSSEND_HAVEPI, new Object[] { detailid }); } else
	 * { rs = queryForRowSet(ifbypi == null ? TURNBASEARBILLDETAIL :
	 * TURNBASEARBILLDETAIL_HAVEPI, new Object[] { pd_id }); } Double total =
	 * qty * price; if (rs.next()) { double allqty = 0; if
	 * ("GOODSSEND".equals(sourcekind)) { Object selqty =
	 * getFieldDataByCondition("GoodsSendDetail", "gsd_qty", "gsd_id=" +
	 * detailid); allqty = selqty == null ? 0 :
	 * Double.parseDouble(selqty.toString()); } else { allqty =
	 * Double.parseDouble(rs.getObject("show").toString()); } int abdid =
	 * getSeqId("ARBILLDETAIL_SEQ"); Object count =
	 * getFieldDataByCondition("ARBillDetail", "max(abd_detno)", "abd_code='" +
	 * ab_code + "'"); count = count == null ? 0 : count; int detno =
	 * Integer.parseInt(count.toString()); Double sendprice =
	 * rs.getGeneralDouble("showprice"); if(ifbypi == null){ Map<String,Object>
	 * m = new HashMap<String, Object>(); m.put("abdid", abdid); m.put("abid",
	 * abid); m.put("detno", ++detno); m.put("allqty", allqty); m.put("total",
	 * df.format(total)); // m.put("statuscode", "ENTERING"); // m.put("status",
	 * 0); m.put("ab_code", ab_code); m.put("qty", qty); m.put("price", price);
	 * m.put("sourcekind", sourcekind); m.put("detailid", detailid);
	 * 
	 * turnBill(m, "ProdInOut!ToARBill!Deal!ars_turn_Arbill", pd_id);
	 * execute(INSERTARBILLDETAIL, new Object[] { abdid, abid, ++detno,
	 * rs.getObject("pd_id"), rs.getObject("pd_id"), rs.getObject("pd_inoutno"),
	 * rs.getObject("pd_prodcode"), allqty, sendprice,
	 * df.format(Double.parseDouble(rs.getObject("show").toString()) *
	 * sendprice), rs.getObject("pd_taxrate"), rs.getObject("pd_taxamount"),
	 * df.format(total), rs.getObject("pd_description"),
	 * rs.getObject("pd_pocode"), rs.getObject("pd_orderdetno"),
	 * rs.getObject("pd_custprodcode"), rs.getObject("pd_cartons"),
	 * rs.getObject("pd_outerboxnw"), rs.getObject("pd_outerboxgw"),
	 * rs.getObject("pd_price"), rs.getObject("pd_discount"), "ENTERING", 0,
	 * rs.getObject("pd_ordercode"), ab_code, rs.getTimestamp("pi_date"),
	 * rs.getObject("pi_currency"), qty, price, rs.getObject("pd_piclass"),
	 * sourcekind, detailid, rs.getObject("pd_pdno") }); } else {
	 * execute(INSERTARBILLDETAIL_HAVEPI, new Object[] { abdid, abid, ++detno,
	 * rs.getObject("pd_id"), rs.getObject("pd_id"), rs.getObject("pd_inoutno"),
	 * rs.getObject("pd_prodcode"), allqty, sendprice,
	 * df.format(Double.parseDouble(rs.getObject("show").toString()) *
	 * sendprice), rs.getObject("pd_taxrate"), rs.getObject("pd_taxamount"),
	 * df.format(total), rs.getObject("pd_description"),
	 * rs.getObject("pd_pocode"), rs.getObject("pd_orderdetno"),
	 * rs.getObject("pd_custprodcode"), rs.getObject("pd_cartons"),
	 * rs.getObject("pd_outerboxnw"), rs.getObject("pd_outerboxgw"),
	 * rs.getObject("pd_price"), rs.getObject("pd_discount"), "ENTERING", 0,
	 * rs.getObject("pd_ordercode"), ab_code, rs.getTimestamp("pi_date"),
	 * rs.getObject("pi_currency"), qty, price, rs.getObject("pd_piclass"),
	 * sourcekind, detailid, rs.getObject("pd_pdno"), rs.getObject("pi_shr")});
	 * }
	 * 
	 * 
	 * } }
	 */

	/**
	 * 应付批量开票 应付发票明细行生成
	 */
	@Override
	public void toAppointedAPBill(String ab_code, int pd_id, double qty, Double price, String sourcekind, String detailid, Object date) {
		Object abid = getFieldDataByCondition("APBill", "ab_id", "ab_code='" + ab_code + "'");
		SqlRowList rs = null;
		if ("ESTIMATE".equals(sourcekind)) {
			rs = queryForRowSet(TURNBASEAPBILLDETAIL_ESTIMATE, new Object[] { detailid });
		} else {
			rs = queryForRowSet(TURNBASEAPBILLDETAIL, new Object[] { pd_id });
		}
		BigDecimal bqty = new BigDecimal(Double.toString(qty));
		BigDecimal bprice = new BigDecimal(price.toString());

		bqty = bqty.setScale(3, BigDecimal.ROUND_HALF_UP);
		bprice = bprice.setScale(9, BigDecimal.ROUND_HALF_UP);

		BigDecimal tot = bqty.multiply(bprice).setScale(2, BigDecimal.ROUND_HALF_UP);
		String total = tot.toString();
		if (rs.next()) {
			double allqty = 0;
			if ("ESTIMATE".equals(sourcekind)) {
				Object selqty = getFieldDataByCondition("EstimateDetail", "esd_qty", "esd_id='" + detailid + "'");
				allqty = selqty == null ? 0 : Double.parseDouble(selqty.toString());
			} else {
				allqty = Double.parseDouble(rs.getObject("show").toString());
			}
			int abdid = getSeqId("APBILLDETAIL_SEQ");
			Object count = getFieldDataByCondition("APBillDetail", "max(abd_detno)", "abd_code='" + ab_code + "'");
			Double orderprice = rs.getGeneralDouble("showprice");
			Double rate = Double.parseDouble(rs.getObject("pd_taxrate").toString());
			count = count == null ? 0 : count;
			price = NumberUtil.formatDouble(price, 8);
			int detno = Integer.parseInt(count.toString());
			execute(INSERTAPBILLDETAIL,
					new Object[] { abdid, abid, ++detno, rs.getObject("pd_id"), rs.getObject("pd_id"), rs.getObject("pd_inoutno"),
							rs.getString("pd_prodcode"), allqty, orderprice,
							NumberUtil.formatDouble(Double.parseDouble(rs.getObject("show").toString()) * orderprice, 2), rate, total,
							rs.getObject("pd_description"), rs.getObject("pd_orderdetno"), rs.getObject("pd_price"),
							rs.getObject("pd_discount"), "ENTERING", 0, rs.getTimestamp("pi_date"), rs.getObject("pd_ordercode"), ab_code,
							rs.getObject("pi_currency"), qty, price, rs.getObject("pd_piclass"), sourcekind, detailid,
							rs.getObject("pd_pdno") });
		}

	}

	@Override
	public String newARBillWithCustomer(Object date, String sendtype, Object tradecode, Object tradename, Object refno,
			Map<String, Object> config, Object ifbypi, Object picode, Object differ, Object taxamount) {
		Employee employee = SystemSession.getUser();
		int abid = getSeqId("ARBILL_SEQ");
		String code = sGetMaxNumber("ARBill", 2);
		String dateStr = DateUtil.currentDateString(Constant.YMD_HMS);
		int yearmonth = Integer.parseInt(DateUtil.currentDateString("yyyyMM"));
		if (StringUtil.hasText(date)) {
			dateStr = date.toString().replace("T", " ");
			yearmonth = Integer.parseInt(date.toString().replace("T", "").replace("-", "").substring(0, 6));
		}
		boolean bool = ifbypi == null ? execute(
				INSERTARBILLWITHCUSTOMER,
				new Object[] { abid, code, BaseUtil.getLocalMessage("ENTERING"), "ENTERING", employee.getEm_id(),
						Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), config.get("ab_custid"),
						config.get("ab_custcode"), "应收发票", config.get("ab_sellerid"), config.get("ab_sellercode"), config.get("ab_seller"),
						config.get("ab_currency"), config.get("ab_rate"), config.get("ab_payments"), "UNPRINT",
						BaseUtil.getLocalMessage("UNPRINT"), "UNCOLLECT", BaseUtil.getLocalMessage("UNCOLLECT"), "UNPOST",
						BaseUtil.getLocalMessage("UNPOST"), employee.getEm_name(), config.get("ab_paymentcode"),
						Timestamp.valueOf(dateStr), yearmonth, sendtype, tradecode, tradename, refno, config.get("ab_custname"), differ,
						taxamount }) : execute(
				INSERTARBILLWITHCUSTOMER_HAVEPI,
				new Object[] { abid, code, BaseUtil.getLocalMessage("ENTERING"), "ENTERING", employee.getEm_id(),
						Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), config.get("ab_custid"),
						config.get("ab_custcode"), "应收发票", config.get("ab_sellerid"), config.get("ab_sellercode"), config.get("ab_seller"),
						config.get("ab_currency"), config.get("ab_rate"), config.get("ab_payments"), "UNPRINT",
						BaseUtil.getLocalMessage("UNPRINT"), "UNCOLLECT", BaseUtil.getLocalMessage("UNCOLLECT"), "UNPOST",
						BaseUtil.getLocalMessage("UNPOST"), employee.getEm_name(), config.get("ab_paymentcode"),
						Timestamp.valueOf(dateStr), yearmonth, sendtype, tradecode, tradename, refno, config.get("ab_custname"), picode,
						differ, taxamount });

		if (bool) {
			Object cu_duedays = getFieldDataByCondition("customer", "cu_duedays", "cu_code='" + config.get("ab_custcode") + "'");
			if ("".equals(cu_duedays) || "null".equals(cu_duedays)) {
				cu_duedays = 0;
			}
			System.out.println(cu_duedays + ":" + config.get("ab_paymentcode") + ":"
					+ Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)));
			String res = callProcedure("SP_GETPAYDATE", new Object[] { Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)),
					config.get("ab_paymentcode"), cu_duedays });
			updateByCondition("arbill", "ab_paydate='" + res + "'", " ab_code='" + code + "'");
			return code;
		}
		return null;
	}

	@Override
	public String newAPBillWithVendor(int veid, String vecode, String vename, String currency, Double rate, Object date, Object refno,
			Object remark) {
		Employee employee = SystemSession.getUser();
		int abid = getSeqId("APBILL_SEQ");
		String code = sGetMaxNumber("APBill", 2);
		String dateStr = DateUtil.currentDateString(Constant.YMD_HMS);
		int yearmonth = Integer.parseInt(DateUtil.currentDateString("yyyyMM"));
		if (StringUtil.hasText(date)) {
			dateStr = date.toString().replace("T", " ");
			yearmonth = Integer.parseInt(date.toString().replace("T", "").replace("-", "").substring(0, 6));
		}
		Object[] objs = getFieldsDataByCondition("Vendor", new String[] { "ve_buyerid", "ve_buyername", "ve_currency", "ve_taxrate",
				"ve_payment", "ve_paymentcode", "ve_name" }, "ve_id=" + veid);
		rate = getFieldValue("Currencysmonth", "cm_crrate", "cm_crname='" + currency + "' and cm_yearmonth='" + yearmonth + "'",
				Double.class);
		if (objs == null || objs.length < 7) {
			objs = new Object[7];
			objs[0] = "";
			objs[1] = "";
			objs[2] = "";
			objs[3] = "";
			objs[4] = "";
			objs[5] = "";
			objs[6] = "";
		}
		boolean bool = execute(
				INSERTAPBILLWITHVENDOR,
				new Object[] { abid, code, BaseUtil.getLocalMessage("ENTERING"), "ENTERING", employee.getEm_id(),
						Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), veid, vecode, "应付发票", objs[0], objs[1], currency,
						rate, objs[4], "UNPRINT", BaseUtil.getLocalMessage("UNPRINT"), "UNPAYMENT", BaseUtil.getLocalMessage("UNPAYMENT"),
						"UNPOST", BaseUtil.getLocalMessage("UNPOST"), employee.getEm_name(), objs[5], Timestamp.valueOf(dateStr),
						yearmonth, refno, remark, objs[6] });

		if (bool) {
			Object ve_duedays = getFieldDataByCondition("vendor", "ve_duedays", "ve_code='" + objs[6] + "'");
			if ("".equals(ve_duedays) || "null".equals(ve_duedays)) {
				ve_duedays = 0;
			}
			String res = callProcedure("SP_GETPAYDATE", new Object[] { Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)),
					objs[5], ve_duedays });
			updateByCondition("apbill", "ab_paydate='" + res + "'", " ab_code='" + code + "'");
			return code;
		}

		return null;
	}

	@Override
	public void getCustomer(int[] id) {
		/*
		 * for(int idx:id){ SqlRowSet rs =
		 * getJdbcTemplate().queryForRowSet(GETVENDOR, idx); if(rs.next()){
		 * execute(SETVENDOR, new
		 * Object[]{rs.getObject(1),rs.getObject(2),rs.getObject
		 * (3),rs.getObject(4),idx}); } }
		 */
	}

	@Override
	public void checkPDQty(int pdid) {
		Object abid = getFieldDataByCondition("ProdIODetail", "pd_piid", "pd_id=" + pdid);
		SqlRowList rs = queryForRowSet("SELECT pd_outqty,pd_invoqty,pd_id FROM ProdIODetail where pd_piid=" + abid);
		boolean bool = true;
		while (rs.next()) {
			int qty = rs.getInt(1);
			int yqty = rs.getInt(2);
			if (yqty > 0) {
				if (yqty == qty) {
					// 修改明细状态为已转
					updateByCondition("ProdIODetail", "pd_auditstatus='TURNAR'", "pd_id=" + rs.getInt(3));
				} else {
					bool = false;
					// 修改明细状态为部分转
					updateByCondition("ProdIODetail", "pd_auditstatus='PARTAR'", "pd_id=" + rs.getInt(3));
				}
			} else {
				bool = false;
			}
		}
		if (bool) {
			updateByCondition("ProdInOut", "pi_billstatuscode='TURNAR',pi_billstatus='" + BaseUtil.getLocalMessage("TURNAR") + "'",
					"pi_id=" + abid);
		} else {
			updateByCondition("ProdInOut", "pi_billstatuscode='PARTAR',pi_billstatus='" + BaseUtil.getLocalMessage("PARTAR") + "'",
					"pi_id=" + abid);
		}
	}

	@Override
	public void checkPDQtyAP(int pdid) {
		Object abid = getFieldDataByCondition("ProdIODetail", "pd_piid", "pd_id=" + pdid);
		SqlRowList rs = queryForRowSet("SELECT ABS(pd_outqty-pd_inqty),pd_showinvoqty,pd_id FROM ProdIODetail where pd_piid=" + abid);
		boolean bool = true;
		while (rs.next()) {
			double qty = rs.getDouble(1);
			double yqty = rs.getDouble(2);
			if (yqty > 0) {
				if (yqty == qty) {
					// 修改明细状态为已转
					updateByCondition("ProdIODetail", "pd_auditstatus='TURNAR'", "pd_id=" + rs.getInt(3));
				} else {
					bool = false;
					// 修改明细状态为部分转
					updateByCondition("ProdIODetail", "pd_auditstatus='PARTAR'", "pd_id=" + rs.getInt(3));
				}
			} else {
				bool = false;
			}
		}
		if (bool) {
			updateByCondition("ProdInOut", "pi_billstatuscode='TURNAR',pi_billstatus='" + BaseUtil.getLocalMessage("TURNAR") + "'",
					"pi_id=" + abid);
		} else {
			updateByCondition("ProdInOut", "pi_billstatuscode='PARTAR',pi_billstatus='" + BaseUtil.getLocalMessage("PARTAR") + "'",
					"pi_id=" + abid);
		}
	}

	@Override
	public void checkGSDQtyAR(String gsdid) {
		Object gsid = getFieldDataByCondition("GoodsSendDetail", "gsd_gsid", "gsd_id='" + gsdid + "'");
		SqlRowList rs = queryForRowSet("SELECT ABS(gsd_qty),gsd_showinvoqty,gsd_id FROM GoodsSendDetail where gsd_gsid='" + gsid.toString()
				+ "'");
		boolean bool = true;
		while (rs.next()) {
			double qty = rs.getDouble(1);
			double yqty = rs.getDouble(2);
			if (yqty > 0) {
				if (yqty == qty) {
					// 修改明细状态为已转
					updateByCondition("GoodsSendDetail", "gsd_statuscode='TURNAR'", "gsd_id=" + rs.getInt(3));
				} else {
					bool = false;
					// 修改明细状态为部分转
					updateByCondition("GoodsSendDetail", "gsd_statuscode='PARTAR'", "gsd_id=" + rs.getInt(3));
				}
			} else {
				bool = false;
			}
		}
		if (bool) {
			updateByCondition("GoodsSend", "gs_invostatuscode='TURNAR',gs_invostatus='" + BaseUtil.getLocalMessage("TURNAR") + "'",
					"gs_id='" + gsid.toString() + "'");
		} else {
			updateByCondition("GoodsSend", "gs_invostatuscode='PARTAR',gs_invostatus='" + BaseUtil.getLocalMessage("PARTAR") + "'",
					"gs_id='" + gsid.toString() + "'");
		}
	}

	@Override
	public void checkESDQtyAR(String esdid) {
		Object esid = getFieldDataByCondition("EstimateDetail", "esd_esid", "esd_id='" + esdid + "'");
		SqlRowList rs = queryForRowSet("SELECT ABS(esd_qty),esd_showinvoqty,esd_id FROM EstimateDetail where esd_esid='" + esid.toString()
				+ "'");
		boolean bool = true;
		while (rs.next()) {
			double qty = rs.getDouble(1);
			double yqty = rs.getDouble(2);
			if (yqty > 0) {
				if (yqty == qty) {
					// 修改明细状态为已转
					updateByCondition("EstimateDetail", "esd_statuscode='TURNAR'", "esd_id=" + rs.getInt(3));
				} else {
					bool = false;
					// 修改明细状态为部分转
					updateByCondition("EstimateDetail", "esd_statuscode='PARTAR'", "esd_id=" + rs.getInt(3));
				}
			} else {
				bool = false;
			}
		}
		if (bool) {
			updateByCondition("Estimate", "es_invostatuscode='TURNAR',es_invostatus='" + BaseUtil.getLocalMessage("TURNAR") + "'",
					"es_id='" + esid.toString() + "'");
		} else {
			updateByCondition("Estimate", "es_invostatuscode='PARTAR',es_invostatus='" + BaseUtil.getLocalMessage("PARTAR") + "'",
					"es_id='" + esid.toString() + "'");
		}
	}

	/*
	 * 拨出单过账后过账拨入单
	 */
	@Override
	public String turnProdIO(int id) {
		String dets = null;
		SqlRowList rs = queryForRowSet("select pi_relativeplace,pi_class from prodinout where pi_id=?", id);
		if (rs.next()) {
			SqlRowList rs1 = queryForRowSet(
					"select pi_id from prodinout where pi_inoutno=? and pi_class in ('拨入单','销售拨入单') and nvl(PI_UNAUTOPOSTIN,0)=0",
					rs.getString("pi_relativeplace"));
			if (rs1.next()) {
				if (isDBSetting("cgyCheck")) {
					/**
					 * maz
					 * 出入库单判断过账人是否在明细行仓库的仓管员资料表中存在,人员资料中查找管理员一样限制如果为非仓库员不允许过账
					 * 虚拟账号不限制 2017080135
					 */
					Object type = getFieldDataByCondition("Employee", "em_code", "em_code='" + SystemSession.getUser().getEm_code() + "'");
					if (type != null) {
						dets = getJdbcTemplate()
								.queryForObject(
										"select wm_concat(pd_pdno) from prodinout,prodiodetail where pi_id=pd_piid and pi_id=? and pd_id not in "
												+ "(select pd_id from prodinout,prodiodetail,warehouse,warehouseman where pi_id=pd_piid and pd_whcode=wh_code and wh_id=wm_whid "
												+ "and pi_id=? and wm_cgycode=?)", String.class, rs1.getInt("pi_id"), rs1.getInt("pi_id"),
										SystemSession.getUser().getEm_code());
					}
				}
				if (dets == null) {
					if ("拨出单".equals(rs.getString("pi_class"))) {
						prodInOutService.postProdInOut(rs1.getInt("pi_id"), "ProdInOut!AppropriationIn");
					} else if ("销售拨出单".equals(rs.getString("pi_class"))) {
						prodInOutService.postProdInOut(rs1.getInt("pi_id"), "ProdInOut!SaleAppropriationIn");
					}
				}
			}
		}
		return dets;
	}

	/*
	 * 拨出单反过账之前反过帐拨入单
	 */
	@Override
	public void resPostProdIn(int id) {
		SqlRowList rs = queryForRowSet("select pi_relativeplace,pi_class from prodinout where pi_id=?", id);
		if (rs.next()) {
			SqlRowList rs1 = queryForRowSet("select pi_id from prodinout where pi_inoutno=? and pi_class in ('拨入单','销售拨入单')",
					rs.getString("pi_relativeplace"));
			if (rs1.next()) {
				if ("拨出单".equals(rs.getString("pi_class"))) {
					prodInOutService.resPostProdInOut("ProdInOut!AppropriationIn", rs1.getInt("pi_id"));
				} else if ("销售拨出单".equals(rs.getString("pi_class"))) {
					prodInOutService.resPostProdInOut("ProdInOut!SaleAppropriationIn", rs1.getInt("pi_id"));
				}
				deleteByCondition("ProdInOut", "pi_id=" + rs1.getInt("pi_id") + " and pi_statuscode='UNPOST' ");
				deleteByCondition("ProdIODetail", "pd_piid=" + rs1.getInt("pi_id") + " and pd_status=0 ");
			}
		}
	}

	/*
	 * 生产领、退、补、委外领、退、补：工单号+用料序号对应的物料必须一致
	 */
	@Override
	public String checkProduct(int id) {
		SqlRowList rs = queryForRowSet(
				"SELECT prodiodetail.pd_pdno from ProdIODetail,MakeMaterial where pd_ordercode=mm_code and pd_orderdetno=mm_detno and pd_piid=? and pd_ordercode<>'' and pd_ordercode is not null and pd_prodcode<>mm_prodcode",
				id);
		int detno = 0;
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			detno = rs.getInt(1);
			if (detno > 0) {
				sb.append("不能打印！序号[");
				sb.append(detno);
				sb.append("]中的物料编号与工单不一致！");
			}
		}
		return sb.toString();
	}

	@Override
	public String checkPurcDetail(int id) {
		SqlRowList rs = queryForRowSet(
				"select * from ProdIODetail left join purchasedetail on pd_ordercode=pd_code and pd_orderdetno=pd_detno where pd_piid =? and (purchasedetail.pd_status='FINISH' or purchasedetail.pd_status='NULLIFIED')",
				id);
		int detno = 0;
		String code = null;
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			detno = rs.getInt("pd_pdno");
			code = rs.getString("pd_ordercode");
			if (code != null) {
				sb.append("不能打印！序号[");
				sb.append(detno);
				sb.append("]中的采购订单明细行已结案或者已作废！采购单号:"
						+ "<a href=\"javascript:openUrl('jsps/scm/purchase/purchase.jsp?formCondition=pu_codeIS" + code + "')\">" + code
						+ "</a>&nbsp;");
			}
		}
		return sb.toString();
	}

	@Override
	public String checkSaleDetail(int id) {
		SqlRowList rs = queryForRowSet(
				"select * from ProdIODetail left join saledetail on pd_ordercode=sd_code and pd_orderdetno=sd_detno where  pd_piid = ? and sd_statuscode in ('FINISH','NULLIFIED')",
				id);
		int detno = 0;
		String code = null;
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			detno = rs.getInt("pd_pdno");
			code = rs.getString("pd_ordercode");
			if (code != null) {
				sb.append("不能打印！序号[");
				sb.append(detno);
				sb.append("]中的销售订单明细行已结案或者已作废！销售单号:" + "<a href=\"javascript:openUrl('jsps/scm/sale/sale.jsp?formCondition=sa_codeIS"
						+ code + "')\">" + code + "</a>&nbsp;");
			}
		}
		return sb.toString();
	}

	@Override
	public String checkexpbackqty(int id) {
		SqlRowList rs = queryForRowSet(
				"select * from (SELECT pd_ordercode,pd_orderdetno,sum(pd_inqty) as pd_inqty,max(pd_pdno)as pd_pdno FROM  "
						+ "ProdIODetail where pd_piid=? group by pd_ordercode,pd_orderdetno )A left join makematerial on mm_code=pd_ordercode and mm_detno=pd_orderdetno "
						+ "left join make on ma_code=mm_code where pd_inqty>nvl(mm_havegetqty,0)-nvl(mm_scrapqty,0)-ma_madeqty*mm_oneuseqty and mm_qty>0",
				id);
		int detno = 0;
		float hgqty = 0;
		float scrapqty = 0;
		float addqty = 0;
		float inqty = 0;
		String code = null;
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			detno = rs.getInt("pd_pdno");
			hgqty = rs.getFloat("mm_havegetqty");
			scrapqty = rs.getFloat("mm_scrapqty");
			addqty = rs.getFloat("mm_addqty");
			inqty = rs.getFloat("pd_inqty");
			code = rs.getString("pd_ordercode");
			if (code != null) {
				sb.append("不能打印！退料数量不能大于结存可退料数,行号[");
				sb.append(detno);
				sb.append("],已领料数[");
				sb.append(hgqty);
				sb.append("],报废数[");
				sb.append(scrapqty);
				sb.append("],已补料数[");
				sb.append(addqty);
				sb.append("],本次退料[");
				sb.append(inqty);
				sb.append("]");
			}
		}
		return sb.toString();
	}

	@Override
	public String checkaddqty(int id) {
		SqlRowList rs = queryForRowSet(
				"select * from (SELECT pd_ordercode,pd_orderdetno,sum(pd_outqty) as pd_outqty,max(pd_pdno)as pd_pdno FROM ProdIODetail "
						+ "where pd_piid=? group by pd_ordercode,pd_orderdetno)A left join makematerial on mm_code=pd_ordercode and mm_detno=pd_orderdetno "
						+ "where nvl(mm_addqty,0)+pd_outqty>nvl(mm_scrapqty,0)+nvl(mm_returnmqty,0)", id);
		int detno = 0;
		float scrapqty = 0;
		float addqty = 0;
		float outqty = 0;
		String code = null;
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			detno = rs.getInt("pd_pdno");
			scrapqty = rs.getFloat("mm_scrapqty");
			addqty = rs.getFloat("mm_addqty");
			outqty = rs.getFloat("pd_outqty");
			code = rs.getString("pd_ordercode");
			if (code != null) {
				sb.append("不能打印！补料数量不能大于报废数,行号[");
				sb.append(detno);
				sb.append("],报废数[");
				sb.append(scrapqty);
				sb.append("],已补料数[");
				sb.append(addqty);
				sb.append("],本次补料[");
				sb.append(outqty);
				sb.append("]");
			}
		}
		return sb.toString();
	}

	@Override
	public String checkgetqty(int id) {
		SqlRowList rs = queryForRowSet(
				"select mm_qty-nvl(mm_canuserepqty,0)-(nvl(mm_havegetqty,0)-nvl(mm_addqty,0)+nvl(mm_returnmqty,0))+nvl(mm_haverepqty,0) as remainqty, * from "
						+ "(SELECT pd_ordercode,pd_orderdetno,pd_prodcode,sum(pd_outqty) as pd_outqty,max(pd_pdno) as pd_pdno FROM  ProdIODetail where  pd_piid=? and pd_status = 0 group by pd_ordercode,"
						+ "pd_orderdetno,pd_prodcode)A left join makematerial on mm_code=pd_ordercode and mm_detno=pd_orderdetno and mm_prodcode=pd_prodcode left join make on ma_code=mm_code where mm_code is not null "
						+ "and ma_qty*mm_oneuseqty+0.01<mm_qty and round(nvl(mm_havegetqty,0)-nvl(mm_addqty,0)-nvl(mm_haverepqty,0)+nvl(mm_returnmqty,0)+pd_outqty,2)>round(nvl(mm_qty,0)-nvl(mm_canuserepqty,0),2)",
				id);
		int detno = 0;
		float remainqty = 0;
		String code = null;
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			detno = rs.getInt("pd_pdno");
			remainqty = rs.getFloat("remainqty");
			code = rs.getString("pd_ordercode");
			if (code != null) {
				sb.append("不能打印！领料数量不能大于订单当前允许领料数,行号[");
				sb.append(detno);
				sb.append("],订单剩余允许领料数[");
				sb.append(remainqty);
				sb.append("]");
			}
		}
		return sb.toString();
	}

	@Override
	public String checkkits(int id) {
		SqlRowList rs = queryForRowSet(
				"SELECT pd_ordercode,sum(pd_inqty) as qty FROM ProdIODetail where pd_piid=? and NVL(pd_status,0)=0 group by pd_ordercode",
				id);
		int detno = 0;
		float qty = 0;
		String code = null;
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			qty = rs.getFloat("qty");
			code = rs.getString("pd_ordercode");
			SqlRowList rl = queryForRowSet("SELECT mm_detno,mm_prodcode,mm_havegetqty FROM Makematerial left join product on pr_code=mm_prodcode "
					+ "left join make on ma_id=mm_maid left join makekind on mk_name=ma_kind WHERE mm_code='"
					+ code
					+ "' and nvl(ma_madeqty,0)+"
					+ qty
					+ "-((NVL(mm_havegetqty,0)-nvl(mm_scrapqty,0))*1.0)/mm_oneuseqty>=1 "
					+ "and nvl(mm_materialstatus,' ') =' ' AND NVL(mk_finishunget,0)=0 and nvl(pr_supplytype,'')<>'VIRTUAL' and mm_oneuseqty>0  and mm_qty>0 "
					+ "and mm_oneuseqty>0.001 and mm_qty+0.01>=mm_oneuseqty*ma_qty");
			if (rl.next()) {
				detno = rl.getInt("mm_detno");
				if (code != null) {
					sb.append("不允许当前操作！工单[");
					sb.append(code);
					sb.append("]用料序号[");
					sb.append(detno);
					sb.append("]未领足料，不能完工!");
				}
			}

		}
		return sb.toString();
	}

	@Override
	public String delletednotAllowPrint(int id) {
		SqlRowList rs = queryForRowSet("SELECT pi_inoutno from ProdInOut where pi_id=? and pi_invostatuscode='DELETED'", id);
		String code = null;
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			code = rs.getString("pi_inoutno");
			if (code != null) {
				sb.append("不能打印！单据[");
				sb.append(code);
				sb.append("]已被删除！");
			}
		}
		return sb.toString();
	}

	@Override
	public String expcurrencyCheck(int id) {
		SqlRowList rs = queryForRowSet("SELECT ma_currency,pd_pdno from Make,ProdIODetail where ma_code=pd_ordercode and pd_piid=?", id);
		int detno = 0;
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			detno = rs.getInt("pd_pdno");
			if (detno > 0) {
				sb.append("不能过账！币种与关联单据币种不一致,明细行号[");
				sb.append(detno);
				sb.append("]");
			}
		}
		return sb.toString();
	}

	@Override
	public String pucurrencyCheck(int id) {
		SqlRowList rs = queryForRowSet("SELECT pu_currency,pd_pdno from Purchase,ProdIODetail where pu_code=pd_ordercode and pd_piid=?", id);
		int detno = 0;
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			detno = rs.getInt("pd_pdno");
			if (detno > 0) {
				sb.append("不能过账！币种与关联采购单币种不一致,明细行号[");
				sb.append(detno);
				sb.append("]");
			}
		}
		return sb.toString();
	}

	@Override
	public String makestatusCheck(int id) {
		SqlRowList rs = queryForRowSet(
				"select pd_pdno from ProdIODetail left join make on pd_ordercode=ma_code where pd_piid = ? and ma_checkstatuscode<>'APPROVE'",
				id);
		int detno = 0;
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			detno = rs.getInt("pd_pdno");
			if (detno > 0) {
				sb.append("序号[");
				sb.append(detno);
				sb.append("]中的工单未批准，不允许出入库操作!");
			}
		}
		return sb.toString();
	}

	@Override
	public String sacurrencyCheck(int id) {
		SqlRowList rs = queryForRowSet("SELECT sa_currency,pd_pdno from Sale,ProdIODetail where sa_code=pd_ordercode and pd_piid=?", id);
		int detno = 0;
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			detno = rs.getInt("pd_pdno");
			if (detno > 0) {
				sb.append("不能过账！币种与关联销售订单币种不一致,明细行号[");
				sb.append(detno);
				sb.append("]");
			}
		}
		return sb.toString();
	}

	@Override
	public String orderinfoCheck(int id) {
		SqlRowList rs = queryForRowSet(
				"SELECT pd_pdno FROM ProdIODetail where (nvl(pd_ordercode,'')<>'' AND nvl(pd_plancode,'')<>'')  and  pd_piid=?", id);
		SqlRowList rs2 = queryForRowSet(
				"select * FROM ProdIODetail left join saleforecastdetail on sd_code=pd_plancode and sd_detno=pd_sdid where (pd_plancode<>'' AND nvl(pd_sdid,0)>0) and pd_piid=? and nvl(sd_prodcode,'')='')",
				id);
		SqlRowList rs3 = queryForRowSet(
				"select * from (SELECT sd_code,sd_detno,sum(pd_outqty+nvl(pd_beipinoutqty,0)) as outqty,max(sd_qty) as sdqty FROM ProdIODetail,saleforecastdetail where sd_code=pd_plancode and sd_detno=pd_sdid and (pd_plancode<>'' AND nvl(pd_sdid,0)>0) and pd_piid=? group by sd_code,sd_detno) A where A.sdqty<outqty",
				id);
		SqlRowList rs4 = queryForRowSet(
				"select * FROM ProdIODetail,saleforecastdetail,product where pr_code=sd_prodcode and sd_code=pd_plancode and sd_detno=pd_sdid and (pd_plancode<>'' AND nvl(pd_sdid,0)>0) and pd_piid=? and (sd_prodcode<>pd_prodcode)",
				id);
		SqlRowList rs5 = queryForRowSet(
				"SELECT pd_pdno FROM ProdIODetail where (nvl(pd_orderdetno,0)>0 AND nvl(pd_sdid,0)>0) and pd_inoutno=?", id);
		SqlRowList rs6 = queryForRowSet(
				"SELECT pd_pdno FROM ProdIODetail where (nvl(pd_orderdetno,0)=0 and nvl(pd_sdid,0)=0) and pd_inoutno=?", id);
		String code = null;
		String code2 = null;
		int detno = 0;
		float sdqty = 0;
		float outqty = 0;
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			detno = rs.getInt("pd_pdno");
			if (detno > 0) {
				sb.append("序号[");
				sb.append(detno);
				sb.append("]不能同时存在销售合同和预测单!");
			}
		}
		while (rs2.next()) {
			detno = rs2.getInt("pd_pdno");
			if (detno > 0) {
				sb.append("序号[");
				sb.append(detno);
				sb.append("]预测单号或序号不存在!");
			}
		}
		while (rs3.next()) {
			detno = rs3.getInt("sd_detno");
			code = rs3.getString("sd_code");
			sdqty = rs3.getFloat("sdqty");
			outqty = rs3.getFloat("outqty");
			if (detno > 0) {
				sb.append("不允许超预测单数量拨出,预测单[");
				sb.append(code);
				sb.append("],序号[");
				sb.append(detno);
				sb.append("],预测数量[");
				sb.append(sdqty);
				sb.append("],拨出数量[");
				sb.append(outqty);
				sb.append("]");
			}
		}
		while (rs4.next()) {
			detno = rs4.getInt("pd_pdno");
			code = rs4.getString("pd_prodcode");
			code2 = rs4.getString("sd_prodcode");
			if (detno > 0) {
				sb.append("拨出物料必须与预测单物料一致，行号[");
				sb.append(detno);
				sb.append("],拨出料号[");
				sb.append(code);
				sb.append("],预测单料号[");
				sb.append(code2);
				sb.append("]");
			}
		}
		while (rs5.next()) {
			detno = rs5.getInt("pd_pdno");
			if (detno > 0) {
				sb.append("序号[");
				sb.append(detno);
				sb.append("],不能同时存在销售合同序号和预测单序号!");
			}
		}
		while (rs6.next()) {
			detno = rs6.getInt("pd_pdno");
			if (detno > 0) {
				sb.append("序号[");
				sb.append(detno);
				sb.append("],必须填写订单或预测单的序号!");
				sb.append(code);
			}
		}
		return sb.toString();
	}

	@Override
	public String whcostCheck(int id) {
		SqlRowList rs = queryForRowSet("SELECT pd_whcode,pd_inwhcode FROM ProdIODetail WHERE pd_piid=?", id);
		String inwhcode = null;
		String outwhcode = null;
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			inwhcode = rs.getString("pd_inwhcode");
			outwhcode = rs.getString("pd_whcode");
			SqlRowList rs2 = queryForRowSet("SELECT nvl(wh_nocost,0) FROM WareHouse where wh_code=?", inwhcode);
			SqlRowList rs3 = queryForRowSet("SELECT nvl(wh_nocost,0) FROM WareHouse where wh_code=?", outwhcode);
			if (rs2.next()) {
				if (rs3.next()) {
					if (rs2.getInt(1) != rs3.getInt(1)) {
						sb.append("有值仓与无值仓之间不能相互调拨!");
					}
				}
			}
		}
		return sb.toString();
	}

	@Override
	public String pwonhandCheck(int id) {
		return null;
	}

	@Override
	public void getexpPrice(int id) {
		SqlRowList rs = queryForRowSet("SELECT * FROM ProdIODetail WHERE pd_piid=? and " + "nvl(pd_price,'')='' or nvl(pd_rate,'')=''", id);
		double price = 0;
		float rate = 0;
		String pd_ordercode = null;
		while (rs.next()) {
			pd_ordercode = rs.getString("pd_ordercode");
			SqlRowList rs2 = queryForRowSet("select ma_rate,ma_price from make where ma_code=? order by ma_date desc", pd_ordercode);
			if (rs2.next()) {
				price = rs2.getDouble("ma_price");
				rate = rs2.getFloat("ma_rate");
				updateByCondition("prodiodetail", "pd_price=" + price + ",pd_rate=" + rate, "pd_id = " + rs.getInt("pd_id"));
			}
		}
	}

	@Override
	public void getpuPrice(int id) {
		SqlRowList rs = queryForRowSet("SELECT * FROM ProdIODetail WHERE pd_piid=? and " + "nvl(pd_price,'')='' or nvl(pd_rate,'')=''", id);
		double price = 0;
		float rate = 0;
		String pd_ordercode = null;
		int pd_orderdetno = 0;
		while (rs.next()) {
			pd_ordercode = rs.getString("pd_ordercode");
			pd_orderdetno = rs.getInt("pd_ordercode");
			SqlRowList rs2 = queryForRowSet("select pd_rate,pd_price from purchasedetail where pd_code=? and pd_detno=?", pd_ordercode,
					pd_orderdetno);
			if (rs2.next()) {
				price = rs2.getDouble("pd_price");
				rate = rs2.getFloat("pd_rate");
				updateByCondition("prodiodetail", "pd_price=" + price + ",pd_rate=" + rate, "pd_id = " + rs.getInt("pd_id"));
			}
		}
	}

	@Override
	public void getsdTaxrate(int id) {
		SqlRowList rs = queryForRowSet("SELECT * FROM ProdIODetail WHERE pd_piid=? and " + "nvl(pd_price,'')='' or nvl(pd_rate,'')=''", id);
		double price = 0;
		float rate = 0;
		String pd_ordercode = null;
		int pd_orderdetno = 0;
		while (rs.next()) {
			pd_ordercode = rs.getString("pd_ordercode");
			pd_orderdetno = rs.getInt("pd_ordercode");
			SqlRowList rs2 = queryForRowSet("select sd_taxrate,sd_price from saledetail where sd_code=? and sd_detno=?", pd_ordercode,
					pd_orderdetno);
			if (rs2.next()) {
				price = rs2.getDouble("sd_price");
				rate = rs2.getFloat("sd_taxrate");
				updateByCondition("prodiodetail", "pd_price=" + price + ",pd_rate=" + rate, "pd_id = " + rs.getInt("pd_id"));
			}
		}
	}

	@Override
	public String qtyonhandCheck(int id) {
		SqlRowList rs = queryForRowSet(
				"SELECT pd_pdno,pd_outqty,pw_onhand FROM prodinout left join ProdIODetail on pi_id = pd_piid left join productwh on pd_whcode=pw_whcode and pd_prodcode=pw_prodcode WHERE pd_piid=? and pd_status=0",
				id);
		float pd_outqty = 0;
		float pw_onhand = 0;
		int pd_pdno = 0;
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			pd_outqty = rs.getFloat("pd_outqty");
			pw_onhand = rs.getFloat("pw_onhand");
			pd_pdno = rs.getInt("pd_pdno");
			if (pd_outqty > pw_onhand) {
				sb.append("序号[");
				sb.append(pd_pdno);
				sb.append("]的出库数量大于当前仓库的库存数!");
			}
		}
		return sb.toString();
	}

	@Override
	public String lineCheck(int id) {
		SqlRowList rs = queryForRowSet("SELECT * FROM  ProdIODetail where nvl(pd_sellercode,'')='' and pd_piid=?", id);
		int pd_pdno = 0;
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			pd_pdno = rs.getInt("pd_pdno");
			if (pd_pdno <= 0) {
				sb.append("序号[");
				sb.append(pd_pdno);
				sb.append("]线别不能为空!");
			}
		}
		return sb.toString();
	}

	@Override
	public String plantCheck(int id) {
		SqlRowList rs = queryForRowSet("SELECT * FROM  ProdIODetail where nvl(pd_wccode,'')='' and pd_piid=?", id);
		int pd_pdno = 0;
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			pd_pdno = rs.getInt("pd_pdno");
			if (pd_pdno <= 0) {
				sb.append("序号[");
				sb.append(pd_pdno);
				sb.append("]车间不能为空!");
			}
		}
		return sb.toString();
	}

	@Override
	public void getqtyfromorder(int id) {
		SqlRowList rs = queryForRowSet("SELECT * FROM ProdIODetail WHERE pd_piid=? and ("
				+ "nvl(pd_prodcode, '') = '') or (nvl(pd_inqty,0)+nvl(pd_outqty,0))<=0)", id);
		float qty = 0;
		String pd_ordercode = null;
		int pd_orderdetno = 0;
		while (rs.next()) {
			pd_ordercode = rs.getString("pd_ordercode");
			pd_orderdetno = rs.getInt("pd_ordercode");
			SqlRowList rs2 = queryForRowSet("select * from saledetail where sd_code=? and sd_detno=?", pd_ordercode, pd_orderdetno);
			if (rs2.next()) {
				qty = rs2.getFloat("sd_leaveassign");
				updateByCondition("prodiodetail", "pd_price=" + rs2.getString("sd_price") + ",pd_prodcode=" + rs2.getString("sd_prodcode")
						+ ",pd_outqty=" + qty, "pd_id = " + rs.getInt("pd_id"));
			}
		}
	}

	@Override
	public String departmentvalidCheck(int id) {
		SqlRowList rs = queryForRowSet(
				"SELECT pd_pdno FROM ProdIODetail where pd_departmentcode<>'' and pd_piid=? and pd_departmentcode not in (SELECT dp_code from department)",
				id);
		if (rs.next()) {
			return "序号[" + rs.getInt(1) + "]部门无效!";
		}
		rs = queryForRowSet("SELECT distinct pd_departmentcode FROM ProdIODetail where pd_departmentcode<>'' and pd_piid=?", id);
		StringBuffer sb = new StringBuffer();
		int count = rs.getResultList().size();
		if (count > 1) {
			sb.append("责任部门必须一致!");
		}
		return sb.toString();
	}

	@Override
	public String descriptionvalidCheck(int id) {
		SqlRowList rs = queryForRowSet(
				"SELECT pd_pdno FROM ProdIODetail where pd_textbox<>'' and pd_piid=? and pd_textbox not in (SELECT nr_name from QUA_NGReason)",
				id);
		if (rs.next()) {
			return "序号[" + rs.getInt(1) + "]不良原因无效!";
		}
		rs = queryForRowSet("SELECT distinct pd_textbox FROM ProdIODetail where pd_textbox<>'' and pd_piid=?", id);
		StringBuffer sb = new StringBuffer();
		int count = rs.getResultList().size();
		if (count > 1) {
			sb.append("同一张单据中明细的退料原因必须一致!");
		}
		return sb.toString();
	}

	@Override
	public void departmentUpdate(int id) {
		String sql = "update ProdIODetail set pd_departmentcode=?, pd_departmentname=? WHERE pd_piid=? and nvl(pd_departmentcode, '') = ''";
		SqlRowList rs = queryForRowSet(
				"select max(pd_departmentcode) from prodiodetail A where A.pd_inoutno=prodiodetail.pd_inoutno and A.pd_piclass=prodiodetail.pd_piclass) where nvl(pd_departmentcode,'')='' and pd_piid='?",
				id);
		String departmentcode = rs.getString(1);
		Object departmentname = getFieldDataByCondition("Department", "dp_name", "dp_code='" + departmentcode + "'");
		if (rs.next()) {
			execute(sql, departmentcode, departmentname, id);
		}
	}

	@Override
	public void descriptionUpdate(int id) {
		String sql = "update ProdIODetail set pd_textbox=? WHERE pd_piid=? and nvl(pd_textbox, '') = ''";
		SqlRowList rs = queryForRowSet(
				"select max(pd_textbox) from prodiodetail A where A.pd_inoutno=prodiodetail.pd_inoutno) where nvl(pd_textbox,'')='' and pd_piid=?",
				id);
		if (rs.next()) {
			execute(sql, rs.getString(1), id);
		}
	}

	@Override
	public String pitypeCheck(int id) {
		SqlRowList rs = queryForRowSet("SELECT pi_inoutno FROM ProdInOut where nvl(pi_type, '') = '' and pi_id=?", id);
		StringBuffer sb = new StringBuffer();
		String code = null;
		if (rs.next()) {
			code = rs.getString("pi_inoutno");
			sb.append("出入库单[");
			sb.append(code);
			sb.append("]的业务类型不能为空！");
		}
		return sb.toString();
	}

	@Override
	public String prstatusCheck(int id) {
		SqlRowList rs = queryForRowSet(
				"select pd_pdno from ProdIODetail left join product on pd_prodcode=pr_code where  pd_piid = ? and pr_statuscode<>'AUDITED'",
				id);
		int detno = 0;
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			detno = rs.getInt("pd_pdno");
			if (detno > 0) {
				sb.append("不能打印！序号[");
				sb.append(detno);
				sb.append("]中的物料未审核！");
			}
		}
		return sb.toString();
	}

	@Override
	public void getcmrate(int id) {
		String sql = "update ProdInOut set pi_rate=(select cr_rate from Currencys where cr_name=pi_currency) where pi_id=? and (pi_rate=0 or pi_rate=1)";
		String sql2 = "update ProdInout set pi_rate=cm_crrate from CurrencysMonth WHERE convert(char(6),pi_date,112)=cm_yearmonth and pi_currency=cm_crname and pi_id=? and nvl(pi_currency,'')<>''";
		String sql3 = "update ProdInout set pi_rate=cr_rate from Currencys where pi_currency=cr_name and nvl(pi_rate,0)=0 and pi_id=?";
		execute(sql, id);
		execute(sql2, id);
		execute(sql3, id);
	}

	@Override
	public String pdqtyCheck(int id) {
		SqlRowList rs = queryForRowSet("select pd_pdno from ProdIODetail where pd_piid =? and (nvl(pd_inqty,0)+nvl(pd_outqty,0))<=0)", id);
		int detno = 0;
		StringBuffer sb = new StringBuffer();
		while (rs.next()) {
			detno = rs.getInt("pd_pdno");
			if (detno > 0) {
				sb.append("序号[");
				sb.append(detno);
				sb.append("]中的出+入库数量为0！");
			}
		}
		return sb.toString();
	}

	@Override
	public void addMaterial(int id, String caller) {
		SqlRowList rs = queryForRowSet(
				"select pd_ordercode,pd_prodcode,sum(pd_outqty) as qty from ProdIODetail left join make on ma_code=pd_ordercode where pd_piid=? and pd_orderdetno=0 and ma_code<>'' and pd_prodcode not in (select mm_prodcode from makematerial where mm_code=pd_ordercode) and pd_prodcode not in (select mp_prodcode from makematerial,makematerialreplace where mm_code=pd_ordercode and mm_id=mp_mmid) group by pd_ordercode,pd_prodcode",
				id);
		String sql = "insert into makematerial(mm_code,mm_detno,mm_prodcode,mm_oneuseqty,mm_qty,mm_lostqty,mm_havegetqty,mm_supplytype,mm_remark,mm_wccode,mm_whcode,mm_balance) values(?,?,?,?,?,?,?,?,?,?,?,?) ";
		String sql2 = "update makematerial set mm_wccode=ma_factory from make where mm_code=ma_code and mm_code=? and mm_detno=?";
		String sql3 = "update makematerial set mm_supplytype=pr_supplytype,mm_whcode=pr_whcode from product where mm_prodcode=pr_code and mm_code=? and mm_detno=?";
		String sql4 = "update ProdIODetail set pd_orderdetno=? where pd_piid=? and pd_ordercode=? and pd_prodcode=?";
		int maxdetno = 0;
		String code = null;
		String prodcode = null;
		while (rs.next()) {
			code = rs.getString("pd_ordercode");
			prodcode = rs.getString("pd_prodcode");
			SqlRowList rs1 = queryForRowSet("select max(mm_detno) from makematerial where mm_code= ?", code);
			maxdetno = rs1.getInt(1) + 1;
			execute(sql, code, maxdetno, prodcode, 0, 0, 0, 0, "", "工单外退料", "", "", 0);
			execute(sql2, code, maxdetno);
			execute(sql3, code, maxdetno);
			execute(sql4, maxdetno, id, code, prodcode);
			// 记录操作
			logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.add"),
					BaseUtil.getLocalMessage("msg.addSuccess") + code + "新增物料序号：" + maxdetno + ",物料编号：" + prodcode, caller + "|pi_id=" + id));
		}
	}

	@Override
	public String piclassCheck(int id) {
		SqlRowList rs = queryForRowSet("select pi_class from ProdInOut where pi_id=?", id);
		StringBuffer sb = new StringBuffer();
		if (rs.next()) {
			sb.append(rs.getString("pi_class"));
			sb.append(",不允许反过账！");
		}
		return sb.toString();
	}

	@Override
	public String kpstatusCheck(int id) {
		SqlRowList rs = queryForRowSet(
				"select pi_inoutno from ProdInOut where pi_id=? and (pi_billstatuscode='PARTAR' or pi_billstatuscode='TURNAR')", id);
		StringBuffer sb = new StringBuffer();
		if (rs.next()) {
			sb.append("该单据已开票或者部分开票,不允许反过账！");
		}
		return sb.toString();
	}

	@Override
	public String getBatchCode(String caller, String field) {
		SqlRowList rs = queryForRowSet("select ds_inorout from DOCUMENTSETUP where ds_table=?", caller);
		String finalCode = null;
		if (rs.next()) {
			String Code = rs.getObject("ds_inorout").toString();
			if (Code.equals("IN") || Code.equals("-OUT")) {
				finalCode = sGetMaxNumber("ProdIOut", 2);
			}
		}
		return finalCode;
	}

	public boolean isIn(String caller) {
		SqlRowList rs = queryForRowSet("select ds_inorout from DOCUMENTSETUP where ds_table=?", caller);
		if (rs.next()) {
			return "IN".equals(rs.getString(1)) || "-OUT".equals(rs.getString(1));
		}
		return false;
	}

	@Override
	public boolean getCostPrice(String caller) {
		SqlRowList rs = queryForRowSet("select ds_inorout from DOCUMENTSETUP where ds_table=?", caller);
		if (rs.next()) {
			String Code = rs.getObject("ds_inorout").toString();
			if (Code.equals("IN") || Code.equals("-OUT")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * update by yaozx@14-01-08
	 * 
	 * @param id
	 *            验退单明细ID
	 * @param ordercode
	 *            采购单编号
	 * @param orderdetno
	 *            采购单序号
	 */
	@Override
	public void outqtyCheck(Object id, Object ordercode, Object orderdetno, Double uqty, Object piclass) {
		id = id == null ? -1 : id;
		Object qty = 0;
		Object ysqty = 0;
		Object[] aq = null;
		uqty = Math.abs(uqty);
		// 判断数量是否超出采购验收数量
		if (ordercode != null && !"".equals(ordercode.toString())) {
			if ("采购验退单".equals(piclass)) {
				qty = getFieldDataByCondition("ProdIODetail left join ProdInOut on pi_id=pd_piid", "sum(pd_outqty)", "pd_ordercode='"
						+ ordercode + "' AND pd_orderdetno=" + orderdetno + " and pd_id <>" + id
						+ " and pd_piclass='采购验退单' and pi_statuscode<>'POSTED'");
				qty = qty == null ? 0 : qty;
				aq = getFieldsDataByCondition("PurchaseDetail", new String[] { "pd_acceptqty", "pd_code", "pd_detno" }, "pd_code='"
						+ ordercode + "' and pd_detno=" + orderdetno);
				if (Double.parseDouble(aq[0].toString()) < Double.parseDouble(qty.toString()) + uqty) {
					BaseUtil.showError("采购验退数量不能大于验收数,采购单号[" + aq[1] + "],序号[" + aq[2] + "],已验退数[" + qty + "],验收数[" + aq[0] + "]");
				}
			} else if ("用品验退单".equals(piclass)) {
				qty = getFieldDataByCondition("ProdIODetail", "sum(pd_outqty)", "pd_ordercode='" + ordercode + "' AND pd_orderdetno="
						+ orderdetno + " and  pd_id <>" + id + " and pd_piclass='用品验退单' and nvl(pd_status,0)=0");
				qty = qty == null ? 0 : qty;
				ysqty = getFieldDataByCondition("oapurchasedetail", "nvl(od_ysqty,0)", "od_code='" + ordercode + "' and od_detno="
						+ orderdetno);
				if (Double.parseDouble(ysqty.toString()) < Double.parseDouble(qty.toString()) + uqty) {
					BaseUtil.showError("采购验退数量不能大于验收数,采购单号[" + ordercode + "],序号[" + orderdetno + "],已验退数[" + qty + "],验收数[" + ysqty + "]");
				}
			}
		}
	}

	@Override
	public String newGoodsSendByPiid(String piid) {
		Employee employee = SystemSession.getUser();
		String code = sGetMaxNumber("GOODSSEND", 2);
		SqlRowList rs = queryForRowSet(NEWGOODSSEND, new Object[] { piid });
		if (rs.next()) {
			int gsid = getSeqId("GOODSSEND_SEQ");
			String em_code = rs.getString("PI_SELLERCODE");
			Object[] em_msg = getFieldsDataByCondition("EMPLOYEE", new String[] { "em_id", "em_name" }, "em_code='" + em_code + "'");
			if (em_msg == null || em_msg.length < 2) {
				em_msg = getFieldsDataByCondition("CUSTOMER", new String[] { "cu_sellerid", "cu_sellername" },
						"cu_code='" + rs.getObject("PI_ARCODE") + "'");
				if (em_msg == null || em_msg.length < 2) {
					em_msg = new Object[2];
					em_msg[0] = "0";
					em_msg[1] = "";
				}
			}
			execute(INSERTNEWGOODSSEND,
					new Object[] { gsid, code, "发出商品", "", rs.getObject("PI_ARCODE"), "", em_msg[0], em_msg[1], rs.getObject("PI_DATE"),
							rs.getObject("PI_PAYDATE"), rs.getObject("PI_CURRENCY"), rs.getObject("PI_RATE"), "",
							rs.getObject("PI_PAYMENT"), "", employee.getEm_id(), BaseUtil.getLocalMessage("UNPOST"),
							BaseUtil.getLocalMessage("PARTAR"), "UNPOST", "PARTAR", "",
							Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), rs.getObject("PI_REMARK"),
							employee.getEm_name(), rs.getObject("PI_INOUTNO"), BaseUtil.getLocalMessage("ENTERING"), "ENTERING" });
			return String.valueOf(gsid);
		}
		return "0";
	}

	@Override
	public void turnGoodsSendDetail(String gsid, String pdid, String piid) {
		Object gscode = getFieldDataByCondition("GoodsSend", "gs_code", "gs_id='" + gsid + "'");
		SqlRowList rs = queryForRowSet(NEWGOODSSENDDETAIL, new Object[] { pdid });
		if (rs.next()) {
			int gsdid = getSeqId("GOODSSENDDETAIL_SEQ");
			Object count = getFieldDataByCondition("GOODSSENDDETAIL", "max(gsd_detno)", "gsd_gsid='" + gsid + "'");
			count = count == null ? 0 : count;
			int detno = Integer.parseInt(count.toString()) + 1;
			int qty = rs.getObject("SHOWQTY") == null ? 0 : Integer.parseInt(rs.getObject("SHOWQTY").toString());
			float price = Float.parseFloat(rs.getObject("PD_PRICE").toString());
			execute(INSERTNEWGOODSSENDDETAIL,
					new Object[] { gsdid, gsid, gscode, detno, rs.getObject("PD_PRODCODE"), rs.getObject("PD_ORDERCODE"), "0",
							rs.getObject("PD_ORDERDETNO"), rs.getObject("SHOWQTY"), "0", rs.getObject("PD_SENDPRICE"),
							rs.getObject("PD_PRICE"), qty * price, rs.getObject("PD_TAXRATE"), rs.getObject("PD_TAXTOTAL"), "0",
							"ENTERING", rs.getObject("PD_POCODE"), "0", "0", rs.getObject("PD_REMARK"), pdid, "0", piid,
							rs.getObject("PD_INOUTNO"), "0", "0" });
		}
	}

	public void account(String piclass, String pricekind, Object startdate, Object enddate, int flowid) {
		if ("拨入单".equals(piclass)) {
			execute("merge into prodiodetail p using "
					+ "(select max(pd1.pd_price) pd_avprice,p2.pd_id from prodinout p1,prodiodetail pd1,(select pi_relativeplace,pd_pdno,pd_prodcode,pd_id from prodinout,prodiodetail where pi_id=pd_piid and pi_class='拨入单' and pd_status>10 "
					+ "and pi_date>="
					+ startdate
					+ " and pd_flowid="
					+ flowid
					+ ") p2 where pi_id=pd_piid and pi_class='拨出单' and pd_status>10 and pi_date<="
					+ enddate
					+ " and p2.pi_relativeplace=p1.pi_inoutno and p2.pd_pdno=pd1.pd_pdno and p2.pd_prodcode=pd1.pd_prodcode group by p2.pd_id) q "
					+ "on (p.pd_id=q.pd_id) when matched then update set p.pd_avprice=q.pd_avprice,p.pd_pricekind='拨出单'");
		} else if ("销售拨入单".equals(piclass)) {
			execute("merge into prodiodetail p using "
					+ "(select max(pd1.pd_price) pd_avprice,p2.pd_id from prodinout p1,prodiodetail pd1,(select pi_relativeplace,pd_pdno,pd_prodcode,pd_id from prodinout,prodiodetail where pi_id=pd_piid and pi_class='销售拨入单' and pd_status>10 "
					+ "and pi_date>="
					+ startdate
					+ " and pd_flowid="
					+ flowid
					+ ") p2 where pi_id=pd_piid and pi_class='销售拨出单' and pd_status>10 and pi_date<="
					+ enddate
					+ " and p2.pi_relativeplace=p1.pi_inoutno and p2.pd_pdno=pd1.pd_pdno and p2.pd_prodcode=pd1.pd_prodcode group by p2.pd_id) q "
					+ "on (p.pd_id=q.pd_id) when matched then update set p.pd_avprice=q.pd_avprice,p.pd_pricekind='销售拨出单'");
		} else if ("其它采购入库单".equals(piclass) && "其它采购出库单".equals(pricekind)) {
			execute("merge into prodiodetail p using "
					+ "(select max(pd1.pd_price) pd_avprice,p2.pd_id from prodinout p1,prodiodetail pd1,(select pd_price,pd_id from prodinout,prodiodetail where pi_id=pd_piid and pi_class='其它采购入库单' and pd_status>10 "
					+ "and pi_date>=" + startdate + " and pd_flowid=" + flowid
					+ ") p2 where pi_id=pd_piid and pi_class='其它采购出库单' and pd_status>10 and pi_date<=" + enddate
					+ " and p2.pd_id=pd1.pd_ioid and ROUND(p2.pd_price,8)<>ROUND(pd1.pd_price,8) group by p2.pd_id) q "
					+ "on (p.pd_id=q.pd_id) when matched then update set p.pd_avprice=q.pd_avprice,p.pd_pricekind='其它采购出库单'");
		} else if ("换货入库单".equals(piclass) && "换货出库单".equals(pricekind)) {
			execute("merge into prodiodetail p using "
					+ "(select max(pd1.pd_price) pd_avprice,p2.pd_id from prodinout p1,prodiodetail pd1,(select pi_relativeplace,pd_prodcode,pd_pdno,pd_price,pd_id from prodinout,prodiodetail where pi_id=pd_piid and pi_class='换货入库单' and pd_status>10 "
					+ "and pi_date>="
					+ startdate
					+ " and pd_flowid="
					+ flowid
					+ ") p2 where pi_id=pd_piid and pi_class='换货出库单' and pd_status>10 and pi_date<="
					+ enddate
					+ " and p2.pi_relativeplace=p1.pi_inoutno and p2.pd_pdno=pd1.pd_pdno and p2.pd_prodcode=pd1.pd_prodcode and ROUND(p2.pd_price,8)<>ROUND(pd1.pd_price,8) group by p2.pd_id) q "
					+ "on (p.pd_id=q.pd_id) when matched then update set p.pd_avprice=q.pd_avprice,p.pd_pricekind='换货出库单'");
		}
	}

	/*
	 * 核算界面自动更新核算单价
	 */
	@Override
	public String getPrice(String piclass, String pricekind, String prodcode, Object startdate, Object enddate, String ordercode,
			Object orderdetno, Double pirate, String whcode, int type, int pdid, int flowid) {
		String sql = null;
		Double getPrice = 0.0;
		Double oldprice = 0.0;
		if ("最新入库单价".equals(pricekind)) {
			sql = "select ROUND(pd_price,8) from (select pd_price from prodinout,prodiodetail where pi_id=pd_piid and pd_prodcode='"
					+ prodcode + "' and pi_statuscode='POSTED' and nvl(pd_price,0) > 0 and nvl(pd_inqty,0) > 0 and pi_date<=" + enddate
					+ " and pd_piid not in (select pd_piid from prodiodetail where pd_id=" + pdid
					+ ") order by pi_date desc,pi_inoutno desc) where rownum<2";
		} else if ("最新出货单价".equals(pricekind)) {
			sql = "select ROUND(pd_price,8) from (select pd_price from prodinout,prodiodetail where pi_id=pd_piid and pd_prodcode='"
					+ prodcode
					+ "' and pi_statuscode='POSTED' and nvl(pd_price,0) > 0 and nvl(pd_outqty,0) > 0 and to_char(pi_date,'yyyymm')<='"
					+ type + "' order by pi_date desc,pi_inoutno desc) where rownum<2";
		} else if ("最新出库单价".equals(pricekind)) {
			sql = "select ROUND(pd_price,8) from (select pd_price from prodinout,prodiodetail where pi_id=pd_piid and pd_prodcode='"
					+ prodcode + "' and pi_statuscode='POSTED' and nvl(pd_price,0) > 0 and nvl(pd_outqty,0) > 0 and pi_date<=" + enddate
					+ " order by pi_date desc,pi_inoutno desc) where rownum<2";
		} else if ("标准单价".equals(pricekind)) {
			sql = "select round(pr_standardprice,8) from product where pr_code='" + prodcode + "'";
		} else if ("平均单价".equals(pricekind)) {
			sql = "select round(pr_avprice,8) from product where pr_code='" + prodcode + "'";
		} else if ("月初库存单价".equals(pricekind)) {
			sql = "select round(pwm_beginamount/pwm_beginqty,8) from (select pwm_beginamount,pwm_beginqty from productwhmonth where pwm_prodcode='"
					+ prodcode + "' and pwm_whcode='" + whcode + "' and nvl(pwm_beginqty,0)>0 order by pwm_yearmonth desc) where rownum<2";
		} else if ("成本表".equals(pricekind)) {
			sql = "select round(cd_costprice,8) from costdetail where cd_makecode='" + ordercode + "' and cd_prodcode='" + prodcode
					+ "' and cd_yearmonth=" + type;
		} else if ("用料表平均单价".equals(pricekind)) {
			execute("update MakeMaterial set mm_havegetqtyia=(select sum(nvl(pd_outqty,0)) from prodiodetail "
					+ " where pd_status>0 and pd_piclass in ('生产领料单','生产补料单','委外领料单','委外补料单') "
					+ " and pd_ordercode=mm_code and pd_orderdetno=mm_detno) where mm_code='" + ordercode + "' and mm_detno=" + orderdetno);
			execute("update MakeMaterial set mm_havegetamountia=(select sum(nvl(pd_outqty,0)*pd_price) from prodiodetail "
					+ " where pd_status>0 and pd_piclass in ('生产领料单','生产补料单','委外领料单','委外补料单') "
					+ " and pd_ordercode=mm_code and pd_orderdetno=mm_detno) where mm_code='" + ordercode + "' and mm_detno=" + orderdetno);
			sql = "select round(nvl(mm_havegetamountia,0)/nvl(mm_havegetqtyia,0),8) from MakeMaterial where mm_code='" + ordercode
					+ "' and mm_detno=" + orderdetno + " and nvl(mm_havegetqtyia,0)>0";
		} else if ("发货平均成本".equals(pricekind)) {
			if (ordercode == null || "".equals(ordercode)) {
				SqlRowList rs = queryForRowSet("select pd_inoutno from ProdIODetail where pd_id =" + pdid);
				if (rs.next()) {
					return "退货单[" + rs.getObject("pd_inoutno") + "]为无PO退货单，请手工核算单价！<br>";
				}
			} else {
				execute("update SaleDetail set sd_sendqtyia=(select sum(nvl(pd_outqty,0)) from prodiodetail "
						+ " where pd_status>0 and pd_piclass = '出货单' "
						+ " and pd_ordercode=sd_code and pd_orderdetno=sd_detno) where sd_code='" + ordercode + "' and sd_detno="
						+ orderdetno);
				execute("update SaleDetail set sd_sendamountia=(select sum(nvl(pd_outqty,0)*pd_price) from prodiodetail "
						+ " where pd_status>0 and pd_piclass = '出货单' "
						+ " and pd_ordercode=sd_code and pd_orderdetno=sd_detno) where sd_code='" + ordercode + "' and sd_detno="
						+ orderdetno);
				sql = "select round(nvl(sd_sendamountia,0)/nvl(sd_sendqtyia,0),8) from SaleDetail where sd_code='" + ordercode
						+ "' and sd_detno=" + orderdetno + " and nvl(sd_sendqtyia,0)>0";
			}
		} else if ("发货平均成本[新]".equals(pricekind)) {
			if (ordercode == null || "".equals(ordercode)) {
				SqlRowList rs = queryForRowSet("select pd_inoutno,pi_date from ProdIODetail left join prodinout on pd_piid=pi_id  where pd_id ="
						+ pdid);
				if (rs.next()) {
					return "退货单[" + rs.getObject("pd_inoutno") + "]为无PO退货单，请手工核算单价！<br>";
				}
			} else {
				execute("update SaleDetail set sd_sendqtyia=(select sum(nvl(pd_outqty,0)) from prodiodetail left join prodinout on pd_piid=pi_id"
						+ " where pd_status>0 and pd_piclass = '出货单' and to_char(pi_date,'yyyymm') < '"
						+ type
						+ "' and pd_ordercode=sd_code and pd_orderdetno=sd_detno) where sd_code='"
						+ ordercode
						+ "' and sd_detno="
						+ orderdetno);
				execute("update SaleDetail set sd_sendamountia=(select sum(nvl(pd_outqty,0)*pd_price) from prodiodetail left join prodinout on pd_piid=pi_id"
						+ " where pd_status>0 and pd_piclass = '出货单'  and to_char(pi_date,'yyyymm') < '"
						+ type
						+ "' and pd_ordercode=sd_code and pd_orderdetno=sd_detno) where sd_code='"
						+ ordercode
						+ "' and sd_detno="
						+ orderdetno);
				sql = "select round(nvl(sd_sendamountia,0)/nvl(sd_sendqtyia,0),8) from SaleDetail where sd_code='" + ordercode
						+ "' and sd_detno=" + orderdetno + " and nvl(sd_sendqtyia,0)>0";
			}
		} else if ("当月总发货平均成本".equals(pricekind)) {
			sql = "select round(sum(pd_outqty*pd_price)/sum(pd_outqty),8) from prodinout,prodiodetail where pi_id=pd_piid and pi_class='出货单' and pi_statuscode='POSTED' and nvl(pd_price,0)>0 and pd_prodcode='"
					+ prodcode + "' and pi_date >= " + startdate + " and pi_date <= " + enddate;
		} else if ("按源单".equals(pricekind)) {
			int ioid = Integer.parseInt(getFieldDataByCondition("ProdIODetail", "nvl(pd_ioid,0)", "pd_id=" + pdid).toString());
			sql = "select nvl(pd_price,0) from prodinout,prodiodetail where pi_id=pd_piid and pi_class='出货单' and pi_statuscode='POSTED' and nvl(pd_price,0)>0 and pd_id="
					+ ioid + " and to_char(pi_date,'yyyymm') < '" + type + "'";
		} else if ("最新已核算单价".equals(pricekind)) {
			sql = "select round(pd_price,8) from (select pd_price from prodinout,prodiodetail where pi_id=pd_piid and pd_prodcode='"
					+ prodcode
					+ "' and pi_statuscode='POSTED' and nvl(pd_price,0)>0 and nvl(pd_outqty,0)>0 and nvl(pd_accountstatus,'')='已核算' and pi_date<="
					+ enddate + " order by pi_date desc,pi_inoutno desc) where rownum<2";
		} else if ("采购定价表".equals(pricekind)) {
			sql = "select round(ppd_price*(1-nvl(ppd_rate,0)/(100+nvl(ppd_rate,0)))*cr_rate,6) from (select ppd_price,ppd_rate,cr_rate from purchaseprice,purchasepricedetail,currencys where"
					+ " pp_id=ppd_ppid and ppd_currency=cr_name and ppd_prodcode='"
					+ prodcode
					+ "' and pp_statuscode='AUDITED' "
					+ "and ppd_statuscode='VALID' and nvl(ppd_price,0)>0 and ppd_todate>sysdate and nvl(pp_kind,' ')='采购' "
					+ "order by ppd_price*(1-nvl(ppd_rate,0)/(100+nvl(ppd_rate,0)))*cr_rate) where rownum<2";
		} else if ("开票记录".equals(pricekind)) {
			sql = "select round(abd_thisvoprice*"
					+ pirate
					+ "*(1-nvl(abd_taxrate,0)/(100+nvl(abd_taxrate,0))),8) from (select abd_thisvoprice,abd_taxrate from apbill,apbilldetail where ab_id=abd_abid and abd_pdid="
					+ pdid + " and to_char(ab_date,'yyyymm') <='" + type + "' order by ab_date desc) where rownum<2";
		} else if ("应付发票".equals(pricekind)) {
			sql = "select round(abd_thisvoprice*"
					+ pirate
					+ "*(1-nvl(abd_taxrate,0)/(100+nvl(abd_taxrate,0))),8) from (select abd_thisvoprice,abd_taxrate from apbill,apbilldetail where ab_id=abd_abid and abd_pdid="
					+ pdid + " order by ab_date desc) where rownum<2";
		} else if ("开票记录(含费用)".equals(pricekind)) {
			sql = "select round(abd_thisvoprice*"
					+ pirate
					+ "*(1-nvl(abd_taxrate,0)/(100+nvl(abd_taxrate,0))),8)+nvl(pd_fee,0) from (select abd_thisvoprice,abd_taxrate,pd_fee from apbill,apbilldetail,prodiodetail where ab_id=abd_abid and abd_pdid=pd_id and abd_pdid="
					+ pdid + " and to_char(ab_date,'yyyymm') <='" + type + "' order by ab_date desc) where rownum<2";
		} else if ("采购单".equals(pricekind)) {
			if (ordercode == null || "".equals(ordercode)) {
				SqlRowList rs = queryForRowSet("select pd_inoutno from ProdIODetail where pd_id =" + pdid);
				if (rs.next()) {
					return "采购验收单[" + rs.getObject("pd_inoutno") + "]为无采购单验收单，请手工核算单价！<br>";
				}
			} else {
				sql = "select round(pd_price*" + pirate + "*(1-nvl(pd_rate,0)/(100+nvl(pd_rate,0))),8)"
						+ " from purchase,purchasedetail where pu_id=pd_puid and pd_code='" + ordercode + "' and pd_detno=" + orderdetno;
			}
		} else if ("验收采购单价".equals(pricekind)) {
			sql = "select round(case when pi_receivecode in('02.01.028','001') then pd_customprice else pd_orderprice end *" + pirate
					+ "*(1-nvl(pd_taxrate,0)/(100+nvl(pd_taxrate,0))),8) from prodinout,prodiodetail where pi_id=pd_piid and pd_id=" + pdid;
		} else if ("验收采购单价(返利)".equals(pricekind)) {
			sql = "select case when nvl(pu_isrebates,0)=0 then round(case when pi_receivecode in('02.01.028','001') then pd_customprice else pd_orderprice end *"
					+ pirate
					+ "*(1-nvl(pd_taxrate,0)/(100+nvl(pd_taxrate,0))),8) else ROUND((nvl(pd_orderprice,0)-nvl(pd_rebatesprice,0))*"
					+ pirate
					+ "/(1+nvl(pd_taxrate,0)/100),8) end from prodinout,prodiodetail,purchase where pi_id=pd_piid and pd_ordercode=pu_code and pd_id="
					+ pdid;
		} else if ("验收采购单价(含运费)".equals(pricekind)) {
			sql = "select round(pd_orderprice*"
					+ pirate
					+ "/(1+NVL(PD_TAXRATE,0)/100)*(1+NVL(PD_DECLARERATE,0)/1000),8) from prodinout,prodiodetail where pi_id=pd_piid and pd_id="
					+ pdid;
		} else if ("验收采购单价(含费用)".equals(pricekind)) {
			sql = "select round(case when pi_receivecode in('02.01.028','001') then pd_customprice else pd_orderprice end *"
					+ pirate
					+ "*(1-nvl(pd_taxrate,0)/(100+nvl(pd_taxrate,0))),8) + nvl(pd_fee,0) from prodinout,prodiodetail where pi_id=pd_piid and pd_id="
					+ pdid;
		} else if ("验收采购单价(双单位)".equals(pricekind)) {
			sql = "select round(case when pi_receivecode in('02.01.028','001') then pd_customprice else pd_orderprice end *"
					+ pirate
					+ "*(1-nvl(pd_taxrate,0)/(100+nvl(pd_taxrate,0)))/(case when pr_unit=nvl(pr_purcunit,pr_unit) then 1 else (case when nvl(pd_purcinqty,0)=0 then 1 else nvl(pd_inqty,0)/pd_purcinqty end) end),8) from prodinout,prodiodetail,product where pi_id=pd_piid and pd_prodcode=pr_code and pd_id="
					+ pdid + " and nvl(pd_inqty,0)<>0";
		} else if ("验收采购单价(贝腾双单位)".equals(pricekind)) {
			SqlRowList rs = queryForRowSet("select pd_purcqty,pd_inqty from ProdIODetail where pd_id =" + pdid);
			if (rs.next()) {
				if (rs.getGeneralDouble("pd_purcqty") != 0 && rs.getGeneralDouble("pd_inqty") != 0) {
					sql = "select round(nvl(pd_customprice,0)*nvl(pd_purcqty,0)/nvl(pd_inqty,0)*" + pirate
							+ "/(1+nvl(pd_taxrate,0)/100),8) from prodiodetail where pd_id=" + pdid;
				} else {
					sql = "select round(pd_orderprice*" + pirate
							+ "*(1-nvl(pd_taxrate,0)/(100+nvl(pd_taxrate,0))),8) from prodiodetail where pd_id=" + pdid;
				}
			}
		} else if ("验收报关单价".equals(pricekind)) {
			sql = "select round(pd_customprice*" + pirate
					+ "*(1-nvl(pd_taxrate,0)/(100+nvl(pd_taxrate,0))),8) from prodinout,prodiodetail where pi_id=pd_piid and pd_id=" + pdid;
		} else if ("用品采购单价".equals(pricekind)) {
			sql = "select round(pd_orderprice*pi_rate/(1+nvl(pd_taxrate,0)/100),8)"
					+ "from prodinout,prodiodetail where pi_id=pd_piid and pd_id=" + pdid;
		} else if ("成本调整单".equals(pricekind)) {
			/*
			 * sql =
			 * "select round(pd_price,8) from prodinout,prodiodetail where pi_id=pd_piid and pd_prodcode='"
			 * + prodcode +
			 * "' and pi_statuscode='POSTED' and nvl(pd_price,0)>0 and nvl(pd_outqty,0)>0 and pi_date<="
			 * + enddate + " order by pi_date desc,pi_inoutno desc";
			 */
			sql = "select round(pd_price,8) from prodiodetail where 1=2";
		} else if ("其它采购出库单".equals(pricekind)) {
			sql = "select nvl(max(PD2.pd_price),0) FROM ProdInOut P1,ProdIODetail PD1,ProdInOut P2,ProdIODetail PD2 "
					+ " where p1.pi_id=pd1.pd_piid and p1.pi_class='其它采购入库单' and pd1.pd_status>10 and p2.pi_id=pd2.pd_piid and p2.pi_class='其它采购出库单' and pd2.pd_status>10 "
					+ " and PD2.pd_ioid=PD1.pd_id and ROUND(PD1.pd_price,8)<>ROUND(PD2.pd_price,8) and pd1.pd_prodcode=pd2.pd_prodcode and PD1.pd_id="
					+ pdid;
		} else if ("借货出货单".equals(pricekind)) {
			sql = "select nvl(max(PD2.pd_price),0) FROM ProdInOut P1,ProdIODetail PD1,ProdInOut P2,ProdIODetail PD2 "
					+ " where p1.pi_id=pd1.pd_piid and p1.pi_class='借货归还单' and pd1.pd_status>10 and p2.pi_id=pd2.pd_piid and p2.pi_class='借货出货单' and pd2.pd_status>10 "
					+ " and PD1.pd_ioid=PD2.pd_id and ROUND(PD1.pd_price,8)<>ROUND(PD2.pd_price,8) and pd1.pd_prodcode=pd2.pd_prodcode and PD1.pd_id="
					+ pdid;
		}
		try {
			if (sql != null) {
				getPrice = getJdbcTemplate().queryForObject(sql, Double.class);
				if (getPrice != null && getPrice > 0) { // 抓取到单价
					oldprice = Double.parseDouble(getFieldDataByCondition("ProdIODetail", "nvl(pd_avprice,0)", "pd_id=" + pdid).toString());
					execute("update prodiodetail set pd_pricekind='" + pricekind + "' where pd_id=" + pdid);
					if (oldprice.compareTo(getPrice) != 0) {
						execute("update prodiodetail set pd_avprice=" + getPrice + " where pd_id=" + pdid);
					}
				}
			}
		} catch (EmptyResultDataAccessException e) {

		}
		return null;
	}

	@Override
	public void checkProductcode(Object purCode, Object purdetno, Object proCode) {
		if (purCode != null && purCode.toString().trim().length() > 0 && !"无".equals(purCode)) {
			int count = getCountByCondition("purchasedetail left join purchase on pu_id=pd_puid", "pu_code='" + purCode + "' and pd_detno="
					+ purdetno + " and pd_prodcode='" + proCode + "'");
			if (count == 0) {
				BaseUtil.showError("采购单号:" + purCode.toString() + ",序号:" + purdetno + "和物料编号不一致,请核对后重新填写!");
			}
		}
	}

	public JSONObject newProdDefectOut(int id, String piclass, String type) {
		Employee employee = SystemSession.getUser();
		Object source = getFieldDataByCondition("ProdInOut", "pi_inoutno", "pi_id=" + id);
		Object pi_tocode = getFieldDataByCondition("ProdInOut", "pi_tocode", "pi_id=" + id);
		Map<String, Object> diffence = new HashMap<String, Object>();
		int piid = getSeqId("PRODINOUT_SEQ");
		String pi_inoutno = sGetMaxNumber(type, 2);
		diffence.put("pi_sourcecode", "'" + source + "'");
		diffence.put("pi_id", piid);
		diffence.put("pi_inoutno", "'" + pi_inoutno + "'");
		diffence.put("pi_class", "'" + piclass + "'");
		diffence.put("pi_recorddate", "sysdate");
		diffence.put("pi_recordman", "'" + employee.getEm_name() + "'");
		diffence.put("pi_updatedate", "sysdate");
		diffence.put("pi_updateman", "'" + employee.getEm_name() + "'");
		diffence.put("pi_operatorcode", "'" + employee.getEm_code() + "'");
		diffence.put("pi_invostatuscode", "'ENTERING'");
		diffence.put("pi_invostatus", "'" + BaseUtil.getLocalMessage("ENTERING") + "'");
		diffence.put("pi_statuscode", "'UNPOST'");
		diffence.put("pi_status", "'" + BaseUtil.getLocalMessage("UNPOST") + "'");
		diffence.put("pi_printstatuscode", "'UNPRINT'");
		diffence.put("pi_printstatus", "'" + BaseUtil.getLocalMessage("UNPRINT") + "'");
		diffence.put("PI_PRINTMAN", "null");
		diffence.put("PI_AUDITMAN", "null");
		diffence.put("PI_AUDITDATE", "null");
		diffence.put("PI_CHECKSTATUS", "null");
		diffence.put("PI_BCID", "0");
		diffence.put("pi_relativeplace", "'" + source + "'");
		diffence.put("pi_date", "sysdate");
		diffence.put("pi_vouchercode", "null");
		diffence.put("pi_invoicecode", "null");
		diffence.put("pi_packingcode", "null");
		diffence.put("PI_SENDSTATUS", "'待上传'");
		diffence.put("pi_date1", "null");
		diffence.put("pi_inoutman", "null");
		diffence.put("pi_billstatuscode", "null");
		diffence.put("pi_billstatus", "null");
		diffence.put("pi_tocode", "'" + pi_tocode + "'");
		// 转入主表
		copyRecord("ProdInOut", "ProdInOut", "pi_id=" + id, diffence);
		updateByCondition("ProdInOut", "pi_relativeplace='" + pi_inoutno + "'", "pi_id=" + id);
		updateByCondition("ProdInOut", "pi_pdastatus=''", "pi_id=" + piid);
		JSONObject j = new JSONObject();
		j.put("pi_id", piid);
		j.put("pi_inoutno", pi_inoutno);
		return j;
	}

	/**
	 * 批量转出货单 pi_inoutno 出库单号 pd_id 入库单明细ID qty 本次转数量
	 */
	@Override
	public void toAppointedProdDefectOut(int pi_id, int pd_id, double qty, int detno) {
		Object[] pi = getFieldsDataByCondition("ProdInOut", new String[] { "pi_id", "pi_class", "pi_inoutno" }, "pi_id=" + pi_id);
		Map<String, Object> diffence = new HashMap<String, Object>();
		diffence.put("pd_piid", pi_id);
		diffence.put("pd_inoutno", "'" + pi[2] + "'");
		diffence.put("pd_piclass", "'" + pi[1] + "'");
		diffence.put("pd_auditstatus", "'ENTERING'");
		diffence.put("pd_status", 0);
		diffence.put("pd_inqty", 0);
		diffence.put("pd_ioid", pd_id);
		diffence.put("pd_pdno", detno);
		diffence.put("pd_id", getSeqId("PRODIODETAIL_SEQ"));
		diffence.put("pd_outqty", qty);
		diffence.put("pd_yqty", 0);
		diffence.put("pd_prodmadedate", "null");
		diffence.put("pd_invoqty", 0);
		diffence.put("pd_showinvoqty", 0);
		diffence.put("pd_turngsqty", 0);
		diffence.put("pd_gsqty", 0);
		diffence.put("pd_ycheck", 0);
		diffence.put("pd_checkqty", 0);
		diffence.put("pd_turnesqty", 0);
		diffence.put("pd_esqty", 0);
		copyRecord("ProdIODetail", "ProdIODetail", "pd_id=" + pd_id, diffence);
	}

	/**
	 * 不良品入单转不良品出单 pi_inoutno 出库单号 pd_id 入库单明细ID qty 本次转数量
	 */
	@Override
	public void toAppointedProdOtherOut(int pi_id, int pd_id, double qty, int detno) {
		Object[] pi = getFieldsDataByCondition("ProdInOut", new String[] { "pi_id", "pi_class", "pi_inoutno" }, "pi_id=" + pi_id);
		Object[] source = getFieldsDataByCondition("ProdIODetail left join ProdInOut on pd_piid=pi_id", new String[] { "pd_inoutno",
				"pd_pdno" }, "pd_id=" + pd_id);
		Map<String, Object> diffence = new HashMap<String, Object>();
		diffence.put("pd_piid", pi_id);
		diffence.put("pd_inoutno", "'" + pi[2] + "'");
		diffence.put("pd_piclass", "'" + pi[1] + "'");
		diffence.put("pd_auditstatus", "'ENTERING'");
		diffence.put("pd_status", 0);
		diffence.put("pd_inqty", 0);
		diffence.put("pd_ioid", pd_id);
		diffence.put("pd_pdno", detno);
		diffence.put("pd_id", getSeqId("PRODIODETAIL_SEQ"));
		diffence.put("pd_outqty", qty);
		diffence.put("pd_yqty", 0);
		diffence.put("pd_prodmadedate", "null");
		diffence.put("pd_ordercode", "'" + source[0] + "'");
		diffence.put("pd_orderdetno", source[1]);
		diffence.put("pd_invoqty", 0);
		diffence.put("pd_showinvoqty", 0);
		diffence.put("pd_turngsqty", 0);
		diffence.put("pd_gsqty", 0);
		diffence.put("pd_ycheck", 0);
		diffence.put("pd_checkqty", 0);
		diffence.put("pd_turnesqty", 0);
		diffence.put("pd_esqty", 0);
		copyRecord("ProdIODetail", "ProdIODetail", "pd_id=" + pd_id, diffence);
	}

	// 借货出货单转出货单
	@Override
	public void toAppointedProdSaleOut(int pi_id, int pd_id, double qty, int detno) {
		Object[] pi = getFieldsDataByCondition("ProdInOut", new String[] { "pi_id", "pi_class", "pi_inoutno" }, "pi_id=" + pi_id);
		Map<String, Object> diffence = new HashMap<String, Object>();
		diffence.put("pd_piid", pi_id);
		diffence.put("pd_inoutno", "'" + pi[2] + "'");
		diffence.put("pd_piclass", "'" + pi[1] + "'");
		diffence.put("pd_auditstatus", "'ENTERING'");
		diffence.put("pd_status", 0);
		diffence.put("pd_inqty", 0);
		diffence.put("pd_ioid", pd_id);
		diffence.put("pd_pdno", detno);
		diffence.put("pd_batchcode", "null");
		diffence.put("pd_ordercode", "null");
		diffence.put("pd_orderdetno", "null");
		diffence.put("pd_batchid", 0);
		diffence.put("pd_orderid", 0);
		diffence.put("pd_yqty", 0);
		diffence.put("pd_id", getSeqId("PRODIODETAIL_SEQ"));
		diffence.put("pd_outqty", qty);
		diffence.put("pd_prodmadedate", "null");
		diffence.put("pd_invoqty", 0);
		diffence.put("pd_showinvoqty", 0);
		diffence.put("pd_turngsqty", 0);
		diffence.put("pd_gsqty", 0);
		diffence.put("pd_ycheck", 0);
		diffence.put("pd_checkqty", 0);
		diffence.put("pd_turnesqty", 0);
		diffence.put("pd_esqty", 0);
		copyRecord("ProdIODetail", "ProdIODetail", "pd_id=" + pd_id, diffence);
	}

	// 销售退货单单转出货单
	@Override
	public void toAppointedProdSaleReturnOut(int pi_id, int pd_id, double qty, int detno) {
		Object[] pi = getFieldsDataByCondition("ProdInOut", new String[] { "pi_id", "pi_class", "pi_inoutno" }, "pi_id=" + pi_id);
		Map<String, Object> diffence = new HashMap<String, Object>();
		diffence.put("pd_piid", pi_id);
		diffence.put("pd_inoutno", "'" + pi[2] + "'");
		diffence.put("pd_piclass", "'" + pi[1] + "'");
		diffence.put("pd_auditstatus", "'ENTERING'");
		diffence.put("pd_status", 0);
		diffence.put("pd_inqty", 0);
		diffence.put("pd_ioid", pd_id);
		diffence.put("pd_pdno", detno);
		diffence.put("pd_batchcode", "null");
		diffence.put("pd_batchid", 0);
		diffence.put("pd_orderid", 0);
		diffence.put("pd_yqty", 0);
		diffence.put("pd_id", getSeqId("PRODIODETAIL_SEQ"));
		diffence.put("pd_outqty", qty);
		diffence.put("pd_prodmadedate", "null");
		diffence.put("pd_invoqty", 0);
		diffence.put("pd_showinvoqty", 0);
		diffence.put("pd_turngsqty", 0);
		diffence.put("pd_gsqty", 0);
		diffence.put("pd_ycheck", 0);
		diffence.put("pd_checkqty", 0);
		diffence.put("pd_turnesqty", 0);
		diffence.put("pd_esqty", 0);
		copyRecord("ProdIODetail", "ProdIODetail", "pd_id=" + pd_id, diffence);
	}

	/**
	 * 拨入单转拨出单 pi_inoutno 出库单号 pd_id 入库单明细ID qty 本次转数量
	 */
	@Override
	public void toAppointedAppropriationOut(int pi_id, int pd_id, double qty, int detno) {
		Object[] pi = getFieldsDataByCondition("ProdInOut", new String[] { "pi_id", "pi_class", "pi_inoutno" }, "pi_id=" + pi_id);
		Map<String, Object> diffence = new HashMap<String, Object>();
		diffence.put("pd_piid", pi_id);
		diffence.put("pd_inoutno", "'" + pi[2] + "'");
		diffence.put("pd_piclass", "'" + pi[1] + "'");
		diffence.put("pd_auditstatus", "'ENTERING'");
		diffence.put("pd_status", 0);
		diffence.put("pd_inqty", 0);
		diffence.put("pd_ioid", pd_id);
		diffence.put("pd_pdno", detno);
		diffence.put("pd_id", getSeqId("PRODIODETAIL_SEQ"));
		diffence.put("pd_outqty", qty);
		diffence.put("pd_inwhcode", "null");
		diffence.put("pd_inwhname", "null");
		diffence.put("pd_batchcode", "null");
		diffence.put("pd_batchid", 0);
		diffence.put("pd_yqty", 0);
		diffence.put("pd_prodmadedate", "null");
		diffence.put("pd_invoqty", 0);
		diffence.put("pd_showinvoqty", 0);
		diffence.put("pd_turngsqty", 0);
		diffence.put("pd_gsqty", 0);
		diffence.put("pd_ycheck", 0);
		diffence.put("pd_checkqty", 0);
		diffence.put("pd_turnesqty", 0);
		diffence.put("pd_esqty", 0);
		copyRecord("ProdIODetail", "ProdIODetail", "pd_id=" + pd_id, diffence);
	}

	/**
	 * 批量转入库单 pi_inoutno 入库单号 pd_id 出库单明细ID qty 本次转数量
	 */
	@Override
	public void toAppointedProdDefectIn(int pi_id, int pd_id, double qty, int detno) {
		Object[] pi = getFieldsDataByCondition("ProdInOut", new String[] { "pi_id", "pi_class", "pi_inoutno" }, "pi_id=" + pi_id);
		Object[] pd = getFieldsDataByCondition("ProdIODetail", new String[] { "pd_batchcode" }, "pd_id=" + pd_id);
		Map<String, Object> diffence = new HashMap<String, Object>();
		diffence.put("pd_piid", pi_id);
		diffence.put("pd_inoutno", "'" + pi[2] + "'");
		diffence.put("pd_piclass", "'" + pi[1] + "'");
		diffence.put("pd_auditstatus", "'ENTERING'");
		diffence.put("pd_status", 0);
		diffence.put("pd_outqty", 0);
		diffence.put("pd_ioid", pd_id);
		diffence.put("pd_qcid", 0);// qcid重置
		diffence.put("pd_pdno", detno);
		diffence.put("pd_id", getSeqId("PRODIODETAIL_SEQ"));
		diffence.put("pd_inqty", qty);
		diffence.put("pd_yqty", 0);
		diffence.put("pd_prodmadedate", "null");
		diffence.put("pd_batchcode", "null");
		diffence.put("pd_batchid", 0);
		diffence.put("pd_invoqty", 0);
		diffence.put("pd_showinvoqty", 0);
		diffence.put("pd_turngsqty", 0);
		diffence.put("pd_gsqty", 0);
		diffence.put("pd_ycheck", 0);
		diffence.put("pd_checkqty", 0);
		diffence.put("pd_turnesqty", 0);
		diffence.put("pd_esqty", 0);
		diffence.put("pd_model", "'" + pd[0] + "'"); // 出货批号
		copyRecord("ProdIODetail", "ProdIODetail", "pd_id=" + pd_id, diffence);
	}

	/**
	 * 批量转入库单 pi_inoutno 入库单号 pd_id 出库单明细ID qty 本次转数量
	 */
	@Override
	public void toAppointedProdSaleReturn(int pi_id, int pd_id, double qty, int detno) {
		Object[] pi = getFieldsDataByCondition("ProdInOut", new String[] { "pi_id", "pi_class", "pi_inoutno" }, "pi_id=" + pi_id);
		Object[] source = getFieldsDataByCondition("ProdIODetail", new String[] { "pd_inoutno", "pd_pdno", "pd_batchcode" }, "pd_id="
				+ pd_id);
		Map<String, Object> diffence = new HashMap<String, Object>();
		String num = sGetMaxNumber("BatchNum", 1);
		diffence.put("pd_piid", pi_id);
		diffence.put("pd_inoutno", "'" + pi[2] + "'");
		diffence.put("pd_piclass", "'" + pi[1] + "'");
		diffence.put("pd_auditstatus", "'ENTERING'");
		diffence.put("pd_status", 0);
		diffence.put("pd_outqty", 0);
		diffence.put("pd_ioid", pd_id);
		diffence.put("pd_qcid", 0);// qcid重置
		diffence.put("pd_pdno", detno);
		diffence.put("pd_id", getSeqId("PRODIODETAIL_SEQ"));
		diffence.put("pd_inqty", qty);
		diffence.put("pd_yqty", 0);
		diffence.put("pd_batchcode", "'" + source[2] + "'||'-'||'" + num + "'");
		diffence.put("pd_batchid", 0);
		diffence.put("pd_prodmadedate", "null");
		diffence.put("pd_invoqty", 0);
		diffence.put("pd_showinvoqty", 0);
		diffence.put("pd_turngsqty", 0);
		diffence.put("pd_gsqty", 0);
		diffence.put("pd_ycheck", 0);
		diffence.put("pd_snid", "null");
		diffence.put("pd_ycheck", 0);
		diffence.put("pd_checkqty", 0);
		diffence.put("pd_turnesqty", 0);
		diffence.put("pd_esqty", 0);
		diffence.put("pd_model", "'" + source[2] + "'"); // 出货批号
		copyRecord("ProdIODetail", "ProdIODetail", "pd_id=" + pd_id, diffence);
	}

	/**
	 * 批量转入库单 pi_inoutno 入库单号 pd_id 出库单明细ID qty 本次转数量
	 */
	@Override
	public void toAppointedProdOutReturn(int pi_id, int pd_id, double qty, int detno) {
		Object[] pi = getFieldsDataByCondition("ProdInOut", new String[] { "pi_id", "pi_class", "pi_inoutno" }, "pi_id=" + pi_id);
		Object[] source = getFieldsDataByCondition("ProdIODetail", new String[] { "pd_inoutno", "pd_pdno", "pd_batchcode" }, "pd_id="
				+ pd_id);
		String num = sGetMaxNumber("BatchNum", 1);
		Map<String, Object> diffence = new HashMap<String, Object>();
		diffence.put("pd_piid", pi_id);
		diffence.put("pd_inoutno", "'" + pi[2] + "'");
		diffence.put("pd_piclass", "'" + pi[1] + "'");
		diffence.put("pd_auditstatus", "'ENTERING'");
		diffence.put("pd_status", 0);
		diffence.put("pd_outqty", 0);
		diffence.put("pd_ioid", pd_id);
		diffence.put("pd_ordercode", "'" + source[0] + "'");
		diffence.put("pd_orderdetno", source[1]);
		diffence.put("pd_batchcode", "'" + source[2] + "'||'-'||'" + num + "'");
		diffence.put("pd_batchid", 0);
		diffence.put("pd_qcid", 0);// qcid重置
		diffence.put("pd_pdno", detno);
		diffence.put("pd_id", getSeqId("PRODIODETAIL_SEQ"));
		diffence.put("pd_inqty", qty);
		diffence.put("pd_yqty", 0);
		diffence.put("pd_prodmadedate", "null");
		diffence.put("pd_invoqty", 0);
		diffence.put("pd_showinvoqty", 0);
		diffence.put("pd_turngsqty", 0);
		diffence.put("pd_gsqty", 0);
		diffence.put("pd_ycheck", 0);
		diffence.put("pd_checkqty", 0);
		diffence.put("pd_turnesqty", 0);
		diffence.put("pd_esqty", 0);
		diffence.put("pd_model", "'" + source[2] + "'"); // 出货批号
		copyRecord("ProdIODetail", "ProdIODetail", "pd_id=" + pd_id, diffence);
	}

	@Override
	public boolean resPostSaleProdIn(int id) {
		SqlRowList rs = queryForRowSet("select pi_relativeplace,pi_class from prodinout where pi_id=?", id);
		if (rs.next()) {
			SqlRowList rs1 = queryForRowSet("select pi_id from prodinout where pi_inoutno=? and pi_class in ('拨入单','销售拨入单')",
					rs.getString("pi_relativeplace"));
			Object inId = null;
			if (rs1.next()) {
				if ("拨出单".equals(rs.getString("pi_class"))) {
					prodInOutService.resPostProdInOut("ProdInOut!AppropriationIn", rs1.getInt("pi_id"));
				} else if ("销售拨出单".equals(rs.getString("pi_class"))) {
					prodInOutService.resPostProdInOut("ProdInOut!SaleAppropriationIn", rs1.getInt("pi_id"));
					inId = getFieldDataByCondition("prodinout", "pi_id", "pi_inoutno='" + rs.getString("pi_relativeplace")
							+ "' and pi_class='销售拨入单'");
				}
				deleteByCondition("ProdInOut", "pi_id=" + rs1.getInt("pi_id") + " and pi_statuscode='UNPOST' ");
				deleteByCondition("ProdIODetail", "pd_piid=" + rs1.getInt("pi_id") + " and pd_status=0 ");
			}
			if (inId != null)
				return false;
		}
		return true;
	}

	static final String TURNPRODIODETAIL = "SELECT * FROM ProdIODetail left join prodinout on pd_piid=pi_id" + " WHERE pd_id=?";

	public JSONObject newMRB(int pd_id, double qty) {
		Employee employee = SystemSession.getUser();
		SqlRowList list = queryForRowSet(TURNPRODIODETAIL, pd_id);
		int mr_id = 0;
		String mr_code = null;
		SqlMap map = null;
		if (list.next()) {
			map = new SqlMap("qua_mrb");
			mr_id = getSeqId("QUA_MRB_SEQ");
			mr_code = sGetMaxNumber("MRB", 2);
			map.set("MR_VECODE", list.getString("pi_inoutno"));
			map.set("MR_ID", mr_id);
			map.set("MR_CODE", mr_code);
			map.set("MR_VEID", pd_id);
			map.set("MR_RECORDER", employee.getEm_name());
			map.set("mr_departmentcode", employee.getEm_departmentcode());
			map.set("mr_departmentname", employee.getEm_depart());
			map.set("MR_PRODCODE", list.getString("pd_prodcode"));
			map.set("MR_INQTY", qty);
			map.set("MR_STATUSCODE", "ENTERING");
			map.set("MR_STATUS", BaseUtil.getLocalMessage("ENTERING"));
			map.set("MR_REMARK", list.getString("pd_remark"));
			map.set("MR_PUCODE", list.getString("pd_ordercode"));
			map.set("MR_PUDETNO", list.getInt("pd_orderdetno"));
			// map.set("MR_DEPARTMENTCODE",
			// list.getString("pi_departmentcode"));
			// map.set("MR_DEPARTMENTNAME",
			// list.getString("pi_departmentname"));
			map.set("MR_VENDCODE", list.getString("pi_cardcode"));
			map.set("MR_VENDNAME", list.getString("pi_title"));
			map.set("mr_datein", list.getObject("pi_date"));
			map.set("mr_whman", list.getObject("pi_cgy"));
			map.set("mr_cop", list.getObject("pi_cop"));
			map.set("mr_qctype", list.getObject("pd_qctype"));
			map.set("MR_SHCODE", list.getObject("pi_sendcode"));
			map.set("mr_batchcode", list.getObject("pd_batchcode"));
			map.set("mr_checkstatuscode", "UNAPPROVED");
			map.set("mr_checkstatus", BaseUtil.getLocalMessage("UNAPPROVED"));
			map.execute();
		}
		execute("update QUA_MRB set MR_PRID=(select pr_id from product where pr_code=mr_prodcode) where mr_id=" + mr_id);
		execute("update QUA_MRB set (mr_contact)=(select ve_contact from vendor where ve_code=mr_vendcode) where mr_id=" + mr_id);
		execute("update QUA_MRB set mr_buyerman=(select pu_buyername from purchase where pu_code=MR_PUCODE) where mr_id=" + mr_id);
		execute("update QUA_MRB set MR_INDATE=sysdate,MR_DATE=sysdate where mr_id=" + mr_id);
		execute("update QUA_MRB set (mr_dispose,mr_remark)=(select ve_code,ve_remark from qua_verifyapplydetail left join qua_verifyapplydetaildet on ve_id=ved_veid left join ProdIODetail on pd_qcid=ved_id where pd_id="
				+ pd_id + ") where mr_id=" + mr_id);
		JSONObject j = new JSONObject();
		j.put("mr_id", mr_id);
		j.put("mr_code", mr_code);
		return j;
	}

	@Override
	public void restoreSNYqty(double uqty, Object sndid) {
		Object[] id = getFieldsDataByCondition("sendnotifydetail left join sendnotify on snd_snid=sn_id", new String[] { "snd_yqty",
				"snd_outqty", "snd_code", "snd_pdno", "snd_ordercode", "snd_orderdetno" }, "snd_id=" + sndid);
		if (id != null) {
			Object y = getFieldDataByCondition("ProdIODetail", "sum(nvl(pd_outqty,0))", "pd_orderid=" + sndid + " and nvl(pd_snid,0) > 0 ");
			Object r = 0;
			if (id[4] != null && !"".equals(id[4])) {
				Object sdid = getFieldDataByCondition("SaleDetail", "sd_id", "sd_code='" + id[4] + "' and sd_detno='" + id[5] + "'");
				if (sdid == null) {
					BaseUtil.showError("订单[" + id[4] + "]序号[" + id[5] + "]不存在！");
				} else {
					r = getFieldDataByCondition("ProdIODetail left join ProdInOut on pd_piid=pi_id", "sum(nvl(pd_inqty,0))",
							"pd_piclass='销售退货单' and nvl(pd_status,0)>0 and pd_ordercode='" + id[4] + "' and pd_orderdetno=" + id[5]);
				}

			}
			y = y == null ? 0 : y;
			r = r == null ? 0 : r;
			if (NumberUtil.formatDouble(Double.parseDouble(y.toString()) + uqty, 2) > NumberUtil.formatDouble(
					Double.valueOf(id[1].toString()), 2)) {
				BaseUtil.showError("通知单号[" + id[2] + "]行号[" + id[3] + "]数量超发,原数量[" + id[1] + "]已转数为[" + y + "]本次数量[" + uqty + "].请修改数量!");
			} else {
				updateByCondition("sendnotifydetail", "snd_yqty=nvl(snd_yqty,0)+" + uqty, "snd_id=" + sndid);
				updateSendNotifyStatus(sndid);
			}
		} else {
			BaseUtil.showError("通知单不存在,请核对后重新录入!");
		}
	}

	@Override
	public void restoreSaleYqty(double uqty, String sdcode, Integer sddetno) {
		Object[] id = getFieldsDataByCondition("saledetail left join sale on sd_said=sa_id left join SaleKind on sa_kind=sk_name",
				new String[] { "sd_id", "sd_yqty", "sd_qty", "sk_outtype", "sa_kind" }, "sa_code='" + sdcode + "' and sd_detno=" + sddetno);
		Object y = getFieldDataByCondition("ProdIODetail", "sum(nvl(pd_outqty,0))", "pd_ordercode='" + sdcode + "' and pd_orderdetno="
				+ sddetno + " and nvl(pd_snid,0)=0 and pd_piclass in ('出货单','其它出库单','换货出库单')");
		Object r = getFieldDataByCondition("ProdIODetail left join ProdInOut on pd_piid=pi_id", "sum(nvl(pd_inqty,0))",
				"pd_piclass='销售退货单' and nvl(pd_status,0)>0 and pd_ordercode='" + sdcode + "' and pd_orderdetno=" + sddetno);
		y = y == null ? 0 : y;
		r = r == null ? 0 : r;
		if (id != null) {
			if (id[3] != null && "TURNSN".equals(id[3])) {
				BaseUtil.showError("订单[" + sdcode + "]订单类型为[" + id[4] + "]出货类型为[转出货通知单]，不允许直接在明细新增订单！");
			}
			if (NumberUtil.formatDouble(Double.parseDouble(y.toString()) + uqty, 2) > NumberUtil.formatDouble(
					Double.valueOf(id[2].toString()) + Double.parseDouble(r.toString()), 2)) {
				BaseUtil.showError("销售单号[" + sdcode + "]订单序号[" + sddetno + "]数量超发,原数量为:" + id[2].toString() + ",已转出货数[" + y + "]销售退货数[" + r
						+ "]本次数量[" + uqty + "].请修改数量!");
			} else {
				updateByCondition("saledetail", "sd_yqty=nvl(sd_yqty,0)+" + uqty, "sd_id=" + id[0]);
				updateSaleStatus(id[0]);
			}
		} else {
			BaseUtil.showError("销售单号[" + sdcode + "]订单序[:" + sddetno + "]不存在,请核对后重新修改!");
		}
	}

	@Override
	public void restoreSNWithQty(int pdid, Double uqty, Object sndid, Object sdcode) {
		Object qty = 0;
		Object aq = 0;
		Object r = 0;
		uqty = Math.abs(uqty);
		// 判断数量是否超出销售数量
		Object[] sns = getFieldsDataByCondition("SendNotifyDetail", new String[] { "snd_outqty", "snd_code", "snd_pdno" }, "snd_id="
				+ sndid);
		Object[] pid = getFieldsDataByCondition("ProdIODetail", new String[] { "pd_orderid", "pd_ordercode", "pd_orderdetno", "pd_snid",
				"nvl(pd_sdid,0)" }, "pd_id=" + pdid);
		if (pid != null && Integer.parseInt(pid[3].toString()) > 0) {
			if (pid[1] != null) {
				if (pid[3] != null && sdcode != null && !"0".equals(pid[3]) && !(pid[1]).equals(sdcode)) {
					BaseUtil.showError("由通知单生成的出货单不允许修改订单号或订单序号！");
				}
				r = getFieldDataByCondition("ProdIODetail left join ProdInOut on pd_piid=pi_id", "sum(pd_inqty)",
						"pd_piclass='销售退货单' and pi_statuscode='POSTED' and pd_ordercode='" + pid[1] + "' and pd_orderdetno=" + pid[2]);
			}
			qty = getFieldDataByCondition("ProdIODetail LEFT JOIN ProdInOut ON pi_id=pd_piid", "sum(pd_outqty)", "pd_orderid=" + sndid
					+ " AND pd_id <>" + pdid + " and nvl(pd_snid,0) > 0 and pd_piclass<>'拨出单'");
			qty = qty == null ? 0 : qty;
			r = r == null ? 0 : r;
			aq = sns[0] == null ? 0 : sns[0];
			if (NumberUtil.formatDouble(Double.parseDouble(aq.toString()), 6) < NumberUtil.formatDouble(Double.parseDouble(qty.toString())
					+ uqty, 6)) {
				BaseUtil.showError("通知单[" + sns[1] + "]序号[" + sns[2] + "]的新数量超出原通知单数量,超出数量:"
						+ (Double.parseDouble(qty.toString()) + uqty - Double.parseDouble(aq.toString())));
			} else {
				updateByCondition("ProdIODetail", "pd_outqty=" + uqty, "pd_id=" + pdid);
				updateByCondition("SendNotifyDetail",
						"snd_yqty=" + NumberUtil.formatDouble((Double.parseDouble(qty.toString()) + uqty), 4), "snd_id=" + sndid);
				updateSendNotifyStatus(sndid);
			}
		}

	}

	@Override
	public void restoreSaleWithQty(int pdid, Double uqty, Object sdcode, Object sddetno) {
		Object qty = 0;
		Object aq = 0;
		Object r = 0;
		uqty = Math.abs(uqty);
		// 判断数量是否超出销售数量
		execute("update ProdIODetail set pd_sdid=(select sd_id from saledetail where pd_ordercode=sd_code and pd_orderdetno=sd_detno) where nvl(pd_ordercode,' ')<>' ' and pd_id="
				+ pdid);
		Object[] snd = getFieldsDataByCondition("ProdIODetail", "pd_sdid,pd_ordercode,pd_orderdetno", "pd_id=" + pdid);
		// 可能存在有订单转过来的通知单 手动去修改订单和序号 导致无法还原数量
		if (sdcode == null || " ".equals(sdcode))
			sddetno = 0;
		Object sdid = getFieldDataByCondition("SaleDetail", "sd_id", "sd_code='" + sdcode + "' and sd_detno='" + sddetno + "'");
		if (snd != null && Integer.parseInt(snd[0].toString()) > 0) {
			if (sdid != null && !"0".equals(sdid) && Integer.parseInt(snd[0].toString()) != Integer.parseInt(sdid.toString())) {
				BaseUtil.showError("由订单生成的出货单不允许修改订单号或订单序号!");
			}
			qty = getFieldDataByCondition("ProdIODetail LEFT JOIN ProdInOut ON pi_ID=pd_piid", "sum(pd_outqty)", "pd_sdid=" + snd[0]
					+ " AND pd_id <>" + pdid + " and pd_piclass in ('出货单','其它出库单','换货出库单') and nvl(pd_snid,0)=0");
			r = getFieldDataByCondition("ProdIODetail left join ProdInOut on pd_piid=pi_id", "sum(pd_inqty)",
					"pd_piclass='销售退货单' and pi_statuscode='POSTED' and pd_ordercode='" + snd[1] + "' and pd_orderdetno=" + snd[2]);
			qty = qty == null ? 0 : qty;
			r = r == null ? 0 : r;
			aq = getFieldDataByCondition("SaleDetail", "sd_qty", "sd_id=" + snd[0]);
			if (Double.parseDouble(aq.toString()) < NumberUtil.formatDouble(
					Double.parseDouble(qty.toString()) + uqty - Double.parseDouble(r.toString()), 6)) {
				BaseUtil.showError("订单"
						+ snd[1]
						+ "序号["
						+ snd[2]
						+ "]的新数量超出原销售数量,超出数量:"
						+ (Double.parseDouble(qty.toString()) + uqty - Double.parseDouble(r.toString()) - Double.parseDouble(aq.toString())));
			} else {
				updateByCondition("ProdIODetail", "pd_outqty=" + uqty, "pd_id=" + pdid);
				updateByCondition(
						"SaleDetail",
						"sd_yqty="
								+ NumberUtil.formatDouble((Double.parseDouble(qty.toString()) + uqty - Double.parseDouble(r.toString())), 4),
						"sd_id=" + sdid);
				updateSaleStatus(sdid);
			}
		}

	}

	public void updateSaleStatus(Object sdid) {
		Object said = getFieldDataByCondition("SaleDetail", "sd_said", "sd_id=" + sdid);
		if (said != null) {
			int count = getCountByCondition("SaleDetail", "sd_said=" + said);
			int yCount = getCountByCondition("SaleDetail", "sd_said=" + said
					+ " and nvl(sd_sendqty,0)=nvl(sd_qty,0) and nvl(sd_sendqty,0)>=0");
			int xCount = getCountByCondition("SaleDetail", "sd_said=" + said + " and nvl(sd_sendqty,0)=0");
			String status = "PARTOUT";
			if (yCount == count) {
				status = "TURNOUT";
			}
			if (xCount == count) {
				status = "";
			}
			updateByCondition("Sale", "Sa_SENDSTATUSCODE='" + status + "',Sa_SENDSTATUS='" + BaseUtil.getLocalMessage(status) + "'",
					"sa_id=" + said);
		}
	}

	public void updateSendNotifyStatus(Object sndid) {
		Object[] snid = getFieldsDataByCondition("SendNotifyDetail", new String[] { "snd_snid", "snd_ordercode", "snd_orderdetno" },
				"snd_id=" + sndid);
		if (snid != null && snid[0] != null && !"0".equals(snid[0])) {
			int count = getCountByCondition("SendNotifyDetail", "snd_snid=" + snid[0]);
			int yCount = getCountByCondition("SendNotifyDetail", "snd_snid=" + snid[0]
					+ " and nvl(snd_yqty,0)=nvl(snd_outqty,0) and nvl(snd_yqty,0)>=0");
			int xCount = getCountByCondition("SendNotifyDetail", "snd_snid=" + snid[0] + " and nvl(snd_yqty,0)=0");
			String status = "PARTOUT";
			if (yCount == count) {
				status = "TURNOUT";
			}
			if (xCount == count) {
				status = "";
			}
			updateByCondition("SendNotify", "SN_SENDSTATUSCODE='" + status + "',SN_SENDSTATUS='" + BaseUtil.getLocalMessage(status) + "'",
					"sn_id=" + snid[0]);
			if (snid[1] != null && !"".equals(snid[1])) {
				Object sdid = getFieldDataByCondition("SaleDetail", "sd_id", "sd_code='" + snid[1] + "' and sd_detno=" + snid[2]);
				if (sdid != null) {
					updateSaleStatus(sdid);
				}
			}
		}
	}

	@Override
	public void restorePurcYqty(double uqty, String pdcode, Integer pddetno) {
		Object[] id = getFieldsDataByCondition(
				"Purchasedetail left join Purchase on pd_puid=pu_id left join PurchaseKind on pu_kind=pk_name", new String[] { "pd_id",
						"pd_yqty", "pd_qty", "nvl(pk_allowin,0)", "pu_kind", "pu_id" }, "pu_code='" + pdcode + "' and pd_detno=" + pddetno);
		Object y = getFieldDataByCondition("ProdIODetail", "sum(nvl(pd_inqty,0))", "pd_ordercode='" + pdcode + "' and pd_orderdetno="
				+ pddetno + " and nvl(pd_qcid,0)=0 and nvl(pd_anid,0)=0 and pd_piclass in ('采购验收单')");
		Object r = getFieldDataByCondition("ProdIODetail left join ProdInOut on pd_piid=pi_id", "sum(nvl(pd_outqty,0))",
				"pd_piclass='采购验退单' and nvl(pd_status,0)>0 and pd_ordercode='" + pdcode + "' and pd_orderdetno=" + pddetno);
		y = y == null ? 0 : y;
		r = r == null ? 0 : r;
		if (id != null) {
			if (NumberUtil.formatDouble(Double.parseDouble(y.toString()) + uqty, 2) > NumberUtil.formatDouble(
					Double.valueOf(id[2].toString()) + Double.parseDouble(r.toString()), 2)) {
				BaseUtil.showError("采购单号[" + pdcode + "]序号[" + pddetno + "]数量超发,原数量为:" + id[2].toString() + ",已转出货数[" + y + "]验退数[" + r
						+ "]本次数量[" + uqty + "].请修改数量!");
			} else {
				updateByCondition("purchasedetail", "pd_yqty=nvl(pd_yqty,0)+" + uqty, "pd_id=" + id[0]);
				Object puid = id[5];
				if (puid != null) {
					int count = getCountByCondition("PurchaseDetail", "pd_puid=" + puid);
					int yCount = getCountByCondition("PurchaseDetail", "pd_puid=" + puid
							+ " and nvl(pd_yqty,0)=nvl(pd_qty,0) and nvl(pd_yqty,0)>0");
					int xCount = getCountByCondition("PurchaseDetail", "pd_puid=" + puid + " and nvl(pd_yqty,0)=0");
					String status = "PART2IN";
					if (yCount == count) {
						status = "TURNIN";
					}
					if (xCount == count) {
						status = "";
					}
					updateByCondition("Purchase", "pu_turnstatuscode='" + status + "',pu_turnstatus='" + BaseUtil.getLocalMessage(status)
							+ "'", "pu_id=" + puid);
				}
			}
		} else {
			BaseUtil.showError("采购单号[" + pdcode + "]序号[:" + pddetno + "]不存在,请核对后重新修改!");
		}
	}

	@Override
	public boolean isOut(String caller) {
		SqlRowList rs = queryForRowSet("select ds_inorout from DOCUMENTSETUP where ds_table=?", caller);
		if (rs.next()) {
			return "-IN".equals(rs.getString(1)) || "OUT".equals(rs.getString(1));
		}
		return false;
	}
}
