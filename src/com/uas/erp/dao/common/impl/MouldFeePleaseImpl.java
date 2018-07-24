package com.uas.erp.dao.common.impl;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.MouldFeePleaseDao;
import com.uas.erp.dao.common.PurMouldDao;
import com.uas.erp.model.Employee;

@Repository
public class MouldFeePleaseImpl extends BaseDao implements MouldFeePleaseDao {
	static final String INSERTACCOUNTREGISTER = "insert into AccountRegister ("
			+ "ar_id,ar_vendcode,ar_vendname,ar_recorddate,ar_date,ar_payment,ar_type,"
			+ "ar_code,ar_sourceid,ar_source,ar_sourcetype,ar_statuscode,ar_status,ar_recordman,"
			+ "ar_accountcode,ar_accountname,ar_accountcurrency,ar_recbankaccount,ar_recbank,ar_memo,ar_cateid,ar_checktitle,ar_arapcurrency) values("
			+ "?,?,?,sysdate,sysdate,?,?,?,?,?,?,'ENTERING',?,?,?,?,?,?,?,?,?,?,?)";
	@Autowired
	private PurMouldDao purMouldDao;

	@Override
	@Transactional
	public JSONObject turnAccountRegister(int id, Object thisamount) {
		Employee employee = SystemSession.getUser();
		Double tamount = Double.parseDouble(thisamount.toString());
		SqlRowList rs = queryForRowSet("select * from MOULDFEEPLEASE where mp_id=?", new Object[] { id });
		if (rs.next()) {
			if (Math.abs(tamount) - (Math.abs(rs.getGeneralDouble("mp_total")) - Math.abs(rs.getGeneralDouble("mp_payamount"))) > 0.01) {
				BaseUtil.showError("本次付款金额大于剩余未付金额!");
			}
			int arid = getSeqId("ACCOUNTREGISTER_SEQ");
			String code = sGetMaxNumber("AccountRegister", 2);
			execute("insert into AccountRegister (ar_id,ar_recorddate,ar_date,"
					+ "ar_payment,ar_type,ar_code,ar_sourceid,ar_source,ar_sourcetype,ar_statuscode,"
					+ "ar_status,ar_recordman,ar_accountcode,ar_accountname,ar_accountcurrency,"
					+ "ar_recbankaccount,ar_recbank,ar_memo,ar_cateid,ar_checktitle,ar_arapcurrency,"
					+ "ar_departmentcode,ar_departmentname,ar_prjcode,ar_prjname) " + "select " + arid + ", sysdate, sysdate, "
					+ thisamount + ", '费用', '" + code + "', mp_id, mp_code, '模具付款申请', " + "'ENTERING', '在录入', '" + employee.getEm_name()
					+ "', mp_bankcode, mp_bankname, ca_currency, "
					+ "ve_bankaccount, ve_bank, mp_vendname||'/'||mp_ordercode||'/'||mp_remarkb, ca_id, mp_pleaseman, ca_currency,"
					+ "mp_departcode,mp_department,mp_prjcode,mp_prjname "
					+ "from MOULDFEEPLEASE, Vendor, Category where mp_vendcode=ve_code and mp_bankcode=ca_code " + "and mp_id=" + id);
			execute("update MOULDFEEPLEASE set mp_payamount=nvl(mp_payamount,0) + " + thisamount + " where mp_id=" + id);
			JSONObject j = new JSONObject();
			j.put("ar_id", arid);
			j.put("ar_code", code);
			return j;
		}
		return null;
	}

	@Override
	public JSONObject turnBillAP(int id, Object thisamount) {
		Employee employee = SystemSession.getUser();
		Double tamount = Double.parseDouble(thisamount.toString());
		SqlRowList rs = queryForRowSet("select * from MOULDFEEPLEASE where mp_id=?", new Object[] { id });
		if (rs.next()) {
			if (tamount - (rs.getGeneralDouble("mp_total") - rs.getGeneralDouble("mp_payamount")) > 0.01) {
				BaseUtil.showError("本次付款金额大于剩余未付金额!");
			}
			int bapid = getSeqId("BILLAP_SEQ");
			String code = sGetMaxNumber("BillAP", 2);
			execute("INSERT INTO BillAP(bap_id,bap_code,bap_date,bap_currency,bap_rate,bap_balance,bap_vendcode"
					+ ",bap_vendname,bap_remark,bap_status,bap_statuscode,bap_operator,bap_recorder,bap_indate"
					+ ",bap_getstatus,bap_sendstatus,bap_doublebalance,bap_topaybalance,bap_cmcurrency,bap_source, bap_sourcetype, bap_sourceid"
					+ ",bap_billkind,bap_settleamount,bap_leftamount,bap_cmrate,bap_othercatecode,bap_nowstatus) " + "select " + bapid
					+ ", '" + code + "', sysdate, ve_currency, ve_rate, mp_total, mp_vendcode, "
					+ "mp_vendname, mp_ordercode||'/'||mp_remarkb, '在录入', 'ENTERING', '" + employee.getEm_name() + "', '"
					+ employee.getEm_name() + "', sysdate, " + "'未领取', '未寄出', " + tamount + ", " + tamount
					+ ", ve_currency, mp_code, '模具付款申请', mp_id, " + "'其它付款', 0, " + tamount + ", 1, mp_bankcode, '未付款' "
					+ "from MOULDFEEPLEASE, Vendor where mp_vendcode=ve_code and mp_id=" + id);
			execute("update BillAP set bap_paybillcode=bap_source where bap_id=" + bapid);
			execute("update MOULDFEEPLEASE set mp_payamount=nvl(mp_payamount,0) + " + thisamount + " where mp_id=" + id);
			JSONObject j = new JSONObject();
			j.put("bap_id", bapid);
			j.put("bap_code", code);
			return j;
		}
		return null;
	}

	@Override
	public JSONObject turnBillARChange(int id, Object thisamount) {
		Employee employee = SystemSession.getUser();
		Double tamount = Double.parseDouble(thisamount.toString());
		SqlRowList rs = queryForRowSet("select * from MOULDFEEPLEASE where mp_id=?", new Object[] { id });
		if (rs.next()) {
			if (tamount - (rs.getGeneralDouble("mp_total") - rs.getGeneralDouble("mp_payamount")) > 0.01) {
				BaseUtil.showError("本次付款金额大于剩余未付金额!");
			}
			int brcid = getSeqId("BILLARCHANGE_SEQ");
			String code = sGetMaxNumber("BillARChange", 2);
			execute("INSERT INTO BILLARCHANGE(brc_id,brc_code,brc_date,brc_kind,brc_billkind2"
					+ ",brc_status,brc_amount,brc_vendcode,brc_vendname,brc_currency,brc_rate"
					+ ",brc_cmcurrency,brc_cmrate,brc_cmamount,brc_recorder,brc_indate,brc_statuscode"
					+ ",BRC_SOURCETYPE,BRC_SOURCEID,BRC_SOURCE,brc_explain) " + "select " + brcid + ", '" + code
					+ "', sysdate, '背书转让', '其他付款', '在录入', " + tamount + ", mp_vendcode, mp_vendname, ve_currency, ve_rate,"
					+ "ve_currency, 1, " + tamount + ",'" + employee.getEm_name() + "', sysdate, 'ENTERING', "
					+ "'模具付款申请', mp_id, mp_code, mp_ordercode||'/'||mp_remarkb "
					+ "from MOULDFEEPLEASE, Vendor where mp_vendcode=ve_code and mp_id=" + id);
			execute("update BILLARCHANGE set brc_ppcode=BRC_SOURCE where brc_id=" + brcid);
			execute("update MOULDFEEPLEASE set mp_payamount=nvl(mp_payamount,0) + " + thisamount + " where mp_id=" + id);
			JSONObject j = new JSONObject();
			j.put("brc_id", brcid);
			j.put("brc_code", code);
			return j;
		}
		return null;
	}

	@Override
	public void restoreYamount(double tqty, String pmcode, Integer pddetno, Object pmddetno) {
		Object[] id = getFieldsDataByCondition("purmoulddet left join purmould on pd_pmid=pm_id", new String[] { "pd_id", "pd_yamount",
				"pd_amount" }, "pm_code='" + pmcode + "' and pd_detno=" + pddetno);
		Object y = getFieldDataByCondition("MOULDFEEPLEASEDETAIL", "sum(nvl(mfd_amount,0))", "mfd_purccode='" + pmcode
				+ "' and mfd_pddetno=" + pddetno);
		y = y == null ? 0 : y;
		if (id != null) {
			if (NumberUtil.formatDouble(Double.parseDouble(y.toString()) + tqty, 2) > NumberUtil.formatDouble(
					Double.valueOf(id[2].toString()), 2)) {
				BaseUtil.showError("模具采购单号[" + pmcode + "]分期付款明细序号[" + pddetno + "]数量超发,超出数量:"
						+ (Double.parseDouble(y.toString()) + tqty - Double.parseDouble(id[2].toString())));
			} else {
				updateByCondition("purmoulddet", "pd_yamount=nvl(pd_yamount,0)+" + tqty, "pd_id=" + id[0]);
				purMouldDao.udpatestatus(Integer.parseInt(id[0].toString()));
			}
		} else {
			BaseUtil.showError("模具采购单号[" + pmcode + "]分期付款明细序号[" + pddetno + "]不存在,请核对后重新修改!");
		}
		if (StringUtil.hasText(pmddetno)) {
			id = getFieldsDataByCondition("PURMOULDDETAIL left join purmould on pmd_pmid=pm_id", new String[] { "pmd_id", "pmd_yamount",
					"pmd_total" }, "pmd_code='" + pmcode + "' and pmd_detno=" + pmddetno);
			y = getFieldDataByCondition("MOULDFEEPLEASEDETAIL", "sum(nvl(mfd_amount,0))", "mfd_purccode='" + pmcode
					+ "' and mfd_purcdetno=" + pmddetno);
			y = y == null ? 0 : y;
			if (id != null) {
				if (NumberUtil.formatDouble(Double.parseDouble(y.toString()) + tqty, 2) > NumberUtil.formatDouble(
						Double.valueOf(id[2].toString()), 2)) {
					BaseUtil.showError("模具采购单号[" + pmcode + "]采购单序号[" + pmddetno + "]数量超发,超出数量:"
							+ (Double.parseDouble(y.toString()) + tqty - Double.parseDouble(id[2].toString())));
				} else {
					updateByCondition("PURMOULDDETAIL", "pmd_yamount=nvl(pmd_yamount,0)+" + tqty, "pmd_id=" + id[0]);
				}
			} else {
				BaseUtil.showError("模具采购单号[" + pmcode + "]采购单序号[" + pmddetno + "]不存在,请核对后重新修改!");
			}
		}
	}

	@Override
	public void restoreWithAmount(int mfdid, Double f, Object pmcode, Object pddetno, Object pmddetno) {
		Object qty = 0;
		Object aq = 0;
		Object newaq = 0;
		Object newqty = 0;
		f = Math.abs(f);
		execute("update MOULDFEEPLEASEDETAIL set mfd_pdid=(select pd_id from purmoulddet,purmould where pm_id=pd_pmid and pm_code =mfd_purccode and pd_detno=mfd_pddetno) where nvl(mfd_purccode,' ')<>' ' and nvl(mfd_pddetno,0)<>0 and mfd_id="
				+ mfdid);
		// 判断是否超出
		Object[] snd = getFieldsDataByCondition("MOULDFEEPLEASEDETAIL", "mfd_pdid,mfd_purccode,mfd_pddetno,mfd_amount,mfd_purcdetno",
				"mfd_id=" + mfdid);
		if (pddetno == null || " ".equals(pddetno))
			pddetno = 0;
		if (pmddetno == null || " ".equals(pmddetno))
			pmddetno = 0;
		Object pd_id = getFieldDataByCondition("purmould left join purmoulddet on pm_id=pd_pmid", "pd_id", "pm_code='" + pmcode
				+ "' and pd_detno='" + pddetno + "'");
		Object pmd_id = getFieldDataByCondition("purmould left join PURMOULDDETAIL on pm_id=pmd_pmid", "pmd_id", "pm_code='" + pmcode
				+ "' and pmd_detno='" + pmddetno + "'");
		if (snd != null && Integer.parseInt(snd[0].toString()) > 0) {
			// 更新模具分期付款明细已转金额
			qty = getFieldDataByCondition("MOULDFEEPLEASEDETAIL", "sum(mfd_amount)", "mfd_pdid=" + snd[0] + " AND mfd_id <>" + mfdid);
			qty = qty == null ? 0 : qty;
			aq = getFieldDataByCondition("purmoulddet", "nvl(pd_amount,0)", "pd_id=" + snd[0]);
			if (pd_id != null && !"0".equals(pd_id)) {
				if (!snd[0].equals(pd_id)) {// 手动去修改采购单和序号
					newqty = getFieldDataByCondition("MOULDFEEPLEASEDETAIL", "sum(mfd_amount)", "mfd_pdid=" + pd_id + " AND mfd_id <>"
							+ mfdid);
					newqty = newqty == null ? 0 : newqty;
					newaq = getFieldDataByCondition("purmoulddet", "nvl(pd_amount,0)", "pd_id=" + pd_id);
					if (Double.parseDouble(newaq.toString()) < NumberUtil.formatDouble(Double.parseDouble(newqty.toString()) + f, 4)) {
						BaseUtil.showError("模具采购单[" + pmcode + "]分期付款明细序号[" + pddetno + "]的新金额超出原模具采购分期付款金额,超出金额:"
								+ (Double.parseDouble(newqty.toString()) + f - Double.parseDouble(newaq.toString())));
					} else {
						updateByCondition("MOULDFEEPLEASEDETAIL", "mfd_amount=" + f + ", mfd_purccode='" + pmcode + "',mfd_pddetno="
								+ pddetno, "mfd_id=" + mfdid);
						updateByCondition("purmoulddet", "pd_yamount=" + NumberUtil.formatDouble(qty.toString(), 4), "pd_id=" + snd[0]);
						purMouldDao.udpatestatus(Integer.parseInt(snd[0].toString()));
						updateByCondition("purmoulddet",
								"pd_yamount=" + NumberUtil.formatDouble((Double.parseDouble(newqty.toString()) + f), 4), "pd_id=" + pd_id);
						purMouldDao.udpatestatus(Integer.parseInt(pd_id.toString()));
					}
				} else {
					if (Double.parseDouble(aq.toString()) < NumberUtil.formatDouble(Double.parseDouble(qty.toString()) + f, 4)) {
						BaseUtil.showError("模具采购单[" + snd[1] + "]分期付款明细序号[" + snd[2] + "]的新金额超出原模具采购分期付款金额,超出金额:"
								+ (Double.parseDouble(qty.toString()) + f - Double.parseDouble(aq.toString())));
					} else {
						updateByCondition("MOULDFEEPLEASEDETAIL", "mfd_amount=" + f, "mfd_id=" + mfdid);
						updateByCondition("purmoulddet",
								"pd_yamount=" + NumberUtil.formatDouble((Double.parseDouble(qty.toString()) + f), 4), "pd_id=" + snd[0]);
						purMouldDao.udpatestatus(Integer.parseInt(snd[0].toString()));
					}
				}
			} else {
				BaseUtil.showError("模具采购单[" + pmcode + "]分期付款明细序号[" + pddetno + "]不存在！");
			}
			if (!"0".equals(pmddetno)) {
				Object pmdid = 0;
				if (snd[4] != null && !"0".equals(snd[4])) {
					pmdid = getFieldDataByCondition("purmould left join PURMOULDDETAIL on pm_id=pmd_pmid", "pmd_id", "pm_code='" + snd[1]
							+ "' and pmd_detno=" + snd[4]);
				}
				// 更新模具采购明细已转金额
				qty = getFieldDataByCondition("MOULDFEEPLEASEDETAIL", "sum(mfd_amount)", "mfd_purccode='" + snd[1] + "' and mfd_purcdetno="
						+ snd[4] + " AND mfd_id <>" + mfdid);
				qty = qty == null ? 0 : qty;
				aq = getFieldDataByCondition("PURMOULDDETAIL", "nvl(pmd_total,0)", "pmd_code='" + snd[1] + "' and pmd_detno=" + snd[4]);
				if (pmd_id != null && !"0".equals(pmd_id)) {
					if (!pmdid.equals(pmd_id)) {// 手动去修改采购单和序号
						newqty = getFieldDataByCondition("MOULDFEEPLEASEDETAIL", "sum(mfd_amount)", "mfd_purccode='" + pmcode
								+ "' and mfd_purcdetno=" + pmddetno + " AND mfd_id <>" + mfdid);
						newqty = newqty == null ? 0 : newqty;
						newaq = getFieldDataByCondition("PURMOULDDETAIL", "nvl(pmd_total,0)", "pmd_id=" + pmd_id);
						if (Double.parseDouble(newaq.toString()) < NumberUtil.formatDouble(Double.parseDouble(newqty.toString()) + f, 4)) {
							BaseUtil.showError("模具采购单[" + pmcode + "]采购明细序号[" + pmddetno + "]的新金额超出原模具采购明细金额,超出金额:"
									+ (Double.parseDouble(newqty.toString()) + f - Double.parseDouble(newaq.toString())));
						} else {
							updateByCondition("PURMOULDDETAIL", "pmd_yamount=" + NumberUtil.formatDouble(qty.toString(), 4), "pmd_id="
									+ pmdid);
							updateByCondition("PURMOULDDETAIL",
									"pd_yamount=" + NumberUtil.formatDouble((Double.parseDouble(newqty.toString()) + f), 4), "pmd_id="
											+ pmd_id);
						}
					} else {
						if (Double.parseDouble(aq.toString()) < NumberUtil.formatDouble(Double.parseDouble(qty.toString()) + f, 4)) {
							BaseUtil.showError("模具采购单[" + snd[1] + "]采购明细序号[" + snd[2] + "]的新金额超出原模具采采购明细金额,超出金额:"
									+ (Double.parseDouble(qty.toString()) + f - Double.parseDouble(aq.toString())));
						} else {
							updateByCondition("PURMOULDDETAIL",
									"pmd_yamount=" + NumberUtil.formatDouble((Double.parseDouble(qty.toString()) + f), 4), "pmd_id="
											+ pmdid);
						}
					}
				} else {
					BaseUtil.showError("模具采购单[" + pmcode + "]采购明细序号[" + pmddetno + "]不存在！");
				}
			}
		}
	}

	@Override
	public void deleteMouldFeePlease(int mp_id) {
		// 模具付款申请单：删除的之前还原模具修改申请单流转状态
		SqlRowList rs = queryForRowSet("select mp_changecode from MOULDFEEPLEASE  where mp_id=" + mp_id);
		if (rs.next()) {
			if (StringUtil.hasText(rs.getObject("mp_changecode"))) {
				updateByCondition("MOD_ALTER", "al_tostatus=null", "al_code='" + rs.getObject("mp_changecode") + "'");
			}
		}
		rs = queryForRowSet("select mfd_id,mfd_amount from MOULDFEEPLEASEDETAIL where mfd_mpid=?", mp_id);
		while (rs.next()) {
			restorePucMould(rs.getGeneralInt("mfd_id"));
			// 删除MouldFeePleaseDetail
			deleteById("MOULDFEEPLEASEdetail", "mfd_id", rs.getGeneralInt("mfd_id"));
		}
		// 删除MouldFeePlease
		deleteById("MOULDFEEPLEASE", "mp_id", mp_id);
	}

	public void restorePucMould(int mfdid) {
		SqlRowList rs = queryForRowSet(
				"select mfd_id,mfd_amount,mfd_purccode,mfd_pddetno,mfd_mpid,mfd_purcdetno from MOULDFEEPLEASEDETAIL where mfd_id=?", mfdid);
		if (rs.next()) {
			if (rs.getObject("mfd_purccode") != null) {
				if (rs.getGeneralInt("mfd_pddetno") != 0) {
					Object pdid = getFieldDataByCondition("purmoulddet left join purmould on pd_pmid=pm_id", "pd_id",
							"pm_code='" + rs.getObject("mfd_purccode") + "' and pd_detno=" + rs.getGeneralInt("mfd_pddetno"));
					updateByCondition("purmoulddet", "pd_yamount=nvl(pd_yamount,0)-" + rs.getGeneralDouble("mfd_amount"), "pd_id=" + pdid
							+ " AND nvl(pd_yamount,0)>0");
					updateByCondition("purmoulddet", "pd_yamount=0", "pd_id=" + pdid + " AND nvl(pd_yamount,0)<0");
					purMouldDao.udpatestatus(Integer.parseInt(pdid.toString()));
				}
				if (rs.getGeneralInt("mfd_purcdetno") != 0) {
					Object pmdid = getFieldDataByCondition("PURMOULDDETAIL left join purmould on pmd_pmid=pm_id", "pmd_id", "pm_code='"
							+ rs.getObject("mfd_purccode") + "' and pmd_detno=" + rs.getGeneralInt("mfd_purcdetno"));
					updateByCondition("PURMOULDDETAIL", "pmd_yamount=nvl(pmd_yamount,0)-" + rs.getGeneralDouble("mfd_amount"), "pmd_id="
							+ pmdid + " AND nvl(pmd_yamount,0)>0");
					updateByCondition("PURMOULDDETAIL", "pmd_yamount=0", "pmd_id=" + pmdid + " AND nvl(pmd_yamount,0)<0");
				}
			}
			updateByCondition("MOULDFEEPLEASE",
					"mp_total=(SELECT round(nvl(sum(nvl(mfd_amount,0)),0),2) FROM MOULDFEEPLEASEDETAIL WHERE mfd_mpid=mp_id)", "mp_id="
							+ rs.getObject("mfd_mpid"));
		}
	}
}
