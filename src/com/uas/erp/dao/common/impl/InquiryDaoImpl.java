package com.uas.erp.dao.common.impl;

import java.sql.Timestamp;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.InquiryDao;

@Repository
public class InquiryDaoImpl extends BaseDao implements InquiryDao {
	static final String INQUIRY = "SELECT in_vendid,in_vendcode,in_code,in_prodtype,in_recorder,in_currency,in_pricetype FROM INQUIRY WHERE in_id=?";
	static final String INQUIRYDETAIL = "SELECT max(id_prodid)id_prodid,id_prodcode,id_currency,id_vendcode,idd_price,idd_lapqty,max(id_rate)id_rate,max(id_id)id_id,max(id_minqty)id_minqty,max(id_maxlimit)id_maxlimit,max(id_minbuyqty)id_minbuyqty,max(id_myfromdate)id_myfromdate,max(id_mytodate)id_mytodate,max(id_vendorprodcode)id_vendorprodcode,max(id_brand)id_brand,max(id_leadtime)id_leadtime,max(id_purcvendcode)id_purcvendcode,max(id_purcvendname)id_purcvendname,max(id_freerate)id_freerate FROM INQUIRYDETAIL left join InquiryDetailDet on idd_idid=id_id WHERE id_inid=? and nvl(id_isagreed,0)=-1 and nvl(idd_price,0)<>0 group by id_prodcode,id_currency,id_vendcode,idd_price,idd_lapqty";
	static final String INSERT_PURCPRICE = "INSERT INTO PURCHASEPRICE(pp_id,pp_code,pp_kind,pp_statuscode,pp_status,pp_recorder,pp_indate,pp_vendid,pp_vendcode,pp_source,pp_prodtype,pp_auditman,PP_CURRENCY) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERT_PURCPRICEDETAIL = "INSERT INTO PURCHASEPRICEDETAIL(ppd_id,ppd_ppid,ppd_detno,ppd_prodcode,ppd_currency,ppd_vendcode,ppd_price,ppd_todate,ppd_status,ppd_lapqty,ppd_rate,ppd_statuscode,ppd_minqty,ppd_maxlimit,ppd_zxbzs,ppd_vendprodcode,ppd_brand,ppd_purctime,ppd_freerate,ppd_code) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERTPURCVEND_PURCPRICEDETAIL = "INSERT INTO PURCHASEPRICEDETAIL(ppd_id,ppd_ppid,ppd_detno,ppd_prodcode,ppd_currency,ppd_vendcode,ppd_price,ppd_todate,ppd_status,ppd_lapqty,ppd_rate,ppd_statuscode,ppd_minqty,ppd_maxlimit,ppd_zxbzs,ppd_vendprodcode,ppd_brand,ppd_purctime,ppd_purcvendcode,ppd_purcvendname,ppd_purccurrency,ppd_purctaxrate,ppd_purcprice,ppd_freerate) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String UPDATE_PURCPRICEDETAIL = "UPDATE PURCHASEPRICEDETAIL SET PPD_STATUS='无效',PPD_STATUSCODE='UNVALID',ppd_remark='新价格入转无效' WHERE PPD_PRODCODE=? AND PPD_CURRENCY=? AND PPD_VENDCODE=?  AND PPD_PPID<>? and nvl(ppd_status,' ')='VALID'";
	static final String UPDATE_PURCPRICEDETAILVENDOR = "UPDATE PURCHASEPRICEDETAIL SET PPD_VENDNAME=(select ve_name from vendor where ve_code=ppd_vendcode) where ppd_ppid=?";
	static final String UPDATE_PURCPRICEVENDOR = "UPDATE PURCHASEPRICEDETAIL SET PPD_PURCVENDNAME=(select ve_name from vendor where ve_code=ppd_purcvendcode) where ppd_ppid=?";
	static final String UPDATE_INQUIRYDETAIL = "update inquirydetail set id_status='已转价格库',id_sendstatus='待上传' where id_id=?";
	static final String UPDATE_INQUIRYDETAILAGREED = "update inquirydetail set id_isagreed=0, id_sendstatus='待上传' where nvl(id_status,' ')<>'已转价格库' and nvl(id_price,0)>0 and id_inid=?";

	@Override
	@Transactional
	public int turnPurcPrice(int id, String type) {
		SqlRowList rs = queryForRowSet(INQUIRY, new Object[] { id });
		if (rs.next()) {
			int ppid = getSeqId("PURCHASEPRICE_SEQ");
			String code = sGetMaxNumber("PurchasePrice", 2);
			execute(INSERT_PURCPRICE,
					new Object[] { ppid, code, type, "AUDITED", BaseUtil.getLocalMessage("AUDITED"), rs.getString(5),
							Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)), rs.getInt(1), rs.getString(2),
							rs.getString(3), rs.getString(4), SystemSession.getUser().getEm_name(), rs.getString("in_pricetype") });
			rs = queryForRowSet(INQUIRYDETAIL, new Object[] { id });
			int ppdid = 0;
			int inid = 0;
			int count = 1;
			while (rs.next()) {
				inid = rs.getInt("id_id");
				ppdid = getSeqId("PURCHASEPRICEDETAIL_SEQ");
				if (rs.getString("id_purcvendcode") != null && !rs.getString("id_purcvendcode").equals(rs.getString("id_vendcode"))) {
					execute(INSERTPURCVEND_PURCPRICEDETAIL,
							new Object[] { ppdid, ppid, count++, rs.getString("id_prodcode"), rs.getString("id_purccurrency"),
									rs.getString("id_purcvendcode"), rs.getObject("id_purcprice"),
									Timestamp.valueOf("2099-12-31 12:00:00"), "有效", rs.getObject("idd_lapqty"),
									rs.getObject("id_purctaxrate"), "VALID", rs.getObject("id_minqty"), rs.getObject("id_maxlimit"),
									rs.getObject("id_minbuyqty"), rs.getString("id_vendorprodcode"), rs.getString("id_brand"),
									rs.getString("id_leadtime"), rs.getString("id_vendcode"), rs.getString("id_vendname"),
									rs.getString("id_currency"), rs.getObject("id_rate"), rs.getObject("idd_price"),
									rs.getObject("id_freerate") });
				} else {
					Object price = rs.getObject("idd_price") == null ? rs.getObject("id_price") : rs.getObject("idd_price");
					execute(INSERT_PURCPRICEDETAIL,
							new Object[] { ppdid, ppid, count++, rs.getString("id_prodcode"), rs.getString("id_currency"),
									rs.getString("id_vendcode"), price, Timestamp.valueOf("2099-12-31 12:00:00"), "有效", rs.getObject(6),
									rs.getObject(7), "VALID", rs.getObject(11), rs.getObject(10), rs.getObject(9), rs.getString(14),
									rs.getString(15), rs.getString(16), rs.getObject("id_freerate"), code });
				}
				if (rs.getObject("id_myfromdate") != null) {
					execute("update purchasepricedetail set ppd_fromdate=? where ppd_id=?", rs.getObject("id_myfromdate"), ppdid);
				}
				if (rs.getObject("id_mytodate") != null) {
					execute("update purchasepricedetail set ppd_todate=? where ppd_id=?", rs.getObject("id_mytodate"), ppdid);
				}
				execute("update purchasepricedetail set (ppd_currency,ppd_rate)=(select ve_currency,ve_taxrate from vendor where ppd_vendcode=ve_code) where ppd_id=? and nvl(ppd_currency,' ')=' '",
						ppdid);
				// 更新报价状态
				execute(UPDATE_INQUIRYDETAIL, new Object[] { inid });
				// 失效价格库其它的原供应商的价格
				// execute(UPDATE_PURCPRICEDETAIL,new
				// Object[]{rs.getString(2),rs.getString(3),rs.getString(4),ppid});
			}
			/**
			 * @author wsy
			 * 索菱：更新明细有效起始日期为审核日期
			 */
			execute("update purchasepricedetail set ppd_fromdate=sysdate where ppd_ppid=?",ppid);
			// 更新价格库供应商名称
			execute(UPDATE_PURCPRICEDETAILVENDOR, new Object[] { ppid });
			// 更新终端供应商名称
			execute(UPDATE_PURCPRICEVENDOR, new Object[] { ppid });
			// 更新物料供应商以及价格
			execute("update product set (pr_vendcode,pr_purcprice)=(select max(ppd_vendcode),max(ppd_price * (1 - NVL(ppd_rate, 0) / (100 + NVL(ppd_rate, 0))) * cr_rate) from purchasepricedetail,currencys where ppd_ppid="
					+ ppid
					+ " and ppd_prodcode=pr_code and ppd_currency=cr_name) where pr_code in (select ppd_prodcode from purchasepricedetail where ppd_ppid="
					+ ppid + ")");
			execute(UPDATE_INQUIRYDETAILAGREED, new Object[] { id });
			// 按最新审核的同物料同供应商的认定状态进行更新
			execute("update purchasepricedetail set PPD_APPSTATUS=(select PPD_APPSTATUS from (select ppd_vendcode,ppd_prodcode,ppd_appstatus,pp_auditdate,rank() over(partition by ppd_vendcode,ppd_prodcode order by pp_auditdate desc,ppd_id desc) mm from purchaseprice left join purchasepricedetail a on pp_id=a.ppd_ppid where pp_statuscode='AUDITED' and pp_id<>"
					+ ppid
					+ "  )C where C.ppd_prodcode=purchasepricedetail.ppd_prodcode and C.ppd_vendcode=purchasepricedetail.ppd_vendcode and MM=1) where ppd_ppid="
					+ ppid);
			// 更新转价格库后认定状态为空的价格信息（根据认定单去查找）
			execute("update purchasepricedetail set ppd_appstatus='合格' where  ppd_ppid="
					+ ppid
					+ " and  nvl(ppd_appstatus,' ')=' 'and exists (select 1 from productApproval where pa_prodcode=ppd_prodcode and pa_providecode=ppd_vendcode and pa_finalresult='合格') ");
			return ppid;
		}
		return 0;
	}

}
