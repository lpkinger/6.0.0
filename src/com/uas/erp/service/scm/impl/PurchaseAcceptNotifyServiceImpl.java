package com.uas.erp.service.scm.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.api.b2c_erp.buyer.service.InvoiceNotifyService;
import com.uas.b2c.service.seller.B2CPurchaseAcceptNotifyService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlMap;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.PurchaseAcceptNotifyService;

@Service
public class PurchaseAcceptNotifyServiceImpl implements PurchaseAcceptNotifyService {

	@Autowired
	private BaseDao baseDao;
    @Autowired
    private B2CPurchaseAcceptNotifyService b2CPurchaseAcceptNotifyService;
    
	static final String VERIFYAPPLY = "INSERT INTO verifyapply(va_id, va_code, va_statuscode, va_status, va_recorder,"
			+ "va_indate,va_date,va_vendcode,va_paymentscode,va_payments,va_class,va_ancode,va_whcode,va_sendcode)"
			+ " values (?,?,'ENTERING',?,?,sysdate,sysdate,?,?,?,'采购收料单',?,?,?)";

	public String turnVerify(String caller, int id) {
		// 判断状态是否已经接收或者拒收
		SqlRowList rs = baseDao
				.queryForRowSet("select pan_statuscode,pan_id,pan_code,pu_vendcode,pu_paymentscode,pu_payments,pu_whcode,pan_b2ccode,pan_statuscode from B2C$PURCHASEACCEPTNOTIFY left join purchase on pu_id=pan_puid where pan_id="
						+ id);
		int vaid = 0;
		SqlMap map = null;
		if (rs.next()) {
			String log = null;
			if ("DISAGREE".equals(rs.getObject("pan_statuscode"))) {
				BaseUtil.showError("本单已经拒收");
			}
			// 转单
			Object ob = baseDao.getFieldDataByCondition("verifyapply", "va_code",
					"nvl(va_anid,0)=0 and va_ancode='" + rs.getString("pan_code") + "'");
			if (ob != null) {
				BaseUtil.showError("本单已转收料单[" + ob.toString() + "]，不能重复转");
			}
			vaid = baseDao.getSeqId("VERIFYAPPLY_SEQ");
			long b2ccode = rs.getLong("pan_b2ccode");
			String code = baseDao.sGetMaxNumber("VerifyApply", 2);
			String sourcecode = rs.getString("pan_code");
			boolean bool = baseDao.execute(
					VERIFYAPPLY,
					new Object[] { vaid, code, BaseUtil.getLocalMessage("ENTERING"), SystemSession.getUser().getEm_name(),
							rs.getObject("pu_vendcode"), rs.getObject("pu_paymentscode"), rs.getString("pu_payments"), sourcecode,
							rs.getObject("pu_whcode"), rs.getObject("pan_b2ccode") });
			if (bool) {
				baseDao.updateByCondition(
						"verifyapply",
						"(va_vendname,va_receivecode,va_receivename)=(select ve_name,ve_apvendcode,ve_apvendname from Vendor where ve_code=va_vendcode)",
						"va_id=" + vaid + " and nvl(va_vendcode,' ')<>' '");
				int count = 1;
				rs = baseDao
						.queryForRowSet("select pd_prodcode,pd_code,pd_detno,pd_qty,pnd_id from B2C$PURACCEPTNOTIFYDET left join purchasedetail on pd_puid=pnd_puid where pd_prodcode=pnd_prodcode and pnd_panid="
								+ id);
				while (rs.next()) {
					map = new SqlMap("VerifyApplyDetail");
					map.set("vad_id", baseDao.getSeqId("VERIFYAPPLYDETAIL_SEQ"));
					map.set("vad_vaid", vaid);
					map.set("vad_code", code);
					map.set("vad_detno", count++);
					map.set("vad_pucode", rs.getObject("pd_code"));
					map.set("vad_pudetno", rs.getObject("pd_detno"));
					map.set("vad_prodcode", rs.getString("pd_prodcode"));
					map.set("vad_qty", rs.getDouble("pd_qty"));
					map.set("vad_class", "采购收料单");
					map.set("vad_sourcecode", sourcecode);
					map.set("vad_andid", rs.getInt("pnd_id"));
					map.set("vad_unitpackage", rs.getDouble("pd_qty"));
					map.execute();
					baseDao.execute("update PURCHASEDETAIL set pd_yqty=nvl(pd_yqty,0)+" + rs.getDouble("pd_qty") + " where pd_code='"
							+ rs.getObject("pd_code") + "' and pd_detno =" + rs.getObject("pd_detno"));
				}
				log = "转入成功,收料单号:"
						+ "<a href=\"javascript:openUrl('jsps/scm/purchase/verifyApply.jsp?whoami=VerifyApply&formCondition=va_idIS" + vaid
						+ "&gridCondition=vad_vaidIS" + vaid + "')\">" + code + "</a>&nbsp;";			
				//if(!"TURNVA".equals(rs.getObject("pan_statuscode"))){//通知商城
				 b2CPurchaseAcceptNotifyService.agreeAccept(new long[]{b2ccode});
				//}
				// 修改收料通知单状态
				baseDao.updateByCondition("B2C$PURCHASEACCEPTNOTIFY",
						"pan_statuscode='TURNVA',pan_status='" + BaseUtil.getLocalMessage("TURNVA") + "'  ", "pan_id=" + id);
				
				return log;
			}
		}
		return null;
	}

}
