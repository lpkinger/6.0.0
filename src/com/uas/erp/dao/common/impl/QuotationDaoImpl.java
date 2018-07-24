package com.uas.erp.dao.common.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.dao.common.QuotationDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Key;

@Repository
public class QuotationDaoImpl extends BaseDao implements QuotationDao {
	static final String INSERTBASESALE = "INSERT INTO SALE(sa_id,sa_code,sa_status,sa_statuscode"
			+ ",sa_recorderid,sa_recorddate,sa_recorder) VALUES (?,?,?,?,?,?,?)";
	static final String INSERTPURCWITHCUST = "INSERT INTO sale(sa_id,sa_code,sa_status,sa_statuscode,sa_recorderid"
			+ ",sa_recorddate,sa_custid, sa_custcode,sa_custname,sa_sellerid,sa_seller,sa_printstatus,sa_recorder"
			+ ",sa_sellercode,sa_printstatuscode, sa_currency, sa_paymentscode, sa_rate, sa_source) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'正常')";
	static final String TURNBASESALEDETAIL = "SELECT qd_prodcode,qd_description,qd_packid,qd_qty,qd_cartons,qd_cbm,qd_gw,qd_nw,qd_price,qd_pricetype"
			+ ",qd_discount,qd_delivery,qd_custprodcode,qd_vendcode,qd_factprice,qd_rate,qd_assqty,qd_yqty,qu_id,qu_code"
			+ " FROM quotationdetail left join quotation on qd_quid=qu_id WHERE qd_id=?";
	static final String INSERTSALEDETAIL = "INSERT INTO saledetail(sd_id,sd_said,sd_detno,sd_sourceid,sd_source,sd_prodcode,sd_description"
			+ ",sd_packid,sd_qty,sd_cartons,sd_cbm,sd_gw,sd_nw,sd_price,sd_pricetype,sd_discount,sd_delivery,sd_custprodcode,sd_vendcode,sd_costprice"
			+ ",sd_taxrate,sd_assqty,sd_statuscode,sd_status,sd_code) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String GETSABYSOURCE = "select sa_code from sale where sa_source=(select qu_code from quotation left join quotationdetail on qu_id=qd_quid where qd_id=?)";

	@Autowired
	private TransferRepository transferRepository;
	
	@Override
	@Transactional
	public int turnSale(int id) {
		Key key = transferRepository.transfer("Quotation", id);
		int said = key.getId();
		// 转入明细
		transferRepository.transferDetail("Quotation", id, key);
		return said;
	}

	@Override
	public String newSale() {
		Employee employee = SystemSession.getUser();
		int said = getSeqId("SALE_SEQ");
		String code = sGetMaxNumber("Sale", 2);
		boolean bool = execute(INSERTBASESALE, new Object[] { said, code, BaseUtil.getLocalMessage("ENTERING"), "ENTERING",
				employee.getEm_id(), Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), employee.getEm_name() });
		if (bool) {
			return code;
		}
		return null;
	}

	@Override
	public String getSaCodeBySourceCode(int id) {
		SqlRowList rs = queryForRowSet(GETSABYSOURCE, new Object[] { id });
		if (rs.next()) {
			return rs.getString(1);
		}
		return null;
	}

	/**
	 * 修改报价单状态
	 */
	@Override
	public void checkAdQty(int pdid) {
		Object quid = getFieldDataByCondition("QuotationDetail", "qd_quid", "qd_id=" + pdid);
		int total = getCountByCondition("QuotationDetail", "qd_quid=" + quid);
		int aud = getCountByCondition("QuotationDetail", "qd_quid=" + quid + " AND nvl(qd_yqty,0)=0");
		int turn = getCountByCondition("QuotationDetail", "qd_quid=" + quid + " AND nvl(qd_yqty,0)=nvl(qd_qty,0)");
		String status = "PART2SA";
		if (aud == total) {
			status = "AUDITED";
		} else if (turn == total) {
			status = "TURNSA";
		}
		updateByCondition("Quotation", "qu_statuscode='" + status + "',qu_status='" + BaseUtil.getLocalMessage(status) + "'",
				"qu_id=" + quid);
	}

	@Override
	public JSONObject newSaleWithCustomer(int custid, String custcode, String custname, String currency, String payments){
		Employee employee = SystemSession.getUser();
		int said = getSeqId("SALE_SEQ");
		String code = sGetMaxNumber("Sale", 2);
		Object[] objs = getFieldsDataByCondition("Customer", new String[] { "cu_sellerid", "cu_sellername", "cu_sellercode", "cu_rate", "cu_currency" }, "cu_id="
				+ custid);
		Object rate = objs[3];
		if (currency == null || "".equals(currency)) {
			// 获取客户币别
			currency = String.valueOf(objs[4]);
		} else {
			rate = getFieldDataByCondition("Currencys", "cr_rate", "cr_name='" + currency + "'");
		}
		boolean bool = execute(INSERTPURCWITHCUST, new Object[] { said, code, BaseUtil.getLocalMessage("ENTERING"), "ENTERING",
				employee.getEm_id(), Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), custid, custcode, custname, objs[0],
				objs[1], BaseUtil.getLocalMessage("UNPRINT"), employee.getEm_name(), objs[2], "UNPRINT", currency, payments, rate});
		if (bool) {
			execute("update sale set (sa_paymentsid,sa_payments)=(select pa_id, pa_name from payments where pa_code=sa_paymentscode) where sa_id=" + said);
			execute("update sale set (sa_apcustcode,sa_apcustname,sa_shcustcode,sa_shcustname)=(select cu_arcode, cu_arname, cu_shcustcode,cu_shcustname from customer where cu_code=sa_custcode) where sa_id=" + said);
			JSONObject j = new JSONObject();
			j.put("sa_id", said);
			j.put("sa_code", code);
			return j;
		}
		return null;
	}

	@Override
	public void getCustomer(int[] id) {

	}

	static final String CHECK_YQTY = "SELECT qd_code,qd_detno,qd_qty FROM QuotationDetail WHERE qd_id=? and qd_qty<?";

	/**
	 * 报价单转入销售单之前， 1.判断通知单状态 2.判断thisqty ≤ qty - yqty
	 */
	@Override
	public void checkQdYqty(List<Map<Object, Object>> datas) {
		int id = 0;
		Object y = 0;
		SqlRowList rs = null;
		boolean bool = false;
		Object[] qus = null;
		for (Map<Object, Object> d : datas) {
			id = Integer.parseInt(d.get("qd_id").toString());
			qus = getFieldsDataByCondition("QuotationDetail left join Quotation on qd_quid=qu_id", "qd_code,qd_detno", "qd_id=" + id);
			if (qus != null) {
				bool = checkIf("Quotation", "qu_code='" + qus[0] + "' and qu_statuscode='AUDITED'");
				if (!bool) {
					BaseUtil.showError("报价单:" + qus[0] + " 未审核通过,无法转销售单!");
				}
				y = getFieldDataByCondition("SaleDetail", "sum(nvl(sd_qty,0))", "sd_sourceid=" + id);
				y = y == null ? 0 : y;
				rs = queryForRowSet(CHECK_YQTY, id, Double.parseDouble(y.toString()) + Double.parseDouble(d.get("qd_turnqty").toString()));
				if (rs.next()) {
					StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],报价单号:").append(rs.getString("qd_code")).append(",行号:")
							.append(rs.getInt("qd_detno")).append(",报价数:").append(rs.getDouble("qd_qty")).append(",已转订单数:").append(y)
							.append(",本次数:").append(d.get("qd_turnqty"));
					BaseUtil.showError(sb.toString());
				}
			}
		}
	}
}
