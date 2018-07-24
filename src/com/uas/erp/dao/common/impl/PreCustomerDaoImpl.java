package com.uas.erp.dao.common.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.dao.common.PreCustomerDao;
import com.uas.erp.model.Key;

@Repository
public class PreCustomerDaoImpl extends BaseDao implements PreCustomerDao {
	
	@Autowired
	private TransferRepository transferRepository;
	
	static final String TURNCUSTOMER = "SELECT * FROM precustomer WHERE cu_id=?";

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int turnCustomer(int id) {
		SqlRowList rs = queryForRowSet(TURNCUSTOMER, new Object[] { id });
		int cuid = 0;
		String isGroup = BaseUtil.getXmlSetting("group");
		String dataSob = BaseUtil.getXmlSetting("dataSob");
		if (rs.next()) {
			Key key = transferRepository.transfer("PreCustomer", id);
			cuid = key.getId();
			String custStatus = getDBSetting("PreCustomer", "CustStatus");
			String statuscode = "AUDITED";
			if (custStatus != null) {
				// 状态是已审核
				if ("1".equals(custStatus)) {
					statuscode = "AUDITED";
				}
				// 状态是在录入
				if ("0".equals(custStatus)) {
					statuscode = "ENTERING";
				}
			}
			updateByCondition("Customer", "cu_auditstatuscode='" + statuscode + "',cu_auditstatus='" + BaseUtil.getLocalMessage(statuscode) + "'", "cu_id=" + cuid);
			updateByCondition("Customer", "cu_auditman='" + SystemSession.getUser().getEm_name() + "', cu_auditdate=sysdate ", "cu_id=" + cuid + " and cu_auditstatuscode='AUDITED'");
			updateByCondition("Customer", "cu_auditman=null, cu_auditdate=null ", "cu_id=" + cuid + " and cu_auditstatuscode='ENTERING'");
			int count = 0;
			if(rs.getString("cu_add1") != null && !"".equals(rs.getString("cu_add1"))){
				String cu_add1 = rs.getString("cu_add1").replace("'", "''");
				count = getCount("select count(*) from CustomerAddress where ca_cuid=" + cuid
						+ " and ca_address='" + cu_add1 + "'");
				if (count == 0) {
					count = getCount("select count(*) from CustomerAddress where ca_remark='是' and  ca_cuid=" + cuid);
					if (count == 0) {
						Object maxdetno = getFieldDataByCondition("CustomerAddress", "max(ca_detno)", "ca_cuid="
								+ cuid);
						//插入客户收货地址时电话不为空时取电话，电话为空时取手机号
						String tel=(rs.getString("cu_tel")==null||"".equals(rs.getString("cu_tel")))?rs.getString("cu_mobile"):rs.getString("cu_tel");
						int detno = maxdetno == null ? 0 : Integer.valueOf(maxdetno.toString());
						execute("Insert into CustomerAddress(ca_id, ca_detno, ca_cuid, ca_address, ca_remark, ca_person, ca_phone, ca_fax) values (?,?,?,?,?,?,?,?)",
								new Object[] { getSeqId("CUSTOMERADDRESS_SEQ"), detno + 1, cuid, rs.getString("cu_add1"), "是",
										rs.getString("cu_contact"), tel, rs.getString("cu_fax")});
					} else {
						updateByCondition("CustomerAddress", "ca_remark=''", "ca_remark='是' and ca_cuid=" + cuid);
						Object maxdetno = getFieldDataByCondition("CustomerAddress", "max(ca_detno)", "ca_cuid="
								+ cuid);
						int detno = maxdetno == null ? 0 : Integer.valueOf(maxdetno.toString());
						execute(
								"Insert into CustomerAddress(ca_id, ca_detno, ca_cuid, ca_address, ca_remark, ca_person, ca_phone, ca_fax) values (?,?,?,?,?,?,?,?)",
								new Object[] { getSeqId("CUSTOMERADDRESS_SEQ"), detno + 1, cuid, rs.getString("cu_add1"), "是",
										rs.getString("cu_contact"), rs.getString("cu_tel"), rs.getString("cu_fax")});
					}
				}
			}
			if(rs.getString("cu_paymentscode") != null && !"".equals(rs.getString("cu_paymentscode"))){
				count = getCount("select count(*) from CustomerPayments where cp_cuid=" + cuid
						+ " and cp_paymentcode='" + rs.getString("cu_paymentscode") + "'");
				if (count == 0) {
					count = getCount("select count(*) from CustomerPayments where cp_isdefault='是' and  cp_cuid=" + cuid);
					if (count == 0) {
						Object maxdetno = getFieldDataByCondition("CustomerPayments", "max(cp_detno)", "cp_cuid="
								+ cuid);
						int detno = maxdetno == null ? 0 : Integer.valueOf(maxdetno.toString());
						execute("Insert into CustomerPayments(cp_id, cp_detno, cp_cuid, cp_paymentcode, cp_payment, cp_isdefault) values (?,?,?,?,?,?)",
								new Object[] { getSeqId("CUSTOMERPAYMENTS_SEQ"), detno + 1, cuid, rs.getString("cu_paymentscode"), rs.getString("cu_payments"), "是"});
					} else {
						updateByCondition("CustomerPayments", "cp_isdefault=''", "cp_isdefault='是' and cp_cuid=" + cuid);
						Object maxdetno = getFieldDataByCondition("CustomerPayments", "max(cp_detno)", "cp_cuid="
								+ cuid);
						int detno = maxdetno == null ? 0 : Integer.valueOf(maxdetno.toString());
						execute("Insert into CustomerPayments(cp_id, cp_detno, cp_cuid, cp_paymentcode, cp_payment, cp_isdefault) values (?,?,?,?,?,?)",
								new Object[] { getSeqId("CUSTOMERPAYMENTS_SEQ"), detno + 1, cuid, rs.getString("cu_paymentscode"), rs.getString("cu_payments"), "是"});
					}
				}
			}
			if(rs.getString("cu_sellercode") != null && !"".equals(rs.getString("cu_sellercode"))){
				count = getCount("select count(*) from CustomerDistr where cd_cuid=" + cuid + " and cd_sellercode='" + rs.getString("cu_sellercode")
						+ "'");
				if (count == 0) {
					Object maxdetno = getFieldDataByCondition("CustomerDistr", "max(cd_detno)", "cd_cuid=" + cuid);
					int detno = maxdetno == null ? 0 : Integer.valueOf(maxdetno.toString());
					execute("Insert into CustomerDistr(cd_id, cd_detno, cd_cuid, cd_sellercode, cd_seller,cd_remark) values (?,?,?,?,?,'是')",
							new Object[] { getSeqId("CUSTOMERDISTR_SEQ"), detno + 1, cuid, rs.getString("cu_sellercode"), rs.getString("cu_sellername")});
				}
			}
			if(rs.getString("cu_servicecode") != null && !"".equals(rs.getString("cu_servicecode"))){
				count = getCount("select count(*) from CustomerDistr where cd_cuid=" + cuid + " and cd_sellercode='" + rs.getString("cu_servicecode")
						+ "'");
				if (count == 0) {
					Object maxdetno = getFieldDataByCondition("CustomerDistr", "max(cd_detno)", "cd_cuid=" + cuid);
					int detno = maxdetno == null ? 0 : Integer.valueOf(maxdetno.toString());
					execute("Insert into CustomerDistr(cd_id, cd_detno, cd_cuid, cd_sellercode, cd_seller,cd_remark) values (?,?,?,?,?,'否')",
							new Object[] { getSeqId("CUSTOMERDISTR_SEQ"), detno + 1, cuid, rs.getString("cu_servicecode"), rs.getString("cu_servicename")});
				}
			}
			if("1".equals(custStatus)) {
				if(rs.getString("cu_contact") != null && !"".equals(rs.getString("cu_contact"))){
					count = getCount("select count(*) from contact where ct_cuid=" + cuid + " and ct_name='" + rs.getString("cu_contact")
							+ "'");
					if (count == 0) {
							execute(
									"Insert into contact(ct_id,ct_detno,ct_remark,ct_name,ct_mobile,ct_officephone,ct_position,ct_personemail,ct_cuid) values (CONTACT_SEQ.nextval,1,'是',"
									+ "?,?,?,?,?,?)",
									new Object[] {rs.getString("cu_contact"),rs.getString("cu_mobile"),rs.getString("cu_tel"),rs.getString("cu_degree"),rs.getString("cu_email"),cuid});
						}
				}
			}
		/*	// 如果是集团帐套 反写到DataSop
			if ("true".equals(isGroup) && dataSob != null && !SystemSession.getUser().getCurrentMaster().getMa_name().equals(dataSob)) {
				String str = callProcedure("sys_post", new String[] { "Customer!Post",
						SystemSession.getUser().getCurrentMaster().getMa_name(), dataSob, String.valueOf(cuid), SystemSession.getUser().getEm_name(),
						String.valueOf(SystemSession.getUser().getEm_id()) });
				if (str != null && !"".equals(str)) {
					BaseUtil.showError(str);
				}
			}*/
			updateByCondition("PreCustomer", "cu_auditstatuscode='TURNED',cu_auditstatus='" +
					BaseUtil.getLocalMessage("TURNED") + "'", "cu_id=" + id);
		}
		return cuid;

	}
}
