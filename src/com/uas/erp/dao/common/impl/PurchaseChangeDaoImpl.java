package com.uas.erp.dao.common.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.formula.functions.Count;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.PurchaseChangeDao;
import com.uas.erp.model.Employee;

@Repository
public class PurchaseChangeDaoImpl extends BaseDao implements PurchaseChangeDao {
	static final String TURNPURC = "SELECT * FROM purchasechange WHERE pc_id=?";
	static final String UPDATEPURC = "update purchase set pu_rate=?,pu_currency=?,pu_paymentscode=?,pu_payments=?"
			+ ",pu_delivery=?,pu_recordman=?,pu_paymentsid=?,pu_receivecode=?,pu_receivename=?,pu_remark=? where pu_code=?";
	static final String TURNPURCDETAIL = "SELECT * FROM purchasechangedetail left join PurchaseChange on pc_id=pcd_pcid left join PurchaseDetail on pc_purccode=pd_code and pcd_pddetno=pd_detno left join product on pcd_newprodcode=pr_code WHERE pcd_pcid=?";
	static final String UPDATEPURCDETAIL = "UPDATE purchasedetail SET pd_rate=?,pd_delivery=?,pd_price=?,pd_beipin=?,pd_qualityqty=?,pd_prodcode=?"
			+ ",pd_qty=?,pd_netprice=?,pd_purcqty=? WHERE pd_code=? AND pd_detno=?";

	@Override
	@Transactional
	public String turnPurchase(int id) {
		SqlRowList rs = queryForRowSet(TURNPURC, new Object[] { id });
		Employee employee = SystemSession.getUser();
		String pucode = null;
		int application = 0;
		if (rs.next()) {
			pucode = rs.getString("pc_purccode");
			application = rs.getGeneralInt("pc_application");
			SqlRowList changeConfigMain = queryForRowSet("select CC_OLDFIELD,CC_NEWFIELD,CC_TOFIELD,CC_AFFECTEDFIELD from changeconfig where cc_caller='PurchaseChange' and nvl(cc_ismain,0)=1 ");
			String cc_oldfiledMain = "";
			String cc_newfiledMain = "";
			String cc_tofieldMain = "";
			String[] cc_affectedfieldMain;
			List<String[]> listMain = new ArrayList<String[]>();
			int fieldCount;
			if (rs.getObject("pc_newpaymentscode") != null && !"".equals(rs.getObject("pc_newpaymentscode"))
					&& !rs.getGeneralString("pc_newpaymentscode").equals(rs.getGeneralString("pc_paymentscode"))) {
				execute("update purchase set pu_paymentscode='" + rs.getObject("pc_newpaymentscode") + "',pu_payments='"
						+ rs.getObject("pc_newpayments") + "' where pu_code='" + pucode + "'");
			}
			if (rs.getObject("pc_newcurrency") != null && !"".equals(rs.getObject("pc_newcurrency"))
					&& !rs.getGeneralString("pc_newcurrency").equals(rs.getGeneralString("pc_currency"))) {
				execute("update purchase set pu_currency='" + rs.getObject("pc_newcurrency") + "',pu_rate=" + rs.getObject("pc_newrate")
						+ " where pu_code='" + pucode + "'");
			}
			if (rs.getObject("pc_newapvendcode") != null && !"".equals(rs.getObject("pc_newapvendcode"))
					&& !rs.getGeneralString("pc_newapvendcode").equals(rs.getGeneralString("pc_apvendcode"))) {
				execute("update purchase set pu_receivecode='" + rs.getObject("pc_newapvendcode") + "',pu_receivename='"
						+ rs.getObject("pc_newapvendname") + "' where pu_code='" + pucode + "'");
			}
			execute("update purchase set (pu_paymentscode,pu_payments)=(select ve_paymentcode,ve_payment from vendor where ve_code=pu_vendcode) where pu_code ='"
					+ pucode + "' and nvl(pu_paymentscode,' ')=' '");
			execute("update purchase set pu_paymentsid=(select pa_id from Payments where pa_code=pu_paymentscode and pa_class='付款方式') where pu_code ='"
					+ pucode + "' and nvl(pu_paymentscode,' ')<>' '");
			execute("update purchase set (pu_receivecode,pu_receivename)=(select ve_apvendcode,ve_apvendname from vendor where ve_code=pu_vendcode) where pu_code ='"
					+ pucode + "' and nvl(pu_receivecode,' ')=' '");
			execute("update purchase set pu_currency=(select ve_currency from vendor where ve_code=pu_vendcode) where pu_code ='" + pucode
					+ "' and nvl(pu_currency,' ')=' '");
			execute("update purchase set pu_rate=(select cr_rate from currencys where cr_name=pu_currency) where pu_code ='" + pucode + "'");
			if (employee != null) {
				execute("update purchase set pu_updatedate=sysdate,pu_updateman='" + employee.getEm_name() + "' where pu_code ='" + pucode
						+ "'");
			}
			while (changeConfigMain.next()) {
				cc_oldfiledMain = changeConfigMain.getString(1);
				cc_newfiledMain = changeConfigMain.getString(2);
				cc_tofieldMain = changeConfigMain.getString(3);
				if(changeConfigMain.getString(4)!=null && !"".equals(changeConfigMain.getString(4))){
					cc_affectedfieldMain = changeConfigMain.getString(4).split(";");
					listMain.add(cc_affectedfieldMain);
				}
				if (cc_oldfiledMain != null && cc_newfiledMain != null) {
					fieldCount = getCountByCondition("user_tab_columns", "table_name='PURCHASECHANGE' and column_name in ('"
							+ cc_oldfiledMain.toUpperCase() + "','" + cc_newfiledMain.toUpperCase() + "')");
					if (fieldCount == 2) {
						if (rs.getString(cc_newfiledMain) != null && !rs.getString(cc_newfiledMain).equals(rs.getString(cc_oldfiledMain))) {
							int count = getCountByCondition("user_tab_columns", "table_name='PURCHASE' and column_name in ('" + cc_tofieldMain.toUpperCase() + "')");
							if (count == 1) {
								execute("update purchase set " + cc_tofieldMain + "='" + rs.getString(cc_newfiledMain)
										+ "' where pu_code='" + pucode + "'");
							}
						}
					}
				}

			}
			if(listMain.size()>0){
				int length = listMain.get(0).length;
				for(int i=0;i<length;i++){
					String sql = "";
					for(String[] s : listMain){
						sql = sql + ("@null".equals(s[i])?"":(s[i])+",");
					}
					if(!"".equals(sql)){
						sql = sql.substring(0, sql.length()-1);
						execute("update purchase set "+sql+" where pu_code='"+pucode+"'");
					}
				}
			}
			rs = queryForRowSet(TURNPURCDETAIL, new Object[] { id });
			Object ad_id = null;
			Object qty = 0;
			Object purcaq = 0;
			Object aq = 0;
			while (rs.next()) {
				// 判断数量是否超出请购数
				ad_id = getFieldDataByCondition("PurchaseDetail", "pd_sourcedetail",
						"pd_code='" + pucode + "' AND pd_detno=" + rs.getObject("pcd_pddetno"));
				if (!isDBSetting("PurchaseChange", "allowADqty")) {// 允许大于来源请购单数量
					if (ad_id != null && Integer.parseInt(ad_id.toString()) > 0) {
						qty = getFieldDataByCondition("PurchaseDetail", "sum(pd_qty+nvl(pd_cancelqty,0))",
								"pd_sourcedetail=" + ad_id + " AND (pd_code <> '" + pucode + "' or (pd_code='" + pucode
										+ "' and pd_detno<>" + rs.getObject("pcd_pddetno") + "))");
						qty = qty == null ? 0 : qty;
						aq = getFieldDataByCondition("ApplicationDetail", "ad_qty", "ad_id=" + ad_id);
						aq = aq == null ? 0 : aq;
						if (Double.parseDouble(aq.toString()) == 0) {

						} else {
							if (Double.parseDouble(aq.toString()) < Double.parseDouble(qty.toString()) + rs.getGeneralDouble("pcd_newqty") && !isDBSetting("Purchase","AllowOut")) {
								BaseUtil.showError("新数量超出原请购数,超出数量:"
										+ (Double.parseDouble(qty.toString()) + rs.getGeneralDouble("pcd_newqty") - Double.parseDouble(aq
												.toString())));
							}
						}
					}
				}
				// 变更后新的数量不能小于已经收料仓数+已经过帐验收的数量
				aq = getFieldDataByCondition("PurchaseDetail", "pd_yqty",
						"pd_code='" + pucode + "' AND pd_detno=" + rs.getObject("pcd_pddetno"));
				aq = aq == null ? 0 : aq;
				if (Double.parseDouble(aq.toString()) > rs.getDouble("pcd_newqty")) {
					BaseUtil.showError("变更后新的库存单位数量不能小于已转库存单位数量[" + aq + "]");
				}
				purcaq = getFieldDataByCondition("PurchaseDetail", "pd_ypurcqty",
						"pd_code='" + pucode + "' AND pd_detno=" + rs.getObject("pcd_pddetno"));
				purcaq = purcaq == null ? 0 : purcaq;
				Double purcnewqty = 0.0;
				purcnewqty = rs.getDouble("pcd_newpurcqty") == -1 ? 0 : rs.getDouble("pcd_newpurcqty");
				
				//没有使用双单位的不需要检查采购单位已转数量
				boolean checkPurcyqty=checkIf("purchasechangedetail  left join product on PCD_NEWPRODCODE=pr_code", 
						"nvl(pcd_prodcode,' ')=nvl(PCD_NEWPRODCODE,' ') and nvl(pr_purcunit,pr_unit)<>pr_unit and pcd_id="+rs.getObject("pcd_id"));
				if (checkPurcyqty && Double.parseDouble(purcaq.toString()) > purcnewqty) {
					BaseUtil.showError("变更后新的采购单位数量不能小于已转采购单位数量[" + purcaq + "]");
				}
				double price = rs.getGeneralDouble("pcd_newprice");
				double oldprice = rs.getGeneralDouble("pcd_oldprice");
				double tax = rs.getGeneralDouble("pcd_newtaxrate");
				double newqty = rs.getGeneralDouble("pcd_newqty");
				double oldqty = rs.getGeneralDouble("pcd_oldqty");
				double newpurcqty = rs.getGeneralDouble("pcd_newpurcqty");
				double oldpurcqty = rs.getGeneralDouble("pcd_oldpurcqty");
				double yqty = rs.getGeneralDouble("pd_yqty");
				double ypurcqty = rs.getGeneralDouble("pd_ypurcqty");
				double p = NumberUtil.formatDouble(price, 6);
				double np = NumberUtil.formatDouble(p / (1 + tax / 100), 6);
				execute(UPDATEPURCDETAIL,
						new Object[] { tax, rs.getObject("pcd_newdelivery"), p, rs.getObject("pcd_newbeipin"), rs.getObject("pcd_newqty"),
								rs.getObject("pcd_newprodcode"), newqty, np, newpurcqty, pucode, rs.getObject("pcd_pddetno") });
				if (rs.getGeneralInt("pr_purcrate")==0 || rs.getGeneralInt("pr_purcrate")==1) {
					if (Math.abs(price - oldprice) > 0) {
						execute("update purchasedetail set pd_total="
								+ NumberUtil.formatDouble(oldprice * yqty + price * (newqty - yqty), 2) + " where pd_code='" + pucode
								+ "' AND pd_detno=" + rs.getObject("pcd_pddetno"));
					} else {
						execute("update purchasedetail set pd_total=" + NumberUtil.formatDouble(newqty * p, 2) + " where pd_code='"
								+ pucode + "' AND pd_detno=" + rs.getObject("pcd_pddetno"));
					}
				} else {
					if (Math.abs(price - oldprice) > 0) {
						execute("update purchasedetail set pd_total="
								+ NumberUtil.formatDouble(oldprice * ypurcqty + price * (newpurcqty - ypurcqty), 2) + " where pd_code='"
								+ pucode + "' AND pd_detno=" + rs.getObject("pcd_pddetno"));
					} else {
						execute("update purchasedetail set pd_total=" + NumberUtil.formatDouble(newpurcqty * p, 2) + " where pd_code='"
								+ pucode + "' AND pd_detno=" + rs.getObject("pcd_pddetno"));
					}
				}
				execute("update purchasedetail set pd_taxtotal=round(pd_total/(1+nvl(pd_rate,0)/100),2) where pd_code='" + pucode
						+ "' AND pd_detno=" + rs.getObject("pcd_pddetno"));
				int argCount = getCountByCondition("user_tab_columns",
						"table_name='PURCHASECHANGEDETAIL' and column_name in ('PCD_ADDRATE','PCD_NEWADDRATE')");
				if (argCount == 2) {
					if (rs.getGeneralDouble("pcd_addrate") != rs.getGeneralDouble("pcd_newaddrate")) {
						execute("update purchasedetail set pd_addrate=" + rs.getGeneralDouble("pcd_newaddrate") + " where pd_code='"
								+ pucode + "' AND pd_detno=" + rs.getObject("pcd_pddetno"));
					}
				}
				argCount = getCountByCondition("user_tab_columns",
						"table_name='PURCHASECHANGEDETAIL' and column_name in ('PCD_NEWPURCPRICE','PCD_PURCPRICE')");
				if (argCount == 2) {
					if (rs.getGeneralDouble("pcd_purcprice") != rs.getGeneralDouble("pcd_newpurcprice")) {
						execute("update purchasedetail set pd_purcprice=" + rs.getGeneralDouble("pcd_newpurcprice") + " where pd_code='"
								+ pucode + "' AND pd_detno=" + rs.getObject("pcd_pddetno"));
					}
				}
				argCount = getCountByCondition("user_tab_columns",
						"table_name='PURCHASECHANGEDETAIL' and column_name in ('PCD_BGPRICE','PCD_NEWBGPRICE')");
				if (argCount == 2) {
					if (rs.getGeneralDouble("pcd_bgprice") != rs.getGeneralDouble("pcd_newbgprice")) {
						execute("update purchasedetail set pd_bgprice=" + rs.getGeneralDouble("pcd_newbgprice") + " where pd_code='"
								+ pucode + "' AND pd_detno=" + rs.getObject("pcd_pddetno"));
					}
				}
				argCount = getCountByCondition("user_tab_columns",
						"table_name='PURCHASECHANGEDETAIL' and column_name in ('PCD_FACTORY','PCD_NEWFACTORY')");
				if (argCount == 2) {
					if (rs.getObject("pcd_newfactory") != null) {
						if (!rs.getGeneralString("pcd_newfactory").equals(rs.getGeneralString("pcd_factory"))) {
							execute("update purchasedetail set pd_factory='" + rs.getGeneralString("pcd_newfactory") + "' where pd_code='"
									+ pucode + "' AND pd_detno=" + rs.getObject("pcd_pddetno"));
						}
					}
				}
				Object adid = getFieldDataByCondition("PurchaseDetail", "pd_sourcedetail",
						"pd_code='" + pucode + "' and pd_detno=" + rs.getObject("pcd_pddetno"));
				if (oldqty - newqty < 0) {// 新数量大于原数量时更新请购单已转数；否则请购转采购界面本次转数量>0,但实际转采购时无法转出：本次数量填写超出可转数量
					execute("update applicationdetail set ad_yqty=nvl(ad_yqty,0)+" + (newqty - oldqty)
							+ ",ad_statuscode='AUDITED',ad_status='" + BaseUtil.getLocalMessage("AUDITED") + "' where ad_id =" + adid);

				}
				if (application == 1) {
					if (oldqty - newqty > 0) {
						execute("update applicationdetail set ad_yqty=nvl(ad_yqty,0)-" + (oldqty - newqty)
								+ ",ad_statuscode='AUDITED',ad_status='" + BaseUtil.getLocalMessage("AUDITED") + "' where ad_id =" + adid);
					}
				} else {
					if (oldqty - newqty > 0) {
						execute("update purchasedetail set pd_cancelqty=nvl(pd_cancelqty,0)+" + (oldqty - newqty) + " where pd_code='"
								+ pucode + "' AND pd_detno=" + rs.getObject("pcd_pddetno"));
					}
				}
				/**
				 * 单据编号：2017100271
				 * 
				 * @author wsy
				 */
				SqlRowList changeConfigDetail = queryForRowSet("select CC_OLDFIELD,CC_NEWFIELD,CC_TOFIELD,CC_AFFECTEDFIELD from changeconfig where cc_caller='PurchaseChange' and nvl(cc_ismain,0)=0 ");
				String cc_oldfiledDetail = "";
				String cc_newfiledDetail = "";
				String cc_tofieldDetail = "";
				String[] cc_affectedfieldDetail;
				List<String[]> listDetail = new ArrayList<String[]>();
				while (changeConfigDetail.next()) {
					cc_oldfiledDetail = changeConfigDetail.getString(1);
					cc_newfiledDetail = changeConfigDetail.getString(2);
					cc_tofieldDetail = changeConfigDetail.getString(3);
					if(changeConfigDetail.getString(4)!=null && !"".equals(changeConfigDetail.getString(4))){
						cc_affectedfieldDetail = changeConfigDetail.getString(4).split(";");
						listDetail.add(cc_affectedfieldDetail);
					}
					if (cc_oldfiledDetail != null && cc_newfiledDetail != null) {
						argCount = getCountByCondition("user_tab_columns", "table_name='PURCHASECHANGEDETAIL' and column_name in ('"
								+ cc_oldfiledDetail.toUpperCase() + "','" + cc_newfiledDetail.toUpperCase() + "')");
						if (argCount == 2) {
							if (rs.getString(cc_newfiledDetail) != null
									&& !rs.getString(cc_newfiledDetail).equals(rs.getString(cc_oldfiledDetail))) {
								int count = getCountByCondition("user_tab_columns", "table_name='PURCHASEDETAIL' and column_name in ('" + cc_tofieldDetail.toUpperCase() + "')");
								if (count == 1) {
									execute("update purchasedetail set " + cc_tofieldDetail + "='"
											+ rs.getString(cc_newfiledDetail) + "' where pd_code='" + pucode + "' AND pd_detno="
											+ rs.getObject("pcd_pddetno"));
								}
							}
						}
					}
				}
				/**
				 * 层级更新   以分号分割，循环更新
				 */
				if(listDetail.size()>0){
					int length = listDetail.get(0).length;
					for(int i=0;i<length;i++){
						String sql = "";
						for(String[] s : listDetail){
							sql = sql + ("@null".equals(s[i])?"":(s[i])+",");
						}
						if(!"".equals(sql)){
							sql = sql.substring(0, sql.length()-1);
							execute("update purchasedetail set "+sql+" where pd_code='"+pucode+"' and pd_detno="+rs.getObject("pcd_pddetno")+"");
						}
					}
				}
				if (application == 1 || oldqty - newqty < 0) {// 更新请购单状态
					Object apid = getFieldDataByCondition("ApplicationDetail", "ad_apid", "ad_id=" + adid);
					// 如果请购明细全部结案,修改请购单状态为结案
					int finish = getCountByCondition("ApplicationDetail", "ad_apid=" + apid + " AND ad_statuscode='FINISH'");
					int count = getCountByCondition("ApplicationDetail", "ad_apid=" + apid);
					String sta = "AUDITED";
					if (finish == count) {
						sta = "FINISH";
					}
					updateByCondition("Application", "ap_statuscode='" + sta + "',ap_status='" + BaseUtil.getLocalMessage(sta) + "'",
							"ap_id=" + apid);
					int yCount = getCountByCondition("ApplicationDetail", "ad_apid=" + apid + " AND ad_yqty>=ad_qty AND NVL(ad_yqty,0)>0");
					int nCount = getCountByCondition("ApplicationDetail", "ad_apid=" + apid + " AND NVL(ad_yqty,0)=0");
					String status = "PART2PU";
					if (nCount == count) {
						status = "";
					} else if (yCount == count) {
						status = "TURNPURC";
					}
					execute("UPDATE Application set ap_turnstatuscode=?,ap_turnstatus=? where ap_id=?", status,
							BaseUtil.getLocalMessage(status), apid);
				}
			}
			execute("update Purchase set pu_total=(select sum(pd_total) from PurchaseDetail where PurchaseDetail.pd_puid = Purchase.pu_id) where pu_code='"
					+ pucode + "'");
			execute("update Purchase set pu_taxtotal=(select sum(pd_taxtotal) from PurchaseDetail where PurchaseDetail.pd_puid = Purchase.pu_id) where pu_code='"
					+ pucode + "'");
			execute("update Purchase set pu_totalupper=L2U(nvl(pu_total,0)) WHERE pu_code='" + pucode + "'");
		}
		return pucode;
	}

	@Override
	public void updatePurchaseStatus(String pu_code) {
		int total = getCountByCondition("PurchaseDetail", "pd_code='" + pu_code + "'");
		int aud = getCountByCondition("PurchaseDetail", "pd_code='" + pu_code + "' AND nvl(pd_yqty,0) = 0");
		int turn = getCountByCondition("PurchaseDetail", "pd_code='" + pu_code + "' AND nvl(pd_yqty,0) = pd_qty");
		String status1 = aud == total ? "" : (turn == total ? "TURNIN" : "PART2IN");
		String status2 = aud == total ? "" : (turn == total ? "TURNVA" : "PART2VA");
		Object allowin = getFieldDataByCondition("PurchaseKind left join Purchase on pk_name=pu_kind", "abs(pk_allowin)", "pu_code='"
				+ pu_code + "'");
		if (allowin != null && Integer.parseInt(String.valueOf(allowin)) == 0) {
			updateByCondition("Purchase", "pu_acceptstatuscode='" + status2 + "',pu_acceptstatus='" + BaseUtil.getLocalMessage(status2)
					+ "'", "pu_code='" + pu_code + "'");
			int aud1 = getCountByCondition("PurchaseDetail", "pd_code='" + pu_code + "' AND nvl(pd_acceptqty,0)=0");
			int turn1 = getCountByCondition("PurchaseDetail", "pd_code='" + pu_code + "' AND nvl(pd_acceptqty,0)=nvl(pd_qty,0)");
			String status = "PART2IN";
			if (aud1 == total) {
				status = "";
			} else if (turn1 == total) {
				status = "TURNIN";
			}
			updateByCondition("Purchase", "pu_turnstatuscode='" + status + "',pu_turnstatus='" + BaseUtil.getLocalMessage(status) + "'",
					"pu_code='" + pu_code + "'");
		} else {
			updateByCondition("Purchase", "pu_turnstatuscode='" + status1 + "',pu_turnstatus='" + BaseUtil.getLocalMessage(status1) + "'",
					"pu_code='" + pu_code + "'");
		}

	}
}
